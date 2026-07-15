/*-
 * CargarSerieDocumentalControlador.java
 *
 * 1.0
 * 
 * 06/08/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.ejb.EjbWorkflowCeroLocal;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 06/08/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  CargarSerieDocumentalControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private String opcion;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos cargarSerie y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivocargarSerie;
	private StringBuilder cadena;

	@EJB
	private EjbWorkflowCeroRemote ejbWorkflowCeroLocal;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CargarSerieDocumentalControlador
	 */
	public CargarSerieDocumentalControlador() {
		super();
		compania = SessionUtil.getCompania();
		opcion = "1";
		try {
			//2179
			numFormulario= GeneralCodigoFormaEnum.CARGAR_SERIE_DOCUMENTAL_CONTROLADOR.getCodigo();
			validarPermisos();
			contArchivocargarSerie = new ContenedorArchivo();
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
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT3577
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargarSeries() {
		//<CODIGO_DESARROLLADO>
		FileInputStream file = null;
		cadena = new StringBuilder();
		try {

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarSerie.getArchivo()
						.getPath();

				String extension = rutaArchivo
						.substring(rutaArchivo.indexOf('.'),
								rutaArchivo.length())
						.substring(1, rutaArchivo.substring(
								rutaArchivo.indexOf('.'),
								rutaArchivo.length()).length());

				file = new FileInputStream(new File(rutaArchivo));

				Workbook workbook = null;

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				}
				else {
					workbook = new XSSFWorkbook(file);
				}

				if(opcion.equals("1")) {

					leerHoja(workbook, 0, 17, cadena, 2);		
					cargarDatos();

				}else if (opcion.equals("3")) {

					if("SI".equals(SysmanFunciones
							.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA VARIABLES DE PROCESO AL CARGAR TRAMITES", SessionUtil.getModulo(), new Date(), true), "NO")
							.toString())) {

						leerHoja(workbook, 0, 17, cadena, 3);		

					}else {
						leerHoja(workbook, 0, 10, cadena, 3);		

					}
					cargarTramite();
				}else {

					leerHoja(workbook, 0, 15, cadena, 2);
					cargarHistorial();

				}

				file.close();
				workbook.close();
			}
		}
		catch (IOException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
		//</CODIGO_DESARROLLADO>
	}

	public boolean validarArchivo() {

		if (contArchivocargarSerie.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		}
		else {
			return true;
		}
	}

	public void leerHoja(Workbook workbook, int hoja, int columnas,
			StringBuilder cadena, int filainicial) {
		cadena.append("TO_CLOB('");
		Sheet sheet = workbook.getSheetAt(hoja);
		Row fila;
		Cell celda;
		int num = 0;
		for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
			fila = sheet.getRow(i);

			if (fila.getCell(0) != null) {

				for (int j = 0; j < columnas; j++) {
					celda = fila.getCell(j);
					if (celda != null) {
						num = num
								+ (celda.getCellType() == 1
								? celda.getStringCellValue()
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

		}
		cadena.append("')"
				+ "");

	}

	private void cargarTramite() {
		try {
			String cadenaTramite = (SysmanFunciones.esBdSqlServer())
					? cadena.toString().replace("TO_CLOB(", "")
							.replace(")", "")
							: cadena.toString();

							ejbWorkflowCeroLocal.cargarTramites(compania, cadenaTramite, SessionUtil.getUser().getCodigo());

							JsfUtil.agregarMensajeInformativo(
									idioma.getString(
											"MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void cargarDatos() {


		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.toString().replace("TO_CLOB(", "")
							.replace(")", "")
							: cadena.toString();


							ejbWorkflowCeroLocal.cargarSerieDocumental(compania, parametro,  SessionUtil.getUser().getCodigo());



							JsfUtil.agregarMensajeInformativo(
									idioma.getString(
											"MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}


	private void cargarHistorial() {
		try {
			String cadenaTramite = (SysmanFunciones.esBdSqlServer())
					? cadena.toString().replace("TO_CLOB(", "")
							.replace(")", "")
							: cadena.toString();

							ejbWorkflowCeroLocal.cargarHistorialVar(compania, cadenaTramite, SessionUtil.getUser().getCodigo());

							JsfUtil.agregarMensajeInformativo(
									idioma.getString(
											"MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna el objeto contArchivocargarSerie
	 * 
	 * @return contArchivocargarSerie
	 */
	public ContenedorArchivo getContArchivocargarSerie() {
		return contArchivocargarSerie;
	}
	/**
	 * Asigna el objeto contArchivocargarSerie
	 * 
	 * @param contArchivocargarSerie
	 * Variable a asignar en contArchivocargarSerie
	 */
	public void setContArchivocargarSerie(ContenedorArchivo contArchivocargarSerie) {
		this.contArchivocargarSerie = contArchivocargarSerie;
	}
	public String getOpcion() {
		return opcion;
	}
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}


	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
