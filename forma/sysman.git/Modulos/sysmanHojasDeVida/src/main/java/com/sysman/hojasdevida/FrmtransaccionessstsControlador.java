/*-
 * FrmtransaccionessstsControlador.java
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
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmtipotransaccionsstsControladorEnum;
import com.sysman.hojasdevida.enums.FrmtransaccionessstsControladorEnum;
import com.sysman.hojasdevida.enums.FrmtransaccionessstsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Arrays;
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
 * Clase encargada de gestionar las operaciones basicas (CRUD) de las
 * transacciones.
 * 
 * @version 1.0, 28/12/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped

public class FrmtransaccionessstsControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable encargada de almacenar lo seleccionado enel combo
     * modelo plantilla
     */
    private String modeloPlantilla;
    /**
     * Variable encargada de almacenar lo obtenido por el flash y que
     * hace referencia al formulario tipotransaccion
     */
    private String tipoTransaccion;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Almacena el valor del campo IND_REUNION al seleccionar la Clase
     * de Evento
     */
    private boolean indicadorReunion;
    /**
     * Variable encargada de gestionar el titulo de la etiqueta que
     * acompàña el campo descripcion
     */
    private String tituloDescripcion;
    /**
     * Variable encargada de gestionar el titulo de la etiqueta que
     * acompaña el campo texto
     */
    private String tituloTexto;

    /**
     * Variable encargada de gestionar el titulo de la etiqueta que
     * acompaña el campo compromisos
     */
    private String tituloCompromisos;

    /**
     * Variable encargada de visualizar o no el campò compromismos
     */
    private boolean visibleCompromisos;

    /**
     * Atributo que administra la visibilidad de los campos lugar y
     * tipo capcitacion
     */
    private boolean visibleCobertura;

    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado posee empleados a cargo
     */
    private boolean indicadorResponsable;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado es Comite
     */
    private boolean indicadorComite;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado es Agente
     */
    private boolean indicadorAgente;
    /**
     * Atributo que almacena la Clase de Transaccion a la que
     * pertenece el "Tipo de Transaccion" que ha sido seleccionado
     */
    private String claseTransaccion;
    /**
     * Atributo que almacena la fecha del formato seleccionado para
     * generar la plantilla
     */
    private Date fechaFormato;
    /**
     * Atributo que valida si los combos de agentes se visualizan o no
     */
    private boolean visibleAgente;
    /**
     * Ejb que hace referencia a la funcion que genera el consecutivo
     * 
     */
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;
    /**
     * Atibuto que define la visibilidad del boton "Detalle"
     */
    private boolean visibleBtnDetalle;
    /**
     * Atributo que almacena el nombre de la Clase de Transaccion
     * Seleccionada
     */
    private String nombreClaseTransaccion;
    /**
     * Atributo que almacena el nombre con el que se genera la
     * plantilla
     */
    private String nombreDocumento;
    /**
     * Atributo que almacena el Titulo de la grilla para el formulario
     */
    private String tituloFrmPrincipal;
    /**
     * Indicador para visualizar los campos de Hora Inicial y Final en
     * el formulario
     */
    private boolean visibleHoras;
    /**
     * Almacena el texto que se visualiza en la etiqueta
     * <code>LB40428</code>
     */
    private String tituloTxtFecha;

    /**
     * Atributo que visualiza el valor del texto de la etiqueta del
     * campo fechalimitecomp
     */
    private String fechaLimite;

    /**
     * Atributo que valida si la fecha limite se hace visible o no
     */
    private boolean mostrarFechaLimite;

    /**
     * Atributo que valida la visibilidad del combo de cargo comite
     */
    private boolean visibleCargoComite;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almcanera los datos de respuesta de las
     * clase de eventos
     */
    private RegistroDataModelImpl listaClaseEvento;
    /**
     * lista encargada de almancenar temporalmante los datos de la
     * tabla personal y que es msotrada en el combo Nit.
     */
    private RegistroDataModelImpl listaCedula;
    /**
     * LIsta encargdada de almancenar todas las plantillas de que
     * pueden ser usadas.
     * 
     */
    private RegistroDataModelImpl listaModeloPlantilla;

    /**
     * Lista encargada de almacenar todos los agentes
     */
    private RegistroDataModelImpl listaAgente;
    /**
     * variable encargada de almacenar la fecha de la plantilla que ha
     * sido seleccionada
     * 
     */

    private final String tipotransaccionCons;
    /**
     * Constante encargada de almacenar el String NOMBRE_CLASE_EVENTO
     * 
     */
    private final String nombreClaseEventoCons;
    /**
     * Constante encargada de almacenar el String IND_REUNION
     * 
     */
    private final String indReunionCons;
    /**
     * Constante encargada de almacenar el String CARGO_ACTUAL
     * 
     */
    private final String cargoActualCons;

    /**
     * Constante encargada de almacenar el String NOMBRE_TRANSACCION
     * 
     */
    private final String nombreTransaccionCons;
    /**
     * Constante encargada de almacenar el String NOMBRE_COMPLETO
     * 
     */
    private final String nombreCompletoCons;
    /**
     * Constante encargada de almacenar el String ID_EMPLEADO
     */
    private final String idEmpleadoCons;
    /**
     * Constante encargada de almacenar el String TB_TB2634
     */
    private final String tb2634Cons;
    /**
     * Constante encargada de almacenar el String
     * RUTAFORMATO_PLANTILLA
     */
    private final String rutaFormatoPlantillaCons;
    /**
     * Atributo que almacena el nombre de la transaccion que se esta
     * trabajando, valor que ha sido enviado por parametro
     */
    private String nombreTransaccion;
    /**
     * Atributo que almacena el texto "tipoTransaccion"
     */
    private String tipoTransaccionPar;
    /**
     * Atributo que almacena el texto "nombreTransaccion"
     */
    private String nombreTransaccionPar;
    /**
     * Atributo que almacena el texto "responsable"
     */
    private String responsablePar;
    /**
     * Atributo que almacena el texto "comite"
     */
    private String comitePar;
    /**
     * Atributo que almacena el texto "claseTransaccion"
     */
    private String claseTransaccionPar;

    /**
     * Atributo que valida si el campo y la etiqueta cobertura se
     * visualiza o no
     */
    private boolean mostrarCobertura;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVida;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmtransaccionessstsControlador
     */
    @SuppressWarnings("unchecked")
    public FrmtransaccionessstsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        registro = new Registro();
        tipotransaccionCons = "TIPO_TRANSACCION";
        nombreClaseEventoCons = "NOMBRE_CLASE_EVENTO";
        indReunionCons = "IND_REUNION";
        cargoActualCons = "CARGO_ACTUAL";
        nombreTransaccionCons = "NOMBRE_TRANSACCION";
        nombreCompletoCons = "NOMBRE_COMPLETO";
        idEmpleadoCons = "ID_EMPLEADO";
        tb2634Cons = "TB_TB2634";
        tipoTransaccionPar = "tipoTransaccion";
        nombreTransaccionPar = "nombreTransaccion";
        responsablePar = "responsable";
        comitePar = "comite";
        claseTransaccionPar = "claseTransaccion";
        rutaFormatoPlantillaCons = "RUTAFORMATO_PLANTILLA";

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null)
        {
            tipoTransaccion = (String) parametros.get(tipoTransaccionPar);
            nombreTransaccion = (String) parametros.get(nombreTransaccionPar);
            indicadorResponsable = (boolean) parametros.get(responsablePar);
            indicadorComite = (boolean) parametros.get(comitePar);
            indicadorAgente = (boolean) parametros.get("agente");
            claseTransaccion = (String) parametros.get(claseTransaccionPar);

            rid = (Map<String, Object>) parametros.get("rid");
        }

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMTRANSACCIONESSSTS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            visibleBtnDetalle = Arrays.asList("A", "I", "L", "E")
                            .contains(claseTransaccion);

            visibleCobertura = Arrays.asList("R", "C")
                            .contains(claseTransaccion);

            visibleCargoComite = "O".equals(claseTransaccion);

            tituloFrmPrincipal = nombreTransaccion.toUpperCase();
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
        cargarListalistaClaseEvento();
        cargarListaCedula();
        cargarListaModeloPlantilla();
        cargarListaAgente();
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

        enumBase = GenericUrlEnum.SST_TRANSACCIONES;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(tipotransaccionCons, tipoTransaccion);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbTercero Metodo ecargargadi de hacer el
     * llamado a la base de datos y almacenarlo en la lista
     * ListaClaseEcento
     *
     */
    public void cargarListalistaClaseEvento()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipotransaccionCons, tipoTransaccion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtransaccionessstsControladorUrlEnum.URL9204
                                                        .getValue());
        listaClaseEvento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCmbTercero Metodo ecargargadi de hacer el
     * llamado a la base de datos y almacenarlo en la lista
     * listaCedula
     *
     */
    public void cargarListaCedula()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtransaccionessstsControladorUrlEnum.URL9964
                                                        .getValue());
        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_DCTO");

    }

    /**
     * 
     * Carga la lista listaModeloPlantilla
     *
     * Metodo ecargargadi de hacer el llamado a la base de datos y
     * almacenarlo en la listaModeloPlantilla
     * 
     */
    public void cargarListaModeloPlantilla()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPO", "39");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtransaccionessstsControladorUrlEnum.URL10934
                                                        .getValue());
        listaModeloPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAgente
     *
     */
    public void cargarListaAgente()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtransaccionessstsControladorUrlEnum.URL553
                                                        .getValue());
        listaAgente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TxtHora
     * 
     * 
     * 
     */
    public void cambiarTxtHora()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "HORA"))
        {
            registro.getCampos().put("HORA", new Date());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseEvento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASE_EVENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreClaseTransaccion = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().put(nombreClaseEventoCons, nombreClaseTransaccion);

        registro.getCampos().put(rutaFormatoPlantillaCons,
                        registroAux.getCampos().get("CODIGO_PLANTILLA"));

        indicadorReunion = (boolean) SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(indReunionCons), false);

        // modificarControlesIndreunion(indicadorReunion);

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(idEmpleadoCons,
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCedula
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedula(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CEDULA",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));
        registro.getCampos().put(nombreCompletoCons,
                        registroAux.getCampos().get(nombreCompletoCons));
        registro.getCampos().put(cargoActualCons,
                        registroAux.getCampos().get(cargoActualCons));

        registro.getCampos().put(idEmpleadoCons,
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbSucursal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbSucursal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantilla(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        modeloPlantilla = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        fechaFormato = (Date) registroAux.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAgente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAgente(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AGENTE", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREAGENTE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("CLASE_AGENTE", registroAux.getCampos().get(GeneralParameterEnum.CLASE.getName()));
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
    public void oprimirBtnParticipantes()
    {
        // <CODIGO_DESARROLLADO>
        if (css == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString(tb2634Cons));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("consecutivo", retornarString(registro,
                        GeneralParameterEnum.CONSECUTIVO.getName()));
        parametros.put(nombreTransaccionPar, nombreTransaccion);
        parametros.put(tipoTransaccionPar, tipoTransaccion);
        parametros.put(responsablePar, indicadorResponsable);
        parametros.put(comitePar, indicadorComite);
        parametros.put(claseTransaccionPar, claseTransaccion);
        parametros.put("agente", indicadorAgente);
        parametros.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FRMSUBTRANSACCIONESSSTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPlantilla en la vista
     *
     *
     */
    public void oprimirBtnPlantilla()
    {
        // <CODIGO_DESARROLLADO
        if (css == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString(tb2634Cons));
            return;
        }
        String cargoActual = retornarString(registro, cargoActualCons);

        modeloPlantilla(cargoActual);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnGenerarCronograma en la
     * vista
     *
     * Realiza la redireccion y envio de parametros al formulario
     * FrmCronogramasStsControlador(1556)
     *
     */
    public void oprimirBtnGenerarCronograma()
    {
        // <CODIGO_DESARROLLADO>
        if (css == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString(tb2634Cons));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("consecutivoTransaccion",
                        retornarString(registro, "CONSECUTIVO"));
        parametros.put(nombreTransaccionPar, nombreTransaccion);
        parametros.put(tipoTransaccionPar, tipoTransaccion);
        parametros.put(responsablePar, indicadorResponsable);
        parametros.put(comitePar, indicadorComite);
        parametros.put(claseTransaccionPar, claseTransaccion);
        parametros.put("agente", indicadorAgente);
        parametros.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FRMCRONOGRAMASST_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnDetalle en la vista
     *
     * Realiza la redireccion y envio de parametros al formulario
     * NatAccidentesTrabajosControlador(1566)
     *
     */
    public void oprimirBtnDetalle()
    {
        // <CODIGO_DESARROLLADO>
        if (css == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString(tb2634Cons));
            return;
        }
        // Obtener nombre clase tx

        nombreClaseTransaccion = registro.getCampos()
                        .get("NOMBRE_CLASE_EVENTO").toString();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("titulo", nombreTransaccion.toUpperCase());
        parametros.put(tipoTransaccionPar, tipoTransaccion);
        parametros.put(claseTransaccionPar, claseTransaccion);
        parametros.put("codEmpleado", retornarString(registro, idEmpleadoCons));
        parametros.put("nombreEmpleado",
                        retornarString(registro, "NOMBRE_COMPLETO"));
        parametros.put("nroDocEmpleado", retornarString(registro, "CEDULA"));
        parametros.put("sucursalEmpleado",
                        retornarString(registro, "SUCURSAL"));
        parametros.put("agente", indicadorAgente);

        parametros.put("nombreClaseTransaccion", nombreClaseTransaccion);

        // para cargar transacciones
        parametros.put(nombreTransaccionPar, nombreTransaccion);
        parametros.put(responsablePar, indicadorResponsable);
        parametros.put(comitePar, indicadorComite);
        parametros.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.NAT_ACCIDENTES_TRABAJOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton BtnDetalle en la vista
     *
     * Realiza la redireccion y envio de parametros al formulario
     * DetalleDocumentosAnexo(1700)
     *
     */
    public void oprimirAnexos()
    {

        String[] campos = { "tipoTransaccion", "nombreTransaccion",
                            "consecutivoTransaccion" };
        Object[] valores = { tipoTransaccion, nombreTransaccion,
                             retornarString(registro,
                                             GeneralParameterEnum.CONSECUTIVO
                                                             .getName()) };
        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.DETALLE_DOCUMENTO_ANEXO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Actividades en la vista
     *
     *
     */
    public void oprimirActividades()
    {
        // <CODIGO_DESARROLLADO>

        Long consecutivo = Long.parseLong(registro.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString());
        try
        {
            ejbHojasDeVida.insertarActividades(compania,
                            Integer.parseInt(tipoTransaccion), consecutivo,
                            SessionUtil.getUser().getCodigo());
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("consecutivo", retornarString(registro,
                            GeneralParameterEnum.CONSECUTIVO.getName()));
            parametros.put(nombreTransaccionPar, nombreTransaccion);
            parametros.put(tipoTransaccionPar, tipoTransaccion);
            parametros.put(responsablePar, indicadorResponsable);
            parametros.put(comitePar, indicadorComite);
            parametros.put(claseTransaccionPar, claseTransaccion);
            parametros.put("agente", indicadorAgente);
            parametros.put("rid", css);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.TRANSACCION_ACTIVIDAD
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    private void modeloPlantilla(String cargoActual)
    {

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        rutaFormatoPlantillaCons))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3946"));
        }
        else
        {
            obtenerParamsPlantilla();

            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = retornarString(registro, rutaFormatoPlantillaCons);
            valores[1] = SysmanFunciones.formatearFecha(fechaFormato);
            valores[2] = nombreDocumento;

            HashMap<String, Object> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s",
                            SysmanFunciones.concatenar("'", compania, "'"));
            variablesConsultaW.put("s$tipoTransaccion$s", SysmanFunciones
                            .concatenar("'", tipoTransaccion, "'"));
            variablesConsultaW.put("s$consecutivo$s", retornarString(
                            registro, GeneralParameterEnum.CONSECUTIVO
                                            .getName()));
            variablesConsultaW.put("s$cargoActual$s", cargoActual);

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos,
                            valores);

        }

    }

    /**
     * Obtiene los valores de Fecha y Nombre del Documento, los cuales
     * son parametros a enviar para la generacion de la plantilla word
     */
    private void obtenerParamsPlantilla()
    {
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        retornarString(registro, rutaFormatoPlantillaCons));
        param.put(FrmtipotransaccionsstsControladorEnum.TIPO.getValue(), "54");

        param.put(FrmtipotransaccionsstsControladorEnum.FECHAGENERACION
                        .getValue(),
                        new Date());

        try
        {
            rs = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtransaccionessstsControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
            fechaFormato = (Date) rs.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName());

            nombreDocumento = idioma.getString("TB_TB3902").replace(
                            "s$nombreTipoTx$s",
                            retornarString(registro, "NOMBRE_TRANSACCION"));
            nombreDocumento = nombreDocumento.replace(
                            "s$IdEmpleado$s",
                            retornarString(registro, idEmpleadoCons));
            nombreDocumento = nombreDocumento.replace(
                            "s$consecutivo$s",
                            retornarString(registro, "CONSECUTIVO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Realiza el llamado a la funcion
     * PCK_SYSMAN_UTL.FC_GEN_CONSECUTIVO, para obetener el valor a
     * asignar en el campo CONSECUTIVO al realizar una insercion
     */
    private void generarConsecutivo()
    {
        String criterio = SysmanFunciones.concatenar("COMPANIA =''", compania,
                        "'' AND TIPO_TRANSACCION = ", tipoTransaccion);

        try
        {
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.SST_TRANSACCIONES.getTable(),
                            criterio,
                            GeneralParameterEnum.CONSECUTIVO.getName(), "1");
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Visibilidad de camos dependiendo el indicador de reunion en la
     * Clase Evento seleccionada (Version Inicial)
     * 
     * @param indicador
     * Indicador de reunion
     */
    private void modificarControlesIndreunion(boolean indicador)
    {
        if (indicador)
        {
            tituloDescripcion = idioma.getString("TB_TB3898");
            visibleCompromisos = true;
        }
        else
        {
            tituloDescripcion = idioma.getString("TT_LB5027");
            visibleCompromisos = false;
        }

    }

    /**
     * Ajustes de visibilidad de objetos y textos en el formulario,
     * ajustados a la Clase Transaccion: Accidentes (A)
     */
    private void modificarControlesAccidente()
    {
        if ("A".equals(claseTransaccion))
        {
            tituloTxtFecha = idioma.getString("TG_FECHA5"); // Fecha:
            tituloDescripcion = idioma.getString("TT_LB5027");// Descripcion:
            visibleCompromisos = true;
            visibleHoras = false;
        }
        else
        {
            tituloTxtFecha = idioma.getString("TG_FECHA_INICIO3");// "Fecha
                                                                  // Inicio:"
            tituloDescripcion = idioma.getString("TB_TB3898");// Agenda
                                                              // de
                                                              // Trabajo:
            visibleCompromisos = false;
            visibleHoras = true;
        }
    }

    private void definirVisibilidadCampos()
    {

        // Capacitacion Formal
        if ("N".equals(claseTransaccion))
        {
            tituloDescripcion = "Tema:";
            tituloTxtFecha = "Fecha Inicio:";
            visibleHoras = true;
        }
        else
        {
            tituloDescripcion = idioma.getString("TT_LB5027");
            tituloTxtFecha = "Fecha:";
            visibleHoras = false;
        }
    }

    /**
     * Realiza la validacion de campos obligatorios, se realiza de
     * esta forma porque la obligatoriedad de estos campos dependen de
     * la selección en otros campos del formulario (Clase Evento)
     */
    private boolean validarCampos()
    {
        boolean respuesta = true;
        if (indicadorReunion)
        {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "DESCRIPCION"))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3938"));
                respuesta = false;
            }
            else if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "TEXTO"))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3939"));
                respuesta = false;
            }
            else if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "COMPROMISOS"))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3940"));
                respuesta = false;
            }
        }
        else if (indicadorComite)
        {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "FECHAVINCULACION"))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3941"));
                respuesta = false;
            }
            else if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "CLASEINTEGRANTE"))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3942"));
                respuesta = false;
            }
            else if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "CARGOCOMITE"))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB3943"));
                respuesta = false;
            }
        }
        return respuesta;
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
        if ((rid != null) && !rid.isEmpty())
        {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), tipoTransaccion);

        try
        {
            Registro rsIndicadorEmpleado = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtransaccionessstsControladorUrlEnum.URL1017
                                                                            .getValue())
                                            .getUrl(), param));

            if ((boolean) rsIndicadorEmpleado.getCampos()
                            .get("IND_REPONSABLE"))
            {
                mostrarCobertura = true;
            }
            else
            {
                mostrarCobertura = false;
            }

            if (Arrays.asList("A", "I", "L", "E").contains(claseTransaccion))
            {
                tituloTexto = idioma.getString("TB_TB4070");
                tituloCompromisos = idioma.getString("TB_TB4072");
                fechaLimite = idioma.getString("TB_TB4077");
                mostrarFechaLimite = true;
            }
            else if ("P".equals(claseTransaccion))
            {
                tituloTexto = idioma.getString("TB_TB4076");
                tituloCompromisos = idioma.getString("TB_TB4071");
                fechaLimite = idioma.getString("TB_TB4078");
                mostrarFechaLimite = true;
            }
            else
            {
                tituloTexto = idioma.getString("TG_TEXTO2");
                tituloCompromisos = idioma.getString("TB_TB4071");
                fechaLimite = idioma.getString("TB_TB4078");
                mostrarFechaLimite = false;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        visibleAgente = indicadorAgente;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (css == null)
            {

                generarConsecutivo();
                registro.getCampos().put("HORA_INICIO", new Date());
                registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                                new Date());

                registro.getCampos().put("FECHAFIN", new Date());
                registro.getCampos().put("FECHAFIN", new Date());
                tituloDescripcion = idioma.getString("TT_LB5027");

                visibleCompromisos = false;
                modificarControlesAccidente();

            }
            else
            {
                // modificarControlesIndreunion(
                // (boolean)
                // registro.getCampos().get(indReunionCons));
                modificarControlesAccidente();

                Map<String, Object> params = new TreeMap<>();

                params.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get("CLASE_EVENTO"));
                params.put(tipotransaccionCons, tipoTransaccion);

                registro.getCampos()
                                .put("RUTAFORMATO_PLANTILLA",
                                                listaClaseEvento.getRegistroUnico(
                                                                params)
                                                                .getCampos()
                                                                .get("CODIGO_PLANTILLA"));

            }

            registro.getCampos().put(nombreTransaccionCons, nombreTransaccion);

            precargarRegistro();

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

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
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // if (!validarCampos()) {
        // return false;
        // }

        registro.getCampos().remove(nombreCompletoCons);
        registro.getCampos().remove(cargoActualCons);
        registro.getCampos().remove(nombreTransaccionCons);
        registro.getCampos().remove(nombreClaseEventoCons);
        registro.getCampos().remove(indReunionCons);
        registro.getCampos().remove("CODIGO_PLANTILLA");
        registro.getCampos().remove(rutaFormatoPlantillaCons);
        registro.getCampos().remove("NOMBREAGENTE");

        registro.getCampos().put(tipotransaccionCons, tipoTransaccion);

        if (indicadorAgente)
        {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(), FrmtransaccionessstsControladorEnum.CLASE_AGENTE.getValue())
                || SysmanFunciones.validarCampoVacio(registro.getCampos(), FrmtransaccionessstsControladorEnum.AGENTE.getValue()))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4131"));
                return false;
            }
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     * 
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getFechaLimite()
    {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite)
    {
        this.fechaLimite = fechaLimite;
    }

    public boolean isMostrarFechaLimite()
    {
        return mostrarFechaLimite;
    }

    public void setMostrarFechaLimite(boolean mostrarFechaLimite)
    {
        this.mostrarFechaLimite = mostrarFechaLimite;
    }

    public boolean isVisibleCargoComite()
    {
        return visibleCargoComite;
    }

    public void setVisibleCargoComite(boolean visibleCargoComite)
    {
        this.visibleCargoComite = visibleCargoComite;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCmbTercero
     * 
     * @return listaCmbTercero
     */

    public RegistroDataModelImpl getListaCedula()
    {
        return listaCedula;
    }

    public RegistroDataModelImpl getListaClaseEvento()
    {
        return listaClaseEvento;
    }

    public void setListaClaseEvento(RegistroDataModelImpl listaClaseEvento)
    {
        this.listaClaseEvento = listaClaseEvento;
    }

    public void setListaCedula(RegistroDataModelImpl listaCedula)
    {
        this.listaCedula = listaCedula;
    }

    public RegistroDataModelImpl getListaModeloPlantilla()
    {
        return listaModeloPlantilla;
    }

    public void setListaModeloPlantilla(
        RegistroDataModelImpl listaModeloPlantilla)
    {
        this.listaModeloPlantilla = listaModeloPlantilla;
    }

    public String getModeloPlantilla()
    {
        return modeloPlantilla;
    }

    public void setModeloPlantilla(String modeloPlantilla)
    {
        this.modeloPlantilla = modeloPlantilla;
    }

    /**
     * Retorna la lista listaAgente
     * 
     * @return listaAgente
     */
    public RegistroDataModelImpl getListaAgente()
    {
        return listaAgente;
    }

    /**
     * Asigna la lista listaAgente
     * 
     * @param listaAgente
     * Variable a asignar en listaAgente
     */
    public void setListaAgente(RegistroDataModelImpl listaAgente)
    {
        this.listaAgente = listaAgente;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public String getTituloDescripcion()
    {
        return tituloDescripcion;
    }

    public void setTituloDescripcion(String tituloDescripcion)
    {
        this.tituloDescripcion = tituloDescripcion;
    }

    public String getTituloTexto()
    {
        return tituloTexto;
    }

    public void setTituloTexto(String tituloTexto)
    {
        this.tituloTexto = tituloTexto;
    }

    public boolean isVisibleCompromisos()
    {
        return visibleCompromisos;
    }

    public void setVisibleCompromisos(boolean visibleCompromisos)
    {
        this.visibleCompromisos = visibleCompromisos;
    }

    public boolean isIndicadorResponsable()
    {
        return indicadorResponsable;
    }

    public void setIndicadorResponsable(boolean indicadorResponsable)
    {
        this.indicadorResponsable = indicadorResponsable;
    }

    public boolean isIndicadorReunion()
    {
        return indicadorReunion;
    }

    public void setIndicadorReunion(boolean indicadorReunion)
    {
        this.indicadorReunion = indicadorReunion;
    }

    public boolean isIndicadorComite()
    {
        return indicadorComite;
    }

    public void setIndicadorComite(boolean indicadorComite)
    {
        this.indicadorComite = indicadorComite;
    }

    public String getClaseTransaccion()
    {
        return claseTransaccion;
    }

    public void setClaseTransaccion(String claseTransaccion)
    {
        this.claseTransaccion = claseTransaccion;
    }

    public boolean isVisibleBtnDetalle()
    {
        return visibleBtnDetalle;
    }

    public void setVisibleBtnDetalle(boolean visibleBtnDetalle)
    {
        this.visibleBtnDetalle = visibleBtnDetalle;
    }

    public String getTituloFrmPrincipal()
    {
        return tituloFrmPrincipal;
    }

    public void setTituloFrmPrincipal(String tituloFrmPrincipal)
    {
        this.tituloFrmPrincipal = tituloFrmPrincipal;
    }

    public boolean isVisibleHoras()
    {
        return visibleHoras;
    }

    public void setVisibleHoras(boolean visibleHoras)
    {
        this.visibleHoras = visibleHoras;
    }

    public String getTituloTxtFecha()
    {
        return tituloTxtFecha;
    }

    public void setTituloTxtFecha(String tituloTxtFecha)
    {
        this.tituloTxtFecha = tituloTxtFecha;
    }

    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    public String getTituloCompromisos()
    {
        return tituloCompromisos;
    }

    public void setTituloCompromisos(String tituloCompromisos)
    {
        this.tituloCompromisos = tituloCompromisos;
    }

    public boolean isVisibleCobertura()
    {
        return visibleCobertura;
    }

    public void setVisibleCobertura(boolean visibleCobertura)
    {
        this.visibleCobertura = visibleCobertura;
    }

    public boolean isMostrarCobertura()
    {
        return mostrarCobertura;
    }

    public void setMostrarCobertura(boolean mostrarCobertura)
    {
        this.mostrarCobertura = mostrarCobertura;
    }

    public boolean isVisibleAgente()
    {
        return visibleAgente;
    }

    public void setVisibleAgente(boolean visibleAgente)
    {
        this.visibleAgente = visibleAgente;
    }

}
