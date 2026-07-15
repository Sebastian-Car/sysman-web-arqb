/*-
 * FrmcreeconfiguracionsControlador.java
 *
 * 1.0
 * 
 * 29/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmcreeconfiguracionsControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmcreeconfiguracionsControladorUrlEnum;
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
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para configurar las cuentas del cree
 *
 * @version 1.0, 29/11/2017
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class FrmcreeconfiguracionsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable que valida si el boton eliminar esta activo o no
     */
    private boolean inactivarEliminar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista de registros del combo cuenta debito actual
     */
    private RegistroDataModelImpl listaCuentaDebitoBase;
    /**
     * lista de rergistros del combo cuenta credito actual
     */
    private RegistroDataModelImpl listaCuentaCreditoBase;
    /**
     * lista de registros del combo cuenta debito anterior
     */
    private RegistroDataModelImpl listaCuentaDebitoBaseAv;
    /**
     * lista de registros del combo cuenta credito anterior
     */
    private RegistroDataModelImpl listaCuentaCreditoBaseAv;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
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

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmcreeconfiguracionsControlador
     */
    @SuppressWarnings("unchecked")
    public FrmcreeconfiguracionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1477
            numFormulario = GeneralCodigoFormaEnum.FRM_CREE_CONFIGURACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                ridConcepto = (Map<String, Object>) parametrosEntrada
                                .get("ridConcepto");
                ano = ridConcepto.get(
                                FrmcreeconfiguracionsControladorEnum.KEY_ANO
                                                .getValue())
                                .toString();
                tipoCobro = ridConcepto
                                .get(FrmcreeconfiguracionsControladorEnum.KEY_TIPOCOBRO
                                                .getValue())
                                .toString();
                concepto = ridConcepto
                                .get(FrmcreeconfiguracionsControladorEnum.KEY_CODIGO
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaDebitoBase();
        cargarListaCuentaCreditoBase();
        cargarListaCuentaDebitoBaseAv();
        cargarListaCuentaCreditoBaseAv();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
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
    public void iniciarListasSubNulo() {
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
    public void inicializar() {
        enumBase = GenericUrlEnum.SF_CREE_CONCEPTOS;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();
        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put(FrmcreeconfiguracionsControladorEnum.KEY_ANO
                        .getValue(), ano);
        parametrosListado.put(FrmcreeconfiguracionsControladorEnum.KEY_TIPOCOBRO
                        .getValue(), tipoCobro);
        parametrosListado.put(FrmcreeconfiguracionsControladorEnum.KEY_CONCEPTO
                        .getValue(), concepto);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCuentaDebitoBase
     */
    public void cargarListaCuentaDebitoBase() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcreeconfiguracionsControladorUrlEnum.URL6778
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoBase
     */
    public void cargarListaCuentaCreditoBase() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcreeconfiguracionsControladorUrlEnum.URL6778
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoBaseAv
     */
    public void cargarListaCuentaDebitoBaseAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcreeconfiguracionsControladorUrlEnum.URL6778
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoBaseAv = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoBaseAv
     */
    public void cargarListaCuentaCreditoBaseAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcreeconfiguracionsControladorUrlEnum.URL6778
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoBaseAv = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmcreeconfiguracionsControladorEnum.CUENTADEBITOBASE
                                        .getValue(),
                                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmcreeconfiguracionsControladorEnum.CUENTACREDITOBASE
                                        .getValue(),
                                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoBaseAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoBaseAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmcreeconfiguracionsControladorEnum.CUENTADEBITOBASE_AV
                                        .getValue(),
                                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoBaseAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoBaseAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmcreeconfiguracionsControladorEnum.CUENTACREDITOBASE_AV
                                        .getValue(),
                                        registroAux.getCampos().get("ID"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    public void oprimireliminar() {

        eliminarReg(registro);
        cargarRegistro(null, ACCION_INSERTAR);
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * metodo que se ejecuta al guardar o actualizar el registro
     * 
     * @return true o false
     */
    public boolean validarCuentas() {
        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos()
                                        .get(FrmcreeconfiguracionsControladorEnum.CUENTADEBITOBASE
                                                        .getValue())
                                        .toString())
            || SysmanFunciones.validarVariableVacio(
                            registro.getCampos().get(
                                            FrmcreeconfiguracionsControladorEnum.CUENTACREDITOBASE
                                                            .getValue())
                                            .toString())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3821"));
            return false;
        }
        return true;
    }

    /**
     * metodo que se ejecuta al guardar o actualizar el registro
     */
    public void actualizarCuentasAnteriores() {
        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos()
                                        .get(FrmcreeconfiguracionsControladorEnum.CUENTADEBITOBASE_AV
                                                        .getValue())
                                        .toString())) {
            registro.getCampos()
                            .put(FrmcreeconfiguracionsControladorEnum.CUENTADEBITOBASE_AV
                                            .getValue(),
                                            registro.getCampos()
                                                            .get(FrmcreeconfiguracionsControladorEnum.CUENTADEBITOBASE
                                                                            .getValue())
                                                            .toString());

        }

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos()
                                        .get(FrmcreeconfiguracionsControladorEnum.CUENTACREDITOBASE_AV
                                                        .getValue())
                                        .toString())) {
            registro.getCampos()
                            .put(FrmcreeconfiguracionsControladorEnum.CUENTACREDITOBASE_AV
                                            .getValue(),
                                            registro.getCampos().get(
                                                            FrmcreeconfiguracionsControladorEnum.CUENTACREDITOBASE
                                                                            .getValue())
                                                            .toString());

        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(FrmcreeconfiguracionsControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(GeneralParameterEnum.CONCEPTO.getName(),
                        concepto);

        Registro rsExiste;
        try {
            rsExiste = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcreeconfiguracionsControladorUrlEnum.URL331
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsExiste != null) {
                cargarRegistro(parametrosListado, ACCION_MODIFICAR);
                inactivarEliminar = false;
            }
            else {
                cargarRegistro(null, ACCION_INSERTAR);
                inactivarEliminar = true;
                registro.getCampos().put("FORMULA", "0");
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (ACCION_INSERTAR.equals(accion)) {
            inactivarEliminar = true;
        }
        else {
            inactivarEliminar = false;
        }

        // </CODIGO_DESARROLLADO>
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

        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos()
                            .remove(FrmcreeconfiguracionsControladorEnum.TIPOCOBRO
                                            .getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONCEPTO.getName());

        }

        if ((boolean) registro.getCampos().get("APLICACREE")) {
            if (SysmanFunciones.validarVariableVacio(
                            registro.getCampos().get("FORMULA").toString())) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3819"));
                return false;

            }

            if (SysmanFunciones.validarVariableVacio(
                            registro.getCampos().get("PORCENTAJECREE")
                                            .toString())
                || "0".equals(registro.getCampos().get("PORCENTAJECREE")
                                .toString())) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3820"));
                return false;

            }

            if (!validarCuentas()) {
                return false;
            }

            actualizarCuentasAnteriores();
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
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable inactivarEliminar
     * 
     * @return inactivarEliminar
     */
    public boolean isInactivarEliminar() {
        return inactivarEliminar;
    }

    /**
     * Asigna la variable inactivarEliminar
     * 
     * @param inactivarEliminar
     * Variable a asignar en inactivarEliminar
     */
    public void setInactivarEliminar(boolean inactivarEliminar) {
        this.inactivarEliminar = inactivarEliminar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaDebitoBase
     * 
     * @return listaCuentaDebitoBase
     */
    public RegistroDataModelImpl getListaCuentaDebitoBase() {
        return listaCuentaDebitoBase;
    }

    /**
     * Asigna la lista listaCuentaDebitoBase
     * 
     * @param listaCuentaDebitoBase
     * Variable a asignar en listaCuentaDebitoBase
     */
    public void setListaCuentaDebitoBase(
        RegistroDataModelImpl listaCuentaDebitoBase) {
        this.listaCuentaDebitoBase = listaCuentaDebitoBase;
    }

    /**
     * Retorna la lista listaCuentaCreditoBase
     * 
     * @return listaCuentaCreditoBase
     */
    public RegistroDataModelImpl getListaCuentaCreditoBase() {
        return listaCuentaCreditoBase;
    }

    /**
     * Asigna la lista listaCuentaCreditoBase
     * 
     * @param listaCuentaCreditoBase
     * Variable a asignar en listaCuentaCreditoBase
     */
    public void setListaCuentaCreditoBase(
        RegistroDataModelImpl listaCuentaCreditoBase) {
        this.listaCuentaCreditoBase = listaCuentaCreditoBase;
    }

    /**
     * Retorna la lista listaCuentaDebitoBaseAv
     * 
     * @return listaCuentaDebitoBaseAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoBaseAv() {
        return listaCuentaDebitoBaseAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoBaseAv
     * 
     * @param listaCuentaDebitoBaseAv
     * Variable a asignar en listaCuentaDebitoBaseAv
     */
    public void setListaCuentaDebitoBaseAv(
        RegistroDataModelImpl listaCuentaDebitoBaseAv) {
        this.listaCuentaDebitoBaseAv = listaCuentaDebitoBaseAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoBaseAv
     * 
     * @return listaCuentaCreditoBaseAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoBaseAv() {
        return listaCuentaCreditoBaseAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoBaseAv
     * 
     * @param listaCuentaCreditoBaseAv
     * Variable a asignar en listaCuentaCreditoBaseAv
     */
    public void setListaCuentaCreditoBaseAv(
        RegistroDataModelImpl listaCuentaCreditoBaseAv) {
        this.listaCuentaCreditoBaseAv = listaCuentaCreditoBaseAv;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
