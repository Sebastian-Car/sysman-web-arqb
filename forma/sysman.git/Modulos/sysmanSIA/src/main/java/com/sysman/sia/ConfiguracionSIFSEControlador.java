/*-
 * ConfiguracionSIFSEControlador.java
 *
 * 1.0
 * 
 * 14/12/2018
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sia.enums.ConfiguracionSIFSEControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para configurar las cuentas de Siie
 *
 * @version 1.0, 14/12/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ConfiguracionSIFSEControlador extends BeanBaseContinuoAcmeImpl {
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
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero del ano seleccioando en el
     * combo del ano del formulario
     */
    private String anio;
    /**
     * Atributo que almacena el codigo del registro seleccionado en el
     * combo naturaleza del formualario
     */
    private String naturaleza;

    private String codigoFuente;
    private String nombreFuente;
    private String codigoConcepto;
    private String nombreConcepto;
    private String numero;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAnio;

    /**
     * Lista de registros de la tabla fuente recursos
     */
    private RegistroDataModelImpl listaFuentesMen;
    /**
     * Lista de registros de la tabla fuente recursos
     */
    private RegistroDataModelImpl listaFuentesMenE;
    /**
     * Lista de registros de la tabla conceptos
     */
    private RegistroDataModelImpl listaConceptosMen;
    /**
     * Lista de registros de la tabla conceptos
     */
    private RegistroDataModelImpl listaConceptosMenE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfiguracionSIFSEControlador
     */
    public ConfiguracionSIFSEControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2009
            numFormulario = GeneralCodigoFormaEnum.CONFIGURACION_SIFSE_CONTROLADOR
                            .getCodigo();
            registro = new Registro();
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

        tabla = "PLAN_PPTAL_CONFIG";
        abrirFormulario();
        reasignarOrigen();
        buscarLlave();
        // <CARGAR_LISTA>
        cargarListaAnio();
        // cargarListaFuentesMen();
        cargarListaFuentesMenE();
        // cargarListaConceptosMen();
        cargarListaConceptosMenE();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionSIFSEControladorUrlEnum.URL0006
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionSIFSEControladorUrlEnum.URL0005
                                                        .getValue());

    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfiguracionSIFSEControladorUrlEnum.URL0001
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
     * Carga la lista listaFuentesMen
     *
     */
    public void cargarListaFuentesMen() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionSIFSEControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaFuentesMen = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuentesMen
     *
     */
    public void cargarListaFuentesMenE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionSIFSEControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaFuentesMenE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaConceptosMen
     *
     */
    public void cargarListaConceptosMen() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionSIFSEControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaConceptosMen = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaConceptosMen
     *
     */
    public void cargarListaConceptosMenE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionSIFSEControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaConceptosMenE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo ejecutado al cambiar el control FuentesMen en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFuentesMenC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("CODIGO", codigoFuente);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ConceptosMen en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarConceptosMenC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("CODIGO", codigoConcepto);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Naturaleza
     * 
     * 
     */
    public void cambiarNaturaleza() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuentesMen
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuentesMen(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuentesMen
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuentesMenE(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();

        codigoFuente = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptosMen
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptosMen(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptosMen
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptosMenE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();

        codigoConcepto = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        naturaleza = "C";
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
     * @return true
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
     * 
     * @return true
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.CENTRO_COSTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());
        registro.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.FUENTE_RECURSO.getName());
        registro.getCampos().remove("COMPROBANTE_PAGO_RETENCION");
        registro.getCampos().remove("BANCO_RETENCION");
        registro.getCampos().put("CONCEPTOS_MEN", codigoConcepto);
        registro.getCampos().put("FUENTES_MEN", codigoFuente);

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

        /*
         * indice = Integer.parseInt(SysmanFunciones.nvl(
         * registro.getCampos().get(
         * GeneralParameterEnum.CODIGO.getName()), " ").toString());
         */

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

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * @return the naturaleza
     */
    public String getNaturaleza() {
        return naturaleza;
    }

    /**
     * @param naturaleza
     * the naturaleza to set
     */
    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * @return the listaFuentesMen
     */
    public RegistroDataModelImpl getListaFuentesMen() {
        return listaFuentesMen;
    }

    /**
     * @param listaFuentesMen
     * the listaFuentesMen to set
     */
    public void setListaFuentesMen(RegistroDataModelImpl listaFuentesMen) {
        this.listaFuentesMen = listaFuentesMen;
    }

    /**
     * @return the listaFuentesMenE
     */
    public RegistroDataModelImpl getListaFuentesMenE() {
        return listaFuentesMenE;
    }

    /**
     * @param listaFuentesMenE
     * the listaFuentesMenE to set
     */
    public void setListaFuentesMenE(RegistroDataModelImpl listaFuentesMenE) {
        this.listaFuentesMenE = listaFuentesMenE;
    }

    /**
     * @return the codigoFuente
     */
    public String getCodigoFuente() {
        return codigoFuente;
    }

    /**
     * @param codigoFuente
     * the codigoFuente to set
     */
    public void setCodigoFuente(String codigoFuente) {
        this.codigoFuente = codigoFuente;
    }

    /**
     * @return the nombreFuente
     */
    public String getNombreFuente() {
        return nombreFuente;
    }

    /**
     * @param nombreFuente
     * the nombreFuente to set
     */
    public void setNombreFuente(String nombreFuente) {
        this.nombreFuente = nombreFuente;
    }

    /**
     * @return the codigoConcepto
     */
    public String getCodigoConcepto() {
        return codigoConcepto;
    }

    /**
     * @param codigoConcepto
     * the codigoConcepto to set
     */
    public void setCodigoConcepto(String codigoConcepto) {
        this.codigoConcepto = codigoConcepto;
    }

    /**
     * @return the nombreConcepto
     */
    public String getNombreConcepto() {
        return nombreConcepto;
    }

    /**
     * @param nombreConcepto
     * the nombreConcepto to set
     */
    public void setNombreConcepto(String nombreConcepto) {
        this.nombreConcepto = nombreConcepto;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar
     * the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the listaConceptosMen
     */
    public RegistroDataModelImpl getListaConceptosMen() {
        return listaConceptosMen;
    }

    /**
     * @param listaConceptosMen
     * the listaConceptosMen to set
     */
    public void setListaConceptosMen(RegistroDataModelImpl listaConceptosMen) {
        this.listaConceptosMen = listaConceptosMen;
    }

    /**
     * @return the listaConceptosMenE
     */
    public RegistroDataModelImpl getListaConceptosMenE() {
        return listaConceptosMenE;
    }

    /**
     * @param listaConceptosMenE
     * the listaConceptosMenE to set
     */
    public void setListaConceptosMenE(
        RegistroDataModelImpl listaConceptosMenE) {
        this.listaConceptosMenE = listaConceptosMenE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
