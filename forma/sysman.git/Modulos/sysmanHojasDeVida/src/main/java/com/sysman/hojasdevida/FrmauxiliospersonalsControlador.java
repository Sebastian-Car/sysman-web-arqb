/*-
 * FrmauxiliospersonalsControlador.java
 *
 * 1.0
 * 
 * 05/02/2018
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmauxiliospersonalControladorEnum;
import com.sysman.hojasdevida.enums.FrmauxiliospersonalControladorUrlEnum;
import com.sysman.hojasdevida.enums.FrmevaluadorevaluadosControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Esta clase me permite gestionar el formulario FRM_AUXILIOSPERSONAL.
 *
 * @version 1.0, 05/02/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmauxiliospersonalsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String cNombre;

    /*
     * Esta variable me almacena el id del empleado seleccionado en el combo de seleccionar persona
     */
    private String idEmpleado;
    /*
     * Esta variable me almacena el valor de la sucursal del empleado seleccionado en el combo de seleccionar persona
     */
    private String sucursal;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * A traves de esta lista obtengo las personalas para posteriormente seleccionarlas y agragarles un auxilio o incentivo.
     */
    private RegistroDataModelImpl listacmbBuscar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    /**
     * Crea una nueva instancia de FrmauxiliospersonalsControlador
     */
    public FrmauxiliospersonalsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_AUXILIOSPERSONAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        cNombre = "NOMBRECOMPLETO";
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbBuscar();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = FrmauxiliospersonalControladorEnum.NAT_INCENTIVOS.getValue();
        asignarOrigenDatos();
        buscarLlave();
        registro = new Registro();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmauxiliospersonalControladorUrlEnum.URL100
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmauxiliospersonalControladorUrlEnum.URL101
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmauxiliospersonalControladorUrlEnum.URL102
                                                        .getValue());

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmauxiliospersonalControladorUrlEnum.URL103
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmauxiliospersonalControladorUrlEnum.URL104
                                                        .getValue());
    }

    /**
     * 
     * Carga la lista listacmbBuscar
     *
     */
    public void cargarListacmbBuscar() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmauxiliospersonalControladorUrlEnum.URL105
                                                        .getValue());

        listacmbBuscar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmauxiliospersonalControladorEnum.NUMERO_DCTO
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listacmbBuscar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbBuscar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        try {

            Map<String, Object> param = new HashMap<>();
            param.put("NUMERO_DOCUMENTO",
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));

            Registro rsiCantidad;
            rsiCantidad = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmauxiliospersonalControladorUrlEnum.URL106
                                                                            .getValue())
                                            .getUrl(), param));

            if (Integer.parseInt(
                            SysmanFunciones.toString(rsiCantidad.getCampos()
                                            .get("CANTIDAD"))) > 0) {

                registro.getCampos().put(
                                GeneralParameterEnum.NUMERO_DCTO.getName(),
                                registroAux.getCampos()
                                                .get(GeneralParameterEnum.NUMERO_DCTO
                                                                .getName()));

                registro.getCampos()
                                .put(FrmevaluadorevaluadosControladorEnum.NOMBRECOMPLETO
                                                .getValue(),
                                                registroAux.getCampos()
                                                                .get("NOMBRECOMPLETO"));

                idEmpleado = SysmanFunciones.toString(
                                registroAux.getCampos()
                                                .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                                .getName()));
                sucursal = SysmanFunciones
                                .toString(registroAux.getCampos()
                                                .get(GeneralParameterEnum.SUCURSAL
                                                                .getName()));
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3973"));
                return;
            }

        }
        catch (SystemException e) {
            // Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1698-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 2, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
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

        if (ACCION_INSERTAR.equals(accion)) {
            // <CODIGO_DESARROLLADO>
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registro.getCampos().put(
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            idEmpleado);

            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);

            registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_DCTO
                                                            .getName()));

            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO_DCTO.getName());

            registro.getCampos()
                            .remove(cNombre);

            // </CODIGO_DESARROLLADO>

        }
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {

            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos().remove("IN_TIPODOCUACTO");
            registro.getCampos().remove("IN_NUME");
            registro.getCampos().remove("IN_FECHRESODECR");
            registro.getCampos().remove("IN_VALOR");
            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO_DCTO.getName());
            registro.getCampos()
                            .remove(FrmevaluadorevaluadosControladorEnum.NOMBRECOMPLETO
                                            .getValue());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbBuscar
     * 
     * @return listacmbBuscar
     */
    public RegistroDataModelImpl getListacmbBuscar() {
        return listacmbBuscar;
    }

    /**
     * Asigna la lista listacmbBuscar
     * 
     * @param listacmbBuscar
     * Variable a asignar en listacmbBuscar
     */
    public void setListacmbBuscar(RegistroDataModelImpl listacmbBuscar) {
        this.listacmbBuscar = listacmbBuscar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public String getcNombre() {
        return cNombre;
    }

    public void setcNombre(String cNombre) {
        this.cNombre = cNombre;
    }
}
