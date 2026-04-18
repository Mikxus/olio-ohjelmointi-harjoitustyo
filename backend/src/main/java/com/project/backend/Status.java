package com.project.backend;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/status")
public class Status {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String status() {
        return "1";
    }
}