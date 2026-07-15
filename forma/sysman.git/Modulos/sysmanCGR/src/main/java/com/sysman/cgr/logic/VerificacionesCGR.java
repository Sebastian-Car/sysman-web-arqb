package com.sysman.cgr.logic;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que se encarga de generar el archivo de excel para el informe
 * de verificacion de CGR
 *
 * @author jsforero
 * @version 1, 15/03/2017
 * 
 * @author jlramirez
 * @version 2, 27/03/2017, Se agregaron métodos para generar la
 * consulta para el informe de verificacion programacion de gastos y
 * verificacion ejecucion gastos.
 * 
 * @author yrojas
 * @version 3, 27/03/2017, Se agregó método de verificación de
 * ingresos.
 */
public class VerificacionesCGR {
    /**
     * Constante que almacena la cadena "CONCEPTO"
     */
    private final String constConcepto;
    /**
     * Constante que almacena la cadena "COD_OEI"
     */
    private final String constCodOEI;
    /**
     * Constante que almacena la cadena "COD_DEST_ESTT"
     */
    private final String constCodDestEstt;
    /**
     * Constante que almacena la cadena "COD_REC"
     */
    private final String constCodRec;
    /**
     * Constante que almacena la cadena "COD_FIN"
     */
    private final String constCodFin;
    /**
     * Constante que almacena la cadena "VIG_GAST"
     */
    private final String constVigGast;
    /**
     * Abecedario para utilizarlo en el manejo de las columnas en
     * excel,
     */
    private final String abecedario;
    /**
     * Listado de la consulta principal,
     */
    private List<Registro> listaPrincipal;

    /**
     * Listado de la subconsulta
     */
    private List<Registro> listaSecunadria;

    private FormContinuoService service;

    /**
     * Tipo de Entidad desde el que se esta haciendo el proceso.
     */
    private int tipoEntidad;
    /**
     * Log para la generacion de errores
     */
    private Log logger;
    /**
     * Recibe el idioma para manejarlo en las excepciones
     */
    private ResourceBundle idioma;

    /**
     * Variable que se encarga de manejar el archivo
     */
    private StreamedContent archivoDescarga;

    /**
     * Numero de columna desde que inician los valores operables
     */
    private int numCol;

    /**
     * Compania con la que se va a hacer el proceso
     */
    private String compania;

    /**
     * Hashmap que contiene los parametros a remplazar en el resuelve
     * consulta
     */
    private HashMap<String, Object> variables;

    /**
     * Constructor de clase, recibe por parametro:
     * 
     * @param tipoEntidad:Tipo
     * de Entidad desde el que se esta haciendo el proceso.
     * @param idioma:
     * Esta variable se utiliza en la generacion de errores
     * @param logger:
     * Esta variable se utiliza en la generacion de errores
     * @param compania:
     * Esta variable se utiliza para generar las consultas, es la
     * compania sobre la que se esta trabajando
     */
    public VerificacionesCGR(int tipoEntidad,
        ResourceBundle idioma, Log logger,
        String compania) {
        constConcepto = "CONCEPTO";
        constCodOEI = "COD_OEI";
        constCodDestEstt = "COD_DEST_ESTT";
        constCodRec = "COD_REC";
        constCodFin = "COD_FIN";
        constVigGast = "VIG_GAST";
        abecedario = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        this.compania = compania;
        this.tipoEntidad = tipoEntidad;
        this.logger = logger;
        this.idioma = idioma;
        service = FormContinuoService.getInstance();

    }

    /**
     *
     * 
     * @param nombre:
     * Nombre del archivo excel a generar.
     * @param consulta
     * Consulta principal, partiendo de esta consulta, se crean la
     * subconsulta para enviar los detalles al informe excel.
     * @param tipoInforme:Recibe
     * la variable que va a definir la subconsulta a generar.
     * @param campos:
     * Es un vector que contiene los encabezados del informe
     * @param mesInicial:
     * Esta variable se utiliza para generar las consultas, es el mes
     * inicial en la condicion
     * @param mesFinal:
     * Esta variable se utiliza para generar las consultas, es el mes
     * final en la condicion
     * @param ano:
     * Esta variable se utiliza para generar las consultas, es el ano
     * sobre el que se esta trabajando
     *
     * @return Retorna un objeto StreamedContent que es el archivo
     * excel generado
     */
    public StreamedContent envioDetalleRES(String nombre, String consulta,
        String tipoInforme, String[] campos, String mesInicial, String mesFinal,
        String ano) {
        String strSql = consulta;
        variables = new HashMap<>();
        variables.put("mesInicial", mesInicial);
        variables.put("mesFinal", mesFinal);
        variables.put("compania", "'" + compania + "'");
        variables.put("ano", ano);

        listaPrincipal = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        strSql);

        ConectorPool con = new ConectorPool();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Workbook workbook = new HSSFWorkbook()) {

            con.conectar(ConectorPool.ESQUEMA_SYSMAN);

            Sheet sheet = workbook.createSheet("Report");
            sheet.createFreezePane(1, 1, 1, 1);

            sheet.setColumnWidth(0, 10000);
            Font font = workbook.createFont();
            font.setFontName("Calibri");
            font.setFontHeightInPoints((short) 14);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setFont(font);
            DataFormat df = workbook.createDataFormat();
            style.setDataFormat(df.getFormat("#,##0.00"));

            llenarTitulos(sheet, style, campos);
            llenarPrincipal(workbook, sheet, style, listaPrincipal, tipoInforme,
                            campos);

            for (int i = 0; i < (campos.length) - 1; i++) {
                sheet.setColumnWidth(i + 1, 7000);

            }

            workbook.write(out);
            out.close();

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(new ByteArrayInputStream(
                                            out.toByteArray()),
                                            nombre + ".xls");

        }
        catch (JRException | IOException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (NullPointerException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            Logger.getLogger(VerificacionesCGR.class.getName())
                            .log(Level.SEVERE, null, e);
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException ex) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                    + ex.getMessage());
                Logger.getLogger(VerificacionesCGR.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        return archivoDescarga;
    }

    /**
     * 
     * @param sheet:Hoja
     * de word a la que le agrega los titulos
     * @param style:Estilo
     * para la celda, tipo de letra y color
     */
    public void llenarTitulos(Sheet sheet, CellStyle style, String[] campos) {

        Row r = sheet.createRow(0);
        for (int i = 0; i < campos.length; i++) {

            Cell cell1 = r.createCell(i);
            cell1.setCellValue(campos[i]);// -----------------------------------
            cell1.setCellStyle(style);

        }

    }

    /**
     * Metodo que se encarga de insetar las filas pertenecientes a la
     * consulta principal, recibe por parametro:
     * 
     * @param workbook:
     * archivo excel
     * @param sheet:
     * hoja de excel sobre la que se esta escribiendo
     * @param style:
     * estilo aplicado a la celda
     * @param listaPrincipal:
     * Resultados de la consutla principal, teniendo en cuenta esta
     * consulta se realizan las consultas secundarias
     */
    public void llenarPrincipal(Workbook workbook, Sheet sheet, CellStyle style,
        List<Registro> listaPrincipal, String tipoInforme, String[] campos) {
        int fila = 2;// ------------------------------------------
        int filaInicial;// ------------------------------------
        double aux;
        String aux2;
        String strSql;
        String select = creaConsulta(tipoInforme);
        for (int i = 0; i < listaPrincipal.size(); i++) {

            strSql = select + " "
                + creaCondicion(listaPrincipal.get(i), tipoInforme);
            listaSecunadria = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);
            fila = fila
                + llenarSecundaria(sheet, style, listaSecunadria, fila, campos);
            Row r = sheet.createRow(fila - 1);

            for (int j = 0; j < campos.length; j++) {

                Cell cell1 = r.createCell(j);

                if (j >= numCol) {
                    aux = Double.valueOf(listaPrincipal.get(i).getCampos()
                                    .get(campos[j]).toString());
                    cell1.setCellValue(aux);

                }
                else {
                    aux2 = listaPrincipal.get(i).getCampos().get(campos[j])
                                    .toString();
                    cell1.setCellValue(aux2);
                }
                cell1.setCellStyle(style);

            }
            filaInicial = fila - listaSecunadria.size() - 1;
            crearOperaciones(workbook, sheet, style, fila, campos.length,
                            filaInicial);
            fila++;
        }
    }

    /**
     * Metodo que se encarga de insetar las filas pertenecientes a la
     * columna secundaria, recibe por parametro:
     * 
     * @param sheet:
     * hoja de excel sobre la que se esta escribiendo
     * @param style:
     * estilo aplicado a la celda
     * @param listaSecundaria:
     * Lista que contiene los registros de la consulta secundaria
     * @param fila:
     * Es el numero de fila en el que se van a insertar las formulas
     * @return: Retorna un int: que es la cantidad de celdas que
     * utiliza
     */
    public int llenarSecundaria(Sheet sheet, CellStyle style,
        List<Registro> listaSecundaria, int fila, String[] campos) {
        double aux;
        for (int i = 0; i < listaSecundaria.size(); i++) {

            Row r = sheet.createRow(fila + i);// ----------------------------------

            Cell cell = r.createCell(0);
            cell.setCellValue(listaSecundaria.get(i).getCampos().get("ID")
                            .toString());
            cell.setCellStyle(style);
            for (int j = 0; j < (campos.length) - numCol; j++) {

                Cell cell1 = r.createCell(j + numCol);

                aux = Double.valueOf(listaSecundaria.get(i).getCampos()
                                .get(campos[j + numCol]).toString());
                cell1.setCellValue(aux);// -----------------------------------
                cell1.setCellStyle(style);

            }
        }
        return listaSecundaria.size() + 2;
    }

    /**
     * Metodo que crea las formulas de la suma, este recibe por
     * parametro
     * 
     * @param workbook:
     * Archivo Excel
     * @param sheet:
     * hoja de excel sobre la que se esta escribiendo
     * @param style:
     * estilo aplicado a la celda
     * @param fila:
     * Es el numero de fila en el que se van a insertar las formulas
     * @param cantCol:
     * es la cantidad de columnas que tiene el registro principal,
     * partiendo de este se calcula cuales seldas son las que se tiene
     * que sumar
     * @param filaInicial:
     * Numero incial de la consulta para hacer la formula
     */
    public void crearOperaciones(Workbook workbook, Sheet sheet,
        CellStyle style, int fila, int cantCol, int filaInicial) {
        double aux;
        double aux2;

        Font font2 = workbook.createFont();
        font2.setFontName("Calibri");
        font2.setFontHeightInPoints((short) 14);
        font2.setBold(true);
        font2.setColor(Font.COLOR_RED);

        CellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(CellStyle.ALIGN_CENTER);
        style2.setFont(font2);

        FormulaEvaluator evaluator = workbook.getCreationHelper()
                        .createFormulaEvaluator();
        char letra;
        // Fila donde agrega la suma
        Row rsum = sheet.createRow(fila - 2);
        // Fila donde agrega la resta
        Row rres = sheet.createRow(fila);

        Row rowTo = sheet.getRow(fila - 1);
        // Celda para la suma
        Cell cellTSum = rsum.createCell(0);
        cellTSum.setCellValue("TOTAL PRESUPUESTO");
        cellTSum.setCellStyle(style);
        // Celda para el encabezado de suma
        Cell cellTRes = rres.createCell(0);
        cellTRes.setCellValue("DIFERENCIA");
        cellTRes.setCellStyle(style);
        for (int j = 0; j < (cantCol - numCol); j++) {

            Cell cellSum = rsum.createCell(numCol + j);
            Cell cellRes = rres.createCell(numCol + j);
            letra = abecedario.charAt(numCol + j);

            cellSum.setCellType(Cell.CELL_TYPE_FORMULA);
            cellSum.setCellFormula("SUM(" + letra + filaInicial + ":" + letra
                + (fila - 2) + ")");
            cellSum.setCellStyle(style);

            cellRes.setCellType(Cell.CELL_TYPE_FORMULA);
            cellRes.setCellFormula(
                            letra + (fila - 1) + "-" + letra + (fila) + "");

            aux2 = rowTo.getCell(numCol + j).getNumericCellValue();
            aux = Double.parseDouble(
                            evaluator.evaluateInCell(cellSum).toString());

            if ((Double.doubleToRawLongBits(aux2)
                - Double.doubleToRawLongBits(aux)) != 0) {
                cellRes.setCellStyle(style2);
            }
            else {
                cellRes.setCellStyle(style);
            }
        }

    }

    /**
     * Metodo que crea el select de la subConsulta
     * 
     * @return El select y el from de la consulta
     */
    public String creaConsulta(String tipoInforme) {
        String selectConsulta = "";
        switch (tipoInforme) {
        case "PI":
            selectConsulta = creaConsultaPI();
            break;
        case "PG":
            selectConsulta = creaConsultaPG();
            break;
        case "EI":
            selectConsulta = creaConsultaEI();
            break;
        case "EG":
            selectConsulta = creaConsultaEG();
            break;
        case "PET":
            selectConsulta = creaConsultaPET();
            break;
        default:
            break;
        }
        return selectConsulta;
    }

    /**
     * Metodo que crea la condicion de la subconsulta
     * 
     * @param registro:Registro
     * principal para crear las condiciones de la lista secundaria
     * @return Condicione where de la consulta
     */

    public String creaCondicion(Registro registro, String tipoInforme) {
        String condicionConsulta = "";
        switch (tipoInforme) {
        case "PI":
        case "EI":
            condicionConsulta = creaCondicionPIyEI(registro);
            break;
        case "PG":
            condicionConsulta = creaCondicionPG(registro);
            break;
        case "EG":
            condicionConsulta = creaCondicionEG(registro);
            break;
        case "PET":
            condicionConsulta = creaCondicionPET(registro);
            break;
        default:
            break;
        }
        return condicionConsulta;

    }

    /**
     * Metodo que crea el select de la consulta cuando el tipo de
     * informe a generar es PI
     * 
     * @return Retorna Select cuando el tipo de informe es PI
     */
    public String creaConsultaPI() {
        String selectConsulta;
        String tablaP;
        String tablaA;

        tablaP = Reporteador.resuelveConsulta("ResumenPpto_P_I_ID",
                        Integer.valueOf("99"), variables);
        tablaA = Reporteador.resuelveConsulta("ResumenPpto_A_I_ID",
                        Integer.valueOf("99"), variables);

        selectConsulta = " SELECT ResumenPpto_P_I_ID.ID,"
            + " TO_NUMBER(ROUND(ResumenPpto_A_I_ID.Apropiado)) AS PRE_INI_INGR,"
            + " TO_NUMBER(ROUND(Abs(ResumenPpto_A_I_ID.Adicion+ResumenPpto_P_I_ID.Adicion),0)) AS MOD_ADI_INGR,"
            + " TO_NUMBER(ROUND(Abs(ResumenPpto_A_I_ID.Reduccion+ResumenPpto_P_I_ID.Reduccion),0)) AS MOD_RED_INGR,"
            + " 0 AS PRE_DEF_ING"
            + " FROM ( " + tablaP + " )ResumenPpto_P_I_ID"
            + " INNER JOIN ( " + tablaA + " )ResumenPpto_A_I_ID "
            + " ON ResumenPpto_P_I_ID.ID = ResumenPpto_A_I_ID.ID	"
            + " AND ResumenPpto_P_I_ID.RECURSOSCHIP = ResumenPpto_A_I_ID.RECURSOSCHIP "
            + " AND ResumenPpto_P_I_ID.ANO = ResumenPpto_A_I_ID.ANO	"
            + " AND ResumenPpto_P_I_ID.CodigoEquivalente = ResumenPpto_A_I_ID.CodigoEquivalente	"
            + " AND ResumenPpto_P_I_ID.ORIGENESPECIFICOINGRESOS = ResumenPpto_A_I_ID.ORIGENESPECIFICOINGRESOS "
            + " AND ResumenPpto_P_I_ID.DESTINACIONDELOSRECURSOS = ResumenPpto_A_I_ID.DESTINACIONDELOSRECURSOS ";

        return selectConsulta;
    }

    /**
     * Metodo que crea el select de la consulta cuando el tipo de
     * informe a generar es PG
     * 
     * @return Retorna Select cuando el tipo de informe es PG
     */

    public String creaConsultaPG() {
        String selectConsulta;
        String tablaP;
        if (tipoEntidad == 2) {
            selectConsulta = "pg2";

        }
        else {
            tablaP = Reporteador.resuelveConsulta("ResumenPpto_PG_ID",
                            Integer.valueOf("99"), variables);
            selectConsulta = "SELECT ResumenPpto_PG_ID.ID, "
                + " TRUNC(ResumenPpto_PG_ID.Apropiado+0.51) AS APR_INI_DIS_GAST1, "
                + " TRUNC(ResumenPpto_PG_ID.Adicion+0.51)AS MOD_ADI_GAST1, "
                + " TRUNC(ResumenPpto_PG_ID.Reduccion+0.51) AS MOD_RED_GAST1, 0 AS CANCELAC_GAST1,"
                + " TRUNC(ResumenPpto_PG_ID.SUMADETRASLADO_DEBITO+0.51) AS MT_CRE_GAST, "
                + " TRUNC(ResumenPpto_PG_ID.SUMADETRASLADO_CREDITO+0.51) AS MT_CCRE_GAST, "
                + " 0 AS APR_DEF_GAST, "
                + " TRUNC(ResumenPpto_PG_ID.Disponibilidad + 0.51) AS CDP_GAST1, 0 AS REV_CDP_GAST "
                + "	FROM (" + tablaP + ") ResumenPpto_PG_ID";
        }
        return selectConsulta;
    }

    /**
     * Metodo que crea el select de la consulta cuando el tipo de
     * informe a generar es EI
     * 
     * @return Retorna Select cuando el tipo de informe es EI
     */
    public String creaConsultaEI() {
        String selectConsulta;
        String tablaP;
        String tablaA;

        tablaP = Reporteador.resuelveConsulta("ResumenPpto_P_ID",
                        Integer.valueOf("99"), variables);
        tablaA = Reporteador.resuelveConsulta("ResumenPpto_A_ID",
                        Integer.valueOf("99"), variables);
        selectConsulta = "SELECT ResumenPpto_P_I_ID.ID, "
            + " ROUND(ResumenPpto_A_I_ID.Totalingresos+ResumenPpto_P_I_ID.Totalingresos,0) AS EE_RECAUDO_INGR,"
            + " 0 AS EE_DEVOLUC_INGR,"
            + " 0 AS EE_REVREC_INGR,"
            + " 0 AS E_OTRAS_INGR, "
            + " 0 AS REV_OTRAS_INGR,"
            + " 0 AS RECONOC_INGR, "
            + " ROUND(ResumenPpto_A_I_ID.VigenciaAnterior+ResumenPpto_P_I_ID.VigenciaAnterior,0) AS REC_VA_INGR,"
            + " 0 AS REV_RVA_INGR "
            + " FROM (" + tablaP + ") ResumenPpto_P_I_ID "
            + " INNER JOIN  (" + tablaA + ") ResumenPpto_A_I_ID "
            + " ON ResumenPpto_P_I_ID.ID = ResumenPpto_A_I_ID.ID"
            + " AND ResumenPpto_P_I_ID.ANO = ResumenPpto_A_I_ID.ANO"
            + " AND ResumenPpto_P_I_ID.CodigoEquivalente = ResumenPpto_A_I_ID.CodigoEquivalente"
            + " AND ResumenPpto_P_I_ID.RECURSOSCHIP = ResumenPpto_A_I_ID.RECURSOSCHIP"
            + " AND ResumenPpto_P_I_ID.ORIGENESPECIFICOINGRESOS = ResumenPpto_A_I_ID.ORIGENESPECIFICOINGRESOS"
            + " AND ResumenPpto_P_I_ID.DESTINACIONDELOSRECURSOS = ResumenPpto_A_I_ID.DESTINACIONDELOSRECURSOS";

        return selectConsulta;
    }

    /**
     * Metodo que crea el select de la consulta cuando el tipo de
     * informe a generar es EG
     * 
     * @return Retorna Select cuando el tipo de informe es EG
     */
    public String creaConsultaEG() {
        String tablaP;
        String tablaA;
        String selectConsulta;

        if (tipoEntidad == 6) {

            tablaP = Reporteador.resuelveConsulta("ResumenPpto_P_ID",
                            Integer.valueOf("99"), variables);
            tablaA = Reporteador.resuelveConsulta("ResumenPpto_A_ID",
                            Integer.valueOf("99"), variables);

            selectConsulta = "SELECT ResumenPpto_P_ID.ID,"
                + " 0 AS EJE_GCCA_GAST1, "
                + " CASE WHEN NVL(ResumenPpto_P_ID.VigenciaGasto,'')='1'"
                + " THEN TO_NUMBER(round(ResumenPpto_P_ID.RegistrosP+ResumenPpto_A_ID.RegistrosA,0))"
                + " ELSE 0"
                + " END AS EJE_GCSA_GAST,"
                + " 0 AS REV_GC_GAST, "
                + " TO_NUMBER(round(ResumenPpto_P_ID.TotalREO+ResumenPpto_A_ID.TotalREO)) AS EJE_OC_GAST1, "
                + " 0 AS REV_OC_GAST, "
                + " TO_NUMBER(round(ResumenPpto_P_ID.TotalIngresos+ResumenPpto_A_ID.TotalIngresos,0))AS EJE_PAGOS_GAST, "
                + " 0 AS ANU_PAGOS_GAST, "
                + " 0 AS RES_PRE_GAST, "
                + " 0 AS CXP_GAST_ACEP,"
                + " 0 AS OBL_PE_GAST "
                + "  FROM (" + tablaP + ")ResumenPpto_P_ID "
                + " INNER JOIN  (" + tablaA + ") ResumenPpto_A_ID "
                + " ON ResumenPpto_P_ID.ID = ResumenPpto_A_ID.ID"
                + " AND ResumenPpto_P_ID.VIGENCIAGASTO = ResumenPpto_A_ID.VIGENCIAGASTO"
                + " AND ResumenPpto_P_ID.RECURSOSCHIP = ResumenPpto_A_ID.RECURSOSCHIP"
                + " AND ResumenPpto_P_ID.DEPENDENCIASCHIP = ResumenPpto_A_ID.DEPENDENCIASCHIP"
                + " AND ResumenPpto_P_ID.FINALIDADGASTO = ResumenPpto_A_ID.FINALIDADGASTO"
                + " AND ResumenPpto_P_ID.DESTINACIONDELOSRECURSOS = ResumenPpto_A_ID.DESTINACIONDELOSRECURSOS"
                + " AND ResumenPpto_P_ID.ORIGENESPECIFICOINGRESOS = ResumenPpto_A_ID.ORIGENESPECIFICOINGRESOS"
                + " AND ResumenPpto_P_ID.CodigoEquivalente = ResumenPpto_A_ID.CodigoEquivalente"
                + " AND ResumenPpto_P_ID.ANO = ResumenPpto_A_ID.ANO  ";

        }
        else if (tipoEntidad == 1 || tipoEntidad == 4 || tipoEntidad == 5
            || tipoEntidad == 7) {
            tablaP = Reporteador.resuelveConsulta("ResumenPpto_PG_ID",
                            Integer.valueOf("99"), variables);
            tablaA = Reporteador.resuelveConsulta("ResumenPpto_AG_ID",
                            Integer.valueOf("99"), variables);

            selectConsulta = "SELECT ResumenPpto_PG_ID.ID,"
                + " TO_NUMBER(round(ResumenPpto_PG_ID.RegistrosP+ResumenPpto_AG_ID.RegistrosA,0)) AS EJE_GCCA_GAST1,"
                + " 0 AS EJE_GCSA_GAST,"
                + " 0 AS REV_GC_GAST, "
                + " TO_NUMBER(round(ResumenPpto_PG_ID.TotalREO+ResumenPpto_AG_ID.TotalREO,0)) AS EJE_OC_GAST1,"
                + " 0 AS REV_OC_GAST,"
                + " TO_NUMBER(round(ResumenPpto_PG_ID.TotalIngresos+ResumenPpto_AG_ID.TotalIngresos,0)) AS EJE_PAGOS_GAST,"
                + " 0 AS ANU_PAGOS_GAST,"
                + " 0 AS RES_PRE_GAST,"
                + " 0 AS CXP_GAST_ACEP,"
                + " 0 AS OBL_PE_GAST"
                + "  FROM (" + tablaP + ")ResumenPpto_PG_ID "
                + " INNER JOIN (" + tablaA + ") ResumenPpto_AG_ID "
                + " ON ResumenPpto_PG_ID.ID = ResumenPpto_AG_ID.ID "
                + " AND ResumenPpto_PG_ID.ANO = ResumenPpto_A_ID.ANO "
                + " AND ResumenPpto_PG_ID.CodigoEquivalente = ResumenPpto_AG_ID.CodigoEquivalente "
                + " AND ResumenPpto_PG_ID.DESTINACIONDELOSRECURSOS = ResumenPpto_AG_ID.DESTINACIONDELOSRECURSOS "
                + " AND ResumenPpto_PG_ID.ORIGENESPECIFICOINGRESOS = ResumenPpto_AG_ID.ORIGENESPECIFICOINGRESOS "
                + " AND ResumenPpto_PG_ID.FINALIDADGASTO = ResumenPpto_AG_ID.FINALIDADGASTO "
                + " AND ResumenPpto_PG_ID.RECURSOSCHIP = ResumenPpto_AG_ID.RECURSOSCHIP "
                + " AND ResumenPpto_PG_ID.VIGENCIAGASTO = ResumenPpto_AG_ID.VIGENCIAGASTO ";

        }
        else if (tipoEntidad == 2) {

            selectConsulta = "2";

        }
        else {
            selectConsulta = "";
        }
        return selectConsulta;
    }

    /**
     * Metodo que crea el select de la consulta cuando el tipo de
     * informe a generar es PET
     * 
     * @return Retorna Select cuando el tipo de informe es PET
     */
    public String creaConsultaPET() {
        String selectConsulta;
        String tablaP;
        String tablaA;
        if (tipoEntidad == 2) {

            selectConsulta = "pg2";

        }
        else {
            tablaP = Reporteador.resuelveConsulta("ResumenPpto_P_VT_ID",
                            Integer.valueOf("99"), variables);
            tablaA = Reporteador.resuelveConsulta("ResumenPpto_A_VT_ID",
                            Integer.valueOf("99"), variables);
            selectConsulta = "SELECT  ResumenPpto_P_VT_ID.ID, "
                + " Val(ROUND(NZ(ResumenPpto_A_VT_ID.Apropiado,0),0)) AS PROG_INI_TESOR, "
                + " Abs(Val(ROUND(ResumenPpto_A_VT_ID.Adicion+ResumenPpto_P_VT_ID.Adicion+ResumenPpto_A_VT_ID.TRASLADO_ADD+ResumenPpto_P_VT_ID.TRASLADO_ADD,0))) AS MOD_ADI_TESOR,"
                + " Val(Abs(ROUND(ResumenPpto_A_VT_ID.Reduccion+ResumenPpto_P_VT_ID.Reduccion+ResumenPpto_A_VT_ID.TRASLADO_RED+ResumenPpto_P_VT_ID.TRASLADO_RED,0))) AS MOD_RED_TESOR,"
                + " 0 AS MOD_ANT_TESOR, "
                + " Val(Abs(ROUND(ResumenPpto_P_VT_ID.Aplazamiento,0))) AS MOD_APL_TESOR,"
                + " 0 AS PROG_DEF_TESOR, "
                + " Val(ROUND(ResumenPpto_A_VT_ID.TotalIngresos+ResumenPpto_P_VT_ID.TotalIngresos,0)) AS PAGOS_TESOR,"
                + " 0 AS ANU_PAGOS_TESOR,"
                + " 0 AS SAL_PE_TESOR"
                + " FROM (" + tablaP + ")ResumenPpto_P_VT_ID "
                + " INNER JOIN (" + tablaA + ") ResumenPpto_A_VT_ID "
                + " ON ResumenPpto_P_VT_ID.ID = ResumenPpto_A_VT_ID.ID "
                + " AND ResumenPpto_P_VT_ID.ANO = ResumenPpto_A_VT_ID.ANO "
                + " AND ResumenPpto_P_VT_ID.CodigoEquivalente = ResumenPpto_A_VT_ID.CodigoEquivalente"
                + " AND ResumenPpto_P_VT_ID.RECURSOSCHIP = ResumenPpto_A_VT_ID.RECURSOSCHIP "
                + " AND ResumenPpto_P_VT_ID.ORIGENESPECIFICOINGRESOS = ResumenPpto_A_VT_ID.ORIGENESPECIFICOINGRESOS "
                + " AND ResumenPpto_P_VT_ID.DESTINACIONDELOSRECURSOS = ResumenPpto_A_VT_ID.DESTINACIONDELOSRECURSOS"
                + " AND ResumenPpto_P_VT_ID.VIGENCIATESORERIASCHIP = ResumenPpto_A_VT_ID.VIGENCIATESORERIASCHIP";
        }
        return selectConsulta;
    }

    /**
     * Metodo que crea la condicion cuando el tipo de informe a
     * generar es PG
     * 
     * @param registro:Registro
     * principal para crear las condiciones de la lista secundaria
     * @return Condicione where de la consulta cuando el tipo de
     * informe es PG
     */
    public String creaCondicionPG(Registro registro) {
        String condicion;
        if (tipoEntidad == 2) {
            numCol = 5;
            condicion = " WHERE CODIGOEQUIVALENTE = '"
                + registro.getCampos().get(constConcepto) + "'"
                + "  		AND NVL(ORIGENESPECIFICOINGRESOS,'') = '"
                + registro.getCampos().get(constCodOEI) + "'  "
                + "	 		AND NVL(DESTINACIONDELOSRECURSOS,'')= '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "			AND NVL(RECURSOSCHIP,'')='"
                + registro.getCampos().get(constCodRec) + "'"
                + "			AND NVL(FINALIDADGASTO,'') = '"
                + registro.getCampos().get(constCodFin) + "'"
                + "	  		AND NATURALEZA = 'D' ";

        }
        else {
            numCol = 7;
            condicion = " WHERE ResumenPpto_PG_ID.CODIGOEQUIVALENTE = '"
                + registro.getCampos().get(constConcepto) + "'"
                + "  		AND NVL(ResumenPpto_PG_ID.ORIGENESPECIFICOINGRESOS,'') ='"
                + registro.getCampos().get(constCodOEI) + "'"
                + "	  		AND NVL(ResumenPpto_PG_ID.DESTINACIONDELOSRECURSOS,'') = '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "	 		AND NVL(ResumenPpto_PG_ID.RECURSOSCHIP,'')= '"
                + registro.getCampos().get(constCodRec) + "'"
                + "	  		AND NVL(ResumenPpto_PG_ID.FINALIDADGASTO,'')= '"
                + registro.getCampos().get(constCodFin) + "'"
                + "			AND NVL(ResumenPpto_PG_ID.VIGENCIAGASTO,'')= '"
                + registro.getCampos().get(constVigGast) + "'"
                + "		 	AND ResumenPpto_PG_ID.NATURALEZA = 'D' ";
        }
        return condicion;
    }

    /**
     * Metodo que crea la condicion cuando el tipo de informe a
     * generar es PI o EG
     * 
     * @param registro:Registro
     * principal para crear las condiciones de la lista secundaria
     * @return Condicione where de la consulta cuando el tipo de
     * informe es PI o EG
     */
    public String creaCondicionPIyEI(Registro registro) {
        String condicion;
        numCol = 9;
        condicion = "WHERE ResumenPpto_P_I_ID.CODIGOEQUIVALENTE='"
            + registro.getCampos().get(constConcepto) + "'"
            + " 		AND NVL(ResumenPpto_P_I_ID.ORIGENESPECIFICOINGRESOS,'')= '"
            + registro.getCampos().get(constCodOEI) + "'"
            + "			AND NVL(ResumenPpto_P_I_ID.DESTINACIONDELOSRECURSOS,'')= '"
            + registro.getCampos().get(constCodDestEstt) + "'"
            + "			AND ResumenPpto_P_I_ID.NATURALEZA= 'C' ";

        return condicion;
    }

    /**
     * Metodo que crea la condicion cuando el tipo de informe a
     * generar es EG
     * 
     * @param registro:Registro
     * principal para crear las condiciones de la lista secundaria
     * @return Condicione where de la consulta cuando el tipo de
     * informe es EG
     */
    public String creaCondicionEG(Registro registro) {
        String condicion;
        if (tipoEntidad == 6) {
            numCol = 12;
            condicion = "WHERE ResumenPpto_P_ID.CODIGOEQUIVALENTE ='"
                + registro.getCampos().get(constConcepto) + "'"
                + " 	 	AND NVL(ResumenPpto_P_ID.ORIGENESPECIFICOINGRESOS,'') ='"
                + registro.getCampos().get(constCodOEI) + "'"
                + "		 	AND NVL(ResumenPpto_P_ID.DESTINACIONDELOSRECURSOS,'') = '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "		 	AND NVL(ResumenPpto_P_ID.RECURSOSCHIP,'')= '"
                + registro.getCampos().get(constCodRec) + "'"
                + "			AND NVL(ResumenPpto_P_ID.DEPENDENCIASCHIP,'')= '"
                + registro.getCampos().get("DEPENDENCIA") + "'"
                + "		 	AND NVL(ResumenPpto_P_ID.FINALIDADGASTO,'')= '"
                + registro.getCampos().get(constCodFin) + "'"
                + "		 	AND NVL(ResumenPpto_P_ID.VIGENCIAGASTO,'')= '"
                + registro.getCampos().get(constVigGast) + "'"
                + "	 		AND ResumenPpto_P_ID.NATURALEZA = 'D' ";

        }
        else if (tipoEntidad == 1 || tipoEntidad == 4 || tipoEntidad == 5
            || tipoEntidad == 7) {
            numCol = 11;
            condicion = "WHERE ResumenPpto_PG_ID.CODIGOEQUIVALENTE ='"
                + registro.getCampos().get(constConcepto) + "'"
                + " 		AND NVL(ResumenPpto_PG_ID.ORIGENESPECIFICOINGRESOS,'') ='"
                + registro.getCampos().get(constCodOEI) + "'"
                + "			AND NVL(ResumenPpto_PG_ID.DESTINACIONDELOSRECURSOS,'') = '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "			AND NVL(ResumenPpto_PG_ID.RECURSOSCHIP,'')= '"
                + registro.getCampos().get(constCodRec) + "'"
                + "			AND NVL(ResumenPpto_PG_ID.FINALIDADGASTO,'')= '"
                + registro.getCampos().get(constCodFin) + "'"
                + "		 	AND NVL(ResumenPpto_PG_ID.VIGENCIAGASTO,'')= '"
                + registro.getCampos().get(constVigGast) + "'"
                + "			AND ResumenPpto_PG_ID.NATURALEZA = 'D' ";
        }
        else if (tipoEntidad == 2) {
            numCol = 9;
            condicion = "WHERE CODIGOEQUIVALENTE ='"
                + registro.getCampos().get(constConcepto) + "'"
                + " 		AND NVL(ORIGENESPECIFICOINGRESOS,'') ='"
                + registro.getCampos().get(constCodOEI) + "'"
                + "			AND NVL(DESTINACIONDELOSRECURSOS,'') = '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "			AND NVL(RECURSOSCHIP,'')= '"
                + registro.getCampos().get(constCodRec) + "'"
                + "			AND NVL(FINALIDADGASTO,'')= '"
                + registro.getCampos().get(constCodFin) + "'"
                + "			AND NATURALEZA = 'D' ";
        }
        else {
            condicion = "";
        }
        return condicion;
    }

    /**
     * Metodo que crea la condicion cuando el tipo de informe a
     * generar es PET
     * 
     * @param registro:Registro
     * principal para crear las condiciones de la lista secundaria
     * @return Condicione where de la consulta cuando el tipo de
     * informe es PG
     */
    public String creaCondicionPET(Registro registro) {
        String condicion;
        if (tipoEntidad == 2) {
            numCol = 4;
            condicion = "WHERE CODIGOEQUIVALENTE = '"
                + registro.getCampos().get(constConcepto) + "'"
                + " 		AND NVL(ORIGENESPECIFICOINGRESOS,'') = '"
                + registro.getCampos().get(constCodOEI) + "'  "
                + "			AND NVL(DESTINACIONDELOSRECURSOS,'')= '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "			AND NVL(RECURSOSCHIP,'')='"
                + registro.getCampos().get(constCodRec) + "'"
                + "	 		AND NATURALEZA = 'D' ";

        }
        else {
            numCol = 5;
            condicion = "WHERE ResumenPpto_P_VT_ID.CODIGOEQUIVALENTE = '"
                + registro.getCampos().get(constConcepto) + "'"
                + "  		AND NVL(ResumenPpto_P_VT_ID.ORIGENESPECIFICOINGRESOS,'') ='"
                + registro.getCampos().get(constCodOEI) + "'"
                + "	  		AND NVL(ResumenPpto_P_VT_ID.DESTINACIONDELOSRECURSOS,'') = '"
                + registro.getCampos().get(constCodDestEstt) + "'"
                + "	 		AND NVL(ResumenPpto_P_VT_ID.RECURSOSCHIP,'')= '"
                + registro.getCampos().get(constCodRec) + "'"
                + "			AND NVL(ResumenPpto_P_VT_ID.VIGENCIATESORERIASCHIP,'')= '"
                + registro.getCampos().get("VIG_TES") + "'"
                + "		 	AND ResumenPpto_P_VT_ID.NATURALEZA = 'D' ";
        }
        return condicion;
    }

    /**
     * Metodo que genera la consulta y determina los parametros que
     * seran enviados para generar el archivo de verificacion de
     * ejecucion de gastos.
     * 
     * @param nombreArchivo
     * Cadena con el nombre del archivo que se va a generar.
     * @param pesos
     * Valor entero
     * @param anio
     * Valor del año que sera enviado al metodo EnviaDetalleRES.
     * @param mesInicial
     * Valor del mes inicial que sera enviado al metodo
     * EnviaDetalleRES.
     * @param mesFinal
     * Valor del mes final que sera enviado al metodo EnviaDetalleRES.
     * @param codigoEntidad
     * Codigo de la entidad SCHIP.
     * @author jlramirez
     */
    public StreamedContent verificarEjecucionGastos(String nombreArchivo,
        int pesos, String anio, String mesInicial, String mesFinal,
        String codigoEntidad) {
        StreamedContent archivo;
        HashMap<String, Object> reemplazos = new HashMap<>();
        HashMap<String, Object> reemplazosP = new HashMap<>();
        HashMap<String, Object> reemplazosA = new HashMap<>();
        String nomConsulta = "";
        String informe = "EG";
        reemplazosP.put("anio", anio);
        reemplazosP.put("mesfinal", mesFinal);
        reemplazosA.put("anio", anio);
        reemplazosA.put("mesinicial", mesInicial);
        String[] campos = { constConcepto, constCodRec, constCodOEI,
                            constCodDestEstt,
                            constCodFin, "COD_SIT", "NDA_COM", "NDA_PAG",
                            "ID_ETRA", "EJE_GCCA_GAST1", "EJE_GCSA_GAST",
                            "REV_GC_GAST", "EJE_PAGOS_GAST", "ANU_PAGOS_GAST",
                            "CXP_GAST_ACEP" };
        if (tipoEntidad == 6) {
            reemplazos.put("resumenpptop",
                            Reporteador.resuelveConsulta("resumenpptop",
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosP));
            reemplazos.put("resumenpptoa",
                            Reporteador.resuelveConsulta("resumenpptoa",
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosA));
            nomConsulta = "miresumen1";
        }
        else if (tipoEntidad == 1 || tipoEntidad == 4 || tipoEntidad == 5
            || tipoEntidad == 7) {
            reemplazos.put("resumenpptopg",
                            Reporteador.resuelveConsulta("resumenpptopg",
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosP));
            reemplazos.put("resumenpptoag",
                            Reporteador.resuelveConsulta("resumenpptoag",
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosA));
            nomConsulta = "miresumen2";
        }
        else if (tipoEntidad == 2) {
            reemplazos.put("resumenpptopg2",
                            Reporteador.resuelveConsulta("resumenpptopg2",
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosP));
            reemplazos.put("resumenpptoag2",
                            Reporteador.resuelveConsulta("resumenpptoag2",
                                            Integer.valueOf(SessionUtil
                                                            .getModulo()),
                                            reemplazosA));
            nomConsulta = "miresumen3";
        }
        reemplazos.put("codigoentidad", codigoEntidad);
        reemplazos.put("pesos", pesos);

        String consulta = Reporteador.resuelveConsulta(nomConsulta,
                        Integer.valueOf(SessionUtil.getModulo()), reemplazos);

        archivo = envioDetalleRES(nombreArchivo, consulta, informe, campos,
                        mesInicial, mesFinal, anio);
        return archivo;
    }

    /**
     * Metodo que genera la consulta y determina los parametros que
     * seran enviados para generar el archivo de verificacion de
     * programacion de gastos.
     * 
     * @param nombreArchivo
     * Cadena con el nombre del archivo que se va a generar.
     * @param anio
     * Valor del año que sera enviado al metodo EnviaDetalleRES.
     * @param mesInicial
     * Valor del mes inicial que sera enviado al metodo
     * EnviaDetalleRES.
     * @param mesFinal
     * Valor del mes final que sera enviado al metodo EnviaDetalleRES.
     * @author jlramirez
     */
    public StreamedContent verificarPrograGastosReg(String nombreArchivo,
        String anio, String mesInicial, String mesFinal) {
        HashMap<String, Object> reemplazos = new HashMap<>();
        HashMap<String, Object> reemplazosPGR = new HashMap<>();
        String nomConsulta;
        StreamedContent archivo;
        String informe = "PG";
        String[] campos = { "CHIP", constConcepto, "VIG_GAST", constCodRec,
                            constCodOEI, constCodDestEstt, constCodFin,
                            "APR_INI_DIS_GAST",
                            "MOD_ADI_GAST", "MOD_RED_GAST", "CANCELAC_GAST",
                            "MT_CRE_GAST", "MT_CCRE_GAST", "APR_DEF_GAST",
                            "CDP_GAST", "REV_CDP_GAST" };
        reemplazosPGR.put("anio", anio);
        reemplazosPGR.put("mesfinal", mesFinal);
        reemplazos.put("resumenpptopgr",
                        Reporteador.resuelveConsulta("resumenpptopgr",
                                        Integer.valueOf(SessionUtil
                                                        .getModulo()),
                                        reemplazosPGR));
        if (tipoEntidad != 2) {
            nomConsulta = "miresumen1";
        }
        else {
            nomConsulta = "miresumen2";
        }
        String consulta = Reporteador.resuelveConsulta(nomConsulta,
                        Integer.valueOf(SessionUtil.getModulo()), reemplazos);

        archivo = envioDetalleRES(nombreArchivo, consulta, informe, campos,
                        mesInicial, mesFinal, anio);
        return archivo;
    }

    /**
     * Metodo que determina los parametros a enviar al metodo
     * EnviaDetalleRes dependiendo de si es programacion o ejecucion
     * de ingresos.
     * 
     * @param nombreArchivo
     * Cadena con el nombre del archivo que se va a generar.
     * @param pesos
     * Valor double
     * @param opcion
     * Valor numerico que determina si se verifica la programacion (1)
     * o la ejecucion (0) de ingresos.
     * @param anio
     * Valor del año que sera enviado al metodo EnviaDetalleRES.
     * @param mesInicial
     * Valor del mes inicial que sera enviado al metodo
     * EnviaDetalleRES.
     * @param mesFinal
     * Valor del mes final que sera enviado al metodo EnviaDetalleRES.
     * @author yrojas
     */
    public StreamedContent verificarIngresos(String nombreArchivo, int pesos,
        int opcion, String anio, String mesInicial, String mesFinal) {
        HashMap<String, Object> reemplazos = new HashMap<>();
        HashMap<String, Object> reemplazosPI = new HashMap<>();
        String strSelect;
        String informe;
        String[] campos = { "CONCEPTO", "COD_REC", "COD_OEI", "COD_DEST_ESTT",
                            "CHIP" };
        StreamedContent archivo;

        if (opcion != 0) {
            strSelect = "       '0' ND_ACTO_ADTIVO, " +
                "       ROUND(CASE WHEN " + pesos + " = 0 " +
                "                  THEN RESUMENPPTO_A_I.APROPIADO" +
                "                  ELSE RESUMENPPTO_A_I.APROPIADO/1000" +
                "             END)                                   PRE_INI_INGR,"
                +
                "       ROUND(ABS(CASE WHEN " + pesos + " = 0" +
                "                      THEN RESUMENPPTO_A_I.ADICION + RESUMENPPTO_P_I.ADICION"
                +
                "                      ELSE (RESUMENPPTO_A_I.ADICION + RESUMENPPTO_P_I.ADICION)/1000"
                +
                "                 END))                              MOD_ADI_INGR,"
                +
                "       ROUND(ABS(CASE WHEN " + pesos + " = 0" +
                "                      THEN RESUMENPPTO_A_I.REDUCCION + RESUMENPPTO_P_I.REDUCCION"
                +
                "                      ELSE (RESUMENPPTO_A_I.REDUCCION + RESUMENPPTO_P_I.REDUCCION)/1000"
                +
                "                 END))                              MOD_RED_INGR,"
                +
                "       0 PRE_DEF_ING ";

            informe = "PI";

            campos[5] = "ND_ACTO_ADTIVO";
            campos[6] = "PRE_INI_INGR";
            campos[7] = "MOD_ADI_INGR";
            campos[8] = "MOD_RED_INGR";
            campos[9] = "PRE_DEF_ING";
        }
        else {
            strSelect = "       0 NDA_DPC," +
                "       0 NDA_REC," +
                "       TO_CHAR(" + tipoEntidad + ") ID_ETRA," +
                "       0 ND_ACTO_ADTIVO," +
                "       0 DPC_INGR," +
                "       0 REV_DPC_INGR," +
                "       ROUND(RESUMENPPTO_A_I.TOTALINGRESOS + RESUMENPPTO_P_I.TOTALINGRESOS, 0) EE_RECAUDO_INGR,"
                +
                "       0 EE_DEVOLUC_INGR," +
                "       0 EE_REVREC_INGR," +
                "       0 E_OTR_INGR," +
                "       0 REV_OTR_INGR," +
                "       0 RECONOC_INGR," +
                "       ROUND(RESUMENPPTO_A_I.VIGENCIAANTERIOR + RESUMENPPTO_P_I.VIGENCIAANTERIOR, 0) REC_VA_INGR,"
                +
                "       0 REV_RVA_INGR ";

            informe = "EI";

            campos[5] = "NDA_DPC";
            campos[6] = "NDA_REC";
            campos[7] = "ID_ETRA";
            campos[8] = "ND_ACTO_ADTIVO";
            campos[9] = "DPC_INGR";
            campos[10] = "REV_DPC_INGR";
            campos[11] = "EE_RECAUDO_INGR";
            campos[12] = "EE_DEVOLUC_INGR";
            campos[13] = "EE_REVREC_INGR";
            campos[14] = "E_OTR_INGR";
            campos[15] = "REV_OTR_INGR";
            campos[16] = "RECONOC_INGR";
            campos[17] = "REC_VA_INGR";
            campos[18] = "REV_RVA_INGR";
        }

        reemplazosPI.put("anio", anio);
        reemplazosPI.put("mesInicial", mesInicial);
        reemplazosPI.put("mesFinal", mesFinal);

        reemplazos.put("select", strSelect);
        reemplazos.put("resumenpptopi",
                        Reporteador.resuelveConsulta("ResumenPPTOpi",
                                        Integer.valueOf(SessionUtil
                                                        .getModulo()),
                                        reemplazosPI));
        reemplazos.put("resumenpptoai",
                        Reporteador.resuelveConsulta("ResumenPPTOai",
                                        Integer.valueOf(SessionUtil
                                                        .getModulo()),
                                        reemplazosPI));

        String consulta = Reporteador.resuelveConsulta("",
                        Integer.valueOf(SessionUtil.getModulo()), reemplazos);

        archivo = envioDetalleRES(nombreArchivo, consulta, informe, campos,
                        mesInicial, mesFinal, anio);
        return archivo;

    }

    /**
     * Retorna la lista listaPrincipal
     * 
     * @return listaPrincipal
     */
    public List<Registro> getListaPrincipal() {
        return listaPrincipal;
    }

    /**
     * Asigna la lista listaPrincipal
     * 
     * @param listaPrincipal
     * Variable a asignar en listaPrincipal
     */
    public void setListaPrincipal(List<Registro> listaPrincipal) {
        this.listaPrincipal = listaPrincipal;
    }

    /**
     * Retorna la lista listaSecunadria
     * 
     * @return listaSecunadria
     */
    public List<Registro> getListaSecunadria() {
        return listaSecunadria;
    }

    /**
     * Asigna la lista listaSecunadria
     * 
     * @param listaSecunadria
     * Variable a asignar en listaSecunadria
     */
    public void setListaSecunadria(List<Registro> listaSecunadria) {
        this.listaSecunadria = listaSecunadria;
    }

    /**
     * Retorna la variable tipoEntidad
     * 
     * @return tipoEntidad
     */
    public int getTipoEntidad() {
        return tipoEntidad;
    }

    /**
     * Asigna la variable tipoEntidad
     * 
     * @param tipoEntidad
     * Variable a asignar en tipoEntidad
     */
    public void setTipoEntidad(int tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     * 
     * @return archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     * 
     * @return archivoDescarga
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * Retorna la variable numCol
     * 
     * @return numCol
     */
    public int getNumCol() {
        return numCol;
    }

    /**
     * Asigna la variable numCol
     * 
     * @param numCol
     * Variable a asignar en numCol
     */
    public void setNumCol(int numCol) {
        this.numCol = numCol;
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
     * Asigna la variable compania
     * 
     * @param compania
     * Variable a asignar en compania
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }

}
