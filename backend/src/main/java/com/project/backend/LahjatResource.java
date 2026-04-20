package com.project.backend;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.sql.DataSource;

import com.project.common.api.LahjatApi;
import com.project.common.api.dto.LahjaGetObj;
import com.project.common.api.dto.LahjatResponse;
import com.project.common.api.dto.StatusResponse;
import com.project.common.api.dto.LahjaCreateRequestObj;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
	@Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public LahjatResponse getLahjat(@QueryParam("count") @DefaultValue("10") int count) {
		List<LahjaGetObj> lahjat = new ArrayList<LahjaGetObj>();

		/* Prevent eranious values */
    	count = Math.max(0, count);
        if (count > 400)
			count = 400;

		String sql = """
		SELECT id, created_at, lahja, hinta, valmistaja, saaja
		FROM lahjat
		ORDER BY created_at DESC
		LIMIT ?;
				""";

		try (Connection conn = dataSource.getConnection(); 
			PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setInt(1, count);
			
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				lahjat.add(new LahjaGetObj(
					results.getLong("id"),
					results.getObject("created_at", OffsetDateTime.class),
					results.getString("lahja"),
					results.getBigDecimal("hinta"),
					results.getString("valmistaja"),
					results.getString("saaja")));
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch lahjat", e);
		}
		return new LahjatResponse(lahjat);
    }

	@Override
	@POST
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public StatusResponse createLahja(LahjaCreateRequestObj request) throws BadRequestException {
		boolean isBadRequst = false;

		if (request == null)
			throw new BadRequestException("Bad request");

		if (request.lahja() == null)
			throw new BadRequestException("Bad requst");

		if (request.valmistaja() == null)
			throw new BadRequestException("Bad requst");

		if (request.hinta() == null)
			throw new BadRequestException("Bad requst");

		if (request.saaja() == null)
			throw new BadRequestException("Bad requst");

		if (request.lahja().isBlank() || request.lahja().length() > 20)
			isBadRequst = true;

		/* if smaller than 0 */
		if (request.hinta().compareTo(new BigDecimal(0)) < 0)
			isBadRequst = true;

		if (request.valmistaja().isBlank() || request.valmistaja().length() > 40)
			isBadRequst = true;

		if (request.saaja().isBlank() || request.saaja().length() > 40)
			isBadRequst = true;

		if (isBadRequst)
			throw new BadRequestException("Bad request");

		String sql = """
		INSERT INTO lahjat (lahja, hinta, valmistaja, saaja) 
		VALUES (?, ?, ?, ?);
				""";

		try (Connection conn = dataSource.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, request.lahja());	
			statement.setBigDecimal(2, request.hinta());
			statement.setString(3, request.valmistaja());	
			statement.setString(4, request.saaja());
			statement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Failed to crate new lahja");
		}
		return new StatusResponse(true);
	}
}
