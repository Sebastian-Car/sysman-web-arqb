/*-
 * HistoriafacturadosControlador.java
 *
 * 1.0
 *
 * 09/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroRemote;
import com.sysman.serviciospublicos.enums.HistoriafacturadosControladorEnum;
import com.sysman.serviciospublicos.enums.HistoriafacturadosControladorUrlEnum;
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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador para la vista del formulario de historia dentro de la
 * consulta de facturación
 *
 * @version 1.0, 09/11/2016
 * @author vmolano
 *
 * @author eamaya
 * @version 2, 01/06/2017 Proceso de Refactoring y Manejo de EJBs
 *
 * @author lcortes
 * @version 3, 24,25/07/2017. Se agrega el dialogo de consulta para
 * generar consulta entre periodos facturados.
 *
 * @author lcortes
 * @version 4, 26,27/07/2017 .Se agregan los dialogos LHistoria y
 * SeleccionarFormato para implementar la funcionalidad de los botones
 * pdf y ver/imprimir.
 *
 * 01,02,03,08,10,11,14,15, 16/08/2017. Se complementan los metodos
 * aceptar y cancelar de los dialogos LHistoria y Seleccionar formato
 * para que generen cada uno de los respectivos reportes. Se agregan
 * nuevos reemplazos para la consulta 800119HistoriaDeFacturacion.
 *
 */
@ManagedBean
@ViewScoped

public class HistoriafacturadosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que contiene el valor de la cadena "PERIODO".
     */
    private final String periodoC;
    /**
     * Constante que contiene el valor de la cadena "ciclo".
     */
    private final String cicloC;
    /**
     * Constante que contiene el valor de la cadena "codigoRuta"
     */
    private final String codigoRutaC;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Almacena el ciclo en el que se encuenta el suscriptor.
     */
    private String cicloActual;

    /**
     * Almacena el código de ruta del suscriptor actual.
     */
    private String codigoRutaActual;

    /**
     * Almacena el total factuado para el periodo histórico que se
     * esté consultando.
     */
    private double totalFacturado;

    /**
     * Recibe todos los comentarios del suscriptor desde el formulario
     * principal de consulta de facturación
     */
    private String comentarios;

    /**
     * Variable en donde se almacenan los comentarios del periodo
     * actual
     */
    private String comentariosActuales;

    /**
     * Variable que permite hacer visible el dialogo
     * ConsEntrePeriodos.
     */
    private boolean mostrarMens;

    /**
     * Variable que permite hacer visible el dialogo LHistoria.
     */
    private boolean mostrarHistorial;

    /**
     * Variable que permite hacer visible el dialogo
     * SeleccionarFormato.
     */
    private boolean mostrarSelFormato;

    /**
     * Atributo que permite identificar el anio inicial seleccionado.
     */
    private String anioInicial;
    /**
     * Atributo que permite identificar el periodo inicial
     * seleccionado.
     */
    private String periodoInicial;
    /**
     * Atributo que permite identificar el anio final seleccionado.
     */
    private String anioFinal;
    /**
     * Atributo que permite identificar el periodo final seleccionado.
     */
    private String periodoFinal;
    /**
     * Atributo que permite identificar el formato seleccionado.
     */
    private String formato;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de anios.
     */
    private List<Registro> listaAnioInicial;
    /**
     * Lista de periodos correspondientes al anio inicial
     * seleccionado.
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista de anios mayores o iguales al anio inicial seleccionado.
     */
    private List<Registro> listaAnioFinal;
    /**
     * Lista de periodos correspondientes al anio final seleccionado.
     */
    private List<Registro> listaPeriodoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Respresenta la lista de datos que genera el subformulario de
     * los facturados del periodo.
     */
    private List<Registro> listaSubtemphistoria;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCuatroRemote ejbSPCuatro;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de HistoriafacturadosControlador
     */
    public HistoriafacturadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        periodoC = GeneralParameterEnum.PERIODO.getName();
        cicloC = "ciclo";
        codigoRutaC = "codigoRuta";
        varVolver = true;
        try {
            numFormulario = GeneralCodigoFormaEnum.HISTORIAFACTURADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                cicloActual = (String) parametrosEntrada.get(cicloC);
                codigoRutaActual = (String) parametrosEntrada.get(codigoRutaC);
                comentarios = (String) parametrosEntrada.get("comentarios");
            }
            else {
                SessionUtil.redireccionarMenu();
            }

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubtemphistoria();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubtemphistoria = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        tabla = HistoriafacturadosControladorEnum.SP_HISTORIA.getValue();
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(),
                        cicloActual);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRutaActual);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        HistoriafacturadosControladorUrlEnum.URL1313
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        HistoriafacturadosControladorUrlEnum.URL1414
                                        .getValue());

    }

    /**
     * Carga la lista listaSub_temp_historia
     */
    public void cargarListaSubtemphistoria() {
        try {

            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CICLO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CICLO
                                            .getName()));
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGORUTA
                                                            .getName()));
            param.put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.ANO
                                            .getName()));
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            registro.getCampos().get(periodoC));

            listaSubtemphistoria = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            HistoriafacturadosControladorUrlEnum.URL7055
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            tabla));
        }
        catch (SysmanException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnioInicial
     *
     */
    public void cargarListaAnioInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnioInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            HistoriafacturadosControladorUrlEnum.URL4883
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPeriodoInicial
     *
     */
    public void cargarListaPeriodoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            HistoriafacturadosControladorUrlEnum.URL5543
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaAnioFinal
     *
     */
    public void cargarListaAnioFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaAnioFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            HistoriafacturadosControladorUrlEnum.URL6350
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPeriodoFinal
     *
     */
    public void cargarListaPeriodoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
        param.put(GeneralParameterEnum.MES.getName(), periodoInicial);

        try {
            if (anioInicial.equals(anioFinal)) {
                listaPeriodoFinal = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                HistoriafacturadosControladorUrlEnum.URL7004
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            else {
                listaPeriodoFinal = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                HistoriafacturadosControladorUrlEnum.URL5543
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * LHistoria en la vista
     *
     */
    public void aceptarLHistoria() {
        // <CODIGO_DESARROLLADO>
        try {
            mostrarHistorial = mostrarMens = mostrarSelFormato = false;
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            StringBuilder perInicial = new StringBuilder();
            StringBuilder perFinal = new StringBuilder();
            perInicial = perInicial.append(anioInicial).append(periodoInicial);
            perFinal = perFinal.append(anioFinal).append(periodoFinal);

            Map<String, Object> parametro = new TreeMap<>();
            parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametro.put(GeneralParameterEnum.CICLO.getName(), cicloActual);
            parametro.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoRutaActual);
            parametro.put(HistoriafacturadosControladorEnum.PERIODO_INICIAL
                            .getValue(), perInicial.toString());
            parametro.put(HistoriafacturadosControladorEnum.PERIODO_FINAL
                            .getValue(), perFinal.toString());

            String listaPeriodos = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            HistoriafacturadosControladorUrlEnum.URL4884
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametro))
                            .getCampos().get("LISTADO")
                            .toString();

            String[] cadena = listaPeriodos.split(",");
            StringBuilder cadenaAux = new StringBuilder();
            StringBuilder cadenaSum = new StringBuilder();
            StringBuilder cadenaGroup = new StringBuilder();
            for (int i = 0; i <= (listaPeriodos.split(",").length - 1); i++) {
                String alias = cadena[i].trim().substring(1,
                                cadena[i].trim().length() - 1);
                cadenaAux.append("NVL(").append('"').append(cadena[i].trim())
                                .append('"').append(",0)").append(" ")
                                .append('"').append(alias).append('"')
                                .append(",");
                cadenaSum.append("NVL(").append('"').append(cadena[i].trim())
                                .append('"').append(",0) + ");
                cadenaGroup.append("NVL(").append('"').append(cadena[i].trim())
                                .append('"').append(",0), ");

            }
            String columnas = cadenaAux.toString();
            String cadTotal = cadenaSum.toString();
            String grupo = cadenaGroup.toString();
            columnas = columnas.substring(0, columnas.length() - 1);
            cadTotal = cadTotal.substring(0, cadTotal.length() - 2);
            grupo = grupo.substring(0, grupo.length() - 2);

            // Reemplazos valores consulta informe
            reemplazar.put("total", cadTotal);
            reemplazar.put("columnas", columnas);
            reemplazar.put("groupBy", grupo);
            reemplazar.put(cicloC, cicloActual);
            reemplazar.put(codigoRutaC, codigoRutaActual);
            reemplazar.put("periodoInicial", perInicial.toString());
            reemplazar.put("periodoFinal", perFinal.toString());
            reemplazar.put("listaPeriodos", listaPeriodos);
            String strSql = Reporteador.resuelveConsulta(
                            "800119HistoriaDeFacturacion",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Historia Facturacion.xls");
            workbook.close();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * ConsEntrePeriodos en la vista
     *
     */
    public void aceptarConsEntrePeriodos() {
        // <CODIGO_DESARROLLADO>
        mostrarMens = mostrarSelFormato = false;
        mostrarHistorial = true;

        anioInicial = registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString();
        periodoInicial = registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString();
        anioFinal = String.valueOf(SysmanFunciones.ano(new Date()));
        periodoFinal = registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString();
        cargarListaAnioInicial();
        cargarListaPeriodoInicial();
        cargarListaAnioFinal();
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * ConsEntrePeriodos en la vista
     *
     */
    public void cancelarConsEntrePeriodos() {
        // <CODIGO_DESARROLLADO>
        mostrarMens = mostrarHistorial = mostrarSelFormato = false;
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_NOMBRE_USUARIO",
                            registro.getCampos().get("NOMBRES"));
            parametros.put("PR_USO", registro.getCampos().get("USO"));
            parametros.put("PR_ESTRATO",
                            registro.getCampos().get("ESTRATO").toString());
            parametros.put("PR_LECTURA",
                            registro.getCampos().get("LECTURA").toString());
            parametros.put("PR_BANCOPERPROCESO",
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get("BANCOPERPROCESO"),
                                            "").toString());
            parametros.put("PR_FECHAPAGOPERPROCESO", SysmanFunciones
                            .convertirAFechaCadena((Date) registro.getCampos()
                                            .get("FECHAPAGOPERPROCESO")));
            parametros.put("PR_PAQUETEPAGOPERPROCESO", SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get("PAQUETEPAGOPERPROCESO"), "")
                            .toString());
            parametros.put("PR_CONSUMO",
                            registro.getCampos().get("CONSUMO").toString());
            parametros.put("PR_TOTFACTURAPERACTUAL",
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("TOTFACTURAPERACTUAL"),
                                            "").toString().trim());

            reemplazar.put(cicloC, cicloActual);
            reemplazar.put("codRuta", codigoRutaActual);
            reemplazar.put("anio", registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));
            reemplazar.put("periodo", registro.getCampos()
                            .get(GeneralParameterEnum.PERIODO.getName()));

            Reporteador.resuelveConsulta("001453HistoriaUsuario",
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001453HistoriaUsuario", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * SeleccionarFormato en la vista
     *
     */
    public void aceptarSeleccionarFormato() {
        mostrarSelFormato = mostrarHistorial = mostrarMens = false;
        archivoDescarga = null;
        if ((formato != null)
            || !SysmanFunciones.validarVariableVacio(formato)) {
            try {
                HashMap<String, Object> reemplazar = new HashMap<>();
                Map<String, Object> parametros = new HashMap<>();

                // Parametros Reporte
                parametros.put("PR_SOLO_DATOS",
                                "1".equals(formato) ? false : true);
                parametros.put("PR_NOMBREGERENTE",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE GERENTE",
                                                SessionUtil.getModulo(),
                                                new Date(), true));
                parametros.put("PR_RUTAFIRMAGERENTE", SessionUtil
                                .getCompaniaIngreso().getFirmaFactura());
                parametros.put("PR_ESHISTORICA", true);

                // Reemplazos consulta
                reemplazar.put("marca", "2");
                reemplazar.put(cicloC, cicloActual);
                reemplazar.put(codigoRutaC, codigoRutaActual);
                reemplazar.put("anio", registro.getCampos()
                                .get(GeneralParameterEnum.ANO.getName()));
                reemplazar.put("periodo", registro.getCampos()
                                .get(GeneralParameterEnum.PERIODO.getName()));
                reemplazar.put("deudaMayorA", 0);

                Reporteador.resuelveConsulta("001281FacturaYOP002Historico",
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001281FacturaYOP002", parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.PDF);
            }
            catch (SysmanException | JRException | IOException
                            | SystemException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1884"));
        }
    }

    public void cambiarAnioInicial() {
        periodoInicial = anioFinal = periodoFinal = null;
        listaPeriodoFinal = null;
        cargarListaPeriodoInicial();
        cargarListaAnioFinal();
    }

    public void cambiarAnioFinal() {
        periodoFinal = null;
        cargarListaPeriodoFinal();
    }

    public void cambiarPeriodoInicial() {
        periodoFinal = null;
        cargarListaPeriodoFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton btHistoricosFactura en la
     * vista
     */
    public void oprimirbtHistoricosFactura() {
        // <CODIGO_DESARROLLADO>
        mostrarSelFormato = true;
        mostrarMens = mostrarHistorial = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton ABRIR en la vista
     */
    public void oprimirABRIR() {
        // <CODIGO_DESARROLLADO>
        mostrarMens = true;
        mostrarHistorial = mostrarSelFormato = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     *
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        accion = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Sub_temp_historia
     *
     */
    public void agregarRegistroSubSubtemphistoria() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo de edicion del formulario Sub_temp_historia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubtemphistoria(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo de eliminacion del formulario Sub_temp_historia
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubtemphistoria(Registro reg) {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Sub_temp_historia
     *
     */
    public void cancelarEdicionSubtemphistoria() {
        cargarListaSubtemphistoria();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        try {
            comentariosActuales = ejbSPCuatro.obtenerComentariosPeriodo(
                            compania, comentarios,
                            Integer.parseInt(registro.getCampos().get(
                                            GeneralParameterEnum.ANO.getName())
                                            .toString()),
                            registro.getCampos().get(periodoC).toString());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return booleano
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualización
     * del registro
     *
     * @return booleano
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
     * @return booleano
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
     * @return booleano
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
     * @return booleano
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto mostrarMens
     *
     * @return mostrarMens
     */
    public boolean isMostrarMens() {
        return mostrarMens;
    }

    /**
     * Asigna el objeto mostrarMens
     *
     * @param mostrarMens
     * Variable a asignar en mostrarMens
     */
    public void setMostrarMens(boolean mostrarMens) {
        this.mostrarMens = mostrarMens;
    }

    /**
     * Retorna el objeto mostrarHistorial
     *
     * @return mostrarHistorial
     */
    public boolean isMostrarHistorial() {
        return mostrarHistorial;
    }

    /**
     * Asigna el objeto mostrarHistorial
     *
     * @param mostrarHistorial
     * Variable a asignar en mostrarHistorial
     */
    public void setMostrarHistorial(boolean mostrarHistorial) {
        this.mostrarHistorial = mostrarHistorial;
    }

    /**
     * Retorna el objeto mostrarSelFormato
     *
     * @return mostrarSelFormato
     */
    public boolean isMostrarSelFormato() {
        return mostrarSelFormato;
    }

    /**
     * Asigna el objeto mostrarSelFormato
     *
     * @param mostrarSelFormato
     * Variable a asignar en mostrarSelFormato
     */
    public void setMostrarSelFormato(boolean mostrarSelFormato) {
        this.mostrarSelFormato = mostrarSelFormato;
    }

    /**
     * Retorna la variable anioInicial
     *
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     *
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anioFinal
     *
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     *
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Asigna la variable formato
     *
     * @param formato
     * Variable a asignar en formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     *
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    public boolean isVarVolver() {
        return varVolver;
    }

    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnioInicial
     *
     * @return listaAnioInicial
     */
    public List<Registro> getListaAnioInicial() {
        return listaAnioInicial;
    }

    /**
     * Asigna la lista listaAnioInicial
     *
     * @param listaAnioInicial
     * Variable a asignar en listaAnioInicial
     */
    public void setListaAnioInicial(List<Registro> listaAnioInicial) {
        this.listaAnioInicial = listaAnioInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnioFinal
     *
     * @return listaAnioFinal
     */
    public List<Registro> getListaAnioFinal() {
        return listaAnioFinal;
    }

    /**
     * Asigna la lista listaAnioFinal
     *
     * @param listaAnioFinal
     * Variable a asignar en listaAnioFinal
     */
    public void setListaAnioFinal(List<Registro> listaAnioFinal) {
        this.listaAnioFinal = listaAnioFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSub_temp_historia
     *
     * @return listaSub_temp_historia
     */
    public List<Registro> getListaSubtemphistoria() {
        return listaSubtemphistoria;
    }

    /**
     * Asigna la lista listaSub_temp_historia
     *
     * @param listaSub_temp_historia
     * Variable a asignar en listaSub_temp_historia
     */
    public void setListaSubtemphistoria(
        List<Registro> listaSubtemphistoria) {
        this.listaSubtemphistoria = listaSubtemphistoria;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>

    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     *
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    public double getTotalFacturado() {
        return totalFacturado;
    }

    public void setTotalFacturado(double totalFacturado) {
        this.totalFacturado = totalFacturado;
    }

    public String getComentariosActuales() {
        return comentariosActuales;
    }

    public void setComentariosActuales(String comentariosActuales) {
        this.comentariosActuales = comentariosActuales;
    }

    /**
     * Asigna el objeto registroSub
     *
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
