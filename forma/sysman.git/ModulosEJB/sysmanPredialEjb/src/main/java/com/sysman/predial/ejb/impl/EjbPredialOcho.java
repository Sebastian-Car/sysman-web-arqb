/*-
 * EjbPredialOcho.java
 *
 * 1.0
 * 
 * 27/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialOchoLocal;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
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
 * Session Bean implementation class PredialOcho
 */
@Stateless
@LocalBean
public class EjbPredialOcho
                implements EjbPredialOchoRemote, EjbPredialOchoLocal {
    /**
     * Default constructor.
     */
    public EjbPredialOcho() {
        // Constructor vacio
    }

    @Override
    public boolean anularPrescripcion(
        String compania,
        String codigo,
        String numeroOrden,
        Date fechaAnterior,
        String resolucionAnt,
        String usuario,
        String observacion,
        String resolucion,
        int prescripcion)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODIGO            =>'", codigo, "', ",
                                    "UN_NUMERO_ORDEN      =>'", numeroOrden,
                                    "', ", "UN_FECHAANTERIOR     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaAnterior),
                                    "','DD/MM/YYYY'), ",
                                    "UN_RESOLUCIONANT     =>'", resolucionAnt,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "', ", "UN_OBSERVACION       =>'",
                                    observacion, "', ",
                                    "UN_RESOLUCION        =>'", resolucion,
                                    "', ", "UN_PRESCRIPCION      =>",
                                    Integer.toString(prescripcion), "" };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_ANULAR_PRESCRIPCION",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean insertarRegistroIgacUno(
        String compania,
        String cadena,
        int anoFecha,
        String usuario,
        String codigoPais,
        String departamento,
        String municipio,
        boolean indTotal,
        String nombreCompania)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CADENA            =>", cadena, ", ",
                                "UN_ANOFECHA          =>",
                                Integer.toString(anoFecha), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CODIGOPAIS        =>'", codigoPais, "', ",
                                "UN_DEPARTAMENTO      =>'", departamento, "', ",
                                "UN_MUNICIPIO         =>'", municipio, "', ",
                                "UN_IND_TOTAL         =>",
                                indTotal ? "-1" : "0", ", ",
                                "UN_NOMBRECOMPANIA    =>'", nombreCompania,
                                "' " };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_INSERTAREGISTROIGACUNO",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public int actualizarAporteRecibo(
        String compania,
        String usuario,
        String concepto,
        BigDecimal valorAporte,
        BigDecimal preval,
        BigInteger proyecto,
        String numeroOrden,
        String factura,
        boolean indAporte)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CONCEPTO          =>'", concepto, "', ",
                                "UN_VALORAPORTE       =>",
                                String.valueOf(valorAporte), ", ",
                                "UN_PREVAL            =>",
                                String.valueOf(preval), ", ",
                                "UN_PROYECTO          =>",
                                String.valueOf(proyecto), ", ",
                                "UN_NUMEROORDEN       =>'", numeroOrden, "', ",
                                "UN_FACTURA           =>'", factura, "', ",
                                "UN_INDAPORTE         =>",
                                indAporte ? "-1" : "0", "" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_ACTUALIZARAPORTERECIBO",
                        SysmanFunciones.concatenar(parametros), Types.INTEGER);
    }

    @Override
    public boolean validarConfigTarifas(
        boolean indTipoEstrato,
        String tipoInicial,
        String tipoFinal,
        boolean indEstrato,
        String estratoInicial,
        String estratoFinal)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_IND_TIPOESTRATO   =>",
                                indTipoEstrato ? "-1" : "0", ", ",
                                "UN_TIPO_INICIAL      =>'", tipoInicial, "', ",
                                "UN_TIPO_FINAL        =>'", tipoFinal, "', ",
                                "UN_IND_ESTRATO       =>",
                                indEstrato ? "-1" : "0", ", ",
                                "UN_ESTRATO_INICIAL   =>'", estratoInicial,
                                "', ", "UN_ESTRATO_FINAL     =>'", estratoFinal,
                                "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_VALIDA_CFG_TARIFAS",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String registrarCertificado(
        String compania,
        String formulario,
        String predio,
        String direccion,
        String nombre,
        String cedula,
        String numOrden,
        int valor,
        String sucursal,
        String usuario,
        String recibo,
        String destino,
        String banco,
        Date fecha,
        int anio)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FORMULARIO        =>'", formulario,
                                    "', ", "UN_PREDIO            =>'", predio,
                                    "', ", "UN_DIRECCION         =>'",
                                    direccion, "', ",
                                    "UN_NOMBRE            =>'", nombre, "', ",
                                    "UN_CEDULA            =>'", cedula, "', ",
                                    "UN_NUMORDEN          =>'", numOrden, "', ",
                                    "UN_VALOR             =>'",
                                    Integer.toString(valor), "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_RECIBO            =>'", recibo, "', ",
                                    "UN_DESTINO           =>'", destino, "', ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_FECHA             =>",
                                    fecha != null ? "TO_DATE('"
                                        + SysmanFunciones.convertirAFechaCadena(
                                                        fecha)
                                        + "','DD/MM/YYYY') " : null,
                                    ", ", "UN_ANIO              =>",
                                    Integer.toString(anio), "" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_REGISTRAR_CERTIFICADO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean aplicarTodasCfgTarifas(
        String compania,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_APLICARTODAS_CFGTARIFAS",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean aplicarConfigTarifa(
        String compania,
        BigInteger id,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID                =>", String.valueOf(id),
                                ", ", "UN_USUARIO           =>'", usuario,
                                "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_APLICAR_CFGTARIFA",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void exencionDeInteresPorDuplicidad(
        String compania,
        String numeroOrden,
        String predio,
        int anoInicial,
        int anoFinal,
        String resolucion,
        String elaboradoPor,
        String firmadoPor,
        String fechaResolucion,
        boolean tipoExencion,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_NUMEROORDEN       =>'", numeroOrden, "', ",
                               "UN_PREDIO            =>'", predio, "', ",
                               "UN_ANOINICIAL        =>",
                               Integer.toString(anoInicial), ", ",
                               "UN_ANOFINAL          =>",
                               Integer.toString(anoFinal), ", ",
                               "UN_RESOLUCION        =>'", resolucion, "', ",
                               "UN_ELABORADOPOR      =>'", elaboradoPor, "', ",
                               "UN_FIRMADOPOR        =>'", firmadoPor, "', ",
                               "UN_FECHARESOLUCION   =>'", fechaResolucion,
                               "', ", "UN_TIPOEXENCION      =>",
                               tipoExencion ? "-1" : "0", ",",
                               "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.PR_EXINTXDUPLICIDADCAT",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public int prepararPeriodo(
        String compania,
        String usuario,
        int ano,
        int mes,
        String periodoBase,
        String numeroOrden)
                    throws SystemException {
        String[] parametro = {
                               "UN_COMPANIA          =>'", compania, "', ",
                               "UN_USUARIO           =>'", usuario, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ", "UN_MES               =>",
                               Integer.toString(mes), ", ",
                               "UN_PERIODO_BASE      =>'", periodoBase, "', ",
                               "UN_NUMERO_ORDEN      =>'", numeroOrden, "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_PREPARAR_PERIODO",
                        SysmanFunciones.concatenar(parametro), Types.INTEGER);
    }

    @Override
    public void registrarPagosWeb(
        String compania,
        String referencia,
        String predio,
        String numeroOrden,
        String pys,
        String inscrito,
        String nitInscrito,
        String usuario,
        String eliminar)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_REFERENCIA        =>'", referencia, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_NUMEROORDEN       =>'", numeroOrden, "', ",
                                "UN_PYS               =>'", pys, "', ",
                                "UN_INSCRITO          =>'", inscrito, "', ",
                                "UN_NITINSCRITO       =>'", nitInscrito, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_ELIMINAR          =>'", eliminar, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.PR_REGISTRARPAGOSWEB",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long actualizarIncrAutoavaluo(
        String compania,
        String numeroOrden,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMEROORDEN       =>'", numeroOrden, "', ",
                                "UN_USUARIO           =>'", usuario, "' " };
        return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_ACTUALIZAR_INCR_AUTOAVALUO",
                        SysmanFunciones.concatenar(parametros), Types.BIGINT);
    }

    @Override
    public boolean generarAutoavaluo(
        String compania,
        String numeroOrden,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_GENERAR_AUTOAVALUO",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public int registrarPagosDobles(
        String usuario,
        String compania,
        String numeroOrden,
        String codigo,
        String factura,
        String pagodBanco,
        String pagodPaquete,
        int pagodAno,
        Date pagodFecha)
                    throws SystemException {
        try {
            String[] parametros = { "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NUMERO_ORDEN      =>'", numeroOrden,
                                    "', ", "UN_CODIGO            =>'", codigo,
                                    "', ", "UN_FACTURA           =>'", factura,
                                    "', ", "UN_PAGOD_BANCO       =>'",
                                    pagodBanco, "', ",
                                    "UN_PAGOD_PAQUETE     =>'", pagodPaquete,
                                    "', ", "UN_PAGOD_ANO         =>",
                                    Integer.toString(pagodAno), ", ",
                                    "UN_PAGOD_FECHA       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    pagodFecha),
                                    "','DD/MM/YYYY')" };
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_REGISTRAR_PAGOSDOBLES",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean actualizarIndicadorActivo(
        String compania,
        String tipo,
        boolean indActivo,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_IND_ACTIVO        =>",
                                indActivo ? "-1" : "0", ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_ACTUALIZAINDACTIVO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean validarConsecutivo(
        String compania,
        BigInteger secuencia,
        String consecutivo,
        String tipo)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_SECUENCIA         =>",
                                String.valueOf(secuencia), ", ",
                                "UN_CONSECUTIVO       =>'", consecutivo, "', ",
                                "UN_TIPO              =>'", tipo, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_VALIDARCONSECUTIVO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String obtenerEncabezadoColumna(
        String compania,
        String nit,
        int codigo,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NIT               =>'", nit, "', ",
                                "UN_CODIGO            =>",
                                Integer.toString(codigo), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_ENCABEZADO_COLUMNA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String prepararListadoGeneral(
        String compania,
        boolean clave,
        String palabraClave,
        boolean nombres,
        String nombreInicial,
        String nombreFinal,
        String direccionInicial,
        String direccionFinal,
        boolean indicador,
        boolean soloPredios,
        int ordenado)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CLAVE             =>",
                                    clave ? "-1" : "0", ", ",
                                    "UN_PALABRACLAVE      =>",
                                    palabraClave != null ? palabraClave : null,
                                    ", ", "UN_NOMBRES           =>",
                                    nombres ? "-1" : "0", ", ",
                                    "UN_NOMBREINICIAL     =>",
                                    nombreInicial != null ? nombreInicial
                                        : null,
                                    ", ", "UN_NOMBREFINAL       =>",
                                    nombreFinal != null ? nombreFinal : null,
                                    ", ",
                                    "UN_DIRECCIONINICIAL  =>",
                                    direccionInicial != null ? direccionInicial
                                        : null,
                                    ", ",
                                    "UN_DIRECCIONFINAL    =>",
                                    direccionFinal != null ? direccionFinal
                                        : null,
                                    ", ", "UN_INDICADOR         =>",
                                    indicador ? "-1" : "0", ", ",
                                    "UN_SOLOPREDIOS       =>",
                                    soloPredios ? "-1" : "0", ", ",
                                    "UN_ORDENADO          =>",
                                    Integer.toString(ordenado), "" };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_PREPARARLISTADOGRAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generaPlanoFacturacion(
        String compania,
        String codigoInicial,
        String codigoFinal,
        String nombreInicial,
        String nombreFinal,
        int anioInicial,
        int anioFinal,
        int hastaAnio,
        String nitInicial,
        String nitFinal,
        String numeroOrden,
        String orden,
        BigDecimal valorInferior,
        BigDecimal valorSuperior)
                    throws SystemException {
        try {
            String[] parametros = {
                                    "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODIGOINICIAL     =>'", codigoInicial,
                                    "', ", "UN_CODIGOFINAL       =>'",
                                    codigoFinal, "', ",
                                    "UN_NOMBREINICIAL     =>'", nombreInicial,
                                    "', ", "UN_NOMBREFINAL       =>'",
                                    nombreFinal, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioInicial), ", ",
                                    "UN_ANIOFINAL         =>",
                                    Integer.toString(anioFinal), ", ",
                                    "UN_HASTAANIO         =>",
                                    Integer.toString(hastaAnio), ", ",
                                    "UN_NITINICIAL        =>'", nitInicial,
                                    "', ", "UN_NITFINAL          =>'", nitFinal,
                                    "', ", "UN_NUMERO_ORDEN      =>'",
                                    numeroOrden, "', ",
                                    "UN_ORDEN             =>'",
                                    orden, "', ",
                                    "UN_VALORINFERIOR     =>",
                                    valorInferior.toString(), ", ",
                                    "UN_VALORSUPERIOR     =>",
                                    valorSuperior.toString(), "" };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_PREDIAL_COM8.FC_GENERAPLANOFACTURACION",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean agregarNuevoCodigoRuta(
        String compania,
        BigInteger numero,
        String claseSolicitud,
        BigInteger numeroNuevo,
        String codigoRutaNuevo,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_CLASESOLICITUD    =>'",
                                claseSolicitud, "', ",
                                "UN_NUMERONUEVO       =>",
                                numeroNuevo.toString(), ", ",
                                "UN_CODIGORUTANUEVO   =>'", codigoRutaNuevo,
                                "', ", "UN_USUARIO           =>'", usuario,
                                "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_AGREGARNUEVOCODIGORUTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal obtenerValorDescuentoEspecial(
        String compania,
        String predio,
        String numeroOrden)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PREDIO            =>'", predio, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_VALORDESCUENTOESPECIAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal obtenerValorPorcDctoEspecial(
        String compania,
        String predio,
        Date fechaEv)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_PREDIO            =>'", predio, "', ",
                                    "UN_FECHA_EV          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaEv),
                                    "','DD/MM/YYYY')" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_PORC_DESC_LOTES",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String obtenerMsjeDesctoEspecial(
        String compania,
        String predio,
        Date fechaEv)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_PREDIO            =>'", predio, "', ",
                                    "UN_FECHA_EV          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaEv),
                                    "','DD/MM/YYYY')" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_MJSDSCTOESP",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoFacturacionAsoBanNoventaOcho(
        String compania,
        String codigoInicial,
        String codigoFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODIGOINICIAL     =>'", codigoInicial,
                                    "', ", "UN_CODIGOFINAL       =>'",
                                    codigoFinal, "'" };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_GENERAPLANOFACTURACIONASO98",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoPuntos(
        String compania,
        String numeroOrden,
        String codigoInicial,
        String codigoFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NUMERO_ORDEN      =>'", numeroOrden,
                                    "', ", "UN_CODIGOINICIAL     =>'",
                                    codigoInicial, "', ",
                                    "UN_CODIGOFINAL       =>'", codigoFinal,
                                    "'" };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_GENERAPLANOPUNTOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String consultarNombreCopropietario(
        String compania,
        String codigoPred,
        String numeroOrden)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOPRED        =>'", codigoPred, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_NOMBRECOPROPIETARIO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String consultarAniosPagos(
        String compania,
        String codigo,
        String numeroOrden)
                    throws SystemException {
        String[] parametros = {
                                "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_ANIOS_PAGOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public long calcularUsuariosFacturaEnLote(
        String compania,
        String numeroOrden,
        String codigoInicial,
        String codigoFinal,
        String direccionInicial,
        String direccionFinal,
        String nombreInicial,
        String nombreFinal,
        String nitInicial,
        String nitFinal,
        int anioInicial,
        int anioFinal,
        BigDecimal valorInferior,
        BigDecimal valorSuperior,
        int hastaAno,
        String tipoPredio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_CODIGOINICIAL     =>'", codigoInicial,
                                "', ", "UN_CODIGOFINAL       =>'", codigoFinal,
                                "', ", "UN_DIRECCIONINICIAL  =>'",
                                direccionInicial, "', ",
                                "UN_DIRECCIONFINAL    =>'", direccionFinal,
                                "', ", "UN_NOMBREINICIAL     =>'",
                                nombreInicial, "', ",
                                "UN_NOMBREFINAL       =>'", nombreFinal, "', ",
                                "UN_NITINICIAL        =>'", nitInicial, "', ",
                                "UN_NITFINAL          =>'", nitFinal, "', ",
                                "UN_ANIOINICIAL       =>",
                                Integer.toString(anioInicial), ", ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(anioFinal), ", ",
                                "UN_VALORINFERIOR     =>",
                                valorInferior.toString(), ", ",
                                "UN_VALORSUPERIOR     =>",
                                valorSuperior.toString(), ", ",
                                "UN_HASTAANO          =>",
                                Integer.toString(hastaAno), ", ",
                                "UN_TIPOPREDIO        =>'", tipoPredio, "'" };
        return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_CALCULA_USUARIOSAFACTURAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public String facturarEnLote(
        String compania,
        String nitcompania,
        String siglaCompania,
        String numeroOrden,
        String codigoInicial,
        String codigoFinal,
        String direccionInicial,
        String direccionFinal,
        String nombreInicial,
        String nombreFinal,
        String nitInicial,
        String nitFinal,
        int anioInicial,
        int anioFinal,
        BigDecimal valorInferior,
        BigDecimal valorSuperior,
        int hastaAno,
        Date fechaLimite,
        String tipoPredio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NITCOMPANIA       =>'", nitcompania, "', ",
                                "UN_SIGLACOMPANIA     =>'", siglaCompania,
                                "', ", "UN_NUMERO_ORDEN      =>'", numeroOrden,
                                "', ", "UN_CODIGOINICIAL     =>'",
                                codigoInicial, "', ",
                                "UN_CODIGOFINAL       =>'", codigoFinal, "', ",
                                "UN_DIRECCIONINICIAL  =>'", direccionInicial,
                                "', ", "UN_DIRECCIONFINAL    =>'",
                                direccionFinal, "', ",
                                "UN_NOMBREINICIAL     =>'", nombreInicial,
                                "', ", "UN_NOMBREFINAL       =>'", nombreFinal,
                                "', ", "UN_NITINICIAL        =>'", nitInicial,
                                "', ", "UN_NITFINAL          =>'", nitFinal,
                                "', ", "UN_ANIOINICIAL       =>",
                                Integer.toString(anioInicial), ", ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(anioFinal), ", ",
                                "UN_VALORINFERIOR     =>",
                                valorInferior.toString(), ", ",
                                "UN_VALORSUPERIOR     =>",
                                valorSuperior.toString(), ", ",
                                "UN_HASTAANO          =>",
                                Integer.toString(hastaAno), ", ",
                                "UN_FECHALIMITE       =>",
                                SysmanFunciones.formatearFecha(fechaLimite),
                                ",",
                                "UN_TIPOPREDIO        =>'", tipoPredio, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.FC_FACTURARLOTEPREDIAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean validaPago(
        String compania,
        String numeroOrden,
        String paquete,
        String pagoBanco,
        Date prefechaVar,
        String codigo,
        String numFactura,
        String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NUMERO_ORDEN      =>'", numeroOrden,
                                    "', ", "UN_PAQUETE           =>",
                                    paquete, ", ",
                                    "UN_PAGOBANCO         =>",
                                    pagoBanco, ", ",
                                    "UN_PREFECHAVAR       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    prefechaVar),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CODIGO            =>'", codigo, "', ",
                                    "UN_NUMFACTURA        =>'", numFactura,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "'" };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM8.FC_VALIDAPAGO",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarAntesPagosDobles(
        String compania,
        String valor,
        String codigo,
        String numeroOrden,
        int anioExc)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VALOR             =>'", valor, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_ANIOEXC           =>",
                                Integer.toString(anioExc), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM8.PR_ACTUALIZAR_ANTESPAGOSDOBLES",
                        SysmanFunciones.concatenar(parametros));
    }

}