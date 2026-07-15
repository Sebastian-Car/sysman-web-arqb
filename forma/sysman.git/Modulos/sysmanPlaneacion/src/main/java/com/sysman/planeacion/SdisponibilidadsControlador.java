package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.SdisponibilidadsControladorEnum;
import com.sysman.planeacion.enums.SdisponibilidadsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
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
 * @author ngomez
 * @version 1, 15/12/2015
 * @modified jguerrero
 * @version 2. 08/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class SdisponibilidadsControlador extends BeanBaseModal {

    private final String compania;

    private String numero;
    private String numeroDependenciaNombre;
    private String responsableNombre;
    private String valorEstimado;
    private String nsolicitud;
    private String concepto;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaNumero;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of SdisponibilidadsControlador
     */
    public SdisponibilidadsControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.SDISPONIBILIDADS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SdisponibilidadsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init() {
        cargarListaNumero();
        abrirFormulario();
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public void cargarListaNumero() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SdisponibilidadsControladorUrlEnum.URL2851
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }

    public void oprimirMemo() {
        // <CODIGO_DESARROLLADO>
        try {

            String criterio = SysmanFunciones.concatenar(
                            GeneralParameterEnum.COMPANIA.getName(), "=''",
                            compania,
                            "''");
            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            SdisponibilidadsControladorEnum.ORDENDESUMINISTRO
                                            .getValue(),
                            criterio,
                            SdisponibilidadsControladorEnum.NSOLICITUDDISPON
                                            .getValue(),
                            "0");

            if (consecutivo == 0) {

                String aux3;
                aux3 = ejbSysmanUtil.consultarParametro(compania,
                                SdisponibilidadsControladorEnum.PARAMETRO_NUMEROSOLDIS
                                                .getValue(),
                                SessionUtil.getModulo(),
                                new Date(), true);

                if (aux3 == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3540"));
                    nsolicitud = "0";
                }
                else {
                    nsolicitud = aux3;
                }

            }
            else {
                nsolicitud = String.valueOf(consecutivo);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        actualizarCampos();
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        actualizarCampos();
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        try {
            archivoDescarga = null;

            String aux3 = ejbSysmanUtil.consultarParametro(compania,
                            SdisponibilidadsControladorEnum.FORMATO_SOLDIS
                                            .getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

            if (aux3 == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3541"));
            }
            else if (aux3.equals(
                            SdisponibilidadsControladorEnum.SDP.getValue())) {

                HashMap<String, Object> parametros = new HashMap<>();
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(SdisponibilidadsControladorEnum.NUMERO_LOWER
                                .getValue(), numero);
                reemplazar.put(SdisponibilidadsControladorEnum.VALORESTIMADO_LOWER
                                .getValue(), valorEstimado);
                reemplazar.put(SdisponibilidadsControladorEnum.NSOLICITUD_LOWER
                                .getValue(), nsolicitud);
                reemplazar.put(SdisponibilidadsControladorEnum.NUMERODEPENDENCIANOMBRE_LOWER
                                .getValue(),
                                numeroDependenciaNombre);
                reemplazar.put(SdisponibilidadsControladorEnum.CONCEPTO_LOWER
                                .getValue(), concepto);
                String strSql = Reporteador.resuelveConsulta(
                                SdisponibilidadsControladorEnum.REPORTE446
                                                .getValue(),
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar);
                parametros.put(SdisponibilidadsControladorEnum.PR_STRSQL
                                .getValue(), strSql);
                parametros.put(SdisponibilidadsControladorEnum.PR_NOMBRECOMPANIA
                                .getValue(), SessionUtil
                                                .getCompaniaIngreso()
                                                .getNombre());
                archivoDescarga = JsfUtil.exportarStreamed(
                                SdisponibilidadsControladorEnum.REPORTE446
                                                .getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = retornarString(registroAux,
                        GeneralParameterEnum.NUMERO.getName());
        numeroDependenciaNombre = retornarString(registroAux,
                        SdisponibilidadsControladorEnum.DEPENDENCIANOMBRE
                                        .getValue());

        responsableNombre = retornarString(registroAux,
                        SdisponibilidadsControladorEnum.NOMBRERESPONSABLE
                                        .getValue());

        valorEstimado = retornarString(registroAux,
                        SdisponibilidadsControladorEnum.VALORESTIMADO
                                        .getValue());
        nsolicitud = retornarString(registroAux,
                        SdisponibilidadsControladorEnum.NSOLICITUDDISPON
                                        .getValue());
        concepto = retornarString(registroAux,
                        SdisponibilidadsControladorEnum.CONCEPTO.getValue());
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNumeroDependenciaNombre() {
        return numeroDependenciaNombre;
    }

    public void setNumeroDependenciaNombre(String numeroDependenciaNombre) {
        this.numeroDependenciaNombre = numeroDependenciaNombre;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public String getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(String valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getNsolicitud() {
        return nsolicitud;
    }

    public void setNsolicitud(String nsolicitud) {
        this.nsolicitud = nsolicitud;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    @Override
    public void abrirFormulario() {
        // Metodo herado del bean base
    }

    public void actualizarCampos() {
        try {
            if (SysmanFunciones.validarVariableVacio(valorEstimado)) {
                valorEstimado = "0";
            }
            if (SysmanFunciones.validarVariableVacio(nsolicitud)) {
                nsolicitud = "0";
            }

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SdisponibilidadsControladorUrlEnum.URL3340
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(SdisponibilidadsControladorEnum.VALORESTIMADO.getValue(),
                            valorEstimado);
            fields.put(SdisponibilidadsControladorEnum.NSOLICITUDDISPON
                            .getValue(), nsolicitud);
            fields.put(GeneralParameterEnum.CONCEPTO.getName(), concepto);
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.NUMERO.getName(), numero);
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

}
