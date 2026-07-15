package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.PeriodoPptalControladorEnum;
import com.sysman.presupuesto.enums.PeriodoPptalControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 15/06/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 17/04/2017
 * 
 * @author asana
 * @version 3, 12/06/2017 Redireccion de formulario.
 */
@ManagedBean
@ViewScoped

public class PeriodoPptalControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Atributo que almacen el codigo del modulo por la cual se
     * ingresa a la aplicacion
     */
    private String modulo;
    private String menuActual;
    // <DECLARAR_ATRIBUTOS>
    private String tipoComprobante;
    private String nombreComprobante;
    private String claseComprobante;
    private String formatoComprobante;
    private String pideTercero;
    private String mes;
    private String ano;

    /**
     * Atributo que contiene el estado del anio.
     */
    private String estado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String opcionMenu;
    private boolean tipoVigenciaFutura;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;
    private Map<String,Object> parametroswf;

    /**
     * Creates a new instance of PeriodoPptalControlador
     */
    public PeriodoPptalControlador() {
        super();
        compania = SessionUtil.getCompania();
        menuActual = SessionUtil.getMenuActual();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_PPTAL_CONTROLADOR
                            .getCodigo();
            
            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("menuActual",SysmanFunciones.nvl(parametroswf.get("menu"),"").toString());
				SessionUtil.setSessionVar("modulo","3");
				menuActual = SessionUtil.getMenuActual();
				modulo = SessionUtil.getModulo();
			}
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoPptalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarParametroMenu();
        cargarListaAno();
        Date sysdate = new Date();
        ano = String.valueOf(SysmanFunciones.ano(sysdate));
        mes = String.valueOf(SysmanFunciones.mes(sysdate));

        /* Asigna un valor al estado del anio. */
        estado = verificarEstado();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoPptalControladorUrlEnum.URL4485
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PeriodoPptalControladorUrlEnum.URL4726
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(PeriodoPptalControladorEnum.PARAM0.getValue(),
                        String.valueOf(opcionMenu));
        param.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());
        param.put(GeneralParameterEnum.MODULO.getName(), SessionUtil.getModulo());

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);
        parametros.put("mes", mes);
        parametros.put("ingreso", false);
        parametros.put("tipoComprobante", tipoComprobante);
        parametros.put("claseComprobante", claseComprobante);
        parametros.put("nombreComprobante", nombreComprobante);
        parametros.put("formatoComprobante", formatoComprobante);
        parametros.put("pideTercero", pideTercero);
        parametros.put("tipoVigenciaFutura", tipoVigenciaFutura);
        parametros.put("estado", estado);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.COMPROBANTEPPTALS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        if(parametroswf != null) {
        	try {
        		parametros.put("parametroswf",parametroswf);
				SessionUtil.setSessionVarContainer("parametros",parametros);
			} catch(NamingException e) {
				e.printStackTrace();
			}
        	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanPresupuesto/comprobantepptal.sysman';");
        } else {
        	RequestContext.getCurrentInstance().closeDialog(direccionador);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica que el anio tenga estado activo.
     *
     * @return retorna A si esta Activo o C si esta Cerrado
     */
    public String verificarEstado() {
        try {

            estado = ejbSysmanUtl.verificarEstadoPeriodoAnual(compania,
                            Integer.parseInt(ano), Integer.parseInt(modulo), 1);

        }
        catch (NumberFormatException | SystemException ex) {

            Logger.getLogger(PeriodoplanptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        return estado;
    }

    /**
     * Verifica que el mes tenga estado activo.
     *
     * @return retorna A si esta Activo o C si esta Cerrado
     */
    public String verificarEstadoMes() {
        try {

            estado = ejbSysmanUtl.verificarEstadoPeriodoMensual(compania,
                            Integer.parseInt(ano), Integer.parseInt(mes),
                            Integer.parseInt(modulo), 1);

        }
        catch (NumberFormatException | SystemException ex) {

            Logger.getLogger(PeriodoplanptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        return estado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del combo Ano en la forma.
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        if ("C".equals(verificarEstado())) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2591").replace("#ANIO#",
                                            ano));

        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes() {

        if ("A".equals(verificarEstado()) && "C".equals(verificarEstadoMes())) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB3115").replace("#MES#",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .valueOf(mes)]));

        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoComprobante = registroAux.getCampos().get("CODIGO").toString();
        nombreComprobante = registroAux.getCampos().get("NOMBRE").toString();
        claseComprobante = registroAux.getCampos().get("CLASE").toString();
        formatoComprobante = registroAux.getCampos().get("FORMATO") != null ? registroAux.getCampos().get("FORMATO").toString() : null;
        pideTercero = ((boolean) registroAux.getCampos()
                        .get("TECEROIGUAL")) ? "S" : "N";
        tipoVigenciaFutura = (boolean) registroAux.getCampos()
                        .get("TIPOVIGENCIAFUTURA");
    }

    // </METODOS_COMBOS_GRANDES>

    public void cargarParametroMenu() {
        switch (menuActual) {
        case "3020108":
            opcionMenu = "ARO";
            break;

        case "30203":
            opcionMenu = "ADC";
            break;

        case "3020102":
            opcionMenu = "ADD";
            break;

        case "3020111":
            opcionMenu = "AEG";
            break;

        case "3020207":
            opcionMenu = "AIC";
            break;

        case "3020105":
            opcionMenu = "ADR";
            break;

        case "3020203":
            opcionMenu = "AIN";
            break;

        case "30206":
            opcionMenu = "APL";
            break;
        case "3020109":
            opcionMenu = "DRO";
            break;
        default:
            opcionMenu = opcion();
            break;
        }
    }

    /**
     * Metodo llamado en cargarParametroMenu
     *
     * @return retorna una tipo de comprobante de acuerdo con la
     * opcion de menu
     */
    private String opcion() {
        switch (menuActual) {

        case "3020103":
            opcionMenu = "DMD";
            break;

        case "3020112":
            opcionMenu = "DEG";
            break;

        case "3020206":
            opcionMenu = "DIC";
            break;

        case "3020106":
            opcionMenu = "DMR";
            break;

        case "3020204":
            opcionMenu = "DIN";
            break;

        case "3020101":
            opcionMenu = "DIS";
            break;

        case "3020110":
            opcionMenu = "EGR";
            break;

        case "3020205":
            opcionMenu = "ICA";
            break;

        case "3020202":
            opcionMenu = "ING";
            break;

        default:
            opcionMenu = opcMen();
            break;
        }
        return opcionMenu;
    }

    private String opcMen() {
        switch (menuActual) {
        case "3020114":
            opcionMenu = "MOP";
            break;

        case "3020113":
            opcionMenu = "EJE";
            break;

        case "3020208":
            opcionMenu = "REC";
            break;

        case "30204":
            opcionMenu = "RED";
            break;

        case "3020107":
            opcionMenu = "REO";
            break;

        case "3020104":
            opcionMenu = "RES";
            break;

        case "30205":
            opcionMenu = "TRA";
            break;

        default:
            break;
        }
        return opcionMenu;
    }

    // <SET_GET_ATRIBUTOS>
    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getNombreComprobante() {
        return nombreComprobante;
    }

    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
