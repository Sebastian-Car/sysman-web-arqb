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
import com.sysman.presupuesto.enums.FcregistrocuentasxpagarControladorEnum;
import com.sysman.presupuesto.enums.FcregistrocuentasxpagarControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author acaceres
 * @version 1, 24/06/2016
 * @modified jsforero
 * @version 2. 18/04/2017 Se realizo el refactory.
 *
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo de EJBs
 */
@ManagedBean
@ViewScoped
public class FcregistrocuentasxpagarControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String cuentaFinalCons;
    private final String codigoCons;
    private final String nombreCons;

    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private String cuentaInicial;
    private String cuentaFinal;
    private int mesInicial;
    private int mesFinal;
    private String centroCostoInicial;
    private String centroCostoFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private int ano;
    private String conSeccion;
    private String conUnidad;
    private String conRegional;
    private String parConSeccion;

    private String nombreNivel1I;
    private String nombreNivel1F;
    private String nombreNivel2I;
    private String nombreNivel2F;
    private String nombreNivel3I;
    private String nombreNivel3F;
    private String nombreNivel4I;
    private String nombreNivel4F;
    private String nombreNivel5I;
    private String nombreNivel5F;
    private String nombreNivel6I;
    private String nombreNivel6F;
    private String condicionCentroCosto;
    private String condicionFuenteRec;
    private String mesIni;
    private String mesFin;

    private String nivel;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private String nombreCenCosIni;
    private String nombreCenCosFin;
    private String nombreFuenteIni;
    private String nombreFuenteFin;
    private StreamedContent archivoDescarga;

    private String lblSeccion = "";
    private String lblUnidad = "";
    private String lblRegional = "";

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMes;
    private List<Registro> listames1;
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

    /**
     * Creates a new instance of FcregistrocuentasxpagarControlador
     */
    public FcregistrocuentasxpagarControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cuentaFinalCons = "9999999999999999";
        codigoCons = "CODIGO";
        nombreCons = "NOMBRE";
        try {
            numFormulario = GeneralCodigoFormaEnum.FCREGISTROCUENTASXPAGAR_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FcregistrocuentasxpagarControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() { 
        cargarListaMes();
        cargarListames1();
        cargarListaAno();
        traerParametros();
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteFinal();  
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones
                        .ano(new Date());
        mesInicial = 1;
        mesFinal = 1;
        nivel = "60";
        indicador = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlListA = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL5994
                                        .getValue());
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListA.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FcregistrocuentasxpagarControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        UrlBean urlListMeI = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL6470
                                        .getValue());
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListMeI.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FcregistrocuentasxpagarControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListames1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        UrlBean urlListMeF = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL6992
                                        .getValue());
        try {
            listames1 = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListMeF.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FcregistrocuentasxpagarControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL7524
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL8534
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(FcregistrocuentasxpagarControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListacentrocostoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL9701
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListacentrocostoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL10414
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(GeneralParameterEnum.CENTRO_COSTO.name(), centroCostoInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaFuenteInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL11246
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistrocuentasxpagarControladorUrlEnum.URL11981
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(FcregistrocuentasxpagarControladorEnum.FUENTEINICIAL
                        .getValue(), fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    private boolean nombreLabes() {
        boolean validaciones;

        if (SysmanFunciones.validarVariableVacio(conSeccion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB41"));
            validaciones = false;
            return validaciones;
        }
        else {
            lblSeccion = idioma.getString("TB_TB45");
        }

        if (SysmanFunciones.validarVariableVacio(conUnidad)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB42"));
            validaciones = false;
            return validaciones;
        }
        else {
            lblUnidad = idioma.getString("TG_UNIDAD_EJECUTORA");
        }

        if (SysmanFunciones.validarVariableVacio(conRegional)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB43"));
            validaciones = false;
            return validaciones;
        }
        else {
            lblRegional = idioma.getString("TG_REGIONAL");
        }
        validaciones = true;
        return validaciones;

    }

    private boolean validarParametros() {

        if (parConSeccion == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB39"));

            return false;
        }
        if (mesInicial > mesFinal) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB40"));

            return false;
        }

        if ("NO".equals(parConSeccion)) {
            parConSeccion = "SI";
            if (!nombreLabes()) {
                return false;
            }

        }
        else {
            lblSeccion = "";
            lblUnidad = "";
            lblRegional = "";
            conSeccion = "";
            conUnidad = "";
            conRegional = "";

        }

        return true;
    }

    private boolean validarNiveles() {
        if (!validarNivelesDelUnoAlTres()) {
            return false;
        }
        if (!validarNiveleDelTresACuatro()) {
            return false;
        }
        if (!validarNivelesDelCincoAlSeis()) {
            return false;
        }

        return true;
    }

    private boolean validarNivelesDelUnoAlTres() {
        if (nombreNivel1I == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1036"));
            return false;
        }

        if (nombreNivel1F == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1037"));
            return false;
        }

        if (nombreNivel2I == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1039"));
            return false;
        }

        if (nombreNivel2F == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1040"));
            return false;
        }

        return true;
    }

    private boolean validarNiveleDelTresACuatro() {
        if (nombreNivel3I == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1041"));
            return false;
        }

        if (nombreNivel3F == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1042"));
            return false;
        }
        if (nombreNivel4I == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1043"));
            return false;
        }
        if (nombreNivel4F == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1044"));
            return false;
        }

        return true;
    }

    private boolean validarNivelesDelCincoAlSeis() {

        if (nombreNivel5I == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1045"));
            return false;
        }

        if (nombreNivel5F == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1046"));
            return false;
        }

        if (nombreNivel6I == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1047"));
            return false;
        }

        if (nombreNivel6F == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1048"));
            return false;
        }
        return true;
    }

    private void mesInicialYMesFinalCondicionesReemplazos() {
        mesIni = String.valueOf(mesInicial);
        mesFin = String.valueOf(mesFinal);

        if (mesIni.length() < 2) {
            mesIni = "0" + mesIni + "";

        }
        if (mesFin.length() < 2) {
            mesFin = "0" + mesFin + "";
            mesFinal = Integer.valueOf(mesFin);
        }

        if (!"0".equals(centroCostoInicial)
                        || (!"99999999999999999999".equals(centroCostoFinal))) {
            condicionCentroCosto = "AND  V_PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
                            + centroCostoInicial + "' AND '" + centroCostoFinal
                            + "'";
        }
        else {
            condicionCentroCosto = "";
        }
        if (!"0".equals(fuenteInicial)
                        || !fuenteFinal.equals(cuentaFinalCons)) {
            condicionFuenteRec = "AND  V_PLAN_PRESUPUESTAL.FUENTE_RECURSO BETWEEN '"
                            + fuenteInicial + "' AND '" + fuenteFinal + "'";

        }
        else {
            condicionFuenteRec = "";
        }
    }

    public void obtenerReporte(FORMATOS formatos) {
        String nombreMes = "";

        String reporte="000947FCREGISTROCUENTASxPAGAR";
        try {
            if (!validarParametros()) {
                return;
            }

            if (!validarNiveles()) {
                return;
            }
            mesInicialYMesFinalCondicionesReemplazos();

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fuenteRecIni", fuenteInicial);
            reemplazar.put("fuenteRecFin", fuenteFinal);
            reemplazar.put("centroCosIni", centroCostoInicial);
            reemplazar.put("centroCosFin", centroCostoFinal);
            reemplazar.put("ano", ano);
            reemplazar.put("mesInicial", mesIni);
            reemplazar.put("mesFinal", mesFin);
            reemplazar.put("nivel", nivel);
            reemplazar.put("nombreNivel1I", nombreNivel1I);
            reemplazar.put("nombreNivel1F", nombreNivel1F);
            reemplazar.put("nombreNivel2I", nombreNivel2I);
            reemplazar.put("nombreNivel2F", nombreNivel2F);
            reemplazar.put("nombreNivel3I", nombreNivel3I);
            reemplazar.put("nombreNivel3F", nombreNivel3F);
            reemplazar.put("nombreNivel4I", nombreNivel4I);
            reemplazar.put("nombreNivel4F", nombreNivel4F);
            reemplazar.put("nombreNivel5I", nombreNivel5I);
            reemplazar.put("nombreNivel5F", nombreNivel5F);
            reemplazar.put("nombreNivel6I", nombreNivel6I);
            reemplazar.put("nombreNivel6F", nombreNivel6F);
            reemplazar.put("condicionCentroCosto", condicionCentroCosto);
            reemplazar.put("condicionFuenteRec", condicionFuenteRec);

            // MANEJO DE PARAMETROS DE REEMPLAZO

            Map<String, Object> parametros = new HashMap<>();
            traerParametros();
            nombreLabes();
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

            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_PERIODO", nombreMes);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CONSECCION", conSeccion);
            parametros.put("PR_CONUNIDAD", conUnidad);
            parametros.put("PR_LBLSECCION_CAPTION", lblSeccion);
            parametros.put("PR_LBLUNIDAD_CAPTION", lblUnidad);
            parametros.put("PR_CONREGIONAL", conRegional);
            parametros.put("PR_LBLREGIONAL_CAPTION", lblRegional);
            parametros.put("PR_MOSTRARID", indicador);
            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }       
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(FcregistrocuentasxpagarControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch (OutOfMemoryError | JRException | IOException  e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SysmanException e) {          
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCuentaIni = "";
        nombreCuentaFin = "";
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(codigoCons).toString();
        nombreCuentaIni = registroAux.getCampos().get(nombreCons).toString();
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(codigoCons).toString();
        nombreCuentaFin = registroAux.getCampos().get(nombreCons).toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = registroAux.getCampos().get(codigoCons).toString();
        nombreCenCosIni = registroAux.getCampos().get(nombreCons).toString();
        centroCostoFinal = "";
        nombreCenCosFin = "";
        cargarListacentrocostoFinal();
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = registroAux.getCampos().get(codigoCons).toString();
        nombreCenCosFin = registroAux.getCampos().get(nombreCons).toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos().get(codigoCons).toString();
        nombreFuenteIni = registroAux.getCampos().get(nombreCons).toString();
        fuenteFinal = "";
        nombreFuenteFin = "";
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos().get(codigoCons).toString();
        nombreFuenteFin = registroAux.getCampos().get(nombreCons).toString();
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

    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getConSeccion() {
        return conSeccion;
    }

    public void setConSeccion(String conSeccion) {
        this.conSeccion = conSeccion;
    }

    public String getConUnidad() {
        return conUnidad;
    }

    public void setConUnidad(String conUnidad) {
        this.conUnidad = conUnidad;
    }

    public String getConRegional() {
        return conRegional;
    }

    public void setConRegional(String conRegional) {
        this.conRegional = conRegional;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNombreCuentaIni() {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni) {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public String getNombreCuentaFin() {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin) {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    public String getNombreCenCosIni() {
        return nombreCenCosIni;
    }

    public void setNombreCenCosIni(String nombreCenCosIni) {
        this.nombreCenCosIni = nombreCenCosIni;
    }

    public String getNombreCenCosFin() {
        return nombreCenCosFin;
    }

    public void setNombreCenCosFin(String nombreCenCosFin) {
        this.nombreCenCosFin = nombreCenCosFin;
    }

    public String getNombreFuenteIni() {
        return nombreFuenteIni;
    }

    public void setNombreFuenteIni(String nombreFuenteIni) {
        this.nombreFuenteIni = nombreFuenteIni;
    }

    public String getNombreFuenteFin() {
        return nombreFuenteFin;
    }

    public void setNombreFuenteFin(String nombreFuenteFin) {
        this.nombreFuenteFin = nombreFuenteFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListames1() {
        return listames1;
    }

    public void setListames1(List<Registro> listames1) {
        this.listames1 = listames1;
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

    private void traerParametros() {
        try {
            conUnidad = sysmanUtil.consultarParametro(
                            compania, "UNIDAD EJECUTORA 036", modulo,
                            new Date(), true);

            conSeccion = sysmanUtil.consultarParametro(
                            compania, "SECCION 036", modulo, new Date(), true);
            conRegional = sysmanUtil.consultarParametro(
                            compania, "REGIONAL 036", modulo, new Date(), true);

            parConSeccion = sysmanUtil.consultarParametro(
                            compania, "SECCION EN INFORMES RESOLUCION 036",
                            modulo,
                            new Date(), true);

            nombreNivel1I = sysmanUtil.consultarParametro(compania, "NIVEL 1I",
                            modulo, new Date(), true);

            nombreNivel1F = sysmanUtil.consultarParametro(compania, "NIVEL 1F",
                            modulo, new Date(), true);
            nombreNivel2I = sysmanUtil.consultarParametro(compania, "NIVEL 2I",
                            modulo, new Date(), true);
            nombreNivel2F = sysmanUtil.consultarParametro(compania, "NIVEL 2F",
                            modulo, new Date(), true);
            nombreNivel3I = sysmanUtil.consultarParametro(compania, "NIVEL 3I",
                            modulo, new Date(), true);
            nombreNivel3F = sysmanUtil.consultarParametro(compania, "NIVEL 3F",
                            modulo, new Date(), true);
            nombreNivel4I = sysmanUtil.consultarParametro(compania, "NIVEL 4I",
                            modulo, new Date(), true);
            nombreNivel4F = sysmanUtil.consultarParametro(compania, "NIVEL 4F",
                            modulo, new Date(), true);
            nombreNivel5I = sysmanUtil.consultarParametro(compania, "NIVEL 5I",
                            modulo, new Date(), true);
            nombreNivel5F = sysmanUtil.consultarParametro(compania, "NIVEL 5F",
                            modulo, new Date(), true);
            nombreNivel6I = sysmanUtil.consultarParametro(compania, "NIVEL 6I",
                            modulo, new Date(), true);
            nombreNivel6F = sysmanUtil.consultarParametro(compania, "NIVEL 6F",
                            modulo, new Date(), true);
        }
        catch (SystemException e) {
            Logger.getLogger(FcregistrocuentasxpagarControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
