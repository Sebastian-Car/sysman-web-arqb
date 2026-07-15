/*-
 * FrmEliminarFacturaDiferidaControlador.java
 *
 * 1.0
 * 
 * 30 may. 2019
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
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.FrmEliminarFacturaDiferidaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que elimina facturas diferidas
 *
 * @version 1.0, 30/05/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmEliminarFacturaDiferidaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que almacena el codigo del usuario que ingresa a al
     * aplicacion
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Lista que carga los numeros de factura
     */
    private String numeroFactura;

    /**
     * Variable que almacena el anio por el que se ingresa al modulo
     */
    private String anio;

    /**
     * Variable que almacena el tipo de cobro por el que se ingresa al
     * modulo
     */
    private String tipoCobro;
    /**
     * Variable que almacena el numero de abono de la factura diferida
     */
    private String numeroAbono;

    /**
     * Variable que almacena el numero de cuotas pagas por la factura
     * diferida
     */
    private String cuotasPagadas;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las facturas diferidas
     */
    private RegistroDataModelImpl listaFacturaDiferida;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbFacturacionGeneralCuatroRemote ejbFacturacionGeneralCuatro;

    /**
     * Crea una nueva instancia de
     * FrmEliminarFacturaDiferidaControlador
     */
    public FrmEliminarFacturaDiferidaControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());

        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

        cuotasPagadas = "0";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ELIMINAR_FACTURA_DIFERIDA_CONTROLADOR
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
        cargarListaFacturaDiferida();
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
     * Carga la lista listaFacturaDiferida
     *
     */
    public void cargarListaFacturaDiferida() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEliminarFacturaDiferidaControladorUrlEnum.URL3701
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        param.put("TIPO_FACTURA", tipoCobro);

        listaFacturaDiferida = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_FACTURA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton EliminarDiferida en la
     * vista
     *
     */
    public void oprimirEliminarDiferida() {
        if (!validarPagoCuotas()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4305")
                            .replace("#CUOTAS#", cuotasPagadas));
        }
        else {

            try {
                ejbFacturacionGeneralCuatro.eliminarFacturaDiferida(compania,
                                tipoCobro, new BigInteger(numeroAbono),
                                new BigInteger(numeroFactura),
                                Integer.parseInt(anio), usuario);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }

            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarPagoCuotas() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), tipoCobro);
        param.put(GeneralParameterEnum.CODIGO.getName(), numeroAbono);

        try {
            Registro regAux = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmEliminarFacturaDiferidaControladorUrlEnum.URL10641
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            cuotasPagadas = regAux.getCampos().get("PAGADAS").toString();

            if (Integer.parseInt(cuotasPagadas) > 0) {
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaDiferida
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaDiferida(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_FACTURA"), "")
                        .toString();

        numeroAbono = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NRO_ABONO"), "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFacturaDiferida
     * 
     * @return listaFacturaDiferida
     */
    public RegistroDataModelImpl getListaFacturaDiferida() {
        return listaFacturaDiferida;
    }

    /**
     * Asigna la lista listaFacturaDiferida
     * 
     * @param listaFacturaDiferida
     * Variable a asignar en listaFacturaDiferida
     */
    public void setListaFacturaDiferida(
        RegistroDataModelImpl listaFacturaDiferida) {
        this.listaFacturaDiferida = listaFacturaDiferida;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
