package com.sysman.plusvalia.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroLocal;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PlusvaliaCero
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPlusvaliaCero
                implements EjbPlusvaliaCeroRemote, EjbPlusvaliaCeroLocal {
    /**
     * Default constructor.
     */
    public EjbPlusvaliaCero() {
    }

    @Override
    public void plusvaliaCargarPlantilla(
        String cargarPlantilla,
        String compania,
        int proyecto,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_CARGAR_PLANTILLA  =>", cargarPlantilla,
                                ", ",
                                "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO          =>",
                                Integer.toString(proyecto), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLUSVALIA.PR_PLUSVALIA_CARGAR_PLANTILLA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String procesoFacturacionCopia(String compania,
        long idProyecto,
        long beneficiarioInicial,
        long beneficiarioFinal,
        BigInteger numeroActo,
        int reclasificar,
        long proceso, String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID_PROYECTO       =>",
                                Long.toString(idProyecto), ", ",
                                "UN_BENEFICIARIO_INICIAL =>",
                                Long.toString(beneficiarioInicial), ", ",
                                "UN_BENEFICIARIO_FINAL =>",
                                Long.toString(beneficiarioFinal),
                                ", ", "UN_NUMERO_ACTO       =>",
                                numeroActo.toString(), ", ",
                                "UN_RECLASIFICAR      =>",
                                Integer.toString(reclasificar),
                                ", ", "UN_PROCESO           =>",
                                Long.toString(proceso), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLUSVALIA.FC_PROCESO_FACTURACION",
                        SysmanFunciones.concatenar(parametros), Types.VARCHAR);
    }

    @Override
    public String calculoPlusvalia(
        String compania,
        long idProyecto,
        long beneficiario,
        int reclasificar,
        long proceso,
        String usuario)
                    throws SystemException {
        String[] parametros =  {
        		          "UN_COMPANIA          =>'" , compania , "', "
                        , "UN_ID_PROYECTO       =>" , Long.toString(idProyecto)   ,", "
                        , "UN_BENEFICIARIO      =>" , Long.toString(beneficiario)   ,", "
                        , "UN_RECLASIFICAR      =>" , Integer.toString(reclasificar) , ", "
                        , "UN_PROCESO           =>" , Long.toString(proceso)   ,", "
                        , "UN_USUARIO           =>'" , usuario , "'"
       };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLUSVALIA.FC_CALCULO_PLUSVALIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal calcularAcuerdos(
        String compania,
        long idProyecto,
        long beneficiario,
        int acuerdo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID_PROYECTO       =>",
                                Long.toString(idProyecto), ", ",
                                "UN_BENEFICIARIO      =>",
                                Long.toString(beneficiario), ", ",
                                "UN_ACUERDO           =>",
                                Integer.toString(acuerdo)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLUSVALIA.FC_CALCULO_ACUERDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String procesoFacturacion(
        String compania,
        long idProyecto,
        long beneficiarioInicial,
        long beneficiarioFinal,
        int etapa,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID_PROYECTO       =>",
                                Long.toString(idProyecto), ", ",
                                "UN_BENEFICIARIO_INICIAL =>",
                                Long.toString(beneficiarioInicial), ", ",
                                "UN_BENEFICIARIO_FINAL =>",
                                Long.toString(beneficiarioFinal), ", ",
                                "UN_ETAPA             =>",
                                Integer.toString(etapa), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLUSVALIA_CAL.FC_CALCULAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    // @Override
    // public BigDecimal crearAcuerdo(
    // String compania,
    // long idProyecto,
    // long idFactura,
    // long idBeneficiario,
    // BigDecimal cuotainicial,
    // int numerocuota,
    // BigDecimal interesacuerdo,
    // String resolucion,
    // String modelointeresdeuda,
    // String usuario)
    // throws SystemException {
    // String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
    // "UN_ID_PROYECTO =>",
    // Long.toString(idProyecto), ", ",
    // "UN_ID_FACTURA =>",
    // Long.toString(idFactura), ", ",
    // "UN_ID_BENEFICIARIO =>",
    // Long.toString(idBeneficiario), ", ",
    // "UN_CUOTAINICIAL =>",
    // cuotainicial.toString(), ", ",
    // "UN_NUMEROCUOTA =>",
    // Integer.toString(numerocuota), ", ",
    // "UN_INTERESACUERDO =>",
    // interesacuerdo.toString(), ", ",
    // "UN_RESOLUCION =>'", resolucion, "', ",
    // "UN_MODELOINTERESDEUDA =>'", modelointeresdeuda,
    // "', ", "UN_USUARIO =>'", usuario, "' "
    // };
    // return (BigDecimal) AccionesImp.ejecutarFuncion(
    // ConectorPool.ESQUEMA_SYSMAN,
    // "PCK_PLUSVALIA_CAL.FC_PREPARARACUERDO",
    // SysmanFunciones.concatenar(parametros),
    // Types.DECIMAL);
    // }

    @Override
    public String crearAcuerdo(
        String compania,
        long idProyecto,
        long idFactura,
        long idBeneficiario,
        BigDecimal cuotainicial,
        int numerocuota,
        BigDecimal interesacuerdo,
        String resolucion,
        String modelointeresdeuda,
        String usuario,
        boolean preliquidar)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID_PROYECTO        =>",
                                Long.toString(idProyecto), ", ",
                                "UN_ID_FACTURA         =>",
                                Long.toString(idFactura), ", ",
                                "UN_ID_BENEFICIARIO    =>",
                                Long.toString(idBeneficiario), ", ",
                                "UN_CUOTAINICIAL       =>",
                                cuotainicial.toString(), ", ",
                                "UN_NUMEROCUOTA        =>",
                                Integer.toString(numerocuota), ", ",
                                "UN_INTERESACUERDO     =>",
                                interesacuerdo.toString(), ", ",
                                "UN_RESOLUCION         =>'", resolucion, "', ",
                                "UN_MODELOINTERESDEUDA =>'", modelointeresdeuda,
                                "', ", "UN_USUARIO            =>'", usuario,
                                "', ", "UN_PRELIQUIDAR        =>",
                                (preliquidar ? "-1" : "0"), ""
        };
        try {
            return Acciones
                            .clobToString((Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PLUSVALIA_CAL.FC_PREPARARACUERDO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));

        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal prorrateoGeneral(
        long acuerdo,
        int cuotaFinal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_ACUERDO           =>",
                                Long.toString(acuerdo), ", ",
                                "UN_CUOTA_FINAL       =>",
                                Integer.toString(cuotaFinal), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ACUERDO_GEN.FC_FACTURARCUOTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

}