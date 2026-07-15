/*-
 * FuncionamientoFutControlador.java
 *
 * 1.0
 * 
 * 27/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.FuncionamientoFutControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera reporte de funcionamiento fut
 *
 * @version 1.0, 27/03/2017
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class FuncionamientoFutControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indicador para generar reporte con pesos
     */
    private boolean ckPesos;
    /**
     * Indicador para generar reporte excel
     */
    private boolean ckPlanoExcel;
    /**
     * Indicador para generar SICEP al reporte
     */
    private boolean ckSicep;
    /**
     * Indicador para revisar verificaciones de generacion
     */
    private boolean ckVerificacion;
    /**
     * Indicador para validar transeferencia
     */
    private boolean ckTransferencia;
    /**
     * Indicador para generar reporte por mes
     */
    private boolean ckPorMes;
    /**
     * Variable que almacena el valor del trimestre
     */
    private String trimestre;
    /**
     * Valriable que almacena el anio seleccionado en la forma
     */
    private String anio;
    /**
     * Variable que almacena el codigo de la entidad
     */

    private String codigoEntidad;
    /**
     * Atributo que contiene el valor asignado al mes en la forma del
     * formulario.
     */
    private int mes;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private boolean visible;

    private boolean camposPeriodo;

    private boolean visibleMes;
    private boolean movimiento;
    private boolean visibleCargar;
    private boolean visibleBoton;
    private String tituloPeriodo;
    private String parRedondeo;
    private int mesFinal;
    private int mesInicial;
    private String periodo;
    private String consulta;
    private String consultaAgrupacion;
    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios de trabajo
     */
    private List<Registro> listaAnoTrabajo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FuncionamientoFutControlador
     */
    public FuncionamientoFutControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1386
            numFormulario = GeneralCodigoFormaEnum.FUNCIONAMIENTO_FUT_CONTROLADOR
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
        cargarListaAnoTrabajo();
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
        // <CODIGO_DESARROLLADO>
        visible = true;
        visibleCargar = false;
        visibleBoton = true;
        String parComboMes;
        boolean mostrarComboMes = true;
        try {
            parComboMes = ejbSysmanUtil.consultarParametro(compania,
                            "MOSTRAR COMBO POR MES EN INFORMES CGR-FUT",
                            SessionUtil.getModulo(), new Date(), false);

            mostrarComboMes = ("SI").equals(parComboMes);

            parRedondeo = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITO REDONDEO DE INFORMES FUT",
                            SessionUtil.getModulo(), new Date(), false);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (mostrarComboMes) {
            visibleMes = true;
        }
        else {
            visibleMes = false;
        }
        tituloPeriodo = "Trimestre:";
        /*
         * FR1386-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore Me!NombreArchivo =
         * "C:\Sysman\Funcionamiento_FUT.XLS" If
         * par("MOSTRAR COMBO POR MES EN INFORMES CGR-FUT") = "SI"
         * Then Me.PorMes.visible = True Me.Etiqueta89.visible = True
         * End If End Sub
         */
        // </CODIGO_DESARROLLADO>
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
                                                            FuncionamientoFutControladorUrlEnum.URL7526
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
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
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String nombreArchivo = validarNombre();
        asignarPeriodo();
        definirMeses();
        if (!ckPlanoExcel) {
            if (ckTransferencia) {
                prepararConsulta(1);
            }
            else {
                prepararConsulta(2);
            }
            llenarDetallePlano(nombreArchivo);
        }
        else {
            cargarGenerar(nombreArchivo);
        }
        // </CODIGO_DESARROLLADO>
    }

    public String validarNombre() {
        String nombre = SessionUtil.getCompaniaIngreso().getNombre();
        if (nombre.length() > 20 && ckTransferencia) {
            nombre = nombre.substring(0, 20)
                + "TRANSFERENCIAS_COMPROMETIDAS.txt";
        }
        else {
            nombre = nombre + "TRANSFERENCIAS_COMPROMETIDAS.txt";
        }

        if (nombre.length() > 20 && !ckTransferencia) {
            nombre = nombre.substring(0, 20) + "_GFuncionamiento.txt";
        }
        else {
            nombre = nombre + "_GFuncionamiento.txt";
        }
        return nombre;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando76 en la vista
     *
     *
     */
    public void oprimirComando76() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!ckSicep) {
            if (ckTransferencia) {
                prepararConsulta(1);
            }
            else {
                prepararConsulta(2);
                movimiento = true;
            }
        }
        else {
            prepararConsulta(3);
        }

        if (ckVerificacion) {

        }
        else {

        }

        // </CODIGO_DESARROLLADO>
    }

    public void prepararConsulta(int opcion) {

        Map<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> reemplazosRes = new HashMap<>();
        Map<String, Object> reemplazosResF = new HashMap<>();
        Map<String, Object> reemplazosInvFut = new HashMap<>();

        if (opcion == 1) {
            reemplazos.put("compania", compania);
            reemplazos.put("anio", anio);
            reemplazos.put("mesFinal", mesFinal);
            reemplazos.put("redondeo", parRedondeo);
            reemplazos.put("codigoEntidad", codigoEntidad);
            reemplazos.put("pesos", ckPesos ? 1 : 0);
            consulta = Reporteador.resuelveConsulta("tmp_transferencias_fut",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);
        }
        else {
            reemplazosRes.put("compania", compania);
            reemplazosRes.put("anio", anio);
            reemplazosRes.put("mesInicial", mesInicial);
            reemplazosRes.put("mesFinal", mesFinal);

            reemplazosResF.put("resumenPpto_Pfun",
                            Reporteador.resuelveConsulta("resumenPpto_Pfun",
                                            Integer.parseInt(SessionUtil
                                                            .getModulo()),
                                            reemplazosRes));

            reemplazosResF.put("resumenPpto_Afun",
                            Reporteador.resuelveConsulta("resumenPpto_Afun",
                                            Integer.parseInt(SessionUtil
                                                            .getModulo()),
                                            reemplazosRes));

            reemplazosInvFut.put("resumenPpto_Ffun",
                            Reporteador.resuelveConsulta("resumenPpto_Ffun",
                                            Integer.parseInt(SessionUtil
                                                            .getModulo()),
                                            reemplazosResF));

            reemplazosInvFut.put("pesos", ckPesos ? 1 : 0);
            reemplazosInvFut.put("movimiento",
                            movimiento ? ",CODIGOSFUT.MOVIMIENTO " : " ");
            reemplazosInvFut.put("redondeo", parRedondeo);

            if (opcion == 2) {
                consulta = Reporteador.resuelveConsulta("inversionesFut",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazosInvFut);
                consultaAgrupacion = Reporteador.resuelveConsulta(
                                "inversionesFutAgrupacion",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazosInvFut);
            }
            if (opcion == 3) {
                consulta = Reporteador.resuelveConsulta("inversionesFutSicep",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazosInvFut);
            }
        }

    }

    public void definirMeses() {
        if (!ckPorMes) {
            mesInicial = (Integer.parseInt(trimestre) * 3) - 2;
            mesFinal = Integer.parseInt(trimestre) * 3;
        }
        else {
            mesInicial = 1;
            mesFinal = mes;
        }

    }

    public void asignarPeriodo() {
        if (ckPorMes) {
            periodo = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes];
        }
        else {
            if ("1".equals(trimestre)) {
                periodo = "0103";
            }
            else if ("2".equals(trimestre)) {
                periodo = "0406";
            }
            else if ("3".equals(trimestre)) {
                periodo = "0709";
            }
            else if ("4".equals(trimestre)) {
                periodo = "1012";
            }
        }
    }

    public void llenarDetallePlano(String nombreArchivo) {
        StringBuilder plano = new StringBuilder();
        List<Registro> aux = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        consulta);
        if (!aux.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3038"));
            return;
        }
        if (!ckTransferencia) {
            List<Registro> auxAgrupacion = service.getListado(
                            ConectorPool.ESQUEMA_SYSMAN, consultaAgrupacion);
            for (int i = 0; i < auxAgrupacion.size(); i++) {
                plano.append("\r\nD\t").append("VAL")
                                .append(aux.get(i).getCampos()
                                                .get("UNIDADEJECUTORA_FUT"))
                                .append("\t")
                                .append(aux.get(i).getCampos().get("FUENTE"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("PRESUPUESTO_INICIAL"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("PRESUPUESTO_DEFINITIVO"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("COMPROMISOS"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("OBLIGACIONES"))
                                .append("\t")
                                .append(aux.get(i).getCampos().get("PAGOS"));
            }
        }

        for (int i = 0; i < aux.size(); i++) {
            // ROUND FALTA
            if (ckTransferencia) {
                plano.append("\r\nD\t")
                                .append(aux.get(i).getCampos().get("CODIGO"))
                                .append("\t")
                                .append(aux.get(i).getCampos().get("NOMBRE"))
                                .append("\t").append(aux.get(i).getCampos()
                                                .get("PRESUPUESTO_DEFINITIVO"));
            }
            else {
                plano.append("\r\nD\t")
                                .append(aux.get(i).getCampos().get("CODIGO"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("UNIDADEJECUTORA_FUT"))
                                .append("\t")
                                .append(aux.get(i).getCampos().get("FUENTE"))
                                .append(".0\t")
                                .append(aux.get(i).getCampos()
                                                .get("PRESUPUESTO_INICIAL"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("PRESUPUESTO_DEFINITIVO"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("COMPROMISOS"))
                                .append("\t")
                                .append(aux.get(i).getCampos()
                                                .get("OBLIGACIONES"))
                                .append("\t")
                                .append(aux.get(i).getCampos().get("PAGOS"));
            }
        }

        String cadena = plano.toString();
        try {
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(cadena),
                            nombreArchivo);
        }
        catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que permite crear el texto que llenara un archivo txt
     * basandose en el excel cargado por la aplicacion web
     * 
     */
    public void cargarGenerar(String nombre) {
        StringBuilder planoExcel = new StringBuilder();
        Sheet sheet = workbook.getSheetAt(0);
        // filas
        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            planoExcel.append("\r\nD\t");
            // Columnas
            for (int column = 0; column < Math.max(
                            sheet.getRow(1).getLastCellNum(), 0); column++) {
                // Valida que la fila y la celda especifica que se
                // va a leer no sean nulas
                if (validarFilaCeldaVacia(sheet, rowNum, column)) {
                    Row r = sheet.getRow(rowNum);
                    Cell cell = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    planoExcel.append(cell.getStringCellValue()).append("\t");
                }
            }
        }
        try {
            workbook.close();

            String cadena = planoExcel.toString();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(cadena),
                            nombre);
            ckPlanoExcel = false;
        }
        catch (IOException | JRException e) {
            Logger.getLogger(FuncionamientoFutControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Valida si una celda especifica dentro de una hoja de datos
     * viene nula o esta en blanco
     * 
     * @param sheet
     * Hoja de datos que se va a analizar
     * @param rowNum
     * Numero de fila que se evaluara
     * @param column
     * Numero de la columna dentro de la fila que se evaluara
     * @return Verdadero si la celda posee valor
     */
    private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
        boolean respuesta = false;
        Row rAux = sheet.getRow(rowNum);
        if (rAux != null) {
            Cell cellAux = rAux.getCell(column, Row.RETURN_BLANK_AS_NULL);
            if (cellAux != null) {
                cellAux.setCellType(Cell.CELL_TYPE_STRING);
                if (!SysmanFunciones.validarVariableVacio(
                                cellAux.getStringCellValue())) {
                    respuesta = true;
                }
            }
        }
        return respuesta;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control
     * cargarExcel
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivocargarExcel(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        String extension;
        try {
            InputStream is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }
            String rutaArchivo = event.getFile().getFileName();
            extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length())
                            .substring(1, rutaArchivo.substring(
                                            rutaArchivo.indexOf('.'),
                                            rutaArchivo.length()).length());
            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if (workbook == null) {
                if ("xls".equals(extension)) {
                    workbook = new HSSFWorkbook(is);
                }
                else {
                    workbook = new XSSFWorkbook(is);
                }
            }

            visibleCargar = false;
            visibleBoton = true;

        }
        catch (IOException e) {
            Logger.getLogger(FuncionamientoFutControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PlanoPExcel
     * 
     */
    public void cambiarPlanoPExcel() {
        // <CODIGO_DESARROLLADO>
        if (ckPlanoExcel) {
            visibleCargar = true;
            visibleBoton = false;
        }
        else {
            visibleCargar = false;
            visibleBoton = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Verificacion
     * 
     * 
     */
    public void cambiarVerificacion() {
        // <CODIGO_DESARROLLADO>
        if (ckVerificacion) {
            visible = false;
            visibleBoton = false;
            visibleCargar = false;
        }
        else {
            visible = true;
            visibleBoton = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorMes
     * 
     * 
     */
    public void cambiarPorMes() {
        // <CODIGO_DESARROLLADO>
        if (ckPorMes) {
            camposPeriodo = true;
            tituloPeriodo = "Mes:";
        }
        else {
            camposPeriodo = false;
            tituloPeriodo = "Trimestre:";
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ckPesos
     * 
     * @return ckPesos
     */
    public boolean getCkPesos() {
        return ckPesos;
    }

    /**
     * Asigna la variable ckPesos
     * 
     * @param ckPesos
     * Variable a asignar en ckPesos
     */
    public void setCkPesos(boolean ckPesos) {
        this.ckPesos = ckPesos;
    }

    /**
     * Retorna la variable ckPlanoExcel
     * 
     * @return ckPlanoExcel
     */
    public boolean getCkPlanoExcel() {
        return ckPlanoExcel;
    }

    /**
     * Asigna la variable ckPlanoExcel
     * 
     * @param ckPlanoExcel
     * Variable a asignar en ckPlanoExcel
     */
    public void setCkPlanoExcel(boolean ckPlanoExcel) {
        this.ckPlanoExcel = ckPlanoExcel;
    }

    /**
     * Retorna la variable ckSicep
     * 
     * @return ckSicep
     */
    public boolean getCkSicep() {
        return ckSicep;
    }

    /**
     * Asigna la variable ckSicep
     * 
     * @param ckSicep
     * Variable a asignar en ckSicep
     */
    public void setCkSicep(boolean ckSicep) {
        this.ckSicep = ckSicep;
    }

    /**
     * Retorna la variable ckVerificacion
     * 
     * @return ckVerificacion
     */
    public boolean getCkVerificacion() {
        return ckVerificacion;
    }

    /**
     * Asigna la variable ckVerificacion
     * 
     * @param ckVerificacion
     * Variable a asignar en ckVerificacion
     */
    public void setCkVerificacion(boolean ckVerificacion) {
        this.ckVerificacion = ckVerificacion;
    }

    /**
     * Retorna la variable ckTransferencia
     * 
     * @return ckTransferencia
     */
    public boolean getCkTransferencia() {
        return ckTransferencia;
    }

    /**
     * Asigna la variable ckTransferencia
     * 
     * @param ckTransferencia
     * Variable a asignar en ckTransferencia
     */
    public void setCkTransferencia(boolean ckTransferencia) {
        this.ckTransferencia = ckTransferencia;
    }

    /**
     * Retorna la variable ckPorMes
     * 
     * @return ckPorMes
     */
    public boolean getCkPorMes() {
        return ckPorMes;
    }

    /**
     * Asigna la variable ckPorMes
     * 
     * @param ckPorMes
     * Variable a asignar en ckPorMes
     */
    public void setCkPorMes(boolean ckPorMes) {
        this.ckPorMes = ckPorMes;
    }

    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isCamposPeriodo() {
        return camposPeriodo;
    }

    public void setCamposPeriodo(boolean camposPeriodo) {
        this.camposPeriodo = camposPeriodo;
    }

    public boolean isVisibleMes() {
        return visibleMes;
    }

    public void setVisibleMes(boolean visibleMes) {
        this.visibleMes = visibleMes;
    }

    public String getTituloPeriodo() {
        return tituloPeriodo;
    }

    public void setTituloPeriodo(String tituloPeriodo) {
        this.tituloPeriodo = tituloPeriodo;
    }

    public boolean isVisibleCargar() {
        return visibleCargar;
    }

    public void setVisibleCargar(boolean visibleCargar) {
        this.visibleCargar = visibleCargar;

    }

    public boolean isVisibleBoton() {
        return visibleBoton;
    }

    public void setVisibleBoton(boolean visibleBoton) {
        this.visibleBoton = visibleBoton;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
