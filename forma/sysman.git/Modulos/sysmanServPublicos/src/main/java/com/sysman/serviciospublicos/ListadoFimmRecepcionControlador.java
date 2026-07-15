/*-
 * ListadoFimmRecepcionControlador.java
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
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ListadoFimmRecepcionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador para generar el listado de lecturas digitales devueltas
 * teniendo en cuenta un rango de fechas, de vigencias y periodos
 *
 * @version 1.0, 16/11/2016
 * @author sdaza
 * 
 * @author eamaya
 * @version 2.0 06/06/2017 Proceso de Refactoring
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class ListadoFimmRecepcionControlador extends BeanBaseModal
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
     * variable que indica que tipo de informe se genera 1- Normal 2-
     * Detallado
     */
    private String tipoInforme;
    /**
     * variable que indica si el informe a genera se filtra por rango
     * de fechas de aforo
     */
    private boolean opAforo;
    /**
     * variable que indica si el informe a genera se filtra por rango
     * de fechas de carga de archivos
     */
    private boolean opCarga;
    /**
     * variable que almacena el periodo inicial para generar el
     * informe
     */
    private String periodoInicial;
    /**
     * variable que almacena el periodo final para generar el informe
     */
    private String periodoFinal;
    /**
     * variable que almacena el a�o inicial para generar el informe
     */
    private String anoInicial;
    /**
     * variable que almacena el a�o final para generar el informe
     */
    private String anoFinal;
    /**
     * variable que almacena la fecha inicial para generar el informe
     */
    private Date fechaInicial;
    /**
     * variable que almacena la fecha final para generar el informe
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
    /**
     * variable que lista las opciones de periodos iniciales teniendo
     * en cuenta el a�o inicial seleccionado
     */
    private List<Registro> listacmbPeriodoInicial;
    /**
     * variable que lista las opciones de periodos finales teniendo en
     * cuenta el a�o final seleccionado
     */
    private List<Registro> listacmbPeriodoFinal;
    /**
     * variable que lista los a�os iniciales
     */
    private List<Registro> listatxtAnoInicial;
    /**
     * variable que lista los a�os finales
     */
    private List<Registro> listatxtAnoFinal;
    /**
     * variable que almacena el tipo reporte que se genera
     */

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ListadoFimmRecepcionControlador
     */
    public ListadoFimmRecepcionControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_FIMM_RECEPCION_CONTROLADOR
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
        // inicialiar variables de fecha inicial y final con la fecha
        // actual
        fechaInicial = new Date();
        fechaFinal = new Date();
        anoInicial = String.valueOf(SysmanFunciones.ano(new Date()));
        // Inicializar opcion para filtros
        opCarga = true;
        opAforo = false;
        // Inicializar tipo de informe
        tipoInforme = "1";
        cargarListacmbPeriodoInicial();
        cargarListacmbPeriodoFinal();
        cargarListatxtAnoInicial();
        cargarListatxtAnoFinal();
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
        // CODIGO_DESARROLLADO
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Metodo para cargas los elementos de la lista de periodo
     */
    public void cargarListacmbPeriodoInicial()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anoInicial);

            listacmbPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFimmRecepcionControladorUrlEnum.URL6405
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo para cargas los elementos de la lista de periodo
     */
    public void cargarListacmbPeriodoFinal()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anoFinal);

            listacmbPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFimmRecepcionControladorUrlEnum.URL7225
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo para cargas los elementos de la lista de a�os
     */
    public void cargarListatxtAnoInicial()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listatxtAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFimmRecepcionControladorUrlEnum.URL8036
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo para cargas los elementos de la lista de a�os
     */
    public void cargarListatxtAnoFinal()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.ANO.getName(),
                            anoInicial);

            listatxtAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFimmRecepcionControladorUrlEnum.URL8592
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo para generar informe en formato PDF
     *
     */
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo para generar informe en formato Excel
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite generar el reporte con los filtros y
     * formatos seleccionados.
     *
     * @param formato
     * Formato en el cual se genera el reporte.
     */
    private void generarInforme(FORMATOS formato)
    {
        String reporte = "";

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try
        {
            if ("1".equals(tipoInforme))
            {
                reporte = "001260ListadoFimmRecep";
            }
            else
            {

                reporte = "001256LPostLectura";
            }
            if (opCarga)
            {
                reemplazar.put("fechaCampo",
                                "TO_CHAR(TRUNC(SP_PLANOSDIGITAL.FECHASUBIDA), 'DD/MM/YYYY')");
                reemplazar.put("condicionFecha",
                                "TRUNC(SP_PLANOSDIGITAL.FECHASUBIDA) BETWEEN TO_DATE('"
                                    + SysmanFunciones
                                                    .convertirAFechaCadena(
                                                                    fechaInicial)
                                    + "', 'DD/MM/YYYY') AND TO_DATE('"
                                    + SysmanFunciones
                                                    .convertirAFechaCadena(
                                                                    fechaFinal)
                                    + "', 'DD/MM/YYYY')");
            }
            if (opAforo)
            {
                reemplazar.put("fechaCampo",
                                "TO_CHAR(SUBSTR(SP_PLANOSDIGITAL.MAEHORAF, 1,2) || '/' || SUBSTR(SP_PLANOSDIGITAL.MAEHORAF, 3,2) || '/' ||SUBSTR(SP_PLANOSDIGITAL.MAEHORAF, 5,2)) ");
                reemplazar.put("condicionFecha",
                                "SP_PLANOSDIGITAL.MAEHORAF BETWEEN TO_CHAR(TO_DATE('"
                                    + SysmanFunciones
                                                    .convertirAFechaCadena(
                                                                    fechaInicial)
                                    + "', 'DD/MM/YYYY'), 'DDMMYY') AND TO_CHAR(TO_DATE('"
                                    + SysmanFunciones
                                                    .convertirAFechaCadena(
                                                                    fechaFinal)
                                    + "', 'DD/MM/YYYY'),'DDMMYY')");
            }

            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("anoFinal", anoFinal);
            reemplazar.put("periodoInicial", periodoInicial);
            reemplazar.put("periodoFinal", periodoFinal);

            parametros.put("PR_TITULO",
                            "Importados entre las fechas " + SysmanFunciones
                                            .convertirAFechaCadena(fechaInicial)
                                + " y "
                                + SysmanFunciones
                                                .convertirAFechaCadena(
                                                                fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
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

        catch (JRException | IOException | SysmanException | ParseException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     *
     * Metodo que actualiza la carga de la lista de periodo inicial
     * despues de cambiar el valor del a�o inicial
     *
     */
    public void cambiartxtAnoInicial()
    {
        cargarListacmbPeriodoInicial();
        cargarListatxtAnoFinal();
    }

    /**
     *
     * Metodo que actualiza la carga de la lista de periodo inicial
     * despues de cambiar el valor del a�o inicial
     *
     */
    public void cambiartxtAnoFinal()
    {
        cargarListacmbPeriodoFinal();
    }

    /**
     *
     * Metodo que valida que solo una opcion de rango de fechas este
     * seleccionada
     *
     */
    public void cambiarOpAforo()
    {
        if (opAforo)
        {
            opCarga = false;
        }
        else
        {
            opCarga = true;
        }

    }

    /**
     *
     * Metodo que valida que solo una opcion de rango de fechas este
     * seleccionada
     *
     */
    public void cambiarOpCarga()
    {
        if (opCarga)
        {
            opAforo = false;
        }
        else
        {
            opAforo = true;
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoInforme
     *
     * @return tipoInforme
     */
    public String getTipoInforme()
    {
        return tipoInforme;
    }

    /**
     * Asigna la variable tipoInforme
     *
     * @param tipoInforme
     * Variable a asignar en tipoInforme
     */
    public void setTipoInforme(String tipoInforme)
    {
        this.tipoInforme = tipoInforme;
    }

    /**
     * Retorna la variable opAforo
     *
     * @return opAforo
     */
    public boolean getOpAforo()
    {
        return opAforo;
    }

    /**
     * Asigna la variable opAforo
     *
     * @param opAforo
     * Variable a asignar en opAforo
     */
    public void setOpAforo(boolean opAforo)
    {
        this.opAforo = opAforo;
    }

    /**
     * Retorna la variable opCarga
     *
     * @return opCarga
     */
    public boolean getOpCarga()
    {
        return opCarga;
    }

    /**
     * Asigna la variable opCarga
     *
     * @param opCarga
     * Variable a asignar en opCarga
     */
    public void setOpCarga(boolean opCarga)
    {
        this.opCarga = opCarga;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial()
    {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial)
    {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal()
    {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal)
    {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial()
    {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial)
    {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal()
    {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal)
    {
        this.anoFinal = anoFinal;
    }

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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbPeriodoInicial
     *
     * @return listacmbPeriodoInicial
     */
    public List<Registro> getListacmbPeriodoInicial()
    {
        return listacmbPeriodoInicial;
    }

    /**
     * Asigna la lista listacmbPeriodoInicial
     *
     * @param listacmbPeriodoInicial
     * Variable a asignar en listacmbPeriodoInicial
     */
    public void setListacmbPeriodoInicial(
        List<Registro> listacmbPeriodoInicial)
    {
        this.listacmbPeriodoInicial = listacmbPeriodoInicial;
    }

    /**
     * Retorna la lista listacmbPeriodoFinal
     *
     * @return listacmbPeriodoFinal
     */
    public List<Registro> getListacmbPeriodoFinal()
    {
        return listacmbPeriodoFinal;
    }

    /**
     * Asigna la lista listacmbPeriodoFinal
     *
     * @param listacmbPeriodoFinal
     * Variable a asignar en listacmbPeriodoFinal
     */
    public void setListacmbPeriodoFinal(List<Registro> listacmbPeriodoFinal)
    {
        this.listacmbPeriodoFinal = listacmbPeriodoFinal;
    }

    /**
     * Retorna la lista listatxtAnoInicial
     *
     * @return listatxtAnoInicial
     */
    public List<Registro> getListatxtAnoInicial()
    {
        return listatxtAnoInicial;
    }

    /**
     * Asigna la lista listatxtAnoInicial
     *
     * @param listatxtAnoInicial
     * Variable a asignar en listatxtAnoInicial
     */
    public void setListatxtAnoInicial(List<Registro> listatxtAnoInicial)
    {
        this.listatxtAnoInicial = listatxtAnoInicial;
    }

    /**
     * Retorna la lista listatxtAnoFinal
     *
     * @return listatxtAnoFinal
     */
    public List<Registro> getListatxtAnoFinal()
    {
        return listatxtAnoFinal;
    }

    /**
     * Asigna la lista listatxtAnoFinal
     *
     * @param listatxtAnoFinal
     * Variable a asignar en listatxtAnoFinal
     */
    public void setListatxtAnoFinal(List<Registro> listatxtAnoFinal)
    {
        this.listatxtAnoFinal = listatxtAnoFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
