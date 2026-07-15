/*-
 * ReporteFamiliaresControlador.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.ReporteFamiliaresControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
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
 * Migracion del formulario access RptFamiliares a web controlador
 * ReporteFamiliaresControlador forma rptfamiliares.xhtml creacion de
 * menu para abrir el formulario modal, creacion de properties para el
 * formulario modal, asi como generacion del informe Rhv_familiares a
 * partir de un boton.
 *
 * 
 * @version 1.0, 31/01/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class ReporteFamiliaresControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * DOCUMENTACION NECESARIA
     */
    private String carpetaInicial;
    /**
     * DOCUMENTACION NECESARIA
     */
    private String carpetaFinal;

    /**
     * DOCUMENTACION NECESARIA
     */
    private String nombreCarpetaInicial;
    /**
     * DOCUMENTACION NECESARIA
     */
    private String nombreCarpetaFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaCarpetaInicial;
    /**
     * DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaCarpetaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ReporteFamiliaresControlador
     */
    public ReporteFamiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1674
            numFormulario = GeneralCodigoFormaEnum.REPORTE_FAMILIARES_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCarpetaInicial();
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
        /*
         * FR1674-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <MES_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCarpetaInicial
     *
     * DOCUMENTACION ADICIONAL
     */
    public void cargarListaCarpetaInicial() {

        // 685028
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteFamiliaresControladorUrlEnum.URL_162
                                                        .getValue());

        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), params, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "NAT_DATOS_PERSONALES"));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCarpetaFinal
     *
     * DOCUMENTACION ADICIONAL
     */
    public void cargarListaCarpetaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteFamiliaresControladorUrlEnum.URL_194
                                                        .getValue());

        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        params.put("NUMEROCARPETAINICIAL", carpetaInicial);

        try {
            listaCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), params, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "NAT_DATOS_PERSONALES"));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        Map<String, Object> remplazar = new HashMap<>();
        remplazar.put("carpetaInicial", carpetaInicial);
        remplazar.put("carpetaFinal", carpetaFinal);

        Map<String, Object> params = new HashMap<>();

        params.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        Reporteador.resuelveConsulta("001684RhvFamiliares",
                        Integer.parseInt(SessionUtil.getModulo()), remplazar,
                        params);

        try {
            archivoDescarga = JsfUtil.exportarStreamed("001684RhvFamiliares",
                            params, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            //
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </MES_CARGAR_LISTA>
    // <MES_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     * DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </MES_BOTONES>
    // <MES_CAMBIAR>
    // </MES_CAMBIAR>
    // <MES_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaInicial
     *
     * DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaInicial = registroAux.getCampos().get("NUMEROCARPETA")
                        .toString();
        nombreCarpetaInicial = registroAux.getCampos().get("NOMBRECOMPLETO")
                        .toString();
        nombreCarpetaFinal = null;
        carpetaFinal = null;
        cargarListaCarpetaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaFinal
     *
     * DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaFinal = registroAux.getCampos().get("NUMEROCARPETA").toString();
        nombreCarpetaFinal = registroAux.getCampos().get("NOMBRECOMPLETO")
                        .toString();
    }

    // </MES_COMBOS_GRANDES>
    // <MES_ARBOL>
    // </MES_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable carpetaInicial
     * 
     * @return carpetaInicial
     */
    public String getCarpetaInicial() {
        return carpetaInicial;
    }

    /**
     * Asigna la variable carpetaInicial
     * 
     * @param carpetaInicial
     * Variable a asignar en carpetaInicial
     */
    public void setCarpetaInicial(String carpetaInicial) {
        this.carpetaInicial = carpetaInicial;
    }

    /**
     * Retorna la variable carpetaFinal
     * 
     * @return carpetaFinal
     */
    public String getCarpetaFinal() {
        return carpetaFinal;
    }

    /**
     * Asigna la variable carpetaFinal
     * 
     * @param carpetaFinal
     * Variable a asignar en carpetaFinal
     */
    public void setCarpetaFinal(String carpetaFinal) {
        this.carpetaFinal = carpetaFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCarpetaInicial
     * 
     * @return listaCarpetaInicial
     */
    public RegistroDataModelImpl getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    /**
     * Asigna la lista listaCarpetaInicial
     * 
     * @param listaCarpetaInicial
     * Variable a asignar en listaCarpetaInicial
     */
    public void setListaCarpetaInicial(
        RegistroDataModelImpl listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    /**
     * Retorna la lista listaCarpetaFinal
     * 
     * @return listaCarpetaFinal
     */
    public RegistroDataModelImpl getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    /**
     * Asigna la lista listaCarpetaFinal
     * 
     * @param listaCarpetaFinal
     * Variable a asignar en listaCarpetaFinal
     */
    public void setListaCarpetaFinal(RegistroDataModelImpl listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }

    public String getNombreCarpetaInicial() {
        return nombreCarpetaInicial;
    }

    public void setNombreCarpetaInicial(String nombreCarpetaInicial) {
        this.nombreCarpetaInicial = nombreCarpetaInicial;
    }

    public String getNombreCarpetaFinal() {
        return nombreCarpetaFinal;
    }

    public void setNombreCarpetaFinal(String nombreCarpetaFinal) {
        this.nombreCarpetaFinal = nombreCarpetaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
