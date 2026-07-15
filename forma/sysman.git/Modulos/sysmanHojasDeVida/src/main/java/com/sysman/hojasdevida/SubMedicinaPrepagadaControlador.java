/*-
 * SubMedicinaPrepagadaControlador.java
 *
 * 1.0
 * 
 * 29/01/2018
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
import com.sysman.hojasdevida.enums.SubMedicinaPrepagadaControladorEnum;
import com.sysman.hojasdevida.enums.SubMedicinaPrepagadaControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que gestionar la medicina prepagada de los funcionarios.
 *
 * @version 1.0, 20/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */

@ManagedBean
@ViewScoped
public class SubMedicinaPrepagadaControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consNombreFondo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>

    private Map<String, Object> parametrosEntrada;

    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    /**
     * Atributo que contiene el valor del documento del empleado el
     * cual se asigna por parametro
     */
    private String documento;

    /**
     * Atributo que contiene el valor de la sucursal del empleado el
     * cual se asigna por parametro
     */
    private String sucursal;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private String fondo;
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo de medicina
     * prepagada.
     */
    private RegistroDataModelImpl listaMedicinaPrepagada;

    /**
     * Lista que contiene los detalles del combo de medicina
     * prepagada.
     */
    private RegistroDataModelImpl listaMedicinaPrepagadaE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubMedicinaPrepagadaControlador
     */
    @SuppressWarnings("unchecked")
    public SubMedicinaPrepagadaControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNombreFondo = "NOMBREFONDO";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.SUB_MEDICINAPREPAGADA_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                documento = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
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
        tabla = GenericUrlEnum.NAT_DATOS_PERSONALES.getTable();
        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaMedicinaPrepagada();
        cargarListaMedicinaPrepagadaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubMedicinaPrepagadaControladorUrlEnum.URL15084
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubMedicinaPrepagadaControladorUrlEnum.URL9050
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        documento);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaMedicinaPrepagada
     *
     */
    public void cargarListaMedicinaPrepagada() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubMedicinaPrepagadaControladorUrlEnum.URL4592
                                                        .getValue());
        listaMedicinaPrepagada = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubMedicinaPrepagadaControladorEnum.FONDO.getValue());

    }

    /**
     * 
     * Carga la lista listaMedicinaPrepagada
     *
     */
    public void cargarListaMedicinaPrepagadaE() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubMedicinaPrepagadaControladorUrlEnum.URL4592
                                                        .getValue());
        listaMedicinaPrepagadaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubMedicinaPrepagadaControladorEnum.FONDO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedicinaPrepagada
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMedicinaPrepagada(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consNombreFondo,
                        registroAux.getCampos().get(consNombreFondo));
        fondo = registroAux.getCampos().get("FONDO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedicinaPrepagada
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMedicinaPrepagadaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NOMBRE_FONDO");
        fondo = registroAux.getCampos().get("FONDO").toString();
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaMedicinaPrepagada
     * 
     * @return listaMedicinaPrepagada
     */
    public RegistroDataModelImpl getListaMedicinaPrepagada() {
        return listaMedicinaPrepagada;
    }

    /**
     * Asigna la lista listaMedicinaPrepagada
     * 
     * @param listaMedicinaPrepagada
     * Variable a asignar en listaMedicinaPrepagada
     */
    public void setListaMedicinaPrepagada(
        RegistroDataModelImpl listaMedicinaPrepagada) {
        this.listaMedicinaPrepagada = listaMedicinaPrepagada;
    }

    public RegistroDataModelImpl getListaMedicinaPrepagadaE() {
        return listaMedicinaPrepagadaE;
    }

    public void setListaMedicinaPrepagadaE(
        RegistroDataModelImpl listaMedicinaPrepagadaE) {
        this.listaMedicinaPrepagadaE = listaMedicinaPrepagadaE;
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
        registro.getCampos().remove(consNombreFondo);
        registro.getCampos().put("MEDICINA_PREPAGADA", fondo);
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", documento);
        parametros.put("sucursal", sucursal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
