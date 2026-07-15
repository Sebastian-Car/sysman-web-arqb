/*-
 * FrmCopiarDeControlador.java
 *
 * 1.0
 * 
 * 18/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.FrmCopiarDeControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite copiar los detalles de un movimiento de
 * almacen.
 *
 * @version 1.0, 18/03/2019
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class FrmCopiarDeControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String tipo;
    private String numero;
    private String tipoCop;
    private String numeroCop;
    private Date fecha;
    private String nombreTipo;
    private String descripcion;
    private String tercero;
    private String sucursal;
    private String auxiliar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoDeMovimiento;
    private RegistroDataModelImpl listaNumero;

    @EJB
    private EjbAlmacenCincoRemote ejbAlmacenCinco;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCopiarDeControlador
     */
    public FrmCopiarDeControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCOPIARDE_CONTROLADOR
                            .getCodigo();
            if (parametros != null) {
                tipoCop = (String) parametros.get("tipo");
                numeroCop = (String) parametros.get("numero");
                fecha = (Date) parametros.get("fecha");
                tercero = (String) parametros.get("tercero");
                sucursal = (String) parametros.get("sucursal");
                auxiliar = (String) parametros.get("auxiliar");
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoDeMovimiento();
        cargarListaNumero();
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
        /*
         * FR2046-AL_ABRIR Private Sub Form_Load() ' formularioAbrir
         * 10, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaTipoDeMovimiento
     *
     */
    public void cargarListaTipoDeMovimiento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "S");
        param.put(GeneralParameterEnum.TIPO.getName(), "C");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCopiarDeControladorUrlEnum.URL79165
                                                        .getValue());
        listaTipoDeMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * Carga la lista listaNumero
     *
     */
    public void cargarListaNumero() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCopiarDeControladorUrlEnum.URL52478
                                                        .getValue());
        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbAlmacenCinco.copiarDeMovimiento(compania, tipoCop,
                            new BigInteger(numeroCop), tipo,
                            new BigInteger(numero), fecha,
                            tercero,
                            sucursal,
                            auxiliar,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Salir en la vista
     *
     */
    public void oprimirSalir() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("accion", "0");
        SessionUtil.setFlash(parametros);
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoDeMovimiento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDeMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = registroAux.getCampos().get("CODIGO").toString();
        nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaNumero();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = registroAux.getCampos().get("NUMERO").toString();
        descripcion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable numero
     * 
     * @return numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Asigna la variable numero
     * 
     * @param numero
     * Variable a asignar en numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Retorna la variable nombreTipo
     * 
     * @return nombreTipo
     */
    public String getNombreTipo() {
        return nombreTipo;
    }

    /**
     * Asigna la variable nombreTipo
     * 
     * @param nombreTipo
     * Variable a asignar en nombreTipo
     */
    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    /**
     * Retorna la variable descripcion
     * 
     * @return descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asigna la variable descripcion
     * 
     * @param descripcion
     * Variable a asignar en descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoDeMovimiento
     * 
     * @return listaTipoDeMovimiento
     */
    public RegistroDataModelImpl getListaTipoDeMovimiento() {
        return listaTipoDeMovimiento;
    }

    /**
     * Asigna la lista listaTipoDeMovimiento
     * 
     * @param listaTipoDeMovimiento
     * Variable a asignar en listaTipoDeMovimiento
     */
    public void setListaTipoDeMovimiento(
        RegistroDataModelImpl listaTipoDeMovimiento) {
        this.listaTipoDeMovimiento = listaTipoDeMovimiento;
    }

    /**
     * Retorna la lista listaNumero
     * 
     * @return listaNumero
     */
    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    /**
     * Asigna la lista listaNumero
     * 
     * @param listaNumero
     * Variable a asignar en listaNumero
     */
    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
