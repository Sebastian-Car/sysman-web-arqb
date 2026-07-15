/*-
 * AnularCertValControlador.java
 *
 * 1.0
 * 
 * 14/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import com.sysman.predial.enums.AnularCertValControladorEnum;
import com.sysman.predial.enums.AnularCertValControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que permite anular el certificado de valorización a
 * aquellas cedulas catastrales a las que se le han expedido paz y
 * salvo.
 *
 * @version 1.0, 14/07/2017 Proceso de Refactoring DSS y correcciones
 * SonarLint
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped

public class AnularCertValControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    private String predio;

    private String numCert;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Registro que contiene la cedula catastral
     */
    private RegistroDataModelImpl listacodcatas;
    /**
     * Registro que contiene los certificados de valoracion
     */
    private RegistroDataModelImpl listacodcert;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AnularCertValControlador
     */
    public AnularCertValControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ANULAR_CERTVALORIZACION_CONTROLADOR
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
        cargarListacodcatas();
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
     * Carga la lista listacodcatas
     *
     */
    public void cargarListacodcatas() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularCertValControladorUrlEnum.URL3897
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodcatas = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "PREDIO");
    }

    /**
     * 
     * Carga la lista listacodcert
     *
     */
    public void cargarListacodcert() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularCertValControladorUrlEnum.URL4617
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.PREDIO.getName(), predio);

        listacodcert = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMCER");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Anular en la vista
     *
     *
     */
    public void oprimirAnular() {
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.USUARIO.getName(),
                            SessionUtil.getUser().getCodigo());

            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            param.put(AnularCertValControladorEnum.NUMCER.getValue(), numCert);

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AnularCertValControladorUrlEnum.URL2857
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
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
     * listacodcatas
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodcatas(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predio = SysmanFunciones.nvl(registroAux.getCampos().get("PREDIO"), "")
                        .toString();

        numCert = null;

        cargarListacodcert();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodcert
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodcert(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numCert = SysmanFunciones.nvl(registroAux.getCampos().get("NUMCER"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public String getPredio() {
        return predio;
    }

    public void setPredio(String predio) {
        this.predio = predio;
    }

    public String getNumCert() {
        return numCert;
    }

    public void setNumCert(String numCert) {
        this.numCert = numCert;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodcatas
     * 
     * @return listacodcatas
     */
    public RegistroDataModelImpl getListacodcatas() {
        return listacodcatas;
    }

    /**
     * Asigna la lista listacodcatas
     * 
     * @param listacodcatas
     * Variable a asignar en listacodcatas
     */
    public void setListacodcatas(RegistroDataModelImpl listacodcatas) {
        this.listacodcatas = listacodcatas;
    }

    /**
     * Retorna la lista listacodcert
     * 
     * @return listacodcert
     */
    public RegistroDataModelImpl getListacodcert() {
        return listacodcert;
    }

    /**
     * Asigna la lista listacodcert
     * 
     * @param listacodcert
     * Variable a asignar en listacodcert
     */
    public void setListacodcert(RegistroDataModelImpl listacodcert) {
        this.listacodcert = listacodcert;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
