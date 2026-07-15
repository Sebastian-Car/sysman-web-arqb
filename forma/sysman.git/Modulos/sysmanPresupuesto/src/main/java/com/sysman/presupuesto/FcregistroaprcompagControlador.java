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
import com.sysman.presupuesto.enums.FcregistroaprcompagControladorEnum;
import com.sysman.presupuesto.enums.FcregistroaprcompagControladorUrlEnum;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import java.sql.SQLException;

import net.sf.dynamicreports.report.exception.DRException;

/**
 *
 * @author acaceres
 * @version 1, 23/06/2016
 * @version 2, 18/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 *
 * @author asana
 * @version 4, 13/06/2017, se modifica enum en formulario y se
 * modifica conexion a BD.
 */
@ManagedBean
@ViewScoped
public class FcregistroaprcompagControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String cNombre;
    private final String cNueves;
    private final String cCodigo;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private String cuentaInicial;
    private String cuentaFinal;
    private int mesInicial;
    private int mesFinal;
    private String centroCostoIni;
    private String centroCostoFin;
    private String fuenteRecInicial;
    private String fuenteRecFinal;
    private int ano;
    private String nivel;
    private String nombreCuentaInicial;
    private String nombreCuentaFinal;
    private String nombreCentroCostoIni;
    private String nombreCentroCostoFin;
    private String nombreFuenteRecIni;
    private String nombreFuenteRecFin;
    private StreamedContent archivoDescarga;
    private boolean formatoEspecialExcel = false;
    private String textoFinalRegistros = "";
    
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
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
     * Creates a new instance of FcregistroaprcompagControlador
     */
    public FcregistroaprcompagControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cNueves = "9999999999999999";
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.FCREGISTROAPRCOMPAG_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FcregistroaprcompagControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        ano = SysmanFunciones
                        .ano(new Date());
        cargarListaMesInicial();
        mesInicial = 1;
        cargarListaMesFinal();
        mesFinal = 1;
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cuentaInicial = "2";
        cargarListacentrocostoInicial();
        centroCostoIni = "0";
        cargarListaFuenteInicial();
        centroCostoFin = "99999999999999999999";
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cuentaFinal = cNueves;
        fuenteRecInicial = "0";
        fuenteRecFinal = cNueves;
        nivel = "61";
        nombreCuentaInicial = "GASTOS";
        nombreCuentaFinal = "NINGUNO";
        nombreCentroCostoFin = "NINGUNO";
        nombreFuenteRecFin = "VARIOS";
        // </CODIGO_DESARROLLADO>
        try {
        textoFinalRegistros = SysmanFunciones.nvlStr(
			    ejbSysmanUtil.consultarParametro(compania,
			        "TEXTO FINAL REGISTRO INGRESOS", 
			        SessionUtil.getModulo(),
			        new Date(),
			        false),
			    "NO");
		
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroaprcompagControladorUrlEnum.URL5147
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroaprcompagControladorUrlEnum.URL5648
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroaprcompagControladorUrlEnum.URL6247
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
                                        FcregistroaprcompagControladorUrlEnum.URL6852
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroaprcompagControladorUrlEnum.URL7857
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FcregistroaprcompagControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /** Carga la lista del combo asociado a listacentrocostoInicial */
    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroaprcompagControladorUrlEnum.URL9093
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /** Carga la lista del combo asociado a listacentrocostoFinal */
    public void cargarListacentrocostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroaprcompagControladorUrlEnum.URL9791
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCostoIni);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /** Carga la lista del combo asociado a listaFuenteInicial */
    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroaprcompagControladorUrlEnum.URL10563
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /** Carga la lista del combo asociado a listaFuenteFinal */
    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroaprcompagControladorUrlEnum.URL11226
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FcregistroaprcompagControladorEnum.FUENTEINICIAL.getValue(),
                        fuenteRecInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    private void obtenerConsultaReporte(FORMATOS formatos) {
        archivoDescarga = null;
        String mesIni = Integer.toString(mesInicial);
        String mesFin = Integer.toString(mesFinal);
        // MANEJO DE PARAMETROS DE REEMPLAZO
        mesIni = SysmanFunciones.strZero(mesIni, 2);
        mesFin = SysmanFunciones.strZero(mesFin, 2);

        try {
            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            if (!validaParametros(parametros) || !validaMeses()) {
                return;
            }

            if (!validarReemplazos(reemplazar)
                || !validarReemplazos2(reemplazar)
                || !validarReemplazos3(reemplazar)) {
                return;
            }
            agregaCondCtoCosto(reemplazar);
            agregaCondFteRec(reemplazar);

            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("ano", ano);
            reemplazar.put("mesInicial", mesIni);
            reemplazar.put("mesFinal", mesFin);
            reemplazar.put("nivel", nivel);
            Reporteador.resuelveConsulta("000942FCREGISTROAPRCOMPAG",
                            Integer.valueOf(modulo), reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CONID",
                            indicador);
            agregaParPeriodo(parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000942FCREGISTROAPRCOMPAG",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validaMeses() {
        if (mesInicial > mesFinal) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB244"));
            return false;
        }
        return true;
    }

    private boolean validaParametros(HashMap<String, Object> parametros) {
        String parConSeccion = null;
        String conSeccion = null;
        String conUnidad = null;
        String conRegional = null;
        try {
            conSeccion = ejbSysmanUtil.consultarParametro(
                            compania, "SECCION 036", modulo, new Date(), true);
            conUnidad = ejbSysmanUtil.consultarParametro(
                            compania, "UNIDAD EJECUTORA 036", modulo,
                            new Date(), true);
            conRegional = ejbSysmanUtil.consultarParametro(
                            compania, "REGIONAL 036", modulo, new Date(), true);
            parConSeccion = ejbSysmanUtil.consultarParametro(
                            compania, "SECCION EN INFORMES RESOLUCION 036",
                            modulo,
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (conSeccion == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB245"));
            return false;
        }

        if (conUnidad == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB246"));
            return false;
        }
        if (conRegional == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB247"));
            return false;
        }
        if (parConSeccion == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB243"));
            return false;
        }
        String lblSeccion;
        String lblUnidad;
        String lblRegional;
        if ("SI".equals(parConSeccion)) {
            lblSeccion = !SysmanFunciones.validarVariableVacio(conSeccion)
                ? "SECCION" : "";
            lblUnidad = !SysmanFunciones.validarVariableVacio(conUnidad)
                ? "UNIDAD EJECUTORA" : "";
            lblRegional = !SysmanFunciones.validarVariableVacio(conRegional)
                ? "REGIONAL" : "";
        }
        else {
            lblSeccion = "";
            lblUnidad = "";
            lblRegional = "";
            conSeccion = "";
            conUnidad = "";
            conRegional = "";
        }
        parametros.put("PR_CONSECCION", conSeccion);
        parametros.put("PR_CONUNIDAD", conUnidad);
        parametros.put("PR_LBLUNIDAD_CAPTION", lblUnidad);
        parametros.put("PR_CONREGIONAL", conRegional);
        parametros.put("PR_LBLREGIONAL_CAPTION", lblRegional);
        parametros.put("PR_LBLSECCION_CAPTION", lblSeccion);
        return true;
    }

    private void agregaParPeriodo(HashMap<String, Object> parametros) {
        String nombreMes;
        if (mesInicial == mesFinal) {
            nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                            .toUpperCase();
        }
        else {
            nombreMes = ""
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                                .toUpperCase()
                + " a  "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
                                .toUpperCase()
                + " ";
        }
        parametros.put("PR_PERIODO", nombreMes);
    }

    private void agregaCondCtoCosto(HashMap<String, Object> reemplazar) {
        String condicionCentroCos;
        if (!"0".equals(centroCostoIni)
            || !"99999999999999999999".equals(centroCostoFin)) {
            condicionCentroCos = "AND  PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
                + centroCostoIni + "' AND '" + centroCostoFin + "'";

        }
        else {
            condicionCentroCos = "";
        }
        reemplazar.put("condicionCentroCos", condicionCentroCos);

    }

    private void agregaCondFteRec(HashMap<String, Object> reemplazar) {
        String condicionFuenteRec;
        if (!"0".equals(fuenteRecInicial)
            || !fuenteRecFinal.equals(cNueves)) {
            condicionFuenteRec = "AND  PLAN_PRESUPUESTAL.FUENTE_RECURSO BETWEEN '"
                + fuenteRecInicial + "' AND '" + fuenteRecFinal
                + "'";

        }
        else {
            condicionFuenteRec = "";
        }
        reemplazar.put("condicionFuenteRec", condicionFuenteRec);
    }

    private boolean validarReemplazos(HashMap<String, Object> reemplazar) {
        String nombreNivel1I = null;
        String nombreNivel1F = null;
        String nombreNivel2I = null;
        String nombreNivel2F = null;
        try {
            nombreNivel1I = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 1I",
                            modulo, new Date(), true);
            nombreNivel1F = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 1F",
                            modulo, new Date(), true);
            nombreNivel2I = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 2I",
                            modulo, new Date(), true);
            nombreNivel2F = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 2F",
                            modulo, new Date(), true);
            if (nombreNivel1I == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1036"));
                return false;
            }
            else if (nombreNivel1F == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1037"));
                return false;
            }
            else if (nombreNivel2I == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1039"));
                return false;
            }
            else if (nombreNivel2F == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1040"));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        reemplazar.put("nombreNivel1I", nombreNivel1I);
        reemplazar.put("nombreNivel1F", nombreNivel1F);
        reemplazar.put("nombreNivel2I", nombreNivel2I);
        reemplazar.put("nombreNivel2F", nombreNivel2F);
        return true;
    }

    private boolean validarReemplazos2(HashMap<String, Object> reemplazar) {
        String nombreNivel3I = null;
        String nombreNivel3F = null;
        String nombreNivel4I = null;
        String nombreNivel4F = null;
        try {
            nombreNivel3I = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 3I",
                            modulo, new Date(), true);
            nombreNivel3F = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 3F",
                            modulo, new Date(), true);
            nombreNivel4I = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 4I",
                            modulo, new Date(), true);
            nombreNivel4F = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 4F",
                            modulo, new Date(), true);
            if (nombreNivel3I == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1041"));
                return false;
            }
            else if (nombreNivel3F == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1042"));
                return false;
            }
            else if (nombreNivel4I == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1043"));
                return false;
            }
            else if (nombreNivel4F == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1044"));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        reemplazar.put("nombreNivel3I", nombreNivel3I);
        reemplazar.put("nombreNivel3F", nombreNivel3F);
        reemplazar.put("nombreNivel4I", nombreNivel4I);
        reemplazar.put("nombreNivel4F", nombreNivel4F);
        return true;
    }

    private boolean validarReemplazos3(HashMap<String, Object> reemplazar) {
        String nombreNivel5I = null;
        String nombreNivel5F = null;
        String nombreNivel6I = null;
        String nombreNivel6F = null;
        try {
            nombreNivel5I = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 5I",
                            modulo, new Date(), true);
            nombreNivel5F = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 5F",
                            modulo, new Date(), true);
            nombreNivel6I = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 6I",
                            modulo, new Date(), true);
            nombreNivel6F = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 6F",
                            modulo, new Date(), true);
            if (nombreNivel5I == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1045"));
                return false;
            }
            else if (nombreNivel5F == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1046"));
                return false;
            }
            else if (nombreNivel6I == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1047"));
                return false;

            }
            else if (nombreNivel6F == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1048"));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        reemplazar.put("nombreNivel5I", nombreNivel5I);
        reemplazar.put("nombreNivel5F", nombreNivel5F);
        reemplazar.put("nombreNivel6I", nombreNivel6I);
        reemplazar.put("nombreNivel6F", nombreNivel6F);
        return true;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        obtenerConsultaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>       
        String mesIni = Integer.toString(mesInicial);
        String mesFin = Integer.toString(mesFinal);
        // MANEJO DE PARAMETROS DE REEMPLAZO
        mesIni = SysmanFunciones.strZero(mesIni, 2);
        mesFin = SysmanFunciones.strZero(mesFin, 2);
        try {           
            HashMap<String, Object> reemplazar = new HashMap<>();
            
                     
            if (formatoEspecialExcel) {
            
                reemplazar.clear();
                
                reemplazar.put("cuentaInicial", cuentaInicial);
                reemplazar.put("cuentaFinal", cuentaFinal);
                reemplazar.put("ano", ano);
                reemplazar.put("mesInicial", mesIni);  
                reemplazar.put("mesFinal", mesFin);   
                reemplazar.put("nivel", nivel);
                
                agregaCondCtoCosto(reemplazar);
                agregaCondFteRec(reemplazar);
                
                String periodoTexto = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial].toUpperCase()
                        + " a "
                        + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal].toUpperCase();

                HashMap<String, Object> parametros = new HashMap<>();
                parametros.put("PR_NOMBRECOMPANIA",
                		SessionUtil.getCompaniaIngreso().getNombre());
                parametros.put("PR_LEYENDA", textoFinalRegistros);
                parametros.put("PR_PERIODO", periodoTexto);
                
                Reporteador.resuelveConsulta("8000577FcRegistroAprCompag_Plano",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);
                
                archivoDescarga = JsfUtil.exportarStreamed("8000577FcRegistroAprCompag_Plano",
                        parametros,
                        ConectorPool.ESQUEMA_SYSMAN,
                        FORMATOS.EXCEL);
                
               
            } else {
                obtenerConsultaReporte(FORMATOS.EXCEL);
            }
            
        } catch (Exception ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        // </CODIGO_DESARROLLADO>
    }
    public void cambiarExcelPlanoCheck() {
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = nombreCuentaInicial = nombreCuentaFinal = "";
        cargarListaMesInicial();
        cargarListaMesFinal();
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nombreCuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        cuentaFinal = "";
        nombreCuentaFinal = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nombreCuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nombreCentroCostoIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        centroCostoFin = "";
        nombreCentroCostoFin = "";
        cargarListacentrocostoFinal();
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nombreCentroCostoFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteRecInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nombreFuenteRecIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        fuenteRecFinal = "";
        nombreFuenteRecFin = "";
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteRecFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        nombreFuenteRecFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
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

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getCentroCostoIni() {
        return centroCostoIni;
    }

    public void setCentroCostoIni(String centroCostoIni) {
        this.centroCostoIni = centroCostoIni;
    }

    public String getCentroCostoFin() {
        return centroCostoFin;
    }

    public void setCentroCostoFin(String centroCostoFin) {
        this.centroCostoFin = centroCostoFin;
    }

    public String getFuenteRecInicial() {
        return fuenteRecInicial;
    }

    public void setFuenteRecInicial(String fuenteRecInicial) {
        this.fuenteRecInicial = fuenteRecInicial;
    }

    public String getFuenteRecFinal() {
        return fuenteRecFinal;
    }

    public void setFuenteRecFinal(String fuenteRecFinal) {
        this.fuenteRecFinal = fuenteRecFinal;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNombreCuentaInicial() {
        return nombreCuentaInicial;
    }

    public void setNombreCuentaInicial(String nombreCuentaInicial) {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    public String getNombreCuentaFinal() {
        return nombreCuentaFinal;
    }

    public void setNombreCuentaFinal(String nombreCuentaFinal) {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }

    public String getNombreCentroCostoIni() {
        return nombreCentroCostoIni;
    }

    public void setNombreCentroCostoIni(String nombreCentroCostoIni) {
        this.nombreCentroCostoIni = nombreCentroCostoIni;
    }

    public String getNombreCentroCostoFin() {
        return nombreCentroCostoFin;
    }

    public void setNombreCentroCostoFin(String nombreCentroCostoFin) {
        this.nombreCentroCostoFin = nombreCentroCostoFin;
    }

    public String getNombreFuenteRecIni() {
        return nombreFuenteRecIni;
    }

    public void setNombreFuenteRecIni(String nombreFuenteRecIni) {
        this.nombreFuenteRecIni = nombreFuenteRecIni;
    }

    public String getNombreFuenteRecFin() {
        return nombreFuenteRecFin;
    }

    public void setNombreFuenteRecFin(String nombreFuenteRecFin) {
        this.nombreFuenteRecFin = nombreFuenteRecFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
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
