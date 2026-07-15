/*-
 * FrmConsolidaInfSIAControlador.java
 *
 * 1.0
 * 
 * 26/11/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sia;
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
import org.apache.poi.ss.usermodel.DateUtil;
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
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sia.ejb.EjbSiaCeroRemote;
import com.sysman.sia.enums.FrmConsolidaInfSIAControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 26/11/2024
 * @author User
 */
@ManagedBean
@ViewScoped
public class  FrmConsolidaInfSIAControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreProceso;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos cargarExcel y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;

	private RegistroDataModelImpl listaProceso;
	private String modulo;
	private StringBuilder cadena;
	private String extension;

	private boolean visibleDialogo;

	private String mensajeDialogo;

	String sobreescribir = null;


	@EJB
	private EjbSiaCeroRemote ejbSiaCeroRemote;
	private boolean validado;
	private String  tablaDestino;
	private String mensajeError = "";
	private boolean aprobado = true;
	private StreamedContent archivoDescarga;
	private String columnasNumber;
	private String columnasTexto;
	private String columnasFecha;

	int num = 0;




	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmConsolidaInfSIAControlador
	 */
	public FrmConsolidaInfSIAControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		contArchivocargarExcel = new ContenedorArchivo();

		try {
			numFormulario = GeneralCodigoFormaEnum.FRMCONSOLIDAR_INFORMES_SIA_CONTROLADOR.getCodigo();
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaProceso();
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
	 * Carga la lista listaProceso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProceso(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmConsolidaInfSIAControladorUrlEnum.URL1750009.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), "2");
		param.put(GeneralParameterEnum.SUBTIPO.getName(),"15");

		listaProceso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT3263
	 * en la vista
	 *
	 *
	 */

	public boolean validarArchivo() {

		String archivo = String.valueOf(contArchivocargarExcel.getArchivo());
		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		}
		else {
			String extension = archivo
					.substring(archivo.indexOf('.'), archivo.length())
					.toLowerCase();
			if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
				return true;
			}
			else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
				return false;
			}
		}
	}

	public void oprimirCargar() {
		FileInputStream file = null;
		cadena = new StringBuilder();

		try {
			if (validarArchivo()) {
				tablaDestino = null;
				int opcion;

				String rutaArchivo = contArchivocargarExcel.getArchivo()
						.getPath();
				extension = rutaArchivo
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

				switch (nombreProceso) {
				case "F01_AGR CATALOGO DE CUENTA":
					tablaDestino = "SIA_CATALOGO_CUENTAS";
					leerHoja(workbook, 0, 11, cadena, 1);					

					break;
				case "F03A_CDN MOVIMIENTO DE BANCOS":
					tablaDestino = "SIA_MOVIMIENTO_BANCOS";
					leerHoja(workbook, 0, 15, cadena, 1);

					break;
				case "F04_AGR POLIZAS DE ASEGURAMIENTO":
					tablaDestino = "SIA_POLIZAS_ASEGURAMIENTO";
					leerHoja(workbook, 0, 15, cadena, 1);

					break;
				case "F06_AGR EJECUCION PRESUPUESTAL DE INGRESOS":
					tablaDestino = "SIA_EJECUCION_PRESUPUESTAL_INGRESOS";
					leerHoja(workbook, 0, 10, cadena, 1);

					break;
				case "F06A_CDN RELACION DE INGRESOS":
					tablaDestino = "SIA_RELACION_INGRESOS";
					leerHoja(workbook, 0, 11, cadena, 1);

					break;
				case "F07_AGR EJECUCION PRESUPUESTAL DE GASTOS":
					tablaDestino = "SIA_EJECUCION_PRESUPUESTAL_GASTOS";
					leerHoja(workbook, 0, 17, cadena, 1);

					break;
				case "F07B_CDN RELACION DE PAGOS":
					tablaDestino = "SIA_RELACION_PAGOS";
					leerHoja(workbook, 0, 20, cadena, 1);

					break;
				case "F08A_AGR MODIFICACIONES PRESUPUESTO DE INGRESOS":
					tablaDestino = "SIA_MODIFICACIONES_PRESUPUESTO_INGRESOS";
					leerHoja(workbook, 0, 9, cadena, 1);

					break;
				case "F08B_AGR MODIFICACIONES PRESUPUESTO DE GASTOS":
					tablaDestino = "SIA_MODIFICACIONES_PRESUPUESTO_GASTOS";
					leerHoja(workbook, 0, 13, cadena, 1);

					break;
				case "F10_AGR EJECUCION RESERVA PRESUPUESTAL":
					tablaDestino = "SIA_EJECUCION_RESERVA_PRESUPUESTAL";
					leerHoja(workbook, 0, 9, cadena, 1);

					break;
				case "F11_AGR EJECUCION CUENTAS POR PAGAR":
					tablaDestino = "SIA_EJECUCION_CUENTAS_PORPAGAR";
					leerHoja(workbook, 0, 8, cadena, 1);

					break;
				case "F13A_AGR CONTRATACION":
					tablaDestino = "SIA_CONTRATACION";
					leerHoja(workbook, 0, 29, cadena, 1);

					break;
				default:
					JsfUtil.agregarMensajeError("Informe no valido, por favor configure los reportes correctamente");
					aprobado = false;
					break;
				}
				workbook.close();

				if(mensajeError.isEmpty()) {
					archivoDescarga = null;
					if (aprobado) {
						archivoDescarga = null;
						mensajeDialogo = ejbSiaCeroRemote.subirConsolidadoSia(tablaDestino, cadena.toString(), SessionUtil.getUser().getCodigo(),"NO");
						if(mensajeDialogo != null) {
							mensajeDialogo = mensajeDialogo.replace("VRep",nombreProceso);
							visibleDialogo = true;
						}else {
							visibleDialogo = false;
							JsfUtil.agregarMensajeInformativo("Proceso Ejecutado Exitosamente.");
						}
					}
				}else{
					try {
						archivoDescarga = JsfUtil.getArchivoDescarga(
								JsfUtil.serializarPlano(mensajeError),
								"Inconsistencias.txt");
					} catch (IOException | JRException e) {
						e.printStackTrace();
					}

				}

			}


		}
		catch (IOException | NumberFormatException | SystemException e) {
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
	}


	public void leerHoja(Workbook workbook, int hoja, int columnas,
			StringBuilder cadena, int filainicial)
	{
		aprobado=true;
		visibleDialogo = false;
		mensajeError = "";
		cadena.append("TO_CLOB('");
		Sheet sheet = workbook.getSheetAt(hoja);
		Row fila;
		Cell celda;

		for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++)
		{
			fila = sheet.getRow(i);

			if (fila.getCell(0) != null)
			{

				for (int j = 0; j < columnas; j++)
				{
					celda = fila.getCell(j);
					if (celda != null && !(celda.getCellType() == Cell.CELL_TYPE_BLANK && celda.toString().isEmpty())) 
						aprobado = validarTipoDato(j+1,celda);
					else
					{
						mensajeError  += "La plantilla contiene celdas vacias en la fila " + (i + 1) + " columna " + (j + 1) +"\n";
						aprobado  = false;
					}
					if (num >= 4000)
					{
						cadena.append("') || TO_CLOB('");
						num = 0;
					}
					cadena.append(SysmanConstantes.SEPARADOR_COL);
				}
				cadena.append(SysmanConstantes.SEPARADOR_REG);
			}else {

			}

		}
		cadena.append("')");

	}


	private boolean validarTipoDato(int j, Cell celda) {
		aprobado = true;
		switch (nombreProceso) {
		case "F01_AGR CATALOGO DE CUENTA":
			if (j == 2 || j == 3 || j == 4 || j == 7 || j == 8 || j == 9 || j == 10 || j == 11) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 5 || j == 6) {
				aprobado = validaColumnasTexto(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F01_AGR CATALOGO DE CUENTA: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F03A_CDN MOVIMIENTO DE BANCOS":
			if (j == 2 || j == 3 || j == 4 || j == 9 || j == 10 || j == 11 || j == 12 || j ==  13 || j ==  14 || j == 15) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 5 || j == 6 || j == 7 || j == 8) {
				aprobado = validaColumnasTexto(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F03A_CDN MOVIMIENTO DE BANCOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F04_AGR POLIZAS DE ASEGURAMIENTO":
			if (j == 2 || j == 3 || j == 4 || j == 15) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 5 || j == 6 || j == 9 || j == 10 || j == 11 || j == 12 || j == 13 || j == 14) {
				aprobado = validaColumnasTexto(celda,j);
			} else if (j == 7 || j == 8) {
				aprobado = validaColumnasFechas(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F04_AGR POLIZAS DE ASEGURAMIENTO: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F06_AGR EJECUCION PRESUPUESTAL DE INGRESOS":
			if (j == 2 || j == 3 || j == 4 || j == 7 || j == 8 || j == 9 || j == 10) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 5 || j == 6) {
				aprobado = validaColumnasTexto(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F06_AGR EJECUCION PRESUPUESTAL DE INGRESOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F06A_CDN RELACION DE INGRESOS":
			if (j == 2 || j == 3 || j == 4 || j == 10 ) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 9 || j == 5 || j == 7 || j == 8  || j == 11 ) {
				aprobado = validaColumnasTexto(celda,j);
			} else if (j == 6) {
				aprobado = validaColumnasFechas(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F06A_CDN RELACION DE INGRESOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F07_AGR EJECUCION PRESUPUESTAL DE GASTOS":
			if (j == 2 || j == 3 || j == 4 || j == 8 || j == 9 || j == 10 || j == 11 || j == 12 || j == 13 || j == 14 || j == 15 || j == 16 || j == 17) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 6 || j == 5 || j == 7) {
				aprobado = validaColumnasTexto(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F07_AGR EJECUCION PRESUPUESTAL DE GASTOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F07B_CDN RELACION DE PAGOS":
			if (j == 2 || j == 3 || j == 4  || j == 11 || j == 13 || j == 14 || j == 15 || j == 16 || j == 17) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 6 || j == 7 || j == 8 || j == 9 || j == 10 || j == 12 || j == 18 || j == 19 || j == 20) {
				aprobado = validaColumnasTexto(celda,j);
			} else if (j == 5) {
				aprobado = validaColumnasFechas(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F07B_CDN RELACION DE PAGOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F08A_AGR MODIFICACIONES PRESUPUESTO DE INGRESOS":
			if (j == 2 || j == 3 || j == 4 || j == 8 || j == 9) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 6 || j == 5) {
				aprobado = validaColumnasTexto(celda,j);
			} else if (j == 7) {
				aprobado = validaColumnasFechas(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F08A_AGR MODIFICACIONES PRESUPUESTO DE INGRESOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F08B_AGR MODIFICACIONES PRESUPUESTO DE GASTOS":
			if (j == 2 || j == 3 || j == 4  || j == 8 || j == 9 || j == 10 || j == 11 || j == 12 || j == 13) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 6 || j == 5) {
				aprobado = validaColumnasTexto(celda,j);
			} else if (j == 7) {
				aprobado = validaColumnasFechas(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F08B_AGR MODIFICACIONES PRESUPUESTO DE GASTOS: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F10_AGR EJECUCION RESERVA PRESUPUESTAL":
			if (j == 2 || j == 3 || j == 4 || j == 7 || j == 8 || j == 9) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1  || j == 5 || j == 6 ) {
				aprobado = validaColumnasTexto(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F10_AGR EJECUCION RESERVA PRESUPUESTAL: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F11_AGR EJECUCION CUENTAS POR PAGAR":
			if (j == 2 || j == 3 || j == 4 || j == 7) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 5 || j == 6 || j == 8) {
				aprobado = validaColumnasTexto(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F11_AGR EJECUCION CUENTAS POR PAGAR: " + j + "\n";
				aprobado = false;
			}
			break;

		case "F13A_AGR CONTRATACION":
			if (j == 2 || j == 3 || j == 4  || j == 9 || j == 11 || j == 14 ||  j == 20  || j == 26 || j == 27) {
				aprobado = validaColumnasNumericas(celda,j);
			} else if (j == 1 || j == 7 || j == 8 || j == 10 || j == 16 || j == 5 || j == 6 || j == 8 || j == 12 || j == 18 || j == 19 || j == 23 || j == 25) {
				aprobado = validaColumnasTexto(celda,j);
			} else if (j == 13 || j == 15 || j == 17 || j == 21 || j == 22 || j == 24 || j == 28 || j == 29) {
				aprobado = validaColumnasFechas(celda,j);
			} else {
				mensajeError += "Columna no válida para el reporte F13A_AGR CONTRATACION: " + j + "\n";
				aprobado = false;
			}
			break;

		default:
			JsfUtil.agregarMensajeError("Proceso no configurado: " + nombreProceso);
			aprobado = false;
			break;
		}

		return aprobado;
	}


	public void aceptarvalidarInfo() {
		try {
			if (aprobado) {
				mensajeDialogo = ejbSiaCeroRemote.subirConsolidadoSia(tablaDestino, cadena.toString(), SessionUtil.getUser().getCodigo(),"SI");
				JsfUtil.agregarMensajeInformativo("Proceso Ejecutado Exitosamente.");

			}else {
				try { 
					archivoDescarga = JsfUtil.getArchivoDescarga(
							JsfUtil.serializarPlano(mensajeError),
							"errores.txt");
				} catch (IOException | JRException e) {
					e.printStackTrace();
				}

			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private boolean validaColumnasTexto(Cell celda,int j){
		try {
			String valorTexto = celda.getStringCellValue();
			num += valorTexto.length();
			cadena.append(valorTexto);
		} catch (Exception e) {
			mensajeError += "Error al procesar texto "+ celda +" en la columna : " + j + "\n";
			aprobado  = false;

		}
		return aprobado;
	}


	private boolean validaColumnasNumericas(Cell celda, int j) {
		try {
			if (celda.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				double valor = celda.getNumericCellValue();
				if (valor == Math.floor(valor)) {
					// Es un número entero
					String numeroEntero = String.valueOf((int) valor);
					num += numeroEntero.length();
					cadena.append(numeroEntero);
				} else {
					// Es un número decimal
					String numeroDecimal = String.valueOf(valor);
					num += numeroDecimal.length();
					cadena.append(numeroDecimal);
				}
			}else {
				throw new IllegalArgumentException("La celda no contiene un valor numérico.");
			}
		}catch (Exception e) {
			mensajeError += "Error al procesar número " + celda + " en la columna: " + j + "\n";
			aprobado = false;
		}
		return aprobado;
	}

	private boolean validaColumnasFechas(Cell celda,int j){
		try {
			if (celda.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(celda)) {
				Date fecha = celda.getDateCellValue();
				String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
				num += fechaFormateada.length();
				cadena.append(fechaFormateada);
			}
		} catch (Exception e) {
			mensajeError += "Error al procesar fecha "+ celda +" en la columna : " + j + "\n";
			aprobado  = false;

		}

		return aprobado;
	}



	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProceso
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProceso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		nombreProceso = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		validado = false;
	}

	/**
	 * Retorna la variable nombreProceso
	 * 
	 * @return  nombreProceso
	 */
	public String getNombreProceso() {
		return nombreProceso;
	}
	/**
	 * Asigna la variable  nombreProceso
	 * 
	 * @param  nombreProceso
	 * Variable a asignar en  nombreProceso
	 */
	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
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
	 * @param contArchivocargarExcel
	 * Variable a asignar en contArchivocargarExcel
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}
	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public RegistroDataModelImpl getListaProceso() {
		return listaProceso;
	}
	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en  listaProceso
	 */
	public void setListaProceso(RegistroDataModelImpl listaProceso) {
		this.listaProceso = listaProceso;
	}
	public boolean getVisibleDialogo() {
		return visibleDialogo;
	}
	public void setVisibleDialogo(boolean visibleDialogo) {
		this.visibleDialogo = visibleDialogo;
	}
	public String getMensajeDialogo() {
		return mensajeDialogo;
	}
	public void setMensajeDialogo(String mensajeDialogo) {
		this.mensajeDialogo = mensajeDialogo;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}



}
