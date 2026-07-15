/*-
 * NatSubRenunciasControlador.java
 *
 * 1.0
 * 
 * 16/12/2017
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatSubRenunciasControladorEnum;
import com.sysman.hojasdevida.enums.NatSubRenunciasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite realizar el registro de retiros de los
 * empleados.
 *
 * @version 1.0, 10/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 */

@ManagedBean
@ViewScoped
public class NatSubRenunciasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consNombreCargo;
    private final String consCausa;
    private final String consCargo;
    /**
     * Constante que contiene el valor de CODIGOCAUSA
     */
    private final String consCodigo;
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
    
    private String codigo;

    /**
     * Atributo que contiene el valor del id del cargo del empleado en
     * la forma
     */
    private String idCargo;
    /**
     * Atributo que contiene los diversos parametros de entrada para
     * el filtro de la informacion
     */
    private Map<String, Object> parametrosEntrada;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;
    
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridNombramiento;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del campo tipo de acto. */
    private List<Registro> listaTipoActo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo de Causa de retiro.
     */
    private RegistroDataModelImpl listaCausaRetiro;
    /**
     * Lista que contiene los detalles del combo de Causa de retiro en
     * la grilla.
     */
    private RegistroDataModelImpl listaCausaRetiroE;
    /**
     * Lista que contiene los detalles del combo de cargo en la
     * grilla.
     */
    private RegistroDataModelImpl listaCargo;
    /**
     * Lista que contiene los detalles del combo de cargo en la
     * grilla.
     */
    private RegistroDataModelImpl listaCargoE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NatSubRenunciasControlador
     */
    @SuppressWarnings("unchecked")
    public NatSubRenunciasControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigo = "CODIGOCAUSA";
        consNombreCargo = "NOMBRE_DEL_CARGO";
        consCausa = "NO_CAUSA";
        consCargo = "ID_DE_CARGO";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUBRENUNCIAS_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("ridDatosPersonales");
                ridNombramiento = (Map<String, Object>) parametrosEntrada
                                .get("ridNombramiento");
                documento = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
                codigo = (String) parametrosEntrada.get("codigo");
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
        tabla = NatSubRenunciasControladorEnum.TABLA.getValue();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaTipoActo();
        cargarListaCausaRetiro();
        cargarListaCausaRetiroE();
        cargarListaCargo();
        cargarListaCargoE();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        NatSubRenunciasControladorUrlEnum.URL8754.getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL6201
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL4968
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL5473
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCausaRetiro
     *
     */
    public void cargarListaCausaRetiro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL5594
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCausaRetiro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    /**
     * 
     * Carga la lista listaCausaRetiro
     *
     */
    public void cargarListaCausaRetiroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL5594
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCausaRetiroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consCodigo);
    }

    /**
     * 
     * Carga la lista listaCargo
     */
    public void cargarListaCargo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL2346
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCargo);
    }

    /**
     * 
     * Carga la lista listaCargo
     */
    public void cargarListaCargoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubRenunciasControladorUrlEnum.URL2346
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCargoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCargo);
    }

    /**
     * 
     * Carga la lista listaTipoActo
     *
     */
    public void cargarListaTipoActo() {
        try {
            listaTipoActo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubRenunciasControladorUrlEnum.URL7452
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CausaRetiro en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCausaRetiroC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(consCausa, registro.getCampos().get(consCausa));
        // </CODIGO_DESARROLLADO>
    }
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCausaRetiro
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCausaRetiro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consCodigo,
                        registroAux.getCampos().get(consCodigo));
        registro.getCampos().put(consCausa, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCausaRetiro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCausaRetiroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consCodigo), "")
                        .toString();
        registro.getCampos().put(consCausa, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idCargo = registroAux.getCampos().get(consCargo).toString();
        registro.getCampos().put(consNombreCargo,
                        registroAux.getCampos().get(consNombreCargo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(consNombreCargo).toString();
        idCargo = registroAux.getCampos().get(consCargo).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarCargo();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarCargo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            documento);
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubRenunciasControladorUrlEnum.URL8147
                                                                            .getValue())
                                            .getUrl(), param));
            registro.getCampos().put(consNombreCargo,
                            reg.getCampos().get(consNombreCargo).toString());
            idCargo = reg.getCampos().get(consCargo).toString();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);

        registro.getCampos().put("RETIRO_O_NOMBRAMIENTO", "R");
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put("NO_TIPODOCUACTO", registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().put("NO_TIPO",
                        registro.getCampos().get(consCodigo));
        registro.getCampos().remove(consCodigo);
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove(consNombreCargo);
        registro.getCampos().put("NO_CARGOA", idCargo);

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
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridNombramiento);
        parametros.put("ridDatos", ridDatosPersonales);
        parametros.put("redireccion", "-1");
        parametros.put("dp_numedocu", documento);
        parametros.put("sucursal", sucursal);
        parametros.put("codigo", codigo);
        
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUB_NOMBRAMIENTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove("TIPOACTOADTIVO");
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
        cargarCargo();
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaTipoActo() {
        return listaTipoActo;
    }

    public void setListaTipoActo(List<Registro> listaTipoActo) {
        this.listaTipoActo = listaTipoActo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCausaRetiro() {
        return listaCausaRetiro;
    }

    public void setListaCausaRetiro(RegistroDataModelImpl listaCausaRetiro) {
        this.listaCausaRetiro = listaCausaRetiro;
    }

    public RegistroDataModelImpl getListaCausaRetiroE() {
        return listaCausaRetiroE;
    }

    public void setListaCausaRetiroE(RegistroDataModelImpl listaCausaRetiroE) {
        this.listaCausaRetiroE = listaCausaRetiroE;
    }

    public RegistroDataModelImpl getListaCargo() {
        return listaCargo;
    }

    public void setListaCargo(RegistroDataModelImpl listaCargo) {
        this.listaCargo = listaCargo;
    }

    public RegistroDataModelImpl getListaCargoE() {
        return listaCargoE;
    }

    public void setListaCargoE(RegistroDataModelImpl listaCargoE) {
        this.listaCargoE = listaCargoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
