package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.RelacioncontratoszipaControladorEnum;
import com.sysman.contratos.enums.RelacioncontratoszipaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 06/10/2015
 * 
 * @modifier amonroy
 * @version 2, 11/08/2017 Se realiza el Proceso de Refactoring y
 * ajustes en metodos de validacion
 */
@ManagedBean
@ViewScoped
public class RelacioncontratoszipaControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String cCodigo;
    private final String cNombre;
    private final String cNit;
    private boolean resumen;
    private String terceroInicial;
    private String nombreInicial;
    private String terceroFinal;
    private String nombreFinal;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String contratoInicial;
    private String contratoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private RegistroDataModelImpl listaTipoContratoFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of RelacioncontratoszipaControlador
     */
    public RelacioncontratoszipaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cNit = RelacioncontratoszipaControladorEnum.NIT.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONCONTRATOSZIPA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelacioncontratoszipaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTerceroInicial();
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
        fechaInicial = fechaFinal = new Date();
        abrirFormulario();
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacioncontratoszipaControladorUrlEnum.URL3642
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cNit);
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacioncontratoszipaControladorUrlEnum.URL4313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelacioncontratoszipaControladorEnum.TECEROINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cNit);
    }

    public void cargarListaTipoContratoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacioncontratoszipaControladorUrlEnum.URL5270
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTipoContratoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacioncontratoszipaControladorUrlEnum.URL6095
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelacioncontratoszipaControladorEnum.CODIGOINI.getValue(),
                        tipoContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimircmdPantalla() {
        archivoDescarga = null;
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);

    }

    public void oprimirExcel() {
        archivoDescarga = null;
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
            return;
        }
        generarInforme(FORMATOS.EXCEL);
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {

            String reporte = resumen ? "000258Relaciondecontratosresumen"
                : "000259RelacionDeContratoszipa";

            // MANEJO DE REEMPLAZOS DEL REPORTE
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoContratoInicial", SysmanFunciones
                            .concatenar("'", tipoContratoInicial, "'"));
            reemplazar.put("tipoContratoFinal", SysmanFunciones.concatenar("'",
                            tipoContratoFinal, "'"));
            reemplazar.put("terceroInicial", SysmanFunciones.concatenar("'",
                            terceroInicial, "' "));
            reemplazar.put("terceroFinal",
                            SysmanFunciones.concatenar("'", terceroFinal, "'"));
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ANO",
                            String.valueOf(SysmanFunciones.getParteFecha(
                                            new Date(),
                                            Calendar.YEAR)));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = validarVacio(registroAux.getCampos().get(cNit));
        nombreInicial = validarVacio(registroAux.getCampos().get(cNombre));
        terceroFinal = null;
        nombreFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = validarVacio(registroAux.getCampos().get(cNit));
        nombreFinal = validarVacio(registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = validarVacio(
                        registroAux.getCampos().get(cCodigo));
        contratoInicial = validarVacio(registroAux.getCampos().get(cNombre));
        tipoContratoFinal = null;
        contratoFinal = null;
        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoFinal = validarVacio(registroAux.getCampos().get(cCodigo));
        contratoFinal = validarVacio(registroAux.getCampos().get(cNombre));
    }

    /**
     * Validacion del campo que ingresa por parametro
     * 
     * @param campo
     * Valor a evaluar
     * @return Comillas si el valor del campo es nulo
     */
    private String validarVacio(Object campo) {
        return SysmanFunciones.nvl(campo, " ").toString();
    }

    public boolean getResumen() {
        return resumen;
    }

    public void setResumen(boolean resumen) {
        this.resumen = resumen;
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

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
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

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public String getContratoInicial() {
        return contratoInicial;
    }

    public void setContratoInicial(String contratoInicial) {
        this.contratoInicial = contratoInicial;
    }

    public String getContratoFinal() {
        return contratoFinal;
    }

    public void setContratoFinal(String contratoFinal) {
        this.contratoFinal = contratoFinal;
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

    public RegistroDataModelImpl getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public RegistroDataModelImpl getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        RegistroDataModelImpl listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    }

}
