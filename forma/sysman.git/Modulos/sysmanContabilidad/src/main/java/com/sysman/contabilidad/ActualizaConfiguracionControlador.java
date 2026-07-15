/*-
 * ActualizaConfiguracionControlador.java
 *
 * 1.0
 * 
 * 19 jun. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.enums.ActualizaConfiguracionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que permite actualizar la información de equivalencias
 * contables para el siguiente anio
 *
 * @version 1.0, 19/06/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ActualizaConfiguracionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que ingreso a la aplicacion
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Anio de fuente
     */
    private String anioFuente;
    /**
     * Anio destino de configuración
     */
    private String anioDestino;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios fuentes
     */
    private List<Registro> listaAnoFuente;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;

    /**
     * Crea una nueva instancia de ActualizaConfiguracionControlador
     */
    public ActualizaConfiguracionControlador() {
        super();
        compania = SessionUtil.getCompania();
        anioFuente = Integer.toString(SysmanFunciones.ano(new Date()));
        anioDestino = Integer.toString(SysmanFunciones.ano(new Date()) + 1);
        usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZA_CONFIGURACION_CONTROLADOR
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
        cargarListaAnoFuente();
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
     * Carga la lista listaAnoFuente
     *
     */
    public void cargarListaAnoFuente() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoFuente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizaConfiguracionControladorUrlEnum.URL4621
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
     * Metodo ejecutado al oprimir el boton Iniciar en la vista
     *
     *
     */
    public void oprimirIniciar() {
        try {
            ejbContabilidadCinco.actualizarConfSiguienteAnio(compania,
                            Integer.parseInt(anioFuente),
                            Integer.parseInt(anioDestino), usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoFuente
     * 
     * 
     */
    public void cambiarAnoFuente() {
        anioDestino = Integer.toString(Integer.parseInt(anioFuente) + 1);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anioFuente
     * 
     * @return anioFuente
     */
    public String getAnioFuente() {
        return anioFuente;
    }

    /**
     * Asigna la variable anioFuente
     * 
     * @param anioFuente
     * Variable a asignar en anioFuente
     */
    public void setAnioFuente(String anioFuente) {
        this.anioFuente = anioFuente;
    }

    /**
     * Retorna la variable anioDestino
     * 
     * @return anioDestino
     */
    public String getAnioDestino() {
        return anioDestino;
    }

    /**
     * Asigna la variable anioDestino
     * 
     * @param anioDestino
     * Variable a asignar en anioDestino
     */
    public void setAnioDestino(String anioDestino) {
        this.anioDestino = anioDestino;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoFuente
     * 
     * @return listaAnoFuente
     */
    public List<Registro> getListaAnoFuente() {
        return listaAnoFuente;
    }

    /**
     * Asigna la lista listaAnoFuente
     * 
     * @param listaAnoFuente
     * Variable a asignar en listaAnoFuente
     */
    public void setListaAnoFuente(List<Registro> listaAnoFuente) {
        this.listaAnoFuente = listaAnoFuente;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
