/*-
 * PlanoXlsDpjControlador.java
 *
 * 1.0
 * 
 * 22/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ConciliacionPorPlanoTipoControlador;
import com.sysman.contabilidad.ejb.EjbContabilidadCpteGeneralRemote;
import com.sysman.contabilizar.ejb.EjbContabilizarCeroRemote;
import com.sysman.contabilizar.enums.PlanoXlsDpjControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 22/05/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  PlanoXlsDpjControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	private final String usuario;
	//<DECLARAR_ATRIBUTOS>
	private boolean ckTercero;
	private String ckRetenciones;
	private String cuenta;
	private String descripcion;
	private String tipoCpte;
	private String baseGravable;
	private String nroDocumento;
	private String tercero;
	private String sucursal;
	private String centroCosto;
	private String auxiliar;
	private String texto;
	private String contrato;
	private String auxiliarContra;
	private String tipoContrato;
	private String tipoPagoSia;
	private String fuenteSia;
	private String compAfectarCont;
	private String numCompAfect;
	private String tipoCompAfect;
	private String numCompPtal;
	private String valorDebito;
	private String valorCredito;
	private String vigenciaPptal;
	private String nroProceso;
	private int anio;
	private boolean agrupado;
	
	private StreamedContent archivoDescarga;
	
	private Workbook workbook;

	private ContenedorArchivo contArchivoExcel;
	
	@EJB
	private EjbContabilidadCpteGeneralRemote ejbContabilidadCpte;
	
	@EJB
	private EjbContabilizarCeroRemote contabilizarCeroRemote;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private RegistroDataModelImpl listaauxiliarcontra;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTipoCpte;
	private String fecha;
	private String columnaError;
	private int filaIni;
	private int filaFin;
	private boolean isTexto;
	private BigInteger comprobante;
	private String campoTexto;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de PlanoXlsDpjControlador
	 */
	public PlanoXlsDpjControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario=2465;
			validarPermisos();
			tipoCpte = "CPJ";
			anio = SysmanFunciones.ano(new Date());
			fecha = "A";
			cuenta = "B";
			descripcion = "C";
			texto = "D";
			baseGravable = "E";
			contrato = "F";
			nroDocumento = "G";
			tercero = "H";
			sucursal = "I";
			centroCosto = "J";
			auxiliar = "K";
			tipoContrato = "L";
			tipoPagoSia = "M";
			fuenteSia = "N";
			tipoCompAfect = "Q";
			numCompAfect = "P";
			vigenciaPptal = "S";
			valorDebito = "T";
			valorCredito = "U";
			nroProceso = "V";
			numCompPtal = "R";
			compAfectarCont = "O";
			columnaError = "Z";
			filaIni = 2;
			filaFin = 3;
			campoTexto = "Interface por archivo XLS.";

			contArchivoExcel = new ContenedorArchivo();

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
		cargarListaauxiliarcontra();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTipoCpte();
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
		comprobante = generarConsecutivo();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaauxiliarcontra
	 *
	 */
	public void cargarListaauxiliarcontra(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date())); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanoXlsDpjControladorUrlEnum.URL0002.getValue());

		listaauxiliarcontra = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaTipoCpte
	 *
	 */
	public void cargarListaTipoCpte(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlanoXlsDpjControladorUrlEnum.URL0001.getValue());

		listaTipoCpte = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Iniciar
	 * en la vista
	 *
	 *
	 */
	public void oprimirIniciar() {
		//<CODIGO_DESARROLLADO>

		try {
			archivoDescarga = null;
			InputStream is;
			workbook = null;
			String archivo = String.valueOf(contArchivoExcel.getArchivo());
			
			if (SysmanFunciones.validarVariableVacio(archivo) || archivo.equals("null")) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
				return;
			}
			//String extension = archivo.substring(archivo.indexOf('.'), archivo.length()).toLowerCase();
//			if (cuentaContra.isEmpty()) {
//				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4358"));
//				return;
//			}

			if(texto.isEmpty()) {

				texto = ".";

			}
            
			String rutaArchivo = contArchivoExcel.getArchivo().getPath();
			String extension = rutaArchivo
					.substring(rutaArchivo.indexOf('.'),
							rutaArchivo.length())
					.substring(1, rutaArchivo.substring(
							rutaArchivo.indexOf('.'),
							rutaArchivo.length()).length());

			is = new FileInputStream(new File(rutaArchivo));
			

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(is);
					
				}
				else {
					workbook = new XSSFWorkbook(is);
				}


			Sheet sheet = workbook.getSheetAt(0);
			isTexto = false;
			//crearEstilos(workbook);
			String respuesta = null;

//			int filaInicial = filaIni;
//			int filaFinal = 0;
//			while (filaInicial <= filaFin) {
//				filaFinal = filaInicial + 999;
//				filaFinal = (filaFinal > filaFin ? filaFin : filaFinal);
				String cadena = "TO_CLOB('"
						+ exportarExcelToCsv(workbook, filaIni - 1,
								filaFin - 1)
						+ "')";

				respuesta = ejecutarFuncPlanoXls(cadena, filaIni);
				
				
				workbook.close();

				if(!respuesta.isEmpty()) {
				
				archivoDescarga = JsfUtil.getArchivoDescarga(
                        JsfUtil.serializarPlano(respuesta),
                        "InconsistenciasProceso.txt");
				
				}
		} catch (IOException | JRException  e) {
			logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton archivo
	 * en la vista
	 *
	 *
	 */
	public void oprimirarchivo() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CodificarRetenciones
	 * en la vista
	 *
	 *
	 */
	public void oprimirCodificarRetenciones() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	
	
	public String exportarExcelToCsv(Workbook workbook, int filainicial,
			int filafinal) {
		StringBuilder data = new StringBuilder();
		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(1);
		int num = 0;

		for (int rowNum = filainicial; rowNum <= filafinal; rowNum++) {
			row = sheet.getRow(rowNum);
			if (row == null) {
				// This whole row is empty
				// Handle it as needed
				continue;
			}
			num = num + 1;

			data.append(asignaDato(row, fecha, "D"));
			data.append(asignaDato(row, cuenta , "S"));
			data.append(asignaDato(row, descripcion, "S"));
			data.append(asignaDato(row, texto, "S"));
			data.append(asignaDato(row, baseGravable, "N"));
			data.append(asignaDato(row, contrato, "S"));
			data.append(asignaDato(row, nroDocumento, "S"));
			data.append(asignaDato(row, tercero, "S"));
			data.append(asignaDato(row, sucursal, "S"));
			data.append(asignaDato(row, centroCosto, "S"));
			data.append(asignaDato(row, auxiliar, "S"));
			data.append(asignaDato(row, tipoContrato, "S"));
			data.append(SysmanFunciones
					.nvlStr(asignaDato(row, valorDebito, "N"), "0"));
			data.append(SysmanFunciones
					.nvlStr(asignaDato(row, valorCredito, "N"), "0"));
			data.append(asignaDato(row, nroProceso, "S"));
			data.append("#");

			if (num >= 100) {
				data.append("') || TO_CLOB('");
			}
		}
		return data.toString();

	}
	
	private String asignaDato(Row row, String columna, String tipo) {
		String salida = "";
		Cell cell = row.getCell(columna.charAt(0) - 65,
				Row.RETURN_BLANK_AS_NULL);
		if (cell == null) {
			salida = ";";
		}
		else {
			isTexto = true;
			salida = asignarFormatoValor(cell, tipo) + ";";
		}
		return salida;
	}
	
	private Object asignarFormatoValor(Cell cell, String tipo) {
		Object object = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				object = cell.getRichStringCellValue().getString();
				object = object.toString().replace(",", "");
				object = object.toString().replace(";", "CHR(59)");
				object = object.toString().replace("#", "CHR(35)");
				break;
			case Cell.CELL_TYPE_NUMERIC:
				object = definirValor(cell, tipo);
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				object = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				object = cell.getCellFormula();
				break;
			case Cell.CELL_TYPE_BLANK:
				object = cell;
				break;
			default:
				object = null;
				break;
			}
		}
		else {
			logger.debug("La celda viene nula");
		}
		return object;
	}
	
	private Object definirValor(Cell cell, String tipo) {
		Object object;

		if (DateUtil.isCellDateFormatted(cell)) {
			object = cell.getDateCellValue();
			if (isTexto) {
				try {
					String fechaC = SysmanFunciones
							.convertirAFechaCadena((Date) object);
					object = fechaC;
				}
				catch (ParseException e) {
					Logger.getLogger(
							ConciliacionPorPlanoTipoControlador.class
							.getName())
					.log(Level.SEVERE, null, e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
			}
			isTexto = false;
		}
		else {
			if ("N".equals(tipo)) {
				object = cell.getNumericCellValue();
			}
			else {
				DataFormatter formatter = new DataFormatter();
				object = formatter.formatCellValue(cell);
			}

		}
		return object;
	}
	
	private String ejecutarFuncPlanoXls(String cadena, int filaInicial) {
		String respuesta = null;
		try {
			respuesta = contabilizarCeroRemote.cargarProcesoJudicialporXls(compania, 
					String.valueOf(anio), tipoCpte, String.valueOf(comprobante), cadena, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 11, 12, 0, 0, 13, 14, 15, ckTercero, agrupado, false, Integer.parseInt(modulo), filaInicial, campoTexto, usuario);
			
	
			 JsfUtil.agregarMensajeInformativo(
                     idioma.getString("MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e) {
			Logger.getLogger(
					ConciliacionPorPlanoTipoControlador.class.getName())
			.log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoCpte
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoCpte(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoCpte= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		
		comprobante = generarConsecutivo();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaauxiliarcontra
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaauxiliarcontra(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarContra= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	
	public BigInteger generarConsecutivo() {
		BigInteger consecutivo = null;
		try {
			consecutivo = ejbContabilidadCpte.enumerarComprobanteCnt(compania, anio, tipoCpte,
					BigInteger.ZERO,
					"");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return consecutivo;
	}
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckTercero
	 * 
	 * @return  ckTercero
	 */
	public boolean getCkTercero() {
		return ckTercero;
	}
	/**
	 * Asigna la variable  ckTercero
	 * 
	 * @param  ckTercero
	 * Variable a asignar en  ckTercero
	 */
	public void setCkTercero(boolean ckTercero) {
		this.ckTercero = ckTercero;
	}
	/**
	 * Retorna la variable ckRetenciones
	 * 
	 * @return  ckRetenciones
	 */
	public String getCkRetenciones() {
		return ckRetenciones;
	}
	/**
	 * Asigna la variable  ckRetenciones
	 * 
	 * @param  ckRetenciones
	 * Variable a asignar en  ckRetenciones
	 */
	public void setCkRetenciones(String ckRetenciones) {
		this.ckRetenciones = ckRetenciones;
	}
	/**
	 * Retorna la variable cuenta
	 * 
	 * @return  cuenta
	 */
	public String getCuenta() {
		return cuenta;
	}
	/**
	 * Asigna la variable  cuenta
	 * 
	 * @param  cuenta
	 * Variable a asignar en  cuenta
	 */
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	/**
	 * Retorna la variable descripcion
	 * 
	 * @return  descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}
	/**
	 * Asigna la variable  descripcion
	 * 
	 * @param  descripcion
	 * Variable a asignar en  descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	/**
	 * Retorna la variable tipoCpte
	 * 
	 * @return  tipoCpte
	 */
	public String getTipoCpte() {
		return tipoCpte;
	}
	/**
	 * Asigna la variable  tipoCpte
	 * 
	 * @param  tipoCpte
	 * Variable a asignar en  tipoCpte
	 */
	public void setTipoCpte(String tipoCpte) {
		this.tipoCpte = tipoCpte;
	}
	/**
	 * Retorna la variable baseGravable
	 * 
	 * @return  baseGravable
	 */
	public String getBaseGravable() {
		return baseGravable;
	}
	/**
	 * Asigna la variable  baseGravable
	 * 
	 * @param  baseGravable
	 * Variable a asignar en  baseGravable
	 */
	public void setBaseGravable(String baseGravable) {
		this.baseGravable = baseGravable;
	}
	/**
	 * Retorna la variable nroDocumento
	 * 
	 * @return  nroDocumento
	 */
	public String getNroDocumento() {
		return nroDocumento;
	}
	/**
	 * Asigna la variable  nroDocumento
	 * 
	 * @param  nroDocumento
	 * Variable a asignar en  nroDocumento
	 */
	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}
	/**
	 * Retorna la variable tercero
	 * 
	 * @return  tercero
	 */
	public String getTercero() {
		return tercero;
	}
	/**
	 * Asigna la variable  tercero
	 * 
	 * @param  tercero
	 * Variable a asignar en  tercero
	 */
	public void setTercero(String tercero) {
		this.tercero = tercero;
	}
	/**
	 * Retorna la variable sucursal
	 * 
	 * @return  sucursal
	 */
	public String getSucursal() {
		return sucursal;
	}
	/**
	 * Asigna la variable  sucursal
	 * 
	 * @param  sucursal
	 * Variable a asignar en  sucursal
	 */
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	/**
	 * Retorna la variable centroCosto
	 * 
	 * @return  centroCosto
	 */
	public String getCentroCosto() {
		return centroCosto;
	}
	/**
	 * Asigna la variable  centroCosto
	 * 
	 * @param  centroCosto
	 * Variable a asignar en  centroCosto
	 */
	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return  auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * Asigna la variable  auxiliar
	 * 
	 * @param  auxiliar
	 * Variable a asignar en  auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
	/**
	 * Retorna la variable texto
	 * 
	 * @return  texto
	 */
	public String getTexto() {
		return texto;
	}
	/**
	 * Asigna la variable  texto
	 * 
	 * @param  texto
	 * Variable a asignar en  texto
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}
	/**
	 * Retorna la variable contrato
	 * 
	 * @return  contrato
	 */
	public String getContrato() {
		return contrato;
	}
	/**
	 * Asigna la variable  contrato
	 * 
	 * @param  contrato
	 * Variable a asignar en  contrato
	 */
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	/**
	 * Retorna la variable auxiliarContra
	 * 
	 * @return  auxiliarContra
	 */
	public String getAuxiliarContra() {
		return auxiliarContra;
	}
	/**
	 * Asigna la variable  auxiliarContra
	 * 
	 * @param  auxiliarContra
	 * Variable a asignar en  auxiliarContra
	 */
	public void setAuxiliarContra(String auxiliarContra) {
		this.auxiliarContra = auxiliarContra;
	}
	/**
	 * Retorna la variable tipoContrato
	 * 
	 * @return  tipoContrato
	 */
	public String getTipoContrato() {
		return tipoContrato;
	}
	/**
	 * Asigna la variable  tipoContrato
	 * 
	 * @param  tipoContrato
	 * Variable a asignar en  tipoContrato
	 */
	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}
	/**
	 * Retorna la variable tipoPagoSia
	 * 
	 * @return  tipoPagoSia
	 */
	public String getTipoPagoSia() {
		return tipoPagoSia;
	}
	/**
	 * Asigna la variable  tipoPagoSia
	 * 
	 * @param  tipoPagoSia
	 * Variable a asignar en  tipoPagoSia
	 */
	public void setTipoPagoSia(String tipoPagoSia) {
		this.tipoPagoSia = tipoPagoSia;
	}
	/**
	 * Retorna la variable fuenteSia
	 * 
	 * @return  fuenteSia
	 */
	public String getFuenteSia() {
		return fuenteSia;
	}
	/**
	 * Asigna la variable  fuenteSia
	 * 
	 * @param  fuenteSia
	 * Variable a asignar en  fuenteSia
	 */
	public void setFuenteSia(String fuenteSia) {
		this.fuenteSia = fuenteSia;
	}
	/**
	 * Retorna la variable compAfectarCont
	 * 
	 * @return  compAfectarCont
	 */
	public String getCompAfectarCont() {
		return compAfectarCont;
	}
	/**
	 * Asigna la variable  compAfectarCont
	 * 
	 * @param  compAfectarCont
	 * Variable a asignar en  compAfectarCont
	 */
	public void setCompAfectarCont(String compAfectarCont) {
		this.compAfectarCont = compAfectarCont;
	}
	/**
	 * Retorna la variable numCompAfect
	 * 
	 * @return  numCompAfect
	 */
	public String getNumCompAfect() {
		return numCompAfect;
	}
	/**
	 * Asigna la variable  numCompAfect
	 * 
	 * @param  numCompAfect
	 * Variable a asignar en  numCompAfect
	 */
	public void setNumCompAfect(String numCompAfect) {
		this.numCompAfect = numCompAfect;
	}
	/**
	 * Retorna la variable tipoCompAfect
	 * 
	 * @return  tipoCompAfect
	 */
	public String getTipoCompAfect() {
		return tipoCompAfect;
	}
	/**
	 * Asigna la variable  tipoCompAfect
	 * 
	 * @param  tipoCompAfect
	 * Variable a asignar en  tipoCompAfect
	 */
	public void setTipoCompAfect(String tipoCompAfect) {
		this.tipoCompAfect = tipoCompAfect;
	}
	/**
	 * Retorna la variable numCompPtal
	 * 
	 * @return  numCompPtal
	 */
	public String getNumCompPtal() {
		return numCompPtal;
	}
	/**
	 * Asigna la variable  numCompPtal
	 * 
	 * @param  numCompPtal
	 * Variable a asignar en  numCompPtal
	 */
	public void setNumCompPtal(String numCompPtal) {
		this.numCompPtal = numCompPtal;
	}
	/**
	 * Retorna la variable valorDebito
	 * 
	 * @return  valorDebito
	 */
	public String getValorDebito() {
		return valorDebito;
	}
	/**
	 * Asigna la variable  valorDebito
	 * 
	 * @param  valorDebito
	 * Variable a asignar en  valorDebito
	 */
	public void setValorDebito(String valorDebito) {
		this.valorDebito = valorDebito;
	}
	/**
	 * Retorna la variable valorCredito
	 * 
	 * @return  valorCredito
	 */
	public String getValorCredito() {
		return valorCredito;
	}
	/**
	 * Asigna la variable  valorCredito
	 * 
	 * @param  valorCredito
	 * Variable a asignar en  valorCredito
	 */
	public void setValorCredito(String valorCredito) {
		this.valorCredito = valorCredito;
	}
	/**
	 * Retorna la variable vigenciaPptal
	 * 
	 * @return  vigenciaPptal
	 */
	public String getVigenciaPptal() {
		return vigenciaPptal;
	}
	/**
	 * Asigna la variable  vigenciaPptal
	 * 
	 * @param  vigenciaPptal
	 * Variable a asignar en  vigenciaPptal
	 */
	public void setVigenciaPptal(String vigenciaPptal) {
		this.vigenciaPptal = vigenciaPptal;
	}
	/**
	 * Retorna la variable nroProceso
	 * 
	 * @return  nroProceso
	 */
	public String getNroProceso() {
		return nroProceso;
	}
	/**
	 * Asigna la variable  nroProceso
	 * 
	 * @param  nroProceso
	 * Variable a asignar en  nroProceso
	 */
	public void setNroProceso(String nroProceso) {
		this.nroProceso = nroProceso;
	}
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public int getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipoCpte
	 * 
	 * @return listaTipoCpte
	 */
	public RegistroDataModelImpl getListaTipoCpte() {
		return listaTipoCpte;
	}
	/**
	 * Asigna la lista listaTipoCpte
	 * 
	 * @param listaTipoCpte
	 * Variable a asignar en  listaTipoCpte
	 */
	public void setListaTipoCpte(RegistroDataModelImpl listaTipoCpte) {
		this.listaTipoCpte = listaTipoCpte;
	}


	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	/**
	 * @return the listaauxiliarcontra
	 */
	public RegistroDataModelImpl getListaauxiliarcontra() {
		return listaauxiliarcontra;
	}
	/**
	 * @param listaauxiliarcontra the listaauxiliarcontra to set
	 */
	public void setListaauxiliarcontra(RegistroDataModelImpl listaauxiliarcontra) {
		this.listaauxiliarcontra = listaauxiliarcontra;
	}
	/**
	 * @return the contArchivoExcel
	 */
	public ContenedorArchivo getContArchivoExcel() {
		return contArchivoExcel;
	}
	/**
	 * @param contArchivoExcel the contArchivoExcel to set
	 */
	public void setContArchivoExcel(ContenedorArchivo contArchivoExcel) {
		this.contArchivoExcel = contArchivoExcel;
	}
	/**
	 * @return the columnaError
	 */
	public String getColumnaError() {
		return columnaError;
	}
	/**
	 * @param columnaError the columnaError to set
	 */
	public void setColumnaError(String columnaError) {
		this.columnaError = columnaError;
	}
	/**
	 * @return the filaIni
	 */
	public int getFilaIni() {
		return filaIni;
	}
	/**
	 * @param filaIni the filaIni to set
	 */
	public void setFilaIni(int filaIni) {
		this.filaIni = filaIni;
	}
	/**
	 * @return the filaFin
	 */
	public int getFilaFin() {
		return filaFin;
	}
	/**
	 * @param filaFin the filaFin to set
	 */
	public void setFilaFin(int filaFin) {
		this.filaFin = filaFin;
	}
	/**
	 * @return the comprobante
	 */
	public BigInteger getComprobante() {
		return comprobante;
	}
	/**
	 * @param comprobante the comprobante to set
	 */
	public void setComprobante(BigInteger comprobante) {
		this.comprobante = comprobante;
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
	/**
	 * @return the campoTexto
	 */
	public String getCampoTexto() {
		return campoTexto;
	}
	/**
	 * @param campoTexto the campoTexto to set
	 */
	public void setCampoTexto(String campoTexto) {
		this.campoTexto = campoTexto;
	}
	/**
	 * @return the agrupado
	 */
	public boolean isAgrupado() {
		return agrupado;
	}
	/**
	 * @param agrupado the agrupado to set
	 */
	public void setAgrupado(boolean agrupado) {
		this.agrupado = agrupado;
	}
	

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
