/*-
 * DepreciacionPlacaAnuladaControlador.java
 *
 * 1.0
 * 
 * 08/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.DepreciacionPlacaAnuladaControladorEnum;
import com.sysman.almacen.enums.DepreciacionPlacaAnuladaControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Permite generar la relaci&oacute;n de los elementos devolutivos cuyas placas fueron anuladas. Se muestra en el informe: el elemento, la placa, tipo de movimiento y el n&uacute;mero del movimiento
 * de ingreso. Para generarlo debe establecerse un rango de elementos (Elemento inicial y final) de los cuales se evaluaran las placas anuladas.
 * 
 *
 * @version 1.0, 08/11/2017
 * @author jrodrigueza
 */
@ManagedBean
@ViewScoped
public class DepreciacionPlacaAnuladaControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Para especificar si se quiere visualizar el informe Agrupado o Detallado.
     */
    private int agrupadoPor;
    /**
     * Resumen por cuenta. (Deshabilitado)
     */
    private boolean resumenPorCuenta;
    /**
     * Permite filtrar por centro de costo inicial y final. (Deshabilitado)
     */
    private boolean porCentroCosto;
    /**
     * Lugar por el que se filtra.
     */
    private String filtroPor;
    /**
     * Centro de costo final.
     */
    private String centroCostoFinal;
    /**
     * Centro de costo inicial.
     */
    private String centroCostoInicial;
    /**
     * Elemento final.
     */
    private String elementoFinal;
    /**
     * Elemento inicial.
     */
    private String elementoInicial;
    /**
     * A&ntilde;o para el cual se va a generar el informe.
     */
    private int ano;
    /**
     * Mes para el cual se va a agenerar el informe.
     */
    private int mes;
    /**
     * Descripci&oacute;n del elemento inicial seleccionado.
     */
    private String textoElementoInicial;
    /**
     * Descripci&oacute;n del elemento final seleccionado.
     */
    private String textoElementoFinal;
    /**
     * Descripci&oacute;n del centro de costo inicial selecconado.
     */
    private String textoCentroCostoInicial;
    /**
     * Descripci&oacute;n del centro de costo final selecconado.
     */
    private String textoCentroCostoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Determina si el check "Por Centro de Costo" se debe mostrar/ocultar.
     */
    private boolean visibleCentroCosto;
    /**
     * Ejb para el manejo de funciones y procedimientos del paquete PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>
    /**
     * Campo CODIGO.
     */
    private static final String CAMPO_CODIGO = GeneralParameterEnum.CODIGO
                    .getName();
    /**
     * Campo CODIGELEMENTO
     */
    private static final String CAMPO_CODIGOELEMENTO = GeneralParameterEnum.CODIGOELEMENTO
                    .getName();
    /**
     * Campo NOMBRE
     */
    private static final String CAMPO_NOMBRE = GeneralParameterEnum.NOMBRE
                    .getName();
    /**
     * Opci&oacute;n de filtro Consolidado.
     */
    private static final String CONSOLIDADO = "Consolidado";
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    /**
     * Listado de filtros.
     */
    private List<Registro> listaFiltradoPor;
    /**
     * Listado de a&ntilde;os.
     */
    private List<Registro> listaAno;
    /**
     * Listado de meses.
     */
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de centro de costos finales.
     */
    private RegistroDataModelImpl listaCentroCostoFinal;
    /**
     * Listado de centro de costos iniciales.
     */
    private RegistroDataModelImpl listaCentroCostoInicial;
    /**
     * Listado de elementos finales.
     */
    private RegistroDataModelImpl listaElementoFinal;
    /**
     * Listado de elementos iniciales.
     */
    private RegistroDataModelImpl listaElementoInicial;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de DepreciacionPlacaAnuladaControlador
     */
    public DepreciacionPlacaAnuladaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DEPRECIACION_PLACA_ANULADA
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
        cargarListaFiltradoPor();
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaElementoInicial();
        cargarListaElementoFinal();
        cargarListaCentroCostoInicial();
        cargarListaCentroCostoFinal();
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
        agrupadoPor = 1;

        /*
         * Check "Por Centro de Costo" no se carga, pero se conserva la validación de visibilidad
         */
        String parametro = "GENERAR RELACION DE DEPRECIACIONES POR CENTRO DE COSTO";
        visibleCentroCosto = "SI".equals(getParametro(parametro, "NO"));

        ano = Calendar.getInstance().get(Calendar.YEAR);
        mes = Calendar.getInstance().get(Calendar.MONTH) + 1;
        filtroPor = CONSOLIDADO;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaFiltradoPor
     */
    public void cargarListaFiltradoPor()
    {
        listaFiltradoPor = new ArrayList<>();
        listaFiltradoPor.add(new Registro(5, crearFila(CONSOLIDADO)));
        listaFiltradoPor.add(new Registro(6, crearFila("Consolidado NIIF")));
    }

    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String urlEnumId = DepreciacionPlacaAnuladaControladorUrlEnum.URL8508
                        .getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(url, param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaMes
     */
    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        String urlEnumId = DepreciacionPlacaAnuladaControladorUrlEnum.URL8996
                        .getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(url, param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaCentroCostoFinal
     */
    public void cargarListaCentroCostoFinal()
    {
        String urlEnumId = DepreciacionPlacaAnuladaControladorUrlEnum.URL9428
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DepreciacionPlacaAnuladaControladorEnum.CODIGOINICIAL
                        .getValue(), centroCostoInicial);

        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     * Carga la lista listaCentroCostoInicial
     */
    public void cargarListaCentroCostoInicial()
    {
        String urlEnumId = DepreciacionPlacaAnuladaControladorUrlEnum.URL10184
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     * Carga la lista listaElementoFinal
     */
    public void cargarListaElementoFinal()
    {
        String urlEnumId = DepreciacionPlacaAnuladaControladorUrlEnum.URL10851
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "D,N,E");
        param.put(DepreciacionPlacaAnuladaControladorEnum.ELEMENTOINICIAL
                        .getValue(),
                        elementoInicial);

        listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGOELEMENTO);
    }

    /**
     * Carga la lista listaElementoInicial
     */
    public void cargarListaElementoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciacionPlacaAnuladaControladorUrlEnum.URL11633
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "D,N,E");

        listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGOELEMENTO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton ImprimirExcel en la vista
     */
    public void oprimirImprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton ImprimirPDF en la vista
     */
    public void oprimirImprimirPDF()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera el informe para el formato pasado por par&aacute;metro.
     * 
     * @param formato
     */
    private void generarInforme(FORMATOS formato)
    {
        if (faltanCamposObligatorios())
        {
            return;
        }
        String agrupacion = getParametro("DIGITOS AGRUPACION INVENTARIO", "3");
        Calendar calendar = Calendar.getInstance();
        calendar.set(ano, mes - 1, 1, 0, 0, 0);
        String primerDia = null;
        String ultimoDia = null;
        try
        {
            primerDia = SysmanFunciones
                            .convertirAFechaCadena(calendar.getTime());
            ultimoDia = SysmanFunciones
                            .convertirAFechaCadena(
                                            ultimoDia(calendar.getTime()));
        }
        catch (ParseException e)
        {
            Logger.getLogger(
                            DepreciacionPlacaAnuladaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        String parametro = getParametro(
                        "MANEJA INTERFACE ALMACEN MENSUAL POR CENTRO COSTO",
                        "NO");
        String tablaInventario = "NO".equalsIgnoreCase(parametro)
                        ? "INVENTARIOCONTABILIDAD"
                        : "INVENTARIOCONTABILIDADCC";

        generarInformePlacaAnulada(agrupacion, primerDia,
                        ultimoDia, tablaInventario, formato);
    }

    /**
     * Genera informe con la relaci&oacute;n de depreciaciones por placa anulada.
     * 
     * @param agrupacion
     * tipo de agrupaci&oacute;n
     * @param primerDia
     * primer d&iacute;a seg&uacute;n el a&ntilde;o y mes seleccionado
     * @param ultimoDia
     * &uacute;ltimo d&iacute;a seg&uacute;n el a&ntilde;o y mes seleccionado
     * @param formato
     * formato del informe
     */
    private void generarInformePlacaAnulada(String agrupacion, String primerDia,
                    String ultimoDia, String tablaInventario, FORMATOS formato)
    {
        String reporte = filtroPor.equals(CONSOLIDADO) ? "001479IDepreciarPlacaAnulada" : "001982DepreciarPlacaAnuladaNIIF";

        // Reemplazos de consulta
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("agrupacion", agrupacion);
        reemplazos.put("elementoInicial", elementoInicial);
        reemplazos.put("elementoFinal", elementoFinal);
        reemplazos.put("primerDia", primerDia);
        reemplazos.put("ultimoDia", ultimoDia);
        reemplazos.put("tablaInventario", tablaInventario);

        // Parametros de reporte
        Map<String, Object> parametros = new HashMap<>();
        String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes];
        parametros.put("PR_ANO", String.valueOf(ano));
        parametros.put("PR_MES", nombreMes.toUpperCase());
        parametros.put("PR_FILTRADOPOR", filtroPor.toUpperCase());
        parametros.put("PR_NOMBRE_CONTADOR",
                        getParametro("NOMBRE CONTADOR", ""));
        parametros.put("PR_ALMACENISTA", getParametro("ALMACENISTA", ""));
        parametros.put("PR_AGRUPADO", agrupadoPor);

        generarArchivoDescarga(reporte, reporte, formato, reemplazos,
                        parametros);
    }

    /**
     * Genera el archivo de descarga
     * 
     * @param reporte
     * nombre del reporte
     * @param consulta
     * nombre de la consulta
     * @param formato
     * formato con el que se va a generar
     * @param reemplazos
     * reemplazos de consulta
     * @param parametros
     * par&aacute;metros de reporte
     */
    private void generarArchivoDescarga(String reporte, String consulta,
                    FORMATOS formato,
                    Map<String, Object> reemplazos,
                    Map<String, Object> parametros)
    {
        int moduloReal = Integer.parseInt(SessionUtil.getModulo());
        Reporteador.resuelveConsulta(consulta, moduloReal, reemplazos,
                        parametros);
        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     */
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        setMes(0);
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorCentroCosto
     */
    public void cambiarPorCentroCosto()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCentroCostoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = SysmanFunciones
                        .toString(registroAux.getCampos().get(CAMPO_CODIGO));
        textoCentroCostoFinal = SysmanFunciones.toString(registroAux.getCampos()
                        .get(CAMPO_NOMBRE));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCentroCostoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = SysmanFunciones
                        .toString(registroAux.getCampos().get(CAMPO_CODIGO));
        textoCentroCostoInicial = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(CAMPO_NOMBRE));
        cargarListaCentroCostoFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaElementoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = SysmanFunciones.toString(registroAux.getCampos()
                        .get(CAMPO_CODIGOELEMENTO));
        textoElementoFinal = SysmanFunciones.toString(registroAux.getCampos()
                        .get("NOMBRELARGO"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaElementoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = SysmanFunciones.toString(registroAux.getCampos()
                        .get(CAMPO_CODIGOELEMENTO));
        textoElementoInicial = SysmanFunciones.toString(registroAux.getCampos()
                        .get("NOMBRELARGO"));
        cargarListaElementoFinal();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable agrupadoPor
     * 
     * @return agrupadoPor
     */
    public int getAgrupadoPor()
    {
        return agrupadoPor;
    }

    /**
     * Asigna la variable agrupadoPor
     * 
     * @param agrupadoPor
     * Variable a asignar en agrupadoPor
     */
    public void setAgrupadoPor(int agrupadoPor)
    {
        this.agrupadoPor = agrupadoPor;
    }

    /**
     * Retorna la variable resumenPorCuenta
     * 
     * @return resumenPorCuenta
     */
    public boolean getResumenPorCuenta()
    {
        return resumenPorCuenta;
    }

    /**
     * Asigna la variable resumenPorCuenta
     * 
     * @param resumenPorCuenta
     * Variable a asignar en resumenPorCuenta
     */
    public void setResumenPorCuenta(boolean resumenPorCuenta)
    {
        this.resumenPorCuenta = resumenPorCuenta;
    }

    /**
     * Retorna la variable porCentroCosto
     * 
     * @return porCentroCosto
     */
    public boolean getPorCentroCosto()
    {
        return porCentroCosto;
    }

    /**
     * Asigna la variable porCentroCosto
     * 
     * @param porCentroCosto
     * Variable a asignar en porCentroCosto
     */
    public void setPorCentroCosto(boolean porCentroCosto)
    {
        this.porCentroCosto = porCentroCosto;
    }

    /**
     * Retorna la variable filtroPor
     * 
     * @return filtroPor
     */
    public String getFiltroPor()
    {
        return filtroPor;
    }

    /**
     * Asigna la variable filtroPor
     * 
     * @param filtroPor
     * Variable a asignar en filtroPor
     */
    public void setFiltroPor(String filtroPor)
    {
        this.filtroPor = filtroPor;
    }

    /**
     * Retorna la variable centroCostoFinal
     * 
     * @return centroCostoFinal
     */
    public String getCentroCostoFinal()
    {
        return centroCostoFinal;
    }

    /**
     * Asigna la variable centroCostoFinal
     * 
     * @param centroCostoFinal
     * Variable a asignar en centroCostoFinal
     */
    public void setCentroCostoFinal(String centroCostoFinal)
    {
        this.centroCostoFinal = centroCostoFinal;
    }

    /**
     * Retorna la variable centroCostoInicial
     * 
     * @return centroCostoInicial
     */
    public String getCentroCostoInicial()
    {
        return centroCostoInicial;
    }

    /**
     * Asigna la variable centroCostoInicial
     * 
     * @param centroCostoInicial
     * Variable a asignar en centroCostoInicial
     */
    public void setCentroCostoInicial(String centroCostoInicial)
    {
        this.centroCostoInicial = centroCostoInicial;
    }

    /**
     * Retorna la variable elementoFinal
     * 
     * @return elementoFinal
     */
    public String getElementoFinal()
    {
        return elementoFinal;
    }

    /**
     * Asigna la variable elementoFinal
     * 
     * @param elementoFinal
     * Variable a asignar en elementoFinal
     */
    public void setElementoFinal(String elementoFinal)
    {
        this.elementoFinal = elementoFinal;
    }

    /**
     * Retorna la variable elementoInicial
     * 
     * @return elementoInicial
     */
    public String getElementoInicial()
    {
        return elementoInicial;
    }

    /**
     * Asigna la variable elementoInicial
     * 
     * @param elementoInicial
     * Variable a asignar en elementoInicial
     */
    public void setElementoInicial(String elementoInicial)
    {
        this.elementoInicial = elementoInicial;
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
     * @return the textoElementoInicial
     */
    public String getTextoElementoInicial()
    {
        return textoElementoInicial;
    }

    /**
     * @param textoElementoInicial
     * the textoElementoInicial to set
     */
    public void setTextoElementoInicial(String textoElementoInicial)
    {
        this.textoElementoInicial = textoElementoInicial;
    }

    /**
     * @return the textoElementoFinal
     */
    public String getTextoElementoFinal()
    {
        return textoElementoFinal;
    }

    /**
     * @param textoElementoFinal
     * the textoElementoFinal to set
     */
    public void setTextoElementoFinal(String textoElementoFinal)
    {
        this.textoElementoFinal = textoElementoFinal;
    }

    /**
     * @return the textoCentroCostoInicial
     */
    public String getTextoCentroCostoInicial()
    {
        return textoCentroCostoInicial;
    }

    /**
     * @param textoCentroCostoInicial
     * the textoCentroCostoInicial to set
     */
    public void setTextoCentroCostoInicial(String textoCentroCostoInicial)
    {
        this.textoCentroCostoInicial = textoCentroCostoInicial;
    }

    /**
     * @return the textoCentroCostoFinal
     */
    public String getTextoCentroCostoFinal()
    {
        return textoCentroCostoFinal;
    }

    /**
     * @param textoCentroCostoFinal
     * the textoCentroCostoFinal to set
     */
    public void setTextoCentroCostoFinal(String textoCentroCostoFinal)
    {
        this.textoCentroCostoFinal = textoCentroCostoFinal;
    }

    /**
     * @return the visibleCentroCosto
     */
    public boolean isVisibleCentroCosto()
    {
        return visibleCentroCosto;
    }

    /**
     * @param visibleCentroCosto
     * the visibleCentroCosto to set
     */
    public void setVisibleCentroCosto(boolean visibleCentroCosto)
    {
        this.visibleCentroCosto = visibleCentroCosto;
    }
    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaFiltradoPor
     * 
     * @return listaFiltradoPor
     */
    public List<Registro> getListaFiltradoPor()
    {
        return listaFiltradoPor;
    }

    /**
     * Asigna la lista listaFiltradoPor
     * 
     * @param listaFiltradoPor
     * Variable a asignar en listaFiltradoPor
     */
    public void setListaFiltradoPor(List<Registro> listaFiltradoPor)
    {
        this.listaFiltradoPor = listaFiltradoPor;
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
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }
    // </SET_GET_LISTAS>

    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCentroCostoFinal
     * 
     * @return listaCentroCostoFinal
     */
    public RegistroDataModelImpl getListaCentroCostoFinal()
    {
        return listaCentroCostoFinal;
    }

    /**
     * Asigna la lista listaCentroCostoFinal
     * 
     * @param listaCentroCostoFinal
     * Variable a asignar en listaCentroCostoFinal
     */
    public void setListaCentroCostoFinal(
                    RegistroDataModelImpl listaCentroCostoFinal)
    {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
    }

    /**
     * Retorna la lista listaCentroCostoInicial
     * 
     * @return listaCentroCostoInicial
     */
    public RegistroDataModelImpl getListaCentroCostoInicial()
    {
        return listaCentroCostoInicial;
    }

    /**
     * Asigna la lista listaCentroCostoInicial
     * 
     * @param listaCentroCostoInicial
     * Variable a asignar en listaCentroCostoInicial
     */
    public void setListaCentroCostoInicial(
                    RegistroDataModelImpl listaCentroCostoInicial)
    {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    /**
     * Retorna la lista listaElementoFinal
     * 
     * @return listaElementoFinal
     */
    public RegistroDataModelImpl getListaElementoFinal()
    {
        return listaElementoFinal;
    }

    /**
     * Asigna la lista listaElementoFinal
     * 
     * @param listaElementoFinal
     * Variable a asignar en listaElementoFinal
     */
    public void setListaElementoFinal(
                    RegistroDataModelImpl listaElementoFinal)
    {
        this.listaElementoFinal = listaElementoFinal;
    }

    /**
     * Retorna la lista listaElementoInicial
     * 
     * @return listaElementoInicial
     */
    public RegistroDataModelImpl getListaElementoInicial()
    {
        return listaElementoInicial;
    }

    /**
     * Asigna la lista listaElementoInicial
     * 
     * @param listaElementoInicial
     * Variable a asignar en listaElementoInicial
     */
    public void setListaElementoInicial(
                    RegistroDataModelImpl listaElementoInicial)
    {
        this.listaElementoInicial = listaElementoInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    // <METODOS_ADICIONALES>
    /**
     * Trae el valor almacenado en la base de datos para el parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Crea un map con el valor pasado por parámetro con las claves CODIGO y NOMBRE.
     * 
     * @param valor
     * @return campos para el registro
     */
    private Map<String, Object> crearFila(String valor)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put(CAMPO_CODIGO, valor);
        map.put(CAMPO_NOMBRE, valor);
        return map;
    }

    /**
     * Verifica si faltan campos obligatorios por diligenciar.
     * 
     * @return verdadero si faltan campos obligatorios.
     */
    private boolean faltanCamposObligatorios()
    {
        if (faltanCamposBasicos())
        {
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(elementoInicial)
                        || SysmanFunciones.validarVariableVacio(elementoFinal))
        {
            JsfUtil.agregarMensajeInformativo(
                            "Por favor seleccione un elemento inicial y final.");
            return true;
        }
        if (porCentroCosto
                        && (SysmanFunciones.validarVariableVacio(centroCostoInicial)
                                        || SysmanFunciones.validarVariableVacio(centroCostoFinal)))
        {
            JsfUtil.agregarMensajeInformativo(
                            "Por favor seleccione un centro de costo inicial y final.");
            return true;
        }
        return false;
    }

    /**
     * Verifica si el a&ntilde;o, mes y filtro fueron diligenciados.
     * 
     * @return verdadero si faltan campos obligatorios.
     */
    private boolean faltanCamposBasicos()
    {
        if (ano < 0 || String.valueOf(ano).length() < 4)
        {
            JsfUtil.agregarMensajeInformativo(
                            "Por favor seleccione un año valido.");
            return true;
        }
        if (mes < 0)
        {
            JsfUtil.agregarMensajeInformativo(
                            "Por favor seleccione un mes valido.");
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(filtroPor))
        {
            JsfUtil.agregarMensajeInformativo(
                            "Por favor seleccione un filtro.");
            return true;
        }
        return false;
    }

    /**
     * Trae una fecha que representa el último día del mes y el año de la fecha ingresada por parámetro.
     *
     * @param fecha
     * @return Objeto tipo <code>Date</code> representando el último día.
     * @author jrodrigueza
     */
    public static Date ultimoDia(Date fecha)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.DAY_OF_MONTH,
                        calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
    // </METODOS_ADICIONALES>
}
