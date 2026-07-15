/*-
 * FrGenerarPlanosPensionadosControlador.java
 *
 * 1.0
 * 
 * 29/11/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
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
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/11/2022
 * @author carenas
 */
@ManagedBean
@ViewScoped
public class FrGenerarPlanosPensionadosControlador extends BeanBaseDatosAcme{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
//<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private Date fechaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private Date fechaFinal;

private String titulo;

private StreamedContent archivoDescarga;

@EJB
private EjbNominaSeisRemote ejbNominaSeis;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrGenerarPlanosPensionadosControlador
     */
	public FrGenerarPlanosPensionadosControlador() {
		super();
			validaOpcionMenuTitulo();
	    	compania = SessionUtil.getCompania();
	    	fechaInicial = new Date();
	    	fechaFinal = new Date();
	    	try {
			numFormulario = GeneralCodigoFormaEnum.FR_GENERAR_PLANOS_PENSIONADOS_CONTROLADOR
					.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		 } catch (Exception ex) {
			 logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	@Override
	public void iniciarListas(){
	//<CARGAR_LISTA_COMBO_GRANDE>
	//</CARGAR_LISTA_COMBO_GRANDE>
	//<CARGAR_LISTA>
	//</CARGAR_LISTA>
	}
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
	@Override
	public void iniciarListasSub(){
	//<CARGAR_LISTAS_SUBFORM>
	//</CARGAR_LISTAS_SUBFORM>
	//<CREAR_ARBOLES>
	//</CREAR_ARBOLES>
	}
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
	@Override
	public void iniciarListasSubNulo(){
	//<CARGAR_LISTAS_SUBFORM_NULL>
	//</CARGAR_LISTAS_SUBFORM_NULL>
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
	}
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
@Override
  public void asignarOrigenDatos() {
origenDatos="";	
}
    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
@Override
  public void reasignarOrigenGrilla() {
	origenGrilla="";
  if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
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
		 registro.getCampos().put("COMPANIA", compania);
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
    
    public void oprimirGenerarPlano() {
    	//String datos = validarTipoPlano();
    	validarTipoPlano();
    	

    }
    
    public void validaOpcionMenuTitulo() {
    	String menuActual = SysmanFunciones.toString(SessionUtil.getMenuActual());
    	System.out.println(menuActual);
    	if (menuActual.equals("6040801")) {
    		titulo = "GENERAR PLANO CARGUE PERIODICO";
    	}else
    	{
    		titulo = "GENERAR PLANO NOVEDADES DE PENSIONADOS";
    	}
    }
    
    public void validarTipoPlano() {
    	String menuActual = SysmanFunciones.toString(SessionUtil.getMenuActual());
    	String datos = null;
    	if (menuActual.equals("6040801")) {
            
    		try {
    			
    			
				datos = ejbNominaSeis.generarPlanoPeriodico(  
					 compania,
				     fechaInicial,
				     fechaFinal
				     );
		        try {
		            archivoDescarga = null;
		            ByteArrayInputStream streamTexto;
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		            String fecha = sdf.format(fechaFinal);
		            fecha = fecha.replaceAll("[/]", "");
		            
		            
		         

		            streamTexto = JsfUtil.serializarPlano(datos);
		            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
		            		"RUA250NNPE"  + fecha +   "NI000800103923.txt");
		            
		        }
		        catch (NumberFormatException | JRException
		                        | IOException e) {
		            logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }
				
			//	return datos;

			}        catch (NumberFormatException | SystemException e) {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
			}
    	}else
    	{
			
			try {
				datos = ejbNominaSeis.generarPlanosPensionados(  
					 compania,
				     fechaInicial,
				     fechaFinal
				     );
		        try {
		            archivoDescarga = null;
		            ByteArrayInputStream streamTexto;
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		            String fecha = sdf.format(fechaFinal);
		            fecha = fecha.replaceAll("[/]", "");
		            
		            
		         

		            streamTexto = JsfUtil.serializarPlano(datos);
		            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
		            		"RUA250NMPP"  + fecha +   "NI000800103923.txt");
		            
		        }
		        catch (NumberFormatException | JRException
		                        | IOException e) {
		            logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }
				
			} catch (SystemException e) {
				e.printStackTrace();
			
			
		//	return datos;
			}
    	}//return datos;
    }
    
	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}
	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
    
}
