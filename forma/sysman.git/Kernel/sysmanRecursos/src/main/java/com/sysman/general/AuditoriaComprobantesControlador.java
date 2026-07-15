/*-
 * AuditoriaComprobantesControlador.java
 *
 * 1.0
 * 
 * 30/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.AuditoriaComprobantesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Genera informe de audiroría por comprobantes.
 *
 * @version 1.0, 30/11/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class AuditoriaComprobantesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String anoInicial;
    private String anoFinal;
    private String tipoInicial;
    private String tipoFinal;
    private String comprobanteInicial;
    private String comprobanteFinal;
    private String usuarioInicial;
    private String usuarioFinal;

    private boolean indAno;
    private boolean indTipo;
    private boolean indComprobante;
    private boolean indUsuario;

    private boolean verAno;
    private boolean verTipo;
    private boolean verComprobante;
    private boolean verUsuario;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnoInicial;
    private List<Registro> listaAnoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaComprobanteInicial;
    private RegistroDataModelImpl listaComprobanteFinal;
    private RegistroDataModelImpl listaUsuarioInicial;
    private RegistroDataModelImpl listaUsuarioFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AuditoriaComprobantesControlador
     */
    public AuditoriaComprobantesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2000
            numFormulario = GeneralCodigoFormaEnum.AUDITORIA_COMPROBANTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            indAno = true;
            indComprobante = true;
            indTipo = true;
            indUsuario = true;
            verAno = true;
            verTipo = true;
            verComprobante = true;
            verUsuario = true;
            anoInicial = String.valueOf(SysmanFunciones.ano(new Date()));
            anoFinal = String.valueOf(SysmanFunciones.ano(new Date()));
            // usuarioInicial = SessionUtil.getUser().toString();
            // usuarioFinal = SessionUtil.getUser().toString();

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

        cargarListaAnoInicial();
        cargarListaAnoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
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
     * Carga la lista listaAnoInicial
     *
     */
    public void cargarListaAnoInicial() {

        try {
            Map<String, Object> parametros = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnoInicial = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            AuditoriaComprobantesControladorUrlEnum.URL002
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaAnoFinal
     *
     */
    public void cargarListaAnoFinal() {

        try {
            Map<String, Object> parametros = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ANO.getName(), anoInicial);

            listaAnoFinal = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            AuditoriaComprobantesControladorUrlEnum.URL2288
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoInicial
     *
     */
    public void cargarListaTipoInicial() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if ("1".equals(SessionUtil.getModulo())) {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL146
                                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL144
                                                            .getValue());
        }

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTipoFinal
     *
     */
    public void cargarListaTipoFinal() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", tipoInicial);
        param.put("TIPOINICIAL", tipoInicial);

        if ("1".equals(SessionUtil.getModulo())) {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL145
                                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL146
                                                            .getValue());

        }

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaComprobanteInicial
     *
     */
    public void cargarListaComprobanteInicial() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOINICIAL", anoInicial);
        param.put("ANOFINAL", anoFinal);
        param.put("TIPOINICIAL", tipoInicial);
        param.put("TIPOFINAL", tipoFinal);

        if ("1".equals(SessionUtil.getModulo())) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL154
                                                            .getValue());
        }
        else {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL148
                                                            .getValue());
        }

        listaComprobanteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * 
     * Carga la lista listaComprobanteFinal
     *
     */
    public void cargarListaComprobanteFinal() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOINICIAL", anoInicial);
        param.put("ANOFINAL", anoFinal);
        param.put("TIPOINICIAL", tipoInicial);
        param.put("TIPOFINAL", tipoFinal);
        param.put("NUMEROINICIAL", comprobanteInicial);

        if ("1".equals(SessionUtil.getModulo())) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL155
                                                            .getValue());
        }
        else {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL149
                                                            .getValue());
        }

        listaComprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listaUsuarioInicial
     *
     */
    public void cargarListaUsuarioInicial() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOINICIAL", anoInicial);
        param.put("ANOFINAL", anoFinal);
        param.put("TIPOINICIAL", tipoInicial);
        param.put("TIPOFINAL", tipoFinal);
        param.put("NUMEROINICIAL", comprobanteInicial);
        param.put("NUMEROFINAL", comprobanteFinal);

        if ("1".equals(SessionUtil.getModulo())) {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL156
                                                            .getValue());
        }
        else {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL150
                                                            .getValue());
        }

        listaUsuarioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.USUARIO.getName());

    }

    /**
     * 
     * Carga la lista listaUsuarioFinal
     *
     */
    public void cargarListaUsuarioFinal() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOINICIAL", anoInicial);
        param.put("ANOFINAL", anoFinal);
        param.put("TIPOINICIAL", tipoInicial);
        param.put("TIPOFINAL", tipoFinal);
        param.put("NUMEROINICIAL", comprobanteInicial);
        param.put("NUMEROFINAL", comprobanteFinal);
        param.put("USUARIOINICIAL", usuarioInicial);

        if ("1".equals(SessionUtil.getModulo())) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL157
                                                            .getValue());
        }
        else {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AuditoriaComprobantesControladorUrlEnum.URL151
                                                            .getValue());
        }

        listaUsuarioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.USUARIO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);

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
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        if (indAno) {
            verAno = true;
        }
        else {
            verAno = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formatos) {

        try {

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("tabla",
                            "1".equals(SessionUtil.getModulo().toString())
                                ? GenericUrlEnum.COMPROBANTE_CNT.getTable()
                                : GenericUrlEnum.COMPROBANTE_PPTAL.getTable());
            reemplazar.put("anio", indAno ? -1 : 0);
            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("anoFinal", anoFinal);
            reemplazar.put("tipo", indTipo ? -1 : 0);
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("comprobante", indComprobante ? -1 : 0);
            reemplazar.put("comprobanteInicial",
                            indComprobante ? comprobanteInicial : 0);
            reemplazar.put("comprobanteFinal",
                            indComprobante ? comprobanteFinal : 0);
            reemplazar.put("usuario", indUsuario ? -1 : 0);
            reemplazar.put("usuarioInicial", usuarioInicial);
            reemplazar.put("usuarioFinal", usuarioFinal);

            String sql = Reporteador.resuelveConsulta(
                            "001970auditoriaComprobantes",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_STRSQL", sql);

            archivoDescarga = JsfUtil.exportarStreamed(

                            "001970auditoriaComprobantes", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Tipo
     * 
     * 
     */
    public void cambiarTipo() {
        // <CODIGO_DESARROLLADO>
        if (indTipo) {
            verTipo = true;

        }
        else {
            verTipo = false;
            tipoInicial = null;
            tipoFinal = null;

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Comprobante
     * 
     * 
     */
    public void cambiarComprobante() {
        // <CODIGO_DESARROLLADO>
        if (indComprobante) {
            verComprobante = true;
            indTipo = true;
            verTipo = true;
        }
        else {

            verComprobante = false;
            comprobanteInicial = null;
            comprobanteFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Usuario
     * 
     * 
     */
    public void cambiarUsuario() {
        // <CODIGO_DESARROLLADO>
        if (indUsuario) {
            verUsuario = true;
        }
        else {
            verUsuario = false;
            usuarioInicial = null;
            usuarioFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>

    public void cambiarAnoInicial() {
        anoFinal = null;
        tipoInicial = null;
        tipoFinal = null;
        comprobanteInicial = null;
        comprobanteFinal = null;
        usuarioInicial = null;
        usuarioFinal = null;
        cargarListaAnoFinal();
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaComprobanteInicial();
        cargarListaComprobanteFinal();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
    }

    public void cambiarAnoFinal() {
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaComprobanteInicial();
        cargarListaComprobanteFinal();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        tipoFinal = null;
        cargarListaTipoFinal();
        cargarListaComprobanteInicial();
        cargarListaComprobanteFinal();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        cargarListaComprobanteInicial();
        cargarListaComprobanteFinal();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaComprobanteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaComprobanteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobanteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
        cargarListaComprobanteFinal();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaComprobanteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaComprobanteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobanteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
        cargarListaUsuarioInicial();
        cargarListaUsuarioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsuarioInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsuarioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usuarioInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("USUARIO"), "")
                        .toString();
        cargarListaUsuarioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsuarioFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsuarioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usuarioFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("USUARIO"), "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anoInicial
     * 
     * @return anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     * 
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable anoFinal
     * 
     * @return anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     * 
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable tipoInicial
     * 
     * @return tipoInicial
     */
    public String getTipoInicial() {
        return tipoInicial;
    }

    /**
     * Asigna la variable tipoInicial
     * 
     * @param tipoInicial
     * Variable a asignar en tipoInicial
     */
    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    /**
     * Retorna la variable tipoFinal
     * 
     * @return tipoFinal
     */
    public String getTipoFinal() {
        return tipoFinal;
    }

    /**
     * Asigna la variable tipoFinal
     * 
     * @param tipoFinal
     * Variable a asignar en tipoFinal
     */
    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    /**
     * Retorna la variable comprobanteInicial
     * 
     * @return comprobanteInicial
     */
    public String getComprobanteInicial() {
        return comprobanteInicial;
    }

    /**
     * Asigna la variable comprobanteInicial
     * 
     * @param comprobanteInicial
     * Variable a asignar en comprobanteInicial
     */
    public void setComprobanteInicial(String comprobanteInicial) {
        this.comprobanteInicial = comprobanteInicial;
    }

    /**
     * Retorna la variable comprobanteFinal
     * 
     * @return comprobanteFinal
     */
    public String getComprobanteFinal() {
        return comprobanteFinal;
    }

    /**
     * Asigna la variable comprobanteFinal
     * 
     * @param comprobanteFinal
     * Variable a asignar en comprobanteFinal
     */
    public void setComprobanteFinal(String comprobanteFinal) {
        this.comprobanteFinal = comprobanteFinal;
    }

    /**
     * Retorna la variable usuarioInicial
     * 
     * @return usuarioInicial
     */
    public String getUsuarioInicial() {
        return usuarioInicial;
    }

    /**
     * Asigna la variable usuarioInicial
     * 
     * @param usuarioInicial
     * Variable a asignar en usuarioInicial
     */
    public void setUsuarioInicial(String usuarioInicial) {
        this.usuarioInicial = usuarioInicial;
    }

    /**
     * Retorna la variable usuarioFinal
     * 
     * @return usuarioFinal
     */
    public String getUsuarioFinal() {
        return usuarioFinal;
    }

    /**
     * Asigna la variable usuarioFinal
     * 
     * @param usuarioFinal
     * Variable a asignar en usuarioFinal
     */
    public void setUsuarioFinal(String usuarioFinal) {
        this.usuarioFinal = usuarioFinal;
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
    /**
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     * 
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     * 
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     * 
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoInicial
     * 
     * @return listaTipoInicial
     */
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    /**
     * Asigna la lista listaTipoInicial
     * 
     * @param listaTipoInicial
     * Variable a asignar en listaTipoInicial
     */
    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    /**
     * Retorna la lista listaTipoFinal
     * 
     * @return listaTipoFinal
     */
    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    /**
     * Asigna la lista listaTipoFinal
     * 
     * @param listaTipoFinal
     * Variable a asignar en listaTipoFinal
     */
    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    /**
     * Retorna la lista listaComprobanteInicial
     * 
     * @return listaComprobanteInicial
     */
    public RegistroDataModelImpl getListaComprobanteInicial() {
        return listaComprobanteInicial;
    }

    /**
     * Asigna la lista listaComprobanteInicial
     * 
     * @param listaComprobanteInicial
     * Variable a asignar en listaComprobanteInicial
     */
    public void setListaComprobanteInicial(
        RegistroDataModelImpl listaComprobanteInicial) {
        this.listaComprobanteInicial = listaComprobanteInicial;
    }

    /**
     * Retorna la lista listaComprobanteFinal
     * 
     * @return listaComprobanteFinal
     */
    public RegistroDataModelImpl getListaComprobanteFinal() {
        return listaComprobanteFinal;
    }

    /**
     * Asigna la lista listaComprobanteFinal
     * 
     * @param listaComprobanteFinal
     * Variable a asignar en listaComprobanteFinal
     */
    public void setListaComprobanteFinal(
        RegistroDataModelImpl listaComprobanteFinal) {
        this.listaComprobanteFinal = listaComprobanteFinal;
    }

    /**
     * Retorna la lista listaUsuarioInicial
     * 
     * @return listaUsuarioInicial
     */
    public RegistroDataModelImpl getListaUsuarioInicial() {
        return listaUsuarioInicial;
    }

    /**
     * Asigna la lista listaUsuarioInicial
     * 
     * @param listaUsuarioInicial
     * Variable a asignar en listaUsuarioInicial
     */
    public void setListaUsuarioInicial(
        RegistroDataModelImpl listaUsuarioInicial) {
        this.listaUsuarioInicial = listaUsuarioInicial;
    }

    /**
     * Retorna la lista listaUsuarioFinal
     * 
     * @return listaUsuarioFinal
     */
    public RegistroDataModelImpl getListaUsuarioFinal() {
        return listaUsuarioFinal;
    }

    /**
     * Asigna la lista listaUsuarioFinal
     * 
     * @param listaUsuarioFinal
     * Variable a asignar en listaUsuarioFinal
     */
    public void setListaUsuarioFinal(RegistroDataModelImpl listaUsuarioFinal) {
        this.listaUsuarioFinal = listaUsuarioFinal;
    }

    public boolean isIndAno() {
        return indAno;
    }

    public void setIndAno(boolean indAno) {
        this.indAno = indAno;
    }

    public boolean isIndTipo() {
        return indTipo;
    }

    public void setIndTipo(boolean indTipo) {
        this.indTipo = indTipo;
    }

    public boolean isIndComprobante() {
        return indComprobante;
    }

    public void setIndComprobante(boolean indComprobante) {
        this.indComprobante = indComprobante;
    }

    public boolean isIndUsuario() {
        return indUsuario;
    }

    public void setIndUsuario(boolean indUsuario) {
        this.indUsuario = indUsuario;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVerAno() {
        return verAno;
    }

    public void setVerAno(boolean verAno) {
        this.verAno = verAno;
    }

    public boolean isVerTipo() {
        return verTipo;
    }

    public void setVerTipo(boolean verTipo) {
        this.verTipo = verTipo;
    }

    public boolean isVerComprobante() {
        return verComprobante;
    }

    public void setVerComprobante(boolean verComprobante) {
        this.verComprobante = verComprobante;
    }

    public boolean isVerUsuario() {
        return verUsuario;
    }

    public void setVerUsuario(boolean verUsuario) {
        this.verUsuario = verUsuario;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
