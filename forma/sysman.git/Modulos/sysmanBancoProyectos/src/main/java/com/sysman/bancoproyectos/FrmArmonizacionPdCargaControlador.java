/*-
 * FrmArmonizacionPdCargaControlador.java
 *
 * 1.0
 * 
 * 20/02/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.primefaces.context.RequestContext;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoUnoRemote;
import com.sysman.bancoproyectos.enums.FrmArmonizacionPdCargaControladorUrlEnum;
import com.sysman.bancoproyectos.enums.FrmInversionxAnoControladorUrlEnum;
import com.sysman.bancoproyectos.enums.FrmarmonizacionpdsControladorUrlEnum;
import com.sysman.bancoproyectos.enums.FrmbptiponovedadpsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import com.sysman.util.ContenedorArchivo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 20/02/2026
 * @author User
 */
@ManagedBean
@ViewScoped
public class  FrmArmonizacionPdCargaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
    private final String compania ;
	
	/**
	 * Almacen el usuario al iniciar sesion
	 */
	public String usuario;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel = new ContenedorArchivo();
	/**
	 * Almacena los valores que se construye en el excel
	 */
	private String cadena;
	/**
	 * Almacena los valores de log de errores para generar el txt
	 */
	private int contador;
	/**
	 * Almacena el valor de inicio del anio
	 */
	private String anio;
	/**
	 * Almacena la lista de anios retornada del combo vigencia 
	 */
	private List<Registro> listaAnio;
	

	@EJB
	private EjbBancoProyectoUnoRemote ejbBancoProyectoUno;
	/**
	 * Crea una nueva instancia de FrmArmonizacionPdCargaControlador
	 */
	public FrmArmonizacionPdCargaControlador() {
		super();
		compania = SessionUtil.getCompania();
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_ARMONIZACION_PD_CARGA_CONTROLADOR.getCodigo();
			validarPermisos();

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

		abrirFormulario();
		cargarListaAnio();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){

	}
	
	/**
	 * Carga la lista listaAno
	 * Propiedad en el HTML: BT8986
	 */
	public void cargarListaAnio() {
		try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmArmonizacionPdCargaControladorUrlEnum.URL4310
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

	}
	
	/**
	 * Obtiene las metas del Plan Indicativo por compania y anio.
	 * Retorna una lista de Registro o una lista vacia si ocurre error.
	 */
	private List<Registro> consultarMetasPlanIndicativo() {
	    try {
	       
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ANO.getName(), anio); 

	        String url = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                        FrmArmonizacionPdCargaControladorUrlEnum.URL433020.getValue())
	                .getUrl();

	        List<Registro> lista = RegistroConverter.toListRegistro(
	                requestManager.getList(url, param));

	        return lista;

	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	        return new ArrayList<>();
	    }
	}
	
	/**
	 * Genera y descarga la plantilla de Armonizacion Plan de Desarrollo en formato Excel.
	 * Crea la hoja Encabezado con las metas consultadas desde la base de datos
	 * y deja columnas vacias para que el usuario diligencie la informacion requerida.
	 * Incluye una hoja Instructivo con descripcion de campos, reglas y ejemplo.
	 * En caso de error registra el mensaje y no genera el archivo.
	 * 
	 *  Propiedad en el HTML: BT4496
	 */
    public void oprimirBtnDescargaPlantilla() {

        setArchivoDescarga(null);
        HSSFWorkbook workbook = new HSSFWorkbook();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ========================= ESTILOS =========================
            Font fontHeader = workbook.createFont();
            fontHeader.setFontName("Arial");
            fontHeader.setBold(true);
            fontHeader.setFontHeightInPoints((short) 10);

            CellStyle styleHeader = workbook.createCellStyle();
            styleHeader.setFont(fontHeader);
            styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleHeader.setBorderBottom(BorderStyle.THIN);
            styleHeader.setBorderTop(BorderStyle.THIN);
            styleHeader.setBorderLeft(BorderStyle.THIN);
            styleHeader.setBorderRight(BorderStyle.THIN);

            CellStyle styleNormal = workbook.createCellStyle();
            styleNormal.setBorderBottom(BorderStyle.THIN);
            styleNormal.setBorderTop(BorderStyle.THIN);
            styleNormal.setBorderLeft(BorderStyle.THIN);
            styleNormal.setBorderRight(BorderStyle.THIN);
            styleNormal.setWrapText(true);

            HSSFDataFormat dataFormat = workbook.createDataFormat();

            CellStyle styleTexto = workbook.createCellStyle();
            styleTexto.setDataFormat(dataFormat.getFormat("@"));
            styleTexto.setWrapText(true);

            Font fontTitle = workbook.createFont();
            fontTitle.setFontName("Arial");
            fontTitle.setBold(true);
            fontTitle.setFontHeightInPoints((short) 12);

            CellStyle styleTitle = workbook.createCellStyle();
            styleTitle.setFont(fontTitle);

            // ========================= HOJA ENCABEZADO =========================
            HSSFSheet excelSheet = workbook.createSheet("ENCABEZADO");
            Row row = excelSheet.createRow(0);

            String[] encabezados = {
                "VIGENCIA_INICIAL_PLAN", "VIGENCIA_META", "CODIGO_META",
                "VIGENCIA_RUBRO", "CODIGO_RUBRO", "FUENTE",
                "CENTROCOSTO", "REFERENCIA", "AUXILIAR", "DESCRIPCION_META"
            };

            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(encabezados[i]);
                cell.setCellStyle(styleHeader);
            }

            // ========================= DATOS DESDE BD =========================
            List<Registro> listaMetas = consultarMetasPlanIndicativo();

            if (listaMetas != null && !listaMetas.isEmpty()) {
                int numFila = 1;
                for (Registro reg : listaMetas) {

                    String vigenciaPlan = obtenerCampo(reg, "VIGENCIA_PLAN");
                    String vigenciaMeta = obtenerCampo(reg, "VIGENCIA_META");
                    String codigoMeta   = obtenerCampo(reg, "ID_PLAN");
                    String descripcion  = obtenerCampo(reg, "DESCRIPCION");

                    Row dataRow = excelSheet.createRow(numFila++);

                    // Col 0 - VIGENCIA_INICIAL_PLAN
                    Cell c0 = dataRow.createCell(0);
                    c0.setCellValue(vigenciaPlan);
                    c0.setCellStyle(styleTexto);

                    // Col 1 - VIGENCIA_META
                    Cell c1 = dataRow.createCell(1);
                    c1.setCellValue(vigenciaMeta);
                    c1.setCellStyle(styleTexto);

                    // Col 2 - CODIGO_META
                    Cell c2 = dataRow.createCell(2);
                    c2.setCellValue(codigoMeta);
                    c2.setCellStyle(styleTexto);

                    // Col 3 - VIGENCIA_RUBRO (vacío, lo diligencia el usuario)
                    Cell c3 = dataRow.createCell(3);
                    c3.setCellValue("");
                    c3.setCellStyle(styleTexto);

                    // Col 4 - CODIGO_RUBRO (vacío, lo diligencia el usuario)
                    Cell c4 = dataRow.createCell(4);
                    c4.setCellValue("");
                    c4.setCellStyle(styleTexto);

                    // Col 5 - FUENTE (vacío, lo diligencia el usuario)
                    Cell c5 = dataRow.createCell(5);
                    c5.setCellValue("");
                    c5.setCellStyle(styleTexto);

                    // Col 6 - CENTROCOSTO (vacío, lo diligencia el usuario)
                    Cell c6 = dataRow.createCell(6);
                    c6.setCellValue("");
                    c6.setCellStyle(styleTexto);

                    // Col 7 - REFERENCIA (vacío, lo diligencia el usuario)
                    Cell c7 = dataRow.createCell(7);
                    c7.setCellValue("");
                    c7.setCellStyle(styleTexto);

                    // Col 8 - AUXILIAR (vacío, lo diligencia el usuario)
                    Cell c8 = dataRow.createCell(8);
                    c8.setCellValue("");
                    c8.setCellStyle(styleTexto);

                    // Col 9 - DESCRIPCION_META
                    Cell c9 = dataRow.createCell(9);
                    c9.setCellValue(descripcion);
                    c9.setCellStyle(styleTexto);
                }
            } else {
                // Si no hay datos deja una fila vacía de ejemplo
                Row dataRow = excelSheet.createRow(1);
                for (int i = 0; i < encabezados.length; i++) {
                    Cell cell = dataRow.createCell(i);
                    cell.setCellValue("");
                    cell.setCellStyle(styleTexto);
                }
            }

            // Ajustar ancho columnas
            for (int i = 0; i < encabezados.length; i++) {
                excelSheet.autoSizeColumn(i);
            }

            // ========================= HOJA INSTRUCTIVO =========================
            HSSFSheet sheetInst = workbook.createSheet("INSTRUCTIVO");

            int fila = 0;

            Row rowTitle = sheetInst.createRow(fila++);
            Cell cellTitle = rowTitle.createCell(0);
            cellTitle.setCellValue("CARGUE ARMONIZACION PLAN DE DESARROLLO");
            cellTitle.setCellStyle(styleTitle);

            fila++;

            Row rowHead = sheetInst.createRow(fila++);
            String[] cols = { "CAMPO", "FORMATO", "TIPO", "OBSERVACIONES" };

            for (int i = 0; i < cols.length; i++) {
                Cell cell = rowHead.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(styleHeader);
            }

            Object[][] data = {
                { "VIGENCIA_INICIAL_PLAN", "NUMBER(4,0)", "Obligatorio",
                    "Vigencia inicial del plan indicativo. Viene prellenado desde la BD, no modificar." },

                { "VIGENCIA_META", "NUMBER(4,0)", "Obligatorio",
                    "Vigencia de la meta del plan indicativo. Viene prellenado desde la BD, no modificar." },

                { "CODIGO_META", "VARCHAR2(100 CHAR)", "Obligatorio",
                    "Código de la meta del plan indicativo. Viene prellenado desde la BD, no modificar." },

                { "VIGENCIA_RUBRO", "NUMBER(4,0)", "Obligatorio",
                    "Vigencia del rubro presupuestal a armonizar con la meta."
                   + " debe validarse en la tabla de Plan Presupuestal del modulo de presupuesto en la ruta PRESUPUESTO/ARCHIVOS/PLAN PRESUPUESTAL" },

                { "CODIGO_RUBRO", "VARCHAR2(100 CHAR)", "Obligatorio",
                    "Código del rubro presupuestal tenga el indicador de movimiento seleccionado,"
                   + " debe estar creado en el plan presupuestal para la vigencia definida en VIGENCIA_RUBRO." },

                { "FUENTE", "VARCHAR2(20 CHAR)", "Obligatorio",
                    "Código de la fuente de recursos relacionado al rubro del plan presupuestal según el registro de apropiaciones,"
                    + " debe estar creada para la vigencia definida en VIGENCIA_RUBRO." },

                { "CENTROCOSTO", "VARCHAR2(20 CHAR)", "Obligatorio",
                    "Código de centro de costo relacionado al rubro del plan presupuestal según el registro de apropiaciones,"
                    + " debe estar creado para la vigencia definida en VIGENCIA_RUBRO." },

                { "REFERENCIA", "VARCHAR2(20 CHAR)", "Obligatorio",
                    "Código de referencia relacionado al rubro del plan presupuestal según el registro de apropiaciones,"
                    + " debe estar creada para la vigencia definida en VIGENCIA_RUBRO." },

                { "AUXILIAR", "VARCHAR2(32 CHAR)", "Obligatorio",
                    "Código de auxiliar relacionado al rubro del plan presupuestal según el registro de apropiaciones,"
                    + " debe estar creado para la vigencia definida en VIGENCIA_RUBRO." },

                { "DESCRIPCION_META", "VARCHAR2(500 CHAR)", "Informativo",
                    "Descripción de la meta del plan indicativo. Viene prellenado desde la BD, no modificar." }
            };

            for (Object[] filaData : data) {
                Row r = sheetInst.createRow(fila++);
                for (int i = 0; i < filaData.length; i++) {
                    Cell c = r.createCell(i);
                    c.setCellValue(filaData[i].toString());
                    c.setCellStyle(styleNormal);
                }
            }

            fila++;

            Row rowNota = sheetInst.createRow(fila++);
            Cell cellNota = rowNota.createCell(0);
            cellNota.setCellValue(
                "Importante: Los campos VIGENCIA_INICIAL_PLAN, VIGENCIA_META, CODIGO_META y DESCRIPCION_META "
                + "vienen prellenados desde la base de datos y NO deben ser modificados. "
                + "El usuario debe diligenciar únicamente: VIGENCIA_RUBRO, CODIGO_RUBRO, FUENTE, CENTROCOSTO, REFERENCIA y AUXILIAR.");
            cellNota.setCellStyle(styleNormal);

            fila++;

            Row rowEjemploTitulo = sheetInst.createRow(fila++);
            Cell cellEjemploTitulo = rowEjemploTitulo.createCell(0);
            cellEjemploTitulo.setCellValue("EJEMPLO");
            cellEjemploTitulo.setCellStyle(styleTitle);

            Row rowEjemploHeader = sheetInst.createRow(fila++);
            String[] encabezadoEjemplo = {
                "VIGENCIA_INICIAL_PLAN", "VIGENCIA_META", "CODIGO_META",
                "VIGENCIA_RUBRO", "CODIGO_RUBRO", "FUENTE",
                "CENTROCOSTO", "REFERENCIA", "AUXILIAR", "DESCRIPCION_META"
            };

            for (int i = 0; i < encabezadoEjemplo.length; i++) {
                Cell cell = rowEjemploHeader.createCell(i);
                cell.setCellValue(encabezadoEjemplo[i]);
                cell.setCellStyle(styleHeader);
            }

            Row rowEjemploData = sheetInst.createRow(fila++);
            String[] datosEjemplo = {
                "2022",
                "2026",
                "META-001",
                "2026",
                "2.3.1.01.01.001.01-12-22-2201-1090002",
                "1.2.4.1.01",
                "99999999999999999999",
                "99999999999999999999",
                "220101700",
                "Construcción vía principal municipio"
            };

            for (int i = 0; i < datosEjemplo.length; i++) {
                Cell cell = rowEjemploData.createCell(i);
                cell.setCellValue(datosEjemplo[i]);
                cell.setCellStyle(styleNormal);
            }

            for (int i = 0; i < encabezadoEjemplo.length; i++) {
                sheetInst.autoSizeColumn(i);
            }

            // ========================= ESCRIBIR ARCHIVO =========================
            workbook.write(out);

            String nombreArchivo = "Armonizacion_Plan_Desarrollo.xls";
            setArchivoDescarga(JsfUtil.getArchivoDescarga(
                new ByteArrayInputStream(out.toByteArray()), nombreArchivo));

        } catch (IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Obtiene el valor de un campo desde un objeto Registro.
     * Si el campo existe retorna su valor como texto.
     * Si es nulo retorna cadena vacia.
     */
    private String obtenerCampo(Registro reg, String campo) {
        Object val = reg.getCampos().get(campo);
        return (val != null) ? val.toString() : "";
    }
    
    
    /**
     * Procesa el archivo Excel cargado por el usuario para la armonizacion.
     * Lee la hoja ENCABEZADO, valida las filas y las procesa en lotes.
     * Envia cada lote a Oracle para su registro y consolida el resultado en un log.
     * Genera un archivo txt con el resumen de insertados omitidos y errores.
     * En caso de fallo registra el error.
     * 
     * Propiedad en el HTML: BT4497
     */
    public void oprimirBtnCargaPlantilla() {
        Workbook workbook = null;
        cadena = "TO_CLOB('";
        contador = 0;
        if (!validarArchivo()) {
            return;
        }
        try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo())) {
            String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();
            String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf('.') + 1);
            if ("xls".equalsIgnoreCase(extension)) {
                workbook = new HSSFWorkbook(file);
            } else {
                workbook = new XSSFWorkbook(file);
            }
            Sheet sheet = workbook.getSheet("ENCABEZADO");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Salta encabezados
                }
                capturaDatosExcelArmonizacion(row);
            }
            cadena = cadena + "')";
            String clobErrores = ejbBancoProyectoUno.cargarArmonizacionPd(compania, cadena, usuario);

            // SIEMPRE genera el TXT con el LOG del proceso
            if (clobErrores != null && !clobErrores.trim().isEmpty()) {
                ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clobErrores);
                archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "RESULTADO_CARGUE_ARMONIZACION.txt");
                JsfUtil.agregarMensajeInformativo("Proceso finalizado. Descargue el archivo de resultados.");
            } else {
                JsfUtil.agregarMensajeError("La función no retornó resultado. Verifique la configuración.");
            }
            

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnDescargaPlantilla
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void ejecutaractualizarMensaje() {
		JsfUtil.agregarMensajeInformativo("Proceso Finalizado");
	}

	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {

		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Lee una fila del Excel y construye la cadena que sera enviada a la base de datos.
	 * Recorre las 10 columnas esperadas, formatea los valores y reemplaza vacios por NoDato.
	 * Controla el tamano para armar bloques TO_CLOB y agrega separadores de columna y registro.
	 */
	private void capturaDatosExcelArmonizacion(Row row) {

	    DataFormatter formatter = new DataFormatter();

	    // Las 10 columnas en orden:
	    // 0-VIGENCIA_INICIAL_PLAN, 1-VIGENCIA_META, 2-CODIGO_META,
	    // 3-VIGENCIA_RUBRO, 4-CODIGO_RUBRO, 5-FUENTE,
	    // 6-CENTROCOSTO, 7-REFERENCIA, 8-AUXILIAR, 9-DESCRIPCION_META

	    for (int i = 0; i < 10; i++) {

	        Cell cell = row.getCell(i);
	        String val = "";

	        if (cell != null) {
	            val = formatter.formatCellValue(cell);
	        }

	        if (val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val.trim())) {
	            val = "NoDato";
	        } else {
	            val = val.trim();
	        }

	        contador += val.length();

	        if (contador >= 3000) {
	            cadena = cadena + "') || TO_CLOB('";
	            contador = 0;
	        }

	        cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
	    }

	    cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());
	    cadena = cadena + SysmanConstantes.SEPARADOR_REG;
	}

	
	// Metodos getters y setters

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
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * @param anio the anio to set
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * @return the listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}
	/**
	 * @param listaAnio the listaAnio to set
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

}
