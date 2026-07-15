package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author acaceres
 * @version 1, 07/06/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el código: Se pasa el numero del formulario al enumerado, se eliminan conexiones y se ajustan metodos de generacion de reportes.
 *
 * @author spina
 * @version 3, 05/07/2017 - no requiere refactorizacion dss ni ejbs
 */
@ManagedBean
@ViewScoped
public class FrmpagosanterioresControlador extends BeanBaseModal
{
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private Date fechaInicio;
    private Date fechaFin;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmpagosanterioresControlador
     */
    public FrmpagosanterioresControlador()
    {
        super();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMPAGOSANTERIORES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmpagosanterioresControlador.class.getName())
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
        fechaInicio = fechaFin = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void obtenerReporte(FORMATOS formatos)
    {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", SessionUtil.getCompania());
        reemplazar.put("fechaInicio",
                        SysmanFunciones.formatearFecha(fechaInicio));
        reemplazar.put("fechaFin",
                        SysmanFunciones.formatearFecha(fechaFin));
        reemplazar.put("numeroOrden",
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta("000874PREDIALPREDACTUAPAGOANT",
                        Integer.valueOf(modulo), reemplazar, parametros);
        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000874PREDIALPREDACTUAPAGOANT", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

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

    public Date getFechaInicio()
    {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio)
    {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin()
    {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin)
    {
        this.fechaFin = fechaFin;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }
}
