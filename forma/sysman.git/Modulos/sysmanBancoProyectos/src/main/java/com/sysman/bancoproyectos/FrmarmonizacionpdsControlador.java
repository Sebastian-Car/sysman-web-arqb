/*-
 * FrmarmonizacionpdsControlador.java
 *
 * 1.0
 * 
 * 13/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmarmonizacionpdsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar las Armonizaciones
 *
 * @version 1.0, 13/03/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class FrmarmonizacionpdsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar los datos del plan indicativo
     */
    private RegistroDataModelImpl listaidMeta;
    /**
     * Lista encargada de almacenar los datos del plan indicativo
     */
    private RegistroDataModelImpl listaidMetaE;
    /**
     * Lista encargada de almacenar los datos de los rubros
     */
    private RegistroDataModelImpl listacmbRubro;
    /**
     * Lista encargada de almacenar los datos del plan indicativo
     */
    private RegistroDataModelImpl listacmbRubroE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Variable encargada de almacenar el año gubernamental
     */
    private String ano;

    /**
     * Varibale encargada de almacenar el nombre del plan indicativo
     */
    private String nombreIdMeta;
    /**
     * Varibale encargada de almacenar la descripcion del rubro
     */
    private String nombreRubro;

    /**
     * Variable encargada de almacenar la fuente del rubro
     */
    private String fuente;
    /**
     * Variable encargada de almacenar el centro costo del rubro
     */
    private String centroCosto;
    /**
     * Variable encargada de almacenar la referencia del rubro
     */
    private String referencia;
    /**
     * Variable encargada de almacenar la auxiliarGeneral del rubro
     */
    private String auxiliarGeneral;

    /**
     * Constante encargada de almacenar el String ID_PLAN
     */
    private final String idPlanCons;
    /**
     * Constante encargada de almacenar el String RUBRO
     */
    private final String rubroCons;
    /**
     * Constante encargada de almacenar el String DESCRIPCION_PLAN_IND
     */
    private final String descripcionPlanIdCons;
    /**
     * Constante encargada de almacenar el String DESCRIPCION_RUBRO
     */
    private final String descripcionRubroCons;
    /**
     * Constante encargada de almacenar el String DESCRIPCION
     */
    private final String descripcionCons;
    
    private final String modulo;
    

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmarmonizacionpdsControlador
     */

    public FrmarmonizacionpdsControlador() {
        super();
        compania = SessionUtil.getCompania();
        idPlanCons = "ID_PLAN";
        rubroCons = "RUBRO";
        descripcionPlanIdCons = "DESCRIPCION_PLAN_IND";
        descripcionRubroCons = "DESCRIPCION_RUBRO";
        descripcionCons = "DESCRIPCION";
        
        modulo = SessionUtil.getModulo();
        
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            ano = (String) parametros.get("vigencia");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMARMONIZACIONPDS_CONTROLADOR
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

        enumBase = GenericUrlEnum.BP_ARMONIZACIONPD;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaidMeta();
        cargarListaidMetaE();
        cargarListacmbRubro();
        cargarListacmbRubroE();
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

        // registro.getCampos().put("FUENTE", fuente);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaidMeta
     *
     */
    public void cargarListaidMeta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmarmonizacionpdsControladorUrlEnum.URL3222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaidMeta = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "BP_PLAN_INDICATIVO_METAS"));
        }
        catch (SysmanException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 433016
    }

    /**
     * 
     * Carga la lista listaidMeta
     *
     */
    public void cargarListaidMetaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmarmonizacionpdsControladorUrlEnum.URL3222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaidMetaE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "BP_PLAN_INDICATIVO_METAS"));
        }
        catch (SysmanException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listacmbRubro
     *
     */
    public void cargarListacmbRubro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmarmonizacionpdsControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        String[] vec = { "RUBRO", "VIGENCIA", "FUENTE_RECURSO", "CENTRO_COSTO", "REFERENCIA", "AUXILIAR" };

        listacmbRubro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vec);
    }

    /**
     * 
     * Carga la lista listacmbRubro
     *
     */
    public void cargarListacmbRubroE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmarmonizacionpdsControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        String[] vec = { "RUBRO", "VIGENCIA", "FUENTE_RECURSO", "CENTRO_COSTO", "REFERENCIA", "AUXILIAR" };

        listacmbRubroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vec);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiaridMetaC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put("ID_META",
                        auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(descripcionPlanIdCons, nombreIdMeta);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbRubro en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbRubroC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("ID_RUBRO", auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(descripcionRubroCons, nombreRubro);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("FUENTE", fuente);        
        
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        				.put("CENTROCOSTO", centroCosto); 
        
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        				.put("REFERENCIA", referencia); 
        
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
        				.put("AUXILIARGENERAL", auxiliarGeneral); 
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Abre el formulario modal de carga de plantilla.
     * Envia el modulo como parametro mediante flash para su uso en el formulario hijo.
     * 
     * Propiedad en el HTML: BT4495
     */
    public void oprimirCargarPlantilla() {
        String[] campos = { "PR_MODULO" };
        Object[] valores = { modulo };

        SessionUtil.cargarModalDatosFlashCerrar(
            Integer.toString(
                GeneralCodigoFormaEnum.FRM_ARMONIZACION_PD_CARGA_CONTROLADOR
                    .getCodigo()
            ),
            modulo,
            campos,
            valores
        );
    }
    
    /**
     * Se ejecuta al cerrar el modal de carga.
     * Recarga la lista inicial para actualizar la informacion en el formulario padre.
     */
    public void retornarFormularioCargarPlantilla(SelectEvent event) {
        listaInicial.load();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidMeta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaidMeta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_META",
                        registroAux.getCampos().get(idPlanCons));

        registro.getCampos().put(descripcionPlanIdCons,
                        registroAux.getCampos().get(descripcionCons));

        registro.getCampos().put("VIGENCIA_PLAN",
                        registroAux.getCampos().get("VIGENCIA_PLAN"));

        registro.getCampos().put("VIGENCIA_META",
                        registroAux.getCampos().get("VIGENCIA_META"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidMeta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaidMetaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, idPlanCons);

        nombreIdMeta = retornarString(registroAux, descripcionCons);

        registro.getCampos().put("VIGENCIA_PLAN",
                        registroAux.getCampos().get("VIGENCIA_PLAN"));

        registro.getCampos().put("VIGENCIA_META",
                        registroAux.getCampos().get("VIGENCIA_META"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbRubro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbRubro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_RUBRO",
                        registroAux.getCampos().get(rubroCons));

        registro.getCampos().put("FUENTE",
                        registroAux.getCampos().get("FUENTE_RECURSO"));

        registro.getCampos().put(descripcionRubroCons,
                        registroAux.getCampos().get(descripcionCons));
        
        registro.getCampos().put("CENTROCOSTO", 
        				registroAux.getCampos().get("CENTRO_COSTO"));
        
        registro.getCampos().put("REFERENCIA", 
        				registroAux.getCampos().get("REFERENCIA"));
        
        registro.getCampos().put("AUXILIARGENERAL", 
        				registroAux.getCampos().get("AUXILIAR"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbRubro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbRubroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, rubroCons);
        nombreRubro = retornarString(registroAux, descripcionCons);

        fuente = SysmanFunciones.nvl(registroAux.getCampos().get("FUENTE_RECURSO"), "").toString();

        registro.getCampos().put("FUENTE", fuente);
        
        registro.getCampos().put("CENTROCOSTO", 
        		registroAux.getCampos().get("CENTRO_COSTO"));
        
        registro.getCampos().put("REFERENCIA", 
        		registroAux.getCampos().get("REFERENCIA"));
        
        registro.getCampos().put("AUXILIARGENERAL", 
        		registroAux.getCampos().get("AUXILIAR"));
        
        centroCosto = registroAux.getCampos().get("CENTRO_COSTO").toString();
        		
        referencia = registroAux.getCampos().get("REFERENCIA").toString();
        
        auxiliarGeneral = registroAux.getCampos().get("AUXILIAR").toString();

        // registro.getCampos().put("FUENTE",
        // registroAux.getCampos().get("FUENTE_RECURSO"));
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
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // registro.getCampos().put("VIGENCIA_PLAN", ano);
        // registro.getCampos().put("VIGENCIA_META", ano);
        registro.getCampos().put("VIGENCIA_RUBRO", ano);
//        registro.getCampos().put("AUXILIARGENERAL", auxiliarGeneral);
        registro.getCampos().remove("AUXILIAR");
        registro.getCampos().remove("DESCRIPCION_PLAN_IND");
        registro.getCampos().remove("DESCRIPCION_RUBRO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
    	listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(descripcionPlanIdCons);
        registro.getCampos().remove(descripcionRubroCons);
//        registro.getCampos().put("AUXILIARGENERAL", auxiliarGeneral);
        // </CODIGO_DESARROLLADO>
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
    	listaInicial.load();
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
    	registro.getCampos().put("AUXILIARGENERAL", auxiliarGeneral);
        registro.getCampos().remove("AUXILIAR");
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
        //

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("VIGENCIA_PLAN");
        registro.getCampos().remove("VIGENCIA_META");
        registro.getCampos().remove("VIGENCIA_RUBRO");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaidMeta
     * 
     * @return listaidMeta
     */
    public RegistroDataModelImpl getListaidMeta() {
        return listaidMeta;
    }

    /**
     * Asigna la lista listaidMeta
     * 
     * @param listaidMeta
     * Variable a asignar en listaidMeta
     */
    public void setListaidMeta(RegistroDataModelImpl listaidMeta) {
        this.listaidMeta = listaidMeta;
    }

    /**
     * Retorna la lista listaidMeta
     * 
     * @return listaidMeta
     */
    public RegistroDataModelImpl getListaidMetaE() {
        return listaidMetaE;
    }

    /**
     * Asigna la lista listaidMeta
     * 
     * @param listaidMeta
     * Variable a asignar en listaidMeta
     */
    public void setListaidMetaE(RegistroDataModelImpl listaidMetaE) {
        this.listaidMetaE = listaidMetaE;
    }

    /**
     * Retorna la lista listacmbRubro
     * 
     * @return listacmbRubro
     */
    public RegistroDataModelImpl getListacmbRubro() {
        return listacmbRubro;
    }

    /**
     * Asigna la lista listacmbRubro
     * 
     * @param listacmbRubro
     * Variable a asignar en listacmbRubro
     */
    public void setListacmbRubro(RegistroDataModelImpl listacmbRubro) {
        this.listacmbRubro = listacmbRubro;
    }

    /**
     * Retorna la lista listacmbRubro
     * 
     * @return listacmbRubro
     */
    public RegistroDataModelImpl getListacmbRubroE() {
        return listacmbRubroE;
    }

    /**
     * Asigna la lista listacmbRubro
     * 
     * @param listacmbRubro
     * Variable a asignar en listacmbRubro
     */
    public void setListacmbRubroE(RegistroDataModelImpl listacmbRubroE) {
        this.listacmbRubroE = listacmbRubroE;
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
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
    
    

}
