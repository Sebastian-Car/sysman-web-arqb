
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InfDependenciasTipoGastoControladorUrlEnum;
import com.sysman.contratos.reports.InfDependenciasTipoGastoReporteador;
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
import com.sysman.util.enums.ConstanteArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 01/10/2015
 * @version 2, 10/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 */
@ManagedBean
@ViewScoped
public class InfDependenciasTipoGastoControlador extends BeanBaseModal {

    private final String compania;
    private String dependencia;
    private String anioInicial;
    private String anioFinal;
    private List<Registro> listaCmbAnoInicial;
    private List<Registro> listaCmbAnoFinal;
    private RegistroDataModelImpl listaCmbDependencia;
    private String nombreDependencia;

    private InfDependenciasTipoGastoReporteador infDependenciasTipoGastoReporteador;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of InfDependenciasTipoGastoControlador
     */
    public InfDependenciasTipoGastoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            anioInicial = String.valueOf(
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR));
            anioFinal = String.valueOf(
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR)
                                + 1);
            numFormulario = GeneralCodigoFormaEnum.INF_DEPENDENCIAS_TIPO_GASTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            infDependenciasTipoGastoReporteador = new InfDependenciasTipoGastoReporteador();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            InfDependenciasTipoGastoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
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
        // 4001
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasTipoGastoControladorUrlEnum.URL5131
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAnoFinal() {
        // 4027
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
        try {
            listaCmbAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasTipoGastoControladorUrlEnum.URL5548
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbDependencia() {
        // 62032
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfDependenciasTipoGastoControladorUrlEnum.URL6052
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAnoInicial() {
        // <CODIGO_DESARROLLADO>
        anioFinal = null;
        cargarListacmbAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    private void generaExcel() {
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(anioInicial)
            || SysmanFunciones.validarVariableVacio(anioFinal)
            || SysmanFunciones.validarVariableVacio(dependencia)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2120"));
            return;
        }
        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", compania);
        reemplazar.put("anioInicial", anioInicial);
        reemplazar.put("anioFinal", anioFinal);
        reemplazar.put("dependencia", dependencia);

        String strSql = Reporteador.resuelveConsulta(
                        "800015DependenciasTipoGasto",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(infDependenciasTipoGastoReporteador.exportarHojaDatosExcel(strSql,
                            reemplazar,stream, nombreDependencia)) {
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(stream.toByteArray()),
                                "Informe.xls",
                                ConstanteArchivo.EXCEL97.getContentType());
                
            } else {
                JsfUtil.agregarMensajeError(idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }
        }
        catch (SQLException | JRException | IOException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaCmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
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

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public InfDependenciasTipoGastoReporteador getInfDependenciasTipoGastoReporteador() {
        return infDependenciasTipoGastoReporteador;
    }

    public void setInfDependenciasTipoGastoReporteador(
        InfDependenciasTipoGastoReporteador infDependenciasTipoGastoReporteador) {
        this.infDependenciasTipoGastoReporteador = infDependenciasTipoGastoReporteador;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // NO ESTA IMPLEMENTADO
    }
}
