/*-
 * FrmEliminarRecaudoControlador.java
 *
 * 1.0
 * 
 * 09/11/2017
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
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.enums.FrmEliminarRecaudoControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmEliminarRecaudoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase que permite eliminar un recaudo.
 *
 * @version 1.0, 09/11/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmEliminarRecaudoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String numFactura;
    private String valorFactura;

    /**
     * Tipo de cobro que ha sido selecionado al ingresar al modulo de
     * Facturacion General
     */
    private String tipoCobro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaNOFACTURA;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionGeneralCero;

    /**
     * Crea una nueva instancia de FrmEliminarRecaudoControlador
     */
    public FrmEliminarRecaudoControlador() {
        super();
        compania = SessionUtil.getCompania();

        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ELIMINAR_RECAUDO_CONTROLADOR
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
        cargarListaNOFACTURA();
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
     * Carga la lista listaNOFACTURA
     *
     */
    public void cargarListaNOFACTURA() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmEliminarRecaudoControladorEnum.PARAM0.getValue(),
                        tipoCobro);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEliminarRecaudoControladorUrlEnum.URL3922
                                                        .getValue());
        listaNOFACTURA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NUMERO_FACTURA");
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

            ejbFacturacionGeneralCero.eliminarRecaudo(compania, tipoCobro,
                            new BigInteger(numFactura),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TIPOFACTURA
     * 
     */
    public void cambiarTIPOFACTURA() {
        // <CODIGO_DESARROLLADO>
        cargarListaNOFACTURA();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNOFACTURA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNOFACTURA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numFactura = registroAux.getCampos().get("NUMERO_FACTURA").toString();
        valorFactura = registroAux.getCampos().get("VALOR_TOTAL").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable numFactura
     * 
     * @return numFactura
     */
    public String getNumFactura() {
        return numFactura;
    }

    /**
     * Asigna la variable numFactura
     * 
     * @param numFactura
     * Variable a asignar en numFactura
     */
    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    /**
     * Retorna la variable valorFactura
     * 
     * @return valorFactura
     */
    public String getValorFactura() {
        return valorFactura;
    }

    /**
     * Asigna la variable valorFactura
     * 
     * @param valorFactura
     * Variable a asignar en valorFactura
     */
    public void setValorFactura(String valorFactura) {
        this.valorFactura = valorFactura;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaNOFACTURA
     * 
     * @return listaNOFACTURA
     */
    public RegistroDataModelImpl getListaNOFACTURA() {
        return listaNOFACTURA;
    }

    /**
     * Asigna la lista listaNOFACTURA
     * 
     * @param listaNOFACTURA
     * Variable a asignar en listaNOFACTURA
     */
    public void setListaNOFACTURA(RegistroDataModelImpl listaNOFACTURA) {
        this.listaNOFACTURA = listaNOFACTURA;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
