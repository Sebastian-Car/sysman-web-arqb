package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.AdicionesdecontratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 18/11/2015
 * 
 * @author eamaya
 * @version 2.0, 02/08/2017, Proceso de Refactoring y cambio
 * enumeracion del controlador
 */
@ManagedBean
@ViewScoped

public class AdicionesdecontratosControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloContratos;
    private String tipoContrato;
    private BigDecimal numeroInicial;
    private String contAfectado;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTipoContratoInicial;
    private RegistroDataModelImpl listaNumeroInicial;

    /**
     * Creates a new instance of AdicionesdecontratosControlador
     */
    public AdicionesdecontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.ADICIONESDECONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AdicionesdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListaNumeroInicial();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "M");

        try {
            listaTipoContratoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionesdecontratosControladorUrlEnum.URL3560
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumeroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionesdecontratosControladorUrlEnum.URL3510
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);

        listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void oprimircmbPdf() {
        if (tipoContrato != null && numeroInicial != null) {
            generarReporteInformesDeAdiciones(ReportesBean.FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel() {
        if (tipoContrato != null && numeroInicial != null) {
            generarReporteInformesDeAdiciones(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void cambiarTipoContratoInicial() {
        numeroInicial = null;
        contAfectado = null;
        cargarListaNumeroInicial();
    }

    public void seleccionarFilaNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroInicial = new BigDecimal(SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString());
        contAfectado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("AFECTADO"), "")
                        .toString();
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public BigDecimal getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(BigDecimal numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getContAfectado() {
        return contAfectado;
    }

    public void setContAfectado(String contAfectado) {
        this.contAfectado = contAfectado;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public RegistroDataModelImpl getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(
        RegistroDataModelImpl listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    private void generarReporteInformesDeAdiciones(FORMATOS formato) {

        archivoDescarga = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipomodificacion", tipoContrato);
            reemplazar.put("numero", numeroInicial.toString());

            String strSql = Reporteador.resuelveConsulta(
                            "000388InformesDeAdiciones",
                            Integer.parseInt(moduloContratos), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000388InformesDeAdiciones", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(AdicionesdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            Logger.getLogger(AdicionesdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
