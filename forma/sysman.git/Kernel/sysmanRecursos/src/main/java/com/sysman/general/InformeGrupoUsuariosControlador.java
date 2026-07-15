/*-
 * InformeGrupoUsuariosControlador.java
 *
 * 1.0
 * 
 * 26/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar 3 excel dependiendo de la opcion que se seleccione.
 *
 * @version 1.0, 26/10/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  InformeGrupoUsuariosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo ;
	private String reporte ;

	private String listaUserGrupo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Crea una nueva instancia de InformeGrupoUsuariosControlador
	 */
	public InformeGrupoUsuariosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.INFORME_GRUPO_USUARIOS_CONTROLADOR.getCodigo();
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
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EXCEL
	 * en la vista
	 *
	 *
	 */
	public void oprimirEXCEL() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;            
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}

	@SuppressWarnings("deprecation")
	public void generarInforme(ReportesBean.FORMATOS formato) 
	{
		if("1".equals(listaUserGrupo)) 
		{
			//opciones menu
			reporte="800201OpcionesMenu";
		} 
		else if ("2".equals(listaUserGrupo))
		{
			//grupo menu
			reporte="800202PorGrupo";
		}
		else if ("3".equals(listaUserGrupo))
		{
			//Usuario
			reporte="800203PorUsuario";
		}

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);

			String sql = Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),reemplazar);


			Workbook workbook = new XSSFWorkbook(
					JsfUtil.exportarHojaDatosStreamed(sql,
							ConectorPool.ESQUEMA_SYSMANK,
							formato).getStream());

			Sheet sheet = workbook.getSheetAt(0);

			sheet.shiftRows(0, sheet.getLastRowNum(),1);

			sheet.createFreezePane(0,2);

			Font font2 = workbook.createFont();
			font2.setFontName("Calibri");
			font2.setFontHeightInPoints((short) 11);
			font2.setBold(false);

			CellStyle style2 = workbook.createCellStyle();
			style2.setAlignment(CellStyle.ALIGN_LEFT);
			style2.setFont(font2);
			style2.setBorderBottom(CellStyle.BORDER_THIN);
			style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style2.setBorderLeft(CellStyle.BORDER_THIN);
			style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style2.setBorderTop(CellStyle.BORDER_THIN);
			style2.setTopBorderColor(IndexedColors.BLACK.getIndex());
			style2.setBorderRight(CellStyle.BORDER_THIN);
			style2.setRightBorderColor(IndexedColors.BLACK.getIndex());
			
			int jc = 0;
			Row r = sheet.createRow(0);
            
			if (reporte.equals("800201OpcionesMenu"))
			{
				Cell cell1 = r.createCell(0);
			
				cell1 = r.createCell(1);
				cell1.setCellValue("LISTADO DE OPCIONES DE MENU");
				
				jc = 3 ;

			}
			else if (reporte.equals("800202PorGrupo"))
			{
				Cell cell1 = r.createCell(0);
				
				cell1 = r.createCell(2);
				cell1.setCellValue("LISTADO DE GRUPOS");
				
				jc = 10;
				
			}
			else if (reporte.equals("800203PorUsuario"))
			{
				Cell cell1 = r.createCell(0);
				
				cell1 = r.createCell(1);
				cell1.setCellValue("LISTADO DE USUARIO");
				
				jc = 4;
			}
			
			for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
				Row r3 = sheet.getRow(i);
				for (int j = 0; j < jc; j++) {
					Cell cell2 = r3.getCell(j);
					cell2.setCellStyle(style2);
				}
			}
			workbook.write(out);

			out.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(out.toByteArray()),"InformeGruposUsuarios.xlsx");
			workbook.close();

		}
		catch (JRException | IOException | SQLException | DRException
				| SysmanException | NumberFormatException
				 e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}


	/**
	 * Retorna la variable listaUserGrupo
	 * 
	 * @return  listaUserGrupo
	 */
	public String getListaUserGrupo() {
		return listaUserGrupo;
	}
	/**
	 * Asigna la variable  listaUserGrupo
	 * 
	 * @param  listaUserGrupo
	 * Variable a asignar en  listaUserGrupo
	 */
	public void setListaUserGrupo(String listaUserGrupo) {
		this.listaUserGrupo = listaUserGrupo;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	public String getCompania() {
		return compania;
	}
	public String getModulo() {
		return modulo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
