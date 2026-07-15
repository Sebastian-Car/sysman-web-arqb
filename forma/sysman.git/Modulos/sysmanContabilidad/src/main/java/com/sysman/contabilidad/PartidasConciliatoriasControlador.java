package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmsubirauxiliaresControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;

import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

/**
 * Permite registrar la fecha, concepto y valor de partida dï¿½bito o
 * crï¿½dito segï¿½n se refleje en el extracto bancario.
 *
 * @author jrodrigueza
 * @version 1, 11/04/2016
 * 
 * @version 2. 06/04/2017 Se realizo el refactory.
 * @author jsforero
 * @version 3. 20/04/2017 Se adaptan llamados a EJBs
 * @author cmanrique
 */
@ManagedBean
@ViewScoped
public class PartidasConciliatoriasControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private String cuenta;

    private int ano;
    private int mes;
    private String codCuenta;
    private boolean visibleOpcion;
    /**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	

	private ContenedorArchivo contArchivocargarExcel = new ContenedorArchivo();
	
	

	private String cadena;
	
	private int contador;
	
	private final String HOJA_CONCILIAR = "CONCILIATORIO";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
	@EJB
	private EjbContabilidadSieteRemote ejbContabilidadSiete;

    /**
     * Creates a new instance of PartidasConciliatoriasControlador
     */
    public PartidasConciliatoriasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PARTIDAS_CONCILIATORIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                ano = (int) parametros.get("ano");
                mes = (int) parametros.get("mes");
                codCuenta = parametros.get("codCuenta").toString();
                cuenta = codCuenta + " " + parametros.get("nombreCuenta");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(PartidasConciliatoriasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.PARTIDAS_CONCILIATORIAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.name(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.name(), ano);
        parametrosListado.put("CODIGOCUENTA", codCuenta);
        parametrosListado.put("MES", mes);

    }

    @Override
    public void abrirFormulario() {
        // Valores predeterminados
        try {
            registro.getCampos().put(GeneralParameterEnum.FECHA.name(),
                            new Date());
            registro.getCampos().put("PARTIDA_DEBITO", 0);
            registro.getCampos().put("PARTIDA_CREDITO", 0);
            registro.getCampos().put("MES_PARTIDA", mes);

            String estado = ejbSysmanUtil.verificarEstadoPeriodoMensual(
                            compania, ano, mes,
                            Integer.parseInt(SessionUtil.getModulo()), 2);
            visibleOpcion= "A".equals(estado);
            
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.name(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.name(), ano);
        registro.getCampos().put(GeneralParameterEnum.CUENTA.name(), codCuenta);
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.name(),
                        genConsecutivo());
        return true;
    }

    /**
     * Genera el consecutivo para almacenar la partida conciliatoria.
     * El consecutivo que se genera depende solamente de la
     * compaï¿½ï¿½a.
     *
     * @return entero que representa el consecutivo
     */
    private long genConsecutivo() {
        try {

            return ejbSysmanUtil.generarSiguienteConsecutivo(
                            GenericUrlEnum.PARTIDAS_CONCILIATORIAS.getTable(),
                            "COMPANIA=''" + compania
                                + "''",
                            "CONSECUTIVO");
        }
        catch (SystemException e) {
            Logger.getLogger(PartidasConciliatoriasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return -1;
        }
    }
	public void oprimirbtnDescargarPlantilla()  {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
		
			CellStyle style = crearEstiloEncabezado(workbook);
			crearHojaConciliatorios(workbook, style);
			
			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Conciliacion.xls");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (IOException | JRException  e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		
		} 


		//</CODIGO_DESARROLLADO>
	}
	
	/**
	 * 
	 * metodo para  estilo de encabezado en archivo excel - generico -
	 * 
	 */
	private CellStyle crearEstiloEncabezado(HSSFWorkbook workbook) {

		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBold(true);
		font.setFontHeightInPoints((short) 10);

		CellStyle style = workbook.createCellStyle();
		style.setFont(font);

		//		groundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		//		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		return style;
	}
	
	/**
	 * 
	 * metodo para crear titulos encabezado generico
	 * 
	 */
	private void crearEncabezados(HSSFSheet sheet, String[] columnas, CellStyle style) {

		Row row = sheet.createRow(0);

		for (int i = 0; i < columnas.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnas[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(i);
		}
	}

	/**
	 * 
	 * metodo para enviar fuente de recurso
	 * 
	 */
	private void crearHojaConciliatorios(HSSFWorkbook workbook, CellStyle style) {

		HSSFSheet sheet = workbook.createSheet(HOJA_CONCILIAR);

		String[] columnas = {
				"CUENTA",
				"FECHA",
				"CONCEPTO",
				"PARTIDA DEBITO",
				"PARTIDA CREDITO",
				"MES"
		};

		crearEncabezados(sheet, columnas, style);
		
	}
	
	public void oprimirbtnCargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;
		
		if (!validarArchivo()) {
			return;
		}
		File archivo = contArchivocargarExcel.getArchivo();
		try (FileInputStream file = new FileInputStream(archivo)) {
			String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();
			String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf('.') + 1);
			Sheet sheet = null;
			String tabla = null;
			//Row header = null;
			int column = 0;
			int registrosProcesados = 0;

			if ("xls".equalsIgnoreCase(extension)) {
				workbook = new HSSFWorkbook(file);
			} else {
				workbook = new XSSFWorkbook(file);
			}

			sheet = workbook.getSheet(HOJA_CONCILIAR);    	
			column = 6;
			tabla = "PARTIDAS_CONCILIATORIAS";
			  
			 
			for (Row row : sheet) {

					if (row.getRowNum() == 0) {
						continue;
					}
					if (filaVacia(row)) {
						continue;
					}
					capturaDatosExcelAuxiliaresGenerales(row, column);
					registrosProcesados ++;
			}

			cadena = cadena + "')";
				
			if (registrosProcesados == 0) {
				JsfUtil.agregarMensajeAlerta("La hoja " + sheet.getSheetName() + " no tiene información.");
				return;
			}
				
				
			String clobErrores = ejbContabilidadSiete.cargarConciliar(tabla, cadena, SessionUtil.getUser().getCodigo(),compania,ano);
						
			if (clobErrores != null && !clobErrores.trim().isEmpty()) {
			
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clobErrores);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LOG.txt");
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4504"));
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
		public boolean validarArchivo() {

			if (contArchivocargarExcel == null || contArchivocargarExcel.getArchivo() == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
				return false;
			} else {
				return true;
			}
		}
		
		//Método para validar si una fila está vacía
		private boolean filaVacia(Row row) {

		    if (row == null) {
		        return true;
		    }

		    DataFormatter formatter = new DataFormatter();

		    for (int c = 0; c < 7; c++) { 
		        Cell cell = row.getCell(c);

		        if (cell != null) {
		            String val = formatter.formatCellValue(cell);
		            if (val != null && !val.trim().isEmpty()) {
		                return false;
		            }
		        }
		    }
		    return true;
		}
		
		private void capturaDatosExcelAuxiliaresGenerales(Row row, int column) {

		    for (int i = 0; i < column; i++) {

		        Cell cell = row.getCell(i);
		        String val = "";

		        if (cell != null) {

		            int cellType = cell.getCellType();

		            if (cellType == Cell.CELL_TYPE_NUMERIC) {

		                BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
		                bd = bd.stripTrailingZeros();
		                //val = bd.toPlainString().replace('.', ',');
		                val = bd.toPlainString();

		            } else if (cellType == Cell.CELL_TYPE_STRING) {

		                val = cell.getStringCellValue().trim();
		                val = normalizarTexto(val);

		            } else {
		                val = "";
		            }
		        }

		        if (val == null || val.isEmpty() || "null".equalsIgnoreCase(val)) {
		            val = "NoDato";
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

	private String normalizarTexto(String texto) {

		    if (texto == null) {
		        return "";
		    }

		    // Reemplazar saltos de línea por espacio
		    texto = texto.replaceAll("\\r\\n|\\r|\\n", " ");

		    // Reemplazar tabulaciones
		    texto = texto.replaceAll("\\t", " ");

		    // Quitar espacios múltiples
		    texto = texto.replaceAll(" +", " ");

		    // 4. Trim final
		    return texto.trim();
	}
    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.name());
        registro.getCampos().remove(GeneralParameterEnum.ANO.name());
        registro.getCampos().remove(GeneralParameterEnum.CUENTA.name());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.name());
    }

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put(GeneralParameterEnum.FECHA.name(), new Date());
        registro.getCampos().put("PARTIDA_DEBITO", 0);
        registro.getCampos().put("PARTIDA_CREDITO", 0);
        registro.getCampos().put("MES_PARTIDA", mes);
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public boolean isVisibleOpcion() {
        return visibleOpcion;
    }

    public void setVisibleOpcion(boolean visibleOpcion) {
        this.visibleOpcion = visibleOpcion;
    }
    
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}
    
    

}
