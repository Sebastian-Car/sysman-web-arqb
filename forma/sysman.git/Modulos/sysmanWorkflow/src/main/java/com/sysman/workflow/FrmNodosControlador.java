/*-
 * FrmNodosControlador.java
 *
 * 1.0
 * 
 * 09/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmNodoRaciControladorEnum;
import com.sysman.workflow.enums.FrmNodoVariablesControladorEnum;
import com.sysman.workflow.enums.FrmNodosControladorEnum;
import com.sysman.workflow.enums.FrmNodosControladorUrlEnum;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>frmnodos</code>.
 *
 * @version 1.0, 09/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmNodosControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que aloja el caracter delimitador de
     * la ruta en el sistema operativo.
     */
    private static final String FILE_SEPARADOR = File.separator;

    /**
     * Constante a nivel de clase que aloja la longitud maxima
     * permitida para la ruta del nodo.
     */
    private static final int LONG_RUTA_NODO = 82;

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /** Constante a nivel de clase que aloja la cadena: CATEGORIA. */
    private final String cCategoria = FrmNodosControladorEnum.CATEGORIA
                    .getValue();

    /** Constante a nivel de clase que aloja la cadena: CODIGO. */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena: CODIGO_PROCESO.
     */
    private final String cCodigoProceso = FrmNodosControladorEnum.CODIGO_PROCESO
                    .getValue();

    /** Constante a nivel de clase que aloja la cadena: COMPANIA. */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /** Constante a nivel de clase que aloja la cadena: NOMBRE. */
    private final String cNombre = GeneralParameterEnum.NOMBRE.getName();

    /**
     * Constante a nivel de clase que aloja la cadena: PROCESO_NOM.
     */
    private final String cProcesoNom = FrmNodosControladorEnum.PROCESO_NOM
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el nombre del menu seleccionado en el
     * combo Menu (CB5957).
     */
    private String menuNom;

    /**
     * Atributo que almacena el nombre del formato seleccionado en el
     * combo Formato (CB5985).
     */
//    private String formatoNom;

    /** Variable que aloja el codigo del proceso. */
    private String proceso;

    /**
     * Coleccion que contiene la llave que identifica el proceso desde
     * el cual se accede a este formulario.
     */
    private Map<String, Object> ridProceso;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo Tipo Calidad
     * (CB5840).
     */
    private List<Registro> listaTipoCalidad;

    /**
     * Lista que contiene los detalles del combo Tipo Operacion
     * (CB5841).
     */
    private List<Registro> listaTipoOperacion;

    /**
     * Lista que contiene los detalles del combo Tipo Estado (CB5844).
     */
    private List<Registro> listaEstado;

    /**
     * Lista que contiene los detalles del combo Tipo Radicado
     * (CB5845).
     */
    private List<Registro> listaTipoRadicado;

    /**
     * Lista que contiene los detalles del combo Modulo (CB5962).
     */
    private List<Registro> listaCbModulo;

    /**
     * Lista que contiene los detalles del combo Tipo Dia (CB6163).
     */
    private List<Registro> listaCbTipoDia;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo Proceso (CB5839). */
    private RegistroDataModelImpl listaCodigoProceso;

    /**
     * Lista que contiene los detalles del combo Dependencia (CB5842).
     */
    private RegistroDataModelImpl listaDependencia;

    /**
     * Lista que contiene los detalles del combo Serie Documental
     * (CB5843).
     */
    private RegistroDataModelImpl listaSerieDocumental;

    /**
     * Lista que contiene los detalles del combo Menu (CB5957).
     */
    private RegistroDataModelImpl listaMenu;

    /**
     * Lista que contiene los detalles del combo Formato (CB5985).
     */
    
    private boolean ckRecepcionCorrespondencia;
    
    /**
     * 
     * Check de recepcion de correspondencia
     */
    private boolean ckEnvioCorrespondencia;
    
    /**
     * 
     * Check de Envio de correspondencia
     */
    
    private boolean ckValidaDependencia;
    
    /**
     * 
     * Check de Envio de correspondencia
     */


    
 //   private RegistroDataModelImpl listaFormato;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmNodosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmNodosControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1753
            numFormulario = GeneralCodigoFormaEnum.FRM_NODOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                proceso = paramIn.get(
                                FrmNodosControladorEnum.PR_PROCESO.getValue())
                                .toString();

                ridProceso = (Map<String, Object>) paramIn
                                .get(FrmNodosControladorEnum.PR_RID.getValue());
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
        cargarListaDependencia();
        cargarListaSerieDocumental();

        cargarListaTipoDia();
        cargarListaTipoCalidad();
        cargarListaTipoOperacion();
        cargarListaEstado();
        cargarListaTipoRadicado();
        cargarListaCbModulo();
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
        enumBase = GenericUrlEnum.NODOS;

        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se asignan las URLs correspondientes a las operaciones CRUD del
     * formulario y los parametros de la grilla.
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(FrmNodosControladorEnum.PROCESO.getValue(),
                        proceso);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaTipoCalidad</code> asociada al combo
     * Tipo Calidad (CB5840).
     */
    public void cargarListaTipoCalidad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 2); // Tipos de calidad

        try {
            listaTipoCalidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodosControladorUrlEnum.URL6936
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCbTipoDia</code> asociada al combo
     * Tipo Dia (CB6163).
     */
    public void cargarListaTipoDia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 11); // Tipos dia

        try {
            listaCbTipoDia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodosControladorUrlEnum.URL6936
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaTipoOperacion</code> asociada al
     * combo Tipo Operacion (CB5841).
     */
    public void cargarListaTipoOperacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 3); // Tipos de operacion

        try {
            listaTipoOperacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodosControladorUrlEnum.URL6936
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaEstado</code> asociada al combo Tipo
     * Estado (CB5844).
     */
    public void cargarListaEstado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 4); // Tipos de estado

        try {
            listaEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodosControladorUrlEnum.URL6936
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCbModulo</code> asociada al combo
     * Modulo (CB5962).
     */
    public void cargarListaCbModulo() {
        try {
            listaCbModulo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaTipoRadicado</code> asociada al
     * combo Tipo Estado (CB5844).
     */
    public void cargarListaTipoRadicado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 5); // Tipos de radicado

        try {
            listaTipoRadicado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodosControladorUrlEnum.URL6936
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCodigoProceso</code> asociada al
     * combo Proceso (CB5839).
     */
    public void cargarListaCodigoProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodosControladorUrlEnum.URL7996
                                                        .getValue());

        listaCodigoProceso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
    }

    /**
     * Carga la lista: <code>listaDependencia</code> asociada al combo
     * Dependencia (CB5842).
     */
    public void cargarListaDependencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodosControladorUrlEnum.URL8542
                                                        .getValue());

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
    }

    /**
     * Carga la lista: <code>listaSerieDocumental</code> asociada al
     * combo Serie Documental (CB5843).
     */
    public void cargarListaSerieDocumental() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodosControladorUrlEnum.URL9097
                                                        .getValue());

        listaSerieDocumental = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
    }

    /**
     * Carga la lista: <code>listaMenu</code> asociada al combo Menu
     * (CB5957).
     */
    public void cargarListaMenu() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmNodosControladorEnum.APLICACION.getValue(),
                        registro.getCampos().get("MODULO"));

        param.put(FrmNodosControladorEnum.TIPO.getValue(), "P");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodosControladorUrlEnum.URL0003
                                                        .getValue());

        listaMenu = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
    }

    

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del check Movimiento
     * (CK1551).
     */
    public void cambiarCkMovimiento() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el valor del campo Codigo
     * (CP54776).
     */
    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>
        generarRutaAdjunto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el item del combo Modulo (CB5962).
     */
    public void cambiarCbModulo() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(FrmNodosControladorEnum.MENU.getValue(), "");
        menuNom = "";

        cargarListaMenu();
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control ckRecepcionCorrespondencia
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarckRecepcionCorrespondencia() {
    	
    	ckRecepcionCorrespondencia = (boolean) registro.getCampos().get("RECEPCION_CORRES");	
         if (ckRecepcionCorrespondencia) {
        	registro.getCampos().put("ENVIO_CORRES", false);
         }
    }
     /**
     * Metodo ejecutado al cambiar el control ckEnvioCorrespondencia
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarckValidaDependencia() {
    	
    	ckValidaDependencia = (boolean) registro.getCampos().get("VALIDA_AL_TRAMITAR");	
    	
    }
   
	public void cambiarckEnvioCorrespondencia() {

    	ckEnvioCorrespondencia = (boolean) registro.getCampos().get("ENVIO_CORRES");
		if (ckEnvioCorrespondencia) {
			registro.getCampos().put("RECEPCION_CORRES", false);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCodigoProceso</code> asociado al combo
     * Proceso(CB5839).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoProceso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cCodigoProceso,
                        registroAux.getCampos().get(cCodigo));

        registro.getCampos().put(cProcesoNom,
                        registroAux.getCampos().get(cNombre));

        registro.getCampos().remove("CODIGO");
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaDependencia</code> asociada al combo Dependencia
     * (CB5842).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get(cCodigo));

        registro.getCampos().put("DEPENDENCIA_NOM",
                        registroAux.getCampos().get(cNombre));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaSerieDocumental</code> asociada al combo Serie
     * Docuemntal (CB5843).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSerieDocumental(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SERIE_DOCUMENTAL",
                        registroAux.getCampos().get(cCodigo));

        registro.getCampos().put("SERIE_DOC_NOM",
                        registroAux.getCampos().get(cNombre));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaMenu</code> asociada al combo Menu (CB5957).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaMenu(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        String menuCod = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        registro.getCampos().put(FrmNodosControladorEnum.MENU.getValue(),
                        menuCod);

        menuNom = SysmanFunciones.nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }



    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Variables (BT3084) en la
     * vista. Redirecciona al formulario de variables (1755).
     */
    public void oprimirBtVariables() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);

        String[] campos = new String[5];
        campos[0] = FrmNodoVariablesControladorEnum.PR_CODIGO_PROCESO
                        .getValue();

        campos[1] = FrmNodoVariablesControladorEnum.PR_CODIGO_NODO.getValue();
        campos[2] = FrmNodoVariablesControladorEnum.PR_PROCESO_NOM.getValue();
        campos[3] = FrmNodoVariablesControladorEnum.PR_NODO_NOM.getValue();
        campos[4] = FrmNodoVariablesControladorEnum.PR_RUTA.getValue();

        Object[] valores = new Object[5];
        valores[0] = registro.getCampos().get(cCodigoProceso);
        valores[1] = registro.getCampos().get(cCodigo);
        valores[2] = registro.getCampos().get(cProcesoNom);
        valores[3] = registro.getCampos().get(cNombre);
        valores[4] = registro.getCampos().get("RUTA");

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_NODO_VARIABLES_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton RACI (BT3102) en la vista.
     * Redirecciona al formulario nodos raci (1773).
     */
    public void oprimirBtRaci() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);

        String[] campos = new String[2];
        campos[0] = FrmNodoRaciControladorEnum.PR_CODIGO_PROCESO.getValue();
        campos[1] = FrmNodoRaciControladorEnum.PR_CODIGO_NODO.getValue();

        Object[] valores = new Object[2];
        valores[0] = registro.getCampos().get(cCodigoProceso);
        valores[1] = registro.getCampos().get(cCodigo);

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_NODOS_RACI_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
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
     * Metodo ejecutado en el momento despues de cargar el registro.
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (css == null) {
            cargarListaCodigoProceso();

            registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), 4); // Estado
                                                                                // Activo
            registro.getCampos().put("FECHA_CREACION", new Date());
            registro.getCampos().put("ELABORO_FECHA", new Date());
            registro.getCampos().put("MOVIMIENTO", true);
            registro.getCampos().put("TIEMPO_MINIMO", 0);
            registro.getCampos().put("TIEMPO_MAXIMO", 30);
            registro.getCampos().put("CODIGO_PROCESO", proceso);

            String procesoNom = recuperarNom(listaCodigoProceso, cCodigo,
                            "CODIGO_PROCESO");

            registro.getCampos().put("PROCESO_NOM", procesoNom);

  //          menuNom = formatoNom = "";
        }
        else {
            cargarListaMenu();
   //         cargarListaFormato();

            menuNom = recuperarNom(listaMenu, cCodigo,
                            FrmNodosControladorEnum.MENU.getValue());

  
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return true.
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        String nodoCod = SysmanFunciones
                        .nvl(registro.getCampos().get("CODIGO"), "").toString();

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(FrmNodosControladorEnum.PROCESO.getValue(), proceso);
        param.put(FrmNodosControladorEnum.NODO.getValue(), nodoCod);
        param.put("ETIQUETA",
                        FrmNodosControladorEnum.S_DOC_ESTANDAR.getValue());

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        UrlBean insert = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodosControladorUrlEnum.URL001
                                                        .getValue());

        try {
            requestManager.save(insert.getUrl(), insert.getMetodo(), parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4143")
                            .replace("#NODO#", nodoCod));
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        double tiempoMinimo = Double.parseDouble(
                        registro.getCampos().get("TIEMPO_MINIMO").toString());

        double tiempoMaximo = Double.parseDouble(
                        registro.getCampos().get("TIEMPO_MAXIMO").toString());

        /* Validar que el tiempo minimo sea menor al tiempo maximo. */
        if (tiempoMinimo > tiempoMaximo) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4062"));
            return false;
        }

        if (css != null) {
            registro.getCampos().remove(cCompania);
        }

        registro.getCampos().remove(cProcesoNom);
        registro.getCampos().remove("DEPENDENCIA_NOM");
        registro.getCampos().remove("SERIE_DOC_NOM");
        registro.getCampos().remove("ID_FORMATO");

 

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
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
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        String nodoCod = registro.getCampos().get(cCodigo).toString();

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put("PROCESO", proceso);
        param.put("NODO", nodoCod);

        UrlBean delete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodosControladorUrlEnum.URL003
                                                        .getValue());

        try {
            requestManager.delete(delete.getUrl(), param);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4153")
                            .replace("#NODO#", nodoCod)
                            .replace("#PROCESO#", proceso));

            return false;
        }

        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario. Redirecciona
     * al formulario desde el cual se accede. Paral el caso, el
     * formulario de procesos (1697).
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmNodosControladorEnum.PR_RID.getValue(), ridProceso);

        Direccionador dir = new Direccionador();
        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_WF_PROCESOS_CONTROLADOR
                                        .getCodigo()));
        dir.setParametros(param);

        SessionUtil.redireccionarForma(dir, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera la ruta donde se ubicaran los documentos asociados al
     * nodo. Toma la longitud maxima que puede tomar la compania, el
     * proceso y el nodo; y crea una ruta espeficica para el nodo.
     */
    private void generarRutaAdjunto() {
        String ruta = SysmanFunciones.concatenar(FILE_SEPARADOR, "C", compania,
                        FILE_SEPARADOR, "P",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(cCodigoProceso), "").toString(),
                        FILE_SEPARADOR, "N",
                        SysmanFunciones.nvl(registro.getCampos().get(cCodigo),
                                        "").toString(),
                        FILE_SEPARADOR);

        /*- Ruta: L
        Compania: 11
         Proceso: 32
            Nodo: 32
           Otros: 7*/
        if (ruta.length() >= LONG_RUTA_NODO) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4081"));
            registro.getCampos().put("RUTA", ruta.substring(LONG_RUTA_NODO));
        }
        else {
            registro.getCampos().put("RUTA", ruta);
        }
    }

    /**
     * Recupera el nombre de un registro asociado a una lista de un
     * combo grande.
     * 
     * @param lista
     * -> Lista de combo grande.
     * @param clave
     * -> Nombre del campo en el registro de la lista.
     * @param campo
     * -> Nombre del campo en el registro principal.
     * @return Nombre del campo del registro, asociado a una lista de
     * un combo grande.
     */
    private String recuperarNom(RegistroDataModelImpl lista, String clave,
        String campo) {
        String result = "";

        /* Validar valor del campo en el registro principal */
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)) {
            return result;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(clave, registro.getCampos().get(campo));

        try {
            Registro miReg = lista.getRegistroUnico(param);

            if (miReg != null) {
                result = SysmanFunciones.nvl(miReg.getCampos().get(cNombre), "")
                                .toString();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return result;
    }

    // <SET_GET_ATRIBUTOS>
    public String getMenuNom() {
        return menuNom;
    }

    public void setMenuNom(String menuNom) {
        this.menuNom = menuNom;
    }

/*    public String getFormatoNom() {
        return formatoNom;
    }  */
    /**
     * Asigna la variable  formatoNom
     * 
     * @param  formatoNom
     * Variable a asignar en  formatoNom
     */
 /*   public void setFormatoNom(String formatoNom) {
        this.formatoNom = formatoNom;
    }   */

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoCalidad
     * 
     * @return listaTipoCalidad
     */
    public List<Registro> getListaTipoCalidad() {
        return listaTipoCalidad;
    }

    /**
     * Asigna la lista listaTipoCalidad
     * 
     * @param listaTipoCalidad
     * Variable a asignar en listaTipoCalidad
     */
    public void setListaTipoCalidad(List<Registro> listaTipoCalidad) {
        this.listaTipoCalidad = listaTipoCalidad;
    }

    /**
     * Retorna la lista listaTipoOperacion
     * 
     * @return listaTipoOperacion
     */
    public List<Registro> getListaTipoOperacion() {
        return listaTipoOperacion;
    }

    /**
     * Asigna la lista listaTipoOperacion
     * 
     * @param listaTipoOperacion
     * Variable a asignar en listaTipoOperacion
     */
    public void setListaTipoOperacion(List<Registro> listaTipoOperacion) {
        this.listaTipoOperacion = listaTipoOperacion;
    }

    /**
     * Retorna la lista listaEstado
     * 
     * @return listaEstado
     */
    public List<Registro> getListaEstado() {
        return listaEstado;
    }

    /**
     * @return the listaTipoRadicado
     */
    public List<Registro> getListaTipoRadicado() {
        return listaTipoRadicado;
    }

    /**
     * @param listaTipoRadicado
     * the listaTipoRadicado to set
     */
    public void setListaTipoRadicado(List<Registro> listaTipoRadicado) {
        this.listaTipoRadicado = listaTipoRadicado;
    }

    public List<Registro> getListaCbModulo() {
        return listaCbModulo;
    }

    public void setListaCbModulo(List<Registro> listaCbModulo) {
        this.listaCbModulo = listaCbModulo;
    }

    public List<Registro> getListaCbTipoDia() {
        return listaCbTipoDia;
    }

    public void setListaCbTipoDia(List<Registro> listaCbTipoDia) {
        this.listaCbTipoDia = listaCbTipoDia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Asigna la lista listaEstado
     * 
     * @param listaEstado
     * Variable a asignar en listaEstado
     */
    public void setListaEstado(List<Registro> listaEstado) {
        this.listaEstado = listaEstado;
    }

    /**
     * @return the listaCodigoProceso
     */
    public RegistroDataModelImpl getListaCodigoProceso() {
        return listaCodigoProceso;
    }

    /**
     * @param listaCodigoProceso
     * the listaCodigoProceso to set
     */
    public void setListaCodigoProceso(
        RegistroDataModelImpl listaCodigoProceso) {
        this.listaCodigoProceso = listaCodigoProceso;
    }

    /**
     * @return the listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * @param listaDependencia
     * the listaDependencia to set
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * @return the listaSerieDocumental
     */
    public RegistroDataModelImpl getListaSerieDocumental() {
        return listaSerieDocumental;
    }

    /**
     * @param listaSerieDocumental
     * the listaSerieDocumental to set
     */
    public void setListaSerieDocumental(
        RegistroDataModelImpl listaSerieDocumental) {
        this.listaSerieDocumental = listaSerieDocumental;
    }

    public RegistroDataModelImpl getListaMenu() {
        return listaMenu;
    }

    public void setListaMenu(RegistroDataModelImpl listaMenu) {
        this.listaMenu = listaMenu;
    }

	/**
	 * @return the ckRecepcionCorrespondencia
	 */
	public boolean isCkRecepcionCorrespondencia() {
		return ckRecepcionCorrespondencia;
	}

	/**
	 * @param ckRecepcionCorrespondencia the ckRecepcionCorrespondencia to set
	 */
	public void setCkRecepcionCorrespondencia(boolean ckRecepcionCorrespondencia) {
		this.ckRecepcionCorrespondencia = ckRecepcionCorrespondencia;
	}

	/**
	 * @return the ckEnvioCorrespondencia
	 */
	public boolean isCkEnvioCorrespondencia() {
		return ckEnvioCorrespondencia;
	}

	/**
	 * @param ckEnvioCorrespondencia the ckEnvioCorrespondencia to set
	 */
	public void setCkEnvioCorrespondencia(boolean ckEnvioCorrespondencia) {
		this.ckEnvioCorrespondencia = ckEnvioCorrespondencia;
	}

	/**
	 * @return the ckValidaDependencia
	 */
	public boolean isCkValidaDependencia() {
		return ckValidaDependencia;
	}

	/**
	 * @param ckValidaDependencia the ckValidaDependencia to set
	 */
	public void setCkValidaDependencia(boolean ckValidaDependencia) {
		this.ckValidaDependencia = ckValidaDependencia;
	}


    
  /*  public RegistroDataModelImpl getListaFormato() {
        return listaFormato;
    }

    public void setListaFormato(RegistroDataModelImpl listaFormato) {
        this.listaFormato = listaFormato;
    }  */

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
