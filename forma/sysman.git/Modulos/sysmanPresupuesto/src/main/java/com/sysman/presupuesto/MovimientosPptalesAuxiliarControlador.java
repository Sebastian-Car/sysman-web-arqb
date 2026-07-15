/*-
 * MovimientosPptalesAuxiliarControlador.java
 *
 * 1.0
 * 
 * 22/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.MovimientosPptalesAuxiliarControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 22/01/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class MovimientosPptalesAuxiliarControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private String ano;
    private int tipoCuenta;
    private boolean ckCentroCosto;
    private boolean ckTercero;
    private boolean ckAuxiliar;
    private boolean ckReferencia;
    private boolean ckFuente;
    private boolean excelEsp;
    private String centroInicial;
    private String centroFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String auxiliarInicial;
    private String auxiliarFinal;
    private String referenciaInicial;
    private String referenciaFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String tipoInicial;
    private String tipoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCentroInicial;
    private RegistroDataModelImpl listaCentroFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaAuxiliarInicial;
    private RegistroDataModelImpl listaAuxiliarFinal;
    private RegistroDataModelImpl listaReferenciaInicial;
    private RegistroDataModelImpl listaReferenciaFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de
     * MovimientosPptalesAuxiliarControlador
     */
    public MovimientosPptalesAuxiliarControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        fechaInicial = fechaFinal = new Date();
        tipoCuenta = 1;
        try {
            numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_PPTALES_AUXILIAR_CONTROLADOR
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

        cargarListaTipoInicial();

        cargarListaCentroInicial();
        cargarListaTerceroInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();

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
        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MovimientosPptalesAuxiliarControladorUrlEnum.URL000
                                                                            .getValue())
                                            .getUrl(),
                            parametros));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCentroInicial
     *
     */
    public void cargarListaCentroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL003
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroFinal
     *
     */
    public void cargarListaCentroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL004
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        listaCentroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTerceroInicial
     *
     */
    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL005
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    /**
     * 
     * Carga la lista listaTerceroFinal
     *
     */
    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL006
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCEROINI", terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL007
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", ano);

        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
    public void cargarListaAuxiliarFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL008
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", ano);
        param.put("CODIGOFINAL", auxiliarInicial);

        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL009
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferenciaFinal
     *
     */
    public void cargarListaReferenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL010
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", ano);
        param.put("REFERENCIAINICIAL", referenciaInicial);

        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteInicial
     *
     */
    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL011
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", ano);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteFinal
     *
     */
    public void cargarListaFuenteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL012
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", ano);
        param.put("FUENTEINICIAL", fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoInicial
     *
     */
    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoFinal
     *
     */
    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosPptalesAuxiliarControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
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
    	if (excelEsp) {
    		generarExcel();
    	}
    	else {
    		archivoDescarga = null;
    		generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    	}
    }
    // </METODOS_BOTONES>
   
    
    
    public void generarExcel () {
    	try {

            HashMap<String, Object> reemplazos = new HashMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("tipoCuenta", tipoCuenta);
            reemplazos.put("fechaInicial", SysmanFunciones
                    .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                    SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("comprobanteInicial", tipoInicial);
            reemplazos.put("comprobanteFinal", tipoFinal);
            reemplazos.put("auxiliarInicial", auxiliarInicial);
            reemplazos.put("auxiliarFinal", auxiliarFinal);
            reemplazos.put("centroInicial", centroInicial);
            reemplazos.put("centroFinal", centroFinal);
            reemplazos.put("fuenteInicial", fuenteInicial);
            reemplazos.put("fuenteFinal", fuenteFinal);
            reemplazos.put("referenciaInicial", referenciaInicial);
            reemplazos.put("referenciaFinal", referenciaFinal);
            reemplazos.put("terceroInicial", terceroInicial);
            reemplazos.put("terceroFinal", terceroFinal);

            reemplazos.put("manAux", ckAuxiliar ? "1" : "0");
            reemplazos.put("manCen", ckCentroCosto ? "1" : "0");
            reemplazos.put("manFue", ckFuente ? "1" : "0");
            reemplazos.put("manRef", ckReferencia ? "1" : "0");
            reemplazos.put("manTer", ckTercero ? "1" : "0");

            String sql = Reporteador.resuelveConsulta(
                            "800398MovPptalconAuxiliares",
                            Integer.parseInt(modulo),
                            reemplazos);
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    // <METODO GENERAR INFORME>
    public void generarInforme(FORMATOS formato) {

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("tipoCuenta", tipoCuenta);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("comprobanteInicial", tipoInicial);
            reemplazar.put("comprobanteFinal", tipoFinal);
            reemplazar.put("auxiliarInicial", auxiliarInicial);
            reemplazar.put("auxiliarFinal", auxiliarFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);

            reemplazar.put("manAux", ckAuxiliar ? "1" : "0");
            reemplazar.put("manCen", ckCentroCosto ? "1" : "0");
            reemplazar.put("manFue", ckFuente ? "1" : "0");
            reemplazar.put("manRef", ckReferencia ? "1" : "0");
            reemplazar.put("manTer", ckTercero ? "1" : "0");

            Reporteador.resuelveConsulta("001985MovPptalconAuxiliares",
                            Integer.parseInt(modulo), reemplazar, parametros);

            // Firmas
            parametros.put("PR_NOMBRE_GERENTE",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE GERENTE", modulo,
                                            new Date(), true));
            parametros.put("PR_CARGO_GERENTE",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO GERENTE", modulo, new Date(),
                                            true));
            parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE ENCARGADO DE TESORERIA",
                                            modulo, new Date(), true));
            parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO ENCARGADO DE TESORERIA",
                                            modulo, new Date(), true));
            // Firmas

            // Parametros diseño reporte
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_FUENTE_VISIBLE", ckFuente ? true : false);
            parametros.put("PR_TERCERO_VISIBLE", ckTercero ? true : false);
            parametros.put("PR_AUXILIAR_VISIBLE", ckAuxiliar ? true : false);
            parametros.put("PR_CENTRO_VISIBLE", ckCentroCosto ? true : false);
            parametros.put("PR_REFERENCIA_VISIBLE",
                            ckReferencia ? true : false);
            parametros.put("PR_SUCURSAL_VISIBLE", false);
            parametros.put("PR_NRO_VISIBLE", false);
            parametros.put("PR_CONTRATO_VISIBLE", false);
            parametros.put("PR_CMPT_VISIBLE", false);
            parametros.put("PR_FECHA_VISIBLE", false);
            parametros.put("PR_CUENTA_VISIBLE", true);
            parametros.put("PR_NOMBRUBRO_VISIBLE", true);
            // titulos
            parametros.put("PR_TIPO", true);
            parametros.put("PR_DEPENDENCIA", true);
            parametros.put("PR_COMPROBANTE", true);
            parametros.put("PR_RUBRO", false);
            // Parametros diseño reporte

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001985MovPptalconAuxiliares",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODO GENERAR INFORME>

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CkCentroCosto
     * 
     * 
     */
    public void cambiarCkCentroCosto() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CkTercero
     * 
     * 
     */
    public void cambiarCkTercero() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CkAuxiliar
     * 
     * 
     */
    public void cambiarCkAuxiliar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CkReferencia
     * 
     * 
     */
    public void cambiarCkReferencia() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CkFuente
     * 
     * 
     */
    public void cambiarCkFuente() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarExcelEsp() {
        //<CODIGO_DESARROLLADO>
       //</CODIGO_DESARROLLADO>
   }
    
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        centroFinal = null;
        cargarListaCentroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
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
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        terceroFinal = null;
        cargarListaTerceroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        auxiliarFinal = null;
        cargarListaAuxiliarFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        referenciaFinal = null;
        cargarListaReferenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        fuenteFinal = null;
        cargarListaFuenteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        tipoFinal = "ZZZ";
        cargarListaTipoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable centroInicial
     * 
     * @return centroInicial
     */
    public String getCentroInicial() {
        return centroInicial;
    }

    /**
     * @return the ckCentroCosto
     */
    public boolean isCkCentroCosto() {
        return ckCentroCosto;
    }

    /**
     * @param ckCentroCosto
     * the ckCentroCosto to set
     */
    public void setCkCentroCosto(boolean ckCentroCosto) {
        this.ckCentroCosto = ckCentroCosto;
    }

    /**
     * @return the ckTercero
     */
    public boolean isCkTercero() {
        return ckTercero;
    }

    /**
     * @param ckTercero
     * the ckTercero to set
     */
    public void setCkTercero(boolean ckTercero) {
        this.ckTercero = ckTercero;
    }

    /**
     * @return the ckAuxiliar
     */
    public boolean isCkAuxiliar() {
        return ckAuxiliar;
    }

    /**
     * @param ckAuxiliar
     * the ckAuxiliar to set
     */
    public void setCkAuxiliar(boolean ckAuxiliar) {
        this.ckAuxiliar = ckAuxiliar;
    }

    /**
     * @return the ckReferencia
     */
    public boolean isCkReferencia() {
        return ckReferencia;
    }

    /**
     * @param ckReferencia
     * the ckReferencia to set
     */
    public void setCkReferencia(boolean ckReferencia) {
        this.ckReferencia = ckReferencia;
    }

    /**
     * @return the ckFuente
     */
    public boolean isCkFuente() {
        return ckFuente;
    }

    /**
     * @param ckFuente
     * the ckFuente to set
     */
    public void setCkFuente(boolean ckFuente) {
        this.ckFuente = ckFuente;
    }

    /**
     * Asigna la variable centroInicial
     * 
     * @param centroInicial
     * Variable a asignar en centroInicial
     */
    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    /**
     * Retorna la variable centroFinal
     * 
     * @return centroFinal
     */
    public String getCentroFinal() {
        return centroFinal;
    }

    /**
     * Asigna la variable centroFinal
     * 
     * @param centroFinal
     * Variable a asignar en centroFinal
     */
    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
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
     * Retorna la variable auxiliarInicial
     * 
     * @return auxiliarInicial
     */
    public String getAuxiliarInicial() {
        return auxiliarInicial;
    }

    /**
     * Asigna la variable auxiliarInicial
     * 
     * @param auxiliarInicial
     * Variable a asignar en auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }

    /**
     * Retorna la variable auxiliarFinal
     * 
     * @return auxiliarFinal
     */
    public String getAuxiliarFinal() {
        return auxiliarFinal;
    }

    /**
     * Asigna la variable auxiliarFinal
     * 
     * @param auxiliarFinal
     * Variable a asignar en auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }

    /**
     * Retorna la variable referenciaInicial
     * 
     * @return referenciaInicial
     */
    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     * 
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     * 
     * @return referenciaFinal
     */
    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     * 
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    /**
     * Retorna la variable fuenteInicial
     * 
     * @return fuenteInicial
     */
    public String getFuenteInicial() {
        return fuenteInicial;
    }

    /**
     * Asigna la variable fuenteInicial
     * 
     * @param fuenteInicial
     * Variable a asignar en fuenteInicial
     */
    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    /**
     * Retorna la variable fuenteFinal
     * 
     * @return fuenteFinal
     */
    public String getFuenteFinal() {
        return fuenteFinal;
    }

    /**
     * Asigna la variable fuenteFinal
     * 
     * @param fuenteFinal
     * Variable a asignar en fuenteFinal
     */
    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    /**
     * Retorna la variable tipoInicial
     * 
     * @return tipoInicial
     */
    public String getTipoInicial() {
        return tipoInicial;
    }

    /**
     * Asigna la variable tipoInicial
     * 
     * @param tipoInicial
     * Variable a asignar en tipoInicial
     */
    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    /**
     * Retorna la variable tipoFinal
     * 
     * @return tipoFinal
     */
    public String getTipoFinal() {
        return tipoFinal;
    }

    /**
     * Asigna la variable tipoFinal
     * 
     * @param tipoFinal
     * Variable a asignar en tipoFinal
     */
    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @return the tipoCuenta
     */
    public int getTipoCuenta() {
        return tipoCuenta;
    }

    /**
     * @param tipoCuenta
     * the tipoCuenta to set
     */
    public void setTipoCuenta(int tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
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
     * Retorna la lista listaCentroInicial
     * 
     * @return listaCentroInicial
     */
    public RegistroDataModelImpl getListaCentroInicial() {
        return listaCentroInicial;
    }

    /**
     * Asigna la lista listaCentroInicial
     * 
     * @param listaCentroInicial
     * Variable a asignar en listaCentroInicial
     */
    public void setListaCentroInicial(
        RegistroDataModelImpl listaCentroInicial) {
        this.listaCentroInicial = listaCentroInicial;
    }

    /**
     * Retorna la lista listaCentroFinal
     * 
     * @return listaCentroFinal
     */
    public RegistroDataModelImpl getListaCentroFinal() {
        return listaCentroFinal;
    }

    /**
     * Asigna la lista listaCentroFinal
     * 
     * @param listaCentroFinal
     * Variable a asignar en listaCentroFinal
     */
    public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) {
        this.listaCentroFinal = listaCentroFinal;
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
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(
        RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }

    /**
     * Retorna la lista listaAuxiliarFinal
     * 
     * @return listaAuxiliarFinal
     */
    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    /**
     * Asigna la lista listaAuxiliarFinal
     * 
     * @param listaAuxiliarFinal
     * Variable a asignar en listaAuxiliarFinal
     */
    public void setListaAuxiliarFinal(
        RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    /**
     * Retorna la lista listaReferenciaInicial
     * 
     * @return listaReferenciaInicial
     */
    public RegistroDataModelImpl getListaReferenciaInicial() {
        return listaReferenciaInicial;
    }

    /**
     * Asigna la lista listaReferenciaInicial
     * 
     * @param listaReferenciaInicial
     * Variable a asignar en listaReferenciaInicial
     */
    public void setListaReferenciaInicial(
        RegistroDataModelImpl listaReferenciaInicial) {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }

    /**
     * Retorna la lista listaReferenciaFinal
     * 
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }

    /**
     * Asigna la lista listaReferenciaFinal
     * 
     * @param listaReferenciaFinal
     * Variable a asignar en listaReferenciaFinal
     */
    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }

    /**
     * Retorna la lista listaFuenteInicial
     * 
     * @return listaFuenteInicial
     */
    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    /**
     * Asigna la lista listaFuenteInicial
     * 
     * @param listaFuenteInicial
     * Variable a asignar en listaFuenteInicial
     */
    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    /**
     * Retorna la lista listaFuenteFinal
     * 
     * @return listaFuenteFinal
     */
    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    /**
     * Asigna la lista listaFuenteFinal
     * 
     * @param listaFuenteFinal
     * Variable a asignar en listaFuenteFinal
     */
    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }

    /**
     * Retorna la lista listaTipoInicial
     * 
     * @return listaTipoInicial
     */
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    /**
     * Asigna la lista listaTipoInicial
     * 
     * @param listaTipoInicial
     * Variable a asignar en listaTipoInicial
     */
    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    /**
     * Retorna la lista listaTipoFinal
     * 
     * @return listaTipoFinal
     */
    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    /**
     * Asigna la lista listaTipoFinal
     * 
     * @param listaTipoFinal
     * Variable a asignar en listaTipoFinal
     */
    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

	public boolean isExcelEsp() {
		return excelEsp;
	}

	public void setExcelEsp(boolean excelEsp) {
		this.excelEsp = excelEsp;
	}
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
