/*-
 * BpordendepartamentalControlador.java
 *
 * 1.0
 * 
 * 01/08/2025
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
import com.sysman.plandesarrollo.enums.BpordendepartamentalControladorEnum;
import com.sysman.plandesarrollo.enums.BpordendepartamentalControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/08/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  BpordendepartamentalControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
	private String ejeEstructural;
	private String medidaPigcct;
	private String ejeEstructuralNom;
	private String medidaPigcctNom;
	private String vigencia;
    private String codPlan;
    
    private RegistroDataModelImpl listaEjes;
    private RegistroDataModelImpl listaEjesE;
    private RegistroDataModelImpl listaMedidas;
    private RegistroDataModelImpl listaMedidasE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    /**
     * Crea una nueva instancia de BpordendepartamentalControlador
     */
    public BpordendepartamentalControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.BPORDENDEPARTAMENTAL_CONTROLADOR.getCodigo();
    		
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
    	enumBase = GenericUrlEnum.BP_ORDENDEPARTAMENTAL;
    	reasignarOrigen();		    
    	buscarLlave();
		registro = new Registro();

		cargarListaEjes(); 
		cargarListaEjesE();
		cargarListaMedidas(); 
		cargarListaMedidasE();
				
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
    	parametrosListado.put(BpordendepartamentalControladorEnum.COD_PLAN.getValue(),codPlan);
    }
    /**
     * 
     * Carga la lista listaEjes
     *
     */
    public void cargarListaEjes() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordendepartamentalControladorUrlEnum.URL1964001.getValue());

    	listaEjes = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,BpordendepartamentalControladorEnum.CODIGO_EJE.getValue());
    }
    /**
     * 
     * Carga la lista listaEjes
     *
     */
    public void  cargarListaEjesE() {
    	listaEjesE = listaEjes;
    }
    /**
     * 
     * Carga la lista listaMedidas
     *
     */
    public void cargarListaMedidas() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	param.put(BpordendepartamentalControladorEnum.EJE.getValue(),
    			SysmanFunciones.nvl(ejeEstructural,"0"));
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordendepartamentalControladorUrlEnum.URL1965001.getValue());

    	listaMedidas = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,BpordendepartamentalControladorEnum.CODIGO_MEDIDA.getValue());
    }
    /**
     * 
     * Carga la lista listaMedidas
     *
     */
    public void  cargarListaMedidasE() {
    	listaMedidasE = listaMedidas;
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
            registro.getCampos().put(BpordendepartamentalControladorEnum.COD_PLAN.getValue(),codPlan);
            registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
            registro.getCampos().put(BpordendepartamentalControladorEnum.CODIGO_EJE.getValue(),ejeEstructural);
            registro.getCampos().put(BpordendepartamentalControladorEnum.CODIGO_MEDIDA.getValue(),medidaPigcct);
            registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),SessionUtil.getUser().getCodigo());
            registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
            		.getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ORDENDEPARTAMENTAL.getCreateKey());
            
            requestManager.save(urlCreate.getUrl(),urlCreate.getMetodo(),registro.getCampos());
            reasignarOrigen();
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        } finally {
        	ejeEstructural = null;
        	ejeEstructuralNom = null;
        	medidaPigcct = null;
        	medidaPigcctNom = null;
        }
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEjes
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEjes(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        ejeEstructural = registroAux.getCampos().get(BpordendepartamentalControladorEnum.CODIGO_EJE.getValue()).toString();
        ejeEstructuralNom = registroAux.getCampos().get(BpordendepartamentalControladorEnum.DESCRIPCION_EJE.getValue()).toString();
        medidaPigcct = null;
        medidaPigcctNom = null;
        cargarListaMedidas();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEjes
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaEjesE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(BpordendepartamentalControladorEnum.CODIGO_EJE.getValue()).toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedidas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaMedidas(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        medidaPigcct = registroAux.getCampos().get(BpordendepartamentalControladorEnum.CODIGO_MEDIDA.getValue()).toString();
        medidaPigcctNom = registroAux.getCampos().get(BpordendepartamentalControladorEnum.DESCRIPCION_MEDIDA.getValue()).toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedidas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaMedidasE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(BpordendepartamentalControladorEnum.CODIGO_MEDIDA.getValue()).toString();
	}
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	@Override
	public void abrirFormulario() {
		ejeEstructural = null;
    	ejeEstructuralNom = null;
    	medidaPigcct = null;
    	medidaPigcctNom = null;
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
     * Retorna la variable ejeEstructural
     * 
     * @return  ejeEstructural
     */
    public String getEjeEstructural() {
        return ejeEstructural;
    }
    /**
     * Asigna la variable  ejeEstructural
     * 
     * @param  ejeEstructural
     * Variable a asignar en  ejeEstructural
     */
    public void setEjeEstructural(String ejeEstructural) {
        this.ejeEstructural = ejeEstructural;
    }
    /**
     * Retorna la variable medidaPigcct
     * 
     * @return  medidaPigcct
     */
    public String getMedidaPigcct() {
        return medidaPigcct;
    }
    /**
     * Asigna la variable  medidaPigcct
     * 
     * @param  medidaPigcct
     * Variable a asignar en  medidaPigcct
     */
    public void setMedidaPigcct(String medidaPigcct) {
        this.medidaPigcct = medidaPigcct;
    }
    /**
     * Retorna la variable ejeEstructuralNom
     * 
     * @return  ejeEstructuralNom
     */
    public String getEjeEstructuralNom() {
        return ejeEstructuralNom;
    }
    /**
     * Asigna la variable  ejeEstructuralNom
     * 
     * @param  ejeEstructuralNom
     * Variable a asignar en  ejeEstructuralNom
     */
    public void setEjeEstructuralNom(String ejeEstructuralNom) {
        this.ejeEstructuralNom = ejeEstructuralNom;
    }
    /**
     * Retorna la variable medidaPigcctNom
     * 
     * @return  medidaPigcctNom
     */
    public String getMedidaPigcctNom() {
        return medidaPigcctNom;
    }
    /**
     * Asigna la variable  medidaPigcctNom
     * 
     * @param  medidaPigcctNom
     * Variable a asignar en  medidaPigcctNom
     */
    public void setMedidaPigcctNom(String medidaPigcctNom) {
        this.medidaPigcctNom = medidaPigcctNom;
    }	
    /**
     * Retorna la lista listaEjes
     * 
     * @return listaEjes
     */
    public RegistroDataModelImpl getListaEjes() {
        return listaEjes;
    }
    /**
     * Asigna la lista listaEjes
     * 
     * @param listaEjes
     * Variable a asignar en  listaEjes
     */
    public void setListaEjes(RegistroDataModelImpl listaEjes) {
        this.listaEjes = listaEjes;
    }
    /**
     * Retorna la lista listaEjes
     * 
     * @return listaEjes
     */
    public RegistroDataModelImpl getListaEjesE() {
        return listaEjesE;
    }
    /**
     * Asigna la lista listaEjes
     * 
     * @param listaEjes
     * Variable a asignar en  listaEjes
     */
    public void setListaEjesE(RegistroDataModelImpl listaEjesE) {
        this.listaEjesE = listaEjesE;
    }
    /**
     * Retorna la lista listaMedidas
     * 
     * @return listaMedidas
     */
    public RegistroDataModelImpl getListaMedidas() {
        return listaMedidas;
    }
    /**
     * Asigna la lista listaMedidas
     * 
     * @param listaMedidas
     * Variable a asignar en  listaMedidas
     */
    public void setListaMedidas(RegistroDataModelImpl listaMedidas) {
        this.listaMedidas = listaMedidas;
    }
    /**
     * Retorna la lista listaMedidas
     * 
     * @return listaMedidas
     */
    public RegistroDataModelImpl getListaMedidasE() {
        return listaMedidasE;
    }
    /**
     * Asigna la lista listaMedidas
     * 
     * @param listaMedidas
     * Variable a asignar en  listaMedidas
     */
    public void setListaMedidasE(RegistroDataModelImpl listaMedidasE) {
        this.listaMedidasE = listaMedidasE;
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
