package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
 * @version 1, 09/12/2015
 * 
 * @author jlramirez
 * @version 2, 05/04/2017, modificaciones segUn especificaciones de SONARLINK
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017 
 */

@ManagedBean
@ViewScoped
public class IordenesanuladasControlador extends BeanBaseModal
{

    private final String modulo;
    private final String cperiodo;
    private final String cstrsql;
    private String opcion;
    private String titulo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of IordenesanuladasControlador
     */
    public IordenesanuladasControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.IORDENESANULADAS_CONTROLADOR.getCodigo();
        modulo = SessionUtil.getModulo();
        cperiodo = "Periodo: ";
        cstrsql = "PR_STRSQL";
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(IordenesanuladasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        opcion = "1";
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        String parReporte = "";
        try
        {

            HashMap<String, Object> remplazar = new HashMap<>();

            if ("1".equals(opcion))
            {
                remplazar.put("fecha", "AND ORDENDECOMPRA.FECHA");
            }
            else
            {
                remplazar.put("fecha", "AND ORDENDECOMPRA.FECHAANULACION");
            }
            remplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            remplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            Map<String, Object> parametros = new HashMap<>();

            if ("400307".equals(SessionUtil.getMenuActual())
                || "90318".equals(SessionUtil.getMenuActual()))
            {
                String strsql = Reporteador.resuelveConsulta(
                                "000430IOrdenesAnuladasA",
                                Integer.parseInt(modulo), remplazar);
                if ("1".equals(opcion))
                {
                    parReporte = "000428IOrdenesAnuladasE";
                    parametros.put(cstrsql, strsql);

                }
                else
                {
                    parReporte = "000430IOrdenesAnuladasA";
                    parametros.put(cstrsql, strsql);
                }

            }

            if ("400313".equals(SessionUtil.getMenuActual()))
            {
                String strsql = Reporteador.resuelveConsulta(
                                "000455IOrdenesAnuladasET",
                                Integer.parseInt(modulo), remplazar);

                if ("1".equals(opcion))
                {
                    parReporte = "000455IOrdenesAnuladasET";
                    parametros.put(cstrsql, strsql);
                }
                else
                {
                    parReporte = "000456IOrdenesAnuladasAT";
                    parametros.put(cstrsql, strsql);
                }

            }

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre() + " - "
                                + SessionUtil.getCompaniaIngreso().getSigla());
            parametros.put("PR_ENCABEZADO",
                            cperiodo
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial)
                                + " a " + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));
            parametros.put("PR_PERIODO",
                            cperiodo
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial)
                                + " a " + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));
            parametros.put("PR_FECHAS",
                            cperiodo
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial)
                                + " a " + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException | ParseException ex)
        {
            String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE");
            msj = msj.replace("s$reporte$s", parReporte);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + msj);
            Logger.getLogger(IordenesanuladasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(IordenesanuladasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        switch (SessionUtil.getMenuActual())
        {
        case "90318":
        case "400307":
            titulo = idioma.getString("TB_TB3051");
            break;
        case "400313":
            titulo = idioma.getString("TB_TB3052");
            break;
        case "6040115":
            titulo = idioma.getString("TB_TB3053");
            break;
        default:
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
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

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }
}
