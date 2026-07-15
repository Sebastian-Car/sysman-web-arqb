/*-
 * RptidiomasControlador.java
 *
 * 1.0
 * 
 * 27/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyacá.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.RptidiomasControladorEnum;
import com.sysman.hojasdevida.enums.RptidiomasControladorUrlEnum;
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
 * Migración del formulario modal de idiomas.
 * 
 * @version 1.0, 27/12/2017
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class RptidiomasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compañía en la cual inició sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente.
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable para el número de documento inicial.
     */
    private String documentoInicial;
    /**
     * Variable para el número de documento final.
     */
    private String documentoFinal;
    /**
     * Variable para el nombre de la persona inicial.
     */
    private String personaIni;
    /**
     * Variable para el nombre de la persona final.
     */
    private String personaFin;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Constante a nivel de clase que aloja el código del módulo desde
     * el cual el usuario inició sesión.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja el nombre de la compañía
     * desde la cual se inició sesión.
     */
    private final String nombreCompania = SessionUtil.getCompaniaIngreso()
                    .getNombre();

    /**
     * lista datamodel para la persona inicial.
     */
    private RegistroDataModelImpl listaPersonaInicial;
    /**
     * Lista datamodel para la persona final.
     */
    private RegistroDataModelImpl listaPersonaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptidiomasControlador.
     */
    public RptidiomasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPT_IDIOMAS_CONTROLADOR
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
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicialización
     * de listas y demás necesarios.
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaPersonaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este método es invocado el método inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaPersonaInicial.
     *
     */
    public void cargarListaPersonaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptidiomasControladorUrlEnum.URL142
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(RptidiomasControladorEnum.COMPANIA.getValue(), compania);

        listaPersonaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    /**
     * 
     * Carga la lista listaPersonaFinal.
     *
     */
    public void cargarListaPersonaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptidiomasControladorUrlEnum.URL198
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(RptidiomasControladorEnum.COMPANIA.getValue(), compania);
        param.put(RptidiomasControladorEnum.EMPLEADOINICIAL.getValue(),
                        documentoInicial);

        listaPersonaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Método ejecutado al oprimir el botón GenerarPDF en la vista.
     *
     */
    public void oprimirGenerarPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Método ejecutado al oprimir el botón GenerarExcel en la vista.
     *
     */
    public void oprimirGenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "001583RHvNatIdiomas";

        archivoDescarga = null;

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("documentoPersonaInicial", documentoInicial);
        reemplazar.put("documentoPersonaFinal", documentoFinal);
        reemplazar.put("compania", compania);
        // </REEMPLAZAR VARIABLES EN CONSULTA>
        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            /*-aqui reporte hace referencia al nombre del reporte*/

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
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
     * Método ejecutado al seleccionar una fila de la lista
     * listaPersonaInicial.
     *
     * @param event
     * objeto que encapsula la acción proveniente de la vista.
     */
    public void seleccionarFilaPersonaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        documentoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName()) == null
                            ? ""
                            : registroAux.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString();

        personaIni = registroAux.getCampos().get(
                        RptidiomasControladorEnum.PERSONA.getValue()) == null
                            ? ""
                            : registroAux.getCampos().get(
                                            RptidiomasControladorEnum.PERSONA
                                                            .getValue())
                                            .toString();

        documentoFinal = null;
        personaFin = null;
        cargarListaPersonaFinal();
    }

    /**
     * 
     * Método ejecutado al seleccionar una fila de la lista
     * listaPersonaFinal.
     *
     * @param event
     * objeto que encapsula la acción proveniente de la vista.
     */
    public void seleccionarFilaPersonaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        documentoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName()) == null
                            ? ""
                            : registroAux.getCampos().get(
                                            GeneralParameterEnum.NUMERO_DCTO
                                                            .getName())
                                            .toString();

        personaFin = registroAux.getCampos().get(
                        RptidiomasControladorEnum.PERSONA.getValue()) == null
                            ? ""
                            : registroAux.getCampos().get(
                                            RptidiomasControladorEnum.PERSONA
                                                            .getValue())
                                            .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable documentoInicial.
     * 
     * @return documentoInicial
     */
    public String getDocumentoInicial() {
        return documentoInicial;
    }

    /**
     * Asigna la variable documentoInicial.
     * 
     * @param documentoInicial
     * Variable a asignar en documentoInicial.
     */
    public void setDocumentoInicial(String documentoInicial) {
        this.documentoInicial = documentoInicial;
    }

    /**
     * Retorna la variable documentoFinal.
     * 
     * @return documentoFinal
     */
    public String getDocumentoFinal() {
        return documentoFinal;
    }

    /**
     * Asigna la variable documentoFinal.
     * 
     * @param documentoFinal
     * Variable a asignar en documentoFinal.
     */
    public void setDocumentoFinal(String documentoFinal) {
        this.documentoFinal = documentoFinal;
    }

    /**
     * Retorna la variable personaIni.
     * 
     * @return personaIni
     */
    public String getPersonaIni() {
        return personaIni;
    }

    /**
     * Asigna la variable personaIni.
     * 
     * @param personaIni
     * Variable a asignar en personaIni.
     */
    public void setPersonaIni(String personaIni) {
        this.personaIni = personaIni;
    }

    /**
     * Retorna la variable personaFin.
     * 
     * @return personaFin
     */
    public String getPersonaFin() {
        return personaFin;
    }

    /**
     * Asigna la variable personaFin.
     * 
     * @param personaFin
     * Variable a asignar en personaFin.
     */
    public void setPersonaFin(String personaFin) {
        this.personaFin = personaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaPersonaInicial.
     * 
     * @return listaPersonaInicial
     */
    public RegistroDataModelImpl getListaPersonaInicial() {
        return listaPersonaInicial;
    }

    /**
     * Asigna la lista listaPersonaInicial.
     * 
     * @param listaPersonaInicial
     * Variable a asignar en listaPersonaInicial.
     */
    public void setListaPersonaInicial(
        RegistroDataModelImpl listaPersonaInicial) {
        this.listaPersonaInicial = listaPersonaInicial;
    }

    /**
     * Retorna la lista listaPersonaFinal.
     * 
     * @return listaPersonaFinal
     */
    public RegistroDataModelImpl getListaPersonaFinal() {
        return listaPersonaFinal;
    }

    /**
     * Asigna la lista listaPersonaFinal.
     * 
     * @param listaPersonaFinal
     * Variable a asignar en listaPersonaFinal.
     */
    public void setListaPersonaFinal(RegistroDataModelImpl listaPersonaFinal) {
        this.listaPersonaFinal = listaPersonaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
