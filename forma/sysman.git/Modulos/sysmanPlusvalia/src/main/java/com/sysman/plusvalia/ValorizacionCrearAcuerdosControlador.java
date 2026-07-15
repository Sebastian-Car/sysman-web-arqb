/*-
 * ValorizacionCrearAcuerdosControlador.java
 *
 * 1.0
 * 
 * 17/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroRemote;
import com.sysman.plusvalia.enums.ValorizacionCrearAcuerdosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 17/05/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ValorizacionCrearAcuerdosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String beneficiario;
    private String proyectos;
    private String numeroFactura;
    private String nombreProyecto;
    private String nombreBeneficiario;
    private String cuotaInicial;
    private String nCuotas;
    private String nResolucion;
    private String claseProyecto;
    private String idProyecto;
    private String idBeneficiario;
    private String idFactura;
    private String interes;
    private String modeloInteres;
    private String datosInforme;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBeneficiario;
    private RegistroDataModelImpl listaProyectos;
    private RegistroDataModelImpl listaFactura;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbPlusvaliaCeroRemote ejbPlusvaliaCeroRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ValorizacionCrearAcuerdosControlador
     */
    public ValorizacionCrearAcuerdosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2075
            numFormulario = GeneralCodigoFormaEnum.VALORIZACION_CREAR_ACUERDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            claseProyecto = "45";
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListaProyectos();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaBeneficiario
     *
     */
    public void cargarListaBeneficiario() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO_PROYECTO", proyectos);
        param.put(GeneralParameterEnum.CLASE.getName(), claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ValorizacionCrearAcuerdosControladorUrlEnum.URL1768
                                                        .getValue());

        listaBeneficiario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "IP_CODIGO");
    }

    /**
     * 
     * Carga la lista listaProyectos
     *
     */
    public void cargarListaProyectos() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CLASEVP", claseProyecto);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ValorizacionCrearAcuerdosControladorUrlEnum.URL1767
                                                        .getValue());

        listaProyectos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFactura
     *
     */
    public void cargarListaFactura() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PROYECTO.getName(), proyectos);
        param.put("BENEFICIARIO", idBeneficiario);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ValorizacionCrearAcuerdosControladorUrlEnum.URL1796
                                                        .getValue());

        listaFactura = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NUMERO_FACTURA");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CrearAcuerdo en la vista
     *
     *
     */
    public void oprimirCrearAcuerdo() {
        // <CODIGO_DESARROLLADO>
        funcionAcuerdo(false);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Proyectar en la vista
     *
     *
     */
    public void oprimirProyectar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        funcionAcuerdo(true);
    }

    // </METODOS_BOTONES>

    public void funcionAcuerdo(boolean preLiquidar) {

        try {
            datosInforme = ejbPlusvaliaCeroRemote.crearAcuerdo(compania,
                            Long.parseLong(idProyecto),
                            Long.parseLong(idFactura),
                            Long.parseLong(idBeneficiario),
                            new BigDecimal(cuotaInicial),
                            Integer.parseInt(nCuotas),
                            new BigDecimal(interes),
                            nResolucion,
                            modeloInteres,
                            SessionUtil.getUser().getCodigo(),
                            preLiquidar);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_PROCESO_EJECUTADO"));
            if (preLiquidar) {
                obtenerInforme(ReportesBean.FORMATOS.PDF);
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void obtenerInforme(ReportesBean.FORMATOS formato) {

        try {
            Map<String, Object> param = new HashMap<>();

            param.put("", "");

            archivoDescarga = JsfUtil.exportarStreamedJson(
                            "002014ProyectarAcuerdo", param,
                            datosInforme, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBeneficiario
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBeneficiario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        beneficiario = SysmanFunciones
                        .nvl(registroAux.getCampos().get("IP_CODIGO"), "")
                        .toString();

        nombreBeneficiario = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();

        idBeneficiario = SysmanFunciones.nvl(
                        registroAux.getCampos().get("ID"),
                        "").toString();
        numeroFactura = null;
        cargarListaFactura();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProyectos
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProyectos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyectos = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        nombreProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), "")
                        .toString();

        idProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "")
                        .toString();

        beneficiario = null;
        nombreBeneficiario = null;
        numeroFactura = null;
        cargarListaBeneficiario();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFactura
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFactura(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_FACTURA"), "")
                        .toString();

        idFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable beneficiario
     * 
     * @return beneficiario
     */
    public String getBeneficiario() {
        return beneficiario;
    }

    /**
     * Asigna la variable beneficiario
     * 
     * @param beneficiario
     * Variable a asignar en beneficiario
     */
    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    /**
     * Retorna la variable proyectos
     * 
     * @return proyectos
     */
    public String getProyectos() {
        return proyectos;
    }

    /**
     * Asigna la variable proyectos
     * 
     * @param proyectos
     * Variable a asignar en proyectos
     */
    public void setProyectos(String proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * Retorna la variable numeroFactura
     * 
     * @return numeroFactura
     */
    public String getNumeroFactura() {
        return numeroFactura;
    }

    /**
     * Asigna la variable numeroFactura
     * 
     * @param numeroFactura
     * Variable a asignar en numeroFactura
     */
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    /**
     * Retorna la variable nombreProyecto
     * 
     * @return nombreProyecto
     */
    public String getNombreProyecto() {
        return nombreProyecto;
    }

    /**
     * Asigna la variable nombreProyecto
     * 
     * @param nombreProyecto
     * Variable a asignar en nombreProyecto
     */
    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    /**
     * Retorna la variable nombreBeneficiario
     * 
     * @return nombreBeneficiario
     */
    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    /**
     * Asigna la variable nombreBeneficiario
     * 
     * @param nombreBeneficiario
     * Variable a asignar en nombreBeneficiario
     */
    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    /**
     * Retorna la variable cuotaInicial
     * 
     * @return cuotaInicial
     */
    public String getCuotaInicial() {
        return cuotaInicial;
    }

    /**
     * Asigna la variable cuotaInicial
     * 
     * @param cuotaInicial
     * Variable a asignar en cuotaInicial
     */
    public void setCuotaInicial(String cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    /**
     * Retorna la variable nCuotas
     * 
     * @return nCuotas
     */
    public String getNCuotas() {
        return nCuotas;
    }

    /**
     * Asigna la variable nCuotas
     * 
     * @param nCuotas
     * Variable a asignar en nCuotas
     */
    public void setNCuotas(String nCuotas) {
        this.nCuotas = nCuotas;
    }

    /**
     * Retorna la variable nResolucion
     * 
     * @return nResolucion
     */
    public String getNResolucion() {
        return nResolucion;
    }

    /**
     * Asigna la variable nResolucion
     * 
     * @param nResolucion
     * Variable a asignar en nResolucion
     */
    public void setNResolucion(String nResolucion) {
        this.nResolucion = nResolucion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * @return the nCuotas
     */
    public String getnCuotas() {
        return nCuotas;
    }

    /**
     * @param nCuotas
     * the nCuotas to set
     */
    public void setnCuotas(String nCuotas) {
        this.nCuotas = nCuotas;
    }

    /**
     * @return the nResolucion
     */
    public String getnResolucion() {
        return nResolucion;
    }

    /**
     * @param nResolucion
     * the nResolucion to set
     */
    public void setnResolucion(String nResolucion) {
        this.nResolucion = nResolucion;
    }

    /**
     * @return the listaBeneficiario
     */
    public RegistroDataModelImpl getListaBeneficiario() {
        return listaBeneficiario;
    }

    /**
     * @param listaBeneficiario
     * the listaBeneficiario to set
     */
    public void setListaBeneficiario(RegistroDataModelImpl listaBeneficiario) {
        this.listaBeneficiario = listaBeneficiario;
    }

    /**
     * @return the listaProyectos
     */
    public RegistroDataModelImpl getListaProyectos() {
        return listaProyectos;
    }

    /**
     * @param listaProyectos
     * the listaProyectos to set
     */
    public void setListaProyectos(RegistroDataModelImpl listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    /**
     * @return the listaFactura
     */
    public RegistroDataModelImpl getListaFactura() {
        return listaFactura;
    }

    /**
     * @param listaFactura
     * the listaFactura to set
     */
    public void setListaFactura(RegistroDataModelImpl listaFactura) {
        this.listaFactura = listaFactura;
    }

    /**
     * Retorna la variable interes
     * 
     * @return interes
     */
    public String getInteres() {
        return interes;
    }

    /**
     * Asigna la variable interes
     * 
     * @param interes
     * Variable a asignar en interes
     */
    public void setInteres(String interes) {
        this.interes = interes;
    }

    /**
     * @return the modeloInteres
     */
    public String getModeloInteres() {
        return modeloInteres;
    }

    /**
     * @param modeloInteres
     * the modeloInteres to set
     */
    public void setModeloInteres(String modeloInteres) {
        this.modeloInteres = modeloInteres;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
