/*-
 * FrmsubtransaccionessstsControlador.java
 *
 * 1.0
 * 
 * 03/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.FrmsubtransaccionessstsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar el detalle de las transacciones de la
 * seguridad en el trabajoi
 *
 * @version 1.0, 03/01/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class FrmsubtransaccionessstsControlador
                extends BeanBaseContinuoAcmeImpl
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
     * Variable encargada de almacenar temprolamente el consecutivo
     * del header
     */
    private String consecutivo;
    /**
     * Variable encargada de almacenar el nombre de la transaccion
     */
    private String nombreTransaccion;
    /**
     * Atributo que valida si se visuazlian o no unos campso
     */
    private boolean mostrarAsistio;

    /**
     * Atributo que valida si el campo calificacion se hace visible o
     * no
     */
    private boolean mostrarCalificacion;
    /**
     * Atributo que almacena el valor del indicador del agente
     */
    private boolean indicadorAgente;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encarga de almacenar los datos de la de la persona
     */
    private RegistroDataModelImpl listaCedula;
    /**
     * Lista encarga de almacenar los datos de la de la persona
     */
    private RegistroDataModelImpl listaCedulaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Variable encargada de almancenar el tipo de transaccion
     */
    private String tipoTransaccion;
    /**
     * Variable encargada de almancenar la cedula de persona para
     * luego poder actualizarla en la grilla
     */
    private String cedula;
    /**
     * Variable encargada de almancenar la sucursal de persona para
     * luego poder actualizarla en la grilla
     */
    private String sucursal;
    /**
     * Variable encargada de almancenar la idEmpleado de persona para
     * luego poder actualizarla en la grilla
     */
    private String idEmpleado;
    /**
     * Variable encargada de almancenar el nombre de persona para
     * luego poder actualizarla en la grilla
     */
    private String nombres;
    /**
     * Constante que almacena el Estring TIPO_TRANSACCION
     */
    private final String tipoTransaccionCons;
    /**
     * Constante que almacena el Estring NOMBRE_TRANSACCION
     */
    private final String nombreTransaccionCons;
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
     * Atributo que almacena la Clase de Transaccion a la que
     * pertenece el "Tipo de Transaccion" que ha sido seleccionado
     */
    private String claseTransaccion;
    /**
     * Atributo que almacena los campos y valores que son llave para
     * el registro que se esta trabajando en el formulario
     * "FrmtransaccionessstsControlador (1563)"
     */
    private Map<String, Object> ridSstTransacciones;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmsubtransaccionessstsControlador
     */
    @SuppressWarnings("unchecked")
    public FrmsubtransaccionessstsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        tipoTransaccionCons = "TIPO_TRANSACCION";
        nombreTransaccionCons = "NOMBRE_TRASACCION";
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null)
        {
            consecutivo = (String) parametros.get("consecutivo");
            nombreTransaccion = (String) parametros.get("nombreTransaccion");
            tipoTransaccion = (String) parametros.get("tipoTransaccion");
            indicadorResponsable = (boolean) parametros.get("responsable");
            indicadorComite = (boolean) parametros.get("comite");
            claseTransaccion = (String) parametros.get("claseTransaccion");
            ridSstTransacciones = (Map<String, Object>) parametros.get("rid");
            indicadorAgente = (boolean) parametros.get("agente");
        }
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMSUBTRANSACCIONESSSTS_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.SST_D_TRANSACCIONES;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        registro.getCampos().put(nombreTransaccionCons, nombreTransaccion);

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCedula();
        cargarListaCedulaE();
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
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        parametrosListado.put(tipoTransaccionCons, tipoTransaccion);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCedula
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar su respuesta en la lsita listaCedula
     */
    public void cargarListaCedula()
    {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsubtransaccionessstsControladorUrlEnum.URL5170
                                                        .getValue());
        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());

    }

    /**
     * 
     * Carga la lista listaCedula
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar su respuesta en la lsita listaCedula
     */
    public void cargarListaCedulaE()
    {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsubtransaccionessstsControladorUrlEnum.URL5170
                                                        .getValue());
        listaCedulaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton ParticipantesExternos en
     * la vista
     *
     *
     */
    public void oprimirParticipantesExternos()
    {

        String[] campos = { "consecutivo", "nombreTransaccion",
                            "tipoTransaccion", "responsable", "comite",
                            "claseTransaccion", "rid" };
        Object[] valores = { retornarString(registro,
                        GeneralParameterEnum.CONSECUTIVO.getName()),
                             nombreTransaccion, tipoTransaccion,
                             indicadorResponsable,
                             indicadorComite, claseTransaccion,
                             ridSstTransacciones };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_SUBTRANSACCIONES_SSTS_EXTERNOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Cedula en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCedulaC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CEDULA",
                        cedula);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("ID_EMPLEADO", idEmpleado);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.NOMBRES.getName(),
                        nombres);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedula
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedula(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        logicaSeleccionarCedula(registroAux);

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedula
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedulaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cedula = retornarString(registroAux,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
        sucursal = retornarString(registroAux,
                        GeneralParameterEnum.SUCURSAL.getName());
        idEmpleado = retornarString(registroAux,
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        nombres = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRES.getName());

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

        mostrarAsistio = Arrays.asList("C", "R").contains(claseTransaccion);

        mostrarCalificacion = "C".equals(claseTransaccion);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
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
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        registro.getCampos().put(tipoTransaccionCons, tipoTransaccion);

        registro.getCampos().remove(nombreTransaccionCons);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());

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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        registro.getCampos().put(tipoTransaccionCons, tipoTransaccion);
        registro.getCampos().remove(nombreTransaccionCons);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());

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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        //
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
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

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {

        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        registro.getCampos().put(nombreTransaccionCons, nombreTransaccion);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable consecutivo
     * 
     * @return consecutivo
     */
    public String getConsecutivo()
    {
        return consecutivo;
    }

    /**
     * Asigna la variable consecutivo
     * 
     * @param consecutivo
     * Variable a asignar en consecutivo
     */
    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getNombreTransaccion()
    {
        return nombreTransaccion;
    }

    public void setNombreTransaccion(String nombreTransaccion)
    {
        this.nombreTransaccion = nombreTransaccion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCedula
     * 
     * @return listaCedula
     */
    public RegistroDataModelImpl getListaCedula()
    {
        return listaCedula;
    }

    /**
     * Asigna la lista listaCedula
     * 
     * @param listaCedula
     * Variable a asignar en listaCedula
     */
    public void setListaCedula(RegistroDataModelImpl listaCedula)
    {
        this.listaCedula = listaCedula;
    }

    /**
     * Retorna la lista listaCedula
     * 
     * @return listaCedula
     */
    public RegistroDataModelImpl getListaCedulaE()
    {
        return listaCedulaE;
    }

    /**
     * Asigna la lista listaCedula
     * 
     * @param listaCedula
     * Variable a asignar en listaCedula
     */
    public void setListaCedulaE(RegistroDataModelImpl listaCedulaE)
    {
        this.listaCedulaE = listaCedulaE;
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

    public boolean isMostrarAsistio()
    {
        return mostrarAsistio;
    }

    public void setMostrarAsistio(boolean mostrarAsistio)
    {
        this.mostrarAsistio = mostrarAsistio;
    }

    public boolean isMostrarCalificacion()
    {
        return mostrarCalificacion;
    }

    public void setMostrarCalificacion(boolean mostrarCalificacion)
    {
        this.mostrarCalificacion = mostrarCalificacion;
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

    private void logicaSeleccionarCedula(Registro registroAux)
    {
        registro.getCampos().put("CEDULA",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));
        registro.getCampos().put("ID_EMPLEADO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRES.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRES
                                        .getName()));

    }

    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
