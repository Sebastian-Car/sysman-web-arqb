package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.impl.EjbPresupuestoCuatro;
import com.sysman.presupuesto.enums.LibroRegistroIngresosControladorEnum;
import com.sysman.presupuesto.enums.LibroRegistroIngresosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;


import org.apache.poi.ss.usermodel.Row;


import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 28/06/2016
 * @modified jguerrero
 * @version 2. 18/04/2017 Se realizo el refactory de las consultas sql en el
 *          controlador. Además se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambió el llamado del código del formulario y
 *          actualización de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class LibroRegistroIngresosControlador extends BeanBaseModal {
	private final String compania;
	/**
	 * Constante definida para almacenar la cadena "SYSDATE"
	 */

	/**
	 * Constante definida para almacena la cadena "CODIGO"
	 */
	private final String cod;
	// <DECLARAR_ATRIBUTOS>
	private boolean formatoEspecialExcel;
	private boolean indicador;
	private boolean fuenteRecursos;
	private boolean centroCosto;
	private String cuentaInicial;
	private String cuentaFinal;
	private String mesInicial;
	private String mesFinal;
	private String centroInicial;
	private String centroFinal;
	private String fuenteInicial;
	private String fuenteFinal;
	private String anio;
	private String nivel;
	private String nmes1;
	private String nmes2;
	private String textoFinalRegistros = "";
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private List<Registro> listaMes;
	private List<Registro> listames1;
	private List<Registro> listaAno;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private RegistroDataModelImpl listacentrocostoInicial;
	private RegistroDataModelImpl listacentrocostoFinal;
	private RegistroDataModelImpl listaFuenteInicial;
	private RegistroDataModelImpl listaFuenteFinal;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbPresupuestoCuatro ejbPresupuestoCuatro;
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Creates a new instance of LibroRegistroIngresosControlador
	 */
	public LibroRegistroIngresosControlador() {
		super();
		compania = SessionUtil.getCompania();

		cod = "CODIGO";
		try {
			numFormulario = GeneralCodigoFormaEnum.LIBRO_REGISTRO_INGRESOS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(LibroRegistroIngresosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		
		try {
		// <CARGAR_LISTA>
		nivel = "60";
		cargarListaAno();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		cargarListaMes();
		mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
		cargarListames1();
		indicador = true;
		mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
		nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)];
		nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial) + 1];
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial();
		cargarListacentrocostoInicial();
		cargarListaFuenteInicial();

		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
		
		textoFinalRegistros = SysmanFunciones.nvlStr(
			    ejbSysmanUtil.consultarParametro(compania,
			        "TEXTO FINAL REGISTRO INGRESOS", 
			        SessionUtil.getModulo(),
			        new Date(),
			        false),
			    "NO");
		
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaMes() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaMes = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											LibroRegistroIngresosControladorUrlEnum.URL4923.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// 7007
	}

	public void cargarListames1() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		param.put("MES", mesInicial);

		try {
			listames1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											LibroRegistroIngresosControladorUrlEnum.URL5337.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// 7018 MES
	}

	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											LibroRegistroIngresosControladorUrlEnum.URL5821.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// 4001
	}

	public void cargarListaCuentaInicial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LibroRegistroIngresosControladorUrlEnum.URL6160.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);

		// 45002

	}

	public void cargarListaCuentaFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LibroRegistroIngresosControladorUrlEnum.URL7060.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(LibroRegistroIngresosControladorEnum.PARAM3.getValue(), cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);
		// 45004
	}

	public void cargarListacentrocostoInicial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LibroRegistroIngresosControladorUrlEnum.URL8079.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cod);

		// 25015
	}

	public void cargarListacentrocostoFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LibroRegistroIngresosControladorUrlEnum.URL8783.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(LibroRegistroIngresosControladorEnum.PARAM9.getValue(), centroInicial);

		listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cod);

		// 25013

	}

	public void cargarListaFuenteInicial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LibroRegistroIngresosControladorUrlEnum.URL9499.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);
		// 23010

	}

	public void cargarListaFuenteFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LibroRegistroIngresosControladorUrlEnum.URL10111.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(LibroRegistroIngresosControladorEnum.PARAM14.getValue(), fuenteInicial);

		listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				cod);

		// 23019 AUXILIARINICIAL
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	public void oprimirExcel() {
	    // Codigo desarrollado
	    archivoDescarga = null;

	    if (formatoEspecialExcel) {

	        
	        if (validacionCamposCentros() || validacionCamposCuentas() || validacionCamposFuente()) {
	            return;
	        }

	        if (validacionCampos()) {
	            return;
	        }

	        // Mapeo de parámetros usados para reemplazar en la consulta SQL
	        HashMap<String, Object> reemplazar = new HashMap<>();
	        reemplazar.put("cuentaInicial", cuentaInicial);
	        reemplazar.put("cuentaFinal", cuentaFinal);
	        reemplazar.put("centroInicial", centroInicial);
	        reemplazar.put("centroFinal", centroFinal);
	        reemplazar.put("fuenteInicial", fuenteInicial);
	        reemplazar.put("fuenteFinal", fuenteFinal);
	        reemplazar.put("mesInicial", mesInicial);
	        reemplazar.put("mesFinal", mesFinal);
	        reemplazar.put("anio", anio);
	        reemplazar.put("nivel", nivel);
	        reemplazar.put("condCentroCosto", conCentroCosto());
	        reemplazar.put("condFuenteRecurso", condFuente());

	        
	        String sql = Reporteador.resuelveConsulta("800542FCREGISTROINGRESOS036",
	                Integer.parseInt(SessionUtil.getModulo()), reemplazar);

	        String nomArchivo = "Libro Registros";
	        String periodoTexto = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)].toUpperCase()
	                + " a "
	                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesFinal)].toUpperCase();

	        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	            // Se genera el libro Excel a partir del resultado de la consulta SQL
	            Workbook workbook = new XSSFWorkbook(
	                    JsfUtil.exportarHojaDatosStreamed(sql,
	                            ConectorPool.ESQUEMA_SYSMAN,
	                            FORMATOS.EXCEL).getStream());

	            Sheet sheet = workbook.getSheet("Report");

	            // Desplazar filas 4 posiciones hacia abajo para insertar encabezados personalizados
	            sheet.shiftRows(0, sheet.getLastRowNum(), 4);
	            sheet.setAutobreaks(true);

	            // BLOQUE 1: Unificacion de celdas para los títulos generales

	            // Título principal (fila 0, columna 0 hasta la última columna existente)
	            CellReference cellRefIniTitulo = new CellReference(0, 0); // Columna 0
	            CellReference cellRefFinTitulo = new CellReference(0,
	                    Math.max(sheet.getRow(4).getLastCellNum(), 0) - 1); // ultima columna del reporte
	            sheet.addMergedRegion(CellRangeAddress.valueOf(cellRefIniTitulo.formatAsString() + ":" + cellRefFinTitulo.formatAsString()));

	            // Subtitulo (fila 1, columna 0 hasta la ultima columna existente)
	            CellReference cellRefIniTitulo1 = new CellReference(1, 0); // Columna 0
	            CellReference cellRefFinTitulo1 = new CellReference(1,
	                    Math.max(sheet.getRow(4).getLastCellNum(), 0) - 1);
	            sheet.addMergedRegion(CellRangeAddress.valueOf(cellRefIniTitulo1.formatAsString() + ":" + cellRefFinTitulo1.formatAsString()));

	            // Periodo o información adicional (fila 2, columna 0 hasta la ultima columna existente)
	            CellReference cellRefIniTitulo2 = new CellReference(2, 0); // Columna 0
	            CellReference cellRefFinTitulo2 = new CellReference(2,
	                    Math.max(sheet.getRow(4).getLastCellNum(), 0) - 1);
	            sheet.addMergedRegion(CellRangeAddress.valueOf(cellRefIniTitulo2.formatAsString() + ":" + cellRefFinTitulo2.formatAsString()));
	            
	            // Periodo contable (fila 3, columna 0 hasta la última columna existente)
	            CellReference cellRefIniTitulo3 = new CellReference(3, 0); // Columna 0
	            CellReference cellRefFinTitulo3 = new CellReference(3,
	                    Math.max(sheet.getRow(4).getLastCellNum(), 0) - 1);
	            sheet.addMergedRegion(CellRangeAddress.valueOf(
	                    cellRefIniTitulo3.formatAsString() + ":" + cellRefFinTitulo3.formatAsString()));

	            // BLOQUE 2: Estilos de celdas para encabezados
	            CellStyle style = workbook.createCellStyle();
	            style.setAlignment(CellStyle.ALIGN_CENTER); // Centrar texto en la celda

	            Font font = workbook.createFont();
	            font.setFontName("SansSerif");
	            font.setBold(true); // Texto en negrita
	            style.setFont(font);

	            // BLOQUE 3: Creacion de filas y celdas del encabezado

	            // Fila 0, columna 0: nombre de la compañía (primer titulo)
	            Cell cell = sheet.createRow(0).createCell(0);
	            cell.setCellValue(SessionUtil.getCompaniaIngreso().getNombre());
	            cell.setCellStyle(style);

	            // Fila 1, columna 0: titulo principal del reporte (texto traducido)
	            Cell cell2 = sheet.createRow(1).createCell(0);
	            cell2.setCellValue("LIBRO DE REGISTROS DE INGRESOS");
	            cell2.setCellStyle(style);
	            
	            
	            // Fila 2, columna 0: periodo contable (Vigencia)
	            Cell cell3 = sheet.createRow(2).createCell(0);
	            cell3.setCellValue("VIGENCIA " + anio);
	            cell3.setCellStyle(style);


	            // Fila 3 columna 0: periodo contable (por ejemplo, ENERO A MARZO)
	            Cell cell4 = sheet.createRow(3).createCell(0);
	            cell4.setCellValue("PERIODO: "+ periodoTexto);
	            cell4.setCellStyle(style);
	            
	            
	            // BLOQUE FINAL: agregar texto "GENERADO POR SYSMAN" al final del reporte

	            int nColumnas = Math.max(sheet.getRow(4).getLastCellNum(), 0); // Numero total de columnas
	            int ultimaFila = sheet.getLastRowNum() + 1; // Una fila mas abajo de la ultima con datos

	            Row filaFinal = sheet.createRow(ultimaFila); // Crear nueva fila debajo de los datos

	            // Crear la celda inicial (columna A)
	            Cell celdaFinal = filaFinal.createCell(0);
	            celdaFinal.setCellValue(textoFinalRegistros);

	            // Unir celdas desde la columna 0 hasta la ultima existente para que el texto no quede cortado
	            sheet.addMergedRegion(new CellRangeAddress(ultimaFila, ultimaFila, 0, Math.max(nColumnas - 1, 0)));

	            // Crear estilo: wrap text, alineado a la izquierda y arriba (constantes antiguas)
	            CellStyle estiloFinal = workbook.createCellStyle();
	            estiloFinal.setWrapText(true);
	            estiloFinal.setAlignment(CellStyle.ALIGN_LEFT);     
	            estiloFinal.setVerticalAlignment(CellStyle.VERTICAL_TOP);

	            // Fuente en negrita opcional
	            Font fuente = workbook.createFont();
	            fuente.setBold(true);
	            estiloFinal.setFont(fuente);

	            // Aplicar el estilo
	            celdaFinal.setCellStyle(estiloFinal);

	            // Ajustar la altura de la fila para que se muestre el texto (ajusta el multiplicador segun necesites)
	            filaFinal.setHeightInPoints(60f);


	            workbook.setForceFormulaRecalculation(true);
	            workbook.write(out);
	            workbook.close();

	            
	            archivoDescarga = JsfUtil.getArchivoDescarga(
	                    new ByteArrayInputStream(out.toByteArray()),
	                    SysmanFunciones.concatenar(nomArchivo, ".xlsx"));

	        } catch (SQLException | JRException | IOException | DRException | SysmanException ex) {
	            JsfUtil.agregarMensajeError(ex.getMessage());
	            logger.error(ex.getMessage(), ex);
	        }

	    } else {
	        // Si no es formato especial, se genera el reporte estandar en formato Excel
	        generaReporte(FORMATOS.EXCEL);
	    }

	}
	

	/**
	 * Valida si se seleccionan los centros de costo
	 *
	 * @return verdadero o falso
	 */
	public boolean validacionCamposCentros() {

		if (centroCosto) {
			if ((centroInicial == null) || centroInicial.isEmpty()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB697"));
				return true;
			} else if ((centroFinal == null) || centroFinal.isEmpty()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB698"));
				return true;
			}
		}

		return false;
	}

	/**
	 * Valida si se seleccionan las cuentas
	 *
	 * @return verdadero o falso
	 */
	public boolean validacionCamposCuentas() {
		if ((cuentaInicial == null) || cuentaInicial.isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB699"));
			return true;
		}
		if ((cuentaFinal == null) || cuentaFinal.isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB700"));
			return true;
		}
		return false;
	}

	/**
	 * Valida si se seleccionan las fuentes de recursos
	 *
	 * @return verdadero o falso
	 */
	public boolean validacionCamposFuente() {
		if (fuenteRecursos) {
			if ((fuenteInicial == null) || fuenteInicial.isEmpty()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB701"));
				return true;
			} else if ((fuenteFinal == null) || fuenteFinal.isEmpty()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB702"));
				return true;
			}
		}

		return false;
	}

	/**
	 * Valida si se selecciona el ano y los meses
	 *
	 * @return verdadero o falso
	 */
	public boolean validacionCampos() {
		if ((mesInicial == null) || mesInicial.isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB703"));
			return true;
		}
		if ((mesFinal == null) || mesFinal.isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB704"));
			return true;
		}
		if ((anio == null) || anio.isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB705"));
			return true;
		}
		return false;
	}

	/**
	 * Devuelve una cadena separada por # para los parametros del reporte, se llama
	 * en el metodo paraSeccionInforme
	 *
	 * @param conSeccion
	 * @param conUnidad
	 * @param conRegional
	 * @return respuesta
	 */
	public String param(String conSeccion, String conUnidad, String conRegional) {
		String respuesta;
		if (!(conSeccion == null ? "" : conSeccion).isEmpty()) {
			respuesta = "SECCION #" + conSeccion + "";
		} else {
			respuesta = " # ";
		}

		if (!(conUnidad == null ? "" : conUnidad).isEmpty()) {
			respuesta = respuesta + "#" + "UNIDAD EJECUTORA #" + conUnidad + "";
		} else {
			respuesta = respuesta + "# # ";
		}

		if (!(conRegional == null ? "" : conRegional).isEmpty()) {
			respuesta = respuesta + "#" + "REGIONAL #" + conRegional + "";
		} else {
			respuesta = respuesta + "# # ";
		}
		return respuesta;
	}

	/**
	 * Retorna una cadena se llama en el metodo generarInforme
	 *
	 * @param seccionInfRes036
	 * @return mensaje
	 */
	public String paraSeccionInforme(String seccionInfRes036) {
		String mensaje = "";
		try {
			if ("SI".equals(seccionInfRes036 == null ? "NO" : seccionInfRes036)) {
				String conSeccion;

				conSeccion = ejbSysmanUtil.consultarParametro(compania, "SECCION 036", SessionUtil.getModulo(),
						new Date(), true);

				String conUnidad = ejbSysmanUtil.consultarParametro(compania, "UNIDAD EJECUTORA 036",
						SessionUtil.getModulo(), new Date(), true);

				String conRegional = ejbSysmanUtil.consultarParametro(compania, "REGIONAL 036", SessionUtil.getModulo(),
						new Date(), true);

				mensaje = param(conSeccion, conUnidad, conRegional);

			} else {
				mensaje = " # # # # # ";
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return mensaje;
	}

	/**
	 * Retorna la condicion para el reporte de centro de costo
	 *
	 * @return condicion
	 */
	public String conCentroCosto() {
		String condicion;
		condicion = centroCosto
				? "  AND PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '" + centroInicial + "'" + " AND '" + centroFinal + "'"
				: "";
		return condicion;
	}

	/**
	 * Retorna la condicion para el reporte de fuente de recursos
	 *
	 * @return condicion
	 */
	public String condFuente() {
		String condicion;
		condicion = fuenteRecursos
				? " AND PLAN_PRESUPUESTAL.AUXILIAR BETWEEN '" + fuenteInicial + "'" + "       AND '" + fuenteFinal + "'"
				: "";
		return condicion;
	}

	private void generaReporte(FORMATOS formato) {
		try {
			if (validacionCamposCentros() || validacionCamposCuentas() || validacionCamposFuente()) {
				return;
			}

			if (validacionCampos()) {
				return;
			}

			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			String reporte = "000952FCREGISTROINGRESOS036";
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("centroInicial", centroInicial);
			reemplazar.put("centroFinal", centroFinal);
			reemplazar.put("fuenteInicial", fuenteInicial);
			reemplazar.put("fuenteFinal", fuenteFinal);
			reemplazar.put("mesInicial", mesInicial);
			reemplazar.put("mesFinal", mesFinal);
			reemplazar.put("anio", anio);
			reemplazar.put("nivel", nivel);
			reemplazar.put("condCentroCosto", conCentroCosto());
			reemplazar.put("condFuenteRecurso", condFuente());

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

			String seccionInfRes036 = ejbSysmanUtil.consultarParametro(compania, "SECCION EN INFORMES RESOLUCION 036",
					SessionUtil.getModulo(), new Date(), true);

			String respuesta = paraSeccionInforme(seccionInfRes036);
			System.out.println(parametros);
			parametros.put("PR_INDICADOR", indicador);
			parametros.put("PR_MES1", mesInicial);
			parametros.put("PR_MES2", mesFinal);
			parametros.put("PR_REGIONAL", respuesta.split("#")[4]);
			parametros.put("PR_CONREGIONAL", respuesta.split("#")[5]);
			parametros.put("PR_UNIDAD", respuesta.split("#")[2]);
			parametros.put("PR_CONUNIDAD", respuesta.split("#")[3]);
			parametros.put("PR_SECCION", respuesta.split("#")[0]);
			parametros.put("PR_CONSECCION", respuesta.split("#")[1]);
			parametros.put("PR_ANO", anio);
			parametros.put("PR_NMES2", nmes2);
			parametros.put("PR_NMES1", nmes1);
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	public void cambiarMes() {
		// <CODIGO_DESARROLLADO>
		nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesInicial)];
		cargarListames1();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarmes1() {
		// <CODIGO_DESARROLLADO>
		nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesFinal)];
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		mesInicial = mesFinal = nmes1 = nmes2 = cuentaInicial = cuentaFinal = centroInicial = centroFinal = fuenteInicial = fuenteFinal = null;
		cargarListaMes();
		cargarListaCuentaInicial();

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control nivel
	 * 
	 * 
	 */
	public void cambiarnivel() {
		// <CODIGO_DESARROLLADO>
		if (SysmanFunciones.validarVariableVacio(nivel)) {
			nivel = "60";
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarcentroCosto() {
		// <CODIGO_DESARROLLADO>
		cargarListacentrocostoInicial();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarfuenteRecurso() {
		// <CODIGO_DESARROLLADO>
		cargarListaFuenteInicial();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "").toString();
		cargarListaCuentaFinal();
		cuentaFinal = null;
	}

	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "").toString();
	}

	public void seleccionarFilacentrocostoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroInicial = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "").toString();
		cargarListacentrocostoFinal();
		centroFinal = null;
	}

	public void seleccionarFilacentrocostoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroFinal = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "").toString();
	}

	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "").toString();
		cargarListaFuenteFinal();
		fuenteFinal = null;
	}

	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = SysmanFunciones.nvl(registroAux.getCampos().get(cod), "").toString();
	}

	// </METODOS_COMBOS_GRANDES>

	// <SET_GET_ATRIBUTOS>
	public boolean getIndicador() {
		return indicador;
	}

	public void setIndicador(boolean indicador) {
		this.indicador = indicador;
	}

	/**
	 * Retorna la variable formatoEspecialExcel
	 * 
	 * @return formatoEspecialExcel
	 */
	public boolean isFormatoEspecialExcel() {
		return formatoEspecialExcel;
	}

	public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
		this.formatoEspecialExcel = formatoEspecialExcel;
	}

	public boolean isFuenteRecursos() {
		return fuenteRecursos;
	}

	public void setFuenteRecursos(boolean fuenteRecursos) {
		this.fuenteRecursos = fuenteRecursos;
	}

	public boolean isCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(boolean centroCosto) {
		this.centroCosto = centroCosto;
	}

	public String getCuentaInicial() {
		return cuentaInicial;
	}

	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	public String getCuentaFinal() {
		return cuentaFinal;
	}

	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	public String getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	public String getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	public String getCentroInicial() {
		return centroInicial;
	}

	public void setCentroInicial(String centroInicial) {
		this.centroInicial = centroInicial;
	}

	public String getCentroFinal() {
		return centroFinal;
	}

	public void setCentroFinal(String centroFinal) {
		this.centroFinal = centroFinal;
	}

	public String getFuenteInicial() {
		return fuenteInicial;
	}

	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}

	public String getFuenteFinal() {
		return fuenteFinal;
	}

	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getNmes1() {
		return nmes1;
	}

	public void setNmes1(String nmes1) {
		this.nmes1 = nmes1;
	}

	public String getNmes2() {
		return nmes2;
	}

	public void setNmes2(String nmes2) {
		this.nmes2 = nmes2;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	public List<Registro> getListaMes() {
		return listaMes;
	}

	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	public List<Registro> getListames1() {
		return listames1;
	}

	public void setListames1(List<Registro> listames1) {
		this.listames1 = listames1;
	}

	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}

	public RegistroDataModelImpl getListacentrocostoInicial() {
		return listacentrocostoInicial;
	}

	public void setListacentrocostoInicial(RegistroDataModelImpl listacentrocostoInicial) {
		this.listacentrocostoInicial = listacentrocostoInicial;
	}

	public RegistroDataModelImpl getListacentrocostoFinal() {
		return listacentrocostoFinal;
	}

	public void setListacentrocostoFinal(RegistroDataModelImpl listacentrocostoFinal) {
		this.listacentrocostoFinal = listacentrocostoFinal;
	}

	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}

	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}

	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}

	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}