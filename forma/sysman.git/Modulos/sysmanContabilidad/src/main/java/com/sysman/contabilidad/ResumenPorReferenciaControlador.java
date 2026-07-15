/*-
 * ResumenPorReferenciaControlador.java
 *
 * 1.0
 * 
 * 23/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ResumenPorReferenciaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * 
 *
 * @version 1.0, 23/10/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ResumenPorReferenciaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String refFinal;
    private String refInicial;
    private String anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;

    private List<Registro> listaAno;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCmbRefInicial;
    private RegistroDataModelImpl listaCmbRefFinal;

    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ResumenPorReferenciaControlador
     */
    public ResumenPorReferenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = 1972;
            fechaInicial = new Date();
            fechaFinal = new Date();
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenPorReferenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaCmbRefInicial();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
         * FR1972-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenPorReferenciaControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     * 
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenPorReferenciaControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("CODIGOINICIAL", codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCmbRefInicial
     *
     * 
     */
    public void cargarListaCmbRefInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenPorReferenciaControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbRefInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCmbRefFinal
     *
     * 
     */
    public void cargarListaCmbRefFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenPorReferenciaControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("REFERENCIAINICIAL", refInicial);

        listaCmbRefFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAnoTrabajo
     *
     * 
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenPorReferenciaControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
            cargarListaCodigoInicial();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAno() {
        refInicial = null;
        refFinal = null;
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbRefInicial
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbRefInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        refInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        cargarListaCmbRefFinal();
        refFinal = null;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbRefFinal
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbRefFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        refFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     * 
     *
     */
    public void oprimirImprimir() {
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    public void oprimirExcel() {
        archivoDescarga = null;
        generarExcel(FORMATOS.EXCEL97);
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "001948RefResPorRefyCuenta";
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("refInicial", refInicial);
            reemplazar.put("refFinal", refFinal);

            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FORMS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_FORMS_REFINICIAL", refInicial);
            parametros.put("PR_FORMS_REFFINAL", refFinal);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void generarExcel(ReportesBean.FORMATOS formato) {
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "001948RefResPorRefyCuenta";
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("refInicial", refInicial);
            reemplazar.put("refFinal", refFinal);

            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FORMS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_FORMS_REFINICIAL", refInicial);
            parametros.put("PR_FORMS_REFFINAL", refFinal);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * @return the refInicial
     */

    /**
     * @return the refFinal
     */
    public String getRefFinal() {
        return refFinal;
    }

    /**
     * @return the refInicial
     */
    public String getRefInicial() {
        return refInicial;
    }

    /**
     * @param refInicial
     * the refInicial to set
     */
    public void setRefInicial(String refInicial) {
        this.refInicial = refInicial;
    }

    /**
     * @return the listaCmbRefInicial
     */
    public RegistroDataModelImpl getListaCmbRefInicial() {
        return listaCmbRefInicial;
    }

    /**
     * @param listaCmbRefInicial
     * the listaCmbRefInicial to set
     */
    public void setListaCmbRefInicial(
        RegistroDataModelImpl listaCmbRefInicial) {
        this.listaCmbRefInicial = listaCmbRefInicial;
    }

    /**
     * @param refFinal
     * the refFinal to set
     */
    public void setRefFinal(String refFinal) {
        this.refFinal = refFinal;
    }

    /**
     * @return the listaCmbRefFinal
     */
    public RegistroDataModelImpl getListaCmbRefFinal() {
        return listaCmbRefFinal;
    }

    /**
     * @param listaCmbRefFinal
     * the listaCmbRefFinal to set
     */
    public void setListaCmbRefFinal(RegistroDataModelImpl listaCmbRefFinal) {
        this.listaCmbRefFinal = listaCmbRefFinal;
    }

    /**
     * @return the codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * @param codigoInicial
     * the codigoInicial to set
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * @return the codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * @param codigoFinal
     * the codigoFinal to set
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * @return the listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * @param listaCodigoInicial
     * the listaCodigoInicial to set
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * @return the listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * @param listaCodigoFinal
     * the listaCodigoFinal to set
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * @return the listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * @param listaAno
     * the listaAno to set
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
