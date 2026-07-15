/*-
 * FrmNotificacionInicioControlador.java
 *
 * 1.0
 * 
 * 23/10/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @version 1.0, 23/10/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmNotificacionInicioControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private String imBanner;
	private String imSimona;
	private String notificaciones;

	private String listadoNotificaciones = "531005";
	private String updateNotificaciones = "531004";

	/**
	 * Crea una nueva instancia de FrmNotificacionInicioControlador
	 */
	public FrmNotificacionInicioControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRMNOTIFICACIONESCONTROLADOR
                    .getCodigo();;
			validarPermisos();
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
		abrirFormulario();
		urlConexionCache = UrlServiceCache.SYSMANIRISST;

	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		cargarImagenes();
		cargarNotificaciones();

	}

	private void cargarNotificaciones() {

		List<Registro> lista = null;

		Date fechaIni = null;
		Date fechaFin = null;
		String descripcion = "";

		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

		try {
			lista = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(listadoNotificaciones).getUrl(), null),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANIRISST, "EVENTOS_CALENDARIO"));

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < lista.size(); i++) {

				Registro registro = (Registro) lista.get(i);

				if (registro.getCampos().get("FECHA_INICIAL") != null) {
					fechaIni = (Date) registro.getCampos().get("FECHA_INICIAL");

				}

				if (registro.getCampos().get("FECHA_FINAL") != null) {
					fechaFin = (Date) registro.getCampos().get("FECHA_FINAL");

				}

				if (registro.getCampos().get("DESCRIPCION") != null) {
					descripcion = registro.getCampos().get("DESCRIPCION").toString();
				}

				String strFechaIni = (fechaIni != null) ? formato.format(fechaIni) : "";
				String strFechaFin = (fechaFin != null) ? formato.format(fechaFin) : "";

				sb.append("De ").append(strFechaIni).append(" a ").append(strFechaFin).append(" : ").append(descripcion)
						.append("\n\n");
			}

			notificaciones = sb.toString();

		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void oprimirEntendido() {
		try {
			urlConexionCache = UrlServiceCache.SYSMANIRISST;
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(updateNotificaciones);

			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(), true);

			Parameter parameter = new Parameter();

			parameter.setFields(param);

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RequestContext.getCurrentInstance().closeDialog(null);

	}

	private void cargarImagenes() {
		InputStream archivoBanner = null;
		InputStream archivoSimona = null;
		imBanner = null;
		imSimona = null;
		try {

			String rutaBanner = "/opt/sysman/data/imagenes/banner.jpg";

			File ficheroBanner = new File(rutaBanner);

			if (!ficheroBanner.exists()) {
				throw new IOException("El archivo no se encontró: " + rutaBanner);
			}

			archivoBanner = new FileInputStream(ficheroBanner);
			imBanner = JsfUtil.encodeImage(IOUtils.toByteArray(archivoBanner));

			JsfUtil.ejecutarJavaScript("cargarImagen('FR2545_nuevo:IM2076')");

			String rutaSimona = "/opt/sysman/data/imagenes/Simona.png";

			File ficheroSimona = new File(rutaSimona);

			if (!ficheroSimona.exists()) {
				throw new IOException("El archivo no se encontró: " + rutaSimona);
			}

			archivoSimona = new FileInputStream(ficheroSimona);
			imSimona = JsfUtil.encodeImage(IOUtils.toByteArray(archivoSimona));

			JsfUtil.ejecutarJavaScript("cargarImagen('FR2545_nuevo:IM2077')");

		} catch (IOException e) {
			System.err.println("Error al procesar el archivo: " + e.getMessage());
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * @return the imBanner
	 */
	public String getImBanner() {
		return imBanner;
	}

	/**
	 * @param imBanner the imBanner to set
	 */
	public void setImBanner(String imBanner) {
		this.imBanner = imBanner;
	}

	/**
	 * @return the imSimona
	 */
	public String getImSimona() {
		return imSimona;
	}

	/**
	 * @param imSimona the imSimona to set
	 */
	public void setImSimona(String imSimona) {
		this.imSimona = imSimona;
	}

	/**
	 * @return the notificaciones
	 */
	public String getNotificaciones() {
		return notificaciones;
	}

	/**
	 * @param notificaciones the notificaciones to set
	 */
	public void setNotificaciones(String notificaciones) {
		this.notificaciones = notificaciones;
	}

}
