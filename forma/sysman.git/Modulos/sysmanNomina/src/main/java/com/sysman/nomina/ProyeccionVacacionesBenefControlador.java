/*-
 * ProyeccionVacacionesBenefControlador.java
 *
 * 1.0
 *
 * 17/01/2019
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 17/01/2019
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ProyeccionVacacionesBenefControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private int ano;
    private int mes;
    private int periodo;
    private int opcion = 1;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
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
     * Crea una nueva instancia de ProyeccionVacacionesBenefControlador
     */
    public ProyeccionVacacionesBenefControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 2020;
            validarPermisos();

            ano = Integer.parseInt(SessionUtil.getSessionVar("anioNomina").toString());
            mes = Integer.parseInt(SessionUtil.getSessionVar("mesNomina").toString());
            periodo = Integer.parseInt(SessionUtil.getSessionVar("periodoNomina").toString());

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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
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
     * Metodo ejecutado al oprimir el boton saldovacdiciembre en la vista
     *
     *
     */
    public void oprimirsaldovacdiciembre()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (((periodo == 8) || (periodo == 3)) && (mes == 12))
        {
            String[] nombresArchivos = new String[2];
            ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("compania", compania);
            reemplazos.put("ano", ano);
            reemplazos.put("mes", mes);
            reemplazos.put("periodo", periodo);

            Map<String, Object> parametro = new HashMap<>();
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametro.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            try
            {
                String extension = opcion == 1 ? ".Pdf" : ".xlsx";
                String reporte = "001983SALDOBeneficiosPendientes";
                nombresArchivos[0] = reporte + extension;
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametro);

                salidas[0] = JsfUtil.serializarReporte(reporte,
                                parametro, ConectorPool.ESQUEMA_SYSMAN,
                                opcion == 1 ? FORMATOS.PDF : FORMATOS.EXCEL);

                reporte = "001984SALDOVacacionesPendientes";
                nombresArchivos[1] = reporte + extension;
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametro);

                salidas[1] = JsfUtil.serializarReporte(reporte,
                                parametro, ConectorPool.ESQUEMA_SYSMAN,
                                opcion == 1 ? FORMATOS.PDF : FORMATOS.EXCEL);

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas, nombresArchivos);

            }
            catch (JRException | IOException | SysmanException | SQLException | DRException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4278"));
        }

        // </CODIGO_DESARROLLADO>
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
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public int getOpcion()
    {
        return opcion;
    }

    public void setOpcion(int opcion)
    {
        this.opcion = opcion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
