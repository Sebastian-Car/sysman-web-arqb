/*-
 * DiscoBancoPopularControlador.java
 *
 * 1.0
 * 
 * 12/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
import com.sysman.nomina.enums.DiscoBancoPopularControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 12/09/2018
 * @author apineda
 */
@ManagedBean
@ViewScoped
public class DiscoBancoPopularControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
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
	 * Banco seleccionado
	 */
	private String banco;
	/**
	 * Proceso seleccionado
	 */
	private String proceso;
	/**
	 * Fecha de generación plano
	 */
	private Date fechaReporte;
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
	 * 
	 */
	private String bancoOrigen;

	
	private String tipoCuentaBanOrigen;
	/**
	 * Cuenta banco origen
	 */
	private String cuentaBancoOrigen;
	/**
	 * Observaciones sobre el plano a generar
	 */
	
	private String observaciones;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista de años
	 */
	private List<Registro> listacbAno;
	/**
	 * Lista de meses
	 */
	private List<Registro> listacbMes;
	/**
	 * Listado de periodos
	 */
	private List<Registro> listacbPeriodo;
	/**
	 * Listado de procesos
	 */
	private List<Registro> listacbProceso;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Listado de bancos nómina
	 */
	private RegistroDataModelImpl listacbBanco;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listacbBancoOrigen;
	
	@EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;
	
	private String nombreBancoOrigen;
	
	private String opcion;

	/**
	 * Crea una nueva instancia de DiscoBancoPopularControlador
	 */
	public DiscoBancoPopularControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.DISCO_BANCO_POPULAR.getCodigo();
			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			anio = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			todosLosBancos = false;
			observaciones = "NOMINA";
			validarPermisos();
			fechaReporte = new Date();
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
		cargarListacbAno();
		cargarListacbMes();
		cargarListacbPeriodo();
		cargarListacbProceso();
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
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacbAno
	 *
	 */
	public void cargarListacbAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listacbAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DiscoBancoPopularControladorUrlEnum.URL4522.getValue())
											.getUrl(),
									param));
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
		try {
			listacbMes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DiscoBancoPopularControladorUrlEnum.URL4996.getValue())
											.getUrl(),
									param));
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

		String urlEnumId = DiscoBancoPopularControladorUrlEnum.URL6204.getValue();
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
	 * Carga la lista listacbProceso
	 *
	 */
	public void cargarListacbProceso() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listacbProceso = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DiscoBancoPopularControladorUrlEnum.URL6203.getValue())
											.getUrl(),
									param));
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
				.getUrlServiceByUrlByEnumID(DiscoBancoPopularControladorUrlEnum.URL6961.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacbBanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"BANCO");
	}
	
	/**
	 * 
	 * Carga la lista listacbBancoOrigen
	 * 
	 */
	public void cargarListacbBancoOrigen() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DiscoBancoPopularControladorUrlEnum.URL459014.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacbBancoOrigen = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.BANCO.getName());
	}
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btGenerar en la vista
	 *
	 */
	public void oprimirbtGenerar() {
		// <CODIGO_DESARROLLADO>
		
		try {
			if(todosLosBancos==false) {
				archivoDescarga = null;
				String datos = ejbNominaCuatro.generarDiscoBancoPopular(compania,
						Integer.parseInt(proceso),
						Integer.parseInt(anio),
						Integer.parseInt(mes),
						Integer.parseInt(periodo),
						SysmanFunciones.nvlStr(banco, " "),
						todosLosBancos,
						fechaReporte,
						observaciones);
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
						"POPULAR" + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)] + anio + ".PRN");
			}else {
				archivoDescarga = null;
				String datos = ejbNominaCuatro.generarDiscoTodosLosBancos(
						compania,
						Integer.parseInt(proceso),						
						Integer.parseInt(anio),
						Integer.parseInt(mes),
						Integer.parseInt(periodo),
						SysmanFunciones.nvlStr(banco, " "),
						todosLosBancos,
						fechaReporte,
						observaciones,
						tipoCuentaBanOrigen,
						cuentaBancoOrigen);
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
						"GENERAL" + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)] + anio + ".PRN");
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
	 * Metodo ejecutado al cambiar el control cbAno
	 * 
	 */
	public void cambiarcbAno() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control cbMes
	 * 
	 */
	public void cambiarcbMes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
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
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4412"));
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
	 *            Variable a asignar en proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
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
	 * Retorna la variable nombreBanco
	 * 
	 * @return nombreBanco
	 */
	public String getNombreBanco() {
		return nombreBanco;
	}

	/**
	 * Asigna la variable nombreBanco
	 * 
	 * @param nombreBanco
	 *            Variable a asignar en nombreBanco
	 */
	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}
	
	/**
	 * Retorna la variable TodosLosBancos
	 * 
	 * @return todosLosBancos
	 */
	public boolean isTodosLosBancos() {
		return todosLosBancos;
	}
	
	/**
	 * Asigna la variable TodosLosBancos
	 * 
	 * @param todosLosBancos
	 *            Variable a asignar en todosLosBancos
	 */

	public void setTodosLosBancos(boolean todosLosBancos) {
		this.todosLosBancos = todosLosBancos;
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
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listacbAno
	 * 
	 * @return listacbAno
	 */
	public List<Registro> getListacbAno() {
		return listacbAno;
	}

	/**
	 * Asigna la lista listacbAno
	 * 
	 * @param listacbAno
	 *            Variable a asignar en listacbAno
	 */
	public void setListacbAno(List<Registro> listacbAno) {
		this.listacbAno = listacbAno;
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

	/**
	 * Retorna la lista listacbProceso
	 * 
	 * @return listacbProceso
	 */
	public List<Registro> getListacbProceso() {
		return listacbProceso;
	}

	/**
	 * Asigna la lista listacbProceso
	 * 
	 * @param listacbProceso
	 *            Variable a asignar en listacbProceso
	 */
	public void setListacbProceso(List<Registro> listacbProceso) {
		this.listacbProceso = listacbProceso;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listacbBanco
	 * 
	 * @return listacbBanco
	 */
	public RegistroDataModelImpl getListacbBanco() {
		return listacbBanco;
	}

	/**
	 * Asigna la lista listacbBanco
	 * 
	 * @param listacbBanco
	 *            Variable a asignar en listacbBanco
	 */
	public void setListacbBanco(RegistroDataModelImpl listacbBanco) {
		this.listacbBanco = listacbBanco;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
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

}
