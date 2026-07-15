/*-
 * AcumuladocostomedioControlador.java
 *
 * 1.0
 *
 * 09/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

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
import com.sysman.nomina.enums.AcumuladocostomedioControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;

import java.io.IOException;
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
 *
 * @version 1.0, 09/01/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class AcumuladocostomedioControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listaAno1;
    /**
     */
    private List<Registro> listaAno2;
    /**
     */
    private List<Registro> listaMes1;
    /**
     */
    private List<Registro> listaMes2;
    /**
     */
    private List<Registro> listaPeriodo1;
    /**
     */
    private List<Registro> listaPeriodo2;
    /**
     */
    private List<Registro> listaProceso;
    /**
     */
    private String proceso;
    /**
     */
    private String anio1;
    /**
     */
    private String anio2;
    /**
     */
    private String mes1;
    /**
     */
    private String mes2;
    /**
     */
    private String periodo1;
    /**
     */
    private String periodo2;
    /**
     *
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AcumuladocostomedioControlador
     */
    public AcumuladocostomedioControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACUMULADOCOSTOMEDIO_CONTROLADOR
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
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
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
     * Metodo ejecutado al cambiar el control Proceso
     *
     */
    public void cambiarProceso()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1()
    {
        cargarListaMes1();
    }

    public void cambiarAno2()
    {
        cargarListaMes2();
    }

    public void cambiarMes1()
    {
        cargarListaPeriodo1();
    }

    public void cambiarMes2()
    {
        cargarListaPeriodo2();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAno1
     *
     */
    public void cargarListaAno1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try
        {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaAno2
     *
     */
    public void cargarListaAno2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try
        {
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaMes1
     *
     */
    public void cargarListaMes1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio1);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaMes2
     *
     */
    public void cargarListaMes2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio2);

        try
        {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaPeriodo1
     *
     */
    public void cargarListaPeriodo1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaPeriodo2
     *
     */
    public void cargarListaPeriodo2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio2);
        param.put(GeneralParameterEnum.MES.getName(), mes2);

        try
        {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaProceso
     *
     */
    public void cargarListaProceso()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladocostomedioControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton PreliminarBancos en la vista
     *
     *
     */
    public void oprimirPreliminarBancos()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();

        parametros.put("PR_FORMS_ACUMULADO_COSTO_MEDIO_MES1",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes1)]);
        parametros.put("PR_FORMS_ACUMULADO_COSTO_MEDIO_ANO1", anio1);
        
        // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
        parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
        // FIN IMPLEMENTACION MARCA_BLANCA
        
        reemplazar.put("ano2", anio2);
        reemplazar.put("mes2", mes2);

        Reporteador.resuelveConsulta("001618CRA68RESUMENTOTAL",
                        Integer.valueOf(SessionUtil.getModulo()),
                        reemplazar, parametros);

        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001618CRA68RESUMENTOTAL", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton acumuladosp en la vista
     *
     *
     */
    public void oprimiracumuladosp()
    {
        // <CODIGO_DESARROLLADO>
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno1
     *
     * @return listaAno1
     */
    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    /**
     * Asigna la lista listaAno1
     *
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    /**
     * Retorna la lista listaAno2
     *
     * @return listaAno2
     */
    public List<Registro> getListaAno2()
    {
        return listaAno2;
    }

    /**
     * Asigna la lista listaAno2
     *
     * @param listaAno2
     * Variable a asignar en listaAno2
     */
    public void setListaAno2(List<Registro> listaAno2)
    {
        this.listaAno2 = listaAno2;
    }

    /**
     * Retorna la lista listaMes1
     *
     * @return listaMes1
     */
    public List<Registro> getListaMes1()
    {
        return listaMes1;
    }

    /**
     * Asigna la lista listaMes1
     *
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes1(List<Registro> listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    /**
     * Retorna la lista listaMes2
     *
     * @return listaMes2
     */
    public List<Registro> getListaMes2()
    {
        return listaMes2;
    }

    /**
     * Asigna la lista listaMes2
     *
     * @param listaMes2
     * Variable a asignar en listaMes2
     */
    public void setListaMes2(List<Registro> listaMes2)
    {
        this.listaMes2 = listaMes2;
    }

    /**
     * Retorna la lista listaPeriodo1
     *
     * @return listaPeriodo1
     */
    public List<Registro> getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    /**
     * Asigna la lista listaPeriodo1
     *
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo1(List<Registro> listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    /**
     * Retorna la lista listaPeriodo2
     *
     * @return listaPeriodo2
     */
    public List<Registro> getListaPeriodo2()
    {
        return listaPeriodo2;
    }

    /**
     * Asigna la lista listaPeriodo2
     *
     * @param listaPeriodo2
     * Variable a asignar en listaPeriodo2
     */
    public void setListaPeriodo2(List<Registro> listaPeriodo2)
    {
        this.listaPeriodo2 = listaPeriodo2;
    }

    /**
     * Retorna la lista listaProceso
     *
     * @return listaProceso
     */
    public List<Registro> getListaProceso()
    {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     *
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    public String getProceso()
    {
        return proceso;
    }

    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    public String getAnio1()
    {
        return anio1;
    }

    public void setAnio1(String anio1)
    {
        this.anio1 = anio1;
    }

    public String getAnio2()
    {
        return anio2;
    }

    public void setAnio2(String anio2)
    {
        this.anio2 = anio2;
    }

    public String getMes1()
    {
        return mes1;
    }

    public void setMes1(String mes1)
    {
        this.mes1 = mes1;
    }

    public String getMes2()
    {
        return mes2;
    }

    public void setMes2(String mes2)
    {
        this.mes2 = mes2;
    }

    public String getPeriodo1()
    {
        return periodo1;
    }

    public void setPeriodo1(String periodo1)
    {
        this.periodo1 = periodo1;
    }

    public String getPeriodo2()
    {
        return periodo2;
    }

    public void setPeriodo2(String periodo2)
    {
        this.periodo2 = periodo2;
    }

    public String getCompania()
    {
        return compania;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
