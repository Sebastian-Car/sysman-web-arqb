package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ResumeningresosControladorEnum;
import com.sysman.contabilidad.enums.ResumeningresosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
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
 * @author NGOMEZ
 * @version 1, 12/04/2016
 * @author yrojas
 * @version 2, 07/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 * @author yrojas
 * @version 3, 20/04/2017 Se cambiaron los llamados de Acciones por
 * las invocaciones de los ejb.
 * 
 * @version 4.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 */
@ManagedBean
@ViewScoped
public class ResumeningresosControlador extends BeanBaseModal {
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private final String compania;
    private String conFirmas;
    private String presupuestal;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private int anio;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    /**
     * Creates a new instance of ResumeningresosControlador
     */
    public ResumeningresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 626
            numFormulario = GeneralCodigoFormaEnum.RESUMENINGRESOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumeningresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumeningresosControladorUrlEnum.URL2797
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumeningresosControladorUrlEnum.URL3555
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(ResumeningresosControladorEnum.PARAM0.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirImprimir() {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        genInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void cambiarFechaInicial() {
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String desdeAux = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);

            String hastaAux = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            reemplazar.put("fechaInicial", desdeAux);
            reemplazar.put("fechaFinal", hastaAux);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_CARGO_TESORERO", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO TESORERO",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "")
                            .toString());

            parametros.put("PR_NOMBRE_TESORERO", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE TESORERO",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "")
                            .toString());
            parametros.put("PR_FORMS_RESUMENINGRESOS_FECHAINICIAL", desdeAux);
            parametros.put("PR_FORMS_RESUMENINGRESOS_FECHAFINAL", hastaAux);
            parametros.put("PR_FORMS_RESUMENINGRESOS_CUENTAINICIAL",
                            cuentaInicial);
            parametros.put("PR_FORMS_RESUMENINGRESOS_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_FIRMAS",
                            "true".equals(conFirmas) ? true : false);
            String reporte = "true".equals(presupuestal)
                ? "000625ResumenIngresosC" : "000626ResumenIngresosflo";
            // Los dos informes usan la misma consulta
            // 000625ResumenIngresosC
            String strSql = Reporteador.resuelveConsulta(
                            "000625ResumenIngresosC",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            String strSql1 = Reporteador.resuelveConsulta(
                    "002800SubTotalPptal",
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_SQL_TOTALPPTAL", strSql1);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getConFirmas() {
        return conFirmas;
    }

    public void setConFirmas(String conFirmas) {
        this.conFirmas = conFirmas;
    }

    public String getPresupuestal() {
        return presupuestal;
    }

    public void setPresupuestal(String presupuestal) {
        this.presupuestal = presupuestal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getCompania() {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
