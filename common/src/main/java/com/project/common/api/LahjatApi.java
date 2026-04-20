package com.project.common.api;

import com.project.common.api.dto.LahjatResponse;
import com.project.common.api.dto.StatusResponse;
import com.project.common.api.dto.LahjaCreateRequestObj;
import com.project.common.api.dto.LahjaGetObj;

public interface LahjatApi {

    /**
     * Get lahja 
     * @param count How many results to return
     * @return
     */
    LahjatResponse getLahjat(int count);

    /**
     * Create lahja
     * @param request lahja, hinta, valmistaja
     * @return Returns created lahja object
     */
    StatusResponse createLahja(LahjaCreateRequestObj request);
}