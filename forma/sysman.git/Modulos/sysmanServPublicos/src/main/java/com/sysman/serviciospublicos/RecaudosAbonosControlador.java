/*-
 * RecaudosAbonosControlador.java
 *
 * 1.0
 * 
 * 09/11/2016
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.RecaudosAbonosControladorEnum;
import com.sysman.serviciospublicos.enums.RecaudosAbonosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario que permite generar
 * el informe de Recaudos por abonos, en Access "Frm_Recaudos_Abonos",
 * el cual es llamado desde Facturacion\Informes\Recaudos\Recaudos por
 * Abonos
 *
 * @version 1.0, 09/11/2016
 * @author amonroy
 * 
 * @version 2.0, 14/06/2017, <strong>pespitia</strong>:<br>
 * Refactoring.
 */
@ManagedBean
@ViewScoped
public class RecaudosAbonosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo de servicios
     * publicos
     */
    private final String modulo;
    /**
     * Constante que almacena el nombre de la compania en la cual se
     * inicio sesion
     */
    private final String nombreCompania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del ciclo inicial seleccionado
     * en el formulario
     */
    private String cicloInicial;
    /**
     * Atributo que almacena el valor del ciclo final seleccionado en
     * el formulario
     */
    private String cicloFinal;
    /**
     * Atributo que almacena el periodo inicial que fue seleccionado
     * en el formulario
     */
    private String periodoInicial;
    /**
     * Atributo que almacena el periodo fianl que fue seleccionado en
     * el formulario
     */
    private String periodoFinal;
    /**
     * Atributo que almacena la fecha inicial que fue seleccionada en
     * el formulario
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final que fue seleccionada en el
     * formulario
     */
    private Date fechaFinal;

    /** Atributo que almacena el anio actual */
    private String anio;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para cargar comboBox de ciclo inicial
     */
    private List<Registro> listacmbCicloInicial;
    /**
     * Listado de registros para cargar comboBox de ciclo final
     */
    private List<Registro> listacmbCicloFinal;
    /**
     * Listado de registros para cargar comboBox de periodo inicial
     */
    private List<Registro> listacmbPeriodoInicial;
    /**
     * Listado de registros para cargar comboBox de periodo final
     */
    private List<Registro> listacmbPeriodoFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RecaudosAbonosControlador
     */
    public RecaudosAbonosControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

        try {
            numFormulario = GeneralCodigoFormaEnum.RECAUDOS_ABONOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            cicloInicial = "1";
            periodoInicial = "01";
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
        cargarListacmbCicloInicial();
        cargarListacmbCicloFinal();
        cargarListacmbPeriodoInicial();
        cargarListacmbPeriodoFinal();
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
     * Carga la lista listacmbCicloInicial
     */
    public void cargarListacmbCicloInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listacmbCicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecaudosAbonosControladorUrlEnum.URL6347
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
     * Carga la lista listacmbCicloFinal
     */
    public void cargarListacmbCicloFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RecaudosAbonosControladorEnum.CICLOINICIAL.getValue(),
                        cicloInicial);

        try {
            listacmbCicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecaudosAbonosControladorUrlEnum.URL6852
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
     * Carga la lista listacmbPeriodoInicial
     */
    public void cargarListacmbPeriodoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listacmbPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecaudosAbonosControladorUrlEnum.URL7434
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
     * Carga la lista listacmbPeriodoFinal
     */
    public void cargarListacmbPeriodoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), periodoInicial);

        try {
            listacmbPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecaudosAbonosControladorUrlEnum.URL8182
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato EXCEL
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato PDF
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Actualiza el lisdo de cicloFinal dependiendo el valor del
     * cicloInicial seleccionado
     */
    public void cambiarcmbCicloInicial() {
        cicloFinal = "";
        cargarListacmbCicloFinal();
    }

    /**
     * Actualiza el lisdo de periodoFinal dependiendo el valor del
     * periodoInicial seleccionado
     */
    public void cambiarcmbPeriodoInicial() {
        periodoFinal = "";
        cargarListacmbPeriodoFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envia los
     * parametros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        String reporte = "001231InfRecaudosAbonos";

        try {
            // HashMap reemplazar es para que reemplace en la
            // consulta almacenada en la tabla CONSULTAS
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", cicloInicial);
            reemplazar.put("cicloFinal", cicloFinal);
            reemplazar.put("periodoInicial", periodoInicial);
            reemplazar.put("periodoFinal", periodoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

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
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
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
     */
    public void setAnio(String anio) {
        this.anio = anio;
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
     * Retorna la lista listacmbCicloInicial
     * 
     * @return listacmbCicloInicial
     */
    public List<Registro> getListacmbCicloInicial() {
        return listacmbCicloInicial;
    }

    /**
     * Asigna la lista listacmbCicloInicial
     * 
     * @param listacmbCicloInicial
     * Variable a asignar en listacmbCicloInicial
     */
    public void setListacmbCicloInicial(List<Registro> listacmbCicloInicial) {
        this.listacmbCicloInicial = listacmbCicloInicial;
    }

    /**
     * Retorna la lista listacmbCicloFinal
     * 
     * @return listacmbCicloFinal
     */
    public List<Registro> getListacmbCicloFinal() {
        return listacmbCicloFinal;
    }

    /**
     * Asigna la lista listacmbCicloFinal
     * 
     * @param listacmbCicloFinal
     * Variable a asignar en listacmbCicloFinal
     */
    public void setListacmbCicloFinal(List<Registro> listacmbCicloFinal) {
        this.listacmbCicloFinal = listacmbCicloFinal;
    }

    /**
     * Retorna la lista listacmbPeriodoInicial
     * 
     * @return listacmbPeriodoInicial
     */
    public List<Registro> getListacmbPeriodoInicial() {
        return listacmbPeriodoInicial;
    }

    /**
     * Asigna la lista listacmbPeriodoInicial
     * 
     * @param listacmbPeriodoInicial
     * Variable a asignar en listacmbPeriodoInicial
     */
    public void setListacmbPeriodoInicial(
        List<Registro> listacmbPeriodoInicial) {
        this.listacmbPeriodoInicial = listacmbPeriodoInicial;
    }

    /**
     * Retorna la lista listacmbPeriodoFinal
     * 
     * @return listacmbPeriodoFinal
     */
    public List<Registro> getListacmbPeriodoFinal() {
        return listacmbPeriodoFinal;
    }

    /**
     * Asigna la lista listacmbPeriodoFinal
     * 
     * @param listacmbPeriodoFinal
     * Variable a asignar en listacmbPeriodoFinal
     */
    public void setListacmbPeriodoFinal(List<Registro> listacmbPeriodoFinal) {
        this.listacmbPeriodoFinal = listacmbPeriodoFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
