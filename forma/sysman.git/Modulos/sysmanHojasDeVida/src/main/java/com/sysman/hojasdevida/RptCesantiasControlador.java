/*-
 * RptcesantiasControlador.java
 *
 * 1.0
 * 
 * 10/01/2018
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
import com.sysman.hojasdevida.enums.RptCesantiasControladorEnum;
import com.sysman.hojasdevida.enums.RptCesantiasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion del formulario RptCesantias, migracion de reportes
 * 001581HojasDeVidaListadoCesantias y 001584RhvseguridadCesantias.
 *
 * @version 1.0, 10/01/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class RptCesantiasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable indicador de Listado
     */
    private boolean listado;
    /**
     * variable cadena que almacena el numero de cedula de la persona
     * incial
     */
    private String personaInicial;
    /**
     * variable cadena que almacena el numero de cedula de la persona
     * final
     */
    private String personaFinal;
    /**
     * variable cadena que almacena el empleado inicial
     */
    private String carpetaInicial;
    /**
     * variable cadena que almacena el empleado inicial
     */
    private String carpetaFinal;
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
     * Declaración de listado de Empleado Inicial
     */
    private RegistroDataModelImpl listaPersonaInicial;
    /**
     * Declaración de listado de Empleado Final
     */
    private RegistroDataModelImpl listaPersonaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptcesantiasControlador
     */
    public RptCesantiasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPT_CESANTIAS_CONTROLADOR
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
        cargarListaPersonaInicial();
        cargarListaPersonaFinal();
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
     * Método cargar de la listaPersonaInicial
     *
     */

    public void cargarListaPersonaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptCesantiasControladorUrlEnum.URL4170
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaPersonaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        RptCesantiasControladorEnum.NUMERO_DCTO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaPersonaFinal
     *
     * Método cargar de la listaPersonaInicial
     */
    public void cargarListaPersonaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptCesantiasControladorUrlEnum.URL1704
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(RptCesantiasControladorEnum.EMPLEADOINICIAL.getValue(),
                        personaInicial);

        listaPersonaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        RptCesantiasControladorEnum.NUMERO_DCTO
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarPDF en la vista
     *
     *
     */
    public void oprimirGenerarPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarExcel en la vista
     *
     *
     */
    public void oprimirGenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que contiene la logica para imprimir los reportes en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generarInforme(FORMATOS formato) {

        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("empleadoInicial", personaInicial);
        reemplazos.put("empleadoFinal", personaFinal);

        String reporte = listado
            ? RptCesantiasControladorEnum.REPORTE001581
                            .getValue()
            : RptCesantiasControladorEnum.REPORTE001584.getValue();

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                        .getNombre().toUpperCase());

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);
        try {
            archivoDescarga = JsfUtil
                            .exportarStreamed(reporte,
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            reporte));
            Logger.getLogger(RptCesantiasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPersonaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPersonaInicial(SelectEvent event) {
        Registro registroAuxI = (Registro) event.getObject(); //
        personaInicial = SysmanFunciones
                        .nvl(registroAuxI.getCampos()
                                        .get(RptCesantiasControladorEnum.NUMERO_DCTO
                                                        .getValue()),
                                        "")
                        .toString();
        carpetaInicial = SysmanFunciones
                        .nvl(registroAuxI.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        personaFinal = null;
        carpetaFinal = null;
        cargarListaPersonaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPersonaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPersonaFinal(SelectEvent event) {
        Registro registroAuxF = (Registro) event.getObject();
        personaFinal = SysmanFunciones
                        .nvl(registroAuxF.getCampos()
                                        .get(RptCesantiasControladorEnum.NUMERO_DCTO
                                                        .getValue()),
                                        "")
                        .toString();
        carpetaFinal = SysmanFunciones
                        .nvl(registroAuxF.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * @return the listado
     */
    public boolean isListado() {
        return listado;
    }

    /**
     * @param listado
     * the listado to set
     */
    public void setListado(boolean listado) {
        this.listado = listado;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Retorna la variable personaInicial
     * 
     * @return personaInicial
     */
    public String getPersonaInicial() {
        return personaInicial;
    }

    /**
     * Asigna la variable personaInicial
     * 
     * @param personaInicial
     * Variable a asignar en personaInicial
     */
    public void setPersonaInicial(String personaInicial) {
        this.personaInicial = personaInicial;
    }

    /**
     * Retorna la variable personaFinal
     * 
     * @return personaFinal
     */
    public String getPersonaFinal() {
        return personaFinal;
    }

    /**
     * Asigna la variable personaFinal
     * 
     * @param personaFinal
     * Variable a asignar en personaFinal
     */
    public void setPersonaFinal(String personaFinal) {
        this.personaFinal = personaFinal;
    }

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

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @return the listaPersonaInicial
     */
    public RegistroDataModelImpl getListaPersonaInicial() {
        return listaPersonaInicial;
    }

    /**
     * @param listaPersonaInicial
     * the listaPersonaInicial to set
     */
    public void setListaPersonaInicial(
        RegistroDataModelImpl listaPersonaInicial) {
        this.listaPersonaInicial = listaPersonaInicial;
    }

    /**
     * @return the listaPersonaFinal
     */
    public RegistroDataModelImpl getListaPersonaFinal() {
        return listaPersonaFinal;
    }

    /**
     * @param listaPersonaFinal
     * the listaPersonaFinal to set
     */
    public void setListaPersonaFinal(RegistroDataModelImpl listaPersonaFinal) {
        this.listaPersonaFinal = listaPersonaFinal;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
