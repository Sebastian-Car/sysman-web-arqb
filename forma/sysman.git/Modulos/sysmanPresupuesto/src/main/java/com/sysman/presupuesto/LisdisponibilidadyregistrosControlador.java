/*-
 * LisdisponibilidadyregistrosControlador.java
 *
 * 1.0
 *
 * 29/11/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisdisponibilidadyregistrosControladorEnum;
import com.sysman.presupuesto.enums.LisdisponibilidadyregistrosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 29/11/2017
 * @author spina
 */
@ManagedBean
@ViewScoped
public class LisdisponibilidadyregistrosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * modulo actual
     */
    private String modulo;

    /**
     * variable con el archivo de descarga del reporte
     */
    private StreamedContent archivoDescarga;

    /**
     * captura el valor de la fecha inicial del formulario
     */
    private Date fechaInicial;
    /**
     * captura el valor de la fecha final del formulario
     */
    private Date fechaFinal;

    /**
     * define el orden de los registro del reporte check del formulario
     */
    private boolean porRubro;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LisdisponibilidadyregistrosControlador
     */
    public LisdisponibilidadyregistrosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISDISPONIBILIDADYREGISTROS_CONTROLADOR
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
        fechaInicial = new Date();
        fechaFinal = new Date();
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
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando51 en la vista
     *
     *
     */
    public void oprimirComando51()
    {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        try
        {
            // Reemplazos valores consulta reporte
            reemplazos.put("compania", compania);
            reemplazos.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            if (porRubro)
            {
                // LisDisponibilidadyRegistros
                reemplazos.put("ordenamiento",
                                "ORDER BY RUBRO, NODISP, FECHADISP, NUMERORES");
            }
            else
            {
                // LisDisponibilidadyRegistrosDIS
                reemplazos.put("ordenamiento",
                                "ORDER BY NODISP, FECHADISP, RUBRO, NUMERORES");
            }

            // Inicio Parametros Informe
            parametros.put("PR_TITULOSEXTOINFORME",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "TITULO SEXTO INFORME CONTRALORIA",
                                            modulo, new Date(), true));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_LISDISPONIBILIDADYREGISTROS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_LISDISPONIBILIDADYREGISTROS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_CARGO_PRESUPUESTO_1",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO PRESUPUESTO 1",
                                            modulo, new Date(), true));
            parametros.put("PR_CARGO_PRESUPUESTO_2",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO PRESUPUESTO 2",
                                            modulo, new Date(), true));

            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "MOSTRAR CONTRATO EN INFORME REGISTROS ABIERTOS POR CUENTAS",
                            modulo, new Date(), true)))
            {
                parametros.put("PR_MOSTRAR_CONTRATO", "SI");
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial));
                param.put(GeneralParameterEnum.FECHAFINAL.getName(),
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal));

                // TOTALDISPONIBILIDADES
                param.put(LisdisponibilidadyregistrosControladorEnum.CLASES
                                .getValue(), "DIS,ADD,DMD");
                Registro regDis = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LisdisponibilidadyregistrosControladorUrlEnum.URL4840
                                                                                .getValue())
                                                .getUrl(), param));
                parametros.put("PR_TOTALDISPONIBILIDADES",
                                BigDecimal.valueOf(SysmanFunciones.nvlDbl(regDis
                                                .getCampos().get("TOTALMOV"),
                                                0.0)));

                // TOTALRESERVAS
                param.put(LisdisponibilidadyregistrosControladorEnum.CLASES
                                .getValue(), "RES,ADR,DMR");
                Registro regRes = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LisdisponibilidadyregistrosControladorUrlEnum.URL4840
                                                                                .getValue())
                                                .getUrl(), param));
                parametros.put("PR_TOTALRESERVAS",
                                BigDecimal.valueOf(SysmanFunciones.nvlDbl(regRes
                                                .getCampos().get("TOTALMOV"),
                                                0.0)));
            }
            else
            {
                parametros.put("PR_MOSTRAR_CONTRATO", "NO");
            }

            Reporteador.resuelveConsulta("001530LisDisponibilidadYRegistros",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001530LisDisponibilidadYRegistros",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
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

    public boolean isPorRubro()
    {
        return porRubro;
    }

    public void setPorRubro(boolean porRubro)
    {
        this.porRubro = porRubro;
    }
}
