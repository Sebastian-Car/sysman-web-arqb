/*-
 * FrmCargarTipoRecursosControlador.java
 *
 * 1.0
 * 
 * 14/07/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;
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
import com.sysman.cgr.ejb.impl.EjbCGRCero;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
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
import com.sysman.util.ContenedorArchivo;
/**
 *
 * @version 1.0, 14/07/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmCargarTipoRecursosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	@EJB
	private EjbCGRCero cgrCero;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos PlantillaTipo y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivoPlantillaTipo;
	private StringBuilder cadena;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmCargarTipoRecursosControlador
	 */
	public FrmCargarTipoRecursosControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2309
			numFormulario = GeneralCodigoFormaEnum.FRM_CARGAR_TIPO_RECURSOS_CONTROLADOR.getCodigo();
			validarPermisos();
			contArchivoPlantillaTipo = new ContenedorArchivo();
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
	 * Metodo ejecutado al oprimir el boton CargarDatos
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargarDatos() {
		//<CODIGO_DESARROLLADO>
		FileInputStream file = null;
		cadena = new StringBuilder();

		try {
			if (validarArchivo()) {
				String ruta = contArchivoPlantillaTipo.getArchivo()
						.getPath();

				String extension = ruta
						.substring(ruta.indexOf('.'),
								ruta.length())
						.substring(1, ruta.substring(
								ruta.indexOf('.'),
								ruta.length()).length());

				file = new FileInputStream(new File(ruta));

				Workbook workbook = null;

				if (workbook == null) {
					if ("xls".equals(extension)) {
						workbook = new HSSFWorkbook(file);
					}
					else {
						workbook = new XSSFWorkbook(file);
					}
				}

				leerHoja(workbook, 0, 4, cadena, 1);		
				cargarDatos();



				file.close();
				workbook.close();
			}

		} catch (IOException | NumberFormatException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		finally {
			try {
				if (file != null) {
					file.close();
				}
			}
			catch (IOException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	public boolean validarArchivo() {

		if (contArchivoPlantillaTipo.getArchivo() == null) {
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
										:  NumberToTextConverter
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


	private void cargarDatos() {


		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.toString().replace("TO_CLOB(", "")
							.replace(")", "")
							: cadena.toString();


							cgrCero.cargarTipoRecurso(compania, parametro, SessionUtil.getUser().getCodigo());

							JsfUtil.agregarMensajeInformativo(
									idioma.getString(
											"MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna el objeto contArchivoPlantillaTipo
	 * 
	 * @return contArchivoPlantillaTipo
	 */
	public ContenedorArchivo getContArchivoPlantillaTipo() {
		return contArchivoPlantillaTipo;
	}
	/**
	 * Asigna el objeto contArchivoPlantillaTipo
	 * 
	 * @param contArchivoPlantillaTipo
	 * Variable a asignar en contArchivoPlantillaTipo
	 */
	public void setContArchivoPlantillaTipo(ContenedorArchivo contArchivoPlantillaTipo) {
		this.contArchivoPlantillaTipo = contArchivoPlantillaTipo;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
