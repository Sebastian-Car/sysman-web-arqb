package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbAlmacenUnoLocal;
import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class AlmacenUno
 * 
 * @author ybecerra
 * @version 2, 10/06/2017, Implementacion metodo concatenar de la
 * clase SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones
 */
@Stateless
@LocalBean
public class EjbAlmacenUno implements EjbAlmacenUnoRemote, EjbAlmacenUnoLocal {
    /**
     * Default constructor.
     */
    public EjbAlmacenUno() {
    }

    @Override
    public String prepararPivotDevolutivo(
        String compania,
        String codigoinicial,
        String codigofinal,
        String elementodesde,
        String elementohasta,
        String agrupacion)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_CODIGOINICIAL   =>'", codigoinicial, "', ",
                               "UN_CODIGOFINAL     =>'", codigofinal, "', ",
                               "UN_ELEMENTODESDE   =>'", elementodesde, "', ",
                               "UN_ELEMENTOHASTA   =>'", elementohasta, "', ",
                               "UN_AGRUPACION      =>'", agrupacion, "'"

        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM1.FC_PREPARARPIVOT_DEVOLUTIVO",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal calcularAcumuladoInventario(
        String compania,
        String elemento,
        Date fecha,
        int opcion,
        int tipokardex,
        long serie)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                                   "UN_ELEMENTO          =>'", elemento, "', ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_OPCION            =>",
                                   Integer.toString(opcion), ", ",
                                   "UN_TIPOKARDEX        =>",
                                   Integer.toString(tipokardex), ", ",
                                   "UN_SERIE             =>",
                                   Long.toString(serie), ""

            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM1.FC_CALACUMULADO",
                            SysmanFunciones.concatenar(parametro),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal calcularDepreciacionAcumulado(
        String compania,
        String elementoinicialin,
        String elementofinal,
        int anio,
        int mes,
        String grupo,
        int par)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                               "UN_ELEMENTOINICIALIN =>'", elementoinicialin,
                               "', ", "UN_ELEMENTOFINAL     =>'", elementofinal,
                               "', ", "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_MES               =>", Integer.toString(mes),
                               ", ", "UN_GRUPO             =>'", grupo, "', ",
                               "UN_PAR               =>", Integer.toString(par),
                               ""

        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM1.FC_DEPRECIACIONCOMODATO",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);
    }

    @Override
    public void calcularValorPromedioMovimiento(
        String compania,
        String elementorect)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA       =>'", compania, "', ",
                               "UN_ELEMENTORECT   =>'", elementorect, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM1.PR_PROMEDIOSENTRADAS",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public int rectificarPromedios(
        String compania,
        String elementorect)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_ELEMENTORECT      =>'", elementorect, "'"

        };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM1.FC_RECTIFICARPROMEDIOS",
                        SysmanFunciones.concatenar(parametro),
                        Types.INTEGER);
    }

    @Override
    public String cargarMovimientoDocAsociado(
        String compania,
        int modulo,
        String clasedocasoc,
        String tipodocasoc,
        long nrodocasoc,
        String tipomov,
        BigInteger nromov,
        int frmmov,
        String proyecto,
        String fuenteR,
        String referencia,
        String auxiliar,
        String centroCosto,
        String bodega)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_MODULO          =>",
                               Integer.toString(modulo), ", ",
                               "UN_CLASEDOCASOC     =>'", clasedocasoc, "', ",
                               "UN_TIPODOCASOC      =>'", tipodocasoc, "', ",
                               "UN_NRODOCASOC       =>",
                               Long.toString(nrodocasoc), ", ",
                               "UN_TIPOMOV          =>'", tipomov, "', ",
                               "UN_NROMOV           =>", nromov.toString(),
                               ", ", "UN_FRMMOV            =>",
                               Integer.toString(frmmov), ", ",
                               "UN_PROYECTO         =>'", proyecto, "', ",
                               "UN_FUENTER          =>'", fuenteR, "', ",
                               "UN_REFERENCIA       =>'", referencia, "', ",
                               "UN_AUXILIAR         =>'", auxiliar, "', ",
                               "UN_CCOSTO           =>'", centroCosto, "', ",
                               "UN_BODEGA           =>'", bodega, "'"

        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM1.FC_CARGAR_DETDOCASOCIADO",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String afectarMovimientoDocAsociado(
        String compania,
        int modulo,
        String clasedocasoc,
        String tipodocasoc,
        long nrodocasoc,
        String tipomov,
        long nromov,
        String usuario,
        String proyMov,
        String operMov,
        String auxMov,
        String ruedMov,
        String fuenteR,
        String referencia,
        String auxiliar,
        String centroCosto)
                    throws SystemException {
    	try {
	        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
	                               "UN_MODULO            =>",
	                               Integer.toString(modulo), ", ",
	                               "UN_CLASEDOCASOC      =>'", clasedocasoc, "', ",
	                               "UN_TIPODOCASOC       =>'", tipodocasoc, "', ",
	                               "UN_NRODOCASOC        =>",
	                               Long.toString(nrodocasoc), ", ",
	                               "UN_TIPOMOV           =>'", tipomov, "', ",
	                               "UN_NROMOV            =>", Long.toString(nromov),
	                               ", ", "UN_USUARIO           =>'", usuario, "', ",
	                               "UN_PROYMOV           =>'", proyMov, "', ",
	                               "UN_OPERMOV           =>'", operMov, "', ",
	                               "UN_AUXMOV            =>'", auxMov, "', ",
	                               "UN_RUEDMOV           =>'", ruedMov, "', ",
	                               "UN_FUENTER           =>'", fuenteR, "', ",
	                               "UN_REFERENCIA        =>'", referencia, "', ",
	                               "UN_AUXILIAR          =>'", auxiliar, "', ",
	                               "UN_CCOSTO            =>'", centroCosto, "'"
	        };
	        return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_ALMACEN_COM1.FC_AFECTARMOV_DOCASOCIADO",
	                        SysmanFunciones.concatenar(parametro),
	                        Types.CLOB));
    	} catch (IOException | SQLException e) {
    		throw new SystemException(e);
    	}
    }

    @Override
    public long generarConsecutivoAlmacen(
        String compania,
        String tipomov,
        String clasemov,
        boolean invinicial,
        long nroinicial,
        int modulo,
        String tipoElemento)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_TIPOMOV           =>'", tipomov, "', ",
                               "UN_CLASEMOV          =>'", clasemov, "', ",
                               "UN_INVINICIAL        =>",
                               invinicial ? "-1" : "0", ", ",
                               "UN_NROINICIAL        =>",
                               Long.toString(nroinicial), ", ",
                               "UN_MODULO            =>",
                               Integer.toString(modulo), ", ",
                               "UN_TIPOELEM          =>'",tipoElemento,"'"

        };
        return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM1.FC_GENERAR_CONSECUTIVOALMACEN",
                        SysmanFunciones.concatenar(parametro),
                        Types.BIGINT);
    }
    
    @Override
    public  String    cargarReexpVidaUtil(
    		String compania, 
    		String elemento, 
    		long serie, 
    		Date fecha, 
    		String vidaUtil, 
    		String usuario) 

    				throws SystemException {
    	try {
    		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
    				, "UN_ELEMENTO          =>'" , elemento , "', "
    				, "UN_SERIE             =>" , Long.toString(serie)   ,", "
    				, "UN_FECHA             =>TO_DATE('",
                    SysmanFunciones.convertirAFechaCadena(fecha),
                    "','DD/MM/YYYY'), "
    				, "UN_VIDA_UTIL         =>'" , vidaUtil , "', "
    				, "UN_USUARIO           =>'" , usuario , "'"
    		};
    		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM1.FC_CARGAR_REEXP_VID_UTL",
    				SysmanFunciones.concatenar(parametros),
    				Types.VARCHAR);
    	}
    	catch (ParseException e) {
    		throw new SystemException(e);
    	}
    }
    
    @Override
    public  String actualizarVlrSaldo(
        String compania
    ) 
                      throws SystemException {
         String[] parametros ={       
            "UN_COMPANIA          =>'" , compania , "' "            
};
         return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM1.FC_ACT_VLR_HIS",
SysmanFunciones.concatenar(parametros),
                Types.VARCHAR);
    }
    
    @Override
    public  void eliminarMovimientos(
	String compania, 
	int anio, 
	String tipomovimiento, 
	String movimiento) 
	                      throws SystemException {
	         String[] parametros ={                    "UN_COMPANIA          =>'" , compania , "', "
	                 , "UN_ANIO              =>" , Integer.toString(anio) , ", "
	                 , "UN_TIPOMOVIMIENTO    =>'" , tipomovimiento , "', "
	                 , "UN_MOVIMIENTO        =>" , movimiento  
	};
	         AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM1.PR_ELIMINAR_MOVIMIENTOS",
	SysmanFunciones.concatenar(parametros));
	    }
}