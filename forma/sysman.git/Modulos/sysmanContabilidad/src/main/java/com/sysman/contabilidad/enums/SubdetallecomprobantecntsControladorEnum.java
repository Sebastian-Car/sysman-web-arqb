/*
 * SubdetallecomprobantecntsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum SubdetallecomprobantecntsControladorEnum {
    CONSECUTIVOAFECTADO("CONSECUTIVOAFECTADO"),

    MAN_AUX_GEN("MAN_AUX_GEN"),

    MAN_AUX_TER("MAN_AUX_TER"),

    CLASECUENTA("CLASECUENTA"),

    NOMBREPLANCONTABLE("NOMBREPLANCONTABLE"),

    MAN_CEN_CTO("MAN_CEN_CTO"),

    MANEJA_CENTRO("MANEJA_CENTRO"),

    MANEJA_AUXILIAR("MANEJA_AUXILIAR"),

    MANEJA_TERCERO("MANEJA_TERCERO"),

    TIPO("TIPO"),

    ANOCOMPROBANTE("ANOCOMPROBANTE"),

    FECHACOMPROBANTE("FECHACOMPROBANTE"),

    COMPRELACIONADO("COMPRELACIONADO"),

    TERCEROCOMPROBANTE("TERCEROCOMPROBANTE"),

    SUCURSALCOMPROBANTE("SUCURSALCOMPROBANTE"),

    CANTIDAD("CANTIDAD"),

    TIPOCOMPROBANTE("TIPOCOMPROBANTE"),

    FALSE("FALSE"),

    TRUE("TRUE"),

    PARAM3("NIT"),

    PARAM4("FORMATO"),

    PARAM1("ANOAFECT"),

    PARAM2("TIPOCPTEAFECT"),

    PARAM0("TIPOFACTURA"),

    PARAM9("COMPAFECT"),

    PARAM7("CUENTAS"),

    PARAM8("TIPOCPTEAFECT"),

    PARAM5("TIPOCOMP"),

    PARAM6("NUMEROCOMP"),

    PARAM12("FACTURA"),

    PARAM13("VLRCUENTA"),

    PARAM10("TIPO"),

    PARAM11("VALORD"),

    FECHAHORA("FECHAHORA"),

    FUENTES("FUENTES"),

    CENTROS("CENTROS"),

    AUXILIARES("AUXILIARES"),

    REFERENCIAS("REFERENCIAS"),

    MOSTRAR_EN_FLUJO("MOSTRAR_EN_FLUJO"),

    CODIGO_FLUJO_EFECTIVO("CODIGO_FLUJO_EFECTIVO"),
    
	TIPORETENCION("TIPORETENCION"),
	
	CODIGORETENCION("CODIGORETENCION"),
	
	NUMEROPROCESO("NUMEROPROCESO"),
	
	KEY_COMPANIA("KEY_COMPANIA"),
	
	KEY_ANO("KEY_ANO"),
	
	KEY_TIPO("KEY_TIPO"),
	
	KEY_NUMERO("KEY_NUMERO"),
	
	KEY_TIPORETENCION("KEY_TIPORETENCION"),
	
	KEY_CODIGORETENCION("KEY_CODIGORETENCION");

    private final String value;

    private SubdetallecomprobantecntsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
