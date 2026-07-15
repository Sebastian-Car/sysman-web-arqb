package com.sysman.predial;

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
import com.sysman.predial.enums.DeudoresPredialControladorEnum;
import com.sysman.predial.enums.DeudoresPredialControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
 * @version 1, 02/06/2016 10:39:57 -- Modificado por jrodriguezr
 * 
 * @version 2 modificado por sdaza Marzo 6/2017 - se modifica el alias
 * o campos que se envian como parametros a la consulta
 * 
 * @version 3 modificado por sdaza Marzo 7/2017 - ajuste a los filtros
 * por rangos de años, se debe agregar atributo para cuando se
 * selecciona por ultimo año de pago
 * 
 * @version 3, 13/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @version 4, 28/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class DeudoresPredialControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String ordenar;
    private String tipo;
    private String anios;
    private boolean orden;
    private boolean resumen;
    private boolean sinLotes;
    private boolean proCobro;
    private boolean todosNombres;
    private boolean todasCedulas;
    private boolean todosCodigos;
    private boolean todosValores;
    private String porPredios;
    private String incluye;
    private String anioInicial;
    private String anioFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String cedulaInicial;
    private String cedulaAux;
    private String cedulaFinal;
    private String codigoInicial;
    private String codigoFinal;
    private String anioPago;
    private String codigoInicialM;
    private String codigoFinalM;
    private String cedulaInicialM;
    private String cedulaFinalM;
    private double deudaMenor;
    private double deudaMayor;
    private StreamedContent archivoDescarga;
    private static final String CODIGO = "CODIGO";
    private static final String NOMBRE = "NOMBRE";
    /**
     * Constante al seleccionar todos los nombres.
     */
    private static final String TODOS_NOMBRES = "TODOS";
    /**
     * Constante para el texto "hasta" usado en el reporte.
     */
    private static final String TEXTO_HASTA = "  HASTA: ";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaanioini;
    private List<Registro> listaaniofin;
    private List<Registro> listaAnoPago;
    private List<Registro> listagenerarPorPred;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbCedulaInicial;
    private RegistroDataModelImpl listaCmbCedulaFinal;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaCmbNombreInicial;
    private RegistroDataModelImpl listaCmbNombreFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private boolean codigoVisible;
    private boolean valoresVisible;
    private boolean cedulaVisible;
    private boolean nombreVisible;
    private boolean aniosVisible;
    private boolean anioFinBloqueado;
    private boolean anioPagoBloqueado;
    private boolean anioIniBloqueado;
    private String nombrePredios;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of DeudoresPredialControlador
     */
    public DeudoresPredialControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.DEUDORES_PREDIAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(DeudoresPredialControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        tipo = "1";
        ordenar = "1";
        codigoVisible = true;
        anioPagoBloqueado = false;
        anioIniBloqueado = anioFinBloqueado = true;
        anioInicial = anioFinal = anioPago = String
                        .valueOf(SysmanFunciones.ano(new Date()));
        aniosVisible = true;
        anios = "1";
        deudaMenor = 0;
        deudaMayor = 9999999999.99;
        incluye = "1";
        cargarListaanioini();
        cargarListaaniofin();
        cargarListaAnoPago();
        cargarListagenerarPorPred();
        porPredios = "3";
        nombrePredios = service.buscarEnLista(porPredios, "NUMERO", NOMBRE,
                        listagenerarPorPred);
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbCedulaInicial();
        cargarListaCodigoInicial();
        cargarListaCmbNombreInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanioini() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaanioini = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DeudoresPredialControladorUrlEnum.URL6661
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaaniofin() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaaniofin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DeudoresPredialControladorUrlEnum.URL7053
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoPago() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAnoPago = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DeudoresPredialControladorUrlEnum.URL6661
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbCedulaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DeudoresPredialControladorUrlEnum.URL7920
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbCedulaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaCmbCedulaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DeudoresPredialControladorUrlEnum.URL8623
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DeudoresPredialControladorEnum.PARAM0.getValue(), cedulaAux);

        listaCmbCedulaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DeudoresPredialControladorUrlEnum.URL9496
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DeudoresPredialControladorUrlEnum.URL10491
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DeudoresPredialControladorEnum.PARAM0.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaCmbNombreInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DeudoresPredialControladorUrlEnum.URL11609
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCmbNombreInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NOMBRE);
    }

    public void cargarListaCmbNombreFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DeudoresPredialControladorUrlEnum.URL12554
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DeudoresPredialControladorEnum.PARAM1.getValue(),
                        nombreInicial);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCmbNombreFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NOMBRE);
    }

    public void cargarListagenerarPorPred() {
        try {
            listagenerarPorPred = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DeudoresPredialControladorUrlEnum.URL11075
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generaReporte(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String reporte = getReporte(reemplazar, parametros);
        if (reporte != null) {
            reemplazar.put("nroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            try {
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    private String getReporte(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros) {
        String codigosPrediosRurales = getParametro("CODIGOS PREDIOS RURALES",
                        "");
        String condicionTipoPredios = SysmanFunciones
                        .colocarComillas(codigosPrediosRurales);
        String condicion2;

        if (!"1".equals(anios)) {
            if (estaVacio(anioInicial, "TB_TB366")
                || estaVacio(anioFinal, "TB_TB377")) {
                return null;
            }
            condicion2 = "AND FACTURADOS.PREANO BETWEEN " + anioInicial
                + "  AND " + anioFinal + "";
        }
        else {
            if (estaVacio(anioPago, "TB_TB378")) {
                return null;
            }
            condicion2 = "AND USUARIOS_PREDIAL.PAGO_ANO BETWEEN " + anioPago
                + "   AND " + anioPago + "";
        }

        if (faltanCamposObligatorios()) {
            return null;
        }

        reemplazar.put("condicion2", condicion2);

        String reporte;

        reporte = getNombreReporte(condicionTipoPredios, reemplazar, parametros,
                        resumen ? true : false);

        return reporte;
    }

    /**
     * Obtiene los parametros de reemplazo de consulta y de reporte,
     * cuando el indicador de resumen esta desactivado.
     *
     * @param condicionTipoPredios
     * Condicion tipo de predios.
     * @param reemplazar
     * Reemplazos de consutla.
     * @param parametros
     * Parametros de reporte.
     * @return Nombre de reporte.
     */
    private String getNombreReporte(String condicionTipoPredios,
        HashMap<String, Object> reemplazar,
        Map<String, Object> parametros, boolean resumen) {
        String strAnos = "";
        String strAnoPago = "";
        StringBuilder sbTipoGeneracion = new StringBuilder("");
        StringBuilder sbCodigos = new StringBuilder("");
        StringBuilder sbCedulas = new StringBuilder();
        StringBuilder sbNombres = new StringBuilder("");
        StringBuilder sbTipoInforme = new StringBuilder("");
        String strLotes = "";
        StringBuilder sbValores = new StringBuilder("");
        String strProCobro = "";

        if ("1".equals(anios)) {
            strAnos = " ";
            strAnoPago = " AND  U.PAGO_ANO =  " + anioPago + " ";
            sbTipoGeneracion.append("\n ULTIMO AÑO PAGO: " + anioPago + " ");
        }
        else if ("3".equals(anios)) {
            strAnos = " AND F.PREANO BETWEEN 1970 AND "
                + SysmanFunciones.ano(new Date()) + " ";
            strAnoPago = "";
            sbTipoGeneracion.append("\n TODOS LOS AÑOS ");
        }
        else if ("2".equals(anios)) {
            strAnos = " AND  F.PREANO BETWEEN  " + anioInicial + "  AND  "
                + anioFinal + " ";
            strAnoPago = "";
            sbTipoGeneracion.append("\n VIGENCIA DESDE: " + anioInicial
                + " HASTA: " + anioFinal + " ");
        }

        procesarTipo(sbTipoGeneracion, sbCodigos, sbCedulas, sbNombres,
                        sbValores);

        StringBuilder sbOrdenarDesAsc = new StringBuilder(" ORDER BY ");
        procesarPorOrdenar(parametros, sbOrdenarDesAsc, sbTipoInforme);
        sbOrdenarDesAsc.append(orden ? "DESC" : "ASC");

        // sin lotes
        if (sinLotes) {
            strLotes = " AND U.AREA_CONSTRUIDA NOT IN (0) ";
            sbTipoInforme.append(idioma.getString("TB_TB412"));
        }
        // Incluir o excluir predio con indicador de proceso
        if (!proCobro) {
            strProCobro = " AND NVL(U.PROCESO_DE_COBRO,0) IN (0) ";
        }
        sbTipoInforme.append(idioma.getString("TB_TB413") + nombrePredios);
        parametros.put("PR_TIPOINFORME", sbTipoInforme.toString());
        parametros.put("PR_TIPOGENERACION", sbTipoGeneracion.toString());
        reemplazar.put("deudaMenor", deudaMenor);
        reemplazar.put("deudaMayor", deudaMayor);
        reemplazar.put("numeroOrden",
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        reemplazar.put("strincluye", getCondicionIncluye());
        reemplazar.put("strtipopred",
                        getCondicionPredios(condicionTipoPredios));
        reemplazar.put("strcodigos", sbCodigos.toString());
        reemplazar.put("stranos", strAnos);
        reemplazar.put("stranoPago", strAnoPago);
        reemplazar.put("strcedulas", sbCedulas.toString());
        reemplazar.put("strnombres", sbNombres.toString());
        reemplazar.put("strvalores", sbValores.toString());
        reemplazar.put("strlotes", strLotes);
        reemplazar.put("strprocesocobro", strProCobro);

        String reporte;
        if (resumen) {
            reporte = "000859PREDIALPorCobrarResRpt";
        }
        else {
            reemplazar.put("ordenarDesAsc", sbOrdenarDesAsc.toString());
            reporte = getParametro(
                            "FORMATO DEUDORES DE PREDIAL", "");
        }

        if ("".equals(reporte)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB397"));
            return null;
        }

        return reporte;
    }

    /**
     * Para cada tipo de opcion de consulta de controlan los criterios
     * según el tipo. Para el informe sin indicador de resumen.
     */
    private void procesarTipo(StringBuilder sbTipoGeneracion,
        StringBuilder sbCodigos, StringBuilder sbCedulas,
        StringBuilder sbNombres, StringBuilder sbValores) {
        if ("1".equals(tipo)) {
            // 'Condiciones de codigos
            if (todosCodigos) {
                sbTipoGeneracion.append(idioma.getString("TB_TB389"));
            }
            else {
                sbCodigos.append(" AND U.CODIGO BETWEEN '" + codigoInicial
                    + "' AND '" + codigoFinal + "' ");
                sbTipoGeneracion.append(" DESDE EL CODIGO " + codigoInicial
                    + TEXTO_HASTA + codigoFinal + ". ");
            }
        }
        else if ("2".equals(tipo)) {
            // Condiciones de cedulas
            if (todasCedulas) {
                sbCedulas.append("");
                sbTipoGeneracion.append(idioma.getString("TB_TB390"));
            }
            else {
                sbTipoGeneracion.append(" DESDE LA CEDULA: " + cedulaInicial
                    + TEXTO_HASTA + cedulaFinal + ". ");
            }
        }
        else if ("3".equals(tipo)) {
            // Condiciones de nombres
            if (todosNombres) {
                sbTipoGeneracion.append(idioma.getString("TB_TB392")
                    + sbTipoGeneracion);
            }
            else {
                sbNombres.append(" AND U.NOMBRE BETWEEN '" + nombreInicial
                    + "' AND '" + nombreFinal + "' ");
                sbTipoGeneracion.append(" DESDE: " + nombreInicial
                    + TEXTO_HASTA + nombreFinal + " ");
            }
        }
        else if ("4".equals(tipo)) {
            DecimalFormat decimal = new DecimalFormat("0");
            decimal.setMaximumFractionDigits(340);
            // Condiciones de valores
            if (todosValores) {
                sbTipoGeneracion.append(idioma.getString("TB_TB395"));
            }
            else {
                sbValores.append(" AND D.TOTALDEUDA BETWEEN " + deudaMenor
                    + " AND  "
                    + deudaMayor + " ");
                sbTipoGeneracion.append(" DEUDAS DESDE: " + deudaMenor
                    + TEXTO_HASTA + decimal.format(deudaMayor) + " ");
            }
        }
    }

    /**
     * Se realiza la validacion de predios para el informe sin
     * indicador de resumen.
     *
     * @return condicion cuando el indicador<b> Incluye Vigencias en
     * acuerdo?</b> esta activado.
     */
    private String getCondicionIncluye() {
        String condicionIncluye = "";
        if ("1".equals(incluye)) {
            condicionIncluye = " AND NVL(U.INDBORRADO,0) IN(0) AND NVL(U.CODIGO_NO_ACTIVO,0) IN(0) ";
        }
        else if ("2".equals(incluye)) {
            condicionIncluye = " AND NVL(U.BLOQUEADO,0) NOT IN(0) ";
        }
        else if ("3".equals(incluye)) {
            condicionIncluye = " AND NVL(U.INDBORRADO,0) IN(0) AND NVL(U.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(U.BLOQUEADO,0) IN(0)";
        }
        else if ("4".equals(incluye)) {
            condicionIncluye = " AND (NVL(U.INDBORRADO,0) NOT IN(0) OR NVL(U.CODIGO_NO_ACTIVO,0) NOT IN(0)) ";
        }
        return condicionIncluye;
    }

    /**
     * Captura de parametros de reporte segun el tipo de orden.
     *
     * @param parametros
     * @param sbOrdenarDesAsc
     * @param sbTipoInforme
     */
    private void procesarPorOrdenar(Map<String, Object> parametros,
        StringBuilder sbOrdenarDesAsc, StringBuilder sbTipoInforme) {
        String prOrdenGrupo0 = "0";
        String prOrdenGrupo1 = "1";
        String prOrdenGrupo2 = "2";
        String prOrdenGrupo3 = "3";
        String prOrdenGrupo4 = "4";
        boolean prVisibleGrupo0 = false;
        boolean prVisibleGrupo1 = false;
        boolean prVisibleGrupo2 = false;
        boolean prVisibleGrupo3 = false;
        boolean prVisibleGrupo4 = false;

        if ("1".equals(ordenar)) {
            // Agrupar por Codigo
            prVisibleGrupo0 = true;
            sbTipoInforme.append(idioma.getString("TB_TB402"));
            sbOrdenarDesAsc.append(" CODIGO ");
        }
        else if ("2".equals(ordenar)) {
            // Agrupar por cedulas
            prOrdenGrupo0 = "1";
            prOrdenGrupo1 = "2";
            prOrdenGrupo2 = "0";
            prVisibleGrupo2 = true;
            sbTipoInforme.append(idioma.getString("TB_TB406"));
            sbOrdenarDesAsc.append(" NIT ");
        }
        else if ("3".equals(ordenar)) {
            // AGRUPAR Y ORDENAR POR NOMBRE
            prOrdenGrupo0 = "1";
            prOrdenGrupo1 = "2";
            prOrdenGrupo2 = "0";
            prVisibleGrupo1 = true;
            sbTipoInforme.append(idioma.getString("TB_TB407"));
            sbOrdenarDesAsc.append(" NOMBRE ");
        }
        else if ("4".equals(ordenar)) {
            // ORDENAR POR VALOR TOTAL
            prOrdenGrupo0 = "1";
            prOrdenGrupo1 = "2";
            prOrdenGrupo2 = "3";
            prOrdenGrupo3 = "4";
            prOrdenGrupo4 = "0";
            prVisibleGrupo4 = true;
            sbTipoInforme.append(idioma.getString("TB_TB409"));
            sbOrdenarDesAsc.append(" TOTAL ");
        }
        else if ("5".equals(ordenar)) {
            // Agrupar por ano
            prOrdenGrupo0 = "1";
            prOrdenGrupo1 = "2";
            prOrdenGrupo2 = "3";
            prOrdenGrupo3 = "0";
            prVisibleGrupo3 = true;
            sbTipoInforme.append(idioma.getString("TB_TB411"));
            sbOrdenarDesAsc.append(" PREANO ");
        }

        parametros.put("PR_ORDENGRUPO0", prOrdenGrupo0);
        parametros.put("PR_ORDENGRUPO1", prOrdenGrupo1);
        parametros.put("PR_ORDENGRUPO2", prOrdenGrupo2);
        parametros.put("PR_ORDENGRUPO3", prOrdenGrupo3);
        parametros.put("PR_ORDENGRUPO4", prOrdenGrupo4);
        parametros.put("PR_VISIBLEGRUPO0", prVisibleGrupo0);
        parametros.put("PR_VISIBLEGRUPO1", prVisibleGrupo1);
        parametros.put("PR_VISIBLEGRUPO2", prVisibleGrupo2);
        parametros.put("PR_VISIBLEGRUPO3", prVisibleGrupo3);
        parametros.put("PR_VISIBLEGRUPO4", prVisibleGrupo4);
    }

    private String getCondicionPredios(String condicionTipoPredios) {
        String strTipoPredios = "";
        if ("1".equals(porPredios)) {
            strTipoPredios = " AND SUBSTR(U.CODIGO,1,2) IN("
                + condicionTipoPredios
                + ") ";
        }
        else if ("2".equals(porPredios)) {
            strTipoPredios = " AND SUBSTR(U.CODIGO,1,2) NOT IN("
                + condicionTipoPredios + ")";
        }
        return strTipoPredios;
    }

    /**
     * Validacion de campos obligatorios según el tipo.
     *
     * @return Verdadero si hay camos nulos o vacios.
     */
    private boolean faltanCamposObligatorios() {
        if (faltanCamposCodigo()) {
            return true;
        }
        if (faltanCamposCedula()) {
            return true;
        }
        if (faltanCamposNombre()) {
            return true;
        }
        if (validarCamposDeuda()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB388"));
            return true;
        }
        return false;
    }

    private boolean validarCamposDeuda() {
        return "4".equals(tipo) && (deudaMenor > deudaMayor);
    }

    private boolean faltanCamposNombre() {
        return "3".equals(tipo) && (estaVacio(nombreInicial, "TB_TB385")
            || estaVacio(nombreFinal, "TB_TB386"));
    }

    private boolean faltanCamposCedula() {
        return "2".equals(tipo) && (estaVacio(cedulaInicial, "TB_TB382")
            || estaVacio(cedulaFinal, "TB_TB384"));
    }

    private boolean faltanCamposCodigo() {
        return "1".equals(tipo) && (estaVacio(codigoInicial, "TB_TB379")
            || estaVacio("codigoFinal", "TB_TB381"));
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
            parametro = ejbSysmanUtilRemote.consultarParametro(compania,
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
    public void cambiarOptTipo() {
        // <CODIGO_DESARROLLADO>
        codigoVisible = false;
        valoresVisible = false;
        cedulaVisible = false;
        nombreVisible = false;
        if ("1".equals(tipo)) {
            codigoVisible = true;
            ordenar = "1";
        }
        else if ("2".equals(tipo)) {
            cedulaVisible = true;
            ordenar = "2";
        }
        else if ("3".equals(tipo)) {
            nombreVisible = true;
            ordenar = "3";
        }
        else if ("4".equals(tipo)) {
            valoresVisible = true;
            ordenar = "4";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiaranioini() {
        cargarListaaniofin();
    }

    public void cambiarOptAnos() {
        // <CODIGO_DESARROLLADO>
        switch (anios) {
        case "1":
            anioPagoBloqueado = false;
            anioIniBloqueado = anioFinBloqueado = true;
            anioInicial = anioFinal = "";
            break;
        case "2":
            anioPagoBloqueado = true;
            anioPago = "";
            anioIniBloqueado = anioFinBloqueado = false;
            break;
        case "3":
            anioPagoBloqueado = anioIniBloqueado = anioFinBloqueado = true;
            anioPago = "";
            anioInicial = "1970";
            anioFinal = String.valueOf(SysmanFunciones.ano(new Date()));
            break;
        default:
            break;

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarchkTodosNombres() {
        // <CODIGO_DESARROLLADO>
        if (todosNombres) {
            nombreInicial = nombreFinal = TODOS_NOMBRES;
        }
        else {
            nombreInicial = nombreFinal = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarchkTodosCodigos() {
        // <CODIGO_DESARROLLADO>
        if (todosCodigos) {
            codigoInicial = codigoFinal = cedulaInicialM = cedulaFinalM = TODOS_NOMBRES;
        }
        else {
            codigoInicial = codigoFinal = cedulaInicialM = cedulaFinalM = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarchkTodosValores() {
        // <CODIGO_DESARROLLADO>
        if (todosValores) {
            deudaMenor = 0;
            deudaMayor = 99999999999.99;
        }
        else {
            deudaMenor = 0;
            deudaMayor = 0;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiargenerarPorPred() {
        // <CODIGO_DESARROLLADO>
        nombrePredios = service.buscarEnLista(porPredios, "NUMERO", NOMBRE,
                        listagenerarPorPred);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDeudaMenor() {
        // <CODIGO_DESARROLLADO>
        BigDecimal deuda = BigDecimal.valueOf(deudaMenor);
        if (deuda.compareTo(BigDecimal.ZERO) != 0) {
            todosValores = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDeudaMayor() {
        // <CODIGO_DESARROLLADO>
        BigDecimal deuda = BigDecimal.valueOf(deudaMayor);
        BigDecimal valor = BigDecimal.valueOf(99999999999.99);
        if (deuda.compareTo(valor) != 0) {
            todosValores = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarchkTodasCedulas() {
        // <CODIGO_DESARROLLADO>
        if (todasCedulas) {
            cedulaInicial = cedulaFinal = codigoFinalM = codigoInicialM = TODOS_NOMBRES;
        }
        else {
            cedulaInicial = cedulaFinal = codigoFinalM = codigoInicialM = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbCedulaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedulaInicial = registroAux.getCampos().get("NIT").toString();
        cedulaAux = registroAux.getCampos().get("CODIGO").toString();
        codigoInicialM = registroAux.getCampos().get(NOMBRE).toString();
        cargarListaCmbCedulaFinal();
        cedulaFinal = codigoFinalM = null;
        todasCedulas = false;

    }

    public void seleccionarFilaCmbCedulaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedulaFinal = registroAux.getCampos().get("NIT").toString();
        codigoFinalM = registroAux.getCampos().get(NOMBRE).toString();
        todasCedulas = false;
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(CODIGO).toString();
        cedulaInicialM = registroAux.getCampos().get("NIT").toString();
        cargarListaCodigoFinal();
        codigoFinal = cedulaFinalM = null;
        todosCodigos = false;
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(CODIGO).toString();
        cedulaFinalM = registroAux.getCampos().get("NIT").toString();
        todosCodigos = false;
    }

    public void seleccionarFilaCmbNombreInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreInicial = registroAux.getCampos().get(NOMBRE).toString();
        cargarListaCmbNombreFinal();
        nombreFinal = null;
        todosNombres = false;
    }

    public void seleccionarFilaCmbNombreFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreFinal = registroAux.getCampos().get(NOMBRE).toString();
        todosNombres = false;
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getOrdenar() {
        return ordenar;
    }

    public void setOrdenar(String ordenar) {
        this.ordenar = ordenar;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAnios() {
        return anios;
    }

    public void setAnios(String anios) {
        this.anios = anios;
    }

    public boolean getOrden() {
        return orden;
    }

    public void setOrden(boolean orden) {
        this.orden = orden;
    }

    public boolean getResumen() {
        return resumen;
    }

    public void setResumen(boolean resumen) {
        this.resumen = resumen;
    }

    public boolean getSinLotes() {
        return sinLotes;
    }

    public void setSinLotes(boolean sinLotes) {
        this.sinLotes = sinLotes;
    }

    public boolean getProCobro() {
        return proCobro;
    }

    public void setProCobro(boolean proCobro) {
        this.proCobro = proCobro;
    }

    public boolean getTodosNombres() {
        return todosNombres;
    }

    public void setTodosNombres(boolean todosNombres) {
        this.todosNombres = todosNombres;
    }

    public boolean getTodasCedulas() {
        return todasCedulas;
    }

    public void setTodasCedulas(boolean todasCedulas) {
        this.todasCedulas = todasCedulas;
    }

    public boolean getTodosCodigos() {
        return todosCodigos;
    }

    public void setTodosCodigos(boolean todosCodigos) {
        this.todosCodigos = todosCodigos;
    }

    public boolean getTodosValores() {
        return todosValores;
    }

    public void setTodosValores(boolean todosValores) {
        this.todosValores = todosValores;
    }

    public String getIncluye() {
        return incluye;
    }

    public void setIncluye(String incluye) {
        this.incluye = incluye;
    }

    public String getPorPredios() {
        return porPredios;
    }

    public void setPorPredios(String porPredios) {
        this.porPredios = porPredios;
    }

    public String getAnioInicial() {
        return anioInicial;
    }

    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    public String getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
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

    public String getCedulaInicial() {
        return cedulaInicial;
    }

    public void setCedulaInicial(String cedulaInicial) {
        this.cedulaInicial = cedulaInicial;
    }

    public String getCedulaFinal() {
        return cedulaFinal;
    }

    public void setCedulaFinal(String cedulaFinal) {
        this.cedulaFinal = cedulaFinal;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getAnioPago() {
        return anioPago;
    }

    public void setAnioPago(String anioPago) {
        this.anioPago = anioPago;
    }

    public String getCodigoInicialM() {
        return codigoInicialM;
    }

    public void setCodigoInicialM(String codigoInicialM) {
        this.codigoInicialM = codigoInicialM;
    }

    public String getCodigoFinalM() {
        return codigoFinalM;
    }

    public void setCodigoFinalM(String codigoFinalM) {
        this.codigoFinalM = codigoFinalM;
    }

    public String getCedulaInicialM() {
        return cedulaInicialM;
    }

    public void setCedulaInicialM(String cedulaInicialM) {
        this.cedulaInicialM = cedulaInicialM;
    }

    public String getCedulaFinalM() {
        return cedulaFinalM;
    }

    public void setCedulaFinalM(String cedulaFinalM) {
        this.cedulaFinalM = cedulaFinalM;
    }

    public double getDeudaMenor() {
        return deudaMenor;
    }

    public void setDeudaMenor(double deudaMenor) {
        this.deudaMenor = deudaMenor;
    }

    public double getDeudaMayor() {
        return deudaMayor;
    }

    public void setDeudaMayor(double deudaMayor) {
        this.deudaMayor = deudaMayor;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isCodigoVisible() {
        return codigoVisible;
    }

    public void setCodigoVisible(boolean codigoVisible) {
        this.codigoVisible = codigoVisible;
    }

    public boolean isValoresVisible() {
        return valoresVisible;
    }

    public void setValoresVisible(boolean valoresVisible) {
        this.valoresVisible = valoresVisible;
    }

    public boolean isCedulaVisible() {
        return cedulaVisible;
    }

    public void setCedulaVisible(boolean cedulaVisible) {
        this.cedulaVisible = cedulaVisible;
    }

    public boolean isNombreVisible() {
        return nombreVisible;
    }

    public void setNombreVisible(boolean nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isAniosVisible() {
        return aniosVisible;
    }

    public void setAniosVisible(boolean aniosVisible) {
        this.aniosVisible = aniosVisible;
    }

    public boolean isAnioFinBloqueado() {
        return anioFinBloqueado;
    }

    public void setAnioFinBloqueado(boolean anioFinBloqueado) {
        this.anioFinBloqueado = anioFinBloqueado;
    }

    public boolean isAnioPagoBloqueado() {
        return anioPagoBloqueado;
    }

    public void setAnioPagoBloqueado(boolean anioPagoBloqueado) {
        this.anioPagoBloqueado = anioPagoBloqueado;
    }

    public boolean isAnioIniBloqueado() {
        return anioIniBloqueado;
    }

    public void setAnioIniBloqueado(boolean anioIniBloqueado) {
        this.anioIniBloqueado = anioIniBloqueado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaanioini() {
        return listaanioini;
    }

    public void setListaanioini(List<Registro> listaanioini) {
        this.listaanioini = listaanioini;
    }

    public List<Registro> getListaaniofin() {
        return listaaniofin;
    }

    public void setListaaniofin(List<Registro> listaaniofin) {
        this.listaaniofin = listaaniofin;
    }

    public List<Registro> getListaAnoPago() {
        return listaAnoPago;
    }

    public void setListaAnoPago(List<Registro> listaAnoPago) {
        this.listaAnoPago = listaAnoPago;
    }

    public List<Registro> getListagenerarPorPred() {
        return listagenerarPorPred;
    }

    public void setListagenerarPorPred(List<Registro> listagenerarPorPred) {
        this.listagenerarPorPred = listagenerarPorPred;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCmbCedulaInicial() {
        return listaCmbCedulaInicial;
    }

    public void setListaCmbCedulaInicial(
        RegistroDataModelImpl listaCmbCedulaInicial) {
        this.listaCmbCedulaInicial = listaCmbCedulaInicial;
    }

    public RegistroDataModelImpl getListaCmbCedulaFinal() {
        return listaCmbCedulaFinal;
    }

    public void setListaCmbCedulaFinal(
        RegistroDataModelImpl listaCmbCedulaFinal) {
        this.listaCmbCedulaFinal = listaCmbCedulaFinal;
    }

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

    public RegistroDataModelImpl getListaCmbNombreInicial() {
        return listaCmbNombreInicial;
    }

    public void setListaCmbNombreInicial(
        RegistroDataModelImpl listaCmbNombreInicial) {
        this.listaCmbNombreInicial = listaCmbNombreInicial;
    }

    public RegistroDataModelImpl getListaCmbNombreFinal() {
        return listaCmbNombreFinal;
    }

    public void setListaCmbNombreFinal(
        RegistroDataModelImpl listaCmbNombreFinal) {
        this.listaCmbNombreFinal = listaCmbNombreFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}