/*-
 * PlanoEmpleadosRegistraduriaControlador.java
 *
 * 1.0
 * 
 * 18/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sia;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sia.enums.PlanoEmpleadosRegistraduriaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 18/09/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlanoEmpleadosRegistraduriaControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private String codigoNivel;
    private String codigoFiliacion;
    private String codigoTipoEmpleado;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaNombreNivelEducativo;
    private RegistroDataModelImpl listaNombreNivelEducativoE;
    private RegistroDataModelImpl listaNombreFiliacionPolitica;
    private RegistroDataModelImpl listaNombreFiliacionPoliticaE;
    private RegistroDataModelImpl listaNombreTipoEmpleado;
    private RegistroDataModelImpl listaNombreTipoEmpleadoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * PlanoEmpleadosRegistraduriaControlador
     */
    public PlanoEmpleadosRegistraduriaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2105
            numFormulario = GeneralCodigoFormaEnum.PLANO_EMPLEADOS_REGISTRADURIA_CONTROLADOR
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
        tabla = enumBase.PERSONAL.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNombreNivelEducativo();
        cargarListaNombreNivelEducativoE();
        cargarListaNombreFiliacionPolitica();
        cargarListaNombreFiliacionPoliticaE();
        cargarListaNombreTipoEmpleado();
        cargarListaNombreTipoEmpleadoE();
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
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanoEmpleadosRegistraduriaControladorUrlEnum.URL0004
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanoEmpleadosRegistraduriaControladorUrlEnum.URL0005
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaNombreNivelEducativo
     *
     */
    public void cargarListaNombreNivelEducativo() {

    }

    /**
     * 
     * Carga la lista listaNombreNivelEducativo
     *
     */
    public void cargarListaNombreNivelEducativoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanoEmpleadosRegistraduriaControladorUrlEnum.URL0001
                                                        .getValue());

        listaNombreNivelEducativoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaNombreFiliacionPolitica
     *
     */
    public void cargarListaNombreFiliacionPolitica() {

    }

    /**
     * 
     * Carga la lista listaNombreFiliacionPolitica
     *
     */
    public void cargarListaNombreFiliacionPoliticaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanoEmpleadosRegistraduriaControladorUrlEnum.URL0002
                                                        .getValue());

        listaNombreFiliacionPoliticaE = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaNombreTipoEmpleado
     *
     */
    public void cargarListaNombreTipoEmpleado() {

    }

    /**
     * 
     * Carga la lista listaNombreTipoEmpleado
     *
     */
    public void cargarListaNombreTipoEmpleadoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanoEmpleadosRegistraduriaControladorUrlEnum.URL0003
                                                        .getValue());

        listaNombreTipoEmpleadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control NombreNivelEducativo en
     * la fila seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNombreNivelEducativoC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "NIVEL_EDUCATIVO_RNEC", codigoNivel);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NombreFiliacionPolitica
     * en la fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNombreFiliacionPoliticaC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "FILIACION_POLITICA_RNEC", codigoFiliacion);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NombreTipoEmpleado en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNombreTipoEmpleadoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "TIPO_EMPLEADO_RNEC", codigoTipoEmpleado);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreNivelEducativo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreNivelEducativo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRE_NIVEL_EDUCATIVO",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreNivelEducativo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreNivelEducativoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NOMBRE");

        codigoNivel = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreFiliacionPolitica
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreFiliacionPolitica(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRE_FILIACION_POLITICA",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreFiliacionPolitica
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreFiliacionPoliticaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NOMBRE");

        codigoFiliacion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreTipoEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreTipoEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRE_TIPO_EMPLEADO",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreTipoEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreTipoEmpleadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("TIPO_EMPRESA");

        codigoTipoEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
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
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
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

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {

        registro.getCampos().remove("NOMBRE_NIVEL_EDUCATIVO");
        registro.getCampos().remove("NOMBRE_TIPO_EMPLEADO");
        registro.getCampos().remove("NOMBRE_FILIACION_POLITICA");
        registro.getCampos().remove("NUMERO_DCTO");
        registro.getCampos().remove("ID_DE_EMPLEADO");
        registro.getCampos().remove("MOMBRE_EMPLEADO");
        registro.getCampos().remove("COMPANIA");
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
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // TODO Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNombreNivelEducativo
     * 
     * @return listaNombreNivelEducativo
     */
    public RegistroDataModelImpl getListaNombreNivelEducativo() {
        return listaNombreNivelEducativo;
    }

    /**
     * Asigna la lista listaNombreNivelEducativo
     * 
     * @param listaNombreNivelEducativo
     * Variable a asignar en listaNombreNivelEducativo
     */
    public void setListaNombreNivelEducativo(
        RegistroDataModelImpl listaNombreNivelEducativo) {
        this.listaNombreNivelEducativo = listaNombreNivelEducativo;
    }

    /**
     * Retorna la lista listaNombreNivelEducativo
     * 
     * @return listaNombreNivelEducativo
     */
    public RegistroDataModelImpl getListaNombreNivelEducativoE() {
        return listaNombreNivelEducativoE;
    }

    /**
     * Asigna la lista listaNombreNivelEducativo
     * 
     * @param listaNombreNivelEducativo
     * Variable a asignar en listaNombreNivelEducativo
     */
    public void setListaNombreNivelEducativoE(
        RegistroDataModelImpl listaNombreNivelEducativoE) {
        this.listaNombreNivelEducativoE = listaNombreNivelEducativoE;
    }

    /**
     * Retorna la lista listaNombreFiliacionPolitica
     * 
     * @return listaNombreFiliacionPolitica
     */
    public RegistroDataModelImpl getListaNombreFiliacionPolitica() {
        return listaNombreFiliacionPolitica;
    }

    /**
     * Asigna la lista listaNombreFiliacionPolitica
     * 
     * @param listaNombreFiliacionPolitica
     * Variable a asignar en listaNombreFiliacionPolitica
     */
    public void setListaNombreFiliacionPolitica(
        RegistroDataModelImpl listaNombreFiliacionPolitica) {
        this.listaNombreFiliacionPolitica = listaNombreFiliacionPolitica;
    }

    /**
     * Retorna la lista listaNombreFiliacionPolitica
     * 
     * @return listaNombreFiliacionPolitica
     */
    public RegistroDataModelImpl getListaNombreFiliacionPoliticaE() {
        return listaNombreFiliacionPoliticaE;
    }

    /**
     * Asigna la lista listaNombreFiliacionPolitica
     * 
     * @param listaNombreFiliacionPolitica
     * Variable a asignar en listaNombreFiliacionPolitica
     */
    public void setListaNombreFiliacionPoliticaE(
        RegistroDataModelImpl listaNombreFiliacionPoliticaE) {
        this.listaNombreFiliacionPoliticaE = listaNombreFiliacionPoliticaE;
    }

    /**
     * Retorna la lista listaNombreTipoEmpleado
     * 
     * @return listaNombreTipoEmpleado
     */
    public RegistroDataModelImpl getListaNombreTipoEmpleado() {
        return listaNombreTipoEmpleado;
    }

    /**
     * Asigna la lista listaNombreTipoEmpleado
     * 
     * @param listaNombreTipoEmpleado
     * Variable a asignar en listaNombreTipoEmpleado
     */
    public void setListaNombreTipoEmpleado(
        RegistroDataModelImpl listaNombreTipoEmpleado) {
        this.listaNombreTipoEmpleado = listaNombreTipoEmpleado;
    }

    /**
     * Retorna la lista listaNombreTipoEmpleado
     * 
     * @return listaNombreTipoEmpleado
     */
    public RegistroDataModelImpl getListaNombreTipoEmpleadoE() {
        return listaNombreTipoEmpleadoE;
    }

    /**
     * Asigna la lista listaNombreTipoEmpleado
     * 
     * @param listaNombreTipoEmpleado
     * Variable a asignar en listaNombreTipoEmpleado
     */
    public void setListaNombreTipoEmpleadoE(
        RegistroDataModelImpl listaNombreTipoEmpleadoE) {
        this.listaNombreTipoEmpleadoE = listaNombreTipoEmpleadoE;
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

    /**
     * @return the codigoNivel
     */
    public String getCodigoNivel() {
        return codigoNivel;
    }

    /**
     * @param codigoNivel
     * the codigoNivel to set
     */
    public void setCodigoNivel(String codigoNivel) {
        this.codigoNivel = codigoNivel;
    }

    /**
     * @return the codigoFiliacion
     */
    public String getCodigoFiliacion() {
        return codigoFiliacion;
    }

    /**
     * @param codigoFiliacion
     * the codigoFiliacion to set
     */
    public void setCodigoFiliacion(String codigoFiliacion) {
        this.codigoFiliacion = codigoFiliacion;
    }

    /**
     * @return the codigoTipoEmpleado
     */
    public String getCodigoTipoEmpleado() {
        return codigoTipoEmpleado;
    }

    /**
     * @param codigoTipoEmpleado
     * the codigoTipoEmpleado to set
     */
    public void setCodigoTipoEmpleado(String codigoTipoEmpleado) {
        this.codigoTipoEmpleado = codigoTipoEmpleado;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
