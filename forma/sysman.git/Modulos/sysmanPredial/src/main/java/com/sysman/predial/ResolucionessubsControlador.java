package com.sysman.predial;

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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.ResolucionessubsControladorEnum;
import com.sysman.predial.enums.ResolucionessubsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 10/06/2016
 * 
 * @author ybecerra
 * @version 2, 02/08/2017, proceso de Refactoring
 * 
 * @author jcrodriguez
 * @version 3, 14/08/2017 Depuracion del controlador
 * 
 */
@ManagedBean
@ViewScoped

public class ResolucionessubsControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el
     * GeneralParameterEnum.CODIGO.getName() de la compania en la cual
     * inicio sesion el usuario, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /** Constante a nivel de clase que aloja el valor VIGENCIA */

    private String nombre;
    private String documento;
    private String destEcono;
    private String direccion;
    private String tarifa;
    private String anioPago;
    private String hect;
    private String metros;
    private String areaConst;
    private String anioConst;
    private String avaluo;
    private String pagBan;
    private String numCom;
    private String pagFec;
    private String trpPor;
    private String pagoAnio;
    private String pagoVal;
    private String sucursal;
    private boolean propietariosVisible;
    private boolean ultimoAnioBloq;
    private boolean bloqueaCodigo;
    private boolean esNuevo;
    private boolean bloqueaNombre1;
    private boolean bloqueaTipoDocumento;
    private boolean bloqueaNumeroDocumento;
    private boolean bloqueaDestinoEconomico;
    private boolean bloqueaAreahectares;
    private boolean bloqueaAreaTerenoM2;
    private boolean bloqueaAreaConstruida;
    private boolean bloqueaAvaluo;
    private boolean bloqueaCondicionTris;
    private boolean bloqueaTarifa;
    private boolean bloqueaVigencia;
    private boolean bloqueaUltimoAnio;
    private boolean bloqueaConcecutivo;
    private boolean bloqueaComando;
    private boolean bloqueaCopia;
    private boolean bloqueaRegistrar;
    private boolean bloqueaPropietarios;
    private boolean bloqueaNumeroOrden;
    private boolean bloqueaUltimoPago;
    private Registro datosAnteriores;
    private List<Registro> listaUbicacion;
    private List<Registro> listaNumeroOrden;
    private List<Registro> listaTotalRegistros;
    private List<Registro> listaUltimoAnio;
    private List<Registro> listaVigencia;
    private List<Registro> listaAnoConstruccion;
    private RegistroDataModelImpl listaTarifa;
    private RegistroDataModelImpl listaCodigoPadre;
    private RegistroDataModelImpl listaCodigo;
    private String anio;
    private String resolucion;
    private String pais;
    private String departamento;
    private String municipio;
    private Date fecha;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPredialDosRemote ejbSysmanPredialDos;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ResolucionessubsControlador
     */
    public ResolucionessubsControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.RESOLUCIONESSUBS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                anio = validarCadena(parametrosEntrada, ResolucionessubsControladorEnum.ANIO.getValue().toLowerCase());
                resolucion = validarCadena(parametrosEntrada, GeneralParameterEnum.RESOLUCION.getName().toLowerCase());
                pais = validarCadena(parametrosEntrada, ResolucionessubsControladorEnum.PAIS.getValue().toLowerCase());
                departamento = validarCadena(parametrosEntrada, ResolucionessubsControladorEnum.DEPARTAMENTO.getValue().toLowerCase());
                municipio = validarCadena(parametrosEntrada, ResolucionessubsControladorEnum.MUNICIPIO.getValue().toLowerCase());
                fecha = (Date) parametrosEntrada.get(GeneralParameterEnum.FECHA.getName().toLowerCase());
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResolucionessubsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        cargarListaTarifa();
        cargarListaCodigoPadre();
        cargarListaCodigo();
        cargarListaUbicacion();
        cargarListaNumeroOrden();
        cargarListaTotalRegistros();
        cargarListaUltimoAnio();
        cargarListaVigencia();
        cargarListaAnoConstruccion();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
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
    public void inicializar()
    {
        enumBase = GenericUrlEnum.IP_IGAC_RESOLUCIONESDET;
        buscarLlave();
        asignarOrigenDatos();

        esNuevo = false;
        datosAnteriores = new Registro();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.RESOLUCION.getName(),
                        resolucion);
        parametrosListado.put(ResolucionessubsControladorEnum.PAIS.getValue(),
                        pais);

        parametrosListado.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        departamento);
        parametrosListado.put(GeneralParameterEnum.MUNICIPIO.getName(),
                        municipio);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaubicacion
     */
    public void cargarListaUbicacion()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaUbicacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL12369
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaNumeroOrden
     *
     */
    public void cargarListaNumeroOrden()
    {
        try
        {
            listaNumeroOrden = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL12897
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaTotalRegistros
     */
    public void cargarListaTotalRegistros()
    {
        try
        {
            listaTotalRegistros = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL12897
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaUltimoAnio
     */
    public void cargarListaUltimoAnio()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaUltimoAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL13490
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaVigencia
     */
    public void cargarListaVigencia()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL13490
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaAnoConstruccion
     */
    public void cargarListaAnoConstruccion()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAnoConstruccion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL13490
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaTarifa
     */
    public void cargarListaTarifa()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResolucionessubsControladorUrlEnum.URL17577
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaTarifa = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ResolucionessubsControladorEnum.TRPCOD.getValue());

    }

    /**
     * 
     * Carga la lista listaCodigoPadre
     */
    public void cargarListaCodigoPadre()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResolucionessubsControladorUrlEnum.URL18375
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoPadre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigo
     */
    public void cargarListaCodigo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResolucionessubsControladorUrlEnum.URL19822
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CancelaInscribe
     * 
     * 
     */
    public void cambiarCancelaInscribe()
    {
        if ("I".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()), " "))
            && SysmanConstantes.NUMERO_ORDEN_PREDIAL.equals(SysmanFunciones.nvl(
                            registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue()), " ")))
        {
            ultimoAnioBloq = false;
        }
        else
        {
            ultimoAnioBloq = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control UltimoAnio
     * 
     */
    public void cambiarUltimoAnio()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            anioPago = agregarInformacionCampos(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue());
            Registro regAux;

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL18296
                                                                            .getValue())
                                            .getUrl(), param));

            if ((regAux != null) && (regAux.getCampos().get(GeneralParameterEnum.PREANO.getName()) != null)
                && (Integer.parseInt(registro.getCampos().get(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue())
                                .toString()) <= Integer
                                                .parseInt(regAux.getCampos()
                                                                .get(GeneralParameterEnum.PREANO.getName())
                                                                .toString())))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB682")
                    + regAux.getCampos().get(GeneralParameterEnum.PREANO.getName())
                    + idioma.getString("TB_TB683"));
                registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue(),
                                registroIni.get(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue()));
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado al cambiar el control NumeroOrden
     * 
     */
    public void cambiarNumeroOrden()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue()));
        try
        {

            if ("C".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()), " ")))
            {
                List<Registro> aux;

                aux = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ResolucionessubsControladorUrlEnum.URL19743
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (aux.isEmpty())
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1415"));
                }
                else
                {
                    registro.getCampos().put("NOMBRE1",
                                    aux.get(0).getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
                    registro.getCampos().put("TIPODOCUMENTO",
                                    aux.get(0).getCampos().get("TIPO_NIT"));
                    registro.getCampos().put(ResolucionessubsControladorEnum.NUMERODOCUMENTO.getValue(),
                                    aux.get(0).getCampos().get("NIT"));
                    registro.getCampos().put(ResolucionessubsControladorEnum.DESTINOECONOMICO.getValue(), aux.get(0)
                                    .getCampos().get(ResolucionessubsControladorEnum.DESTINO_ECONOMICO.getValue()));
                    registro.getCampos().put(ResolucionessubsControladorEnum.AREAHECTARES.getValue(),
                                    aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AREA_HA.getValue()));
                    registro.getCampos().put(ResolucionessubsControladorEnum.AREATERENOM2.getValue(),
                                    aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AREA_M2.getValue()));
                    registro.getCampos().put(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue(),
                                    aux.get(0).getCampos()
                                                    .get(ResolucionessubsControladorEnum.AREA_CONSTRUIDA.getValue()));
                    registro.getCampos().put(GeneralParameterEnum.AVALUO.getName(),
                                    aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AVALUO_ANO.getValue()));
                    registro.getCampos().put("CONDICIONTRIS",
                                    aux.get(0).getCampos()
                                                    .get("CONDICION_TRIBUTARIA"));
                    registro.getCampos().put(ResolucionessubsControladorEnum.TARIFA.getValue(),
                                    aux.get(0).getCampos().get(ResolucionessubsControladorEnum.TRPCOD.getValue()));
                    registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
                                    aux.get(0).getCampos().get(GeneralParameterEnum.VIGENCIA.getName()));
                    registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue(),
                                    aux.get(0).getCampos().get(ResolucionessubsControladorEnum.PAGO_ANO.getValue()));
                    registro.getCampos().put("ANTRESOLUCION",
                                    aux.get(0).getCampos().get(GeneralParameterEnum.RESOLUCION.getName()));
                }
            }

            if (!"001".equals(registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue())))
            {
                bloqueaNombre1 = false;
                bloqueaTipoDocumento = false;
                bloqueaNumeroDocumento = false;
                bloqueaDestinoEconomico = true;
                bloqueaAreahectares = true;
                bloqueaAreaTerenoM2 = true;
                bloqueaAreaConstruida = true;
                bloqueaAvaluo = true;
                bloqueaCondicionTris = true;
                bloqueaTarifa = true;
                bloqueaVigencia = true;
                bloqueaUltimoAnio = true;
            }
            else
            {
                bloqueaNombre1 = false;
                bloqueaTipoDocumento = false;
                bloqueaNumeroDocumento = false;
                bloqueaDestinoEconomico = false;
                bloqueaAreahectares = false;
                bloqueaAreaTerenoM2 = false;
                bloqueaAreaConstruida = false;
                bloqueaAvaluo = false;
                bloqueaCondicionTris = false;
                bloqueaTarifa = false;
                bloqueaVigencia = false;
                bloqueaUltimoAnio = false;
            }

            if ("I".equals(registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()))
                && SysmanConstantes.NUMERO_ORDEN_PREDIAL
                                .equals(registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue())))
            {
                ultimoAnioBloq = true;
            }
            else
            {
                ultimoAnioBloq = false;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Metodo ejecutado al cambiar el control AnoConstruccion
     * 
     */
    public void cambiarAnoConstruccion()
    {
        anioConst = agregarInformacionCampos(ResolucionessubsControladorEnum.ANO_CONSTRUCCION.getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control Vigencia
     * 
     */
    public void cambiarVigencia()
    {
        if ("I".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()), " "))
            && " ".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue()), " ")))
        {
            registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue(), Integer.parseInt(
                            validarCadena(registro.getCampos(), GeneralParameterEnum.VIGENCIA.getName()))
                - 1);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control NombreUno
     * 
     */
    public void cambiarNombreUno()
    {
        nombre = agregarInformacionCampos(GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * Metodo ejecutado al cambiar el control Codigo
     * 
     */
    public void cambiarCodigo()
    {

        if (registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString().length() == 15)
        {
            registro.getCampos().put(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            cambiarCodigoAux();
            cambiarParteInferirorFormulario();
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2996"));
        }
    }

    /**
     * Metodo ejecutado al cambiar el control CodigoAux
     * 
     */
    private void cambiarCodigoAux()
    {
        registro.getCampos().put(ResolucionessubsControladorEnum.AREAHECTARES.getValue(), 0);
        registro.getCampos().put(ResolucionessubsControladorEnum.AREATERENOM2.getValue(), 0);
        registro.getCampos().put(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue(), 0);
        registro.getCampos().put(GeneralParameterEnum.AVALUO.getName(), "");
        registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_PAGO.getValue(), 0);
        registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue(), 0);
        registro.getCampos().put("TIPOREGISTRO", 1);
        registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), 0);
        registro.getCampos().put(ResolucionessubsControladorEnum.ANO_CONSTRUCCION.getValue(), 0);
    }

    /**
     * Metodo ejecutado al cambiar los controles del formulario.
     * 
     */
    private void cambiarParteInferirorFormulario()
    {
        cambiarAreahectares();
        cambiarAnoConstruccion();
        cambiarAreaConstruida();
        cambiarDestinoEconomico();
        cambiarAvaluo();
        cambiarAreaterrenomDos();
        cambiarNombreUno();
        cambiarNumeroDocumento();
        cambiarDireccion();
        cambiarUltimoAnio();
    }

    /**
     * Metodo ejecutado al cambiar el control Areahectares
     * 
     */
    public void cambiarAreahectares()
    {
        hect = agregarInformacionCampos(ResolucionessubsControladorEnum.AREAHECTARES.getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control AreaConstruida
     * 
     */
    public void cambiarAreaConstruida()
    {
        areaConst = agregarInformacionCampos(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control DestinoEconomico
     * 
     */
    public void cambiarDestinoEconomico()
    {
        destEcono = agregarInformacionCampos(ResolucionessubsControladorEnum.DESTINOECONOMICO.getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control Avaluo
     * 
     */
    public void cambiarAvaluo()
    {
        avaluo = agregarInformacionCampos(GeneralParameterEnum.AVALUO.getName());
    }

    /**
     * Metodo ejecutado al cambiar el control AreaterrenoNomDos
     * 
     */
    public void cambiarAreaterrenomDos()
    {
        metros = agregarInformacionCampos(ResolucionessubsControladorEnum.AREATERENOM2.getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control CondicionTris
     * 
     */
    public void cambiarCondicionTris()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NumeroDocumento
     * 
     */
    public void cambiarNumeroDocumento()
    {
        documento = agregarInformacionCampos(ResolucionessubsControladorEnum.NUMERODOCUMENTO.getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control Direccion
     * 
     */
    public void cambiarDireccion()
    {
        direccion = agregarInformacionCampos(GeneralParameterEnum.DIRECCION.getName());
    }

    /**
     * Metodo ejecutado al cambiar el control UltimoPago
     * 
     */
    public void cambiarUltimoPago()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Tarifa
     * 
     */
    public void cambiarTarifa()
    {
        tarifa = registro.getCampos().get(ResolucionessubsControladorEnum.TARIFA.getValue()).toString();
    }

    /**
     * Metodo ejecutado al cambiar el control CmdPropietarios
     * 
     * 
     */
    public void retornarFormularioCmdPropietarios(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>

        if (event.getObject() != null)
        {
            String mensaje = event.getObject().toString();
            if (mensaje.startsWith("E"))
            {
                JsfUtil.agregarMensajeError(mensaje.replace("M", ""));
            }
            else if (mensaje.startsWith("I"))
            {
                JsfUtil.agregarMensajeInformativo(mensaje.replace("I", ""));
            }
            else if (mensaje.startsWith("N"))
            {
                JsfUtil.agregarMensajeError(mensaje.replace("N", ""));
            }
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTarifa
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTarifa(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        ResolucionessubsControladorEnum.TRPRAN.getValue(),
                        registroAux.getCampos()
                                        .get(ResolucionessubsControladorEnum.TRPRAN
                                                        .getValue()));
        registro.getCampos().put(ResolucionessubsControladorEnum.TARIFA.getValue(),
                        registroAux.getCampos().get(ResolucionessubsControladorEnum.TRPCOD.getValue()));

        trpPor = registroAux.getCampos().get("TRPPOR").toString();
        cambiarTarifa();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoPadre
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoPadre(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        try
        {
            pagBan = actualizarNulos(registroAux.getCampos().get(
                            GeneralParameterEnum.PAG_BAN.getName()));
            numCom = SysmanFunciones
                            .nvlStr(actualizarNulos(registroAux.getCampos().get(
                                            ResolucionessubsControladorEnum.NUM_COM
                                                            .getValue())),
                                            "");

            pagFec = SysmanFunciones.convertirAFechaCadena(
                            (Date) registroAux.getCampos().get("PAG_FEC"));
            pagoAnio = SysmanFunciones
                            .nvlStr(actualizarNulos(registroAux.getCampos()
                                            .get(ResolucionessubsControladorEnum.PAGO_ANO.getValue())),
                                            "");

            pagoVal = registroAux.getCampos().get(ResolucionessubsControladorEnum.PAG_VAL.getValue()).toString();

            registro.getCampos().put(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue(),
                            registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            ResolucionessubsControladorEnum.CODIGO_PADRE.getValue()))
            {

                heredar(registro.getCampos().get(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue()).toString());
                registro.getCampos().put(GeneralParameterEnum.AVALUO.getName(),
                                datosAnteriores.getCampos().get(ResolucionessubsControladorEnum.AVALUO_ANT.getValue()));
                registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_PAGO.getValue(),
                                datosAnteriores.getCampos().get(ResolucionessubsControladorEnum.ULTIMO_PAGO.getValue()));
                registro.getCampos().put(ResolucionessubsControladorEnum.TARIFA.getValue(),
                                datosAnteriores.getCampos().get(ResolucionessubsControladorEnum.ULTIMA_TARIFA.getValue()));

                Map<String, Object> fields = new TreeMap<>();
                fields.put(ResolucionessubsControladorEnum.TRPCOD.getValue(), datosAnteriores.getCampos()
                                .get(ResolucionessubsControladorEnum.ULTIMA_TARIFA.getValue())
                                .toString());

                registro.getCampos().put(ResolucionessubsControladorEnum.TRPRAN
                                .getValue(),
                                SysmanFunciones.validarCampoVacio(
                                                datosAnteriores.getCampos(),
                                                ResolucionessubsControladorEnum.ULTIMA_TARIFA.getValue()) ? ""
                                                    : listaTarifa.getRegistroUnico(
                                                                    fields)
                                                                    .getCampos()
                                                                    .get(ResolucionessubsControladorEnum.TRPRAN
                                                                                    .getValue()));

                registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue(),
                                datosAnteriores.getCampos().get(ResolucionessubsControladorEnum.ULTIMO_ANO.getValue()));
                registro.getCampos().put(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue(),
                                datosAnteriores.getCampos()
                                                .get(ResolucionessubsControladorEnum.AREA_CONSTRUIDA.getValue()));
                registro.getCampos().put(ResolucionessubsControladorEnum.AREAHECTARES.getValue(),
                                datosAnteriores.getCampos().get(ResolucionessubsControladorEnum.AREA_HA.getValue()));
                registro.getCampos().put(ResolucionessubsControladorEnum.AREATERENOM2.getValue(),
                                datosAnteriores.getCampos().get(ResolucionessubsControladorEnum.AREA_M2.getValue()));
                registro.getCampos().put(GeneralParameterEnum.DIRECCION.getName(),
                                datosAnteriores.getCampos().get(GeneralParameterEnum.DIRECCION.getName()));

            }
            else
            {
                registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue(), null);
                registro.getCampos().put(GeneralParameterEnum.AVALUO.getName(), null);
                registro.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_PAGO.getValue(), null);
                registro.getCampos().put(ResolucionessubsControladorEnum.TARIFA.getValue(), null);

            }
            cambiarParteInferirorFormulario();
            cambiarTarifa();
        }
        catch (ParseException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(Registro registroAux)
    {

        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName())
                        .toString();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(ResolucionessubsControladorEnum.NUMEROORDEN.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.NUMERO_ORDEN.getName()));

        Registro regAux = recuperarDatosCodigo(registroAux);

        actualizarCampos(regAux);
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        cambiarCodigoAux();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdRegistrar en la vista
     *
     */
    public void oprimirCmdRegistrar()
    {
        // <CODIGO_DESARROLLADO>
        if (validarInsertarAntes(false))
        {
            agregarRegistroNuevo(false);

            try
            {
                String radicacion = SysmanFunciones
                                .convertirAFechaCadena((Date) registro
                                                .getCampos().get(ResolucionessubsControladorEnum.FECHAINGRESOSISTEMA.getValue()));
                BigDecimal pagBa = new BigDecimal(SysmanFunciones.nvl(pagoVal,
                                "0").toString());
                BigDecimal trpPo = new BigDecimal(SysmanFunciones.nvl(trpPor,
                                "0")
                                .toString());

                String res = ejbSysmanPredialDos
                                .insertarUsuariosDesdeResoluciones(departamento,
                                                municipio, resolucion,
                                                radicacion, compania,
                                                SessionUtil.getUser()
                                                                .getCodigo(),
                                                Integer.parseInt(SysmanFunciones
                                                                .nvl(pagoAnio,
                                                                                "0")
                                                                .toString()),
                                                pagBa,
                                                SysmanFunciones.nvl(pagBan, "")
                                                                .toString(),
                                                SysmanFunciones.nvl(numCom, "")
                                                                .toString(),
                                                registro.getCampos()
                                                                .get(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue())
                                                                .toString(),
                                                SysmanFunciones.nvl(pagFec, "")
                                                                .toString(),
                                                trpPo);

                JsfUtil.agregarMensajeInformativo(res);
                registro.getCampos()
                                .put(ResolucionessubsControladorEnum.REGISTRADOS
                                                .getValue(), "true");
                if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                ResolucionessubsControladorEnum.REGISTRADOS
                                                .getValue()))
                {
                    registro.getCampos().put(
                                    ResolucionessubsControladorEnum.REGISTRADO
                                                    .getValue(),
                                    "true".equals(registro.getCampos()
                                                    .get(ResolucionessubsControladorEnum.REGISTRADOS
                                                                    .getValue())
                                                    .toString()) ? true
                                                        : false);
                }
                cargarRegistro();
            }
            catch (SystemException | NumberFormatException | ParseException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Copia en la vista
     *
     */
    public void oprimirCopia()
    {
        // <CODIGO_DESARROLLADO>
        Object numeroOrdenAux = registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue());

        if (validarInsertarAntes(true))
        {

            Registro copia = new Registro(registro.getCampos());
            long consec = generarConsecutivo();
            int consecutivoAux = Integer.parseInt(
                            registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());
            Object registradosAux = registro.getCampos().get(ResolucionessubsControladorEnum.REGISTRADOS
                            .getValue());
            Object registradoAux = registro.getCampos().get(ResolucionessubsControladorEnum.REGISTRADO
                            .getValue());
            copia.getCampos().put(ResolucionessubsControladorEnum.REGISTRADO
                            .getValue(), 0);
            copia.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), consec);
            copia.getCampos().remove(ResolucionessubsControladorEnum.REGISTRADOS
                            .getValue());

            try
            {

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ResolucionessubsControladorUrlEnum.URL38366
                                                                .getValue());

                Parameter parameter = new Parameter();
                parameter.setFields(copia.getCampos());

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                copia.getCampos());

                registro.getCampos().put(ResolucionessubsControladorEnum.REGISTRADOS
                                .getValue(), registradosAux);

                registro.getCampos().put(ResolucionessubsControladorEnum.REGISTRADO
                                .getValue(), registradoAux);

                registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivoAux);

                registro.getCampos().put(ResolucionessubsControladorEnum.NUMEROORDEN.getValue(), numeroOrdenAux);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1416")
                                    + " " + consec);

            }
            catch (SystemException e)
            {
                Logger.getLogger(ResolucionessubsControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton IngresoAvaluos en la vista
     *
     */
    public void oprimirIngresoAvaluos()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);

        if (SysmanConstantes.NUMERO_ORDEN_PREDIAL
                        .equals(registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue())))
        {
            String[] campos = { "codigo", "norden", GeneralParameterEnum.RESOLUCION.getName().toLowerCase(),
                                "ultimo_anio", "codigoPadre" };
            String[] valores = { registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString(),
                                 registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue())
                                                 .toString(),
                                 registro.getCampos().get(GeneralParameterEnum.RESOLUCION.getName())
                                                 .toString(),
                                 registro.getCampos().get(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue())
                                                 .toString(),
                                 registro.getCampos().get(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue())
                                                 .toString() };
            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.SUBAVALUOSDOS_CONTROLADOR
                                                            .getCodigo()),
                            SessionUtil.getModulo(),
                            campos, valores);
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB684"));
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPropietarios en la
     * vista
     *
     */
    public void oprimirCmdPropietarios()
    {
        agregarRegistroNuevo(false);

        try
        {
            String res = ejbSysmanPredialDos.insertarCopropietarios(compania,
                            pais, departamento, municipio, resolucion,
                            new BigInteger(registro.getCampos()
                                            .get(GeneralParameterEnum.CONSECUTIVO.getName()).toString()),
                            Integer.parseInt(anio),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString(),
                            SessionUtil.getUser().getCodigo());

            if (SysmanFunciones.validarVariableVacio(res))
            {
                String fechaAux;
                fechaAux = SysmanFunciones.convertirAFechaCadena((Date) registro
                                .getCampos().get(ResolucionessubsControladorEnum.FECHAINGRESOSISTEMA.getValue()));
                String[] campos = { "codigo", GeneralParameterEnum.RESOLUCION.getName().toLowerCase(),
                                    "nombre", "fecha",
                                    "compania", "pais", "departamento",
                                    "municipio",
                                    "consecutivo", "ano", "sucursal" };
                String[] valores = { registro.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                                .toString(),
                                     registro.getCampos().get(GeneralParameterEnum.RESOLUCION.getName())
                                                     .toString(),
                                     registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName())
                                                     .toString(),
                                     fechaAux, compania, pais, departamento,
                                     municipio,
                                     registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName())
                                                     .toString(),
                                     anio, sucursal };
                SessionUtil.cargarModalDatosFlash(Integer
                                .toString(GeneralCodigoFormaEnum.FRMPROPIETARIOSIGACS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos, valores);
            }
            else
            {
                JsfUtil.agregarMensajeError(res);
            }
        }
        catch (NumberFormatException | SystemException | ParseException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    public String actualizarNulos(Object campo)
    {
        return campo == null ? null : campo.toString();
    }

    /**
     * Asigna valores a las variables asociadas a los campos
     * 
     * @param regAux
     * Contiene los datos del registro
     */
    public void actualizarCampos(Registro regAux)
    {

        nombre = actualizarNulos(regAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        documento = actualizarNulos(regAux.getCampos().get("NIT"));
        destEcono = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.DESTINO_ECONOMICO.getValue()));
        tarifa = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.TARIFA.getValue()));
        anioPago = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.PAGO_ANO.getValue()));
        direccion = actualizarNulos(regAux.getCampos().get(GeneralParameterEnum.DIRECCION.getName()));
        hect = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.AREA_HA.getValue()));
        metros = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.AREA_M2.getValue()));
        areaConst = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.AREA_CONSTRUIDA.getValue()));
        anioConst = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.ANO_CONSTRUCCION.getValue()));
        avaluo = actualizarNulos(regAux.getCampos().get(ResolucionessubsControladorEnum.AVALUO_ANO.getValue()));
    }

    /**
     * Recupera el registro con los datos del
     * GeneralParameterEnum.CODIGO.getName() seleccionado.
     * 
     * @param registroAux
     * @return
     */
    public Registro recuperarDatosCodigo(Registro registroAux)
    {

        Registro datosCodigo = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NUMERO_ORDEN.getName()));

        try
        {
            datosCodigo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL21646
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return datosCodigo;
    }

    /**
     * Metodo ejecutado al insertar o cargar un registro
     */
    private void bloquearUltimoAno()
    {
        if (registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()) != null)
        {
            if ("I".equals(registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()))
                && SysmanConstantes.NUMERO_ORDEN_PREDIAL
                                .equals(registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue())))
            {
                ultimoAnioBloq = false;
            }
            else
            {
                ultimoAnioBloq = true;
            }
        }
        else
        {
            ultimoAnioBloq = true;
        }
    }

    /**
     * Metodo ejecutado al cargar un registro
     */
    private void bloquearCodigo()
    {
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        ResolucionessubsControladorEnum.REGISTRADO
                                        .getValue()))
        {
            if ((boolean) registro.getCampos()
                            .get(ResolucionessubsControladorEnum.REGISTRADO
                                            .getValue()))
            {
                bloqueaCodigo = true;
            }
            else
            {
                bloqueaCodigo = false;
            }
        }
        else
        {
            bloqueaCodigo = false;
        }
        if ("i".equals(accion))
        {
            bloqueaCodigo = false;
            bloquearDesbloquear(false, false, true, false, true);
        }
    }

    /**
     * Metodo ejecutado al insertar o cargar un registro
     * 
     * @param insertarActivar
     * @param modificarActivar
     * @param btnMActivar
     * @param btnIActivar
     * @param btnAvaluo
     */
    private void bloquearDesbloquear(boolean insertarActivar,
        boolean modificarActivar, boolean btnMActivar, boolean btnIActivar,
        boolean btnAvaluo)
    {
        bloqueaConcecutivo = modificarActivar;
        bloqueaCodigo = modificarActivar;
        bloqueaNumeroOrden = modificarActivar;
        bloqueaNombre1 = modificarActivar;
        bloqueaNumeroDocumento = modificarActivar;
        bloqueaDestinoEconomico = insertarActivar;
        bloqueaVigencia = insertarActivar;
        bloqueaCondicionTris = insertarActivar;
        bloqueaTarifa = insertarActivar;
        bloqueaUltimoAnio = insertarActivar;
        bloqueaUltimoPago = modificarActivar;
        bloqueaAreahectares = insertarActivar;
        bloqueaAreaTerenoM2 = insertarActivar;
        bloqueaAreaConstruida = insertarActivar;
        bloqueaAvaluo = btnAvaluo;
        bloqueaTipoDocumento = modificarActivar;
        bloqueaPropietarios = btnMActivar;
        bloqueaComando = btnMActivar;
        bloqueaCopia = btnMActivar;
        bloqueaRegistrar = btnIActivar;
    }

    /**
     * Metodo ejecutado al cargar un registro
     */
    public void actualizarCampos()
    {
        nombre = actualizarNulos(registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        documento = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.NUMERODOCUMENTO.getValue()));
        destEcono = actualizarNulos(
                        registro.getCampos().get(ResolucionessubsControladorEnum.DESTINOECONOMICO.getValue()));
        tarifa = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.TARIFA.getValue()));
        anioPago = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.ULTIMO_ANIO.getValue()));
        direccion = actualizarNulos(registro.getCampos().get(GeneralParameterEnum.DIRECCION.getName()));
        hect = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.AREAHECTARES.getValue()));
        metros = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.AREATERENOM2.getValue()));
        areaConst = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue()));
        anioConst = actualizarNulos(registro.getCampos().get(ResolucionessubsControladorEnum.ANO_CONSTRUCCION.getValue()));
        avaluo = actualizarNulos(registro.getCampos().get(GeneralParameterEnum.AVALUO.getName()));
    }

    /**
     * Metodo ejecutado antes de insertar un registro, o al oprimir
     * los botones de registrar y copia
     * 
     * @return true
     */
    private boolean validarInsertarAntes(boolean opcion)
    {
        if ("0.00".equals(registro.getCampos().get(GeneralParameterEnum.AVALUO.getName()).toString()))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB1424"));
            return false;
        }
        if (opcion)
        {
            generarConsecutivoNumeroOrden();
        }

        return true;
    }

    private boolean consultarConsecutivoNumeroOrden()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue()));
        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
        param.put(GeneralParameterEnum.RESOLUCION.getName(), resolucion);
        Registro reg = null;

        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL4703
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg != null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3460"));
                return true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    private void generarConsecutivoNumeroOrden()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        param.put(GeneralParameterEnum.RESOLUCION.getName(), resolucion);
        Registro reg = null;
        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL70824
                                                                            .getValue())
                                            .getUrl(), param));
            if (!SysmanFunciones.validarCampoVacio(reg.getCampos(), GeneralParameterEnum.NUMERO.getName()))
            {
                registro.getCampos().put(ResolucionessubsControladorEnum.NUMEROORDEN.getValue(),
                                reg.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
            }
            else
            {
                registro.getCampos().put(ResolucionessubsControladorEnum.NUMEROORDEN.getValue(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al insertar o editar registro
     * 
     * @return true
     */
    private boolean validarCancela()
    {
        if ("I".equals(SysmanFunciones
                        .nvl(registro.getCampos().get(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue()), ""))
            && SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            ResolucionessubsControladorEnum.CODIGO_PADRE.getValue()))
        {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1426"));
            return false;
        }
        return true;
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @return true
     */
    private boolean validarAvaluo()
    {
        BigInteger validaAvaluo = new BigInteger(
                        registro.getCampos().get(GeneralParameterEnum.AVALUO.getName()).toString());
        if (validaAvaluo.compareTo(BigInteger.ZERO) <= 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1424"));
            return false;
        }
        return true;
    }

    /**
     * Metodo ejecutado al editar un registro
     */
    private void removerCampos()
    {
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        ResolucionessubsControladorEnum.REGISTRADOS.getValue()))
        {
            registro.getCampos().put(ResolucionessubsControladorEnum.REGISTRADO
                            .getValue(),
                            "true".equals(registro.getCampos()
                                            .get(ResolucionessubsControladorEnum.REGISTRADOS
                                                            .getValue())
                                            .toString()) ? -1
                                                : 0);
            registro.getCampos()
                            .remove(ResolucionessubsControladorEnum.REGISTRADOS
                                            .getValue());
        }

        if (ACCION_MODIFICAR.equals(accion))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(
                            ResolucionessubsControladorEnum.PAIS.getValue());
            registro.getCampos().remove(
                            GeneralParameterEnum.DEPARTAMENTO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.MUNICIPIO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.RESOLUCION.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());

        }
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @return true
     */
    private boolean validarAreasConstruccion()
    {
        return Integer.parseInt(SysmanFunciones.nvlStr(areaConst, "0")) == 0
            && !"0".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue()), "0"))
            && "0".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(ResolucionessubsControladorEnum.ANO_CONSTRUCCION.getValue()),
                            "0"));
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @return true
     */
    private boolean validarCamposVacios()
    {
        if (!validarCodigos())
        {
            return false;
        }

        if (!validarAreas())
        {
            return false;
        }
        if (!validarObligatorios())
        {
            return false;
        }
        return true;
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @return true
     */
    private boolean validarObligatorios()
    {
        return validarVacio(ResolucionessubsControladorEnum.CANCELAINSCRIBE.getValue(), "TB_TB1425")
            || validarVacio(ResolucionessubsControladorEnum.NUMERODOCUMENTO.getValue(), "TB_TB1432")
            || validarVacio(GeneralParameterEnum.AVALUO.getName(), "TB_TB1423");
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @return true
     */
    private boolean validarAreas()
    {
        return validarVacio(ResolucionessubsControladorEnum.AREAHECTARES.getValue(), "TB_TB1421")
            || validarVacio(ResolucionessubsControladorEnum.AREATERENOM2.getValue(), "TB_TB1422")
            || validarVacio(ResolucionessubsControladorEnum.AREACONSTRUIDA.getValue(), "TB_TB1420");
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @return true
     */
    private boolean validarCodigos()
    {
        return validarVacio(GeneralParameterEnum.CODIGO.getName(), "TB_TB1418")
            || validarVacio(ResolucionessubsControladorEnum.NUMEROORDEN.getValue(), "TB_TB1419")
            || validarVacio(ResolucionessubsControladorEnum.NUMERODOCUMENTO.getValue(), "TB_TB1433");
    }

    /**
     * Metodo ejecutado al insertar o editar un registro
     * 
     * @param var
     * @param mensaje
     * @return true
     */
    private boolean validarVacio(String var, String mensaje)
    {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), var))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensaje));
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Metodo ejecutado al insertar un registro o en el oprimir del
     * boton registrar
     * 
     * @return consecutivo de la tabla IP_IGAC_RESOLUCIONESDET
     */
    public long generarConsecutivo()
    {

        long aux = 0;
        try
        {
            String[] cadena = { "IP_IGAC_RESOLUCIONESDET.COMPANIA =''",
                                compania, "''",
                                " AND IP_IGAC_RESOLUCIONESDET.ANO =", anio,
                                " AND IP_IGAC_RESOLUCIONESDET.RESOLUCION = ''",
                                resolucion,
                                "'' AND IP_IGAC_RESOLUCIONESDET.PAIS=''", pais,
                                "'' AND IP_IGAC_RESOLUCIONESDET.DEPARTAMENTO=''",
                                departamento,
                                "'' AND IP_IGAC_RESOLUCIONESDET.MUNICIPIO=''",
                                municipio, "''"

            };
            aux = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "IP_IGAC_RESOLUCIONESDET",
                            SysmanFunciones.concatenar(cadena),
                            GeneralParameterEnum.CONSECUTIVO.getName(), "1");

        }
        catch (SystemException ex)
        {
            String[] cadena = { idioma.getString("MSM_TRANS_INTERRUMPIDA"), "",
                                idioma.getString("TB_TB688"), ex.getMessage()

            };
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(cadena));
            Logger.getLogger(ResolucionessubsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        return aux;
    }

    /**
     * Metodo ejecutado al seleccionar un
     * GeneralParameterEnum.CODIGO.getName() de predio de la vista
     * 
     * @param GeneralParameterEnum.CODIGO.getName()
     */
    public void heredar(String codigo)
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PREDIO.getName(), codigo);
            List<Registro> aux;

            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResolucionessubsControladorUrlEnum.URL70836
                                                                            .getValue())
                                            .getUrl(), param));

            if (!aux.isEmpty())
            {
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.CODIGO_ANT.getValue(),
                                aux.get(0).getCampos().get(GeneralParameterEnum.CODIGO.getName()));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AVALUO_ANT.getValue(),
                                aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AVALUO_ANO.getValue()));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANO.getValue(),
                                aux.get(0).getCampos().get(ResolucionessubsControladorEnum.PAGO_ANO.getValue()));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_PAGO.getValue(), SysmanFunciones
                                .nvl(aux.get(0).getCampos().get(ResolucionessubsControladorEnum.PAG_VAL.getValue()), 0));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.ULTIMA_TARIFA.getValue(), SysmanFunciones
                                .nvl(aux.get(0).getCampos().get(ResolucionessubsControladorEnum.TRPCOD.getValue()), ""));
                datosAnteriores.getCampos().put(GeneralParameterEnum.DIRECCION.getName(),
                                aux.get(0).getCampos().get(GeneralParameterEnum.DIRECCION.getName()));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AREA_HA.getValue(),
                                aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AREA_HA.getValue()));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AREA_M2.getValue(),
                                aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AREA_M2.getValue()));
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AREA_CONSTRUIDA.getValue(),
                                aux.get(0).getCampos().get(ResolucionessubsControladorEnum.AREA_CONSTRUIDA.getValue()));
            }
            else
            {
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.CODIGO_ANT.getValue(), " ");
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AVALUO_ANT.getValue(), 0);
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_ANO.getValue(), 0);
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.ULTIMO_PAGO.getValue(), 0);
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.ULTIMA_TARIFA.getValue(), " ");
                datosAnteriores.getCampos().put(GeneralParameterEnum.DIRECCION.getName(), " ");
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AREA_HA.getValue(), 0);
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AREA_M2.getValue(), 0);
                datosAnteriores.getCampos().put(ResolucionessubsControladorEnum.AREA_CONSTRUIDA.getValue(), 0);
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado para visualizar campos en la parte inferior
     * para los campos informativos
     * 
     * @param nombreCampo
     * @return valor del campo editado
     */
    private String agregarInformacionCampos(String nombreCampo)
    {
        return SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        nombreCampo) ? ""
                            : registro.getCampos().get(nombreCampo).toString();
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MODIFICACIONES CO-PROPIETARIOS RESOLUCIONES IGAC",
                                            SessionUtil.getModulo(), new Date(),
                                            true)

            , "NO")))
            {
                propietariosVisible = true;
            }
            else
            {
                propietariosVisible = false;

            }
        }
        // </CODIGO_DESARROLLADO>
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        Registro reg;
        Registro regAux;
        registro.getCampos().put(ResolucionessubsControladorEnum.FECHAINGRESOSISTEMA.getValue(), fecha);
        bloqueaCodigo = false;
        ultimoAnioBloq = false;

        if (ACCION_MODIFICAR.equals(accion))
        {
            bloquearUltimoAno();
            if ((boolean) registro.getCampos()
                            .get(ResolucionessubsControladorEnum.REGISTRADO
                                            .getValue()))
            {
                bloquearDesbloquear(true, true, false, true, false);
            }
            else
            {
                bloquearDesbloquear(false, false, false, false, true);
            }
            try
            {

                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(ResolucionessubsControladorEnum.CODIGO_PADRE.getValue()));

                reg = listaCodigoPadre.getRegistroUnico(fields);
                if (reg != null)
                {
                    pagBan = SysmanFunciones.validarCampoVacio(reg.getCampos(),
                                    GeneralParameterEnum.PAG_BAN.getName()) ? ""
                                        : reg.getCampos()
                                                        .get(GeneralParameterEnum.PAG_BAN
                                                                        .getName())
                                                        .toString();
                    numCom = SysmanFunciones.validarCampoVacio(reg.getCampos(),
                                    ResolucionessubsControladorEnum.NUM_COM
                                                    .getValue()) ? ""
                                                        : reg.getCampos()
                                                                        .get(ResolucionessubsControladorEnum.NUM_COM
                                                                                        .getValue())
                                                                        .toString();
                    pagFec = SysmanFunciones.convertirAFechaCadena(
                                    (Date) reg.getCampos().get("PAG_FEC"));
                    pagoAnio = SysmanFunciones.validarCampoVacio(
                                    reg.getCampos(), ResolucionessubsControladorEnum.PAGO_ANO.getValue()) ? "0"
                                        : reg.getCampos().get(ResolucionessubsControladorEnum.PAGO_ANO.getValue())
                                                        .toString();
                    pagoVal = SysmanFunciones.validarCampoVacio(reg.getCampos(),
                                    ResolucionessubsControladorEnum.PAG_VAL.getValue()) ? "0"
                                        : reg.getCampos().get(ResolucionessubsControladorEnum.PAG_VAL.getValue())
                                                        .toString();
                    Map<String, Object> condicion = new TreeMap<>();
                    fields.put(ResolucionessubsControladorEnum.TRPCOD.getValue(),
                                    registro.getCampos().get(ResolucionessubsControladorEnum.TARIFA.getValue()));
                    fields.put("TRPANO", anio);

                    regAux = listaTarifa.getRegistroUnico(condicion);
                    trpPor = ((BigInteger) regAux.getCampos().get("TRPPOR"))
                                    .toString();
                }
            }
            catch (ParseException | SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

        }

        bloquearCodigo();

        actualizarCampos();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (validarInsertarAntes(true))
        {
            long consecutivo = generarConsecutivo();

            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(
                            ResolucionessubsControladorEnum.PAIS.getValue(),
                            pais);
            registro.getCampos().put(
                            GeneralParameterEnum.DEPARTAMENTO.getName(),
                            departamento);
            registro.getCampos().put(GeneralParameterEnum.MUNICIPIO.getName(),
                            municipio);
            registro.getCampos().put(GeneralParameterEnum.RESOLUCION.getName(), resolucion);
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);

        }
        else
        {
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
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
    public boolean actualizarAntes()
    {

        // <CODIGO_DESARROLLADO>

        if (consultarConsecutivoNumeroOrden())
        {
            return false;
        }
        removerCampos();

        if (!validarCamposVacios() || !validarAvaluo() || !validarCancela())
        {
            return false;
        }

        if (validarAreasConstruccion())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1428"));
            return false;
        }

        if (esNuevo
            && !"001".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(ResolucionessubsControladorEnum.NUMEROORDEN.getValue()), "")))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1429") + " "
                + registro.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                + " " + idioma.getString("TB_TB1431"));
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos()
                        .get(ResolucionessubsControladorEnum.REGISTRADO
                                        .getValue()))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB685"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar()
    {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.RESOLUCIONES_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre()
    {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable documento
     * 
     * @return documento
     */
    public String getDocumento()
    {
        return documento;
    }

    /**
     * Asigna la variable documento
     * 
     * @param documento
     * Variable a asignar en documento
     */
    public void setDocumento(String documento)
    {
        this.documento = documento;
    }

    /**
     * Retorna la variable destEcono
     * 
     * @return destEcono
     */
    public String getDestEcono()
    {
        return destEcono;
    }

    /**
     * Asigna la variable destEcono
     * 
     * @param destEcono
     * Variable a asignar en destEcono
     */
    public void setDestEcono(String destEcono)
    {
        this.destEcono = destEcono;
    }

    /**
     * Retorna la variable direccion
     * 
     * @return direccion
     */
    public String getDireccion()
    {
        return direccion;
    }

    /**
     * Asigna la variable direccion
     * 
     * @param direccion
     * Variable a asignar en direccion
     */
    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }

    /**
     * Retorna la variable tarifa
     * 
     * @return tarifa
     */
    public String getTarifa()
    {
        return tarifa;
    }

    /**
     * Asigna la variable tarifa
     * 
     * @param tarifa
     * Variable a asignar en tarifa
     */
    public void setTarifa(String tarifa)
    {
        this.tarifa = tarifa;
    }

    /**
     * Retorna la variable anioPago
     * 
     * @return anioPago
     */
    public String getAnioPago()
    {
        return anioPago;
    }

    /**
     * Asigna la variable anioPago
     * 
     * @param anioPago
     * Variable a asignar en anioPago
     */
    public void setAnioPago(String anioPago)
    {
        this.anioPago = anioPago;
    }

    /**
     * Retorna la variable hect
     * 
     * @return hect
     */
    public String getHect()
    {
        return hect;
    }

    /**
     * Asigna la variable hect
     * 
     * @param hect
     * Variable a asignar en hect
     */
    public void setHect(String hect)
    {
        this.hect = hect;
    }

    /**
     * Retorna la variable metros
     * 
     * @return metros
     */
    public String getMetros()
    {
        return metros;
    }

    /**
     * Asigna la variable metros
     * 
     * @param metros
     * Variable a asignar en metros
     */
    public void setMetros(String metros)
    {
        this.metros = metros;
    }

    /**
     * Retorna la variable areaConst
     * 
     * @return areaConst
     */
    public String getAreaConst()
    {
        return areaConst;
    }

    /**
     * Asigna la variable areaConst
     * 
     * @param areaConst
     * Variable a asignar en areaConst
     */
    public void setAreaConst(String areaConst)
    {
        this.areaConst = areaConst;
    }

    /**
     * Retorna la variable avaluo
     * 
     * @return avaluo
     */
    public String getAvaluo()
    {
        return avaluo;
    }

    /**
     * Asigna la variable avaluo
     * 
     * @param avaluo
     * Variable a asignar en avaluo
     */
    public void setAvaluo(String avaluo)
    {
        this.avaluo = avaluo;
    }

    /**
     * Retorna la variable propietariosVisible
     * 
     * @return propietariosVisible
     */
    public boolean isPropietariosVisible()
    {
        return propietariosVisible;
    }

    /**
     * Asigna la variable propietariosVisible
     * 
     * @param propietariosVisible
     * Variable a asignar en propietariosVisible
     */
    public void setPropietariosVisible(boolean propietariosVisible)
    {
        this.propietariosVisible = propietariosVisible;
    }

    /**
     * Retorna la variable anioConst
     * 
     * @return anioConst
     */
    public String getAnioConst()
    {
        return anioConst;
    }

    /**
     * Asigna la variable anioConst
     * 
     * @param anioConst
     * Variable a asignar en anioConst
     */
    public void setAnioConst(String anioConst)
    {
        this.anioConst = anioConst;
    }

    /**
     * Retorna la variable ultimoAnioBloq
     * 
     * @return ultimoAnioBloq
     */
    public boolean isUltimoAnioBloq()
    {
        return ultimoAnioBloq;
    }

    /**
     * Asigna la variable ultimoAnioBloq
     * 
     * @param ultimoAnioBloq
     * Variable a asignar en ultimoAnioBloq
     */
    public void setUltimoAnioBloq(boolean ultimoAnioBloq)
    {
        this.ultimoAnioBloq = ultimoAnioBloq;
    }

    /**
     * Retorna la variable bloqueaCodigo
     * 
     * @return bloqueaCodigo
     */
    public boolean isBloqueaCodigo()
    {
        return bloqueaCodigo;
    }

    /**
     * Asigna la variable bloqueaCodigo
     * 
     * @param bloqueaCodigo
     * Variable a asignar en bloqueaCodigo
     */
    public void setBloqueaCodigo(boolean bloqueaCodigo)
    {
        this.bloqueaCodigo = bloqueaCodigo;
    }

    /**
     * Retorna la variable esNuevo
     * 
     * @return esNuevo
     */
    public boolean isEsNuevo()
    {
        return esNuevo;
    }

    /**
     * Asigna la variable esNuevo
     * 
     * @param esNuevo
     * Variable a asignar en esNuevo
     */
    public void setEsNuevo(boolean esNuevo)
    {
        this.esNuevo = esNuevo;
    }

    /**
     * Retorna la variable bloqueaNombre1
     * 
     * @return bloqueaNombre1
     */
    public boolean isBloqueaNombre1()
    {
        return bloqueaNombre1;
    }

    /**
     * Asigna la variable bloqueaNombre1
     * 
     * @param bloqueaNombre1
     * Variable a asignar en bloqueaNombre1
     */
    public void setBloqueaNombre1(boolean bloqueaNombre1)
    {
        this.bloqueaNombre1 = bloqueaNombre1;
    }

    /**
     * Retorna la variable bloqueaTipoDocumento
     * 
     * @return bloqueaTipoDocumento
     */
    public boolean isBloqueaTipoDocumento()
    {
        return bloqueaTipoDocumento;
    }

    /**
     * Asigna la variable bloqueaTipoDocumento
     * 
     * @param bloqueaTipoDocumento
     * Variable a asignar en bloqueaTipoDocumento
     */
    public void setBloqueaTipoDocumento(boolean bloqueaTipoDocumento)
    {
        this.bloqueaTipoDocumento = bloqueaTipoDocumento;
    }

    /**
     * Retorna la variable bloqueaNumeroDocumento
     * 
     * @return bloqueaNumeroDocumento
     */
    public boolean isBloqueaNumeroDocumento()
    {
        return bloqueaNumeroDocumento;
    }

    /**
     * Asigna la variable bloqueaNumeroDocumento
     * 
     * @param bloqueaNumeroDocumento
     * Variable a asignar en bloqueaNumeroDocumento
     */
    public void setBloqueaNumeroDocumento(boolean bloqueaNumeroDocumento)
    {
        this.bloqueaNumeroDocumento = bloqueaNumeroDocumento;
    }

    /**
     * Retorna la variable bloqueaDestinoEconomico
     * 
     * @return bloqueaDestinoEconomico
     */
    public boolean isBloqueaDestinoEconomico()
    {
        return bloqueaDestinoEconomico;
    }

    /**
     * Asigna la variable bloqueaDestinoEconomico
     * 
     * @param bloqueaDestinoEconomico
     * Variable a asignar en bloqueaDestinoEconomico
     */
    public void setBloqueaDestinoEconomico(boolean bloqueaDestinoEconomico)
    {
        this.bloqueaDestinoEconomico = bloqueaDestinoEconomico;
    }

    /**
     * Retorna la variable bloqueaAreahectares
     * 
     * @return bloqueaAreahectares
     */
    public boolean isBloqueaAreahectares()
    {
        return bloqueaAreahectares;
    }

    /**
     * Asigna la variable bloqueaAreahectares
     * 
     * @param bloqueaAreahectares
     * Variable a asignar en bloqueaAreahectares
     */
    public void setBloqueaAreahectares(boolean bloqueaAreahectares)
    {
        this.bloqueaAreahectares = bloqueaAreahectares;
    }

    /**
     * Retorna la variable bloqueaAreaTerenoM2
     * 
     * @return bloqueaAreaTerenoM2
     */
    public boolean isBloqueaAreaTerenoM2()
    {
        return bloqueaAreaTerenoM2;
    }

    /**
     * Asigna la variable bloqueaAreaTerenoM2
     * 
     * @param bloqueaAreaTerenoM2
     * Variable a asignar en bloqueaAreaTerenoM2
     */
    public void setBloqueaAreaTerenoM2(boolean bloqueaAreaTerenoM2)
    {
        this.bloqueaAreaTerenoM2 = bloqueaAreaTerenoM2;
    }

    /**
     * Retorna la variable bloqueaAreaConstruida
     * 
     * @return bloqueaAreaConstruida
     */
    public boolean isBloqueaAreaConstruida()
    {
        return bloqueaAreaConstruida;
    }

    /**
     * Asigna la variable bloqueaAreaConstruida
     * 
     * @param bloqueaAreaConstruida
     * Variable a asignar en bloqueaAreaConstruida
     */
    public void setBloqueaAreaConstruida(boolean bloqueaAreaConstruida)
    {
        this.bloqueaAreaConstruida = bloqueaAreaConstruida;
    }

    /**
     * Retorna la variable bloqueaAvaluo
     * 
     * @return bloqueaAvaluo
     */
    public boolean isBloqueaAvaluo()
    {
        return bloqueaAvaluo;
    }

    /**
     * Asigna la variable bloqueaAvaluo
     * 
     * @param bloqueaAvaluo
     * Variable a asignar en bloqueaAvaluo
     */
    public void setBloqueaAvaluo(boolean bloqueaAvaluo)
    {
        this.bloqueaAvaluo = bloqueaAvaluo;
    }

    /**
     * Retorna la variable bloqueaCondicionTris
     * 
     * @return bloqueaCondicionTris
     */
    public boolean isBloqueaCondicionTris()
    {
        return bloqueaCondicionTris;
    }

    /**
     * Asigna la variable bloqueaCondicionTris
     * 
     * @param bloqueaCondicionTris
     * Variable a asignar en bloqueaCondicionTris
     */
    public void setBloqueaCondicionTris(boolean bloqueaCondicionTris)
    {
        this.bloqueaCondicionTris = bloqueaCondicionTris;
    }

    /**
     * Retorna la variable bloqueaTarifa
     * 
     * @return bloqueaTarifa
     */
    public boolean isBloqueaTarifa()
    {
        return bloqueaTarifa;
    }

    /**
     * Asigna la variable bloqueaTarifa
     * 
     * @param bloqueaTarifa
     * Variable a asignar en bloqueaTarifa
     */
    public void setBloqueaTarifa(boolean bloqueaTarifa)
    {
        this.bloqueaTarifa = bloqueaTarifa;
    }

    /**
     * Retorna la variable bloqueaVigencia
     * 
     * @return bloqueaVigencia
     */
    public boolean isBloqueaVigencia()
    {
        return bloqueaVigencia;
    }

    /**
     * Asigna la variable bloqueaVigencia
     * 
     * @param bloqueaVigencia
     * Variable a asignar en bloqueaVigencia
     */
    public void setBloqueaVigencia(boolean bloqueaVigencia)
    {
        this.bloqueaVigencia = bloqueaVigencia;
    }

    /**
     * Retorna la variable bloqueaUltimoAnio
     * 
     * @return bloqueaUltimoAnio
     */
    public boolean isBloqueaUltimoAnio()
    {
        return bloqueaUltimoAnio;
    }

    /**
     * Asigna la variable bloqueaUltimoAnio
     * 
     * @param bloqueaUltimoAnio
     * Variable a asignar en bloqueaUltimoAnio
     */
    public void setBloqueaUltimoAnio(boolean bloqueaUltimoAnio)
    {
        this.bloqueaUltimoAnio = bloqueaUltimoAnio;
    }

    /**
     * Retorna la variable bloqueaConcecutivo
     * 
     * @return bloqueaConcecutivo
     */
    public boolean isBloqueaConcecutivo()
    {
        return bloqueaConcecutivo;
    }

    /**
     * Asigna la variable bloqueaConcecutivo
     * 
     * @param bloqueaConcecutivo
     * Variable a asignar en bloqueaConcecutivo
     */
    public void setBloqueaConcecutivo(boolean bloqueaConcecutivo)
    {
        this.bloqueaConcecutivo = bloqueaConcecutivo;
    }

    /**
     * Retorna la variable bloqueaComando
     * 
     * @return bloqueaComando
     */
    public boolean isBloqueaComando()
    {
        return bloqueaComando;
    }

    /**
     * Asigna la variable bloqueaComando
     * 
     * @param bloqueaComando
     * Variable a asignar en bloqueaComando
     */
    public void setBloqueaComando(boolean bloqueaComando)
    {
        this.bloqueaComando = bloqueaComando;
    }

    /**
     * Retorna la variable bloqueaCopia
     * 
     * @return bloqueaCopia
     */
    public boolean isBloqueaCopia()
    {
        return bloqueaCopia;
    }

    /**
     * Asigna la variable bloqueaCopia
     * 
     * @param bloqueaCopia
     * Variable a asignar en bloqueaCopia
     */
    public void setBloqueaCopia(boolean bloqueaCopia)
    {
        this.bloqueaCopia = bloqueaCopia;
    }

    /**
     * Retorna la variable bloqueaRegistrar
     * 
     * @return bloqueaRegistrar
     */
    public boolean isBloqueaRegistrar()
    {
        return bloqueaRegistrar;
    }

    /**
     * Asigna la variable bloqueaRegistrar
     * 
     * @param bloqueaRegistrar
     * Variable a asignar en bloqueaRegistrar
     */
    public void setBloqueaRegistrar(boolean bloqueaRegistrar)
    {
        this.bloqueaRegistrar = bloqueaRegistrar;
    }

    /**
     * Retorna la variable bloqueaPropietarios
     * 
     * @return bloqueaPropietarios
     */
    public boolean isBloqueaPropietarios()
    {
        return bloqueaPropietarios;
    }

    /**
     * Asigna la variable bloqueaPropietarios
     * 
     * @param bloqueaPropietarios
     * Variable a asignar en bloqueaPropietarios
     */
    public void setBloqueaPropietarios(boolean bloqueaPropietarios)
    {
        this.bloqueaPropietarios = bloqueaPropietarios;
    }

    /**
     * Retorna la variable bloqueaNumeroOrden
     * 
     * @return bloqueaNumeroOrden
     */
    public boolean isBloqueaNumeroOrden()
    {
        return bloqueaNumeroOrden;
    }

    /**
     * Asigna la variable bloqueaNumeroOrden
     * 
     * @param bloqueaNumeroOrden
     * Variable a asignar en bloqueaNumeroOrden
     */
    public void setBloqueaNumeroOrden(boolean bloqueaNumeroOrden)
    {
        this.bloqueaNumeroOrden = bloqueaNumeroOrden;
    }

    /**
     * Retorna la variable bloqueaUltimoPago
     * 
     * @return bloqueaUltimoPago
     */
    public boolean isBloqueaUltimoPago()
    {
        return bloqueaUltimoPago;
    }

    /**
     * Asigna la variable bloqueaUltimoPago
     * 
     * @param bloqueaUltimoPago
     * Variable a asignar en bloqueaUltimoPago
     */
    public void setBloqueaUltimoPago(boolean bloqueaUltimoPago)
    {
        this.bloqueaUltimoPago = bloqueaUltimoPago;
    }

    /**
     * Retorna la lista listaubicacion
     * 
     * @return listaubicacion
     */
    public List<Registro> getListaUbicacion()
    {
        return listaUbicacion;
    }

    /**
     * Asigna la lista listaubicacion
     * 
     * @param listaubicacion
     * Variable a asignar en listaubicacion
     */
    public void setListaUbicacion(List<Registro> listaUbicacion)
    {
        this.listaUbicacion = listaUbicacion;
    }

    /**
     * Retorna la lista listaNumeroOrden
     * 
     * @return listaNumeroOrden
     */
    public List<Registro> getListaNumeroOrden()
    {
        return listaNumeroOrden;
    }

    /**
     * Asigna la lista listaNumeroOrden
     * 
     * @param listaNumeroOrden
     * Variable a asignar en listaNumeroOrden
     */
    public void setListaNumeroOrden(List<Registro> listaNumeroOrden)
    {
        this.listaNumeroOrden = listaNumeroOrden;
    }

    /**
     * Retorna la lista listaTotalRegistros
     * 
     * @return listaTotalRegistros
     */
    public List<Registro> getListaTotalRegistros()
    {
        return listaTotalRegistros;
    }

    /**
     * Asigna la lista listaTotalRegistros
     * 
     * @param listaTotalRegistros
     * Variable a asignar en listaTotalRegistros
     */
    public void setListaTotalRegistros(List<Registro> listaTotalRegistros)
    {
        this.listaTotalRegistros = listaTotalRegistros;
    }

    /**
     * Retorna la lista listaUltimoAnio
     * 
     * @return listaUltimoAnio
     */
    public List<Registro> getListaUltimoAnio()
    {
        return listaUltimoAnio;
    }

    /**
     * Asigna la lista listaUltimoAnio
     * 
     * @param listaUltimoAnio
     * Variable a asignar en listaUltimoAnio
     */
    public void setListaUltimoAnio(List<Registro> listaUltimoAnio)
    {
        this.listaUltimoAnio = listaUltimoAnio;
    }

    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia()
    {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia)
    {
        this.listaVigencia = listaVigencia;
    }

    /**
     * Retorna la lista listaAnoConstruccion
     * 
     * @return listaAnoConstruccion
     */
    public List<Registro> getListaAnoConstruccion()
    {
        return listaAnoConstruccion;
    }

    /**
     * Asigna la lista listaAnoConstruccion
     * 
     * @param listaAnoConstruccion
     * Variable a asignar en listaAnoConstruccion
     */
    public void setListaAnoConstruccion(List<Registro> listaAnoConstruccion)
    {
        this.listaAnoConstruccion = listaAnoConstruccion;
    }

    /**
     * Retorna la lista listaTarifa
     * 
     * @return listaTarifa
     */
    public RegistroDataModelImpl getListaTarifa()
    {
        return listaTarifa;
    }

    /**
     * Asigna la lista listaTarifa
     * 
     * @param listaTarifa
     * Variable a asignar en listaTarifa
     */
    public void setListaTarifa(RegistroDataModelImpl listaTarifa)
    {
        this.listaTarifa = listaTarifa;
    }

    /**
     * Retorna la lista listaCodigoPadre
     * 
     * @return listaCodigoPadre
     */
    public RegistroDataModelImpl getListaCodigoPadre()
    {
        return listaCodigoPadre;
    }

    /**
     * Asigna la lista listaCodigoPadre
     * 
     * @param listaCodigoPadre
     * Variable a asignar en listaCodigoPadre
     */
    public void setListaCodigoPadre(RegistroDataModelImpl listaCodigoPadre)
    {
        this.listaCodigoPadre = listaCodigoPadre;
    }

    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo()
    {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    /**
     * metodos get y set
     */

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna la variable resolucion
     * 
     * @return resolucion
     */
    public String getResolucion()
    {
        return resolucion;
    }

    /**
     * Asigna la variable resolucion
     * 
     * @param resolucion
     * Variable a asignar en resolucion
     */
    public void setResolucion(String resolucion)
    {
        this.resolucion = resolucion;
    }

    /**
     * Retorna la variable pais
     * 
     * @return pais
     */
    public String getPais()
    {
        return pais;
    }

    /**
     * Asigna la variable pais
     * 
     * @param pais
     * Variable a asignar en pais
     */
    public void setPais(String pais)
    {
        this.pais = pais;
    }

    /**
     * Retorna la variable departamento
     * 
     * @return departamento
     */
    public String getDepartamento()
    {
        return departamento;
    }

    /**
     * Asigna la variable departamento
     * 
     * @param departamento
     * Variable a asignar en departamento
     */
    public void setDepartamento(String departamento)
    {
        this.departamento = departamento;
    }

    /**
     * Retorna la variable municipio
     * 
     * @return municipio
     */
    public String getMunicipio()
    {
        return municipio;
    }

    /**
     * Asigna la variable municipio
     * 
     * @param municipio
     * Variable a asignar en municipio
     */
    public void setMunicipio(String municipio)
    {
        this.municipio = municipio;
    }

}
