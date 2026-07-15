/*-
 * FrmImprimirFacturaFrida.java
 *
 * 1.0
 * 
 * 28/04/2021
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
import com.sysman.facturaciongeneral.enums.FrmImprimirFacturaFridaEnum;
import com.sysman.facturaciongeneral.enums.FrmImprimirFacturaFridaUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite imprimir las facturas de FRIDA
 *
 * @version 1.0, 28/04/2021
 * @author eamaya
 */
@ManagedBean
@ViewScoped

class FrmImprimirFacturaFrida extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String factura;
    /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * de ingreso al modulo
     */
    private String anio;
    /**
     * Variable encargada de almacenar temporalmente el tipo de cobro
     * de ingreso al modulo
     */
    private String tipoCobro;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaFacturaInicial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmImprimirFacturaFrida
     */
    public FrmImprimirFacturaFrida() {
        super();
        compania = SessionUtil.getCompania();
        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        try {
            // 2270
            numFormulario = GeneralCodigoFormaEnum.FRM_IMPRIMIRFACTURADIFEREIDA
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
                                        FrmImprimirFacturaFridaUrlEnum.URL4309
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmImprimirFacturaFridaEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaFacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_FACTURA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
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
        factura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_FACTURA"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable factura
     * 
     * @return factura
     */
    public String getFactura() {
        return factura;
    }

    /**
     * Asigna la variable factura
     * 
     * @param factura
     * Variable a asignar en factura
     */
    public void setFactura(String factura) {
        this.factura = factura;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
