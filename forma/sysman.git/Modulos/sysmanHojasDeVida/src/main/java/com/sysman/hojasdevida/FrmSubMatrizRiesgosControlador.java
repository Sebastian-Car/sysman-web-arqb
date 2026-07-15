/*-
 * FrmSubMatrizRiesgosControlador.java
 *
 * 1.0
 * 
 * 02/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.FrmSubMatrizRiesgosControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite gestionar los detalles de la matriz de
 * riesgos.
 *
 * @version 1.0, 02/10/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class FrmSubMatrizRiesgosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consCodigo;

    private final String consGrupo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listacmbRiesgo;
    private RegistroDataModelImpl listacmbRiesgoE;

    private RegistroDataModelImpl listacmbGrupo;
    private RegistroDataModelImpl listacmbGrupoE;

    private RegistroDataModelImpl listatxtProceso;
    private RegistroDataModelImpl listatxtProcesoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private String version;

    private Map<String, Object> ridDatos;
    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmSubMatrizRiesgosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmSubMatrizRiesgosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigo = "CODIGO_FACTOR";
        consGrupo = "GRUPO";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_SUBMATRIZRIESGOS_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
                version = (String) parametrosEntrada.get("version");
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
        enumBase = GenericUrlEnum.SST_DETALLE_MATRIZ_RIESGOS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbRiesgo();
        cargarListacmbRiesgoE();
        cargarListacmbGrupo();
        cargarListacmbGrupoE();
        cargarListatxtProceso();
        cargarListatxtProcesoE();
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
        parametrosListado.put("VERSION",
                        version);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listacmbRiesgo
     *
     */
    public void cargarListacmbRiesgo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubMatrizRiesgosControladorUrlEnum.URL6229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(consGrupo,
                        registro.getCampos().get(consGrupo));

        listacmbRiesgo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);

    }

    /**
     * 
     * Carga la lista listacmbRiesgo
     *
     */
    public void cargarListacmbRiesgoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubMatrizRiesgosControladorUrlEnum.URL6229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(consGrupo,
                        auxiliar);

        listacmbRiesgoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);

    }

    /**
     * 
     * Carga la lista listacmbGrupo
     *
     */
    public void cargarListacmbGrupo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubMatrizRiesgosControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbGrupo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consGrupo);

    }

    /**
     * 
     * Carga la lista listacmbGrupo
     *
     */
    public void cargarListacmbGrupoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubMatrizRiesgosControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbGrupoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consGrupo);

    }

    /**
     * Carga la lista listatxtProceso
     *
     */
    public void cargarListatxtProceso() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubMatrizRiesgosControladorUrlEnum.URL4786
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listatxtProceso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listatxtProceso
     *
     */
    public void cargarListatxtProcesoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSubMatrizRiesgosControladorUrlEnum.URL4786
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listatxtProcesoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control cmbGrupo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbGrupoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbRiesgo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbRiesgo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FACTOR_RIESGO",
                        registroAux.getCampos().get(consCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbRiesgo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbRiesgoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(consCodigo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbGrupo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbGrupo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consGrupo,
                        registroAux.getCampos().get(consGrupo));
        cargarListacmbRiesgo();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbGrupo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbGrupoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(consGrupo);
        cargarListacmbRiesgoE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatxtProceso
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatxtProceso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PROCESO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put("ZONA_LUGAR",
                        registroAux.getCampos().get(
                                        "ZONA_LUGAR"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatxtProceso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatxtProcesoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("VERSION", version);

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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRE_FACTOR");
        registro.getCampos().remove("ZONA_LUGAR");
        registro.getCampos().remove("NOMBRE_PROCESO");
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
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        auxiliar= listaInicial.getDatasource().get(indice).getCampos().get("GRUPO").toString();
        cargarListacmbRiesgoE();
    }

    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FRM_MATRIZRIESGOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbRiesgo
     * 
     * @return listacmbRiesgo
     */
    public RegistroDataModelImpl getListacmbRiesgo() {
        return listacmbRiesgo;
    }

    /**
     * Asigna la lista listacmbRiesgo
     * 
     * @param listacmbRiesgo
     * Variable a asignar en listacmbRiesgo
     */
    public void setListacmbRiesgo(RegistroDataModelImpl listacmbRiesgo) {
        this.listacmbRiesgo = listacmbRiesgo;
    }

    /**
     * Retorna la lista listacmbRiesgo
     * 
     * @return listacmbRiesgo
     */
    public RegistroDataModelImpl getListacmbRiesgoE() {
        return listacmbRiesgoE;
    }

    /**
     * Asigna la lista listacmbRiesgo
     * 
     * @param listacmbRiesgo
     * Variable a asignar en listacmbRiesgo
     */
    public void setListacmbRiesgoE(RegistroDataModelImpl listacmbRiesgoE) {
        this.listacmbRiesgoE = listacmbRiesgoE;
    }

    /**
     * Retorna la lista listacmbGrupo
     * 
     * @return listacmbGrupo
     */
    public RegistroDataModelImpl getListacmbGrupo() {
        return listacmbGrupo;
    }

    /**
     * Asigna la lista listacmbGrupo
     * 
     * @param listacmbGrupo
     * Variable a asignar en listacmbGrupo
     */
    public void setListacmbGrupo(RegistroDataModelImpl listacmbGrupo) {
        this.listacmbGrupo = listacmbGrupo;
    }

    /**
     * Retorna la lista listacmbGrupo
     * 
     * @return listacmbGrupo
     */
    public RegistroDataModelImpl getListacmbGrupoE() {
        return listacmbGrupoE;
    }

    /**
     * Asigna la lista listacmbGrupo
     * 
     * @param listacmbGrupo
     * Variable a asignar en listacmbGrupo
     */
    public void setListacmbGrupoE(RegistroDataModelImpl listacmbGrupoE) {
        this.listacmbGrupoE = listacmbGrupoE;
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

    public RegistroDataModelImpl getListatxtProceso() {
        return listatxtProceso;
    }

    public void setListatxtProceso(RegistroDataModelImpl listatxtProceso) {
        this.listatxtProceso = listatxtProceso;
    }

    public RegistroDataModelImpl getListatxtProcesoE() {
        return listatxtProcesoE;
    }

    public void setListatxtProcesoE(RegistroDataModelImpl listatxtProcesoE) {
        this.listatxtProcesoE = listatxtProcesoE;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
    
    

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
