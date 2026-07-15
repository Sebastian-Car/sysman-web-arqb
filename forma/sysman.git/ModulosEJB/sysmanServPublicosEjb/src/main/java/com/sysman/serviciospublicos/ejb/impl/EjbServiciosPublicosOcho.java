/*-
 * EjbServiciosPublicosOcho.java
 *
 * 1.0
 *
 * 15/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
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
 * Session Bean implementation class ServiciosPublicosOcho
 *
 * -- Modificado por lcortes 10/06/2017. Implementacion metodo
 * concatenar de la clase SysmanFunciones para el envio de parametros
 * a los diferentes procedimientos y funciones.
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosOcho implements EjbServiciosPublicosOchoRemote,
                EjbServiciosPublicosOchoLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosOcho() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public boolean registrarActaFinanciable(
        String compania,
        String idacta,
        String clase,
        int ciclo,
        String codigoruta,
        int conceptoinicial,
        int conceptofinal,
        boolean impreso,
        String periodo,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA        => '", compania, "', ",
                                "UN_IDACTA          => '", idacta, "', ",
                                "UN_CLASE           => '", clase, "', ",
                                "UN_CICLO           => ",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA      => '", codigoruta, "', ",
                                "UN_CONCEPTOINICIAL => ",
                                Integer.toString(conceptoinicial), ", ",
                                "UN_CONCEPTOFINAL   => ",
                                Integer.toString(conceptofinal), ", ",
                                "UN_IMPRESO         => ", impreso ? "-1" : "0",
                                ", ", "UN_PERIODO         => '", periodo,
                                "', ", "UN_USUARIO         => '", usuario,
                                "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_REGISTRAR_ACTA_FINANCIABLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void ajustarEstadoUsuario(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA => '", compania, "', ",
                                "UN_USUARIO  => '", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.PR_AJUSTARESTADOUSUARIO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean borrarPaquetePago(String compania,
        String banco,
        Date fecha,
        String paquete)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                    "UN_BANCO     =>'", banco, "', ",
                                    "UN_FECHA     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "' , 'DD/MM/YYYY'), ",
                                    "UN_PAQUETE   =>'", paquete, "' "
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM8.FC_BORRAR_PAQUETE_PAGO",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarMetrosDesviacion(
        String compania,
        int ciclo,
        String codigoruta,
        int metros,
        boolean periodocobro,
        int ano,
        String periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_CICLO        =>", Integer.toString(ciclo),
                                ", ", "UN_CODIGORUTA   =>'", codigoruta, "', ",
                                "UN_METROS       =>", Integer.toString(metros),
                                ", ", "UN_PERIODOCOBRO =>",
                                periodocobro ? "-1" : "0", ", ",
                                "UN_ANO          =>", Integer.toString(ano),
                                ", ", "UN_PERIODO      =>'", periodo, "', ",
                                "UN_USUARIO      =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.PR_ACTUALIZAMETROSDESVIACION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String realizarAbonoCuotasFinanciable(
        String compania,
        int cuotas,
        int ciclo,
        String codigoruta,
        String periodo,
        int anio,
        String usuario,
        String consecutivo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA    => '", compania, "', ",
                                "UN_CUOTAS      => ", Integer.toString(cuotas),
                                ", ", "UN_CICLO       => ",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA  => '", codigoruta, "', ",
                                "UN_PERIODO     => '", periodo, "', ",
                                "UN_ANIO        => ", Integer.toString(anio),
                                ", ", "UN_USUARIO     => '", usuario, "', ",
                                "UN_CONSECUTIVO => '", consecutivo, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ABONARCUOTASFINANCIABLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void reconstruccionDeRecaudosPorConcepto(
        String compania,
        Date fecha,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA => '", compania, "', ",
                                    "UN_FECHA    => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "' , 'DD/MM/YYYY'), ",
                                    "UN_USUARIO  =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM8.PR_RECONSTRUCCIONRECAUCONCEPTO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean validarFinanciable(
        String compania,
        int ciclo,
        int ano,
        String periodo,
        boolean extra,
        String fimm,
        long lectura,
        String bancoperproceso,
        String periodosnocobrofac,
        Date fecha)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA           =>'", compania,
                                    "', ", "UN_CICLO              =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_ANO                =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO            =>'", periodo, "', ",
                                    "UN_EXTRA              =>",
                                    extra ? "-1" : "0", ", ",
                                    "UN_FIMM               =>'", fimm, "', ",
                                    "UN_LECTURA            =>",
                                    Long.toString(lectura), ", ",
                                    "UN_BANCOPERPROCESO    =>'",
                                    bancoperproceso, "', ",
                                    "UN_PERIODOSNOCOBROFAC =>'",
                                    periodosnocobrofac, "', ",
                                    "UN_FECHA              =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY') " };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM8.FC_VALIDAR_FINANCIABLE",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public int asignarConceptosGrupoUsuarios(
        String compania,
        int ciclo,
        String codigoInicial,
        String codigoFinal,
        String periodo,
        int anio,
        String concepto,
        BigDecimal nuevoValorFact,
        String condicionUso,
        String condicionEst,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_CICLO          =>", Integer.toString(ciclo),
                                ", ", "UN_CODIGOINICIAL  =>'", codigoInicial,
                                "', ", "UN_CODIGOFINAL    =>'", codigoFinal,
                                "', ", "UN_PERIODO        =>'", periodo, "', ",
                                "UN_ANIO           =>", Integer.toString(anio),
                                ", ", "UN_CONCEPTO       =>'", concepto, "', ",
                                "UN_NUEVOVALORFACT =>",
                                nuevoValorFact.toString(), ", ",
                                "UN_CONDICIONUSO   =>'", condicionUso, "', ",
                                "UN_CONDICIONEST   =>'", condicionEst, "', ",
                                "UN_USUARIO        =>'", usuario, "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ASIGNARCONCGRUPOUSUARIOS",
                        SysmanFunciones.concatenar(parametros), Types.INTEGER);
    }

    @Override
    public void actualizaConsecutivos(
        String compania,
        int ciclo,
        String marca,
        String codigoInicial,
        String codigoFinal,
        Date fechaLimite1,
        Date fechaLimite2,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA      => '", compania, "', ",
                                    "UN_CICLO         => ",
                                    Integer.toString(ciclo), ", ",
                                    "UN_MARCA         => '", marca, "', ",
                                    "UN_CODIGOINICIAL => '", codigoInicial,
                                    "', ", "UN_CODIGOFINAL   => '", codigoFinal,
                                    "', ", "UN_FECHALIMITE1  => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaLimite1),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE2  => TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaLimite2),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO       => '", usuario, "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM8.PR_ACTUALIZACONSECUTIVOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean validarCodigoRuta(
        String compania,
        int ciclo,
        String codigoRuta)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_CICLO      =>", Integer.toString(ciclo),
                                ", ", "UN_CODIGORUTA =>'", codigoRuta, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_VALIDACODIGORUTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String crearParametroFacturacion(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        int marca,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_MARCA             =>",
                                Integer.toString(marca), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_CREARPARAMETROFACTURA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public int eliminarFinanciable(
        String compania,
        int ciclo,
        String codigoRuta,
        int ano,
        String periodo,
        String bancoPerProceso,
        int concepto,
        String usuario,
        String codigoInterno)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA        => '", compania, "', ",
                                "UN_CICLO           => ",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA      => '", codigoRuta, "', ",
                                "UN_ANO             => ", Integer.toString(ano),
                                ", ", "UN_PERIODO         => '", periodo, "', ",
                                "UN_BANCOPERPROCESO => '", bancoPerProceso,
                                "', ", "UN_CONCEPTO        => ",
                                Integer.toString(concepto), ", ",
                                "UN_USUARIO         => '", usuario, "', ",
                                "UN_CODIGOINTERNO   => '", codigoInterno,
                                "' " };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ELIMINARFINANCIBLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int eliminarFacturado(
        String compania,
        int ciclo,
        String codigoRuta,
        int ano,
        String periodo,
        String bancoPerproceso,
        int concepto,
        String usuario,
        String codigoInterno)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_CICLO           =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA      =>'", codigoRuta, "', ",
                                "UN_ANO             =>", Integer.toString(ano),
                                ", ", "UN_PERIODO         =>'", periodo, "', ",
                                "UN_BANCOPERPROCESO =>'", bancoPerproceso,
                                "', ", "UN_CONCEPTO        =>",
                                Integer.toString(concepto), ", ",
                                "UN_USUARIO         =>'", usuario, "', ",
                                "UN_CODIGOINTERNO   =>'", codigoInterno, "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ELIMINARFACTURADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String cargarConsumosManYProm(
        String compania,
        int ciclo,
        String datosExcel,
        int ano,
        String periodo,
        String usuario,
        String strsql,
        String strlog,
        int numUsuError,
        int numUsuOk,
        boolean tipoConsumo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CICLO             =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_DATOSEXCEL        =>'", datosExcel,
                                    "', ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_STRSQL            =>'", strsql, "', ",
                                    "UN_STRLOG            =>'", strlog, "', ",
                                    "UN_NUM_USU_ERROR     =>",
                                    Integer.toString(numUsuError), ", ",
                                    "UN_NUM_USU_OK        =>",
                                    Integer.toString(numUsuOk), ", ",
                                    "UN_TIPOCONSUMO       =>",
                                    tipoConsumo ? "-1" : "0", "" };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM8.FC_CARGAR_CONSUMOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarConcepto(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String codigoRuta,
        String codigoInterno,
        int concepto,
        String usuario,
        BigDecimal deudaAnterior,
        BigDecimal deudaNueva,
        BigDecimal facturadoAnterior,
        BigDecimal facturadoNuevo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                                "UN_CODIGOINTERNO     =>'", codigoInterno,
                                "', ", "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_DEUDAANTERIOR     =>",
                                deudaAnterior.toString(), ", ",
                                "UN_DEUDANUEVA        =>",
                                deudaNueva.toString(), ", ",
                                "UN_FACTURADOANTERIOR =>",
                                facturadoAnterior.toString(), ", ",
                                "UN_FACTURADONUEVO    =>",
                                facturadoNuevo.toString() };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.PR_ACTUALIZARCONCEPTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String prepararInformeSuspensiones(
        String compania,
        int cicloInicial,
        int cicloFinal,
        int abonos,
        int chapetas,
        int pqr,
        int periodoAtrasoIni,
        int periodoAtrasoFin,
        int condicion,
        BigDecimal valorSuperior,
        int ordenadoPor)
                    throws SystemException {
        try {
            String[] parametro = {
                                   "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_CICLOINICIAL      =>",
                                   Integer.toString(cicloInicial), ", ",
                                   "UN_CICLOFINAL        =>",
                                   Integer.toString(cicloFinal), ", ",
                                   "UN_ABONOS            =>",
                                   Integer.toString(abonos), ", ",
                                   "UN_CHAPETAS          =>",
                                   Integer.toString(chapetas), ", ",
                                   "UN_PQR               =>",
                                   Integer.toString(pqr), ", ",
                                   "UN_PERIODOATRASOINI  =>",
                                   Integer.toString(periodoAtrasoIni), ", ",
                                   "UN_PERIODOATRASOFIN  =>",
                                   Integer.toString(periodoAtrasoFin), ", ",
                                   "UN_CONDICION         =>",
                                   Integer.toString(condicion), ", ",
                                   "UN_VALORSUPERIOR     =>",
                                   String.valueOf(valorSuperior), ", ",
                                   "UN_ORDENADOPOR       =>",
                                   Integer.toString(ordenadoPor), ""
            };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM8.FC_PREPARARINFORMESUSPENSIONES",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarDatosMultiusuarios(
        String compania,
        int ciclo,
        String codigoRuta,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.PR_ACTUALIZARDATOSMULTIUSU",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void generarOrdenDeTrabajo(
        String compania,
        BigInteger consecutivo,
        BigInteger numOrden,
        String usuario)
                    throws SystemException {
        String[] parametro = {
                               "UN_COMPANIA          =>'", compania, "', ",
                               "UN_CONSECUTIVO       =>",
                               String.valueOf(consecutivo), ", ",
                               "UN_NUMORDEN          =>",
                               String.valueOf(numOrden), ", ",
                               "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.PR_GENERARORDENTRABAJO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public boolean actualizarMedidor(String compania, String auxiliar,
        String codigoRuta, int ciclo, String usuario) throws SystemException {
        // TODO Auto-generated method stub
        return false;
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
                                "UN_NUMERO            =>",
                                String.valueOf(numero), ", ",
                                "UN_CLASESOLICITUD    =>'", claseSolicitud,
                                "', ", "UN_NUMERONUEVO       =>",
                                String.valueOf(numeroNuevo), ", ",
                                "UN_CODIGORUTANUEVO   =>'", codigoRutaNuevo,
                                "',", "UN_USUARIO     =>'", usuario, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_AGREGARNUEVOCODIGORUTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String actualizarSubTotalElementos(
        String compania,
        int modulo,
        String usuario,
        String claseSolicitud,
        BigInteger solicitud)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CLASESOLICITUD    =>'", claseSolicitud,
                                "', ", "UN_SOLICITUD         =>",
                                String.valueOf(solicitud), "" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ACTSUBTOTELEMENTOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean actualizarMedidorEstado(
        String compania,
        String estado,
        String marca,
        String codigo,
        String usuario,
        int clase,
        int localizacion,
        int ciclo,
        String codigoRuta,
        int digitos)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ESTADO            =>'", estado, "', ",
                                "UN_MARCA             =>'", marca, "', ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_CLASE             =>",
                                Integer.toString(clase), ", ",
                                "UN_LOCALIZACION      =>",
                                Integer.toString(localizacion), ", ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                                "UN_DIGITOS           =>",
                                Integer.toString(digitos), "" };
        byte salida;
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ACTUALIZAR_MEDIDOR_ESTADO",
                        SysmanFunciones.concatenar(parametros), Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String obtenerAnioPeriodoActual(
        String compania,
        int ciclo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM8.FC_ANOPERACTUAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

}