/*-
 * FrmViaticosControlador.java
 *
 * 1.0
 * 
 * 17/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos;

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
import com.sysman.componentes.Direccionador;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.ejb.EjbViaticosCeroRemote;
import com.sysman.viaticos.enums.FrmViaticosControladorEnum;
import com.sysman.viaticos.enums.FrmViaticosControladorUrlEnum;

/**
 * Formulario que administra la solicitud de viaticos
 *
 * @version 1.0, 18/01/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmViaticosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el nombre del cargo del solictante
     */
    private String nombreCargo;
    /**
     * Atributo que almacena el nombre del responsable solictante
     */
    private String nombreResponsable;
    /**
     * Atributo que almacena los dias habiles entre las dos fechas que
     * se seleccionan en la vista
     */
    private String diasHabiles;
    /**
     * Atributo que almacena el nombre del tipo de solicitud
     */
    private String nombreTipoSolicitud;

    /**
     * Atributo que gestiona la visibilidad del botn imprimir
     */
    private boolean verImprimir;

    /**
     * Atributo que administra la inactividad de los objetos
     * relacionados con transporte
     */
    private boolean bloqueaTransporte;

    /**
     * Atributo que administra la inactividad de los objetos
     * relacionados con el periodo de viaticos
     */
    private boolean bloquearPeriodo;

    /**
     * Atributo que administra la inactividad del indicador de
     * aprobado
     */
    private boolean bloqueaAprobacion;

    /**
     * Atributo que gestiona el bloqueo del boton Detalles Viaticos
     */
    private boolean bloqueaDetalles;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga las ciudades destino
     */
    private List<Registro> listaCiudadeDestino;
    /**
     * Lista que carga los paises destino
     */
    private List<Registro> listaPaisDestino;
    /**
     * Lista que carga los departamentos destino
     */
    private List<Registro> listaDepartamentoDestino;
    /**
     * Lista que carga las ciudades origen
     */
    private List<Registro> listaCiudadOrigen;
    /**
     * Lista que carga los paises origen
     */
    private List<Registro> listaPaisOrigen;
    /**
     * Lista que carga los departamentos origen
     */
    private List<Registro> listaDepartamentoOrigen;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lsita que carga las dependencias
     */
    private RegistroDataModelImpl listaMisional;
    /**
     * Lsita que carga los tipos de visita
     */
    private RegistroDataModelImpl listaTipoVisita;
    /**
     * Lsita que carga los tipos de solicitud
     */
    private RegistroDataModelImpl listaTipoSolicitud;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbViaticosCeroRemote ejbViaticosCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmViaticosControlador
     */
    public FrmViaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        bloqueaDetalles = false;
        verImprimir = false;

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_VIATICOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");

                boolean retorno = SysmanFunciones
                                .nvl(parametrosEntrada.get("retorno"), "false")
                                .toString() != null;

                if (retorno) {
                    accion = "v";
                    SessionUtil.setFlash(null);

                }
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
        cargarListaMisional();
        cargarListaTipoVisita();
        cargarListaTipoSolicitud();
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
        enumBase = GenericUrlEnum.VI_VIATICOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaMisional
     *
     */
    public void cargarListaMisional() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmViaticosControladorUrlEnum.URL7394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaMisional = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaTipoVisita
     *
     */
    public void cargarListaTipoVisita() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmViaticosControladorUrlEnum.URL8063
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoVisita = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODTVISITA");
    }

    /**
     * 
     * Carga la lista listaTipoSolicitud
     *
     */
    public void cargarListaTipoSolicitud() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmViaticosControladorUrlEnum.URL8727
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmViaticosControladorEnum.CODTIPO.getValue());
    }

    /**
     * 
     * Carga la lista listaPaisOrigen
     *
     */
    public void cargarListaPaisOrigen() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos()
                                        .get(GeneralParameterEnum.FECHA
                                                        .getName())));

        try {
            listaPaisOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL9023
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDepartamentoOrigen
     *
     */
    public void cargarListaDepartamentoOrigen() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put("PAIS", registro.getCampos().get("PAIS_ORIGEN"));

        param.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos()
                                        .get(GeneralParameterEnum.FECHA
                                                        .getName())));

        try {
            listaDepartamentoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL5050
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCiudadOrigen
     *
     */
    public void cargarListaCiudadOrigen() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("PAIS", registro.getCampos().get("PAIS_ORIGEN"));

        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO_ORIGEN"));
        param.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos()
                                        .get(GeneralParameterEnum.FECHA
                                                        .getName())));

        try {
            listaCiudadOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL12916
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPaisDestino
     *
     */
    public void cargarListaPaisDestino() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos()
                                        .get(GeneralParameterEnum.FECHA
                                                        .getName())));

        try {
            listaPaisDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL15119
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDepartamentoDestino
     *
     */
    public void cargarListaDepartamentoDestino() {

        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put("PAIS", registro.getCampos().get("PAIS"));

            param.put(GeneralParameterEnum.TERCERO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TERCERO
                                                            .getName()));

            param.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano((Date) registro.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName())));

            listaDepartamentoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL23235
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCiudadeDestino
     *
     */
    public void cargarListaCiudadeDestino() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("PAIS",
                        registro.getCampos().get("PAIS"));
        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos()
                                        .get(FrmViaticosControladorEnum.DEPARTAMENTO
                                                        .getValue()));

        param.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos()
                                        .get(GeneralParameterEnum.FECHA
                                                        .getName())));

        try {
            listaCiudadeDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL17338
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PaisDestino
     * 
     */
    public void cambiarPaisDestino() {
        cargarListaDepartamentoDestino();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoDestino
     * 
     */
    public void cambiarDepartamentoDestino() {
        cargarListaCiudadeDestino();
    }

    /**
     * Metodo ejecutado al cambiar el control PaisOrigen
     * 
     */
    public void cambiarPaisOrigen() {
        cargarListaDepartamentoOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoOrigen
     * 
     */
    public void cambiarDepartamentoOrigen() {
        cargarListaCiudadOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control OptAprobado
     * 
     */
    public void cambiarOptAprobado() {

        if (registro.getCampos().get("APROBADO").equals(true)) {
            verImprimir = true;
        }
        else {
            verImprimir = false;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicio
     * 
     */
    public void cambiarFechaInicio() {
        int anioFechaSolicitud = SysmanFunciones.ano((Date) registro.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName()));
        int anioFechaInicio = SysmanFunciones.ano((Date) registro.getCampos()
                        .get(FrmViaticosControladorEnum.FECHAINICIO
                                        .getValue()));

        if (anioFechaInicio < anioFechaSolicitud) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3972"));
            registro.getCampos()
                            .put(FrmViaticosControladorEnum.FECHAINICIO
                                            .getValue(), null);
        }

        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFin
     * 
     */
    public void cambiarFechaFin() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptSabado
     * 
     */
    public void cambiarOptSabado() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptDomingo
     * 
     * 
     */
    public void cambiarOptDomingo() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptFestivo
     * 
     */
    public void cambiarOptFestivo() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptVehiculo
     * 
     * 
     */
    public void cambiarOptVehiculo() {
        if ((boolean) registro.getCampos().get("VEHICULO")) {
            registro.getCampos().put("TRSNESPECIAL", false);
        }

    }

    /**
     * Metodo ejecutado al cambiar el control OptTrsnEspecial
     */
    public void cambiarOptTrsnEspecial() {
        if ((boolean) registro.getCampos().get("TRSNESPECIAL")) {
            registro.getCampos().put("VEHICULO", false);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMisional
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMisional(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(FrmViaticosControladorEnum.MISIONAL.getValue(),
                        registroAux.getCampos().get("CODIGO"));

        cargarDatosResponsable();
    }

    private void cargarDatosResponsable() {
        String codigoResponsable;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(FrmViaticosControladorEnum.MISIONAL
                                                        .getValue()));

        Registro regResponsables;
        try {
            regResponsables = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL4444
                                                                            .getValue())
                                            .getUrl(), param));

            if (regResponsables != null) {

                codigoResponsable = SysmanFunciones
                                .nvl(regResponsables.getCampos()
                                                .get("FUNCIONARIO"),
                                                "")
                                .toString();

                Map<String, Object> paramPersonal = new TreeMap<>();

                paramPersonal.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                                codigoResponsable);

                Registro regPersonal = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmViaticosControladorUrlEnum.URL5555
                                                                                .getValue())
                                                .getUrl(), paramPersonal));

                registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                                regPersonal.getCampos()
                                                .get("NUMERO_DCTO").toString());

                registro.getCampos().put("SUCURSAL", regPersonal.getCampos()
                                .get("SUCURSAL").toString());

                registro.getCampos().put("ID_CATEGORIA", regPersonal.getCampos()
                                .get("ID_DE_CATEGORIA").toString());

                registro.getCampos()
                                .put(FrmViaticosControladorEnum.ESCALAFON
                                                .getValue(),
                                                regPersonal.getCampos()
                                                                .get(FrmViaticosControladorEnum.ESCALAFON
                                                                                .getValue())
                                                                .toString());

                nombreResponsable = regPersonal.getCampos()
                                .get("NOMBRECOMPLETO").toString();

                Map<String, Object> paramCargo = new TreeMap<>();

                paramCargo.put(GeneralParameterEnum.CARGO.getName(),
                                regPersonal.getCampos()
                                                .get("ID_DE_CARGO")
                                                .toString());

                Registro regCargo = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmViaticosControladorUrlEnum.URL7777
                                                                                .getValue())
                                                .getUrl(), paramCargo));
                nombreCargo = regCargo.getCampos()
                                .get("NOMBRE_DEL_CARGO").toString();

                cargarListaPaisOrigen();
                cargarListaDepartamentoOrigen();
                cargarListaCiudadOrigen();
                cargarListaPaisDestino();
                cargarListaDepartamentoDestino();
                cargarListaCiudadeDestino();

            }
            else {

                registro.getCampos().put(
                                FrmViaticosControladorEnum.MISIONAL.getValue(),
                                "");
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3952"));

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoVisita
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoVisita(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TVISITA",
                        registroAux.getCampos().get("CODTVISITA"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoSolicitud
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(
                        FrmViaticosControladorEnum.TSOLICITUD.getValue(),
                        registroAux.getCampos()
                                        .get(FrmViaticosControladorEnum.CODTIPO
                                                        .getValue()));

        nombreTipoSolicitud = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), "")
                        .toString();

        validarVisibilidaTransporte();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton DetalleViaticos en la
     * vista
     *
     *
     */
    public void oprimirDetalleViaticos() {
        // <CODIGO_DESARROLLADO>

        try {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put("codigoSolicitud",
                            registro.getCampos()
                                            .get(FrmViaticosControladorEnum.CODSOLICITUD
                                                            .getValue()));
            parametros.put("nombreResponsable", nombreResponsable);
            parametros.put("tipoviatico",
                            registro.getCampos().get("TIPO_VIATICO"));

            parametros.put("numerosolicitud",
                            registro.getCampos()
                                            .get(FrmViaticosControladorEnum.NUMSOLICITUD
                                                            .getValue()));

            parametros.put("descripcion",
                            registro.getCampos().get("OBSERVACION"));
            parametros.put("ano", SysmanFunciones
                            .ano((Date) registro.getCampos().get("FECHA")));

            parametros.put("fechaInicio",
                            registro.getCampos().get("FECHAINICIO"));

            parametros.put("fechaFinal", registro.getCampos().get("FECHAFIN"));

            parametros.put("sabado", registro.getCampos().get("SABADO"));

            parametros.put("domingo", registro.getCampos().get("DOMINGO"));

            parametros.put("festivo", registro.getCampos().get("FESTIVO"));

            parametros.put("codigoTercero",
                            registro.getCampos().get("TERCERO"));

            parametros.put("escalafon", registro.getCampos().get(
                            FrmViaticosControladorEnum.ESCALAFON.getValue()));

            parametros.put("categoria",
                            registro.getCampos().get("ID_CATEGORIA"));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.SFVIATICOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            modulo);

        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Calcular en la vista
     *
     *
     */
    public void oprimirCalcular() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

        // </CODIGO_DESARROLLADO>
    }

    private boolean existeRegistro() {
        boolean rta = true;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        param.put(FrmViaticosControladorEnum.CODSOLICITUD.getValue(),
                        registro.getCampos()
                                        .get(FrmViaticosControladorEnum.CODSOLICITUD
                                                        .getValue()));

        param.put("TIPO_VIATICO", registro.getCampos().get("TIPO_VIATICO"));

        Registro reg;
        try {

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL1616
                                                                            .getValue())
                                            .getUrl(), param));

            if ("0".equals(reg.getCampos().get("CANTIDAD").toString())) {

                rta = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        try {

            registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                            "");
            nombreResponsable = "";
            nombreCargo = "";
            diasHabiles = "";
            nombreTipoSolicitud = "";

            validarIndicadorAprobado();

            if (accion.equals(ACCION_INSERTAR)) {
                registro.getCampos().put(
                                FrmViaticosControladorEnum.FECHAINICIO
                                                .getValue(),
                                new Date());
                registro.getCampos().put(
                                FrmViaticosControladorEnum.FECHAFIN.getValue(),
                                new Date());

                registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                                new Date());

                bloqueaDetalles = true;

            }
            else {

                if (!existeRegistro()) {
                    accion = ACCION_MODIFICAR;
                }

                cargarDatosResponsable();

                cargarListaPaisOrigen();
                cargarListaDepartamentoOrigen();
                cargarListaCiudadOrigen();

                cargarDiasHabiles();

                bloqueaDetalles = false;

                Map<String, Object> params = new TreeMap<>();

                params.put(FrmViaticosControladorEnum.CODTIPO.getValue(),
                                registro.getCampos()
                                                .get(FrmViaticosControladorEnum.TSOLICITUD
                                                                .getValue()));

                nombreTipoSolicitud = SysmanFunciones
                                .nvl(listaTipoSolicitud.getRegistroUnico(params)
                                                .getCampos()
                                                .get(GeneralParameterEnum.DESCRIPCION
                                                                .getName()),
                                                "")
                                .toString();

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private void validarIndicadorAprobado() {

        bloqueaAprobacion = true;
        try {
            String grupoSolicitud = ejbSysmanUtil.consultarParametro(compania,
                            "GRUPO SOLICITUD", modulo, new Date(), false);

            String usuarioAprobacion = ejbSysmanUtil.consultarParametro(
                            compania,
                            "USUARIO APROBACION", modulo, new Date(), false);

            if (usuario.equals(usuarioAprobacion)) {

                Map<String, Object> param = new TreeMap<>();
                param.put("GRUPO", grupoSolicitud);

                Registro regCuetna = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmViaticosControladorUrlEnum.URL6262
                                                                                .getValue())
                                                .getUrl(), param));

                if (regCuetna != null) {
                    bloqueaAprobacion = false;

                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validarVisibilidaTransporte() {
        if ("002".equals(registro.getCampos()
                        .get(FrmViaticosControladorEnum.TSOLICITUD.getValue())
                        .toString())) {
            bloqueaTransporte = true;
        }
        else {
            bloqueaTransporte = false;
        }
    }

    private void cargarDiasHabiles() {

        if (validarFechasVacias()) {
            return;
        }

        if (validarFechas()) {

            return;
        }

        int numMaxDias = 0;

        boolean sabado = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get("SABADO"), false);

        boolean domingo = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get("DOMINGO"), false);

        boolean festivo = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get("FESTIVO"), false);

        try {

            numMaxDias = Integer
                            .parseInt(ejbSysmanUtil.consultarParametro(compania,
                                            "NUMERO MAXIMO DE DIAS DE COMISION",
                                            modulo,
                                            new Date(), false));

            diasHabiles = SysmanFunciones.nvl(
                            ejbSysmanUtil.retornarDiasHabilesViaticos(compania,
                                            (Date) registro.getCampos()
                                                            .get(FrmViaticosControladorEnum.FECHAINICIO
                                                                            .getValue()),
                                            (Date) registro.getCampos()
                                                            .get(FrmViaticosControladorEnum.FECHAFIN
                                                                            .getValue()),
                                            sabado, domingo, festivo),
                            "0").toString();

            if (Integer.parseInt(diasHabiles) > numMaxDias) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3953")
                                .replace("#$diasHabiles#$", diasHabiles));

                diasHabiles = "0";
                registro.getCampos()
                                .put(FrmViaticosControladorEnum.FECHAFIN
                                                .getValue(), null);
                return;
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarFechasVacias() {
        if (registro.getCampos()
                        .get(FrmViaticosControladorEnum.FECHAFIN
                                        .getValue()) == null
            || registro.getCampos().get(
                            FrmViaticosControladorEnum.FECHAINICIO
                                            .getValue()) == null) {

            diasHabiles = "0";
            return true;
        }
        return false;
    }

    private boolean validarFechas() {

        if (SysmanFunciones.comparaFechas((Date) registro.getCampos()
                        .get(FrmViaticosControladorEnum.FECHAFIN
                                        .getValue()),
                        (Date) registro.getCampos()
                                        .get(FrmViaticosControladorEnum.FECHAINICIO
                                                        .getValue()))) {

            registro.getCampos()
                            .put(FrmViaticosControladorEnum.FECHAFIN
                                            .getValue(), null);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3713"));

            diasHabiles = "0";

            return true;
        }
        return false;

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName())));

        registro.getCampos().put(
                        FrmViaticosControladorEnum.NUMSOLICITUD.getValue(),
                        generarConsecutivoNumSolicitud());

        registro.getCampos().put(
                        FrmViaticosControladorEnum.CODSOLICITUD.getValue(),
                        generarConsecutivoCodSolicitud());

        registro.getCampos().put("TIPO_VIATICO", "1");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private Object generarConsecutivoNumSolicitud() {
        long numeroSolicitud = 0;

        try {
            numeroSolicitud = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "VI_VIATICOS", "COMPANIA = " + compania,
                            FrmViaticosControladorEnum.NUMSOLICITUD.getValue());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return numeroSolicitud;
    }

    private Object generarConsecutivoCodSolicitud() {
        long codigoSolicitud = 0;

        String manejaConsecutivo;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {

            Registro regCodSolicitud = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmViaticosControladorUrlEnum.URL5858
                                                                            .getValue())
                                            .getUrl(), param));

            if (regCodSolicitud != null) {

                manejaConsecutivo = ejbSysmanUtil.consultarParametro(compania,
                                "MANEJA CONSECUTIVO INICIAL", modulo,
                                new Date(),
                                false);
                if ("SI".equals(manejaConsecutivo)) {
                    codigoSolicitud = Long
                                    .parseLong(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "CONSECUTIVO INICIAL DE SOLICITUD",
                                                    modulo,
                                                    new Date(),
                                                    false));
                }
                else {
                    codigoSolicitud = ejbSysmanUtil.generarSiguienteConsecutivo(
                                    "VI_VIATICOS", "COMPANIA = " + compania,
                                    FrmViaticosControladorEnum.CODSOLICITUD
                                                    .getValue());
                }
            }
            else {

                codigoSolicitud = Long.parseLong(SysmanFunciones.ano(new Date())
                    + SysmanFunciones.padl("1", 6, "0"));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return codigoSolicitud;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues() {

        accion = ACCION_VER;
        cargarRegistro();
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }

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
     * 
     */
    @Override
    public boolean eliminarDespues() {

        return true;
    }

    // <SET_GET_ATRIBUTOS>

    public boolean isBloqueaTransporte() {
        return bloqueaTransporte;
    }

    public void setBloqueaTransporte(boolean bloqueaTransporte) {
        this.bloqueaTransporte = bloqueaTransporte;
    }

    public boolean isVerImprimir() {
        return verImprimir;
    }

    public void setVerImprimir(boolean verImprimir) {
        this.verImprimir = verImprimir;
    }

    public boolean isBloqueaDetalles() {
        return bloqueaDetalles;
    }

    public void setBloqueaDetalles(boolean bloqueaDetalles) {
        this.bloqueaDetalles = bloqueaDetalles;
    }

    /**
     * Retorna la variable nombreCargo
     * 
     * @return nombreCargo
     */
    public String getNombreCargo() {
        return nombreCargo;
    }

    /**
     * Asigna la variable nombreCargo
     * 
     * @param nombreCargo
     * Variable a asignar en nombreCargo
     */
    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    /**
     * Retorna la variable nombreResponsable
     * 
     * @return nombreResponsable
     */
    public String getNombreResponsable() {
        return nombreResponsable;
    }

    /**
     * Asigna la variable nombreResponsable
     * 
     * @param nombreResponsable
     * Variable a asignar en nombreResponsable
     */
    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    /**
     * Retorna la variable diasHabiles
     * 
     * @return diasHabiles
     */
    public String getDiasHabiles() {
        return diasHabiles;
    }

    /**
     * Retorna la variable nombreTipoSolicitud
     * 
     * @return nombreTipoSolicitud
     */
    public String getNombreTipoSolicitud() {
        return nombreTipoSolicitud;
    }

    /**
     * Asigna la variable nombreTipoSolicitud
     * 
     * @param nombreTipoSolicitud
     * Variable a asignar en nombreTipoSolicitud
     */
    public void setNombreTipoSolicitud(String nombreTipoSolicitud) {
        this.nombreTipoSolicitud = nombreTipoSolicitud;
    }

    /**
     * Asigna la variable diasHabiles
     * 
     * @param diasHabiles
     * Variable a asignar en diasHabiles
     */
    public void setDiasHabiles(String diasHabiles) {
        this.diasHabiles = diasHabiles;
    }

    public boolean isBloqueaAprobacion() {
        return bloqueaAprobacion;
    }

    public void setBloqueaAprobacion(boolean bloqueaAprobacion) {
        this.bloqueaAprobacion = bloqueaAprobacion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiudadeDestino
     * 
     * @return listaCiudadeDestino
     */
    public List<Registro> getListaCiudadeDestino() {
        return listaCiudadeDestino;
    }

    /**
     * Asigna la lista listaCiudadeDestino
     * 
     * @param listaCiudadeDestino
     * Variable a asignar en listaCiudadeDestino
     */
    public void setListaCiudadeDestino(List<Registro> listaCiudadeDestino) {
        this.listaCiudadeDestino = listaCiudadeDestino;
    }

    /**
     * Retorna la lista listaPaisDestino
     * 
     * @return listaPaisDestino
     */
    public List<Registro> getListaPaisDestino() {
        return listaPaisDestino;
    }

    /**
     * Asigna la lista listaPaisDestino
     * 
     * @param listaPaisDestino
     * Variable a asignar en listaPaisDestino
     */
    public void setListaPaisDestino(List<Registro> listaPaisDestino) {
        this.listaPaisDestino = listaPaisDestino;
    }

    /**
     * Retorna la lista listaDepartamentoDestino
     * 
     * @return listaDepartamentoDestino
     */
    public List<Registro> getListaDepartamentoDestino() {
        return listaDepartamentoDestino;
    }

    /**
     * Asigna la lista listaDepartamentoDestino
     * 
     * @param listaDepartamentoDestino
     * Variable a asignar en listaDepartamentoDestino
     */
    public void setListaDepartamentoDestino(
        List<Registro> listaDepartamentoDestino) {
        this.listaDepartamentoDestino = listaDepartamentoDestino;
    }

    /**
     * Retorna la lista listaCiudadOrigen
     * 
     * @return listaCiudadOrigen
     */
    public List<Registro> getListaCiudadOrigen() {
        return listaCiudadOrigen;
    }

    /**
     * Asigna la lista listaCiudadOrigen
     * 
     * @param listaCiudadOrigen
     * Variable a asignar en listaCiudadOrigen
     */
    public void setListaCiudadOrigen(List<Registro> listaCiudadOrigen) {
        this.listaCiudadOrigen = listaCiudadOrigen;
    }

    /**
     * Retorna la lista listaPaisOrigen
     * 
     * @return listaPaisOrigen
     */
    public List<Registro> getListaPaisOrigen() {
        return listaPaisOrigen;
    }

    /**
     * Asigna la lista listaPaisOrigen
     * 
     * @param listaPaisOrigen
     * Variable a asignar en listaPaisOrigen
     */
    public void setListaPaisOrigen(List<Registro> listaPaisOrigen) {
        this.listaPaisOrigen = listaPaisOrigen;
    }

    /**
     * Retorna la lista listaDepartamentoOrigen
     * 
     * @return listaDepartamentoOrigen
     */
    public List<Registro> getListaDepartamentoOrigen() {
        return listaDepartamentoOrigen;
    }

    /**
     * Asigna la lista listaDepartamentoOrigen
     * 
     * @param listaDepartamentoOrigen
     * Variable a asignar en listaDepartamentoOrigen
     */
    public void setListaDepartamentoOrigen(
        List<Registro> listaDepartamentoOrigen) {
        this.listaDepartamentoOrigen = listaDepartamentoOrigen;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaMisional
     * 
     * @return listaMisional
     */
    public RegistroDataModelImpl getListaMisional() {
        return listaMisional;
    }

    /**
     * Asigna la lista listaMisional
     * 
     * @param listaMisional
     * Variable a asignar en listaMisional
     */
    public void setListaMisional(RegistroDataModelImpl listaMisional) {
        this.listaMisional = listaMisional;
    }

    /**
     * Retorna la lista listaTipoVisita
     * 
     * @return listaTipoVisita
     */
    public RegistroDataModelImpl getListaTipoVisita() {
        return listaTipoVisita;
    }

    /**
     * Asigna la lista listaTipoVisita
     * 
     * @param listaTipoVisita
     * Variable a asignar en listaTipoVisita
     */
    public void setListaTipoVisita(RegistroDataModelImpl listaTipoVisita) {
        this.listaTipoVisita = listaTipoVisita;
    }

    /**
     * Retorna la lista listaTipoSolicitud
     * 
     * @return listaTipoSolicitud
     */
    public RegistroDataModelImpl getListaTipoSolicitud() {
        return listaTipoSolicitud;
    }

    /**
     * Asigna la lista listaTipoSolicitud
     * 
     * @param listaTipoSolicitud
     * Variable a asignar en listaTipoSolicitud
     */
    public void setListaTipoSolicitud(
        RegistroDataModelImpl listaTipoSolicitud) {
        this.listaTipoSolicitud = listaTipoSolicitud;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public boolean isBloquearPeriodo() {
        return bloquearPeriodo;
    }

    public void setBloquearPeriodo(boolean bloquearPeriodo) {
        this.bloquearPeriodo = bloquearPeriodo;
    }

    // </SET_GET_ADICIONALES>
}
