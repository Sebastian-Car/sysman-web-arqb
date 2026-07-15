/*-
 * NatHistoriaClinicasControlador.java
 *
 * 1.0
 * 
 * 29/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
import com.sysman.hojasdevida.enums.NatHistoriaClinicaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que permite administrar la historia clinica de los
 * empleados.
 *
 * @version 1.0, 29/12/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class NatHistoriaClinicaControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    private final String consNombre;
    /**
     * Atributo que contiene el valor asignado al nombre del 
     * empleado
     */
    private String nombreEmpleado;
    /**
     * Atributo que contiene el valor asignado al numero de documento del empleado
     */
    private String numeroDoc;
    /**
     * Atributo que contiene el valor asignado a la sucursal del empleado
     */
    private String sucursal;
    /**
     * Atributo que contiene el valor asignado para bloquear el campo de empleado
     */
    private boolean bloqueadoEmpl;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del empleado. */
    private RegistroDataModelImpl listaEmpleado;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de NatHistoriaClinicasControlador
     */
    public NatHistoriaClinicaControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNombre="NOMBRECOMPLETO";
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_HISTORIACLINICA_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaEmpleado();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.NAT_HISTORIA_CLINICA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaEmpleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatHistoriaClinicaControladorUrlEnum.URL4529
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_DE_EMPLEADO",
                        registroAux.getCampos().get("ID_DE_EMPLEADO"));
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                registroAux.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()));
        nombreEmpleado= registroAux.getCampos().get("NOMBRE").toString();
        numeroDoc = registroAux.getCampos().get(GeneralParameterEnum.NUMERO_DCTO.getName()).toString();
        sucursal =registroAux.getCampos().get("SUCURSAL").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	bloqueadoEmpl=false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		nombreEmpleado = registro.getCampos().get(consNombre) != null
				? registro.getCampos().get(consNombre).toString() : null;
		bloqueadoEmpl = accion.equals(ACCION_INSERTAR) ? false : true;
		// </CODIGO_DESARROLLADO>
	}

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		try {
		    registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
	                            ejbSysmanUtil.generarSiguienteConsecutivo(
	                                            GenericUrlEnum.NAT_HISTORIA_CLINICA
	                                                            .getTable(),
	                                            " COMPANIA= ''" + compania
	                                                + "'' AND NUMERO_DCTO=''"
	                                                + numeroDoc
	                                                + "'' AND SUCURSAL=''"
	                                                + sucursal
	                                                + "'' AND FECHA_EXAMEN= ''"
	                                                + SysmanFunciones
	                                                                .convertirAFechaCadena(
	                                                                                (Date) registro.getCampos()
	                                                                                                .get("FECHA_EXAMEN"))
	                                                + "''",

	                                            "CONSECUTIVO"));
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(), numeroDoc);
			registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
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
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
    	if(accion.equals(ACCION_MODIFICAR)){
    		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    		registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
    		registro.getCampos().remove(consNombre);
    		
    	}
        // </CODIGO_DESARROLLADO>
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
    // <SET_GET_ATRIBUTOS>
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEmpleado
     * 
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }
   

    /**
     * Asigna la lista listaEmpleado
     * 
     * @param listaEmpleado
     * Variable a asignar en  listaEmpleado
     */
    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

	public boolean isBloqueadoEmpl() {
		return bloqueadoEmpl;
	}

	public void setBloqueadoEmpl(boolean bloqueadoEmpl) {
		this.bloqueadoEmpl = bloqueadoEmpl;
	}
    
    // </SET_GET_ADICIONALES>
}
