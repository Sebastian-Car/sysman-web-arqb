/*-
 * CertificadoinexistenciasControlador.java
 *
 * 1.0
 * 
 * 01/07/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

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
import javax.naming.NamingException;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.ejb.impl.EjbPrecontractualUno;
import com.sysman.precontractual.enums.CertificadoinexistenciasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/07/2022
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class CertificadoinexistenciasControlador extends BeanBaseDatosAcmeImpl{
	/**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    /**
     * atributo en el cual se almacena el nuevo numero que se
     * asignara al certificado de inexistencia
     */
    private String nuevoNumero;
    /**
     * variable que almacena el valor del ckMultiple
     */
    private boolean ckMultiple;
    /**
     * variable que almacena el valor del campo noRegistros
     */
    private int noRegistros;
    /**
     * variable que permite la visibilidad del campo noRegistros
     */
    private boolean visibleReg;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * carga la lista de los roles de solicitudes
     */
    private RegistroDataModelImpl listasolicitante;
    /**
     * carga la lista de las dependencias
     */
    private RegistroDataModelImpl listadependencia;
    /**
     * carga la lista de los causales de inexistencia
     */
    private RegistroDataModelImpl listacausalInexistencia;
    /**
     * carga la lista de los proyectos
     */
    private RegistroDataModelImpl listaproyecto;
    /**
     * carga la lista de la plantilla
     */
    private RegistroDataModelImpl listaplantilla;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    @EJB
    private EjbPrecontractualUno ejbPrecontractualUno;
    
    private Map<String,Object> parametroswf;
    /**
     * Crea una nueva instancia de CertificadoinexistenciasControlador
     */
	public CertificadoinexistenciasControlador() 
	{
		super();
	    compania = SessionUtil.getCompania();
	    
	    try 
	    {
	    	numFormulario = GeneralCodigoFormaEnum.CERTIFICADO_INEXISTENCIA_CONTROLADOR
							.getCodigo();
	    	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "19");
			}
	    	validarPermisos();
		 } 
	    catch (Exception ex) 
	    {
	    	logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	@Override
	public void iniciarListas()
	{
		cargarListasolicitante(); 
		cargarListadependencia(); 
		cargarListacausalInexistencia(); 
		cargarListaproyecto(); 
		cargarListaplantilla();
	}
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
	@Override
	public void iniciarListasSub() {}
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
	@Override
	public void iniciarListasSubNulo() {}
    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
    	enumBase = GenericUrlEnum.CERTIFICADO_INEXISTENCIA;
        buscarLlave();
        asignarOrigenDatos();
        
        ckMultiple = false;
        noRegistros = 1;
	}
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() 
    {
    	buscarUrls();
    	
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);   
    }
    /**
     * 
     * Carga la lista listasolicitante
     */
    public void cargarListasolicitante()
    {
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                CertificadoinexistenciasControladorUrlEnum.URL1892001
                                                .getValue());
    	
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listasolicitante = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());    
	}
    /**
     * 
     * Carga la lista listadependencia
     */
    public void cargarListadependencia()
    {    
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                CertificadoinexistenciasControladorUrlEnum.URL62109
                                                .getValue());
    	
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listadependencia = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());  
    }
    /**
     * 
     * Carga la lista listacausalInexistencia
     */
	public void cargarListacausalInexistencia()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                CertificadoinexistenciasControladorUrlEnum.URL1890001
                                                .getValue());
    	
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listacausalInexistencia = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());  
	}
    /**
     * 
     * Carga la lista listaproyecto
     */
	public void cargarListaproyecto()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                CertificadoinexistenciasControladorUrlEnum.URL32055
                                                .getValue());
    	
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaproyecto = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());  
	}
    /**
     * 
     * Carga la lista listaplantilla
     */
	public void cargarListaplantilla()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                CertificadoinexistenciasControladorUrlEnum.URL104073
                                                .getValue());
    	
		Map<String, Object> param = new TreeMap<>();
		
		listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
						urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName()); 
	}
    /**
     * Metodo ejecutado al cambiar el control multiple
     */
	public void cambiarmultiple() 
	{
		if(ckMultiple)
		{
			visibleReg = true;
		}
		else
		{
			visibleReg = false;
		}
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * del dialogo cambiarNumero en la vista
     *
     */
	public void aceptarcambiarNumero() 
	{
		String usuario = SessionUtil.getUser().getCodigo();
        long numeroCertificado = Long.parseLong(
        		extraerString(registro.getCampos().get("NUMERO")));
        long numeroNuevo = Long.parseLong(nuevoNumero);
        
        try 
        {
        	ejbPrecontractualUno.cambiarNumeroCertificado(compania, 
        			usuario, 
        			numeroCertificado, 
        			numeroNuevo);

            Map<String, Object> nuevaLlave = css;
            nuevaLlave.put("KEY_NUMERO", numeroNuevo);
            cargarRegistro(nuevaLlave, accion);
            
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            
            nuevoNumero = null;
        }
        catch (SystemException e) 
        {
        	logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	 /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listasolicitante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilasolicitante(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
        registro.getCampos().put("SOLICITANTE", registroAux.getCampos()
        		.get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRE_SOLICITANTE", registroAux.getCampos()
        		.get("NOMBRE_ROL"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFiladependencia(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    registro.getCampos().put("DEPENDENCIA", registroAux.getCampos()
	    		.get(GeneralParameterEnum.CODIGO.getName()));
	    registro.getCampos().put("NOMBRE_DEP", registroAux.getCampos()
        		.get(GeneralParameterEnum.NOMBRE.getName()));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacausalInexistencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilacausalInexistencia(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    registro.getCampos().put("COD_INEXISTENCIA", registroAux.getCampos()
	    		.get(GeneralParameterEnum.CODIGO.getName()));
	    registro.getCampos().put("NOMBRE_CAUSAL", registroAux.getCampos()
        		.get("DESCRIPCION"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaproyecto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaproyecto(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    registro.getCampos().put("COD_PROYECTO", registroAux.getCampos()
	    		.get(GeneralParameterEnum.CODIGO.getName()));
	    registro.getCampos().put("NOMBRE_PROYECTO", registroAux.getCampos()
        		.get("NOMBREPROYECTO"));
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaplantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaplantilla(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
	    registro.getCampos().put("CODIGO", registroAux.getCampos()
	    		.get(GeneralParameterEnum.CODIGO.getName()));
	    registro.getCampos().put("NOMBRE_PLANTILLA", registroAux.getCampos()
        		.get(GeneralParameterEnum.NOMBRE.getName()));
	}
    /**
     * 
     * Metodo ejecutado al oprimir el boton btCambiar
     * en la vista
     *
     */
	public void oprimirbtCambiar() {}
    /**
     * 
     * Metodo ejecutado al oprimir el boton btPlantilla
     * en la vista
     *
     */
	public void oprimirbtPlantilla() 
	{
        try 
        {
        	String plantilla = "";
        	
        	plantilla = registro.getCampos().get("CODIGO").toString();
        	
			Map<String, Object> param = new TreeMap<>();
			
			param.put("CODIGO", plantilla);
	        param.put("TIPO", "17");
	        
	        Registro rs;

			rs = RegistroConverter.toRegistro(
			                requestManager.get(UrlServiceUtil
			                                .getInstance()
			                                .getUrlServiceByUrlByEnumID(
			                                                CertificadoinexistenciasControladorUrlEnum.URL104063
			                                                                .getValue())
			                                .getUrl(),
			                                param));
			
			if (rs != null) 
			{
                Date fecha = (Date) rs.getCampos()
                                .get(GeneralParameterEnum.FECHA.getName());
                
                String[] campos = new String[3];
                String[] valores = new String[3];
                
                campos[0] = "codigoPlantilla";
                campos[1] = "fechaPlantilla";
                campos[2] = "nombreDocDescarga";
                
                valores[0] = plantilla;
                valores[1] = SysmanFunciones.formatearFecha(fecha);
                valores[2] = SysmanFunciones.concatenar("CERTIFICADO_INEXISTENCIA", "_",
                			 	registro.getCampos().get("NUMERO").toString());
                
                HashMap<String, String> variablesConsultaW = new HashMap<>();
                
                variablesConsultaW.put("s$compania$s",
                                SysmanFunciones.concatenar("'", compania,
                                                "'"));
                variablesConsultaW.put("s$numeroCert$s",
                                registro.getCampos().get("NUMERO").toString());
                
                SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);
                
                String numForm = String
                        .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo());
                
                SessionUtil.cargarModalDatosFlash(numForm,
                        SessionUtil.getModulo(),
                        campos, valores);
			}
		} 
        catch (SystemException e)
        {
			e.printStackTrace();
		}
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	@Override
	public void abrirFormulario() {}
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() 
    {
        precargarRegistro();
        
        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put("KEY_NUMERO", registro.getCampos().get("NUMERO"));
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes()
    {
    	for (int i = 1; i <= noRegistros; i++) 
    	{
	    	registro.getCampos().put("COMPANIA", compania);
	    	
	    	registro.getCampos().remove("NOMBRE_SOLICITANTE");
	    	registro.getCampos().remove("NOMBRE_DEP");
	    	registro.getCampos().remove("NOMBRE_CAUSAL");
	    	registro.getCampos().remove("NOMBRE_PROYECTO");
	    	
	    	try 
	    	{
		    	long numero = ejbSysmanUtil.generarConsecutivoConValorInicial(
		    			"CERTIFICADO_INEXISTENCIA",
		    			"COMPANIA = ''" + compania + "''", "NUMERO", "1");
		    	
		    	registro.getCampos().put("NUMERO", numero);
	
		    	Map<String, Object> parameters = registro.getCampos();
                parameters.put("CREATED_BY",
                                SessionUtil.getUser().getCodigo());
                parameters.put("DATE_CREATED", new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(parameters);
                
                rid = requestManager.save(urlCreacion.getUrl(),
                                urlCreacion.getMetodo(), parameter);
                
	    	}
		    catch (SystemException ex) 
	    	{
	            Logger.getLogger(CertificadoinexistenciasControlador.class.getName())
	                            .log(Level.SEVERE, null, ex);
	        }
    	}
    	
    	insertarDespues();
        actualizarDespues();
        JsfUtil.agregarMensajeInformativo(
        		idioma.getString("MSM_REGISTRO_INGRESADO"));
    	
		return false;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarDespues()
    {
    	ckMultiple = false;
    	noRegistros = 1;
    	
    	if(!ckMultiple)
    	{
    		visibleReg = false;
    	}

    	return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes()
    {
    	registro.getCampos().remove("NOMBRE_SOLICITANTE");
    	registro.getCampos().remove("NOMBRE_DEP");
    	registro.getCampos().remove("NOMBRE_CAUSAL");
    	registro.getCampos().remove("NOMBRE_PROYECTO");
    	registro.getCampos().remove("CODIGO");
    	
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues()
    {
    	return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarAntes()
    {
    	return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarDespues()
    {
    	return true;
    }
    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
	public void ejecutarrcCerrar() {
		if (parametroswf != null) {
			Map<String,Object> parametros = new TreeMap<>();
			parametros.put("PR_ROWKEY",parametroswf.get("PR_ROWKEY"));
	
			Direccionador direccionador = new Direccionador();
			direccionador.setNumForm(Integer.toString(
					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo()));
	
			direccionador.setParametros(parametros);
			SessionUtil.redireccionarForma(direccionador,"35");
		} else {
			SessionUtil.redireccionar("/menu.sysman");
		}
	}
    /**
     * Retorna la variable nuevoNumero
     * 
     * @return  nuevoNumero
     */
    public String getNuevoNumero() 
    {
        return nuevoNumero;
    }
    /**
     * Asigna la variable  nuevoNumero
     * 
     * @param  nuevoNumero
     * Variable a asignar en  nuevoNumero
     */
    public void setNuevoNumero(String nuevoNumero) 
    {
        this.nuevoNumero = nuevoNumero;
    }
    /**
     * Retorna la variable ckMultiple
     * 
     * @return  ckMultiple
     */
    public boolean getCkMultiple() 
    {
        return ckMultiple;
    }
    /**
     * Asigna la variable  ckMultiple
     * 
     * @param  ckMultiple
     * Variable a asignar en  ckMultiple
     */
    public void setCkMultiple(boolean ckMultiple) 
    {
        this.ckMultiple = ckMultiple;
    }
    /**
     * Retorna la variable noRegistros
     * 
     * @return  noRegistros
     */
    public int getNoRegistros() 
    {
        return noRegistros;
    }
    /**
     * Asigna la variable  noRegistros
     * 
     * @param  noRegistros
     * Variable a asignar en  noRegistros
     */
    public void setNoRegistros(int noRegistros) 
    {
        this.noRegistros = noRegistros;
    }
    /**
     * Retorna la variable visibleReg
     * 
     * @return  visibleReg
     */    
    public boolean getVisibleReg() 
    {
        return visibleReg;
    }
    /**
     * Asigna la variable  visibleReg
     * 
     * @param  visibleReg
     * Variable a asignar en  visibleReg
     */
    public void setVisibleReg(boolean visibleReg) 
    {
        this.visibleReg = visibleReg;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() 
    {
        return archivoDescarga;
    }
    /**
     * Retorna la lista listasolicitante
     * 
     * @return listasolicitante
     */
    public RegistroDataModelImpl getListasolicitante() 
    {
        return listasolicitante;
    }
    /**
     * Asigna la lista listasolicitante
     * 
     * @param listasolicitante
     * Variable a asignar en  listasolicitante
     */
    public void setListasolicitante(RegistroDataModelImpl listasolicitante) 
    {
        this.listasolicitante = listasolicitante;
    }
    /**
     * Retorna la lista listadependencia
     * 
     * @return listadependencia
     */
    public RegistroDataModelImpl getListadependencia() 
    {
        return listadependencia;
    }
    /**
     * Asigna la lista listadependencia
     * 
     * @param listadependencia
     * Variable a asignar en  listadependencia
     */
    public void setListadependencia(RegistroDataModelImpl listadependencia) 
    {
        this.listadependencia = listadependencia;
    }
    /**
     * Retorna la lista listacausalInexistencia
     * 
     * @return listacausalInexistencia
     */
    public RegistroDataModelImpl getListacausalInexistencia() 
    {
        return listacausalInexistencia;
    }
    /**
     * Asigna la lista listacausalInexistencia
     * 
     * @param listacausalInexistencia
     * Variable a asignar en  listacausalInexistencia
     */
    public void setListacausalInexistencia(RegistroDataModelImpl listacausalInexistencia) 
    {
        this.listacausalInexistencia = listacausalInexistencia;
    }
    /**
     * Retorna la lista listaproyecto
     * 
     * @return listaproyecto
     */
    public RegistroDataModelImpl getListaproyecto() 
    {
        return listaproyecto;
    }
    /**
     * Asigna la lista listaproyecto
     * 
     * @param listaproyecto
     * Variable a asignar en  listaproyecto
     */
    public void setListaproyecto(RegistroDataModelImpl listaproyecto) 
    {
        this.listaproyecto = listaproyecto;
    }
    /**
     * Retorna la lista listaplantilla
     * 
     * @return listaplantilla
     */
    public RegistroDataModelImpl getListaplantilla() 
    {
        return listaplantilla;
    }
    /**
     * Asigna la lista listaplantilla
     * 
     * @param listaplantilla
     * Variable a asignar en  listaplantilla
     */
    public void setListaplantilla(RegistroDataModelImpl listaplantilla) 
    {
        this.listaplantilla = listaplantilla;
    }
}
