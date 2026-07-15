/*
 * SubformmovpptalsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubformmovpptalsControladorUrlEnum {

    URL25656("SUBFORMMOVPPTALSCONTROLADORURL25656",
                    " Registro regAux = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, \"SELECT\n\" + \" PCK_GENERALES.FC_SALDOPPTAL('\" + compania + \"','SALDOAPROPIACION',\" + anio + \",'\" + codigo + \"',(\" + mesInicial + \"-1)) - PCK_GENERALES.FC_SALDOPPTAL('\" + compania + \"','DISPONIBILIDADACUM',\" + anio + \",'\" + codigo + \"',(\" + mesInicial + \"-1)) SALDOINICIAL,\n\" + \" PCK_GENERALES.FC_SALDOPPTAL('\" + compania + \"','SALDOAPROPIACION',\" + anio + \",'\" + codigo + \"',\" + mesFinal + \") - PCK_GENERALES.FC_SALDOPPTAL('\" + compania + \"','DISPONIBILIDADACUM',\" + anio + \","),

    URL12399("SUBFORMMOVPPTALSCONTROLADORURL12399",
                    "25026"),

    URL13221("SUBFORMMOVPPTALSCONTROLADORURL13221",
                    " listatipoCpteE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR922:TBCB4134\", \"SELECT 'TODOS' CODIGO,\" + \" 'TODOS' NOMBRE,\" + \" 'A' ORDEN\" + \" FROM DUAL\" + \" UNION ALL\" + \" SELECT CODIGO,\" + \" NOMBRE,\" + \" NOMBRE\" + \" FROM TIPO_COMPROBPP \" + \" WHERE COMPANIA = '\" + compania + \"' \" + \" ORDER BY 3\","),

    URL11290("SUBFORMMOVPPTALSCONTROLADORURL11290",
                    "7010"),

    URL11842("SUBFORMMOVPPTALSCONTROLADORURL11842",
                    "7020"),

    URL22679("SUBFORMMOVPPTALSCONTROLADORURL22679",
                    "38022"),

    URL6969("SUBFORMMOVPPTALSCONTROLADORURL6969",
                    "38020");


    private final String key;
    private final String value;

    private SubformmovpptalsControladorUrlEnum(String key, String value) {
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
