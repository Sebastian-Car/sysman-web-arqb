/*-
 * CAlmacenContabilidadDepNiidCcControlador.java
 *
 * 1.0
 * 
 * 02/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
import com.sysman.contabilizar.enums.CAlmacenContabilidadDepNiidCcControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Permite configurar datos de interfaz entre almacen y contabilidad
 *
 * @version 1.0, 02/10/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidadDepNiidCcControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el numero del modulo
     * por el cual se ingreso a la aplicación
     */

    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     */
    private String centroCosto;

    private String centroCos;
    /**
     */
    private String codigoElemento;
    /**
     */
    private String nombre;

    private String anio;

    private boolean verCentroCosto;

    /**
     * Atributo que almacena el tipo heredado del formulario que abre
     * la clase
     */
    private String tipo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listaTipoMovimiento;
    private RegistroDataModelImpl listaTipoMovimientoE;
    private RegistroDataModelImpl listaCuentaDebito;
    private RegistroDataModelImpl listaCuentaDebitoE;
    private RegistroDataModelImpl listaCuentaCredito;
    private RegistroDataModelImpl listaCuentaCreditoE;
    private RegistroDataModelImpl listaCentroCosto;
    private RegistroDataModelImpl listaCentroCostoE;
    private RegistroDataModelImpl listacuentaDebitoVlrAct;
    private RegistroDataModelImpl listacuentaCreditoVlrAct;
    private RegistroDataModelImpl listaBodega;
    private RegistroDataModelImpl listacuentaDebitoVlrActE;
    private RegistroDataModelImpl listacuentaCreditoVlrActE;
    private RegistroDataModelImpl listaBodegaE;
    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacen;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private Map<String, Object> ridP;

    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * CAlmacenContabilidadDepNiidCcControlador
     */
    public CAlmacenContabilidadDepNiidCcControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1943
            numFormulario = GeneralCodigoFormaEnum.FRM_CALMACENCONTABILIDADDEPNIIFCC_CONTROLADOR.getCodigo();
            parametrosEntrada = SessionUtil.getFlash();

            centroCosto = SysmanConstantes.CONS_CENTRO;

            if (parametrosEntrada != null) {

                ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");

                codigoElemento = parametrosEntrada.get("codigoElemento")
                                .toString();

                anio = parametrosEntrada.get("anio")
                                .toString();

                tipo = parametrosEntrada.get("tipo")
                                .toString();

                nombre = parametrosEntrada.get("nombre")
                                .toString();

                centroCos = parametrosEntrada.get("centroCostoNiif").toString();

            }
            validarPermisos();
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

        enumBase = GenericUrlEnum.NIIF_INVENTARIOCONTA;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoMovimiento();
        cargarListaTipoMovimientoE();
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarlistacuentaDebitoVlrAct();
        cargarlistacuentaDebitoVlrActE();
        cargarlistacuentaCreditoVlrAct();
        cargarlistacuentaCreditoVlrActE();
        cargarlistaBodega();
        cargarlistaBodegaE();
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

        try {
            ejbContabilizarAlmacen.actualizarBodegaTipoActivo(compania, codigoElemento, Integer.parseInt(anio), centroCosto, SessionUtil.getUser().toString());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCosto);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0001
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoMovimiento
     *
     */
    public void cargarListaTipoMovimiento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0002.getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO_TIPOACTIVO");
    }

    /**
     * 
     * Carga la lista listaTipoMovimiento
     *
     */
    public void cargarListaTipoMovimientoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0002.getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoMovimientoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO_TIPOACTIVO");
    }

    /**
     * 
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebitoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaDebitoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

      
    }

    /**
     * 
     * Carga la lista listaCuentaCredito
     */
    public void cargarListaCuentaCredito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaCredito
     *
     */
    public void cargarListaCuentaCreditoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaCreditoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCostoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    
    public void cargarlistacuentaCreditoVlrAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacuentaCreditoVlrAct = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

      
    }
    public void cargarlistacuentaCreditoVlrActE() {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        
        listacuentaCreditoVlrActE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
        
    }
    
    public void cargarlistacuentaDebitoVlrAct() {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        
        listacuentaDebitoVlrAct = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
        
    }
    public void cargarlistacuentaDebitoVlrActE() {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0003.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        
        listacuentaDebitoVlrActE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
        
    }
    public void cargarlistaBodega() {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0005.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        
        listaBodega = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
        
    }
    public void cargarlistaBodegaE() {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CAlmacenContabilidadDepNiidCcControladorUrlEnum.URL0005.getValue());
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        
        listaBodegaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
        
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CentroCosto en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCentroCostoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoMovimiento en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoMovimientoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBRE_TIPOACTIVO", registro
                                        .getCampos()
                                        .get("NOMBRE"));

        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ");
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ");
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOACTIVO", registroAux.getCampos().get("CODIGO_TIPOACTIVO"));

        registro.getCampos().put("NOMBRE_TIPOACTIVO",
                        registroAux.getCampos().get(
                                        "NOMBRE_TIPOACTIVO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimientoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO_TIPOACTIVO"), "").toString();

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        "NOMBRE_TIPOACTIVO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITO", registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITO", registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCosto = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();

        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();

        reasignarOrigen();
    }
    public void seleccionarFilacuentaDebitoVlrAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBITOVLRACTIVO", registroAux.getCampos().get("CODIGO"));
        
        reasignarOrigen();
    }
    public void seleccionarFilacuentaCreditoVlrAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDITOVLRACTIVO", registroAux.getCampos().get("CODIGO"));
        
        reasignarOrigen();
    }
    public void seleccionarFilaBodega(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("BODEGA", registroAux.getCampos().get("CODIGO"));
        reasignarOrigen();
    }
    public void seleccionarFilacuentaDebitoVlrActE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
        
        reasignarOrigen();
    }
    public void seleccionarFilacuentaCreditoVlrActE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
        
        reasignarOrigen();
    }
    public void seleccionarFilaBodegaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
        
        reasignarOrigen();
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

        if (centroCos.equals("true")) {

            verCentroCosto = true;

        }
        else {
            verCentroCosto = false;
        }

        /*
         * FR1918-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Maximize End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado TODO DOCUMENTACION ADICIONAL
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCosto);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
        registro.getCampos().remove("NOMBRE_TIPOACTIVO");
        registro.getCampos().remove("NOMBREBODEGA");
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

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", ridP);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("codigoElemento", registro.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.CALMACEN_CONTABILIDADS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // TODO Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable centroCosto
     * 
     * @return centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * Asigna la variable centroCosto
     * 
     * @param centroCosto
     * Variable a asignar en centroCosto
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * Retorna la variable codigoElemento
     * 
     * @return codigoElemento
     */
    public String getCodigoElemento() {
        return codigoElemento;
    }

    /**
     * Asigna la variable codigoElemento
     * 
     * @param codigoElemento
     * Variable a asignar en codigoElemento
     */
    public void setCodigoElemento(String codigoElemento) {
        this.codigoElemento = codigoElemento;
    }

    /**
     * Retorna la variable nombreLargo
     * 
     * @return nombreLargo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombreLargo
     * 
     * @param nombreLargo
     * Variable a asignar en nombreLargo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoMovimiento
     * 
     * @return listaTipoMovimiento
     */
    public RegistroDataModelImpl getListaTipoMovimiento() {
        return listaTipoMovimiento;
    }

    /**
     * Asigna la lista listaTipoMovimiento
     * 
     * @param listaTipoMovimiento
     * Variable a asignar en listaTipoMovimiento
     */
    public void setListaTipoMovimiento(RegistroDataModelImpl listaTipoMovimiento) {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }

    /**
     * Retorna la lista listaTipoMovimiento
     * 
     * @return listaTipoMovimiento
     */
    public RegistroDataModelImpl getListaTipoMovimientoE() {
        return listaTipoMovimientoE;
    }

    /**
     * Asigna la lista listaTipoMovimiento
     * 
     * @param listaTipoMovimiento
     * Variable a asignar en listaTipoMovimiento
     */
    public void setListaTipoMovimientoE(RegistroDataModelImpl listaTipoMovimientoE) {
        this.listaTipoMovimientoE = listaTipoMovimientoE;
    }

    /**
     * Retorna la lista listaCuentaDebito
     * 
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebito() {
        return listaCuentaDebito;
    }

    /**
     * Asigna la lista listaCuentaDebito
     * 
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebito(RegistroDataModelImpl listaCuentaDebito) {
        this.listaCuentaDebito = listaCuentaDebito;
    }

    /**
     * Retorna la lista listaCuentaDebito
     * 
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebitoE() {
        return listaCuentaDebitoE;
    }

    /**
     * Asigna la lista listaCuentaDebito
     * 
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebitoE(RegistroDataModelImpl listaCuentaDebitoE) {
        this.listaCuentaDebitoE = listaCuentaDebitoE;
    }

    /**
     * Retorna la lista listaCuentaCredito
     * 
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCredito() {
        return listaCuentaCredito;
    }

    /**
     * Asigna la lista listaCuentaCredito
     * 
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCredito(RegistroDataModelImpl listaCuentaCredito) {
        this.listaCuentaCredito = listaCuentaCredito;
    }

    /**
     * Retorna la lista listaCuentaCredito
     * 
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCreditoE() {
        return listaCuentaCreditoE;
    }

    /**
     * Asigna la lista listaCuentaCredito
     * 
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCreditoE(RegistroDataModelImpl listaCuentaCreditoE) {
        this.listaCuentaCreditoE = listaCuentaCreditoE;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCostoE() {
        return listaCentroCostoE;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
        this.listaCentroCostoE = listaCentroCostoE;
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

    public boolean isVerCentroCosto() {
        return verCentroCosto;
    }

    public void setVerCentroCosto(boolean verCentroCosto) {
        this.verCentroCosto = verCentroCosto;
    }

    public RegistroDataModelImpl getListacuentaDebitoVlrAct() {
        return listacuentaDebitoVlrAct;
    }

    public void setListacuentaDebitoVlrAct(
        RegistroDataModelImpl listacuentaDebitoVlrAct) {
        this.listacuentaDebitoVlrAct = listacuentaDebitoVlrAct;
    }

    public RegistroDataModelImpl getListacuentaCreditoVlrAct() {
        return listacuentaCreditoVlrAct;
    }

    public void setListacuentaCreditoVlrAct(
        RegistroDataModelImpl listacuentaCreditoVlrAct) {
        this.listacuentaCreditoVlrAct = listacuentaCreditoVlrAct;
    }

    public RegistroDataModelImpl getListaBodega() {
        return listaBodega;
    }

    public void setListaBodega(RegistroDataModelImpl listaBodega) {
        this.listaBodega = listaBodega;
    }

    public RegistroDataModelImpl getListacuentaDebitoVlrActE() {
        return listacuentaDebitoVlrActE;
    }

    public void setListacuentaDebitoVlrActE(
        RegistroDataModelImpl listacuentaDebitoVlrActE) {
        this.listacuentaDebitoVlrActE = listacuentaDebitoVlrActE;
    }

    public RegistroDataModelImpl getListacuentaCreditoVlrActE() {
        return listacuentaCreditoVlrActE;
    }

    public void setListacuentaCreditoVlrActE(
        RegistroDataModelImpl listacuentaCreditoVlrActE) {
        this.listacuentaCreditoVlrActE = listacuentaCreditoVlrActE;
    }

    public RegistroDataModelImpl getListaBodegaE() {
        return listaBodegaE;
    }

    public void setListaBodegaE(RegistroDataModelImpl listaBodegaE) {
        this.listaBodegaE = listaBodegaE;
    }
    
    

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
