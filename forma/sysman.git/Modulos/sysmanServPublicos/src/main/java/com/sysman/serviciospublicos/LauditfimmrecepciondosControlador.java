/*-
 * LauditfimmrecepciondosControlador.java
 *
 * 1.0
 *
 * 25/11/2016
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
import com.sysman.serviciospublicos.enums.LauditfimmrecepciondosControladorEnum;
import com.sysman.serviciospublicos.enums.LauditfimmrecepciondosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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
 * Controlarod del formulario LauditfimmrecepciondosControlador
 *
 * @version 1.0, 25/11/2016
 * @author cperez
 *
 * @version 2, 05/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class LauditfimmrecepciondosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el id del combo seleccionado por el usiario ciclo
     * Inicial de la consulta
     */
    private String cicloInicial;
    /**
     * Obtiene el id del combo seleccionado por el usiario ciclo Final
     * de la consulta
     */
    private String cicloFinal;
    /**
     * Obtiene el id del combo seleccionado por el codigo Inicial
     * Final de la consulta
     */
    private String codigoInicial;
    /**
     * Obtiene el id del combo seleccionado por el codigo Final Final
     * de la consulta
     */
    private String codigoFinal;
    /**
     * Obtiene el nombre del combo seleccionado por el aforador Final
     * de la consulta
     */
    private String aforador;
    /*
     * Obtiene el id del combo seleccionado por el aforador Final de
     * la consulta
     */
    private String codigoAfanador;
    /**
     * Obtiene la fecha Inicial del combo seleccionado
     */
    private Date fechaInicial;
    /**
     * Obtiene la fecha Final del combo seleccionado
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /*
     * variable para asiganar el nombre del tipo de error
     * "MSM_TRANS_INTERRUMPIDA"
     */

    /*
     * variable para asignar el nombre constante de "CODIGORUTA"
     */
    private static final String CODIGORUTA = "CODIGORUTA";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Necesario para obtener mandar la lista Ciclo Inicial
     */
    private List<Registro> listaCicloInicial;
    /**
     * Necesario para obtener mandar la lista Codigo Inicial
     */
    private List<Registro> listaCicloFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener mandar la lista Codigo Final
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Necesario para obtener mandar la lista Codigo Final
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Necesario para obtener mandar la lista del Aforador
     */
    private RegistroDataModelImpl listacmbAforador;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LauditfimmrecepciondosControlador
     */
    public LauditfimmrecepciondosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LAUDITFIMMRECEPCIONDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaCicloInicial();
        cargarListaCicloFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListacmbAforador();
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
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCicloInicial
     */
    public void cargarListaCicloInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaCicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LauditfimmrecepciondosControladorUrlEnum.URL6533
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
     * Carga la lista listaCicloFinal
     */
    public void cargarListaCicloFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(LauditfimmrecepciondosControladorEnum.PARAM0.getValue(),
                            cicloInicial);
            listaCicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LauditfimmrecepciondosControladorUrlEnum.URL6965
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
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LauditfimmrecepciondosControladorUrlEnum.URL7481
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LauditfimmrecepciondosControladorEnum.PARAM0.getValue(),
                        cicloInicial);
        param.put(LauditfimmrecepciondosControladorEnum.PARAM1.getValue(),
                        cicloFinal);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGORUTA);
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LauditfimmrecepciondosControladorUrlEnum.URL8473
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LauditfimmrecepciondosControladorEnum.PARAM0.getValue(),
                        cicloInicial);
        param.put(LauditfimmrecepciondosControladorEnum.PARAM1.getValue(),
                        cicloFinal);
        param.put(LauditfimmrecepciondosControladorEnum.PARAM2.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGORUTA);
    }

    /**
     *
     * Carga la lista listacmbAforador
     */
    public void cargarListacmbAforador() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LauditfimmrecepciondosControladorUrlEnum.URL9463
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbAforador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>

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
        genInforme(FORMATOS.PDF, "001272rptPlanos");
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL, "001272rptPlanos");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        try {

            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("aforador", codigoAfanador);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_AFORADOR", aforador);
            parametros.put("PR_FORMS_LAUDITFIMMRECEPCION2_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_LAUDITFIMMRECEPCION2_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
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
    /**
     * Metodo ejecutado al cambiar el control CicloInicial
     *
     */
    public void cambiarCicloInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaCicloFinal();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cicloFinal = "";
        codigoInicial = "";
        codigoFinal = "";
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CicloFinal
     *
     */
    public void cambiarCicloFinal() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        codigoInicial = "";
        codigoFinal = "";
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     *
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        fechaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux != null
            ? registroAux.getCampos().get(CODIGORUTA).toString() : "";
        codigoFinal = "";
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
        codigoFinal = registroAux != null
            ? registroAux.getCampos().get(CODIGORUTA).toString() : "";
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbAforador
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbAforador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        aforador = registroAux.getCampos().get("NOMBRE").toString();
        codigoAfanador = registroAux.getCampos().get("CODIGO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cicloInicial
     *
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     *
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     *
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     *
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
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
     * Retorna la variable aforador
     *
     * @return aforador
     */
    public String getAforador() {
        return aforador;
    }

    /**
     * Asigna la variable aforador
     *
     * @param aforador
     * Variable a asignar en aforador
     */
    public void setAforador(String aforador) {
        this.aforador = aforador;
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
    /**
     * Retorna la lista listaCicloInicial
     *
     * @return listaCicloInicial
     */
    public List<Registro> getListaCicloInicial() {
        return listaCicloInicial;
    }

    /**
     * Asigna la lista listaCicloInicial
     *
     * @param listaCicloInicial
     * Variable a asignar en listaCicloInicial
     */
    public void setListaCicloInicial(List<Registro> listaCicloInicial) {
        this.listaCicloInicial = listaCicloInicial;
    }

    /**
     * Retorna la lista listaCicloFinal
     *
     * @return listaCicloFinal
     */
    public List<Registro> getListaCicloFinal() {
        return listaCicloFinal;
    }

    /**
     * Asigna la lista listaCicloFinal
     *
     * @param listaCicloFinal
     * Variable a asignar en listaCicloFinal
     */
    public void setListaCicloFinal(List<Registro> listaCicloFinal) {
        this.listaCicloFinal = listaCicloFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public RegistroDataModelImpl getListacmbAforador() {
        return listacmbAforador;
    }

    public void setListacmbAforador(RegistroDataModelImpl listacmbAforador) {
        this.listacmbAforador = listacmbAforador;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
