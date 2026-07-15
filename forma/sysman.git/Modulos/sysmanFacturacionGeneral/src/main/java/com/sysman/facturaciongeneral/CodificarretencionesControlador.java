/*-
 * CodificarretencionesControlador.java
 *
 * 1.0
 * 
 * 11/03/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

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
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/03/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  CodificarretencionesControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
    /**
     * Lista de las cuentas de la tabla PLAN_CONTABLE
     */
	private RegistroDataModelImpl listaCuenta;
    /**
     * Lista de las cuentas de la tabla PLAN_CONTABLE
     */
	private RegistroDataModelImpl listaCuentaE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
	private String auxiliar;
	
	private String anio;
    private String tipoComp;
    private String numeroComp;
    
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;
    /**
     * Crea una nueva instancia de CodificarretencionesControlador
     */
    public CodificarretencionesControlador() {
    	super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CODIFICARRETENCIONES_CONTROLADOR.getCodigo();
            validarPermisos();
            
            Map<String,Object> parametrosEntrada = SessionUtil.getFlash();
            if(parametrosEntrada != null) {
                anio = parametrosEntrada.get("anio").toString();
                tipoComp = parametrosEntrada.get("tipoComp").toString();
                numeroComp = parametrosEntrada.get("numeroComp").toString();
            }
        } catch(SysmanException ex) {
            Logger.getLogger(CodificarretencionesControlador.class.getName())
                            .log(Level.SEVERE,null,ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        	SessionUtil.cleanFlash();
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
    	enumBase = GenericUrlEnum.RETENCIONESVENTACNT;
    	buscarLlave();
    	reasignarOrigen();		    
    	registro = new Registro(new HashMap<String,Object>());
    	cargarListaCuenta(); 
    	cargarListaCuentaE();
		abrirFormulario();
				
		JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','950px');");
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
    	parametrosListado.put(GeneralParameterEnum.ANIO.getName(),anio);
    	parametrosListado.put(GeneralParameterEnum.TIPOCOMP.getName(),tipoComp);
    	parametrosListado.put("NUMEROCOMP",numeroComp);
    }
    /**
     * 
     * Carga la lista listaCuenta
     *
     */
    public void cargarListaCuenta() {
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID("16164");
    	
    	Map<String,Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		param.put(GeneralParameterEnum.ANO.getName(),anio);

		listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                	  urlBean.getUrlConteo().getUrl(),param,true,
                	  GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaCuenta
     *
     */
    public void  cargarListaCuentaE() {
    	listaCuentaE = listaCuenta;
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuenta(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
        		registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar = (String) registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario() {
    	registro.getCampos().put("VALOR_RETENIDO","0");
    	registro.getCampos().put("BASEGRAVABLE","0");
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
    	long consecutivo = 0;
		try {
			consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo("RETENCIONESVENTACNT", 
																	SysmanFunciones.concatenar("COMPANIA = ''", compania , "''"), 
																	GeneralParameterEnum.CONSECUTIVO.getName());
		} catch(SystemException e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),consecutivo);
    	registro.getCampos().put(GeneralParameterEnum.ANIO.getName(),anio);
    	registro.getCampos().put(GeneralParameterEnum.TIPOCOMP.getName(),tipoComp);
    	registro.getCampos().put("NUMEROCOMP",numeroComp);
    	
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
    public void asignarValoresRegistro() {
    	registro.getCampos().put("VALOR_RETENIDO","0");
    	registro.getCampos().put("BASEGRAVABLE","0");
    }
    /**
     * Retorna la lista listaCuenta
     * 
     * @return listaCuenta
     */
    public RegistroDataModelImpl getListaCuenta() {
    	return listaCuenta;
    }
    /**
     * Asigna la lista listaCuenta
     * 
     * @param listaCuenta
     * Variable a asignar en  listaCuenta
     */
    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
    	this.listaCuenta = listaCuenta;
    }
    /**
     * Retorna la lista listaCuenta
     * 
     * @return listaCuenta
     */
    public RegistroDataModelImpl getListaCuentaE() {
    	return listaCuentaE;
    }
    /**
     * Asigna la lista listaCuenta
     * 
     * @param listaCuenta
     * Variable a asignar en  listaCuenta
     */
    public void setListaCuentaE(RegistroDataModelImpl listaCuentaE) {
    	this.listaCuentaE = listaCuentaE;
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