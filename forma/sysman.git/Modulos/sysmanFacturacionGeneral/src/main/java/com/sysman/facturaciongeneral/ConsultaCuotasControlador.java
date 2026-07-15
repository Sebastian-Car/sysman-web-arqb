/*-
 * ConsultaCuotasControlador.java
 *
 * 1.0
 * 
 * 21/11/2017
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
import com.sysman.facturaciongeneral.enums.ConsultaCuotasControladorEnum;
import com.sysman.facturaciongeneral.enums.ConsultaCuotasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario
 * "Distribución de Cuotas por Concepto" en Access
 * "SUBFRM_CONSULTA_CUOTA", el cual es llamado desde
 * Facturación\Procesos\Acuerdos de Pago\Consultar Acuerdo (Boton
 * Distribucion)
 *
 * @version 1.0, 21/11/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ConsultaCuotasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Tipo de cobro que ha sido selecionado al ingresar al modulo de
     * Facturacion General
     */
    private String tipoCobro;
    /**
     * Codigo que identifica Acuerdo de Pago que se esta trabajando
     */
    private String codigoAcuerdo;
    /**
     * Numero de cuota, perteneciente al acuerdo de pago que se esta
     * trabajado y de la cual se quiere definir el detalle
     */
    private String cuota;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del total Base para las cuotas
     */
    private String totalBase;
    /**
     * Atributo que almacena el valor del total Iva para las cuotas
     */
    private String totalIva;
    /**
     * Atributo que almacena el valor del Retefuente Base para las
     * cuotas
     */
    private String totalRetefuente;
    /**
     * Atributo que almacena el valor del total Descuento para las
     * cuotas
     */
    private String totalDescuento;
    /**
     * Atributo que almacena el valor del total Ica para las cuotas
     */
    private String totalIca;
    /**
     * Atributo que almacena el valor del total Cuota para las cuotas
     */
    private String totalCuota;
    /**
     * Almacena los valores llave del acuerdo desde el que ha sido
     * redireccioando a este formulario
     */
    private Map<String, Object> ridAcuerdo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConsultaCuotasControlador
     */
    public ConsultaCuotasControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        try {
            numFormulario = GeneralCodigoFormaEnum.CONSULTA_CUOTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codigoAcuerdo = parametrosEntrada.get("codigoAcuerdo")
                                .toString();
                cuota = parametrosEntrada.get("cuota").toString();
                ridAcuerdo = (Map<String, Object>) parametrosEntrada.get("rid");
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
        tabla = ConsultaCuotasControladorEnum.SF_DETALLE_CUOTA.getValue();
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
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsultaCuotasControladorUrlEnum.URL0002
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        ConsultaCuotasControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        parametrosListado.put(
                        ConsultaCuotasControladorEnum.CODACUERDO.getValue(),
                        codigoAcuerdo);
        parametrosListado.put(ConsultaCuotasControladorEnum.NUMCUOTA.getValue(),
                        cuota);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * Asigna el identificador del concepto seleccionado al registro
     * del formulario principal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * Asigna el identificador del concepto seleccionado al registro
     * del formulario principal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
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
        calcularTotales();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     * Realiza la redireccion al formulario ConsultaAcuerdoPagos(1457)
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridAcuerdo);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CONSULTA_ACUERDO_PAGOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
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
     * Realiza el calculo de los totales que seran visualizados en el
     * pie del formulario
     */
    private void calcularTotales() {
        // <CODIGO_DESARROLLADO>
        try {
            Registro regTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsultaCuotasControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), parametrosListado));

            totalBase = formatearValor(regTotales, "TOTALBASE");
            totalIva = formatearValor(regTotales, "TOTALIVA");
            totalRetefuente = formatearValor(regTotales, "TOTALRETE");
            totalDescuento = formatearValor(regTotales, "TOTALDESCUENTO");
            totalIca = formatearValor(regTotales, "TOTALICA");
            totalCuota = formatearValor(regTotales, "TOTALCUOTA");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Adiciona el formato de moneda a los totales que se presentan en
     * el pie del formulario
     * 
     * @param registro
     * Registro que obtiene los valores a formatear
     * @param campo
     * Nombre del campo a formatear
     * @return Valor con el formato moneda aplicado
     */
    private String formatearValor(Registro registro, String campo) {
        return new java.text.DecimalFormat(" #,##0.00")
                        .format(Double.parseDouble(registro.getCampos()
                                        .get(campo).toString()));
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable totalBase
     * 
     * @return totalBase
     */
    public String getTotalBase() {
        return totalBase;
    }

    /**
     * Asigna valor a lavariable totalBase
     * 
     * @param totalBase
     */
    public void setTotalBase(String totalBase) {
        this.totalBase = totalBase;
    }

    /**
     * Retorna la variable totalIva
     * 
     * @return totalIva
     */
    public String getTotalIva() {
        return totalIva;
    }

    /**
     * Asigna valor a lavariable totalIva
     * 
     * @param totalIva
     */
    public void setTotalIva(String totalIva) {
        this.totalIva = totalIva;
    }

    /**
     * Retorna la variable totalRetefuente
     * 
     * @return totalRetefuente
     */
    public String getTotalRetefuente() {
        return totalRetefuente;
    }

    /**
     * Asigna valor a la variable totalRetefuente
     * 
     * @param totalRetefuente
     */
    public void setTotalRetefuente(String totalRetefuente) {
        this.totalRetefuente = totalRetefuente;
    }

    /**
     * Retorna la variable totalIca
     * 
     * @return totalIca
     */
    public String getTotalDescuento() {
        return totalDescuento;
    }

    /**
     * Asigna valor a la variable totalDescuento
     * 
     * @param totalDescuento
     */
    public void setTotalDescuento(String totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    /**
     * Retorna la variable totalIca
     * 
     * @return totalIca
     */
    public String getTotalIca() {
        return totalIca;
    }

    /**
     * Asigna valor a la variable totalIca
     * 
     * @param totalIca
     */
    public void setTotalIca(String totalIca) {
        this.totalIca = totalIca;
    }

    /**
     * Retorna la variable totalCuota
     * 
     * @return totalCuota
     */
    public String getTotalCuota() {
        return totalCuota;
    }

    /**
     * Asigna valor a la variable totalCuota
     * 
     * @param totalCuota
     */
    public void setTotalCuota(String totalCuota) {
        this.totalCuota = totalCuota;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
