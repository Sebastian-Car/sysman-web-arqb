/*-
 * IniciativasControlador.java
 *
 * 1.0
 * 
 * 17/06/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 17/06/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  IniciativasControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
    /**
     * Atributo que almacena el codigo del pilar
     */
	private String codigoPilar;
    /**
     * Atributo que almacena la descripcion del pilar
     */
	private String descripPilar;
	 /**
     * Atributo que almacena el acuerdo
     */
	private String acuerdo;
    /**
     * Crea una nueva instancia de IniciativasControlador
     */
    public IniciativasControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.INICIATIVAS_CONTROLADOR.getCodigo();
    		
    		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codigoPilar = SysmanFunciones.nvl(parametrosEntrada.get("pilar"),"").toString();
                descripPilar = SysmanFunciones.nvl(parametrosEntrada.get("descripPilar"),"").toString();
                acuerdo = SysmanFunciones.nvl(parametrosEntrada.get("acuerdo"),"").toString();
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
    	enumBase = GenericUrlEnum.INICIATIVAS;
    	reasignarOrigen();		    
    	buscarLlave();
    	registro = new Registro();
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
    	parametrosListado.put("PILAR",codigoPilar);
    	parametrosListado.put("ACUERDO",acuerdo);
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
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	registro.getCampos().put("PILAR",codigoPilar);
    	registro.getCampos().put("ACUERDO",acuerdo);
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
     * Retorna la variable codigoPilar
     * 
     * @return  codigoPilar
     */
    public String getCodigoPilar() {
    	return codigoPilar;
    }
    /**
     * Asigna la variable  codigoPilar
     * 
     * @param  codigoPilar
     * Variable a asignar en  codigoPilar
     */
    public void setCodigoPilar(String codigoPilar) {
    	this.codigoPilar = codigoPilar;
    }
    /**
     * Retorna la variable descripPilar
     * 
     * @return  descripPilar
     */
    public String getDescripPilar() {
        return descripPilar;
    }
    /**
     * Asigna la variable  descripPilar
     * 
     * @param  descripPilar
     * Variable a asignar en  descripPilar
     */
    public void setDescripPilar(String descripPilar) {
        this.descripPilar = descripPilar;
    }
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
}
