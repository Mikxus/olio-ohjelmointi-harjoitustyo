package com.project.app.api;

import com.project.app.auth.AuthFlow;


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

/* Apis */
import com.project.common.api.StatusApi;
import com.project.common.api.dto.StatusResponse;
import com.project.common.api.LahjatApi;
import com.project.common.api.dto.LahjatResponse;
import com.project.common.api.dto.LahjaDto;

import java.io.IOException;
/* DataTypes */
import java.lang.IllegalStateException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiClient implements StatusApi, LahjatApi {
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
                List<LahjaDto> lahjat = new ArrayList<LahjaDto>();

                for (JsonElement item : items) {
                    JsonObject obj = item.getAsJsonObject();

                    lahjat.add(new LahjaDto(
                        obj.get("id").getAsLong(),
                        OffsetDateTime.parse(obj.get("created_at").getAsString()),
                        obj.get("lahja").getAsString(),
                        obj.get("hinta").getAsBigDecimal(),
                        obj.get("valmistaja").getAsString()));
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
