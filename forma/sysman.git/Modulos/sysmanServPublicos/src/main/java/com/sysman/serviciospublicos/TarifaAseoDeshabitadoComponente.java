/*-
 * TarifaAseoDeshabitadoComponente.java
 *
 * 1.0
 *
 * 29/09/2016
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte "Tarifas de aseo deshabitado por componente"
 *
 * @version 1, 29/09/2016 16:21:52 -- Modificado por jlozano
 * @author jlozano
 * @version 2.0, 16/06/2017
 * @author jguerrero Se ajusta la generación del repote teniendo en cuenta los filtros.
 */
@ManagedBean
@ViewScoped
public class TarifaAseoDeshabitadoComponente extends BeanBaseModal
{
    /**
     * Controlador que permite generar el reporte "Tarifas de aseo deshabitado por componente" para un anio y periodo seleccionados
     */

    // <DECLARAR_ATRIBUTOS>
    private StreamedContent archivoDescarga;

    /**
     * Anio para el que se genera el reporte
     */
    private String ano;
    /**
     * Periodo para el que se genera el reporte
     */
    private String mes;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of TarifaAseoDeshabitadoComponente
     */
    public TarifaAseoDeshabitadoComponente()
    {
        super();

        ano = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)); // anio
        // por
        // defecto
        // (anio
        // actual)
        mes = StringUtils.leftPad(String.valueOf(
                        Calendar.getInstance().get(Calendar.MONTH) + 1), 2,
                        '0'); // mes
        // por
        // defecto
        // (mes
        // actual)
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TARIFA_ASEO_DESHABITADO_COMPONENTE
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ue se ejecuta al presionar el boton Aceptar. Verifica que el anio y el periodo seleccionados sean validos
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!"".equals(ano) && !"".equals(mes))
        {
            if ((Integer.parseInt(mes) == 0) || (Integer.parseInt(mes) > 12))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1657")); // El
                                                                             // período
                                                                             // no
                                                                             // puede
                                                                             // ser
                                                                             // 0
                                                                             // ni
                                                                             // mayor
                                                                             // a
                                                                             // 12
                return;
            }
            if (Integer.parseInt(ano) < 1900)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1659") // Me
                                                                           // parece
                                                                           // que
                                                                           // el
                                                                           // año
                                                                           // s$ano$s
                                                                           // está
                                                                           // mal
                                .replace("s$ano$s", ano));
                return;
            }
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1658")); // Faltan
                                                            // datos
                                                            // para
                                                            // generar
                                                            // el
                                                            // informe.
            return;
        }
        generarreporte("001110TarifaAseoDeshabitadoComponentes",
                        ReportesBean.FORMATOS.PDF);
    }

    /**
     * Metodo que genera el reporte
     *
     * @param nombreInforme
     * Nombre del reporte que se va a generar
     * @param formato
     * Formato en el que se va a generar el reporte
     */
    private void generarreporte(String nombreInforme,
        ReportesBean.FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazos.put("ano", ano);
            reemplazos.put("mes", mes);

            parametros.put("PR_ANO", ano);
            parametros.put("PR_MES", StringUtils.capitalize(
                            new DateFormatSymbols()
                                            .getMonths()[Integer.parseInt(mes)
                                                - 1]));

            Reporteador.resuelveConsulta(nombreInforme,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreInforme, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </CODIGO_DESARROLLADO>

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes()
    {
        // <CODIGO_DESARROLLADO>
        mes = StringUtils.leftPad(mes, 2, '0');
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
