/*-
 * FrmInfContabilidadControlador.java
 *
 * 1.0
 * 
 * 9/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.facturaciongeneral.enums.FrmInfContabilidadControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmInfContabilidadControladorUrlEnum;

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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera los reportes contables de facturación general
 *
 * @version 1.0, 09/11/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmInfContabilidadControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el numero de modulo por el cual el
     * usuario inicia sesion
     */

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la factura inicial seleccionada en la
     * vista
     */
    private String facturaInicial;
    /**
     * Atributo que almacena la factura final seleccionada en la vista
     */
    private String facturaFinal;
    /**
     * Atributo que almacena la fecha inicial seleccionada en la vista
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final seleccionada en la vista
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
     * lista que almacena las facturas
     */
    private RegistroDataModelImpl listafacturaInicial;
    /**
     * lista que almacena las facturas a partir de la factura inicial
     */
    private RegistroDataModelImpl listafacturaFinal;

    /**
     * Variable que almacena el valor del indicador Validar Fecha del
     * formulario
     */
    private boolean verFechas;
    /**
     * Variable que se envia como parametro al reporte para mostrar
     * encabezado de fechas
     */

    private String mostrarFechas;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmInfContabilidadControlador
     */
    public FrmInfContabilidadControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INF_CONTABILIDAD_CONTROLADOR
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
        cargarListafacturaInicial();
        verFechas = true;
        fechaInicial = new Date();
        fechaFinal = new Date();
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
     * Carga la lista listafacturaInicial
     *
     */
    public void cargarListafacturaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfContabilidadControladorUrlEnum.URL5623
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listafacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmInfContabilidadControladorEnum.NRO_FACTURA
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listafacturaFinal
     *
     */
    public void cargarListafacturaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfContabilidadControladorUrlEnum.URL6394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmInfContabilidadControladorEnum.FACTURAINICIAL.getValue(),
                        facturaInicial);

        listafacturaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmInfContabilidadControladorEnum.NRO_FACTURA
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdOficial en la vista
     *
     */
    public void oprimircmdPdf() {

        generarReporte(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdExcel en la vista
     *
     *
     */
    public void oprimircmdExcel() {
        generarReporte(FORMATOS.EXCEL);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control validaFecha
     * 
     * 
     */
    public void cambiarvalidaFecha() {
        // <CODIGO_DESARROLLADO>
        if (verFechas) {
            mostrarFechas = "true";
        }
        else {
            mostrarFechas = "false";
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafacturaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafacturaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmInfContabilidadControladorEnum.NRO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();
        facturaFinal = null;
        cargarListafacturaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafacturaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafacturaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmInfContabilidadControladorEnum.NRO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;
        String condicionFechas = "";

        if (!validarCamposVacios()) {
            return;
        }

        try {
            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();

            reemplazos.put("compania", compania);

            if (verFechas) {
                condicionFechas = "AND CCNT.FECHA BETWEEN "
                    + SysmanFunciones.formatearFecha(fechaInicial) + " AND "
                    + SysmanFunciones.formatearFecha(fechaFinal);

            }
            reemplazos.put("condicionFechas", condicionFechas);
            reemplazos.put("facturaInicial", facturaInicial);
            reemplazos.put("facturaFinal", facturaFinal);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));

            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_FECHA_VISIBLE", mostrarFechas);

            Reporteador.resuelveConsulta("001487INFCONTABILIDAD",
                            Integer.parseInt(modulo), reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001487INFCONTABILIDAD",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarCamposVacios() {
        if (verFechas && SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(fechaInicial, "").toString())
            ||
            SysmanFunciones.validarVariableVacio(
                            SysmanFunciones.nvl(fechaFinal, "")
                                            .toString())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB150"));
            return false;
        }

        return true;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable facturaInicial
     * 
     * @return facturaInicial
     */
    public String getFacturaInicial() {
        return facturaInicial;
    }

    /**
     * Asigna la variable facturaInicial
     * 
     * @param facturaInicial
     * Variable a asignar en facturaInicial
     */
    public void setFacturaInicial(String facturaInicial) {
        this.facturaInicial = facturaInicial;
    }

    /**
     * Retorna la variable facturaFinal
     * 
     * @return facturaFinal
     */
    public String getFacturaFinal() {
        return facturaFinal;
    }

    /**
     * Asigna la variable facturaFinal
     * 
     * @param facturaFinal
     * Variable a asignar en facturaFinal
     */
    public void setFacturaFinal(String facturaFinal) {
        this.facturaFinal = facturaFinal;
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

    public boolean isVerFechas() {
        return verFechas;
    }

    public void setVerFechas(boolean verFechas) {
        this.verFechas = verFechas;
    }

    public String getMostrarFechas() {
        return mostrarFechas;
    }

    public void setMostrarFechas(String mostrarFechas) {
        this.mostrarFechas = mostrarFechas;
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
     * Retorna la lista listafacturaInicial
     * 
     * @return listafacturaInicial
     */
    public RegistroDataModelImpl getListafacturaInicial() {
        return listafacturaInicial;
    }

    /**
     * Asigna la lista listafacturaInicial
     * 
     * @param listafacturaInicial
     * Variable a asignar en listafacturaInicial
     */
    public void setListafacturaInicial(
        RegistroDataModelImpl listafacturaInicial) {
        this.listafacturaInicial = listafacturaInicial;
    }

    /**
     * Retorna la lista listafacturaFinal
     * 
     * @return listafacturaFinal
     */
    public RegistroDataModelImpl getListafacturaFinal() {
        return listafacturaFinal;
    }

    /**
     * Asigna la lista listafacturaFinal
     * 
     * @param listafacturaFinal
     * Variable a asignar en listafacturaFinal
     */
    public void setListafacturaFinal(RegistroDataModelImpl listafacturaFinal) {
        this.listafacturaFinal = listafacturaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
