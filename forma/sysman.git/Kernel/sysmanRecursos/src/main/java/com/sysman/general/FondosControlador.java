package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FondosControladorEnum;
import com.sysman.general.enums.FondosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 * 
 * @author asana
 * @version 2, 05/10/2017 Se realiza refactoring de controlador.
 */
@ManagedBean
@ViewScoped
public class FondosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;

    /**
     * Variable que almacena el nombre del tipo de dcto Siif
     */
    private String nombreSiif;

    private Registro registroSub;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private List<Registro> listaBancoPago;
    private List<Registro> listaCLASE;
    private List<Registro> listaSubclasefondo;
    private RegistroDataModelImpl listaNit;
    /**
     * Atributo que valida si el campo de rubro patrono es visible o
     * no
     */
    private boolean rubroPatrono;
    /**
     * Atributo que valida si el campo de rubro empleado es visible o
     * no
     */
    private boolean rubroEmpleado;
    /**
     * Atributo que recibe el valor del parametro mostrarRubro, este
     * es para validar de que opcion de menu se esta abriendo este
     * formulario
     */
    private boolean mostrarRubro;
    /**
     * Esta variable me almacena los datos de la lista de
     * listaRubroEmpleado
     */
    private RegistroDataModelImpl listaRubroEmpleado;
    /**
     * Esta variable me almacena los datos de la lista de
     * listaRubroPatrono
     */
    private RegistroDataModelImpl listaRubroPatrono;
    private String pais;
    private String departamento;
    private String claseFondo;
    private String valorFondo;
    private String menu;
    private static final String ID_DE_FONDO = "ID_DE_FONDO";
    private static final String CLASEFONDOO = "CLASEFONDO";
    private HashMap<String, Object> llaveRID;
    private String tituloFondos;
    private String paginado;
    private RegistroDataModelImpl listaDocSiifCesantias;

    /*
     * Estra variable me oculta el subformulario de clases que se
     * encuentra en el formulario de fondos.
     */
    private boolean ocultarClases;
    /*
     * Esta variable me muestra u oculta el boton de insertar
     */
    private boolean mostrarInsertar;
    /*
     * Esta variable me bloquea los campos de SIIF en el formulario de
     * fondos.
     */
    private boolean elementosBloqueados;
    /*
     * Estas 3 varables me permiten determinar si voy a mostrar o no
     * los campos de tipo_cuentas, rubro_empleado y rubro_patrono.
     */
    private boolean bloqueoRubroEmpleado;
    /*
     * Esta variable me bloquea los campos de SIIF en el formulario de
     * fondos.
     */
    private boolean bloqueoRubroPatrono;

    @EJB
    private EjbSysmanUtil ejbSysmanUtil;

    public FondosControlador() {
        super();
        compania = SessionUtil.getCompania();
        // formulario 33
        numFormulario = GeneralCodigoFormaEnum.FONDOS_CONTROLADOR.getCodigo();
        menu = SessionUtil.getMenuActual();
        registro = new Registro(new HashMap<String, Object>());

        mostrarInsertar = false;
        elementosBloqueados = true;
        ocultarClases = false;
        try {
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                claseFondo = (String) parametrosEntrada.get("claseFondo");
                mostrarRubro = (boolean) SysmanFunciones.nvl(
                                parametrosEntrada.get("mostrarRubro"), false);
                llaveRID = (HashMap<String, Object>) parametrosEntrada
                                .get("ridR");

                String retorno = (String) SessionUtil
                                .getSessionVarContainer(
                                                FondosControladorEnum.RETORNO
                                                                .getValue());

                if ((claseFondo.equals(FondosControladorEnum.AFP.getValue())
                    || claseFondo.equals(FondosControladorEnum.EPS.getValue()))
                    && retorno != null) {
                    bloqueoRubroEmpleado = false;
                    bloqueoRubroPatrono = false;
                }
                else if ("FRP,APV,AFC,CCF".contains(claseFondo)
                    && retorno != null) {
                    bloqueoRubroEmpleado = true;
                    bloqueoRubroPatrono = false;
                }
                else {
                    mostrarInsertar = true;
                    elementosBloqueados = false;
                    ocultarClases = true;
                    bloqueoRubroPatrono = true;
                    bloqueoRubroEmpleado = true;
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(FondosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        registro = new Registro(new HashMap<String, Object>());
        registroSub = new Registro(new HashMap<String, Object>());

    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.FONDO.getTable();
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Este formulario se puede abrir desde nomina y hojas de vida,
     * Por lo que se implento una validacion para que cuando ingrese
     * desde la opcion de menu del modulo de hojas de vida este
     * ingrese al fondo de sindicato.
     */

    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(FondosControladorEnum.PARAM3.getValue(),
                        claseFondo);

        obtenerTitulo();
        if (!("NAS".equals(claseFondo))) {
            paginado = FondosControladorUrlEnum.URL14775.getValue();
        }
        else {
            paginado = FondosControladorUrlEnum.URL14770.getValue();
        }

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(paginado);

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FondosControladorUrlEnum.URL14772.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FondosControladorUrlEnum.URL14774
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FondosControladorUrlEnum.URL14773
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FondosControladorUrlEnum.URL14771.getValue());

    }

    @Override
    public void iniciarListas() {
        cargarListaNit();
        cargarListaPais();
        cargarListaBancoPago();
        cargarListaCLASE();

        cargarListaRubroEmpleado();
        cargarListaRubroPatrono();
        cargarListaDocSiifCesantias();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubClaseFondo();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubclasefondo = null;
    }

    public void agregarRegistroSubSubclasefondo() {
        try {

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(ID_DE_FONDO,
                            registro.getCampos().get(ID_DE_FONDO));
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos().remove("NOMBRE_CLASEFONDO");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FondosControladorUrlEnum.URL13371
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubClaseFondo();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
        }
        catch (SystemException ex) {
            Logger.getLogger(FondosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            ex.getMessage()));
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubclasefondo(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("NOMBRE_CLASEFONDO");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put("KEY_COMPANIA", reg.getCampos()
                            .get(GeneralParameterEnum.COMPANIA.getName()));
            reg.getCampos().put("KEY_ID", reg.getCampos().get("ID"));
            reg.getCampos().put(FondosControladorEnum.PARAM6.getValue(), reg
                            .getCampos()
                            .get(FondosControladorEnum.PARAM6.getValue()));
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove("ID");

            Parameter parameter = new Parameter();

            parameter.setFields(reg.getCampos());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FondosControladorUrlEnum.URL15368
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_MODIFICADO));
        }
        catch (SystemException ex) {
            Logger.getLogger(FondosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            ex.getMessage()));
        }
        finally {
            cargarListaSubClaseFondo();
        }
    }

    public void eliminarRegSubSubclasefondo(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FondosControladorUrlEnum.URL14769
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubClaseFondo();

        }
        catch (SystemException ex) {
            Logger.getLogger(FondosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            ex.getMessage()));
        }
    }

    public void obtenerTitulo() {
        if ("60106".equals(SessionUtil.getMenuActual())) {
            tituloFondos = "FONDOS";
        }
        else {
            tituloFondos = "FONDOS DE SINDICATOS";
        }
    }

    public void requeryPais() {
        // <CODIGO_DESARROLLADO>

        try {
            pais = registro.getCampos().get("PAIS").toString();

            Map<String, Object> param = new TreeMap<>();

            param.put(FondosControladorEnum.PARAM4.getValue(), pais);

            listaDepartamento = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL63461
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        requeryPais();

        departamento = null;
        registro.getCampos().put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        null);
        registro.getCampos().put(GeneralParameterEnum.CIUDAD.getName(), null);
        listaCiudad = null;

        // </CODIGO_DESARROLLADO>
    }

    public void requeryDepartamento() {
        // <CODIGO_DESARROLLADO>
        departamento = registro.getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO.getName())
                        .toString();

        Map<String, Object> param = new TreeMap<>();

        param.put(FondosControladorEnum.PARAM4.getValue(), pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamento);

        try {
            listaCiudad = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL16911
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        requeryDepartamento();

        registro.getCampos().put(GeneralParameterEnum.CIUDAD.getName(), null);

    }

    public void cambiarNit() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaDocSiifCesantias
     *
     */
    public void cargarListaDocSiifCesantias() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FondosControladorUrlEnum.URL15377
                                                        .getValue());

        listaDocSiifCesantias = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FondosControladorEnum.DCTO_IDENTIDAD.getValue());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * 
     * Carga la lista listaRubroEmpleado
     *
     */
    public void cargarListaRubroEmpleado() {
        Map<String, Object> param = new TreeMap<>();

        param.put("COMPANIA", compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FondosControladorUrlEnum.URL15376
                                                        .getValue());

        listaRubroEmpleado = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaRubroPatrono
     *
     */
    public void cargarListaRubroPatrono() {
        Map<String, Object> param = new TreeMap<>();

        param.put("COMPANIA", compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FondosControladorUrlEnum.URL15376
                                                        .getValue());

        listaRubroPatrono = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaNit(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos().get("SUCURSAL"));
        registro.getCampos().put("NOMBRE_FONDO",
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDocSiifCesantias
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDocSiifCesantias(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(FondosControladorEnum.DOC_SIIF.getValue(),
                        registroAux.getCampos().get(
                                        FondosControladorEnum.DCTO_IDENTIDAD
                                                        .getValue()));

        nombreSiif = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBRO_SIIF_EMPLEADO",
                        registroAux.getCampos().get("CODIGO"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroPatrono
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroPatrono(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBRO_SIIF_PATRONO",
                        registroAux.getCampos().get("CODIGO"));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if (mostrarRubro) {
            rubroPatrono = rubroEmpleado = true;

        }
        else {
            rubroPatrono = rubroEmpleado = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    private void cargarRegistroAux1() {
        registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());
        departamento = null;
        registro.getCampos().put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        null);
        listaDepartamento = null;
        listaCiudad = null;
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
        try {
            switch (accion) {
            case "i":
                cargarRegistroAux1();
                break;
            case "m":
                cargarTipoM();

                if (!SysmanFunciones.validarVariableVacio(SysmanFunciones
                                .nvl(registro.getCampos().get(
                                                FondosControladorEnum.DOC_SIIF
                                                                .getValue()),
                                                "")
                                .toString())) {

                    Map<String, Object> params = new TreeMap<>();

                    params.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);

                    params.put(FondosControladorEnum.DCTO_IDENTIDAD.getValue(),
                                    registro.getCampos().get(
                                                    FondosControladorEnum.DOC_SIIF
                                                                    .getValue()));

                    nombreSiif = SysmanFunciones.nvl(
                                    listaDocSiifCesantias
                                                    .getRegistroUnico(params)
                                                    .getCampos()
                                                    .get("DESCRIPCION"),
                                    "")
                                    .toString();
                }
                else {
                    nombreSiif = "";
                }
                break;
            default:
                break;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarTipoM() {
        registro.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        requeryPais();
        requeryDepartamento();

    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cargarListaSubClaseFondo() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FondosControladorEnum.PARAM1.getValue(),
                            registro.getCampos().get(ID_DE_FONDO));

            listaSubclasefondo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL91001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            CLASEFONDOO));

        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(FondosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPais() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaPais = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FondosControladorUrlEnum.URL14375
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDepartamento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(FondosControladorEnum.PARAM4.getValue(), pais);

        try {
            listaDepartamento = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL71121
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCiudad() {

        Map<String, Object> param = new TreeMap<>();
        param.put(FondosControladorEnum.PARAM4.getValue(), pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamento);

        try {
            listaCiudad = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL16613
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaBancoPago() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaBancoPago = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL49751
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCLASE() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCLASE = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FondosControladorUrlEnum.URL16091
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaNit() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FondosControladorUrlEnum.URL17546
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNit = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    @Override
    public boolean insertarAntes() {
        try {

            long id = ejbSysmanUtil.generarSiguienteConsecutivo(tabla,
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania, "''  "),
                            ID_DE_FONDO);

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(ID_DE_FONDO, id);

        }
        catch (SystemException ex) {
            Logger.getLogger(FondosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean insertarDespues() {
        if (claseFondo != null) {

            try {
                // <CODIGO_DESARROLLADO>
                HashMap<String, Object> sub = new HashMap<>();
                sub.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                sub.put("CLASE", claseFondo);
                sub.put(ID_DE_FONDO, registro.getCampos().get(ID_DE_FONDO));
                sub.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                sub.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FondosControladorUrlEnum.URL13371
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                sub);
                // </CODIGO_DESARROLLADO>
            }
            catch (SystemException ex) {
                Logger.getLogger(FondosControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                SysmanFunciones.concatenar(
                                                idioma.getString(
                                                                Constantes.MSM_TRANS_INTERRUMPIDA),
                                                ex.getMessage()));
            }
        }
        return true;
    }

    public void ejecutarrcCerrar() {

        if ("60101".equals(menu)) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("ridR", llaveRID);

            Direccionador direccionador = new Direccionador();
            direccionador.setParametros(param);
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PERSONALS_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }

        else {

            try {
                String retorno = (String) SessionUtil
                                .getSessionVarContainer("retorno");

                if (retorno != null) {
                    SessionUtil.redireccionarMenuFormulario(retorno);
                }
                else {
                    SessionUtil.redireccionarMenu();
                }

                SessionUtil.removeSessionVarContainer("retorno");
            }
            catch (NamingException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

    public void cancelarEdicionSubclasefondo() {
        // heredado del bean base

    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public List<Registro> getListaSubclasefondo() {
        return listaSubclasefondo;
    }

    public void setListaSubclasefondo(List<Registro> listaSubclasefondo) {
        this.listaSubclasefondo = listaSubclasefondo;
    }

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaBancoPago() {
        return listaBancoPago;
    }

    public void setListaBancoPago(List<Registro> listaBancoPago) {
        this.listaBancoPago = listaBancoPago;
    }

    public List<Registro> getListaCLASE() {
        return listaCLASE;
    }

    public void setListaCLASE(List<Registro> listaCLASE) {
        this.listaCLASE = listaCLASE;
    }

    public RegistroDataModelImpl getListaNit() {
        return listaNit;
    }

    public void setListaNit(RegistroDataModelImpl listaNit) {
        this.listaNit = listaNit;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getClaseFondo() {
        return claseFondo;
    }

    public void setClaseFondo(String claseFondo) {
        this.claseFondo = claseFondo;
    }

    public String getValorFondo() {
        return valorFondo;
    }

    public void setValorFondo(String valorFondo) {
        this.valorFondo = valorFondo;
    }

    public String getTituloFondos() {
        return tituloFondos;
    }

    public void setTituloFondos(String tituloFondos) {
        this.tituloFondos = tituloFondos;
    }

    public boolean isRubroPatrono() {
        return rubroPatrono;
    }

    public void setRubroPatrono(boolean rubroPatrono) {
        this.rubroPatrono = rubroPatrono;
    }

    public boolean isRubroEmpleado() {
        return rubroEmpleado;
    }

    public void setRubroEmpleado(boolean rubroEmpleado) {
        this.rubroEmpleado = rubroEmpleado;
    }

    /**
     * Asigna la lista listaRubroPatrono
     * 
     * @param listaRubroPatrono
     * Variable a asignar en listaRubroPatrono
     */
    public void setListaRubroPatrono(RegistroDataModelImpl listaRubroPatrono) {
        this.listaRubroPatrono = listaRubroPatrono;
    }

    /**
     * Retorna la lista listaRubroEmpleado
     * 
     * @return listaRubroEmpleado
     */
    public RegistroDataModelImpl getListaRubroEmpleado() {
        return listaRubroEmpleado;
    }

    /**
     * Asigna la lista listaRubroEmpleado
     * 
     * @param listaRubroEmpleado
     * Variable a asignar en listaRubroEmpleado
     */
    public void setListaRubroEmpleado(
        RegistroDataModelImpl listaRubroEmpleado) {
        this.listaRubroEmpleado = listaRubroEmpleado;
    }

    /**
     * Retorna la lista listaRubroPatrono
     * 
     * @return listaRubroPatrono
     */
    public RegistroDataModelImpl getListaRubroPatrono() {
        return listaRubroPatrono;
    }

    public String getPaginado() {
        return paginado;
    }

    public void setPaginado(String paginado) {
        this.paginado = paginado;
    }

    public boolean getMostrarInsertar() {
        return mostrarInsertar;
    }

    public void setMostrarInsertar(boolean mostrarInsertar) {
        this.mostrarInsertar = mostrarInsertar;
    }

    public boolean isElementosBloqueados() {
        return elementosBloqueados;
    }

    public void setElementosBloqueados(boolean elementosBloqueados) {
        this.elementosBloqueados = elementosBloqueados;
    }

    public boolean isOcultarClases() {
        return ocultarClases;
    }

    public void setOcultarClases(boolean ocultarClases) {
        this.ocultarClases = ocultarClases;
    }

    public boolean isBloqueoRubroEmpleado() {
        return bloqueoRubroEmpleado;
    }

    public void setBloqueoRubroEmpleado(boolean bloqueoRubroEmpleado) {
        this.bloqueoRubroEmpleado = bloqueoRubroEmpleado;
    }

    public boolean isBloqueoRubroPatrono() {
        return bloqueoRubroPatrono;
    }

    public void setBloqueoRubroPatrono(boolean bloqueoRubroPatrono) {
        this.bloqueoRubroPatrono = bloqueoRubroPatrono;
    }

    /**
     * Retorna la lista listaDocSiifCesantias
     * 
     * @return listaDocSiifCesantias
     */
    public RegistroDataModelImpl getListaDocSiifCesantias() {
        return listaDocSiifCesantias;
    }

    /**
     * Asigna la lista listaDocSiifCesantias
     * 
     * @param listaDocSiifCesantias
     * Variable a asignar en listaDocSiifCesantias
     */
    public void setListaDocSiifCesantias(
        RegistroDataModelImpl listaDocSiifCesantias) {
        this.listaDocSiifCesantias = listaDocSiifCesantias;
    }

    public String getNombreSiif() {
        return nombreSiif;
    }

    public void setNombreSiif(String nombreSiif) {
        this.nombreSiif = nombreSiif;
    }

}
