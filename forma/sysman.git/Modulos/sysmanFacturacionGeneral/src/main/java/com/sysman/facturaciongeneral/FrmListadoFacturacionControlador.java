/*-
 * FrmListadoFacturacionControlador.java
 *
 * 1.0
 * 
 * 08/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.facturaciongeneral.enums.FrmListadoFacturacionControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmListadoFacturacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * @author jcrodriguez,Migracion, actualizacion de controlador y
 * forma, tambien se migra el reporte INF_LISFAC_STD01,depuracion del
 * controlador (se eliminan metodos de los combos que se cambiaron por
 * campos), se adiciona a la consulta del reporte los filtros
 * deacuerdo a los campos del formuario
 * @version 1.0, 08/11/2017
 */
@ManagedBean
@ViewScoped

public class FrmListadoFacturacionControlador extends BeanBaseModal {
    /**
     * variable cadena que almacena la compania de la sesion actual
     */
    private final String compania;
    /**
     * variable cadena que almacena el modulo
     */
    
    private String formatoReporte;
    
    private boolean mostrarAcum;

    private final String modulo;
    
    private boolean verAcumulado;

	private boolean verEspecial;
    /**
     * variable cadena que almacena el nit inicial
     */

    private String nitInicial;
    /**
     * variable cadena que almacena el nit final o co
     */

    private String nitFinal;
    /**
     * variable cadena que almacena el concepto inicial
     */

    private String conceptoInicial;
    /**
     * variable cadena que almacena el concepto final
     */
    private String conceptoFinal;
    /**
     * variable cadena que almacena el nombre del tercero
     */
    private String terceroInicial;
    /**
     * variable que almacena el nombre del tercero final
     */

    private String terceroFinal;
    /**
     * variable cadena que almacena el nombre del concepto inicial
     */

    private String conInicial;
    /**
     * variable cadena que alamcena el nombre del concepto final
     */

    private String conFinal;
    /**
     * variable que almacena la fecha inicial
     */

    private Date fechaIncial;
    /**
     * variable que almacena la fecha final
     */

    private Date fechaFinal;
    /**
     * variable que lista el nit inicial
     */

    private RegistroDataModelImpl listaNITINICIAL;
    /**
     * variable que lista el nit final
     */
    private RegistroDataModelImpl listaNITFINAL;
    /**
     * variable que lista el concepto inicial
     */

    private RegistroDataModelImpl listaCONCEPTOINICIAL;
    /**
     * variable que lista el cocepto final
     */

    private RegistroDataModelImpl listaCONCEPTOFINAL;
    /**
     * variable de session que almacena el tipo de cobro
     */

    private String tipoCobro;
    /**
     * variable de session que almacena el nombre del tipo de cobro
     */

    private String nombreTipoCobro;
    /**
     * variable que almacena los reportes en formato pdf y excel para
     * su posterior descarga
     */

    private StreamedContent archivoDescarga;
    /**
     * variable que almacena el ano de cobro
     */
    private String anoCobro;

    /**
     * Crea una nueva instancia de FrmListadoFacturacionControlador
     */
    public FrmListadoFacturacionControlador() {

        super();
        compania = SessionUtil.getCompania();
        tipoCobro = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.TIPOCOBRO
                                                        .getValue()),
                                        "")
                        .toString();

        anoCobro = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.ANIO
                                                        .getValue()),
                                        "")
                        .toString();
        nombreTipoCobro = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar(
                                        ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                                        .getValue()),
                                        "")
                        .toString();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_LISTADOFACTURACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();

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

    	mostrarAcum = true;
    	formatoReporte = FrmListadoFacturacionControladorEnum.INFORME001486.getValue();
        cargarListaNITINICIAL();
        cargarListaNITFINAL();
        cargarListaCONCEPTOINICIAL();
        cargarListaCONCEPTOFINAL();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario Se adiciona el valor inicial del ano actual para las
     * fechas inicial y final al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        fechaIncial = fechaFinal = new Date();
    }

    /**
     * 
     * Carga la lista listaNITINICIAL
     */
    public void cargarListaNITINICIAL() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoFacturacionControladorUrlEnum.URL6416
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaNITINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmListadoFacturacionControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaNITFINAL
     */
    public void cargarListaNITFINAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoFacturacionControladorUrlEnum.URL5690
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(FrmListadoFacturacionControladorEnum.TERCEROINICIAL
                        .getValue(),
                        nitInicial);

        listaNITFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmListadoFacturacionControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaCONCEPTOINICIAL
     */
    public void cargarListaCONCEPTOINICIAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoFacturacionControladorUrlEnum.URL5627
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(FrmListadoFacturacionControladorEnum.TIPOCOBRO
                        .getValue(),
                        tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoCobro);

        listaCONCEPTOINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCONCEPTOFINAL
     */
    public void cargarListaCONCEPTOFINAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoFacturacionControladorUrlEnum.URL5629
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(FrmListadoFacturacionControladorEnum.TIPOCOBRO
                        .getValue(),
                        tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoCobro);
        param.put(FrmListadoFacturacionControladorEnum.CONCEPTOINICIAL
                        .getValue(),
                        conceptoInicial);

        listaCONCEPTOFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton pdf en la vista
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton excel en la vista
     *
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        
        if(mostrarAcum) {
    		
        	if (verEspecial) {
	            generaInformeEsp(FORMATOS.EXCEL);
	        } else {
	            generaInforme(FORMATOS.EXCEL);
	        }
    	}else {
    		
    		generaInformeAcum(FORMATOS.EXCEL);
    	}
        
        
    }

    /**
     * metodo que contiene la logica para imprimir un reporte en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generaInforme(FORMATOS formato) {

        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("nitInicial", nitInicial);
            reemplazar.put("nitFinal", nitFinal);
            reemplazar.put("conceptoInicial", conceptoInicial);
            reemplazar.put("conceptoFinal", conceptoFinal);
            reemplazar.put("fechaIncial",
                            SysmanFunciones.formatearFecha(fechaIncial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("tipoCobro", tipoCobro);
            
            Reporteador.resuelveConsulta(formatoReporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            parametros.put("PR_TIPOCOBRO", nombreTipoCobro);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaIncial));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            archivoDescarga = JsfUtil.exportarStreamed(formatoReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",formatoReporte));
            Logger.getLogger(FrmlistadoRecaudoDifControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    	


    }
    
    
    private void generaInformeAcum(FORMATOS formato) {
    	
    	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
    		HashMap<String, Object> reemplazar = new HashMap<>();
    		
            reemplazar.put("compania", compania);
            reemplazar.put("nitInicial", nitInicial);
            reemplazar.put("nitFinal", nitFinal);
            reemplazar.put("fechaIncial",
                            SysmanFunciones.formatearFecha(fechaIncial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("tipoCobro", tipoCobro);
            
            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            String reporte = FrmListadoFacturacionControladorEnum.INFORMEACUMULADO.getValue();
    		String sql = Reporteador.resuelveConsulta(reporte,reemplazar);
    		
    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                    ConectorPool.ESQUEMA_SYSMAN, formato, reporte);
    		
    		Workbook workbook = new XSSFWorkbook(
    				JsfUtil.exportarHojaDatosStreamed(sql,
    						ConectorPool.ESQUEMA_SYSMAN,
    						formato).getStream());

    		Sheet sheet = workbook.getSheetAt(0);

    		sheet.shiftRows(0, sheet.getLastRowNum(),3);

    		sheet.createFreezePane(0,3);

    		Font font2 = workbook.createFont();
    		font2.setFontName("Calibri");
    		font2.setFontHeightInPoints((short) 11);
    		font2.setBold(false);

    			Row r = sheet.createRow(0);
    			Cell cell1 = r.createCell(0);
    			cell1 = r.createCell(1);
    			cell1.setCellValue(nombreCompania.toUpperCase());
    			
    			Row r2 = sheet.createRow(1);
    			Cell cell2 = r2.createCell(0);
    			cell2 = r2.createCell(1);
    			cell2.setCellValue("RESUMEN DE FACTURAS EMITIDAS");
    			
    			Row r3 = sheet.createRow(2);
    			Cell cell3 = r3.createCell(0);
    			cell3 = r3.createCell(1);
    			String fechaIncials = new SimpleDateFormat("dd-MM-yyy").format(fechaIncial);
    			String fechaFinals = new SimpleDateFormat("dd-MM-yyy").format(fechaFinal);
    			cell3.setCellValue("De "+fechaIncials+" a "+fechaFinals);
    			
    			
    			Row r4 = sheet.createRow(sheet.getLastRowNum()+2);
    			Cell cell4 = r4.createCell(1);
    			
    			cell4 = r4.createCell(4);
    			cell4.setCellValue("TOTAL GENERAL");

    			cell4 = r4.createCell(5);
    			cell4.setCellFormula("SUM(F5:F"+(sheet.getLastRowNum()-1)+")");
    			
    			cell4 = r4.createCell(6);
    			cell4.setCellFormula("SUM(G5:G"+(sheet.getLastRowNum()-1)+")");
    			
    			cell4 = r4.createCell(7);
    			cell4.setCellFormula("SUM(H5:H"+(sheet.getLastRowNum()-1)+")");
    			
    			cell4 = r4.createCell(8);
    			cell4.setCellFormula("SUM(I5:I"+(sheet.getLastRowNum()-1)+")");
    			    			
    		workbook.write(out);

    		archivoDescarga = JsfUtil.getArchivoDescarga(
    				new ByteArrayInputStream(out.toByteArray()), formatoReporte+".xlsx");
    		workbook.close(); 	

    	}
    	catch ( JRException | IOException | DRException | SQLException | SysmanException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	} 


    }
    
    private void generaInformeEsp(FORMATOS formato) {

		try {
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("compania", compania);
			reemplazar.put("nitInicial", nitInicial);
			reemplazar.put("nitFinal", nitFinal);
			reemplazar.put("conceptoInicial", conceptoInicial);
			reemplazar.put("conceptoFinal", conceptoFinal);
			reemplazar.put("fechaIncial",
					SysmanFunciones.formatearFecha(fechaIncial));
			reemplazar.put("fechaFinal",
					SysmanFunciones.formatearFecha(fechaFinal));
			reemplazar.put("tipoCobro", tipoCobro);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(Reporteador.resuelveConsulta(formatoReporte, 
        			Integer.parseInt(SessionUtil.getModulo()),
        			reemplazar).toString(), 
    				ConectorPool.ESQUEMA_SYSMAN,
    				FORMATOS.EXCEL,formatoReporte);
		}
		catch (OutOfMemoryError | JRException
                | IOException | SysmanException | NumberFormatException | SQLException | DRException e) {
	    logger.error(e.getMessage(), e);
	    JsfUtil.agregarMensajeError(e.getMessage());
		}

    }


    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNITINICIAL Metodo que al cambiar el combo nit inicial
     * agrega a las variable nitInicial,terceroInicial valores
     * conrrepondiente a la seleccion de la fila del combo, tambien se
     * actualizan varios campos para que se restablesca la informacion
     * a vacio
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNITINICIAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitInicial = SysmanFunciones.nvl(registroAux.getCampos().get(
                        FrmListadoFacturacionControladorEnum.NIT.getValue()),
                        "")
                        .toString();
        terceroInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "")
                        .toString();
        terceroFinal = null;
        nitFinal = null;
        conceptoInicial = null;
        conceptoFinal = null;
        conInicial = null;
        conFinal = null;
        cargarListaNITFINAL();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNITFINAL Metodo que al cambiar el combo nit final agrega a
     * las variable nitFinal,terceroFinal valores conrrepondiente a la
     * seleccion de la fila del combo, tambien se actualizan varios
     * campos para que se restablesca la informacion a vacio
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNITFINAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
                        FrmListadoFacturacionControladorEnum.NIT.getValue()),
                        "")
                        .toString();
        terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.NOMBRE.getName()),
                        "")
                        .toString();
        conceptoInicial = null;
        conceptoFinal = null;
        conInicial = null;
        conFinal = null;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCONCEPTOINICIAL Metodo que al cambiar el combo concepto
     * inicial agrega a las variable conceptoInicial,conIncial valores
     * conrrepondiente a la seleccion de la fila del combo, tambien se
     * actualizan varios campos para que se restablesca la informacion
     * a vacio
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCONCEPTOINICIAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        conInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        conceptoFinal = null;
        conFinal = null;
        cargarListaCONCEPTOFINAL();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCONCEPTOFINAL Metodo que al cambiar el combo concepto
     * final agrega a las variable conceptoFinal,conFinal valores
     * conrrepondiente a la seleccion de la fila del combo, tambien se
     * actualizan varios campos para que se restablesca la informacion
     * a vacio
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCONCEPTOFINAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        conFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }
    
    
  //<METODOS_CAMBIAR>
  
    
    	public void cambiarAcumulado() {
         //<CODIGO_DESARROLLADO>
    		
    		if(verEspecial && verAcumulado) {
    			verEspecial = false;
    		}
    		
			if (mostrarAcum) {
				mostrarAcum = false;
				formatoReporte = FrmListadoFacturacionControladorEnum.INFORMEACUMULADO.getValue();
		    }
		    else {
		    	mostrarAcum = true;
		    	formatoReporte = FrmListadoFacturacionControladorEnum.INFORME001486.getValue();
		    }
        //</CODIGO_DESARROLLADO>
    }
    	
    	/**
    	 * Metodo ejecutado al cambiar el control Especial
    	 * 
    	 * TODO DOCUMENTACION ADICIONAL
    	 * 
    	 */
    	public void cambiarEspecial() {
    		//<CODIGO_DESARROLLADO>
    		verAcumulado = false;
    		mostrarAcum = false;
    		cambiarAcumulado();
    		if (verEspecial) {
    		formatoReporte = FrmListadoFacturacionControladorEnum.INFORMEESPECIAL.getValue();
    		}
    		//</CODIGO_DESARROLLADO>
    	}
    	
//</METODOS_CAMBIAR>


    /**
     * metodos get y set del controlador
     */


public boolean isMostrarAcum() {
    return mostrarAcum;
}


public void setMostrarAcum(boolean mostrarAcum) {
    this.mostrarAcum = mostrarAcum;
}

public String getFormatoReporte() {
    return formatoReporte;
}

public void setFormatoReporte(String formatoReporte) {
    this.formatoReporte = formatoReporte;
}

	/**
	 * Retorna la variable verEspecial
	 * 
	 * @return  verEspecial
	 */
	public boolean getVerEspecial() {
		return verEspecial;
	}
	/**
	 * Asigna la variable  verEspecial
	 * 
	 * @param  verEspecial
	 * Variable a asignar en  verEspecial
	 */
	public void setVerEspecial(boolean verEspecial) {
		this.verEspecial = verEspecial;
	}
	
	/**
	 * Retorna la variable verAcumulado
	 * 
	 * @return  verAcumulado
	 */
	public boolean getVerAcumulado() {
		return verAcumulado;
	}
	/**
	 * Asigna la variable  verAcumulado
	 * 
	 * @param  verAcumulado
	 * Variable a asignar en  verAcumulado
	 */
	public void setVerAcumulado(boolean verAcumulado) {
		this.verAcumulado = verAcumulado;
	}

/* 

  
    /**
     * Retorna la variable nitInicial
     * 
     * @return nitInicial
     */
    public String getNitInicial() {
        return nitInicial;
    }

    /**
     * Asigna la variable nitInicial
     * 
     * @param nitInicial
     * Variable a asignar en nitInicial
     */
    public void setNitInicial(String nitInicial) {
        this.nitInicial = nitInicial;
    }

    /**
     * Retorna la variable nitFinal
     * 
     * @return nitFinal
     */
    public String getNitFinal() {
        return nitFinal;
    }

    /**
     * Asigna la variable nitFinal
     * 
     * @param nitFinal
     * Variable a asignar en nitFinal
     */
    public void setNitFinal(String nitFinal) {
        this.nitFinal = nitFinal;
    }

    /**
     * Retorna la variable conceptoInicial
     * 
     * @return conceptoInicial
     */
    public String getConceptoInicial() {
        return conceptoInicial;
    }

    /**
     * Asigna la variable conceptoInicial
     * 
     * @param conceptoInicial
     * Variable a asignar en conceptoInicial
     */
    public void setConceptoInicial(String conceptoInicial) {
        this.conceptoInicial = conceptoInicial;
    }

    /**
     * Retorna la variable conceptoFinal
     * 
     * @return conceptoFinal
     */
    public String getConceptoFinal() {
        return conceptoFinal;
    }

    /**
     * Asigna la variable conceptoFinal
     * 
     * @param conceptoFinal
     * Variable a asignar en conceptoFinal
     */
    public void setConceptoFinal(String conceptoFinal) {
        this.conceptoFinal = conceptoFinal;
    }

    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable conInicial
     * 
     * @return conInicial
     */
    public String getConInicial() {
        return conInicial;
    }

    /**
     * Asigna la variable conInicial
     * 
     * @param conInicial
     * Variable a asignar en conInicial
     */
    public void setConInicial(String conInicial) {
        this.conInicial = conInicial;
    }

    /**
     * Retorna la variable conFinal
     * 
     * @return conFinal
     */
    public String getConFinal() {
        return conFinal;
    }

    /**
     * Asigna la variable conFinal
     * 
     * @param conFinal
     * Variable a asignar en conFinal
     */
    public void setConFinal(String conFinal) {
        this.conFinal = conFinal;
    }

    /**
     * Retorna la variable fechaIncial
     * 
     * @return fechaIncial
     */
    public Date getFechaIncial() {
        return fechaIncial;
    }

    /**
     * Asigna la variable fechaIncial
     * 
     * @param fechaIncial
     * Variable a asignar en fechaIncial
     */
    public void setFechaIncial(Date fechaIncial) {
        this.fechaIncial = fechaIncial;
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

    /**
     * Retorna la lista listaNITINICIAL
     * 
     * @return listaNITINICIAL
     */
    public RegistroDataModelImpl getListaNITINICIAL() {
        return listaNITINICIAL;
    }

    /**
     * Asigna la lista listaNITINICIAL
     * 
     * @param listaNITINICIAL
     * Variable a asignar en listaNITINICIAL
     */
    public void setListaNITINICIAL(RegistroDataModelImpl listaNITINICIAL) {
        this.listaNITINICIAL = listaNITINICIAL;
    }

    /**
     * Retorna la lista listaNITFINAL
     * 
     * @return listaNITFINAL
     */
    public RegistroDataModelImpl getListaNITFINAL() {
        return listaNITFINAL;
    }

    /**
     * Asigna la lista listaNITFINAL
     * 
     * @param listaNITFINAL
     * Variable a asignar en listaNITFINAL
     */
    public void setListaNITFINAL(RegistroDataModelImpl listaNITFINAL) {
        this.listaNITFINAL = listaNITFINAL;
    }

    /**
     * Retorna la lista listaCONCEPTOINICIAL
     * 
     * @return listaCONCEPTOINICIAL
     */
    public RegistroDataModelImpl getListaCONCEPTOINICIAL() {
        return listaCONCEPTOINICIAL;
    }

    /**
     * Asigna la lista listaCONCEPTOINICIAL
     * 
     * @param listaCONCEPTOINICIAL
     * Variable a asignar en listaCONCEPTOINICIAL
     */
    public void setListaCONCEPTOINICIAL(
        RegistroDataModelImpl listaCONCEPTOINICIAL) {
        this.listaCONCEPTOINICIAL = listaCONCEPTOINICIAL;
    }

    /**
     * Retorna la lista listaCONCEPTOFINAL
     * 
     * @return listaCONCEPTOFINAL
     */
    public RegistroDataModelImpl getListaCONCEPTOFINAL() {
        return listaCONCEPTOFINAL;
    }

    public void setListaCONCEPTOFINAL(
        RegistroDataModelImpl listaCONCEPTOFINAL) {
        this.listaCONCEPTOFINAL = listaCONCEPTOFINAL;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getNombreTipoCobro() {
        return nombreTipoCobro;
    }

    public void setNombreTipoCobro(String nombreTipoCobro) {
        this.nombreTipoCobro = nombreTipoCobro;
    }

}
