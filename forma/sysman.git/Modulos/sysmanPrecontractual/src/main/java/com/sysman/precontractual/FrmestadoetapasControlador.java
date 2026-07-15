package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.enums.FrmestadoetapasControladorEnum;
import com.sysman.precontractual.enums.FrmestadoetapasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author ybecerra
 * @version 1, 04/12/2015
 * @modified jguerrero
 * @version 2. 25/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class FrmestadoetapasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String tipoContrato;
    private String contratoInicial;
    private String etapaInicial;
    private String contratoFinal;
    private String etapaFinal;
    private String estado;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTipoContrato;
    private RegistroDataModelImpl listaContratoInicial;
    private RegistroDataModelImpl listaEtapaInicial;
    private RegistroDataModelImpl listaContratoFinal;
    private RegistroDataModelImpl listaEtapaFinal;

    /**
     * Creates a new instance of FrmestadoetapasControlador
     */
    public FrmestadoetapasControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMESTADOETAPAS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(FrmestadoetapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContrato();
        abrirFormulario();
    }

    public void cargarListaTipoContrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmestadoetapasControladorUrlEnum.URL2867
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 184003
    }

    public void cargarListaContratoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestadoetapasControladorUrlEnum.URL3465
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GeneralParameterEnum.ESTADO.getName(),
                        FrmestadoetapasControladorEnum.AC.getValue());

        listaContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());

        // 188005
    }

    public void cargarListaEtapaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestadoetapasControladorUrlEnum.URL4560
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);

        listaEtapaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmestadoetapasControladorEnum.IDETAPA.getValue());

        // 497001
    }

    public void cargarListaContratoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestadoetapasControladorUrlEnum.URL5796
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(GeneralParameterEnum.ESTADO.getName(),
                        FrmestadoetapasControladorEnum.AC.getValue());
        param.put(FrmestadoetapasControladorEnum.CONTRATOINICIAL.getValue(),
                        contratoInicial);

        listaContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());

        // 188007
    }

    public void cambiarTipoContrato() {
        contratoInicial = null;
        contratoFinal = null;
        etapaInicial = null;
        cargarListaContratoInicial();
        cargarListaEtapaInicial();
    }

    public void cargarListaEtapaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestadoetapasControladorUrlEnum.URL7109
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);
        param.put(FrmestadoetapasControladorEnum.ETAPAINI.getValue(),
                        etapaInicial);

        listaEtapaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmestadoetapasControladorEnum.IDETAPA.getValue());

        // 497003
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {

            String parReporte = FrmestadoetapasControladorEnum.REPORTE421
                            .getValue();

            Map<String, Object> remplazar = new HashMap<>();
            String estadoTransaccion;

            switch (estado) {
            case "X":
                estadoTransaccion = " ";
                break;
            case "A":
                estadoTransaccion = FrmestadoetapasControladorEnum.COND_ESTADO_A
                                .getValue();
                break;
            case "P":
                estadoTransaccion = FrmestadoetapasControladorEnum.CND_ESTADO_P
                                .getValue();
                break;
            default:
                estadoTransaccion = FrmestadoetapasControladorEnum.COND_ESTADO_DEFAULT
                                .getValue();
                break;
            }

            remplazar.put(FrmestadoetapasControladorEnum.TIPOCONTRATOLOWER
                            .getValue(), tipoContrato);
            remplazar.put(FrmestadoetapasControladorEnum.CONTRATOINICIALLOWER
                            .getValue(), contratoInicial);
            remplazar.put(FrmestadoetapasControladorEnum.CONTRATOFINALLOWER
                            .getValue(), contratoFinal);
            remplazar.put(FrmestadoetapasControladorEnum.ETAPAINICIALLOWER
                            .getValue(), etapaInicial);
            remplazar.put(FrmestadoetapasControladorEnum.ETAPAFINALLOWER
                            .getValue(), etapaFinal);
            remplazar.put(FrmestadoetapasControladorEnum.ESTADOTRASACCION
                            .getValue(), estadoTransaccion);

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            String nombre = SessionUtil.getCompaniaIngreso().getNombre();
            String nit = SysmanFunciones.concatenar(
                            FrmestadoetapasControladorEnum.NITLOWER.getValue(),
                            SessionUtil.getCompaniaIngreso().getNit());

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(FrmestadoetapasControladorEnum.PR_STRSQL.getValue(),
                            strsql);
            parametros.put(FrmestadoetapasControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), nombre);
            parametros.put(FrmestadoetapasControladorEnum.PR_NITCOMPANIA
                            .getValue(), nit);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoInicial = retornarString(registroAux,
                        GeneralParameterEnum.CONSECUTIVO.getName());
        contratoFinal = null;
        cargarListaContratoFinal();

    }

    public void seleccionarFilaEtapaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        etapaInicial = retornarString(registroAux,
                        FrmestadoetapasControladorEnum.IDETAPA.getValue());
        cargarListaEtapaFinal();

    }

    public void seleccionarFilaContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoFinal = retornarString(registroAux,
                        GeneralParameterEnum.CONSECUTIVO.getName());

    }

    public void seleccionarFilaEtapaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        etapaFinal = retornarString(registroAux,
                        FrmestadoetapasControladorEnum.IDETAPA.getValue());
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getContratoInicial() {
        return contratoInicial;
    }

    public void setContratoInicial(String contratoInicial) {
        this.contratoInicial = contratoInicial;
    }

    public String getEtapaInicial() {
        return etapaInicial;
    }

    public void setEtapaInicial(String etapaInicial) {
        this.etapaInicial = etapaInicial;
    }

    public String getContratoFinal() {
        return contratoFinal;
    }

    public void setContratoFinal(String contratoFinal) {
        this.contratoFinal = contratoFinal;
    }

    public String getEtapaFinal() {
        return etapaFinal;
    }

    public void setEtapaFinal(String etapaFinal) {
        this.etapaFinal = etapaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaTipoContrato() {
        return listaTipoContrato;
    }

    public void setListaTipoContrato(List<Registro> listaTipoContrato) {
        this.listaTipoContrato = listaTipoContrato;
    }

    public RegistroDataModelImpl getListaContratoInicial() {
        return listaContratoInicial;
    }

    public void setListaContratoInicial(
        RegistroDataModelImpl listaContratoInicial) {
        this.listaContratoInicial = listaContratoInicial;
    }

    public RegistroDataModelImpl getListaEtapaInicial() {
        return listaEtapaInicial;
    }

    public void setListaEtapaInicial(RegistroDataModelImpl listaEtapaInicial) {
        this.listaEtapaInicial = listaEtapaInicial;
    }

    public RegistroDataModelImpl getListaContratoFinal() {
        return listaContratoFinal;
    }

    public void setListaContratoFinal(
        RegistroDataModelImpl listaContratoFinal) {
        this.listaContratoFinal = listaContratoFinal;
    }

    public RegistroDataModelImpl getListaEtapaFinal() {
        return listaEtapaFinal;
    }

    public void setListaEtapaFinal(RegistroDataModelImpl listaEtapaFinal) {
        this.listaEtapaFinal = listaEtapaFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
}
