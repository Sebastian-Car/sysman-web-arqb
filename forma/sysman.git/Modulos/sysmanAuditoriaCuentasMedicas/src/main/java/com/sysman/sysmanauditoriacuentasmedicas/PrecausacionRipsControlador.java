/*-
 * PrecausacionRipsControlador.java
 *
 * 1.0
 * 
 * 28/01/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.PrecausacionRipsControladorUrlEnum;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite filtrar los rips por consecutivo
 *
 * @version 1.0, 27/01/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class PrecausacionRipsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    /**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
    
    /**
	 * Atributo que almacena la lista de auditores extraidos desde una
	 * consulta en la base de datos
	 */
	private RegistroDataModelImpl listaAuditor;

	/**
	 * Atributo auxiliar que almacena la lista de auditores extraidos
	 * desde una consulta en la base de datos
	 */
	private RegistroDataModelImpl listaAuditorE;
    
    /**
	 * Variable que controla si el formulario ver la columna auditor
	 */
	private boolean muestraAuditor;
	
	/**
	 * Esta variable se usa para almacenar los estado dependiendo del 
	 * menu que ya este controlador
	 */
	private String estadoRips;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PrecausacionRipsControlador
     */
    public PrecausacionRipsControlador() {
        super();
        compania = SessionUtil.getCompania();
        
        if ("840203".equals(SessionUtil.getMenuActual())) 
        {
        	estadoRips = ",C,T,";
        	muestraAuditor = false;
        }
        else if ("840204".equals(SessionUtil.getMenuActual())) {

        	estadoRips = ",T,";
        	muestraAuditor = true;
        }
        try {

            // 2153
            numFormulario = GeneralCodigoFormaEnum.PRECAUSACION_RIPS_CONTROLADOR
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
        tabla = GenericUrlEnum.CM_IMPORTAR_RIPS.getTable();
        reasignarOrigen();
        cargarListaAuditor();
        cargarListaAuditorE();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        
        parametrosListado.put(GeneralParameterEnum.ESTADO.getName(),
        		estadoRips);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrecausacionRipsControladorUrlEnum.URL4391
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		PrecausacionRipsControladorUrlEnum.URL4393
                                                .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    
    /**
     * 
     * Carga la lista listaAuditor
     *
     */ 
	public void cargarListaAuditor()
	{	
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		PrecausacionRipsControladorUrlEnum.URL4392
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaAuditor = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                "AUDITOR");
	}
	
	/**
     * 
     * Carga la lista listaAuditorE
     *
     */ 
	public void cargarListaAuditorE()
	{			
		setListaAuditorE(listaAuditor);
	}
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirAceptar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();

        param.put("consecutivo", reg.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName()));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        if ("840203".equals(SessionUtil.getMenuActual())) {

        	direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FRM_CAUSAR_RIPS
                                            .getCodigo()));
        }
        else if ("840204".equals(SessionUtil.getMenuActual())) {

        	direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FRM_AUDITORIA_GLOSAS
                                            .getCodigo()));
        }

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    
    /**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuditor
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuditor(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registro.getCampos().put("AUDITOR",
				registroAux.getCampos().get("AUDITOR"));
		registro.getCampos().put("NOMBRE_AUDITOR",
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuditorE
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuditorE(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		
		auxiliar = registroAux.getCampos().get("AUDITOR").toString();
		
		registro.getCampos().put("AUDITOR",
				registroAux.getCampos().get("AUDITOR"));
		registro.getCampos().put("NOMBRE_AUDITOR",
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));

	}
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() 
    {
    	 if ("840203".equals(SessionUtil.getMenuActual())) 
         {
    		 permisos[2] = false;
         }
         else if ("840204".equals(SessionUtil.getMenuActual())) {

        	 permisos[2] = true;
         }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
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
     */
    @Override
    public boolean actualizarAntes() 
    {
    	registro.getCampos().remove("COD_PREST_SERV_SALUD");
    	registro.getCampos().remove("COMPANIA");
    	registro.getCampos().remove("CONSECUTIVO");
    	registro.getCampos().remove("VALOR_PAGAR");
    	registro.getCampos().remove("FECHA");
    	registro.getCampos().remove("NOMBRE_RAZON_SOCIAL");
    	registro.getCampos().remove("NIT");
    	registro.getCampos().remove("NOMBRE_AUDITOR");
    	registro.getCampos().remove("SUCURSAL");
    	registro.getCampos().remove("RADICADO");
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
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
    public void removerCombos() 
    {
    	registro.getCampos().remove("NOMBRE_AUDITOR");
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
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the listaAuditores
	 */
	public RegistroDataModelImpl getListaAuditor() {
		return listaAuditor;
	}

	/**
	 * @param listaAuditores the listaAuditores to set
	 */
	public void setListaAuditores(RegistroDataModelImpl listaAuditor) {
		this.listaAuditor = listaAuditor;
	}

	/**
	 * @return the muestraAuditor
	 */
	public boolean isMuestraAuditor() {
		return muestraAuditor;
	}

	/**
	 * @param muestraAuditor the muestraAuditor to set
	 */
	public void setMuestraAuditor(boolean muestraAuditor) {
		this.muestraAuditor = muestraAuditor;
	}

	/**
	 * @return the listaAuditorE
	 */
	public RegistroDataModelImpl getListaAuditorE() {
		return listaAuditorE;
	}

	/**
	 * @param listaAuditorE the listaAuditorE to set
	 */
	public void setListaAuditorE(RegistroDataModelImpl listaAuditorE) {
		this.listaAuditorE = listaAuditorE;
	}

	/**
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * @return the estadoRips
	 */
	public String getEstadoRips() {
		return estadoRips;
	}

	/**
	 * @param estadoRips the estadoRips to set
	 */
	public void setEstadoRips(String estadoRips) {
		this.estadoRips = estadoRips;
	}
}
