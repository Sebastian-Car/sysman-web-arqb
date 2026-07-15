/*-
 * FrmevevidenciasControlador.java
 *
 * 1.0
 *
 * 07/02/2018
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
import com.sysman.hojasdevida.enums.FrmevevidenciasControladorEnum;
import com.sysman.hojasdevida.enums.FrmevevidenciasControladorUrlEnum;
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
 * Formulario que gestiona las evidencias del detallle de evaluaciones
 *
 * @version 1.0, 07/02/2018
 * @author spina
 *
 * @version 1.5,23/02/2018, Se adiciono el combo aportado por y el
 * campo que tiene el nombre del codigo del aportado por
 * @author eamaya
 *
 */
@ManagedBean
@ViewScoped
public class FrmevevidenciasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el nombre del aportado por
     */
    private String nombreAportadoPor;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     */
    private RegistroDataModelImpl listacmbCargoJefeDir;

    private RegistroDataModelImpl listaAportadoPor;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private String numeroEvaluacion;
    private String claseEvaluacion;
    private String tipoEvaluacion;
    private String cedulaEvaluado;
    private String cedulaEvaluador;
    private String sucursalEvaluado;
    private String sucursalEvaluador;

    private Map<String, Object> ridDatos;

    private long consecutivo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmevevidenciasControlador
     */
    @SuppressWarnings("unchecked")
    public FrmevevidenciasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMEVEVIDENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
                numeroEvaluacion = SysmanFunciones.nvl(
                                parametrosEntrada.get("numeroEvaluacion"), "")
                                .toString();
                claseEvaluacion = SysmanFunciones.nvl(
                                parametrosEntrada.get("claseEvaluacion"), "")
                                .toString();
                tipoEvaluacion = SysmanFunciones.nvl(
                                parametrosEntrada.get("tipoEvaluacion"), "")
                                .toString();
                cedulaEvaluado = SysmanFunciones.nvl(
                                parametrosEntrada.get("cedulaEvaluado"), "")
                                .toString();
                cedulaEvaluador = SysmanFunciones.nvl(
                                parametrosEntrada.get("cedulaEvaluador"), "")
                                .toString();
                sucursalEvaluado = SysmanFunciones.nvl(
                                parametrosEntrada.get("sucursalEvaluado"), "")
                                .toString();
                sucursalEvaluador = SysmanFunciones.nvl(
                                parametrosEntrada.get("sucursalEvaluador"), "")
                                .toString();
            }
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbCargoJefeDir();
        cargarListaAportadoPor();
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
        enumBase = GenericUrlEnum.EV_EVIDENCIAS;
        consecutivo = 1;
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
        parametrosListado.put(FrmevevidenciasControladorEnum.NUMEROEVALUACION
                        .getValue(), numeroEvaluacion);
        parametrosListado.put(FrmevevidenciasControladorEnum.CLASEEVALUACION
                        .getValue(), claseEvaluacion);
        parametrosListado.put(FrmevevidenciasControladorEnum.TIPOEVALUACION
                        .getValue(), tipoEvaluacion);
        parametrosListado.put(FrmevevidenciasControladorEnum.CEDULAEVALUADO
                        .getValue(), cedulaEvaluado);
        parametrosListado.put(FrmevevidenciasControladorEnum.CEDULAEVALUADOR
                        .getValue(), cedulaEvaluador);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacmbCargoJefeDir
     *
     */
    public void cargarListacmbCargoJefeDir() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevevidenciasControladorUrlEnum.URL4130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listacmbCargoJefeDir = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmevevidenciasControladorEnum.EV_COMPETENCIAS
                                                            .getValue()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAportadoPor() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevevidenciasControladorUrlEnum.URL5555
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAportadoPor = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void seleccionarFilacmbCargoJefeDir(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        // registro.getCampos().put(
        // FrmevevidenciasControladorEnum.VERSION.getValue(),
        // registroAux.getCampos()
        // .get(FrmevevidenciasControladorEnum.VERSION
        // .getValue()));
        // registro.getCampos()
        // .put(FrmevevidenciasControladorEnum.TIPO_COMPETENCIA
        // .getValue(),
        // registroAux.getCampos()
        // .get(FrmevevidenciasControladorEnum.TIPO_COMPETENCIA
        // .getValue()));
        // registro.getCampos().put(
        // FrmevevidenciasControladorEnum.NUMERO_MANUAL
        // .getValue(),
        // registroAux.getCampos()
        // .get(FrmevevidenciasControladorEnum.NUMERO_MANUAL
        // .getValue()));
        //
        // registro.getCampos().put(FrmevevidenciasControladorEnum.ID_COMPETENCIA
        // .getValue(),
        // registroAux.getCampos()
        // .get(FrmevevidenciasControladorEnum.CONSECUTIVO
        // .getValue()));
        // registro.getCampos()
        // .put(FrmevevidenciasControladorEnum.NUMERO_EVALUACION
        // .getValue(), numeroEvaluacion);
        // registro.getCampos().put(FrmevevidenciasControladorEnum.TIPO_EVALUACION
        // .getValue(), tipoEvaluacion);
        // registro.getCampos().put(FrmevevidenciasControladorEnum.CLASE_EVALUACION
        // .getValue(), claseEvaluacion);
    }

    public void seleccionarFilaAportadoPor(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("APORTADO_POR",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));

        nombreAportadoPor = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName()),
                        " ")
                        .toString();

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        nombreAportadoPor = null;
        if (accion.equals(ACCION_MODIFICAR) || accion.equals(ACCION_VER)) {
            nombreAportadoPor = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()),
                                            "")
                            .toString();

            // Map<String, Object> params = new TreeMap<>();
            //
            // params.put(GeneralParameterEnum.CODIGO.getName(),
            // registro.getCampos().get("APORTADO_POR"));
            //
            // try
            // {
            // nombreAportadoPor = SysmanFunciones
            // .nvl(listaAportadoPor.getRegistroUnico(params)
            // .getCampos()
            // .get(GeneralParameterEnum.NOMBRE
            // .getName()),
            // "")
            // .toString();
            // }
            // catch (SystemException e)
            // {
            // logger.error(e.getMessage(), e);
            // JsfUtil.agregarMensajeError(e.getMessage());
            // }
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
                        .put(FrmevevidenciasControladorEnum.NUMERO_EVALUACION
                                        .getValue(), numeroEvaluacion);
        registro.getCampos().put(FrmevevidenciasControladorEnum.CLASE_EVALUACION
                        .getValue(), claseEvaluacion);
        registro.getCampos().put(FrmevevidenciasControladorEnum.TIPO_EVALUACION
                        .getValue(), tipoEvaluacion);
        registro.getCampos().put(FrmevevidenciasControladorEnum.CEDULA_EVALUADO
                        .getValue(), cedulaEvaluado);
        registro.getCampos().put(FrmevevidenciasControladorEnum.CEDULA_EVALUADOR
                        .getValue(), cedulaEvaluador);
        registro.getCampos()
                        .put(FrmevevidenciasControladorEnum.SUCURSAL_EVALUADO
                                        .getValue(), sucursalEvaluado);
        registro.getCampos()
                        .put(FrmevevidenciasControladorEnum.SUCURSAL_EVALUADOR
                                        .getValue(), sucursalEvaluador);
        try {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "EV_EVIDENCIAS", "COMPANIA = " + compania
                                + " AND NUMERO_EVALUACION = " +
                                numeroEvaluacion
                                + " AND CLASE_EVALUACION = " + claseEvaluacion
                                + " AND TIPO_EVALUACION = " + tipoEvaluacion,
                            FrmevevidenciasControladorEnum.CONSECUTIVO
                                            .getValue(),
                            "1");
            registro.getCampos()
                            .put(FrmevevidenciasControladorEnum.CONSECUTIVO
                                            .getValue(), consecutivo);
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
        registro.getCampos().remove("VERSION");
        registro.getCampos().remove("ID_COMPETENCIA");
        registro.getCampos().remove("VERSION");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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

    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);
        parametros.put("cedulaEvaluado", cedulaEvaluado);
        parametros.put("cedulaEvaluador", cedulaEvaluador);
        parametros.put("sucursalEvaluado", sucursalEvaluado);
        parametros.put("sucursalEvaluador", sucursalEvaluador);
        parametros.put("tipo", tipoEvaluacion);
        parametros.put("clase", claseEvaluacion);
        parametros.put("evaluacion", numeroEvaluacion);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbCargoJefeDir
     *
     * @return listacmbCargoJefeDir
     */
    public RegistroDataModelImpl getListacmbCargoJefeDir() {
        return listacmbCargoJefeDir;
    }

    /**
     * Asigna la lista listacmbCargoJefeDir
     *
     * @param listacmbCargoJefeDir
     * Variable a asignar en listacmbCargoJefeDir
     */
    public void setListacmbCargoJefeDir(
        RegistroDataModelImpl listacmbCargoJefeDir) {
        this.listacmbCargoJefeDir = listacmbCargoJefeDir;
    }

    public RegistroDataModelImpl getListaAportadoPor() {
        return listaAportadoPor;
    }

    public void setListaAportadoPor(RegistroDataModelImpl listaAportadoPor) {
        this.listaAportadoPor = listaAportadoPor;
    }

    public String getNombreAportadoPor() {
        return nombreAportadoPor;
    }

    public void setNombreAportadoPor(String nombreAportadoPor) {
        this.nombreAportadoPor = nombreAportadoPor;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
