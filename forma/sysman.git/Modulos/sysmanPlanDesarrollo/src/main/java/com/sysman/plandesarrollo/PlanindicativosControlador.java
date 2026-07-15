/*-
 * PlanindicativosControlador.java
 *
 * 1.0
 * 
 * 08/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.CentroscostosControlador;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoRemote;
import com.sysman.plandesarrollo.enums.PlanindicativosControladorEnum;
import com.sysman.plandesarrollo.enums.PlanindicativosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.TreeService;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para administar los planes indicativos segun sus
 * niveles
 *
 * @version 1.0, 08/03/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class PlanindicativosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por el cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor de la vigencia recibida por
     * parametro
     */
    private String vigencia;
    /**
     * Atributo que visualiza el ano de la vigencia seleccionada en la
     * grilla del subformulario plan de accion
     */
    private String vigenciaUno;
    /**
     * Atributo que visualiza el ano + 1 de la vigencia seleccionada
     * en la grilla del subformulario plan de accion
     */
    private String vigenciaDos;
    /**
     * Atributo que visualiza el ano + 2 de la vigencia seleccionada
     * en la grilla del subformulario plan de accion
     */
    private String vigenciaTres;
    /**
     * Atributo que visualiza el ano + 3 de la vigencia seleccionada
     * en la grilla del subformulario plan de accion
     */
    private String vigenciaCuatro;
    /**
     * Atributo que valida si el campo de Meta para el ano 1 se puede
     * editar o no
     */
    private boolean bloquearMetaUno;
    /**
     * Atributo que valida si el campo de Meta para el ano 2 se puede
     * editar o no
     */
    private boolean bloquearMetaDos;
    /**
     * Atributo que valida si el campo de Meta para el ano 3 se puede
     * editar o no
     */
    private boolean bloquearMetaTres;
    /**
     * Atributo que valida si el campo de Meta para el ano 4 se puede
     * editar o no
     */
    private boolean bloquearMetaCuatro;
    /**
     * Atributo que valida si el campo de Avance para el ano 1 se
     * puede editar o no
     */
    private boolean bloquearAvanceUno;
    /**
     * Atributo que valida si el campo de Avance para el ano 2 se
     * puede editar o no
     */
    private boolean bloquearAvanceDos;
    /**
     * Atributo que valida si el campo de Avance para el ano 3 se
     * puede editar o no
     */
    private boolean bloquearAvanceTres;
    /**
     * Atributo que valida si el campo de Avance para el ano 4 se
     * puede editar o no
     */
    private boolean bloquearAvanceCuatro;

    /**
     * Atributo que almacena lo que retorna la funcion
     * obtenerDigitosAccion
     */
    private int digitosAccion;
    /**
     * Atributo que almacena lo que retorna la funcion
     * obtenerDigitosMetaProduccion
     */
    private int digitosMetaProducto;
    /**
     * Atributo que almacena el valor del parametro recibido
     * administrador
     */
    private boolean esAdministrador;
    /**
     * * Atributo que almacena el valor del parametro recibido
     * jefeUnidad
     */
    private boolean esJefeUnidad;
    /**
     * Atributo que almacena el valor del parametro recibido numero
     */
    private String numero;

    /**
     * Atributo que valida si los botones de actualizar y eliminar se
     * hacen visibles o no
     */
    private boolean cargarBotones;
    /**
     * Atributo que almacena el valor del parametro recibido
     * dependencia
     */
    private String dependencia;
    /**
     * Atributo que almacena el valor del parametro recibido tipo
     */
    private String tipo;
    /**
     * Atributo que almacena el nonbre de la dependencia segun
     * registro seleccionado en el arbol
     */
    private String nombreDependencia;

    /**
     * Atributo que permite validar si el dialogo de eliminar es
     * visible
     */
    private boolean dialogoEliminar;
    /**
     * Atributo que almacena el id del registro seleccionado en el
     * arbol
     */
    private String claveArbol;
    /**
     * Atributo que almacena el nombre del registro seleccionado en el
     * arbol
     */
    private String nombreArbol;
    /**
     * Atributo que valida si el subFormulario indicadores de meta
     * permite realizar actualizacion
     */
    private boolean permiteEditar;
    /**
     * Atributo que almacena el valor del vigencia final del registro
     * seleccionado en el arbol
     */
    private String vigenciaFinal;
    /**
     * Atributo que almacena el valor de la vigencia inicial del
     * registro seleccionado del arbol
     */
    private String vigenciaInicial;
    /**
     * Este atributo declara el nodo superior del arbol Plan
     */
    private TreeNode raizPlan;
    /**
     * Este atributo identifica el nodo seleccionado del arbol Plan
     */
    private TreeNode nodoSeleccionadoPlan;
    /**
     * Objeto que contiene las funcionalidades necesarias para
     * trabajar con el componente de arbol Plan
     */
    @ManagedProperty("#{treeService}")
    private TreeService treeServicePlan;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista de registros de los indicadores de meta
     */
    private List<Registro> listaIndicadores;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;
    /**
     * Atributo qe referencia para el registro segun seleccion del
     * arbol
     */
    Registro registroPlan;

    // </DECLARAR_ADICIONALES>
    @EJB
    private EjbPlanDesarrolloCeroRemote ejbPlanDesarrolloCero;
    @EJB
    private EjbPlanDesarrolloUnoRemote ejbPlanDesarrolloUno;
    @EJB
    private EjbPlanDesarrolloDosRemote ejbPlanDesarrolloDos;

    /**
     * Crea una nueva instancia de PlanindicativosControlador
     */
    public PlanindicativosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1737
            numFormulario = GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                vigencia = parametrosEntrada
                                .get(PlanindicativosControladorEnum.VIGENCIA
                                                .getValue())
                                .toString();
                tipo = parametrosEntrada.get("tipo").toString();
                esAdministrador = (boolean) parametrosEntrada
                                .get(PlanindicativosControladorEnum.ADMINISTRADOR
                                                .getValue());
                esJefeUnidad = (boolean) parametrosEntrada
                                .get(PlanindicativosControladorEnum.JEFE_UNIDAD
                                                .getValue());
                dependencia = SysmanFunciones.nvlStr(
                                parametrosEntrada.get(
                                                GeneralParameterEnum.DEPENDENCIA
                                                                .getName()
                                                                .toLowerCase())
                                                .toString(),
                                "");
                numero = SysmanFunciones.nvlStr(
                                parametrosEntrada
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()
                                                                .toLowerCase())
                                                .toString(),
                                "");

            }
            registroSub = new Registro(new HashMap<String, Object>());
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
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaIndicadores();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        cargarArbolPlan();
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaIndicadores = null;
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
        enumBase = GenericUrlEnum.BP_PLAN_INDICATIVO;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();

        cargarArbolPlan();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    /**
     * 
     * Carga la lista listaIndicadores
     *
     */
    public void cargarListaIndicadores() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanindicativosControladorEnum.TIPO.getValue(), tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(), vigencia);
        param.put(GeneralParameterEnum.ID_PLAN.getName(), claveArbol);

        try {
            listaIndicadores = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.PI_INDICADOR_META_TRA
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            GenericUrlEnum.PI_INDICADOR_META_TRA
                                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    /**
     * Creacion del arbol Plan segun la consulta dada
     * 
     * 
     */
    private void cargarArbolPlan() {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("vigenciaInicial", vigencia);
            ejbPlanDesarrolloCero.cargarNivel(compania,
                            Integer.parseInt(vigencia), "");
            digitosAccion = ejbPlanDesarrolloCero.obtenerDigitosAccion();
            digitosMetaProducto = ejbPlanDesarrolloCero
                            .obtenerDigitosMetaProduccion();
            if (!esAdministrador) {
                reemplazar.put("esAdministrador", 0);
            }
            else {
                reemplazar.put("esAdministrador", 1);
            }

            reemplazar.put("accion", digitosAccion);
            reemplazar.put("metaProducto", digitosMetaProducto);
            reemplazar.put("dependencia",
                            SysmanFunciones.concatenar("'", dependencia, "'"));
            String consulta = Reporteador.resuelveConsulta(
                            "800136ArbolPlanIndicativo",
                            Integer.parseInt(modulo),
                            reemplazar);

            treeServicePlan = new TreeService();
            raizPlan = treeServicePlan.crearArbol(consulta);
        }
        catch (NumberFormatException | SystemException
                        | com.sysman.util.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Anexo
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirAnexo(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // llamado boton
        logger.info("llamado a boton");
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", claveArbol);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta());

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigenciaGubernamental", vigencia);

        param.put("idPlan", reg.getCampos().get("ID_PLAN").toString());

        param.put("digitosAccion", digitosAccion);

        param.put("nombrePlan", registro.getCampos()
                        .get(GeneralParameterEnum.DESCRIPCION.getName()));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_ANEXO_PROYECTOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cerrar
     */
    public void oprimirCerrar() {
        // <CODIGO_DESARROLLADO>
        devolverFormulario();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Eliminar
     */
    public void oprimirEliminar() {
        // <CODIGO_DESARROLLADO>
        dialogoEliminar = true;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Actualizar en la vista
     *
     */
    public void oprimirActualizar() {
        // <CODIGO_DESARROLLADO>

        try {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbPlanDesarrolloUno
                                            .actualizarPlanIndicativo(compania,
                                                            Integer.parseInt(
                                                                            vigencia),
                                                            tipo,
                                                            Long.valueOf(numero),
                                                            nombreDependencia,
                                                            digitosAccion,
                                                            SessionUtil.getUser()
                                                                            .getCodigo())),
                            SysmanFunciones.concatenar(
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNombre(),
                                            "Inconsistencias.txt"));
        }
        catch (JRException | IOException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        ;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Seguimiento en la vista
     *
     */
    public void oprimirSeguimiento() {

        Map<String, Object> params = new TreeMap<>();

        params.put("vigenciaGubernamental", vigencia);

        params.put("tipoT", tipo);

        params.put("esAdministrador", esAdministrador);

        params.put("esJefeUnidad", esJefeUnidad);

        params.put("dependencia", dependencia);

        params.put("numeroT", numero);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(params);

        direccionador
                        .setNumForm(Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_PLAN_INDICATIVO_INDI_CONTROLADOR
                                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PlanIndicativo en la vista
     *
     */
    public void oprimirPlanIndicativo() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "vigencia", "tipo", "administrador", "jefeUnidad",
                            "dependencia", "numero", "predecesor",
                            "digitosAccion", "esUsuarioConsulta",
                            "digitosMetaProducto", "vigenciaFinal",
                            "vigenciaInicial",
                            "nombrePlan" };
        Object[] valores = { vigencia, tipo, esAdministrador, esJefeUnidad,
                             dependencia, numero, claveArbol,
                             digitosAccion, esUsuarioConsulta(),
                             digitosMetaProducto, vigenciaFinal,
                             vigenciaInicial,
                             registro.getCampos().get(
                                             GeneralParameterEnum.DESCRIPCION
                                                             .getName()) };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLAN_DE_METAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PlanAccion en la vista
     *
     */
    public void oprimirPlanAccion() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "vigencia", "tipo", "administrador", "jefeUnidad",
                            "dependencia", "numero", "predecesor",
                            "digitosAccion", "esUsuarioConsulta",
                            "digitosMetaProducto", "vigenciaFinal", "idPlan",
                            "vigenciaInicial", "nombrePlan" };
        Object[] valores = { vigencia, tipo, esAdministrador, esJefeUnidad,
                             dependencia, numero, claveArbol,
                             digitosAccion, esUsuarioConsulta(),
                             digitosMetaProducto, vigenciaFinal, claveArbol,
                             vigenciaInicial,
                             registro.getCampos().get(
                                             GeneralParameterEnum.DESCRIPCION
                                                             .getName()) };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLAN_DE_ACCION_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Indicadores
     * 
     */
    public void agregarRegistroSubIndicadores() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de edicion del formulario Indicadores
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubIndicadores(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove("TIPO");
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove(
                            GeneralParameterEnum.VIGENCIA_INICIAL.getName());
            reg.getCampos().remove(GeneralParameterEnum.ID_PLAN.getName());
            reg.getCampos().remove("INDICADOR");
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().remove("LB_FIN");
            reg.getCampos().remove("REFERENTE_FIN");
            reg.getCampos().remove("META_CUATRENIO_FIN");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_INDICADOR_META_TRA
                                                            .getUpdateKey());

            // Se comenta esta sección de código para qoe e puedan
            // modificar los datos en
            // cualquier nivel del árbol, y funcione correctamente el
            // TAR n° 1000096442. jdrodriguez

            // if (permitirEditarSub())
            // {
            // JsfUtil.agregarMensajeError(idioma.getString("TB_TB4061"));
            // return;
            // }
            // else
            // {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            // }

        }
        catch (SystemException ex) {
            Logger.getLogger(CentroscostosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaIndicadores();
        }

    }

    /**
     * Metodo de eliminacion del formulario Indicadores
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubIndicadores(Registro reg) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Indicadores
     *
     */
    public void cancelarEdicionIndicadores() {
        cargarListaIndicadores();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo ejecutado al expander un nodo en el arbol Plan, lo cual
     * incluye desplegar los hijos del nodo correpondiente
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void expanderNodoPlan(NodeExpandEvent event) {
        treeServicePlan.traerDescendientes(event.getTreeNode());
    }

    /**
     * Metodo ejecutado al colapsar un nodo en el arbol Plan, lo cual
     * incluye ocultar los hijos del nodo correpondiente
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void colapsarNodoPlan(NodeCollapseEvent event) {
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        null);
        listaIndicadores.clear();
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        null);
        nombreDependencia = null;

    }

    /**
     * Metodo ejecutado al seleccionar un nodo en el arbol Plan
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void selecionarNodoPlan(NodeSelectEvent event) {
        claveArbol = treeServicePlan.getIdentificador(event.getTreeNode());
        nombreArbol = treeServicePlan.getNombre(event.getTreeNode());
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        nombreArbol);

        cargarListaIndicadores();
        cargarRegistroPlan();
        validarNivelConfigurado();

    }

    /**
     * Metodo ejecutado al deseleccionar un nodo en el arbol Plan
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void deseleccionarNodoPlan(NodeUnselectEvent event) {
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        null);
        cargarListaIndicadores();
    }

    /**
     * Metoto que se ejecuta al seleccionar un registro del arbol
     */
    public void cargarRegistroPlan() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanindicativosControladorEnum.ID.getValue(), claveArbol);
        param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(), vigencia);

        try {
            registroPlan = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            PlanindicativosControladorUrlEnum.URL566
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
            nombreDependencia = SysmanFunciones
                            .nvl(registroPlan.getCampos().get(
                                            PlanindicativosControladorEnum.NOMBREDEPENDENCIA
                                                            .getValue()),
                                            "")
                            .toString();
            registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            registroPlan.getCampos().get(
                                            GeneralParameterEnum.DEPENDENCIA
                                                            .getName()));
            vigenciaFinal = String
                            .valueOf(registroPlan.getCampos().get(
                                            PlanindicativosControladorEnum.VIGENCIA_FINAL
                                                            .getValue()));
            vigenciaInicial = String
                            .valueOf(registroPlan.getCampos().get(
                                            PlanindicativosControladorEnum.VIGENCIA_INICIAL
                                                            .getValue()));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Metodo que valida si el subFormulario Indicadores de metas
     * permite o no realizar las operaciones crud
     * 
     * @return
     */
    private boolean esUsuarioConsulta() {
        int nivelUsuario = SessionUtil
                        .getNivelUsuario(Integer.parseInt(modulo));

        if (nivelUsuario == 0 && esAdministrador) {
            return true;
        }
        return false;
    }

    /**
     * Metodo que valida si se puede editar el subformulario plan de
     * accion
     * 
     * @return true
     */
    public boolean permitirEditarSub() {

        int largo = claveArbol.length();
        int metaProducto = 0;
        int metaResultado = 0;
        try {
            ejbPlanDesarrolloCero.cargarNivel(compania,
                            Integer.parseInt(vigencia), "");

            metaProducto = ejbPlanDesarrolloCero.obtenerDigitosMetaProduccion();

            metaResultado = ejbPlanDesarrolloCero.obtenerDigitosMetaResultado();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return largo == metaProducto || largo == metaResultado;

    }

    /**
     * Metodo que se ejecuta al momento de seleccionar un registro en
     * el arbol, este ejecuta un mensaje
     */
    public void validarNivelConfigurado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        param.put(PlanindicativosControladorEnum.DIGITOS.getValue(),
                        claveArbol.length());

        try {
            Registro rsNivel = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            PlanindicativosControladorUrlEnum.URL741
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if ("0".equals(rsNivel.getCampos().get("EXISTE").toString())) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4063"));
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Este metodo se ejecuta al momento de darle clic en le boton si
     * del dialogo visualizado en el formulario
     */
    public void aceptarEliminarPlan() {
        try {
            ejbPlanDesarrolloDos.eliminarPlanIndicativo(compania,
                            Integer.parseInt(vigencia), tipo,
                            Long.valueOf(numero),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4064")
                            .replace("s$numero$s", numero));
            dialogoEliminar = false;
            devolverFormulario();

        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Este metodo se ejecuta al momento de darle clic en le boton no
     * del dialogo visualizado en el formulario
     */
    public void cancelarEliminarPlan() {
        dialogoEliminar = false;
    }

    /**
     * Metodo que redirecciona al formulario 1735
     */
    public void devolverFormulario() {
        Map<String, Object> param = new TreeMap<>();

        param.put("tipo", tipo);
        param.put(GeneralParameterEnum.VIGENCIA.getName().toLowerCase(),
                        vigencia);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.TRANSPLANDESARROLLO_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
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
            ejbPlanDesarrolloCero.generarPredecesor(compania,
                            Integer.parseInt(vigencia),
                            SessionUtil.getUser().getCodigo());

            int nivelGrupo = SessionUtil.getNivelGrupo(modulo);

            if (nivelGrupo != 0 && (esAdministrador || esJefeUnidad)) {
                cargarBotones = true;
            }
            else {
                cargarBotones = false;
            }

            int vigenciaAct = Integer.parseInt(vigencia);
            registro = new Registro();
            if (esUsuarioConsulta()) {
                permiteEditar = false;
            }
            else {
                permiteEditar = true;
            }

            vigenciaUno = vigencia;
            vigenciaDos = Integer.toString(vigenciaAct + 1);
            vigenciaTres = Integer.toString(vigenciaAct + 2);
            vigenciaCuatro = Integer.toString(vigenciaAct + 3);

            bloquearMetaUno = ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania, Integer.parseInt(vigenciaUno),
                            "2");
            bloquearMetaDos = ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania, Integer.parseInt(vigenciaDos),
                            "2");
            bloquearMetaTres = ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania, Integer.parseInt(vigenciaTres),
                            "2");
            bloquearMetaCuatro = ejbPlanDesarrolloDos
                            .verificarPlanAdquisiciones(compania,
                                            Integer.parseInt(vigenciaCuatro),
                                            "2");
            bloquearAvanceUno = ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania, Integer.parseInt(vigenciaUno),
                            "3");
            bloquearAvanceDos = ejbPlanDesarrolloDos.verificarPlanAdquisiciones(
                            compania, Integer.parseInt(vigenciaDos),
                            "3");
            bloquearAvanceTres = ejbPlanDesarrolloDos
                            .verificarPlanAdquisiciones(compania,
                                            Integer.parseInt(vigenciaTres),
                                            "3");
            bloquearAvanceCuatro = ejbPlanDesarrolloDos
                            .verificarPlanAdquisiciones(compania,
                                            Integer.parseInt(vigenciaCuatro),
                                            "3");
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        registro.getLlave().put("KEY_COMPANIA", compania);
        registro.getLlave().put("KEY_VIGENCIA_INICIAL", vigencia);
        registro.getLlave().put("KEY_ID", "0");
        cargarRegistro(registro.getLlave(), ACCION_MODIFICAR);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPoblBeneficiada(Registro reg, int indice) {
        // llamado boton
        logger.info("llamado a boton");
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        // param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        // param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigenciaGubernamental", vigencia);

        param.put("idPlan", reg.getCampos().get("ID_PLAN").toString());

        param.put("digitosAccion", digitosAccion);

        // param.put("nombrePlan", nombrePlan);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_PIPOBLACIONBENEFICIADA
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     * Retorna el objeto raizPlan
     * 
     * @return raizPlan
     */
    public TreeNode getRaizPlan() {
        return raizPlan;
    }

    /**
     * Retorna el objeto nodoSeleccionadoPlan
     * 
     * @return nodoSeleccionadoPlan
     */
    public TreeNode getNodoSeleccionadoPlan() {
        return nodoSeleccionadoPlan;
    }

    /**
     * Retorna el objeto claveArbol
     * 
     * @return claveArbol
     */
    public String getClaveArbol() {
        return claveArbol;
    }

    /**
     * Asigna el objeto claveArbol
     * 
     * @param claveArbol
     * Variable a asignar en claveArbol
     */
    public void setClaveArbol(String claveArbol) {
        this.claveArbol = claveArbol;
    }

    /**
     * Retorna el objeto nombreArbol
     * 
     * @return nombreArbol
     */
    public String getNombreArbol() {
        return nombreArbol;
    }

    /**
     * Asigna el objeto nombreArbol
     * 
     * @param nombreArbol
     * Variable a asignar en nombreArbol
     */
    public void setNombreArbol(String nombreArbol) {
        this.nombreArbol = nombreArbol;
    }

    /**
     * Asigna el objeto nodoSeleccionadoPlan
     * 
     * @param nodoSeleccionadoPlan
     * Variable a asignar en nodoSeleccionadoPlan
     */
    public void setNodoSeleccionadoPlan(TreeNode nodoSeleccionado) {
        this.nodoSeleccionadoPlan = nodoSeleccionado;
    }

    /**
     * Retorna el objeto treeServicePlan
     * 
     * @return treeServicePlan
     */
    public TreeService getTreeServicePlan() {
        return treeServicePlan;
    }

    /**
     * Asigna el objeto treeServicePlan
     * 
     * @param treeServicePlan
     * Variable a asignar en treeServicePlan
     */
    public void setTreeServicePlan(TreeService treeService) {
        this.treeServicePlan = treeService;
    }

    /**
     * Retorna el objeto nombreDependencia
     * 
     * @return nombreDependencia
     */
    public String getNombreDependencia() {
        return nombreDependencia;
    }

    /**
     * Asigna el objeto nombreDependencia
     * 
     * @param nombreDependencia
     * Variable a asignar en nombreDependencia
     */
    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    /**
     * Retorna el objeto permiteEditar
     * 
     * @return permiteEditar
     */
    public boolean isPermiteEditar() {
        return permiteEditar;
    }

    /**
     * Asigna el objeto permiteEditar
     * 
     * @param permiteEditar
     * Variable a asignar en permiteEditar
     */
    public void setPermiteEditar(boolean permiteEditar) {
        this.permiteEditar = permiteEditar;
    }

    /**
     * Retorna el objeto vigenciaUno
     * 
     * @return vigenciaUno
     */
    public String getVigenciaUno() {
        return vigenciaUno;
    }

    /**
     * Asigna el objeto vigenciaUno
     * 
     * @param vigenciaUno
     * Variable a asignar en vigenciaUno
     */
    public void setVigenciaUno(String vigenciaUno) {
        this.vigenciaUno = vigenciaUno;
    }

    /**
     * Retorna el objeto vigenciaDos
     * 
     * @return vigenciaDos
     */
    public String getVigenciaDos() {
        return vigenciaDos;
    }

    /**
     * Asigna el objeto vigenciaDos
     * 
     * @param vigenciaDos
     * Variable a asignar en vigenciaDos
     */
    public void setVigenciaDos(String vigenciaDos) {
        this.vigenciaDos = vigenciaDos;
    }

    /**
     * Retorna el objeto vigenciaTres
     * 
     * @return vigenciaTres
     */
    public String getVigenciaTres() {
        return vigenciaTres;
    }

    /**
     * Asigna el objeto vigenciaTres
     * 
     * @param vigenciaTres
     * Variable a asignar en vigenciaTres
     */
    public void setVigenciaTres(String vigenciaTres) {
        this.vigenciaTres = vigenciaTres;
    }

    /**
     * Retorna el objeto vigenciaCuatro
     * 
     * @return vigenciaCuatro
     */
    public String getVigenciaCuatro() {
        return vigenciaCuatro;
    }

    /**
     * Asigna el objeto vigenciaCuatro
     * 
     * @param vigenciaCuatro
     * Variable a asignar en vigenciaCuatro
     */
    public void setVigenciaCuatro(String vigenciaCuatro) {
        this.vigenciaCuatro = vigenciaCuatro;
    }

    /**
     * Retorna el objeto bloquearMetaUno
     * 
     * @return bloquearMetaUno
     */
    public boolean isBloquearMetaUno() {
        return bloquearMetaUno;
    }

    /**
     * Asigna el objeto bloquearMetaUno
     * 
     * @param bloquearMetaUno
     * Variable a asignar en bloquearMetaUno
     */
    public void setBloquearMetaUno(boolean bloquearMetaUno) {
        this.bloquearMetaUno = bloquearMetaUno;
    }

    /**
     * Retorna el objeto bloquearMetaDos
     * 
     * @return bloquearMetaDos
     */
    public boolean isBloquearMetaDos() {
        return bloquearMetaDos;
    }

    /**
     * Asigna el objeto bloquearMetaDos
     * 
     * @param bloquearMetaDos
     * Variable a asignar en bloquearMetaDos
     */
    public void setBloquearMetaDos(boolean bloquearMetaDos) {
        this.bloquearMetaDos = bloquearMetaDos;
    }

    /**
     * Retorna el objeto bloquearMetaTres
     * 
     * @return bloquearMetaTres
     */
    public boolean isBloquearMetaTres() {
        return bloquearMetaTres;
    }

    /**
     * Asigna el objeto bloquearMetaTres
     * 
     * @param bloquearMetaTres
     * Variable a asignar en bloquearMetaTres
     */
    public void setBloquearMetaTres(boolean bloquearMetaTres) {
        this.bloquearMetaTres = bloquearMetaTres;
    }

    /**
     * Retorna el objeto bloquearMetaCuatro
     * 
     * @return bloquearMetaCuatro
     */
    public boolean isBloquearMetaCuatro() {
        return bloquearMetaCuatro;
    }

    /**
     * Asigna el objeto bloquearMetaCuatro
     * 
     * @param bloquearMetaCuatro
     * Variable a asignar en bloquearMetaCuatro
     */
    public void setBloquearMetaCuatro(boolean bloquearMetaCuatro) {
        this.bloquearMetaCuatro = bloquearMetaCuatro;
    }

    /**
     * Retorna el objeto bloquearAvanceUno
     * 
     * @return bloquearAvanceUno
     */
    public boolean isBloquearAvanceUno() {
        return bloquearAvanceUno;
    }

    /**
     * Asigna el objeto bloquearAvanceUno
     * 
     * @param bloquearAvanceUno
     * Variable a asignar en bloquearAvanceUno
     */
    public void setBloquearAvanceUno(boolean bloquearAvanceUno) {
        this.bloquearAvanceUno = bloquearAvanceUno;
    }

    /**
     * Retorna el objeto bloquearAvanceDos
     * 
     * @return bloquearAvanceDos
     */
    public boolean isBloquearAvanceDos() {
        return bloquearAvanceDos;
    }

    /**
     * Asigna el objeto bloquearAvanceDos
     * 
     * @param bloquearAvanceDos
     * Variable a asignar en bloquearAvanceDos
     */
    public void setBloquearAvanceDos(boolean bloquearAvanceDos) {
        this.bloquearAvanceDos = bloquearAvanceDos;
    }

    /**
     * Retorna el objeto bloquearAvanceTres
     * 
     * @return bloquearAvanceTres
     */
    public boolean isBloquearAvanceTres() {
        return bloquearAvanceTres;
    }

    /**
     * Asigna el objeto bloquearAvanceTres
     * 
     * @param bloquearAvanceTres
     * Variable a asignar en bloquearAvanceTres
     */
    public void setBloquearAvanceTres(boolean bloquearAvanceTres) {
        this.bloquearAvanceTres = bloquearAvanceTres;
    }

    /**
     * Retorna el objeto bloquearAvanceCuatro
     * 
     * @return bloquearAvanceCuatro
     */
    public boolean isBloquearAvanceCuatro() {
        return bloquearAvanceCuatro;
    }

    /**
     * Asigna el objeto bloquearAvanceCuatro
     * 
     * @param bloquearAvanceCuatro
     * Variable a asignar en bloquearAvanceCuatro
     */
    public void setBloquearAvanceCuatro(boolean bloquearAvanceCuatro) {
        this.bloquearAvanceCuatro = bloquearAvanceCuatro;
    }

    /**
     * Retorna el objeto cargarBotones
     * 
     * @return cargarBotones
     */
    public boolean isCargarBotones() {
        return cargarBotones;
    }

    /**
     * Asigna el objeto cargarBotones
     * 
     * @param cargarBotones
     * Variable a asignar en cargarBotones
     */
    public void setCargarBotones(boolean cargarBotones) {
        this.cargarBotones = cargarBotones;
    }

    /**
     * Retorna el objeto dialogoEliminar
     * 
     * @return dialogoEliminar
     */
    public boolean isDialogoEliminar() {
        return dialogoEliminar;
    }

    /**
     * Asigna el objeto dialogoEliminar
     * 
     * @param dialogoEliminar
     * Variable a asignar en dialogoEliminar
     */
    public void setDialogoEliminar(boolean dialogoEliminar) {
        this.dialogoEliminar = dialogoEliminar;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaIndicadores
     * 
     * @return listaIndicadores
     */
    public List<Registro> getListaIndicadores() {
        return listaIndicadores;
    }

    /**
     * Asigna la lista listaIndicadores
     * 
     * @param listaIndicadores
     * Variable a asignar en listaIndicadores
     */
    public void setListaIndicadores(List<Registro> listaIndicadores) {
        this.listaIndicadores = listaIndicadores;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
