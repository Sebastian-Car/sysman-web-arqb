/*-
 * LisAuxiliarSaldosCGRControlador.java
 *
 * 1.0
 * 
 * 4/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.LisAuxiliarSaldosCGRControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera el reporte de saldos auxiliares CGR
 *
 * @version 1.0, 04/06/2021
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class LisAuxiliarSaldosCGRControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que alamcena el tipo inicial
     */
    private String tipoInicial;
    /**
     * Variable que alamcena el tipo inicial
     */
    private String tipoFinal;
    /**
     * Variable que almacena la cuenta inicial
     */
    private String cuentaInicial;
    /**
     * Variable que almacena la cuenta final
     */
    private String cuentaFinal;
    /**
     * Variable que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la fecha final
     */
    private Date fechaFinal;

    /**
     * Variable que almacena el anio de la fecha inicial
     */
    private int anio;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private String centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lsita que carga los tipos iniciales
     */
    private RegistroDataModelImpl listaTipoInicial;
    /**
     * Lsita que carga los tipos finales
     */
    private RegistroDataModelImpl listaTipoFinal;
    /**
     * Lista que carga las cuentas iniciales
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista que carga las cuentas finales
     */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LisAuxiliarSaldosCGRControlador
     */
    public LisAuxiliarSaldosCGRControlador() {
        super();
        compania = SessionUtil.getCompania();
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try {
            // 2285
            numFormulario = GeneralCodigoFormaEnum.LISAUXILIARSALDOSCGR_CONTROLADOR
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
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
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
     * Carga la lista listaTipoInicial
     *
     */
    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliarSaldosCGRControladorUrlEnum.URL5764
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

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
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliarSaldosCGRControladorUrlEnum.URL6545
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put("TIPOINICIAL",
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliarSaldosCGRControladorUrlEnum.URL7411
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliarSaldosCGRControladorUrlEnum.URL8586
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put("CODIGOINICIAL",
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.CSV);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;

        String strSql;

        Map<String, Object> reemplazar = new TreeMap<>();

        try {
            reemplazar = asignarReemplazosConsulta();
            String baseAuxiliar = Reporteador.resuelveConsulta(
                            "800044BaseAuxiliares",
                            1,
                            reemplazar);
            reemplazar.put("baseAuxiliar", baseAuxiliar);

            strSql = Reporteador.resuelveConsulta(
                            "000682LisAuxiliarSaldosPorIdoficio",
                            1,
                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, formato,
                            "LibroAuxiliar");

        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private HashMap<String, Object> asignarReemplazosConsulta() {
        HashMap<String, Object> reemplazos = new HashMap<>();
        try {
            reemplazos.put("referenciaInicial", "");
            reemplazos.put("referenciafinal", "");
            reemplazos.put("id_codigo", idioma.getString("TG_CODIGO2"));
            reemplazos.put("id_cuenta", idioma.getString("TG_CUENTA"));
            reemplazos.put("agrupa", idioma.getString("TG_CODIGO2"));
            reemplazos.put("anio", anio);
            reemplazos.put("tipoInicial", tipoInicial);
            reemplazos.put("tipoFinal", tipoFinal);
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("centroInicial", centroInicial);
            reemplazos.put("centroFinal", centroFinal);
            reemplazos.put("mesAnterior", SysmanFunciones
                            .getParteFecha(fechaInicial, Calendar.MONTH));
            reemplazos.put("mes", SysmanFunciones.getParteFecha(fechaInicial,
                            Calendar.MONTH)

                + 1);

            reemplazos.put("filtrosCentro",
                            " AND DETALLE_COMPROBANTE_CNT.CENTRO_COSTO BETWEEN '"
                                + centroInicial + "' AND  '" + centroFinal
                                + "' ");
            reemplazos.put("es_id", "0");
            reemplazos.put("filtrosTercero", "");
            reemplazos.put("filtrosTer", "");
            reemplazos.put("condicionReferencias", "");

        }
        catch (

        ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reemplazos;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechainicial() {
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cargarListaCuentaInicial();
    }

    // </METODOS_CAMBIAR>
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
        tipoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

        tipoFinal = "";
        cargarListaTipoFinal();
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
        tipoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

        cuentaFinal = "";

        cargarListaCuentaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
