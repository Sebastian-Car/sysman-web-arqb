/*-
 * FrmaccionesdemejoracomportamentalesControlador.java
 *
 * 1.0
 * 
 * 08/02/2018
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmaccionesdemejorasControladorEnum;
import com.sysman.hojasdevida.enums.FrmaccionesdemejorasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
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
 * * Esta clase se encarga de gestionar los datos del formulario
 * FRM_EV_ACCIONESMEJORAS_comportamentales
 *
 * @version 1.0, 08/02/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmaccionesdemejoracomportamentalesControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo que se necarga de llenar los datos de la lista
     * listacmbCompetencia
     */
    private RegistroDataModelImpl listacmbCompetencia;
    private RegistroDataModelImpl listaMotivo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /*
     * Constante que sirve para almacenar el consecutivo del codigo en
     * la tabla ev_tipo_funciones 0000x
     */
    private long consecutivo;
    /* MAP que me recibe los datos de formulario padre */
    Map<String, Object> parametroRecibidos = new TreeMap<>();

    private String tipoCompetencia;
    private String numeroManual;
    private String version;
    private String nombreMotivo;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * FrmaccionesdemejoracomportamentalesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmaccionesdemejoracomportamentalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        consecutivo = 0;
        parametroRecibidos = SessionUtil.getFlash();

        if (parametroRecibidos != null) {
            ridDatosPersonales = (Map<String, Object>) parametroRecibidos
                            .get("rid");
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMEVACCIONESMEJORASCOMPORTAMENTALES_CONTROLADOR
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
        cargarListacmbCompetencia();
        cargarListaMotivo();
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
        enumBase = GenericUrlEnum.EV_ACCIONES_MEJORA_COMPORTAMEN;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado
                        .put(FrmaccionesdemejorasControladorEnum.NUMEROEVALUACION
                                        .getValue(),
                                        parametroRecibidos.get(
                                                        "numeroEvaluacion"));

        parametrosListado
                        .put(FrmaccionesdemejorasControladorEnum.CLASEEVALUACION
                                        .getValue(),
                                        parametroRecibidos.get(
                                                        "claseEvaluacion"));

        parametrosListado.put(FrmaccionesdemejorasControladorEnum.TIPOEVALUACION
                        .getValue(),
                        parametroRecibidos.get("tipoEvaluacion"));

        parametrosListado
                        .put(FrmaccionesdemejorasControladorEnum.CEDULAEVALUADOR
                                        .getValue(),
                                        parametroRecibidos.get(
                                                        "cedulaEvaluador"));

        parametrosListado.put(FrmaccionesdemejorasControladorEnum.CEDULAEVALUADO
                        .getValue(),
                        parametroRecibidos.get("cedulaEvaluado"));

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbCompetencia
     *
     */
    public void cargarListacmbCompetencia() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmaccionesdemejorasControladorUrlEnum.URL100
                                                            .getValue());

            listacmbCompetencia = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmaccionesdemejorasControladorEnum.EV_COMPETENCIAS
                                                            .getValue()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listacmbEvaluadorComision
     *
     */
    public void cargarListaMotivo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmaccionesdemejorasControladorUrlEnum.URL15084
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        parametroRecibidos.get("claseEvaluacion"));

        listaMotivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCompetencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCompetencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("ID_COMPETENCIA",
                        registroAux.getCampos()
                                        .get("CONSECUTIVO"));

        tipoCompetencia = SysmanFunciones.toString(
                        registroAux.getCampos()
                                        .get(FrmaccionesdemejorasControladorEnum.TIPO_COMPETENCIA
                                                        .getValue()));
        numeroManual = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(FrmaccionesdemejorasControladorEnum.NUMERO_MANUAL
                                                        .getValue()));
        version = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(FrmaccionesdemejorasControladorEnum.VERSION
                                                        .getValue()));

        registro.getCampos().put(
                        FrmaccionesdemejorasControladorEnum.VERSION.getValue(),
                        version);

        registro.getCampos().put(
                        "DESCRIPCION",
                        registroAux.getCampos()
                                        .get("DESCRIPCION"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodEmpEvaluado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMotivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MOTIVO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreMotivo = registroAux
                        .getCampos().get(GeneralParameterEnum.NOMBRE.getName())
                        .toString();
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
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if(accion.equals(ACCION_MODIFICAR)) {
            nombreMotivo = registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registro.getCampos().put(
                            FrmaccionesdemejorasControladorEnum.NUMERO_EVALUACION
                                            .getValue(),
                            parametroRecibidos.get("numeroEvaluacion"));

            registro.getCampos().put(
                            FrmaccionesdemejorasControladorEnum.CLASE_EVALUACION
                                            .getValue(),
                            parametroRecibidos.get("claseEvaluacion"));

            registro.getCampos().put("TIPO_EVALUACION",
                            parametroRecibidos.get("tipoEvaluacion"));

            registro.getCampos().put("CEDULA_EVALUADO",
                            parametroRecibidos.get("cedulaEvaluado"));

            registro.getCampos().put("SUCURSAL_EVALUADO",
                            parametroRecibidos.get("sucursalEvaluado"));

            registro.getCampos().put("CEDULA_EVALUADOR",
                            parametroRecibidos.get("cedulaEvaluador"));

            registro.getCampos().put("SUCURSAL_EVALUADOR",
                            parametroRecibidos.get("sucursalEvaluador"));

            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(

                            GenericUrlEnum.EV_ACCIONES_MEJORA_COMPORTAMEN
                                            .getTable(),
                            SysmanFunciones.concatenar("COMPANIA=''", compania,
                                            "''"),
                            FrmaccionesdemejorasControladorEnum.CONSECUTIVO
                                            .getValue());

            registro.getCampos()
                            .put(FrmaccionesdemejorasControladorEnum.CONSECUTIVO
                                            .getValue(),
                                            consecutivo);

            registro.getCampos()
                            .put(FrmaccionesdemejorasControladorEnum.CONSECUTIVO_COMPETENCIA
                                            .getValue(),
                                            consecutivo);

            registro.getCampos()
                            .put(FrmaccionesdemejorasControladorEnum.TIPO_COMPETENCIA
                                            .getValue(),
                                            tipoCompetencia);

            registro.getCampos()
                            .put(FrmaccionesdemejorasControladorEnum.NUMERO_MANUAL
                                            .getValue(),
                                            numeroManual);

            registro.getCampos().put(FrmaccionesdemejorasControladorEnum.VERSION
                            .getValue(),
                            version);

            registro.getCampos().remove("DESCRIPCION");
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
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        
       
        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    public void prueba(Object x) {
        System.out.println("gdf");
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(FrmaccionesdemejorasControladorEnum.NUMERO_EVALUACION
                                            .getValue());
            registro.getCampos()
                            .remove(FrmaccionesdemejorasControladorEnum.CLASE_EVALUACION
                                            .getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.TIPO.getName());
            registro.getCampos()
                            .remove("ID_COMPETENCIA");
            registro.getCampos()
            .remove(GeneralParameterEnum.NOMBRE.getName());
            String aux= new String("sdd");
            prueba(aux);
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("tipo", parametroRecibidos.get("tipoEvaluacion"));
        parametros.put("evaluacion",
                        parametroRecibidos.get("numeroEvaluacion"));

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCompetencia
     * 
     * @return listacmbCompetencia
     */
    public RegistroDataModelImpl getListacmbCompetencia() {
        return listacmbCompetencia;
    }

    /**
     * Asigna la lista listacmbCompetencia
     * 
     * @param listacmbCompetencia
     * Variable a asignar en listacmbCompetencia
     */
    public void setListacmbCompetencia(
        RegistroDataModelImpl listacmbCompetencia) {
        this.listacmbCompetencia = listacmbCompetencia;
    }

    public RegistroDataModelImpl getListaMotivo() {
        return listaMotivo;
    }

    public void setListaMotivo(RegistroDataModelImpl listaMotivo) {
        this.listaMotivo = listaMotivo;
    }

    public String getNombreMotivo() {
        return nombreMotivo;
    }

    public void setNombreMotivo(String nombreMotivo) {
        this.nombreMotivo = nombreMotivo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
