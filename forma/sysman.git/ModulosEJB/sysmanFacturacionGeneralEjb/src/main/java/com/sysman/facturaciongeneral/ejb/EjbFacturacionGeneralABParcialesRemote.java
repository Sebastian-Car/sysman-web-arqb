package com.sysman.facturaciongeneral.ejb;


import com.sysman.exception.SystemException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;


@Remote
public interface EjbFacturacionGeneralABParcialesRemote {
	
	
	public  boolean    verificarAbonoActivo(
			String compania, 
			int ano, 
			String tipocobro, 
			BigInteger factura) 
			                      throws SystemException;
	
	public  void    registrarPagoParcial(
			String compania, 
			int ano, 
			String tipocobro, 
			BigInteger factura, 
			Date fechacorte, 
			BigDecimal vlrintereses, 
			int diasmora, 
			BigDecimal tasa, 
			BigDecimal vlrabono, 
			String usuario,
			Date fecharesolucion,
    		Date fechaEjecutora,
    		String resolucion,
    		String expediente) 
			                      throws SystemException;
	
	public BigDecimal consultarCodigoAbono(
			String compania, 
			String tipocobro, 
			BigInteger factura) 
								throws SystemException;
	
	
    public  String    recaudarPagoParcial(
    		String compania, 
    		int ano, 
    		String tipofra, 
    		BigInteger factura, 
    		Date fechapago,
    		Date fechaPagoBanco,
    		String cuentarecaudo, 
    		String usuario) 
                      throws SystemException;
    
    public  String    cuotasActPagoParcial(
    		String compania, 
    		int ano, 
    		String tipofra, 
    		BigInteger factura) 
                      throws SystemException;

}
