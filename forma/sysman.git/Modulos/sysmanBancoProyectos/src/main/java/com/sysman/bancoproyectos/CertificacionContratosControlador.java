package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.CertificacionContratosControladorEnum;
import com.sysman.bancoproyectos.enums.CertificacionContratosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
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
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 30/09/2015
 * 
 * @author eamaya
 * @version 2.0, 11/09/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs, correcciones SonarLint, cambio de numero de formulario por
 * enum y cambio de textos por Textos en Bean
 * 
 */
@ManagedBean
@ViewScoped

public class CertificacionContratosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private boolean todas;
    private String fuenteIni;
    private String fuenteFin;
    private String vigencia;
    private String fuenteIniCodigo;
    private String fuenteFinCodigo;
    private Date fechaIni;
    private Date fechaFin;
    private Date fechaActual;
    private String nombreRepresentante;
    private String cargoRepresentante;
    private String firmaRepresentante;
    private StreamedContent archivoDescarga;
    private List<Registro> listaVigencia;
    private RegistroDataModelImpl listaFuentesFinanciacion;
    private RegistroDataModelImpl listaFuentesFinanciacionFinal;
    private ContenedorArchivo contArchivoselectorPlantilla;

    @EJB
    private EjbBancoProyectoTresRemote ejbBancoTres;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of CertificacionContratosControlador
     */
    public CertificacionContratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numFormulario = GeneralCodigoFormaEnum.CERTIFICACION_CONTRATOS_CONTROLADOR
                        .getCodigo();
        try {
            contArchivoselectorPlantilla = new ContenedorArchivo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CertificacionContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        todas = true;
        fechaActual = new Date();
        vigencia = String.valueOf(SysmanFunciones.getParteFecha(fechaActual,
                        Calendar.YEAR));
        fechaIni = fechaActual;
        fechaFin = fechaActual;
        cargarListaVigencia();
        cargarListaFuentesFinanciacion();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(), "2000");

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificacionContratosControladorUrlEnum.URL4239
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFuentesFinanciacion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificacionContratosControladorUrlEnum.URL4620
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigencia == null ? "0" : vigencia);

        listaFuentesFinanciacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFuentesFinanciacionFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificacionContratosControladorUrlEnum.URL5749
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigencia);
        param.put(CertificacionContratosControladorEnum.FUENTEINICIAL
                        .getValue(),
                        fuenteIniCodigo);

        listaFuentesFinanciacionFinal = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirExportar() {
        archivoDescarga = null;
        if (verificar()) {
            FileInputStream fileIn;
            int contador = 1;
            int fila = 8;
            int columna = 0;
            double totSaldoDisponible = 0;
            double totSgr = 0;
            double totOtrasFuentes = 0;
            double totVlrTotal = 0;
            double totAnticipo = 0;
            double totPagDifAnt = 0;
            double totTotPago = 0;

            String aux;

            String meses = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                            .getParteFecha(fechaIni, Calendar.MONTH)
                + 1] + " - "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                .getParteFecha(fechaFin, Calendar.MONTH)
                    + 1];

            try (ByteArrayOutputStream fileOut = new ByteArrayOutputStream()) {

                if (todas) {

                    aux = ejbBancoTres.prepararDatosSCV18(compania,
                                    Integer.parseInt(vigencia),
                                    fechaIni, fechaFin, null, null, -1);

                }

                else {

                    aux = ejbBancoTres.prepararDatosSCV18(compania,
                                    Integer.parseInt(vigencia),
                                    fechaIni, fechaFin, fuenteIniCodigo,
                                    fuenteFinCodigo, 0);

                }

                if (SysmanFunciones.validarVariableVacio(aux)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(
                                    "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                    return;
                }

                fileIn = new FileInputStream(
                                contArchivoselectorPlantilla.getArchivo());

                Workbook workbook = new HSSFWorkbook(fileIn);
                Sheet sheet = workbook.getSheet("Formato F-SCV-18");
                fileIn.close();

                // Propiedades letra Detalle Negrita
                Font font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                font.setFontHeightInPoints((short) 14);

                // Propiedades letra Detalle
                Font fonts = workbook.createFont();
                fonts.setFontName("Arial");
                fonts.setBold(false);
                fonts.setFontHeightInPoints((short) 12);

                // Crear Estilo del Encabezado
                CellStyle cellStyleEncabezado = workbook.createCellStyle();
                cellStyleEncabezado.setAlignment(CellStyle.ALIGN_CENTER);
                cellStyleEncabezado.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                cellStyleEncabezado.setFont(font);

                // Crear Estilo del Detalle
                CellStyle cellStyleDetalle = workbook.createCellStyle();
                cellStyleDetalle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalle.setFont(fonts);

                // Crear Estilo del Detalle Monetario
                CellStyle cellStyleDetalleMoneda = workbook.createCellStyle();
                cellStyleDetalleMoneda.setAlignment(CellStyle.ALIGN_RIGHT);
                cellStyleDetalleMoneda
                                .setBorderBottom(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalleMoneda.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalleMoneda
                                .setBorderRight(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalleMoneda.setBorderTop(HSSFCellStyle.BORDER_THIN);
                cellStyleDetalleMoneda.setFont(fonts);

                // Llenar encabezado de la plantilla
                Row rowFecha = sheet.getRow(1);
                Cell cellFecha = rowFecha.createCell(20);
                cellFecha.setCellValue(SysmanFunciones
                                .getParteFecha(fechaActual, Calendar.YEAR)
                    + " - "
                    + (SysmanFunciones.getParteFecha(fechaActual,
                                    Calendar.MONTH)
                        + 1)
                    + " - " + SysmanFunciones.getParteFecha(fechaActual,
                                    Calendar.DAY_OF_MONTH));
                cellFecha.setCellStyle(cellStyleEncabezado);

                Row rowEntidad = sheet.getRow(3);
                Cell cellCompania = rowEntidad.getCell(2);
                cellCompania.setCellValue(
                                SessionUtil.getCompaniaIngreso().getNombre());
                cellCompania.setCellStyle(cellStyleEncabezado);

                Cell cellNit = rowEntidad.getCell(12);
                cellNit.setCellValue(SessionUtil.getCompaniaIngreso().getNit());
                cellNit.setCellStyle(cellStyleEncabezado);

                Cell cellVigencia = rowEntidad.getCell(18);
                cellVigencia.setCellValue(vigencia);
                cellVigencia.setCellStyle(cellStyleEncabezado);

                String[] filaAux = aux
                                .split(SysmanConstantes.SEPARADOR_REG);
                for (int i = 0; i < filaAux.length; i++) {
                    String[] columnaAux = filaAux[i]
                                    .split(SysmanConstantes.SEPARADOR_COL);
                    // crea la fila
                    Row rowDetalle = sheet.getRow(fila);
                    Cell cellConsecutivo = rowDetalle.getCell(columna);
                    cellConsecutivo.setCellValue(contador);
                    cellConsecutivo.setCellStyle(cellStyleDetalle);

                    Cell cellCodigoBPIM = rowDetalle.getCell(columna + 1);
                    cellCodigoBPIM.setCellValue(columnaAux[0]);
                    cellCodigoBPIM.setCellStyle(cellStyleDetalle);

                    Cell cellNombre = rowDetalle.getCell(columna + 2);
                    cellNombre.setCellValue(columnaAux[1]);
                    cellNombre.setCellStyle(cellStyleDetalle);

                    Cell cellCodigoPrecontractual = rowDetalle
                                    .getCell(columna + 3);
                    cellCodigoPrecontractual.setCellValue(columnaAux[2]);
                    cellCodigoPrecontractual.setCellStyle(cellStyleDetalle);

                    Cell cellCodigoRubros = rowDetalle.getCell(columna + 4);
                    cellCodigoRubros.setCellValue(columnaAux[3]);
                    cellCodigoRubros.setCellStyle(cellStyleDetalle);

                    Cell cellNoContrato = rowDetalle.getCell(columna + 5);
                    cellNoContrato.setCellValue(columnaAux[4]);
                    cellNoContrato.setCellStyle(cellStyleDetalle);

                    Cell cellNombreContratista = rowDetalle
                                    .getCell(columna + 6);
                    cellNombreContratista.setCellValue(columnaAux[5]);
                    cellNombreContratista.setCellStyle(cellStyleDetalle);

                    Cell cellCedulaContratista = rowDetalle
                                    .getCell(columna + 7);
                    cellCedulaContratista.setCellValue(columnaAux[6]);
                    cellCedulaContratista.setCellStyle(cellStyleDetalle);

                    Cell cellObjeto = rowDetalle.getCell(columna + 8);
                    cellObjeto.setCellValue(columnaAux[7]);
                    cellObjeto.setCellStyle(cellStyleDetalle);

                    Cell cellRegistroPresupuestal = rowDetalle
                                    .getCell(columna + 9);
                    cellRegistroPresupuestal.setCellValue(columnaAux[8]);
                    cellRegistroPresupuestal.setCellStyle(cellStyleDetalle);

                    Cell cellPlazoEntrega = rowDetalle
                                    .getCell(columna + 10);
                    cellPlazoEntrega.setCellValue(columnaAux[9]);
                    cellPlazoEntrega.setCellStyle(cellStyleDetalle);

                    Cell cellSaldoDisponible = rowDetalle
                                    .getCell(columna + 11);
                    cellSaldoDisponible.setCellValue(columnaAux[10]);
                    totSaldoDisponible = totSaldoDisponible
                        + Double.parseDouble(columnaAux[10]);
                    cellSaldoDisponible
                                    .setCellStyle(cellStyleDetalleMoneda);

                    Cell cellSGR = rowDetalle.getCell(columna + 12);
                    cellSGR.setCellValue(columnaAux[11]);
                    totSgr = totSgr + Double.parseDouble(columnaAux[11]);
                    cellSGR.setCellStyle(cellStyleDetalleMoneda);

                    Cell cellOtrasFuentes = rowDetalle
                                    .getCell(columna + 13);
                    cellOtrasFuentes.setCellValue(columnaAux[12]);
                    totOtrasFuentes = totOtrasFuentes
                        + Double.parseDouble(columnaAux[12]);
                    cellOtrasFuentes.setCellStyle(cellStyleDetalleMoneda);

                    Cell cellValorTotal = rowDetalle.getCell(columna + 14);
                    cellValorTotal.setCellValue(columnaAux[13]);
                    totVlrTotal = totVlrTotal
                        + Double.parseDouble(columnaAux[13]);
                    cellValorTotal.setCellStyle(cellStyleDetalleMoneda);

                    Cell cellValorAnticipo = rowDetalle
                                    .getCell(columna + 15);
                    cellValorAnticipo.setCellValue(columnaAux[14]);
                    totAnticipo = totAnticipo
                        + Double.parseDouble(columnaAux[14]);
                    cellValorAnticipo.setCellStyle(cellStyleDetalleMoneda);

                    Cell cellValorDiferente = rowDetalle
                                    .getCell(columna + 16);
                    cellValorDiferente.setCellValue(columnaAux[15]);
                    totPagDifAnt = totPagDifAnt
                        + Double.parseDouble(columnaAux[15]);
                    cellValorDiferente.setCellStyle(cellStyleDetalleMoneda);

                    Cell cellValorPagos = rowDetalle.getCell(columna + 17);
                    cellValorPagos.setCellValue(columnaAux[16]);
                    totTotPago = totTotPago
                        + Double.parseDouble(columnaAux[16]);
                    cellValorPagos.setCellStyle(cellStyleDetalleMoneda);

                    Cell cellInterventor = rowDetalle.getCell(columna + 18);
                    cellInterventor.setCellValue(columnaAux[17]);
                    cellInterventor.setCellStyle(cellStyleDetalle);

                    Cell cellCedulaInterventor = rowDetalle
                                    .getCell(columna + 19);
                    cellCedulaInterventor.setCellValue(columnaAux[18]);
                    cellCedulaInterventor.setCellStyle(cellStyleDetalle);

                    Cell cellMeses = rowDetalle.getCell(columna + 20);
                    cellMeses.setCellValue(meses);
                    cellMeses.setCellStyle(cellStyleDetalle);

                    fila = fila + 1;
                    contador++;
                }
                columna = 0;

                CellStyle styleTotal = workbook.createCellStyle();
                styleTotal.setFillBackgroundColor(
                                IndexedColors.GREY_25_PERCENT.getIndex());
                styleTotal.setFillForegroundColor(
                                IndexedColors.GREY_25_PERCENT.getIndex());
                styleTotal.setFillPattern(CellStyle.SOLID_FOREGROUND);
                styleTotal.setFont(fonts);

                CellRangeAddress rango = new CellRangeAddress(fila, fila, 1,
                                10);
                sheet.addMergedRegion(rango);

                // carga totales
                Row rowTotales = sheet.getRow(fila);
                Cell cellAux0 = rowTotales.getCell(columna);
                cellAux0.setCellValue("");
                cellAux0.setCellStyle(styleTotal);

                Cell cellTotal = rowTotales.getCell(columna + 1);
                cellTotal.setCellValue(idioma.getString("TG_TOTALES2"));
                cellTotal.setCellStyle(styleTotal);
                CellUtil.setAlignment(cellTotal, workbook,
                                CellStyle.ALIGN_CENTER);

                Cell cellTotalSaldo = rowTotales.getCell(columna + 11);
                cellTotalSaldo.setCellValue(totSaldoDisponible);
                cellTotalSaldo.setCellStyle(styleTotal);

                Cell cellSGR = rowTotales.getCell(columna + 12);
                cellSGR.setCellValue(totSgr);
                cellSGR.setCellStyle(styleTotal);

                Cell cellOtrasFuentes = rowTotales.getCell(columna + 13);
                cellOtrasFuentes.setCellValue(totOtrasFuentes);
                cellOtrasFuentes.setCellStyle(styleTotal);

                Cell cellValorTotal = rowTotales.getCell(columna + 14);
                cellValorTotal.setCellValue(totVlrTotal);
                cellValorTotal.setCellStyle(styleTotal);

                Cell cellvalorAnticipo = rowTotales.getCell(columna + 15);
                cellvalorAnticipo.setCellValue(totAnticipo);
                cellvalorAnticipo.setCellStyle(styleTotal);

                Cell cellValorDiferente = rowTotales.getCell(columna + 16);
                cellValorDiferente.setCellValue(totPagDifAnt);
                cellValorDiferente.setCellStyle(styleTotal);

                Cell cellValorPagos = rowTotales.getCell(columna + 17);
                cellValorPagos.setCellValue(totTotPago);
                cellValorPagos.setCellStyle(styleTotal);

                Cell cellAux1 = rowTotales.getCell(columna + 18);
                cellAux1.setCellStyle(styleTotal);

                Cell cellAux2 = rowTotales.getCell(columna + 19);
                cellAux2.setCellStyle(styleTotal);

                Cell cellAux3 = rowTotales.getCell(columna + 20);
                cellAux3.setCellStyle(styleTotal);

                // pie de pagina
                fila = fila + 3;

                // Crea estilo Representante legal
                CellStyle estiloRepresentante = workbook.createCellStyle();
                estiloRepresentante.setFont(font);

                Row rowPie = sheet.getRow(fila);
                Cell cellRepresentante = rowPie.getCell(2);
                cellRepresentante.setCellValue(
                                idioma.getString("TB_TB3556"));
                cellRepresentante.setCellStyle(estiloRepresentante);
                CellUtil.setAlignment(cellRepresentante, workbook,
                                CellStyle.ALIGN_CENTER);

                // crea rango Representante
                CellRangeAddress rangoRepresentante = new CellRangeAddress(
                                fila,
                                fila, 2, 6);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoRepresentante, sheet, workbook);
                sheet.addMergedRegion(rangoRepresentante);

                Cell cellRDiligenciado = rowPie.getCell(8);
                cellRDiligenciado.setCellValue(
                                idioma.getString("TB_TB3454")
                                                .toUpperCase());
                cellRDiligenciado.setCellStyle(estiloRepresentante);
                CellUtil.setAlignment(cellRDiligenciado, workbook,
                                CellStyle.ALIGN_CENTER);

                // rango digilenciado
                CellRangeAddress rangoDiligenciado = new CellRangeAddress(
                                fila,
                                fila, 8, 12);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoDiligenciado, sheet, workbook);
                sheet.addMergedRegion(rangoDiligenciado);

                Row rowPie1 = sheet.getRow(fila + 1);
                Cell cellNombre = rowPie1.getCell(2);
                cellNombre.setCellValue(idioma.getString("TG_NOMBRE2"));
                cellNombre.setCellStyle(estiloRepresentante);

                // crea rango Representante
                CellRangeAddress rangoNombre = new CellRangeAddress(
                                fila + 1,
                                fila + 1, 3, 5);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoNombre,
                                sheet, workbook);
                sheet.addMergedRegion(rangoNombre);

                Cell cellNombreR = rowPie1.getCell(3);
                cellNombreR.setCellValue(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE REPRESENTANTE LEGAL",
                                                modulo, new Date(),
                                                false));
                CellUtil.setAlignment(cellNombreR, workbook,
                                CellStyle.ALIGN_CENTER);

                Cell cellNombreD = rowPie1.getCell(8);
                cellNombreD.setCellValue(idioma.getString("TG_NOMBRE2"));
                cellNombreD.setCellStyle(estiloRepresentante);

                // crea rango Representante
                CellRangeAddress rangoNombreD = new CellRangeAddress(
                                fila + 1,
                                fila + 1, 9, 11);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoNombreD,
                                sheet, workbook);
                sheet.addMergedRegion(rangoNombreD);

                Cell cellNombreDil = rowPie1.getCell(9);
                cellNombreDil.setCellValue(
                                SessionUtil.getUser().getCodigo());
                CellUtil.setAlignment(cellNombreDil, workbook,
                                CellStyle.ALIGN_CENTER);

                // Cargo
                Row rowpie2 = sheet.getRow(fila + 2);
                Cell cellFirma = rowpie2.getCell(2);
                cellFirma.setCellValue(idioma.getString("TG_CARGO2"));
                cellFirma.setCellStyle(estiloRepresentante);

                // crea rango Representante
                CellRangeAddress rangoCargo = new CellRangeAddress(fila + 2,
                                fila + 2, 3, 5);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoCargo,
                                sheet, workbook);
                sheet.addMergedRegion(rangoCargo);

                Cell cellF = rowpie2.getCell(3);
                cellF.setCellValue(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO REPRESENTANTE LEGAL",
                                                modulo, new Date(),
                                                false));
                CellUtil.setAlignment(cellF, workbook,
                                CellStyle.ALIGN_CENTER);

                Cell cellCargo = rowpie2.getCell(8);
                cellCargo.setCellValue(idioma.getString("TG_CARGO2"));
                cellCargo.setCellStyle(estiloRepresentante);

                // crea rango Representante
                CellRangeAddress rangoCargoD = new CellRangeAddress(
                                fila + 2,
                                fila + 2, 9, 11);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoCargoD,
                                sheet, workbook);
                sheet.addMergedRegion(rangoCargoD);

                Cell cellcargoD = rowpie2.getCell(9);
                cellcargoD.setCellValue(
                                SessionUtil.getUser()
                                                .getTituloProfesional());
                CellUtil.setAlignment(cellcargoD, workbook,
                                CellStyle.ALIGN_CENTER);

                // firma
                Row rowpie3 = sheet.getRow(fila + 4);
                Cell cellFi = rowpie3.getCell(2);
                cellFi.setCellValue(
                                idioma.getString("TB_TB3457")
                                                .toUpperCase());
                cellFi.setCellStyle(estiloRepresentante);

                // crea rango Firma
                CellRangeAddress rangoFirma = new CellRangeAddress(fila + 4,
                                fila + 4, 3, 5);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoFirma,
                                sheet, workbook);
                sheet.addMergedRegion(rangoFirma);

                Cell cellFirma1 = rowpie3.getCell(3);
                cellFirma1.setCellValue(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA REPRESENTANTE LEGAL",
                                                modulo, new Date(),
                                                false));
                CellUtil.setAlignment(cellFirma1, workbook,
                                CellStyle.ALIGN_CENTER);

                Cell cellFirmaD = rowpie3.getCell(8);
                cellFirmaD.setCellValue(
                                idioma.getString("TB_TB3457")
                                                .toUpperCase());
                cellFirmaD.setCellStyle(estiloRepresentante);

                // crea rango Firma
                CellRangeAddress rangoFirmaD = new CellRangeAddress(
                                fila + 4,
                                fila + 4, 9, 11);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                rangoFirmaD,
                                sheet, workbook);
                sheet.addMergedRegion(rangoFirmaD);

                Cell cellFirmD = rowpie3.getCell(9);
                cellFirmD.setCellValue("");
                CellUtil.setAlignment(cellFirmD, workbook,
                                CellStyle.ALIGN_CENTER);

                workbook.write(fileOut);
                fileOut.close();

                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(
                                                fileOut.toByteArray()),
                                "Plantilla Ejecucion de proyectos.xls");

            }
            catch (FileNotFoundException ex) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2079"));
                Logger.getLogger(CertificacionContratosControlador.class
                                .getName()).log(Level.SEVERE, null, ex);
            }
            catch (JRException | IOException ex) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                    + ex.getMessage());
                Logger.getLogger(CertificacionContratosControlador.class
                                .getName()).log(Level.SEVERE, null, ex);

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    public boolean verificar() {
        boolean estado = true;

        if (!validarVacios() || !validarVigencia()) {
            estado = false;
        }
        else if (!todas) {
            if ((fuenteIni == null || "".equals(fuenteIni))
                || (fuenteFin == null || "".equals(fuenteFin))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2290"));
                estado = false;
            }
        }
        else if (contArchivoselectorPlantilla.getArchivo() == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2291"));
            estado = false;
        }
        return estado;
    }

    private boolean validarVacios() {
        if (vigencia == null || "".equals(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2288"));
            return false;
        }
        else if (fechaIni == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2155"));
            return false;
        }
        else if (fechaFin == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB169"));
            return false;
        }
        else if (fechaIni.after(fechaFin)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
            return false;
        }
        return true;
    }

    private boolean validarVigencia() {

        if (Integer.parseInt(vigencia == null ? "0" : vigencia) < 2000) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2292"));
            vigencia = null;
            return false;
        }
        else {
            return true;
        }
    }

    private void actualizarAnios() {
        Calendar calendario = new GregorianCalendar();
        if (fechaIni != null) {
            calendario.set(Integer.parseInt(vigencia),
                            SysmanFunciones.getParteFecha(fechaIni,
                                            Calendar.MONTH),
                            SysmanFunciones.getParteFecha(fechaIni,
                                            Calendar.DAY_OF_MONTH));
            fechaIni = calendario.getTime();
        }
        if (fechaFin != null) {
            calendario.set(Integer.parseInt(vigencia),
                            SysmanFunciones.getParteFecha(fechaFin,
                                            Calendar.MONTH),
                            SysmanFunciones.getParteFecha(fechaFin,
                                            Calendar.DAY_OF_MONTH));
            fechaFin = calendario.getTime();
        }
    }

    public void cambiarVigencia() {
        // <CODIGO_DESARROLLADO>
        if (validarVigencia()) {
            actualizarAnios();
        }

        fuenteIni = null;
        fuenteFinCodigo = null;
        fuenteFin = null;
        fuenteFinCodigo = null;

        cargarListaFuentesFinanciacion();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechainicial() {
        // <CODIGO_DESARROLLADO>
        if (vigencia != null && SysmanFunciones.getParteFecha(fechaIni,
                        Calendar.YEAR) != Integer.parseInt(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2293"));
            fechaIni = null;
            return;
        }
        if (fechaFin != null && fechaIni.after(fechaFin)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
            fechaIni = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechafinal() {
        // <CODIGO_DESARROLLADO>
        if (vigencia != null && SysmanFunciones.getParteFecha(fechaFin,
                        Calendar.YEAR) != Integer.parseInt(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2293"));
            fechaFin = null;
            return;
        }
        if (fechaIni != null && fechaIni.after(fechaFin)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2295"));
            fechaFin = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerificacion16() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFuentesFinanciacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        fuenteIniCodigo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        fuenteFin = null;
        fuenteFinCodigo = null;
        cargarListaFuentesFinanciacionFinal();
    }

    public void seleccionarFilaFuentesFinanciacionFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        fuenteFinCodigo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    public boolean isTodas() {
        return todas;
    }

    public void setTodas(boolean todas) {
        this.todas = todas;
    }

    public String getFuenteIni() {
        return fuenteIni;
    }

    public void setFuenteIni(String fuenteIni) {
        this.fuenteIni = fuenteIni;
    }

    public String getFuenteFin() {
        return fuenteFin;
    }

    public void setFuenteFin(String fuenteFin) {
        this.fuenteFin = fuenteFin;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getNombreRepresentante() {
        return nombreRepresentante;
    }

    public void setNombreRepresentante(String nombreRepresentante) {
        this.nombreRepresentante = nombreRepresentante;
    }

    public String getCargoRepresentante() {
        return cargoRepresentante;
    }

    public void setCargoRepresentante(String cargoRepresentante) {
        this.cargoRepresentante = cargoRepresentante;
    }

    public String getFirmaRepresentante() {
        return firmaRepresentante;
    }

    public void setFirmaRepresentante(String firmaRepresentante) {
        this.firmaRepresentante = firmaRepresentante;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public RegistroDataModelImpl getListaFuentesFinanciacion() {
        return listaFuentesFinanciacion;
    }

    public void setListaFuentesFinanciacion(
        RegistroDataModelImpl listaFuentesFinanciacion) {
        this.listaFuentesFinanciacion = listaFuentesFinanciacion;
    }

    public RegistroDataModelImpl getListaFuentesFinanciacionFinal() {
        return listaFuentesFinanciacionFinal;
    }

    public void setListaFuentesFinanciacionFinal(
        RegistroDataModelImpl listaFuentesFinanciacionFinal) {
        this.listaFuentesFinanciacionFinal = listaFuentesFinanciacionFinal;
    }

    public ContenedorArchivo getContArchivoselectorPlantilla() {
        return contArchivoselectorPlantilla;
    }

    public void setContArchivoselectorPlantilla(
        ContenedorArchivo contArchivoselectorPlantilla) {
        this.contArchivoselectorPlantilla = contArchivoselectorPlantilla;
    }

}