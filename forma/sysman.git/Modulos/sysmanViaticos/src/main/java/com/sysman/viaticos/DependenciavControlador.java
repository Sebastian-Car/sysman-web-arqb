/*-
 * DependenciavControlador.java
 *
 * 1.0
 *
 * 17/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.DependenciavControladorUrlEnum;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 17/01/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class DependenciavControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el codigo del modulo actual
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de dependencias final
     */
    private RegistroDataModelImpl listaCmbAreaFin;
    /**
     * Listado de dependencias inicial
     */
    private RegistroDataModelImpl listaCmbAreaIn;

    /**
     * Variable que almacena el objeto del archivo de descarga para generar los reportes
     *
     */
    private StreamedContent archivoDescarga;

    /**
     * Variable que almacena la fecha inicial seleccionada por el usuario para los filtros
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la fecha final seleccionada por el usuario para los filtros
     */
    private Date fechaFinal;

    /**
     * Almacena el codigo inicial seleccionado por el usuario
     */
    private String codigoInicial;
    /**
     * Almacena el codigo final seleccionado por el usuario
     */
    private String codigoFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DependenciavControlador
     */
    public DependenciavControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DEPENDENCIAV_CONTROLADOR
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
        cargarListaCmbAreaIn();
        cargarListaCmbAreaFin();
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

    /**
     *
     * Carga la lista listaCmbAreaIn
     *
     */
    public void cargarListaCmbAreaIn()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DependenciavControladorUrlEnum.URL4130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCmbAreaIn = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCmbAreaFin
     *
     */
    public void cargarListaCmbAreaFin()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DependenciavControladorUrlEnum.URL4131
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), codigoInicial);
        listaCmbAreaFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdInforme en la vista
     *
     *
     */
    public void oprimirCmdInforme()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme("001649InfDependencia");
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Viaticos en la vista
     *
     *
     */
    public void oprimirViaticos()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme("001654VIATICOSDIAN");
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(String reporte)
    {

        if (fechaFinal.before(fechaInicial))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3930"));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();

        reemplazar.put("fechaInicial", SysmanFunciones
                        .formatearFechaCadena(fechaInicial, "DD/MM/YYYY"));
        reemplazar.put("fechaFinal", SysmanFunciones
                        .formatearFechaCadena(fechaFinal, "DD/MM/YYYY"));
        reemplazar.put("codigoInicial", codigoInicial);
        reemplazar.put("codigoFinal", codigoFinal);

        try
        {
            parametros.put("PR_FORMS_MENU_VIATICOS_ICONO", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FORMS_DEPENDENCIA_FECHA", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FORMS_DEPENDENCIA_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaCmbAreaIn(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        cargarListaCmbAreaFin();
    }

    public void seleccionarFilaCmbAreaFin(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
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
    /**
     * Retorna la lista listaCmbAreaFin
     *
     * @return listaCmbAreaFin
     */
    public RegistroDataModelImpl getListaCmbAreaFin()
    {
        return listaCmbAreaFin;
    }

    /**
     * Asigna la lista listaCmbAreaFin
     *
     * @param listaCmbAreaFin
     * Variable a asignar en listaCmbAreaFin
     */
    public void setListaCmbAreaFin(RegistroDataModelImpl listaCmbAreaFin)
    {
        this.listaCmbAreaFin = listaCmbAreaFin;
    }

    /**
     * Retorna la lista listaCmbAreaIn
     *
     * @return listaCmbAreaIn
     */
    public RegistroDataModelImpl getListaCmbAreaIn()
    {
        return listaCmbAreaIn;
    }

    /**
     * Asigna la lista listaCmbAreaIn
     *
     * @param listaCmbAreaIn
     * Variable a asignar en listaCmbAreaIn
     */
    public void setListaCmbAreaIn(RegistroDataModelImpl listaCmbAreaIn)
    {
        this.listaCmbAreaIn = listaCmbAreaIn;
    }

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

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
