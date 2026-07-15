/*-
 * FrmPlanindicativoIndiControlador.java
 *
 * 1.0
 * 
 * 20/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoRemote;
import com.sysman.plandesarrollo.enums.FrmPlanIndicativoIndiControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.TreeService;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.persistencia.ConectorPool;

import net.sf.jasperreports.engine.JRException;

import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.primefaces.model.TreeNode;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;


/**
 * Formulario que administra el árbol de indicadores del plan de
 * acción
 *
 * @version 1.0, 20/04/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmPlanIndicativoIndiControlador extends BeanBaseDatosAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el codigo del usuario que ingresa a la
     * aplicacion
     */
    private final String usuario;

    /**
     * Constante que almacena el numero del modulo
     */
    private final String modulo;

    /**
     * Atributo utilizado para administrar la visibilidad de los
     * campos de porcentaje dependiendo la opcion seleccionada
     */
    private boolean verPorcentaje;
    /**
     * Atributo que almacena el valor del parametro recibido
     * administrador
     */
    private boolean esAdministrador;

    /**
     * Atributo que almacena el valor de la opcion seleccionada en la
     * vista
     */
    private String opcion;

    /**
     * Atributo que almacena la vigencia GetVIGENCIA_GUB()
     */
    private String vigenciaGubernamental;

    /**
     * Atributo que almacena el valor del parametro VIGENCIA
     * GUBERNAMENTAL ACTUAL
     */
    private String vigencia;

    /**
     * Atributo que almacena el valor de GetTipoT()
     */
    private String tipoT;

    /**
     * Atributo que almacena el valor de GetNumeroT()
     * 
     */
    private String numeroT;

    /**
     * Atributo que almacena el valor de GetID_PLAN()
     * 
     */
    private String idPLan;

    /**
     * Atributo que almacena el valor del campo anio
     */
    private String anio;

    /**
     * Atributo que almacena el valor del campo meses de gracia
     */
    private String mesesGracia;

    /**
     * Atributo que almacena el nodo superior del arbol
     */
    private TreeNode raizIndicadores;

    /**
     * Atributo que guarda el nodo seleccionado del arbol
     */
    private TreeNode nodoSeleccionadoIndicadores;

    /**
     * Atributo que almacena el nombre del nodo
     */
    private String tituloNodo;

    /**
     * Atributo que almacena el porcentaje inferior
     */
    private int porcentajeInferior;
    /**
     * Atributo que almacena el nombre del proyecto seleccionado
     */
    private String tituloSub;

    /**
     * Atributo que almacena el porcentaje superior
     */
    private int porcentajeSuperior;

    /**
     * Atributo que administra el nombre de la etiqueta de la opcion
     * cuatro del cuadro de opciones
     */
    private String tituloVigencia;

    /**
     * Atributo utilizado para redireccionar al formulario anterior
     */
    private boolean esJefeUnidad;

    /**
     * Atributo utilizado para redireccionar al formulario anterior
     */
    private String dependencia;

    /**
     * Objeto que contiene las funcionalidades necesarias para
     * trabajar con el componente de arbol Plan
     */
    @ManagedProperty("#{treeService}")
    private TreeService treeServiceIndicadores;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla periodos
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que carga el pi de opbservacion
     */
    private List<Registro> listaPiplanobservacion;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPlanDesarrolloCeroRemote ejbPlanDesarrolloCero;

    @EJB
    private EjbPlanDesarrolloUnoRemote ejbPlanDesarrolloUno;
    private String nivel;
    
  	private RegistroDataModelImpl listaNivel;
  	
  	private StreamedContent archivoDescarga;
	private String digitos;

  

	// </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmPlanindicativoIndiControlador
     */
    public FrmPlanIndicativoIndiControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        verPorcentaje = false;
        opcion = "0";
        tituloNodo = "";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_PLAN_INDICATIVO_INDI_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                vigenciaGubernamental = parametrosEntrada
                                .get("vigenciaGubernamental").toString();
                esAdministrador = (boolean) parametrosEntrada
                                .get("esAdministrador");
                numeroT = parametrosEntrada.get("numeroT").toString();
                tipoT = parametrosEntrada.get("tipoT").toString();

                dependencia = parametrosEntrada.get("dependencia").toString();

                esJefeUnidad = (boolean) parametrosEntrada.get("esJefeUnidad");

            }
            // <INI_ADICIONAL>
            registro = new Registro(new HashMap<String, Object>());
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
        listaPiplanobservacion = null;
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
        tabla = "COMPANIA";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        cargarListaAno();
        cargarListaNivel();
        abrirFormulario();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";

    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaPiplanobservacion
     *
     */
    public void cargarListaPiplanobservacion() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(),
                            vigenciaGubernamental);
            param.put(GeneralParameterEnum.ID_PLAN.getName(), idPLan);

            listaPiplanobservacion = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmPlanIndicativoIndiControladorUrlEnum.URL11702
                                                                                            .getValue())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            "PI_PLAN_INDICATIVO_OBS"));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmPlanIndicativoIndiControladorUrlEnum.URL16823
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }
    
    public void cargarListaNivel() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmPlanIndicativoIndiControladorUrlEnum.URL554023
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANIO.getName(),vigenciaGubernamental );

        listaNivel = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "DIGITOS");
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    public void cambiarOpciones() {

        if ("2".equals(opcion) || "3".equals(opcion)) {
            verPorcentaje = true;
            porcentajeInferior = 70;

            porcentajeSuperior = 90;

            mesesGracia = "0";

            if ("3".equals(opcion)) {
                mesesGracia = "5";
            }
        }
        else {
            verPorcentaje = false;
        }

        cargarArbol();
    }

    public void cambiarAno() {
        cargarArbol();
    }

    public void cambiartxtMesGracia() {
        cargarArbol();
    }

    public void cambiarporcentajeInferior() {
        cargarArbol();
    }

    public void cambiarporcentajeSuperior() {
        cargarArbol();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton MayorizarDatos en la vista
     *
     */
    public void oprimirMayorizarDatos() {
        try {
            ejbPlanDesarrolloCero.mayorizarCompPagMetas(compania,
                            Integer.parseInt(vigencia),
                            ejbPlanDesarrolloCero
                                            .obtenerDigitosMetaProduccion(),
                            tipoT, new BigInteger(numeroT), usuario);

            ejbPlanDesarrolloCero.mayorizarCompPagPlan(compania,
                            Integer.parseInt(vigencia),
                            ejbPlanDesarrolloCero
                                            .obtenerDigitosMetaProduccion(),
                            tipoT, new BigInteger(numeroT), usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

            cargarArbol();
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    private boolean validar(){
        boolean estado=true;
       
        if ((nivel == null) || "".equals(nivel)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4487"));
            estado=false;
        }
        return estado;
    }
    
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    
    private void generaReporte(FORMATOS formato) {
    	if(validar()){
	    	try {
	                HashMap<String, Object> reemplazar = new HashMap<>();
	                String reporte = "800719InformeParcialPorNiveles";
	                 reemplazar.put("NDIGITOS", digitos);
	                 reemplazar.put("COMPANIA", compania);
	                 reemplazar.put("VIGENCIA", vigenciaGubernamental);
	                 reemplazar.put("TIPO", tipoT);
	                 reemplazar.put("NUMERO", numeroT);
	
	                // MANEJO DE PARAMETROS DEL REPORTE
	                Map<String, Object> parametros = new HashMap<>();
	                Reporteador.resuelveConsulta(reporte,
	                                Integer.parseInt(SessionUtil.getModulo()),
	                                reemplazar,
	                                parametros);
	              System.out.println();
	               parametros.put("PR_NOMBRECOMPANIA",
	                                SessionUtil.getCompaniaIngreso().getNombre());
	               parametros.put("PR_ANO_VIGENCIA", vigenciaGubernamental);
	             
	           	
					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
				} catch (JRException | IOException | SysmanException ex) {
					 logger.error(ex.getMessage(), ex);
			            JsfUtil.agregarMensajeError(ex.getMessage());
				}
    	}
    }
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Piplanobservacion
     * 
     */
    public void agregarRegistroSubPiplanobservacion() {
        try {

            if (idPLan != null) {

                registroSub.getCampos().put(
                                GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                registroSub.getCampos().put(
                                GeneralParameterEnum.CONSECUTIVO.getName(),
                                generarConsecutivo());

                registroSub.getCampos().put(
                                GeneralParameterEnum.ID_PLAN.getName(), idPLan);

                registroSub.getCampos().put("VIGENCIA_INICIAL",
                                vigenciaGubernamental);

                registroSub.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                usuario);

                registroSub.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.PI_PLAN_INDICATIVO_OBS
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSub.getCampos());

                cargarListaPiplanobservacion();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
            else {

                JsfUtil.agregarMensajeAlerta(
                                "Por favor seleccione un nodo del ábol");
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    private Object generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "PI_PLAN_INDICATIVO_OBS", "COMPANIA = " + compania
                                + " AND VIGENCIA_INICIAL = "
                                + vigenciaGubernamental + " AND ID_PLAN ="
                                + idPLan,
                            "CONSECUTIVO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    /**
     * Metodo de edicion del formulario Piplanobservacion
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubPiplanobservacion(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_OBS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaPiplanobservacion();
        }
    }

    /**
     * Metodo de eliminacion del formulario Piplanobservacion
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubPiplanobservacion(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_OBS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaPiplanobservacion();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("vigencia", vigenciaGubernamental);
        parametros.put("tipo", tipoT);
        parametros.put("administrador", esAdministrador);
        parametros.put("jefeUnidad", esJefeUnidad);
        parametros.put((GeneralParameterEnum.DEPENDENCIA.getName()
                        .toLowerCase()), dependencia);

        parametros.put("numero", numeroT);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Piplanobservacion
     *
     */
    public void cancelarEdicionPiplanobservacion() {
        cargarListaPiplanobservacion();
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
    public void expanderNodoIndicadores(NodeExpandEvent event) {
        treeServiceIndicadores.traerDescendientes(event.getTreeNode());

    }

    /**
     * Metodo ejecutado al colapsar un nodo en el arbol Plan, lo cual
     * incluye ocultar los hijos del nodo correpondiente
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void colapsarNodoIndicadores(NodeCollapseEvent event) {
        // METODO_NO_IMPLEMENTADO

    }

    /**
     * Metodo ejecutado al seleccionar un nodo en el arbol Plan
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void selecionarNodoIndicadores(NodeSelectEvent event) {
        int aux = treeServiceIndicadores.getNombre(event.getTreeNode())
                        .split("-").length
            + 1;
        tituloNodo = treeServiceIndicadores.getNombre(event.getTreeNode())
                        .substring(aux,
                                        treeServiceIndicadores.getNombre(
                                                        event.getTreeNode())
                                                        .length());

        idPLan = treeServiceIndicadores.getIdentificador(event.getTreeNode());

        cargarListaPiplanobservacion();
    }

    
    public void seleccionarFilaNivel(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nivel =  SysmanFunciones.toString(registroAux.getCampos().get("DESCRIPCION"));
        digitos =  SysmanFunciones.toString(registroAux.getCampos().get("DIGITOS"));
        
    }
    

    
    /**
     * Metodo ejecutado al deseleccionar un nodo en el arbol Plan
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     * 
     */
    public void deseleccionarNodoIndicadores(NodeUnselectEvent event) {
        // Metodo_no_implementado
    }

    private void cargarArbol() {
        String campo;
        String origen;
        String por = "";
        String limites = "";
        String base = "";
        String limiteInf;
        String limiteSup;
        String valor;
        String total;
        Date fechaCalculo;
        Map<String, Object> reemplazos = new TreeMap<>();
        try {

            ejbPlanDesarrolloCero.cargarNivel(compania,
                            Integer.parseInt(vigencia), "");

            fechaCalculo = new Date();

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA OBLIGACIONES EN PLAN DE ACCION",
                                            modulo, new Date(), false), "NO")
                            .toString())) {

                campo = "VALOR_OBLIGACIONES_FIN";
            }
            else {
                campo = "VALOR_PAGADO_FIN";
            }

            origen = "PI_PLAN_INDICATIVO";

            if ("0".equals(opcion)) {
                por = " ROUND(SUM(METAS.VALOR_COMPROMETIDO_FIN)/SUM(CASE WHEN METAS.VALOR_PRESUPUESTO_FIN=0 THEN 1 ELSE METAS.VALOR_PRESUPUESTO_FIN END)* 100,2)";
            }
            else if ("1".equals(opcion)) {
                por = " ROUND(SUM(METAS." + campo
                    + ")/SUM(CASE WHEN METAS.VALOR_PRESUPUESTO_FIN=0 THEN 1 ELSE METAS.VALOR_PRESUPUESTO_FIN END)* 100,2)";
            }
            else if ("2".equals(opcion) || "3".equals(opcion)) {
                origen = "PI_PLAN_INDICATIVO_METAS";

                por = " CASE WHEN METAS.VALOR_PRESUPUESTO_FIN = 0 THEN 0 ELSE ROUND((CASE WHEN "
                    + opcion
                    + "= 2 THEN METAS.VALOR_COMPROMETIDO_FIN ELSE METAS."
                    + campo
                    + " END )/METAS.VALOR_PRESUPUESTO_FIN * 100,2) END "
                    + ((SysmanFunciones.esBdSqlServer())
                        ? " , '% - (' , METAS.POR_AVANCE_INDICADOR/100 , '%) - '"
                        : "  ||'% - ('||METAS.POR_AVANCE_INDICADOR/100||'%) - '");

                if (Integer.parseInt(anio) < SysmanFunciones.ano(new Date())) {

                    fechaCalculo = SysmanFunciones.convertirAFecha(
                                    "31/12/"
                                        + SysmanFunciones.ano(fechaCalculo),
                                    "DD/MM/YYYY");

                }

                if (SysmanFunciones.mes(fechaCalculo) <= Integer.parseInt(
                                SysmanFunciones.nvlStr(mesesGracia, "0"))) {

                    base = "negro";
                }
                else {
                    base = "rojo";
                }

                limiteInf = "/METAS.VALOR_PRESUPUESTO_FIN) * 100,2)<=NVL("
                    + porcentajeInferior + ", 0)";

                limiteSup = "/METAS.VALOR_PRESUPUESTO_FIN ) * 100,2)<=NVL("
                    + porcentajeSuperior + ", 0)";

                limites = " ,CASE WHEN METAS.VALOR_PRESUPUESTO_FIN <= 0 THEN'"
                    + base + "' ELSE CASE WHEN " + opcion + " = 2 THEN"
                    + " CASE WHEN ROUND((METAS.VALOR_COMPROMETIDO_FIN "
                    + limiteInf + " THEN '" + base + "' ELSE"
                    + " CASE WHEN ROUND((METAS.VALOR_COMPROMETIDO_FIN "
                    + limiteSup + " THEN 'amarillo' ELSE"
                    + " CASE WHEN ROUND((METAS.VALOR_COMPROMETIDO_FIN /METAS.VALOR_PRESUPUESTO_FIN ) * 100,2)<=NVL(100, 0)"
                    + " THEN 'verde' ELSE 'negro' END END END ELSE"
                    + " CASE WHEN ROUND((METAS.VALOR_PAGADO_FIN "
                    + limiteInf + " THEN '" + base + "' ELSE"
                    + " CASE WHEN ROUND((METAS.VALOR_PAGADO_FIN "
                    + limiteSup
                    + " THEN 'amarillo' ELSE"
                    + " CASE WHEN ROUND((METAS.VALOR_PAGADO_FIN /METAS.VALOR_PRESUPUESTO_FIN ) * 100,2)<=NVL(100, 0)"
                    + " THEN 'verde' ELSE 'negro' END END END END END TIPOS";
            }

            reemplazos.put("limites", limites);

            reemplazos.put("por", por);

            reemplazos.put("compania", compania);

            reemplazos.put("vigencia", vigencia);

            reemplazos.put("tipo", tipoT);

            reemplazos.put("numero", numeroT);

            reemplazos.put("ano", anio);

            if (!esAdministrador) {
                reemplazos.put("dependencia", "AND PLAN.DEPENDENCIA_INI   = '"
                    + SessionUtil.getUser().getDependencia().getCodigo()
                    + "' ");
            }
            else {
                reemplazos.put("dependencia", "");
            }

            if ("PI_PLAN_INDICATIVO".equals(origen)) {

                String consulta = Reporteador.resuelveConsulta(
                                "800140ArbolPlanIndi", Integer.parseInt(modulo),
                                reemplazos);

                treeServiceIndicadores = new TreeService();
                raizIndicadores = treeServiceIndicadores.crearArbol(consulta);
            }
            else {

                String consulta = Reporteador.resuelveConsulta(
                                "800141ArbolPlanIndiMetas",
                                Integer.parseInt(modulo),
                                reemplazos);

                treeServiceIndicadores = new TreeService();
                raizIndicadores = treeServiceIndicadores.crearArbol(consulta);

            }
        }
        catch (SystemException | com.sysman.util.SysmanException
                        | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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

        try {
            tituloSub = "";
            vigencia = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "VIGENCIA GUBERNAMENTAL ACTUAL",
                                            modulo, new Date(), false), "0")
                            .toString();

            ejbPlanDesarrolloCero.generarPredecesor(compania,
                            Integer.parseInt(vigencia), usuario);

            ejbPlanDesarrolloUno.calcularAvanceIndicadores(compania,
                            Integer.parseInt(vigencia), tipoT,
                            new BigInteger(numeroT), usuario);

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA OBLIGACIONES EN PLAN DE ACCION",
                                            modulo, new Date(), false), "NO")
                            .toString())) {

                tituloVigencia = "% Avance Vigencia (Obligaciones/Fisico)";
            }
            else {
                tituloVigencia = "% Avance Vigencia (Pagado/Fisico)";
            }

            cargarArbol();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaPiplanobservacion
     * 
     * @return listaPiplanobservacion
     */
    public List<Registro> getListaPiplanobservacion() {
        return listaPiplanobservacion;
    }

    /**
     * Asigna la lista listaPiplanobservacion
     * 
     * @param listaPiplanobservacion
     * Variable a asignar en listaPiplanobservacion
     */
    public void setListaPiplanobservacion(
        List<Registro> listaPiplanobservacion) {
        this.listaPiplanobservacion = listaPiplanobservacion;
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

    public TreeNode getRaizIndicadores() {
        return raizIndicadores;
    }

    public void setRaizIndicadores(TreeNode raizIndicadores) {
        this.raizIndicadores = raizIndicadores;
    }

    public TreeNode getNodoSeleccionadoIndicadores() {
        return nodoSeleccionadoIndicadores;
    }

    public void setNodoSeleccionadoIndicadores(
        TreeNode nodoSeleccionadoIndicadores) {
        this.nodoSeleccionadoIndicadores = nodoSeleccionadoIndicadores;
    }

    public TreeService getTreeServiceIndicadores() {
        return treeServiceIndicadores;
    }

    public void setTreeServiceIndicadores(TreeService treeServiceIndicadores) {
        this.treeServiceIndicadores = treeServiceIndicadores;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public boolean isVerPorcentaje() {
        return verPorcentaje;
    }

    public void setVerPorcentaje(boolean verPorcentaje) {
        this.verPorcentaje = verPorcentaje;
    }

    public String getTituloNodo() {
        return tituloNodo;
    }

    public void setTituloNodo(String tituloNodo) {
        this.tituloNodo = tituloNodo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMesesGracia() {
        return mesesGracia;
    }

    public void setMesesGracia(String mesesGracia) {
        this.mesesGracia = mesesGracia;
    }

    public int getPorcentajeInferior() {
        return porcentajeInferior;
    }

    public void setPorcentajeInferior(int porcentajeInferior) {
        this.porcentajeInferior = porcentajeInferior;
    }

    public int getPorcentajeSuperior() {
        return porcentajeSuperior;
    }

    public void setPorcentajeSuperior(int porcentajeSuperior) {
        this.porcentajeSuperior = porcentajeSuperior;
    }

    public String getTituloVigencia() {
        return tituloVigencia;
    }

    public void setTituloVigencia(String tituloVigencia) {
        this.tituloVigencia = tituloVigencia;
    }
    
    public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	
    public RegistroDataModelImpl getListaNivel() {
		return listaNivel;
	}

	public void setListaNivel(RegistroDataModelImpl listaNivel) {
		this.listaNivel = listaNivel;
	}
	
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	  public String getDigitos() {
			return digitos;
		}

		public void setDigitos(String digitos) {
			this.digitos = digitos;
		}
    /**
     * Retorna el objeto tituloSub
     * 
     * @return tituloSub
     */
    public String getTituloSub() {
        return tituloSub;
    }

    /**
     * Asigna el objeto tituloSub
     * 
     * @param tituloSub
     * Variable a asignar en tituloSub
     */
    public void setTituloSub(String tituloSub) {
        this.tituloSub = tituloSub;
    }

    // </SET_GET_ADICIONALES>
}
