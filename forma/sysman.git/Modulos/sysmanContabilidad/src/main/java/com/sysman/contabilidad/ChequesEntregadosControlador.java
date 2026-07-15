package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ChequesEntregadosControladorUrlEnum;
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 25/04/2016
 *
 * @modified jguerrero
 * @version 2. 12/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class ChequesEntregadosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private boolean entregado;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private int anioPlan;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private final String codigoCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ChequesEntregadosControlador
     */
    public ChequesEntregadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CHEQUES_ENTREGADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        Date fechaActual = new Date();
        fechaFinal = fechaActual;
        fechaInicial = fechaActual;
        anioPlan = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado de la clase beanBase
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ChequesEntregadosControladorUrlEnum.URL3053
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioPlan);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ChequesEntregadosControladorUrlEnum.URL3953
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioPlan);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cambiarFechaInicial() {
        if (SysmanFunciones.validarVariableVacio(fechaInicial.toString())) {
            anioPlan = SysmanFunciones.getParteFecha(fechaInicial,
                            Calendar.YEAR);
        }
        else {
            anioPlan = 0;
        }
        cuentaFinal = null;
        cuentaInicial = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        if (!SysmanFunciones.validarVariableVacio(fechaInicial.toString())) {
            anioPlan = SysmanFunciones.getParteFecha(fechaInicial,
                            Calendar.YEAR);
        }
        else {
            anioPlan = 0;
        }
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        if (!validarFechas()) {
            return;
        }
        if (!SysmanFunciones.validarVariableVacio(fechaInicial.toString())) {
            anioPlan = SysmanFunciones.getParteFecha(fechaInicial,
                            Calendar.YEAR);
        }
        else {
            anioPlan = 0;
        }
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(codigoCons).toString();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    private void generarInforme(FORMATOS formato) {
        try {
            String nombreReporte;
            Map<String, Object> parametros = new HashMap<>();
            if (entregado) {
                nombreReporte = "000670LisChequesNoEntregados_1";
                parametros.put("PR_TITULO", idioma.getString("TB_TB844"));
            }
            else {
                nombreReporte = "000670LisChequesNoEntregados";
                parametros.put("PR_TITULO",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "TITULO INFORME LISTADO DE CHEQUES NO ENTREGADOS",
                                                modulo, new Date(), true));
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("anio", anioPlan);
            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000670LisChequesNoEntregados", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | JRException | IOException
                        | SystemException | SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
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

    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta("TB_TB528");
            rta = false;
        }
        return rta;
    }

}
