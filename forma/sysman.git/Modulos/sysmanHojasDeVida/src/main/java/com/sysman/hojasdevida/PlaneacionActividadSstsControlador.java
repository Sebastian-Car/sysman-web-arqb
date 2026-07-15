/*-
 * PlaneacionActividadSstsControlador.java
 *
 * 1.0
 * 
 * 03/01/2018
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
import com.sysman.hojasdevida.enums.PlaneacionActividadSstsControladorEnum;
import com.sysman.hojasdevida.enums.PlaneacionActividadSstsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
 * Formulario que permite administrar la planeacion de la induccion y
 * de la capacitacion SGSST
 *
 * @version 1.0, 03/01/2018
 * @author jreina
 * 
 * @version 1.1, 02/02/2018
 * @modifier amonroy, Se crea el metodo definirTipoActividad() y se
 * realizan ajustes en la forma adicionando el campo "OBJETIVO"
 */

@ManagedBean
@ViewScoped
public class PlaneacionActividadSstsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado tipo de planeacion de
     * la actividad
     */
    private String tipo;
    /**
     * Atributo que contiene el valor asignado al nombre del area de
     * la actividad.
     */
    private String nombreArea;
    /**
     * Atributo que contiene el valor asignado al titulo de la forma
     * del formulario.
     */
    private String titulo;
    /**
     * Atributo que contiene el valor asignado para hacer visible el
     * campo observaciones
     */
    private boolean visibleObservacion;

    private boolean visibleNivel;

    private boolean verAnexos;

    // private String modulo = SessionUtil.getModulo();

    // private String actividades;

    // private int ano;

    private String cTipo = PlaneacionActividadSstsControladorEnum.TIPO
                    .getValue();

    private String cActividad = PlaneacionActividadSstsControladorEnum.ACTIVIDADES
                    .getValue();

    private String cAno = PlaneacionActividadSstsControladorEnum.ANO.getValue();

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los anios. */
    private List<Registro> listaAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene las diferentes areas. */
    private RegistroDataModelImpl listaArea;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlaneacionActividadSstsControlador
     */
    public PlaneacionActividadSstsControlador() {
        super();
        compania = SessionUtil.getCompania();
        definirTipoActividad();
        try {
            numFormulario = GeneralCodigoFormaEnum.PLANEACION_ACTIVIDADSSTS_CONTROLADOR
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaArea();
        cargarListaAno();
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
        enumBase = GenericUrlEnum.NAT_PLANEACIONACTIVIDADES;
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
                        .put(PlaneacionActividadSstsControladorEnum.TIPO_ACTIVIDAD
                                        .getValue(), tipo);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaArea
     *
     */
    public void cargarListaArea() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlaneacionActividadSstsControladorUrlEnum.URL4213
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaArea = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PlaneacionActividadSstsControladorUrlEnum.URL4454
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
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
     * Metodo ejecutado al seleccionar una fila de la lista listaArea
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaArea(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        PlaneacionActividadSstsControladorEnum.AREA.getValue(),
                        retornarString(registroAux,
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreArea = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton anexos en la vista
     *
     * 
     *
     */
    public void oprimiranexos() {

        String[] campos = { "tipo", "ano", "actividad" };
        Object[] valores = { registro.getCampos().get(cTipo).toString(),
                             registro.getCampos().get(cAno).toString(),
                             registro.getCampos().get(cActividad).toString() };
        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_DETALLE_ANEXO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    /**
     * Permite definir el Tipo de Actividad que se esta trabajando de
     * acuerdo a la opcion de menu por la que se ingresa al
     * formulario. Tambien permite la definicion del tiulo de
     * formulario
     */
    private void definirTipoActividad() {
        switch (SessionUtil.getMenuActual()) {
        case "210402010102":
            tipo = "1";
            titulo = idioma.getString("TB_TB3961");
            visibleObservacion = true;
            visibleNivel = true;
            verAnexos = true;
            break;

        case "210402010201":
            tipo = "2";
            titulo = idioma.getString("TB_TB3962");
            visibleObservacion = true;
            visibleNivel = true;
            verAnexos = true;
            break;

        case "210402010301":
            tipo = "3";
            titulo = idioma.getString("TB_TB3963");
            visibleNivel = true;
            verAnexos = true;
            break;

        case "210402010401":
            tipo = "4";
            titulo = idioma.getString("TB_TB3964");
            visibleObservacion = true;
            visibleNivel = true;
            verAnexos = true;
            break;

        case "210402020202":
            tipo = "6";
            titulo = idioma.getString("TB_TB3904");
            visibleObservacion = false;
            visibleNivel = true;
            verAnexos = true;
            break;
        case "210802050101":
            tipo = "102";
            titulo = idioma.getString("TB_TB4202");
            visibleObservacion = true;
            visibleNivel = true;
            verAnexos = true;
            break;
        case "210402020101":
            tipo = "7";
            titulo = idioma.getString("TB_TB3903");
            visibleObservacion = true;
            verAnexos = true;

            break;

        case "210802050202":
            tipo = "101";
            titulo = idioma.getString("TB_TB4203");
            verAnexos = false;
            break;

        default:
            verAnexos = false;

            break;
        }

    }

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
        nombreArea = retornarString(registro,
                        GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        PlaneacionActividadSstsControladorEnum.TIPO.getValue(),
                        tipo);
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
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaArea
     * 
     * @return listaArea
     */
    public RegistroDataModelImpl getListaArea() {
        return listaArea;
    }

    /**
     * Asigna la lista listaArea
     * 
     * @param listaArea
     * Variable a asignar en listaArea
     */
    public void setListaArea(RegistroDataModelImpl listaArea) {
        this.listaArea = listaArea;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isVisibleObservacion() {
        return visibleObservacion;
    }

    public void setVisibleObservacion(boolean visibleObservacion) {
        this.visibleObservacion = visibleObservacion;
    }

    /**
     * @return the visibleNivel
     */
    public boolean isVisibleNivel() {
        return visibleNivel;
    }

    /**
     * @param visibleNivel
     * the visibleNivel to set
     */
    public void setVisibleNivel(boolean visibleNivel) {
        this.visibleNivel = visibleNivel;
    }

    /**
     * @return the verAnexos
     */
    public boolean isVerAnexos() {
        return verAnexos;
    }

    /**
     * @param verAnexos
     * the verAnexos to set
     */
    public void setVerAnexos(boolean verAnexos) {
        this.verAnexos = verAnexos;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
