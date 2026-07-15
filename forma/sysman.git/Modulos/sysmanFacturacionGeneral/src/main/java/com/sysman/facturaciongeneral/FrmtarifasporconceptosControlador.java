/*-
 * FrmtarifasporconceptosControlador.java
 *
 * 1.0
 * 
 * 15/12/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.FrmtarifasporconceptosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.FrmtarifasporconceptosControladorEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/12/2023
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmtarifasporconceptosControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String tipoCobro;
	private String ano;
	private String concepto;
	private String numeroLista;
	private String cadena;
	private String auxiliar;
	private int contador;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */

	private StreamedContent archivoDescarga;

	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;

	private List<Registro> listaAno;
	private List<Registro> listaconceptos;

	private RegistroDataModelImpl listaconcepto;
	private RegistroDataModelImpl listaconceptoE;
	private RegistroDataModelImpl listanumerolista;
	private RegistroDataModelImpl listanumerolistaE;

	@EJB
	private EjbFacturacionGeneralCuatroRemote ejbFactGenCuatro;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmtarifasporconceptosControlador
	 */
	public FrmtarifasporconceptosControlador() {
		super();
		compania = SessionUtil.getCompania();
		ano = SessionUtil.getSessionVar(FrmtarifasporconceptosControladorEnum.ANIO.getValue()).toString();
		contArchivocargarExcel = new ContenedorArchivo();
		tipoCobro = SessionUtil.getSessionVar(FrmtarifasporconceptosControladorEnum.TIPOCOBRO.getValue()).toString();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_TARIFAS_POR_CONCEPTOS_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.TARIFAS_POR_CONCEPTO;
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		cargarListaAno();
		cargarListaconcepto();
		cargarListaconceptoE();
		cargarListanumerolista();
		cargarListanumerolistaE();
		abrirFormulario();
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put("TIPOCOBRO", tipoCobro);
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmtarifasporconceptosControladorUrlEnum.URL001.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaconcepto
	 *
	 * 
	 */
	public void cargarListaconcepto() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOCOBRO", tipoCobro);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmtarifasporconceptosControladorUrlEnum.URL002.getValue());

		listaconcepto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaconcepto
	 *
	 * 
	 */
	public void cargarListaconceptoE() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOCOBRO", tipoCobro);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmtarifasporconceptosControladorUrlEnum.URL002.getValue());

		listaconceptoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listanumerolista
	 *
	 */
	public void cargarListanumerolista() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmtarifasporconceptosControladorUrlEnum.URL006.getValue());

		listanumerolista = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listanumerolista
	 *
	 */
	public void cargarListanumerolistaE() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmtarifasporconceptosControladorUrlEnum.URL006.getValue());

		listanumerolistaE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 *
	 */
	public void oprimirCargar() {
		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;
		try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo());) {

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheet("Plantilla");

				for (Row row : sheet) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}

					capturaDatosExcel(row);
				}
				cadena = cadena + "')";
				cargarDatos();
			}
		} catch (IOException | NumberFormatException | NullPointerException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

	}

	/**
	 * Verifica el valor de la celda y retorna false si esta vacia.
	 * 
	 * @param celda Objeto de tipo <code>Cell</code>
	 * @return false si la celda esta vacia.
	 */
	private boolean validarCelda(Cell celda) {
		if (celda == null) {
			return false;
		}
		return true;
	//	return !celda.getStringCellValue().isEmpty();
		
	}

	private void capturaDatosExcel(Row row) {
	   
	        if (row.getRowNum() > 0) {

			for (int i = 0; i < 6; i++) {
				Object val = row.getCell(i) + "";			
				if (val.equals("") || val == null || val.equals("null")) {					
						val = "NoDato";
				}
				else
				{
					val =  row.getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC ? NumberToTextConverter.toText(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue();					
				}	
				
				contador = contador + val.toString().length();
                                
				if (contador >= 3000) {
				    cadena = cadena + "') || TO_CLOB('";
				    contador = 0; 
				}
				
				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			
			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}

	}

	private void cargarDatos() {

		try {
			String parametro = (SysmanFunciones.esBdSqlServer()) ? cadena.replace("TO_CLOB(", "").replace(")", "")
					: cadena;

			ejbFactGenCuatro.cargarTarifasConceptos(compania, parametro, SessionUtil.getUser().getCodigo());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {

		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Crear en la vista
	 * 
	 * @throws IOException
	 *
	 *
	 */
	public void oprimirCrear() throws IOException {
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			HSSFSheet excelSheet = workbook.createSheet("Plantilla");

			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Arial");
			font.setBold(true);

			// Tama�o de letra
			font.setFontHeightInPoints((short) 10);

			/* Estilo encabezado */
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);
			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row row = excelSheet.createRow(0);
			Cell cell = row.createCell(0);

			row = excelSheet.createRow(0);
	
			cell = row.createCell(0);
			cell.setCellValue("ANO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("CONCEPTO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("NUMER DE LISTA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("VALOR");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("FECHA INICIAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(4);

			cell = row.createCell(5);
			cell.setCellValue("FECHA FINAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(5);

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaconceptos = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmtarifasporconceptosControladorUrlEnum.URL007.getValue())
									.getUrl(),
							param));
			int rowIndex = 1;
			for (Registro option : listaconceptos) 
			{
				 row = excelSheet.createRow(rowIndex++);
				 cell = row.createCell(0);
				 cell.setCellValue(option.getCampos().get("ANO").toString());
				 
				 cell = row.createCell(1);					 
				 cell.setCellValue(option.getCampos().get("CONCEPTO").toString());
				 
				 cell = row.createCell(2);					 
				 cell.setCellValue(option.getCampos().get("NUMEROLISTA").toString());
				 
				 cell = row.createCell(3);					 
				 cell.setCellValue(option.getCampos().get("VALOR").toString());

				 cell = row.createCell(4);				 
				 if(option.getCampos().get("FECHAINICIAL") == null) {
					 cell.setCellValue("");
				 }else {
					 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					 String fechaTexto = formatter.format(option.getCampos().get("FECHAINICIAL"));
					 cell.setCellValue(fechaTexto );
				 }
				 cell = row.createCell(5);
				 if(option.getCampos().get("FECHAFINAL") == null) {
					 cell.setCellValue("");
				 }else {
					 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");					 	
					 String fechaTexto = formatter.format(option.getCampos().get("FECHAFINAL"));
					 cell.setCellValue(fechaTexto );
				 }
			}
			
			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla_tarifas_conceptos.xls");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (IOException | JRException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
			e.printStackTrace();
		} finally {
			workbook.close();
		}

		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconcepto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconcepto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO", registroAux.getCampos()
                 .get(GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconcepto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconceptoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanumerolista
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanumerolista(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NUMEROLISTA", registroAux.getCampos()
                .get(GeneralParameterEnum.CODIGO.getName()));
//		numeroLista = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanumerolista
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanumerolistaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}

//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
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
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {

		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);

	/*	registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);

		registro.getCampos().put("CONCEPTO", concepto);
		
		registro.getCampos().put("NUMEROLISTA", numeroLista);*/

		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove("ano");
		registro.getCampos().remove("numeroLista");
		registro.getCampos().remove("concepto");
		
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {

	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable concepto
	 * 
	 * @return concepto
	 */
	public String getConcepto() {
		return concepto;
	}

	/**
	 * Asigna la variable concepto
	 * 
	 * @param concepto Variable a asignar en concepto
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	/**
	 * Retorna la variable numeroLista
	 * 
	 * @return numeroLista
	 */
	public String getNumeroLista() {
		return numeroLista;
	}

	/**
	 * Asigna la variable numeroLista
	 * 
	 * @param numeroLista Variable a asignar en numeroLista
	 */
	public void setNumeroLista(String numeroLista) {
		this.numeroLista = numeroLista;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna el objeto contArchivocargarExcel
	 * 
	 * @return contArchivocargarExcel
	 */
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	/**
	 * Asigna el objeto contArchivocargarExcel
	 * 
	 * @param contArchivocargarExcel Variable a asignar en contArchivocargarExcel
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaconcepto
	 * 
	 * @return listaconcepto
	 */
	public RegistroDataModelImpl getListaconcepto() {
		return listaconcepto;
	}

	/**
	 * Asigna la lista listaconcepto
	 * 
	 * @param listaconcepto Variable a asignar en listaconcepto
	 */
	public void setListaconcepto(RegistroDataModelImpl listaconcepto) {
		this.listaconcepto = listaconcepto;
	}

	/**
	 * Retorna la lista listaconcepto
	 * 
	 * @return listaconcepto
	 */
	public RegistroDataModelImpl getListaconceptoE() {
		return listaconceptoE;
	}

	/**
	 * Asigna la lista listaconcepto
	 * 
	 * @param listaconcepto Variable a asignar en listaconcepto
	 */
	public void setListaconceptoE(RegistroDataModelImpl listaconceptoE) {
		this.listaconceptoE = listaconceptoE;
	}

	/**
	 * Retorna la lista listanumerolista
	 * 
	 * @return listanumerolista
	 */
	public RegistroDataModelImpl getListanumerolista() {
		return listanumerolista;
	}

	/**
	 * Asigna la lista listanumerolista
	 * 
	 * @param listanumerolista Variable a asignar en listanumerolista
	 */
	public void setListanumerolista(RegistroDataModelImpl listanumerolista) {
		this.listanumerolista = listanumerolista;
	}

	/**
	 * Retorna la lista listanumerolista
	 * 
	 * @return listanumerolista
	 */
	public RegistroDataModelImpl getListanumerolistaE() {
		return listanumerolistaE;
	}

	/**
	 * Asigna la lista listanumerolista
	 * 
	 * @param listanumerolista Variable a asignar en listanumerolista
	 */
	public void setListanumerolistaE(RegistroDataModelImpl listanumerolistaE) {
		this.listanumerolistaE = listanumerolistaE;
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
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

}
