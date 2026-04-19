package com.project.backend;

import com.project.common.api.StatusApi;
import com.project.common.api.dto.StatusResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/status")
public class StatusResource implements StatusApi {
    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResponse getStatus() {
        return new StatusResponse(true);
    }
}