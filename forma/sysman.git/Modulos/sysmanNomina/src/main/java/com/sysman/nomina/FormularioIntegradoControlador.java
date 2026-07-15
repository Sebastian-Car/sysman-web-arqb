package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCincoRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.FormularioIntegradoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 11/12/2015
 * @author amonroy
 * @version 1.1, 17/03/2017 Ajustes de buenas practicas de
 * programacion sugeridas por la herramienta SonarLint
 *
 * -- Modificado por lcortes 21,22/03/2017. Se asiganan a textos en
 * bean los textos que se encontraban en el controlador.
 * 
 * @modified jguerrero
 * @version 2. 06/10/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class FormularioIntegradoControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "INTEGRADO_" en el formulario, almacena el
     * texto "INTEGRADO_"
     */
    private final String cIntegrado;

    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE en el formulario, almacena el texto
     * NOMBRE
     */
    private final String cNombre;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "_INT_" en el formulario, almacena el
     * texto "_INT_"
     */
    private final String cInt;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "anioNomina" en el formulario, almacena el
     * texto "anioNomina"
     */
    private final String cAnioNomina;
    private final String cIdDeEmpleado;
    private String periodoNomina;
    private String anioNomina;
    private String mesNomina;
    private String procesoNomina;
    private String modulo;
    private String nitCompania;
    private String periodoRetro;
    private String planilla;
    private String estructura;
    private String anio;
    private String mes;
    private String tipoLiquidacion;
    private String numRadicacion;
    private String claseAportante;
    private String encabezado;
    private String numCorreccion;
    private int empleado;
    private String nombreEmpleado;
    private String cedulaEmpleado;
    private int orden;
    private boolean mostrarRetroactivo;
    private boolean correccion;
    private Date fechaCorreccion;
    private Date fechaAuto;
    private List<Registro> listatipoPlanilla;
    private List<Registro> listaEstructura;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaCbLiquidacion;
    private List<Registro> listaPeriodoRetro;
    private RegistroDataModelImpl listaEmpleado;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;
    @EJB
    private EjbNominaCincoRemote ejbNominaCinco;
    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;

    /**
     * Creates a new instance of FormularioIntegradoControlador
     */
    public FormularioIntegradoControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cIntegrado = "INTEGRADO_";
        cIdDeEmpleado = "ID_DE_EMPLEADO";

        cNombre = GeneralParameterEnum.NOMBRE.getName();

        cInt = "_INT_";
        cAnioNomina = "anioNomina";
        empleado = 0;
        orden = 1;
        try {
            numFormulario = GeneralCodigoFormaEnum.FORMULARIO_INTEGRADO_CONTROLADOR
                            .getCodigo();
            periodoNomina = SessionUtil.getSessionVar("periodoNomina")
                            .toString();
            anioNomina = SessionUtil.getSessionVar(cAnioNomina).toString();
            mesNomina = SessionUtil.getSessionVar("mesNomina").toString();
            procesoNomina = SessionUtil.getSessionVar("procesoNomina")
                            .toString();
            modulo = SessionUtil.getModulo();
            if (estructura == null) {
                estructura = "2388";
            }
            nitCompania = SessionUtil.getCompaniaIngreso().getNit();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FormularioIntegradoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        encabezado = SysmanFunciones.concatenar(idioma.getString("TG_ANIO3"),
                        " ",
                        String.valueOf(SessionUtil.getSessionVar(cAnioNomina)),
                        " - Mes ",
                        String.valueOf(SessionUtil
                                        .getSessionVar("nombreMesNomina")),
                        " - Periodo ",
                        String.valueOf(SessionUtil
                                        .getSessionVar("nombrePeriodoNomina")));
        anio = anioNomina;
        mes = mesNomina;

        try {
            fechaAuto = SysmanFunciones.ultimoDiaDate(
                            SysmanFunciones.convertirAFecha(SysmanFunciones
                                            .concatenar("01/", mesNomina, "/",
                                                            anioNomina)));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaCbLiquidacion();
        cargarListaTipoPlanilla();
        cargarlistaEstructura();
        if (correccion) {
            cargarListaAno1();
            cargarListaMes1();
            cargarListaPeriodoRetro();
        }
        fijarTipoAportante();
        cargarListaEmpleado();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void fijarTipoAportante() {
        claseAportante = "";
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            ejbNominaUno.getParametroNomina(compania, 32));

            Registro miRegistro = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FormularioIntegradoControladorUrlEnum.URL9099
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
            if (miRegistro != null) {
                claseAportante = miRegistro.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString() == null
                                    ? ""
                                    : miRegistro.getCampos().get(
                                                    GeneralParameterEnum.NOMBRE
                                                                    .getName())
                                                    .toString();
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodoRetro() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        SessionUtil.getSessionVar("procesoNomina"));
        param.put(GeneralParameterEnum.ANO.getName(),
                        SessionUtil.getSessionVar(cAnioNomina));
        param.put(GeneralParameterEnum.MES.getName(),
                        SessionUtil.getSessionVar("mesNomina"));

        try {
            listaPeriodoRetro = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FormularioIntegradoControladorUrlEnum.URL9097
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 471031 COMPANIA PROCESO ANO MES
    }

    public void cargarListaTipoPlanilla() {
        if (tipoLiquidacion == null) {
            listatipoPlanilla = null;
        }
        else {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("TIPO", tipoLiquidacion);
            try {
                listatipoPlanilla = RegistroConverter
                                .toListRegistro(requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FormularioIntegradoControladorUrlEnum.URL9096
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            if (tipoLiquidacion == "1") {
                planilla = "E";
            }
            if (tipoLiquidacion == "2") {
                planilla = "N";
            }
            if (tipoLiquidacion == "3") {
                planilla = "P";
            }

        }

    }

    public void cargarlistaEstructura() {
        if (tipoLiquidacion == null) {
            listaEstructura = null;
            estructura = "";
        }
        else {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("TIPO", tipoLiquidacion);
            try {
                listaEstructura = RegistroConverter
                                .toListRegistro(requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FormularioIntegradoControladorUrlEnum.URL9094
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FormularioIntegradoControladorUrlEnum.URL22108
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 4001
    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaMes1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FormularioIntegradoControladorUrlEnum.URL22663
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 471030
    }

    public void cargarListaEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormularioIntegradoControladorUrlEnum.URL9095
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cIdDeEmpleado);
    }

    /**
     * Consulta y retorna el valor del parametro "TIPO DE NOMINA
     * ACTIVOS O PENSIONADOS"
     *
     * @return el valor que posee el parametro en la base de datos
     */
    private String obtenerParametro() {
        String liquidacionPensionados = null;

        try {
            liquidacionPensionados = ejbSysmanUtil.consultarParametro(compania,
                            "TIPO DE NOMINA ACTIVOS O PENSIONADOS",
                            SessionUtil.getModulo(), new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return liquidacionPensionados;
    }

    /**
     * Compara el valor de la cadena de entrada con la palabra
     * "PENSIONADOS"
     *
     * @param liquidacionPensionados
     * @return si los valores que se comparan son iguales
     */
    private boolean compararPensionados(String liquidacionPensionados) {
        return (liquidacionPensionados != null)
            && "PENSIONADOS".equalsIgnoreCase(liquidacionPensionados);
    }

    public void cargarListaCbLiquidacion() {
        correccion = false;
        listaCbLiquidacion = new ArrayList<>();
        int contador = 0;
        HashMap<String, Object> opciones = new HashMap<>();
        String liquidacionPensionados = obtenerParametro();
        if (compararPensionados(liquidacionPensionados)) {
            contador++;
            opciones = new HashMap<>();
            opciones.put(cCodigo, "3");
            opciones.put(cNombre, idioma.getString("TB_TB2941"));
            listaCbLiquidacion.add(new Registro(contador, opciones));
            tipoLiquidacion = "3";
        }
        else if (esPeriodoDiferenciasRetroactivo()) {
            nitCompania = nitCompania.replace(".", "");
            nitCompania = nitCompania.replace("-", "");
            nitCompania = nitCompania.replace(" ", "");
            nitCompania = nitCompania.substring(0, 9);
            contador++;
            opciones = new HashMap<>();
            opciones.put(cCodigo, "2");
            opciones.put(cNombre, idioma.getString("TB_TB2942"));
            listaCbLiquidacion.add(new Registro(contador, opciones));
            tipoLiquidacion = "2";
            correccion = true;
        }
        else {
            contador++;
            opciones.put(cCodigo, "1");
            opciones.put(cNombre, idioma.getString("TB_TB2940"));
            listaCbLiquidacion.add(new Registro(contador, opciones));
            tipoLiquidacion = "1";
        }

        // sdfsdf
    }

    private boolean esPeriodoDiferenciasRetroactivo() {
        boolean respuesta = false;
        Registro registro = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        SessionUtil.getSessionVar("procesoNomina"));
        param.put(GeneralParameterEnum.ANO.getName(),
                        SessionUtil.getSessionVar(cAnioNomina));
        param.put(GeneralParameterEnum.MES.getName(),
                        SessionUtil.getSessionVar("mesNomina"));
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        SessionUtil.getSessionVar("periodoNomina"));

        try {
            registro = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FormularioIntegradoControladorUrlEnum.URL9093
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((registro != null)
            && (boolean) registro.getCampos().get("DIFERENCIASRETROACTIVO")) {
            respuesta = true;
        }
        else {
            respuesta = false;
        }
        return respuesta;
        // 471074 COMPANIA ID_DE_PROCESO ANO MES PERIODO
    }

    /**
     * Define el valor a enviar en el parametro UN_NUMCORRECCION de la
     * funcion PR_SISTEMAINTEGRADOELECTRONICO
     *
     * @return valor a enviar en el parametro
     */
    private String armarParametroNumCorreccion() {
        return correccion ? numCorreccion : null;
    }

    /**
     * Define el valor a enviar en el parametro UN_FECHACORRECCION de
     * la funcion PR_SISTEMAINTEGRADOELECTRONICO
     *
     * @return valor a enviar en el parametro
     */
    private Date armarParametroFechaCorreccion() {
        return correccion ? fechaCorreccion : null;
    }

    /**
     * Define el valor a enviar en el parametro UN_ANIOCORRECCION de
     * la funcion PR_SISTEMAINTEGRADOELECTRONICO
     *
     * @return valor a enviar en el parametro
     */
    private int armarParametroAnioCorreccion() {
        if (correccion && (anio != null)) {
            return Integer.parseInt(anio);
        }
        else {
            return 0;
        }

    }

    /**
     * Define el valor a enviar en el parametro UN_MESCORRECCION de la
     * funcion PR_SISTEMAINTEGRADOELECTRONICO
     *
     * @return valor a enviar en el parametro
     */
    private int armarParametroMesCorreccion() {
        if (correccion && (mes != null)) {
            return Integer.parseInt(mes);
        }
        else {
            return 0;
        }

    }

    /**
     * Define el valor a enviar en el parametro UN_PERIODORETRO de la
     * funcion PR_SISTEMAINTEGRADOELECTRONICO
     *
     * @return valor a enviar en el parametro
     */
    private int armarParametroPeriodoRetro() {
        if (correccion && (periodoRetro != null)) {
            return Integer.parseInt(periodoRetro);
        }
        else {
            return 0;
        }

    }

    /**
     * Realiza las validaciones iniciales de campos vacios para
     * tipoLiquidacion, numRadicacion, planilla, estructura
     *
     * @return verdadero si algunos de los campos estï¿½ vacio o nulo
     */
    private boolean oprimirGenerarValidacion() {
        boolean respuesta = false;
        if (SysmanFunciones.validarVariableVacio(tipoLiquidacion)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2574"));
            respuesta = true;
        }
        if (SysmanFunciones.validarVariableVacio(planilla)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2576"));
            respuesta = true;
        }
        if (SysmanFunciones.validarVariableVacio(estructura)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2577"));
            respuesta = true;
        }
        return respuesta;
    }

    /**
     * Realiza las validaciones iniciales de campos vacios para
     * numCorreccion, fechaCorreccion, anio, mes
     *
     * @return verdadero si algunos de los campos estï¿½ vacio o nulo
     */
    private boolean validarCorreccion() {
        boolean respuesta = false;
        if (SysmanFunciones.validarVariableVacio(numCorreccion)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2578"));
            respuesta = true;
        }
        if (fechaCorreccion == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2579"));
            respuesta = true;
        }
        if (SysmanFunciones.validarVariableVacio(periodoRetro)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2582"));
            respuesta = true;
        }

        return respuesta;
    }

    /**
     * Realiza la validacion inicial de campos obligatorios para
     * realizar el proceso "Generar Disco"
     *
     * @return verdadero si existen campos vacios necesarios para el
     * proceso de "Generar Disco"
     */
    private boolean oprimirCmdGenerarValidacionInicial() {
        boolean vacios = false;
        if (oprimirGenerarValidacion() || (correccion && validarCorreccion())) {
            vacios = true;
        }
        if (fechaAuto == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2583"));
            vacios = true;
        }
        if(periodoNomina.equals("7")) {
        	JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4447"));
        	vacios = true;
        }
        
        return vacios;
    }

    public void oprimirCmdGenerar() {
        archivoDescarga = null;

        // <CODIGO_DESARROLLADO>
        if (oprimirCmdGenerarValidacionInicial()) {
            return;
        }
        try {

            String[] nombresArchivos = new String[2];
            String[] discoFinal;
            ByteArrayInputStream in;
            ByteArrayInputStream inIntegrado;

            String respuesta = null;

            if ("1".equals(tipoLiquidacion)) {
                respuesta = ejbNominaCinco.generarDisco(compania,
                                Integer.parseInt(tipoLiquidacion),
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina), estructura,
                                planilla, correccion,
                                armarParametroNumCorreccion(),
                                armarParametroFechaCorreccion(),
                                armarParametroAnioCorreccion(),
                                armarParametroMesCorreccion(), nitCompania,
                                correccion, armarParametroPeriodoRetro(),
                                fechaAuto,
                                numRadicacion, orden, empleado,
                                SessionUtil.getUser().getCodigo());

                nombresArchivos[0] = SysmanFunciones.concatenar(compania, cInt,
                                SysmanFunciones.nvlStr(anio, anioNomina),
                                SysmanFunciones.strZero(SysmanFunciones
                                                .nvlStr(mes, mesNomina), 2),
                                "_", estructura);
                nombresArchivos[1] = SysmanFunciones.concatenar(cIntegrado,
                                compania, "_",
                                SysmanFunciones.nvlStr(anio, anioNomina),
                                SysmanFunciones.strZero(SysmanFunciones
                                                .nvlStr(mes, mesNomina), 2));

            }
            else if ("3".equals(tipoLiquidacion)) {
                respuesta = ejbNominaCinco.generarDisco(compania,
                                Integer.parseInt(tipoLiquidacion),
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina), estructura,
                                planilla, false, null, null, 0, 0, nitCompania,
                                false, 0, null, null, orden, empleado,
                                SessionUtil.getUser().getCodigo());

                nombresArchivos[0] = SysmanFunciones.concatenar(compania, cInt,
                                SysmanFunciones.nvlStr(anio, anioNomina),
                                SysmanFunciones.strZero(SysmanFunciones
                                                .nvlStr(mes, mesNomina), 2),
                                "_P_", estructura);

            }
            else if ("2".equals(tipoLiquidacion)) {

                respuesta = ejbNominaCinco.generarDisco(compania,
                                Integer.parseInt(tipoLiquidacion),
                                Integer.parseInt(procesoNomina),
                                Integer.parseInt(anioNomina),
                                Integer.parseInt(mesNomina),
                                Integer.parseInt(periodoNomina), estructura,
                                planilla, correccion,
                                armarParametroNumCorreccion(),
                                armarParametroFechaCorreccion(),
                                Integer.parseInt(anio),
                                Integer.parseInt(mes), nitCompania, correccion,
                                armarParametroPeriodoRetro(), fechaAuto,
                                numRadicacion, orden, empleado,
                                SessionUtil.getUser().getCodigo());

                nombresArchivos[0] = SysmanFunciones.concatenar(compania, "_",
                                nitCompania, cInt,
                                SysmanFunciones.nvlStr(anio, anioNomina),
                                SysmanFunciones.strZero(SysmanFunciones
                                                .nvlStr(mes, mesNomina), 2),
                                "_RET");

                nombresArchivos[1] = SysmanFunciones.concatenar(cIntegrado,
                                compania, "_", nitCompania, "_",
                                SysmanFunciones.nvlStr(anio, anioNomina),
                                SysmanFunciones.strZero(SysmanFunciones
                                                .nvlStr(mes, mesNomina), 2),
                                "_RET");

            }

            if (respuesta != null) {
                discoFinal = respuesta.split("<siguiente>");
                in = JsfUtil.serializarPlano(discoFinal[0]);
                inIntegrado = JsfUtil.serializarPlano(discoFinal[1]);
                if ("3".equals(tipoLiquidacion)) {
                    ByteArrayInputStream[] listaArchivosp = { in };
                    archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                    listaArchivosp, nombresArchivos);
                }
                else {
                    ByteArrayInputStream[] listaArchivos = { in, inIntegrado };
                    archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                    listaArchivos, nombresArchivos);
                }

            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirrevision() {
        archivoDescarga = null;
        // <CODIGO_DESARROLLADO>
        try {
            int erroresEncontrados = Integer.parseInt(ejbNominaSeis
                            .inconsistenciaSOI(compania,
                                            Integer.parseInt(anioNomina),
                                            Integer.parseInt(mesNomina),
                                            Integer.parseInt(periodoNomina),
                                            Integer.parseInt(procesoNomina),
                                            SessionUtil.getUser().getCodigo())
                            .toString());

            if (erroresEncontrados != 0) {
                generarInforme();
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2588"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Genera el reporte de Liquidacion y envï¿½a los parï¿½metros
     * definidos en el reporte 000070ReporteLiquidacion
     *
     */
    private void generarInforme() {
        try {
            String nombreReporte = "000070ReporteLiquidacion";
            String strSql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo),
                            new HashMap<String, Object>());
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarIndCorreccion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCbLiquidacion() {
        correccion = false;
        try {
            if ((tipoLiquidacion != null) && "1".equals(tipoLiquidacion)) {
                String participacionesPila;
                participacionesPila = ejbSysmanUtil.consultarParametro(compania,
                                "MANEJA SISTEMA GENERAL DE PARTICIPACIONES PILA",
                                SessionUtil.getModulo(), new Date(), true);

                if ((participacionesPila != null)
                    && "SI".equalsIgnoreCase(participacionesPila)) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2589"));
                }

            }
            else if ((tipoLiquidacion != null) && "2".equals(tipoLiquidacion)) {
                correccion = false;
            }
            cargarListaTipoPlanilla();
            cargarlistaEstructura();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getPlanilla() {
        return planilla;
    }

    public void setPlanilla(String planilla) {
        this.planilla = planilla;
    }

    public String getEstructura() {
        return estructura;
    }

    public void setEstructura(String estructura) {
        this.estructura = estructura;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(String tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public String getNumRadicacion() {
        return numRadicacion;
    }

    public void setNumRadicacion(String numRadicacion) {
        this.numRadicacion = numRadicacion;
    }

    public Date getFechaCorreccion() {
        return fechaCorreccion;
    }

    public void setFechaCorreccion(Date fechaCorreccion) {
        this.fechaCorreccion = fechaCorreccion;
    }

    public String getClaseAportante() {
        return claseAportante;
    }

    public void setClaseAportante(String claseAportante) {
        this.claseAportante = claseAportante;
    }

    public List<Registro> getListatipoPlanilla() {
        return listatipoPlanilla;
    }

    public void setListatipoPlanilla(List<Registro> listatipoPlanilla) {
        this.listatipoPlanilla = listatipoPlanilla;
    }

    public List<Registro> getListaEstructura() {
        return listaEstructura;
    }

    public void setListaEstructura(List<Registro> listaEstructura) {
        this.listaEstructura = listaEstructura;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaCbLiquidacion() {
        return listaCbLiquidacion;
    }

    public void setListaCbLiquidacion(List<Registro> listaCbLiquidacion) {
        this.listaCbLiquidacion = listaCbLiquidacion;
    }

    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isCorreccion() {
        return correccion;
    }

    public String getPeriodoRetro() {
        return periodoRetro;
    }

    public void setPeriodoRetro(String periodoRetro) {
        this.periodoRetro = periodoRetro;
    }

    public List<Registro> getListaPeriodoRetro() {
        return listaPeriodoRetro;
    }

    public void setListaPeriodoRetro(List<Registro> listaPeriodoRetro) {
        this.listaPeriodoRetro = listaPeriodoRetro;
    }

    public String getNumCorreccion() {
        return numCorreccion;
    }

    public void setNumCorreccion(String numCorreccion) {
        this.numCorreccion = numCorreccion;
    }

    public Date getFechaAuto() {
        return fechaAuto;
    }

    public void setFechaAuto(Date fechaAuto) {
        this.fechaAuto = fechaAuto;
    }

    public boolean isMostrarRetroactivo() {
        return mostrarRetroactivo;
    }

    public void setMostrarRetroactivo(boolean mostrarRetroactivo) {
        this.mostrarRetroactivo = mostrarRetroactivo;
    }

    public int getEmpleado() {
        return empleado;
    }

    public void setEmpleado(int empleado) {
        this.empleado = empleado;
    }

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = Integer.parseInt(SysmanFunciones
                        .nvl(registroAux.getCampos().get(cIdDeEmpleado), "0")
                        .toString());
        nombreEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                        .toString();
        cedulaEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_DCTO"), "")
                        .toString();
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCedulaEmpleado() {
        return cedulaEmpleado;
    }

    public void setCedulaEmpleado(String cedulaEmpleado) {
        this.cedulaEmpleado = cedulaEmpleado;
    }
}
