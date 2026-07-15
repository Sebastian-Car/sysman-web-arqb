/*-
 * FrmReportesCuipoControlador.java
 *
 * 1.0
 * 
 * 22/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.FrmReportesCuipoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 22/06/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmReportesCuipoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String anio;
	private String trimestre;
	private String reporte;
	private String codigoEntidad;
	private String codigoContaduria;
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
	private String nombreConsulta;
	private String encabezadoInforme;
	private Map<String, Object> reemplazar = new HashMap<>();
	
	/**
	 * Implementacion del EJB de EjbSysmanUtilRemote para hacer el llamado a las
	 * funciones y procedimientos que se invocan dentro del Controlador y se
	 * encuentran almacenadas en el paquete PCK_SYSMAN_UTIL
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbSysmanUtilRemote sysmanUtilRemote;
	@EJB
    private EjbPresupuestoTresGeneralRemote ejbPresupuestoTres;
	private int tipoEntidad;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Atributos usados para los encabezados de reportes
	 */
	public static final List<String> ENCABEZADOS_PROG_ING_APRO = Arrays.asList(
            "COMPANIA", "ANO", "CODIGO", "CODIGO_CCPET", "DETALLE_SECTORIAL",
            "APROPIACIONINICIAL", "MODIFICACION", "OBSERVACION");

    public static final List<String> ENCABEZADOS_PROG_ING_DET = Arrays.asList(
            "COMPANIA", "ANO", "CUENTA", "TIPO_CPTE", "COMPROBANTE", "CODIGO_CCPET",
            "DETALLE_SECTORIAL", "APROPIACIONINICIAL", "MODIFICACION", "OBSERVACION");

    public static final List<String> ENCABEZADOS_EJE_ING = Arrays.asList(
            "CUENTA", "TIPO_CPTE", "COMPROBANTE", "CONSECUTIVO", "CODIGO_CCPET", "CPC", "DETALLE_SECTORIAL",
            "FUENTE_FINANCIACION", "TERCERO", "POLITICA_PUBLICA", "TOTAL", "OBSERVACION");

    public static final List<String> ENCABEZADOS_PROG_GASTO_APRO = Arrays.asList(
            "COMPANIA", "ANO", "CODIGO", "CODIGO_CCPET", "EQUIVALENTE_CUIPO",
            "SECCION_PPTAL", "PROGRAMA", "CODIGOBPIN", "DETALLE_SECTORIAL",
            "APROPIACIONINICIAL", "MODIFICACION", "OBSERVACION");

    public static final List<String> ENCABEZADOS_PROG_GASTO_DET = Arrays.asList(
            "COMPANIA", "ANO", "CUENTA", "TIPO_CPTE", "COMPROBANTE", "CODIGO_CCPET", "EQUIVALENTE_CUIPO",
            "SECCION_PPTAL", "PROGRAMA", "CODIGOBPIN", "DETALLE_SECTORIAL", "APROPIACIONINICIAL",
            "MODIFICACION", "OBSERVACION");

    public static final List<String> ENCABEZADOS_EJE_GASTOS = Arrays.asList(
            "CUENTA", "TIPO_CPTE", "COMPROBANTE", "CONSECUTIVO", "CODIGO_CCPET", "VIGENCIA", "SECCION_PPTAL_CUIPO",
            "SECTOR_CUIPO", "CPC", "DETALLE_SECTORIAL", "FUENTE_FINANCIACION", "TIPO_NORMA", "NUMERO_NORMA", "FECHA_NORMA",
            "PROGRAMATICO", "BPIN", "SITUACION_FONDOS", "POLITICA_PUBLICA", "TERCERO", "COMPROMISOS", "OBSERVACION");
    
    public static final List<String> ENCABEZADOS_NUMEROS = Arrays.asList(
    		"APROPIACIONINICIAL", "MODIFICACION", "COMPROMISOS", "TOTAL");
	
	/**
	 * Crea una nueva instancia de FrmReportesCuipoControlador
	 */
	public FrmReportesCuipoControlador() {
		super();

		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		tipoEntidad = SessionUtil.getCompaniaIngreso().getTipoEntidad();
		try {
			codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigosChip();
			codigoContaduria = SessionUtil.getCompaniaIngreso().getCodigoContaduria();

			//numFormulario=2304;
			numFormulario = GeneralCodigoFormaEnum.FRMREPORTESCUIPOCONTROLADOR
                    .getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
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
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmReportesCuipoControladorUrlEnum.URL189
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Plano
	 * en la vista
	 *
	 *
	 */
	public void oprimirPlano() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		
		reemplazar.put("nombre", "NOMBRE,");
		validarTipoEntidad();
		generarInforme(FORMATOS.TXT);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		
		reemplazar.put("nombre", "NOMBRE,");
		validarTipoEntidad();
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	
	public void oprimirValidar() {
		archivoDescarga = null;
		String mesFinal = null;
		switch (trimestre) {
		case "1":
			mesFinal = "03";
			break;
		case "2":
			mesFinal = "06";
			break;
		case "3":
			mesFinal = "09";
			break;
		case "4":
			mesFinal = "12";
			break;
		}
		
		generarEncabezado();
		
		Map<String, Object> param = new HashMap<>();
		param.put("COMPANIA", compania);
		param.put("ANIO", anio);
		param.put("MESFINAL", mesFinal);

		List<String> nombresHojas = new ArrayList<>();
		List<List<String>> listaEncabezados = new ArrayList<>();
		List<List<Registro>> listaReportes = new ArrayList<>();
		List<String> columnasContables = new ArrayList<>();

		switch (reporte) {
		case "1":
			actualizarArbol();

			agregarHojaReporte("Apropiaci�n",
		            FrmReportesCuipoControladorUrlEnum.URL1979004.getValue(),
		            param, ENCABEZADOS_PROG_ING_APRO ,
		            nombresHojas, listaEncabezados, listaReportes);

		    agregarHojaReporte("Detalles",
		            FrmReportesCuipoControladorUrlEnum.URL1979005.getValue(),
		            param, ENCABEZADOS_PROG_ING_DET,
		            nombresHojas, listaEncabezados, listaReportes);

			break;
		case "2":
			actualizarArbol();
			actualizarDetalles();
			
			agregarHojaReporte("Ingresos",
		            FrmReportesCuipoControladorUrlEnum.URL1979006.getValue(),
		            param, ENCABEZADOS_EJE_ING,
		            nombresHojas, listaEncabezados, listaReportes);
			break;
		case "3":
			actualizarArbol();

			agregarHojaReporte("Apropiaci�n",
		            FrmReportesCuipoControladorUrlEnum.URL1979001.getValue(),
		            param, ENCABEZADOS_PROG_GASTO_APRO,
		            nombresHojas, listaEncabezados, listaReportes);

		    agregarHojaReporte("Detalles",
		            FrmReportesCuipoControladorUrlEnum.URL1979002.getValue(),
		            param, ENCABEZADOS_PROG_GASTO_DET,
		            nombresHojas, listaEncabezados, listaReportes);

			break;
		case "4":
			actualizarArbol();
			actualizarDetalles();
			
			agregarHojaReporte("Movimientos",
		            FrmReportesCuipoControladorUrlEnum.URL1979003.getValue(),
		            param, ENCABEZADOS_EJE_GASTOS,
		            nombresHojas, listaEncabezados, listaReportes);
			
			break;
		case "6":
			actualizarArbol();

			agregarHojaReporte("Apropiaci�n",
		            FrmReportesCuipoControladorUrlEnum.URL1979010.getValue(),
		            param, ENCABEZADOS_PROG_ING_APRO,
		            nombresHojas, listaEncabezados, listaReportes);

		    agregarHojaReporte("Detalles",
		            FrmReportesCuipoControladorUrlEnum.URL1979011.getValue(),
		            param, ENCABEZADOS_PROG_ING_DET,
		            nombresHojas, listaEncabezados, listaReportes);

			break;
		case "7":
			actualizarDetalles();
			
			agregarHojaReporte("Ingresos",
		            FrmReportesCuipoControladorUrlEnum.URL1979012.getValue(),
		            param, ENCABEZADOS_EJE_ING,
		            nombresHojas, listaEncabezados, listaReportes);
			break;
		case "8":
			actualizarArbol();

			agregarHojaReporte("Apropiaci�n",
		            FrmReportesCuipoControladorUrlEnum.URL1979007.getValue(),
		            param, ENCABEZADOS_PROG_GASTO_APRO,
		            nombresHojas, listaEncabezados, listaReportes);

		    agregarHojaReporte("Detalles",
		            FrmReportesCuipoControladorUrlEnum.URL1979008.getValue(),
		            param, ENCABEZADOS_PROG_GASTO_DET,
		            nombresHojas, listaEncabezados, listaReportes);
			break;
		case "9":
			actualizarDetalles();
			
			agregarHojaReporte("Ingresos",
		            FrmReportesCuipoControladorUrlEnum.URL1979009.getValue(),
		            param, ENCABEZADOS_EJE_GASTOS,
		            nombresHojas, listaEncabezados, listaReportes);
			break;
		}

		try {
			columnasContables = ENCABEZADOS_NUMEROS;
			archivoDescarga = generarReporteValidacion("POSIBLES_INCONSISTENCIAS_" + encabezadoInforme, nombresHojas, listaEncabezados, listaReportes, columnasContables);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void agregarHojaReporte(
			String nombreHoja,
			String urlEnum,
			Map<String, Object> param,
			List<String> encabezados,
			List<String> nombresHojas,
			List<List<String>> listaEncabezados,
			List<List<Registro>> listaReportes) {
		try {
			List<Registro> datos = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(urlEnum)
							.getUrl(),
							param));

			nombresHojas.add(nombreHoja);
			listaEncabezados.add(encabezados);
			listaReportes.add(datos);
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}  
	}
	
	private void actualizarArbol() {
		try {
			ejbPresupuestoTres.actualizarClasificadoresPptal(compania, Integer.parseInt(anio),
					SessionUtil.getUser().getCodigo());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void actualizarDetalles() {
		try {
			ejbPresupuestoTres.actualizarFuenteDetalle(compania, Integer.parseInt(anio),
					SessionUtil.getUser().getCodigo());
			ejbPresupuestoTres.actualizaSituacionFondos(compania,Integer.parseInt(anio));
			ejbPresupuestoTres.actualizaClasificadorCadenaPptal(compania, Integer.parseInt(anio),
					SessionUtil.getUser().getCodigo());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	//</METODOS_BOTONES>


	public void generarInforme(FORMATOS formato)
	{
		String mesFinal = null;

		try {
			switch (trimestre) {
			case "1":
				mesFinal = "03";
				break;
			case "2":
				mesFinal = "06";
				break;
			case "3":
				mesFinal = "09";
				break;
			case "4":
				mesFinal = "12";
				break;
			}

			reemplazar.put("anio", anio);
			reemplazar.put("mesfinal", mesFinal);
			reemplazar.put("compania", compania);

			archivoDescarga = JsfUtil.reportesFut(
					nombreConsulta, reemplazar,
					generarEncabezado(),
					formato,
					encabezadoInforme,
					modulo, false);

		}
		catch (JRException | IOException | SQLException | DRException |  com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo que arma el encabezado en el informe
	 * 
	 * @return
	 */
	private String generarEncabezado()
	{
		String retorno = "";
		try {
		String mesIni = null;
		String mesFin = null;
		String parametro = null;
		
		parametro = SysmanFunciones.nvlStr(sysmanUtilRemote.consultarParametro(compania, "ENCABEZADO MES A MES INFORMES CUIPO", SessionUtil.getModulo(), new Date(), false), "NO");
		
		if(parametro.equals("SI")) {

			switch (trimestre) {
			case "1":
				mesIni = "03";
				mesFin = "03";
				break;
			case "2":
				mesIni = "06";
				mesFin = "06";
				break;
			case "3":
				mesIni = "09";
				mesFin = "09";
				break;
			case "4":
				mesIni = "12";
				mesFin = "12";
				break;
			}

		}else {
			switch (trimestre) {
			case "1":
				mesIni = "01";
				mesFin = "03";
				break;
			case "2":
				mesIni = "04";
				mesFin = "06";
				break;
			case "3":
				mesIni = "07";
				mesFin = "09";
				break;
			case "4":
				mesIni = "10";
				mesFin = "12";
				break;
			}
		}

		switch (reporte) {
		case "1":
			encabezadoInforme ="A_PROGRAMACION_DE_INGRESOS";

			break;
		case "2":
			encabezadoInforme ="B_EJECUCION_DE_INGRESOS";
			break;
		case "3":
			encabezadoInforme ="C_PROGRAMACION_DE_GASTOS";
			break;
		case "4":
			encabezadoInforme ="D_EJECUCION_DE_GASTOS";
			break;
		case "5":
			encabezadoInforme ="E_ID_SECCIONES_PRESUPUESTALES_ADICIONALES";
			break;
		case "6":
			encabezadoInforme ="PROGRAMACION_DE_INGRESOS";

			break;
		case "7":
			encabezadoInforme ="EJECUCION_DE_INGRESOS";
			break;
		case "8":
			encabezadoInforme ="PROGRAMACION_DE_GASTOS";
			break;
		case "9":
			encabezadoInforme ="EJECUCION_DE_GASTOS";
			break;
		}

		String separadorColumnas = "\t";
		
			retorno = SysmanFunciones.concatenar("S", separadorColumnas, codigoContaduria, separadorColumnas, "1", mesIni, mesFin,
					separadorColumnas, anio, separadorColumnas, encabezadoInforme, separadorColumnas,
					SysmanFunciones.convertirAFechaCadena(
							new Date(),
							"dd-MM-yyyy"));
		}
		catch (ParseException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

		return retorno;

	}
	

	public void validarTipoEntidad() {

	    Registro consulta = null;
	    nombreConsulta = null;
	    Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.TIPO.getName(), "8");
            param.put("SUBTIPO",tipoEntidad);
            param.put("REPORTE",reporte);
            
            try {
                consulta = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmReportesCuipoControladorUrlEnum.URL175
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
            if (consulta != null) {
            
                if (consulta.getCampos().get("CONSULTA") == null) {
                
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4429"));
                }
                
                else {
                    
                    nombreConsulta = consulta.getCampos().get("CONSULTA").toString();
                }
            }
            
            else {
                
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4430"));
                
            }
	}

	private StreamedContent generarReporteValidacion(
	        String nombreReporte,
	        List<String> nombresHojas,
	        List<List<String>> listaEncabezados,
	        List<List<Registro>> listaReportes,
	        List<String> columnasContables) throws Exception {
		
		boolean tieneDatos = false;
		
		for (List<Registro> reporte : listaReportes) {
		    if (reporte != null && !reporte.isEmpty()) {
		        tieneDatos = true;
		        break;
		    }
		}

	    if (!tieneDatos) {
	        JsfUtil.agregarMensajeAlerta("El reporte no presenta inconsistencias.");
	        return null;
	    }

	    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	        Workbook workbook = new XSSFWorkbook();
	        CellStyle contableStyle = workbook.createCellStyle();
	        DataFormat df = workbook.createDataFormat();
	        contableStyle.setDataFormat(df.getFormat("#,##0.00"));

	        for (int i = 0; i < nombresHojas.size(); i++) {
	            String nombreHoja = nombresHojas.get(i);
	            List<String> encabezados = listaEncabezados.get(i);
	            List<Registro> reporte = listaReportes.get(i);

	            Sheet sheet = workbook.createSheet(nombreHoja);

	            Row headerRow = sheet.createRow(0);
	            for (int col = 0; col < encabezados.size(); col++) {
	                Cell cell = headerRow.createCell(col);
	                cell.setCellValue(encabezados.get(col));
	            }

	            int filaDatos = 1;
	            for (Registro reg : reporte) {
	                Row dataRow = sheet.createRow(filaDatos++);
	                for (int col = 0; col < encabezados.size(); col++) {
	                    String campo = encabezados.get(col);
	                    Object valor = reg.getCampos().get(campo);
	                    Cell cell = dataRow.createCell(col);
	                    
	                    if (valor == null) {
	                        cell.setCellValue("");
	                    } else if (columnasContables.contains(campo)) {
	                        cell.setCellValue(((Number) valor).doubleValue());
	                        cell.setCellStyle(contableStyle);
	                    } else {
	                    	cell.setCellValue(valor.toString());
	                    }
	                }
	            }

	            for (int col = 0; col < encabezados.size(); col++) {
	                sheet.autoSizeColumn(col);
	            }
	        }

	        workbook.write(out);
	        workbook.close();

	        return JsfUtil.getArchivoDescarga(
	                new ByteArrayInputStream(out.toByteArray()),
	                nombreReporte + ".xlsx");
	    }
	}

	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	/**
	 * Trae el valor almacenado en la base de datos para el parametro ingresado.
	 *
	 * @param nombreParametro
	 * Nombre del parametro en la base de datos.
	 * @param valorDefault
	 * Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String getParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania,
					nombreParametro, SessionUtil.getModulo(),
					new Date(), true);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}
	//<SET_GET_ATRIBUTOS>
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
	 * Retorna la variable trimestre
	 * 
	 * @return  trimestre
	 */
	public String getTrimestre() {
		return trimestre;
	}
	/**
	 * Asigna la variable  trimestre
	 * 
	 * @param  trimestre
	 * Variable a asignar en  trimestre
	 */
	public void setTrimestre(String trimestre) {
		this.trimestre = trimestre;
	}
	/**
	 * Retorna la variable reporte
	 * 
	 * @return  reporte
	 */
	public String getReporte() {
		return reporte;
	}
	/**
	 * Asigna la variable  reporte
	 * 
	 * @param  reporte
	 * Variable a asignar en  reporte
	 */
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	/**
	 * Retorna la variable codigoEntidad
	 * 
	 * @return  codigoEntidad
	 */
	public String getCodigoEntidad() {
		return codigoEntidad;
	}
	/**
	 * Asigna la variable  codigoEntidad
	 * 
	 * @param  codigoEntidad
	 * Variable a asignar en  codigoEntidad
	 */
	public void setCodigoEntidad(String codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
	}
	/**
	 * Retorna la variable codigoContaduria
	 * 
	 * @return  codigoContaduria
	 */
	public String getCodigoContaduria() {
		return codigoContaduria;
	}
	/**
	 * Asigna la variable  codigoContaduria
	 * 
	 * @param  codigoContaduria
	 * Variable a asignar en  codigoContaduria
	 */
	public void setCodigoContaduria(String codigoContaduria) {
		this.codigoContaduria = codigoContaduria;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
