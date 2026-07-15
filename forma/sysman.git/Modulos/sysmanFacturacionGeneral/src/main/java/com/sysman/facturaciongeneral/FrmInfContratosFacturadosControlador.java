/*-
 * FrmInfContratosFacturadosControlador.java
 *
 * 1.0
 * 
 * 7/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.facturaciongeneral.enums.FrmInfContratosFacturadosControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmInfContratosFacturadosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera los informes de estados de facturacion a
 * contratos
 *
 * @version 1.0, 07/11/2017, Proceso de migracion Access a Web y
 * Refactoring DSS
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmInfContratosFacturadosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el numero del modulo por el cual el
     * usuario inicio sesion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del contrato inicial
     * seleccionado en la vista
     */
    private String contratoInicial;
    /**
     * Atributo que almacena el valor del contrato final seleccionado
     * en la vista
     */
    private String contratoFinal;
    /**
     * Atributo que almacena el valor del tercero inicial seleccionado
     * en la vista
     */
    private String terceroInicial;
    /**
     * Atributo que almacena el valor del tercero final seleccionado
     * en la vista
     */
    private String terceroFinal;

    /**
     * Atributo que almacena el valor de la fecha inicial seleccionada
     * en la vista
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena el valor de la fecha final seleccionada
     * en la vista
     */
    private Date fechaFinal;

    private String tipoFormato;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private String numeroContratoInicial;

    private String tipoContratoInicial;

    private String numeroContratoFinal;

    private String tipoContratoFinal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista que almacena los contratos
     */
    private RegistroDataModelImpl listaContratoInicial;
    /**
     * lista que almacena los contratos a partir de la lista inicial
     */
    private RegistroDataModelImpl listaContratoFinal;
    /**
     * lista que almacena los terceros
     */
    private RegistroDataModelImpl listaTerceroInicial;
    /**
     * lista que almacena los terceros a partir del tercero incial
     */
    private RegistroDataModelImpl listaTerceroFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmInfContratosFacturadosControlador
     */
    public FrmInfContratosFacturadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INF_CONTRATOS_FACTURADOS_CONTROLADOR
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
        cargarListaContratoInicial();

        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
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
        fechaInicial = new Date();
        fechaFinal = new Date();

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaContratoInicial
     *
     */
    public void cargarListaContratoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfContratosFacturadosControladorUrlEnum.URL5656
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmInfContratosFacturadosControladorEnum.CONTRATO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaContratoFinal
     *
     */
    public void cargarListaContratoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfContratosFacturadosControladorUrlEnum.URL6562
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(FrmInfContratosFacturadosControladorEnum.TIPOCONTRATO
                        .getValue(),
                        tipoContratoInicial);

        param.put(FrmInfContratosFacturadosControladorEnum.NUMEROCONTRATO
                        .getValue(), numeroContratoInicial);

        listaContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmInfContratosFacturadosControladorEnum.CONTRATO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaTerceroInicial
     *
     */
    public void cargarListaTerceroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfContratosFacturadosControladorUrlEnum.URL7442
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaTerceroFinal
     *
     */
    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfContratosFacturadosControladorUrlEnum.URL7892
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(FrmInfContratosFacturadosControladorEnum.TERCEROINICIAL
                        .getValue(), terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdExcel en la vista
     *
     *
     */
    public void oprimircmdExcel() {
        archivoDescarga = null;
        try {
            String condicionFormato = "";

            if ("1".equals(tipoFormato)) {
                condicionFormato = "AND SF_FACTURA.FECHA_PAGO IS NOT NULL";
            }
            else if ("2".equals(tipoFormato)) {
                condicionFormato = "AND SF_FACTURA.FECHA_PAGO IS NULL";
            }

            Map<String, Object> reemplazos = new TreeMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("condicionFormato", condicionFormato);
            reemplazos.put("tipoCobro", SessionUtil.getSessionVar(
                            ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()));
            reemplazos.put("tipoInicial", tipoContratoInicial);
            reemplazos.put("tipoFinal", tipoContratoFinal);
            reemplazos.put("numeroInicial", numeroContratoInicial);
            reemplazos.put("numeroFinal", numeroContratoFinal);
            reemplazos.put("terceroInicial", terceroInicial);
            reemplazos.put("terceroFinal", terceroFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            String sql = Reporteador.resuelveConsulta(
                            "800126ContratosFacturados",
                            Integer.parseInt(modulo),
                            reemplazos);

            if (comprobarConsulta(sql)) {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
                                sql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                                "Contratos_Facturados.xls");
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private boolean comprobarConsulta(String sql) {
        try {
            return service.getConteoConsulta(sql) > 0;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaContratoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmInfContratosFacturadosControladorEnum.CONTRATO
                                                        .getValue()),
                                        "")
                        .toString();

        tipoContratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO"), "")
                        .toString();

        numeroContratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();

        cargarListaContratoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaContratoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmInfContratosFacturadosControladorEnum.CONTRATO
                                                        .getValue()),
                                        "")
                        .toString();

        tipoContratoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO"), "")
                        .toString();

        numeroContratoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();

        cargarListaTerceroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable contratoInicial
     * 
     * @return contratoInicial
     */
    public String getContratoInicial() {
        return contratoInicial;
    }

    /**
     * Asigna la variable contratoInicial
     * 
     * @param contratoInicial
     * Variable a asignar en contratoInicial
     */
    public void setContratoInicial(String contratoInicial) {
        this.contratoInicial = contratoInicial;
    }

    /**
     * Retorna la variable contratoFinal
     * 
     * @return contratoFinal
     */
    public String getContratoFinal() {
        return contratoFinal;
    }

    /**
     * Asigna la variable contratoFinal
     * 
     * @param contratoFinal
     * Variable a asignar en contratoFinal
     */
    public void setContratoFinal(String contratoFinal) {
        this.contratoFinal = contratoFinal;
    }

    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public String getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    public String getNumeroContratoInicial() {
        return numeroContratoInicial;
    }

    public void setNumeroContratoInicial(String numeroContratoInicial) {
        this.numeroContratoInicial = numeroContratoInicial;
    }

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getNumeroContratoFinal() {
        return numeroContratoFinal;
    }

    public void setNumeroContratoFinal(String numeroContratoFinal) {
        this.numeroContratoFinal = numeroContratoFinal;
    }

    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaContratoInicial
     * 
     * @return listaContratoInicial
     */
    public RegistroDataModelImpl getListaContratoInicial() {
        return listaContratoInicial;
    }

    /**
     * Asigna la lista listaContratoInicial
     * 
     * @param listaContratoInicial
     * Variable a asignar en listaContratoInicial
     */
    public void setListaContratoInicial(
        RegistroDataModelImpl listaContratoInicial) {
        this.listaContratoInicial = listaContratoInicial;
    }

    /**
     * Retorna la lista listaContratoFinal
     * 
     * @return listaContratoFinal
     */
    public RegistroDataModelImpl getListaContratoFinal() {
        return listaContratoFinal;
    }

    /**
     * Asigna la lista listaContratoFinal
     * 
     * @param listaContratoFinal
     * Variable a asignar en listaContratoFinal
     */
    public void setListaContratoFinal(
        RegistroDataModelImpl listaContratoFinal) {
        this.listaContratoFinal = listaContratoFinal;
    }

    /**
     * Retorna la lista listaTerceroInicial
     * 
     * @return listaTerceroInicial
     */
    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    /**
     * Asigna la lista listaTerceroInicial
     * 
     * @param listaTerceroInicial
     * Variable a asignar en listaTerceroInicial
     */
    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    /**
     * Retorna la lista listaTerceroFinal
     * 
     * @return listaTerceroFinal
     */
    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    /**
     * Asigna la lista listaTerceroFinal
     * 
     * @param listaTerceroFinal
     * Variable a asignar en listaTerceroFinal
     */
    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
