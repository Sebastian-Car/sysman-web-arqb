package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadCincoLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
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
 * Session Bean implementation class contabilidadCinco
 *
 * -- Modificado por lcortes 10/06/2017. Implementacion metodo
 * concatenar de la clase SysmanFunciones para el envio de parametros
 * a los diferentes procedimientos y funciones.
 */
@Stateless
@LocalBean
public class EjbContabilidadCinco implements EjbContabilidadCincoRemote,
                EjbContabilidadCincoLocal {
    /**
     * Default constructor.
     */
    public EjbContabilidadCinco() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public boolean verificarSaldoRegistro(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        BigDecimal valordocumento)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_ANO            =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO_CPTE      =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE    =>",
                                comprobante.toString(), ", ",
                                "UN_VALORDOCUMENTO =>",
                                valordocumento.toString() };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.FC_VERIFICARSALDOREGISTRO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void generarComprobantePresupuestalVarios(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        boolean siafecta,
        String listaNumeroAfectar,
        String listaTerceroAfectar,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_ANO              =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO             =>'", tipo, "', ",
                                "UN_NUMERO           =>", numero.toString(),
                                ", ", "UN_SIAFECTA          =>",
                                siafecta ? "-1" : "0", ", ",
                                "UN_LISTANUMEROAFECTAR =>'", listaNumeroAfectar,
                                "', ", "UN_LISTATERCEROAFECTAR =>'",
                                listaTerceroAfectar, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_GENERARCOMPTEPPTALVARIOS",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void generarComprobantePresupuestalVarios(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        boolean siafecta,
        String listaNumeroAfectar,
        String listaTerceroAfectar,
        String usuario,
        String listaNumeroAfectarcauto, 
        int variable)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_ANO              =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO             =>'", tipo, "', ",
                                "UN_NUMERO           =>", numero.toString(),
                                ", ", "UN_SIAFECTA          =>",
                                siafecta ? "-1" : "0", ", ",
                                "UN_LISTANUMEROAFECTAR =>'", listaNumeroAfectar,
                                "', ", "UN_LISTATERCEROAFECTAR =>'",
                                listaTerceroAfectar, "', ",
                                "UN_USUARIO           =>'", usuario, "',",
                                "UN_LISTANUMEROAFECTAR_CAUTO =>'", listaNumeroAfectarcauto,"', ",
                                "UN_VARIABLE           =>", Integer.toString(variable), ""};
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_GENERARCOMPTEPPTALVARIOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void afectarComprobantePptalvarios(
        String compania,
        int anoafec,
        String tipoafec,
        BigInteger numeroafec,
        int ano,
        String tipo,
        BigInteger numero,
        String clase,
        Date fecha)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_ANOAFEC    =>",
                                    Integer.toString(anoafec), ", ",
                                    "UN_TIPOAFEC   =>'", tipoafec, "', ",
                                    "UN_NUMEROAFEC =>",
                                    numeroafec.toString(), ", ",
                                    "UN_ANO        =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO       =>'", tipo, "', ",
                                    "UN_NUMERO     =>",
                                    numero.toString(), ", ",
                                    "UN_CLASE      =>'", clase, "', ",
                                    "UN_FECHA      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY')" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD5.PR_AFECTARCOMPTEPPTALVARIOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void crearDetallesPptalesVarios(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        String clase)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_ANO           =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO          =>'", tipo, "', ",
                                "UN_NUMERO        =>", numero.toString(),
                                ", ", "UN_CLASE   =>'", clase, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_CREARDETALLESPPTALESVARIOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarDebitosCreditosAfectados(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        String valorDebito,
        String valorCredito,
        int consecutivo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA             =>'", compania, "', ",
                                "UN_ANO                  =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO                 =>'", tipo, "', ",
                                "UN_NUMERO               =>", numero.toString(),
                                ", ", "UN_VALOR_DEBITO      =>'", valorDebito,
                                "', ", "UN_VALOR_CREDITO     =>'", valorCredito,
                                "', ", "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo) };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_ACT_DEBITOSCREDITOSAFECT",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void eliminarComprobantesCNT(
        String compania,
        int anio,
        String tipo,
        BigInteger numero, 
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_ANIO     =>",Integer.toString(anio), ", ",
                                "UN_TIPO     =>'", tipo, "', ",
                                "UN_NUMERO   =>", numero.toString() ,", ", 
                                "UN_USUARIO  =>'" , usuario , "'"
                               };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_ELIMINAR_COMP_CNT",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void corregirOrdenes(
        String compania)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_CORREGIRORDENES",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void revisaArAfectacionesCnth(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_ANO        =>",
                                Integer.toString(ano) };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_REVISARAFECTACIONESCNTH",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean LimiteInferiorRetencion(
        String compania,
        String tipo,
        int ano,
        String codigo,
        BigDecimal valorbase)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA    =>'", compania, "', ",
                                "UN_TIPO        =>'", tipo, "', ",
                                "UN_ANO         =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO      =>'", codigo, "', ",
                                "UN_VALORBASE   =>",
                                valorbase.toString() };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.FC_LIMITE_INFERIOR_RETEN",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean permiteCambiarValor(
        String compania,
        String tipo,
        int ano,
        String codigo)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.FC_PERMITE_CAMBIAR_VALOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean permiteCambiarValorBase(
        String compania,
        String tipo,
        int ano,
        String codigo)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.FC_PERMITE_CAMBIAR_VALORBASE",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarConfSiguienteAnio(
        String compania,
        int anioFuente,
        int anioDestino,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOFUENTE        =>",
                                Integer.toString(anioFuente), ", ",
                                "UN_ANIODESTINO       =>",
                                Integer.toString(anioDestino), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_ACTCONFEQUIV",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void actualizarAnoControlCartera(
        String compania,
        int anioFuente,
        int anioDestino,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOFUENTE        =>",
                                Integer.toString(anioFuente), ", ",
                                "UN_ANIODESTINO       =>",
                                Integer.toString(anioDestino), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_ACTUANOCONTROLCARTERA",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
	public String cargarTerceros(
			String compania, 
			String cadenaplan, 
			String usuario)
					throws SystemException {
		try {
		String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                				"UN_CADENAPLAN        =>", cadenaplan, ", ",
                				"UN_USUARIO           =>'", usuario, "'"
		};
		return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                ConectorPool.ESQUEMA_SYSMAN,
                "PCK_CONTABILIDAD5.FC_CARGAR_TERCEROS",
                SysmanFunciones.concatenar(parametros),
                Types.CLOB));
		}
        catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
		
	}
    
    @Override
    public void generarEstadoCambiosPatrimonio(
            String compania,
            int anioIni,
            int anioFin,
            int mesIni,
            int mesFin,
            String codigoIni,
            String codigoFin) throws SystemException {

        try {
            String[] parametros = {
                "UN_COMPANIA   =>'", compania, "', ",
                "UN_ANIO_INI   =>", Integer.toString(anioIni), ", ",
                "UN_ANIO_FIN   =>", Integer.toString(anioFin), ", ",
                "UN_MES_INI    =>", Integer.toString(mesIni), ", ",
                "UN_MES_FIN    =>", Integer.toString(mesFin), ", ",
                "UN_CODIGO_INI =>'", codigoIni, "', ",
                "UN_CODIGO_FIN =>'", codigoFin, "'"
            };

            AccionesImp.ejecutarProcedimiento(
                    ConectorPool.ESQUEMA_SYSMAN,
                    "PCK_CONTABILIDAD5.PR_EST_CAMBIOS_PATRI",
                    SysmanFunciones.concatenar(parametros)
            );
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public void generarCausacionIng(
        String compania,
        String anio,
        String tipo,
        String numero, 
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_ANIO     =>", anio, ", ",
                                "UN_TIPO     =>'", tipo, "', ",
                                "UN_NUMERO   =>", numero.toString() ,", ", 
                                "UN_USUARIO  =>'" , usuario , "'"
                               };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD5.PR_CAUSACION_AUTING",
                        SysmanFunciones.concatenar(parametros));
    } 

}