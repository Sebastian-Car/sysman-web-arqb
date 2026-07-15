/*-
 * RespuestaResponseList.java
 *
 * 1.0
 * 
 * 22/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * POJO del tag RespuestaResponseList
 * 
 * @version 1.0, 22/12/2020
 * @author eamaya
 *
 */
public class RespuestaResponseList {

    private String numberRangeResponseSpecified;

    private List<RespuestaNumberRangeResponse> numberRangeResponse;

    public String getNumberRangeResponseSpecified() {
        return numberRangeResponseSpecified;
    }

    public void setNumberRangeResponseSpecified(
        String numberRangeResponseSpecified) {
        this.numberRangeResponseSpecified = numberRangeResponseSpecified;
    }

    public List<RespuestaNumberRangeResponse> getNumberRangeResponse() {
        return numberRangeResponse;
    }

    public void setNumberRangeResponse(
        List<RespuestaNumberRangeResponse> numberRangeResponse) {
        this.numberRangeResponse = numberRangeResponse;
    }

}
