/*-
 * ConfiguracionParametrosControlador.java
 *
 * 1.0
 * 
 * 25/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ConfiguracionParametrosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbReportesRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que gestiona los valores de los parametros para
 * asignarlos a la consultas.
 *
 * @version 1.0, 25/05/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class ConfiguracionParametrosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consValFiltro;
    private final String consValFiltroDos;
    private final String consValFiltroTres;
    private final String consValFiltroCuatro;

    // <DECLARAR_ATRIBUTOS>
    private String nombreFiltro;
    private String modulo;
    private String reporte;
    private String nombreReporte;
    private String auxiliar;
    private String nombreEncabezados;

    private String nombreCampo1;
    private String nombreCampo2;
    private String nombreCampo3;
    private String nombreCampo4;
    /**
     * Atributo que almacena el numero de columnas del parametro
     */
    private String numeroColumnas;

    private String url;

    private boolean visibleCombo2;
    private boolean visibleCombo3;
    private boolean visibleCombo4;
    private boolean visibleCombo5;

    private int indice;

    private String grupo;
    private String codigo;
    private String secuencia;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaValorFiltro;
    private RegistroDataModelImpl listaValorFiltroTres;
    private RegistroDataModelImpl listaValorFiltroCuatro;

    private RegistroDataModelImpl listaValorFiltroE;
    private RegistroDataModelImpl listaValorFiltroTresE;
    private RegistroDataModelImpl listaValorFiltroCuatroE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbReportesRemote ejbReportes;

    /**
     * Crea una nueva instancia de ConfiguracionParametrosControlador
     */
    public ConfiguracionParametrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consValFiltro = "VALOR_FILTRO";
        consValFiltroDos = "VALOR_FILTRODOS";
        consValFiltroTres = "VALOR_FILTROTRES";
        consValFiltroCuatro = "VALOR_FILTROCUATRO";
        
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        visibleCombo2 = true;
        visibleCombo3 = true;
        visibleCombo4 = true;
        visibleCombo5 = true;
        try {
            if (parametrosEntrada != null) {

                reporte = SysmanFunciones.nvl(parametrosEntrada
                                .get("reporte"), "").toString();

                grupo = SysmanFunciones.nvl(parametrosEntrada
                                .get("grupo"), "").toString();

                codigo = SysmanFunciones.nvl(parametrosEntrada
                                .get("codigo"), "").toString();

                secuencia = SysmanFunciones.nvl(parametrosEntrada
                                .get("secuencia"), "").toString();

            }

            numFormulario = GeneralCodigoFormaEnum.CONFIGURACIONPARAMETROS_CONTROLADOR
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
        if ("9991104".equals(SessionUtil.getMenuActual())) {
            tabla = GenericUrlEnum.D_PARAMETROS_PLANOS.getTable();
        }
        else {
            urlConexionCache = UrlServiceCache.SYSMANIRISST;
            enumBase = GenericUrlEnum.D_PARAMETROSIMP;
        }
        buscarLlave();
        reasignarOrigen();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaValorFiltro
     *
     */
    public void cargarListaValorFiltro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaValorFiltro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaValorFiltro
     *
     */
    public void cargarListaValorFiltroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaValorFiltroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaValorFiltroTres
     *
     */
    public void cargarListaValorFiltroTres() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String[] vec = { GeneralParameterEnum.CODIGO.getName(),
                         GeneralParameterEnum.NOMBRE.getName() };

        listaValorFiltroTres = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vec);

    }

    /**
     * 
     * Carga la lista listaValorFiltroTres
     *
     */
    public void cargarListaValorFiltroTresE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String[] vec = { GeneralParameterEnum.CODIGO.getName(),
                         GeneralParameterEnum.NOMBRE.getName() };

        listaValorFiltroTresE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vec);

    }

    /**
     * 
     * Carga la lista listaValorFiltroCuatro
     *
     */
    public void cargarListaValorFiltroCuatro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String[] vec = { GeneralParameterEnum.CODIGO.getName(),
                         GeneralParameterEnum.NOMBRE.getName(), "EXPRESION1",
                         "EXPRESION2" };

        listaValorFiltroCuatro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vec);
    }

    /**
     * 
     * Carga la lista listaValorFiltroCuatro
     *
     */
    public void cargarListaValorFiltroCuatroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(url);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String[] vec = { GeneralParameterEnum.CODIGO.getName(),
                         GeneralParameterEnum.NOMBRE.getName(), "EXPRESION1",
                         "EXPRESION2" };

        listaValorFiltroCuatroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vec);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaValorFiltro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaValorFiltro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consValFiltro,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreFiltro = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilaValorFiltroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        " ")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaValorFiltroTres
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaValorFiltroTres(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consValFiltro,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreFiltro = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilaValorFiltroTresE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        " ")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaValorFiltroCuatro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaValorFiltroCuatro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consValFiltro,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreFiltro = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilaValorFiltroCuatroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        " ")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        try {
            archivoDescarga = null;
            String strSql = ejbReportes.resolverConsulta(compania, reporte,
                            SessionUtil.getUser().getCodigo());
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        nombreCampo1 = "Código";
        nombreCampo2 = "Nombre";
        nombreCampo3 = "Expresion 1";
        nombreCampo4 = "Expresion 2";

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCombos() {

        switch (numeroColumnas) {
        case "2":
            cargarListaValorFiltroE();
            cargarEncabezados(2);
            break;
        case "3":
            cargarListaValorFiltroTresE();
            cargarEncabezados(3);
            break;
        case "4":
            cargarListaValorFiltroCuatroE();
            cargarEncabezados(4);
            break;
        default:
            break;

        }

    }

    private void cargarEncabezados(int nColumnas) {

        String[] encabezados = nombreEncabezados.split(",");

        if (nColumnas == 2) {
            nombreCampo1 = encabezados[0];
            nombreCampo2 = encabezados[1];
        }
        else if (nColumnas == 3) {
            nombreCampo1 = encabezados[0];
            nombreCampo2 = encabezados[1];
            nombreCampo3 = encabezados[2];
        }
        else if (nColumnas == 4) {
            nombreCampo1 = encabezados[0];
            nombreCampo2 = encabezados[1];
            nombreCampo3 = encabezados[2];
            nombreCampo4 = encabezados[3];
        }
    }

    public void activarEdicion(Registro r) {
        Registro reg = r;
        numeroColumnas = SysmanFunciones
                        .nvl(reg.getCampos().get("N_COLUMNAS"), "0")
                        .toString();
        nombreEncabezados = SysmanFunciones
                        .nvl(reg.getCampos().get("NOMBRE_ENCABEZADOS"), ",")
                        .toString();
        url = SysmanFunciones
                        .nvl(reg.getCampos().get("URL"), "")
                        .toString();
        cargarCombosFila();
        cargarListaCombos();
    }

    private void cargarCombosFila() {
        switch (numeroColumnas) {
        case "0":
            visibleCombo2 = visibleCombo3 = visibleCombo4 = true;
            visibleCombo5 = false;
            break;
        case "2":
            visibleCombo2 = false;
            visibleCombo3 = visibleCombo4 = visibleCombo5 = true;
            break;
        case "3":
            visibleCombo3 = false;
            visibleCombo2 = visibleCombo4 = visibleCombo5 = true;
            break;
        case "4":
            visibleCombo2 = visibleCombo3 = visibleCombo5 = true;
            visibleCombo4 = false;
            break;
        default:
            break;
        }

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("N_COLUMNAS");
        registro.getCampos().remove("TIPO_PARAM");
        registro.getCampos().remove("TIPO");
        registro.getCampos().remove("URL");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("NOMBRE_ENCABEZADOS");
        registro.getCampos().remove("CODIGO_REPORTE");
        String aux = registro.getCampos().get(consValFiltroDos) != null
            ? registro.getCampos().get(consValFiltroDos).toString()
            : (registro.getCampos().get(consValFiltroTres) != null
                ? registro.getCampos().get(consValFiltroTres).toString()
                : registro.getCampos().get(consValFiltroCuatro) != null
                    ? registro.getCampos().get(consValFiltroCuatro).toString()
                    : "");

        registro.getCampos().remove(consValFiltroDos);
        registro.getCampos().remove(consValFiltroTres);
        registro.getCampos().remove(consValFiltroCuatro);
        registro.getCampos().put(consValFiltro, aux);
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void ejecutarrcCerrar() {
        Map<String, Object> param = new TreeMap<>();
        param.put("reporte", reporte);
        param.put("nombreReporte", nombreReporte);
        param.put("modulo", modulo);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        if ("9991104".equals(SessionUtil.getMenuActual())) {
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRESION_PLANOS_CONTROLADOR
                                            .getCodigo()));
        }
        else {
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRESIONREPORTES_CONTROLADOR
                                            .getCodigo()));
        }
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombreFiltro
     * 
     * @return nombreFiltro
     */
    public String getNombreFiltro() {
        return nombreFiltro;
    }

    /**
     * Asigna la variable nombreFiltro
     * 
     * @param nombreFiltro
     * Variable a asignar en nombreFiltro
     */
    public void setNombreFiltro(String nombreFiltro) {
        this.nombreFiltro = nombreFiltro;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaValorFiltro
     * 
     * @return listaValorFiltro
     */
    public RegistroDataModelImpl getListaValorFiltro() {
        return listaValorFiltro;
    }

    /**
     * Asigna la lista listaValorFiltro
     * 
     * @param listaValorFiltro
     * Variable a asignar en listaValorFiltro
     */
    public void setListaValorFiltro(RegistroDataModelImpl listaValorFiltro) {
        this.listaValorFiltro = listaValorFiltro;
    }

    /**
     * Retorna la lista listaValorFiltroTres
     * 
     * @return listaValorFiltroTres
     */
    public RegistroDataModelImpl getListaValorFiltroTres() {
        return listaValorFiltroTres;
    }

    /**
     * Asigna la lista listaValorFiltroTres
     * 
     * @param listaValorFiltroTres
     * Variable a asignar en listaValorFiltroTres
     */
    public void setListaValorFiltroTres(
        RegistroDataModelImpl listaValorFiltroTres) {
        this.listaValorFiltroTres = listaValorFiltroTres;
    }

    /**
     * Retorna la lista listaValorFiltroCuatro
     * 
     * @return listaValorFiltroCuatro
     */
    public RegistroDataModelImpl getListaValorFiltroCuatro() {
        return listaValorFiltroCuatro;
    }

    /**
     * Asigna la lista listaValorFiltroCuatro
     * 
     * @param listaValorFiltroCuatro
     * Variable a asignar en listaValorFiltroCuatro
     */
    public void setListaValorFiltroCuatro(
        RegistroDataModelImpl listaValorFiltroCuatro) {
        this.listaValorFiltroCuatro = listaValorFiltroCuatro;
    }

    public boolean isVisibleCombo2() {
        return visibleCombo2;
    }

    public void setVisibleCombo2(boolean visibleCombo2) {
        this.visibleCombo2 = visibleCombo2;
    }

    public boolean isVisibleCombo3() {
        return visibleCombo3;
    }

    public void setVisibleCombo3(boolean visibleCombo3) {
        this.visibleCombo3 = visibleCombo3;
    }

    public boolean isVisibleCombo4() {
        return visibleCombo4;
    }

    public void setVisibleCombo4(boolean visibleCombo4) {
        this.visibleCombo4 = visibleCombo4;
    }

    public boolean isVisibleCombo5() {
        return visibleCombo5;
    }

    public void setVisibleCombo5(boolean visibleCombo5) {
        this.visibleCombo5 = visibleCombo5;
    }

    public String getNombreCampo1() {
        return nombreCampo1;
    }

    public void setNombreCampo1(String nombreCampo1) {
        this.nombreCampo1 = nombreCampo1;
    }

    public String getNombreCampo2() {
        return nombreCampo2;
    }

    public void setNombreCampo2(String nombreCampo2) {
        this.nombreCampo2 = nombreCampo2;
    }

    public String getNombreCampo3() {
        return nombreCampo3;
    }

    public void setNombreCampo3(String nombreCampo3) {
        this.nombreCampo3 = nombreCampo3;
    }

    public String getNombreCampo4() {
        return nombreCampo4;
    }

    public void setNombreCampo4(String nombreCampo4) {
        this.nombreCampo4 = nombreCampo4;
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO

    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // METODO NO IMPLEMENTADO

    }

    @Override
    public void reasignarOrigen() {

        if ("9991104".equals(SessionUtil.getMenuActual())) {
            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosListado.put("GRUPO",
                            grupo);
            parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                            codigo);
            parametrosListado.put("SECUENCIA",
                            secuencia);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionParametrosControladorUrlEnum.URL2472
                                                            .getValue());

            urlActualizacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionParametrosControladorUrlEnum.URL5412
                                                            .getValue());
        }
        else {
            buscarUrls();
            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                            reporte);
            parametrosListado.put(GeneralParameterEnum.USUARIO.getName(),
                            SessionUtil.getUser().getCodigo());

        }

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaValorFiltroE() {
        return listaValorFiltroE;
    }

    public void setListaValorFiltroE(RegistroDataModelImpl listaValorFiltroE) {
        this.listaValorFiltroE = listaValorFiltroE;
    }

    public RegistroDataModelImpl getListaValorFiltroTresE() {
        return listaValorFiltroTresE;
    }

    public void setListaValorFiltroTresE(
        RegistroDataModelImpl listaValorFiltroTresE) {
        this.listaValorFiltroTresE = listaValorFiltroTresE;
    }

    public RegistroDataModelImpl getListaValorFiltroCuatroE() {
        return listaValorFiltroCuatroE;
    }

    public void setListaValorFiltroCuatroE(
        RegistroDataModelImpl listaValorFiltroCuatroE) {
        this.listaValorFiltroCuatroE = listaValorFiltroCuatroE;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
