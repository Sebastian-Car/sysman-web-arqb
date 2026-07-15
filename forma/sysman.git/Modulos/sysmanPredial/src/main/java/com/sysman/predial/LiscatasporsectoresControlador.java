/*-
 * LiscatasporsectoresControlador.java
 *
 * 1.0
 *
 * 14/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario desde el cual genera el informe de estadisticas catastrales por sectores. Se accede desde la ruta Panel Principal\Impuesto Predial\Informes\Con predios\Estadisticas catastrales por
 * sectores.
 *
 * @version 1.0, 14/02/2017
 * @author lcortes
 *
 * @author asana
 * @version 2.0 13/06/2017 se implementa enum en formulario y se ajusta conexion.
 *
 * @author spina
 * @version 3, 07/07/2017 - no requiere refactorizacion para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class LiscatasporsectoresControlador extends BeanBaseModal
{

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que identifica el sector inicial digitado en el formulario.
     */
    private String sectorInicial;
    /**
     * Atributo que identifica el sector final digitado en el formulario.
     */
    private String sectorFinal;
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
     * Crea una nueva instancia de LiscatasporsectoresControlador
     */
    public LiscatasporsectoresControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISCATASPORSECTORES_CONTROLADOR
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
     * Metodo ejecutado al oprimir el boton Imprimir en la vista Permite generar el reporte en formato PDF
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarCamposVacios())
        {
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton presExcel en la vista Permite generar el reporte en formato Excel
     *
     */
    public void oprimirpresExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarCamposVacios())
        {
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo encargado de generar el reporte 001402rptListadoCatastralSectores
     *
     * @param formato
     * define el formato en el cual se va a generar el informe, para el caso Excel o PDF
     */
    private void generarInforme(FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            sectorInicial = sectorInicial.replace("-", "");
            sectorFinal = sectorFinal.replace("-", "");

            if (Integer.parseInt(sectorInicial) > Integer.parseInt(sectorFinal))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3367"));
                return;
            }

            reemplazar.put("sectorInicial", sectorInicial);
            reemplazar.put("sectorFinal", sectorFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_SECTORINICIAL", sectorInicial);
            parametros.put("PR_SECTORFINAL", sectorFinal);
            Reporteador.resuelveConsulta("001402rptListadoCatastralSectores",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001402rptListadoCatastralSectores",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que verifica que ninguno de los campos del formulario est�n vacios
     *
     * @return true si los campos estan completamente diligenciados.
     */
    private boolean validarCamposVacios()
    {
        if (SysmanFunciones.validarVariableVacio(sectorInicial)
            || SysmanFunciones.validarVariableVacio(sectorFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2823"));
            return false;
        }

        return true;
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
     * Retorna la variable sectorInicial
     *
     * @return sectorInicial
     */
    public String getSectorInicial()
    {
        return sectorInicial;
    }

    /**
     * Asigna la variable sectorInicial
     *
     * @param sectorInicial
     * Variable a asignar en sectorInicial
     */
    public void setSectorInicial(String sectorInicial)
    {
        this.sectorInicial = sectorInicial;
    }

    /**
     * Retorna la variable sectorFinal
     *
     * @return sectorFinal
     */
    public String getSectorFinal()
    {
        return sectorFinal;
    }

    /**
     * Asigna la variable sectorFinal
     *
     * @param sectorFinal
     * Variable a asignar en sectorFinal
     */
    public void setSectorFinal(String sectorFinal)
    {
        this.sectorFinal = sectorFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
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
