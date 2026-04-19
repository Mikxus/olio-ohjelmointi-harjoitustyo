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
    @Produces(MediaType.TEXT_PLAIN)
    public StatusResponse getStatus() {
        return true;
    }
}