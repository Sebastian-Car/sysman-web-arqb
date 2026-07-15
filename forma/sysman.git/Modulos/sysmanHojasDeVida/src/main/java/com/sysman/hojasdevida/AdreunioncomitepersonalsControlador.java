/*-
 * AdreunioncomitepersonalsControlador.java
 *
 * 1.0
 * 
 * 05/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.AdreunioncomitepersonalControladorUrlEnum;
import com.sysman.hojasdevida.enums.RepresentantereunioncomisionControladorEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase me permite controlar los integrantes de un comite
 *
 * @version 1.0, 05/02/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class AdreunioncomitepersonalsControlador
                extends BeanBaseContinuoAcmeImpl {
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
     * variable que almacena los registros de la listacmbCodigo
     */
    private RegistroDataModelImpl listacmbCodigo;
    /**
     * variable que almacena los registros de la listacmbCodigoE
     */
    private RegistroDataModelImpl listacmbCodigoE;
    private String sucursal;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private Map<String, Object> parametrosRecibidos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AdreunioncomitepersonalsControlador
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public AdreunioncomitepersonalsControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.ADREUNIONCOMITEPERSONAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            parametrosRecibidos = new TreeMap<>();
            parametrosRecibidos = SessionUtil.getFlash();
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
        enumBase = GenericUrlEnum.NAT_ASISTENTESCOMITE;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigo();
        cargarListacmbCodigoE();
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

        buscarUrls();

        parametrosListado.put("COMPANIA",
                        compania);

        parametrosListado
                        .put(RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                                        .getValue(),
                                        parametrosRecibidos
                                                        .get(RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                                                                        .getValue()));

        parametrosListado
                        .put(RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                                        .getValue(),
                                        parametrosRecibidos
                                                        .get(RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                                                                        .getValue()));
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbCodigo
     *
     */
    public void cargarListacmbCodigo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdreunioncomitepersonalControladorUrlEnum.URL100
                                                        .getValue());
        listacmbCodigo = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        "NIT");
    }

    /**
     * 
     * Carga la lista listacmbCodigo
     *
     */
    public void cargarListacmbCodigoE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbCodigo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbCodigoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT",
                        registroAux.getCampos().get("NIT"));

        registro.getCampos().put("NOMBRE",
                        registroAux.getCampos().get("NOMBRES"));

        sucursal = SysmanFunciones
                        .toString(registroAux.getCampos().get("SUCURSAL"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NUMERO_DCTO");
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
        // </CODIGO_DESARROLLADO>
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
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos()
                        .put(RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                                        .getValue(),
                                        parametrosRecibidos
                                                        .get(RepresentantereunioncomisionControladorEnum.NUMERO_COMITE
                                                                        .getValue()));
        registro.getCampos().put("SUCURSAL", sucursal);
        registro.getCampos().put("NUMERO_DCTO",
                        registro.getCampos().get("NIT"));

        registro.getCampos()
                        .put(RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                                        .getValue(),
                                        parametrosRecibidos
                                                        .get(RepresentantereunioncomisionControladorEnum.TIPOCOMITE
                                                                        .getValue()));
        registro.getCampos().remove("NIT");
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
        listaInicial.load();
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
        // Auto-generated method stub
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigo
     * 
     * @return listacmbCodigo
     */
    public RegistroDataModelImpl getListacmbCodigo() {
        return listacmbCodigo;
    }

    /**
     * Asigna la lista listacmbCodigo
     * 
     * @param listacmbCodigo
     * Variable a asignar en listacmbCodigo
     */
    public void setListacmbCodigo(RegistroDataModelImpl listacmbCodigo) {
        this.listacmbCodigo = listacmbCodigo;
    }

    /**
     * Retorna la lista listacmbCodigo
     * 
     * @return listacmbCodigo
     */
    public RegistroDataModelImpl getListacmbCodigoE() {
        return listacmbCodigoE;
    }

    /**
     * Asigna la lista listacmbCodigo
     * 
     * @param listacmbCodigo
     * Variable a asignar en listacmbCodigo
     */
    public void setListacmbCodigoE(RegistroDataModelImpl listacmbCodigoE) {
        this.listacmbCodigoE = listacmbCodigoE;
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
