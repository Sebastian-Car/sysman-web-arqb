/*-
 * RetencionesPorTerceroControlador.java
 *
 * 1.0
 * 
 * 19/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.RetencionesPorTerceroControladorEnum;
import com.sysman.contabilidad.enums.RetencionesPorTerceroControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;
/**
 * Clase que permite configurar las retenciones por tercero
 *
 * @version 1.0, 19/06/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  RetencionesPorTerceroControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String sucursal;
    //<DECLARAR_ATRIBUTOS>
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    /**
     * Variable registra listaAno
     */
    private List<Registro> listaAno;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaTerceroE;
    private RegistroDataModelImpl listaTipoRetencion;
    private RegistroDataModelImpl listaTipoRetencionE;
    private RegistroDataModelImpl listaCodigoRetencion;
    private RegistroDataModelImpl listaCodigoRetencionE;
    /**
     * Esta variable se usa como auxiliar para 
     * subformularios y en esta se alamcena el
     * identificador del registro que se selecciono
     */
    private String auxiliar;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RetencionesPorTerceroControlador
     */
    public RetencionesPorTerceroControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
        	//1827
            numFormulario=GeneralCodigoFormaEnum.FRM_RETENCIONESPORTERCERO_CONTROLADOR.getCodigo();
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
        enumBase = GenericUrlEnum.TERCERO_RETENCIONES;
        reasignarOrigen();              
        buscarLlave();
        registro= new Registro();
        //<CARGAR_LISTA>
        cargarListaAno();
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero(); 
        cargarListaTerceroE();
        cargarListaTipoRetencion(); 
        cargarListaTipoRetencionE();
        cargarListaCodigoRetencion(); 
        cargarListaCodigoRetencionE();
        //</CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno(){
        try
        {
            HashMap<String, Object> parametros = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL7530.getValue()).getUrl(), parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero(){

        HashMap<String, Object> parametros = new HashMap<>();

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL8420.getValue());

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
                        "NIT");

    }
    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void  cargarListaTerceroE(){

        HashMap<String, Object> parametros = new HashMap<>();

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL8420.getValue());

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
                        "NIT");
    }
    /**
     * 
     * Carga la lista listaTipoRetencion
     *
     */
    public void cargarListaTipoRetencion()
    {

        HashMap<String, Object> parametros = new HashMap<>();

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL8421.getValue());

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoRetencion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
                        GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaTipoRetencion
     *
     */
    public void  cargarListaTipoRetencionE(){

        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put("ANO", registro.getCampos().get("ANO"));
        // "TIPO_RETENCION"
        parametros.put("TIPO", registro.getCampos().get(RetencionesPorTerceroControladorEnum.PARAM0.getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL8421.getValue());

        listaTipoRetencionE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
                        GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaCodigoRetencion
     *
     */
    public void cargarListaCodigoRetencion(){

        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put("ANO", registro.getCampos().get("ANO"));
        parametros.put("TIPO", registro.getCampos().get(RetencionesPorTerceroControladorEnum.PARAM0.getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL8425.getValue());

        listaCodigoRetencion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
                        GeneralParameterEnum.CODIGO.getName());

    }
    /**
     * 
     * Carga la lista listaCodigoRetencion
     *
     */
    public void  cargarListaCodigoRetencionE(){

        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put("ANO", registro.getCampos().get("ANO"));
        parametros.put("TIPO", registro.getCampos().get(RetencionesPorTerceroControladorEnum.PARAM0.getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetencionesPorTerceroControladorUrlEnum.URL8425.getValue());

        listaCodigoRetencionE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), parametros, true,
                        GeneralParameterEnum.CODIGO.getName());

    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        sucursal = registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString();
        registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * Metodo ejecutado al cambiar el control TipoRetencion en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoRetencionC(int rowNum) {
        //<CODIGO_DESARROLLADO>
    	// "TIPO_RETENCION"
        registro.getCampos().put(RetencionesPorTerceroControladorEnum.PARAM0.getValue(), SysmanFunciones.nvl(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get("TIPO_RETENCION"), " ").toString());

        registro.getCampos().put("ANO", SysmanFunciones.nvl(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos()
                        .get("ANO"), " ").toString());
        //"NOMBRETIPO"
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(RetencionesPorTerceroControladorEnum.PARAM2.getValue(), registro.getCampos().get(RetencionesPorTerceroControladorEnum.PARAM2.getValue()));

        cargarListaCodigoRetencion();
        cargarListaCodigoRetencionE();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control CodigoRetencion en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */     
    public void cambiarCodigoRetencionC(int rowNum) {
        //<CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(RetencionesPorTerceroControladorEnum.PARAM3.getValue(), registro.getCampos().get(RetencionesPorTerceroControladorEnum.PARAM3.getValue()));
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("CODIGO_RETENCION"));
        //</CODIGO_DESARROLLADO>
    }

    public void cambiarTerceroC(int rowNum) {
        //<CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        //</CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        //<CODIGO_DESARROLLADO>
        registro.getCampos();

        //</CODIGO_DESARROLLADO>
    }


    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT").toString();
        sucursal = registroAux.getCampos().get("SUCURSAL").toString();
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRetencion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRetencion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_RETENCION", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(RetencionesPorTerceroControladorEnum.PARAM2.getValue(), registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        cargarListaCodigoRetencion();




    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRetencion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRetencionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  (String) registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().put("NOMBRETIPO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
        cargarListaCodigoRetencionE();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRetencion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRetencion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(RetencionesPorTerceroControladorEnum.PARAM3.getValue(), SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()),"").toString());
        registro.getCampos().put("CODIGO_RETENCION", SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()),"").toString());

    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRetencion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRetencionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar =  (String) registroAux.getCampos().get("CODIGO");
        registro.getCampos().put("NOMBRECODIGO", SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"),"").toString());
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        registro.getCampos().put("SUCURSAL", sucursal);
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

        registro.getCampos().remove("NOMBRETIPO");
        registro.getCampos().remove("CODIGO");
        registro.getCampos().remove("NOMBRECODIGO");
        registro.getCampos().remove("NOMBRE");

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
        registro.getCampos();
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
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
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
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }
    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en  listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
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
    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en  listaTercero
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
     * Variable a asignar en  listaTercero
     */
    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
    }
    /**
     * Retorna la lista listaTipoRetencion
     * 
     * @return listaTipoRetencion
     */
    public RegistroDataModelImpl getListaTipoRetencion() {
        return listaTipoRetencion;
    }
    /**
     * Asigna la lista listaTipoRetencion
     * 
     * @param listaTipoRetencion
     * Variable a asignar en  listaTipoRetencion
     */
    public void setListaTipoRetencion(RegistroDataModelImpl listaTipoRetencion) {
        this.listaTipoRetencion = listaTipoRetencion;
    }
    /**
     * Retorna la lista listaTipoRetencion
     * 
     * @return listaTipoRetencion
     */
    public RegistroDataModelImpl getListaTipoRetencionE() {
        return listaTipoRetencionE;
    }
    /**
     * Asigna la lista listaTipoRetencion
     * 
     * @param listaTipoRetencion
     * Variable a asignar en  listaTipoRetencion
     */
    public void setListaTipoRetencionE(RegistroDataModelImpl listaTipoRetencionE) {
        this.listaTipoRetencionE = listaTipoRetencionE;
    }
    /**
     * Retorna la lista listaCodigoRetencion
     * 
     * @return listaCodigoRetencion
     */
    public RegistroDataModelImpl getListaCodigoRetencion() {
        return listaCodigoRetencion;
    }
    /**
     * Asigna la lista listaCodigoRetencion
     * 
     * @param listaCodigoRetencion
     * Variable a asignar en  listaCodigoRetencion
     */
    public void setListaCodigoRetencion(RegistroDataModelImpl listaCodigoRetencion) {
        this.listaCodigoRetencion = listaCodigoRetencion;
    }
    /**
     * Retorna la lista listaCodigoRetencion
     * 
     * @return listaCodigoRetencion
     */
    public RegistroDataModelImpl getListaCodigoRetencionE() {
        return listaCodigoRetencionE;
    }
    /**
     * Asigna la lista listaCodigoRetencion
     * 
     * @param listaCodigoRetencion
     * Variable a asignar en  listaCodigoRetencion
     */
    public void setListaCodigoRetencionE(RegistroDataModelImpl listaCodigoRetencionE) {
        this.listaCodigoRetencionE = listaCodigoRetencionE;
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
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
