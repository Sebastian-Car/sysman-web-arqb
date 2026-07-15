/*-
 * FrmevrequisitosControlador.java
 *
 * 1.0
 *
 * 17/01/2018
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
import com.sysman.hojasdevida.enums.FrmevmanualsControladorEnum;
import com.sysman.hojasdevida.enums.FrmevrequisitosControladorEnum;
import com.sysman.hojasdevida.enums.FrmevrequisitosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase que permite gestionar la forma "Requisitos" de
 * "Manual de Funciones".
 *
 * @version 1.0, 17/01/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class FrmevrequisitosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // <DECLARAR_ATRIBUTOS>s
    /**
     * Variable que almacena el registro seleccionado en la lista
     * Número Manual.
     */
    private String numeroManual;
    // <DECLARAR_LISTAS>
    /**
     * Declaracion Lista Version de Manual
     */
    private List<Registro> listacmbVersion;
    /**
     * Declaracion Lista Tipo Requisito
     */
    private List<Registro> listaCmbTrequisito;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Declaraci�n Lista Número de Manual.
     */
    private RegistroDataModelImpl listacmbNumeroManual;
    /**
     * Guarda la llave del registro del manual para redireccionar al
     * formulario frmevmanualsControlador
     */
    private Map<String, Object> ridManual;

    /**
     * permite bloquear el combo numero manual al entrar desde el
     * formulario frmevmanualsControlador
     */
    private boolean bloqNumero;

    /**
     * nombre del manual cargado desde el formulario principal
     */
    private String nombreManual;

    /**
     * sigla del manual cargado desde el formulario principal
     */
    private String sigla;

    // Llamado EJB para invocar funciones.

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmevrequisitosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmevrequisitosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_EVREQUISITOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            bloqNumero = false;
            if (parametrosEntrada != null && parametrosEntrada
                            .get("rid") != null) {
                ridManual = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                nombreManual = SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                                                                .getValue()),
                                                "")
                                .toString();

                sigla = SysmanFunciones.nvl(parametrosEntrada
                                .get(GeneralParameterEnum.SIGLA.getName()), "")
                                .toString();
                parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                                ridManual.get(FrmevmanualsControladorEnum.KEY_NUMERO_MANUAL
                                                .getValue()));
                parametrosListado.put(
                                FrmevmanualsControladorEnum.VERSION.getValue(),
                                ridManual.get(FrmevmanualsControladorEnum.KEY_VERSION
                                                .getValue()));
                bloqNumero = true;
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
        cargarListacmbNumeroManual();
        cargarListaCmbTrequisito();
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
        enumBase = GenericUrlEnum.EV_REQUISITOS;
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
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacmbNumeroManual
     *
     *
     */
    public void cargarListacmbNumeroManual() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevrequisitosControladorUrlEnum.URL101
                                                        .getValue());
        Map<String, Object> paramNM = new TreeMap<>();
        paramNM.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listacmbNumeroManual = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), paramNM, true,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmevrequisitosControladorEnum.EV_MANUAL
                                                            .getValue()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCmbTrequisito
     *
     */
    public void cargarListaCmbTrequisito() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaCmbTrequisito = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmevrequisitosControladorUrlEnum.URL103
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbNumeroManual
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbNumeroManual(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmevrequisitosControladorEnum.NUMERO_MANUAL.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevrequisitosControladorEnum.NUMERO_MANUAL
                                                        .getValue()));
        numeroManual = registroAux.getCampos().get(
                        FrmevrequisitosControladorEnum.NUMERO_MANUAL.getValue())
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.SIGLA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.SIGLA.getName()));
        registro.getCampos().put(
                        FrmevrequisitosControladorEnum.NOMBRE_MANUAL.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                                                        .getValue()));
        registro.getCampos().put(
                        FrmevrequisitosControladorEnum.VERSION.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevrequisitosControladorEnum.VERSION
                                                        .getValue()));
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
        numeroManual = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(FrmevrequisitosControladorEnum.NUMERO_MANUAL
                                                        .getValue()),
                                        "")
                        .toString();

        if (ACCION_INSERTAR.equals(accion) && ridManual != null) {
            registro.getCampos()
                            .put(FrmevmanualsControladorEnum.NUMERO_MANUAL
                                            .getValue(),
                                            ridManual.get(FrmevmanualsControladorEnum.KEY_NUMERO_MANUAL
                                                            .getValue()));
            registro.getCampos()
                            .put(FrmevmanualsControladorEnum.VERSION
                                            .getValue(),
                                            ridManual.get(FrmevmanualsControladorEnum.KEY_VERSION
                                                            .getValue()));

            registro.getCampos().put(GeneralParameterEnum.SIGLA.getName(),
                            sigla);
            registro.getCampos()
                            .put(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                                            .getValue(), nombreManual);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return El consecutivo de acuerdo al criterio asignado.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // el consecutivo se debe generar aca ya que depende del
        // manual seleccionado en el combo
        long consecutivo = 1;
        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            FrmevrequisitosControladorEnum.EV_REQUISITOS
                                            .getValue(),
                            SysmanFunciones.concatenar(
                                            "COMPANIA = ''" + compania,
                                            "'' AND NUMERO_MANUAL = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevrequisitosControladorEnum.NUMERO_MANUAL
                                                                                            .getValue()),
                                                            "").toString(),
                                            "'' AND VERSION = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevrequisitosControladorEnum.VERSION
                                                                                            .getValue()),
                                                            "").toString(),
                                            "'' AND TIPO_REQUISITO = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevrequisitosControladorEnum.TIPO_REQUISITO
                                                                                            .getValue()),
                                                            "").toString(),
                                            "'' AND ALTERNATIVA = ''",
                                            SysmanFunciones.nvl(
                                                            registro.getCampos()
                                                                            .get(FrmevrequisitosControladorEnum.ALTERNATIVA
                                                                                            .getValue()),
                                                            "").toString(),
                                            "''"),
                            GeneralParameterEnum.CONSECUTIVO.getName());

            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void ejecutarrcCerrar() {
        String menuActual = SessionUtil.getMenuActual();
        if ("21070101".equals(menuActual)) {
            Direccionador direccionador = new Direccionador();
            HashMap<String, Object> param = new HashMap<>();
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.FRM_EVMANUAL_CONTROLADOR
                                            .getCodigo()));
            param.put("rid", ridManual);
            direccionador.setParametros(param);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {

            SessionUtil.redireccionarMenu();
        }
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return True en caso de requerir acciones después de insertar.
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
     * @return True Al momento de actualizar un registro seleccionado.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());

        }
        registro.getCampos()
                        .remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos()
                        .remove(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                                        .getValue());

        registro.getCampos().remove(GeneralParameterEnum.SIGLA.getName());
        registro.getCampos().remove(FrmevrequisitosControladorEnum.NOMBRE_MANUAL
                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     *
     * @return True en caso de requerir acciones después de insertar.
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
     * @return True para acciones previas a la elliminaci�n de un
     * registro.
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
     * @return True para acciones posteriores a la eliminaci�n de un
     * registro.
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * @return the numeroManual
     */
    public String getNumeroManual() {
        return numeroManual;
    }

    /**
     * @param numeroManual
     * the numeroManual to set
     */
    public void setNumeroManual(String numeroManual) {
        this.numeroManual = numeroManual;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listacmbVersion
     *
     * @return listacmbVersion
     */
    public List<Registro> getListacmbVersion() {
        return listacmbVersion;
    }

    /**
     * @return the listaCmbTrequisito
     */
    public List<Registro> getListaCmbTrequisito() {
        return listaCmbTrequisito;
    }

    /**
     * @param listaCmbTrequisito
     * the listaCmbTrequisito to set
     */
    public void setListaCmbTrequisito(List<Registro> listaCmbTrequisito) {
        this.listaCmbTrequisito = listaCmbTrequisito;
    }

    /**
     * @return the ejbSysmanUtil
     */
    public EjbSysmanUtilRemote getEjbSysmanUtil() {
        return ejbSysmanUtil;
    }

    /**
     * @param ejbSysmanUtil
     * the ejbSysmanUtil to set
     */
    public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
        this.ejbSysmanUtil = ejbSysmanUtil;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * Asigna la lista listacmbVersion
     *
     * @param listacmbVersion
     * Variable a asignar en listacmbVersion
     */
    public void setListacmbVersion(List<Registro> listacmbVersion) {
        this.listacmbVersion = listacmbVersion;
    }

    /**
     * @return the listacmbNumeroManual
     */
    public RegistroDataModelImpl getListacmbNumeroManual() {
        return listacmbNumeroManual;
    }

    /**
     * @param listacmbNumeroManual
     * the listacmbNumeroManual to set
     */
    public void setListacmbNumeroManual(
        RegistroDataModelImpl listacmbNumeroManual) {
        this.listacmbNumeroManual = listacmbNumeroManual;
    }

    @Override
    public Map<String, Object> getRid() {
        return rid;
    }

    @Override
    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public boolean isBloqNumero() {
        return bloqNumero;
    }

    public void setBloqNumero(boolean bloqNumero) {
        this.bloqNumero = bloqNumero;
    }

    public String getNombreManual() {
        return nombreManual;
    }

    public void setNombreManual(String nombreManual) {
        this.nombreManual = nombreManual;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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
