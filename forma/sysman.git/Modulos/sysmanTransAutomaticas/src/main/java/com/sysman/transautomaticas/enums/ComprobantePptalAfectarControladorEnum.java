package com.sysman.transautomaticas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * EnumeraciÃ³n que permite clasificar cada uno de los parÃ¡metros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeraciÃ³n.
 */
public enum ComprobantePptalAfectarControladorEnum {
    
    /**
     * KEY_COMPANIA
     */
    KEY_COMPANIA("KEY_COMPANIA"),
    
    /**
     * KEY_TIPO
     */
    KEY_TIPO("KEY_TIPO"),
    
    /**
     * KEY_NUMERO_MODELO
     */
    KEY_NUMERO_MODELO("KEY_NUMERO_MODELO"),
    
    KEY_ANO("KEY_ANO"),
    
    KEY_NUMERO("KEY_NUMERO"),
    
    FECHARES("FECHARES"),

    CADENA("CADENA"),

    CLASEAFECTAR("CLASEAFECTAR"),

    TIPO_CPTE("TIPO_CPTE"),

    SUCURSALCOMPROBANTE("SUCURSAL"),

    TERCEROCOMPROBANTE("TERCERO"),
    
    VALORDOCUMENTO("VALORDOCUMENTO"),    
    
    SALDO("SALDO"),
    
    SI("SI"),
    
    NO("NO"),
    
    /**
     * Nombre de a tabla que tiene las llaves de cada registro del componente
     */
    DETALLE_COMPROBANTE_PPTAL("DETALLE_COMPROBANTE_PPTAL"),
    
    HEREDAR_COMPROBANTES_VIGENCIA_FUTURA_ANIO_POSTERIOR("HEREDAR COMPROBANTES VIGENCIA FUTURA ANIO POSTERIOR"),
    
    ;
    

    private final String value;

    private ComprobantePptalAfectarControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
