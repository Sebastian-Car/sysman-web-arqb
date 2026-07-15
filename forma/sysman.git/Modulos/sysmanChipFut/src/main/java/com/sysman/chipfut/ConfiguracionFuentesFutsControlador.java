/*-
 * ConfiguracionFuentesFutsControlador.java
 *
 * 1.0
 * 
 * 16 jul. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.chipfut;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.enums.ConfiguracionFuentesFutsControladorUrlEnum;
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

/**
 * Formulario que permite administrar la configuración de tesorería de
 * salud desde bancos
 *
 * @version 1.0, 16/07/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ConfiguracionFuentesFutsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo que almacena el valor de cuenta banco
     */
    private String cuentaBanco;

    /**
     * Atributo que almacena el valor del trimestre seleccionado
     */
    private String trimestre;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del anio seleccionado
     */
    private String anio;
    /**
     * Variable que almacena el nombre de la cuenta banco seleccionada
     */
    private String nombreBanco;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que carga los anios
     */
    private List<Registro> listaanio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que carga las cuentas de banco
     */
    private RegistroDataModelImpl listacuentaBanco;
    /**
     * Lista que carga las cuentas de banco
     */
    private RegistroDataModelImpl listacuentaBancoE;
    /**
     * Lista que carga las cuentas de tesoreria de salud
     */
    private RegistroDataModelImpl listacodigoFut;
    /**
     * Lista que carga las cuentas de tesoreria de salud
     */
    private RegistroDataModelImpl listacodigoFutE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfiguracionFuentesFutsControlador
     */
    public ConfiguracionFuentesFutsControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        trimestre = "3";
        nombreBanco = "";
        try {
            // 1862
            numFormulario = GeneralCodigoFormaEnum.CONFIGURACION_FUENTES_FUTS_CONTROLADOR
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
        tabla = "DETALLE_COMPROBANTE_CNT";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListacuentaBanco();
        cargarListaanio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigoFut();
        cargarListacodigoFutE();
        cargarListacuentaBanco();
        cargarListacuentaBancoE();
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

        parametrosListado.put("CUENTABANCO", cuentaBanco);

        parametrosListado.put("TRIMESTRE", trimestre);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionFuentesFutsControladorUrlEnum.URL7895
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionFuentesFutsControladorUrlEnum.URL7865
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaanio
     *
     */
    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaanio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfiguracionFuentesFutsControladorUrlEnum.URL8074
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
     * Carga la lista listacuentaBanco
     *
     */
    public void cargarListacuentaBanco() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionFuentesFutsControladorUrlEnum.URL7765
                                                        .getValue());

        listacuentaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacuentaBanco
     *
     */
    public void cargarListacuentaBancoE() {

        listacuentaBancoE = listacuentaBanco;
    }

    /**
     * 
     * Carga la lista listacodigoFut
     *
     */
    public void cargarListacodigoFut() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        param.put(GeneralParameterEnum.TIPO.getName(), "TS");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionFuentesFutsControladorUrlEnum.URL8390
                                                        .getValue());
        listacodigoFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacodigoFut
     *
     */
    public void cargarListacodigoFutE() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        param.put(GeneralParameterEnum.TIPO.getName(), "TS");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionFuentesFutsControladorUrlEnum.URL8999
                                                        .getValue());
        listacodigoFutE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control trimestre
     * 
     */
    public void cambiartrimestre() {
        reasignarOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control anio
     * 
     * 
     */
    public void cambiaranio() {

        cuentaBanco = "";
        reasignarOrigen();
        cargarListacuentaBanco();
        cargarListacuentaBancoE();
        cargarListacodigoFut();
        cargarListacodigoFutE();
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaBanco
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaBanco = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

        nombreBanco = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();

        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaBanco
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaBancoE(SelectEvent event) {
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
     * listacodigoFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoFut(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_FUT",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoFutE(SelectEvent event) {
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
     * 
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
     */
    @Override
    public boolean actualizarAntes() {
    	
    	if (listacodigoFut == null || listacodigoFut.getRowCount() == 0) {
    		JsfUtil.agregarMensajeAlerta(
    				"No se encontraron códigos FUT de tesorería de salud configurados para el año seleccionado.");
    	}
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

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        registro.getCampos().remove("TIPO_SALUD");

        registro.getCampos().remove("CONSECUTIVO");

        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());

        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());

        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

        registro.getCampos().remove(GeneralParameterEnum.TIPO_CPTE.getName());

        registro.getCampos().remove("VALOR_DEBITO");

        registro.getCampos().remove("VALOR_CREDITO");

        registro.getCampos().remove(GeneralParameterEnum.COMPROBANTE.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable cuentaBanco
     * 
     * @return cuentaBanco
     */
    public String getCuentaBanco() {
        return cuentaBanco;
    }

    /**
     * Asigna la variable cuentaBanco
     * 
     * @param cuentaBanco
     * Variable a asignar en cuentaBanco
     */
    public void setCuentaBanco(String cuentaBanco) {
        this.cuentaBanco = cuentaBanco;
    }

    public RegistroDataModelImpl getListacuentaBancoE() {
        return listacuentaBancoE;
    }

    /**
     * Asigna la lista listacuentaBanco
     * 
     * @param listacuentaBanco
     * Variable a asignar en listacuentaBanco
     */
    public void setListacuentaBancoE(RegistroDataModelImpl listacuentaBancoE) {
        this.listacuentaBancoE = listacuentaBancoE;
    }

    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
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

    /**
     * Retorna la variable nombreBanco
     * 
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     * 
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaanio
     * 
     * @return listaanio
     */
    public List<Registro> getListaanio() {
        return listaanio;
    }

    /**
     * Asigna la lista listaanio
     * 
     * @param listaanio
     * Variable a asignar en listaanio
     */
    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacuentaBanco
     * 
     * @return listacuentaBanco
     */
    public RegistroDataModelImpl getListacuentaBanco() {
        return listacuentaBanco;
    }

    /**
     * Asigna la lista listacuentaBanco
     * 
     * @param listacuentaBanco
     * Variable a asignar en listacuentaBanco
     */
    public void setListacuentaBanco(RegistroDataModelImpl listacuentaBanco) {
        this.listacuentaBanco = listacuentaBanco;
    }

    /**
     * Retorna la lista listacodigoFut
     * 
     * @return listacodigoFut
     */
    public RegistroDataModelImpl getListacodigoFut() {
        return listacodigoFut;
    }

    /**
     * Asigna la lista listacodigoFut
     * 
     * @param listacodigoFut
     * Variable a asignar en listacodigoFut
     */
    public void setListacodigoFut(RegistroDataModelImpl listacodigoFut) {
        this.listacodigoFut = listacodigoFut;
    }

    /**
     * Retorna la lista listacodigoFut
     * 
     * @return listacodigoFut
     */
    public RegistroDataModelImpl getListacodigoFutE() {
        return listacodigoFutE;
    }

    /**
     * Asigna la lista listacodigoFut
     * 
     * @param listacodigoFut
     * Variable a asignar en listacodigoFut
     */
    public void setListacodigoFutE(RegistroDataModelImpl listacodigoFutE) {
        this.listacodigoFutE = listacodigoFutE;
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
