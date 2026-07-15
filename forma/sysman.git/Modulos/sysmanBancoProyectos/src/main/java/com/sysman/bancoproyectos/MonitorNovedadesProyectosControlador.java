package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.MonitorNovedadesProyectosControladorEnum;
import com.sysman.bancoproyectos.enums.MonitorNovedadesProyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
 * @author acaceres
 * @version 1, 25/08/2015
 *
 * @author spina
 * @version 2, 26/09/2017 - se refactoriza para dss, depuracion sonar
 * y ejbs
 */
@ManagedBean
@ViewScoped
public class MonitorNovedadesProyectosControlador
                extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private final String modulo;
    private String descripcion;
    private String nivel;
    private final String cTodas;
    private final String cTodos;
    private String tipoTFiltrar;
    private String estadoFiltrar;
    private String proyecto;
    private String cmbDependencia;
    private String tipoT;
    private String nombreTipoT;
    private String codigo;
    private String claseT;
    private String nombreT;
    private String nombreProyecto;
    private String rid;
    private String ano;
    private String anio;
    private Date fechaInicial;
    private Date fechaFinal;
    private String totalValor;
    private String totalAprobado;
    private String claseNovedad;
    private String tipoNovedad;
    private String auxiliar;
    private String sSql;
    private String nombreDependencia;
    private String nombreNivel;
    private String combo;
    private boolean bloqueaTitulo;
    private boolean bloqueaDescripcion;
    private boolean bloqueaProyecto;
    private RegistroDataModelImpl listaTipoTFiltrar;
    private RegistroDataModelImpl listaTipoTFiltrarE;
    private RegistroDataModelImpl listaTipoT;
    private RegistroDataModelImpl listaTipoTE;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listacmbNiveles;
    private RegistroDataModelImpl listacmbNivelesE;
    private RegistroDataModelImpl listaProyectoE;
    private RegistroDataModelImpl listacmbDependencia;
    private RegistroDataModelImpl listacmbDependenciaE;
    private RegistroDataModelImpl listacmbDescripcion;
    private RegistroDataModelImpl listacmbDescripcionE;
    private RegistroDataModelImpl listacmbCombo;

    private RegistroDataModelImpl listacmbComboE;
    private List<Registro> listaTotales;
    private List<Registro> listacmbAnio;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of MonitorNovedadesProyectosControlador
     */
    public MonitorNovedadesProyectosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = "0";
        cTodas = "Todas";
        cTodos = "Todos";
        combo = "0";
        nivel = "0";
        descripcion = "";
        try
        {

            numFormulario = GeneralCodigoFormaEnum.MONITOR_NOVEDADES_PROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(MonitorNovedadesProyectosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {

        tabla = MonitorNovedadesProyectosControladorEnum.BPNOVEDADPROYECTO
                        .getValue();
        buscarLlave();
        registro = new Registro();
        /*
         * Inicializacion de los filtros, para cargar el formulario.
         */
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null)
        {

            try
            {
                proyecto = SysmanFunciones
                                .nvl(parametrosEntrada.get("proyectoM"), "")
                                .toString();
                tipoTFiltrar = SysmanFunciones
                                .nvl(parametrosEntrada.get("tipoTFiltrarM"), "")
                                .toString();
                estadoFiltrar = SysmanFunciones
                                .nvl(parametrosEntrada.get("estadoFiltrarM"),
                                                "")
                                .toString();
                cmbDependencia = SysmanFunciones
                                .nvl(parametrosEntrada.get("cmbDependenciaM"),
                                                "")
                                .toString();
                fechaInicial = SysmanFunciones
                                .convertirAFecha((String) parametrosEntrada
                                                .get("fechaInicialM"));
                fechaFinal = SysmanFunciones.convertirAFecha(
                                (String) parametrosEntrada.get("fechaFinalM"));
            }
            catch (ParseException ex)
            {
                Logger.getLogger(MonitorNovedadesProyectosControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
            }
            SessionUtil.cleanFlash();

        }

        cargarListaProyecto();
        cargarListacmbDependencia();
        cargarlistacmbAnio();
        cargarlistacmbNiveles();
        cargarlistacmbNivelesE();
        cargarListacmbCombo();
        cargarListacmbComboE();
        cargarListacmbDescripcion();
        cargarListacmbDescripcionE();
        cargarListaTipoTFiltrar();
        cargarListaTipoTFiltrarE();
        cargarListaTipoT();
        cargarListaTipoTE();
        bloqueaTitulo = true;
        bloqueaDescripcion = true;
        bloqueaProyecto = true;
        nombreDependencia = cTodas;
        inicializarValores();
        reasignarOrigen();
        abrirFormulario();

    }

    public void inicializarValores()
    {
        proyecto = cTodos;
        anio = "";
        // String.valueOf(SysmanFunciones
        // .ano(new Date()));
        nivel = "";
        combo = "";
        descripcion = "";
        tipoTFiltrar = cTodos;
        estadoFiltrar = "T";
        cmbDependencia = cTodas;
        fechaInicial = new Date();
        fechaFinal = SysmanFunciones.sumarRestarDiasFecha(fechaInicial, 30);
        nombreProyecto = cTodos;
        nombreT = cTodas;

    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(MonitorNovedadesProyectosControladorUrlEnum.URL3420
                                        .getValue());

        try
        {
            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            // parametrosListado.put(MonitorNovedadesProyectosControladorEnum.ANIO
            // .getValue(), anio);
            //
            // parametrosListado.put(MonitorNovedadesProyectosControladorEnum.NIVEL
            // .getValue(), nivel);

            parametrosListado
                            .put(MonitorNovedadesProyectosControladorEnum.NOVORIGINAL
                                            .getValue(), combo);

            parametrosListado.put(GeneralParameterEnum.DESCRIPCION.getName(),
                            descripcion);

            parametrosListado.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametrosListado.put(GeneralParameterEnum.FECHAFINAL.getName(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal));
            parametrosListado
                            .put(MonitorNovedadesProyectosControladorEnum.FILTROPROYECTO
                                            .getValue(), proyecto);

            parametrosListado
                            .put(MonitorNovedadesProyectosControladorEnum.FILTRODEPENDENCIA
                                            .getValue(), cmbDependencia);
            parametrosListado
                            .put(MonitorNovedadesProyectosControladorEnum.FILTROTIPOT
                                            .getValue(), tipoTFiltrar);
            parametrosListado
                            .put(MonitorNovedadesProyectosControladorEnum.FILTROESTADO
                                            .getValue(), estadoFiltrar);
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void reiniciarTotales()
    {
        totalValor = "0";
        totalAprobado = "0";
    }

    private boolean validarCondiciones()
    {
        boolean rta = true;
        if (SysmanFunciones.validarVariableVacio(proyecto))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2423"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cmbDependencia))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2424"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoTFiltrar))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2426"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(estadoFiltrar))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2425"));
            rta = false;
        }

        if (fechaInicial.after(fechaFinal))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3713"));
            rta = false;
        }

        if (!rta)
        {
            reiniciarTotales();
        }
        return rta;
    }

    /*
     * Dependiendo de cada uno de los filtros se cargara el origen de
     * datos y se reiniciaran los totales de la grilla. Ademas se
     * filtra el origen de por el filtro que seleccione el usuario.
     */
    public void actualizarDatos()
    {
        if (!validarCondiciones())
        {
            return;
        }
        reasignarOrigen();
        calcularTotales();
    }

    /*
     * Metodo para calcular los totales en la grila.
     */
    private void calcularTotales()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                        fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(),
                        fechaFinal);
        param.put(MonitorNovedadesProyectosControladorEnum.FILTROPROYECTO
                        .getValue(), proyecto);
        param.put(MonitorNovedadesProyectosControladorEnum.FILTRODEPENDENCIA
                        .getValue(), cmbDependencia);
        param.put(MonitorNovedadesProyectosControladorEnum.FILTROTIPOT
                        .getValue(), tipoTFiltrar);
        param.put(MonitorNovedadesProyectosControladorEnum.FILTROESTADO
                        .getValue(), estadoFiltrar);
        try
        {
            listaTotales = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MonitorNovedadesProyectosControladorUrlEnum.URL3415
                                                                            .getValue())
                                            .getUrl(), param));

            if ((listaTotales.get(0).getCampos()
                            .get(MonitorNovedadesProyectosControladorEnum.TOTALVALORSOLICITADO
                                            .getValue()) != null)
                && (listaTotales.get(0).getCampos()
                                .get(MonitorNovedadesProyectosControladorEnum.TOTALVALORAPROBADO
                                                .getValue()) != null))
            {
                DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
                DecimalFormat dblDF = new DecimalFormat("#,##0.00", dfs);
                totalValor = "0".equals(listaTotales.get(0).getCampos()
                                .get(MonitorNovedadesProyectosControladorEnum.TOTALVALORSOLICITADO
                                                .getValue())
                                .toString()) ? "0"
                                    : dblDF.format(Double.parseDouble(
                                                    listaTotales.get(0)
                                                                    .getCampos()
                                                                    .get(MonitorNovedadesProyectosControladorEnum.TOTALVALORSOLICITADO
                                                                                    .getValue())
                                                                    .toString()));
                totalAprobado = "0".equals(listaTotales.get(0).getCampos()
                                .get(MonitorNovedadesProyectosControladorEnum.TOTALVALORAPROBADO
                                                .getValue())
                                .toString()) ? "0"
                                    : dblDF.format(Double.parseDouble(
                                                    listaTotales.get(0)
                                                                    .getCampos()
                                                                    .get(MonitorNovedadesProyectosControladorEnum.TOTALVALORAPROBADO
                                                                                    .getValue())
                                                                    .toString()));
            }
            else
            {
                reiniciarTotales();
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaProyecto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3411
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaProyectoE()
    {
        listaProyectoE = listaProyecto;
    }

    public void cargarListacmbDependencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacmbDependenciaE()
    {
        listacmbDependenciaE = listacmbDependencia;
    }

    public void cargarListaTipoTFiltrar()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3413
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoTFiltrar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MonitorNovedadesProyectosControladorEnum.TIPOT
                                        .getValue());
    }

    public void cargarlistacmbAnio()
    {
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            try
            {
                listacmbAnio = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                MonitorNovedadesProyectosControladorUrlEnum.URL3416
                                                                                .getValue())
                                                .getUrl(), param));
            }
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }

    }

    public void cargarlistacmbNiveles()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3417
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MonitorNovedadesProyectosControladorEnum.ANIO.getValue(),
                        anio);
        listacmbNiveles = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MonitorNovedadesProyectosControladorEnum.DIGITOS
                                        .getValue());

    }

    public void cargarlistacmbNivelesE()
    {

        listacmbNivelesE = listacmbNiveles;
    }

    public void cargarListacmbDescripcion()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3418
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MonitorNovedadesProyectosControladorEnum.NIVEL.getValue(),
                        nivel);
        listacmbDescripcion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacmbDescripcion
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacmbDescripcionE()
    {

        listacmbDescripcionE = listacmbDescripcion;

    }

    // public void cargarListacmbDescripcion()
    // {
    // UrlBean urlBean = UrlServiceUtil.getInstance()
    // .getUrlServiceByUrlByEnumID(
    // MonitorNovedadesProyectosControladorUrlEnum.URL3418
    // .getValue());
    // Map<String, Object> param = new TreeMap<>();
    // param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    // param.put(MonitorNovedadesProyectosControladorEnum.NIVEL.getValue(),
    // nivel);
    // listacmbDescripcion = new
    // RegistroDataModelImpl(urlBean.getUrl(),
    // urlBean.getUrlConteo().getUrl(), param, true,
    // GeneralParameterEnum.CODIGO.getName());
    // }
    //
    // public void cargarListacmbDescripcionE()
    // {
    // UrlBean urlBean = UrlServiceUtil.getInstance()
    // .getUrlServiceByUrlByEnumID(
    // MonitorNovedadesProyectosControladorUrlEnum.URL3418
    // .getValue());
    // Map<String, Object> param = new TreeMap<>();
    // param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    // param.put("CODIGO", nivel);
    // listacmbDescripcionE = new
    // RegistroDataModelImpl(urlBean.getUrl(),
    // urlBean.getUrlConteo().getUrl(), param, true,
    // GeneralParameterEnum.CODIGO.getName());
    // }

    public void cargarListacmbCombo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3419
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),
                        fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(),
                        fechaFinal);
        param.put(MonitorNovedadesProyectosControladorEnum.FILTROTIPOT
                        .getValue(), tipoTFiltrar);

        listacmbCombo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MonitorNovedadesProyectosControladorEnum.NOVORIGINAL
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listacmbCombo
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacmbComboE()
    {

        listacmbComboE = listacmbCombo;
    }

    public void cargarListaTipoTFiltrarE()
    {
        listaTipoTFiltrarE = listaTipoTFiltrar;
    }

    public void cargarListaTipoT()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonitorNovedadesProyectosControladorUrlEnum.URL3414
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MonitorNovedadesProyectosControladorEnum.CLASET
                                        .getValue());

        cargarListacmbCombo();
    }

    public void cargarListaTipoTE()
    {
        listaTipoTE = listaTipoT;
    }

    public void oprimirver()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void getReporte(ReportesBean.FORMATOS formatos)
    {
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("fechainicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechafinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("filtroproyecto", proyecto);
            reemplazar.put("filtrodependencia", cmbDependencia);
            reemplazar.put("filtrotipot", tipoTFiltrar);
            reemplazar.put("filtroestado", estadoFiltrar);

            Map<String, Object> parametros = new HashMap<>();

            String nombreEstado = estadoFiltrar;

            switch (estadoFiltrar)
            {
            case "V":
                nombreEstado = "Vigente";
                break;
            case "A":
                nombreEstado = "Anulada";
                break;
            case "N":
                nombreEstado = "No Aprobado";
                break;
            case "AP":
                nombreEstado = "Aprobada";
                break;
            case "T":
                nombreEstado = cTodos;
                break;
            default:
                break;
            }

            parametros.put("PR_ESTADO", nombreEstado);
            parametros.put("PR_PROYECTO", proyecto);
            parametros.put("PR_DEPENDENCIA", nombreDependencia);
            parametros.put("PR_TIPO", nombreT);

            Reporteador.resuelveConsulta("000171RPTMonitorNovedades",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000171RPTMonitorNovedades", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formatos);

        }
        catch (SysmanException | JRException | IOException ex)
        {
            Logger.getLogger(MonitorNovedadesProyectosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipoTFiltrar()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEstadoFiltrar()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbAnio()
    {
        // <CODIGO_DESARROLLADO>
        nivel = " ";
        descripcion = " ";
        cargarlistacmbNiveles();
        reasignarOrigen();
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbDependencia()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbNivelesC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea

        // descripcion = " ";

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CODIGO",
                        nivel);

        reasignarOrigen();
        actualizarDatos();

        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ");
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbNiveles()
    {
        // <CODIGO_DESARROLLADO>

        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarObjeto1()
    {

    }

    public void cambiarcmbDescripcion()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechainicial()
    {
        // <CODIGO_DESARROLLADO>
        actualizarDatos();
        // </CODIGO_DESARROLLADO>

    }

    public void oprimirInforme()
    {
        // <CODIGO_DESARROLLADO>
        getReporte(FORMATOS.PDF);

        // <CODIGO DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        getReporte(FORMATOS.EXCEL);
    }

    /*
     * Metodo para redireccionar al formulario solicitudCDP. Al
     * oprimir el boton ver se envian los parametros ano,
     * argNovedades, rid, bpNovedad, claseNovedad, TipoNovedad,
     * codigo.
     */
    public void oprimircmdVer(Registro reg, int indice) // parametro
                                                        // indice
                                                        // necesario
                                                        // para el
                                                        // xhtml
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        tipoT = SysmanFunciones.nvl(reg.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.TIPOT
                                        .getValue()),
                        "").toString();
        nombreTipoT = SysmanFunciones.nvl(reg.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.CLASET
                                        .getValue()),
                        "").toString();

        String strTipoClase = tipoT + "" + nombreTipoT;
        codigo = SysmanFunciones.nvl(
                        reg.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        claseNovedad = SysmanFunciones.nvl(reg.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.CLASET
                                        .getValue()),
                        "")
                        .toString();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (!codigo.isEmpty())

        {
            /*
             * ABRIR EL FORMULARIO FRMSUBDNOVEDADESPROYECTO DESDE EL
             * BOTON NOVEDADES PROYECTO
             */
            ano = SysmanFunciones.nvl(reg.getCampos()
                            .get(GeneralParameterEnum.VIGENCIA.getName()), "0")
                            .toString();

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ano", ano);
            parametros.put("argNovedades", strTipoClase + "" + codigo);
            parametros.put("rid", reg.getLlave());
            parametros.put("bpNovedad",
                            (reg.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName())).toString());
            parametros.put("claseNovedad",
                            reg.getCampos().get(
                                            MonitorNovedadesProyectosControladorEnum.CLASET
                                                            .getValue())
                                            .toString());
            parametros.put("tipoNovedad", tipoT);
            parametros.put("codigo",
                            SysmanFunciones.nvl(reg.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()),
                                            "")
                                            .toString());
            parametros.put("proyectoM", proyecto);
            parametros.put("tipoTFiltrarM", tipoTFiltrar);
            parametros.put("estadoFiltrarM", estadoFiltrar);
            parametros.put("cmbDependenciaM", cmbDependencia);
            parametros.put("fechaInicialM", dateFormat.format(fechaInicial));
            parametros.put("fechaFinalM", dateFormat.format(fechaFinal));

            Direccionador direccionador = new Direccionador();
            direccionador.setParametros(parametros);
            direccionador.setNumForm(String.valueOf(
                            GeneralCodigoFormaEnum.FRM_SOLICITUD_CDP_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador, modulo);

        }
        else

        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2428"));
        }

    }

    public void getSolicitudCdp(FORMATOS formatos)
    {
        // CODIGO DESARROLLADO
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoT", tipoT);
            reemplazar.put("claseT", claseT);
            reemplazar.put("codigo", codigo);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String jefeBancoProyectos = ejbSysmanUtil.consultarParametro(
                            compania,
                            "JEFE DE BANCO DE PROYECTOS", modulo, new Date(),
                            true);

            String profesionalUniversitario = ejbSysmanUtil.consultarParametro(
                            compania,
                            "PROFESIONAL UNIVERSITARIO EGR ESM", modulo,
                            new Date(),
                            true);

            String nombreCompania = SessionUtil.getCompaniaIngreso()
                            .getNombre();
            String reporte = "000175SolicitudCDP";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);

            // long total = service.getConteoConsulta(strSql);
            // if (total <= 0)
            // {
            // JsfUtil.agregarMensajeAlerta(idioma.getString(
            // "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            // return;
            // }

            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_JEFE_BANCO_PROYECTOS", jefeBancoProyectos);
            parametros.put("PR_PROFESIONAL_UNIVERSITARIO_EGR_ESM",
                            profesionalUniversitario);
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(MonitorNovedadesProyectosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        // CODIGO DESARROLLADO
    }

    /*
     * Metodo para generar el reporte 000175SolicitudCDP.
     */
    public void oprimirCmdImprimir(Registro reg, int indice) // parametro
                                                             // indice
                                                             // necesario
                                                             // por el
                                                             // xhtml
    {
        // <CODIGO_DESARROLLADO>
        tipoT = SysmanFunciones.nvl(reg.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.TIPOT
                                        .getValue()),
                        "")
                        .toString();
        codigo = SysmanFunciones.nvl(
                        reg.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        claseT = SysmanFunciones.nvl(reg.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.CLASET
                                        .getValue()),
                        "").toString();
        if (!codigo.isEmpty() && "B".equals(claseT))
        {
            getSolicitudCdp(FORMATOS.PDF);
        }
        else
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2429"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaProyecto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proyecto = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreProyecto = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.NOMBREPROYECTO
                                        .getValue()),
                        "").toString();
        actualizarDatos();

    }

    public void seleccionarFilaTipoTFiltrar(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        tipoTFiltrar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.TIPOT
                                        .getValue()),
                        "").toString();
        nombreT = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        cargarListacmbCombo();
        actualizarDatos();
    }

    public void seleccionarFilaTipoTE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get(MonitorNovedadesProyectosControladorEnum.CLASET
                                                        .getValue()),
                        "").toString();
        actualizarDatos();

    }

    public void seleccionarFilacmbDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cmbDependencia = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();

        nombreDependencia = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        actualizarDatos();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbNiveles
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilacmbNiveles(SelectEvent event)
    {
        descripcion = " ";
        Registro registroAux = (Registro) event.getObject();
        nivel = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(MonitorNovedadesProyectosControladorEnum.DIGITOS
                                        .getValue()),
                        "")
                        .toString();

        cargarListacmbDescripcion();
        actualizarDatos();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbDescripcion
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbDescripcion(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        descripcion = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();

        actualizarDatos();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCombo
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    // public void seleccionarFilacmbCombo(SelectEvent event)
    // {
    // Registro registroAux = (Registro) event.getObject();
    // combo = (String) registroAux.getCampos().get("NOVEDAD");
    // }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbDescripcion
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilacmbCombo(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();

        combo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOVEDAD.getName()),
                        "")
                        .toString();

        actualizarDatos();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCombo
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    // public void seleccionarFilacmbComboE(SelectEvent event)
    // {
    // Registro registroAux = (Registro) event.getObject();
    // auxiliar = (String) registroAux.getCampos().get("NOVORIGINAL");
    //
    // actualizarDatos();
    // }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .remove(MonitorNovedadesProyectosControladorEnum.NOMBREESTADO
                                        .getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(MonitorNovedadesProyectosControladorEnum.NOMBREESTADO
                                        .getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(MonitorNovedadesProyectosControladorEnum.NOMBREESTADO
                                        .getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // Metodo generado por herencia
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo generado por herencia
    }

    public boolean isBloqueaProyecto()
    {
        return bloqueaProyecto;
    }

    public void setBloqueaProyecto(boolean bloqueaProyecto)
    {
        this.bloqueaProyecto = bloqueaProyecto;
    }

    public boolean isBloqueaDescripcion()
    {
        return bloqueaDescripcion;
    }

    public void setBloqueaDescripcion(boolean bloqueaDescripcion)
    {
        this.bloqueaDescripcion = bloqueaDescripcion;
    }

    public boolean isBloqueaTitulo()
    {
        return bloqueaTitulo;
    }

    public void setBloqueaTitulo(boolean bloqueaTitulo)
    {
        this.bloqueaTitulo = bloqueaTitulo;
    }

    public String getTotalValor()
    {
        return totalValor;
    }

    public String getTipoT()
    {
        return tipoT;
    }

    public String getCombo()
    {
        return combo;
    }

    /**
     * Asigna la variable combo
     * 
     * @param combo
     * Variable a asignar en combo
     */
    public void setCombo(String combo)
    {
        this.combo = combo;
    }

    public void setTipoT(String tipoT)
    {
        this.tipoT = tipoT;
    }

    public String getNombreTipoT()
    {
        return nombreTipoT;
    }

    public void setNombreTipoT(String nombreTipoT)
    {
        this.nombreTipoT = nombreTipoT;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getClaseT()
    {
        return claseT;
    }

    public void setClaseT(String claseT)
    {
        this.claseT = claseT;
    }

    public String getClaseNovedad()
    {
        return claseNovedad;
    }

    public void setClaseNovedad(String claseNovedad)
    {
        this.claseNovedad = claseNovedad;
    }

    public String getTipoNovedad()
    {
        return tipoNovedad;
    }

    public void setTipoNovedad(String tipoNovedad)
    {
        this.tipoNovedad = tipoNovedad;
    }

    public String getsSql()
    {
        return sSql;
    }

    public void setsSql(String sSql)
    {
        this.sSql = sSql;
    }

    public List<Registro> getListaTotales()
    {
        return listaTotales;
    }

    public void setListaTotales(List<Registro> listaTotales)
    {
        this.listaTotales = listaTotales;
    }

    public void setTotalValor(String totalValor)
    {
        this.totalValor = totalValor;
    }

    public String getTotalAprobado()
    {
        return totalAprobado;
    }

    public void setTotalAprobado(String totalAprobado)
    {
        this.totalAprobado = totalAprobado;
    }

    @Override
    public FormContinuoService getService()
    {
        return service;
    }

    @Override
    public void setService(FormContinuoService service)
    {
        this.service = service;
    }

    public RegistroDataModelImpl getListaProyecto()
    {
        return listaProyecto;
    }

    public void setListaProyecto(RegistroDataModelImpl listaProyecto)
    {
        this.listaProyecto = listaProyecto;
    }

    public RegistroDataModelImpl getListaProyectoE()
    {
        return listaProyectoE;
    }

    public void setListaProyectoE(RegistroDataModelImpl listaProyectoE)
    {
        this.listaProyectoE = listaProyectoE;
    }

    public RegistroDataModelImpl getListacmbDependencia()
    {
        return listacmbDependencia;
    }

    public void setListacmbDependencia(
        RegistroDataModelImpl listacmbDependencia)
    {
        this.listacmbDependencia = listacmbDependencia;
    }

    public RegistroDataModelImpl getListacmbDependenciaE()
    {
        return listacmbDependenciaE;
    }

    public void setListacmbDependenciaE(
        RegistroDataModelImpl listacmbDependenciaE)
    {
        this.listacmbDependenciaE = listacmbDependenciaE;
    }

    public RegistroDataModelImpl getListaTipoTFiltrar()
    {
        return listaTipoTFiltrar;
    }

    public void setListaTipoTFiltrar(RegistroDataModelImpl listaTipoTFiltrar)
    {
        this.listaTipoTFiltrar = listaTipoTFiltrar;
    }

    public RegistroDataModelImpl getListaTipoTFiltrarE()
    {
        return listaTipoTFiltrarE;
    }

    public void setListaTipoTFiltrarE(RegistroDataModelImpl listaTipoTFiltrarE)
    {
        this.listaTipoTFiltrarE = listaTipoTFiltrarE;
    }

    public RegistroDataModelImpl getListaTipoT()
    {
        return listaTipoT;
    }

    public void setListaTipoT(RegistroDataModelImpl listaTipoT)
    {
        this.listaTipoT = listaTipoT;
    }

    public RegistroDataModelImpl getListaTipoTE()
    {
        return listaTipoTE;
    }

    public void setListaTipoTE(RegistroDataModelImpl listaTipoTE)
    {
        this.listaTipoTE = listaTipoTE;
    }

    public String getRid()
    {
        return rid;
    }

    public void setRid(String rid)
    {
        this.rid = rid;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getTipoTFiltrar()
    {
        return tipoTFiltrar;
    }

    public void setTipoTFiltrar(String tipoTFiltrar)
    {
        this.tipoTFiltrar = tipoTFiltrar;
    }

    public String getEstadoFiltrar()
    {
        return estadoFiltrar;
    }

    public void setEstadoFiltrar(String estadoFiltrar)
    {
        this.estadoFiltrar = estadoFiltrar;
    }

    public String getNombreT()
    {
        return nombreT;
    }

    public void setNombreT(String nombreT)
    {
        this.nombreT = nombreT;
    }

    public String getNombreProyecto()
    {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto)
    {
        this.nombreProyecto = nombreProyecto;
    }

    public String getProyecto()
    {
        return proyecto;
    }

    public void setProyecto(String proyecto)
    {
        this.proyecto = proyecto;
    }

    public String getCmbDependencia()
    {
        return cmbDependencia;
    }

    public void setCmbDependencia(String cmbDependencia)
    {
        this.cmbDependencia = cmbDependencia;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListacmbAnio()
    {
        return listacmbAnio;
    }

    public void setListacmbAnio(List<Registro> listacmbAnio)
    {
        this.listacmbAnio = listacmbAnio;
    }

    public RegistroDataModelImpl getListacmbNiveles()
    {
        return listacmbNiveles;
    }

    public void setListacmbNiveles(RegistroDataModelImpl listacmbNiveles)
    {
        this.listacmbNiveles = listacmbNiveles;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getNivel()
    {
        return nivel;
    }

    public void setNivel(String nivel)
    {
        this.nivel = nivel;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public String getNombreNivel()
    {
        return nombreNivel;
    }

    public void setNombreNivel(String nombreNivel)
    {
        this.nombreNivel = nombreNivel;
    }

    public RegistroDataModelImpl getListacmbNivelesE()
    {
        return listacmbNivelesE;
    }

    public RegistroDataModelImpl getListacmbDescripcion()
    {
        return listacmbDescripcion;
    }

    public void setListacmbDescripcion(
        RegistroDataModelImpl listacmbDescripcion)
    {
        this.listacmbDescripcion = listacmbDescripcion;
    }

    public RegistroDataModelImpl getListacmbDescripcionE()
    {
        return listacmbDescripcionE;
    }

    public void setListacmbDescripcionE(
        RegistroDataModelImpl listacmbDescripcionE)
    {
        this.listacmbDescripcionE = listacmbDescripcionE;
    }

    public void setListacmbNivelesE(RegistroDataModelImpl listacmbNivelesE)
    {
        this.listacmbNivelesE = listacmbNivelesE;
    }

    public RegistroDataModelImpl getListacmbCombo()
    {
        return listacmbCombo;
    }

    public void setListacmbCombo(RegistroDataModelImpl listacmbCombo)
    {
        this.listacmbCombo = listacmbCombo;
    }

    public RegistroDataModelImpl getListacmbComboE()
    {
        return listacmbComboE;
    }

    public void setListacmbComboE(RegistroDataModelImpl listacmbComboE)
    {
        this.listacmbComboE = listacmbComboE;
    }

}