/*-
 * FrmequivalenciasControlador.java
 *
 * 1.0
 * 
 * 07/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.RetencionsControlador;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroGeneralRemote;
import com.sysman.plandesarrollo.enums.FrmequivalenciasControladorEnum;
import com.sysman.plandesarrollo.enums.FrmequivalenciasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * 
 * Se migro el formulario frmequivalencias de la versión de access
 * SysmanDES2018.01.01 con la forma <code>frmequivalencias</code>.
 *
 * @version 1.0, 07/05/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class FrmequivalenciasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo que contiene la vigencia inicial
     */

    private String vigencia;

    /**
     * Atributo que contiene el digMetaProd que se toma como parametro
     * de entrada
     */

    private int digMetaProd;

    /**
     * Atributo que contiene el codigo del subproyecto
     */

    private String codigoSubProyecto;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atirbuto que almacena el valor del ano seleccionado en el combo
     * de vigencia meta
     */
    private String anoVigencia;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo vigenciaMeta
     * (CB5918).
     */
    private List<Registro> listavigenciaMeta;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo SubProyecto (CB3447).
     */
    private RegistroDataModelImpl listaSubProyecto;
    /**
     * Lista que contiene los detalles del combo SubProyecto (CB3447).
     */
    private RegistroDataModelImpl listaSubProyectoE;
    /**
     * Lista que contiene los detalles del combo Rubro (CB5868).
     */
    private RegistroDataModelImpl listaRubro;
    /**
     * Lista que contiene los detalles del combo Rubro (CB5868).
     */
    private RegistroDataModelImpl listaRubroE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>.
     */

    @EJB
    private EjbPlanDesarrolloCeroGeneralRemote ejbPlanDesarrollo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmequivalenciasControlador
     */
    public FrmequivalenciasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1053
            numFormulario = GeneralCodigoFormaEnum.FRMEQUIVALENCIAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>

           Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

       /*         vigencia = String.valueOf(parametrosEntrada
                                .get(FrmequivalenciasControladorEnum.VIGENCIA
                                                .getValue()));  */
                
                vigencia = parametrosEntrada.get("vigencia").toString();
                digMetaProd = (int) parametrosEntrada.get("digMetaProd"); 

            } 
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

        enumBase = GenericUrlEnum.BP_EQUIVALENCIAS;

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        cargarListavigenciaMeta();
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

        parametrosListado.put(FrmequivalenciasControladorEnum.VIGENCIA_INICIAL
                        .getValue(), vigencia);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * /** Carga la lista: <code>listavigenciaMeta</code> asociada al
     * combo estado (CB5918).
     */

    public void cargarListavigenciaMeta() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmequivalenciasControladorEnum.VIGENCIA_PLAN.getValue(),
                        vigencia);

        try {
            listavigenciaMeta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmequivalenciasControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(RetencionsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista: <code>listaSubProyecto</code> asociada al combo
     * estado (CB3447).
     */
    public void cargarListaSubProyecto() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmequivalenciasControladorEnum.VIGENCIA.getValue()
                        .toUpperCase(),
                        anoVigencia);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmequivalenciasControladorUrlEnum.URL0001
                                                        .getValue());

        listaSubProyecto = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmequivalenciasControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista: <code>listaSubProyectoE</code> asociada al
     * combo estado (CB3447).
     */
    public void cargarListaSubProyectoE() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmequivalenciasControladorEnum.VIGENCIA.getValue()
                        .toUpperCase(),
                        anoVigencia);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmequivalenciasControladorUrlEnum.URL0001
                                                        .getValue());

        listaSubProyectoE = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmequivalenciasControladorEnum.ID.getValue());

    }

    /**
     * 
     ** Carga la lista: <code>listaRubro</code> asociada al combo
     * estado (CB5868).
     */
    public void cargarListaRubro() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(FrmequivalenciasControladorEnum.NATURALEZA.getValue(), "D");

        param.put(FrmequivalenciasControladorEnum.ANO.getValue(), anoVigencia);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmequivalenciasControladorUrlEnum.URL0002
                                                        .getValue());

        listaRubro = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmequivalenciasControladorEnum.CODIGO.getValue());

    }

    /**
     * Carga la lista: <code>listaRubroE</code> asociada al combo
     * estado (CB5868).
     */
    public void cargarListaRubroE() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(FrmequivalenciasControladorEnum.NATURALEZA.getValue(), "D");

        param.put(FrmequivalenciasControladorEnum.ANO.getValue(), anoVigencia);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmequivalenciasControladorUrlEnum.URL0002
                                                        .getValue());

        listaRubroE = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmequivalenciasControladorEnum.CODIGO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control vigenciaMeta
     * 
     * 
     */
    public void cambiarvigenciaMeta() {
        // <CODIGO_DESARROLLADO>
        anoVigencia = registro.getCampos()
                        .get(FrmequivalenciasControladorEnum.VIGENCIA_META
                                        .getValue())
                        .toString();
        registro.getCampos().put(GeneralParameterEnum.RUBRO.getName(), null);
        registro.getCampos().put(
                        FrmequivalenciasControladorEnum.SUBPROYECTO.getValue(),
                        null);
        cargarListaRubro();
        cargarListaSubProyecto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtPorcentaje
     * 
     * 
     */
    public void cambiartxtPorcentaje() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * txtPorcentajeObligaciones
     */
    public void cambiartxtPorcentajeObligaciones() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control vigenciaMeta en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarvigenciaMetaC(int rowNum) {

        anoVigencia = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(FrmequivalenciasControladorEnum.VIGENCIA_META
                                        .getValue())
                        .toString();
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.RUBRO.getName(), null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        FrmequivalenciasControladorEnum.SUBPROYECTO.getValue(),
                        null);
        cargarListaRubroE();
        cargarListaSubProyectoE();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control SubProyecto en la fila
     * seleccionada dentro de la grilla.
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarSubProyectoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.CODIGO.getName(),
                        codigoSubProyecto);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubProyecto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSubProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SUBPROYECTO",
                        registroAux.getCampos().get("ID"));
        registro.getCampos().put(
                        FrmequivalenciasControladorEnum.CODIGO.getValue(),
                        registroAux.getCampos()
                                        .get(FrmequivalenciasControladorEnum.CODIGO
                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubProyecto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSubProyectoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("ID").toString();

        codigoSubProyecto = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaRubro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBRO",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaRubro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("CODIGO").toString();
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
        anoVigencia = vigencia;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(
                        GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos()
                        .put(FrmequivalenciasControladorEnum.VIGENCIA_INICIAL
                                        .getValue(), vigencia);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMSUBPROYECTO");
        registro.getCampos().remove("NOMBRERUBRO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     *
     * 
     * @return true
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
     *
     * 
     * @return true
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
     * 
     * @return true
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
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("VIGENCIA_INICIAL");

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = registro.getIndice();
        anoVigencia = String.valueOf(registro.getCampos()
                        .get(FrmequivalenciasControladorEnum.VIGENCIA_META
                                        .getValue()));
        cargarListaRubroE();
        cargarListaSubProyectoE();
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listavigenciaMeta
     * 
     * @return listavigenciaMeta
     */
    public List<Registro> getListavigenciaMeta() {
        return listavigenciaMeta;
    }

    /**
     * Asigna la lista listavigenciaMeta
     * 
     * @param listavigenciaMeta
     * Variable a asignar en listavigenciaMeta
     */
    public void setListavigenciaMeta(List<Registro> listavigenciaMeta) {
        this.listavigenciaMeta = listavigenciaMeta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaSubProyecto
     * 
     * @return listaSubProyecto
     */
    public RegistroDataModelImpl getListaSubProyecto() {
        return listaSubProyecto;
    }

    /**
     * Asigna la lista listaSubProyecto
     * 
     * @param listaSubProyecto
     * Variable a asignar en listaSubProyecto
     */
    public void setListaSubProyecto(RegistroDataModelImpl listaSubProyecto) {
        this.listaSubProyecto = listaSubProyecto;
    }

    /**
     * Retorna la lista listaSubProyecto
     * 
     * @return listaSubProyecto
     */
    public RegistroDataModelImpl getListaSubProyectoE() {
        return listaSubProyectoE;
    }

    /**
     * Asigna la lista listaSubProyecto
     * 
     * @param listaSubProyecto
     * Variable a asignar en listaSubProyecto
     */
    public void setListaSubProyectoE(RegistroDataModelImpl listaSubProyectoE) {
        this.listaSubProyectoE = listaSubProyectoE;
    }

    /**
     * Retorna la lista listaRubro
     * 
     * @return listaRubro
     */
    public RegistroDataModelImpl getListaRubro() {
        return listaRubro;
    }

    /**
     * Asigna la lista listaRubro
     * 
     * @param listaRubro
     * Variable a asignar en listaRubro
     */
    public void setListaRubro(RegistroDataModelImpl listaRubro) {
        this.listaRubro = listaRubro;
    }

    /**
     * Retorna la lista listaRubro
     * 
     * @return listaRubro
     */
    public RegistroDataModelImpl getListaRubroE() {
        return listaRubroE;
    }

    /**
     * Asigna la lista listaRubro
     * 
     * @param listaRubro
     * Variable a asignar en listaRubro
     */
    public void setListaRubroE(RegistroDataModelImpl listaRubroE) {
        this.listaRubroE = listaRubroE;
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
