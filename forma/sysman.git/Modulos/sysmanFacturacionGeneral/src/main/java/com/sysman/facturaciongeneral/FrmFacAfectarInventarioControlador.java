/*-
 * FrmFacAfectaInventarioControlador.java
 *
 * 1.0
 * 
 * 9/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.FrmFacAfectarInventarioControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmFacAfectarInventarioControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

/**
 * Proceso que a partir de la factura crea el movimiento de elementos
 * en Almacen
 *
 * @version 1.0, 09/11/2017 Proceso de migracion de Access a Web,
 * Refactoring DSS,manejo de EJBs y cambio de numero de formulario por
 * enum
 * @author eamaya
 * 
 * @version 2.0, 14/11/2018 Adición del EJB registrarMovimiento
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmFacAfectarInventarioControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el tipo de cobro seleccionado al iniciar
     * el modulo
     */
    private final String tipoCobro;

    /**
     * Constante que alamacena el usuario que inicio sesion en la
     * aplciacion
     */
    private final String usuario;

    /**
     * Constante que almacena el anio de cobro seleccionado al iniciar
     * el modulo
     */
    private final String anioCobro;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que guarda la Dependencia seleccionada en la vista
     */
    private String dependencia;
    /**
     * Atributo que guarda el tipo de movimiento seleccionado en la
     * vista
     */
    private String tipoMovimiento;
    /**
     * Atributo que guarda el tipo de factura seleccionado en la vista
     */
    private String tipoFactura;
    /**
     * Atributo que guarda el numero de factura seleccionado en la
     * vista
     */
    private String numeroFactura;

    /**
     * Atributo que guarda la fecha en que se realiza el movimiento
     */
    private Date fechaMoviento;

    /**
     * Atributo que almacena el nombre de movimiento del combo tipo
     * movimiento
     */
    private String nombreMovimiento;
    private String claseMovimiento;

    private String bodegaOrigen;

    private String bodegaDestino;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que guarda las dependencias
     */
    private List<Registro> listaDependencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que guarda los tipos de movimientos
     */
    private RegistroDataModelImpl listaTipoMovimiento;
    /**
     * Lista que guarda los tipos de factura
     */
    private RegistroDataModelImpl listaTIPOFACTURA;
    /**
     * Lista que guarda los numeros de factura
     */
    private RegistroDataModelImpl listaNOFACTURA;

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionCero;

    @EJB
    private EjbFacturacionGeneralCuatroRemote ejbFacturacionCuatro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmFacAfectaInventarioControlador
     */
    public FrmFacAfectarInventarioControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();

        anioCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();

        usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_FAC_AFECTAR_INVENTARIO_CONTROLADOR
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
        // <CARGAR_LISTA>
        cargarListaDependencia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoMovimiento();
        cargarListaTIPOFACTURA();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaMoviento = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaTIPOFACTURA
     *
     */
    public void cargarListaTIPOFACTURA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmFacAfectarInventarioControladorUrlEnum.URL5732
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anioCobro);

        listaTIPOFACTURA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaNOFACTURA
     *
     */
    public void cargarListaNOFACTURA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmFacAfectarInventarioControladorUrlEnum.URL6213
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmFacAfectarInventarioControladorEnum.TIPOFACTURA.getValue(),
                        tipoFactura);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioCobro);

        listaNOFACTURA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_FACTURA");

    }

    /**
     * 
     * Carga la lista listaTipoMovimiento
     *
     */
    public void cargarListaTipoMovimiento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmFacAfectarInventarioControladorUrlEnum.URL7210
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmFacAfectarInventarioControladorUrlEnum.URL7616
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAceptar en la vista
     *
     *
     */
    public void oprimirCmdAceptar() {
        // <CODIGO_DESARROLLADO>
        try {

            long numeroMovimiento = 0;

            numeroMovimiento = ejbFacturacionCero.afectarInventario(compania,
                            tipoFactura,
                            new BigInteger(numeroFactura), tipoMovimiento,
                            fechaMoviento,
                            dependencia, tipoCobro, bodegaOrigen, bodegaDestino,
                            usuario);

            if (numeroMovimiento != 0) {

                ejbFacturacionCuatro.registrarMovimiento(compania,
                                tipoMovimiento,
                                numeroMovimiento, fechaMoviento, usuario);

            }
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        nombreMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        claseMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CLASE"), "")
                        .toString();
        bodegaOrigen = SysmanFunciones.nvl(
                        registroAux.getCampos().get("CLASE_BODEGA_ORIGEN"), "")
                        .toString();
        bodegaDestino = SysmanFunciones.nvl(
                        registroAux.getCampos().get("CLASE_BODEGA_DESTINO"), "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTIPOFACTURA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTIPOFACTURA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        cargarListaNOFACTURA();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNOFACTURA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNOFACTURA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_FACTURA"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable dependencia
     * 
     * @return dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * Asigna la variable dependencia
     * 
     * @param dependencia
     * Variable a asignar en dependencia
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * Retorna la variable tipoMovimiento
     * 
     * @return tipoMovimiento
     */
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    /**
     * Asigna la variable tipoMovimiento
     * 
     * @param tipoMovimiento
     * Variable a asignar en tipoMovimiento
     */
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    /**
     * Retorna la variable tipoFactura
     * 
     * @return tipoFactura
     */
    public String getTipoFactura() {
        return tipoFactura;
    }

    /**
     * Asigna la variable tipoFactura
     * 
     * @param tipoFactura
     * Variable a asignar en tipoFactura
     */
    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }

    /**
     * Retorna la variable numeroFactura
     * 
     * @return numeroFactura
     */
    public String getNumeroFactura() {
        return numeroFactura;
    }

    /**
     * Asigna la variable numeroFactura
     * 
     * @param numeroFactura
     * Variable a asignar en numeroFactura
     */
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    /**
     * Retorna la variable nombreMovimiento
     * 
     * @return nombreMovimiento
     */
    public String getNombreMovimiento() {
        return nombreMovimiento;
    }

    /**
     * Asigna la variable nombreMovimiento
     * 
     * @param nombreMovimiento
     * Variable a asignar en nombreMovimiento
     */
    public void setNombreMovimiento(String nombreMovimiento) {
        this.nombreMovimiento = nombreMovimiento;
    }

    /**
     * Retorna la variable fechaMoviento
     * 
     * @return fechaMoviento
     */
    public Date getFechaMoviento() {
        return fechaMoviento;
    }

    /**
     * Asigna la variable fechaMoviento
     * 
     * @param fechaMoviento
     * Variable a asignar en fechaMoviento
     */
    public void setFechaMoviento(Date fechaMoviento) {
        this.fechaMoviento = fechaMoviento;
    }

    public String getClaseMovimiento() {
        return claseMovimiento;
    }

    public void setClaseMovimiento(String claseMovimiento) {
        this.claseMovimiento = claseMovimiento;
    }

    public String getBodegaOrigen() {
        return bodegaOrigen;
    }

    public void setBodegaOrigen(String bodegaOrigen) {
        this.bodegaOrigen = bodegaOrigen;
    }

    public String getBodegaDestino() {
        return bodegaDestino;
    }

    public void setBodegaDestino(String bodegaDestino) {
        this.bodegaDestino = bodegaDestino;
    }

    public String getAnioCobro() {
        return anioCobro;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public List<Registro> getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(List<Registro> listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

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
    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento) {
        this.listaTipoMovimiento = listaTipoMovimiento;
    }

    /**
     * Retorna la lista listaTIPOFACTURA
     * 
     * @return listaTIPOFACTURA
     */
    public RegistroDataModelImpl getListaTIPOFACTURA() {
        return listaTIPOFACTURA;
    }

    /**
     * Asigna la lista listaTIPOFACTURA
     * 
     * @param listaTIPOFACTURA
     * Variable a asignar en listaTIPOFACTURA
     */
    public void setListaTIPOFACTURA(RegistroDataModelImpl listaTIPOFACTURA) {
        this.listaTIPOFACTURA = listaTIPOFACTURA;
    }

    /**
     * Retorna la lista listaNOFACTURA
     * 
     * @return listaNOFACTURA
     */
    public RegistroDataModelImpl getListaNOFACTURA() {
        return listaNOFACTURA;
    }

    /**
     * Asigna la lista listaNOFACTURA
     * 
     * @param listaNOFACTURA
     * Variable a asignar en listaNOFACTURA
     */
    public void setListaNOFACTURA(RegistroDataModelImpl listaNOFACTURA) {
        this.listaNOFACTURA = listaNOFACTURA;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
