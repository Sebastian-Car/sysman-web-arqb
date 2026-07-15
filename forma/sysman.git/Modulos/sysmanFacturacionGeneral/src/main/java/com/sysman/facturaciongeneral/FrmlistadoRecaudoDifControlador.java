
/*-
 * FrmlistadoRecaudoDif.java
 *
 * 1.0
 * 
 * 08/11/2017
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.facturaciongeneral.enums.FrmlistadoRecaudoDifControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmlistadoRecaudoDifControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * @author jcrodriguez, Migracion del formulario
 * FRM_LISTADORECAUDO_DIF a web FrmlistadoRecaudoDifControlador forma
 * frmlistadorecaudodif, reporte access INF_RECAUDO_DIF migrado
 * 001484INFRECAUDODIF, creacion de dss para los combo facturaco
 * inicial y final
 * @version 1.0, 08/11/2017
 */
@ManagedBean
@ViewScoped

public class FrmlistadoRecaudoDifControlador extends BeanBaseModal {
    /**
     * variable cadena que almacena la compania con la que se
     * encuentra actualmente en la session
     */
    private final String compania;
    /**
     * variable que cadena que almacena el codigo de facturacion
     * inicial
     */

    private String facturacionIni;
    /**
     * variable cadena que almacena el codigo de facturacion final
     */

    private String facturacionFin;
    /**
     * variable tipo fecha que almacena la fecha inicial
     */

    private Date txtFechaInicial;
    /**
     * variable tipo fecha que almacena la fecha final
     */

    private Date txtFechaFinal;

    /**
     * variable que almacena el archivo de descarga en formato pdf o
     * excel
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que lista las factura inicial
     */
    private RegistroDataModelImpl listaFacturaIni;
    /**
     * variable que lista las factura final
     */
    private RegistroDataModelImpl listaFacturaFin;
    /**
     * variable cadena que almacena el tipo de cobro ingresado en el
     * formulario modal principal de facturacion general
     */
    private String tipoCobro;
    /**
     * variable cadena que almacena el ano seleccionado del formulario
     * modal principal de facturacion general
     */
    private String anoCobro;
    /**
     * variable cadena que almacena el modulo actual
     */
    private final String modulo;
    /**
     * variable cadena que almacena el nombre del tipo de cobro
     */
    private String nombreTipoCobro;

    /**
     * Crea una nueva instancia de FrmlistadoRecaudoDif
     */
    public FrmlistadoRecaudoDifControlador() {
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
        nombreTipoCobro = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                                        .getValue()),
                                        "")
                        .toString();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_LISTADORECAUDODIF_CONTROLADOR
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

        cargarListaFacturaIni();
        cargarListaFacturaFin();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        txtFechaInicial = txtFechaFinal = new Date();
    }

    /**
     * 
     * Carga la lista listaFacturaInicial
     */
    public void cargarListaFacturaIni() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmlistadoRecaudoDifControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmlistadoRecaudoDifControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(FrmlistadoRecaudoDifControladorEnum.ANOCOBRO.getValue(),
                        anoCobro);
        listaFacturaIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmlistadoRecaudoDifControladorEnum.NUMERO_FACTURA
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaFacturaFinal
     */
    public void cargarListaFacturaFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmlistadoRecaudoDifControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmlistadoRecaudoDifControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(FrmlistadoRecaudoDifControladorEnum.ANOCOBRO.getValue(),
                        anoCobro);
        param.put(FrmlistadoRecaudoDifControladorEnum.NUMEROFACTURA.getValue(),
                        facturacionIni);
        listaFacturaFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmlistadoRecaudoDifControladorEnum.NUMERO_FACTURA
                                        .getValue());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton pdf en la vista
     *
     */
    public void oprimirpdf() {
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton excel en la vista
     */
    public void oprimirexcel() {
        archivoDescarga = null;
        generaInforme(FORMATOS.EXCEL);
    }

    /**
     * metodo que contiene la logica para imprimir un reporte en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generaInforme(FORMATOS formato) {

        try {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(
                                            txtFechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(
                            txtFechaFinal));
            reemplazar.put("facturaIni", facturacionIni);
            reemplazar.put("facturaFin", facturacionFin);

            Reporteador.resuelveConsulta(
                            FrmlistadoRecaudoDifControladorEnum.INFORME001484
                                            .getValue(),
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            parametros.put("PR_TIPOCOBRO", nombreTipoCobro);
            parametros.put("PR_TXTFECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(txtFechaInicial));
            parametros.put("PR_TXTFECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(txtFechaFinal));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_AHORA", new Date());
            parametros.put("PR_FACTURAINI", facturacionIni);
            parametros.put("PR_FACTURAFIN", facturacionFin);
            archivoDescarga = JsfUtil.exportarStreamed(
                            FrmlistadoRecaudoDifControladorEnum.INFORME001484
                                            .getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            FrmlistadoRecaudoDifControladorEnum.INFORME001484
                                            .getValue()));
            Logger.getLogger(FrmlistadoRecaudoDifControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaIni
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturacionIni = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmlistadoRecaudoDifControladorEnum.NUMERO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();
        facturacionFin = null;
        cargarListaFacturaFin();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaFin
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturacionFin = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmlistadoRecaudoDifControladorEnum.NUMERO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * Retorna la variable facturacionIni *
     * 
     * @return facturacionIni
     */
    public String getFacturacionIni() {
        return facturacionIni;
    }

    /**
     * Asigna la variable facturacionIni
     * 
     * @param facturacionIni
     * Variable a asignar en facturacionIni
     */
    public void setFacturacionIni(String facturacionIni) {
        this.facturacionIni = facturacionIni;
    }

    /**
     * Retorna la variable facturacionFin
     * 
     * @return facturacionFin
     */
    public String getFacturacionFin() {
        return facturacionFin;
    }

    /**
     * Asigna la variable facturacionFin
     * 
     * @param facturacionFin
     * Variable a asignar en facturacionFin
     */
    public void setFacturacionFin(String facturacionFin) {
        this.facturacionFin = facturacionFin;
    }

    /**
     * Retorna la variable txtFechaInicial
     * 
     * @return txtFechaInicial
     */
    public Date getTxtFechaInicial() {
        return txtFechaInicial;
    }

    /**
     * Asigna la variable txtFechaInicial
     * 
     * @param txtFechaInicial
     * Variable a asignar en txtFechaInicial
     */
    public void setTxtFechaInicial(Date txtFechaInicial) {
        this.txtFechaInicial = txtFechaInicial;
    }

    /**
     * Retorna la variable txtFechaFinal
     * 
     * @return txtFechaFinal
     */
    public Date getTxtFechaFinal() {
        return txtFechaFinal;
    }

    /**
     * Asigna la variable txtFechaFinal
     * 
     * @param txtFechaFinal
     * Variable a asignar en txtFechaFinal
     */
    public void setTxtFechaFinal(Date txtFechaFinal) {
        this.txtFechaFinal = txtFechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaFacturaIni
     * 
     * @return listaFacturaIni
     */
    public RegistroDataModelImpl getListaFacturaIni() {
        return listaFacturaIni;
    }

    /**
     * Asigna la lista listaFacturaIni
     * 
     * @param listaFacturaIni
     * Variable a asignar en listaFacturaIni
     */
    public void setListaFacturaIni(RegistroDataModelImpl listaFacturaIni) {
        this.listaFacturaIni = listaFacturaIni;
    }

    /**
     * Retorna la lista listaFacturaFin
     * 
     * @return listaFacturaFin
     */
    public RegistroDataModelImpl getListaFacturaFin() {
        return listaFacturaFin;
    }

    /**
     * Asigna la lista listaFacturaFin
     * 
     * @param listaFacturaFin
     * Variable a asignar en listaFacturaFin
     */
    public void setListaFacturaFin(RegistroDataModelImpl listaFacturaFin) {
        this.listaFacturaFin = listaFacturaFin;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getAnoCobro() {
        return anoCobro;
    }

    public void setAnoCobro(String anoCobro) {
        this.anoCobro = anoCobro;
    }

    public String getNombreTipoCobro() {
        return nombreTipoCobro;
    }

    public void setNombreTipoCobro(String nombreTipoCobro) {
        this.nombreTipoCobro = nombreTipoCobro;
    }

}
