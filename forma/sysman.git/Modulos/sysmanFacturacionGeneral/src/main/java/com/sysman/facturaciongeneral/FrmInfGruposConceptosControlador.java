/*-
 * FrmInfGruposConceptosControlador.java
 *
 * 1.0
 * 
 * 07/11/2017
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
import com.sysman.facturaciongeneral.enums.FrmInfGruposConceptosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
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
 * Clase encargada de generar el reporte 001482INFGRUPOSDECONCEPTOS el
 * cual muestra la facturacion de grupos conceptos
 *
 * @version 1.0, 07/11/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped

public class FrmInfGruposConceptosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo grupo Inicial en la interfaz grafica
     */
    private String grupoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el combo grupo Final en la interfaz grafica
     */
    private String grupoFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el campo fecha Inicial en la interfaz grafica
     */
    private Date fechaInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el campo fecha Final en la interfaz grafica
     */
    private Date fechaFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el campo nombre Inicial en la interfaz grafica
     */
    private String nombreInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * en el campo nombre Final en la interfaz grafica
     */
    private String nombreFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */

    private String tipoCobro;

    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista encargarda de almanar temporalmente el resultado a la
     * llamada de la base de datos
     */
    private RegistroDataModelImpl listaGRUPOINICIAL;
    /**
     * Lista encargarda de almanar temporalmente el resultado a la
     * llamada de la base de datos
     */
    private RegistroDataModelImpl listaGRUPOFINAL;
    private final String codigoCons;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmInfGruposConceptosControlador
     */
    public FrmInfGruposConceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = GeneralParameterEnum.CODIGO.getName();

        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFGRUPOSCONCEPTOS_CONTROLADOR
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
        cargarListaGRUPOINICIAL();
        cargarListaGRUPOFINAL();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaGRUPOINICIAL
     *
     * Metodo encargado de hacer la llamado a la base de datos a la
     * tabla SF_GRUPOS_CONCEPTOS por medio de Dss.
     */
    public void cargarListaGRUPOINICIAL() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfGruposConceptosControladorUrlEnum.URL5690
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOCONTRATO", tipoCobro);

        listaGRUPOINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * 
     * Carga la lista listaGRUPOFINAL
     *
     * Metodo encargado de hacer la llamado a la base de datos a la
     * tabla SF_GRUPOS_CONCEPTOS por medio de Dss.
     */
    public void cargarListaGRUPOFINAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmInfGruposConceptosControladorUrlEnum.URL6416
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOCONTRATO", tipoCobro);
        param.put("CODIGOINICIAL", grupoInicial);

        listaGRUPOFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     * Boton que se encarga de invocar el metodo que Exporta el
     * informe en formato PDF
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
     * Boton que se encarga de invocar el metodo que Exporta el
     * informe en formato EXCEL
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control GRUPOINICIAL
     * 
     * 
     * 
     */

    public void seleccionarFilaGRUPOINICIAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        grupoInicial = retornarString(registroAux, codigoCons);

        grupoFinal = null;
        nombreInicial = null;
        nombreFinal = null;
        nombreInicial = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        cargarListaGRUPOFINAL();

    }

    public void seleccionarFilaGRUPOFINAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        grupoFinal = retornarString(registroAux, codigoCons);
        nombreFinal = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable grupoInicial
     * 
     * @return grupoInicial
     */
    public String getGrupoInicial() {
        return grupoInicial;
    }

    /**
     * Asigna la variable grupoInicial
     * 
     * @param grupoInicial
     * Variable a asignar en grupoInicial
     */
    public void setGrupoInicial(String grupoInicial) {
        this.grupoInicial = grupoInicial;
    }

    /**
     * Retorna la variable grupoFinal
     * 
     * @return grupoFinal
     */
    public String getGrupoFinal() {
        return grupoFinal;
    }

    /**
     * Asigna la variable grupoFinal
     * 
     * @param grupoFinal
     * Variable a asignar en grupoFinal
     */
    public void setGrupoFinal(String grupoFinal) {
        this.grupoFinal = grupoFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */

    /**
     * Retorna la variable nombreInicial
     * 
     * @return nombreInicial
     */
    public String getNombreInicial() {
        return nombreInicial;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Asigna la variable nombreInicial
     * 
     * @param nombreInicial
     * Variable a asignar en nombreInicial
     */
    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    /**
     * Retorna la variable nombreFinal
     * 
     * @return nombreFinal
     */
    public String getNombreFinal() {
        return nombreFinal;
    }

    /**
     * Asigna la variable nombreFinal
     * 
     * @param nombreFinal
     * Variable a asignar en nombreFinal
     */
    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
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
     * Retorna la lista listaGRUPOINICIAL
     * 
     * @return listaGRUPOINICIAL
     */

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private void genInforme(ReportesBean.FORMATOS formato) {

        try {

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("grupoInicial", grupoInicial);
            reemplazar.put("grupoFinal", grupoFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_FRM_INFGRUPOSCONCEPTOS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_FRM_INFGRUPOSCONCEPTOS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal));

            Reporteador.resuelveConsulta("001482INFGRUPOSDECONCEPTOS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001482INFGRUPOSDECONCEPTOS", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public RegistroDataModelImpl getListaGRUPOINICIAL() {
        return listaGRUPOINICIAL;
    }

    public void setListaGRUPOINICIAL(RegistroDataModelImpl listaGRUPOINICIAL) {
        this.listaGRUPOINICIAL = listaGRUPOINICIAL;
    }

    public RegistroDataModelImpl getListaGRUPOFINAL() {
        return listaGRUPOFINAL;
    }

    public void setListaGRUPOFINAL(RegistroDataModelImpl listaGRUPOFINAL) {
        this.listaGRUPOFINAL = listaGRUPOFINAL;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
