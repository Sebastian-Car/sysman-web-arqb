/*-
 * FrmevaluadorevaluadosControlador.java
 *
 * 1.0
 * 
 * 25/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmevaluadorevaluadosControladorEnum;
import com.sysman.hojasdevida.enums.FrmevaluadorevaluadosControladorUrlEnum;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Esta clase me permite esgstionar el formulario FRM_EVALUADOR,
 * EVALUADO
 *
 * @version 1.0, 25/01/2018
 * @author mvenegas
 * 
 * Se agrego el cargarListaGrupoAplicar
 * 
 * @version 2.0 30/05/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class FrmevaluadorevaluadosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista encargada de mostrar los datos del combo
     * listacmbTipoEvalcuacion
     */
    private RegistroDataModelImpl listacmbTipoEvalcuacion;
    /**
     * Lista encargada de mostrar los datos del combo
     * listacmbCodigoEvaluador
     */
    private RegistroDataModelImpl listacmbCodigoEvaluador;
    /**
     * Lista encargada de mostrar los datos del combo
     * listacmbCodigoEEvaluado
     */
    private RegistroDataModelImpl listacmbCodigoEEvaluado;

    private RegistroDataModelImpl listaGrupoAplicar;

    /*
     * Variable para recibir la clase de evaluacion
     */
    private String claseEvaluacion;
    /*
     * Variable encargada de crear el titulo segun la clase de
     * evaluacion seleccionada en MAYUSCULA
     */
    private String titulo;
    /*
     * Variable encargada de crear el titulo segun la clase de
     * evaluacion seleccionada en minuscula
     */
    private String titulo2;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private int indice;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmevaluadorevaluadosControlador
     */
    public FrmevaluadorevaluadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        claseEvaluacion = SessionUtil.getSessionVar(
                        ConstantesHojasDeVidaEnum.CLASE_EVALUACION.getValue())
                        .toString();
        titulo = "";
        titulo2 = "";
        indice = 0;
        try {
            numFormulario = GeneralCodigoFormaEnum.EVALUADOR_EVALUADO_CONTROLADOR
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
        enumBase = GenericUrlEnum.EV_EVALUADOR_EVALUADO;
        asignarOrigenDatos();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        cargarListacmbTipoEvalcuacion();
        cargarListacmbCodigoEvaluador();
        cargarListacmbCodigoEEvaluado();
        cargarListaGrupoAplicar();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado
                        .put(FrmevaluadorevaluadosControladorEnum.CLASEEVALUACION
                                        .getValue(),
                                        claseEvaluacion);

    }

    /**
     * 
     * Carga la lista listacmbTipoEvalcuacion
     *
     */
    public void cargarListacmbTipoEvalcuacion() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmevaluadorevaluadosControladorEnum.CLASEEVALUACION
                        .getValue(), claseEvaluacion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevaluadorevaluadosControladorUrlEnum.URL106
                                                        .getValue());

        listacmbTipoEvalcuacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmevaluadorevaluadosControladorEnum.CODIGO.getValue());
    }

    /**
     * 
     * Carga la lista listacmbCodigoEvaluador
     *
     */
    public void cargarListacmbCodigoEvaluador() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmevaluadorevaluadosControladorEnum.ESCALAFON.getValue(),
                        registro.getCampos()
                                        .get(FrmevaluadorevaluadosControladorEnum.ESCALAFON_EVALUADOR
                                                        .getValue()));

        param.put(FrmevaluadorevaluadosControladorEnum.ID_DE_CARGO.getValue(),
                        registro.getCampos()
                                        .get(FrmevaluadorevaluadosControladorEnum.CARGO_EVALUADOR
                                                        .getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevaluadorevaluadosControladorUrlEnum.URL102
                                                        .getValue());

        listacmbCodigoEvaluador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmevaluadorevaluadosControladorEnum.ID_DE_EMPLEADO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listacmbCodigoEEvaluado
     *
     */
    public void cargarListacmbCodigoEEvaluado() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevaluadorevaluadosControladorUrlEnum.URL102
                                                        .getValue());

        listacmbCodigoEEvaluado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmevaluadorevaluadosControladorEnum.ID_DE_EMPLEADO
                                        .getValue());
    }

    public void cargarListaGrupoAplicar() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseEvaluacion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevaluadorevaluadosControladorUrlEnum.URL108
                                                        .getValue());

        listaGrupoAplicar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "GRUPO");

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbTipoEvalcuacion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoEvalcuacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.TIPO_EVALUACION
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.CODIGO
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBREEVALUACION
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoEvaluador
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoEvaluador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.CODIGO_EMPLEADO_EVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.ID_DE_EMPLEADO
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.CEDULA_EVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.NUMERO_DCTO
                                                                        .getValue()));
        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.SUCURSAL_EVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.SUCURSAL
                                                                        .getValue()));

        // NUEVOS
        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBREEVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBREEMPLEADO"));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.ESCALAFON_EVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("ESCALAFON"));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBREESCALAFONEVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBREESCALAFON"));

        registro.getCampos()
                        .put("CARGO_EVALUADOR",
                                        registroAux.getCampos()
                                                        .get("ID_DE_CARGO"));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBRECARGOEVALUADOR
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBRECARGO"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoEEvaluado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoEEvaluado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.CODIGO_EMPLEADO_EVALUADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.ID_DE_EMPLEADO
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.CEDULA_EVALUADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.NUMERO_DCTO
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.SUCURSAL_EVALUADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevaluadorevaluadosControladorEnum.SUCURSAL
                                                                        .getValue()));

        // NUEVOS

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBREEVALUADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBREEMPLEADO"));

        registro.getCampos()
                        .put("ESCALAFON_EVALUADO",
                                        registroAux.getCampos()
                                                        .get("ESCALAFON"));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBREESCALAFONEVALUADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBREESCALAFON"));

        registro.getCampos()
                        .put("CARGO_EVALUADO",
                                        registroAux.getCampos()
                                                        .get("ID_DE_CARGO"));

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.NOMBRECARGORVALUADO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("NOMBRECARGO"));
    }

    public void seleccionarFilaGrupoAplicar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("GRUPO_APLICAR",
                        registroAux.getCampos().get("GRUPO").toString());

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.DESCRIPCION
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("DESCRIPCION"));
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
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(FrmevaluadorevaluadosControladorEnum.CODIGO.getValue(),
                            claseEvaluacion);

            Registro rsIdEmpleado;
            rsIdEmpleado = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmevaluadorevaluadosControladorUrlEnum.URL107
                                                                            .getValue())
                                            .getUrl(), param));

            titulo = SysmanFunciones.concatenar("EVALUADOR, EVALUADO ",
                            rsIdEmpleado.getCampos()
                                            .get(FrmevaluadorevaluadosControladorEnum.NOMBRE
                                                            .getValue())
                                + "");

            titulo2 = SysmanFunciones.concatenar("Evaluador, Evaluado ",
                            rsIdEmpleado.getCampos()
                                            .get(FrmevaluadorevaluadosControladorEnum.NOMBRE
                                                            .getValue())
                                + "");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
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

        registro.getCampos()
                        .put(FrmevaluadorevaluadosControladorEnum.CLASE_EVALUACION
                                        .getValue(),
                                        claseEvaluacion);

        if (accion.equals(ACCION_INSERTAR)) {

            registro.getCampos()
                            .remove("NOMBREEVALUADO");

            registro.getCampos().remove("DESCRIPCION");

            registro.getCampos()
                            .remove("NOMBREESCALAFONEVALUADO");

            registro.getCampos()
                            .remove("NOMBRECARGORVALUADO");

            registro.getCampos()
                            .remove("NOMBREEVALUADOR");

            registro.getCampos()
                            .remove("NOMBREESCALAFONEVALUADOR");

            registro.getCampos()
                            .remove("NOMBRECARGOEVALUADOR");

            registro.getCampos()
                            .remove("NOMBREEVALUACION");
        }

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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {

            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());

            registro.getCampos()
                            .remove("NOMBREEVALUADO");

            registro.getCampos().remove("DESCRIPCION");

            registro.getCampos()
                            .remove("NOMBREESCALAFONEVALUADO");

            registro.getCampos()
                            .remove("NOMBRECARGORVALUADO");

            registro.getCampos()
                            .remove("NOMBREEVALUADOR");

            registro.getCampos()
                            .remove("NOMBREESCALAFONEVALUADOR");

            registro.getCampos()
                            .remove("NOMBRECARGOEVALUADOR");

            registro.getCampos()
                            .remove("NOMBREEVALUACION");
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
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void activarEdicion(Registro registro) {
        // VA EL CODIGO ANEXO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbTipoEvalcuacion
     * 
     * @return listacmbTipoEvalcuacion
     */
    public RegistroDataModelImpl getListacmbTipoEvalcuacion() {
        return listacmbTipoEvalcuacion;
    }

    /**
     * Asigna la lista listacmbTipoEvalcuacion
     * 
     * @param listacmbTipoEvalcuacion
     * Variable a asignar en listacmbTipoEvalcuacion
     */
    public void setListacmbTipoEvalcuacion(
        RegistroDataModelImpl listacmbTipoEvalcuacion) {
        this.listacmbTipoEvalcuacion = listacmbTipoEvalcuacion;
    }

    /**
     * Retorna la lista listacmbCodigoEvaluador
     * 
     * @return listacmbCodigoEvaluador
     */
    public RegistroDataModelImpl getListacmbCodigoEvaluador() {
        return listacmbCodigoEvaluador;
    }

    /**
     * Asigna la lista listacmbCodigoEvaluador
     * 
     * @param listacmbCodigoEvaluador
     * Variable a asignar en listacmbCodigoEvaluador
     */
    public void setListacmbCodigoEvaluador(
        RegistroDataModelImpl listacmbCodigoEvaluador) {
        this.listacmbCodigoEvaluador = listacmbCodigoEvaluador;
    }

    /**
     * Retorna la lista listacmbCodigoEEvaluado
     * 
     * @return listacmbCodigoEEvaluado
     */
    public RegistroDataModelImpl getListacmbCodigoEEvaluado() {
        return listacmbCodigoEEvaluado;
    }

    /**
     * Asigna la lista listacmbCodigoEEvaluado
     * 
     * @param listacmbCodigoEEvaluado
     * Variable a asignar en listacmbCodigoEEvaluado
     */
    public void setListacmbCodigoEEvaluado(
        RegistroDataModelImpl listacmbCodigoEEvaluado) {
        this.listacmbCodigoEEvaluado = listacmbCodigoEEvaluado;
    }

    /**
     * Retorna la lista listaGrupoAplicar
     * 
     * @return listaGrupoAplicar
     */
    public RegistroDataModelImpl getListaGrupoAplicar() {
        return listaGrupoAplicar;
    }

    /**
     * Asigna la lista listaGrupoAplicar
     * 
     * @param listaGrupoAplicar
     * Variable a asignar en listaGrupoAplicar
     */
    public void setListaGrupoAplicar(RegistroDataModelImpl listaGrupoAplicar) {
        this.listaGrupoAplicar = listaGrupoAplicar;
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

    public String getClaseEvaluacion() {
        return claseEvaluacion;
    }

    public void setClaseEvaluacion(String claseEvaluacion) {
        this.claseEvaluacion = claseEvaluacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo2() {
        return titulo2;
    }

    public void setTitulo2(String titulo2) {
        this.titulo2 = titulo2;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
    }

    @Override
    public void iniciarListasSubNulo() {
        // Auto-generated method stub

    }

    @Override
    public void iniciarListasSub() {
        // Auto-generated method stub

    }
}
