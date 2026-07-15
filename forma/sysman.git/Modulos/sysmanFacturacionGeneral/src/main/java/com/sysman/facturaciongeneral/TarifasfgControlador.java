/*-
 * TarifasfgControlador.java
 *
 * 1.0
 * 
 * 14/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.TarifasfgControladorEnum;
import com.sysman.facturaciongeneral.enums.TarifasfgControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase permite la creación de Tarifas asociadas a un Concepto,
 * el cual va ligado al año que se selecciona.
 *
 * @version 1.0, 14/11/2017
 * @author dnino
 * 
 * 
 * * @version 2.0, 20/04/2018
 * @author dnino
 * 
 * Se actualiza para mostrar el nombre del concepto y se corrigen
 * inconsistencias en los tipos de letra.
 */
@ManagedBean
@ViewScoped
public class TarifasfgControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Tipo de cobro seleccionado en el modal
     * "Seleccionar tipo de cobro"
     */
    private String tipoCobro;
    /**
     * Año seleccionado en el combo.
     */
    private int ano;
    /**
     * Concepto Relacionado al Año seleccionado
     */
    private String concRelacionado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que visualiza los años.
     */
    private List<Registro> listaANO;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Carga la lista de Conceptos relacionados al año seleccionado
     * (lista no visible actualmente - oculta en el formulario)
     */
    private RegistroDataModelImpl listaConcRelacionado;
    /**
     * Carga la lista de Conceptos relacionados al año seleccionado
     * (lista no visible actualmente - oculta en el formulario)
     */
    private RegistroDataModelImpl listaConcRelacionadoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String nombreConcepto;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TarifasfgControlador
     */
    public TarifasfgControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = SysmanFunciones.toString(SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()));
        try {
            numFormulario = GeneralCodigoFormaEnum.TARIFAS_FG_CONTROLADOR
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
        enumBase = GenericUrlEnum.SF_TARIFA;
        ano = SysmanFunciones.ano(new Date());
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaANO();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        parametrosListado.put(TarifasfgControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaANO
     *
     */
    public void cargarListaANO() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaANO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasfgControladorUrlEnum.URL6170
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
     * Carga la lista listaConcRelacionado (lista no visible
     * actualmente - oculta en el formulario)
     *
     */
    public void cargarListaConcRelacionado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TarifasfgControladorUrlEnum.URL4507
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(TarifasfgControladorEnum.TIPOCOBRO.getValue(), tipoCobro);

        listaConcRelacionado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ANO
     *
     * 
     */
    public void cambiarANO() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcRelacionado (lista no visible actualmente - oculta en
     * el formulario)
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcRelacionado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        concRelacionado = SysmanFunciones.toString(SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), ""));
        nombreConcepto = SysmanFunciones.toString(SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), ""));
        reasignarOrigen();

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
     * @return Verdadero si permite insertar registros antes.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(TarifasfgControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Verdadero si permite insertar después.
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
     * @return Verdadero si permite actualizar antes.
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
     * @return Verdadero si permite actualizar después.
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
     * @return verdadero si permite eliminar antes.
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
     * @return Verdadero si permite eliminar después.
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos()
                        .remove(TarifasfgControladorEnum.TIPOCOBRO.getValue());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // No se requiere agregar valores al registro.
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * @return the concRelacionado
     */
    public String getConcRelacionado() {
        return concRelacionado;
    }

    /**
     * @param concRelacionado
     * the concRelacionado to set
     */
    public void setConcRelacionado(String concRelacionado) {
        this.concRelacionado = concRelacionado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaANO
     * 
     * @return listaANO
     */
    public List<Registro> getListaANO() {
        return listaANO;
    }

    /**
     * Asigna la lista listaANO
     * 
     * @param listaANO
     * Variable a asignar en listaANO
     */
    public void setListaANO(List<Registro> listaANO) {
        this.listaANO = listaANO;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConcRelacionado
     * 
     * @return listaConcRelacionado
     */
    public RegistroDataModelImpl getListaConcRelacionado() {
        return listaConcRelacionado;
    }

    /**
     * Asigna la lista listaConcRelacionado
     * 
     * @param listaConcRelacionado
     * Variable a asignar en listaConcRelacionado
     */
    public void setListaConcRelacionado(
        RegistroDataModelImpl listaConcRelacionado) {
        this.listaConcRelacionado = listaConcRelacionado;
    }

    /**
     * Retorna la lista listaConcRelacionado
     * 
     * @return listaConcRelacionado
     */
    public RegistroDataModelImpl getListaConcRelacionadoE() {
        return listaConcRelacionadoE;
    }

    /**
     * Asigna la lista listaConcRelacionado
     * 
     * @param listaConcRelacionado
     * Variable a asignar en listaConcRelacionado
     */
    public void setListaConcRelacionadoE(
        RegistroDataModelImpl listaConcRelacionadoE) {
        this.listaConcRelacionadoE = listaConcRelacionadoE;
    }

    /**
     * @return the nombreConcepto
     */
    public String getNombreConcepto() {
        return nombreConcepto;
    }

    /**
     * @param nombreConcepto
     * the nombreConcepto to set
     */
    public void setNombreConcepto(String nombreConcepto) {
        this.nombreConcepto = nombreConcepto;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
