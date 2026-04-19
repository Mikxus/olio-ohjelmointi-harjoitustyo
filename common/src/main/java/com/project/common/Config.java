package com.project.common;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

/**
 * Config helpers
 */
public final class Config {
    private static Properties props = new Properties();

    private Config() {
        loadProps();
    }

    private static void checkAndThrowNotFound(String value, String envName) {
        if (value == null || value.isBlank() == true) {
            throw  new NoSuchElementException("Env variable " + envName + " not found");
        }
    }

    private static String getPropsPath() {
        return getPropertiesStoreDir() + "app.properties"; 
    }

    private static void saveProps() {
        try {
            FileWriter writer = new FileWriter(getPropsPath());
            props.store(writer, getOauthAuthorizationUrl());
            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to store properties: " + e.toString());
        }
    }

    private static void loadProps() {
        try {
            FileReader reader = new FileReader(getPropsPath());
            props.load(reader);
            reader.close();
        } catch (Exception e) {
            System.err.println("Failed to read properties: " + e.toString());
        }
    } 

    public static boolean isProd() {return "prod".equalsIgnoreCase(System.getenv("ENV"));}
    public static boolean isDev() {return "dev".equalsIgnoreCase(System.getenv("ENV"));}
    public static boolean allowHTTP() {return Boolean.parseBoolean(System.getenv("ALLOW_HTTP"));}

    public static boolean isBackendUrl() {
        String url = getBackendUrl();
        if (url == null || url.isBlank() == true)
            return false;

        return true;
    }

    public static String getBackendUrl() {
        loadProps();
        return props.getProperty("backendUrl");
    }

    public static void setBackendUrl(String url) {
        props.setProperty("backendUrl", url);
        saveProps();
    }

    public static String getPropertiesStoreDir() {
        return System.getProperty("APP_PROPERTIES_STORE_DIR", "~/.project-app/");
    }

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
