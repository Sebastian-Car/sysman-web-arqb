/*-
 * BpordennacionalControlador.java
 *
 * 1.0
 * 
 * 31/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.BpordennacionalControladorEnum;
import com.sysman.plandesarrollo.enums.BpordennacionalControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 31/07/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  BpordennacionalControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
	private String acuerdo;
    private String pilar;
    private String iniciativa;
    private String acuerdoNom;
    private String pilarNom;
    private String iniciativaNom;
    private String vigencia;
    private String codPlan;
    
    private RegistroDataModelImpl listaAcuerdos;
    private RegistroDataModelImpl listaAcuerdosE;
    private RegistroDataModelImpl listaPilares;
    private RegistroDataModelImpl listaPilaresE;
    private RegistroDataModelImpl listaIniciativas;
    private RegistroDataModelImpl listaIniciativasE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    /**
     * Crea una nueva instancia de BpordennacionalControlador
     */
    public BpordennacionalControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.BPORDENNACIONAL_CONTROLADOR.getCodigo();
    		
    		Map<String,Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                vigencia = SysmanFunciones.nvl(parametrosEntrada.get("vigencia"),"").toString();
                codPlan = SysmanFunciones.nvl(parametrosEntrada.get("codPlan"),"").toString();
            }
    		validarPermisos();
		} catch(Exception ex) {
			logger.error(ex.getMessage(),ex);
		    SessionUtil.redireccionarMenuPermisos();
        } 
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
    	enumBase = GenericUrlEnum.BP_ORDENNACIONAL;
    	reasignarOrigen();		    
    	buscarLlave();
    	registro = new Registro();

    	cargarListaAcuerdos(); 
    	cargarListaAcuerdosE();
    	cargarListaPilares(); 
    	cargarListaPilaresE();
    	cargarListaIniciativas(); 
    	cargarListaIniciativasE();
	  
    	abrirFormulario();
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
    	buscarUrls();
    	
    	parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
    	parametrosListado.put(BpordennacionalControladorEnum.COD_PLAN.getValue(),codPlan);
    }
    /**
     * 
     * Carga la lista listaAcuerdos
     *
     */
    public void cargarListaAcuerdos() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordennacionalControladorUrlEnum.URL1961001.getValue());

    	listaAcuerdos = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,BpordennacionalControladorEnum.COD_ACUERDO.getValue());
    }
    /**
     * 
     * Carga la lista listaAcuerdos
     *
     */
    public void  cargarListaAcuerdosE() {
    	listaAcuerdosE = listaAcuerdos;
    }
    /**
     * 
     * Carga la lista listaPilares
     *
     */
    public void cargarListaPilares() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	param.put(BpordennacionalControladorEnum.ACUERDO.getValue(),
    			SysmanFunciones.nvl(acuerdo,"0"));
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordennacionalControladorUrlEnum.URL1962001.getValue());

    	listaPilares = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,BpordennacionalControladorEnum.COD_PILAR.getValue());
    }
    /**
     * 
     * Carga la lista listaPilares
     *
     */
    public void  cargarListaPilaresE() {
    	listaPilaresE = listaPilares;
    }
    /**
     * 
     * Carga la lista listaIniciativas
     *
     */
    public void cargarListaIniciativas() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	param.put(BpordennacionalControladorEnum.PILAR.getValue(),
    			SysmanFunciones.nvl(pilar,"0"));
    	param.put(BpordennacionalControladorEnum.ACUERDO.getValue(),
    			SysmanFunciones.nvl(acuerdo,"0"));
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordennacionalControladorUrlEnum.URL1963001.getValue());

    	listaIniciativas = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,BpordennacionalControladorEnum.INICIATIVA.getValue());
    }
    /**
     * 
     * Carga la lista listaIniciativas
     *
     */
    public void  cargarListaIniciativasE() {
    	listaIniciativasE = listaIniciativas;
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Adicionar
     * en la vista
     *
     *
     */
    public void oprimirAdicionar() {
    	try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
            registro.getCampos().put(BpordennacionalControladorEnum.COD_PLAN.getValue(),codPlan);
            registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
            registro.getCampos().put(BpordennacionalControladorEnum.ACUERDO.getValue(),acuerdo);
            registro.getCampos().put(BpordennacionalControladorEnum.PILAR.getValue(),pilar);
            registro.getCampos().put(BpordennacionalControladorEnum.INICIATIVA.getValue(),iniciativa);
            registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),SessionUtil.getUser().getCodigo());
            registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
            		.getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ORDENNACIONAL.getCreateKey());
            
            requestManager.save(urlCreate.getUrl(),urlCreate.getMetodo(),registro.getCampos());
            reasignarOrigen();
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        } finally {
        	acuerdo = null;
        	acuerdoNom = null;
        	pilar = null;
        	pilarNom = null;
        	iniciativa = null;
        	iniciativaNom = null;
        }
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAcuerdos
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAcuerdos(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        acuerdo = registroAux.getCampos().get(BpordennacionalControladorEnum.COD_ACUERDO.getValue()).toString();
        acuerdoNom = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        pilar = null;
        pilarNom = null;
        cargarListaPilares();
        iniciativa = null;
        iniciativaNom = null;
        cargarListaIniciativas();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAcuerdos
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAcuerdosE(SelectEvent event) {
    	 Registro registroAux = (Registro) event.getObject();
         auxiliar = registroAux.getCampos().get(BpordennacionalControladorEnum.COD_ACUERDO.getValue()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPilares
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPilares(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        pilar = registroAux.getCampos().get(BpordennacionalControladorEnum.COD_PILAR.getValue()).toString();
        pilarNom = registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
        iniciativa = null;
        iniciativaNom = null;
        cargarListaIniciativas();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPilares
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPilaresE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(BpordennacionalControladorEnum.COD_PILAR.getValue()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIniciativas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIniciativas(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        iniciativa = registroAux.getCampos().get(BpordennacionalControladorEnum.INICIATIVA.getValue()).toString();
        iniciativaNom = registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIniciativas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIniciativasE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(BpordennacionalControladorEnum.INICIATIVA.getValue()).toString();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario() {
    	acuerdo = null;
    	acuerdoNom = null;
    	pilar = null;
    	pilarNom = null;
    	iniciativa = null;
    	iniciativaNom = null;
    }
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
    	getListaInicial().load();
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
	@Override
    public boolean insertarDespues() {
		return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override   
    public boolean actualizarDespues() {
    	return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override    
    public boolean eliminarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     * @return TODO VARIABLE
     */
    @Override   
    public boolean eliminarDespues() {
    	return true;
    }
    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {}
    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
    	RequestContext.getCurrentInstance().closeDialog(null);
    }
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {}
    /**
     * Retorna la variable acuerdo
     * 
     * @return  acuerdo
     */
    public String getAcuerdo() {
        return acuerdo;
    }
    /**
     * Asigna la variable  acuerdo
     * 
     * @param  acuerdo
     * Variable a asignar en  acuerdo
     */
    public void setAcuerdo(String acuerdo) {
        this.acuerdo = acuerdo;
    }
    /**
     * Retorna la variable pilar
     * 
     * @return  pilar
     */
    public String getPilar() {
        return pilar;
    }
    /**
     * Asigna la variable  pilar
     * 
     * @param  pilar
     * Variable a asignar en  pilar
     */
    public void setPilar(String pilar) {
        this.pilar = pilar;
    }
    /**
     * Retorna la variable iniciativa
     * 
     * @return  iniciativa
     */
    public String getIniciativa() {
        return iniciativa;
    }
    /**
     * Asigna la variable  iniciativa
     * 
     * @param  iniciativa
     * Variable a asignar en  iniciativa
     */
    public void setIniciativa(String iniciativa) {
        this.iniciativa = iniciativa;
    }
    /**
     * Retorna la variable acuerdoNom
     * 
     * @return  acuerdoNom
     */
    public String getAcuerdoNom() {
        return acuerdoNom;
    }
    /**
     * Asigna la variable  acuerdoNom
     * 
     * @param  acuerdoNom
     * Variable a asignar en  acuerdoNom
     */
    public void setAcuerdoNom(String acuerdoNom) {
        this.acuerdoNom = acuerdoNom;
    }
    /**
     * Retorna la variable pilarNom
     * 
     * @return  pilarNom
     */
    public String getPilarNom() {
        return pilarNom;
    }
    /**
     * Asigna la variable  pilarNom
     * 
     * @param  pilarNom
     * Variable a asignar en  pilarNom
     */
    public void setPilarNom(String pilarNom) {
        this.pilarNom = pilarNom;
    }
    /**
     * Retorna la variable iniciativaNom
     * 
     * @return  iniciativaNom
     */
    public String getIniciativaNom() {
        return iniciativaNom;
    }
    /**
     * Asigna la variable  iniciativaNom
     * 
     * @param  iniciativaNom
     * Variable a asignar en  iniciativaNom
     */
    public void setIniciativaNom(String iniciativaNom) {
        this.iniciativaNom = iniciativaNom;
    }
    /**
     * Retorna la lista listaAcuerdos
     * 
     * @return listaAcuerdos
     */
    public RegistroDataModelImpl getListaAcuerdos() {
        return listaAcuerdos;
    }
    /**
     * Asigna la lista listaAcuerdos
     * 
     * @param listaAcuerdos
     * Variable a asignar en  listaAcuerdos
     */
    public void setListaAcuerdos(RegistroDataModelImpl listaAcuerdos) {
        this.listaAcuerdos = listaAcuerdos;
    }
    /**
     * Retorna la lista listaAcuerdos
     * 
     * @return listaAcuerdos
     */
    public RegistroDataModelImpl getListaAcuerdosE() {
        return listaAcuerdosE;
    }
    /**
     * Asigna la lista listaAcuerdos
     * 
     * @param listaAcuerdos
     * Variable a asignar en  listaAcuerdos
     */
    public void setListaAcuerdosE(RegistroDataModelImpl listaAcuerdosE) {
        this.listaAcuerdosE = listaAcuerdosE;
    }
    /**
     * Retorna la lista listaPilares
     * 
     * @return listaPilares
     */
    public RegistroDataModelImpl getListaPilares() {
        return listaPilares;
    }
    /**
     * Asigna la lista listaPilares
     * 
     * @param listaPilares
     * Variable a asignar en  listaPilares
     */
    public void setListaPilares(RegistroDataModelImpl listaPilares) {
        this.listaPilares = listaPilares;
    }
    /**
     * Retorna la lista listaPilares
     * 
     * @return listaPilares
     */
    public RegistroDataModelImpl getListaPilaresE() {
        return listaPilaresE;
    }
    /**
     * Asigna la lista listaPilares
     * 
     * @param listaPilares
     * Variable a asignar en  listaPilares
     */
    public void setListaPilaresE(RegistroDataModelImpl listaPilaresE) {
        this.listaPilaresE = listaPilaresE;
    }
    /**
     * Retorna la lista listaIniciativas
     * 
     * @return listaIniciativas
     */
    public RegistroDataModelImpl getListaIniciativas() {
        return listaIniciativas;
    }
    /**
     * Asigna la lista listaIniciativas
     * 
     * @param listaIniciativas
     * Variable a asignar en  listaIniciativas
     */
    public void setListaIniciativas(RegistroDataModelImpl listaIniciativas) {
        this.listaIniciativas = listaIniciativas;
    }
    /**
     * Retorna la lista listaIniciativas
     * 
     * @return listaIniciativas
     */
    public RegistroDataModelImpl getListaIniciativasE() {
        return listaIniciativasE;
    }
    /**
     * Asigna la lista listaIniciativas
     * 
     * @param listaIniciativas
     * Variable a asignar en  listaIniciativas
     */
    public void setListaIniciativasE(RegistroDataModelImpl listaIniciativasE) {
        this.listaIniciativasE = listaIniciativasE;
    }
    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }
    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar= auxiliar;
    }
}
