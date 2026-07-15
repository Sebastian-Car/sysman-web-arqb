/*-
 * PlanoInformeCovidControlador.java
 *
 * 1.0
 * 
 * 29/07/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.chipfut.ejb.EjbChipFutUnoRemote;
import com.sysman.chipfut.enums.PlanoChipSaldosyMovimientosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
import org.primefaces.event.FileUploadEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/07/2021
 * @author kmartinez
 */
@ManagedBean
@ViewScoped
public class PlanoInformeCovidControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean pesos;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean centavos;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean ejecutarVigencia;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean planoExcel;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean digitos;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anioTrabajo;
	
	/**
     * Almacena el valor de la fila
     */
    private String valorFila = "";
    
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String trimestre;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoEntidad;
	
    /**
     * Atributo que permite la lectura de los datos contenidos en el
     * archivo que se carga
     */
    private String extension;
    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    /**
     * Atributo que almacena la fila en la que se encuentra el
     * encabezado en el archivo a importar
     */
    private String fila;

    private String inconsistencia;
    /**
     * Atributi que almacena el contenido del excel leido
     */
    private String valoresImportar = "";
    
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	
	 /**
     * Este atributo se usa como auxiliar del componente referencia de
     * archivos cargarExcel y funciona como contenedor del archivo que
     * se desea cargar
     */
    private UploadedFile archivoCargacargarExcel;
    /**
     * Atributo que permite validar la visualizacion de las cuentas
     * con longitud de digitos seis
     */
    boolean validaDigito = false;

    private boolean plantillaExcel;
    private StringBuilder planApropiacion;
    private StringBuilder cargarCuentas;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    @EJB
    private EjbChipFutCeroRemote ejbChipFutCero;

    @EJB
    private EjbChipFutUnoRemote chipFutUnoRemote;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAnoTrabajo;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de PlanoInformeCovidControlador
	 */
	public PlanoInformeCovidControlador() {
		super();
		compania = SessionUtil.getCompania();
		codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
		//2313
		try {
			numFormulario = GeneralCodigoFormaEnum.PLANO_INFORME_COVID_CONTROLADOR
                    .getCodigo();
			validarPermisos();
			fila = "1";
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
//<CARGAR_LISTA>
		cargarListaAnoTrabajo();
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		anioTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        trimestre = "1";
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnoTrabajo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAnoTrabajo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAnoTrabajo = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											PlanoChipSaldosyMovimientosControladorUrlEnum.URL4198.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Generar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirGenerar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			if (planoExcel) {
				exportarDatos(workbook);

				archivoDescarga = JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(valoresImportar),
						SysmanFunciones.concatenar(SessionUtil.getCompaniaIngreso().getNombre(), "_CGN2020_004_COVID_19.txt"));

				valoresImportar = "";
			} else

			{
				archivoDescarga = JsfUtil.getArchivoDescarga(
						JsfUtil.serializarPlano(generarPlano(false, pesos, centavos, true)),
						SysmanFunciones.concatenar(SessionUtil.getCompaniaIngreso().getNombre(), "_CGN2020_004_COVID_19.txt"));
			}

		} catch (JRException | IOException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Recorre la hoja en la que se encuentran los datos que se van a importar y
	 * realiza el proceso de insercion de los mismos
	 * 
	 * @param workbook   Contenido del archivo seleccionado
	 * @param numeroHoja Numero de hoja con la informacion a importar
	 */
	private void exportarDatos(Workbook workbook) {
		try {

			String fecha = SysmanFunciones.convertirAFechaCadena(new Date(), "dd-MM-yyyy");
			valoresImportar = SysmanFunciones.concatenar("S", "\t", codigoEntidad, "\t", "1", nombreTrimestre(), "\t",
					anioTrabajo, "\t", "CGN2020_004_COVID_19", "\t", fecha, "\r\n");
			Sheet sheet = workbook.getSheetAt(0);

			// Filas
			for (int numFila = Integer.parseInt(fila); numFila <= sheet.getLastRowNum(); numFila++) {
				// Columnas
				for (int numColumna = 0; numColumna < Math.max(sheet.getRow(Integer.parseInt(fila)).getLastCellNum(),
						0); numColumna++) {

					if (numColumna != 1) {

						// Valida que la fila y la celda
						// especifica
						// que se
						// va a leer no sean nulas
						if (validarFilaCeldaVacia(sheet, numFila, numColumna)) {

							Row r = sheet.getRow(numFila);
							Cell cell = r.getCell(numColumna, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
							cell.setCellType(Cell.CELL_TYPE_STRING);
							// Almacena temporalmente el valor de
							// la
							// celda
							String valorColumna = !SysmanFunciones.validarVariableVacio(cell.getStringCellValue())
									? cell.getStringCellValue()
									: null;

							validarDigitosCuenta(numColumna, valorColumna);

						} else {
							return;
						}

					}
				}

				if (!valorFila.isEmpty()) {
					valoresImportar = SysmanFunciones.concatenar(valoresImportar, "D", "\t", valorFila, "\r\n");
					valorFila = "";
				}
				validaDigito = false;

			}
		} catch (ParseException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * Metodo que permite identificar si el archivo descarga con todas las cuentas o
	 * las cuentas con digitos de 6
	 * 
	 * @param numColumna
	 * @param valorColumna
	 */
	public void validarDigitosCuenta(int numColumna, String valorColumna) {

		if (digitos) {
			if (numColumna == 0 && valorColumna.length() == 6) {

				valorColumna = traerCuenta(valorColumna);
				validaDigito = true;
			}

			if (validaDigito) {
				valorFila = SysmanFunciones.concatenar(valorFila, valorColumna, " \t");
			} else {
				valorFila = "";
			}

		} else {
			if (numColumna == 0) {
				valorColumna = traerCuenta(valorColumna);
			}

			valorFila = SysmanFunciones.concatenar(valorFila, valorColumna, " \t");
		}

	}

	/**
	 * Metodo que retorna el trimestre seleccionado en numero, para encabezado del
	 * archivo plano
	 * 
	 * @return
	 */
	public String nombreTrimestre() {

		String valorTrimestre;
		switch (trimestre) {
		case "1":
			valorTrimestre = "0103";
			break;
		case "2":
			valorTrimestre = "0406";
			break;
		case "3":
			valorTrimestre = "0709";
			break;
		default:
			valorTrimestre = "1012";
			break;
		}
		return valorTrimestre;
	}

	/**
	 * Metodo que recibe codigo de cuenta y dependiendo la longitud de esta se
	 * separa con un punto(.)
	 * 
	 * @param cuenta
	 * @return
	 */
	public String traerCuenta(String cuenta) {
		String codigoCuenta = "";
		if (cuenta.length() > 0 && cuenta.length() < 2) {
			codigoCuenta = cuenta;
		} else if (cuenta.length() > 0 && cuenta.length() < 3) {
			codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1), ".", cuenta.substring(1, 2));
		} else if (cuenta.length() > 0 && cuenta.length() < 5) {
			codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1), ".", cuenta.substring(1, 2), ".",
					cuenta.substring(2, 4));
		} else if (cuenta.length() > 0 && cuenta.length() < 7) {
			codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1), ".", cuenta.substring(1, 2), ".",
					cuenta.substring(2, 4), ".", cuenta.substring(4, 6));
		}
		return codigoCuenta;
	}

	/**
	 * Valida si una celda especifica dentro de una hoja de datos viene nula o esta
	 * en blanco
	 * 
	 * @param sheet  Hoja de datos que se va a analizar
	 * @param rowNum Numero de fila que se evaluara
	 * @param column Numero de la columna dentro de la fila que se evaluara
	 * @return Verdadero si la celda posee valor
	 */
	private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
		boolean respuesta = false;
		Row rAux = sheet.getRow(rowNum);
		if (rAux != null) {
			Cell cellAux = rAux.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			if (cellAux != null) {
				cellAux.setCellType(Cell.CELL_TYPE_STRING);
				if (!SysmanFunciones.validarVariableVacio(cellAux.getStringCellValue())) {
					respuesta = true;

				}
			}

		}
		return respuesta;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            archivoDescarga = null;

                String archivoPlano = generarPlano(true, pesos, centavos, true);
                //String archivoPlanoTxt = generarPlano(true, pesos, centavos,false);
                String separadorRegistros = System
                                .getProperty("line.separator");
                String separadorColumnas = "\t";
                String nombreHoja = "CGN2020_004_COVID_19";
                String[] nombresArchivos = new String[2];
                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

                salidas[0] = new ByteArrayInputStream(
                                generarPlanoExcel(archivoPlano,
                                                separadorRegistros,
                                                separadorColumnas, nombreHoja));
                //salidas[1] = JsfUtil.serializarPlano(archivoPlanoTxt);

                nombresArchivos[0] = "CGN2020_004_COVID_19.xls";
                //nombresArchivos[1] = "ValidacionCGN2005.001.txt";
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas,
                                nombresArchivos, nombreHoja);

        }
        catch (JRException | IOException | SQLException | DRException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
		// </CODIGO_DESARROLLADO>
	}

	public boolean validarNumero(String cadena) {
		try {
			Double.parseDouble(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public void leerHoja(Workbook workbook, int hoja, int columnas, StringBuilder cadena, int filainicial) {
		cadena.append("TO_CLOB('");
		Sheet sheet = workbook.getSheetAt(hoja);
		Row fila;
		Cell celda;
		int num = 0;
		for (int i = filainicial; i < sheet.getLastRowNum() - 17; i++) {
			fila = sheet.getRow(i);
			for (int j = 2; j < columnas; j++) {

				celda = fila.getCell(j);
				if (celda != null) {
					num = num + (celda.getCellType() == 1 ? celda.getStringCellValue().replaceFirst("'", " ").length()
							: NumberToTextConverter.toText(celda.getNumericCellValue()).length());
					celda.setCellValue(0);

					cadena.append(celda.getCellType() == 1 ? celda.getStringCellValue().replaceFirst("'", " ")
							: NumberToTextConverter.toText(celda.getNumericCellValue()));

				} else {
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

		cadena.append("')" + "");
	}
	
	/**
	 * 
	 * Metodo invocado al ejecutar el comando remoto incosistencias en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void ejecutarinconsistencias() {
        // <CODIGO_DESARROLLADO>
        if (!inconsistencia.equals(null)) {

            String mensaje = idioma.getString("TB_TB4285");
            mensaje = mensaje.replace("s$archivoTxt$s",
                            "Cuentas_Existentes_Plantilla.txt");
            JsfUtil.agregarMensajeInformativo(mensaje);
        }
        else {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1637"));
        }
        return;
        // </CODIGO_DESARROLLADO>
    }

	
	
	/**
     * Metodo que genera el archivo excel de acuerdo al clob recibido
     * por parametro
     * 
     * @param cadena
     * @param fila
     * @param columna
     * @param nombreHoja
     * @return
     */
    public byte[] generarPlanoExcel(String cadena, String fila,
        String columna, String nombreHoja) {
        try {
            String hoja;
            Workbook workbook = new HSSFWorkbook();
            if (nombreHoja == null) {
                hoja = "Hoja1";
            }
            else {
                hoja = nombreHoja;
            }

            workbook.createSheet(hoja);
            Sheet sheet = workbook.getSheetAt(0);
            String[] separadorFila = cadena.split(fila);

            for (int i = 0; i < separadorFila.length; i++) {
                Row row = sheet.createRow(i);
                //recorre el string traido de la consulta y crea la celda en formato String y Double is es numerico
                String[] separadorColumna = separadorFila[i].split(columna);
                for (int j = 0; j < separadorColumna.length; j++) {
                	if(j!=1 && j!=2 && i>0) {
                    Cell newCell =row.createCell(j);
                    newCell.setCellValue(Double.parseDouble(separadorColumna[j]));                    
                    }
                	else {
                    	Cell newCell = row.createCell(j);                        
                    	newCell.setCellValue(separadorColumna[j]);
                    }sheet.autoSizeColumn(i);
                	}
              	}
                           

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            out.close();
            workbook.close();
            return out.toByteArray();

        }
        catch (IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return null;

    }

    
    public void leerCuentas(Workbook workbook, int hoja, int columnas,
            StringBuilder cadena, int filainicial) {
            cadena.append("TO_CLOB('");
            Sheet sheet = workbook.getSheetAt(hoja);
            Row fila;
            Cell celda;
            int num = 0;
            for (int i = filainicial; i < sheet.getLastRowNum() - 17; i++) {
                fila = sheet.getRow(i);

                celda = fila.getCell(0);
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

                cadena.append(SysmanConstantes.SEPARADOR_REG);
            }

            cadena.append("')"
                + "");
        }
    
 
    private String generarInconsisteciaCuentas() {

        String archivo;

        try {
            archivo = chipFutUnoRemote.validarCuentas(compania,
                            Integer.parseInt(anioTrabajo),
                            cargarCuentas.toString());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }

        return archivo;

    }

    private String generarCuentasExistentes() {

        String archivo;

        try {
            archivo = chipFutUnoRemote.cuentasExistentes(compania,
                            Integer.parseInt(anioTrabajo),
                            cargarCuentas.toString());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }

        return archivo;

    }

    private String enviarFormatoEspecial() {

        String archivo = "";

        try {
            archivo = chipFutUnoRemote.enviarFormatoEspecial(compania,
                            Integer.parseInt(anioTrabajo),
                            Integer.parseInt(trimestre),
                            cargarCuentas.toString());

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return archivo;

    }
    
    /**
     * Genera archivo plano de saldos y movimientos contables
     * 
     * @param exportaExcel
     * Indica si se va a exportar a MS Excel.
     * @return archivo plano
     */
    private String generarPlano(boolean exportaExcel, boolean miles,
        boolean centavos, boolean formato) {

        String archivo;
        String cadenaExcel;
        String cadenaTxt;
        try {
            archivo = ejbChipFutCero.generarPlanoSaldoMovimiento(compania,
                            Integer.parseInt(anioTrabajo),
                            Integer.parseInt(trimestre), codigoEntidad, 6,
                            exportaExcel, miles, centavos, true);

            String[] cadenas = archivo.split("&");
            cadenaExcel = cadenas[0];
            cadenaTxt = cadenas[1];

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }

        if (formato) {
            return cadenaExcel;
        }
        else {
            return cadenaTxt;
        }

    }
    
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * 
	 * Metodo ejecutado al cargar un archivo desde el control Excel
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 *
	 */
	public void cargarArchivoExcel(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control AnoTrabajo
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnoTrabajo() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control PlantillaExcel
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarPlantillaExcel() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control PlanoPExcel
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarPlanoPExcel() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable plantillaExcel
	 * 
	 * @return plantillaExcel
	 */
	public boolean getPlantillaExcel() {
		return plantillaExcel;
	}

	/**
	 * Asigna la variable plantillaExcel
	 * 
	 * @param plantillaExcel Variable a asignar en plantillaExcel
	 */
	public void setPlantillaExcel(boolean plantillaExcel) {
		this.plantillaExcel = plantillaExcel;
	}

	/**
	 * Retorna la variable pesos
	 * 
	 * @return pesos
	 */
	public boolean getPesos() {
		return pesos;
	}

	/**
	 * Asigna la variable pesos
	 * 
	 * @param pesos Variable a asignar en pesos
	 */
	public void setPesos(boolean pesos) {
		this.pesos = pesos;
	}

	/**
	 * Retorna la variable centavos
	 * 
	 * @return centavos
	 */
	public boolean getCentavos() {
		return centavos;
	}

	/**
	 * Asigna la variable centavos
	 * 
	 * @param centavos Variable a asignar en centavos
	 */
	public void setCentavos(boolean centavos) {
		this.centavos = centavos;
	}

	/**
	 * Retorna la variable ejecutarVigencia
	 * 
	 * @return ejecutarVigencia
	 */
	public boolean getEjecutarVigencia() {
		return ejecutarVigencia;
	}

	/**
	 * Asigna la variable ejecutarVigencia
	 * 
	 * @param ejecutarVigencia Variable a asignar en ejecutarVigencia
	 */
	public void setEjecutarVigencia(boolean ejecutarVigencia) {
		this.ejecutarVigencia = ejecutarVigencia;
	}

	/**
	 * Retorna la variable planoExcel
	 * 
	 * @return planoExcel
	 */
	public boolean getPlanoExcel() {
		return planoExcel;
	}

	/**
	 * Asigna la variable planoExcel
	 * 
	 * @param planoExcel Variable a asignar en planoExcel
	 */
	public void setPlanoExcel(boolean planoExcel) {
		this.planoExcel = planoExcel;
	}

	/**
	 * Retorna la variable digitos
	 * 
	 * @return digitos
	 */
	public boolean getDigitos() {
		return digitos;
	}

	/**
	 * Asigna la variable digitos
	 * 
	 * @param digitos Variable a asignar en digitos
	 */
	public void setDigitos(boolean digitos) {
		this.digitos = digitos;
	}

	/**
	 * Retorna la variable anioTrabajo
	 * 
	 * @return anioTrabajo
	 */
	public String getAnioTrabajo() {
		return anioTrabajo;
	}

	/**
	 * Asigna la variable anioTrabajo
	 * 
	 * @param anioTrabajo Variable a asignar en anioTrabajo
	 */
	public void setAnioTrabajo(String anioTrabajo) {
		this.anioTrabajo = anioTrabajo;
	}

	/**
	 * Retorna la variable trimestre
	 * 
	 * @return trimestre
	 */
	public String getTrimestre() {
		return trimestre;
	}

	/**
	 * Asigna la variable trimestre
	 * 
	 * @param trimestre Variable a asignar en trimestre
	 */
	public void setTrimestre(String trimestre) {
		this.trimestre = trimestre;
	}

	/**
	 * Retorna la variable codigoEntidad
	 * 
	 * @return codigoEntidad
	 */
	public String getCodigoEntidad() {
		return codigoEntidad;
	}

	/**
	 * Asigna la variable codigoEntidad
	 * 
	 * @param codigoEntidad Variable a asignar en codigoEntidad
	 */
	public void setCodigoEntidad(String codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnoTrabajo
	 * 
	 * @return listaAnoTrabajo
	 */
	public List<Registro> getListaAnoTrabajo() {
		return listaAnoTrabajo;
	}

	/**
	 * Asigna la lista listaAnoTrabajo
	 * 
	 * @param listaAnoTrabajo Variable a asignar en listaAnoTrabajo
	 */
	public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
		this.listaAnoTrabajo = listaAnoTrabajo;
	}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
