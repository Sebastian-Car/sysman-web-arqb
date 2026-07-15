/*-
 * FrmplanaccionestpreviosControlador.java
 *
 * 1.0
 * 
 * 05/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EpitemsestproysControladorEnum;
import com.sysman.precontractual.enums.FrmplanaccionestpreviosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * En este formulario de configuran los items de cada estudio previo
 *
 * @version 1.0, 05/10/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmplanaccionestpreviosControlador extends BeanBaseDatosAcmeImpl {
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
    /**
     * Listado de listaCmbVigencia
     */
    private List<Registro> listaCmbVigencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de listaCmbPlandIndicativo
     */
    private RegistroDataModelImpl listaCmbPlandIndicativo;
    /**
     * Listado de listaCmbPlanAdquisiciones
     */
    private RegistroDataModelImpl listaCmbPlanAdquisiciones;
    /**
     * parametros enviados desde el controlador que se llama este
     * formulario
     */
    private Map<String, Object> parametrosRecibidos;
    /**
     * variable que recibe el numero del estudio
     */
    private String numeroEstudio;
    /**
     * CONSTANTES
     */
    private String cEstudio;
    private String cTablaPlanIndicativo;
    private String cNombrePlanAdquisicion;
    private String cFichaBPIN;
    private String cSaldoItem;
    private String cActividadPlanIndicativo;

    /**
     * Variable que almacena el modulo
     */
    private final String modulo;
    /**
     * Variable que almacena la vigencia del estudio
     */
    private String vigenciaPeriodo;
    /**
     * Variable para redireccionar atras
     */
    private HashMap<String, Object> llaveRid;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmplanaccionestpreviosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmplanaccionestpreviosControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosRecibidos = SessionUtil.getFlash();
        cEstudio = "ESTUDIO";
        cTablaPlanIndicativo = "BP_PLAN_INDICATIVO";
        cNombrePlanAdquisicion = "NOMBRE_PLAN_ADQUISICION";
        cFichaBPIN = "FICHA_BPIN";
        cSaldoItem = "SALDO_ITEM";
        cActividadPlanIndicativo = "ACTIVIDAD_PLAN_INDICATIVO";
        modulo = SessionUtil.getModulo();

        if (parametrosRecibidos != null) {
            numeroEstudio = SysmanFunciones
                            .nvl(parametrosRecibidos.get("numeroEstudio"), "0")
                            .toString();
            vigenciaPeriodo = SysmanFunciones
                            .nvl(parametrosRecibidos.get("vigenciaPeriodo"), "0")
                            .toString();

            llaveRid = (HashMap<String, Object>) parametrosRecibidos.get(
                            "rid");
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_PLANACCIONESTPREVIO_CONTROLADOR.getCodigo();
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
        // <CARGAR_LISTA>
        cargarListaCmbVigencia();
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
        enumBase = GenericUrlEnum.ESTPREVIO_PLANACCION;
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

        parametrosListado.put(cEstudio,
                        numeroEstudio);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbVigencia
     *
     */
    public void cargarListaCmbVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaCmbVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmplanaccionestpreviosControladorUrlEnum.URL001.getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCmbPlandIndicativo
     *
     */
    public void cargarListaCmbPlandIndicativo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmplanaccionestpreviosControladorUrlEnum.URL002.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        registro.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()));

        try {
            listaCmbPlandIndicativo = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            cTablaPlanIndicativo));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCmbPlanAdquisiciones
     *
     */
    public void cargarListaCmbPlanAdquisiciones() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmplanaccionestpreviosControladorUrlEnum.URL003.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        registro.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()));

        try {
            listaCmbPlanAdquisiciones = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            "BP_PLAN_ADQUISICIONES_VIGENCIA"));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CmbVigencia
     * 
     * 
     */
    public void cambiarCmbVigencia() {
        // <CODIGO_DESARROLLADO>
        listaCmbPlanAdquisiciones = null;
        listaCmbPlandIndicativo = null;
        registro.getCampos().put(cNombrePlanAdquisicion, null);
        registro.getCampos().put(cActividadPlanIndicativo, null);
        registro.getCampos().put(cFichaBPIN, null);
        registro.getCampos().put(cSaldoItem, null);
        registro.getCampos().put("ID_PLAN", null);
        registro.getCampos().put("PLAN_INDICATIVO", null);

        cargarListaCmbPlandIndicativo();
        cargarListaCmbPlanAdquisiciones();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbPlandIndicativo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbPlandIndicativo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PLAN_INDICATIVO", registroAux.getCampos().get("ID"));
        registro.getCampos().put(cActividadPlanIndicativo, registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
        registro.getCampos().put(cFichaBPIN, registroAux.getCampos().get("CODIGOBPIM"));
        registro.getCampos().put("VIGENCIA_INICIAL_PI", registroAux.getCampos().get("VIGENCIA_INICIAL"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbPlanAdquisiciones
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbPlanAdquisiciones(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ID_PLAN.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.ID_PLAN.getName()));
        registro.getCampos().put(cNombrePlanAdquisicion, registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
        registro.getCampos().put(cSaldoItem, registroAux.getCampos().get("VALOR_ESTIMADO"));
        registro.getCampos().put("VIGENCIA_INICIAL_PAV", registroAux.getCampos().get("VIGENCIA_INICIAL"));
        registro.getCampos().put("CODIGO_PAV", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("VIGENCIA_PAV", registroAux.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()));

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
        registro.getCampos().remove(cActividadPlanIndicativo);
        registro.getCampos().remove(cNombrePlanAdquisicion);
        registro.getCampos().remove(cFichaBPIN);
        registro.getCampos().remove(cSaldoItem);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(), numeroEstudio);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        accion = "v";
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
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridEstPrevios",
                        llaveRid);
        parametros.put(EpitemsestproysControladorEnum.TXT_COD_ESTUDIOLOWER
                        .getValue(), numeroEstudio);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCmbVigencia
     * 
     * @return listaCmbVigencia
     */
    public List<Registro> getListaCmbVigencia() {
        return listaCmbVigencia;
    }

    /**
     * Asigna la lista listaCmbVigencia
     * 
     * @param listaCmbVigencia
     * Variable a asignar en listaCmbVigencia
     */
    public void setListaCmbVigencia(List<Registro> listaCmbVigencia) {
        this.listaCmbVigencia = listaCmbVigencia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCmbPlandIndicativo
     * 
     * @return listaCmbPlandIndicativo
     */
    public RegistroDataModelImpl getListaCmbPlandIndicativo() {
        return listaCmbPlandIndicativo;
    }

    /**
     * Asigna la lista listaCmbPlandIndicativo
     * 
     * @param listaCmbPlandIndicativo
     * Variable a asignar en listaCmbPlandIndicativo
     */
    public void setListaCmbPlandIndicativo(RegistroDataModelImpl listaCmbPlandIndicativo) {
        this.listaCmbPlandIndicativo = listaCmbPlandIndicativo;
    }

    /**
     * Retorna la lista listaCmbPlanAdquisiciones
     * 
     * @return listaCmbPlanAdquisiciones
     */
    public RegistroDataModelImpl getListaCmbPlanAdquisiciones() {
        return listaCmbPlanAdquisiciones;
    }

    /**
     * Asigna la lista listaCmbPlanAdquisiciones
     * 
     * @param listaCmbPlanAdquisiciones
     * Variable a asignar en listaCmbPlanAdquisiciones
     */
    public void setListaCmbPlanAdquisiciones(RegistroDataModelImpl listaCmbPlanAdquisiciones) {
        this.listaCmbPlanAdquisiciones = listaCmbPlanAdquisiciones;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
