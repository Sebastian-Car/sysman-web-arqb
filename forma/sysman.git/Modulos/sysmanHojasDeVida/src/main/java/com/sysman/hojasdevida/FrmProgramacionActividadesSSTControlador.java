/*-
 * FrmProgramacionActividadesSSTControlador.java
 *
 * 1.0
 *
 * 02/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.CotizacionesActividadesControladorEnum;
import com.sysman.hojasdevida.enums.FrmProgramacionActividadesSSTControladorEnum;
import com.sysman.hojasdevida.enums.FrmProgramacionActividadesSSTControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>frmprogramacionactividadessst</code>
 * . Fue migrado del formulario: ACTIVIDADESINDUCCION del modulo
 * SysmanHv2018.01.07_HV_SST_Manual_SelPersonal_Bienestar.
 *
 * @version 1.0, 02/02/2018
 * @author pespitia
 * 
 * 
 * @versión 2.0 16/05/2018
 * @author lbotia
 * 
 * 
 * Se agrego verResponsable y verDirigido con sus get and set
 * respectivamente validando en la forma que atributos pertenecen a
 * cada uno de ellos para hacerlos visibles o no visibles en el
 * formulario y validando en el metodo asignarTipoEvento a que opción
 * de menu spertenecen para realizar la acción pertienente.
 * 
 */
@ManagedBean
@ViewScoped
public class FrmProgramacionActividadesSSTControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente.
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja el codigo del menu desde
     * el cual se abre el formulario.
     */
    private final String menuActual = SessionUtil.getMenuActual();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>COMPANIA</code>.
     */
    private final String cCoordEvento = FrmProgramacionActividadesSSTControladorEnum.COORDEVENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>NOMBRE</code>
     */

    private final String cNombre = FrmProgramacionActividadesSSTControladorEnum.NOMBRE
                    .getValue();
    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>FECHAINICIAL</code>.
     */
    private final String cFechaInicial = GeneralParameterEnum.FECHAINICIAL
                    .getName();

    private final String cDependencia = GeneralParameterEnum.DEPENDENCIA
                    .getName();

    private final String cEscalafon = FrmProgramacionActividadesSSTControladorEnum.ESCALAFON
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>IDEVENTO</code>.
     */
    private final String cIdEvento = FrmProgramacionActividadesSSTControladorEnum.IDEVENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena <code>NIT</code>
     * .
     */
    private final String cNit = FrmProgramacionActividadesSSTControladorEnum.NIT
                    .getValue();

    private final String cNombreDep = FrmProgramacionActividadesSSTControladorEnum.NOMBREDEP
                    .getValue();

    private final String cNombreEs = FrmProgramacionActividadesSSTControladorEnum.NOMBREES
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>NITESTABLECIMIENTO</code>.
     */
    private final String cNitEstablecimiento = FrmProgramacionActividadesSSTControladorEnum.NITESTABLECIMIENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>NUMERO_DCTO</code>.
     */
    private final String cNumeroDcto = GeneralParameterEnum.NUMERO_DCTO
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>NUMERODOCUMENTO</code>
     */

    private final String cNumeroDocumento = FrmProgramacionActividadesSSTControladorEnum.NUMERODOCUMENTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>IDEMPLEADO</code>
     */

    private final String cIdEmpleado = FrmProgramacionActividadesSSTControladorEnum.IDEMPLEADO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>SUCURSAL</code>.
     */
    private final String cSucursal = GeneralParameterEnum.SUCURSAL.getName();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>SUCURSALCOOR</code>.
     */
    private final String cSucursalCoor = FrmProgramacionActividadesSSTControladorEnum.SUCURSALCOOR
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena
     * <code>TIPOEVENTO</code>.
     */
    private final String cTipoEvento = FrmProgramacionActividadesSSTControladorEnum.TIPOEVENTO
                    .getValue();

    private final String cTipoDocumento = FrmProgramacionActividadesSSTControladorEnum.TIPODOCUMENTO
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /** Atributo que contiene el codigo de la actividad programada. */
    private String idEvento;

    /**
     * Atributo que contiene el de evento de la actividad programada.
     */
    private int tipoEvento;

    /**
     * Atributo que contiene la fecha inicial de la actividad
     * programada.
     */
    private Date fechaInicial;

    private String tituloPrincipal;

    private String titulo;

    /**
     * Atributo que contiene la variable verResponsable con los campos
     * que debe mostrar de la actividad programada.
     */

    private boolean verResponsable;

    /**
     * Atributo que contiene la variable verDirigido con los campos
     * que se deben mostrar de la actividad programada.
     */

    private boolean verDirigido;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los items del combo estado (CB5633).
     */
    private List<Registro> listaCbEstado;

    /**
     * Lista que contiene los detalles de combo tipo de documento
     * (CB5632).
     */
    private List<Registro> listaTipoDocumento;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo numero de dcoumento
     * (CB5631).
     */
    private RegistroDataModelImpl listaCbNumDocumento;

    /**
     * Lista que contioene los detalles del combo establecimiento
     * organizador (CB5634).
     */
    private RegistroDataModelImpl listaNitEstablecimiento;

    /**
     * Lista que contiene los detalles del combo coordiador evento
     * (CB5696).
     */
    private RegistroDataModelImpl listaCoordinadorEvento;

    /**
     * Esta variable define si se ocultan o no los campos para el menu
     * de seguridad y salud en el trabajo
     */
    private boolean ocultarSST;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_EJBs>

    /**
     * Crea una nueva instancia de
     * FrmProgramacionActividadesSSTControlador
     */
    public FrmProgramacionActividadesSSTControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1683
            numFormulario = GeneralCodigoFormaEnum.FRM_PROGRAMACION_ACTIVIDADES_SST_CONTROLADOR
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
        cargarListaCbNumDocumento();
        cargarListaTipoDocumento();
        cargarListaCoordinadorEvento();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCbEstado();
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
        enumBase = GenericUrlEnum.NAT_ACTIVIDADESPROGRAMADAS;

        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se asigna la referencia de las consultas de las operaciones
     * CRUD del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        asignarTipoEvento();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipoEvento, tipoEvento);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaCbEstado</code> asociada al combo
     * estado (CB5633).
     */
    public void cargarListaCbEstado() {
        try {
            listaCbEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProgramacionActividadesSSTControladorUrlEnum.URL5494
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCbNumDocumento</code> asociada al
     * combo numero de documento (CB5631).
     */
    public void cargarListaCbNumDocumento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmProgramacionActividadesSSTControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCoordinadorEvento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cNumeroDocumento);

    }

    /**
     * Carga la lista: <code>listaTipoDocumento</code> asociada al
     * combo tipo de documento (CB5632).
     */
    public void cargarListaTipoDocumento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaTipoDocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProgramacionActividadesSSTControladorUrlEnum.URL6603
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaNitEstablecimiento</code> asociada
     * al combo establecimiento organizador (CB5634).
     */
    public void cargarListaNitEstablecimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmProgramacionActividadesSSTControladorUrlEnum.URL7194
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cIdEvento, idEvento);
        param.put(cTipoEvento, tipoEvento);

        listaNitEstablecimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNitEstablecimiento);
    }

    /**
     * Carga la lista: <code>listaCoordinadorEvento</code> asociada al
     * combo coordinador evento (CB5696).
     */
    public void cargarListaCoordinadorEvento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmProgramacionActividadesSSTControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCoordinadorEvento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cNit);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del campo fecha inicial
     * (CP52048).
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = (Date) registro.getCampos().get(cFechaInicial);

        if (fechaInicial != null) {
            asignarConsecutivoIdEvento();
        }
        else {
            idEvento = "";
            registro.getCampos().put(cIdEvento, "");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de cerrar el formulario que se abre al
     * presionar el boton establecimiento organizador (BT2945).
     */
    public void retornarFormularioEstablecimientoOrganizador(
        SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        cargarListaNitEstablecimiento();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCbNumDocumento</code> asociada al combo numero de
     * documento (CB5631).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCbNumDocumento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cNumeroDocumento,
                        registroAux.getCampos().get(cNumeroDcto));

    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaNitEstablecimiento</code> asociada al combo
     * establecimiento organizador (CB5634).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitEstablecimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cNitEstablecimiento,
                        registroAux.getCampos().get(cNitEstablecimiento));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCoordinadorEvento</code> asociada al combo
     * coordinador evento (CB5696).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaCoordinadorEvento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cCoordEvento,
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put(cSucursalCoor,
                        registroAux.getCampos().get(cSucursal));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));

        registro.getCampos().put(cNumeroDocumento,
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put(cNombre,
                        registroAux.getCampos().get(cNombre));

        registro.getCampos().put("TIPODOCUMENTO",
                        registroAux.getCampos().get("TIPOID"));

        registro.getCampos().put(cDependencia,
                        registroAux.getCampos().get(cDependencia));

        registro.getCampos().put(cEscalafon,
                        registroAux.getCampos().get(cEscalafon));

        registro.getCampos().put(cNombreDep,
                        registroAux.getCampos().get(cNombreDep));

        registro.getCampos().put(cNombreEs,
                        registroAux.getCampos().get(cNombreEs));

        validarTercero();

    }

    public void validarTercero() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registro.getCampos().get("COORDEVENTO"));
        param.put(cSucursal,
                        registro.getCampos().get(cSucursal));

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProgramacionActividadesSSTControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {

                registro.getCampos().put(cDependencia,
                                rs.getCampos().get(cDependencia));

                registro.getCampos().put(cEscalafon,
                                rs.getCampos().get(cEscalafon));

                registro.getCampos().put(cNombreDep,
                                rs.getCampos().get(cNombreDep));

                registro.getCampos().put(cNombreEs,
                                rs.getCampos().get(cNombreEs));

            }

        }
        catch (SystemException e) {
            // Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton EstablecimientoOrganizador
     * (BT2945) en la vista.
     */
    public void oprimirEstablecimientoOrganizador() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cNitEstablecimiento, "");

        agregarRegistroNuevo(css == null);

        String[] campos = new String[3];
        campos[0] = CotizacionesActividadesControladorEnum.PR_IDEVENTO
                        .getValue();
        campos[1] = CotizacionesActividadesControladorEnum.PR_TIPOEVENTO
                        .getValue();
        campos[2] = CotizacionesActividadesControladorEnum.PR_FECHAINICIAL
                        .getValue();

        Object[] valores = new Object[3];
        valores[0] = idEvento;
        valores[1] = tipoEvento;
        valores[2] = fechaInicial;

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.COTIZACIONES_ACTIVIDADES_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
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
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        asignarNomTitulo();

        /**
         * Esta validacion permite ocultar o no diferentes campos en
         * el controlador
         */
        if (FrmProgramacionActividadesSSTControladorEnum.MENU210802050102
                        .getValue()
                        .equals(menuActual)
            || FrmProgramacionActividadesSSTControladorEnum.MENU210802050203
                            .getValue().equals(menuActual)) {
            ocultarSST = true;
        }
        else {
            ocultarSST = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro.
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (css == null) {
            fechaInicial = SysmanFunciones.truncarFecha(new Date());

            registro.getCampos().put(cFechaInicial, fechaInicial);
            registro.getCampos().put("FECHAFINAL", fechaInicial);

            asignarConsecutivoIdEvento();
        }
        else {
            idEvento = SysmanFunciones
                            .nvl(registro.getCampos().get(cIdEvento), "")
                            .toString();

            fechaInicial = (Date) registro.getCampos().get(cFechaInicial);
        }

        cargarListaNitEstablecimiento();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     *
     * @return true -> Permite realizar la insercion del registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cTipoEvento, tipoEvento);
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove("NOMBREES");
        registro.getCampos().remove("NOMBREDEP");

        if ("210802050102".equals(menuActual)
            || "210802050203".equals(menuActual)) {
            registro.getCampos().put("TIPODOCUMENTO", "C");

        }
        else {
            String coordinador = SysmanFunciones.toString(
                            registro.getCampos().get("COORDEVENTO"));
            if (coordinador == null || coordinador.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(
                                "Por favor seleccione un coordinador de evento");
                return false;
            }
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
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
     * del registro.
     *
     * @return true -> Permite realizar la insercion o actualizacion
     * del registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cNombre);
            registro.getCampos().remove("NOMBREES");
            registro.getCampos().remove("NOMBREDEP");
            registro.getCampos().remove(cTipoEvento);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
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
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     *
     * @return true -> Permite eliminar el registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Determina el tipo de evento a tener en cuenta en el origen de
     * datos de las actividades programadas.
     */
    private void asignarTipoEvento() {
        switch (menuActual) {
        case "210402010103":
            tipoEvento = 1;
            titulo = idioma.getString("TB_TB4116");

            break;
        case "210402010202":
            tipoEvento = 2;
            titulo = idioma.getString("TB_TB4117");

            break;
        case "210402010302":
            tipoEvento = 3;
            titulo = idioma.getString("TB_TB4118");

            break;
        case "210402010402":
            tipoEvento = 4;
            titulo = idioma.getString("TB_TB4119");
            break;
        case "210402020302":
            tipoEvento = 5;
            titulo = idioma.getString("TB_TB4122");

            break;
        case "210402020203":
            tipoEvento = 6;
            verDirigido = true;
            verResponsable = true;
            titulo = idioma.getString("TB_TB4121");
            break;
        case "210802050102":
            tipoEvento = 102;
            ocultarSST = true;
            titulo = idioma.getString("TB_TB4205");
            break;
        case "210802050203":
            tipoEvento = 101;
            titulo = idioma.getString("TB_TB4204");
            // titulo = idioma.getString("TB_TB4122");
            break;
        case "210802050301":
            tipoEvento = 103;
            verDirigido = true;
            verResponsable = true;
            titulo = idioma.getString("TB_TB4206");
            break;
        default: // 210402020102
            tipoEvento = 7;
            verDirigido = true;
            verResponsable = true;
            titulo = idioma.getString("TB_TB4120");
            break;
        }
    }

    /**
     * Determina el nombre asociado al tipo de evento y lo asigna al
     * titulo del formulario principal (LB42893).
     */
    private void asignarNomTitulo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put("CODIGO", tipoEvento);

        Registro registroAux = null;

        try {
            registroAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmProgramacionActividadesSSTControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        tituloPrincipal = registroAux != null
            ? SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "")
                            .toString()
            : "";
    }

    /**
     * Genera y asigna el valor que se debe mostrar en el campo codigo
     * asignado al evento (CP52045). El valor consiste en el
     * consecutivo del campo <code>IDEVENTO</code>.
     */
    private void asignarConsecutivoIdEvento() {
        try {
            String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "''", " AND TIPOEVENTO = ",
                            Integer.toString(tipoEvento));

            idEvento = Long.toString(
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            enumBase.getTable(), criterio,
                                            cIdEvento, "1"));

            registro.getCampos().put(cIdEvento, idEvento);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <SET_GET_ATRIBUTOS>

    public String getTituloPrincipal() {
        return tituloPrincipal;
    }

    public void setTituloPrincipal(String tituloPrincipal) {
        this.tituloPrincipal = tituloPrincipal;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    public List<Registro> getListaCbEstado() {
        return listaCbEstado;
    }

    public void setListaCbEstado(List<Registro> listaCbEstado) {
        this.listaCbEstado = listaCbEstado;
    }

    public List<Registro> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<Registro> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCbNumDocumento() {
        return listaCbNumDocumento;
    }

    public void setListaCbNumDocumento(
        RegistroDataModelImpl listaCbNumDocumento) {
        this.listaCbNumDocumento = listaCbNumDocumento;
    }

    /**
     * Retorna la lista listaNitEstablecimiento
     *
     * @return listaNitEstablecimiento
     */
    public RegistroDataModelImpl getListaNitEstablecimiento() {
        return listaNitEstablecimiento;
    }

    /**
     * Asigna la lista listaNitEstablecimiento
     *
     * @param listaNitEstablecimiento
     * Variable a asignar en listaNitEstablecimiento
     */
    public void setListaNitEstablecimiento(
        RegistroDataModelImpl listaNitEstablecimiento) {
        this.listaNitEstablecimiento = listaNitEstablecimiento;
    }

    public RegistroDataModelImpl getListaCoordinadorEvento() {
        return listaCoordinadorEvento;
    }

    public void setListaCoordinadorEvento(
        RegistroDataModelImpl listaCoordinadorEvento) {
        this.listaCoordinadorEvento = listaCoordinadorEvento;
    }

    /**
     * @return the verResponsable
     */
    public boolean isVerResponsable() {
        return verResponsable;
    }

    /**
     * @param verResponsable
     * the verResponsable to set
     */
    public void setVerResponsable(boolean verResponsable) {
        this.verResponsable = verResponsable;
    }

    /**
     * @return the verDirigido
     */
    public boolean isVerDirigido() {
        return verDirigido;
    }

    /**
     * @param verDirigido
     * the verDirigido to set
     */
    public void setVerDirigido(boolean verDirigido) {
        this.verDirigido = verDirigido;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isOcultarSST() {
        return ocultarSST;
    }

    public void setOcultarSST(boolean ocultarSST) {
        this.ocultarSST = ocultarSST;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
