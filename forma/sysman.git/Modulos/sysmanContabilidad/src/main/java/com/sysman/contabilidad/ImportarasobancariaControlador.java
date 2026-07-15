/*-
 * ImportarasobancariaControlador.java
 *
 * 1.0
 * 
 * 24/07/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadTres;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.FileUploadEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 24/07/2021
 * @author sdaza
 */
@ManagedBean
@ViewScoped
public class  ImportarasobancariaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private BufferedReader brArchivo;
	private String ruta;
	private long longitudArchivo;
	private String lineaArchivo;
	//private String archivoErrores;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	private int linea;
	@EJB
	private EjbContabilidadTresRemote ejbContabilidadTresRemote;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ImportarasobancariaControlador
	 */
	public ImportarasobancariaControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.IMPORTARASOBANCARIACT_CONTROLADOR.getCodigo();
			validarPermisos();
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
			SessionUtil.cleanFlash();
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
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
FR2279-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton registrarPagos
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirregistrarPagos() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     

		linea = 0;
		ByteArrayInputStream streamResultado;
		String salida = null;
		StringBuilder textoArchivo = new StringBuilder();
		try
		{
			
			if (brArchivo == null )
			{
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1962"));
			}
			else
			{
				textoArchivo = archivoConSeparador(textoArchivo);
				textoArchivo.append("RT");
				textoArchivo.append(ruta);
				textoArchivo.append("@");
					
				String planoClob = Acciones
					.getClobConcatenado(textoArchivo.toString());
			
			
				String ultimoCaracter = planoClob.substring(planoClob.length() - 1);
				if (",".equals(ultimoCaracter))
				{
					planoClob = planoClob.substring(0, planoClob.length() - 1);
				}
				salida = ejbContabilidadTresRemote
						.asobancariaImportarCT(compania,
				SessionUtil.getUser().getCodigo(),
								planoClob);
				
				streamResultado = JsfUtil.serializarPlano(salida);

	            archivoDescarga = JsfUtil.getArchivoDescarga(streamResultado,
	                            "Log_" + ruta);
					
	            streamResultado.close();	
				
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>


	public StringBuilder archivoConSeparador(StringBuilder textoArchivo)
	{

		try
		{
			while ((lineaArchivo = brArchivo.readLine()) != null)
			{
				textoArchivo.append(lineaArchivo);
				if(linea == (longitudArchivo / 162)) {
					textoArchivo.append("@");
				}
				if (linea != (longitudArchivo / 162))
				{
					textoArchivo.append("@");
				}
				linea++;
			}			
			brArchivo.close();
			brArchivo = null; 
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return textoArchivo;
	}
	//<METODOS_CAMBIAR>
	/**
	 * 
	 * Metodo ejecutado al cargar un archivo desde el control lectorArchivo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 *
	 */
	public void cargarArchivolectorArchivo(FileUploadEvent event){
		
		ruta = event.getFile().getFileName();
		longitudArchivo = event.getFile().getSize();
		String formato = (ruta.substring(ruta.length() - 4, ruta.length())).toUpperCase();
		
		try
		{
			if (!".TXT".equals(formato))
			{
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3813"));
			}
			else
			{
				//ruta = event.getFile().getFileName();
				brArchivo = new BufferedReader(new InputStreamReader(
						event.getFile().getInputstream()));
			}

		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
