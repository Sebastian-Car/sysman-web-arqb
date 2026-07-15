/*-
 * NatSubActividadesControlador.java
 *
 * 1.0
 * 
 * 15/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatSubActividadesControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite realizar el registro de las actividades en
 * una hoja de vida.
 *
 * @version 1.0, 06/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */

@ManagedBean
@ViewScoped
public class NatSubActividadesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que contiene el valor de AC_CONSECUTIVO
     */
    private final String consConsecutivo;
    /**
     * Atributo que contiene el valor del documento del empleado el
     * cual se asigna por parametro
     */
    private String documento;
    /**
     * Atributo que contiene el valor de la sucursal del empleado el
     * cual se asigna por parametro
     */
    private String sucursal;
    
    private Date fechaI;
    
    private Date fechaF;
    
    private String retorno;
    /**
     * Atributo que contiene los diversos parametros de entrada para
     * el filtro de la informacion
     */
    private Map<String, Object> parametrosEntrada;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NatSubActividadesControlador
     */
    public NatSubActividadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        consConsecutivo = "AC_CONSECUTIVO";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUBACTIVIDADES_CONTROLADOR
                            .getCodigo();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                documento = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
            }
            fechaI = null;
            fechaF = null;
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
        enumBase = GenericUrlEnum.NAT_ACTIVIDADES;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
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
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
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
        // </CODIGO_DESARROLLADO>
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
        try {
            long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "NAT_ACTIVIDADES",
                            " NAT_ACTIVIDADES.COMPANIA =''" + compania
                                + "'' AND NAT_ACTIVIDADES.DP_NUMEDOCU =''"
                                + documento
                                + "'' AND NAT_ACTIVIDADES.SUCURSAL =''"
                                + sucursal
                                + "''",
                            consConsecutivo);
            registro.getCampos().put(consConsecutivo, consecutivo);
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                            documento);
        }
        catch (SystemException e) {
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
     * 
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
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", documento);
        parametros.put("sucursal", sucursal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(consConsecutivo);
        registro.getCampos().remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
    }

    public void cambiarFechaInicioC(int rowNum) {
    	
    	//<CODIGO_DESARROLLADO>
    	
    	fechaI = (Date)listaInicial.getDatasource().get(rowNum % 10).getCampos().get(NatSubActividadesControladorEnum.PARAM0.getValue());
    	fechaF = (Date)listaInicial.getDatasource().get(rowNum % 10).getCampos().get(NatSubActividadesControladorEnum.PARAM1.getValue());
    	retorno = validarFechas(fechaI, fechaF);
    	listaInicial.getDatasource().get(rowNum % 10).getCampos().put(NatSubActividadesControladorEnum.PARAM2.getValue(), retorno.substring(0, 2));
    	listaInicial.getDatasource().get(rowNum % 10).getCampos().put(NatSubActividadesControladorEnum.PARAM3.getValue(), retorno.substring(3, 5));
    	

        //</CODIGO_DESARROLLADO>
    }
    
    public void cambiarFechaFinC(int rowNum) {
    	
    	//<CODIGO_DESARROLLADO>
    	
    	fechaI = (Date)listaInicial.getDatasource().get(rowNum % 10).getCampos().get(NatSubActividadesControladorEnum.PARAM0.getValue());
    	fechaF = (Date)listaInicial.getDatasource().get(rowNum % 10).getCampos().get(NatSubActividadesControladorEnum.PARAM1.getValue());
    	retorno = validarFechas(fechaI, fechaF);
    	listaInicial.getDatasource().get(rowNum % 10).getCampos().put(NatSubActividadesControladorEnum.PARAM2.getValue(), retorno.substring(0, 2));
    	listaInicial.getDatasource().get(rowNum % 10).getCampos().put(NatSubActividadesControladorEnum.PARAM3.getValue(), retorno.substring(3, 5));
    	
        //</CODIGO_DESARROLLADO>
    }
    
    public String validarFechas(Object fechaInicio, Object fechaFin) {
    	String fecha = null;
    	try {
    		if ((fechaInicio != null) && (fechaFin != null)) {
    			fecha = ejbSysmanUtil.calcularDiferenciaEntreFechas((Date)fechaInicio, 
    																(Date) fechaFin, 1, 0);
    		}
			
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    	return fecha;
    }
    
    public void cambiarFechaInicio() {
    	//<CODIGO DESARROLLADO>
    	retorno = validarFechas((Date)registro.getCampos().get(NatSubActividadesControladorEnum.PARAM0.getValue()), (Date)registro.getCampos().get(NatSubActividadesControladorEnum.PARAM1.getValue()));
    	if (retorno != null) {
    		registro.getCampos().put(NatSubActividadesControladorEnum.PARAM2.getValue(), retorno.substring(0, 2));
        	registro.getCampos().put(NatSubActividadesControladorEnum.PARAM3.getValue(), retorno.substring(3, 5));
    	}
    	//</CODIGO DESARROLLADO>
    }
    
    public void cambiarFechaFin() {
    	//<CODIGO DESARROLLADO>
    	registro.getCampos();
    	retorno = validarFechas((Date)registro.getCampos().get(NatSubActividadesControladorEnum.PARAM0.getValue()), (Date)registro.getCampos().get(NatSubActividadesControladorEnum.PARAM1.getValue()));
    	if (retorno != null) {
    		registro.getCampos().put(NatSubActividadesControladorEnum.PARAM2.getValue(), retorno.substring(0, 2));
        	registro.getCampos().put(NatSubActividadesControladorEnum.PARAM3.getValue(), retorno.substring(3, 5));
    	}
    	//</CODIGO DESARROLLADO>
    }
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
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
