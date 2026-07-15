/*-
 * EventosCalendariosControlador.java
 *
 * 1.0
 * 
 * 01/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Permite el registro de eventos relevantes para las entidades, los
 * cuales ser&aacute;n mostrados en el calendario de eventos.
 *
 * @version 1.0, 01/09/2017
 * @author jrodrigueza
 */
@ManagedBean
@ViewScoped
public class EventosCalendariosControlador extends BeanBaseContinuoAcmeImpl {

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Implementaci&oacute;n del EJB de EjbSysmanUtilRemote para hacer
     * el llamado a las funciones y procedimientos que se invocan
     * dentro del Controlador y se encuentran almacenadas en el
     * paquete PCK_SYSMAN_UTIL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Duraci&oacute;n m&iacute;nima de un evento, en segundos.
     */
    private static final long DURACION_MINIMA = 900;

    /**
     * Campo FECHA_INICIAL.
     */
    private static final String FECHA_INICIAL = "FECHA_INICIAL";

    /**
     * Campo FECHA_FINAL.
     */
    private static final String FECHA_FINAL = GeneralParameterEnum.FECHA_FINAL
                    .getName();

    /**
     * Crea una nueva instancia de EventosCalendariosControlador
     */
    public EventosCalendariosControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.EVENTOS_CALENDARIO_CONTROLADOR
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
        enumBase = GenericUrlEnum.EVENTOS_CALENDARIO;
        urlConexionCache = UrlServiceCache.SYSMANIRISST;
        buscarLlave();
        reasignarOrigen();
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
     * @return
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
     * @return
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
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (!fechasValidas()) {
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return
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
     * @return
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
     * @return
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
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // No se requiere agregar campos auxiliares
    }

    /**
     * Validaci&oacute;n de fechas antes de insertar o actualizar un
     * registro.
     * 
     * @return verdadero si las fechas ingresadas son correctas.
     */
    private boolean fechasValidas() {
        if (!fechaHabil()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3561"));
            return false;
        }
        if (!rangoValido()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3507"));
            return false;
        }
        if (!duracionValida()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3508"));
            return false;
        }
        return true;
    }

    /**
     * Valida que la fecha inicial no est&eacute; antes de la fecha
     * actual.
     * 
     * @return Verdadero si es una fecha h&aacute;bil.
     */
    private boolean fechaHabil() {
        Map<String, Object> campos = registro.getCampos();
        Date fechaHoy = new Date();
        Date fechaInicial = (Date) campos.get(FECHA_INICIAL);
        return fechaHoy.before(fechaInicial);
    }

    /**
     * Verifica que la fecha inicial sea inferior a la final o no sea
     * iguales.
     * 
     * @return Verdadero si el rango es v&aacute;lido.
     */
    private boolean rangoValido() {
        Map<String, Object> campos = registro.getCampos();
        Date fechaInicial = (Date) campos.get(FECHA_INICIAL);
        Date fechaFinal = (Date) campos.get(FECHA_FINAL);
        return fechaInicial.before(fechaFinal);
    }

    /**
     * Verifica que la duraci&oacute;n estipulada en el rango de
     * fechas no sea inferior a la m&iacute;nima.
     * 
     * @return Verdadero si la duraci&oacute;n es v&aacute;lida.
     */
    private boolean duracionValida() {
        Map<String, Object> campos = registro.getCampos();
        Date fechaInicial = (Date) campos.get(FECHA_INICIAL);
        Date fechaFinal = (Date) campos.get(FECHA_FINAL);
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(
                        fechaFinal.getTime() - fechaInicial.getTime());
        return diffSeconds >= DURACION_MINIMA;
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
