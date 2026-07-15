package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadTresLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
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
 * Session Bean implementation class ContabilidadTres
 * 
 * @modifier amonroy, 10/06/2017 Implementacion de SysmanFunciones.concatenar() para envio de parametros a funciones y procedimientos
 */
@Stateless
@LocalBean
public class EjbContabilidadTres
                implements EjbContabilidadTresRemote, EjbContabilidadTresLocal
{
    /**
     * Default constructor.
     */
    public EjbContabilidadTres()
    {
    }

    @Override
    public String verificarInconsistenciasCuentasContables(
                    String compania,
                    int anio)
                    throws SystemException
    {

        String[] parametros = { "UN_COMPANIA         =>'", compania, "' ",
                        ",UN_ANIO            =>",
                        Integer.toString(anio) };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.FC_CALCULAR_INCONSISTENCIAS",
                        SysmanFunciones.concatenar(parametros), Types.VARCHAR);
    }

    @Override
    public String generarCuentasContablesNiif(
                    String compania,
                    int anio,
                    String codigo)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA             =>'", compania, "' ",
                        ",UN_ANIO                =>",
                        Integer.toString(anio),
                        ",UN_CODIGO              =>'", codigo, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.FC_CREA_CUENTA_NIIF",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String contabilizarComprobantesContablesNiif(
                    String compania,
                    int mesInicial,
                    int mesFinal,
                    String tipoInicial,
                    String tipoFinal,
                    int anio,
                    String usuario)
                    throws SystemException
    {

        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                        "UN_MES_INICIAL        =>",
                        Integer.toString(mesInicial), ", ",
                        "UN_MES_FINAL          =>",
                        Integer.toString(mesFinal), ", ",
                        "UN_TIPO_INICIAL       =>'", tipoInicial, "', ",
                        "UN_TIPO_FINAL         =>'", tipoFinal, "', ",
                        "UN_ANIO               =>",
                        Integer.toString(anio), ", ",
                        "UN_USUARIO            =>'", usuario, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.FC_NIIF_LOTES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String consultarPredecesorCuentaContable(
                    String compania,
                    int anio,
                    String codigo)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                        "UN_ANIO             =>",
                        Integer.toString(anio), ", ",
                        "UN_CODIGO           =>'", codigo, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.FC_PREDECESOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean insertarCuentaContableEnNiif(
                    String companianiif,
                    String compania,
                    int anoFuente,
                    int anoDestino,
                    String codigo,
                    String usuario)
                    throws SystemException
    {

        String[] parametros = { "UN_COMPANIANIIF       =>'", companianiif,
                        "', ",
                        "UN_COMPANIA           =>'", compania, "', ",
                        "UN_ANO_FUENTE         =>",
                        Integer.toString(anoFuente), ", ",
                        "UN_ANO_DESTINO        =>",
                        Integer.toString(anoDestino), ", ",
                        "UN_CODIGO             =>'", codigo, "', ",
                        "UN_USUARIO            =>'", usuario, "' " };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.FC_CREARCUENTANIIF",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void copiarConfiguracionEquivalenteNIIF(
                    String compania,
                    int anoFuente,
                    int anoDestino)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                        "UN_ANO_FUENTE          =>",
                        Integer.toString(anoFuente), ", ",
                        "UN_ANO_DESTINO         =>",
                        Integer.toString(anoDestino) };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.PR_EQUIVALENTENIIIF",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void subirSaldosInicialesNiif(
                    String compania,
                    String companianiif,
                    int anoFuente,
                    int anoDestino,
                    boolean clave,
                    String usuario)
                    throws SystemException
    {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_COMPANIANIIF      =>'", companianiif, "', ",
                        "UN_ANO_FUENTE        =>",
                        Integer.toString(anoFuente), ", ",
                        "UN_ANO_DESTINO       =>",
                        Integer.toString(anoDestino), ", ",
                        "UN_CLAVE             =>", clave
                                        ? String.valueOf(-1)
                                        : String.valueOf(0),
                        ", ", "UN_USUARIO            =>'", usuario,
                        "' " };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD3.PR_SUBIRSALDOSNIIF",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reclasificarniif(
                    String compania,
                    int ano,
                    String tipo,
                    int numeroregistro,
                    long consecutivo,
                    Date fecha,
                    String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                            "UN_ANO               =>",
                            Integer.toString(ano), ", ",
                            "UN_TIPO              =>'", tipo, "', ",
                            "UN_NUMEROREGISTRO    =>",
                            Integer.toString(numeroregistro), ", ",
                            "UN_CONSECUTIVO       =>",
                            Long.toString(consecutivo), ", ",
                            "UN_FECHA             =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fecha),
                            "','DD/MM/YYYY'), ",
                            "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD3.PR_RECLASIFICARNIIF",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String inconsistenciasPlanoBancolombia(String compania, int ano,
                    String tipo, String cuentaInicial, String cuentaFinal,
                    String numeroInicial, String numeroFinal, String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                            "UN_ANO               =>",
                            Integer.toString(ano), ", ",
                            "UN_TIPO               =>'", tipo, "', ",
                            "UN_CUENTA_INICIAL              =>'",
                            cuentaInicial, "', ",
                            "UN_CUENTA_FINAL            =>'",
                            cuentaFinal, "', ",
                            "UN_NUMERO_INICIAL              =>",
                            numeroInicial, ", ",
                            "UN_NUMERO_FINAL            =>",
                            numeroFinal, ", ",
                            "UN_USUARIO           =>'", usuario, "'" };
            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD3.FC_INCONS_PLANOBANCOLOMBIA",
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
    public  String  asobancariaImportarCT(
    		String compania, 
    		String usuario, 
    		String cadena) 
    		throws SystemException
    {
    	String[] parametros ={	"UN_COMPANIA          =>'" , compania , "', "
				              , "UN_USUARIO           =>'" , usuario , "', "
					          , "UN_CADENA            =>", cadena, ""
		};

		return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, 
					"PCK_CONTABILIDAD3.FC_ASOBANCARIA_IMPORTARCT",
					SysmanFunciones.concatenar(parametros),
					Types.VARCHAR);
    	
    }
    
    @Override
    public void reclasificarNiifMensual(
                    String compania,
                    int ano,
                    String tipo,
                    int numeroregistro,
                    long consecutivo,
                    Date fecha,
                    String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { 
            				"UN_COMPANIA          =>'", compania, "', ",
                            "UN_ANO               =>", Integer.toString(ano), ", ",
                            "UN_TIPO              =>'", tipo, "', ",
                            "UN_NUMEROREGISTRO    =>",Integer.toString(numeroregistro), ", ",
                            "UN_CONSECUTIVO       =>", Long.toString(consecutivo), ", ",
                            "UN_FECHA             =>TO_DATE('",SysmanFunciones.convertirAFechaCadena(fecha),
                            						"','DD/MM/YYYY'), ",
                            "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD3.PR_RECLASIFICARNIIF_MENSUAL",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public  void    actPagosFacturacion(
    		String compania, 
    		Date fechaIni, 
    		Date fechaFin, 
    		String tipoCobroIni, 
    		String tipoCobroFin, 
    		String facturaIni, 
    		String facturaFin,
    		String general) 
    				throws SystemException {
    	try {
    		String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
    				, "UN_FECHA_INI         =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaIni) , "','DD/MM/YYYY'), "
    				, "UN_FECHA_FIN         =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechaFin) , "','DD/MM/YYYY'), "
    				, "UN_TIPO_COBRO_INI    =>'" , tipoCobroIni , "', "
    				, "UN_TIPO_COBRO_FIN    =>'" , tipoCobroFin , "', "
    				, "UN_FACTURA_INI       =>'" ,facturaIni, "', "
    				, "UN_FACTURA_FIN       =>'" ,facturaFin, "',"
    				, "UN_GENERAL           =>'", general ,"'"
    		};
    		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIDAD3.PR_ACTPAGOS_FACT",
    				SysmanFunciones.concatenar(parametros));
    	}
    	catch (ParseException e) {
    		throw new SystemException(e);
    	}
    }
    
    
    @Override
    public boolean causacionAutomatica(
        String compania,       
        int ano,        
        String tipo,
        BigInteger numero,        
        String usuario,
        int varCausar)
                    throws SystemException {
    	byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",                                    
		                        "UN_ANO               =>", Integer.toString(ano), ", ",
		                        "UN_TIPO              =>'", tipo, "', ",
		                        "UN_NUMERO            =>",numero.toString(), ", ",                                    
		                        "UN_USUARIO => '", usuario,"', ",
		                        "UN_VARIABLE => ", Integer.toString(varCausar),"" };
        salida = (byte) AccionesImp.ejecutarFuncion(
		                ConectorPool.ESQUEMA_SYSMAN,
		                "PCK_CONTABILIDAD3.FC_CAUSACIONAUTOMATICA",
		                SysmanFunciones.concatenar(parametros),
		                Types.TINYINT);
        return salida == 0 ? false : true;
    }
    
	@Override
    public  void validacionCausacionAutomatica(
            String compania,       
            int ano,        
            String tipo,
            BigInteger numero,        
            String usuario)
                        throws SystemException {

    	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",                                    
		                        "UN_ANO               =>", Integer.toString(ano), ", ",
		                        "UN_TIPO              =>'", tipo, "', ",
		                        "UN_NUMERO            =>",numero.toString(), ", ",                                    
		                        "UN_USUARIO => '", usuario,"'" };
		AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIDAD3.PR_VALCAUSACIONAUTOMATICA",
				SysmanFunciones.concatenar(parametros));
    	}
	
	@Override
	 public int pasarTasasInteres(
		        int anioIni,
		        int anioFin,
		        String usuario)
		                    throws SystemException {
		        String[] parametro = { "UN_ANIO_INI  =>", Integer.toString(anioIni),
		                               ", UN_ANIO_FIN  =>", Integer.toString(anioFin),
		                               ", UN_USUARIO   =>'", usuario, "'"
		        };
		        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
		                        "PCK_CONTABILIDAD3.FC_PASAR_TASAS_INT",
		                        SysmanFunciones.concatenar(parametro),
		                        Types.INTEGER);
		    }
	
	@Override
	public String generarPlanoSudameris(String compania, int ano,
	                String egresoInicial, String egresoFinal,
	                String fechaPago, String cuentaDebitar,
	                String cuentaPrincipalAfiliada, String codigoBanco,
	                String tipoEgreso, String cuentaInicial,
	                String cuentaFinal, String identificador,
	                String codigoVerificacion, String tipoCuentaCliente,
	                String claseTransaccion) throws SystemException {
	    try {
	        String[] parametros = {
	            "UN_COMPANIA          =>'", compania, "', ",
	            "UN_ANO               =>", Integer.toString(ano), ", ",
	            "UN_EGRESO_INICIAL    =>", egresoInicial, ", ",
	            "UN_EGRESO_FINAL      =>", egresoFinal, ", ",
	            "UN_FECHA_PAGO        =>'", fechaPago, "', ",
	            "UN_CUENTA_DEBITAR    =>", cuentaDebitar, ", ",
	            "UN_CUENTA_PRINCIPAL_AFILIADA =>", cuentaPrincipalAfiliada, ", ",
	            "UN_CODIGO_BANCO      =>'", codigoBanco, "', ",
	            "UN_TIPO_EGRESO       =>'", tipoEgreso, "', ",
	            "UN_CUENTA_INICIAL    =>'", cuentaInicial, "', ",
	            "UN_CUENTA_FINAL      =>'", cuentaFinal, "', ",
	            "UN_IDENTIFICADOR     =>", identificador, ", ",
	            "UN_CODIGO_VERIFICACION =>", codigoVerificacion, ", ",
	            "UN_TIPO_CUENTA_CLIENTE =>'", tipoCuentaCliente, "', ",
	            "UN_CLASE_TRANSACCION =>'", claseTransaccion, "'"
	        };
	        
	        return Acciones.clobToStringSalto((Clob) AccionesImp
	                .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_CONTABILIDAD3.FC_GENERAR_PLANO_SUDAMERIS",
	                        SysmanFunciones.concatenar(parametros),
	                        Types.CLOB));
	    } catch (IOException | SQLException e) {
	        throw new SystemException(e);
	    }
	}
	
	public String generarPlanoItau(String compania, int ano,
	        String egresoInicial, String egresoFinal,
	        String fechaPago, String cuentaDebitar,
	        String cuentaPrincipalAfiliada, String codigoBanco,
	        String tipoEgreso, String cuentaInicial,
	        String cuentaFinal, String identificador,
	        String codigoVerificacion, String tipoCuentaCliente,
	        String nitCliente) throws SystemException {  // ← QUITAR claseTransaccion
	    try {
	        String[] parametros = {
	            "UN_COMPANIA          =>'", compania, "', ",
	            "UN_ANO               =>", Integer.toString(ano), ", ",
	            "UN_EGRESO_INICIAL    =>", egresoInicial, ", ",
	            "UN_EGRESO_FINAL      =>", egresoFinal, ", ",
	            "UN_FECHA_PAGO        =>'", fechaPago, "', ",
	            "UN_CUENTA_DEBITAR    =>'", cuentaDebitar, "', ",
	            "UN_CUENTA_PRINCIPAL_AFILIADA =>", cuentaPrincipalAfiliada, ", ",
	            "UN_CODIGO_BANCO      =>'", codigoBanco, "', ",
	            "UN_TIPO_EGRESO       =>'", tipoEgreso, "', ",
	            "UN_CUENTA_INICIAL    =>'", cuentaInicial, "', ",
	            "UN_CUENTA_FINAL      =>'", cuentaFinal, "', ",
	            "UN_IDENTIFICADOR     =>", identificador, ", ",
	            "UN_CODIGO_VERIFICACION =>", codigoVerificacion, ", ",
	            "UN_TIPO_CUENTA_CLIENTE =>'", tipoCuentaCliente, "', ",
	            "UN_NIT_CLIENTE       =>'", nitCliente, "'"  
	          
	        };
	        
	        return Acciones.clobToStringSalto((Clob) AccionesImp
	                .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
	                        "PCK_CONTABILIDAD3.FC_GENERAR_PLANO_ITAU",
	                        SysmanFunciones.concatenar(parametros),
	                        Types.CLOB));
	    } catch (IOException | SQLException e) {
	        throw new SystemException(e);
	    }
	}
	@Override
	public String pasarProvedores(
	    String compania,
	    int anoOrigen,
	    int anoDestino
	) throws SystemException {
	    try {
	        String[] parametro = {
	            "UN_COMPANIA    =>'", compania, "', ",
	            "UN_ANO_ORIGEN  =>", Integer.toString(anoOrigen), ", ",
	            "UN_ANO_DESTINO =>", Integer.toString(anoDestino)
	        };
	        
	        return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
	            ConectorPool.ESQUEMA_SYSMAN,
	            "PCK_CONTABILIDAD3.FC_PASAR_PROVEDORES",
	            SysmanFunciones.concatenar(parametro),
	            Types.CLOB
	        ));
	        
	    } catch (IOException | SQLException e) {
	        throw new SystemException(e);
	    }
	}
	
	@Override
	public String pasarReferenciados(
	    String compania,
	    int anoOrigen,
	    int anoDestino
	) throws SystemException {
	    try {
	        String[] parametro = {
	            "UN_COMPANIA    =>'", compania, "', ",
	            "UN_ANO_ORIGEN  =>", Integer.toString(anoOrigen), ", ",
	            "UN_ANO_DESTINO =>", Integer.toString(anoDestino)
	        };
	        
	        return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
	            ConectorPool.ESQUEMA_SYSMAN,
	            "PCK_CONTABILIDAD3.FC_PASAR_REF_SERV_CTA",
	            SysmanFunciones.concatenar(parametro),
	            Types.CLOB
	        ));
	        
	    } catch (IOException | SQLException e) {
	        throw new SystemException(e);
	    }
	}

	@Override
	public String cargarMasivoRetencion(
	        String compania,
	        String cadena,
	        int mes,
	        int dia,
	        String usuario) throws SystemException {
	    try {
	        String[] parametro = {
	            "UN_COMPANIA => '", compania, "', ",
	            "UN_CADENA   => ", cadena, ", ",   // <-- sin comillas
	            "UN_MES      => ", Integer.toString(mes), ", ",
	            "UN_DIA      => ", Integer.toString(dia), ", ",
	            "UN_USUARIO  => '", usuario, "'"
	        };

	        return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
	            ConectorPool.ESQUEMA_SYSMAN,
	            "PCK_CONTABILIDAD3.FC_CARGUE_MASIVO_RETENCION",
	            SysmanFunciones.concatenar(parametro),
	            Types.CLOB
	        ));

	    } catch (IOException | SQLException e) {
	        throw new SystemException(e);
	    }
	}

	}
