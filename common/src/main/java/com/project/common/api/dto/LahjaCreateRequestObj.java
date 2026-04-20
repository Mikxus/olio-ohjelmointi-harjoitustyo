package com.project.common.api.dto;

import java.math.BigDecimal;

public record LahjaCreateRequestObj(
    String lahja,
    BigDecimal hinta,
    String valmistaja,
    String saaja
) {}
    
