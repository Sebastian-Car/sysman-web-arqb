/*-
 * FacturarLotesControlador.java
 *
 * 1.0
 * 
 * 10/11/2017
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
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorEnum;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorUrlEnum;
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
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
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
 * Esta clase es el controlador para el formulario "Facturacion en
 * Lotes" en Access "FRM__FACTURARLOTES", el cual es llamado desde
 * Facturación\Procesos\Facturación en Serie
 *
 * 
 * @version 1.0, 10/11/2017 Se realiza el proceso de Migracion,
 * Refactory a las consultas dentro del Controlador e implementación
 * de EJBs
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class FacturarLotesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicio sesion
     */
    private final String usuario;
    /**
     * Constante que almacena el codigo del modulo en el que se esta
     * trabjando
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la contrato inicial que se desea generar
     */
    private String contratoInicial;
    /**
     * Atributo que almacena la contrato final que se desea generar
     */
    private String contratoFinal;

    /**
     * Atributo que almacena el codigo del lugar
     */
    private String lugar;

    /**
     * Atributo que almacena la fecha de facturacion
     */
    private Date fechaFacturacion;

    /**
     * Tipo de cobro que ha sido selecionado al ingresar al modulo de
     * Facturacion General
     */
    private String tipoCobro;
    /**
     * Anio de trabajo que ha sido seleccionado al ingresar al modulo
     * de Facturacion General
     */
    private String anio;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionGeneralCero;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros que se despliegan en el combo de "Contrato
     * Inicial"
     */
    private RegistroDataModelImpl listaContratoInicial;
    /**
     * Listado de registros que se despliegan en el combo de "Contrato
     * Final"
     */
    private RegistroDataModelImpl listaContratoFinal;

    /**
     * Listado de registros de los lugares
     */
    private RegistroDataModelImpl listaLugar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FacturarLotesControlador
     */
    public FacturarLotesControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        modulo = SessionUtil.getModulo();

        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());

        contratoInicial = SysmanConstantes.DEFECTOINICIAL_NUMBER;
        contratoFinal = SysmanConstantes.CONS_DEPENDENCIA;

        lugar = FacturarLotesControladorEnum.TODOS.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURAR_LOTES_CONTROLADOR
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
        cargarListaContratoInicial();
        cargarListaLugar();
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
     * Carga la lista listaContratoInicial
     *
     */
    public void cargarListaContratoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturarLotesControladorUrlEnum.URL4457
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listaContratoFinal
     *
     */
    public void cargarListaContratoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturarLotesControladorUrlEnum.URL5625
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(FacturarLotesControladorEnum.CONTRATOINICIAL.getValue(),
                        contratoInicial);

        listaContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * 
     * Carga la lista listaLugar
     *
     */
    public void cargarListaLugar() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturarLotesControladorUrlEnum.URL5897
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaLugar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /*
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Facturar en la vista
     *
     */
    public void oprimirFacturar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {
            ejbFacturacionGeneralCero.facturarEnSerie(compania,
                            Integer.parseInt(anio), tipoCobro,
                            new BigInteger(contratoInicial),
                            new BigInteger(contratoFinal), lugar,
                            fechaFacturacion,
                            usuario);

            generarReporteFacturacion();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private void generarReporteFacturacion() {

        String codigoEan;

        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(FacturarLotesControladorEnum.CONTRATOINICIAL.getValue(),
                        contratoInicial);
        params.put(FacturarLotesControladorEnum.CONTRATOFINAL.getValue(),
                        contratoFinal);

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturarLotesControladorUrlEnum.URL86963
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            if (rs != null) {

                String informe = obtenerFormato();

                if (ejbSysmanUtil.consultarParametro(compania,
                                "SF CODIGO EAN POR CADA TIPO DE COBRO",
                                SessionUtil.getModulo(), new Date(), false)
                                .equals("NO")) {

                    codigoEan = SysmanFunciones
                                    .nvl(ejbSysmanUtil.consultarParametro(
                                                    compania, "SF CODIGO EAN",
                                                    SessionUtil.getModulo(),
                                                    new Date(), false), "")
                                    .toString();
                }
                else {
                    Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put("ANO", anio);
                    param.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);
                    Registro rsCodigoEan = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FacturarLotesControladorUrlEnum.URL815
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                    codigoEan = rsCodigoEan.getCampos().get("CODIGOEAN")
                                    .toString();
                }

                Map<String, Object> reemplazar = new TreeMap<>();
                reemplazar.put("codigoEan", codigoEan);
                reemplazar.put("compania", compania);
                reemplazar.put("anio", anio);
                reemplazar.put("tipoFactura", tipoCobro);
                reemplazar.put("facturaInicial",
                                rs.getCampos().get("FACTURAINICIAL"));
                reemplazar.put("facturaFinal",
                                rs.getCampos().get("FACTURAFINAL"));

                // PARAMETROS PARA GENERACION DE INFORME
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("PR_NOMBRECOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNombre());
                parametros.put("PR_NITCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());
                parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());

                Reporteador.resuelveConsulta(informe,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

            }

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaContratoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();
        contratoFinal = null;
        lugar = FacturarLotesControladorEnum.TODOS.getValue();
        cargarListaContratoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaContratoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contratoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();
        lugar = FacturarLotesControladorEnum.TODOS.getValue();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaLugar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaLugar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        lugar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

        contratoInicial = SysmanConstantes.DEFECTOINICIAL_NUMBER;
        contratoFinal = SysmanConstantes.CONS_DEPENDENCIA;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
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
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturarLotesControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                            "FORMATO_FACTURA")) {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else {
                formato = obtenerParametro("SF FORMATO FACTURACION",
                                "001493INFFACSTD010");
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return formato;
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        try {
            /*
             * El formato que ha sido migrado a hoy 16/11/2017 es
             * "001493INFFACSTD010"
             */
            String informe = obtenerFormato();
            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("tipoFactura", tipoCobro);
            reemplazar.put("contratoInicial", contratoInicial);
            reemplazar.put("contratoFinal", contratoFinal);
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
            parametros.put("PR_CUENTABANCO1",
                            obtenerParametro("SF BANCO CUENTA 1", ""));
            parametros.put("PR_CUENTABANCO2",
                            obtenerParametro("SF BANCO CUENTA 2", ""));
            parametros.put("PR_CUENTABANCO3",
                            obtenerParametro("SF BANCO CUENTA 3", ""));
            parametros.put("PR_CUENTABANCO4",
                            obtenerParametro("SF BANCO CUENTA 4", ""));
            parametros.put("PR_SF_CPTO_PPAL_PRODESARROLLO", obtenerParametro(
                            "SF CONCEPTO PRINCIPAL PRODESARROLLO", ""));
            parametros.put("PR_SF_MANEJA_CODIGO_BARRAS",
                            "SI".equalsIgnoreCase(obtenerParametro(
                                            "SF MANEJA CODIGO DE BARRAS", "SI"))
                                                ? true
                                                : false);
            //inicia implementacion de marca_blanca LJDIAZ (Luis Jacobo Diaz Muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            //fin implementacion marca_blanca

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException
                        | OutOfMemoryError e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable contratoInicial
     * 
     * @return contratoInicial
     */
    public String getContratoInicial() {
        return contratoInicial;
    }

    /**
     * Asigna la variable contratoInicial
     * 
     * @param contratoInicial
     * Variable a asignar en contratoInicial
     */
    public void setContratoInicial(String contratoInicial) {
        this.contratoInicial = contratoInicial;
    }

    /**
     * Retorna la variable contratoFinal
     * 
     * @return contratoFinal
     */
    public String getContratoFinal() {
        return contratoFinal;
    }

    /**
     * Asigna la variable contratoFinal
     * 
     * @param contratoFinal
     * Variable a asignar en contratoFinal
     */
    public void setContratoFinal(String contratoFinal) {
        this.contratoFinal = contratoFinal;
    }

    /**
     * Retorna la variable lugar
     * 
     * @return lugar
     */
    public String getLugar() {
        return lugar;
    }

    /**
     * Asigna la variable lugar
     * 
     * @param lugar
     * Variable a asignar en lugar
     */
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

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
     * Retorna la variable fechaFacturacion
     * 
     * @return fechaFacturacion
     */

    public Date getFechaFacturacion() {
        return fechaFacturacion;
    }

    /**
     * Asigna la variable fechaFacturacion
     * 
     * @param fechaFacturacion
     * Variable a asignar en fechaFacturacion
     */
    public void setFechaFacturacion(Date fechaFacturacion) {
        this.fechaFacturacion = fechaFacturacion;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaContratoInicial
     * 
     * @return listaContratoInicial
     */
    public RegistroDataModelImpl getListaContratoInicial() {
        return listaContratoInicial;
    }

    /**
     * Asigna la lista listaContratoInicial
     * 
     * @param listaContratoInicial
     * Variable a asignar en listaContratoInicial
     */
    public void setListaContratoInicial(
        RegistroDataModelImpl listaContratoInicial) {
        this.listaContratoInicial = listaContratoInicial;
    }

    /**
     * Retorna la lista listaContratoFinal
     * 
     * @return listaContratoFinal
     */
    public RegistroDataModelImpl getListaContratoFinal() {
        return listaContratoFinal;
    }

    /**
     * Asigna la lista listaContratoFinal
     * 
     * @param listaContratoFinal
     * Variable a asignar en listaContratoFinal
     */
    public void setListaContratoFinal(
        RegistroDataModelImpl listaContratoFinal) {
        this.listaContratoFinal = listaContratoFinal;
    }

    /**
     * Retorna la lista listaLugar
     * 
     * @return listaLugar
     */
    public RegistroDataModelImpl getListaLugar() {
        return listaLugar;
    }

    /**
     * Asigna la lista listaLugar
     * 
     * @param listaLugar
     * Variable a asignar en listaLugar
     */
    public void setListaLugar(RegistroDataModelImpl listaLugar) {
        this.listaLugar = listaLugar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
