/*-
 * SubeConceptosHistControlador.java
 *
 * 1.0
 * 
 * 19/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.enums.SubeConceptosHistControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
/**
 * @version 1.0, 19/10/2023
 * @author Rent_16
 */
@ManagedBean
@ViewScoped
public class  SubeConceptosHistControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//private final String modulo;
	private final String proceso;
	private String opcion;
	private String anio;
	private String mes;
	private String periodo;
	private String tituloBoton;
	/**
	 * Este atributo se usa como auxiliar del componente referencia de
	 * archivos selectorArchivo y funciona como contenedor del archivo que se
	 * desea cargar
	 */
	private UploadedFile archivoCargaselectorArchivo;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private List<Registro> listaAnio;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;
	
	@EJB
	private EjbNominaSeisRemote ejbNominaSeisRemote;
	
	@EJB
    private EjbNominaCeroGeneralRemote ejbNominaCero;
	private boolean activo;
	private boolean errorFecha = false;
	private String obs;
	boolean permisosAdmin  = false;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de SubeConceptosHistControlador
	 */
	public SubeConceptosHistControlador() {
		super();
		compania = SessionUtil.getCompania();
		//modulo = SessionUtil.getModulo();
		proceso = (String) SessionUtil.getSessionVar("procesoNomina");
		try {
			//2428
		numFormulario = GeneralCodigoFormaEnum.SUBE_CONCEPTOS_HIST_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(SubeNovedadesControlador.class.getName())
			.log(Level.SEVERE, null, ex);
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
		cargarListaAnio();
		cargarListaMes();
		cargarListaPeriodo();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
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
		// <CODIGO_DESARROLLADO>
		opcion = "1";
		anio = (String) SessionUtil.getSessionVar("anioNomina");;
		cargarListaMes();
		mes =  (String) SessionUtil.getSessionVar("mesNomina");
		cargarListaPeriodo();
		periodo = (String) SessionUtil.getSessionVar("periodoNomina");
		
		activo = Boolean
                .parseBoolean(SysmanFunciones.nvl(
                                SessionUtil.getSessionVar(
                                                "periodoActivo"),
                                "false").toString());
		tituloBoton = "Crear";
		
		
		// </CODIGO_DESARROLLADO>

	}
	//<CODIGO_DESARROLLADO>
	/*
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
    /**

	 */
	public void cargarListaAnio(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubeConceptosHistControladorUrlEnum.URL5617
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaMes
	 */
	public void cargarListaMes(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubeConceptosHistControladorUrlEnum.URL6298
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * Carga la lista listaPeriodo
	 */
	public void cargarListaPeriodo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		try {
			listaPeriodo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubeConceptosHistControladorUrlEnum.URL7130
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton SubirConc
	 * en la vista
	 *
	 */
	public void oprimirSubirConc() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if ("1".equals(opcion)) {
			generarEstructura();
		}
		else {
			if (!estadoNomina()) {
				JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2550"));

                return;
            }

			 if ("".equals(archivoCargaselectorArchivo.getFileName())) {
		            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1901"));
		            return;
		        }
			
			subirExcel();
		}
		//</CODIGO_DESARROLLADO>
	}

	public void generarEstructura() {

		try {
			// Crear un nuevo libro de Excel
			Workbook workbook = new HSSFWorkbook();

			// Crear una hoja en el libro
			Sheet sheet = workbook.createSheet("Estructura");

			// Crear una fila para el encabezado
			Row headerRow = sheet.createRow(0);


			Font fuente = workbook.createFont();
			fuente.setBold(true);

			CellStyle estiloFuente = workbook.createCellStyle();
			estiloFuente.setFont(fuente);
			estiloFuente.setAlignment(HorizontalAlignment.CENTER); // Centrar el texto
			estiloFuente.setBorderTop(BorderStyle.MEDIUM);
			estiloFuente.setBorderBottom(BorderStyle.MEDIUM);
			estiloFuente.setBorderLeft(BorderStyle.MEDIUM);
			estiloFuente.setBorderRight(BorderStyle.MEDIUM);
			estiloFuente.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			estiloFuente.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			DataFormat dataFormat = workbook.createDataFormat();
			estiloFuente.setDataFormat(dataFormat.getFormat("TEXT"));
			

			Cell cell1 = headerRow.createCell(0);
			cell1.setCellValue("ID EMPLEADO");
			sheet.autoSizeColumn(0);

			Cell cell2 = headerRow.createCell(1);
			cell2.setCellValue("NOMBRE DEL EMPLEADO");
			sheet.autoSizeColumn(1);

			Cell cell3 = headerRow.createCell(2);
			cell3.setCellValue("ID CONCEPTO");
			sheet.autoSizeColumn(2);

			Cell cell4 = headerRow.createCell(3);
			cell4.setCellValue("NOMBRE DEL CONCEPTO");
			sheet.autoSizeColumn(3);

			Cell cell5 = headerRow.createCell(4);
			cell5.setCellValue("FECHA");
			sheet.autoSizeColumn(4);
			cell5.setCellStyle(estiloFuente);
			
			

			Cell cell6 = headerRow.createCell(5);
			cell6.setCellValue("VALOR - Usar punto como separador decimal");
			sheet.autoSizeColumn(5);

			Cell cell7 = headerRow.createCell(6);
			cell7.setCellValue("MANUAL SI/NO");
			sheet.autoSizeColumn(6);

			Cell cell8 = headerRow.createCell(7);
			cell8.setCellValue("OBSERVACIONES");
			sheet.autoSizeColumn(7);        

			for (int i = 0; i < 8; i++) {
				headerRow.getCell(i).setCellStyle(estiloFuente);
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();


			// Escribir el libro en el archivo
			workbook.write(out);

			out.close();
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(out.toByteArray()),
					"InformeBase.xls");


		} catch ( IOException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void subirExcel() {
		boolean errorConversion;
		try {
			
			String extension = archivoCargaselectorArchivo.getFileName()
					.substring(archivoCargaselectorArchivo.getFileName()
							.lastIndexOf('.') + 1);
			if ("xls".equals(extension)) {

				Workbook workbook = new HSSFWorkbook(
						archivoCargaselectorArchivo.getInputstream());
				Sheet sheet = workbook.getSheetAt(0);

				int rowStart = 1;
				int rowEnd = sheet.getLastRowNum();

				for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
					errorConversion = false;
					ejecutarFuncionExcel(sheet, rowNum, errorConversion);

				}
				ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
				workbook.write(fileOut);

				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(fileOut.toByteArray()),
						"historicos.xls");
				fileOut.close();
				//  ejecutado = true;

			}
			else {
				if ("".equals(archivoCargaselectorArchivo.getFileName())) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
				}
				else {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2679"));
				}

			}

		}
		catch (JRException | IOException ex) {
			Logger.getLogger(SubeNovedadesControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(
					SysmanFunciones.concatenar(
							idioma.getString(
									"MSM_TRANS_INTERRUMPIDA"),
							ex.getMessage()));
		}

	}

	public void ejecutarFuncionExcel(Sheet sheet, int rowNum,
			boolean errorConversion) {
		try {
		String aux = "!"; 
		boolean error = true;
		int idEmpleado = 0;
		int idConcepto = 0;
		Date fechaC = null;		
		double valor = 0;
		Cell cellError = null;
		Date fecha = null;
		boolean errorNumero = false;
		
		Row row = sheet.getRow(rowNum);
		if (sheet.getRow(rowNum) != null) {
		int lastColumn = 5;

		for (int cn = 0; cn <= lastColumn; cn++) {
			Cell cell = row.getCell(cn);
		 if (cell != null && !(cell.getCellType() == Cell.CELL_TYPE_BLANK && cell.toString().isEmpty())) {
			switch (cn) {
			case 0:
				idEmpleado = (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
				? (int) cell.getNumericCellValue()
						: Integer.parseInt(cell.getStringCellValue());
				break;
			case 2:
				idConcepto = (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
						? (int) cell.getNumericCellValue()
								: Integer.parseInt(cell.getStringCellValue());
				break;
			case 4:
				fechaC = (cell.getCellType() == Cell.CELL_TYPE_STRING)?
						SysmanFunciones.convertirAFecha(cell.getStringCellValue())
						: cell.getDateCellValue();
						
				fecha = compararFecha(fechaC);
			    
				break;
			case 5:
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			        // Si es numérico, obtener como cadena y procesar el formato
					valor = cell.getNumericCellValue();
			        
			    } else {
			        // Si no es numérico, asumir que es una cadena y procesar el formato
			        valor = parseNumero(cell.getStringCellValue());
			        errorNumero = true;
			    }
			    break;
			default:
				break;
			}
		}else {			
			error = false;
		}
		
		
		}
		
		if(error && !errorNumero) {
				aux = ejbNominaSeisRemote.subirCnHistoricos(compania, Integer.parseInt(proceso), 
						Integer.parseInt(anio), 
						Integer.parseInt(mes), 
						Integer.parseInt(periodo), 
						idEmpleado, 
						idConcepto, 
						valor, 
						fecha,
						obs,
						SessionUtil.getUser().getCodigo());
		        }

		cellError = row.getCell(7) == null ? row.createCell(7) : row.getCell(7);
		cellError.setCellType(Cell.CELL_TYPE_STRING);
		Cell cell1 = row.getCell(6) == null ? row.createCell(6) : row.getCell(6);
		cell1.setCellType(Cell.CELL_TYPE_STRING);

		if (aux.startsWith("OK") && error) {
			
			if(errorFecha) {
				String alerta = aux.substring(3);
				cellError.setCellValue("**Carga Masiva.Cargado exitosamente** - Fecha errada, se cambió por la fecha final del periodo. La fecha ingresada se guardó en observaciones de la tabla históricos, "+ alerta);
			}
			else {
				String alerta = aux.substring(3);
				cellError.setCellValue("**Carga Masiva.Cargado exitosamente** " + alerta);
			}
			cell1.setCellValue("SI");
			sheet.autoSizeColumn(7);
			
		}
		else {
			if(error) {
			cellError.setCellValue(aux);
			cell1.setCellValue("NO");
			
			if(errorNumero) {
				cellError.setCellValue("**Carga Masiva**. Error – Revise el valor, el separador decimal es el punto y el separador de miles es la coma.");
				cell1.setCellValue("NO");
			} 
			}else {
				cellError.setCellValue("No puede haber campos vacíos. Por favor rellene todos los campos.");
				cell1.setCellValue("NO");
			}
			sheet.autoSizeColumn(7);
		}
		}
		errorFecha = false; 
		obs = null; 
		}
		catch ( NumberFormatException | SystemException | ParseException  e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	private double parseNumero(String valorStr) {
		try {
			
			valorStr = valorStr.replaceFirst("\\.", "");
			//Verificar si la coma es el separador de miles o decimal
	        if (valorStr.contains(",")) {
	            // Conservar la coma si es el separador de miles
	            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
	            return format.parse(valorStr).doubleValue();
	            
	        } else {
	            // Reemplazar la coma por punto si es el separador decimal
	            return Double.parseDouble(valorStr.replace(',', '.'));
	        }
	    } catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
	        return 0.0;
	    }
	}

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 *      
	 */
	public void cambiarAnio() {
		//<CODIGO_DESARROLLADO>
		mes = null;
		periodo = null;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes
	 */
	public void cambiarMes() {
		//<CODIGO_DESARROLLADO>
		periodo = null;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control TipoArchivo
	 */
	public void cambiarTipoArchivo() {
		//<CODIGO_DESARROLLADO>
		if ("1".equals(opcion)) {
			tituloBoton = "Crear";
		}
		else {
			tituloBoton = "Subir";
		}

		//hacer llamado del metodo y compararlo si es igual o equals a 9 para poder habilitar el boton

		boolean nivel = validarPermisosProceso();

		if ((nivel && opcion.equals("3")) || opcion.equals("1")){
			permisosAdmin = false;
				
		} else {
			permisosAdmin = true;
			JsfUtil.agregarMensajeAlerta("Señor usuario, usted no posee los permisos de administrador suficientes para ejecutar este proceso.");

		}		
	}

		//</CODIGO_DESARROLLADO>
	
	public boolean estadoNomina() {
		
		boolean periodoActivo = false;
		
		try {
			 periodoActivo = ejbNominaCero
			        .validarPeriodoActivoNomina(compania,
			                        Integer.parseInt(proceso),
			                        Integer.parseInt(anio),
			                        Integer.parseInt(mes),
			                        Integer.parseInt(periodo));
		} catch (NumberFormatException | SystemException e) {			
			JsfUtil.agregarMensajeError(e.getMessage());
	         logger.error(e.getMessage(), e);;
		}
		
		return periodoActivo; 
	}
	
	
	public boolean validarPermisosProceso() {
		
		String user = SessionUtil.getUser().getCodigo();			
			
			Map<String, Object> param = new HashMap<>();
			param.put("USUARIO", user);
			boolean nivel= false;

			try {
				List<Registro> rsUsuario = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										SubeConceptosHistControladorUrlEnum.URL004.getValue())
								.getUrl(),
								param));

				if (rsUsuario != null) {
					
					for (Registro campo : rsUsuario) {
					    if ("ADMIN_NOMINA".equals(campo.getCampos().get("GRUPO"))) {
					       nivel = true;
					       return nivel;
					    }
					}
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}  
			return nivel;
		}					
	
	
	public Date compararFecha(Date fechaC) {
		Date fecha = null;
		try {
		
		Date fechaIni = SysmanFunciones.convertirAFecha("01/"+ mes+ "/" + anio);
		Date fechaFin = SysmanFunciones.ultimoDiaDate(fechaIni);
		
		
		if (fechaC.equals(fechaIni) || fechaC.equals(fechaFin) ||
	            (fechaC.after(fechaIni) && fechaC.before(fechaFin))) {
	            fecha = fechaC;
	            
	        } else {
	        	errorFecha = true;
	            fecha = fechaFin;
	            obs = "Fecha Errada: " + SysmanFunciones.convertirAFechaCadena(fechaC);
	        }
		
		
		} catch (ParseException e) {
			 JsfUtil.agregarMensajeError(e.getMessage());
	         logger.error(e.getMessage(), e);
		}
		return fecha;
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcion
	 * 
	 * @return  opcion
	 */
	public String getOpcion() {
		return opcion;
	}
	/**
	 * Asigna la variable  opcion
	 * 
	 * @param  opcion
	 * Variable a asignar en  opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable periodo
	 * 
	 * @return  periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * Asigna la variable  periodo
	 * 
	 * @param  periodo
	 * Variable a asignar en  periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	/**
	 * Retorna el objeto contArchivoselectorArchivo
	 * 
	 * @return contArchivoselectorArchivo
	 */
	public UploadedFile getArchivoCargaselectorArchivo() {
		return archivoCargaselectorArchivo;
	}
	/**
	 * Asigna el objeto contArchivoselectorArchivo
	 * 
	 * @param contArchivoselectorArchivo
	 * Variable a asignar en contArchivoselectorArchivo
	 */
	public void setArchivoCargaselectorArchivo(UploadedFile archivoCargaselectorArchivo) {
		this.archivoCargaselectorArchivo = archivoCargaselectorArchivo;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}
	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}
	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}
	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}
	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo
	 * Variable a asignar en  listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}
	/**
	 * @return the tituloBoton
	 */
	public String getTituloBoton() {
		return tituloBoton;
	}
	/**
	 * @param tituloBoton the tituloBoton to set
	 */
	public void setTituloBoton(String tituloBoton) {
		this.tituloBoton = tituloBoton;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @return the activo
	 */
	public boolean isActivo() {
		return activo;
	}
	/**
	 * @param activo the activo to set
	 */
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	/**
	 * @return the permisosAdmin
	 */
	public boolean isPermisosAdmin() {
		return permisosAdmin;
	}
	/**
	 * @param permisosAdmin the permisosAdmin to set
	 */
	public void setPermisosAdmin(boolean permisosAdmin) {
		this.permisosAdmin = permisosAdmin;
	}
	
	
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
