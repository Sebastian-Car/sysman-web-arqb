/*-
 * DiscobanbogotagobnarControlador.java
 *
 * 1.0
 * 
 * 26/08/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
import org.primefaces.model.StreamedContent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
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
import com.sysman.nomina.ejb.EjbNominaCincoRemote;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.DiscobanbogotagobnarControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 26/08/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  DiscobanbogotagobnarControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private String modulo;
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	private String referencia;

	private Date fechareporte;
	//<DECLARAR_ATRIBUTOS>
	private StreamedContent archivoDescarga;
	private ContenedorArchivo contArchivoSelectorArchivos;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private List<Registro> listaAno1;
	private List<Registro> listaMes1;
	private List<Registro> listaPeriodo1;

	@EJB
	private EjbNominaCincoRemote ejbNominaCinco;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DiscobanbogotagobnarControlador
	 */
	public DiscobanbogotagobnarControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {

			numFormulario = GeneralCodigoFormaEnum.DISPERSION_BANCO_BOGOTA.getCodigo();
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			anio = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			fechareporte = new Date();
			referencia = "2020001453";
			contArchivoSelectorArchivos = new ContenedorArchivo();
			validarPermisos();
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
		//<CARGAR_LISTA>
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
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
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno1(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		String urlEnumId = DiscobanbogotagobnarControladorUrlEnum.URL5135.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
		try {
			listaAno1 = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMes1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaMes1(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		String urlEnumId = DiscobanbogotagobnarControladorUrlEnum.URL5500.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listaMes1 = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaPeriodo1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPeriodo1(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		String urlEnumId = DiscobanbogotagobnarControladorUrlEnum.URL5969.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listaPeriodo1 = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarDisco
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirGenerarDisco() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEnviarExcel() {
		//<CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;
			String datos = ejbNominaCinco.generarDisPlanoBBogota(compania, Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(periodo), referencia);
			if (datos.length() > 0) {
			generarExcel(datos);
			}else {
				  JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4003"));
			}
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}	

		//</CODIGO_DESARROLLADO>
	}

	public void generarExcel(String datos) {
		FileInputStream file = null;
		Workbook workbook = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			String archivo = String.valueOf(contArchivoSelectorArchivos.getArchivo());
			String extension = archivo.substring(archivo.indexOf('.'), archivo.length()).toLowerCase();
			if (validarArchivo(extension)) {
				String rutaArchivo = contArchivoSelectorArchivos.getArchivo().getPath();
				file = new FileInputStream(new File(rutaArchivo));
				String nombreArchivo = contArchivoSelectorArchivos.getArchivo().getName();

				if (".xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheet("NOMINA");

				String[] registro = datos.split("#"/*SysmanConstantes.SEPARADOR_REG*/);
				//	String[] colum;

				if (registro.length == 0) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2514"));
					return;
				}
				asignarValor(registro,sheet,0);
				workbook.setForceFormulaRecalculation(true);
				workbook.write(out);
				out.close();
				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
						nombreArchivo);
			}
		} catch (IOException | NumberFormatException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			try {
				if (file != null) {
					file.close();
				}
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	public boolean validarArchivo(String extension) {
		if (contArchivoSelectorArchivos.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
				return true;
			} else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
				return false;
			}
		}
	}


	public void asignarValor(String[] datosGenerales, Sheet sheet, int tipo) {
		String[] colum;
		Row row;
		for (int i = 0; i < datosGenerales.length; i++) {
			colum = datosGenerales[i].split(";");
			row = sheet.createRow(i + 14);
			int k = 0;
			for (int j = 0; j < colum.length; j++) {
				Cell nCell = row.createCell(k);
				formatea(nCell, j, colum);
				if (tipo == 1 && j == 1) {
					k = 3;
				}
				k++;
			}
		}
	}

	private void formatea(Cell cell, int columna, String[] colum) {

		if (columna == 5) {
			formatoNumero(cell);
			cell.setCellValue(Double.parseDouble(colum[columna]));
		} else {
			cell.setCellValue(colum[columna]);
		}

	}
	private void formatoNumero(Cell cell) {
		Workbook wb = cell.getSheet().getWorkbook();
		CellStyle style = wb.createCellStyle();
		DataFormat format = wb.createDataFormat();
		style.setDataFormat(format.getFormat("#,##0.00"));
		style.setLocked(false);
		cell.setCellStyle(style);
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno1
	 * 
	 * @return listaAno1
	 */
	public List<Registro> getListaAno1() {
		return listaAno1;
	}
	/**
	 * Asigna la lista listaAno1
	 * 
	 * @param listaAno1
	 * Variable a asignar en  listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}
	/**
	 * Retorna la lista listaMes1
	 * 
	 * @return listaMes1
	 */
	public List<Registro> getListaMes1() {
		return listaMes1;
	}
	/**
	 * Asigna la lista listaMes1
	 * 
	 * @param listaMes1
	 * Variable a asignar en  listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}
	/**
	 * Retorna la lista listaPeriodo1
	 * 
	 * @return listaPeriodo1
	 */
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}
	/**
	 * Asigna la lista listaPeriodo1
	 * 
	 * @param listaPeriodo1
	 * Variable a asignar en  listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public Date getFechareporte() {
		return fechareporte;
	}
	public void setFechareporte(Date fechareporte) {
		this.fechareporte = fechareporte;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public ContenedorArchivo getContArchivoSelectorArchivos() {
		return contArchivoSelectorArchivos;
	}
	public void setContArchivoSelectorArchivos(ContenedorArchivo contArchivoSelectorArchivos) {
		this.contArchivoSelectorArchivos = contArchivoSelectorArchivos;
	}
}
