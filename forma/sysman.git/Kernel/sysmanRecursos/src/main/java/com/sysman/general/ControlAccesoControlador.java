/*-
 * ControlAccesoControlador.java
 *
 * 1.0
 * 
 * 8/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ControlAccesoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que administra el control de acceso de los usuarios a la
 * aplicación
 *
 * @version 1.0, 08/05/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ControlAccesoControlador extends BeanBaseModal {

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el codigo del grupo seleccionado en la
     * vista
     */
    private String grupo;
    /**
     * Variable que almacena el codigo de la compania seleccionada en
     * la vista
     */
    private String compania;
    /**
     * Variable que almacena el nombre de la compania seleccionada en
     * la vista
     */
    private String nombreCompania;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los grupos o roles del sistema
     */
    private List<Registro> listaGrupo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las companias
     */
    private RegistroDataModelImpl listaCompania;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ControlAccesoControlador
     */
    public ControlAccesoControlador() {
        super();

        try {
            numFormulario = GeneralCodigoFormaEnum.CONTROL_ACCESO_CONTROLADOR
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
        // <CARGAR_LISTA>
        cargarListaGrupo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCompania();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaGrupo
     *
     */
    public void cargarListaGrupo() {

        try {
            listaGrupo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ControlAccesoControladorUrlEnum.URL3675
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaCompania
     *
     */
    public void cargarListaCompania() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControlAccesoControladorUrlEnum.URL3966
                                                        .getValue());
        listaCompania = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Acceso en la vista
     *
     */
    public void oprimirAcceso() {

        insertarDias();

        Map<String, Object> param = new TreeMap<>();

        param.put("compania", compania);

        param.put("cuenta", grupo);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm("1787");

        RequestContext.getCurrentInstance().closeDialog(direccionador);
    }

    private void insertarDias() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CUENTA.getName(), grupo);

        try {

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ControlAccesoControladorUrlEnum.URL6879
                                                                            .getValue())
                                            .getUrl(), param));

            if ("0".equals(reg.getCampos().get("EXISTE").toString())) {

                for (int i = 0; i <= 6; i++) {

                    String[] dias = { "Domingo", "Lunes", "Martes", "Miercoles",
                                      "Jueves", "Viernes", "Sabado" };

                    Map<String, Object> parametro = new TreeMap<>();

                    parametro.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);

                    parametro.put(GeneralParameterEnum.CUENTA.getName(), grupo);

                    parametro.put(GeneralParameterEnum.NUMERO.getName(), i);

                    parametro.put("DIA", dias[i]);

                    parametro.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                    new Date());

                    parametro.put(GeneralParameterEnum.CREATED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());

                    parametro.put("HORA_INICIAL", SysmanFunciones
                                    .convertirHoraAFecha("00:00"));

                    parametro.put("HORA_FINAL", SysmanFunciones
                                    .convertirHoraAFecha("23:59"));

                    Parameter parameter = new Parameter();

                    parameter.setFields(parametro);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    ControlAccesoControladorUrlEnum.URL8585
                                                                    .getValue());

                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(),
                                    parameter);

                }

            }

        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excepciones en la vista
     *
     *
     */
    public void oprimirExcepciones() {
        Map<String, Object> param = new TreeMap<>();

        param.put("compania", compania);

        param.put("cuenta", grupo);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm("1788");

        RequestContext.getCurrentInstance().closeDialog(direccionador);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Grupo
     * 
     * 
     */
    public void cambiarGrupo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCompania
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCompania(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        compania = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        nombreCompania = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable grupo
     * 
     * @return grupo
     */
    public String getGrupo() {
        return grupo;
    }

    /**
     * Asigna la variable grupo
     * 
     * @param grupo
     * Variable a asignar en grupo
     */
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    /**
     * Retorna la variable compania
     * 
     * @return compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * Asigna la variable compania
     * 
     * @param compania
     * Variable a asignar en compania
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }

    /**
     * Retorna la variable nombreCompania
     * 
     * @return nombreCompania
     */
    public String getNombreCompania() {
        return nombreCompania;
    }

    /**
     * Asigna la variable nombreCompania
     * 
     * @param nombreCompania
     * Variable a asignar en nombreCompania
     */
    public void setNombreCompania(String nombreCompania) {
        this.nombreCompania = nombreCompania;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaGrupo
     * 
     * @return listaGrupo
     */
    public List<Registro> getListaGrupo() {
        return listaGrupo;
    }

    /**
     * Asigna la lista listaGrupo
     * 
     * @param listaGrupo
     * Variable a asignar en listaGrupo
     */
    public void setListaGrupo(List<Registro> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCompania
     * 
     * @return listaCompania
     */
    public RegistroDataModelImpl getListaCompania() {
        return listaCompania;
    }

    /**
     * Asigna la lista listaCompania
     * 
     * @param listaCompania
     * Variable a asignar en listaCompania
     */
    public void setListaCompania(RegistroDataModelImpl listaCompania) {
        this.listaCompania = listaCompania;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
