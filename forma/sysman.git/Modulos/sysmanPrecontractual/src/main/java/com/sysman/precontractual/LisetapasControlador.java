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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.enums.LisetapasControladorEnum;
import com.sysman.precontractual.enums.LisetapasControladorUrlEnum;
import com.sysman.reportes.Reporteador;

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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 03/12/2015
 *
 * @version2, 02/06/2017 sdaza - incluir ruta para validar reporte
 * faltante
 * @modified jguerrero
 * @version 2. 31/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class LisetapasControlador extends BeanBaseModal {

    private String compania;
    private String moduloPrecontractual;
    private String tipoContrato;
    private String etapaInicial;
    private String etapaFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listatipoContrato;
    private List<Registro> listaVariableIni;
    private List<Registro> listaVariableFin;

    /**
     * Creates a new instance of LisetapasControlador
     */
    public LisetapasControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISETAPAS_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            moduloPrecontractual = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LisetapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListatipoContrato();
        cargarListaVariableIni();
        cargarListaVariableFin();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado del bean base
    }

    public void cargarListatipoContrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listatipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisetapasControladorUrlEnum.URL2748
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaVariableIni() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisetapasControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);
        try {
            listaVariableIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisetapasControladorUrlEnum.URL3299
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 497005 TIPOCONTRATO
    }

    public void cargarListaVariableFin() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisetapasControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);
        param.put(LisetapasControladorEnum.ETAPAINICIAL.getValue(),
                        etapaInicial);

        try {
            listaVariableFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisetapasControladorUrlEnum.URL4062
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 497006 TIPOCONTRATO ETAPAINICIAL
    }

    public void oprimirImprimir() {
        archivoDescarga = null;
        generarReporteLisEtapasxTipoTTP(ReportesBean.FORMATOS.PDF);

    }

    public void oprimirbtnExcel() {
        archivoDescarga = null;
        generarReporteLisEtapasxTipoTTP(ReportesBean.FORMATOS.EXCEL97);

    }

    public void cambiartipoContrato() {
        etapaInicial = null;
        etapaFinal = null;
        cargarListaVariableIni();
        cargarListaVariableFin();
    }

    /**
     * Metodo ejecutado al cambiar el control VariableIni
     * 
     * 
     */
    public void cambiarVariableIni() {
        // <CODIGO_DESARROLLADO>
        etapaFinal = null;
        cargarListaVariableFin();
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporteLisEtapasxTipoTTP(FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(LisetapasControladorEnum.TIPOCONTRATOLOWER
                            .getValue(), tipoContrato);
            reemplazar.put(LisetapasControladorEnum.ETAPAINICIALLOWER
                            .getValue(), etapaInicial);
            reemplazar.put(LisetapasControladorEnum.ETAPAFINALLOWER.getValue(),
                            etapaFinal);

            String strSql = Reporteador.resuelveConsulta(
                            LisetapasControladorEnum.REPORTE410.getValue(),
                            Integer.parseInt(moduloPrecontractual), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE LOS PARAMETROS DEL REPORTE
            parametros.put(LisetapasControladorEnum.PR_STRSQL.getValue(),
                            strSql);
            parametros.put(LisetapasControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(LisetapasControladorEnum.PR_NITCOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNit());

            archivoDescarga = JsfUtil.exportarStreamed(
                            LisetapasControladorEnum.REPORTE410.getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getEtapaInicial() {
        return etapaInicial;
    }

    public void setEtapaInicial(String etapaInicial) {
        this.etapaInicial = etapaInicial;
    }

    public String getEtapaFinal() {
        return etapaFinal;
    }

    public void setEtapaFinal(String etapaFinal) {
        this.etapaFinal = etapaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListatipoContrato() {
        return listatipoContrato;
    }

    public void setListatipoContrato(List<Registro> listatipoContrato) {
        this.listatipoContrato = listatipoContrato;
    }

    public List<Registro> getListaVariableIni() {
        return listaVariableIni;
    }

    public void setListaVariableIni(List<Registro> listaVariableIni) {
        this.listaVariableIni = listaVariableIni;
    }

    public List<Registro> getListaVariableFin() {
        return listaVariableFin;
    }

    public void setListaVariableFin(List<Registro> listaVariableFin) {
        this.listaVariableFin = listaVariableFin;
    }

}
