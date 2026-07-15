/*
 * FrmdsctoespecialesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmdsctoespecialesControladorUrlEnum {
    URL14222("FRMDSCTOESPECIALESCONTROLADORURL14222",
                    "398001"),

    URL14208("FRMDSCTOESPECIALESCONTROLADORURL14208",
                    " listaCodigoFinalE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1015:TBCB4277\", \"SELECT CODIGO, \" + \" NOMBRE\" + \" FROM IP_USUARIOS_PREDIAL\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND NUMERO_ORDEN = '\" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + \"' \" + \" AND CODIGO_NO_ACTIVO NOT IN (0)\" + \" AND INDBORRADO NOT IN (0)\" + \" AND CODIGO >= '\" + codigoInicial + \"' \" + \" ORDER BY CODIGO \","), URL15594(
                                    "FRMDSCTOESPECIALESCONTROLADORURL15594",
                                    " listaClasePredioE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1015:TBCB4297\", \"SELECT CODIGO, \" + \" DESCRIPCION\" + \" FROM IP_CLASE_PREDIOS \" + \" ORDER BY CODIGO\","), URL9074(
                                                    "FRMDSCTOESPECIALESCONTROLADORURL9074",
                                                    "7019"), URL9556("FRMDSCTOESPECIALESCONTROLADORURL9556", "4001"), URL15108(
                                                                    "FRMDSCTOESPECIALESCONTROLADORURL15108",
                                                                    "380001"), URL13308(
                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL13308",
                                                                                    "367075"), URL9973(
                                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL9973",
                                                                                                    " listaMesPreparar = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT DISTINCT MES.NUMERO, \" + \" MES.NOMBRE\" + \" FROM MES\" + \" WHERE MES.COMPANIA = '\" + compania + \"' \" + \" AND MES.NUMERO NOT IN (0, 13)\" + \" ORDER BY MES.NUMERO ASC\");"), URL11641(
                                                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL11641",
                                                                                                                    "367073"), URL15997(
                                                                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL15997",
                                                                                                                                    " List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"), URL17000(
                                                                                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL17000",
                                                                                                                                                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, tablaIpDescuentosEspeciales,"), URL10471(
                                                                                                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL10471",
                                                                                                                                                                    "4007"), URL12475(
                                                                                                                                                                                    "FRMDSCTOESPECIALESCONTROLADORURL12475",
                                                                                                                                                                                    " listaCodigoInicialE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1015:TBCB4276\", \"SELECT CODIGO, \" + \" NOMBRE \" + \" FROM IP_USUARIOS_PREDIAL \" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND NUMERO_ORDEN = '\" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + \"' \" + \" AND CODIGO_NO_ACTIVO NOT IN (0) \" + \" AND INDBORRADO NOT IN (0) \" + \" ORDER BY CODIGO \",");

    private final String key;
    private final String value;

    private FrmdsctoespecialesControladorUrlEnum(String key, String value)
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
