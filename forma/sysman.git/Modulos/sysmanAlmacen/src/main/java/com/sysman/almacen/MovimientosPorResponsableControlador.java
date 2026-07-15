/*-
 * MovimientosPorResponsableControlador.java
 *
 * 1.0
 * 
 * 31/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.MovimientosPorResponsableControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
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
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar un informe de los movimientos por
 * responsable.
 *
 * @version 1.0, 31/07/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class MovimientosPorResponsableControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String responsableInicial;
    private String responsableFinal;
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreResponsableIni;
    private String nombreResponsableFin;
    private String nombreCodigoIni;
    private String nombreCodigoFin;
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
    private RegistroDataModelImpl listaResponsableInicial;
    private RegistroDataModelImpl listaResponsableFinal;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * MovimientosPorResponsableControlador
     */
    public MovimientosPorResponsableControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_PORRESPONSABLE_CONTROLADOR
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
        cargarListaResponsableInicial();
        cargarListaResponsableFinal();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
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
     * Carga la lista listaResponsableInicial
     *
     */
    public void cargarListaResponsableInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPorResponsableControladorUrlEnum.URL7458
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaResponsableInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CEDULA.getName());
    }

    /**
     * 
     * Carga la lista listaResponsableFinal
     *
     */
    public void cargarListaResponsableFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPorResponsableControladorUrlEnum.URL6471
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), responsableInicial);

        listaResponsableFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CEDULA.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPorResponsableControladorUrlEnum.URL2613
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPorResponsableControladorUrlEnum.URL3383
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());

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
        try {
            archivoDescarga = null;
            String reporte = "001841AUXNARINO";
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("elementoInicial", codigoInicial);
            reemplazar.put("elementoFinal", codigoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("responsableInicial", responsableInicial);
            reemplazar.put("responsableFinal", responsableFinal);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            
            parametros.put("PR_FECHAINICIAL",
            		SysmanFunciones.convertirAFechaCadena(fechaInicial));
            
            parametros.put("PR_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal)
                            );

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirXls() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String reporte = "001841AUXNARINOXLS";
            String consulta = "001841AUXNARINO";
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("elementoInicial", codigoInicial);
            reemplazar.put("elementoFinal", codigoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("responsableInicial", responsableInicial);
            reemplazar.put("responsableFinal", responsableFinal);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            
            parametros.put("PR_FECHAINICIAL",
            		SysmanFunciones.convertirAFechaCadena(fechaInicial));
            
            parametros.put("PR_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal)
                            );

            Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsableInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CEDULA.getName()).toString();
        nombreResponsableIni = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaResponsableFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsableFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsableFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CEDULA.getName()).toString();
        nombreResponsableFin = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName())
                        .toString();
        nombreCodigoIni = registroAux.getCampos()
                        .get("NOMBRELARGO")
                        .toString();
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName())
                        .toString();
        nombreCodigoFin = registroAux.getCampos()
                        .get("NOMBRELARGO")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable responsableInicial
     * 
     * @return responsableInicial
     */
    public String getResponsableInicial() {
        return responsableInicial;
    }

    /**
     * Asigna la variable responsableInicial
     * 
     * @param responsableInicial
     * Variable a asignar en responsableInicial
     */
    public void setResponsableInicial(String responsableInicial) {
        this.responsableInicial = responsableInicial;
    }

    /**
     * Retorna la variable responsableFinal
     * 
     * @return responsableFinal
     */
    public String getResponsableFinal() {
        return responsableFinal;
    }

    /**
     * Asigna la variable responsableFinal
     * 
     * @param responsableFinal
     * Variable a asignar en responsableFinal
     */
    public void setResponsableFinal(String responsableFinal) {
        this.responsableFinal = responsableFinal;
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
     * Retorna la variable nombreResponsableIni
     * 
     * @return nombreResponsableIni
     */
    public String getNombreResponsableIni() {
        return nombreResponsableIni;
    }

    /**
     * Asigna la variable nombreResponsableIni
     * 
     * @param nombreResponsableIni
     * Variable a asignar en nombreResponsableIni
     */
    public void setNombreResponsableIni(String nombreResponsableIni) {
        this.nombreResponsableIni = nombreResponsableIni;
    }

    /**
     * Retorna la variable nombreResponsableFin
     * 
     * @return nombreResponsableFin
     */
    public String getNombreResponsableFin() {
        return nombreResponsableFin;
    }

    /**
     * Asigna la variable nombreResponsableFin
     * 
     * @param nombreResponsableFin
     * Variable a asignar en nombreResponsableFin
     */
    public void setNombreResponsableFin(String nombreResponsableFin) {
        this.nombreResponsableFin = nombreResponsableFin;
    }

    /**
     * Retorna la variable nombreCodigoIni
     * 
     * @return nombreCodigoIni
     */
    public String getNombreCodigoIni() {
        return nombreCodigoIni;
    }

    /**
     * Asigna la variable nombreCodigoIni
     * 
     * @param nombreCodigoIni
     * Variable a asignar en nombreCodigoIni
     */
    public void setNombreCodigoIni(String nombreCodigoIni) {
        this.nombreCodigoIni = nombreCodigoIni;
    }

    /**
     * Retorna la variable nombreCodigoFin
     * 
     * @return nombreCodigoFin
     */
    public String getNombreCodigoFin() {
        return nombreCodigoFin;
    }

    /**
     * Asigna la variable nombreCodigoFin
     * 
     * @param nombreCodigoFin
     * Variable a asignar en nombreCodigoFin
     */
    public void setNombreCodigoFin(String nombreCodigoFin) {
        this.nombreCodigoFin = nombreCodigoFin;
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
     * Retorna la lista listaResponsableInicial
     * 
     * @return listaResponsableInicial
     */
    public RegistroDataModelImpl getListaResponsableInicial() {
        return listaResponsableInicial;
    }

    /**
     * Asigna la lista listaResponsableInicial
     * 
     * @param listaResponsableInicial
     * Variable a asignar en listaResponsableInicial
     */
    public void setListaResponsableInicial(
        RegistroDataModelImpl listaResponsableInicial) {
        this.listaResponsableInicial = listaResponsableInicial;
    }

    /**
     * Retorna la lista listaResponsableFinal
     * 
     * @return listaResponsableFinal
     */
    public RegistroDataModelImpl getListaResponsableFinal() {
        return listaResponsableFinal;
    }

    /**
     * Asigna la lista listaResponsableFinal
     * 
     * @param listaResponsableFinal
     * Variable a asignar en listaResponsableFinal
     */
    public void setListaResponsableFinal(
        RegistroDataModelImpl listaResponsableFinal) {
        this.listaResponsableFinal = listaResponsableFinal;
    }

    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
