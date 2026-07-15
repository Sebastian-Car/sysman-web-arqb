/*-
 * CAlmacenContabilidaddepsControlador.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.enums.CAlmacenContabilidaddepsControladorUrlEnum;
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
import com.sysman.services.RegistroDataModelImpl;

/**
 * Formulario que cambia la interfaz de almacen a contabilidad a fin
 * de mes
 *
 * @version 1.0, 13/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CAlmacenContabilidaddepsControlador extends BeanBaseDatosAcmeImpl {
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
    /**
     * Atributo que recibe el anio recibido anteriormente
     */
    private String anio;

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
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista ajuste por inflacion bodega debito
     */
    private RegistroDataModelImpl listaAjusInflaDebito;
    /**
     * Lista ajuste por inflacion bodega credito
     */
    private RegistroDataModelImpl listaAjusInflaCredito;
    /**
     * Lista depreciacion bodega debito
     */
    private RegistroDataModelImpl listaDeprecDebito;
    /**
     * Lista depreciacion bodega debito
     */
    private RegistroDataModelImpl listaDeprecCredito;
    /**
     * Lista ajuste a la depreciacion bodega debito
     */
    private RegistroDataModelImpl listaAjusDeprecDebito;
    /**
     * Lista ajuste a la depreciacion bodega credito
     */
    private RegistroDataModelImpl listaAjusDeprecCredito;
    /**
     * Lista valor historico bajas debito
     */
    private RegistroDataModelImpl listaCostoSalDb;
    /**
     * Lista valor historico bajas credito
     */
    private RegistroDataModelImpl listaCostoSalCr;
    /**
     * Lista ajuste por inflacion acum bajas debito
     */
    private RegistroDataModelImpl listaCostoSalAjDb;
    /**
     * Lista ajuste por inflacion acum bajas credito
     */
    private RegistroDataModelImpl listaCostoSalAjCr;
    /**
     * Lista depreciacion acum bajas debito
     */
    private RegistroDataModelImpl listaDepAcumuladaDb;
    /**
     * Lista depreciacion acum bajas credito
     */
    private RegistroDataModelImpl listaDepAcumuladaCr;
    /**
     * Lista ajuste a la depreciacion acum bajas debito
     */
    private RegistroDataModelImpl listaAjusteDepreciacionDb;
    /**
     * Lista ajuste a la depreciacion acum bajas credito
     */
    private RegistroDataModelImpl listaAjusteDepreciacionCr;
    /**
     * Lista ajuste por inflacion servicio debito
     */
    private RegistroDataModelImpl listaAjusInflaDebitoS;
    /**
     * Lista ajuste por inflacion servicio credito
     */
    private RegistroDataModelImpl listaAjusInflaCreditoS;
    /**
     * Lista depreciacion servicio debito
     */
    private RegistroDataModelImpl listaDeprecDebitoS;
    /**
     * Lista depreciacion servicio credito
     */
    private RegistroDataModelImpl listaDeprecCreditoS;
    /**
     * Lista ajuste a la depreciacion servicio debito
     */
    private RegistroDataModelImpl listaAjusDeprecDebitoS;
    /**
     * Lista ajuste a la depreciacion servicio credito
     */
    private RegistroDataModelImpl listaAjusDeprecCreditoS;
    /**
     * Lista ajuste a la depreciacion comodato servicio debito
     */
    private RegistroDataModelImpl listaDEPRECDEBITOSCOMODATO;
    /**
     * Lista ajuste a la depreciacion comodato servicio credito
     */
    private RegistroDataModelImpl listaDEPRECCREDITOSCOMODATO;
    /**
     * Lista cuantia minima servicio debito
     */
    private RegistroDataModelImpl listaDebCuantia;
    /**
     * Lista cuantia minima servicio credito
     */
    private RegistroDataModelImpl listaCreCuantia;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> ridP;

    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CAlmacenContabilidaddepsControlador
     */
    public CAlmacenContabilidaddepsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CALMACEN_CONTABILIDAD_DEPS_CONTROLADOR
                            .getCodigo();
            parametrosEntrada = SessionUtil.getFlash();
            registro = new Registro(new HashMap<String, Object>());
            if (parametrosEntrada != null) {

                ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");
                anio = parametrosEntrada.get("anio")
                                .toString();

                codigoElemento = parametrosEntrada.get("codigoElemento")
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaAjusInflaDebito();
        cargarListaAjusInflaCredito();
        cargarListaDeprecDebito();
        cargarListaDeprecCredito();
        cargarListaAjusDeprecDebito();
        cargarListaAjusDeprecCredito();
        cargarListaCostoSalDb();
        cargarListaCostoSalCr();
        cargarListaCostoSalAjDb();
        cargarListaCostoSalAjCr();
        cargarListaDepAcumuladaDb();
        cargarListaDepAcumuladaCr();
        cargarListaAjusteDepreciacionDb();
        cargarListaAjusteDepreciacionCr();
        cargarListaAjusInflaDebitoS();
        cargarListaAjusInflaCreditoS();
        cargarListaDeprecDebitoS();
        cargarListaDeprecCreditoS();
        cargarListaAjusDeprecDebitoS();
        cargarListaAjusDeprecCreditoS();
        cargarListaDEPRECDEBITOSCOMODATO();
        cargarListaDEPRECCREDITOSCOMODATO();
        cargarListaDebCuantia();
        cargarListaCreCuantia();
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
        enumBase = GenericUrlEnum.INVENTARIOCONTABILIDAD;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
        iniciarListas();
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

        parametrosListado.put("KEY_CODIGOELEMENTO", codigoElemento);

        parametrosListado.put("KEY_ANO", anio);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAjusInflaDebito
     *
     */
    public void cargarListaAjusInflaDebito() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CAlmacenContabilidaddepsControladorUrlEnum.URL11643
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaAjusInflaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAjusInflaCredito
     *
     */
    public void cargarListaAjusInflaCredito() {
        listaAjusInflaCredito = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDeprecDebito
     *
     */
    public void cargarListaDeprecDebito() {
        listaDeprecDebito = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDeprecCredito
     *
     */
    public void cargarListaDeprecCredito() {
        listaDeprecCredito = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusDeprecDebito
     *
     */
    public void cargarListaAjusDeprecDebito() {
        listaAjusDeprecDebito = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusDeprecCredito
     *
     */
    public void cargarListaAjusDeprecCredito() {
        listaAjusDeprecCredito = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaCostoSalDb
     *
     */
    public void cargarListaCostoSalDb() {
        listaCostoSalDb = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaCostoSalCr
     *
     */
    public void cargarListaCostoSalCr() {
        listaCostoSalCr = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaCostoSalAjDb
     *
     */
    public void cargarListaCostoSalAjDb() {
        listaCostoSalAjDb = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaCostoSalAjCr
     *
     */
    public void cargarListaCostoSalAjCr() {
        listaCostoSalAjCr = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDepAcumuladaDb
     *
     */
    public void cargarListaDepAcumuladaDb() {
        listaDepAcumuladaDb = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDepAcumuladaCr
     *
     */
    public void cargarListaDepAcumuladaCr() {
        listaDepAcumuladaCr = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusteDepreciacionDb
     *
     */
    public void cargarListaAjusteDepreciacionDb() {
        listaAjusteDepreciacionDb = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusteDepreciacionCr
     *
     */
    public void cargarListaAjusteDepreciacionCr() {
        listaAjusteDepreciacionCr = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusInflaDebitoS
     *
     */
    public void cargarListaAjusInflaDebitoS() {
        listaAjusInflaDebitoS = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusInflaCreditoS
     *
     */
    public void cargarListaAjusInflaCreditoS() {
        listaAjusInflaCreditoS = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDeprecDebitoS
     *
     */
    public void cargarListaDeprecDebitoS() {
        listaDeprecDebitoS = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDeprecCreditoS
     *
     */
    public void cargarListaDeprecCreditoS() {
        listaDeprecCreditoS = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusDeprecDebitoS
     *
     */
    public void cargarListaAjusDeprecDebitoS() {
        listaAjusDeprecDebitoS = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaAjusDeprecCreditoS
     *
     */
    public void cargarListaAjusDeprecCreditoS() {
        listaAjusDeprecCreditoS = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDEPRECDEBITOSCOMODATO
     *
     */
    public void cargarListaDEPRECDEBITOSCOMODATO() {
        listaDEPRECDEBITOSCOMODATO = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDEPRECCREDITOSCOMODATO
     *
     */
    public void cargarListaDEPRECCREDITOSCOMODATO() {
        listaDEPRECCREDITOSCOMODATO = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaDebCuantia
     *
     */
    public void cargarListaDebCuantia() {
        listaDebCuantia = listaAjusInflaDebito;
    }

    /**
     * 
     * Carga la lista listaCreCuantia
     *
     */
    public void cargarListaCreCuantia() {
        listaCreCuantia = listaAjusInflaDebito;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusInflaDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusInflaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLADEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusInflaCredito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusInflaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLACREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDeprecDebito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDeprecDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECDEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDeprecCredito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDeprecCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECCREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusDeprecDebito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusDeprecDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECDEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusDeprecCredito
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusDeprecCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECCREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCostoSalDb
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCostoSalDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALDB",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCostoSalCr
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCostoSalCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALCR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCostoSalAjDb
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCostoSalAjDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALAJDB",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCostoSalAjCr
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCostoSalAjCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COSTOSALAJCR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDepAcumuladaDb
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDepAcumuladaDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPACUMULADADB",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDepAcumuladaCr
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDepAcumuladaCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPACUMULADACR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusteDepreciacionDb
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusteDepreciacionDb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSTEDEPRECIACIONDB",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusteDepreciacionCr
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusteDepreciacionCr(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSTEDEPRECIACIONCR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusInflaDebitoS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusInflaDebitoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLADEBITOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusInflaCreditoS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusInflaCreditoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSINFLACREDITOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDeprecDebitoS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDeprecDebitoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECDEBITOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDeprecCreditoS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDeprecCreditoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECCREDITOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusDeprecDebitoS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusDeprecDebitoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECDEBITOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAjusDeprecCreditoS
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAjusDeprecCreditoS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AJUSDEPRECCREDITOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDEPRECDEBITOSCOMODATO
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDEPRECDEBITOSCOMODATO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECDEBITOSCOMODATO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDEPRECCREDITOSCOMODATO
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDEPRECCREDITOSCOMODATO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPRECCREDITOSCOMODATO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebCuantia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebCuantia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPCUANTIAMIN_DEB",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreCuantia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreCuantia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPCUANTIAMIN_CRE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

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

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        if (existeRegistro()) {
            cargarRegistro(parametrosListado, ACCION_MODIFICAR);
        }

    }

    private boolean existeRegistro() {
        boolean rta = true;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoElemento);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CAlmacenContabilidaddepsControladorUrlEnum.URL22019
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg == null) {

            rta = false;
        }
        return rta;

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaAjusInflaDebito
     * 
     * @return listaAjusInflaDebito
     */
    public RegistroDataModelImpl getListaAjusInflaDebito() {
        return listaAjusInflaDebito;
    }

    /**
     * Asigna la lista listaAjusInflaDebito
     * 
     * @param listaAjusInflaDebito
     * Variable a asignar en listaAjusInflaDebito
     */
    public void setListaAjusInflaDebito(
        RegistroDataModelImpl listaAjusInflaDebito) {
        this.listaAjusInflaDebito = listaAjusInflaDebito;
    }

    /**
     * Retorna la lista listaAjusInflaCredito
     * 
     * @return listaAjusInflaCredito
     */
    public RegistroDataModelImpl getListaAjusInflaCredito() {
        return listaAjusInflaCredito;
    }

    /**
     * Asigna la lista listaAjusInflaCredito
     * 
     * @param listaAjusInflaCredito
     * Variable a asignar en listaAjusInflaCredito
     */
    public void setListaAjusInflaCredito(
        RegistroDataModelImpl listaAjusInflaCredito) {
        this.listaAjusInflaCredito = listaAjusInflaCredito;
    }

    /**
     * Retorna la lista listaDeprecDebito
     * 
     * @return listaDeprecDebito
     */
    public RegistroDataModelImpl getListaDeprecDebito() {
        return listaDeprecDebito;
    }

    /**
     * Asigna la lista listaDeprecDebito
     * 
     * @param listaDeprecDebito
     * Variable a asignar en listaDeprecDebito
     */
    public void setListaDeprecDebito(RegistroDataModelImpl listaDeprecDebito) {
        this.listaDeprecDebito = listaDeprecDebito;
    }

    /**
     * Retorna la lista listaDeprecCredito
     * 
     * @return listaDeprecCredito
     */
    public RegistroDataModelImpl getListaDeprecCredito() {
        return listaDeprecCredito;
    }

    /**
     * Asigna la lista listaDeprecCredito
     * 
     * @param listaDeprecCredito
     * Variable a asignar en listaDeprecCredito
     */
    public void setListaDeprecCredito(
        RegistroDataModelImpl listaDeprecCredito) {
        this.listaDeprecCredito = listaDeprecCredito;
    }

    /**
     * Retorna la lista listaAjusDeprecDebito
     * 
     * @return listaAjusDeprecDebito
     */
    public RegistroDataModelImpl getListaAjusDeprecDebito() {
        return listaAjusDeprecDebito;
    }

    /**
     * Asigna la lista listaAjusDeprecDebito
     * 
     * @param listaAjusDeprecDebito
     * Variable a asignar en listaAjusDeprecDebito
     */
    public void setListaAjusDeprecDebito(
        RegistroDataModelImpl listaAjusDeprecDebito) {
        this.listaAjusDeprecDebito = listaAjusDeprecDebito;
    }

    /**
     * Retorna la lista listaAjusDeprecCredito
     * 
     * @return listaAjusDeprecCredito
     */
    public RegistroDataModelImpl getListaAjusDeprecCredito() {
        return listaAjusDeprecCredito;
    }

    /**
     * Asigna la lista listaAjusDeprecCredito
     * 
     * @param listaAjusDeprecCredito
     * Variable a asignar en listaAjusDeprecCredito
     */
    public void setListaAjusDeprecCredito(
        RegistroDataModelImpl listaAjusDeprecCredito) {
        this.listaAjusDeprecCredito = listaAjusDeprecCredito;
    }

    /**
     * Retorna la lista listaCostoSalDb
     * 
     * @return listaCostoSalDb
     */
    public RegistroDataModelImpl getListaCostoSalDb() {
        return listaCostoSalDb;
    }

    /**
     * Asigna la lista listaCostoSalDb
     * 
     * @param listaCostoSalDb
     * Variable a asignar en listaCostoSalDb
     */
    public void setListaCostoSalDb(RegistroDataModelImpl listaCostoSalDb) {
        this.listaCostoSalDb = listaCostoSalDb;
    }

    /**
     * Retorna la lista listaCostoSalCr
     * 
     * @return listaCostoSalCr
     */
    public RegistroDataModelImpl getListaCostoSalCr() {
        return listaCostoSalCr;
    }

    /**
     * Asigna la lista listaCostoSalCr
     * 
     * @param listaCostoSalCr
     * Variable a asignar en listaCostoSalCr
     */
    public void setListaCostoSalCr(RegistroDataModelImpl listaCostoSalCr) {
        this.listaCostoSalCr = listaCostoSalCr;
    }

    /**
     * Retorna la lista listaCostoSalAjDb
     * 
     * @return listaCostoSalAjDb
     */
    public RegistroDataModelImpl getListaCostoSalAjDb() {
        return listaCostoSalAjDb;
    }

    /**
     * Asigna la lista listaCostoSalAjDb
     * 
     * @param listaCostoSalAjDb
     * Variable a asignar en listaCostoSalAjDb
     */
    public void setListaCostoSalAjDb(RegistroDataModelImpl listaCostoSalAjDb) {
        this.listaCostoSalAjDb = listaCostoSalAjDb;
    }

    /**
     * Retorna la lista listaCostoSalAjCr
     * 
     * @return listaCostoSalAjCr
     */
    public RegistroDataModelImpl getListaCostoSalAjCr() {
        return listaCostoSalAjCr;
    }

    /**
     * Asigna la lista listaCostoSalAjCr
     * 
     * @param listaCostoSalAjCr
     * Variable a asignar en listaCostoSalAjCr
     */
    public void setListaCostoSalAjCr(RegistroDataModelImpl listaCostoSalAjCr) {
        this.listaCostoSalAjCr = listaCostoSalAjCr;
    }

    /**
     * Retorna la lista listaDepAcumuladaDb
     * 
     * @return listaDepAcumuladaDb
     */
    public RegistroDataModelImpl getListaDepAcumuladaDb() {
        return listaDepAcumuladaDb;
    }

    /**
     * Asigna la lista listaDepAcumuladaDb
     * 
     * @param listaDepAcumuladaDb
     * Variable a asignar en listaDepAcumuladaDb
     */
    public void setListaDepAcumuladaDb(
        RegistroDataModelImpl listaDepAcumuladaDb) {
        this.listaDepAcumuladaDb = listaDepAcumuladaDb;
    }

    /**
     * Retorna la lista listaDepAcumuladaCr
     * 
     * @return listaDepAcumuladaCr
     */
    public RegistroDataModelImpl getListaDepAcumuladaCr() {
        return listaDepAcumuladaCr;
    }

    /**
     * Asigna la lista listaDepAcumuladaCr
     * 
     * @param listaDepAcumuladaCr
     * Variable a asignar en listaDepAcumuladaCr
     */
    public void setListaDepAcumuladaCr(
        RegistroDataModelImpl listaDepAcumuladaCr) {
        this.listaDepAcumuladaCr = listaDepAcumuladaCr;
    }

    /**
     * Retorna la lista listaAjusteDepreciacionDb
     * 
     * @return listaAjusteDepreciacionDb
     */
    public RegistroDataModelImpl getListaAjusteDepreciacionDb() {
        return listaAjusteDepreciacionDb;
    }

    /**
     * Asigna la lista listaAjusteDepreciacionDb
     * 
     * @param listaAjusteDepreciacionDb
     * Variable a asignar en listaAjusteDepreciacionDb
     */
    public void setListaAjusteDepreciacionDb(
        RegistroDataModelImpl listaAjusteDepreciacionDb) {
        this.listaAjusteDepreciacionDb = listaAjusteDepreciacionDb;
    }

    /**
     * Retorna la lista listaAjusteDepreciacionCr
     * 
     * @return listaAjusteDepreciacionCr
     */
    public RegistroDataModelImpl getListaAjusteDepreciacionCr() {
        return listaAjusteDepreciacionCr;
    }

    /**
     * Asigna la lista listaAjusteDepreciacionCr
     * 
     * @param listaAjusteDepreciacionCr
     * Variable a asignar en listaAjusteDepreciacionCr
     */
    public void setListaAjusteDepreciacionCr(
        RegistroDataModelImpl listaAjusteDepreciacionCr) {
        this.listaAjusteDepreciacionCr = listaAjusteDepreciacionCr;
    }

    /**
     * Retorna la lista listaAjusInflaDebitoS
     * 
     * @return listaAjusInflaDebitoS
     */
    public RegistroDataModelImpl getListaAjusInflaDebitoS() {
        return listaAjusInflaDebitoS;
    }

    /**
     * Asigna la lista listaAjusInflaDebitoS
     * 
     * @param listaAjusInflaDebitoS
     * Variable a asignar en listaAjusInflaDebitoS
     */
    public void setListaAjusInflaDebitoS(
        RegistroDataModelImpl listaAjusInflaDebitoS) {
        this.listaAjusInflaDebitoS = listaAjusInflaDebitoS;
    }

    /**
     * Retorna la lista listaAjusInflaCreditoS
     * 
     * @return listaAjusInflaCreditoS
     */
    public RegistroDataModelImpl getListaAjusInflaCreditoS() {
        return listaAjusInflaCreditoS;
    }

    /**
     * Asigna la lista listaAjusInflaCreditoS
     * 
     * @param listaAjusInflaCreditoS
     * Variable a asignar en listaAjusInflaCreditoS
     */
    public void setListaAjusInflaCreditoS(
        RegistroDataModelImpl listaAjusInflaCreditoS) {
        this.listaAjusInflaCreditoS = listaAjusInflaCreditoS;
    }

    /**
     * Retorna la lista listaDeprecDebitoS
     * 
     * @return listaDeprecDebitoS
     */
    public RegistroDataModelImpl getListaDeprecDebitoS() {
        return listaDeprecDebitoS;
    }

    /**
     * Asigna la lista listaDeprecDebitoS
     * 
     * @param listaDeprecDebitoS
     * Variable a asignar en listaDeprecDebitoS
     */
    public void setListaDeprecDebitoS(
        RegistroDataModelImpl listaDeprecDebitoS) {
        this.listaDeprecDebitoS = listaDeprecDebitoS;
    }

    /**
     * Retorna la lista listaDeprecCreditoS
     * 
     * @return listaDeprecCreditoS
     */
    public RegistroDataModelImpl getListaDeprecCreditoS() {
        return listaDeprecCreditoS;
    }

    /**
     * Asigna la lista listaDeprecCreditoS
     * 
     * @param listaDeprecCreditoS
     * Variable a asignar en listaDeprecCreditoS
     */
    public void setListaDeprecCreditoS(
        RegistroDataModelImpl listaDeprecCreditoS) {
        this.listaDeprecCreditoS = listaDeprecCreditoS;
    }

    /**
     * Retorna la lista listaAjusDeprecDebitoS
     * 
     * @return listaAjusDeprecDebitoS
     */
    public RegistroDataModelImpl getListaAjusDeprecDebitoS() {
        return listaAjusDeprecDebitoS;
    }

    /**
     * Asigna la lista listaAjusDeprecDebitoS
     * 
     * @param listaAjusDeprecDebitoS
     * Variable a asignar en listaAjusDeprecDebitoS
     */
    public void setListaAjusDeprecDebitoS(
        RegistroDataModelImpl listaAjusDeprecDebitoS) {
        this.listaAjusDeprecDebitoS = listaAjusDeprecDebitoS;
    }

    /**
     * Retorna la lista listaAjusDeprecCreditoS
     * 
     * @return listaAjusDeprecCreditoS
     */
    public RegistroDataModelImpl getListaAjusDeprecCreditoS() {
        return listaAjusDeprecCreditoS;
    }

    /**
     * Asigna la lista listaAjusDeprecCreditoS
     * 
     * @param listaAjusDeprecCreditoS
     * Variable a asignar en listaAjusDeprecCreditoS
     */
    public void setListaAjusDeprecCreditoS(
        RegistroDataModelImpl listaAjusDeprecCreditoS) {
        this.listaAjusDeprecCreditoS = listaAjusDeprecCreditoS;
    }

    /**
     * Retorna la lista listaDEPRECDEBITOSCOMODATO
     * 
     * @return listaDEPRECDEBITOSCOMODATO
     */
    public RegistroDataModelImpl getListaDEPRECDEBITOSCOMODATO() {
        return listaDEPRECDEBITOSCOMODATO;
    }

    /**
     * Asigna la lista listaDEPRECDEBITOSCOMODATO
     * 
     * @param listaDEPRECDEBITOSCOMODATO
     * Variable a asignar en listaDEPRECDEBITOSCOMODATO
     */
    public void setListaDEPRECDEBITOSCOMODATO(
        RegistroDataModelImpl listaDEPRECDEBITOSCOMODATO) {
        this.listaDEPRECDEBITOSCOMODATO = listaDEPRECDEBITOSCOMODATO;
    }

    /**
     * Retorna la lista listaDEPRECCREDITOSCOMODATO
     * 
     * @return listaDEPRECCREDITOSCOMODATO
     */
    public RegistroDataModelImpl getListaDEPRECCREDITOSCOMODATO() {
        return listaDEPRECCREDITOSCOMODATO;
    }

    /**
     * Asigna la lista listaDEPRECCREDITOSCOMODATO
     * 
     * @param listaDEPRECCREDITOSCOMODATO
     * Variable a asignar en listaDEPRECCREDITOSCOMODATO
     */
    public void setListaDEPRECCREDITOSCOMODATO(
        RegistroDataModelImpl listaDEPRECCREDITOSCOMODATO) {
        this.listaDEPRECCREDITOSCOMODATO = listaDEPRECCREDITOSCOMODATO;
    }

    /**
     * Retorna la lista listaDebCuantia
     * 
     * @return listaDebCuantia
     */
    public RegistroDataModelImpl getListaDebCuantia() {
        return listaDebCuantia;
    }

    /**
     * Asigna la lista listaDebCuantia
     * 
     * @param listaDebCuantia
     * Variable a asignar en listaDebCuantia
     */
    public void setListaDebCuantia(RegistroDataModelImpl listaDebCuantia) {
        this.listaDebCuantia = listaDebCuantia;
    }

    /**
     * Retorna la lista listaCreCuantia
     * 
     * @return listaCreCuantia
     */
    public RegistroDataModelImpl getListaCreCuantia() {
        return listaCreCuantia;
    }

    /**
     * Asigna la lista listaCreCuantia
     * 
     * @param listaCreCuantia
     * Variable a asignar en listaCreCuantia
     */
    public void setListaCreCuantia(RegistroDataModelImpl listaCreCuantia) {
        this.listaCreCuantia = listaCreCuantia;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigoElemento() {
        return codigoElemento;
    }

    public void setCodigoElemento(String codigoElemento) {
        this.codigoElemento = codigoElemento;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
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
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
