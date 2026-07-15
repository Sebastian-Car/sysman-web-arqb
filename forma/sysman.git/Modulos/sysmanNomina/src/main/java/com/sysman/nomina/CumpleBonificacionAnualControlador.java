package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.CumpleBonificacionAnualControladorEnum;
import com.sysman.nomina.enums.CumpleBonificacionAnualControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 08/09/2015
 *
 * @author spina
 * @version 2, 05/09/2017 - se refactoriza para dss, depuracion sonar
 */
@ManagedBean
@ViewScoped
public class CumpleBonificacionAnualControlador extends BeanBaseModal
{

    private final String compania;
    private String mes1;
    private List<Registro> listaMes1;
    private String anio;
    private String proceso;
    private String mes;
    private String periodo;
    private StreamedContent archivoDescarga;
    private String tipoInforme;
    private boolean visibleMsg;

    /**
     * Creates a new instance of CumpleBonificacionAnualControlador
     */
    public CumpleBonificacionAnualControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        proceso = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                        .toString();
        mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        periodo = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CUMPLE_BONIFICACION_ANUAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(CumpleBonificacionAnualControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        mes1 = mes;
        cargarListaMes1();
        abrirFormulario();
    }

    public void cargarListaMes1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CumpleBonificacionAnualControladorEnum.PROCESO.getValue(),
                        proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        try
        {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CumpleBonificacionAnualControladorUrlEnum.URL3100
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generaInformeID(ReportesBean.FORMATOS formato)
    {
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mes1", mes1);
            reemplazar.put("anio", anio);
            Reporteador.resuelveConsulta("000015ListadoBAN",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            // MANEJO DE PARAMETROS DEL REPORTE
            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes1)];
            parametros.put("PR_FORMS_CUMPLE_BONIFICACION_ANUAL_MES1",
                            nombreMes.toUpperCase());
            archivoDescarga = JsfUtil.exportarStreamed("000015ListadoBAN",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            Logger.getLogger(CumpleBonificacionAnualControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generaInformeCB(ReportesBean.FORMATOS formato)
    {
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mes1", mes1);
            reemplazar.put("anio", anio);
            Reporteador.resuelveConsulta("000187ListadoBANB",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            // MANEJO DE PARAMETROS DEL REPORTE
            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes1)];
            parametros.put("NOMBREMES", nombreMes.toUpperCase());
            archivoDescarga = JsfUtil.exportarStreamed("000187ListadoBANB",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            Logger.getLogger(CumpleBonificacionAnualControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void oprimirPreliminar()
    {
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        generarInforme(ReportesBean.FORMATOS.EXCEL);
    }

    public void generarInforme(FORMATOS formato)
    {
        if (mes1 == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2523"));
            return;
        }
        archivoDescarga = null;

        if (CumpleBonificacionAnualControladorEnum.CB.getValue()
                        .equals(tipoInforme))
        {
            // Cumplimiento Bonificacion
            generaInformeCB(formato);
        }
        else
        {
            // Ingreso Distrito
            generaInformeID(formato);
        }
    }

    public void cambiarTipo()
    {
        visibleMsg = CumpleBonificacionAnualControladorEnum.CB.getValue()
                        .equals(tipoInforme);
    }

    public String getMes1()
    {
        return mes1;
    }

    public void setMes1(String mes1)
    {
        this.mes1 = mes1;
    }

    public List<Registro> getListaMes1()
    {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTipoInforme()
    {
        return tipoInforme;
    }

    public void setTipoInforme(String tipoInforme)
    {
        this.tipoInforme = tipoInforme;
    }

    public boolean isVisibleMsg()
    {
        return visibleMsg;
    }

    public void setVisibleMsg(boolean visibleMsg)
    {
        this.visibleMsg = visibleMsg;
    }

}
