/*-
 * FrmAprobacionMinticControlador.java
 *
 * 1.0
 * 
 * 19 oct. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.viaticos.enums.FrmAprobacionMinticControladorUrlEnum;
import com.sysman.viaticos.enums.FrmComisionesControladorEnum;
import com.sysman.viaticos.enums.FrmViaticosControladorEnum;

/**
 * Formulario que permite aprobar las solicitudes de comision para
 * MinTIC
 *
 * @version 1.0, 19/10/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmAprobacionMinticControlador extends BeanBaseDatosAcmeImpl {
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
     * Lista que carga el pais de origen
     */
    private List<Registro> listaPaisOrigen;
    /**
     * Lista que carga el pais de destino
     */
    private List<Registro> listaPaisDestino;
    /**
     * Lista que carga el departamento de origen
     */
    private List<Registro> listaDepartamentoOrigen;
    /**
     * Lista que carga el departamento de destino
     */
    private List<Registro> listaDepartamentoDestino;
    /**
     * Lista que carga la ciudad de origen
     */
    private List<Registro> listaCiudadOrigen;
    /**
     * Lista que carga la ciudad de destino
     */
    private List<Registro> listaCiudadDestino;
    /**
     * Lista que carga el pais de regreso
     */
    private List<Registro> listaPaisRegreso;
    /**
     * Lista que carga el departamento de regreso
     */
    private List<Registro> listaDepartamentoRegreso;
    /**
     * Lista que carga la ciudad de regreso
     */
    private List<Registro> listaCiudadRegreso;

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
     * Crea una nueva instancia de FrmAprobacionMinticControlador
     */
    public FrmAprobacionMinticControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1971;
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
        cargarListaPaisOrigen();
        cargarListaPaisDestino();
        cargarListaPaisRegreso();

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
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAprobacionMinticControladorUrlEnum.URL245157
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAprobacionMinticControladorUrlEnum.URL28654
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAprobacionMinticControladorUrlEnum.URL272824
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
                                                            FrmAprobacionMinticControladorUrlEnum.URL9883
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
                                                            FrmAprobacionMinticControladorUrlEnum.URL9883
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

        param.put("PAIS", registro.getCampos().get(
                        FrmComisionesControladorEnum.PAIS_ORIGEN.getValue()));

        try {
            listaDepartamentoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAprobacionMinticControladorUrlEnum.URL10536
                                                                            .getValue()

                                            )
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

        param.put("PAIS", registro.getCampos().get(
                        FrmComisionesControladorEnum.PAIS_DESTINO.getValue()));

        try {
            listaDepartamentoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAprobacionMinticControladorUrlEnum.URL10881
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

        param.put("PAIS", registro.getCampos().get(
                        FrmComisionesControladorEnum.PAIS_ORIGEN.getValue()));

        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO_ORIGEN"));

        try {
            listaCiudadOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAprobacionMinticControladorUrlEnum.URL11213
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
                        registro.getCampos().get(
                                        FrmComisionesControladorEnum.PAIS_DESTINO
                                                        .getValue()));
        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos()
                                        .get("DEPARTAMENTO_DESTINO"));

        try {
            listaCiudadDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAprobacionMinticControladorUrlEnum.URL11548
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
     * Carga la lista listaPaisRegreso
     *
     */
    public void cargarListaPaisRegreso() {
        try {
            listaPaisRegreso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAprobacionMinticControladorUrlEnum.URL11880
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
     * Carga la lista listaDepartamentoRegreso
     *
     */
    public void cargarListaDepartamentoRegreso() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_REGRESO"));

        try {
            listaDepartamentoRegreso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAprobacionMinticControladorUrlEnum.URL12216
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
     * Carga la lista listaCiudadRegreso
     *
     */
    public void cargarListaCiudadRegreso() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_REGRESO"));

        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO_REGRESO"));

        try {
            listaCiudadRegreso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(

                                                            FrmAprobacionMinticControladorUrlEnum.URL12550
                                                                            .getValue()

                                            )
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
        /*
         * FR1971-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name log
         * "Ingresó a Procesos, Solicitud de Comisión" If
         * Me.OptAprobado = -1 Then Me.CmbImprimir.Enabled = True Else
         * Me.CmbImprimir.Enabled = False End If End Sub
         *//*
            * FR1971-AL_ABRIR Private Sub Form_Load()
            * Me.FechaSistema.Locked = True Me.CodSolicitud.Locked =
            * True Me.TxtResponsable.Locked = True Me.TxtCargo.Locked
            * = True Me.TxtDias.Locked = True Me.Caption =
            * NombreEmpresa(0) & ".Solicitud de Comision" End Sub
            */
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

        cargarListaDepartamentoOrigen();
        cargarListaCiudadOrigen();

        cargarListaDepartamentoDestino();
        cargarListaCiudadDestino();

        cargarListaDepartamentoRegreso();
        cargarListaCiudadRegreso();

        // </CODIGO_DESARROLLADO>
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

        registro.getCampos().remove(
                        FrmComisionesControladorEnum.NOMBRECLASETRANSPORTE
                                        .getValue());

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

    /**
     * Retorna la lista listaPaisRegreso
     * 
     * @return listaPaisRegreso
     */
    public List<Registro> getListaPaisRegreso() {
        return listaPaisRegreso;
    }

    /**
     * Asigna la lista listaPaisRegreso
     * 
     * @param listaPaisRegreso
     * Variable a asignar en listaPaisRegreso
     */
    public void setListaPaisRegreso(List<Registro> listaPaisRegreso) {
        this.listaPaisRegreso = listaPaisRegreso;
    }

    /**
     * Retorna la lista listaDepartamentoRegreso
     * 
     * @return listaDepartamentoRegreso
     */
    public List<Registro> getListaDepartamentoRegreso() {
        return listaDepartamentoRegreso;
    }

    /**
     * Asigna la lista listaDepartamentoRegreso
     * 
     * @param listaDepartamentoRegreso
     * Variable a asignar en listaDepartamentoRegreso
     */
    public void setListaDepartamentoRegreso(
        List<Registro> listaDepartamentoRegreso) {
        this.listaDepartamentoRegreso = listaDepartamentoRegreso;
    }

    /**
     * Retorna la lista listaCiudadRegreso
     * 
     * @return listaCiudadRegreso
     */
    public List<Registro> getListaCiudadRegreso() {
        return listaCiudadRegreso;
    }

    /**
     * Asigna la lista listaCiudadRegreso
     * 
     * @param listaCiudadRegreso
     * Variable a asignar en listaCiudadRegreso
     */
    public void setListaCiudadRegreso(List<Registro> listaCiudadRegreso) {
        this.listaCiudadRegreso = listaCiudadRegreso;
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
