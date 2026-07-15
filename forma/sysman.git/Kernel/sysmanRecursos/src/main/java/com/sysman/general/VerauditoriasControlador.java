/*-
 * VerauditoriasControlador.java
 *
 * 1.0
 * 
 * 29/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.text.ParseException;
import java.util.Date;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/06/2023
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  VerauditoriasControlador  extends BeanBaseContinuoAcmeImpl {
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
	/**
     * Variable que almacena la fecha
     */
	private Date fecha;
    /**
     * Crea una nueva instancia de VerauditoriasControlador
     */
    public VerauditoriasControlador() {
		super();
		compania = SessionUtil.getCompania();
		
		try {
			numFormulario = GeneralCodigoFormaEnum.VER_AUDITORIA.getCodigo();
			validarPermisos();
			
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			
            if (parametrosEntrada != null) {
            	fecha = (Date) parametrosEntrada.get("fecha");
            }
		} catch (Exception ex) {
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
	  	tabla = "INF_AUDITORIA";
	  	reasignarOrigen();		    
	  	buscarLlave();
	  	registro= new Registro();
	  	abrirFormulario();
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
		try {
			parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			parametrosListado.put(GeneralParameterEnum.FECHA.getName(), 
					SysmanFunciones.convertirAFechaCadena(fecha, "dd/MM/yyyy HH:mm:ss"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("1909002");
    }
    /**
     * Retorna la variable indice 
     * @return indice
     */
    public int getIndice() {
    	return indice;
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
     */
    @Override
    public boolean insertarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
	@Override
    public boolean insertarDespues() {
		return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     */
    @Override
    public boolean actualizarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     */
    @Override   
    public boolean actualizarDespues() {
    	return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     */
    @Override    
    public boolean eliminarAntes() {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
    	indice = listaInicial.getRowIndex();
    }
    /**
     * Metodo ejecutado cuando se cierra el formulario
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
}
