/*-
 * DevolutivospornombreControlador.java
 *
 * 1.0
 * 
 * 26/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.DevolutivospornombreControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite obtener el formulario Devolutivos por Nombre del
 * modulo de almacen
 *
 * @version 1.0, 26/01/2017
 * @author acaceres
 * 
 * @author jlramirez
 * @version 2, 27/04/2017, se realizó refactoring y manejo de EJBs
 */
@ManagedBean
@ViewScoped
public class DevolutivospornombreControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el numero del modulo en
     * el cual inicia sesion el usuario, el valor de esta constante
     * esta asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;

    /**
     * Constante que almacenara la cadena "CODIGOELEMENTO"
     */
    private final String codigoElementoC;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacenara el valor que el usuario seleccione en
     * el check de responsabilidades
     */
    private boolean responsabilidades;

    /**
     * Atributo que almacenara el elemento inicial que seleccione el
     * usuario
     */
    private String elementoDesde;

    /**
     * Atributo que almacenara el elemento final que seleccione el
     * usuario
     */
    private String elementoHasta;

    /**
     * Atributo que almacenara el nombre del elemento inicial que
     * seleccione el usuario
     */
    private String nombreElementoDesde;

    /**
     * Atributo que almacenara el nombre del elemento final que
     * seleccione el usuario
     */
    private String nombreElementoHasta;

    /**
     * Atributo que almacenara la fecha que seleccionara el usuario
     */
    private Date fechaInicial;

    /**
     * Atributo que permite volver visible el campo de la fecha
     */
    private boolean fechaInicialVisible;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena el valor del parametro
     * "DEFENSA CIVIL MANEJA NUEVAS COLUMNAS"
     */
    private String parDefensaCivil;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Registro que contiene la lista inicial de elementos desde los
     * cuales el usuario puede filtrar
     */
    private RegistroDataModelImpl listacmbElementoDesde;

    /**
     * Registro que contiene la lista final de elementos desde los
     * cuales el usuario puede filtrar
     */
    private RegistroDataModelImpl listacmbElementoHasta;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de DevolutivospornombreControlador
     */
    public DevolutivospornombreControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoElementoC = "CODIGOELEMENTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOSPORNOMBRE_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
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
        try {
            parDefensaCivil = sysmanUtil.consultarParametro(compania,
                            "DEFENSA CIVIL MANEJA NUEVAS COLUMNAS",
                            modulo, new Date(), true);
            fechaInicial = new Date();

            if ("SI".equals(parDefensaCivil)) {
                fechaInicialVisible = true;
            }
            else {
                fechaInicialVisible = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbElementoDesde
     *
     */
    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivospornombreControladorUrlEnum.URL8041
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoC);
    }

    /**
     * 
     * Carga la lista listacmbElementoHasta
     *
     */
    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivospornombreControladorUrlEnum.URL8866
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), elementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoC);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla en la vista,
     * genera el informe el formato pdf
     *
     *
     */
    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @param formatos
     */
    public void obtenerReporteDefCiv(FORMATOS formatos) {

        String condBodegas = cargarConsultas();
        String parametroFecha;
        String reporte = "001371DevolutivosPorNombreDefCiv";

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("elementoInicial", elementoDesde);
            reemplazar.put("elementoFinal", elementoHasta);
            reemplazar.put("fecha",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("condBodegas", condBodegas);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            parametroFecha = "A "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial,
                                "EEEEE dd MMMMM yyyy")
                + "";
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAINICIAL", parametroFecha);
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void obtenerDevolutivosNombre(FORMATOS formatos) {
        String reporte = "001372DevolutivosPorNombre";

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("elementoInicial", elementoDesde);
            reemplazar.put("elementoFinal", elementoHasta);
            reemplazar.put("fecha", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo usado para obtener el valor de los parametros y las
     * condiciones que se van a enviar para generar el reporte
     * 
     * @return condBodegas: condicion que se incluye en la consulta
     * para generar el reporte
     */
    public String cargarConsultas() {
        String condBodegas = "";
        String bodegasRespo;
        String bodegasInser;

        if (responsabilidades) {
            try {
                bodegasRespo = sysmanUtil.consultarParametro(compania,
                                "RESPONSABILIDADES", modulo,
                                new Date(), true);

                bodegasInser = sysmanUtil.consultarParametro(compania,
                                "BODEGA INSERVIBLES",
                                modulo, new Date(), true);

                if (!bodegasRespo.isEmpty() && !bodegasInser.isEmpty()) {
                    condBodegas = "  AND MOVINVENTARIO.DEPENDENCIA NOT IN ('"
                        + bodegasRespo + "','" + bodegasInser + "') ";
                }
                else if (!bodegasRespo.isEmpty() && bodegasInser.isEmpty()) {
                    condBodegas = " AND MOVINVENTARIO.DEPENDENCIA NOT IN ('"
                        + bodegasRespo + "')";
                }
                else if (bodegasRespo.isEmpty() && !bodegasInser.isEmpty()) {
                    condBodegas = "AND MOVINVENTARIO.DEPENDENCIA NOT IN ('"
                        + bodegasInser + "')";
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return condBodegas;
    }

    /**
     * Define el reporte a generar dependiendo el valor del parametro
     * "DEFENSA CIVIL MANEJA NUEVAS COLUMNA"
     * 
     * @param formato
     * Formato en el que se desea generar el reporte
     */
    private void generarReporte(FORMATOS formato) {

        if ("SI".equals(parDefensaCivil)) {
            obtenerReporteDefCiv(formato);
        }
        else {
            obtenerDevolutivosNombre(formato);
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista, genera
     * el informe en formato excel
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbElementoDesde
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreElementoDesde = null;
        nombreElementoHasta = null;
        elementoHasta = null;
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoElementoC), "")
                        .toString();
        nombreElementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        cargarListacmbElementoHasta();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbElementoHasta
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = null;
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoElementoC), "")
                        .toString();
        nombreElementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    /**
     * Retorna la variable elementoDesde
     * 
     * @return elementoDesde
     */
    public String getElementoDesde() {
        return elementoDesde;
    }

    public boolean isResponsabilidades() {
        return responsabilidades;
    }

    public void setResponsabilidades(boolean responsabilidades) {
        this.responsabilidades = responsabilidades;
    }

    /**
     * Asigna la variable elementoDesde
     * 
     * @param elementoDesde
     * Variable a asignar en elementoDesde
     */
    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    /**
     * Retorna la variable elementoHasta
     * 
     * @return elementoHasta
     */
    public String getElementoHasta() {
        return elementoHasta;
    }

    /**
     * Asigna la variable elementoHasta
     * 
     * @param elementoHasta
     * Variable a asignar en elementoHasta
     */
    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    /**
     * Retorna la variable nombreElementoDesde
     * 
     * @return nombreElementoDesde
     */
    public String getNombreElementoDesde() {
        return nombreElementoDesde;
    }

    /**
     * Asigna la variable nombreElementoDesde
     * 
     * @param nombreElementoDesde
     * Variable a asignar en nombreElementoDesde
     */
    public void setNombreElementoDesde(String nombreElementoDesde) {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    /**
     * Retorna la variable nombreElementoHasta
     * 
     * @return nombreElementoHasta
     */
    public String getNombreElementoHasta() {
        return nombreElementoHasta;
    }

    /**
     * Asigna la variable nombreElementoHasta
     * 
     * @param nombreElementoHasta
     * Variable a asignar en nombreElementoHasta
     */
    public void setNombreElementoHasta(String nombreElementoHasta) {
        this.nombreElementoHasta = nombreElementoHasta;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listacmbElementoDesde
     * 
     * @return listacmbElementoDesde
     */
    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public boolean isFechaInicialVisible() {
        return fechaInicialVisible;
    }

    public void setFechaInicialVisible(boolean fechaInicialVisible) {
        this.fechaInicialVisible = fechaInicialVisible;
    }

    /**
     * Asigna la lista listacmbElementoDesde
     * 
     * @param listacmbElementoDesde
     * Variable a asignar en listacmbElementoDesde
     */
    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    /**
     * Retorna la lista listacmbElementoHasta
     * 
     * @return listacmbElementoHasta
     */
    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    /**
     * Asigna la lista listacmbElementoHasta
     * 
     * @param listacmbElementoHasta
     * Variable a asignar en listacmbElementoHasta
     */
    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }
}
