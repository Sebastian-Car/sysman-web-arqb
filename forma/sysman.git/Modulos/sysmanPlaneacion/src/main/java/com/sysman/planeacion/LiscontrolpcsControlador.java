package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.LiscontrolpcsControladorEnum;
import com.sysman.planeacion.enums.LiscontrolpcsControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.application.resource.StreamedContentHandler;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 17/12/2015
 * @modified jguerrero
 * @version 2. 07/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class LiscontrolpcsControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreCodigoIni;
    private String nombreCodigoFin;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private final String codigoCons;
    private final String nombreCons;

    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of LiscontrolpcsControlador
     */
    public LiscontrolpcsControlador() {
        super();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISCONTROLPCS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LiscontrolpcsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListacmbElementoDesde();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscontrolpcsControladorUrlEnum.URL3005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.getParteFecha(fechaInicial,
                                        Calendar.YEAR));

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 542001 COMPANIA ANO
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscontrolpcsControladorUrlEnum.URL3869
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LiscontrolpcsControladorEnum.CODIGOINI.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.getParteFecha(fechaInicial,
                                        Calendar.YEAR));

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 542003 CODIGOINI ano
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

    public void cambiarFechaInicial() {

        cargarListacmbElementoDesde();

    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            String parReporte = LiscontrolpcsControladorEnum.REPORTE442
                            .getValue();

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put(LiscontrolpcsControladorEnum.FECHAINICIAL.getValue(),
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put(LiscontrolpcsControladorEnum.FECHAFINAL.getValue(),
                            SysmanFunciones.formatearFecha(fechaFinal));
            remplazar.put(LiscontrolpcsControladorEnum.CODIGOINICIAL.getValue(),
                            codigoInicial);
            remplazar.put(LiscontrolpcsControladorEnum.CODIGOFINAL.getValue(),
                            codigoFinal);
            remplazar.put(LiscontrolpcsControladorEnum.ANO.getValue(),
                            SysmanFunciones.formatearFecha(fechaInicial));

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            HashMap<String, Object> parametros = new HashMap<>();

            parametros.put(LiscontrolpcsControladorEnum.PR_STRSQL.getValue(),
                            strsql);
            parametros.put(LiscontrolpcsControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(),
                            SysmanFunciones.concatenar(
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNombre(),
                                            " - ",
                                            SessionUtil.getCompaniaIngreso()
                                                            .getSigla()));
            parametros.put(LiscontrolpcsControladorEnum.PR_FECHAS.getValue(),
                            SysmanFunciones.concatenar(
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial),
                                            " a ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaFinal)));

            parametros.put(LiscontrolpcsControladorEnum.PR_RUBROS.getValue(),
                            SysmanFunciones.concatenar(" ENTRE RUBRO ",
                                            codigoInicial, " - ",
                                            nombreCodigoIni, " HASTA ",
                                            codigoFinal, " - ",
                                            nombreCodigoFin));

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = retornarString(registroAux, codigoCons);
        nombreCodigoIni = retornarString(registroAux, nombreCons);
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = retornarString(registroAux, codigoCons);
        nombreCodigoFin = retornarString(registroAux, nombreCons);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>

    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContentHandler archivoDescarga) {
        this.archivoDescarga = (StreamedContent) archivoDescarga;
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

    public String getNombreCodigoIni() {
        return nombreCodigoIni;
    }

    public void setNombreCodigoIni(String nombreCodigoIni) {
        this.nombreCodigoIni = nombreCodigoIni;
    }

    public String getNombreCodigoFin() {
        return nombreCodigoFin;
    }

    public void setNombreCodigoFin(String nombreCodigoFin) {
        this.nombreCodigoFin = nombreCodigoFin;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
