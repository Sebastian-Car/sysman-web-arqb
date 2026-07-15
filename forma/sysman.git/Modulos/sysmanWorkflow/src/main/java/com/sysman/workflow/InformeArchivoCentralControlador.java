/*-
 * InformeArchivoCentralControlador.java
 *
 * 1.0
 * 
 * 05/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.CorrespondenciaRecibidaControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorUrlEnum;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 05/02/2020
 * @author Cesar Ochoa
 */
@ManagedBean
@ViewScoped
public class InformeArchivoCentralControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual
     * inicio sesion el usuario, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String numeroInicial;
    private String numeroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreInicial;
    private String nombreFinal;
    private Date horaInicial;
    private Date horaFinal;
    private String serieDocumental;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /** Constante a nivel de clase que aloja la cadena: COMPANIA. */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();
	/** Constante a nivel de clase que aloja la cadena: CODIGO. */
	private final String cCodigo = GeneralParameterEnum.CODIGO.getName();
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private List<Registro> listaNumeroInicial;
    private List<Registro> listaNumeroFinal;
    private RegistroDataModelImpl listaCbSerieDocumental;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeArchivoCentralControlador
     */
    public InformeArchivoCentralControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 2154
            numFormulario = GeneralCodigoFormaEnum.IMFORME_ARCHIVO_CENTRAL_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
     * sido creado, en este se realizan las asignaciones iniciales necesarias para
     * la visualizacion del formulario, como son tablas, origenes de datos,
     * inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNumeroInicial();
        cargarListaCbSerieDocumental();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            SimpleDateFormat formateadorHora = new SimpleDateFormat("HH:MM");
            horaInicial = formateadorHora.parse("07:00");
            horaFinal = formateadorHora.parse("18:00");
            fechaInicial = SysmanFunciones.convertirAFecha(SysmanFunciones.primeroDeMesCadena(new Date()));
            fechaFinal = SysmanFunciones.ultimoDiaDate(new Date());
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaNumeroInicial
     *
     */
    public void cargarListaNumeroInicial() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNumeroInicial = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            CorrespondenciaRecibidaControladorUrlEnum.URL001.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCbSerieDocumental</code> asociada al combo Serie
     * Documental (CB5904).
     */
	/**
	 * Carga la lista: <code>listaCbSerieDocumental</code> asociada al
	 * combo Serie Documental (CB5904).
	 */
	public void cargarListaCbSerieDocumental() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTramitesControladorUrlEnum.URL0002
						.getValue());

		listaCbSerieDocumental = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
    }
    
    /**
	 * Metodo ejecutado al seleccionar una fila de la lista:
	 * <code>listaCbSerieDocumental</code> asociada al combo .
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbSerieDocumental(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        serieDocumental = String.valueOf(registroAux.getCampos().get(cCodigo));
    }
    /**
     * 
     * Carga la lista listaNumeroFinal
     *
     */
    public void cargarListaNumeroFinal() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroInicial);

        try {
            listaNumeroFinal = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            CorrespondenciaRecibidaControladorUrlEnum.URL002.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control NumeroInicial
     * 
     * 
     */
    public void cambiarNumeroInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaNumeroFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {

            Map<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazos.put("numeroInicial", numeroInicial);
            reemplazos.put("numeroFinal", numeroFinal);
            reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("horaInicial", SysmanFunciones.convertirAHoraCadena(horaInicial));
            reemplazos.put("horaFinal", SysmanFunciones.convertirAHoraCadena(horaFinal));
            parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta("002077ArchivoCentral", Integer.parseInt(modulo), reemplazos,
                    parametros);

            archivoDescarga = JsfUtil.exportarStreamed("002077ArchivoCentral", parametros,
                    ConectorPool.ESQUEMA_SYSMAN, formato);

        } catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException
                | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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

    /**
     * Retorna la variable numeroInicial
     * 
     * @return numeroInicial
     */
    public String getNumeroInicial() {
        return numeroInicial;
    }

    /**
     * Asigna la variable numeroInicial
     * 
     * @param numeroInicial Variable a asignar en numeroInicial
     */
    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    /**
     * Retorna la variable numeroFinal
     * 
     * @return numeroFinal
     */
    public String getNumeroFinal() {
        return numeroFinal;
    }

    /**
     * Asigna la variable numeroFinal
     * 
     * @param numeroFinal Variable a asignar en numeroFinal
     */
    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable nombreInicial
     * 
     * @return nombreInicial
     */
    public String getNombreInicial() {
        return nombreInicial;
    }

    /**
     * Asigna la variable nombreInicial
     * 
     * @param nombreInicial Variable a asignar en nombreInicial
     */
    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    /**
     * Retorna la variable nombreFinal
     * 
     * @return nombreFinal
     */
    public String getNombreFinal() {
        return nombreFinal;
    }

    /**
     * Asigna la variable nombreFinal
     * 
     * @param nombreFinal Variable a asignar en nombreFinal
     */
    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    /**
     * Retorna la variable horaInicial
     * 
     * @return horaInicial
     */
    public Date getHoraInicial() {
        return horaInicial;
    }

    /**
     * Asigna la variable horaInicial
     * 
     * @param horaInicial Variable a asignar en horaInicial
     */
    public void setHoraInicial(Date horaInicial) {
        this.horaInicial = horaInicial;
    }

    /**
     * Retorna la variable horaFinal
     * 
     * @return horaFinal
     */
    public Date getHoraFinal() {
        return horaFinal;
    }

    /**
     * Asigna la variable horaFinal
     * 
     * @param horaFinal Variable a asignar en horaFinal
     */
    public void setHoraFinal(Date horaFinal) {
        this.horaFinal = horaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public List<Registro> getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(List<Registro> listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public List<Registro> getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(List<Registro> listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    public String getSerieDocumental() {
        return serieDocumental;
    }

    public void setSerieDocumental(String serieDocumental) {
        this.serieDocumental = serieDocumental;
    }

    public RegistroDataModelImpl getListaCbSerieDocumental() {
        return listaCbSerieDocumental;
    }

    public void setListaCbSerieDocumental(RegistroDataModelImpl listaCbSerieDocumental) {
        this.listaCbSerieDocumental = listaCbSerieDocumental;
    }



    // </SET_GET_LISTAS_COMBO_GRANDE>
}
