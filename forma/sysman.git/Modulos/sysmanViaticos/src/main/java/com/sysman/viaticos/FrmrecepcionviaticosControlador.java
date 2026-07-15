/*-
 * FrmrecepcionviaticosControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.viaticos.enums.FrmComisionesControladorEnum;
import com.sysman.viaticos.enums.FrmViaticosControladorEnum;
import com.sysman.viaticos.enums.FrmrecepcionviaticosControladorUrlEnum;

/**
 * Formlario que se encarga de recibir las legalizaciones de viaticos
 *
 * @version 1.0, 18/09/2018
 * @author jromero
 * 
 * @version 2.0, 19/09/2018
 * @author eamaya,Proceso de Refactoring DSS y ajustes de diseño de
 * forma
 */
@ManagedBean
@ViewScoped
public class FrmrecepcionviaticosControlador extends BeanBaseDatosAcmeImpl {
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
     * Lista de pais origen
     */
    private List<Registro> listaPaisOrigen;
    /**
     * Lista de pais destino
     */
    private List<Registro> listaPaisDestino;
    /**
     * Lista de departamento origen
     */
    private List<Registro> listaDepartamentoOrigen;
    /**
     * Lista de departamento destino
     */
    private List<Registro> listaDepartamentoDestino;
    /**
     * Lista de ciudad origen
     */
    private List<Registro> listaCiudadOrigen;
    /**
     * Lista de ciudad destino
     */
    private List<Registro> listaCiudadDestino;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Atributo que administra la visibilidad del campo total pesos
     */
    private boolean visiblePesos;

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
     * Crea una nueva instancia de FrmrecepcionviaticosControlador
     */
    public FrmrecepcionviaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1924;
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
        enumBase = GenericUrlEnum.VI_VIATICOS;
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

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrecepcionviaticosControladorUrlEnum.URL4615
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrecepcionviaticosControladorUrlEnum.URL35421
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrecepcionviaticosControladorUrlEnum.URL56214
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaPaisOrigen
     *
     */
    public void cargarListaPaisOrigen() {
        try {
            listaPaisOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecepcionviaticosControladorUrlEnum.URL5852
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
     * Carga la lista listaPaisDestino
     *
     */
    public void cargarListaPaisDestino() {
        try {
            listaPaisDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecepcionviaticosControladorUrlEnum.URL5852
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
     * Carga la lista listaDepartamentoOrigen
     *
     */
    public void cargarListaDepartamentoOrigen() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_ORIGEN"));

        try {
            listaDepartamentoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecepcionviaticosControladorUrlEnum.URL55124
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDepartamentoDestino
     *
     */
    public void cargarListaDepartamentoDestino() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_DESTINO"));

        try {
            listaDepartamentoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecepcionviaticosControladorUrlEnum.URL55124
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCiudadOrigen
     *
     */
    public void cargarListaCiudadOrigen() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_ORIGEN"));

        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO_ORIGEN"));

        try {
            listaCiudadOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecepcionviaticosControladorUrlEnum.URL87512
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCiudadDestino
     *
     */
    public void cargarListaCiudadDestino() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS",
                        registro.getCampos().get("PAIS_DESTINO"));
        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos()
                                        .get("DEPARTAMENTO_DESTINO"));

        try {
            listaCiudadDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecepcionviaticosControladorUrlEnum.URL87512
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

        cargarListaPaisOrigen();
        cargarListaPaisDestino();
        cargarListaDepartamentoOrigen();
        cargarListaDepartamentoDestino();
        cargarListaCiudadOrigen();
        cargarListaCiudadDestino();

        calcularFechaLegalizacion();

        if ("1".equals(registro.getCampos().get("TIPOCOMISION")
                        .toString())) {

            visiblePesos = true;

        }
        else {
            visiblePesos = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    private void calcularFechaLegalizacion() {
        Date fechaRegreso = (Date) registro.getCampos().get("FECHAFIN");

        try {
            registro.getCampos().put("FECHA_LEGALIZACION",
                            ejbSysmanUtil.retornarFechaMasDiasHabiles(compania,
                                            fechaRegreso, 3,
                                            false));

            agregarRegistroNuevo(false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        registro.getCampos()
                        .remove(GeneralParameterEnum.COMPANIA.getName());

        registro.getCampos().remove(
                        FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                        .getValue());

        registro.getCampos()
                        .remove(FrmComisionesControladorEnum.NOMBRETERCERO
                                        .getValue());

        registro.getCampos().remove(
                        FrmComisionesControladorEnum.NOMBRERESPONSABLE
                                        .getValue());

        registro.getCampos().remove(FrmComisionesControladorEnum.NOMBREBANCO
                        .getValue());

        registro.getCampos().remove("NOMBRECLASETRANSPORTE");

        registro.getCampos().remove(FrmComisionesControladorEnum.NOMBRE_EXPE
                        .getValue());
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaPaisOrigen
     * 
     * @return listaPaisOrigen
     */
    public List<Registro> getListaPaisOrigen() {
        return listaPaisOrigen;
    }

    /**
     * Asigna la lista listaPaisOrigen
     * 
     * @param listaPaisOrigen
     * Variable a asignar en listaPaisOrigen
     */
    public void setListaPaisOrigen(List<Registro> listaPaisOrigen) {
        this.listaPaisOrigen = listaPaisOrigen;
    }

    /**
     * Retorna la lista listaPaisDestino
     * 
     * @return listaPaisDestino
     */
    public List<Registro> getListaPaisDestino() {
        return listaPaisDestino;
    }

    /**
     * Asigna la lista listaPaisDestino
     * 
     * @param listaPaisDestino
     * Variable a asignar en listaPaisDestino
     */
    public void setListaPaisDestino(List<Registro> listaPaisDestino) {
        this.listaPaisDestino = listaPaisDestino;
    }

    /**
     * Retorna la lista listaDepartamentoOrigen
     * 
     * @return listaDepartamentoOrigen
     */
    public List<Registro> getListaDepartamentoOrigen() {
        return listaDepartamentoOrigen;
    }

    /**
     * Asigna la lista listaDepartamentoOrigen
     * 
     * @param listaDepartamentoOrigen
     * Variable a asignar en listaDepartamentoOrigen
     */
    public void setListaDepartamentoOrigen(
        List<Registro> listaDepartamentoOrigen) {
        this.listaDepartamentoOrigen = listaDepartamentoOrigen;
    }

    /**
     * Retorna la lista listaDepartamentoDestino
     * 
     * @return listaDepartamentoDestino
     */
    public List<Registro> getListaDepartamentoDestino() {
        return listaDepartamentoDestino;
    }

    /**
     * Asigna la lista listaDepartamentoDestino
     * 
     * @param listaDepartamentoDestino
     * Variable a asignar en listaDepartamentoDestino
     */
    public void setListaDepartamentoDestino(
        List<Registro> listaDepartamentoDestino) {
        this.listaDepartamentoDestino = listaDepartamentoDestino;
    }

    /**
     * Retorna la lista listaCiudadOrigen
     * 
     * @return listaCiudadOrigen
     */
    public List<Registro> getListaCiudadOrigen() {
        return listaCiudadOrigen;
    }

    /**
     * Asigna la lista listaCiudadOrigen
     * 
     * @param listaCiudadOrigen
     * Variable a asignar en listaCiudadOrigen
     */
    public void setListaCiudadOrigen(List<Registro> listaCiudadOrigen) {
        this.listaCiudadOrigen = listaCiudadOrigen;
    }

    /**
     * Retorna la lista listaCiudadDestino
     * 
     * @return listaCiudadDestino
     */
    public List<Registro> getListaCiudadDestino() {
        return listaCiudadDestino;
    }

    /**
     * Asigna la lista listaCiudadDestino
     * 
     * @param listaCiudadDestino
     * Variable a asignar en listaCiudadDestino
     */
    public void setListaCiudadDestino(List<Registro> listaCiudadDestino) {
        this.listaCiudadDestino = listaCiudadDestino;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public boolean isVisiblePesos() {
        return visiblePesos;
    }

    public void setVisiblePesos(boolean visiblePesos) {
        this.visiblePesos = visiblePesos;
    }

    // </SET_GET_ADICIONALES>
}
