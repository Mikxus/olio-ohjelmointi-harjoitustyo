package com.project.common;

import java.util.NoSuchElementException;
import java.nio.file.Path;

/**
 * Config helpers
 */
public final class Config {
    private Config() {}

    private static void checkAndThrowNotFound(String value, String envName) {
        if (value == null || value.isBlank() == true) {
            throw  new NoSuchElementException("Env variable " + envName + " not found");
        }
    }

    public static boolean isProd() {return "prod".equalsIgnoreCase(System.getenv("ENV"));}
    public static boolean isDev() {return "dev".equalsIgnoreCase(System.getenv("ENV"));}
    public static boolean allowHTTP() {return Boolean.parseBoolean(System.getenv("ALLOW_HTTP"));}

    public static String getOauthClientID() throws NoSuchElementException {
        String clientID = System.getenv("APP_OAUTH_CLIENT_ID");
        checkAndThrowNotFound(clientID, "APP_OAUTH_CLIENT_ID");
        return clientID;
    }

    public static String getOauthAuthorizationUrl() throws NoSuchElementException {
        String url = System.getenv("APP_OAUTH_AUTHORIZATION_URL");
        checkAndThrowNotFound(url, "APP_OAUTH_AUTHORIZATION_URL");
        return url;
    }

    public static String getOauthTokenUrl() throws NoSuchElementException {
        String url = System.getenv("APP_OAUTH_TOKEN_URL");
        checkAndThrowNotFound(url, "APP_OAUTH_TOKEN_URL");
        return url;
    }

    public static String getOauthUserID() throws NoSuchElementException {
        String userID = System.getenv("APP_OAUTH_USER_ID");
        checkAndThrowNotFound(userID, "APP_OAUTH_USER_ID");
        return userID;
    }

    public static Path getOauthStoreDir() throws NoSuchElementException {
        Path dir = Path.of(System.getenv("APP_OAUTH_STORE_DIR"));
        checkAndThrowNotFound(String.valueOf(dir), "APP_OAUTH_STORE_DIR");
        return dir;
    }
}
