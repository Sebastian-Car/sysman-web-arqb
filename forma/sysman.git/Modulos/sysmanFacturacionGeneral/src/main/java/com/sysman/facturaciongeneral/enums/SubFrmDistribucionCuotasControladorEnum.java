/*-
 * SubFrmDistribucionCuotasControladorEnum.java
 *
 * 1.0
 * 
 * 11/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.facturaciongeneral.SubFrmDistribucionCuotasControlador}
 * 
 * @version 1.0, 11/12/2017
 * @author pespitia
 *
 */
public enum SubFrmDistribucionCuotasControladorEnum {

    MI_CUOTA("MI_CUOTA"),

    TIPO("TIPO");

    private final String value;

    private SubFrmDistribucionCuotasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
