/*-
 * BpordenmunicipalControlador.java
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
import com.sysman.plandesarrollo.enums.BpordenmunicipalControladorEnum;
import com.sysman.plandesarrollo.enums.BpordenmunicipalControladorUrlEnum;
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
public class  BpordenmunicipalControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
	private String politica;
    private String categoria;
    private String subCategoria;
    private String politicaNom;
    private String categoriaNom;
    private String subCategoriaNom;
    private String tipoP;
    private String tipoC;
    private String tipoSub;
    private String vigencia;
    private String codPlan;

    private RegistroDataModelImpl listaPoliticas;
    private RegistroDataModelImpl listaPoliticasE;
    private RegistroDataModelImpl listaCategorias;
    private RegistroDataModelImpl listaCategoriasE;
    private RegistroDataModelImpl listaSubCategorias;
    private RegistroDataModelImpl listaSubCategoriasE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    /**
     * Crea una nueva instancia de BpordenmunicipalControlador
     */
    public BpordenmunicipalControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.BPORDENMUNICIPAL_CONTROLADOR.getCodigo();
    		
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
    	enumBase = GenericUrlEnum.BP_ORDENMUNICIPAL;
    	reasignarOrigen();		    
    	buscarLlave();
		registro = new Registro();
		
		cargarListaPoliticas(); 
		cargarListaPoliticasE();
		cargarListaCategorias(); 
		cargarListaCategoriasE();
		cargarListaSubCategorias(); 
		cargarListaSubCategoriasE();

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
    	parametrosListado.put(BpordenmunicipalControladorEnum.COD_PLAN.getValue(),codPlan);
    }
    /**
     * 
     * Carga la lista listaPoliticas
     *
     */
    public void cargarListaPoliticas() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordenmunicipalControladorUrlEnum.URL1966001.getValue());

    	listaPoliticas = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaPoliticas
     *
     */
    public void  cargarListaPoliticasE() {
    	listaPoliticasE = listaPoliticas;
    }
    /**
     * 
     * Carga la lista listaCategorias
     *
     */
    public void cargarListaCategorias() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	param.put(BpordenmunicipalControladorEnum.POLITICA.getValue(),
    			SysmanFunciones.nvl(politica,"0"));
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordenmunicipalControladorUrlEnum.URL1966003.getValue());

    	listaCategorias = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaCategorias
     *
     */
    public void  cargarListaCategoriasE() {
    	listaCategoriasE = listaCategorias;
    }
    /**
     * 
     * Carga la lista listaSubCategorias
     *
     */
    public void cargarListaSubCategorias() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	param.put(BpordenmunicipalControladorEnum.CATEGORIA.getValue(),
    			SysmanFunciones.nvl(categoria,"0"));
    	param.put(BpordenmunicipalControladorEnum.POLITICA.getValue(),
    			SysmanFunciones.nvl(politica,"0"));
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(BpordenmunicipalControladorUrlEnum.URL1966005.getValue());

    	listaSubCategorias = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaSubCategorias
     *
     */
    public void  cargarListaSubCategoriasE() {
    	listaSubCategoriasE = listaSubCategorias;
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
            registro.getCampos().put(BpordenmunicipalControladorEnum.COD_PLAN.getValue(),codPlan);
            registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
            registro.getCampos().put(BpordenmunicipalControladorEnum.COD_POLITICA.getValue(),politica);
            registro.getCampos().put(BpordenmunicipalControladorEnum.TIPOP.getValue(),tipoP);
            registro.getCampos().put(BpordenmunicipalControladorEnum.COD_CATEGORIA.getValue(),categoria);
            registro.getCampos().put(BpordenmunicipalControladorEnum.TIPOC.getValue(),tipoC);
            registro.getCampos().put(BpordenmunicipalControladorEnum.COD_SUBCATEGORIA.getValue(),subCategoria);
            registro.getCampos().put(BpordenmunicipalControladorEnum.TIPOSUB.getValue(),tipoSub);
            registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),SessionUtil.getUser().getCodigo());
            registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
            		.getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ORDENMUNICIPAL.getCreateKey());
            
            requestManager.save(urlCreate.getUrl(),urlCreate.getMetodo(),registro.getCampos());
            reasignarOrigen();
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        } finally {
        	politica = null;
        	politicaNom = null;
        	categoria = null;
        	categoriaNom = null;
        	subCategoria = null;
        	subCategoriaNom = null;
        }
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPoliticas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPoliticas(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        politica = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        politicaNom = registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
        tipoP = registroAux.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString();
        categoria = null;
    	categoriaNom = null;
        cargarListaCategorias();
        subCategoria = null;
    	subCategoriaNom = null;
        cargarListaSubCategorias();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPoliticas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPoliticasE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCategorias
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCategorias(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        categoria = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        categoriaNom = registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
        tipoC = registroAux.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString();
        subCategoria = null;
    	subCategoriaNom = null;
        cargarListaSubCategorias();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCategorias
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCategoriasE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubCategorias
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSubCategorias(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        subCategoria = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        subCategoriaNom = registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
        tipoSub = registroAux.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubCategorias
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaSubCategoriasE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	@Override
	public void abrirFormulario() {
		politica = null;
    	politicaNom = null;
    	categoria = null;
    	categoriaNom = null;
    	subCategoria = null;
    	subCategoriaNom = null;
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
     * Retorna la variable politica
     * 
     * @return  politica
     */
    public String getPolitica() {
        return politica;
    }
    /**
     * Asigna la variable  politica
     * 
     * @param  politica
     * Variable a asignar en  politica
     */
    public void setPolitica(String politica) {
        this.politica = politica;
    }
    /**
     * Retorna la variable categoria
     * 
     * @return  categoria
     */
    public String getCategoria() {
        return categoria;
    }
    /**
     * Asigna la variable  categoria
     * 
     * @param  categoria
     * Variable a asignar en  categoria
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    /**
     * Retorna la variable subCategoria
     * 
     * @return  subCategoria
     */
    public String getSubCategoria() {
        return subCategoria;
    }
    /**
     * Asigna la variable  subCategoria
     * 
     * @param  subCategoria
     * Variable a asignar en  subCategoria
     */
    public void setSubCategoria(String subCategoria) {
        this.subCategoria = subCategoria;
    }
    /**
     * Retorna la variable politicaNom
     * 
     * @return  politicaNom
     */
    public String getPoliticaNom() {
        return politicaNom;
    }
    /**
     * Asigna la variable  politicaNom
     * 
     * @param  politicaNom
     * Variable a asignar en  politicaNom
     */
    public void setPoliticaNom(String politicaNom) {
        this.politicaNom = politicaNom;
    }
    /**
     * Retorna la variable categoriaNom
     * 
     * @return  categoriaNom
     */
    public String getCategoriaNom() {
        return categoriaNom;
    }
    /**
     * Asigna la variable  categoriaNom
     * 
     * @param  categoriaNom
     * Variable a asignar en  categoriaNom
     */
    public void setCategoriaNom(String categoriaNom) {
        this.categoriaNom = categoriaNom;
    }
    /**
     * Retorna la variable subCategoriaNom
     * 
     * @return  subCategoriaNom
     */
    public String getSubCategoriaNom() {
        return subCategoriaNom;
    }
    /**
     * Asigna la variable  subCategoriaNom
     * 
     * @param  subCategoriaNom
     * Variable a asignar en  subCategoriaNom
     */
    public void setSubCategoriaNom(String subCategoriaNom) {
        this.subCategoriaNom = subCategoriaNom;
    }
    /**
     * Retorna la lista listaPoliticas
     * 
     * @return listaPoliticas
     */
    public RegistroDataModelImpl getListaPoliticas() {
        return listaPoliticas;
    }
    /**
     * Asigna la lista listaPoliticas
     * 
     * @param listaPoliticas
     * Variable a asignar en  listaPoliticas
     */
    public void setListaPoliticas(RegistroDataModelImpl listaPoliticas) {
        this.listaPoliticas = listaPoliticas;
    }
    /**
     * Retorna la lista listaPoliticas
     * 
     * @return listaPoliticas
     */
    public RegistroDataModelImpl getListaPoliticasE() {
        return listaPoliticasE;
    }
    /**
     * Asigna la lista listaPoliticas
     * 
     * @param listaPoliticas
     * Variable a asignar en  listaPoliticas
     */
    public void setListaPoliticasE(RegistroDataModelImpl listaPoliticasE) {
        this.listaPoliticasE = listaPoliticasE;
    }
    /**
     * Retorna la lista listaCategorias
     * 
     * @return listaCategorias
     */
    public RegistroDataModelImpl getListaCategorias() {
        return listaCategorias;
    }
    /**
     * Asigna la lista listaCategorias
     * 
     * @param listaCategorias
     * Variable a asignar en  listaCategorias
     */
    public void setListaCategorias(RegistroDataModelImpl listaCategorias) {
        this.listaCategorias = listaCategorias;
    }
    /**
     * Retorna la lista listaCategorias
     * 
     * @return listaCategorias
     */
    public RegistroDataModelImpl getListaCategoriasE() {
        return listaCategoriasE;
    }
    /**
     * Asigna la lista listaCategorias
     * 
     * @param listaCategorias
     * Variable a asignar en  listaCategorias
     */
    public void setListaCategoriasE(RegistroDataModelImpl listaCategoriasE) {
        this.listaCategoriasE = listaCategoriasE;
    }
    /**
     * Retorna la lista listaSubCategorias
     * 
     * @return listaSubCategorias
     */
    public RegistroDataModelImpl getListaSubCategorias() {
        return listaSubCategorias;
    }
    /**
     * Asigna la lista listaSubCategorias
     * 
     * @param listaSubCategorias
     * Variable a asignar en  listaSubCategorias
     */
    public void setListaSubCategorias(RegistroDataModelImpl listaSubCategorias) {
        this.listaSubCategorias = listaSubCategorias;
    }
    /**
     * Retorna la lista listaSubCategorias
     * 
     * @return listaSubCategorias
     */
    public RegistroDataModelImpl getListaSubCategoriasE() {
        return listaSubCategoriasE;
    }
    /**
     * Asigna la lista listaSubCategorias
     * 
     * @param listaSubCategorias
     * Variable a asignar en  listaSubCategorias
     */
    public void setListaSubCategoriasE(RegistroDataModelImpl listaSubCategoriasE) {
        this.listaSubCategoriasE = listaSubCategoriasE;
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
