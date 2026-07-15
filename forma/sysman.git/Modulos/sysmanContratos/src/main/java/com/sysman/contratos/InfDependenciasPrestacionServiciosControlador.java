
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InfDependenciasPrestacionServiciosControladorUrlEnum;
import com.sysman.contratos.reports.InfDependenciasPrestacionServiciosReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.SelectEvent;

import net.sf.dynamicreports.report.exception.DRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 01/10/2015
 * @modified jguerrero
 * @version 2. 16/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class InfDependenciasPrestacionServiciosControlador
                extends BeanBaseModal {

    private final String compania;
    private final String anioFinalCons;
    private final String anioInicialCons;
    private final String dependenciaCons;
    private final String companiaCons;
    private String dependencia;
    private String anioInicial;
    private String anioFinal;
    private List<Registro> listaCmbAnoInicial;
    private List<Registro> listaCmbAnoFinal;
    private RegistroDataModelImpl listaCmbDependencia;
    private String nombreDependencia;
    private InfDependenciasPrestacionServiciosReporteador infDepPresServRepo;

    /**
     * Creates a new instance of
     * InfDependenciasPrestacionServiciosControlador
     */
    public InfDependenciasPrestacionServiciosControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.INF_DEPENDENCIAS_PRESTACION_SERVICIOS_CONTROLADOR
                        .getCodigo();
        infDepPresServRepo = new InfDependenciasPrestacionServiciosReporteador();
        compania = SessionUtil.getCompania();
        anioFinalCons = "anioFinal";
        anioInicialCons = "anioInicial";
        dependenciaCons = "dependencia";
        companiaCons = GeneralParameterEnum.COMPANIA.getName().toLowerCase();
        anioInicial = String.valueOf(
                        SysmanFunciones.getParteFecha(new Date(),
                                        Calendar.YEAR));
        anioFinal = String.valueOf(
                        SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR)
                            + 1);
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(InfDependenciasPrestacionServiciosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbAnoInicial();
        cargarListacmbAnoFinal();
        cargarListacmbDependencia();
        abrirFormulario();
    }

    public void cargarListacmbAnoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasPrestacionServiciosControladorUrlEnum.URL5277
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacmbAnoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaCmbAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasPrestacionServiciosControladorUrlEnum.URL5691
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfDependenciasPrestacionServiciosControladorUrlEnum.URL6120
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 62002
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>

        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando12() {
        // <CODIGO_DESARROLLADO>
        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListacmbAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    private void generaExcel() {

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(companiaCons, compania);
        reemplazar.put(anioInicialCons, anioInicial);
        reemplazar.put(anioFinalCons, anioFinal);
        reemplazar.put(dependenciaCons, dependencia);
        String strSql = Reporteador.resuelveConsulta(
                        "800012DependenciasPrestacion",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        exportarHojaDatos(strSql);

    }

    public void exportarHojaDatos(String sql) {
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext
                            .getCurrentInstance().getExternalContext()
                            .getResponse();
            ServletOutputStream stream;
            // ReportesBean.FORMATOS.EXCEL97
            response.addHeader("Content-disposition",
                            "attachment; filename=hojaDatos.xls");
            response.setContentType("application/vnd.ms-excel");
            stream = response.getOutputStream();

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(companiaCons, compania);
            reemplazar.put(anioInicialCons, anioInicial);
            reemplazar.put(anioFinalCons, anioFinal);
            reemplazar.put(dependenciaCons, dependencia);

            boolean rta = infDepPresServRepo
                            .exportarHojaDatosExcel(sql, stream, reemplazar,
                                            nombreDependencia);
            if (!rta) {
                stream = null;
                response.reset();
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2065"));
            }

            if (stream != null) {
                stream.flush();
                stream.close();
                FacesContext.getCurrentInstance().responseComplete();
            }
        }
        catch (IOException | SQLException | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaCmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = retornoString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombreDependencia = retornoString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getAnioInicial() {
        return anioInicial;
    }

    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    public String getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    public List<Registro> getListaCmbAnoInicial() {
        return listaCmbAnoInicial;
    }

    public void setListaCmbAnoInicial(List<Registro> listaCmbAnoInicial) {
        this.listaCmbAnoInicial = listaCmbAnoInicial;
    }

    public List<Registro> getListaCmbAnoFinal() {
        return listaCmbAnoFinal;
    }

    public void setListaCmbAnoFinal(List<Registro> listaCmbAnoFinal) {
        this.listaCmbAnoFinal = listaCmbAnoFinal;
    }

    public RegistroDataModelImpl getListaCmbDependencia() {
        return listaCmbDependencia;
    }

    public void setListaCmbDependencia(
        RegistroDataModelImpl listaCmbDependencia) {
        this.listaCmbDependencia = listaCmbDependencia;
    }

    @Override
    public void abrirFormulario() {
        // NO ESTï¿½ IMPLEMENTADO
    }

    private String retornoString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}