/*-
 * DiscoBancoAgrarioControlador.java
 *
 * 1.0
 * 
 * 19/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import java.util.Map;
import java.util.TreeMap;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDiezRemote;
import com.sysman.nomina.enums.DiscoBancoAgrarioControladorUrlEnum;
import com.sysman.nomina.enums.DiscoBancolombiaControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 19/10/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  DiscoBancoAgrarioControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String opcion;
	private String anio;
	private String mes;
	private String periodo;
	private String bancoOrigen;
	private String banco;
	private String nombreBancoOrigen;
	private Date fechaReporte;
	private String observaciones;
	private String lote;
	private String nombreBanco;

	@EJB
	public EjbNominaDiezRemote nominaDiezRemote;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos Plantilla y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivoPlantilla;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaanio;
	private List<Registro> listames;
	private List<Registro> listaperiodo;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listabancoOrigen;
	private RegistroDataModelImpl listabanco;
	private String proceso;
	private boolean todosLosBancos;
	private String tipoCuentaBanOrigen;
	private String cuentaBancoOrigen;
	CellStyle styleMoneda;
	CellStyle styleTextos;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DiscoBancoAgrarioControlador
	 */
	public DiscoBancoAgrarioControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2429;
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			anio = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			todosLosBancos = false;
			contArchivoPlantilla = new ContenedorArchivo();
			setOpcion("1");
			todosLosBancos = true;
			fechaReporte = new Date();
			observaciones = "PAGO NOMINA";
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
		cargarListaanio();
		cargarListames();
		cargarListaperiodo();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListabancoOrigen(); cargarListabanco();
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
	 * Carga la lista listaanio
	 *
	 */
	public void cargarListaanio(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		String urlEnumId = DiscoBancoAgrarioControladorUrlEnum.URL5135.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
		try {
			listaanio = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listames
	 *
	 */
	public void cargarListames(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		String urlEnumId = DiscoBancoAgrarioControladorUrlEnum.URL5500.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listames = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaperiodo
	 *
	 */
	public void cargarListaperiodo(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		String urlEnumId = DiscoBancoAgrarioControladorUrlEnum.URL5969.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listaperiodo = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listabancoOrigen
	 *
	 */
	public void cargarListabancoOrigen(){


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DiscoBancoAgrarioControladorUrlEnum.URL5136.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listabancoOrigen = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.BANCO.getName());
	}
	/**
	 * 
	 * Carga la lista listabanco
	 *
	 */
	public void cargarListabanco(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DiscoBancolombiaControladorUrlEnum.URL6365.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listabanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.BANCO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btEstructuraPab2015
	 * en la vista
	 *
	 *
	 */
	public void oprimirbtEstructuraPab2015() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		try {

			String datos = nominaDiezRemote.discoBancoAgrario(compania,Integer.parseInt(proceso), Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(periodo),
					SysmanFunciones.nvlStr(banco, " "), SysmanFunciones.convertirAFechaCadena(fechaReporte), todosLosBancos, observaciones, lote, 1,
					tipoCuentaBanOrigen, cuentaBancoOrigen);

			if(datos.isEmpty()) {
				
				JsfUtil.agregarMensajeError(
                        idioma.getString(
                                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
				
			}else {
			ByteArrayInputStream streamTexto;

			streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Pagos.fil.txt");
			}

		} catch (NumberFormatException | SystemException | JRException | IOException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btGenerarPlano
	 * en la vista
	 *
	 *
	 */
	public void oprimirbtGenerarPlano() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btEnviarExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirbtEnviarExcel() {
		//<CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;
			String datos = nominaDiezRemote.discoBancoAgrario(compania,Integer.parseInt(proceso), Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(periodo),
					SysmanFunciones.nvlStr(banco, " "), SysmanFunciones.convertirAFechaCadena(fechaReporte), todosLosBancos, observaciones, lote, 2,
					tipoCuentaBanOrigen, cuentaBancoOrigen);
			if(datos.isEmpty()) {
				JsfUtil.agregarMensajeError(
                        idioma.getString(
                                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
			}else {
			generarExcel(datos);
			}
			
		} catch (NumberFormatException | SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}       
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	public void generarExcel(String datos) {
		FileInputStream file = null;
		Workbook workbook = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			String archivo = String.valueOf(contArchivoPlantilla.getArchivo());
			String extension = archivo.substring(archivo.indexOf('.'), archivo.length()).toLowerCase();
			if (validarArchivo(extension)) {
				String rutaArchivo = contArchivoPlantilla.getArchivo().getPath();
				file = new FileInputStream(new File(rutaArchivo));

				if (".xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheetAt(0);

				String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
				String[] colum;

				if (registro.length == 0) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2514"));
					return;
				}
				Row fila;
				Cell celda;
				colum = registro[0].split(SysmanConstantes.SEPARADOR_COL);
				for (int i = 0; i < registro.length; i++) {
					fila = sheet.createRow(i + 1);
					colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
					for (int j = 0; j < colum.length; j++) {
						celda = fila.createCell(j);
						if (j == 6) {
							styleMoneda = workbook.createCellStyle();
							celda.setCellValue(Double.parseDouble(colum[j]));
							styleMoneda.setDataFormat(workbook.createDataFormat().getFormat("###0.00"));
							celda.setCellStyle(styleMoneda);

						}else if (j == 0 || j == 2 || j == 4){
							styleTextos = workbook.createCellStyle();
							celda.setCellValue(Long.parseLong(colum[j]));
							styleTextos.setAlignment(HorizontalAlignment.CENTER);
							celda.setCellStyle(styleTextos);

						}else if (j == 1 || j == 3){
							styleTextos = workbook.createCellStyle();
							celda.setCellValue(colum[j]);
							styleTextos.setAlignment(HorizontalAlignment.RIGHT);
							celda.setCellStyle(styleTextos);

						}else {
							celda.setCellValue(colum[j]);
							sheet.autoSizeColumn(j);
						}
					}
				}
				workbook.write(out);
				out.close();
				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
						"ARCHIVO INSCRIPCION Y DISPERSION FONDOS " + anio + "_" + mes + extension);
			}
		} catch (IOException | NumberFormatException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			try {
				if (file != null) {
					file.close();
				}
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	public boolean validarArchivo(String extension) {
		if (contArchivoPlantilla.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
				return true;
			} else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
				return false;
			}
		}
	}
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control anio
	 * 
	 * 
	 */
	public void cambiaranio() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control mes
	 * 
	 * 
	 */
	public void cambiarmes() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control TipoR
	 * 
	 * 
	 */
	public void cambiarTipoR() {
		//<CODIGO_DESARROLLADO>
		if (opcion.equals("1")) {
			todosLosBancos = true;
		} else {
			todosLosBancos = false;
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listabancoOrigen
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilabancoOrigen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bancoOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ").toString();
		nombreBancoOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();
		tipoCuentaBanOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("TIPO_CUENTA"), " ").toString();
		cuentaBancoOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("CUENTA"), "0").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listabanco
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilabanco(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ").toString();
		nombreBanco = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcion
	 * 
	 * @return  opcion
	 */
	public String getOpcion() {
		return opcion;
	}
	/**
	 * Asigna la variable  opcion
	 * 
	 * @param  opcion
	 * Variable a asignar en  opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
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
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable periodo
	 * 
	 * @return  periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * Asigna la variable  periodo
	 * 
	 * @param  periodo
	 * Variable a asignar en  periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	/**
	 * Retorna la variable bancoOrigen
	 * 
	 * @return  bancoOrigen
	 */
	public String getBancoOrigen() {
		return bancoOrigen;
	}
	/**
	 * Asigna la variable  bancoOrigen
	 * 
	 * @param  bancoOrigen
	 * Variable a asignar en  bancoOrigen
	 */
	public void setBancoOrigen(String bancoOrigen) {
		this.bancoOrigen = bancoOrigen;
	}
	/**
	 * Retorna la variable banco
	 * 
	 * @return  banco
	 */
	public String getBanco() {
		return banco;
	}
	/**
	 * Asigna la variable  banco
	 * 
	 * @param  banco
	 * Variable a asignar en  banco
	 */
	public void setBanco(String banco) {
		this.banco = banco;
	}
	/**
	 * Retorna la variable nombreBancoOrigen
	 * 
	 * @return  nombreBancoOrigen
	 */
	public String getNombreBancoOrigen() {
		return nombreBancoOrigen;
	}
	/**
	 * Asigna la variable  nombreBancoOrigen
	 * 
	 * @param  nombreBancoOrigen
	 * Variable a asignar en  nombreBancoOrigen
	 */
	public void setNombreBancoOrigen(String nombreBancoOrigen) {
		this.nombreBancoOrigen = nombreBancoOrigen;
	}
	/**
	 * Retorna la variable fechaReporte
	 * 
	 * @return  fechaReporte
	 */
	public Date getFechaReporte() {
		return fechaReporte;
	}
	/**
	 * Asigna la variable  fechaReporte
	 * 
	 * @param  fechaReporte
	 * Variable a asignar en  fechaReporte
	 */
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}
	/**
	 * Retorna la variable observaciones
	 * 
	 * @return  observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}
	/**
	 * Asigna la variable  observaciones
	 * 
	 * @param  observaciones
	 * Variable a asignar en  observaciones
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	/**
	 * Retorna la variable lote
	 * 
	 * @return  lote
	 */
	public String getLote() {
		return lote;
	}
	/**
	 * Asigna la variable  lote
	 * 
	 * @param  lote
	 * Variable a asignar en  lote
	 */
	public void setLote(String lote) {
		this.lote = lote;
	}
	/**
	 * Retorna la variable nombreBanco
	 * 
	 * @return  nombreBanco
	 */
	public String getNombreBanco() {
		return nombreBanco;
	}
	/**
	 * Asigna la variable  nombreBanco
	 * 
	 * @param  nombreBanco
	 * Variable a asignar en  nombreBanco
	 */
	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * Retorna el objeto contArchivoPlantilla
	 * 
	 * @return contArchivoPlantilla
	 */
	public ContenedorArchivo getContArchivoPlantilla() {
		return contArchivoPlantilla;
	}
	/**
	 * Asigna el objeto contArchivoPlantilla
	 * 
	 * @param contArchivoPlantilla
	 * Variable a asignar en contArchivoPlantilla
	 */
	public void setContArchivoPlantilla(ContenedorArchivo contArchivoPlantilla) {
		this.contArchivoPlantilla = contArchivoPlantilla;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaanio
	 * 
	 * @return listaanio
	 */
	public List<Registro> getListaanio() {
		return listaanio;
	}
	/**
	 * Asigna la lista listaanio
	 * 
	 * @param listaanio
	 * Variable a asignar en  listaanio
	 */
	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}
	/**
	 * Retorna la lista listames
	 * 
	 * @return listames
	 */
	public List<Registro> getListames() {
		return listames;
	}
	/**
	 * Asigna la lista listames
	 * 
	 * @param listames
	 * Variable a asignar en  listames
	 */
	public void setListames(List<Registro> listames) {
		this.listames = listames;
	}
	/**
	 * Retorna la lista listaperiodo
	 * 
	 * @return listaperiodo
	 */
	public List<Registro> getListaperiodo() {
		return listaperiodo;
	}
	/**
	 * Asigna la lista listaperiodo
	 * 
	 * @param listaperiodo
	 * Variable a asignar en  listaperiodo
	 */
	public void setListaperiodo(List<Registro> listaperiodo) {
		this.listaperiodo = listaperiodo;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listabancoOrigen
	 * 
	 * @return listabancoOrigen
	 */
	public RegistroDataModelImpl getListabancoOrigen() {
		return listabancoOrigen;
	}
	/**
	 * Asigna la lista listabancoOrigen
	 * 
	 * @param listabancoOrigen
	 * Variable a asignar en  listabancoOrigen
	 */
	public void setListabancoOrigen(RegistroDataModelImpl listabancoOrigen) {
		this.listabancoOrigen = listabancoOrigen;
	}
	/**
	 * Retorna la lista listabanco
	 * 
	 * @return listabanco
	 */
	public RegistroDataModelImpl getListabanco() {
		return listabanco;
	}
	/**
	 * Asigna la lista listabanco
	 * 
	 * @param listabanco
	 * Variable a asignar en  listabanco
	 */
	public void setListabanco(RegistroDataModelImpl listabanco) {
		this.listabanco = listabanco;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
