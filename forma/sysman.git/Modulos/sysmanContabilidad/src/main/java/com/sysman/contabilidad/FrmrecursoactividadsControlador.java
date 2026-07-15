/*-
 * FrmrecursoactividadsControlador.java
 *
 * 1.0
 * 
 * 11/07/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;

import com.sysman.contabilidad.enums.FrmrecursoactividadsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/07/2024
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmrecursoactividadsControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se
	 * activa la edicion de un registro. Toma el valor del indice
	 * dentro de la grilla del registro seleccionado para editar
	 */
	private int indice;
	//<DECLARAR_ATRIBUTOS>

	private String actividad;
	private String recurso;
	private String nombreActividad;
	private String nombreRecurso;
	private String codigoConcatenado;
	private String nombreConcatenado;

	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaRecurso;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaRecursoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaActividad;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaActividadE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmrecursoactividadsControlador
	 */
 public FrmrecursoactividadsControlador() {
	 super();
	 compania = SessionUtil.getCompania();
	 try {
		 numFormulario = GeneralCodigoFormaEnum.FRM_RECURSO_ACTIVIDAD
                 .getCodigo();
			validarPermisos();
		 //<INI_ADICIONAL>
		 //</INI_ADICIONAL>
	 } catch (Exception ex) {
		 SessionUtil.redireccionarMenuPermisos();
         Logger.getLogger(FrmrecursoactividadsControlador.class.getName())
                         .log(Level.SEVERE, null, ex);
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
	 public void inicializar(){
	
		 enumBase = GenericUrlEnum.DC_RECURSO_ACTIVIDAD_SUI;
		 buscarLlave();
		 reasignarOrigen();		    
		 registro= new Registro();
		 abrirFormulario();
		 cargarListaRecurso(); cargarListaRecursoE();
		 cargarListaActividad(); cargarListaActividadE();
	
	 }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
    	
    	buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaRecurso
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaRecurso(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmrecursoactividadsControladorUrlEnum.URL1941003.getValue());
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
    	listaRecurso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
    	
    }
    /**
     * 
     * Carga la lista listaRecurso
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void  cargarListaRecursoE(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmrecursoactividadsControladorUrlEnum.URL1941003.getValue());
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaRecursoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaActividad(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmrecursoactividadsControladorUrlEnum.URL1941001.getValue());
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaActividad = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void  cargarListaActividadE(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmrecursoactividadsControladorUrlEnum.URL1941001.getValue());
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaActividadE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
    }
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRecurso
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRecurso(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("CODIGO_RECURSO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    	recurso = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    	nombreRecurso = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRecurso
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRecursoE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    	registro.getCampos().put("CODIGO_RECURSO", auxiliar);
    	recurso = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    	nombreRecurso = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    	
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividad(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("CODIGO_ACTIVIDAD", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    	actividad = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    	nombreActividad = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividadE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar =  registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    	registro.getCampos().put("CODIGO_ACTIVIDAD", auxiliar);
    	actividad = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    	nombreActividad = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();    	
    }
//</METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
@Override
	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * TODO DOCUMENTACION ADICIONAL
     */
    @Override
 public void cancelarEdicion(RowEditEvent event) {
  	 getListaInicial().load();
      }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes(){
         //<CODIGO_DESARROLLADO>
    	
    	codigoConcatenado = recurso + actividad ;
    	nombreConcatenado = nombreRecurso + "/" + nombreActividad;
    	
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
    	registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
    			codigoConcatenado);
    	registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
    			nombreConcatenado);
        //</CODIGO_DESARROLLADO>
           return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
	@Override
    public boolean insertarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes(){
         //<CODIGO_DESARROLLADO>
    	codigoConcatenado = recurso + actividad ;
    	nombreConcatenado = nombreRecurso + "/" + nombreActividad;
    	
    	registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
    			codigoConcatenado);
    	registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
    			nombreConcatenado);
    	 //</CODIGO_DESARROLLADO>
         return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override   
    public boolean actualizarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override    
    public boolean eliminarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
      return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override   
    public boolean eliminarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
       return true;
    }
    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {

    	registro.getCampos().remove("NOMBRE_RECURSO");
    	registro.getCampos().remove("NOMBRE_ACTIVIDAD");
    }
    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        
        recurso = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("CODIGO_RECURSO"),
                "").toString();
        actividad = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("CODIGO_ACTIVIDAD"),
                "").toString();
        nombreActividad = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("NOMBRE_ACTIVIDAD"),
                "").toString();
        nombreRecurso = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("NOMBRE_RECURSO"),
                "").toString();
        
    }
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
   @Override
    public void asignarValoresRegistro()
    {
        // TODO Auto-generated method stub
    }
//<SET_GET_ATRIBUTOS>
   
   /**
    * Retorna la variable indice 
    * @return indice
    */
   public int getIndice() {
       return indice;
   }
   /**
    * Asigna la variable indice
    * 
    * @param indice
    * Variable a asignar en indice
    */
   public void setIndice(int indice){
       this.indice = indice;
   }
   
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaRecurso
     * 
     * @return listaRecurso
     */
    public RegistroDataModelImpl getListaRecurso() {
        return listaRecurso;
    }
    /**
     * Asigna la lista listaRecurso
     * 
     * @param listaRecurso
     * Variable a asignar en  listaRecurso
     */
    public void setListaRecurso(RegistroDataModelImpl listaRecurso) {
        this.listaRecurso = listaRecurso;
    }
    /**
     * Retorna la lista listaRecurso
     * 
     * @return listaRecurso
     */
    public RegistroDataModelImpl getListaRecursoE() {
        return listaRecursoE;
    }
    /**
     * Asigna la lista listaRecurso
     * 
     * @param listaRecurso
     * Variable a asignar en  listaRecurso
     */
    public void setListaRecursoE(RegistroDataModelImpl listaRecursoE) {
        this.listaRecursoE = listaRecursoE;
    }
    /**
     * Retorna la lista listaActividad
     * 
     * @return listaActividad
     */
    public RegistroDataModelImpl getListaActividad() {
        return listaActividad;
    }
    /**
     * Asigna la lista listaActividad
     * 
     * @param listaActividad
     * Variable a asignar en  listaActividad
     */
    public void setListaActividad(RegistroDataModelImpl listaActividad) {
        this.listaActividad = listaActividad;
    }
    /**
     * Retorna la lista listaActividad
     * 
     * @return listaActividad
     */
    public RegistroDataModelImpl getListaActividadE() {
        return listaActividadE;
    }
    /**
     * Asigna la lista listaActividad
     * 
     * @param listaActividad
     * Variable a asignar en  listaActividad
     */
    public void setListaActividadE(RegistroDataModelImpl listaActividadE) {
        this.listaActividadE = listaActividadE;
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
//</SET_GET_LISTAS_COMBO_GRANDE>
}
