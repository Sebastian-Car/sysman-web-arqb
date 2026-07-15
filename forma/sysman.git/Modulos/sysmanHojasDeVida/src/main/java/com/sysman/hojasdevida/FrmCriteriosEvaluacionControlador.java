/*-
 * FrmCriteriosEvaluacionControlador.java
 *
 * 1.0
 * 
 * 12/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmCriteriosEvaluacionControladorEnum;
import com.sysman.hojasdevida.enums.FrmCriteriosEvaluacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Permiso el registro de los criterios de evaluación.
 *
 * @version 1.0, 12/01/2018
 * @author asana
 * 
 * Se actualiza para visualizar el tipo de Evaluación seleccionado en
 * la opción evaluaciones.
 * 
 * @version 2.0, 08/02/2018
 * @author dnino
 * 
 * Se actualiza consulta y DSS para tomar como variable de sesión la
 * Clase de evaluación.
 * 
 * @version 3.0, 19/02/2018
 * @author dnino
 * 
 * @version 4.0, 30/05/2018, <strong>pespitia</strong>:
 * <li>Se adiciono el combo Copiar De (CB5994) en la grilla.
 * 
 * @version 5.0, 27/07/2018 
 * @author asana
 * Se realiza la validación al momento de crear se asigna clase según menu.
 */
@ManagedBean
@ViewScoped
public class FrmCriteriosEvaluacionControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * abre el formulario.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private String claseEvaluacion;

    private String titulo;

    /**
     * Atributo que administra la edicion del campo de puntaje
     * 
     */
    private boolean bloqueaPuntaje;

    /**
     * Atributo que administra la edicion del campo puntaje maximo
     * 
     */
    private boolean bloqueaPuntajeMaximo;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo que contiene el codigo del registro que esta siendo
     * editado en la grilla.
     */
    private String codigo;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo Copiar De (CB5994) al
     * ingresar un nuevo registro.
     */
    private RegistroDataModelImpl listaCopiarDe;

    /**
     * Lista que contiene los detalles del combo Copiar De (CB5994) al
     * ediatr un registro.
     */
    private RegistroDataModelImpl listaCopiarDeE;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCriteriosEvaluacionControlador
     */
    public FrmCriteriosEvaluacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        claseEvaluacion = (String) SessionUtil
                        .getSessionVar(FrmCriteriosEvaluacionControladorEnum.PARAM0
                                        .getValue());

        bloqueaPuntaje = false;

        bloqueaPuntajeMaximo = false;

        try {
            // 1594
            numFormulario = GeneralCodigoFormaEnum.CRITERIOSEVALUACION_CONTROLADOR
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
        enumBase = GenericUrlEnum.EV_CRITERIOS_EVALUACION;

        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        buscarUrls();
        if ("21090202".equals(SessionUtil.getMenuActual())) {
            parametrosListado.put(FrmCriteriosEvaluacionControladorEnum.PARAM1
                            .getValue(),
                            FrmCriteriosEvaluacionControladorEnum.PARAM4
                                            .getValue());
        }
        else {
            parametrosListado.put(FrmCriteriosEvaluacionControladorEnum.PARAM1
                            .getValue(),
                            claseEvaluacion);
        }
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * Carga la lista: <code>listaCopiarDeE</code> asociada al combo
     * Copiar De (CB5994).
     */
    public void cargarListaCopiarDeE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CLASE_EVALUACION", claseEvaluacion);
        param.put("MI_CODIGO", codigo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCriteriosEvaluacionControladorUrlEnum.URL001
                                                        .getValue());

        listaCopiarDeE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Escompromiso
     * 
     * 
     */
    public void cambiarEscompromiso() {

        if ((boolean) registro.getCampos()
                        .get(FrmCriteriosEvaluacionControladorEnum.ESCOMPROMISO
                                        .getValue())) {

            bloqueaPuntaje = true;
            bloqueaPuntajeMaximo = false;
        }
        else {
            bloqueaPuntaje = false;
            bloqueaPuntajeMaximo = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Escompromiso en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEscompromisoC(int rowNum) {

        boolean esCompromiso = (boolean) listaInicial.getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(FrmCriteriosEvaluacionControladorEnum.ESCOMPROMISO
                                        .getValue());

        if (esCompromiso) {

            bloqueaPuntaje = true;
            bloqueaPuntajeMaximo = false;
        }
        else {
            bloqueaPuntaje = false;
            bloqueaPuntajeMaximo = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control PuntajeMaximo
     * 
     */
    public void cambiarPuntajeMaximo() {

        registro.getCampos().put("PUNTAJE",
                        registro.getCampos().get("PUNTAJEMAXIMO"));

    }

    /**
     * Metodo ejecutado al cambiar el control PuntajeMaximo en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPuntajeMaximoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("PUNTAJE", listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get("PUNTAJEMAXIMO"));

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCopiarDe</code> asociada al combo Copiar De
     * (CB5994).
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCopiarDe(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(FrmCriteriosEvaluacionControladorEnum.COPIAR_DE
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCopiarDeE</code> asociada al combo Copiar De
     * (CB5994). Aplica al editar el registro.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCopiarDeE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
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
        // Consulta que trae el nombre de la evaluación seleccionada.
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGO.getName(), claseEvaluacion);
        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCriteriosEvaluacionControladorUrlEnum.URL405
                                                                            .getValue())
                                            .getUrl(), param));
            // Decisión que trae la Clase de evaluación si proviene de
            // la opción Evaluaciones.
            if ("21090202".equals(SessionUtil.getMenuActual())) {
                titulo = FrmCriteriosEvaluacionControladorEnum.PARAM2
                                .getValue();
            }
            else {

                titulo = FrmCriteriosEvaluacionControladorEnum.PARAM3
                                .getValue()
                    + rs.getCampos().get(GeneralParameterEnum.NOMBRE.getName())
                                    .toString();
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        
        if ("21090202".equals(SessionUtil.getMenuActual())) {
            
            registro.getCampos().put(FrmCriteriosEvaluacionControladorEnum.PARAM1
                            .getValue(), FrmCriteriosEvaluacionControladorEnum.PARAM4
                            .getValue());
        }
        else {
            registro.getCampos().put(FrmCriteriosEvaluacionControladorEnum.PARAM1
                            .getValue(), claseEvaluacion);
        }
        
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(FrmCriteriosEvaluacionControladorEnum.COPIAR_DE
                                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * deban enviar en el registro, se indican los campos a quitar al
     * momento de editar
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmCriteriosEvaluacionControladorEnum.COPIAR_DE
                                        .getValue())) {
            copiarDe();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        cambiarEscompromisoC(indice);

        codigo = registro.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                        .toString();

        cargarListaCopiarDeE();
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Ejecuta el proceso por el cual se copia la configuracion de un
     * criterio a otro.
     */
    private void copiarDe() {
        Map<String, Object> parSet = new TreeMap<>();
        parSet.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parSet.put("MI_CODIGO", codigo);

        parSet.put("MI_COPIARDE",
                        registro.getCampos()
                                        .get(FrmCriteriosEvaluacionControladorEnum.COPIAR_DE
                                                        .getValue()));

        parSet.put("CLASE_EVALUACION", claseEvaluacion);
        parSet.put(GeneralParameterEnum.USUARIO.getName(), usuario);

        Parameter parametro = new Parameter();
        parametro.setFields(parSet);

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCriteriosEvaluacionControladorUrlEnum.URL002
                                                        .getValue());

        try {
            requestManager.save(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parametro);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // <SET_GET_ATRIBUTOS>

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the claseEvaluacion
     */
    public String getClaseEvaluacion() {
        return claseEvaluacion;
    }

    /**
     * @param claseEvaluacion
     * the claseEvaluacion to set
     */
    public void setClaseEvaluacion(String claseEvaluacion) {
        this.claseEvaluacion = claseEvaluacion;
    }

    public boolean isBloqueaPuntaje() {
        return bloqueaPuntaje;
    }

    public void setBloqueaPuntaje(boolean bloqueaPuntaje) {
        this.bloqueaPuntaje = bloqueaPuntaje;
    }

    public boolean isBloqueaPuntajeMaximo() {
        return bloqueaPuntajeMaximo;
    }

    public void setBloqueaPuntajeMaximo(boolean bloqueaPuntajeMaximo) {
        this.bloqueaPuntajeMaximo = bloqueaPuntajeMaximo;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaCopiarDe() {
        return listaCopiarDe;
    }

    public void setListaCopiarDe(RegistroDataModelImpl listaCopiarDe) {
        this.listaCopiarDe = listaCopiarDe;
    }

    public RegistroDataModelImpl getListaCopiarDeE() {
        return listaCopiarDeE;
    }

    public void setListaCopiarDeE(RegistroDataModelImpl listaCopiarDeE) {
        this.listaCopiarDeE = listaCopiarDeE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
