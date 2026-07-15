/*-
 * ConsultaauditoriasControlador.java
 *
 * 1.0
 * 
 * 23/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
import co.com.sysman.comun.excepcion.NegocioExcepcion;
import co.com.sysman.conector.IConector;
import co.com.sysman.utilidades.RespuestaApi;
import net.sf.jasperreports.engine.JRException;
import sysman.util.consumo.AuditoriaProcesador2;
import sysman.util.consumo.enums.EnumCodAuditoria;
import sysman.util.consumo.enums.EnumParametros;
import sysman.util.consumo.pojo.RespuestaConsultaAud;
import sysman.util.consumo.pojo.RespuestaHistoricosAud;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/06/2023
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  ConsultaauditoriasControlador  extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
	 * variable que almacena la clase
	 */
	private String clase;
	/**
	 * variable que almacena el tipo
	 */
	private String tipo;
	/**
	 * variable que almacena el modulo
	 */
	private String modulo;
	/**
	 * variable que almacena la fecha inicial
	 */
	private Date fechaInicial;
	/**
	 * variable que almacena la fecha final
	 */
	private Date fechaFinal;
	/**
	 * variable que se usa para bloquear los botones buscar y generar
	 */
	private boolean bloqueaProc = true;
	/**
	 * Objetos estaticos para reemplazo
	 */
	private static final String CODCOMPANIA = "#codCompania#";
	private static final String FECHAINI = "#fechaIni#";
	private static final String FECHAFIN = "#fechaFin#";
	private static final String CODPROCESO = "#codProceso#";
	private static final String OBTODO = "#obtenerTodo#";
	private static final String CODENTIDAD = "#codEntidad#";
	private static final String FORMATO = "#formatoReporte#";
	private static final String inicio = "{\"codigo\":0,\"mensaje\":\"OK\",\"cuerpo\":{\"datos\":";
	private static final String fin = "}}";
	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	//Ejecuta procesos de auditoria
	AuditoriaProcesador2 auditProcesador = new AuditoriaProcesador2();
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Crea una nueva instancia de ConsultaauditoriasControlador
	 */
	public ConsultaauditoriasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.CONSULTA_AUDITORIA.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(ConsultaauditoriasControlador.class.getName())
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
	public void inicializar() {
		enumBase = GenericUrlEnum.INF_AUDITORIA;
        abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
	}
	/**
	 * Metodo ejecutado al oprimir el boton buscar
	 * en la vista
	 */
	public void oprimirbuscar() {
		borrarDatosConsultaHistoricos();
		String respuesta = decodeBase64ExcelToJson(consultaHistorAudit());
		
		if (respuesta != null) {
			respuesta = inicio + respuesta + fin;
			
			Gson gson = new Gson();
			RespuestaConsultaAud respuestaApi = gson.fromJson(respuesta, RespuestaConsultaAud.class);
			
			for(RespuestaHistoricosAud respuestaHistoricos : respuestaApi.getCuerpo().getDatos()) {
				insertarDatos(respuestaHistoricos);
			}
				
			reasignarOrigen();
		}
	}
	/**
	 * Retorna datos de los historicos
	 */
	public byte[] consultaHistorAudit() {
		RespuestaApi respApi = new RespuestaApi();
		
		try {
			String url = auditProcesador.obtenerUrlAuditoria(EnumParametros.SERV_INFORME_AUDIT.getValue())
					.replace(CODCOMPANIA, compania)
					.replace(FECHAINI, SysmanFunciones.convertirAFechaCadena(fechaInicial, "yyyy-MM-dd"))
					.replace(FECHAFIN, SysmanFunciones.convertirAFechaCadena(fechaFinal, "yyyy-MM-dd"))
					.replace(CODPROCESO, codProceso())
					.replace(OBTODO, "false")
					.replace(CODENTIDAD, auditProcesador.obtenerCodEntidad(compania))
					.replace(FORMATO, "xlsx");
			
			respApi = auditProcesador.ejecutarServicio(url, null, 
					IConector.METHOD_GET, EnumParametros.SERV_INFORME_AUDIT.getValue());
			
		} catch (NegocioExcepcion | ParseException e) {
			e.printStackTrace();
		}
		
		if (respApi.getCodigo() != 0) {
			JsfUtil.agregarMensajeAlerta("No se encontraron datos.");
			return new byte[0];
		}
		
		return Base64.getDecoder().decode(respApi.getCuerpo().toString());
	}
	/**
	 * Retorna el codigo del proceso
	 */
	public String codProceso() {
		String codProceso = "0";
		
		if (clase.equals("DT")) {
			switch (tipo) {
    		case "D":
    			codProceso = EnumCodAuditoria.DETERIORO_ELEMENTOS_DEVOLUTIVOS.getValue();
    			break;
    		case "N":
    			codProceso = EnumCodAuditoria.DETERIORO_ELEMENTOS_INMUEBLES.getValue();
    			break;
    		default:
    			break;
    		} 
		} else {
			codProceso = auditProcesador.obtenerCodProceso(clase,"",tipo);
		}
		
		return codProceso;
	}
	/**
	 * Lee el archivo base64 y lo convierte a json
	 */
	public String decodeBase64ExcelToJson(byte[] base64) {
		try {
			if (base64.length != 0) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(base64);
				Workbook workbook = new XSSFWorkbook(inputStream);
				Sheet sheet = workbook.getSheetAt(0);
				List<Map<String,String>> data = new ArrayList<>();
				Row headerRow = sheet.getRow(3);
				
				for(int i = 4; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					Map<String,String> rowData = new HashMap<>();
					
					for(int j = 0; j < headerRow.getLastCellNum(); j++) {
						Cell cell = row.getCell(j);
						String columnName = headerRow.getCell(j).getStringCellValue();
						columnName = reemplazosEncabezado(columnName);
						
						String cellValue = getCellValueAsString(cell);
				        rowData.put(columnName, cellValue);
					}
					data.add(rowData);
				}
				
				Gson gson = new Gson();
				String json = gson.toJson(data);
				inputStream.close();
				
				return json;
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	/**
	 * Valida tipo de dato de las celdas
	 */
	private static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		CellType cellType = cell.getCellTypeEnum();
		
		if (cellType == CellType.STRING) {
			return cell.getStringCellValue();
		} else if (cellType == CellType.NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else if (cellType == CellType.BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else {
			return "";
		}
	}
	/**
	 * Reemplazos del encabezado
	 */
	public static String reemplazosEncabezado(String column) {
		column = column.replace(" ", "_");
		column = column.replace(".", "");
		column = column.replace("ó", "o");
		
		return column;
	}
	/**
	 * Elimina los datos de la tabla INF_AUDITORIA
	 */
	private void borrarDatosConsultaHistoricos() {
		try {
            UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("1909001");
            requestManager.delete(urlDelete.getUrl(), null);
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
	}
	/**
	 * Inserta la informacion de los historicos en la tabla INF_AUDITORIA
	 */
	private void insertarDatos(RespuestaHistoricosAud respuestaHistoricos) {
		try {
			Date fecha = formato.parse(respuestaHistoricos.getFecha().replace("-", "/"));
			HashMap<String, Object> param = new HashMap<>();
			
			param.put("COMPANIA", compania);
			param.put("USUARIO", respuestaHistoricos.getUsuario());
			param.put("FECHA", fecha);
			param.put("NOMBRE_PROCESO", respuestaHistoricos.getNombre_Proceso());
			param.put("ACCION", respuestaHistoricos.getAccion());
			param.put("IP", respuestaHistoricos.getDir_Ip());
			param.put("VAL_ANTERIOR", respuestaHistoricos.getValor_Inicial());
			param.put("VAL_ACTUAL", respuestaHistoricos.getValor_Final());
			param.put("CREATED_BY", SessionUtil.getUser().getCodigo());
			param.put("DATE_CREATED", new Date());
			
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.INF_AUDITORIA.getCreateKey());
			
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), param);
		} catch (ParseException | SystemException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Metodo ejecutado al oprimir el boton ver
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirver(Registro reg, int indice) {
		String[] campos = { "fecha" };
		Object[] valores = { reg.getCampos().get("FECHA") };

		String numForm = String.valueOf(GeneralCodigoFormaEnum.VER_AUDITORIA
				.getCodigo());
		
		SessionUtil.cargarModalDatosFlashCerrar(numForm, SessionUtil.getModulo(), campos, valores);
	}
	/**
	 * Metodo ejecutado al oprimir el boton generar
	 * en la vista
	 */
	public void oprimirgenerar() {
		archivoDescarga = null;
		byte[] informe = consultaHistorAudit();
		
		try {
			ByteArrayOutputStream outInf = new ByteArrayOutputStream();
			
			if (informe.length != 0) {
				outInf.write(informe);
				outInf.close();
				
				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(outInf.toByteArray()),
						"Historicos_Auditoria.xlsx");
			} else {
				return;
			}
		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Metodo ejecutado al cambiar el control fechaInicial
	 */
	public void cambiarfechaInicial() {
		if (validarFechas()) {
			bloqueaProc = false;
		} else {
			bloqueaProc = true;
		}
	}
	/**
	 * Metodo ejecutado al cambiar el control fechaFinal
	 */
	public void cambiarfechaFinal() {
		if (validarFechas()) {
			bloqueaProc = false;
		} else {
			bloqueaProc = true;
		}
	}
	/**
	 * Valida que las fechas seleccionadas sean validas
	 */
	public boolean validarFechas() {
		boolean fechaValida = true;
		
		if (fechaInicial != null && fechaFinal != null) {
			if (fechaFinal.before(fechaInicial)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4427"));
				fechaValida = false;
			} 
	
			try {
				int dias = SysmanFunciones.calcularDiferenciaDias(fechaInicial, fechaFinal);
	
				if (dias > 30) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4428"));
					fechaValida = false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			fechaValida = false;
		}
		return fechaValida;
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 */
	@Override
	public boolean insertarAntes() {
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 */
	@Override
	public boolean insertarDespues() {
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro 
	 */
	@Override
	public boolean actualizarAntes() {
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 */
	@Override   
	public boolean actualizarDespues() {
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 */
	@Override    
	public boolean eliminarAntes() {
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 */
	@Override   
	public boolean eliminarDespues() {
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {}
	/**
	 * Retorna la variable clase
	 * 
	 * @return  clase
	 */
	public String getClase() {
		return clase;
	}
	/**
	 * Asigna la variable  clase
	 * 
	 * @param  clase
	 * Variable a asignar en  clase
	 */
	public void setClase(String clase) {
		this.clase = clase;
	}
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * Retorna la variable modulo
	 * 
	 * @return  modulo
	 */
	public String getModulo() {
		return modulo;
	}
	/**
	 * Asigna la variable  modulo
	 * 
	 * @param  modulo
	 * Variable a asignar en  modulo
	 */
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * @return the bloqueaProc
	 */
	public boolean isBloqueaProc() {
		return bloqueaProc;
	}

	/**
	 * @param bloqueaProc
	 * the bloqueaProc to set
	 */
	public void setBloqueaProc(boolean bloqueaProc) {
		this.bloqueaProc = bloqueaProc;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
}
