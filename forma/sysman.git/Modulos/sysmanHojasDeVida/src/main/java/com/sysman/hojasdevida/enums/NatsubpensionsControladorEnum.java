/*-
 * NatsubpensionsControladorEnum.java
 *
 * 1.0
 * 
 * 24/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Se encarga de almacenar los enumerados necesarios para el
 * funcionamiento del controlador NatsubpensionsControlador
 * 
 * @version 1.0, 24/01/2018
 * @author mvenegas
 *
 */
public enum NatsubpensionsControladorEnum {

    NAT_SEGURIDAD_SOCIAL("NAT_SEGURIDAD_SOCIAL"),

    SS_ENTIDAD("SS_ENTIDAD"),

    ID_DEL_FONDO("ID_DEL_FONDO"),

    SS_REGIMEN("SS_REGIMEN"),

    SS_FECHVINC("SS_FECHVINC"),

    SS_FECHARADICACION("SS_FECHARADICACION"),

    NOMBRE_FONDO_RIESGOS("NOMBRE_FONDO_RIESGOS"),

    NOMBRE_DEL_FONDO("NOMBRE_DEL_FONDO")

    ;

    private final String value;

    private NatsubpensionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
