/*-
 * AcuerdoPazControlador.java
 *
 * 1.0
 * 
 * 16/06/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 16/06/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class AcuerdoPazControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    /**
     * Lista que almacena los pilares
     */
    private RegistroDataModelImpl listaPilares;
    /**
     * Atributo de referencia para el subformulario 
     */
    private Registro registroSub;
    
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    /**
     * Crea una nueva instancia de AcuerdoPazControlador
     */
	public AcuerdoPazControlador() {
		super();
	    compania = SessionUtil.getCompania();
	    try {
	    	registro = new Registro(new HashMap<String,Object>());
			registroSub = new Registro(new HashMap<String,Object>());
			numFormulario = GeneralCodigoFormaEnum.ACUERDO_PAZ_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch(Exception ex) {
			 logger.error(ex.getMessage(),ex);
			 SessionUtil.redireccionarMenuPermisos();
        } 
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	@Override
	public void iniciarListas() {}
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
	@Override
	public void iniciarListasSub() {
		cargarListaPilares();
	}
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
	@Override
	public void iniciarListasSubNulo() {
		listaPilares = null;
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
		enumBase = GenericUrlEnum.ACUERDO_PAZ;
		buscarLlave();
		asignarOrigenDatos();
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
    	parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    }
    /**
     * 
     * Carga la lista listaPilares
     *
     */
    public void cargarListaPilares() {
    	try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			param.put("ACUERDO",registro.getCampos().get("COD_ACUERDO"));

	        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
	        					  GenericUrlEnum.PILARES.getGridKey());

	        listaPilares = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
	                       		  CacheUtil.getLlaveServicio(urlConexionCache,GenericUrlEnum.PILARES.getTable()));
    	} catch(SysmanException e) {
	        logger.error(e.getMessage(),e);
	        JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }
    /**
     * Metodo ejecutado al oprimir el boton Iniciativa
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirIniciativa(Registro reg,int indice) {
    	agregarRegistroNuevo(false);     
    	String[] campos = { "pilar","descripPilar","acuerdo" };
        String[] valores = { reg.getCampos().get("COD_PILAR").toString(),
        					 reg.getCampos().get("DESCRIPCION").toString(),
        					 reg.getCampos().get("ACUERDO").toString() };
        
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.INICIATIVAS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),campos,valores);
    }
    /**
     * Metodo de insercion del formulario Pilares
     * 
     */   
    public void agregarRegistroSubPilares() { 
    	try {
	    	registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
	    	registroSub.getCampos().put("ACUERDO",registro.getCampos().get("COD_ACUERDO"));
	    	registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());
			
			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
									GenericUrlEnum.PILARES.getCreateKey());
			
			requestManager.save(urlCreate.getUrl(),urlCreate.getMetodo(),registroSub.getCampos());
			cargarListaPilares();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
    	} catch(SystemException ex) {
			Logger.getLogger(AcuerdoPazControlador.class.getName()).log(Level.SEVERE,null,ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String,Object>());
		}
    }
    /**
     * Metodo de edicion del formulario Pilares
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubPilares(RowEditEvent event) {
    	Registro reg = (Registro) event.getObject();
    	try {
    		reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					new Date());
			
	    	UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
									GenericUrlEnum.PILARES.getUpdateKey());
	    	
			requestManager.update(urlUpdate.getUrl(),urlUpdate.getMetodo(),reg.getCampos(),reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
    	} catch(SystemException ex) {
			Logger.getLogger(AcuerdoPazControlador.class.getName()).log(Level.SEVERE,null,ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
            cargarListaPilares();
        }
    }
    /**
     * Metodo de eliminacion del formulario Pilares
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubPilares(Registro reg) {
    	try {
			UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
									GenericUrlEnum.PILARES.getDeleteKey());
			
			requestManager.delete(urlDelete.getUrl(),reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			cargarListaPilares();
		} catch(SystemException ex) {
			Logger.getLogger(AcuerdoPazControlador.class.getName()).log(Level.SEVERE,null,ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
    }
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * para el subformulario Pilares
     *
     */
    public void cancelarEdicionPilares() {
    	cargarListaPilares();
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
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
    	precargarRegistro();
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
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }
    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
}
