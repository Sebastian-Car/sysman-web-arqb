/*-
 * FrmImprimirFactLote.java
 *
 * 1.0
 * 
 * 18/08/2020
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
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmImprimirFactLoteEnum;
import com.sysman.facturaciongeneral.enums.FrmImprimirFactLoteUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite imprimir los reportes de facturacion por
 * lotes
 *
 * @version 1.0, 18/08/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmImprimirFactLote extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el anio seleccionado en el combo de anios
     */
    private String anio;
    /**
     * Variable que alamacena el codigo de factura inicial
     * seleccionado
     */
    private String facturaInicial;
    /**
     * Variable que alamacena el codigo del tercero inicial
     * seleccionado
     */
    private String terceroInicial;
    /**
     * Variable que alamacena el codigo del tercero final seleccionado
     */
    private String terceroFinal;
    /**
     * Variable que alamacena el codigo de factura final seleccionado
     */
    private String facturaFinal;

    /**
     * variabale que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * variabale que almacena la fecha inicial
     */
    private Date fechaFinal;

    /**
     * Variable que almacena el tipo de cobro seleccionado antes de
     * ingresar al modulo
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
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las facturas iniciales
     */
    private RegistroDataModelImpl listaFacturaInicial;
    /**
     * Lista que carga los terceros iniciales
     */
    private RegistroDataModelImpl listaTerceroInicial;
    /**
     * Lista que carga los terceros finales
     */
    private RegistroDataModelImpl listaTerceroFinal;
    /**
     * Lista que carga las facturas finales
     */
    private RegistroDataModelImpl listaFacturaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmImprimirFactLote
     */
    public FrmImprimirFactLote() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = (String) SessionUtil.getSessionVar("tipoCobro");
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        try {
            // 2180
            numFormulario = GeneralCodigoFormaEnum.FRM_IMPRIMIR_FACT_LOTE
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
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFacturaInicial();
        cargarListaTerceroInicial();
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
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmImprimirFactLoteUrlEnum.URL5457
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaFacturaInicial
     *
     */
    public void cargarListaFacturaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimirFactLoteUrlEnum.URL5904
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOFACTURA", tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmImprimirFactLoteEnum.NUMERO_FACTURA.getValue());
    }

    /**
     * 
     * Carga la lista listaTerceroInicial
     *
     */
    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimirFactLoteUrlEnum.URL6768
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaTerceroFinal
     *
     */
    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimirFactLoteUrlEnum.URL7218
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINICIAL", terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaFacturaFinal
     *
     */
    public void cargarListaFacturaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImprimirFactLoteUrlEnum.URL7818
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put("TIPOFACTURA", tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("FACTURA", facturaInicial);

        listaFacturaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmImprimirFactLoteEnum.NUMERO_FACTURA.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {

        try {
            String informe = obtenerFormato();
            String codigoEan = "";
            Map<String, Object> reemplazar = new HashMap<>();

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
                param.put("ANO", anio);
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
            reemplazar.put("anio", anio);
            reemplazar.put("tipoFactura", tipoCobro);
            reemplazar.put("facturaInicial", facturaInicial);
            reemplazar.put("facturaFinal", facturaFinal);
            reemplazar.put("compania", compania);
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
            parametros.put("PR_CUENTABANCO1",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "SF BANCO CUENTA 1",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            ""));
            parametros.put("PR_CUENTABANCO2", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SF BANCO CUENTA 2",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            ""));
            parametros.put("PR_CUENTABANCO3", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SF BANCO CUENTA 3",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            ""));
            parametros.put("PR_CUENTABANCO4", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SF BANCO CUENTA 4",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            ""));
            parametros.put("PR_SF_CPTO_PPAL_PRODESARROLLO", SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SF CONCEPTO PRINCIPAL PRODESARROLLO",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            ""));
            parametros.put("PR_SF_MANEJA_CODIGO_BARRAS",
                            "SI".equalsIgnoreCase(SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "SF MANEJA CODIGO DE BARRAS",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            "SI").toString()));
            //inicia implementacion de marca_blanca LJDIAZ (Luis Jacobo Diaz Muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            //fin implementacion marca_blanca
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Permite obtener el nombre del formato con el que se desea
     * generar la factura
     *
     * @return nombre del formato a generar
     */
    private String obtenerFormato() {
        String formato = "";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        params.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22805
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            if ((rs != null)
                && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                                "FORMATO_FACTURA")) {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else {
                formato = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "SF FORMATO FACTURACION",
                                                SessionUtil.getModulo(),
                                                new Date(), false)

                                , "001493INFFACSTD010");
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return formato;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        facturaInicial = "";
        facturaFinal = "";
        cargarListaFacturaInicial();
        cargarListaFacturaFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmImprimirFactLoteEnum.NUMERO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();

        facturaFinal = "";
        cargarListaFacturaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();

        terceroFinal = "";

        cargarListaTerceroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFacturaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        facturaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmImprimirFactLoteEnum.NUMERO_FACTURA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable facturaInicial
     * 
     * @return facturaInicial
     */
    public String getFacturaInicial() {
        return facturaInicial;
    }

    /**
     * Asigna la variable facturaInicial
     * 
     * @param facturaInicial
     * Variable a asignar en facturaInicial
     */
    public void setFacturaInicial(String facturaInicial) {
        this.facturaInicial = facturaInicial;
    }

    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable facturaFinal
     * 
     * @return facturaFinal
     */
    public String getFacturaFinal() {
        return facturaFinal;
    }

    /**
     * Asigna la variable facturaFinal
     * 
     * @param facturaFinal
     * Variable a asignar en facturaFinal
     */
    public void setFacturaFinal(String facturaFinal) {
        this.facturaFinal = facturaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

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

    /**
     * Retorna la lista listaTerceroInicial
     * 
     * @return listaTerceroInicial
     */
    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    /**
     * Asigna la lista listaTerceroInicial
     * 
     * @param listaTerceroInicial
     * Variable a asignar en listaTerceroInicial
     */
    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    /**
     * Retorna la lista listaTerceroFinal
     * 
     * @return listaTerceroFinal
     */
    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    /**
     * Asigna la lista listaTerceroFinal
     * 
     * @param listaTerceroFinal
     * Variable a asignar en listaTerceroFinal
     */
    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    /**
     * Retorna la lista listaFacturaFinal
     * 
     * @return listaFacturaFinal
     */
    public RegistroDataModelImpl getListaFacturaFinal() {
        return listaFacturaFinal;
    }

    /**
     * Asigna la lista listaFacturaFinal
     * 
     * @param listaFacturaFinal
     * Variable a asignar en listaFacturaFinal
     */
    public void setListaFacturaFinal(RegistroDataModelImpl listaFacturaFinal) {
        this.listaFacturaFinal = listaFacturaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
