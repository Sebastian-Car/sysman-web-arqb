/*-
 * CdestadospgastosfuncControlador.java
 *
 * 1.0
 *
 * 28/11/2017
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.CdestadospgastosfuncControladorEnum;
import com.sysman.presupuesto.enums.CdestadospgastosfuncControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 28/11/2017
 * @author spina
 */
@ManagedBean
@ViewScoped
public class CdestadospgastosfuncControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo que contiene el valor asignado al mes por la cual se va a filtrar el reporte
     */
    private int mes;
    /**
     * Atributo que contiene el valor asignado al anio por la cual se va a filtrar el reporte
     */
    private int ano;
    /**
     * listado de anos para filtrar el reporte
     */
    private List<Registro> listaAno;

    /**
     * Valor cuenta inicial para el filtro del reporte
     */
    private String cuentaInicial;
    /**
     * Valor cuenta final para el filtro del reporte
     */
    private String cuentaFinal;
    /**
     * lista del combo con las cuentas
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * lista del combo con las cuentas
     */
    private RegistroDataModelImpl listaCuentaFinal;

    /**
     * variable con el archivo de descarga del reporte
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CdestadospgastosfuncControlador
     */
    public CdestadospgastosfuncControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CDESTADOSPGASTOS_CONTROLADOR
                            .getCodigo();
            ano = SysmanFunciones.ano(new Date());
            validarPermisos();
            cargarListaAno();
            cargarListaCuentaInicial();
            cargarListaCuentaFinal();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            UrlBean urlListAno = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CdestadospgastosfuncControladorUrlEnum.URL2580
                                                            .getValue());
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListAno.getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(CdestadospgastosfuncControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CdestadospgastosfuncControladorUrlEnum.URL2581
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CdestadospgastosfuncControladorUrlEnum.URL2582
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), ano);
        param.put(CdestadospgastosfuncControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
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
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando53 en la vista
     *
     *
     */
    public void oprimirComando53()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
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
            reemplazos.put("ano", ano);
            reemplazos.put("mes", mes);
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);

            // Inicio Parametros Informe
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_DEPARTAMENTOCOMPANIA",
                            SysmanFunciones.nvl(
                                            SessionUtil.getCompaniaIngreso()
                                                            .getDepartamento(),
                                            "").toString());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_ANO", ano);
            parametros.put("PR_NOMMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE REPRESENTANTE LEGAL",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));
            parametros.put("PR_CEDULA_REPRESENTANTE_LEGAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CEDULA REPRESENTANTE LEGAL",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));
            parametros.put("PR_POLIZA_REPRESENTANTE_LEGAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "POLIZA REPRESENTANTE LEGAL",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));

            parametros.put("PR_VENCIMIENTO_POLIZA_REPRESENTANTE_LEGAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "VENCIMIENTO POLIZA REPRESENTANTE LEGAL",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));

            parametros.put("PR_ASEGURADORA_REPRESENTANTE_LEGAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "ASEGURADORA REPRESENTANTE LEGAL",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));

            parametros.put("PR_VALOR_ASEGURADO_REPRESENTANTE_LEGAL",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "VALOR ASEGURADO REPRESENTANTE LEGAL",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));

            parametros.put("PR_CARGO_PRESUPUESTO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO PRESUPUESTO",
                                            SessionUtil.getModulo(),
                                            new Date(),
                                            true));

            Reporteador.resuelveConsulta("001525CDESTADOSPGASTOSFUNC",
                            Integer.parseInt(
                                            SessionUtil.getModulo()),
                            reemplazos, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001525CDESTADOSPGASTOSFUNC",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno()
    {
        cargarListaCuentaInicial();
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
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
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial)
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public int getMes()
    {
        return mes;
    }

    public void setMes(int mes)
    {
        this.mes = mes;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

}
