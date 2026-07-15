/*
 * FrmConfiguracionInterfazControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.sysmanauditoriacuentasmedicas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmConfiguracionInterfazControladorUrlEnum {

    URL29726("FRMCONFIGURACIONINTERFAZCONTROLADORURL29726",
                    "15005"),

    URL15898("FRMCONFIGURACIONINTERFAZCONTROLADORURL15898",
                    "34001"),

    URL9744("FRMCONFIGURACIONINTERFAZCONTROLADORURL9744",
                    "4001"),

    URL16928("FRMCONFIGURACIONINTERFAZCONTROLADORURL16928",
                    "16005"),

    URL18091("FRMCONFIGURACIONINTERFAZCONTROLADORURL18091",
                    "16005"),

    URL10795("FRMCONFIGURACIONINTERFAZCONTROLADORURL10795",
                    "23006"),

    URL11414("FRMCONFIGURACIONINTERFAZCONTROLADORURL11414",
                    "20013"),

    URL13984("FRMCONFIGURACIONINTERFAZCONTROLADORURL13984",
                    "14001"),

    URL14660("FRMCONFIGURACIONINTERFAZCONTROLADORURL14660",
                    "13001"),

    URL10182("FRMCONFIGURACIONINTERFAZCONTROLADORURL10182",
                    "23006"),

    URL12368("FRMCONFIGURACIONINTERFAZCONTROLADORURL12368",
                    "20013"),

    URL28101("FRMCONFIGURACIONINTERFAZCONTROLADORURL28101",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"SIFAS_DET_CAUSACION_AUTOMATICA\","),

    URL18703("FRMCONFIGURACIONINTERFAZCONTROLADORURL18703",
                    "16005"),

    URL26836("FRMCONFIGURACIONINTERFAZCONTROLADORURL26836",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"SIFAS_DET_CAUSACION_AUTOMATICA\","),

    URL15276("FRMCONFIGURACIONINTERFAZCONTROLADORURL15276",
                    "13001"),

    URL17507("FRMCONFIGURACIONINTERFAZCONTROLADORURL17507",
                    "16005"),

    URL16414("FRMCONFIGURACIONINTERFAZCONTROLADORURL16414",
                    "34001"),

    URL8773("FRMCONFIGURACIONINTERFAZCONTROLADORURL8773",
                    " listaSubconfiguracioninterfaz = service.getListado( ConectorPool.ESQUEMA_SYSMAN, \"SELECT COMPANIA\" + \" ,ANO\" + \" ,CODIGO_TRANSACCION\" + \" ,VARIABLE\" + \" ,CUENTA_DEBITO\" + \" ,CUENTA_CREDITO\" + \" ,CENTRO_COSTO\" + \" ,TERCERO\" + \" ,SUCURSAL\" + \" ,AUXILIAR\" + \" ,REFERENCIA\" + \" ,FUENTE_RECURSO\" + \" FROM SIFAS_DET_CAUSACION_AUTOMATICA WHERE COMPANIA='\" + compania + \"' AND CODIGO_TRANSACCION='\" + registro.getCampos().get(\"CODIGO_TRANSACCION\") + \"'\","),

    URL13314("FRMCONFIGURACIONINTERFAZCONTROLADORURL13314",
                    "14001");

    private final String key;
    private final String value;

    private FrmConfiguracionInterfazControladorUrlEnum(String key,
        String value) {
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
