/*-
 * FrmEstadoFinancieraAuxiliaresControlador.java
 *
 * 1.0
 * 
 * 30/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmEstadoFinancieraAuxiliaresControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 30/05/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmEstadoFinancieraAuxiliaresControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private boolean ckCentroCosto;
    private boolean ckTercero;
    private boolean ckAuxiliar;
    private boolean ckSaldoCero;
    private boolean ckFormatoEspecial;
    private boolean ckSoloSaldos;
    private boolean ckFuenteRecurso;
    private boolean ckReferencia;
    private int anio;
    private int mes;
    private String codigoInicial;
    private String codigoFinal;
    private String titulo;
    private String centroCostoInicial;
    private String centroCostoFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String auxiliarInicial;
    private String auxiliarFinal;
    private String fuenteRecursoInicial;
    private String fuenteRecursoFinal;
    private String referenciaInicial;
    private String referenciaFinal;
    private String digitos;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnio;
    private List<Registro> listaMes;
    private List<Registro> listaTitulo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCentroCostoInicial;
    private RegistroDataModelImpl listaCentroCostoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaAuxiliarInicial;
    private RegistroDataModelImpl listaAuxiliarFinal;
    private RegistroDataModelImpl listaFuenteRecursoInicial;
    private RegistroDataModelImpl listaFuenteRecursoFinal;
    private RegistroDataModelImpl listaReferenciaInicial;
    private RegistroDataModelImpl listaReferenciaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    //
    private static final String CODIGO = "CODIGO";
    private static final String CODIGO_INICIAL = "CODIGOINICIAL";
    private static final String NIT = "NIT";
    private static final String TERCERO_VISIBLE = "PR_TERCERO";
    private static final String CENTRO_COSTO_VISIBLE = "PR_CENTRO_COSTO";
    private static final String AUXILIAR_VISIBLE = "PR_AUXILIAR";
    private static final String REFERENCIA_VISIBLE = "PR_REFERENCIA";
    private static final String FUENTE_RECURSO_VISIBLE = "PR_FUENTE_RECURSO";
    private static final String FRT_ESPECIAL_VISIBLE = "PR_FORMATO_ESPECIAL";
    private static final String NOMBRE_REPORTE = "002009LisResultadosAuxiliares";

    //
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * FrmEstadoFinancieraAuxiliaresControlador
     */
    public FrmEstadoFinancieraAuxiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 2080
            numFormulario = GeneralCodigoFormaEnum.FRM_ESTADO_FINANCIERA_AUXILIARES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            digitos = "6";
            titulo = "1";
            anio = SysmanFunciones.ano(new Date());
            mes = SysmanFunciones.mes(new Date());
            codigoInicial = "4";
            codigoFinal = "8";
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
        cargarListaAnio();
        cargarListaMes();
        cargarListaTitulo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCentroCostoInicial();
        cargarListaCentroCostoFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        cargarListaAuxiliarInicial();
        cargarListaAuxiliarFinal();
        cargarListaFuenteRecursoInicial();
        cargarListaFuenteRecursoFinal();
        cargarListaReferenciaInicial();
        cargarListaReferenciaFinal();
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
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL4828
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
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL5322
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
     * Carga la lista listaTitulo
     *
     */
    public void cargarListaTitulo() {

        try {
            listaTitulo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL4199
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL6648
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL7929
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(CODIGO_INICIAL, codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    /**
     * 
     * Carga la lista listaCentroCostoInicial
     *
     */
    public void cargarListaCentroCostoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL13433
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroCostoFinal
     *
     */
    public void cargarListaCentroCostoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL14266
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTerceroInicial
     *
     */
    public void cargarListaTerceroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL15101
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NIT);

    }

    /**
     * 
     * Carga la lista listaTerceroFinal
     *
     */
    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL16130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NIT);

    }

    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL17165
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
    public void cargarListaAuxiliarFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL18172
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteRecursoInicial
     *
     */
    public void cargarListaFuenteRecursoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL21231
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteRecursoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuenteRecursoFinal
     *
     */
    public void cargarListaFuenteRecursoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL22291
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteRecursoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL19185
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferenciaFinal
     *
     */
    public void cargarListaReferenciaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEstadoFinancieraAuxiliaresControladorUrlEnum.URL20207
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

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
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
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
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String condicionSaldoCero;
            condicionSaldoCero = ckSaldoCero
                ? ""
                : "WHERE V_PLAN_CONTABLE.SALDO NOT IN (0) ";

            titulo = service.buscarEnLista(titulo, CODIGO, "TITULO",
                            listaTitulo);
            int mesAnterior = mes - 1;

            reemplazar.put("anoTrabajo", anio);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("mesFin", mes);
            reemplazar.put("mesAnt", mesAnterior);
            reemplazar.put("saldoCero", condicionSaldoCero);
            reemplazar.put("manTer", ckTercero ? "1" : "0");
            reemplazar.put("manAux", ckAuxiliar ? "1" : "0");
            reemplazar.put("manCen", ckCentroCosto ? "1" : "0");
            reemplazar.put("manRef", ckReferencia ? "1" : "0");
            reemplazar.put("manFue", ckFuenteRecurso ? "1" : "0");
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("auxiliarInicial", auxiliarInicial);
            reemplazar.put("auxiliarFinal", auxiliarFinal);
            reemplazar.put("centroInicial", centroCostoInicial);
            reemplazar.put("centroFinal", centroCostoFinal);
            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);
            reemplazar.put("fuenteInicial", fuenteRecursoInicial);
            reemplazar.put("fuenteFinal", fuenteRecursoFinal);

            if (ckSaldoCero) {
                reemplazar.put("digitosCond", digitos != null
                    ? "  WHERE LENGTH(V_PLAN_CONTABLE.CODIGO)<=" + digitos
                    : "");
            }
            else {
                reemplazar.put("digitosCond", digitos != null
                    ? " AND LENGTH(V_PLAN_CONTABLE.CODIGO)<=" + digitos
                    : "");
            }

            reemplazar.put("saldoCero", condicionSaldoCero);
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800326BaseEstadoFinancieraAux",
                            Integer.parseInt(modulo), reemplazar));

            // MANEJO DE PARAMETROS DE REEMPLAZO

            String firmaResultados1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA RESULTADOS 1", modulo,
                                            new Date(), true),
                            "FIRMA RESULTADOS 1");
            String cargoResultados1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO RESULTADOS 1", modulo,
                                            new Date(), true),
                            "CARGO RESULTADOS 1");
            String documentoResultados1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DOCUMENTO RESULTADOS 1", modulo,
                                            new Date(), true),
                            "DOCUMENTO RESULTADOS 1");
            String firmaResultados2 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA RESULTADOS 2", modulo,
                                            new Date(), true),
                            "FIRMA RESULTADOS 2");
            String cargoResultados2 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO RESULTADOS 2", modulo,
                                            new Date(), true),
                            "CARGO RESULTADOS 2");
            String docuemntoResultados2 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DOCUMENTO RESULTADOS 2", modulo,
                                            new Date(), true),
                            "DOCUMENTO RESULTADOS 2");
            String firmaResultados3 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA RESULTADOS 3", modulo,
                                            new Date(), true),
                            "FIRMA RESULTADOS 3");
            String cargoResultados3 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO RESULTADOS 3", modulo,
                                            new Date(), true),
                            "CARGO RESULTADOS 3");
            String documentoResultados3 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "DOCUMENTO RESULTADOS 3", modulo,
                                            new Date(), true),
                            "DOCUMENTO RESULTADOS 3");

            // MANEJO DE PARAMETROS DEL REPORTE
            String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes];

            String nombreMes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesAnterior];
            String entre = titulo + " " + nombreMes.toUpperCase() + " DE "
                + anio;

            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_MESANTERIOR", nombreMes1);
            parametros.put("PR_MESTRABAJO", nombreMes);
            parametros.put("PR_FIRMA_RESULTADOS_1",
                            firmaResultados1);
            parametros.put("PR_CARGO_RESULTADOS_1",
                            cargoResultados1);
            parametros.put("PR_DOCUMENTO_RESULTADOS_1",
                            documentoResultados1);
            parametros.put("PR_FIRMA_RESULTADOS_2",
                            firmaResultados2);
            parametros.put("PR_CARGO_RESULTADOS_2",
                            cargoResultados2);
            parametros.put("PR_DOCUMENTO_RESULTADOS_2",
                            docuemntoResultados2);
            parametros.put("PR_FIRMA_RESULTADOS_3",
                            firmaResultados3);
            parametros.put("PR_CARGO_RESULTADOS_3",
                            cargoResultados3);
            parametros.put("PR_DOCUMENTO_RESULTADOS_3",
                            documentoResultados3);
            parametros.put("PR_ENTRE",
                            entre);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put(TERCERO_VISIBLE, ckTercero);
            parametros.put(CENTRO_COSTO_VISIBLE, ckCentroCosto);
            parametros.put(AUXILIAR_VISIBLE, ckAuxiliar);
            parametros.put(REFERENCIA_VISIBLE, ckReferencia);
            parametros.put(FUENTE_RECURSO_VISIBLE, ckFuenteRecurso);
            parametros.put(FRT_ESPECIAL_VISIBLE, ckFormatoEspecial);

            Reporteador.resuelveConsulta(NOMBRE_REPORTE,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            NOMBRE_REPORTE, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        listaCodigoInicial = null;
        listaCodigoFinal = null;

        cargarListaMes();
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckCentroCosto
     * 
     * 
     */
    public void cambiarckCentroCosto() {
        // <CODIGO_DESARROLLADO>
        listaCentroCostoInicial = null;
        listaCentroCostoFinal = null;

        cargarListaCentroCostoInicial();
        cargarListaCentroCostoFinal();

        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckTercero
     * 
     * 
     */
    public void cambiarckTercero() {
        // <CODIGO_DESARROLLADO>
        listaTerceroInicial = null;
        listaTerceroFinal = null;

        cargarListaTerceroInicial();
        cargarListaTerceroFinal();

        terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckAuxiliar
     * 
     * 
     */
    public void cambiarckAuxiliar() {
        // <CODIGO_DESARROLLADO>
        listaAuxiliarInicial = null;
        listaAuxiliarFinal = null;

        cargarListaAuxiliarInicial();
        cargarListaAuxiliarFinal();

        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckFuenteRecurso
     * 
     * 
     */
    public void cambiarckFuenteRecurso() {
        // <CODIGO_DESARROLLADO>
        listaFuenteRecursoInicial = null;
        listaFuenteRecursoFinal = null;

        cargarListaFuenteRecursoInicial();
        cargarListaFuenteRecursoFinal();

        fuenteRecursoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteRecursoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckReferencia
     * 
     * 
     */
    public void cambiarckReferencia() {
        // <CODIGO_DESARROLLADO>
        listaReferenciaInicial = null;
        listaReferenciaFinal = null;

        cargarListaReferenciaInicial();
        cargarListaReferenciaFinal();

        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarckFormatoEspecial() {
        // <CODIGO_DESARROLLADO>
        if (ckFormatoEspecial) {
            ckSoloSaldos = false;

        }
        // </CODIGO_DESARROLLADO>

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        codigoInicial = registroAux.getCampos().get(CODIGO).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(CODIGO).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        centroCostoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        centroCostoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NIT), " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NIT), " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliarInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliarFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecursoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecursoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        fuenteRecursoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecursoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecursoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        fuenteRecursoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        referenciaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        referenciaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ckCentroCosto
     * 
     * @return ckCentroCosto
     */
    public boolean getCkCentroCosto() {
        return ckCentroCosto;
    }

    /**
     * Asigna la variable ckCentroCosto
     * 
     * @param ckCentroCosto
     * Variable a asignar en ckCentroCosto
     */
    public void setCkCentroCosto(boolean ckCentroCosto) {
        this.ckCentroCosto = ckCentroCosto;
    }

    /**
     * Retorna la variable ckTercero
     * 
     * @return ckTercero
     */
    public boolean getCkTercero() {
        return ckTercero;
    }

    /**
     * Asigna la variable ckTercero
     * 
     * @param ckTercero
     * Variable a asignar en ckTercero
     */
    public void setCkTercero(boolean ckTercero) {
        this.ckTercero = ckTercero;
    }

    /**
     * Retorna la variable ckAuxiliar
     * 
     * @return ckAuxiliar
     */
    public boolean getCkAuxiliar() {
        return ckAuxiliar;
    }

    /**
     * Asigna la variable ckAuxiliar
     * 
     * @param ckAuxiliar
     * Variable a asignar en ckAuxiliar
     */
    public void setCkAuxiliar(boolean ckAuxiliar) {
        this.ckAuxiliar = ckAuxiliar;
    }

    /**
     * Retorna la variable ckSaldoCero
     * 
     * @return ckSaldoCero
     */
    public boolean getCkSaldoCero() {
        return ckSaldoCero;
    }

    /**
     * Asigna la variable ckSaldoCero
     * 
     * @param ckSaldoCero
     * Variable a asignar en ckSaldoCero
     */
    public void setCkSaldoCero(boolean ckSaldoCero) {
        this.ckSaldoCero = ckSaldoCero;
    }

    /**
     * Retorna la variable ckFormatoEspecial
     * 
     * @return ckFormatoEspecial
     */
    public boolean getCkFormatoEspecial() {
        return ckFormatoEspecial;
    }

    /**
     * Asigna la variable ckFormatoEspecial
     * 
     * @param ckFormatoEspecial
     * Variable a asignar en ckFormatoEspecial
     */
    public void setCkFormatoEspecial(boolean ckFormatoEspecial) {
        this.ckFormatoEspecial = ckFormatoEspecial;
    }

    /**
     * Retorna la variable ckSoloSaldos
     * 
     * @return ckSoloSaldos
     */
    public boolean getCkSoloSaldos() {
        return ckSoloSaldos;
    }

    /**
     * Asigna la variable ckSoloSaldos
     * 
     * @param ckSoloSaldos
     * Variable a asignar en ckSoloSaldos
     */
    public void setCkSoloSaldos(boolean ckSoloSaldos) {
        this.ckSoloSaldos = ckSoloSaldos;
    }

    /**
     * Retorna la variable ckFuenteRecurso
     * 
     * @return ckFuenteRecurso
     */
    public boolean getCkFuenteRecurso() {
        return ckFuenteRecurso;
    }

    /**
     * Asigna la variable ckFuenteRecurso
     * 
     * @param ckFuenteRecurso
     * Variable a asignar en ckFuenteRecurso
     */
    public void setCkFuenteRecurso(boolean ckFuenteRecurso) {
        this.ckFuenteRecurso = ckFuenteRecurso;
    }

    /**
     * Retorna la variable ckReferencia
     * 
     * @return ckReferencia
     */
    public boolean getCkReferencia() {
        return ckReferencia;
    }

    /**
     * Asigna la variable ckReferencia
     * 
     * @param ckReferencia
     * Variable a asignar en ckReferencia
     */
    public void setCkReferencia(boolean ckReferencia) {
        this.ckReferencia = ckReferencia;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(int mes) {
        this.mes = mes;
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
     * Retorna la variable titulo
     * 
     * @return titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna la variable titulo
     * 
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Retorna la variable CentroCostoInicial
     * 
     * @return CentroCostoInicial
     */
    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    /**
     * Asigna la variable CentroCostoInicial
     * 
     * @param CentroCostoInicial
     * Variable a asignar en CentroCostoInicial
     */
    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    /**
     * Retorna la variable CentroCostoFinal
     * 
     * @return CentroCostoFinal
     */
    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    /**
     * Asigna la variable CentroCostoFinal
     * 
     * @param CentroCostoFinal
     * Variable a asignar en CentroCostoFinal
     */
    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable auxiliarInicial
     * 
     * @return auxiliarInicial
     */
    public String getAuxiliarInicial() {
        return auxiliarInicial;
    }

    /**
     * Asigna la variable auxiliarInicial
     * 
     * @param auxiliarInicial
     * Variable a asignar en auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }

    /**
     * Retorna la variable auxiliarFinal
     * 
     * @return auxiliarFinal
     */
    public String getAuxiliarFinal() {
        return auxiliarFinal;
    }

    /**
     * Asigna la variable auxiliarFinal
     * 
     * @param auxiliarFinal
     * Variable a asignar en auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }

    /**
     * Retorna la variable fuenteRecursoInicial
     * 
     * @return fuenteRecursoInicial
     */
    public String getFuenteRecursoInicial() {
        return fuenteRecursoInicial;
    }

    /**
     * Asigna la variable fuenteRecursoInicial
     * 
     * @param fuenteRecursoInicial
     * Variable a asignar en fuenteRecursoInicial
     */
    public void setFuenteRecursoInicial(String fuenteRecursoInicial) {
        this.fuenteRecursoInicial = fuenteRecursoInicial;
    }

    /**
     * Retorna la variable fuenteRecursoFinal
     * 
     * @return fuenteRecursoFinal
     */
    public String getFuenteRecursoFinal() {
        return fuenteRecursoFinal;
    }

    /**
     * Asigna la variable fuenteRecursoFinal
     * 
     * @param fuenteRecursoFinal
     * Variable a asignar en fuenteRecursoFinal
     */
    public void setFuenteRecursoFinal(String fuenteRecursoFinal) {
        this.fuenteRecursoFinal = fuenteRecursoFinal;
    }

    /**
     * Retorna la variable referenciaInicial
     * 
     * @return referenciaInicial
     */
    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     * 
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     * 
     * @return referenciaFinal
     */
    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     * 
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    /**
     * Retorna la variable digitos
     * 
     * @return digitos
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * Asigna la variable digitos
     * 
     * @param digitos
     * Variable a asignar en digitos
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
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
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaTitulo
     * 
     * @return listaTitulo
     */
    public List<Registro> getListaTitulo() {
        return listaTitulo;
    }

    /**
     * Asigna la lista listaTitulo
     * 
     * @param listaTitulo
     * Variable a asignar en listaTitulo
     */
    public void setListaTitulo(List<Registro> listaTitulo) {
        this.listaTitulo = listaTitulo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    /**
     * Retorna la lista listaCentroCostoInicial
     * 
     * @return listaCentroCostoInicial
     */
    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }

    /**
     * Asigna la lista listaCentroCostoInicial
     * 
     * @param listaCentroCostoInicial
     * Variable a asignar en listaCentroCostoInicial
     */
    public void setListaCentroCostoInicial(
        RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    /**
     * Retorna la lista listaCentroCostoFinal
     * 
     * @return listaCentroCostoFinal
     */
    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }

    /**
     * Asigna la lista listaCentroCostoFinal
     * 
     * @param listaCentroCostoFinal
     * Variable a asignar en listaCentroCostoFinal
     */
    public void setListaCentroCostoFinal(
        RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
    }

    /**
     * Retorna la lista listaTerceroInicial
     * 
     * @return listaTerceroInicial
     */
    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    /**
     * Asigna la lista listaTerceroInicial
     * 
     * @param listaTerceroInicial
     * Variable a asignar en listaTerceroInicial
     */
    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    /**
     * Retorna la lista listaTerceroFinal
     * 
     * @return listaTerceroFinal
     */
    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    /**
     * Asigna la lista listaTerceroFinal
     * 
     * @param listaTerceroFinal
     * Variable a asignar en listaTerceroFinal
     */
    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    /**
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(
        RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }

    /**
     * Retorna la lista listaAuxiliarFinal
     * 
     * @return listaAuxiliarFinal
     */
    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    /**
     * Asigna la lista listaAuxiliarFinal
     * 
     * @param listaAuxiliarFinal
     * Variable a asignar en listaAuxiliarFinal
     */
    public void setListaAuxiliarFinal(
        RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    /**
     * Retorna la lista listaFuenteRecursoInicial
     * 
     * @return listaFuenteRecursoInicial
     */
    public RegistroDataModelImpl getListaFuenteRecursoInicial() {
        return listaFuenteRecursoInicial;
    }

    /**
     * Asigna la lista listaFuenteRecursoInicial
     * 
     * @param listaFuenteRecursoInicial
     * Variable a asignar en listaFuenteRecursoInicial
     */
    public void setListaFuenteRecursoInicial(
        RegistroDataModelImpl listaFuenteRecursoInicial) {
        this.listaFuenteRecursoInicial = listaFuenteRecursoInicial;
    }

    /**
     * Retorna la lista listaFuenteRecursoFinal
     * 
     * @return listaFuenteRecursoFinal
     */
    public RegistroDataModelImpl getListaFuenteRecursoFinal() {
        return listaFuenteRecursoFinal;
    }

    /**
     * Asigna la lista listaFuenteRecursoFinal
     * 
     * @param listaFuenteRecursoFinal
     * Variable a asignar en listaFuenteRecursoFinal
     */
    public void setListaFuenteRecursoFinal(
        RegistroDataModelImpl listaFuenteRecursoFinal) {
        this.listaFuenteRecursoFinal = listaFuenteRecursoFinal;
    }

    /**
     * Retorna la lista listaReferenciaInicial
     * 
     * @return listaReferenciaInicial
     */
    public RegistroDataModelImpl getListaReferenciaInicial() {
        return listaReferenciaInicial;
    }

    /**
     * Asigna la lista listaReferenciaInicial
     * 
     * @param listaReferenciaInicial
     * Variable a asignar en listaReferenciaInicial
     */
    public void setListaReferenciaInicial(
        RegistroDataModelImpl listaReferenciaInicial) {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }

    /**
     * Retorna la lista listaReferenciaFinal
     * 
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }

    /**
     * Asigna la lista listaReferenciaFinal
     * 
     * @param listaReferenciaFinal
     * Variable a asignar en listaReferenciaFinal
     */
    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
