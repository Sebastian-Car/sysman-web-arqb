/*-
 * PlandesarrollotrazControlador.java
 *
 * 1.0
 * 
 * 31/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.PlandesarrollotrazControladorEnum;
import com.sysman.plandesarrollo.enums.PlandesarrollotrazControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 31/07/2025
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class PlandesarrollotrazControlador extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    private String nombreDependencia;
    private boolean visibleTrazadores;
    private boolean bloqueadoTipoMeta;
    private boolean bloqueadoMetaIndicador;
    private boolean bloqueadoUnidadMedida;
    private boolean bloqueadoDependencia;
    private final String vigenciaFinalCons;
    private final String dependenciaCons;
    private final String tipoMetaPlanCons;
    private String vigencia;
    private String digAccion;
    private boolean esMeta;
    
    private List<Registro> listaVigencia;
    private List<Registro> listaVigenciaF;
    private List<Registro> listaUnidad;
    private List<Registro> listaSector;
    private RegistroDataModelImpl listaDependencia;

    /**
     * Crea una nueva instancia de PlandesarrollotrazControlador
     */
	public PlandesarrollotrazControlador() {
		super();
		vigenciaFinalCons = "VIGENCIA_FINAL";
		compania = SessionUtil.getCompania();
		dependenciaCons = GeneralParameterEnum.DEPENDENCIA.getName();
        tipoMetaPlanCons = PlandesarrollotrazControladorEnum.TIPO_META_PLAN.getValue();
        
        try {
        	numFormulario = GeneralCodigoFormaEnum.PLANDESARROLLOTRAZ_CONTROLADOR.getCodigo();
        	validarPermisos();
            registro = new Registro(new HashMap<String,Object>());
            bloqueadoTipoMeta = true;
            bloqueadoMetaIndicador = true;
            bloqueadoUnidadMedida = true;
            bloqueadoDependencia = true;
            Map<String,Object> parametros = SessionUtil.getFlash();
            if(parametros != null) {
                vigencia = parametros.get(PlandesarrollotrazControladorEnum.VIGENCIA_LOWER.getValue()).toString();
                rid = (HashMap<String,Object>) parametros.get(PlandesarrollotrazControladorEnum.RID_LOWER.getValue());
                digAccion = parametros.get("digAccion").toString();
            }
		 } catch(Exception ex) {
			 Logger.getLogger(PlandesarrollotrazControlador.class.getName())
			 				 .log(Level.SEVERE,null,ex);
			 SessionUtil.redireccionarMenuPermisos();
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	@Override
	public void iniciarListas() {
		cargarListaDependencia();
		cargarListaVigencia();
		cargarListaVigenciaF();
		cargarListaUnidad();
		cargarListaSector();
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
    public void inicializar() {
    	tabla = GenericUrlEnum.BP_PLAN_INDICATIVO.getTable();
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
    	parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
    	parametrosListado.put(PlandesarrollotrazControladorEnum.ACCION.getValue(),digAccion);
    	
    	urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                		PlandesarrollotrazControladorUrlEnum.URL55200R.getValue());
    	
    	urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
    					PlandesarrollotrazControladorUrlEnum.URL552060.getValue());
    	
    	urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
    					PlandesarrollotrazControladorUrlEnum.URL55200C.getValue());

		urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
						PlandesarrollotrazControladorUrlEnum.URL55200U.getValue());
		
		urlEliminacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
						PlandesarrollotrazControladorUrlEnum.URL55200D.getValue());
    }	
    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
            		requestManager.getList(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL4001.getValue())
            				.getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Carga la lista listaVigenciaF
     *
     */
    public void cargarListaVigenciaF() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        try {
            listaVigenciaF = RegistroConverter.toListRegistro(
            		requestManager.getList(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL4001.getValue())
            				.getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Carga la lista listaUnidad
     *
     */
    public void cargarListaUnidad() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        try {
            listaUnidad = RegistroConverter.toListRegistro(
            		requestManager.getList(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL553001.getValue())
            				.getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Carga la lista listaSector
     *
     */
    public void cargarListaSector() {
    	Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        
        try {
            listaSector = RegistroConverter.toListRegistro(
            		requestManager.getList(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL203001.getValue())
            				.getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }	
    }
    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {
    	Map<String,Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance().
    			getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL62053.getValue());

    	listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(),param,
                true,GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * Metodo ejecutado al cambiar el control VigenciaF
     * 
     * 
     */
    public void cambiarVigenciaF() {
    	if(registro.getCampos().get(vigenciaFinalCons) != null) {
    		int fechaIni = Integer.parseInt(registro.getCampos().
    				get(GeneralParameterEnum.VIGENCIA_INICIAL.getName()).toString());
            int fechaFin = Integer.parseInt(registro.getCampos().get(vigenciaFinalCons).toString());
            
            if(fechaFin < fechaIni) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3735"));
                registro.getCampos().put(vigenciaFinalCons,null);
            }
        }
    }
    /**
     * Metodo ejecutado al cambiar el control Codigo
     * 
     * 
     */
    public void cambiarCodigo() {
    	if(existePlan()) {
            String id = registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString();
            
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2302").replace("#$id#$",id)
            		.replace("#$vigencia#$",vigencia));
            
            registro.getCampos().put(GeneralParameterEnum.ID.getName(),null);
            return;
        }
        verificarNivel();
    }
    /**
     * Verifica en la base de datos si ya existe un plan con el
     * codigo ingresado.
     *
     * @return true si hay registros, false si no se encontraron
     * registros
     */
    private boolean existePlan() {
        String id = registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString();
        boolean rta;
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
        param.put(GeneralParameterEnum.ID.getName(),id);

        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
            		requestManager.get(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL552001.getValue())
            				.getUrl(),param));
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if(reg != null) {
            rta = true;
        } else {
            rta = false;
        }
        return rta;
    }
    
    private String retornarString(Registro reg,String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(),campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
    
    /**
     * Verifica que el nivel ingresado est� configurado en el plan
     * indicativo. Configura los controles seg�n el tipo de meta.
     */
    public void verificarNivel() {
        try {
            String id = retornarString(registro,GeneralParameterEnum.ID.getName());
            if(id == null) {
                return;
            }
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
            param.put(GeneralParameterEnum.ID.getName(),id);

            Registro reg = RegistroConverter.toRegistro(
            		requestManager.get(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL554001.getValue())
            				.getUrl(),param));

            if(reg == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2296").replace(
                		"#$tamanio#$",String.valueOf(id.length())));
            } else {
                // Verifica si el nivel maneja meta
                if(Boolean.parseBoolean(reg.getCampos().get(
                		PlandesarrollotrazControladorEnum.META_RESUL.getValue()).toString())) {
                    registro.getCampos().put(tipoMetaPlanCons,"001");
                    bloqueadoTipoMeta = true;
                    bloqueadoMetaIndicador = false;
                    bloqueadoUnidadMedida = false;
                    esMeta = true;
                } else if(Boolean.parseBoolean(reg.getCampos().get(
                		PlandesarrollotrazControladorEnum.META_PRODUC.getValue()).toString())) {
                    registro.getCampos().put(tipoMetaPlanCons,"002");
                    bloqueadoTipoMeta = true;
                    bloqueadoMetaIndicador = false;
                    bloqueadoUnidadMedida = false;
                    esMeta = true;
                } else {
                    registro.getCampos().put(tipoMetaPlanCons,null);
                    bloqueadoTipoMeta = true;
                    bloqueadoMetaIndicador = true;
                    bloqueadoUnidadMedida = true;
                    esMeta = false;
                }
                // Verifica si tiene configurada la dependencia
                bloqueadoDependencia = !Boolean.parseBoolean(reg.getCampos().get(
                		PlandesarrollotrazControladorEnum.MANEJA_DEPEN.getValue()).toString());
            }
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaDependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(dependenciaCons,registroAux.getCampos()
        		.get(GeneralParameterEnum.CODIGO.getName()));
        cargarNombreDependencia(retornarString(registro,dependenciaCons));
	}
	/**
     * Carga el nombre de la dependencia
     *
     * @param codigo
     * codigo de la dependencia
     */
    public void cargarNombreDependencia(String codigo) {
        try {
            if(codigo == null) {
                return;
            }
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),codigo);

            Registro reg = RegistroConverter.toRegistro(
            		requestManager.get(UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL62052.getValue())
            				.getUrl(),param));
            if(reg != null) {
                nombreDependencia = retornarString(reg,GeneralParameterEnum.NOMBRE.getName());
            }
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton OrdenNacional
     * en la vista
     *
     *
     */
    public void oprimirOrdenNacional() {
    	String[] campos = { "vigencia","codPlan" };
        String[] valores = { vigencia,registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString() };
        
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.BPORDENNACIONAL_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),campos,valores);
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton OrdenDepartamental
     * en la vista
     *
     *
     */
    public void oprimirOrdenDepartamental() {
    	String[] campos = { "vigencia","codPlan" };
        String[] valores = { vigencia,registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString() };
        
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.BPORDENDEPARTAMENTAL_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),campos,valores);
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton OrdenMunicipal
     * en la vista
     *
     *
     */
    public void oprimirOrdenMunicipal() {
    	String[] campos = { "vigencia","codPlan" };
        String[] valores = { vigencia,registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString() };
        
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.BPORDENMUNICIPAL_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),campos,valores);
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
     * 
     */
    @Override
    public void cargarRegistro() {
    	precargarRegistro();
    	
    	if(css != null) {
    		verificarNivel();
            validarFisicoFinanciero();
            validaTrazadores();
        } else {
            seleccionarOpcionI();
            visibleTrazadores = false;
        }
        cargarNombreDependencia(retornarString(registro,dependenciaCons));
    }
    
    private void validarFisicoFinanciero() {
        if(SysmanFunciones.validarCampoVacio(registro.getCampos(),
        		PlandesarrollotrazControladorEnum.AVANCE.getValue())) {
            registro.getCampos().put(PlandesarrollotrazControladorEnum.AVANCE.getValue(),0);
        }
        if(SysmanFunciones.validarCampoVacio(registro.getCampos(),
        		PlandesarrollotrazControladorEnum.AVANCE_FINANCIERO.getValue())) {
            registro.getCampos().put(PlandesarrollotrazControladorEnum.AVANCE_FINANCIERO.getValue(),0);
        }
    }
    
    public void seleccionarOpcionI() {
        registro.getCampos().put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),vigencia);

        bloqueadoTipoMeta = true;
        bloqueadoMetaIndicador = false;
        bloqueadoUnidadMedida = false;
        bloqueadoDependencia = false;
        nombreDependencia = "";
        inicializarCampos();
    }
    /**
     * Inicializa los campos que tienen valores predeterminados.
     */
    private void inicializarCampos() {
        registro.getCampos().put(PlandesarrollotrazControladorEnum.PONDERACION.getValue(),0);
        registro.getCampos().put(PlandesarrollotrazControladorEnum.AVANCE.getValue(),
        		new BigDecimal(BigInteger.ZERO));
        registro.getCampos().put(PlandesarrollotrazControladorEnum.AVANCE_FINANCIERO.getValue(),
                new BigDecimal(BigInteger.ZERO));
        registro.getCampos().put(PlandesarrollotrazControladorEnum.UNIDAD_MEDIDA.getValue(),
                PlandesarrollotrazControladorEnum.NO.getValue());
        registro.getCampos().put(PlandesarrollotrazControladorEnum.META.getValue(),0);
        registro.getCampos().put(PlandesarrollotrazControladorEnum.LB.getValue(),0);
    }
    
    private void validaTrazadores() {
    	int digitos = 0;
    	String id = registro.getCampos().get(GeneralParameterEnum.ID.getName()).toString();
    	try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(),vigencia);
            
			Registro digTrazador = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(PlandesarrollotrazControladorUrlEnum.URL554025.getValue())
							.getUrl(),param));
			if(digTrazador != null) {
				digitos = (int) digTrazador.getCampos().get("DIGITOS");
			}
		} catch(SystemException e) {
			e.printStackTrace();
		}
    	
    	int digId = id.length();
		if(digitos == digId) {
    		visibleTrazadores = true;
    	} else {
    		visibleTrazadores = false;
    	}
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes() {
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
		boolean respuesta = true;
		if(esMeta) {
		    String tipoIndicador = registro.getCampos().get(
		    		PlandesarrollotrazControladorEnum.TIPO_META_INDICADOR.getValue()).toString();
		    
		    if(tipoIndicador == null) {
		        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2298"));
		        respuesta = false;
		    }
		    String dependencia = registro.getCampos().get(dependenciaCons).toString();
		    if("".equals(dependencia)) {
		        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2297"));
		        respuesta = false;
		    }
		}
		return respuesta;
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
     * Retorna la variable nombreDependencia
     * 
     * @return  nombreDependencia
     */
    public String getNombreDependencia() {
        return nombreDependencia;
    }
    /**
     * Asigna la variable  nombreDependencia
     * 
     * @param  nombreDependencia
     * Variable a asignar en  nombreDependencia
     */
    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }
    /**
     * Retorna la variable visibleTrazadores
     * 
     * @return  visibleTrazadores
     */
    public boolean isVisibleTrazadores() {
        return visibleTrazadores;
    }
    /**
     * Asigna la variable  visibleTrazadores
     * 
     * @param  visibleTrazadores
     * Variable a asignar en  visibleTrazadores
     */
    public void setVisibleTrazadores(boolean visibleTrazadores) {
        this.visibleTrazadores = visibleTrazadores;
    }
    /**
     * Retorna la variable bloqueadoTipoMeta
     * 
     * @return  bloqueadoTipoMeta
     */
    public boolean isBloqueadoTipoMeta() {
        return bloqueadoTipoMeta;
    }
    /**
     * Asigna la variable  bloqueadoTipoMeta
     * 
     * @param  bloqueadoTipoMeta
     * Variable a asignar en  bloqueadoTipoMeta
     */
    public void setBloqueadoTipoMeta(boolean bloqueadoTipoMeta) {
        this.bloqueadoTipoMeta = bloqueadoTipoMeta;
    }
    /**
     * Retorna la variable bloqueadoMetaIndicador
     * 
     * @return  bloqueadoMetaIndicador
     */
    public boolean isBloqueadoMetaIndicador() {
        return bloqueadoMetaIndicador;
    }
    /**
     * Asigna la variable  bloqueadoMetaIndicador
     * 
     * @param  bloqueadoMetaIndicador
     * Variable a asignar en  bloqueadoMetaIndicador
     */
    public void setBloqueadoMetaIndicador(boolean bloqueadoMetaIndicador) {
        this.bloqueadoMetaIndicador = bloqueadoMetaIndicador;
    }
    /**
     * Retorna la variable bloqueadoUnidadMedida
     * 
     * @return  bloqueadoUnidadMedida
     */
    public boolean isBloqueadoUnidadMedida() {
        return bloqueadoUnidadMedida;
    }
    /**
     * Asigna la variable  bloqueadoUnidadMedida
     * 
     * @param  bloqueadoUnidadMedida
     * Variable a asignar en  bloqueadoUnidadMedida
     */
    public void setBloqueadoUnidadMedida(boolean bloqueadoUnidadMedida) {
        this.bloqueadoUnidadMedida = bloqueadoUnidadMedida;
    }
    /**
     * Retorna la variable bloqueadoDependencia
     * 
     * @return  bloqueadoDependencia
     */
    public boolean isBloqueadoDependencia() {
        return bloqueadoDependencia;
    }
    /**
     * Asigna la variable  bloqueadoDependencia
     * 
     * @param  bloqueadoDependencia
     * Variable a asignar en  bloqueadoDependencia
     */
    public void setBloqueadoDependencia(boolean bloqueadoDependencia) {
        this.bloqueadoDependencia = bloqueadoDependencia;
    }
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }
    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en  listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }
    /**
     * Retorna la lista listaVigenciaF
     * 
     * @return listaVigenciaF
     */
    public List<Registro> getListaVigenciaF() {
        return listaVigenciaF;
    }
    /**
     * Asigna la lista listaVigenciaF
     * 
     * @param listaVigenciaF
     * Variable a asignar en  listaVigenciaF
     */
    public void setListaVigenciaF(List<Registro> listaVigenciaF) {
        this.listaVigenciaF = listaVigenciaF;
    }
    /**
     * Retorna la lista listaUnidad
     * 
     * @return listaUnidad
     */
    public List<Registro> getListaUnidad() {
        return listaUnidad;
    }
    /**
     * Asigna la lista listaUnidad
     * 
     * @param listaUnidad
     * Variable a asignar en  listaUnidad
     */
    public void setListaUnidad(List<Registro> listaUnidad) {
        this.listaUnidad = listaUnidad;
    }
    /**
     * Retorna la lista listaSector
     * 
     * @return listaSector
     */
    public List<Registro> getListaSector() {
        return listaSector;
    }
    /**
     * Asigna la lista listaSector
     * 
     * @param listaSector
     * Variable a asignar en  listaSector
     */
    public void setListaSector(List<Registro> listaSector) {
        this.listaSector = listaSector;
    }	
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }
    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en  listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }
}
