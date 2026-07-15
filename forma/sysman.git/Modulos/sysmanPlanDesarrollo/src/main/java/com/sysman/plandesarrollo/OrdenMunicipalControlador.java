/*-
 * OrdenMunicipalControlador.java
 *
 * 1.0
 * 
 * 01/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/07/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  OrdenMunicipalControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
    /**
     * Lista que almacena el tipo
     */
	private List<Registro> listaTipo;
	
	@EJB
    private EjbSysmanUtilRemote sysmanUtil;
    /**
     * Crea una nueva instancia de OrdenMunicipalControlador
     */
    public OrdenMunicipalControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.ORDENMUNICIPAL_CONTROLADOR.getCodigo();
    		validarPermisos();
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
    	enumBase = GenericUrlEnum.ORDENMUNICIPAL;
    	buscarLlave();
    	registro = new Registro(new HashMap<String,Object>());
    	reasignarOrigen();		    
		cargarListaTipo();
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
    }
    /**
     * 
     * Carga la lista listaTipo
     *
     */
	public void cargarListaTipo() {
		Map<String,Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID("1967001")
                                            .getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	@Override
	public void abrirFormulario() {
		String manejaTrazadores = "";
		try {
			manejaTrazadores = SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
									"APLICA TRAZADORES EN PLAN DE DESARROLLO",
									SessionUtil.getModulo(),new Date(),true),"NO");
		} catch(SystemException e) {
			e.printStackTrace();
		}
		
		try {
			if(manejaTrazadores.equals("NO")) {
				throw new SysmanException(
	                    idioma.getString("MSM_PERMISOS_ACCEDER"));
			}
		} catch(Exception ex) {
			 logger.error(ex.getMessage(),ex);
			 SessionUtil.redireccionarMenuPermisos();
       } 
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
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
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
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {}
    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public List<Registro> getListaTipo() {
        return listaTipo;
    }
    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en  listaTipo
     */
    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }
}
