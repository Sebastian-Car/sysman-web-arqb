package com.sysman.plandesarrollo.enums;

public enum PlandesarrollotrazControladorEnum {

	TIPO_META_PLAN("TIPO_META_PLAN"),
	VIGENCIA_LOWER("vigencia"),
	RID_LOWER("rid"),
	ACCION("ACCION"),
	META_RESUL("META_RESUL"),
	META_PRODUC("META_PRODUC"),
	MANEJA_DEPEN("MANEJA_DEPEN"),
	AVANCE("AVANCE"),
	AVANCE_FINANCIERO("AVANCE_FINANCIERO"),
	PONDERACION("PONDERACION"),
	UNIDAD_MEDIDA("UNIDAD_MEDIDA"),
	META("META"),
    LB("LB"),
    NO("No."),
    TIPO_META_INDICADOR("TIPO_META_INDICADOR");

    private final String value;

    private PlandesarrollotrazControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
