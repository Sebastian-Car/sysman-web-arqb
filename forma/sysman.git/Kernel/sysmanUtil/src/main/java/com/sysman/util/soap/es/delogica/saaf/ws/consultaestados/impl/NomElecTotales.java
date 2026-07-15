package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.math.BigDecimal;

public class NomElecTotales {

    private BigDecimal redondeo;
    private BigDecimal devengadosTotal;
    private BigDecimal deduccionesTotal;
    private BigDecimal comprobanteTotal;

    public NomElecTotales() {
    }

    public BigDecimal getRedondeo() {
        return redondeo;
    }

    public void setRedondeo(BigDecimal redondeo) {
        this.redondeo = redondeo;
    }

    public BigDecimal getDevengadosTotal() {
        return devengadosTotal;
    }

    public void setDevengadosTotal(BigDecimal devengadosTotal) {
        this.devengadosTotal = devengadosTotal;
    }

    public BigDecimal getDeduccionesTotal() {
        return deduccionesTotal;
    }

    public void setDeduccionesTotal(BigDecimal deduccionesTotal) {
        this.deduccionesTotal = deduccionesTotal;
    }

    public BigDecimal getComprobanteTotal() {
        return comprobanteTotal;
    }

    public void setComprobanteTotal(BigDecimal comprobanteTotal) {
        this.comprobanteTotal = comprobanteTotal;
    }
}