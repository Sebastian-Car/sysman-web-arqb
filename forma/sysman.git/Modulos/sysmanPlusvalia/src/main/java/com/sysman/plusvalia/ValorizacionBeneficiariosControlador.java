/*-
 * ValorizacionBeneficiariosControlador.java
 *
 * 1.0
 * 
 * 08/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.enums.PlusvaliaBeneficiariosControladorUrlEnum;
import com.sysman.plusvalia.enums.ValorizacionBeneficiariosControladorEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 08/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ValorizacionBeneficiariosControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    private String encabezado;

    // <DECLARAR_ATRIBUTOS>

    // </DECLARAR_ATRIBUTOS>
    private Map<String, Object> parametrosEntrada;
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaDestinacionEco;
    private RegistroDataModelImpl listaGradoBeneficio;
    private RegistroDataModelImpl listaIpCodigo;
    private RegistroDataModelImpl listaEtapaProceso;
    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    private String nombreClase;
    private String claseVP;
    private Map<String, Object> ridProyecto;
    private String destinacioEconomica;
    private String gradoBeneficio;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * ValorizacionBeneficiariosControlador
     */
    public ValorizacionBeneficiariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        claseVP = "45";

        encabezado = idioma.getString("TB_TB4289");

        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            idProyecto = (BigInteger) parametrosEntrada
                            .get("idProyecto");
            codigoProyecto = (String) parametrosEntrada
                            .get("codigoProyecto");
            claseProyecto = (String) parametrosEntrada.get("claseProyecto");
            ridProyecto = (Map<String, Object>) parametrosEntrada.get("rid");
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.VALORIZACION_BENEFICIARIOS_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero();
        cargarListaIpCodigo();
        cargarListaDestinacionEco();
        cargarListaGradoBeneficio();
        cargarListaEtapaProceso();
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
        enumBase = GenericUrlEnum.VP_BENEFICIARIOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        idProyecto);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTercero
     *
     * 
     */
    public void cargarListaTercero() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL14040
                                                        .getValue());

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ValorizacionBeneficiariosControladorEnum.NIT
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaIpCodigo
     *
     * 
     */
    public void cargarListaIpCodigo() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL367
                                                        .getValue());

        listaIpCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDestinacionEco
     *
     */
    public void cargarListaDestinacionEco() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PROYECTO.getName(), idProyecto);
        param.put(ValorizacionBeneficiariosControladorEnum.CLASE_FACTOR
                        .getValue(), "FDE");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL1780
                                                        .getValue());

        listaDestinacionEco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");

    }

    /**
     * 
     * Carga la lista listaGradoBeneficio
     *
     */
    public void cargarListaGradoBeneficio() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PROYECTO.getName(), idProyecto);
        param.put(ValorizacionBeneficiariosControladorEnum.CLASE_FACTOR
                        .getValue(), "FGB");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL1780
                                                        .getValue());

        listaGradoBeneficio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    /**
     * 
     * Carga la lista listaEtapaProceso
     *
     */
    public void cargarListaEtapaProceso() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL1795
                                                        .getValue());

        listaEtapaProceso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AreaFisica
     * 
     * 
     */
    public void cambiarAreaFisica() {
        // <CODIGO_DESARROLLADO>
        Double areaFisica = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get("AREA_FISICA"), "0.0")
                        .toString());
        Double areaAfectada = SysmanFunciones
                        .nvlDbl(registro.getCampos().get("AREA_AFECTADA")
                                        .toString(),
                                        0.0);

        registro.getCampos().put("AREA_GRAVADA", areaFisica - areaAfectada);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AreaAfectada
     * 
     * 
     */
    public void cambiarAreaAfectada() {
        // <CODIGO_DESARROLLADO>
        Double areaFisica = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get("AREA_FISICA"), "0.0")
                        .toString());
        Double areaAfectada = SysmanFunciones
                        .nvlDbl(registro.getCampos().get("AREA_AFECTADA")
                                        .toString(),
                                        0.0);

        registro.getCampos().put("AREA_GRAVADA", areaFisica - areaAfectada);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.NOMBRETERCERO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("DOCUMENTO_PROPIETARIO",
                        registroAux.getCampos().get("NIT"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaIpCodigo
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIpCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.IP_CODIGO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.IP_USUARIO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        ValorizacionBeneficiariosControladorEnum.NIT
                                                        .getValue()));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.NOMBRE_USUARIO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.IP_NUMERO_ORDEN
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_ORDEN
                                                        .getName()));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.MATRICULA_INMOBILIARIA
                                        .getValue(),
                        registroAux.getCampos().get(
                                        ValorizacionBeneficiariosControladorEnum.MATRICULA_INMOBILIARIA
                                                        .getValue()));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.DIRECCION
                                        .getValue(),
                        registroAux.getCampos().get(
                                        ValorizacionBeneficiariosControladorEnum.DIRECCION
                                                        .getValue()));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.DIRECCION_CORRESPONDENCIA
                                        .getValue(),
                        registroAux.getCampos().get(
                                        ValorizacionBeneficiariosControladorEnum.DIRECCION_CORRESPONDENCIA
                                                        .getValue()));

        registro.getCampos().put("DOCUMENTO_PROPIETARIO",
                        registroAux.getCampos().get(
                                        ValorizacionBeneficiariosControladorEnum.NIT
                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDestinacionEco
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDestinacionEco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.DESTINACION_ECONOMICA
                                        .getValue(),
                        registroAux.getCampos().get("ID"));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.FACTOR_DESTINACION_ECONOMICA
                                        .getValue(),
                        registroAux.getCampos().get("CODIGO"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaGradoBeneficio
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaGradoBeneficio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.GRADO_BENEFICIO
                                        .getValue(),
                        registroAux.getCampos().get("ID"));

        registro.getCampos().put(
                        ValorizacionBeneficiariosControladorEnum.FACTOR_GRADO_BENEFICIO
                                        .getValue(),
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEtapaProceso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEtapaProceso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRE_ETAPA",
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put("CODIGOETAPA",
                        registroAux.getCampos().get("CODIGO"));
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("ID");
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_PROYECTO", idProyecto);
        registro.getCampos().put("CODIGO_PROYECTO", codigoProyecto);
        registro.getCampos().put("CLASE", claseVP);
        registro.getCampos().remove("NOMBRETERCERO");
        registro.getCampos().remove("IP_USUARIO");
        registro.getCampos().remove("PRODESC");
        registro.getCampos().remove("AFECTACIONES");
        registro.getCampos().remove("AREA_NETA");
        registro.getCampos().remove("AREA_BRUTA");
        registro.getCampos().remove("NOMBRE_ETAPA");
        registro.getCampos().remove(
                        ValorizacionBeneficiariosControladorEnum.FACTOR_GRADO_BENEFICIO
                                        .getValue());
        registro.getCampos().remove(
                        ValorizacionBeneficiariosControladorEnum.FACTOR_DESTINACION_ECONOMICA
                                        .getValue());

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
        registro.getCampos().remove("ID");
        registro.getCampos().remove("NOMBRETERCERO");
        registro.getCampos().remove("IP_USUARIO");
        registro.getCampos().remove("PRODESC");
        registro.getCampos().remove("NOMBREUSUARIO");
        registro.getCampos().remove("CODIGO_HECHO");
        registro.getCampos().remove("ID_HECHOS_PROYECTOS");
        registro.getCampos().remove("AFECTACIONES");
        registro.getCampos().remove("AREA_NETA");
        registro.getCampos().remove("AREA_BRUTA");
        registro.getCampos().remove("NOMBRE_ETAPA");
        registro.getCampos().remove(
                        ValorizacionBeneficiariosControladorEnum.FACTOR_GRADO_BENEFICIO
                                        .getValue());
        registro.getCampos().remove(
                        ValorizacionBeneficiariosControladorEnum.FACTOR_DESTINACION_ECONOMICA
                                        .getValue());

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
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("idProyecto", idProyecto);
        parametros.put("codigoProyecto", codigoProyecto);
        parametros.put("claseProyecto", claseProyecto);
        parametros.put("rid", ridProyecto);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.VALORIZACION_PROYECTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaIpCodigo
     * 
     * @return listaIpCodigo
     */
    public RegistroDataModelImpl getListaIpCodigo() {
        return listaIpCodigo;
    }

    /**
     * Asigna la lista listaIpCodigo
     * 
     * @param listaIpCodigo
     * Variable a asignar en listaIpCodigo
     */
    public void setListaIpCodigo(RegistroDataModelImpl listaIpCodigo) {
        this.listaIpCodigo = listaIpCodigo;
    }

    /**
     * Retorna la lista listaDestinacionEco
     * 
     * @return listaDestinacionEco
     */
    public RegistroDataModelImpl getListaDestinacionEco() {
        return listaDestinacionEco;
    }

    /**
     * Asigna la lista listaDestinacionEco
     * 
     * @param listaDestinacionEco
     * Variable a asignar en listaDestinacionEco
     */
    public void setListaDestinacionEco(
        RegistroDataModelImpl listaDestinacionEco) {
        this.listaDestinacionEco = listaDestinacionEco;
    }

    /**
     * Retorna la lista listaGradoBeneficio
     * 
     * @return listaGradoBeneficio
     */
    public RegistroDataModelImpl getListaGradoBeneficio() {
        return listaGradoBeneficio;
    }

    /**
     * Asigna la lista listaGradoBeneficio
     * 
     * @param listaGradoBeneficio
     * Variable a asignar en listaGradoBeneficio
     */
    public void setListaGradoBeneficio(
        RegistroDataModelImpl listaGradoBeneficio) {
        this.listaGradoBeneficio = listaGradoBeneficio;
    }

    /**
     * @return the nombreClase
     */
    public String getNombreClase() {
        return nombreClase;
    }

    /**
     * @param nombreClase
     * the nombreClase to set
     */
    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    /**
     * @return the encabezado
     */
    public String getEncabezado() {
        return encabezado;
    }

    /**
     * @param encabezado
     * the encabezado to set
     */
    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }

    /**
     * Retorna la lista listaEtapaProceso
     * 
     * @return listaEtapaProceso
     */
    public RegistroDataModelImpl getListaEtapaProceso() {
        return listaEtapaProceso;
    }

    /**
     * Asigna la lista listaEtapaProceso
     * 
     * @param listaEtapaProceso
     * Variable a asignar en listaEtapaProceso
     */
    public void setListaEtapaProceso(RegistroDataModelImpl listaEtapaProceso) {
        this.listaEtapaProceso = listaEtapaProceso;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
