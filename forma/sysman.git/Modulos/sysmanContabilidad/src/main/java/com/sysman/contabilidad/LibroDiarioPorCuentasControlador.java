package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LibroDiarioPorCuentasControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 02/05/2016
 * @modified jguerrero
 * @version 2. 07/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 */
@ManagedBean
@ViewScoped
public class LibroDiarioPorCuentasControlador extends BeanBaseModal {
    private final String compania;
    private String tipoInicial;
    private String tipoFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    private String anoTrabajo;
    private final String codigoCons;

    /**
     * Creates a new instance of LibroDiarioPorCuentasControlador
     */
    public LibroDiarioPorCuentasControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIBRO_DIARIO_POR_CUENTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LibroDiarioPorCuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = fechaFinal = new Date();
        anoTrabajo = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaTipoInicial();
        tipoInicial = " ";
        cargarListaTipoFinal();
        tipoFinal = "ZZZ";
        cargarListaCuentaInicial();
        cuentaInicial = "0";
        cargarListaCuentaFinal();
        cuentaFinal = "9999999999999999999999999999";
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
                                        LibroDiarioPorCuentasControladorUrlEnum.URL3284
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaTipoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroDiarioPorCuentasControladorUrlEnum.URL3963
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOINICIAL", tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroDiarioPorCuentasControladorUrlEnum.URL4797
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroDiarioPorCuentasControladorUrlEnum.URL5829
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put("CUENTAINICIAL", cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validaciones() {
        boolean rta = true;
        if (SysmanFunciones.validarVariableVacio(anoTrabajo)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB841"));
            rta = false;
        }
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB168"));
            rta = false;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB169"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB835"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB836"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB842"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB843"));
            rta = false;
        }

        return rta;
    }

    private void generarInforme(ReportesBean.FORMATOS formato) {

        if (!validaciones()) {
            return;
        }
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "000718LisLibroDiarioPorCuentas";
            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FECHAINICIAL", fechaIni);
            parametros.put("PR_FECHAFINAL", fechaFin);
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        anoTrabajo = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCuentaInicial();
        cuentaInicial = null;
        cargarListaCuentaFinal();
        cuentaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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

    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
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

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

}
