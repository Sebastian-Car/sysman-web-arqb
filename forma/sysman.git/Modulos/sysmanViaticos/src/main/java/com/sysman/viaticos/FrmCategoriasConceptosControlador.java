/*-
 * FrmCategoriasConceptosControlador.java
 *
 * 1.0
 * 
 * 23/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.FrmCategoriasConceptosControladorEnum;
import com.sysman.viaticos.enums.FrmCategoriasConceptosControladorUrlEnum;

/**
 * Formulario que administra los conceptos de las categoría de
 * viaticos
 *
 * @version 1.0, 23/01/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmCategoriasConceptosControlador
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
    /**
     * Constante a nivel de clase que almacena el codigo del modulo al
     * que ingreso el usuario
     */
    private final String modulo;

    /**
     * Atributo que toma el valor del indicador origen destino del
     * concepto seleccionado
     */
    private boolean indicadorOrigenDestino;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Parametro que almacena el anio heredado
     */
    private String anio;

    /**
     * Parametro que almacena el escalafon heredado
     */
    private String escalafon;

    /**
     * Parametro que almacena el ide de categoria heredado
     */

    private String idCategoria;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los paises de origen
     */
    private List<Registro> listaPaisOrigen;
    /**
     * Lista que carga los departamentos de origen
     */
    private List<Registro> listaDepartamentoOrigen;
    /**
     * Lista que carga las ciudades de origen
     */
    private List<Registro> listaCiudadOrigen;
    /**
     * Lista que carga los paises de destino
     */
    private List<Registro> listaPaisDestino;
    /**
     * Lista que carga los departamentos de destino
     */
    private List<Registro> listaDepartamentoDestino;
    /**
     * Lista que carga las ciudades de destino
     */
    private List<Registro> listaCiudadDestino;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los conceptos de viaticos
     */
    private RegistroDataModelImpl listaCodigoConcepto;
    /**
     * Lista que carga los conceptos de viaticos en la grilla
     */
    private RegistroDataModelImpl listaCodigoConceptoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmCategoriasConceptosControlador
     */
    public FrmCategoriasConceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        indicadorOrigenDestino = true;

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_CATEGORIAS_CONCEPTOS_CONTROLADOR
                            .getCodigo();

            parametrosEntrada = SessionUtil.getFlash();
            registro = new Registro(new HashMap<String, Object>());
            if (parametrosEntrada != null) {

                anio = parametrosEntrada.get("anio")
                                .toString();

                escalafon = parametrosEntrada.get("escalafon")
                                .toString();

                idCategoria = parametrosEntrada.get("idCategoria")
                                .toString();

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
        enumBase = GenericUrlEnum.VI_DETALLE_CATEGORIA_CONCEPTO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaPaisOrigen();
        cargarListaPaisDestino();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoConcepto();
        cargarListaCodigoConceptoE();
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
        parametrosListado.put("CATEGORIA", idCategoria);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put("ESCALAFON", escalafon);

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
                                                            FrmCategoriasConceptosControladorUrlEnum.URL3238
                                                                            .getValue())
                                            .getUrl(),
                                            null));
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

        param.put("PAIS",
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.PAISORIGEN
                                                        .getValue()));

        try {
            listaDepartamentoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCategoriasConceptosControladorUrlEnum.URL5717
                                                                            .getValue())
                                            .getUrl(),
                                            param));
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

        param.put("PAIS",
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.PAISORIGEN
                                                        .getValue()));

        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.DEPARTAMENTOORIGEN
                                                        .getValue()));

        try {
            listaCiudadOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCategoriasConceptosControladorUrlEnum.URL6507
                                                                            .getValue())
                                            .getUrl(),
                                            param));
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
                                                            FrmCategoriasConceptosControladorUrlEnum.URL5555
                                                                            .getValue())
                                            .getUrl(),
                                            null));
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

        param.put("PAIS",
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.PAISDESTINO
                                                        .getValue()));

        try {
            listaDepartamentoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCategoriasConceptosControladorUrlEnum.URL7777
                                                                            .getValue())
                                            .getUrl(),
                                            param));
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
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.PAISDESTINO
                                                        .getValue()));

        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.DEPARTAMENTODESTINO
                                                        .getValue()));

        try {
            listaCiudadDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmCategoriasConceptosControladorUrlEnum.URL9999
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCodigoConcepto
     *
     */
    public void cargarListaCodigoConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCategoriasConceptosControladorUrlEnum.URL7238
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCodigoConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmCategoriasConceptosControladorEnum.CODIGO_CONCEPTO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaCodigoConcepto
     *
     */
    public void cargarListaCodigoConceptoE() {
        listaCodigoConceptoE = listaCodigoConcepto;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PaisOrigen
     * 
     * 
     */
    public void cambiarPaisOrigen() {
        cargarListaDepartamentoOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoOrigen
     * 
     * 
     */
    public void cambiarDepartamentoOrigen() {
        cargarListaCiudadOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control PaisDestino
     * 
     * 
     */
    public void cambiarPaisDestino() {
        cargarListaDepartamentoDestino();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoDestino
     * 
     * 
     */
    public void cambiarDepartamentoDestino() {
        cargarListaCiudadDestino();
    }

    /**
     * Metodo ejecutado al cambiar el control CodigoConcepto en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoConceptoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.NOMBRECONCEPTO
                                        .getValue(), registro
                                                        .getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.NOMBRECONCEPTO
                                                                        .getValue()));
    }

    public void cambiarPaisOrigenC(int rowNum) {

        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.PAISORIGEN
                                        .getValue(),
                                        listaInicial.getDatasource()
                                                        .get(rowNum % 10)
                                                        .getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.PAISORIGEN
                                                                        .getValue()));

        cargarListaDepartamentoOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoOrigen en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDepartamentoOrigenC(int rowNum) {
        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.DEPARTAMENTOORIGEN
                                        .getValue(),
                                        listaInicial.getDatasource()
                                                        .get(rowNum % 10)
                                                        .getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.DEPARTAMENTOORIGEN
                                                                        .getValue()));

        cargarListaCiudadOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control PaisDestino en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPaisDestinoC(int rowNum) {

        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.PAISDESTINO
                                        .getValue(),
                                        listaInicial.getDatasource()
                                                        .get(rowNum % 10)
                                                        .getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.PAISDESTINO
                                                                        .getValue()));

        cargarListaDepartamentoDestino();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoDestino en
     * la fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDepartamentoDestinoC(int rowNum) {
        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.DEPARTAMENTODESTINO
                                        .getValue(),
                                        listaInicial.getDatasource()
                                                        .get(rowNum % 10)
                                                        .getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.DEPARTAMENTODESTINO
                                                                        .getValue()));

        cargarListaCiudadDestino();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.CODIGO_CONCEPTO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.CODIGO_CONCEPTO
                                                                        .getValue()));

        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.NOMBRECONCEPTO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()));

        indicadorOrigenDestino = (boolean) registroAux.getCampos()
                        .get(FrmCategoriasConceptosControladorEnum.IND_ORIGEN_DESTINO
                                        .getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoConcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmCategoriasConceptosControladorEnum.CODIGO_CONCEPTO
                                        .getValue()),
                        "").toString();

        registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.NOMBRECONCEPTO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()));

        indicadorOrigenDestino = (boolean) registroAux.getCampos()
                        .get(FrmCategoriasConceptosControladorEnum.IND_ORIGEN_DESTINO
                                        .getValue());
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
     * 
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

        if (validadLugaresVacios()) {

            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        registro.getCampos().put("ESCALAFON", escalafon);
        registro.getCampos().put("ID_CATEGORIA", idCategoria);
        registro.getCampos().put("CONSECUTIVO", generarConsecutivo());
        registro.getCampos().remove("PAISORIGENNOMBRE");
        registro.getCampos().remove("DEPTOORIGENNOMBRE");
        registro.getCampos().remove("CIUDADORIGENNOMBRE");
        registro.getCampos().remove("PAISDESTINONOMBRE");
        registro.getCampos().remove("DEPTODESTINONOMBRE");
        registro.getCampos().remove("CIUDADDESTINONOMBRE");
        registro.getCampos()
                        .remove(FrmCategoriasConceptosControladorEnum.NOMBRECONCEPTO
                                        .getValue());
        return true;
    }

    private boolean validadLugaresVacios() {
        if (indicadorOrigenDestino) {

            if (SysmanFunciones

                            .validarVariableVacio(SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get("PAISORIGEN"),
                                                            "")
                                            .toString())
                && SysmanFunciones.validarVariableVacio(SysmanFunciones
                                .nvl(registro.getCampos()
                                                .get("DEPARTAMENTOORIGEN"),
                                                "")
                                .toString())
                && SysmanFunciones
                                .validarVariableVacio(SysmanFunciones
                                                .nvl(registro.getCampos().get(
                                                                "CIUDADORIGEN"),
                                                                "")
                                                .toString())) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3935"));

                return true;

            }
            else if (SysmanFunciones
                            .validarVariableVacio(SysmanFunciones
                                            .nvl(registro.getCampos().get(
                                                            "PAISDESTINO"),
                                                            "")
                                            .toString())
                && SysmanFunciones.validarVariableVacio(SysmanFunciones
                                .nvl(registro.getCampos()
                                                .get("DEPARTAMENTODESTINO"), "")
                                .toString())
                && SysmanFunciones.validarVariableVacio(SysmanFunciones
                                .nvl(registro.getCampos().get("CIUDADDESTINO"),
                                                "")
                                .toString())) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3936"));

                return true;

            }
        }
        return false;
    }

    private Object generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "VI_DETALLE_CATEGORIA_CONCEPTO",
                            " COMPANIA =" + compania, "CONSECUTIVO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
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
        registro.getCampos().remove("PAISORIGENNOMBRE");
        registro.getCampos().remove("DEPTOORIGENNOMBRE");
        registro.getCampos().remove("CIUDADORIGENNOMBRE");
        registro.getCampos().remove("PAISDESTINONOMBRE");
        registro.getCampos().remove("DEPTODESTINONOMBRE");
        registro.getCampos().remove("CIUDADDESTINONOMBRE");
        registro.getCampos()
                        .remove(FrmCategoriasConceptosControladorEnum.NOMBRECONCEPTO
                                        .getValue());
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

        validarBloqueoOrigenesDestinos(registro);

        this.registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.PAISORIGEN
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.PAISORIGEN
                                                                        .getValue()));
        this.registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.DEPARTAMENTOORIGEN
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.DEPARTAMENTOORIGEN
                                                                        .getValue()));

        cargarListaDepartamentoOrigen();
        cargarListaCiudadOrigen();

        this.registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.PAISDESTINO
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.PAISDESTINO
                                                                        .getValue()));
        this.registro.getCampos()
                        .put(FrmCategoriasConceptosControladorEnum.DEPARTAMENTODESTINO
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(FrmCategoriasConceptosControladorEnum.DEPARTAMENTODESTINO
                                                                        .getValue()));

        cargarListaDepartamentoDestino();
        cargarListaCiudadDestino();

    }

    private void validarBloqueoOrigenesDestinos(Registro registro) {
        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        params.put(FrmCategoriasConceptosControladorEnum.CODIGO_CONCEPTO
                        .getValue(),
                        registro.getCampos()
                                        .get(FrmCategoriasConceptosControladorEnum.CODIGO_CONCEPTO
                                                        .getValue()));

        Registro reg;
        try {
            reg = listaCodigoConceptoE.getRegistroUnico(params);

            indicadorOrigenDestino = (boolean) reg.getCampos()
                            .get(FrmCategoriasConceptosControladorEnum.IND_ORIGEN_DESTINO
                                            .getValue());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_CATEGORIAS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
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
    /**
     * Retorna la lista listaCodigoConcepto
     * 
     * @return listaCodigoConcepto
     */
    public RegistroDataModelImpl getListaCodigoConcepto() {
        return listaCodigoConcepto;
    }

    /**
     * Asigna la lista listaCodigoConcepto
     * 
     * @param listaCodigoConcepto
     * Variable a asignar en listaCodigoConcepto
     */
    public void setListaCodigoConcepto(
        RegistroDataModelImpl listaCodigoConcepto) {
        this.listaCodigoConcepto = listaCodigoConcepto;
    }

    /**
     * Retorna la lista listaCodigoConcepto
     * 
     * @return listaCodigoConcepto
     */
    public RegistroDataModelImpl getListaCodigoConceptoE() {
        return listaCodigoConceptoE;
    }

    /**
     * Asigna la lista listaCodigoConcepto
     * 
     * @param listaCodigoConcepto
     * Variable a asignar en listaCodigoConcepto
     */
    public void setListaCodigoConceptoE(
        RegistroDataModelImpl listaCodigoConceptoE) {
        this.listaCodigoConceptoE = listaCodigoConceptoE;
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

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isIndicadorOrigenDestino() {
        return indicadorOrigenDestino;
    }

    public void setIndicadorOrigenDestino(boolean indicadorOrigenDestino) {
        this.indicadorOrigenDestino = indicadorOrigenDestino;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
