/*-
 * FrmconvertirxmlControlador.java
 *
 * 1.0
 * 
 * 05/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgroups.demos.wb.Node;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.ContenedorArchivo;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/06/2023
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class FrmconvertirxmlControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmconvertirxmlControlador
	 */
	public FrmconvertirxmlControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_CONVERTIRXMLATEB.getCodigo();//2409
			validarPermisos();

			contArchivocargarExcel = new ContenedorArchivo();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargar() {
		// <CODIGO_DESARROLLADO>
		//se toma el archivo selecionado
		try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo());) {
			//se factoriza hasta ser un tipo documento XML, en el cual se vera su contenuido de una forma ordenada
			BufferedReader reader = new BufferedReader(new FileReader(contArchivocargarExcel.getArchivo()));
			StringBuilder cadenaRecepcion = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null ) {
				if(line.contains("xmlns:schemaLocation")) {
					line = line.replace("xmlns:schemaLocation", "xsi:schemaLocation");
				}
				cadenaRecepcion.append(line+"\n");
			}

			//se procesde a recar el unevo archivo para descargar
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			out.write(cadenaRecepcion.toString().getBytes());
			// se agrega el content type text/xml para que retome los valores del formato
			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					contArchivocargarExcel.getArchivo().getName(),"text/xml","UTF-8");

		} catch (IOException | JRException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna el objeto contArchivocargarExcel
	 * 
	 * @return contArchivocargarExcel
	 */
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	/**
	 * Asigna el objeto contArchivocargarExcel
	 * 
	 * @param contArchivocargarExcel Variable a asignar en contArchivocargarExcel
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
