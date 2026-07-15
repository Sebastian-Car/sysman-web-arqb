package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.ContratodependenciaControladorEnum;
import com.sysman.contratos.enums.ContratodependenciaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
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
 * @author dcastro
 * @version 1, 08/10/2015
 * 
 * @version 2, 08/08/2017, <strong>pespitia</strong>:<br>
 * Refactoring.
 */
@ManagedBean
@ViewScoped
public class ContratodependenciaControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que contiene el codigo del modulo
     * desde el cual el usuario inicio sesion.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloj el valor de la cadena
     * <code>CODIGO</code>
     */
    private final String cCodigo;

    private String dependenciaInicial;
    private String dependenciaFinal;

    /**
     * Atributo que controla el valor seleccionado en el combo:
     * <code>Estado</code>
     */
    private String estado;

    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    private RegistroDataModelImpl listaDepInicial;
    private RegistroDataModelImpl listaDepFinal;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private RegistroDataModelImpl listaTipoContratoFinal;

    /**
     * Creates a new instance of ContratodependenciaControlador
     */
    public ContratodependenciaControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();

        try {
            // 267
            numFormulario = GeneralCodigoFormaEnum.CONTRATODEPENDENCIA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ContratodependenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fechaInicial = fechaFinal = new Date();

        cargarListaDepInicial();
        cargarListaTipoContratoInicial();
        abrirFormulario();
    }

    public void cargarListaDepInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ContratodependenciaControladorUrlEnum.URL3308
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDepInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaDepFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ContratodependenciaControladorUrlEnum.URL4028
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ContratodependenciaControladorEnum.DEPENDENCIA.getValue(),
                        dependenciaInicial);

        listaDepFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTipoContratoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ContratodependenciaControladorUrlEnum.URL4795
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
                                        ContratodependenciaControladorUrlEnum.URL5401
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ContratodependenciaControladorEnum.CODIGOINI.getValue(),
                        tipoContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimircmdPdf() {
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL97);
    }

    public void seleccionarFilaDepInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        dependenciaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        dependenciaFinal = null;

        cargarListaDepFinal();
    }

    public void seleccionarFilaDepFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        dependenciaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoContratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        tipoContratoFinal = null;

        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoContratoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "T".equals(estado) ? "000267CEstadoContratogenSOG3"
            : "000266CEstadoContratogenSOG2";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));

        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));

        reemplazar.put("dependenciaInicial",
                        SysmanFunciones.colocarComillas(dependenciaInicial));

        reemplazar.put("dependenciaFinal",
                        SysmanFunciones.colocarComillas(dependenciaFinal));

        reemplazar.put("tipoContratoInicial",
                        SysmanFunciones.colocarComillas(tipoContratoInicial));

        reemplazar.put("tipoContratoFinal",
                        SysmanFunciones.colocarComillas(tipoContratoFinal));

        reemplazar.put("estado", SysmanFunciones.colocarComillas(estado));

        // </REEMPLAZAR VARIABLES EN CONSULTA>

        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_DEPINICIAL", dependenciaInicial);
            parametros.put("PR_DEPFINAL", dependenciaFinal);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            String sql = parametros.get("PR_STRSQL").toString();

            if (service.getConteoConsulta(sql) == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
                return;
            }

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaDepInicial() {
        return listaDepInicial;
    }

    public void setListaDepInicial(RegistroDataModelImpl listaDepInicial) {
        this.listaDepInicial = listaDepInicial;
    }

    public RegistroDataModelImpl getListaDepFinal() {
        return listaDepFinal;
    }

    public void setListaDepFinal(RegistroDataModelImpl listaDepFinal) {
        this.listaDepFinal = listaDepFinal;
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
}
