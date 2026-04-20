package com.project.app.api;

import com.project.app.auth.AuthFlow;
import com.google.api.client.http.ByteArrayContent;
/* Google Oauth2 */
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

/* google json parser */
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.common.Config;
/* Apis */
import com.project.common.api.*;
import com.project.common.api.dto.*;

/* DataTypes */
import java.io.IOException;
import java.lang.IllegalStateException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiClient implements StatusApi, LahjatApi {
    private String backendUrl;
    private final AuthFlow auth;

    public ApiClient(String baseUrl, AuthFlow auth) {
        this.backendUrl = baseUrl;
        this.auth = auth;
    }

    public ApiClient(AuthFlow auth) {
        this.backendUrl = "";
        this.auth = auth;
    }

    public void setBackendUrl(String baseUrl) {
        this.backendUrl = baseUrl;
        Config.setBackendUrl(baseUrl);
    }

    public boolean isBackendUrl() {
        if (backendUrl == null || backendUrl.isBlank()) {
            return false;
        }
        return true;
    }

    /* Unauthenticated endpoint */
    @Override
    public StatusResponse getStatus() {
        HttpRequestFactory rf = new NetHttpTransport().createRequestFactory();
        String statusUrl = buildUrl("/status");

        try {
            HttpRequest request = rf.buildGetRequest(new GenericUrl(statusUrl));
            HttpResponse response = request.execute();
            try {
                String responseBody = response.parseAsString();
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                boolean status = json.has("status")
                        && !json.get("status").isJsonNull()
                        && json.get("status").getAsBoolean();
                return new StatusResponse(status);
            } finally {
                response.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch status from " + statusUrl + "\nInfo: " + e);
            return new StatusResponse(false);
        }
    }

    @Override
    public LahjatResponse getLahjat(int count) {
        GenericUrl url = new GenericUrl(buildUrl("/lahjat"));
        HttpRequestFactory rf;

        url.put("count", count);
        try {
            rf = auth.createAuthorizedRequestFactory();
            HttpRequest req = rf.buildGetRequest(url);
            HttpResponse response = req.execute();

            /* Possible failures: 
             *  - Unauthenticated
             *  - ?
             */
            if (response.getStatusCode() != 200) {
                throw new IllegalStateException("Invalid response code: " + String.valueOf(response.getStatusCode()));
            }

            try {
                String responseBody = response.parseAsString();
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                if (json.has("lahjat") == false || json.get("lahjat").isJsonArray() == false) {
                    throw new IllegalStateException("Invalid response: " + json.toString());
                }

                JsonArray items = json.getAsJsonArray("lahjat");
                List<LahjaGetObj> lahjat = new ArrayList<LahjaGetObj>();

                for (JsonElement item : items) {
                    JsonObject obj = item.getAsJsonObject();

                    lahjat.add(new LahjaGetObj(
                        obj.get("id").getAsLong(),
                        OffsetDateTime.parse(obj.get("created_at").getAsString()),
                        obj.get("lahja").getAsString(),
                        obj.get("hinta").getAsBigDecimal(),
                        obj.get("valmistaja").getAsString(),
                        obj.get("saaja").getAsString()));
                }

                return new LahjatResponse(lahjat);
            } finally {
                response.disconnect();
            }

        } catch (IOException e) {
            System.out.println("getLahjat(): Request failed to: " + url.toString() + " Reason: "  + e.toString());
            return new LahjatResponse(List.of());
        }
    }

    @Override
    public StatusResponse createLahja(LahjaCreateRequestObj request) {
        GenericUrl url = new GenericUrl(buildUrl("/lahjat"));

        try {
            HttpRequestFactory rf = auth.createAuthorizedRequestFactory();
            JsonObject body = new JsonObject();
            body.addProperty("lahja", request.lahja());
            body.addProperty("hinta", request.hinta());
            body.addProperty("valmistaja", request.valmistaja());
            body.addProperty("saaja", request.saaja());
            
            ByteArrayContent content = new ByteArrayContent(
                "application/json",
                body.toString().getBytes(StandardCharsets.UTF_8)
            );

            HttpRequest req = rf.buildPostRequest(url, content);
            req.getHeaders().setContentType("application/json");    
            HttpResponse resp = null;

            try {
                resp = req.execute();
            } finally {
                if (resp != null)
                    resp.disconnect();
            }
        
        } catch (Exception ex) {
            // TODO: exception handling
        }

        return new StatusResponse(false);
    }

    private String buildUrl(String path) {
        if (backendUrl.endsWith("/") && path.startsWith("/")) {
            return backendUrl + path.substring(1);
        }

        if (!backendUrl.endsWith("/") && !path.startsWith("/")) {
            return backendUrl + "/" + path;
        }

        return backendUrl + path;
    }
}
