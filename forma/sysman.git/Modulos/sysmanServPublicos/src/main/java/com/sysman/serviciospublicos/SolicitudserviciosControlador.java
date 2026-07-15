package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.SolicitudserviciosControladorEnum;
import com.sysman.serviciospublicos.enums.SolicitudserviciosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jguerrero
 * @version 1, 02/09/2016 14:47:00 -- Modificado por jguerrero
 *
 * @author ybecerra
 * @version 2, 13/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum, para el codigo del formulario
 *
 * @author spina
 * @version 3, 27/06/2017 - refactorizo dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class SolicitudserviciosControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private final String modulo;
    private final String usuario;
    private final String etiquetaTbCons;

    // <DECLARAR_ATRIBUTOS>
    private String usos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listacicloSub;
    private List<Registro> listaClaseSolicitud;
    private List<Registro> listaFormateado;
    private List<Registro> listaTIPORESPUESTA;
    private List<Registro> listaTipoDocumento;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private List<Registro> listaBARRIO;
    private List<Registro> listaUso;
    private List<Registro> listaEstrato;
    private RegistroDataModelImpl listacmbFormato;
    private RegistroDataModelImpl listaDocumento;
    private RegistroDataModelImpl listaDocumentoE;
    private RegistroDataModelImpl listaTipoDePredio;
    private RegistroDataModelImpl listaTipoVivienda;
    private List<Registro> listaAnoInicial;
    private List<Registro> listaPeriodoInicial;
    private RegistroDataModelImpl listaDependencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaCodigoE;
    private RegistroDataModel listaCuadrocombinado7;
    private RegistroDataModel listaCuadrocombinado7E;
    private String auxiliar;
    private RegistroDataModelImpl listaConcepto;
    private RegistroDataModelImpl listaConceptoE;

    private List<Registro> listaEstratoAlumbrado;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaSubsolicituddocpresentado;
    private List<Registro> listaSolicitudsubmateriales;
    private List<Registro> listaSolicitudserfinanciables;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSubSubSolicitudDocPresentado;
    private Registro registroSubSolicitudSubMateriales;
    private Registro registroSubSolicitudSerFinanciables;
    private String descripcionAux;
    private String cantidadAux;
    private String valorUnitario;
    private String ivaAux;
    private String cicloSubFinanciables;
    private String ano;
    private String periodo;
    private String anoInicial;
    private double areaConstruida;
    private double areaLibre;
    private String nombreTipoVivienda;
    private String nombreTipoPredio;
    private String auxiliarNombreDocumentoPresentado;
    private String consecutivoNumeroSolicitudServicio;
    private String valortotal;
    private int indiceSolicitudserfinanciables;
    private StreamedContent archivoDescarga;
    private String solicitaServicio;
    private String existeRedAcueducto;
    private String alcantarillado;
    private String existeRedAlcantarillado;
    private String aseo;
    private boolean estratoAlumbrado;
    private boolean visibleFechaExpedicion;
    private String existeAcometidaE;
    private String lblExisteAlcantarillado;

    private String longFondoPredioE;
    private boolean eliminarSolicitud;
    private boolean cmdActivarActivo;
    private boolean activarCmdCopiar;
    private boolean bloqueTipoRespuesta;
    private boolean bloqueaIndicadores;
    private String tipoFormatoVariable;
    private boolean cargarFormateado;
    private String codigoFraude;
    private Date fechaOculta;
    private String plantillaWord;
    private String nombreDescarga;
    private boolean dialogoVisible;
    private String codigoRutaNuevo;
    private String maximoCaracteresCodigoRuta;
    private double resultadoParametro;
    private Date fechaSolicitud;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;

    /*
     *
     */

    // </DECLARAR_ADICIONALES>
    public SolicitudserviciosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        etiquetaTbCons = "TB_TB1515";

        try
        {

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null)
            {
                codigoFraude = parametros.get("codigoFraude").toString();

            }

            numFormulario = GeneralCodigoFormaEnum.SOLICITUDSERVICIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubSubSolicitudDocPresentado = new Registro(
                            new HashMap<String, Object>());
            registroSubSolicitudSubMateriales = new Registro(
                            new HashMap<String, Object>());
            registroSubSolicitudSerFinanciables = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigo();
        cargarListaCodigoE();
        cargarListaConcepto();
        cargarListaConceptoE();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaClaseSolicitud();
        cargarListaFormateado();
        cargarListaTIPORESPUESTA();
        cargarListaTipoDocumento();
        cargarListaPais();

        cargarListaUso();
        cargarListaDocumento();
        cargarListaDocumentoE();
        cargarListaTipoDePredio();
        cargarListaTipoVivienda();
        cargarListaAnoInicial();
        cargarListacmbFormato();
        cargarListaDependencia();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubsolicituddocpresentado();
        cargarListaSolicitudsubmateriales();
        cargarListaSolicitudserfinanciables();

        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubsolicituddocpresentado = null;
        listaSolicitudsubmateriales = null;
        listaSolicitudserfinanciables = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = "SP_SOLICITUDSERVICIO";
        enumBase = GenericUrlEnum.SP_SOLICITUDSERVICIO;
        buscarLlave();
        asignarOrigenDatos();
        fechaSolicitud = new Date();
        try
        {
            maximoCaracteresCodigoRuta = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NUMERO DE CARACTERES CODIGO RUTA",
                                            modulo, new Date(),
                                            true), "16")
                            .toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaSubsolicituddocpresentado()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SolicitudserviciosControladorEnum.CLASESOLICITUD.getValue(),
                        registro.getCampos()
                                        .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                        .getValue()));
        param.put(SolicitudserviciosControladorEnum.SOLICITUDSERVICIO
                        .getValue(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()));
        try
        {
            listaSubsolicituddocpresentado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL3985
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SP_SOLICITUDDOCPRESENTADO"));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSolicitudsubmateriales()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(SolicitudserviciosControladorEnum.CLASESOLICITUD
                            .getValue(),
                            registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue()));
            param.put("NUMEROSOLICITUD", registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName()));

            listaSolicitudsubmateriales = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL3989
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SP_ELEMENTOSSOLICITUD"));

        }
        catch (SysmanException | SystemException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSolicitudserfinanciables()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SolicitudserviciosControladorEnum.CLASESOLICITUD.getValue(),
                        registro.getCampos().get(
                                        SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                        .getValue()));
        param.put(SolicitudserviciosControladorEnum.SOLICITUD.getValue(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()));
        try
        {
            listaSolicitudserfinanciables = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL3993
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SP_SOLICITUDFINANCIABLES"));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaClaseSolicitud()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaClaseSolicitud = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3973
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFormateado()
    {
        // Metodo llamado en la vista
    }

    public void cargarListaTIPORESPUESTA()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SolicitudserviciosControladorEnum.TIPO.getValue(), 4);
        try
        {
            listaTIPORESPUESTA = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3974
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipoDocumento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaTipoDocumento = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3976
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPais()
    {
        Map<String, Object> param = new TreeMap<>();
        try
        {
            listaPais = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3977
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartamento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(SolicitudserviciosControladorEnum.PAIS.getValue(),
                        registro.getCampos().get("PAIS"));
        try
        {
            listaDepartamento = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3978
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiudad()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(SolicitudserviciosControladorEnum.PAIS.getValue(),
                        registro.getCampos().get("PAIS"));
        param.put(SolicitudserviciosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO"));
        try
        {
            listaCiudad = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3979
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaBARRIO()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(SolicitudserviciosControladorEnum.PAIS.getValue(),
                        registro.getCampos().get("PAIS"));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos().get("DEPARTAMENTO"));
        param.put(GeneralParameterEnum.CIUDAD.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CIUDAD.getName()));

        try
        {
            listaBARRIO = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3980
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaUso()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaUso = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3981
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEstrato()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("USO_ACTUAL", registro.getCampos().get("USO"));
        try
        {
            listaEstrato = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SolicitudserviciosControladorUrlEnum.URL3982
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDocumento()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL3997
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaDocumentoE()
    {
        listaDocumentoE = listaDocumento;
    }

    public void cargarListaTipoDePredio()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL3983
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoDePredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoVivienda()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL3998
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoVivienda = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaAnoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL3999
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);
        try
        {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL4003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDependencia()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL3984
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCodigo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL4001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCodigoE()
    {
        listaCodigoE = listaCodigo;
    }

    public void cargarListaConcepto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL4002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaConceptoE()
    {
        listaConceptoE = listaConcepto;
    }

    public void cargarListaEstratoAlumbrado()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("USO", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "USO ESPECIAL PARA ALUMBRADO",
                                            modulo,
                                            new Date(),
                                            true), "")
                            .toString());
            listaEstratoAlumbrado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL4000
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacmbFormato()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudserviciosControladorUrlEnum.URL3975
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SolicitudserviciosControladorEnum.TIPO.getValue(), 34);

        listacmbFormato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarPais()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(SolicitudserviciosControladorEnum.DEPARTAMENTO
                        .getValue(), null);
        registro.getCampos().put(GeneralParameterEnum.CIUDAD.getName(), null);
        registro.getCampos().put(
                        SolicitudserviciosControladorEnum.BARRIO.getValue(),
                        null);

        cargarListaDepartamento();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.CIUDAD.getName(), null);
        registro.getCampos().put(
                        SolicitudserviciosControladorEnum.BARRIO.getValue(),
                        null);

        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCiudad()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(
                        SolicitudserviciosControladorEnum.BARRIO.getValue(),
                        null);
        cargarListaBARRIO();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarUso()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put("ESTRATO", null);
        registro.getCampos().put("ESTRATOALUMBRADO", null);
        cargarListaEstrato();
        cargarListaEstratoAlumbrado();

    }

    public void cambiarAnoInicial()
    {
        // <CODIGO_DESARROLLADO>
        anoInicial = registroSubSolicitudSerFinanciables.getCampos()
                        .get(SolicitudserviciosControladorEnum.ANOINICIAL
                                        .getValue())
                        .toString();
        registroSubSolicitudSerFinanciables.getCampos().put("PERIODOINICIAL",
                        null);
        cargarListaPeriodoInicial();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcicloSub()
    {
        // <CODIGO_DESARROLLADO>

        ano = service.buscarEnLista(cicloSubFinanciables,
                        GeneralParameterEnum.NUMERO.getName(), "ANO",
                        listacicloSub);
        periodo = service.buscarEnLista(cicloSubFinanciables,
                        GeneralParameterEnum.NUMERO.getName(),
                        "PERIODO", listacicloSub);

        registroSubSolicitudSerFinanciables.getCampos().put(
                        SolicitudserviciosControladorEnum.ANOINICIAL.getValue(),
                        null);
        registroSubSolicitudSerFinanciables.getCampos().put("PERIODOINICIAL",
                        null);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAreaConstruida()
    {
        // <CODIGO_DESARROLLADO>

        areaConstruida = Double.parseDouble(
                        registro.getCampos().get("AREACONSTRUIDA").toString());
        registro.getCampos().put(
                        SolicitudserviciosControladorEnum.AREATOTAL.getValue(),
                        areaConstruida);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAreaLibre()
    {
        // <CODIGO_DESARROLLA DO>
        if (!"".equals(registro.getCampos().get("AREALIBRE")))
        {

            areaLibre = Double.parseDouble(
                            registro.getCampos().get("AREALIBRE").toString());
            double total = areaConstruida + areaLibre;
            registro.getCampos().put(SolicitudserviciosControladorEnum.AREATOTAL
                            .getValue(), total);
        }
        else
        {
            registro.getCampos().put(SolicitudserviciosControladorEnum.AREATOTAL
                            .getValue(), areaConstruida);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarLongFrentePredio()
    {
        // <CODIGO_DESARROLLADO>
        int longFrentePredio = 1;
        int longFondoPredio = 1;
        if (registro.getCampos()
                        .get(SolicitudserviciosControladorEnum.LONGFRENTEPREDIO
                                        .getValue()) != null)
        {
            longFrentePredio = Integer.parseInt(registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.LONGFRENTEPREDIO
                                            .getValue())
                            .toString());

        }
        if (registro.getCampos()
                        .get(SolicitudserviciosControladorEnum.LONGFONDOPREDIO
                                        .getValue()) != null)
        {
            longFondoPredio = Integer.parseInt(registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.LONGFONDOPREDIO
                                            .getValue())
                            .toString());
        }

        if (longFondoPredio > 1)
        {
            int areaTotal = longFondoPredio * longFrentePredio;
            registro.getCampos()
                            .put(SolicitudserviciosControladorEnum.AREACONSTRUIDARED
                                            .getValue(), areaTotal);
        }
        else
        {
            registro.getCampos()
                            .put(SolicitudserviciosControladorEnum.AREACONSTRUIDARED
                                            .getValue(), null);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarLongFondoPredio()
    {
        // <CODIGO_DESARROLLADO>
        int longFrentePredio = 1;
        int longFondoPredio = 1;
        if (registro.getCampos()
                        .get(SolicitudserviciosControladorEnum.LONGFRENTEPREDIO
                                        .getValue()) != null)
        {
            longFrentePredio = Integer.parseInt(registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.LONGFRENTEPREDIO
                                            .getValue())
                            .toString());

        }
        if (registro.getCampos()
                        .get(SolicitudserviciosControladorEnum.LONGFONDOPREDIO
                                        .getValue()) != null)
        {
            longFondoPredio = Integer.parseInt(registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.LONGFONDOPREDIO
                                            .getValue())
                            .toString());
        }

        if (longFrentePredio > 1)
        {
            int areaTotal = longFondoPredio * longFrentePredio;
            registro.getCampos()
                            .put(SolicitudserviciosControladorEnum.AREACONSTRUIDARED
                                            .getValue(), areaTotal);
        }
        else
        {
            registro.getCampos()
                            .put(SolicitudserviciosControladorEnum.AREACONSTRUIDARED
                                            .getValue(), null);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcantidad()
    {
        // <CODIGO_DESARROLLADO>

        calcularTotalSubMateriales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarporcIva()
    {
        // <CODIGO_DESARROLLADO>
        calcularTotalSubMateriales();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVALOR()
    {
        // <CODIGO_DESARROLLADO>
        solicitudServicioFinanciableCalcularValorCuota();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarinicial()
    {
        // <CODIGO_DESARROLLADO>
        solicitudServicioFinanciableCalcularValorCuota();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcuotas()
    {
        // <CODIGO_DESARROLLADO>
        if ((registroSubSolicitudSerFinanciables.getCampos()
                        .get(SolicitudserviciosControladorEnum.CUOTAS
                                        .getValue()) != null)
            || !"".equals(registroSubSolicitudSerFinanciables.getCampos()
                            .get(SolicitudserviciosControladorEnum.CUOTAS
                                            .getValue())))
        {
            int cuotas = Integer.parseInt(registroSubSolicitudSerFinanciables
                            .getCampos()
                            .get(SolicitudserviciosControladorEnum.CUOTAS
                                            .getValue())
                            .toString());
            if (cuotas == 0)
            {
                registroSubSolicitudSerFinanciables.getCampos()
                                .put(SolicitudserviciosControladorEnum.CUOTAS
                                                .getValue(),
                                                0);
            }
            else
            {
                double valor = 0;
                if (registroSubSolicitudSerFinanciables.getCampos()
                                .get(GeneralParameterEnum.VALOR
                                                .getName()) != null)
                {

                    valor = Double.parseDouble(
                                    registroSubSolicitudSerFinanciables
                                                    .getCampos()
                                                    .get(GeneralParameterEnum.VALOR
                                                                    .getName())
                                                    .toString());
                }
                double inicial = 0;
                if (registroSubSolicitudSerFinanciables.getCampos()
                                .get(SolicitudserviciosControladorEnum.INICIAL
                                                .getValue()) != null)
                {

                    inicial = Double.parseDouble(
                                    registroSubSolicitudSerFinanciables
                                                    .getCampos()
                                                    .get(SolicitudserviciosControladorEnum.INICIAL
                                                                    .getValue())
                                                    .toString());
                }

                double resultado = (valor - inicial) / cuotas;
                registroSubSolicitudSerFinanciables.getCampos()
                                .put(SolicitudserviciosControladorEnum.VALORCUOTA
                                                .getValue(), resultado);
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS

        // <CODIGO_DESARROLLADO>

        listaSolicitudsubmateriales.get(rowNum).getCampos().put(
                        GeneralParameterEnum.DESCRIPCION.getName(),
                        descripcionAux);
        listaSolicitudsubmateriales.get(rowNum).getCampos().put(
                        GeneralParameterEnum.CANTIDAD.getName(),
                        cantidadAux);
        listaSolicitudsubmateriales.get(rowNum).getCampos().put(
                        SolicitudserviciosControladorEnum.VALORUNITARIO
                                        .getValue(),
                        valorUnitario);
        listaSolicitudsubmateriales.get(rowNum).getCampos().put(
                        GeneralParameterEnum.PORCIVA.getName(),
                        ivaAux);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarConceptoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS

        // <CODIGO_DESARROLLADO>

        listaSolicitudserfinanciables.get(rowNum).getCampos()
                        .put(GeneralParameterEnum.VALOR.getName(), 0);
        listaSolicitudserfinanciables.get(rowNum).getCampos().put(
                        SolicitudserviciosControladorEnum.INICIAL.getValue(),
                        0);
        listaSolicitudserfinanciables.get(rowNum).getCampos().put(
                        SolicitudserviciosControladorEnum.CUOTAS.getValue(),
                        0);
        listaSolicitudserfinanciables.get(rowNum).getCampos().put(
                        SolicitudserviciosControladorEnum.VALORCUOTA.getValue(),
                        0);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoInicialC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS

        // <CODIGO_DESARROLLADO>
        if (listaSolicitudserfinanciables.get(rowNum).getCampos()
                        .get(SolicitudserviciosControladorEnum.ANOINICIAL
                                        .getValue()) != null)
        {

            anoInicial = listaSolicitudserfinanciables.get(rowNum).getCampos()
                            .get(SolicitudserviciosControladorEnum.ANOINICIAL
                                            .getValue())
                            .toString();
            cargarListaPeriodoInicial();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcicloSubC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVALORC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        solicitudServicioFinanciableCalcularValorCuotaEditar(rowNum);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarinicialC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // <CODIGO_DESARROLLADO>
        solicitudServicioFinanciableCalcularValorCuotaEditar(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcuotasC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        solicitudServicioFinanciableCalcularValorCuotaEditar(rowNum);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcantidadC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        double cantidad = Double.parseDouble(listaSolicitudsubmateriales
                        .get(rowNum).getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName())
                        .toString());
        double iva = Double.parseDouble(listaSolicitudsubmateriales.get(rowNum)
                        .getCampos().get(GeneralParameterEnum.PORCIVA.getName())
                        .toString());
        double valorUnitarioCant = Double.parseDouble(
                        listaSolicitudsubmateriales.get(rowNum).getCampos()
                                        .get(SolicitudserviciosControladorEnum.VALORUNITARIO
                                                        .getValue())
                                        .toString());
        double total = cantidad * valorUnitarioCant;
        double textoTotal = total + (total * (iva / 100));
        listaSolicitudsubmateriales.get(rowNum).getCampos().put(
                        SolicitudserviciosControladorEnum.TEXTOTOTAL.getValue(),
                        textoTotal);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarporcIvaC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        double cantidad = Double.parseDouble(listaSolicitudsubmateriales
                        .get(rowNum).getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName())
                        .toString());
        double iva = Double.parseDouble(listaSolicitudsubmateriales.get(rowNum)
                        .getCampos().get(GeneralParameterEnum.PORCIVA.getName())
                        .toString());
        double valorUnitarioPorc = Double.parseDouble(
                        listaSolicitudsubmateriales.get(rowNum).getCampos()
                                        .get(SolicitudserviciosControladorEnum.VALORUNITARIO
                                                        .getValue())
                                        .toString());
        double total = cantidad * valorUnitarioPorc;
        double textoTotal = total + (total * (iva / 100));
        listaSolicitudsubmateriales.get(rowNum).getCampos().put(
                        SolicitudserviciosControladorEnum.TEXTOTOTAL.getValue(),
                        textoTotal);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseSolicitud()
    {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos()
                        .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                        .getValue()) != null)
        {
            String claseSolicitud = registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue())
                            .toString();
            if ("04".equals(claseSolicitud))
            {
                bloqueaIndicadores = false;
            }
            else
            {
                bloqueaIndicadores = true;
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbFormato()
    {
        // <CODIGO_DESARROLLADO>
        if ("5".equals(tipoFormatoVariable))
        {
            cargarFormateado = true;
        }
        else
        {
            cargarFormateado = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoRuta()
    {
        // <CODIGO_DESARROLLADO>
        codigoRutaBeforeUpdate();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechaexpedicion()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            String fechaExpedicion = "";
            if (registro.getCampos().get("FECHAEXPEDICION") != null)
            {
                fechaExpedicion = SysmanFunciones.convertirAFechaCadena(
                                (Date) registro.getCampos()
                                                .get("FECHAEXPEDICION"));

            }

            Map<String, Object> param = new TreeMap<>();
            param.put("FECHAEXP", fechaExpedicion);
            param.put("PARFECHAVCTO", resultadoParametro);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL4004
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg.getCampos().get("FECHA") != null)
            {
                Date fechaVencimiento = SysmanFunciones.convertirAFecha(
                                String.valueOf(reg.getCampos().get("FECHA")));
                registro.getCampos().put("FECHAVENCIMIENTO", fechaVencimiento);

            }
            else
            {
                registro.getCampos().put("FECHAVENCIMIENTO", "");
            }
        }
        catch (SystemException | ParseException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCODIGODANE()
    {
        // <CODIGO_DESARROLLADO>

        if (!"".equals(registro.getCampos()
                        .get(SolicitudserviciosControladorEnum.CODIGODANE
                                        .getValue())))
        {
            String codigoDane = registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.CODIGODANE
                                            .getValue())
                            .toString();
            if (codigoDane.length() != 9)
            {
                registro.getCampos()
                                .put(SolicitudserviciosControladorEnum.CODIGODANE
                                                .getValue(), null);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1541"));

            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDocumentoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS

        // <CODIGO_DESARROLLADO>
        listaSubsolicituddocpresentado.get(rowNum).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        auxiliarNombreDocumentoPresentado);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registroSubSolicitudSubMateriales.getCampos().put(
                        GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registroSubSolicitudSubMateriales.getCampos().put(
                        GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
        registroSubSolicitudSubMateriales.getCampos()
                        .put(GeneralParameterEnum.CANTIDAD.getName(), "1");
        registroSubSolicitudSubMateriales.getCampos()
                        .put(SolicitudserviciosControladorEnum.VALORUNITARIO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SolicitudserviciosControladorEnum.VALORUNITARIO
                                                                        .getValue()));
        registroSubSolicitudSubMateriales.getCampos().put(
                        GeneralParameterEnum.PORCIVA.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.PORCIVA
                                        .getName()));

        calcularTotalSubMateriales();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaCodigoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = String.valueOf(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        descripcionAux = registroAux.getCampos()
                        .get(GeneralParameterEnum.DESCRIPCION.getName())
                        .toString();
        valorUnitario = registroAux.getCampos()
                        .get(SolicitudserviciosControladorEnum.VALORUNITARIO
                                        .getValue())
                        .toString();
        ivaAux = registroAux.getCampos()
                        .get(GeneralParameterEnum.PORCIVA.getName()).toString();
        cantidadAux = "1";

    }

    public void seleccionarFilaConcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if ("12".equals(registroAux.getCampos().get(
                        GeneralParameterEnum.CODIGO.getName()).toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1703"));
            return;
        }
        else
        {

            registroSubSolicitudSerFinanciables.getCampos().put(
                            GeneralParameterEnum.CONCEPTO.getName(),
                            registroAux.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));
        }
    }

    public void seleccionarFilaConceptoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaTipoDePredio(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SolicitudserviciosControladorEnum.TIPODEPREDIO
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreTipoPredio = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

    }

    public void seleccionarFilaTipoVivienda(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SolicitudserviciosControladorEnum.TIPOVIVIENDA
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreTipoVivienda = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaDocumento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubSolicitudDocPresentado.getCampos().put("DOCUMENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registroSubSubSolicitudDocPresentado.getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilaDocumentoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = String.valueOf(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        auxiliarNombreDocumentoPresentado = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

    }

    public void seleccionarFilacmbFormato(SelectEvent event)
    {
        try
        {
            Registro registroAux = (Registro) event.getObject();
            tipoFormatoVariable = registroAux.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString();
            if (registroAux.getCampos().get("FECHAOCULTA") != null)
            {
                fechaOculta = SysmanFunciones
                                .convertirAFecha(String.valueOf(registroAux
                                                .getCampos()
                                                .get("FECHAOCULTA")));
            }
            else
            {
                fechaOculta = null;
            }

            nombreDescarga = registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())
                            .toString();
            plantillaWord = registroAux.getCampos().get("PLANTILLA").toString();
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimircmdCopia()
    {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = true;

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>

        if (css != null)
        {

            if ("".equals(tipoFormatoVariable))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1513"));
            }
            else
            {
                String fechaPlantilla;
                if (fechaOculta == null)
                {
                    fechaPlantilla = "";
                }
                else
                {
                    fechaPlantilla = SysmanFunciones
                                    .formatearFecha(fechaOculta);
                }

                String[] campos = { "codigoPlantilla", "fechaPlantilla",
                                    "nombreDocDescarga" };
                String[] valores = { tipoFormatoVariable, fechaPlantilla,
                                     nombreDescarga };

                String codigoRutaWord = registro.getCampos()
                                .get(GeneralParameterEnum.CODIGORUTA.getName())
                                .toString();
                String nombreLargo = SessionUtil.getCompaniaIngreso()
                                .getNombre();
                String nit = SessionUtil.getCompaniaIngreso().getNit();
                String claseSolicitud = registro.getCampos()
                                .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                .getValue())
                                .toString();

                String numero = registro.getCampos()
                                .get(GeneralParameterEnum.NUMERO.getName())
                                .toString();

                HashMap<String, String> variablesConsultaW = new HashMap<>();

                variablesConsultaW.put("s$compania$s", "'" + compania + "'");
                variablesConsultaW.put("s$codigoRuta$s",
                                "" + codigoRutaWord + "");
                variablesConsultaW.put("s$nombreLargo$s",
                                "'" + nombreLargo + "'");
                variablesConsultaW.put("s$nit$s", "'" + nit + "'");
                variablesConsultaW.put("s$claseSolicitud$s",
                                "" + claseSolicitud + "");
                variablesConsultaW.put("s$numero$s", "" + numero + "");

                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);

                SessionUtil.cargarModalDatosFlash(
                                String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                modulo, campos,
                                valores);

            }

        }
        else
        {

            agregarRegistroNuevo(false);

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdEliminar()
    {
        // <CODIGO_DESARROLLADO>
        if (!(boolean) registro.getCampos().get("ACTIVADO"))
        {

            try
            {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SolicitudserviciosControladorUrlEnum.URL4008
                                                                .getValue());

                Registro reg = new Registro();
                reg.getCampos().put("ANULADA", -1);
                reg.getCampos().put("FECHA_ANUL", new Date());
                reg.getCampos().put("USUARIO_ANUL", usuario);
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                usuario);
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                reg.getLlave().put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                reg.getLlave().put(
                                SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                .getValue(),
                                registro.getCampos()
                                                .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                                .getValue()));
                reg.getLlave().put(GeneralParameterEnum.NUMERO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()));

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(),
                                reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                SolicitudserviciosControladorEnum.MSM_REGISTRO_ELIMINADO
                                                                .getValue()));

                SessionUtil.redireccionar("/solicitudservicio.sysman");
            }
            catch (SystemException e)
            {
                Logger.getLogger(SolicitudserviciosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1484"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmbDirTecnica()
    {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
                                        .getCodigo()),
                        modulo);

        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariocmbDirTecnica(SelectEvent event) // event
    // necesario
    // en
    // la
    // vista
    {
        // <CODIGO_DESARROLLADO>
        if (SessionUtil.getFlash() != null)
        {
            Map<String, Object> retorno = SessionUtil.getFlash();
            registro.getCampos().put("DIRTECNICA", retorno.get("direccion"));
            SessionUtil.cleanFlash();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarnuevoCodigoRuta()
    {
        // <CODIGO_DESARROLLADO>

        if (css != null)
        {
            try
            {
                if (codigoRutaNuevo.length() > Integer
                                .parseInt(maximoCaracteresCodigoRuta))
                {
                    codigoRutaNuevo = null;
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1493"));
                }
                else
                {

                    generarConsecutivoNumero();
                    ejbServiciosPublicosOcho.agregarNuevoCodigoRuta(compania,
                                    new BigInteger(SysmanFunciones.nvl(
                                                    registro.getCampos()
                                                                    .get(GeneralParameterEnum.NUMERO
                                                                                    .getName()),
                                                    "0").toString()),
                                    SysmanFunciones.nvl(registro.getCampos()
                                                    .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                                    .getValue()),
                                                    "").toString(),
                                    new BigInteger(consecutivoNumeroSolicitudServicio),
                                    codigoRutaNuevo,
                                    SessionUtil.getUser().getCodigo());

                }
                codigoRutaNuevo = null;
                dialogoVisible = false;
            }
            catch (SystemException e)
            {
                Logger.getLogger(SolicitudserviciosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1437"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cancelarnuevoCodigoRuta()
    {
        // <CODIGO_DESARROLLADO>
        codigoRutaNuevo = null;
        dialogoVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirmdImprimir()
    {
        // <CODIGO_DESARROLLADO>
        if (css != null)
        {
            genInforme(FORMATOS.PDF);
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1656"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdActivar()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);

        if ((registro.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName()) != null)
            || (registro.getCampos()
                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue()) != null)
            || (registro.getCampos().get(
                            GeneralParameterEnum.NUMERO.getName()) != null))
        {

            String[] campos = { "CODIGOFRAUDE", "REGISTROSOLICITUDSERVICIO",
                                "CSS" };
            Object[] valores = { codigoFraude, registro.getCampos(), css };

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.PEDIRCICLONUEVOS_CONTROLADOR
                                            .getCodigo()),
                            modulo,
                            campos, valores);
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1495"));
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubsolicituddocpresentado()
    {
        try
        {
            registroSubSubSolicitudDocPresentado.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSubSolicitudDocPresentado.getCampos().put(
                            SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue(),
                            registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue()));
            registroSubSubSolicitudDocPresentado.getCampos().put(
                            SolicitudserviciosControladorEnum.SOLICITUDSERVICIO
                                            .getValue(),
                            registro.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()));
            registroSubSubSolicitudDocPresentado.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());
            registroSubSubSolicitudDocPresentado.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubSubSolicitudDocPresentado.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser()
                                            .getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3988
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubSolicitudDocPresentado.getCampos());

            cargarListaSubsolicituddocpresentado();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSubSolicitudDocPresentado = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubsolicituddocpresentado(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.SOLICITUDSERVICIO
                                            .getValue());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3987
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            SolicitudserviciosControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        cargarListaSubsolicituddocpresentado();
    }

    public void eliminarRegSubSubsolicituddocpresentado(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3986
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            SolicitudserviciosControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaSubsolicituddocpresentado();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarEdicionSubsolicituddocpresentado()
    {
        cargarListaSubsolicituddocpresentado();
        cargarListaSolicitudsubmateriales();
        cargarListaSolicitudserfinanciables();
    }

    public void agregarRegistroSubSolicitudsubmateriales()
    {
        try
        {
            registroSubSolicitudSubMateriales.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSolicitudSubMateriales.getCampos().put(
                            SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue(),
                            registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue()));
            registroSubSolicitudSubMateriales.getCampos()
                            .put(SolicitudserviciosControladorEnum.SOLICITUD
                                            .getValue(),
                                            registro.getCampos()
                                                            .get(GeneralParameterEnum.NUMERO
                                                                            .getName()));

            registroSubSolicitudSubMateriales.getCampos()
                            .remove(SolicitudserviciosControladorEnum.TEXTOTOTAL
                                            .getValue());
            registroSubSolicitudSubMateriales.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registroSubSolicitudSubMateriales.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(), usuario);
            registroSubSolicitudSubMateriales.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3992
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSolicitudSubMateriales.getCampos());

            cargarListaSolicitudsubmateriales();
            actualizarTotalSubElementos();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSolicitudSubMateriales = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSolicitudsubmateriales(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue());
            reg.getCampos().remove(SolicitudserviciosControladorEnum.SOLICITUD
                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
            reg.getCampos().remove(
                            GeneralParameterEnum.VALORUNITARIO.getName());
            reg.getCampos().remove(SolicitudserviciosControladorEnum.TEXTOTOTAL
                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3991
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            actualizarTotalSubElementos();
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(SolicitudserviciosControladorEnum.MSM_REGISTRO_MODIFICADO
                                            .getValue()));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSolicitudsubmateriales();
        }
    }

    public void eliminarRegSubSolicitudsubmateriales(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3990
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            actualizarTotalSubElementos();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            SolicitudserviciosControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaSolicitudsubmateriales();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSolicitudsubmateriales()
    {
        cargarListaSolicitudsubmateriales();
        cargarListaSolicitudserfinanciables();
    }

    public void agregarRegistroSubSolicitudserfinanciables()
    {
        registro.getCampos().remove("INICIAL");
        agregarRegistroNuevo(false);
        try
        {
            registroSubSolicitudSerFinanciables.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSolicitudSerFinanciables.getCampos().put(
                            SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue(),
                            registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue()));
            registroSubSolicitudSerFinanciables.getCampos()
                            .put(SolicitudserviciosControladorEnum.SOLICITUD
                                            .getValue(),
                                            registro.getCampos()
                                                            .get(GeneralParameterEnum.NUMERO
                                                                            .getName()));

            registroSubSolicitudSerFinanciables.getCampos().remove("CICLO");
            registroSubSolicitudSerFinanciables.getCampos()
                            .remove(SolicitudserviciosControladorEnum.VALORCUOTA
                                            .getValue());

            registroSubSolicitudSerFinanciables.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(), usuario);
            registroSubSolicitudSerFinanciables.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3996
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSolicitudSerFinanciables.getCampos());

            cargarListaSolicitudserfinanciables();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSolicitudSerFinanciables = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSolicitudserfinanciables(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove(SolicitudserviciosControladorEnum.VALORCUOTA
                            .getValue());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.NOMBRECONCEPTO
                                            .getValue());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.NOMBREPERIODOINICIAL
                                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.CLASESOLICITUD
                                            .getValue());
            reg.getCampos().remove(SolicitudserviciosControladorEnum.SOLICITUD
                            .getValue());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.NOMBRECONCEPTO
                                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            reg.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3995
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            SolicitudserviciosControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSolicitudserfinanciables();
        }
    }

    public void eliminarRegSubSolicitudserfinanciables(Registro reg)
    {
        if (reg.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName()) == null)
        {
            return;
        }

        String concepto = reg.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName())
                        .toString();
        if ("7".equals(concepto))
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(SolicitudserviciosControladorEnum.CLASESOLICITUD
                            .getValue(), registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue())
                                            .toString());
            param.put(GeneralParameterEnum.NUMERO.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName())
                            .toString());

            try
            {
                Registro regConsulta = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SolicitudserviciosControladorUrlEnum.URL4005
                                                                                .getValue())
                                                .getUrl(), param));
                if (regConsulta != null)
                {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB1496"));
                    return;
                }
                else
                {
                    eliminarRegSubSolFin(reg);
                }
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else
        {
            eliminarRegSubSolFin(reg);
        }

    }

    public void eliminarRegSubSolFin(Registro reg)
    {
        try
        {
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.NOMBRECONCEPTO
                                            .getValue());
            reg.getCampos().remove(
                            SolicitudserviciosControladorEnum.NOMBREPERIODOINICIAL
                                            .getValue());

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudserviciosControladorUrlEnum.URL3994
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(SolicitudserviciosControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));
            cargarListaSolicitudserfinanciables();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionSolicitudserfinanciables()
    {
        cargarListaSolicitudserfinanciables();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        if ("74051902".equals(SessionUtil.getMenuActual())
            && (codigoFraude != null))
        {
            cargarRegistro(null, "i");

        }

        try

        {
            boolean cambiarNombre = "SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CAMBIAR NOMBRE SERVICIO ACUEDUCTO",
                                            modulo, new Date(), true),
                            "NO"));
            if (cambiarNombre)
            {

                String nombreServicio = ejbSysmanUtil.consultarParametro(
                                compania,
                                "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO", modulo,
                                new Date(), true);
                existeRedAcueducto = solicitaServicio = SysmanFunciones
                                .concatenar(idioma.getString("TB_TB1514"), " ",
                                                nombreServicio,
                                                idioma.getString(
                                                                etiquetaTbCons));
            }
            else
            {
                solicitaServicio = idioma.getString("TB_TB1516");
                existeRedAcueducto = idioma.getString("TB_TB1517");
            }

            cambiarNombreAlcant();

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CAMBIAR NOMBRE SERVICIO ASEO",
                                            modulo, new Date(), true),
                                            "NO")))
            {
                String nombreServicio = ejbSysmanUtil.consultarParametro(
                                compania, "NOMBRE SERVICIO A REMPLAZAR ASEO",
                                modulo, new Date(),
                                true);
                aseo = SysmanFunciones.concatenar(nombreServicio,
                                idioma.getString(etiquetaTbCons));

            }
            else
            {
                aseo = idioma.getString("TB_TB1520");
            }

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SEPARAR CATEGORIA DE ALUMBRADO",
                                            modulo, new Date(), true),
                                            "NO")))
            {
                estratoAlumbrado = true;

            }
            else
            {
                estratoAlumbrado = false;
            }

            resultadoParametro = Double.parseDouble(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "VENCIMIENTO PARA SOLICITUD DE SERVICIO",
                                            modulo, new Date(),
                                            true), "0")
                            .toString());

            if (resultadoParametro > 0)
            {
                visibleFechaExpedicion = true;

            }
            else
            {
                visibleFechaExpedicion = false;
            }

            if ("ACACIAS".equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRES EN SOLICITUD DE SERVICIO",
                                            modulo, new Date(), true),
                            "NO")))
            {
                existeRedAcueducto = idioma.getString("TB_TB1521");
                existeRedAlcantarillado = idioma.getString("TB_TB1522");
                existeAcometidaE = idioma.getString("TB_TB1523");
                lblExisteAlcantarillado = idioma.getString("TB_TB1525");
                longFondoPredioE = idioma.getString("TB_TB1526");
            }
            else
            {
                existeAcometidaE = idioma.getString("TB_TB1527");
                lblExisteAlcantarillado = idioma.getString("TB_TB1528");
                longFondoPredioE = idioma.getString("TB_TB1530");
            }

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE ELIMINAR SOLICITUD DE SERVICIO",
                                            modulo, new Date(),
                                            true), "NO")))
            {
                eliminarSolicitud = true;
            }
            else
            {
                eliminarSolicitud = false;
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private void cambiarNombreAlcant()
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO",
                                            modulo, new Date(),
                                            true), "NO")))
            {
                String nombreServicio = ejbSysmanUtil.consultarParametro(
                                compania,
                                "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO",
                                modulo,
                                new Date(), true);

                alcantarillado = SysmanFunciones.concatenar(nombreServicio,
                                idioma.getString(etiquetaTbCons));

                existeRedAlcantarillado = SysmanFunciones.concatenar(
                                idioma.getString("AC_CK901"), nombreServicio,
                                idioma.getString(etiquetaTbCons));
            }
            else
            {
                alcantarillado = idioma.getString("TB_TB1518");
                existeRedAlcantarillado = idioma.getString("TB_TB1519");
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        try
        {

            precargarRegistro();
            cargarListaDepartamento();
            cargarListaCiudad();
            cargarListaBARRIO();
            cargarListaEstrato();
            cargarListaEstratoAlumbrado();

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE ELIMINAR SOLICITUD DE SERVICIO",
                                            modulo, new Date(),
                                            true), "NO")))
            {

                eliminarSolicitud = true;

            }
            else
            {
                eliminarSolicitud = false;
            }

            if (css == null)
            {
                valortotal = "0";
                nombreTipoPredio = null;
                nombreTipoVivienda = null;

                registro.getCampos().put("OPERADOR", usuario);
                registro.getCampos().put("FECHASOLICITUD", fechaSolicitud);
                registro.getCampos().put("HORASOLICITUD", new Date());

            }
            else
            {
                accion = "m";
                actualizarTotalSubElementos();
                registro.getCampos().put("OPERADOR", usuario);
                registro.getCampos().put("USUARIO",
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CREATED_BY
                                                                .getName()));
                if (registro.getCampos()
                                .get(SolicitudserviciosControladorEnum.TIPODEPREDIO
                                                .getValue()) != null)
                {
                    Map<String, Object> param = new HashMap<>();
                    param.put("CODIGO", registro.getCampos().get(
                                    SolicitudserviciosControladorEnum.TIPODEPREDIO
                                                    .getValue()));
                    nombreTipoPredio = listaTipoDePredio.getRegistroUnico(param)
                                    .getCampos()
                                    .get(GeneralParameterEnum.NOMBRE.getName())
                                    .toString();
                }
                else
                {
                    nombreTipoPredio = null;
                }

                if (registro.getCampos()
                                .get(SolicitudserviciosControladorEnum.TIPOVIVIENDA
                                                .getValue()) != null)
                {
                    Map<String, Object> param = new HashMap<>();
                    param.put("CODIGO", registro.getCampos()
                                    .get(SolicitudserviciosControladorEnum.TIPOVIVIENDA
                                                    .getValue()));
                    nombreTipoVivienda = listaTipoVivienda
                                    .getRegistroUnico(param).getCampos()
                                    .get(GeneralParameterEnum.NOMBRE.getName())
                                    .toString();

                }
                else
                {
                    nombreTipoVivienda = null;
                }

                boolean activado = (boolean) registro.getCampos()
                                .get("ACTIVADO");

                if (activado)
                {
                    cmdActivarActivo = false;
                    accion = "v";
                    validarParametros();
                }
                else
                {
                    cmdActivarActivo = true;
                }
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class
                            .getName()).log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    private void validarParametros()
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE COPIAR SOLICITUD DESPUES DE SER ACTIVADA",
                                            modulo,
                                            new Date(), true), "NO")))
            {
                activarCmdCopiar = false;
            }
            else
            {
                activarCmdCopiar = true;
            }

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CAMBIAR TIPO RESPUESTA SOLICITUD SERVICIO",
                                            modulo, new Date(),
                                            true), "NO")))
            {
                bloqueTipoRespuesta = false;
            }
            else
            {
                bloqueTipoRespuesta = true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if ((registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()) == null)
            || "".equals(registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName())))
        {
            generarConsecutivoNumero();
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivoNumeroSolicitudServicio);
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("FECHASOLICITUD", fechaSolicitud);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void calcularTotalSubMateriales()
    {
        if (registroSubSolicitudSubMateriales.getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName()) != null)
        {
            if (registroSubSolicitudSubMateriales.getCampos()
                            .get(GeneralParameterEnum.PORCIVA
                                            .getName()) != null)
            {
                double cantidad = Double.parseDouble(
                                registroSubSolicitudSubMateriales.getCampos()
                                                .get(GeneralParameterEnum.CANTIDAD
                                                                .getName())
                                                .toString());
                double iva = Integer.parseInt(registroSubSolicitudSubMateriales
                                .getCampos()
                                .get(GeneralParameterEnum.PORCIVA.getName())
                                .toString());
                double valorUnitarioCalc = Integer.parseInt(
                                registroSubSolicitudSubMateriales.getCampos()
                                                .get(SolicitudserviciosControladorEnum.VALORUNITARIO
                                                                .getValue())
                                                .toString());

                if ((cantidad > 0) && (iva > 0))
                {
                    double total = cantidad * valorUnitarioCalc;
                    double totalFinal = total + (total * (iva / 100));
                    registroSubSolicitudSubMateriales.getCampos()
                                    .put(SolicitudserviciosControladorEnum.TEXTOTOTAL
                                                    .getValue(), totalFinal);
                }
            }
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1487"));
        }

    }

    public void generarConsecutivoNumero()
    {
        try
        {
            boolean consunico = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CONSECUTIVO UNICO PARA SOLICITUD DE SERVICIOS",
                                            modulo,
                                            new Date(), true), "NO"));

            consecutivoNumeroSolicitudServicio = String.valueOf(ejbSysmanUtil
                            .generarSiguienteConsecutivo("SP_SOLICITUDSERVICIO",
                                            consunico
                                                ? "COMPANIA = " + compania + ""
                                                : "COMPANIA = " + compania
                                                    + " AND CLASESOLICITUD IN "
                                                    + registro.getCampos()
                                                                    .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                                                    .getValue())
                                                    + "",
                                            GeneralParameterEnum.NUMERO
                                                            .getName()));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void actualizarTotalSubElementos()
    {
        try
        {
            valortotal = ejbServiciosPublicosOcho.actualizarSubTotalElementos(
                            compania, Integer.parseInt(modulo), usuario,
                            registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue())
                                            .toString(),
                            new BigInteger(registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())
                                            .toString()));

            cargarListaSolicitudserfinanciables();

        }
        catch (NumberFormatException | SystemException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void activarEdicionSolicitudserfinanciables(Registro r)
    {
        indiceSolicitudserfinanciables = listaSolicitudserfinanciables
                        .indexOf(r);

    }

    public void solicitudServicioFinanciableCalcularValorCuota()
    {
        double valor = 0;
        double cuotas = 0;
        if (registroSubSolicitudSerFinanciables.getCampos()
                        .get(GeneralParameterEnum.VALOR.getName()) != null)
        {
            valor = Double.parseDouble(registroSubSolicitudSerFinanciables
                            .getCampos()
                            .get(GeneralParameterEnum.VALOR.getName())
                            .toString());

        }

        if ((registroSubSolicitudSerFinanciables.getCampos()
                        .get(SolicitudserviciosControladorEnum.INICIAL
                                        .getValue()) != null)
            && (Double.parseDouble(registroSubSolicitudSerFinanciables
                            .getCampos()
                            .get(SolicitudserviciosControladorEnum.INICIAL
                                            .getValue())
                            .toString()) > valor))
        {
            registroSubSolicitudSerFinanciables.getCampos()
                            .put(SolicitudserviciosControladorEnum.INICIAL
                                            .getValue(),
                                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1488"));

        }

        if (registroSubSolicitudSerFinanciables.getCampos()
                        .get(SolicitudserviciosControladorEnum.CUOTAS
                                        .getValue()) != null)
        {
            cuotas = Double.parseDouble(registroSubSolicitudSerFinanciables
                            .getCampos()
                            .get(SolicitudserviciosControladorEnum.CUOTAS
                                            .getValue())
                            .toString());
        }

        if (Double.doubleToRawLongBits(cuotas) != 0)
        {

            registro.getCampos().put(SolicitudserviciosControladorEnum.INICIAL
                            .getValue(), null);

        }
        else
        {
            registroSubSolicitudSerFinanciables.getCampos()
                            .put(SolicitudserviciosControladorEnum.VALORCUOTA
                                            .getValue(),
                                            0);
        }

    }

    public void solicitudServicioFinanciableCalcularValorCuotaEditar(
        int rowNum)
    {

        double valor = 0;
        double cuotas = 0;
        double inicial = 0;

        if (listaSolicitudserfinanciables.get(rowNum).getCampos()
                        .get(GeneralParameterEnum.VALOR.getName()) != null)
        {
            valor = Double.parseDouble(listaSolicitudserfinanciables.get(rowNum)
                            .getCampos()
                            .get(GeneralParameterEnum.VALOR.getName())
                            .toString());

        }

        if (listaSolicitudserfinanciables.get(rowNum).getCampos()
                        .get(SolicitudserviciosControladorEnum.INICIAL
                                        .getValue()) != null)
        {
            if (Double.parseDouble(listaSolicitudserfinanciables.get(rowNum)
                            .getCampos()
                            .get(SolicitudserviciosControladorEnum.INICIAL
                                            .getValue())
                            .toString()) < valor)
            {

                inicial = Double.parseDouble(listaSolicitudserfinanciables
                                .get(rowNum).getCampos()
                                .get(SolicitudserviciosControladorEnum.INICIAL
                                                .getValue())
                                .toString());
            }
            else
            {
                listaSolicitudserfinanciables.get(rowNum).getCampos().put(
                                SolicitudserviciosControladorEnum.INICIAL
                                                .getValue(),
                                listaSolicitudserfinanciables.get(rowNum)
                                                .getCampos()
                                                .get(GeneralParameterEnum.VALOR
                                                                .getName()));
                registroSubSolicitudSerFinanciables.getCampos()
                                .put(SolicitudserviciosControladorEnum.INICIAL
                                                .getValue(),
                                                null);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1488"));
            }
        }

        if (listaSolicitudserfinanciables.get(rowNum).getCampos()
                        .get(SolicitudserviciosControladorEnum.CUOTAS
                                        .getValue()) != null)
        {
            cuotas = Double.parseDouble(listaSolicitudserfinanciables
                            .get(rowNum).getCampos()
                            .get(SolicitudserviciosControladorEnum.CUOTAS
                                            .getValue())
                            .toString());
        }

        if (Double.doubleToRawLongBits(cuotas) != 0)
        {

            double resultadoFinanciables = (valor - inicial)
                / Double.doubleToRawLongBits(cuotas);
            listaSolicitudserfinanciables.get(rowNum).getCampos()
                            .put(SolicitudserviciosControladorEnum.VALORCUOTA
                                            .getValue(), resultadoFinanciables);
        }
        else
        {
            listaSolicitudserfinanciables.get(rowNum).getCampos()
                            .put(SolicitudserviciosControladorEnum.VALORCUOTA
                                            .getValue(), 0);
        }

    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        String reporte = "001061SolSerMateriales";
        try
        {

            actualizarAntes();

            archivoDescarga = null;

            String cargoDirector = String.valueOf(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO DIRECTOR COMERCIAL", modulo,
                                            new Date(), true), ""));
            String nombreDirector = String.valueOf(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE DIRECTOR COMERCIAL", modulo,
                                            new Date(), true), ""));

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("claseSolicitud",
                            registro.getCampos()
                                            .get(SolicitudserviciosControladorEnum.CLASESOLICITUD
                                                            .getValue()));
            reemplazar.put("numero", registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName()));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRE_DIRECTOR", nombreDirector);
            parametros.put("PR_CARGO_DIRECTOR", cargoDirector);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void codigoRutaBeforeUpdate()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGORUTA
                                                            .getName())
                                            .toString());

            Registro regCodigoRuta = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudserviciosControladorUrlEnum.URL4006
                                                                            .getValue())
                                            .getUrl(), param));
            if (regCodigoRuta != null)
            {
                String ciclo = regCodigoRuta.getCampos().get("CICLO")
                                .toString();
                String nombre = regCodigoRuta.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();
                JsfUtil.agregarMensajeAlerta(
                                SysmanFunciones.concatenar(
                                                idioma.getString("TB_TB1498"),
                                                " ",
                                                ciclo,
                                                idioma.getString("TB_TB1490"),
                                                nombre));
                registro.getCampos().put(
                                GeneralParameterEnum.CODIGORUTA.getName(),
                                null);
            }
            else
            {
                Registro regCodigoR = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SolicitudserviciosControladorUrlEnum.URL4007
                                                                                .getValue())
                                                .getUrl(), param));

                if (regCodigoR != null)
                {
                    String codigo = regCodigoR.getCampos()
                                    .get(GeneralParameterEnum.NUMERO.getName())
                                    .toString();
                    String nombre = regCodigoR.getCampos()
                                    .get(GeneralParameterEnum.NOMBRE.getName())
                                    .toString();
                    registro.getCampos().put(
                                    GeneralParameterEnum.CODIGORUTA.getName(),
                                    null);
                    JsfUtil.agregarMensajeAlerta(
                                    SysmanFunciones.concatenar(
                                                    idioma.getString(
                                                                    "TB_TB1492"),
                                                    " ",
                                                    codigo, " ",
                                                    idioma.getString(
                                                                    "TB_TB1490"),
                                                    " ", nombre));
                }

            }

            if (registro.getCampos().get(GeneralParameterEnum.CODIGORUTA
                            .getName()) != null)
            {
                String codigoruta = registro.getCampos()
                                .get(GeneralParameterEnum.CODIGORUTA.getName())
                                .toString();
                int maximoCaracAux = Integer
                                .parseInt(maximoCaracteresCodigoRuta);
                if (codigoruta.length() > maximoCaracAux)
                {
                    registro.getCampos().put(
                                    GeneralParameterEnum.CODIGORUTA.getName(),
                                    null);
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1493"));
                }
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(SolicitudserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getUsos()
    {
        return usos;
    }

    public void setUsos(String usos)
    {
        this.usos = usos;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaClaseSolicitud()
    {
        return listaClaseSolicitud;
    }

    public void setListaClaseSolicitud(List<Registro> listaClaseSolicitud)
    {
        this.listaClaseSolicitud = listaClaseSolicitud;
    }

    public List<Registro> getListaFormateado()
    {
        return listaFormateado;
    }

    public void setListaFormateado(List<Registro> listaFormateado)
    {
        this.listaFormateado = listaFormateado;
    }

    public List<Registro> getListaTIPORESPUESTA()
    {
        return listaTIPORESPUESTA;
    }

    public void setListaTIPORESPUESTA(List<Registro> listaTIPORESPUESTA)
    {
        this.listaTIPORESPUESTA = listaTIPORESPUESTA;
    }

    public List<Registro> getListaTipoDocumento()
    {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<Registro> listaTipoDocumento)
    {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public List<Registro> getListaPais()
    {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais)
    {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento()
    {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento)
    {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad()
    {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad)
    {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaBARRIO()
    {
        return listaBARRIO;
    }

    public void setListaBARRIO(List<Registro> listaBARRIO)
    {
        this.listaBARRIO = listaBARRIO;
    }

    public List<Registro> getListaUso()
    {
        return listaUso;
    }

    public void setListaUso(List<Registro> listaUso)
    {
        this.listaUso = listaUso;
    }

    public List<Registro> getListaEstrato()
    {
        return listaEstrato;
    }

    public void setListaEstrato(List<Registro> listaEstrato)
    {
        this.listaEstrato = listaEstrato;
    }

    public RegistroDataModelImpl getListaDocumento()
    {
        return listaDocumento;
    }

    public void setListaDocumento(RegistroDataModelImpl listaDocumento)
    {
        this.listaDocumento = listaDocumento;
    }

    public RegistroDataModelImpl getListaTipoDePredio()
    {
        return listaTipoDePredio;
    }

    public void setListaTipoDePredio(RegistroDataModelImpl listaTipoDePredio)
    {
        this.listaTipoDePredio = listaTipoDePredio;
    }

    public RegistroDataModelImpl getListaTipoVivienda()
    {
        return listaTipoVivienda;
    }

    public void setListaTipoVivienda(RegistroDataModelImpl listaTipoVivienda)
    {
        this.listaTipoVivienda = listaTipoVivienda;
    }

    public List<Registro> getListaAnoInicial()
    {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial)
    {
        this.listaAnoInicial = listaAnoInicial;
    }

    public List<Registro> getListaPeriodoInicial()
    {
        return listaPeriodoInicial;
    }

    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial)
    {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigo()
    {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModelImpl listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModelImpl getListaCodigoE()
    {
        return listaCodigoE;
    }

    public void setListaCodigoE(RegistroDataModelImpl listaCodigoE)
    {
        this.listaCodigoE = listaCodigoE;
    }

    public RegistroDataModel getListaCuadrocombinado7()
    {
        return listaCuadrocombinado7;
    }

    public void setListaCuadrocombinado7(
        RegistroDataModel listaCuadrocombinado7)
    {
        this.listaCuadrocombinado7 = listaCuadrocombinado7;
    }

    public RegistroDataModel getListaCuadrocombinado7E()
    {
        return listaCuadrocombinado7E;
    }

    public void setListaCuadrocombinado7E(
        RegistroDataModel listaCuadrocombinado7E)
    {
        this.listaCuadrocombinado7E = listaCuadrocombinado7E;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaConcepto()
    {
        return listaConcepto;
    }

    public void setListaConcepto(RegistroDataModelImpl listaConcepto)
    {
        this.listaConcepto = listaConcepto;
    }

    public RegistroDataModelImpl getListaConceptoE()
    {
        return listaConceptoE;
    }

    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE)
    {
        this.listaConceptoE = listaConceptoE;
    }

    public List<Registro> getListaEstratoAlumbrado()
    {
        return listaEstratoAlumbrado;
    }

    public void setListaEstratoAlumbrado(List<Registro> listaEstratoAlumbrado)
    {
        this.listaEstratoAlumbrado = listaEstratoAlumbrado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubsolicituddocpresentado()
    {
        return listaSubsolicituddocpresentado;
    }

    public void setListaSubsolicituddocpresentado(
        List<Registro> listaSubsolicituddocpresentado)
    {
        this.listaSubsolicituddocpresentado = listaSubsolicituddocpresentado;
    }

    public List<Registro> getListaSolicitudsubmateriales()
    {
        return listaSolicitudsubmateriales;
    }

    public void setListaSolicitudsubmateriales(
        List<Registro> listaSolicitudsubmateriales)
    {
        this.listaSolicitudsubmateriales = listaSolicitudsubmateriales;
    }

    public List<Registro> getListaSolicitudserfinanciables()
    {
        return listaSolicitudserfinanciables;
    }

    public void setListaSolicitudserfinanciables(
        List<Registro> listaSolicitudserfinanciables)
    {
        this.listaSolicitudserfinanciables = listaSolicitudserfinanciables;
    }

    public boolean isEliminarSolicitud()
    {
        return eliminarSolicitud;
    }

    public void setEliminarSolicitud(boolean eliminarSolicitud)
    {
        this.eliminarSolicitud = eliminarSolicitud;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSubSubSolicitudDocPresentado()
    {
        return registroSubSubSolicitudDocPresentado;
    }

    public void setRegistroSubSubSolicitudDocPresentado(
        Registro registroSubSubSolicitudDocPresentado)
    {
        this.registroSubSubSolicitudDocPresentado = registroSubSubSolicitudDocPresentado;
    }

    public Registro getRegistroSubSolicitudSubMateriales()
    {
        return registroSubSolicitudSubMateriales;
    }

    public void setRegistroSubSolicitudSubMateriales(
        Registro registroSubSolicitudSubMateriales)
    {
        this.registroSubSolicitudSubMateriales = registroSubSolicitudSubMateriales;
    }

    public Registro getRegistroSubSolicitudSerFinanciables()
    {
        return registroSubSolicitudSerFinanciables;
    }

    public void setRegistroSubSolicitudSerFinanciables(
        Registro registroSubSolicitudSerFinanciables)
    {
        this.registroSubSolicitudSerFinanciables = registroSubSolicitudSerFinanciables;
    }

    public RegistroDataModelImpl getListaDocumentoE()
    {
        return listaDocumentoE;
    }

    public void setListaDocumentoE(RegistroDataModelImpl listaDocumentoE)
    {
        this.listaDocumentoE = listaDocumentoE;
    }

    public String getDescripcionAux()
    {
        return descripcionAux;
    }

    public void setDescripcionAux(String descripcionAux)
    {
        this.descripcionAux = descripcionAux;
    }

    public String getCantidadAux()
    {
        return cantidadAux;
    }

    public void setCantidadAux(String cantidadAux)
    {
        this.cantidadAux = cantidadAux;
    }

    public String getValorUnitario()
    {
        return valorUnitario;
    }

    public void setValorUnitario(String valorUnitario)
    {
        this.valorUnitario = valorUnitario;
    }

    public String getIvaAux()
    {
        return ivaAux;
    }

    public void setIvaAux(String ivaAux)
    {
        this.ivaAux = ivaAux;
    }

    public List<Registro> getListacicloSub()
    {
        return listacicloSub;
    }

    public void setListacicloSub(List<Registro> listacicloSub)
    {
        this.listacicloSub = listacicloSub;
    }

    public String getCicloSubFinanciables()
    {
        return cicloSubFinanciables;
    }

    public void setCicloSubFinanciables(String cicloSubFinanciables)
    {
        this.cicloSubFinanciables = cicloSubFinanciables;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getAnoInicial()
    {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial)
    {
        this.anoInicial = anoInicial;
    }

    public double getAreaConstruido()
    {
        return areaConstruida;
    }

    public void setAreaConstruido(double areaConstruido)
    {
        this.areaConstruida = areaConstruido;
    }

    public double getAreaLibre()
    {
        return areaLibre;
    }

    public void setAreaLibre(double areaLibre)
    {
        this.areaLibre = areaLibre;
    }

    public double getAreaConstruida()
    {
        return areaConstruida;
    }

    public void setAreaConstruida(double areaConstruida)
    {
        this.areaConstruida = areaConstruida;
    }

    public String getNombreTipoVivienda()
    {
        return nombreTipoVivienda;
    }

    public void setNombreTipoVivienda(String nombreTipoVivienda)
    {
        this.nombreTipoVivienda = nombreTipoVivienda;
    }

    public String getNombreTipoPredio()
    {
        return nombreTipoPredio;
    }

    public void setNombreTipoPredio(String nombreTipoPredio)
    {
        this.nombreTipoPredio = nombreTipoPredio;
    }

    public String getAuxiliarNombreDocumentoPresentado()
    {
        return auxiliarNombreDocumentoPresentado;
    }

    public void setAuxiliarNombreDocumentoPresentado(
        String auxiliarNombreDocumentoPresentado)
    {
        this.auxiliarNombreDocumentoPresentado = auxiliarNombreDocumentoPresentado;
    }

    public String getConsecutivoNumeroSolicitudServicio()
    {
        return consecutivoNumeroSolicitudServicio;
    }

    public void setConsecutivoNumeroSolicitudServicio(
        String consecutivoNumeroSolicitudServicio)
    {
        this.consecutivoNumeroSolicitudServicio = consecutivoNumeroSolicitudServicio;
    }

    public String getValortotal()
    {
        return valortotal;
    }

    public void setValortotal(String valortotal)
    {
        this.valortotal = valortotal;
    }

    public int getIndiceSolicitudserfinanciables()
    {
        return indiceSolicitudserfinanciables;
    }

    public void setIndiceSolicitudserfinanciables(
        int indiceSolicitudserfinanciables)
    {
        this.indiceSolicitudserfinanciables = indiceSolicitudserfinanciables;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getSolicitaServicio()
    {
        return solicitaServicio;
    }

    public void setSolicitaServicio(String solicitaServicio)
    {
        this.solicitaServicio = solicitaServicio;
    }

    public String getExisteRedAcueducto()
    {
        return existeRedAcueducto;
    }

    public void setExisteRedAcueducto(String existeRedAcueducto)
    {
        this.existeRedAcueducto = existeRedAcueducto;
    }

    public String getAlcantarillado()
    {
        return alcantarillado;
    }

    public void setAlcantarillado(String alcantarillado)
    {
        this.alcantarillado = alcantarillado;
    }

    public String getExisteRedAlcantarillado()
    {
        return existeRedAlcantarillado;
    }

    public void setExisteRedAlcantarillado(String existeRedAlcantarillado)
    {
        this.existeRedAlcantarillado = existeRedAlcantarillado;
    }

    public String getAseo()
    {
        return aseo;
    }

    public void setAseo(String aseo)
    {
        this.aseo = aseo;
    }

    public boolean isEstratoAlumbrado()
    {
        return estratoAlumbrado;
    }

    public void setEstratoAlumbrado(boolean estratoAlumbrado)
    {
        this.estratoAlumbrado = estratoAlumbrado;
    }

    public boolean isVisibleFechaExpedicion()
    {
        return visibleFechaExpedicion;
    }

    public void setVisibleFechaExpedicion(boolean visibleFechaExpedicion)
    {
        this.visibleFechaExpedicion = visibleFechaExpedicion;
    }

    public String getExisteAcometidaE()
    {
        return existeAcometidaE;
    }

    public void setExisteAcometidaE(String existeAcometidaE)
    {
        this.existeAcometidaE = existeAcometidaE;
    }

    public String getLblExisteAlcantarillado()
    {
        return lblExisteAlcantarillado;
    }

    public void setLblExisteAlcantarillado(String lblExisteAlcantarillado)
    {
        this.lblExisteAlcantarillado = lblExisteAlcantarillado;
    }

    public String getLongFondoPredioE()
    {
        return longFondoPredioE;
    }

    public void setLongFondoPredioE(String longFondoPredioE)
    {
        this.longFondoPredioE = longFondoPredioE;
    }

    public boolean isCmdActivarActivo()
    {
        return cmdActivarActivo;
    }

    public void setCmdActivarActivo(boolean cmdActivarActivo)
    {
        this.cmdActivarActivo = cmdActivarActivo;
    }

    public boolean isActivarCmdCopiar()
    {
        return activarCmdCopiar;
    }

    public void setActivarCmdCopiar(boolean activarCmdCopiar)
    {
        this.activarCmdCopiar = activarCmdCopiar;
    }

    public boolean isBloqueTipoRespuesta()
    {
        return bloqueTipoRespuesta;
    }

    public void setBloqueTipoRespuesta(boolean bloqueTipoRespuesta)
    {
        this.bloqueTipoRespuesta = bloqueTipoRespuesta;
    }

    public boolean isBloqueaIndicadores()
    {
        return bloqueaIndicadores;
    }

    public void setBloqueaIndicadores(boolean bloqueaIndicadores)
    {
        this.bloqueaIndicadores = bloqueaIndicadores;
    }

    public String getTipoFormatoVariable()
    {
        return tipoFormatoVariable;
    }

    public void setTipoFormatoVariable(String tipoFormatoVariable)
    {
        this.tipoFormatoVariable = tipoFormatoVariable;
    }

    public boolean isCargarFormateado()
    {
        return cargarFormateado;
    }

    public void setCargarFormateado(boolean cargarFormateado)
    {
        this.cargarFormateado = cargarFormateado;
    }

    public RegistroDataModelImpl getListacmbFormato()
    {
        return listacmbFormato;
    }

    public void setListacmbFormato(RegistroDataModelImpl listacmbFormato)
    {
        this.listacmbFormato = listacmbFormato;
    }

    public String getCodigoFraude()
    {
        return codigoFraude;
    }

    public void setCodigoFraude(String codigoFraude)
    {
        this.codigoFraude = codigoFraude;
    }

    public Date getFechaOculta()
    {
        return fechaOculta;
    }

    public void setFechaOculta(Date fechaOculta)
    {
        this.fechaOculta = fechaOculta;
    }

    public String getPlantillaWord()
    {
        return plantillaWord;
    }

    public void setPlantillaWord(String plantillaWord)
    {
        this.plantillaWord = plantillaWord;
    }

    public String getNombreDescarga()
    {
        return nombreDescarga;
    }

    public void setNombreDescarga(String nombreDescarga)
    {
        this.nombreDescarga = nombreDescarga;
    }

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
    }

    public String getCodigoRutaNuevo()
    {
        return codigoRutaNuevo;
    }

    public void setCodigoRutaNuevo(String codigoRutaNuevo)
    {
        this.codigoRutaNuevo = codigoRutaNuevo;
    }

    public String getMaximoCaracteresCodigoRuta()
    {
        return maximoCaracteresCodigoRuta;
    }

    public void setMaximoCaracteresCodigoRuta(String maximoCaracteresCodigoRuta)
    {
        this.maximoCaracteresCodigoRuta = maximoCaracteresCodigoRuta;
    }

    public Date getFechaSolicitud()
    {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud)
    {
        this.fechaSolicitud = fechaSolicitud;
    }

    // </SET_GET_ADICIONALES>
}
