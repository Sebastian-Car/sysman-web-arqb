/*-
 * NatSubEvaluacionControlador.java
 *
 * 1.0
 * 
 * 7/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import java.text.ParseException;
import java.util.Date;
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatSubEvaluacionControladorEnum;
import com.sysman.hojasdevida.enums.NatSubEvaluacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;

import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

import com.sysman.util.SysmanFunciones;

/**
 * Formulario que registra las evaluaciones en periodos de prueba
 *
 * @version 1.0, 07/02/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class NatSubEvaluacionControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el numero de modulo al
     * cual el usuario ingreso
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que indica si la calificacion es satisfactoria
     */
    private boolean satisafactoria;
    /**
     * Atributo que indica si la calificacion es insatisafactoria
     */
    private boolean insatisafactoria;
    /**
     * Atributo que almacena el total de dias a calificar
     */
    private String numeroDeDias;
    /**
     * Atributo que almacena el total de calificacion
     */
    private Double calificacion;

    /**
     * Numero de documento del empleado que vienen por parametro
     */
    private String numeroDcto;

    /**
     * Numero de sucursal del empleado que vienen por parametro
     */
    private String sucursal;

    /**
     * Atributo que almacena el nombre de la clase de evaliacion
     */
    private String nombreClase;

    /**
     * Atributo que almacena la descripcion del motivo
     */
    private String descripcionMotivo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga lso registros de los evaluadores
     */
    private RegistroDataModelImpl listaDocumentoEvaluador;
    /**
     * Lista que carga el tipo de evaluacion a realizar
     */
    private RegistroDataModelImpl listaClaseEvaluacion;
    /**
     * Lista que carga los motivos de concertacion
     */
    private RegistroDataModelImpl listaMotivoConcertacion;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Map<String, Object> parametrosEntrada;
    // </DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de NatSubEvaluacionControlador
     */
    public NatSubEvaluacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUB_EVALUACION_CONTROLADOR
                            .getCodigo();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {

                numeroDcto = parametrosEntrada.get("numeroDcto")
                                .toString();

                sucursal = parametrosEntrada.get("sucursal")
                                .toString();

            }

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
        cargarListaDocumentoEvaluador();
        cargarListaClaseEvaluacion();
        cargarListaMotivoConcertacion();
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
        tabla = "NAT_EVALUACION";
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDcto);

        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL5555
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL6666
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL7777
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL8888
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL9999
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDocumentoEvaluador
     *
     */
    public void cargarListaDocumentoEvaluador() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL7854
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDocumentoEvaluador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_DCTO");

    }

    /**
     * 
     * Carga la lista listaClaseEvaluacion
     *
     */
    public void cargarListaClaseEvaluacion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL8913
                                                        .getValue());

        listaClaseEvaluacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "CE_CODIGO");
    }

    /**
     * 
     * Carga la lista listaMotivoConcertacion
     *
     */
    public void cargarListaMotivoConcertacion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubEvaluacionControladorUrlEnum.URL9498
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMotivoConcertacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaDesdeVal
     * 
     */
    public void cambiarFechaDesdeVal() {
        cargarNumeroDiasEvaluacion();
    }

    /**
     * Metodo ejecutado al cambiar el control FechaHastaVal
     * 
     */
    public void cambiarFechaHastaVal() {
        cargarNumeroDiasEvaluacion();
    }

    /**
     * Metodo ejecutado al cambiar el control DiasNoEvaluados
     * 
     */
    public void cambiarDiasNoEvaluados() {
        cargarNumeroDiasEvaluacion();
    }

    /**
     * Metodo ejecutado al cambiar el control EvaluacionLogroObjetivos
     * 
     */
    public void cambiarEvaluacionLogroObjetivos() {

        double evaluacion = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(NatSubEvaluacionControladorEnum.EV_TOT_EV_LOG_OBJ
                                                        .getValue()),
                                        "0")
                        .toString());

        try {
            evaluacion = evaluacion * ((Double.parseDouble(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PORCENTAJE DE EVALUACION LOGRO DE OBJETIVOS",
                                            modulo, new Date(), false), "0")
                            .toString()))
                / 100);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos()
                        .put(NatSubEvaluacionControladorEnum.EV_TOT_EV_LOG_OBJ
                                        .getValue(), evaluacion);

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDocumentoEvaluador
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDocumentoEvaluador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EV_NUMEDOCUEVAL",
                        registroAux.getCampos().get("NUMERO_DCTO"));

        registro.getCampos().put("EV_NOMBEVAL",
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put("CARGOEVALUADOR",
                        registroAux.getCampos().get("NOMBRE_DE_CARGO"));

        registro.getCampos().put("EV_TIPODOCUEVAL",
                        registroAux.getCampos().get("DCTO_IDENTIDAD"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseEvaluacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseEvaluacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EV_CLASEVAL",
                        registroAux.getCampos().get("CE_CODIGO"));

        nombreClase = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CE_NOMBRE"), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMotivoConcertacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMotivoConcertacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EV_MOTICONC",
                        registroAux.getCampos().get("CODIGO"));

        descripcionMotivo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.DESCRIPCION.getName()), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Productividad en la vista
     *
     */
    public void oprimirProductividad() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton AdministracionDePersonal
     * en la vista
     *
     */
    public void oprimirAdministracionDePersonal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ConductaLaboral en la
     * vista
     *
     */
    public void oprimirConductaLaboral() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton actualizarCalificacion en
     * la vista
     *
     */
    public void oprimiractualizarCalificacion() {

        cargarRegistro(css, ACCION_MODIFICAR);

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
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        satisafactoria = false;

        insatisafactoria = false;

        nombreClase = "";

        descripcionMotivo = "";
        calificacion = 0.0;

        if (accion.equals(ACCION_INSERTAR)) {
            numeroDeDias = "0";
            registro.getCampos()
                            .put(NatSubEvaluacionControladorEnum.EV_FECHDESDEEVAL
                                            .getValue(), new Date());
            registro.getCampos()
                            .put(NatSubEvaluacionControladorEnum.EV_FECHHASTAEVAL
                                            .getValue(), new Date());
        }
        else {

            cargarNombreClase();
            cargarDescripcionMotivo();
            cargarCalificacion();
            cargarNumeroDiasEvaluacion();
        }

        // </CODIGO_DESARROLLADO>
    }

    private void cargarNombreClase() {
        Map<String, Object> param = new TreeMap<>();

        param.put("CE_CODIGO",
                        registro.getCampos().get("EV_CLASEVAL"));

        try {
            nombreClase = SysmanFunciones
                            .nvl(listaClaseEvaluacion
                                            .getRegistroUnico(param)
                                            .getCampos().get("CE_NOMBRE"),
                                            "")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarDescripcionMotivo() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get("EV_MOTICONC"));

        try {
            descripcionMotivo = SysmanFunciones
                            .nvl(listaMotivoConcertacion
                                            .getRegistroUnico(param)
                                            .getCampos()
                                            .get(GeneralParameterEnum.DESCRIPCION
                                                            .getName()),
                                            "")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarCalificacion() {
        calificacion = 0.0;

        Double logroObjetivo = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(NatSubEvaluacionControladorEnum.EV_TOT_EV_LOG_OBJ
                                                        .getValue()),
                                        "0")
                        .toString());

        Double factorDesempeno = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get("EV_TOT_EV_FAC_DES"), "0")
                        .toString());

        calificacion = logroObjetivo + factorDesempeno;

        if (calificacion > 600) {
            satisafactoria = true;
            insatisafactoria = false;
        }
        else {
            insatisafactoria = true;
            satisafactoria = false;
        }

    }

    private void cargarNumeroDiasEvaluacion() {
        int diferenciaDias;
        int diasNoEvaluados;
        try {

            if (validarFechasVacias()) {
                return;
            }

            diferenciaDias = SysmanFunciones.calcularDiferenciaDias(
                            (Date) registro.getCampos()
                                            .get(NatSubEvaluacionControladorEnum.EV_FECHDESDEEVAL
                                                            .getValue()),
                            (Date) registro.getCampos()
                                            .get(NatSubEvaluacionControladorEnum.EV_FECHHASTAEVAL
                                                            .getValue()));

            diasNoEvaluados = Integer.parseInt(SysmanFunciones
                            .nvl(registro.getCampos().get("DIASNOEVALUADOS"),
                                            "0")
                            .toString());

            numeroDeDias = Integer.toString(diferenciaDias - diasNoEvaluados);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarFechasVacias() {
        if (registro.getCampos()
                        .get(NatSubEvaluacionControladorEnum.EV_FECHDESDEEVAL
                                        .getValue()) == null
            ||
            registro.getCampos()
                            .get(NatSubEvaluacionControladorEnum.EV_FECHHASTAEVAL
                                            .getValue()) == null) {

            numeroDeDias = "0";
            return true;
        }
        return false;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put("DP_NUMEDOCU", numeroDcto);

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
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
        cargarRegistro();
        JsfUtil.ejecutarJavaScript("$('#FR1706_nuevo\\\\:BT2991').click()");
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
    public void ejecutarrcCerrar() {

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_INSCRIPCIONES_CARRERAS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    // <SET_GET_ATRIBUTOS>

    public boolean isSatisafactoria() {
        return satisafactoria;
    }

    public void setSatisafactoria(boolean satisafactoria) {
        this.satisafactoria = satisafactoria;
    }

    public boolean isInsatisafactoria() {
        return insatisafactoria;
    }

    public void setInsatisafactoria(boolean insatisafactoria) {
        this.insatisafactoria = insatisafactoria;
    }

    /**
     * Retorna la variable numeroDeDias
     * 
     * @return numeroDeDias
     */
    public String getNumeroDeDias() {
        return numeroDeDias;
    }

    /**
     * Asigna la variable numeroDeDias
     * 
     * @param numeroDeDias
     * Variable a asignar en numeroDeDias
     */
    public void setNumeroDeDias(String numeroDeDias) {
        this.numeroDeDias = numeroDeDias;
    }

    /**
     * Retorna la variable calificacion
     * 
     * @return calificacion
     */
    public Double getCalificacion() {
        return calificacion;
    }

    /**
     * Asigna la variable calificacion
     * 
     * @param calificacion
     * Variable a asignar en calificacion
     */
    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDocumentoEvaluador
     * 
     * @return listaDocumentoEvaluador
     */
    public RegistroDataModelImpl getListaDocumentoEvaluador() {
        return listaDocumentoEvaluador;
    }

    /**
     * Asigna la lista listaDocumentoEvaluador
     * 
     * @param listaDocumentoEvaluador
     * Variable a asignar en listaDocumentoEvaluador
     */
    public void setListaDocumentoEvaluador(
        RegistroDataModelImpl listaDocumentoEvaluador) {
        this.listaDocumentoEvaluador = listaDocumentoEvaluador;
    }

    /**
     * Retorna la lista listaClaseEvaluacion
     * 
     * @return listaClaseEvaluacion
     */
    public RegistroDataModelImpl getListaClaseEvaluacion() {
        return listaClaseEvaluacion;
    }

    /**
     * Asigna la lista listaClaseEvaluacion
     * 
     * @param listaClaseEvaluacion
     * Variable a asignar en listaClaseEvaluacion
     */
    public void setListaClaseEvaluacion(
        RegistroDataModelImpl listaClaseEvaluacion) {
        this.listaClaseEvaluacion = listaClaseEvaluacion;
    }

    /**
     * Retorna la lista listaMotivoConcertacion
     * 
     * @return listaMotivoConcertacion
     */
    public RegistroDataModelImpl getListaMotivoConcertacion() {
        return listaMotivoConcertacion;
    }

    /**
     * Asigna la lista listaMotivoConcertacion
     * 
     * @param listaMotivoConcertacion
     * Variable a asignar en listaMotivoConcertacion
     */
    public void setListaMotivoConcertacion(
        RegistroDataModelImpl listaMotivoConcertacion) {
        this.listaMotivoConcertacion = listaMotivoConcertacion;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    public String getDescripcionMotivo() {
        return descripcionMotivo;
    }

    public void setDescripcionMotivo(String descripcionMotivo) {
        this.descripcionMotivo = descripcionMotivo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
