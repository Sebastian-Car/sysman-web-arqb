package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadDosGeneralLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadDosLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadDosRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilidadDos
 */
@Stateless
@LocalBean
public class EjbContabilidadDos
                implements EjbContabilidadDosRemote, EjbContabilidadDosLocal {

    @EJB
    private EjbContabilidadDosGeneralLocal ejbContabilidadDosGeneral;

    /**
     * Default constructor.
     */
    public EjbContabilidadDos() {
    }

    @Override
    public int calcularRetenciones(
        String compania,
        int modulo,
        int consecmensajes,
        int ano,
        Date fecha,
        String tipo,
        BigInteger numero,
        String auxiliar,
        String tercero,
        String sucursal,
        String nombretercero,
        BigDecimal valorbase,
        BigDecimal valorbaseiva,
        String descripcion,
        String centrocosto,
        String nrodocumento,
        String usuario,
        String fuenteR,
        String referencia)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_CONSECMENSAJES    =>",
                                    Integer.toString(consecmensajes), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'",
                                    tipo, "', ", "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_AUXILIAR          =>'", auxiliar, "', ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_NOMBRETERCERO     =>'", nombretercero,
                                    "', ", "UN_VALORBASE         =>",
                                    valorbase.toString(), ", ",
                                    "UN_VALORBASEIVA      =>",
                                    valorbaseiva.toString(), ", ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ",
                                    "UN_CENTROCOSTO       =>'", centrocosto,
                                    "', ",
                                    "UN_NRODOCUMENTO      =>'", nrodocumento,
                                    "', ", 
                                    "UN_USUARIO           =>'", usuario,
                                    "', ", 
                                    "UN_FUENTER           =>'", fuenteR,
                                    "', ",
                                    "UN_REFERENCIA        =>'", referencia,
                                    "' "};
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_CALCULORETENCIONES",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public long distribuirCentrosDeCosto(
        String compania,
        BigDecimal consecmensajes,
        int ano,
        String auxiliar,
        String tercero,
        String sucursal,
        BigDecimal valordistribuir,
        String cuenta,
        boolean debito,
        boolean credito,
        BigInteger numero,
        String tipo,
        Date fecha,
        String descripcion)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CONSECMENSAJES    =>",
                                    consecmensajes.toString(),
                                    ", ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_AUXILIAR          =>'", auxiliar, "', ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_VALORDISTRIBUIR   =>",
                                    valordistribuir.toString(),
                                    ", ", "UN_CUENTA            =>'", cuenta,
                                    "', ", "UN_DEBITO            =>",
                                    (debito ? "-1" : "0"), ", ",
                                    "UN_CREDITO           =>",
                                    (credito ? "-1" : "0"), ", ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "'" };

            return (long) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_DISTRIBUIRCENTROS",
                            SysmanFunciones.concatenar(parametros),
                            Types.BIGINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal calcularRetencionesTrabajadoresIndepedientes(
        String compania,
        int modulo,
        BigDecimal consecmensajes,
        int ano,
        Date fecha,
        String tipo,
        BigInteger numero,
        String tercero,
        String sucursal,
        String nombretercero,
        BigDecimal valorbase,
        BigDecimal valorbaseiva,
        int consecutivo,
        boolean tienecontenido,
        String cuentaDebito1,
        String cuentaCredito1,
        String tiporetencion,
        String cuentaDebito,
        String cuentaCredito,
        BigDecimal factorredondeo,
        String codigoretencion,
        String descripcion,
        String strcentroCosto,
        String nroDocumento)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_CONSECMENSAJES    =>",
                                    consecmensajes.toString(),
                                    ", ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_NOMBRETERCERO     =>'", nombretercero,
                                    "', ", "UN_VALORBASE         =>",
                                    valorbase.toString(),
                                    ", ", "UN_VALORBASEIVA      =>",
                                    valorbaseiva.toString(), ", ",
                                    "UN_CONSECUTIVO       =>",
                                    Integer.toString(consecutivo),
                                    ", ", "UN_TIENECONTENIDO    =>",
                                    (tienecontenido ? "-1" : "0"), ", ",
                                    "UN_CUENTA_DEBITO1    =>'", cuentaDebito1,
                                    "', ", "UN_CUENTA_CREDITO1   =>'",
                                    cuentaCredito1, "', ",
                                    "UN_TIPORETENCION     =>'", tiporetencion,
                                    "', ", "UN_CUENTA_DEBITO     =>'",
                                    cuentaDebito, "', ",
                                    "UN_CUENTA_CREDITO    =>'", cuentaCredito,
                                    "', ", "UN_FACTORREDONDEO    =>",
                                    factorredondeo.toString(), ", ",
                                    "UN_CODIGORETENCION   =>'", codigoretencion,
                                    "', ", "UN_DESCRIPCION       =>'",
                                    descripcion, "', ",
                                    "UN_STRCENTRO_COSTO   =>'", strcentroCosto,
                                    "', ", "UN_NRO_DOCUMENTO     =>'",
                                    nroDocumento, "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_CONRETENCIONESLEY1450",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal buscarPorcentajeRetencionEnLaFuente(
        String compania,
        int anio,
        BigDecimal base,
        BigDecimal uvt,
        String opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_BASE              =>", base.toString(),
                                ", ", "UN_UVT               =>", uvt.toString(),
                                ", ", "UN_OPCION            =>'", opcion, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_RETEF2007",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal buscarValorParaRetencionEnLaFuente(
        String compania,
        int consecmensajes,
        int anio,
        BigDecimal base)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONSECMENSAJES    =>",
                                Integer.toString(consecmensajes), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_BASE              =>", base.toString(),
                                "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_RETEF20071",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal consultarValorUvt(
        String compania,
        BigDecimal consecmensajes,
        BigDecimal base,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONSECMENSAJES    =>",
                                consecmensajes.toString(), ", ",
                                "UN_BASE              =>", base.toString(),
                                ", ", "UN_ANIO              =>",
                                Integer.toString(anio), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_RETEF2013_384",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal generarValorRetencionPagosLaborales(
        String compania,
        int modulo,
        int consecmensajes,
        BigDecimal valorb,
        BigDecimal salud,
        BigDecimal pension,
        BigDecimal fondospensional,
        BigDecimal uvt,
        int mes,
        String ano,
        boolean declararenta,
        BigDecimal factorredondeo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_CONSECMENSAJES    =>",
                                Integer.toString(consecmensajes), ", ",
                                "UN_VALORB            =>", valorb.toString(),
                                ", ", "UN_SALUD             =>",
                                salud.toString(), ", ",
                                "UN_PENSION           =>", pension.toString(),
                                ", ", "UN_FONDOSPENSIONAL   =>",
                                fondospensional.toString(), ", ",
                                "UN_UVT               =>", uvt.toString(), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_ANO               =>'", ano, "', ",
                                "UN_DECLARARENTA      =>",
                                (declararenta ? "-1" : "0"), ", ",
                                "UN_FACTORREDONDEO    =>",
                                factorredondeo.toString(), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_RETENCIONES_MINIMAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal generarValorDeRetencionesEmpleados(
        String compania,
        int consecmensajes,
        int ano,
        Date fecha,
        String tipo,
        BigInteger numero,
        String tercero,
        String sucursal,
        BigDecimal valorbase,
        BigDecimal valorbaseiva,
        int consecutivo,
        boolean tienecontenido,
        String cuentaDebito,
        String cuentaCredito,
        String tiporetencion,
        BigDecimal limiteInf,
        BigDecimal pctBase,
        BigDecimal pctAplicar,
        BigDecimal valorAplicar,
        BigDecimal factorredondeo,
        String codigoretencion,
        boolean indiley,
        String descripcion,
        String strcentroCosto,
        String nroDocumento)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CONSECMENSAJES    =>",
                                    Integer.toString(consecmensajes),
                                    ", ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_VALORBASE         =>",
                                    valorbase.toString(), ", ",
                                    "UN_VALORBASEIVA      =>",
                                    valorbaseiva.toString(),
                                    ", ", "UN_CONSECUTIVO       =>",
                                    Integer.toString(consecutivo), ", ",
                                    "UN_TIENECONTENIDO    =>",
                                    (tienecontenido ? "-1" : "0"), ", ",
                                    "UN_CUENTA_DEBITO     =>'", cuentaDebito,
                                    "', ", "UN_CUENTA_CREDITO    =>'",
                                    cuentaCredito, "', ",
                                    "UN_TIPORETENCION     =>'", tiporetencion,
                                    "', ", "UN_LIMITE_INF        =>",
                                    limiteInf.toString(),
                                    ", ", "UN_PCT_BASE          =>",
                                    pctBase.toString(),
                                    ", ", "UN_PCT_APLICAR       =>",
                                    pctAplicar.toString(),
                                    ", ", "UN_VALOR_APLICAR     =>",
                                    valorAplicar.toString(), ", ",
                                    "UN_FACTORREDONDEO    =>",
                                    factorredondeo.toString(),
                                    ", ", "UN_CODIGORETENCION   =>'",
                                    codigoretencion, "', ",
                                    "UN_INDILEY           =>",
                                    (indiley ? "-1" : "0"), ", ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_STRCENTRO_COSTO   =>'",
                                    strcentroCosto, "', ",
                                    "UN_NRO_DOCUMENTO     =>'", nroDocumento,
                                    "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_SINRETENCIONESLEY1450",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal calcularRetencionesPagosLaborales1607(
        String compania,
        int modulo,
        int consecmensajes,
        int ano,
        Date fecha,
        String tipo,
        BigInteger numero,
        String tercero,
        String sucursal,
        String nombretercero,
        BigDecimal valorbase,
        BigDecimal valorbaseiva,
        String descripcion,
        String strcentroCosto,
        String nroDocumento)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_CONSECMENSAJES    =>",
                                    Integer.toString(consecmensajes), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_NOMBRETERCERO     =>'", nombretercero,
                                    "', ", "UN_VALORBASE         =>",
                                    valorbase.toString(), ", ",
                                    "UN_VALORBASEIVA      =>",
                                    valorbaseiva.toString(), ", ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_STRCENTRO_COSTO   =>'",
                                    strcentroCosto, "', ",
                                    "UN_NRO_DOCUMENTO     =>'", nroDocumento,
                                    "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_CALCULORETENCIONESLEY1607",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal calcularRetencionesPagosLaboralesLey1450(
        String compania,
        int modulo,
        BigDecimal consecmensajes,
        int ano,
        Date fecha,
        String tipo,
        BigInteger numero,
        String tercero,
        String sucursal,
        String nombretercero,
        BigDecimal valorbase,
        BigDecimal valorbaseiva,
        String descripcion,
        String strcentroCosto,
        String nroDocumento,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_CONSECMENSAJES    =>",
                                    consecmensajes.toString(), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_NOMBRETERCERO     =>'", nombretercero,
                                    "', ", "UN_VALORBASE         =>",
                                    valorbase.toString(), ", ",
                                    "UN_VALORBASEIVA      =>",
                                    valorbaseiva.toString(), ", ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_STRCENTRO_COSTO   =>'",
                                    strcentroCosto, "', ",
                                    "UN_NRO_DOCUMENTO     =>'", nroDocumento,
                                    "',",
                                    "UN_USUARIO => '", usuario, "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_CALCULARLEY1450",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int generarConsecutivoParaMensajes()
                    throws SystemException {
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_CONSECUTIVOMENSAJES", "",
                        Types.INTEGER);
    }

    @Override
    public int ordenarConsecutivoParaMensajes(
        int consecutivo)
                    throws SystemException {
        String[] parametros = { "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo), "" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_ORDENMENSAJES",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void copiarComprobanteContable(
        String compania,
        int anocopiar,
        String tipocopiar,
        BigInteger numerocopiar,
        String tercero,
        String sucursal,
        int ano,
        String tipo,
        BigInteger numero,
        Date fecha,
        String descripcion,
        String nroDocumento,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANOCOPIAR         =>",
                                    Integer.toString(anocopiar), ", ",
                                    "UN_TIPOCOPIAR        =>'", tipocopiar,
                                    "', ", "UN_NUMEROCOPIAR      =>",
                                    numerocopiar.toString(), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_NRO_DOCUMENTO     =>'",
                                    nroDocumento, "', ", "UN_USUARIO       =>'",
                                    usuario, "' " };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_COMPACOPIAR_AFTERUPDATE",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }

    }

    @Override
    public String consultarNaturalezaCuentaContable(
        String cuenta)
                    throws SystemException {
        String[] parametros = { "UN_CUENTA            =>'", cuenta, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_NATURALEZACUENTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal distribuirPorConceptosComprobanteContable(
        String compania,
        String tipo,
        BigInteger numero,
        int ano,
        String strtipocobro,
        String strconcepto,
        boolean regimen,
        BigDecimal vlrdocumento,
        String tipocobroconcepto,
        String concepto,
        Date fecha,
        String descripcion,
        BigDecimal vlrbase,
        BigDecimal vlrbaseiva,
        String centroCosto,
        String tercero,
        String sucursal,
        String auxiliar,
        String referencia)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_STRTIPOCOBRO      =>'", strtipocobro,
                                    "', ", "UN_STRCONCEPTO       =>'",
                                    strconcepto, "', ",
                                    "UN_REGIMEN           =>",
                                    (regimen ? "-1" : "0"), ", ",
                                    "UN_VLRDOCUMENTO      =>",
                                    vlrdocumento.toString(), ", ",
                                    "UN_TIPOCOBROCONCEPTO =>'",
                                    tipocobroconcepto, "', ",
                                    "UN_CONCEPTO          =>'", concepto, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_VLRBASE           =>",
                                    vlrbase.toString(), ", ",
                                    "UN_VLRBASEIVA        =>",
                                    vlrbaseiva.toString(), ", ",
                                    "UN_CENTRO_COSTO      =>'", centroCosto,
                                    "', ", "UN_TERCERO           =>'", tercero,
                                    "', ", "UN_SUCURSAL          =>'", sucursal,
                                    "', ", "UN_AUXILIAR          =>'", auxiliar,
                                    "', ", "UN_REFERENCIA        =>'",
                                    referencia, "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_DISTRIBUYECONCEPTO",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String llenarFormatoDeCheque(
        String compania,
        String nombrecompania,
        int modulo,
        int ano,
        String tipo,
        BigInteger numero)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NOMBRECOMPANIA    =>'", nombrecompania,
                                "', ", "UN_MODULO            =>",
                                Integer.toString(modulo), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD2.FC_LLENARFORMATOCHEQUETOTAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal modificarAuxiliaresEnDetallesContables(
        String compania,
        int ano,
        String tipo,
        BigInteger comprobante,
        Date fechaa,
        String terceroa,
        String sucursala,
        String descripciona,
        String numerodoca,
        String referenciaa,
        String auxiliara,
        Date fechan,
        String terceron,
        String sucursaln,
        String descripcionn,
        String numerodocn,
        String referencian,
        String auxiliarn)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_COMPROBANTE       =>",
                                    comprobante.toString(), ", ",
                                    "UN_FECHAA            =>'",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaa),
                                    "', ", "UN_TERCEROA          =>'", terceroa,
                                    "', ", "UN_SUCURSALA         =>'",
                                    sucursala, "', ",
                                    "UN_DESCRIPCIONA      =>'", descripciona,
                                    "', ", "UN_NUMERODOCA        =>'",
                                    numerodoca, "', ",
                                    "UN_REFERENCIAA       =>'", referenciaa,
                                    "', ", "UN_AUXILIARA         =>'",
                                    auxiliara, "', ",
                                    "UN_FECHAN            =>'",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechan),
                                    "', ", "UN_TERCERON          =>'", terceron,
                                    "', ", "UN_SUCURSALN         =>'",
                                    sucursaln, "', ",
                                    "UN_DESCRIPCIONN      =>'", descripcionn,
                                    "', ", "UN_NUMERODOCN        =>'",
                                    numerodocn, "', ",
                                    "UN_REFERENCIAN       =>'", referencian,
                                    "', ", "UN_AUXILIARN         =>'",
                                    auxiliarn, "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.FC_ACTUALIZARDETALLECNT",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarValorPagosEnComprobantesContables(
        String compania,
        int ano,
        String tipo,
        BigDecimal numero,
        Date fecha,
        Date fechaVcnDoc,
        BigDecimal vlrDocumento,
        BigDecimal vlrDocumentoAnt,
        String tipoContrato,
        BigDecimal numeroContrato,
        String clase,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_FECHA             =>'",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "', ", "UN_FECHA_VCN_DOC     =>'",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaVcnDoc),
                                    "', ", "UN_VLR_DOCUMENTO     =>",
                                    vlrDocumento.toString(), ", ",
                                    "UN_VLR_DOCUMENTO_ANT =>",
                                    vlrDocumentoAnt.toString(), ", ",
                                    "UN_TIPO_CONTRATO     =>'", tipoContrato,
                                    "', ", "UN_NUMERO_CONTRATO   =>",
                                    numeroContrato.toString(), ", ",
                                    "UN_CLASE             =>'", clase, "', ",
                                    "UN_USUARIO           =>'", usuario, "' " };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD2.PR_ACTUALIZAR_VALOR_PAGOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarComprobantesContablesNiif(
        String compania,
        int mesInicial,
        int mesFinal,
        String tipoInicial,
        String tipoFinal,
        int anio,
        String usuario)
                    throws SystemException {

        return ejbContabilidadDosGeneral.contabilizarComprobantesContablesNiif(
                        compania, mesInicial, mesFinal, tipoInicial, tipoFinal,
                        anio, usuario);
    }

    @Override
    public String crearCompaniaNiifLotes(
        String compania,
        String companiaNiif)
                    throws SystemException {

        return ejbContabilidadDosGeneral.crearCompaniaNiifLotes(compania,
                        companiaNiif);

    }
}