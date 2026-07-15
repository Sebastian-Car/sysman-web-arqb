package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PresupuestoDos
 *
 * @version 2, 10/06/2017 jrodriguezr Se realiza refactorización de
 * concatenados
 */
@Stateless
@LocalBean
public class EjbPresupuestoDos
                implements EjbPresupuestoDosRemote, EjbPresupuestoDosLocal {
    /**
     * Default constructor.
     */
    public EjbPresupuestoDos() {
        // No tiene sentencias
    }

    @Override
    public boolean actualizarRubrosAsociadosEnContratacion(
        String compania,
        int ano,
        String tipocomprobante,
        BigInteger numerocomprobante,
        String tipocontrato,
        long numerocontrato)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPOCOMPROBANTE   =>'", tipocomprobante,
                                "', ", "UN_NUMEROCOMPROBANTE =>",
                                numerocomprobante.toString(), ", ",
                                "UN_TIPOCONTRATO      =>'", tipocontrato, "', ",
                                "UN_NUMEROCONTRATO    =>",
                                Long.toString(numerocontrato), "" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.FC_AUCOMPROBANTE_DETALLEPPTAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);

        return rta != 0;
    }

    @Override
    public void afectarOtroComprobantePresupuestal(
        String compania,
        int modulo,
        int ano,
        int ano0,
        String tipo0,
        String tipo,
        BigInteger numero,
        String cuenta,
        BigDecimal creditoa,
        BigDecimal contracreditoa,
        BigDecimal credito,
        BigDecimal contracredito,
        int consecutivo,
        int consecutivoppto,
        String con,
        BigInteger numero0)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_ANO0              =>",
                                Integer.toString(ano0), ", ",
                                "UN_TIPO0             =>'", tipo0, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ",
                                "UN_CUENTA            =>'", cuenta, "', ",
                                "UN_CREDITOA          =>", creditoa.toString(),
                                ", ",
                                "UN_CONTRACREDITOA    =>",
                                contracreditoa.toString(), ", ",
                                "UN_CREDITO           =>", credito.toString(),
                                ", ",
                                "UN_CONTRACREDITO     =>",
                                contracredito.toString(), ", ",
                                "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo), ", ",
                                "UN_CONSECUTIVOPPTO   =>",
                                Integer.toString(consecutivoppto),
                                ", ", "UN_CON               =>'", con, "', ",
                                "UN_NUMERO0           =>", numero0.toString(),
                                "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.PR_AFECTAROTROCOMPROBANTE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String liberarComprobantePresupuestal(
        String compania,
        int modulo,
        int ano,
        String tipo,
        BigInteger numero,
        int consecutivo,
        BigDecimal valorneto,
        Date fecha,
        String cuenta,
        String tercero,
        String sucursal,
        String centroCosto,
        String auxiliar,
        String fuenteRecursos,
        String referencia,
        String naturaleza,
        String descripcionUsuario,
        String usuario)
                    throws SystemException { 
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_CONSECUTIVO       =>",
                                    Integer.toString(consecutivo), ", ",
                                    "UN_VALORNETO         =>",
                                    valorneto.toString(), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CUENTA            =>'", cuenta, "', ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_CENTRO_COSTO      =>'", centroCosto,
                                    "', ", "UN_AUXILIAR   =>'", auxiliar, "', ",
                                    "UN_FUENTE_RECURSO     =>'", fuenteRecursos,
                                    "', ",
                                    "UN_REFERENCIA         =>'", referencia,
                                    "', ",
                                    "UN_NATURALEZA         =>'", naturaleza,
                                    "', ","UN_DESCRIPCION_USUARIO      => '",descripcionUsuario,"',",
                                    "UN_USUARIO           =>'", usuario, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO2.FC_LIBERARCOMPROBANTE",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean modificarAuxiliaresEnDetallesPresupuestales(
        String compania,
        int modulo,
        int ano,
        String tipo,
        BigInteger comprobante,
        String terceroa,
        String terceron,
        String sucursala,
        String sucursaln,
        String descripciona,
        String descripcionn,
        String numerodoca,
        String numerodocn,
        String referenciaa,
        String referencian,
        String auxiliara,
        String auxiliarn)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_COMPROBANTE       =>",
                                comprobante.toString(), ", ",
                                "UN_TERCEROA          =>'", terceroa, "', ",
                                "UN_TERCERON          =>'", terceron, "', ",
                                "UN_SUCURSALA         =>'", sucursala, "', ",
                                "UN_SUCURSALN         =>'", sucursaln, "', ",
                                "UN_DESCRIPCIONA      =>'", descripciona, "', ",
                                "UN_DESCRIPCIONN      =>'", descripcionn, "', ",
                                "UN_NUMERODOCA        =>'", numerodoca, "', ",
                                "UN_NUMERODOCN        =>'", numerodocn, "', ",
                                "UN_REFERENCIAA       =>'", referenciaa, "', ",
                                "UN_REFERENCIAN       =>'", referencian, "', ",
                                "UN_AUXILIARA         =>'", auxiliara, "', ",
                                "UN_AUXILIARN         =>'", auxiliarn, "'" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.FC_ACTUALIZARDETALLEPPTAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void cuadrarSaldosPpto(
        String compania,
        int mesinicial,
        int mesfinal,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MESINICIAL        =>",
                                Integer.toString(mesinicial), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesfinal), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.PR_CUADRESALDOSPTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void mayorizarPacApropiado(
        String compania,
        int anio,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.PR_MAYORIZAPACAPROPIADO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String verificarSaldoDisponible(
        String clase,
        String compania,
        int anio,
        Date fecha,
        String codigo,
        String tercero,
        String sucursal,
        String auxiliar,
        String centro,
        String referencia,
        String fuenterecurso,
        BigDecimal debitoant,
        BigDecimal creditoant,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal valor)
                    throws SystemException {
        try {
            String[] parametros = { "UN_CLASE             =>'", clase, "', ",
                                    "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CODIGO            =>'", codigo, "', ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_AUXILIAR          =>'", auxiliar, "', ",
                                    "UN_CENTRO            =>'", centro, "', ",
                                    "UN_REFERENCIA        =>'", referencia,
                                    "', ", "UN_FUENTERECURSO     =>'",
                                    fuenterecurso, "', ",
                                    "UN_DEBITOANT         =>",
                                    debitoant.toString(), ", ",
                                    "UN_CREDITOANT        =>",
                                    creditoant.toString(), ", ",
                                    "UN_DEBITO            =>",
                                    debito.toString(), ", ",
                                    "UN_CREDITO           =>",
                                    credito.toString(), ", ",
                                    "UN_VALOR             =>", valor.toString(),
                                    "" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO2.FC_SALDODISPONIBLE",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void agregarRubrosInferiores(
        String compania,
        int anio,
        String codigo,
        String cuentaFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_CUENTA_FINAL      =>'", cuentaFinal, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.PR_AGREGAR_RUBROS_INF",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long generarRegistroConTipo(String compania,
        int anio,
        String tipo,
        Date fechaComprobante,
        String descripcionComprobante,
        String objeto,
        String documentoComprobante,
        String vlrDocumentoComprobante,
        String tercero,
        String sucursal,
        BigDecimal debitoComprobante,
        BigDecimal creditoComprobante,
        BigDecimal abonadoComprobante,
        String creadorComprobante,
        String destinoComprobante,
        String tipoComprobante,
        long numeroComprobante)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA                   =>'",
                                    compania, "', ",
                                    "UN_ANO                        =>",
                                    Integer.toString(anio), ", ",
                                    "UN_TIPO                       =>'", tipo,
                                    "', ",
                                    "UN_FECHA_COMPROBANTE          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaComprobante),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DESCRIPCION_COMPROBANTE    =>'",
                                    descripcionComprobante, "', ",
                                    "UN_OBJETO                     =>'", objeto,
                                    "', ", "UN_DOCUMENTO_COMPROBANTE      =>'",
                                    documentoComprobante, "', ",
                                    "UN_VLR_DOCUMENTO_COMPROBANTE  =>'",
                                    vlrDocumentoComprobante, "', ",
                                    "UN_TERCERO                    =>'",
                                    tercero, "', ",
                                    "UN_SUCURSAL                   =>'",
                                    sucursal, "', ",
                                    "UN_DEBITO_COMPROBANTE         =>",
                                    debitoComprobante.toString(), ", ",
                                    "UN_CREDITO_COMPROBANTE        =>",
                                    creditoComprobante.toString(), ", ",
                                    "UN_ABONADO_COMPROBANTE        =>",
                                    abonadoComprobante.toString(), ", ",
                                    "UN_CREADOR_COMPROBANTE        =>'",
                                    creadorComprobante, "', ",
                                    "UN_DESTINO_COMPROBANTE        =>'",
                                    descripcionComprobante, "', ",
                                    "UN_TIPO_COMPROBANTE           =>'",
                                    tipoComprobante, "', ",
                                    "UN_NUMERO_COMPROBANTE         =>",
                                    Long.toString(numeroComprobante), ", " };
            return (long) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO2.FC_GENERAR_REGISTRO_CON_TIPO",
                            SysmanFunciones.concatenar(parametros),
                            Types.BIGINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean diferenciaValidaVA(
        String compania,
        int anio,
        String tipoCpte,
        BigInteger comprobante,
        String claseComprobante)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                                "UN_COMPROBANTE       =>",
                                comprobante.toString(), ", ",
                                "UN_CLASE_COMPROBANTE =>'", claseComprobante,
                                "'" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO2.FC_DIFERENCIA_VALIDA_VA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String afectarCptesDesdeSolicitudes(
        String compania,
        int ano,
        Date fecha,
        String tipot,
        String usuario,
        String tipoCpteAfect,
        BigInteger cmpteAfectado,
        String dependencia,
        BigInteger novedad,
        String claset,
        String vlrDocumento,
        String debito,
        String cargo,
        String codProyecto,
        String descripcion,
        String tipoCpte,
        BigInteger numeroCpte,
        String destino)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPOT             =>'", tipot, "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_TIPO_CPTE_AFECT   =>'", tipoCpteAfect,
                                    "', ", "UN_CMPTE_AFECTADO    =>",
                                    cmpteAfectado.toString(), ", ",
                                    "UN_DEPENDENCIA       =>'", dependencia,
                                    "', ", "UN_NOVEDAD           =>",
                                    novedad.toString(), ", ",
                                    "UN_CLASET            =>'", claset, "', ",
                                    "UN_VLR_DOCUMENTO     =>'", vlrDocumento,
                                    "', ", "UN_DEBITO            =>'", debito,
                                    "', ", "UN_CARGO             =>'", cargo,
                                    "', ", "UN_COD_PROYECTO      =>'",
                                    codProyecto, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "',", "UN_TIPO_CPTE         =>'", tipoCpte,
                                    "',", "UN_NUMERO_CPTE   =>",
                                    numeroCpte.toString(),
                                    ", ", "UN_DESTINO   => '",
                                    destino, "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESUPUESTO2.FC_AFECTARCPTES",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
}