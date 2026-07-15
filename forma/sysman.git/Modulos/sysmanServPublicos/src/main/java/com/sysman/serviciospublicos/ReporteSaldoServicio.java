/*-
 * ReporteSaldoServicio.java
 *
 * 1.0
 *
 * 11/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ReporteSaldoServicioEnum;
import com.sysman.serviciospublicos.enums.ReporteSaldoServicioUrlEnum;

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
 * Controlador que permite generar el reporte de saldos credito por
 * servicio
 *
 * @version 1.0, 11/11/2016
 * @author jlozano
 *
 * -- Modificado por lcortes 16/06/2017. Refactorizacion de las
 * consultas de las listas para usar dss.
 */
@ManagedBean
@ViewScoped
public class ReporteSaldoServicio extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor seleccionado para el combo Ciclo
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor seleccionado para el combo
     * Codigo inicial
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el valor seleccionado para el combo
     * Codigo final
     */
    private String codigoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Constante para literal "CODIGORUTA"
     */
    private static final String CODIGO_RUTA = "CODIGORUTA";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que se muestra en el combo Ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que se muestra en el combo Codigo inicial
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Lista que se muestra en el combo Codigo final
     */
    private RegistroDataModelImpl listacmbCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ReporteSaldoServicio
     */
    public ReporteSaldoServicio() {
        super();
        compania = SessionUtil.getCompania();
        ciclo = "T";
        codigoInicial = "0";
        codigoFinal = "9999";
        try {
            numFormulario = GeneralCodigoFormaEnum.REPORTE_SALDO_SERVICIO
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
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigoInicial();
        cargarListacmbCodigoFinal();
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
         * FR1198-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name Dim db As
         * DAO.Database Dim rs As DAO.Recordset Dim str As String Set
         * db = CurrentDb() str = "T" Set rs = db.OpenRecordset(
         * "Select * from Ciclo where Compania='" & Getcompany() &
         * "' Order by Compania,Numero", , dbSQLPassThrough) While Not
         * rs.EOF str = str & ";" & rs!Numero rs.MoveNext Wend
         * rs.Close Me!Ciclo.RowSource = str End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Metodo que carga de los elementos de la lista Ciclo.
     *
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteSaldoServicioUrlEnum.URL0001
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
     * Metodo que carga los elementos de la lista Codigo inicial
     */
    public void cargarListacmbCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteSaldoServicioUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? "-1" : ciclo);

        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO_RUTA);
    }

    /**
     *
     * Metodo que carga los elementos de la lista Codigo final
     */
    public void cargarListacmbCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteSaldoServicioUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        "T".equals(ciclo) ? "-1" : ciclo);
        param.put(ReporteSaldoServicioEnum.PARAM0.getValue(), codigoInicial);

        listacmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO_RUTA);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     * Genera el reporte "Informe de saldos credito por servicio" en
     * formato PDF
     *
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando32 en la vista
     *
     * Genera el reporte "Informe de saldos credito por servicio" en
     * formato Excel
     *
     */
    public void oprimirComando32() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     *
     * Recarga los valores de los combos Codigo inicial y Codigo final
     * filtrando por el ciclo seleccionado
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";
        cargarListacmbCodigoInicial();
        cargarListacmbCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoInicial
     *
     * Asigna al atributo codigoInicial el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(CODIGO_RUTA).toString();
        cargarListacmbCodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoFinal
     *
     * Asigna al atributo codigoFinal el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(CODIGO_RUTA).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    public void generarInforme(FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_CICLO",
                            "T".equals(ciclo) ? "Todos" : ciclo);
            reemplazar.put("codIni", codigoInicial);
            reemplazar.put("codFin", codigoFinal);
            reemplazar.put("condicionCiclo", "T".equals(ciclo) ? " "
                : "AND SP_FACTURADO.CICLO = " + ciclo);

            Reporteador.resuelveConsulta("001246rptSaldosCreditoServicio",
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001246rptSaldosCreditoServicio", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
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
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigoInicial
     *
     * @return listacmbCodigoInicial
     */
    public RegistroDataModelImpl getListacmbCodigoInicial() {
        return listacmbCodigoInicial;
    }

    /**
     * Asigna la lista listacmbCodigoInicial
     *
     * @param listacmbCodigoInicial
     * Variable a asignar en listacmbCodigoInicial
     */
    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial) {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    /**
     * Retorna la lista listacmbCodigoFinal
     *
     * @return listacmbCodigoFinal
     */
    public RegistroDataModelImpl getListacmbCodigoFinal() {
        return listacmbCodigoFinal;
    }

    /**
     * Asigna la lista listacmbCodigoFinal
     *
     * @param listacmbCodigoFinal
     * Variable a asignar en listacmbCodigoFinal
     */
    public void setListacmbCodigoFinal(
        RegistroDataModelImpl listacmbCodigoFinal) {
        this.listacmbCodigoFinal = listacmbCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
