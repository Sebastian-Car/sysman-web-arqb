/*-
 * CAlmacenContabilidadTrasControlador.java
 *
 * 1.0
 * 
 * 7/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTrasControladorEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTrasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;

import java.util.Date;
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
 * Formulario que cambia la interfaz de almacen a contabilidad por
 * transaccion
 *
 * @version 1.0, 07/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidadTrasControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el modulo de la en la
     * cual inicio sesion el usuario
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena todos los movimientos
     */
    private RegistroDataModelImpl listaTipoMovimiento;
    /**
     * Lista que almacena todos los movimientos
     */
    private RegistroDataModelImpl listaTipoMovimientoE;

    /**
     * Lista que almacena las cuentas para debito iva
     */
    private RegistroDataModelImpl listadebitoIva;
    /**
     * Lista que almacena las cuentas para debito iva
     */
    private RegistroDataModelImpl listadebitoIvaE;
    /**
     * Lista que almacena las cuentas para credito iva
     */
    private RegistroDataModelImpl listacreditoIva;
    /**
     * Lista que almacena las cuentas para credito iva
     */
    private RegistroDataModelImpl listacreditoIvaE;

    /**
     * Lista que almacena las cuentas para debito
     */
    private RegistroDataModelImpl listacuentaDebito;
    /**
     * Lista que almacena las cuentas para debito
     */
    private RegistroDataModelImpl listacuentaDebitoE;
    /**
     * Lista que almacena las cuentas para credito
     */
    private RegistroDataModelImpl listacuentaCredito;
    /**
     * Lista que almacena las cuentas para credito
     */
    private RegistroDataModelImpl listacuentaCreditoE;

    /**
     * Lista que almacena las cuentas para debito base
     */
    private RegistroDataModelImpl listadebitoBase;
    /**
     * Lista que almacena las cuentas para debito base
     */
    private RegistroDataModelImpl listadebitoBaseE;
    /**
     * Lista que almacena las cuentas para credito base
     */
    private RegistroDataModelImpl listacreditoBase;
    /**
     * Lista que almacena las cuentas para credito base
     */
    private RegistroDataModelImpl listacreditoBaseE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Atributo que almacena el valor del codio de elemento
     * proveninete como parametro
     */
    private String codigoElemento;

    /**
     * Atributo que almacena el nombre del elemento proveninete como
     * parametro
     */
    private String nombreLargo;
    /**
     * Atributo que almacena el valor del anio por el momento
     */
    private String anio;

    /**
     * Atributo que almacena el tipo heredado del formulario que abre
     * la clase
     */
    private String tipo;

    /**
     * Atributo que almacena el valor del parámetro VALOR DE IVA
     * DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN
     */
    private String interfazAlmacen;

    /**
     * Atributo que administra la visibilidad de las columnas de
     * interfaz de almacen
     */
    private boolean verInterfazAlmacen;

    private Map<String, Object> ridP;

    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacenCero;

    /**
     * Crea una nueva instancia de CAlmacenContabilidadTrasControlador
     */
    public CAlmacenContabilidadTrasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CALMACEN_CONTABILIDAD_TRAS_CONTROLADOR
                            .getCodigo();

            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {

                ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");

                codigoElemento = parametrosEntrada.get("codigoElemento")
                                .toString();

                anio = parametrosEntrada.get("anio")
                                .toString();

                tipo = parametrosEntrada.get("tipo")
                                .toString();

                nombreLargo = parametrosEntrada.get("nombre")
                                .toString();

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
        tabla = GenericUrlEnum.ALMACEN_CONTABILIDAD.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoMovimiento();
        cargarListaTipoMovimientoE();
        cargarListadebitoIva();
        cargarListadebitoIvaE();
        cargarListacreditoIva();
        cargarListacreditoIvaE();
        cargarListacuentaDebito();
        cargarListacuentaDebitoE();
        cargarListacuentaCredito();
        cargarListacuentaCreditoE();
        cargarListadebitoBase();
        cargarListadebitoBaseE();
        cargarListacreditoBase();
        cargarListacreditoBaseE();
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

        parametrosListado.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        try {
            int ano=Integer.parseInt(anio);
    			ejbContabilizarAlmacenCero.insertaAlmacenContabilidad(compania, codigoElemento, tipo, ano);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL6969
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL8888
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL7777
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL5555
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
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL10599
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(CAlmacenContabilidadTrasControladorEnum.TIPO.getValue(),
                        tipo);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoMovimiento
     *
     */
    public void cargarListaTipoMovimientoE() {
        listaTipoMovimientoE = listaTipoMovimiento;
    }

    public void cargarListadebitoIva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL13180
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listadebitoIva = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listadebitoIva
     *
     */
    public void cargarListadebitoIvaE() {
        listadebitoIvaE = listadebitoIva;
    }

    /**
     * 
     * Carga la lista listacreditoIva
     *
     */
    public void cargarListacreditoIva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL14203
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacreditoIva = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacreditoIva
     *
     */
    public void cargarListacreditoIvaE() {
        listacreditoIvaE = listacreditoIva;
    }

    /**
     * 
     * Carga la lista listacuentaDebito
     *
     */
    public void cargarListacuentaDebito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL15235
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacuentaDebito
     *
     */
    public void cargarListacuentaDebitoE() {
        listacuentaDebitoE = listacuentaDebito;
    }

    /**
     * 
     * Carga la lista listacuentaCredito
     *
     */
    public void cargarListacuentaCredito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL16535
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacuentaCredito
     *
     */
    public void cargarListacuentaCreditoE() {
        listacuentaCreditoE = listacuentaCredito;
    }

    /**
     * 
     * Carga la lista listadebitoBase
     *
     */
    public void cargarListadebitoBase() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL17787
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listadebitoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listadebitoBase
     *
     */
    public void cargarListadebitoBaseE() {
        listadebitoBaseE = listadebitoBase;
    }

    /**
     * 
     * Carga la lista listacreditoBase
     *
     */
    public void cargarListacreditoBase() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTrasControladorUrlEnum.URL19060
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacreditoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacreditoBase
     *
     */
    public void cargarListacreditoBaseE() {
        listacreditoBaseE = listacreditoBase;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cerrar en la vista
     *
     */
    public void oprimirCerrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TipoMovimiento en la
     * fila seleccionada dentro de la grilla
     * 
     * indice de la fila seleccionada
     */
    public void cambiarTipoMovimientoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registro
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOMOVIMIENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoMovimiento
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoMovimientoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoIva
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoIva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoIva
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoIvaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoIva
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoIva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoIva
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoIvaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaDebito
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaDebito
     *
     * 
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaDebitoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaCredito
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaCredito
     * 
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaCreditoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoBaseE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoBaseE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
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

        try {
            interfazAlmacen = ejbSysmanUtil.consultarParametro(compania,
                            "VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN",
                            modulo, new Date(), false);

            verInterfazAlmacen = "SI".equals(interfazAlmacen) ? true : false;
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);

        registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {

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

    public String getCodigoElemento() {
        return codigoElemento;
    }

    public void setCodigoElemento(String codigoElemento) {
        this.codigoElemento = codigoElemento;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public Map<String, Object> getRidP() {
        return ridP;
    }

    public void setRidP(Map<String, Object> ridP) {
        this.ridP = ridP;
    }

    /**
     * Retorna la variable nombreLargo
     * 
     * @return nombreLargo
     */
    public String getNombreLargo() {
        return nombreLargo;
    }

    /**
     * Asigna la variable nombreLargo
     * 
     * @param nombreLargo
     * Variable a asignar en nombreLargo
     */
    public void setNombreLargo(String nombreLargo) {
        this.nombreLargo = nombreLargo;
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
    public void setListaTipoMovimiento(
        RegistroDataModelImpl listaTipoMovimiento) {
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
    public void setListaTipoMovimientoE(
        RegistroDataModelImpl listaTipoMovimientoE) {
        this.listaTipoMovimientoE = listaTipoMovimientoE;
    }

    /**
     * Retorna la lista listadebitoIva
     * 
     * @return listadebitoIva
     */
    public RegistroDataModelImpl getListadebitoIva() {
        return listadebitoIva;
    }

    /**
     * Asigna la lista listadebitoIva
     * 
     * @param listadebitoIva
     * Variable a asignar en listadebitoIva
     */
    public void setListadebitoIva(RegistroDataModelImpl listadebitoIva) {
        this.listadebitoIva = listadebitoIva;
    }

    /**
     * Retorna la lista listadebitoIva
     * 
     * @return listadebitoIva
     */
    public RegistroDataModelImpl getListadebitoIvaE() {
        return listadebitoIvaE;
    }

    /**
     * Asigna la lista listadebitoIva
     * 
     * @param listadebitoIva
     * Variable a asignar en listadebitoIva
     */
    public void setListadebitoIvaE(RegistroDataModelImpl listadebitoIvaE) {
        this.listadebitoIvaE = listadebitoIvaE;
    }

    /**
     * Retorna la lista listacreditoIva
     * 
     * @return listacreditoIva
     */
    public RegistroDataModelImpl getListacreditoIva() {
        return listacreditoIva;
    }

    /**
     * Asigna la lista listacreditoIva
     * 
     * @param listacreditoIva
     * Variable a asignar en listacreditoIva
     */
    public void setListacreditoIva(RegistroDataModelImpl listacreditoIva) {
        this.listacreditoIva = listacreditoIva;
    }

    /**
     * Retorna la lista listacreditoIva
     * 
     * @return listacreditoIva
     */
    public RegistroDataModelImpl getListacreditoIvaE() {
        return listacreditoIvaE;
    }

    /**
     * Asigna la lista listacreditoIva
     * 
     * @param listacreditoIva
     * Variable a asignar en listacreditoIva
     */
    public void setListacreditoIvaE(RegistroDataModelImpl listacreditoIvaE) {
        this.listacreditoIvaE = listacreditoIvaE;
    }

    /**
     * Retorna la lista listacuentaDebito
     * 
     * @return listacuentaDebito
     */
    public RegistroDataModelImpl getListacuentaDebito() {
        return listacuentaDebito;
    }

    /**
     * Asigna la lista listacuentaDebito
     * 
     * @param listacuentaDebito
     * Variable a asignar en listacuentaDebito
     */
    public void setListacuentaDebito(RegistroDataModelImpl listacuentaDebito) {
        this.listacuentaDebito = listacuentaDebito;
    }

    /**
     * Retorna la lista listacuentaDebito
     * 
     * @return listacuentaDebito
     */
    public RegistroDataModelImpl getListacuentaDebitoE() {
        return listacuentaDebitoE;
    }

    /**
     * Asigna la lista listacuentaDebito
     * 
     * @param listacuentaDebito
     * Variable a asignar en listacuentaDebito
     */
    public void setListacuentaDebitoE(
        RegistroDataModelImpl listacuentaDebitoE) {
        this.listacuentaDebitoE = listacuentaDebitoE;
    }

    /**
     * Retorna la lista listacuentaCredito
     * 
     * @return listacuentaCredito
     */
    public RegistroDataModelImpl getListacuentaCredito() {
        return listacuentaCredito;
    }

    /**
     * Asigna la lista listacuentaCredito
     * 
     * @param listacuentaCredito
     * Variable a asignar en listacuentaCredito
     */
    public void setListacuentaCredito(
        RegistroDataModelImpl listacuentaCredito) {
        this.listacuentaCredito = listacuentaCredito;
    }

    /**
     * Retorna la lista listacuentaCredito
     * 
     * @return listacuentaCredito
     */
    public RegistroDataModelImpl getListacuentaCreditoE() {
        return listacuentaCreditoE;
    }

    /**
     * Asigna la lista listacuentaCredito
     * 
     * @param listacuentaCredito
     * Variable a asignar en listacuentaCredito
     */
    public void setListacuentaCreditoE(
        RegistroDataModelImpl listacuentaCreditoE) {
        this.listacuentaCreditoE = listacuentaCreditoE;
    }

    /**
     * Retorna la lista listadebitoBase
     * 
     * @return listadebitoBase
     */
    public RegistroDataModelImpl getListadebitoBase() {
        return listadebitoBase;
    }

    /**
     * Asigna la lista listadebitoBase
     * 
     * @param listadebitoBase
     * Variable a asignar en listadebitoBase
     */
    public void setListadebitoBase(RegistroDataModelImpl listadebitoBase) {
        this.listadebitoBase = listadebitoBase;
    }

    /**
     * Retorna la lista listadebitoBase
     * 
     * @return listadebitoBase
     */
    public RegistroDataModelImpl getListadebitoBaseE() {
        return listadebitoBaseE;
    }

    /**
     * Asigna la lista listadebitoBase
     * 
     * @param listadebitoBase
     * Variable a asignar en listadebitoBase
     */
    public void setListadebitoBaseE(RegistroDataModelImpl listadebitoBaseE) {
        this.listadebitoBaseE = listadebitoBaseE;
    }

    /**
     * Retorna la lista listacreditoBase
     * 
     * @return listacreditoBase
     */
    public RegistroDataModelImpl getListacreditoBase() {
        return listacreditoBase;
    }

    /**
     * Asigna la lista listacreditoBase
     * 
     * @param listacreditoBase
     * Variable a asignar en listacreditoBase
     */
    public void setListacreditoBase(RegistroDataModelImpl listacreditoBase) {
        this.listacreditoBase = listacreditoBase;
    }

    /**
     * Retorna la lista listacreditoBase
     * 
     * @return listacreditoBase
     */
    public RegistroDataModelImpl getListacreditoBaseE() {
        return listacreditoBaseE;
    }

    /**
     * Asigna la lista listacreditoBase
     * 
     * @param listacreditoBase
     * Variable a asignar en listacreditoBase
     */
    public void setListacreditoBaseE(RegistroDataModelImpl listacreditoBaseE) {
        this.listacreditoBaseE = listacreditoBaseE;
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

    public boolean isVerInterfazAlmacen() {
        return verInterfazAlmacen;
    }

    public void setVerInterfazAlmacen(boolean verInterfazAlmacen) {
        this.verInterfazAlmacen = verInterfazAlmacen;
    }

    public String getInterfazAlmacen() {
        return interfazAlmacen;
    }

    public void setInterfazAlmacen(String interfazAlmacen) {
        this.interfazAlmacen = interfazAlmacen;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
