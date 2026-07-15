/*-
 * FrmmonitortramitesControlador.java
 *
 * 1.0
 * 
 * 27/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;
import com.sysman.workflow.enums.FrmmonitortramitesControladorEnum;
import com.sysman.workflow.enums.FrmmonitortramitesControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.transaction.SystemException;

import org.primefaces.event.RowEditEvent;

/**
 * Controlador que permite la visualización de trámites con
 * información detallada de cada uno.
 *
 * @version 1.0, 27/04/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class FrmmonitortramitesControlador extends BeanBaseContinuoAcmeImpl {

    // <DECLARAR_ATRIBUTOS>

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /** Constante a nivel de clase que aloja la cadena: COMPANIA */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuarioInterno = SessionUtil.getUser().getCodigo();

    /**
     * Constante a nivel de clase que aloja la cadena: USUARIO_INTERNO
     */
    private final String cUsuarioInterno = FrmmonitortramitesControladorEnum.USUARIO_INTERNO
                    .getValue();

    /** Constante a nivel de clase que aloja la cadena: PROCESOS */
    private final String cProcesos = FrmmonitortramitesControladorEnum.PROCESOS
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena: TIPO_TRAMITE
     */
    private final String cTipoTramite = GeneralParameterEnum.TIPO_TRAMITE
                    .getName();

    /** Constante a nivel de clase que aloja la cadena: NUMERO */
    private final String cNumero = GeneralParameterEnum.NUMERO.getName();
    
    /**
	 * registro del nivel de usuario
	 */
    private Registro nivelUsuario;

      
    private boolean mostrarNombreTercero;
    private boolean mostrarTercero;
    private boolean mostrarContr;
     
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmmonitortramitesControlador
     */
    public FrmmonitortramitesControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {

            numFormulario = GeneralCodigoFormaEnum.FRMMONITORTRAMITES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
        tabla = GenericUrlEnum.TRAMITES.getTable();
        registro = new Registro();
        reasignarOrigen();
        buscarLlave();

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        
        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cUsuarioInterno, usuarioInterno);
        
        if(obtenerParametro("CARGAR MONITOR TRAMITES FLORENCIA", "NO").equals("SI")) {
        	
        	urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                    FrmmonitortramitesControladorUrlEnum.URL001.getValue());
        	
        }else {
        	
        	urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                    FrmmonitortramitesControladorUrlEnum.URL003.getValue());
        	
        }
     

 }

    // <METODOS_CARGAR_LISTA>R
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Ver
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirVer(Registro reg, int indice) {
        Registro registroTramites = new Registro();
        Direccionador direccionador = new Direccionador();

        Map<String, Object> param = new TreeMap<>();

        registroTramites.getCampos()
                        .put(FrmmonitortramitesControladorEnum.KEY_COMPANIA
                                        .getValue(), compania);
        registroTramites.getCampos()
                        .put(FrmmonitortramitesControladorEnum.KEY_PROCESOS
                                        .getValue(),
                                        reg.getCampos().get(cProcesos));
        registroTramites.getCampos()
                        .put(FrmmonitortramitesControladorEnum.KEY_TIPO_TRAMITE
                                        .getValue(),
                                        reg.getCampos().get(cTipoTramite));
        registroTramites.getCampos()
                        .put(FrmmonitortramitesControladorEnum.KEY_NUMERO
                                        .getValue(),
                                        reg.getCampos().get(cNumero));
        param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(),
                        registroTramites.getCampos());

        param.put(FrmTramitesControladorEnum.PR_VER_DESDE_MONITOR.getValue(),
                        true);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
                                        .getCodigo()));

        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

    }

    public void oprimirIndicador(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Tramitar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirTramitar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	mostrarContr = obtenerParametro("MOSTRAR CONTRATOS MONITOR TRAMITES", "NO").equals("SI");
    	mostrarNombreTercero = obtenerParametro("MOSTRAR PROCEDENCIA MONITOR TRAMITES", "NO").equals("SI");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
    		String valorDefault)
    {
    	String parametro = null;
    	try {
    		parametro = ejbSysmanUtil.consultarParametro(compania,
    				nombreParametro, SessionUtil.getModulo(),
    				new Date(), true);
    	} catch (com.sysman.exception.SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    	return parametro != null ? parametro : valorDefault;
    }
    
    public String nivelU() {
    		Map<String, Object> param = new HashMap<>();
    		param.put(cUsuarioInterno, usuarioInterno);
    		String nivel="9";

    		try {
    			nivelUsuario = RegistroConverter
    					.toRegistro(requestManager.get(
    							UrlServiceUtil.getInstance()
    							.getUrlServiceByUrlByEnumID(
    									FrmmonitortramitesControladorUrlEnum.URL002.getValue())
    							.getUrl(),
    							param));

    			if (nivelUsuario != null) {
    				nivel = nivelUsuario.getCampos().get("NIVEL_USUARIO").toString();
    			}
    			else {
    				nivel="9";
    				}
    	
    		} catch (Exception e) {
    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
			}  
    		if(nivel=="true") {
    			nivel="1";
    		}
    		return nivel;
    	}
    

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

	public Registro getNivelUsuario() {
		return nivelUsuario;
	}

	public void setNivelUsuario(Registro nivelUsuario) {
		this.nivelUsuario = nivelUsuario;
	}

	//XP251
	/**
	 * @return the mostrarNombreTercero
	 */
	public boolean ismostrarNombreTercero() {
		return mostrarNombreTercero;
	}

	/**
	 * @param mostrarNombreTercero the mostrarNombreTercero to set
	 */
	public void setmostrarNombreTercero(boolean mostrarNombreTercero) {
		this.mostrarNombreTercero =mostrarNombreTercero;
	}

	/**
	 * @return the mostrarTercero
	 */
	public boolean isMostrarTercero() {
		return mostrarTercero;
	}

	/**
	 * @param mostrarTercero the mostrarTercero to set
	 */
	public void setMostrarTercero(boolean mostrarTercero) {
		this.mostrarTercero = mostrarTercero;
	}

	/**
	 * @return the mostrarContr
	 */
	public boolean isMostrarContr() {
		return mostrarContr;
	}

	/**
	 * @param mostrarContr the mostrarContr to set
	 */
	public void setMostrarContr(boolean mostrarContr) {
		this.mostrarContr = mostrarContr;
	}

	
	
	

    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
	

	
	
}
