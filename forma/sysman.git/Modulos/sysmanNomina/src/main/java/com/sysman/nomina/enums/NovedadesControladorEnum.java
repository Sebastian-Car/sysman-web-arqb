/*
 * NovedadesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeración que permite clasificar cada uno de los parámetros
 *          identificados en el refactoring, para ser convertidos Map
 *          <String,String> y disponibles en dicha enumeración.
 */
public enum NovedadesControladorEnum {
	TIPO("TIPO"),

	PIVOT("PIVOT"),

	ALIAS("ALIAS"),

	IDIOMANOMBREPERIODO("s$nombrePeriodo$s"),

	IDIOMANOMBREMES("s$nombreMes$s"),

	TG_NO_EXISTE("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"),

	OBSERVACIONES("OBSERVACIONES"),

	EMAIL_CORPORATIVO("EMAIL_CORPORATIVO"),

	NUMERO_DCTO("NUMERO_DCTO"),

	EMAIL_PERSONAL("EMAIL_PERSONAL"),

	PROCESO("PROCESO"),

	REPORTE000141("000141VolantesUno"),

	REPORTE000070("000070ReporteLiquidacion"),

	REPORTE900001("900001KardexdeNovedades"),

	MSM_INFORME_NO_EXISTE("MSM_INFORME_NO_EXISTE"),

	EMPLEADO("EMPLEADO"),

	EMPLEADOINI("empleadoIni"),

	EMPLEADOFIN("empleadoFin"),

	CENTROCOSTOINI("centroCostoIni"),

	CENTROCOSTOFIN("centroCostoFin"),

	CEDULA("CEDULA"),

	NOMBRE_CONCEPTO("NOMBRE_CONCEPTO"),

	MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"),

	ID_DE_CONCEPTO("ID_DE_CONCEPTO"),

	FECHAINI("FECHAINI"),

	FECHAFIN("FECHAFIN"),

	TI_MS_ERROR_VALIDACION("TI_MS_ERROR_VALIDACION"),

	COMPANIA_AUX("COMPANIA_AUX"),

	ID_DE_PROCESO_AUX("ID_DE_PROCESO_AUX"),

	ID_DE_EMPLEADO_AUX("ID_DE_EMPLEADO_AUX"),

	PERIODO_AUX("PERIODO_AUX"),

	MES_AUX("MES_AUX"),

	ANO_AUX("ANO_AUX"),

	ID_DE_PROCESO("ID_DE_PROCESO"),

	ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

	KEY_PERIODO("KEY_PERIODO"),

	PERIODO("PERIODO");

	private final String value;

	private NovedadesControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
