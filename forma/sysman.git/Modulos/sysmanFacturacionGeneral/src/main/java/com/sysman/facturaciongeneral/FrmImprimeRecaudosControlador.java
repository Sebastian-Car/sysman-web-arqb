/*-
 * FrmImprimeRecaudosControlador.java
 *
 * 1.0
 * 
 * 10/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmImprimeRecaudosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Clase encargada de exportar informe de los comprobantes.
 *
 * @version 1.0, 10/11/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped

public class FrmImprimeRecaudosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * en el combo ano del formulario
     */
    private String ano;
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * en el combo Comprobante Inicial del formulario
     */
    private String comprobanteInicial;
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * en el combo Comprobante Final del formulario
     */
    private String comprobanteFinal;
    /**
     * Variable encargada de almancenar temporalmente lo seleccionado
     * en el combo Tipo Comprobante Inicial del formulario
     */
    private String tipoComp;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Variable encargada de almancenar temporalmente los datos de
     * respueta a la llamada de labase de datos para ser mostrados en
     * el combo ano
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almancenar temporalmente los datos de
     * respueta a la llamada de labase de datos para ser mostrados en
     * el combo Comprobante Inicial
     */
    private RegistroDataModelImpl listaComprobanteInicial;
    /**
     * Variable encargada de almancenar temporalmente los datos de
     * respueta a la llamada de labase de datos para ser mostrados en
     * el combo Comprobante Final
     */
    private RegistroDataModelImpl listaComprobanteFinal;
    /**
     * Variable encargada de almancenar temporalmente los datos de
     * respueta a la llamada de labase de datos para ser mostrados en
     * el combo Tipo Comprobante
     */
    private RegistroDataModelImpl listatipoComprobante;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Constante encargada de almacenar la constante NUMERO
     */
    private final String numeroCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmImprimeRecaudosControlador
     */
    public FrmImprimeRecaudosControlador() {
        super();
        compania = SessionUtil.getCompania();
        numeroCons = GeneralParameterEnum.NUMERO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMIMPRIMERECAUDOS_CONTROLADOR
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
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaComprobanteInicial();
        cargarListaComprobanteFinal();
        cargarListatipoComprobante();
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
     * Carga la lista listaAno
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar el resultado en ListaAno.
     */
    public void cargarListaAno() {

        // 72068

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmImprimeRecaudosControladorUrlEnum.URL5656
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaComprobanteInicial
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar el resultado en ListaComprobanteInicial
     */
    public void cargarListaComprobanteInicial() {

        // 72069 ANO TIPOCOMPROBANTE

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimeRecaudosControladorUrlEnum.URL6330
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("TIPOCOMPROBANTE", tipoComp);

        listaComprobanteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);

    }

    /**
     * 
     * Carga la lista listaComprobanteFinal
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar el resultado en ListaComprobanteFinal
     */
    public void cargarListaComprobanteFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimeRecaudosControladorUrlEnum.URL7342
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("TIPOCOMPROBANTE", tipoComp);
        param.put("COMPROBANTEINICIAL", comprobanteInicial);

        listaComprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);

        // 72071 TIPOCOMPROBANTE COMPROBANTEINICIAL
    }

    /**
     * 
     * Carga la lista listatipoComprobante
     *
     * Metodo encargado de hacer la llamada a la base de datos y
     * almacenar el resultado en ListaTipoComprobante
     */
    public void cargarListatipoComprobante() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimeRecaudosControladorUrlEnum.URL8413
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listatipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "TIPO");

        // 72073 COMPANIA ANO
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista y
     * encargado de la exportacion del infomre en formato PDF
     * 
     * 
     * 
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        genInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista y
     * encargado de la exportacion del infomre en formato EXCEL
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        genInforme(ReportesBean.FORMATOS.EXCEL);
    } //

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        tipoComp = null;
        comprobanteInicial = null;
        comprobanteFinal = null;
        cargarListatipoComprobante();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        comprobanteInicial = retornarString(registroAux, numeroCons);
        comprobanteFinal = null;
        cargarListaComprobanteFinal();
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
        comprobanteFinal = retornarString(registroAux, numeroCons);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoComp = retornarString(registroAux, "TIPO");

        comprobanteFinal = null;
        comprobanteInicial = null;
        cargarListaComprobanteInicial();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
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
     * Retorna la variable tipoComp
     * 
     * @return tipoComp
     */
    public String getTipoComp() {
        return tipoComp;
    }

    /**
     * Asigna la variable tipoComp
     * 
     * @param tipoComp
     * Variable a asignar en tipoComp
     */
    public void setTipoComp(String tipoComp) {
        this.tipoComp = tipoComp;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
     * Retorna la lista listatipoComprobante
     * 
     * @return listatipoComprobante
     */
    public RegistroDataModelImpl getListatipoComprobante() {
        return listatipoComprobante;
    }

    /**
     * Asigna la lista listatipoComprobante
     * 
     * @param listatipoComprobante
     * Variable a asignar en listatipoComprobante
     */
    public void setListatipoComprobante(
        RegistroDataModelImpl listatipoComprobante) {
        this.listatipoComprobante = listatipoComprobante;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        try {

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("tipo", tipoComp);
            reemplazar.put("comprobanteInicial", comprobanteInicial);
            reemplazar.put("comprobanteFinal", comprobanteFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_VISIBLECUADROS", true);

            Reporteador.resuelveConsulta("001491INFRECAUDO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001491INFRECAUDO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
