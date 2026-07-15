package com.sysman.general;

import com.sysman.almacen.ejb.EjbAlmacenCuatroGeneralRemote;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.POrdenDeSuministroControladorEnum;
import com.sysman.general.enums.POrdenDeSuministroControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodrigueza
 * @version 1, 17/10/2015
 * @author yrojas
 * @version 2, 07/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 * @author ybecerra
 * @version 3, 22/12/2017 Se quitaron los metodos
 * verificarPlanDeCompras,comprobarPlanCompras, debido a que no se
 * necesitan al momento de crear un registro
 */
@ManagedBean
@ViewScoped
public class POrdenDeSuministroControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;

    /** Constante a nivel de clase que aloja el valor CANTIDADTT */
    private final String cantidadTt;

    /** Constante a nivel de clase que aloja el valor CODIGOCUBS */
    private final String codigoCubs;

    /** Constante a nivel de clase que aloja el valor DISPONIBLE */
    private final String disponible;

    /** Constante a nivel de clase que aloja el valor EXISTENCIAALM */
    private final String existenciaAlm;

    /**
     * Constante a nivel de clase que aloja el valor
     * MSM_TRANS_INTERRUMPIDA
     */
    private final String mensaje1;

    /**
     * Constante a nivel de clase que aloja el valor
     * D_ORDENDESUMINISTRO
     */
    private final String dOrdenSuministro;

    /**
     * Constante a nivel de clase que aloja el valor
     * CANTIDADPORENTREGAR
     */
    private final String cantidadEntregar;

    /**
     * Constante a nivel de clase que aloja el valor CANTIDADAPROBADA
     */
    private final String cantidadApropiada;

    private Registro registroSub;
    private RegistroDataModelImpl listaauxiliarCombo;
    private List<Registro> listaDependenciaDos;
    private List<Registro> listaSubordensuministro;
    private List<Registro> listaClaseBodega;
    private List<Registro> listaDependencia;
    private List<Registro> listaResponsableAux;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private RegistroDataModelImpl listaResponsable;
    private StreamedContent archivoDescarga;
    private String auxiliar;
    private String nombreAuxiliar;
    private String responsable;
    private boolean cargar;
    private boolean bloqueado;
    private boolean registrado = true;
    private int anio;
    private int numeroOrden;
    private int indiceSubordensuministro;
    private double cantidadAprobadaAnterior;
    private String WIDTHESTADO;
    /**
     * Esta variable almacena el valor de si maneja ono control de
     * requisiciones
     */
    private String controlRequisiciones;

    @EJB
    private EjbGeneralesRemote ejbGenerales;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenCuatroGeneralRemote ejbAlmacenComCuadtro;

	private Map<String, Object> parametroswf;

	private boolean habilitaApro;

    @SuppressWarnings("unchecked")
	public POrdenDeSuministroControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        cantidadApropiada = "CANTIDADAPROBADA";
        cantidadEntregar = "CANTIDADPORENTREGAR";
        cantidadTt = "CANTIDADTT";
        codigoCubs = "CODIGOCUBS";
        disponible = "DISPONIBLE";
        dOrdenSuministro = "D_ORDENDESUMINISTRO";
        mensaje1 = "MSM_TRANS_INTERRUMPIDA";
        existenciaAlm = "EXISTENCIAALM";

        try
        {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "10");
        	}
        	numFormulario = GeneralCodigoFormaEnum.P_ORDEN_DE_SUMINISTRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());

        }
        catch (Exception ex)
        {
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.ORDENDESUMINISTRO;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        cargarPorDependencia();
    }

    @Override
    public void iniciarListas()
    {
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaResponsable();
        cargarListaDependencia();
        cargarListaDependenciaDos();
        cargarListaClaseBodega();
        cargarListaauxiliarCombo();

    }

    @Override
    public void iniciarListasSub()
    {
        cargarListaSubordensuministro();
        anio = SysmanFunciones.getParteFecha(
                        (Date) registro.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName()),
                        Calendar.YEAR);
        cargarNombreAuxiliar();
        cargarListaauxiliarCombo();
    }

    @Override
    public void iniciarListasSubNulo()
    {
        listaSubordensuministro = null;
    }

    public void cargarListaSubordensuministro()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ORDEN.getName(),
                            registro.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()));

            listaSubordensuministro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.D_ORDENDESUMINISTRO
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            dOrdenSuministro));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    public void cargarListaDependenciaDos()
    {
        listaDependenciaDos = listaDependencia;
    }

    public void cargarListaElemento()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        POrdenDeSuministroControladorUrlEnum.URL9024
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaElementoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        POrdenDeSuministroControladorUrlEnum.URL10031
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaDependencia()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL9994
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaResponsable()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        POrdenDeSuministroControladorUrlEnum.URL11617
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.RESPONSABLE.getName());
    }

    public void cargarListaauxiliarCombo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        POrdenDeSuministroControladorUrlEnum.URL13165
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaauxiliarCombo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaClaseBodega()
    {
        try
        {
            listaClaseBodega = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL11750
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubSubordensuministro()
    {
        try
        {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(
                            GeneralParameterEnum.ORDENDESUMINISTRO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()));
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos()
                            .remove(GeneralParameterEnum.UNIDAD.getName());
            
            registroSub.getCampos().remove("NOMBRELARGO");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_ORDENDESUMINISTRO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubordensuministro();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex)
        {
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
            inicializarValoresSub();
        }
    }

    public void editarRegSubSubordensuministro(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(GeneralParameterEnum.UNIDAD.getName());
            reg.getCampos().remove("NOMBREDEPENDENCIA");
            reg.getCampos().remove("NOMBRELARGO");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_ORDENDESUMINISTRO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSubordensuministro();
        }
    }

    public void eliminarRegSubSubordensuministro(Registro reg)
    {
        try
        {
            if ((boolean) reg.getCampos().get("IND_REG"))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2251"));
                return;
            }

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_ORDENDESUMINISTRO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubordensuministro();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubordensuministro()
    {
        cargarListaSubordensuministro();
    }

    public void cambiarFecha()
    {
        anio = SysmanFunciones.getParteFecha(
                        (Date) registro.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName()),
                        Calendar.YEAR);
        cargarListaauxiliarCombo();
        Map<String, Object> registros = new HashMap<>();
        registros.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.AUXILIAR
                                        .getName()));

        Registro regAux;
        try
        {
            regAux = listaauxiliarCombo.getRegistroUnico(registros);
            if (regAux == null)
            {
                registro.getCampos().put(
                                GeneralParameterEnum.AUXILIAR.getName(), null);
                nombreAuxiliar = "";
            }
            else
            {
                cargarNombreAuxiliar();
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    public void oprimircmdCambiar(ActionEvent ac)
    {
        // Codigo
    }

    public void oprimircmdPantalla()
    {
        generarReporte(ReportesBean.FORMATOS.PDF);
    }

    public void oprimircmdExcel()
    {
        generarReporte(ReportesBean.FORMATOS.EXCEL);
    }

    public void cambiarDependencia()
    {
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), "");
        responsable = "";
        cargarListaResponsable();
    }

    public void oprimircmdRegistrar()
    {
        String claseBodegaAlmacen = SysmanConstantes.CONS_CLASE_BODEGA_ALM;
        String claseBodega = registro.getCampos().get("CLASE_BODEGA")
                        .toString();
        int orden = Integer
                        .parseInt(registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName())
                                        .toString());

        try
        {
            ejbGenerales.registrarSolicitud(compania, orden, "CANTRESERVADA",
                            claseBodegaAlmacen, claseBodega);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName()));

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL526
                                                                            .getValue())
                                            .getUrl(), param));

            if ((boolean) rs.getCampos()
                            .get(POrdenDeSuministroControladorEnum.IND_REG
                                            .getValue()))
            {
                registrado = false;
            }
            else
            {
                registrado = true;

            }
            cargarListaSubordensuministro();
            if(habilitaApro) {
        		registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
        				"SOLICITADO");
        	}
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2252"));
            agregarRegistroNuevo(false);
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(mensaje1)
                                + ex.getMessage());
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void aceptarinputBox()
    {
        // Recibe el nuevo n�mero de la orden

        long numeroAnterior = Integer
                        .parseInt(registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName())
                                        .toString());
        boolean salida = false;

        try
        {
            salida = ejbGenerales.cambiarRequisicion(compania, numeroOrden,
                            numeroAnterior, SessionUtil.getUser().getCodigo(),
                            (Date) registro.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName()),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.DEPENDENCIA
                                                            .getName())
                                            .toString(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TERCERO
                                                            .getName())
                                            .toString(),
                            registro
                                            .getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString(),
                            new BigDecimal(registro
                                            .getCampos()
                                            .get("VALORESTIMADO")
                                            .toString()),
                            registro
                                            .getCampos()
                                            .get(GeneralParameterEnum.DESCRIPCION
                                                            .getName())
                                            .toString(),
                            registro
                                            .getCampos()
                                            .get(GeneralParameterEnum.OBSERVACIONES
                                                            .getName())
                                            .toString(),
                            Long.parseLong(registro
                                            .getCampos().get("PLAZO")
                                            .toString()),
                            registro
                                            .getCampos()
                                            .get("UNIDAD_TIEMPO")
                                            .toString(),
                            registro
                                            .getCampos()
                                            .get("PERIODICIDAD")
                                            .toString(),
                            Long.parseLong(registro
                                            .getCampos()
                                            .get("NUMERO_ENTREGAS")
                                            .toString()),
                            registro
                                            .getCampos().get("CLASE_BODEGA")
                                            .toString(),
                            registro
                                            .getCampos()
                                            .get(GeneralParameterEnum.AUXILIAR
                                                            .getName())
                                            .toString());

            rid = registro.getLlave();
            rid.put("KEY_NUMERO", numeroOrden);
            cargarRegistro(rid, "m", -2);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2253"));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (!salida)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2254"));
        }
    }

    public void cambiarElementoC(int rowNum)
    {
        String codigoElemento = listaSubordensuministro.get(rowNum)
                        .getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName())
                        .toString();
        traerDatosElemento(codigoElemento, rowNum);
    }

    public void seleccionarFilaElemento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()));
        String codigoElemento = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName())
                        .toString();
        traerDatosElemento(codigoElemento, -1);
    }

    public void seleccionarFilaauxiliarCombo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreAuxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void traerDatosElemento(String codigoElemento, int rowNum)
    {
        if (codigoElemento == null)
        {
            return;
        }

        Registro reg;
        String existencia;
        String codigoCUBS;
        String unidad;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoElemento);

        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL20478
                                                                            .getValue())
                                            .getUrl(), param));

            existencia = reg.getCampos().get("EXISTENCIA").toString();
            codigoCUBS = reg.getCampos().get(codigoCubs) == null
                ? " " : reg.getCampos().get(codigoCubs).toString();
            unidad = reg.getCampos()
                            .get(GeneralParameterEnum.UNIDAD.getName()) == null
                                ? " "
                                : reg.getCampos()
                                                .get(GeneralParameterEnum.UNIDAD
                                                                .getName())
                                                .toString();

            if (rowNum >= 0)
            {
                listaSubordensuministro.get(rowNum).getCampos()
                                .put(existenciaAlm, existencia);
                listaSubordensuministro.get(rowNum).getCampos()
                                .put(codigoCubs, codigoCUBS);
                listaSubordensuministro.get(rowNum).getCampos().put(
                                GeneralParameterEnum.UNIDAD.getName(),
                                unidad);
                listaSubordensuministro.get(rowNum).getCampos().put("NOMBRELARGO",
                		reg.getCampos().get("NOMBRELARGO"));
                
            }
            else
            {
                registroSub.getCampos().put(existenciaAlm, existencia);
                registroSub.getCampos().put(codigoCubs, codigoCUBS);
                registroSub.getCampos().put(
                                GeneralParameterEnum.UNIDAD.getName(), unidad);
                registroSub.getCampos().put("NOMBRELARGO", reg.getCampos().get("NOMBRELARGO"));
                
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaElementoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName())
                        .toString();
    }

    public void seleccionarFilaResponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()));

        Map<String, Object> registros = new HashMap<>();
        registros.put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()));
        Registro regResponsable;
        try
        {
            regResponsable = listaResponsable.getRegistroUnico(registros);
            String sucursal = "";

            responsable = "";
            if (regResponsable != null)
            {
                sucursal = regResponsable.getCampos()
                                .get(GeneralParameterEnum.SUCURSAL.getName())
                                .toString();
                responsable = regResponsable.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();
            }
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario(){
    	habilitaApro = obtenerParametro("HABILITAR APROBACION DE SOLICITUDES DE SUMINISTRO", "NO").equals("SI");
        WIDTHESTADO = habilitaApro?"10%":"0%";
    }

    @Override
    public void cargarRegistro()
    {
        precargarRegistro();
        nombreAuxiliar = "";
        responsable = "";
        cargarListaResponsable();
        cargarListaauxiliarCombo();

        switch (accion)
        {
        case "i":
            cargarRegistroI();
            break;
        case "m":
            cargarNombreAuxiliar();
            cargarNombreResponsable();
            inicializarValoresSub();
            bloquearValorEstimado();
            break;
        case "v":
            bloqueado = true;
            break;
        default:
            break;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));

        Registro rs;
        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL526
                                                                            .getValue())
                                            .getUrl(), param));

            if ((boolean) rs.getCampos()
                            .get(POrdenDeSuministroControladorEnum.IND_REG
                                            .getValue()))
            {
                registrado = false;
            }
            else
            {
                registrado = true;
            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando la accion es i
     */
    private void cargarRegistroI()
    {
        inicializarValores();
        anio = SysmanFunciones.getParteFecha(
                        (Date) registro.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName()),
                        Calendar.YEAR);
        cargarListaauxiliarCombo();
        bloqueado = false;
    }

    private void bloquearValorEstimado()
    {
        bloqueado = false;
        Iterator<Registro> it = listaSubordensuministro.iterator();
        while (it.hasNext())
        {
            Registro reg = it.next();
            bloqueado = (boolean) reg.getCampos().get("IND_REG");
            if (bloqueado)
            {
                break;
            }
        }
    }

    public void inicializarValores()
    {
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        registro.getCampos().put("PERIODICIDAD", 0);
        registro.getCampos().put("UNIDAD_TIEMPO", "DIAS");
    }

    public void inicializarValoresSub()
    {
        registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        genConsecutivoItem());
        registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), 0);
        registroSub.getCampos().put(cantidadApropiada, 0);
        registroSub.getCampos().put(cantidadEntregar, 0);
        registroSub.getCampos().put(cantidadEntregar, 0);
        registroSub.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));
        registroSub.getCampos().put(cantidadTt, 0);
        registroSub.getCampos().put("NOMBRELARGO", "");
    }

    private long genConsecutivoItem()
    {
        long consecutivo = 0;
        int ordenDeSuministro = Integer
                        .parseInt(registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName())
                                        .toString());
        try
        {
            String[] cadena = { "COMPANIA = ''", compania,
                                "'' AND ORDENDESUMINISTRO = ",
                                Integer.toString(ordenDeSuministro), ""

            };

            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "D_ORDENDESUMINISTRO",
                            SysmanFunciones.concatenar(cadena),
                            GeneralParameterEnum.CODIGO.getName());

        }
        catch (SystemException ex)
        {
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return consecutivo;
    }

    /**
     * Este metodo obtiene el valor acumularo y valos maximo de un
     * empleado
     * 
     * @return
     */
    public int[] obtenerValorMaximo()
    {
        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.COMPANIA
                                                        .getName()));
        try
        {
            parametros.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.toString(registro
                                                            .getCampos()
                                                            .get(GeneralParameterEnum.FECHA
                                                                            .getName())))));
        }
        catch (ParseException e1)
        {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        parametros.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.TERCERO
                                                        .getName()));

        parametros.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        Registro rsValorAcum;
        Registro rsValorMax;
        int[] valores = new int[2];
        valores[0] = 0;
        valores[1] = 0;
        try
        {
            rsValorAcum = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "1060001")
                                            .getUrl(), parametros));

            rsValorMax = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "1060002")
                                            .getUrl(), parametros));

            valores[0] = Integer.parseInt(SysmanFunciones.toString(rsValorAcum
                            .getCampos()
                            .get("VLR_ACUMULADO")));

            valores[1] = Integer.parseInt(SysmanFunciones.toString(rsValorMax
                            .getCampos()
                            .get("VLR_MAX")));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valores;
    }
    
    public void ejecutarrcCerrar() {
    	try {
    		if (parametroswf != null) {
    			Map<String,Object> parametros = new TreeMap<>();
    			parametros.put("PR_ROWKEY",parametroswf.get("PR_ROWKEY"));

    			SessionUtil.removeSessionVarContainer("parametroswf");

    			Direccionador direccionador = new Direccionador();
    			direccionador.setNumForm(Integer.toString(
    					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo()));

    			direccionador.setParametros(parametros);
    			SessionUtil.redireccionarForma(direccionador,"35");
    		} else {
    			SessionUtil.redireccionar("/menu.sysman");
    		}
    	} catch (NamingException e) {
    		logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    @Override
    public boolean insertarAntes()
    {
        /**
         * Esta validacion se hace para garantizar que el monto por
         * requisicion que se agrege a
         */

    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
    			compania);
    	if(habilitaApro) {
    		registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
    				"POR SOLICITAR");
    	}
        controlRequisiciones = obtenerParametro(
                        "MANEJA CONTROL DE REQUISICIONES",
                        "");
        actualizarAntes();
        try
        {

            String codigo = String
                            .valueOf(ejbSysmanUtil.generarSiguienteConsecutivo(
                                            "ORDENDESUMINISTRO",
                                            "COMPANIA = ''" + compania + "''",
                                            GeneralParameterEnum.NUMERO
                                                            .getName()));

            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            codigo);
        }
        catch (SystemException ex)
        {
            Logger.getLogger(POrdenDeSuministroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        if (accion.equals(ACCION_INSERTAR))
        {
            if ("SI".equals(controlRequisiciones))
            {

                int ano = SysmanFunciones.ano((Date) registro.getCampos().get(
                                GeneralParameterEnum.FECHA
                                                .getName()));

                String tercero = SysmanFunciones.toString(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.TERCERO
                                                                .getName()));

                String dependencia = SysmanFunciones.toString(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.DEPENDENCIA
                                                                .getName()));

                String sucursal = SysmanFunciones.toString(
                                registro.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName()));

                String nuevoValorEstimado = SysmanFunciones.toString(
                                registro.getCampos().get("VALORESTIMADO"));

                String numero = SysmanFunciones.toString(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()));

                int resultado = 0;
                try
                {
                    resultado = ejbAlmacenComCuadtro.validarRequisicion(
                                    compania,
                                    dependencia, tercero, ano, sucursal,
                                    "I", nuevoValorEstimado, numero);
                }
                catch (SystemException e)
                {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

                if (resultado == 0)
                {
                    JsfUtil.agregarMensajeError(
                                    "El Valor Que Intenta Ingresar Supera El Monto De Requisicion Para El Tercero.");
                    return false;
                }
            }

        }

        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        controlRequisiciones = obtenerParametro(
                        "MANEJA CONTROL DE REQUISICIONES",
                        "");

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.AUXILIAR.getName()))
        {
            registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                            SysmanConstantes.CONS_AUXILIAR);
        }

        if (accion.equals(ACCION_MODIFICAR))
        {

            if ("SI".equals(controlRequisiciones))
            {

                int ano = SysmanFunciones.ano((Date) registro.getCampos().get(
                                GeneralParameterEnum.FECHA
                                                .getName()));

                String tercero = SysmanFunciones.toString(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.TERCERO
                                                                .getName()));

                String dependencia = SysmanFunciones.toString(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.DEPENDENCIA
                                                                .getName()));

                String sucursal = SysmanFunciones.toString(
                                registro.getCampos().get(
                                                GeneralParameterEnum.SUCURSAL
                                                                .getName()));

                String nuevoValorEstimado = SysmanFunciones.toString(
                                registro.getCampos().get("VALORESTIMADO"));

                String numero = SysmanFunciones.toString(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()));

                int resultado = 0;
                try
                {
                    resultado = ejbAlmacenComCuadtro.validarRequisicion(
                                    compania,
                                    dependencia, tercero, ano, sucursal,
                                    "M", nuevoValorEstimado, numero);
                }
                catch (SystemException e)
                {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

                if (resultado == 0)
                {
                    JsfUtil.agregarMensajeError(
                                    "El Valor Que Intenta Ingresar Supera El Monto De Requisicion Para El Tercero.");
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
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
        return true;
    }

    private void generarReporte(ReportesBean.FORMATOS formato) 
    {
    	try
        {
	        if ("i".equals(accion))
	        {
	            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2122"));
	            return;
	        }
        
	        String reporte= SysmanFunciones.nvlStr(
	     				ejbSysmanUtil.consultarParametro(
	     							compania, 
	     							"FORMATO REQUISICIONES GBR NARINO", 
	     							SessionUtil.getModulo(), 
	     							new Date(), 
	     							false), "000336IOrdenDeSuministro");
	        
	        String formatosUspec = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, 
     					"FORMATOS UNICOS USPEC", 
     					SessionUtil.getModulo(), 
     					new Date(), 
     					false), "NO");
    
	        HashMap<String, Object> reemplazos = new HashMap<>();
	        Map<String, Object> parametros = new HashMap<>();
	        
	        int ordenDeSuministro = Integer
	                        .parseInt(registro.getCampos().get(
	                                        GeneralParameterEnum.NUMERO.getName())
	                                        .toString());

	        reemplazos.put("ordendesuministro",
                        	Integer.toString(ordenDeSuministro));
	        
	        int modulo = Integer.parseInt(SessionUtil.getModulo());
	        String consulta = Reporteador.resuelveConsulta(reporte, modulo,reemplazos);
   
	        if ("SI".equals(formatosUspec)) {
	        	parametros.put("PR_FORMATOS_USPEC", true);
	        } else {
	        	parametros.put("PR_FORMATOS_USPEC", false);
	        }

	        parametros.put("PR_STRSQL", consulta);

	        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Carga el nombre del responsable.
     */
    private void cargarNombreResponsable()
    {
        Registro reg;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));
        param.put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL18002
                                                                            .getValue())
                                            .getUrl(), param));
            responsable = reg.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName()) == null
                                ? ""
                                : reg.getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName())
                                                .toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarNombreAuxiliar()
    {
        Registro reg;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.AUXILIAR
                                        .getName()));
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            POrdenDeSuministroControladorUrlEnum.URL21552
                                                                            .getValue())
                                            .getUrl(), param));
            nombreAuxiliar = reg.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName()) == null
                                ? ""
                                : reg.getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName())
                                                .toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarCantidad()
    {
        registroSub.getCampos().put(cantidadTt,
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.CANTIDAD
                                                        .getName()));
    }

    public void cambiarCantidadC(int rowNum)
    {
        listaSubordensuministro.get(rowNum).getCampos().put(
                        cantidadTt,
                        listaSubordensuministro.get(rowNum).getCampos()
                                        .get(GeneralParameterEnum.CANTIDAD
                                                        .getName()));
    }

    public void cambiarCantidadAprobada()
    {
        registroSub.getCampos().put(cantidadEntregar,
                        registroSub.getCampos().get(cantidadApropiada));
    }

    public void cambiarCantidadAprobadaC(int rowNum)
    {
        // <CODIGODESARROLLADO>
        // </CODIGODESARROLLADO>
    }

    public void activarEdicionSubordensuministro(Registro registro)
    {
        String valor = registro.getCampos()
                        .get(cantidadApropiada).toString();
        cantidadAprobadaAnterior = Double.valueOf(valor);
    }

    public boolean verificarAsociadoPc(Registro reg)
    {
        return (boolean) reg.getCampos().get("ASOCIADOPC");
    }

    public int asignarCantidadAprobada(int rowNum)
    {
        int cantidadAprobada;
        if (rowNum >= 0)
        {
            cantidadAprobada = Integer
                            .parseInt(listaSubordensuministro
                                            .get(rowNum).getCampos()
                                            .get(cantidadApropiada).toString());
        }
        else
        {
            cantidadAprobada = Integer.parseInt(registroSub
                            .getCampos().get(cantidadApropiada).toString());
        }
        return cantidadAprobada;
    }

    public String asignarElemento(int rowNum)
    {
        String elemento;

        if (rowNum >= 0)
        {
            elemento = listaSubordensuministro.get(rowNum)
                            .getCampos()
                            .get(GeneralParameterEnum.ELEMENTO.getName())
                            .toString();
        }
        else
        {
            elemento = registroSub.getCampos()
                            .get(GeneralParameterEnum.ELEMENTO.getName())
                            .toString();
        }

        return elemento;
    }

    public void asignarListaSubordensuministro(int rowNum)
    {
        if (rowNum >= 0)
        {
            listaSubordensuministro.get(rowNum).getCampos()
                            .put(cantidadApropiada, 0);
            listaSubordensuministro.get(rowNum).getCampos()
                            .put(cantidadEntregar, 0);
            listaSubordensuministro.get(rowNum).getCampos()
                            .put(existenciaAlm, 0);
        }
        else
        {
            registroSub.getCampos().put(cantidadApropiada, 0);
            registroSub.getCampos().put(cantidadEntregar,
                            0);
            registroSub.getCampos().put(existenciaAlm, 0);
        }
    }
    
    public void cargarPorDependencia() {
    	try {
    		String parametro = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, 					
    				"LISTAR REQUISICIONES POR DEPENDENCIA", 
    				SessionUtil.getModulo(), new Date(), false), "NO");
    		
    		if(parametro.equals("SI")) {
    			Map<String, Object> param = new HashMap<>();
    			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    			param.put(GeneralParameterEnum.CODIGO.getName(), SessionUtil.getUser().getCodigo());

    			Registro rsDependencia = RegistroConverter
    					.toRegistro(requestManager.get(
    							UrlServiceUtil.getInstance()
    							.getUrlServiceByUrlByEnumID(
    									POrdenDeSuministroControladorUrlEnum.URL52002.getValue())
    							.getUrl(),
    							param));

    			if(rsDependencia != null) {

    				String dependencia = SysmanFunciones.toString(rsDependencia.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

    				parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
    						dependencia);

    			}else {
    				
    				parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
    						"");
    				
    				JsfUtil.agregarMensajeInformativo("Debe configurar una dependencia para el usuario: "+ SessionUtil.getUser().getCodigo());
    			}
    			
    			urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
						POrdenDeSuministroControladorUrlEnum.URL109026.getValue());
    		}


    	} catch (SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    public List<Registro> getListaSubordensuministro()
    {
        return listaSubordensuministro;
    }

    public List<Registro> getListaDependenciaDos()
    {
        return listaDependenciaDos;
    }

    public void setListaDependenciaDos(List<Registro> listaDependenciaDos)
    {
        this.listaDependenciaDos = listaDependenciaDos;
    }

    public void setListaSubordensuministro(
        List<Registro> listaSubordensuministro)
    {
        this.listaSubordensuministro = listaSubordensuministro;
    }

    public RegistroDataModelImpl getListaElemento()
    {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento)
    {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE()
    {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE)
    {
        this.listaElementoE = listaElementoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public List<Registro> getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(List<Registro> listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaResponsable()
    {
        return listaResponsable;
    }

    public void setListaResponsable(RegistroDataModelImpl listaResponsable)
    {
        this.listaResponsable = listaResponsable;
    }

    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public boolean isBloqueado()
    {
        return bloqueado;
    }

    public boolean isRegistrado()
    {
        return registrado;
    }

    public void setRegistrado(boolean registrado)
    {
        this.registrado = registrado;
    }

    public void setBloqueado(boolean bloqueado)
    {
        this.bloqueado = bloqueado;
    }

    public int getNumeroOrden()
    {
        return numeroOrden;
    }

    public void setNumeroOrden(int numeroOrden)
    {
        this.numeroOrden = numeroOrden;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaClaseBodega()
    {
        return listaClaseBodega;
    }

    public void setListaClaseBodega(List<Registro> listaClaseBodega)
    {
        this.listaClaseBodega = listaClaseBodega;
    }

    public List<Registro> getListaResponsableAux()
    {
        return listaResponsableAux;
    }

    public void setListaResponsableAux(List<Registro> listaResponsableAux)
    {
        this.listaResponsableAux = listaResponsableAux;
    }

    public String getResponsable()
    {
        return responsable;
    }

    public void setResponsable(String responsable)
    {
        this.responsable = responsable;
    }

    public RegistroDataModelImpl getListaauxiliarCombo()
    {
        return listaauxiliarCombo;
    }

    public void setListaauxiliarCombo(
        RegistroDataModelImpl listaauxiliarCombo)
    {
        this.listaauxiliarCombo = listaauxiliarCombo;
    }

    public String getNombreAuxiliar()
    {
        return nombreAuxiliar;
    }

    public void setNombreAuxiliar(String nombreAuxiliar)
    {
        this.nombreAuxiliar = nombreAuxiliar;
    }

    public int getAnio()
    {
        return anio;
    }

    public void setAnio(int anio)
    {
        this.anio = anio;
    }

    public int getIndiceSubordensuministro()
    {
        return indiceSubordensuministro;
    }

    public void setIndiceSubordensuministro(int indiceSubordensuministro)
    {
        this.indiceSubordensuministro = indiceSubordensuministro;
    }

    public boolean isCargar()
    {
        return cargar;
    }

    public void setCargar(boolean cargar)
    {
        this.cargar = cargar;
    }

    public double getCantidadAprobadaAnterior()
    {
        return cantidadAprobadaAnterior;
    }

    public void setCantidadAprobadaAnterior(double cantidadAprobadaAnterior)
    {
        this.cantidadAprobadaAnterior = cantidadAprobadaAnterior;
    }

    public String getDisponible()
    {
        return disponible;
    }

	/**
	 * @return the wIDTHESTADO
	 */
	public String getWIDTHESTADO() {
		return WIDTHESTADO;
	}

	/**
	 * @param wIDTHESTADO the wIDTHESTADO to set
	 */
	public void setWIDTHESTADO(String wIDTHESTADO) {
		WIDTHESTADO = wIDTHESTADO;
	}

	/**
	 * @return the habilitaApro
	 */
	public boolean isHabilitaApro() {
		return habilitaApro;
	}

	/**
	 * @param habilitaApro the habilitaApro to set
	 */
	public void setHabilitaApro(boolean habilitaApro) {
		this.habilitaApro = habilitaApro;
	}
 
}
