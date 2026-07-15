/*-
 * RptPublicacionesControlador.java
 *
 * 1.0
 * 
 * 14/12/2017
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
import com.sysman.hojasdevida.enums.RptPublicacionesControladorEnum;
import com.sysman.hojasdevida.enums.RptPublicacionesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/*
 * @version 1.0, 14/12/2017
 * 
 * @author asana Se realiza migracion de formulario desde access
 */
@ManagedBean
@ViewScoped
public class RptPublicacionesControlador extends BeanBaseModal {
    // <DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS>
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    /**
     * Define nombre del empleado de acuerdo al documento
     * seleccionado, esto para el combo empleado inicial
     */
    private String nombreInicial;
    /**
     * Define nombre del empleado de acuerdo al documento
     * seleccionado, esto para el combo empleado final
     */
    private String nombreFinal;
    /**
     * Dado que el filtro se realiza por carpetas, al momento de
     * selecccionar registro se guarda en esta variable la carpeta
     * deldocumento seleccinado end el combo inicial
     */
    private String carpetaInicial;
    /**
     * Dado que el filtro se realiza por carpetas, al momento de
     * selecccionar registro se guarda en esta variable la carpeta
     * deldocumento seleccinado end el combo inicial
     */
    private String carpetaFinal;
    /**
     * Almacena el documento inicial seleccionado en el combo empleado
     * inicial del formulario
     */
    private String documentoInicial;
    /**
     * Almacena el documento inicial seleccionado en el combo empleado
     * final del formulario
     */
    private String documentoFinal;

    /**
     * Almacena los registros que se muestran en el combo empleado
     * inicial
     */
    private RegistroDataModelImpl listaCarpetaInicial;
    /**
     * Almacena los registros que se muestran en el combo empleado
     * final
     */
    private RegistroDataModelImpl listaCarpetaFinal;
    /**
     * Almacena los datos para realizar la corespondiente descarga del
     * formulario
     */
    private StreamedContent archivoDescarga;
    /**
     * Almacena el nombre "NUMEROCARPETA" dado que se utiliza en
     * varias ocasiones en el formulario.
     */
    private String nNumeroCarpeta;

    // </DECLARAR_LISTAS>
    // </DECLARAR_ATRIBUTOS>
    // </DECLARAR_PARAMETROS>

    public RptPublicacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
            numFormulario = GeneralCodigoFormaEnum.RPT_PUBLICACIONES_CONTROLADOR
                            .getCodigo();
            carpetaInicial = "0";
            carpetaFinal = "0";
            nNumeroCarpeta = "NUMEROCARPETA";
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        cargarListaCarpetaInicial();
        cargarListaCarpetaFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    public void cargarListaCarpetaInicial() {
        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptPublicacionesControladorUrlEnum.URL0002
                                                        .getValue());

        listaCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, nNumeroCarpeta);

    }

    public void cargarListaCarpetaFinal() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RptPublicacionesControladorEnum.PARAM0.getValue(),
                        carpetaInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptPublicacionesControladorUrlEnum.URL0001
                                                        .getValue());

        listaCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, nNumeroCarpeta);

    }

    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcell() {
        archivoDescarga = null;
        getInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void getInforme(ReportesBean.FORMATOS formato) {

        try {
            Map<String, Object> param = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("carpetaInicial", carpetaInicial);
            param.put("carpetaFinal", carpetaFinal);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("001560RhvPublicaciones",
                            Integer.valueOf(SessionUtil.getModulo()), param,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001560RhvPublicaciones",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        carpetaInicial = registroAux.getCampos().get(nNumeroCarpeta).toString();
        documentoInicial = registroAux.getCampos().get("NUMERO_DCTO")
                        .toString();
        nombreInicial = registroAux.getCampos().get("NOMBRES").toString();
        cargarListaCarpetaFinal();
    }

    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        documentoFinal = registroAux.getCampos().get("NUMERO_DCTO").toString();
        nombreFinal = registroAux.getCampos().get("NOMBRES").toString();
        carpetaFinal = registroAux.getCampos().get(nNumeroCarpeta).toString();
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public RegistroDataModelImpl getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    public void setListaCarpetaInicial(
        RegistroDataModelImpl listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    public RegistroDataModelImpl getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    public void setListaCarpetaFinal(RegistroDataModelImpl listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }

    public String getDocumentoInicial() {
        return documentoInicial;
    }

    public void setDocumentoInicial(String documentoInicial) {
        this.documentoInicial = documentoInicial;
    }

    public String getDocumentoFinal() {
        return documentoFinal;
    }

    public void setDocumentoFinal(String documentoFinal) {
        this.documentoFinal = documentoFinal;
    }

    public String getCarpetaInicial() {
        return carpetaInicial;
    }

    public void setCarpetaInicial(String carpetaInicial) {
        this.carpetaInicial = carpetaInicial;
    }

    public String getCarpetaFinal() {
        return carpetaFinal;
    }

    public void setCarpetaFinal(String carpetaFinal) {
        this.carpetaFinal = carpetaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
