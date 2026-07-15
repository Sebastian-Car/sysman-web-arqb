package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author jrodriguezr
 * @version 1, 05/02/2016
 *
 * @author eamaya
 * @version 2.0, 12/06/2017 Cambio código formulario
 */
@ManagedBean
@ViewScoped
public class ImportarExcelControlador extends BeanBaseModal {

    private final String compania;

    private StringBuilder parametrosFuncion;
    private InputStream is;

    @EJB
    private EjbAlmacenCuatroRemote almacenCuatro;

    /**
     * Creates a new instance of ImportarExcelControlador
     */
    public ImportarExcelControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.IMPORTAR_EXCEL_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ImportarExcelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public void oprimirbtnCargar() {
        // <CODIGO_DESARROLLADO>

        parametrosFuncion = new StringBuilder();

        if (is == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
        }
        else {

            try {
                File directorio = new File(
                                SessionUtil.getRuta(SessionUtil.getModulo())
                                    + "plantillas" + File.separator
                                    + numFormulario);
                directorio.mkdir();
                File archivo = JsfUtil.upload(is, "excel2.xlsx",
                                directorio.getAbsolutePath() + File.separator);

                Workbook workbook = null;
                workbook = new XSSFWorkbook(archivo);

                leerHoja(workbook, 0, 19, parametrosFuncion, 1);

                String mensaje = idioma.getString("TB_TB1902");
                mensaje = mensaje.replace("s$contador$s",
                                actualizarDesdeExcel(
                                                parametrosFuncion.toString()));
                JsfUtil.agregarMensajeInformativo(mensaje);
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1323"));
            }
            catch (IOException | OpenXML4JException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena, int filainicial) {
        cadena.append("TO_CLOB('");
        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        int num = 0;
        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
            fila = sheet.getRow(i);

            for (int j = 0; j < columnas; j++) {
                celda = fila.getCell(j, fila.RETURN_BLANK_AS_NULL);

                if (celda != null) {
                    num = num
                        + (celda.getCellType() == 1
                            ? celda.getStringCellValue()
                                            .replaceFirst("'", " ").length()
                            : NumberToTextConverter
                                            .toText(celda.getNumericCellValue())
                                            .length());
                    cadena.append(celda.getCellType() == 1
                        ? celda.getStringCellValue().replaceFirst("'", " ")
                        : NumberToTextConverter
                                        .toText(celda.getNumericCellValue()));

                }
                else {
                    cadena.append("");
                }
                if (num >= 10000) {
                    cadena.append("') || TO_CLOB('");
                    num = 0;
                }
                cadena.append(SysmanConstantes.SEPARADOR_COL);
            }
            cadena.append(SysmanConstantes.SEPARADOR_REG);

        }
        cadena.append("')"
            + "");
    }

    private String actualizarDesdeExcel(String plano)
                    throws SystemException {

        if (SysmanFunciones.esBdSqlServer()) {
            plano = plano.replace("TO_DATE(", "");
            plano = plano.replace(",''DD/MM/YYYY'')", "");
            plano = plano.replace("TO_CLOB(", "").replace(")", "");
        }
        return SysmanFunciones
                        .nvl(almacenCuatro.actualizarDesdeExcel(compania, plano,
                                        SessionUtil.getUser().getCodigo()), 0)
                        .toString();
    }

    public void cargarArchivoCargaExcel(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getFile() != null) {
            try {
                is = event.getFile().getInputstream();
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1903")
                    + event.getFile().getFileName());
            }
            catch (NullPointerException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst)
                    throws SAXException {
        XMLReader parser = XMLReaderFactory
                        .createXMLReader("org.apache.xerces.parsers.SAXParser");
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    private class SheetHandler extends DefaultHandler {

        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;

        private String columna;
        private String where = " WHERE DEVOLUTIVO.COMPANIA = '" + compania
            + "' "
            + "  AND DEVOLUTIVO.ELEMENTO = ";
        private String campos;

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
            where = " WHERE DEVOLUTIVO.COMPANIA = '" + compania + "' "
                + " AND DEVOLUTIVO.ELEMENTO = ";
            columna = null;
            campos = "";
        }

        @Override
        public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
            if ((columna != null) && !lastContents.isEmpty()
                && !"\n ".equals(lastContents) && !"\n".equals(lastContents)) {
                concatenaCampos();
            }
            if ("c".equals(name)) {
                columna = attributes.getValue("r").substring(0, 1);
                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                if ((cellType != null) && "s".equals(cellType)) {
                    nextIsString = true;
                }
                else {
                    nextIsString = false;
                }
            }
            lastContents = "";
        }

        private void concatenaCampos() {
            String campo = lastContents.replace(" ", "");
            switch (columna) {
            case "A":
                where += campo;
                break;
            case "F":
                where += " AND DEVOLUTIVO.SERIE = " + campo;
                break;
            case "E":
                campos += " MESESVIDAUTILPLACA = "
                    + (campo.isEmpty() ? "0" : campo) + ", ";
                break;
            case "J":
                campos += " DEPACUMULADA = " + campo + ", ";
                break;
            case "K":
                campos += " VLRLIBROS = " + campo + ", ";
                break;
            case "L":
                campos += " NIIF_VALOR_TOTAL = " + campo + ", ";
                break;
            case "M":
                campos += " SALVAMENTO = " + campo + ", ";
                break;
            case "N":
                campos += " DETERIORO = " + campo + ", ";
                break;
            case "P":
                campos += " NIIF_VIDA_UTIL = "
                    + (campo.isEmpty() ? "NULL"
                        : lastContents)
                    + ", ";
                break;
            case "Q":
                campos += " NIIF_FECHAFUNCIONAMIENTO = TO_DATE(''"
                    + campo
                    + "'',''DD/MM/YYYY''), ";
                break;
            case "R":
                campos += " NIIF_TIPO_ACTIVO = ''" + campo + "''" + ", ";
                break;
            case "S":
                campos += " APLICA_NIIF = " + campo;
                break;
            case "T":
                validaFin();
                break;
            default:
                break;
            }
        }

        private void validaFin() {
            if (!" WHERE".equals(where.substring(0, 6))
                && !campos.isEmpty()) {
                campos += " # " + where + " @";
                parametrosFuncion.append(campos);
            }
            where = "  DEVOLUTIVO.COMPANIA = ''" + compania + "'' "
                + " AND DEVOLUTIVO.ELEMENTO = ";
            campos = "";
        }

        @Override
        public void endElement(String uri, String localName, String name)
                        throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if (nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx))
                                .toString();
                nextIsString = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                        throws SAXException {
            lastContents += new String(ch, start, length);
        }
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

}
