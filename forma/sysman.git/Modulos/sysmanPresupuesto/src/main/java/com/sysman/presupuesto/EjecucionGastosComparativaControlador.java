/*-
 * EjecucionGastosComparativaControlador.java
 *
 * 1.0
 * 
 * 10/09/2018
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionGastosComparativaControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario utilizado para la ejecucion de gastos comparativos y generacion de informe
 *
 * @version 1.0, 10/09/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class EjecucionGastosComparativaControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena la cuenta inicial
     */
    private String cuentaInicial;
    /**
     * variable que almacena la cuenta final
     */
    private String cuentaFinal;
    /**
     * variable que almacena mes
     */
    private int mes;
    /**
     * variable que almacena ano
     */
    private int ano;
    /**
     * variable que almacena el reporte
     */
    private String reporte;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista los meses
     */
    private List<Registro> listaMes;
    /**
     * lista los años
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista las cuentas iniciales
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * lista las cuentas finales
     */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EjecucionGastosComparativaControlador
     */
    public EjecucionGastosComparativaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_GASTOS_COMPARATIVA_CONTROLADOR.getCodigo();
            validarPermisos();

        }
        catch (Exception ex)
        {
            Logger.getLogger(AimregistroejecucgastosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaAno();
        ano = SysmanFunciones.ano(new Date());
        cargarListaMes();
        mes = SysmanFunciones.mes(new Date());
        cargarListaCuentaInicial();
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
    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", ano);

        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionGastosComparativaControladorUrlEnum.URL0016
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
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionGastosComparativaControladorUrlEnum.URL0007
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
     * Carga la lista listaCuentaInicial y listaCuentaFinal
     *
     */
    public void cargarListaCuentaInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EjecucionGastosComparativaControladorUrlEnum.URL0036.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("ANO", ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "ID");
    }

    public void cargarListaCuentaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EjecucionGastosComparativaControladorUrlEnum.URL0034.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("ANO", ano);
        param.put("CUENTAINICIAL", cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "ID");
    }

    /**
     * 
     * Metodos ejecutados al oprimir el boton PDF o Excel en la vista
     */
    public void oprimirPDF()
    {

        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);

    }

    public void oprimirExcel()
    {

        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL);
    }

    /**
     * metodo que contiene la logica para genera los reportes en formato pdf y excel
     *
     * @param formato
     */

    // </CODIGO_DESARROLLADO>

    public void generaInforme(ReportesBean.FORMATOS formato)
    {

        try
        {
            reporte = formato.equals(FORMATOS.PDF)
                            ? "001904Ejec_Gastos_comparativa"
                            : "001904Ejec_Gastos_comparativa_Excel";
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", ano);
            reemplazar.put("mes", mes);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ANO", ano);
            parametros.put("PR_MES", mes);

            if (formato.equals(FORMATOS.PDF))
            {
                Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else
            {
                reemplazar.put("consultaBase", Reporteador.resuelveConsulta(
                                "001904Ejec_Gastos_comparativa",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar));
                String salida = Reporteador.resuelveConsulta(
                                reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar);
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(salida, ConectorPool.ESQUEMA_SYSMAN, formato,
                                "EjecucionGastosComparativo");
            }

        }
        catch (JRException | IOException | SysmanException | SQLException | DRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodos ejecutados al seleccionar una fila de la lista listaCuentaInicial, FilaCuentaFinal, FilaAno y FilaMes
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    public void cambiarAno()
    {
        cargarListaMes();
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
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
     * Retorna la variable mes
     * 
     * @return mes
     */
    public int getMes()
    {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(int mes)
    {
        this.mes = mes;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public int getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getReporte()
    {
        return reporte;
    }

    public void setReporte(String reporte)
    {
        this.reporte = reporte;
    }

    public String getCompania()
    {
        return compania;
    }

    public String getModulo()
    {
        return modulo;
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
    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }

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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
