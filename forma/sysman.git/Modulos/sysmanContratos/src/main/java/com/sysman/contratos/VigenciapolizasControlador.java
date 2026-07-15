package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.VigenciapolizasControladorEnum;
import com.sysman.contratos.enums.VigenciapolizasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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
 * @version 1, 01/10/2015
 *
 * @author spina
 * @version 2, 15/08/2017 - se refactoriza para dss, depuracion y ejb
 */
@ManagedBean
@ViewScoped
public class VigenciapolizasControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String tipoInicial;
    private String tipoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String tipoIni;
    private String tipoFin;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of VigenciapolizasControlador
     */
    public VigenciapolizasControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.VIGENCIAPOLIZAS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(VigenciapolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init()
    {
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void cargarListaTipoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VigenciapolizasControladorUrlEnum.URL3320
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VigenciapolizasControladorUrlEnum.URL3321
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(VigenciapolizasControladorEnum.CODIGOINI.getValue(), tipoIni);
        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirPDF()
    {
        archivoDescarga = null;
        if (tipoIni != null && tipoFinal != null && fechaInicial != null
            && fechaFinal != null)
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            try
            {
                generarReporte(ReportesBean.FORMATOS.PDF);
            }
            catch (SQLException ex)
            {
                Logger.getLogger(VigenciapolizasControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }

        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimirEXCEL()
    {
        archivoDescarga = null;
        if (tipoIni != null && tipoFinal != null && fechaInicial != null
            && fechaFinal != null)
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            try
            {
                generarReporte(ReportesBean.FORMATOS.EXCEL97);
            }
            catch (SQLException ex)
            {
                Logger.getLogger(VigenciapolizasControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public boolean validarFechas(Date fechaInicial, Date fechaFinal)
    {
        return fechaInicial.compareTo(fechaFinal) <= 0;
    }

    @Override
    public void abrirFormulario()
    {
        // HEREDADO DEL BEAN BASE
    }

    private void generarReporte(FORMATOS formatos) throws SQLException
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoIni", tipoIni);
            reemplazar.put("tipoFin", tipoFin);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            tipoIni = SysmanFunciones.nvl(tipoIni, "A").toString();
            tipoFin = SysmanFunciones.nvl(tipoFin, "ZZZ").toString();
            parametros.put("PR_TIPOINICIAL", tipoIni);
            parametros.put("PR_TIPOFINAL", tipoFin);

            Reporteador.resuelveConsulta("000253InformeVigenciaPolizas",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000253InformeVigenciaPolizas", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (SysmanException | JRException | IOException
                        | ParseException ex)
        {
            Logger.getLogger(VigenciapolizasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void seleccionarFilaTipoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoIni = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        tipoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        tipoFin = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    public String getTipoInicial()
    {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial)
    {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal()
    {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal)
    {
        this.tipoFinal = tipoFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public RegistroDataModelImpl getListaTipoInicial()
    {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial)
    {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal()
    {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal)
    {
        this.listaTipoFinal = listaTipoFinal;
    }

}
