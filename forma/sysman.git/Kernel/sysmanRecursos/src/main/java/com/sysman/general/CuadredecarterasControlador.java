/*-
 * CuadredecarterasControlador.java
 *
 * 1.0
 * 
 * 22/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.ibm.icu.text.SimpleDateFormat;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteGeneralRemote;
import com.sysman.general.enums.CuadredecarterasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite ingresar a la informacion de cuentas por cobrar
 *
 * @version 1.0, 20/04/2021
 * @author jacevedo
 */
@ManagedBean
@ViewScoped
public class CuadredecarterasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la
     * cual inicio sesion el usuario, el valor de esta constante es asignado en
     * el constructor a la variable de sesion correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista de terceros
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * lista de cuentas con el indicador cuentas por cobrar
     */
    private RegistroDataModelImpl listacuenta;
    /**
     * lista de detalles con cuentas por cobrar y saldos
     */
    private RegistroDataModelImpl listadetalleComCnt;

    private Date fechaCorte;
    private String cuenta;
    private String tercero;
    private String nombretercero;
    private boolean bloqueaAbonoinicial;
    private String ano;
    private Registro subRegistro = new Registro();

    private String totalDeuda;
    private String saldoTotal;
    private String saldo1;
    private String saldo2;
    private String saldo3;
    private String saldo4;
    private String saldo5;
    private String saldo6;
    private String saldo7;
    private String valorDebito;
    private String valorCredito;
    private String debitoAfectado;
    private String creditoAfectado;
    private String abonoInicial;
    private boolean fechaVencimiento;

    @EJB
    private EjbContabilidadSieteGeneralRemote ejbContabilidadSiete;

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getCompania() {
        return compania;
    }

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CuadredecarterasControlador
     */
    public CuadredecarterasControlador() {
        super();
        compania = SessionUtil.getCompania();
        bloqueaAbonoinicial = false;
        fechaCorte = new Date();
        try {
            numFormulario = 2261;
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del
     * Bean ha sido creado, en este se realizan las asignaciones iniciales
     * necesarias para la visualizacion del formulario, como son tablas,
     * origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        tabla = "DETALLE_COMPROBANTE_CNT";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();

        // <CARGAR_LISTA>
        cargarListaTercero();
        cargarListacuenta();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor
     * de la consulta del formulario. Tambien carga la lista del formulario por
     * primera vez
     */
    @Override
    public void reasignarOrigen() {
        ano = String.valueOf(SysmanFunciones.ano(fechaCorte));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(fechaCorte);

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        parametrosListado.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        parametrosListado.put(GeneralParameterEnum.FECHA.getName(), strDate);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);

        if (!fechaVencimiento) {

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CuadredecarterasControladorUrlEnum.URL003
                                                            .getValue());

            cargado = false;

        }
        else {

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CuadredecarterasControladorUrlEnum.URL39101
                                                            .getValue());

            cargado = false;

        }

    }

    private void generarTotales() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(fechaCorte);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.FECHA.getName(), strDate);

        String formatoMonedaCons = "#,##0.00";

        Registro regTotales;

        try {

            if (fechaVencimiento) {

                regTotales = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                CuadredecarterasControladorUrlEnum.URL39103
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

            }
            else {

                regTotales = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                CuadredecarterasControladorUrlEnum.URL39100
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

            }

            if (regTotales != null) {

                totalDeuda = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales,
                                                "TOTALDEUDA"));

                saldoTotal = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDOT"));

                saldo1 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO1T"));

                saldo2 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO2T"));

                saldo3 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO3T"));

                saldo4 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO4T"));

                saldo5 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO5T"));

                saldo6 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO6T"));

                saldo7 = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales, "SALDO7T"));

                valorDebito = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales,
                                                "VALOR_DEBITO"));

                valorCredito = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales,
                                                "VALOR_CREDITO"));

                debitoAfectado = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales,
                                                "DEBITO_AFECTADO"));

                creditoAfectado = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales,
                                                "CREDITO_AFECTADO"));

                abonoInicial = new java.text.DecimalFormat(formatoMonedaCons)
                                .format(retornarDouble(regTotales,
                                                "ABONOINICIAL"));
            }

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

    @Override
    public void editar(RowEditEvent event) {
        subRegistro = (Registro) event.getObject();

        double diferencia = Double.parseDouble(
                        subRegistro.getCampos().get("TOTALDEUDA").toString())
            -
            Double.parseDouble(subRegistro.getCampos().get("ABONOINICIAL")
                            .toString());

        if (diferencia >= 0) {
            try {

                String anio = subRegistro.getCampos().get("ANO").toString();
                String comprobante = subRegistro.getCampos()
                                .get("COMPROBANTECARTERA").toString();
                String tipocomp = subRegistro.getCampos().get("TIPO_CPTE")
                                .toString();
                String consecutivo = subRegistro.getCampos().get("CONSECUTIVO")
                                .toString();
                String compani = subRegistro.getCampos().get("COMPANIA")
                                .toString();
                String abononuevo = subRegistro.getCampos().get("ABONOINICIAL")
                                .toString();

                Date fechaAbono = (Date) SysmanFunciones
                                .nvl(subRegistro.getCampos()
                                                .get("FECHA_ABONOINICIAL"),
                                                new Date());

                ejbContabilidadSiete.actualizarAbono(compani,
                                Integer.parseInt(anio), tipocomp,
                                new BigInteger(comprobante),
                                Integer.parseInt(consecutivo),
                                new BigDecimal(abononuevo),
                                fechaAbono, SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                "Registro guardado correctamente");

                bloqueaAbonoinicial = true;
            }
            catch (SystemException | NumberFormatException | ParseException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            "El valor abonado no puede ser mayor al valor de la deuda");
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuadredecarterasControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NIT.getName());
    }

    /**
     * 
     * Carga la lista listacuentas por cobrar
     */
    public void cargarListacuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CuadredecarterasControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listacuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * Metodo ejecutado al cambiar el control FechaCorte
     * 
     */
    public void cambiarFechaCorte() {
        reasignarOrigen();

        generarTotales();
        cargarListacuenta();
    }

    /**
     * Metodo ejecutado al cambiar el control FechaVencimiento
     * 
     */
    public void cambiarFechaVencimiento() {
        reasignarOrigen();
        generarTotales();
        cargarListacuenta();
    }

    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tercero = registroAux == null ? ""
            : registroAux.getCampos().get("NIT").toString();
        nombretercero = registroAux == null ? ""
            : registroAux.getCampos().get("NOMBRE").toString();

        reasignarOrigen();

        generarTotales();

    }

    public void seleccionarFilacuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuenta = registroAux == null ? ""
            : registroAux.getCampos().get("CODIGO").toString();

        reasignarOrigen();

        generarTotales();
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */

    @Override
    public void abrirFormulario() {

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
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
        reasignarOrigen();
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del
     * registro
     * 
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
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
     * pueden remover valores auxiliares que no se desee o se deban enviar en el
     * registro
     */
    @Override
    public void removerCombos() {

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del
     * registro se usa cuando se desean agregar valores al registro despues de
     * dichas acciones
     */
    public void error(String error) {
        FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                        "Message Content."));
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListacuenta() {
        return listacuenta;
    }

    public void setListacuenta(RegistroDataModelImpl listacuenta) {
        this.listacuenta = listacuenta;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getNombretercero() {
        return nombretercero;
    }

    public void setNombretercero(String nombretercero) {
        this.nombretercero = nombretercero;
    }

    public RegistroDataModelImpl getListadetalleComCnt() {
        return listadetalleComCnt;
    }

    public void setListadetalleComCnt(
        RegistroDataModelImpl listadetalleComCnt) {
        this.listadetalleComCnt = listadetalleComCnt;
    }

    public boolean isBloqueaAbonoinicial() {
        return bloqueaAbonoinicial;
    }

    public void setBloqueaAbonoinicial(boolean bloqueaAbonoinicial) {
        this.bloqueaAbonoinicial = bloqueaAbonoinicial;
    }

    /**
     * @return the fechaCorte
     */
    public Date getFechaCorte() {
        return fechaCorte;
    }

    /**
     * @param fechaCorte
     * the fechaCorte to set
     */
    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public Registro getSubRegistro() {
        return subRegistro;
    }

    public void setSubRegistro(Registro subRegistro) {
        this.subRegistro = subRegistro;
    }

    public String getTotalDeuda() {
        return totalDeuda;
    }

    public void setTotalDeuda(String totalDeuda) {
        this.totalDeuda = totalDeuda;
    }

    public String getSaldoTotal() {
        return saldoTotal;
    }

    public void setSaldoTotal(String saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public String getSaldo1() {
        return saldo1;
    }

    public void setSaldo1(String saldo1) {
        this.saldo1 = saldo1;
    }

    public String getSaldo2() {
        return saldo2;
    }

    public void setSaldo2(String saldo2) {
        this.saldo2 = saldo2;
    }

    public String getSaldo3() {
        return saldo3;
    }

    public void setSaldo3(String saldo3) {
        this.saldo3 = saldo3;
    }

    public String getSaldo4() {
        return saldo4;
    }

    public void setSaldo4(String saldo4) {
        this.saldo4 = saldo4;
    }

    public String getSaldo5() {
        return saldo5;
    }

    public void setSaldo5(String saldo5) {
        this.saldo5 = saldo5;
    }

    public String getSaldo6() {
        return saldo6;
    }

    public void setSaldo6(String saldo6) {
        this.saldo6 = saldo6;
    }

    public String getSaldo7() {
        return saldo7;
    }

    public void setSaldo7(String saldo7) {
        this.saldo7 = saldo7;
    }

    public String getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(String valorDebito) {
        this.valorDebito = valorDebito;
    }

    public String getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(String valorCredito) {
        this.valorCredito = valorCredito;
    }

    public String getDebitoAfectado() {
        return debitoAfectado;
    }

    public void setDebitoAfectado(String debitoAfectado) {
        this.debitoAfectado = debitoAfectado;
    }

    public String getCreditoAfectado() {
        return creditoAfectado;
    }

    public void setCreditoAfectado(String creditoAfectado) {
        this.creditoAfectado = creditoAfectado;
    }

    public String getAbonoInicial() {
        return abonoInicial;
    }

    public void setAbonoInicial(String abonoInicial) {
        this.abonoInicial = abonoInicial;
    }

    public boolean isFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(boolean fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
