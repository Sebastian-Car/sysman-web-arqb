/*-
 * FrmDiferirFacturaControlador.java
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
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosRemote;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmDiferirFacturaControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmDiferirFacturaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * @author jcrodriguez ,Migracion, creacion de controlador y forma,
 * creacion de dss para los combos y migracion de reporte
 * INF_FAC_STD02
 * @version 1.0, 09/11/2017
 */
@ManagedBean
@ViewScoped
public class FrmDiferirFacturaControlador extends BeanBaseModal {

    /**
     * variable cadena que almacena la compania de la sesion
     */
    private final String compania;
    /**
     * variable que almacena el estado del combo chek diferida
     */

    private boolean inDiferida;
    /**
     * variable que almacena el numero de cuotas
     */

    private String cuotas;
    /**
     * variable que almacena el codugo de ruta
     */

    private String codigoRuta;

    /**
     * variable cadena que almacen a el numero de factura
     */

    private String noFactura;
    /**
     * variable cadena que almacena la tasa
     */

    private String tasa;
    /**
     * variable que almacena el efectivo
     */

    private String efectivo;
    /**
     * varible que almacena el valor credito
     */

    private String credito;
    /**
     * variable que almacena el valor de la cuota
     */

    private String vlrCuota;
    /**
     * variable cadena que almacena el tipo de abono
     */

    private String tipoAbono;
    /**
     * variable cadena que almacena el numero del abono
     */
    private String nroAbono;
    /**
     * variable cadena que almacena el valor total
     */

    private String vlrTotal;
    /**
     * variable que almacena el abono relacionado
     */

    private String abonoRelacionado;
    /**
     * variable que almacena la lista de tipo de facturas
     */

    private RegistroDataModelImpl listaTIPOFACTURA;
    /**
     * variable que almacena la lista de numero de facturas
     */

    private RegistroDataModelImpl listaNOFACTURA;
    /**
     * variable de que almacena el valor de la variable de session
     * tipo de cobro
     */

    private String tipoCobro;
    /**
     * variable que almacena los reportes en formato pdf y excel para
     * su posterior descarga
     */
    private String anoCobro;

    private StreamedContent archivoDescarga;

    /**
     * variable que almacena el tipo de factura
     */
    private String tipoFacturaR;
    /**
     * variable cadena que almacena el ano
     */
    private DiferirFacturaReporteador diferirFacturaReporteador;

    private String ano;
    /**
     * variable que almacen el nombre de la factura
     */
    private String nombreFactura;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbFacturacionGeneralDosRemote ejbFacturacionGeneralDos;

    /**
     * Crea una nueva instancia de FrmDiferirFacturaControlador
     */
    public FrmDiferirFacturaControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.TIPOCOBRO
                                                        .getValue()),
                                        "")
                        .toString();
        anoCobro = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.ANIO
                                                        .getValue()),
                                        "")
                        .toString();
        ano = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.ANIO
                                                        .getValue()),
                                        "9999")
                        .toString();

        nombreFactura = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                        .getValue());
        try {

            numFormulario = GeneralCodigoFormaEnum.FRM_DIFERIRFACTURA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
        cargarListaTIPOFACTURA();
        cargarListaNOFACTURA();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
        diferirFacturaReporteador = new DiferirFacturaReporteador(
                        ejbSysmanUtil);
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * 
     * Carga la lista listaTIPOFACTURA
     */
    public void cargarListaTIPOFACTURA() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDiferirFacturaControladorUrlEnum.URL6325
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmDiferirFacturaControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(FrmDiferirFacturaControladorEnum.ANOCOBRO.getValue(),
                        anoCobro);

        listaTIPOFACTURA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaNOFACTURA
     */
    public void cargarListaNOFACTURA() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDiferirFacturaControladorUrlEnum.URL6394
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmDiferirFacturaControladorEnum.TIPOFACTURA.getValue(),
                        tipoCobro);
        param.put(FrmDiferirFacturaControladorEnum.ANOCOBRO.getValue(),
                        anoCobro);
        listaNOFACTURA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmDiferirFacturaControladorEnum.NUMERO_FACTURA
                                        .getValue());

    }

    private Registro generalFactura() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDiferirFacturaControladorUrlEnum.URL6332
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmDiferirFacturaControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoCobro);

        try {
            return RegistroConverter.toRegistro(
                            requestManager.get(urlBean.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * metodo migrado de access FtoFactura()
     * 
     * @return
     */
    public String ftoFactura() {
        Registro reg = generalFactura();

        return SysmanFunciones
                        .nvl(reg.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.FORMATO_FACTURA
                                                        .getValue()),
                                        getParametro("SF FORMATO FACTURACION",
                                                        true))
                        .toString();
    }

    public void cambiarEfectivo() {
        credito = String.valueOf(Double.parseDouble(vlrTotal)
            - Double.parseDouble(efectivo));
    }

    /**
     * Metodo que contiene la logia para descargar un reporte en
     * formato pdf y excel
     * 
     * @param formato
     */

    private void generarReporte(FORMATOS formato) {

        String codigoEan = "";
        String reporte = ftoFactura();
        if (idioma.getString("TB_TB3776").equals(reporte)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3775"));
            return;
        }

        try {
            Map<String, Object> parametros = new TreeMap<>();
            Map<String, Object> reemplazar = new TreeMap<>();

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
            
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());

            reemplazar.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            if (ejbSysmanUtil.consultarParametro(compania,
                            "SF CODIGO EAN POR CADA TIPO DE COBRO",
                            SessionUtil.getModulo(), new Date(), false)
                            .equals("NO")) {

                codigoEan = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "SF CODIGO EAN",
                                                SessionUtil.getModulo(),
                                                new Date(), false), "")
                                .toString();
            }
            else {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put("ANO", ano);
                param.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);
                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FacturacionconceptosControladorUrlEnum.URL1717
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                codigoEan = rs.getCampos().get("CODIGOEAN").toString();
            }

            reemplazar.put("codigoEan", codigoEan);

            reemplazar.put("tipoFactura", tipoFacturaR);

            reemplazar.put("facturaInicial", noFactura);

            reemplazar.put("facturaFinal", noFactura);

            reemplazar.put("ano", ano);

            reemplazar.put("modulo", SessionUtil.getModulo());

            archivoDescarga = diferirFacturaReporteador.generarInforme(reporte,
                            generalFactura(), parametros, reemplazar, formato);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdVerificar en la vista
     */
    public void oprimirCmdVerificar() {
        // heredado del bean base

        try {
            String verificar = ejbFacturacionGeneralDos.verificarFactura(
                            compania,
                            tipoFacturaR,
                            new Long(noFactura),
                            new BigDecimal(tasa),
                            Integer.parseInt(cuotas),
                            new BigDecimal(efectivo),
                            tipoCobro,
                            Integer.parseInt(ano),
                            SessionUtil.getUser().getCodigo());

            String[] vector = verificar.split(SysmanConstantes.SEPARADOR_COL);

            if (validarNumero(vector[0])) {
                vlrCuota = vector[0];
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3854")
                                .replace("s$valor$s", vector[0]));

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1323"));
            }

            if (vector.length > 1 || !validarNumero(vector[0])) {
                StringBuilder concatenar = new StringBuilder();
                for (int i = 0; i < vector.length; i++) {
                    concatenar.append(vector[i]);
                    concatenar.append("\n");
                }

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3855")
                                .replace("s$mensaje$s", concatenar.toString()));

            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validarNumero(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdCancelar en la vista
     *
     */
    public void oprimirCmdCancelar() {
        // heredado del bean base
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdFactura en la vista
     *
     */
    public void oprimirPdf() {
        // heredado del bean base
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdFactura en la vista
     *
     */
    public void oprimirExcel() {
        // heredado del bean base
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCODIGORUTA
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCODIGORUTA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.CODIGORUTA
                                                        .getValue()),
                                        "")
                        .toString();
    }

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
        tipoFacturaR = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.TIPO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();

        noFactura = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.NUMERO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();

        vlrTotal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.VALOR_TOTAL
                                                        .getValue()),
                                        "")
                        .toString();

        abonoRelacionado = String.valueOf(SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmDiferirFacturaControladorEnum.TIPO_ABONO
                                                        .getValue()),
                                        " ")
                        .toString()
            + " - " +
            SysmanFunciones.nvl(registroAux.getCampos()
                            .get(FrmDiferirFacturaControladorEnum.NRO_ABONO
                                            .getValue()),
                            "0").toString());

        inDiferida = Boolean.parseBoolean(SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.DIFERIDA
                                                        .getValue()),
                                        "0")
                        .toString());
        tasa = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.TASA_INTERES
                                                        .getValue()),
                                        "0")
                        .toString();
        efectivo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.VLR_EFECTIVO
                                                        .getValue()),
                                        "0")
                        .toString();
        credito = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.VLR_CREDITO
                                                        .getValue()),
                                        "0")
                        .toString();
        cuotas = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.CUOTAS_DIFERIDAS
                                                        .getValue()),
                                        "0")
                        .toString();
        vlrCuota = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmDiferirFacturaControladorEnum.VLR_PROM_CUOTA
                                                        .getValue()),
                                        "0")
                        .toString();

    }

    /**
     * Metodos get y set
     */

    /**
     * Retorna la variable inDiferida
     * 
     * @return inDiferida
     */
    public boolean getInDiferida() {
        return inDiferida;
    }

    /**
     * Asigna la variable inDiferida
     * 
     * @param inDiferida
     * Variable a asignar en inDiferida
     */
    public void setInDiferida(boolean inDiferida) {
        this.inDiferida = inDiferida;
    }

    /**
     * Retorna la variable cuotas
     * 
     * @return cuotas
     */
    public String getCuotas() {
        return cuotas;
    }

    /**
     * Asigna la variable cuotas
     * 
     * @param cuotas
     * Variable a asignar en cuotas
     */
    public void setCuotas(String cuotas) {
        this.cuotas = cuotas;
    }

    /**
     * Retorna la variable codigoRuta
     * 
     * @return codigoRuta
     */
    public String getCodigoRuta() {
        return codigoRuta;
    }

    /**
     * Asigna la variable codigoRuta
     * 
     * @param codigoRuta
     * Variable a asignar en codigoRuta
     */
    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    /**
     * Retorna la variable noFactura
     * 
     * @return noFactura
     */
    public String getNoFactura() {
        return noFactura;
    }

    /**
     * Asigna la variable noFactura
     * 
     * @param noFactura
     * Variable a asignar en noFactura
     */
    public void setNoFactura(String noFactura) {
        this.noFactura = noFactura;
    }

    /**
     * Retorna la variable tasa
     * 
     * @return tasa
     */
    public String getTasa() {
        return tasa;
    }

    /**
     * Asigna la variable tasa
     * 
     * @param tasa
     * Variable a asignar en tasa
     */
    public void setTasa(String tasa) {
        this.tasa = tasa;
    }

    /**
     * Retorna la variable efectivo
     * 
     * @return efectivo
     */
    public String getEfectivo() {
        return efectivo;
    }

    /**
     * Asigna la variable efectivo
     * 
     * @param efectivo
     * Variable a asignar en efectivo
     */
    public void setEfectivo(String efectivo) {
        this.efectivo = efectivo;
    }

    /**
     * Retorna la variable credito
     * 
     * @return credito
     */
    public String getCredito() {
        return credito;
    }

    /**
     * Asigna la variable credito
     * 
     * @param credito
     * Variable a asignar en credito
     */
    public void setCredito(String credito) {
        this.credito = credito;
    }

    /**
     * Retorna la variable vlrCuota
     * 
     * @return vlrCuota
     */
    public String getVlrCuota() {
        return vlrCuota;
    }

    /**
     * Asigna la variable vlrCuota
     * 
     * @param vlrCuota
     * Variable a asignar en vlrCuota
     */
    public void setVlrCuota(String vlrCuota) {
        this.vlrCuota = vlrCuota;
    }

    /**
     * Retorna la variable tipoAbono
     * 
     * @return tipoAbono
     */
    public String getTipoAbono() {
        return tipoAbono;
    }

    /**
     * Asigna la variable tipoAbono
     * 
     * @param tipoAbono
     * Variable a asignar en tipoAbono
     */
    public void setTipoAbono(String tipoAbono) {
        this.tipoAbono = tipoAbono;
    }

    /**
     * Retorna la variable nroAbono
     * 
     * @return nroAbono
     */
    public String getNroAbono() {
        return nroAbono;
    }

    /**
     * Asigna la variable nroAbono
     * 
     * @param nroAbono
     * Variable a asignar en nroAbono
     */
    public void setNroAbono(String nroAbono) {
        this.nroAbono = nroAbono;
    }

    /**
     * Retorna la variable vlrTotal
     * 
     * @return vlrTotal
     */
    public String getVlrTotal() {
        return vlrTotal;
    }

    /**
     * Asigna la variable vlrTotal
     * 
     * @param vlrTotal
     * Variable a asignar en vlrTotal
     */
    public void setVlrTotal(String vlrTotal) {
        this.vlrTotal = vlrTotal;
    }

    /**
     * Retorna la variable abonoRelacionado
     * 
     * @return abonoRelacionado
     */
    public String getAbonoRelacionado() {
        return abonoRelacionado;
    }

    /**
     * Asigna la variable abonoRelacionado
     * 
     * @param abonoRelacionado
     * Variable a asignar en abonoRelacionado
     */
    public void setAbonoRelacionado(String abonoRelacionado) {
        this.abonoRelacionado = abonoRelacionado;
    }

    /**
     * Retorna la lista listaTIPOFACTURA
     * 
     * @return listaTIPOFACTURA
     */
    public RegistroDataModelImpl getListaTIPOFACTURA() {
        return listaTIPOFACTURA;
    }

    /**
     * Asigna la lista listaTIPOFACTURA
     * 
     * @param listaTIPOFACTURA
     * Variable a asignar en listaTIPOFACTURA
     */
    public void setListaTIPOFACTURA(RegistroDataModelImpl listaTIPOFACTURA) {
        this.listaTIPOFACTURA = listaTIPOFACTURA;
    }

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

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTipoFacturaR() {
        return tipoFacturaR;
    }

    public void setTipoFacturaR(String tipoFacturaR) {
        this.tipoFacturaR = tipoFacturaR;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getNombreFactura() {
        return nombreFactura;
    }

    public void setNombreFactura(String nombreFactura) {
        this.nombreFactura = nombreFactura;
    }

}
