/*-
 * BalanceGeneralPorAuxiliaresControlador.java
 *
 * 1.0
 * 
 * 12/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceGeneralPorAuxiliaresControladorUrlEnum;
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
 * Formulario que permite generar un informe del balance general por
 * auxiliares.
 *
 * @version 1.0, 12/10/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class BalanceGeneralPorAuxiliaresControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // <DECLARAR_ATRIBUTOS>

    private boolean indCentroCosto;
    private boolean indTercero;
    private boolean indAuxiliar;
    private boolean indReferencia;
    private boolean indFuenteRecurso;
    private boolean saldo;
    private String anio;
    private String codigoInicial;
    private String codigoFinal;
    private String centroInicial;
    private String centroFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String auxiliarInicial;
    private String auxiliarFinal;
    private String referenciaInicial;
    private String referenciaFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private int mes;
    private String digitos;
    private final String modulo;
    private boolean formatoEspecialExcel;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnio;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaAuxiliarFinal;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCentroInicial;
    private RegistroDataModelImpl listaCentroFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaAuxiliarInicial;
    private RegistroDataModelImpl listaReferenciaInicial;
    private RegistroDataModelImpl listaReferenciaFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * BalanceGeneralPorAuxiliaresControlador
     */
    public BalanceGeneralPorAuxiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCEGENERALPORAUXILIARES_CONTROLADOR
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
        digitos = "6";
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = 1;
        // <CARGAR_LISTA>
        cargarListaAnio();
        cargarListaAuxiliarFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaCentroInicial();
        cargarListaCentroFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaReferenciaFinal();
        cargarListaFuenteInicial();
        cargarListaFuenteFinal();
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
            listaAnio = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            BalanceGeneralPorAuxiliaresControladorUrlEnum.URL11002
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
    public void cargarListaAuxiliarFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL18172
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("CODIGOINICIAL", auxiliarInicial);

        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL11513
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL12468
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("CODIGOINICIAL", codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroInicial
     *
     */
    public void cargarListaCentroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL13433
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCentroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroFinal
     *
     */
    public void cargarListaCentroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL14266
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        listaCentroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
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
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL15101
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaTerceroFinal
     *
     */
    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL16130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINICIAL", terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL17165
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
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL19185
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
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL20207
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("REFERENCIAINICIAL", referenciaInicial);

        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteInicial
     *
     */
    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL21231
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteFinal
     *
     */
    public void cargarListaFuenteFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceGeneralPorAuxiliaresControladorUrlEnum.URL22291
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("FUENTEINICIAL", fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
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
        generarInforme(FORMATOS.PDF);
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
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato) {
        try {
            if (validarCheck()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4233"));
                return;
            }
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String informe = "";
            String consulta = "";

            String calidad = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CALIDAD", SessionUtil.getModulo(),
                            new Date(),
                            true);

            reemplazar.put("mesFinal", mes);
            reemplazar.put("mestrabajo", mes);
            reemplazar.put("anio", anio);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal + "9");
            reemplazar.put("codigoFin", "'" + codigoFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("digitos1", digitos);
            reemplazar.put("manTer", indTercero ? "1" : "0");
            reemplazar.put("manAux", indAuxiliar ? "1" : "0");
            reemplazar.put("manCen", indCentroCosto ? "1" : "0");
            reemplazar.put("manRef", indReferencia ? "1" : "0");
            reemplazar.put("manFue", indFuenteRecurso ? "1" : "0");
            reemplazar.put("mesFin", mes);
            reemplazar.put("mesIni", mes);
            reemplazar.put("mayoriza", "1");
            if (indTercero) {
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            }else {
                reemplazar.put("terceroInicial", "0");
                reemplazar.put("terceroFinal", "9999999999999999999");
            }
            if (indAuxiliar) {
                reemplazar.put("auxiliarInicial", auxiliarInicial);
                reemplazar.put("auxiliarFinal", auxiliarFinal);
            }else {
                reemplazar.put("auxiliarInicial", "0");
                reemplazar.put("auxiliarFinal", "9999999999999999999");
            }
            if (indCentroCosto) {
                reemplazar.put("centroInicial", centroInicial);
                reemplazar.put("centroFinal", centroFinal);
            }else {
                reemplazar.put("centroInicial", "0");
                reemplazar.put("centroFinal", "9999999999999999999");
            }
            if (indReferencia) {
                reemplazar.put("referenciaInicial", referenciaInicial);
                reemplazar.put("referenciaFinal", referenciaFinal);
            }else {
                reemplazar.put("referenciaInicial", "0");
                reemplazar.put("referenciaFinal", "9999999999999999999");
            }
            if (indFuenteRecurso) {
                reemplazar.put("fuenteInicial", fuenteInicial);
                reemplazar.put("fuenteFinal", fuenteFinal);
            }else {
                reemplazar.put("fuenteInicial", "0");
                reemplazar.put("fuenteFinal", "9999999999999999999");
            }
            
            
            reemplazar.put("auxiliarTercero", indTercero
                ? "AND SALDO_AUX_CONTABLE.TERCERO BETWEEN '" + terceroInicial
                    + "'  AND '" + terceroFinal + "'"
                : "");
            reemplazar.put("auxiliarGeneral", indAuxiliar
                ? "AND SALDO_AUX_CONTABLE.AUXILIAR BETWEEN '" + auxiliarInicial
                    + "'  AND '" + auxiliarFinal + "'"
                : "");
            reemplazar.put("auxiliarCentroCosto", indCentroCosto
                ? "AND SALDO_AUX_CONTABLE.CENTRO_COSTO BETWEEN '"
                    + centroInicial
                    + "'   AND '" + centroFinal + "'"
                : "");
            reemplazar.put("auxiliarReferencia", indReferencia
                ? "AND SALDO_AUX_CONTABLE.REFERENCIA BETWEEN '"
                    + referenciaInicial
                    + "' AND '" + referenciaFinal + "'"
                : "");
            reemplazar.put("auxiliarFteRecurso", indFuenteRecurso
                ? "AND SALDO_AUX_CONTABLE.FUENTE_RECURSO BETWEEN '"
                    + fuenteInicial + "' AND '" + fuenteFinal + "'"
                : "");

            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalancesAuxiliares",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            if (saldo) {
                reemplazar.put("saldoCero", " ");
                reemplazar.put("saldoCeroExt", " ");
                reemplazar.put("condicion", " ");
            }
            else {
                reemplazar.put("saldoCero", " AND PLAN_CONTABLE.SALDO"
                    + mes + "<> 0");
                reemplazar.put("saldoCeroExt",
                                " AND SALDO" + mes + "<> 0");
                reemplazar.put("condicion", "AND V_PLAN_CONTABLE.SALDO"
                    + mes + " <> 0");
            }

            if ("SI".equals(calidad)) {
                informe = "000568BalanceGeneralCOS";
                consulta = informe;
            }
            else {
                informe = "002583BalanceGeneralAuxiliares";
                
                String strSqlSub = Reporteador.resuelveConsulta(informe,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
                
                parametros.put("PR_STRSQLDETALLE", strSqlSub);
                reemplazar.put("consultaTabla", strSqlSub);
                
                consulta = "800627InformeTabla";
                
            }

            String titulo;
            if ("SI".equals(calidad)) {
                titulo = "DEL MES DE "
                    + (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes])
                                    .toUpperCase()
                    + " DE "
                    + anio + "";
            }
            else {
                titulo = "BALANCE GENERAL DEL MES DE "
                    + (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes])
                                    .toUpperCase()
                    + " DE " + anio
                    + "";
            }

            String firmaCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), true);

            String cargoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), true);

            String documentoCont1 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 1", modulo,
                            new Date(), true);

            String firmaCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), true);

            String cargoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), true);

            String documentoCont2 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 2", modulo,
                            new Date(), true);

            String firmaCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), true);

            String cargoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), true);

            String documentoCont3 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 3", modulo,
                            new Date(), true);

            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_FIRMA_CONTABLE_1", firmaCont1);
            parametros.put("PR_FIRMA_CONTABLE_2", firmaCont2);
            parametros.put("PR_CARGO_CONTABLE_1", cargoCont1);
            parametros.put("PR_CARGO_CONTABLE_2", cargoCont2);
            parametros.put("PR_DOCUMENTO_CONTABLE_1", documentoCont1);
            parametros.put("PR_DOCUMENTO_CONTABLE_2", documentoCont2);
            parametros.put("PR_VER_CENTROC", indCentroCosto);
            parametros.put("PR_VER_AUXILIAR", indAuxiliar);
            parametros.put("PR_VER_REFERENCIA", indReferencia);
            parametros.put("PR_VER_TERCERO", indTercero);
            parametros.put("PR_VER_FUENTE", indFuenteRecurso);

            String firmaTres = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, "GENERA FIRMA CONTABLE 3",
                                            modulo,
                                            new Date(), true), "NO");

            if ("SI".equals(firmaTres)) {
                parametros.put("PR_FIRMA_CONTABLE_3", firmaCont3);
                parametros.put("PR_CARGO_CONTABLE_3", cargoCont3);
                parametros.put("PR_DOCUMENTO_CONTABLE_3", documentoCont3);
            }

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", true);

            Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            System.out.print(parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarAnio() {
        cargarListaAuxiliarInicial();
        cargarListaAuxiliarFinal();
        cargarListaCentroInicial();
        cargarListaCentroFinal();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaFuenteInicial();
        cargarListaFuenteFinal();
        cargarListaReferenciaInicial();
        cargarListaReferenciaFinal();
    }

    public boolean validarCheck() {
        return !indCentroCosto && !indAuxiliar && !indTercero && !indReferencia
                        && !indFuenteRecurso;
    }

    /**
     * Metodo ejecutado al cambiar el control IndCentroCosto
     * 
     * 
     */
    public void cambiarIndCentroCosto() {
        // <CODIGO_DESARROLLADO>
//        indAuxiliar = indFuenteRecurso = indReferencia = indTercero = false;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndTercero
     * 
     * 
     */
    public void cambiarIndTercero() {
        // <CODIGO_DESARROLLADO>
//        indAuxiliar = indFuenteRecurso = indReferencia = indCentroCosto = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndAuxiliar
     * 
     * 
     */
    public void cambiarIndAuxiliar() {
        // <CODIGO_DESARROLLADO>
//        indCentroCosto = indFuenteRecurso = indReferencia = indTercero = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndReferencia
     * 
     * 
     */
    public void cambiarIndReferencia() {
        // <CODIGO_DESARROLLADO>
//        indAuxiliar = indFuenteRecurso = indCentroCosto = indTercero = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndFuente
     * 
     * 
     */
    public void cambiarIndFuente() {
        // <CODIGO_DESARROLLADO>
//        indAuxiliar = indCentroCosto = indReferencia = indTercero = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndSaldo
     * 
     * 
     */
    public void cambiarIndSaldo() {
        // <CODIGO_DESARROLLADO>
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
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
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
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaCentroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        cargarListaTerceroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaAuxiliarFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaReferenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaFuenteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isIndCentroCosto() {
        return indCentroCosto;
    }

    public void setIndCentroCosto(boolean indCentroCosto) {
        this.indCentroCosto = indCentroCosto;
    }

    public boolean isIndTercero() {
        return indTercero;
    }

    public void setIndTercero(boolean indTercero) {
        this.indTercero = indTercero;
    }

    public boolean isIndAuxiliar() {
        return indAuxiliar;
    }

    public void setIndAuxiliar(boolean indAuxiliar) {
        this.indAuxiliar = indAuxiliar;
    }

    public boolean isIndReferencia() {
        return indReferencia;
    }

    public void setIndReferencia(boolean indReferencia) {
        this.indReferencia = indReferencia;
    }

    public boolean isIndFuenteRecurso() {
        return indFuenteRecurso;
    }

    public void setIndFuenteRecurso(boolean indFuenteRecurso) {
        this.indFuenteRecurso = indFuenteRecurso;
    }

    public boolean isSaldo() {
        return saldo;
    }

    public void setSaldo(boolean saldo) {
        this.saldo = saldo;
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
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
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
     * Retorna la variable centroInicial
     * 
     * @return centroInicial
     */
    public String getCentroInicial() {
        return centroInicial;
    }

    /**
     * Asigna la variable centroInicial
     * 
     * @param centroInicial
     * Variable a asignar en centroInicial
     */
    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    /**
     * Retorna la variable centroFinal
     * 
     * @return centroFinal
     */
    public String getCentroFinal() {
        return centroFinal;
    }

    /**
     * Asigna la variable centroFinal
     * 
     * @param centroFinal
     * Variable a asignar en centroFinal
     */
    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
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
     * Retorna la variable fuenteInicial
     * 
     * @return fuenteInicial
     */
    public String getFuenteInicial() {
        return fuenteInicial;
    }

    /**
     * Asigna la variable fuenteInicial
     * 
     * @param fuenteInicial
     * Variable a asignar en fuenteInicial
     */
    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    /**
     * Retorna la variable fuenteFinal
     * 
     * @return fuenteFinal
     */
    public String getFuenteFinal() {
        return fuenteFinal;
    }

    /**
     * Asigna la variable fuenteFinal
     * 
     * @param fuenteFinal
     * Variable a asignar en fuenteFinal
     */
    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
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
     * Retorna la lista listaCentroInicial
     * 
     * @return listaCentroInicial
     */
    public RegistroDataModelImpl getListaCentroInicial() {
        return listaCentroInicial;
    }

    /**
     * Asigna la lista listaCentroInicial
     * 
     * @param listaCentroInicial
     * Variable a asignar en listaCentroInicial
     */
    public void setListaCentroInicial(
        RegistroDataModelImpl listaCentroInicial) {
        this.listaCentroInicial = listaCentroInicial;
    }

    /**
     * Retorna la lista listaCentroFinal
     * 
     * @return listaCentroFinal
     */
    public RegistroDataModelImpl getListaCentroFinal() {
        return listaCentroFinal;
    }

    /**
     * Asigna la lista listaCentroFinal
     * 
     * @param listaCentroFinal
     * Variable a asignar en listaCentroFinal
     */
    public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) {
        this.listaCentroFinal = listaCentroFinal;
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

    /**
     * Retorna la lista listaFuenteInicial
     * 
     * @return listaFuenteInicial
     */
    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    /**
     * Asigna la lista listaFuenteInicial
     * 
     * @param listaFuenteInicial
     * Variable a asignar en listaFuenteInicial
     */
    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    /**
     * Retorna la lista listaFuenteFinal
     * 
     * @return listaFuenteFinal
     */
    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    /**
     * Asigna la lista listaFuenteFinal
     * 
     * @param listaFuenteFinal
     * Variable a asignar en listaFuenteFinal
     */
    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }

    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    public void setListaAuxiliarFinal(
        RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	/**
	 * @return the formatoEspecialExcel
	 */
	public boolean isFormatoEspecialExcel() {
		return formatoEspecialExcel;
	}

	/**
	 * @param formatoEspecialExcel the formatoEspecialExcel to set
	 */
	public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
		this.formatoEspecialExcel = formatoEspecialExcel;
	}
	
	

	/**
	 * @return the mesFinal
	 */
    

    // </SET_GET_LISTAS_COMBO_GRANDE>
}