package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.SubbpproyectoplanindicativosControladorEnum;
import com.sysman.bancoproyectos.enums.SubbpproyectoplanindicativosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 09/09/2015
 * 
 * @author jcrodriguez,Refactoring y depuracion del controlador
 * @version 2, 27/09/2017
 */
@ManagedBean
@ViewScoped
public class SubbpproyectoplanindicativosControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String moduloBancos;
    private String codigoProyecto;
    private Map<String, Object> ridProyecto;
    private String nivel;
    private String programa;
    private String vigencia;
    private String etiquetaNivel;
    private String nombrePrograma;
    private String nombreNivel;
    private RegistroDataModelImpl listaNivel;
    private RegistroDataModelImpl listanivelE;
    private RegistroDataModelImpl listaPrograma;
    private RegistroDataModelImpl listaProgramaE;
    private RegistroDataModelImpl listaIdPlanP;
    private RegistroDataModelImpl listaIdPlanPE;
    private RegistroDataModelImpl listaComponente;
    private RegistroDataModelImpl listaComponenteE;
    private RegistroDataModelImpl listaActividad;
    private RegistroDataModelImpl listaActividadE;
    private String auxiliar;
    private List<Registro> listaVigencia;
    private List<Registro> listaVigenciaMetaP;
    private List<Registro> listaTipoComponente;
    private String tipoComponente;
    private String componente;
    private String vigenciaMetaP;
    private String vigenciaPlanP;
    private String rid;
    private int indice;
    private String anoIni;
    private String anoFin;
    private String menuActual;
    private boolean muestraRegistro;
    private String proyectoMonitor;
    private String dependenciaMonitor;
    private String vigenciaMonitor;
    private String estadoMonitor;
    private String idDependenciaMonitor;
    private boolean primeraEdicion;
    private String accion;
    private final Map<String, Object> parametrosEntrada;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * SubbpproyectoplanindicativosControlador
     */
    public SubbpproyectoplanindicativosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        moduloBancos = SessionUtil.getModulo();
        parametrosEntrada = SessionUtil.getFlash();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBBPPROYECTOPLANINDICATIVOS_CONTROLADOR.getCodigo();
            validarPermisos();
            menuActual = SessionUtil.getMenuActual();
            validarMenu();

            if (parametrosEntrada != null)
            {
                codigoProyecto = validarCamposCadena(parametrosEntrada, "codigoProyecto");
                if (codigoProyecto == null)
                {
                    SessionUtil.redireccionar(new Direccionador(String.valueOf(GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR.getCodigo()),
                                    "/frmproyectos.sysman", parametrosEntrada));
                    return;
                }
                ridProyecto = (Map<String, Object>) parametrosEntrada
                                .get("ridProyecto");
                setAnoIni(validarCamposCadena(parametrosEntrada, "anoIni"));
                setAnoFin(validarCamposCadena(parametrosEntrada, "anoFin"));
                setProyectoMonitor(validarCamposCadena(parametrosEntrada, "proyectoMonitor"));
                setDependenciaMonitor(validarCamposCadena(parametrosEntrada, "dependenciaMonitor"));
                setVigenciaMonitor(validarCamposCadena(parametrosEntrada, "vigenciaMonitor"));
                setEstadoMonitor(validarCamposCadena(parametrosEntrada, "estadoMonitor"));
                setIdDependenciaMonitor(validarCamposCadena(parametrosEntrada, "idDependenciaMonitor"));
                accion = validarCamposCadena(parametrosEntrada, "accion");
                parametrosEntrada.put("rid", ridProyecto);
                parametrosEntrada.remove("codigoProyecto");
                parametrosEntrada.remove("ridProyecto");
            }
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(SubbpproyectoplanindicativosControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, moduloBancos, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    private String validarCamposCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void validarMenu()
    {
        switch (menuActual)
        {
        case "52020102":
        case "52020402":
            muestraRegistro = false;
            break;
        case "52020101":
            muestraRegistro = true;
            break;

        case "NULL":
            SessionUtil.redireccionarMenu();
            break;

        default:
            break;
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.BP_PROYECTO_PLAN_INDICATIVO;
        buscarLlave();
        vigencia = SubbpproyectoplanindicativosControladorEnum.TODAS.getValue();
        programa = "99999999999";
        reasignarOrigen();
        registro = new Registro();
        nivel = "0";
        nombreNivel = idioma.getString("TI_TODOS");
        etiquetaNivel = idioma.getString("TB_TB3662").replace("s$nombre$s", nombreNivel);
        nombrePrograma = idioma.getString("TI_TODOS").toUpperCase();
        cargarListaVigencia();
        cargarListaNivel();

        cargarListaVigenciaMetaP();
        cargarListaTIPOCOMPONENTE();
        cargarListaIdPlanP();
        cargarListaCOMPONENTE();
        cargarListaACTIVIDAD();
        cargarListaIdPlanPE();
        cargarListaCOMPONENTEE();
        cargarListaACTIVIDADE();
        abrirFormulario();
        cargarListaprograma();
    }

    @Override
    public void reasignarOrigen()
    {
        if (accion == null)
        {
            SessionUtil.redireccionarMenuPermisos();
            return;
        }
        if (ACCION_VER.equals(accion))
        {
            muestraRegistro = false;
        }
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        parametrosListado.put(SubbpproyectoplanindicativosControladorEnum.PROGRAMA.getValue(), programa);
    }

    public void cargarListaVigencia()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        try
        {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubbpproyectoplanindicativosControladorUrlEnum.URL1023
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaVigenciaMetaP()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaVigenciaMetaP = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubbpproyectoplanindicativosControladorUrlEnum.URL1025
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTIPOCOMPONENTE()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.VIGENCIAMETAP.getValue(), vigenciaMetaP);
        param.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        try
        {
            listaTipoComponente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubbpproyectoplanindicativosControladorUrlEnum.URL1027
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaNivel()
    {
        if (SysmanFunciones.validarVariableVacio(vigencia))
        {
            vigencia = "0";
        }
        if (SubbpproyectoplanindicativosControladorEnum.TODAS.getValue().equalsIgnoreCase(vigencia))
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubbpproyectoplanindicativosControladorUrlEnum.URL1029
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaNivel = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            SubbpproyectoplanindicativosControladorEnum.DIGITOS.getValue());

        }
        else
        {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubbpproyectoplanindicativosControladorUrlEnum.URL1031
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
            listaNivel = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            SubbpproyectoplanindicativosControladorEnum.DIGITOS.getValue());

        }

    }

    public void cargarListaprograma()
    {
        if (SysmanFunciones.validarVariableVacio(nivel))
        {
            nivel = "";
        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpproyectoplanindicativosControladorUrlEnum.URL1033
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.VIGENCIAPLANP.getValue(), vigenciaPlanP);
        param.put(SubbpproyectoplanindicativosControladorEnum.NIVEL.getValue(), nivel);
        listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        SubbpproyectoplanindicativosControladorEnum.ID.getValue());

    }

    public void cargarListaIdPlanP()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpproyectoplanindicativosControladorUrlEnum.URL1039
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.VIGENCIAMETAP.getValue(), vigenciaMetaP);

        listaIdPlanP = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        SubbpproyectoplanindicativosControladorEnum.ID_PLAN.getValue());

    }

    public void cargarListaIdPlanPE()
    {
        listaIdPlanPE = listaIdPlanP;
    }

    public void cargarListaCOMPONENTE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpproyectoplanindicativosControladorUrlEnum.URL1041
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue(),
                        registro.getCampos().get(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue()));
        param.put(SubbpproyectoplanindicativosControladorEnum.VIGENCIAMETAP.getValue(), vigenciaMetaP);
        param.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        listaComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCOMPONENTEE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpproyectoplanindicativosControladorUrlEnum.URL1041
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue(), listaInicial == null ? null
            : listaInicial.getDatasource().get(indice % 10)
                            .getCampos()
                            .get(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue()));
        param.put(SubbpproyectoplanindicativosControladorEnum.VIGENCIAMETAP.getValue(), vigenciaMetaP);
        param.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        listaComponenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaACTIVIDAD()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpproyectoplanindicativosControladorUrlEnum.URL1043
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        param.put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(),
                        registro.getCampos().get(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue()));
        param.put(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue(),
                        registro.getCampos().get(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue()));

        listaActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.ACTIVIDAD.getName());

    }

    public void cargarListaACTIVIDADE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubbpproyectoplanindicativosControladorUrlEnum.URL1043
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpproyectoplanindicativosControladorEnum.CODIGOPROYECTO.getValue(), codigoProyecto);
        param.put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(), listaInicial == null ? null
            : listaInicial.getDatasource().get(indice % 10)
                            .getCampos().get(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue()));
        param.put(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue(), listaInicial == null ? null
            : listaInicial.getDatasource().get(indice % 10)
                            .getCampos()
                            .get(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue()));

        listaActividadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.ACTIVIDAD.getName());

    }

    public void oprimirFiltrar()
    {

        cargarListaVigenciaMetaP();
        cargarListaTIPOCOMPONENTE();
        cargarListaIdPlanP();
        cargarListaCOMPONENTE();
        cargarListaACTIVIDAD();
        reasignarOrigen();
        cargarForma();
    }

    public void cambiarnivel()
    {
        // METODO_NO_IMPLEMENTADO
        cargarListaprograma();
    }

    public void cambiarprograma()
    {
        // METODO_NO_IMPLEMENTADO
    }

    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        primeraEdicion = true;
    }

    public void cambiarVigencia()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(vigencia))
        {
            vigencia = "0";
        }
        cargarListaNivel();
        nivel = null;
        nombreNivel = null;
        etiquetaNivel = null;
        programa = null;
        nombrePrograma = null;
        cargarListaprograma();
    }

    public void cambiarVigenciaMetaP()
    {
        vigenciaMetaP = validarCamposCadena(registro.getCampos(), SubbpproyectoplanindicativosControladorEnum.VIGENCIA_META_P.getValue());
        cargarListaIdPlanP();
        cargarListaTIPOCOMPONENTE();
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.ID_PLAN_P.getValue(), null);
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.DESCRIPCION_META.getValue(), null);
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue(), null);
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(), null);
        registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), null);
    }

    public void cambiarVigenciaMetaPC(int rowNum)
    {
        vigenciaMetaP = validarCamposCadena(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos(), SubbpproyectoplanindicativosControladorEnum.VIGENCIA_META_P.getValue());
        if (!primeraEdicion)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubbpproyectoplanindicativosControladorEnum.ID_PLAN_P.getValue(), null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubbpproyectoplanindicativosControladorEnum.DESCRIPCION_META.getValue(), null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubbpproyectoplanindicativosControladorEnum.TIPOCOMPONENTE.getValue(), null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(), null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.ACTIVIDAD.getName(), null);
        }
        cargarListaIdPlanPE();
        cargarListaTIPOCOMPONENTE();
        cargarListaCOMPONENTEE();
        cargarListaACTIVIDADE();
    }

    public void cambiarIdPlanP()
    {
        // METODO_NO_IMPLEMENTADO
    }

    public void cambiarIdPlanPC(int rowNum)
    {
        // METODO_NO_IMPLEMENTADO
    }

    public void cambiarTipoComponente()
    {
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(), null);
        registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(), null);
        cargarListaCOMPONENTE();
    }

    public void cambiarTipoComponenteC(int rowNum)
    {
        if (!primeraEdicion)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(), null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.ACTIVIDAD.getName(), null);
        }
        cargarListaCOMPONENTEE();
    }

    public void cambiarComponenteC(int rowNum)
    {
        if (!primeraEdicion)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.ACTIVIDAD.getName(), null);
        }
        cargarListaACTIVIDADE();
    }

    public void cambiarActividadC(int rowNum)
    {
        primeraEdicion = false;
    }

    public void seleccionarFilaNivel(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nivel = "".equals(validarCamposCadena(registroAux.getCampos(), SubbpproyectoplanindicativosControladorEnum.DIGITOS.getValue())) ? ""
            : String.valueOf(Integer.parseInt(
                            validarCamposCadena(registroAux.getCampos(), SubbpproyectoplanindicativosControladorEnum.DIGITOS.getValue())));
        nombreNivel = validarCamposCadena(registroAux.getCampos(), GeneralParameterEnum.DESCRIPCION.getName());
        etiquetaNivel = idioma.getString("TB_TB3662").replace("s$nombre$s", nombreNivel);
        programa = null;
        nombrePrograma = null;
        cargarListaprograma();

    }

    public void seleccionarFilaPrograma(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        programa = validarCamposCadena(registroAux.getCampos(), SubbpproyectoplanindicativosControladorEnum.ID.getValue());
        nombrePrograma = validarCamposCadena(registroAux.getCampos(), GeneralParameterEnum.DESCRIPCION.getName());
    }

    public void seleccionarFilaIdPlanP(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.ID_PLAN_P.getValue(),
                        registroAux.getCampos().get(SubbpproyectoplanindicativosControladorEnum.ID_PLAN.getValue()));
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.DESCRIPCION_META.getValue(),
                        registroAux.getCampos().get(SubbpproyectoplanindicativosControladorEnum.DESCRIPCION_AUX.getValue()));
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.VIGENCIA_PLAN_P.getValue(),
                        registroAux.getCampos().get(SubbpproyectoplanindicativosControladorEnum.VIGENCIA_PLAN.getValue()));
    }

    public void seleccionarFilaIdPlanPE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCamposCadena(registroAux.getCampos(), SubbpproyectoplanindicativosControladorEnum.ID_PLAN.getValue());
        listaInicial.getDatasource().get(indice % 10).getCampos().put(
                        SubbpproyectoplanindicativosControladorEnum.DESCRIPCION_META.getValue(),
                        registroAux.getCampos().get(SubbpproyectoplanindicativosControladorEnum.DESCRIPCION_AUX.getValue()));
        listaInicial.getDatasource().get(indice % 10).getCampos().put(
                        SubbpproyectoplanindicativosControladorEnum.VIGENCIA_PLAN_P.getValue(),
                        registroAux.getCampos().get(SubbpproyectoplanindicativosControladorEnum.VIGENCIA_PLAN.getValue()));
    }

    public void seleccionarFilaComponente(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.COMPONENTE.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        cargarListaACTIVIDAD();
    }

    public void seleccionarFilaComponenteE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCamposCadena(registroAux.getCampos(), GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaActividad(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()));
    }

    public void seleccionarFilaActividadE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCamposCadena(registroAux.getCampos(), GeneralParameterEnum.ACTIVIDAD.getName());
    }

    @Override
    public void abrirFormulario()
    {
        vigenciaPlanP = getParametro("VIGENCIA GUBERNAMENTAL ACTUAL", false);
        if (vigenciaPlanP == null)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2443"));
            SessionUtil.cleanFlash();
        }

    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(SubbpproyectoplanindicativosControladorEnum.PROYECTO.getValue(), codigoProyecto);
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        recargarCombos();
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        registro.getCampos().remove(GeneralParameterEnum.VIGENCIA.getName());
        registro.getCampos().remove(SubbpproyectoplanindicativosControladorEnum.NOMBRETIPOCOMPONENTE.getValue());
        registro.getCampos().remove(SubbpproyectoplanindicativosControladorEnum.NOMBRECOMPONENTE.getValue());
        registro.getCampos().remove(SubbpproyectoplanindicativosControladorEnum.NOMBREACTIVIDAD.getValue());
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        recargarCombos();
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        recargarCombos();
        return true;
    }

    public void ejecutarrcCerrar()
    {
        SessionUtil.setFlash(parametrosEntrada);
        SessionUtil.redireccionar("/frmproyectos.sysman");
    }

    @Override
    public void removerCombos()
    {
        // heredado del bean base
    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
    }

    private void recargarCombos()
    {
        cargarListaVigencia();
        cargarListaNivel();
        cargarListaprograma();
        cargarListaVigenciaMetaP();
        cargarListaTIPOCOMPONENTE();
        cargarListaIdPlanP();
        cargarListaCOMPONENTE();
        cargarListaACTIVIDAD();
        cargarListaIdPlanPE();
        cargarListaCOMPONENTEE();
        cargarListaACTIVIDADE();
        reasignarOrigen();
        cargarForma();
    }

    public String getAnoFin()
    {
        return anoFin;
    }

    public void setAnoFin(String anoFin)
    {
        this.anoFin = anoFin;
    }

    public String getProyectoMonitor()
    {
        return proyectoMonitor;
    }

    public void setProyectoMonitor(String proyectoMonitor)
    {
        this.proyectoMonitor = proyectoMonitor;
    }

    public String getDependenciaMonitor()
    {
        return dependenciaMonitor;
    }

    public void setDependenciaMonitor(String dependenciaMonitor)
    {
        this.dependenciaMonitor = dependenciaMonitor;
    }

    public String getVigenciaMonitor()
    {
        return vigenciaMonitor;
    }

    public void setVigenciaMonitor(String vigenciaMonitor)
    {
        this.vigenciaMonitor = vigenciaMonitor;
    }

    public String getEstadoMonitor()
    {
        return estadoMonitor;
    }

    public void setEstadoMonitor(String estadoMonitor)
    {
        this.estadoMonitor = estadoMonitor;
    }

    public String getIdDependenciaMonitor()
    {
        return idDependenciaMonitor;
    }

    public void setIdDependenciaMonitor(String idDependenciaMonitor)
    {
        this.idDependenciaMonitor = idDependenciaMonitor;
    }

    public String getAnoIni()
    {
        return anoIni;
    }

    public void setAnoIni(String anoIni)
    {
        this.anoIni = anoIni;
    }

    public String getRid()
    {
        return rid;
    }

    public void setRid(String rid)
    {
        this.rid = rid;
    }

    public String getTipoComponente()
    {
        return tipoComponente;
    }

    public void setTipoComponente(String tipoComponente)
    {
        this.tipoComponente = tipoComponente;
    }

    public String getComponente()
    {
        return componente;
    }

    public void setComponente(String componente)
    {
        this.componente = componente;
    }

    public String getVigenciaMetaP()
    {
        return vigenciaMetaP;
    }

    public void setVigenciaMetaP(String vigenciaMetaP)
    {
        this.vigenciaMetaP = vigenciaMetaP;
    }

    public String getVigenciaPlanP()
    {
        return vigenciaPlanP;
    }

    public void setVigenciaPlanP(String vigenciaPlanP)
    {
        this.vigenciaPlanP = vigenciaPlanP;
    }

    public List<Registro> getListaVigencia()
    {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia)
    {
        this.listaVigencia = listaVigencia;
    }

    public List<Registro> getListaVigenciaMetaP()
    {
        return listaVigenciaMetaP;
    }

    public void setListaVigenciaMetaP(List<Registro> listaVigenciaMetaP)
    {
        this.listaVigenciaMetaP = listaVigenciaMetaP;
    }

    public List<Registro> getListaTipoComponente()
    {
        return listaTipoComponente;
    }

    public void setListaTipoComponente(List<Registro> listaTipoComponente)
    {
        this.listaTipoComponente = listaTipoComponente;
    }

    public String getCodigoProyecto()
    {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto)
    {
        this.codigoProyecto = codigoProyecto;
    }

    public Map<String, Object> getRidProyecto()
    {
        return ridProyecto;
    }

    public void setRidProyecto(Map<String, Object> ridProyecto)
    {
        this.ridProyecto = ridProyecto;
    }

    public RegistroDataModelImpl getListaNivel()
    {
        return listaNivel;
    }

    public void setListaNivel(RegistroDataModelImpl listanivel)
    {
        this.listaNivel = listanivel;
    }

    public RegistroDataModelImpl getListanivelE()
    {
        return listanivelE;
    }

    public void setListanivelE(RegistroDataModelImpl listanivelE)
    {
        this.listanivelE = listanivelE;
    }

    public RegistroDataModelImpl getListaPrograma()
    {
        return listaPrograma;
    }

    public void setListaPrograma(RegistroDataModelImpl listaprograma)
    {
        this.listaPrograma = listaprograma;
    }

    public RegistroDataModelImpl getListaProgramaE()
    {
        return listaProgramaE;
    }

    public void setListaprogramaE(RegistroDataModelImpl listaprogramaE)
    {
        this.listaProgramaE = listaprogramaE;
    }

    public RegistroDataModelImpl getListaIdPlanP()
    {
        return listaIdPlanP;
    }

    public void setListaIdPlanP(RegistroDataModelImpl listaIdPlanP)
    {
        this.listaIdPlanP = listaIdPlanP;
    }

    public RegistroDataModelImpl getListaIdPlanPE()
    {
        return listaIdPlanPE;
    }

    public void setListaIdPlanPE(RegistroDataModelImpl listaIdPlanPE)
    {
        this.listaIdPlanPE = listaIdPlanPE;
    }

    public RegistroDataModelImpl getListaComponente()
    {
        return listaComponente;
    }

    public void setListaComponente(RegistroDataModelImpl listaComponente)
    {
        this.listaComponente = listaComponente;
    }

    public RegistroDataModelImpl getListaComponenteE()
    {
        return listaComponenteE;
    }

    public void setListaComponenteE(RegistroDataModelImpl listaComponenteE)
    {
        this.listaComponenteE = listaComponenteE;
    }

    public RegistroDataModelImpl getListaActividad()
    {
        return listaActividad;
    }

    public void setListaActividad(RegistroDataModelImpl listaActividad)
    {
        this.listaActividad = listaActividad;
    }

    public RegistroDataModelImpl getListaActividadE()
    {
        return listaActividadE;
    }

    public void setListaActividadE(RegistroDataModelImpl listaActividadE)
    {
        this.listaActividadE = listaActividadE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getNivel()
    {
        return nivel;
    }

    public void setNivel(String nivel)
    {
        this.nivel = nivel;
    }

    public String getPrograma()
    {
        return programa;
    }

    public void setPrograma(String programa)
    {
        this.programa = programa;
    }

    public String getVigencia()
    {
        return vigencia;
    }

    public void setVigencia(String vigencia)
    {
        this.vigencia = vigencia;
    }

    public String getEtiquetaNivel()
    {
        return etiquetaNivel;
    }

    public void setEtiquetaNivel(String etiquetaNivel)
    {
        this.etiquetaNivel = etiquetaNivel;
    }

    public String getNombrePrograma()
    {
        return nombrePrograma;
    }

    public void setNombrePrograma(String nombrePrograma)
    {
        this.nombrePrograma = nombrePrograma;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public boolean isMuestraRegistro()
    {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro)
    {
        this.muestraRegistro = muestraRegistro;
    }

}