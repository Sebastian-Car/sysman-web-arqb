// package com.sysman.contabilizar;
//

/*-
 * EnominacontabilidadControlador.java
 *
 * 1.0
 *
 * 18/06/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaDosGeneralRemote;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaGeneralRemote;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaUnoGeneralRemote;
import com.sysman.contabilizar.enums.EnominacontabilidadControladorEnum;
import com.sysman.contabilizar.enums.EnominacontabilidadControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase del controlador Enomina contabilidad.
 *
 * @version 1.0, 18/06/2018
 * @author mzanguna
 */
@ManagedBean
@ViewScoped

public class EnominacontabilidadControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    @EJB
    EjbSysmanUtil ejbSysmanUtiles;
    private String tipoComprobanteInterfaseNomina;

    private String modulo;

    private String companiaNomina;

    private int proceso;

    private int periodo;

    private int ano;

    private int mes;

    private String porEmpleado = "";

    private String centroCosto;

    private String seleccionarEmpleadoInterfazNomina;

    private String tipo;

    private Date fechaInterface;

    private String nombrePeriodo;

    private String nombreCompania;

    private String nombreProceso;

    private String nombreMes;

    private String nombreEmpleado;

    private String nombreCentroCosto;

    private String retorno;
    private boolean visConCuenFonPub = true;
    private boolean visEmpleados = true;
    private boolean visTercero = false;
    private boolean visCentroCosto = false;
    private boolean visEmpleado = false;
    private boolean visIntRetencion = false;
    private boolean visProceso = true;
    private boolean bloqEmpleado = true;
    private boolean visPeriodo = true;
    private boolean visAno = true;
    private boolean visMes = true;
    private boolean visIntCuetaspagar = false;

    private boolean blockTercero = false;
    private boolean blockEmpleado = false;
    private boolean porCentroCosto = false;

    private boolean parProcesoPatrono = false;
    private boolean parSeleccionEmple = false;
    private boolean parReclasifica = false;

    private boolean patronoT = false;
    private boolean empleadoT = false;
    private boolean provisionesT = false;

    private boolean porTercero = false;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
    
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
    
     */
    private RegistroDataModelImpl listaCompaniaNomina;
    /**
    
     */

    private RegistroDataModelImpl listaProceso;
    /**
    
     */
    private RegistroDataModelImpl listaPeriodo;
    /**
    
     */
    private RegistroDataModelImpl listaMes;
    /**
    
     */
    private RegistroDataModelImpl listaPorEmpleado;
    /**
    
     */
    private RegistroDataModelImpl listacmbCentroDeCosto;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilizarNominaGeneralRemote ejbContaNomina;

    @EJB
    private EjbContabilizarNominaUnoGeneralRemote ejbContaNominaUno;

    @EJB
    private EjbContabilizarNominaDosGeneralRemote ejbContaNominaDos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EnominacontabilidadControlador
     */
    public EnominacontabilidadControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.E_NOMINA_CONTABILIDAD_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        abrirFormulario();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCompaniaNomina();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     *
     * @throws SystemException
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        cargarParametros();
        tipo = tipoComprobanteInterfaseNomina;
        fechaInterface = new Date();
        try
        {
            seleccionarEmpleadoInterfazNomina = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                            "SELECCIONAR EMPLEADO INTERFAZ NOMINA", modulo, new Date(), true), "");
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarParametros()
    {
        try
        {
            tipoComprobanteInterfaseNomina = SysmanFunciones.nvlStr(ejbSysmanUtiles.consultarParametro(compania,
                            "TIPO COMPROBANTE INTERFASE NOMINA", modulo, new Date(), true), "");
            parProcesoPatrono = "SI"
                            .equalsIgnoreCase(
                                            (String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                                                            "PROCESO POR PATRONO/EMPLEADO POR TERCERO", modulo, new Date(), false),
                                                            "NO"));

            parSeleccionEmple = "SI"
                            .equalsIgnoreCase((String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SELECCIONAR EMPLEADO INTERFAZ NOMINA", modulo, new Date(), false), "NO"));

            visCentroCosto = "SI"
                            .equalsIgnoreCase((String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "INTERFACES CON UNICO CENTRO DE COSTO", modulo, new Date(), false), "NO"));

            parReclasifica = "SI"
                            .equalsIgnoreCase(
                                            (String) SysmanFunciones.nvl(
                                                            ejbSysmanUtil.consultarParametro(compania,
                                                                            "RECLASIFICACION CUENTAS POR PAGAR NOMINA", modulo, new Date(),
                                                                            false),
                                                            "NO"));
        }
        catch (SystemException e)
        {
            Logger.getLogger(EnominacontabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAno
     *
     *
     */
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(companiaNomina));
        param.put("PROCESO", proceso);
        try
        {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            com.sysman.contabilizar.enums.EnominacontabilidadControladorUrlEnum.URL4440.getValue())
                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCompaniaNomina
     *
     *
     */
    public void cargarListaCompaniaNomina()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EnominacontabilidadControladorUrlEnum.URL4410.getValue());
        listaCompaniaNomina = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, true,
                        EnominacontabilidadControladorEnum.CODIGO.getValue());
    }

    /**
     *
     * Carga la lista listaProceso
     *
     *
     */
    public void cargarListaProceso()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), companiaNomina);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EnominacontabilidadControladorUrlEnum.URL4420.getValue());
        listaProceso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        EnominacontabilidadControladorEnum.IDPROCESO.getValue());
    }

    /**
     *
     * Carga la lista listaPeriodo
     *
     *
     */
    public void cargarListaPeriodo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EnominacontabilidadControladorUrlEnum.URL4430.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(companiaNomina));
        param.put(EnominacontabilidadControladorEnum.IDPROCESO.getValue(), proceso);
        param.put("ANO", ano);
        param.put("MES", mes);

        listaPeriodo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        "PERIODO");
    }

    /**
     *
     * Carga la lista listaMes
     *
     *
     */
    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(companiaNomina));
        param.put("ANO", ano);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EnominacontabilidadControladorUrlEnum.URL4470.getValue());
        listaMes = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "NUMERO");

    }

    /**
     *
     * Carga la lista listaPorEmpleado
     *
     *
     */
    public void cargarListaPorEmpleado()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(companiaNomina));
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        param.put(EnominacontabilidadControladorEnum.PROCESO.getValue(), proceso);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EnominacontabilidadControladorUrlEnum.URL4460.getValue());
        listaPorEmpleado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        "ID_DE_EMPLEADO");
    }

    /**
     *
     * Carga la lista listacmbCentroDeCosto
     *
     *
     */
    public void cargarListacmbCentroDeCosto()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));
        param.put("ANO", ano);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(EnominacontabilidadControladorUrlEnum.URL4450.getValue());
        listacmbCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, EnominacontabilidadControladorEnum.CODIGO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Seleccionar en la vista
     *
     *
     *
     */
    public void oprimirSeleccionar()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Salir en la vista
     *
     *
     *
     */
    public void oprimirSalir()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Configurarcuentasfondospublicos en la vista
     *
     *
     *
     */
    public void oprimirConfigurarcuentasfondospublicos()
    {
        retorno = "960227";
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cerrar", "1");
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.PARAMETROS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);
        SessionUtil.redireccionarMenuFormulario("99905");
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton REVISARCUENTAS04 en la vista
     *
     *
     *
     */
    public void oprimirRevisarCuentas04()
    {
        archivoDescarga = null;
        try
        {
            ejbContaNominaDos.revisarCuentasPlancontable(companiaNomina, compania, ano, mes, periodo, proceso,
                            SessionUtil.getUser().getCodigo());

            HashMap<String, Object> reemplazar = new HashMap<>();
            HashMap<String, Object> parametros = new HashMap<>();
            reemplazar.put("compania", companiaNomina);

            Reporteador.resuelveConsulta("000070ReporteLiquidacion", 6, reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000070ReporteLiquidacion", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.PDF);
            JsfUtil.agregarMensajeInformativo(idioma.getString(EnominacontabilidadControladorEnum.MENSAJE.getValue()));
        }
        catch (JRException | IOException | SysmanException | SystemException e)// SystemException
                                                                               // e)
        {
            Logger.getLogger(EnominacontabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton btnInRetenciones en la vista
     *
     *
     *
     */
    public void oprimirbtnInRetenciones()
    {
        archivoDescarga = null;
        try
        {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbContaNominaUno.contabilizarRetenciones(compania, companiaNomina, proceso,
                                            ano, mes, centroCosto, fechaInterface, SessionUtil.getUser().getCodigo())),
                            "Novedades Contabilizar Retencion.txt");
            JsfUtil.agregarMensajeInformativo(idioma.getString(EnominacontabilidadControladorEnum.MENSAJE.getValue()));
        }
        catch (JRException | IOException | SystemException e)
        {
            Logger.getLogger(EnominacontabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton btnCuentasPagar_A en la vista
     *
     *
     *
     */
    public void oprimirBtnCuentasPagarA()
    {
        archivoDescarga = null;
        try
        {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbContaNominaUno.contabilizarCuentasporpagar(companiaNomina, compania,
                                            proceso, ano, mes, fechaInterface, SessionUtil.getUser().getCodigo())),
                            "Novedades Contabilizar Cuentas por pagar.txt");
            JsfUtil.agregarMensajeInformativo(idioma.getString(EnominacontabilidadControladorEnum.MENSAJE.getValue()));
        }
        catch (JRException | IOException | SystemException e)
        {
            Logger.getLogger(EnominacontabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     *
     */
    public void oprimirAceptar()
    {
        archivoDescarga = null;
        try
        {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(
                                            ejbContaNomina.contabilizarNomina(compania, companiaNomina, ano, mes, periodo, proceso,
                                                            fechaInterface, tipo, visEmpleado, porTercero, porCentroCosto, porEmpleado,
                                                            patronoT, empleadoT, provisionesT, centroCosto,
                                                            SessionUtil.getUser().getCodigo())),
                            "Novedades Contabilizar.txt");
            JsfUtil.agregarMensajeInformativo(idioma.getString(EnominacontabilidadControladorEnum.MENSAJE.getValue()));
        }

        catch (JRException | IOException | SystemException e)
        {
            Logger.getLogger(EnominacontabilidadControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     *
     *
     *
     */
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        if (ano == 0)
        {
            visMes = true;
        }
        else
        {

            visMes = false;
        }
        cargarListacmbCentroDeCosto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorEmpleado
     *
     *
     *
     */
    public void cambiarPorEmpleado()
    {
        // <CODIGO_DESARROLLADO>
        if (parSeleccionEmple)
        {
            visTercero = false;
        }

        if ("NO".equals(seleccionarEmpleadoInterfazNomina))
        {
            visEmpleados = true;

        }
        else
        {
            if (visEmpleado == false)
            {
                visEmpleados = true;
            }
            else
            {
                visEmpleados = false;
            }
        }
        if(!visEmpleado) {
        	porEmpleado = "";
        	nombreEmpleado = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorTercero
     *
     *
     *
     */
    public void cambiarPorTercero()
    {
        if (parProcesoPatrono)
        {
            visTercero = porTercero;
        }
        else
        {
            visTercero = false;
        }
        if (porTercero == false)
        {
            visConCuenFonPub = true;
        }
        else
        {
            visConCuenFonPub = false;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control PorCentrodeCosto
     *
     *
     *
     */
    public void cambiarPorCentrodeCosto()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control verIntRetecion
     *
     *
     *
     */
    public void cambiarverIntRetecion()
    {
        // <CODIGO_DESARROLLADO>
        visIntCuetaspagar = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerIntCuentasPagarA()
    {
        visIntRetencion = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCompaniaNomina
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCompaniaNomina(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        companiaNomina = SysmanFunciones
                        .toString(registroAux.getCampos().get(EnominacontabilidadControladorEnum.CODIGO.getValue()));

        nombreCompania = SysmanFunciones
                        .toString(registroAux.getCampos().get(EnominacontabilidadControladorEnum.NOMBRE.getValue()));

        if (companiaNomina == null)
        {
            visProceso = true;
        }
        else
        {

            visProceso = false;
        }
        cargarListaProceso();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaProceso
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProceso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        proceso = Integer.parseInt(SysmanFunciones
                        .nvl(registroAux.getCampos().get(EnominacontabilidadControladorEnum.IDPROCESO.getValue()), "-1")
                        .toString());

        nombreProceso = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_PROCESO"));

        if (proceso == -1)
        {
            visAno = true;
        }
        else
        {

            visAno = false;
        }
        cargarListaAno();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaPeriodo
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPeriodo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        periodo = Integer.parseInt(SysmanFunciones.nvl(registroAux.getCampos().get("PERIODO"), "-1").toString());

        nombrePeriodo = SysmanFunciones.toString(registroAux.getCampos().get("NOM_PERIODO"));

        if (periodo == -1)
        {
            bloqEmpleado = true;
        }
        else
        {

            bloqEmpleado = false;
        }
        cargarListaPorEmpleado();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaMes
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMes(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        mes = Integer.parseInt(SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "0").toString());

        nombreMes = SysmanFunciones
                        .toString(registroAux.getCampos().get(EnominacontabilidadControladorEnum.NOMBRE.getValue()));
        if (mes == 0)
        {
            visPeriodo = true;
        }
        else
        {

            visPeriodo = false;
        }

        cargarListaPeriodo();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaPorEmpleado
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPorEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        porEmpleado = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_EMPLEADO"));

        nombreEmpleado = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRECOMPLETO"));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listacmbCentroDeCosto
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCentroDeCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = SysmanFunciones
                        .toString(registroAux.getCampos().get(EnominacontabilidadControladorEnum.CODIGO.getValue()));
        nombreCentroCosto = SysmanFunciones
                        .toString(registroAux.getCampos().get(EnominacontabilidadControladorEnum.NOMBRE.getValue()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable visEmpleado
     *
     * @return visEmpleado
     */
    public boolean getVisEmpleado()
    {
        return visEmpleado;
    }

    /**
     * Asigna la variable visEmpleado
     *
     * @param visEmpleado
     * Variable a asignar en visEmpleado
     */
    public void setVisEmpleado(boolean visEmpleado)
    {
        this.visEmpleado = visEmpleado;
    }

    public boolean isVisIntRetencion()
    {
        return visIntRetencion;
    }

    public void setVisIntRetencion(boolean visIntRetencion)
    {
        this.visIntRetencion = visIntRetencion;
    }

    public boolean isVisProceso()
    {
        return visProceso;
    }

    public void setVisProceso(boolean visProceso)
    {
        this.visProceso = visProceso;
    }

    public boolean isVisPeriodo()
    {
        return visPeriodo;
    }

    public void setVisPeriodo(boolean visPeriodo)
    {
        this.visPeriodo = visPeriodo;
    }

    public boolean isVisAno()
    {
        return visAno;
    }

    public void setVisAno(boolean visAno)
    {
        this.visAno = visAno;
    }

    public boolean isVisMes()
    {
        return visMes;
    }

    public void setVisMes(boolean visMes)
    {
        this.visMes = visMes;
    }

    public boolean isVisIntCuetaspagar()
    {
        return visIntCuetaspagar;
    }

    public void setVisIntCuetaspagar(boolean visIntCuetaspagar)
    {
        this.visIntCuetaspagar = visIntCuetaspagar;
    }

    public boolean isBlockTercero()
    {
        return blockTercero;
    }

    public void setBlockTercero(boolean blockTercero)
    {
        this.blockTercero = blockTercero;
    }

    public boolean isBlockEmpleado()
    {
        return blockEmpleado;
    }

    public void setBlockEmpleado(boolean blockEmpleado)
    {
        this.blockEmpleado = blockEmpleado;
    }

    public boolean isVisTercero()
    {
        return visTercero;
    }

    public void setVisTercero(boolean visTercero)
    {
        this.visTercero = visTercero;
    }

    public boolean isVisCentroCosto()
    {
        return visCentroCosto;
    }

    public void setVisCentroCosto(boolean visCentroCosto)
    {
        this.visCentroCosto = visCentroCosto;
    }

    /**
     * Retorna la variable porTercero
     *
     * @return porTercero
     */
    public boolean getPorTercero()
    {
        return porTercero;
    }

    /**
     * Asigna la variable porTercero
     *
     * @param porTercero
     * Variable a asignar en porTercero
     */
    public void setPorTercero(boolean porTercero)
    {
        this.porTercero = porTercero;
    }

    /**
     * Retorna la variable patronoT
     *
     * @return patronoT
     */
    public boolean getPatronoT()
    {
        return patronoT;
    }

    public boolean isParReclasifica()
    {
        return parReclasifica;
    }

    public void setParReclasifica(boolean parReclasifica)
    {
        this.parReclasifica = parReclasifica;
    }

    /**
     * Asigna la variable patronoT
     *
     * @param patronoT
     * Variable a asignar en patronoT
     */
    public void setPatronoT(boolean patronoT)
    {
        this.patronoT = patronoT;
    }

    /**
     * Retorna la variable empleadoT
     *
     * @return empleadoT
     */
    public boolean getEmpleadoT()
    {
        return empleadoT;
    }

    /**
     * Asigna la variable empleadoT
     *
     * @param empleadoT
     * Variable a asignar en empleadoT
     */
    public void setEmpleadoT(boolean empleadoT)
    {
        this.empleadoT = empleadoT;
    }

    /**
     * Retorna la variable provisionesT
     *
     * @return provisionesT
     */
    public boolean getProvisionesT()
    {
        return provisionesT;
    }

    /**
     * Asigna la variable provisionesT
     *
     * @param provisionesT
     * Variable a asignar en provisionesT
     */
    public void setProvisionesT(boolean provisionesT)
    {
        this.provisionesT = provisionesT;
    }

    /**
     * Retorna la variable porCentroCosto
     *
     * @return porCentroCosto
     */
    public boolean getPorCentroCosto()
    {
        return porCentroCosto;
    }

    /**
     * Asigna la variable porCentroCosto
     *
     * @param porCentroCosto
     * Variable a asignar en porCentroCosto
     */
    public void setPorCentroCosto(boolean porCentroCosto)
    {
        this.porCentroCosto = porCentroCosto;
    }

    /**
     * Retorna la variable companiaNomina
     *
     * @return companiaNomina
     */
    public String getCompaniaNomina()
    {
        return companiaNomina;
    }

    /**
     * Asigna la variable companiaNomina
     *
     * @param companiaNomina
     * Variable a asignar en companiaNomina
     */
    public void setCompaniaNomina(String companiaNomina)
    {
        this.companiaNomina = companiaNomina;
    }

    /**
     * Retorna la variable proceso
     *
     * @return proceso
     */
    public int getProceso()
    {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     *
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(int proceso)
    {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public int getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(int periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public int getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano)
    {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     *
     * @return mes
     */
    public int getMes()
    {
        return mes;
    }

    /**
     * Asigna la variable mes
     *
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(int mes)
    {
        this.mes = mes;
    }

    /**
     * Retorna la variable porEmpleado
     *
     * @return porEmpleado
     */
    public String getPorEmpleado()
    {
        return porEmpleado;
    }

    /**
     * Asigna la variable porEmpleado
     *
     * @param porEmpleado
     * Variable a asignar en porEmpleado
     */
    public void setPorEmpleado(String porEmpleado)
    {
        this.porEmpleado = porEmpleado;
    }

    /**
     * Retorna la variable centroCosto
     *
     * @return centroCosto
     */
    public String getCentroCosto()
    {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     *
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto)
    {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable tipo
     *
     * @return tipo
     */
    public String getTipo()
    {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     *
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable fechaInterface
     *
     * @return fechaInterface
     */
    public Date getFechaInterface()
    {
        return fechaInterface;
    }

    /**
     * Asigna la variable fechaInterface
     *
     * @param fechaInterface
     * Variable a asignar en fechaInterface
     */
    public void setFechaInterface(Date fechaInterface)
    {
        this.fechaInterface = fechaInterface;
    }

    /**
     * Retorna la variable nombrePeriodo
     *
     * @return nombrePeriodo
     */
    public String getNombrePeriodo()
    {
        return nombrePeriodo;
    }

    /**
     * Asigna la variable nombrePeriodo
     *
     * @param nombrePeriodo
     * Variable a asignar en nombrePeriodo
     */
    public void setNombrePeriodo(String nombrePeriodo)
    {
        this.nombrePeriodo = nombrePeriodo;
    }

    /**
     * Retorna la variable nombreCompania
     *
     * @return nombreCompania
     */
    public String getNombreCompania()
    {
        return nombreCompania;
    }

    /**
     * Asigna la variable nombreCompania
     *
     * @param nombreCompania
     * Variable a asignar en nombreCompania
     */
    public void setNombreCompania(String nombreCompania)
    {
        this.nombreCompania = nombreCompania;
    }

    /**
     * Retorna la variable nombreProceso
     *
     * @return nombreProceso
     */
    public String getNombreProceso()
    {
        return nombreProceso;
    }

    /**
     * Asigna la variable nombreProceso
     *
     * @param nombreProceso
     * Variable a asignar en nombreProceso
     */
    public void setNombreProceso(String nombreProceso)
    {
        this.nombreProceso = nombreProceso;
    }

    /**
     * Retorna la variable nombreMes
     *
     * @return nombreMes
     */
    public String getNombreMes()
    {
        return nombreMes;
    }

    /**
     * Asigna la variable nombreMes
     *
     * @param nombreMes
     * Variable a asignar en nombreMes
     */
    public void setNombreMes(String nombreMes)
    {
        this.nombreMes = nombreMes;
    }

    /**
     * Retorna la variable nombreEmpleado
     *
     * @return nombreEmpleado
     */
    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }

    /**
     * Asigna la variable nombreEmpleado
     *
     * @param nombreEmpleado
     * Variable a asignar en nombreEmpleado
     */
    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }

    /**
     * Retorna la variable nombreCentroCosto
     *
     * @return nombreCentroCosto
     */
    public String getNombreCentroCosto()
    {
        return nombreCentroCosto;
    }

    /**
     * Asigna la variable nombreCentroCosto
     *
     * @param nombreCentroCosto
     * Variable a asignar en nombreCentroCosto
     */
    public void setNombreCentroCosto(String nombreCentroCosto)
    {
        this.nombreCentroCosto = nombreCentroCosto;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getRetorno()
    {
        return retorno;
    }

    public void setRetorno(String retorno)
    {
        this.retorno = retorno;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     *
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     *
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCompaniaNomina
     *
     * @return listaCompaniaNomina
     */
    public RegistroDataModelImpl getListaCompaniaNomina()
    {
        return listaCompaniaNomina;
    }

    /**
     * Asigna la lista listaCompaniaNomina
     *
     * @param listaCompaniaNomina
     * Variable a asignar en listaCompaniaNomina
     */
    public void setListaCompaniaNomina(RegistroDataModelImpl listaCompaniaNomina)
    {
        this.listaCompaniaNomina = listaCompaniaNomina;
    }

    /**
     * Retorna la lista listaProceso
     *
     * @return listaProceso
     */
    public RegistroDataModelImpl getListaProceso()
    {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     *
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(RegistroDataModelImpl listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    /**
     * Retorna la lista listaPeriodo
     *
     * @return listaPeriodo
     */
    public RegistroDataModelImpl getListaPeriodo()
    {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     *
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(RegistroDataModelImpl listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaMes
     *
     * @return listaMes
     */
    public RegistroDataModelImpl getListaMes()
    {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     *
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(RegistroDataModelImpl listaMes)
    {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPorEmpleado
     *
     * @return listaPorEmpleado
     */
    public RegistroDataModelImpl getListaPorEmpleado()
    {
        return listaPorEmpleado;
    }

    /**
     * Asigna la lista listaPorEmpleado
     *
     * @param listaPorEmpleado
     * Variable a asignar en listaPorEmpleado
     */
    public void setListaPorEmpleado(RegistroDataModelImpl listaPorEmpleado)
    {
        this.listaPorEmpleado = listaPorEmpleado;
    }

    /**
     * Retorna la lista listacmbCentroDeCosto
     *
     * @return listacmbCentroDeCosto
     */
    public RegistroDataModelImpl getListacmbCentroDeCosto()
    {
        return listacmbCentroDeCosto;
    }

    /**
     * Asigna la lista listacmbCentroDeCosto
     *
     * @param listacmbCentroDeCosto
     * Variable a asignar en listacmbCentroDeCosto
     */
    public void setListacmbCentroDeCosto(RegistroDataModelImpl listacmbCentroDeCosto)
    {
        this.listacmbCentroDeCosto = listacmbCentroDeCosto;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public boolean isBloqEmpleado()
    {
        return bloqEmpleado;
    }

    public void setBloqEmpleado(boolean bloqEmpleado)
    {
        this.bloqEmpleado = bloqEmpleado;
    }

    public boolean isVisConCuenFonPub()
    {
        return visConCuenFonPub;
    }

    public void setVisConCuenFonPub(boolean visConCuenFonPub)
    {
        this.visConCuenFonPub = visConCuenFonPub;
    }

    public String getTipoComprobanteInterfaseNomina()
    {
        return tipoComprobanteInterfaseNomina;
    }

    public void setTipoComprobanteInterfaseNomina(String tipoComprobanteInterfaseNomina)
    {
        this.tipoComprobanteInterfaseNomina = tipoComprobanteInterfaseNomina;
    }

    public boolean isVisEmpleados()
    {
        return visEmpleados;
    }

    public void setVisEmpleados(boolean visEmpleados)
    {
        this.visEmpleados = visEmpleados;
    }

    public String getSeleccionarEmpleadoInterfazNomina()
    {
        return seleccionarEmpleadoInterfazNomina;
    }

    public void setSeleccionarEmpleadoInterfazNomina(String seleccionarEmpleadoInterfazNomina)
    {
        this.seleccionarEmpleadoInterfazNomina = seleccionarEmpleadoInterfazNomina;
    }
}
