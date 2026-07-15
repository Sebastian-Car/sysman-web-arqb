package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FrmfraudesControladorEnum;
import com.sysman.serviciospublicos.enums.FrmfraudesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar los fraudes registrados se encuentra
 * en la ruta SERVICIOS PUBLICOS/Procesos/administracion de
 * fraudes/registrados
 *
 * @author ybecerra
 * @version 1, 22/11/2016 09:51:54 -- Modificado por ybecerra
 *
 * @author eamaya
 * @version 2, 26/05/2017 Proceso de Refactoring y Manejo de EJBS
 * 
 */
@ManagedBean
@ViewScoped
public class FrmfraudesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida que almacena el codigo del modulo de la
     * aplicacion por la que se ingresa
     */
    private final String modulo;

    /**
     * Constante definida que almacena la cadena "SP_CARTA_PREGUNTA",
     * se llama en el cargar lista del SubCartaPregunta y en los
     * metodos de agregar, actualiza y eliminar de este
     */
    private final String cartaPregunta;

    /**
     * Constante definida que almacena la cadena "CONSECUTIVO", se
     * llama en los cargar lista de los subFormularios, en los metodos
     * de insertarAntes, agregar y editar de los subFormularios
     */
    private final String consecutivo;
    /**
     * Constante definida que alamcena la cadena "SP_FRAUDES_CARTA",
     * se llama en el cargar Lista del SubFraudesCarta, en los metodos
     * de agregar, editar y eliminar de este subFormulario
     */
    private final String fraudesCarta;

    /**
     * Constante definida que almacena la cadena "SUBCLASE", se llama
     * en el cargar lista del subFraudesCarta y enlos metodos agregar
     * el subCartaPregunta y oprimirImprime
     */
    private final String subClase;

    /**
     * Constante definida que almacena la cadena "ESTADO", se llama en
     * los metodos cambiarEstado, actualizarReg y cargarRegistro
     */
    private final String estado;

    /**
     * Constante definida que almacena la cadena "NOMBRERUTA", se
     * llama en los metodos seleccionarFilaCodigoRuta y en el
     * InsertarAntes llama en
     */
    private final String nombreCodigoRuta;

    /**
     * Constante definida que almacena la cadena "FRAUDE", se llama en
     * los metodos oprimirImprime y en el agregar del subFraudesCarta
     */
    private final String fraude;

    /**
     * Constante definida que almacena la cadena "CLASE", se llama en
     * los metodos oprimirImpre y en los agregar de los subFormularios
     */
    private final String clase;

    /**
     * Constante definida que almacena la cadena "PREGUNTA", se llama
     * en los metodos cargarListaSubcartapregunta,
     * editarRegSubsubcartapregunta
     */
    private final String pregunta;
    /**
     * Constante definida que almacena la cadena "FRM_FRAUDES", se
     * llama en los metodos insertarDespues, actualizarDespues,
     * eliminarDespues
     */
    private final String frmFraudes;

    /**
     * Contante definida que almacena la cadena "FRM_FRAUDES_CARTA",
     * se llama en los metodos de agregarRegistroSubSubfraudescarta
     * editarRegSubSubfraudescarta eliminarRegSubSubfraudescarta
     */
    private final String frmFraudesCarta;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del ciclo del formulario
     * PedirCiclo
     */
    private String ciclo;
    /**
     * Atributo que almacena el ano del registro seleccionado del
     * combo Codigo Ruta del formulario
     */
    private String ano;
    /**
     * Atributo que almacena el periodo del registro selecciando del
     * combo Codigo Ruta del Formulario
     */
    private String periodo;

    /**
     * Atributo que valida si los campos METROSCOBRAR, CONSECUTIVO del
     * Formulario se hacen visibles o no
     */
    private boolean conmetVisible;
    /**
     * Atributo que valida si la pesta�a Digitalizacion se hace
     * visible o no
     */
    private boolean visibleDigitalizacion;

    /**
     * Atributo que valida si el boton imprimir del subFormulario se
     * inactiva o no
     */
    private boolean inactivoBoton;
    /**
     * Atributo que almacena el codigo escogido en el combo codigo
     * ruta del formulario
     */
    private String auxiliar;
    /**
     * Atributo que valida si el combo codigo ruta del formulario
     * permite o no seleccionar registros
     */
    private boolean bloqueadoRuta;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de las Preguntas
     */
    private List<Registro> listaPregunta;
    /**
     * Lista de registros de los ano de cobro
     */
    private List<Registro> listaAnoCobro;
    /**
     * Lista de registros de los periodos de cobro
     */
    private List<Registro> listaPeriodoCobro;
    /**
     * Lista de registros de las subClases
     */
    private List<Registro> listaSubClase;
    /**
     * Lista de registros de los tipos
     */
    private List<Registro> listaTipo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista de registros de los codigos de Ruta
     */
    private RegistroDataModelImpl listaCodigoRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista de registros que contiene la tabla
     * SP_RESPUESTA_MODELO_TIPO
     */
    private List<Registro> listaSubcartapregunta;
    /**
     * Lista de registros que contiene de la tabla SP_FRAUDES_CARTA
     */
    private List<Registro> listaSubfraudescarta;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    /**
     * Variable que permite almacenar el valor del indice de la fila
     * del subformulario SubFraudesCarta que va a ser editado.
     */
    private int indiceSubfraudescarta;
    /**
     * Variable creada para guardar el registro de la grilla antes de
     * ser editado el registro seleccionado en el subFormulario
     * subFraudesCartas
     */
    private Registro regSubFraudesCarta;
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario subCartaPregunta
     */
    private Registro registroSubsubCartaPregunta;
    /**
     * Atributo de referencia para el sunformulario subFraudesCarta
     */
    private Registro registroSubsubFraudesCarta;
    /**
     * Este atributo almacena el registro seleccionado en el
     * subFraudesCarta
     */
    private Registro registroCarta;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbSPCero;

    @EJB
    private EjbSysmanUtilRemote ejbUtil;

    @EJB
    private EjbServiciosPublicosTresRemote ejbSPTres;

    // </DECLARAR_ADICIONALES>
    public FrmfraudesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cartaPregunta = "SP_RESPUESTA_MODELO_TIPO";
        fraudesCarta = "SP_FRAUDES_CARTA";
        nombreCodigoRuta = "NOMBRERUTA";
        consecutivo = "CONSECUTIVO";
        subClase = "SUBCLASE";
        estado = "ESTADO";
        fraude = "FRAUDE";
        clase = "CLASE";
        pregunta = "PREGUNTA";
        frmFraudes = "FRM_FRAUDES";
        frmFraudesCarta = "FRM_FRAUDES_CARTA";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMFRAUDES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = (String) parametrosEntrada.get("ciclo");

            }
            // <INI_ADICIONAL>
            registroSubsubCartaPregunta = new Registro(
                            new HashMap<String, Object>());
            registroSubsubFraudesCarta = new Registro(
                            new HashMap<String, Object>());

            regSubFraudesCarta = new Registro(
                            new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoRuta();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaPregunta();

        cargarListaPeriodoCobro();
        cargarListaSubClase();
        cargarListaTipo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubcartapregunta();
        cargarListaSubfraudescarta();
        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubcartapregunta = null;
        listaSubfraudescarta = null;
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
        enumBase = GenericUrlEnum.SP_FRAUDES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
    }

    /**
     *
     * Carga la lista listaSubcartapregunta
     *
     * Metodo para cargar los registros del subFormulario
     * subCartaPregunta
     */
    public void cargarListaSubcartapregunta() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registro.getCampos().get(consecutivo).toString());

            listaSubcartapregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL12099
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            cartaPregunta));

            for (Registro registroAux : listaSubcartapregunta) {

                Map<String, Object> paramPregunta = new TreeMap<>();

                paramPregunta.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                paramPregunta.put(GeneralParameterEnum.CLASE.getName(), "32");

                paramPregunta.put(GeneralParameterEnum.CODIGO.getName(),
                                registroAux.getCampos().get("CODIGO"));

                Registro rsPregunta = RegistroConverter
                                .toRegistro(requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmfraudesControladorUrlEnum.URL5959
                                                                                .getValue())
                                                .getUrl(), paramPregunta));

                registroAux.getCampos().put(pregunta,
                                rsPregunta.getCampos().get(pregunta));

            }

        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaSubfraudescarta
     *
     * Metodo para cargar los registro del subFormulario
     * subFraudesCarta
     */
    public void cargarListaSubfraudescarta() {
        Registro rsNombre = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(FrmfraudesControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get(consecutivo).toString());

            listaSubfraudescarta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL14989
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            fraudesCarta));
            for (Registro registroAux : listaSubfraudescarta) {
                Date fechaGeneracion = (Date) registroAux.getCampos()
                                .get(GeneralParameterEnum.FECHA
                                                .getName());

                Map<String, Object> paramNombre = new TreeMap<>();

                paramNombre.put(FrmfraudesControladorEnum.PARAM1.getValue(),
                                "32");

                paramNombre.put(GeneralParameterEnum.CODIGO.getName(),
                                registroAux.getCampos().get(subClase));

                paramNombre.put(GeneralParameterEnum.FECHA.getName(),
                                fechaGeneracion);

                rsNombre = RegistroConverter
                                .toRegistro(requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmfraudesControladorUrlEnum.URL6969
                                                                                .getValue())
                                                .getUrl(), paramNombre));

                registroAux.getCampos().put("NOMBRECLASE",
                                rsNombre.getCampos().get("NOMBRE"));

            }
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que permite cargar la lista de los codigos y preguntas
     * de los fraudes
     */
    public void cargarListaPregunta() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CLASE.getName(),
                            "32");

            listaPregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL16668
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite cargar el listado de anos mayores o iguales
     * al ano actual registrados en la tabla SP_PERIODO
     */
    public void cargarListaAnoCobro() {

        String anoPeriodo = SysmanFunciones
                        .nvl(registro.getCampos().get("PER"), "0").toString();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(), anoPeriodo == "0" ? "0"
            : anoPeriodo.substring(anoPeriodo.length() - 4));

        try {
            listaAnoCobro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL17508
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite cargar el listado de los nombres de los
     * meses registrados en la tabla SP_PERIODO dependiendo el ano
     */
    public void cargarListaPeriodoCobro() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(new Date()));

            listaPeriodoCobro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL18254
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite cargar la lista de los codigos y nombres de
     * las plantillas de los fraudes
     */
    public void cargarListaSubClase() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CLASE.getName(),
                            "32");

            listaSubClase = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL19084
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite cargar el listado de los nonbres de tipos de
     * fraudes registrados
     */
    public void cargarListaTipo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL19600
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite cargar la lista de los codigos y nombre
     * completo del usuario dependiendo del ciclo por el cual se
     * selecciono en el formulario pedirCiclo
     */
    public void cargarListaCodigoRuta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmfraudesControladorUrlEnum.URL20157
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el registro del combo estado del
     * formulario
     */
    public void cambiarEstado() {

        if ("C".equals(registro.getCampos().get(estado))) {

            if ((registro.getCampos().get("DECISION") == "")
                || (registro.getCampos().get("DECISION") == null)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1300"));
                registro.getCampos().put("ESTADO", "A");
                return;
            }

            registro.getCampos().put("FECHACIERRE", new Date());
            registro.getCampos().put("ANOCIERRE",
                            registro.getCampos()
                                            .get(FrmfraudesControladorEnum.PARAM4
                                                            .getValue()));
            registro.getCampos().put("PERIODOCIERRE",
                            registro.getCampos()
                                            .get(FrmfraudesControladorEnum.PARAM5
                                                            .getValue()));

            agregarRegistroNuevo(false);
            try {
                registro.getCampos().put("PERCIERRE",
                                ejbSPCero.asignarNombrePeriodo(
                                                compania,
                                                Integer.parseInt(registro
                                                                .getCampos()
                                                                .get(FrmfraudesControladorEnum.PARAM4
                                                                                .getValue())
                                                                .toString()),
                                                registro.getCampos()
                                                                .get(FrmfraudesControladorEnum.PARAM5
                                                                                .getValue())
                                                                .toString(),
                                                null));

            }
            catch (NumberFormatException | SystemException ex) {

                Logger.getLogger(FrmfraudesControlador.class.getName())
                                .log(Level.SEVERE, null, ex);

                JsfUtil.agregarMensajeError(ex.getMessage());
            }

        }

        cargarRegistro();

    }

    /**
     * Metodo ejecutado al cambiar el registro del combo de la carta
     * del subFormulario fraudesCarta
     */
    public void cambiarSubClase() {

        Registro rs = null;
        try {
            Map<String, Object> paramSub = new TreeMap<>();

            paramSub.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            paramSub.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registro.getCampos().get(consecutivo));

            paramSub.put(FrmfraudesControladorEnum.PARAM2.getValue(),
                            registroSubsubFraudesCarta.getCampos()
                                            .get(subClase));

            paramSub.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            paramSub.put(GeneralParameterEnum.CODIGORUTA.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.CODIGORUTA.getName()));

            rs = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL1313
                                                                            .getValue())
                                            .getUrl(), paramSub));

            if (rs != null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1318"));
                registroSubsubFraudesCarta.getCampos().put(subClase, null);
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar la decision
     */
    public void cambiarDecision() {
        // Este metodo no se ejecuta, debido a que no realiza ningun
        // procedimiento al momento de cambiar o editar la decision
    }

    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado al seleccionar una fila de la grilla
     * codigoRuta del formulario Fraudes
     *
     * @param event
     * Objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGORUTA.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGORUTA
                                                        .getName()));
        registro.getCampos().put(nombreCodigoRuta,
                        registroAux.getCampos().get(nombreCodigoRuta));
        registro.getCampos().put("PER", registroAux.getCampos().get("PER"));
        ano = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("ANO").toString(), "");
        periodo = SysmanFunciones.nvlStr(registroAux.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString(),
                        "");

        cargarListaAnoCobro();

    }

    // </METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado al imprimir la plantilla del subFormulario
     * fraudesCarta
     */
    public void retornarFormularioPlantilla() {
        cargarListaSubfraudescarta();
    }

    // <METODOS_BOTONES>

    /**
     * Metodo ejecutado al darle clic al boton imprimir del
     * subFormulario fraudesCarta
     */
    public void oprimirPlantilla() {
        Date fechaActual = new Date();
        try {
            Date fechaGeneracion = (Date) registroCarta.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName());

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registroCarta.getCampos().get(subClase));

            param.put(FrmfraudesControladorEnum.PARAM1.getValue(), "32");

            param.put(FrmfraudesControladorEnum.PARAM3.getValue(),
                            fechaGeneracion);

            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL1414
                                                                            .getValue())
                                            .getUrl(), param));

            Date fecha = (Date) rs.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName());
            String codigoPlantilla = registroCarta.getCampos().get(subClase)
                            .toString();

            String strNombreDocumento = idioma.getString("TB_TB1349");
            strNombreDocumento = strNombreDocumento.replace("s$CodigoRuta$s",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGORUTA
                                                            .getName())
                                            .toString());

            strNombreDocumento = SysmanFunciones.concatenar(strNombreDocumento,
                            " ",
                            SysmanFunciones.convertirAFechaCadena(fechaActual)
                                            .replace("/", ""),
                            "_",
                            SysmanFunciones.convertirAHoraCadena(fechaActual)
                                            .replace(":", ""));

            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = codigoPlantilla;
            valores[1] = SysmanFunciones.formatearFecha(fecha);
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$consecutivo$s",
                            "" + registroCarta.getCampos().get(fraude) + "");
            variablesConsultaW.put("s$ciclo$s", "'" + ciclo + "'");
            variablesConsultaW.put("s$codigoRuta$s",
                            "'" + registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGORUTA
                                                            .getName())
                                + "'");
            variablesConsultaW.put("s$clase$s",
                            "" + registroCarta.getCampos().get(subClase) + "");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            String numForm = String
                            .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo());
            SessionUtil.cargarModalDatosFlash(numForm, SessionUtil.getModulo(),
                            campos, valores);
            if (!(boolean) registroCarta.getCampos().get("IMPRESA")) {
                String criterio = SysmanFunciones.concatenar("COMPANIA = ",
                                compania, " AND CLASE = ",
                                String.valueOf(registroCarta.getCampos()
                                                .get(clase)),
                                " AND SUBCLASE = ", String.valueOf(registroCarta
                                                .getCampos().get(subClase)),
                                " ");
                int consecutivoF = (int) ejbUtil
                                .generarConsecutivoConValorInicial(
                                                "SP_FRAUDES_CARTA", criterio,
                                                GeneralParameterEnum.CONSECUTIVO
                                                                .getName(),
                                                "1");

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivoF);
                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(FrmfraudesControladorEnum.PARAM0.getValue(),
                                registroCarta.getCampos().get(fraude));
                parametros.put(FrmfraudesControladorEnum.PARAM2.getValue(),
                                registroCarta.getCampos().get(subClase));

                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmfraudesControladorUrlEnum.URL30501
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);

            }
            cargarListaSubfraudescarta();
            compararCartas(registroCarta);

            // </CODIGO_DESARROLLADO>
        }
        catch (ParseException | SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado al darle clic al boton imprimir del
     * subFormulario fraudesCarta
     *
     * @param reg
     * Registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla del subFormulario fraudeCarta
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla del subFormulario fraudeCarta
     */
    public void oprimirImprime(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        registroCarta = reg;
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del subFormulario subCartaPregunta
     */
    public void agregarRegistroSubSubcartapregunta() {

        try {
            registroSubsubCartaPregunta.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);

            registroSubsubCartaPregunta.getCampos().put(clase, "32");

            registroSubsubCartaPregunta.getCampos().put("CONSECUTIVOCLASE",
                            registro.getCampos().get(consecutivo));

            registroSubsubCartaPregunta.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubsubCartaPregunta.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubsubCartaPregunta.getCampos());

            cargarListaSubcartapregunta();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_INGRESADO));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubsubCartaPregunta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo que ejecuta la actualzacion del registro seleccionado en
     * la grilla del subFormulario subCartaPregunta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubcartapregunta(RowEditEvent event) {

        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(pregunta);

            reg.getCampos().remove(GeneralParameterEnum.CLASE.getName());

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

            reg.getCampos().remove("CONSECUTIVOCLASE");

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_MODIFICADO));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubcartapregunta();
        }
    }

    /**
     * Metodo de eliminacion del subFormulario subCartaPregunta
     *
     * @param reg
     * registro seleccionado en el subFormulario
     */
    public void eliminarRegSubSubcartapregunta(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_ELIMINADO));
            cargarListaSubcartapregunta();
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado en la grilla del subFormulario subCartaPregunta
     */
    public void cancelarEdicionSubcartapregunta() {
        cargarListaSubcartapregunta();
    }

    /**
     * Metodo de insercion del subFormulario subFraudesCarta
     */
    public void agregarRegistroSubSubfraudescarta() {

        try {

            registroSubsubFraudesCarta.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);

            registroSubsubFraudesCarta.getCampos().put(fraude,
                            registro.getCampos().get(consecutivo));

            registroSubsubFraudesCarta.getCampos().put(
                            GeneralParameterEnum.FECHA.getName(),
                            new Date());

            registroSubsubFraudesCarta.getCampos().put(clase, "32");

            registroSubsubFraudesCarta.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubsubFraudesCarta.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_FRAUDES_CARTA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            registroSubsubFraudesCarta.getCampos());

            cargarListaSubfraudescarta();

            auditarModif(registroSubsubFraudesCarta.getCampos()
                            .get(fraude).toString(), "1",
                            frmFraudesCarta);
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubsubFraudesCarta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo que ejecuta la actualizacion del registro seleccionado
     * dentro de la grilla del subformulario subFraudesCarta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubfraudescarta(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove("NOMBRECLASE");
            reg.getCampos().remove("PLANTILLA");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_FRAUDES_CARTA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            cargarListaSubfraudescarta();

            compararRegistros(regSubFraudesCarta, reg,
                            frmFraudesCarta);

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_MODIFICADO));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        finally {
            cargarListaSubcartapregunta();
        }
    }

    /**
     * Metodo de eliminacion del registro seleccionado en el
     * subFormulario subFraudesCarta
     *
     * @param reg
     * registro seleccionado en el subFormulario
     */
    public void eliminarRegSubSubfraudescarta(Registro reg) {
        try {

            if (reg.getCampos().get(consecutivo) == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1317"));
                return;
            }

            auditarModif(reg.getCampos()
                            .get(consecutivo).toString(), "2", frmFraudesCarta);

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_FRAUDES_CARTA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_ELIMINADO));
            cargarListaSubfraudescarta();

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado en la grilla del subFormulario subFraudesCarta
     */
    public void cancelarEdicionSubfraudescarta() {
        cargarListaSubfraudescarta();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Metodo para los valores del registro seleccionado en la edicion
     * del subFormulario subFraudesCarta
     *
     * @param reg
     * registro que contiene los campos almacenados del registro
     * seleccionado
     */
    public void activarEdicionSubfraudescarta(Registro reg) {
        indiceSubfraudescarta = reg.getIndice();
        regSubFraudesCarta = new Registro(new HashMap<>(reg.getCampos()));
    }

    /**
     * Metodo que se ejecuta al momento de cambiar el estado, si el
     * estado esta cerrado solo permitira visualizar la informacion.
     */
    public void actualizarReg() {

        if ("C".equals(registro.getCampos().get(estado))) {
            accion = "v";
            bloqueadoRuta = true;

        }
        else if ("A".equals(registro.getCampos().get(estado))) {
            if ("i".equals(accion)) {

                bloqueadoRuta = false;
            }
            else {
                accion = "m";
                bloqueadoRuta = true;
            }

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
        String formulario) {
        StringBuilder resultado = new StringBuilder();
        int contador = 0;
        Iterator<Entry<String, Object>> it = regDespues.getCampos().entrySet()
                        .iterator();
        while (it.hasNext()) {
            Entry<String, Object> e = it.next();
            String campoAnt = "";
            String campoNue = "";
            try {
                campoAnt = (regAntes.getCampos()
                                .get(e.getKey()) != null)
                    && (regAntes.getCampos().get(e.getKey()) instanceof Date)
                        ? SysmanFunciones
                                        .convertirAFechaCadena(
                                                        (Date) regAntes.getCampos()
                                                                        .get(e.getKey()),
                                                        "dd/MM/yyyy HH:mm:ss")
                        : regAntes.getCampos().get(e.getKey()) == null
                            ? ""
                            : regAntes.getCampos().get(e.getKey()).toString();
                campoNue = (e.getValue() != null)
                    && (e.getValue() instanceof Date)
                        ? SysmanFunciones.convertirAFechaCadena(
                                        (Date) e.getValue(),
                                        "dd/MM/yyyy HH:mm:ss")
                        : regDespues.getCampos().get(e.getKey()) == null
                            ? ""
                            : regDespues.getCampos().get(e.getKey()).toString();

                if (!campoNue.equals(campoAnt)) {
                    campoNue = campoNue == "" ? "nulo" : campoNue;
                    resultado.append(
                                    e.getKey() + "," + campoAnt + "," + campoNue
                                        + ";");
                    contador++;
                }
            }
            catch (ParseException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }
        }

        if (frmFraudes.equals(formulario)) {
            auditarRegComparar(regDespues.getCampos().get(consecutivo)
                            .toString(), resultado, contador, formulario);
        }
        else {
            auditarRegComparar(regDespues.getCampos().get(fraude)
                            .toString(), resultado, contador, formulario);
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton imprimir del subFormulario
     * subFraudesCarta
     *
     * @param reg
     * Registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla, antes de ser actualizado
     */
    public void compararCartas(Registro reg) {
        Registro resFraude = null;
        try {
            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(FrmfraudesControladorEnum.PARAM0.getValue(),
                            reg.getCampos().get(fraude));

            param.put(FrmfraudesControladorEnum.PARAM2.getValue(),
                            reg.getCampos().get(subClase));

            resFraude = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL1515
                                                                            .getValue())
                                            .getUrl(), param));

            compararRegistros(reg, resFraude,
                            frmFraudesCarta);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite ejecutar el procedimiento PR_AUDITARMODIF
     *
     * @param consecutivo
     * Valor del consecutivo del Fraude insertado
     * @param tipoMod
     * Tipo de modificaci�n que se esta realizando. 1 para
     * Insercion, 2 para Eliminacion.
     * @param formulario
     * nombre del formulario a auditar
     */
    private void auditarModif(String consecutivo, String tipoMod,
        String formulario) {
        String campo = SysmanFunciones
                        .concatenar(consecutivo, "^", compania, "^", ciclo, "^",
                                        registro.getCampos()
                                                        .get(GeneralParameterEnum.CODIGORUTA
                                                                        .getName())
                                                        .toString());
        try {
            ejbSPTres.auditarModif(compania, formulario,
                            Integer.parseInt(tipoMod), campo,
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que ejecuta el procedimiento PR_AUDITARREGCOMPARAR
     *
     * @param consecutivo
     * Consecutivo del fraude
     * @param camposMod
     * Nombre y valores de los campos modificados en la edicion
     * @param contador
     * Numero de veces que se debe recorrer la cadena camposMod
     * @param formulario
     * Nombre del formulario modificado
     */
    private void auditarRegComparar(String consecutivo, StringBuilder camposMod,
        int contador, String formulario) {
        String campo = SysmanFunciones
                        .concatenar(consecutivo, "^", compania, "^", ciclo, "^",
                                        registro.getCampos()
                                                        .get(GeneralParameterEnum.CODIGORUTA
                                                                        .getName())
                                                        .toString());
        if (contador != 0) {
            try {
                ejbSPTres.auditarRegistroComparar(compania, formulario, campo,
                                camposMod.toString(),
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(ciclo), registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGORUTA
                                                                .getName())
                                                .toString(),
                                Integer.parseInt(registro.getCampos().get("ANO")
                                                .toString()),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.PERIODO
                                                                .getName())
                                                .toString(),
                                contador);
            }
            catch (NumberFormatException | SystemException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {

        // <CODIGO_DESARROLLADO>
        try {

            String parametro = ejbUtil.consultarParametro(compania,
                            "CONSECUTIVO Y METROS EN FRAUDES", modulo,
                            new Date(), false);

            if ("SI".equals(parametro)) {
                conmetVisible = true;
            }
            else {
                conmetVisible = false;

            }
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {

            registro.getCampos().put(estado, "A");
            registro.getCampos().put("CUOTASDECOBRO", 1);

        }

        actualizarReg();
        precargarRegistro();
        cargarListaAnoCobro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion al registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("CICLO", ciclo);
        registro.getCampos().remove(nombreCodigoRuta);
        registro.getCampos().remove("PER");
        registro.getCampos().put("ANO", ano);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        try {
            int consecutivoF = (int) ejbUtil.generarSiguienteConsecutivo(
                            "SP_FRAUDES",
                            "COMPANIA= " + compania,
                            GeneralParameterEnum.CONSECUTIVO.getName());

            registro.getCampos().put(consecutivo, consecutivoF);

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());

        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion al registro
     *
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        auditarModif(registro.getCampos().get(consecutivo).toString(), "1",
                        frmFraudes);

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
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos().remove("PER");
            registro.getCampos().remove("NOMBRERUTA");
            registro.getCampos().remove("ANOCIERRE");
            registro.getCampos().remove(
                            FrmfraudesControladorEnum.PARAM4.getValue());
            registro.getCampos().remove("PERCIERRE");
            registro.getCampos().remove(
                            FrmfraudesControladorEnum.PARAM5.getValue());

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
        if (ACCION_MODIFICAR.equals(accion)) {
            Registro regAnterior = new Registro(registroIni);

            compararRegistros(regAnterior, registro, frmFraudes);
        }
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
        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CLASE.getName(), "32");

        param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registro.getCampos().get(consecutivo));

        Registro rs;
        try {
            rs = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesControladorUrlEnum.URL1616
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1301"));
                return false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        auditarModif(registro.getCampos().get(consecutivo).toString(), "2",
                        frmFraudes);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna el objeto indiceSubfraudescarta
     *
     * @return indiceSubfraudescarta
     */
    public int getIndiceSubfraudescarta() {
        return indiceSubfraudescarta;
    }

    /**
     * Asigna el objeto indiceSubfraudescarta
     *
     * @param indiceSubfraudescarta
     */
    public void setIndiceSubfraudescarta(int indiceSubfraudescarta) {
        this.indiceSubfraudescarta = indiceSubfraudescarta;
    }

    /**
     * Retorna el objeto visibleDigitalizacion
     *
     * @return visibleDigitalizacion
     */
    public boolean isVisibleDigitalizacion() {
        return visibleDigitalizacion;
    }

    /**
     * Asigna el objeto visibleDigitalizacion
     *
     * @param visibleDigitalizacion
     */
    public void setVisibleDigitalizacion(boolean visibleDigitalizacion) {
        this.visibleDigitalizacion = visibleDigitalizacion;
    }

    /**
     * Retorna el atributo inactivoBoton
     *
     * @return inactivoBoton
     */
    public boolean isInactivoBoton() {
        return inactivoBoton;
    }

    /**
     * Asigna el atributo inactivoBoton
     *
     * @param inactivoBoton
     */
    public void setInactivoBoton(boolean inactivoBoton) {
        this.inactivoBoton = inactivoBoton;
    }

    /**
     * Retorna el objeto bloqueadoRuta
     *
     * @return bloqueadoRuta
     */
    public boolean isBloqueadoRuta() {
        return bloqueadoRuta;
    }

    /**
     * Asigna el objeto bloqueadoRuta
     *
     * @param bloqueadoRuta
     */
    public void setBloqueadoRuta(boolean bloqueadoRuta) {
        this.bloqueadoRuta = bloqueadoRuta;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaPregunta
     *
     * @return listaPregunta
     */
    public List<Registro> getListaPregunta() {
        return listaPregunta;
    }

    /**
     * Asigna la lista listaPregunta
     *
     * @param listaPregunta
     */
    public void setListaPregunta(List<Registro> listaPregunta) {
        this.listaPregunta = listaPregunta;
    }

    /**
     * Retorna la lista listaAnoCobro
     *
     * @return listaAnoCobro
     */
    public List<Registro> getListaAnoCobro() {
        return listaAnoCobro;
    }

    /**
     * Asigna la lista listaAnoCobro
     *
     * @param listaAnoCobro
     */
    public void setListaAnoCobro(List<Registro> listaAnoCobro) {
        this.listaAnoCobro = listaAnoCobro;
    }

    /**
     * Retorna la lista de listaPeriodoCobro
     *
     * @return listaPeriodoCobro
     */
    public List<Registro> getListaPeriodoCobro() {
        return listaPeriodoCobro;
    }

    /**
     * Asigna la lista listaPeriodoCobro
     *
     * @param listaPeriodoCobro
     */
    public void setListaPeriodoCobro(List<Registro> listaPeriodoCobro) {
        this.listaPeriodoCobro = listaPeriodoCobro;
    }

    /**
     * Retorna la lista listaSubClase
     *
     * @return listaSubClase
     */
    public List<Registro> getListaSubClase() {
        return listaSubClase;
    }

    /**
     * Asigna la lista listaSubClase
     *
     * @param listaSubClase
     */
    public void setListaSubClase(List<Registro> listaSubClase) {
        this.listaSubClase = listaSubClase;
    }

    /**
     * Retorna la lista listaTipo
     *
     * @return listaTipo
     */
    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     *
     * @param listaTipo
     */
    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    // </SET_GET_LISTAS>
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
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna el objeto auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna el objeto auxiliar
     *
     * @param auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * Retorna el objeto conmetVisible
     *
     * @return conmetVisible
     */
    public boolean isConmetVisible() {
        return conmetVisible;
    }

    /**
     * Asigna el objeto conmetVisible
     *
     * @param conmetVisible
     */
    public void setConmetVisible(boolean conmetVisible) {
        this.conmetVisible = conmetVisible;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubcartapregunta
     *
     * @return listaSubcartapregunta
     */
    public List<Registro> getListaSubcartapregunta() {
        return listaSubcartapregunta;
    }

    /**
     * Asigna la lista listaSubcartapregunta
     *
     * @param listaSubcartapregunta
     */
    public void setListaSubcartapregunta(List<Registro> listaSubcartapregunta) {
        this.listaSubcartapregunta = listaSubcartapregunta;
    }

    /**
     * Retorna la lista listaSubfraudescarta
     *
     * @return listaSubfraudescarta
     */
    public List<Registro> getListaSubfraudescarta() {
        return listaSubfraudescarta;
    }

    /**
     * Asigna la lista listaSubfraudescarta
     *
     * @param listaSubfraudescarta
     */
    public void setListaSubfraudescarta(List<Registro> listaSubfraudescarta) {
        this.listaSubfraudescarta = listaSubfraudescarta;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * retorna el objeto registroSubsubCartaPregunta
     *
     * @return registroSubsubCartaPregunta
     */
    public Registro getRegistroSubsubCartaPregunta() {
        return registroSubsubCartaPregunta;
    }

    /**
     * Asigna el objeto registroSubsubCartaPregunta
     *
     * @param registroSubsubCartaPregunta
     */
    public void setRegistroSubsubCartaPregunta(
        Registro registroSubsubCartaPregunta) {
        this.registroSubsubCartaPregunta = registroSubsubCartaPregunta;
    }

    /**
     * Retorna el objeto registroSubsubFraudesCarta
     *
     * @return registroSubsubFraudesCarta
     */
    public Registro getRegistroSubsubFraudesCarta() {
        return registroSubsubFraudesCarta;
    }

    /**
     * Asigna el objeto registroSubsubFraudesCarta
     *
     * @param registroSubsubFraudesCarta
     */
    public void setRegistroSubsubFraudesCarta(
        Registro registroSubsubFraudesCarta) {
        this.registroSubsubFraudesCarta = registroSubsubFraudesCarta;
    }

    /**
     * Retorna el objeto registroCarta
     *
     * @return registroCarta
     */
    public Registro getRegistroCarta() {
        return registroCarta;
    }

    /**
     * Asigna el objeto registroCarta
     *
     * @param registroCarta
     */
    public void setRegistroCarta(Registro registroCarta) {
        this.registroCarta = registroCarta;
    }

    // </SET_GET_ADICIONALES>
}
