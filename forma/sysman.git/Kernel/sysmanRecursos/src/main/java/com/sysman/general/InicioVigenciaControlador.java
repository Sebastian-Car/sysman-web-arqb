/*-
 * InicioVigenciaControlador.java
 *
 * 1.0
 * 
 * 27/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.InicioVigenciaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Formulario que permite ejecutar el inicio de vigencias
 *
 * @version 1.0, 27/02/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class InicioVigenciaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indicador de seleccion de contabilidad
     */
    private boolean contabilidad;
    /**
     * Indicador de seleccion de presupuesto
     */
    private boolean presupuesto;
    /**
     * Indicador de seleccion de facturacion general
     */
    private boolean facturacionGeneral;
    /**
     * Indicador de seleccion de nomina
     */
    private boolean nomina;
    /**
     * Variable que alamcena el valor del anio inicial seleccionado
     */
    private String anioInicial;
    /**
     * Variable que alamcena el valor del anio final seleccionado
     */
    private String anioFinal;
    
    private final String modulo = SessionUtil.getModulo();
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios iniciales
     */
    private List<Registro> listaAnioInicial;
    /**
     * Lista que carga los anios finales
     */
    private List<Registro> listaAnioFinal;
    

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPrepararAnoRemote ejbPreparAno;
    
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de InicioVigenciaControlador
     */
    public InicioVigenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2144
            numFormulario = GeneralCodigoFormaEnum.INICIO_VIGENCIA_CONTROLADOR
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
        // <CARGAR_LISTA>
        cargarListaAnioInicial();
        cargarListaAnioFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnioInicial
     *
     */
    public void cargarListaAnioInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        try {
            listaAnioInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InicioVigenciaControladorUrlEnum.URL4333
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnioFinal
     *
     */
    public void cargarListaAnioFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnioFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InicioVigenciaControladorUrlEnum.URL4794
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton IniciarProceso en la vista
     *
     *
     */
	public void oprimirIniciarProceso() {
		// <CODIGO_DESARROLLADO>
		
//INI_7709782 (25/02/2022 mrosero)
		 String mostrarProceso;
		if (validarIndicadoresSeleccion()) {

			try {
				mostrarProceso = ejbSysmanUtil.consultarParametro(compania,
                        "SF VALIDAR PRESUPUESTO EN INICIO DE VIGENCIA",
                         SessionUtil.getModulo(), new Date(), false);

				if ((!"SI".equals(mostrarProceso) && !"NO".equals(mostrarProceso) )  && facturacionGeneral) {
	
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4399"));
					return;
				}
//FIN_7709782 (25/02/2022 mrosero)
				
				ejbPreparAno.prepararInicioVigencia(compania, Integer.parseInt(anioFinal),
						Integer.parseInt(anioInicial), compania, nomina, contabilidad, facturacionGeneral);

				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	
 // </CODIGO_DESARROLLADO>
    private boolean validarIndicadoresSeleccion() {
        if (!nomina && !contabilidad && !presupuesto && !facturacionGeneral) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2124"));
            return false;
        }
        else {
            return true;
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isContabilidad() {
        return contabilidad;
    }

    public void setContabilidad(boolean contabilidad) {
        this.contabilidad = contabilidad;
    }

    public boolean isPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(boolean presupuesto) {
        this.presupuesto = presupuesto;
    }

    public boolean isFacturacionGeneral() {
        return facturacionGeneral;
    }

    public void setFacturacionGeneral(boolean facturacionGeneral) {
        this.facturacionGeneral = facturacionGeneral;
    }

    public boolean isNomina() {
        return nomina;
    }

    public void setNomina(boolean nomina) {
        this.nomina = nomina;
    }

    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable anioFinal
     * 
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     * 
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnioInicial
     * 
     * @return listaAnioInicial
     */
    public List<Registro> getListaAnioInicial() {
        return listaAnioInicial;
    }

    /**
     * Asigna la lista listaAnioInicial
     * 
     * @param listaAnioInicial
     * Variable a asignar en listaAnioInicial
     */
    public void setListaAnioInicial(List<Registro> listaAnioInicial) {
        this.listaAnioInicial = listaAnioInicial;
    }

    /**
     * Retorna la lista listaAnioFinal
     * 
     * @return listaAnioFinal
     */
    public List<Registro> getListaAnioFinal() {
        return listaAnioFinal;
    }

    /**
     * Asigna la lista listaAnioFinal
     * 
     * @param listaAnioFinal
     * Variable a asignar en listaAnioFinal
     */
    public void setListaAnioFinal(List<Registro> listaAnioFinal) {
        this.listaAnioFinal = listaAnioFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}