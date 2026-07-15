package com.sysman.kernel.api.clientwso2.dbs;

import com.sysman.kernel.api.clientwso2.beans.Parameter;

import java.util.List;

/**
 * Resultado de ejecutar localmente un recurso de tipo grilla/combo:
 * la pagina de filas y el total de registros, obtenidos en la misma
 * llamada a DbsDispatcherConfig (sin la segunda consulta de conteo
 * por URL que hace falta en el camino remoto por WSO2).
 */
public class ListaConTotal {

    private final List<Parameter> filas;
    private final int total;

    public ListaConTotal(List<Parameter> filas, int total) {
        this.filas = filas;
        this.total = total;
    }

    public List<Parameter> getFilas() {
        return filas;
    }

    public int getTotal() {
        return total;
    }
}
