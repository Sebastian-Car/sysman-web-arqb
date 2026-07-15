/*-
 * PertinenciaNovedadControlador.java
 *
 * 1.0
 * 
 * 21/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.PertinenciaNovedadControladorEnum;
import com.sysman.bancoproyectos.enums.PertinenciaNovedadControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 21/12/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PertinenciaNovedadControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    private String tipoNovedad;
    private String claseNovedad;
    private String novedad;
    private String dependencia;
    private String impactoCual;
    private String metaResul;
    private String metaProd;
    private String politica;
    private String eje;

    private Map<String, Object> rowIdPertinencia;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaImpactoCual;

    private RegistroDataModelImpl listaProductoCual;

    private RegistroDataModelImpl listaResultadoCual;
    
    private RegistroDataModelImpl listaPoliticaCual;
    
    private RegistroDataModelImpl listaEjeCual;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PertinenciaNovedadControlador
     */
    public PertinenciaNovedadControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            rowIdPertinencia = (Map<String, Object>) parametrosEntrada
                            .get("rowIdPertinencia");

            tipoNovedad = (String) parametrosEntrada.get(
                            PertinenciaNovedadControladorEnum.TIPOT.getValue());
            claseNovedad = (String) parametrosEntrada
                            .get(PertinenciaNovedadControladorEnum.CLASET
                                            .getValue());
            novedad = (String) parametrosEntrada
                            .get(GeneralParameterEnum.CODIGO.getName());
            dependencia = (String) parametrosEntrada
                            .get(GeneralParameterEnum.DEPENDENCIA.getName());

            numFormulario = GeneralCodigoFormaEnum.PERTINENCIA_NOVEDAD_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            // <INI_ADICIONAL>
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

    /**
     * Retorna la variable varVolver
     * 
     * @return var
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     * 
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaImpactoCual();
        cargarListaProductoCual();
        cargarListaResultadoCual();
        cargarListaPoliticaCual(); 
        //cargarListaEjeCual();
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
        enumBase = GenericUrlEnum.PERTINENCIA_NOVEDAD;
        buscarLlave();

        asignarOrigenDatos();
        abrirFormulario();
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
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     * 
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaImpactoCual
     *
     */
    public void cargarListaImpactoCual() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PertinenciaNovedadControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PertinenciaNovedadControladorEnum.TIPON.getValue(),
                        tipoNovedad);
        param.put(PertinenciaNovedadControladorEnum.CLASEN.getValue(),
                        claseNovedad);
        param.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);

        listaImpactoCual = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PertinenciaNovedadControladorEnum.NOMBRE_INDICADOR
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaProductoCual
     *
     */
    public void cargarListaProductoCual() {

        try {
            metaProd = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS META-PRODUCTO",
                            SessionUtil.getModulo(), new Date(), true);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PertinenciaNovedadControladorUrlEnum.URL0002
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PertinenciaNovedadControladorEnum.TIPON.getValue(),
                            tipoNovedad);
            param.put(PertinenciaNovedadControladorEnum.CLASEN.getValue(),
                            claseNovedad);
            param.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);

            param.put(PertinenciaNovedadControladorEnum.DIGITOS.getValue(),
                            metaProd);

            listaProductoCual = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, "ID");
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaResultadoCual
     *
     */
    public void cargarListaResultadoCual() {

        try {
            metaResul = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS META-RESULTADO",
                            SessionUtil.getModulo(), new Date(), true);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PertinenciaNovedadControladorUrlEnum.URL0002
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PertinenciaNovedadControladorEnum.TIPON.getValue(),
                            tipoNovedad);
            param.put(PertinenciaNovedadControladorEnum.CLASEN.getValue(),
                            claseNovedad);
            param.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);

            param.put(PertinenciaNovedadControladorEnum.DIGITOS.getValue(),
                            metaResul);

            listaResultadoCual = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, "ID");

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Carga la lista listaPoliticaCual
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaPoliticaCual(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						PertinenciaNovedadControladorUrlEnum.URL1986001
						.getValue());
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaPoliticaCual = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaEjeCual
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaEjeCual(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						PertinenciaNovedadControladorUrlEnum.URL1987001
						.getValue());
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("POLITICA", politica);

        listaEjeCual = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO_EJE");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaImpactoCual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaImpactoCual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("IMPACTO_CUAL",
                        registroAux.getCampos().get(
                                        PertinenciaNovedadControladorEnum.NOMBRE_INDICADOR
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProductoCual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProductoCual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("PRODUCTO_CUAL",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResultadoCual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResultadoCual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESULTADO_CUAL",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPoliticaCual
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPoliticaCual(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	politica = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"),"").toString();
    	String nombrePolitica = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"),"").toString();
    	registro.getCampos().put("APPT_CUAL_POLITICA", politica);
    	registro.getCampos().put("APPT_CUAL", nombrePolitica);
    	registro.getCampos().put("APBOT_CUAL_EJE", "");
    	registro.getCampos().put("APBOT_CUAL", "");
    	cargarListaEjeCual();
    	
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEjeCual
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEjeCual(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	eje = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO_EJE"),"").toString();
    	String nombreEje = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"),"").toString();
    	registro.getCampos().put("APBOT_CUAL_EJE", eje);
    	registro.getCampos().put("APBOT_CUAL", nombreEje);
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
        try {
            iniciarListas();
            Map<String, Object> key = new HashMap<>();
            key.put("KEY_COMPANIA", compania);
            key.put("KEY_TIPONOVEDAD", tipoNovedad);
            key.put("KEY_CLASENOVEDAD", claseNovedad);
            key.put("KEY_NOVEDAD", novedad);
            key.put("KEY_DEPENDENCIA", dependencia);

            Registro regTemp = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PertinenciaNovedadControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(), key));

            if (regTemp == null) {
                registro = new Registro();
            }
            else {
                rid = key;
                cargarRegistro(rid, ACCION_MODIFICAR);

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("TIPONOVEDAD", tipoNovedad);
        registro.getCampos().put("CLASENOVEDAD", claseNovedad);
        registro.getCampos().put("NOVEDAD", novedad);
        registro.getCampos().put("DEPENDENCIA", dependencia);
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
    	registro.getCampos().remove("NOMBREPOLITICA");
    	registro.getCampos().remove("NOMBREEJE");
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
     * 
     */
    @Override
    public boolean eliminarAntes() {

    	try {
    		Map<String, Object> params = new TreeMap<>();

    		params.put("KEY_COMPANIA", compania);
    		params.put("KEY_TIPONOVEDAD", tipoNovedad);
    		params.put("KEY_CLASENOVEDAD", claseNovedad);
    		params.put("KEY_NOVEDAD", novedad);
    		params.put("KEY_DEPENDENCIA", dependencia);


    		UrlBean urlDelete = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						PertinenciaNovedadControladorUrlEnum.URL176200D
    						.getValue());
    		requestManager.delete(urlDelete.getUrl(), params);
    	}
    	catch (SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    	RequestContext.getCurrentInstance().closeDialog(null);
    	return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the tipoNovedad
     */
    public String getTipoNovedad() {
        return tipoNovedad;
    }

    /**
     * @return the listaImpactoCual
     */
    public RegistroDataModelImpl getListaImpactoCual() {
        return listaImpactoCual;
    }

    /**
     * @param listaImpactoCual
     * the listaImpactoCual to set
     */
    public void setListaImpactoCual(RegistroDataModelImpl listaImpactoCual) {
        this.listaImpactoCual = listaImpactoCual;
    }

    /**
     * @return the listaProductoCual
     */
    public RegistroDataModelImpl getListaProductoCual() {
        return listaProductoCual;
    }

    /**
     * @param listaProductoCual
     * the listaProductoCual to set
     */
    public void setListaProductoCual(RegistroDataModelImpl listaProductoCual) {
        this.listaProductoCual = listaProductoCual;
    }

    /**
     * @return the listaResultadoCual
     */
    public RegistroDataModelImpl getListaResultadoCual() {
        return listaResultadoCual;
    }

    /**
     * @param listaResultadoCual
     * the listaResultadoCual to set
     */
    public void setListaResultadoCual(
        RegistroDataModelImpl listaResultadoCual) {
        this.listaResultadoCual = listaResultadoCual;
    }

    /**
     * @param tipoNovedad
     * the tipoNovedad to set
     */
    public void setTipoNovedad(String tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    /**
     * @return the claseNovedad
     */
    public String getClaseNovedad() {
        return claseNovedad;
    }

    /**
     * @param claseNovedad
     * the claseNovedad to set
     */
    public void setClaseNovedad(String claseNovedad) {
        this.claseNovedad = claseNovedad;
    }

    /**
     * @return the novedad
     */
    public String getNovedad() {
        return novedad;
    }

    /**
     * @param novedad
     * the novedad to set
     */
    public void setNovedad(String novedad) {
        this.novedad = novedad;
    }

    /**
     * @return the dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * @param dependencia
     * the dependencia to set
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * @return the impactoCual
     */
    public String getImpactoCual() {
        return impactoCual;
    }

    /**
     * @param impactoCual
     * the impactoCual to set
     */
    public void setImpactoCual(String impactoCual) {
        this.impactoCual = impactoCual;
    }

    /**
     * @return the metaResul
     */
    public String getMetaResul() {
        return metaResul;
    }

    /**
     * @param metaResul
     * the metaResul to set
     */
    public void setMetaResul(String metaResul) {
        this.metaResul = metaResul;
    }

    /**
     * @return the metaProd
     */
    public String getMetaProd() {
        return metaProd;
    }

    /**
     * @param metaProd
     * the metaProd to set
     */
    public void setMetaProd(String metaProd) {
        this.metaProd = metaProd;
    }
    
    public RegistroDataModelImpl getListaPoliticaCual() {
        return listaPoliticaCual;
    }
    
    public void setListaPoliticaCual(RegistroDataModelImpl listaPoliticaCual) {
        this.listaPoliticaCual = listaPoliticaCual;
    }
    
    public RegistroDataModelImpl getListaEjeCual() {
        return listaEjeCual;
    }
    
    public void setListaEjeCual(RegistroDataModelImpl listaEjeCual) {
        this.listaEjeCual = listaEjeCual;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
