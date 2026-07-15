/*-
 * FrseguiminetocuatrenioControlador.java
 *
 * 1.0
 * 
 * 01/05/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.enums.FrseguiminetocuatrenioControladorEnum;
import com.sysman.plandesarrollo.enums.FrseguiminetocuatrenioControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 01/05/2021
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  FrseguiminetocuatrenioControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
//<DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacen el anio
     */
private String anio;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos selPlantilla y funciona como contenedor del archivo que se
     * debe guardar
     */
 private ContenedorArchivo contArchivoselPlantilla;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
    /**
     * listado variable de vigencia 
     */
private List<Registro> listavigencia;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrseguiminetocuatrenioControlador
     */
    public FrseguiminetocuatrenioControlador() {
  super();
            compania = SessionUtil.getCompania();           
            contArchivoselPlantilla = new ContenedorArchivo();
        try {
        //2272;		
   	numFormulario = GeneralCodigoFormaEnum.FRSEGUIMINETOCUATRENIOCONTROLADOR.getCodigo();
   	validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }finally{
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
		 cargarListavigencia();
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
        //</CODIGO_DESARROLLADO>
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listavigencia
     *
     */
public void cargarListavigencia(){
	
	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

	try {
		listavigencia = RegistroConverter.toListRegistro(requestManager.getList(
				UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrseguiminetocuatrenioControladorUrlEnum.URL001.getValue())
				.getUrl(),
				param));

	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}


}
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdExcel
     * en la vista
     *
     *
     */
public void oprimircmdExcel() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;            
         armarExcel(); 
 		//</CODIGO_DESARROLLADO>
 	}


 	private void armarExcel()  {

 		if (contArchivoselPlantilla.getArchivo() == null) {
 			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
 			return;
 		}
 		
 		int fila = 17;

 		try (FileInputStream fileIn = new FileInputStream(contArchivoselPlantilla.getArchivo())) {
 			
 	
 				Workbook workbook = new HSSFWorkbook(fileIn);
 				Sheet sheet = workbook.getSheet("Hoja1");
 								
 				Map<String, Object> reemplazos = new TreeMap<>();

 				 reemplazos.put("compania", compania);
 		            reemplazos.put("anio", anio);
 		            String strSql = Reporteador.resuelveConsulta(
 		                            "800433SeguimientoCuatrenio",
 		                            Integer.parseInt(SessionUtil.getModulo()),
 		                            reemplazos);
 		            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
 		                            strSql);

 				Row rowVigencia = sheet.getRow(14);
 				Cell cellvigencia = rowVigencia.getCell(2);
 				cellvigencia.setCellValue(anio);

 				for (Registro valor : rs) {

 					Row rowDatos = sheet.getRow(fila);


 					Cell cellConsecutivo = rowDatos.getCell(0);
 					cellConsecutivo.setCellValue(valor.getCampos().get(FrseguiminetocuatrenioControladorEnum.CONSECUTIVO.getValue()).toString());

 					Cell cellIniciativaEstrategica = rowDatos.getCell(1);
 					cellIniciativaEstrategica.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.INICIATIVA_ESTRATEGICA.getValue())," ").toString());

 					Cell cellSectorInversion = rowDatos.getCell(2);
 					cellSectorInversion.setCellValue(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.SECTOR_INVERSION.getValue()).toString());

 					Cell cellPrograma = rowDatos.getCell(3);
 					cellPrograma.setCellValue(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.PROGRAMA.getValue()).toString());

 					Cell cellMetaProducto = rowDatos.getCell(4);
 					cellMetaProducto.setCellValue(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.META_PRODUCTO.getValue()).toString());

 					Cell cellDependencia = rowDatos.getCell(5);
 					cellDependencia .setCellValue(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.DEPENDENCIA.getValue()).toString());

 					Cell cellDescripcion = rowDatos.getCell(6);
 					cellDescripcion.setCellValue(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.DESCRIPCION.getValue()).toString());

 					Cell cellMeta = rowDatos.getCell(7);
 					cellMeta.setCellValue(valor.getCampos().get(FrseguiminetocuatrenioControladorEnum.META
 							.getValue()).toString());

 					Cell cellMetaind = rowDatos.getCell(8);
 					cellMetaind.setCellValue(valor.getCampos().get(FrseguiminetocuatrenioControladorEnum.MET_IND
 							.getValue()).toString());


 					Cell cellMetaFisicaEsperada = rowDatos.getCell(9);
 					cellMetaFisicaEsperada.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.META_FISICA_ESPERADA.getValue()),0).toString());

 					Cell cellMetaFisicaEjecutada = rowDatos.getCell(10);
 					cellMetaFisicaEjecutada.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.META_FISICA_EJECUTADA.getValue()),0).toString());

 					Cell cellPorcentajeEjeucion = rowDatos.getCell(11);
 					cellPorcentajeEjeucion.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.PORCENTAJE_EJECUCION.getValue()),0).toString());

 					Cell cellMetaFinanciera = rowDatos.getCell(12);
 					cellMetaFinanciera.setCellValue(Double.parseDouble(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.META_FINANCIERA.getValue()), 0).toString()));

 					Cell cellComprometidoVigencia = rowDatos.getCell(13);
 					cellComprometidoVigencia.setCellValue(Double.parseDouble(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.COMPROMETIDO_VIGENCIA.getValue()), 0).toString()));

 					Cell cellAvanceComprometido = rowDatos.getCell(14);
 					cellAvanceComprometido.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.AVANCE_COMPROMETIDO.getValue()), 0).toString());


 					Cell cellPagadoVigencia = rowDatos.getCell(15);
 					cellPagadoVigencia.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.PAGADO_VIGENCIA.getValue()), 0).toString());


 					Cell cellAvancePagodo = rowDatos.getCell(16);
 					cellAvancePagodo.setCellValue(SysmanFunciones.nvl(valor.getCampos()
 							.get(FrseguiminetocuatrenioControladorEnum.AVANCE_PAGADO.getValue()), 0).toString());

 					fila++;

 				}

 				ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
 				workbook.write(fileOut);
 				fileOut.close();
 				fileIn.close();

 				ByteArrayInputStream excelSerializado = new ByteArrayInputStream(fileOut.toByteArray());


 				ByteArrayInputStream[] archivoSerial = { excelSerializado};




 				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(fileOut.toByteArray()),
 						"PLANTILLA INFORME CUATRENIO" + ".xls");
 			}
 	

 		catch (IOException | JRException e) {
             Logger.getLogger(FrseguiminetocuatrenioControlador.class.getName())
             .log(Level.SEVERE, null, e);
       JsfUtil.agregarMensajeError(e.getMessage());
 }
 	}

 	/**
 	 * 
 	 * Metodo que valida si el archivo seleccionado de la plantilla es valido.
 	 * 
 	 */


 	public boolean validarArchivo() {

 		String archivo = String.valueOf(contArchivoselPlantilla.getArchivo());
 		if (contArchivoselPlantilla.getArchivo() == null) {
 			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
 			return false;
 		}
 		else {
 			String extension = archivo
 					.substring(archivo.indexOf('.'), archivo.length())
 					.toLowerCase();
 			if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
 				return true;
 			}
 			else {
 				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
 				return false;
 			}
 		}
 	}
 	public void msgAlertaErrorArchivo() {
 		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4361"));
 	}

 	public void msgAlertaErrorArchivoValido() {
 		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4362"));
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
     * Retorna la variable anio
     * 
     * @return  anio
     */
public String getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    /**
     * Retorna el objeto contArchivoselPlantilla
     * 
     * @return contArchivoselPlantilla
     */
public ContenedorArchivo getContArchivoselPlantilla() {
        return contArchivoselPlantilla;
    }
    /**
     * Asigna el objeto contArchivoselPlantilla
     * 
     * @param contArchivoselPlantilla
     * Variable a asignar en contArchivoselPlantilla
     */
    public void setContArchivoselPlantilla(ContenedorArchivo contArchivoselPlantilla) {
        this.contArchivoselPlantilla = contArchivoselPlantilla;
    }
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
    /**
     * Retorna la lista listavigencia
     * 
     * @return listavigencia
     */
public List<Registro> getListavigencia() {
        return listavigencia;
    }
    /**
     * Asigna la lista listavigencia
     * 
     * @param listavigencia
     * Variable a asignar en  listavigencia
     */
public void setListavigencia(List<Registro> listavigencia) {
        this.listavigencia = listavigencia;
    }
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
