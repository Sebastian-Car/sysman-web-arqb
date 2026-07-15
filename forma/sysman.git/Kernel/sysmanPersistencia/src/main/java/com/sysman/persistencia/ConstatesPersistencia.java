/*-
 * ConstatesPersistencia.java
 *
 * 1.0
 * 
 * 19/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.persistencia;

/**
 * @version 1.0, 19/01/2017
 * @author cmanrique
 */
public class ConstatesPersistencia {

    public static final String RAISE_WITH_MSG = "\nPCK_ERR_MSG.RAISE_WITH_MSG(\n"
        + "UN_EXC_COD =>SQLCODE,\n"
        + "UN_TABLAERROR =>";

    public static final String ACME_RTA = ":= MI_RTA;\n";
    public static final String ACME_ASIGNACION_RTA = "    MI_RTA:= PCK_DATOS.FC_ACME(  UN_TABLA =>";
    public static final String ACME_DECLARE = "DECLARE\n"
        + "    MI_RTA VARCHAR2(4000);\n ";

    public static final String ACME_ACCION_ELIMINAR = "     UN_ACCION => 'E',\n";
    public static final String ACME_ACCION_INSERTAR = "     UN_ACCION => 'I',\n";
    public static final String ACME_ACCION_MODIFICAR = "     UN_ACCION => 'M',\n";

    public static final String ACME_ACCION_INSERTAR_MERGE = "     UN_ACCION => 'IM',\n";

    public static final String ACME_ASIGNACION_MERGE_ENLACE = "     UN_MERGEENLACE => ";
    public static final String ACME_ASIGNACION_CAMPOS = "     UN_CAMPOS => ";
    public static final String ACME_ASIGNACION_CONDICION = "     UN_CONDICION => ";
    public static final String ACME_ASIGNACION_CONDICION_ST = "     UN_CONDICION => ";

    public static final String ACME_ASIGNACION_MERGE_EXISTE_CIERRE = "     UN_MERGEEXISTE => ";
    public static final String ACME_ASIGNACION_MERGE_EXISTE = "     UN_MERGEEXISTE => ";
    public static final String ACME_ASIGNACION_MERGE_NO_EXISTE_CIERRE = "     UN_MERGENOEXIS =>";
    public static final String ACME_ASIGNACION_MERGE_NO_EXISTE = "     UN_MERGEUSING =>";
    public static final String ACME_ASIGNACION_UN_VALORES_CIERRE = "     UN_VALORES =>";
    public static final String ACME_OPERADOR_AND = " AND ";
    public static final String ACME_FORMATO_FECHA_CONCATENADO = "','DD/MM/YYYY HH24:mi:ss'),";
    public static final String ACME_VARIABLE_NULL_MAYUS = "'NULL'";
    public static final String ACME_VARIABLE_FALSE = "'false'";
    public static final String ACME_VARIABLE_NULL = "'null'";
    public static final String ACME_VARIABLE_TRUE = "'true'";
    public static final String ACME_OPERADOR_TO_DATE_IGUAL = "= TO_DATE('";
    public static final String ACME_OPERADOR_BEGIN = "BEGIN\n";
    public static final String ACME_EXCEPCION_ELIMINAR = "EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN";
    public static final String ACME_EXCEPCION_ACTUALIZAR = "EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN";
    public static final String ACME_EXCEPCION_INSERTAR = "EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN";
    public static final String ACME_EXCEPCION_MERGE = "EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN";
    public static final String ACME_CAMPO_LLAVE = "LLAVE";
    public static final String ACME_CAMPO_PARAMETRO = "PARAMETRO";
    public static final String ACME_LLAMADO_ACME = "PCK_DATOS.FC_ACME(UN_TABLA =>";
    public static final String ACME_CAMPO_ROWID = "ROWID";
    public static final String ACME_LLAMADO_PAR = "SELECT PCK_SYSMAN_UTL.FC_PAR(";
    public static final String ACME_CAMPO_TABLA = "TABLA.";
    public static final String ACME_OPERADOR_TO_DATE = "TO_DATE('";
    public static final String ACME_ASIGNACION_TABLA = "UN_TABLA =>";
    public static final String ACME_ACCION_INSERTAR_MERGE_N = "\nUN_ACCION =>'IM'";
    public static final String ACME_ASIGNACION_CAMPOS_N = "\nUN_CAMPOS =>";
    public static final String ACME_ASIGNACION_CONDICION_N = "\nUN_CONDICION =>";
    public static final String ACME_ASIGNACION_MERGE_ENLACE_N = "\nUN_MERGEENLACE=>";
    public static final String ACME_ASIGNACION_MERGE_EXISTE_N = "\nUN_MERGEEXISTE=>";
    public static final String ACME_ASIGNACION_MERGE_USING_N = "\nUN_MERGEUSING =>";
    public static final String ACME_ASIGNACION_VALOR_N = "\nUN_VALOR =>";
    public static final String ACME_FORMATO_FECHA_DEFAULT = "dd/MM/yyyy HH:mm:ss";
    /**
     * Prefijo de llamada a funcion.
     */
    public static final String ACME_LLAMADA = "{?=call ";
    /**
     * Prefijo de llamada a procedimiento.
     */
    public static final String ACME_LLAMADA_PROCEDURE = "{call ";

    public static final String ACME_RESERVADA_SYSDATE = "SYSDATE";

    private ConstatesPersistencia() {

    }

}
