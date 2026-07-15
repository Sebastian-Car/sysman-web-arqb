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
import com.sysman.presupuesto.enums.EstComparativoEjecPptalControladorEnum;
import com.sysman.presupuesto.enums.EstComparativoEjecPptalControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 05/07/2016
 * 
 * @author eamaya
 * @version 2, 18/04/2016, Proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class EstComparativoEjecPptalControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String periodo;
    private String anio;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private String nmes1;
    private String nmes2;
    private String nivel;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listames;
    private List<Registro> listames1;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private boolean mesLocked;
    private boolean mes1Visible;

    /**
     * Creates a new instance of EstComparativoEjecPptalControlador
     */
    public EstComparativoEjecPptalControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.EST_COMPARATIVO_EJEC_PPTAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(EstComparativoEjecPptalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListames();
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        periodo = "T1";
        nivel = "6";
        cambiarPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR972-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListames() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstComparativoEjecPptalControladorUrlEnum.URL3962
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListames1() {
        listames1 = listames;

    }

    public void cargarListaAno() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstComparativoEjecPptalControladorUrlEnum.URL4744
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
                                        EstComparativoEjecPptalControladorUrlEnum.URL5083
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstComparativoEjecPptalControladorUrlEnum.URL6083
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(EstComparativoEjecPptalControladorEnum.PARAM3.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
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
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generaReporte(FORMATOS formato) {

        try {
            if (!validaVacios()) {
                return;
            }
            if (anio.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB674"));
                return;
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000969EstadoComparativoEjecPPtalDU";
            mesFinal = "M".equals(periodo) ? mesInicial : mesFinal;
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("anio", anio);
            reemplazar.put("nivel", nivel);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_NMES1", nmes1.toUpperCase());
            parametros.put("PR_NMES2", nmes2.toUpperCase());
            parametros.put("PR_ANO", anio);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validaVacios() {
        periodo = SysmanFunciones.nvlStr(periodo, "");
        cuentaInicial = SysmanFunciones.nvlStr(cuentaInicial, "");
        cuentaFinal = SysmanFunciones.nvlStr(cuentaFinal, "");
        mesInicial = SysmanFunciones.nvlStr(mesInicial, "");
        anio = SysmanFunciones.nvlStr(anio, "");
        if (periodo.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB670"));
            return false;
        }
        if (cuentaInicial.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB671"));
            return false;
        }
        if (cuentaFinal.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB672"));
            return false;
        }
        if (mesInicial.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB673"));
            return false;
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarmes() {
        // <CODIGO_DESARROLLADO>
        if ("M".equals(periodo)) {
            nmes1 = nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mesInicial)];
        }
        else {
            nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mesInicial)];
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmes1() {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesFinal)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>
        mesLocked = true;
        mes1Visible = true;
        switch (periodo) {
        case "M":
            mesFinal = mesInicial;
            mesLocked = false;
            mes1Visible = false;
            break;
        case "T1":
            mesInicial = "1";
            mesFinal = "3";
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
            mesInicial = mesFinal = "1";
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
            break;
        }
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesFinal)];
        cargarListames1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mesInicial = mesFinal = nmes1 = nmes2 = cuentaInicial = cuentaFinal = nombreCuentaIni = nombreCuentaFin = periodo = null;
        cargarListames();
        cargarListames1();
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), " ").toString();
        nombreCuentaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
        cargarListaCuentaFinal();
        cuentaFinal = nombreCuentaFin = null;

    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), " ").toString();
        nombreCuentaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isMesLocked() {
        return mesLocked;
    }

    public void setMesLocked(boolean mesLocked) {
        this.mesLocked = mesLocked;
    }

    public boolean isMes1Visible() {
        return mes1Visible;
    }

    public void setMes1Visible(boolean mes1Visible) {
        this.mes1Visible = mes1Visible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListames() {
        return listames;
    }

    public void setListames(List<Registro> listames) {
        this.listames = listames;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
