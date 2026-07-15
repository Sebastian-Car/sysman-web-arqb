/*-
 * LpqrControlador.java
 *
 * 1.0
 * 
 * 13/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LpqrControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario 1236 Listado de PQR el cual se encarga
 * de generar varios reportes
 * 
 * @version 1.0, 13/12/2016
 * @author eamaya
 * @modified jguerrero
 * @version 2. 09/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class LpqrControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el valor del problema inicial
     */
    private String problemaInicial;
    /**
     * Atributo que almacena el valor del problema final
     */
    private String problemaFinal;
    /**
     * Atributo que almacena el valor de la clase de problema inicial
     */
    private String claseProblemaInicial;
    /**
     * Atributo que almacena el valor de la clase de problema final
     */
    private String claseProblemaFinal;
    /**
     * Atributo que almacena el valor del codigo inicial
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el valor del codigo final
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el valor del ciclo
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor de la clase de documento
     */
    private String claseDoc;
    /**
     * Atributo que almacena el valor del tipo de reporte
     */
    private String tipoReporte;
    /**
     * Atributo que almacena el codigo de la dependencia
     */
    private String dependencia;
    /**
     * Atributo que almacena el valor de fecha inicial
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena el valor de fecha final
     */
    private Date fechaFinal;
    /**
     * Atributo que almacena el estado de visibilidad del combo
     * dependencia
     */
    private boolean visibleDependencia;
    /**
     * Atributo que almacena el estado de visibilidad del combo
     * usuario
     */
    private boolean visibleUsuario;
    /**
     * Atributo que almacena el nombre de usuario
     */
    private String usuario;
    /**
     * Atributo que almacena el valor de presentacion
     */
    private String presentacion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista de clases de problema
     */
    private List<Registro> listaClaseProblemaInicial;
    /**
     * Lista de clases de problema
     */
    private List<Registro> listaClaseProblemaFinal;
    /**
     * Lista de ciclos
     */
    private List<Registro> listaciclo;
    /**
     * Lista de dependencias disponibles
     */
    private List<Registro> listaDependencia;
    /**
     * Lista de usuarios disponibles
     */
    private List<Registro> listaUSUARIO;

    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista de problema inicial disponible
     */
    private RegistroDataModelImpl listaProblemaInicial;
    /**
     * Lista de problema final disponible
     */
    private RegistroDataModelImpl listaProblemaFinal;
    /**
     * Lista de codigo final disponible
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Lista de codigo inicial disponible
     */
    private RegistroDataModelImpl listaCodigoInicial;

    private static final String CLASEPROBLEMACONS = "CLASEPROBLEMA";
    private static final String CODIGORUTACONS = GeneralParameterEnum.CODIGORUTA
                    .getName();
    private static final String CODIGOCONS = GeneralParameterEnum.CODIGO
                    .getName();

    private static final String PQRORDENTRABAJOCONS = "PQR y Ordenes de Trabajo";
    private static final String RESUMENESTADISTICAS = "Resumen Estadisticas PQR";
    private static final String TIPOLETRACONS = "SansSerif";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LpqrControlador
     */
    public LpqrControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.LPQR_CONTROLADOR.getCodigo();
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
        cargarListaClaseProblemaInicial();
        cargarListaClaseProblemaFinal();
        cargarListaciclo();
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

        fechaInicial = new Date();
        fechaFinal = new Date();

        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaClaseProblemaInicial
     */
    public void cargarListaClaseProblemaInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaClaseProblemaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LpqrControladorUrlEnum.URL11793
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 314001
    }

    /**
     * Carga la lista listaClaseProblemaFinal
     */
    public void cargarListaClaseProblemaFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CLASEPROBLEMACONS, claseProblemaInicial);

        try {
            listaClaseProblemaFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LpqrControladorUrlEnum.URL11794
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 314002
    }

    /**
     * Carga la lista listaciclo
     */
    public void cargarListaciclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaciclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LpqrControladorUrlEnum.URL9938
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 214029
    }

    /**
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LpqrControladorUrlEnum.URL10672
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGORUTACONS);
        // 366014
    }

    /**
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LpqrControladorUrlEnum.URL11792
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put("CODIGOINI", codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGORUTACONS);

        // 366016 CICLO CODIGOINI
    }

    /**
     * Carga la lista listaDependencia
     */
    public void cargarListaDependencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LpqrControladorUrlEnum.URL12155
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 52001
    }

    /**
     * Carga la lista listaUSUARIO
     */

    public void cargarListaUSUARIO() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ESTADO.getName(), "A");
        param.put("TIPOCUENTA", "U");

        try {
            listaUSUARIO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LpqrControladorUrlEnum.URL12767
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 47005 TIPOCUENTA
    }

    /**
     * Carga la lista listaProblemaInicial
     */
    public void cargarListaProblemaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LpqrControladorUrlEnum.URL14283
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CLASEPROBLEMACONS, claseProblemaInicial);

        listaProblemaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGOCONS);

        // 234006 CLASEPROBLEMA CODIGOINICIAL
    }

    /**
     * Carga la lista listaProblemaFinal
     */
    public void cargarListaProblemaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LpqrControladorUrlEnum.URL15647
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CLASEPROBLEMACONS, claseProblemaFinal);

        listaProblemaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGOCONS);

        // 234008 CLASEPROBLEMA CODIGOINICIAL
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton PDF en la vista
     * 
     * @throws ParseException
     */
    public void oprimirPDF() throws ParseException {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método que retorna el informe de opción nula
     * 
     * @param reemplazos
     * @param parametros
     * @param informeBase
     * @param consultaLpqrResCos
     * @param consultaSubPqr
     * @param formatoCalidad
     * @return informe
     */

    private String reporte(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, String informeBase,
        String consultaLpqrResCos, String consultaSubPqr,
        String formatoCalidad) {

        String informe;
        reemplazos.put(informeBase,
                        Reporteador.resuelveConsulta(consultaLpqrResCos,
                                        Integer.parseInt(
                                                        SessionUtil.getModulo()),
                                        reemplazos));

        Reporteador.resuelveConsulta(consultaSubPqr,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);
        Reporteador.resuelveConsulta(consultaLpqrResCos,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);

        if ("SI".equals(formatoCalidad)) {

            informe = "001342lPQRresProblemaCOS";

        }
        else {

            informe = "001344LPQRresProblema";

        }

        return informe;

    }

    /**
     * Método que retorna el informe de opción 1
     * 
     * @param reemplazos
     * @param parametros
     * @param formatoCalidad
     * @return informe
     */

    private String reporteUno(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, String formatoCalidad) {
        String informe;

        if ("SI".equals(formatoCalidad)) {

            Reporteador.resuelveConsulta("001296lPQRCOS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);
            informe = "001296lPQRCOS";
        }
        else {

            Reporteador.resuelveConsulta("001446LPQRDet",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            informe = "001304LPQRDet";

        }

        return informe;
    }

    /**
     * Método que retorna el informe de opción 2
     * 
     * @param reemplazos
     * @param parametros
     * @param informeBase
     * @param consultaLpqrResCos
     * @param consultaSubPqr
     * @param formatoCalidad
     * @param extobservaciones
     * @return informe
     */

    private String reporteDos(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, String informeBase,
        String consultaLpqrResCos, String consultaSubPqr, String formatoCalidad,
        String extobservaciones) {

        reemplazos.put(informeBase,
                        Reporteador.resuelveConsulta(consultaLpqrResCos,
                                        Integer.parseInt(
                                                        SessionUtil.getModulo()),
                                        reemplazos));

        Reporteador.resuelveConsulta(consultaSubPqr,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);
        Reporteador.resuelveConsulta(consultaLpqrResCos,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);

        String informe = "";

        if ("SI".equals(extobservaciones) && "SI".equals(formatoCalidad)) {

            informe = "001306lPQRresCOS";

        }
        else if ("SI".equals(extobservaciones) && "NO".equals(formatoCalidad)) {

            informe = "001320LPQRRes";

        }
        else if ("NO".equals(extobservaciones) && "SI".equals(formatoCalidad)) {

            informe = "001324lPQRresNoExtCOS";
        }

        else if ("NO".equals(extobservaciones) && "NO".equals(formatoCalidad)) {

            informe = "001326LPqrResNoExt";

        }

        return informe;
    }

    /**
     * Método que retorna el informe de opción tres
     * 
     * @param reemplazos
     * @param parametros
     * @param informeBase
     * @param consultaLpqrResCos
     * @param consultaSubPqr
     * @param formatoCalidad
     * @return
     */

    private String reporteTres(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, String informeBase,
        String consultaLpqrResCos, String consultaSubPqr,
        String formatoCalidad) {

        String informe;
        String consultaLpqrResFecha = "001330lPqrResFecha";
        parametros.put("PR_DEPENDENCIA", dependencia);

        if ("SI".equals(formatoCalidad)) {

            reemplazos.put(informeBase,
                            Reporteador.resuelveConsulta(consultaLpqrResCos,
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos));

            Reporteador.resuelveConsulta(consultaSubPqr,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);
            Reporteador.resuelveConsulta(consultaLpqrResCos,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            informe = "001327lPQRresFechaCOS";

        }
        else {
            reemplazos.put(informeBase,
                            Reporteador.resuelveConsulta(consultaLpqrResFecha,
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos));

            Reporteador.resuelveConsulta(consultaSubPqr,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);
            Reporteador.resuelveConsulta(consultaLpqrResFecha,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            informe = "001330lPqrResFecha";
        }
        return informe;

    }

    /**
     * Método que retorna el informe de opción cuatro
     * 
     * @param reemplazos
     * @param parametros
     * @param informeBase
     * @param consultaLpqrResCos
     * @param consultaSubPqr
     * @param formatoCalidad
     * @param codigoUsuario
     * @return
     */
    private String reporteCuatro(HashMap<String, Object> reemplazos,
        Map<String, Object> parametros, String informeBase,
        String consultaLpqrResCos, String formatoCalidad,
        String codigoUsuario) {

        String informe = null;
        if (SysmanFunciones.validarVariableVacio(usuario)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3082"));

        }
        else {

            String condicionUsuario = !("TODOS").equals(usuario)
                ? "AND SP_ORDENTRABAJO.OPERADOR = '" + usuario + "'"
                : "";

            reemplazos.put(codigoUsuario, condicionUsuario);

            reemplazos.put(informeBase,
                            Reporteador.resuelveConsulta(consultaLpqrResCos,
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos));

            Reporteador.resuelveConsulta("001336lpqrResFechaT",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            if ("SI".equals(formatoCalidad)) {

                informe = "001333lPQRResFechaTCOS";

            }
            else {

                informe = "001336lpqrResFechaT";

            }
        }
        return informe;

    }

    /**
     * Método utilizado para la generación de reportes de tipo PDF
     * 
     * @param formato
     */

    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;

        try {
            String informe;
            String codigoUsuario = "usuario";
            String informeBase = "informeBase";
            String consultaLpqrResCos = "001306lPQRresCOS";
            String consultaSubPqr = "001307SubPqr";
            String consultaPqrSolucionados = "001341LPQRSolucionados";

            String condicionUsuario = "";

            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String formatoCalidad = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CALIDAD", SessionUtil.getModulo(),
                            new Date(), true);

            String extobservaciones = ejbSysmanUtil.consultarParametro(compania,
                            "EXTENDER OBSERVACIONES LISTADO PQR",
                            SessionUtil.getModulo(),
                            new Date(), true);

            reemplazos.put("compania", compania);
            reemplazos.put("codigoInicial", codigoInicial);
            reemplazos.put("codigoFinal", codigoFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("claseProblemaInicial", claseProblemaInicial);
            reemplazos.put("claseProblemaFinal", claseProblemaFinal);
            reemplazos.put("problemaInicial", problemaInicial);
            reemplazos.put("problemaFinal", problemaFinal);
            generarCondicionInforme(reemplazos);
            reemplazos.put(codigoUsuario, condicionUsuario);
            reemplazos.put(codigoUsuario, condicionUsuario);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_CODIGOINICIAL", codigoInicial);
            parametros.put("PR_CODIGOFINAL", codigoFinal);
            parametros.put("PR_CLASEDOC", claseDoc);

            if (tipoReporte == null) {

                archivoDescarga = JsfUtil.exportarStreamed(reporte(reemplazos,
                                parametros, informeBase, consultaLpqrResCos,
                                consultaSubPqr, formatoCalidad),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

            if ("1".equals(tipoReporte)) {

                archivoDescarga = JsfUtil.exportarStreamed(
                                reporteUno(reemplazos, parametros,
                                                formatoCalidad),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

            if ("2".equals(tipoReporte)) {

                archivoDescarga = JsfUtil.exportarStreamed(
                                reporteDos(reemplazos, parametros, informeBase,
                                                consultaLpqrResCos,
                                                consultaSubPqr, formatoCalidad,
                                                extobservaciones),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

            if ("3".equals(tipoReporte)) {

                archivoDescarga = JsfUtil.exportarStreamed(
                                reporteTres(reemplazos, parametros, informeBase,
                                                consultaLpqrResCos,
                                                consultaSubPqr, formatoCalidad),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

            if ("4".equals(tipoReporte)) {

                archivoDescarga = JsfUtil.exportarStreamed(
                                reporteCuatro(reemplazos, parametros,
                                                informeBase, consultaLpqrResCos,
                                                formatoCalidad,
                                                codigoUsuario),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

            if ("6".equals(tipoReporte)) {

                String condicionSolucion = "AND SP_D_ORDENTRABAJO.FECHASOLUCION IS NULL ";

                reemplazos.put("solucion", condicionSolucion);
                reemplazos.put(informeBase,
                                Reporteador.resuelveConsulta(
                                                consultaPqrSolucionados,
                                                Integer.parseInt(
                                                                SessionUtil.getModulo()),
                                                reemplazos));

                Reporteador.resuelveConsulta(consultaSubPqr,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametros);
                Reporteador.resuelveConsulta(consultaPqrSolucionados,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametros);

                informe = "001337LPQRNoSolucionados";

                archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

            if ("7".equals(tipoReporte)) {

                String condicionSolucion = "AND SP_D_ORDENTRABAJO.FECHASOLUCION IS NOT NULL ";

                reemplazos.put("solucion", condicionSolucion);
                reemplazos.put(informeBase,
                                Reporteador.resuelveConsulta(
                                                consultaPqrSolucionados,
                                                Integer.parseInt(
                                                                SessionUtil.getModulo()),
                                                reemplazos));

                Reporteador.resuelveConsulta(consultaSubPqr,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametros);
                Reporteador.resuelveConsulta(consultaPqrSolucionados,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametros);

                informe = "001341LPQRSolucionados";

                archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }

        }

        catch (JRException | IOException
                        | ParseException | SystemException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + " "
                                + ex.getMessage());
            Logger.getLogger(ImpresionfacturasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método utilizado para la generación de reporte de tipo Excel
     * que cuenta con dos hojas
     * 
     * @param formato
     */

    private void generarExcel() {

        archivoDescarga = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            HashMap<String, Object> reemplazos = new HashMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("codigoInicial", codigoInicial);
            reemplazos.put("codigoFinal", codigoFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("claseProblemaInicial", claseProblemaInicial);
            reemplazos.put("claseProblemaFinal", claseProblemaFinal);
            reemplazos.put("problemaInicial", problemaInicial);
            reemplazos.put("problemaFinal", problemaFinal);

            generarCondicionInforme(reemplazos);

            String consulta = Reporteador.resuelveConsulta("800075PQR",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            String consulta2 = Reporteador.resuelveConsulta(
                            "800076ResumenEstadisticasPQR",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            String[] consultas = { consulta, consulta2 };
            String[] nombreHojas = { PQRORDENTRABAJOCONS,
                                     RESUMENESTADISTICAS };

            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(consultas,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            ReportesBean.FORMATOS.EXCEL97,
                                            nombreHojas).getStream());

            // Hoja uno

            if ((workbook.getSheet(PQRORDENTRABAJOCONS) != null)
                && (workbook.getSheet(RESUMENESTADISTICAS) != null)) {
                generaResumenEstadistPqrExcel(workbook);
                generarHojaPqrOrdenesTrabajoExcel(workbook);

            }
            else {
                generaExcelBlanco(workbook);
            }
            if ((workbook.getSheet(PQRORDENTRABAJOCONS) != null)
                && (workbook.getSheet(RESUMENESTADISTICAS) == null)) {

                generarHojaPqrOrdenesTrabajoExcel(workbook);

            }
            if ((workbook.getSheet(PQRORDENTRABAJOCONS) == null)
                && (workbook.getSheet(RESUMENESTADISTICAS) != null)) {
                generaResumenEstadistPqrExcel(workbook);

            }

            workbook.write(out);

            out.close();

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "LISTADO PQR.xls");

            workbook.close();

        }
        catch (IOException | DRException
                        | ParseException | JRException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ClaseProblemaInicial
     */
    public void cambiarClaseProblemaInicial() {
        // <CODIGO_DESARROLLADO>
        problemaInicial = null;
        cargarListaProblemaInicial();
        claseProblemaFinal = null;
        cargarListaClaseProblemaFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProblemaInicial
     * 
     * 
     */
    public void cambiarProblemaInicial() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ClaseProblemaFinal
     */
    public void cambiarClaseProblemaFinal() {
        // <CODIGO_DESARROLLADO>
        problemaFinal = null;
        cargarListaProblemaFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ciclo
     */
    public void cambiarciclo() {
        // <CODIGO_DESARROLLADO>
        if (("T").equalsIgnoreCase(ciclo)) {
            codigoInicial = "0000000000000000";
            codigoFinal = "9999999999999999";

        }
        else {

            codigoInicial = "";
            codigoFinal = "";

        }
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbTipoReporte
     */
    public void cambiarcmbTipoReporte() {
        // <CODIGO_DESARROLLADO>

        if (!SysmanFunciones.validarVariableVacio(tipoReporte)) {
            switch (tipoReporte) {
            case "3":
                visibleDependencia = true;
                visibleUsuario = false;
                cargarListaDependencia();
                break;
            case "4":
                visibleUsuario = true;
                visibleDependencia = false;
                cargarListaUSUARIO();
                break;

            default:
                visibleDependencia = false;
                visibleUsuario = false;
                break;
            }
        }
        else {
            visibleDependencia = false;
            visibleUsuario = false;

        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProblemaInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProblemaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        problemaInicial = retornoString(registroAux, CODIGOCONS);
        problemaFinal = null;
        cargarListaProblemaFinal();

    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProblemaFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProblemaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        problemaFinal = retornoString(registroAux, CODIGOCONS);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = retornoString(registroAux, CODIGORUTACONS);
        cargarListaCodigoFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = retornoString(registroAux, CODIGORUTACONS);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable problemaInicial
     * 
     * @return problemaInicial
     */
    public String getProblemaInicial() {
        return problemaInicial;
    }

    /**
     * Asigna la variable problemaInicial
     * 
     * @param problemaInicial
     * Variable a asignar en problemaInicial
     */
    public void setProblemaInicial(String problemaInicial) {
        this.problemaInicial = problemaInicial;
    }

    /**
     * Retorna la variable claseProblemaFinal
     * 
     * @return claseProblemaFinal
     */
    public String getProblemaFinal() {
        return problemaFinal;
    }

    /**
     * Asigna la variable claseProblemaFinal
     * 
     * @param claseProblemaFinal
     * Variable a asignar en claseProblemaFinal
     */
    public void setProblemaFinal(String problemaFinal) {
        this.problemaFinal = problemaFinal;
    }

    /**
     * Retorna la variable claseProblemaInicial
     * 
     * @return claseProblemaInicial
     */
    public String getClaseProblemaInicial() {
        return claseProblemaInicial;
    }

    /**
     * Asigna la variable claseProblemaInicial
     * 
     * @param claseProblemaInicial
     * Variable a asignar en claseProblemaInicial
     */

    public void setClaseProblemaInicial(String claseProblemaInicial) {
        this.claseProblemaInicial = claseProblemaInicial;
    }

    /**
     * Retorna la variable usuario
     * 
     * @return usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Asigna la variable usuario
     * 
     * @param usuario
     * Variable a asignar en usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable claseProblemaFinal
     * 
     * @return claseProblemaFinal
     */
    public String getClaseProblemaFinal() {
        return claseProblemaFinal;
    }

    /**
     * Asigna la variable claseProblemaFinal
     * 
     * @param claseProblemaFinal
     * Variable a asignar en claseProblemaFinal
     */

    public void setClaseProblemaFinal(String claseProblemaFinal) {
        this.claseProblemaFinal = claseProblemaFinal;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable tipoReporte
     * 
     * @return tipoReporte
     */
    public String getTipoReporte() {
        return tipoReporte;
    }

    /**
     * Asigna la variable tipoReporte
     * 
     * @param tipoReporte
     * Variable a asignar en tipoReporte
     */
    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    /**
     * Retorna la variable claseDoc
     * 
     * @return claseDoc
     */
    public String getClaseDoc() {
        return claseDoc;
    }

    /**
     * Asigna la variable claseDoc
     * 
     * @param claseDoc
     * Variable a asignar en claseDoc
     */
    public void setClaseDoc(String claseDoc) {
        this.claseDoc = claseDoc;
    }

    /**
     * Retorna la variable presentacion
     * 
     * @return presentacion
     */
    public String getPresentacion() {
        return presentacion;
    }

    /**
     * Asigna la variable presentacion
     * 
     * @param presentacion
     * Variable a asignar en presentacion
     */
    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    /**
     * Retorna la variable dependencia
     * 
     * @return dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * Asigna la variable dependencia
     * 
     * @param dependencia
     * Variable a asignar en dependencia
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
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

    /**
     * Retorna la variable visibleDependencia
     * 
     * @return fechaFinal
     */
    public boolean isVisibleDependencia() {
        return visibleDependencia;
    }

    /**
     * Asigna la variable visibleDependencia
     * 
     * @param fechaFinal
     * Variable a asignar en visibleDependencia
     */
    public void setVisibleDependencia(boolean visibleDependencia) {
        this.visibleDependencia = visibleDependencia;
    }

    /**
     * Retorna la variable visibleUsuario
     * 
     * @return fechaFinal
     */
    public boolean isVisibleUsuario() {
        return visibleUsuario;
    }

    /**
     * Asigna la variable visibleUsuario
     * 
     * @param fechaFinal
     * Variable a asignar en visibleUsuario
     */
    public void setVisibleUsuario(boolean visibleUsuario) {
        this.visibleUsuario = visibleUsuario;
    }

    /**
     * Retorna la variable compania
     * 
     * @return compania
     */
    public String getCompania() {
        return compania;
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
    /**
     * Retorna la lista listaClaseProblemaInicial
     * 
     * @return listaClaseProblemaInicial
     */
    public List<Registro> getListaClaseProblemaInicial() {
        return listaClaseProblemaInicial;
    }

    /**
     * Asigna la lista listaClaseProblemaInicial
     * 
     * @param listaClaseProblemaInicial
     * Variable a asignar en listaClaseProblemaInicial
     */
    public void setListaClaseProblemaInicial(
        List<Registro> listaClaseProblemaInicial) {
        this.listaClaseProblemaInicial = listaClaseProblemaInicial;
    }

    /**
     * Retorna la lista listaClaseProblemaFinal
     * 
     * @return listaClaseProblemaFinal
     */
    public List<Registro> getListaClaseProblemaFinal() {
        return listaClaseProblemaFinal;
    }

    /**
     * Asigna la lista listaClaseProblemaFinal
     * 
     * @param listaClaseProblemaFinal
     * Variable a asignar en listaClaseProblemaFinal
     */
    public void setListaClaseProblemaFinal(
        List<Registro> listaClaseProblemaFinal) {
        this.listaClaseProblemaFinal = listaClaseProblemaFinal;
    }

    /**
     * Retorna la lista listaciclo
     * 
     * @return listaciclo
     */
    public List<Registro> getListaciclo() {
        return listaciclo;
    }

    /**
     * Asigna la lista listaciclo
     * 
     * @param listaciclo
     * Variable a asignar en listaciclo
     */
    public void setListaciclo(List<Registro> listaciclo) {
        this.listaciclo = listaciclo;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public List<Registro> getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(List<Registro> listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaUSUARIO
     * 
     * @return listaUSUARIO
     */
    public List<Registro> getListaUSUARIO() {
        return listaUSUARIO;
    }

    /**
     * Asigna la lista listaUSUARIO
     * 
     * @param listaUSUARIO
     * Variable a asignar en listaUSUARIO
     */
    public void setListaUSUARIO(List<Registro> listaUSUARIO) {
        this.listaUSUARIO = listaUSUARIO;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaProblemaInicial
     * 
     * @return listaProblemaInicial
     */
    public RegistroDataModelImpl getListaProblemaInicial() {
        return listaProblemaInicial;
    }

    /**
     * Asigna la lista listaProblemaInicial
     * 
     * @param listaProblemaInicial
     * Variable a asignar en listaProblemaInicial
     */
    public void setListaProblemaInicial(
        RegistroDataModelImpl listaProblemaInicial) {
        this.listaProblemaInicial = listaProblemaInicial;
    }

    /**
     * Retorna la lista listaProblemaFinal
     * 
     * @return listaProblemaFinal
     */
    public RegistroDataModelImpl getListaProblemaFinal() {
        return listaProblemaFinal;
    }

    /**
     * Asigna la lista listaProblemaFinal
     * 
     * @param listaProblemaFinal
     * Variable a asignar en listaProblemaFinal
     */
    public void setListaProblemaFinal(
        RegistroDataModelImpl listaProblemaFinal) {
        this.listaProblemaFinal = listaProblemaFinal;
    }

    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    private String retornoString(Registro registro, String campo) {
        return SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)
            ? "" : registro.getCampos().get(campo).toString();

    }

    private void generarCondicionInforme(HashMap<String, Object> reemplazos) {
        String condicionCiclo = !("T").equalsIgnoreCase(ciclo)
            ? "AND SP_ORDENTRABAJO.CICLO = " + ciclo
            : "";
        String condicionClaseDoc = !("T").equalsIgnoreCase(claseDoc)
            ? "AND SP_ORDENTRABAJO.CLASEDOC = '" + claseDoc + "'"
            : "";
        String condicionPresentacion = !("A").equalsIgnoreCase(presentacion)
            ? "AND SP_ORDENTRABAJO.PRESENTACION = '" + presentacion + "'"
            : "";
        String condicionDependencia = ((dependencia) != null)
            ? "AND SP_ORDENTRABAJO.DEPENDENCIAENV = '" + dependencia + "'"
            : "";

        reemplazos.put("ciclo", condicionCiclo);
        reemplazos.put("claseDoc", condicionClaseDoc);
        reemplazos.put("presentacion", condicionPresentacion);
        reemplazos.put("dependencia", condicionDependencia);

    }

    private void generaResumenEstadistPqrExcel(Workbook workbook) {
        Sheet sheet2 = workbook.getSheet(idioma.getString("TB_TB3206"));

        sheet2.shiftRows(0, sheet2.getLastRowNum(), 3);

        int nColumnas2 = Math.max(sheet2.getRow(3).getLastCellNum(), 0)
            - 1;

        CellReference celdaTitulo4Ini = new CellReference(0, 0);
        String titulo4Ini = celdaTitulo4Ini.formatAsString();
        CellReference celdaTitulo4Fin = new CellReference(0,
                        nColumnas2);
        String titulo4Fin = celdaTitulo4Fin.formatAsString();

        CellRangeAddress region4 = CellRangeAddress
                        .valueOf("" + titulo4Ini + ":" + titulo4Fin);

        CellReference celdaTitulo5Ini = new CellReference(1, 0);
        String titulo5Ini = celdaTitulo5Ini.formatAsString();
        CellReference celdaTitulo5Fin = new CellReference(1,
                        nColumnas2);
        String titulo5Fin = celdaTitulo5Fin.formatAsString();

        CellRangeAddress region5 = CellRangeAddress
                        .valueOf("" + titulo5Ini + ":" + titulo5Fin);

        sheet2.addMergedRegion(region4);
        sheet2.addMergedRegion(region5);

        /* Propiedades letra encabezado Hoja dos */
        Font font2 = workbook.createFont();
        font2.setFontName(TIPOLETRACONS);
        font2.setBold(true);

        // Tamaño de letra Hoja dos
        font2.setFontHeightInPoints((short) 10);

        /* Estilo encabezado Hoja dos */
        CellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(CellStyle.ALIGN_CENTER);
        style2.setFont(font2);

        /* Titulo 4 Hoja dos */
        Cell cell4 = sheet2.createRow(0).createCell(0);
        cell4.setCellValue(
                        SessionUtil.getCompaniaIngreso().getNombre());
        cell4.setCellStyle(style2);

        /* Titulo 5 Hoja dos */
        Cell cell5 = sheet2.createRow(1).createCell(0);
        cell5.setCellValue(idioma.getString("TB_TB3205"));
        cell5.setCellStyle(style2);

    }

    private void generarHojaPqrOrdenesTrabajoExcel(Workbook workbook) {
        try {
            Sheet sheet = workbook.getSheet(PQRORDENTRABAJOCONS);

            sheet.shiftRows(0,
                            sheet.getLastRowNum(),
                            4);

            int nColumnas = Math.max(sheet.getRow(4).getLastCellNum(), 0)
                - 1;

            CellReference celdaTitulo1Ini = new CellReference(0, 0);
            String titulo1Ini = celdaTitulo1Ini.formatAsString();
            CellReference celdaTitulo1Fin = new CellReference(0, nColumnas);
            String titulo1Fin = celdaTitulo1Fin.formatAsString();

            CellRangeAddress region = CellRangeAddress
                            .valueOf("" + titulo1Ini + ":" + titulo1Fin);

            CellReference celdaTitulo2Ini = new CellReference(1, 0);
            String titulo2Ini = celdaTitulo2Ini.formatAsString();
            CellReference celdaTitulo2Fin = new CellReference(1, nColumnas);
            String titulo2Fin = celdaTitulo2Fin.formatAsString();

            CellRangeAddress region2 = CellRangeAddress
                            .valueOf("" + titulo2Ini + ":" + titulo2Fin);

            CellReference celdaTitulo3Ini = new CellReference(2, 0);
            String titulo3Ini = celdaTitulo3Ini.formatAsString();
            CellReference celdaTitulo3Fin = new CellReference(2, nColumnas);
            String titulo3Fin = celdaTitulo3Fin.formatAsString();

            CellRangeAddress region3 = CellRangeAddress
                            .valueOf("" + titulo3Ini + ":" + titulo3Fin);

            sheet.addMergedRegion(region);
            sheet.addMergedRegion(region2);
            sheet.addMergedRegion(region3);

            /* Propiedades letra encabezado Hoja uno */
            Font font = workbook.createFont();
            font.setFontName(TIPOLETRACONS);
            font.setBold(true);

            // Tamaño de letra Hoja uno
            font.setFontHeightInPoints((short) 10);

            /* Estilo encabezado Hoja uno */
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setFont(font);

            /* Titulo 1 Hoja uno */
            Cell cell1 = sheet.createRow(0).createCell(0);
            cell1.setCellValue(
                            SessionUtil.getCompaniaIngreso().getNombre());
            cell1.setCellStyle(style);

            /* Titulo 2 Hoja uno */
            Cell cell2 = sheet.createRow(1).createCell(0);
            cell2.setCellValue(idioma.getString("TB_TB3203"));
            cell2.setCellStyle(style);

            /* Titulo 3 Hoja uno */
            Cell cell3 = sheet.createRow(2).createCell(0);

            cell3.setCellValue("Entre: "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial)
                + " y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            cell3.setCellStyle(style);

        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generaExcelBlanco(Workbook workbook) {

        Sheet sheet2 = workbook.createSheet();

        sheet2.setColumnWidth(0, 10000);

        CellRangeAddress region4 = CellRangeAddress
                        .valueOf("A1:U1");

        sheet2.addMergedRegion(region4);

        /* Propiedades letra encabezado Hoja dos */
        Font font2 = workbook.createFont();
        font2.setFontName(TIPOLETRACONS);
        font2.setBold(true);

        // Tamaño de letra Hoja dos
        font2.setFontHeightInPoints((short) 18);

        /* Estilo encabezado Hoja dos */
        CellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(CellStyle.ALIGN_CENTER);
        style2.setFont(font2);

        /* Titulo 4 Hoja dos */
        Cell cell4 = sheet2.createRow(0).createCell(0);
        cell4.setCellValue(idioma.getString(
                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
        cell4.setCellStyle(style2);
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
