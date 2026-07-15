package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbAlmacenCincoLocal;
import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class AlmacenCinco
 */
@Stateless
@LocalBean

public class EjbAlmacenCinco
                implements EjbAlmacenCincoRemote, EjbAlmacenCincoLocal {
    /**
     * Default constructor.
     */
    public EjbAlmacenCinco() {
    }

    @Override
    public String calcularDepreciacionHNiif(
        String compania,
        int anoInicial,
        int mesInicial,
        int anoFinal,
        int mesFinal,
        String strElementoInicial,
        String strElementoFinal,
        long placaInicial,
        long placaFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOINICIAL        =>",
                                Integer.toString(anoInicial), ", ",
                                "UN_MESINICIAL        =>",
                                Integer.toString(mesInicial), ", ",
                                "UN_ANOFINAL          =>",
                                Integer.toString(anoFinal), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesFinal), ", ",
                                "UN_STRELEMENTOINICIAL =>'", strElementoInicial,
                                "', ",
                                "UN_STRELEMENTOFINAL  =>'", strElementoFinal,
                                "', ",
                                "UN_PLACAINICIAL      =>",
                                Long.toString(placaInicial), ", ",
                                "UN_PLACAFINAL        =>",
                                Long.toString(placaFinal)
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM5.FC_CALDEPRECIARH_NIIF",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {

            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlantComponentes(
        String compania,
        String estacion,
        String bodega,
        String responsable,
        boolean informe,
        String dependencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ESTACION          =>'", estacion, "', ",
                                "UN_BODEGA            =>'", bodega, "', ",
                                "UN_RESPONSABLE       =>'", responsable, "', ",
                                "UN_INFORME           =>",
                                (informe ? "-1" : "0"), ", ",
                                "UN_DEPENDENCIA       =>'", dependencia, "'"
        };
        try {
            return Acciones.clobToString(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM5.FC_PLANTILLACOMPONENTES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return dependencia;
    }

    @Override
    public void clasificacionMaterialComponentes(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM5.PR_CLASIFICACIONMATERIAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void copiarDeMovimiento(
        String compania,
        String tipoMov,
        BigInteger numero,
        String tipoMovCop,
        BigInteger numeroCop,
        Date fecha,
        String tercero,
        String sucursal,
        String auxiliar,
        String usuario) throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA    =>'", compania, "', ",
                                    "UN_TIPOMOV     =>'", tipoMov, "', ",
                                    "UN_NUMERO      => ", numero.toString(),
                                    ", ",
                                    "UN_TIPOMOVCOP  =>'", tipoMovCop,
                                    "', ",
                                    "UN_NUMEROCOP   => ",
                                    numeroCop.toString(), ", ",
                                    "UN_FECHA       =>  TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TERCERO     =>'", tercero, "', ",
                                    "UN_SUCURSAL    =>'", sucursal, "', ",
                                    "UN_AUXILIAR    =>'", auxiliar, "', ",
                                    "UN_USUARIO     =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM5.PR_COPIARDE_MOVIMIENTO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }

    }

    @Override
    public void insertInventarioFisico(
        String compania,
        Date fechaLectura,
        String dependencia,
        String responsable,
        String sucursal,
        String referencia,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA_LECTURA     =>  TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaLectura),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DEPENDENCIA       =>'", dependencia,
                                    "', ", "UN_RESPONSABLE       =>'",
                                    responsable, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_REFERENCIA        =>'", referencia,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM5.PR_INSERT_INVENTARIO",
                            SysmanFunciones.concatenar(parametros));

        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void eliminarLoteAlmacen(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                "', ", "UN_MOVIMIENTO        =>",
                                Long.toString(movimiento)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM5.PR_ELIMINARLOTE_DMOVIMIENTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void copiarPlacasLote(
        String compania,
        String tipomovimiento,
        long movimiento,
        String dependencia,
        String responsable,
        String sucursal,
        String clase,
        Date fecha,
        String usuario)
                    throws SystemException {

        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                    "', ", "UN_MOVIMIENTO        =>",
                                    Long.toString(movimiento), ", ",
                                    "UN_DEPENDENCIA       =>'", dependencia,
                                    "', ",
                                    "UN_RESPONSABLE       =>'", responsable,
                                    "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_CLASE             =>'", clase, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",

                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM5.PR_COPIARPLACASLOTE",
                            SysmanFunciones.concatenar(parametros));

        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public  String  plantillaExcelIDCBIS  (
    		String compania, 
    		String tipomovimiento, 
    		long numero) 
    				throws SystemException {
    	try {
    		String[] parametros ={                   
    				"UN_COMPANIA          =>'" , compania , "', "
    				, "UN_TIPOMOVIMIENTO    =>'" , tipomovimiento , "', "
    				, "UN_NUMERO            =>" , Long.toString(numero)   
    		};
    		return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM5.FC_PLANTILLA_IDCBIS",
    				SysmanFunciones.concatenar(parametros),
    				Types.CLOB));
    	}
    	catch (IOException | SQLException e) {
    		throw new SystemException(e);
    	}
    }

    @Override
    public void actCantidadAfectada(
        String compania,
        String tipoMov,
        BigInteger numero,
        Double cantidad,
        String elemento,
        long serie,
        String consecutivoAfect,
        Double cantAnterior,
        String accion)
                    throws SystemException {
        String[] parametros = {"UN_COMPANIA          =>'", compania, "', ",
                               "UN_TIPOMOV           =>'", tipoMov, "', ",
                               "UN_NUMERO            => ", numero.toString(),", ",
                               "UN_CANTIDAD          =>",
                    			String.valueOf(cantidad), ", ",
                    		   "UN_ELEMENTO          =>'", elemento, "', ",
                    		   "UN_SERIE             =>", Long.toString(serie),", ",
                    		   "UN_CONSECUTIVO_AFECT =>'", consecutivoAfect, "', ",
                    		   "UN_CANTANTERIOR      =>",
                    			String.valueOf(cantAnterior), ", ",
                               "UN_ACCION            =>'", accion, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
        				"PCK_ALMACEN_COM5.PR_ACT_CANTIDADAFECTADA",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void saldosInventBodega(
        String compania,
        String tipoMov,
        long movimiento,
        String elemento,
        String bodegaO,
        String bodegaD,
        String fuenteR,
        String referencia,
        String auxiliar,
        String proyecto,
        String centroCosto,
        String lote,
        Double cantidad,
        Double cantAnterior,
        String accion,
        String usuario)
                    throws SystemException {
        String[] parametros = {"UN_COMPANIA          =>'", compania,    "', ",
                               "UN_TIPOMOVIMIENTO    =>'", tipoMov,     "', ",
                               "UN_MOVIMIENTO        =>",
                                Long.toString(movimiento), ", ",
                               "UN_ELEMENTO          =>'", elemento,    "', ",
                               "UN_BODEGAO           =>'", bodegaO,     "', ",
                               "UN_BODEGAD           =>'", bodegaD,     "', ",
                               "UN_FUENTER           =>'", fuenteR,     "', ",
                               "UN_REFERENCIA        =>'", referencia,  "', ",
                               "UN_AUXILIAR          =>'", auxiliar,    "', ",
                               "UN_PROYECTO          =>'", proyecto,    "', ",
                               "UN_CCOSTO            =>'", centroCosto, "', ",
                               "UN_LOTE              =>'", lote, "', ",
                               "UN_CANTIDAD          =>",
                    			String.valueOf(cantidad), ", ",
                    		   "UN_CANTANTERIOR      =>",
                    			String.valueOf(cantAnterior), ", ",
                               "UN_ACCION            =>'", accion, "', ",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
        				"PCK_ALMACEN_COM5.PR_SALDOS_INVENTBODEGA",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public  String  generarCausacion  (
    		String compania, 
    		String tipomovimiento, 
    		long numero,
            String usuario) 
    				throws SystemException {
    	try {
    		String[] parametros ={                   
    				"UN_COMPANIA          =>'" , compania , "', "
    				, "UN_TIPOMOVIMIENTO  =>'" , tipomovimiento , "', "
    				, "UN_NUMERO          =>" , Long.toString(numero) , ", "
                    , "UN_USUARIO         =>'" , usuario , "' " 
    		};
    		return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM5.FC_CREAR_CAUSACION",
    				SysmanFunciones.concatenar(parametros),
    				Types.CLOB));
    	}
    	catch (IOException | SQLException e) {
    		throw new SystemException(e);
    	}
    }

    @Override
    public void depreciacionAcumulada(
        String compania,
        String elemento,
        String serie,
        int mes,
        int anio,
        String accion,
        String usuario
    ) throws SystemException {
        
        String[] parametros = {
            "UN_COMPANIA =>'", compania, "', ",
            "UN_ELEMENTO =>'", elemento, "', ",
            "UN_SERIE    =>'", serie, "', ",
            "UN_MES      =>", Integer.toString(mes), ", ",
            "UN_ANIO     =>", Integer.toString(anio), ", ",
            "UN_ACCION   =>'", accion, "', ",
            "UN_USUARIO  =>'", usuario, "'"
        };
        
        AccionesImp.ejecutarProcedimiento(
            ConectorPool.ESQUEMA_SYSMAN,
            "PCK_ALMACEN_COM5.PR_DEPRECIACION_ACUMULADA",
            SysmanFunciones.concatenar(parametros)
        );
    }
    
    @Override
    public void depreciacionAcumuladaInicial(
        String compania,
        String elemento,
        String serie,
        int mes,
        int anio,
        String accion,
        String usuario
    ) throws SystemException {
        
        String[] parametros = {
            "UN_COMPANIA =>'", compania, "', ",
            "UN_ELEMENTO =>'", elemento, "', ",
            "UN_SERIE    =>'", serie, "', ",
            "UN_MES      =>", Integer.toString(mes), ", ",
            "UN_ANIO     =>", Integer.toString(anio), ", ",
            "UN_ACCION   =>'", accion, "', ",
            "UN_USUARIO  =>'", usuario, "'"
        };
        
        AccionesImp.ejecutarProcedimiento(
            ConectorPool.ESQUEMA_SYSMAN,
            "PCK_ALMACEN_COM5.PR_DEPRECIACION_ACUM_INI",
            SysmanFunciones.concatenar(parametros)
        );
    }

    @Override
    public String cargarDepreciacionInicial(
    		String compania,
    		String cadena,
    		String usuario)
    				throws SystemException
    {
    	String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
    			                "UN_CADENA   =>", cadena, ", ",
    			                "UN_USUARIO  =>'", usuario, "'"
    	};
    	try
    	{
    		return Acciones.clobToStringSalto(
    				(Clob) AccionesImp.ejecutarFuncion(
    						ConectorPool.ESQUEMA_SYSMAN,
    						"PCK_ALMACEN_COM5.FC_CARGAR_DEPRECIACION_INI \r\n"
    								+ "",
    								SysmanFunciones.concatenar(
    										parametros),
    								Types.CLOB));
    	}
    	catch (IOException | SQLException e)
    	{

    		throw new SystemException(e);
    	}
    }
    
	@Override
	public void cargarElemConsumoFisico(String compania, String bodega, int ano, Date fechaCorte, String usuario)
			throws SystemException {

		try {
			String[] parametros = {
					"UN_COMPANIA     =>'", compania, "', ", 
					"UN_BODEGA       =>'", bodega, "', ", 
					"UN_ANO          =>",Integer.toString(ano), ", ", 
					"UN_FECHA_CORTE  =>TO_DATE('",SysmanFunciones.convertirAFechaCadena(fechaCorte), "','DD/MM/YYYY'), ",
					"UN_USUARIO      =>'", usuario, "'" };

			AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_ALMACEN_COM5.PR_CARGARELEMCONSUMOFISICO", SysmanFunciones.concatenar(parametros));
		} catch (ParseException e) {
			throw new SystemException(e);
		}
	}
	
	@Override
	public String aplicarAjusteInventario(
	        String compania,
	        int anio,
	        String bodegaOrigen,
	        String bodegaDestino,
	        String tipoCredito,
	        String tipoDebito,
	        String dependencia,
	        String respOrigen,
	        String sucursalResponsable,
	        String respDestino,
	        String fuenteRecurso,
	        String referencia,
	        String auxiliar,
	        String centroCosto,
	        String observaciones,
	        String fechaCorte,
	        String bodegaDebito,
	        String bodegaCredito,
	        String usuario) throws SystemException 
	{

		try {

	    String[] parametros = {
	        "UN_COMPANIA        =>'", compania, "', ",
	        "UN_ANIO            =>", Integer.toString(anio), ", ",
	        "UN_BODEGA_ORIGEN   =>'", bodegaOrigen, "', ",
	        "UN_BODEGA_DESTINO  =>'", bodegaDestino, "', ",
	        "UN_TIPO_CREDITO    =>'", tipoCredito, "', ",
	        "UN_TIPO_DEBITO     =>'", tipoDebito, "', ",
	        "UN_DEPENDENCIA     =>'", dependencia, "', ",
	        "UN_RESP_ORIGEN     =>'", respOrigen, "', ",
	        "UN_SUCURSAL_RESP     =>'", sucursalResponsable, "', ",
	        "UN_RESP_DESTINO    =>'", respDestino, "', ",
	        "UN_FUENTER         =>'", fuenteRecurso, "', ",
	        "UN_REFERENCIA      =>'", referencia, "', ",
	        "UN_AUXILIAR        =>'", auxiliar, "', ",
	        "UN_CENTROCOSTO     =>'", centroCosto, "', ",
	        "UN_OBSERVACIONES   =>'", observaciones, "', ",
	        "UN_FECHA_CORTE     =>TO_DATE('", fechaCorte, "','DD/MM/YYYY'), ",
	        "UN_BODEGA_DEBITO   =>'", bodegaDebito, "', ",
	        "UN_BODEGA_CREDITO  =>'", bodegaCredito, "', ",
	        "UN_USUARIO         =>'", usuario, "'" 
	    };

	        return Acciones.clobToStringSalto(
	                (Clob) AccionesImp.ejecutarFuncion(
	                        ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_ALMACEN_COM5.FC_APLICAR_AJUSTE_INVENTARIO",
	                        SysmanFunciones.concatenar(parametros),
	                        Types.CLOB));
	    } catch (IOException | SQLException e) {
	        throw new SystemException(e);
	    }
	}
	
	 @Override
	    public void depreciacionInicial(
	        String compania,
	        String elemento,
	        String serie,
	        int mes,
	        int anio,
	        String usuario
	    ) throws SystemException {
	        
	        String[] parametros = {
	            "UN_COMPANIA =>'", compania, "', ",
	            "UN_ELEMENTO =>'", elemento, "', ",
	            "UN_SERIE    =>'", serie, "', ",
	            "UN_MES      =>", Integer.toString(mes), ", ",
	            "UN_ANIO     =>", Integer.toString(anio), ", ",
	            "UN_USUARIO  =>'", usuario, "'"
	        };
	        
	        AccionesImp.ejecutarProcedimiento(
	            ConectorPool.ESQUEMA_SYSMAN,
	            "PCK_ALMACEN_COM5.PR_DEPRECIACION_INICIAL",
	            SysmanFunciones.concatenar(parametros)
	        );
	    }
	 
	 @Override
	 public  String crearElementoCompania  (
			 String compania, 
			 String elemento,
			 String usuario) 
					 throws SystemException {
		 try {
			 String[] parametros ={                   
					 "UN_COMPANIA          =>'" , compania , "', "
					 , "UN_ELEMENTO  	   =>'" , elemento , "', "
					 , "UN_USUARIO         =>'" , usuario , "' " 
			 };
			 return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					 ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM5.FC_CREARELEMENTOSC",
					 SysmanFunciones.concatenar(parametros),
					 Types.CLOB));
		 }
		 catch (IOException | SQLException e) {
			 throw new SystemException(e);
		 }
	 }
	 
	 @Override
	 public String crearMovimientoCompania  (
			String compania,
			String tipoMov, 
			long movimiento,
			int anio,
        	String usuario) 
					 throws SystemException {
		 try {
			 String[] parametros ={                   
					"UN_COMPANIA          =>'", compania,    "', ",
					"UN_TIPOMOVIMIENTO    =>'", tipoMov,     "', ",
					"UN_MOVIMIENTO        =>", Long.toString(movimiento), ", ",
					"UN_ANIO     =>", Integer.toString(anio), ", ",	
					"UN_USUARIO  =>'", usuario, "'"	 
			 };
			 return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
					 ConectorPool.ESQUEMA_SYSMAN, "PCK_ALMACEN_COM5.FC_CREARMOVCOMPANIAD",
					 SysmanFunciones.concatenar(parametros),
					 Types.CLOB));
		 }
		 catch (IOException | SQLException e) {
			 throw new SystemException(e);
		 }
	 }

	@Override
	public void ubicacionFisica(
			String accion,
			int idHistorial,
			String compania, 
			String elemento, 
			String serie,
			Date   fecha,
			String ubicacion, 
			String observaciones,
			String usuario) throws SystemException {
        
		try {
			String[] parametros = {
					"UN_ACCION      =>'", accion, "', ",
					"UN_IDHISTORIAL =>",Integer.toString(idHistorial), ", ",
					"UN_COMPANIA    =>'", compania, "', ",
					"UN_ELEMENTO    =>'", elemento, "', ",
					"UN_SERIE       =>'", serie, "', ",
					"UN_FECHA       =>TO_DATE('",
					SysmanFunciones.convertirAFechaCadena(
							fecha),
                    "','DD/MM/YYYY'), ",
					"UN_UBICACION       =>'", ubicacion, "', ",
					"UN_OBSERVACIONES   =>'", observaciones, "', ",
					"UN_USUARIO         =>'", usuario, "'"
			};

			AccionesImp.ejecutarProcedimiento(
					ConectorPool.ESQUEMA_SYSMAN,
					"PCK_ALMACEN_COM5.PR_UBICACION_FISICA",
					SysmanFunciones.concatenar(parametros)
					);
		}
		catch (ParseException e)
		{
			throw new SystemException(e);
		}
	}
	
	 @Override
	    public String cargarDepreciacionNoCalculada(
	    		String compania,
	    		String cadena,
	    		String usuario)
	    				throws SystemException
	    {
	    	String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
	    			                "UN_CADENA   =>", cadena, ", ",
	    			                "UN_USUARIO  =>'", usuario, "'"
	    	};
	    	try
	    	{
	    		return Acciones.clobToStringSalto(
	    				(Clob) AccionesImp.ejecutarFuncion(
	    						ConectorPool.ESQUEMA_SYSMAN,
	    						"PCK_ALMACEN_COM5.FC_CARGAR_DEP_NOCALCULADA"
	    								+ "",
	    								SysmanFunciones.concatenar(
	    										parametros),
	    								Types.CLOB));
	    	}
	    	catch (IOException | SQLException e)
	    	{

	    		throw new SystemException(e);
	    	}
	    }

}