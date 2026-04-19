package com.project.common.api;

import com.project.common.api.dto.LahjatResponse;

public interface LahjatApi {

    /**
     * Get lahja 
     * @param count How many results to return
     * @return
     */
    LahjatResponse getLahjat(int count);
}