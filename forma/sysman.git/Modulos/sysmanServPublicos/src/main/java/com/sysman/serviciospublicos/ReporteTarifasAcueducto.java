/*-
 * ReporteTarifasAcueducto.java
 *
 * 1.0
 *
 * 17/11/2016
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ReporteTarifasAcueductoEnum;
import com.sysman.serviciospublicos.enums.ReporteTarifasAcueductoUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte e TArifas Aplicadas para un rango de anios y periodos seleccionados
 *
 * @version 1.0, 17/11/2016
 * @author jlozano
 *
 * @version 2.0, 16/06/2017
 * @author jguerrero Se realiza el refactoring a las consultas del formulario incluyendo la generación en excel del reporte.
 */
@ManagedBean
@ViewScoped
public class ReporteTarifasAcueducto extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del ciclo seleccionado
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor del anio inicial seleccionado
     */
    private String anoInicial;
    /**
     * Atributo que almacena el valor del periodo inicial seleccionado
     */
    private String periodoInicial;
    /**
     * Atributo que almacena el valor del anio final seleccionado
     */
    private String anoFinal;
    /**
     * Atributo que almacena el valor del periodo final seleccionado
     */
    private String periodoFinal;
    /**
     * Atributo que almacena el valor del nombre de la hoja
     */
    private String nombreHoja;
    /**
     * Atributo que almacena el valor del nombre del archivo seleccionado
     */

    private String titulo;

    private String nombreArchivo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de archivos selectorPlantilla y funciona como contenedor del archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoselectorPlantilla;
    /**
     * Constante para el literal "PERIODO"
     */
    private static final String PERIODO = "PERIODO";
    /**
     * Constante para el literal "2. Tar. alc."
     */
    private static final String NOMBRE_HOJA = "2. Tar. alc.";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena los anios disponibles
     */
    private List<Registro> listaAnoInicial;
    /**
     * Lista que almacena los periodos disponibles
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista que almacena los anios disponibles
     */
    private List<Registro> listaAnoFinal;
    /**
     * Lista que almacena los periodos disponibles
     */
    private List<Registro> listaPeriodoFinal;
    /**
     * Lista de hojas disponibles en el archivo seleccionado
     */
    private List<Registro> listaNombreHoja;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ReporteTarifasAcueducto
     */
    public ReporteTarifasAcueducto()
    {
        super();
        nombreFormulario();
        compania = SessionUtil.getCompania();
        anoInicial = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        anoFinal = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        periodoInicial = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.MONTH)
            + 1);
        periodoFinal = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.MONTH)
            + 1);
        nombreArchivo = "sol_tarifas_2003_ac_alc";
        contArchivoselectorPlantilla = new ContenedorArchivo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.REPORTE_TARIFAS_ACUEDUCTO
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>

        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1206-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim db As DAO.Database Dim rs As DAO.Recordset Dim str As String Set db = CurrentDb() str = "T" Set rs = db.OpenRecordset(
         * "Select * from Ciclo where Compania='" & Getcompany() & "' Order by Compania,Numero", , dbSQLPassThrough) While Not rs.EOF str = str & ";" & rs!Numero rs.MoveNext Wend Me!Ciclo.RowSource =
         * str End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnoInicial
     *
     */
    public void cargarListaAnoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoInicial = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ReporteTarifasAcueductoUrlEnum.URL7670.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPeriodoInicial
     *
     */
    public void cargarListaPeriodoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaPeriodoInicial = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ReporteTarifasAcueductoUrlEnum.URL8298.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaAnoFinal
     *
     */
    public void cargarListaAnoFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoFinal = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ReporteTarifasAcueductoUrlEnum.URL9078.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPeriodoFinal
     *
     */
    public void cargarListaPeriodoFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), anoFinal);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.MES.getName(), periodoInicial);

        try
        {
            listaPeriodoFinal = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ReporteTarifasAcueductoUrlEnum.URL9696.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     */
    public void cargarListaNombreHoja()
    {
        // No tiene sentencias

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     * Genera el reporte en formato Excel
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    public String obtenerNombreMes(String mes)
    {
        String res = "";
        try
        {
            res = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)];
        }
        catch (NumberFormatException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return res;
    }

    private void generarExcel()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ReporteTarifasAcueductoEnum.ANOINICIAL.getValue(), anoInicial);
        param.put(ReporteTarifasAcueductoEnum.ANOFINAL.getValue(), anoFinal);
        param.put(ReporteTarifasAcueductoEnum.PERIODOINICIAL.getValue(), periodoInicial);
        param.put(ReporteTarifasAcueductoEnum.PERIODOFINAL.getValue(), periodoFinal);

        List<Registro> listaPivot;

        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoselectorPlantilla.getArchivo());)
        {
            listaPivot = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ReporteTarifasAcueductoUrlEnum.URL14772.getValue()).getUrl(), param));

            String inPivot = "";
            StringBuilder aux = new StringBuilder();
            for (Registro registro : listaPivot)
            {
                aux.append("'" + registro.getCampos().get(PERIODO) + "' AS \""
                    + registro.getCampos().get(PERIODO) + "\",");
            }
            inPivot = aux.toString();
            if (inPivot.length() > 0)
            {
                inPivot = inPivot.substring(0, inPivot.length() - 1);
            }
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("anoInicial", anoInicial);
            reemplazos.put("periodoInicial", periodoInicial);
            reemplazos.put("anoFinal", anoFinal);
            reemplazos.put("periodoFinal", periodoFinal);
            reemplazos.put("inPivot", inPivot);

            String strSql = Reporteador.resuelveConsulta(
                            "800068InformeTarifasAcueducto",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            Workbook workbook = new HSSFWorkbook(fileIs);
            Sheet sheet = workbook.getSheet(nombreHoja);
            sheet.shiftRows(0, sheet.getLastRowNum(), 7);

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            Font font = workbook.createFont();
            font.setFontName("SansSerif");
            font.setBold(true);
            style.setFont(font);
            Row encabezado = sheet.createRow(7);
            int index = 1;
            crearEncabezadoExcel(listaPivot, encabezado, index, style);

            for (int i = 0; i < rs.size(); i++)
            {
                Row row = sheet.createRow(i + 10);
                for (int j = 0; j < listaPivot.size(); j++)
                {
                    Cell cell = row.createCell(j + 1);
                    Object value = rs.get(i).getCampos().get(listaPivot.get(j).getCampos().get(PERIODO).toString()) == null ? 0
                        : rs.get(i).getCampos().get(listaPivot.get(j).getCampos().get(PERIODO).toString());

                    cell.setCellValue(Double.parseDouble(value.toString()));
                    sheet.autoSizeColumn(j + 1);
                }
            }

            CellReference cellRefIniTitulo = new CellReference(2, 0);
            String celdaIniTitulo = cellRefIniTitulo.formatAsString();
            CellReference cellRefFinTitulo = new CellReference(2,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo = cellRefFinTitulo.formatAsString();
            CellRangeAddress region = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo + ":" + celdaFinTitulo);
            sheet.addMergedRegion(region);

            CellReference cellRefIniTitulo1 = new CellReference(5, 0);
            String celdaIniTitulo1 = cellRefIniTitulo1.formatAsString();
            CellReference cellRefFinTitulo1 = new CellReference(5,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo1 = cellRefFinTitulo1.formatAsString();
            CellRangeAddress region1 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo1 + ":" + celdaFinTitulo1);
            sheet.addMergedRegion(region1);

            CellReference cellRefIniTitulo2 = new CellReference(6, 0);
            String celdaIniTitulo2 = cellRefIniTitulo2.formatAsString();
            CellReference cellRefFinTitulo2 = new CellReference(6,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo2 = cellRefFinTitulo2.formatAsString();
            CellRangeAddress region2 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo2 + ":" + celdaFinTitulo2);
            sheet.addMergedRegion(region2);

            Cell cell = sheet.createRow(5).createCell(0);
            cell.setCellValue(idioma.getString("TG_CIUDAD2")
                + " " + SessionUtil.getCompaniaIngreso().getCiudad()
                + StringUtils.repeat(" ", 20)
                + idioma.getString("TG_DEPARTAMENTO") + ": " +
                SessionUtil.getCompaniaIngreso().getDepartamento());
            Cell cell2 = sheet.createRow(6).createCell(0);
            cell2.setCellValue(idioma.getString("TB_TB2055")
                + " " + SessionUtil.getCompaniaIngreso().getNombre());
            Cell cell3 = sheet.createRow(2).createCell(0);
            cell3.setCellValue(idioma.getString("TB_TB2059")
                + " " + (anoInicial.equals(anoFinal)
                    ? anoInicial : anoInicial + "-" + anoFinal));

            workbook.setActiveSheet(workbook.getSheetIndex(sheet));
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            contArchivoselectorPlantilla.getArchivo()
                                            .getName());
            fileIs.close();
        }
        catch (IOException | JRException | SystemException e1)
        {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    /**
     * Crea las columnas del emcabezado del reporte Excel
     *
     * @param lista
     * Lista de elementos del encabezado
     * @param encabezado
     * Fila delencabezado
     * @param inicio
     * posicion inicial de le los encabezados
     * @param style
     * Estilo de las celdas de los encabezados
     */
    public void crearEncabezadoExcel(List<Registro> lista, Row encabezado,
        int inicio, CellStyle style)
    {
        int index = inicio;
        for (Registro registro : lista)
        {
            Cell cell = encabezado.createCell(index);

            String nombreMes = obtenerNombreMes((registro
                            .getCampos()
                            .get(PERIODO).toString()).substring(
                                            4,
                                            6));

            cell.setCellValue(nombreMes + "-" + (registro
                            .getCampos()
                            .get(PERIODO).toString()).substring(
                                            0,
                                            4));
            cell.setCellStyle(style);
            index++;
        }
        Cell total = encabezado.createCell(index);
        total.setCellValue(" ");
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcActualizarHojas en la vista
     *
     */
    public void ejecutarrcActualizarHojas()
    {
        // <CODIGO_DESARROLLADO>
        Workbook workbook = null;
        nombreHoja = "";
        listaNombreHoja = new ArrayList<>();
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoselectorPlantilla.getArchivo());)
        {
            workbook = new HSSFWorkbook(fileIs);
            cargarNombreHojas(workbook);
            for (int i = 0; i < listaNombreHoja.size(); i++)
            {
                if (NOMBRE_HOJA.equals(listaNombreHoja.get(i)
                                .getCampos().get("NOMBREHOJA")))
                {
                    nombreHoja = NOMBRE_HOJA;
                    break;
                }
            }
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     *
     * Recarga la lista de periodos iniciales filtrando por el anio inicial seleccionado
     *
     */
    public void cambiarAnoInicial()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial
     *
     * Recarga la lista periodoFinal filtrando por el anio final y periodo inicial seleccionados
     *
     */
    public void cambiarPeriodoInicial()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     *
     * Recarga la lista periodoFinal filtrando por el anio final y periodo inicial seleccionados
     *
     */
    public void cambiarAnoFinal()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga la lista de hojas que contien el archivo Excel seleccionado
     *
     * @param workbook
     */
    private void cargarNombreHojas(Workbook workbook)
    {
        listaNombreHoja.clear();
        int hojas = workbook.getNumberOfSheets();
        for (int i = 0; i < hojas; i++)
        {
            Sheet sheet = workbook.getSheetAt(i);
            String hoja = sheet.getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put("NOMBREHOJA", hoja);
            listaNombreHoja.add(reg);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial()
    {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial)
    {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial()
    {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial)
    {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal()
    {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal)
    {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal()
    {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal)
    {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable nombreHoja
     *
     * @return nombreHoja
     */
    public String getNombreHoja()
    {
        return nombreHoja;
    }

    /**
     * Asigna la variable nombreHoja
     *
     * @param nombreHoja
     * Variable a asignar en nombreHoja
     */
    public void setNombreHoja(String nombreHoja)
    {
        this.nombreHoja = nombreHoja;
    }

    /**
     * Retorna la variable nombreArchivo
     *
     * @return nombreArchivo
     */
    public String getNombreArchivo()
    {
        return nombreArchivo;
    }

    /**
     * Asigna la variable nombreArchivo
     *
     * @param nombreArchivo
     * Variable a asignar en nombreArchivo
     */
    public void setNombreArchivo(String nombreArchivo)
    {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public ContenedorArchivo getContArchivoselectorPlantilla()
    {
        return contArchivoselectorPlantilla;
    }

    /**
     * Asigna el objeto contArchivoselectorPlantilla
     *
     * @param contArchivoselectorPlantilla
     * Variable a asignar en contArchivoselectorPlantilla
     */
    public void setContArchivoselectorPlantilla(
        ContenedorArchivo contArchivoselectorPlantilla)
    {
        this.contArchivoselectorPlantilla = contArchivoselectorPlantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoInicial
     *
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial()
    {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     *
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial)
    {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial()
    {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial)
    {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     *
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal()
    {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     *
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal)
    {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal()
    {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal)
    {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaNombreHoja
     */
    public List<Registro> getListaNombreHoja()
    {
        return listaNombreHoja;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    /**
     * Asigna la lista listaNombreHoja
     *
     * @param listaNombreHoja
     * Variable a asignar en listaNombreHoja
     */
    public void setListaNombreHoja(List<Registro> listaNombreHoja)
    {
        this.listaNombreHoja = listaNombreHoja;
    }

    public void nombreFormulario()
    {

        if ("7407070101".equals(SessionUtil.getMenuActual()))
        {
            titulo = idioma.getString("TB_TB2694");
        }
        else
        {
            titulo = idioma.getString("TB_TB2695");
        }

    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
