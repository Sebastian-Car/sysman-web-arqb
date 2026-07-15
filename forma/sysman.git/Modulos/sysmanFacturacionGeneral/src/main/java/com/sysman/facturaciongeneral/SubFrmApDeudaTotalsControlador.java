/*-
 * SubFrmApDeudaTotalsControlador.java
 *
 * 1.0
 * 
 * 17/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.SubFrmApDeudaTotalsControladorEnum;
import com.sysman.facturaciongeneral.enums.SubFrmApDeudaTotalsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Controlador que gestiona la logica de la forma:
 * <code>subfrmapdeudatotal</code>. Resultado de la migracion del
 * formulario: <code>SUBFRM_AP_DEUDATOTAL</code> en Access.
 *
 * @version 1.0, 17/11/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class SubFrmApDeudaTotalsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SELECCIONADOS</code>.
     */
    private final String cSeleccionados = SubFrmApDeudaTotalsControladorEnum.SELECCIONADOS
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOCOBRO</code>.
     */
    private final String cTipoCobro = SubFrmApDeudaTotalsControladorEnum.TIPOCOBRO
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    /** Atributo que contiene la sumatoria del campo Base. */
    private String totalBase;

    /** Atributo que contiene la sumatoria del campo IVA. */
    private String totalIVA;

    /** Atributo que contiene la sumatoria del campo Ret. Fuente. */
    private String totalRetFuente;

    /** Atributo que contiene la sumatoria del campo Descuento. */
    private String totalDescuento;

    /** Atributo que contiene la sumatoria del campo ICA. */
    private String totalICA;

    /** Atributo que contiene la sumatoria del campo Total. */
    private String total;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Atributo que almacena el tipo de cobro seleccionado al ingresar
     * al modulo de facturacion general.
     */
    private String tipoCobro;

    /**
     * Atributo que almacena los numeros de factura seleccionados en
     * el controlador:
     * {@link com.sysman.facturaciongeneral.FrmAcuerdoPagoControlador}
     */
    private String seleccionados;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SubFrmApDeudaTotalsControlador
     */
    public SubFrmApDeudaTotalsControlador() {
        super();

        compania = SessionUtil.getCompania();

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        if (parametrosEntrada != null) {
            tipoCobro = (String) parametrosEntrada.get("tipoCobro");
            seleccionados = (String) parametrosEntrada.get("seleccionados");
        }

        try {
            // 1454
            numFormulario = GeneralCodigoFormaEnum.SUB_FRM_AP_DEUDA_TOTALS_CONTROLADOR
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
        tabla = GenericUrlEnum.SF_DETALLE_FACTURA.getTable();

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
        urlListado = UrlServiceUtil
                        .getUrlBeanById(SubFrmApDeudaTotalsControladorUrlEnum.URL0001
                                        .getValue());

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cTipoCobro, tipoCobro);
        parametrosListado.put(cSeleccionados, seleccionados);
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
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubFrmApDeudaTotalsControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), parametrosListado));

            if (regAux != null) {
                totalBase = formatearADecimal(regAux, "TOTALBASE", df);
                totalIVA = formatearADecimal(regAux, "TOTALIVA", df);
                totalRetFuente = formatearADecimal(regAux, "TOTALRETE", df);
                totalDescuento = formatearADecimal(regAux, "TOTALDTO", df);
                totalICA = formatearADecimal(regAux, "TOTALICA", df);
                total = formatearADecimal(regAux, "TOTALNETO", df);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return true -> Permitir realizar la insercion.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return true -> Permitir realizar la insercion o actualizacion.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return true -> Permitir eliminar el registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario.
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
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

    /**
     * Asigna la mascara <code>#,##0.00</code> al valor numerico del
     * campo: <code>nomCampo</code>.
     * 
     * @param parReg
     * -> Registro que contiene la coleccion de campos.
     * @param nomCampo
     * -> Nombre del campo.
     * @param df
     * -> Referencia de la estructura de formateo.
     * @return El valor formateado a decimal.
     */
    private String formatearADecimal(Registro parReg, String nomCampo,
        DecimalFormat df) {
        return df.format(Double.parseDouble(SysmanFunciones
                        .nvl(parReg.getCampos().get(nomCampo), "0")
                        .toString()));
    }

    // <SET_GET_ATRIBUTOS>
    public String getTotalBase() {
        return totalBase;
    }

    public void setTotalBase(String totalBase) {
        this.totalBase = totalBase;
    }

    public String getTotalIVA() {
        return totalIVA;
    }

    public void setTotalIVA(String totalIVA) {
        this.totalIVA = totalIVA;
    }

    public String getTotalRetFuente() {
        return totalRetFuente;
    }

    public void setTotalRetFuente(String totalRetFuente) {
        this.totalRetFuente = totalRetFuente;
    }

    public String getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(String totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public String getTotalICA() {
        return totalICA;
    }

    public void setTotalICA(String totalICA) {
        this.totalICA = totalICA;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
