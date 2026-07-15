
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.SubresponsablesproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.SubresponsablesproyectosControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 15/09/2015
 * 
 * @author ybecerra
 * @version 2, 02/10/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class SubresponsablesproyectosControlador
                extends BeanBaseContinuoAcmeImpl
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almancenar el codigo del modulo por la
     * cual se ingreso a la aplicacion
     */
    private final String moduloBancos;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    private final String sucursalCons;
    // <DECLARAR_ATRIBUTOS>
    private final String responsableCons;
    private final String nombreResponsableCons;
    private final String fechaInactivoCons;
    private final String codigoCons;
    private String codigoProyecto;
    private String dependencia;
    private String nomresponsable;
    private String sucursal;
    private String anoIni;
    private String anoFin;
    private boolean muestraRegistro;
    private String menuActual;
    private String proyectoMonitor;
    private String dependenciaMonitor;
    private String vigenciaMonitor;
    private String estadoMonitor;
    private String idDependenciaMonitor;
    private boolean edicion;
    private String accion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> ridProyecto;
    private Map<String, Object> rid;
    private final Map<String, Object> parametrosEntrada;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    private RegistroDataModelImpl listaResponsable;
    private RegistroDataModelImpl listaResponsableE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de SubresponsablesproyectosControlador
     */
    @SuppressWarnings("unchecked")
    public SubresponsablesproyectosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        moduloBancos = SessionUtil.getModulo();
        parametrosEntrada = SessionUtil.getFlash();
        sucursalCons = GeneralParameterEnum.SUCURSAL.getName();
        responsableCons = GeneralParameterEnum.RESPONSABLE.getName();
        nombreResponsableCons = SubresponsablesproyectosControladorEnum.NOMB_RESPONSABLE
                        .getValue();
        fechaInactivoCons = SubresponsablesproyectosControladorEnum.FECHAINACTIVO
                        .getValue();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        try
        {
            // 181
            numFormulario = GeneralCodigoFormaEnum.SUBRESPONSABLESPROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;
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

            if (parametrosEntrada != null)
            {
                codigoProyecto = parametrosEntrada
                                .get("codigoProyecto").toString();
                if (codigoProyecto == null)
                {
                    Direccionador direccionador = new Direccionador();

                    direccionador.setParametros(parametrosEntrada);
                    direccionador.setNumForm(Integer
                                    .toString(GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                                                    .getCodigo()));

                    SessionUtil.redireccionarForma(direccionador,
                                    SessionUtil.getModulo());

                    return;
                }
                ridProyecto = (HashMap<String, Object>) parametrosEntrada
                                .get("ridProyecto");
                setAnoIni((String) parametrosEntrada.get("anoIni"));
                setAnoFin((String) parametrosEntrada.get("anoFin"));
                setProyectoMonitor((String) parametrosEntrada
                                .get("proyectoMonitor"));
                dependenciaMonitor = (String) parametrosEntrada
                                .get("dependenciaMonitor");
                vigenciaMonitor = (String) parametrosEntrada
                                .get("vigenciaMonitor");
                estadoMonitor = (String) parametrosEntrada.get("estadoMonitor");
                idDependenciaMonitor = (String) parametrosEntrada
                                .get("idDependenciaMonitor");
                accion = (String) parametrosEntrada.get("accion");
                parametrosEntrada.put("rid", ridProyecto);
                parametrosEntrada.remove("codigoProyecto");
                parametrosEntrada.remove("ridProyecto");
            }
            SessionUtil.cleanFlash();
            // </INI_ADICIONAL>
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SubresponsablesproyectosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
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
        if (("v").equals(accion))
        {
            muestraRegistro = false;
        }
        enumBase = GenericUrlEnum.BPRESPONSABLEPROYECTO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDependencia();
        cargarListaDependenciaE();
        cargarListaResponsable();
        cargarListaResponsableE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(SubresponsablesproyectosControladorEnum.CODIGOPROYECTO
                                        .getValue(), codigoProyecto);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDependencia
     */

    public void cargarListaDependencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubresponsablesproyectosControladorUrlEnum.URL9660
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * 
     * Carga la lista listaDependencia
     */
    public void cargarListaDependenciaE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubresponsablesproyectosControladorUrlEnum.URL9660
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsable()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubresponsablesproyectosControladorUrlEnum.URL7704
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, responsableCons);

    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsableE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubresponsablesproyectosControladorUrlEnum.URL7704
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        listaResponsableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, responsableCons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton AbrirResponsables en la
     * vista
     *
     */
    public void oprimirAbrirResponsables()
    {
        Map<String, Object> param = new HashMap<>();
        param.put("ridProyecto", ridProyecto);
        param.put("anoIni", anoIni);
        param.put("anoFin", anoFin);
        param.put("proyectoMonitor", proyectoMonitor);
        param.put("dependenciaMonitor", dependenciaMonitor);
        param.put("vigenciaMonitor", vigenciaMonitor);
        param.put("estadoMonitor", estadoMonitor);
        param.put("idDependenciaMonitor", idDependenciaMonitor);
        param.put("accion", accion);
        param.put("codigoProyecto", codigoProyecto);
        
        Direccionador direccionador= new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.RESPONSABLES_CONTROLADOR
                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, moduloBancos);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Dependencia
     * 
     */
    public void cambiarDependencia()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Activo
     * 
     */
    public void cambiarActivo()
    {
        // <CODIGO_DESARROLLADO>
        if (!(boolean) registro.getCampos().get("ACTIVO"))
        {
            registro.getCampos().put(fechaInactivoCons,
                            Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN));
        }
        else
        {
            registro.getCampos().put(fechaInactivoCons,
                            null);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Dependencia en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDependenciaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (edicion)
        {
            edicion = false;
        }
        else
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(responsableCons, "");
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(nombreResponsableCons, "");
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(sucursalCons, "");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Responsable en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */

    public void cambiarResponsableC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(nombreResponsableCons, nomresponsable);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(sucursalCons, sucursal);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Activo en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarActivoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        if (!(boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get("ACTIVO"))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            fechaInactivoCons,
                            Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN));
        }
        else
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            fechaInactivoCons,
                            null);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Responsable
     * 
     */
    public void cambiarResponsable()
    {
        try
        {
            // <CODIGO_DESARROLLADO>
            registro.getCampos().put(codigoCons,
                            ejbSysmanUtil.generarSiguienteConsecutivo(
                                            GenericUrlEnum.BPRESPONSABLEPROYECTO
                                                            .getTable(),
                                            SysmanFunciones.concatenar(
                                                            "COMPANIA = ''",
                                                            compania,
                                                            "'' AND PROYECTO = ''",
                                                            codigoProyecto,
                                                            "'' AND RESPONSABLE =  ''",
                                                            registro.getCampos()
                                                                            .get(responsableCons)
                                                                            .toString(),
                                                            "''"),
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (("Inactivo").equalsIgnoreCase(
                        registroAux.getCampos().get("MOVIMIENTO").toString()))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2456"));
            return;
        }
        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get(codigoCons));
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        registro.getCampos().put(responsableCons, "");
        registro.getCampos().put(nombreResponsableCons, "");
        registro.getCampos().put(codigoCons, "");
        registro.getCampos().put(sucursalCons, "");
        cargarListaResponsable();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (("Inactivo").equalsIgnoreCase(registroAux.getCampos()
                        .get("MOVIMIENTO").toString()))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2456"));
            return;
        }
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        dependencia = auxiliar;

        cargarListaResponsableE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(responsableCons,
                        registroAux.getCampos().get(responsableCons));
        registro.getCampos().put(nombreResponsableCons,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(sucursalCons,
                        registroAux.getCampos().get(sucursalCons));

        try
        {
            registro.getCampos().put(codigoCons,
                            ejbSysmanUtil.generarSiguienteConsecutivo(
                                            GenericUrlEnum.BPRESPONSABLEPROYECTO
                                                            .getTable(),
                                            SysmanFunciones.concatenar(
                                                            "COMPANIA = ''",
                                                            compania,
                                                            "'' AND PROYECTO = ''",
                                                            codigoProyecto,
                                                            "'' AND RESPONSABLE =  ''",
                                                            registro.getCampos()
                                                                            .get(responsableCons)
                                                                            .toString(),
                                                            "''"),
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(responsableCons), "")
                        .toString();
        nomresponsable = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(sucursalCons), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.PROYECTO.getName(),
                        codigoProyecto);
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
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
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
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.PROYECTO.getName());
        registro.getCampos()
                        .remove(SubresponsablesproyectosControladorEnum.NOMBRETIPO
                                        .getValue());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        dependencia = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()),
                                        "")
                        .toString();
        cargarListaResponsableE();
        edicion = true;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar()
    {
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametrosEntrada);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anoIni
     * 
     * @return anoIni
     */
    public String getAnoIni()
    {
        return anoIni;
    }

    /**
     * Asigna la variable anoIni
     * 
     * @param anoIni
     * Variable a asignar en anoIni
     */
    public void setAnoIni(String anoIni)
    {
        this.anoIni = anoIni;
    }

    /**
     * Retorna la variable anoFin
     * 
     * @return anoFin
     */
    public String getAnoFin()
    {
        return anoFin;
    }

    /**
     * Asigna la variable anoFin
     * 
     * @param anoFin
     * Variable a asignar en anoFin
     */
    public void setAnoFin(String anoFin)
    {
        this.anoFin = anoFin;
    }

    /**
     * Retorna la variable proyectoMonitor
     * 
     * @return proyectoMonitor
     */
    public String getProyectoMonitor()
    {
        return proyectoMonitor;
    }

    /**
     * Asigna la variable proyectoMonitor
     * 
     * @param proyectoMonitor
     * Variable a asignar en proyectoMonitor
     */
    public void setProyectoMonitor(String proyectoMonitor)
    {
        this.proyectoMonitor = proyectoMonitor;
    }

    /**
     * Retorna la variable nomresponsable
     * 
     * @return nomresponsable
     */
    public String getNomresponsable()
    {
        return nomresponsable;
    }

    /**
     * Asigna la variable nomresponsable
     * 
     * @param nomresponsable
     * Variable a asignar en nomresponsable
     */
    public void setNomresponsable(String nomresponsable)
    {
        this.nomresponsable = nomresponsable;
    }

    /**
     * Retorna la variable sucursal
     * 
     * @return sucursal
     */
    public String getSucursal()
    {
        return sucursal;
    }

    /**
     * Asigna la variable sucursal
     * 
     * @param sucursal
     * Variable a asignar en sucursal
     */
    public void setSucursal(String sucursal)
    {
        this.sucursal = sucursal;
    }

    /**
     * Retorna la variable dependenciaMonitor
     * 
     * @return dependenciaMonitor
     */
    public String getDependenciaMonitor()
    {
        return dependenciaMonitor;
    }

    /**
     * Asigna la variable dependenciaMonitor
     * 
     * @param dependenciaMonitor
     * Variable a asignar en dependenciaMonitor
     */
    public void setDependenciaMonitor(String dependenciaMonitor)
    {
        this.dependenciaMonitor = dependenciaMonitor;
    }

    /**
     * Retorna la variable vigenciaMonitor
     * 
     * @return vigenciaMonitor
     */
    public String getVigenciaMonitor()
    {
        return vigenciaMonitor;
    }

    /**
     * Asigna la variable vigenciaMonitor
     * 
     * @param vigenciaMonitor
     * Variable a asignar en vigenciaMonitor
     */
    public void setVigenciaMonitor(String vigenciaMonitor)
    {
        this.vigenciaMonitor = vigenciaMonitor;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    /**
     * Retorna la variable estadoMonitor
     * 
     * @return estadoMonitor
     */
    public String getEstadoMonitor()
    {
        return estadoMonitor;
    }

    /**
     * Asigna la variable estadoMonitor
     * 
     * @param estadoMonitor
     * Variable a asignar en estadoMonitor
     */
    public void setEstadoMonitor(String estadoMonitor)
    {
        this.estadoMonitor = estadoMonitor;
    }

    /**
     * Retorna la variable idDependenciaMonitor
     * 
     * @return idDependenciaMonitor
     */
    public String getIdDependenciaMonitor()
    {
        return idDependenciaMonitor;
    }

    /**
     * Asigna la variable idDependenciaMonitor
     * 
     * @param idDependenciaMonitor
     * Variable a asignar en idDependenciaMonitor
     */
    public void setIdDependenciaMonitor(String idDependenciaMonitor)
    {
        this.idDependenciaMonitor = idDependenciaMonitor;
    }

    /**
     * Retorna la variable codigoProyecto
     * 
     * @return codigoProyecto
     */
    public String getCodigoProyecto()
    {
        return codigoProyecto;
    }

    /**
     * Asigna la variable codigoProyecto
     * 
     * @param codigoProyecto
     * Variable a asignar en codigoProyecto
     */
    public void setCodigoProyecto(String codigoProyecto)
    {
        this.codigoProyecto = codigoProyecto;
    }

    /**
     * Retorna la variable muestraRegistro
     * 
     * @return muestraRegistro
     */
    public boolean isMuestraRegistro()
    {
        return muestraRegistro;
    }

    /**
     * Asigna la variable muestraRegistro
     * 
     * @param muestraRegistro
     * Variable a asignar en muestraRegistro
     */
    public void setMuestraRegistro(boolean muestraRegistro)
    {
        this.muestraRegistro = muestraRegistro;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    /**
     * @return
     */
    public Map<String, Object> getParametrosEntrada()
    {
        return parametrosEntrada;
    }

    /**
     * @return
     */
    public Map<String, Object> getRidProyecto()
    {
        return ridProyecto;
    }

    /**
     * @param ridProyecto
     */
    public void setRidProyecto(Map<String, Object> ridProyecto)
    {
        this.ridProyecto = ridProyecto;
    }

    /**
     * @return
     */
    public Map<String, Object> getRid()
    {
        return rid;
    }

    /**
     * @param rid
     */
    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependenciaE()
    {
        return listaDependenciaE;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE)
    {
        this.listaDependenciaE = listaDependenciaE;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsable()
    {
        return listaResponsable;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsable(RegistroDataModelImpl listaResponsable)
    {
        this.listaResponsable = listaResponsable;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsableE()
    {
        return listaResponsableE;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsableE(RegistroDataModelImpl listaResponsableE)
    {
        this.listaResponsableE = listaResponsableE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */

    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}