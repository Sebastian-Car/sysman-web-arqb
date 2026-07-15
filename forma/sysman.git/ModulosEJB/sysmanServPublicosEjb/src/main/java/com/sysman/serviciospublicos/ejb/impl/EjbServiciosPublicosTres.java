package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosTres
 * 
 * @author ybecerra
 * @version 2, 10/06/2017, Implementacion metodo concatenar de la
 * clase SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosTres implements EjbServiciosPublicosTresRemote,
                EjbServiciosPublicosTresLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosTres() {
    }

    @Override
    public void actualizarEstado(
        String compania,
        int ciclo,
        String codigoRuta,
        String estadoNuevo,
        String usuario,
        String codigoAuditoria)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                                "UN_ESTADONUEVO       =>'", estadoNuevo, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CODIGOAUDITORIA   =>'", codigoAuditoria,
                                "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_ACTUALIZARESTADO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String auditoriaGeneral(
        String compania,
        String usuario,
        String macroproceso,
        String subproceso,
        int anio,
        String periodo,
        String codinterno,
        String descripcion)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_USUARIO       =>'", usuario, "', ",
                               "UN_MACROPROCESO  =>'", macroproceso, "', ",
                               "UN_SUBPROCESO    =>'", subproceso, "', ",
                               "UN_ANIO          =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO       =>'", periodo, "', ",
                               "UN_CODINTERNO    =>'", codinterno, "', ",
                               "UN_DESCRIPCION   =>'", descripcion, "'"

        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.FC_AUDITORIAGENERAL",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public boolean validarConceptoFacturado(
        String compania,
        String codigoruta,
        int ciclo,
        int concepto,
        int ano,
        String periodo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_CODIGORUTA  =>'", codigoruta, "', ",
                               "UN_CICLO       =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CONCEPTO    =>",
                               Integer.toString(concepto), ", ",
                               "UN_ANO         =>", Integer.toString(ano),
                               ", ", "UN_PERIODO   =>'", periodo, "'"

        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.FC_TIENECONCEPTOFACTURADO",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);

        return rta != 0;
    }

    @Override
    public void auditarModif(
        String compania,
        String formorigen,
        int intTipo,
        String campo,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_FORMORIGEN      =>'", formorigen, "', ",
                               "UN_INTTIPO         =>",
                               Integer.toString(intTipo), ", ",
                               "UN_CAMPO           =>'", campo, "', ",
                               "UN_USUARIO         =>'", usuario, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_AUDITARMODIF",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void auditarRegistroComparar(
        String compania,
        String formorigen,
        String strcampo,
        String camposmodif,
        String usuario,
        int ciclo,
        String codigoruta,
        int anio,
        String periodo,
        int cont)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_FORMORIGEN        =>'", formorigen, "', ",
                               "UN_STRCAMPO          =>'", strcampo, "', ",
                               "UN_CAMPOSMODIF       =>'", camposmodif, "', ",
                               "UN_USUARIO           =>'", usuario, "', ",
                               "UN_CICLO         =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA    =>'", codigoruta, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO       =>'", periodo, "', ",
                               "UN_CONT              =>",
                               Integer.toString(cont), ""

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_AUDITARREGCOMPARAR",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void preparaExcluirCartera(
        String compania,
        int cicloinicial,
        int ciclofinal,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CICLOINICIAL      =>",
                               Integer.toString(cicloinicial), ", ",
                               "UN_CICLOFINAL        =>",
                               Integer.toString(ciclofinal), ", ",
                               "UN_USUARIO           =>'", usuario, "' "
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_PREPARAEXCLUIRCARTERA",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void precargarPromedios(
        String compania,
        String codigointerno,
        int ciclo,
        String codigoruta)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CODIGOINTERNO =>'", codigointerno, "', ",
                               "UN_CICLO         =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA    =>'", codigoruta, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_PRECARGARPROMEDIOS",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void operarConsumoManual(
        String compania,
        int ciclo,
        String codigointerno,
        int opcion,
        BigDecimal consumo,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CICLO       =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGOINTERNO     =>'", codigointerno, "', ",
                               "UN_OPCION            =>",
                               Integer.toString(opcion), ", ",
                               "UN_CONSUMO           =>", consumo.toString(),
                               ", ",
                               "UN_USUARIO           =>'", usuario, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_OPERACONSUMOMANUAL",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public Date ultimoDia(
        Date fecha)
                    throws SystemException {
        try {
            String[] parametro = { "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY')"

            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM3.FC_ULTIMODIA",
                            SysmanFunciones.concatenar(parametro),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void anularFinanciabledeDeuda(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        BigDecimal vlrafinanciar)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA  =>'", codigoruta, "', ",
                               "UN_ANO         =>", Integer.toString(ano),
                               ", ", "UN_PERIODO =>'", periodo, "', ",
                               "UN_VLRAFINANCIAR     =>",
                               vlrafinanciar.toString(), ""

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_ANULARFINANCIABLEDEDEUDA",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public boolean existePeriodo(
        String compania,
        int ano,
        String periodo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA =>'", compania, "', ",
                               "UN_ANO      =>", Integer.toString(ano),
                               ", ", "UN_PERIODO  =>'", periodo, "'"

        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.FC_EXISTEPERIODO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void discriminarFinanciacion(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        BigDecimal valorabono,
        int nrocuotas,
        BigDecimal vrafinanciar,
        String usuario,
        long consecutivo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA        =>'", codigoruta, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ", "UN_PERIODO           =>'", periodo, "', ",
                               "UN_VALORABONO        =>", valorabono.toString(),
                               ", ", "UN_NROCUOTAS         =>",
                               Integer.toString(nrocuotas), ", ",
                               "UN_VRAFINANCIAR      =>",
                               vrafinanciar.toString(), ", ",
                               "UN_USUARIO           =>'", usuario, "', ",
                               "UN_CONSECUTIVO       =>",
                               Long.toString(consecutivo)

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.PR_DISCRIMINARFINANCIACION",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public boolean actualizaFinanciabledeDeuda(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        BigDecimal valorabono,
        BigDecimal vrafinanciar,
        int nrocuotas,
        String usuario,
        boolean sinabonoin,
        int pernocobro,
        long consecutivo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA        =>'", codigoruta, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ", "UN_PERIODO           =>'", periodo, "', ",
                               "UN_VALORABONO        =>", valorabono.toString(),
                               ", ", "UN_VRAFINANCIAR      =>",
                               vrafinanciar.toString(), ", ",
                               "UN_NROCUOTAS         =>",
                               Integer.toString(nrocuotas), ", ",
                               "UN_USUARIO           =>'", usuario, "', ",
                               "UN_SINABONOIN        =>",
                               sinabonoin ? "-1" : "0", ", ",
                               "UN_PERNOCOBRO        =>",
                               Integer.toString(pernocobro), ", ",
                               "UN_CONSECUTIVO       =>",
                               Long.toString(consecutivo)

        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM3.FC_ACTUALIZAFINANCIABLEDEUDA",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }
}