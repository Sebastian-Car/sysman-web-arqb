package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialSieteLocal;
import com.sysman.predial.ejb.EjbPredialSieteRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialSiete
 * 
 * @author eamaya
 * @version 2.0 , Refactoring concatenacion
 */
@Stateless
@LocalBean
public class EjbPredialSiete
                implements EjbPredialSieteRemote, EjbPredialSieteLocal {
    /**
     * Default constructor.
     */
    public EjbPredialSiete() {
    }

    @Override
    public boolean importarIgacMeses(
        String compania,
        int anio,
        String usuario,
        String numeroorden)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_NUMEROORDEN       =>'", numeroorden, "'"
        };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.FC_IMPORTAR_IGAC_MESES",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);

        return rta != 0;
    }

    @Override
    public String importarIgacTipoDos(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.FC_IMPORTAR_IGAC_TIPO_DOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void prepararTipoMes(
        String compania)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.PR_PREPARARTIPOMES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void prepararTipoDos(
        String compania)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'" + compania + "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.PR_PREPARARTIPODOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String actualizarNotificaPredial(
        String compania,
        String codigoinicial,
        String codigofinal,
        String orden,
        String usuario,
        String aniosdeuda,
        int deudas)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOINICIAL  =>'", codigoinicial, "', ",
                                "UN_CODIGOFINAL      =>'", codigofinal, "', ",
                                "UN_ORDEN             =>'", orden, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_ANIOSDEUDA        =>'", aniosdeuda, "', ",
                                "UN_DEUDAS       =>", Integer.toString(deudas)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.FC_ACTUALIZARNOTIFICAPREDIAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String eliminarAcuerdoDePago(
        String compania,
        String codigoacuerdo,
        String usuario,
        String codpredio,
        int preanioi,
        int preanio,
        String numeroorden,
        String concuotascanc,
        String eliminarcuotcanc,
        String aplicadscesp,
        String leydescesp,
        int vigenciasaldo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOACUERDO     =>'", codigoacuerdo,
                                "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_PREANIOI          =>",
                                Integer.toString(preanioi), ", ",
                                "UN_PREANIO           =>",
                                Integer.toString(preanio), ", ",
                                "UN_NUMEROORDEN       =>'", numeroorden, "', ",
                                "UN_CONCUOTASCANC     =>'", concuotascanc,
                                "', ",
                                "UN_ELIMINARCUOTCANC  =>'",
                                eliminarcuotcanc, "', ",
                                "UN_APLICADSCESP      =>'", aplicadscesp, "', ",
                                "UN_LEYDESCESP        =>'", leydescesp, "', ",
                                "UN_VIGENCIASALDO     =>",
                                Integer.toString(vigenciasaldo)
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.FC_ELIMINARACUERDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void insertarResolucionesIgacMes(
        String compania,
        String usuario,
        int factorDeMultiplicacion,
        long lineas,
        String lineaArchivo,
        int codigoPais)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_FACTORDEMULTIPLIC =>",
                                Integer.toString(factorDeMultiplicacion), ", ",
                                "UN_LINEAS            =>",
                                Long.toString(lineas), ", ",
                                "UN_LINEAARCHIVO      =>'", lineaArchivo, "', ",
                                "UN_CODIGOPAIS        =>",
                                Integer.toString(codigoPais), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.PR_RESOLUCIONES123",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean actualizarIgacMes(
        String compania,
        String plano,
        long longitudArchivo,
        int anio,
        String usuario,
        int codigoPais)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PLANO             =>", plano, ", ",
                                "UN_LONGITUDARCHIVO   =>",
                                Long.toString(longitudArchivo), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CODIGOPAIS        =>",
                                Integer.toString(codigoPais), "" };
        byte salida;
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.FC_ACTUALIZARIGACMES",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void anularCertificadoCatastral(
        String compania,
        String certificado,
        String usuario) throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CERTIFICADO       =>'", certificado, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.PR_ANULARCERT_CATASTRAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int prepararConceptoAnio(
        String compania,
        int aniofinal,
        int anioinicial,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(aniofinal), ", ",
                                "UN_ANIOINICIAL       =>",
                                Integer.toString(anioinicial), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM7.FC_PREPARAR_CONCEPTO_ANIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String actualizarIgacDos(
        String compania,
        String plano,
        long longitudArchivo,
        String departamento,
        String pais,
        String municipio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PLANO             => ", plano, " , ",
                                "UN_LONGITUDARCHIVO   =>",
                                Long.toString(longitudArchivo), ", ",
                                "UN_DEPARTAMENTO      =>'", departamento, "', ",
                                "UN_PAIS              =>'", pais, "', ",
                                "UN_MUNICIPIO         =>'", municipio, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PREDIAL_COM7.FC_ACTUALIZARIGACDOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}