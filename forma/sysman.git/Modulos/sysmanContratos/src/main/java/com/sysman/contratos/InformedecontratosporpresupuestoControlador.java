package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InformedecontratosporpresupuestoControladorEnum;
import com.sysman.contratos.enums.InformedecontratosporpresupuestoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 11/12/2015
 *
 * @author spina
 * @version 2, 11/08/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class InformedecontratosporpresupuestoControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private final String fechaInicialCons;
    private final String fechaFinalCons;
    private final String formatoFechaCons;
    private final String condicionCons;
    private static final String MENU90348 = "90348";
    private static final String MENU90326 = "90326";
    private static final String MENU90325 = "90325";
    private static final String MENU90309 = "90309";
    private String rubroFinal;
    private String rubroInicial;
    private String tipo;
    private String terceroCed;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private String terceroCedFinal;
    private String presupuesto;
    private Date fechaFinal;
    private Date fechaInicial;
    private String nombreTerceroFinal;
    private String nombreTerceroIni;
    private RegistroDataModelImpl listaRubroFinal;
    private RegistroDataModelImpl listaRubroInicial;
    private List<Registro> listaTipo;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private RegistroDataModelImpl listaTipoContratoFinal;
    private RegistroDataModelImpl listaPresupuesto;
    private RegistroDataModelImpl listaTerceroCed;
    private RegistroDataModelImpl listaTerceroCedFinal;
    private String tituloModal;
    private String tituloLabel;
    private String menuActual;
    private boolean visibleResumenContTerceros;
    private boolean visibleTerceroInicial;
    private boolean visibleTerceroFinal;
    private boolean visibleTipoCInicial;
    private boolean visibleTipoCFinal;
    private boolean visibleRubroInicial;
    private boolean visibleRubroFinal;
    private boolean visiblePresupuesto;
    private boolean visibleTipo;
    private boolean visibleFechaInicial;
    private boolean visibleFechaFinal;
    private String nombreContratoInicial;
    private String nombreContratoFinal;
    private String nombreRubroInicial;
    private String nombreRubroFinal;
    private StreamedContent archivoDescarga;
    private String nombreCompania;
    private String nitCompania;
    private String deptoCompania;
    private String nombreTipo;
    private String parametroDisponibilidad;
    private boolean checkResumen;
    private String topBotones;
    private boolean mostrarMiles = true;
    private String vTotales;
    private String vReserva;
    private String vtotaln;
    private String vPagosn;
    



	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of InformedecontratosporpresupuestoControlador
     */
    public InformedecontratosporpresupuestoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        menuActual = SessionUtil.getMenuActual();
        nombreCompania = SessionUtil.getCompaniaIngreso()
                        .getNombre();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        deptoCompania = SessionUtil.getCompaniaIngreso()
                        .getDepartamento();
        fechaInicialCons = "fechaInicial";
        fechaFinalCons = "fechaFinal";
        formatoFechaCons = "dd/MM/yyyy";
        condicionCons = "condicion";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORMEDECONTRATOSPORPRESUPUESTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            visibleRubroInicial = false;
            visibleRubroFinal = false;
            visibleTipoCInicial = false;
            visibleTipoCFinal = false;
            visiblePresupuesto = false;
            visibleTipo = false;
            visibleResumenContTerceros = false;
            visibleTerceroInicial = false;
            visibleTerceroFinal = false;
            visibleFechaInicial = true;
            visibleFechaFinal = true;

        }
        catch (Exception ex)
        {
            Logger.getLogger(InformedecontratosporpresupuestoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void menu90348()
    {
        tituloModal = idioma.getString("TB_TB3511");
        visibleTipoCInicial = true;
        visibleTipoCFinal = true;
        visibleRubroInicial = true;
        visibleRubroFinal = true;
        topBotones = "333px";
    }

    public void menu90309()
    {
        try
        {
            parametroDisponibilidad = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA INFORME ESPECIAL EN CONTRATOS POR COD PPTAL",
                            modulo, new Date(), true);
            if (("NO").equalsIgnoreCase(parametroDisponibilidad))
            {
                visiblePresupuesto = true;
                topBotones = "160px";
            }
            else
            {
                visibleRubroInicial = true;
                visibleRubroFinal = true;
                topBotones = "333px";
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(
                            InformedecontratosporpresupuestoControlador.class
                                            .getName())
                            .log(Level.SEVERE,
                                            null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void menu90325()
    {
        tituloModal = idioma.getString("TB_TB3512");
        visibleResumenContTerceros = true;
        visibleTerceroInicial = true;
        visibleTerceroFinal = true;
        topBotones = "220px";
        checkResumen = false;
    }

    @PostConstruct
    public void inicializar()
    {
        switch (menuActual)
        {

        case MENU90309:
            tituloModal = idioma.getString("TB_TB3513");
            menu90309();
            break;
        case MENU90325:
            menu90325();
            break;
        case MENU90326:
            tituloModal = idioma.getString("TB_TB3514");
            visibleTipo = true;
            topBotones = "180px";
            break;
        case MENU90348:
            menu90348();
            break;
        default:
            break;
        }
        tituloLabel = tituloModal.toUpperCase();
        cargarListaTipo();
        cargarListaTipoContratoInicial();
        cargarListaTerceroCed();
        abrirFormulario();
    }

    public void cargarListaRubroInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5599
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                        SysmanFunciones.ano(fechaInicial));
        param.put(GeneralParameterEnum.FECHAFINAL.getName(),
                        SysmanFunciones.ano(fechaFinal));

        listaRubroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        InformedecontratosporpresupuestoControladorEnum.ID
                                        .getValue());
    }

    public void cargarListaRubroFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5600
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformedecontratosporpresupuestoControladorEnum.RUBROINICIAL
                        .getValue(), rubroInicial);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                        SysmanFunciones.ano(fechaInicial));
        param.put(GeneralParameterEnum.FECHAFINAL.getName(),
                        SysmanFunciones.ano(fechaFinal));

        listaRubroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        InformedecontratosporpresupuestoControladorEnum.ID
                                        .getValue());
    }

    public void cargarListaTipo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformedecontratosporpresupuestoControladorUrlEnum.URL5604
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoContratoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5597
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoContratoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5598
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformedecontratosporpresupuestoControladorEnum.CODIGOINI
                        .getValue(), tipoContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaPresupuesto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5601
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformedecontratosporpresupuestoControladorEnum.ANOINICIAL
                        .getValue(), SysmanFunciones.getParteFecha(fechaInicial,
                                        Calendar.YEAR));
        param.put(InformedecontratosporpresupuestoControladorEnum.ANOFINAL
                        .getValue(), SysmanFunciones.getParteFecha(fechaFinal,
                                        Calendar.YEAR));
        param.put(InformedecontratosporpresupuestoControladorEnum.TIPO
                        .getValue(), "DIS");

        listaPresupuesto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        InformedecontratosporpresupuestoControladorEnum.NUMEROPPTO
                                        .getValue());
    }

    public void cargarListaTerceroCed()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5602
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroCed = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        InformedecontratosporpresupuestoControladorEnum.NIT
                                        .getValue());
    }

    public void cargarListaTerceroCedFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformedecontratosporpresupuestoControladorUrlEnum.URL5603
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformedecontratosporpresupuestoControladorEnum.TECEROINICIAL
                        .getValue(), terceroCed);

        listaTerceroCedFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        InformedecontratosporpresupuestoControladorEnum.NIT
                                        .getValue());
    }

    public void cambiarCKResumen()
    {
        // <CODIGO_DESARROLLADO>
        if (!checkResumen)
        {
            visibleTipoCInicial = false;
            visibleTipoCFinal = false;
            tipoContratoInicial = null;
            nombreContratoInicial = null;
            tipoContratoFinal = null;
            nombreContratoFinal = null;
            topBotones = "220px";
        }
        else
        {
            visibleTipoCInicial = true;
            visibleTipoCFinal = true;
            topBotones = "280px";
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipo()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(tipo))
        {
            return;
        }
        nombreTipo = service.buscarEnLista(tipo,
                        GeneralParameterEnum.CODIGO.getName(),
                        GeneralParameterEnum.NOMBRE.getName(),
                        listaTipo);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaInicial()
    {
        cargarListaPresupuesto();
        rubroInicial = null;
        nombreRubroInicial = null;
        presupuesto = null;
    }

    public void cambiarFechaFinal()
    {
        if (fechaFinal != null && fechaInicial != null
            && fechaFinal.before(fechaInicial))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB147"));
            fechaFinal = null;
        }

        cargarListaPresupuesto();
        if (SysmanFunciones.validarVariableVacio(rubroInicial))
        {
            cargarListaRubroInicial();
            cargarListaRubroFinal();
        }
        rubroFinal = null;
        nombreRubroFinal = null;
    }

    public void seleccionarFilaRubroFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        rubroFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
        nombreRubroFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName()),
                        "").toString();
    }

    public void seleccionarFilaRubroInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        rubroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        nombreRubroInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        rubroFinal = null;
        nombreRubroFinal = null;
        cargarListaRubroFinal();
    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreContratoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName()),
                        "").toString();
        tipoContratoFinal = null;
        nombreContratoFinal = null;
        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreContratoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    public void seleccionarFilaTerceroCed(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroCed = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        nombreTerceroIni = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName()),
                        "").toString();
        terceroCedFinal = null;
        nombreTerceroFinal = null;
        cargarListaTerceroCedFinal();
    }

    public void seleccionarFilaTerceroCedFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroCedFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        nombreTerceroFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    public void seleccionarFilaPresupuesto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        presupuesto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMEROPPTO"), "0")
                        .toString();
    }

    public void presentar90309(FORMATOS formato)
    {
        if (("NO").equalsIgnoreCase(parametroDisponibilidad))
        {
            if (SysmanFunciones.validarVariableVacio(presupuesto))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2127"));
                return;
            }
            generaInformeDeContratosPorX(formato);
        }
        else
        {
            if (SysmanFunciones.validarVariableVacio(rubroInicial))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                InformedecontratosporpresupuestoControladorEnum.TB_TB2128
                                                                .getValue()));
                return;
            }
            if (SysmanFunciones.validarVariableVacio(rubroFinal))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                InformedecontratosporpresupuestoControladorEnum.TB_TB2129
                                                                .getValue()));
                return;
            }
            generaInformeEspecial(formato);
        }
    }

    public void presentar90325(FORMATOS formato)
    {
        if (SysmanFunciones.validarVariableVacio(terceroCed))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2130"));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(terceroCedFinal))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2131"));
            return;
        }

        if (!checkResumen)
        {
            generaInformeDeContratosPorX(formato);
        }
        else
        {
            if (SysmanFunciones.validarVariableVacio(tipoContratoInicial))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                InformedecontratosporpresupuestoControladorEnum.TB_TB2132
                                                                .getValue()));
                return;
            }
            if (SysmanFunciones.validarVariableVacio(tipoContratoFinal))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                InformedecontratosporpresupuestoControladorEnum.TB_TB2133
                                                                .getValue()));
                return;
            }
            generaInformeContratosTercero(formato);
        }
    }

    public void presentar90326(FORMATOS formato)
    {
        if (SysmanFunciones.validarVariableVacio(tipo))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2134"));
            return;
        }
        generaInformeDeContratosPorX(formato);
    }

    public void presentar90348(FORMATOS formato)
    {
        if (SysmanFunciones.validarVariableVacio(tipoContratoInicial))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            InformedecontratosporpresupuestoControladorEnum.TB_TB2132
                                                            .getValue()));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(tipoContratoFinal))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            InformedecontratosporpresupuestoControladorEnum.TB_TB2133
                                                            .getValue()));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(rubroInicial))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            InformedecontratosporpresupuestoControladorEnum.TB_TB2128
                                                            .getValue()));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(rubroFinal))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            InformedecontratosporpresupuestoControladorEnum.TB_TB2129
                                                            .getValue()));
            return;
        }
        generaInformeContratosRubro(formato);
    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporteSegunMenu(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporteSegunMenu(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporteSegunMenu(FORMATOS formato)
    {
        switch (menuActual)
        {
        case MENU90309:
            presentar90309(formato);
            break;
        case MENU90325:
            presentar90325(formato);
            break;
        case MENU90326:
            presentar90326(formato);
            break;
        case MENU90348:
            presentar90348(formato);
            break;
        default:
            break;
        }
    }

    public Map<String, Object> putParametros(String parametro1,
        String parametro2,
        int menu)
    {
        Map<String, Object> parametros = new HashMap<>();
        if (menu == 90326)
        {
            parametros.put("PR_TIPOCONT", nombreTipo);
        }
        try
        {
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_CARGO_INFORME_CONTRATOS
                            .getValue(), ejbSysmanUtil.consultarParametro(
                                            compania, parametro1, modulo,
                                            new Date(), true));
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_TITULO_INFORME_CONTRATOS
                            .getValue(),
                            ejbSysmanUtil.consultarParametro(compania,
                                            parametro2, modulo, new Date(),
                                            true));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametros;
    }

    public Map<String, Object> parametros(int menuCase)
    {
        Map<String, Object> parametros = new HashMap<>();
        switch (menuCase)
        {
        case 90309:
            agregarParametros90309(parametros);
            break;
        case 90325:
            parametros = putParametros("CARGO INFORME CONTRATOS POR TERCERO",
                            "TITULO INFORME CONTRATOS POR TERCERO", 90325);
            break;
        case 90326:
            parametros = putParametros(
                            "CARGO INFORME CONTRATOS POR TIPO CONTRATO",
                            "TITULO INFORME CONTRATOS POR TIPO CONTRATO",
                            90326);
            break;
        default:
            break;
        }
        return parametros;
    }

    private void agregarParametros90309(Map<String, Object> parametros)
    {
        parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_CARGO_INFORME_CONTRATOS
                        .getValue(), idioma.getString("TB_TB3461"));
        parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_TITULO_INFORME_CONTRATOS
                        .getValue(),
                        idioma.getString("TB_TB3462").replace(
                                        "s$departamento$s",
                                        deptoCompania.toUpperCase()));
    }

    public void generaInformeDeContratosPorX(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();
            String strSql;
            switch (menuActual)
            {
            case MENU90309:
                reemplazar.put(condicionCons, " AND " + presupuesto
                    + " IN (SELECT NUMEROPPTO FROM ORDENDECOMPRAPPTO WHERE ORDENDECOMPRA.COMPANIA = ORDENDECOMPRAPPTO.COMPANIA \n"
                    + "        AND ORDENDECOMPRA.CLASEORDEN = ORDENDECOMPRAPPTO.CLASEORDEN\n  AND ORDENDECOMPRA.NUMERO = ORDENDECOMPRAPPTO.NUMERO)");
                parametros = parametros(90309);
                break;
            case MENU90325:
                reemplazar.put(condicionCons, " AND TERCERO.NIT BETWEEN '"
                    + terceroCed + "' AND '"
                    + terceroCedFinal + "' " + " ORDER BY TERCERO.NIT ");
                parametros = parametros(90325);
                break;
            case MENU90326:
                reemplazar.put(condicionCons,
                                " AND ORDENDECOMPRA.CLASEORDEN = '"
                                    + tipo + "' ");
                
                parametros = parametros(90326);
                break;
            default:
                break;
            }
            
            if (mostrarMiles)
            {
            	vTotales = "ROUND(ORDENDECOMPRA.VALORTOTAL, - 3) / 1000";
            	vReserva = "ROUND(ORDENDECOMPRA.VALORRESERVA, - 3) / 1000";
            	vtotaln  = "ROUND(ADICIONOC.VALORTOTAL, - 3) / 1000";
            	vPagosn  = "ROUND(ORDENDECOMPRA.VALORPAGOS,-3) /1000";
            }
            else
            {
            	vTotales = "ORDENDECOMPRA.VALORTOTAL";
            	vReserva = "ORDENDECOMPRA.VALORRESERVA";
            	vtotaln = "ADICIONOC.VALORTOTAL";
            	vPagosn = "ORDENDECOMPRA.VALORPAGOS ";
            }
            parametros.put("PR_MOSTRARMILES", mostrarMiles);
            reemplazar.put("vTotales", vTotales);
            reemplazar.put("vReserva", vReserva);
            reemplazar.put("vtotaln", vtotaln);
            reemplazar.put("vPagosn", vPagosn);
            reemplazar.put(fechaInicialCons,
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put(fechaFinalCons,
                            SysmanFunciones.formatearFecha(fechaFinal));
            strSql = Reporteador.resuelveConsulta(
                            "000427InformeDeContratosPorX",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE REPRESENTANTE LEGAL",
                                                            modulo, new Date(),
                                                            true),
                                            ""));
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), nombreCompania);
            parametros.put("PR_NITCOMPANIA", nitCompania);
            parametros.put("PR_ANO", SysmanFunciones.getParteFecha(fechaInicial,
                            Calendar.YEAR));
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial,
                                            formatoFechaCons));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal,
                                            formatoFechaCons));
            parametros.put("PR_FIRMA_DEL_ASESOR_JURIDICO",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "FIRMA DEL ASESOR JURIDICO",
                                                            modulo, new Date(),
                                                            true),
                                            ""));
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_STRSQL
                            .getValue(), strSql);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000427InformeDeContratosPorX", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }
        catch (JRException | IOException | ParseException | SysmanException
                        | SystemException ex)
        {
            Logger.getLogger(InformedecontratosporpresupuestoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void generaInformeContratosRubro(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSql;
            reemplazar.put(fechaInicialCons,
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put(fechaFinalCons,
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("rubroInicial", rubroInicial);
            reemplazar.put("rubroFinal", rubroFinal);
            reemplazar.put("tipoContratoInicial", tipoContratoInicial);
            reemplazar.put("tipoContratoFinal", tipoContratoFinal);
            strSql = Reporteador.resuelveConsulta("000445InformeCONTRATOSRUBRO",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), nombreCompania);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial,
                                            formatoFechaCons));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal,
                                            formatoFechaCons));
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_STRSQL
                            .getValue(), strSql);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000445InformeCONTRATOSRUBRO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (SysmanException | JRException | IOException
                        | ParseException ex)
        {
            Logger.getLogger(InformedecontratosporpresupuestoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void generaInformeEspecial(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSql;
            reemplazar.put(fechaInicialCons,
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put(fechaFinalCons,
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("rubroInicial", rubroInicial);
            reemplazar.put("rubroFinal", rubroFinal);
            strSql = Reporteador.resuelveConsulta("000448ContratosPorRubro",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), nombreCompania);
            parametros.put("PR_TITULO",
                            idioma.getString("TB_TB3463").replace(
                                            "s$fechaInicial$s",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial,
                                                            formatoFechaCons))
                                            .replace("s$fechaFinal$s",
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            formatoFechaCons)));
            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_STRSQL
                            .getValue(), strSql);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000448ContratosPorRubro", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }

        catch (SysmanException | JRException | IOException
                        | ParseException ex)
        {
            Logger.getLogger(InformedecontratosporpresupuestoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void generaInformeContratosTercero(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;

        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            String formatoReporte = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "FORMATO CERTIFICADO DE CONTRATO",
                                            modulo, new Date(), false),
                                            "000449INFORMECONTRATOSTERCERO");

            reemplazar.put(fechaInicialCons,
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put(fechaFinalCons,
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("terceroCed", terceroCed);
            reemplazar.put("terceroCedFinal", terceroCedFinal);
            reemplazar.put("tipoContratoInicial", tipoContratoInicial);
            reemplazar.put("tipoContratoFinal", tipoContratoFinal);

            String titulo = idioma.getString("TB_TB3464").replace(
                            "s$fechaInicial$s",
                            SysmanFunciones.convertirAFechaCadena(fechaInicial))
                            .toString()
                            .replace("s$fechaFinal$s", SysmanFunciones
                                            .convertirAFechaCadena(
                                                            fechaFinal))
                            .toString();

            parametros.put("PR_TITULO", titulo);

            parametros.put(InformedecontratosporpresupuestoControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), nombreCompania);
            parametros.put("PR_NOMBRE_JEFE_AREA_DE_CONTRATACION",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE JEFE AREA DE CONTRATACION",
                                            modulo, new Date(), true));
            parametros.put("PR_NOMBRE_CARGO_OFICINA_DE_CONTRATACION",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE CARGO OFICINA DE CONTRATACION",
                                            modulo, new Date(), true));

            Reporteador.resuelveConsulta(formatoReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(formatoReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException | ParseException
                        | SystemException ex)
        {
            Logger.getLogger(InformedecontratosporpresupuestoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public String getRubroFinal()
    {
        return rubroFinal;
    }

    public void setRubroFinal(String rubroFinal)
    {
        this.rubroFinal = rubroFinal;
    }

    public String getRubroInicial()
    {
        return rubroInicial;
    }

    public void setRubroInicial(String rubroInicial)
    {
        this.rubroInicial = rubroInicial;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public String getTerceroCed()
    {
        return terceroCed;
    }

    public void setTerceroCed(String terceroCed)
    {
        this.terceroCed = terceroCed;
    }

    public String getTipoContratoInicial()
    {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial)
    {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal()
    {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal)
    {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    public String getTerceroCedFinal()
    {
        return terceroCedFinal;
    }

    public void setTerceroCedFinal(String terceroCedFinal)
    {
        this.terceroCedFinal = terceroCedFinal;
    }

    public String getPresupuesto()
    {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto)
    {
        this.presupuesto = presupuesto;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public String getNombreTerceroFinal()
    {
        return nombreTerceroFinal;
    }

    public void setNombreTerceroFinal(String nombreTerceroFinal)
    {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }

    public String getNombreTerceroIni()
    {
        return nombreTerceroIni;
    }

    public void setNombreTerceroIni(String nombreTerceroIni)
    {
        this.nombreTerceroIni = nombreTerceroIni;
    }

    public RegistroDataModelImpl getListaRubroFinal()
    {
        return listaRubroFinal;
    }

    public void setListaRubroFinal(RegistroDataModelImpl listaRubroFinal)
    {
        this.listaRubroFinal = listaRubroFinal;
    }

    public RegistroDataModelImpl getListaRubroInicial()
    {
        return listaRubroInicial;
    }

    public void setListaRubroInicial(RegistroDataModelImpl listaRubroInicial)
    {
        this.listaRubroInicial = listaRubroInicial;
    }

    public List<Registro> getListaTipo()
    {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    public RegistroDataModelImpl getListaTipoContratoInicial()
    {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial)
    {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public RegistroDataModelImpl getListaTipoContratoFinal()
    {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        RegistroDataModelImpl listaTipoContratoFinal)
    {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    public RegistroDataModelImpl getListaPresupuesto()
    {
        return listaPresupuesto;
    }

    public void setListapresupuesto(RegistroDataModelImpl listaPresupuesto)
    {
        this.listaPresupuesto = listaPresupuesto;
    }

    public RegistroDataModelImpl getListaTerceroCed()
    {
        return listaTerceroCed;
    }

    public void setListaTerceroCed(RegistroDataModelImpl listaTerceroCed)
    {
        this.listaTerceroCed = listaTerceroCed;
    }

    public RegistroDataModelImpl getListaTerceroCedFinal()
    {
        return listaTerceroCedFinal;
    }

    public void setListaterceroCedFinal(
        RegistroDataModelImpl listaTerceroCedFinal)
    {
        this.listaTerceroCedFinal = listaTerceroCedFinal;
    }

    public String getTituloModal()
    {
        return tituloModal;
    }

    public void setTituloModal(String tituloModal)
    {
        this.tituloModal = tituloModal;
    }

    public boolean getVisibleResumenContTerceros()
    {
        return visibleResumenContTerceros;
    }

    public void setVisibleResumenContTerceros(
        boolean visibleResumenContTerceros)
    {
        this.visibleResumenContTerceros = visibleResumenContTerceros;
    }

    public boolean getVisibleTerceroInicial()
    {
        return visibleTerceroInicial;
    }

    public void setVisibleTerceroInicial(boolean visibleTerceroInicial)
    {
        this.visibleTerceroInicial = visibleTerceroInicial;
    }

    public boolean getVisibleTerceroFinal()
    {
        return visibleTerceroFinal;
    }

    public void setVisibleTerceroFinal(boolean visibleTerceroFinal)
    {
        this.visibleTerceroFinal = visibleTerceroFinal;
    }

    public boolean getVisibleTipoCInicial()
    {
        return visibleTipoCInicial;
    }

    public void setVisibleTipoCInicial(boolean visibleTipoCInicial)
    {
        this.visibleTipoCInicial = visibleTipoCInicial;
    }

    public boolean getVisibleTipoCFinal()
    {
        return visibleTipoCFinal;
    }

    public void setVisibleTipoCFinal(boolean visibleTipoCFinal)
    {
        this.visibleTipoCFinal = visibleTipoCFinal;
    }

    public boolean getVisibleRubroInicial()
    {
        return visibleRubroInicial;
    }

    public void setVisibleRubroInicial(boolean visibleRubroInicial)
    {
        this.visibleRubroInicial = visibleRubroInicial;
    }

    public boolean getVisibleRubroFinal()
    {
        return visibleRubroFinal;
    }

    public void setVisibleRubroFinal(boolean visibleRubroFinal)
    {
        this.visibleRubroFinal = visibleRubroFinal;
    }

    public boolean getVisiblePresupuesto()
    {
        return visiblePresupuesto;
    }

    public void setVisiblePresupuesto(boolean visiblePresupuesto)
    {
        this.visiblePresupuesto = visiblePresupuesto;
    }

    public boolean getVisibleTipo()
    {
        return visibleTipo;
    }

    public void setVisibleTipo(boolean visibleTipo)
    {
        this.visibleTipo = visibleTipo;
    }

    public boolean getVisibleFechaInicial()
    {
        return visibleFechaInicial;
    }

    public void setVisibleFechaInicial(boolean visibleFechaInicial)
    {
        this.visibleFechaInicial = visibleFechaInicial;
    }

    public boolean getVisibleFechaFinal()
    {
        return visibleFechaFinal;
    }

    public void setVisibleFechaFinal(boolean visibleFechaFinal)
    {
        this.visibleFechaFinal = visibleFechaFinal;
    }

    public String getNombreContratoInicial()
    {
        return nombreContratoInicial;
    }

    public void setNombreContratoInicial(String nombreContratoInicial)
    {
        this.nombreContratoInicial = nombreContratoInicial;
    }

    public String getNombreContratoFinal()
    {
        return nombreContratoFinal;
    }

    public void setNombreContratoFinal(String nombreContratoFinal)
    {
        this.nombreContratoFinal = nombreContratoFinal;
    }

    public String getNombreRubroInicial()
    {
        return nombreRubroInicial;
    }

    public void setNombreRubroInicial(String nombreRubroInicial)
    {
        this.nombreRubroInicial = nombreRubroInicial;
    }

    public String getNombreRubroFinal()
    {
        return nombreRubroFinal;
    }

    public void setNombreRubroFinal(String nombreRubroFinal)
    {
        this.nombreRubroFinal = nombreRubroFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isCheckResumen()
    {
        return checkResumen;
    }

    public void setCheckResumen(boolean checkResumen)
    {
        this.checkResumen = checkResumen;
    }

    public String getTopBotones()
    {
        return topBotones;
    }

    public void setTopBotones(String topBotones)
    {
        this.topBotones = topBotones;
    }

    @Override
    public void abrirFormulario()
    {
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public int getNumFormulario()
    {
        return numFormulario;
    }

    public String getMenuActual()
    {
        return menuActual;
    }

    public void setMenuActual(String menuActual)
    {
        this.menuActual = menuActual;
    }

    public String getTituloLabel()
    {
        return tituloLabel;
    }

    public void setTituloLabel(String tituloLabel)
    {
        this.tituloLabel = tituloLabel;
    }
    
	public boolean isMostrarMiles() {
		return mostrarMiles;
	}

	public void setMostrarMiles(boolean mostrarMiles) {
		this.mostrarMiles = mostrarMiles;
	}

}