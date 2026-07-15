/*-
 * FrmSubTransaccionesSstExternosControlador.java
 *
 * 1.0
 * 
 * 25/04/2018
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
import com.sysman.hojasdevida.enums.FrmSubTransaccionesSstExternosControladorEnum;
import com.sysman.hojasdevida.enums.FrmSubTransaccionesSstExternosControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que administra la información de los participantes
 * externos
 *
 * @version 1.0, 25/04/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmSubTransaccionesSstExternosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
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
     * Variable encargada de almancenar el tipo de transaccion
     */
    private String tipoTransaccion;

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
    /**
     * Atributo que valida si un campo se hace visible o no
     */
    private boolean mostrarAsistio;

    /**
     * Atributo que valida si el campo calificacion se hace visible o
     * no
     */
    private boolean mostrarCalificacion;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los nit de los terceros
     */
    private RegistroDataModelImpl listaNit;
    /**
     * Lista que carga los nit de los terceros en la grilla
     */
    private RegistroDataModelImpl listaNitE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmSubTransaccionesSstExternosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmSubTransaccionesSstExternosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1776;

            Map<String, Object> parametros = SessionUtil.getFlash();

            if (parametros != null) {
                consecutivo = (String) parametros.get("consecutivo");
                nombreTransaccion = (String) parametros
                                .get("nombreTransaccion");
                tipoTransaccion = (String) parametros.get("tipoTransaccion");
                indicadorResponsable = (boolean) parametros.get("responsable");
                indicadorComite = (boolean) parametros.get("comite");
                claseTransaccion = (String) parametros.get("claseTransaccion");
                ridSstTransacciones = (Map<String, Object>) parametros
                                .get("rid");
            }
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
    public void inicializar() {
        enumBase = GenericUrlEnum.SST_D_TRANSACCIONES;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNit();
        cargarListaNitE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        parametrosListado.put("TIPO_TRANSACCION", tipoTransaccion);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubTransaccionesSstExternosControladorUrlEnum.URL4444
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubTransaccionesSstExternosControladorUrlEnum.URL5555
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubTransaccionesSstExternosControladorUrlEnum.URL6666
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubTransaccionesSstExternosControladorUrlEnum.URL7777
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaNit
     *
     */
    public void cargarListaNit() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubTransaccionesSstExternosControladorUrlEnum.URL7435
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNit = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaNit
     *
     */
    public void cargarListaNitE() {
        listaNitE = listaNit;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Nit
     * 
     */
    public void cambiarNit() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Nit en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNitC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmSubTransaccionesSstExternosControladorEnum.NOMBRES
                                        .getValue(), registro
                                                        .getCampos()
                                                        .get(FrmSubTransaccionesSstExternosControladorEnum.NOMBRES
                                                                        .getValue()));

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(), registro
                                        .getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNit
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNit(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CEDULA", registroAux.getCampos().get("NIT"));

        registro.getCampos().put("ID_EMPLEADO", "0");

        registro.getCampos()
                        .put(FrmSubTransaccionesSstExternosControladorEnum.NOMBRES
                                        .getValue(), registroAux.getCampos()
                                                        .get(GeneralParameterEnum.NOMBRE
                                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNit
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();

        registro.getCampos().put("ID_EMPLEADO", "0");

        registro.getCampos()
                        .put(FrmSubTransaccionesSstExternosControladorEnum.NOMBRES
                                        .getValue(), registroAux.getCampos()
                                                        .get(GeneralParameterEnum.NOMBRE
                                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        mostrarAsistio = Arrays.asList("C", "R").contains(claseTransaccion);

        mostrarCalificacion = "C".equals(claseTransaccion);

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        registro.getCampos().put("TIPO_TRANSACCION", tipoTransaccion);

        registro.getCampos().put("IND_EXTERNO", "-1");

        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
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
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1776-ANTES_ACTUALIZAR Private Sub
         * Form_BeforeUpdate(Cancel As Integer) If
         * Nz(Me.TxtDescripcion, "") = "" Then MsgBox
         * "Debe ingresar una descripción.", vbInformation,
         * "Sysman Software" Cancel = True End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
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
    public boolean eliminarDespues() {
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
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("IND_EXTERNO");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("consecutivo", consecutivo);
        parametros.put("tipoTransaccion", tipoTransaccion);
        parametros.put("nombreTransaccion", nombreTransaccion);
        parametros.put("responsable", indicadorResponsable);
        parametros.put("comite", indicadorComite);
        parametros.put("claseTransaccion", claseTransaccion);
        parametros.put("rid", ridSstTransacciones);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FRMSUBTRANSACCIONESSSTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNit
     * 
     * @return listaNit
     */
    public RegistroDataModelImpl getListaNit() {
        return listaNit;
    }

    /**
     * Asigna la lista listaNit
     * 
     * @param listaNit
     * Variable a asignar en listaNit
     */
    public void setListaNit(RegistroDataModelImpl listaNit) {
        this.listaNit = listaNit;
    }

    /**
     * Retorna la lista listaNit
     * 
     * @return listaNit
     */
    public RegistroDataModelImpl getListaNitE() {
        return listaNitE;
    }

    /**
     * Asigna la lista listaNit
     * 
     * @param listaNit
     * Variable a asignar en listaNit
     */
    public void setListaNitE(RegistroDataModelImpl listaNitE) {
        this.listaNitE = listaNitE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getNombreTransaccion() {
        return nombreTransaccion;
    }

    public void setNombreTransaccion(String nombreTransaccion) {
        this.nombreTransaccion = nombreTransaccion;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public boolean isMostrarAsistio() {
        return mostrarAsistio;
    }

    public void setMostrarAsistio(boolean mostrarAsistio) {
        this.mostrarAsistio = mostrarAsistio;
    }

    public boolean isMostrarCalificacion() {
        return mostrarCalificacion;
    }

    public void setMostrarCalificacion(boolean mostrarCalificacion) {
        this.mostrarCalificacion = mostrarCalificacion;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
