package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LiscomprobantesControladorEnum;
import com.sysman.contabilidad.enums.LiscomprobantesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 04/05/2016
 * @version 2, 19/04/2017, mzanguna, refactorizaci�n.
 * @version 3, 20/04/2017, Cambio EJB.
 */
@ManagedBean
@ViewScoped

public class LiscomprobantesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String tipoIncial;
    private String tipoFinal;
    private String terceroInicial;
    private String terceroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LiscomprobantesControlador
     */
    public LiscomprobantesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            initValores();
            numFormulario = GeneralCodigoFormaEnum.LISCOMPROBANTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LiscomprobantesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void initValores() {
        tipoFinal = "ZZZ";
        tipoIncial = "AAA";
        terceroFinal = "ZZZZZZZZZZZ";
        terceroInicial = "00000000000";
        fechaInicial = fechaFinal = new Date();
    }

    @PostConstruct
    public void init() {
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomprobantesControladorUrlEnum.URL3394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        LiscomprobantesControladorEnum.COD.getValue());

    }

    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomprobantesControladorUrlEnum.URL3975
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LiscomprobantesControladorEnum.TIPOINI.getValue(),
                        tipoIncial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        LiscomprobantesControladorEnum.COD.getValue());
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomprobantesControladorUrlEnum.URL4731
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscomprobantesControladorUrlEnum.URL5321
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LiscomprobantesControladorEnum.TERCEROINI.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");

    }

    public void oprimirImprimir(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando14(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("tipoInicial", "'" + tipoIncial + "'");
            reemplazar.put("tipoFinal", "'" + tipoFinal + "'");
            reemplazar.put("terceroInicial", "'" + terceroInicial + "'");
            reemplazar.put("terceroFinal", "'" + terceroFinal + "'");

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000726LisComprobantes";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_FECHAINCIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FORMS_LISCOMPROBANTES_TIPOINICIAL", tipoIncial);
            parametros.put("PR_TIPOFINAL", tipoFinal);
            parametros.put("PR_TIPOINICIAL", tipoIncial);

            String valorParametro = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "FORMATO CALIDAD", modulo,
                                            new Date(), false), "NO");

            if (("SI").equalsIgnoreCase(valorParametro)) {
                reporte = "000735LisComprobantesCOS";
            }
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoIncial = registroAux.getCampos()
                        .get(LiscomprobantesControladorEnum.COD.getValue())
                        .toString();
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos()
                        .get(LiscomprobantesControladorEnum.COD.getValue())
                        .toString();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        terceroFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
    }

    public String getTipoIncial() {
        return tipoIncial;
    }

    public void setTipoIncial(String tipoIncial) {
        this.tipoIncial = tipoIncial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

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
}
