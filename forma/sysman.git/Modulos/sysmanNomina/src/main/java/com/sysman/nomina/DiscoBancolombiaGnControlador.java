
/*-
 * DiscoBancolombiaControlador.java
 *
 * 1.0
 * 
 * 30/08/2018
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
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.DiscoBancolombiaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 30/08/2018
 * @author apineda
 */
@ManagedBean
@ViewScoped

public class DiscoBancolombiaGnControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Indica si se debe consultar teniendo en cuenta todos los bancos o un solo
	 * banco
	 */
	private String opcion;
	/**
	 * Año seleccionado
	 */
	private String anio;
	/**
	 * Mes seleccionado
	 */
	private String mes;
	/**
	 * Periodo seleccionado
	 */
	private String periodo;
	/**
	 * Banco de destino seleccionado
	 */
	private String banco;
	/**
	 * Banco origen
	 */
	private String bancoOrigen;
	/**
	 * Fecha de generación del plano
	 */
	private Date fechaReporte;
	/**
	 * Observaciones del usuario
	 */
	private String observaciones;
	/**
	 * Identificador de lote
	 */
	private String lote;
	/**
	 * Identificador de lote
	 */
	private String desPago;
	/**
	 * Identificador de lote
	 */
	private String referencia;
	/**
	 * Proceso de nómina con el que se desea generar el archivo.
	 */
	private String proceso;
	/**
	 * Nombre del banco origen seleccionado
	 */
	private String nombreBancoOrigen;

	/**
	 * Nombre del banco seleccionado
	 */
	private String nombreBanco;
	/**
	 * Indicador para consultar incluyendo todos los bancos o solo el seleccionado
	 */
	private boolean todosLosBancos;

	/**
	 * Tipo cuenta banco origen
	 */
	private String tipoCuentaBanOrigen;
	/**
	 * Cuenta banco origen
	 */
	private String cuentaBancoOrigen;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * archivo y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivoPlantilla;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Listado de años
	 */
	private List<Registro> listacbAnio;
	/**
	 * Listado de meses
	 */
	private List<Registro> listacbMes;
	/**
	 * Listado de periodos
	 */
	private List<Registro> listacbPeriodo;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Listado de bancos
	 */
	private RegistroDataModelImpl listacbBanco;

	/**
	 * Listado banco origen
	 */
	private RegistroDataModelImpl listacbBancoOrigen;


	private String modulo;
	
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de DiscoBancolombiaControlador
	 */
	public DiscoBancolombiaGnControlador() {
		super();
		compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.BANCOLOMBIA_CONTROLADOR.getCodigo();
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			anio = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			todosLosBancos = false;
			validarPermisos();
			fechaReporte = new Date();
			contArchivoPlantilla = new ContenedorArchivo();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
		// <CARGAR_LISTA>
		cargarListacbAnio();
		cargarListacbMes();
		cargarListacbPeriodo();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListacbBanco();
		cargarListacbBancoOrigen();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
	
		String paramidi;
		try {
			
			paramidi = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"ENCABEZADO PLANO BANCOLOMBIA IDIPRON", modulo, new Date(), false), "NO");

			if(paramidi.equalsIgnoreCase("NO")) {
				observaciones = "NOM"+((SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt (mes)]).toUpperCase()).substring(0, 3)+anio.substring(2);
				lote = "AA";
				}
				else {
				observaciones = "PG. NOMINA";
				lote = "A";
				}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		// </CODIGO_DESARROLLADO>
	}

	public String getDesPago() {
		return desPago;
	}

	public void setDesPago(String desPago) {
		this.desPago = desPago;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacbAnio
	 * 
	 */
	public void cargarListacbAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		String urlEnumId = DiscoBancolombiaControladorUrlEnum.URL5135.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
		try {
			listacbAnio = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacbMes
	 * 
	 */
	public void cargarListacbMes() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		String urlEnumId = DiscoBancolombiaControladorUrlEnum.URL5500.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listacbMes = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacbPeriodo
	 *
	 */
	public void cargarListacbPeriodo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		String urlEnumId = DiscoBancolombiaControladorUrlEnum.URL5969.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listacbPeriodo = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacbBanco
	 *
	 */
	public void cargarListacbBanco() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DiscoBancolombiaControladorUrlEnum.URL6365.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacbBanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.BANCO.getName());
	}

	/**
	 * 
	 * Carga la lista listacbBancoOrigen
	 * 
	 */
	public void cargarListacbBancoOrigen() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DiscoBancolombiaControladorUrlEnum.URL5136.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacbBancoOrigen = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.BANCO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btEstructuraPab2015 en la vista
	 *
	 *
	 */
	public void oprimirbtEstructuraPab2015() {
		// <CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;
			String datos = ejbNominaCuatro.generarDiscoBancoColombia(compania, Integer.parseInt(proceso),
					Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(periodo),
					SysmanFunciones.nvlStr(banco, " "), fechaReporte, todosLosBancos, observaciones, lote, 1,
					tipoCuentaBanOrigen, cuentaBancoOrigen);
			ByteArrayInputStream streamTexto;

			streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Pagos.fil.txt");
		} catch (NumberFormatException | SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btEnviarExcel en la vista
	 *
	 *
	 */
	public void oprimirbtEnviarExcel() {
		// <CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;
			String datos = ejbNominaCuatro.generarDiscoBancoColombiaGn(compania, Integer.parseInt(proceso),
					Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(periodo),
					SysmanFunciones.nvlStr(banco, " "), fechaReporte, todosLosBancos, observaciones, lote,desPago,referencia, 2,
					tipoCuentaBanOrigen, cuentaBancoOrigen);
			generarExcel(datos);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

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
				Sheet sheet = workbook.getSheet("FORMATO");

				String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
				String[] colum;

				if (registro.length == 0) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2514"));
					return;
				}
				Row fila;
				Cell celda;
				colum = registro[0].split(SysmanConstantes.SEPARADOR_COL);
				fila = sheet.getRow(1);
				for (int j = 0; j < colum.length; j++) {
					celda = fila.getCell(j);
					celda.setCellValue(colum[j]);
					if (j == 0 || j == 4) {
						celda.setCellValue(Double.parseDouble(colum[j]));
					} else {
						celda.setCellValue(colum[j]);
					}
				}

				for (int i = 1; i < registro.length; i++) {
					fila = sheet.createRow(i + 2);
					colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);
					for (int j = 0; j < colum.length; j++) {
						celda = fila.createCell(j);
						if (j == 1) {
							celda.setCellValue(Double.parseDouble(colum[j]));
						} else if (j == 6) {
							celda = fila.createCell(10);
							celda.setCellValue(Double.parseDouble(colum[j]));
						} else {
							celda.setCellValue(colum[j]);
						}
					}
				}
				workbook.write(out);
				out.close();
				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
						"NUEVO FORMATO NOMINA BANCOLOMBIA" + anio + "_" + mes + ".xls");
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

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btGenerarPlano en la vista
	 *
	 */
	public void oprimirbtGenerarPlano() {
		// <CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;
			
			String formatoEsp = ejbSysmanUtil.consultarParametro(compania,
                    "FORMATO ESPECIAL BANCOS", modulo, new Date(), false); 
			String nomArchivo = ejbSysmanUtil.consultarParametro(compania,
                    "ARCHIVO PLANO BANCOLOMBIA TXT", modulo, new Date(), false); 
			
			if(formatoEsp.equalsIgnoreCase("SI")) {
				if (nomArchivo.equals("DiscoBancolombiaIDI")) {
					String datos = ejbNominaCuatro.generarDiscoBancolombiaIdipron(compania, Integer.parseInt(proceso),
							Integer.parseInt(periodo), Integer.parseInt(mes), Integer.parseInt(anio), new Date(), fechaReporte,
							observaciones, lote, SysmanFunciones.nvlStr(banco, " "),  todosLosBancos);
					ByteArrayInputStream streamTexto;

					streamTexto = JsfUtil.serializarPlano(datos);
					archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, nomArchivo+".txt");
				}				
			}else {
				String datos = ejbNominaCuatro.generarDiscoBancoColombia(compania, Integer.parseInt(proceso),
						Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(periodo),
						SysmanFunciones.nvlStr(banco, " "), fechaReporte, todosLosBancos, observaciones, lote, 0,
						tipoCuentaBanOrigen, cuentaBancoOrigen);
				ByteArrayInputStream streamTexto;

				streamTexto = JsfUtil.serializarPlano(datos);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Pagos.fil.txt");
			}
			
		} catch (NumberFormatException | SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control cbAnio
	 * 
	 */
	public void cambiarcbAnio() {
		mes = null;
		periodo = null;
		cargarListacbMes();
		cargarListacbPeriodo();
	}

	/**
	 * Metodo ejecutado al cambiar el control cbMes
	 * 
	 * 
	 */
	public void cambiarcbMes() {
		periodo = null;
		cargarListacbPeriodo();
	}

	/**
	 * Metodo ejecutado al cambiar el control TipoR
	 * 
	 */
	public void cambiarTipoR() {
		// <CODIGO_DESARROLLADO>
		if (opcion.equals("1")) {
			todosLosBancos = true;
		} else {
			todosLosBancos = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacbBanco
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacbBanco(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ").toString();
		nombreBanco = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacbBancoOrigen
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacbBancoOrigen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bancoOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ").toString();
		nombreBancoOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();
		tipoCuentaBanOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("TIPO_CUENTA"), " ").toString();
		cuentaBancoOrigen = SysmanFunciones.nvl(registroAux.getCampos().get("CUENTA"), "0").toString();
		if ("0".equals(cuentaBancoOrigen)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4215"));
		}
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio
	 *            Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes
	 *            Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable periodo
	 * 
	 * @return periodo
	 */
	public String getPeriodo() {
		return periodo;
	}

	/**
	 * Asigna la variable periodo
	 * 
	 * @param periodo
	 *            Variable a asignar en periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna la variable banco
	 * 
	 * @return banco
	 */
	public String getBanco() {
		return banco;
	}

	/**
	 * Asigna la variable banco
	 * 
	 * @param banco
	 *            Variable a asignar en banco
	 */
	public void setBanco(String banco) {
		this.banco = banco;
	}

	/**
	 * Retorna la variable bancoOrigen
	 * 
	 * @return bancoOrigen
	 */
	public String getBancoOrigen() {
		return bancoOrigen;
	}

	/**
	 * Asigna la variable bancoOrigen
	 * 
	 * @param bancoOrigen
	 *            Variable a asignar en bancoOrigen
	 */
	public void setBancoOrigen(String bancoOrigen) {
		this.bancoOrigen = bancoOrigen;
	}

	/**
	 * Retorna la variable fechaReporte
	 * 
	 * @return fechaReporte
	 */
	public Date getFechaReporte() {
		return fechaReporte;
	}

	/**
	 * Asigna la variable fechaReporte
	 * 
	 * @param fechaReporte
	 *            Variable a asignar en fechaReporte
	 */
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}

	/**
	 * Retorna la variable observaciones
	 * 
	 * @return observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * Asigna la variable observaciones
	 * 
	 * @param observaciones
	 *            Variable a asignar en observaciones
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * Retorna la variable lote
	 * 
	 * @return lote
	 */
	public String getLote() {
		return lote;
	}

	/**
	 * Asigna la variable lote
	 * 
	 * @param lote
	 *            Variable a asignar en lote
	 */
	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public ContenedorArchivo getContArchivoPlantilla() {
		return contArchivoPlantilla;
	}

	public void setContArchivoPlantilla(ContenedorArchivo contArchivoPlantilla) {
		this.contArchivoPlantilla = contArchivoPlantilla;
	}

	public boolean isTodosLosBancos() {
		return todosLosBancos;
	}

	public void setTodosLosBancos(boolean todosLosBancos) {
		this.todosLosBancos = todosLosBancos;
	}

	public String getTipoCuentaBanOrigen() {
		return tipoCuentaBanOrigen;
	}

	public void setTipoCuentaBanOrigen(String tipoCuentaBanOrigen) {
		this.tipoCuentaBanOrigen = tipoCuentaBanOrigen;
	}

	public String getCuentaBancoOrigen() {
		return cuentaBancoOrigen;
	}

	public void setCuentaBancoOrigen(String cuentaBancoOrigen) {
		this.cuentaBancoOrigen = cuentaBancoOrigen;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String getNombreBanco() {
		return nombreBanco;
	}

	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}

	/**
	 * Retorna la variable nombreBancoOrigen
	 * 
	 * @return nombreBancoOrigen
	 */
	public String getNombreBancoOrigen() {
		return nombreBancoOrigen;
	}

	/**
	 * Asigna la variable nombreBancoOrigen
	 * 
	 * @param nombreBancoOrigen
	 *            Variable a asignar en nombreBancoOrigen
	 */
	public void setNombreBancoOrigen(String nombreBancoOrigen) {
		this.nombreBancoOrigen = nombreBancoOrigen;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listacbAnio
	 * 
	 * @return listacbAnio
	 */
	public List<Registro> getListacbAnio() {
		return listacbAnio;
	}

	/**
	 * Asigna la lista listacbAnio
	 * 
	 * @param listacbAnio
	 *            Variable a asignar en listacbAnio
	 */
	public void setListacbAnio(List<Registro> listacbAnio) {
		this.listacbAnio = listacbAnio;
	}

	/**
	 * Retorna la lista listacbMes
	 * 
	 * @return listacbMes
	 */
	public List<Registro> getListacbMes() {
		return listacbMes;
	}

	/**
	 * Asigna la lista listacbMes
	 * 
	 * @param listacbMes
	 *            Variable a asignar en listacbMes
	 */
	public void setListacbMes(List<Registro> listacbMes) {
		this.listacbMes = listacbMes;
	}

	/**
	 * Retorna la lista listacbPeriodo
	 * 
	 * @return listacbPeriodo
	 */
	public List<Registro> getListacbPeriodo() {
		return listacbPeriodo;
	}

	/**
	 * Asigna la lista listacbPeriodo
	 * 
	 * @param listacbPeriodo
	 *            Variable a asignar en listacbPeriodo
	 */
	public void setListacbPeriodo(List<Registro> listacbPeriodo) {
		this.listacbPeriodo = listacbPeriodo;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListacbBanco() {
		return listacbBanco;
	}

	public void setListacbBanco(RegistroDataModelImpl listacbBanco) {
		this.listacbBanco = listacbBanco;
	}

	/**
	 * Retorna la lista listacbBancoOrigen
	 * 
	 * @return listacbBancoOrigen
	 */
	public RegistroDataModelImpl getListacbBancoOrigen() {
		return listacbBancoOrigen;
	}

	/**
	 * Asigna la lista listacbBancoOrigen
	 * 
	 * @param listacbBancoOrigen
	 *            Variable a asignar en listacbBancoOrigen
	 */
	public void setListacbBancoOrigen(RegistroDataModelImpl listacbBancoOrigen) {
		this.listacbBancoOrigen = listacbBancoOrigen;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
