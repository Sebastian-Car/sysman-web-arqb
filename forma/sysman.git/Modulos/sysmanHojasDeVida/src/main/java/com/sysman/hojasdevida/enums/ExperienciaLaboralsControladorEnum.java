/*-
 * ExperienciaLaboralsControladorEnum.java
 *
 * 1.0
 * 
 * 18 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author amonroy
 * 
 * @version 1.0, 18 de dic. de 2017
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ExperienciaLaboralsControladorEnum {

    FECHARETIRO("FECHARETIRO"),

    NEL_CODIGOPERSONA("NEL_CODIGOPERSONA"),

    PAIS("PAIS"),

    ANOSERVICIO("ANOSERVICIO"),

    MESESERVICIO("MESESERVICIO"),

    DIASERVICIO("DIASERVICIO"),;

    private final String value;

    private ExperienciaLaboralsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
