/*-
 * EstructuraComponenteControlador.java
 *
 * 1.0
 * 
 * 30/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import com.sysman.almacen.enums.EstructuraComponenteControladorEnum;
import com.sysman.almacen.enums.EstructuraComponenteControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.DepartamentosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * Permite congiurar los elementos que estan conformados por componentes.
 *
 * @version 1.0, 30/08/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  EstructuraComponenteControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    //<DECLARAR_ATRIBUTOS>
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * carga la lista de elementos del inventario por devolutivo
     */
    private RegistroDataModelImpl listaCodigoComponente;
    /**
     *  carga la lista de elementos del inventario por devolutivo
     */
    private RegistroDataModelImpl listaCodigoComponenteE;


    private RegistroDataModelImpl listatipoComponente;

    private RegistroDataModelImpl listatipoComponenteE;

    private List<Registro> listapais;
    private List<Registro> listadepartamento;
    private List<Registro> listamunicipio;
    
    private int indice;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;

    private String pais;
    private String departamento;
    @EJB
    private EjbSysmanUtil ejbSysmanUtil;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EstructuraComponenteControlador
     */
    public EstructuraComponenteControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ESTRUCTURA_COMPONENTE_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
        enumBase = GenericUrlEnum.ELEMENTOSCOMPONENTES;
        buscarLlave();
        reasignarOrigen();		    
        registro = new Registro();
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoComponente(); 
        cargarListaCodigoComponenteE();
        cargarListatipoComponente();
        cargarListapais(); 
        cargarListadepartamento(); 
         cargarListamunicipio(); 
        abrirFormulario();
        //</CARGAR_LISTA_COMBO_GRANDE>


    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoComponente
     *
     *  muestra la lista de elementos del inventario por devolutivo
     */
    public void cargarListaCodigoComponente(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstructuraComponenteControladorUrlEnum.URL3238
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        EstructuraComponenteControladorEnum.PARAM0.getValue());

    }
    /**
     * 
     * Carga la lista listaCodigoComponente
     *
     *  muestra la lista de elementos del inventario por devolutivo
     */
    public void  cargarListaCodigoComponenteE(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstructuraComponenteControladorUrlEnum.URL3238
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoComponenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        EstructuraComponenteControladorEnum.PARAM0.getValue());
    }

    /**
     * 
     * Carga la lista listatipoComponente
     *
     */
    public void cargarListatipoComponente(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstructuraComponenteControladorUrlEnum.URL5684
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listatipoComponente
     *
     */
    public void  cargarListatipoComponenteE(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstructuraComponenteControladorUrlEnum.URL5684
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoComponenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listapais
     *
     */
    public void cargarListapais(){

        try {
            listapais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstructuraComponenteControladorUrlEnum.URL5685
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    
    public void cambiarpaisC(int rowNum) {
        pais =  listaInicial.getDatasource().get(rowNum % 10).getCampos().get("PAIS").toString();
        cargarListadepartamento();
        cargarListamunicipio();
    }

    public void cambiardepartamentoC(int rowNum) {

        departamento =  listaInicial.getDatasource().get(rowNum % 10).getCampos().get("DEPARTAMENTO").toString();
        cargarListamunicipio();
    }

    public void cambiarmunicipioC(int rowNum) {

    }
   
    /**
     * 
     * Carga la lista listadepartamento
     *
     */
    public void cargarListadepartamento(){

        try {
            
            HashMap<String, Object> param = new HashMap<>();
            
            param.put("PAIS", pais);
            
            listadepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstructuraComponenteControladorUrlEnum.URL5686
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
     * Carga la lista listamunicipio
     *
     */
    public void cargarListamunicipio(){

        try {
            
            HashMap<String, Object> param = new HashMap<>();
            
            param.put("PAIS", pais);
            param.put("DEPARTAMENTO", departamento);
            listamunicipio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstructuraComponenteControladorUrlEnum.URL5687
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CodigoComponente en la fila
     * seleccionada dentro de la grilla
     * 
     * se atualiza campo nombre al momento de cambiar codigo
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoComponenteC(int rowNum) {

        //<CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("CODIGO", 
                        registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE", 
                        registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        
        //</CODIGO_DESARROLLADO>
    }
    
    public void cambiarpais() {
        
      //<CODIGO_DESARROLLADO>
        pais = registro.getCampos().get("PAIS").toString();
        cargarListadepartamento();
        //</CODIGO_DESARROLLADO>
    }
    
    
    public void cambiardepartamento() {

      //<CODIGO_DESARROLLADO>
        departamento = registro.getCampos().get("DEPARTAMENTO").toString();
        cargarListamunicipio();
        //</CODIGO_DESARROLLADO>
    }
    
    
    public void cambiarmunicipio() {
       
      //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
       
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoComponente
     *
     * Pemite cargar los valores al momento de seleccionar el codigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoComponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(EstructuraComponenteControladorEnum.PARAM0.getValue()).toString();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), registroAux.getCampos().get(EstructuraComponenteControladorEnum.PARAM0.getValue()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroAux.getCampos().get("NOMBRELARGO"));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoComponente
     *
     * Pemite cargar los valores al momento de seleccionar el codigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoComponenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  registroAux.getCampos().get(EstructuraComponenteControladorEnum.PARAM0.getValue()).toString();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), registroAux.getCampos().get(EstructuraComponenteControladorEnum.PARAM0.getValue()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroAux.getCampos().get("NOMBRELARGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoComponente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoComponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOCOMPONENTE", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("TIPONOMBRE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoComponente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoComponenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  (String) registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().put("TIPOCOMPONENTE", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("TIPONOMBRE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listapais
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilapais(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PAIS", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBREPAIS", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        cargarListadepartamento();
        cargarListamunicipio();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadepartamento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladepartamento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPARTAMENTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("DEPARTAMENTONOMBRE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        cargarListamunicipio();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listamunicipio
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilamunicipio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CIUDAD", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("CIUDADNOMBRE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listamunicipio
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilamunicipioE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  (String) registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().put("CIUDAD", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("CIUDADNOMBRE", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        
    }

    //</METODOS_COMBOS_GRANDES>
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
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
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
    public boolean insertarAntes(){
        //<CODIGO_DESARROLLADO>
        String criterio = SysmanFunciones.concatenar(" COMPANIA = ''", compania, "''");

       
        
        try {
            registro.getCampos();
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), 
                            ejbSysmanUtil.generarConsecutivoConValorInicial(GenericUrlEnum.ELEMENTOSCOMPONENTES.getTable(), 
                                            criterio, 
                                            GeneralParameterEnum.CONSECUTIVO.getName(), "1"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
        registro.getCampos().remove("DEPARTAMENTONOMBRE");
        registro.getCampos().remove("CIUDADNOMBRE");
        registro.getCampos().remove("TIPONOMBRE");
        registro.getCampos().remove("NOMBREPAIS");
        
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
        registro.getCampos();
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
    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        listaInicial.getDatasource().get(indice).getCampos();
        pais = registro.getCampos().get("PAIS").toString();
        departamento = registro.getCampos().get("DEPARTAMENTO").toString();
        cargarListapais();
        cargarListadepartamento();
        cargarListamunicipio();
        
    }
    
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
    //<CODIGO_DESARROLLADO>
    //</CODIGO_DESARROLLADO>
    @Override
    public void asignarValoresRegistro()
    {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //<SET_GET_ATRIBUTOS>
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCodigoComponente
     * 
     * @return listaCodigoComponente
     */
    public RegistroDataModelImpl getListaCodigoComponente() {
        return listaCodigoComponente;
    }
    /**
     * Asigna la lista listaCodigoComponente
     * 
     * @param listaCodigoComponente
     * Variable a asignar en  listaCodigoComponente
     */
    public void setListaCodigoComponente(RegistroDataModelImpl listaCodigoComponente) {
        this.listaCodigoComponente = listaCodigoComponente;
    }
    /**
     * Retorna la lista listaCodigoComponente
     * 
     * @return listaCodigoComponente
     */
    public RegistroDataModelImpl getListaCodigoComponenteE() {
        return listaCodigoComponenteE;
    }
    /**
     * Asigna la lista listaCodigoComponente
     * 
     * @param listaCodigoComponente
     * Variable a asignar en  listaCodigoComponente
     */
    public void setListaCodigoComponenteE(RegistroDataModelImpl listaCodigoComponenteE) {
        this.listaCodigoComponenteE = listaCodigoComponenteE;
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
        this.auxiliar= auxiliar;
    }
    public RegistroDataModelImpl getListatipoComponente() {
        return listatipoComponente;
    }
    public void setListatipoComponente(RegistroDataModelImpl listatipoComponente) {
        this.listatipoComponente = listatipoComponente;
    }
    public RegistroDataModelImpl getListatipoComponenteE() {
        return listatipoComponenteE;
    }
    public int getIndice() {
        return indice;
    }
    public void setIndice(int indice) {
        this.indice = indice;
    }
    public void setListatipoComponenteE(
        RegistroDataModelImpl listatipoComponenteE) {
        this.listatipoComponenteE = listatipoComponenteE;
    }
    public List<Registro> getListapais() {
        return listapais;
    }
    public void setListapais(List<Registro> listapais) {
        this.listapais = listapais;
    }
    public List<Registro> getListadepartamento() {
        return listadepartamento;
    }
    public void setListadepartamento(List<Registro> listadepartamento) {
        this.listadepartamento = listadepartamento;
    }
    public List<Registro> getListamunicipio() {
        return listamunicipio;
    }
    public void setListamunicipio(List<Registro> listamunicipio) {
        this.listamunicipio = listamunicipio;
    }


    //</SET_GET_LISTAS_COMBO_GRANDE>
}
