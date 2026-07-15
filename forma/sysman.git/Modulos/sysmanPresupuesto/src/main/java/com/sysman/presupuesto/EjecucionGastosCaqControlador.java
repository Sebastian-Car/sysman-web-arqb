/*-
 * EjecucionGastosCaqControlador.java
 *
 * 1.0
 * 
 * 05/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorEnum;
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar archivo excel con la ejecucion de gastos
 *
 * @version 1.0, 05/10/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class EjecucionGastosCaqControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo del modulo en la que ingreso en
     * la aplicacion
     */
    private final String modulo;
    /**
     * Constante definida por la cantidad de veces que se realiza el
     * llamado al texto <code>3040526</code>
     */
    private final String menu3040526;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del Indicador que referencia a
     * "Con centro de costo"
     */
    private boolean indCentroCosto;
    /**
     * Atributo que almacena el valor Indicador que referencia a "Con
     * auxiliar general"
     */
    private boolean indAuxiliar;
    /**
     * Atributo que almacena el valor Indicador que referencia a "Con
     * referencia"
     */
    private boolean indReferencia;
    /**
     * Atributo que almacena el valor Indicador que referencia a "Con
     * fuente recursos"
     */
    private boolean indFuenteRecursos;
    /**
     * Atributo que almacena el valor del codigo inicial seleccionado
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el valor del codigo final seleccionado
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el valor del numero de ano seleccionado
     */
    private String ano;
    /**
     * Atributo que almacena el valor del numero de mes seleccionado
     */
    private String mes;
    /**
     * Atributo que almacena el valor del centro de costo inicial
     * seleccionado
     */
    private String centroCostoInicial;
    /**
     * Atributo que almacena el valor del centro de costo final
     * seleccionado
     */
    private String centroCostoFinal;
    /**
     * Atributo que almacena el valor del auxiliar inicial
     * seleccionado
     */
    private String auxiliarInicial;
    /**
     * Atributo que almacena el valor del auxiliar final seleccionado
     */
    private String auxiliarFinal;
    /**
     * Atributo que almacena el valor de la referencia inicial
     * seleccionada
     */
    private String referenciaInicial;
    /**
     * Atributo que almacena el valor de la referencia final
     * seleccionada
     */
    private String referenciaFinal;
    /**
     * Atributo que almacena el valor de la fuente de recurso inicial
     * seleccionda
     */
    private String fuenteInicial;
    /**
     * Atributo que almacena el valor de la fuente de recurso final
     * seleccionada
     */
    private String fuenteFinal;
    /**
     * Atributo que almacena el valor del nivel digitado
     */
    private String nivel;
    /**
     * Atributo que almacena el nombre del codigo inicial seleccionado
     */
    private String nombreCodigoInicial;
    /**
     * Atributo que almacena el nombre del codigo final seleccionado
     */
    private String nombreCodigoFinal;
    /**
     * Atributo que almacena el nombre del centro de costo inicial
     * seleccionado
     */
    private String nombreCentroInicial;
    /**
     * Atributo que almacena el nombre del centro de costo final
     * seleccionado
     */
    private String nombreCentroFinal;
    /**
     * Atributo que almacena el nombre del auxiliar inicial
     * seleccionado
     */
    private String nombreAuxiliarInicial;
    /**
     * Atributo que almacena el nombre del auxiliar final seleccionado
     */
    private String nombreAuxiliarFinal;
    /**
     * Atributo que almacena el nombre de la referencia inicial
     * seleccionada
     */
    private String nombreReferenciaInicial;
    /**
     * Atributo que almacena el nombre de la referencia final
     * seleccionada
     */
    private String nombreReferenciaFinal;
    /**
     * Atributo que almacena el nombre de la fuente de recursos
     * inicial seleccionada
     */
    private String nombreFuenteInicial;
    /**
     * Atributo que almacena el titulo del formulario
     * 
     */
    private String titulo;
    /**
     * Atributo que almacena el nombre de la fuente de recursos final
     * seleccionada
     */
    private String nombreFuenteFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de los anos
     */
    private List<Registro> listaAno;
    /**
     * Lista de registros de los meses
     */
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los codigos presupuestales
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de registros de los codigos presupuestale
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Lista de registros de los centros de costos
     */
    private RegistroDataModelImpl listaCentroCostoInicial;
    /**
     * Lista de registros de los centros de costos
     */
    private RegistroDataModelImpl listaCentroCostoFinal;
    /**
     * Lista de registros de los auxiliares
     */
    private RegistroDataModelImpl listaAuxiliarInicial;
    /**
     * Lista de registros de los auxiliares
     */
    private RegistroDataModelImpl listaAuxiliarFinal;
    /**
     * Lista de registros de las referencias
     */
    private RegistroDataModelImpl listaReferenciaInicial;
    /**
     * Lista de registros de las referencias
     */
    private RegistroDataModelImpl listaReferenciaFinal;
    /**
     * Lista de registros de las fuentes de recursos
     */
    private RegistroDataModelImpl listaFuenteInicial;
    /**
     * Lista de registros de las fuentes de recursos
     */
    private RegistroDataModelImpl listaFuenteFinal;
    
    private boolean indFuentCuipo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de EjecucionGastosCaqControlador
     */
    public EjecucionGastosCaqControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        menu3040526 = "3040526";
        try {
            // 1957
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_GASTOS_CAQ_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        abrirFormulario();
        cargarListaAno();
        cargarListaMes();
        cargarListaCodigoInicial();
        cargarListaCentroCostoInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionGastosCaqControladorUrlEnum.URL419
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionGastosCaqControladorUrlEnum.URL452
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        if (menu3040526.equals(SessionUtil.getMenuActual())) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EjecucionGastosCaqControladorUrlEnum.URL536
                                                            .getValue());

            listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EjecucionGastosCaqControladorUrlEnum.URL558
                                                            .getValue());

            listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());

        }

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecucionGastosCaqControladorEnum.CUENTAINICIAL.getValue(),
                        codigoInicial);

        if (menu3040526.equals(SessionUtil.getMenuActual())) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EjecucionGastosCaqControladorUrlEnum.URL557
                                                            .getValue());
            listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());

        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EjecucionGastosCaqControladorUrlEnum.URL559
                                                            .getValue());
            listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());

        }
    }

    /**
     * 
     * Carga la lista listaCentroCostoInicial
     *
     */
    public void cargarListaCentroCostoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL584
                                                        .getValue());
        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroCostoFinal
     *
     */
    public void cargarListaCentroCostoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecucionGastosCaqControladorEnum.CENTRO_COSTO.getValue(),
                        centroCostoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL600
                                                        .getValue());
        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecucionGastosCaqControladorEnum.ANIO.getValue(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL749
                                                        .getValue());
        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
    public void cargarListaAuxiliarFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecucionGastosCaqControladorEnum.ANIO.getValue(), ano);
        param.put(EjecucionGastosCaqControladorEnum.CODIGOFINAL.getValue(),
                        auxiliarInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL776
                                                        .getValue());
        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL707
                                                        .getValue());
        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaReferenciaFinal
     *
     */
    public void cargarListaReferenciaFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecucionGastosCaqControladorEnum.REFERENCIAINICIAL
                        .getValue(), referenciaInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL732
                                                        .getValue());
        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuenteInicial
     *
     */
    public void cargarListaFuenteInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL663
                                                        .getValue());
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuenteFinal
     *
     */
    public void cargarListaFuenteFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecucionGastosCaqControladorEnum.FUENTEINICIAL.getValue(),
                        fuenteInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionGastosCaqControladorUrlEnum.URL684
                                                        .getValue());
        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        nombreCodigoInicial = null;
        codigoFinal = null;
        nombreCodigoFinal = null;
        centroCostoInicial = null;
        nombreCentroInicial = null;
        centroCostoFinal = null;
        nombreCentroFinal = null;
        auxiliarInicial = null;
        nombreAuxiliarInicial = null;
        auxiliarFinal = null;
        nombreAuxiliarFinal = null;
        referenciaInicial = null;
        nombreReferenciaInicial = null;
        referenciaFinal = null;
        nombreReferenciaFinal = null;
        fuenteInicial = null;
        nombreFuenteInicial = null;
        fuenteFinal = null;
        nombreFuenteFinal = null;
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCodigoInicial();
        cargarListaCentroCostoInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndCentroCosto
     * 
     * 
     */
    public void cambiarIndCentroCosto() {
        // <CODIGO_DESARROLLADO>
        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreCentroInicial = null;
        nombreCentroFinal = null;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndAuxiliar
     * 
     * 
     */
    public void cambiarIndAuxiliar() {
        // <CODIGO_DESARROLLADO>
        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreAuxiliarInicial = null;
        nombreReferenciaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndReferencia
     * 
     * 
     */
    public void cambiarIndReferencia() {
        // <CODIGO_DESARROLLADO>
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreReferenciaInicial = null;
        nombreReferenciaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control IndFuenteRecursos
     * 
     * 
     */
    public void cambiarIndFuenteRecursos() {
        // <CODIGO_DESARROLLADO>
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreFuenteInicial = null;
        nombreFuenteFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreCodigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        codigoFinal = null;
        nombreCodigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreCodigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreCentroInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        centroCostoFinal = null;
        nombreCentroFinal = null;
        cargarListaCentroCostoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreCentroFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreAuxiliarInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        auxiliarFinal = null;
        nombreAuxiliarFinal = null;
        cargarListaAuxiliarFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreAuxiliarFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreReferenciaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        referenciaFinal = null;
        nombreReferenciaFinal = null;
        cargarListaReferenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreReferenciaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreFuenteInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        fuenteFinal = null;
        nombreReferenciaFinal = null;
        cargarListaFuenteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreFuenteFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     *
     */
    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto Mensaje en la
     * vista
     *
     *
     */
    public void ejecutarMensaje() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeError(idioma.getString(
                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que realiza el proceso de resolver la consulta y
     * exportarla en un archivo excel
     */
    private void generarExcel() {
        try {
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("ano", ano);
            reemplazos.put("codigoInicial", codigoInicial);
            reemplazos.put("codigoFinal", codigoFinal);

            reemplazos.put("mesFinal", mes);
            validarAuxiliares(reemplazos);

            String strSql;
            if (menu3040526.equals(SessionUtil.getMenuActual())) {
                reemplazos.put("naturaleza", "D");
                
                if("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
    					"MANEJA FORMATO ESPECIAL EJECUCION GASTOS AUXILIARES", modulo, new Date(), true), "NO"))) {
	                strSql = Reporteador.resuelveConsulta(
	                                "800628CuentasConAuxiliaresGastosEspecial",
	                                Integer.parseInt(modulo), reemplazos);
                } else {
                	strSql = Reporteador.resuelveConsulta(
		                            "800295CuentasConAuxiliaresGastos",
		                            Integer.parseInt(modulo), reemplazos);
                }
                
            }
            else if("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA FORMATO ESPECIAL EJECUCION INGRESOS AUXILIARES", modulo, new Date(), true), "NO"))) {
                reemplazos.put("naturaleza", "C");
                strSql = Reporteador.resuelveConsulta(
                                "800647CuentasAuxiliaresIngresosEspecial",
                                Integer.parseInt(modulo), reemplazos);
            } else {
            	
                reemplazos.put("naturaleza", "C");
                strSql = Reporteador.resuelveConsulta(
                                "800199CuentasConAuxiliares",
                                Integer.parseInt(modulo), reemplazos);
            }

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
            
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void validarAuxiliares(Map<String, Object> reemplazos) {
        reemplazos.put("centroCosto", "");
        reemplazos.put("auxiliar", "");
        reemplazos.put("referencia", "");
        reemplazos.put("fuenteRecurso", "");
        reemplazos.put("cuenta","V.ID CUENTA,");
        reemplazos.put("fuenterecursonombre","V.FUENTE_RECURSO");
        reemplazos.put("fuentecuipo","");
        reemplazos.put("fuentecuipoGroup","");
        
        if (indCentroCosto) {
            reemplazos.put("centroCosto", " AND CENTRO_COSTO BETWEEN '"
                + centroCostoInicial + "' AND  '" + centroCostoFinal + "' ");
        }
        if (indAuxiliar) {
            reemplazos.put("auxiliar", " AND AUXILIAR BETWEEN '"
                + auxiliarInicial + "'  AND '" + auxiliarFinal + "' ");

        }
        if (indReferencia) {
            reemplazos.put("referencia", " AND REFERENCIA BETWEEN '"
                + referenciaInicial + "' AND '" + referenciaFinal + "' ");
        }
        if (indFuenteRecursos) {
            reemplazos.put("fuenteRecurso", " AND FUENTE_RECURSO BETWEEN '"
                + fuenteInicial + "' AND '" + fuenteFinal + "' ");
        }
          
       if (indFuentCuipo) {
            reemplazos.put("cuenta", "");
            reemplazos.put("fuenterecursonombre", "V.FUENTE_RECURSO || ' ' || V.NOMBREFUENTE");
            reemplazos.put("fuentecuipo","V.FUENTECUIPO || ' ' || TC.NOMBRE  FUENTE_CUIPO,");
            reemplazos.put("fuentecuipoGroup","V.FUENTECUIPO || ' ' || TC.NOMBRE ,");
                }
    }

    	// </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        if (menu3040526.equals(SessionUtil.getMenuActual())) {
            titulo = idioma.getString("TB_TB4237");
        }
        else {
            titulo = idioma.getString("TB_TB4238");
        }

        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     * @return true
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
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indCentroCosto
     * 
     * @return indCentroCosto
     */
    public boolean getIndCentroCosto() {
        return indCentroCosto;
    }

    /**
     * Asigna la variable indCentroCosto
     * 
     * @param indCentroCosto
     * Variable a asignar en indCentroCosto
     */
    public void setIndCentroCosto(boolean indCentroCosto) {
        this.indCentroCosto = indCentroCosto;
    }

    /**
     * Retorna la variable indAuxiliar
     * 
     * @return indAuxiliar
     */
    public boolean getIndAuxiliar() {
        return indAuxiliar;
    }

    /**
     * Asigna la variable indAuxiliar
     * 
     * @param indAuxiliar
     * Variable a asignar en indAuxiliar
     */
    public void setIndAuxiliar(boolean indAuxiliar) {
        this.indAuxiliar = indAuxiliar;
    }

    /**
     * Retorna la variable indReferencia
     * 
     * @return indReferencia
     */
    public boolean getIndReferencia() {
        return indReferencia;
    }

    /**
     * Asigna la variable indReferencia
     * 
     * @param indReferencia
     * Variable a asignar en indReferencia
     */
    public void setIndReferencia(boolean indReferencia) {
        this.indReferencia = indReferencia;
    }

    /**
     * Retorna la variable indFuenteRecursos
     * 
     * @return indFuenteRecursos
     */
    public boolean getIndFuenteRecursos() {
        return indFuenteRecursos;
    }

    /**
     * Asigna la variable indFuenteRecursos
     * 
     * @param indFuenteRecursos
     * Variable a asignar en indFuenteRecursos
     */
    public void setIndFuenteRecursos(boolean indFuenteRecursos) {
        this.indFuenteRecursos = indFuenteRecursos;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

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
     * Retorna la variable centroCostoInicial
     * 
     * @return centroCostoInicial
     */
    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    /**
     * Asigna la variable centroCostoInicial
     * 
     * @param centroCostoInicial
     * Variable a asignar en centroCostoInicial
     */
    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    /**
     * Retorna la variable centroCostoFinal
     * 
     * @return centroCostoFinal
     */
    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    /**
     * Asigna la variable centroCostoFinal
     * 
     * @param centroCostoFinal
     * Variable a asignar en centroCostoFinal
     */
    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    /**
     * Retorna la variable auxiliarInicial
     * 
     * @return auxiliarInicial
     */
    public String getAuxiliarInicial() {
        return auxiliarInicial;
    }

    /**
     * Asigna la variable auxiliarInicial
     * 
     * @param auxiliarInicial
     * Variable a asignar en auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }

    /**
     * Retorna la variable auxiliarFinal
     * 
     * @return auxiliarFinal
     */
    public String getAuxiliarFinal() {
        return auxiliarFinal;
    }

    /**
     * Asigna la variable auxiliarFinal
     * 
     * @param auxiliarFinal
     * Variable a asignar en auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }

    /**
     * Retorna la variable referenciaInicial
     * 
     * @return referenciaInicial
     */
    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     * 
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     * 
     * @return referenciaFinal
     */
    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     * 
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    /**
     * Retorna la variable fuenteInicial
     * 
     * @return fuenteInicial
     */
    public String getFuenteInicial() {
        return fuenteInicial;
    }

    /**
     * Asigna la variable fuenteInicial
     * 
     * @param fuenteInicial
     * Variable a asignar en fuenteInicial
     */
    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    /**
     * Retorna la variable fuenteFinal
     * 
     * @return fuenteFinal
     */
    public String getFuenteFinal() {
        return fuenteFinal;
    }

    /**
     * Asigna la variable fuenteFinal
     * 
     * @param fuenteFinal
     * Variable a asignar en fuenteFinal
     */
    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
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
     * Retorna la variable nombreCodigoInicial
     * 
     * @return nombreCodigoInicial
     */
    public String getNombreCodigoInicial() {
        return nombreCodigoInicial;
    }

    /**
     * Asigna la variable nombreCodigoInicial
     * 
     * @param nombreCodigoInicial
     * Variable a asignar en nombreCodigoInicial
     */
    public void setNombreCodigoInicial(String nombreCodigoInicial) {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    /**
     * Retorna la variable nombreCodigoFinal
     * 
     * @return nombreCodigoFinal
     */
    public String getNombreCodigoFinal() {
        return nombreCodigoFinal;
    }

    /**
     * Asigna la variable nombreCodigoFinal
     * 
     * @param nombreCodigoFinal
     * Variable a asignar en nombreCodigoFinal
     */
    public void setNombreCodigoFinal(String nombreCodigoFinal) {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    /**
     * Retorna la variable nombreCentroInicial
     * 
     * @return nombreCentroInicial
     */
    public String getNombreCentroInicial() {
        return nombreCentroInicial;
    }

    /**
     * Asigna la variable nombreCentroInicial
     * 
     * @param nombreCentroInicial
     * Variable a asignar en nombreCentroInicial
     */
    public void setNombreCentroInicial(String nombreCentroInicial) {
        this.nombreCentroInicial = nombreCentroInicial;
    }

    /**
     * Retorna la variable nombreCentroFinal
     * 
     * @return nombreCentroFinal
     */
    public String getNombreCentroFinal() {
        return nombreCentroFinal;
    }

    /**
     * Asigna la variable nombreCentroFinal
     * 
     * @param nombreCentroFinal
     * Variable a asignar en nombreCentroFinal
     */
    public void setNombreCentroFinal(String nombreCentroFinal) {
        this.nombreCentroFinal = nombreCentroFinal;
    }

    /**
     * Retorna la variable nombreAuxiliarInicial
     * 
     * @return nombreAuxiliarInicial
     */
    public String getNombreAuxiliarInicial() {
        return nombreAuxiliarInicial;
    }

    /**
     * Asigna la variable nombreAuxiliarInicial
     * 
     * @param nombreAuxiliarInicial
     * Variable a asignar en nombreAuxiliarInicial
     */
    public void setNombreAuxiliarInicial(String nombreAuxiliarInicial) {
        this.nombreAuxiliarInicial = nombreAuxiliarInicial;
    }

    /**
     * Retorna la variable nombreAuxiliarFinal
     * 
     * @return nombreAuxiliarFinal
     */
    public String getNombreAuxiliarFinal() {
        return nombreAuxiliarFinal;
    }

    /**
     * Asigna la variable nombreAuxiliarFinal
     * 
     * @param nombreAuxiliarFinal
     * Variable a asignar en nombreAuxiliarFinal
     */
    public void setNombreAuxiliarFinal(String nombreAuxiliarFinal) {
        this.nombreAuxiliarFinal = nombreAuxiliarFinal;
    }

    /**
     * Retorna la variable nombreReferenciaInicial
     * 
     * @return nombreReferenciaInicial
     */
    public String getNombreReferenciaInicial() {
        return nombreReferenciaInicial;
    }

    /**
     * Asigna la variable nombreReferenciaInicial
     * 
     * @param nombreReferenciaInicial
     * Variable a asignar en nombreReferenciaInicial
     */
    public void setNombreReferenciaInicial(String nombreReferenciaInicial) {
        this.nombreReferenciaInicial = nombreReferenciaInicial;
    }

    /**
     * Retorna la variable nombreReferenciaFinal
     * 
     * @return nombreReferenciaFinal
     */
    public String getNombreReferenciaFinal() {
        return nombreReferenciaFinal;
    }

    /**
     * Asigna la variable nombreReferenciaFinal
     * 
     * @param nombreReferenciaFinal
     * Variable a asignar en nombreReferenciaFinal
     */
    public void setNombreReferenciaFinal(String nombreReferenciaFinal) {
        this.nombreReferenciaFinal = nombreReferenciaFinal;
    }

    /**
     * Retorna la variable nombreFuenteInicial
     * 
     * @return nombreFuenteInicial
     */
    public String getNombreFuenteInicial() {
        return nombreFuenteInicial;
    }

    /**
     * Asigna la variable nombreFuenteInicial
     * 
     * @param nombreFuenteInicial
     * Variable a asignar en nombreFuenteInicial
     */
    public void setNombreFuenteInicial(String nombreFuenteInicial) {
        this.nombreFuenteInicial = nombreFuenteInicial;
    }

    /**
     * Retorna la variable nombreFuenteFinal
     * 
     * @return nombreFuenteFinal
     */
    public String getNombreFuenteFinal() {
        return nombreFuenteFinal;
    }

    /**
     * Asigna la variable nombreFuenteFinal
     * 
     * @param nombreFuenteFinal
     * Variable a asignar en nombreFuenteFinal
     */
    public void setNombreFuenteFinal(String nombreFuenteFinal) {
        this.nombreFuenteFinal = nombreFuenteFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Retorna la lista listaCentroCostoInicial
     * 
     * @return listaCentroCostoInicial
     */
    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }

    /**
     * Asigna la lista listaCentroCostoInicial
     * 
     * @param listaCentroCostoInicial
     * Variable a asignar en listaCentroCostoInicial
     */
    public void setListaCentroCostoInicial(
        RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    /**
     * Retorna la lista listaCentroCostoFinal
     * 
     * @return listaCentroCostoFinal
     */
    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }

    /**
     * Asigna la lista listaCentroCostoFinal
     * 
     * @param listaCentroCostoFinal
     * Variable a asignar en listaCentroCostoFinal
     */
    public void setListaCentroCostoFinal(
        RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
    }

    /**
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(
        RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }

    /**
     * Retorna la lista listaAuxiliarFinal
     * 
     * @return listaAuxiliarFinal
     */
    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    /**
     * Asigna la lista listaAuxiliarFinal
     * 
     * @param listaAuxiliarFinal
     * Variable a asignar en listaAuxiliarFinal
     */
    public void setListaAuxiliarFinal(
        RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    /**
     * Retorna la lista listaReferenciaInicial
     * 
     * @return listaReferenciaInicial
     */
    public RegistroDataModelImpl getListaReferenciaInicial() {
        return listaReferenciaInicial;
    }

    /**
     * Asigna la lista listaReferenciaInicial
     * 
     * @param listaReferenciaInicial
     * Variable a asignar en listaReferenciaInicial
     */
    public void setListaReferenciaInicial(
        RegistroDataModelImpl listaReferenciaInicial) {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }

    /**
     * Retorna la lista listaReferenciaFinal
     * 
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }

    /**
     * Asigna la lista listaReferenciaFinal
     * 
     * @param listaReferenciaFinal
     * Variable a asignar en listaReferenciaFinal
     */
    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }

    /**
     * Retorna la lista listaFuenteInicial
     * 
     * @return listaFuenteInicial
     */
    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    /**
     * Asigna la lista listaFuenteInicial
     * 
     * @param listaFuenteInicial
     * Variable a asignar en listaFuenteInicial
     */
    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    /**
     * Retorna la lista listaFuenteFinal
     * 
     * @return listaFuenteFinal
     */
    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    /**
     * Asigna la lista listaFuenteFinal
     * 
     * @param listaFuenteFinal
     * Variable a asignar en listaFuenteFinal
     */
    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }

    /**
     * Retorna la variable titulo
     * 
     * @return titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna la variable titulo
     * 
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public boolean getindFuentCuipo() {
		return indFuentCuipo;
	}

	public void setindFuentCuipo(boolean indFuentCuipo) {
		this.indFuentCuipo = indFuentCuipo;
	}



    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
