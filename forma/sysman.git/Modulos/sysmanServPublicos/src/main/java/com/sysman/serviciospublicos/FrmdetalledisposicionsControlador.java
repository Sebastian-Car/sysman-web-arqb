/*-
 * FrmdetalledisposicionsControlador.java
 *
 * 1.0
 * 
 * 16/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.FrmdetalledisposicionsControladorEnum;
import com.sysman.serviciospublicos.enums.FrmdetalledisposicionsControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Clase que getiona la CRUD de los detalles de disposicion
 *
 * @version 1.0, 16/12/2016
 * @author jguerrero
 * @modified jguerrero
 * @version 2. 30/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class FrmdetalledisposicionsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que se encarga de almacenar temporalmente el nombre
     * del usuario luego de seleccionarlo en el codigo ruta
     */
    private String nombre;
    /**
     * Variable encargada de almacenar temporalmente el ciclo
     * seleccionado del modal pedircilos
     */
    private String ciclo;
    /**
     * Variable encargada de almacenar temporalmente el a�o del ciclo
     * luego de seleccionado del modal pedircilos
     */
    private String ano;
    /**
     * Variable encargada de almacenar temporalmente el periodo del
     * ciclo luego de seleccionado del modal pedircilos
     */
    private String periodo;

    /**
     * Variable encargada de almacenar temporalmente el departamento
     * que se selecciona en el combo departamento del formulario
     */

    private final String departamentorCons;
    private final String codigoRutaCons;
    private final String nombreCons;
    private final String cicloCons;
    private final String companiaCons;
    private final String fechaInicialCons;

    private int indice;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que se encarga de almacenar los datos de la peticion a la
     * base de datos de los paises
     */
    private List<Registro> listaCMDPAIS;
    /**
     * Lista que se encarga de almacenar los datos de la peticion a la
     * base de datos de los municipios
     */
    private List<Registro> listaCMDMunicipio;
    /**
     * Lista que se encarga de almacenar los datos de la peticion a la
     * base de datos de periodo
     */
    private List<Registro> listaCmdPeriodo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que se encarga de almacenar los datos de la peticion a la
     * base de datos de codigoRuta
     */
    private RegistroDataModelImpl listacodigoruta;
    /**
     * Lista que se encarga de almacenar los datos de la peticion a la
     * base de datos de los codigoRuta
     */
    private RegistroDataModelImpl listacodigorutaE;
    /**
     * Lista que se encarga de almacenar los datos de la peticion a la
     * base de datos de los departamento
     */
    private List<Registro> listaCMDDEPARTAMENTO;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmdetalledisposicionsControlador
     */

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServPubCero;

    public FrmdetalledisposicionsControlador() {
        super();

        compania = SessionUtil.getCompania();
        departamentorCons = GeneralParameterEnum.DEPARTAMENTO.getName();
        codigoRutaCons = GeneralParameterEnum.CODIGORUTA.getName();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        cicloCons = GeneralParameterEnum.CICLO.getName();
        companiaCons = GeneralParameterEnum.COMPANIA.getName();
        fechaInicialCons = "FECHAINICIAL";

        try {
            // 1241
            numFormulario = GeneralCodigoFormaEnum.FRMDETALLEDISPOSICIONS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                ciclo = parametros.get("ciclo").toString();
                ano = parametros.get("anio").toString();
                periodo = parametros.get("periodo").toString();
            }

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
        enumBase = GenericUrlEnum.SP_DETALLEDISPOSICION;

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaCMDPAIS();
        cargarListaCMDMunicipio();
        cargarListaCmdPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigoruta();
        cargarListacodigorutaE();
        cargarListaCMDDEPARTAMENTO();

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
        parametrosListado.put("ANODIS", ano);
        parametrosListado.put("PERIODODIS", periodo);
        parametrosListado.put("CICLODIS", ciclo);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCMDPAIS
     *
     * 
     */
    public void cargarListaCMDPAIS() {

        // 1001
        try {
            listaCMDPAIS = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdetalledisposicionsControladorUrlEnum.URL9658
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
     * Carga la lista listaCMDMunicipio
     *
     * 
     */
    public void cargarListaCMDMunicipio() {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", registro.getCampos().get("PAIS"));
        param.put(departamentorCons,
                        registro.getCampos().get(departamentorCons));

        param.put(FrmdetalledisposicionsControladorEnum.PARAM1.getValue(),
                        String.valueOf(registro.getCampos()
                                        .get(departamentorCons)));

        try {
            listaCMDMunicipio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdetalledisposicionsControladorUrlEnum.URL10197
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
     * Carga la lista listaCmdPeriodo
     *
     * 
     */
    public void cargarListaCmdPeriodo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));

        try {
            listaCmdPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdetalledisposicionsControladorUrlEnum.URL10995
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 227017
    }

    /**
     * 
     * Carga la lista listacodigoruta
     *
     * 
     */
    public void cargarListacodigoruta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdetalledisposicionsControladorUrlEnum.URL11862
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigoruta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);

        // 366004

    }

    /**
     * 
     * Carga la lista listacodigoruta
     *
     * 
     */
    public void cargarListacodigorutaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdetalledisposicionsControladorUrlEnum.URL11862
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigorutaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);

        // 366004
    }

    /**
     * 
     * Carga la lista listaCMDDEPARTAMENTO
     *
     * 
     */
    public void cargarListaCMDDEPARTAMENTO() {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", registro.getCampos().get("PAIS"));

        try {
            listaCMDDEPARTAMENTO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdetalledisposicionsControladorUrlEnum.URL13214
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton exportarExcel en la vista
     *
     *
     * 
     */
    public void oprimirexportarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CMDPAIS
     * 
     * 
     * 
     */
    public void cambiarCMDPAIS() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(departamentorCons, null);
        registro.getCampos().put(GeneralParameterEnum.MUNICIPIO.getName(),
                        null);
        cargarListaCMDDEPARTAMENTO();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CMDDEPARTAMENTO
     * 
     * 
     * 
     */
    public void cambiarCMDDEPARTAMENTO() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.MUNICIPIO.getName(),
                        null);
        cargarListaCMDMunicipio();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control codigoruta en la fila
     * seleccionada dentro de la grilla *
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */

    public void cambiarCMDPAISC(int rowNum) {

        String pais = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get("PAIS").toString();
        registro.getCampos().put("PAIS", pais);

        cargarListaCMDDEPARTAMENTO();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CMDDEPARTAMENTO en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCMDDEPARTAMENTOC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        String departamento = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(departamentorCons).toString();
        registro.getCampos().put(departamentorCons, departamento);
        cargarListaCMDMunicipio();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcodigorutaC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        nombreCons,
                        nombre);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoruta
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoruta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoRutaCons,
                        registroAux.getCampos().get(codigoRutaCons));
        registro.getCampos().put(nombreCons,
                        registroAux.getCampos().get(nombreCons).toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoruta
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigorutaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoRutaCons).toString();
        nombre = registroAux.getCampos().get(nombreCons).toString();

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

        cargarListaCmdPeriodo();
        registro.getCampos().put(cicloCons, ciclo);

        periodoSiguiente();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * 
     *
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(companiaCons, compania);
        registro.getCampos().put(cicloCons, ciclo);

        registro.getCampos().put(fechaInicialCons,
                        registro.getCampos().get(fechaInicialCons));
        registro.getCampos().remove(nombreCons);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * 
     *
     */
    @Override
    public boolean insertarDespues() {

        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(fechaInicialCons,
                        registro.getCampos().get(fechaInicialCons));
        registro.getCampos().remove(nombreCons);
        registro.getCampos().remove("NOMBREDEPARTAMENTO");
        registro.getCampos().remove("NOMBRECIUDAD");
        registro.getCampos().remove("NOMBREPAIS");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
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
     * 
     * 
     */
    @Override
    public void removerCombos() {
        //
        registro.getCampos().remove(companiaCons);
        registro.getCampos().remove(cicloCons);

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

    // /**
    // * Metodo ejecutado cuando se activa la edicion de un registro
    // del
    // * formulario
    // *
    // *
    // * @param registro
    // * registro del cual se activo la edicion
    // */

    @Override
    public void asignarValoresRegistro() {

        registro.getCampos().put(companiaCons, compania);
        registro.getCampos().put(cicloCons, ciclo);

        periodoSiguiente();

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCMDPAIS
     * 
     * @return listaCMDPAIS
     */
    public List<Registro> getListaCMDPAIS() {
        return listaCMDPAIS;
    }

    /**
     * Asigna la lista listaCMDPAIS
     * 
     * @param listaCMDPAIS
     * Variable a asignar en listaCMDPAIS
     */
    public void setListaCMDPAIS(List<Registro> listaCMDPAIS) {
        this.listaCMDPAIS = listaCMDPAIS;
    }

    /**
     * Retorna la lista listaCMDMunicipio
     * 
     * @return listaCMDMunicipio
     */
    public List<Registro> getListaCMDMunicipio() {
        return listaCMDMunicipio;
    }

    /**
     * Asigna la lista listaCMDMunicipio
     * 
     * @param listaCMDMunicipio
     * Variable a asignar en listaCMDMunicipio
     */
    public void setListaCMDMunicipio(List<Registro> listaCMDMunicipio) {
        this.listaCMDMunicipio = listaCMDMunicipio;
    }

    /**
     * Retorna la lista listaCmdPeriodo
     * 
     * @return listaCmdPeriodo
     */
    public List<Registro> getListaCmdPeriodo() {
        return listaCmdPeriodo;
    }

    /**
     * Asigna la lista listaCmdPeriodo
     * 
     * @param listaCmdPeriodo
     * Variable a asignar en listaCmdPeriodo
     */
    public void setListaCmdPeriodo(List<Registro> listaCmdPeriodo) {
        this.listaCmdPeriodo = listaCmdPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigoruta
     * 
     * @return listacodigoruta
     */
    public RegistroDataModelImpl getListacodigoruta() {
        return listacodigoruta;
    }

    /**
     * Asigna la lista listacodigoruta
     * 
     * @param listacodigoruta
     * Variable a asignar en listacodigoruta
     */
    public void setListacodigoruta(RegistroDataModelImpl listacodigoruta) {
        this.listacodigoruta = listacodigoruta;
    }

    /**
     * Retorna la lista listacodigoruta
     * 
     * @return listacodigoruta
     */
    public RegistroDataModelImpl getListacodigorutaE() {
        return listacodigorutaE;
    }

    /**
     * Asigna la lista listacodigoruta
     * 
     * @param listacodigoruta
     * Variable a asignar en listacodigoruta
     */
    public void setListacodigorutaE(RegistroDataModelImpl listacodigorutaE) {
        this.listacodigorutaE = listacodigorutaE;
    }

    /**
     * Retorna la lista listaCMDDEPARTAMENTO
     * 
     * @return listaCMDDEPARTAMENTO
     */

    public List<Registro> getListaCMDDEPARTAMENTO() {
        return listaCMDDEPARTAMENTO;
    }

    public void setListaCMDDEPARTAMENTO(List<Registro> listaCMDDEPARTAMENTO) {
        this.listaCMDDEPARTAMENTO = listaCMDDEPARTAMENTO;
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

    public void periodoSiguiente() {
        String periodoPrincipal = null;

        try {

            periodoPrincipal = ejbServPubCero.prepararAnoPeriodoSiguiente(
                            compania, Integer.parseInt(ano), periodo, "1",
                            null);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodoPrincipal);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdetalledisposicionsControladorUrlEnum.URL12695
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg == null) {

                String reemplazos = idioma.getString("TB_TB3003");
                reemplazos = reemplazos.replace("s$ciclo$s", ciclo);
                reemplazos = reemplazos.replace("s$periodo$s",
                                periodoPrincipal);
                JsfUtil.agregarMensajeAlerta(reemplazos);

                return;
            }

            this.registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            ano);

            this.registro.getCampos().put("PERIODO", periodoPrincipal);
            cargarListaCmdPeriodo();

        }
        catch (NumberFormatException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
