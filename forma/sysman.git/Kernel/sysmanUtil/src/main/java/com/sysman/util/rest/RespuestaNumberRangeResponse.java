/*-
 * RespuestaNumberRangeResponse.java
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

/**
 * POJO del tag numberRangeResponse
 * 
 * @version 1.0, 22/12/2020
 * @author eamaya
 *
 */
public class RespuestaNumberRangeResponse {
    private String prefix;
    private String resolutionDate;
    private int fromNumber;
    private String validDateFrom;
    private boolean validDateToSpecified;
    private String validDateTo;
    private boolean technicalKeySpecified;
    private String technicalKey;
    private boolean resolutionNumberSpecified;
    private String resolutionNumber;
    private boolean resolutionDateSpecified;
    private boolean prefixSpecified;
    private boolean fromNumberSpecified;
    private boolean toNumberSpecified;
    private long toNumber;
    private boolean validDateFromSpecified;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(String resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public int getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(int fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getValidDateFrom() {
        return validDateFrom;
    }

    public void setValidDateFrom(String validDateFrom) {
        this.validDateFrom = validDateFrom;
    }

    public boolean isValidDateToSpecified() {
        return validDateToSpecified;
    }

    public void setValidDateToSpecified(boolean validDateToSpecified) {
        this.validDateToSpecified = validDateToSpecified;
    }

    public String getValidDateTo() {
        return validDateTo;
    }

    public void setValidDateTo(String validDateTo) {
        this.validDateTo = validDateTo;
    }

    public boolean isTechnicalKeySpecified() {
        return technicalKeySpecified;
    }

    public void setTechnicalKeySpecified(boolean technicalKeySpecified) {
        this.technicalKeySpecified = technicalKeySpecified;
    }

    public String getTechnicalKey() {
        return technicalKey;
    }

    public void setTechnicalKey(String technicalKey) {
        this.technicalKey = technicalKey;
    }

    public boolean isResolutionNumberSpecified() {
        return resolutionNumberSpecified;
    }

    public void setResolutionNumberSpecified(
        boolean resolutionNumberSpecified) {
        this.resolutionNumberSpecified = resolutionNumberSpecified;
    }

    public String getResolutionNumber() {
        return resolutionNumber;
    }

    public void setResolutionNumber(String resolutionNumber) {
        this.resolutionNumber = resolutionNumber;
    }

    public boolean isResolutionDateSpecified() {
        return resolutionDateSpecified;
    }

    public void setResolutionDateSpecified(boolean resolutionDateSpecified) {
        this.resolutionDateSpecified = resolutionDateSpecified;
    }

    public boolean isPrefixSpecified() {
        return prefixSpecified;
    }

    public void setPrefixSpecified(boolean prefixSpecified) {
        this.prefixSpecified = prefixSpecified;
    }

    public boolean isFromNumberSpecified() {
        return fromNumberSpecified;
    }

    public void setFromNumberSpecified(boolean fromNumberSpecified) {
        this.fromNumberSpecified = fromNumberSpecified;
    }

    public boolean isToNumberSpecified() {
        return toNumberSpecified;
    }

    public void setToNumberSpecified(boolean toNumberSpecified) {
        this.toNumberSpecified = toNumberSpecified;
    }

    public long getToNumber() {
        return toNumber;
    }

    public void setToNumber(long toNumber) {
        this.toNumber = toNumber;
    }

    public boolean isValidDateFromSpecified() {
        return validDateFromSpecified;
    }

    public void setValidDateFromSpecified(boolean validDateFromSpecified) {
        this.validDateFromSpecified = validDateFromSpecified;
    }

}
