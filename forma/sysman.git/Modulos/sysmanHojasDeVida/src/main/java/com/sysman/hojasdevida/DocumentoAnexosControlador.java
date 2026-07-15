/*-
 * DocumentoAnexosControlador.java
 *
 * 1.0
 * 
 * 02/02/2018
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
import com.sysman.hojasdevida.enums.DocumentoAnexosControladorEnum;
import com.sysman.hojasdevida.enums.DocumentoAnexosControladorUrlEnum;
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
 * Clase migrada para administrar los documentos anexos segun el tipo
 * de transaccion seleccionada
 *
 * @version 1.0, 02/02/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class DocumentoAnexosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el tipo de transaccion recibido por
     * parametro
     */
    private String tipoTransaccion;
    /**
     * Atributo que almacena el nombre del tipo de transaccion
     * recibida por parametro
     */
    private String nombreTransaccion;
    /**
     * Atributo que valida el nombre de la transaccion a visualizar
     */
    private String tituloDocumento;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los tipo de documentos de anexo
     */
    private RegistroDataModelImpl listaTipoDocumento;
    /**
     * Lista de registros de los tipo de documentos de anexo
     */
    private RegistroDataModelImpl listaTipoDocumentoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DocumentoAnexosControlador
     */
    public DocumentoAnexosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1686
            numFormulario = GeneralCodigoFormaEnum.DOCUMENTO_ANEXO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                tipoTransaccion = parametrosEntrada
                                .get("tipoTransaccion").toString();
                nombreTransaccion = parametrosEntrada.get("nombreTransaccion")
                                .toString();
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

        enumBase = GenericUrlEnum.SST_DOCUMENTO_ANEXO;
        buscarLlave();
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), true);
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoDocumento();
        cargarListaTipoDocumentoE();
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
        parametrosListado.put(DocumentoAnexosControladorEnum.TIPO_TRANSACCION
                        .getValue(), tipoTransaccion);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoDocumento
     *
     */
    public void cargarListaTipoDocumento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DocumentoAnexosControladorUrlEnum.URL152
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTipoDocumento
     *
     */
    public void cargarListaTipoDocumentoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DocumentoAnexosControladorUrlEnum.URL152
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoDocumentoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoDocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDocumento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_DOCUMENTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoDocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDocumentoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
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
        tituloDocumento = nombreTransaccion;

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
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("TIPO_TRANSACCION", tipoTransaccion);
        registro.getCampos().remove("NOMBREDOCUMENTO");

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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return true
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("TIPO_TRANSACCION");
        registro.getCampos().remove("NOMBREDOCUMENTO");

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
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), true);

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tituloDocumento
     * 
     * @return tituloDocumento
     */
    public String getTituloDocumento() {
        return tituloDocumento;
    }

    /**
     * Asigna la variable tituloDocumento
     * 
     * @param tituloDocumento
     */
    public void setTituloDocumento(String tituloDocumento) {
        this.tituloDocumento = tituloDocumento;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoDocumento
     * 
     * @return listaTipoDocumento
     */
    public RegistroDataModelImpl getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    /**
     * Asigna la lista listaTipoDocumento
     * 
     * @param listaTipoDocumento
     * Variable a asignar en listaTipoDocumento
     */
    public void setListaTipoDocumento(
        RegistroDataModelImpl listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    /**
     * Retorna la lista listaTipoDocumento
     * 
     * @return listaTipoDocumento
     */
    public RegistroDataModelImpl getListaTipoDocumentoE() {
        return listaTipoDocumentoE;
    }

    /**
     * Asigna la lista listaTipoDocumento
     * 
     * @param listaTipoDocumento
     * Variable a asignar en listaTipoDocumento
     */
    public void setListaTipoDocumentoE(
        RegistroDataModelImpl listaTipoDocumentoE) {
        this.listaTipoDocumentoE = listaTipoDocumentoE;
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
