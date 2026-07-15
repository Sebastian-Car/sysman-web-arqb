/*-
 * FrmDeterioroDeCarteraControlador.java
 *
 * 1.0
 * 
 * 29/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.primefaces.model.StreamedContent;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.contabilidad.FrmDeterioroDeCarteraControlador;
import com.sysman.contabilidad.enums.FrmDeterioroDeCarteraUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *  controlador para deterioro cartera
 *
 * @version 1.0, 29/04/2021
 * @author eorozco
 */
@ManagedBean
@ViewScoped
public class  FrmDeterioroDeCarteraControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     *  variable que almacena la compa�ia
     */
private String anio1;
    /**
     * La variable que almacena el a�o.
     */
private String mes1;
    /**
     * variable que almacena el mes.
     */
private List<Registro> listaAnio1;
    /**
     *  variable que almacena la lista de a�o.
     */
private List<Registro> listames1;
/**
 *variable que almacena la lista de mes.
 */


private StreamedContent archivoDescarga;
/**
 * Este atributo se usa como auxiliar del componente selector de
 * archivos cargarExcel y funciona como contenedor del archivo que se
 * debe guardar
 */
private ContenedorArchivo contArchivocargarExcel;

private List<Registro> listaNetoValor;

private List<Registro> listaNetoCero;
   
@EJB
private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmDeterioroDeCarteraControlador
     */
    public FrmDeterioroDeCarteraControlador() {
    	super();
    	compania = SessionUtil.getCompania();
		
        try {
            numFormulario = GeneralCodigoFormaEnum.DETERIORO_CARTERA
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmDeterioroDeCarteraControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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

		 cargarListaAnio1();
	
		 
		 abrirFormulario();
    }
    
    
    
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario(){
		  cargarListaMes1();
	       
	     }
  
        //<CODIGO_DESARROLLADO>
            /*
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio1
     *
     * TODO DOCUMENTACION ADICIONAL
     */
     public void cargarListaAnio1() {

 		

 		try {
 			Map<String, Object> param = new TreeMap<>();
 			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
 			listaAnio1 = RegistroConverter.toListRegistro(
 					requestManager.getList(UrlServiceUtil.getInstance()
 							.getUrlServiceByUrlByEnumID(
 									FrmDeterioroDeCarteraUrlEnum.URL3516
 									.getValue())
 							.getUrl(), param));
 		}
 		catch (SystemException e) {
 			logger.error(e.getMessage(), e);
 			JsfUtil.agregarMensajeError(e.getMessage());
 		}

 	}

 	public void cargarListaMes1() {

 	

 		try {
 			Map<String, Object> param = new TreeMap<>();
 	 		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
 	 		param.put(GeneralParameterEnum.ANO.getName(), anio1);
 			listames1 = RegistroConverter.toListRegistro(
 					requestManager.getList(UrlServiceUtil.getInstance()
 							.getUrlServiceByUrlByEnumID(
 									FrmDeterioroDeCarteraUrlEnum.URL4061
 									.getValue())
 							.getUrl(), param));
 		}
 		catch (SystemException e) {
 			logger.error(e.getMessage(), e);
 			JsfUtil.agregarMensajeError(e.getMessage());
 		}

 	}
 	
 	

 	    public void oprimirExcel()  {
 	        // <CODIGO_DESARROLLADO>
 	        archivoDescarga = null;
 	       
 	        //  generaInforme(ReportesBean.FORMATOS.EXCEL97);
 	       HSSFWorkbook workbook = new HSSFWorkbook();
 	       
 	  	   HSSFSheet excelSheet =  null;
 		   try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

 				Map<String, Object> param = new HashMap<>();
 				
 				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
 	 	 		param.put(GeneralParameterEnum.ANO.getName(), anio1);
 	 	 		param.put(GeneralParameterEnum.MES.getName(), mes1);
 	 	 		param.put("NETO", 1);
 				listaNetoValor = RegistroConverter
 						.toListRegistro(
 								requestManager.getList(
 										UrlServiceUtil.getInstance()
 										.getUrlServiceByUrlByEnumID(
 												FrmDeterioroDeCarteraUrlEnum.URL1865003.getValue())
 										.getUrl(),
 										param));
 				excelSheet = workbook.getSheet("Facturas deterioradas periodo" );
				// lista desplegable separada
				addValidationToSheet2(workbook, excelSheet, listaNetoValor, 'A', 2, 10000, "Facturas deterioradas periodo");
 				
				param.put("NETO", 0);
 				listaNetoCero = RegistroConverter
 						.toListRegistro(
 								requestManager.getList(
 										UrlServiceUtil.getInstance()
 										.getUrlServiceByUrlByEnumID(
 												FrmDeterioroDeCarteraUrlEnum.URL1865003.getValue())
 										.getUrl(),
 										param));
 				excelSheet = workbook.getSheet("Facturas deterioradas 100%" );
 				addValidationToSheet2(workbook, excelSheet, listaNetoCero, 'A', 2, 10000, "Facturas deterioradas 100%");
 				
 				workbook.write(out);

 				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
 						"002258DeterioroDeCartera.xls");

 			} catch (IOException | JRException | SystemException e) {
 				e.printStackTrace();
 			
 			}
 	        
 	        // </CODIGO_DESARROLLADO>
 	    }
 		/**
 		 * Agregar una lista desplegable a la página de la hoja
 		 *
 		 * Archivo de Excel del libro de trabajo @param, utilizado para agregar e
 		 * nombre
 		 * 
 		 * @param targetSheet La página de la hoja donde se encuentra la lista en
 		 *                    cascada
 		 * @param options     Datos en cascada ['Baidu', 'Alibaba']
 		 * @param column      La columna de la lista desplegable comienza en'A '
 		 * @param fromRow     fila de inicio del límite desplegable
 		 * @param endRow      límite desplegable de la fila final
 		 */
 		public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
 				int fromRow, int endRow, String name) {
 			String hiddenSheetName = name;
 			Sheet optionsSheet =  null;
 			
 			int i = 0;
 			try {
 				optionsSheet = workbook.createSheet(hiddenSheetName);
 			} catch (Exception e) {
 				optionsSheet = workbook.getSheet(hiddenSheetName);
 				i++;
 			}
 			Row row = optionsSheet.createRow(0);
 			Cell cell = row.createCell(0);
 			row = optionsSheet.createRow(0);


 			CellStyle stylenumber = workbook.createCellStyle();
    		DataFormat format = workbook.createDataFormat();
    		stylenumber.setDataFormat(format.getFormat("###,###,##0.00"));
    		
    		

 			cell = row.createCell(0);
 			cell.setCellValue("ANO");
 			optionsSheet.autoSizeColumn(0);
 			
 			cell = row.createCell(1);
 			cell.setCellValue("MES");
 			optionsSheet.autoSizeColumn(1);

 			cell = row.createCell(2);
 			cell.setCellValue("TERCERO");
 			optionsSheet.autoSizeColumn(2);
 			
 			cell = row.createCell(3);
 			cell.setCellValue("NOMBRE TERCERO");
 			optionsSheet.autoSizeColumn(3);
 			
 			cell = row.createCell(4);
 			cell.setCellValue("TIPO");
 			optionsSheet.autoSizeColumn(4);
 			
 			cell = row.createCell(5);
 			cell.setCellValue("NUMERO");
 			optionsSheet.autoSizeColumn(5);
 			
 			cell = row.createCell(6);
 			cell.setCellValue("DESCRIPCION");
 			optionsSheet.autoSizeColumn(6);
 			
 			cell = row.createCell(7);
 			cell.setCellValue("FECHA");
 			optionsSheet.autoSizeColumn(7);
 			
 			cell = row.createCell(8);
 			cell.setCellValue("CUENTA CONTABLE");
 			optionsSheet.autoSizeColumn(8);
 			
 			cell = row.createCell(9);
 			cell.setCellValue("DIAS EDAD");
 			optionsSheet.autoSizeColumn(9);
 			
 			cell = row.createCell(10);
 			cell.setCellValue("TASA");
 			optionsSheet.autoSizeColumn(10);
 			
 			cell = row.createCell(11);
 			cell.setCellValue("VALOR FACTURA");
 			optionsSheet.autoSizeColumn(11);
 			
 			cell = row.createCell(12);
 			cell.setCellValue("DETERIORO MENSUAL");
 			optionsSheet.autoSizeColumn(12);
 			
 			cell = row.createCell(13);
 			cell.setCellValue("DETERIORO ACUMULADO");
 			optionsSheet.autoSizeColumn(13);
 			
 			cell = row.createCell(14);
 			cell.setCellValue("VALOR NETO");
 			optionsSheet.autoSizeColumn(14);
 			

 			
				
 			
 			
 			String nameName = column + "_parent";

 			int rowIndex = 1;
 			for (Registro option : options) {
 				int columnIndex = 0;
 				 row = optionsSheet.createRow(rowIndex++);
 				 cell = row.createCell(columnIndex++);
 				 cell.setCellValue(option.getCampos().get("ANO").toString());
 				 cell = row.createCell(1);
 				 cell.setCellValue(option.getCampos().get("MES").toString());
 				 cell = row.createCell(2);
 				 cell.setCellValue(option.getCampos().get("TERCERO").toString());
 				 cell = row.createCell(3);
 				 cell.setCellValue(option.getCampos().get("NOMBRETERCERO").toString());
 				 cell = row.createCell(4);
 				 cell.setCellValue(option.getCampos().get("TIPO").toString());
 				 cell = row.createCell(5);
 				 cell.setCellValue(option.getCampos().get("NUMERO").toString());
 				 cell = row.createCell(6);
 				 cell.setCellValue(option.getCampos().get("DESCRIPCION").toString());
 				 cell = row.createCell(7);
 				 cell.setCellValue(option.getCampos().get("FECHA").toString());
 				 cell = row.createCell(8);
 				 cell.setCellValue(option.getCampos().get("CUENTA").toString());
 				 cell = row.createCell(9);
 				 cell.setCellValue(option.getCampos().get("EDAD").toString());
 				 cell = row.createCell(10);
 				 cell.setCellValue(option.getCampos().get("TASA").toString());
 				 
 				 cell = row.createCell(11);
 				 double val = new Double(option.getCampos().get("VALOR").toString()); 
 				 cell.setCellValue(val);
 				 cell.setCellStyle(stylenumber);
 				  
 				 cell = row.createCell(12);
 				 double detmen = new Double(option.getCampos().get("DETERIORO_MENSUAL").toString()); 
 				 cell.setCellValue(detmen);
 				 cell.setCellStyle(stylenumber);
 				  
 				 cell = row.createCell(13);
 				 double detacum = new Double(option.getCampos().get("DETERIORO_ACUMULADO").toString()); 
 				 cell.setCellValue(detacum);
 				 cell.setCellStyle(stylenumber);
 				 
 				 cell = row.createCell(14);
 				 double vaneto = new Double(option.getCampos().get("VALOR_NETO").toString()); 
 				 cell.setCellValue(vaneto);
 				 cell.setCellStyle(stylenumber);

 			}
 			
 			
 		}

		private static Name createName(Workbook workbook, String nameName, String formula) {
			Name name = workbook.createName();
			name.setNameName(nameName);
			name.setRefersToFormula(formula);
			return name;
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
 	//</METODOS_BOTONES>	
 	//<METODOS_SUBFORM>	
 	//</METODOS_SUBFORM>	
 	//<METODOS_ADICIONALES>	
 	//</METODOS_ADICIONALES>
 	    
 	    private void generaInforme(ReportesBean.FORMATOS formato)  {
 	        try {
 	            Map<String, Object> parametros = new HashMap<>();
 	            HashMap<String, Object> reemplazar = new HashMap<>();
 	            reemplazar.put("compania",compania);
 	            reemplazar.put("ano", anio1);
 	            reemplazar.put("mes", mes1);     
 	           

 	            // MANEJO DE PARAMETROS DEL REPORTE
 	            Reporteador.resuelveConsulta("002258DeterioroDeCartera",
 	                            Integer.parseInt(SessionUtil.getModulo()),
 	                            reemplazar,
 	                            parametros);

 	         
 					
						archivoDescarga = JsfUtil.exportarStreamed(
						                "002258DeterioroDeCartera", parametros,
						                ConectorPool.ESQUEMA_SYSMAN, formato);
				
 				
 	        }
 	        catch (JRException | IOException e) {
 	            logger.error(e.getMessage(), e);
 	            JsfUtil.agregarMensajeError(e.getMessage());
 	           e.printStackTrace();
 	        } catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 logger.error(e.getMessage(), e);
	 	            JsfUtil.agregarMensajeError(e.getMessage());
			}

 	    }
 	    
 	    
 	   public void cambiarAnio1() {
 	        // <CODIGO_DESARROLLADO>
 	        mes1 = null;
 	        cargarListaMes1();
 	        // </CODIGO_DESARROLLADO>
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
     * Retorna la variable anio1
     * 
     * @return  anio1
     */
	public String getanio1() {
		return anio1;
	}
	public void setAnio1(String anio1) {
		this.anio1 = anio1;
	}
	public String getMes1() {
		return mes1;
	}
	public void setMes1(String mes1) {
		this.mes1 = mes1;
	}
	public List<Registro> getListaAnio1() {
		return listaAnio1;
	}
	public void setListaAnio1(List<Registro> listaAnio1) {
		this.listaAnio1 = listaAnio1;
	}
	public List<Registro> getListames1() {
		return listames1;
	}
	public void setListames1(List<Registro> listames1) {
		this.listames1 = listames1;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public List<Registro> getListaNetoValor() {
		return listaNetoValor;
	}
	public void setListaNetoValor(List<Registro> listaNetoValor) {
		this.listaNetoValor = listaNetoValor;
	}
	public List<Registro> getListaNetoCero() {
		return listaNetoCero;
	}
	public void setListaNetoCero(List<Registro> listaNetoCero) {
		this.listaNetoCero = listaNetoCero;
	}
 	   
 	   



//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
