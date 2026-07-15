package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadCuatroLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilidadCuatro
 * 
 * @author ybecerra
 * @version 2, 10/06/2017, Implementacion metodo concatenar de la
 * clase SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones
 */
@Stateless
@LocalBean
public class EjbContabilidadCuatro implements EjbContabilidadCuatroRemote,
                EjbContabilidadCuatroLocal {
    /**
     * Default constructor.
     */
    public EjbContabilidadCuatro() {
    }

    @Override
    public String revisarDeterioroDeCartera(
        String compania,
        Date fechacorte,
        int ano,
        String terceroinicial,
        String tercerofinal,
        int mesesvencidos,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA =>'", compania, "', ",
                                   "UN_FECHACORTE        =>'",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacorte),
                                   "', ",
                                   "UN_ANO  =>", Integer.toString(ano), ", ",
                                   "UN_TERCEROINICIAL =>'", terceroinicial,
                                   "', ",
                                   "UN_TERCEROFINAL   =>'", tercerofinal, "', ",
                                   "UN_MESESVENCIDOS    =>",
                                   Integer.toString(mesesvencidos), ", ",
                                   "UN_USUARIO        =>'", usuario, "' "
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_REVISAAFECTACIONESDETERIORO",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean reversarCierreContable(
        String compania,
        int ano,
        String tipocpte,
        BigInteger nrocpte,
        String modulo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                               "UN_ANO        =>", Integer.toString(ano), ", ",
                               "UN_TIPOCPTE   =>'", tipocpte, "', ",
                               "UN_NROCPTE    =>", nrocpte.toString(), ", ",
                               "UN_MODULO     =>'", modulo, "'"

        };
        byte salida;
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD4.FC_REVERSARCIERRE",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String generarComprobanteContableDeterioroDeCartera(
        String companiaSeleccionada,
        String compania,
        int ano,
        Date fechaCorte,
        String descripcion,
        int mesesVencidos,
        String terceroInicial,
        String terceroFinal,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIASELECCIONADA =>'",
                                   companiaSeleccionada, "', ",
                                   "UN_COMPANIA             =>'", compania,
                                   "', ",
                                   "UN_ANO                  =>",
                                   Integer.toString(ano), ", ",
                                   "UN_FECHACORTE        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechaCorte),
                                   "','DD/MM/YYYY'), ",
                                   "UN_DESCRIPCION       =>'", descripcion,
                                   "', ",
                                   "UN_MESESVENCIDOS     =>",
                                   Integer.toString(mesesVencidos), ", ",
                                   "UN_TERCEROINICIAL    =>'", terceroInicial,
                                   "', ",
                                   "UN_TERCEROFINAL      =>'", terceroFinal,
                                   "', ",
                                   "UN_USUARIO           =>'", usuario, "'"

            };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_CREARCOMPROBANTEDETERIORO",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal consultarTasaDeInteres(
        Date fecha)
                    throws SystemException {
        try {
            String[] parametro = { "UN_FECHA   =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY') "

            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_TASAI",
                            SysmanFunciones.concatenar(parametro),
                            Types.DECIMAL);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigInteger generarConsecutivoComprobanteDeterioro(
        String compania,
        String tipo,
        int anio)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_TIPO          =>'", tipo, "', ",
                               "UN_ANIO          =>", Integer.toString(anio), ""

        };
        return (BigInteger) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD4.FC_ENUMERARCOMPRO_DETERIORO",
                        SysmanFunciones.concatenar(parametro),
                        Types.BIGINT);
    }

    @Override
    public String revisarFacturasCanceladasDeterioroDeCartera(
        String companiaseleccionada,
        String compania,
        Date fechacorte,
        int ano,
        int mesesvencidos,
        String terceroinicial,
        String tercerofinal,
        String descripcion,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIASELECCIONADA =>'",
                                   companiaseleccionada, "', ",
                                   "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_FECHACORTE        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacorte),
                                   "','DD/MM/YYYY'), ",
                                   "UN_ANO               =>",
                                   Integer.toString(ano), ", ",
                                   "UN_MESESVENCIDOS     =>",
                                   Integer.toString(mesesvencidos), ", ",
                                   "UN_TERCEROINICIAL    =>'", terceroinicial,
                                   "', ",
                                   "UN_TERCEROFINAL      =>'", tercerofinal,
                                   "', ",
                                   "UN_DESCRIPCION       =>'", descripcion,
                                   "', ", "UN_USUARIO           =>'", usuario,
                                   "'"
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_REVISARFACTURASCANCELADAS",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean GenerarPacProgramado(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        Date fechacomprobante)
                    throws SystemException {
        byte salida;
        try {
            String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_ANO               =>",
                                   Integer.toString(ano), ", ",
                                   "UN_TIPO              =>'", tipo, "', ",
                                   "UN_NUMERO            =>", numero.toString(),
                                   ", ", "UN_FECHACOMPROBANTE  =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacomprobante),
                                   "','DD/MM/YYYY')"

            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_GENERAPACPPCIONALALGIRO_COM",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean generarPacProporcionalAlGiro(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        int mes,
        Date datfechacomprobante,
        BigDecimal dbltotalgiro,
        String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_TIPO              =>'", tipo, "', ",
                                   "UN_NUMERO            =>", numero.toString(),
                                   ", ", "UN_MES               =>",
                                   Integer.toString(mes), ", ",
                                   "UN_DATFECHACOMPROBANTE =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   datfechacomprobante),
                                   "','DD/MM/YYYY'), ",
                                   "UN_DBLTOTALGIRO      =>",
                                   dbltotalgiro.toString(), ", ",
                                   "UN_USUARIO          =>'", usuario, "' "

            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_GENERARPACPPCIONALALGIRO",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean generarPacProporcionalAlGiroSinOrdenDePago(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        Date fechacomprobante,
        BigDecimal totalgiro,
        Date fechainicial,
        Date fechafinal,
        String tercero,
        String sucursal)
                    throws SystemException {
        byte salida;
        try {
            String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_TIPO              =>'", tipo, "', ",
                                   "UN_NUMERO            =>", numero.toString(),
                                   ", ", "UN_FECHACOMPROBANTE  =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacomprobante),
                                   "','DD/MM/YYYY'), ",
                                   "UN_TOTALGIRO         =>",
                                   totalgiro.toString(), ", ",
                                   "UN_FECHAINICIAL      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechainicial),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAFINAL        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechafinal),
                                   "','DD/MM/YYYY'), ",
                                   "UN_TERCERO           =>'", tercero, "', ",
                                   "UN_SUCURSAL          =>'", sucursal, "'"

            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_GENPACPPNALALGIRO_SINCOM",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public String generarPacTesoreria(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        Date fecha,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_TIPO              =>'", tipo, "', ",
                                   "UN_NUMERO            =>", numero.toString(),
                                   ", ", "UN_FECHA       =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_USUARIO           =>'", usuario, "'"

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_GENERARPACTESORERIA",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String crearPacDeIngresos(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        Date fechacpte,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_ANO               =>",
                                   Integer.toString(ano), ", ",
                                   "UN_TIPO              =>'", tipo, "', ",
                                   "UN_NUMERO            =>", numero.toString(),
                                   ", ", "UN_FECHACPTE         =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechacpte),
                                   "','DD/MM/YYYY'), ",
                                   "UN_USUARIO           =>'", usuario, "'"

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_GENERARPACINGRESOS",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizaRetencionesBancos(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String bancoinicial,
        String bancofinal, String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_FECHAINICIAL      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechainicial),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAFINAL        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechafinal),
                                   "','DD/MM/YYYY'), ",
                                   "UN_BANCOINICIAL      =>'", bancoinicial,
                                   "', ", "UN_BANCOFINAL        =>'",
                                   bancofinal, "', ",
                                   "UN_USUARIO           =>'", usuario, "'"

            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.PR_RETENCIONBANCO",
                            SysmanFunciones.concatenar(parametro));
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String cierreContable(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        Date fecha,
        int mes,
        String centroCosto,
        String usuario,
        boolean generaCompDesc)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_TIPOCOMPROBANTE   =>'", tipocomprobante,
                                   "', ", "UN_NUMERO            =>",
                                   numero.toString(), ", ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_MES               =>",
                                   Integer.toString(mes), ", ",
                                   "UN_CENTRO_COSTO      =>'", centroCosto,
                                   "', ", "UN_USUARIO           =>'", usuario,
                                   "', ", "UN_GENERA_COMP_DESC  =>",
                                   generaCompDesc ? "-1" : "0", ""

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_CIERRECONTABLE",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String interfazContableAct(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        int mes,
        Date fecha,
        String tercero,
        String sucursal,
        String centroCosto,
        String descripcion,
        String creador,
        boolean simple,
        boolean indimpresion,
        boolean plano,
        boolean generaCompDesc,
        String texto)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_TIPOCOMPROBANTE   =>'", tipocomprobante,
                                   "', ", "UN_NUMERO            =>",
                                   numero.toString(), ", ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_MES               =>",
                                   Integer.toString(mes), ", ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_TERCERO           =>'", tercero, "', ",
                                   "UN_SUCURSAL          =>'", sucursal, "', ",
                                   "UN_CENTRO_COSTO      =>'", centroCosto,
                                   "', ", "UN_DESCRIPCION       =>'",
                                   descripcion, "', ",
                                   "UN_CREADOR           =>'", creador, "', ",
                                   "UN_SIMPLE            =>",
                                   simple ? "-1" : "0", ", ",
                                   "UN_INDIMPRESION      =>",
                                   indimpresion ? "-1" : "0", ", ",
                                   "UN_PLANO             =>",
                                   plano ? "-1" : "0", ", ",
                                   "UN_GENERA_COMP_DESC  =>",
                                   generaCompDesc ? "-1" : "0", ", ",
                                   "UN_TEXTO             =>'", texto, "'"

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_INTERFAZ_CONTABLE_HACT(",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String interfazContable(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        int mes,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        String creador,
        boolean simple,
        boolean verificaauxiliar,
        String texto)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_TIPOCOMPROBANTE   =>'", tipocomprobante,
                                   "', ", "UN_NUMERO            =>",
                                   numero.toString(), ", ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_MES               =>",
                                   Integer.toString(mes), ", ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_TERCERO           =>'", tercero, "', ",
                                   "UN_SUCURSAL          =>'", sucursal, "', ",
                                   "UN_DESCRIPCION       =>'", descripcion,
                                   "', ", "UN_CREADOR           =>'", creador,
                                   "', ", "UN_SIMPLE            =>",
                                   simple ? "-1" : "0", ", ",
                                   "UN_VERIFICAAUXILIAR  =>",
                                   verificaauxiliar ? "-1" : "0", ", ",
                                   "UN_TEXTO             =>'", texto, "'"

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_INTERFAZ_CONTABLEH",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String cierreContabledeImpuestos(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        Date fecha,
        int mes,
        String centroCosto,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_TIPOCOMPROBANTE   =>'", tipocomprobante,
                                   "', ", "UN_NUMERO            =>",
                                   numero.toString(), ", ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_MES               =>",
                                   Integer.toString(mes), ", ",
                                   "UN_CENTRO_COSTO      =>'", centroCosto,
                                   "', ", "UN_USUARIO           =>'", usuario,
                                   "'"

            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_CIERRECONTABLEIMPUESTOS",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean verificarPeriodo(
        String compania,
        int ano,
        int mes)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                               "UN_ANO          =>", Integer.toString(ano),
                               ", ",
                               "UN_MES          =>", Integer.toString(mes), ""
        };
        byte salida;
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD4.FC_VERIFICAPERIODO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean verificarCuentas(
        String compania,
        int ano,
        String tipocomprobante)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ", "UN_TIPOCOMPROBANTE   =>'",
                               tipocomprobante, "'"

        };
        byte salida;
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD4.FC_VERIFICARCUENTAS",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean cierreContableValidado(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        Date fecha,
        int mes,
        String centroCosto,
        boolean generaCompDesc,
        boolean cierreutilidad,
        boolean cierreimpuestos,
        String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_TIPOCOMPROBANTE   =>'", tipocomprobante,
                                   "', ", "UN_NUMERO            =>",
                                   numero.toString(), ", ",
                                   "UN_ANIO              =>",
                                   Integer.toString(anio), ", ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_MES               =>",
                                   Integer.toString(mes), ", ",
                                   "UN_CENTRO_COSTO      =>'", centroCosto,
                                   "', ", "UN_GENERA_COMP_DESC  =>",
                                   generaCompDesc ? "-1" : "0", ", ",
                                   "UN_CIERREUTILIDAD    =>",
                                   cierreutilidad ? "-1" : "0", ", ",
                                   "UN_CIERREIMPUESTOS   =>",
                                   cierreimpuestos ? "-1" : "0", ", ",
                                   "UN_USUARIO           =>'", usuario, "' "

            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD4.FC_CIERRECONTABLE_VALIDADO",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public String deterioroCuentaH(
        String compania,
        int ano,
        int mes,
        Date fechacorte,
        String usuario,
        String funcion)
                    throws SystemException {
        try {
            String nombreFuncion= "PCK_CONTABILIDAD4."+funcion;
        	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            nombreFuncion,
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarDeterioro(
        String compania,
        int ano,
        int mes,
        Date fechacorte,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD4.FC_CONTABILIZARDETERIORO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String reversarDeterioro(
        String compania,
        int ano,
        int mes,
        Date fechacorte,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD4.FC_REVERSIONDETERIORO",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizar3110(
        String compania,
        int aniotrabajo,
        int aniocomparar,
        int mestrabajo,
        int mescomparar,
        boolean ajustando,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOTRABAJO       =>",
                                Integer.toString(aniotrabajo), ", ",
                                "UN_ANIOCOMPARAR      =>",
                                Integer.toString(aniocomparar), ", ",
                                "UN_MESTRABAJO        =>",
                                Integer.toString(mestrabajo), ", ",
                                "UN_MESCOMPARAR       =>",
                                Integer.toString(mescomparar), ", ",
                                "UN_AJUSTANDO         =>",
                                (ajustando ? "-1" : "0"), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD4.PR_ACTUALIZAR3110",
                        SysmanFunciones.concatenar(parametros));
    }

}