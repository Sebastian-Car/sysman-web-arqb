package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmvigenciapresupuestalgastosduControladorEnum;
import com.sysman.presupuesto.enums.FrmvigenciapresupuestalgastosduControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 01/07/2016
 * @modified jsforero
 * @version 2. 18/04/2017 Se realizo el refactory.
 */
@ManagedBean
@ViewScoped
public class FrmvigenciapresupuestalgastosduControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private final String consCodigo;
    // <DECLARAR_ATRIBUTOS>
    private int ano;
    private String mesInicial;
    private String mesFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private String nivel;
    private String periodo;
    private Boolean bloqueaMesIni;
    private Boolean bloqueaMesFin;
    private Boolean mesIniVisible;
    private Boolean mesFinVisible;
    private boolean etiqMesFinVisible;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listames1;
    private List<Registro> listaAno;
    private List<Registro> listames;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of
     * FrmvigenciapresupuestalgastosduControlador
     */
    public FrmvigenciapresupuestalgastosduControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMVIGENCIAPRESUPUESTALGASTOSDU_CONTROLADOR
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

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListames1();
        cargarListaAno();
        cargarListames();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones
                        .ano(new Date());
        periodo = "T1";
        mesInicial = "1";
        mesFinal = "3";
        cuentaInicial = "0";
        cuentaFinal = "9999999999999999";
        nivel = "6";
        mesIniVisible = true;
        bloqueaMesIni = true;
        mesFinVisible = true;
        bloqueaMesFin = true;
        etiqMesFinVisible = true;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListames1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        UrlBean urlListmes1 = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvigenciapresupuestalgastosduControladorUrlEnum.URL5445
                                                        .getValue());
        try {
            listames1 = RegistroConverter.toListRegistro(requestManager
                            .getList(urlListmes1.getUrl(), param));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlListAno = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvigenciapresupuestalgastosduControladorUrlEnum.URL4965
                                                        .getValue());
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListAno.getUrl(), param));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    public void cargarListames() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        UrlBean urlListMes = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvigenciapresupuestalgastosduControladorUrlEnum.URL5445
                                                        .getValue());
        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListMes.getUrl(), param));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvigenciapresupuestalgastosduControladorUrlEnum.URL5973
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);

    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvigenciapresupuestalgastosduControladorUrlEnum.URL6933
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        param.put(FrmvigenciapresupuestalgastosduControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);
        param.put(GeneralParameterEnum.ANO.name(), ano);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);

    }

    public void obtenerReporte(FORMATOS formatos) {
        try {
            if (("M").equals(periodo)) {
                mesFinal = mesInicial;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("nivel", nivel);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String entre = "ENTRE "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .valueOf(mesInicial)].toUpperCase()
                + " Y "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .valueOf(mesFinal)].toUpperCase()
                + "";
            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta("000964LisEjecPptalGastosDU",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000964LisEjecPptalGastosDU", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            " ", e.getMessage()));
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
        nombreCuentaIni = "";
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>
        mesIniVisible = true;
        mesFinVisible = true;
        bloqueaMesIni = true;
        bloqueaMesFin = true;
        etiqMesFinVisible = true;

        switch (periodo) {
        case "M":
            asignarBoolean();
            break;
        case "T2":
            mesInicial = "4";
            mesFinal = "6";
            break;
        case "T3":
            mesInicial = "7";
            mesFinal = "9";
            break;
        case "T4":
            mesInicial = "10";
            mesFinal = "12";
            break;
        case "S1":
            mesInicial = "1";
            mesFinal = "6";
            break;
        case "S2":
            mesInicial = "7";
            mesFinal = "12";
            break;
        case "A":
            mesInicial = "1";
            mesFinal = "12";
            break;

        default:
            mesInicial = "1";
            mesFinal = "3";
            break;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void asignarBoolean() {
        mesFinal = mesInicial;
        bloqueaMesIni = false;
        mesIniVisible = true;
        bloqueaMesFin = true;
        mesFinVisible = false;
        etiqMesFinVisible = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(consCodigo).toString();
        nombreCuentaIni = registroAux.getCampos().get("NOMBRE").toString();
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(consCodigo).toString();
        nombreCuentaFin = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public Boolean getEtiqMesFinVisible() {
        return etiqMesFinVisible;
    }

    public void setEtiqMesFinVisible(Boolean etiqMesFinVisible) {
        this.etiqMesFinVisible = etiqMesFinVisible;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
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

    public void setEtiqMesFinVisible(boolean etiqMesFinVisible) {
        this.etiqMesFinVisible = etiqMesFinVisible;
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

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Boolean getBloqueaMesIni() {
        return bloqueaMesIni;
    }

    public void setBloqueaMesIni(Boolean bloqueaMesIni) {
        this.bloqueaMesIni = bloqueaMesIni;
    }

    public Boolean getBloqueaMesFin() {
        return bloqueaMesFin;
    }

    public void setBloqueaMesFin(Boolean bloqueaMesFin) {
        this.bloqueaMesFin = bloqueaMesFin;
    }

    public Boolean getMesIniVisible() {
        return mesIniVisible;
    }

    public void setMesIniVisible(Boolean mesIniVisible) {
        this.mesIniVisible = mesIniVisible;
    }

    public Boolean getMesFinVisible() {
        return mesFinVisible;
    }

    public void setMesFinVisible(Boolean mesFinVisible) {
        this.mesFinVisible = mesFinVisible;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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

    public List<Registro> getListames() {
        return listames;
    }

    public void setListames(List<Registro> listames) {
        this.listames = listames;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
