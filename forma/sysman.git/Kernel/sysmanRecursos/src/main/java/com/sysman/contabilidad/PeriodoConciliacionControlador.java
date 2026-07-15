package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.PeriodoConciliacionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodrigueza
 * @version 1, 09/03/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 05/04/2017
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class PeriodoConciliacionControlador extends BeanBaseModal {

    private String compania;
    private String modulo;
    private String nombreCompania;
    private int ano;
    private int mes;
    private String estadoAnio;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PeriodoConciliacionControlador
     */
    public PeriodoConciliacionControlador() {
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_CONCILIACION_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH) + 1;
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        cargarListaAno();
        cargarListaMes();
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoConciliacionControladorUrlEnum.URL2632
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), Integer.toString(ano));

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoConciliacionControladorUrlEnum.URL2937
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        if (esAnoCerrado()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB31"));
            return;
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);
        parametros.put("mes", mes);
        Direccionador direccionador = new Direccionador();
        // conciliacionbancaria.sysman
        direccionador.setNumForm(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.CONCILIACION_BANCARIA_CONTROLADOR
                                                        .getCodigo()));
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);
    }

    public void oprimirCancelar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void oprimirPresentar() {
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
    }
    
    public void oprimirExcel(){
    	 archivoDescarga = null;
         generarReporte(ReportesBean.FORMATOS.EXCEL97);
    }

    private void generarReporte(ReportesBean.FORMATOS formato) {
        String reporte = "000556ICuentasConciliadas";
        try {
        	Calendar fecha = Calendar.getInstance();
    		fecha.set(ano, mes - 1, 1);
    		Date fechaInicial = fecha.getTime();
    		Date fechaFinal = null;
    		try
    		{
    			fechaFinal = SysmanFunciones.ultimoDiaDate(fechaInicial);
    		}
    		catch (ParseException ex)
    		{
    			JsfUtil.agregarMensajeError(ex.getMessage());
                logger.error(ex.getMessage(), ex);
    		}
    		String fechaArmada = SysmanFunciones.formatearFecha(fechaFinal);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mes", Integer.toString(mes));
            reemplazar.put("mesAnt", mes - 1);
            reemplazar.put("ano", Integer.toString(ano));
            reemplazar.put("fechaFin", fechaArmada);
            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();

            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void cambiarAno() {
        cargarListaMes();
    }

    /**
     *
     * @return <code>true</code> si el a�o est� cerrado.
     */
    private boolean esAnoCerrado() {

        try {
            estadoAnio = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania,
                            ano, Integer.valueOf(modulo),
                            2);
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return "C".equals(estadoAnio);

    }

    /*
     * Getters and Setters
     */
    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getNombreCompania() {
        return nombreCompania;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

}
