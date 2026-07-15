/*-
 * TransaccionmodelosControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.transautomaticas.ejb.impl.EjbTransAutomaticasCero;
import com.sysman.transautomaticas.enums.TransaccionModelosControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
/**
 * Se Almacenan las transacciones modelos.
 *
 * @version 1.0, 18/09/2018
 * @author asana
 * 
 * @version 2.0 06/11/2018
 * @author asana
 * 
 * Según indicaciones Yolima - Henry P
 * 1. Se quita del combo Clase a Pedir la opción "Cuenta de presupuesto"
 * 2. El numero de transacción se genera solo con el consecutivo que ingresa el usuario, no por consecutivo
 * 3. El campo número se cambia a tipo varchar para que se pueda configurar estructura de las transacciones.
 * 4. Solo los movimientos que tienen check de Movimiento activo se permite configurr centros de costo y detalles
 * 
 * 
 * ***** FAVOR NO ACTUALIZAR LA FORMA DADO QUE EN PROPIEDAD "BORDE" SE COLOCA "#AAAAAA  none  1px; text-transform:uppercase" 
 * PARA QUE SE VALIDE QUE LAS LETRAS QUE INGRESE EN LAS FORMULAS SE MUESTREN AL USUARIO EN MAYÚSCULAS  *****
 */
@ManagedBean
@ViewScoped
public class TransaccionModelosControlador extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    //<DECLARAR_ATRIBUTOS>
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_LISTAS>
    private RegistroDataModelImpl listaTipo;
    private RegistroDataModelImpl listacentroCosto;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaFuenteRecurso;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaTipoACopiar;
    private RegistroDataModelImpl listaNumeroACopiar;
    private RegistroDataModelImpl listaNumeroGenerar;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaTipoGenerar;
    private RegistroDataModelImpl listatipoGasto;
    private RegistroDataModelImpl listaconcepto;
    private RegistroDataModelImpl listamedioPago;
    
    private int anio;
    private String color;
    private String mensaje;

    private boolean formulaT;
    private boolean formulaB;
    private boolean formulaI;
    private boolean formulaG;
    private boolean formulaTC;
    private boolean formulaBC;
    private boolean formulaIC;
    private boolean formulaGC;
    private boolean camposBloqueoActua;
    
    
    @SuppressWarnings("unused")
	private Map<String, Object> parametroTransaccion;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTercero;
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;

    @EJB
    private EjbTransAutomaticasCero ejbTransAutomaticas;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    //<DECLARAR_LISTAS_SUBFORM>
    //</DECLARAR_LISTAS_SUBFORM>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_ADICIONALES>
    //</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de TransaccionmodelosControlador
     */
    @SuppressWarnings("unchecked")
	public TransaccionModelosControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = SysmanFunciones.ano(new Date());
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if(parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("parametroTransaccion");
                anio = (int) parametrosEntrada.get("ano");
            }
            numFormulario = GeneralCodigoFormaEnum.TRANSACCIONMODELO_CONTROLADOR.getCodigo();
            validarPermisos();
            formulaTC = true;
            formulaBC = true;
            formulaIC = true;
            formulaGC = true;
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
            SessionUtil.cleanFlash();
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas(){
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CARGAR_LISTA>
        cargarListaAno();
        cargarListaTipo();
        

        //</CARGAR_LISTA>
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub(){
        //<CARGAR_LISTAS_SUBFORM>
        //</CARGAR_LISTAS_SUBFORM>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
    }
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo(){
        //<CARGAR_LISTAS_SUBFORM_NULL>
        //</CARGAR_LISTAS_SUBFORM_NULL>
    }
    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar(){
        enumBase = GenericUrlEnum.TRANSACCIONMODELO;
        buscarLlave();
        asignarOrigenDatos();
    }
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaTipo(){

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL11959
                                        .getValue());

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listacentroCosto
     */
    public void cargarListacentroCosto(){

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.validarCampoVacio(registro.getCampos(), 
                        GeneralParameterEnum.ANO.getName()) ? anio : 
                            registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1812
                                        .getValue());

        listacentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaAuxiliar
     */
    public void cargarListaAuxiliar(){


        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1816
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.validarCampoVacio(registro.getCampos(), 
                        GeneralParameterEnum.ANO.getName()) ? anio : 
                            registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
        
        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaFuenteRecurso
     */
    public void cargarListaFuenteRecurso(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1815
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.validarCampoVacio(registro.getCampos(), 
                        GeneralParameterEnum.ANO.getName()) ? anio : 
                            registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaReferencia
     */
    public void cargarListaReferencia(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1814
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.validarCampoVacio(registro.getCampos(), 
                        GeneralParameterEnum.ANO.getName()) ? anio : 
                            registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaTipoACopiar
     */
    public void cargarListaTipoACopiar(){


        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1818
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        if (registro.getCampos().get(GeneralParameterEnum.ANO.getName())== null) {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1960"));

        } else {
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

            listaTipoACopiar = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.TIPO.getName());

        }

    }

    public void cargarListaTipoGenerar(){


        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1818
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.validarVariableVacio(String.valueOf(anio)) ? registro.getCampos().get(GeneralParameterEnum.ANO.getName()) : anio);

        listaTipoGenerar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.TIPO.getName());

    }
    /**
     * 
     * Carga la lista listaNumeroACopiar
     */
    public void cargarListaNumeroACopiar(){


        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1819
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get("TIPOCOPIAR"));

        listaNumeroACopiar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }
    /**
     * 
     * Carga la lista listaNumeroGenerar
     */
    public void cargarListaNumeroGenerar(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL1819
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get("TIPOGENERAR"));

        listaNumeroGenerar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }
    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno(){

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionModelosControladorUrlEnum.URL12000
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }


    }
    /**
     * 
     * Carga la lista listaTercero
     */
    public void cargarListaTercero(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL12012
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }
    public void cargarListaTipoGasto(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL2144
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        listatipoGasto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }
    public void cargarListaConceptosDian(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL2145
                                        .getValue());
        
        listaconcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }
    public void cargarListaMediosDePago(){
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionModelosControladorUrlEnum.URL2146
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        
        listamedioPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }
    //</METODOS_CARGAR_LISTA>

    public void cambiarVlrComprobante() {
        formulaTC = validarFormula(registro.getCampos().get("VLRCOMPROBANTE").toString());
        
        
        if(formulaTC) {
            formulaT = false; 
        }else {
            formulaT = true; 
        }
        
        
    }

    public void cambiarVlrBaseGravable() {
        formulaBC = validarFormula(registro.getCampos().get("VLRBASEGRAVABLE").toString());
        if(formulaBC) {
            formulaB = false; 
        }else {
            formulaB = true; 
        }
        
        
    }
    public void cambiarVlrIvaFacturado() {
        formulaIC = validarFormula(registro.getCampos().get("VLRIVAFACTURADO").toString());
        if(formulaIC) {
            formulaI = false; 
        }else {
            formulaI = true; 
        }
        
    }
    public void cambiarVlrAGirar() {
        formulaGC = validarFormula(registro.getCampos().get("VLRAGIRAR").toString());
        if(formulaGC) {
            formulaG = false; 
        }else {
            formulaG = true; 
        }
        
    }
    //<METODOS_CAMBIAR>	

    public void cambiarAno() {
        //<CODIGO_DESARROLLADO>
        anio = (int)registro.getCampos().get(GeneralParameterEnum.ANO.getName());
        cargarListaAuxiliar();
        cargarListacentroCosto();
        cargarListaFuenteRecurso();
        cargarListaReferencia();
        cargarListaTercero();
        cargarListaTipoGenerar();
        cargarListaConceptosDian();
        cargarListaMediosDePago();
        cargarListaTipoGasto();
        //</CODIGO_DESARROLLADO>
    }

    public void cambiarMovimiento() {
        
        if (ACCION_MODIFICAR.equals(accion)) {
            
            if ((boolean) registro.getCampos().get("MOVIMIENTO")) {
                camposBloqueoActua = true; 
            } else  {
                camposBloqueoActua = false; 
            }
        } else {
            camposBloqueoActua = false; 
            
        }
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>	

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaNumeroACopiar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NUMEROCOPIAR", registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
    }

    public void seleccionarFilaTipoACopiar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOCOPIAR", registroAux.getCampos().get(GeneralParameterEnum.TIPO.getName()));
        cargarListaNumeroACopiar();
    }

    public void seleccionarFilaTipoGenerar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOGENERAR", registroAux.getCampos().get(GeneralParameterEnum.TIPO.getName()));
        registro.getCampos().put("TIPOGENERARNOMBRE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));

        cargarListaNumeroGenerar();
    }
    public void seleccionarFilaconcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRECONCEPTO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("RESOLUCION", registroAux.getCampos().get(GeneralParameterEnum.RESOLUCION.getName()));
        
        cargarListaNumeroGenerar();
    }
    public void seleccionarFilatipoGasto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_GASTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRETIPOGASTO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        
        cargarListaNumeroGenerar();
    }

    public void seleccionarFilaNumeroGenerar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NUMEROGENERAR", registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
        registro.getCampos().put("NUMEROGENERARNOMBRE", registroAux.getCampos().get(GeneralParameterEnum.FECHA.getName()));

    }
    public void seleccionarFilamedioPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MEDIO_PAGO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREMEDIOPAGO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        
    }



    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRECENTROCOSTO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
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
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), registroAux.getCampos().get("NIT"));
        registro.getCampos().put("NOMBRETERCERO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREAUXILIAR", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRERECURSO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));


    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREREFERENCIA", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>       
    //</METODOS_ARBOL>
    //<METODOS_BOTONES>     
    /**
     * 
     * Metodo ejecutado al oprimir el boton btn_Centros
     * en la vista
     *
     */
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>	
    //</METODOS_ARBOL>
    //<METODOS_BOTONES>	
    /**
     * 
     * Metodo ejecutado al oprimir el boton btn_Centros
     * en la vista
     *
     */
    public void oprimirbtnCentros() {
        //<CODIGO_DESARROLLADO>
        String[] campos = { "ano", "tipo", "numero"};

        Object[] valores = { registro.getCampos().get(GeneralParameterEnum.ANO.getName().toString()),
                        String.valueOf(registro.getCampos().get(GeneralParameterEnum.TIPO.getName().toString())),
                        String.valueOf(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName().toString()))
        };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_CCTRANSACCIONMODELO_CONTROLADOR.getCodigo()),
                        SessionUtil.getModulo(), campos, valores);
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnDetalle
     * en la vista
     *
     */
    public void oprimirBtnDetalle() {
        //<CODIGO_DESARROLLADO>
        boolean guardaRegistro = false;
        
        if(!SysmanFunciones.validarCampoVacio(registro.getCampos(), "TIPO")) {
            if(accion.equals(ACCION_INSERTAR)) {
                agregarRegistroNuevo(true);
               
            } else {
                agregarRegistroNuevo(false);
            }
            if (guardaRegistro) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2634"));
                return;
            }
            
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.ANO.getName(), Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString()));
            param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString());
            param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
            param.put(GeneralParameterEnum.DESCRIPCION.getName(), registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
            param.put("rowTransaccion", registro.getLlave());
            
            Direccionador direccionador = new Direccionador();
            
            direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.DTRANSACCIONMODELO_CONTROLADOR.getCodigo()));
            direccionador.setParametros(param);
            SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        } else{
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2634"));
        }

        //</CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar() {
        //<CODIGO_DESARROLLADO>


        try {
            ejbTransAutomaticas.copiarTransaccionModelo( compania,
                            anio, registro.getCampos().get("TIPOCOPIAR").toString(), 
                            registro.getCampos().get("NUMEROCOPIAR").toString(), 
                            SessionUtil.getUser().toString());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton CopiarDe
     * en la vista
     *
     */
    public void oprimirCopiarDe() {
        //<CODIGO_DESARROLLADO>
        cargarListaTipoACopiar();
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>	
    //<METODOS_SUBFORM>	
    //</METODOS_SUBFORM>	
    //<METODOS_ADICIONALES>

    public boolean validarFormula (String formula) {

        String orden = null;

        formula = formula.toUpperCase();

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));


        Registro registro;
        try {
            registro = RegistroConverter.toRegistro
                            (requestManager.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                            TransaccionModelosControladorUrlEnum.URL2143.getValue()).getUrl(), param));

            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(), "ORDEN")) {
                orden = registro.getCampos().get(GeneralParameterEnum.ORDEN.getName()).toString();

            } else {
                orden = "";
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }


        if (!SysmanFunciones.strValidarFormula(formula, 
                        orden, true)) {
            
            mensaje = idioma.getString("TB_TB4221");
            color = "#FF0000"; 
            return false;
        } else {
            return true;
        }

    }

    public boolean validarInsertUpdate() {

        if(formulaTC && formulaBC && formulaIC && formulaGC) {
            return true;
        } else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4222"));
            return false;
        }
    }
    
    public void cargarNombres() {
        
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanConstantes.CONS_CENTRO);
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), SysmanConstantes.CONS_AUXILIAR);
        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), SysmanConstantes.CONS_REFERENCIA);
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(), SysmanConstantes.CONS_FUENTE);
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), SysmanConstantes.CONS_TERCERO);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
        registro.getCampos().put("TODOSLOSCENTROS", true);
                             
                    //Centro Costo
                    UrlBean urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    TransaccionModelosControladorUrlEnum.URL20
                                                    .getValue());
                    consultarNombre(urlBean, "NOMBRECENTROCOSTO", SysmanConstantes.CONS_CENTRO);
                  //Auxiliar
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    TransaccionModelosControladorUrlEnum.URL23
                                                    .getValue());
                    consultarNombre(urlBean, "NOMBREAUXILIAR", SysmanConstantes.CONS_AUXILIAR);
                    
                  //Fuente Recurso
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    TransaccionModelosControladorUrlEnum.URL34
                                                    .getValue());
                    consultarNombre(urlBean, "NOMBRERECURSO", SysmanConstantes.CONS_FUENTE);
                    
                    //Referencia
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    TransaccionModelosControladorUrlEnum.URL13
                                                    .getValue());
                    consultarNombre(urlBean, "NOMBREREFERENCIA", SysmanConstantes.CONS_REFERENCIA);
                    
                    //Tercero
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    TransaccionModelosControladorUrlEnum.URL14
                                                    .getValue());
                    consultarNombre(urlBean, "NOMBRETERCERO", SysmanConstantes.CONS_TERCERO);
                    
    }
    
    public void consultarNombre(UrlBean urlServ, String campoAsignar, String contante) {
        
        try {

            Map<String, Object> parametrosAux = new HashMap<>();
        
        parametrosAux.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosAux.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosAux.put(GeneralParameterEnum.CODIGO.getName(), contante);
        parametrosAux.put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
        parametrosAux.put("NIT", SysmanConstantes.CONS_TERCERO);
        
        Registro registroAuxiliares;
            registroAuxiliares = RegistroConverter.toRegistro(
                            requestManager.get(urlServ.getUrl(), parametrosAux));
        
        registro.getCampos().put(campoAsignar, registroAuxiliares.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    //</METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        //<CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarListacentroCosto();
        cargarListaAuxiliar();
        cargarListaFuenteRecurso();
        cargarListaReferencia();
        cargarListaTipoGenerar();
        cargarListaConceptosDian();
        cargarListaMediosDePago();
        cargarListaTipoGasto();
        formulaT = false;
        formulaB = false;
        formulaI = false;
        formulaG = false;
        if (accion.equals(ACCION_INSERTAR)) {
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), new Date());
            camposBloqueoActua = false; 
            
            registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanConstantes.CONS_CENTRO);
            registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), SysmanConstantes.CONS_AUXILIAR);
            registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), SysmanConstantes.CONS_REFERENCIA);
            registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(), SysmanConstantes.CONS_FUENTE);
            registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), SysmanConstantes.CONS_TERCERO);
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
            
            registro.getCampos().put("TODOSLOSCENTROS", true);

            
            cargarNombres();
            Map<String, Object> parametrosAux = new HashMap<>();
            
            parametrosAux.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametrosAux.put(GeneralParameterEnum.ANO.getName(), anio);
            parametrosAux.put(GeneralParameterEnum.CODIGO.getName(), SysmanConstantes.CONS_CENTRO);
            
            cargarNombres();
            
        } else if (accion.equals(ACCION_MODIFICAR)){
            
            if ((boolean) registro.getCampos().get("MOVIMIENTO")) {
                camposBloqueoActua = true; 
            } else  {
                camposBloqueoActua = false; 
            }
            
        }
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes(){
        //<CODIGO_DESARROLLADO>



            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

            
        if(!validarInsertUpdate()) {
            return false;
        }
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues(){
        //<CODIGO_DESARROLLADO>
        /*
FR1925-DESPUES_INSERTAR
Private Sub Form_AfterInsert()
   Me.BtnDetalle.Enabled = True
End Sub
         */
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     */
    @Override
    public boolean actualizarAntes(){
        //<CODIGO_DESARROLLADO>
        
        if (!validarInsertUpdate()) {
            return false; 
        }

        registro.getCampos().remove("NOMBREAUXILIAR");
        registro.getCampos().remove("NOMBREREFERENCIA");
        registro.getCampos().remove("NOMBRECENTROCOSTO");
        registro.getCampos().remove("NOMBRERECURSO");
        registro.getCampos().remove("NOMBRETERCERO");
        registro.getCampos().remove("TIPOCOPIAR");
        registro.getCampos().remove("NUMEROCOPIAR");
        registro.getCampos().remove("TIPOGENERARNOMBRE");
        registro.getCampos().remove("NUMEROGENERARNOMBRE");
        registro.getCampos().remove("NOMBRETIPOGASTO");
        registro.getCampos().remove("NOMBREMEDIOPAGO");
        registro.getCampos().remove("NOMBRECONCEPTO");
        
        registro.getCampos().put("VLRIVAFACTURADO", SysmanFunciones.nvl(registro.getCampos().get("VLRIVAFACTURADO"),"").toString().toUpperCase());
        registro.getCampos().put("VLRAGIRAR", SysmanFunciones.nvl(registro.getCampos().get("VLRAGIRAR"),"").toString().toUpperCase());
        registro.getCampos().put("VLRCOMPROBANTE", SysmanFunciones.nvl(registro.getCampos().get("VLRCOMPROBANTE"),"").toString().toUpperCase());
        registro.getCampos().put("VLRBASEGRAVABLE", SysmanFunciones.nvl(registro.getCampos().get("VLRBASEGRAVABLE"),"").toString().toUpperCase());
                
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarAntes(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    //<SET_GET_ATRIBUTOS>
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }
    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en  listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
    /**
     * Retorna la lista listacentroCosto
     * 
     * @return listacentroCosto
     */

    /**
     * Retorna la lista listaNumeroACopiar
     * 
     * @return listaNumeroACopiar
     */
    public RegistroDataModelImpl getListaNumeroACopiar() {
        return listaNumeroACopiar;
    }
    public RegistroDataModelImpl getListacentroCosto() {
        return listacentroCosto;
    }
    public void setListacentroCosto(RegistroDataModelImpl listacentroCosto) {
        this.listacentroCosto = listacentroCosto;
    }
    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }
    public RegistroDataModelImpl getListaFuenteRecurso() {
        return listaFuenteRecurso;
    }
    public void setListaFuenteRecurso(RegistroDataModelImpl listaFuenteRecurso) {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }
    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }
    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }
    public RegistroDataModelImpl getListaTipoACopiar() {
        return listaTipoACopiar;
    }
    public void setListaTipoACopiar(RegistroDataModelImpl listaTipoACopiar) {
        this.listaTipoACopiar = listaTipoACopiar;
    }
    /**
     * Asigna la lista listaNumeroACopiar
     * 
     * @param listaNumeroACopiar
     * Variable a asignar en  listaNumeroACopiar
     */
    public void setListaNumeroACopiar(RegistroDataModelImpl listaNumeroACopiar) {
        this.listaNumeroACopiar = listaNumeroACopiar;
    }
    /**
     * Retorna la lista listaNumeroGenerar
     * 
     * @return listaNumeroGenerar
     */
    public RegistroDataModelImpl getListaNumeroGenerar() {
        return listaNumeroGenerar;
    }
    /**
     * Asigna la lista listaNumeroGenerar
     * 
     * @param listaNumeroGenerar
     * Variable a asignar en  listaNumeroGenerar
     */
    public void setListaNumeroGenerar(RegistroDataModelImpl listaNumeroGenerar) {
        this.listaNumeroGenerar = listaNumeroGenerar;
    }

    //</SET_GET_LISTAS>


    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }
    public List<Registro> getListaAno() {
        return listaAno;
    }
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en  listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }
    public RegistroDataModelImpl getListaTipoGenerar() {
        return listaTipoGenerar;
    }
    public void setListaTipoGenerar(RegistroDataModelImpl listaTipoGenerar) {
        this.listaTipoGenerar = listaTipoGenerar;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public boolean isFormulaT() {
        return formulaT;
    }
    public void setFormulaT(boolean formulaT) {
        this.formulaT = formulaT;
    }
    public boolean isFormulaB() {
        return formulaB;
    }
    public void setFormulaB(boolean formulaB) {
        this.formulaB = formulaB;
    }
    public boolean isFormulaI() {
        return formulaI;
    }
    public void setFormulaI(boolean formulaI) {
        this.formulaI = formulaI;
    }
    public boolean isFormulaG() {
        return formulaG;
    }
    public void setFormulaG(boolean formulaG) {
        this.formulaG = formulaG;
    }
    public boolean isCamposBloqueoActua() {
        return camposBloqueoActua;
    }
    public void setCamposBloqueoActua(boolean camposBloqueoActua) {
        this.camposBloqueoActua = camposBloqueoActua;
    }
    
    public RegistroDataModelImpl getListatipoGasto() {
        return listatipoGasto;
    }
    public void setListatipoGasto(RegistroDataModelImpl listatipoGasto) {
        this.listatipoGasto = listatipoGasto;
    }
    public RegistroDataModelImpl getListaconcepto() {
        return listaconcepto;
    }
    public void setListaconcepto(RegistroDataModelImpl listaconcepto) {
        this.listaconcepto = listaconcepto;
    }
    public RegistroDataModelImpl getListamedioPago() {
        return listamedioPago;
    }
    public void setListamedioPago(RegistroDataModelImpl listamedioPago) {
        this.listamedioPago = listamedioPago;
    }
    

    //</SET_GET_LISTAS_COMBO_GRANDE>
    //<SET_GET_LISTAS_SUBFORM>
    //</SET_GET_LISTAS_SUBFORM>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_ADICIONALES>	
    //</SET_GET_ADICIONALES>
}
