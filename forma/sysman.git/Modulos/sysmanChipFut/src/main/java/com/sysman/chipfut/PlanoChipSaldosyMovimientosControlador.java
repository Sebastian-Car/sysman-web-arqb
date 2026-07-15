/*-
 * PlanoChipSaldosyMovimientosControlador.java
 *
 * 1.0
 * 
 * 18/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para general el plano de Saldos y movimientos CGN 001
 * Nº TAR Nº1000083960
 * 
 * @version 1.0, 18/05/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class PlanoChipSaldosyMovimientosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor de la casilla de verificacion
     * "En miles de pesos"
     */
    private boolean pesos;
    /**
     * Atributo que almacena el valor de la casilla de verificacion
     * "Ejecutar cierre d vigencia"
     */
    private boolean ejecutarVigencia;
    /**
     * Atributi que almacena el valor de la casilla de verificacion
     * "Generar archivo plano a partir de un archivo de excel"
     */
    private boolean planoExcel;
    /**
     * Atributo que almacena el valor de la casilla de verificacion
     * "Generar a 6 digitos"
     */
    private boolean digitos;
    /**
     * Atributo que almacena el valor del campo
     */
    private boolean centavos;
    /**
     * Almacena el valor de la fila
     */
    private String valorFila = "";
    /**
     * Atributo que almacena el valor del codigo del semestre
     * seleccinado
     */
    private String trimestre;
    /**
     * Atributo que almacena el numero de ano seleccioando
     */
    private String anioTrabajo;
    /**
     * Atributo que almacena el codigo de la entidad ingresada
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
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
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
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ANO
     */
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMes;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
	private final String modulo;
    private String mes;
    private boolean ckMensual;
    private boolean visibleMensual;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    
    /**
     * Crea una nueva instancia de
     * PlanoChipSaldosyMovimientosControlador
     */
    public PlanoChipSaldosyMovimientosControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
		modulo = SessionUtil.getModulo();
        //GENERAR INFORME SALDOS Y MOVIMIENTOS MENSUAL
        try {
            // 1381
            numFormulario = GeneralCodigoFormaEnum.PLANO_CHIP_SALDOSY_MOVIMIENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            fila = "1";
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar() {
        // <CARGAR_LISTA>
        
        try {
        	visibleMensual=	"SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"GENERAR INFORME SALDOS Y MOVIMIENTOS MENSUAL", SessionUtil.getModulo(), new Date(), true), "NO")) ? true : false;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
        cargarListaAnoTrabajo();
        
        if(visibleMensual)
        {
        	cargarListaMes();
        }
        
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anioTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
        trimestre = "1";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanoChipSaldosyMovimientosControladorUrlEnum.URL4198
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		PlanoChipSaldosyMovimientosControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Generar en la vista
     *
     *
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            if (planoExcel) {
                exportarDatos(workbook);

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(JsfUtil.serializarPlano(
                                                valoresImportar),
                                                SysmanFunciones.concatenar(
                                                                SessionUtil.getCompaniaIngreso()
                                                                                .getNombre(),
                                                                "_CGN2005_001.txt"));

                valoresImportar = "";
            }
            else

            {
                archivoDescarga = JsfUtil
                                .getArchivoDescarga(JsfUtil.serializarPlano(
                                                generarPlano(false, pesos,
                                                                centavos,
                                                                true)),
                                                SysmanFunciones.concatenar(
                                                                SessionUtil.getCompaniaIngreso()
                                                                                .getNombre(),
                                                                "_CGN2005_001.txt"));
            }

        }
        catch (JRException | IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Recorre la hoja en la que se encuentran los datos que se van a
     * importar y realiza el proceso de insercion de los mismos
     * 
     * @param workbook
     * Contenido del archivo seleccionado
     * @param numeroHoja
     * Numero de hoja con la informacion a importar
     */
    private void exportarDatos(Workbook workbook) {
        try {

            String fecha = SysmanFunciones.convertirAFechaCadena(new Date(),
                            "dd-MM-yyyy");
            valoresImportar = SysmanFunciones.concatenar("S", "\t",
                            codigoEntidad, "\t", "1", nombreTrimestre(), "\t",
                            anioTrabajo,
                            "\t",
                            "CGN2005_001_SALDOS_Y_MOVIMIENTOS", "\t", fecha,
                            "\r\n");
            Sheet sheet = workbook.getSheetAt(0);

            // Filas
            for (int numFila = Integer.parseInt(fila); numFila <= sheet
                            .getLastRowNum(); numFila++) {
                // Columnas
                for (int numColumna = 0; numColumna < Math
                                .max(sheet.getRow(Integer.parseInt(fila))
                                                .getLastCellNum(),
                                                0); numColumna++) {

                    if (numColumna != 1) {

                        // Valida que la fila y la celda
                        // especifica
                        // que se
                        // va a leer no sean nulas
                        if (validarFilaCeldaVacia(sheet, numFila, numColumna)) {

                            Row r = sheet.getRow(numFila);
                            Cell cell = r.getCell(numColumna,
                                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            // Almacena temporalmente el valor de
                            // la
                            // celda
                            String valorColumna = !SysmanFunciones
                                            .validarVariableVacio(
                                                            cell.getStringCellValue())
                                                                ? cell.getStringCellValue()
                                                                : null;

                            validarDigitosCuenta(numColumna, valorColumna);

                        }
                        else {
                            return;
                        }

                    }
                }

                if (!valorFila.isEmpty()) {
                    valoresImportar = SysmanFunciones.concatenar(
                                    valoresImportar, "D", "\t", valorFila,
                                    "\r\n");
                    valorFila = "";
                }
                validaDigito = false;

            }
        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo que permite identificar si el archivo descarga con todas
     * las cuentas o las cuentas con digitos de 6
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
                valorFila = SysmanFunciones.concatenar(valorFila, valorColumna,
                                " \t");
            }
            else {
                valorFila = "";
            }

        }
        else {
            if (numColumna == 0) {
                valorColumna = traerCuenta(valorColumna);
            }

            valorFila = SysmanFunciones.concatenar(valorFila, valorColumna,
                            " \t");
        }

    }

    /**
     * Metodo que retorna el trimestre seleccionado en numero, para
     * encabezado del archivo plano
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
     * Metodo que recibe codigo de cuenta y dependiendo la longitud de
     * esta se separa con un punto(.)
     * 
     * @param cuenta
     * @return
     */
    public String traerCuenta(String cuenta) {
        String codigoCuenta = "";
        if (cuenta.length() > 0 && cuenta.length() < 2) {
            codigoCuenta = cuenta;
        }
        else if (cuenta.length() > 0 && cuenta.length() < 3) {
            codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1),
                            ".", cuenta.substring(1, 2));
        }
        else if (cuenta.length() > 0 && cuenta.length() < 5) {
            codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1),
                            ".", cuenta.substring(1, 2), ".",
                            cuenta.substring(2, 4));
        }
        else if (cuenta.length() > 0 && cuenta.length() < 7) {
            codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1),
                            ".", cuenta.substring(1, 2), ".",
                            cuenta.substring(2, 4), ".",
                            cuenta.substring(4, 6));
        }
        return codigoCuenta;
    }

    /**
     * Valida si una celda especifica dentro de una hoja de datos
     * viene nula o esta en blanco
     * 
     * @param sheet
     * Hoja de datos que se va a analizar
     * @param rowNum
     * Numero de fila que se evaluara
     * @param column
     * Numero de la columna dentro de la fila que se evaluara
     * @return Verdadero si la celda posee valor
     */
    private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
        boolean respuesta = false;
        Row rAux = sheet.getRow(rowNum);
        if (rAux != null) {
            Cell cellAux = rAux.getCell(column,
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cellAux != null) {
                cellAux.setCellType(Cell.CELL_TYPE_STRING);
                if (!SysmanFunciones.validarVariableVacio(
                                cellAux.getStringCellValue())) {
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
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            archivoDescarga = null;

            if (plantillaExcel) {

                planApropiacion = new StringBuilder();
                cargarCuentas = new StringBuilder();

                // leerHoja(workbook, 0, 8, planApropiacion, 10);
                leerCuentas(workbook, 0, 1, cargarCuentas, 10);

                inconsistencia = generarInconsisteciaCuentas();

                String enviarFormato = enviarFormatoEspecial();

                String cuentasExis = generarCuentasExistentes();

                Sheet sheet = workbook.getSheetAt(0);

                String[] filaExcel = enviarFormato
                                .split(SysmanConstantes.SEPARADOR_REG);

                for (int i = 0; i < filaExcel.length; i++) {
                    Row row = sheet.getRow(i + 10);

                    String[] columnas = filaExcel[i]
                                    .split(SysmanConstantes.SEPARADOR_COL);

                    for (int j = 0; j < columnas.length; j++) {
                        Cell cell = row.getCell(j);

                        if (validarNumero(columnas[j])) {
                            cell.setCellValue(Double.parseDouble(columnas[j]));
                        }
                        else {
                            cell.setCellValue(columnas[j]);
                        }

                    }

                }
                FormulaEvaluator evaluator = workbook.getCreationHelper()
                                .createFormulaEvaluator();
                evaluator.evaluateAll();

                workbook.write(out);

                // workbook.close();
                String[] nombresArchivos = new String[3];

                ByteArrayInputStream[] salidas = new ByteArrayInputStream[3];

                salidas[0] = JsfUtil.serializarPlano(inconsistencia);

                salidas[1] = new ByteArrayInputStream(
                                out.toByteArray());

                salidas[2] = JsfUtil.serializarPlano(cuentasExis);

                nombresArchivos[0] = "Cuentas_Existentes_Plantilla.txt";
                nombresArchivos[1] = "CGN2005.001.xlsx";
                nombresArchivos[2] = "Cuentas_Existentes_Base_Datos.txt";

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas,
                                nombresArchivos, "CGN2005.001");
                ejecutarinconsistencias();
            }
            else {

                String archivoPlano = generarPlano(true, pesos, centavos, true);
                String archivoPlanoTxt = generarPlano(true, pesos, centavos,
                                false);
                String separadorRegistros = System
                                .getProperty("line.separator");
                String separadorColumnas = "\t";
                String nombreHoja = "CGN2005.001";
                String[] nombresArchivos = new String[2];
                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

                salidas[0] = new ByteArrayInputStream(
                                generarPlanoExcel(archivoPlano,
                                                separadorRegistros,
                                                separadorColumnas, nombreHoja));
                salidas[1] = JsfUtil.serializarPlano(archivoPlanoTxt);

                nombresArchivos[0] = "CGN2005.001_Sin_Plantilla.xls";
                nombresArchivos[1] = "ValidacionCGN2005.001.txt";
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas,
                                nombresArchivos, nombreHoja);
            }

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
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena, int filainicial) {
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
                    num = num
                        + (celda.getCellType() == 1
                            ? celda.getStringCellValue()
                                            .replaceFirst("'", " ").length()
                            : NumberToTextConverter
                                            .toText(celda.getNumericCellValue())
                                            .length());
                    celda.setCellValue(0);

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
    }

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
				// recorre el string traido de la consulta y crea la celda en formato String y
				// Double is es numerico
				String[] separadorColumna = separadorFila[i].split(columna);
				for (int j = 0; j < separadorColumna.length; j++) {
					if (j!=1 && i>0) {
						Cell newCell =row.createCell(j);
						if (!separadorColumna[j].isEmpty()) {
							newCell.setCellValue(Double.parseDouble(separadorColumna[j]));							
						}
					} else {
						Cell newCell = row.createCell(j);
						newCell.setCellValue(separadorColumna[j]);
					}
					sheet.autoSizeColumn(i);
				}

			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			workbook.write(out);

			out.close();
			workbook.close();
			return out.toByteArray();

		} catch (IOException e) {
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
			if (visibleMensual && ckMensual) {
				archivo = ejbChipFutCero.generarPlanoSaldoMovimientoMensual(compania, Integer.parseInt(anioTrabajo),
						Integer.parseInt(mes), codigoEntidad, 6, exportaExcel, miles, centavos, false);

				String[] cadenas = archivo.split("&");
				cadenaExcel = cadenas[0];
				cadenaTxt = cadenas[1];

			} else {

				archivo = ejbChipFutCero.generarPlanoSaldoMovimiento(compania, Integer.parseInt(anioTrabajo),
						Integer.parseInt(trimestre), codigoEntidad, 6, exportaExcel, miles, centavos, false);

				String[] cadenas = archivo.split("&");
				cadenaExcel = cadenas[0];
				cadenaTxt = cadenas[1];

			}
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return null;
		}

		if (formato) {
			return cadenaExcel;
		} else {
			return cadenaTxt;
		}

	}

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Excel
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoExcel(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        workbook = null;
        try {
            InputStream is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }
            String rutaArchivo = event.getFile().getFileName();
            extension = FilenameUtils.getExtension(rutaArchivo);
            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if (workbook == null) {
                if ("xls".equals(extension)) {
                    workbook = new HSSFWorkbook(is);
                }
                else {
                    workbook = new XSSFWorkbook(is);
                }
            }
        }

        catch (IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoTrabajo
     * 
     * 
     */
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PlanoPExcel
     * 
     * 
     */
    public void cambiarPlanoPExcel() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PlantillaExcel
     * 
     * 
     */
    public void cambiarPlantillaExcel() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarmensual() {
        //<CODIGO_DESARROLLADO>
       //</CODIGO_DESARROLLADO>
   }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
     * @param pesos
     * Variable a asignar en pesos
     */
    public void setPesos(boolean pesos) {
        this.pesos = pesos;
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
     * @param ejecutarVigencia
     * Variable a asignar en ejecutarVigencia
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
     * @param planoExcel
     * Variable a asignar en planoExcel
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
     * @param digitos
     * Variable a asignar en digitos
     */
    public void setDigitos(boolean digitos) {
        this.digitos = digitos;
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
     * Asigna la variable codigoEquivalente
     * 
     * @param codigoEquivalente
     * Variable a asignar en codigoEquivalente
     */
    public void setCentavos(boolean centavos) {
        this.centavos = centavos;
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
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
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
     * @param anioTrabajo
     * Variable a asignar en anioTrabajo
     */
    public void setAnioTrabajo(String anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
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
     * @param codigoEntidad
     * Variable a asignar en codigoEntidad
     */
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivocargarExcel
     * 
     * @return contArchivocargarExcel
     */
    public UploadedFile getArchivoCargacargarExcel() {
        return archivoCargacargarExcel;
    }

    /**
     * Asigna el objeto contArchivocargarExcel
     * 
     * @param contArchivocargarExcel
     * Variable a asignar en contArchivocargarExcel
     */
    public void setArchivoCargacargarExcel(
        UploadedFile archivoCargacargarExcel) {
        this.archivoCargacargarExcel = archivoCargacargarExcel;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * @return the plantillaExcel
     */
    public boolean getPlantillaExcel() {
        return plantillaExcel;
    }

    /**
     * @param plantillaExcel
     * the plantillaExcel to set
     */
    public void setPlantillaExcel(boolean plantillaExcel) {
        this.plantillaExcel = plantillaExcel;
    }

    /**
     * @return the inconsistencia
     */
    public String getInconsistencia() {
        return inconsistencia;
    }

    /**
     * @param inconsistencia
     * the inconsistencia to set
     */
    public void setInconsistencia(String inconsistencia) {
        this.inconsistencia = inconsistencia;
    }

	public boolean isVisibleMensual() {
		return visibleMensual;
	}

	public void setVisibleMensual(boolean visibleMensual) {
		this.visibleMensual = visibleMensual;
	}

	public String getModulo() {
		return modulo;
	}

	public List<Registro> getListaMes() {
		return listaMes;
	}

	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public boolean isCkMensual() {
		return ckMensual;
	}

	public void setCkMensual(boolean ckMensual) {
		this.ckMensual = ckMensual;
	}   

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
