package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbAlmacenCuatroGeneralRemote;
import com.sysman.almacen.ejb.EjbAlmacenCuatroLocal;
import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class AlmacenCuatro
 * 
 * @modifier amonroy, 10/06/2017 Implementacion de
 * SysmanFunciones.concatenar() para envio de parametros a funciones y
 * procedimientos
 */
@Stateless
@LocalBean
public class EjbAlmacenCuatro
                implements EjbAlmacenCuatroRemote, EjbAlmacenCuatroLocal {

    @EJB
    private EjbAlmacenCuatroGeneralRemote ejbAlmacenCuatroGeneral;

    /**
     * Default constructor.
     */
    public EjbAlmacenCuatro() {
    }

    @Override
    public long actualizarResponsable(
        String compania,
        String id,
        long seriePlaca,
        String codigoInventario,
        boolean predio)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_ID                 =>'", id, "', ",
                                "UN_SERIE_PLACA        =>",
                                Long.toString(seriePlaca), ", ",
                                "UN_CODIGO_INVENTARIO  =>'", codigoInventario,
                                "', ",
                                "UN_PREDIO             =>",
                                predio ? "-1" : "0" };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_ACTUALIZAR_RESPONSABLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public BigDecimal calcularVidaUtilRestante(
        String compania,
        long placa)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_PLACA              =>",
                                Long.toString(placa) };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_VIDAUTILRESTANTE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String calcularDepreciacionInicial(
        String compania,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA               =>'", compania,
                                    "', ",
                                    "UN_STRELEMENTOINICIAL   =>'",
                                    strelementoinicial, "', ",
                                    "UN_STRELEMENTOFINAL     =>'",
                                    strelementofinal, "', ",
                                    "UN_PLACAINICIAL         =>",
                                    Long.toString(placainicial), ", ",
                                    "UN_PLACAFINAL           =>",
                                    Long.toString(placafinal) };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM4.FC_CALDEPRECIARIIH",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String calcularDepreciacionInicialNiif(
        String compania,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA        =>'", compania,
                                    "', ",
                                    "UN_STRELEMENTOINICIAL   =>'",
                                    strelementoinicial, "', ",
                                    "UN_STRELEMENTOFINAL    =>'",
                                    strelementofinal, "', ",
                                    "UN_PLACAINICIAL        =>",
                                    Long.toString(placainicial), ", ",
                                    "UN_PLACAFINAL          =>",
                                    Long.toString(placafinal), "" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM4.FC_CALDEPRECIARIIH_NIIF",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String verificarCuentaAlmacen(
        String compania,
        Date fechaInicial,
        Date fechaFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA               =>'", compania,
                                    "', ", "UN_FECHA_INICIAL     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA_FINAL       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY')" };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM4.FC_VERIFICARCUENTAALMACEN",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String calcularDepreciacionMensual(
        String compania,
        int anoinicial,
        int mesinicial,
        int anofinal,
        int mesfinal,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA            =>'", compania,
                                    "', ",
                                    "UN_ANOINICIAL          =>",
                                    Integer.toString(anoinicial), ", ",
                                    "UN_MESINICIAL          =>",
                                    Integer.toString(mesinicial), ", ",
                                    "UN_ANOFINAL            =>",
                                    Integer.toString(anofinal), ", ",
                                    "UN_MESFINAL            =>",
                                    Integer.toString(mesfinal), ", ",
                                    "UN_STRELEMENTOINICIAL   =>'",
                                    strelementoinicial, "', ",
                                    "UN_STRELEMENTOFINAL    =>'",
                                    strelementofinal, "', ",
                                    "UN_PLACAINICIAL        =>",
                                    Long.toString(placainicial), ", ",
                                    "UN_PLACAFINAL          =>",
                                    Long.toString(placafinal), "" };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM4.FC_CALDEPRECIARH",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String calcularDepreciacionMensualNiif(
        String compania,
        int anoinicial,
        int mesinicial,
        int anofinal,
        int mesfinal,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA            =>'", compania,
                                    "', ",
                                    "UN_ANOINICIAL        =>",
                                    Integer.toString(anoinicial), ", ",
                                    "UN_MESINICIAL        =>",
                                    Integer.toString(mesinicial), ", ",
                                    "UN_ANOFINAL          =>",
                                    Integer.toString(anofinal), ", ",
                                    "UN_MESFINAL          =>",
                                    Integer.toString(mesfinal), ", ",
                                    "UN_STRELEMENTOINICIAL =>'",
                                    strelementoinicial, "', ",
                                    "UN_STRELEMENTOFINAL  =>'",
                                    strelementofinal, "', ",
                                    "UN_PLACAINICIAL      =>",
                                    Long.toString(placainicial), ", ",
                                    "UN_PLACAFINAL        =>",
                                    Long.toString(placafinal) };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM4.FC_CALDEPRECIARH_NIIF",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean evaluarDepreciacionesPosteriores(
        String compania,
        int ano,
        int mes,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA                =>'", compania,
                                "', ",
                                "UN_ANO              =>",
                                Integer.toString(ano), ", ",
                                "UN_MES              =>",
                                Integer.toString(mes), ", ",
                                "UN_ELEMENTOINICIAL  =>'", elementoinicial,
                                "', ", "UN_ELEMENTOFINAL    =>'",
                                elementofinal, "', ", "UN_PLACAINICIAL     =>",
                                Long.toString(placainicial), ", ",
                                "UN_PLACAFINAL       =>",
                                Long.toString(placafinal) };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_DEPRECIACIONES_POSTERIORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public long actResponsablePredioVias(
        String compania,
        boolean predio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_PREDIO           =>",
                                predio ? "-1" : "0" };

        return (long) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_AC_RESPONSABLE_PREDIOSVIAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public void registrarAdicionPredial(
        String compania,
        String seleccion,
        BigInteger serie,
        String elemento,
        Date fechamovimiento,
        BigInteger numeromovimiento,
        String tipomovimiento,
        String tipoinmueble,
        String valortotal,
        String ordena,
        int anioconstruccion,
        String sucursal,
        String descripcion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                    "UN_SELECCION         =>'", seleccion,
                                    "', ", "UN_SERIE             =>",
                                    serie.toString(), ", ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_FECHAMOVIMIENTO   =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechamovimiento),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NUMEROMOVIMIENTO  =>",
                                    numeromovimiento.toString(), ", ",
                                    "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                    "', ", "UN_TIPOINMUEBLE      =>'",
                                    tipoinmueble, "', ",
                                    "UN_VALORTOTAL        =>'", valortotal,
                                    "', ", "UN_ORDENA            =>'", ordena,
                                    "', ", "UN_ANIOCONSTRUCCION  =>",
                                    Integer.toString(anioconstruccion), ", ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_USUARIO          =>'", usuario,
                                    "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN_COM4.PR_REGISTRARADICIONPREDIAL",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal actualizarDesdeExcel(
        String compania,
        String plano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_PLANO             =>", plano, ", ",
                                "UN_USUARIO       =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_ACTUALIZARDESDEEXCEL",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void cambiarNumeroMovimiento(
        String compania,
        String usuario,
        String tipoMovimiento,
        long numeroMovimiento,
        long numeroNuevo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA             =>'", compania, "', ",
                                "UN_USUARIO              =>'", usuario, "', ",
                                "UN_TIPO_MOVIMIENTO      =>'", tipoMovimiento,
                                "', ", "UN_NUMERO_MOVIMIENTO    =>",
                                Long.toString(numeroMovimiento), ", ",
                                "UN_NUMERO_NUEVO         =>",
                                Long.toString(numeroNuevo), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.PR_CAMBIAR_NUMERO_MOVIMIENTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean tieneMovimientosSinRegistrar(
        String compania,
        String tipoMovimiento,
        long numeroMovimiento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA             =>'", compania, "', ",
                                "UN_TIPO_MOVIMIENTO      =>'", tipoMovimiento,
                                "', ",
                                "UN_NUMERO_MOVIMIENTO    =>",
                                Long.toString(numeroMovimiento) };

        byte salida;
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_TIENE_DETMOV_SIN_REGISTRAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String traerTipoFormato(
        String compania,
        String tipoMovimiento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO_MOVIMIENTO   =>'", tipoMovimiento,
                                "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_TRAER_TIPO_FORMATO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String traerNombreDocumentoAsociado(
        String compania,
        String codDocumento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_COD_DOCUMENTO     =>'", codDocumento, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_TRAER_NOMBRE_DOCASOCIADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public int actualizarResponsablePredioVias(
        String compania,
        boolean predio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PREDIO            =>", predio ? "-1" : "0",
                                ", ", "UN_USUARIO           =>'", usuario,
                                "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_AC_RESPONSABLE_PREDIOSVIAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int actualizarVidaUtilR(
        String compania,
        boolean predio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PREDIO            =>", predio ? "-1" : "0",
                                ", ", "UN_USUARIO           =>'", usuario,
                                "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_ACTUALIZAR_VIDAUTILR",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String generarNumeroRequisiciones(
        String compania,
        String tipoMovimiento,
        long movimientoInicial,
        long movimientoFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOMOVIMIENTO    =>'", tipoMovimiento,
                                "', ", "UN_MOVIMIENTOINICIAL =>",
                                Long.toString(movimientoInicial), ", ",
                                "UN_MOVIMIENTOFINAL   =>",
                                Long.toString(movimientoFinal)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_NUMREQUISICIONES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void subirInventarioInicial(
        String compania,
        String inventarios,
        String dependencias,
        String responsables,
        String ordenescompra,
        String claseorden,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_INVENTARIOS       => ", inventarios, " , ",
                                "UN_DEPENDENCIAS      => ", dependencias, " , ",
                                "UN_RESPONSABLES      => ", responsables, " , ",
                                "UN_ORDENESCOMPRA     => ", ordenescompra,
                                " , ", "UN_CLASEORDEN        =>'", claseorden,
                                "', ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.PR_SUBIR_INVENTARIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String generarMovInventarioInicial(
        String compania,
        String claseorden,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.FC_MOVINVENTARIOINICIAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public int validarRequisicion(
        String compania,
        String dependencia,
        String tercero,
        int ano,
        String sucursal,
        String accion,
        String nuevoValorEstimado,
        String numero)
                    throws SystemException {
        return ejbAlmacenCuatroGeneral.validarRequisicion(compania, dependencia,
                        tercero, ano, sucursal, accion, nuevoValorEstimado,
                        numero);
    }

    @Override
    public void actualizarIdentificadoresPlaca(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.PR_ACTUALIZAR_PLACAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void subirCambioPlaca(
        String compania,
        String cambios,
        String usuario,
        long cambio)
                    throws SystemException {

        ejbAlmacenCuatroGeneral.subirCambioPlaca(compania, cambios, usuario,
                        cambio);
    }

    @Override
    public void cambiarPlacas(
        String compania,
        String usuario,
        long cambio,
        int opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CAMBIO            =>",
                                Long.toString(cambio), ",",
                                "UN_OPCION            =>  ",
                                String.valueOf(opcion)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN_COM4.PR_CAMBIOS_DE_PLACA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String validarInventarioInicial(String compania, String inventarios,
        String dependencias, String responsables, String ordenescompra)
                    throws SystemException {

        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_INVENTARIOS       => ", inventarios,
                                    " , ",
                                    "UN_DEPENDENCIAS      => ", dependencias,
                                    " , ",
                                    "UN_RESPONSABLES      => ", responsables,
                                    " , ",
                                    "UN_ORDENESCOMPRA     => ", ordenescompra };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_ALMACEN_COM4.FC_VALIDARINVENTARIO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }

    }

}