/*-
 * RelacionPrestamoBienes.java
 *
 * 1.0
 * 
 * 19/10/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.RelacionPrestamoBienesControladorEnum;
import com.sysman.almacen.enums.RelacionPrestamoBienesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario modal que permite generar de informe para
 * an&aacute;lisis de tr&aacute;mites gestionados para el
 * pr&eacute;stamo de bienes, de acuerdo a los par&aacute;metros
 * ingresados.
 *
 * @version 1.0, 19/10/2016
 * @author jrodrigueza
 * @modifier amonroy
 * @version 2, 11/05/2017 Proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class RelacionPrestamoBienesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Elemento final.
     */
    private String elementoFinal;
    /**
     * Elemento inicial.
     */
    private String elementoInicial;
    /**
     * Placa inicial.
     */
    private int placaInicial;
    /**
     * Placa final.
     */
    private int placaFinal;
    /**
     * Fecha inicial.
     */
    private Date fechaInicial;
    /**
     * Fecha final.
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista de elementos para seleccionar el elemento inicial.
     */
    private RegistroDataModelImpl listaElementoFinal;
    /**
     * Lista de elementos para seleccionar el elemento final.
     */
    private RegistroDataModelImpl listaElementoInicial;
    /**
     * Lista de placas para seleccionar la placa inicial.
     */
    private RegistroDataModelImpl listaPlacaInicial;
    /**
     * Lista de placas para seleccionar la placa final.
     */
    private RegistroDataModelImpl listaPlacaFinal;
    /**
     * Constante definida por la cantidad de veces que se encuentra la
     * palabra "ELEMENTO" en el formulario
     */
    private final String elementoCons;
    /**
     * Constante definida por la cantidad de veces que se encuentra la
     * palabra "SERIE" en el formulario
     */
    private final String serieCons;

    /**
     * Crea una nueva instancia de RelacionPrestamoBienes.
     */
    public RelacionPrestamoBienesControlador() {
        super();
        compania = SessionUtil.getCompania();
        elementoCons = "ELEMENTO";
        serieCons = "SERIE";
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACION_PRESTAMO_BIENES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        cargarListaElementoFinal();
        cargarListaElementoInicial();
        cargarListaPlacaInicial();
        cargarListaPlacaFinal();
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
        fechaInicial = new Date();
        try {
            fechaFinal = SysmanFunciones.ultimoDiaDate(new Date());
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaElementoFinal.
     */
    public void cargarListaElementoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPrestamoBienesControladorUrlEnum.URL5558
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        elementoInicial);

        listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, elementoCons);
    }

    /**
     * 
     * Carga la lista listaElementoInicial.
     */
    public void cargarListaElementoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPrestamoBienesControladorUrlEnum.URL6612
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, elementoCons);
    }

    /**
     * 
     * Carga la lista listaPlacaInicial.
     */
    public void cargarListaPlacaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPrestamoBienesControladorUrlEnum.URL7682
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaPlacaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, serieCons);
    }

    /**
     * 
     * Carga la lista listaPlacaFinal.
     */
    public void cargarListaPlacaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionPrestamoBienesControladorUrlEnum.URL8655
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelacionPrestamoBienesControladorEnum.PARAM0.getValue(),
                        placaInicial);

        listaPlacaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, serieCons);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PresentarExcel en la
     * vista. Genera el informe en formato MS Excel.
     */
    public void oprimirPresentarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PresentarPDF en la vista.
     * Genera el informe en formato PDF.
     *
     */
    public void oprimirPresentarPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el informe de relaci�n de pr�stamos de bienes,
     * seg�n el formato ingresado por par�metro.
     * 
     * @param formato
     * Formato con el que se debe generar el reporte.
     */
    private void generarReporte(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("elementoInicial", elementoInicial);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("serieInicial", placaInicial);
            reemplazar.put("serieFinal", placaFinal);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "001154RelacionPrestamoBienes";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB1998"), e.getMessage()));
        }
    }

    public void seleccionarFilaElementoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(elementoCons),
                        "").toString();
    }

    public void seleccionarFilaElementoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(elementoCons),
                        "").toString();
        elementoFinal = null;
        cargarListaElementoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlacaInicial.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlacaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        placaInicial = Integer.parseInt(SysmanFunciones.nvl(
                        registroAux.getCampos().get(serieCons), 0).toString());
        placaFinal = 0;
        cargarListaPlacaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlacaFinal.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlacaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        placaFinal = Integer.parseInt(SysmanFunciones.nvl(
                        registroAux.getCampos().get(serieCons), 0).toString());
    }

    /**
     * Retorna la variable elementoFinal
     * 
     * @return elementoFinal
     */
    public String getElementoFinal() {
        return elementoFinal;
    }

    /**
     * Asigna la variable elementoFinal
     * 
     * @param elementoFinal
     * Variable a asignar en elementoFinal
     */
    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }

    /**
     * Retorna la variable elementoInicial
     * 
     * @return elementoInicial
     */
    public String getElementoInicial() {
        return elementoInicial;
    }

    /**
     * Asigna la variable elementoInicial
     * 
     * @param elementoInicial
     * Variable a asignar en elementoInicial
     */
    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    /**
     * Retorna la variable placaInicial
     * 
     * @return placaInicial
     */
    public int getPlacaInicial() {
        return placaInicial;
    }

    /**
     * Asigna la variable placaInicial
     * 
     * @param placaInicial
     * Variable a asignar en placaInicial
     */
    public void setPlacaInicial(int placaInicial) {
        this.placaInicial = placaInicial;
    }

    /**
     * Retorna la variable placaFinal
     * 
     * @return placaFinal
     */
    public int getPlacaFinal() {
        return placaFinal;
    }

    /**
     * Asigna la variable placaFinal
     * 
     * @param placaFinal
     * Variable a asignar en placaFinal
     */
    public void setPlacaFinal(int placaFinal) {
        this.placaFinal = placaFinal;
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
     * @param fechaInicial
     * Variable a asignar en fechaInicial
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
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaElementoFinal
     * 
     * @return listaElementoFinal
     */
    public RegistroDataModelImpl getListaElementoFinal() {
        return listaElementoFinal;
    }

    /**
     * Asigna la lista listaElementoFinal
     * 
     * @param listaElementoFinal
     * Variable a asignar en listaElementoFinal
     */
    public void setListaElementoFinal(
        RegistroDataModelImpl listaElementoFinal) {
        this.listaElementoFinal = listaElementoFinal;
    }

    /**
     * Retorna la lista listaElementoInicial
     * 
     * @return listaElementoInicial
     */
    public RegistroDataModelImpl getListaElementoInicial() {
        return listaElementoInicial;
    }

    /**
     * Asigna la lista listaElementoInicial
     * 
     * @param listaElementoInicial
     * Variable a asignar en listaElementoInicial
     */
    public void setListaElementoInicial(
        RegistroDataModelImpl listaElementoInicial) {
        this.listaElementoInicial = listaElementoInicial;
    }

    /**
     * Retorna la lista listaPlacaInicial
     * 
     * @return listaPlacaInicial
     */
    public RegistroDataModelImpl getListaPlacaInicial() {
        return listaPlacaInicial;
    }

    /**
     * Asigna la lista listaPlacaInicial
     * 
     * @param listaPlacaInicial
     * Variable a asignar en listaPlacaInicial
     */
    public void setListaPlacaInicial(RegistroDataModelImpl listaPlacaInicial) {
        this.listaPlacaInicial = listaPlacaInicial;
    }

    /**
     * Retorna la lista listaPlacaFinal
     * 
     * @return listaPlacaFinal
     */
    public RegistroDataModelImpl getListaPlacaFinal() {
        return listaPlacaFinal;
    }

    /**
     * Asigna la lista listaPlacaFinal
     * 
     * @param listaPlacaFinal
     * Variable a asignar en listaPlacaFinal
     */
    public void setListaPlacaFinal(RegistroDataModelImpl listaPlacaFinal) {
        this.listaPlacaFinal = listaPlacaFinal;
    }
}
