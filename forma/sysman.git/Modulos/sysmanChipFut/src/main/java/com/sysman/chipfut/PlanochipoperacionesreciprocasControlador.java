/*-
 * PlanochipoperacionesreciprocasControlador.java
 *
 * 1.0
 * 
 * 25/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.ejb.EjbChipFutCeroRemote;
import com.sysman.chipfut.enums.PlanochipoperacionesreciprocasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar el archivo plano de saldos de cuentas
 * reciprocas TAR Nº1000083960
 *
 * @version 1.0, 25/06/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class PlanochipoperacionesreciprocasControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por el cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del check miles de pesos
     */
    private boolean pesos;
    /**
     * Atributo que almacena el valor del check Generar Por Código
     * Equivalente
     */
    private boolean codigoEquivalente;
    /**
     * Atributo que almacena el valor del check CGN 002 NIIF
     */
    private boolean companiaNiif;
    /**
     * Atributo que almacena el valor del check Sin centavos
     */
    private boolean centavos;
    /**
     * Atributo que almacena el valor del check Generar Archivo plano
     * a partir de un archivo de excel
     */
    private boolean planoExcel;
    /**
     * Atributo que almacena el valor del codigo del trimestre
     * seleccionado
     */
    private String trimestre;
    /**
     * Atributo que almacena el valor de numero de ano seleccionado
     */
    private String anioTrabajo;
    /**
     * Atributo que almacena el valor del codigo de entidad digitado
     */
    private String codigoEntidad;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
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
    /**
     * Atributi que almacena el contenido del excel leido
     */
    private String valoresImportar = "";
    /**
     * Atributo que permite validar si la etiqueta y el indicador CGN
     * 002 NIIF, se visualiza o no
     */
    private boolean visibleCodigoNiif;
    /**
     * Atributo que permite validar si la etiqueta y el indicador
     * Generar por codigo equivalente
     */
    private boolean visibleCodEquiv;
    
    /**
     * Variable que almacena el valor del parametro: NOMBRE ENCABEZADO INFORME OPERACIONES RECIPROCAS
     */
    private String tituloEncabezado;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    @EJB
    private EjbChipFutCeroRemote ejbChipFutCero;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registro del combo ano
     */
    private List<Registro> listaAnoTrabajo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * PlanochipoperacionesreciprocasControlador
     */
    public PlanochipoperacionesreciprocasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            // 1378
            numFormulario = GeneralCodigoFormaEnum.PLANOCHIPOPERACIONESRECIPROCAS_CONTROLADOR
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
        cargarListaAnoTrabajo();
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

        try {
            String codEquiv = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "INFORMES CGN POR CODIGO EQUIVALENTE",
                                            modulo, new Date(), false),
                            "");
            String codigoNiif = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            idioma.getString("TB_TB4137"),
                                            modulo, new Date(), false),
                            "");
            
             tituloEncabezado = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "NOMBRE ENCABEZADO INFORME OPERACIONES RECIPROCAS",
                                    modulo,
                                    new Date(), true),
                    "CGN2005_002_OPERACIONES_RECIPROCAS");
            
            if ("100".equals(codigoNiif) && "100".equals(compania)) {
                visibleCodigoNiif = true;
            }
            else {
                visibleCodigoNiif = false;
            }

            if ("SI".equals(codEquiv)) {
                visibleCodEquiv = true;
            }
            else {
                visibleCodEquiv = false;
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
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
                                                            PlanochipoperacionesreciprocasControladorUrlEnum.URL189
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
     * Metodo ejecutado al oprimir el boton GenerarPlano en la vista
     *
     */
    public void oprimirGenerarPlano() {
        // <CODIGO_DESARROLLADO>
        try {
            if (planoExcel) {
                exportarDatos(workbook);
                archivoDescarga = JsfUtil
                                .getArchivoDescarga(JsfUtil.serializarPlano(
                                                valoresImportar),
                                                SysmanFunciones.concatenar(
                                                                SessionUtil.getCompaniaIngreso()
                                                                                .getNombre(),
                                                                companiaNiif
                                                                    ? "_CGN2015_002.txt"
                                                                    : "_CGN2005_002.txt"));
                valoresImportar = "";
            }
            else {

                archivoDescarga = JsfUtil
                                .getArchivoDescarga(JsfUtil.serializarPlano(
                                                generarPlano(false, pesos,
                                                                centavos,
                                                                companiaNiif,
                                                                codigoEquivalente)),
                                                SysmanFunciones.concatenar(
                                                                SessionUtil.getCompaniaIngreso()
                                                                                .getNombre(),
                                                                companiaNiif
                                                                    ? "_CGN2015_002.txt"
                                                                    : "_CGN2005_002.txt"));

            }
        }
        catch (NumberFormatException | JRException | IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
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
                    tituloEncabezado, "\t", fecha,
                    "\r\n");
             Sheet sheet = workbook.getSheetAt(0);
             
            // Almacena el valor de la fila
            String valorFila = "";
            // Filas
            for (int numFila = Integer.parseInt(fila); numFila <= sheet
                            .getLastRowNum(); numFila++) {
                // Columnas
                for (int numColumna = 0; numColumna < Math
                                .max(sheet.getRow(Integer.parseInt(fila))
                                                .getLastCellNum(),
                                                0); numColumna++) {

                    if (numColumna != 1) {
                        if (numColumna != 3) {
                            // Valida que la fila y la celda
                            // especifica
                            // que se
                            // va a leer no sean nulas
                            if (validarFilaCeldaVacia(sheet, numFila,
                                            numColumna)) {

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

                                if (numColumna == 0) {
                                    valorColumna = traerCuenta(valorColumna);
                                }

                                valorFila = SysmanFunciones.concatenar(
                                                valorFila, valorColumna, " \t");

                            }
                            else {
                                return;
                            }
                        }
                    }
                }

                valoresImportar = SysmanFunciones.concatenar(valoresImportar,
                                "D", "\t", valorFila, "\r\n");
                valorFila = "";
            }
        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

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
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     *
     */
    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String archivoPlano = generarPlano(true, pesos, centavos, companiaNiif,
                        codigoEquivalente);
        String separadorRegistros = System.getProperty("line.separator");
        String separadorColumnas = "\t";
        String nombreHoja = companiaNiif ? "CGN2015.002" : "CGN2005.002";
        String nombreDocumento = "CGN2OPERACIONESRECIPROCAS";
        archivoDescarga = armarExcelChip(archivoPlano, separadorRegistros,
                        separadorColumnas, nombreHoja, nombreDocumento);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera archivo plano de saldos y movimientos contables
     * 
     * @param exportaExcel
     * Indica si se va a exportar a MS Excel.
     * @return archivo plano
     */
    private String generarPlano(boolean exportaExcel, boolean miles,
        boolean centavos, boolean cgn, boolean codEquivalente) {
        try {
            return ejbChipFutCero.generarPlanoReciproco(compania,
                            Integer.parseInt(anioTrabajo),
                            Integer.parseInt(trimestre), codigoEntidad, 6,
                            exportaExcel, miles, centavos, cgn, codEquivalente);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }
    
    public static StreamedContent armarExcelChip(String cadena, String fila, String columna, String nombreHoja,
			String nombreDocumento) {
		try {
			String hoja;
			Workbook workbook = new HSSFWorkbook();
			if (nombreHoja == null) {
				hoja = "Hoja1";
			} else {
				hoja = nombreHoja;
			}

			workbook.createSheet(hoja);
			Sheet sheet = workbook.getSheetAt(0);
			String[] separadorFila = cadena.split(fila);

			for (int i = 0; i < separadorFila.length; i++) {
				Row row = sheet.createRow(i);
				String[] separadorColumna = separadorFila[i].split(columna);
				for (int j = 0; j < separadorColumna.length; j++) {					
					Cell newCell = row.createCell(j);
					if(i >= 1 && j == 5) {

						newCell.setCellValue(Double.parseDouble(separadorColumna[j]));
					}else {
					newCell.setCellValue(separadorColumna[j]);
				    }
				}
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			out.close();
			workbook.close();
			return JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), nombreDocumento + ".xls");
		} catch (IOException | JRException e) {

			Logger.getLogger(ArchivosBean.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}

	}

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TRIMESTRE
     * 
     */
    public void cambiarTRIMESTRE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoTrabajo
     * 
     */
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Pesos
     * 
     */
    public void cambiarPesos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Excel
     *
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

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    public void cambiarPlanoPExcel() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable pesos
     * 
     * @return pesos
     */
    public boolean isPesos() {
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
     * Retorna la variable codigoEquivalente
     * 
     * @return codigoEquivalente
     */
    public boolean isCodigoEquivalente() {
        return codigoEquivalente;
    }

    /**
     * Asigna la variable codigoEquivalente
     * 
     * @param codigoEquivalente
     * Variable a asignar en codigoEquivalente
     */
    public void setCodigoEquivalente(boolean codigoEquivalente) {
        this.codigoEquivalente = codigoEquivalente;
    }

    /**
     * Retorna la variable companiaNiif
     * 
     * @return companiaNiif
     */
    public boolean isCompaniaNiif() {
        return companiaNiif;
    }

    /**
     * Asigna la variable companiaNiif
     * 
     * @param companiaNiif
     * Variable a asignar en companiaNiif
     */
    public void setCompaniaNiif(boolean companiaNiif) {
        this.companiaNiif = companiaNiif;
    }

    /**
     * Retorna la variable centavos
     * 
     * @return centavos
     */
    public boolean isCentavos() {
        return centavos;
    }

    /**
     * Asigna la variable centavos
     * 
     * @param centavos
     * Variable a asignar en centavos
     */
    public void setCentavos(boolean centavos) {
        this.centavos = centavos;
    }

    /**
     * Retorna la variable planoExcel
     * 
     * @return planoExcel
     */
    public boolean isPlanoExcel() {
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
     * Retorna la variable fila
     * 
     * @return fila
     */
    public String getFila() {
        return fila;
    }

    /**
     * Asigna la variable fila
     * 
     * @param fila
     * Variable a asignar en fila
     */
    public void setFila(String fila) {
        this.fila = fila;
    }

    /**
     * Retorna la variable visibleCodigoNiif
     * 
     * @return visibleCodigoNiif
     */
    public boolean isVisibleCodigoNiif() {
        return visibleCodigoNiif;
    }

    /**
     * Asigna la variable visibleCodigoNiif
     * 
     * @param visibleCodigoNiif
     * Variable a asignar en visibleCodigoNiif
     */
    public void setVisibleCodigoNiif(boolean visibleCodigoNiif) {
        this.visibleCodigoNiif = visibleCodigoNiif;
    }

    /**
     * Retorna la variable visibleCodEquiv
     * 
     * @return visibleCodEquiv
     */
    public boolean isVisibleCodEquiv() {
        return visibleCodEquiv;
    }

    /**
     * Asigna la variable visibleCodEquiv
     * 
     * @param visibleCodEquiv
     * Variable a asignar en visibleCodEquiv
     */
    public void setVisibleCodEquiv(boolean visibleCodEquiv) {
        this.visibleCodEquiv = visibleCodEquiv;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
