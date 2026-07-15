/*-
 * RetornoReporte.java
 *
 * 1.0
 * 
 * 29/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.reporte;

import java.util.HashMap;

/**
 * Esta clase permite generar el retorno para la preparación de los
 * reportes devolviendo los reemplazos y parametros necesarios para un
 * reporte dado
 * 
 * @version 1.0, 29/05/2018
 * @author jgomez
 *
 */
public class RetornoReporte {
    /**
     * Guarda los datos a reemplazar en la consulta del reporte
     */
    HashMap<String, Object> reemplazar;
    /**
     * Guarda los datos a parametros que se envian al reporte
     */
    HashMap<String, Object> parametros = new HashMap<>();

    public HashMap<String, Object> getReemplazar() {
        return reemplazar;
    }

    public void setReemplazar(HashMap<String, Object> reemplazar) {
        this.reemplazar = reemplazar;
    }

    public HashMap<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(HashMap<String, Object> parametros) {
        this.parametros = parametros;
    }

}
