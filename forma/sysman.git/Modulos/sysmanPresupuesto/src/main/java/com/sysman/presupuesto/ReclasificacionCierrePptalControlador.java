/*-
 * ReclacificacionCierrePptalControlador.java
 *
 * 1.0
 * 
 * 14/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.presupuesto.enums.ReclasificacionCierrePptalControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @version 1.0, 14/02/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ReclasificacionCierrePptalControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;
	// <DECLARAR_ATRIBUTOS>
	private int anio;
	private int anioCierre;
	private String nombreRubro;
	private String titulo;
	private String encabezado;
	private String tipoVigencia;
	private String tipoCierre;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>

	private List<Registro> listaAnio;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaResRubroPptal;
	private RegistroDataModelImpl listaResRubroPptalE;
	private RegistroDataModelImpl listaResFuentePptal;
	private RegistroDataModelImpl listaResFuentePptalE;
	private RegistroDataModelImpl listaResReferenciaPptal;
	private RegistroDataModelImpl listaResReferenciaPptalE;
	private RegistroDataModelImpl listaResCentroPptal;
	private RegistroDataModelImpl listaResCentroPptalE;
	private RegistroDataModelImpl listaResAuxilialPptal;
	private RegistroDataModelImpl listaResAuxilialPptalE;
	private RegistroDataModelImpl listaReoRubroPptal;
	private RegistroDataModelImpl listaReoRubroPptalE;
	private RegistroDataModelImpl listaReoFuentePptal;
	private RegistroDataModelImpl listaReoFuentePptalE;
	private RegistroDataModelImpl listaReoReferenciaPptal;
	private RegistroDataModelImpl listaReoReferenciaPptalE;
	private RegistroDataModelImpl listaReoCentroPptal;
	private RegistroDataModelImpl listaReoCentroPptalE;
	private RegistroDataModelImpl listaReoAuxiliarPptal;
	private RegistroDataModelImpl listaReoAuxiliarPptalE;

	private Map<String, Object> parametrosEntrada;

	private List<Registro> listaData;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;

	private String clase;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;

	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * SelecFile y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaSelecFile;
	
	@EJB
    private EjbPresupuestoTresGeneralRemote ejbPresupuestoTresGeneralRemote;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ReclacificacionCierrePptalControlador
	 */
	public ReclasificacionCierrePptalControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		
		contArchivocargarExcel = new ContenedorArchivo();
		
		parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null) {
			clase = (String) parametrosEntrada.get("clase");
			tipoVigencia = (String) parametrosEntrada.get("tipoVigencia");

			tipoCierre = (String) parametrosEntrada.get("tipoCierre");

			if (clase.equals("APROPIACION")) {
				titulo = idioma.getString("TB_TB4283");
				encabezado = idioma.getString("TB_TB4283").toUpperCase();
			} else if (clase.equals("CAJA")) {
				titulo = idioma.getString("TB_TB4282");
				encabezado = idioma.getString("TB_TB4282").toUpperCase();
			}
		}

		try {
			numFormulario = GeneralCodigoFormaEnum.RECLASIFICACION_CIERRE_PPTAL_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			
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

		tabla = "PLAN_PPTAL_CONFIG";
		anio = SysmanFunciones.ano(new Date());
		buscarLlave();
		registro = new Registro();

		// <CARGAR_LISTA>
		cargarListaAnio();
		reasignarOrigen();
		// </CARGAR_LISTA>

		// cargarListaResRubroPptal();
		cargarListaResRubroPptalE();
		// cargarListaResFuentePptal();
		cargarListaResFuentePptalE();
		// cargarListaResReferenciaPptal();
		cargarListaResReferenciaPptalE();
		// cargarListaResCentroPptal();
		cargarListaResCentroPptalE();
		// cargarListaResAuxilialPptal();
		cargarListaResAuxilialPptalE();
		// cargarListaReoRubroPptal();
		cargarListaReoRubroPptalE();
		// cargarListaReoFuentePptal();
		cargarListaReoFuentePptalE();
		// cargarListaReoReferenciaPptal();
		cargarListaReoReferenciaPptalE();
		// cargarListaReoCentroPptal();
		cargarListaReoCentroPptalE();
		// cargarListaReoAuxiliarPptal();
		cargarListaReoAuxiliarPptalE();

		// <CARGAR_LISTA_COMBO_GRANDE>

		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {

		anioCierre = anio + 1;
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

		parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);

		parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(), tipoVigencia);

		parametrosListado.put("TIPOCIERRE", tipoCierre);

		parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

		parametrosListado.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				registro.getCampos().get(GeneralParameterEnum.FUENTE_RECURSO.getName()));

		parametrosListado.put(GeneralParameterEnum.REFERENCIA.getName(),
				registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()));

		parametrosListado.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));

		parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

		parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()));

		parametrosListado.put(GeneralParameterEnum.AUXILIAR.getName(),
				registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()));

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL0002.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL1031.getValue());

	}

	/**
	 * Retorna la variable indice
	 * 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ReclasificacionCierrePptalControladorUrlEnum.URL0001.getValue())
									.getUrl(),
							param));

		} catch (SystemException e) {
			Logger.getLogger(SiifReportesControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaResRubroPptal() {

	}

	/**
	 * 
	 * Carga la lista listaResRubroPptal
	 *
	 * 
	 */
	public void cargarListaResRubroPptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL45031.getValue());

		listaResRubroPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaResFuentePptal
	 *
	 * 
	 */
	public void cargarListaResFuentePptal() {

	}

	/**
	 * 
	 * Carga la lista listaResFuentePptal
	 *
	 * 
	 */
	public void cargarListaResFuentePptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL34038.getValue());

		listaResFuentePptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaResReferenciaPptal
	 *
	 * 
	 */
	public void cargarListaResReferenciaPptal() {

	}

	/**
	 * 
	 * Carga la lista listaResReferenciaPptal
	 *
	 * 
	 */
	public void cargarListaResReferenciaPptalE() {
		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL13026.getValue());

		listaResReferenciaPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaResCentroPptal
	 *
	 * 
	 */
	public void cargarListaResCentroPptal() {

	}

	/**
	 * 
	 * Carga la lista listaResCentroPptal
	 *
	 * 
	 */
	public void cargarListaResCentroPptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL20040.getValue());

		listaResCentroPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaResAuxilialPptal
	 *
	 * 
	 */
	public void cargarListaResAuxilialPptal() {

	}

	/**
	 * 
	 * Carga la lista listaResAuxilialPptal
	 *
	 * 
	 */
	public void cargarListaResAuxilialPptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL23028.getValue());

		listaResAuxilialPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReoRubroPptal
	 *
	 * 
	 */
	public void cargarListaReoRubroPptal() {

	}

	/**
	 * 
	 * Carga la lista listaReoRubroPptal
	 *
	 * 
	 */
	public void cargarListaReoRubroPptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL45031.getValue());

		listaReoRubroPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReoFuentePptal
	 *
	 * 
	 */
	public void cargarListaReoFuentePptal() {

	}

	/**
	 * 
	 * Carga la lista listaReoFuentePptal
	 *
	 * 
	 */
	public void cargarListaReoFuentePptalE() {
		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL34038.getValue());

		listaReoFuentePptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReoReferenciaPptal
	 *
	 * 
	 */
	public void cargarListaReoReferenciaPptal() {

	}

	/**
	 * 
	 * Carga la lista listaReoReferenciaPptal
	 *
	 * 
	 */
	public void cargarListaReoReferenciaPptalE() {
		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL13026.getValue());

		listaReoReferenciaPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReoCentroPptal
	 *
	 * 
	 */
	public void cargarListaReoCentroPptal() {

	}

	/**
	 * 
	 * Carga la lista listaReoCentroPptal
	 *
	 * 
	 */
	public void cargarListaReoCentroPptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL20040.getValue());

		listaReoCentroPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReoAuxiliarPptal
	 *
	 * 
	 */
	public void cargarListaReoAuxiliarPptal() {

	}

	/**
	 * 
	 * Carga la lista listaReoAuxiliarPptal
	 *
	 * 
	 */
	public void cargarListaReoAuxiliarPptalE() {

		Map<String, Object> param = new HashMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioCierre);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReclasificacionCierrePptalControladorUrlEnum.URL23028.getValue());

		listaReoAuxiliarPptalE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Descargar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @throws IOException
	 *
	 */
	public void oprimirCrear() throws IOException {
		// <CODIGO_DESARROLLADO>
		setArchivoDescarga(null);
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			HSSFSheet excelSheet = workbook.createSheet("Plantilla");

			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Calibri");
			font.setBold(true);

			// TamaÃƒÂ±o de letra
			font.setFontHeightInPoints((short) 8);

			/* Estilo encabezado */
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);

			Row row = excelSheet.createRow(1);
			Cell cell = row.createCell(0);

			row = excelSheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("Rubro");
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("Nombre Rubro");
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("Fuente Recurso");
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("Referencia");
			excelSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("Centro Costo");
			excelSheet.autoSizeColumn(4);	
			
			cell = row.createCell(5);
			cell.setCellValue("Auxiliar");
			excelSheet.autoSizeColumn(5);	

			cell = row.createCell(6);
			cell.setCellValue("Saldo");
			excelSheet.autoSizeColumn(6);	
			
			cell = row.createCell(7);
			cell.setCellValue("Tipo Vigencia");
			excelSheet.autoSizeColumn(7);	
			
			cell = row.createCell(8);
			cell.setCellValue("Rubro Cierre");
			excelSheet.autoSizeColumn(8);	
			
			cell = row.createCell(9);
			cell.setCellValue("Fuente Recurso Cierre");
			excelSheet.autoSizeColumn(9);	
			
			cell = row.createCell(10);
			cell.setCellValue("Referencia Cierre");
			excelSheet.autoSizeColumn(10);	
			
			cell = row.createCell(11);
			cell.setCellValue("Centro Costo Cierre");
			excelSheet.autoSizeColumn(11);	
			
			cell = row.createCell(12);
			cell.setCellValue("Auxiliar Cierre");
			excelSheet.autoSizeColumn(12);	

			Map<String, Object> param = new HashMap<>(); //MPEREZ
	        try {
	        	param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
			
	        	param.put(GeneralParameterEnum.ANO.getName(), anio);
			
	        	param.put(GeneralParameterEnum.CLASE.getName(), clase);
			
	        	param.put(GeneralParameterEnum.VIGENCIA.getName(),
			            tipoVigencia);
			
	        	param.put("TIPOCIERRE", tipoCierre);
			
	        /*	param.put(GeneralParameterEnum.CODIGO.getName(),
			            registro.getCampos().get(
			                            GeneralParameterEnum.CODIGO.getName()));
			
	        	param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
			            registro.getCampos()
			                            .get(GeneralParameterEnum.FUENTE_RECURSO
			                                            .getName()));
			
	        	param.put(GeneralParameterEnum.REFERENCIA.getName(),
			            registro.getCampos().get(GeneralParameterEnum.REFERENCIA
			                            .getName()));
			
	        	param.put(GeneralParameterEnum.TERCERO.getName(), registro
			            .getCampos()
			            .get(GeneralParameterEnum.TERCERO.getName()));
			
	        	param.put(GeneralParameterEnum.SUCURSAL.getName(), registro
			            .getCampos()
			            .get(GeneralParameterEnum.SUCURSAL.getName()));
			
	        	param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
			            registro.getCampos()
			                            .get(GeneralParameterEnum.CENTRO_COSTO
			                                            .getName()));
			
	        	param.put(GeneralParameterEnum.AUXILIAR.getName(), registro
			            .getCampos()
			            .get(GeneralParameterEnum.AUXILIAR.getName()));*/
				
				listaData = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ReclasificacionCierrePptalControladorUrlEnum.URL0003.getValue())
										.getUrl(),
										param));

				int rowIndex = 1;
				for (Registro option : listaData) {
					 row = excelSheet.createRow(rowIndex++);
					 cell = row.createCell(0);
					 cell.setCellValue(option.getCampos().get("CODIGO").toString());
					 
					 cell = row.createCell(1);					 
					 cell.setCellValue(option.getCampos().get("NOMBRE_RUBRO").toString());
					 
					 cell = row.createCell(2);					 
					 cell.setCellValue(option.getCampos().get("FUENTE_RECURSO").toString());

					 cell = row.createCell(3);					 
					 cell.setCellValue(option.getCampos().get("REFERENCIA").toString());
			
					cell = row.createCell(4);					 
			 		cell.setCellValue(option.getCampos().get("CENTRO_COSTO").toString());
					 
					 cell = row.createCell(5);					 
					 cell.setCellValue(option.getCampos().get("AUXILIAR").toString());
					 
					 cell = row.createCell(6);					 
					 cell.setCellValue(option.getCampos().get("SALDO").toString());
					 
					 cell = row.createCell(7);					 
					 cell.setCellValue(option.getCampos().get("TIPOVIGENCIA").toString());	
				
					if(clase == "APROPIACION" || clase == "PASIVO")
					{
					 	 cell = row.createCell(8);	
						 if(option.getCampos().get("RES_CODIGO_CIERRE") == null) {
							 cell.setCellValue("");
						 }else {
							 cell.setCellValue(option.getCampos().get("RES_CODIGO_CIERRE").toString());
						 }
						 
						 cell = row.createCell(9);		
						 if(option.getCampos().get("RES_FUENTE_RECURSO_CIERRE") == null) {
							 cell.setCellValue("");
						 }else {
							 cell.setCellValue(option.getCampos().get("RES_FUENTE_RECURSO_CIERRE").toString());
						 }
						 
						 cell = row.createCell(10);	
						 if(option.getCampos().get("RES_REFERENCIA_CIERRE") == null) {
							 cell.setCellValue("");
						 }else {
							 cell.setCellValue(option.getCampos().get("RES_REFERENCIA_CIERRE").toString());
						 }
						 
						 cell = row.createCell(11);
						 if(option.getCampos().get("RES_CENTRO_COSTO_CIERRE") == null) {
							 cell.setCellValue("");
						 }else {
							 cell.setCellValue(option.getCampos().get("RES_CENTRO_COSTO_CIERRE").toString());
						 }
	
						 cell = row.createCell(12);
						 if(option.getCampos().get("RES_AUXILIAR_CIERRE") == null) {
							 cell.setCellValue("");
						 }else {
							 cell.setCellValue(option.getCampos().get("RES_AUXILIAR_CIERRE").toString());
						 }		
					}
					else
					{
						if(clase == "CAJA")
						{
							cell = row.createCell(8);	
							if(option.getCampos().get("RES_CODIGO_CIERRE") == null) 
							{
								cell.setCellValue("");
							}else 
							{
								cell.setCellValue(option.getCampos().get("RES_CODIGO_CIERRE").toString());
							}
							cell = row.createCell(9);		
							if(option.getCampos().get("RES_FUENTE_RECURSO_CIERRE") == null) 
							{
								cell.setCellValue("");
							}else {
								cell.setCellValue(option.getCampos().get("RES_FUENTE_RECURSO_CIERRE").toString());
							}
												 
							cell = row.createCell(10);	
							if(option.getCampos().get("RES_REFERENCIA_CIERRE") == null) {
								cell.setCellValue("");
							}else {
								cell.setCellValue(option.getCampos().get("RES_REFERENCIA_CIERRE").toString());
							}
												 
							cell = row.createCell(11);
							if(option.getCampos().get("RES_CENTRO_COSTO_CIERRE") == null) {
								cell.setCellValue("");
							}else {
								cell.setCellValue(option.getCampos().get("RES_CENTRO_COSTO_CIERRE").toString());
							}
							
							cell = row.createCell(12);
							if(option.getCampos().get("RES_AUXILIAR_CIERRE") == null) {
								cell.setCellValue("");
							}else {
							cell.setCellValue(option.getCampos().get("RES_AUXILIAR_CIERRE").toString());
							}
						}
					}
				}	
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        workbook.write(out);
	        
			setArchivoDescarga(JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Reclasificación.xls"));

		} catch (IOException | JRException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}
	}
	
	public void oprimirCargar() 
	{
		FileInputStream file = null;	
		archivoDescarga = null;
		String mensaje = "";
		String claseReserva = "";
		if(contArchivocargarExcel.getArchivo()==null)
		{
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3347"));
        }
		else
		{
	        try {	            
	                String rutaArchivo = contArchivocargarExcel.getArchivo()
	                                .getPath();
	                String extension = rutaArchivo
	                                .substring(rutaArchivo.indexOf('.'),
	                                                rutaArchivo.length())
	                                .substring(1, rutaArchivo.substring(
	                                                rutaArchivo.indexOf('.'),
	                                                rutaArchivo.length()).length());
	                file = new FileInputStream(new File(rutaArchivo));
	                Workbook workbook = null;
	
	                if (workbook == null) {
	                    if ("xls".equals(extension)) {
	                        workbook = new HSSFWorkbook(file);
	                    }
	                    else {
	                        workbook = new XSSFWorkbook(file);
	                    }   
	                }
	
	                StringBuilder cadena = new StringBuilder();
	                cadena.append("TO_CLOB('");
	                Sheet sheet = workbook.getSheetAt(0);
	                Row fila;
	                Cell celda;
	                int num = 0;
	                fila = sheet.getRow(0);
	                if(fila.getCell(0).getStringCellValue().equals("Rubro") && 
	                		fila.getCell(1).getStringCellValue().equals("Nombre Rubro") &&
	                		fila.getCell(2).getStringCellValue().equals("Fuente Recurso") &&
	                		fila.getCell(3).getStringCellValue().equals("Referencia") &&
	                		fila.getCell(4).getStringCellValue().equals("Centro Costo" ) &&
	                		fila.getCell(5).getStringCellValue().equals("Auxiliar") &&
	                		fila.getCell(6).getStringCellValue().equals("Saldo") &&
	                		fila.getCell(7).getStringCellValue().equals("Tipo Vigencia") &&
	                		fila.getCell(8).getStringCellValue().equals("Rubro Cierre") &&
	                		fila.getCell(9).getStringCellValue().equals("Fuente Recurso Cierre") &&
	                		fila.getCell(10).getStringCellValue().equals("Referencia Cierre") &&
	                		fila.getCell(11).getStringCellValue().equals("Centro Costo Cierre") &&
	                		fila.getCell(12).getStringCellValue().equals("Auxiliar Cierre"))
	                {                	
		                for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
		                    fila = sheet.getRow(i);
		                    for (int j = 0; j < 13; j++) {
		                        celda = fila.getCell(j);
		                        if (celda != null) {
		                            num = num
		                                + (celda.getCellType() == 1 ? celda.getStringCellValue()
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
		                
		                String cadenaCodigos  = (SysmanFunciones.esBdSqlServer())
		                        ? cadena.toString().replace("TO_CLOB(", "")
		                                .replace(")", "")
		                : cadena.toString();
		               
		               if(clase.equals("APROPIACION") || clase.equals("PASIVO"))
		               {
		            	   claseReserva = "RES";
		               }
		               else
		               {
		            	   if(clase.equals("CAJA"))
		            	   {
		            		   claseReserva = "REO";
		            	   }
		               }
		               String retorno = ejbPresupuestoTresGeneralRemote.cargarReclasificacionCierre
		            		   				(	compania,
		            		   					anio,
		            		   					claseReserva,
		            		   					cadenaCodigos,
		            		   					SessionUtil.getUser().getCodigo());             
		
						if(retorno.isEmpty() || retorno.equals(" "))
						{
							mensaje = "El archivo se cargó exitosamente.";
						}
						else
						{
							rutaArchivo = "C:\\opt\\ErroresAuxiliares.txt";
							File archivo = new File(rutaArchivo);
						    // Si el archivo no existe es creado
						    if (!archivo.exists()) 
						    {
						    	archivo.createNewFile();
						    }
						    FileWriter fw = new FileWriter(archivo);
						    BufferedWriter bw = new BufferedWriter(fw);
						    bw.write(retorno);				    
						    bw.close();
						    mensaje = "El archivo se cargó con errores."
						    		+ "Por favor revisar el archivo generado en la ruta: "+ rutaArchivo;
						}
		                
		                workbook.close();
		                
		                JsfUtil.agregarMensajeInformativo(mensaje);
	                }
	                else
	                {
	                	JsfUtil.agregarMensajeError("El archivo no cumple con la estructura requerida.");
	                }	                
	        }
	        catch (IOException | NumberFormatException | SystemException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	            e.printStackTrace(); 
	        } 
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		// <CODIGO_DESARROLLADO>
		reasignarOrigen();

		cargarListaResRubroPptalE();
		cargarListaReoRubroPptalE();

		cargarListaResFuentePptalE();
		cargarListaReoFuentePptalE();

		cargarListaResReferenciaPptalE();
		cargarListaReoReferenciaPptalE();

		cargarListaResCentroPptalE();
		cargarListaReoCentroPptalE();

		cargarListaResAuxilialPptalE();
		cargarListaReoAuxiliarPptalE();

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control RubroPptal en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarResRubroPptalC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");
		// Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
		// se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA",
		// "hola ");
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("RES_CODIGO_CIERRE", auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("ANO_CIERRE", anioCierre);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_RUBRO_RES", nombreRubro);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ReoRubroPptal en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarReoRubroPptalC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");
		// Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
		// se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA",
		// "hola ");
		// <CODIGO_DESARROLLADO>

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("REO_CODIGO_CIERRE", auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("ANO_CIERRE", anioCierre);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_RUBRO_REO", nombreRubro);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResRubroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResRubroPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("RES_CODIGO_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResRubroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResRubroPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();

		nombreRubro = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResFuentePptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResFuentePptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("RES_FUENTE_RECURSO_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResFuentePptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResFuentePptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResReferenciaPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResReferenciaPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("RES_REFERENCIA_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResReferenciaPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResReferenciaPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResCentroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResCentroPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("RES_CENTRO_COSTO_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResCentroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResCentroPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResAuxilialPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResAuxilialPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("RES_AUXILIAR_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResAuxilialPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResAuxilialPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoRubroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoRubroPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("REO_CODIGO_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoRubroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoRubroPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();

		nombreRubro = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoFuentePptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoFuentePptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("REO_FUENTE_RECURSO_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoFuentePptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoFuentePptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoReferenciaPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoReferenciaPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("REO_REFERENCIA_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoReferenciaPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoReferenciaPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoCentroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoCentroPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("REO_CENTRO_COSTO_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoCentroPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoCentroPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoAuxiliarPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoAuxiliarPptal(SelectEvent event) {
		/*
		 * Registro registroAux = (Registro) event.getObject();
		 * registro.getCampos().put("REO_AUXILIAR_CIERRE",
		 * registroAux.getCampos().get("CODIGO"));
		 */
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReoAuxiliarPptal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReoAuxiliarPptalE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
	}

	// </METODOS_COMBOS_GRANDES>
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
	 * Metodo ejecutado cuando se cancela la edicion del registro
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
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
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

		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove("NOMBRE_RUBRO");
		registro.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
		registro.getCampos().remove("NOMBRE_RUBRO_REO");
		registro.getCampos().remove("NOMBRE_RUBRO_RES");
		registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
		registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
		registro.getCampos().remove(GeneralParameterEnum.CENTRO_COSTO.getName());
		registro.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove("SALDO");
		registro.getCampos().remove("TIPOVIGENCIA");
		registro.getCampos().remove(GeneralParameterEnum.FUENTE_RECURSO.getName());

	}

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario
	 * 
	 *
	 * @param registro registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}

	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("compania", compania);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.CONFIGURACION_CIERRE_CONTROLADOR.getCodigo()));
		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public int getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
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
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaResRubroPptal
	 * 
	 * @return listaResRubroPptal
	 */
	public RegistroDataModelImpl getListaResRubroPptal() {
		return listaResRubroPptal;
	}

	/**
	 * Asigna la lista listaResRubroPptal
	 * 
	 * @param listaResRubroPptal Variable a asignar en listaResRubroPptal
	 */
	public void setListaResRubroPptal(RegistroDataModelImpl listaResRubroPptal) {
		this.listaResRubroPptal = listaResRubroPptal;
	}

	/**
	 * Retorna la lista listaResRubroPptal
	 * 
	 * @return listaResRubroPptal
	 */
	public RegistroDataModelImpl getListaResRubroPptalE() {
		return listaResRubroPptalE;
	}

	/**
	 * Asigna la lista listaResRubroPptal
	 * 
	 * @param listaResRubroPptal Variable a asignar en listaResRubroPptal
	 */
	public void setListaResRubroPptalE(RegistroDataModelImpl listaResRubroPptalE) {
		this.listaResRubroPptalE = listaResRubroPptalE;
	}

	/**
	 * Retorna la lista listaResFuentePptal
	 * 
	 * @return listaResFuentePptal
	 */
	public RegistroDataModelImpl getListaResFuentePptal() {
		return listaResFuentePptal;
	}

	/**
	 * Asigna la lista listaResFuentePptal
	 * 
	 * @param listaResFuentePptal Variable a asignar en listaResFuentePptal
	 */
	public void setListaResFuentePptal(RegistroDataModelImpl listaResFuentePptal) {
		this.listaResFuentePptal = listaResFuentePptal;
	}

	/**
	 * Retorna la lista listaResFuentePptal
	 * 
	 * @return listaResFuentePptal
	 */
	public RegistroDataModelImpl getListaResFuentePptalE() {
		return listaResFuentePptalE;
	}

	/**
	 * Asigna la lista listaResFuentePptal
	 * 
	 * @param listaResFuentePptal Variable a asignar en listaResFuentePptal
	 */
	public void setListaResFuentePptalE(RegistroDataModelImpl listaResFuentePptalE) {
		this.listaResFuentePptalE = listaResFuentePptalE;
	}

	/**
	 * Retorna la lista listaResReferenciaPptal
	 * 
	 * @return listaResReferenciaPptal
	 */
	public RegistroDataModelImpl getListaResReferenciaPptal() {
		return listaResReferenciaPptal;
	}

	/**
	 * Asigna la lista listaResReferenciaPptal
	 * 
	 * @param listaResReferenciaPptal Variable a asignar en listaResReferenciaPptal
	 */
	public void setListaResReferenciaPptal(RegistroDataModelImpl listaResReferenciaPptal) {
		this.listaResReferenciaPptal = listaResReferenciaPptal;
	}

	/**
	 * Retorna la lista listaResReferenciaPptal
	 * 
	 * @return listaResReferenciaPptal
	 */
	public RegistroDataModelImpl getListaResReferenciaPptalE() {
		return listaResReferenciaPptalE;
	}

	/**
	 * Asigna la lista listaResReferenciaPptal
	 * 
	 * @param listaResReferenciaPptal Variable a asignar en listaResReferenciaPptal
	 */
	public void setListaResReferenciaPptalE(RegistroDataModelImpl listaResReferenciaPptalE) {
		this.listaResReferenciaPptalE = listaResReferenciaPptalE;
	}

	/**
	 * Retorna la lista listaResCentroPptal
	 * 
	 * @return listaResCentroPptal
	 */
	public RegistroDataModelImpl getListaResCentroPptal() {
		return listaResCentroPptal;
	}

	/**
	 * Asigna la lista listaResCentroPptal
	 * 
	 * @param listaResCentroPptal Variable a asignar en listaResCentroPptal
	 */
	public void setListaResCentroPptal(RegistroDataModelImpl listaResCentroPptal) {
		this.listaResCentroPptal = listaResCentroPptal;
	}

	/**
	 * Retorna la lista listaResCentroPptal
	 * 
	 * @return listaResCentroPptal
	 */
	public RegistroDataModelImpl getListaResCentroPptalE() {
		return listaResCentroPptalE;
	}

	/**
	 * Asigna la lista listaResCentroPptal
	 * 
	 * @param listaResCentroPptal Variable a asignar en listaResCentroPptal
	 */
	public void setListaResCentroPptalE(RegistroDataModelImpl listaResCentroPptalE) {
		this.listaResCentroPptalE = listaResCentroPptalE;
	}

	/**
	 * Retorna la lista listaResAuxilialPptal
	 * 
	 * @return listaResAuxilialPptal
	 */
	public RegistroDataModelImpl getListaResAuxilialPptal() {
		return listaResAuxilialPptal;
	}

	/**
	 * Asigna la lista listaResAuxilialPptal
	 * 
	 * @param listaResAuxilialPptal Variable a asignar en listaResAuxilialPptal
	 */
	public void setListaResAuxilialPptal(RegistroDataModelImpl listaResAuxilialPptal) {
		this.listaResAuxilialPptal = listaResAuxilialPptal;
	}

	/**
	 * Retorna la lista listaResAuxilialPptal
	 * 
	 * @return listaResAuxilialPptal
	 */
	public RegistroDataModelImpl getListaResAuxilialPptalE() {
		return listaResAuxilialPptalE;
	}

	/**
	 * Asigna la lista listaResAuxilialPptal
	 * 
	 * @param listaResAuxilialPptal Variable a asignar en listaResAuxilialPptal
	 */
	public void setListaResAuxilialPptalE(RegistroDataModelImpl listaResAuxilialPptalE) {
		this.listaResAuxilialPptalE = listaResAuxilialPptalE;
	}

	/**
	 * Retorna la lista listaReoRubroPptal
	 * 
	 * @return listaReoRubroPptal
	 */
	public RegistroDataModelImpl getListaReoRubroPptal() {
		return listaReoRubroPptal;
	}

	/**
	 * Asigna la lista listaReoRubroPptal
	 * 
	 * @param listaReoRubroPptal Variable a asignar en listaReoRubroPptal
	 */
	public void setListaReoRubroPptal(RegistroDataModelImpl listaReoRubroPptal) {
		this.listaReoRubroPptal = listaReoRubroPptal;
	}

	/**
	 * Retorna la lista listaReoRubroPptal
	 * 
	 * @return listaReoRubroPptal
	 */
	public RegistroDataModelImpl getListaReoRubroPptalE() {
		return listaReoRubroPptalE;
	}

	/**
	 * Asigna la lista listaReoRubroPptal
	 * 
	 * @param listaReoRubroPptal Variable a asignar en listaReoRubroPptal
	 */
	public void setListaReoRubroPptalE(RegistroDataModelImpl listaReoRubroPptalE) {
		this.listaReoRubroPptalE = listaReoRubroPptalE;
	}

	/**
	 * Retorna la lista listaReoFuentePptal
	 * 
	 * @return listaReoFuentePptal
	 */
	public RegistroDataModelImpl getListaReoFuentePptal() {
		return listaReoFuentePptal;
	}

	/**
	 * Asigna la lista listaReoFuentePptal
	 * 
	 * @param listaReoFuentePptal Variable a asignar en listaReoFuentePptal
	 */
	public void setListaReoFuentePptal(RegistroDataModelImpl listaReoFuentePptal) {
		this.listaReoFuentePptal = listaReoFuentePptal;
	}

	/**
	 * Retorna la lista listaReoFuentePptal
	 * 
	 * @return listaReoFuentePptal
	 */
	public RegistroDataModelImpl getListaReoFuentePptalE() {
		return listaReoFuentePptalE;
	}

	/**
	 * Asigna la lista listaReoFuentePptal
	 * 
	 * @param listaReoFuentePptal Variable a asignar en listaReoFuentePptal
	 */
	public void setListaReoFuentePptalE(RegistroDataModelImpl listaReoFuentePptalE) {
		this.listaReoFuentePptalE = listaReoFuentePptalE;
	}

	/**
	 * Retorna la lista listaReoReferenciaPptal
	 * 
	 * @return listaReoReferenciaPptal
	 */
	public RegistroDataModelImpl getListaReoReferenciaPptal() {
		return listaReoReferenciaPptal;
	}

	/**
	 * Asigna la lista listaReoReferenciaPptal
	 * 
	 * @param listaReoReferenciaPptal Variable a asignar en listaReoReferenciaPptal
	 */
	public void setListaReoReferenciaPptal(RegistroDataModelImpl listaReoReferenciaPptal) {
		this.listaReoReferenciaPptal = listaReoReferenciaPptal;
	}

	/**
	 * Retorna la lista listaReoReferenciaPptal
	 * 
	 * @return listaReoReferenciaPptal
	 */
	public RegistroDataModelImpl getListaReoReferenciaPptalE() {
		return listaReoReferenciaPptalE;
	}

	/**
	 * Asigna la lista listaReoReferenciaPptal
	 * 
	 * @param listaReoReferenciaPptal Variable a asignar en listaReoReferenciaPptal
	 */
	public void setListaReoReferenciaPptalE(RegistroDataModelImpl listaReoReferenciaPptalE) {
		this.listaReoReferenciaPptalE = listaReoReferenciaPptalE;
	}

	/**
	 * Retorna la lista listaReoCentroPptal
	 * 
	 * @return listaReoCentroPptal
	 */
	public RegistroDataModelImpl getListaReoCentroPptal() {
		return listaReoCentroPptal;
	}

	/**
	 * Asigna la lista listaReoCentroPptal
	 * 
	 * @param listaReoCentroPptal Variable a asignar en listaReoCentroPptal
	 */
	public void setListaReoCentroPptal(RegistroDataModelImpl listaReoCentroPptal) {
		this.listaReoCentroPptal = listaReoCentroPptal;
	}

	/**
	 * Retorna la lista listaReoCentroPptal
	 * 
	 * @return listaReoCentroPptal
	 */
	public RegistroDataModelImpl getListaReoCentroPptalE() {
		return listaReoCentroPptalE;
	}

	/**
	 * Asigna la lista listaReoCentroPptal
	 * 
	 * @param listaReoCentroPptal Variable a asignar en listaReoCentroPptal
	 */
	public void setListaReoCentroPptalE(RegistroDataModelImpl listaReoCentroPptalE) {
		this.listaReoCentroPptalE = listaReoCentroPptalE;
	}

	/**
	 * Retorna la lista listaReoAuxiliarPptal
	 * 
	 * @return listaReoAuxiliarPptal
	 */
	public RegistroDataModelImpl getListaReoAuxiliarPptal() {
		return listaReoAuxiliarPptal;
	}

	/**
	 * Asigna la lista listaReoAuxiliarPptal
	 * 
	 * @param listaReoAuxiliarPptal Variable a asignar en listaReoAuxiliarPptal
	 */
	public void setListaReoAuxiliarPptal(RegistroDataModelImpl listaReoAuxiliarPptal) {
		this.listaReoAuxiliarPptal = listaReoAuxiliarPptal;
	}

	/**
	 * Retorna la lista listaReoAuxiliarPptal
	 * 
	 * @return listaReoAuxiliarPptal
	 */
	public RegistroDataModelImpl getListaReoAuxiliarPptalE() {
		return listaReoAuxiliarPptalE;
	}

	/**
	 * Asigna la lista listaReoAuxiliarPptal
	 * 
	 * @param listaReoAuxiliarPptal Variable a asignar en listaReoAuxiliarPptal
	 */
	public void setListaReoAuxiliarPptalE(RegistroDataModelImpl listaReoAuxiliarPptalE) {
		this.listaReoAuxiliarPptalE = listaReoAuxiliarPptalE;
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

	// </SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}

	/**
	 * @return the anioCierre
	 */
	public int getAnioCierre() {
		return anioCierre;
	}

	/**
	 * @param anioCierre the anioCierre to set
	 */
	public void setAnioCierre(int anioCierre) {
		this.anioCierre = anioCierre;
	}

	/**
	 * @return the nombreRubro
	 */
	public String getNombreRubro() {
		return nombreRubro;
	}

	/**
	 * @param nombreRubro the nombreRubro to set
	 */
	public void setNombreRubro(String nombreRubro) {
		this.nombreRubro = nombreRubro;
	}

	/**
	 * @return the clase
	 */
	public String getClase() {
		return clase;
	}

	/**
	 * @param clase the clase to set
	 */
	public void setClase(String clase) {
		this.clase = clase;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the encabezado
	 */
	public String getEncabezado() {
		return encabezado;
	}

	/**
	 * @param encabezado the encabezado to set
	 */
	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}

	/**
	 * @return the tipoVigencia
	 */
	public String getTipoVigencia() {
		return tipoVigencia;
	}

	/**
	 * @param tipoVigencia the tipoVigencia to set
	 */
	public void setTipoVigencia(String tipoVigencia) {
		this.tipoVigencia = tipoVigencia;
	}

	/**
	 * @return the tipoCierre
	 */
	public String getTipoCierre() {
		return tipoCierre;
	}

	/**
	 * @param tipoCierre the tipoCierre to set
	 */
	public void setTipoCierre(String tipoCierre) {
		this.tipoCierre = tipoCierre;
	}

	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the contArchivocargarExcel
	 */
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	/**
	 * @param contArchivocargarExcel the contArchivocargarExcel to set
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

	/**
	 * @return the archivoCargaSelecFile
	 */
	public UploadedFile getArchivoCargaSelecFile() {
		return archivoCargaSelecFile;
	}

	/**
	 * @param archivoCargaSelecFile the archivoCargaSelecFile to set
	 */
	public void setArchivoCargaSelecFile(UploadedFile archivoCargaSelecFile) {
		this.archivoCargaSelecFile = archivoCargaSelecFile;
	}

	/**
	 * @return the listaData
	 */
	public List<Registro> getListaData() {
		return listaData;
	}

	/**
	 * @param listaData the listaData to set
	 */
	public void setListaData(List<Registro> listaData) {
		this.listaData = listaData;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}

