/*-
 * DgrupopersonalControlador.java
 *
 * 1.0
 * 
 * 12/12/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.precontractual.enums.PeriodoEtapaControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 12/12/2023
 * @author ecabrera
 */
@ManagedBean
@ViewScoped
public class  DgrupopersonalControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
	
	private String codigo;
    private String nombre;
    private String descripcion;
    
    private List<Registro> listaano;
    
    private RegistroDataModelImpl listacodigo;
    private RegistroDataModelImpl listacodigoE;
    
    private String auxiliar;
    
    /**
     * Crea una nueva instancia de DgrupopersonalControlador
     */
    public DgrupopersonalControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario=2439;
    		validarPermisos();
    	} catch (Exception ex) {
    		logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
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
	  enumBase = GenericUrlEnum.D_GRUPO_PERSONAL;
	  buscarLlave();
	  reasignarOrigen();
	  registro = new Registro(new HashMap<String, Object>());
	  cargarListaano();
	  cargarListacodigo();
	  cargarListacodigoE();
	  abrirFormulario();
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
    	buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        parametrosListado.put("CODIGO",codigo);
    }

    //<METODOS_CARGAR_LISTA>
    public void cargarListaano(){
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	
	    try {
	    	listaano = RegistroConverter.toListRegistro(
	                        requestManager.getList(UrlServiceUtil.getInstance()
	                                        .getUrlServiceByUrlByEnumID(
	                                                        PeriodoEtapaControladorUrlEnum.URL3282
	                                                                        .getValue())
	                                        .getUrl(), param));
	    }
	    catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}
    
    public void cargarListacodigo(){
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID("184400G");
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }
    
    public void  cargarListacodigoE(){
    	listacodigoE = listacodigo;
    }
    
    public void seleccionarFilacodigo(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
		setCodigo(registroAux.getCampos().get("CODIGO").toString());
		setNombre(registroAux.getCampos().get("NOMBRE").toString());
		setDescripcion(registroAux.getCampos().get("DESCRIPCION").toString());
		reasignarOrigen();
    }
    
    public void seleccionarFilacodigoE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
		setAuxiliar((String) registroAux.getCampos().get("CODIGO"));
    }
    
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
    	if (SysmanFunciones.validarVariableVacio(codigo)) {
    		JsfUtil.agregarMensajeError(idioma.getString("TI_MS_ERROR_VALIDACION"));
    		return false;
    	}
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	registro.getCampos().put("CODIGO", codigo);
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
       return true;
    }
    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
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

   public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public RegistroDataModelImpl getListacodigo() {
		return listacodigo;
	}
	public void setListacodigo(RegistroDataModelImpl listacodigo) {
		this.listacodigo = listacodigo;
	}
	public RegistroDataModelImpl getListacodigoE() {
		return listacodigoE;
	}
	public void setListacodigoE(RegistroDataModelImpl listacodigoE) {
		this.listacodigoE = listacodigoE;
	}
	public List<Registro> getListaano() {
		return listaano;
	}
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}
	public String getAuxiliar() {
		return auxiliar;
	}
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
	
	 public void ejecutarrcCerrar() {
	     Map<String, Object> param = new HashMap<>();
	     //param.put("CODIGO", parametrosEntrada.get("CODIGO").toString());
	     Direccionador direccionador = new Direccionador();
	     direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.FRM_GRUPO_PERSONAL_CONTROLADOR.getCodigo()));
	     direccionador.setParametros(param);
	     SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	 }
}
