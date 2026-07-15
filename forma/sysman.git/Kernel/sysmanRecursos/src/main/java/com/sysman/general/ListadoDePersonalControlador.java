/*-
 * ListadoDePersonalControlador.java
 *
 * 1.0
 * 
 * 11/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general;

import java.io.IOException;
import java.text.ParseException;
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralLocal;
import com.sysman.general.enums.ListadoDePersonalControladorEnum;
import com.sysman.general.enums.ListadoDePersonalControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera reportes de personal a partir de distintos
 * parametros
 *
 * @version 1.0, 11/01/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ListadoDePersonalControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Proceso de nomina seleccionado al entrar al modulo
     */
    private String procesoNomina;
    /**
     * Anio de nomina seleccionado al entrar al modulo
     */
    private String anoNomina;
    /**
     * Mes de nomina seleccionado al entrar al modulo
     */
    private String mesNomina;
    /**
     * Periodo de nomina seleccionado al entrar al modulo
     */
    private String periodoNomina;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el nombre del estado del personal
     */
    private String nombreEstado;

    /**
     * Atributo que almacena el valor del radiobutton de personal
     */
    private String opcionPersonal;
    /**
     * Atributo que almacena el valor del radiobutton de pensionados
     */
    private String opcionPensionado;

    /**
     * Atributo que almacena el valor del indicador de incapacidades
     */
    private boolean incapacidades;

    /**
     * Atributo que almacena el valor del indicador de licencia de
     * maternidad
     */
    private boolean licenciaMaternidad;
    /**
     * Atributo que almacena el genero seleccionado en la vista
     */
    private String genero;
    /**
     * Atributo que almacena el codigo del cargo inicial
     */
    private String cargoInicial;
    /**
     * Atributo que almacena el codigo del cargo final
     */
    private String cargoFinal;
    /**
     * Atributo que almacena el valor del estado actual del empleado
     */
    private String estadoActual;
    /**
     * Atributo que almacena el codigo del tipo de contrato inicial
     */
    private String tipoContratoInicial;
    /**
     * Atributo que almacena el codigo del tipo de contrato final
     */
    private String tipoContratoFinal;
    /**
     * Atributo que almacena el codigo de la profesion inicial
     */
    private String profesionInicial;
    /**
     * Atributo que almacena el codigo de la dependencia inicial
     */
    private String dependenciaInicial;
    /**
     * Atributo que almacena el codigo de la dependencia final
     */
    private String dependenciaFinal;
    /**
     * Atributo que almacena el codigo de la incapacidad inicial
     */
    private String incapacidadInicial;
    /**
     * Atributo que almacena el codigo de la incapacidad final
     */
    private String incapacidadFinal;
    /**
     * Atributo que almacena el codigo de la enfermedad inicial
     */
    private String enfermedadInicial;
    /**
     * Atributo que almacena el codigo de la enfermedad final
     */
    private String enfermedadFinal;
    /**
     * Atributo que almacena el codigo de la licencia inicial
     */
    private String licenciaInicial;
    /**
     * Atributo que almacena el codigo de la licencia final
     */
    private String licenciaFinal;
    /**
     * Atributo que almacena el codigo de la eps inicial
     */
    private String epsInicial;
    /**
     * Atributo que almacena el codigo de la eps final
     */
    private String epsFinal;
    /**
     * Atributo que almacena el codigo de la profesion inicial
     */
    private String profesionFinal;
    /**
     * Atributo que almacena el codigo de la ARP inicial
     */
    private String arpInicial;
    /**
     * Atributo que almacena la fecha de ingreso inicial
     * 
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha de ingreso final
     */
    private Date fechaFinal;
    /**
     * Atributo que almacena la fecha de nacimiento inicial
     */
    private Date fechaInicialNacimiento;
    /**
     * Atributo que almacena la fecha de nacimiento final
     */
    private Date fechaFinalNacimiento;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena los estado que tiene el personal
     */
    private List<Registro> listaEstadoActual;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que alamcena los cargos iniciales
     */
    private RegistroDataModelImpl listaCargoInicial;
    /**
     * Lista que alamcena los cargos finales
     */
    private RegistroDataModelImpl listaCargoFinal;
    /**
     * Lista que almacena los tipos de contratos iniciales
     */
    private RegistroDataModelImpl listaTipoContratoInicial;
    /**
     * Lista que almacena los tipos de contratos finales
     */
    private RegistroDataModelImpl listaTipoContratoFinal;
    /**
     * Lista que almacena las dependencias iniciales
     */
    private RegistroDataModelImpl listaDependenciaInicial;
    /**
     * Lista que almacena las dependencias finales
     */
    private RegistroDataModelImpl listaDependenciaFinal;

    /**
     * Lista que almacena las profesiones iniciales
     */
    private RegistroDataModelImpl listaProfesionInicial;

    /**
     * Lista que almacena las profesiones finales
     */
    private RegistroDataModelImpl listaProfesionFinal;

    /**
     * Lista que almacena las licencias iniciales
     */
    private RegistroDataModelImpl listaLicenciaInicial;
    /**
     * Lista que almacena las licencias finales
     */
    private RegistroDataModelImpl listaLicenciaFinal;
    /**
     * Lista que almacena las incapacidades iniciales
     */
    private RegistroDataModelImpl listaIncapacidadInicial;
    /**
     * Lista que almacena las incapacidades finales
     */
    private RegistroDataModelImpl listaIncapacidadFinal;

    /**
     * Lista que almacena las enfermedades iniciales
     */
    private RegistroDataModelImpl listaEnfermedadInicial;
    /**
     * Lista que almacena las enfermedades finales
     */
    private RegistroDataModelImpl listaEnfermedadFinal;

    /**
     * Lista que almacena las EPS iniciales
     */
    private RegistroDataModelImpl listaEPSInicial;
    /**
     * Lista que almacena las EPS finales
     */
    private RegistroDataModelImpl listaEPSFinal;
    /**
     * Lista que almacena las ARP iniciales
     */
    private RegistroDataModelImpl listaARPInicial;

    /**
     * Constantque almacena el valor de la cadena "procesoNomina"
     */
    private final String constanteProcesoNomina;

    /**
     * Constantque almacena el valor de la cadena "mesNomina"
     */
    private final String constanteMesNomina;
    /**
     * Constantque almacena el valor de la cadena "periodoNomina"
     */
    private final String constantePeriodoNomina;

    /**
     * Constantque almacena el valor de la cadena "condicion"
     */
    private final String constanteCondicion;

    private boolean validarRadio;

    @EJB
    private EjbNominaCeroGeneralLocal ejbNominaCero;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    /**
     * Reemplazos a enviar a la consulta del reporte
     */
    Map<String, Object> reemplazosReporte = new TreeMap<>();
    /**
     * Parametros a enviar al reporte
     */
    Map<String, Object> parametrosReporte = new TreeMap<>();

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ListadoDePersonalControlador
     */
    public ListadoDePersonalControlador() {
        super();
        compania = SessionUtil.getCompania();

        constanteProcesoNomina = "procesoNomina";
        constanteMesNomina = "mesNomina";
        constantePeriodoNomina = "periodoNomina";
        constanteCondicion = "condicion";

        try {
            procesoNomina = (String) SessionUtil
                            .getSessionVarContainer(constanteProcesoNomina);
            anoNomina = (String) SessionUtil
                            .getSessionVarContainer("anioNomina");
            mesNomina = (String) SessionUtil
                            .getSessionVarContainer(constanteMesNomina);
            periodoNomina = (String) SessionUtil
                            .getSessionVarContainer(constantePeriodoNomina);

            numFormulario = GeneralCodigoFormaEnum.LISTADO_DE_PERSONAL_CONTROLADOR
                            .getCodigo();
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();

            opcionPersonal = "1";
            estadoActual = "1";
            nombreEstado = "Activo";
            genero = "T";
            fechaInicial = SysmanFunciones.convertirAFecha("01/01/1900");
            fechaInicialNacimiento = SysmanFunciones
                            .convertirAFecha("01/01/1900");

            fechaFinal = new Date();
            fechaFinalNacimiento = new Date();

            if ("2109010102".equals(SessionUtil.getMenuActual())) {

                validarRadio = true;
            }
            else {
                validarRadio = false;
            }

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
        cargarListaCargoInicial();
        cargarListaTipoContratoInicial();
        cargarListaDependenciaInicial();
        cargarListaProfesionInicial();
        cargarListaLicenciaInicial();
        cargarListaIncapacidadInicial();
        cargarListaEnfermedadInicial();
        cargarListaEPSInicial();
        cargarListaARPInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaEstadoActual();
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
        asignarOrigenDatos();
        iniciarListas();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        // Metodo heredado de la clase BeanBase
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaEstadoActual
     *
     */
    public void cargarListaEstadoActual() {

        try {
            listaEstadoActual = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoDePersonalControladorUrlEnum.URL46374
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCargoInicial
     *
     */
    public void cargarListaCargoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL10010
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCargoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ListadoDePersonalControladorEnum.ID_DE_CARGO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaCargoFinal
     *
     */
    public void cargarListaCargoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL11242
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoDePersonalControladorEnum.CARGOINICIAL.getValue(),
                        cargoInicial);

        listaCargoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ListadoDePersonalControladorEnum.ID_DE_CARGO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaTipoContratoInicial
     *
     */
    public void cargarListaTipoContratoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL12567
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ListadoDePersonalControladorEnum.IDFORMA.getValue());
    }

    /**
     * 
     * Carga la lista listaTipoContratoFinal
     *
     */
    public void cargarListaTipoContratoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL13392
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoDePersonalControladorEnum.TIPOCONTRATOINICIAL
                        .getValue(),
                        tipoContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ListadoDePersonalControladorEnum.IDFORMA.getValue());
    }

    /**
     * 
     * Carga la lista listaDependenciaInicial
     *
     */
    public void cargarListaDependenciaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL14307
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDependenciaFinal
     *
     */
    public void cargarListaDependenciaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL15163
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoDePersonalControladorEnum.CODIGOINICIAL.getValue(),
                        dependenciaInicial);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaProfesionInicial
     *
     */
    public void cargarListaProfesionInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL16130
                                                        .getValue());

        listaProfesionInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true,
                        ListadoDePersonalControladorEnum.CODIGOPROF.getValue());
    }

    /**
     * 
     * Carga la lista listaProfesionFinal
     *
     */
    public void cargarListaProfesionFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL16687
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ListadoDePersonalControladorEnum.PROFESIONINICIAL.getValue(),
                        profesionInicial);

        listaProfesionFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ListadoDePersonalControladorEnum.CODIGOPROF.getValue());
    }

    /**
     * 
     * Carga la lista listaLicenciaInicial
     *
     */
    public void cargarListaLicenciaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL17358
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaLicenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ListadoDePersonalControladorEnum.LICENCIA.getValue());
    }

    /**
     * 
     * Carga la lista listaLicenciaFinal
     *
     */
    public void cargarListaLicenciaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL18188
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoDePersonalControladorEnum.LICENCIAINICIAL.getValue(),
                        licenciaInicial);

        listaLicenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ListadoDePersonalControladorEnum.LICENCIA.getValue());
    }

    /**
     * 
     * Carga la lista listaIncapacidadInicial
     *
     */
    public void cargarListaIncapacidadInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL19115
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaIncapacidadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ListadoDePersonalControladorEnum.INCAPACIDAD
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaIncapacidadFinal
     *
     */
    public void cargarListaIncapacidadFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL19913
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoDePersonalControladorEnum.INCAPACIDADINICIAL
                        .getValue(),
                        incapacidadInicial);

        listaIncapacidadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ListadoDePersonalControladorEnum.INCAPACIDAD
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaEnfermedadInicial
     *
     */
    public void cargarListaEnfermedadInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL20803
                                                        .getValue());

        listaEnfermedadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true,
                        ListadoDePersonalControladorEnum.CODIGOEN.getValue());
    }

    /**
     * 
     * Carga la lista listaEnfermedadFinal
     *
     */
    public void cargarListaEnfermedadFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL21621
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ListadoDePersonalControladorEnum.ENFERMEDADINICIAL.getValue(),
                        enfermedadInicial);

        listaEnfermedadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ListadoDePersonalControladorEnum.CODIGOEN.getValue());
    }

    /**
     * 
     * Carga la lista listaEPSInicial
     *
     */
    public void cargarListaEPSInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL22548
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaEPSInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ListadoDePersonalControladorEnum.FONDO_SALUD
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaEPSFinal
     *
     */
    public void cargarListaEPSFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL23100
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ListadoDePersonalControladorEnum.EPSINICIAL.getValue(),
                        epsInicial);

        listaEPSFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ListadoDePersonalControladorEnum.FONDO_SALUD
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaARPInicial
     *
     */
    public void cargarListaARPInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoDePersonalControladorUrlEnum.URL23723
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(ListadoDePersonalControladorEnum.TIPOADMINISTRADORA
                        .getValue(),
                        "03");

        listaARPInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO_FONDO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control EstadoActual
     * 
     */
    public void cambiarEstadoActual() {
        nombreEstado = service.buscarEnLista(estadoActual,
                        GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaEstadoActual);

    }

    public void cambiarMarco10() {

        opcionPensionado = "";
        estadoActual = "1";

        if ("6".equals(opcionPersonal)) {
            estadoActual = "3";
        }

        nombreEstado = service.buscarEnLista(estadoActual,
                        GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaEstadoActual);

    }

    public void cambiarMarco20() {
        opcionPersonal = "";
        estadoActual = "2";

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCargoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cargoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.ID_DE_CARGO
                                                        .getValue()),
                                        "")
                        .toString();

        cargoFinal = "";
        cargarListaCargoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCargoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cargoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.ID_DE_CARGO
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoContratoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.IDFORMA
                                                        .getValue()),
                                        "")
                        .toString();

        tipoContratoFinal = "";
        cargarListaTipoContratoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoContratoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.IDFORMA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString();

        dependenciaFinal = "";
        cargarListaDependenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProfesionInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProfesionInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        profesionInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.CODIGOPROF
                                                        .getValue()),
                                        "")
                        .toString();

        profesionFinal = "";
        cargarListaProfesionFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProfesionFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProfesionFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        profesionFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.CODIGOPROF
                                                        .getValue()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaLicenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaLicenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        licenciaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.LICENCIA
                                                        .getValue()),
                                        "")
                        .toString();

        licenciaFinal = "";
        cargarListaLicenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaLicenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaLicenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        licenciaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.LICENCIA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIncapacidadInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIncapacidadInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        incapacidadInicial = registroAux.getCampos().get(
                        ListadoDePersonalControladorEnum.INCAPACIDAD.getValue())
                        .toString();

        incapacidadFinal = "";
        cargarListaIncapacidadFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIncapacidadFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIncapacidadFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        incapacidadFinal = registroAux.getCampos().get(
                        ListadoDePersonalControladorEnum.INCAPACIDAD.getValue())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEnfermedadInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEnfermedadInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        enfermedadInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.CODIGOEN
                                                        .getValue()),
                                        "")
                        .toString();

        enfermedadFinal = "";
        cargarListaEnfermedadFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEnfermedadFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEnfermedadFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        enfermedadFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.CODIGOEN
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEPSInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEPSInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        epsInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.FONDO_SALUD
                                                        .getValue()),
                                        "")
                        .toString();

        epsFinal = "";
        cargarListaEPSFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEPSFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEPSFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        epsFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ListadoDePersonalControladorEnum.FONDO_SALUD
                                                        .getValue()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaARPInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaARPInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        arpInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO_FONDO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton PDFPersonal en la vista
     *
     */
    public void oprimirPDFPersonal() {
        // <CODIGO_DESARROLLADO>
        if (validarOpcionPersonalVacio()) {
            return;
        }

        archivoDescarga = null;
        generarReportePersonal(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton ContratosVencer en la
     * vista
     *
     */

    public void oprimirContratosVencer() {

        archivoDescarga = null;
        try {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            Date fechaInicialVencer = ejbNominaCero.getFechaPeriodoIniFin(
                            compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anoNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), true, false);

            Date fechaFinalVencer = ejbNominaCero.getFechaPeriodoIniFin(
                            compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anoNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), false, false);

            reemplazar.put("fechaInicio", SysmanFunciones
                            .formatearFechaCadena(fechaInicialVencer,
                                            "DD/MM/YYYY"));

            reemplazar.put(ListadoDePersonalControladorEnum.FECHAINICIAL
                            .getValue(), SysmanFunciones
                                            .formatearFechaCadena(
                                                            fechaFinalVencer,
                                                            "DD/MM/YYYY"));

            parametros.put(ListadoDePersonalControladorEnum.PRNOMBREEMPRESA
                            .getValue(), nombreEmpresa);

            Reporteador.resuelveConsulta("000003PersonalVenceContrato",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000003PersonalVenceContrato", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton ExcelPersonal en la vista
     *
     */
    public void oprimirExcelPersonal() {
        // <CODIGO_DESARROLLADO>

        if (validarOpcionPersonalVacio()) {
            return;
        }
        archivoDescarga = null;
        generarReportePersonal(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PDFPensionados en la vista
     *
     *
     */
    public void oprimirPDFPensionados() {
        // <CODIGO_DESARROLLADO>

        if (validarOpcionPensionadoVacio()) {
            return;
        }

        archivoDescarga = null;
        generarReportePensionados(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ExcelPensionados en la
     * vista
     *
     *
     */
    public void oprimirExcelPensionados() {
        // <CODIGO_DESARROLLADO>
        if (validarOpcionPensionadoVacio()) {
            return;
        }
        archivoDescarga = null;
        generarReportePensionados(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarOpcionPersonalVacio() {
        if (SysmanFunciones.validarVariableVacio(opcionPersonal)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3970"));
            return true;
        }
        return false;
    }

    private boolean validarOpcionPensionadoVacio() {
        if (SysmanFunciones.validarVariableVacio(opcionPensionado)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3971"));
            return true;
        }
        return false;
    }
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    private void generarReportePersonal(FORMATOS formato) {
        String reporte;
        reemplazosReporte = new TreeMap<>();

        parametrosReporte = new TreeMap<>();
        try {

            if (!validarCamposVacios() || !validarCamposVaciosDos()
                || !validarCamposVaciosTres()) {
                return;
            }

            reporte = seleccionarReportePersonal();

            reemplazosReporte.put("compania", compania);
            reemplazosReporte.put("estadoActual", estadoActual);
            reemplazosReporte.put("genero", genero);
            reemplazosReporte.put("cargoInicial", cargoInicial);
            reemplazosReporte.put("cargoFinal", cargoFinal);
            reemplazosReporte.put("tipoContratoInicial", tipoContratoInicial);
            reemplazosReporte.put("tipoContratoFinal", tipoContratoFinal);
            reemplazosReporte.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazosReporte.put(ListadoDePersonalControladorEnum.FECHAINICIAL
                            .getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazosReporte.put("fechaInicialNacimiento", SysmanFunciones
                            .convertirAFechaCadena(fechaInicialNacimiento));
            reemplazosReporte.put("fechaFinalNacimiento", SysmanFunciones
                            .convertirAFechaCadena(fechaFinalNacimiento));
            reemplazosReporte.put("fechaFinalNacimiento", SysmanFunciones
                            .convertirAFechaCadena(fechaFinalNacimiento));

            reemplazosReporte.put("nombreEstado", nombreEstado);

            reemplazosReporte.put(constanteProcesoNomina, procesoNomina);
            reemplazosReporte.put("anoNomina", anoNomina);
            reemplazosReporte.put(constanteMesNomina, mesNomina);
            reemplazosReporte.put(constantePeriodoNomina, periodoNomina);

            parametrosReporte
                            .put(ListadoDePersonalControladorEnum.PRNOMBREEMPRESA
                                            .getValue(),
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNombre());

            parametrosReporte.put("PR_NOMBRE_ESTADOACTUAL",
                            nombreEstado);

            parametrosReporte.put(
                            "PR_FORMS_LISTADO_DE_PERSONAL_FECHAINICIAL",
                            SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaInicial));

            parametrosReporte.put("PR_FORMS_LISTADO_DE_PERSONAL_FECHAFINAL",
                            SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazosReporte, parametrosReporte);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametrosReporte,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarReportePensionados(FORMATOS formato) {

        reemplazosReporte = new TreeMap<>();

        parametrosReporte = new TreeMap<>();

        String reporte = seleccionarReportePensionados();
        try {

            reemplazosReporte.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazosReporte.put(ListadoDePersonalControladorEnum.FECHAINICIAL
                            .getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            reemplazosReporte.put(constanteProcesoNomina, procesoNomina);
            reemplazosReporte.put("anoNomina", anoNomina);
            reemplazosReporte.put(constanteMesNomina, mesNomina);
            reemplazosReporte.put(constantePeriodoNomina, periodoNomina);

            parametrosReporte
                            .put(ListadoDePersonalControladorEnum.PRNOMBREEMPRESA
                                            .getValue(),
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNombre());

            parametrosReporte.put(
                            "PR_FORMS_LISTADO_DE_PERSONAL_FECHAINICIAL",
                            SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaInicial));

            parametrosReporte.put("PR_FORMS_LISTADO_DE_PERSONAL_FECHAFINAL",
                            SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazosReporte, parametrosReporte);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametrosReporte,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarCamposVacios() {

        if ("9".equals(opcionPersonal)
            && (SysmanFunciones.validarVariableVacio(profesionInicial)
                || SysmanFunciones.validarVariableVacio(profesionFinal))) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3932"));

            return false;
        }
        else if ("10".equals(opcionPersonal)
            && (SysmanFunciones.validarVariableVacio(licenciaInicial)
                || SysmanFunciones.validarVariableVacio(licenciaFinal))) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3965"));

            return false;
        }

        return true;
    }

    private boolean validarCamposVaciosTres() {
        if ("11".equals(opcionPersonal)
            && (SysmanFunciones.validarVariableVacio(incapacidadInicial)
                || SysmanFunciones.validarVariableVacio(incapacidadFinal)
                || SysmanFunciones.validarVariableVacio(enfermedadInicial)
                || SysmanFunciones.validarVariableVacio(enfermedadFinal))) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3966"));

            return false;
        }

        else if ("12".equals(opcionPersonal)
            && (SysmanFunciones.validarVariableVacio(epsInicial)
                || SysmanFunciones.validarVariableVacio(epsFinal))) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3967"));

            return false;
        }
        return true;
    }

    private boolean validarCamposVaciosDos() {
        if ("12".equals(opcionPersonal)
            && (!incapacidades
                && !licenciaMaternidad)) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3968"));

            return false;
        }

        else if ("13".equals(opcionPersonal)
            && (SysmanFunciones.validarVariableVacio(arpInicial))) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3969"));

            return false;
        }
        return true;
    }

    private String seleccionarReportePersonal() {
        String reporte = "";
        switch (opcionPersonal) {
        case "1":
            reporte = "001632PersonalporCodigo";

            break;

        case "2":
            reporte = "001633PersonalPorCedula";

            break;

        case "3":
            reporte = "001634PersonalPorAlfabetico";

            break;

        case "4":
            reporte = "001635PersonalDocentes";

            break;

        case "5":
            reporte = "001639PERSONALENTREFECHASACTIVO";

            break;

        case "6":
            reporte = "001642personalentrefechasretirado";

            break;

        case "7":
            reporte = "001643PERSONALENTREFECHASENCARGOS";

            break;

        case "8":
            reporte = "001644PERSONALFORMADENOMBRAMIENTO";

            break;

        case "9":
            reporte = "001656PERSONALPORPROFESIONES";
            crearReemplazosProfesiones();
            break;

        case "10":
            reporte = "001659PERSONALLICENCIAS";
            crearReemplazosLicencias();

            break;

        case "11":
            reporte = "001670PERSONALINCAPACIDADES";
            crearReemplazosIncapacidades();

            break;

        case "12":
            reporte = "001672INCAPACIDADESDESCONTADAS";
            crearReemplazosEps();

            break;

        case "13":
            reporte = "001674INCAPACIDADESARPDESCONTADAS";
            crearReemplazoArp();

            break;

        default:
            break;
        }
        return reporte;
    }

    private void crearReemplazosProfesiones() {
        reemplazosReporte.put("profesionInicial", profesionInicial);
        reemplazosReporte.put("profesionFinal", profesionFinal);

    }

    private void crearReemplazosLicencias() {
        reemplazosReporte.put("licenciaInicial", licenciaInicial);
        reemplazosReporte.put("licenciaFinal", licenciaFinal);

    }

    private void crearReemplazosIncapacidades() {
        reemplazosReporte.put("incapacidadInicial", incapacidadInicial);
        reemplazosReporte.put("incapacidadFinal", incapacidadFinal);
        reemplazosReporte.put("enfermedadInicial", enfermedadInicial);
        reemplazosReporte.put("enfermedadFinal", enfermedadFinal);

    }

    private void crearReemplazosEps() {
        reemplazosReporte.put(constanteCondicion, "");
        reemplazosReporte.put("epsInicial", epsInicial);
        reemplazosReporte.put("epsFinal", epsFinal);

        if (incapacidades) {
            reemplazosReporte.put(constanteCondicion,
                            " AND NOVEDADES_AUTOLIQUIDACION.VR_INCAP NOT IN (0) ");
        }

        if (licenciaMaternidad) {
            reemplazosReporte.put(constanteCondicion,
                            " AND NOVEDADES_AUTOLIQUIDACION.VR_MAT NOT IN (0) ");
        }

        if (incapacidades && licenciaMaternidad) {
            reemplazosReporte.put(constanteCondicion,
                            " AND NOVEDADES_AUTOLIQUIDACION.VR_INCAP NOT IN (0) "
                                + " AND NOVEDADES_AUTOLIQUIDACION.VR_MAT NOT IN (0) ");
        }

    }

    private void crearReemplazoArp() {
        reemplazosReporte.put("arpInicial", arpInicial);

    }

    private String seleccionarReportePensionados() {
        String reporte = "";
        switch (opcionPensionado) {
        case "1":
        case "3":
            reporte = "001683PersonalPensionados";
            break;
        case "2":
        case "4":
            reporte = "001685PersonalPensionadosRET2";
            break;
        case "5":
            reporte = "001687PersonalPensionadoCausantes";
            break;

        case "6":
            reporte = "001689PersonalPensionadoCausantesret";
            break;

        default:
            break;
        }
        return reporte;
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

    // <SET_GET_ATRIBUTOS>

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    /**
     * Retorna la variable opcionPersonal
     * 
     * @return opcionPersonal
     */
    public String getOpcionPersonal() {
        return opcionPersonal;
    }

    /**
     * Asigna la variable opcionPersonal
     * 
     * @param opcionPersonal
     * Variable a asignar en opcionPersonal
     */
    public void setOpcionPersonal(String opcionPersonal) {
        this.opcionPersonal = opcionPersonal;
    }

    /**
     * Retorna la variable opcionPensionado
     * 
     * @return opcionPensionado
     */
    public String getOpcionPensionado() {
        return opcionPensionado;
    }

    /**
     * Asigna la variable opcionPensionado
     * 
     * @param opcionPensionado
     * Variable a asignar en opcionPensionado
     */
    public void setOpcionPensionado(String opcionPensionado) {
        this.opcionPensionado = opcionPensionado;
    }

    public boolean isIncapacidades() {
        return incapacidades;
    }

    public void setIncapacidades(boolean incapacidades) {
        this.incapacidades = incapacidades;
    }

    public boolean isLicenciaMaternidad() {
        return licenciaMaternidad;
    }

    public void setLicenciaMaternidad(boolean licenciaMaternidad) {
        this.licenciaMaternidad = licenciaMaternidad;
    }

    /**
     * Retorna la variable genero
     * 
     * @return genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * Asigna la variable genero
     * 
     * @param genero
     * Variable a asignar en genero
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * Retorna la variable cargoInicial
     * 
     * @return cargoInicial
     */
    public String getCargoInicial() {
        return cargoInicial;
    }

    /**
     * Asigna la variable cargoInicial
     * 
     * @param cargoInicial
     * Variable a asignar en cargoInicial
     */
    public void setCargoInicial(String cargoInicial) {
        this.cargoInicial = cargoInicial;
    }

    /**
     * Retorna la variable cargoFinal
     * 
     * @return cargoFinal
     */
    public String getCargoFinal() {
        return cargoFinal;
    }

    /**
     * Asigna la variable cargoFinal
     * 
     * @param cargoFinal
     * Variable a asignar en cargoFinal
     */
    public void setCargoFinal(String cargoFinal) {
        this.cargoFinal = cargoFinal;
    }

    /**
     * Retorna la variable estadoActual
     * 
     * @return estadoActual
     */
    public String getEstadoActual() {
        return estadoActual;
    }

    /**
     * Asigna la variable estadoActual
     * 
     * @param estadoActual
     * Variable a asignar en estadoActual
     */
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    /**
     * Retorna la variable tipoContratoInicial
     * 
     * @return tipoContratoInicial
     */
    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    /**
     * Asigna la variable tipoContratoInicial
     * 
     * @param tipoContratoInicial
     * Variable a asignar en tipoContratoInicial
     */
    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    /**
     * Retorna la variable tipoContratoFinal
     * 
     * @return tipoContratoFinal
     */
    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    /**
     * Asigna la variable tipoContratoFinal
     * 
     * @param tipoContratoFinal
     * Variable a asignar en tipoContratoFinal
     */
    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    /**
     * Retorna la variable profesionInicial
     * 
     * @return profesionInicial
     */
    public String getProfesionInicial() {
        return profesionInicial;
    }

    /**
     * Asigna la variable profesionInicial
     * 
     * @param profesionInicial
     * Variable a asignar en profesionInicial
     */
    public void setProfesionInicial(String profesionInicial) {
        this.profesionInicial = profesionInicial;
    }

    /**
     * Retorna la variable dependenciaInicial
     * 
     * @return dependenciaInicial
     */
    public String getDependenciaInicial() {
        return dependenciaInicial;
    }

    /**
     * Asigna la variable dependenciaInicial
     * 
     * @param dependenciaInicial
     * Variable a asignar en dependenciaInicial
     */
    public void setDependenciaInicial(String dependenciaInicial) {
        this.dependenciaInicial = dependenciaInicial;
    }

    /**
     * Retorna la variable dependenciaFinal
     * 
     * @return dependenciaFinal
     */
    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    /**
     * Asigna la variable dependenciaFinal
     * 
     * @param dependenciaFinal
     * Variable a asignar en dependenciaFinal
     */
    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
    }

    /**
     * Retorna la variable incapacidadInicial
     * 
     * @return incapacidadInicial
     */
    public String getIncapacidadInicial() {
        return incapacidadInicial;
    }

    /**
     * Asigna la variable incapacidadInicial
     * 
     * @param incapacidadInicial
     * Variable a asignar en incapacidadInicial
     */
    public void setIncapacidadInicial(String incapacidadInicial) {
        this.incapacidadInicial = incapacidadInicial;
    }

    /**
     * Retorna la variable incapacidadFinal
     * 
     * @return incapacidadFinal
     */
    public String getIncapacidadFinal() {
        return incapacidadFinal;
    }

    /**
     * Asigna la variable incapacidadFinal
     * 
     * @param incapacidadFinal
     * Variable a asignar en incapacidadFinal
     */
    public void setIncapacidadFinal(String incapacidadFinal) {
        this.incapacidadFinal = incapacidadFinal;
    }

    /**
     * Retorna la variable enfermedadInicial
     * 
     * @return enfermedadInicial
     */
    public String getEnfermedadInicial() {
        return enfermedadInicial;
    }

    /**
     * Asigna la variable enfermedadInicial
     * 
     * @param enfermedadInicial
     * Variable a asignar en enfermedadInicial
     */
    public void setEnfermedadInicial(String enfermedadInicial) {
        this.enfermedadInicial = enfermedadInicial;
    }

    /**
     * Retorna la variable enfermedadFinal
     * 
     * @return enfermedadFinal
     */
    public String getEnfermedadFinal() {
        return enfermedadFinal;
    }

    /**
     * Asigna la variable enfermedadFinal
     * 
     * @param enfermedadFinal
     * Variable a asignar en enfermedadFinal
     */
    public void setEnfermedadFinal(String enfermedadFinal) {
        this.enfermedadFinal = enfermedadFinal;
    }

    /**
     * Retorna la variable licenciaInicial
     * 
     * @return licenciaInicial
     */
    public String getLicenciaInicial() {
        return licenciaInicial;
    }

    /**
     * Asigna la variable licenciaInicial
     * 
     * @param licenciaInicial
     * Variable a asignar en licenciaInicial
     */
    public void setLicenciaInicial(String licenciaInicial) {
        this.licenciaInicial = licenciaInicial;
    }

    /**
     * Retorna la variable licenciaFinal
     * 
     * @return licenciaFinal
     */
    public String getLicenciaFinal() {
        return licenciaFinal;
    }

    /**
     * Asigna la variable licenciaFinal
     * 
     * @param licenciaFinal
     * Variable a asignar en licenciaFinal
     */
    public void setLicenciaFinal(String licenciaFinal) {
        this.licenciaFinal = licenciaFinal;
    }

    /**
     * Retorna la variable epsInicial
     * 
     * @return epsInicial
     */
    public String getEpsInicial() {
        return epsInicial;
    }

    /**
     * Asigna la variable epsInicial
     * 
     * @param epsInicial
     * Variable a asignar en epsInicial
     */
    public void setEpsInicial(String epsInicial) {
        this.epsInicial = epsInicial;
    }

    /**
     * Retorna la variable epsFinal
     * 
     * @return epsFinal
     */
    public String getEpsFinal() {
        return epsFinal;
    }

    /**
     * Asigna la variable epsFinal
     * 
     * @param epsFinal
     * Variable a asignar en epsFinal
     */
    public void setEpsFinal(String epsFinal) {
        this.epsFinal = epsFinal;
    }

    /**
     * Retorna la variable profesionFinal
     * 
     * @return profesionFinal
     */
    public String getProfesionFinal() {
        return profesionFinal;
    }

    /**
     * Asigna la variable profesionFinal
     * 
     * @param profesionFinal
     * Variable a asignar en profesionFinal
     */
    public void setProfesionFinal(String profesionFinal) {
        this.profesionFinal = profesionFinal;
    }

    /**
     * Retorna la variable arpInicial
     * 
     * @return arpInicial
     */
    public String getArpInicial() {
        return arpInicial;
    }

    /**
     * Asigna la variable arpInicial
     * 
     * @param arpInicial
     * Variable a asignar en arpInicial
     */
    public void setArpInicial(String arpInicial) {
        this.arpInicial = arpInicial;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable fechaInicialNacimiento
     * 
     * @return fechaInicialNacimiento
     */
    public Date getFechaInicialNacimiento() {
        return fechaInicialNacimiento;
    }

    /**
     * Asigna la variable fechaInicialNacimiento
     * 
     * @param fechaInicialNacimiento
     * Variable a asignar en fechaInicialNacimiento
     */
    public void setFechaInicialNacimiento(Date fechaInicialNacimiento) {
        this.fechaInicialNacimiento = fechaInicialNacimiento;
    }

    /**
     * Retorna la variable fechaFinalNacimiento
     * 
     * @return fechaFinalNacimiento
     */
    public Date getFechaFinalNacimiento() {
        return fechaFinalNacimiento;
    }

    /**
     * Asigna la variable fechaFinalNacimiento
     * 
     * @param fechaFinalNacimiento
     * Variable a asignar en fechaFinalNacimiento
     */
    public void setFechaFinalNacimiento(Date fechaFinalNacimiento) {
        this.fechaFinalNacimiento = fechaFinalNacimiento;
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
     * Retorna la lista listaEstadoActual
     * 
     * @return listaEstadoActual
     */
    public List<Registro> getListaEstadoActual() {
        return listaEstadoActual;
    }

    /**
     * Asigna la lista listaEstadoActual
     * 
     * @param listaEstadoActual
     * Variable a asignar en listaEstadoActual
     */
    public void setListaEstadoActual(List<Registro> listaEstadoActual) {
        this.listaEstadoActual = listaEstadoActual;
    }

    /**
     * Retorna la lista listaProfesionInicial
     * 
     * @return listaProfesionInicial
     */
    public RegistroDataModelImpl getListaProfesionInicial() {
        return listaProfesionInicial;
    }

    /**
     * Asigna la lista listaProfesionInicial
     * 
     * @param listaProfesionInicial
     * Variable a asignar en listaProfesionInicial
     */
    public void setListaProfesionInicial(
        RegistroDataModelImpl listaProfesionInicial) {
        this.listaProfesionInicial = listaProfesionInicial;
    }

    /**
     * Retorna la lista listaEnfermedadInicial
     * 
     * @return listaEnfermedadInicial
     */
    public RegistroDataModelImpl getListaEnfermedadInicial() {
        return listaEnfermedadInicial;
    }

    /**
     * Asigna la lista listaEnfermedadInicial
     * 
     * @param listaEnfermedadInicial
     * Variable a asignar en listaEnfermedadInicial
     */
    public void setListaEnfermedadInicial(
        RegistroDataModelImpl listaEnfermedadInicial) {
        this.listaEnfermedadInicial = listaEnfermedadInicial;
    }

    /**
     * Retorna la lista listaEnfermedadFinal
     * 
     * @return listaEnfermedadFinal
     */
    public RegistroDataModelImpl getListaEnfermedadFinal() {
        return listaEnfermedadFinal;
    }

    /**
     * Asigna la lista listaEnfermedadFinal
     * 
     * @param listaEnfermedadFinal
     * Variable a asignar en listaEnfermedadFinal
     */
    public void setListaEnfermedadFinal(
        RegistroDataModelImpl listaEnfermedadFinal) {
        this.listaEnfermedadFinal = listaEnfermedadFinal;
    }

    /**
     * Retorna la lista listaLicenciaInicial
     * 
     * @return listaLicenciaInicial
     */
    public RegistroDataModelImpl getListaLicenciaInicial() {
        return listaLicenciaInicial;
    }

    /**
     * Asigna la lista listaLicenciaInicial
     * 
     * @param listaLicenciaInicial
     * Variable a asignar en listaLicenciaInicial
     */
    public void setListaLicenciaInicial(
        RegistroDataModelImpl listaLicenciaInicial) {
        this.listaLicenciaInicial = listaLicenciaInicial;
    }

    /**
     * Retorna la lista listaLicenciaFinal
     * 
     * @return listaLicenciaFinal
     */
    public RegistroDataModelImpl getListaLicenciaFinal() {
        return listaLicenciaFinal;
    }

    /**
     * Asigna la lista listaLicenciaFinal
     * 
     * @param listaLicenciaFinal
     * Variable a asignar en listaLicenciaFinal
     */
    public void setListaLicenciaFinal(
        RegistroDataModelImpl listaLicenciaFinal) {
        this.listaLicenciaFinal = listaLicenciaFinal;
    }

    /**
     * Retorna la lista listaEPSInicial
     * 
     * @return listaEPSInicial
     */
    public RegistroDataModelImpl getListaEPSInicial() {
        return listaEPSInicial;
    }

    /**
     * Asigna la lista listaEPSInicial
     * 
     * @param listaEPSInicial
     * Variable a asignar en listaEPSInicial
     */
    public void setListaEPSInicial(RegistroDataModelImpl listaEPSInicial) {
        this.listaEPSInicial = listaEPSInicial;
    }

    /**
     * Retorna la lista listaEPSFinal
     * 
     * @return listaEPSFinal
     */
    public RegistroDataModelImpl getListaEPSFinal() {
        return listaEPSFinal;
    }

    /**
     * Asigna la lista listaEPSFinal
     * 
     * @param listaEPSFinal
     * Variable a asignar en listaEPSFinal
     */
    public void setListaEPSFinal(RegistroDataModelImpl listaEPSFinal) {
        this.listaEPSFinal = listaEPSFinal;
    }

    /**
     * Retorna la lista listaProfesionFinal
     * 
     * @return listaProfesionFinal
     */
    public RegistroDataModelImpl getListaProfesionFinal() {
        return listaProfesionFinal;
    }

    /**
     * Asigna la lista listaProfesionFinal
     * 
     * @param listaProfesionFinal
     * Variable a asignar en listaProfesionFinal
     */
    public void setListaProfesionFinal(
        RegistroDataModelImpl listaProfesionFinal) {
        this.listaProfesionFinal = listaProfesionFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCargoInicial
     * 
     * @return listaCargoInicial
     */
    public RegistroDataModelImpl getListaCargoInicial() {
        return listaCargoInicial;
    }

    /**
     * Asigna la lista listaCargoInicial
     * 
     * @param listaCargoInicial
     * Variable a asignar en listaCargoInicial
     */
    public void setListaCargoInicial(RegistroDataModelImpl listaCargoInicial) {
        this.listaCargoInicial = listaCargoInicial;
    }

    /**
     * Retorna la lista listaCargoFinal
     * 
     * @return listaCargoFinal
     */
    public RegistroDataModelImpl getListaCargoFinal() {
        return listaCargoFinal;
    }

    /**
     * Asigna la lista listaCargoFinal
     * 
     * @param listaCargoFinal
     * Variable a asignar en listaCargoFinal
     */
    public void setListaCargoFinal(RegistroDataModelImpl listaCargoFinal) {
        this.listaCargoFinal = listaCargoFinal;
    }

    /**
     * Retorna la lista listaTipoContratoInicial
     * 
     * @return listaTipoContratoInicial
     */
    public RegistroDataModelImpl getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    /**
     * Asigna la lista listaTipoContratoInicial
     * 
     * @param listaTipoContratoInicial
     * Variable a asignar en listaTipoContratoInicial
     */
    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    /**
     * Retorna la lista listaTipoContratoFinal
     * 
     * @return listaTipoContratoFinal
     */
    public RegistroDataModelImpl getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    /**
     * Asigna la lista listaTipoContratoFinal
     * 
     * @param listaTipoContratoFinal
     * Variable a asignar en listaTipoContratoFinal
     */
    public void setListaTipoContratoFinal(
        RegistroDataModelImpl listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    /**
     * Retorna la lista listaDependenciaInicial
     * 
     * @return listaDependenciaInicial
     */
    public RegistroDataModelImpl getListaDependenciaInicial() {
        return listaDependenciaInicial;
    }

    /**
     * Asigna la lista listaDependenciaInicial
     * 
     * @param listaDependenciaInicial
     * Variable a asignar en listaDependenciaInicial
     */
    public void setListaDependenciaInicial(
        RegistroDataModelImpl listaDependenciaInicial) {
        this.listaDependenciaInicial = listaDependenciaInicial;
    }

    /**
     * Retorna la lista listaDependenciaFinal
     * 
     * @return listaDependenciaFinal
     */
    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    /**
     * Asigna la lista listaDependenciaFinal
     * 
     * @param listaDependenciaFinal
     * Variable a asignar en listaDependenciaFinal
     */
    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }

    /**
     * Retorna la lista listaIncapacidadInicial
     * 
     * @return listaIncapacidadInicial
     */
    public RegistroDataModelImpl getListaIncapacidadInicial() {
        return listaIncapacidadInicial;
    }

    /**
     * Asigna la lista listaIncapacidadInicial
     * 
     * @param listaIncapacidadInicial
     * Variable a asignar en listaIncapacidadInicial
     */
    public void setListaIncapacidadInicial(
        RegistroDataModelImpl listaIncapacidadInicial) {
        this.listaIncapacidadInicial = listaIncapacidadInicial;
    }

    /**
     * Retorna la lista listaIncapacidadFinal
     * 
     * @return listaIncapacidadFinal
     */
    public RegistroDataModelImpl getListaIncapacidadFinal() {
        return listaIncapacidadFinal;
    }

    /**
     * Asigna la lista listaIncapacidadFinal
     * 
     * @param listaIncapacidadFinal
     * Variable a asignar en listaIncapacidadFinal
     */
    public void setListaIncapacidadFinal(
        RegistroDataModelImpl listaIncapacidadFinal) {
        this.listaIncapacidadFinal = listaIncapacidadFinal;
    }

    /**
     * Retorna la lista listaARPInicial
     * 
     * @return listaARPInicial
     */
    public RegistroDataModelImpl getListaARPInicial() {
        return listaARPInicial;
    }

    /**
     * Asigna la lista listaARPInicial
     * 
     * @param listaARPInicial
     * Variable a asignar en listaARPInicial
     */
    public void setListaARPInicial(RegistroDataModelImpl listaARPInicial) {
        this.listaARPInicial = listaARPInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public boolean isValidarRadio() {
        return validarRadio;
    }

    public void setValidarRadio(boolean validarRadio) {
        this.validarRadio = validarRadio;
    }

}
