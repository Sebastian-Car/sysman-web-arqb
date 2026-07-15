/*-
 * FrmEvaluacionesControlador.java
 *
 * 1.0
 * 
 * 29/01/2018
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmEvaluacionesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite administrar las evaluaciones del empleado.
 *
 * @version 1.0, 29/01/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmEvaluacionesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado a la clase de
     * evaluacion
     */
    private String claseEvaluacion;

    private String titulo;

    private boolean visibleCompetencia;

    private boolean visibleCompromiso;

    private boolean bloqueadoEmpleado;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo de grupo. */
    private RegistroDataModelImpl listacmbGrupo;

    /** Lista que contiene los detalles del combo de empleados. */
    private RegistroDataModelImpl listacmbCodigoEmpleado;

    private RegistroDataModelImpl listaTipoEvaluacion;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmEvaluacionesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmEvaluacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();

        if (parametros != null) {
            rid = (Map<String, Object>) parametros.get("rid");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_EVALUACIONES_CONTROLADOR
                            .getCodigo();
            claseEvaluacion = (String) SessionUtil.getSessionVar(
                            ConstantesHojasDeVidaEnum.CLASE_EVALUACION
                                            .getValue());

            if ("1".equals(claseEvaluacion)) {
                titulo = idioma.getString("TB_TB3976");
            }
            else if ("2".equals(claseEvaluacion)) {
                titulo = idioma.getString("TB_TB3977");
            }
            else {
                titulo = idioma.getString("TB_TB3978");
            }
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigoEmpleado();
        cargarListaTipoEvaluacion();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbGrupo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
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
     * 
     * Metodo ejecutado al oprimir el boton valoraciones en la vista
     *
     * DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirvaloraciones() {
        // <CODIGO_DESARROLLADO>

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FRMEVVALORACIONES_CONTROLADOR
                                        .getCodigo()));
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put("rid", css);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
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
        enumBase = GenericUrlEnum.EV_EVALUACIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(),
                        claseEvaluacion);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbGrupo
     *
     */
    public void cargarListacmbGrupo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseEvaluacion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesControladorUrlEnum.URL4812
                                                        .getValue());
        listacmbGrupo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "GRUPO");
    }

    /**
     * 
     * Carga la lista listacmbCodigoEmpleado
     *
     */
    public void cargarListacmbCodigoEmpleado() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesControladorUrlEnum.URL5453
                                                        .getValue());
        listacmbCodigoEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");
    }

    /**
     * 
     * Carga la lista listaTipoEvaluacion
     *
     */
    public void cargarListaTipoEvaluacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseEvaluacion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesControladorUrlEnum.URL8571
                                                        .getValue());
        listaTipoEvaluacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    public void cambiartxtFechaInicial() {
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos()
                                        .get("FECHA_INICIAL_EVALUACION")));
        if (SysmanFunciones.mes((Date) registro.getCampos()
                        .get("FECHA_INICIAL_EVALUACION")) > 6) {
            registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                            "2");
        }
        else {
            registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                            "1");
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get("NOMBRECOMPLETO"));
        registro.getCampos().put("CODIGO_EMPLEADO", registroAux.getCampos()
                        .get(GeneralParameterEnum.ID_DE_EMPLEADO.getName())
                        .toString());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName())
                                        .toString());
        registro.getCampos().put("CEDULA", registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoEvaluacion
     */
    public void seleccionarFilaTipoEvaluacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_EVALUACION",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("NOMBRE_TIPO",
                        registroAux.getCampos().get("NOMBRE"));
        if ((registro.getCampos().get("NOMBRE_TIPO").toString())
                        .contains("Evaluación Acuerdo 565")) {
            bloqueadoEmpleado = true;
            registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                            "TODOS LOS EMPLEADOS");
            registro.getCampos().put("CODIGO_EMPLEADO", 0);
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            SysmanConstantes.CONS_SUCURSAL);
            registro.getCampos().put("CEDULA", "999999999999999999");
        }
        else {
            bloqueadoEmpleado = false;
            registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                            null);
            registro.getCampos().put("CODIGO_EMPLEADO", null);
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbGrupo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbGrupo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRE_GRUPO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
        registro.getCampos().put("GRUPO_APLICAR",
                        registroAux.getCampos().get("GRUPO").toString());
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
        try {
            visibleCompetencia = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CANTIDAD DE COMPETENCIAS",
                            SessionUtil.getModulo(), new Date(), true));
            visibleCompromiso = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CANTIDAD DE COMPROMISOS",
                            SessionUtil.getModulo(), new Date(), true));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((rid != null) && !rid.isEmpty()) {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }

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
        if (accion.equals(ACCION_INSERTAR)) {
            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            new Date());
            bloqueadoEmpleado=false;
        }
        if ((registro.getCampos().get("NOMBRE_TIPO").toString())
                        .contains("Evaluación Acuerdo 565")) {
            bloqueadoEmpleado = true;
        }
        else {
            bloqueadoEmpleado = false;
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("CLASE_EVALUACION", claseEvaluacion);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove("NOMBRE_GRUPO");
        registro.getCampos().remove("NOMBRE_TIPO");
        registro.getCampos().remove("SEMESTRAL");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isVisibleCompetencia() {
        return visibleCompetencia;
    }

    public void setVisibleCompetencia(boolean visibleCompetencia) {
        this.visibleCompetencia = visibleCompetencia;
    }

    public boolean isVisibleCompromiso() {
        return visibleCompromiso;
    }

    public void setVisibleCompromiso(boolean visibleCompromiso) {
        this.visibleCompromiso = visibleCompromiso;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigoEmpleado
     * 
     * @return listacmbCodigoEmpleado
     */
    public RegistroDataModelImpl getListacmbCodigoEmpleado() {
        return listacmbCodigoEmpleado;
    }

    /**
     * Asigna la lista listacmbCodigoEmpleado
     * 
     * @param listacmbCodigoEmpleado
     * Variable a asignar en listacmbCodigoEmpleado
     */
    public void setListacmbCodigoEmpleado(
        RegistroDataModelImpl listacmbCodigoEmpleado) {
        this.listacmbCodigoEmpleado = listacmbCodigoEmpleado;
    }

    public RegistroDataModelImpl getListacmbGrupo() {
        return listacmbGrupo;
    }

    public void setListacmbGrupo(RegistroDataModelImpl listacmbGrupo) {
        this.listacmbGrupo = listacmbGrupo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public RegistroDataModelImpl getListaTipoEvaluacion() {
        return listaTipoEvaluacion;
    }

    public void setListaTipoEvaluacion(
        RegistroDataModelImpl listaTipoEvaluacion) {
        this.listaTipoEvaluacion = listaTipoEvaluacion;
    }

    public boolean isBloqueadoEmpleado() {
        return bloqueadoEmpleado;
    }

    public void setBloqueadoEmpleado(boolean bloqueadoEmpleado) {
        this.bloqueadoEmpleado = bloqueadoEmpleado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
