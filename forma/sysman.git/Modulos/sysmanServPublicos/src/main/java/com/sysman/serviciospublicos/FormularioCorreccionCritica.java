/*
 * FormularioCorreccionCritica.java
 *
 * 1.0
 *
 * 23/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyaca. All rights
 * reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FormularioCorreccionCriticaEnum;
import com.sysman.serviciospublicos.enums.FormularioCorreccionCriticaUrlEnum;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador de la forma
 * formulariocorreccioncritica, llamada desde Panel
 * Principal/Facturacion de Servicios Publicos/Novedades/Correccion de
 * Critica/Boton Aceptar, y muestra los datos de la correccion de
 * critica en el subformulario formulariocorreccioncriticalista. Los
 * subformularios Formulariocorreccionhistoria y
 * Formularioproblemascorreccioncritica se desprenden del primero al
 * actualizarse segun la fila seleccionada.
 *
 * @version 1.0, 23/09/2016
 * @author dmaldonado
 *
 * @version 2.0, 23/05/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejb
 */
@ManagedBean
@ViewScoped
public class FormularioCorreccionCritica extends BeanBaseDatosAcmeImpl {
    /**
     * variable con el codigo de la compañia que tiene la sesion
     */
    private final String compania;
    /**
     * variable con el codigo del modulo que se esta trabajando
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que es llamado desde la forma para descargar el
     * reporte 800060ExportaCritica.
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaNOMBRE;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * grilla para el subformulario problema
     */
    private RegistroDataModelImpl listaProblema;
    /**
     * datos para el formulario en modal problema
     */
    private RegistroDataModelImpl listaProblemaE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModel listaFormulariocorreccioncriticalista;
    private List<Registro> listaFormulariocorreccionhistoria;
    private List<Registro> listaFormularioproblemascorreccioncritica;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * registro del subformulario correccion critica lista
     */
    private Registro registroSubFormularioCorreccionCriticaLista;
    /**
     * registro del subformulario historial
     */
    private Registro registroSubFormularioCorreccionHistoria;
    /**
     * registro del subformulario de problemas
     */
    private Registro registroSubFormularioProblemasCorreccionCritica;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el ciclo
     * seleccionado.
     */
    private String ciclo;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el codigo inicial
     * seleccionado.
     */
    private String codigoInicial;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el codigo final
     * seleccionado.
     */
    private String codigoFinal;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el ano del ciclo
     * seleccionado.
     */
    private String anoCiclo;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el periodo del ciclo
     * seleccionado.
     */
    private String periodoCiclo;

    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el valor seleccionado
     * del check Excluir Consumos Manuales
     */
    private boolean manual;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el valor seleccionado
     * del check Excluir Lecturas Iguales
     */
    private boolean iguales;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el valor seleccionado
     * del check Suscriptores Sin Critica
     */
    private boolean normales;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica, que contiene el valor seleccionado
     * del check Desviaciï¿½n Significativa Anterior.
     */
    private boolean desviacion;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica que contiene el valor del ano
     * siguiente, calculado desde alli.
     */
    private String anoSiguiente;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica que contiene el valor del ano
     * siguiente, calculado desde alli.
     */
    private String periodoSiguiente;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica que contiene el valor del consumo,
     * calculado desde alli.
     */
    private String condicion;
    /**
     * Atributo que proviene del formulario
     * FormularioPedirCicloCritica que contiene el valor de crï¿½tica,
     * calculado desde alli.
     */
    private String frecuencia;
    /**
     * Indice del registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private int indiceFormulariocorreccioncriticalista;
    /**
     * Indice del registro activo en el subformulario
     * Formularioproblemascorreccioncritica.
     */
    private int indiceFormularioproblemascorreccioncritica;

    /**
     * Variable creada para guardar el registro de la grilla antes de
     * ser editado el registro seleccionado en el subFormulario
     * Formularioproblemascorreccioncritica
     */
    private Registro regSubProblemasCorrecion;
    /**
     * Indice del registro activo en el subformulario
     * Formulariocorreccionhistoria.
     */
    private int indiceFormulariocorreccionhistoria;
    /**
     * Registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private Registro ccListaSeleccionado;
    /**
     * Nombre del suscriptor del registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private String nombreRegistroSeleccionado;
    /**
     * Cï¿½digo de ruta del registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private String codigoRutaSeleccionado;
    /**
     * Lectura del registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private String lecturaSeleccionado;
    /**
     * Fecha de lectura de aforo del registro activo en el
     * subformulario Formulariocorreccioncriticalista.
     */
    private Date fechaLecAforoSeleccionado;
    /**
     * Lectura de Aforo del registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private String lecturaAforoSeleccionado;
    /**
     * Consumo promedio del registro activo en el subformulario
     * Formulariocorreccioncriticalista.
     */
    private String consumoPromedioSeleccionado;

    /**
     * Atributo que hace visible o no el dialogo DgNombreProblema
     */
    private boolean visibleDialogo;
    /**
     * Indicador del campo DESVIACIONAFORO del registro activo en el
     * subformulario Formulariocorreccioncriticalista. Se utiliza con
     * el fin de mostrar o no el formulario FormularioActivarDesvio
     * segï¿½n su valor.
     */
    private boolean desvAforo;
    /**
     * Indicador para hacer editable o no la lectura del registro en
     * el subFormulario FormularioCorreccionCriticaLista, con base en
     * el parametro PERMITE MODIFICAR LECTURA ACTUAL EN CRITICA
     */
    private boolean bloqueaLectura;
    /**
     * Periodo de inicio del historial, calculado con las funciones
     * del paquete PCK_SERVICIOSPUBLICOS, ANON y PERN.
     */
    private String periodoInicioHistorial;
    /**
     * Periodo final del historial, calculado con las funciones del
     * paquete PCK_SERVICIOSPUBLICOS, ANON y PERN.
     */
    private String periodoFinalHistorial;
    /**
     * Variable con el valor ingresado en el formulario modal 1117
     */
    private String consumoMenor;
    /**
     * Variable con el valor ingresado en el formulario modal 1117
     */
    private int porcentajeMenor;
    /**
     * Variable con el valor ingresado en el formulario modal 1117
     */
    private int porcentajeMayor;

    /**
     * variable constante con el valor "ciclo" para obtener los
     * parametros enviados desde el formulario anterior
     */
    private static final String CTECICLO = "ciclo";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCeroGeneralRemote ejbServiciosPublicosCeroGeneral;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    // </DECLARAR_ADICIONALES>
    public FormularioCorreccionCritica() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FORMULARIO_CORRECCION_CRITICA
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubFormularioCorreccionCriticaLista = new Registro(
                            new HashMap<String, Object>());
            registroSubFormularioCorreccionHistoria = new Registro(
                            new HashMap<String, Object>());
            registroSubFormularioProblemasCorreccionCritica = new Registro(
                            new HashMap<String, Object>());
            regSubProblemasCorrecion = new Registro(
                            new HashMap<String, Object>());
            cargarFlash();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    public void cargarFlash() {
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            ciclo = SysmanFunciones.nvl(parametrosEntrada.get(CTECICLO), "")
                            .toString();
            codigoInicial = SysmanFunciones
                            .nvl(parametrosEntrada.get("codigoInicial"), "")
                            .toString();
            codigoFinal = SysmanFunciones
                            .nvl(parametrosEntrada.get("codigoFinal"), "")
                            .toString();
            periodoCiclo = SysmanFunciones
                            .nvl(parametrosEntrada.get("periodoCiclo"), "")
                            .toString();
            anoCiclo = SysmanFunciones
                            .nvl(parametrosEntrada.get("anoCiclo"), "")
                            .toString();
            frecuencia = SysmanFunciones
                            .nvl(parametrosEntrada.get("frecuencia"), "")
                            .toString();
            manual = (boolean) parametrosEntrada.get("manual");
            iguales = (boolean) parametrosEntrada.get("iguales");
            normales = (boolean) parametrosEntrada.get("normales");
            desviacion = (boolean) parametrosEntrada.get("desviacion");

            consumoMenor = String
                            .valueOf(parametrosEntrada.get("consumoMenor"));

            porcentajeMenor = Integer.parseInt(String
                            .valueOf(parametrosEntrada.get("porcentajeMenor")));

            porcentajeMayor = Integer.parseInt(String
                            .valueOf(parametrosEntrada.get("porcentajeMayor")));

        }
        else {
            SessionUtil.redireccionarMenu();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaNOMBRE();
        cargarListaProblema();
        cargarListaProblemaE();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFormulariocorreccioncriticalista();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaFormulariocorreccioncriticalista = null;
        listaFormulariocorreccionhistoria = null;
        listaFormularioproblemascorreccioncritica = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        // se agregan la funciones que venian del formulario anterior
        bloqueaLectura = true;
        try {
            String mensaje = ejbServiciosPublicosCeroGeneral
                            .prepararAnoPeriodoSiguiente(
                                            compania,
                                            Integer.parseInt(anoCiclo),
                                            periodoCiclo, "#", "");

            anoSiguiente = String.valueOf(mensaje.split("#")[0]);
            periodoSiguiente = String.valueOf(mensaje.split("#")[1]);

            tabla = "";
            iniciarListasSub();
            iniciarListas();
            nombreRegistroSeleccionado = "";

            periodoInicioHistorial = obtenerAnoPeriodo(-5);
            periodoFinalHistorial = obtenerAnoPeriodo(0);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String obtenerAnoPeriodo(int tipo) {
        String anoN = "";
        String periodoN = "";
        try {
            anoN = String.valueOf(
                            ejbServiciosPublicosCero.prepararAnoSiguiente(
                                            compania,
                                            Integer.parseInt(anoCiclo),
                                            periodoCiclo,
                                            tipo, frecuencia,
                                            Integer.parseInt(modulo)));
            periodoN = String.valueOf(
                            ejbServiciosPublicosCero.prepararPeriodoSiguiente(
                                            compania,
                                            Integer.parseInt(anoCiclo),
                                            periodoCiclo,
                                            tipo, frecuencia,
                                            Integer.parseInt(modulo)));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return anoN + periodoN;
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    public void cargarListaFormulariocorreccioncriticalista() {
        try {
            condicion = ejbServiciosPublicosCeroGeneral.prepararCritica(
                            compania, Integer.parseInt(modulo),
                            Integer.parseInt(ciclo), codigoInicial, codigoFinal,
                            String.valueOf(consumoMenor),
                            Integer.parseInt(anoCiclo),
                            Integer.parseInt(periodoCiclo),
                            Double.parseDouble(String.valueOf(porcentajeMenor)),
                            Double.parseDouble(String.valueOf(porcentajeMayor)),
                            normales, manual, iguales, desviacion,
                            SessionUtil.getUser()
                                            .getCodigo(),
                            false);

            listaFormulariocorreccioncriticalista = new RegistroDataModel(
                            ConectorPool.ESQUEMA_SYSMAN,
                            ":FR1116_nuevo:tablePL2174", condicion,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FormularioCorreccionCriticaEnum.SP_USUARIO
                                                            .getValue()));

        }
        catch (NumberFormatException | SysmanException | SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    public void cargarListaFormulariocorreccionhistoria() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoCiclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoCiclo);
        param.put("PERIODOINICIOHIST", periodoInicioHistorial);
        param.put("PERIODOFINHIST", periodoFinalHistorial);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRutaSeleccionado);
        param.put(GeneralParameterEnum.CLASE.getName(), "AFR");
        param.put("FRECUENCIA", frecuencia);
        try {
            listaFormulariocorreccionhistoria = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FormularioCorreccionCriticaUrlEnum.URL18575
                                                                                            .getValue())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            FormularioCorreccionCriticaEnum.SP_USUARIO_PROBLEMA
                                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaFormularioproblemascorreccioncritica() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOSIGUIENTE", anoSiguiente);
        param.put("PERIODOSIGUIENTE", periodoSiguiente);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRutaSeleccionado);
        param.put(GeneralParameterEnum.CLASE.getName(), "AFR");

        try {
            listaFormularioproblemascorreccioncritica = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FormularioCorreccionCriticaUrlEnum.URL19287
                                                                                            .getValue())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            FormularioCorreccionCriticaEnum.SP_USUARIO_PROBLEMA
                                                                            .getValue()));
        }

        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaNOMBRE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "AFR");
        try {
            listaNOMBRE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FormularioCorreccionCriticaUrlEnum.URL20478
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProblema() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormularioCorreccionCriticaUrlEnum.URL20479
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "AFR");

        listaProblema = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaProblemaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormularioCorreccionCriticaUrlEnum.URL20479
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "AFR");

        listaProblemaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarProblema() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProblemaC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarLECTURAAFOROC(int rowNun) {

        // <CODIGO_DESARROLLADO>
        Registro registro = listaFormulariocorreccioncriticalista
                        .getDatasource().get(rowNun % 10);
        Double lecturaAforo = Double.parseDouble(
                        registro.getCampos()
                                        .get(FormularioCorreccionCriticaEnum.LECTURAAFORO
                                                        .getValue())
                                        .toString());
        Double lectura = Double.parseDouble(
                        registro.getCampos()
                                        .get(FormularioCorreccionCriticaEnum.LECTURA
                                                        .getValue())
                                        .toString());
        if (0 != lecturaAforo) {
            if ("0".equals(SysmanFunciones.nvlStr(lectura.toString(),
                            "0"))) {
                listaFormulariocorreccioncriticalista.getDatasource()
                                .get(rowNun % 10).getCampos()
                                .put(FormularioCorreccionCriticaEnum.LECTURAAFORO
                                                .getValue(), 0);
                lectura = 0.0;
            }
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1663"));
            return;
        }

        cambiarLecturaAforoCValidarRedondeo(lecturaAforo, lectura, rowNun);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarLecturaAforoCValidarRedondeo(double lecturaAforo,
        double lectura, int rowNum) {
        Double dife = lecturaAforo - lectura;

        String uso = registroSubFormularioCorreccionCriticaLista.getCampos()
                        .get("USO").toString();
        /*
         * Las siguientes variables booleanas se hacen debido a que
         * SonarCube no permite mas de 3 condiciones para el mismo
         * caso
         */
        boolean pareceErradaUno = "01"
                        .equals(uso)
            && (dife > 200);
        boolean pareceErradaDos = !"01"
                        .equals(uso)
            && (dife > 500);
        boolean pareceErradaTres = (lecturaAforo < lectura) || (dife > 300);
        boolean condicionRedondeo = SysmanFunciones.redondear(
                        Math.log10(lecturaAforo + 0.5),
                        0) > Double.parseDouble(
                                        registroSubFormularioCorreccionCriticaLista
                                                        .getCampos()
                                                        .get("NUMERODIGITOS")
                                                        .toString());
        if (condicionRedondeo) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1664"));
            listaFormulariocorreccioncriticalista.getDatasource()
                            .get(rowNum % 10).getCampos()
                            .put(FormularioCorreccionCriticaEnum.LECTURAAFORO
                                            .getValue(),
                                            ConectorPool.ESQUEMA_SYSMAN);
        }
        else if (pareceErradaUno || pareceErradaDos || pareceErradaTres) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1665"));
        }
    }

    public void cambiarDesviacionAforoC(int rowNum) throws SysmanException {
        // <CODIGO_DESARROLLADO>
        desvAforo = (boolean) listaFormulariocorreccioncriticalista
                        .getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get(FormularioCorreccionCriticaEnum.DESVIACIONAFORO
                                        .getValue());

        if (!desvAforo) {
            listaFormulariocorreccioncriticalista.getDatasource()
                            .get(indiceFormulariocorreccioncriticalista % 10)
                            .getCampos()
                            .put(FormularioCorreccionCriticaEnum.DESVIACIONAFORO
                                            .getValue(), true);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNOMBREC(int rowNum) {

        String nombreProblema = String.valueOf(listaFormulariocorreccionhistoria
                        .get(rowNum).getCampos().get("PROBLEMA"));
        if (nombreProblema == null) {
            visibleDialogo = true;
        }
        else {
            visibleDialogo = false;
        }

    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaProblema(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubFormularioProblemasCorreccionCritica.getCampos()
                        .put("PROBLEMA", registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));
        registroSubFormularioProblemasCorreccionCritica.getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registroAux
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    public void seleccionarFilaProblemaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = String.valueOf(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        listaFormularioproblemascorreccioncritica
                        .get(indiceFormularioproblemascorreccioncritica)
                        .getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registroAux
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>

    public void oprimirAbrirDialogoActivaDesvio() {
        // <CODIGO_DESARROLLADO>
        if (!desvAforo) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1677"));
            return;
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("formularioPrevio", "FormularioCorreccionCritica");
        parametros.put(CTECICLO, ciclo);
        parametros.put("codigoRuta", codigoRutaSeleccionado);
        parametros.put("ano", anoSiguiente);
        parametros.put("periodo", periodoSiguiente);
        parametros.put(FormularioCorreccionCriticaEnum.LECTURA.getValue(),
                        lecturaSeleccionado);
        parametros.put("lecturaAforo", lecturaAforoSeleccionado);
        parametros.put("fechaLecAforo", fechaLecAforoSeleccionado);
        parametros.put("consumoPromedio", consumoPromedioSeleccionado);
        SessionUtil.setFlash(parametros);
        SessionUtil.cargarModal(
                        String.valueOf(GeneralCodigoFormaEnum.FORMULARIO_ACTIVAR_DESVIO_CONTROLADOR
                                        .getCodigo()),
                        modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioAbrirDialogoActivaDesvio(SelectEvent event) // variable
                                                                              // necesario
                                                                              // para
                                                                              // la
                                                                              // vista
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = SessionUtil.getFlash();
        boolean activaDesvio = false;
        if (parametros != null) {
            activaDesvio = (boolean) SysmanFunciones
                            .nvl(parametros.get("activaDesvio"), false);
        }
        if (!activaDesvio) {
            listaFormulariocorreccioncriticalista.getDatasource()
                            .get(indiceFormulariocorreccioncriticalista % 10)
                            .getCampos()
                            .put(FormularioCorreccionCriticaEnum.DESVIACIONAFORO
                                            .getValue(), false);
        }
        else {
            editarRegSubFormulariocorreccioncriticalista(null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExporta() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        exportaCritica();
        // </CODIGO_DESARROLLADO>
    }

    public String extraerConsultaExportaCritica() {
        try {
            condicion = ejbServiciosPublicosCeroGeneral.prepararCritica(
                            compania, Integer.parseInt(modulo),
                            Integer.parseInt(ciclo), codigoInicial, codigoFinal,
                            String.valueOf(consumoMenor),
                            Integer.parseInt(anoCiclo),
                            Integer.parseInt(periodoCiclo),
                            Double.parseDouble(String.valueOf(porcentajeMenor)),
                            Double.parseDouble(String.valueOf(porcentajeMayor)),
                            normales, manual, iguales, desviacion,
                            SessionUtil.getUser()
                                            .getCodigo(),
                            true);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        HashMap<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("compania", compania);
        reemplazos.put(CTECICLO, ciclo);
        reemplazos.put("codigoInicial", codigoInicial);
        reemplazos.put("codigoFinal", codigoFinal);
        reemplazos.put("ano", anoCiclo);
        reemplazos.put("periodo", periodoCiclo);
        reemplazos.put("condicion", condicion);
        reemplazos.put("anoSiguiente", anoSiguiente);
        reemplazos.put("periodoSiguiente", periodoSiguiente);

        return Reporteador.resuelveConsulta("800060ExportaCritica",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos);
    }

    public void exportaCritica() {
        String strSql = extraerConsultaExportaCritica();
        try {
            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(
                                            strSql, ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97)
                                            .getStream());
            Sheet sheet = workbook.getSheet("Report");
            sheet.shiftRows(0, sheet.getLastRowNum(), 4);
            CellReference cellRefIniTitulo = new CellReference(0, 0);
            String celdaIniTitulo = cellRefIniTitulo.formatAsString();
            CellReference cellRefFinTitulo = new CellReference(0,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo = cellRefFinTitulo.formatAsString();
            CellRangeAddress region = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo + ":" + celdaFinTitulo);
            sheet.addMergedRegion(region);

            CellReference cellRefIniTitulo1 = new CellReference(1, 0);
            String celdaIniTitulo1 = cellRefIniTitulo1.formatAsString();
            CellReference cellRefFinTitulo1 = new CellReference(1,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo1 = cellRefFinTitulo1.formatAsString();
            CellRangeAddress region1 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo1 + ":" + celdaFinTitulo1);
            sheet.addMergedRegion(region1);

            CellReference cellRefIniTitulo2 = new CellReference(2, 0);
            String celdaIniTitulo2 = cellRefIniTitulo2.formatAsString();
            CellReference cellRefFinTitulo2 = new CellReference(2,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo2 = cellRefFinTitulo2.formatAsString();
            CellRangeAddress region2 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo2 + ":" + celdaFinTitulo2);
            sheet.addMergedRegion(region2);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            Font font = workbook.createFont();
            font.setFontName("SansSerif");
            font.setBold(true);
            style.setFont(font);
            Cell cell = sheet.createRow(0).createCell(0);
            cell.setCellValue(SysmanFunciones
                            .concatenar(idioma.getString("TB_TB1691"), " ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            new Date(),
                                                            "dd/MM/yyyy HH:mm:ss")));
            cell.setCellStyle(style);
            String filtroIni = "";
            if (manual) {
                filtroIni = SysmanFunciones.concatenar(filtroIni,
                                idioma.getString("TB_TB1687"));
            }
            if (iguales) {
                filtroIni = SysmanFunciones.concatenar(filtroIni,
                                idioma.getString("TB_TB1688"));
            }
            if (normales) {
                filtroIni = SysmanFunciones.concatenar(filtroIni,
                                idioma.getString("TB_TB1689"));
            }
            if (desviacion) {
                filtroIni = SysmanFunciones.concatenar(filtroIni,
                                idioma.getString("TB_TB1690"));
            }
            Cell cell2 = sheet.createRow(1).createCell(0);
            cell2.setCellValue(idioma.getString("TB_TB1692")
                            .replace("s$codigoInicial$s", codigoInicial)
                            .replace("s$codigoFinal$s", codigoFinal)
                + filtroIni);
            cell2.setCellStyle(style);
            Cell cell3 = sheet.createRow(2).createCell(0);
            cell3.setCellValue("");
            cell3.setCellStyle(style);
            for (int i = 0; i < (Math.max(sheet.getRow(4).getLastCellNum(), 0)
                - 1); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Informe.xls");
        }
        catch (IOException | JRException | SQLException | DRException
                        | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirMas(Registro reg, int indice) // variable
                                                     // indice
                                                     // necesaria en
                                                     // la vista
    {
        // <CODIGO_DESARROLLADO>
        codigoRutaSeleccionado = reg.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
        nombreRegistroSeleccionado = reg.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaFormularioproblemascorreccioncritica();
        cargarListaFormulariocorreccionhistoria();
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarSeleccionFila() {
        codigoRutaSeleccionado = "";
        nombreRegistroSeleccionado = "";
        cargarListaFormularioproblemascorreccioncritica();
        cargarListaFormulariocorreccionhistoria();
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    public void activarEdicionFormulariocorreccioncriticalista(Registro reg) {
        indiceFormulariocorreccioncriticalista = reg.getIndice();
        registroSubFormularioCorreccionCriticaLista = new Registro(
                        new HashMap<>(reg.getCampos()));
        codigoRutaSeleccionado = reg.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
        lecturaSeleccionado = reg.getCampos()
                        .get(FormularioCorreccionCriticaEnum.LECTURA.getValue())
                        .toString();
        lecturaAforoSeleccionado = reg.getCampos().get(
                        FormularioCorreccionCriticaEnum.LECTURAAFORO.getValue())
                        .toString();
        consumoPromedioSeleccionado = reg.getCampos().get("CONSUMOP")
                        .toString();
        try {
            if (reg.getCampos().get("FECHALECTURAAFORO") != null) {
                fechaLecAforoSeleccionado = SysmanFunciones
                                .convertirAFecha(reg.getCampos()
                                                .get("FECHALECTURAAFORO")
                                                .toString());
            }
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        nombreRegistroSeleccionado = reg.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaFormularioproblemascorreccioncritica();
        cargarListaFormulariocorreccionhistoria();

    }

    public void agregarRegistroSubFormulariocorreccioncriticalista() {
        // Metodo no implementado
    }

    public void editarRegSubFormulariocorreccioncriticalista(
        RowEditEvent event) {
        Registro reg;
        if (event == null) {
            reg = listaFormulariocorreccioncriticalista.getDatasource()
                            .get(indiceFormulariocorreccioncriticalista % 10);
        }
        else {
            reg = (Registro) event.getObject();
        }

        try {
            Registro registroAux = new Registro();
            registroAux.getCampos().put("LECTURA",
                            reg.getCampos().get("LECTURA"));
            registroAux.getCampos()
                            .put(FormularioCorreccionCriticaEnum.LECTURAAFORO
                                            .getValue(),
                                            reg.getCampos().get(
                                                            FormularioCorreccionCriticaEnum.LECTURAAFORO
                                                                            .getValue()));
            registroAux.getCampos().put(
                            GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroAux.getCampos().put(
                            GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            registroAux.getCampos()
                            .put(FormularioCorreccionCriticaEnum.DESVIACIONAFORO
                                            .getValue(), reg.getCampos().get(
                                                            FormularioCorreccionCriticaEnum.DESVIACIONAFORO
                                                                            .getValue()));

            registroAux.getLlave().put("KEY_COMPANIA",
                            reg.getLlave().get(GeneralParameterEnum.COMPANIA
                                            .getName()));
            registroAux.getLlave().put("KEY_CICLO",
                            reg.getLlave().get(GeneralParameterEnum.CICLO
                                            .getName()));
            registroAux.getLlave()
                            .put(FormularioCorreccionCriticaEnum.KEY_CODIGORUTA
                                            .getValue(),
                                            reg.getLlave().get(
                                                            GeneralParameterEnum.CODIGORUTA
                                                                            .getName()));

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FormularioCorreccionCriticaUrlEnum.URL19291
                                                            .getValue());

            int conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            registroAux.getCampos(),
                            registroAux.getLlave());

            if (conteo > 0) {

                compararRegistros(registroSubFormularioCorreccionCriticaLista,
                                reg,
                                reg.getCampos().get(
                                                GeneralParameterEnum.CODIGORUTA
                                                                .getName())
                                                .toString(),
                                FormularioCorreccionCriticaEnum.FRM_CORRECCIONCRITICA_LIS
                                                .getValue());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_MODIFICADO));
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaFormulariocorreccioncriticalista();
        }
    }

    public void eliminarRegSubFormulariocorreccioncriticalista(Registro reg) {
        // No se implementa la grilla no debe permitir eliminar
    }

    public void cancelarEdicionFormulariocorreccioncriticalista() {
        cancelarSeleccionFila();
        cargarListaFormulariocorreccioncriticalista();
        cargarListaFormulariocorreccionhistoria();
        cargarListaFormularioproblemascorreccioncritica();
    }

    public void activarEdicionFormulariocorreccionhistoria(Registro reg) {
        // no se permite editar ningun registro del historial
    }

    public void agregarRegistroSubFormulariocorreccionhistoria() {

        // No se implementa por que no se permite agregar ningún
        // registro de la grilla del historico
    }

    public void editarRegSubFormulariocorreccionhistoria(RowEditEvent event) {
        // No se implementa por que no se permite editar ningún
        // registro de la grilla del historico
    }

    public void eliminarRegSubFormulariocorreccionhistoria(Registro reg) {
        // No se implementa por que no se permite eliminar ningún
        // registro de la grilla del historico
    }

    public void cancelarEdicionFormulariocorreccionhistoria() {
        cargarListaFormulariocorreccionhistoria();
        cargarListaFormularioproblemascorreccioncritica();
    }

    public void activarEdicionFormularioproblemascorreccioncritica(
        Registro reg) {
        indiceFormularioproblemascorreccioncritica = reg.getIndice();
        regSubProblemasCorrecion = new Registro(new HashMap<>(reg.getCampos()));
    }

    public void agregarRegistroSubFormularioproblemascorreccioncritica() {
        try {
            if ("".equals(SysmanFunciones.nvlStr(codigoRutaSeleccionado, ""))) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1682"));
                return;
            }
            registroSubFormularioProblemasCorreccionCritica.getCampos()
                            .put(GeneralParameterEnum.COMPANIA.getName(),
                                            compania);
            registroSubFormularioProblemasCorreccionCritica.getCampos()
                            .put(GeneralParameterEnum.CICLO.getName(), ciclo);
            registroSubFormularioProblemasCorreccionCritica.getCampos().put(
                            GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoRutaSeleccionado);
            registroSubFormularioProblemasCorreccionCritica.getCampos()
                            .put("ANO", anoSiguiente);
            registroSubFormularioProblemasCorreccionCritica.getCampos().put(
                            "PERIODO", periodoSiguiente);
            registroSubFormularioProblemasCorreccionCritica.getCampos().put(
                            "CLASE", "AFR");
            registroSubFormularioProblemasCorreccionCritica.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());

            registroSubFormularioProblemasCorreccionCritica.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubFormularioProblemasCorreccionCritica.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FormularioCorreccionCriticaUrlEnum.URL19290
                                                            .getValue());

            Map<String, Object> conteo = requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            registroSubFormularioProblemasCorreccionCritica
                                            .getCampos());

            cargarListaFormularioproblemascorreccioncritica();
            if (!conteo.isEmpty()) {

                auditarModif("1", codigoRutaSeleccionado,
                                FormularioCorreccionCriticaEnum.FRM_CORRECCIONCRITICA_PROB
                                                .getValue());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_INGRESADO));
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubFormularioProblemasCorreccionCritica = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubFormularioproblemascorreccioncritica(
        RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
            reg.getCampos().remove(GeneralParameterEnum.CICLO.getName());
            reg.getCampos().remove("CLASE");
            reg.getCampos().remove("ANO");
            reg.getCampos().remove("PERIODO");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FormularioCorreccionCriticaUrlEnum.URL19288
                                                            .getValue());

            int conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            if (conteo > 0) {
                compararRegistros(
                                regSubProblemasCorrecion,
                                reg,
                                reg.getLlave().get(
                                                FormularioCorreccionCriticaEnum.KEY_CODIGORUTA
                                                                .getValue())
                                                .toString(),
                                FormularioCorreccionCriticaEnum.FRM_CORRECCIONCRITICA_PROB
                                                .getValue());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_MODIFICADO));
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaFormularioproblemascorreccioncritica();
        }
    }

    public void eliminarRegSubFormularioproblemascorreccioncritica(
        Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FormularioCorreccionCriticaUrlEnum.URL19289
                                                            .getValue());
            int conteo = requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            if (conteo > 0) {

                auditarModif("2",
                                reg.getLlave().get(
                                                FormularioCorreccionCriticaEnum.KEY_CODIGORUTA
                                                                .getValue())
                                                .toString(),
                                FormularioCorreccionCriticaEnum.FRM_CORRECCIONCRITICA_PROB
                                                .getValue());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_ELIMINADO));
            }
            cargarListaFormularioproblemascorreccioncritica();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionFormularioproblemascorreccioncritica() {
        cargarListaFormularioproblemascorreccioncritica();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void aceptarDgNombreProblema() {
        // Metodo no implementado
    }

    public void cancelarDgNombreProblema() {
        // Metodo no implementado
    }

    /**
     * Metodo ejecutado al abrir el formulario desde una opcion de
     * menu
     */
    public void mensajesInicioModal() {
        String parametro;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE MODIFICAR LECTURA ACTUAL EN CRITICA",
                            modulo, new Date(), true);
            if (parametro == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2720"));
                return;
            }
            bloqueaLectura = !"SI".equals(parametro);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite comparar los registros de los campos antes
     * de editar y los campos modificados para ejecutar el
     * procedimiento AUDITARREGCOMPARAR
     *
     * @param regAntes
     * registro del fraude o registro de la grilla del subformulario
     * listaSubfraudescarta antes de ser modificado
     * @param regDespues
     * registro del fraude o registro de la grilla del subformulario
     * listaSubfraudescarta con los campos modificados
     * @param formulario
     * nombre del formulario a comparar
     */
    private void compararRegistros(Registro regAntes, Registro regDespues,
        String codRuta,
        String formulario) {
        StringBuilder resultado = new StringBuilder();
        int contador = 0;
        Iterator<Entry<String, Object>> it = regDespues.getCampos().entrySet()
                        .iterator();
        while (it.hasNext()) {
            Entry<String, Object> e = it.next();
            String campoAnt = obtenerValor(regAntes, e);
            String campoNue = obtenerValor(regDespues, e);

            if (!campoNue.equals(campoAnt)) {
                campoNue = campoNue.isEmpty() ? "nulo" : campoNue;
                resultado.append(
                                e.getKey() + "," + campoAnt + "," + campoNue
                                    + ";");
                contador++;
            }
        }
        auditarRegComparar(resultado, contador, codRuta, formulario);

    }

    private String obtenerValor(Registro registroObt, Entry<String, Object> e) {
        try {
            return (registroObt.getCampos()
                            .get(e.getKey()) != null)
                && (registroObt.getCampos().get(e.getKey()) instanceof Date)
                    ? SysmanFunciones
                                    .convertirAFechaCadena(
                                                    (Date) registroObt
                                                                    .getCampos()
                                                                    .get(e.getKey()),
                                                    "dd/MM/yyyy HH:mm:ss")
                    : registroObt.getCampos().get(e.getKey()) == null
                        ? ""
                        : registroObt.getCampos().get(e.getKey()).toString();
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
            return "";
        }
    }

    /**
     * Metodo que ejecuta el procedimiento PR_AUDITARREGCOMPARAR
     *
     * @param camposMod
     * Nombre y valores de los campos modificados en la edicion
     * @param contador
     * Numero de veces que se debe recorrer la cadena camposMod
     * @param formulario
     * Nombre del formulario modificado
     */
    private void auditarRegComparar(StringBuilder camposMod,
        int contador, String codRuta, String formulario) {
        if (contador != 0) {

            try {
                ejbServiciosPublicosTres.auditarRegistroComparar(compania,
                                formulario, String.valueOf(compania + "^" +
                                    ciclo + "^" + codRuta),
                                camposMod.toString(),
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(ciclo), codRuta,
                                Integer.parseInt(anoCiclo),
                                periodoCiclo, contador);

            }
            catch (NumberFormatException | SystemException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    /**
     * Metodo que permite ejecutar el procedimiento PR_AUDITARMODIF
     *
     * @param tipoMod
     * Tipo de modificacion que se esta realizando. 1 para Insercion,
     * 2 para Eliminacion.
     * @param formulario
     * nombre del formulario a auditar
     */
    private void auditarModif(String tipoMod, String codRuta,
        String formulario) {
        try {
            ejbServiciosPublicosTres.auditarModif(compania, formulario,
                            Integer.parseInt(tipoMod),
                            String.valueOf(compania + "^"
                                + ciclo + "^" + codRuta),
                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        mensajesInicioModal();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getNombreRegistroSeleccionado() {
        return nombreRegistroSeleccionado;
    }

    public void setNombreRegistroSeleccionado(
        String nombreRegistroSeleccionado) {
        this.nombreRegistroSeleccionado = nombreRegistroSeleccionado;
    }

    public boolean isBloqueaLectura() {
        return bloqueaLectura;
    }

    public void setBloqueaLectura(boolean bloqueaLectura) {
        this.bloqueaLectura = bloqueaLectura;
    }

    public Registro getCcListaSeleccionado() {
        return ccListaSeleccionado;
    }

    public void setCcListaSeleccionado(Registro ccListaSeleccionado) {
        this.ccListaSeleccionado = ccListaSeleccionado;
    }

    public int getIndiceFormularioproblemascorreccioncritica() {
        return indiceFormularioproblemascorreccioncritica;
    }

    public void setIndiceFormularioproblemascorreccioncritica(
        int indiceFormularioproblemascorreccioncritica) {
        this.indiceFormularioproblemascorreccioncritica = indiceFormularioproblemascorreccioncritica;
    }

    public int getIndiceFormulariocorreccionhistoria() {
        return indiceFormulariocorreccionhistoria;
    }

    public void setIndiceFormulariocorreccionhistoria(
        int indiceFormulariocorreccionhistoria) {
        this.indiceFormulariocorreccionhistoria = indiceFormulariocorreccionhistoria;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaNOMBRE() {
        return listaNOMBRE;
    }

    public void setListaNOMBRE(List<Registro> listaNOMBRE) {
        this.listaNOMBRE = listaNOMBRE;
    }

    public RegistroDataModelImpl getListaProblema() {
        return listaProblema;
    }

    public void setListaProblema(RegistroDataModelImpl listaProblema) {
        this.listaProblema = listaProblema;
    }

    public RegistroDataModelImpl getListaProblemaE() {
        return listaProblemaE;
    }

    public void setListaProblemaE(RegistroDataModelImpl listaProblemaE) {
        this.listaProblemaE = listaProblemaE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public RegistroDataModel getListaFormulariocorreccioncriticalista() {
        return listaFormulariocorreccioncriticalista;
    }

    public void setListaFormulariocorreccioncriticalista(
        RegistroDataModel listaFormulariocorreccioncriticalista) {
        this.listaFormulariocorreccioncriticalista = listaFormulariocorreccioncriticalista;
    }

    public List<Registro> getListaFormulariocorreccionhistoria() {
        return listaFormulariocorreccionhistoria;
    }

    public void setListaFormulariocorreccionhistoria(
        List<Registro> listaFormulariocorreccionhistoria) {
        this.listaFormulariocorreccionhistoria = listaFormulariocorreccionhistoria;
    }

    public List<Registro> getListaFormularioproblemascorreccioncritica() {
        return listaFormularioproblemascorreccioncritica;
    }

    public void setListaFormularioproblemascorreccioncritica(
        List<Registro> listaFormularioproblemascorreccioncritica) {
        this.listaFormularioproblemascorreccioncritica = listaFormularioproblemascorreccioncritica;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public Registro getRegistroSubFormularioCorreccionCriticaLista() {
        return registroSubFormularioCorreccionCriticaLista;
    }

    public int getIndiceFormulariocorreccioncriticalista() {
        return indiceFormulariocorreccioncriticalista;
    }

    public void setIndiceFormulariocorreccioncriticalista(
        int indiceFormulariocorreccioncriticalista) {
        this.indiceFormulariocorreccioncriticalista = indiceFormulariocorreccioncriticalista;
    }

    public void setRegistroSubFormularioCorreccionCriticaLista(
        Registro registroSubFormularioCorreccionCriticaLista) {
        this.registroSubFormularioCorreccionCriticaLista = registroSubFormularioCorreccionCriticaLista;
    }

    public Registro getRegistroSubFormularioCorreccionHistoria() {
        return registroSubFormularioCorreccionHistoria;
    }

    public void setRegistroSubFormularioCorreccionHistoria(
        Registro registroSubFormularioCorreccionHistoria) {
        this.registroSubFormularioCorreccionHistoria = registroSubFormularioCorreccionHistoria;
    }

    public Registro getRegistroSubFormularioProblemasCorreccionCritica() {
        return registroSubFormularioProblemasCorreccionCritica;
    }

    public void setRegistroSubFormularioProblemasCorreccionCritica(
        Registro registroSubFormularioProblemasCorreccionCritica) {
        this.registroSubFormularioProblemasCorreccionCritica = registroSubFormularioProblemasCorreccionCritica;
    }
    // </SET_GET_ADICIONALES>
}
