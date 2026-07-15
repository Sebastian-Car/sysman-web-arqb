/*-
 * GenerarExcelConsolidadorControlador.java
 *
 * 1.0
 * 
 * 18/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.GenerarExcelConsolidadorControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar un informe para el consolidador.
 *
 * @version 1.0, 18/12/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class GenerarExcelConsolidadorControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private int anio;
    private String periodo;
    private int mesInicial;
    private int mesFinal;
    private String representante;
    private String tesorero;
    private String contador;
    private String tarjetaContador;
    private String revisor;
    private String tarjetaRevisor;
    private boolean bloqueadoMes;
    private ContenedorArchivo contArchivoPlantilla;
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
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSiete;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de GenerarExcelConsolidadorControlador
     */
    public GenerarExcelConsolidadorControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.GENERAR_EXCELCONSOLIDADOR_CONTROLADOR
                            .getCodigo();
            contArchivoPlantilla = new ContenedorArchivo();
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
        cargarListaMesInicial();
        cargarListaMesFinal();
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
        bloqueadoMes = true;
        /*
         * FR2011-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore Me.NombreArchivo = Getcompany() & "_" &
         * GetYear() & "_" & IIf(Me!PERIODO = "M", Format(Me!Mes,
         * "00"), Me!PERIODO.Column(4)) If
         * par("MANEJA PLANO CUENTAS QUE NO EXISTEN EN INSTITUCIONES",
         * Getcompany()) = "SI" Then NoExistenExcel.VISIBLE = True End
         * If End Sub
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
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarExcelConsolidadorControladorUrlEnum.URL4621
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);

            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarExcelConsolidadorControladorUrlEnum.URL7451
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenerarExcelConsolidadorControladorUrlEnum.URL3521
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
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
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            archivoDescarga = null;
            if (contArchivoPlantilla.getArchivo() == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
                return;
            }

            String rutaArchivo = contArchivoPlantilla.getArchivo()
                            .getPath();
            FileInputStream file;

            file = new FileInputStream(new File(rutaArchivo));
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);

            Font font = workbook.createFont();
            font.setFontName("Courier");
            font.setFontHeightInPoints((short) 10);
            font.setColor(IndexedColors.YELLOW.getIndex());

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_LEFT);
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);

            style.setBorderBottom((short) 1);
            style.setBorderLeft((short) 1);
            style.setBorderTop((short) 1);
            style.setBorderRight((short) 1);

            Font font2 = workbook.createFont();
            font2.setFontName("Courier");
            font2.setFontHeightInPoints((short) 10);
            font2.setBold(true);

            CellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(CellStyle.ALIGN_LEFT);
            style2.setFont(font2);
            style2.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
            style2.setFillPattern(CellStyle.SOLID_FOREGROUND);

            style2.setBorderBottom((short) 1);
            style2.setBorderLeft((short) 1);
            style2.setBorderTop((short) 1);
            style2.setBorderRight((short) 1);

            Font font3 = workbook.createFont();
            font3.setFontName("Courier");
            font3.setFontHeightInPoints((short) 10);
            font3.setBold(true);

            CellStyle style3 = workbook.createCellStyle();
            style3.setAlignment(CellStyle.ALIGN_RIGHT);
            style3.setFont(font3);
            style3.setFillForegroundColor(
                            IndexedColors.GREY_40_PERCENT.getIndex());
            style3.setFillPattern(CellStyle.SOLID_FOREGROUND);

            style3.setBorderBottom((short) 1);
            style3.setBorderLeft((short) 1);
            style3.setBorderTop((short) 1);
            style3.setBorderRight((short) 1);

            style3.setDataFormat(workbook.createDataFormat()
                            .getFormat("#,##0.00"));

            asignarEncabezado(sheet);
            String datos = ejbContabilidadSiete.generarArchivoVerde(compania,
                            anio, mesInicial, mesFinal);

            String[] fila = datos.split(SysmanConstantes.SEPARADOR_REG);

            sheet.shiftRows(13, sheet.getLastRowNum(),
                            fila.length - 1);
            boolean estado;
            List<Integer> totales = new ArrayList<>();
            List<Integer> subTotales = new ArrayList<>();
            List<Integer> subTotales2 = new ArrayList<>();
            for (int i = 0; i < fila.length; i++) {
                String[] columna = fila[i]
                                .split(SysmanConstantes.SEPARADOR_COL);
                Row rt = sheet.createRow(i + 13);
                estado = true;
                for (int j = 0; j < columna.length; j++) {
                    Cell cell = rt.createCell(j + 2);
                    if (validarNumero(columna[j])) {
                        cell.setCellValue(Double.parseDouble(columna[j]));
                    }
                    else {
                        cell.setCellValue(columna[j]);
                    }

                    if (j == 0 && columna[j].length() > 4) {
                        estado = false;
                    }
                    if (j == 0 && columna[j].length() == 1) {
                        totales.add(cell.getRowIndex());
                        switch (columna[j]) {
                        case "1":
                        case "5":
                        case "6":
                            subTotales.add(cell.getRowIndex());
                            break;
                        case "2":
                        case "3":
                        case "4":
                            subTotales2.add(cell.getRowIndex());
                            break;
                        default:
                            break;
                        }
                    }
                    if (j >= 2) {
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(style3);
                    }
                    else {
                        if (estado) {
                            cell.setCellStyle(style2);
                        }
                        else {
                            cell.setCellStyle(style);
                        }
                    }

                }

            }
            generarTotales(totales, sheet, style3);
            generarSubtotales(sheet, subTotales, style3, 27);
            generarSubtotales(sheet, subTotales2, style3, 21);
            asignarPiePagina(sheet);

            FormulaEvaluator evaluator = workbook.getCreationHelper()
                            .createFormulaEvaluator();
            evaluator.evaluateAll();

            workbook.write(out);

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(new ByteArrayInputStream(
                                            out.toByteArray()),
                                            "PlantillaVerde.xls");
            workbook.close();

        }
        catch (IOException | JRException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void asignarPiePagina(Sheet sheet) {
        Row rt = sheet.getRow(sheet.getLastRowNum() - 11);
        Cell cell = rt.getCell(5);
        cell.setCellValue(representante);

        rt = sheet.getRow(sheet.getLastRowNum() - 7);
        cell = rt.getCell(5);
        cell.setCellValue(tesorero);

        rt = sheet.getRow(sheet.getLastRowNum() - 2);
        cell = rt.getCell(3);
        cell.setCellValue(contador);

        cell = rt.getCell(8);
        cell.setCellValue(revisor);

        rt = sheet.getRow(sheet.getLastRowNum() - 1);
        cell = rt.getCell(3);
        cell.setCellValue(tarjetaContador);

        cell = rt.getCell(8);
        cell.setCellValue(tarjetaRevisor);
    }

    public boolean validarNumero(String cadena) {
        try {
            Double.parseDouble(cadena);
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    public void generarSubtotales(Sheet sheet, List<Integer> lista,
        CellStyle style, int fila) {
        Cell celda;
        for (int i = 0; i < lista.size(); i++) {
            int aux = 5;
            while (aux <= 9) {
                celda = sheet.getRow(sheet.getLastRowNum() - fila + i)
                                .createCell(aux);
                StringBuilder celdaIni = new StringBuilder("");
                CellReference cellRefIni = new CellReference(lista.get(i),
                                aux - 1);
                celdaIni.append(cellRefIni.formatAsString() + "-");
                cellRefIni = new CellReference(lista.get(i),
                                aux);
                celdaIni.append(cellRefIni.formatAsString());
                celda.setCellFormula(celdaIni.toString());
                celda.setCellStyle(style);
                aux = aux + 4;
            }

        }

    }

    public void generarTotales(List<Integer> lista, Sheet sheet,
        CellStyle style) {

        for (int i = 4; i < 10; i++) {
            Cell celda = sheet.getRow(sheet.getLastRowNum() - 31).createCell(i);
            celda.setCellType(Cell.CELL_TYPE_FORMULA);
            StringBuilder celdaIni = new StringBuilder("");
            for (int j = 0; j < lista.size(); j++) {
                if (sheet.getRow(lista.get(j)).getCell(i,
                                Row.RETURN_BLANK_AS_NULL) != null) {
                    CellReference cellRefIni = new CellReference(lista.get(j),
                                    i);
                    celdaIni.append(cellRefIni.formatAsString() + ",");
                }

            }

            celda.setCellFormula("SUM("
                + celdaIni.toString().substring(0, celdaIni.length() - 1)
                + ")");
            celda.setCellStyle(style);
        }

    }

    public void asignarEncabezado(Sheet sheet) {
        Row rt = sheet.getRow(4);
        Cell cell = rt.getCell(4);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getNombre());

        cell = rt.getCell(8);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getNit());

        rt = sheet.getRow(5);
        cell = rt.getCell(4);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getTelefono());

        cell = rt.getCell(6);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getFax());

        cell = rt.getCell(8);
        cell.setCellValue("5".equals(periodo) ? "-" : periodo);

        rt = sheet.getRow(6);
        cell = rt.getCell(4);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getDireccion());

        cell = rt.getCell(8);
        cell.setCellValue(obtenerPeriodo());

        rt = sheet.getRow(12);
        cell = rt.getCell(4);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getCodigo());

        cell = rt.getCell(5);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getDireccion());

        cell = rt.getCell(7);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getTelefono());

        cell = rt.getCell(8);
        cell.setCellValue("5".equals(periodo) ? "-" : periodo);

        cell = rt.getCell(9);
        cell.setCellValue(obtenerPeriodo());

        rt = sheet.getRow(10);
        cell = rt.getCell(4);
        cell.setCellValue(SessionUtil.getCompaniaIngreso().getNombre());

    }

    public String obtenerPeriodo() {
        String cadena = "";
        try {
            switch (periodo) {
            case "1":
                cadena = "Enero-Febrero-Marzo";
                break;
            case "2":
                cadena = "Abril-Mayo-Junio";
                break;
            case "3":
                cadena = "Julio-Agosto-Septiembre";
                break;
            case "4":
                cadena = "Octubre-Noviembre-Diciembre";
                break;
            case "5":
                cadena = ejbSysmanUtil.mostrarNombreDeMes(mesInicial);
                break;
            default:
                break;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return cadena;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarPeriodo() {
        switch (periodo) {
        case "1":
            mesInicial = 1;
            cargarListaMesFinal();
            mesFinal = 3;
            bloqueadoMes = true;
            break;
        case "2":
            mesInicial = 3;
            cargarListaMesFinal();
            mesFinal = 6;
            bloqueadoMes = true;
            break;
        case "3":
            mesInicial = 7;
            cargarListaMesFinal();
            mesFinal = 9;
            bloqueadoMes = true;
            break;
        case "4":
            mesInicial = 10;
            cargarListaMesFinal();
            mesFinal = 12;
            bloqueadoMes = true;
            break;
        case "5":
            bloqueadoMes = false;
            mesFinal = mesInicial;
            break;

        default:
            break;
        }
    }

    public void cambiarAnoTrabajo() {
        cargarListaMesInicial();
        cargarListaMesFinal();
    }

    public void cambiarMesInicial() {
        cargarListaMesFinal();
        if ("5".equals(periodo)) {
            mesFinal = mesInicial;
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public int getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public int getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable representante
     * 
     * @return representante
     */
    public String getRepresentante() {
        return representante;
    }

    /**
     * Asigna la variable representante
     * 
     * @param representante
     * Variable a asignar en representante
     */
    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    /**
     * Retorna la variable tesorero
     * 
     * @return tesorero
     */
    public String getTesorero() {
        return tesorero;
    }

    /**
     * Asigna la variable tesorero
     * 
     * @param tesorero
     * Variable a asignar en tesorero
     */
    public void setTesorero(String tesorero) {
        this.tesorero = tesorero;
    }

    /**
     * Retorna la variable contador
     * 
     * @return contador
     */
    public String getContador() {
        return contador;
    }

    /**
     * Asigna la variable contador
     * 
     * @param contador
     * Variable a asignar en contador
     */
    public void setContador(String contador) {
        this.contador = contador;
    }

    /**
     * Retorna la variable revisor
     * 
     * @return revisor
     */
    public String getRevisor() {
        return revisor;
    }

    /**
     * Asigna la variable revisor
     * 
     * @param revisor
     * Variable a asignar en revisor
     */
    public void setRevisor(String revisor) {
        this.revisor = revisor;
    }

    public String getTarjetaContador() {
        return tarjetaContador;
    }

    public void setTarjetaContador(String tarjetaContador) {
        this.tarjetaContador = tarjetaContador;
    }

    public String getTarjetaRevisor() {
        return tarjetaRevisor;
    }

    public void setTarjetaRevisor(String tarjetaRevisor) {
        this.tarjetaRevisor = tarjetaRevisor;
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

    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public boolean isBloqueadoMes() {
        return bloqueadoMes;
    }

    public void setBloqueadoMes(boolean bloqueadoMes) {
        this.bloqueadoMes = bloqueadoMes;
    }

    public ContenedorArchivo getContArchivoPlantilla() {
        return contArchivoPlantilla;
    }

    public void setContArchivoPlantilla(
        ContenedorArchivo contArchivoPlantilla) {
        this.contArchivoPlantilla = contArchivoPlantilla;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
