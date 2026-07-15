package com.sysman.planeacion;

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
import com.sysman.planeacion.enums.InformepaasControladorEnum;
import com.sysman.planeacion.enums.InformepaasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
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
 * @author ybecerra
 * @version 1, 30/12/2015
 * @modified jguerrero
 * @version 2. 07/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 * 
 */
@ManagedBean
@ViewScoped

public class InformepaasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String responsable;
    private String anio;
    private String descripcion;
    private String codigos;
    private StreamedContent archivoDescarga;
    private List<Registro> listaanio;
    private RegistroDataModelImpl listaresponsable;

    /**
     * Creates a new instance of InformepaasControlador
     */
    public InformepaasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORMEPAAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InformepaasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
        cargarListaanio();
        cargarListaresponsable();
        abrirFormulario();
    }

    public void cargarListaanio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformepaasControladorUrlEnum.URL2657
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaresponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformepaasControladorUrlEnum.URL2989
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaresponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NOMBRE.getName());

    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formatos) {

        try {
            String parReporte = InformepaasControladorEnum.REPORTE457
                            .getValue();
            String subReporte = InformepaasControladorEnum.SUB458.getValue();

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put(InformepaasControladorEnum.ANIO_LOWER.getValue(),
                            anio);

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);
            String subStrsql = Reporteador.resuelveConsulta(subReporte,
                            Integer.parseInt(modulo), remplazar);

            HashMap<String, Object> parametros = new HashMap<>();

            parametros.put(InformepaasControladorEnum.PR_STRSQL.getValue(),
                            strsql);
            parametros.put(InformepaasControladorEnum.PR_STRSQL_SUB_PAA
                            .getValue(), subStrsql);
            parametros.put(InformepaasControladorEnum.PR_DESCRIPCIONA
                            .getValue(), descripcion);
            parametros.put(InformepaasControladorEnum.PR_CODIGOA.getValue(),
                            responsable);
            parametros.put(InformepaasControladorEnum.PR_RESPONSABLEA
                            .getValue(), codigos);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaresponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsable = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCodigos() {
        return codigos;
    }

    public void setCodigos(String codigos) {
        this.codigos = codigos;
    }

    public List<Registro> getListaanio() {
        return listaanio;
    }

    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }

    public RegistroDataModelImpl getListaresponsable() {
        return listaresponsable;
    }

    public void setListaresponsable(RegistroDataModelImpl listaresponsable) {
        this.listaresponsable = listaresponsable;
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado del bean base
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
