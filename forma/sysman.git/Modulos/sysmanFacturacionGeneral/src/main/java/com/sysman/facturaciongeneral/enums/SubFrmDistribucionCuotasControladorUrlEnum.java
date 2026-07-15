/*-
 * SubFrmDistribucionCuotasControladorUrlEnum.java
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.facturaciongeneral.SubFrmDistribucionCuotasControlador}
 * 
 * @version 1.0, 11/12/2017
 * @author pespitia
 *
 */
public enum SubFrmDistribucionCuotasControladorUrlEnum {

    URL0002("SUBFRMDISTRIBUCIONCUOTASCONTROLADORURL0002", "684001"),

    URL0001("SUBFRMDISTRIBUCIONCUOTASCONTROLADORURL0001", "684003");

    private final String key;
    private final String value;

    private SubFrmDistribucionCuotasControladorUrlEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
