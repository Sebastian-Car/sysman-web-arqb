package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.precontractual.enums.LisestpreviosControladorEnum;
import com.sysman.precontractual.enums.LisestpreviosControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 03/12/2015
 *
 * @version2, 02/06/2017 sdaza - incluir ruta para validar reporte
 * faltante
 * 
 * @modified jguerrero
 * @version 2. 30/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class LisestpreviosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el valor COD_T_CONTRATO
     */
    private final String cCodTContrato;

    private String modalidadInicial;
    private String modalidadFinal;
    private String codModaInicial;
    private String codModaFinal;

    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /** Lista que contiene los items del combo Modalidad Inicial */
    private RegistroDataModelImpl listaModalidadInicial;

    /** Lista que contiene los items del combo Modalidad Final */
    private RegistroDataModelImpl listaModalidadFinal;

    /**
     * Creates a new instance of LisestpreviosControlador
     */
    public LisestpreviosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getCompania();
        cCodTContrato = LisestpreviosControladorEnum.COD_T_CONTRATO.getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.LISESTPREVIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LisestpreviosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListaModalidadInicial();

        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /** Carga la lista listaModalidadInicial con los items */
    public void cargarListaModalidadInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisestpreviosControladorUrlEnum.URL3176
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaModalidadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodTContrato);

    }

    /** Carga los items en la lista listaModalidadFinal */
    public void cargarListaModalidadFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisestpreviosControladorUrlEnum.URL3775
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisestpreviosControladorEnum.CONTRATO.getValue(),
                        codModaInicial);

        listaModalidadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodTContrato);
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModalidadInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modalidadInicial = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        codModaInicial = retornarString(registroAux, cCodTContrato);

        modalidadFinal = null;
        codModaFinal = null;
        cargarListaModalidadFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModalidadFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modalidadFinal = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        codModaFinal = retornarString(registroAux, cCodTContrato);
    }
    // </METODOS_COMBOS_GRANDES>

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            String parReporte = LisestpreviosControladorEnum.REPORTE416
                            .getValue();

            HashMap<String, Object> remplazar = new HashMap<>();

            remplazar.put(LisestpreviosControladorEnum.MODALIDADINICIALLOWER
                            .getValue(), codModaInicial);
            remplazar.put(LisestpreviosControladorEnum.MODALIDADFINALLOWER
                            .getValue(), codModaFinal);
            remplazar.put(LisestpreviosControladorEnum.FECHAINICIALLOWER
                            .getValue(),
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put(LisestpreviosControladorEnum.FECHAFINALLOWER
                            .getValue(),
                            SysmanFunciones.formatearFecha(fechaFinal));

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(LisestpreviosControladorEnum.PR_STRSQL.getValue(),
                            strsql);

            archivoDescarga = JsfUtil.exportarStreamed(
                            LisestpreviosControladorEnum.REPORTE416.getValue(),
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getModalidadInicial() {
        return modalidadInicial;
    }

    public void setModalidadInicial(String modalidadInicial) {
        this.modalidadInicial = modalidadInicial;
    }

    public String getModalidadFinal() {
        return modalidadFinal;
    }

    public void setModalidadFinal(String modalidadFinal) {
        this.modalidadFinal = modalidadFinal;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaModalidadInicial() {
        return listaModalidadInicial;
    }

    public void setListaModalidadInicial(
        RegistroDataModelImpl listaModalidadInicial) {
        this.listaModalidadInicial = listaModalidadInicial;
    }

    public RegistroDataModelImpl getListaModalidadFinal() {
        return listaModalidadFinal;
    }

    public void setListaModalidadFinal(
        RegistroDataModelImpl listaModalidadFinal) {
        this.listaModalidadFinal = listaModalidadFinal;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
