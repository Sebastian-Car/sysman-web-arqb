/*-
 * FrmGeneraAutoavaluoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 21 ago. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial.enums;

/**
 * Enumeraci&oacute;n que permite clasificar cada uno de los recursos
 * DSS que implementa el formulario.
 * 
 * @version 1.0, 21 ago. 2019
 * @author jrodrigueza
 *
 */
public enum FrmGeneraAutoavaluoControladorUrlEnum {

    /** DSS: 367210 */
    DSS_367210("ipusuariospredial/prediosactivosynoborrados/pag", "367210"),

    /** DSS: 385035 */
    DSS_385035("ipfacturados/detalledeclaracionautoavaluo/pag", "385035"),

    /** DSS: 376016 */
    DSS_376016("iptarifas/rangostarifadeclaracionautoavaluo/pag", "376016"),

    /** DSS: 385037 */
    DSS_385037("ipfacturados/avaluotarifaeindpagado", "385037"),

    /** DSS: 385034 */
    DSS_385034("ipfacturados/anosfacturadospagados", "385034"),

    /** DSS: 374038 */
    DSS_374038("iprecibosdepago/anularrecibopago", "374038");

    /** Clave para el enumardo. */
    private final String key;

    /** Valor del enumerado. */
    private final String value;

    /**
     * Define constructor para el enumerado.
     * 
     * @param key
     * clave para el enumardo.
     * @param value
     * valor del enumerado.
     */
    private FrmGeneraAutoavaluoControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
