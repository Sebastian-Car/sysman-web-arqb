package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbAlmacenTresLocal;
import com.sysman.almacen.ejb.EjbAlmacenTresRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class AlmacenTres
 * 
 * @author eamaya
 * @version 2.0 , Refactoring concatenacion
 */
@Stateless
@LocalBean
public class EjbAlmacenTres
                implements EjbAlmacenTresRemote, EjbAlmacenTresLocal {
    /**
     * Default constructor.
     */
    public EjbAlmacenTres() {
    }

    @Override
    public String analizarInconsistenciasKardex(
        String compania,
        String tipomovimiento,
        long movimiento,
        String elemento,
        long codigo,
        String actpromedio,
        int ano,
        int mes)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                    "', ", "UN_MOVIMIENTO        =>",
                                    Long.toString(movimiento), ", ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_CODIGO            =>",
                                    Long.toString(codigo), ", ",
                                    "UN_ACTPROMEDIO       =>'", actpromedio,
                                    "', ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), "" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM3.FC_KARDEXELEMENTO",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String obtenerInconsistenciasKardex(
        String compania,
        int intanoinicial,
        int intmesinicial,
        int intanofinal,
        int intmesfinal,
        String strelementoinicial,
        String strelementofinal,
        int kardexgeneral,
        Date fechaInicial,
        Date fechaFinal,
        String tipoMov,
        long movimiento)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_INTANOINICIAL     =>",
                                    Integer.toString(intanoinicial), ", ",
                                    "UN_INTMESINICIAL     =>",
                                    Integer.toString(intmesinicial),
                                    ", ",
                                    "UN_INTANOFINAL       =>",
                                    Integer.toString(intanofinal), ", ",
                                    "UN_INTMESFINAL       =>",
                                    Integer.toString(intmesfinal), ", ",
                                    "UN_STRELEMENTOINICIAL =>'",
                                    strelementoinicial, "', ",
                                    "UN_STRELEMENTOFINAL  =>'",
                                    strelementofinal, "', ",
                                    "UN_KARDEXGENERAL     =>",
                                    Integer.toString(kardexgeneral), ", ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPOMOVIMIENTO    =>'", tipoMov, "', ",
                                    "UN_MOVIMIENTO        =>", Long.toString(movimiento)
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String obtenerNumeroRequisiciones(
        String compania,
        String tipoorden,
        long numorden)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOORDEN         =>'", tipoorden,
                                    "', ", "UN_NUMORDEN          =>",
                                    Long.toString(numorden), "" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM3.FC_NUMREQUISICIONES",
                            SysmanFunciones.concatenar(
                                            parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean revisarHoras(
        String compania,
        String elementoini,
        String elementofin,
        long placainicial,
        long placafinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ELEMENTOINI       =>'", elementoini, "', ",
                                "UN_ELEMENTOFIN       =>'", elementofin, "', ",
                                "UN_PLACAINICIAL      =>",
                                Long.toString(placainicial), ", ",
                                "UN_PLACAFINAL        =>",
                                Long.toString(placafinal), "" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.FC_REVISAHORAS",
                        SysmanFunciones.concatenar(
                                        parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void revisarHoraH(
        String compania)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.PR_REVISAHORAH",
                        SysmanFunciones.concatenar(
                                        parametros));
    }

    @Override
    public String obtenerCedulaResponsable(
        String compania)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.FC_CEDULARESPONSABLEH",
                        SysmanFunciones.concatenar(
                                        parametros),
                        Types.VARCHAR);
    }

    @Override
    public void revisarFechasSalidaServicio(
        String compania,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                "', ", "UN_ELEMENTOFINAL     =>'",
                                elementofinal, "', ", "UN_PLACAINICIAL      =>",
                                Long.toString(placainicial), ", ",
                                "UN_PLACAFINAL        =>",
                                Long.toString(placafinal), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.PR_REVISARFECHASSALIDASERVICIO",
                        SysmanFunciones.concatenar(
                                        parametros));
    }

    @Override
    public String revisarRegistroDepreciacion(
        String compania,
        int anioinicial,
        int mesinicial,
        int aniofinal,
        int mesfinal,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioinicial), ", ",
                                    "UN_MESINICIAL        =>",
                                    Integer.toString(mesinicial), ", ",
                                    "UN_ANIOFINAL         =>",
                                    Integer.toString(aniofinal), ", ",
                                    "UN_MESFINAL          =>",
                                    Integer.toString(mesfinal), ", ",
                                    "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                    "', ", "UN_ELEMENTOFINAL     =>'",
                                    elementofinal, "', ",
                                    "UN_PLACAINICIAL      =>",
                                    Long.toString(placainicial), ", ",
                                    "UN_PLACAFINAL        =>",
                                    Long.toString(placafinal), "" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM3.FC_REVISAREGISTRODEPRECIACION",
                            SysmanFunciones.concatenar(
                                            parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String revisarPlacasSinMovimiento(
        String compania,
        int anioinicial,
        int mesinicial,
        int aniofinal,
        int mesfinal,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioinicial), ", ",
                                    "UN_MESINICIAL        =>",
                                    Integer.toString(mesinicial), ", ",
                                    "UN_ANIOFINAL         =>",
                                    Integer.toString(aniofinal), ", ",
                                    "UN_MESFINAL          =>",
                                    Integer.toString(mesfinal), ", ",
                                    "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                    "', ", "UN_ELEMENTOFINAL     =>'",
                                    elementofinal, "', ",
                                    "UN_PLACAINICIAL      =>",
                                    Long.toString(placainicial), ", ",
                                    "UN_PLACAFINAL        =>",
                                    Long.toString(placafinal), "" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM3.FC_REVISAPLACASSINMOVIMIENTOH",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String revisarDevolutivos(
        String compania,
        int anioinicial,
        int mesinicial,
        String elementoinicial,
        long serieinicial,
        int aniofinal,
        int mesfinal,
        String elementofinal,
        long seriefinal,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioinicial), ", ",
                                    "UN_MESINICIAL        =>",
                                    Integer.toString(mesinicial), ", ",
                                    "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                    "', ", "UN_SERIEINICIAL      =>",
                                    Long.toString(serieinicial), ", ",
                                    "UN_ANIOFINAL         =>",
                                    Integer.toString(aniofinal), ", ",
                                    "UN_MESFINAL          =>",
                                    Integer.toString(mesfinal), ", ",
                                    "UN_ELEMENTOFINAL     =>'", elementofinal,
                                    "', ", "UN_SERIEFINAL        =>",
                                    Long.toString(seriefinal), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM3.FC_REVISADEVOLUTIVOSH",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void rectificarDevolutivos(
        String compania,
        long placainicial,
        long placafinal)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PLACAINICIAL      =>",
                                Long.toString(placainicial), ", ",
                                "UN_PLACAFINAL        =>",
                                Long.toString(placafinal), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.PR_RECTIFICARDEVOLUTIVOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarValor(
        String compania,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                "', ", "UN_ELEMENTOFINAL     =>'",
                                elementofinal, "', ", "UN_PLACAINICIAL      =>",
                                Long.toString(placainicial), ", ",
                                "UN_PLACAFINAL        =>",
                                Long.toString(placafinal), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.PR_ACTUALIZARVLR",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarDependencia(
        String compania,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal,
        int mesfinal,
        int aniofinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                "', ", "UN_ELEMENTOFINAL     =>'",
                                elementofinal, "', ", "UN_PLACAINICIAL      =>",
                                Long.toString(placainicial), ", ",
                                "UN_PLACAFINAL        =>",
                                Long.toString(placafinal), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesfinal), ", ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(aniofinal), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM3.PR_ACTUALIZADEPENDENCIA",
                        SysmanFunciones.concatenar(parametros));
    }
}