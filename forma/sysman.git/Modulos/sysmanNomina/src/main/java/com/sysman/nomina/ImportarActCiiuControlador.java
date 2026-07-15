/*-
 * ImportarActCiiuControlador.java
 *
 * 1.0
 * 
 * 22/11/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;


import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.sql.SQLException;
import java.util.HashMap;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/11/2022
 * @author jcrojas
 */
@ManagedBean
@ViewScoped
public class  ImportarActCiiuControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos ActividadesCiiu y funciona como contenedor del archivo que se
     * debe guardar
     */
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoActividadesCiiu;
        
    private StringBuilder actividadesCiiu;
    
    @EJB
    private EjbNominaOchoRemote ejbNominaOcho;
    /**
     * Crea una nueva instancia de ImportarActCiiuControlador
     */
    public ImportarActCiiuControlador() 
    {
    	super();
        compania = SessionUtil.getCompania();
        contArchivoActividadesCiiu = new ContenedorArchivo();
        
        try 
        {
            numFormulario = GeneralCodigoFormaEnum.IMPORTARACTCIIU_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) 
        {
            Logger.getLogger(ImportarActCiiuControlador.class.getName())
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
    public void inicializar()
    {
		abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario() {}
    /**
     * 
     * Metodo ejecutado al oprimir el boton btAceptar
     * en la vista
     * @throws SystemException 
     *
     */
    public void oprimirbtAceptar() throws SystemException 
    {
    	try 
    	{
    		archivoDescarga = null;
    		if (contArchivoActividadesCiiu.getArchivo() == null) 
    		{
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4372"));
                return;
            }
    		
    		String rutaArchivo = contArchivoActividadesCiiu.getArchivo().getPath();
        	FileInputStream file = new FileInputStream(new File(rutaArchivo));
            Workbook workbook = new XSSFWorkbook(file); 
            
            actividadesCiiu = new StringBuilder();
            leerHoja(workbook, 0, 7, actividadesCiiu, 1);
            
            //String plano = actividadesCiiu.toString().replace("TO_CLOB('", "").replace("')", "");
            String plano = actividadesCiiu.toString();
            
            archivoDescarga = JsfUtil.getArchivoDescarga(
                    				JsfUtil.serializarPlano(
                    						ejbNominaOcho.actualizaActividadesCiiu(compania,
 												   plano,
                    							   SessionUtil.getUser().getCodigo())
                    						),
                    				"CargaActividadesCIIU.log"
                    				);
            
			workbook.close();
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} 
    	catch (JRException | SystemException | IOException e) 
    	{
    		e.printStackTrace();
		}
    }
    
    public void leerHoja(Workbook workbook, int hoja, int columnas, 
    		StringBuilder cadena, int filainicial) 
    {
    	cadena.append("TO_CLOB('");
        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        int num = 0;
        
        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) 
        {
        	fila = sheet.getRow(i);
            for (int j = 0; j < columnas; j++) 
            {
            	celda = fila.getCell(j);
            	if (celda != null) 
            	{
            		num = num
            			+ (celda.getCellType() == 1 ? celda.getStringCellValue()
            							.replaceFirst("'", " ").length()
            			   : NumberToTextConverter
            			   				.toText(celda.getNumericCellValue())
            			    			.length());
                           cadena.append(celda.getCellType() == 1 ? celda.getStringCellValue()
                        		   					  .replaceFirst("'", " ")
                           : NumberToTextConverter
                           				.toText(celda.getNumericCellValue()));
                }
                else 
                {
                	cadena.append("");
                }
            	
                if (num >= 10000) 
                {
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
    /**
     * 
     * Metodo ejecutado al oprimir el boton btCancelar
     * en la vista
     *
     */
    public void oprimirbtCancelar() 
    {
    	RequestContext.getCurrentInstance().closeDialog(null);
    }
    /**
     * Retorna el objeto contArchivoActividadesCiiu
     * 
     * @return contArchivoActividadesCiiu
     */
    public void oprimirbtGenerar() 
    {
    	archivoDescarga = null;
    	String sql = Reporteador.resuelveConsulta("800549ActividadesCIIUPersonal",
    			Integer.parseInt( SessionUtil.getModulo()),
    			null);

    	try
    	{
    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
    				ConectorPool.ESQUEMA_SYSMAN,
    				ReportesBean.FORMATOS.EXCEL, "ActividadesEconomicasCIIU");
    	}
    	catch (JRException | IOException | SQLException | DRException
    			| SysmanException e)
    	{
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}

    }
    
    public ContenedorArchivo getContArchivoActividadesCiiu() 
    {
        return contArchivoActividadesCiiu;
    }
    /**
     * Asigna el objeto contArchivoActividadesCiiu
     * 
     * @param contArchivoActividadesCiiu
     * Variable a asignar en contArchivoActividadesCiiu
     */
    public void setContArchivoActividadesCiiu(ContenedorArchivo contArchivoActividadesCiiu) 
    {
        this.contArchivoActividadesCiiu = contArchivoActividadesCiiu;
    }
    
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }
}
