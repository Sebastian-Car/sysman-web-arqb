/*-
 * AuxiliarRecaudosUsoEstraControlador.java
 *
 * 1.0
 *
 * 01/11/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.AuxiliarRecaudosUsoEstraControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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
 * Clase que permite generar el reporte AUXILIAR DE RECAUDOS POR USO Y
 * ESTRATO DE PERIODO.
 *
 * @version 1.0, 01/11/2016
 * @author jrodriguezr
 *
 * -- Modificado por lcortes 16,17/05/2017. Refactorizacion de codigo
 * de las listas para utilizar dss's. Se realiza la validacion para
 * que la fecha inicial no sea mayor a la fecha final y que las fechas
 * inicial y final esten dentro del rango de las fechas permitidas
 * para el ciclo.
 */
@ManagedBean
@ViewScoped
public class AuxiliarRecaudosUsoEstraControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a ciclo
     */
    private String ciclo;
    /**
     * Valor del atributo correspondiente a fechaInicial
     */
    private Date fechaInicial;
    /**
     * Valor del atributo correspondiente a fechaFinal
     */
    private Date fechaFinal;
    /**
     * Valor del atributo correspondiente a periodo
     */
    private String periodo;
    /**
     * Valor del atributo correspondiente a primerPago
     */
    private String primerPago;
    /**
     * Valor del atributo correspondiente a ultimoPago
     */
    private String ultimoPago;
    /**
     * Constante a nivel de clase que almacena el valor TODOS para
     * efecto de comparaciones en el bean
     */
    private final String consTodos;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena la fecha del primer pago
     */
    private Date fechaPrimerPago;
    /**
     * Atributo que almacena la fecha del ultimo pago
     */
    private Date fechaUltimoPago;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Metodo que realiza la carga de los elementos de la listaCiclo .
     */
    private RegistroDataModelImpl listaCiclo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AuxiliarRecaudosUsoEstraControlador
     */
    public AuxiliarRecaudosUsoEstraControlador() {
        super();
        compania = SessionUtil.getCompania();
        consTodos = "TODOS";
        try {
            numFormulario = GeneralCodigoFormaEnum.AUXILIAR_RECAUDOS_USO_ESTRA_CONTROLADOR
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
        cargarListaCiclo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

        fechaInicial = fechaFinal = new Date();
        ciclo = consTodos;
        periodo = consTodos;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AuxiliarRecaudosUsoEstraControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
            fechaPrimerPago = (Date) regAux.getCampos().get("PRIMERAFECHA");
            fechaUltimoPago = (Date) regAux.getCampos().get("ULTIMAFECHA");
            primerPago = SysmanFunciones.convertirAFechaCadena(fechaPrimerPago);
            ultimoPago = SysmanFunciones.convertirAFechaCadena(fechaUltimoPago);
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarRecaudosUsoEstraControladorUrlEnum.URL5802
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
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
        generaInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    //
    /**
     * Metodo que permite generar el reporte con los filtros y
     * formatos seleccionados.
     *
     * @param formato
     * Formato en el cual se genera el reporte.
     */
    private void generaInforme(FORMATOS formato) {
        if (!validarFechas()) {
            return;
        }
        String reporte = "001193LRecaudosUsoEstrato";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("ciclo", consTodos.equals(ciclo) ? ""
                : " AND USUARIO.CICLO = '" + ciclo + "'");
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CICLO", "Ciclo: "
                + (consTodos.equals(ciclo) ? "Todos" : ciclo));
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            e.getMessage()));
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     *
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     *
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite verificar que la fecha inicial no sea mayor
     * a la fecha final.
     *
     * @return true si la fecha inicial es menor a la fecha final
     */
    private boolean validarFechas() {

        if ((fechaInicial != null) && (fechaFinal != null)) {
            if (fechaFinal.before(fechaInicial)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB574"));
                fechaFinal = null;
                return false;
            }

            if (!validarRangoFechas()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo que permite validar que las fechas inicial y final esten
     * dentro del rango de las fechas permitidas para el ciclo
     *
     * @return true si las fechas inicial y final estan dentro del
     * rango de las fechas de pagos del ciclo
     */
    private boolean validarRangoFechas() {
        if (fechaInicial.before(fechaPrimerPago)
            || fechaInicial.after(fechaUltimoPago)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3160")
                            .replace("s$fecha$s", "inicial"));
            fechaInicial = null;
            return false;
        }
        if (fechaFinal.before(fechaPrimerPago)
            || fechaFinal.after(fechaUltimoPago)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3160")
                            .replace("s$fecha$s", "final"));
            fechaFinal = null;
            return false;

        }
        return true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        periodo = registroAux.getCampos().get("PERIODO").toString();

        Registro regAux;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        try {

            if (consTodos.equals(ciclo)) {

                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                AuxiliarRecaudosUsoEstraControladorUrlEnum.URL0001
                                                                                .getValue())
                                                .getUrl(), param));
            }
            else {
                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                AuxiliarRecaudosUsoEstraControladorUrlEnum.URL0002
                                                                                .getValue())
                                                .getUrl(), param));
            }
            fechaPrimerPago = (Date) regAux.getCampos().get("PRIMERAFECHA");
            fechaUltimoPago = (Date) regAux.getCampos().get("ULTIMAFECHA");
            primerPago = SysmanFunciones.convertirAFechaCadena(fechaPrimerPago);
            ultimoPago = SysmanFunciones.convertirAFechaCadena(fechaUltimoPago);
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
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
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable primerPago
     *
     * @return primerPago
     */
    public String getPrimerPago() {
        return primerPago;
    }

    /**
     * Asigna la variable primerPago
     *
     * @param primerPago
     * Variable a asignar en primerPago
     */
    public void setPrimerPago(String primerPago) {
        this.primerPago = primerPago;
    }

    /**
     * Retorna la variable ultimoPago
     *
     * @return ultimoPago
     */
    public String getUltimoPago() {
        return ultimoPago;
    }

    /**
     * Asigna la variable ultimoPago
     *
     * @param ultimoPago
     * Variable a asignar en ultimoPago
     */
    public void setUltimoPago(String ultimoPago) {
        this.ultimoPago = ultimoPago;
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
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
