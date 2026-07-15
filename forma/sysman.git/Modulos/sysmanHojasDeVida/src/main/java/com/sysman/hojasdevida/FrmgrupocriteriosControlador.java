/*-
 * FrmgrupocriteriosControlador.java
 *
 * 1.0
 * 
 * 30/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmgrupocriteriosControladorEnum;
import com.sysman.hojasdevida.enums.FrmgrupocriteriosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Clase para listar, ingresar, modificar y eliminar datos de la tabla
 * EV_CRITERIO_GRUPO. Los parámetros a usar son el código de grupo,
 * que proviene del formulario grupo, y la clase de evaluación que
 * proviene de la sesión.
 *
 * @version 1.0, 30/01/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class FrmgrupocriteriosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compañía en la cual inició sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente
     */
    private final String compania;

    private String grupo;
    private String claseEvaluacion;
    private String nombreCriterio;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de código de criterio.
     */
    private RegistroDataModelImpl listacmbCodigoCriterio;
    /**
     * Lista auxiliar de código de criterio.
     */
    private RegistroDataModelImpl listacmbCodigoCriterioE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * seleccionó
     */
    private String auxiliar;

    /**
     * Atributo que almacena el valor del indicador escompromiso del
     * padre de grupo de criterio de evaluacion
     */
    private boolean escompromiso;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmgrupocriteriosControlador
     */
    public FrmgrupocriteriosControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            grupo = (String) parametros.get("GRUPO");
        }
        claseEvaluacion = (String) SessionUtil.getSessionVar("claseEvaluacion");
        try {
            /** Formulario no 1646. */
            numFormulario = GeneralCodigoFormaEnum.FRM_GRUPO_CRITERIO_CONTROLADOR
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
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualización del
     * formulario, como son tablas, origenes de datos, inicialización
     * de listas y demás necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.EV_CRITERIO_GRUPO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbCodigoCriterio();
        cargarListacmbCodigoCriterioE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este método se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. También carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(
                        FrmgrupocriteriosControladorEnum.COMPANIA.getValue(),
                        compania);
        parametrosListado.put(
                        FrmgrupocriteriosControladorEnum.CLASE_EVALUACION
                                        .getValue(),
                        claseEvaluacion);
        parametrosListado.put(
                        FrmgrupocriteriosControladorEnum.GRUPO.getValue(),
                        grupo);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbCodigoCriterio
     *
     */
    public void cargarListacmbCodigoCriterio() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmgrupocriteriosControladorUrlEnum.URL143
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(FrmgrupocriteriosControladorEnum.COMPANIA.getValue(),
                        String.valueOf(compania));

        param.put(FrmgrupocriteriosControladorEnum.CLASE_EVALUACION
                        .getValue(),
                        claseEvaluacion);

        listacmbCodigoCriterio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmgrupocriteriosControladorEnum.CODIGO.getValue());
    }

    /**
     * 
     * Carga la lista listacmbCodigoCriterio
     *
     */
    public void cargarListacmbCodigoCriterioE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmgrupocriteriosControladorUrlEnum.URL143
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(FrmgrupocriteriosControladorEnum.COMPANIA.getValue(),
                        String.valueOf(compania));

        param.put(FrmgrupocriteriosControladorEnum.CLASE_EVALUACION
                        .getValue(),
                        claseEvaluacion);

        listacmbCodigoCriterioE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmgrupocriteriosControladorEnum.CODIGO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Método ejecutado al cambiar el control cmbCodigoCriterio en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * índice de la fila seleccionada
     */
    public void cambiarcmbCodigoCriterioC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmgrupocriteriosControladorEnum.NOMBRE
                                        .getValue(), nombreCriterio);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Método ejecutado al seleccionar una fila de la lista
     * listacmbCodigoCriterio
     *
     * @param event
     * objeto que encapsula la acción proveniente de la vista
     */
    public void seleccionarFilacmbCodigoCriterio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmgrupocriteriosControladorEnum.CODIGO_CRITERIO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        FrmgrupocriteriosControladorEnum.CODIGO
                                                                        .getValue()));
        registro.getCampos()
                        .put(FrmgrupocriteriosControladorEnum.NOMBRE
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmgrupocriteriosControladorEnum.NOMBRE
                                                                        .getValue()));

        escompromiso = (boolean) registroAux.getCampos().get("ESCOMPROMISO");
    }

    /**
     * 
     * Método ejecutado al seleccionar una fila de la lista
     * listacmbCodigoCriterio
     *
     * @param event
     * objeto que encapsula la acción proveniente de la vista
     */
    public void seleccionarFilacmbCodigoCriterioE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                        FrmgrupocriteriosControladorEnum.CODIGO.getValue()), "")
                        .toString();
        nombreCriterio = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmgrupocriteriosControladorEnum.NOMBRE
                                                        .getValue()),
                                        "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este método es invocado en el método inicializar, se ejecutan
     * las acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método ejecutado cuando se cancela la edición del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Método ejecutado antes de realizar la inserción del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        if (!validarGrupo()) {
            return false;
        }

        registro.getCampos().remove(
                        FrmgrupocriteriosControladorEnum.NOMBRE.getValue());
        registro.getCampos().put(
                        FrmgrupocriteriosControladorEnum.COMPANIA.getValue(),
                        compania);
        registro.getCampos().put("CLASE_EVALUACION", claseEvaluacion);
        registro.getCampos().put(
                        FrmgrupocriteriosControladorEnum.GRUPO.getValue(),
                        grupo);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarGrupo() {

        if (escompromiso) {

            String codigo = registro.getCampos()
                            .get(FrmgrupocriteriosControladorEnum.CODIGO_CRITERIO
                                            .getValue())
                            .toString();

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(FrmgrupocriteriosControladorEnum.CLASE_EVALUACION
                            .getValue(),
                            claseEvaluacion);

            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

            try {
                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmgrupocriteriosControladorUrlEnum.URL4444
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.parseInt(SysmanFunciones
                                .nvl(reg.getCampos().get("PUNTAJE"), "0")
                                .toString()) > 100) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4066"));

                    return false;
                }
                else if (Integer.parseInt(SysmanFunciones
                                .nvl(reg.getCampos().get("PUNTAJE"), "0")
                                .toString()) < 100) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4067"));

                    return false;
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la inserción y actualización
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
     * Método ejecutado después de realizar la inserción y
     * actualización del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la eliminación del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la eliminación del
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
     * Este método se ejecuta antes enviar la acción de actualización,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(
                        FrmgrupocriteriosControladorEnum.NOMBRE.getValue());
        registro.getCampos().remove(
                        FrmgrupocriteriosControladorEnum.COMPANIA.getValue());
    }

    /**
     * Método ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este método es ejecutado después de finalizar la inserción y
     * edición del registro, se usa cuando se desean agregar valores
     * al registro después de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // No hay código aquí.
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigoCriterio
     * 
     * @return listacmbCodigoCriterio
     */
    public RegistroDataModelImpl getListacmbCodigoCriterio() {
        return listacmbCodigoCriterio;
    }

    /**
     * Asigna la lista listacmbCodigoCriterio
     * 
     * @param listacmbCodigoCriterio
     * Variable a asignar en listacmbCodigoCriterio
     */
    public void setListacmbCodigoCriterio(
        RegistroDataModelImpl listacmbCodigoCriterio) {
        this.listacmbCodigoCriterio = listacmbCodigoCriterio;
    }

    /**
     * Retorna la lista listacmbCodigoCriterio
     * 
     * @return listacmbCodigoCriterio
     */
    public RegistroDataModelImpl getListacmbCodigoCriterioE() {
        return listacmbCodigoCriterioE;
    }

    /**
     * Asigna la lista listacmbCodigoCriterio
     * 
     * @param listacmbCodigoCriterio
     * Variable a asignar en listacmbCodigoCriterio
     */
    public void setListacmbCodigoCriterioE(
        RegistroDataModelImpl listacmbCodigoCriterioE) {
        this.listacmbCodigoCriterioE = listacmbCodigoCriterioE;
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
