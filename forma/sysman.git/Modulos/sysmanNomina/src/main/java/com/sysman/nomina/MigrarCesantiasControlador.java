/*-
 * MigrarCesantiasControlador.java
 *
 * 1.0
 * 
 * 15/01/2018
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.enums.MigrarCesantiasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite migrar las cesantias.
 *
 * @version 1.0, 15/01/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class MigrarCesantiasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consNombre;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado al anio para la toma de
     * datos de la forma del formulario.
     */
    private String anioInicial;
    /**
     * Atributo que contiene el valor asignado al anio para la
     * asignacion de datos de la forma del formulario.
     */
    private String anioFinal;
    /**
     * Atributo que contiene el valor asignado al mes para la toma de
     * datos de la forma del formulario.
     */
    private String mesInicial;
    /**
     * Atributo que contiene el valor asignado al mes para la
     * asignacion de datos de la forma del formulario.
     */
    private String mesFinal;
    /**
     * Atributo que contiene el valor asignado al periodo para la toma
     * de datos de la forma del formulario.
     */
    private String periodoInicial;
    /**
     * Atributo que contiene el valor asignado al periodo para la
     * asignacion de datos de la forma del formulario.
     */
    private String periodoFinal;
    /**
     * Atributo que contiene el valor asignado al proceso para la toma
     * de datos de la forma del formulario.
     */
    private String procesoInicial;
    /**
     * Atributo que contiene el valor asignado al concepto de cesantia
     * de la forma del formulario.
     */
    private String conceptoCesantia;
    /**
     * Atributo que contiene el valor asignado al concepto de interes
     * de la forma del formulario.
     */
    private String conceptoInteres = "0";
    /**
     * Atributo que contiene el valor asignado al codigo de cesantias
     * de la forma del formulario.
     */
    private String codigoCesantias;
    /**
     * Atributo que contiene el valor asignado al codigo de interes de
     * la forma del formulario.
     */
    private String codigoInteres = "0";

    private String nombreConceptoCesantia;
    private String nombreConceptoInteres;
    private String nombreCodigoCesantia;
    private String nombreCodigoInteres;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del anio */
    private List<Registro> listaAno1;
    /** Lista que contiene los detalles del combo anio */
    private List<Registro> listaAno2;
    /** Lista que contiene los detalles del combo mes */
    private List<Registro> listaMes1;
    /** Lista que contiene los detalles del combo mes */
    private List<Registro> listaMes2;
    /** Lista que contiene los detalles del combo periodo */
    private List<Registro> listaPeriodo1;
    /** Lista que contiene los detalles del combo periodo */
    private List<Registro> listaPeriodo2;
    /** Lista que contiene los detalles del combo proceso */
    private List<Registro> listaProceso;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que contiene los detalles del combo concepto Cesantia */
    private RegistroDataModelImpl listaConceptoCesantia;
    /** Lista que contiene los detalles del combo concepto Interes */
    private RegistroDataModelImpl listaConceptoInteres;
    /** Lista que contiene los detalles del combo codigo Cesantia */
    private RegistroDataModelImpl listaCodigoCesantias;
    /** Lista que contiene los detalles del combo Codigo Interes */
    private RegistroDataModelImpl listaCodigoInteres;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;

    /**
     * Crea una nueva instancia de MigrarCesantiasControlador
     */
    public MigrarCesantiasControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNombre = "NOMBRE_CONCEPTO";
        try {
            procesoInicial = (String) SessionUtil
                            .getSessionVar("procesoNomina");
            anioInicial = anioFinal = (String) SessionUtil
                            .getSessionVar("anioNomina");
            mesInicial = mesFinal = (String) SessionUtil
                            .getSessionVar("mesNomina");
            periodoInicial = periodoFinal = (String) SessionUtil
                            .getSessionVar("periodoNomina");
            numFormulario = GeneralCodigoFormaEnum.MIGRAR_CESANTIAS_CONTROLADOR
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
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConceptoCesantia();
        cargarListaConceptoInteres();
        cargarListaCodigoCesantias();
        cargarListaCodigoInteres();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno1
     *
     */
    public void cargarListaAno1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno1 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL7338
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
     * Carga la lista listaAno2
     *
     */
    public void cargarListaAno2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
            listaAno2 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL7680
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
     * Carga la lista listaMes1
     *
     */
    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaMes1 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL8022
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
     * Carga la lista listaMes2
     *
     */
    public void cargarListaMes2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
            listaMes2 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL8022
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
     * Carga la lista listaPeriodo1
     *
     */
    public void cargarListaPeriodo1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
            param.put(GeneralParameterEnum.MES.getName(), mesInicial);

            listaPeriodo1 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL9136
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
     * Carga la lista listaPeriodo2
     *
     */
    public void cargarListaPeriodo2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
            param.put(GeneralParameterEnum.MES.getName(), mesFinal);
            listaPeriodo2 = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL9136
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
     * Carga la lista listaProceso
     *
     */
    public void cargarListaProceso() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaProceso = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MigrarCesantiasControladorUrlEnum.URL9754
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
     * Carga la lista listaConceptoCesantia
     *
     */
    public void cargarListaConceptoCesantia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MigrarCesantiasControladorUrlEnum.URL10243
                                                        .getValue());
        listaConceptoCesantia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.ID_DE_CONCEPTO.getName());
    }

    /**
     * 
     * Carga la lista listaConceptoInteres
     *
     */
    public void cargarListaConceptoInteres() {
        listaConceptoInteres = listaConceptoCesantia;

    }

    /**
     * 
     * Carga la lista listaCodigoCesantias
     *
     */
    public void cargarListaCodigoCesantias() {
        listaCodigoCesantias = listaConceptoCesantia;
    }

    /**
     * 
     * Carga la lista listaCodigoInteres
     *
     */
    public void cargarListaCodigoInteres() {
        listaCodigoInteres = listaConceptoCesantia;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbNominaSiete.migrarACesantias(compania,
                            convertirCadena(procesoInicial),
                            convertirCadena(anioInicial),
                            convertirCadena(mesInicial),
                            convertirCadena(periodoInicial),
                            convertirCadena(conceptoCesantia),
                            convertirCadena(conceptoInteres),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public int convertirCadena(String cad) {
        return Integer.parseInt(cad);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando75 en la vista
     *
     *
     */
    public void oprimirComando75() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbNominaSiete.migrarAHistoricos(compania,
                            convertirCadena(procesoInicial),
                            convertirCadena(anioInicial),
                            convertirCadena(mesInicial),
                            convertirCadena(periodoInicial),
                            convertirCadena(anioFinal),
                            convertirCadena(mesFinal),
                            convertirCadena(periodoFinal),
                            convertirCadena(conceptoCesantia),
                            convertirCadena(conceptoInteres),
                            convertirCadena(codigoCesantias),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano1
     * 
     * 
     */
    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes1();
        cargarListaAno2();
        anioFinal = mesInicial = mesFinal = periodoInicial = periodoFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Ano2
     * 
     * 
     */
    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes2();
        mesFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes1
     * 
     * 
     */
    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        periodoInicial = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes2
     * 
     * 
     */
    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo2();
        periodoFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoCesantia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoCesantia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoCesantia = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreConceptoCesantia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consNombre), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoInteres
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoInteres(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoInteres = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreConceptoInteres = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consNombre), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCesantias
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCesantias(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoCesantias = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreCodigoCesantia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consNombre), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInteres
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInteres(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInteres = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreCodigoInteres = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consNombre), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable anioFinal
     * 
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     * 
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
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

    /**
     * Retorna la variable PeriodoInicial
     * 
     * @return PeriodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable PeriodoInicial
     * 
     * @param PeriodoInicial
     * Variable a asignar en PeriodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable procesoInicial
     * 
     * @return procesoInicial
     */
    public String getProcesoInicial() {
        return procesoInicial;
    }

    /**
     * Asigna la variable procesoInicial
     * 
     * @param procesoInicial
     * Variable a asignar en procesoInicial
     */
    public void setProcesoInicial(String procesoInicial) {
        this.procesoInicial = procesoInicial;
    }

    /**
     * Retorna la variable conceptoCesantia
     * 
     * @return conceptoCesantia
     */
    public String getConceptoCesantia() {
        return conceptoCesantia;
    }

    /**
     * Asigna la variable conceptoCesantia
     * 
     * @param conceptoCesantia
     * Variable a asignar en conceptoCesantia
     */
    public void setConceptoCesantia(String conceptoCesantia) {
        this.conceptoCesantia = conceptoCesantia;
    }

    /**
     * Retorna la variable conceptoInteres
     * 
     * @return conceptoInteres
     */
    public String getConceptoInteres() {
        return conceptoInteres;
    }

    /**
     * Asigna la variable conceptoInteres
     * 
     * @param conceptoInteres
     * Variable a asignar en conceptoInteres
     */
    public void setConceptoInteres(String conceptoInteres) {
        this.conceptoInteres = conceptoInteres;
    }

    /**
     * Retorna la variable codigoCesantias
     * 
     * @return codigoCesantias
     */
    public String getCodigoCesantias() {
        return codigoCesantias;
    }

    /**
     * Asigna la variable codigoCesantias
     * 
     * @param codigoCesantias
     * Variable a asignar en codigoCesantias
     */
    public void setCodigoCesantias(String codigoCesantias) {
        this.codigoCesantias = codigoCesantias;
    }

    /**
     * Retorna la variable codigoInteres
     * 
     * @return codigoInteres
     */
    public String getCodigoInteres() {
        return codigoInteres;
    }

    /**
     * Asigna la variable codigoInteres
     * 
     * @param codigoInteres
     * Variable a asignar en codigoInteres
     */
    public void setCodigoInteres(String codigoInteres) {
        this.codigoInteres = codigoInteres;
    }

    public String getNombreConceptoCesantia() {
        return nombreConceptoCesantia;
    }

    public void setNombreConceptoCesantia(String nombreConceptoCesantia) {
        this.nombreConceptoCesantia = nombreConceptoCesantia;
    }

    public String getNombreConceptoInteres() {
        return nombreConceptoInteres;
    }

    public void setNombreConceptoInteres(String nombreConceptoInteres) {
        this.nombreConceptoInteres = nombreConceptoInteres;
    }

    public String getNombreCodigoCesantia() {
        return nombreCodigoCesantia;
    }

    public void setNombreCodigoCesantia(String nombreCodigoCesantia) {
        this.nombreCodigoCesantia = nombreCodigoCesantia;
    }

    public String getNombreCodigoInteres() {
        return nombreCodigoInteres;
    }

    public void setNombreCodigoInteres(String nombreCodigoInteres) {
        this.nombreCodigoInteres = nombreCodigoInteres;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno1
     * 
     * @return listaAno1
     */
    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    /**
     * Asigna la lista listaAno1
     * 
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    /**
     * Retorna la lista listaAno2
     * 
     * @return listaAno2
     */
    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    /**
     * Asigna la lista listaAno2
     * 
     * @param listaAno2
     * Variable a asignar en listaAno2
     */
    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    /**
     * Retorna la lista listaMes1
     * 
     * @return listaMes1
     */
    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    /**
     * Asigna la lista listaMes1
     * 
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    /**
     * Retorna la lista listaMes2
     * 
     * @return listaMes2
     */
    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    /**
     * Asigna la lista listaMes2
     * 
     * @param listaMes2
     * Variable a asignar en listaMes2
     */
    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    /**
     * Retorna la lista listaPeriodo1
     * 
     * @return listaPeriodo1
     */
    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    /**
     * Asigna la lista listaPeriodo1
     * 
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    /**
     * Retorna la lista listaPeriodo2
     * 
     * @return listaPeriodo2
     */
    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    /**
     * Asigna la lista listaPeriodo2
     * 
     * @param listaPeriodo2
     * Variable a asignar en listaPeriodo2
     */
    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConceptoCesantia
     * 
     * @return listaConceptoCesantia
     */
    public RegistroDataModelImpl getListaConceptoCesantia() {
        return listaConceptoCesantia;
    }

    /**
     * Asigna la lista listaConceptoCesantia
     * 
     * @param listaConceptoCesantia
     * Variable a asignar en listaConceptoCesantia
     */
    public void setListaConceptoCesantia(
        RegistroDataModelImpl listaConceptoCesantia) {
        this.listaConceptoCesantia = listaConceptoCesantia;
    }

    /**
     * Retorna la lista listaConceptoInteres
     * 
     * @return listaConceptoInteres
     */
    public RegistroDataModelImpl getListaConceptoInteres() {
        return listaConceptoInteres;
    }

    /**
     * Asigna la lista listaConceptoInteres
     * 
     * @param listaConceptoInteres
     * Variable a asignar en listaConceptoInteres
     */
    public void setListaConceptoInteres(
        RegistroDataModelImpl listaConceptoInteres) {
        this.listaConceptoInteres = listaConceptoInteres;
    }

    /**
     * Retorna la lista listaCodigoCesantias
     * 
     * @return listaCodigoCesantias
     */
    public RegistroDataModelImpl getListaCodigoCesantias() {
        return listaCodigoCesantias;
    }

    /**
     * Asigna la lista listaCodigoCesantias
     * 
     * @param listaCodigoCesantias
     * Variable a asignar en listaCodigoCesantias
     */
    public void setListaCodigoCesantias(
        RegistroDataModelImpl listaCodigoCesantias) {
        this.listaCodigoCesantias = listaCodigoCesantias;
    }

    /**
     * Retorna la lista listaCodigoInteres
     * 
     * @return listaCodigoInteres
     */
    public RegistroDataModelImpl getListaCodigoInteres() {
        return listaCodigoInteres;
    }

    /**
     * Asigna la lista listaCodigoInteres
     * 
     * @param listaCodigoInteres
     * Variable a asignar en listaCodigoInteres
     */
    public void setListaCodigoInteres(
        RegistroDataModelImpl listaCodigoInteres) {
        this.listaCodigoInteres = listaCodigoInteres;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
