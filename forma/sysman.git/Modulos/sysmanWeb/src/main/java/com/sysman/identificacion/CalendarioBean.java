/*-
 * CalendarioView.java
 *
 * 1.0
 * 
 * 4/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.identificacion;

import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Define la instancia para el modelo del calendario y los eventos que
 * lo componen.
 * 
 * @version 1.0, 4/09/2017
 * @author jrodrigueza
 *
 */
@ManagedBean
@SessionScoped
public class CalendarioBean {

    /**
     * Modelo que define al calendario.
     */
    private ScheduleModel modeloCalendario;

    /**
     * Atributo que toma el valor del evento seleccionado en el
     * calendario.
     */
    private ScheduleEvent eventoCalendario = new DefaultScheduleEvent();

    /**
     * Para asignar zona horaria del servidor.
     */
    private TimeZone zonaHoraria;

    /**
     * Recurso 531001 getEventoscalendarioOrdenPorFechasQuery
     */
    private static final String URL531001 = "531001";

    /**
     * Objeto para informar de eventos o acciones ocurridas durante un
     * proceso.
     */
    private static final Log LOGGER = LogFactory.getLog(CalendarioBean.class);

    /**
     * Clase de estilo para evento pr&oacute;ximo a vencer.
     */
    private static final String EVENTO_PROXIMO = "caja-amarilla";

    /**
     * Clase de estilo para evento vencido.
     */
    private static final String EVENTO_VENCIDO = "caja-roja";

    /**
     * Crea una instancia de CalendarioBean.
     */
    public CalendarioBean() {
        setZonaHoraria(ZonaHoraria.cargarZonaHoraria());
        cargarEventos();
    }

    /**
     * Asignaciones iniciales necesarias para la visualizacion del
     * componente Calendario de Primefaces.
     */
    @PostConstruct
    public void inicializar() {
        // nada por hacer...
    }

    /**
     * Permite cargar los eventos de calendario configurados en la
     * adminsitraci&oacute;n del sistema.
     */
    private void cargarEventos() {
        modeloCalendario = new LazyScheduleModel() {

            private static final long serialVersionUID = -7899968584453858315L;

            @Override
            public void loadEvents(Date start, Date end) {
                List<Registro> eventos = traerEventosCalendario(start, end);
                if (eventos == null) {
                    return;
                }
                for (Registro evento : eventos) {
                    Map<String, Object> campos = evento.getCampos();
                    agregarEvento(campos);
                }
            }
        };
    }

    /**
     * Trae los eventos que est&aacute;n entre las fechas
     * <code>inicio</code> y <code>fin</code> enviadas por
     * par&aacute;metro.
     * 
     * @param inicio
     * Fecha inicial
     * @param fin
     * Fecha final
     * @return lista de eventos seg&uacute;n el rango de fechas.
     */
    private List<Registro> traerEventosCalendario(Date inicio, Date fin) {
        List<Registro> lista = null;
        Map<String, Object> filtros = new TreeMap<>();
        filtros.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                        SysmanFunciones.truncarFecha(inicio));
        filtros.put(GeneralParameterEnum.FECHAFINAL.getName(),
                        SysmanFunciones.truncarFecha(SysmanFunciones
                                        .sumarRestarDiasFecha(fin, 1)));
        String urlEnumId = URL531001;
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        RequestManager requestManager = new RequestManager();
        try {
            List<Parameter> parameters = requestManager.getList(url, filtros);
            lista = RegistroConverter.toListRegistro(parameters);
        }
        catch (SystemException e) {
            LOGGER.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return lista;
    }

    /**
     * Extrae la descripci&oacute;n, la fecha inicial y la fecha final
     * del evento recibido por par&aacute;metro; y lo agrega al
     * calendario.
     * 
     * @param campos
     * detalles del evento
     */
    private void agregarEvento(Map<String, Object> campos) {
        DefaultScheduleEvent scheduleEvent = new DefaultScheduleEvent();
        String descripcion = extraerString(
                        campos.get(GeneralParameterEnum.DESCRIPCION
                                        .getName()));
        scheduleEvent.setTitle(descripcion);
        scheduleEvent.setDescription(descripcion);
        Date fechaInicial = (Date) campos.get("FECHA_INICIAL");
        scheduleEvent.setStartDate(fechaInicial);
        Date fechaFinal = (Date) campos.get("FECHA_FINAL");
        scheduleEvent.setEndDate(fechaFinal);
        long diffDays = TimeUnit.MILLISECONDS.toDays(
                        fechaInicial.getTime() - (new Date()).getTime());
        String claseEstilo = null;
        if (diffDays >= 0 && diffDays < 3) {
            claseEstilo = EVENTO_PROXIMO;
        }
        else if (diffDays < 0) {
            claseEstilo = EVENTO_VENCIDO;
        }
        scheduleEvent.setStyleClass(claseEstilo);
        modeloCalendario.addEvent(scheduleEvent);
    }

    /**
     * Al seleccionar un evento existente en el Programador
     *
     * @param selectEvent
     */
    public void seleccionarEvento(SelectEvent selectEvent) {
        eventoCalendario = (ScheduleEvent) selectEvent.getObject();
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

    // <SET_GET_ATRIBUTOS>
    /**
     * @return the modeloCalendario
     */
    public ScheduleModel getModeloCalendario() {
        return modeloCalendario;
    }

    /**
     * @param modeloCalendario
     * the modeloCalendario to set
     */
    public void setModeloCalendario(ScheduleModel modeloCalendario) {
        this.modeloCalendario = modeloCalendario;
    }

    /**
     * @return the eventoCalendario
     */
    public ScheduleEvent getEventoCalendario() {
        return eventoCalendario;
    }

    /**
     * @param eventoCalendario
     * the eventoCalendario to set
     */
    public void setEventoCalendario(ScheduleEvent eventoCalendario) {
        this.eventoCalendario = eventoCalendario;
    }

    /**
     * @return the zonaHoraria
     */
    public TimeZone getZonaHoraria() {
        return zonaHoraria;
    }

    /**
     * @param zonaHoraria
     * the zonaHoraria to set
     */
    public void setZonaHoraria(TimeZone zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }
    // </SET_GET_ATRIBUTOS>

}
