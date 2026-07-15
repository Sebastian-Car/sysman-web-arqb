/*-
 * InventarioFisicoGeneralControlador.java
 *
 * 1.0
 * 
 * 06/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 * Controlador que se usa para imprimir el inventario fisico general.
 *
 * @version 1.0, 06/08/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  InventarioFisicoGeneralControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	private final String modulo;
	
	private String reporte;

	private String digitos;
	
	private boolean sinDepen;
	
	private Date txtFecha;
	
	private boolean saldoLibros;

	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InventarioFisicoGeneralControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public InventarioFisicoGeneralControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo=SessionUtil.getModulo();
		try { 
			numFormulario=GeneralCodigoFormaEnum.INVENTARIO_FISICO_GENERAL_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
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
	public void inicializar(){
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		try {

			digitos = ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
					false);

		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
		FR1880-AL_ABRIR
		Private Sub Form_Open(Cancel As Integer)
		   'formularioAbrir 10, Me.Name
		End Sub
				 */


		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * Metodos de imprimir.
	 *
	 */
	public void oprimirpdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	
	public void oprimirexcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if(saldoLibros) {
		generaInformeSaldoLibros(FORMATOS.EXCEL);
		}else {
		generaInforme(FORMATOS.EXCEL);
		}
		//</CODIGO_DESARROLLADO>
	}
	public void consultarReporte() {

		if (sinDepen == true) {
			reporte = "001857InventarioFisicoDeDevolutivosGN";

		}
		else {
			reporte = "001856InventarioFisicoDeDevolutivos";
		}

	}

	public void generaInforme(ReportesBean.FORMATOS formato) {

		try{

			consultarReporte();
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("txtFecha", SysmanFunciones.formatearFechaCadena(txtFecha, "DD/MM/YYYY"));
			reemplazar.put("compania", compania);
			reemplazar.put("digito", digitos);
			reemplazar.put("dependencia", sinDepen);

			Reporteador.resuelveConsulta("001856InventarioFisicoDeDevolutivos", Integer.parseInt(modulo),reemplazar, parametros);   
			parametros.put("PR_FORMS_INVENTARIOFISICOGENERAL_TXTFECHA", SysmanFunciones.convertirAFechaCadena(txtFecha));

			archivoDescarga = JsfUtil.exportarStreamed(reporte,
					parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	private void generaInformeSaldoLibros(FORMATOS formato) {
	    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        HashMap<String, Object> reemplazar = new HashMap<>();

	        reemplazar.put("compania", compania);
	        reemplazar.put("fecha", SysmanFunciones.formatearFechaCadena(txtFecha, "DD/MM/YYYY"));
	        reemplazar.put("digito", digitos);

	        String reporte = "800634InventarioFisicoSaldoLibros";
	        String sql = Reporteador.resuelveConsulta(reporte, reemplazar);

	        archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, reporte);

	        Workbook workbook = new XSSFWorkbook(JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato).getStream());
	        Sheet sheet = workbook.getSheetAt(0);

	        sheet.shiftRows(0, sheet.getLastRowNum(), 3);
	        sheet.createFreezePane(0, 3);

	        Font font2 = workbook.createFont();
	        font2.setFontName("SansSerif");
	        font2.setFontHeightInPoints((short) 11);
	        font2.setBold(false);

	        Font boldFont = workbook.createFont();
	        boldFont.setBold(true);

	        CellStyle sumStyle = workbook.createCellStyle();
	        sumStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
	        sumStyle.setFont(boldFont);
	        
	        Row r = sheet.createRow(0);
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
	        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
	        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));

	        CellStyle style = workbook.createCellStyle();
	        style.setAlignment(HorizontalAlignment.CENTER);
	        style.setVerticalAlignment(VerticalAlignment.CENTER);
	        style.setFont(boldFont);

	        Row r1 = sheet.createRow(0);
	        Cell cell1 = r1.createCell(0);
	        cell1.setCellValue(SessionUtil.getCompaniaIngreso().getNombre());
	        cell1.setCellStyle(style);

	        Row r2 = sheet.createRow(1);
	        Cell cell2 = r2.createCell(0);
	        cell2.setCellValue("INVENTARIO DE DEVOLUTIVOS GENERAL");
	        cell2.setCellStyle(style);

	        Row r3 = sheet.createRow(2);
	        Cell cell3 = r3.createCell(0);
	        String fechaCorte = new SimpleDateFormat("dd-MM-yyyy").format(txtFecha);
	        cell3.setCellValue("FECHA DE CORTE: " + fechaCorte);
	        cell3.setCellStyle(style);

	        int startRow = 4;
	        int lastRow = sheet.getLastRowNum();
	        String previousGrupo = null;
	        List<Integer> totalRows = new ArrayList<>();

	        for (int i = startRow; i <= lastRow; i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            Cell grupoCell = row.getCell(1);
	            if (grupoCell == null || grupoCell.getCellTypeEnum() != CellType.STRING) continue;

	            String currentGrupo = grupoCell.getStringCellValue();
	            if (!currentGrupo.equals(previousGrupo) && previousGrupo != null) {
	                // Mover las filas hacia abajo antes de insertar la fila de suma
	                sheet.shiftRows(i, lastRow, 1);
	                Row sumRow = sheet.createRow(i);
	                Cell sumCell = sumRow.createCell(1);
	                sumCell.setCellValue("TOTAL " + previousGrupo);
	                sumCell.setCellStyle(style);

	                sumCell = sumRow.createCell(10);
	                sumCell.setCellFormula("SUM(K" + startRow + ":K" + i + ")");
	                sumCell.setCellStyle(sumStyle);

	                sumCell = sumRow.createCell(11);
	                sumCell.setCellFormula("SUM(L" + startRow + ":L" + i + ")");
	                sumCell.setCellStyle(sumStyle);

	                totalRows.add(i); // Añadir la fila de sub total a la lista

	                startRow = i + 2; // Actualizar la fila inicial para el próximo grupo
	                i++; // Incrementar el índice para saltar la fila de suma recién insertada
	                lastRow++; // Incrementar el número de la última fila después del cambio
	            }
	            previousGrupo = currentGrupo;
	        }

	        // Insertar la última fila de suma
	        if (previousGrupo != null) {
	            Row sumRow = sheet.createRow(lastRow + 1);
	            Cell sumCell = sumRow.createCell(1);
	            sumCell.setCellValue("TOTAL " + previousGrupo);
	            sumCell.setCellStyle(style);

	            sumCell = sumRow.createCell(10);
	            sumCell.setCellFormula("SUM(K" + startRow + ":K" + (lastRow+1) + ")");
	            sumCell.setCellStyle(sumStyle);

	            sumCell = sumRow.createCell(11);
	            sumCell.setCellFormula("SUM(L" + startRow + ":L" + (lastRow+1) + ")");
	            sumCell.setCellStyle(sumStyle);

	            totalRows.add(lastRow + 1); // Añadir la última fila de sub total a la lista
	        }

	        // Insertar la fila de suma total general
	        int totalGeneralRow = sheet.getLastRowNum() + 1;
	        Row totalRow = sheet.createRow(totalGeneralRow);
	        Cell totalCell = totalRow.createCell(1);
	        totalCell.setCellValue("TOTAL GENERAL");
	        totalCell.setCellStyle(style);

	        int maxArgs = 255;
	        StringBuilder sumIFinal = new StringBuilder();
	        StringBuilder sumJFinal = new StringBuilder();

	        for (int i = 0; i < totalRows.size(); i++) {
	            if (i > 0 && i % maxArgs == 0) {
	                sumIFinal.append(")");
	                sumJFinal.append(")");
	                if (i < totalRows.size() - 1) {
	                    sumIFinal.append("+SUM(");
	                    sumJFinal.append("+SUM(");
	                }
	            }
	            if (sumIFinal.length() == 0 || sumIFinal.charAt(sumIFinal.length() - 1) == ')') {
	                sumIFinal.append("SUM(");
	                sumJFinal.append("SUM(");
	            }
	            if (i % maxArgs > 0) {
	                sumIFinal.append(",");
	                sumJFinal.append(",");
	            }
	            sumIFinal.append("K").append(totalRows.get(i) + 1);
	            sumJFinal.append("L").append(totalRows.get(i) + 1);
	        }
	        sumIFinal.append(")");
	        sumJFinal.append(")");

	        Cell totalSumICell = totalRow.createCell(10);
	        totalSumICell.setCellFormula(sumIFinal.toString());
	        totalSumICell.setCellStyle(sumStyle);

	        Cell totalSumJCell = totalRow.createCell(11);
	        totalSumJCell.setCellFormula(sumJFinal.toString());
	        totalSumJCell.setCellStyle(sumStyle);

	        workbook.write(out);

	        archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), "001856InventarioFisicoSaldoLibros.xlsx");
	        workbook.close();
	    } catch (JRException | IOException | DRException | SQLException | SysmanException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control SaldoLibros
	 * 
	 * 
	 */
	public void cambiarSaldoLibros() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable sinDepen
	 * 
	 * @return  sinDepen
	 */
	public boolean getSinDepen() {
		return sinDepen;
	}
	/**
	 * Asigna la variable  sinDepen
	 * 
	 * @param  sinDepen
	 * Variable a asignar en  sinDepen
	 */
	public void setSinDepen(boolean sinDepen) {
		this.sinDepen = sinDepen;
	}
	/**
	 * Retorna la variable txtFecha
	 * 
	 * @return  txtFecha
	 */
	public Date getTxtFecha() {
		return txtFecha;
	}
	/**
	 * Asigna la variable  txtFecha
	 * 
	 * @param  txtFecha
	 * Variable a asignar en  txtFecha
	 */
	public void setTxtFecha(Date txtFecha) {
		this.txtFecha = txtFecha;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	public boolean isSaldoLibros() {
		return saldoLibros;
	}
	public void setSaldoLibros(boolean saldoLibros) {
		this.saldoLibros = saldoLibros;
	}

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
