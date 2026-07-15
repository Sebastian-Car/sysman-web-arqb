package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadUnoLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
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
 * Session Bean implementation class ContabilidadUno
 * 
 * @author eamaya
 * @version 2.0 , Refactoring concatenacion
 * 
 */
@Stateless
@LocalBean
public class EjbContabilidadUno
                implements EjbContabilidadUnoRemote, EjbContabilidadUnoLocal {
    /**
     * Default constructor.
     */
    public EjbContabilidadUno() {
    }

    @Override
    public int insertarCuentasRetenciones(
        String compania,
        int anioactual,
        int aniopreparar)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOACTUAL        =>",
                                Integer.toString(anioactual), ", ",
                                "UN_ANIOPREPARAR      =>",
                                Integer.toString(aniopreparar), "" };

        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.FC_CREARCUENTA_ANIOPREPARAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public BigDecimal calcularValoraGirar(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String clase,
        BigDecimal valoragirar)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_ANIO      =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO      =>'", tipo, "', ",
                                "UN_NUMERO    =>", numero.toString(), ", ",
                                "UN_CLASE     =>'", clase, "', ",
                                "UN_VALORAGIRAR =>", valoragirar.toString()
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.FC_CALCULARVLRGIRAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void eliminarComprobantePresupuestal(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.PR_ELIMINARCOMPROBANTEPPTAL",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void afectarOtroComprobantePresupuestal(
        String compania,
        String tipo0,
        String tipo,
        BigInteger numero,
        String cuenta,
        BigDecimal creditoa,
        BigDecimal contracreditoa,
        BigDecimal credito,
        BigDecimal contracredito,
        int consecutivo,
        String con,
        BigInteger numero0,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO0             =>'", tipo0, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_CUENTA            =>'", cuenta, "', ",
                                "UN_CREDITOA          =>", creditoa.toString(),
                                ", ", "UN_CONTRACREDITOA    =>",
                                contracreditoa.toString(), ", ",
                                "UN_CREDITO           =>", credito.toString(),
                                ", ", "UN_CONTRACREDITO     =>",
                                contracredito.toString(), ", ",
                                "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo), ", ",
                                "UN_CON               =>'", con, "', ",
                                "UN_NUMERO0           =>", numero0.toString(),
                                ", ", "UN_USUARIO           =>'", usuario,
                                "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.PR_AFECTAROTROCOMPROBANTE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean revisarDesembolso(
        String compania,
        int anio,
        String cuenta,
        BigDecimal desembolso,
        BigDecimal valor)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_ANIO      =>", Integer.toString(anio), ", ",
                                "UN_CUENTA    =>'", cuenta, "', ",
                                "UN_DESEMBOLSO =>", desembolso.toString(), ", ",
                                "UN_VALOR     =>", valor.toString() };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.FC_VERIFICARDESEMBOLSO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean descargarDesembolso(
        String compania,
        int anio,
        String cuenta,
        BigDecimal desembolso,
        String tipo,
        BigInteger numero,
        Date fecha,
        BigDecimal valor,
        BigDecimal tasadecambio,
        int consecutivo,
        String tercero,
        String sucursal,
        String centrocosto,
        String auxiliar,
        String tipoafect,
        BigInteger numeroafect,
        String descripcion,
        double porcretencion)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_CUENTA            =>'", cuenta, "', ",
                                    "UN_DESEMBOLSO        =>",
                                    desembolso.toString(), ", ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_VALOR             =>", valor.toString(),
                                    ", ",
                                    "UN_TASADECAMBIO      =>",
                                    tasadecambio.toString(),
                                    ", ", "UN_CONSECUTIVO       =>",
                                    Integer.toString(consecutivo), ", ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_CENTROCOSTO       =>'", centrocosto,
                                    "', ", "UN_AUXILIAR          =>'", auxiliar,
                                    "', ", "UN_TIPOAFECT         =>'",
                                    tipoafect, "', ", "UN_NUMEROAFECT       =>",
                                    numeroafect.toString(), ", ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_PORCRETENCION     =>",
                                    Double.toString(porcretencion), "" };
            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_DESCARGARDESEMBOLSO",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void afectarDesembolso(
        String compania,
        int anio,
        String cuenta,
        BigDecimal desembolso,
        BigDecimal vdesem,
        BigDecimal vavdesem)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_CUENTA            =>'", cuenta, "', ",
                                "UN_DESEMBOLSO        =>",
                                desembolso.toString(), ", ",
                                "UN_VDESEM            =>", vdesem.toString(),
                                ", ", "UN_VAVDESEM          =>",
                                vavdesem.toString(), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.PR_AFECTARDESEMBOLSO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void insertarDetallecomprobanteContable(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String cuenta,
        int consecutivo,
        String fecha,
        String descripcion,
        String tercero,
        String sucursal,
        String centrocosto,
        String auxiliar,
        BigDecimal valordebito,
        BigDecimal valorcredito,
        String tipoafect,
        BigInteger numeroafect,
        BigDecimal debitoequiv,
        BigDecimal creditoequiv,
        double porcentajeret)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ",
                                "UN_CUENTA            =>'", cuenta, "', ",
                                "UN_CONSECUTIVO       =>",
                                Integer.toString(consecutivo), ", ",
                                "UN_FECHA             =>'", fecha, "', ",
                                "UN_DESCRIPCION       =>'", descripcion, "', ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_CENTROCOSTO       =>'", centrocosto, "', ",
                                "UN_AUXILIAR          =>'", auxiliar, "', ",
                                "UN_VALORDEBITO       =>",
                                valordebito.toString(), ", ",
                                "UN_VALORCREDITO      =>",
                                valorcredito.toString(), ", ",
                                "UN_TIPOAFECT         =>'", tipoafect, "', ",
                                "UN_NUMEROAFECT       =>",
                                numeroafect.toString(), ", ",
                                "UN_DEBITOEQUIV       =>",
                                debitoequiv.toString(), ", ",
                                "UN_CREDITOEQUIV      =>",
                                creditoequiv.toString(), ", ",
                                "UN_PORCENTAJERET     =>",
                                Double.toString(porcentajeret), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.PR_INSERTAR_DETALLECOMPROBANTE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String porcentajeCuentaBancos(
        String compania,
        int anio,
        String tipo,
        BigInteger numero)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD1.FC_PORCENTAJEBANCOSCUENTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String consolidarCompanias(
        String companiacon,
        int anio)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIACON =>'", companiacon, "', ",
                                    "UN_ANIO        =>", Integer.toString(anio)
            };
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_CONSOLIDARCOMPANIAH",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal consultarSaldoFinalDeCaja(
        String compania,
        Date fecha,
        String cuenta,
        String naturaleza)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CUENTA            =>'", cuenta, "', ",
                                    "UN_NATURALEZA        =>'", naturaleza,
                                    "'" };

            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_SALDOFINALCAJA",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal consultarSaldoFinalCajayBancos(
        String compania,
        Date fecha,
        String cuenta,
        String naturaleza)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CUENTA            =>'", cuenta, "', ",
                                    "UN_NATURALEZA        =>'", naturaleza,
                                    "'" };

            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_SALDOFINALC",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarLibroDiario(
        String compania,
        String tipoInicial,
        String tipoFinal,
        Date fechaInicial,
        Date fechaFinal)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPO_INICIAL      =>'", tipoInicial,
                                    "', ", "UN_TIPO_FINAL        =>'",
                                    tipoFinal, "', ",
                                    "UN_FECHA_INICIAL     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA_FINAL       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY')" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_PREPARARPIVOT_LIBRODIARIO",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String prepararLibroDiario(
        String compania,
        String tipoInicial,
        String tipoFinal,
        Date fechaInicial,
        Date fechaFinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                    "UN_TIPO_INICIAL   =>'", tipoInicial, "', ",
                                    "UN_TIPO_FINAL     =>'", tipoFinal, "', ",
                                    "UN_FECHA_INICIAL     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA_FINAL       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY')"
            };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_PREPARARPIVOT_NVLLIBRO",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String consultarConsecutivosFaltantes(
        String tipo,
        String compania,
        Date fechainicial,
        Date fechafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_TIPO              =>'", tipo, "', ",
                                    "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINICIAL      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicial),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY')" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_CONCATENACOMPROBANTES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String revisarAfectacionesDeCartera(
        String compania,
        int ano,
        Date fechaCorte)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA_CORTE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCorte),
                                    "','DD/MM/YYYY')" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_REVISARAFECTACIONESCARTERA",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String afectarComprobantePresupuestal(
        String compania,
        String tipoafec,
        BigInteger numeroafec,
        int anoafec,
        Date fecha,
        String tipo,
        BigInteger numero,
        String clase,
        int ano,
        String pacproporcionalgiro)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOAFEC          =>'", tipoafec, "', ",
                                    "UN_NUMEROAFEC        =>",
                                    numeroafec.toString(), ", ",
                                    "UN_ANOAFEC           =>",
                                    Integer.toString(anoafec), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'),",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>",
                                    numero.toString(), ", ",
                                    "UN_CLASE             =>'", clase, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PACPROPORCIONALGI =>'",
                                    pacproporcionalgiro, "'" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_AFECTARCOMPROBANTEPPTAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void generarComprobantePresupuestal(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        String numerodoc,
        BigDecimal valorDoc,
        String tipoPptal,
        String cadenaInsertar,
        int cantidad,
        String usuario)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>",
                                    (tipo != null
                                        ? SysmanFunciones.colocarComillas(tipo)
                                        : null),
                                    ", ", "UN_NUMERO            =>",
                                    numero.toString(),
                                    ", ", "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_NUMERODOC         =>'",
                                    numerodoc, "', ", "UN_VALORDOC          =>",
                                    valorDoc.toString(), ", ",
                                    "UN_TIPOPPTAL         =>'",
                                    tipoPptal, "', ",
                                    "UN_CADENAINSERTAR    =>'", cadenaInsertar,
                                    "', ", "UN_CANTIDAD          =>",
                                    Integer.toString(cantidad),
                                    ", ", "UN_USUARIO           =>'", usuario,
                                    "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Es el mismo llamado a generarComprobantePresupuestal
     * pero está diseñado para recibir en el parámetro cadenainsertar
     * una concatenación de clobs y no una cadena de caracteres. 
     */
    @Override
    public void generarComprobantePresupuestalClob(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        String numerodoc,
        BigDecimal valorDoc,
        String tipoPptal,
        String cadenaInsertar,
        int cantidad,
        String usuario)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_TIPO              =>",
                                    (tipo != null
                                        ? SysmanFunciones.colocarComillas(tipo)
                                        : null),
                                    ", ", "UN_NUMERO            =>",
                                    numero.toString(),
                                    ", ", "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TERCERO           =>'", tercero, "', ",
                                    "UN_SUCURSAL          =>'", sucursal, "', ",
                                    "UN_DESCRIPCION       =>'", descripcion,
                                    "', ", "UN_NUMERODOC         =>'",
                                    numerodoc, "', ", "UN_VALORDOC          =>",
                                    valorDoc.toString(), ", ",
                                    "UN_TIPOPPTAL         =>'",
                                    tipoPptal, "', ",
                                    "UN_CADENAINSERTAR    =>", cadenaInsertar,
                                    ", ", "UN_CANTIDAD          =>",
                                    Integer.toString(cantidad),
                                    ", ", "UN_USUARIO           =>'", usuario,
                                    "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public String cargarFlujoEfectivo(
        String compania,
        String cadena,
        String usuario)
                    throws SystemException {
        try {

        	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
        			"UN_CADENA           =>", cadena, ", ",
        			"UN_USUARIO           =>'", usuario, "'" };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD1.FC_CARGAR_FLUJO_EFEC",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
    }
    
	@Override
	public void generarMasivoComPptal(
			String compania,
			String tipo,
			String anio,       
			String mes,        
			String numeroIni,  
			String numeroFin,  
			String usuario)
					throws SystemException {

		String[] parametros = { 
				"UN_COMPANIA          =>'", compania, "', ",
				"UN_TIPO              =>'", tipo, "', ",
				"UN_ANIO              =>", anio, ", ",
				"UN_MES               =>", mes, ", ",
				"UN_NUMERO_INI        =>", numeroIni, ", ",
				"UN_NUMERO_FIN        =>", numeroFin, ", ",
				"UN_USUARIO           =>'", usuario, "'"
		};

		AccionesImp.ejecutarProcedimiento(
				ConectorPool.ESQUEMA_SYSMAN,
				"PCK_CONTABILIDAD1.PR_MASIVA_COM_PPTAL",
				SysmanFunciones.concatenar(parametros)
				);
	}

}