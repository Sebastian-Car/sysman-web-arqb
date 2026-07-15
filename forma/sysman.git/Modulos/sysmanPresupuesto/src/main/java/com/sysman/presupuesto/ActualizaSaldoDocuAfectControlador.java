/*-
 * ActualizaSaldoDocuAfectControlador.java
 *
 * 1.0
 * 
 * 3 jul. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroLocal;
import com.sysman.presupuesto.enums.ActualizaSaldoDocuAfectControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite actualizar los saldos de los documentos
 * afectados
 *
 * @version 1.0, 03/07/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ActualizaSaldoDocuAfectControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el numero del comprobante inicial
     */
    private String comprobanteInicial;
    /**
     * Variable que almacena el numero del comprobante final
     */
    private String comprobanteFinal;
    /**
     * Variable que alamcena el codigo del tipo de comprobante
     */
    private String tipoComprobante;
    /**
     * Variable que almacena el anio seleccioando en la vista
     */
    private String anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los comprobantes iniciales
     */
    private RegistroDataModelImpl listaNumeroInicial;
    /**
     * Lista que carga los comprobantes finales
     */
    private RegistroDataModelImpl listaNumeroFinal;
    /**
     * Lista que carga los tipos de comprobante
     */
    private RegistroDataModelImpl listaTipo;

    @EJB
    private EjbPresupuestoCuatroLocal ejbPresupuestoCuatro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ActualizaSaldoDocuAfectControlador
     */
    public ActualizaSaldoDocuAfectControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZA_SALDOS_DOCU_AFECT
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
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizaSaldoDocuAfectControladorUrlEnum.URL4492
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaNumeroInicial
     *
     */
    public void cargarListaNumeroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizaSaldoDocuAfectControladorUrlEnum.URL4745
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TIPO.getName(),
                        String.valueOf(tipoComprobante));
        param.put("ANIO",
                        String.valueOf(anio));

        listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listaNumeroFinal
     *
     */
    public void cargarListaNumeroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizaSaldoDocuAfectControladorUrlEnum.URL6019
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TIPO.getName(),
                        tipoComprobante);
        param.put("ANIO",
                        anio);
        param.put("NUMEROINI", String.valueOf(comprobanteInicial));

        listaNumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listaTipo
     *
     */
    public void cargarListaTipo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizaSaldoDocuAfectControladorUrlEnum.URL7522
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar() {

        try {
            ejbPresupuestoCuatro.congelarSaldosMan(compania,
                            Integer.parseInt(anio), tipoComprobante,
                            new BigInteger(comprobanteInicial),
                            new BigInteger(comprobanteFinal));

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        comprobanteInicial = "";
        comprobanteFinal = "";

        cargarListaNumeroInicial();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobanteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();

        comprobanteFinal = "";

        cargarListaNumeroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        comprobanteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoComprobante = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        comprobanteInicial = "";
        comprobanteFinal = "";

        cargarListaNumeroInicial();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable comprobanteInicial
     * 
     * @return comprobanteInicial
     */
    public String getComprobanteInicial() {
        return comprobanteInicial;
    }

    /**
     * Asigna la variable comprobanteInicial
     * 
     * @param comprobanteInicial
     * Variable a asignar en comprobanteInicial
     */
    public void setComprobanteInicial(String comprobanteInicial) {
        this.comprobanteInicial = comprobanteInicial;
    }

    /**
     * Retorna la variable comprobanteFinal
     * 
     * @return comprobanteFinal
     */
    public String getComprobanteFinal() {
        return comprobanteFinal;
    }

    /**
     * Asigna la variable comprobanteFinal
     * 
     * @param comprobanteFinal
     * Variable a asignar en comprobanteFinal
     */
    public void setComprobanteFinal(String comprobanteFinal) {
        this.comprobanteFinal = comprobanteFinal;
    }

    /**
     * Retorna la variable tipoComprobante
     * 
     * @return tipoComprobante
     */
    public String getTipoComprobante() {
        return tipoComprobante;
    }

    /**
     * Asigna la variable tipoComprobante
     * 
     * @param tipoComprobante
     * Variable a asignar en tipoComprobante
     */
    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNumeroInicial
     * 
     * @return listaNumeroInicial
     */
    public RegistroDataModelImpl getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    /**
     * Asigna la lista listaNumeroInicial
     * 
     * @param listaNumeroInicial
     * Variable a asignar en listaNumeroInicial
     */
    public void setListaNumeroInicial(
        RegistroDataModelImpl listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    /**
     * Retorna la lista listaNumeroFinal
     * 
     * @return listaNumeroFinal
     */
    public RegistroDataModelImpl getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    /**
     * Asigna la lista listaNumeroFinal
     * 
     * @param listaNumeroFinal
     * Variable a asignar en listaNumeroFinal
     */
    public void setListaNumeroFinal(RegistroDataModelImpl listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

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
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
