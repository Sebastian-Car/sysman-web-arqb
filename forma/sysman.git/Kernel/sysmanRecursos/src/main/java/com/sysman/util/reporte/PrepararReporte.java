/*-
 * PrepararReporte.java
 *
 * 1.0
 *
 * 29/05/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.reporte;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.ejb.impl.EjbNominaCeroGeneral;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.reporte.enums.PrepararReporteEnums;
import com.sysman.util.reporte.enums.PrepararReporteUrlEnums;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJB;

/**
 * En esta se incluye los reemplazos y parametros de los informes
 *
 * @version 1.0, 29/05/2018
 * @author jgomez
 *
 */
public class PrepararReporte
{

    /**
     * Guarda los datos a reemplazar en la consulta del reporte
     */
    private static HashMap<String, Object> reemplazar = new HashMap<>();
    /**
     * Guarda los datos a parametros que se envian al reporte
     */
    private static HashMap<String, Object> parametros = new HashMap<>();
    /**
     * Variable que guarda los datos de sessión al momento de instanciar y no se tiene session
     */
    private static DatosSesion datosSesion = null;
    /**
     * Variable que guarda el modulo de nomina
     */
    private static String moduloNomina = "6";
    /**
     * guarda el nombre de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaNombre;
    /**
     * guarda el nit de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaNit;
    /**
     * guarda la sigla de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaSigla;
    /**
     * guarda la ciudad de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaCiudad;
    /**
     * guarda el codigo la ciudad de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaCiudadCodigo;
    /**
     * guarda el departamento de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaDepartamento;
    /**
     * guarda el codigo del departamento de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaDepartamentoCodigo;
    /**
     * guarda el pais de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String companiaPais;
    /**
     * guarda el usuario de la compañia sea de las varaiables de sessión o del objeto recibido en el constructor
     */
    private static String usuario;

    /**
     * Constante del parametro del valor del porcentaje a aplicar procesos de retencion
     */
    private static final String VALOR_PORCENTAJE_APLICAR = "VALOR PORCENTAJE APLICAR PROCESO RETEFUENTE";
    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(PrepararReporte.class);
    /**
     * Implementacion del EJB Nomina general para acceder a funciones y/o procedimientos definidos en el paquete PCK_NOMINA
     */
    @EJB
    private static EjbNominaCeroGeneralRemote ejbNominaCeroGeneral = new EjbNominaCeroGeneral();

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private static EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();

    /**
     * Permite utilizar las constantes de idioma
     */
    protected static ResourceBundle idioma;

    /**
     * Constructor utilizado cuando se tiene sesión de jsf inicializada, NO sirve para servicios o APIs
     */
    public PrepararReporte()
    {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        iniciarVariables();
    }

    /**
     * Constructor utilizado desde servicios o APIs, en las cuales se define primero el objeto que simula la sessión
     *
     * @param datos
     */
    public PrepararReporte(DatosSesion datos)
    {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        datosSesion = datos;
        iniciarVariables();
    }

    /**
     * Permite armar el objeto devuelto por los metodos que preparan informes
     *
     * @return RetornoReporte
     */
    private static RetornoReporte generarRetorno()
    {
        RetornoReporte retorno = new RetornoReporte();
        retorno.setParametros(parametros);
        retorno.setReemplazar(reemplazar);
        return retorno;
    }

    /**
     * Permite preparar los parametros del volante de pago para los modulo de nomina, hojas de vida y autoservicio
     *
     * @param compania
     * @param idEmpleadoIni
     * @param idEmpleadoFin
     * @param proceso
     * @param ano
     * @param mes
     * @param periodo
     * @param centroCostoIni
     * @param centroCostoFin
     * @param rutaEncabezado
     * @param rutaPiePagina
     * @return
     * @throws Exception
     */
    public RetornoReporte preparaVolante(String compania,
        int idEmpleadoIni, int idEmpleadoFin, int proceso, int ano, int mes,
        int periodo, String centroCostoIni, String centroCostoFin, String rutaEncabezado, String rutaPiePagina)
                    throws Exception
    {


        reemplazar.put(PrepararReporteEnums.proceso.name()
                        .toLowerCase(), proceso);
        reemplazar.put(PrepararReporteEnums.ano.name()
                        .toLowerCase(), ano);
        reemplazar.put(PrepararReporteEnums.mes.name()
                        .toLowerCase(), mes);
        reemplazar.put(PrepararReporteEnums.periodo.name()
                        .toLowerCase(),
                        periodo);
        reemplazar.put(PrepararReporteEnums.empleadoIni.name(), idEmpleadoIni);
        reemplazar.put(PrepararReporteEnums.empleadoFin.name(), idEmpleadoFin);
        reemplazar.put(PrepararReporteEnums.centroCostoIni.name(),
                        centroCostoIni);
        reemplazar.put(PrepararReporteEnums.centroCostoFin.name(),
                        centroCostoFin);
        parametros.put(PrepararReporteEnums.PR_TITULO.name(),
                        tituloVolante(compania, proceso, ano, mes, periodo));
        parametros.put(PrepararReporteEnums.PR_NOMBREEMPRESA.name(),
                        companiaNombre);
        System.out.println("50.1");
      
        System.out.println("50.5");
        parametros.put("PR_ENCABEZADO",
                        rutaEncabezado);
        parametros.put("PR_PIEPAGINA", rutaPiePagina);
        System.out.println("50.6");
        return generarRetorno();
    }

    /**
     * Permite preparar los parametros del volante de pago para los modulo de nomina, hojas de vida y autoservicio
     *
     * @param compania
     * @param idEmpleadoIni
     * @param idEmpleadoFin
     * @param proceso
     * @param ano
     * @param mes
     * @param periodo
     * @param centroCostoIni
     * @param centroCostoFin
     * @return
     * @throws Exception
     */
    public RetornoReporte preparaVolante(String compania,
        int idEmpleadoIni, int idEmpleadoFin, int proceso, int ano, int mes,
        int periodo, String centroCostoIni, String centroCostoFin)
                    throws Exception
    {


        reemplazar.put(PrepararReporteEnums.proceso.name()
                        .toLowerCase(), proceso);
        reemplazar.put(PrepararReporteEnums.ano.name()
                        .toLowerCase(), ano);
        reemplazar.put(PrepararReporteEnums.mes.name()
                        .toLowerCase(), mes);
        reemplazar.put(PrepararReporteEnums.periodo.name()
                        .toLowerCase(),
                        periodo);
        reemplazar.put(PrepararReporteEnums.empleadoIni.name(), idEmpleadoIni);
        reemplazar.put(PrepararReporteEnums.empleadoFin.name(), idEmpleadoFin);
        reemplazar.put(PrepararReporteEnums.centroCostoIni.name(),
                        centroCostoIni);
        reemplazar.put(PrepararReporteEnums.centroCostoFin.name(),
                        centroCostoFin);
        parametros.put(PrepararReporteEnums.PR_TITULO.name(),
                        tituloVolante(compania, proceso, ano, mes, periodo));
        parametros.put(PrepararReporteEnums.PR_NOMBREEMPRESA.name(),
                        companiaNombre);
        System.out.println("50.1");
        String rutaEncabezado =  "";
        String rutaPiePagina =  "";
        try
        {
            rutaEncabezado = ejbSysmanUtil.consultarParametro(
                            compania,
                            "RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS",
                            moduloNomina, new Date(), false);
            System.out.println("50.2");
            rutaPiePagina = ejbSysmanUtil.consultarParametro(
                            compania,
                            "RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS",
                            moduloNomina, new Date(), false);
            System.out.println("50.3");
        }
        catch (Exception e)
        {
            LOG.error("Error obteniendo las rutas de las imagenes de encabezado y/o pie de página "
                + "->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getStackTrace());
            System.out.println("50.4");
            throw new Exception(e);
        }
        System.out.println("50.5");
        parametros.put("PR_ENCABEZADO",
                        rutaEncabezado);
        parametros.put("PR_PIEPAGINA", rutaPiePagina);
        System.out.println("50.6");
        return generarRetorno();
    }

    /**
     * Genera el titulo necesario para el volante de pago
     *
     * @param compania
     * @param proceso
     * @param ano
     * @param mes
     * @param periodo
     * @return
     * @throws SysmanException
     */
    private String tituloVolante(String compania, int proceso, int ano,
        int mes,
        int periodo) throws SysmanException
    {
        String tituloVolante = "";
        Date fechaFin;
        Date fechaIni;
        try
        {
            fechaFin = ejbNominaCeroGeneral.getFechaPeriodoIniFin(
                            compania,
                            proceso,
                            ano,
                            mes,
                            periodo,
                            false, true);
            fechaIni = ejbNominaCeroGeneral.getFechaPeriodoIniFin(
                            compania,
                            proceso,
                            ano,
                            mes,
                            periodo,
                            true, true);
        }
        catch (SystemException e)
        {
            LOG.error("Error consultando fechas de periodo ->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getCause());
            throw new SysmanException(e);
        }

        try
        {
            tituloVolante = SysmanFunciones.concatenar(
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaIni),
                            " ",
                            idioma.getString("TB_TB3685"),
                            " ",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFin));
        }
        catch (ParseException e)
        {
            LOG.error("Error obteniendo el título del volante ->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getCause());
            throw new SysmanException(e);
        }
        return tituloVolante;
    }

    /**
     * Permite preparar los parametros del certificado de ingresos y retenciones de nomina para los modulo de nomina, hojas de vida y autoservicio
     *
     * @param compania
     * @param ano
     * @param cedula
     * @return
     * @throws SysmanException
     */
    public RetornoReporte prepararCertificadoDian(String compania, int ano,
        String cedulaInicial, String cedulaFinal, Date fechaExpedicion,
        String original)
                    throws SysmanException
    {
        Registro rsAno = null;
        
        String miModulo = datosSesion == null ? SessionUtil.getModulo() : datosSesion.getModulo();
        rsAno = obtenerDatosAnoDian(compania, ano);
        reemplazar.put("anio", ano);
        reemplazar.put("cedulaInicial", cedulaInicial);
        reemplazar.put("cedulaFinal", cedulaFinal);
        reemplazar.put("modulo", datosSesion == null ? SessionUtil.getModulo() : datosSesion.getModulo());
        reemplazar.put("fecha2",
                        SysmanFunciones.formatearFecha(new Date()));
        reemplazar.put("fecha1",
                        SysmanFunciones.formatearFecha(new Date()));
        if (ano < 2019) {
        	parametros.put("PR_NUMERO_FORMATO",
                    obtenerRuta(compania, "Formato220.jpg"));
        	parametros.put("PR_LOGO_MUISCA",
                    obtenerRuta(compania, "logoMuisca.jpg"));
        	parametros.put("PR_LOGO_DIAN", obtenerRuta(compania, "DIAN2013.jpg"));
        }
        else {
        	parametros.put("PR_NUMERO_FORMATO",
                    obtenerRuta(compania, "Formato2202019.jpg"));
        	parametros.put("PR_LOGO_MUISCA",
                    obtenerRuta(compania, "logoMuisca.jpg"));
        	parametros.put("PR_LOGO_DIAN", obtenerRuta(compania, "DIAN2019.jpg"));
        }
        
        parametros.put("PR_FORMS_CERTIFICADOS_ORIGINAL", original);

        parametros.put("PR_CEDULA_DE_QUIEN_FIRMA_CERTIFICADO_DIAN",
                        SysmanFunciones.nvl(rsAno.getCampos().get("CEDULAFIRMACERT_DIAN"), "").toString());

        parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_CERTIFICADO_DIAN",
                        SysmanFunciones.nvl(rsAno.getCampos().get("NOMBREFIRMACERT_DIAN"), "").toString());
        
        try {

        	reemplazar.put("cuentasPagosSolidariosCovid", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 SOLIDARIOS COVID",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosSalariosEE", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 SALARIOS EMOLUMENTOS ECLESIASTICOS",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosHonorarios", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 HONORARIOS",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosServicios", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 SERVICIOS",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosComisiones", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 COMISIONES",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosPrestacionesSociales", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 PRESTACIONES SOCIALES",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosViaticos", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 VIATICOS",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosGastosRepresentacion", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 GASTOS DE REPRESENTACION",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosCompensaciones", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 COMPENSACIONES POR EL TRABAJO ASOCIADO COOPERATIVO",
        							miModulo,
        							new Date(), false),
        					"")
        			));

        	reemplazar.put("cuentasPagosOtros", SysmanFunciones.colocarComillas(
        			SysmanFunciones.nvlStr(ejbSysmanUtil
        					.consultarParametro(compania,
        							"CUENTAS PAGOS 220 OTROS PAGOS",
        							miModulo,
        							new Date(), false),
        					"")
        			));
        	
        	reemplazar.put("filtroNitONombre", "TERCERO.NIT");
        	
        } catch (SystemException e)
        {
        	LOG.error("Error obteniendo parámetros cuentas 220 ->> mensaje ->> {} / causa ->> {}",
        			e.getMessage(),
        			e.getCause());
        	throw new SysmanException(e);
        }
        
        return generarRetorno();
    }

    /**
     * Metodo que devuelve el valor del parametro ingresado por parametro
     *
     * @param compania
     * Codigo de la compania
     * @param nomPar
     * Nombre del parametro a consultar
     * @param validar
     * Valida si trae valor por defecto 0
     * @param fecha
     * Fecha para la cual se va a consultar el parametro
     * @param modulo
     * Modulo desde el cual se va a consultar el parametro
     * @return
     * @throws SysmanException
     */
    public String consultarParametro(String compania, String nomPar,
        boolean validar, Date fecha, String modulo) throws SysmanException
    {
        String valor;
        valor = "1";
        try
        {
            if (validar)
            {
                valor = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                nomPar, modulo,
                                                fecha, true),
                                "0");
            }
            else
            {
                valor = ejbSysmanUtil.consultarParametro(compania,
                                nomPar, modulo, fecha, true);
            }
        }
        catch (SystemException e)
        {
            LOG.error(SysmanFunciones.concatenar("Consultando {}",
                            nomPar, " ->> mensaje ->> {} / causa ->> {}"),
                            e.getMessage(),
                            e.getCause());
            throw new SysmanException(e);
        }

        return valor;

    }

    /**
     * Unico metodo donde se puede incluir variables de sessiones esto se dejo para guardar compatibilidad con lo que estaba acoplado con la session
     */
    private void iniciarVariables()
    {
        if (datosSesion != null)
        {
            companiaNombre = datosSesion.getCompaniaIngreso().getNombre();
            companiaNit = datosSesion.getCompaniaIngreso().getNit();
            companiaSigla = datosSesion.getCompaniaIngreso().getSigla();
            companiaCiudad = datosSesion.getCompaniaIngreso().getCiudad();
            companiaCiudadCodigo = datosSesion.getCompaniaIngreso()
                            .getCodigoCiudad();
            companiaDepartamento = datosSesion.getCompaniaIngreso()
                            .getDepartamento();
            companiaDepartamentoCodigo = datosSesion.getCompaniaIngreso()
                            .getCodigoDepartamento();
            companiaPais = datosSesion.getCompaniaIngreso().getPais();
            usuario = datosSesion.getUser().getCodigo();

        }
        else
        {
            companiaNombre = SessionUtil.getCompaniaIngreso().getNombre();
            companiaNit = SessionUtil.getCompaniaIngreso().getNit();
            companiaSigla = SessionUtil.getCompaniaIngreso().getSigla();
            companiaCiudad = SessionUtil.getCompaniaIngreso().getCiudad();
            companiaCiudadCodigo = SessionUtil.getCompaniaIngreso()
                            .getCodigoCiudad();
            companiaDepartamento = SessionUtil.getCompaniaIngreso()
                            .getDepartamento();
            companiaDepartamentoCodigo = SessionUtil.getCompaniaIngreso()
                            .getCodigoDepartamento();
            companiaPais = SessionUtil.getCompaniaIngreso().getPais();
            usuario = SessionUtil.getUser().getCodigo();

        }
    }

    /**
     * Metodo que devuelve la ruta de la imagen almacenada
     *
     * @param imagen
     * @return URL de la imagen buscada
     * @throws SysmanException
     */
    public String obtenerRuta(String compania, String imagen)
                    throws SysmanException
    {
        String imagenRuta = null;
        Map<String, Object> parametros = new HashMap<>();
        RequestManager requestManager = new RequestManager();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            Registro ruta = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PrepararReporteUrlEnums.URL647
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
            String registroRuta = ruta.getCampos().get("RUTA_IMAGEN")
                            .toString();
            imagenRuta = SysmanFunciones.concatenar(registroRuta.substring(0,
                            registroRuta.lastIndexOf(File.separator) + 1),
                            imagen);

        }
        catch (SystemException e)
        {
            LOG.error(SysmanFunciones.concatenar("Buscando ruta de ", imagen,
                            " => {} causa {}"),
                            e.getMessage(),
                            e.getCause());
            throw new SysmanException(e);
        }

        return imagenRuta;
    }

    /**
     * Llena los parámetros del certificado Dian, de la tabla Año.
     *
     * @param compania
     * @throws SysmanException
     */
    public Registro obtenerDatosAnoDian(String compania, int ano)
                    throws SysmanException
    {

        Map<String, Object> param = new HashMap<>();

        RequestManager requestManager = new RequestManager();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try
        {
            return RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PrepararReporteUrlEnums.URL648
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

        }
        catch (SystemException e)
        {
            LOG.error(SysmanFunciones.concatenar("Consultando datos del año: ", String.valueOf(ano),
                            " => {} causa {}"),
                            e.getMessage(),
                            e.getCause());
            throw new SysmanException(e);
        }

    }

}
