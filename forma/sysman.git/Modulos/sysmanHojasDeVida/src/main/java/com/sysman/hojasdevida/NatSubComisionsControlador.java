/*-
 * NatsubcomisionsControlador.java
 *
 * 1.0
 * 
 * 04/01/2018
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
import com.sysman.hojasdevida.enums.NatSubComisionesControladorEnum;
import com.sysman.hojasdevida.enums.NatSubComisionesControladorUrlEnum;
import com.sysman.hojasdevida.enums.SubAcademicasControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar comisiones de personas
 *
 * @version 1.0, 11/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class NatSubComisionsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el valor del numero documento del rid
     * recibido por parametro
     */
    private String numeroDocumento;
    /**
     * Variable que almacena el valor de la sucursal del rid recibido
     * por parametro
     */
    private String sucursal;

    /**
     * Map recibida por parametro
     */
    Map<String, Object> ridDatosBasicos;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registro de la tabla pais
     */
    private List<Registro> listaPais;
    /**
     * Lista de registos de la tabla departamento
     */
    private List<Registro> listaDepartamento;
    /**
     * Lista de registros de la tabla ciudad
     */
    private List<Registro> listaMunicipio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla cargo
     */
    private RegistroDataModelImpl listaCargo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatsubcomisionsControlador
     */
    @SuppressWarnings("unchecked")
    public NatSubComisionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            // 1553
            numFormulario = GeneralCodigoFormaEnum.COMISION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                ridDatosBasicos = (Map<String, Object>) parametrosEntrada
                                .get("rid");

                numeroDocumento = ridDatosBasicos.get(
                                NatSubComisionesControladorEnum.KEY_NUMERO_DCTO
                                                .getValue())
                                .toString();
                sucursal = ridDatosBasicos
                                .get(NatSubComisionesControladorEnum.KEY_SUCURSAL
                                                .getValue())
                                .toString();

            }
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
        cargarListaCargo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaPais();

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
        enumBase = GenericUrlEnum.NAT_COMISION;
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
        parametrosListado.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDocumento);
        parametrosListado.put(
                        GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaPais
     */
    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubComisionesControladorUrlEnum.URL210
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
     * Carga la lista listaDepartamento
     */
    public void cargarListaDepartamento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(),
                        registro.getCampos().get("COPAIS"));

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubComisionesControladorUrlEnum.URL234
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaMunicipio
     */
    public void cargarListaMunicipio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(SubAcademicasControladorEnum.PAIS.getValue(),
                        registro.getCampos().get("COPAIS"));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos().get("CODPTO"));

        try {
            listaMunicipio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NatSubComisionesControladorUrlEnum.URL261
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaCargo
     */
    public void cargarListaCargo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubComisionesControladorUrlEnum.URL282
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_CARGO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Pais
     * 
     */
    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        cargarListaDepartamento();
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Departamento
     * 
     */
    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Fecha Desde
     * 
     */
    public void cambiarFechaDesde() {
        validarFecha();
    }

    /**
     * Metodo ejecutado al cambiar el control Fecha Hasta
     * 
     */
    public void cambiarFechaHasta() {
        validarFecha();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CO_CARGO",
                        registroAux.getCampos().get("ID_DE_CARGO"));
        registro.getCampos().put("NOMBRECARGO",
                        registroAux.getCampos().get("NOMBRE_DEL_CARGO"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que valida las que la fecha desde no pueda ser mayor de
     * la fecha hasta
     */
    public void validarFecha() {

        if (!(SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "CO_FECHDESDE")
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            NatSubComisionesControladorEnum.CO_FECHHASTA
                                            .getValue()))) {
            Date fechaIni = (Date) registro.getCampos().get("CO_FECHDESDE");
            Date fechaFin = (Date) registro.getCampos()
                            .get(NatSubComisionesControladorEnum.CO_FECHHASTA
                                            .getValue());

            if (fechaFin.before(fechaIni)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3591"));
                registro.getCampos()
                                .put(NatSubComisionesControladorEnum.CO_FECHHASTA
                                                .getValue(), null);
            }
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
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarListaDepartamento();
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        NatSubComisionesControladorEnum.CO_NUMEDOCU.getValue(),
                        numeroDocumento);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(NatSubComisionesControladorEnum.CO_NUMEDOCU
                                            .getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());

        }

        registro.getCampos().remove("NOMBRECARGO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
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
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { ridDatosBasicos };

        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(
                                        GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable numeroDocumento
     * 
     * @return numeroDocumento
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Asigna la variable numeroDocumento
     * 
     * @param numeroDocumento
     * Variable a asignar en numeroDocumento
     */
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    /**
     * Retorna la variable sucursal
     * 
     * @return sucursal
     */
    public String getSucursal() {
        return sucursal;
    }

    /**
     * Asigna la variable sucursal
     * 
     * @param sucursal
     * Variable a asignar en sucursal
     */
    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaPais
     * 
     * @return listaPais
     */
    public List<Registro> getListaPais() {
        return listaPais;
    }

    /**
     * Asigna la lista listaPais
     * 
     * @param listaPais
     * Variable a asignar en listaPais
     */
    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    /**
     * Retorna la lista listaDepartamento
     * 
     * @return listaDepartamento
     */
    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    /**
     * Asigna la lista listaDepartamento
     * 
     * @param listaDepartamento
     * Variable a asignar en listaDepartamento
     */
    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    /**
     * Retorna la lista listaMunicipio
     * 
     * @return listaMunicipio
     */
    public List<Registro> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Asigna la lista listaMunicipio
     * 
     * @param listaMunicipio
     * Variable a asignar en listaMunicipio
     */
    public void setListaMunicipio(List<Registro> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaCargo() {
        return listaCargo;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargo(RegistroDataModelImpl listaCargo) {
        this.listaCargo = listaCargo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}