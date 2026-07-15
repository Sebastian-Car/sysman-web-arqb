/*-
 * FrmenviosigecControlador.java
 *
 * 1.0
 * 
 * 19/03/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;

import com.sysman.contabilidad.enums.FrmenviosigecControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.contabilidad.enums.FrmenviosigecControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APISIGEC;
import com.sysman.util.rest.ParametrosLiquidacionSIGEC;
import com.sysman.util.rest.ParametrosPagoSIGEC;
import com.sysman.util.rest.ParametrosSIGEC;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaApiSigec;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 19/03/2024
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmenviosigecControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private String nitCompania;
	private int anio;
	private String usuario;
	private Date fechaInicio;
	private Date fechaFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de FrmenviosigecControlador
	 */
	public FrmenviosigecControlador() {
		super();
		compania = SessionUtil.getCompania();
		nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		anio = SysmanFunciones.ano(new Date());
		usuario = SessionUtil.getUser().getCodigo();
		fechaInicio = new Date();
		fechaFin = new Date();
		try {
			// 2454
			numFormulario = GeneralCodigoFormaEnum.FRM_ENVIO_SIGEC.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			SessionUtil.cleanFlash();
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

	/**
	 * Metodo ejecutado al oprimir el boton EnviarActoDocumento en la vista
	 */
	public void oprimirEnviarActo_Documento() {
		// <CODIGO_DESARROLLADO>
		/*
		 * RUTINA PARA A. SERVICIO REPORTE DE ACTO/DOCUMENTO
		 */
		String url;
		String token = null;
		String log = null;
		String respuesta = "";
		String json = "";
		archivoDescarga = null;
		log = "|---------------         LOG DE LOGICA SERVICIO ACTO DOCUMENTO / SIGEC        ---------------|";

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);

			Calendar c = Calendar.getInstance();
			c.setTime(fechaFin);
			c.add(Calendar.DATE, 1);
			fechaFin = c.getTime();

			Map<String, Object> params = new TreeMap<>();
			SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			params.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicio);
			params.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFin);
			//campo tiposigec AD4 - AD8 -- 
			// CHECK tipoorden de compra estampillasigec
			List<Registro> listaActoSysman = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmenviosigecControladorUrlEnum.URL001.getValue())
											.getUrl(),
									params));
			Gson gson = new Gson();
			List<Map<String, Object>> listFinal = new ArrayList<>();
			List<String> listJson = new ArrayList<>();
			
			if (!listaActoSysman.isEmpty()) {
				for (Registro reg : listaActoSysman) {

					ParametrosSIGEC param = new ParametrosSIGEC();
					String fechaInicio = formatFecha
							.format(reg.getCampos().get(FrmenviosigecControladorEnum.FECHAINICIO.getValue()));
					String fechaFin = formatFecha
							.format(reg.getCampos().get(FrmenviosigecControladorEnum.FECHAFINALIZACION.getValue()));

					BigInteger valorBigInteger = (BigInteger) reg.getCampos()
							.get(FrmenviosigecControladorEnum.PLATAFORMA.getValue());
					BigInteger valorBigInteger1 = (BigInteger) reg.getCampos()
							.get(FrmenviosigecControladorEnum.VALORTOTAL.getValue());

					Integer plataforma = valorBigInteger.intValue();
					Integer valorTotal = valorBigInteger1.intValue();

					param.setPlatform(plataforma);
					param.setActDocumentCode(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.EQUIV_SIGEC.getValue()), "")
							.toString());
					param.setGeneratorFactValue(valorTotal);
					param.setPayerDocumentParametricTypeCode(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.SIGEC.getValue()), "").toString());
					param.setTaxpayerDocumentNumber(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.TERCERO.getValue()), "").toString());
					param.setTaxpayerName(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.NOMBRE.getValue()), "").toString());
					param.setGeneratorFactStartDate(fechaInicio);
					param.setGeneratorFactEndDate(fechaFin);
					param.setParametricActDocumentCodeType(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.TIPO_SIGEC.getValue()), "")
							.toString());
					
					
					json = gson.toJson(param, ParametrosSIGEC.class);
			        listJson.add(json); // Agregamos el JSON a la lista

					APISIGEC apiSigec = new APISIGEC();
					Map<String, Object> paramss = new HashMap<>();
					try {
						respuesta = apiSigec.postActoDocumento(token, url, json);
						paramss.put("error", respuesta);
						listFinal.add(paramss);
					} catch (SysmanException | IOException | RuntimeException e) {
						listFinal.add(paramss);
					}
				
					
				}
			} 
			
			
			for (Map<String, Object> error : listFinal) {
			    log += "\n" + SysmanFunciones.nvl(error.get("error"),"");
			}
			
			log +="\n" + "|------------------------------- JSONS --------------------------------------------|\n";
			for (String jsonV : listJson) {
			    log += jsonV + "\n";
			}
			
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnvioSigec.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException  | IOException  | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton EnviarLiquidacion/Anulacion en la vista
	 */
	public void oprimirEnviarLiquidacion_Anulacion() {
		// <CODIGO_DESARROLLADO>
		/*
		 * RUTINA PARA A. SERVICIO REPORTE DE LIQUIDACION
		 */
		String url;
		String token = null;
		String log = null;
		String respuesta = "";
		String json = "";
		archivoDescarga = null;
		log = "|---------------         LOG DE LOGICA SERVICIO LIQUIDACION / SIGEC        ---------------|";

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);

			Calendar c = Calendar.getInstance();
			c.setTime(fechaFin);
			c.add(Calendar.DATE, 1);
			fechaFin = c.getTime();

			Map<String, Object> params = new TreeMap<>();
			SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			params.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicio);
			params.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFin);

			List<Registro> listaActoSysman = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmenviosigecControladorUrlEnum.URL002.getValue())
											.getUrl(),
									params));
			
			Gson gson = new Gson();
			List<Map<String, Object>> listFinal = new ArrayList<>();
			List<String> listJson = new ArrayList<>();
			
			if (!listaActoSysman.isEmpty()) {
				for (Registro reg : listaActoSysman) {

					ParametrosLiquidacionSIGEC param = new ParametrosLiquidacionSIGEC();

					param.setType(((Double) reg.getCampos().get(FrmenviosigecControladorEnum.TIPO_SIGEC.getValue()))
							.intValue());
					param.setActDocumentCode(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.EQUIV_SIGEC.getValue()), "")
							.toString());
					param.setStampNumber(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.CUENTAPPTAL.getValue()), "0"));
					param.setLiquidatedValue(
							((Double) reg.getCampos().get(FrmenviosigecControladorEnum.VALOR_DEBITO.getValue()))
									.intValue());
					param.setLiquidatedValueId(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.NUMERO.getValue()), "").toString());
					param.setPayerDocumentParametricTypeCode(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.SIGEC.getValue()), "").toString());
					param.setTaxpayerDocumentNumber(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.TERCERO.getValue()), "").toString());

					json = gson.toJson(param, ParametrosLiquidacionSIGEC.class);
			        listJson.add(json); // Agregamos el JSON a la lista

					APISIGEC apiSigec = new APISIGEC();
					Map<String, Object> paramss = new HashMap<>();
					try {
						respuesta = apiSigec.postLiquidacion(token, url, json);
						paramss.put("error", respuesta);
						listFinal.add(paramss);
					} catch (SysmanException | IOException | RuntimeException e) {
						listFinal.add(paramss);
					}

				}
			}
			
			for (Map<String, Object> error : listFinal) {
			    log += "\n" + SysmanFunciones.nvl(error.get("error"),"");
			}
			
			log +="\n" + "|------------------------------- JSONS --------------------------------------------|\n";
			for (String jsonV : listJson) {
			    log += jsonV + "\n";
			}
			

			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnvioSigec.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException  | IOException  | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton EnviarPago/Anulacion en la vista
	 */
	public void oprimirEnviarPago_Anulacion() {
		// <CODIGO_DESARROLLADO>
		/*
		 * RUTINA PARA A. SERVICIO REPORTE DE PAGO
		 */
		String url;
		String token = null;
		String log = null;
		String respuesta = "";
		String json = "";
		archivoDescarga = null;
		log = "|---------------         LOG DE LOGICA SERVICIO PAGO / SIGEC        ---------------|";

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);

			Calendar c = Calendar.getInstance();
			c.setTime(fechaFin);
			c.add(Calendar.DATE, 1);
			fechaFin = c.getTime();

			Map<String, Object> params = new TreeMap<>();
			SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			params.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicio);
			params.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFin);

			List<Registro> listaActoSysman = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmenviosigecControladorUrlEnum.URL003.getValue())
											.getUrl(),
									params));

			Gson gson = new Gson();
			List<Map<String, Object>> listFinal = new ArrayList<>();
			List<String> listJson = new ArrayList<>();
			
			if (!listaActoSysman.isEmpty()) {
				for (Registro reg : listaActoSysman) {

					ParametrosPagoSIGEC param = new ParametrosPagoSIGEC();

					String fecha = formatFecha.format(reg.getCampos().get(GeneralParameterEnum.FECHA.getName()));

					param.setType(((Double) reg.getCampos().get(FrmenviosigecControladorEnum.TIPO_SIGEC.getValue()))
							.intValue());
					param.setPaymentDate(fecha);
					param.setValuePayed((int) reg.getCampos().get(FrmenviosigecControladorEnum.VALOR.getValue()));
					param.setLiquidatedValueId(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.CMPTE_AFECTADO.getValue()), "")
							.toString());
					param.setPaidValueId(SysmanFunciones
							.nvl(reg.getCampos().get(FrmenviosigecControladorEnum.NUMERO.getValue()), "").toString());

					json = gson.toJson(param, ParametrosPagoSIGEC.class);
			        listJson.add(json); // Agregamos el JSON a la lista

					APISIGEC apiSigec = new APISIGEC();
					Map<String, Object> paramss = new HashMap<>();
					try {
						respuesta = apiSigec.postPago(token, url, json);
						paramss.put("error", respuesta);
						listFinal.add(paramss);
					} catch (SysmanException | IOException | RuntimeException e) {
						listFinal.add(paramss);
					}
				
				}
			}
			
			for (Map<String, Object> error : listFinal) {
			    log += "\n" + SysmanFunciones.nvl(error.get("error"),"");
			}
			
			log +="\n" + "|------------------------------- JSONS --------------------------------------------|\n";
			for (String jsonV : listJson) {
			    log += jsonV + "\n";
			}

			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnvioSigec.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			
		} catch (SystemException  | IOException  | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String getNitCompania() {
		return nitCompania;
	}

	public void setNitCompania(String nitCompania) {
		this.nitCompania = nitCompania;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getCompania() {
		return compania;
	}
}
