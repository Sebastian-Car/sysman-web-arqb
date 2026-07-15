/*-
 * FrmConsultasGrandesControlador.java
 *
 * 1.0
 * 
 * 31/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Usuario;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmConsultasGrandesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *  yo soy la documentacion *cara fachera*
 *  no mentira si le voy a colocar unos comentarios
 * @version 1.0, 31/07/2025
 * @author jmillan
 */
@ManagedBean
@ViewScoped
public class FrmConsultasGrandesControlador extends BeanBaseDatosAcme{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    private Usuario usuario;
    
//VARIABLES 
private String aplicacion = "";
private String modulo = "";
private String consulta = "";
private int registros = 0;
private boolean existe =  false;
private StreamedContent archivoDescarga;
//CONSTANTES
private final int cModulo = 99;
private final String cInforme = "INFORME";
private final String cAplicacion = "APLICACION";
private final String cNombre = "NOMBRE";
private final String cRegistrsos = "REGISTROS";
private final String cCreated = "CREATED_BY";
private final String cModified = "MODIFIED_BY";
private final String cKeyI = "KEY_INFORME";
private final String cKeyA ="KEY_APLICACION";
private final String cTitulo ="Listado de Consultas Configuradas";
private final String cMetodoU = "_putlimiteconsultas_updateregistros";
private final String cMetodoC = "_postlimiteconsultas_setregistros";
private final String usuarioValido = "PRUEBAS_SS"; //"ADMIN" usuario al cual se le es permitido interactuar con el dormulario;
private final String reporte="800718ListadoConsultasLimitadas";
//COMBOS O LISTAS
private RegistroDataModelImpl listaCB8858; // lista de modulo/aplicacion

private RegistroDataModelImpl listaCB8859; // lista de consultas/informes 


    /**
     * Crea una nueva instancia de FrmConsultasGrandesControlador
     */
    public FrmConsultasGrandesControlador() {
        super();
            compania = SessionUtil.getCompania();
            usuario = SessionUtil.getUser();

            
        try {
        	if(usuario.getCodigo().equalsIgnoreCase(usuarioValido)) {
	            numFormulario = GeneralCodigoFormaEnum.CONSULTAS_GRANDES_CONTROLADOR.getCodigo();  //2529; 
	            validarPermisos();
        	}

         } catch (Exception ex) {
        	 logger.error(ex.getMessage(),ex);
             SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }

    public void iniciarListas(){
     cargarListaCB8858(); //aplicacion modulo 
    }

    @PostConstruct
    public void inicializar(){
    	iniciarListas(); 
    }
    
//<METODOS_CARGAR_LISTA>    
    /**
     * 
     * Carga la lista listaCB8858
     * llena los modulos
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaCB8858(){

    UrlBean urlBean = UrlServiceUtil.getInstance()
	         .getUrlServiceByUrlByEnumID(
	        		 FrmConsultasGrandesControladorUrlEnum.URL4058
	                                         .getValue());
    

	listaCB8858 = new RegistroDataModelImpl(urlBean.getUrl(),
	                 urlBean.getUrlConteo().getUrl(), null,
	                 true, cAplicacion); 

}
    /**
     * 
     * Carga la lista listaCB8859
     * llena las consultas 
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaCB8859(){
	

	    UrlBean urlBean = UrlServiceUtil.getInstance()
		         .getUrlServiceByUrlByEnumID(
		        		 FrmConsultasGrandesControladorUrlEnum.URL4735
		                                         .getValue());

		 Map<String, Object> param = new TreeMap<>();
		 param.put(cAplicacion, aplicacion);

		 listaCB8859 = new RegistroDataModelImpl(urlBean.getUrl(),
		                 urlBean.getUrlConteo().getUrl(), param,
		                 true, cInforme);  
}

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCB8858
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCB8858(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();

	aplicacion = SysmanFunciones.nvl(registroAux.getCampos().get(cAplicacion), "").toString(); //es el codigo de la aplicacion
	modulo = SysmanFunciones.nvl(registroAux.getCampos().get(cNombre), "").toString(); // es el nombre de la aplicacion 
	
	cargarListaCB8859();
}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCB8859
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
public void seleccionarFilaCB8859(SelectEvent event) {
	Registro registroAux = (Registro) event.getObject();

	consulta = SysmanFunciones.nvl(registroAux.getCampos().get(cInforme), "").toString(); //el nombre de la consulta 
	
	buscarRegistros(); //verifica si esta o no en la tabla 
	
}

//</METODOS_COMBOS_GRANDES>

public void buscarRegistros() {
	
	try {
	Map<String, Object> param = new TreeMap<>();
	param.put(cInforme, consulta);
	param.put(cAplicacion, aplicacion);

	Registro rsRegistros = RegistroConverter
			.toRegistro(
					requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmConsultasGrandesControladorUrlEnum.URL7945.getValue())
									.getUrl(),
							param));
	
			if(rsRegistros != null) {
				existe = true; //si exite trae el numero de registros limitados 
				registros = (int) Double.parseDouble(rsRegistros.getCampos().get(cRegistrsos).toString());
			}else {
				existe = false; // si no existe inicializa en 0 para que luego se inserte 
				registros = 0;
			}
		}
		catch (SystemException e) {
		    JsfUtil.agregarMensajeError(e.getMessage());
		    logger.error(e.getMessage(), e);
		
		} 
}
//<METODOS_BOTONES> 
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT4157
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirBT4157() {
         //<CODIGO_DESARROLLADO>
	
try {
		
		if(usuario.getCodigo().equalsIgnoreCase(usuarioValido)) {
			         
			try {
				Map<String, Object> param = new TreeMap<>();
				Map<String, Object> keys = new TreeMap<>();
				
				if(existe) { // modifica el registro 
					
					keys.put(cKeyI, consulta);
					keys.put(cKeyA, aplicacion);
					param.put(cRegistrsos, registros);
					param.put(cModified, usuarioValido);
					
					requestManager.update(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmConsultasGrandesControladorUrlEnum.URL5840.getValue())
									.getUrl(),
							cMetodoU, param, keys);
					
					JsfUtil.agregarMensajeGlobal(idioma.getString("TG_CAMBIOS_GUARDADOS"),FacesMessage.SEVERITY_INFO);
					
					//MSM_REGISTRO_INGRESADO
					
				}else { // inserta el registro 
					
					param.put(cInforme, consulta);
					param.put(cAplicacion, aplicacion);
					param.put(cRegistrsos, registros);
					param.put(cCreated, usuarioValido);
					
					requestManager.save(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmConsultasGrandesControladorUrlEnum.URL4579.getValue())
									.getUrl(),
							cMetodoC, param);
								
					JsfUtil.agregarMensajeGlobal(idioma.getString("TG_CAMBIOS_GUARDADOS"),FacesMessage.SEVERITY_INFO);
					//MSM_REGISTRO_MODIFICADO
					
				}
			}catch (SystemException e) {
				    JsfUtil.agregarMensajeError(e.getMessage());
				    logger.error(e.getMessage(), e);
				
			} 

		}else {
				JsfUtil.agregarMensajeError(idioma.getString("MSM_PERMISOS_ACCEDER"));
		}
		
		 
	} catch (NumberFormatException e) {
		e.printStackTrace();
	}
	
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT4158
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
public void oprimirBT4158() {
         //<CODIGO_DESARROLLADO>
	if(usuario.getCodigo().equalsIgnoreCase(usuarioValido)) {
        
        generarInforme(ReportesBean.FORMATOS.EXCEL);

	}else {
		
		JsfUtil.agregarMensajeError(idioma.getString("MSM_PERMISOS_ACCEDER"));
		
	}
	
        //</CODIGO_DESARROLLADO>
    }


public void generarInforme(ReportesBean.FORMATOS formato) 
{

	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
		
        String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        Map<String, Object> reemplazar = new HashMap<>();
		reemplazar.put(cAplicacion, SysmanFunciones.nvl(aplicacion,-99));

		String sql = Reporteador.resuelveConsulta(reporte, cModulo, reemplazar);
		
		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, reporte);
		
		Workbook workbook = new XSSFWorkbook(
				JsfUtil.exportarHojaDatosStreamed(sql,
						ConectorPool.ESQUEMA_SYSMAN,
						FORMATOS.EXCEL).getStream());

		Sheet sheet = workbook.getSheetAt(0);

		sheet.shiftRows(0, sheet.getLastRowNum(),2);

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
			cell2.setCellValue(cTitulo.toUpperCase());	    			
			    			
		workbook.write(out);

		archivoDescarga = JsfUtil.getArchivoDescarga(
				new ByteArrayInputStream(out.toByteArray()), reporte+".xlsx");
		workbook.close(); 	

	}
	catch ( JRException | IOException | DRException | SQLException | SysmanException | NumberFormatException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	} 


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
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
    @Override
    public void cargarRegistro() {
        //<CODIGO_DESARROLLADO>
        precargarRegistro();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
@Override
    public boolean insertarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
@Override
    public boolean actualizarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
//<SET_GET_ATRIBUTOS>
    public String getModulo() {
    	return modulo;
    }
    public void setModulo(String modulo) {
    	this.modulo = modulo;
    }
    public String getAplicacion() {
    	return aplicacion;
    }
    public void setAplicacion(String aplicacion) {
    	this.aplicacion = aplicacion;
    }
    public String getConsulta() {
    	return consulta;
    }
    public void setConsulta(String consulta) {
    	this.consulta = consulta;
    }
    public Integer getRegistros() {
    	return registros;
    }
    public void setRegistros(Integer registros) {
    	this.registros = registros;
    }
    public String getReporte() {
    	return reporte;
    }
    public StreamedContent getArchivoDescarga() {
    	return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
    	this.archivoDescarga = archivoDescarga;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE> 
    /**
     * Retorna la lista listaCB8858
     * 
     * @return listaCB8858
     */
    
    public RegistroDataModelImpl getListaCB8858() {
		return listaCB8858;
	}
    
    /**
     * Asigna la lista listaCB8858
     * 
     * @param listaCB8858
     * Variable a asignar en  listaCB8858
     */
    
	public void setListaCB8858(RegistroDataModelImpl listaCB8858) {
		this.listaCB8858 = listaCB8858;
	}
    
    /**
     * Retorna la lista listaCB8859
     * 
     * @return listaCB8859
     */
	
    public RegistroDataModelImpl getListaCB8859() {
		return listaCB8859;
	}

    /**
     * Asigna la lista listaCB8859
     * 
     * @param listaCB8859
     * Variable a asignar en  listaCB8859
     */

	public void setListaCB8859(RegistroDataModelImpl listaCB8859) {
		this.listaCB8859 = listaCB8859;
	}
	
	@Override
	public void iniciarListasSubNulo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void iniciarListasSub() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reasignarOrigenGrilla() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void asignarOrigenDatos() {
		// TODO Auto-generated method stub
		
	}

//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES> 
//</SET_GET_ADICIONALES>
}
