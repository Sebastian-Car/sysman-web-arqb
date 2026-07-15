package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.EstadocontratoControladorUrlEnum;
import com.sysman.contratos.enums.SupervisorControladorUrlEnum;
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
import java.util.List;
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
 * @version 1, 09/10/2015
 *
 * @author spina
 * @version 2, 14/08/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class SupervisorControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String tipoOpcion;
    private String terceroInicial;
    private String terceroFinal;
    private String estado;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    //private List<Registro> listaTerceroInicial;
    //private List<Registro> listaTerceroFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private static final String FORMATOFECHA = "dd/MM/yyyy";
    private static final String MSMTRANSINTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";

    /**
     * Creates a new instance of SupervisorControlador
     */
    public SupervisorControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUPERVISOR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(SupervisorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTerceroInicial();
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTerceroInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        	UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(SupervisorControladorUrlEnum.URL3321.getValue());
        	listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true, "NIT");
        	
        
    }

    public void cargarListaTerceroFinal()
    {
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        	UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(SupervisorControladorUrlEnum.URL3321.getValue());
        	listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true, "NIT");
        
    }
    
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaTerceroFinal();

    }
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    public void cambiarTerceroInicial()
    {
        cargarListaTerceroFinal();
    }

    private boolean compararNulos(Object valor1, Object valor2)
    {
        return valor1 != null && valor2 != null;
    }

    private boolean validarNoNulos()
    {
        return compararNulos(terceroFinal, terceroInicial) &&
            compararNulos(estado, fechaFinal) &&
            fechaInicial != null;
    }

    public void oprimircmdPantalla()
    {
        archivoDescarga = null;
        if (validarNoNulos())
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB49"));
                return;
            }
            if (tipoOpcion == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2124"));
            }
            else
            {
                if ("1".equals(tipoOpcion))
                {
                    generarReporteSupervisor(ReportesBean.FORMATOS.PDF);
                }
                else
                {
                    generarReporteInterventor(ReportesBean.FORMATOS.PDF);
                }
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmbExcel()
    {
        archivoDescarga = null;
        if (validarNoNulos())
        {
            if (!validarFechas(fechaInicial, fechaFinal))
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB49"));
                return;
            }
            if (tipoOpcion == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2124"));
            }
            else
            {
                if ("1".equals(tipoOpcion))
                {
                    generarReporteSupervisor(ReportesBean.FORMATOS.EXCEL97);
                }
                else
                {
                    generarReporteInterventor(ReportesBean.FORMATOS.EXCEL97);
                }
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

    private void generarReporteInterventor(FORMATOS formatos)
    {
        try
        {
            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("compania", compania);
            reemplazos.put("terceroInicial", terceroInicial);
            reemplazos.put("terceroFinal", terceroFinal);
            reemplazos.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("estado", estado);

            String strSql = Reporteador.resuelveConsulta("000268Interventor",
                            Integer.parseInt(modulo), reemplazos);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_SUBTITULO",
                            idioma.getString("TB_TB3466").replace(
                                            "s$fechaInicial$s",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial,
                                                            FORMATOFECHA))
                                            .replace("s$fechaFinal$s",
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            FORMATOFECHA)));

            archivoDescarga = JsfUtil.exportarStreamed("000268Interventor",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (ParseException | SysmanException | JRException
                        | IOException ex)
        {
            Logger.getLogger(SupervisorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones
                            .concatenar(idioma.getString(MSMTRANSINTERRUMPIDA),
                                            ex.getMessage()));
        }
    }

    private void generarReporteSupervisor(FORMATOS formatos)
    {
        try
        {
            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("compania", compania);
            reemplazos.put("terceroInicial", terceroInicial);
            reemplazos.put("terceroFinal", terceroFinal);
            reemplazos.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("estado", estado);

            String strSql = Reporteador.resuelveConsulta("000269Supervisor",
                            Integer.parseInt(modulo), reemplazos);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_SUBTITULO",

                            idioma.getString("TB_TB3466").replace(
                                            "s$fechaInicial$s",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial,
                                                            FORMATOFECHA))
                                            .replace("s$fechaFinal$s",
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            FORMATOFECHA)));

            archivoDescarga = JsfUtil.exportarStreamed("000269Supervisor",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (ParseException | SysmanException | JRException
                        | IOException ex)
        {
            Logger.getLogger(SupervisorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(MSMTRANSINTERRUMPIDA),
                            ex.getMessage()));
        }
    }

    public String getTipoOpcion()
    {
        return tipoOpcion;
    }

    public void setTipoOpcion(String tipoOpcion)
    {
        this.tipoOpcion = tipoOpcion;
    }

    public String getTerceroInicial()
    {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial)
    {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal()
    {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal)
    {
        this.terceroFinal = terceroFinal;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
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

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTerceroInicial()
    {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial)
    {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal()
    {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal)
    {
        this.listaTerceroFinal = listaTerceroFinal;
    }

}
