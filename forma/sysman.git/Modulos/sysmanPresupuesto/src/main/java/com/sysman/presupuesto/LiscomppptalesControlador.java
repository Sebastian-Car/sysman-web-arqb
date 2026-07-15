package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LiscomppptalesControladorEnum;
import com.sysman.presupuesto.enums.LiscomppptalesControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
 * @author dsuesca
 * @version 1, 08/07/2016
 *
 * -- Modificado por lcortes 19/04/2017 14:58. Ajustes refactorizacion
 * de codigo.
 * 
 * @author asana,
 * @version 3, 13/06/2017 se modifica enum de formulario y se ajusta Conexi�n
 */
@ManagedBean
@ViewScoped
public class LiscomppptalesControlador extends BeanBaseModal {
    private final String compania;

    private final String strCodigo;
    private final String strNumero;

    // <DECLARAR_ATRIBUTOS>
    private String formato;
    private String tipoInicial;
    private String tipoFinal;
    private String numeroInicial;
    private String numeroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaNumeroInicial;
    private RegistroDataModelImpl listaNumeroFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LiscomppptalesControlador
     */
    public LiscomppptalesControlador() {
        super();
        compania = SessionUtil.getCompania();

        strCodigo = "CODIGO";
        strNumero = "NUMERO";

        try {
            numFormulario = GeneralCodigoFormaEnum.LISCOMPPPTALES_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            tipoFinal = "ZZZ";
            fechaInicial = fechaFinal = new Date();
            formato = "1";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LiscomppptalesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR985-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 3, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomppptalesControladorUrlEnum.URL3966
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaTipoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomppptalesControladorUrlEnum.URL4589
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LiscomppptalesControladorEnum.PARAM0.getValue(), tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaNumeroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomppptalesControladorUrlEnum.URL5292
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LiscomppptalesControladorEnum.PARAM1.getValue(), tipoInicial);
        param.put(LiscomppptalesControladorEnum.PARAM2.getValue(), tipoFinal);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);

        listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strNumero);
    }

    public void cargarListaNumeroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomppptalesControladorUrlEnum.URL6866
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.COMPROBANTE.getName(),
                        String.valueOf(numeroInicial));
        param.put(LiscomppptalesControladorEnum.PARAM1.getValue(), tipoInicial);
        param.put(LiscomppptalesControladorEnum.PARAM2.getValue(), tipoFinal);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);

        listaNumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strNumero);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formatoReporte) {
        String reporte = "";
        switch (formato) {
        case "1":
            reporte = "000988LisCompPptales";
            break;

        case "2":
            reporte = "000990LisCompPptalesDependencia";
            break;
        case "3":
            reporte = "000989LisCompPptalesafecta";
            break;
        default:
            break;
        }
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("numeroInicial", numeroInicial);
            reemplazar.put("numeroFinal", numeroFinal);
            reemplazar.put("sucursal", SysmanConstantes.CONS_SUCURSAL);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta("000988LisCompPptales",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_TIPOFINAL", tipoFinal);
            parametros.put("PR_TIPOINICIAL", tipoInicial);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, 
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, 
                            formatoReporte);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }


    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        numeroInicial = numeroFinal = null;
        cargarListaNumeroInicial();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        numeroInicial = numeroFinal = null;
        cargarListaNumeroInicial();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite verificar que la fecha inicial no sea mayor
     * a la fecha final.
     *
     * @return true si la fecha inicial es menor a la fecha final
     */
    private boolean validarFechas() {

        if (((fechaInicial != null) && (fechaFinal != null))
                        && (fechaFinal.before(fechaInicial))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB574"));
            fechaFinal = null;
            return false;
        }

        return true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos().get(strCodigo).toString();
        numeroInicial = numeroFinal = null;
        tipoFinal = "ZZZ";
        cargarListaTipoFinal();
        cargarListaNumeroInicial();

    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos().get(strCodigo).toString();
        numeroInicial = numeroFinal = null;
        cargarListaNumeroInicial();

    }

    public void seleccionarFilaNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroInicial = registroAux.getCampos().get(strNumero).toString();
        numeroFinal = null;
        cargarListaNumeroFinal();
    }

    public void seleccionarFilaNumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFinal = registroAux.getCampos().get(strNumero).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    public RegistroDataModelImpl getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(
        RegistroDataModelImpl listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public RegistroDataModelImpl getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(RegistroDataModelImpl listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
