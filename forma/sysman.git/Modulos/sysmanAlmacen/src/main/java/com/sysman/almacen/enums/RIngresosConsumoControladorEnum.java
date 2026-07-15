/*-
 * RIngresosConsumoControladorEnum.java
 *
 * 1.0
 * 
 * 6/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 6/07/2018
 * @author bcardenas
 *
 */
public enum RIngresosConsumoControladorEnum {

    PR_FECHA_INICIAL("PR_FECHAINICIAL"),

    PR_FECHA_FINAL("PR_FECHAFINAL"),

    PR_CARGO("PR_CARGO_COORDINADOR"),
    
    PR_NOMBRE_COORDINADOR("PR_NOMBRE_COORDINADOR"),
    
    PR_STRSQL_SUBC_INGRESOSCONSUMO("PR_STRSQL_SUBC_INGRESOSCONSUMO"),
    
    PR_STRSQL("PR_STRSQL"),
    
    PR_CLASE("PR_CLASE"),
    
    PR_TITULO ("PR_TITULO");

    private final String value;

    private RIngresosConsumoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
