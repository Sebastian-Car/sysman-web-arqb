/*-
 * FrmpremiacionsControlador.java
 *
 * 1.0
 * 
 * 03/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.FrmpremiacionsControladorEnum;
import com.sysman.hojasdevida.enums.FrmpremiacionsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Migracion del formulario access FRM_PREMIACION a web controlador
 * FrmpremiacionsControlador forma frmpremiacion.xhtml creacion de
 * menu para abrir el formulario continuo, creacion de properties para
 * el formulario continuo.
 * 
 *
 * 
 * @version 1.0, 03/02/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class FrmpremiacionsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que obtiene la calificacion del combo correspondiente
     */
    private String calificacion;
    /**
     * Variable que obtiene el beneficiario del combo correspondiente
     */
    private boolean beneficiario;
    /**
     * Variable que obtiene la sucursal del combo correspondiente
     */
    private String sucursal;
    /**
     * Variable que obtiene el tipo evento del combo correspondiente
     */
    private String tipoEvento;
    /**
     * Variable que obtiene el id del evento del combo correspondiente
     */
    private String idEvento;

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
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listacmbTipoEvento;
    /**
     */
    private RegistroDataModelImpl listacmbTipoEventoE;
    /**
     */
    private RegistroDataModelImpl listacmbEvento;
    /**
     */
    private RegistroDataModelImpl listacmbEventoE;
    /**
     */
    private RegistroDataModelImpl listacmbAsistente;
    /**
     */
    private RegistroDataModelImpl listacmbAsistenteE;
    /**
     */
    private RegistroDataModelImpl listacmbPremio;
    /**
     */
    private RegistroDataModelImpl listacmbPremioE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmpremiacionsControlador
     */
    public FrmpremiacionsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            // 1688
            numFormulario = GeneralCodigoFormaEnum.FRM_PREMIACIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
        enumBase = GenericUrlEnum.NAT_PREMIACION;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbTipoEvento();
        cargarListacmbTipoEventoE();
        cargarListacmbPremio();
        cargarListacmbPremioE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <MES_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbTipoEvento
     *
     */
    public void cargarListacmbTipoEvento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL185
                                                        .getValue());

        listacmbTipoEvento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacmbTipoEvento
     *
     */
    public void cargarListacmbTipoEventoE()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL185
                                                        .getValue());

        listacmbTipoEventoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacmbEvento
     *
     */
    public void cargarListacmbEvento()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("TIPO_EVENTO", registro.getCampos()
                        .get(FrmpremiacionsControladorEnum.TIPOEVENTO
                                        .getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL227
                                                        .getValue());

        listacmbEvento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmpremiacionsControladorEnum.IDEVENTO.getValue());

    }

    /**
     * 
     * Carga la lista listacmbEvento
     *
     */
    public void cargarListacmbEventoE()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPO_EVENTO", tipoEvento);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL227
                                                        .getValue());

        listacmbEventoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmpremiacionsControladorEnum.IDEVENTO.getValue());

    }

    /**
     * 
     * Carga la lista listacmbAsistente
     *
     */
    public void cargarListacmbAsistente()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL281
                                                        .getValue());

        param.put(FrmpremiacionsControladorEnum.IDEVENTO.getValue(),
                        registro.getCampos()
                                        .get(FrmpremiacionsControladorEnum.IDEVENTO
                                                        .getValue()));

        listacmbAsistente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());

    }

    /**
     * 
     * Carga la lista listacmbAsistente
     *
     */
    public void cargarListacmbAsistenteE()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmpremiacionsControladorEnum.IDEVENTO.getValue(), idEvento);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL281
                                                        .getValue());

        listacmbAsistenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    /**
     * 
     * Carga la lista listacmbPremio
     *
     */
    public void cargarListacmbPremio()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL330
                                                        .getValue());

        listacmbPremio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmpremiacionsControladorEnum.ID_TIPO_PREMIO
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listacmbPremio
     *
     */
    public void cargarListacmbPremioE()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmpremiacionsControladorUrlEnum.URL330
                                                        .getValue());

        listacmbPremioE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmpremiacionsControladorEnum.ID_TIPO_PREMIO
                                        .getValue());
    }

    // </MES_CARGAR_LISTA>
    // <MES_BOTONES>
    // </MES_BOTONES>
    // <MES_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbAsistente en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbAsistenteC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmpremiacionsControladorEnum.BENEFICIARIO
                                        .getValue(), beneficiario);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmpremiacionsControladorEnum.CALIFICACION
                                        .getValue(),
                                        calificacion);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbTipoEvento en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbTipoEventoC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>

        tipoEvento = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(FrmpremiacionsControladorEnum.TIPOEVENTO
                                        .getValue())
                        .toString();

        cargarListacmbEventoE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        indice = registro.getIndice();
        idEvento = registro.getCampos()
                        .get(FrmpremiacionsControladorEnum.IDEVENTO.getValue())
                        .toString();
        tipoEvento = registro.getCampos().get("TIPOEVENTO").toString();
        cargarListacmbAsistenteE();
        cargarListacmbEventoE();
    }

    /**
     * Metodo ejecutado al cambiar el control cmbEvento en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbEventoC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        idEvento = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(FrmpremiacionsControladorEnum.IDEVENTO.getValue())
                        .toString();
        cargarListacmbAsistenteE();
        // </CODIGO_DESARROLLADO>
    }

    // </MES_CAMBIAR>
    // <MES_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbTipoEvento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoEvento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOEVENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put("NOMBRE_TIPOEVENTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.IDEVENTO.getValue(), "");

        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        "");

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.CALIFICACION.getValue(),
                        "");

        listacmbEvento = null;
        listacmbAsistente = null;

        cargarListacmbEvento();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbTipoEvento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoEventoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        listacmbEventoE = null;
        listacmbAsistenteE = null;
        cargarListacmbEventoE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbEvento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbEvento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.IDEVENTO.getValue(),
                        registroAux.getCampos()
                                        .get(FrmpremiacionsControladorEnum.IDEVENTO
                                                        .getValue()));

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.EVENTO.getValue(),
                        registroAux.getCampos().get("NOMBREEVENTO"));

        cargarListacmbAsistente();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbEvento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbEventoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmpremiacionsControladorEnum.IDEVENTO
                                                        .getValue())
                                        .toString(),
                                        "")
                        .toString();

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.IDEVENTO.getValue(),
                        registroAux.getCampos()
                                        .get(FrmpremiacionsControladorEnum.IDEVENTO
                                                        .getValue()));

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.EVENTO.getValue(),
                        registroAux.getCampos().get("NOMBREEVENTO"));
        cargarListacmbAsistenteE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbAsistente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbAsistente(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.ASISTENTE.getValue(),
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.BENEFICIARIO.getValue(),
                        registroAux.getCampos().get(
                                        FrmpremiacionsControladorEnum.BENEFICIARIO
                                                        .getValue()));
        registro.getCampos().put(
                        FrmpremiacionsControladorEnum.CALIFICACION.getValue(),
                        registroAux.getCampos()
                                        .get(FrmpremiacionsControladorEnum.CALIFICACION
                                                        .getValue()));

        sucursal = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbAsistente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbAsistenteE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName())
                                        .toString(),
                        "").toString();

        beneficiario = (boolean) registroAux.getCampos()
                        .get(FrmpremiacionsControladorEnum.BENEFICIARIO
                                        .getValue());

        calificacion = registroAux.getCampos().get("CALIFICACION").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbPremio
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPremio(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PREMIO",
                        registroAux.getCampos()
                                        .get(FrmpremiacionsControladorEnum.ID_TIPO_PREMIO
                                                        .getValue()));

        registro.getCampos().put("NOMBRE_PREMIO",
                        registroAux.getCampos()
                                        .get(FrmpremiacionsControladorEnum.NOMBRE_TIPO_PREMIO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbPremio
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPremioE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmpremiacionsControladorEnum.ID_TIPO_PREMIO
                                        .getValue()),
                        "").toString();
    }

    // </MES_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        registro.getCampos().remove("NOMBRE_TIPOEVENTO");
        registro.getCampos().remove("ASISTENTE");
        registro.getCampos().remove("EVENTO");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return VARIABLE
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE_TIPOEVENTO");
        registro.getCampos().remove("EVENTO");
        registro.getCampos().remove("ASISTENTE");
        registro.getCampos().remove("NOMBRE_PREMIO");

        Registro test = registro;
        test.getCampos();

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return VARIABLE
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     * @return VARIABLE
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     * @return VARIABLE
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        listaInicial.load();
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("NOMBRE_TIPOEVENTO");
        registro.getCampos().remove("ASISTENTE");
        registro.getCampos().remove("EVENTO");
        registro.getCampos().remove("NOMBRE_PREMIO");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbTipoEvento
     * 
     * @return listacmbTipoEvento
     */
    public RegistroDataModelImpl getListacmbTipoEvento()
    {
        return listacmbTipoEvento;
    }

    /**
     * Asigna la lista listacmbTipoEvento
     * 
     * @param listacmbTipoEvento
     * Variable a asignar en listacmbTipoEvento
     */
    public void setListacmbTipoEvento(
        RegistroDataModelImpl listacmbTipoEvento)
    {
        this.listacmbTipoEvento = listacmbTipoEvento;
    }

    /**
     * Retorna la lista listacmbTipoEvento
     * 
     * @return listacmbTipoEvento
     */
    public RegistroDataModelImpl getListacmbTipoEventoE()
    {
        return listacmbTipoEventoE;
    }

    /**
     * Asigna la lista listacmbTipoEvento
     * 
     * @param listacmbTipoEvento
     * Variable a asignar en listacmbTipoEvento
     */
    public void setListacmbTipoEventoE(
        RegistroDataModelImpl listacmbTipoEventoE)
    {
        this.listacmbTipoEventoE = listacmbTipoEventoE;
    }

    /**
     * Retorna la lista listacmbEvento
     * 
     * @return listacmbEvento
     */
    public RegistroDataModelImpl getListacmbEvento()
    {
        return listacmbEvento;
    }

    /**
     * Asigna la lista listacmbEvento
     * 
     * @param listacmbEvento
     * Variable a asignar en listacmbEvento
     */
    public void setListacmbEvento(RegistroDataModelImpl listacmbEvento)
    {
        this.listacmbEvento = listacmbEvento;
    }

    /**
     * Retorna la lista listacmbEvento
     * 
     * @return listacmbEvento
     */
    public RegistroDataModelImpl getListacmbEventoE()
    {
        return listacmbEventoE;
    }

    /**
     * Asigna la lista listacmbEvento
     * 
     * @param listacmbEvento
     * Variable a asignar en listacmbEvento
     */
    public void setListacmbEventoE(RegistroDataModelImpl listacmbEventoE)
    {
        this.listacmbEventoE = listacmbEventoE;
    }

    /**
     * Retorna la lista listacmbAsistente
     * 
     * @return listacmbAsistente
     */
    public RegistroDataModelImpl getListacmbAsistente()
    {
        return listacmbAsistente;
    }

    /**
     * Asigna la lista listacmbAsistente
     * 
     * @param listacmbAsistente
     * Variable a asignar en listacmbAsistente
     */
    public void setListacmbAsistente(RegistroDataModelImpl listacmbAsistente)
    {
        this.listacmbAsistente = listacmbAsistente;
    }

    /**
     * Retorna la lista listacmbAsistente
     * 
     * @return listacmbAsistente
     */
    public RegistroDataModelImpl getListacmbAsistenteE()
    {
        return listacmbAsistenteE;
    }

    /**
     * Asigna la lista listacmbAsistente
     * 
     * @param listacmbAsistente
     * Variable a asignar en listacmbAsistente
     */
    public void setListacmbAsistenteE(
        RegistroDataModelImpl listacmbAsistenteE)
    {
        this.listacmbAsistenteE = listacmbAsistenteE;
    }

    /**
     * Retorna la lista listacmbPremio
     * 
     * @return listacmbPremio
     */
    public RegistroDataModelImpl getListacmbPremio()
    {
        return listacmbPremio;
    }

    /**
     * Asigna la lista listacmbPremio
     * 
     * @param listacmbPremio
     * Variable a asignar en listacmbPremio
     */
    public void setListacmbPremio(RegistroDataModelImpl listacmbPremio)
    {
        this.listacmbPremio = listacmbPremio;
    }

    /**
     * Retorna la lista listacmbPremio
     * 
     * @return listacmbPremio
     */
    public RegistroDataModelImpl getListacmbPremioE()
    {
        return listacmbPremioE;
    }

    /**
     * Asigna la lista listacmbPremio
     * 
     * @param listacmbPremio
     * Variable a asignar en listacmbPremio
     */
    public void setListacmbPremioE(RegistroDataModelImpl listacmbPremioE)
    {
        this.listacmbPremioE = listacmbPremioE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice()
    {
        return indice;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        //
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

}
