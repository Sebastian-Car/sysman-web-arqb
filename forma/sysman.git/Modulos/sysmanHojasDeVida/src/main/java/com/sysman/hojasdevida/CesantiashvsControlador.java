/*-
 * CesantiashvsControlador.java
 *
 * 1.0
 * 
 * 26/02/2018
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
import com.sysman.hojasdevida.enums.CesantiashvsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase que gestiona las cesantias de un empleado
 *
 *
 * @version 1.0, 17/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CesantiashvsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar temporalmente los datos de los
     * fondos.
     */
    private RegistroDataModelImpl listanombreFondo;
    /**
     * Lista encargada de almacenar temporalmente los datos de los
     * fondos.
     */
    private RegistroDataModelImpl listanombreFondoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Varaible encargdada de almacenar el parametro fecha que ingresa
     * al formulario desde el formulario natdatos personales
     */
    private String fecha;
    /**
     * Varaible encargdada de almacenar el parametro fecha que ingresa
     * al formulario desde el formulario natdatos personales
     */
    private String idDeEmpleado;
    /**
     * Varaible encargdada de almacenar el parametro fecha que ingresa
     * al formulario desde el formulario natdatos personales
     * 
     */
    private Map<String, Object> rid;
    /**
     * Varibale encargada de almacenar el fondo de Cesantias que sera
     * guardado en el formulario
     */
    private String fondoCesantias;
    /**
     * Constante encargada de almacenar el String CESANTIAS
     */
    private final String cesantiasCons;
    /**
     * Constante encargada de almacenar el String FONDO_CESANTIAS
     */
    private final String fondoCesantiasCons;
    /**
     * Constante encargada de almacenar el String NOMBRE_FONDO
     */
    private final String nombreFondoCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CesantiashvsControlador
     */
    @SuppressWarnings("unchecked")
    public CesantiashvsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cesantiasCons = "CESANTIAS";
        fondoCesantiasCons = "FONDO_CESANTIAS";
        nombreFondoCons = "NOMBRE_FONDO";

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            idDeEmpleado = (String) parametros.get("idDeEmpleado");
            fecha = (String) parametros.get("fecha");
            rid = (Map<String, Object>) parametros.get("rid");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.CESANTIASHVS_CONTROLADOR
                            .getCodigo();
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

        tabla = cesantiasCons;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListanombreFondo();
        cargarListanombreFondoE();
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
        parametrosListado.put("ID_EMPLEADO",
                        idDeEmpleado);
        parametrosListado.put(GeneralParameterEnum.FECHA.getName(), fecha);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CesantiashvsControladorUrlEnum.URL001
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CesantiashvsControladorUrlEnum.URL002
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listanombreFondo
     *
     */
    public void cargarListanombreFondo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CesantiashvsControladorUrlEnum.URL004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listanombreFondo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, fondoCesantiasCons);

    }

    /**
     * 
     * Carga la lista listanombreFondo
     *
     */
    public void cargarListanombreFondoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CesantiashvsControladorUrlEnum.URL004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listanombreFondoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, fondoCesantiasCons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarnombreFondoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(nombreFondoCons, auxiliar);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("FONDO", fondoCesantias);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanombreFondo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanombreFondo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nombreFondoCons,
                        registroAux.getCampos().get("NOMBRE_FONDO_CESANTIAS"));

        registro.getCampos().put("FONDO",
                        registroAux.getCampos().get(fondoCesantiasCons));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanombreFondo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanombreFondoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get("NOMBRE_FONDO_CESANTIAS");
        fondoCesantias = registroAux.getCampos().get(fondoCesantiasCons)
                        .toString();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1722-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name log
         * "Ingresó a Datos Basicos, Fondos, Cajas de Compensacion"
         * End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(nombreFondoCons);
        registro.getCampos().remove("CESANTIA");
        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
        registro.getCampos().remove("INTERES");

        registro.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        registro.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());

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
        //
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", rid);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listanombreFondo
     * 
     * @return listanombreFondo
     */
    public RegistroDataModelImpl getListanombreFondo() {
        return listanombreFondo;
    }

    /**
     * Asigna la lista listanombreFondo
     * 
     * @param listanombreFondo
     * Variable a asignar en listanombreFondo
     */
    public void setListanombreFondo(RegistroDataModelImpl listanombreFondo) {
        this.listanombreFondo = listanombreFondo;
    }

    /**
     * Retorna la lista listanombreFondo
     * 
     * @return listanombreFondo
     */
    public RegistroDataModelImpl getListanombreFondoE() {
        return listanombreFondoE;
    }

    /**
     * Asigna la lista listanombreFondo
     * 
     * @param listanombreFondo
     * Variable a asignar en listanombreFondo
     */
    public void setListanombreFondoE(RegistroDataModelImpl listanombreFondoE) {
        this.listanombreFondoE = listanombreFondoE;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
