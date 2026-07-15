package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.IadyprocontratoControladorEnum;
import com.sysman.contratos.enums.IadyprocontratoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 18/11/2015
 * @modified jguerrero
 * @version 2. 08/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 */
@ManagedBean
@ViewScoped

public class IadyprocontratoControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloContratos;
    private final String numeroCons;

    private String tipoContratoInicial;
    private String numeroInicial;
    private String numeroFinal;
    private String tipoContrato;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private RegistroDataModelImpl listaNumeroInicial;
    private RegistroDataModelImpl listaNumeroFinal;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of IadyprocontratoControlador
     */
    public IadyprocontratoControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.IADYPROCONTRATO_CONTROLADOR
                        .getCodigo();
        moduloContratos = SessionUtil.getModulo();
        compania = SessionUtil.getCompania();
        numeroCons = GeneralParameterEnum.NUMERO.getName();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(IadyprocontratoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListaNumeroInicial();
        cargarListaNumeroFinal();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IadyprocontratoControladorUrlEnum.URL2957
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaNumeroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IadyprocontratoControladorUrlEnum.URL3746
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(IadyprocontratoControladorEnum.PARAM2.getValue(),
                        tipoContratoInicial);

        listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);

    }

    public void cargarListaNumeroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IadyprocontratoControladorUrlEnum.URL4650
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(IadyprocontratoControladorEnum.PARAM2.getValue(),
                        tipoContratoInicial);
        param.put(IadyprocontratoControladorEnum.PARAM5.getValue(),
                        numeroInicial);

        listaNumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroCons);

    }

    public void oprimircmdPdf() {
        archivoDescarga = null;

        generarReporteAdyContratos(ReportesBean.FORMATOS.PDF);

    }

    public void oprimircmbExcel() {
        archivoDescarga = null;
        generarReporteAdyContratos(ReportesBean.FORMATOS.EXCEL97);

    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = retornoString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        tipoContrato = retornoString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        numeroInicial = null;
        numeroFinal = null;
        cargarListaNumeroInicial();
    }

    public void seleccionarFilaNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroInicial = retornoString(registroAux, numeroCons);
        numeroFinal = null;
        cargarListaNumeroFinal();
    }

    public void seleccionarFilaNumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFinal = retornoString(registroAux, numeroCons);
    }

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public RegistroDataModelImpl getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(
        RegistroDataModelImpl listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public RegistroDataModelImpl getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(RegistroDataModelImpl listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    private void generarReporteAdyContratos(FORMATOS formato) {
        try {
            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("claseorden", tipoContratoInicial);
            reemplazar.put("numeroinicial", numeroInicial);
            reemplazar.put("numerofinal", numeroFinal);

            String strSql = Reporteador.resuelveConsulta(
                            "000384IADYPROContratos",
                            Integer.parseInt(moduloContratos), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_TIPOCONTRATOINICIAL", tipoContrato);
            parametros.put("PR_FIRMAORDENESDESERVICIO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            IadyprocontratoControladorEnum.PARAM0
                                                            .getValue(),
                                            SessionUtil.getModulo(),
                                            new Date(), true));

            archivoDescarga = JsfUtil.exportarStreamed("000384IADYPROContratos",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
            Logger.getLogger(IadyprocontratoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void abrirFormulario() {
        // Metodo generado de la clase BeanBase
    }

    private String retornoString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
