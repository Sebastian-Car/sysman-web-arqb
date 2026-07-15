/*-
 * MunicipioControlador.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.MunicipioControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Clase encargada de generar informe de viaticos por municipo
 *
 * @version 1.0, 18/01/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped

public class MunicipioControlador extends BeanBaseModal {

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo pais Inicial
     */
    private String paisInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo departamento Inicial
     */
    private String departamentoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo ciudad Inicial
     */
    private String ciudadInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo pais final
     */
    private String paisFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo departamento final
     */
    private String departamentoFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo ciudad final
     */
    private String ciudadFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en le campo fecha incial
     */
    private Date fechaInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en le campo fecha Final
     */
    private Date fechaFinal;
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
     * Lista encargada de almacenar temporalmente los datos de resputa
     * de la base datos a la tabla pais
     */
    private RegistroDataModelImpl listaPaisInicial;
    /**
     * Lista encargada de almacenar temporalmente los datos de resputa
     * de la base datos a la tabla departamento
     */
    private RegistroDataModelImpl listaDepartamentoInicial;
    /**
     * Lista encargada de almacenar temporalmente los datos de resputa
     * de la base datos a la tabla ciudad
     */
    private RegistroDataModelImpl listaCmbCiudadIn;
    /**
     * Lista encargada de almacenar temporalmente los datos de resputa
     * de la base datos a la tabla pais
     */
    private RegistroDataModelImpl listaCmbPaisFin;
    /**
     * Lista encargada de almacenar temporalmente los datos de resputa
     * de la base datos a la tabla departamento
     */
    private RegistroDataModelImpl listaDepartamentoFinal;
    /**
     * Lista encargada de almacenar temporalmente los datos de resputa
     * de la base datos a la tabla ciudad
     */
    private RegistroDataModelImpl listaCiudadFinal;

    /**
     * 
     */
    private String nombrePaisIni;
    private String nombreDepIni;
    private String nombreCiudadIni;

    private String nombrePaisFin;
    private String nombreDepFin;
    private String nombreCiudadFin;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de MunicipioControlador
     */

    public MunicipioControlador() {
        super();

        try {
            numFormulario = GeneralCodigoFormaEnum.MUNICIPIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            fechaFinal = new Date();
            fechaInicial = new Date();

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
        cargarListaPaisInicial();
        cargarListaDepartamentoInicial();
        cargarListaCmbCiudadIn();
        cargarListaCmbPaisFin();
        cargarListaDepartamentoFinal();
        cargarListaCiudadFinal();
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
     * Carga la lista listaPaisInicial
     *
     * Metodo encargado de hacer el llamado a la tabla pais por medio
     * del Dss y almacenar la respuesta en la lista listaPaisInicial
     */
    public void cargarListaPaisInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MunicipioControladorUrlEnum.URL5331
                                                        .getValue());
        listaPaisInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 1003
    }

    /**
     * 
     * Carga la lista listaDepartamentoInicial
     *
     * Metodo encargado de hacer el llamado a la tabla pais por medio
     * del Dss y almacenar la respuesta en la lista
     * listaDepartamentoInicial
     */
    public void cargarListaDepartamentoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MunicipioControladorUrlEnum.URL5893
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisInicial);

        listaDepartamentoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // PAISINICIAL 2007
    }

    /**
     * 
     * Carga la lista listaCmbCiudadIn
     *
     * Metodo encargado de hacer el llamado a la tabla pais por medio
     * del Dss y almacenar la respuesta en la lista listaCmbCiudadIn
     */
    public void cargarListaCmbCiudadIn() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MunicipioControladorUrlEnum.URL6498
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("DEPARTAMENTO", String.valueOf(departamentoInicial));
        param.put("PAIS", String.valueOf(paisInicial));

        listaCmbCiudadIn = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 5007
    }

    /**
     * 
     * Carga la lista listaCmbPaisFin
     *
     * Metodo encargado de hacer el llamado a la tabla pais por medio
     * del Dss y almacenar la respuesta en la lista listaCmbPaisFin
     */
    public void cargarListaCmbPaisFin() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MunicipioControladorUrlEnum.URL5331
                                                        .getValue());
        listaCmbPaisFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 1003
    }

    /**
     * 
     * Carga la lista listaDepartamentoFinal
     *
     * Metodo encargado de hacer el llamado a la tabla pais por medio
     * del Dss y almacenar la respuesta en la lista
     * listaDepartamentoFinal
     */
    public void cargarListaDepartamentoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MunicipioControladorUrlEnum.URL5893
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisFinal);

        listaDepartamentoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // PAISINICIAL 2007
    }

    /**
     * 
     * Carga la lista listaCiudadFinal
     *
     * Metodo encargado de hacer el llamado a la tabla pais por medio
     * del Dss y almacenar la respuesta en la lista listaCiudadFinal
     */
    public void cargarListaCiudadFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MunicipioControladorUrlEnum.URL6498
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("DEPARTAMENTO", String.valueOf(departamentoFinal));
        param.put("PAIS", String.valueOf(paisFinal));

        listaCiudadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

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
        genInforme(FORMATOS.PDF);
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
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPaisInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPaisInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        paisInicial = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombrePaisIni = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

        departamentoInicial = null;
        ciudadInicial = null;
        cargarListaDepartamentoInicial();
        cargarListaCmbCiudadIn();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDepartamentoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDepartamentoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        departamentoInicial = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombreDepIni = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

        ciudadInicial = null;
        cargarListaCmbCiudadIn();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCiudadIn
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCiudadIn(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciudadInicial = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombreCiudadIni = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbPaisFin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbPaisFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        paisFinal = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombrePaisFin = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

        departamentoFinal = null;
        ciudadFinal = null;

        cargarListaDepartamentoFinal();
        cargarListaCmbCiudadIn();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDepartamentoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDepartamentoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        departamentoFinal = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreDepFin = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

        ciudadFinal = null;
        cargarListaCiudadFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCiudadFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiudadFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciudadFinal = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreCiudadFin = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable paisInicial
     * 
     * @return paisInicial
     */
    public String getPaisInicial() {
        return paisInicial;
    }

    /**
     * Asigna la variable paisInicial
     * 
     * @param paisInicial
     * Variable a asignar en paisInicial
     */
    public void setPaisInicial(String paisInicial) {
        this.paisInicial = paisInicial;
    }

    /**
     * Retorna la variable departamentoInicial
     * 
     * @return departamentoInicial
     */
    public String getDepartamentoInicial() {
        return departamentoInicial;
    }

    /**
     * Asigna la variable departamentoInicial
     * 
     * @param departamentoInicial
     * Variable a asignar en departamentoInicial
     */
    public void setDepartamentoInicial(String departamentoInicial) {
        this.departamentoInicial = departamentoInicial;
    }

    /**
     * Retorna la variable ciudadInicial
     * 
     * @return ciudadInicial
     */
    public String getCiudadInicial() {
        return ciudadInicial;
    }

    /**
     * Asigna la variable ciudadInicial
     * 
     * @param ciudadInicial
     * Variable a asignar en ciudadInicial
     */
    public void setCiudadInicial(String ciudadInicial) {
        this.ciudadInicial = ciudadInicial;
    }

    /**
     * Retorna la variable paisFinal
     * 
     * @return paisFinal
     */
    public String getPaisFinal() {
        return paisFinal;
    }

    /**
     * Asigna la variable paisFinal
     * 
     * @param paisFinal
     * Variable a asignar en paisFinal
     */
    public void setPaisFinal(String paisFinal) {
        this.paisFinal = paisFinal;
    }

    /**
     * Retorna la variable departamentoFinal
     * 
     * @return departamentoFinal
     */
    public String getDepartamentoFinal() {
        return departamentoFinal;
    }

    /**
     * Asigna la variable departamentoFinal
     * 
     * @param departamentoFinal
     * Variable a asignar en departamentoFinal
     */
    public void setDepartamentoFinal(String departamentoFinal) {
        this.departamentoFinal = departamentoFinal;
    }

    /**
     * Retorna la variable ciudadFinal
     * 
     * @return ciudadFinal
     */
    public String getCiudadFinal() {
        return ciudadFinal;
    }

    /**
     * Asigna la variable ciudadFinal
     * 
     * @param ciudadFinal
     * Variable a asignar en ciudadFinal
     */
    public void setCiudadFinal(String ciudadFinal) {
        this.ciudadFinal = ciudadFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaPaisInicial
     * 
     * @return listaPaisInicial
     */
    public RegistroDataModelImpl getListaPaisInicial() {
        return listaPaisInicial;
    }

    /**
     * Asigna la lista listaPaisInicial
     * 
     * @param listaPaisInicial
     * Variable a asignar en listaPaisInicial
     */
    public void setListaPaisInicial(RegistroDataModelImpl listaPaisInicial) {
        this.listaPaisInicial = listaPaisInicial;
    }

    /**
     * Retorna la lista listaDepartamentoInicial
     * 
     * @return listaDepartamentoInicial
     */
    public RegistroDataModelImpl getListaDepartamentoInicial() {
        return listaDepartamentoInicial;
    }

    /**
     * Asigna la lista listaDepartamentoInicial
     * 
     * @param listaDepartamentoInicial
     * Variable a asignar en listaDepartamentoInicial
     */
    public void setListaDepartamentoInicial(
        RegistroDataModelImpl listaDepartamentoInicial) {
        this.listaDepartamentoInicial = listaDepartamentoInicial;
    }

    /**
     * Retorna la lista listaCmbCiudadIn
     * 
     * @return listaCmbCiudadIn
     */
    public RegistroDataModelImpl getListaCmbCiudadIn() {
        return listaCmbCiudadIn;
    }

    /**
     * Asigna la lista listaCmbCiudadIn
     * 
     * @param listaCmbCiudadIn
     * Variable a asignar en listaCmbCiudadIn
     */
    public void setListaCmbCiudadIn(RegistroDataModelImpl listaCmbCiudadIn) {
        this.listaCmbCiudadIn = listaCmbCiudadIn;
    }

    /**
     * Retorna la lista listaCmbPaisFin
     * 
     * @return listaCmbPaisFin
     */
    public RegistroDataModelImpl getListaCmbPaisFin() {
        return listaCmbPaisFin;
    }

    /**
     * Asigna la lista listaCmbPaisFin
     * 
     * @param listaCmbPaisFin
     * Variable a asignar en listaCmbPaisFin
     */
    public void setListaCmbPaisFin(RegistroDataModelImpl listaCmbPaisFin) {
        this.listaCmbPaisFin = listaCmbPaisFin;
    }

    /**
     * Retorna la lista listaDepartamentoFinal
     * 
     * @return listaDepartamentoFinal
     */
    public RegistroDataModelImpl getListaDepartamentoFinal() {
        return listaDepartamentoFinal;
    }

    /**
     * Asigna la lista listaDepartamentoFinal
     * 
     * @param listaDepartamentoFinal
     * Variable a asignar en listaDepartamentoFinal
     */
    public void setListaDepartamentoFinal(
        RegistroDataModelImpl listaDepartamentoFinal) {
        this.listaDepartamentoFinal = listaDepartamentoFinal;
    }

    /**
     * Retorna la lista listaCiudadFinal
     * 
     * @return listaCiudadFinal
     */
    public RegistroDataModelImpl getListaCiudadFinal() {
        return listaCiudadFinal;
    }

    public String getNombrePaisIni() {
        return nombrePaisIni;
    }

    public void setNombrePaisIni(String nombrePaisIni) {
        this.nombrePaisIni = nombrePaisIni;
    }

    public String getNombreDepIni() {
        return nombreDepIni;
    }

    public void setNombreDepIni(String nombreDepIni) {
        this.nombreDepIni = nombreDepIni;
    }

    public String getNombreCiudadIni() {
        return nombreCiudadIni;
    }

    public void setNombreCiudadIni(String nombreCiudadIni) {
        this.nombreCiudadIni = nombreCiudadIni;
    }

    public String getNombrePaisFin() {
        return nombrePaisFin;
    }

    public void setNombrePaisFin(String nombrePaisFin) {
        this.nombrePaisFin = nombrePaisFin;
    }

    public String getNombreDepFin() {
        return nombreDepFin;
    }

    public void setNombreDepFin(String nombreDepFin) {
        this.nombreDepFin = nombreDepFin;
    }

    public String getNombreCiudadFin() {
        return nombreCiudadFin;
    }

    public void setNombreCiudadFin(String nombreCiudadFin) {
        this.nombreCiudadFin = nombreCiudadFin;
    }

    /**
     * Asigna la lista listaCiudadFinal
     * 
     * @param listaCiudadFinal
     * Variable a asignar en listaCiudadFinal
     */
    public void setListaCiudadFinal(RegistroDataModelImpl listaCiudadFinal) {
        this.listaCiudadFinal = listaCiudadFinal;

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    public void genInforme(ReportesBean.FORMATOS formato) {

        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("paisInicial", paisInicial);
        reemplazar.put("paisFinal", paisFinal);
        reemplazar.put("departamentoInicial", departamentoInicial);
        reemplazar.put("departamentoFinal", departamentoFinal);
        reemplazar.put("ciudadInicial", ciudadInicial);
        reemplazar.put("ciudadFinal", ciudadFinal);
        reemplazar.put("fechaIncial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));

        Map<String, Object> parametros = new HashMap<>();

        Reporteador.resuelveConsulta("001653InfMunicipio",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001653InfMunicipio", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
