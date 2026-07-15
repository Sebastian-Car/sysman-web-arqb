/*-
 * ListadoFraudesControlador.java
 *
 * 1.0
 *
 * 26/09/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ListadoFraudesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario modal que permite generar el listado de los procesos de
 * Fraudes.
 *
 * @version 1.0, 26/09/2016
 * @author jrodrigueza
 * @modified jguerrero
 * @version 2. 05/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class ListadoFraudesControlador extends BeanBaseModal {
    /**
     * Código de la compañía con la que el usuario ingresó.
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indica si deben ser seleccionados todos los ciclos.
     */
    private boolean seleccionaTodos;
    /**
     *
     */
    private int ciclo;
    /**
     * Código de la ruta inicial.
     */
    private String rutaInicial;
    /**
     * Código de la ruta final.
     */
    private String rutaFinal;
    /**
     * Estado del fraude.
     */
    private String estado;
    /**
     * Fecha Inicial.
     */
    private Date fechaInicial;
    /**
     * Fecha Final.
     */
    private Date fechaFinal;
    /**
     * Objeto para descargar el reporte que se genera.
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de ciclos.
     */
    private List<Registro> listaCiclo;
    /**
     * Listado de rutas para seleccionar la inicial.
     */
    private List<Registro> listaRutaIni;
    /**
     * Listado de rutas para seleccionar la final
     */
    private List<Registro> listaRutaFin;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Creates a new instance of ListadoFraudesControlador
     */
    public ListadoFraudesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_FRAUDES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            fechaInicial = new Date();
            fechaFinal = new Date();
            setSeleccionaTodos(true);
            estado = "T";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /***
     * Inicialización de las listas que cargan los combos.
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaRutaIni();
        cargarListaRutaFin();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Acciones que se ejecutan al abrir el formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista de ciclos.
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFraudesControladorUrlEnum.URL5198
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 214005
    }

    /**
     * Carga la lista de rutas.
     */
    public void cargarListaRutaIni() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        try {
            listaRutaIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFraudesControladorUrlEnum.URL5199
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Carga la lista de rutas.
     */
    public void cargarListaRutaFin() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), rutaInicial);

        try {
            listaRutaFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoFraudesControladorUrlEnum.URL5200
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
     * Acciones que se ejecutan al oprimir el botón imprimir.
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!seleccionaTodos && (ciclo == 0)) {
            JsfUtil.agregarMensajeAlerta("Por favor seleccione un ciclo.");
            return;
        }
        try {
            generarInforme();
        }
        catch (DRException | SQLException | NamingException
                        | IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeErrorDialogo(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera informe en excel con encabezado personalizado.
     *
     * @throws NamingException
     * @throws SQLException
     * @throws IOException
     * @throws DRException
     * @throws ParseException
     */
    private void generarInforme() throws NamingException, SQLException,
                    IOException, DRException, ParseException {

        if (!validarFechas()) {
            return;
        }

        ConectorPool conectorPool = new ConectorPool();
        Statement statement = null;
        ResultSet rs = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            conectorPool.conectar(ConectorPool.ESQUEMA_SYSMAN);
            conectorPool.getConection().setAutoCommit(false);
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("rutaInicial", rutaInicial);
            reemplazos.put("rutaFinal", rutaFinal);
            String criterioEstado = "T".equals(estado)
                ? "" : "AND SP_FRAUDES.ESTADO = '" + estado + "'";
            reemplazos.put("criterioEstado", criterioEstado);
            reemplazos.put("ciclo", 0 == ciclo ? ""
                : "AND SP_FRAUDES.CICLO=" + ciclo);

            String sql = Reporteador.resuelveConsulta("800059ListadoFraudes",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);
            statement = conectorPool.getConection().createStatement();
            rs = statement.executeQuery(sql);
            if (!rs.next()) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                return;
            }
            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(sql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            workbook.setSheetName(0, "Fraudes");
            List<String> listadoFilas = getFilasEncabezado();
            crearFilaEncabezado(workbook, listadoFilas);
            workbook.setForceFormulaRecalculation(true);
            conectorPool.getConection().commit();
            workbook.write(out);
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "listadoFraudes.xls");
        }
        catch (JRException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            Logger.getLogger(ListadoFraudesControlador.class
                            .getName()).log(Level.SEVERE, null, e);
        }

        finally {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
            conectorPool.getConection().close();
        }
    }

    /**
     * Crea la lista de parrafos que se van a mostrar en el encabezado
     * del informe.
     *
     * @return lineas de texto para el encabezado
     * @throws ParseException
     * en caso de que el formato de fecha no sea válido.
     */
    private List<String> getFilasEncabezado() throws ParseException {
        List<String> list = new ArrayList<>();
        // Listado de Fraudes a s$fechaHoy$s
        String texto = idioma.getString("TB_TB1671");
        texto = texto.replace("s$fechaHoy$s",
                        SysmanFunciones.convertirAFechaCadena(new Date(),
                                        "dd/MM/yyyy hh:mm:ss a"));
        list.add(texto);
        // Entre s$fechaInicial$s y s$fechaFinal$s
        texto = idioma.getString("TB_TB1672");
        texto = texto.replace("s$fechaInicial$s",
                        SysmanFunciones.convertirAFechaCadena(fechaInicial));
        texto = texto.replace("s$fechaFinal$s",
                        SysmanFunciones.convertirAFechaCadena(fechaFinal));
        list.add(texto);
        // Ciclo: s$ciclo$s
        texto = idioma.getString("TB_TB1673");
        texto = texto.replace("s$ciclo$s", ciclo == 0
            ? idioma.getString("TI_TODOS") : String.valueOf(ciclo));
        list.add(texto);
        // Código de Ruta entre s$rutaInicial$s y s$rutaFinal$s
        texto = idioma.getString("TB_TB1674");
        texto = texto.replace("s$rutaInicial$s", rutaInicial);
        texto = texto.replace("s$rutaFinal$s", rutaFinal);
        list.add(texto);
        // Estado: s$estado$s
        texto = idioma.getString("TB_TB1675");
        texto = texto.replace("s$estado$s", getNombreEstado(estado));
        list.add(texto);
        return list;
    }

    /**
     * Inserta las filas del encabezado al inicio de la hoja de
     * trabajo.
     *
     * @param workbook
     * Libro de trabajo.
     * @param list
     * Lista de textos que van en el encabezado.
     */
    private void crearFilaEncabezado(Workbook workbook, List<String> list) {
        Sheet sheet = workbook.getSheetAt(0);
        sheet.shiftRows(0, sheet.getLastRowNum(), list.size());
        int ultimaCelda = sheet.getRow(list.size())
                        .getLastCellNum();
        ultimaCelda = Math.max(ultimaCelda, 0) - 1;
        CellStyle style = crearEstiloEncabezado(workbook);
        Row filaInicial = sheet.getRow(0);
        short altoInicial = filaInicial != null ? filaInicial.getHeight()
            : (short) 1;
        int i = 0;
        for (String texto : list) {
            Row row = sheet.createRow(i);
            row.setHeight(altoInicial);
            Cell cell = row.createCell(0);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellStyle(style);
            cell.setCellValue(texto);
            sheet.addMergedRegion(
                            new CellRangeAddress(i, i, 0, ultimaCelda));
            CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);
            i++;
        }
    }

    /**
     * Crea estilo de celdas personalizado para el encabezado.
     *
     * @param workbook
     * Libro de trabajo.
     * @return estilo de celda.
     */
    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(getFuenteTexto(workbook, "SansSerif", (short) 10,
                        IndexedColors.BLACK.getIndex(), true));
        style.setWrapText(true);
        return style;
    }

    /**
     * Crea una fuente de texto según los parametros ingresados.
     *
     * @param workbook
     * Libro de trabajo.
     * @param fontName
     * Nombre de la fuente.
     * @param size
     * Tamaño del texto.
     * @param color
     * Color del texto.
     * @param negrita
     * Indica si la fuente debe usar negrilla.
     * @return fuente de texto.
     */
    public Font getFuenteTexto(Workbook workbook, String fontName, short size,
        short color, boolean negrita) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(size);
        font.setFontName(fontName);
        font.setColor(color);
        font.setBold(negrita);
        return font;
    }

    /**
     * Trae el nombre del estado.
     *
     * @param codigoEstado
     * Código del estado.
     * @return nombre de estado.
     */
    private String getNombreEstado(String codigoEstado) {
        String nombreEstado = "";
        switch (codigoEstado) {
        case "A":
            nombreEstado = idioma.getString("OD_CB3700_0");
            break;
        case "C":
            nombreEstado = idioma.getString("OD_CB3700_1");
            break;
        case "T":
            nombreEstado = idioma.getString("TI_TODOS");
            break;
        default:
            break;
        }
        return nombreEstado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        rutaInicial = "";
        rutaFinal = "";
        cargarListaRutaIni();
        cargarListaRutaFin();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control RutaIni
     * 
     * 
     */
    public void cambiarRutaIni() {
        // <CODIGO_DESARROLLADO>
        rutaFinal = null;
        cargarListaRutaFin();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerificacionTodosCiclos() {
        // <CODIGO_DESARROLLADO>
        ciclo = 0;
        rutaInicial = null;
        rutaFinal = null;
        cargarListaRutaIni();
        cargarListaRutaFin();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna el valor del check para seleccionar todos los ciclos.
     *
     * @return
     */
    public boolean isSeleccionaTodos() {
        return seleccionaTodos;
    }

    /**
     * Trae el valor del ciclo seleccionado.
     *
     * @return número de ciclo.
     */
    public int getCiclo() {
        return ciclo;
    }

    /**
     * Asigna el valor al ciclo.
     *
     * @param ciclo
     */
    public void setCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Asigna un valor al check selecionar todos los ciclos.
     *
     * @param seleccionaTodos
     */
    public void setSeleccionaTodos(boolean seleccionaTodos) {
        this.seleccionaTodos = seleccionaTodos;
    }

    /**
     * Trae el valor de la ruta inicial.
     *
     * @return ruta inicial
     */
    public String getRutaInicial() {
        return rutaInicial;
    }

    /**
     * Asigna un valor a la ruta inicial
     *
     * @param rutaInicial
     * código de la ruta inicial
     */
    public void setRutaInicial(String rutaInicial) {
        this.rutaInicial = rutaInicial;
    }

    /**
     * Trae el valor de la ruta final
     *
     * @return ruta final
     */
    public String getRutaFinal() {
        return rutaFinal;
    }

    /**
     * Asigna un valor a la ruta final.
     *
     * @param rutaFinal
     * ruta final
     */
    public void setRutaFinal(String rutaFinal) {
        this.rutaFinal = rutaFinal;
    }

    /**
     * Trae el valor del estado del fraude.
     *
     * @return estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna un valor al estado del fraude.
     *
     * @param estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Trae el valor de la fecha inicial.
     *
     * @return fecha inicial.
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna un valor a la fecha inicial.
     *
     * @param fechaInicial
     * fecha inicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Trae el valor de la fecha final
     *
     * @return fecha final
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna un valor a la fecha final.
     *
     * @param fechaFinal
     * Fecha final
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna el valor del archivo de descarga.
     *
     * @return informe generado
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Trae la lista de ciclos.
     *
     * @return Lista de ciclos
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna los elementos a la lista de ciclos.
     *
     * @param listaCiclo
     * Lista de ciclos.
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Trae la lista de rutas.
     *
     * @return listado de rutas
     */
    public List<Registro> getListaRutaIni() {
        return listaRutaIni;
    }

    /**
     * Asigna los elementos a la lista de rutas.
     *
     * @param listaRutaIni
     * Lista de rutas.
     */
    public void setListaRutaIni(List<Registro> listaRutaIni) {
        this.listaRutaIni = listaRutaIni;
    }

    /**
     * Trae la lista de rutas.
     *
     * @return listado de rutas
     */
    public List<Registro> getListaRutaFin() {
        return listaRutaFin;
    }

    /**
     * Asigna los elementos a la lista de rutas.
     *
     * @param listaRutaFin
     * Lista de rutas.
     */
    public void setListaRutaFin(List<Registro> listaRutaFin) {
        this.listaRutaFin = listaRutaFin;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private boolean validarFechas() {
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
            return false;
        }
        return true;
    }

}
