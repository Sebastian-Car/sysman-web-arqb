/*-
 * FrmimportarcodigosflujoefectivosControlador.java
 *
 * 1.0
 * 
 * 13/10/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;


/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 13/10/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmimportarcodigosflujoefectivosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;
	
	@EJB
    private EjbContabilidadUnoRemote ejbContabilidadUnoRemote;
	
	private StreamedContent archivoDescarga;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmimportarcodigosflujoefectivosControlador
	 */
	public FrmimportarcodigosflujoefectivosControlador() 
	{
		super();
		compania = SessionUtil.getCompania();
		try {
			// 2370;
			numFormulario = GeneralCodigoFormaEnum.FRM_IMPORTAR_CODIGOS_FLUJO_EFECTIVOS.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			contArchivocargarExcel = new ContenedorArchivo();
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
	 * Metodo ejecutado al oprimir el boton cargar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargar() 
	{
		FileInputStream file = null;	
		archivoDescarga = null;
		String mensaje = "";
        try {
            
                String rutaArchivo = contArchivocargarExcel.getArchivo()
                                .getPath();
                String extension = rutaArchivo
                                .substring(rutaArchivo.indexOf('.'),
                                                rutaArchivo.length())
                                .substring(1, rutaArchivo.substring(
                                                rutaArchivo.indexOf('.'),
                                                rutaArchivo.length()).length());
                file = new FileInputStream(new File(rutaArchivo));
                Workbook workbook = null;

                if (workbook == null) {
                    if ("xls".equals(extension)) {
                        workbook = new HSSFWorkbook(file);
                    }
                    else {
                        workbook = new XSSFWorkbook(file);
                    }   
                }

                StringBuilder cadena = new StringBuilder();
                cadena.append("TO_CLOB('");
                Sheet sheet = workbook.getSheetAt(0);
                Row fila;
                Cell celda;
                int num = 0;
                for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                    fila = sheet.getRow(i);
                    for (int j = 0; j < 9; j++) {
                        celda = fila.getCell(j);
                        if (celda != null) {
                            num = num
                                + (celda.getCellType() == 1 ? celda.getStringCellValue()
                                                .replaceFirst("'", " ").length()
                                    : NumberToTextConverter
                                                    .toText(celda.getNumericCellValue())
                                                    .length());
                            cadena.append(celda.getCellType() == 1
                                ? celda.getStringCellValue().replaceFirst("'", " ")
                                : NumberToTextConverter
                                                .toText(celda.getNumericCellValue()));                            
                        }
                        else {
                            cadena.append("");
                        }
                        if (num >= 10000) {
                            cadena.append("') || TO_CLOB('");
                            num = 0;
                        }
                        cadena.append(SysmanConstantes.SEPARADOR_COL);
                    }
                    cadena.append(SysmanConstantes.SEPARADOR_REG);
                }
                cadena.append("')"
                    + "");   
                
                String cadenaCodigos  = (SysmanFunciones.esBdSqlServer())
                        ? cadena.toString().replace("TO_CLOB(", "")
                                .replace(")", "")
                : cadena.toString();
				workbook.close();
				
			String retorno = ejbContabilidadUnoRemote.cargarFlujoEfectivo(compania, cadenaCodigos,
					SessionUtil.getUser().getCodigo());
			
			retorno = retorno.replace("/n", System.getProperty("line.separator"));
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4444"));
			
			
			archivoDescarga = JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(retorno),
					SysmanFunciones.concatenar("Reporte de Carga", ".txt"));
			
			
		} catch (IOException | NumberFormatException | SystemException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
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
