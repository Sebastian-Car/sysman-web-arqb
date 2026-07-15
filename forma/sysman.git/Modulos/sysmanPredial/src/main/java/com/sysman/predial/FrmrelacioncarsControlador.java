package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
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
 * @version 1, 03/06/2016
 * 
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario y se ajusta
 * conexion
 * 
 * @author ybecerra
 * @version 3, 06/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class FrmrelacioncarsControlador extends BeanBaseModal
{

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmrelacioncarsControlador
     */
    public FrmrelacioncarsControlador()
    {
        super();
        modulo = SessionUtil.getModulo();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMRELACIONCARS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmrelacioncarsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String parReporte = "";
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NITCOMPANIA", idioma.getString("TG_NIT4")
                + SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_FECHAS",
                            idioma.getString("TG_ENTRE").toUpperCase()
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial)
                                + " Y "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));

            if ("8912800003".equals(SessionUtil.getCompaniaIngreso().getNit()))
            {
                parReporte = "000873INFRELACIONCARPTO";

            }
            else
            {
                parReporte = "000871INFRELACIONCAR";

            }
            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", parReporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarfechafinal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLAD>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
