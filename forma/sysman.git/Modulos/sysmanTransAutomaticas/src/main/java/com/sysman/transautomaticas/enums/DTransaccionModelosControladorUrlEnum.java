/*-
 * TransaccionModelosControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.transautomaticas.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 18/09/2018
 * @author asana
 *
 */
public enum DTransaccionModelosControladorUrlEnum {

    URL11959("DTRANSACCIONMODELOSCONTROLADORURL11959", "1719001"), // tipoTransaccion

    URL12000("DTRANSACCIONMODELOSCONTROLADORURL12000", "4001"), // año

    URL12012("DTRANSACCIONMODELOSCONTROLADORURL12012", "14040"), // tercero

    URL1812("DTRANSACCIONMODELOSCONTROLADORURL1812", "20003"), // centrocosto

    URL1814("DTRANSACCIONMODELOSCONTROLADORURL1814", "13001"), // referencia

    URL1815("DTRANSACCIONMODELOSCONTROLADORURL1815", "34001"), //// fuenterecurso

    URL1816("DTRANSACCIONMODELOSCONTROLADORURL1816", "23040"), // auxiliar

    URL1817("DTRANSACCIONMODELOSCONTROLADORURL1817", "46001"), // Clase
                                                               // Cuenta

    URL1818("DTRANSACCIONMODELOSCONTROLADORURL1818", "16159"), // Cuenta
                                                               // Contable

    URL1819("DTRANSACCIONMODELOSCONTROLADORURL1819", "1753001"), // TipoRetencionNombre

    URL2143("DTRANSACCIONMODELOSCONTROLADORURL2143", "45063"), // cuenta
                                                               // presupuestal

    URL2144("DTRANSACCIONMODELOSCONTROLADORURL2144", "1720005"); // auxiliares
                                                                 // del
                                                                 // header

    private final String key;
    private final String value;

    private DTransaccionModelosControladorUrlEnum(String key, String value) {
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
