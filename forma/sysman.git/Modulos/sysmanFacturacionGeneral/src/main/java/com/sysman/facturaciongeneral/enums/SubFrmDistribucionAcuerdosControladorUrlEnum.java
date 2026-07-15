/*-
 * SubFrmDistribucionAcuerdosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.facturaciongeneral.SubFrmDistribucionAcuerdosControlador}
 * 
 * @version 1.0, 6/12/2017
 * @author pespitia
 *
 */
public enum SubFrmDistribucionAcuerdosControladorUrlEnum {

    URL0002("SUBFRMDISTRIBUCIONACUERDOSCONTROLADORURL0002", "681003"),

    URL0001("SUBFRMDISTRIBUCIONACUERDOSCONTROLADORURL0001", "681001");

    private final String key;
    private final String value;

    private SubFrmDistribucionAcuerdosControladorUrlEnum(String key,
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
