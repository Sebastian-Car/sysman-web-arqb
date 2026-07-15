/*-
 * FrmCodificacionGlosasControlador.java
 *
 * 1.0
 * 
 * 22/01/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmCodificacionGlosasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite administrar la codificaicón de glosas
 *
 * @version 1.0, 22/01/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmCodificacionGlosasControlador extends BeanBaseContinuoAcmeImpl {
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
     * Lista que carga los codigos generales de glosas
     */
    private RegistroDataModelImpl listaCodigoGeneral;
    /**
     * Lista que carga los codigos generales de glosas en la grilla
     */
    private RegistroDataModelImpl listaCodigoGeneralE;
    /**
     * Lista que carga los codigos especiales de glosas
     */
    private RegistroDataModelImpl listaCodigoEspecial;
    /**
     * Lista que carga los codigos especiales de glosas en la grilla
     */
    private RegistroDataModelImpl listaCodigoEspecialE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCodificacionGlosasControlador
     */
    public FrmCodificacionGlosasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            // 2151
            numFormulario = GeneralCodigoFormaEnum.FRM_CODIFICACION_GLOSAS_CONTROLADOR
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
        enumBase = GenericUrlEnum.CM_CODIFICACION_GLOSAS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoGeneral();
        cargarListaCodigoGeneralE();
        cargarListaCodigoEspecial();
        cargarListaCodigoEspecialE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        Object idRegistro = registro.getCampos().get("ID");
        if(idRegistro == null && registro.getCampos().get("CODACTIVO") == null) {
        	registro.getCampos().put("CODACTIVO", true);
        }
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
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigoGeneral
     *
     */
    public void cargarListaCodigoGeneral() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCodificacionGlosasControladorUrlEnum.URL4476
                                                        .getValue());

        listaCodigoGeneral = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoGeneral
     *
     */
    public void cargarListaCodigoGeneralE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCodificacionGlosasControladorUrlEnum.URL4864
                                                        .getValue());
        listaCodigoGeneralE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoEspecial
     *
     */
    public void cargarListaCodigoEspecial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCodificacionGlosasControladorUrlEnum.URL5254
                                                        .getValue());
        listaCodigoEspecial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoEspecial
     *
     */
    public void cargarListaCodigoEspecialE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCodificacionGlosasControladorUrlEnum.URL5641
                                                        .getValue());
        listaCodigoEspecialE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CodigoGeneral en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoGeneralC(int rowNum) {

        listaInicial.getDatasource().get(rowNum
            % 10).getCampos().put("ID", auxiliar
                +
                SysmanFunciones.nvl(listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos()
                                .get(GeneralParameterEnum.CODIGO_ESPECIAL
                                                .getName()),
                                "")
                                .toString() + SysmanFunciones
            					.nvl(listaInicial.getDatasource().get(rowNum % 10)
            							.getCampos()
            							.get("CONCEPTO_APLICACION"),
            							"")
            					.toString());

    }

    /**
     * Metodo ejecutado al cambiar el control CodigoEspecial en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoEspecialC(int rowNum) {

        listaInicial.getDatasource().get(rowNum
            % 10).getCampos().put("ID", SysmanFunciones
                            .nvl(listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(GeneralParameterEnum.CODIGO_GENERAL
                                                            .getName()),
                                            "")
                            .toString()
                + auxiliar + SysmanFunciones
				.nvl(listaInicial.getDatasource().get(rowNum % 10)
						.getCampos()
						.get("CONCEPTO_APLICACION"),
						"")
				.toString());

    }
    
    /**
     * Metodo ejecutado al cambiar el control CONCEPTO_APLICACION
     * 
     * 
     */
    public void cambiarCodigoAplicacion() {
    	//<CODIGO_DESARROLLADO>
    	registro.getCampos().put("ID", SysmanFunciones
    			.nvl(registro.getCampos()
    					.get(GeneralParameterEnum.CODIGO_GENERAL
    							.getName()),
    					"")
    			.toString()
    			+
    			SysmanFunciones.nvl(registro.getCampos().get(
    					GeneralParameterEnum.CODIGO_ESPECIAL.getName()), "")
    			.toString()
    			+ SysmanFunciones.nvl(registro.getCampos().get("CONCEPTO_APLICACION"), "")
    			.toString());
    	//</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control CONCEPTO_APLICACION en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoAplicacionC(int rowNum) {
    	//<CODIGO_DESARROLLADO>
    	listaInicial.getDatasource().get(rowNum
    			% 10).getCampos().put("ID", SysmanFunciones
    					.nvl(listaInicial.getDatasource().get(rowNum % 10)
    							.getCampos()
    							.get(GeneralParameterEnum.CODIGO_GENERAL
    									.getName()),
    							"")
    					.toString()
    					+ SysmanFunciones
    					.nvl(listaInicial.getDatasource().get(rowNum % 10)
    							.getCampos()
    							.get(GeneralParameterEnum.CODIGO_ESPECIAL
    									.getName()),
    							"")
    					.toString()
    					+ SysmanFunciones
    					.nvl(listaInicial.getDatasource().get(rowNum % 10)
    							.getCampos()
    							.get("CONCEPTO_APLICACION"),
    							"")
    					.toString());
    	//</CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoGeneral
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGeneral(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGO_GENERAL.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put("ID", SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO_GENERAL
                                                        .getName()),
                                        "")
                        .toString()
            +
            SysmanFunciones.nvl(registro.getCampos().get(
                            GeneralParameterEnum.CODIGO_ESPECIAL.getName()), "")
                            .toString()
                            + SysmanFunciones.nvl(registro.getCampos().get("CONCEPTO_APLICACION"), "")
                			.toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoGeneral
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoGeneralE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoEspecial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoEspecial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGO_ESPECIAL.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put("ID", SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO_GENERAL
                                                        .getName()),
                                        "")
                        .toString()
            +
            SysmanFunciones.nvl(registro.getCampos().get(
                            GeneralParameterEnum.CODIGO_ESPECIAL.getName()), "")
                            .toString()
                            + SysmanFunciones.nvl(registro.getCampos().get("CONCEPTO_APLICACION"), "")
                			.toString());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoEspecial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoEspecialE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
    	registro.getCampos().put("CODACTIVO", true);
        // </CODIGO_DESARROLLADO>
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaCodigoGeneral
     * 
     * @return listaCodigoGeneral
     */
    public RegistroDataModelImpl getListaCodigoGeneral() {
        return listaCodigoGeneral;
    }

    /**
     * Asigna la lista listaCodigoGeneral
     * 
     * @param listaCodigoGeneral
     * Variable a asignar en listaCodigoGeneral
     */
    public void setListaCodigoGeneral(
        RegistroDataModelImpl listaCodigoGeneral) {
        this.listaCodigoGeneral = listaCodigoGeneral;
    }

    /**
     * Retorna la lista listaCodigoGeneral
     * 
     * @return listaCodigoGeneral
     */
    public RegistroDataModelImpl getListaCodigoGeneralE() {
        return listaCodigoGeneralE;
    }

    /**
     * Asigna la lista listaCodigoGeneral
     * 
     * @param listaCodigoGeneral
     * Variable a asignar en listaCodigoGeneral
     */
    public void setListaCodigoGeneralE(
        RegistroDataModelImpl listaCodigoGeneralE) {
        this.listaCodigoGeneralE = listaCodigoGeneralE;
    }

    /**
     * Retorna la lista listaCodigoEspecial
     * 
     * @return listaCodigoEspecial
     */
    public RegistroDataModelImpl getListaCodigoEspecial() {
        return listaCodigoEspecial;
    }

    /**
     * Asigna la lista listaCodigoEspecial
     * 
     * @param listaCodigoEspecial
     * Variable a asignar en listaCodigoEspecial
     */
    public void setListaCodigoEspecial(
        RegistroDataModelImpl listaCodigoEspecial) {
        this.listaCodigoEspecial = listaCodigoEspecial;
    }

    /**
     * Retorna la lista listaCodigoEspecial
     * 
     * @return listaCodigoEspecial
     */
    public RegistroDataModelImpl getListaCodigoEspecialE() {
        return listaCodigoEspecialE;
    }

    /**
     * Asigna la lista listaCodigoEspecial
     * 
     * @param listaCodigoEspecial
     * Variable a asignar en listaCodigoEspecial
     */
    public void setListaCodigoEspecialE(
        RegistroDataModelImpl listaCodigoEspecialE) {
        this.listaCodigoEspecialE = listaCodigoEspecialE;
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