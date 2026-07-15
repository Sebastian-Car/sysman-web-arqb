/*-
 * InformePlanosDigitales.java
 *
 * 1.0
 *
 * 16/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte de archivos generados
 * para lecturas digitales para un rango de fechas seleccionado
 *
 * @version 1.0, 16/11/2016
 * @author jlozano
 * 
 * @version 2.0, 05/06/2017 Proceso de refactoring.
 * @author jrodrigueza
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class InformePlanosDigitales extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la fecha inicial seleccionada
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final seleccionada
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformePlanosDigitales
     */
    public InformePlanosDigitales()
    {
        super();
        compania = SessionUtil.getCompania();
        fechaInicial = new Date();
        fechaFinal = new Date();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORME_PLANOS_DIGITALES
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     * Genera el reporte en formato PDF
     *
     */
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * Genera el reporte en formato EXCEL
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Generacion del reporte de <b>Archivos generados para lectores
     * digitales</b>.
     * 
     * @param formato
     * Formato para generar el reporte.
     */
    public void generarInforme(FORMATOS formato)
    {
        String reporte = "001255LAudFimmEnvio";
        if (fechaInicial.after(fechaFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
        }
        else
        {

            try
            {
                HashMap<String, Object> reemplazar = new HashMap<>();
                Map<String, Object> parametros = new HashMap<>();

                reemplazar.put("fechaInicial",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial, "dd/MM/yyyy"));
                reemplazar.put("fechaFinal",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal, "dd/MM/yyyy"));
                parametros.put("PR_FECHAINICIAL",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial));
                parametros.put("PR_FECHAFINAL",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));
                Reporteador.resuelveConsulta("001255LAudFimmEnvio",
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (FileNotFoundException e)
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                                .replace("s$reporte$s", reporte)
                                    + e.getMessage());
                logger.error(e.getMessage(), e);
            }

            catch (JRException | IOException | SysmanException
                            | ParseException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getCompania()
    {
        return compania;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
