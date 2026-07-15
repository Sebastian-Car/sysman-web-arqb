/**
 * 
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author kmartinez
 *
 */
public enum FrmPagosparcialesControladorEnum {
	
	ANOCOBRO("ANOCOBRO"),
	
	TIPOCOBRO("TIPOCOBRO"),
	
	TIPOABONO("TIPOABONO"),
	
	TIPOFACTURA("TIPOFACTURA"),
	
	NROFACTURA("NROFACTURA"),
	
	VALORTOTAL("VALORTOTAL"),
	
	VALOR("VALOR"),
	
	FECHAVENCIMIENTO("FECHA_VENCIMIENTO"),
	
	FECHAEXPEDICION("FECHA_EXPEDICION"),
	
	SF_DETALLE_FACTURA ("SF_DETALLE_FACTURA"),

	FECHAULTIMOABONO("FECHA_ULTIMOABONO"),
	
	DIASMORA("DIAS_MORA"),
	
	ABONO("ABONO"),
	
	FECHACORTE("FECHA_CORTE"),
	
	INTERESES ("INTERESES_MORA"),
	
	NROABONO("NROABONO"),
	
	VALOR_ACTUALIZADO("VALOR_ACTUALIZADO"),
	
	FECHARESOLUCION("FECHA_RESOLUCION"),
	
	FECHAEJECUTORA("FECHA_EJECUTORA"),
	
	NUMERORESOLUCION("NUMERO_RESOLUCION"),
	
	NUMEROEXPEDIENTE("NUMERO_EXPEDIENTE"),
	
	SALDO("SALDO");
	
	private final String value;
	
	private FrmPagosparcialesControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
        return value;
    }
}
