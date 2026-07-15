/*-
 * FrmInventarioSitioControlador.java
 *
 * 1.0
 * 
 * 27/12/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario modal de inventario sitio
 *
 * @version 1.0, 27/12/2019
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class FrmInventarioSitioControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private Date fechaInicial;

    private Date fechaFinal;

    private String tipoLectura;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmInventarioSitioControlador
     */
    public FrmInventarioSitioControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
        	//2146
            numFormulario = GeneralCodigoFormaEnum.FRM_INVENTARIO_SITIO_CONTROLADOR.getCodigo();
            validarPermisos();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirImprimir() {
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
    }
    
    public void oprimirExcel() {
        archivoDescarga = null;
        generaInformePlano(FORMATOS.EXCEL);
    }

    private void generaReporte(FORMATOS formato) {
        try {
            String reporte = "001984RPTINVENTARIOSITIO";

            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("tipo", "'" + tipoLectura + "'");

            String pfechaInicio = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String pfechaFinal = SysmanFunciones
                            .convertirAFechaCadena(fechaFinal);

            reemplazar.put("fechaIni", pfechaInicio);
            reemplazar.put("fechaFin", pfechaFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
 private void generaInformePlano(FORMATOS formato) {
    	
    	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
    		HashMap<String, Object> reemplazar = new HashMap<>();
    		
    		reemplazar.put("compania", compania);
            reemplazar.put("tipo", "'" + tipoLectura + "'");

            String pfechaInicio = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String pfechaFinal = SysmanFunciones
                            .convertirAFechaCadena(fechaFinal);

            reemplazar.put("fechaIni", pfechaInicio);
            reemplazar.put("fechaFin", pfechaFinal);
            
            String reporte = "001984RPTINVENTARIOSITIO";
    		String sql = Reporteador.resuelveConsulta(reporte,reemplazar);
    		
    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                    ConectorPool.ESQUEMA_SYSMAN, formato, reporte);
    		
    		Workbook workbook = new XSSFWorkbook(
    				JsfUtil.exportarHojaDatosStreamed(sql,
    						ConectorPool.ESQUEMA_SYSMAN,
    						formato).getStream());

    		Sheet sheet = workbook.getSheetAt(0);

    		sheet.shiftRows(0, sheet.getLastRowNum(),1);

    		sheet.createFreezePane(0,2);
    		
    		CellStyle titleStyle = workbook.createCellStyle();
    		Font font2 = workbook.createFont();
    		font2.setFontName("Calibri");
    		font2.setFontHeightInPoints((short) 16);
    		font2.setBold(true);
    		titleStyle.setFont(font2);
    			
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
				
    			Row r = sheet.createRow(0);
    			Cell cell = r.createCell(0);
    			cell.setCellValue((SessionUtil.getCompaniaIngreso().getNombre()+" INVENTARIO EN SITIO").toUpperCase());
    			cell.setCellStyle(titleStyle);
    			

    			
    		workbook.write(out); 

    		archivoDescarga = JsfUtil.getArchivoDescarga(
    				new ByteArrayInputStream(out.toByteArray()), reporte+".xlsx");
    		workbook.close(); 	

    	}
    	catch ( JRException | IOException | DRException | SQLException | SysmanException | ParseException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	} 


    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getTipoLectura() {
        return tipoLectura;
    }

    public void setTipoLectura(String tipoLectura) {
        this.tipoLectura = tipoLectura;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
