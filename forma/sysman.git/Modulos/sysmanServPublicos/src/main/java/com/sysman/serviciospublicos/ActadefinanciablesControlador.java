/*-
 * ActadefinanciablesControlador.java
 *
 * 1.0
 *
 * 02/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.ActadefinanciablesControladorEnum;
import com.sysman.serviciospublicos.enums.ActadefinanciablesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite registrar y generar las actas de
 * financiaciones de los suscriptores. Se accede desde la ruta Panel
 * Principal\Facturacion de Servicios
 * Publicos\Suscriptores\Correspondencia\Acta de financiaciones.
 *
 * @version 1.0, 02/02/2017
 * @author lcortes
 *
 * -- Modificado por lcortes 16/03/2017 14:36 Se incluyen los campos
 * que muestran los datos de creado y modificado en el formulario.
 * @version 2, 15/05/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class ActadefinanciablesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que identifica el nombre de la tabla
     * SP_CERTIFICADOSESTRATIFICACION
     */
    private final String tSpCertEstratificacion;
    /**
     * Constante que identifica el nombre del campo ANOFINANCIABLE
     */
    private final String campoAnioFinan;
    /**
     * Constante que identifica el nombre del campo CLASE
     */
    private final String campoClase;
    /**
     * Constante que identifica el nombre del campo CODIGORUTA
     */
    private final String campoCodRuta;
    /**
     * Constante que identifica el nombre del campo CONCEPTO
     */
    private final String campoConcepto;
    /**
     * Constante que identifica el nombre del campo IDACTA
     */
    private final String campoIdActa;
    /**
     * Constante que identifica el nombre del campo IMPRESO
     */
    private final String campoImpreso;
    /**
     * Constante que identifica el nombre del campo NOMBRE
     */
    private final String campoNombre;
    /**
     * Constante que identifica el nombre del campo PERIODOFINANCIABLE
     */
    private final String campoPerFinanciable;
    /**
     * Constante que identifica el nombre del campo
     * TOTFACTURAPERACTUAL
     */
    private final String campoTotFactPerAct;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar el ciclo seleccionado en el
     * que se desea registrar el acta.
     */
    private String ciclo;
    /**
     * Atributo que identifica el modelo de plantilla seleccionado
     * para generar el acta.
     */
    private String formato;
    /**
     * Atributo que permite identificar el codigo de usuario
     * seleccionado al que se realizara el registro del acta.
     */
    private String codigoRuta;
    /**
     * Atributo que identitifca el concepto final seleccionado.
     */
    private String conceptoFin;
    /**
     * Atributo que identitifca el concepto inicial seleccionado.
     */
    private String conceptoIni;
    /**
     * Atributo que permite identificar el nombre del usuario
     * seleccionado.
     */
    private String nomUsuario;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que permite identificar el periodo del ciclo
     * seleccionado.
     */
    private String periodo;
    /**
     * Atributo que permite identificar el periodo financiable del
     * ciclo seleccionado.
     */
    private String periodoFinan;
    /**
     * Atributo que permite identificar el nombre de la plantilla
     * seleccionada.
     */
    private String nombreDoc;
    /**
     * Atributo que permite identificar la fecha de la plantilla
     * seleccionada.
     */
    private String fechaPlantilla;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de los ciclos.
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Lista de los codigos de ruta que pertenecen al ciclo
     * seleccionado.
     */
    private RegistroDataModelImpl listaCodigoRuta;
    /**
     * Lista de conceptos disponibles para el ciclo y codigo de ruta
     * selecionados.
     */
    private RegistroDataModelImpl listaConceptoFin;
    /**
     * Lista de conceptos disponibles para el ciclo y codigo de ruta
     * selecionado.
     */
    private RegistroDataModelImpl listaConceptoIni;
    /**
     * Lista de plantillas del tipo ctas financiacion.
     */
    private RegistroDataModelImpl listaFormato;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ActadefinanciablesControlador
     */
    public ActadefinanciablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        tSpCertEstratificacion = "SP_CERTIFICADOSESTRATIFICACION";
        campoAnioFinan = "ANOFINANCIABLE";
        campoClase = GeneralParameterEnum.CLASE.getName();
        campoCodRuta = GeneralParameterEnum.CODIGORUTA.getName();
        campoConcepto = GeneralParameterEnum.CONCEPTO.getName();
        campoIdActa = "IDACTA";
        campoImpreso = GeneralParameterEnum.IMPRESO.getName();
        campoNombre = GeneralParameterEnum.NOMBRE.getName();
        campoPerFinanciable = "PERIODOFINANCIABLE";
        campoTotFactPerAct = "TOTFACTURAPERACTUAL";
        try {
            numFormulario = 1284;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        cargarListaFormato();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = tSpCertEstratificacion;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL0001
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL0002
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL0003
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL0004
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL0005
                                                        .getValue());

    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     *
     *
     */

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL12157
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     *
     * Carga la lista listaCodigoRuta
     *
     */
    public void cargarListaCodigoRuta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL12619
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodRuta);
    }

    /**
     *
     * Carga la lista listaConceptoFin
     *
     */
    public void cargarListaConceptoFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL13509
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoFinan);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(ActadefinanciablesControladorEnum.CONCEPTOINICIAL.getValue(),
                        conceptoIni);

        listaConceptoFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoConcepto);
    }

    /**
     *
     * Carga la lista listaConceptoIni
     *
     */
    public void cargarListaConceptoIni() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL14472
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoFinan);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        listaConceptoIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoConcepto);
    }

    /**
     *
     * Carga la lista listaFormato que contiene las plantillas que
     * corresponden al tipo actas financiables.
     *
     */
    public void cargarListaFormato() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadefinanciablesControladorUrlEnum.URL15514
                                                        .getValue());
        listaFormato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Impreso
     *
     *
     */
    public void cambiarImpreso() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()),
                        "")
                        .toString();
        periodo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.PERIODO.getName()),
                                        "")
                        .toString();
        codigoRuta = "";
        nomUsuario = "";
        periodoFinan = "";
        registro.getCampos().put(campoCodRuta, "");
        registro.getCampos().put(campoPerFinanciable, "");
        registro.getCampos().put(campoAnioFinan, "");
        registro.getCampos().put(campoTotFactPerAct, "");
        conceptoIni = "";
        conceptoFin = "";
        listaConceptoFin = null;
        cargarListaCodigoRuta();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodRuta), "")
                        .toString();
        nomUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoNombre), "")
                        .toString();
        periodoFinan = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODOFINAN"), "")
                        .toString();
        registro.getCampos().put(campoCodRuta, codigoRuta);
        registro.getCampos().put(campoPerFinanciable, periodoFinan);
        registro.getCampos().put(campoAnioFinan,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));
        registro.getCampos().put(campoTotFactPerAct,
                        registroAux.getCampos().get(campoTotFactPerAct));
        conceptoIni = "";
        conceptoFin = "";
        listaConceptoFin = null;
        cargarListaConceptoIni();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoFin
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoConcepto), "")
                        .toString();
        registro.getCampos().put("CONCEPTO_FIN", conceptoFin);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoIni
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoConcepto), "")
                        .toString();
        registro.getCampos().put("CONCEPTO_INI", conceptoIni);
        cargarListaConceptoFin();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormato
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formato = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        nombreDoc = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoNombre), "")
                        .toString();
        fechaPlantilla = SysmanFunciones.formatearFecha(
                        (Date) registroAux.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista.
     * Realiza el registro del acta financiable y permite generar la
     * plantilla seleccionada.
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(formato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2795"));
            return;
        }
        if (ACCION_INSERTAR.equals(accion) && SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), campoIdActa)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2796"));
            return;
        }

        try {
            if (!ejbServiciosPublicosOcho.registrarActaFinanciable(compania,
                            registro.getCampos().get(campoIdActa).toString(),
                            registro.getCampos().get(campoClase).toString(),
                            Integer.parseInt(ciclo),
                            codigoRuta, Integer.parseInt(conceptoIni),
                            Integer.parseInt(conceptoFin),
                            Boolean.parseBoolean(
                                            registro.getCampos()
                                                            .get(campoImpreso)
                                                            .toString()),
                            periodo, SessionUtil.getUser().getCodigo())) {
                return;
            }

            String[] camposW = { "codigoPlantilla", "nombreDocDescarga",
                                 "fechaPlantilla" };
            String[] valoresW = { formato, nombreDoc, fechaPlantilla };

            String nombrePeriodo = ejbServiciosPublicosDos
                            .asignarNombrePeriodoDe(compania,
                                            Integer.parseInt(
                                                            registro.getCampos()
                                                                            .get(campoAnioFinan)
                                                                            .toString()),
                                            periodo, null);

            String parNombreDirCom = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DIRECTOR COMERCIAL",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "");
            String parCargoDirCom = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DIRECTOR COMERCIAL",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "");
            String parNombreSubgerente = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE SUBGERENTE",
                                            SessionUtil.getModulo(),
                                            new Date(), true),
                                            "");

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$nombreComp$s", "'"
                + SessionUtil.getCompaniaIngreso().getNombre().toUpperCase()
                + "'");
            variablesConsultaW.put("s$nombrePeriodo$s",
                            "'" + nombrePeriodo.toLowerCase() + "'");
            variablesConsultaW.put("s$nombreDirCom$s",
                            "'" + parNombreDirCom + "'");
            variablesConsultaW.put("s$cargoDirCom$s",
                            "'" + parCargoDirCom + "'");
            variablesConsultaW.put("s$nombreSubGer$s",
                            "'" + parNombreSubgerente + "'");
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$idacta$s",
                            "'" + registro.getCampos().get(campoIdActa) + "'");
            variablesConsultaW.put("s$ciclo$s", "'" + ciclo + "'");
            variablesConsultaW.put("s$codruta$s", "'" + codigoRuta + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), camposW, valoresW);
            // </CODIGO_DESARROLLADO>

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css == null) {
            registro.getCampos().put("FECHAACTA", new Date());
            ciclo = "";
            codigoRuta = "";
            nomUsuario = "";
            conceptoIni = "";
            conceptoFin = "";
            periodoFinan = "";
            listaCiclo = null;
            listaCodigoRuta = null;
            listaConceptoIni = null;
            listaConceptoFin = null;
            cargarListaCiclo();
        }
        else {
            ciclo = registro.getCampos()
                            .get(GeneralParameterEnum.CICLO.getName())
                            .toString();
            codigoRuta = registro.getCampos().get(campoCodRuta).toString();
            nomUsuario = registro.getCampos().get(campoNombre).toString();
            conceptoIni = registro.getCampos().get("CONCEPTO_INI").toString();
            conceptoFin = registro.getCampos().get("CONCEPTO_FIN").toString();
            periodoFinan = registro.getCampos().get(campoPerFinanciable)
                            .toString();
            periodo = periodoFinan;

            cargarListaCodigoRuta();
            cargarListaConceptoIni();
            cargarListaConceptoFin();
            cargarListaFormato();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            registro.getCampos()
                            .put(campoIdActa,
                                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                                            tSpCertEstratificacion,
                                                            " COMPANIA = ''"
                                                                + compania
                                                                + "'' AND CLASE    = ''37''",
                                                            campoIdActa, "1"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(campoClase, "37");
        registro.getCampos().put(GeneralParameterEnum.CICLO.getName(), ciclo);
        registro.getCampos().put(campoImpreso, SysmanFunciones
                        .nvl(registro.getCampos().get(campoImpreso),
                                        false));

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return true si se realizo la insercion del registro
     * correctamente.
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(campoIdActa);
            registro.getCampos().remove(campoClase);
            registro.getCampos().remove(campoNombre);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable formato
     *
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     *
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Retorna la variable codigoRuta
     *
     * @return codigoRuta
     */
    public String getCodigoRuta() {
        return codigoRuta;
    }

    /**
     * Asigna la variable codigoRuta
     *
     * @param codigoRuta
     * Variable a asignar en codigoRuta
     */
    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    /**
     * Retorna la variable conceptoFin
     *
     * @return conceptoFin
     */
    public String getConceptoFin() {
        return conceptoFin;
    }

    /**
     * Asigna la variable conceptoFin
     *
     * @param conceptoFin
     * Variable a asignar en conceptoFin
     */
    public void setConceptoFin(String conceptoFin) {
        this.conceptoFin = conceptoFin;
    }

    /**
     * Retorna la variable conceptoIni
     *
     * @return conceptoIni
     */
    public String getConceptoIni() {
        return conceptoIni;
    }

    /**
     * Asigna la variable conceptoIni
     *
     * @param conceptoIni
     * Variable a asignar en conceptoIni
     */
    public void setConceptoIni(String conceptoIni) {
        this.conceptoIni = conceptoIni;
    }

    /**
     * Retorna la variable nomUsuario
     *
     * @return nomUsuario
     */
    public String getNomUsuario() {
        return nomUsuario;
    }

    /**
     * Asigna la variable nomUsuario
     *
     * @param nomUsuario
     * Variable a asignar en nomUsuario
     */
    public void setNomUsuario(String nomUsuario) {
        this.nomUsuario = nomUsuario;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaCodigoRuta
     *
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    /**
     * Asigna la lista listaCodigoRuta
     *
     * @param listaCodigoRuta
     * Variable a asignar en listaCodigoRuta
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    /**
     * Retorna la lista listaConceptoFin
     *
     * @return listaConceptoFin
     */
    public RegistroDataModelImpl getListaConceptoFin() {
        return listaConceptoFin;
    }

    /**
     * Asigna la lista listaConceptoFin
     *
     * @param listaConceptoFin
     * Variable a asignar en listaConceptoFin
     */
    public void setListaConceptoFin(RegistroDataModelImpl listaConceptoFin) {
        this.listaConceptoFin = listaConceptoFin;
    }

    /**
     * Retorna la lista listaConceptoIni
     *
     * @return listaConceptoIni
     */
    public RegistroDataModelImpl getListaConceptoIni() {
        return listaConceptoIni;
    }

    /**
     * Asigna la lista listaConceptoIni
     *
     * @param listaConceptoIni
     * Variable a asignar en listaConceptoIni
     */
    public void setListaConceptoIni(RegistroDataModelImpl listaConceptoIni) {
        this.listaConceptoIni = listaConceptoIni;
    }

    /**
     * Retorna la lista listaFormato
     *
     * @return listaFormato
     */
    public RegistroDataModelImpl getListaFormato() {
        return listaFormato;
    }

    /**
     * Asigna la lista listaFormato
     *
     * @param listaFormato
     * Variable a asignar en listaFormato
     */
    public void setListaFormato(RegistroDataModelImpl listaFormato) {
        this.listaFormato = listaFormato;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}