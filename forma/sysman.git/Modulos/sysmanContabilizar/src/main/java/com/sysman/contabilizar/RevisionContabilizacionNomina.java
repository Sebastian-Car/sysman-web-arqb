/*-
 * RevisionContabilizacionNomina.java
 *
 * 1.0
 * 
 * 31 jul. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.enums.RevisionContabilizacionNominaUrlEnum;
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
import com.sysman.kernel.templates.annotations.Refactoring;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite revisar el proceso de contabilizar en nomina
 *
 * @version 1.0, 31/07/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class RevisionContabilizacionNomina extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private String compania;
    
    private String procesos;

    /**
     * Constante que almacena el numero del modulo
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Varibale que almacena el mes seleccionado en la vista
     */
    private String mes;
    /**
     * Varibale que almacena el anio seleccionado en la vista
     */
    private String anio;
    /**
     * Varibale que almacena el periodo seleccionado en la vista
     */
    private String periodo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Variable que almacena el titulo de la opcion de menu
     */
    private String tituloFormulario;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los meses por anio
     */
    private List<Registro> listaMes;
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
    /**
     * Lista que carga los periodos por mes
     */
    private List<Registro> listaPeriodo;
    
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCompania;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaProcesos;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RevisionContabilizacionNomina
     */
    public RevisionContabilizacionNomina() {
        super();
        //compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.REVISION_CONTABILIZACION_NOMINA
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCompania();
        cargarListaProcesos();
//        cargarListaMes();
//        cargarListaAnio();
//        cargarListaPeriodo();
        
        
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarTitulo();
        // </CODIGO_DESARROLLADO>
    }

    private void cargarTitulo() {

        String menu = SessionUtil.getMenuActual();

        switch (menu) {
        case "96030201":
            tituloFormulario = idioma.getString("TB_TB4319");
            break;
        case "96030202":
            tituloFormulario = idioma.getString("TB_TB4320");
            break;
        case "96030203":
            tituloFormulario = idioma.getString("TB_TB4321");
            break;
        case "96030204":
            tituloFormulario = idioma.getString("TB_TB4322");
            break;
        case "96030205":
            tituloFormulario = idioma.getString("TB_TB4322");
            break;
        default:
            tituloFormulario = "";
            break;
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        try {
            listaMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionContabilizacionNominaUrlEnum.URL5965
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Carga la lista listaCompania
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCompania(){
        try {
            listaCompania = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionContabilizacionNominaUrlEnum.URL5505
                                                                            .getValue())
                                            .getUrl(),
                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    // listaCompania = service.getListado(conectorPool, "SELECT CODIGO,NOMBRE FROM COMPANIA");
    }
    
    /**
     * 
     * Carga la lista listaProcesos
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaProcesos(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaProcesos = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionContabilizacionNominaUrlEnum.URL5506
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
     //listaProcesos = service.getListado(conectorPool, "SELECT ID_DE_PROCESO, NOMBRE_PROCESO FROM PROCESOS_DE_NOMINA");
    }

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionContabilizacionNominaUrlEnum.URL6393
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo
     *
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(GeneralParameterEnum.MES.getName(),
                        mes);

        try {
            listaPeriodo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RevisionContabilizacionNominaUrlEnum.URL6844
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL);
    }

    private void generarReporte(FORMATOS formato) {
        String reporte;

        reporte = seleccionarReporte();

        Map<String, Object> reemplazos = new TreeMap<>();
        reemplazos.put("compania", compania);
        reemplazos.put("companiaDs", SessionUtil.getCompania());
        reemplazos.put("ano", anio);
        reemplazos.put("mesFinal", mes);
        reemplazos.put("periodo", periodo);
        reemplazos.put("procesos", procesos);

        String strSql = Reporteador
                        .resuelveConsulta(reporte,
                                        Integer.parseInt(modulo),
                                        reemplazos);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, formato,
                            tituloFormulario);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String seleccionarReporte() {
        String reporte = null;

        String menu = SessionUtil.getMenuActual();

        switch (menu) {
        case "96030201":
            reporte = "800334CUENTASCONFNOEXISTENTESCONTABILIDAD";
            break;
        case "96030202":
            reporte = "800335CUENTASMAYORESCONFIGURASENINTERFAZ";
            break;
        case "96030203":
            reporte = "800336RESUMENNOMINAINTERFAZCUENTACONTABLE";
            break;
        case "96030204":
            reporte = "800337REVISIONCONFIGURACIONINTERFAZNOMINA";
            break;
        case "96030205":
            reporte = "800338TERCEROSNOEXISTENCONTABILIDAD";
            break;
        default:

            break;
        }

        return reporte;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Compania
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarCompania() {
        //<CODIGO_DESARROLLADO>
        procesos=null;
        anio=null;
        mes = null;
        periodo = null;
        cargarListaProcesos();
        cargarListaAnio();
       //</CODIGO_DESARROLLADO>
   }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    
    

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getProcesos() {
        return procesos;
    }

    public void setProcesos(String procesos) {
        this.procesos = procesos;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
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
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }
    
    /**
     * Retorna la lista listaCompania
     * 
     * @return listaCompania
     */
public List<Registro> getListaCompania() {
        return listaCompania;
    }
    /**
     * Asigna la lista listaCompania
     * 
     * @param listaCompania
     * Variable a asignar en  listaCompania
     */
public void setListaCompania(List<Registro> listaCompania) {
        this.listaCompania = listaCompania;
    }
    /**
     * Retorna la lista listaProcesos
     * 
     * @return listaProcesos
     */
public List<Registro> getListaProcesos() {
        return listaProcesos;
    }
    /**
     * Asigna la lista listaProcesos
     * 
     * @param listaProcesos
     * Variable a asignar en  listaProcesos
     */
public void setListaProcesos(List<Registro> listaProcesos) {
        this.listaProcesos = listaProcesos;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
