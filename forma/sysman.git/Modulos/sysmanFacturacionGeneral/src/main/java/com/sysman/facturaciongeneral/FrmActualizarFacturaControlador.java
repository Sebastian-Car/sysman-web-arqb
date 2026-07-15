/*-
 * FrmActualizarFacturaControlador.java
 *
 * 1.0
 * 
 * 16/11/2017
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
import com.sysman.facturaciongeneral.enums.FrmActualizarFacturaControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmActualizarFacturaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que actualiza las facturas recaudadas manualmente
 *
 * @version 1.0, 16/11/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmActualizarFacturaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String tipoCobro;

    private final String modulo;

    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el tipo de factura seleccionado de la
     * vista
     */
    private String tipoFactura;

    /**
     * Atributo que almacena el numero de factura seleccionado de la
     * vista
     */
    private String numeroFactura;

    /**
     * Atributo que alamacena el codigo de cobro de la factura
     */
    private String codigoCobro;

    /**
     * Atributo que almacena el tipo de comprobante seleccionado de la
     * vista
     */
    private String tipoComprobante;
    /**
     * Atributo que almacena la fecha de pago ingresada en la vista
     */
    private Date fechaPago;
    /**
     * Atributo que toma el ano de la fecha de pago ingresada en la
     * vista
     */
    private String anioPago;

    /**
     * Atributo que almacena el valor de la factura seleccionada en la
     * vista
     */
    private String valorFactura;

    /**
     * Atributo que almacena el tercero de la factura seleccionada en
     * la vista
     */
    private String nombreTercero;

    /**
     * Atributo que almacena el numero de comprobante seleccionado en
     * la vista
     */
    private String numeroComprobante;

    /**
     * Variable que almacena el indicador si la factura es diferida
     */

    private String diferida;

    /**
     * Variable que almacena la cuenta de recaudo de la factura
     */
    private String cuentaRecaudo;

    /**
     * Variable que almacena el valor del parametro SF MANEJA CONCEPTO
     * CON CUENTA DE RECAUDO
     */
    private String manejaConcepto;

    /**
     * Variable que almacena el codigo del banco de pago de la factura
     */
    private String bancoPago;

    /**
     * Variable que almacena el nombre del banco de pago de la factura
     */
    private String nombreBancoPago;

    private String numeroAbono;

    private String tipoAbono;

    private String cuotaAbono;
    /**
     * Variable que gestiona la visibilidad del campo banco en la
     * vista
     */
    private boolean verBanco;
    /**
     * Variable que gestiona la visibilidad de la ventana de dialogo
     * de facturas diferidas
     * 
     */

    private boolean visibleDialogoDiferida;
    /**
     * Variable que gestiona la visibilidad de la ventana de dialogo
     * de facturas vencidas
     * 
     */
    private boolean visibleDialogoVencida;
    /**
     * Variable que almacena el texto a mostrar en el dialogo de
     * facturas diferidas
     * 
     */
    private String textoDialogoDiferida;
    /**
     * Variable que almacena el texto a mostrar en el dialogo de
     * facturas vencidas
     * 
     */
    private String textoDialogoVencida;

    /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * de ingreso al modulo
     */
    private String ano;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena los tipos de facturas
     */
    private List<Registro> listaTIPOFACTURA;

    /**
     * Lista que almacena los componenetes de recuado
     */
    private List<Registro> listaCompRecaudo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que almacena las factueas
     */
    private RegistroDataModelImpl listaNOFACTURA;

    /**
     * Lista que almacena los numeros de comprobantes
     */
    private RegistroDataModelImpl listaNumComprobante;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionCero;

    /**
     * Crea una nueva instancia de FrmActualizarFacturaControlador
     */
    public FrmActualizarFacturaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();

        ano = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());

        usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACTUALIZAR_FACTURA_CONTROLADOR
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
        fechaPago = new Date();
        anioPago = Integer.toString(SysmanFunciones.ano(fechaPago));
        cargarListaTIPOFACTURA();
        cargarListaCompRecaudo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

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

        try {
            manejaConcepto = ejbSysmanUtl.consultarParametro(compania,
                            "SF MANEJA CONCEPTO CON CUENTA DE RECAUDO", modulo,
                            new Date(), false);

            verBanco = false;
            visibleDialogoDiferida = false;
            visibleDialogoVencida = false;
            textoDialogoDiferida = idioma.getString("TB_TB3801");
            textoDialogoVencida = idioma.getString("TB_TB3801");

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaTIPOFACTURA
     */
    public void cargarListaTIPOFACTURA() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmActualizarFacturaControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        try {
            listaTIPOFACTURA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmActualizarFacturaControladorUrlEnum.URL14423
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaNOFACTURA
     */
    public void cargarListaNOFACTURA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmActualizarFacturaControladorUrlEnum.URL14995
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmActualizarFacturaControladorEnum.TIPOFACTURA.getValue(),
                        tipoFactura);

        listaNOFACTURA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO_FACTURA");
    }

    /**
     * Carga la lista listaCompRecaudo
     */
    public void cargarListaCompRecaudo() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmActualizarFacturaControladorEnum.CLASECONTABLE.getValue(),
                        "I");

        try {
            listaCompRecaudo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmActualizarFacturaControladorUrlEnum.URL15659
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaNumComprobante
     */
    public void cargarListaNumComprobante() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmActualizarFacturaControladorUrlEnum.URL17525
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmActualizarFacturaControladorEnum.ANIO.getValue(),
                        ano);
        param.put(FrmActualizarFacturaControladorEnum.TIPO.getValue(),
                        tipoComprobante);

        listaNumComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton CmdActualizar en la vista
     *
     */
    public void oprimirCmdActualizar() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPROBANTE.getName(),
                        numeroComprobante);
        param.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComprobante);

        try {
            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmActualizarFacturaControladorUrlEnum.URL9595
                                                                            .getValue())
                                            .getUrl(), param));

            if (registro != null) {
                if (!"false".equals(diferida)) {

                    visibleDialogoDiferida = true;
                }
                else {
                    ejbFacturacionCero.actualizarPagosAbono(compania, tipoAbono,
                                    new BigInteger(numeroAbono),
                                    tipoFactura,
                                    new BigInteger(numeroFactura),
                                    new BigInteger(codigoCobro),
                                    fechaPago,
                                    tipoComprobante,
                                    new BigInteger(numeroComprobante),
                                    bancoPago, usuario);

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_PROCESO_EJECUTADO"));
                }

            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3803"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TIPOFACTURA
     * 
     */
    public void cambiarTIPOFACTURA() {
        cargarListaNOFACTURA();
    }

    /**
     * Metodo ejecutado al cambiar el control CompRecaudo
     * 
     */
    public void cambiarCompRecaudo() {
        cargarListaNumComprobante();
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * facturaDiferida en la vista
     *
     */
    public void aceptarfacturaDiferida() {

        Date fechaAbono;

        try {
            String mensaje = ejbFacturacionCero.consultarAbono(compania,
                            tipoAbono, new BigInteger(numeroAbono));

            if ("NE".equals(mensaje)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3804"));
                visibleDialogoDiferida = false;
                return;
            }
            else {

                String[] aux = mensaje.split(",");

                cuotaAbono = aux[0];
                fechaAbono = SysmanFunciones.convertirAFecha(aux[1]);

                if (SysmanFunciones.comparaFechas(fechaAbono, new Date())) {
                    visibleDialogoVencida = true;

                }
            }

            visibleDialogoDiferida = false;
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * facturaDiferida en la vista
     * 
     */
    public void cancelarfacturaDiferida() {

        visibleDialogoDiferida = false;
        actualizarPagosAbonos();
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * cuotaVencida en la vista
     * 
     */
    public void aceptarcuotaVencida() {
        visibleDialogoVencida = false;

        actualizarPagosAbonos();
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * cuotaVencida en la vista
     *
     */
    public void cancelarcuotaVencida() {
        visibleDialogoVencida = false;

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
        numeroFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_FACTURA"), "")
                        .toString();

        valorFactura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("VALOR_TOTAL"), "")
                        .toString();

        nombreTercero = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        diferida = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DIFERIDA"), "")
                        .toString();

        cuentaRecaudo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CUENTA_RECAUDO"), "")
                        .toString();

        tipoAbono = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO_ABONO"), "")
                        .toString();

        numeroAbono = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NRO_ABONO"), "0")
                        .toString();

        codigoCobro = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO_COBRO"), "0")
                        .toString();

        validarBancoDePago();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumComprobante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroComprobante = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();

        validarCuentaPago();
    }

    private void validarCuentaPago() {
        Registro registro = null;

        Map<String, Object> param = new TreeMap<>();
        try {
            if (SysmanFunciones.validarVariableVacio(bancoPago)) {

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                param.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                                tipoComprobante);

                param.put(GeneralParameterEnum.COMPROBANTE.getName(),
                                numeroComprobante);

                registro = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmActualizarFacturaControladorUrlEnum.URL4848
                                                                                .getValue())
                                                .getUrl(), param));
            }

            if (registro != null) {
                bancoPago = SysmanFunciones.nvl(registro.getCampos()
                                .get(GeneralParameterEnum.CUENTA.getName()),
                                "")
                                .toString();
            }
        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void validarBancoDePago() {
        Registro registro;

        Map<String, Object> param = new TreeMap<>();
        try {
            if (!SysmanFunciones.validarVariableVacio(cuentaRecaudo)
                && "SI".equals(manejaConcepto)) {

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                param.put(GeneralParameterEnum.ANO.getName(), anioPago);

                param.put(GeneralParameterEnum.CODIGO.getName(), cuentaRecaudo);

                param.put(FrmActualizarFacturaControladorEnum.CLASECUENTA
                                .getValue(), "B,J");

                registro = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmActualizarFacturaControladorUrlEnum.URL4545
                                                                                .getValue())
                                                .getUrl(), param));

                if (registro != null) {
                    bancoPago = SysmanFunciones.nvl(registro.getCampos()
                                    .get(GeneralParameterEnum.CODIGO.getName()),
                                    "")
                                    .toString();
                    nombreBancoPago = SysmanFunciones.nvl(registro.getCampos()
                                    .get(GeneralParameterEnum.NOMBRE.getName()),
                                    "")
                                    .toString();

                    verBanco = true;
                }
                else {
                    verBanco = false;
                }

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void actualizarPagosAbonos() {
        Map<String, Object> param = new TreeMap<>();
        try {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            param.put(GeneralParameterEnum.FECHAPAGO.getName(), fechaPago);
            param.put(FrmActualizarFacturaControladorEnum.ANODOC.getValue(),
                            anioPago);
            param.put(FrmActualizarFacturaControladorEnum.TIPODOC.getValue(),
                            tipoComprobante);
            param.put(FrmActualizarFacturaControladorEnum.NRODOC.getValue(),
                            numeroComprobante);
            param.put(FrmActualizarFacturaControladorEnum.TIPO_ABONO.getValue(),
                            tipoAbono);
            param.put(GeneralParameterEnum.BANCO_PAGO.getName(),
                            bancoPago);
            param.put(GeneralParameterEnum.ESTADO.getName(),
                            "R");
            param.put(FrmActualizarFacturaControladorEnum.CODIGO_ABONO
                            .getValue(), numeroAbono);

            param.put(GeneralParameterEnum.CUOTA.getName(),
                            SysmanFunciones.nvlStr(cuotaAbono, "0"));

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            UrlBean urlActualizar = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmActualizarFacturaControladorUrlEnum.URL20100
                                                            .getValue());

            requestManager.update(urlActualizar.getUrl(),
                            urlActualizar.getMetodo(),
                            parameter);

            ejbFacturacionCero.actualizarPagosAbono(compania, tipoAbono,
                            new BigInteger(numeroAbono),
                            tipoFactura,
                            new BigInteger(numeroFactura),
                            new BigInteger(codigoCobro),
                            fechaPago,
                            tipoComprobante,
                            new BigInteger(numeroComprobante),
                            bancoPago, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoFactura
     * 
     * @return tipoFactura
     */
    public String getTipoFactura() {
        return tipoFactura;
    }

    /**
     * Asigna la variable tipoFactura
     * 
     * @param tipoFactura
     * Variable a asignar en tipoFactura
     */
    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
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
     * Retorna la variable tipoComprobante
     * 
     * @return tipoComprobante
     */
    public String getTipoComprobante() {
        return tipoComprobante;
    }

    /**
     * Asigna la variable tipoComprobante
     * 
     * @param tipoComprobante
     * Variable a asignar en tipoComprobante
     */
    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    /**
     * Retorna la variable fechaPago
     * 
     * @return fechaPago
     */
    public Date getFechaPago() {
        return fechaPago;
    }

    /**
     * Asigna la variable fechaPago
     * 
     * @param fechaPago
     * Variable a asignar en fechaPago
     */
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getAnioPago() {
        return anioPago;
    }

    public void setAnioPago(String anioPago) {
        this.anioPago = anioPago;
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

    /**
     * Retorna la variable nombreTercero
     * 
     * @return nombreTercero
     */
    public String getNombreTercero() {
        return nombreTercero;
    }

    /**
     * Asigna la variable nombreTercero
     * 
     * @param nombreTercero
     * Variable a asignar en nombreTercero
     */
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    /**
     * Retorna la variable numeroComprobante
     * 
     * @return numeroComprobante
     */
    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    /**
     * Asigna la variable numeroComprobante
     * 
     * @param numeroComprobante
     * Variable a asignar en numeroComprobante
     */
    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getDiferida() {
        return diferida;
    }

    public void setDiferida(String diferida) {
        this.diferida = diferida;
    }

    public String getCuentaRecaudo() {
        return cuentaRecaudo;
    }

    public void setCuentaRecaudo(String cuentaRecaudo) {
        this.cuentaRecaudo = cuentaRecaudo;
    }

    public String getBancoPago() {
        return bancoPago;
    }

    public void setBancoPago(String bancoPago) {
        this.bancoPago = bancoPago;
    }

    public String getNombreBancoPago() {
        return nombreBancoPago;
    }

    public void setNombreBancoPago(String nombreBancoPago) {
        this.nombreBancoPago = nombreBancoPago;
    }

    public boolean isVerBanco() {
        return verBanco;
    }

    public void setVerBanco(boolean verBanco) {
        this.verBanco = verBanco;
    }

    public boolean isVisibleDialogoDiferida() {
        return visibleDialogoDiferida;
    }

    public void setVisibleDialogoDiferida(boolean visibleDialogoDiferida) {
        this.visibleDialogoDiferida = visibleDialogoDiferida;
    }

    public String getTextoDialogoDiferida() {
        return textoDialogoDiferida;
    }

    public void setTextoDialogoDiferida(String textoDialogoDiferida) {
        this.textoDialogoDiferida = textoDialogoDiferida;
    }

    public String getNumeroAbono() {
        return numeroAbono;
    }

    public void setNumeroAbono(String numeroAbono) {
        this.numeroAbono = numeroAbono;
    }

    public String getTipoAbono() {
        return tipoAbono;
    }

    public void setTipoAbono(String tipoAbono) {
        this.tipoAbono = tipoAbono;
    }

    public String getCuotaAbono() {
        return cuotaAbono;
    }

    public void setCuotaAbono(String cuotaAbono) {
        this.cuotaAbono = cuotaAbono;
    }

    public boolean isVisibleDialogoVencida() {
        return visibleDialogoVencida;
    }

    public void setVisibleDialogoVencida(boolean visibleDialogoVencida) {
        this.visibleDialogoVencida = visibleDialogoVencida;
    }

    public String getTextoDialogoVencida() {
        return textoDialogoVencida;
    }

    public void setTextoDialogoVencida(String textoDialogoVencida) {
        this.textoDialogoVencida = textoDialogoVencida;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTIPOFACTURA
     * 
     * @return listaTIPOFACTURA
     */
    public List<Registro> getListaTIPOFACTURA() {
        return listaTIPOFACTURA;
    }

    /**
     * Asigna la lista listaTIPOFACTURA
     * 
     * @param listaTIPOFACTURA
     * Variable a asignar en listaTIPOFACTURA
     */
    public void setListaTIPOFACTURA(List<Registro> listaTIPOFACTURA) {
        this.listaTIPOFACTURA = listaTIPOFACTURA;
    }

    /**
     * Retorna la lista listaCompRecaudo
     * 
     * @return listaCompRecaudo
     */
    public List<Registro> getListaCompRecaudo() {
        return listaCompRecaudo;
    }

    /**
     * Asigna la lista listaCompRecaudo
     * 
     * @param listaCompRecaudo
     * Variable a asignar en listaCompRecaudo
     */
    public void setListaCompRecaudo(List<Registro> listaCompRecaudo) {
        this.listaCompRecaudo = listaCompRecaudo;
    }

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

    /**
     * Retorna la lista listaNumComprobante
     * 
     * @return listaNumComprobante
     */
    public RegistroDataModelImpl getListaNumComprobante() {
        return listaNumComprobante;
    }

    /**
     * Asigna la lista listaNumComprobante
     * 
     * @param listaNumComprobante
     * Variable a asignar en listaNumComprobante
     */
    public void setListaNumComprobante(
        RegistroDataModelImpl listaNumComprobante) {
        this.listaNumComprobante = listaNumComprobante;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
