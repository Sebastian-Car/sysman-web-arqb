package com.sysman.contratos.enums;

public enum FrmGestionContractualControladorEnum {

	F5_1("800425F5.1_CONTRATOS_RIGEN_POR_LEY_80"),

	F5_2("800426F5.2_CONTRATOS_RIGEN_DERECHO_PRIVADO"),

	F5_3("800427F5.3_ORDENES_COMPRA_Y_TRABAJO"),

	F5_4("800428F5.4_CONVENIOS_CONTRATOS_INTERADMINISTRATIVOS"),

	F5_5("800429F5.5_INTEGRANTES_CONSORCIOS_Y_UNIONES_TEMPORALES"),
	
	F5_2_1("800649F5.2_CONTRATOS_RIGEN_DERECHO_PRIVADO");
	
	private final String value;

	private FrmGestionContractualControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}