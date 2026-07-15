package com.sysman.precontractual.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.ejb.EjbPrecontractualUnoGeneralRemote;
import com.sysman.precontractual.ejb.EjbPrecontractualUnoLocal;
import com.sysman.precontractual.ejb.EjbPrecontractualUnoRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PrecontractualUno
 */
@Stateless
@LocalBean
public class EjbPrecontractualUno implements EjbPrecontractualUnoRemote,
                EjbPrecontractualUnoLocal {

    @EJB
    private EjbPrecontractualUnoGeneralRemote ejbPrecontractualUnoGeneral;

    /**
     * Default constructor.
     */
    public EjbPrecontractualUno() {
    }

    @Override
    public String actualizarFormulas(
        String compania,
        String campo,
        String valor,
        String tipocontrato,
        BigInteger consecutivo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CAMPO             =>'", campo, "', ",
                                "UN_VALOR             =>'", valor, "', ",
                                "UN_TIPOCONTRATO      =>'", tipocontrato, "', ",
                                "UN_CONSECUTIVO       =>",
                                consecutivo.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.FC_ACTUALIZARFORMULAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void copiarDatosEstudioPrevio(
        String compania,
        long codEstudio,
        String usuario,
        long consecutivo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODESTUDIO        =>",
                                Long.toString(codEstudio), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CONSECUTIVO       =>",
                                Long.toString(consecutivo)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_COPIARESTUDIOPREVIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void insertProponentesEtapas(
        String compania,
        String tipoContrato,
        String transaccion,
        long consecutivo,
        String proponente,
        String sucursal,
        boolean cotizaInventario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "',",
                                "UN_TIPOCONTRATO      =>'", tipoContrato, "', ",
                                "UN_TRANSACCION       =>'", transaccion, "', ",
                                "UN_CONSECUTIVO       =>",
                                Long.toString(consecutivo), ", ",
                                "UN_PROPONENTE        =>'", proponente, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_COTIZAINVENTARIO  =>",
                                cotizaInventario ? "-1" : "0", ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_PROPONENTE_ETAPAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean cambiarEstudioPrevio(
        String compania,
        String tipoContrato,
        long transaccion,
        BigInteger estudioPrevio,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCONTRATO      =>'", tipoContrato, "', ",
                                "UN_TRANSACCION       =>",
                                Long.toString(transaccion), ", ",
                                "UN_ESTUDIOPREVIO     =>",
                                estudioPrevio.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.FC_CAMBIARESTUDIOPREVIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarEstudioPrevio(
        String compania,
        String tipoContrato,
        long transaccion,
        BigInteger estudioPrevio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCONTRATO      =>'", tipoContrato, "', ",
                                "UN_TRANSACCION       =>",
                                Long.toString(transaccion), ", ",
                                "UN_ESTUDIOPREVIO     =>",
                                estudioPrevio.toString(), ",",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_ACTUALIZARESTUDIOPREVIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarInfoProponentes(
        String compania,
        String tipoContrato,
        long transaccion,
        int consecActual,
        int consecSiguiente,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCONTRATO      =>'", tipoContrato, "', ",
                                "UN_TRANSACCION       =>",
                                Long.toString(transaccion), ", ",
                                "UN_CONSECACTUAL      =>",
                                Integer.toString(consecActual), ", ",
                                "UN_CONSECSIGUIENTE   =>",
                                Integer.toString(consecSiguiente), ",",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_ACTUALIZARINFOPROPONENTES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void crearDetallesProceso(
        String compania,
        String tipoContrato,
        long transaccion,
        Date fechaInicio,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOCONTRATO      =>'", tipoContrato,
                                    "', ", "UN_TRANSACCION       =>",
                                    Long.toString(transaccion), ", ",
                                    "UN_FECHAINICIO       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicio),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRECONTRACTUAL1.PR_CREARDETALLESTX",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void subirCodigosUnspsc(
        String compania,
        String cambios,
        String usuario)
                    throws SystemException {
        ejbPrecontractualUnoGeneral.subirCodigosUnspsc(compania, cambios,
                        usuario);
    }

    @Override
    public void insertarAcuerdos(
        String compania,
        long nroEstudio,
        String tipoContrato,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NRO_ESTUDIO       =>",
                                Long.toString(nroEstudio), ", ",
                                "UN_TIPO_CONTRATO     =>'", tipoContrato, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_INSERTAR_ACUERDOSCOMERP3",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void crearRiesgoPorDefecto(
        String compania,
        int tRiesgo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_T_RIESGO          =>",
                                Integer.toString(tRiesgo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_CREARRIESGO_DEFECTO",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void cambiarNumeroCertificado(
        String compania,
        String usuario,
        long numeroCertificado,
        long numeroNuevo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA                     =>'", compania, "', ",
                                "UN_USUARIO                      =>'", usuario,  "', ",
                                "UN_NUMERO_CERTIFICADO    =>",
                                Long.toString(numeroCertificado), ", ",
                                "UN_NUMERO_NUEVO         =>",
                                Long.toString(numeroNuevo), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRECONTRACTUAL1.PR_CAMBIAR_NUM_CERT_INEXIST",
                        SysmanFunciones.concatenar(parametros));
    }

}