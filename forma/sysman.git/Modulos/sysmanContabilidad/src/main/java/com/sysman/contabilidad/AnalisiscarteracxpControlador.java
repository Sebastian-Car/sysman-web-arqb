package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.AnalisiscarteracxpControladorEnum;
import com.sysman.contabilidad.enums.AnalisiscarteracxpControladorUrlEnum;
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
 * @author NGOMEZ
 * @version 1, 05/05/2016
 * 
 * @version 2.0, 06/04/2017, <strong>pespitia</strong>:<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico refactoring.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class AnalisiscarteracxpControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * AnalisiscarteracxpControladorEnum.NIT
     */
    private final String cNit;

    private String porVencimiento;
    private String soloAprobados;
    private String resumido;
    private String terceroInicial;
    private String terceroFinal;
    private Date fechaCorte;
    private String nombreTerceroInicial;
    private String nombreTerceroFinal;
    private Boolean resumidoVisible;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;

    /**
     * Creates a new instance of AnalisiscarteracxpControlador
     */
    public AnalisiscarteracxpControlador() {
        super();

        compania = SessionUtil.getCompania();

        cNit = AnalisiscarteracxpControladorEnum.NIT.getValue();

        try {
            // 689
            numFormulario = GeneralCodigoFormaEnum.ANALISISCARTERACXP_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AnalisiscarteracxpControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTerceroInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaCorte = new Date();
        resumidoVisible = false;
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisiscarteracxpControladorUrlEnum.URL2929
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cNit);
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisiscarteracxpControladorUrlEnum.URL3462
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AnalisiscarteracxpControladorEnum.NITINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, cNit);
    }

    public void oprimirImprimir() {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        genInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;

        // Antes de generar el informe se ejecuta la rutina:
        // CrearCxPaFecha
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaCorte",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("porVencimiento", "true".equals(porVencimiento)
                ? "FECHA_VCN_DOC" : "FECHA");
            reemplazar.put("soloAprobados",
                            "true".equals(soloAprobados)
                                ? "AND COMPROBANTE_CNT.PROGRAMADO NOT IN (0)"
                                : "");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_ANALISISCARTERACXP_TERCEROINICIAL",
                            nombreTerceroInicial);
            parametros.put("PR_FORMS_ANALISISCARTERACXP_TERCEROFINAL",
                            nombreTerceroFinal);
            parametros.put("PR_FORMS_ANALISISCARTERACXP_FECHACORTE",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));

            String reporte;

            if ("true".equals(soloAprobados)) {
                if ("true".equals(resumido)) {
                    reporte = "000751AnaslisisCarteraCXPAprobadosResumido";
                }
                else {
                    reporte = "000752AnalisisCarteraCXP";
                }
                Reporteador.resuelveConsulta(
                                reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
            }
            else {
                reporte = "000752AnalisisCarteraCXP";

                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
            }

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = asignarValorCampo(registroAux, cNit);
        nombreTerceroInicial = asignarValorCampo(registroAux, "NOMBRE");
        terceroFinal = null;
        nombreTerceroFinal = null;
        cargarListaTerceroFinal();
    }

    /**
     * Verifica que el registro <code>reg</code> tenga una coleccion
     * de campos que no sea nula.
     * 
     * @param reg
     * @param campo
     * El campo a evaluar en la coleccion.
     * @return El valor del campo segun la coleccion.
     */
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : reg.getCampos().get(campo).toString();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = asignarValorCampo(registroAux, cNit);
        nombreTerceroFinal = asignarValorCampo(registroAux, "NOMBRE");
    }

    public void cambiarSoloAprobados() {
        resumidoVisible = !resumidoVisible;
    }

    public String getPorVencimiento() {
        return porVencimiento;
    }

    public void setPorVencimiento(String porVencimiento) {
        this.porVencimiento = porVencimiento;
    }

    public String getSoloAprobados() {
        return soloAprobados;
    }

    public void setSoloAprobados(String soloAprobados) {
        this.soloAprobados = soloAprobados;
    }

    public String getResumido() {
        return resumido;
    }

    public void setResumido(String resumido) {
        this.resumido = resumido;
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

    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public String getCompania() {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreTerceroInicial() {
        return nombreTerceroInicial;
    }

    public void setNombreTerceroInicial(String nombreTerceroInicial) {
        this.nombreTerceroInicial = nombreTerceroInicial;
    }

    public String getNombreTerceroFinal() {
        return nombreTerceroFinal;
    }

    public void setNombreTerceroFinal(String nombreTerceroFinal) {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public Boolean getResumidoVisible() {
        return resumidoVisible;
    }

    public void setResumidoVisible(Boolean resumidoVisible) {
        this.resumidoVisible = resumidoVisible;
    }

}
