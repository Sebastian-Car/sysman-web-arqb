package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ResumenegresosControladorEnum;
import com.sysman.contabilidad.enums.ResumenegresosControladorUrlEnum;
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
 * @author NGOMEZ
 * @version 1, 12/04/2016
 *
 * @version 2, 10/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class ResumenegresosControlador extends BeanBaseModal {
    private final String compania;
    private final String consCodigo;
    private String preparar;
    private String contraPartidas;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private int anio;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    /**
     * Creates a new instance of ResumenegresosControlador
     */
    public ResumenegresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMENEGRESOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenegresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        fechaInicial = fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cargarListaCuentaInicial();
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenegresosControladorUrlEnum.URL2693
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenegresosControladorUrlEnum.URL3422
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ResumenegresosControladorEnum.PARAM0.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
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
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), " ")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), " ")
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
            reemplazar.put("clasesContables", "true".equals(contraPartidas)
                ? "'E','G','A','D'" : "'E','G'");
            reemplazar.put("claseCuenta", "true".equals(contraPartidas)
                ? "AND V_PLAN_CONTABLE.CLASECUENTA NOT IN ('J','B')" : "");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_RESUMENEGRESOS_FECHAINICIAL", desdeAux);
            parametros.put("PR_FORMS_RESUMENEGRESOS_FECHAFINAL", hastaAux);
            parametros.put("PR_FORMS_RESUMENEGRESOS_CUENTAINICIAL",
                            cuentaInicial);
            parametros.put("PR_FORMS_RESUMENEGRESOS_CUENTAFINAL", cuentaFinal);
            String reporte = "000627I2ResumenEgresos";
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getPreparar() {
        return preparar;
    }

    public void setPreparar(String preparar) {
        this.preparar = preparar;
    }

    public String getContraPartidas() {
        return contraPartidas;
    }

    public void setContraPartidas(String contraPartidas) {
        this.contraPartidas = contraPartidas;
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

}
