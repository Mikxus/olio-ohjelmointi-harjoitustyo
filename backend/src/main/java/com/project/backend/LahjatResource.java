package com.project.backend;

import com.project.common.api.LahjatApi;
import com.project.common.api.dto.LahjaDto;
import com.project.common.api.dto.LahjatResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.print.DocFlavor.STRING;
import javax.sql.DataSource;

import jakarta.inject.Inject;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/lahjat")
public class LahjatResource implements LahjatApi {
    @Inject
    DataSource dataSource;

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LahjatResponse getLahjat(@QueryParam("count") @DefaultValue("10") int count) {
		List<LahjaDto> lahjat = new ArrayList<LahjaDto>();

		/* Prevent eranious values */
    	count = Math.max(0, count);
        if (count > 20)
			count = 20;

		String sql = """
		SELECT id, created_at, lahja, hinta, valmistaja
		FROM lahjat
		ORDER BY created_at DESC
		LIMIT ?
				""";

		try {
			Connection conn = dataSource.getConnection(); 
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, count);
			
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				lahjat.add(new LahjaDto(
					results.getLong("id"),
					results.getObject("created_at", OffsetDateTime.class),
					results.getString("lahja"),
					results.getBigDecimal("hinta"),
					results.getString("valmistaja")));
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch lahjat", e);
		}
		return new LahjatResponse(lahjat);
    }
}
