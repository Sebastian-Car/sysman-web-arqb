package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.ejb.EjbNominaTresRemote;
import com.sysman.nomina.enums.EmbargosControladorEnum;
import com.sysman.nomina.enums.EmbargosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author ngomez
 * @version 1, 21/07/2015
 *
 * @author asana
 * @version 2, 03 y 04/10/2017
 */
@ManagedBean
@ViewScoped
public class EmbargosControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private List<Registro> listaIdEmbargo;
    private RegistroDataModelImpl listaTipoDemandante;
    private List<Registro> listaDctoIdentidad;
    private List<Registro> listaBanco;
    private List<Registro> listaOficinaDestino;
    private RegistroDataModelImpl listaIdEmpleado;
    private RegistroDataModelImpl listaIdJuzgado;
    private RegistroDataModelImpl listaIdConcepto;
    private RegistroDataModelImpl listaFactor1;
    private RegistroDataModelImpl listaFactor2;
    private RegistroDataModelImpl listaFactor3;
    private RegistroDataModelImpl listaFactor4;
    private RegistroDataModelImpl listaFactor5;
    private RegistroDataModelImpl listaFactor6;
    private RegistroDataModelImpl listaFactor7;
    private RegistroDataModelImpl listaFactor8;
    private String idempleado;
    private String titulo;
    private String saldo;
    private String nombreCompleto;
    private String tipoEmbargo;
    private String nombreJuzgado;
    private String nombreConcepto;
    private String conceptoDescuento;
    private String factorUno;
    private String factorDos;
    private String factorTres;
    private String factorCuatro;
    private String factorCinco;
    private String factorSeis;
    private String factorSiete;
    private String factorOcho;
    private String numRadicacion;
    private final String proceso;
    private final String anio;
    private final String mes;
    private final String periodo;
    private String nVlrCuota;
    private static final String CTEFACTOR1 = "FACTOR1";
    private static final String CTEFACTOR2 = "FACTOR2";
    private static final String CTEFACTOR3 = "FACTOR3";
    private static final String CTEFACTOR4 = "FACTOR4";
    private static final String CTEFACTOR5 = "FACTOR5";
    private static final String CTEFACTOR6 = "FACTOR6";
    private static final String CTEFACTOR7 = "FACTOR7";
    private static final String CTEFACTOR8 = "FACTOR8";
    private static final String CTEIDCONCEPTO = "ID_DE_CONCEPTO";
    private static final String CTEIDEMPLEADO = "ID_DE_EMPLEADO";
    private static final String CTEIDJUZGADO = "ID_JUZGADO";
    private static final String CTEIDEMBARGO = "ID_EMBARGO";
    private static final String CTENOMCONCEPTO = "NOMBRECONCEPTO";
    private static final String CTENOMBRECONCEPTO = "NOMBRE_CONCEPTO";
    private static final String CTENOMJUZGADO = "NOMJUZGADO";
    private String nSaldo;
    private String nValor;
    private String nPorcentaje;
    private String nMontoEmbargo;
    private String nombreTipoDemandante;

    @EJB
    private EjbNominaCuatroRemote ejbNominaCuatroRemote;
    @EJB
    private EjbNominaTresRemote ejbNominaTresRemote;
	private String obligaCampos1;

    @SuppressWarnings("unchecked")
    public EmbargosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        anio = (String) SessionUtil.getSessionVar("anioNomina");
        mes = (String) SessionUtil.getSessionVar("mesNomina");
        periodo = (String) SessionUtil.getSessionVar("periodoNomina");
        nSaldo = "SALDO";
        nValor = "VALOR";
        nPorcentaje = "PORCENTAJE";
        nMontoEmbargo = "MONTO_EMBARGO";
        nVlrCuota = "VR_CUOTA";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.EMBARGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            titulo = idioma.getString("TB_TB3726")
                            .replace("s$mes$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes)])
                            .replace("s$anio$s", anio)
                            .replace("s$periodo$s", periodo);

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
            SessionUtil.cleanFlash();
        }
        catch (Exception ex)
        {
            Logger.getLogger(EmbargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.EMBARGOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(EmbargosControladorEnum.PROCESO.getValue(),
                        proceso);
        parametrosListado.put(EmbargosControladorEnum.ANIO.getValue(), anio);
        parametrosListado.put(EmbargosControladorEnum.MES.getValue(), mes);
        parametrosListado.put(EmbargosControladorEnum.PERIODO.getValue(),
                        periodo);
    }

    @Override
    public void iniciarListas()
    {
        cargarListaIdEmpleado();
        cargarListaIdEmbargo();
        cargarListaIdJuzgado();
        cargarListaIdConcepto();
        cargarListaTipoDemandante();
        cargarListaDctoIdentidad();
        cargarListaBanco();
        cargarListaOficinaDestino();
        cargarListaFactor1();
        cargarListaFactor2();
        cargarListaFactor3();
        cargarListaFactor4();
        cargarListaFactor5();
        cargarListaFactor6();
        cargarListaFactor7();
        cargarListaFactor8();
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // Metodo heredado
    }

    @Override
    public void iniciarListasSub()
    {
        // Metodo heredado
    }

    public void cargarListaIdEmbargo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaIdEmbargo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EmbargosControladorUrlEnum.URL10343
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

    public void cargarListaIdJuzgado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL12700
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaIdJuzgado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDJUZGADO);
    }

    public void cargarListaIdConcepto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL13551
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaIdConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }

    public void cargarListaTipoDemandante()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EmbargosControladorEnum.TIPOEMBARGO.getValue(), tipoEmbargo);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL64754
                                                        .getValue());
        listaTipoDemandante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID_DEMANDANTE");
    }

    public void cargarListaDctoIdentidad()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaDctoIdentidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EmbargosControladorUrlEnum.URL82065
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaBanco()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaBanco = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            EmbargosControladorUrlEnum.URL11878
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

    public void cargarListaOficinaDestino()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaOficinaDestino = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            EmbargosControladorUrlEnum.URL79742
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

    public void cargarListaFactor1()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);

    }

    public void cargarListaFactor2()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor2 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }

    public void cargarListaFactor3()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaFactor3 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);

    }

    public void cargarListaFactor4()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor4 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }

    public void cargarListaFactor5()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor5 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }
    
    public void cargarListaFactor6()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor6 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }
    
    public void cargarListaFactor7()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor7 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }
    
    public void cargarListaFactor8()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL15192
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaFactor8 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDCONCEPTO);
    }

    public void cargarListaIdEmpleado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EmbargosControladorUrlEnum.URL98805
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaIdEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CTEIDEMPLEADO);
    }

    public void seleccionarFilaIdEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nombreCompleto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOM"), " ")
                        .toString();
        registro.getCampos().put(CTEIDEMPLEADO,
                        registroAux.getCampos().get(CTEIDEMPLEADO));
    }

    public void cambiarIdEmbargo()
    {
        // <CODIGO_DESARROLLADO>
        tipoEmbargo = registro.getCampos().get(CTEIDEMBARGO).toString();
        cargarListaTipoDemandante();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoDemandante(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nombreTipoDemandante = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(EmbargosControladorEnum.DEMANDANTE
                                                        .getValue()),
                                        " ")
                        .toString();
        registro.getCampos().put(
                        EmbargosControladorEnum.TIPO_DEMANDANTE.getValue(),
                        registroAux.getCampos()
                                        .get(EmbargosControladorEnum.ID_DEMANDANTE
                                                        .getValue()));
    }

    public void seleccionarFilaIdJuzgado(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();

        nombreJuzgado = registroAux.getCampos().get(CTENOMJUZGADO) == null ? ""
            : registroAux.getCampos().get(CTENOMJUZGADO).toString();
        registro.getCampos().put(CTEIDJUZGADO,
                        registroAux.getCampos().get(CTEIDJUZGADO) == null ? ""
                            : registroAux.getCampos().get(CTEIDJUZGADO));

        registro.getCampos().put(CTENOMJUZGADO,
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.NOMBRE
                                                                            .getName()));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaIdConcepto(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>

        Registro registroAux = (Registro) event.getObject();
        nombreConcepto = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
            ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEIDCONCEPTO,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        registro.getCampos().put(CTENOMCONCEPTO,
                        registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
                            ? ""
                            : registroAux.getCampos().get(CTENOMBRECONCEPTO));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFactor1(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorUno = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR1,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFactor2(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorDos = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR2,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFactor3(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorTres = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR3,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFactor4(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorCuatro = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
            ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR4,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFactor5(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorCinco = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
            ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR5,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }
    
    public void seleccionarFilaFactor6(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorSeis = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
            ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR6,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }
    
    public void seleccionarFilaFactor7(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorSiete = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
            ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR7,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }
    
    public void seleccionarFilaFactor8(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        factorOcho = registroAux.getCampos().get(CTENOMBRECONCEPTO) == null
            ? ""
            : registroAux.getCampos().get(CTENOMBRECONCEPTO).toString();
        registro.getCampos().put(CTEFACTOR8,
                        registroAux.getCampos().get(CTEIDCONCEPTO) == null ? ""
                            : registroAux.getCampos().get(CTEIDCONCEPTO));
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirMod()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TIPO_EMBARGOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirLeer()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CLASEDEMANDANTES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirJuz()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.JUZGADOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPrep()
    {
        agregarRegistroNuevo(false);
        SessionUtil.cargarModal(Integer
                        .toString(GeneralCodigoFormaEnum.PREPARAR_EMBARGOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo());
    }

    public void oprimirBorrar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            boolean a = ejbNominaCuatroRemote.borrarEmbargos(
                            Integer.parseInt(proceso), Integer.parseInt(mes),
                            Integer.parseInt(anio),
                            Integer.parseInt(periodo), compania);
            if (a)
            {
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.EMBARGOS_CONTROLADOR
                                                .getCodigo()));
                SessionUtil.redireccionarForma(direccionador, modulo);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2555"));
            }

        }
        catch (NumberFormatException | SystemException ex)
        {
            Logger.getLogger(EmbargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            ex.getMessage()));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEtiqueta26(ActionEvent ac)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        try
        {
            if ("i".equals(accion))
            {
                registro.getCampos().put(nPorcentaje, "0");
                registro.getCampos().put(nVlrCuota, "0");
                registro.getCampos().put("PORCENTAJE_CESANTIAS", "0");
                registro.getCampos().put("PORCENTAJE_PRIMAS", "0");
                registro.getCampos().put(nValor, "0");
                registro.getCampos().put("COMISION", "0");
                registro.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                registro.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                nombreCompleto = null;
                factorUno = null;
                factorDos = null;
                factorTres = null;
                factorCuatro = null;
                factorCinco = null;
                factorSeis= null;
                factorSiete = null;
                factorOcho = null;
                tipoEmbargo = null;
                nombreJuzgado = null;
                nombreTipoDemandante = null;
            }
            else if ("m".equals(accion) || "v".equals(accion))
            {
                if (registro.getCampos().get(CTEIDEMPLEADO) == null)
                {
                    nombreCompleto = "";
                }
                else
                {
                    Registro registroAux;
                    Map<String, Object> param = new TreeMap<>();

                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(EmbargosControladorEnum.ID_EMPLEADO.getValue(),
                                    registro.getCampos().get(
                                                    CTEIDEMPLEADO));
                    registroAux = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    EmbargosControladorUrlEnum.URL92848
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));

                    nombreCompleto = registroAux.getCampos()
                                    .get(EmbargosControladorEnum.NOMBRECOMPLETO
                                                    .getValue())
                                    .toString();

                }
                factorUno = validarFactor(CTEFACTOR1);
                factorDos = validarFactor(CTEFACTOR2);
                factorTres = validarFactor(CTEFACTOR3);
                factorCuatro = validarFactor(CTEFACTOR4);
                factorCinco = validarFactor(CTEFACTOR5);
                factorSeis = validarFactor(CTEFACTOR6);
                factorSiete = validarFactor(CTEFACTOR7);
                factorOcho = validarFactor(CTEFACTOR8);

                if (registro.getCampos().get(CTEIDEMBARGO) == null)
                {
                    tipoEmbargo = "";
                }
                else
                {
                    tipoEmbargo = registro.getCampos()
                                    .get(CTEIDEMBARGO).toString();
                    cargarListaTipoDemandante();
                }
                registro.getCampos().put(
                                GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                registro.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                nombreJuzgado = registro.getCampos().get(CTENOMJUZGADO)
                                .toString();
                registro.getCampos().get(EmbargosControladorEnum.NOMBRECONCEPTO
                                .getValue()).toString();
                nombreTipoDemandante = SysmanFunciones
                                .nvl(registro.getCampos()
                                                .get(EmbargosControladorEnum.NOMBRETIPO
                                                                .getValue()),
                                                "")
                                .toString();
            }
            cargarBorde();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(EmbargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            ex.getMessage()));
        }

        // </CODIGO_DESARROLLADO>
    }

    private void cargarBorde() {
    	obligaCampos1 ="#000000 solid 1px";
	}

	private String validarFactor(String factor)
    {
        return registro.getCampos().get(factor) == null ? ""
            : ejecutarFuncionPL(factor);
    }

    public void cambiarSaldo()
    {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    public void cambiarValor()
    {

        int valorCuota;
        registro.getCampos().put(nPorcentaje, 0);
        if (Long.parseLong(registro.getCampos().get(nMontoEmbargo)
                        .toString()) >= Long
                                        .parseLong(registro.getCampos()
                                                        .get(nValor)
                                                        .toString()))
        {
            valorCuota = Integer.parseInt(
                            registro.getCampos().get(nMontoEmbargo).toString())
                / Integer.parseInt(registro.getCampos().get(nValor).toString());
            if ((valorCuota < 999) && (valorCuota > 0))
            {
                registro.getCampos().put("CUOTAS", valorCuota);
                registro.getCampos().put(nVlrCuota,
                                registro.getCampos().get(nValor).toString());
            }

        }
        else
        {
            registro.getCampos().put("CUOTAS", 0);
            registro.getCampos().put(nVlrCuota, 0);
            registro.getCampos().put(nValor, 0);
        }

    }

    public void cambiarCuotas()
    {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    public void cambiarMontoEmbargo()
    {
        registro.getCampos().put(nMontoEmbargo,
                        registro.getCampos().get(nMontoEmbargo) == null ? "0"
                            : registro.getCampos().get(nMontoEmbargo));
        registro.getCampos().put(nSaldo,
                        registro.getCampos().get(nMontoEmbargo) == null ? "0"
                            : registro.getCampos().get(nMontoEmbargo));

        registro.getCampos().put(nValor, "0");
        registro.getCampos().put("CUOTAS", 0);
        
    }

    public void cambiarPorcentaje()
    {
        registro.getCampos().put(nValor, "0");
        registro.getCampos().put("CUOTAS", 0);
        registro.getCampos().put(nVlrCuota, "0");
    }

    private String ejecutarFuncionPL(String factor)
    {
        String nombreconcepto = null;
        try
        {
            nombreconcepto = ejbNominaTresRemote.nombreConcepto(compania,
                            Integer.parseInt(registro.getCampos().get(factor)
                                            .toString()));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return nombreconcepto;
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        proceso);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), mes);
        registro.getCampos().put(EmbargosControladorEnum.PERIODO.getValue(),
                        periodo);
        registro.getCampos().remove(CTENOMJUZGADO);
        registro.getCampos().remove(CTENOMCONCEPTO);
        return true;

        // </CODIGO_DESARROLLADO>
    }

    private boolean cambiarBorde() {
    	boolean rta = false;
    	Double saldo = Double.parseDouble(SysmanFunciones.toString(registro.getCampos().get("SALDO")));
    	if(registro.getCampos().get("ESTADO").equals("C") && saldo > 0) {
    		 if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "OBSERVACIONES")) {
	                obligaCampos1 = "#FF0000 solid 1px"; 
	                rta = true;
	            }else {
	            	obligaCampos1 = "#000000 solid 1px"; 
	            }
    	}
	      return rta;

    }
    
   

	@Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(CTENOMJUZGADO, nombreJuzgado);
        registro.getCampos().put(CTENOMCONCEPTO, nombreConcepto);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
    	if(cambiarBorde()) {
    		JsfUtil.agregarMensajeAlerta("Por favor,  justifique el motivo por el cual el embargo se finaliza con saldo pendiente.");
    		return false;
    	}
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
            registro.getCampos().remove(GeneralParameterEnum.MES.getName());
        }
        registro.getCampos().remove(CTENOMJUZGADO);
        registro.getCampos().remove(CTENOMCONCEPTO);
        registro.getCampos()
                        .remove(EmbargosControladorEnum.NOMBRETIPO.getValue());
        return true;

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(CTENOMJUZGADO, nombreJuzgado);
        registro.getCampos().put(CTENOMCONCEPTO, nombreConcepto);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(CTENOMJUZGADO);
        registro.getCampos().remove(CTENOMCONCEPTO);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(CTENOMJUZGADO);
        registro.getCampos().remove(CTENOMCONCEPTO);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public String getNombreCompleto()
    {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto)
    {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombreJuzgado()
    {
        return nombreJuzgado;
    }

    public void setNombreJuzgado(String nombreJuzgado)
    {
        this.nombreJuzgado = nombreJuzgado;
    }

    public List<Registro> getListaIdEmbargo()
    {
        return listaIdEmbargo;
    }

    public void setListaIdEmbargo(List<Registro> listaIdEmbargo)
    {
        this.listaIdEmbargo = listaIdEmbargo;
    }

    public RegistroDataModelImpl getListaTipoDemandante()
    {
        return listaTipoDemandante;
    }

    public void setListaTipoDemandante(
        RegistroDataModelImpl listaTipoDemandante)
    {
        this.listaTipoDemandante = listaTipoDemandante;
    }

    public List<Registro> getListaDctoIdentidad()
    {
        return listaDctoIdentidad;
    }

    public void setListaDctoIdentidad(List<Registro> listaDctoIdentidad)
    {
        this.listaDctoIdentidad = listaDctoIdentidad;
    }

    public List<Registro> getListaBanco()
    {
        return listaBanco;
    }

    public void setListaBanco(List<Registro> listaBanco)
    {
        this.listaBanco = listaBanco;
    }

    public List<Registro> getListaOficinaDestino()
    {
        return listaOficinaDestino;
    }

    public void setListaOficinaDestino(List<Registro> listaOficinaDestino)
    {
        this.listaOficinaDestino = listaOficinaDestino;
    }

    public RegistroDataModelImpl getListaIdJuzgado()
    {
        return listaIdJuzgado;
    }

    public void setListaIdJuzgado(RegistroDataModelImpl listaIdJuzgado)
    {
        this.listaIdJuzgado = listaIdJuzgado;
    }

    public RegistroDataModelImpl getListaIdConcepto()
    {
        return listaIdConcepto;
    }

    public void setListaIdConcepto(RegistroDataModelImpl listaIdConcepto)
    {
        this.listaIdConcepto = listaIdConcepto;
    }

    public RegistroDataModelImpl getListaFactor1()
    {
        return listaFactor1;
    }

    public void setListaFactor1(RegistroDataModelImpl listaFactor1)
    {
        this.listaFactor1 = listaFactor1;
    }

    public RegistroDataModelImpl getListaIdEmpleado()
    {
        return listaIdEmpleado;
    }

    public void setListaIdEmpleado(RegistroDataModelImpl listaIdEmpleado)
    {
        this.listaIdEmpleado = listaIdEmpleado;
    }

    public RegistroDataModelImpl getListaFactor2()
    {
        return listaFactor2;
    }

    public void setListaFactor2(RegistroDataModelImpl listaFactor2)
    {
        this.listaFactor2 = listaFactor2;
    }

    public RegistroDataModelImpl getListaFactor3()
    {
        return listaFactor3;
    }

    public void setListaFactor3(RegistroDataModelImpl listaFactor3)
    {
        this.listaFactor3 = listaFactor3;
    }

    public RegistroDataModelImpl getListaFactor4()
    {
        return listaFactor4;
    }

    public void setListaFactor4(RegistroDataModelImpl listaFactor4)
    {
        this.listaFactor4 = listaFactor4;
    }

    public RegistroDataModelImpl getListaFactor5()
    {
        return listaFactor5;
    }

    public void setListaFactor5(RegistroDataModelImpl listaFactor5)
    {
        this.listaFactor5 = listaFactor5;
    }

    public String getIdempleado()
    {
        return idempleado;
    }

    public void setIdempleado(String idempleado)
    {
        this.idempleado = idempleado;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getTipoEmbargo()
    {
        return tipoEmbargo;
    }

    public void setTipoEmbargo(String tipoEmbargo)
    {
        this.tipoEmbargo = tipoEmbargo;
    }

    public String getNumRadicacion()
    {
        return numRadicacion;
    }

    public void setNumRadicacion(String numRadicacion)
    {
        this.numRadicacion = numRadicacion;
    }

    public String getConceptoDescuento()
    {
        return conceptoDescuento;
    }

    public void setConceptoDescuento(String conceptoDescuento)
    {
        this.conceptoDescuento = conceptoDescuento;
    }

    public String getFactorUno()
    {
        return factorUno;
    }

    public void setFactorUno(String factorUno)
    {
        this.factorUno = factorUno;
    }

    public String getFactorDos()
    {
        return factorDos;
    }

    public void setFactorDos(String factorDos)
    {
        this.factorDos = factorDos;
    }

    public String getFactorTres()
    {
        return factorTres;
    }

    public void setFactorTres(String factorTres)
    {
        this.factorTres = factorTres;
    }

    public String getFactorCuatro()
    {
        return factorCuatro;
    }

    public void setFactorCuatro(String factorCuatro)
    {
        this.factorCuatro = factorCuatro;
    }

    public String getFactorCinco()
    {
        return factorCinco;
    }

    public void setFactorCinco(String factorCinco)
    {
        this.factorCinco = factorCinco;
    }

    public String getSaldo()
    {
        return saldo;
    }

    public void setSaldo(String saldo)
    {
        this.saldo = saldo;
    }

    public String getNombreConcepto()
    {
        return nombreConcepto;
    }

    public void setNombreConcepto(String nombreConcepto)
    {
        this.nombreConcepto = nombreConcepto;
    }

    public String getModulo()
    {
        return modulo;
    }

    public String getNombreTipoDemandante()
    {
        return nombreTipoDemandante;
    }

    public void setNombreTipoDemandante(String nombreTipoDemandante)
    {
        this.nombreTipoDemandante = nombreTipoDemandante;
    }

	public String getFactorSeis() {
		return factorSeis;
	}

	public void setFactorSeis(String factorSeis) {
		this.factorSeis = factorSeis;
	}

	public String getFactorSiete() {
		return factorSiete;
	}

	public void setFactorSiete(String factorSiete) {
		this.factorSiete = factorSiete;
	}

	public String getFactorOcho() {
		return factorOcho;
	}

	public void setFactorOcho(String factorOcho) {
		this.factorOcho = factorOcho;
	}

	public RegistroDataModelImpl getListaFactor6() {
		return listaFactor6;
	}

	public void setListaFactor6(RegistroDataModelImpl listaFactor6) {
		this.listaFactor6 = listaFactor6;
	}

	public RegistroDataModelImpl getListaFactor7() {
		return listaFactor7;
	}

	public void setListaFactor7(RegistroDataModelImpl listaFactor7) {
		this.listaFactor7 = listaFactor7;
	}

	public RegistroDataModelImpl getListaFactor8() {
		return listaFactor8;
	}

	public void setListaFactor8(RegistroDataModelImpl listaFactor8) {
		this.listaFactor8 = listaFactor8;
	}

	/**
	 * @return the obligaCampos1
	 */
	public String getObligaCampos1() {
		return obligaCampos1;
	}

	/**
	 * @param obligaCampos1 the obligaCampos1 to set
	 */
	public void setObligaCampos1(String obligaCampos1) {
		this.obligaCampos1 = obligaCampos1;
	}

	
}
