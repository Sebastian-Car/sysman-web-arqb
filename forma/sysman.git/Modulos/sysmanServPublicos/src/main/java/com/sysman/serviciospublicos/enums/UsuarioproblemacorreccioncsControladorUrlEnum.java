/*
 * UsuarioproblemacorreccioncsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum UsuarioproblemacorreccioncsControladorUrlEnum {
    URL7321("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL7321", "234006"),

    URL7322("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL7322", "238011"),

    URL7324("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL7324", "23800C"),

    URL7326("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL7326", "23800D"),

    URL7328("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL7326", "238004"),

    URL7335("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL7335",
                    " listaCuadrocombinado12 = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1415:TBCB4609\", \"SELECT \" + \" PROBLEMA.CODIGO, \" + \" PROBLEMA.NOMBRE, \" + \" PROBLEMA.SOLUCION \" + \" FROM \" + \" PROBLEMA \" + \" WHERE \" + \" (\" + \" ((PROBLEMA.CLASEPROBLEMA) = 'AFR') \" + \" AND \" + \" ((PROBLEMA.COMPANIA) = '\" + compania + \"')\" + \" ) \" + \" ORDER BY \" + \" PROBLEMA.COMPANIA, \" + \" PROBLEMA.CLASEPROBLEMA, \" + \" PROBLEMA.CODIGO \" + \" \","),

    URL4758("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL4758",
                    " listaCuadrocombinado11 = service.getListado(conectorPool, \"SELECT \" + \" PROBLEMA.CODIGO, \" + \" PROBLEMA.NOMBRE \" + \" FROM \" + \" PROBLEMA \" + \" WHERE \" + \" (\" + \" ((PROBLEMA.CLASEPROBLEMA) = 'AFR') \" + \" AND \" + \" ((PROBLEMA.COMPANIA) = '\" + compania + \"')\" + \" ) \" + \" ORDER BY \" + \" PROBLEMA.COMPANIA, \" + \" PROBLEMA.CLASEPROBLEMA, \" + \" PROBLEMA.CODIGO \" + \" \");"),

    URL5551("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL5551",
                    " listaProblema = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1415:TBCB4607\", \"SELECT \" + \" PROBLEMA.CODIGO, \" + \" PROBLEMA.NOMBRE, \" + \" PROBLEMA.SOLUCION \" + \" FROM \" + \" PROBLEMA \" + \" WHERE \" + \" (\" + \" ((PROBLEMA.CLASEPROBLEMA) = 'AFR') \" + \" AND \" + \" ((PROBLEMA.COMPANIA) = '\" + compania + \"')\" + \" ) \" + \" ORDER BY \" + \" PROBLEMA.COMPANIA, \" + \" PROBLEMA.CLASEPROBLEMA, \" + \" PROBLEMA.CODIGO \" + \" \","),

    URL6434("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL6434",
                    " listaProblemaE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1415:TBCB4607\", \"SELECT \" + \" PROBLEMA.CODIGO, \" + \" PROBLEMA.NOMBRE, \" + \" PROBLEMA.SOLUCION \" + \" FROM \" + \" PROBLEMA \" + \" WHERE \" + \" (\" + \" ((PROBLEMA.CLASEPROBLEMA) = 'AFR') \" + \" AND \" + \" ((PROBLEMA.COMPANIA) = '\" + compania + \"')\" + \" ) \" + \" ORDER BY \" + \" PROBLEMA.COMPANIA, \" + \" PROBLEMA.CLASEPROBLEMA, \" + \" PROBLEMA.CODIGO \" + \" \","),

    URL8245("USUARIOPROBLEMACORRECCIONCSCONTROLADORURL8245",
                    " listaCuadrocombinado12E = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1415:TBCB4609\", \"SELECT \" + \" PROBLEMA.CODIGO, \" + \" PROBLEMA.NOMBRE, \" + \" PROBLEMA.SOLUCION \" + \" FROM \" + \" PROBLEMA \" + \" WHERE \" + \" (\" + \" ((PROBLEMA.CLASEPROBLEMA) = 'AFR') \" + \" AND \" + \" ((PROBLEMA.COMPANIA) = '\" + compania + \"')\" + \" ) \" + \" ORDER BY \" + \" PROBLEMA.COMPANIA, \" + \" PROBLEMA.CLASEPROBLEMA, \" + \" PROBLEMA.CODIGO \" + \" \",");

    private final String key;
    private final String value;

    private UsuarioproblemacorreccioncsControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
