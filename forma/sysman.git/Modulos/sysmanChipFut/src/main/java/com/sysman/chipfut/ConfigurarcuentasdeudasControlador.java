/*-
 * ConfigurarcuentasdeudasControlador.java
 *
 * 1.0
 * 
 * 23/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.enums.ConfigurarcuentasdeudasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma configurarcuentasdeudas, asociada al
 * formulario Configurar cuentas de deuda. Que permite visualizar y
 * editar el tipo de deuda, tipo de operacion y fuente de una cuenta
 * en deuda del plan presupuestal, en un a�o especifico.
 *
 * @version 1.0, 23/03/2017
 * @author jlramirez
 * 
 * @version 2.0,13/07/2018, Proceso de Refactoring DSS,cambio de
 * numero de formulario por enum,cambio de tabla origen por
 * PLAN_PPTAL_CONFIG
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class ConfigurarcuentasdeudasControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena la cadena de texto "CODIGOFU_FUT"
     */
    private final String codFufut;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el a�o seleccionado en la combo.
     */
    private String anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * del a�o.
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * de la fuente de recursos FUT.
     */
    private RegistroDataModelImpl listafuenteFut;
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * de la fuente de recursos FUT.
     */
    private RegistroDataModelImpl listafuenteFutE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfigurarcuentasdeudasControlador
     */
    public ConfigurarcuentasdeudasControlador() {
        super();
        compania = SessionUtil.getCompania();
        codFufut = "CODIGOFU_FUT";
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        try {

            // 1372
            numFormulario = GeneralCodigoFormaEnum.CONFIGURARCUENTASDEUDAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ConfigurarcuentasdeudasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        tabla = "PLAN_PPTAL_CONFIG";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAnio();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarcuentasdeudasControladorUrlEnum.URL5565
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarcuentasdeudasControladorUrlEnum.URL5568
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnio
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarcuentasdeudasControladorUrlEnum.URL5250
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
     * Carga la lista listafuenteFut
     */
    public void cargarListafuenteFut() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarcuentasdeudasControladorUrlEnum.URL19563
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(GeneralParameterEnum.DESTINO.getName(), "I");
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listafuenteFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codFufut);

    }

    /**
     * Carga la lista listafuenteFut
     */
    public void cargarListafuenteFutE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarcuentasdeudasControladorUrlEnum.URL19563
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(GeneralParameterEnum.DESTINO.getName(), "I");
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listafuenteFutE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codFufut);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        cargarListafuenteFutE();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuenteFut
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteFut(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_FUT",
                        registroAux.getCampos().get(codFufut));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuenteFut
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteFutE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codFufut), "")
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
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBREFU_FUT");
        registro.getCampos().remove("TIPODEUDA_FUTNOM");
        registro.getCampos().remove("TIPOOPERACION_FUTNOM");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("CENTRO_COSTO");
        registro.getCampos().remove("TERCERO");
        registro.getCampos().remove("SUCURSAL");
        registro.getCampos().remove("AUXILIAR");
        registro.getCampos().remove("REFERENCIA");
        registro.getCampos().remove("FUENTE_RECURSO");
        registro.getCampos().remove("SECTOR_REGALIAS");
        registro.getCampos().remove("TIPODEUDA_FUTNOM");
        registro.getCampos().remove("TIPOOPERACION_FUTNOM");
        registro.getCampos().remove("NOMBREFU_FUT");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    // <SET_GET_ATRIBUTOS>
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
     * Retorna la lista listafuenteFut
     * 
     * @return listafuenteFut
     */
    public RegistroDataModelImpl getListafuenteFut() {
        return listafuenteFut;
    }

    /**
     * Asigna la lista listafuenteFut
     * 
     * @param listafuenteFut
     * Variable a asignar en listafuenteFut
     */
    public void setListafuenteFut(RegistroDataModelImpl listafuenteFut) {
        this.listafuenteFut = listafuenteFut;
    }

    /**
     * Retorna la lista listafuenteFut
     * 
     * @return listafuenteFut
     */
    public RegistroDataModelImpl getListafuenteFutE() {
        return listafuenteFutE;
    }

    /**
     * Asigna la lista listafuenteFut
     * 
     * @param listafuenteFut
     * Variable a asignar en listafuenteFut
     */
    public void setListafuenteFutE(RegistroDataModelImpl listafuenteFutE) {
        this.listafuenteFutE = listafuenteFutE;
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
