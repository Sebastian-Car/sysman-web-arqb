package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.RelcontratosvencerControladorEnum;
import com.sysman.contratos.enums.RelcontratosvencerControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 13/10/2015
 * @modified jguerrero
 * @version 2. 10/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class RelcontratosvencerControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo = SessionUtil.getModulo();
    private String terceroInicial;
    private String terceroFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    private RegistroDataModelImpl listaTipoContratoInicial;

    private RegistroDataModelImpl listaTipoContratoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private String parametro;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private static final String MSMTRANSINTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";

    /**
     * Creates a new instance of RelcontratosvencerControlador
     */
    public RelcontratosvencerControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.RELCONTRATOSVENCER_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelcontratosvencerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();

        }
    }

    @PostConstruct
    public void inicializar() {
        try {

            parametro = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            RelcontratosvencerControladorEnum.PARAM0
                                                            .getValue(),
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO");

        }
        catch (SystemException ex) {
            Logger.getLogger(RelcontratosvencerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();

        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(RelcontratosvencerControladorEnum.PARAM1.getValue(),
                        "SI".equals(parametro) ? "0" : null);

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosvencerControladorUrlEnum.URL4052
                                                        .getValue());
        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaTipoContratoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(RelcontratosvencerControladorEnum.PARAM2.getValue(),
                        tipoContratoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelcontratosvencerControladorEnum.PARAM1.getValue(),
                        "SI".equals(parametro) ? "0" : null);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosvencerControladorUrlEnum.URL4051
                                                        .getValue());
        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaTerceroInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosvencerControladorUrlEnum.URL5039
                                                        .getValue());
        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        RelcontratosvencerControladorEnum.PARAM4.getValue());

    }

    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelcontratosvencerControladorUrlEnum.URL5681
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(RelcontratosvencerControladorEnum.PARAM3.getValue(),
                        terceroInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        RelcontratosvencerControladorEnum.PARAM4.getValue());

    }

    public void oprimircmdPDF() {
        archivoDescarga = null;

        try {

            if (fechaInicial.after(fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB574"));
                return;
            }
            generarReporteRelContratosVencer(ReportesBean.FORMATOS.PDF);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RelcontratosvencerControladorUrlEnum.URL4053
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.VALOR.getName(),
                            "SI");
            fields.put(RelcontratosvencerControladorEnum.PARAM5.getValue(),
                            compania);
            fields.put(RelcontratosvencerControladorEnum.PARAM6.getValue(),
                            RelcontratosvencerControladorEnum.PARAM7
                                            .getValue());
            fields.put(RelcontratosvencerControladorEnum.PARAM8.getValue(),
                            RelcontratosvencerControladorEnum.PARAM9
                                            .getValue());
            fields.put(RelcontratosvencerControladorEnum.PARAM10.getValue(),
                            new Date());
            fields.put(RelcontratosvencerControladorEnum.PARAM11.getValue(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            MSMTRANSINTERRUMPIDA),
                                            ex.getMessage()));
            Logger.getLogger(RelcontratosvencerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;
        try {

            if (fechaInicial.after(fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB574"));
                return;
            }
            generarReporteRelContratosVencer(ReportesBean.FORMATOS.EXCEL97);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RelcontratosvencerControladorUrlEnum.URL4053
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.VALOR.getName(),
                            "SI");
            fields.put(RelcontratosvencerControladorEnum.PARAM5.getValue(),
                            compania);
            fields.put(RelcontratosvencerControladorEnum.PARAM6.getValue(),
                            RelcontratosvencerControladorEnum.PARAM7
                                            .getValue());
            fields.put(RelcontratosvencerControladorEnum.PARAM8.getValue(),
                            "-1");
            fields.put(RelcontratosvencerControladorEnum.PARAM10.getValue(),
                            new Date());
            fields.put(RelcontratosvencerControladorEnum.PARAM11.getValue(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(MSMTRANSINTERRUMPIDA),
                            ex.getMessage()));
            Logger.getLogger(RelcontratosvencerControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = retornoString(registroAux,
                        RelcontratosvencerControladorEnum.PARAM4.getValue());

        nombreInicial = retornoString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        terceroFinal = null;
        nombreFinal = null;

        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = retornoString(registroAux,
                        RelcontratosvencerControladorEnum.PARAM4.getValue());

        nombreFinal = retornoString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = retornoString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        tipoContratoFinal = null;
        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoFinal = retornoString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

    }

    private void generarReporteRelContratosVencer(FORMATOS formatos) {

        try {
            String parReporte = RelcontratosvencerControladorEnum.PARAM12
                            .getValue();

            HashMap<String, Object> remplazar = new HashMap<>();
            remplazar.put("terceroInicial", terceroInicial);
            remplazar.put("terceroFinal", terceroFinal);
            remplazar.put("contratoIncial", tipoContratoInicial);
            remplazar.put("contratoFinal", tipoContratoFinal);
            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            String strSql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_SUBTITULO", SysmanFunciones.concatenar(
                            " Entre ", SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial, "dd-MMM/YYYY"),
                            " y ", SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal,
                                            "dd-MMM/YYYY")));

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        // Metodo Ejecutado cuando se invoca el formulario

    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
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

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
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

    public RegistroDataModelImpl getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public RegistroDataModelImpl getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        RegistroDataModelImpl listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    private String retornoString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
