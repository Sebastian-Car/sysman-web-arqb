package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.NnovedadesacontratosControladorUrlEnum;
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
import java.text.ParseException;
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
 * @version 1, 23/11/2015
 *
 * @version 2, 10/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 */
@ManagedBean
@ViewScoped
public class NnovedadesacontratosControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloContratos;
    private String tipoContratoInicial;
    private String tipon;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private RegistroDataModelImpl listaTipon;
    private static final String CODIGO = GeneralParameterEnum.CODIGO.getName();
    private static final String FORMATOFECHA = "dd/MM/yyyy";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    private String firmaOrdenes;

    /**
     * Creates a new instance of NnovedadesacontratosControlador
     */
    public NnovedadesacontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.NNOVEDADESACONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(NnovedadesacontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListatipon();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {
        // 73010
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NnovedadesacontratosControladorUrlEnum.URL3116
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListatipon() {
        // 73029
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NnovedadesacontratosControladorUrlEnum.URL3887
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipon = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void oprimircmdPantalla() {
        if ((tipoContratoInicial != null) && (tipon != null)
            && (fechaFinal != null)
            && (fechaInicial != null)) {
            if (!validarFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB574"));
                return;
            }
            if ("ACL".equals(tipon)) {
                generarReporteNovedadesContratosAcl(ReportesBean.FORMATOS.PDF);
            }
            else {
                generarReporteNovedadesContratosAds(ReportesBean.FORMATOS.PDF);
            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel() {
        if ((tipoContratoInicial != null) && (tipon != null)
            && (fechaFinal != null)
            && (fechaInicial != null)) {
            if (!validarFechas(fechaInicial, fechaFinal)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB574"));
                return;
            }
            if ("ACL".equals(tipon)) {
                generarReporteNovedadesContratosAcl(
                                FORMATOS.EXCEL97);
            }
            else {
                generarReporteNovedadesContratosAds(
                                FORMATOS.EXCEL97);
            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();

    }

    public void seleccionarFilaTipon(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipon = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
    }

    public boolean validarFechas(Date fechaInicial, Date fechaFinal) {
        return fechaInicial.compareTo(fechaFinal) <= 0;
    }

    private void generarReporteNovedadesContratosAcl(FORMATOS formato) {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoContrato", tipoContratoInicial);
            reemplazar.put("tipon", tipon);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(
                            "000390NNOVEDADESCONTRATOSACL",
                            Integer.parseInt(moduloContratos), reemplazar,
                            parametros);
            // MANEJA LOS PARAMETROS DEL REPORTE
            parametros.put("PR_FIRMA_ORDENES_DE_SERVICIO", firmaOrdenes);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, FORMATOFECHA));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, FORMATOFECHA));

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000390NNOVEDADESCONTRATOSACL", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generarReporteNovedadesContratosAds(FORMATOS formato) {
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoContrato", tipoContratoInicial);
            reemplazar.put("tipon", tipon);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(
                            "000392NNOVEDADESCONTRATOSADS",
                            Integer.parseInt(moduloContratos), reemplazar,
                            parametros);
            // MANEJA LOS PARAMETROS DEL REPORTE
            parametros.put("PR_FIRMA_ORDENES_DE_SERVICIO", firmaOrdenes);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, FORMATOFECHA));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, FORMATOFECHA));

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000392NNOVEDADESCONTRATOSADS", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = new Date();
        try {
            firmaOrdenes = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA ORDENES DE SERVICIO",
                            moduloContratos, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipon() {
        return tipon;
    }

    public void setTipon(String tipon) {
        this.tipon = tipon;
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

    public RegistroDataModelImpl getListaTipon() {
        return listaTipon;
    }

    public void setListaTipon(RegistroDataModelImpl listaTipon) {
        this.listaTipon = listaTipon;
    }

}
