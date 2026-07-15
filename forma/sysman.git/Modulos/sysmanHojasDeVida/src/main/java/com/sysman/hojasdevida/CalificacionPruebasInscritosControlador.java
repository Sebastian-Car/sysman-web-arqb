/*-
 * CalificacionPruebasInscritosControlador.java
 *
 * 1.0
 * 
 * 27/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.CalificacionPruebasInscritosControladorEnum;
import com.sysman.hojasdevida.enums.CalificacionPruebasInscritosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
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
 * Formulario que administra la calificacion para el proceso de
 * selección de personal
 *
 * @version 1.0, 27/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class CalificacionPruebasInscritosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el moduloen el cual
     * inicio sesion el usuario, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atrinuto que contiene el numero de prueba proveniente como
     * parametro
     */
    private String prueba;
    /**
     * Atrinuto que contiene el numero de convocatoria proveniente
     * como parametro
     */
    private String numeroConvocatoria;

    /**
     * Atributo que almacena la sucursal del aspirante
     */
    private String sucursal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena los documentos de los aspirantes
     */
    private RegistroDataModelImpl listaCedula;
    /**
     * Lista en la grilla que almacena los documentos de los
     * aspirantes
     */
    private RegistroDataModelImpl listaCedulaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private String valorMaximo;

    private boolean permiteVer;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * CalificacionPruebasInscritosControlador
     */
    public CalificacionPruebasInscritosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CALIFICACION_PRUEBAS_INSCRITOS_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                numeroConvocatoria = parametrosEntrada.get("numeroConvocatoria")
                                .toString();

                prueba = parametrosEntrada.get("prueba")
                                .toString();

                permiteVer = (boolean) parametrosEntrada.get("permiteVer");

            }
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
        enumBase = GenericUrlEnum.NAT_CALIFICACION_PRUEBAS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCedula();
        cargarListaCedulaE();
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(
                        CalificacionPruebasInscritosControladorEnum.CONVOCATORIA
                                        .getValue(),
                        numeroConvocatoria);

        parametrosListado.put(CalificacionPruebasInscritosControladorEnum.PRUEBA
                        .getValue(), prueba);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalificacionPruebasInscritosControladorUrlEnum.URL8795
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCedula
     *
     */
    public void cargarListaCedula() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalificacionPruebasInscritosControladorUrlEnum.URL7589
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(CalificacionPruebasInscritosControladorEnum.CONVOCATORIA
                        .getValue(),
                        numeroConvocatoria);

        param.put("PRUEBA", prueba);

        listaCedula = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    /**
     * 
     * Carga la lista listaCedula
     *
     */
    public void cargarListaCedulaE() {
        listaCedulaE = listaCedula;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Cedula en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCedulaC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(), registro
                                        .getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedula
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedula
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedulaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName()), "")
                        .toString();

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));

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
     * seleccionado
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put("NRO_CONVOCATORIA",
                        numeroConvocatoria);

        registro.getCampos().put("PRUEBA",
                        prueba);

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
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

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        registro.getCampos().remove("CERRADA");
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.CALIFICACION_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.CALIFICACION_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    /**
     * Metodo ejecutado al cambiar el control Calificacion
     * 
     * 
     * 
     */
    public void cambiarCalificacion() {
        // <CODIGO_DESARROLLADO>
        String calificacion = registro.getCampos().get(
                        CalificacionPruebasInscritosControladorEnum.CALIFICACION
                                        .getValue())
                        .toString();
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CONVOCATORIA", numeroConvocatoria);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CalificacionPruebasInscritosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));

            valorMaximo = rs.getCampos().get("PUNMAX").toString();

            if (Integer.parseInt(valorMaximo) < Integer
                            .parseInt(calificacion)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4181")
                                .replace("s$valorMaximo$s", valorMaximo));

                registro.getCampos().put(
                                CalificacionPruebasInscritosControladorEnum.CALIFICACION
                                                .getValue(),
                                "");

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Calificacion en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCalificacionC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        String calificacion = listaInicial.getDatasource().get(rowNum %
            10).getCampos().get("CALIFICACION").toString();

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CONVOCATORIA", numeroConvocatoria);

        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CalificacionPruebasInscritosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));

            valorMaximo = rs.getCampos().get("PUNMAX").toString();

            if (Integer.parseInt(valorMaximo) < Integer
                            .parseInt(calificacion)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4181")
                                .replace("s$valorMaximo$s", valorMaximo));

                listaInicial.getDatasource().get(rowNum %
                    10).getCampos().put("CALIFICACION", null);

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCedula
     * 
     * @return listaCedula
     */
    public RegistroDataModelImpl getListaCedula() {
        return listaCedula;
    }

    /**
     * Asigna la lista listaCedula
     * 
     * @param listaCedula
     * Variable a asignar en listaCedula
     */
    public void setListaCedula(RegistroDataModelImpl listaCedula) {
        this.listaCedula = listaCedula;
    }

    /**
     * Retorna la lista listaCedula
     * 
     * @return listaCedula
     */
    public RegistroDataModelImpl getListaCedulaE() {
        return listaCedulaE;
    }

    /**
     * Asigna la lista listaCedula
     * 
     * @param listaCedula
     * Variable a asignar en listaCedula
     */
    public void setListaCedulaE(RegistroDataModelImpl listaCedulaE) {
        this.listaCedulaE = listaCedulaE;
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
     * @return the valorMaximo
     */
    public String getValorMaximo() {
        return valorMaximo;
    }

    /**
     * @param valorMaximo
     * the valorMaximo to set
     */
    public void setValorMaximo(String valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    /**
     * @return the permiteVer
     */
    public boolean isPermiteVer() {
        return permiteVer;
    }

    /**
     * @param permiteVer
     * the permiteVer to set
     */
    public void setPermiteVer(boolean permiteVer) {
        this.permiteVer = permiteVer;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
