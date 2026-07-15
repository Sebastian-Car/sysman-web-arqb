/*-
 * ConfigurarplancontablechipsControlador.java
 *
 * 1.0
 * 
 * 16/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.enums.ConfigurarplancontablechipsControladorEnum;
import com.sysman.chipfut.enums.ConfigurarplancontablechipsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configurar los planes contables de CHIP-FUT
 *
 * @version 1.0, 16/03/2017
 * @author eamaya
 * 
 * @version 1.5 21/03/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ConfigurarplancontablechipsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;

    private final String modulo;

    /**
     * Constante que almacena el valor "CODIGO"
     */
    private final String cCodigo;
    /**
     * Atributo que almacena el valor de la selección del check de
     * configurar plan a 6 dígitos
     */
    private boolean plan6Dig;
    /**
     * Atributo que almacena el código de la clase de cuenta
     */
    private String claseCuenta;
    /**
     * Atributo que almacena el número de una cuenta
     */
    private String cuenta;

    /**
     * Atributo que almacena el valor del año
     */
    private String anio;

    /**
     * Atributo utilizado para validar la visibilidad del cuadro de
     * confirmación de mostrar cuentas auxiliares
     */
    private boolean visibleDialogo;
    /**
     * Variable que alamacena el texto que tendra el cuadro de diálogo
     */
    private String textoDialogo;

    /**
     * Variable que almacena la seleccion del botón Mostrar Cuentas
     * Auxiliares
     */

    private boolean botonCuentaAuxiliar;

    /**
     * Combo que carga los terceros equivalentes
     */
    private RegistroDataModelImpl listaTerceroEquivalente;
    /**
     * Combo que carga los terceros equivalentes en la grilla
     */
    private RegistroDataModelImpl listaTerceroEquivalenteE;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que administra la visibilidad del boton Configuracion
     * Prevalidacion
     */
    private boolean verPrevalidacion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que almacena los registros de las fuentes
     */
    private List<Registro> listaFUENTE;
    /**
     * Lista que almacena los años
     */
    private List<Registro> listaanio;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena los registros de las clases contables
     */
    private RegistroDataModelImpl listaCLASE;

    /**
     * Lista que almacena las cuentas
     */
    private RegistroDataModelImpl listacuenta;
    /**
     * Lista que almacena los registros de codigos equivalentes
     */
    private RegistroDataModelImpl listaTexto151;

    /**
     * Lista auxiliar que almacena los registros de codigos
     * equivalentes
     */
    private RegistroDataModelImpl listaTexto151E;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Lista que almacena los registros de las fuentes
     */
    private RegistroDataModelImpl listaFuente;
    /**
     * Lista que almacena los registros de las fuentes
     */
    private RegistroDataModelImpl listaFuenteE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ConfigurarplancontablechipsControlador
     */
    public ConfigurarplancontablechipsControlador() {
        super();
        compania = SessionUtil.getCompania();

        cCodigo = "CODIGO";
        visibleDialogo = false;
        botonCuentaAuxiliar = false;
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        verPrevalidacion = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.CONFIGURARPLANCONTABLECHIPS_CONTROLADOR
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
        tabla = GenericUrlEnum.PLANCONTABLE.getTable();
        buscarLlave();
        registro = new Registro();

        // <CARGAR_LISTA>
        cargarListaanio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCLASE();

        cargarListaTerceroEquivalente();
        cargarListaTerceroEquivalenteE();

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
        cargado = false;
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        parametrosListado.put(GeneralParameterEnum.CLASE.getName(),
                        claseCuenta);

        if (plan6Dig) {

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplancontablechipsControladorUrlEnum.URL3665
                                                            .getValue());

        }
        else {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplancontablechipsControladorUrlEnum.URL20888
                                                            .getValue());

        }

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL19121
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCLASE
     *
     */
    public void cargarListaCLASE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL14271
                                                        .getValue());

        listaCLASE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacuenta
     *
     */
    public void cargarListacuenta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL14271
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listacuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuente
     *
     */
    public void cargarListaFuente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL10355
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuente
     *
     */
    public void cargarListaFuenteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL10355
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTexto151
     *
     */
    public void cargarListaTexto151() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL1578
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaTexto151 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTexto151
     *
     */
    public void cargarListaTexto151E() {
        listaTexto151E = listaTexto151;
    }

    /**
     * 
     * Carga la lista listaTerceroEquivalente
     *
     */
    public void cargarListaTerceroEquivalente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarplancontablechipsControladorUrlEnum.URL1687
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroEquivalente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ConfigurarplancontablechipsControladorEnum.NIT_CEDULA
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaTerceroEquivalente
     *
     */
    public void cargarListaTerceroEquivalenteE() {
        listaTerceroEquivalenteE = listaTerceroEquivalente;
    }

    /**
     * 
     * Carga la lista listaanio
     *
     */
    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarplancontablechipsControladorUrlEnum.URL1512
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton abrirpre en la vista
     *
     */
    public void oprimirabrirpre() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton REVISARMOV en la vista
     *
     */
    public void oprimirREVISARMOV() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando152 en la vista
     *
     */
    public void oprimirComando152() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(claseCuenta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2945"));
            return;
        }
        else {
            botonCuentaAuxiliar = true;
            plan6Dig = false;
            reasignarOrigen();
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BotonANRR en la vista
     *
     */
    public void oprimirBotonANRR() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(claseCuenta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2945"));
            return;
        }
        else {

            visibleDialogo = true;
            textoDialogo = idioma.getString("TB_TB4034").replace("#$ANIO#$",
                            anio);

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control anio
     * 
     */
    public void cambiaranio() {
        cargarListacuenta();
        reasignarOrigen();

        cargarListaTexto151();
        cargarListaTexto151E();
        cargarListaFuente();
        cargarListaFuenteE();
    }

    /**
     * Metodo ejecutado al cambiar el control ValidaPlan6dig
     */
    public void cambiarValidaPlan6dig() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(claseCuenta)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2944"));
            plan6Dig = false;
            return;
        }
        else {
            reasignarOrigen();

        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * confirmacionCAxuiliar en la vista
     */
    public void aceptarconfirmacionCAxuiliar() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarplancontablechipsControladorUrlEnum.URL15847
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        visibleDialogo = false;

    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * confirmacionCAxuiliar en la vista
     */
    public void cancelarconfirmacionCAxuiliar() {
        visibleDialogo = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCLASE
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCLASE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        claseCuenta = registroAux.getCampos().get(cCodigo).toString();
        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuenta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuenta = registroAux.getCampos().get(cCodigo).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTexto151
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto151(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_EQUIV",
                        registroAux.getCampos().get(cCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTexto151
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto151E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroEquivalente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroEquivalente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TERCERO_RECIPROCAS",
                        registroAux.getCampos()
                                        .get(ConfigurarplancontablechipsControladorEnum.NIT_CEDULA
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroEquivalente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroEquivalenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(ConfigurarplancontablechipsControladorEnum.NIT_CEDULA
                                        .getValue())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {

        validarVisibilidadPrevalidacion();

    }

    private void validarVisibilidadPrevalidacion() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.USUARIO.getName(),
                        usuario);

        param.put("APLICACION", modulo);

        try {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarplancontablechipsControladorUrlEnum.URL8935
                                                                            .getValue())
                                            .getUrl(), param));

            if (Integer.parseInt(SysmanFunciones
                            .nvl(reg.getCampos().get("NIVEL_USUARIO"), "0")
                            .toString()) < 9) {
                verPrevalidacion = false;
            }
            else {
                verPrevalidacion = true;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        try {
            if (registro.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                            .toString().length() == 6) {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ConfigurarplancontablechipsControladorUrlEnum.URL739
                                                                .getValue());

                Map<String, Object> parametros = new TreeMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.ANO.getName(), anio);
                parametros.put(GeneralParameterEnum.CODIGO.getName(), registro
                                .getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName()));
                parametros.put(ConfigurarplancontablechipsControladorEnum.RECIPROCAS
                                .getValue(),
                                registro
                                                .getCampos()
                                                .get(ConfigurarplancontablechipsControladorEnum.NOREPORTARRECIPROCAS
                                                                .getValue()));
                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);

            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
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

    public boolean isPlan6Dig() {
        return plan6Dig;
    }

    public void setPlan6Dig(boolean plan6Dig) {
        this.plan6Dig = plan6Dig;
    }

    /**
     * Retorna la variable claseCuenta
     * 
     * @return claseCuenta
     */
    public String getClaseCuenta() {
        return claseCuenta;
    }

    /**
     * Asigna la variable claseCuenta
     * 
     * @param claseCuenta
     * Variable a asignar en claseCuenta
     */
    public void setClaseCuenta(String claseCuenta) {
        this.claseCuenta = claseCuenta;
    }

    /**
     * Retorna la variable cuenta
     * 
     * @return cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * Asigna la variable cuenta
     * 
     * @param cuenta
     * Variable a asignar en cuenta
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
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

    public String getTextoDialogo() {
        return textoDialogo;
    }

    public void setTextoDialogo(String textoDialogo) {
        this.textoDialogo = textoDialogo;
    }

    public boolean isBotonCuentaAuxiliar() {
        return botonCuentaAuxiliar;
    }

    public void setBotonCuentaAuxiliar(boolean botonCuentaAuxiliar) {
        this.botonCuentaAuxiliar = botonCuentaAuxiliar;
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

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCLASE
     * 
     * @return listaCLASE
     */
    public RegistroDataModelImpl getListaCLASE() {
        return listaCLASE;
    }

    /**
     * Asigna la lista listaCLASE
     * 
     * @param listaCLASE
     * Variable a asignar en listaCLASE
     */
    public void setListaCLASE(RegistroDataModelImpl listaCLASE) {
        this.listaCLASE = listaCLASE;
    }

    /**
     * Retorna la lista listacuenta
     * 
     * @return listacuenta
     */
    public RegistroDataModelImpl getListacuenta() {
        return listacuenta;
    }

    /**
     * Asigna la lista listacuenta
     * 
     * @param listacuenta
     * Variable a asignar en listacuenta
     */
    public void setListacuenta(RegistroDataModelImpl listacuenta) {
        this.listacuenta = listacuenta;
    }

    /**
     * Retorna la lista listaFuente
     * 
     * @return listaFuente
     */

    public RegistroDataModelImpl getListaFuente() {
        return listaFuente;
    }

    /**
     * Asigna la lista listaFuente
     * 
     * @param listaFuente
     * Variable a asignar en listaFuente
     */
    public void setListaFuente(RegistroDataModelImpl listaFuente) {
        this.listaFuente = listaFuente;
    }

    /**
     * Retorna la lista listaFuente
     * 
     * @return listaFuente
     */
    public RegistroDataModelImpl getListaFuenteE() {
        return listaFuenteE;
    }

    /**
     * Asigna la lista listaFuente
     * 
     * @param listaFuente
     * Variable a asignar en listaFuente
     */
    public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
        this.listaFuenteE = listaFuenteE;
    }

    /**
     * Retorna la lista listaTexto151
     * 
     * @return listaTexto151
     */
    public RegistroDataModelImpl getListaTexto151() {
        return listaTexto151;
    }

    /**
     * Asigna la lista listaTexto151
     * 
     * @param listaTexto151
     * Variable a asignar en listaTexto151
     */
    public void setListaTexto151(RegistroDataModelImpl listaTexto151) {
        this.listaTexto151 = listaTexto151;
    }

    /**
     * Retorna la lista listaTexto151
     * 
     * @return listaTexto151
     */
    public RegistroDataModelImpl getListaTexto151E() {
        return listaTexto151E;
    }

    /**
     * Asigna la lista listaTexto151
     * 
     * @param listaTexto151
     * Variable a asignar en listaTexto151
     */
    public void setListaTexto151E(RegistroDataModelImpl listaTexto151E) {
        this.listaTexto151E = listaTexto151E;
    }

    /**
     * Retorna la lista listaTerceroEquivalente
     * 
     * @return listaTerceroEquivalente
     */
    public RegistroDataModelImpl getListaTerceroEquivalente() {
        return listaTerceroEquivalente;
    }

    /**
     * Asigna la lista listaTerceroEquivalente
     * 
     * @param listaTerceroEquivalente
     * Variable a asignar en listaTerceroEquivalente
     */
    public void setListaTerceroEquivalente(
        RegistroDataModelImpl listaTerceroEquivalente) {
        this.listaTerceroEquivalente = listaTerceroEquivalente;
    }

    /**
     * Retorna la lista listaTerceroEquivalente
     * 
     * @return listaTerceroEquivalente
     */
    public RegistroDataModelImpl getListaTerceroEquivalenteE() {
        return listaTerceroEquivalenteE;
    }

    /**
     * Asigna la lista listaTerceroEquivalente
     * 
     * @param listaTerceroEquivalente
     * Variable a asignar en listaTerceroEquivalente
     */
    public void setListaTerceroEquivalenteE(
        RegistroDataModelImpl listaTerceroEquivalenteE) {
        this.listaTerceroEquivalenteE = listaTerceroEquivalenteE;
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

    public boolean isVerPrevalidacion() {
        return verPrevalidacion;
    }

    public void setVerPrevalidacion(boolean verPrevalidacion) {
        this.verPrevalidacion = verPrevalidacion;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
