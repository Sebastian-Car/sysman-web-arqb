package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FormatoDIANRetControladorEnum;
import com.sysman.contabilidad.enums.FormatoDIANRetControladorUrlEnum;
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
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 11/04/2016
 * @modifed jsforero
 * @version 2. 07/04/2017 Se realizo el refactory.
 * 
 * @author jlramirez
 * @version 3. 20/04/2017, Manejo de EJBs
 * 
 * @version 4, 10/05/2017, pespitia:<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FormatoDIANRetControlador extends BeanBaseModal {
    private final String formulaConst;
    private final String compania;
    private int anio;
    private int mesInicial;
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoCARGARPLANTILLAEXCEL;
    private List<Registro> listaAno;
    private List<Registro> listaMesinicial;

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    private CellStyle style;

    /**
     * Creates a new instance of Formato350Controlador
     */
    public FormatoDIANRetControlador() {
        super();
        compania = SessionUtil.getCompania();
        formulaConst = "FORMULAID";
        try {
            contArchivoCARGARPLANTILLAEXCEL = new ContenedorArchivo();
            numFormulario = GeneralCodigoFormaEnum.FORMATO_DIANRET_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FormatoDIANRetControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        cargarListaAno();
        Date fechaActual = new Date();
        anio = SysmanFunciones.ano(fechaActual);
        mesInicial = SysmanFunciones.mes(fechaActual);
        cargarListaMesinicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlListAno = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormatoDIANRetControladorUrlEnum.URL3612
                                                        .getValue());
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListAno.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FormatoDIANRetControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesinicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        UrlBean urlListMes = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormatoDIANRetControladorUrlEnum.URL3916
                                                        .getValue());
        try {
            listaMesinicial = RegistroConverter.toListRegistro(
                            requestManager.getList(urlListMes.getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(FormatoDIANRetControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (contArchivoCARGARPLANTILLAEXCEL.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB856"));
            return;
        }

        try (Workbook workbook = new HSSFWorkbook(new FileInputStream(
                        contArchivoCARGARPLANTILLAEXCEL.getArchivo()))) {

            Sheet sheet = workbook.getSheet("Formulario");
            style = workbook.createCellStyle();
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setTopBorderColor(IndexedColors.BLUE_GREY.getIndex());
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setLeftBorderColor(IndexedColors.BLUE_GREY.getIndex());
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setRightBorderColor(IndexedColors.BLUE_GREY.getIndex());
            style.setAlignment(CellStyle.ALIGN_RIGHT);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBottomBorderColor(IndexedColors.BLUE_GREY.getIndex());
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 8);
            font.setFontName("Arial");
            style.setFont(font);

            if (sheet == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB857"));
                return;
            }         
            
            // usando bucle for simple para recorrer los caracteres del año
            Cell cellAnio;
            String ano = String.valueOf(anio);
            for (int i = 0; i < ano.length(); i++) 
            {	
                cellAnio = sheet.getRow(4).createCell(4+i);
                cellAnio.setCellValue(String.valueOf(ano.charAt(i)));
                cellAnio.setCellStyle(style);                
            }            
            
            // usando bucle for simple para recorrer los caracteres del mes
            Cell cellMes;
            String mes = String.valueOf(mesInicial);
            if(mesInicial < 10)
            {
            	mes = "0"+mes;
            }
            for (int i = 0; i < mes.length(); i++) 
            {	
            	cellMes = sheet.getRow(4).createCell(23+i);
            	cellMes.setCellValue(String.valueOf(mes.charAt(i)));
            	cellMes.setCellStyle(style);                
            }
            
            // Se obtiene el nit sin digitop de verificacion
            String nitCompania = SessionUtil.getCompaniaIngreso().getNit();
            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre(); 
            String digito = "";
            if (nitCompania.contains("-"))
            {	
                int fin = nitCompania.indexOf("-");
                digito = nitCompania.substring(fin+1, nitCompania.length());
                nitCompania = nitCompania.substring(0, fin);                
            }  
            
            // usando bucle for simple para recorrer los caracteres del nit de la compañia
            Cell cellNit;
            for (int i = 0; i < nitCompania.length(); i++) 
            {	
            	cellNit = sheet.getRow(11).createCell(2+i);
            	cellNit.setCellValue(String.valueOf(nitCompania.charAt(i)));
            	cellNit.setCellStyle(style);                
            }
            
            cellNit = sheet.getRow(11).createCell(16);
        	cellNit.setCellValue(String.valueOf(digito));
        	cellNit.setCellStyle(style);  
            
            Cell cellNombreCompa = sheet.getRow(14).createCell(2);
            cellNombreCompa.setCellValue(nombreCompania);
            style.setAlignment(CellStyle.ALIGN_LEFT);
            cellNombreCompa.setCellStyle(style);
            
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            param.put(GeneralParameterEnum.ANO.name(), anio);
            param.put(FormatoDIANRetControladorEnum.MESINICIAL.getValue(),
                            mesInicial);  
            param.put(GeneralParameterEnum.NATURALEZA.name(), "J");
            boolean revisaCuenta = "SI".equals(sysmanUtil.consultarParametro(
                            compania,
                            "REVISAR CUENTAS POR PAGAR POR FECHA PAGO",
                            SessionUtil.getModulo(), new Date(), true));
            UrlBean urlList;
            if (revisaCuenta) {
                urlList = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FormatoDIANRetControladorUrlEnum.URL16224
                                                                .getValue());
            }
            else {
                urlList = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FormatoDIANRetControladorUrlEnum.URL16223
                                                                .getValue());
            }   
            
            prepararInforme(sheet, urlList, "BD", formulaConst, "Q", param,
                            "VALORB", "J");
            param.put(GeneralParameterEnum.NATURALEZA.name(), "N");
            prepararInforme(sheet, urlList, "BD", formulaConst, "AK", param,
                    "VALORB", "N");
            param.put(GeneralParameterEnum.NATURALEZA.name(), "J");
            prepararInforme(sheet, urlList, "BE", formulaConst, "AA", param,
                    "VALOR", "J");
            param.put(GeneralParameterEnum.NATURALEZA.name(), "N");
            prepararInforme(sheet, urlList, "BE", formulaConst, "AU", param,
            		"VALOR", "N");

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Formulario retencion 350-2024.xls");
        }
        catch (IOException | JRException | SystemException e) {
            Logger.getLogger(FormatoDIANRetControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean prepararInforme(Sheet sheet, UrlBean urlLista,
        String strcolumnareferencia, String strCampoBuscar, String numColumna,
        Map<String, Object> param, String campo, String naturaleza) {
        List<Registro> rs = null;
        try {        	
        	rs = RegistroConverter.toListRegistro(
                            requestManager.getList(urlLista.getUrl(), param));

            if (rs.isEmpty()) {
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(FormatoDIANRetControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        envioDetalle(sheet, rs, strcolumnareferencia, strCampoBuscar,
                        numColumna, campo, naturaleza);
        return true;
    }

    private void envioDetalle(Sheet sheet, List<Registro> rs,
        String strcolumnareferencia, String strCampoBuscar, String numColumna,
        String campo, String naturaleza) {
        String signo;        
        int column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
        
        for (Registro registro : rs) {
            signo = "+";
            String busqueda = signo + SysmanFunciones
                            .nvl(registro.getCampos().get(strCampoBuscar), "")
                            .toString()
                + signo;
            
            Cell cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            
            if (cell != null) {
                int numColumn = CellRangeAddress.valueOf(
                                "" + numColumna + ":" + numColumna + "")
                                .getFirstColumn();
                
                if(!((naturaleza == "J" && 
                		(cell.getRowIndex() == 18 || cell.getRowIndex() == 19 || cell.getRowIndex() == 30)) ||
                		(naturaleza == "N" && (cell.getRowIndex() == 36))))
                {
                	double aux = sheet.getRow(cell.getRowIndex()).getCell(numColumn)
                                .getNumericCellValue()
                    + Double.parseDouble(
                                    registro.getCampos().get(campo).toString());

	                Cell newCell = sheet.getRow(cell.getRowIndex())
	                                .createCell(numColumn);
	
	                newCell.setCellStyle(style);
	                newCell.setCellValue(aux);
	            }
            }
        }
    }

    private boolean verificarCelda(Cell celda, String searchText) {
        if ((celda != null) && (searchText != null)) {
            switch (celda.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                return celda.getStringCellValue().contains(searchText);
            case HSSFCell.CELL_TYPE_FORMULA:
                return celda.getCellFormula().contains(searchText);
            default:
                return searchText.equals(celda.getStringCellValue());
            }
        }

        return false;
    }

    public Cell searchColumnSheet(String searchText, int column, int rowStart,
        int rowEnd, Sheet sheet) {
        Cell c = null;
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            if ((sheet.getRow(rowNum) != null)
                && (sheet.getRow(rowNum).getCell(column) != null)) {
                Row r = sheet.getRow(rowNum);

                if (verificarCelda(r.getCell(column, Row.RETURN_BLANK_AS_NULL),
                                searchText)) {
                    return r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                }

            }
        }
        return c;
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mesInicial = 0;
        listaMesinicial = null;

        if (anio != 0) {
            cargarListaMesinicial();
        }
        // </CODIGO_DESARROLLADO>
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoCARGARPLANTILLAEXCEL() {
        return contArchivoCARGARPLANTILLAEXCEL;
    }

    public void setContArchivoCARGARPLANTILLAEXCEL(
        ContenedorArchivo contArchivoCARGARPLANTILLAEXCEL) {
        this.contArchivoCARGARPLANTILLAEXCEL = contArchivoCARGARPLANTILLAEXCEL;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMesinicial() {
        return listaMesinicial;
    }

    public void setListaMesinicial(List<Registro> listaMesinicial) {
        this.listaMesinicial = listaMesinicial;
    }
}