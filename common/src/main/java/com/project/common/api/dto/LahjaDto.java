package com.project.common.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LahjaDto(
    long id,
    OffsetDateTime created_at,
    String lahja,
    BigDecimal hinta,
    String valmistaja
) {}