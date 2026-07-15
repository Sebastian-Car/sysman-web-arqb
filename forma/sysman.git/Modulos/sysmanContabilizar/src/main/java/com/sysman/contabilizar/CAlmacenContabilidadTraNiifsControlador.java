/*-
 * CAlmacenContabilidadTraNiifsControlador.java
 *
 * 1.0
 * 
 * 11/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.ejb.EjbContabilizarAlmacenCeroRemote;
import com.sysman.contabilizar.enums.CAlmacenContabilidadTraNiifsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/09/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidadTraNiifsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String codigoElemento;
    /**
     * TODO DOCUMENTACION NECESARIA
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

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaTipoMovimiento;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaTipoMovimientoE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaCuentaDebito;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaCuentaDebitoE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaCuentaCredito;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaCuentaCreditoE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listadebitoBase;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listadebitoBaseE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listacreditoBase;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listacreditoBaseE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listadebitoIva;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listadebitoIvaE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listacreditoIva;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listacreditoIvaE;

    @EJB
    private EjbContabilizarAlmacenCeroRemote ejbContabilizarAlmacenCero;
    
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private Map<String, Object> parametrosEntrada;

    private Map<String, Object> ridP;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * CAlmacenContabilidadTraNiifsControlador
     */
    public CAlmacenContabilidadTraNiifsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try

        {
            numFormulario = GeneralCodigoFormaEnum.C_ALMACEN_CONTABILIDAD_TRAN_IIF_CONTROLADOR
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
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();
        cargarListadebitoBase();
        cargarListadebitoBaseE();
        cargarListacreditoBase();
        cargarListacreditoBaseE();
        cargarListadebitoIva();
        cargarListadebitoIvaE();
        cargarListacreditoIva();
        cargarListacreditoIvaE();
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
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0001
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0002
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0003
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0004
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoMovimiento
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaTipoMovimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put("TIPO", tipo);

        listaTipoMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoMovimiento
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaTipoMovimientoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put("TIPO", tipo);

        listaTipoMovimientoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaDebito
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuentaDebito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuentaDebitoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
                                                        .getValue());
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
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuentaCredito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuentaCreditoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
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
     * Carga la lista listadebitoBase
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListadebitoBase() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListadebitoBaseE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listadebitoBaseE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacreditoBase
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacreditoBase() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacreditoBaseE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacreditoBaseE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listadebitoIva
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListadebitoIva() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0007
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListadebitoIvaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0007
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listadebitoIvaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacreditoIva
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacreditoIva() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0007
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
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacreditoIvaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidadTraNiifsControladorUrlEnum.URL0007
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacreditoIvaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TipoMovimiento en la
     * fila seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoMovimientoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registro
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
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
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
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
     * listaCuentaDebito
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CUENTADEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
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
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
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
        registro.getCampos().put("NIIF_CUENTACREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
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
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoBase
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_DEBITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoBase
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoBaseE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoBase
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CREDITO_BASE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoBase
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoBaseE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoIva
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoIva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_DEBITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadebitoIva
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladebitoIvaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoIva
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoIva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIIF_CREDITO_IVA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacreditoIva
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacreditoIvaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
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
        /*
         * FR1917-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim Str As String Dim strCuentaDebito As String Dim
         * strCuentaCredito As String Dim db As DAO.Database Dim
         * Compania As String Dim RsCuentas As DAO.Recordset Dim
         * RsValidar As DAO.Recordset Set db = CurrentDb()
         * DoCmd.Hourglass True '(TAR:1000076865; FECHA:06/10/2017;
         * AUTOR:AA) If
         * par("COMPANIA PARA INSERTAR COMPROBANTE ALMACEN",
         * Getcompany()) <> "100" Then Compania =
         * par("COMPANIA PARA INSERTAR COMPROBANTE ALMACEN",
         * Getcompany()) Else Compania =
         * par("COMPAÑIA EQUIVALENTE NIIF", Getcompany()) End If Str =
         * _
         * " SELECT Plan_contable.Codigo, Plan_contable.Nombre,COMPANIA,ANO "
         * & _ " FROM Plan_Contable " & _ " WHERE Compania='" &
         * Compania & "' AND Ano=" & GetYear() &
         * " AND Centro_costo  IS NULL  AND tercero IS NULL AND auxiliar is null and (Movimiento+Man_cen_cto+man_aux_ter+man_aux_gen) <>0 "
         * & _ " ORDER BY COMPANIA,ANO,ID "
         * Me!CuentaDebito_1.RowSource = Str Me!CuentaDebito_1.Requery
         * Me!CuentaCredito_1.RowSource = Str
         * Me!CuentaCredito_1.Requery '(TAR: ; FECHA: 29/05/2018;
         * AUTOR: JEG) 'Se agrega condición, debido a que en bases de
         * datos postgres cuando ya existen los registros noabre el
         * formulario. 'db.Execute
         * "INSERT INTO AlmacenContabilidad ( Compania, CodigoElemento, TipoMovimiento, Ano, CuentaDebito, CuentaCredito )"
         * & _ " SELECT TipoMovimiento.Compania,'" &
         * Forms![C_AlmacenContabilidad]![cmbElemento] &
         * "', TipoMovimiento.Codigo," & GetYear() & "," & _
         * " iif(TipoMovimiento.Clase = 'E','" &
         * Forms![C_AlmacenContabilidad]![CuentaActivo] &
         * "', '') ,iif(TipoMovimiento.Clase = 'S','" &
         * Forms![C_AlmacenContabilidad]![CuentaActivo] &
         * "', '') FROM TipoMovimiento Where TipoMovimiento.TipoElemento = '"
         * & Forms![C_AlmacenContabilidad]![Tipo] & "'" If
         * TIPOCONEXION = "POSTGRESQL" Then Str =
         * " SELECT TipoMovimiento.Compania,'" &
         * Forms![C_AlmacenContabilidad]![cmbElemento] &
         * "' AS CODIGOELEMENTO, TipoMovimiento.Codigo AS TIMPOMOV , "
         * & GetYear() & " AS ANO," & _
         * " iif(TipoMovimiento.Clase = 'E','" &
         * Forms![C_AlmacenContabilidad]![CuentaActivo] &
         * "', '') AS CUENTADEBITO, " & _
         * " iif(TipoMovimiento.Clase = 'S','" &
         * Forms![C_AlmacenContabilidad]![CuentaActivo] &
         * "', '') AS CUETACREDITO " & vbCrLf & _
         * " FROM TipoMovimiento LEFT JOIN AlmacenContabilidad ON (TipoMovimiento.COMPANIA = AlmacenContabilidad.COMPANIA) "
         * & _
         * " AND (TipoMovimiento.Codigo = AlmacenContabilidad.tipomovimiento) "
         * & vbCrLf & _ " WHERE TipoMovimiento.COMPANIA = '" &
         * Getcompany() & "' " & _
         * " AND TipoMovimiento.TipoElemento = '" &
         * Forms![C_AlmacenContabilidad]![Tipo] & "' " & _
         * " AND AlmacenContabilidad.CODIGOELEMENTO = '" &
         * Forms![C_AlmacenContabilidad]![cmbElemento] & "' " Set
         * RsCuentas = db.OpenRecordset(Str) If Not RsCuentas.EOF Then
         * RsCuentas.MoveLast RsCuentas.MoveFirst While Not
         * RsCuentas.EOF Str = " SELECT DISTINCT 'X' AS VALIDA " &
         * vbCrLf & _ " FROM ALMACENCONTABILIDAD AS AC " & vbCrLf & _
         * " WHERE AC.COMPANIA = '" & RsCuentas!Compania & "' " & _
         * " AND AC.CODIGOELEMENTO = '" & RsCuentas!CODIGOELEMENTO &
         * "' " & _ " AND AC.TIPOMOVIMIENTO = '" & RsCuentas!TIMPOMOV
         * & "' " & _ " AND AC.ANO = " & RsCuentas!Ano & " " Set
         * RsValidar = db.OpenRecordset(Str) If RsValidar.EOF Then
         * db.Execute
         * " INSERT INTO AlmacenContabilidad ( Compania, CodigoElemento, TipoMovimiento, Ano, CuentaDebito, CuentaCredito ) "
         * & vbCrLf & _ " VALUES ('" & RsCuentas!Compania & "', '" &
         * RsCuentas!CODIGOELEMENTO & "', '" & RsCuentas!TIMPOMOV &
         * "', " & _ " " & RsCuentas!Ano & ", '" &
         * RsCuentas!CuentaDebito & "', '" & RsCuentas!CUETACREDITO &
         * "') " End If RsCuentas.MoveNext Wend End If RsCuentas.Close
         * Else db.Execute
         * " INSERT INTO AlmacenContabilidad ( Compania, CodigoElemento, TipoMovimiento, Ano, CuentaDebito, CuentaCredito ) "
         * & vbCrLf & _ " SELECT TipoMovimiento.Compania, '" &
         * Forms![C_AlmacenContabilidad]![cmbElemento] &
         * "', TipoMovimiento.Codigo," & GetYear() & ", " & _
         * " iif(TipoMovimiento.Clase = 'E','" &
         * Forms![C_AlmacenContabilidad]![CuentaActivo] &
         * "', ''), iif(TipoMovimiento.Clase = 'S','" &
         * Forms![C_AlmacenContabilidad]![CuentaActivo] &
         * "', '') FROM TipoMovimiento Where TipoMovimiento.TipoElemento = '"
         * & Forms![C_AlmacenContabilidad]![Tipo] & "'" End If
         * Me.Requery 'DoCmd.Maximize '2007.12.01 If
         * par("VALOR DE IVA DISCRIMINADO EN INTERFAZ DIARIA DE ALMACEN"
         * ) = "SI" Then Me.DEBITO_BASE.VISIBLE = True
         * Me.DEBITO_BASE_Etiqueta.VISIBLE = True
         * Me.CREDITO_BASE.VISIBLE = True
         * Me.CREDITO_BASE_Etiqueta.VISIBLE = True
         * Me.DEBITO_IVA.VISIBLE = True Me.DEBITO_IVA_Etiqueta.VISIBLE
         * = True Me.CREDITO_IVA.VISIBLE = True
         * Me.CREDITO_IVA_Etiqueta.VISIBLE = True Me.ScrollBars = 3
         * Else Me.DEBITO_BASE.VISIBLE = False
         * Me.DEBITO_BASE_Etiqueta.VISIBLE = False
         * Me.CREDITO_BASE.VISIBLE = False
         * Me.CREDITO_BASE_Etiqueta.VISIBLE = False
         * Me.DEBITO_IVA.VISIBLE = False
         * Me.DEBITO_IVA_Etiqueta.VISIBLE = False
         * Me.CREDITO_IVA.VISIBLE = False
         * Me.CREDITO_IVA_Etiqueta.VISIBLE = False Me.ScrollBars = 2
         * End If DoCmd.Maximize DoCmd.Hourglass False 'FIN 2007.12.01
         * End Sub
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

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);

        registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

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
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
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
