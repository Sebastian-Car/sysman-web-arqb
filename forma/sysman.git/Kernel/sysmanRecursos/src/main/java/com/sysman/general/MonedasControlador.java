/*-
 * MonedasControlador.java
 *
 * 1.0
 * 
 * 22/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.MonedasControladorEnum;
import com.sysman.general.enums.MonedasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * Formularios encargado de ingresar las monedas y sus respectivas tasas de cambio.
 *
 * @version 1.0, 22/01/2018
 * @author jreina
 */


@ManagedBean
@ViewScoped
public class MonedasControlador extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    private final String consMonedaOrigen;
    //<DECLARAR_ATRIBUTOS>
    
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    
    private String moneda;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que presenta las monedas 
     */
    private RegistroDataModelImpl listaMoneda;
    
    /**
     * Lista que presenta las monedas 
     */
    private RegistroDataModelImpl listaMonedaE;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    //<DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que presenta las tasas de cambio para la moneda seleccionada.
     */
    private List<Registro> listaTasasdecambio;
    //</DECLARAR_LISTAS_SUBFORM>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario 
     */
    private Registro registroSub;
    //</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de MonedasControlador
     */
    public MonedasControlador() {
        super();
        compania = SessionUtil.getCompania();
        consMonedaOrigen="MONEDAORIGEN";
        try {
            numFormulario = GeneralCodigoFormaEnum.MONEDA_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } 
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas(){
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaMoneda();
        cargarListaMonedaE();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub(){
        //<CARGAR_LISTAS_SUBFORM>
        cargarListaTasasdecambio();
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
        listaTasasdecambio = null;
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
        enumBase = GenericUrlEnum.MONEDA;
        buscarLlave();
        asignarOrigenDatos();
    }
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }
    /**
     * 
     * Carga la lista listaTasasdecambio
     *
     */
    public void cargarListaTasasdecambio(){
        try {
            
            Map<String, Object> param = new HashMap<>();
            param.put(consMonedaOrigen, registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            listaTasasdecambio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MonedasControladorUrlEnum.URL130
                                                                            .getValue())
                                            .getUrl(), param), CacheUtil.getLlaveServicio(urlConexionCache, "TASADECAMBIO"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    //<METODOS_CARGAR_LISTA>
    
    /**
     * 
     * Carga la lista listaMoneda
     *
     */
    public void cargarListaMoneda() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonedasControladorUrlEnum.URL9281
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(MonedasControladorEnum.MONEDA.getValue(),moneda);

        listaMoneda = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaMoneda
     *
     */
    public void cargarListaMonedaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MonedasControladorUrlEnum.URL9281
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(MonedasControladorEnum.MONEDA.getValue(),moneda);

        listaMonedaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_CAMBIAR>	
    /**
     * Metodo ejecutado al cambiar el control Codigo
     * 
     * 
     */
    public void cambiarCodigo() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Moneda en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarMonedaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaTasasdecambio.get(rowNum).getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroSub.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        // </CODIGO_DESARROLLADO>
    }

    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>	
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMoneda
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMoneda(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("MONEDADESTINO",
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        
        
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMoneda
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMonedaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName());
        registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>	
    //</METODOS_ARBOL>
    //<METODOS_BOTONES>	
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdVariablesLocales
     * en la vista
     *
     *
     */
    public void oprimircmdVariablesLocales() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>	
    //<METODOS_SUBFORM>	
    /**
     * Metodo de insercion del formulario Tasasdecambio
     * 
     */   
    public void agregarRegistroSubTasasdecambio() {
        try {
            registroSub.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            registroSub.getCampos().put(consMonedaOrigen, registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TASADECAMBIO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaTasasdecambio();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }
    /**
     * Metodo de edicion del formulario Tasasdecambio
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubTasasdecambio(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().put(consMonedaOrigen, registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TASADECAMBIO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), null);
            cargarListaTasasdecambio();
        }
    }
    /**
     * Metodo de eliminacion del formulario Tasasdecambio
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubTasasdecambio(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TASADECAMBIO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaTasasdecambio();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * para el subformulario Tasasdecambio
     *
     */
    public void cancelarEdicionTasasdecambio(){
        cargarListaTasasdecambio();
    }
    //</METODOS_SUBFORM>	
    //<METODOS_ADICIONALES>	
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
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (accion.equals(ACCION_MODIFICAR)) {
            moneda = registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString();
            cargarListaMoneda();
            cargarListaMonedaE();
        }

        // </CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    //<SET_GET_ATRIBUTOS>
    
    public String getAuxiliar() {
        return auxiliar;
    }
    
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaMoneda() {
        return listaMoneda;
    }
    

    public void setListaMoneda(RegistroDataModelImpl listaMoneda) {
        this.listaMoneda = listaMoneda;
    }
    
    
    public RegistroDataModelImpl getListaMonedaE() {
        return listaMonedaE;
    }
    
    public void setListaMonedaE(RegistroDataModelImpl listaMonedaE) {
        this.listaMonedaE = listaMonedaE;
    }
    //</SET_GET_LISTAS_COMBO_GRANDE>
    //<SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaTasasdecambio
     * 
     * @return listaTasasdecambio
     */
    public List<Registro> getListaTasasdecambio() {
        return listaTasasdecambio;
    }
   
    /**
     * Asigna la lista listaTasasdecambio
     * 
     * @param listaTasasdecambio
     * Variable a asignar en  listaTasasdecambio
     */
    public void setListaTasasdecambio(List<Registro> listaTasasdecambio) {
        this.listaTasasdecambio = listaTasasdecambio;
    }
    //</SET_GET_LISTAS_SUBFORM>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_ADICIONALES>	
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }
    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    //</SET_GET_ADICIONALES>
}
