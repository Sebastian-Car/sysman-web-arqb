/*-
 * RegistroIngresosControlador.java
 *
 * 1.0
 *
 * 08/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.RegistroIngresosControladorEnum;
import com.sysman.cgr.enums.RegistroIngresosControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Controlador del formulario 'Libro registro de ingresos' asociado a
 * la forma registroingresos.
 *
 * @version 1.0, 08/03/2017
 * @author pespitia
 * @version 2.0 16/08/2017
 * @modifiedby jrodriguezr Se elimina la conexion y se ajusta el
 * manejo de excepciones
 * @version 3, 30/08/2017
 * @modifiedby <strong>jrodriguezr </strong>Se refactoriza el código
 * SQL de las listas para utilizar DSS. También los llamados a
 * funciones, procedimientos y métodos de la clase Acciones a llamados
 * a EJB. Textos al archivo properties. Cambio el numero del
 * formulario al enumerado.
 */
@ManagedBean
@ViewScoped
public class RegistroIngresosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual inicio sesion el usuario, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;

    /** Constante a nivel de clase que aloja la cadena CODIGO */
    private final String cCodigo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que gestiona el valor del check 'Mostrar Codigo
     * Interno'
     */
    private boolean ckIndicador;

    /**
     * Atributo que gestiona el valor seleccionado en el combo 'Cuenta
     * Inicial'
     */
    private String cuentaInicial;

    /**
     * Atributo que gestiona el valor seleccionado en el combo 'Cuenta
     * Final'
     */
    private String cuentaFinal;

    /**
     * Atributo que gestiona el valor seleccionado en el combo 'Mes
     * Inicial'
     */
    private String mesInicial;

    /**
     * Atributo que gestiona el valor seleccionado en el combo 'Mes
     * Final'
     */
    private String mesFinal;

    /**
     * Atributo que gestiona el valor seleccionado en el combo 'Ano'
     */
    private int anio;

    /** Atributo que gestiona el valor asignado en el campo 'Nivel' */
    private String nivel;

    /**
     * Atributo que controla la visibilidad del campo 'SECCION',
     * 'UNIDAD EJECUTORA' y 'REGIONAL'
     */
    private boolean visibleSeccion;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>

    /**
     * Atributo que aloja el valor del parametro 'SECCION 036'
     * asignado en la base de datos
     */
    private String parSeccion036;

    /**
     * Atributo que aloja el valor del parametro 'UNIDAD EJECUTORA
     * 036' asignado en la base de datos
     */
    private String parUnidad;

    /**
     * Atributo que aloja el valor del parametro 'REGIONAL 036'
     * asignado en la base de datos
     */
    private String parRegional;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los items del combo 'Mes Inicial' */
    private List<Registro> listaMesInicial;

    /** Lista que contiene los items del combo 'Mes Final' */
    private List<Registro> listaMesFinal;

    /** Lista que contiene los items del combo 'Ano'. */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que contiene los items del combo 'Cuenta Inicial' */
    private RegistroDataModelImpl listaCuentaInicial;

    /** Lista que contiene los items del combo 'Cuenta Final' */
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RegistroIngresosControlador
     */
    public RegistroIngresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTRO_INGRESOS_CONTROLADOR
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
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaMesInicial();
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
        anio = SysmanFunciones.ano(SysmanFunciones.hoy().getTime());
        nivel = "99";
        ckIndicador = true;

        comprobarParametros();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Comprueba que los parametros utilizados en el formulario
     * existan en la base de datos
     */
    private void comprobarParametros() {
        String par = "SECCION EN INFORMES RESOLUCION 036";

        /**
         * Atributo que aloja el valor del parametro 'SECCION EN
         * INFORMES RESOLUCION 036' asignado en la base de datos
         */
        String parSeccion;

        try {
            parSeccion = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania, par, modulo, new Date(),
                                            true),
                            "");

            visibleSeccion = validarParametro(par, parSeccion);

            par = "SECCION 036";

            parSeccion036 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania, par, modulo, new Date(),
                                            true),
                            "");

            validarParametro(par, parSeccion036);

            par = "UNIDAD EJECUTORA 036";

            parUnidad = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania, par, modulo, new Date(),
                                            true),
                            "");

            validarParametro(par, parUnidad);

            par = "REGIONAL 036";

            parRegional = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(
                                            compania, par, modulo, new Date(),
                                            true),
                            "");

            validarParametro(par, parRegional);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     *
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2908")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista {@code listaAnio} asociada al combo 'Aï¿½o' */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroIngresosControladorUrlEnum.URL9723
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista {@code listaCuentaInicial} asociada al combo
     * 'Cuenta Inicial'
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroIngresosControladorUrlEnum.URL10132
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista {@code listaCuentaFinal} asociada al combo
     * 'Cuenta Final'
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroIngresosControladorUrlEnum.URL10998
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(RegistroIngresosControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista {@code listaMesInicial} asociada al combo 'Mes
     * Inicial'
     */
    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroIngresosControladorUrlEnum.URL11602
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista {@code listaMesFinal} asociada al combo 'Mes
     * Final'
     */
    public void cargarListaMesFinal() {
        // 7012
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
        try {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroIngresosControladorUrlEnum.URL12203
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf en la vista, gestiona
     * los eventos del mismo.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista,
     * gestiona los eventos del mismo.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     *
     * @param formato
     * Extension o tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {

        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "001442RegistroIngresos";

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("anio", anio);
        reemplazar.put("mesInicial", mesInicial);
        reemplazar.put("mesFinal", mesFinal);
        reemplazar.put("cuentaIni", "'" + cuentaInicial + "'");
        reemplazar.put("cuentaFin", "'" + cuentaFinal + "'");
        reemplazar.put("nivel", nivel);
        // </REEMPLAZAR VARIABLES EN CONSULTA>

        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_INDICADOR", ckIndicador);
        parametros.put("PR_VISIBLESECCION", visibleSeccion);
        parametros.put("PR_CONSECCION", parSeccion036);
        parametros.put("PR_CONUNIDAD", parUnidad);
        parametros.put("PR_CONREGIONAL", parRegional);
        parametros.put("PR_PERIODO", parPeriodo());
        // </ENVIAR PARAMETROS AL REPORTE>

        Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                        reemplazar, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Forma el valor del parametro periodo en el reporte.
     *
     * @return El valor del parametro periodo.
     */
    private String parPeriodo() {
        String nomPeriodoI = service.buscarEnLista(mesInicial, "NUMERO",
                        "NOMBRE", listaMesInicial).toUpperCase();
        String nomPeriodoF = service.buscarEnLista(mesFinal, "NUMERO", "NOMBRE",
                        listaMesFinal).toUpperCase();

        return SysmanFunciones.concatenar("DE ", nomPeriodoI, " A ",
                        nomPeriodoF);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al interacturar con el control
     * {@code MesInicial}
     */
    public void cambiarMesInicial() {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;
        cargarListaMesFinal();
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al cambiar de valor en el control Anio */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        mesInicial = mesFinal = null;
        cuentaInicial = cuentaFinal = null;

        cargarListaMesInicial();
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(cCodigo).toString();

        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(cCodigo).toString();
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
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public boolean isVisibleSeccion() {
        return visibleSeccion;
    }

    public void setVisibleSeccion(boolean visibleSeccion) {
        this.visibleSeccion = visibleSeccion;
    }

    public boolean isCkIndicador() {
        return ckIndicador;
    }

    public void setCkIndicador(boolean ckIndicador) {
        this.ckIndicador = ckIndicador;
    }

    /**
     * Asigna la variable cuentaInicial
     *
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     *
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     *
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable mesInicial
     *
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     *
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     *
     * @return mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     *
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable nivel
     *
     * @return nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Asigna la variable nivel
     *
     * @param nivel
     * Variable a asignar en nivel
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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

    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
