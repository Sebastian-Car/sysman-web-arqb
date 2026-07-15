package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.IcontratacionControladorEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 19/01/2016
 *
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 08/09/2017
 */
@ManagedBean
@ViewScoped
public class IcontratacionControlador extends BeanBaseModal
{

    private final String modulo;
    private boolean especial;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of IcontratacionControlador
     */
    public IcontratacionControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.ICONTRATACION_CONTROLADOR.getCodigo();
        modulo = SessionUtil.getModulo();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(IcontratacionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init()
    {
        fechaInicial = Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN);
        fechaFinal = Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN);
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void generarInforme(ReportesBean.FORMATOS formatos)
    {
        archivoDescarga = null;
        String parReporte = "";
        try
        {

            HashMap<String, Object> remplazar = new HashMap<>();
            remplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));

            HashMap<String, Object> parametros = new HashMap<>();
            if (especial)
            {
                parReporte = IcontratacionControladorEnum.REPORTE000466.getValue();
            }
            else
            {
                parReporte = IcontratacionControladorEnum.REPORTE000465.getValue();

            }

            parametros.put(IcontratacionControladorEnum.PR_NOMBRECOMPANIA.getValue(),
                            SysmanFunciones.concatenar(SessionUtil.getCompaniaIngreso().getNombre(), " - ",
                                            SessionUtil.getCompaniaIngreso().getSigla()));
            parametros.put(IcontratacionControladorEnum.PR_FECHAS.getValue(),
                            SysmanFunciones.concatenar(SysmanFunciones.convertirAFechaCadena(fechaInicial), " a ",
                                            SysmanFunciones.convertirAFechaCadena(fechaFinal)));

            Reporteador.resuelveConsulta(IcontratacionControladorEnum.REPORTE000465.getValue(), Integer.parseInt(modulo), remplazar,
                            parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ", parReporte));
            Logger.getLogger(IcontratacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (SysmanException | JRException | ParseException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(IcontratacionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean getEspecial()
    {
        return especial;
    }

    public void setEspecial(boolean especial)
    {
        this.especial = especial;
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

}
