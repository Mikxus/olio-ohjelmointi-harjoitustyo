package com.project.app.api;

import com.project.app.auth.AuthFlow;

/* Google Oauth2 */
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

/* Apis */
import com.project.common.api.StatusApi;
import com.project.common.api.dto.StatusResponse;

public class ApiClient implements StatusApi {
    private String baseUrl;
    private final AuthFlow auth;

    public ApiClient(String baseUrl, AuthFlow auth) {
        this.baseUrl = baseUrl;
        this.auth = auth;
    }

    public ApiClient(AuthFlow auth) {
        this.baseUrl = "";
        this.auth = auth;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
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
                return GsonFactory.getDefaultInstance()
                        .createJsonParser(response.getContent())
                        .parse(StatusResponse.class);
            } finally {
                response.disconnect();
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to fetch status from " + statusUrl, exception);
        }
    }

    private String buildUrl(String path) {
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl + path.substring(1);
        }

        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }

        return baseUrl + path;
    }
}
