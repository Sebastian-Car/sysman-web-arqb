/*-
 * InversionesfutControlador.java
 *
 * 1.0
 *
 * 28/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.GeneraProcesoSiaControladorUrlEnum;
import com.sysman.chipfut.enums.InversionesfutControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
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

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar los informes de gastos de gestion
 * FUT de un periodo de un anio determinado. Se accede desde la ruta
 * Panel Principal/Entes de Control/Chip-Fut/Informes FUT/Informes
 * FUT.
 *
 * @version 1.0, 28/03/2017
 * @author lcortes
 */
@ManagedBean
@ViewScoped
public class InversionesfutControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que identifica la casilla de verificacion que indica
     * si el informe va en miles de pesos
     */
    private boolean pesos;
    /**
     * Atributo que identifica la casilla de verificacion que indica
     * si se va a generar un informe a partir de un archivo Excel.
     */
    private boolean planoPExcel;

    /**
     * Atributo que identifica la casilla de verificacion que indica
     * si se va a agregar el nombre fuente para informe SICEP en
     * archivo Excel.
     */
    private boolean sicep;
    /**
     * Atributo que identifica la casilla de verificacion que indica
     * si se muestra el listado de meses para el respectivo anio
     * seleccionado.
     */
    private boolean porMes;
    /**
     * Atributo que identifica el trimeste o mes seleccionado para el
     * cual se va a generar el informe.
     */
    private int trimestre;
    /**
     * Atributo que identifica el anio seleccionado para el cual se
     * quiere generar el informe.
     */
    /**
     * Atributo que identifica si el informe genera regsitro de
     * totales
     */
    private boolean totales;

    private int anioTrabajo;
    private String nombreArchivo;
    private String codigoEntidad;
    private String ruta;
    private String hoja;
    private boolean visible;
    private int mesInicial;
    private int mesFinal;
    private int mes;
    private String etiqueta;
    private String titulo;
    private String opcion;
    private String menu;
    private boolean verNombreFuente;
    /**
     * Atributo que guarda el nombre del reporte a generar
     */
    private String nombreReporte;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMes;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InversionesfutControlador
     */
    public InversionesfutControlador() {
        super();
        compania = SessionUtil.getCompania();
        verNombreFuente = true;
        try {
            // 1388
            numFormulario = GeneralCodigoFormaEnum.INVERSIONESFUT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            menu = SessionUtil.getMenuActual();
            totales = false;
            opcion = "";
            if ("99020302".equals(menu)) {
                titulo = "GASTOS DE INVERSION";
                opcion = "I";
                nombreReporte = "800184InformeFUTGastoInversion";
                totales = true;
            }
            else if ("99020303".equals(menu)) {
                titulo = "GASTOS FUNCIONAMIENTO";
                opcion = "F";
                nombreReporte = "800183InformeFUTGastoFuncionamiento";
                totales = true;
            }
            else if ("99020310".equals(menu)) {
                titulo = "EJECUCION DE GASTOS SGR";
                opcion = "1";
                verNombreFuente = false;
                nombreReporte = "800186InformeFUTSGRGastos";
                totales = true;
            }
            else if ("99020311".equals(menu)) {
                titulo = "EJECUCION DE INGRESOS SGR";
                opcion = "0";
                verNombreFuente = false;
                nombreReporte = "800185InformeFUTSGRIngresos";
            }
            else if ("99020312".equals(menu)) {
                titulo = "SALDOS DISPONIBLES";
                opcion = "";
                verNombreFuente = false;
                nombreReporte = "800182InformeFUTSaldosDisponibles";
                totales = true;
            }

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
        cargarListaAnoTrabajo();
        abrirFormulario();
        cargarListaMes();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        anioTrabajo = SysmanFunciones.ano(new Date());

        pesos = true;
        porMes = true;
        visible = false;
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        etiqueta = "Mes:";
        mesInicial = mesFinal = mes = SysmanFunciones.mes(new Date());
        trimestre = 1;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GeneraProcesoSiaControladorUrlEnum.URL3933
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaTrimestre
     *
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InversionesfutControladorUrlEnum.URL4612
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    private String generarEncabezado() throws ParseException {
        String periodo = "";
        String separadorColumnas = "\t";
        switch (trimestre) {
        case 1:
            periodo = "0103";
            break;
        case 2:
            periodo = "0406";
            break;
        case 3:
            periodo = "0709";
            break;
        case 4:
            periodo = "1012";
            break;
        default:
            break;
        }
        return SysmanFunciones.concatenar("S",
                        separadorColumnas,
                        codigoEntidad,
                        separadorColumnas, "1", periodo,
                        separadorColumnas,
                        Integer.toString(anioTrabajo),
                        separadorColumnas,
                        titulo,
                        separadorColumnas,
                        SysmanFunciones.convertirAFechaCadena(
                                        new Date(),
                                        "dd-MM-yyyy"));
    }

    /**
     * permite crear los reportes desde el modelo estandar de
     * consultas
     * 
     * @param formato
     */
    private void generarConsulta(ReportesBean.FORMATOS formato) {
        validarMeses();
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            String parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            "DIGITO REDONDEO DE INFORMES FUT",
                            SessionUtil.getModulo(),
                            new Date(), true);

            parametro = SysmanFunciones.validarVariableVacio(parametro)
                ? "0"
                : parametro;
            reemplazar.put("redondeo", parametro);
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        reemplazar.put("aniotrabajo", anioTrabajo);
        reemplazar.put("mesfinal", mesFinal);
        reemplazar.put("mesinicial", mesInicial);
        reemplazar.put("mesanterior", mesInicial - 1);
        reemplazar.put("tipo", opcion);
        reemplazar.put("enmiles", pesos);

        try {
            archivoDescarga = JsfUtil.reportesFut(nombreReporte, reemplazar,
                            generarEncabezado(), formato,
                            titulo, SessionUtil.getModulo(), totales);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        validarMeses();
        generarConsulta(ReportesBean.FORMATOS.TXT);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdExcel en la vista
     *
     */
    public void oprimircmdExcel() {
        archivoDescarga = null;
        generarConsulta(ReportesBean.FORMATOS.EXCEL);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerificarConfiguracion en
     * la vista
     *
     */
    public void oprimirVerificarConfiguracion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        ByteArrayInputStream salidaNombreExcel = null;
        ByteArrayInputStream salidaNombreExcel2 = null;
        ByteArrayInputStream[] salida = new ByteArrayInputStream[2];
        String[] nombres = new String[2];

        Map<String, Object> reemplazar = new TreeMap<>();

        reemplazar.put("compania", compania);
        reemplazar.put("aniotrabajo", anioTrabajo);

        if ("99020303".equals(menu)) {
            String consulta1 = Reporteador.resuelveConsulta(
                            "800341RUBROS_NO_CONFIGURADOS_FUT_GASTOS_FUNCIONAMIENTO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            String consulta2 = Reporteador.resuelveConsulta(
                            "800342RUBROS_CONFIGURADOS_FUT_GASTOS_FUNCIONAMIENTO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            try {

                salidaNombreExcel = JsfUtil.serializarHojaDatos(consulta1,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
            }
            catch (SysmanException | JRException | IOException | DRException
                            | SQLException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            try {
                salidaNombreExcel2 = JsfUtil.serializarHojaDatos(consulta2,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);

            }
            catch (SysmanException | JRException | IOException | DRException
                            | SQLException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }

            int cantidad = 0;
            if (salidaNombreExcel != null) {
                salida[cantidad] = salidaNombreExcel;
                nombres[cantidad] = "RubronsNoConfiguradosFutGastosFuncionamiento.xlsx";
                cantidad++;
            }

            if (salidaNombreExcel2 != null) {
                salida[cantidad] = salidaNombreExcel2;
                nombres[cantidad] = "RubrosConfiguradoFutGastosFuncionamientoDiferentesAVigenciaActual.xlsx";
                cantidad++;
            }
            try {

                if (cantidad > 0) {
                    archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                    salida,
                                    nombres, "VerificarConfiguracion");

                }

            }
            catch (JRException | IOException | SQLException | DRException e2) {
                logger.error(e2.getMessage(), e2);
                JsfUtil.agregarMensajeError(e2.getMessage());
            }
        }

        else if ("99020312".equals(menu)) {
            String consulta1 = Reporteador.resuelveConsulta(
                            "800375SALDOS_DISPONIBLES_REVISION_CONFIGURACION_CUENTAS_BANCARIAS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            String consulta2 = Reporteador.resuelveConsulta(
                            "800374SALDOS_DISPONIBLES_REVISION_CONFIGURACION_FUENTE_RECURSOS",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            try {

                salidaNombreExcel = JsfUtil.serializarHojaDatos(consulta1,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
            }
            catch (SysmanException | JRException | IOException | DRException
                            | SQLException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            try {
                salidaNombreExcel2 = JsfUtil.serializarHojaDatos(consulta2,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);

            }
            catch (SysmanException | JRException | IOException | DRException
                            | SQLException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }

            int cantidad = 0;
            if (salidaNombreExcel != null) {
                salida[cantidad] = salidaNombreExcel;
                nombres[cantidad] = "SaldosDisponiblesRevisionConfiguracionFuenteRecursos.xlsx";
                cantidad++;
            }

            if (salidaNombreExcel2 != null) {
                salida[cantidad] = salidaNombreExcel2;
                nombres[cantidad] = "SaldosDisponiblesRevisionConfiguracionCuentasBancarias.xlsx";
                cantidad++;
            }
            try {

                if (cantidad > 0) {
                    archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                    salida,
                                    nombres, "VerificarConfiguracionSaldos");

                }

            }
            catch (JRException | IOException | SQLException | DRException e2) {
                logger.error(e2.getMessage(), e2);
                JsfUtil.agregarMensajeError(e2.getMessage());
            }
        }

        else {

            String sql = Reporteador.resuelveConsulta(
                            "800343RUBROS_NOCONFIGURADOS_FUT_GASTOS_INVERSION",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            try {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL,
                                "RubrosNoConfiguradosFutGastosInversion.xlsx");
            }
            catch (JRException | IOException | SQLException | DRException
                            | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    /**
     * Metodo ejecutado al cambiar el control PorMes
     *
     */
    public void cambiarPorMes() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        validarMeses();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes() {
        validarMeses();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    public void validarMeses() {
        if (porMes) {
            etiqueta = "Mes:";
            visible = false;
            mesInicial = mes;
            mesFinal = mes;
        }
        else {
            etiqueta = "Trimestre:";
            visible = true;
            if (trimestre == 1) {
                mesInicial = 1;
                mesFinal = 3;
            }
            else if (trimestre == 2) {
                mesInicial = 4;
                mesFinal = 6;
            }
            else if (trimestre == 3) {
                mesInicial = 7;
                mesFinal = 9;
            }
            else {
                mesInicial = 10;
                mesFinal = 12;
            }
        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable pesos
     *
     * @return pesos
     */
    public boolean isPesos() {
        return pesos;
    }

    /**
     * Asigna la variable pesos
     *
     * @param pesos
     * Variable a asignar en pesos
     */

    public void setPesos(boolean pesos) {
        this.pesos = pesos;
    }

    /**
     * Retorna la variable planoPExcel
     *
     * @return planoPExcel
     */
    public boolean isPlanoPExcel() {
        return planoPExcel;
    }

    /**
     * Asigna la variable planoPExcel
     *
     * @param planoPExcel
     * Variable a asignar en planoPExcel
     */
    public void setPlanoPExcel(boolean planoPExcel) {
        this.planoPExcel = planoPExcel;
    }

    /**
     * Retorna la variable sicep
     *
     * @return sicep
     */
    public boolean isSicep() {
        return sicep;
    }

    /**
     * Asigna la variable sicep
     *
     * @param sicep
     * Variable a asignar en sicep
     */
    public void setSicep(boolean sicep) {
        this.sicep = sicep;
    }

    /**
     * Retorna la variable porMes
     *
     * @return porMes
     */

    public boolean isPorMes() {
        return porMes;
    }

    /**
     * Asigna la variable porMes
     *
     * @param porMes
     * Variable a asignar en porMes
     */
    public void setPorMes(boolean porMes) {
        this.porMes = porMes;
    }

    /**
     * Retorna la variable trimestre
     *
     * @return trimestre
     */
    public int getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     *
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable anioTrabajo
     *
     * @return anioTrabajo
     */
    public int getAnioTrabajo() {
        return anioTrabajo;
    }

    /**
     * Asigna la variable anioTrabajo
     *
     * @param anioTrabajo
     * Variable a asignar en anioTrabajo
     */
    public void setAnioTrabajo(int anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
    }

    /**
     * Retorna la variable nombreArchivo
     *
     * @return nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Asigna la variable nombreArchivo
     *
     * @param nombreArchivo
     * Variable a asignar en nombreArchivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Retorna la variable codigoEntidad
     *
     * @return codigoEntidad
     */
    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    /**
     * Asigna la variable codigoEntidad
     *
     * @param codigoEntidad
     * Variable a asignar en codigoEntidad
     */
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    /**
     * Retorna la variable ruta
     *
     * @return ruta
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * Asigna la variable ruta
     *
     * @param ruta
     * Variable a asignar en ruta
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Retorna la variable hoja
     *
     * @return hoja
     */
    public String getHoja() {
        return hoja;
    }

    /**
     * Asigna la variable hoja
     *
     * @param hoja
     * Variable a asignar en hoja
     */
    public void setHoja(String hoja) {
        this.hoja = hoja;
    }

    /**
     * Retorna la variable visible
     *
     * @return visible
     */

    public boolean isVisible() {
        return visible;
    }

    /**
     * Asigna la variable visible
     *
     * @param visible
     * Variable a asignar en visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
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
     * Retorna la lista listaAnoTrabajo
     *
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     *
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the verNombreFuente
     */
    public boolean isVerNombreFuente() {
        return verNombreFuente;
    }

    /**
     * @param verNombreFuente
     * the verNombreFuente to set
     */
    public void setVerNombreFuente(boolean verNombreFuente) {
        this.verNombreFuente = verNombreFuente;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
