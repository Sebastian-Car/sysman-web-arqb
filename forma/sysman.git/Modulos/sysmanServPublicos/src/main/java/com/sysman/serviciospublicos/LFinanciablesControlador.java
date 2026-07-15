/*-
 * LFinanciablesControlador.java
 *
 * 1.0
 * 
 * 11/10/2016
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LFinanciablesControladorEnum;
import com.sysman.serviciospublicos.enums.LFinanciablesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario modal LFinanciables.
 *
 * @version 1.0, 11/10/2016
 * @author Pablo Espitia Cuca
 * 
 * @author eamaya
 * @version 2.0, 05/06/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class LFinanciablesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /** Constante que contiene el valor: "CODIGO" */
    private final String codigo;
    /** Constante que contiene el valor: "CODIGORUTA". */
    private final String codigoruta;
    // <DECLARAR_ATRIBUTOS>
    /** Indica si la casilla usuario esta seleccionada. */
    private boolean ckUsuario;

    /** Indica si la casilla auditoria esta seleccionada. */
    private boolean ckAuditoria;

    /** Contiene el codigo de ruta inicial seleccionado. */
    private String codigoInicial;

    /** Contiene el codigo de ruta final seleccionado. */
    private String codigoFinal;

    /** Contiene el ciclo seleccionado. */
    private String ciclo;

    /** Contiene el concepto inicial seleccionado. */
    private String conceptoInicial;

    /** Contiene el concepto final seleccionado. */
    private String conceptoFinal;

    /** Contiene el a�o inicial seleccionado. */
    private String anoInicial;

    /** Contiene el periodo inicial seleccionado. */
    private String periodoInicial;

    /** Contiene el a�o final seleccionado. */
    private String anoFinal;

    /** Contiene el periodo final seleccionado. */
    private String periodoFinal;

    /** Contiene el nombre de usuario con codigo inicial. */
    private String nombreInicial;

    /** Contiene el nombre de usuario con codigo final. */
    private String nombreFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista del combo a�o inicial. */
    private List<Registro> listatxtAnoInicial;

    /** Lista del combo periodo inicial. */
    private List<Registro> listatxtPeriodoInicial;

    /** Lista del combo a�o final. */
    private List<Registro> listatxtAnoFinal;

    /** Lista del combo periodo final. */
    private List<Registro> listatxtPeriodoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista del combo codigo inicial. */
    private RegistroDataModelImpl listaCodigoInicial;

    /** Lista del comobo codigo final */
    private RegistroDataModelImpl listaCodigoFinal;

    /** Lista del combo ciclo. */
    private RegistroDataModelImpl listaCiclo;

    /** Lista del combo concepto inicial. */
    private RegistroDataModelImpl listacmbConceptoInicial;

    /** Lista del combo concepto final. */
    private RegistroDataModelImpl listaCmbConceptoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LFinanciablesControlador
     */
    public LFinanciablesControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigo = "CODIGO";
        codigoruta = "CODIGORUTA";
        anoInicial = String.valueOf(SysmanFunciones.ano(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.L_FINANCIABLES_CONTROLADOR
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
        cargarListatxtAnoInicial();
        cargarListatxtPeriodoInicial();
        cargarListatxtAnoFinal();
        cargarListatxtPeriodoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaCiclo();
        cargarListacmbConceptoInicial();
        cargarListaCmbConceptoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
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
    /** Muestra la lista en el combo a�o inicial. */
    public void cargarListatxtAnoInicial() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listatxtAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanciablesControladorUrlEnum.URL6617
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Muestra la lista en el combo periodo inicial. */
    public void cargarListatxtPeriodoInicial() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.ANO.getName(),
                            anoInicial);

            listatxtPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanciablesControladorUrlEnum.URL7020
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Muestra la lista en el combo a�o final. */
    public void cargarListatxtAnoFinal() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.ANO.getName(),
                            anoInicial);

            listatxtAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanciablesControladorUrlEnum.URL7480
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Muestra la lista en el combo periodo final. */
    public void cargarListatxtPeriodoFinal() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.ANO.getName(),
                            anoFinal);

            listatxtPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanciablesControladorUrlEnum.URL7876
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Muestra la lista en el combo codigo inicial. */
    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFinanciablesControladorUrlEnum.URL8339
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoruta);
    }

    /** Muestra la lista en el combo codigo final. */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFinanciablesControladorUrlEnum.URL8972
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);
        param.put(LFinanciablesControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoruta);
    }

    /** Muestra la lista en el combo ciclo. */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFinanciablesControladorUrlEnum.URL9703
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    /** Muestra la lista en el combo concepto inicial. */
    public void cargarListacmbConceptoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFinanciablesControladorUrlEnum.URL11814
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbConceptoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /** Muestra la lista en el combo concepto final. */
    public void cargarListaCmbConceptoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LFinanciablesControladorUrlEnum.URL12364
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LFinanciablesControladorEnum.PARAM1.getValue(),
                        conceptoInicial);

        listaCmbConceptoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /** Evento del boton PDF */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /** Evento del boton EXCEL */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            /* Verifica el valor del parametro FORMATO CALIDAD */
            boolean key;

            key = "SI".equals(
                            SysmanFunciones.nvlStr(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "FORMATO CALIDAD",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),

                                            "NO"));

            String reporte = "";

            /* Si esta marcada la casilla de usuario. */
            if (ckUsuario) {
                reporte = key ? "001139FinanciablesUsuarioCOS"
                    : "001145FinanciablesUsuario";
            }
            else {
                reporte = key ? "001131FinanciablesCOS" : "001137Financiables";
            }

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            /* Si el ciclo es T, envie el ciclo con menor valor. */
            reemplazar.put("cicloInicial",
                            ciclo != "T" ? ciclo : 1);
            /* Si el ciclo es T, envie el ciclo con mayor valor. */
            reemplazar.put("cicloFinal", ciclo != "T" ? ciclo : 999);

            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("conceptoInicial", conceptoInicial);
            reemplazar.put("conceptoFinal", conceptoFinal);

            /* La fecha inicial es el a�o y el periodo inicial. */
            reemplazar.put("fechaInicial", anoInicial + "" + periodoInicial);

            /* La fecha final es el a�o y el periodo final. */
            reemplazar.put("fechaFinal", anoFinal + "" + periodoFinal);
            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            /* Enviar -1 para ver los campos de auditoria. */
            parametros.put("PR_AUDITORIA", ckAuditoria ? -1 : 0);
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException
                        | SystemException | SysmanException ex) {
            Logger.getLogger(LFinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartxtAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListatxtAnoFinal();
        cargarListatxtPeriodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtAnoFinal() {
        // <CODIGO_DESARROLLADO>
        cargarListatxtPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckUsuario
     */
    public void cambiarckUsuario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Ejecuta los eventos del combo codigo inicial.
     * 
     * @param event
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoruta).toString();
        /* Recupera el nombre del usuario con el codigo inicial */
        nombreInicial = registroAux.getCampos().get("NOMBRE")
                        .toString();

        codigoFinal = "";
        nombreFinal = "";

        cargarListaCodigoFinal();
    }

    /**
     * Ejecuta los eventos del combo codigo final.
     * 
     * @param event
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoruta).toString();
        /* Recupera el nombre del usuario con el codigo final */
        nombreFinal = registroAux.getCampos().get("NOMBRE")
                        .toString();
    }

    /**
     * Ejecuta los eventos del combo ciclo.
     * 
     * @param event
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        /* Recupera el codigo inicial del ciclo */
        codigoInicial = registroAux.getCampos().get("CODIGOINICIAL").toString();
        /* Recupera el codigo final del ciclo */
        codigoFinal = registroAux.getCampos().get("CODIGOFINAL").toString();
        /* Recupera el nombre del usuario del codigo inicial */
        nombreInicial = registroAux.getCampos().get("NOMBREINICIAL").toString();
        /* Recupera el nombre del usuario codigo final */
        nombreFinal = registroAux.getCampos().get("NOMBREFINAL").toString();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
    }

    /**
     * Ejecuta los eventos del combo concepto inicial.
     * 
     * @param event
     */
    public void seleccionarFilacmbConceptoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoInicial = registroAux.getCampos().get(codigo).toString();
        conceptoFinal = "";
        cargarListaCmbConceptoFinal();
    }

    /**
     * Ejecuta los eventos del combo concepto final.
     * 
     * @param event
     */
    public void seleccionarFilaCmbConceptoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoFinal = registroAux.getCampos().get(codigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public boolean isCkUsuario() {
        return ckUsuario;
    }

    public void setCkUsuario(boolean ckUsuario) {
        this.ckUsuario = ckUsuario;
    }

    public boolean isCkAuditoria() {
        return ckAuditoria;
    }

    public void setCkAuditoria(boolean ckAuditoria) {
        this.ckAuditoria = ckAuditoria;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getConceptoInicial() {
        return conceptoInicial;
    }

    public void setConceptoInicial(String conceptoInicial) {
        this.conceptoInicial = conceptoInicial;
    }

    public String getConceptoFinal() {
        return conceptoFinal;
    }

    public void setConceptoFinal(String conceptoFinal) {
        this.conceptoFinal = conceptoFinal;
    }

    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public String getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    public String getPeriodoFinal() {
        return periodoFinal;
    }

    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListatxtAnoInicial() {
        return listatxtAnoInicial;
    }

    public void setListatxtAnoInicial(List<Registro> listatxtAnoInicial) {
        this.listatxtAnoInicial = listatxtAnoInicial;
    }

    public List<Registro> getListatxtPeriodoInicial() {
        return listatxtPeriodoInicial;
    }

    public void setListatxtPeriodoInicial(
        List<Registro> listatxtPeriodoInicial) {
        this.listatxtPeriodoInicial = listatxtPeriodoInicial;
    }

    public List<Registro> getListatxtAnoFinal() {
        return listatxtAnoFinal;
    }

    public void setListatxtAnoFinal(List<Registro> listatxtAnoFinal) {
        this.listatxtAnoFinal = listatxtAnoFinal;
    }

    public List<Registro> getListatxtPeriodoFinal() {
        return listatxtPeriodoFinal;
    }

    public void setListatxtPeriodoFinal(List<Registro> listatxtPeriodoFinal) {
        this.listatxtPeriodoFinal = listatxtPeriodoFinal;
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

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListacmbConceptoInicial() {
        return listacmbConceptoInicial;
    }

    public void setListacmbConceptoInicial(
        RegistroDataModelImpl listacmbConceptoInicial) {
        this.listacmbConceptoInicial = listacmbConceptoInicial;
    }

    public RegistroDataModelImpl getListaCmbConceptoFinal() {
        return listaCmbConceptoFinal;
    }

    public void setListaCmbConceptoFinal(
        RegistroDataModelImpl listaCmbConceptoFinal) {
        this.listaCmbConceptoFinal = listaCmbConceptoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
