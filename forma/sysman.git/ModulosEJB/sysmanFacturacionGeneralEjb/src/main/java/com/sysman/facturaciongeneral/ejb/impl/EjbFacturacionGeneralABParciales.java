package com.sysman.facturaciongeneral.ejb.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralABParcialesLocal;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralABParcialesRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.persistencia.ConectorPool;

/**
 * Session Bean implementation class FacturacionGeneralABParciales
 */
@Stateless
@LocalBean
public class EjbFacturacionGeneralABParciales 
				implements EjbFacturacionGeneralABParcialesRemote,
				EjbFacturacionGeneralABParcialesLocal {
	
	/**
     * Default constructor.
     */
	public EjbFacturacionGeneralABParciales (){
		//Constructor vacio
	}
	
	@Override
    public  boolean    verificarAbonoActivo(
		String compania, 
		int ano, 
		String tipocobro, 
		BigInteger factura) 
                      throws SystemException {
		byte salida; 		
		try {
	         String[] parametros ={      "UN_COMPANIA          =>'" , compania , "', "
						                 , "UN_ANO               =>" , Integer.toString(ano) , ", "
						                 , "UN_TIPOCOBRO         =>'" , tipocobro , "', "
						                 , "UN_FACTURA           =>" ,factura.toString() 
	         };
	         salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_ABPARCIALES.FC_VERIFICAR_ABACTIVOS",
	        		 SysmanFunciones.concatenar(parametros),
	                Types.TINYINT);
		} catch (Exception e) {
			throw new SystemException(e);
		}
                return salida == 0 ? false : true;
    }
	
	@Override
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
                      throws SystemException {
         	try {
         		String[] parametros ={  "UN_COMPANIA          =>'" , compania , "', "
         						    	, "UN_ANO               =>" , Integer.toString(ano) , ", "
         						    	, "UN_TIPOCOBRO         =>'" , tipocobro , "', "
         						    	, "UN_FACTURA           =>" ,factura.toString() ,", "
         						    	, "UN_FECHACORTE        =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechacorte) , "','DD/MM/YYYY'), "
         						    	, "UN_VLRINTERESES      =>" ,vlrintereses.toString() ,", "
         						    	, "UN_DIASMORA          =>" , Integer.toString(diasmora) , ", "
         						    	, "UN_TASA              =>" ,tasa.toString() ,", "
         						    	, "UN_VLRABONO          =>" ,vlrabono.toString() ,", "
         						    	, "UN_USUARIO           =>'" , usuario , "', "         						    	
         						    	, "UN_FECHARESOLUCION   =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fecharesolucion) , "','DD/MM/YYYY'), "
         						    	, "UN_FECHAEJECUTORA    =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaEjecutora) , "','DD/MM/YYYY'), "
         						    	, "UN_RESOLUCION        =>'" , resolucion , "', "
         						    	, "UN_EXPEDIENTE        =>'" , expediente , "'"
         		};
         AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_ABPARCIALES.PR_REGISTRAR_PAGOPARCIAL",
        		 SysmanFunciones.concatenar(parametros));
         }
         catch (ParseException e) {
             throw new SystemException(e);
          }
    }

	@Override
    public  BigDecimal    consultarCodigoAbono(
    		String compania, 
    		String tipocobro, 
    		BigInteger factura) 
                      throws SystemException {
         	String[] parametros ={	"UN_COMPANIA          =>'" , compania , "', "
         							, "UN_TIPOCOBRO         =>'" , tipocobro , "', "
         							, "UN_FACTURA           =>" ,factura.toString() 
         	};
         return (BigDecimal) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_ABPARCIALES.FC_CONSULTAR_ABCODIGO",
        		 SysmanFunciones.concatenar(parametros),
                Types.DECIMAL);
    }

	@Override
    public  String    recaudarPagoParcial(
    		String compania, 
    		int ano, 
    		String tipofra, 
    		BigInteger factura, 
    		Date fechapago, 
    		Date fechaPagoBanco,
    		String cuentarecaudo, 
    		String usuario) 
                      throws SystemException {
         	try {
         		String[] parametros ={  "UN_COMPANIA          =>'" , compania , "', "
         								, "UN_ANO               =>" , Integer.toString(ano) , ", "
         								, "UN_TIPOFRA           =>'" , tipofra , "', "
         								, "UN_FACTURA           =>" ,factura.toString() ,", "
         								, "UN_FECHAPAGO         =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechapago) , "','DD/MM/YYYY'), "
         								, "UN_FECHAPAGOBANCO    =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaPagoBanco) , "','DD/MM/YYYY'), "
         								, "UN_CUENTARECAUDO     =>'" ,cuentarecaudo ,"', "
         								, "UN_USUARIO           =>'" , usuario , "'"
         		};
         	return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_ABPARCIALES.FC_RECAUDAR_PAGOPARCIAL",
         				SysmanFunciones.concatenar(parametros),
                Types.VARCHAR );
         	}
         	catch (ParseException e) {
         		throw new SystemException(e);
         	}
    }

    @Override
    public  String    cuotasActPagoParcial(
    		String compania, 
    		int ano, 
    		String tipofra, 
    		BigInteger factura) 
                      throws SystemException {
         	String[] parametros ={	"UN_COMPANIA          =>'" , compania , "', "
         							, "UN_ANO               =>" , Integer.toString(ano) , ", "
         							, "UN_TIPOFRA           =>'" , tipofra , "', "
         							, "UN_FACTURA           =>" ,factura.toString() ," "
         							
         	};
         	return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_FACT_GENERAL_ABPARCIALES.FC_CUOTASACTIVAS_PAGOPARCIAL",
         			SysmanFunciones.concatenar(parametros),
                Types.VARCHAR );
    }

}	
