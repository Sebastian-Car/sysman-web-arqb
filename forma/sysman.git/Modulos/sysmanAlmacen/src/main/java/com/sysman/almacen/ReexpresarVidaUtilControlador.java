/*-
 * ReexpresarVidaUtil.java
 *
 * 1.0
 * 
 * 13/12/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.batik.css.engine.value.StringValue;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;


import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.almacen.enums.ReexpresarVidaUtilControladorUrlEnum;
import com.sysman.almacen.enums.RegistroDeterioroControladorEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
/**
 *
 * @version 1.0, 13/12/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  ReexpresarVidaUtilControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	private StreamedContent archivoDescarga;
	private ContenedorArchivo contArchivoPlantilla;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaElemento;
	private RegistroDataModelImpl listaElementoE;
	private RegistroDataModelImpl listaPlaca;
	private RegistroDataModelImpl listaPlacaE;
	
	@EJB
	private EjbAlmacenUnoRemote almacenUnoRemote;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	private	String salida;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ReexpresarVidaUtil
	 */
	public ReexpresarVidaUtilControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2440;
			validarPermisos();
			contArchivoPlantilla= new ContenedorArchivo();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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

		enumBase = GenericUrlEnum.REGISTRO_REEXPRESION;
		buscarLlave();
		reasignarOrigen();
		registro = new Registro(new HashMap<String, Object>());
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElemento(); cargarListaElementoE();
		cargarListaPlaca(); cargarListaPlacaE();
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElemento(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ReexpresarVidaUtilControladorUrlEnum.URL112154
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("COMPANIA", String.valueOf(compania));

		listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGOELEMENTO");
	}
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void  cargarListaElementoE(){
		listaElementoE = listaElemento;
	}
	/**
	 * 
	 * Carga la lista listaPlaca
	 *
	 */
	public void cargarListaPlaca(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ReexpresarVidaUtilControladorUrlEnum.URL112156
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos()
				.get(GeneralParameterEnum.ELEMENTO.getName()));

		listaPlaca = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());

	}
	/**
	 * 
	 * Carga la lista listaPlaca
	 *
	 */
	public void  cargarListaPlacaE(){
		listaPlacaE = listaPlaca;
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Elemento
	 * 
	 * 
	 */
	public void cambiarElemento() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control FechaCambio
	 * 
	 * 
	 */
	public void cambiarFechaCambio() {
		//<CODIGO_DESARROLLADO>
		try {

			double valorLibros = 0;
			double valorHist = 0;

			Date fecha = (Date) registro.getCampos()
					.get(RegistroDeterioroControladorEnum.FECHACAMBIO
							.getValue());
			String elemento = SysmanFunciones.toString(registro.getCampos()
					.get(GeneralParameterEnum.ELEMENTO.getName()));
			String serie = SysmanFunciones.toString(registro.getCampos()
					.get(GeneralParameterEnum.SERIE.getName()));


			fecha = SysmanFunciones.sumarRestarMesesFecha(fecha, -1);
			String fechaCambio = SysmanFunciones.convertirAFechaCadena(SysmanFunciones.ultimoDiaDate(fecha));

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

			param.put(GeneralParameterEnum.SERIE.getName(), serie);

			param.put("FECHA_CAMBIO", fechaCambio);


			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ReexpresarVidaUtilControladorUrlEnum.URL369
									.getValue())
							.getUrl(),
							param));

			if(rs != null) {
				valorLibros = Double.parseDouble(SysmanFunciones.nvl(rs.getCampos()
						.get("NIIF_VLRLIBROS"), "0").toString());

				valorHist = Double.parseDouble(SysmanFunciones.nvl(rs.getCampos()
						.get("NIIF_COSTOAJUSTADO"), "0").toString());
			}

			registro.getCampos().put(
					RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
					.getValue(), valorHist);

			registro.getCampos().put(
					RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
					.getValue(), valorLibros);


		} catch (ParseException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Elemento en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarElementoC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control FechaCambio en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarFechaCambioC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		try {

			double valorLibros = 0;
			double valorHist = 0;

			Date fecha = (Date) listaInicial.getDatasource().get(rowNum % 10).getCampos()
					.get(RegistroDeterioroControladorEnum.FECHACAMBIO
							.getValue());
			String elemento = SysmanFunciones.toString(listaInicial.getDatasource().get(rowNum % 10).getCampos()
					.get(GeneralParameterEnum.ELEMENTO.getName()));
			String serie = SysmanFunciones.toString(listaInicial.getDatasource().get(rowNum % 10).getCampos()
					.get(GeneralParameterEnum.SERIE.getName()));


			fecha = SysmanFunciones.sumarRestarMesesFecha(fecha, -1);
			String fechaCambio = SysmanFunciones.convertirAFechaCadena(SysmanFunciones.ultimoDiaDate(fecha));

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

			param.put(GeneralParameterEnum.SERIE.getName(), serie);

			param.put("FECHA_CAMBIO", fechaCambio);


			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ReexpresarVidaUtilControladorUrlEnum.URL369
									.getValue())
							.getUrl(),
							param));

			if(rs != null) {
				valorLibros = Double.parseDouble(SysmanFunciones.nvl(rs.getCampos()
						.get("NIIF_VLRLIBROS"), "0").toString());

				valorHist = Double.parseDouble(SysmanFunciones.nvl(rs.getCampos()
						.get("NIIF_COSTOAJUSTADO"), "0").toString());
			}

			listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
					RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
					.getValue(), valorHist);

			listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
					RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
					.getValue(), valorLibros);


		} catch (ParseException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElemento
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
				registroAux.getCampos().get("CODIGOELEMENTO"));

		registro.getCampos().put("FECHACAMBIO", null);
		registro.getCampos().put(
				"DETERIORO",
				"");
		registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), "");
		registro.getCampos()
		.put("NIIF_VALOR_TOTAL", "");
		registro.getCampos()
		.put("NIIF_VLRLIBROS", "");
		registro.getCampos().put(
				"VIDAUTIL",
				"");

		registro.getCampos().put(
				"NOMBRELARGO",
				registroAux.getCampos().get("NOMBRELARGO"));

		cargarListaPlaca();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElemento
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGOELEMENTO");
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPlaca
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlaca(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.SERIE.getName(),
				registroAux.getCampos()
				.get(GeneralParameterEnum.SERIE
						.getName()));

		registro.getCampos().put(
				RegistroDeterioroControladorEnum.NIIF_VALOR_TOTAL
				.getValue(), "");

		registro.getCampos().put(
				RegistroDeterioroControladorEnum.NIIF_VLRLIBROS
				.getValue(), "");

		registro.getCampos().put("ORIGEN", String.valueOf(registroAux.getCampos().get("ORIGEN")));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPlaca
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlacaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("SERIE");
	}
	//</METODOS_COMBOS_GRANDES>
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
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		registro.getCampos().put("HORA", new Date());
		//</CODIGO_DESARROLLADO>
		return validarCampos();

	}
	
	
	//validar que los campos no sean nulos 
	private boolean validarCampos() {
	    String[] campos = {"ELEMENTO", "NOMBRELARGO", "SERIE", "FECHACAMBIO", "VIDAUTIL"};
	    
	    for (String campo : campos) {
	        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)) {
	            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4337"));
	            return false;
	        }
	    }
	    
	    return true;
	}
	
	
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarPlantilla
	 * en la vista
	 *
	 * 
	 *
	 */
	public void oprimirGenerarPlantilla() {
		//<CODIGO_DESARROLLADO>
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();

			String sql= "SELECT '' ELEMENTO, '' NOMBRE_ELEMENTO, '"
					+ "' PLACA, '' FECHA, '' VIDA_UTIL FROM DUAL";

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed
					(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, "Seguimiento a tiempos");
		}
		catch (JRException | IOException | NumberFormatException  | DRException | SQLException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	
	/**
     * 
     * Metodo ejecutado al oprimir el boton ActualizarVlrHistorico
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */

	
	public void oprimirCargarPlantilla() {

		     archivoDescarga = subirExcel();
		    
		    JsfUtil.agregarMensajeInformativo("Proceso terminado correctamente");
	}
	
	private StreamedContent  subirExcel() {
	    Workbook workbook = null;
	    FileInputStream file = null;
	    StreamedContent descarga = null;  
	    try {
	    	
	    	String archivo = String.valueOf(contArchivoPlantilla.getArchivo());
	    	String extension = archivo.substring(archivo.indexOf('.'), archivo.length()).toLowerCase();

	        if ("".equals(contArchivoPlantilla.getArchivo())) {
	            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
	        } else {
	        	String rutaArchivo = contArchivoPlantilla.getArchivo().getPath();
    			file = new FileInputStream(new File(rutaArchivo));
    			
    			workbook = (".xls".equals(extension))
    						?new HSSFWorkbook(file)
    								:new XSSFWorkbook(file);

	            Sheet sheet = workbook.getSheetAt(0);
	            int numFilas = sheet.getPhysicalNumberOfRows();
	            for (int i = 1; i < numFilas; i++) {
	                Row row = sheet.getRow(i);
	                if (row != null) {
	                    procesarCelda(row);
	                }
	            }
	            sheet.autoSizeColumn(5); 
	            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
	            workbook.write(fileOut);
	            descarga = JsfUtil.getArchivoDescarga(
	            	    new ByteArrayInputStream(fileOut.toByteArray()),
	            	    "hoja1." + extension);
	            fileOut.close();
	            workbook.close();
	        }
	    } catch (JRException | IOException ex) {
	    	logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
	    }
		return descarga;
	}

	private void procesarCelda(Row row) {
	    String elemento = null;
	    String nomElemento = null;
	    int placa = 0;
	    Date fecha = null;
	    int vidaUtil = 0;

	    for (int j = 0; j < 5; j++) {
	        Cell cell = row.getCell(j);
	        switch (j) {
	            case 0:
	                elemento = String.valueOf(obtenerValorCeldaNum(cell));
	                break;
	            case 1:
	                nomElemento = obtenerValorCelda(cell);
	                break;
	            case 2:
	                placa = obtenerValorCeldaNum(cell);
	                break;
	            case 3:
				try {
					fecha = obtenerFechaCelda(cell);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	                break;
	            case 4:
	                vidaUtil = obtenerValorCeldaNum(cell);
	                break;
	            default:
	                break;
	        }
	        
	        
	        
	    }
	    
	    
	    
	    try {
	    	salida = almacenUnoRemote.cargarReexpVidaUtil
	    			(compania, elemento, (long)placa, fecha, String.valueOf(vidaUtil)
	    					, SessionUtil.getUser().getCodigo());
	    	Cell cell = row.createCell(5);
	        cell.setCellValue(salida);
	        
			
			
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	    
	}

	private String obtenerValorCelda(Cell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return "";
		} else {
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				return String.valueOf(cell.getNumericCellValue());
			} else {
				return cell.getStringCellValue();
			}
		}
	}
	private int obtenerValorCeldaNum(Cell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
	        return 0;
	    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
	        return (int) cell.getNumericCellValue();
	    } else {
	        try {
	            return Integer.parseInt(cell.getStringCellValue());
	        } catch (NumberFormatException e) {
	            return 0;
	        }
	    }
	}

	private Date obtenerFechaCelda(Cell cell) throws ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			String fechaActualFormateada = formato.format(new Date());
	        fecha = formato.parse(fechaActualFormateada);
		}else {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String fechaTxt = cell.getStringCellValue();
				fecha = formato.parse(fechaTxt);
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

				Date fechaCell = cell.getDateCellValue();
				String fechaFormateada = formato.format(fechaCell);

				fecha = formato.parse(fechaFormateada);
			}}
	    
	    return fecha;
	}




	
	
	  /**
     * 
     * Metodo ejecutado al oprimir el boton ActualizarVlrHistorico
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
	
public void oprimirActualizarVlrHistorico() {
         
			try{
				salida = almacenUnoRemote.
						actualizarVlrSaldo(compania);
				JsfUtil.agregarMensajeInformativo(
						idioma.getString(
								salida));
				
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

    }
	
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove(
				GeneralParameterEnum.COMPANIA
				.getName());
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
	}
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}
	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento
	 * Variable a asignar en  listaElemento
	 */
	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElementoE() {
		return listaElementoE;
	}
	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento
	 * Variable a asignar en  listaElemento
	 */
	public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
		this.listaElementoE = listaElementoE;
	}
	/**
	 * Retorna la lista listaPlaca
	 * 
	 * @return listaPlaca
	 */
	public RegistroDataModelImpl getListaPlaca() {
		return listaPlaca;
	}
	/**
	 * Asigna la lista listaPlaca
	 * 
	 * @param listaPlaca
	 * Variable a asignar en  listaPlaca
	 */
	public void setListaPlaca(RegistroDataModelImpl listaPlaca) {
		this.listaPlaca = listaPlaca;
	}
	/**
	 * Retorna la lista listaPlaca
	 * 
	 * @return listaPlaca
	 */
	public RegistroDataModelImpl getListaPlacaE() {
		return listaPlacaE;
	}
	/**
	 * Asigna la lista listaPlaca
	 * 
	 * @param listaPlaca
	 * Variable a asignar en  listaPlaca
	 */
	public void setListaPlacaE(RegistroDataModelImpl listaPlacaE) {
		this.listaPlacaE = listaPlacaE;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	
	public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna el objeto contArchivoPlantilla
     * 
     * @return contArchivoPlantilla
     */
	
	public ContenedorArchivo getContArchivoPlantilla() {
        return contArchivoPlantilla;
    }
    /**
     * Asigna el objeto contArchivoPlantilla
     * 
     * @param contArchivoPlantilla
     * Variable a asignar en contArchivoPlantilla
     */
    public void setContArchivoPlantilla(ContenedorArchivo contArchivoPlantilla) {
        this.contArchivoPlantilla = contArchivoPlantilla;
    }
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
