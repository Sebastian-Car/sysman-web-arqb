/*-
 * TipoClaseEventoSstsControlador.java
 *
 * 1.0
 * 
 * 28/12/2017
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmtipotransaccionsstsControladorEnum;
import com.sysman.hojasdevida.enums.FrmtipotransaccionsstsControladorUrlEnum;
import com.sysman.hojasdevida.enums.TipoClaseEventoSstsControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario Clase Evento SST en
 * Access "FRM_TIPO_CLASE_EVENTO_SST", el cual es llamado desde Hojas
 * de vida / Seguridad y salud en el trabajo / Archivos / Clase de
 * evento SST
 *
 * 
 * @version 1.0, 28/12/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class TipoClaseEventoSstsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String cCodigo;

    private String tipotransaccion;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo de Transaccion cuando es un
     * registro nuevo
     */
    private RegistroDataModelImpl listaTipoTransaccion;
    /**
     * Listado de registros para el combo de Transaccion cuando se
     * esta editando un registro
     */
    private RegistroDataModelImpl listaTipoTransaccionE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */

    /**
     * variable que almanna la lista de plantillas
     */
    private RegistroDataModelImpl listaModeloPlantilla;
    /**
     * variable que almanna la lista de plantillas
     */
    private RegistroDataModelImpl listaModeloPlantillaE;

    private String codigoPlantilla;

    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TipoClaseEventoSstsControlador
     */
    public TipoClaseEventoSstsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            tipotransaccion = (String) parametros.get("transaccion");
            codigoPlantilla = (String) parametros.get("plantilla");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.TIPO_CLASE_EVENTOSSTS_CONTROLADOR
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
        enumBase = GenericUrlEnum.SST_CLASE_EVENTO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaModeloPlantilla();
        cargarListaModeloPlantillaE();
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
        parametrosListado.put("TIPOTRANSACCION",
                        tipotransaccion);
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaModeloPlantilla
     */
    public void cargarListaModeloPlantilla() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipotransaccionsstsControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmtipotransaccionsstsControladorEnum.TIPO.getValue(),
                        "54");

        listaModeloPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaModeloPlantilla
     */
    public void cargarListaModeloPlantillaE() {

        listaModeloPlantillaE = listaModeloPlantilla;

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
     * listaTipoTransaccion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoTransaccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(TipoClaseEventoSstsControladorEnum.TIPO_TRANSACCION
                                        .getValue(),
                                        retornarString(registroAux, cCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoTransaccion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoTransaccionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, cCodigo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put("CODIGO_PLANTILLA", SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(GeneralParameterEnum.CODIGO
                                                                        .getName()),
                                                        "")
                                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantillaE(SelectEvent event) {
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
     * Genera el valor consecutivo antes de realizar una insercion
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("TIPO_TRANSACCION", tipotransaccion);
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            String criterio = SysmanFunciones.concatenar("COMPANIA =''",
                            compania, "'' AND TIPO_TRANSACCION =",
                            tipotransaccion);

            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SST_CLASE_EVENTO",
                            criterio,
                            "CODIGO",
                            "1");
            registro.getCampos().put(cCodigo, consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Si el proceso de insercion fue exitoso
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
     * @return Si el proceso previo a la actualizacion fue exitoso
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
     * @return Si el proceso de actualizacion fue exitoso
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
     * @return Si el proceso previo a la eliminacion fue exitoso
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
     * @return Si el proceso de eliminacion fue exitoso
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
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos()
                        .remove(TipoClaseEventoSstsControladorEnum.NOMBRETIPOTX
                                        .getValue());

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

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoTransaccion
     * 
     * @return listaTipoTransaccion
     */
    public RegistroDataModelImpl getListaTipoTransaccion() {
        return listaTipoTransaccion;
    }

    /**
     * Asigna la lista listaTipoTransaccion
     * 
     * @param listaTipoTransaccion
     * Variable a asignar en listaTipoTransaccion
     */
    public void setListaTipoTransaccion(
        RegistroDataModelImpl listaTipoTransaccion) {
        this.listaTipoTransaccion = listaTipoTransaccion;
    }

    /**
     * Retorna la lista listaTipoTransaccion
     * 
     * @return listaTipoTransaccion
     */
    public RegistroDataModelImpl getListaTipoTransaccionE() {
        return listaTipoTransaccionE;
    }

    /**
     * Asigna la lista listaTipoTransaccion
     * 
     * @param listaTipoTransaccion
     * Variable a asignar en listaTipoTransaccion
     */
    public void setListaTipoTransaccionE(
        RegistroDataModelImpl listaTipoTransaccionE) {
        this.listaTipoTransaccionE = listaTipoTransaccionE;
    }

    /**
     * Retorna la lista listaModeloPlantilla
     * 
     * @return listaModeloPlantilla
     */
    public RegistroDataModelImpl getListaModeloPlantilla() {
        return listaModeloPlantilla;
    }

    /**
     * Asigna la lista listaModeloPlantilla
     * 
     * @param listaModeloPlantilla
     * Variable a asignar en listaModeloPlantilla
     */
    public void setListaModeloPlantilla(
        RegistroDataModelImpl listaModeloPlantilla) {
        this.listaModeloPlantilla = listaModeloPlantilla;
    }

    /**
     * Retorna la lista listaModeloPlantilla
     * 
     * @return listaModeloPlantilla
     */
    public RegistroDataModelImpl getListaModeloPlantillaE() {
        return listaModeloPlantillaE;
    }

    /**
     * Asigna la lista listaModeloPlantilla
     * 
     * @param listaModeloPlantilla
     * Variable a asignar en listaModeloPlantilla
     */
    public void setListaModeloPlantillaE(
        RegistroDataModelImpl listaModeloPlantillaE) {
        this.listaModeloPlantillaE = listaModeloPlantillaE;
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
