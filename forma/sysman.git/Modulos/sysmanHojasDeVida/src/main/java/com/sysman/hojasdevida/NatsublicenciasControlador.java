/*-
 * NatsublicenciasControlador.java
 *
 * 1.0
 * 
 * 18/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatsublicenciasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase que se encarga de gestionar las licencias no remuneradas.
 *
 * @version 1.0, 18/12/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class NatsublicenciasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variables encargada de almacenar temporalmente el usuario al
     * que se va a registrar la licencia no remunerada
     */
    private String idEmpleado;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almacenar temporalmente los datos de los
     * tipos de licencias
     */
    private RegistroDataModelImpl listaLicencia;
    /**
     * Variable encargada de almacenar temporalmente los datos de los
     * anos
     */
    private List<Registro> listaAno;
    /**
     * Variable encargada de almacenar temporalmente los datos de los
     * periodos
     */
    private RegistroDataModelImpl listaPeriodo;
    /**
     * Variable encargada de almacenar temporalmente el periodo
     * seleccionado de la interfaz grafica
     */
    private String periodo;
    /**
     * Variable encargada de almacenar temporalmente los datos de los
     * meses
     */
    private List<Registro> listaMes;
    /**
     * Variable encargada de almacenar temporalmente los datos de los
     * tipos actos
     */
    private RegistroDataModelImpl listaTipoActo;
    /**
     * Variable encargada de almacenar temporalmente el tipo acto
     * seleccionado
     */
    private String tipoActo;
    /**
     * Variable encargada de almacenar temporalmente la licencia
     * seleccionado
     */
    private String licencia;

    private final String idDeProcesoCons;
    private final String licenciaCons;
    private final String nomLicenciaCons;
    private final String nomPeriodoCons;
    private final String nomTipoActoCons;
    private Map<String, Object> parametrosRetorno;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatsublicenciasControlador
     */
    @SuppressWarnings("unchecked")
    public NatsublicenciasControlador() {
        super();
        compania = SessionUtil.getCompania();

        Map<String, Object> parametros = SessionUtil.getFlash();

        idDeProcesoCons = "ID_DE_PROCESO";
        licenciaCons = "LICENCIA";
        nomLicenciaCons = "NOM_LICENCIA";
        nomPeriodoCons = "NOM_PERIODO";
        nomTipoActoCons = "NOM_TIPOACTO";

        registro = new Registro();
        if (parametros != null) {
            idEmpleado = parametros.get("idEmpleado").toString();
            parametrosRetorno = (HashMap<String, Object>) parametros.get("rid");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.NATSUBLICENCIAS_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaLicencia();
        cargarListaAno();
        cargarListaTipoActo();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaPeriodo();
        cargarListaMes();
        registro.getCampos().put("FECHANOVEDAD", new Date());
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        tabla = "LICENCIAS";
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put("KEY_ID_DE_EMPLEADO", idEmpleado);
        parametrosListado.put("KEY_ID_DE_PROCESO",
                        registro.getCampos().get(idDeProcesoCons));
        parametrosListado.put("KEY_NUMERO_ACTO",
                        registro.getCampos().get("NUMERO_ACTO"));

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0001
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0002
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0003
                                                        .getValue());

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0004
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0005
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaLicencia
     *
     */
    public void cargarListaLicencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0007
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaLicencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, licenciaCons);

        // 626013
    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatsublicenciasControladorUrlEnum.URL0008
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
     * Carga la lista listaTipoActo
     *
     */
    public void cargarListaTipoActo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0009
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoActo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "CODIGO");

        // 707001
    }

    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatsublicenciasControladorUrlEnum.URL0006
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 7013 ANO COMPANIA
    }

    /**
     * 
     * Carga la lista listaPeriodo
     *
     */
    public void cargarListaPeriodo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsublicenciasControladorUrlEnum.URL0010
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(GeneralParameterEnum.MES.getName(),
                        registro.getCampos().get("MES"));

        listaPeriodo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.PERIODO.getName());

        // 471055

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
        cargarListaPeriodo();
        registro.getCampos().put("MES", null);
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaLicencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaLicencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nomLicenciaCons,
                        retornarString(registroAux, "DESCRIPCION"));

        licencia = retornarString(registroAux, licenciaCons);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPeriodo
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPeriodo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nomPeriodoCons,
                        retornarString(registroAux, nomPeriodoCons));
        registro.getCampos().put(idDeProcesoCons,
                        retornarString(registroAux, idDeProcesoCons));

        periodo = retornarString(registroAux,
                        GeneralParameterEnum.PERIODO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoActo
     *
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoActo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nomTipoActoCons,
                        registroAux.getCampos().get("NOMBRE"));

        tipoActo = retornarString(registroAux, "CODIGO");

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registro.getCampos().put("TIPOACTO", tipoActo);
        registro.getCampos().put(licenciaCons, licencia);

        registro.getCampos().remove(nomPeriodoCons);
        registro.getCampos().remove(nomTipoActoCons);
        registro.getCampos().remove(nomLicenciaCons);
        if (!validarFechas()) {
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
        registro.getCampos().remove(nomPeriodoCons);
        registro.getCampos().remove(nomTipoActoCons);
        registro.getCampos().remove(nomLicenciaCons);

        if (!validarFechas()) {
            return false;
        }
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
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", parametrosRetorno);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaLicencia
     * 
     * @return listaLicencia
     */
    public RegistroDataModelImpl getListaLicencia() {
        return listaLicencia;
    }

    /**
     * Asigna la lista listaLicencia
     * 
     * @param listaLicencia
     * Variable a asignar en listaLicencia
     */
    public void setListaLicencia(RegistroDataModelImpl listaLicencia) {
        this.listaLicencia = listaLicencia;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(RegistroDataModelImpl listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public RegistroDataModelImpl getListaTipoActo() {
        return listaTipoActo;
    }

    public void setListaTipoActo(RegistroDataModelImpl listaTipoActo) {
        this.listaTipoActo = listaTipoActo;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private boolean validarFechas() {
        boolean rta = true;
        Date fechaInicial = (Date) registro.getCampos().get("FECHA_INICIO");
        Date fechaFinal = (Date) registro.getCampos().get("FECHA_FINAL");

        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;

    }

}
