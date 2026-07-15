/*-
 * FrmEliminarCptoNominaControlador.java
 *
 * 1.0
 * 
 * 24/05/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Usuario;
import com.sysman.nomina.ejb.impl.EjbNominaCeroGeneral;
import com.sysman.nomina.ejb.impl.EjbNominaNueveGeneral;
import com.sysman.nomina.enums.FrmEliminarCptoNominaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * @version 1.0, 24/05/2023
 * @author jmillan
 */
@ManagedBean(name="frmEliminarCptoNominaControlador")
@ViewScoped
public class  FrmEliminarCptoNominaControlador extends BeanBaseModal{

	//private final String usuarioValido = "PRUEBAS_SS"; //"ADMIN" usuario al cual se le es permitido interactuar con el dormulario;
	private final String reporte="800574LISTADOFUNCIONARIOSCPTO";

	private final String compania ;
	
	private String esAdminNomina = "0";
    
private String proceso;

private String anio;

private String mes;

private String CPTO;

private String periodo;

private Usuario usuario;

private String empleado;

private List<Registro> listaProceso;

private List<Registro> listaAno;

private List<Registro> listaMes;

private List<Registro> listaPeriodo;

private RegistroDataModelImpl listaconcepto;

private RegistroDataModelImpl listaempleado;

private EjbNominaCeroGeneral ejbNominaCero = new EjbNominaCeroGeneral();
private EjbNominaNueveGeneral ejbNominaNueve = new EjbNominaNueveGeneral();
private StreamedContent archivoDescarga;
   
    public FrmEliminarCptoNominaControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser();
        try {
            // 2403
            numFormulario = GeneralCodigoFormaEnum.FRM_ELIMINAR_CPTO_NOMINA
                            .getCodigo();
        
            validarPermisos();

		    } catch (Exception ex) {
		        logger.error(ex.getMessage(),ex);
		        SessionUtil.redireccionarMenuPermisos();
		    }finally{
		    }
    }

    @PostConstruct
    public void inicializar(){
		cargarListaProceso();
		verificaarAdminNomina();
		abrirFormulario();
    }
    
    
    public void limpiarformulario() {	
        anio = null;
        mes = null;
        periodo = null;
        CPTO= null;
        empleado = null;
        listaMes = null;
        listaPeriodo = null;
        listaconcepto = null;
        listaempleado = null;
        
    	cargarListaProceso();
		abrirFormulario();  	
    }

  @Override
	public void abrirFormulario(){ 
      proceso = "1";
      cargarListaAno();
      anio = Integer.toString(SysmanFunciones.ano(new Date()));
      cargarListaMes();
      mes = Integer.toString(SysmanFunciones.mes(new Date()));
      cargarListaPeriodo();
    }


public void cargarListaProceso(){

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEliminarCptoNominaControladorUrlEnum.URL4058
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        } 

    
}


public void verificaarAdminNomina(){

   

    try {
    	 Map<String, Object> param = new TreeMap<>();
    	 param.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());

        Registro rs = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                        		FrmEliminarCptoNominaControladorUrlEnum.URL7956
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));

        if (rs != null)
        {
        	esAdminNomina = SysmanFunciones.nvl(rs.getCampos().get("EXISTE"),"0").toString();
        }
        else
        {
        	esAdminNomina = "0";
        }
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    } 


}

public void cargarListaAno(){

    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

    try {
        listaAno = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        FrmEliminarCptoNominaControladorUrlEnum.URL4735
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }
    
}

public void cargarListaMes(){
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
    param.put(GeneralParameterEnum.ANO.getName(), anio);

    try {
        listaMes = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        FrmEliminarCptoNominaControladorUrlEnum.URL5723
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }
}

public void cargarListaPeriodo(){

    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
    param.put(GeneralParameterEnum.ANO.getName(), anio);
    param.put(GeneralParameterEnum.MES.getName(), mes);

    try {
        listaPeriodo = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        FrmEliminarCptoNominaControladorUrlEnum.URL7274
                                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e) {
        JsfUtil.agregarMensajeError(e.getMessage());
        logger.error(e.getMessage(), e);

    }
    
}

public void cargarListaconcepto(){

	   Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
	    param.put(GeneralParameterEnum.ANO.getName(), anio);
	    param.put(GeneralParameterEnum.MES.getName(), mes);
	    param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

	    	
	        UrlBean urlBean = UrlServiceUtil.getInstance()
	                                        .getUrlServiceByUrlByEnumID(
	                                                        FrmEliminarCptoNominaControladorUrlEnum.URL6834
	                                                                        .getValue());
   
	        
	    	listaconcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true,
                    GeneralParameterEnum.ID_DE_CONCEPTO.getName());
    
	    
}

public void cargarlistaempleado(){

	   Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
	    param.put(GeneralParameterEnum.ANO.getName(), anio);
	    param.put(GeneralParameterEnum.MES.getName(), mes);
	    param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
	    param.put(GeneralParameterEnum.CONCEPTO.getName(), CPTO);
	    
	        UrlBean urlBean = UrlServiceUtil.getInstance()
	                                        .getUrlServiceByUrlByEnumID(
	                                                        FrmEliminarCptoNominaControladorUrlEnum.URL7945
	                                                                        .getValue());

	        listaempleado = new RegistroDataModelImpl(urlBean.getUrl(),
                 urlBean.getUrlConteo().getUrl(), param,
                 true,
                 GeneralParameterEnum.ID_DE_EMPLEADO.getName());
	        
 
	    
}

public void oprimircmdIniciar() {
			try {
				
				if(!"0".equalsIgnoreCase(esAdminNomina)) {
					boolean periodoActivo = ejbNominaCero
			                 .validarPeriodoActivoNomina(compania,
			                                 Integer.parseInt(proceso),
			                                 Integer.parseInt(anio),
			                                 Integer.parseInt(mes),
			                                 Integer.parseInt(periodo));
			
						 if(periodoActivo) {
			           	
			           	 ejbNominaNueve.updateCptoPeriodoNomina(compania, Integer.parseInt(proceso),
			           			 Integer.parseInt(anio), Integer.parseInt(mes), 
			           			 Integer.parseInt(periodo));
			           	 
			           	 
			           	 JsfUtil.agregarMensajeInformativo(
			                     idioma.getString("MSM_PROCESO_EJECUTADO"));
			           	 
			            }else {
			           	 
						   	 JsfUtil.agregarMensajeError(idioma.getString("TB_TB2550"));
			            }
				}else {
					 JsfUtil.agregarMensajeError(idioma.getString("MSM_PERMISOS_ACCEDER"));
				}
				
				 
			} catch (NumberFormatException | SystemException e) {
				 e.printStackTrace();
			}

    }


public void oprimircmdEliminar() {

	
	try {
		
		if(!"0".equalsIgnoreCase(esAdminNomina)) {
			boolean periodoActivo = ejbNominaCero
	                 .validarPeriodoActivoNomina(compania,
	                                 Integer.parseInt(proceso),
	                                 Integer.parseInt(anio),
	                                 Integer.parseInt(mes),
	                                 Integer.parseInt(periodo));
	
				 if(periodoActivo) {
			   	
					 ejbNominaNueve.deleteCptoPeriodoNomina(compania, Integer.parseInt(proceso),
				  			 Integer.parseInt(anio), Integer.parseInt(mes), 
				  			 Integer.parseInt(periodo), Integer.parseInt(CPTO),Integer.parseInt(empleado)); 
					 
			   	 
			   	 JsfUtil.agregarMensajeInformativo(
			             idioma.getString("MSM_PROCESO_EJECUTADO"));
			   	limpiarformulario();
			   	 
			    }else {
			   	 
			   	 JsfUtil.agregarMensajeError(idioma.getString("TB_TB2550"));
			   	 
			    }
		}else {
			 JsfUtil.agregarMensajeError(idioma.getString("MSM_PERMISOS_ACCEDER"));
		}
		
		 
	} catch (NumberFormatException | SystemException e) {
		e.printStackTrace();
	}
}

public void oprimircmdExcel() {
		try {
			
			if(!"0".equalsIgnoreCase(esAdminNomina)) {
				boolean periodoActivo = ejbNominaCero
		                 .validarPeriodoActivoNomina(compania,
		                                 Integer.parseInt(proceso),
		                                 Integer.parseInt(anio),
		                                 Integer.parseInt(mes),
		                                 Integer.parseInt(periodo));
		
					 if(periodoActivo) {
				         
				         generarInforme(ReportesBean.FORMATOS.EXCEL);
				   	 
				    }else {
				   	 
				    	JsfUtil.agregarMensajeError(idioma.getString("TB_TB2550"));
				    }
			}else {
					JsfUtil.agregarMensajeError(idioma.getString("MSM_PERMISOS_ACCEDER"));
			}
			
			 
		} catch (NumberFormatException | SystemException e) {
			e.printStackTrace();
		}
    }

public void generarInforme(ReportesBean.FORMATOS formato) 
{

	
	 try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("proceso", proceso);
		reemplazar.put("compania", "'"+compania+"'");
        reemplazar.put("ano", anio);
        reemplazar.put("mes", mes);
        reemplazar.put("periodo", periodo);
        reemplazar.put("concepto", CPTO);
        reemplazar.put("empleado",  "'"+empleado+"'");

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

		Font font2 = workbook.createFont();
		font2.setFontName("Calibri");
		font2.setFontHeightInPoints((short) 11);
		font2.setBold(false);

		CellStyle style2 = workbook.createCellStyle();
		style2.setAlignment(CellStyle.ALIGN_LEFT);
		style2.setFont(font2);
		style2.setBorderBottom(CellStyle.BORDER_THIN);
		style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style2.setBorderLeft(CellStyle.BORDER_THIN);
		style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style2.setBorderTop(CellStyle.BORDER_THIN);
		style2.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setRightBorderColor(IndexedColors.BLACK.getIndex());
		
		int jc = 0;
		Row r = sheet.createRow(0);
       
			Cell cell1 = r.createCell(0);
			
			cell1 = r.createCell(1);
			cell1.setCellValue("HISTORICOS DE FUNCIONARIOS ANO "+anio+" MES "+mes+" PEDIODO "+periodo+" POR CONCEPTO "+CPTO);
			
			jc = 9;
		
		
		for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
			Row r3 = sheet.getRow(i);
			for (int j = 0; j < jc; j++) {
				Cell cell2 = r3.getCell(j);
				cell2.setCellStyle(style2);
			}
		}
		workbook.write(out);

		out.close();

		archivoDescarga = JsfUtil.getArchivoDescarga(
				new ByteArrayInputStream(out.toByteArray()),"800574LISTADOFUNCIONARIOSCPTO.xlsx");
		workbook.close(); 	

	}
	catch (IOException | NumberFormatException | JRException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException
			 e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	} 

}

public void cambiarProceso() {
	
    anio = null;
    mes = null;
    periodo = null;
    CPTO= null;
    empleado = null;
    listaMes = null;
    listaPeriodo = null;
    listaconcepto = null;
    listaempleado = null;
	cargarListaAno();
    cambiarAno();
    cambiarMes();
    }


public void cambiarAno() {

    mes = null;
    periodo = null;
    CPTO= null;
    empleado = null;
    listaPeriodo = null;
    listaconcepto = null;
    listaempleado = null;
    cargarListaMes();
    cambiarMes();

    }

public void cambiarMes() {
	 periodo = null;
	 CPTO= null;
	 empleado = null;
	 listaconcepto = null;
	 listaempleado = null;
     cargarListaPeriodo();
     cambiarPeriodo();
    }

public void cambiarPeriodo() {
	CPTO= null;
	empleado = null;
    listaconcepto = null;
    listaempleado = null;
	cargarListaconcepto();
    }



public void seleccionarFilaconcepto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        CPTO= registroAux.getCampos().get("ID_DE_CONCEPTO").toString();
        empleado = null;
    	listaempleado = null;
    	cargarlistaempleado();
}

public void seleccionarFilaempleado(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();
	empleado= registroAux.getCampos().get("ID_DE_EMPLEADO").toString();
}


public String getEmpleado() {
    return empleado;
}

public void setEmpleado(String empleado) {
    this.empleado = empleado;
}

public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

public String getCPTO() {
        return CPTO;
    }

    public void setCPTO(String CPTO) {
        this.CPTO = CPTO;
    }

public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

public List<Registro> getListaProceso() {
        return listaProceso;
    }

public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

public List<Registro> getListaAno() {
        return listaAno;
    }

public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

public List<Registro> getListaMes() {
        return listaMes;
    }

public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public RegistroDataModelImpl getListaconcepto() {
        return listaconcepto;
    }

    public void setListaconcepto(RegistroDataModelImpl listaconcepto) {
        this.listaconcepto = listaconcepto;
    }
    
    
    public RegistroDataModelImpl getListaempleado() {
        return listaempleado;
    }

    public void setListaempleado(RegistroDataModelImpl listaempleado) {
        this.listaempleado = listaempleado;
    }
    
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

   
}
