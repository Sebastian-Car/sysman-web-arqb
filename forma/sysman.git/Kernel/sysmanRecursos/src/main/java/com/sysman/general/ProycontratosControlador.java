/*-
 * ProycontratosControlador.java
 *
 * 1.0
 * 
 * 06/03/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 06/03/2024
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  ProycontratosControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
    /**
     * Variable que almacena la fecha
     */
	private String fecha;
    /**
     * Variable que almacena el numero de contrato
     */
	private String numeroContrato;
	/**
     * Variable que almacena el tipo de contrato
     */
	private String tipoContrato;
    /**
     * Lista que almacena las referencias
     */
	private RegistroDataModelImpl listacodigo;
    /**
     * Lista que almacena las referencias
     */
	private RegistroDataModelImpl listacodigoE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
	private String auxiliar;
    /**
     * Crea una nueva instancia de ProycontratosControlador
     */
    public ProycontratosControlador() {
    	super();
        numFormulario = GeneralCodigoFormaEnum.PROYECTOS_CONT_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        
        try {
        	cargarFlash();
        	validarPermisos();
        } catch (SysmanException ex) {
        	logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }
    
    private void cargarFlash() {
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        
        if (parametrosEntrada != null) {
            numeroContrato = SysmanFunciones.nvl(parametrosEntrada.get("numeroContrato"), "")
                            .toString();
            
            tipoContrato = SysmanFunciones.nvl(parametrosEntrada.get("tipoContrato"), "")
                    		.toString();
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
    	enumBase = GenericUrlEnum.PROYCONTRATOS;
    	reasignarOrigen();
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        cargarListacodigo();
        cargarListacodigoE();
        abrirFormulario();
        
    	try {
            fecha = SysmanFunciones.convertirAFechaCadena(
                            new Date(),
                            "dd/MM/yyyy");
        }
        catch (ParseException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(ProycontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
    	buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);
        parametrosListado.put(GeneralParameterEnum.NUMEROCONTRATO.getName(), numeroContrato);
    }
    /**
     * 
     * Carga la lista listacodigo
     *
     */
	public void cargarListacodigo() {
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("32057");
    	
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
		              urlBean.getUrlConteo().getUrl(), param,
		              true, GeneralParameterEnum.CODIGO.getName());
	}
    /**
     * 
     * Carga la lista listacodigo
     *
     */
	public void cargarListacodigoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("32057");
    	
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
		               urlBean.getUrlConteo().getUrl(), param,
		               true, GeneralParameterEnum.CODIGO.getName());
	}
    /**
     * Metodo ejecutado al cambiar el control codigo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
	public void cambiarcodigoC(int rowNum) {
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilacodigo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), 
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilacodigoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
	    auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	    registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), 
	    		registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	@Override
	public void abrirFormulario() {}
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * 
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
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                compania);
    	registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                tipoContrato);
    	registro.getCampos().put(GeneralParameterEnum.NUMEROCONTRATO.getName(),
                numeroContrato);
    	registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    	
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
    	registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    	
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
     * Retorna la variable fecha
     * 
     * @return  fecha
     */
    public String getFecha() {
        return fecha;
    }
    /**
     * Asigna la variable  fecha
     * 
     * @param  fecha
     * Variable a asignar en  fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    /**
     * Retorna la variable numeroContrato
     * 
     * @return  numeroContrato
     */
    public String getNumeroContrato() {
        return numeroContrato;
    }
    /**
     * Asigna la variable  numeroContrato
     * 
     * @param  numeroContrato
     * Variable a asignar en  numeroContrato
     */
    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }
    /**
     * Retorna la variable tipoContrato
     * 
     * @return  tipoContrato
     */
    public String getTipoContrato() {
        return tipoContrato;
    }
    /**
     * Asigna la variable  tipoContrato
     * 
     * @param  tipoContrato
     * Variable a asignar en  tipoContrato
     */
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigo() {
        return listacodigo;
    }
    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en  listacodigo
     */
    public void setListacodigo(RegistroDataModelImpl listacodigo) {
        this.listacodigo = listacodigo;
    }
    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigoE() {
        return listacodigoE;
    }
    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en  listacodigo
     */
    public void setListacodigoE(RegistroDataModelImpl listacodigoE) {
        this.listacodigoE = listacodigoE;
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
