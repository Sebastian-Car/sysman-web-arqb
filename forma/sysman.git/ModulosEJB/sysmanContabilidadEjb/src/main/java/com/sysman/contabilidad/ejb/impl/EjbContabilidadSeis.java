package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadSeisGeneralRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
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
 * 
 */
/**
 * Session Bean implementation class contabilidadSeis
 * 
 * @version 2.0, 10/06/2017, <strong>pespitia</strong>:<br>
 * Implementacion de la funcion SysmanFunciones.concatenar
 */
@Stateless
@LocalBean

public class EjbContabilidadSeis
                implements EjbContabilidadSeisRemote, EjbContabilidadSeisLocal {

    @EJB
    private EjbContabilidadSeisGeneralRemote ejbContabilidadSeisGeneral;

    /**
     * Default constructor.
     */
    public EjbContabilidadSeis() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public int generarEgresos(
        String compania,
        int ano,
        String tipoCpte,
        long comprobante,
        String listanumeroafectar,
        String listaterceroafectar,
        boolean terceroegreso,
        Date fecha,
        String clase,
        String tercero,
        String sucursal,
        BigDecimal valorapagar,
        String usuario,
        int cantidad)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_ANO               =>", Integer.toString(ano),
                             ", ", "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                             "UN_COMPROBANTE       =>",
                             Long.toString(comprobante), ", ",
                             "UN_LISTANUMEROAFECTAR =>'", listanumeroafectar,
                             "', ", "UN_LISTATERCEROAFECTAR =>'",
                             listaterceroafectar, "', ",
                             "UN_TERCEROEGRESO     =>",
                             (terceroegreso ? "-1" : "0"), ", ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ", "UN_CLASE             =>'",
                             clase, "', ", "UN_TERCERO           =>'", tercero,
                             "', ", "UN_SUCURSAL          =>'", sucursal, "', ",
                             "UN_VALORAPAGAR       =>", valorapagar.toString(),
                             ", ", "UN_USUARIO           =>'", usuario, "',",
                             "UN_CANTIDAD          =>",
                             Integer.toString(cantidad), "" };

            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD6.FC_GENERAREGRESOS",
                            SysmanFunciones.concatenar(par),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal calcularValorAGirar(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String clase,
        BigDecimal valoragirar,
        BigDecimal vlrdocumento,
        BigDecimal totaldebito,
        BigDecimal vlrgirardg)
                    throws SystemException {

        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_TIPO              =>'", tipo, "', ",
                         "UN_NUMERO            =>", numero.toString(), ", ",
                         "UN_CLASE             =>'", clase, "', ",
                         "UN_VALORAGIRAR       =>", valoragirar.toString(),
                         ", ", "UN_VLRDOCUMENTO      =>",
                         vlrdocumento.toString(), ", ",
                         "UN_TOTALDEBITO       =>", totaldebito.toString(),
                         ", ", "UN_VLRGIRARDG        =>", vlrgirardg.toString(),
                         "" };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_CALCULAR_VALORAGIRAR",
                        SysmanFunciones.concatenar(par),
                        Types.DECIMAL);
    }

    @Override
    public String generarRtaConciliadosXPlano(
        String compania,
        String cuenta,
        String cadena,
        boolean eliminarpartidas,
        boolean maningpartidasconc,
        boolean validadoc,
        int colvalingreso,
        int colvalcredito,
        int coldocumento,
        int colerror,
        int colfecha,
        Date fecha,
        String formatofecha,
        int filaini,
        String usuario)
                    throws SystemException {

        return ejbContabilidadSeisGeneral.generarRtaConciliadosXPlano(compania,
                        cuenta, cadena, eliminarpartidas,
                        maningpartidasconc, validadoc, colvalingreso,
                        colvalcredito, coldocumento, colerror, colfecha, fecha,
                        formatofecha, filaini, usuario);
    }

    @Override
    public BigDecimal crearTotalListaAfec(
        String compania,
        String listanumeroafectar,
        String clase)
                    throws SystemException {

        StringBuilder builder = new StringBuilder();
        builder.append("UN_COMPANIA => '");
        builder.append(compania);
        builder.append("', ");
        builder.append("UN_LISTANUMEROAFECTAR =>'");
        builder.append(listanumeroafectar);
        builder.append("',");
        builder.append("UN_CLASE => '");
        builder.append(clase);
        builder.append("'");
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_CREAR_TOTAL_LISTAAFEC",
                        builder.toString(),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal crearTotalListaAfecTer(
        String compania,
        String listanumeroafectar,
        String tercero,
        String sucursal,
        String clase)
                    throws SystemException {

        StringBuilder builder = new StringBuilder();
        builder.append("UN_COMPANIA => '");
        builder.append(compania);
        builder.append("', ");
        builder.append("UN_LISTANUMEROAFECTAR =>'");
        builder.append(listanumeroafectar);
        builder.append("',");
        builder.append("UN_TERCERO =>'");
        builder.append(tercero);
        builder.append("',");
        builder.append("UN_SUCURSAL =>'");
        builder.append(sucursal);
        builder.append("',");
        builder.append("UN_CLASE => '");
        builder.append(clase);
        builder.append("'");
        ;

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_CREAR_TOTAL_LISTAAFEC_TER",
                        builder.toString(),
                        Types.DECIMAL);
    }

    @Override
    public boolean traerSaldoCuenta(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        BigDecimal vlrgirar)
                    throws SystemException {
        byte salida;
        String[] par = { "UN_COMPANIA => '", compania, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_TIPO              =>'", tipo, "', ",
                         "UN_NUMERO            =>", numero.toString(), ", ",
                         "UN_VLRGIRAR          =>", vlrgirar.toString(), ""
        };

        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_SALDO_CUENTA_EGRESO",
                        SysmanFunciones.concatenar(par),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void generarIngreso(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        String tercero,
        String sucursal,
        String listanumeroafectar,
        Date fecha,
        String clase,
        String cuenta,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_ANO               =>", Integer.toString(ano),
                             ", ", "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                             "UN_COMPROBANTE       =>", comprobante.toString(),
                             ", ", "UN_TERCERO           =>'", tercero, "', ",
                             "UN_SUCURSAL          =>'", sucursal, "', ",
                             "UN_LISTANUMEROAFECTAR =>'", listanumeroafectar,
                             "', ", "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ", "UN_CLASE             =>'",
                             clase, "', ", "UN_CUENTA            =>'", cuenta,
                             "', ", "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD6.PR_GENERARINGRESO",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
  //INI_7741561_CONTABILIDAD (mrosero)
    @Override
    public void generarIngresoNotasCliente(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        String tercero,
        String sucursal,
        String listanumeroafectar,
        Date fecha,
        String clase,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_ANO               =>", Integer.toString(ano),
                             ", ", "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                             "UN_COMPROBANTE       =>", comprobante.toString(),
                             ", ", "UN_TERCERO           =>'", tercero, "', ",
                             "UN_SUCURSAL          =>'", sucursal, "', ",
                             "UN_LISTANUMEROAFECTAR =>'", listanumeroafectar,
                             "', ", "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ", "UN_CLASE             =>'",
                             clase, "', ", 
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD6.PR_GENERARINGRESONCR",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
  //FIN_7741561_CONTABILIDAD (mrosero)    
    
    @Override
    public void validarChequera(
        String compania,
        int ano,
        String cuenta,
        long chequeini,
        long chequefin)
                    throws SystemException {
        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANO               =>", Integer.toString(ano), ", ",
                         "UN_CUENTA            =>'", cuenta, "', ",
                         "UN_CHEQUEINI         =>", Long.toString(chequeini),
                         ", ", "UN_CHEQUEFIN         =>",
                         Long.toString(chequefin), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.PR_VALIDAR_CHEQUERA",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public void reflejarSaldos(
        String compania,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.PR_REFLEJAR_SALDOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean retencionPorTercero(
        String compania,
        int ano,
        String tipoComprobro,
        BigInteger numeroComprob,
        String tercero,
        String sucursal,
        String valorBase,
        String valor,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = {
                                "UN_COMPANIA           =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPOCOMPROBRO     =>'", tipoComprobro,
                                "', ",
                                "UN_NUMEROCOMPROB     =>",
                                numeroComprob.toString(), ", ",
                                "UN_TERCERO           =>'", tercero, "', ",
                                "UN_SUCURSAL          =>'", sucursal, "', ",
                                "UN_VALORBASE         =>'", valorBase, "', ",
                                "UN_VALOR             =>'", valor, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_RETENCIONPORTERCERO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean actualizarEstadoC(
        String compania,
        int anio,
        int mes,
        String cuenta,
        Date ultimodia,
        String usuario,
        String clases,
        boolean seleccion)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_CUENTA            =>'", cuenta, "', ",
                                    "UN_ULTIMODIA         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    ultimodia),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_CLASES            =>'", clases, "', ",
                                    "UN_SELECCION         =>",
                                    seleccion ? "-1" : "0", ""
            };

            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD6.FC_ESTADOCONCILIADO",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public void generarAnulacion(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        Date fecha,
        String clase,
        String listanumeroafectar,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_ANOACTUAL         =>", Integer.toString(ano),
                             ", ",
                             "UN_TIPOACTUAL        =>'", tipoCpte, "', ",
                             "UN_NUMEROACTUAL      =>", comprobante.toString(),
                             ", ",
                             "UN_FECHA             =>TO_DATE('",
                             SysmanFunciones.convertirAFechaCadena(fecha),
                             "','DD/MM/YYYY'), ",
                             "UN_CLASE             =>'", clase, "', ",
                             "UN_LISTAAAFECTAR =>'", listanumeroafectar, "', ",
                             "UN_USUARIO           =>'", usuario, "'" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD6.PR_GENERARANULACION",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }


    @Override
    public String getPivotPlanContable(
    		  String compania
    		, Date fechaInicial
    		, Date fechaFinal
    	    , String parametro
    	    , String tipo)
                        throws SystemException {
            try {
                String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                                 "UN_FECHAINICIAL      =>TO_DATE('",  SysmanFunciones.convertirAFechaCadena(fechaInicial), "','DD/MM/YYYY HH24:MI:SS'), ",
                                 "UN_FECHAFINAL        =>TO_DATE('",  SysmanFunciones.convertirAFechaCadena(fechaFinal), "','DD/MM/YYYY HH24:MI:SS'), ",
                                 "UN_PARAMETRO         =>'", parametro, "', ",
                                 "UN_TIPO              =>'", tipo, "' " };

                return Acciones.clobToStringSalto(
                                (Clob) AccionesImp.ejecutarFuncion(
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                "PCK_CONTABILIDAD6.FC_PREPPIVOTCONSULTADESCUENTOS",
                                                SysmanFunciones.concatenar(par),
                                                Types.CLOB));
            } catch (SQLException | IOException | ParseException e) {
                throw new SystemException(e);
            }
        }
    
    @Override
    public BigDecimal obtenerValorFacturado(
        String compania,
        String anioCpte,
        String tipoCpte,
        String nroCpte)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
                                "UN_ANOCPTE   =>", anioCpte, ", ",
                                "UN_TIPOCPTE  =>'", tipoCpte, "', ",
                                "UN_NROCPTE   =>", nroCpte};
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_OBTENERVALORFACTURADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }
    

    @Override
    public void cargarmovcontables(
    		String compania, 
    		String cadenah, 
    		String cadenad,
    		int proceso,
    		String usuario)
    				throws SystemException {
    	String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
    			"UN_CADENAH   =>" , cadenah , " , ",
    			"UN_CADENAD   =>" , cadenad , " , ",
                "UN_PROCESO   =>" ,  Integer.toString(proceso) ," , ",
    			"UN_USUARIO   =>'", usuario , "' "
    	};
    	AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                "PCK_CONTABILIDAD6.PR_CARGAR_MOV_CONTABLES",
                SysmanFunciones.concatenar(parametros));


    }
    
    @Override
    public void generarIngresoOST(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        String tercero,
        String sucursal,
        String listanumeroafectar,
        Date fecha,
        String usuario)
                    throws SystemException {
        try {
            String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                             "UN_ANO               =>", Integer.toString(ano),", ", 
                             "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                             "UN_COMPROBANTE       =>", comprobante.toString(),", ", 
                             "UN_TERCERO           =>'", tercero, "', ",
                             "UN_SUCURSAL          =>'", sucursal, "', ",
                             "UN_LISTANUMEROAFECTAR =>'", listanumeroafectar, "', ", 
                             "UN_FECHA             =>TO_DATE('",
                             	SysmanFunciones.convertirAFechaCadena(fecha), "','DD/MM/YYYY'), ", 
                             "UN_USUARIO           =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD6.PR_GENERARINGRESOOST",
                            SysmanFunciones.concatenar(par));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public void calcularValorNeto(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        int valoragirar,
        int vlrdocumento)
                    throws SystemException {

        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", Integer.toString(anio),
                         ", ", "UN_TIPO              =>'", tipo, "', ",
                         "UN_NUMERO            =>", numero.toString(), ", ",
                         "UN_VLRDOCUMENTO       =>", Integer.toString(vlrdocumento),
                         ", ", "UN_VLRGIRARDG      =>",Integer.toString(valoragirar), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.PR_CALCULAR_VALORAGIRAR",
                        SysmanFunciones.concatenar(par));
    }
    
    @Override
    public void calcularValorNeto(
        String compania,
        String anio,
        String tipo,
        String numero,
        String valoragirar,
        String vlrdocumento)
                    throws SystemException {

        String[] par = { "UN_COMPANIA          =>'", compania, "', ",
                         "UN_ANIO              =>", anio,
                         ", ", "UN_TIPO              =>'", tipo, "', ",
                         "UN_NUMERO            =>", numero, ", ",
                         "UN_VLRDOCUMENTO       =>", vlrdocumento,
                         ", ", "UN_VLRGIRARDG      =>",valoragirar, "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.PR_CALCULAR_VALORAGIRAR",
                        SysmanFunciones.concatenar(par));
    }

    @Override
    public String validarAuxiliaresEgresos(
        String compania,
        int ano,
        String tipoCpte, 
        long comprobante,
        double valorBanco,
        String referencia,
        String centroCosto,
        String listanumeroafectar
        )
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				            		"UN_ANO               =>",
				                    Integer.toString(ano), ", ",
				                    "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
				                    "UN_COMPROBANTE       =>",
		                             Long.toString(comprobante), ", ",
		                             "UN_VALORBANCO               =>", Double.toString(valorBanco), ", ",
		                             "UN_REFERENCIA         =>'", referencia, "', ",
		                             "UN_CENTRO_COSTO         =>'", centroCosto, "', ",
		                             "UN_LISTANUMEROAFECTAR =>'", listanumeroafectar,"'"
            };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIDAD6.FC_VALIDAR_AUXILIARES_EGRESOS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
    @Override
    public BigDecimal validarcsaldoafectado(
    		 String compania,
    	        String anioCpte,
    	        String tipoCpte,
    	        String nroCpte,
    	        String cvalorcredito,
    	        String cvalordebito,
    	        String numeroComp,
    	        String tipoComp,
    	        String anio,
    	        String cuenta)
    	                    throws SystemException {

    	        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
    	                                "UN_ANOCPTE   =>", anioCpte, ", ",
    	                                "UN_TIPOCPTE  =>'", tipoCpte, "', ",
    	                                "UN_NROCPTE  =>", nroCpte, ", ",
    	                                "UN_VALORCREDITO_NUEVO  =>", cvalorcredito, ", ", 
    	                                "UN_VALORDEBITO_NUEVO  =>", cvalordebito, ", ", 
    	                                "UN_NROCOMPROBANTE_NUEVO  =>", numeroComp, ", ",
    	                                "UN_TIPOCPTE_NUEVO  =>'", tipoComp, "', ",
    	                                "UN_ANONUEVO   =>", anio, ", ",
    	                                "UN_CUENTA   =>", cuenta};
    	        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_VALIDARCSALDOAFECTADO", 
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }
    @Override
    public void actualizarsaldoafect(
    		 String compania,
    	        String anioCpte,
    	        String tipoCpte,
    	        String nroCpte,
    	        String cvalorcredito,
    	        String cvalordebito,
    	        String numeroComp,
    	        String tipoComp,
    	        String anio,
    	        String cuenta,
    	        String accion,
    	        String consecutivo)
    	                    throws SystemException {

    	        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
    	                                "UN_ANOCPTE   =>", anioCpte, ", ",
    	                                "UN_TIPOCPTE  =>'", tipoCpte, "', ",
    	                                "UN_NROCPTE  =>", nroCpte, ", ",
    	                                "UN_VALORCREDITO_NUEVO  =>", cvalorcredito, ", ", 
    	                                "UN_VALORDEBITO_NUEVO  =>", cvalordebito, ", ", 
    	                                "UN_NROCOMPROBANTE_NUEVO  =>", numeroComp, ", ",
    	                                "UN_TIPOCPTE_NUEVO  =>'", tipoComp, "', ",
    	                                "UN_ANONUEVO   =>", anio, ", ",
    	                                "UN_CUENTA   =>", cuenta, ", ",
    	                                "UN_ACCION   =>", accion, ", ",
    	                                "UN_CONSECUTIVO  =>", consecutivo};
    	       AccionesImp.ejecutarProcedimiento(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.PR_ACTUALIZARSALDOAFECT", 
                        SysmanFunciones.concatenar(parametros));
                       
     }
    
    @Override
    public void actualizarsaldopj(
    		 String compania,
    	        String cuenta,
    	        String valordebito,
    	        String numeroProceso)
    	                    throws SystemException {

    	        String[] parametros = { "UN_COMPANIA  =>'", compania, "', ",
    	                                "UN_CUENTA   =>", cuenta, ", ",
    	                                "UN_VALORDEBITO  =>", valordebito, ", ",
    	                                "UN_NUMEROPROCESO   =>", numeroProceso};
    	       AccionesImp.ejecutarProcedimiento(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.PR_ACTUALIZARSALDOPJ", 
                        SysmanFunciones.concatenar(parametros));
                       
     }
    
    @Override
    public String validarcuentas(
    		String compania,
            int ano,
            String listacuentas
            )
                        throws SystemException {
            try {
            	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO               =>", Integer.toString(ano), ", ",
                        "UN_LISTACODIGOS      =>'", listacuentas, "'" };


                return Acciones.clobToStringSalto(
                		(Clob) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD6.FC_VAL_CTAS_AFEC",
                        SysmanFunciones.concatenar(parametros),
                        Types.CLOB));
            }
            catch (Exception e) {
                throw new SystemException(e);
            }
        }
    
    @Override
    public void generarAnticipo(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        String tercero,
        String sucursal,
        String listaFacturas,
        String listaAnticipos,
        Date fecha,
        String clase,
        BigDecimal valor,
        String usuario
    ) throws SystemException {
        try {
            String[] par = {
                "UN_COMPANIA          =>'", compania, "', ",
                "UN_ANO               =>", Integer.toString(ano), ", ",
                "UN_TIPO_CPTE         =>'", tipoCpte, "', ",
                "UN_COMPROBANTE       =>", comprobante.toString(), ", ",
                "UN_TERCERO           =>'", tercero, "', ",
                "UN_SUCURSAL          =>'", sucursal, "', ",
                "UN_LISTAFACTURAS     =>'", listaFacturas, "', ",
                "UN_LISTAANTICIPOS    =>'", listaAnticipos, "', ",
                "UN_FECHA             =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fecha), "','DD/MM/YYYY'), ",
                "UN_CLASE             =>'", clase, "', ",
                "UN_VALOR             =>", valor.toString(), ", ",
                "UN_USUARIO           =>'", usuario, "'"
            };

            AccionesImp.ejecutarProcedimiento(
                ConectorPool.ESQUEMA_SYSMAN,
                "PCK_CONTABILIDAD6.PR_GENERARANTICIPO",
                SysmanFunciones.concatenar(par)
            );
        } catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    
    @Override
    public void generarADC(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        Date fecha,
        String clase,
        String listaAfectar,
        String usuario
    ) throws SystemException {
        try {
            String[] par = {
                "UN_COMPANIA          =>'", compania, "', ",
                "UN_ANIO              =>", Integer.toString(ano), ", ",
                "UN_TIPO              =>'", tipoCpte, "', ",
                "UN_NUMERO            =>", comprobante.toString(), ", ",
                "UN_FECHA             =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fecha), "','DD/MM/YYYY'), ",
                "UN_CLASE             =>'", clase, "', ",
                "UN_LISTAAAFECTAR     =>'", listaAfectar, "', ",
                "UN_USUARIO           =>'", usuario, "'"
            };

            AccionesImp.ejecutarProcedimiento(
                ConectorPool.ESQUEMA_SYSMAN,
                "PCK_CONTABILIDAD6.PR_GENERAR_ADC",
                SysmanFunciones.concatenar(par)
            );
        } catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    
    @Override
    public BigDecimal validarSaldoAuxCxp(
    		String compania,
    		int ano,
    		String tipoCpte, 
    		long comprobante,
    		String referencia,
    		String centroCosto,
    		String listanumeroafectar
    		)
    				throws SystemException {

    	String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
    			"UN_ANO               =>", Integer.toString(ano), ", ",
    			"UN_TIPO_CPTE         =>'", tipoCpte, "', ",
    			"UN_COMPROBANTE       =>", Long.toString(comprobante), ", ",
    			"UN_REFERENCIA         =>'", referencia, "', ",
    			"UN_CENTRO_COSTO         =>'", centroCosto, "', ",
    			"UN_LISTANUMEROAFECTAR =>'", listanumeroafectar,"'"
    	};

    	return (BigDecimal) AccionesImp.ejecutarFuncion(
    			ConectorPool.ESQUEMA_SYSMAN,
    			"PCK_CONTABILIDAD6.FC_SALDO_AUX_CXP",
    			SysmanFunciones.concatenar(parametros),
    			Types.DECIMAL);

    } 


 }

