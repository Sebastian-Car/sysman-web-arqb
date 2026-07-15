/*-
 * FrmctaauxtercerosControlador.java
 *
 * 1.0
 * 
 * 05/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmcreeconfiguracionsControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmctaauxtercerosControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmctaauxtercerosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para almacenar las cuentas con auxiliar tercero
 * dependiendo conceptt¡o
 *
 * @version 1.0, 05/12/2017
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class FrmctaauxtercerosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en la aplicacion
     */
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ano recibido por parametro
     */
    private String ano;
    /**
     * Atributo que almacena el tipo de cobro recibido por parametro
     */
    private String tipoCobro;
    /**
     * Atributo que almacena el concepto recibido por parametro
     */
    private String concepto;
    /**
     * Map recibida por parametro que trae la llave del registro por
     * el cual se carga este formulario
     */
    Map<String, Object> ridConcepto;

    /**
     * variable que almacena el valor de la sucursal del tercero
     * seleccionado
     */
    private String sucursalTercero;
    /**
     * variable que almacena el valor del nombre de la cuenta
     * seleccionada
     */
    private String nombreCuenta;
    /**
     * variable que almacena el valor del nombre del tercero
     * seleccionado
     */
    private String nombreTercero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros del combo cuenta
     */
    private RegistroDataModelImpl listaCuenta;
    /**
     * Lista de registros del combo cuenta
     */
    private RegistroDataModelImpl listaCuentaE;
    /**
     * Lista de registros del combo tercero
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lista de registros del combo tercero
     */
    private RegistroDataModelImpl listaTerceroE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmctaauxtercerosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmctaauxtercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1483
            numFormulario = GeneralCodigoFormaEnum.FRM_CTAAUXTERCERO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                ridConcepto = (Map<String, Object>) parametrosEntrada
                                .get("ridConcepto");
                ano = ridConcepto.get(
                                FrmctaauxtercerosControladorEnum.KEY_ANO
                                                .getValue())
                                .toString();
                tipoCobro = ridConcepto
                                .get(FrmctaauxtercerosControladorEnum.KEY_TIPOCOBRO
                                                .getValue())
                                .toString();
                concepto = ridConcepto
                                .get(FrmctaauxtercerosControladorEnum.KEY_CODIGO
                                                .getValue())
                                .toString();
            }
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

        enumBase = GenericUrlEnum.SF_CUENTAS_AUXTERCERO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuenta();
        cargarListaCuentaE();
        cargarListaTercero();
        cargarListaTerceroE();
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(
                        FrmctaauxtercerosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        parametrosListado.put(GeneralParameterEnum.CONCEPTO.getName(),
                        concepto);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCuenta
     */
    public void cargarListaCuenta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmctaauxtercerosControladorUrlEnum.URL5308
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuenta
     */
    public void cargarListaCuentaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmctaauxtercerosControladorUrlEnum.URL5308
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaTercero
     */
    public void cargarListaTercero() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmctaauxtercerosControladorUrlEnum.URL8768
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    /**
     * 
     * Carga la lista listaTercero
     */
    public void cargarListaTerceroE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmctaauxtercerosControladorUrlEnum.URL8768
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Cuenta en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCuentaC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        FrmctaauxtercerosControladorEnum.NOMBRECUENTA
                                        .getValue(),
                        nombreCuenta);

    }

    /**
     * Metodo ejecutado al cambiar el control Tercero en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTerceroC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        FrmctaauxtercerosControladorEnum.NOMBRETERCERO
                                        .getValue(),
                        nombreTercero);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA", registroAux.getCampos().get("ID"));
        registro.getCampos().put(FrmctaauxtercerosControladorEnum.NOMBRECUENTA
                        .getValue(),
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
        nombreCuenta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(FrmctaauxtercerosControladorEnum.NOMBRETERCERO
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        nombreTercero = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
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
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        ano);
        registro.getCampos().put(FrmcreeconfiguracionsControladorEnum.TIPOCOBRO
                        .getValue(),
                        tipoCobro);
        registro.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        concepto);
        registro.getCampos()
                        .remove(FrmctaauxtercerosControladorEnum.NOMBRECUENTA
                                        .getValue());
        registro.getCampos()
                        .remove(FrmctaauxtercerosControladorEnum.NOMBRETERCERO
                                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        Registro existeCuenta = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FrmcreeconfiguracionsControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        param.put(GeneralParameterEnum.CUENTA.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CUENTA.getName()));

        try {

            existeCuenta = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmctaauxtercerosControladorUrlEnum.URL9426
                                                                            .getValue())
                                            .getUrl(), param));

            if (existeCuenta != null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3824")
                                .replace("s$cuenta$s",
                                                registro.getCampos()
                                                                .get(GeneralParameterEnum.CUENTA
                                                                                .getName())
                                                                .toString()));
                registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                                null);
                registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                                null);
                return false;
            }
            param.put(GeneralParameterEnum.CODIGO.getName(), concepto);

            Registro existeCodigoCuenta = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmctaauxtercerosControladorUrlEnum.URL7037
                                                                            .getValue())
                                            .getUrl(), param));
            if ("0".equals(existeCodigoCuenta.getCampos()
                            .get(GeneralParameterEnum.CUENTA.getName())
                            .toString())) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3825"));
                registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                                null);
                registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                                null);
                return false;
            }

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
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
     * @return true
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
     * @return true
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
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(
                        FrmctaauxtercerosControladorEnum.TIPOCOBRO.getValue());
        registro.getCampos().remove(GeneralParameterEnum.CONCEPTO.getName());
        registro.getCampos()
                        .remove(FrmctaauxtercerosControladorEnum.NOMBRECUENTA
                                        .getValue());
        registro.getCampos()
                        .remove(FrmctaauxtercerosControladorEnum.NOMBRETERCERO
                                        .getValue());

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "ridConcepto" };
        Object[] valores = { ridConcepto };

        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SFCONCEPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable sucursalTercero
     * 
     * @return sucursalTercero
     */
    public String getSucursalTercero() {
        return sucursalTercero;
    }

    /**
     * Asigna la variable sucursalTercero
     * 
     * @param sucursalTercero
     * Variable a asignar en sucursalTercero
     */
    public void setSucursalTercero(String sucursalTercero) {
        this.sucursalTercero = sucursalTercero;
    }

    /**
     * Retorna la variable nombreCuenta
     * 
     * @return nombreCuenta
     */
    public String getNombreCuenta() {
        return nombreCuenta;
    }

    /**
     * Asigna la variable nombreCuenta
     * 
     * @param nombreCuenta
     * Variable a asignar en nombreCuenta
     */
    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    /**
     * Retorna la variable nombreTercero
     * 
     * @return nombreTercero
     */
    public String getNombreTercero() {
        return nombreTercero;
    }

    /**
     * Asigna la variable nombreTercero
     * 
     * @param nombreTercero
     * Variable a asignar en nombreTercero
     */
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuenta
     * 
     * @return listaCuenta
     */
    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    /**
     * Asigna la lista listaCuenta
     * 
     * @param listaCuenta
     * Variable a asignar en listaCuenta
     */
    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

    /**
     * Retorna la lista listaCuenta
     * 
     * @return listaCuenta
     */
    public RegistroDataModelImpl getListaCuentaE() {
        return listaCuentaE;
    }

    /**
     * Asigna la lista listaCuenta
     * 
     * @param listaCuenta
     * Variable a asignar en listaCuenta
     */
    public void setListaCuentaE(RegistroDataModelImpl listaCuentaE) {
        this.listaCuentaE = listaCuentaE;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
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
