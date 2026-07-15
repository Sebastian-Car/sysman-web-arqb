/*-
 * FrmNodoVariablesControlador.java
 *
 * 1.0
 * 
 * 10/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmNodoVariablesControladorEnum;
import com.sysman.workflow.enums.FrmNodoVariablesControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>frmnodovariables</code>, encargado
 * de gestionar las operaciones CRUD y propias del formulario.
 *
 * @version 1.0, 10/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmNodoVariablesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /** Constante a nivel de clase que aloja la cadena: ADJUNTO. */
    private final String cAdjunto = FrmNodoVariablesControladorEnum.ADJUNTO
                    .getValue();

    /** Constante a nivel de clase que aloja la cadena: CATEGORIA. */
    private final String cCategoria = FrmNodoVariablesControladorEnum.CATEGORIA
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena: CODIGO_NODO.
     */
    private final String cCodigoNodo = FrmNodoVariablesControladorEnum.CODIGO_NODO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena: CODIGOELEMENTO.
     */
    private final String cCodigoElemento = GeneralParameterEnum.CODIGOELEMENTO
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena: CODIGO_PROCESO.
     */
    private final String cCodigoProceso = FrmNodoVariablesControladorEnum.CODIGO_PROCESO
                    .getValue();

    /** Constante a nivel de clase que aloja la cadena: COMPANIA. */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena: MANEJA_ADJUNTO.
     */
    private final String cNodo = FrmNodoVariablesControladorEnum.NODO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena: NOMBRELARGO.
     */
    private final String cNombreLargo = FrmNodoVariablesControladorEnum.NOMBRELARGO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena: PROCESO.
     */
    private final String cProceso = FrmNodoVariablesControladorEnum.PROCESO
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /** Atributo que contiene el codigo del proceso del nodo. */
    private String codigoProceso;

    /** Atributo que contiene el codigo del nodo. */
    private String codigoNodo;

    /** Atributo que contiene el nombre del proceso. */
    private String nombreProceso;

    /** Atributo que contiene el nombre del nodo. */
    private String nombreNodo;

    /** Atributo que contiene la ruta relativa del nodo. */
    private String ruta;

    /**
     * Variable que contiene el codigo del registro seleccionado en el
     * combo Codigo Servicio.
     */
    private String codRegistroTabla;

    /** Variable que contiene el codigo del menu asociado al nodo. */
    private String nodoMenu;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo Estado (CB5846). */
    private List<Registro> listaCbEstado;

    /**
     * Lista que contiene los detalles del combo Tipo Dato (CB5853).
     */
    private List<Registro> listaTipoDato;

    /**
     * Lista que contiene los detalles del Combo Servicio (CB5931).
     */
    private List<Registro> listaCbTabla;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo Personal (CB5932),
     * aplica cuando el item seleccionado en el combo Servicio
     * (CB5931) es PERSONAL (13).
     */
    private RegistroDataModelImpl listaCbPersonal;

    /**
     * Lista que contiene los detalles del combo Dependencia (CB5941),
     * aplica cuando el item seleccionado en el combo Servicio
     * (CB5931) es DEPENDENCIA (14).
     */
    private RegistroDataModelImpl listaCbDependencia;

    /**
     * Lista que contiene los detalles del combo Inventario (CB5943),
     * aplica cuando el item seleccionado en el combo Servicio
     * (CB5931) es INVENTARIO (15).
     */
    private RegistroDataModelImpl listaCbInventario;

    /**
     * Lista que contiene los detalles del combo Devolutivo (CB5978),
     * aplica cuando el item seleccionado en el combo Servicio
     * (CB5931) es DEVOLUTIVO (16).
     */
    private RegistroDataModelImpl listaCbDevolutivo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmNodoVariablesControlador
     */
    public FrmNodoVariablesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1755
            numFormulario = GeneralCodigoFormaEnum.FRM_NODO_VARIABLES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramEntrada = SessionUtil.getFlash();

            if (paramEntrada != null) {
                codigoProceso = paramEntrada
                                .get(FrmNodoVariablesControladorEnum.PR_CODIGO_PROCESO
                                                .getValue())
                                .toString();

                codigoNodo = paramEntrada
                                .get(FrmNodoVariablesControladorEnum.PR_CODIGO_NODO
                                                .getValue())
                                .toString();

                nombreProceso = paramEntrada
                                .get(FrmNodoVariablesControladorEnum.PR_PROCESO_NOM
                                                .getValue())
                                .toString();

                nombreNodo = paramEntrada
                                .get(FrmNodoVariablesControladorEnum.PR_NODO_NOM
                                                .getValue())
                                .toString();

                ruta = paramEntrada.get(FrmNodoVariablesControladorEnum.PR_RUTA
                                .getValue()).toString();
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCbEstado();
        cargarListaTipoDato();
        cargarListaCbTabla();
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
        enumBase = GenericUrlEnum.NODO_VARIABLES;

        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se asignan las URLs y los parametros asociados de las
     * operaciones CRUD del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cProceso, codigoProceso);
        parametrosListado.put(cNodo, codigoNodo);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaCbEstado</code> asociada al combo
     * Estado (CB5846).
     */
    public void cargarListaCbEstado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 4); // Tipos de estado

        try {
            listaCbEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoVariablesControladorUrlEnum.URL5515
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaTipoDato</code> asociado al combo
     * Tipo Dato (CB5853).
     */
    public void cargarListaTipoDato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 6); // Tipos de dato

        try {
            listaTipoDato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoVariablesControladorUrlEnum.URL5515
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCbPersonal</code> asociada al combo
     * Personal (CB5932).
     */
    public void cargarListaCbPersonal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodoVariablesControladorUrlEnum.URL0001
                                                        .getValue());

        listaCbPersonal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     * Carga la lista: <code>listaCbDependencia</code> asociada al
     * combo Dependencia (CB5941).
     */
    public void cargarListaCbDependencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodoVariablesControladorUrlEnum.URL0002
                                                        .getValue());

        listaCbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: <code>listaCbInventario</code> asociada al
     * combo Dependencia (CB5943).
     */
    public void cargarListaCbInventario() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodoVariablesControladorUrlEnum.URL0003
                                                        .getValue());

        listaCbInventario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cCodigoElemento);
    }

    /**
     * Carga la lista: <code>listaCbDevolutivo</code> asociada al
     * combo Devolutivo (CB5943).
     */
    public void cargarListaCbDevolutivo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmNodoVariablesControladorUrlEnum.URL0004
                                                        .getValue());

        listaCbDevolutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.SERIE.getName());
    }

    /**
     * Carga la lista: <code>listaCbTabla</code> asociada al combo
     * Servicio (CB5931).
     */
    public void cargarListaCbTabla() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCategoria, 9);

        try {
            listaCbTabla = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoVariablesControladorUrlEnum.URL5515
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el check Maneja Adjunto (CK1553).
     * Cuando el indicador esta marcado genera la ruta del campo
     * adjunto (CP55322).
     */
    public void cambiarCkAdjunto() {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get("MANEJA_ADJUNTO")) {
            registro.getCampos().put(cAdjunto, ruta);
        }
        else {
            registro.getCampos().put(cAdjunto, "");
            registro.getCampos().put("ADJUNTO_OBLIGATORIO", false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el combo Servicio (CB5931).
     */
    public void cambiarCbTabla() {
        // <CODIGO_DESARROLLADO>
        String tabla = SysmanFunciones
                        .nvl(registro.getCampos().get("TABLA"), "").toString();

        codRegistroTabla = "";

        registro.getCampos().put(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                        .getValue(), "");

        registro.getCampos().put("SUC_REG_TABLA", "");

        registro.getCampos()
                        .remove(FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                                        .getValue());

        switch (tabla) {
        case "13": // PERSONAL
            cargarListaCbPersonal();
            break;
        case "14": // DEPENDENCIA
            cargarListaCbDependencia();
            break;
        case "15": // INVENTARIO
            cargarListaCbInventario();
            break;
        case "16": // DEVOLUTIVO
            cargarListaCbDevolutivo();
            break;
        default:
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el valor del campo Codigo
     * (CP54897).
     */
    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCbPersonal</code> asociada al combo Codigo Servicio
     * (CB5932).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCbPersonal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codRegistroTabla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                        .getValue(), codRegistroTabla);

        registro.getCampos().put(FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                        .getValue(),
                        registroAux.getCampos().get("NOMBRECOMPLETO"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCbDependencia</code> asociada al combo Codigo
     * Servicio (CB5941).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codRegistroTabla = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        registro.getCampos().put(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                        .getValue(), codRegistroTabla);

        registro.getCampos().put(FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                        .getValue(),
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCbInventario</code> asociada al combo Codigo
     * Servicio (CB5943).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCbInventario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codRegistroTabla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                        .getValue(), codRegistroTabla);

        registro.getCampos().put(
                        FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                                        .getValue(),
                        registroAux.getCampos().get(cNombreLargo));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCbDevolutivo</code> asociada al combo Codigo
     * Servicio (CB5978).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCbDevolutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codRegistroTabla = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.SERIE.getName()),
                                        "")
                        .toString();

        registro.getCampos().put(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                        .getValue(), codRegistroTabla);

        registro.getCampos().put(
                        FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                                        .getValue(),
                        registroAux.getCampos().get(cNombreLargo));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
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
        recuperarRegNodo();
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
            registro.getCampos().put(cCodigoProceso, codigoProceso);
            registro.getCampos().put(cCodigoNodo, codigoNodo);
            registro.getCampos().put("PROCESO_NOM", nombreProceso);
            registro.getCampos().put("NODO_NOM", nombreNodo);
            registro.getCampos().put("ESTADO", 4); /*- Estado Tipo Activo*/
        }
        else {
            codRegistroTabla = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                                                            .getValue()),
                                            "")
                            .toString();

            cargarNomRegTabla();
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
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion o actualizacion
     * del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cCodigoProceso);
            registro.getCampos().remove(cCodigoNodo);
        }

        registro.getCampos().remove("CODIGO_VAR");
        registro.getCampos().remove("NODO_NOM");
        registro.getCampos().remove("PROCESO_NOM");
        registro.getCampos()
                        .remove(FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion o
     * actualizacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        cargarRegistro();
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
        // </CODIGO_DESARROLLADO>
        return true;
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

    /** Metodo ejecutado cuando se cierra el formulario. */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Busca y carga el nombre asociado al valor seleccionado en el
     * combo Codigo Servicio.
     */
    private void cargarNomRegTabla() {
        String tabla = SysmanFunciones
                        .nvl(registro.getCampos().get("TABLA"), "").toString();

        String nomReg = "";

        switch (tabla) {
        case "13": // PERSONAL
            cargarListaCbPersonal();
            nomReg = recuperarNomRegTabla(listaCbPersonal,
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            "NOMBRECOMPLETO");
            break;
        case "14": // DEPENDENCIA
            cargarListaCbDependencia();
            nomReg = recuperarNomRegTabla(listaCbDependencia,
                            GeneralParameterEnum.CODIGO.getName(),
                            "NOMBRE");
            break;
        case "15": // INVENTARIO
            cargarListaCbInventario();
            nomReg = recuperarNomRegTabla(listaCbInventario, cCodigoElemento,
                            cNombreLargo);
            break;
        case "16": // DEVOLUTIVO
            cargarListaCbDevolutivo();
            nomReg = recuperarNomRegTabla(listaCbDevolutivo,
                            GeneralParameterEnum.SERIE.getName(), cNombreLargo);
            break;
        default:
            break;
        }

        registro.getCampos().put(FrmNodoVariablesControladorEnum.NOM_REG_TABLA
                        .getValue(), nomReg);
    }

    /**
     * Recupera el nombre: <code>campo</code> de un registro asociado
     * a una lista: <code>list</code>.
     * 
     * @param list
     * -> Lista que contiene los registros.
     * @param clave
     * -> Valor clave que identifica el registro en la lista
     * <code>list</code>.
     * @param campo
     * -> Nombre del campo que contiene el nombre en el registro.
     * @return El nombre del registro.
     */
    private String recuperarNomRegTabla(RegistroDataModelImpl list,
        String clave, String campo) {
        Map<String, Object> param = new TreeMap<>();
        param.put(clave, registro.getCampos()
                        .get(FrmNodoVariablesControladorEnum.COD_REG_TABLA
                                        .getValue()));

        String nombre = "";

        try {
            nombre = SysmanFunciones.nvl(list.getRegistroUnico(param)
                            .getCampos().get(campo), "").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return nombre;
    }

    /** Metodo que permite recuperar los datos del nodo. */
    private void recuperarRegNodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cProceso, codigoProceso);
        param.put(cNodo, codigoNodo);

        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoVariablesControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));

            if (regAux != null) {
                nodoMenu = SysmanFunciones
                                .nvl(regAux.getCampos().get("MENU"), "")
                                .toString();
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <SET_GET_ATRIBUTOS>
    public String getCodRegistroTabla() {
        return codRegistroTabla;
    }

    public void setCodRegistroTabla(String codRegistroTabla) {
        this.codRegistroTabla = codRegistroTabla;
    }

    public String getNodoMenu() {
        return nodoMenu;
    }

    public void setNodoMenu(String nodoMenu) {
        this.nodoMenu = nodoMenu;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCbEstado
     * 
     * @return listaCbEstado
     */
    public List<Registro> getListaCbEstado() {
        return listaCbEstado;
    }

    /**
     * Asigna la lista listaCbEstado
     * 
     * @param listaCbEstado
     * Variable a asignar en listaCbEstado
     */
    public void setListaCbEstado(List<Registro> listaCbEstado) {
        this.listaCbEstado = listaCbEstado;
    }

    /**
     * @return the listaTipoDato
     */
    public List<Registro> getListaTipoDato() {
        return listaTipoDato;
    }

    /**
     * @param listaTipoDato
     * the listaTipoDato to set
     */
    public void setListaTipoDato(List<Registro> listaTipoDato) {
        this.listaTipoDato = listaTipoDato;
    }

    public List<Registro> getListaCbTabla() {
        return listaCbTabla;
    }

    public void setListaCbTabla(List<Registro> listaCbTabla) {
        this.listaCbTabla = listaCbTabla;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCbPersonal() {
        return listaCbPersonal;
    }

    public void setListaCbPersonal(RegistroDataModelImpl listaCbPersonal) {
        this.listaCbPersonal = listaCbPersonal;
    }

    public RegistroDataModelImpl getListaCbDependencia() {
        return listaCbDependencia;
    }

    public void setListaCbDependencia(
        RegistroDataModelImpl listaCbDependencia) {
        this.listaCbDependencia = listaCbDependencia;
    }

    public RegistroDataModelImpl getListaCbInventario() {
        return listaCbInventario;
    }

    public void setListaCbInventario(RegistroDataModelImpl listaCbInventario) {
        this.listaCbInventario = listaCbInventario;
    }

    public RegistroDataModelImpl getListaCbDevolutivo() {
        return listaCbDevolutivo;
    }

    public void setListaCbDevolutivo(RegistroDataModelImpl listaCbDevolutivo) {
        this.listaCbDevolutivo = listaCbDevolutivo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
