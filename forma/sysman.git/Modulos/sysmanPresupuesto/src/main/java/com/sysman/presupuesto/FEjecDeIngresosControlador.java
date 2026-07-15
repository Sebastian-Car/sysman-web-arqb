/*-
 * FEjecDeIngresosControlador.java
 *
 * 1.0
 *
 * 29/05/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FEjecDeIngresosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera el reporte de ejecucion por anios
 *
 * @version 1.0, 29/05/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FEjecDeIngresosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que alamcena el numero de modulo por
     * el cual accedio el usuario
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la cuenta inicial seleccionada
     */
    private String cuentaInicial;
    /**
     * Atributo que almacena la cuenta final seleccionada
     */
    private String cuentaFinal;
    /**
     * Atributo que almacena el anio seleccionado
     */
    private String anio;

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
     * Lsita que carga los anios
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las cuentas iniciales
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista que carga las cuentas finales
     */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FEjecDeIngresosControlador
     */
    public FEjecDeIngresosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            numFormulario = 1805;
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
        cargarListaAno();
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
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        cargarListaCuentaInicial();
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FEjecDeIngresosControladorUrlEnum.URL4229
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
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FEjecDeIngresosControladorUrlEnum.URL4692
                                                        .getValue());

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FEjecDeIngresosControladorUrlEnum.URL5334
                                                        .getValue());
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf()
    {
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
    }

    private void generarReporte(FORMATOS formato)
    {
        Map<String, Object> parametros = new TreeMap<>();
        Map<String, Object> reemplazos = new TreeMap<>();
        try
        {
            reemplazos.put("compania", compania);

            reemplazos.put("anio", anio);

            reemplazos.put("cuentaInicial", cuentaInicial);

            reemplazos.put("cuentaFinal", cuentaFinal);

            parametros.put("PR_NOMBRECOMPANIA", SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "TITULO INFORMES MUNICIPIO", modulo,
                                            new Date(),
                                            false),
                            SessionUtil.getCompaniaIngreso().getNombre()));

            parametros.put("PR_FIRMA_1_EN_PRESUPUESTO", SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "FIRMA 1 EN PRESUPUESTO", modulo,
                                            new Date(),
                                            false),
                            ""));

            parametros.put("PR_CARGO_1_EN_PRESUPUESTO", SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "CARGO 1 EN PRESUPUESTO", modulo,
                                            new Date(),
                                            false),
                            ""));

            parametros.put("PR_FIRMA_2_EN_PRESUPUESTO", SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "FIRMA 2 EN PRESUPUESTO", modulo,
                                            new Date(),
                                            false),
                            ""));

            parametros.put("PR_CARGO_2_EN_PRESUPUESTO", SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "CARGO 2 EN PRESUPUESTO", modulo,
                                            new Date(),
                                            false),
                            ""));

            parametros.put("PR_ANO", anio);

            Reporteador.resuelveConsulta("001781FEjecDeIngresos",
                            Integer.parseInt(modulo), reemplazos, parametros);

            if (formato.equals(ReportesBean.FORMATOS.EXCEL)
                || formato.equals(ReportesBean.FORMATOS.EXCEL97))
            {
                reemplazos.put("consultaBase", Reporteador.resuelveConsulta(
                                "001781FEjecDeIngresos",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));

                String salida = Reporteador.resuelveConsulta(
                                "001781FEjecDeIngresos_Excel",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(salida,
                                ConectorPool.ESQUEMA_SYSMAN, formato,
                                "001781FEjecDeIngresos");
            }
            else
            {

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001781FEjecDeIngresos",
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

        }
        catch (SystemException | JRException | IOException
                        | SysmanException | SQLException | DRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     *
     *
     */
    public void cambiarAno()
    {
        cargarListaCuentaInicial();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cuentaInicial
     *
     * @return cuentaInicial
     */
    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     *
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     *
     * @return cuentaFinal
     */
    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     *
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio)
    {
        this.anio = anio;
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
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
