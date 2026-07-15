/*-
 * FrmCronogramasStsControlador.java
 *
 * 1.0
 * 
 * 28/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmCronogramasStsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario para registrar los datos del formulario CRONOGRAMA
 *
 * @version 1.0, 28/12/2017
 * @author asana
 */
@ManagedBean
@ViewScoped

public class FrmCronogramasStsControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Estructura que almacena los campos y valores que son llave para
     * el registro que se esta trabajando en el formulario
     * "FrmtransaccionessstsControlador (1563)"
     */
    private Map<String, Object> ridSstTransacciones;
    /**
     * Estructura que almacena los parametros que han sido enviados al
     * formulario mediante SessionUtil.getFlash()
     */
    private Map<String, Object> parametrosEntrada;
    /**
     * Variable encargada de almacenar lo obtenido por el flash y que
     * hace referencia al Tipo de Transaccion que se esta trabajando
     */
    private String tipoTransaccion;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que se esta
     * trabajando posee empleados a cargo
     */
    private boolean indicadorResponsable;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que se esta
     * trabajando es Comite
     */
    private boolean indicadorComite;
    /**
     * Atirbuto que indica si el "Tipo de Transaccion", requiere
     * agente
     */
    private boolean indicadorAgente;
    /**
     * Atributo que almacena la Clase de Transaccion a la que
     * pertenece el "Tipo de Transaccion" que ha sido seleccionado
     */
    private String claseTransaccion;
    /**
     * Atributo que almacena el nombre de la transaccion que se esta
     * trabajando, valor que ha sido enviado por parametro
     */
    private String nombreTransaccion;
    /**
     * Atributo que almacena el consecutivo de la transaccion, valor
     * que ha sido enviado por parametro
     */
    private String consecutivoTransaccion;
    /**
     * Atributo que almacena el cosdigo del menu actual
     */
    private String menu;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable que registra la lista de las actividades a mostrar en
     * el campo
     */
    private RegistroDataModelImpl listaactividad;
    /**
     * Variable que registra la lista de los empleados incluyendo la
     * sucursal a mostrar en el campo
     */
    private RegistroDataModelImpl listasucursal;
    /**
     * Variable que registra la lista de los los responsables por
     * compañía
     */
    private RegistroDataModelImpl listaresponsable;

    /**
     * Variable que guarda el valor de la palabra sucursal
     */
    private String nSucursal;
    /**
     * Variable que guarda el valor de la sucursal del responsable que
     * se selecciona
     */
    private String sucursal;

    private String nDocumento;

    private String nResponsable;

    private String nActividad;

    private String codActividad;

    private String nConsecutivo;

    private String nCodigoActividad;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmCronogramasStsControlador
     */
    public FrmCronogramasStsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        menu = SessionUtil.getMenuActual();
        nSucursal = "SUCURSAL";
        nDocumento = "NUMERO_DCTO";
        nResponsable = "NOMBRERESPONSABLE";
        nActividad = "NOMBREACTIVIDAD";
        nConsecutivo = "CONSECUTIVO";
        nCodigoActividad = "CODIGO_ACTIVIDAD";

        try
        {

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                consecutivoTransaccion = (String) parametrosEntrada
                                .get("consecutivoTransaccion");
                claseTransaccion = (String) parametrosEntrada
                                .get("claseTransaccion");
                tipoTransaccion = (String) parametrosEntrada
                                .get("tipoTransaccion");
                nombreTransaccion = (String) parametrosEntrada
                                .get("nombreTransaccion");
                indicadorResponsable = (boolean) parametrosEntrada
                                .get("responsable");
                indicadorComite = (boolean) parametrosEntrada.get("comite");
                indicadorAgente = (boolean) parametrosEntrada.get("agente");
                ridSstTransacciones = (Map<String, Object>) parametrosEntrada
                                .get("rid");
            }

            numFormulario = GeneralCodigoFormaEnum.FRMCRONOGRAMASST_CONTROLADOR
                            .getCodigo();
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaActividades();
        cargarListaCmbResponsable();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
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
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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
        enumBase = GenericUrlEnum.SST_CRONOGRAMA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * DOCUMENTACION ADICIONAL
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        if ("21080201".equals(menu))
        {
            parametrosListado.put("TIPO_TRANSACCION", tipoTransaccion);
            parametrosListado.put("CONSECUTIVOT", consecutivoTransaccion);

        }
        if ("21080202".equals(menu))
        {
            parametrosListado.put("TIPO_TRANSACCION", tipoTransaccion);
            // parametrosListado.put("CONSECUTIVOT",
            // consecutivoTransaccion);

        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaactividad
     *
     * DOCUMENTACION ADICIONAL
     */
    public void cargarListaActividades()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCronogramasStsControladorUrlEnum.URL4803
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOTRANSACCION", tipoTransaccion);

        listaactividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");

    }

    /**
     * 
     * Carga la lista listaCmbResponsable
     */
    public void cargarListaCmbResponsable()
    {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCronogramasStsControladorUrlEnum.URL5529
                                                        .getValue());
        listaresponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros, true,
                        nDocumento);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTxt_codAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaactividad(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (registro.getCampos().get(nCodigoActividad) != null)
        {
            codActividad = registro.getCampos().get(nCodigoActividad)
                            .toString();
        }

        registro.getCampos().put(nCodigoActividad,
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put(nActividad,
                        registroAux.getCampos().get(nActividad));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbSucursal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilasucursal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nSucursal,
                        registroAux.getCampos().get(nSucursal));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaresponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nResponsable,
                        registroAux.getCampos().get(nResponsable));
        registro.getCampos().put("RESPONSABLE",
                        registroAux.getCampos().get(nDocumento));
        registro.getCampos().put(nSucursal,
                        registroAux.getCampos().get(nSucursal));

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnDetalle en la vista
     *
     *
     */
    public void oprimirBtnDetalle()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        if (css == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2634"));
        }
        else
        {
            Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put("numero", registro.getCampos().get(nConsecutivo));
            parametros.put("codActividad", registro.getCampos()
                            .get(nCodigoActividad).toString());
            parametros.put("actividad", registro.getCampos().get(nActividad));
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.FRMSUBCRONOGRAMASST_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (accion.equals(ACCION_MODIFICAR))
        {
            sucursal = registro.getCampos().get(nSucursal).toString();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        long consecutivo;
        try
        {
            consecutivo = ejbSysmanUtilRemote.generarConsecutivoConValorInicial(
                            GenericUrlEnum.SST_CRONOGRAMA.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania,
                                            "''"),
                            "CONSECUTIVO", "1");

            registro.getCampos().put(nConsecutivo, consecutivo);
            registro.getCampos().put("TIPO_TRANSACCION", tipoTransaccion);
            registro.getCampos().put("CONSECUTIVO_TRANSACCION",
                            consecutivoTransaccion);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (validarCamposHora())
        {
            if (ACCION_MODIFICAR.equals(accion))
            {
                registro.getCampos().remove(
                                GeneralParameterEnum.COMPANIA.getName());
                registro.getCampos().remove(
                                GeneralParameterEnum.CONSECUTIVO.getName());
            }
            registro.getCampos().remove(nDocumento);
            registro.getCampos().remove("ID_DE_EMPLEADO");
            registro.getCampos().remove(nActividad);
            registro.getCampos().remove(nResponsable);

            return true;
        }
        else
        {
            return false;
        }

        // </CODIGO_DESARROLLADO>

    }

    private boolean validarCamposHora()
    {
        Date fechaInicio = (Date) registro.getCampos().get("FECHA_INICIAL");
        Date fechaFinal = (Date) registro.getCampos().get("FECHA_FINAL");
        Date horaInicial = (Date) registro.getCampos().get("HORA_INICIAL");
        Date horaFinal = (Date) registro.getCampos().get("HORA_FINAL");

        if ((registro.getCampos().get("HORA_INICIAL") == null)
            || (registro.getCampos().get("HORA_FINAL") == null))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3896"));
            return false;
        }
        if ((fechaInicio.compareTo(fechaFinal) == 0)
            && (horaFinal.compareTo(horaInicial) < 0))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3944"));
            return false;
        }
        else
        {
            return true;
        }

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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
     * Tiene en cuenta el menu desde el cual se redirecciona a este
     * formulario
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        if ("21080201".equals(menu))
        {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("tipoTransaccion", tipoTransaccion);
            parametros.put("nombreTransaccion", nombreTransaccion);
            parametros.put("responsable", indicadorResponsable);
            parametros.put("comite", indicadorComite);
            parametros.put("claseTransaccion", claseTransaccion);
            parametros.put("rid", ridSstTransacciones);
            parametros.put("agente", indicadorAgente);

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(String.valueOf(
                            GeneralCodigoFormaEnum.FRMTRANSACCIONESSSTS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else
        {
            SessionUtil.cleanFlash();
            SessionUtil.redireccionarMenu();
        }
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaactividad
     * 
     * @return listaactividad
     */
    public RegistroDataModelImpl getListaactividad()
    {
        return listaactividad;
    }

    /**
     * Asigna la lista listaactividad
     * 
     * @param listaactividad
     * Variable a asignar en listaactividad
     */

    public void setListaactividad(RegistroDataModelImpl listaactividad)
    {
        this.listaactividad = listaactividad;
    }

    /**
     * Retorna la lista listasucursal
     * 
     * @return listasucursal
     */
    public RegistroDataModelImpl getListasucursal()
    {
        return listasucursal;
    }

    /**
     * Asigna la lista listasucursal
     * 
     * @param listasucursal
     * Variable a asignar en listasucursal
     */

    public void setListasucursal(RegistroDataModelImpl listasucursal)
    {
        this.listasucursal = listasucursal;
    }

    public RegistroDataModelImpl getListaresponsable()
    {
        return listaresponsable;
    }

    public void setListaresponsable(RegistroDataModelImpl listaresponsable)
    {
        this.listaresponsable = listaresponsable;
    }

    public String getSucursal()
    {
        return sucursal;
    }

    public void setSucursal(String sucursal)
    {
        this.sucursal = sucursal;
    }

    public String getCodActividad()
    {
        return codActividad;
    }

    public void setCodActividad(String codActividad)
    {
        this.codActividad = codActividad;
    }

    /**
     * Retorna la lista listaresponsable
     * 
     * @return listaresponsable
     */

    /**
     * Asigna la lista listaresponsable
     * 
     * @param listaresponsable
     * Variable a asignar en listaresponsable
     */

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
