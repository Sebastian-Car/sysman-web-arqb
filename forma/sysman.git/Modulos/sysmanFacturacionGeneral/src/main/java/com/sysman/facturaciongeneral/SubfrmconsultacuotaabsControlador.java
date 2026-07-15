/*-
 * SubfrmconsultacuotaabsControlador.java
 *
 * 1.0
 * 
 * 28/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.SubfrmconsultacuotaabsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase encargada de visualizar el detalle de las cuotas por
 * conceptos
 *
 * @version 1.0, 28/02/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class SubfrmconsultacuotaabsControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable encargada de almacenar temporalmente el tipo
     * facturacion de ingreso al modulo
     */
    private String tipo;
    /**
     * Variable encargada de almacenar temporalmente el codigo del
     * abono
     */
    private String codigo;
    /**
     * Variable encargada de almacenar temporalmente la cuota
     */
    private String cuota;
    /**
     * Variable encargada de la visualizacion de la sumatoria de la
     * columna base
     */
    private String totalBase;
    /**
     * Variable encargada de la visualizacion de la sumatoria de la
     * columna iva
     */
    private String totalIva;
    /**
     * Variable encargada de la visualizacion de la sumatoria de la
     * columna totalRete
     */
    private String totalRete;
    /**
     * Variable encargada de la visualizacion de la sumatoria de la
     * columna total Descuento
     */
    private String totalDescuento;
    /**
     * Variable encargada de la visualizacion de la sumatoria de la
     * columna total ica
     */
    private String totalIca;
    /**
     * Variable encargada de la visualizacion de la sumatoria de la
     * columna total Totales
     */
    private String totalTotales;
    /**
     * Constante encargada de almacenar String "$ #,##0.00"
     */
    private final String formatoMonedaCons;
    /**
     * Constante encargada de almacenar String "SF_DETALLE_CUOTAABONO"
     */
    private final String sfDetalleCuotaAbono;
    /**
     * Variable encargada de almacenar las llaves del registro
     * anterior
     */
    private Map<String, Object> ridConsulta;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubfrmconsultacuotaabsControlador
     */
    @SuppressWarnings("unchecked")
    public SubfrmconsultacuotaabsControlador() {
        super();
        compania = SessionUtil.getCompania();
        sfDetalleCuotaAbono = "SF_DETALLE_CUOTAABONO";
        formatoMonedaCons = "$ #,##0.00";
        Map<String, Object> parametros = SessionUtil.getFlash();

        if (parametros != null) {
            tipo = (String) parametros.get("tipo");
            codigo = (String) parametros.get("codigo");
            cuota = (String) parametros.get("cuota");
            ridConsulta = (Map<String, Object>) parametros.get("rid");

        }

        try {
            numFormulario = GeneralCodigoFormaEnum.SUBFRMCONSULTACUOTAABS_CONTROLADOR
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

        tabla = sfDetalleCuotaAbono;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("TIPO", tipo);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        parametrosListado.put(GeneralParameterEnum.CUOTA.getName(), cuota);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubfrmconsultacuotaabsControladorUrlEnum.URL9923
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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
        generarTotalesSub();
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
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
        //
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridConsulta);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FRMCONSULTAABONOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    public String getTotalBase() {
        return totalBase;
    }

    public void setTotalBase(String totalBase) {
        this.totalBase = totalBase;
    }

    public String getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(String totalIva) {
        this.totalIva = totalIva;
    }

    public String getTotalRete() {
        return totalRete;
    }

    public void setTotalRete(String totalRete) {
        this.totalRete = totalRete;
    }

    public String getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(String totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public String getTotalIca() {
        return totalIca;
    }

    public void setTotalIca(String totalIca) {
        this.totalIca = totalIca;
    }

    public String getTotalTotales() {
        return totalTotales;
    }

    public void setTotalTotales(String totalTotales) {
        this.totalTotales = totalTotales;
    }

    private void generarTotalesSub() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
            param.put("TIPO", tipo);
            param.put(GeneralParameterEnum.CUOTA.getName(), cuota);

            Registro regTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubfrmconsultacuotaabsControladorUrlEnum.URL23432
                                                                            .getValue())
                                            .getUrl(), param));

            totalBase = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(
                                            retornarDouble(regTotales,
                                                            "BASE"));
            totalIva = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "IVA"));
            totalRete = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "RETE"));
            totalDescuento = new java.text.DecimalFormat(
                            formatoMonedaCons)
                                            .format(retornarDouble(regTotales,
                                                            "DESCUENTO"));

            totalIca = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "ICA"));
            totalTotales = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "TOTALES"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private double retornarDouble(Registro reg, String campo) {
        return Double.parseDouble(SysmanFunciones
                        .nvl(reg.getCampos().get(campo), "0").toString());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
