package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.ejb.EjbNominaTresRemote;
import com.sysman.nomina.ejb.impl.EjbNominaCero;
import com.sysman.nomina.enums.CalculoRetencionUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 24/07/2015
 *
 * @version 2, 24/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * @version 3, 24/01/2017
 * @author mzanguna, Se adicionan codigo de la ultim versión
 */

@ManagedBean
@ViewScoped
public class CalculoRetencionControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String anio;
    private final String moduloNomina;
    private static final String STRCOMPANIA = "compania";
    private List<Registro> listaCbofecha1;
    private List<Registro> listaCboFecha2;
    private Date fechaInicial;
    private Date fechaFinal;
    private String semestre = " ";
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoSeleccionarArchivo;
    private ContenedorArchivo contArchivoAsignar;
    private UploadedFile rutaAsignar;
    private UploadedFile archivoCargarefAsignar;
    private boolean ckPromedio = false;
    private boolean ckPromediocn309 = false;
    private boolean ckPorcenRtnc    = false;
    private boolean ckAnioDesde     = false;
    private boolean ckAnioHasta     = false;
    private boolean ckPromedioTmp   = false;



	/**
     *
     * Atributo para mostrar el dialogo de confirmación DesicionParar
     *
     */
    private boolean muestraMensajeCancelarPrc = false;

    private final String mensajeInterrumpidaCons;
    private final String parteFechaCons;
    private final String parteFechaDosCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaCero ejbNominaCero;

    @EJB
    private EjbNominaTresRemote ejbNominaTres;

    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;

    public CalculoRetencionControlador() {

        compania = SessionUtil.getCompania();
        anio = (String) SessionUtil.getSessionVar("anioNomina");
        moduloNomina = SessionUtil.getModulo();
        mensajeInterrumpidaCons = "MSM_TRANS_INTERRUMPIDA";
        parteFechaCons = "01/01/";
        parteFechaDosCons = "31/12/";

        try {
            numFormulario = GeneralCodigoFormaEnum.CALCULO_RETENCION_CONTROLADOR
                            .getCodigo();
            registro = new Registro(new HashMap<String, Object>());
            contArchivoSeleccionarArchivo = new ContenedorArchivo();
            contArchivoAsignar = new ContenedorArchivo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(CalculoRetencionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        // Metodo heredado de la clase beanBase
    }

    public void cambiarSemestre() {

        String mes = (String) SessionUtil.getSessionVar("mesNomina");
        int nMes = Integer.parseInt(mes);
        ckPromedioTmp =  false;
        try {
            if ("1".equals(semestre)) {
                ckAnioDesde = false;
                ckAnioHasta = true;
                if (nMes >= 8) {

                    fechaInicial = SysmanFunciones.convertirAFecha(
                                    "01/12/" + (Integer.parseInt(anio) - 1));

                    fechaFinal = SysmanFunciones.convertirAFecha(
                                    "30/11/" + (Integer.parseInt(anio)));

                }
                if (nMes <= 6) {

                    fechaInicial = SysmanFunciones.convertirAFecha(
                                    "01/12/" + (Integer.parseInt(anio) - 2));
                    fechaFinal = SysmanFunciones.convertirAFecha(
                                    "30/11/" + (Integer.parseInt(anio) - 1));

                }
            }
            if ("2".equals(semestre) && (nMes >= 1)) {
                ckAnioDesde = true;
                ckAnioHasta = false;
                fechaInicial = SysmanFunciones.convertirAFecha(
                                "01/06/" + (Integer.parseInt(anio) - 1));
                fechaFinal = SysmanFunciones.convertirAFecha(
                                "31/05/" + (Integer.parseInt(anio)));

            }
        }
        catch (NumberFormatException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    /**
     * Metodo ejecutado al cambiar el control ckAnioDesde
     *
     */
    public void cambiarckAnioDesde() {
        ckAnioHasta = false;
        ckPromedioTmp =  false;
    }

    /**
     * Metodo ejecutado al cambiar el control ckAnioHasta
     *
     */
    public void cambiarckAnioHasta() {
        ckAnioDesde = false;
        ckPromedioTmp =  false;
    }
    
    public void cambiarCkPromedioTmp() {
        ckAnioHasta = false;
        ckAnioDesde = false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DesicionParar en la vista
     *
     * Dialogo para cancelar el proceso de cálculo
     *
     */
    public void aceptarDesicionParar() {
        muestraMensajeCancelarPrc = false;
        generarCalculo();
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DesicionParar en la vista
     *
     * Dialogo para cancelar el proceso de cálculo
     *
     */
    public void cancelarDesicionParar() {
        muestraMensajeCancelarPrc = false;
    }

    private boolean validaciones() {
        boolean rta = true;
        if (semestre == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2511"));
            rta = false;
        }
        if (contArchivoSeleccionarArchivo.getArchivo() == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            rta = false;
        }

        return rta;
    }

    public void oprimirCmdCalcular() {
        archivoDescarga = null;
        if (!ckPromediocn309) {
            muestraMensajeCancelarPrc = true;
        }
        else {
            generarCalculo();
        }
    }

    public void generarCalculo() {
        archivoDescarga = null;
        if (!validaciones()) {
            return;
        }

        try {
            ejbNominaTres.cargarParCalCret(compania,
                            SessionUtil.getUser().getCodigo());

            String rutaArchivo = contArchivoSeleccionarArchivo.getArchivo()
                            .getPath();
            FileInputStream file = new FileInputStream(new File(rutaArchivo));
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            FormulaEvaluator evaluator = workbook.getCreationHelper()
                            .createFormulaEvaluator();
            file.close();

            CreationHelper createHelper = workbook.getCreationHelper();

            HSSFSheet sheet = workbook.getSheetAt(0);

            Row row = sheet.getRow(2);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(
                            IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            cellStyle.setDataFormat(createHelper.createDataFormat()
                            .getFormat("dd/MM/yyyy"));

            Cell cellDate = row.createCell(0);
            cellDate.setCellValue(new Date());
            cellDate.setCellStyle(cellStyle);
            row.setRowNum(2);
            Cell nombreCell = row.createCell(1);
            nombreCell.setCellValue(
                            SessionUtil.getCompaniaIngreso().getNombre());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaIni = sdf.format(fechaInicial);
            String fechaFin = sdf.format(fechaFinal);
            String periodo = (String) SessionUtil
                            .getSessionVar("periodoNomina");
            int anioHastaDedePro;
            if(ckAnioHasta ==true ) {
            	anioHastaDedePro =  -1;            	
            }else if (ckAnioDesde == true) {
            	anioHastaDedePro =  0;
            }else {//ckPromedioTmp
            	anioHastaDedePro =  -2;
            }
            	
            if ("2".equals(semestre)) {
            	Registro conregistro;
            	   Map<String, Object> param = new TreeMap<>();
                   param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                try {
                	conregistro = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		CalculoRetencionUrlEnum.URL210168
                                                                        .getValue())
                                        .getUrl(), param));
                  
                  
                  boolean valor = Boolean.parseBoolean(SysmanFunciones.toString(conregistro.getCampos().get("VALOR")));

                    if (!valor) {
                    	JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4489"));
                        return;
                    }

                } catch (SystemException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
            
            String datos = ejbNominaTres.calcularRtfParUno(compania,
                            Integer.parseInt(anio), Integer.parseInt(periodo),
                            fechaIni, fechaFin, ckPromediocn309, anioHastaDedePro,
                            SessionUtil.getUser().getCodigo());
            String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
            String[] colum;

            if ((registro.length == 0) || datos.isEmpty()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3346"));
                workbook.close();
                return;
            }

            DataFormatter dataFormatter = new DataFormatter();
            FormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator(
                            workbook);

            for (int i = 0; i < registro.length; i++) {
                Cell nCell;
                colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
                int fila = Integer.parseInt(
                                colum[0]);
                int columna = Integer.parseInt(colum[1]);
                row = sheet.getRow(fila);
                String valor = colum[2];

                nCell = row.getCell(columna) == null ? row.createCell(columna)
                    : row.getCell(columna);

                if (isNumeric2(valor)) {
                    double valorNum = Double.parseDouble(valor);
                    nCell.setCellValue(valorNum);
                    nCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                }
                else {
                    nCell.setCellValue(valor);
                }
                if ((i < registro.length)
                    && (Integer.parseInt(colum[1]) == 28)) {
                    Cell c;

                    c = row.getCell(27);
                    double promedioX8 = (double) handleCell(c.getCellType(), c,
                                    evaluator);

                    c = row.getCell(21);
                    double promediox22 = (double) handleCell(c.getCellType(), c,
                                    evaluator);

                    c = row.getCell(0);
                    String documento = dataFormatter.formatCellValue(c,
                                    formulaEvaluator);
                    documento = documento.replace(".", "");

                    BigDecimal imeses = BigDecimal.valueOf(
                                    row.getCell(28).getNumericCellValue());

                    String datosPar = ejbNominaTres.calcularRtfParDos(compania,
                                    documento, BigDecimal.valueOf(promedioX8),
                                    fechaInicial,
                                    fechaFinal, imeses,
                                    BigDecimal.valueOf(promediox22),
                                    ckPorcenRtnc,
                                    SessionUtil.getUser().getCodigo());
                    String[] registroPar = datosPar
                                    .split(SysmanConstantes.SEPARADOR_REG);
                    calcularPartDos(registroPar, row, evaluator);

                }
                
                Cell dateCelli = row.createCell(43);  // JM 612 07/01/2025
                dateCelli.setCellValue(fechaInicial); // JM 612 07/01/2025
                Cell dateCellf = row.createCell(44);  // JM 612 07/01/2025
                dateCellf.setCellValue(fechaFinal);   // JM 612 07/01/2025
            }

            generarPromedios(workbook, fechaIni, fechaFin);

            evaluator.evaluateAll();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();

            String nombreReporte;

            if ("1".equals(semestre)) {
                nombreReporte = SysmanFunciones.concatenar(
                                "RETEFUENTE(CALCULOS Y PORCENTAJES)R2_",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial,
                                                "ddMMyyyy"),
                                "_1erS.xls");
            }
            else {
                nombreReporte = SysmanFunciones.concatenar(
                                "RETEFUENTE(CALCULOS Y PORCENTAJES)R2_",
                                SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial,
                                                "ddMMyyyy"),
                                "_2doS.xls");
            }

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            nombreReporte);

        }
        catch (IOException | JRException
                        | ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generarPromedios(HSSFWorkbook workbook, String fechaIni,
        String fechaFin) {
        if (ckPromedio) {
            try {
                HSSFSheet sheetProm = workbook.getSheet("PROMEDIOS");
                Row rowProm;

                String datosPromedio;

                datosPromedio = ejbNominaTres.calcularRtfParTres(compania,
                                Integer.parseInt(anio), fechaIni, fechaFin,
                                SessionUtil.getUser().getCodigo());

                String[] registroProm = datosPromedio
                                .split(SysmanConstantes.SEPARADOR_REG);
                String[] columProm;
                for (int i = 0; i < registroProm.length; i++) {
                    Cell nCellProm;
                    columProm = registroProm[i]
                                    .split(SysmanConstantes.SEPARADOR_COL);
                    int filaProm = Integer.parseInt(
                                    columProm[0]);
                    int columnaProm = Integer.parseInt(columProm[1]);

                    rowProm = sheetProm.getRow(filaProm) == null
                        ? sheetProm.createRow(filaProm)
                        : sheetProm.getRow(filaProm);

                    String valor = columProm[2];

                    nCellProm = rowProm.getCell(columnaProm) == null
                        ? rowProm.createCell(columnaProm)
                        : rowProm.getCell(columnaProm);

                    if (isNumeric2(valor)) {
                        double valorNum = Double.parseDouble(valor);
                        nCellProm.setCellValue(valorNum);
                        nCellProm.setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    else {
                        nCellProm.setCellValue(valor);
                    }
                }
            }
            catch (NumberFormatException | SystemException e) {
                Logger.getLogger(CalculoRetencionControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    private static Object handleCell(int type, Cell cell,
        FormulaEvaluator evaluator) {
        Object rta = null;
        if (type == HSSFCell.CELL_TYPE_STRING) {
            rta = cell.getStringCellValue();
        }
        else if (type == HSSFCell.CELL_TYPE_NUMERIC) {
            rta = cell.getNumericCellValue();
        }
        else if (type == HSSFCell.CELL_TYPE_BOOLEAN) {
            rta = cell.getBooleanCellValue();

        }
        else if (type == HSSFCell.CELL_TYPE_FORMULA) {
            // Re-run based on the formula type
            evaluator.evaluateFormulaCell(cell);
            rta = handleCell(cell.getCachedFormulaResultType(), cell,
                            evaluator);
        }

        return rta;
    }

    private void calcularPartDos(String[] registroPar, Row row,
        FormulaEvaluator evaluator) {
        String[] columPar;
        double columna35 = 0;
        for (int k = 0; k < registroPar.length; k++) {
            columPar = registroPar[k]
                            .split(SysmanConstantes.SEPARADOR_COL);
            int columna2 = Integer.parseInt(columPar[1]);
            Cell nCell2 = row.createCell(columna2);
            if (columPar.length > 2) {
                String valor2 = columPar[2];
                if (columna2 != 100) {
                    if (isNumeric2(valor2)) {
                        double valorNum = Double.parseDouble(valor2);
                        nCell2.setCellValue(valorNum);

                    }
                    else {
                        nCell2.setCellValue(valor2);
                    }

                    // MZ La columna 36 se toma en base a
                    // Round(objexcel.cells(M, 35) * 100, 2)
                    if (k == (registroPar.length - 2)) {
                        Cell c;
                        c = row.getCell(37);
                        columna35 = (double) handleCell(c.getCellType(), c,
                                        evaluator);

                        columna35 = SysmanFunciones.redondear(columna35 * 100,
                                        2);

                        // Asigna el valor a la columna 36
                        nCell2 = row.createCell(39);
                        nCell2.setCellValue(columna35);

                    }
                }
//MOD JM CC 1971 para que tambien incluya los que tienene %0
                if (ckPorcenRtnc && (columna2 == 100) && (columna35 >= 0)) {
                    try {
                        ejbNominaCero.insertarNovedad(compania, 0, 0, 0, 0,
                                        Integer.parseInt(valor2), 303,
                                        BigDecimal.valueOf(columna35),
                                        "",
                                        SessionUtil.getUser().getCodigo());
                    }
                    catch (NumberFormatException | SystemException e) {
                        Logger.getLogger(CalculoRetencionControlador.class
                                        .getName()).log(Level.SEVERE, null, e);
                        JsfUtil.agregarMensajeError(e.getMessage());

                    }
                }

            }
        }
    }

    private boolean isNumeric2(String cadena) {
        return NumberUtils.isNumber(cadena);
    }

    public void oprimirREVISARCONCEPTOS() {
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2512"));
        try {
            ejbNominaSiete.revisarConceptos(compania,
                            SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2515"));
    }

    public void ejecutarCambiarFechas304() {
        // <CODIGO_DESARROLLADO>
        try {
            fechaInicial = SysmanFunciones.convertirAFecha(
                            parteFechaCons + (Integer.parseInt(anio) - 1));
            fechaFinal = SysmanFunciones.convertirAFecha(
                            parteFechaDosCons + (Integer.parseInt(anio) - 1));
        }
        catch (ParseException ex) {
            Logger.getLogger(CalculoRetencionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton RevisarPorcentajes en la
     * vista
     *
     *
     */
    public void oprimirRevisarPorcentajes() {
        archivoDescarga = null;
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(STRCOMPANIA, compania);
            String strSql = Reporteador.resuelveConsulta(
                            "800130RevisarPorcentajes",
                            Integer.parseInt(moduloNomina), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            Logger.getLogger(CalculoRetencionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton ConsultaPromedios en la
     * vista
     *
     */
    public void oprimirConsultaPromedios() {
        archivoDescarga = null;
        try {
            boolean nominaMensual = "SI"
                            .equalsIgnoreCase((String) SysmanFunciones
                                            .nvl(ejbSysmanUtil
                                                            .consultarParametro(
                                                                            compania,
                                                                            "NOMINA MENSUAL",
                                                                            moduloNomina,
                                                                            new Date(),
                                                                            false),
                                                            "NO"));

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(STRCOMPANIA, compania);
            reemplazar.put("ano1", SysmanFunciones.ano(fechaInicial));
            reemplazar.put("ano2", SysmanFunciones.ano(fechaFinal));

            reemplazar.put("mes1", SysmanFunciones.mes(fechaInicial));
            reemplazar.put("mes2", SysmanFunciones.mes(fechaFinal));

            reemplazar.put("parNomina", nominaMensual ? 1 : 0);

            String strSql = Reporteador.resuelveConsulta(
                            "800129PromediosDeducibles",
                            Integer.parseInt(moduloNomina), reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
            Logger.getLogger(CalculoRetencionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void renderizar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirdedsaludempleado() {
        // <CODIGO_DESARROLLADO>
        String mes = (String) SessionUtil.getSessionVar("mesNomina");
        int nMes = Integer.parseInt(mes);
        if (nMes >= 1) {
            try {
                fechaInicial = SysmanFunciones.convertirAFecha(
                                parteFechaCons + (Integer.parseInt(anio) - 1));
                fechaFinal = SysmanFunciones.convertirAFecha(
                                parteFechaDosCons
                                    + (Integer.parseInt(anio) - 1));
                int ano = SysmanFunciones.ano(fechaFinal);
                Map<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(STRCOMPANIA, compania);
                reemplazar.put("anio", String.valueOf(ano));
                String strSql = Reporteador.resuelveConsulta(
                                "800036dedsaludempleado",
                                Integer.parseInt(moduloNomina), reemplazar);
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL97);
            }
            catch (IOException | DRException | JRException
                            | SQLException | ParseException
                            | SysmanException ex) {
                Logger.getLogger(CalculoRetencionControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(mensajeInterrumpidaCons)
                                    + ex.getMessage());
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarArchivoAsignarPorcentaje(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getFile() != null) {
            rutaAsignar = event.getFile();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2518")
                + event.getFile().getFileName());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAsignar() {
        // <CODIGO_DESARROLLADO>
        if (validarArchivoGenerado()) {
            String rutaArchivo = contArchivoAsignar.getArchivo()
                            .getPath();

            try (FileInputStream file = new FileInputStream(
                            new File(rutaArchivo))) {
                Workbook workbook = new HSSFWorkbook(file);
                Sheet sheet = workbook.getSheetAt(0);
                if (!"CALCULOS".equals(sheet.getSheetName())) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB2517"));
                    rutaAsignar = null;
                    workbook.close();
                    return;
                }

                int apuntador = 9;
                Row row;
                Cell cell;
                Cell cell1;
                Cell cell2;
                DataFormatter dataFormatter = new DataFormatter();
                FormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator(
                                (HSSFWorkbook) workbook);
                FormulaEvaluator evaluator = workbook.getCreationHelper()
                                .createFormulaEvaluator();
                boolean estado = true;
                while (estado) {
                    row = sheet.getRow(apuntador);
                    if ((row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK)
                        || (row.getCell(1)
                                        .getCellType() == Cell.CELL_TYPE_BLANK)
                        || (row.getCell(33)
                                        .getCellType() == Cell.CELL_TYPE_BLANK)
                        || (row.getCell(28)
                                        .getCellType() == Cell.CELL_TYPE_BLANK)) {

                        estado = false;
                    }
                    else {
                        cell = row.getCell(39);
                        cell1 = row.getCell(34);
                        cell2 = row.getCell(0);
                        String documento = dataFormatter.formatCellValue(cell2,
                                        formulaEvaluator);
                        String ingreso = cell.getStringCellValue();
                        String retPorc = validarCellUno(cell1, evaluator);

                        ejbNominaTres.calcRetencion(compania, fechaFinal,
                                        SysmanFunciones
                                                        .convertirAFecha(
                                                                        ingreso),
                                        BigDecimal.valueOf(
                                                        Double.parseDouble(
                                                                        retPorc)
                                                            * 100),
                                        documento,
                                        SessionUtil.getUser().getCodigo());
                        apuntador++;
                    }
                }
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2516"));
            }
            catch (IOException | ParseException | NumberFormatException
                            | SystemException ex) {
                Logger.getLogger(CalculoRetencionControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(mensajeInterrumpidaCons)
                                    + ex.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarArchivoGenerado() {
        if (contArchivoAsignar.getArchivo() == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            return false;
        }
        return true;
    }

    public String validarCellUno(Cell cell1, FormulaEvaluator evaluator) {
        String retPorc = null;
        if (cell1 != null) {
            switch (evaluator.evaluateInCell(cell1).getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                retPorc = String.valueOf(cell1.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                retPorc = String.valueOf(cell1.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                retPorc = String.valueOf(cell1.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_ERROR:
                retPorc = String.valueOf(cell1.getErrorCellValue());
                break;
            default:
                retPorc = "";
                break;
            }

        }

        return retPorc;

    }

    public void oprimirACTUALIZARNOVEDADES304() {
        try {
            fechaInicial = SysmanFunciones.convertirAFecha(
                            parteFechaCons + (Integer.parseInt(anio) - 1));
            fechaFinal = SysmanFunciones.convertirAFecha(
                            parteFechaDosCons + (Integer.parseInt(anio) - 1));

            ejbNominaTres.actualizarNovedad304(compania,
                            Integer.parseInt(anio) - 1,
                            SessionUtil.getUser().getCodigo());

        }
        catch (ParseException | NumberFormatException | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeInterrumpidaCons)
                                + ex.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2516"));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    public UploadedFile getRutaAsignar() {
        return rutaAsignar;
    }

    public void setRutaAsignar(UploadedFile rutaAsignar) {
        this.rutaAsignar = rutaAsignar;
    }

    public List<Registro> getListaCbofecha1() {
        return listaCbofecha1;
    }

    public void setListaCbofecha1(List<Registro> listaCbofecha1) {
        this.listaCbofecha1 = listaCbofecha1;
    }

    public List<Registro> getListaCboFecha2() {
        return listaCboFecha2;
    }

    public void setListaCboFecha2(List<Registro> listaCboFecha2) {
        this.listaCboFecha2 = listaCboFecha2;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoSeleccionarArchivo() {
        return contArchivoSeleccionarArchivo;
    }

    public void setContArchivoSeleccionarArchivo(
        ContenedorArchivo contArchivoSeleccionarArchivo) {
        this.contArchivoSeleccionarArchivo = contArchivoSeleccionarArchivo;
    }

    public UploadedFile archivoCargarefAsignar() {
        return archivoCargarefAsignar;
    }

    public void setArchivoCargarefAsignar(UploadedFile archivoCargarefAsignar) {
        this.archivoCargarefAsignar = archivoCargarefAsignar;
    }

    @Override
    public void cargarRegistro() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListasSub() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void iniciarListas() {
        // Metodo heredado de la clase BeanBase

    }

    @Override
    public boolean insertarAntes() {
        return false;

    }

    @Override
    public boolean insertarDespues() {
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        return false;
    }

    public ContenedorArchivo getContArchivoAsignar() {
        return contArchivoAsignar;
    }

    public void setContArchivoAsignar(ContenedorArchivo contArchivoAsignar) {
        this.contArchivoAsignar = contArchivoAsignar;
    }

    public boolean isCkPromedio() {
        return ckPromedio;
    }

    public void setCkPromedio(boolean ckPromedio) {
        this.ckPromedio = ckPromedio;
    }

    public boolean isCkPromediocn309() {
        return ckPromediocn309;
    }

    public void setCkPromediocn309(boolean ckPromediocn309) {
        this.ckPromediocn309 = ckPromediocn309;
    }

    public boolean isCkPorcenRtnc() {
        return ckPorcenRtnc;
    }

    public void setCkPorcenRtnc(boolean ckPorcenRtnc) {
        this.ckPorcenRtnc = ckPorcenRtnc;
    }

    public boolean isMuestraMensajeCancelarPrc() {
        return muestraMensajeCancelarPrc;
    }

    public void setMuestraMensajeCancelarPrc(
        boolean muestraMensajeCancelarPrc) {
        this.muestraMensajeCancelarPrc = muestraMensajeCancelarPrc;
    }

    public boolean isCkAnioDesde() {
        return ckAnioDesde;
    }

    public void setCkAnioDesde(boolean ckAnioDesde) {
        this.ckAnioDesde = ckAnioDesde;
    }

    public boolean isCkAnioHasta() {
        return ckAnioHasta;
    }

    public void setCkAnioHasta(boolean ckAnioHasta) {
        this.ckAnioHasta = ckAnioHasta;
    }

    public boolean isCkPromedioTmp() {
		return ckPromedioTmp;
	}

	public void setCkPromedioTmp(boolean ckPromedioTmp) {
		this.ckPromedioTmp = ckPromedioTmp;
	}
}
