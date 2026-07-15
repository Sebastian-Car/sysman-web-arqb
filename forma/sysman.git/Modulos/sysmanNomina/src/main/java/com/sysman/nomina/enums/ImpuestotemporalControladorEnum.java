package com.sysman.nomina.enums;

public enum ImpuestotemporalControladorEnum {

    LIMITE_INFERIOR("LIMITE_INFERIOR"), LIMITE_SUPERIOR(
                    "LIMITE_SUPERIOR"), TARIFA("TARIFA"), OBLIGATORIO(
                                    "OBLIGATORIO"), DESCRIPCION(
                                                    "DESCRIPCION"), OBLIGAT(
                                                                    "OBLIGAT"), VALOR_EXENTO(
                                                                                    "VALOR_EXENTO"), VALOR_A_ADICIONAR(
                                                                                                    "VALOR_A_ADICIONAR");

    private final String value;

    private ImpuestotemporalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
