
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.ejb.EjbContratosCeroRemote;
import com.sysman.contratos.enums.InfDependenciasControladorEnum;
import com.sysman.contratos.enums.InfDependenciasControladorUrlEnum;
import com.sysman.contratos.reports.InfDependenciasReporteador;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 29/09/2015
 * 
 * @modifier amonroy
 * @version 2, 09/08/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones que son llamadas en el
 * controlador
 */
@ManagedBean
@ViewScoped
public class InfDependenciasControlador extends BeanBaseModal {

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
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de EjbContratosCeroRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_CONTRATOS
     */
    @EJB
    private EjbContratosCeroRemote ejbContratosCero;
    /**
     * Clase delegada para la creacion de la Hoja de Datos
     */
    private InfDependenciasReporteador infDependenciasReporteador;

    /**
     * Creates a new instance of InfDependenciasControlador
     */
    public InfDependenciasControlador() {
        super();
        compania = SessionUtil.getCompania();
        anioFinalCons = InfDependenciasControladorEnum.ANIOFINAL.getValue();
        anioInicialCons = InfDependenciasControladorEnum.ANIOINICIAL.getValue();
        dependenciaCons = InfDependenciasControladorEnum.DEPENDENCIA.getValue();
        companiaCons = InfDependenciasControladorEnum.COMPANIA.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.INF_DEPENDENCIAS_CONTROLADOR
                            .getCodigo();
            anioInicial = String.valueOf(
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR));
            anioFinal = String.valueOf(
                            SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR)
                                + 1);
            validarPermisos();
            infDependenciasReporteador = new InfDependenciasReporteador();
        }
        catch (Exception ex) {
            Logger.getLogger(InfDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarlistaCmbAnoInicial();
        cargarlistaCmbAnoFinal();
        cargarlistaCmbDependencia();
        abrirFormulario();
    }

    public void cargarlistaCmbAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasControladorUrlEnum.URL5267
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarlistaCmbAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaCmbAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasControladorUrlEnum.URL5741
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarlistaCmbDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfDependenciasControladorUrlEnum.URL6192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirComando11() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAnoInicial() {
        // <CODIGO_DESARROLLADO>
        anioFinal = null;
        cargarlistaCmbAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    private void generaInforme() {
        try {
            String pivot = ejbContratosCero.getPivotDependencias(compania,
                            Integer.parseInt(anioInicial),
                            Integer.parseInt(anioFinal),
                            dependencia);

            if (pivot == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2065"));
                return;
            }
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(companiaCons, compania);
            reemplazar.put(anioInicialCons, anioInicial);
            reemplazar.put(anioFinalCons, anioFinal);
            reemplazar.put(dependenciaCons, dependencia);
            reemplazar.put("pivot", pivot);
            String strSql = Reporteador.resuelveConsulta(
                            InfDependenciasControladorEnum.DEPENDENCIAS4
                                            .getValue(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            exportarHojaDatos(strSql);

        }
        catch (NumberFormatException
                        | SystemException ex) {
            Logger.getLogger(InfDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void exportarHojaDatos(String sql) {
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            infDependenciasReporteador.exportarHojaDatosExcel(sql,
                            stream, compania, anioInicial, anioFinal,
                            dependencia, nombreDependencia);

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(stream.toByteArray()),
                            InfDependenciasControladorEnum.INFORME.getValue(),
                            ConstanteArchivo.EXCEL97.getContentType());

        }
        catch (IOException | SQLException | DRException | SysmanException
                        | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaCmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
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

    public List<Registro> getlistaCmbAnoInicial() {
        return listaCmbAnoInicial;
    }

    public void setlistaCmbAnoInicial(List<Registro> listaCmbAnoInicial) {
        this.listaCmbAnoInicial = listaCmbAnoInicial;
    }

    public List<Registro> getlistaCmbAnoFinal() {
        return listaCmbAnoFinal;
    }

    public void setlistaCmbAnoFinal(List<Registro> listaCmbAnoFinal) {
        this.listaCmbAnoFinal = listaCmbAnoFinal;
    }

    public RegistroDataModelImpl getlistaCmbDependencia() {
        return listaCmbDependencia;
    }

    public void setlistaCmbDependencia(
        RegistroDataModelImpl listaCmbDependencia) {
        this.listaCmbDependencia = listaCmbDependencia;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
