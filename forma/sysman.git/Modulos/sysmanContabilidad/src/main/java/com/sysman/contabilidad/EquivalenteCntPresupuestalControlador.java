/*-
 * EquivalenteCntPresupuestalControlador.java
 *
 * 1.0
 * 
 * 18 jun. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.EquivalenteCntPresupuestalControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que permite creaar las equivalencias contables de un
 * rubro presupuestal
 *
 * @version 1.0, 18/06/2018
 * @author eamaya
 * 
 * @author asan, 27/08/2018
 * @version 2.0 
 * 1. Se redirecciona datos a mostrar en lista rubro a tabla PLAN_PPTAL_CONFIG
 * 2. se agregan campos AUXILIAR, FUENTE_RECURSOS, TERCERO, REFERENCIA, CENTRO_COSTO,
 *    ajustndo la tabla con los datos mencionadosta la sucursal en el f
 * 
 @author asana, 04/09/2018
 * @version 3.0 
 * 1. Se actualiza asignacion valores al momento de seleccionar el rubro al momento de actualiza
 * 2. Se reasigna el consecutivo en el método insertar
 */
@ManagedBean
@ViewScoped
public class EquivalenteCntPresupuestalControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por el que el usuario ingresa
     */

    private final String modulo;

    /**
     * Atributo que almacena el anio seleccionado en la vista
     */
    private String anio;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaNumero;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que carga las cuentas debitos
     */
    private RegistroDataModelImpl listaCuentaDebito;
    /**
     * Lista que carga las cuentas debitos en la grilla
     */
    private RegistroDataModelImpl listaCuentaDebitoE;

    /**
     * Lista que carga las cuentas creditos
     */
    private RegistroDataModelImpl listaCuentaCredito;
    /**
     * Lista que carga las cuentas creditos en la grilla
     */
    private RegistroDataModelImpl listaCuentaCreditoE;
    /**
     * Lista que carga los rubros presupuestales
     */
    private RegistroDataModelImpl listaRubroPptal;
    /**
     * Lista que carga los rubros presupuestales en la grilla
     */
    private RegistroDataModelImpl listaRubroPptalE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * EquivalenteCntPresupuestalControlador
     */
    public EquivalenteCntPresupuestalControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.EQUIVALENTECNT_PRESUPUESTAL_CONTROLADOR
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

        enumBase = GenericUrlEnum.EQUIVALENTECNT_PRESUPUESTAL;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaNumero();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();
        cargarListaRubroPptal();
        cargarListaRubroPptalE();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("ANIO", anio);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaNumero
     *
     */
    public void cargarListaNumero() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNumero = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            EquivalenteCntPresupuestalControladorUrlEnum.URL4993
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EquivalenteCntPresupuestalControladorUrlEnum.URL5613
                                                        .getValue());
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
        listaCuentaDebitoE = listaCuentaDebito;
    }

    /**
     * 
     * Carga la lista listaCuentaCredito
     *
     */
    public void cargarListaCuentaCredito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EquivalenteCntPresupuestalControladorUrlEnum.URL10445
                                                        .getValue());
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
        listaCuentaCreditoE = listaCuentaCredito;
    }

    /**
     * 
     * Carga la lista listaRubroPptal
     *
     */
    public void cargarListaRubroPptal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EquivalenteCntPresupuestalControladorUrlEnum.URL14757
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaRubroPptal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID"); //GeneralParameterEnum.CODIGO.getName()
    }

    /**
     * 
     * Carga la lista listaRubroPptal
     *
     */
    public void cargarListaRubroPptalE() {
        listaRubroPptalE = listaRubroPptal;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Iniciar en la vista
     *
     *
     */
    public void oprimirIniciar() {
        SessionUtil.cargarModal(
                        String.valueOf(GeneralCodigoFormaEnum.ACTUALIZA_CONFIGURACION_CONTROLADOR
                                        .getCodigo()),
                        modulo);
    }

    public void cambiarRubroPptalC(int rowNun)
    {
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put("CENTRO_COSTO", registro
                                        .getCampos()
                                        .get("CENTRO_COSTO"));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put("TERCERO", registro
                                        .getCampos()
                                        .get("TERCERO"));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put("AUXILIAR", registro
                                        .getCampos()
                                        .get("AUXILIAR"));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
                        .put("REFERENCIA", registro
                                        .getCampos()
                                        .get("REFERENCIA"));
        listaInicial.getDatasource().get(rowNun % 10).getCampos()
        .put("FUENTE_RECURSO", registro
                        .getCampos()
                        .get("FUENTE_RECURSO"));
    }
    
    
    
    /**
     * Metodo ejecutado al cambiar el control Numero
     * 
     */
    public void cambiarNumero() {
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();
        cargarListaRubroPptal();
        cargarListaRubroPptalE();

        reasignarOrigen();

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_CREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvlStr(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString(),
                        "");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_DEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvlStr(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString(),
                        "");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroPptal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroPptal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBRO_PPTAL",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        "CENTRO_COSTO"));
        registro.getCampos().put("TERCERO",
                        registroAux.getCampos().get(
                                        "TERCERO"));
        registro.getCampos().put("AUXILIAR",
                        registroAux.getCampos().get(
                                        "AUXILIAR"));
        registro.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get(
                                        "REFERENCIA"));
        registro.getCampos().put("FUENTE_RECURSO",
                        registroAux.getCampos().get(
                                        "FUENTE_RECURSO"));
        registro.getCampos().put("NOMBRE",
                        registroAux.getCampos().get(
                                        "NOMBRE"));
        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get(
                                        "SUCURSAL"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroPptal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroPptalE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        
        registro.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        "CENTRO_COSTO"));
        registro.getCampos().put("TERCERO",
                        registroAux.getCampos().get(
                                        "TERCERO"));
        registro.getCampos().put("AUXILIAR",
                        registroAux.getCampos().get(
                                        "AUXILIAR"));
        registro.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get(
                                        "REFERENCIA"));
        registro.getCampos().put("FUENTE_RECURSO",
                        registroAux.getCampos().get(
                                        "FUENTE_RECURSO"));
        registro.getCampos().put("NOMBRE",
                        registroAux.getCampos().get(
                                        "NOMBRE"));
        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get(
                                        "SUCURSAL"));
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
     * 
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
        try {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        anio);

        String criterio = SysmanFunciones.concatenar(" COMPANIA = ''", compania, "''" );
        long consecutivo;
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial("EQUIVALENTECNT_PRESUPUESTAL", criterio, "CONSECUTIVO", "1");
        
        registro.getCampos().put("CONSECUTIVO", consecutivo);
        
        registro.getCampos().remove("NOMBRE");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
        
        registro.getCampos().remove("NOMBRE");
                        
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
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
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaNumero
     * 
     * @return listaNumero
     */
    public List<Registro> getListaNumero() {
        return listaNumero;
    }

    /**
     * Asigna la lista listaNumero
     * 
     * @param listaNumero
     * Variable a asignar en listaNumero
     */
    public void setListaNumero(List<Registro> listaNumero) {
        this.listaNumero = listaNumero;
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
    public void setListaCuentaDebitoE(
        RegistroDataModelImpl listaCuentaDebitoE) {
        this.listaCuentaDebitoE = listaCuentaDebitoE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
    public void setListaCuentaCredito(
        RegistroDataModelImpl listaCuentaCredito) {
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
    public void setListaCuentaCreditoE(
        RegistroDataModelImpl listaCuentaCreditoE) {
        this.listaCuentaCreditoE = listaCuentaCreditoE;
    }

    /**
     * Retorna la lista listaRubroPptal
     * 
     * @return listaRubroPptal
     */
    public RegistroDataModelImpl getListaRubroPptal() {
        return listaRubroPptal;
    }

    /**
     * Asigna la lista listaRubroPptal
     * 
     * @param listaRubroPptal
     * Variable a asignar en listaRubroPptal
     */
    public void setListaRubroPptal(RegistroDataModelImpl listaRubroPptal) {
        this.listaRubroPptal = listaRubroPptal;
    }

    /**
     * Retorna la lista listaRubroPptal
     * 
     * @return listaRubroPptal
     */
    public RegistroDataModelImpl getListaRubroPptalE() {
        return listaRubroPptalE;
    }

    /**
     * Asigna la lista listaRubroPptal
     * 
     * @param listaRubroPptal
     * Variable a asignar en listaRubroPptal
     */
    public void setListaRubroPptalE(RegistroDataModelImpl listaRubroPptalE) {
        this.listaRubroPptalE = listaRubroPptalE;
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
