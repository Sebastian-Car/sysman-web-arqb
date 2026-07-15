/*-
 * FrmAnularFacturaControlador.java
 *
 * 1.0
 * 
 * 27/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoRemote;
import com.sysman.facturaciongeneral.enums.FrmAnularFacturaControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmAnularFacturaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase que permite anular determinado numero de facturas.
 *
 * @version 1.0, 27/11/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmAnularFacturaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consNumeroFactura;
    // <DECLARAR_ATRIBUTOS>
    private String facturaInicial;
    private String facturaFinal;
    private String tipoCobro;
    private String anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaFacturaInicial;
    private RegistroDataModelImpl listaFacturaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    
    @EJB
    private EjbFacturacionGeneralUnoRemote ejbFacturacionGeneralUno;
    
    /**
     * Crea una nueva instancia de FrmAnularFacturaControlador
     */
    public FrmAnularFacturaControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNumeroFactura = "NUMERO_FACTURA";
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();
        anio = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ANULAR_FACTURACION_CONTROLADOR
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFacturaInicial();
        cargarListaFacturaFinal();
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
     * Carga la lista listaFacturaInicial
     *
     */
    public void cargarListaFacturaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAnularFacturaControladorUrlEnum.URL4507
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmAnularFacturaControladorEnum.PARAM0.getValue(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        
        listaFacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consNumeroFactura);

    }

    /**
     * 
     * Carga la lista listaFacturaFinal
     *
     */
    public void cargarListaFacturaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAnularFacturaControladorUrlEnum.URL6170
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmAnularFacturaControladorEnum.PARAM0.getValue(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.FACTURA.getName(), facturaInicial);
        listaFacturaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        consNumeroFactura);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAceptar en la vista
     *
     */
    public void oprimirCmdAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbFacturacionGeneralUno.ejecutarAnulacionFacturas(compania,
                            tipoCobro, Integer.parseInt(anio),
                            new BigInteger(facturaInicial),
                            new BigInteger(facturaFinal),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturaInicial = registroAux.getCampos().get(consNumeroFactura)
                        .toString();
        cargarListaFacturaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturaFinal = registroAux.getCampos().get(consNumeroFactura)
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable facturaInicial
     * 
     * @return facturaInicial
     */
    public String getFacturaInicial() {
        return facturaInicial;
    }

    /**
     * Asigna la variable facturaInicial
     * 
     * @param facturaInicial
     * Variable a asignar en facturaInicial
     */
    public void setFacturaInicial(String facturaInicial) {
        this.facturaInicial = facturaInicial;
    }

    /**
     * Retorna la variable facturaFinal
     * 
     * @return facturaFinal
     */
    public String getFacturaFinal() {
        return facturaFinal;
    }

    /**
     * Asigna la variable facturaFinal
     * 
     * @param facturaFinal
     * Variable a asignar en facturaFinal
     */
    public void setFacturaFinal(String facturaFinal) {
        this.facturaFinal = facturaFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFacturaInicial
     * 
     * @return listaFacturaInicial
     */
    public RegistroDataModelImpl getListaFacturaInicial() {
        return listaFacturaInicial;
    }

    /**
     * Asigna la lista listaFacturaInicial
     * 
     * @param listaFacturaInicial
     * Variable a asignar en listaFacturaInicial
     */
    public void setListaFacturaInicial(
        RegistroDataModelImpl listaFacturaInicial) {
        this.listaFacturaInicial = listaFacturaInicial;
    }

    /**
     * Retorna la lista listaFacturaFinal
     * 
     * @return listaFacturaFinal
     */
    public RegistroDataModelImpl getListaFacturaFinal() {
        return listaFacturaFinal;
    }

    /**
     * Asigna la lista listaFacturaFinal
     * 
     * @param listaFacturaFinal
     * Variable a asignar en listaFacturaFinal
     */
    public void setListaFacturaFinal(RegistroDataModelImpl listaFacturaFinal) {
        this.listaFacturaFinal = listaFacturaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
