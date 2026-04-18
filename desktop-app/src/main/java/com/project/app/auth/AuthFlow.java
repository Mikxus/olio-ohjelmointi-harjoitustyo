package com.project.app.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.sun.net.httpserver.HttpServer;
import com.project.common.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Handles desktop-app's Authentication
 */
public class AuthFlow {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList("openid", "profile", "email");
    private static final int CALLBACK_PORT = 29000;
    private static final long CALLBACK_TIMEOUT_SECONDS = 180;

    public AuthFlow() {
    }

    private static void openWebpage(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.contains("nix") || os.contains("nux")) {
                // The standard way to open a URL on Linux
                rt.exec(new String[]{"xdg-open", url});
            } else if (os.contains("mac")) {
                rt.exec(new String[]{"open", url});
            } else if (os.contains("win")) {
                rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else {
                System.err.println("Failed to start browser");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to start browser: " + e.getMessage());
        }
    }

    private static String base64UrlNoPadding(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String randomUrlSafe(int bytes) {
        byte[] random = new byte[bytes];
        new SecureRandom().nextBytes(random);
        return base64UrlNoPadding(random);
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> out = new HashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) {
            return out;
        }

        for (String pair : rawQuery.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            out.put(key, value);
        }
        return out;
    }

    private AuthorizationCodeFlow buildFlow() throws IOException {
        return new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                new GenericUrl(Config.getOauthTokenUrl()),
                request -> {},
                Config.getOauthClientID(),
                Config.getOauthAuthorizationUrl())
                .setScopes(SCOPES)
                .enablePKCE()
                .setDataStoreFactory(new FileDataStoreFactory(Config.getOauthStoreDir().toFile()))
                .build();
    }

    private Credential loadCredential() throws IOException {
        return buildFlow().loadCredential(Config.getOauthUserID());
    }

    public boolean isAuthorized() throws IOException {
        Credential credential = loadCredential();
        if (credential == null) {
            return false;
        }

        Long expiration = credential.getExpirationTimeMilliseconds();
        if (expiration == null || expiration > System.currentTimeMillis()) {
            return credential.getAccessToken() != null && !credential.getAccessToken().isBlank();
        }

        if (credential.getRefreshToken() == null || credential.getRefreshToken().isBlank()) {
            return false;
        }

        return credential.refreshToken();
    }

    public boolean authorizeIfNeeded() throws Exception {
        if (isAuthorized()) {
            return true;
        }
        loginAndGetCode();
        return isAuthorized();
    }

    public void loginAndGetCode() throws Exception {
        String clientId = Config.getOauthClientID();
        String state = randomUrlSafe(16);
        AuthorizationCodeFlow flow = buildFlow();

        /* 
         * Authentik expects 20k - 50k ports to be used
         * to prevent used ports halting authentication
         */
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", CALLBACK_PORT), 0);
        int port = server.getAddress().getPort();
        String redirectUri = "http://127.0.0.1:" + port + "/callback";

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> authCodeRef = new AtomicReference<>();
        AtomicReference<String> authErrorRef = new AtomicReference<>();
        AtomicReference<String> stateRef = new AtomicReference<>();

        server.createContext("/callback", handler -> {
            Map<String, String> query = parseQuery(handler.getRequestURI().getRawQuery());
            String code = query.get("code");
            String error = query.get("error");
            String returnedState = query.get("state");

            if (returnedState != null && !returnedState.isBlank()) {
                stateRef.set(returnedState);
            }

            if (error != null && !error.isBlank()) {
                authErrorRef.set(error);
            }
            if (code != null && !code.isBlank()) {
                authCodeRef.set(code);
            }

            String response;
            if (authCodeRef.get() != null) {
                response = "Authentication complete. You can close this window.";
            } else {
                response = "Authentication failed: " + (authErrorRef.get() == null ? "unknown_error" : authErrorRef.get());
            }

            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            handler.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            handler.sendResponseHeaders(200, payload.length);
            handler.getResponseBody().write(payload);
            handler.close();

            latch.countDown();
        });
       String authRequestUrl = flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .setState(state)
            .build();

        System.out.println("Starting CB server");
        server.start();
        openWebpage(authRequestUrl);

        boolean callbackReceived = latch.await(CALLBACK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        server.stop(0);

        if (!callbackReceived) {
            throw new IllegalStateException("Authorization callback timed out after " + CALLBACK_TIMEOUT_SECONDS + " seconds");
        }

        if (authErrorRef.get() != null) {
            throw new IllegalStateException("Authorization failed: " + authErrorRef.get());
        }

        if (!state.equals(stateRef.get())) {
            throw new IllegalStateException("Authorization failed: invalid state returned");
        }

        String authCode = authCodeRef.get();
        if (authCode == null || authCode.isBlank()) {
            throw new IllegalStateException("Authorization code was not returned");
        }

        System.out.printf("Going to create new token request:\nAuth response: %s\n", authCode);
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(authCode)
                .setRedirectUri(redirectUri)
                .set("client_id", clientId);
        
        TokenResponse tokenResponse = tokenRequest.execute();
        flow.createAndStoreCredential(tokenResponse, Config.getOauthUserID());
        System.out.println("AUTH done");
    }
}
