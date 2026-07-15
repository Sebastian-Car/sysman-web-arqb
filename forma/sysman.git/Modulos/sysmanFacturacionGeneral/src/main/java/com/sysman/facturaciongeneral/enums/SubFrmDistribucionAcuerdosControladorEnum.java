/*-
 * SubFrmDistribucionAcuerdosControladorEnum.java
 *
 * 1.0
 * 
 * 7/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.facturaciongeneral.SubFrmDistribucionAcuerdosControlador}
 * 
 * @version 1.0, 7/12/2017
 * @author pespitia
 *
 */
public enum SubFrmDistribucionAcuerdosControladorEnum {

    PAR_CUOTA("cuota"),

    TIPO("TIPO");

    private final String value;

    private SubFrmDistribucionAcuerdosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
