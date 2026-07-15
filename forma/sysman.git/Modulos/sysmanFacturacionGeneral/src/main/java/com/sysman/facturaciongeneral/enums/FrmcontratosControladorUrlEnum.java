/*-
 * FrmcontratosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 10 jul. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * Clase reservada para almacenar llamado a DSS para listas.
 * 
 * @version 1.0, 03/08/2018
 * @author dnino
 *
 */

public enum FrmcontratosControladorUrlEnum {
     URL001("FRMCONTRATOSCONTROLADORURL458", "14122"), // VENDEDOR.
     URL365("FRMCONTRATOSCONTROLADORURL365", "668002"), // Tarifa.
    URL458("FRMCONTRATOSCONTROLADORURL458", "14001"), // Tercero.
    URL632("FRMCONTRATOSCONTROLADORURL632", "23035"), // Auxiliar.
    URL726("FRMCONTRATOSCONTROLADORURL726", "663019"), // Conceptos.
    URL727("FRMCONTRATOSCONTROLADORURL727", "663029"), // ObtenerConceptos
   
    URL545("FRMCONTRATOSCONTROLADORURL545", "20065"), // Centro costo.
    URL971("FRMCONTRATOSCONTROLADORURL971", "683001"), // Totales.
    URL351("FRMCONTRATOSCONTROLADORURL351", "664010"), // Contratos.
    URL440("FRMCONTRATOSCONTROLADORURL440", "68200G"), // sub_Contratos.
    URL310("FRMCONTRATOSCONTROLADORURL310", "663014"), // Conceptos.
    URL320("FRMCONTRATOSCONTROLADORURL320", "667002"), // Estratos.
    URL330("FRMCONTRATOSCONTROLADORURL330", "678005"), // Afectar
                                                       // Cotización.
    URL340("FRMCONTRATOSCONTROLADORURL340", "663001"), // Concepto.
    URL350("FRMCONTRATOSCONTROLADORURL350", "682001"), // Suma
                                                       // valores.
    URL635("FRMCONTRATOSCONTROLADORURL635", "1839001"),

    URL525("FRMCONTRATOSCONTROLADORURL525", "1839003"),

    URL171("FRMCONTRATOSCONTROLADORURL171", "13026"),

    URL184("FRMCONTRATOSCONTROLADORURL184", "34001"),
    
    URL185("FRMCONTRATOSCONTROLADORURL545", "1915003"),
    
    URL1711("FRMCONTRATOSCONTROLADORURL1711", "1711001"), // TarifasBase.
    
    URL1925001("FRMCONTRATOSCONTROLADORURL1711", "1925001"),// Tipos de pago
    
    URL1897007("FRMCONTRATOSCONTROLADORURL1897007", "1897007"), // codigo CIIU
    
    URL678008("FRMCONTRATOSCONTROLADORURL678008","678008"),// Lista cotizaciones aceptadas
    
    URL683003("FRMCONTRATOSCONTROLADORURL683003","683003"),
    
    URL664020("FRMCONTRATOSCONTROLADORURL664020","664020"),
    
    URL1947001("FRMCONTRATOSCONTROLADORURL1947001","1947001"),
    
    URL682003("FRMCONTRATOSCONTROLADORURL682003","682003"),
    
    URL104082("FRMCONTRATOSCONTROLADORURL104082","104082");  		
	

    private final String key;
    private final String value;

    private FrmcontratosControladorUrlEnum(String key,
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
