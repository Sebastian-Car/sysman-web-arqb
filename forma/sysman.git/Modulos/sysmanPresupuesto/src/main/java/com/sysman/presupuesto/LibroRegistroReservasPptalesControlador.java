package com.sysman.presupuesto;

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
import com.sysman.presupuesto.enums.LibroRegistroReservasPptalesControladorEnum;
import com.sysman.presupuesto.enums.LibroRegistroReservasPptalesControladorUrlEnum;
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
 *
 * @author jrodriguezr
 * @version 1, 29/06/2016
 * @version 2, 19/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @version 4.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class LibroRegistroReservasPptalesControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean centroCosto;
    private boolean fuenteRecursos;
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String centroInicial;
    private String centroFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String nivel;
    private String nmes1;
    private String nmes2;
    private StreamedContent archivoDescarga;
    private boolean formatoEspecialExcel = false;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMes1;
    private List<Registro> listames2;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listacentrocostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * LibroRegistroReservasPptalesControlador
     */
    public LibroRegistroReservasPptalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 957
            numFormulario = GeneralCodigoFormaEnum.LIBRO_REGISTRO_RESERVAS_PPTALES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LibroRegistroReservasPptalesControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        nivel = "60";
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMes1();
        mesInicial = "1";
        cargarListames2();
        mesFinal = "1";
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        indicador = true;
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMes1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroRegistroReservasPptalesControladorUrlEnum.URL4492
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListames2() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
        try {
            listames2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroRegistroReservasPptalesControladorUrlEnum.URL4924
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroRegistroReservasPptalesControladorUrlEnum.URL5425
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroReservasPptalesControladorUrlEnum.URL5783
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroReservasPptalesControladorUrlEnum.URL6845
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LibroRegistroReservasPptalesControladorEnum.CUENTAINICIAL
                        .getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroReservasPptalesControladorUrlEnum.URL8043
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacentrocostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroReservasPptalesControladorUrlEnum.URL8771
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LibroRegistroReservasPptalesControladorEnum.CENTRO_COSTO
                        .getValue(), centroInicial);
        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFuenteInicial() {
        //
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroReservasPptalesControladorUrlEnum.URL9507
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFuenteFinal() {
        //
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroReservasPptalesControladorUrlEnum.URL10142
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LibroRegistroReservasPptalesControladorEnum.AUXILIARINICIAL
                        .getValue(), fuenteInicial);
        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    public void oprimirExcel() {
    	try {           
    		HashMap<String, Object> reemplazar = new HashMap<>();

    		if (formatoEspecialExcel) {
    			// Limpiar y volver a llenar el HashMap reemplazar para el formato especial
    			reemplazar.clear();
    			reemplazar.put("cuentaInicial", cuentaInicial);
    			reemplazar.put("cuentaFinal", cuentaFinal);
    			reemplazar.put("centroInicial", centroInicial);
    			reemplazar.put("centroFinal", centroFinal);
    			reemplazar.put("fuenteInicial", fuenteInicial);
    			reemplazar.put("fuenteFinal", fuenteFinal);
    			reemplazar.put("mesInicial", mesInicial);
    			reemplazar.put("mesFinal", mesFinal);
    			reemplazar.put("anio", anio);
    			reemplazar.put("nivel", nivel);
    			reemplazar.put("centroCostoCond",
    					centroCosto
    					? "  AND PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
    					+ centroInicial + "'"
    					+ " AND '" + centroFinal + "'"
    					: "");
    			reemplazar.put("fuenteRecursoCond",
    					fuenteRecursos
    					? " AND PLAN_PRESUPUESTAL.AUXILIAR BETWEEN '"
    					+ fuenteInicial + "'" +
    					"       AND '" + fuenteFinal + "'"
    					: "");

    			String periodoTexto = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)].toUpperCase()
    					+ " a "
    					+ SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesFinal)].toUpperCase();

    			HashMap<String, Object> parametros = new HashMap<>();
    			parametros.put("PR_NOMBRECOMPANIA",
    					SessionUtil.getCompaniaIngreso().getNombre());
    			
    			parametros.put("PR_PERIODO", periodoTexto);

    			Reporteador.resuelveConsulta("8000578Fcregistroreservapptal036_Plano",
    					Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

    			archivoDescarga = JsfUtil.exportarStreamed("8000578Fcregistroreservapptal036_Plano",
    					parametros,
    					ConectorPool.ESQUEMA_SYSMAN,
    					FORMATOS.EXCEL);

    		} else {
    			archivoDescarga = null;
    			generaReporte(FORMATOS.EXCEL);
    		}

    	} catch (JRException | IOException | SysmanException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }
    
    
    public void cambiarExcelPlanoCheck() {
    }

    private void generaReporte(FORMATOS formato) {

        if (faltanCamposObligatorios()) {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        reemplazar.put("cuentaInicial", cuentaInicial);
        reemplazar.put("cuentaFinal", cuentaFinal);
        reemplazar.put("centroInicial", centroInicial);
        reemplazar.put("centroFinal", centroFinal);
        reemplazar.put("fuenteInicial", fuenteInicial);
        reemplazar.put("fuenteFinal", fuenteFinal);
        reemplazar.put("mesInicial", mesInicial);
        reemplazar.put("mesFinal", mesFinal);
        reemplazar.put("anio", anio);
        reemplazar.put("nivel", nivel);
        reemplazar.put("centroCostoCond",
                        centroCosto
                            ? "  AND PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
                                + centroInicial + "'"
                                + " AND '" + centroFinal + "'"
                            : "");
        reemplazar.put("fuenteRecursoCond",
                        fuenteRecursos
                            ? " AND PLAN_PRESUPUESTAL.AUXILIAR BETWEEN '"
                                + fuenteInicial + "'" +
                                "       AND '" + fuenteFinal + "'"
                            : "");

        String unidad = "";
        String conUnidad = "";
        String conSeccion = "";
        String seccion = "";
        String conRegional = "";
        String regional = "";

        String seccionInfRes036 = getParametro(
                        "SECCION EN INFORMES RESOLUCION 036",
                        "NO");
        if ("SI".equals(seccionInfRes036)) {
            conSeccion = getParametro("SECCION 036", "");
            if (!"".equals(conSeccion)) {
                seccion = idioma.getString("TG_SECCION");
            }
            else {
                seccion = "";
            }
            conUnidad = getParametro("UNIDAD EJECUTORA 036", "");
            if (!"".equals(conUnidad)) {
                unidad = idioma.getString("TG_UNIDAD_EJECUTORA");
            }
            else {
                unidad = "";
            }
            conRegional = getParametro("REGIONAL 036", "");
            if (!"".equals(conRegional)) {
                regional = idioma.getString("TG_REGIONAL");
            }
            else {
                regional = "";
            }
        }
        String nivel1i = getParametro("NIVEL 1I", "");
        String nivel1f = getParametro("NIVEL 1F", "");
        String nivel2i = getParametro("NIVEL 2I", "");
        String nivel2f = getParametro("NIVEL 2F", "");
        String nivel3i = getParametro("NIVEL 3I", "");
        String nivel3f = getParametro("NIVEL 3F", "");
        String nivel4i = getParametro("NIVEL 4I", "");
        String nivel4f = getParametro("NIVEL 4F", "");
        String nivel5i = getParametro("NIVEL 5I", "");
        String nivel5f = getParametro("NIVEL 5F", "");
        String nivel6i = getParametro("NIVEL 6I", "");
        String nivel6f = getParametro("NIVEL 6F", "");

        parametros.put("PR_INDICADOR", indicador);
        parametros.put("PR_NIVEL_1I", nivel1i);
        parametros.put("PR_NIVEL_1F", nivel1f);
        parametros.put("PR_NIVEL_2I", nivel2i);
        parametros.put("PR_NIVEL_2F", nivel2f);
        parametros.put("PR_NIVEL_3I", nivel3i);
        parametros.put("PR_NIVEL_3F", nivel3f);
        parametros.put("PR_NIVEL_4I", nivel4i);
        parametros.put("PR_NIVEL_4F", nivel4f);
        parametros.put("PR_NIVEL_5I", nivel5i);
        parametros.put("PR_NIVEL_5F", nivel5f);
        parametros.put("PR_NIVEL_6I", nivel6i);
        parametros.put("PR_NIVEL_6F", nivel6f);

        parametros.put("PR_MES1", mesInicial);
        parametros.put("PR_MES2", mesFinal);
        parametros.put("PR_REGIONAL", regional);
        parametros.put("PR_CONREGIONAL", conRegional);
        parametros.put("PR_UNIDAD", unidad);
        parametros.put("PR_CONUNIDAD", conUnidad);
        parametros.put("PR_SECCION", seccion);
        parametros.put("PR_CONSECCION", conSeccion);
        parametros.put("PR_ANO", anio);
        parametros.put("PR_NMES2", nmes2);
        parametros.put("PR_NMES1", nmes1);
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());

        archivoDescarga = getReporte(reemplazar, parametros, formato);

    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Genera el reporte como objeto tipo StreamedContent.
     *
     * @param reporte
     * Nombre del reporte.
     * @param reemplazos
     * Reemplazos para resolver la consulta.
     * @param parametros
     * Parametros necesarios para generar el reporte.
     * @param formato
     * Formato con el que se generara el informe.
     * @return archivo para descarga.
     */
    private StreamedContent getReporte(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, FORMATOS formato) {

        String reporte = "000956FCREGISTRORESERVAPPTAL036";

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return archivoDescarga;
    }

    /**
     * Validacion de campos obligatorios.
     *
     * @return Verdadero en caso de que algun campo obligatorio este
     * nulo o vacio.
     */
    private boolean faltanCamposObligatorios() {
        boolean faltanCampos = false;
        if (faltaCentroCosto() || faltaFuenteRecursos()) {
            faltanCampos = true;
        }
        if (estaVacio(cuentaInicial, "MSM_DEBE_CUENTA_INI")
            || estaVacio(cuentaFinal, "MSM_DEBE_CUENTA_FIN")) {
            faltanCampos = true;
        }
        if (estaVacio(mesInicial, "MSM_DEBE_MES_INI") ||
            estaVacio(mesFinal, "MSM_DEBE_MES_FIN") ||
            estaVacio(anio, "MSM_DEBE_ANO")) {
            faltanCampos = true;
        }
        return faltanCampos;
    }

    /**
     * Si se requiere, valida si los campos de fuente recursos son
     * nulos o vacios.
     *
     * @return Verdadero si faltan campos de fuente recursos.
     */
    private boolean faltaFuenteRecursos() {
        return fuenteRecursos
            && (estaVacio(fuenteInicial, "MSM_DEBE_FUENTE_INI")
                || estaVacio(fuenteFinal, "MSM_DEBE_FUENTE_FIN"));
    }

    /**
     * Si se requiere, valida si los campos de centro de costo son
     * nulos o vacios.
     *
     * @return Verdadero si faltan campos de centro de costo.
     */
    private boolean faltaCentroCosto() {
        return centroCosto && (estaVacio(centroInicial, "MSM_DEBE_CENTRO_INI")
            || estaVacio(centroFinal, "MSM_DEBE_CENTRO_FIN"));
    }

    /**
     * Verifica si el campo esta nulo o vacio, de ser el caso muestra
     * el mensaje de alerta.
     *
     * @param campo
     * Cadena a validar.
     * @param textoProperties
     * Nombre de la propiedad que contiene el mensaje de alerta.
     * @return Verdadero si el campo esta vacio o nulo.
     */
    private boolean estaVacio(String campo, String textoProperties) {
        if (SysmanFunciones.validarVariableVacio(campo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(textoProperties));
            return true;
        }
        return false;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        cargarListames2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmes2() {
        // <CODIGO_DESARROLLADO>
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesFinal)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mesInicial = mesFinal = nmes1 = nmes2 = cuentaInicial = cuentaFinal = centroInicial = centroFinal = fuenteInicial = fuenteFinal = null;
        cargarListaMes1();
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcentroCosto() {
        // <CODIGO_DESARROLLADO>
        cargarListacentrocostoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfuenterecursos() {
        // <CODIGO_DESARROLLADO>
        cargarListaFuenteInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListacentrocostoFinal();
        centroFinal = null;
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaFuenteFinal();
        fuenteFinal = null;
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public boolean getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public boolean getFuenteRecursos() {
        return fuenteRecursos;
    }

    public void setFuenteRecursos(boolean fuenteRecursos) {
        this.fuenteRecursos = fuenteRecursos;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getCentroInicial() {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal() {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
    }

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNmes1() {
        return nmes1;
    }

    public void setNmes1(String nmes1) {
        this.nmes1 = nmes1;
    }

    public String getNmes2() {
        return nmes2;
    }

    public void setNmes2(String nmes2) {
        this.nmes2 = nmes2;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListames2() {
        return listames2;
    }

    public void setListames2(List<Registro> listames2) {
        this.listames2 = listames2;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListacentrocostoInicial() {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial) {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListacentrocostoFinal() {
        return listacentrocostoFinal;
    }

    public void setListacentrocostoFinal(
        RegistroDataModelImpl listacentrocostoFinal) {
        this.listacentrocostoFinal = listacentrocostoFinal;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

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
    
}

