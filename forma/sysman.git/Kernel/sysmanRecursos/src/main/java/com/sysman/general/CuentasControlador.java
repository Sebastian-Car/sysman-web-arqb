package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CuentasControladorEnum;
import com.sysman.general.enums.CuentasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbMenukRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import sysman.util.consumo.enums.EnumParametros;

import java.math.BigDecimal;
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
import javax.servlet.Registration;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 * @version 1, 29/09/2015
 * @author yrojas
 * @version 2, 21/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 * @author jcrodriguez
 * @version 3, 25/05/2017 depuracion del controaldor llevando a las
 * cadenas al enum del controaldor DSS:correccion
 * 
 * @author asana
 * @version 4, 12/06/2017 Redireccion de formulario.
 * 
 * @author jguerrero
 * @version 4, 22/01/2018 Creacion combo y etiqueta No.
 * Identificacion.
 * 
 */
@ManagedBean
@ViewScoped
public class CuentasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que contiene el codigo del usuario
     * que inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    private final String compania = SessionUtil.getCompaniaIngreso()
                    .getCodigo();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>USUARIO</code>.
     */
    private final String cUsuario = GeneralParameterEnum.USUARIO.getName();

    private Registro registroSubCuentaGrupos;
    private Registro registroSubCuentaDependencias;
    private Registro registroSubCuentaMenu;
    private Registro registroSubSubCuentaFormulario;
    private Registro registroSubCuentaCompania;
    private Registro registroSubSubMovimientosAcceso;

    private List<Registro> listaListaApliaciones;
    private List<Registro> listaListaGeneros;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private List<Registro> listaListaGrupos;
    private List<Registro> listaListaCompania;
    private List<Registro> listalistaCompaniaPest;
    private List<Registro> listaListaCompaniaForm;
    private List<Registro> listaCuentagrupos;
    private List<Registro> listaCuentadependencias;
    private List<Registro> listaListaAplicacionForm;

    /** Lista que contiene los items del combo menu (CB5781) */
    private List<Registro> listaCodigoMenu;
    private RegistroDataModelImpl listaCuentamenu;
    private RegistroDataModelImpl listaSubcuentaformulario;
    private List<Registro> listaCuentacompania;
    private RegistroDataModelImpl listaResponsableAsociado;
    private RegistroDataModelImpl listaResponsableAsociadoE;
    private RegistroDataModelImpl listaListaDependencias;
    private RegistroDataModelImpl listaListaDependenciasE;
    private String auxiliar;
    private RegistroDataModelImpl listaListaMenus;
    private RegistroDataModelImpl listaListaMenusE;
    private RegistroDataModelImpl listaListaFormularios;
    private RegistroDataModelImpl listaListaFormulariosE;
    private RegistroDataModelImpl listaSubmovimientosacceso;
    private String companiaMenu;
    private String companiaFormulario;
    private String companiaDependencia;
    private String dependencia;
    private String sucursal;
    private String pais;
    private String dpto;
    private int indiceCuentadependencias;
    private String descMenu;
    private String nombreForm;
    private String descForm;
    private String rutaForm;
    private String contraseniaAux;
    private String politicaContrasena;
    private int cantidadNumeros = 0;
    private int cantidadLetras = 0;
    private int cantidadSimbolos = 0;
    private String aplicacionFormulario;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Atributo que contiene el valor asignado en el campo codigo
     * (CP53440) de la pestaña menus de acceso.
     */
    private String codigoMenu;

    private RegistroDataModelImpl listanitTercero;

    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete <code>MENUK</code>.
     */
    @EJB
    private EjbMenukRemote ejbMenuk;

	private boolean manejaFechaLimite;

	private boolean esAdministrador = false;
    // </DECLARAR_EJBs>

    public CuentasControlador() {
        super();

        try {
            numFormulario = GeneralCodigoFormaEnum.CUENTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            urlConexionCache = UrlServiceCache.SYSMANIRISST;
            registroSubCuentaGrupos = new Registro(
                            new HashMap<String, Object>());
            registroSubCuentaDependencias = new Registro(
                            new HashMap<String, Object>());
            registroSubCuentaMenu = new Registro(new HashMap<String, Object>());
            registroSubSubCuentaFormulario = new Registro(
                            new HashMap<String, Object>());
            registroSubCuentaCompania = new Registro(
                            new HashMap<String, Object>());
            registroSubSubMovimientosAcceso = new Registro(
                    new HashMap<String, Object>());
            indiceCuentadependencias = -1;
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.USUARIO;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    @Override
    public void iniciarListas() {
        companiaMenu = null;
        companiaFormulario = null;
        listaCuentamenu = null;
        listaSubcuentaformulario = null;
        listaListaAplicacionForm = null;
        cargarListalistaApliaciones();
        cargarListalistaGeneros();
        cargarListaPais();
        cargarListalistaCompania();
        cargarListanitTercero();
    }

    @Override
    public void iniciarListasSub() {
        companiaMenu = null;
        companiaFormulario = null;
        listaCuentamenu = null;
        listaSubcuentaformulario = null;
        listaSubmovimientosacceso = null;
        if ("U".equals(registro.getCampos()
                        .get(CuentasControladorEnum.TIPOCUENTA.getValue()))) {
            cargarListaCuentagrupos();
            cargarListaCuentadependencias();
            cargarListalistaGrupos();
        }
        else {
            cargarListaCuentamenu();
            cargarListalistaMenus();
            cargarListalistaMenusE();
            cargarListalistaFormularios();
            cargarListalistaFormulariosE();
            cargarListaSubcuentaformulario();
            cargarListaCuentacompania();
            cargarListalistaCompaniaPest();
        }

      cargarListaListaAplicacionForm();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaCuentagrupos = null;
        listaCuentadependencias = null;
        listaCuentamenu = null;
        listaSubcuentaformulario = null;
        listaCuentacompania = null;
        listaSubmovimientosacceso = null;
    }

    /**
     * Carga la lista: <code>listaCodigoMenu</code> asociada al combo
     * menu (CB5781).
     */
    public void cargarListaCodigoMenu() {
        String grupo = SysmanFunciones
                        .nvl(registro.getCampos().get("CODIGO"), "").toString();

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, companiaMenu);
        param.put("GRUPO", grupo);

        try {
            listaCodigoMenu = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL0001
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
     * Carga la lista listanitTercero
     *
     */
    public void cargarListanitTercero() {

        Map<String, Object> param = new TreeMap<>();

        param.put("COMPANIA", compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL20829
                                                        .getValue());

        listanitTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");

    }

    public void cargarListaCuentagrupos() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(cUsuario, registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));

            listaCuentagrupos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.USUARIO_D
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.USUARIO_D
                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCuentadependencias() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(cUsuario, registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));

            listaCuentadependencias = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.USUARIO_DEPENDENCIA
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            GenericUrlEnum.USUARIO_DEPENDENCIA
                                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentamenu() {
        if ((companiaMenu != null) && !companiaMenu.isEmpty()) {
            try {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.GRUPO_MENU
                                                                .getGridKey());
                Map<String, Object> param = new TreeMap<>();
                param.put(cCompania, companiaMenu);
                param.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));

                listaCuentamenu = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param,
                                CacheUtil.getLlaveServicio(urlConexionCache,
                                                GenericUrlEnum.GRUPO_MENU
                                                                .getTable()));
            }
            catch (SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            listaCuentamenu = null;
        }
    }

    public void cargarListaSubcuentaformulario() {
        if ((companiaFormulario != null) && !companiaFormulario.isEmpty()) {
            try {
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.GRUPO_FORMULARIO
                                                                .getGridKey());
                Map<String, Object> param = new TreeMap<>();
                param.put(cCompania, companiaFormulario);
                param.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));

                listaSubcuentaformulario = new RegistroDataModelImpl(
                                urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(),
                                param,
                                CacheUtil.getLlaveServicio(urlConexionCache,
                                                GenericUrlEnum.GRUPO_FORMULARIO
                                                                .getTable()));
            }
            catch (SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            listaSubcuentaformulario = null;
        }
    }

    public void cargarListaCuentacompania() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            listaCuentacompania = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.GRUPO_COMPANIA
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            GenericUrlEnum.GRUPO_COMPANIA
                                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListalistaApliaciones() {
        try {
            listaListaApliaciones = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL11138
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListalistaGeneros() {
        try {
            listaListaGeneros = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL11357
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL11543
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDepartamento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(CuentasControladorEnum.PAIS.getValue(), pais);

        try {
            listaDepartamento = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL11869
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
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dpto);
        param.put(CuentasControladorEnum.PAIS.getValue(), pais);

        try {
            listaCiudad = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL12452
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListalistaGrupos() {
        try {
            listaListaGrupos = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL12938
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListalistaCompania() {
        try {
            listaListaCompania = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL13294
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaResponsableAsociado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL14451
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, companiaDependencia);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        listaResponsableAsociado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CuentasControladorEnum.CEDULA.getValue());
    }

    public void cargarListaResponsableAsociadoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL14451
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, companiaDependencia);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        listaResponsableAsociadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CuentasControladorEnum.CEDULA.getValue());
    }

    public void cargarListalistaDependencias() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL18313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, companiaDependencia);

        listaListaDependencias = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListalistaDependenciasE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL18313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, companiaDependencia);

        listaListaDependenciasE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListalistaMenus() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL19307
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CuentasControladorEnum.APLICACION.getValue(),
                        registro.getCampos()
                                        .get(CuentasControladorEnum.APLICACION
                                                        .getValue()));

        listaListaMenus = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListalistaMenusE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL19307
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CuentasControladorEnum.APLICACION.getValue(),
                        registro.getCampos()
                                        .get(CuentasControladorEnum.APLICACION
                                                        .getValue()));

        listaListaMenusE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListalistaCompaniaPest() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        try {
            listalistaCompaniaPest = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            CuentasControladorUrlEnum.URL20828
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        listaListaCompaniaForm = listalistaCompaniaPest;
    }

    public void cargarListalistaFormularios() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL20827
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CuentasControladorEnum.APLICACION.getValue(),
                        registro.getCampos()
                                        .get(CuentasControladorEnum.APLICACION
                                                        .getValue()));

        listaListaFormularios = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        CuentasControladorEnum.CODIGO_FORMULARIO.getValue());
    }

    public void cargarListalistaFormulariosE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuentasControladorUrlEnum.URL19307
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CuentasControladorEnum.APLICACION.getValue(),
                        registro.getCampos()
                                        .get(CuentasControladorEnum.APLICACION
                                                        .getValue()));

        listaListaFormulariosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        CuentasControladorEnum.CODIGO_FORMULARIO.getValue());
    }
    
    public void cargarListaListaAplicacionForm() {     
    	try {
			listaListaAplicacionForm = RegistroConverter
										    .toListRegistro(
										            requestManager
										                   .getList(
										                       UrlServiceUtil.getInstance()
										                                          .getUrlServiceByUrlByEnumID(
										                                                           CuentasControladorUrlEnum.URL58008
										                                                                               .getValue())
										                                                     .getUrl(),
										                                            null));
		} catch (SystemException e) {
			Logger.getLogger(CuentasControlador.class.getName())
            .log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
    
    public void cargarListaSubmovimientosacceso() throws SysmanException {
    	UrlBean urlBean = null;
    	if(aplicacionFormulario.equals("1")) {
    		urlBean = UrlServiceUtil.getInstance()
		                .getUrlServiceByUrlByEnumID(
		                		CuentasControladorUrlEnum.URL15081.getValue());
    	}else if(aplicacionFormulario.equals("3")) {
    		urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                		CuentasControladorUrlEnum.URL25055.getValue());
    	}else if(aplicacionFormulario.equals("10")) {
    		urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                		CuentasControladorUrlEnum.URL139031.getValue());
    	}
    	if(urlBean != null) {
			Map<String, Object> param = new TreeMap<>();
			param.put(cCompania, SessionUtil.getCompania());
			param.put(GeneralParameterEnum.USUARIO.getName(), registro.getCampos().get(CuentasControladorEnum.CODIGO.getValue()));
			param.put(GeneralParameterEnum.MODULO.getName(), aplicacionFormulario);
								
			listaSubmovimientosacceso = new RegistroDataModelImpl(
			        urlBean.getUrl(),
			        urlBean.getUrlConteo().getUrl(),
			        param, 
			        CuentasControladorEnum.CODIGO.getValue());
    	}else {
    		listaSubmovimientosacceso = null;
    	}

    }
    
    public void agregarRegistroSubCuentagrupos() {
        try {
            registroSubCuentaGrupos.getCampos().put(cUsuario,
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSubCuentaGrupos.getCampos()
                            .put(CuentasControladorEnum.APLICACION.getValue(),
                                            service.buscarEnLista(
                                                            registroSubCuentaGrupos
                                                                            .getCampos()
                                                                            .get(CuentasControladorEnum.GRUPO
                                                                                            .getValue())
                                                                            .toString(),
                                                            GeneralParameterEnum.CODIGO
                                                                            .getName(),
                                                            CuentasControladorEnum.APLICACION
                                                                            .getValue(),
                                                            listaListaGrupos));
            registroSubCuentaGrupos.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubCuentaGrupos.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubCuentaGrupos.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.USUARIO_D
                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubCuentaGrupos.getCampos());
            cargarListaCuentagrupos();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubCuentaGrupos = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubCuentagrupos(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try {
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.USUARIO_D
                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaCuentagrupos();
        }
    }

    public void eliminarRegSubCuentagrupos(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.USUARIO_D
                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaCuentagrupos();
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionCuentagrupos() {
        cargarListaCuentagrupos();
        cargarListaCuentadependencias();
        cargarListaCuentamenu();
        cargarListaSubcuentaformulario();
        cargarListaCuentacompania();
    }

    public void agregarRegistroSubCuentadependencias() {
        try {
            registroSubCuentaDependencias.getCampos().put(cUsuario,
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSubCuentaDependencias.getCampos().remove(
                            CuentasControladorEnum.NOMBRE_COMPANIA.getValue());
            registroSubCuentaDependencias.getCampos()
                            .remove(CuentasControladorEnum.NOMBRE_DEPENDENCIA
                                            .getValue());
            registroSubCuentaDependencias.getCampos().remove(
                            CuentasControladorEnum.NOMBRE_TERCERO.getValue());
            registroSubCuentaDependencias.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubCuentaDependencias.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.USUARIO_DEPENDENCIA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubCuentaDependencias.getCampos());
            cargarListaCuentadependencias();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubCuentaDependencias = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubCuentadependencias(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(
                            CuentasControladorEnum.NOMBRE_TERCERO.getValue());
            reg.getCampos().remove(
                            CuentasControladorEnum.NOMBRE_COMPANIA.getValue());
            reg.getCampos().remove(CuentasControladorEnum.NOMBRE_DEPENDENCIA
                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.USUARIO_DEPENDENCIA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex) {

            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }
        finally {
            cargarListaCuentadependencias();
        }
    }

    public void eliminarRegSubCuentadependencias(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.USUARIO_DEPENDENCIA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaCuentadependencias();
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionCuentadependencias() {
        cargarListaCuentadependencias();
        cargarListaCuentamenu();
        cargarListaSubcuentaformulario();
        cargarListaCuentacompania();
        indiceCuentadependencias = -1;
    }

    public void agregarRegistroSubCuentamenu() {

        try {        	
            registroSubCuentaMenu.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registroSubCuentaMenu.getCampos().put(cCompania, companiaMenu);
            registroSubCuentaMenu.getCampos().put(
                            CuentasControladorEnum.GRUPO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSubCuentaMenu.getCampos().put(
                            CuentasControladorEnum.APLICACION.getValue(),
                            registro.getCampos().get(
                                            CuentasControladorEnum.APLICACION
                                                            .getValue()));
            registroSubCuentaMenu.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubCuentaMenu.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_MENU
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubCuentaMenu.getCampos());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubCuentaMenu = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubCuentamenu(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try {
            reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_MENU
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            listaCuentamenu.load();
        }
    }

    public void eliminarRegSubCuentamenu(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_MENU
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            listaCuentamenu.load();
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionCuentamenu() {
        listaCuentamenu.load();
    }

    public void agregarRegistroSubSubcuentaformulario() {

        try {        	
            registroSubSubCuentaFormulario.getCampos().remove(
                            CuentasControladorEnum.DESCRIPCION_FORMULARIO
                                            .getValue());
            registroSubSubCuentaFormulario.getCampos()
                            .remove(CuentasControladorEnum.NOMBRE_FORMULARIO
                                            .getValue());
            registroSubSubCuentaFormulario.getCampos()
                            .remove(CuentasControladorEnum.RUTA.getValue());
            registroSubSubCuentaFormulario.getCampos().put(
                            CuentasControladorEnum.GRUPO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSubSubCuentaFormulario.getCampos().put(cCompania,
                            companiaFormulario);
            registroSubSubCuentaFormulario.getCampos().put(
                            CuentasControladorEnum.APLICACION.getValue(),
                            registro.getCampos().get(
                                            CuentasControladorEnum.APLICACION
                                                            .getValue()));
            registroSubSubCuentaFormulario.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubCuentaFormulario.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_FORMULARIO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubCuentaFormulario.getCampos());
            listaSubcuentaformulario.load();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubCuentaFormulario = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubcuentaformulario(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try {
            reg.getCampos().remove(CuentasControladorEnum.NOMBRE_FORMULARIO
                            .getValue());
            reg.getCampos().remove(CuentasControladorEnum.DESCRIPCION_FORMULARIO
                            .getValue());
            reg.getCampos().remove(CuentasControladorEnum.RUTA.getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_FORMULARIO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            listaSubcuentaformulario.load();
        }
    }

    public void eliminarRegSubSubcuentaformulario(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_FORMULARIO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            listaSubcuentaformulario.load();
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }
    public void cancelarEdicionSubcuentaformulario() {
        listaSubcuentaformulario.load();
    }

    public void agregarRegistroSubCuentacompania() {
        try {
            registroSubCuentaCompania.getCampos().put(
                            CuentasControladorEnum.GRUPO.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSubCuentaCompania.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());
            registroSubCuentaCompania.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubCuentaCompania.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_COMPANIA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubCuentaCompania.getCampos());
            cargarListaCuentacompania();
            cargarListalistaCompaniaPest();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubCuentaCompania = new Registro(
                            new HashMap<String, Object>());
        }

    }

    public void editarRegSubCuentacompania() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void eliminarRegSubCuentacompania(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.GRUPO_COMPANIA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaCuentacompania();
            cargarListalistaCompaniaPest();
            cambiarlistaCompaniaPest();
            cambiarListaCompaniaForm();
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionCuentacompania() {
        cargarListaCuentacompania();
    }
    public void agregarRegistroSubSubmovimientosacceso(Registro reg) {
    	try {
    		int ver = 0;
    		if((boolean) reg.getCampos().get(CuentasControladorEnum.VER.getValue())) {
    			ver = -1;
    		}
    		
    		registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                    SessionUtil.getUser().getCodigo());
        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            
        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), SessionUtil.getCompania());
        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.TIPO.getName(), reg.getCampos().get(CuentasControladorEnum.CODIGO.getValue()));
        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.USUARIO.getName(), registro.getCampos()
                    .get(GeneralParameterEnum.CODIGO.getName()).toString());
        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.APLICACION.getName(), aplicacionFormulario);
        	registroSubSubMovimientosAcceso.getCampos().put(CuentasControladorEnum.VER.getValue(), String.valueOf(ver));


            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		CuentasControladorUrlEnum.URL1946003.getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
            		registroSubSubMovimientosAcceso.getCampos());
            
            cargarListaSubmovimientosacceso();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            CuentasControladorEnum.MSM_REGISTRO_INGRESADO
                                                            .getValue()));
        }
        catch (SystemException | SysmanException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
        	registroSubSubMovimientosAcceso = new Registro(
                            new HashMap<String, Object>());
        }
    }
    public void editarRegSubSubmovimientosacceso(RowEditEvent event) {
    	Registro reg = (Registro) event.getObject();
        try { 
        	// se consulta primero si no existe se crea y si existe se actaualiza
        	registroSubSubMovimientosAcceso = new Registro();
        	
        	Map<String, Object> param = new TreeMap<>();
        	param.put(GeneralParameterEnum.COMPANIA.getName(), SessionUtil.getCompania());
        	param.put(GeneralParameterEnum.TIPO.getName(), reg.getCampos().get(CuentasControladorEnum.CODIGO.getValue()));
        	param.put(GeneralParameterEnum.USUARIO.getName(), registro.getCampos()
                    .get(GeneralParameterEnum.CODIGO.getName()));
        	param.put(CuentasControladorEnum.APLICACION.getValue(), aplicacionFormulario);
        	
        	Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                                    UrlServiceUtil.getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    CuentasControladorUrlEnum.URL1946002
                                                                                    .getValue())
                                                    .getUrl(),
                                    param));
        	if (rs != null) {
        		if((int) rs.getCampos().get("EXITO") > 0) {
		        	registroSubSubMovimientosAcceso.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		        	registroSubSubMovimientosAcceso.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
		        	
		        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
		                    SessionUtil.getUser().getCodigo());
		        	registroSubSubMovimientosAcceso.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
		                            new Date());
		            
		        	registroSubSubMovimientosAcceso.getLlave().put(CuentasControladorEnum.KEY_COMPANIA.getValue(), SessionUtil.getCompania());
		        	registroSubSubMovimientosAcceso.getLlave().put(CuentasControladorEnum.KEY_TIPO.getValue(), reg.getCampos().get(CuentasControladorEnum.CODIGO.getValue()));
		        	registroSubSubMovimientosAcceso.getLlave().put(CuentasControladorEnum.KEY_USUARIO.getValue(), registro.getCampos()
		                    .get(GeneralParameterEnum.CODIGO.getName()));
		        	registroSubSubMovimientosAcceso.getLlave().put(CuentasControladorEnum.KEY_APLICACION.getValue(), aplicacionFormulario);
		        	int ver = 0;
		    		if((boolean) reg.getCampos().get(CuentasControladorEnum.VER.getValue())) {
		    			ver = -1;
		    		}
		        	registroSubSubMovimientosAcceso.getCampos().put(CuentasControladorEnum.VER.getValue(), String.valueOf(ver));
		        	
		            UrlBean urlUpdate = UrlServiceUtil.getInstance()
		                            .getUrlServiceByUrlByEnumID(
		                            		CuentasControladorUrlEnum.URL1946001.getValue());
		            
		            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
		            		registroSubSubMovimientosAcceso.getCampos(), registroSubSubMovimientosAcceso.getLlave());
		            
		            JsfUtil.agregarMensajeInformativo(
		                            idioma.getString(
		                                            CuentasControladorEnum.MSM_REGISTRO_MODIFICADO
		                                                            .getValue()));
        		}else {
        			agregarRegistroSubSubmovimientosacceso(reg);
        		}
        	}
        }
        catch (SystemException ex) {
            Logger.getLogger(CuentasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            listaSubmovimientosacceso.load();
        }
    }
    public void cancelarEdicionSubmovimientosacceso() {
    	
    }
    public void eliminarRegSubSubmovimientosacceso(Registro reg) {
    	
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanitTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanitTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CEDULA", registroAux.getCampos().get("NIT"));
        registro.getCampos().put("NOMBRE1",
                        registroAux.getCampos().get("NOMBRE1"));

        registro.getCampos().put("NOMBRE2",
                        registroAux.getCampos().get("NOMBRE2"));

        registro.getCampos().put("APELLIDO1",
                        registroAux.getCampos().get("APELLIDO1"));

        registro.getCampos().put("APELLIDO2",
                        registroAux.getCampos().get("APELLIDO2"));

        registro.getCampos().put("CORREOELECTRONICO",
                        registroAux.getCampos().get("DIRECCIONEMAIL"));

        registro.getCampos().put("DIRECCION",
                        registroAux.getCampos().get("DIRECCION"));

        registro.getCampos().put("CELULAR",
                        registroAux.getCampos().get("TELEFONOS"));

        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));
    }

    public void seleccionarFilaResponsableAsociado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubCuentaDependencias.getCampos().put(
                        GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos()
                                        .get(CuentasControladorEnum.CEDULA
                                                        .getValue()));
        registroSubCuentaDependencias.getCampos().put(
                        GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    public void seleccionarFilaResponsableAsociadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(CuentasControladorEnum.CEDULA.getValue()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(CuentasControladorEnum.CEDULA
                                                            .getValue())
                                            .toString();
        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString();
        indiceCuentadependencias = -1;
    }

    public void seleccionarFilaListaDependencias(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubCuentaDependencias.getCampos().put(
                        GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        dependencia = registroSubCuentaDependencias.getCampos()
                        .get(GeneralParameterEnum.DEPENDENCIA.getName()) == null
                            ? " "
                            : registroSubCuentaDependencias.getCampos().get(
                                            GeneralParameterEnum.DEPENDENCIA
                                                            .getName())
                                            .toString();
        cargarListaResponsableAsociado();
        registroSubCuentaDependencias.getCampos()
                        .put(GeneralParameterEnum.RESPONSABLE.getName(), null);
        registroSubCuentaDependencias.getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(), null);
    }

    public void seleccionarFilaListaDependenciasE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        indiceCuentadependencias = -1;
    }

    public void seleccionarFilaListaMenus(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubCuentaMenu.getCampos().put("MENU",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registroSubCuentaMenu.getCampos().put(
                        GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
    }

    public void seleccionarFilaListaMenusE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        descMenu = registroAux.getCampos()
                        .get(GeneralParameterEnum.DESCRIPCION.getName()) == null
                            ? " "
                            : registroAux.getCampos().get(
                                            GeneralParameterEnum.DESCRIPCION
                                                            .getName())
                                            .toString();
    }

    public void seleccionarFilaListaFormularios(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubCuentaFormulario.getCampos().put(
                        CuentasControladorEnum.FORMULARIO.getValue(),
                        registroAux.getCampos().get(
                                        CuentasControladorEnum.CODIGO_FORMULARIO
                                                        .getValue()));
        registroSubSubCuentaFormulario.getCampos().put(
                        CuentasControladorEnum.NOMBRE_FORMULARIO.getValue(),
                        registroAux.getCampos().get(
                                        CuentasControladorEnum.NOMBRE_FORMULARIO
                                                        .getValue()));
        registroSubSubCuentaFormulario.getCampos().put(
                        CuentasControladorEnum.DESCRIPCION_FORMULARIO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        CuentasControladorEnum.DESCRIPCION_FORMULARIO
                                                        .getValue()));
        registroSubSubCuentaFormulario.getCampos().put(
                        CuentasControladorEnum.RUTA.getValue(),
                        registroAux.getCampos().get(CuentasControladorEnum.RUTA
                                        .getValue()));

    }

    public void seleccionarFilaListaFormulariosE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = ((BigDecimal) registroAux.getCampos().get(
                        CuentasControladorEnum.CODIGO_FORMULARIO.getValue()))
                                        .toString();
        nombreForm = registroAux.getCampos()
                        .get(CuentasControladorEnum.NOMBRE_FORMULARIO
                                        .getValue()) == null ? " "
                                            : registroAux.getCampos().get(
                                                            CuentasControladorEnum.NOMBRE_FORMULARIO
                                                                            .getValue())
                                                            .toString();
        descForm = registroAux.getCampos()
                        .get(CuentasControladorEnum.DESCRIPCION_FORMULARIO
                                        .getValue()) == null ? " "
                                            : registroAux.getCampos().get(
                                                            CuentasControladorEnum.DESCRIPCION_FORMULARIO
                                                                            .getValue())
                                                            .toString();
        rutaForm = registroAux.getCampos()
                        .get(CuentasControladorEnum.RUTA.getValue()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(CuentasControladorEnum.RUTA
                                                            .getValue())
                                            .toString();
    }

    public void activarEdicionCuentadependencias(Registro r) {
        indiceCuentadependencias = listaCuentadependencias.indexOf(r);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Asignar (BT3031) en la
     * vista.
     */
    public void oprimirAsignar() {
        // <CODIGO_DESARROLLADO>
        asignarAccesoMenus(true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Quitar (BT3032) en la
     * vista.
     */
    public void oprimirQuitar() {
        // <CODIGO_DESARROLLADO>
        asignarAccesoMenus(false);
        // </CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton SeleccionarTodos
     * en la vista
     *
     *
     */
    public void oprimirSeleccionarTodos() {
    	//<CODIGO_DESARROLLADO>
    	permiteSeleccQuitarTodos("-1");
    	JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
    	//</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton QuitarTodos
     * en la vista
     *
     *
     */
    public void oprimirQuitarTodos() {
    	//<CODIGO_DESARROLLADO>
    	permiteSeleccQuitarTodos("0");
    	JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
    	//</CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>
    
    public void insertarAccesos() {

    	try {
    		UrlBean urlBean = null;
    		if(aplicacionFormulario.equals("1")) {
    			urlBean = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							CuentasControladorUrlEnum.URL1946005.getValue());
    		}else if(aplicacionFormulario.equals("3")) {
    			urlBean = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							CuentasControladorUrlEnum.URL1946006.getValue());
    		}else if(aplicacionFormulario.equals("10")) {
    			urlBean = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							CuentasControladorUrlEnum.URL1946007.getValue());
    		}

    		Map<String, Object> param = new TreeMap<>();
    		param.put(cCompania, SessionUtil.getCompania());
    		param.put(GeneralParameterEnum.USUARIO.getName(), registro.getCampos().get(CuentasControladorEnum.CODIGO.getValue()));
    		param.put(GeneralParameterEnum.MODULO.getName(), aplicacionFormulario);
    		param.put("CREATED", SessionUtil.getUser().getCodigo());


    		requestManager.save(urlBean.getUrl(), urlBean.getMetodo(), param);

    	} catch (SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}

    }

    public void permiteSeleccQuitarTodos(String ver) {
    	// <CODIGO_DESARROLLADO>
    	try {

    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(),
    				compania);
    		param.put(GeneralParameterEnum.USUARIO.getName(), registro.getCampos().get(CuentasControladorEnum.CODIGO.getValue()));
    		param.put(GeneralParameterEnum.MODULO.getName(), aplicacionFormulario);
    		param.put("VER", ver);
    		param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
    		param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
    		
    		Parameter parameter = new Parameter();
    		parameter.setFields(param);
    		
    		UrlBean urlUpdate = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						CuentasControladorUrlEnum.URL1946004.getValue());
    		
    		requestManager.update(urlUpdate.getUrl(),
    				urlUpdate.getMetodo(),
    				parameter);
    	}
    	catch (SystemException e) {
    		JsfUtil.agregarMensajeError(e.getMessage());
    		logger.error(e.getMessage(), e);

    	}
    	// </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        manejaFechaLimite = false;
        consultarPoliticaContrasena();

        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos().put(
                            CuentasControladorEnum.TIPOCUENTA.getValue(), "U");
            registro.getCampos().put("MAN_FECHA_LIMITE", false);
        }
        else {
            pais = registro.getCampos()
                            .get(CuentasControladorEnum.PAIS.getValue()) == null
                                ? " "
                                : registro.getCampos()
                                                .get(CuentasControladorEnum.PAIS
                                                                .getValue())
                                                .toString();
            cargarListaDepartamento();
            dpto = registro.getCampos().get(
                            CuentasControladorEnum.REGION.getValue()) == null
                                ? " "
                                : registro.getCampos().get(
                                                CuentasControladorEnum.REGION
                                                                .getValue())
                                                .toString();
            cargarListaCiudad();
            if ("U".equals(registro.getCampos().get(
                            CuentasControladorEnum.TIPOCUENTA.getValue()))) {
                contraseniaAux = registro.getCampos()
                                .get(CuentasControladorEnum.PASSWORD.getValue())
                                .toString();
            }
        }
        
    	try {
			 if ("U".equals(registro.getCampos()
	                     .get(CuentasControladorEnum.TIPOCUENTA.getValue()))) {
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.USUARIO.getName(), usuario);
	
				Registro rs;
				rs = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(CuentasControladorUrlEnum.URL47038.getValue()).getUrl(),
						param));
				if (rs != null) {
					if (SysmanFunciones.nvl(rs.getCampos().get("ES_ADMINISTRADOR"), "").equals("")) {
						esAdministrador = false;
					} else {
						esAdministrador = SysmanFunciones.nvl(rs.getCampos().get("ES_ADMINISTRADOR"), "").equals(true)
								? true
								: false;
					}
				}else {
					esAdministrador = false;
				}
			 }else {
				 esAdministrador = false;
			 }
		} catch (SystemException e) {
			   logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
		}

        // </CODIGO_DESARROLLADO>
    }

    private void consultarPoliticaContrasena() {

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CuentasControladorUrlEnum.URL1864001
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));

            if (rs != null) {
                cantidadNumeros = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("CANTIDAD_NUMEROS"),
                                                "0")
                                .toString());
                cantidadLetras = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("CANTIDAD_LETRAS"), "0")
                                .toString());
                cantidadSimbolos = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("CANTIDAD_SIMBOLOS"),
                                                "0")
                                .toString());
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        politicaContrasena = "Señor usuario la contraseña debe contener "
            + cantidadNumeros + " numeros, " + cantidadLetras + " letras y "
            + cantidadSimbolos + " simbolos";

    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if ("U".equals(registro.getCampos()
                        .get(CuentasControladorEnum.TIPOCUENTA.getValue()))
            && (("U".equals(registro.getCampos()
                            .get(CuentasControladorEnum.TIPOCUENTA.getValue()))
                && (registro.getCampos()
                                .get(CuentasControladorEnum.PASSWORD
                                                .getValue()) == null))
                || registro.getCampos()
                                .get(CuentasControladorEnum.PASSWORD.getValue())
                                .toString().isEmpty())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB813"));
            return false;

        }
        if (!"G".equals(registro.getCampos()
                        .get(CuentasControladorEnum.TIPOCUENTA.getValue()))) {
            registro.getCampos().put(
                            CuentasControladorEnum.NIVEL_GRUPO.getValue(),
                            null);
            registro.getCampos().put(
                            CuentasControladorEnum.APLICACION.getValue(), null);
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)
            && ((registro.getCampos().get(
                            CuentasControladorEnum.PASSWORD.getValue()) == null)
                || registro.getCampos()
                                .get(CuentasControladorEnum.PASSWORD.getValue())
                                .toString().isEmpty()
                || (registro.getCampos().get(CuentasControladorEnum.PASSWORD
                                .getValue()) == registroIni
                                                .get(CuentasControladorEnum.PASSWORD
                                                                .getValue())))) {
            registro.getCampos()
                            .remove(CuentasControladorEnum.PASSWORD.getValue());
            registro.getCampos().put(CuentasControladorEnum.PASSWORD.getValue(),
                            contraseniaAux);
        }
        
		manejaFechaLimite = (SysmanFunciones.toString(registro.getCampos().get("MAN_FECHA_LIMITE")).equals("true"))?true:false;

		if(manejaFechaLimite) {
			if(SysmanFunciones.nvl(registro.getCampos().get("FECHA_LIMITE_ING"),"").equals("")) {
				 JsfUtil.agregarMensajeError("Por favor, seleccione la fecha límite de ingreso");
				return false;
			}
		}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cambiarTipoCuenta() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CuentasControladorEnum.TIPOCUENTA.getValue())) {
            registro.getCampos().put(
                            CuentasControladorEnum.TIPOCUENTA.getValue(), "U");
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarFechaMax() {
    	registro.getCampos().put("FECHA_LIMITE_ING",null);
    }

    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        pais = registro.getCampos()
                        .get(CuentasControladorEnum.PAIS.getValue()) == null
                            ? " "
                            : registro.getCampos()
                                            .get(CuentasControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamento();
        dpto = null;
        registro.getCampos().put(CuentasControladorEnum.REGION.getValue(),
                        null);
        registro.getCampos().put(CuentasControladorEnum.CIUDAD.getValue(),
                        null);
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        dpto = registro.getCampos()
                        .get(CuentasControladorEnum.REGION.getValue()) == null
                            ? " "
                            : registro.getCampos()
                                            .get(CuentasControladorEnum.REGION
                                                            .getValue())
                                            .toString();
        registro.getCampos().put(CuentasControladorEnum.CIUDAD.getValue(),
                        null);
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaCompania() {
        // <CODIGO_DESARROLLADO>
        companiaDependencia = registroSubCuentaDependencias.getCampos()
                        .get(cCompania) == null ? " "
                            : registroSubCuentaDependencias.getCampos()
                                            .get(GeneralParameterEnum.COMPANIA
                                                            .getName())
                                            .toString();
        registroSubCuentaDependencias.getCampos()
                        .put(GeneralParameterEnum.RESPONSABLE.getName(), null);
        registroSubCuentaDependencias.getCampos()
                        .put(GeneralParameterEnum.DEPENDENCIA.getName(), null);
        registroSubCuentaDependencias.getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(), null);
        cargarListalistaDependencias();
        cargarListaResponsableAsociado();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaCompaniaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (indiceCuentadependencias == -1) {
            listaCuentadependencias.get(rowNum).getCampos().put(
                            GeneralParameterEnum.DEPENDENCIA.getName(), null);
            listaCuentadependencias.get(rowNum).getCampos().put(
                            GeneralParameterEnum.RESPONSABLE.getName(), null);
            listaCuentadependencias.get(rowNum).getCampos()
                            .put(GeneralParameterEnum.SUCURSAL.getName(), null);
        }
        companiaDependencia = listaCuentadependencias.get(rowNum).getCampos()
                        .get(cCompania) == null ? " "
                            : listaCuentadependencias.get(rowNum).getCampos()
                                            .get(GeneralParameterEnum.COMPANIA
                                                            .getName())
                                            .toString();
        cargarListalistaDependenciasE();
        cargarListaResponsableAsociadoE();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaDependenciasC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (indiceCuentadependencias == -1) {
            listaCuentadependencias.get(rowNum).getCampos().put(
                            GeneralParameterEnum.RESPONSABLE.getName(), null);
            listaCuentadependencias.get(rowNum).getCampos()
                            .put(GeneralParameterEnum.SUCURSAL.getName(), null);
        }
        dependencia = listaCuentadependencias.get(rowNum).getCampos()
                        .get(GeneralParameterEnum.DEPENDENCIA.getName()) == null
                            ? " "
                            : listaCuentadependencias.get(rowNum).getCampos()
                                            .get(GeneralParameterEnum.DEPENDENCIA
                                                            .getName())
                                            .toString();
        cargarListaResponsableAsociadoE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarResponsableAsociadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (indiceCuentadependencias == -1) {
            listaCuentadependencias.get(rowNum).getCampos().put(
                            GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        }
        indiceCuentadependencias = -1;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaMenusC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaCuentamenu.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.DESCRIPCION.getName(),
                        descMenu);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaFormulariosC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaSubcuentaformulario.getDatasource().get(rowNum % 10).getCampos()
                        .put(CuentasControladorEnum.NOMBRE_FORMULARIO
                                        .getValue(), nombreForm);
        listaSubcuentaformulario.getDatasource().get(rowNum % 10).getCampos()
                        .put(CuentasControladorEnum.DESCRIPCION_FORMULARIO
                                        .getValue(), descForm);
        listaSubcuentaformulario.getDatasource().get(rowNum % 10).getCampos()
                        .put("RUTA", rutaForm);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaGrupos() {
        // <CODIGO_DESARROLLADO>
        registroSubCuentaGrupos.getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        service.buscarEnLista(
                                        registroSubCuentaGrupos.getCampos().get(
                                                        CuentasControladorEnum.GRUPO
                                                                        .getValue())
                                                        .toString(),
                                        GeneralParameterEnum.CODIGO.getName(),
                                        GeneralParameterEnum.NOMBRE.getName(),
                                        listaListaGrupos));

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarListaGruposC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaCuentagrupos.get(rowNum).getCampos().put(
                        CuentasControladorEnum.APLICACION.getValue(),
                        service.buscarEnLista(
                                        listaCuentagrupos.get(rowNum)
                                                        .getCampos()
                                                        .get(CuentasControladorEnum.GRUPO
                                                                        .getValue())
                                                        .toString(),
                                        GeneralParameterEnum.CODIGO.getName(),
                                        CuentasControladorEnum.APLICACION
                                                        .getValue(),
                                        listaListaGrupos));
        listaCuentagrupos.get(rowNum).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        service.buscarEnLista(
                                        listaCuentagrupos.get(rowNum)
                                                        .getCampos()
                                                        .get(CuentasControladorEnum.GRUPO
                                                                        .getValue())
                                                        .toString(),
                                        GeneralParameterEnum.CODIGO.getName(),
                                        GeneralParameterEnum.NOMBRE.getName(),
                                        listaListaGrupos));

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite actualizar el indicador de visualizacion de
     * los menus respecto a una compania, grupo y menu padre.
     * 
     * @param indVer
     * <li>{true } para activar visualizacion.
     * <li>{false} para desactivar visualizacion.
     */
    private void asignarAccesoMenus(boolean indVer) {
        String grupo = SysmanFunciones
                        .nvl(registro.getCampos().get("CODIGO"), "").toString();

        int modulo = (int) registro.getCampos().get("APLICACION");

        try {
            ejbMenuk.asignarAccesoMenus(companiaMenu, grupo,
                            SysmanFunciones.nvl(codigoMenu, "").toString(),
                            usuario,
                            indVer, modulo);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4013"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarListaCuentamenu();
    }

    /**
     * @return the codigoMenu
     */
    public String getCodigoMenu() {
        return codigoMenu;
    }

    /**
     * @param codigoMenu
     * the codigoMenu to set
     */
    public void setCodigoMenu(String codigoMenu) {
        this.codigoMenu = codigoMenu;
    }

    public void cambiarlistaCompaniaPest() {
        cargarListaCuentamenu();
        cargarListaCodigoMenu();
    }

    public void cambiarListaCompaniaForm() {
        cargarListaSubcuentaformulario();
    }

    /**
     * Metodo ejecutado al cambiar el control Password
     * 
     * 
     */
    public void cambiarPassword() {
        // <CODIGO_DESARROLLADO>

        String contrasena = registro.getCampos().get("PASSWORD").toString();

        if (!SysmanFunciones.validarContrasena(contrasena, cantidadNumeros,
                        cantidadLetras, cantidadSimbolos)) {
            registro.getCampos().put("PASSWORD", "");

            JsfUtil.agregarMensajeAlerta(
                            "La contraseña ingresada no cumple con las politicas de privacidad");
        }
        else {
            registro.getCampos().put("FECHA_ACTCONTRASENA", new Date());
        }

        // </CODIGO_DESARROLLADO>
    } 
    
    public void cambiarListaAplicacionForm() {
    	try {
    		insertarAccesos();
			cargarListaSubmovimientosacceso();
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
    public List<Registro> getListaListaApliaciones() {
        return listaListaApliaciones;
    }

    public void setListaListaApliaciones(List<Registro> listalistaApliaciones) {
        this.listaListaApliaciones = listalistaApliaciones;
    }

    public List<Registro> getListaListaGeneros() {
        return listaListaGeneros;
    }

    public void setListaListaGeneros(List<Registro> listalistaGeneros) {
        this.listaListaGeneros = listalistaGeneros;
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

    public List<Registro> getListaListaGrupos() {
        return listaListaGrupos;
    }

    public void setListaListaGrupos(List<Registro> listalistaGrupos) {
        this.listaListaGrupos = listalistaGrupos;
    }

    public List<Registro> getListaListaCompania() {
        return listaListaCompania;
    }

    public void setListaListaCompania(List<Registro> listalistaCompania) {
        this.listaListaCompania = listalistaCompania;
    }

    public List<Registro> getListaCuentagrupos() {
        return listaCuentagrupos;
    }

    public void setListaCuentagrupos(List<Registro> listaCuentagrupos) {
        this.listaCuentagrupos = listaCuentagrupos;
    }

    public List<Registro> getListaCuentadependencias() {
        return listaCuentadependencias;
    }

    public void setListaCuentadependencias(
        List<Registro> listaCuentadependencias) {
        this.listaCuentadependencias = listaCuentadependencias;
    }

    public RegistroDataModelImpl getListaCuentamenu() {
        return listaCuentamenu;
    }

    public void setListaCuentamenu(RegistroDataModelImpl listaCuentamenu) {
        this.listaCuentamenu = listaCuentamenu;
    }

    public RegistroDataModelImpl getListaSubcuentaformulario() {
        return listaSubcuentaformulario;
    }

    public void setListaSubcuentaformulario(
        RegistroDataModelImpl listaSubcuentaformulario) {
        this.listaSubcuentaformulario = listaSubcuentaformulario;
    }

    public List<Registro> getListaCuentacompania() {
        return listaCuentacompania;
    }

    public void setListaCuentacompania(List<Registro> listaCuentacompania) {
        this.listaCuentacompania = listaCuentacompania;
    }

    public RegistroDataModelImpl getListaResponsableAsociado() {
        return listaResponsableAsociado;
    }

    public void setListaResponsableAsociado(
        RegistroDataModelImpl listaResponsableAsociado) {
        this.listaResponsableAsociado = listaResponsableAsociado;
    }

    public RegistroDataModelImpl getListaResponsableAsociadoE() {
        return listaResponsableAsociadoE;
    }

    public void setListaResponsableAsociadoE(
        RegistroDataModelImpl listaResponsableAsociadoE) {
        this.listaResponsableAsociadoE = listaResponsableAsociadoE;
    }

    public RegistroDataModelImpl getListaListaDependencias() {
        return listaListaDependencias;
    }

    public void setListaListaDependencias(
        RegistroDataModelImpl listalistaDependencias) {
        this.listaListaDependencias = listalistaDependencias;
    }

    public RegistroDataModelImpl getListaListaDependenciasE() {
        return listaListaDependenciasE;
    }

    public void setListaListaDependenciasE(
        RegistroDataModelImpl listalistaDependenciasE) {
        this.listaListaDependenciasE = listalistaDependenciasE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaListaMenus() {
        return listaListaMenus;
    }

    public void setListaListaMenus(RegistroDataModelImpl listalistaMenus) {
        this.listaListaMenus = listalistaMenus;
    }

    public RegistroDataModelImpl getListaListaMenusE() {
        return listaListaMenusE;
    }

    public void setListalistaMenusE(RegistroDataModelImpl listalistaMenusE) {
        this.listaListaMenusE = listalistaMenusE;
    }

    public RegistroDataModelImpl getListaListaFormularios() {
        return listaListaFormularios;
    }

    public void setListaListaFormularios(
        RegistroDataModelImpl listalistaFormularios) {
        this.listaListaFormularios = listalistaFormularios;
    }

    public RegistroDataModelImpl getListaListaFormulariosE() {
        return listaListaFormulariosE;
    }

    public void setListaListaFormulariosE(
        RegistroDataModelImpl listalistaFormulariosE) {
        this.listaListaFormulariosE = listalistaFormulariosE;
    }

    public Registro getRegistroSubCuentaGrupos() {
        return registroSubCuentaGrupos;
    }

    public void setRegistroSubCuentaGrupos(Registro registroSubCuentaGrupos) {
        this.registroSubCuentaGrupos = registroSubCuentaGrupos;
    }

    public Registro getRegistroSubCuentaDependencias() {
        return registroSubCuentaDependencias;
    }

    public void setRegistroSubCuentaDependencias(
        Registro registroSubCuentaDependencias) {
        this.registroSubCuentaDependencias = registroSubCuentaDependencias;
    }

    public Registro getRegistroSubCuentaMenu() {
        return registroSubCuentaMenu;
    }

    public void setRegistroSubCuentaMenu(Registro registroSubCuentaMenu) {
        this.registroSubCuentaMenu = registroSubCuentaMenu;
    }

    public Registro getRegistroSubSubCuentaFormulario() {
        return registroSubSubCuentaFormulario;
    }

    public void setRegistroSubSubCuentaFormulario(
        Registro registroSubSubCuentaFormulario) {
        this.registroSubSubCuentaFormulario = registroSubSubCuentaFormulario;
    }

    public Registro getRegistroSubCuentaCompania() {
        return registroSubCuentaCompania;
    }

    public void setRegistroSubCuentaCompania(
        Registro registroSubCuentaCompania) {
        this.registroSubCuentaCompania = registroSubCuentaCompania;
    }

    public String getCompaniaMenu() {
        return companiaMenu;
    }

    public void setCompaniaMenu(String companiaMenu) {
        this.companiaMenu = companiaMenu;
    }

    public String getCompaniaFormulario() {
        return companiaFormulario;
    }

    public void setCompaniaFormulario(String companiaFormulario) {
        this.companiaFormulario = companiaFormulario;
    }

    public int getIndiceCuentadependencias() {
        return indiceCuentadependencias;
    }

    public void setIndiceCuentadependencias(int indiceCuentadependencias) {
        this.indiceCuentadependencias = indiceCuentadependencias;
    }

    public List<Registro> getListalistaCompaniaPest() {
        return listalistaCompaniaPest;
    }

    public void setListalistaCompaniaPest(
        List<Registro> listalistaCompaniaPest) {
        this.listalistaCompaniaPest = listalistaCompaniaPest;
    }

    public List<Registro> getListaListaCompaniaForm() {
        return listaListaCompaniaForm;
    }

    public void setListaListaCompaniaForm(
        List<Registro> listalistaCompaniaForm) {
        this.listaListaCompaniaForm = listalistaCompaniaForm;
    }

    /**
     * @return the listaCodigoMenu
     */
    public List<Registro> getListaCodigoMenu() {
        return listaCodigoMenu;
    }

    /**
     * @param listaCodigoMenu
     * the listaCodigoMenu to set
     */
    public void setListaCodigoMenu(List<Registro> listaCodigoMenu) {
        this.listaCodigoMenu = listaCodigoMenu;
    }

    public RegistroDataModelImpl getListanitTercero() {
        return listanitTercero;
    }

    public void setListanitTercero(RegistroDataModelImpl listanitTercero) {
        this.listanitTercero = listanitTercero;
    }

    public String getPoliticaContrasena() {
        return politicaContrasena;
    }

    public void setPoliticaContrasena(String politicaContrasena) {
        this.politicaContrasena = politicaContrasena;
    }

	public String getAplicacionFormulario() {
		return aplicacionFormulario;
	}

	public void setAplicacionFormulario(String aplicacionFormulario) {
		this.aplicacionFormulario = aplicacionFormulario;
	}

	public List<Registro> getListaListaAplicacionForm() {
		return listaListaAplicacionForm;
	}

	public void setListaListaAplicacionForm(List<Registro> listaListaAplicacionForm) {
		this.listaListaAplicacionForm = listaListaAplicacionForm;
	}

	public Registro getRegistroSubSubMovimientosAcceso() {
		return registroSubSubMovimientosAcceso;
	}

	public void setRegistroSubSubMovimientosAcceso(Registro registroSubSubMovimientosAcceso) {
		this.registroSubSubMovimientosAcceso = registroSubSubMovimientosAcceso;
	}

	public RegistroDataModelImpl getListaSubmovimientosacceso() {
		return listaSubmovimientosacceso;
	}

	public void setListaSubmovimientosacceso(RegistroDataModelImpl listaSubmovimientosacceso) {
		this.listaSubmovimientosacceso = listaSubmovimientosacceso;
	}

	/**
	 * @return the esAdministrador
	 */
	public boolean isEsAdministrador() {
		return esAdministrador;
	}

	/**
	 * @param esAdministrador the esAdministrador to set
	 */
	public void setEsAdministrador(boolean esAdministrador) {
		this.esAdministrador = esAdministrador;
	}
	
	

}
