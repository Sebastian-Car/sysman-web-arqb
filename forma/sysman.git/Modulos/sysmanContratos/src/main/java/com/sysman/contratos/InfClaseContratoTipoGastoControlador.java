package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InfClaseContratoTipoGastoControladorUrlEnum;
import com.sysman.contratos.reports.InfClaseContratoTipoGastoReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 29/09/2015
 * 
 * @version 2, 09/08/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Reemplazo del numero del formulario por enumerado.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class InfClaseContratoTipoGastoControlador extends BeanBaseModal {

    private final String compania;
    private String claseContrato;
    private String anioInicial;
    private String anioFinal;
    private List<Registro> listaCmbclasecontrato;
    private List<Registro> listaCmbAnoInicial;
    private List<Registro> listaCmbAnoFinal;
    private String descripcionContrato;

    /** Atributo que contiene el archivo de descarga */
    private StreamedContent archivoDescarga;

    private InfClaseContratoTipoGastoReporteador infClaseContratoTipoGastoReporteador;

    /**
     * Creates a new instance of InfClaseContratoTipoGastoControlador
     */
    public InfClaseContratoTipoGastoControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 223
            numFormulario = GeneralCodigoFormaEnum.INF_CLASE_CONTRATO_TIPO_GASTO_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            infClaseContratoTipoGastoReporteador = new InfClaseContratoTipoGastoReporteador();
        }
        catch (Exception ex) {
            Logger.getLogger(InfClaseContratoTipoGastoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        anioFinal = anioInicial = String
                        .valueOf(SysmanFunciones.ano(new Date()));

        cargarListacmbAnoInicial();
        cargarListacmbAnoFinal();
        cargarListacmbclasecontrato();
        abrirFormulario();
    }

    public void cargarListacmbclasecontrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbclasecontrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfClaseContratoTipoGastoControladorUrlEnum.URL4962
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfClaseContratoTipoGastoControladorUrlEnum.URL5439
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
                                                            InfClaseContratoTipoGastoControladorUrlEnum.URL5867
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirComando14() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAnoInicial() {
        // <CODIGO_DESARROLLADO>
        anioFinal = "";

        cargarListacmbAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    private void generaExcel() {
        descripcionContrato = service.buscarEnLista(claseContrato, "CODIGO",
                        "NOMBRE", listaCmbclasecontrato);

        exportarHojaDatos();
    }

    public void exportarHojaDatos() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            infClaseContratoTipoGastoReporteador.exportarHojaDatosExcel(stream,
                            compania, anioInicial, anioFinal, claseContrato,
                            descripcionContrato);

            if (stream.size() == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
                return;
            }

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(stream.toByteArray()),
                            "informeConsolidado.xls",
                            ConstanteArchivo.EXCEL97.getContentType());
        }
        catch (DRException | IOException | SysmanException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getClaseContrato() {
        return claseContrato;
    }

    public void setClaseContrato(String claseContrato) {
        this.claseContrato = claseContrato;
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

    public List<Registro> getListaCmbclasecontrato() {
        return listaCmbclasecontrato;
    }

    public void setListaCmbclasecontrato(List<Registro> listaCmbclasecontrato) {
        this.listaCmbclasecontrato = listaCmbclasecontrato;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
