/*-
 * DiscoBancoAgrarioDjControlador.java
 *
 * 1.0
 * 
 * 22/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

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
import com.sysman.nomina.enums.DiscoBancoAgrarioDjControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Gerenaci&oacute;n de archivo plano para Bancolombia.
 *
 * @version 1.0, 22/08/2018
 * @author apineda
 */
@ManagedBean
@ViewScoped
public class DiscoBancoAgrarioDjControlador extends BeanBaseModal {

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Año con el que se desea generar el archivo.
	 */
	private String anio;
	/**
	 * Mes con el que se desea generar el archivo.
	 */
	private String mes;
	/**
	 * Periodo con el que se desea generar el archivo.
	 */
	private String periodo;
	/**
	 * Proceso de nómina con el que se desea generar el archivo.
	 */
	private String proceso;
	/**
	 * Banco seleccionado.
	 */
	private String banco;
	/**
	 * Fecha del reporte.
	 */
	private Date fechaReporte;
	/**
	 * Número de consecutivo diligenciado por el usuario
	 */
	private String consecutivo;
	/**
	 * Código oficina banco origen
	 */
	private String oficinaorigen;

	/**
	 * Nombre del banco seleccionado.
	 */
	private String nombreBanco;
	/**
	 * Sigla entidad
	 */
	private String entidad;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista de a&ntilde;os.
	 */
	private List<Registro> listaAno1;
	/**
	 * Lista de meses.
	 */
	private List<Registro> listaMes1;
	/**
	 * Lista de periodos.
	 */
	private List<Registro> listaPeriodo1;
	/**
	 * Lista de procesos.
	 */
	private List<Registro> listaProceso;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista de bancos.
	 */
	private RegistroDataModelImpl listaBanco1;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DiscoBancoAgrarioDjControlador
	 */
	public DiscoBancoAgrarioDjControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.DISCO_BANCO_AGRARIO_DJ_CONTROLADOR
					.getCodigo();
			validarPermisos();
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			anio = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			consecutivo = "1";
			fechaReporte = new Date();

			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
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
		// <CARGAR_LISTA>
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
		cargarListaProceso();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaBanco1();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		try {
			entidad = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
					compania,
					"CODIGO ENTIDAD SEGUN BANCO AGRARIO",
					SessionUtil.getModulo(), new Date(), true), "");

			oficinaorigen = SysmanFunciones
					.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"CODIGO OFICINA ORIGEN BANCO AGRARIO",
							SessionUtil.getModulo(), new Date(),
							true), "");

		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno1
	 * 
	 */
	public void cargarListaAno1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		String urlEnumId = DiscoBancoAgrarioDjControladorUrlEnum.URL5916
				.getValue();
		String url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
		try {
			listaAno1 = RegistroConverter
					.toListRegistro(requestManager.getList(url, param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMes1
	 *
	 */
	public void cargarListaMes1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		String urlEnumId = DiscoBancoAgrarioDjControladorUrlEnum.URL6375
				.getValue();
		String url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listaMes1 = RegistroConverter
					.toListRegistro(requestManager.getList(url, param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodo1
	 * 
	 */
	public void cargarListaPeriodo1() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		String urlEnumId = DiscoBancoAgrarioDjControladorUrlEnum.URL6985
				.getValue();
		String url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listaPeriodo1 = RegistroConverter
					.toListRegistro(requestManager.getList(url, param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		String urlEnumId = DiscoBancoAgrarioDjControladorUrlEnum.URL7480
				.getValue();
		String url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
		try {
			listaProceso = RegistroConverter
					.toListRegistro(requestManager.getList(url, param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaBanco1
	 *
	 */
	public void cargarListaBanco1() {
		String urlEnumId = DiscoBancoAgrarioDjControladorUrlEnum.URL7820
				.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId);
		Map<String, Object> params = new HashMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaBanco1 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), params, true,
				GeneralParameterEnum.BANCO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
	 *
	 */
	public void oprimirEnviarExcel() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarDisco en la vista
	 *
	 */
	public void oprimirGenerarDisco() {
		// <CODIGO_DESARROLLADO>
		try {
			String nombrePlano;
			String nitCompania;
			archivoDescarga = null;

			String formatoEsp = ejbSysmanUtil.consultarParametro(compania,
					"ENCABEZADO PLANO AGRARIO GOBNARINO", modulo, new Date(), false); 
			String nomArchivo = ejbSysmanUtil.consultarParametro(compania,
					"ARCHIVO PLANO BANAGRARIO TXT", modulo, new Date(), false); 

			if(formatoEsp.equalsIgnoreCase("SI")) {
				if (nomArchivo.equals("DiscoBancoagrarioGobNarino")) {
					String datos = ejbNominaCuatro.discoBancoAgrarioGobNarino(compania,
							Integer.parseInt(proceso),
							Integer.parseInt(anio), Integer.parseInt(mes),
							Integer.parseInt(periodo),
							SysmanFunciones.nvlStr(banco, " "), fechaReporte,
							Integer.parseInt(oficinaorigen));
					ByteArrayInputStream streamTexto;
					streamTexto = JsfUtil.serializarPlano(datos);

					nitCompania = SessionUtil
							.getCompaniaIngreso()
							.getNit()
							.replace(".", "")
							.replace("-", "");

					nitCompania = nitCompania.substring(0, 9);

					nitCompania = nitCompania + Integer.toString(ejbSysmanUtil
							.generarDigitoDeVerificacion(nitCompania));

					nombrePlano = SysmanFunciones.concatenar("GD",
							SysmanFunciones.convertirAFechaCadena(fechaReporte,
									"yyyyMMdd"),
							SysmanFunciones.strZero(nitCompania, 11),
							"_", SysmanFunciones.strZero(consecutivo, 2),
							".txt");
					archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
							nombrePlano);
				}
			}
			else {
				String datos = ejbNominaCuatro.discoBancoAgrario(compania,
						Integer.parseInt(proceso),
						Integer.parseInt(anio), Integer.parseInt(mes),
						Integer.parseInt(periodo),
						SysmanFunciones.nvlStr(banco, " "), fechaReporte,
						Integer.parseInt(oficinaorigen));
				ByteArrayInputStream streamTexto;
				streamTexto = JsfUtil.serializarPlano(datos);

				nitCompania = SessionUtil
						.getCompaniaIngreso()
						.getNit()
						.replace(".", "")
						.replace("-", "");

				nitCompania = nitCompania.substring(0, 9);

				nitCompania = nitCompania + Integer.toString(ejbSysmanUtil
						.generarDigitoDeVerificacion(nitCompania));

				nombrePlano = SysmanFunciones.concatenar("GD",
						SysmanFunciones.convertirAFechaCadena(fechaReporte,
								"yyyyMMdd"),
						SysmanFunciones.strZero(nitCompania, 11),
						"_", SysmanFunciones.strZero(consecutivo, 2),
						".txt");
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
						nombrePlano);
			}
		}
		catch (NumberFormatException | SystemException | JRException
				| IOException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano1
	 * 
	 */
	public void cambiarAno1() {
		mes = null;
		periodo = null;
		cargarListaMes1();
		cargarListaPeriodo1();
	}

	/**
	 * Metodo ejecutado al cambiar el control Mes1
	 * 
	 */
	public void cambiarMes1() {
		periodo = null;
		cargarListaPeriodo1();
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBanco1
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBanco1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ")
				.toString();
		nombreBanco = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRE"), " ")
				.toString();
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
	 * Variable a asignar en anio
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
	 * Variable a asignar en mes
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
	 * Variable a asignar en periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna la variable proceso
	 * 
	 * @return proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * Asigna la variable proceso
	 * 
	 * @param proceso
	 * Variable a asignar en proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
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
	 * Variable a asignar en banco
	 */
	public void setBanco(String banco) {
		this.banco = banco;
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
	 * Variable a asignar en fechaReporte
	 */
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}

	/**
	 * Retorna la variable consecutivo
	 * 
	 * @return consecutivo
	 */
	public String getConsecutivo() {
		return consecutivo;
	}

	/**
	 * Asigna la variable consecutivo
	 * 
	 * @param consecutivo
	 * Variable a asignar en consecutivo
	 */
	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	/**
	 * Retorna la variable oficinaorigen
	 * 
	 * @return oficinaorigen
	 */
	public String getOficinaorigen() {
		return oficinaorigen;
	}

	/**
	 * Asigna la variable oficinaorigen
	 * 
	 * @param oficinaorigen
	 * Variable a asignar en oficinaorigen
	 */
	public void setOficinaorigen(String oficinaorigen) {
		this.oficinaorigen = oficinaorigen;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
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

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno1
	 * 
	 * @return listaAno1
	 */
	public List<Registro> getListaAno1() {
		return listaAno1;
	}

	/**
	 * Asigna la lista listaAno1
	 * 
	 * @param listaAno1
	 * Variable a asignar en listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}

	/**
	 * Retorna la lista listaMes1
	 * 
	 * @return listaMes1
	 */
	public List<Registro> getListaMes1() {
		return listaMes1;
	}

	/**
	 * Asigna la lista listaMes1
	 * 
	 * @param listaMes1
	 * Variable a asignar en listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}

	/**
	 * Retorna la lista listaPeriodo1
	 * 
	 * @return listaPeriodo1
	 */
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}

	/**
	 * Asigna la lista listaPeriodo1
	 * 
	 * @param listaPeriodo1
	 * Variable a asignar en listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}

	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}

	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaBanco1
	 * 
	 * @return listaBanco1
	 */
	public RegistroDataModelImpl getListaBanco1() {
		return listaBanco1;
	}

	/**
	 * Asigna la lista listaBanco1
	 * 
	 * @param listaBanco1
	 * Variable a asignar en listaBanco1
	 */
	public void setListaBanco1(RegistroDataModelImpl listaBanco1) {
		this.listaBanco1 = listaBanco1;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	// <METODOS_ADICIONALES>
	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente
	 * de nulo.
	 * 
	 * @param object
	 * Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}
	// </METODOS_ADICIONALES>
}
