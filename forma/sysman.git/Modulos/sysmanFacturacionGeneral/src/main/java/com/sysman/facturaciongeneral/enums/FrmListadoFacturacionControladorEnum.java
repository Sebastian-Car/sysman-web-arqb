/*-
 * FrmListadoFacturacionControladorEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 8/11/2017
 * @author jcrodriguez
 *
 */
public enum FrmListadoFacturacionControladorEnum {
    INFORME001486("001486INFLISFACSTD01"),

    INFORMEACUMULADO("800625INFLISFACSTD01"),
    
    CONCEPTOINICIAL("CONCEPTOINICIAL"),

    TIPOCOBRO("TIPOCOBRO"),

    TERCEROINICIAL("TERCEROINICIAL"),

    NIT("NIT"),
    
    INFORMEESPECIAL("002597INFLISFACSTDESP");

    private final String value;

    private FrmListadoFacturacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
