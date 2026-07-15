/*-
 * FrmRecaudoControlador.java
 *
 * 1.0
 * 
 * 27/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.FrmRecaudoControladorEnum;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * *
 * 
 * @version 1.0, 27/07/2017
 * @author jcrodriguez migracion (forma, controlador y reportes) y
 * depuracion
 */
@ManagedBean
@ViewScoped
public class FrmRecaudoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * variable cadena que almacena el modulo actual
     */
    private final String modulo;
    /**
     * variable cadena que almacena el menu actual
     */
    private final String menu;
    private StreamedContent archivoDescarga;
    /**
     * variable que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * variable que almacena la fecha final
     */
    private Date fechaFinal;
    /**
     * variable que almacena el estado para volve visible un
     * componente de la vista
     */
    private boolean visibleFecha;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmRecaudoControlador
     */
    public FrmRecaudoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        menu = SessionUtil.getMenu();
        try
        {

            numFormulario = GeneralCodigoFormaEnum.FRMRECUADO_CONTROLADOR.getCodigo();
            validarPermisos();

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que llama a un parametro del sistema
     * 
     * @param nombre
     * @param indMayus
     * @return
     */
    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        fechaFinal = fechaInicial = new Date();
        String parametro = SysmanFunciones.nvlStr(getParametro("USUARIOS PAGADOS EN INFORME RECAUDOS", true), "NO");
        if ("NO".equals(parametro) && !"74080203".equals(menu) && !"74080103".equals(menu))
        {
            visibleFecha = false;
        }
        else
        {
            visibleFecha = true;
        }
    }

    private void formatoInforme(FORMATOS formato)
    {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        if ("74080103".equals(menu))
        {
            // la primera ruta
            generarInforme(formato, "001457rptRecaudosTercerizado",
                            reemplazar, parametros);
        }
        else if ("74080203".equals(menu))
        {
            generarInforme(formato, "001455RptRecaudosConvenios",
                            reemplazar, parametros);
        }
        else
        {
            // la tercera ruta
            reemplazarParametroInfUsuarioreca(reemplazar, parametros);

            generarInforme(formato, "001454InfUsuarioreca", reemplazar, parametros);
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        formatoInforme(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        formatoInforme(FORMATOS.EXCEL);
    }

    /**
     * metodo que adiciona los reemplazos y parametros para el reporte
     * 001454InfUsuarioreca
     * 
     * @param reemplazar
     * @param parametros
     */
    private void reemplazarParametroInfUsuarioreca(Map<String, Object> reemplazar, Map<String, Object> parametros)
    {
        reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);
        String parametro = SysmanFunciones.nvlStr(getParametro("USUARIOS PAGADOS EN INFORME RECAUDOS", true), "NO");
        StringBuilder filtros = new StringBuilder("");

        if (parametro.equals(FrmRecaudoControladorEnum.SI.getValue()))
        {
            filtros.append(" AND SP_HISTORIA_EXTERNA.BANCO_PAGO IS NOT NULL ");
            filtros.append(" AND SP_HISTORIA_EXTERNA.FECHA_PAGO IS NOT NULL ");
            filtros.append(" AND TRUNC (SP_HISTORIA_EXTERNA.FECHA_PAGO) BETWEEN ");
            filtros.append(SysmanFunciones.formatearFechaCadena(fechaInicial, FrmRecaudoControladorEnum.FORMATO_FECHA.getValue()));
            filtros.append(" AND ");
            filtros.append(SysmanFunciones.formatearFechaCadena(fechaFinal, FrmRecaudoControladorEnum.FORMATO_FECHA.getValue()));

        }
        parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());
        parametros.put("PR_VISIBLE", visibleFecha);
        reemplazar.put("filtros", filtros.toString());

    }

    private void generarInforme(FORMATOS formato, String reporte, Map<String, Object> reemplazar, Map<String, Object> parametros)
    {

        reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFechaCadena(fechaInicial, FrmRecaudoControladorEnum.FORMATO_FECHA.getValue()));
        reemplazar.put("fechaFinal", SysmanFunciones.formatearFechaCadena(fechaFinal, FrmRecaudoControladorEnum.FORMATO_FECHA.getValue()));

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar,
                        parametros);

        try
        {
            parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            reporte));
            Logger.getLogger(FrmRecaudoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCompania()
    {
        return compania;
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

    public boolean isVisibleFecha()
    {
        return visibleFecha;
    }

    public void setVisibleFecha(boolean visibleFecha)
    {
        this.visibleFecha = visibleFecha;
    }

}
