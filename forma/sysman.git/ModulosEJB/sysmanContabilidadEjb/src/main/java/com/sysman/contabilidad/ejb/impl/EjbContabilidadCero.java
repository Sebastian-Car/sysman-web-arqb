package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadCeroGeneralRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilidadCero * Modificado por
 * jgomez 10/06/2017. Implementacion metodo concatenar de la clase
 * SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones.
 */
@Stateless
@LocalBean
public class EjbContabilidadCero
                implements EjbContabilidadCeroRemote, EjbContabilidadCeroLocal {

    @EJB
    private EjbContabilidadCeroGeneralRemote ejbContabilidadCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbContabilidadCero() {
    }

    @Override
    public void mayorizarContable(
        String compania,
        int anio,
        int mesIni,
        int mesFin)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "' ",
                               ", UN_ANO        =>", Integer.toString(anio),
                               ", UN_MES_INI    =>", Integer.toString(mesIni),
                               ", UN_MES_FIN    =>", Integer.toString(mesFin)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_MAYORIZARCUENTAS",
                        SysmanFunciones.concatenar(parametro));

    }

    @Override
    public void corregirSaldosAuxiliaresContables(
        String compania,
        int anio,
        int mesIni,
        int mesFin,
        String codigoIni,
        String codigoFin)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "' ",
                               ", UN_ANIO       =>", Integer.toString(anio),
                               ", UN_MES_INI    =>", Integer.toString(mesIni),
                               ", UN_MES_FIN    =>", Integer.toString(mesFin),
                               ", UN_CODIGO_INI =>'", codigoIni, "' ",
                               ", UN_CODIGO_FIN =>'", codigoFin, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_CUADRECONTA_AUX",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void actualizarIndicadorDeMovimientoContable(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "' ",
                               ", UN_ANIO       =>", Integer.toString(anio)
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_REVISAR_MOVIMIENTO_CONTABLE",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void cargarSaldosIniciales(
        String compania,
        int anio,
        String modificador)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "' ",
                               ", UN_ANIO       =>", Integer.toString(anio),
                               ", UN_MODIFICADOR  =>'", modificador, "'"
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_SUBIR_SALDOS_INICIALES",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void prepararSaldosAnoSiguiente(
        String compania,
        int anoDestino)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "' ",
                               ", UN_ANO_DESTINO     =>",
                               Integer.toString(anoDestino)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_PASAR_SALDOS_ANO_SIGUIENTE",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public String corregirChequera(
        String compania,
        int ano,
        String cuenta)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "' ",
                               ", UN_ANO     =>", Integer.toString(ano),
                               ", UN_CUENTA  =>'", cuenta, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.FC_CORRECION_CHEQUERA",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public boolean revisarConciliacionPeriodo(
        int ano,
        int mes)
                    throws SystemException {
        String[] parametro = { "UN_ANO               =>", Integer.toString(ano),
                               ", ",
                               "UN_MES               =>", Integer.toString(mes)
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.FC_VERIFICAPERIODOCONCILIA",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public BigDecimal consultarSaldosCaja(
        String compania,
        Date fecha,
        String cuenta,
        String naturaleza)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY HH24:MI:SS'), ",
                                   "UN_CUENTA            =>'", cuenta, "', ",
                                   "UN_NATURALEZA        =>'", naturaleza, "'"
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD.FC_SALDOINICIALCAJA",
                            SysmanFunciones.concatenar(parametro),
                            Types.NUMERIC);
        }
        catch (ParseException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    @Override
    public void cambiarNitTerceros(
        String nueCompania,
        String nueNit,
        String nueSucursal,
        String antCompania,
        String antNit,
        String antSucursal, String usuario)
                    throws SystemException {

        ejbContabilidadCeroGeneral.cambiarNitTerceros(nueCompania, nueNit,
                        nueSucursal, antCompania, antNit, antSucursal, usuario);
    }

    @Override
    public boolean verificarPeriodo(
        String compania,
        int ano,
        int mes)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ", "UN_MES               =>",
                               Integer.toString(mes)
        };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.FC_VERIFICAPERIODO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String verificarInconsistencias(
        String compania,
        int ano)
                    throws SystemException {

        return ejbContabilidadCeroGeneral.verificarInconsistencias(compania,
                        ano);

    }
    
    @Override
    public String validarCuentaUtilizar(
    		String compania,
			int ano, String cuenta, boolean validaBloqueo) throws SystemException {
		return ejbContabilidadCeroGeneral.validarCuentaUtilizar(compania, ano, cuenta, validaBloqueo);

	}

	@Override
	public String cambiarFechaComprobante(String compania, String anio, String tipoComprobante, String comprobante,
			Date fechaActual, String usuario) throws SystemException {
		try {
			String[] parametro = { "UN_COMPANIA  =>'", compania,
					"' ", ", UN_ANIO     =>", anio,
					", UN_TIPOCOMPROBANTE     => '", tipoComprobante,
					"' ", ", UN_COMPROBANTE     =>", comprobante,
					", UN_FECHAACTUAL    =>TO_DATE('",
	                SysmanFunciones.convertirAFechaCadena(
	                		fechaActual),
                    "' ,'DD/MM/YYYY'), ",
                    "  UN_USUARIO  =>'",usuario ,"' "};
			
			return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CONTABILIDAD.FC_CAMBIARFECHACOMPROBANTE", SysmanFunciones.concatenar(parametro), Types.VARCHAR);
		
		}
	    catch (ParseException e) {
	        throw new SystemException(e);
	    }
	}
	
	@Override
    public void perpararDatosAjusteFiscal(String compania, 
    		String anoFiscal, 
    		String mesFiscal) 
    				throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "' ",
                               ", UN_ANO_FISCAL        =>", anoFiscal,
                               ", UN_MES_FISCAL    =>", mesFiscal,
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_PERPARARDATOSAJUSTEFISCAL",
                        SysmanFunciones.concatenar(parametro));

    }
	
	
    @Override
    public  void   eliminarComprobantesIngreso(
	String compania, 
	int ano, 
	String tipo, 
	String numero, 
	String tercero) 
	                      throws SystemException {
    	String[] parametros = {
    		    "UN_COMPANIA          =>'", compania  , "', "
    		    , "UN_ANO               =>" , 	Integer.toString(ano) , ", "
                , "UN_TIPO              =>'" , tipo , "', "
                , "UN_NUMERO            =>'" , numero , "', "
                , "UN_TERCERO           =>'" , tercero , "'"
    		};

	         AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_CONTABILIDAD.PR_ELIMINAR_COMP_ING",
	SysmanFunciones.concatenar(parametros));
	    }

	
}