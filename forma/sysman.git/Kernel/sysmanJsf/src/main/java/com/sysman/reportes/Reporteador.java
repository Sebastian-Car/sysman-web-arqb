/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.reportes;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanConstantes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jgomez
 */
public class Reporteador {

	protected static final ResourceBundle idioma;

	private static final String PAR_CONSULTA = "CONSULTA";
	private static final String PAR_INFORME = "INFORME";

	private static final String SERVICIO_INFORMES = "404001";
	private static final String SERVICIO_SUBINFORMES = "404002";

	protected static DatosSesion datosSesion;
	private static String compania;
	private static String companiaNombre;
	private static String companiaNit;
	private static String companiaSigla;
	private static String companiaCiudad;
	private static String companiaDepartamento;
	private static String companiaPais;
	private static String user;
	private static boolean tieneDatos;

	static {
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
	}

	private Reporteador() {
	}

	public static String resuelveConsulta(String reporte, int moduloReal, Map<String, Object> reemplazos) {
		// Busca la consulta del informe en la tabla
		String consulta = "";
		try {
			RequestManager re = new RequestManager();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(PAR_INFORME, reporte.toUpperCase());
			Registro registro = RegistroConverter
					.toRegistro(re.get(UrlServiceUtil.getUrlBeanById(SERVICIO_INFORMES).getUrl(), parametros));

			if (registro == null) {
				String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE_BD");
				msj = msj.replace("s$reporte$s", reporte.toUpperCase());
				throw new SysmanException(msj);
			}

			consulta = (String) registro.getCampos().get(PAR_CONSULTA);
			if (!("".equals(consulta))) {
				if (reemplazos != null) {
					consulta = reemplazarInicial(reemplazos, consulta);
					if (reemplazos.containsKey("usuario") && reemplazos.get("usuario").equals("webservice")) {
							return consulta;
					}
				}
					consulta = reemplazaSql(consulta);

			} else {
				JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_INFORME_NO_EXISTE_APLICACION));
				return "";
			}
		} catch (NullPointerException | NumberFormatException | SystemException | SysmanException ex) {
			JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " - " + ex.getMessage());
			Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
		}

		return consulta;
	}
	/**
	 * @author ldiaz (Luis Jacobo Diaz Muñoz)
	 * Esta sobre carga del metodo es mas que todo para el desarrollo de marcablanca, 
	 * pues no contiene comapnia, año, por lo tanto no puede resolver cualquier consulta
	 * @param reporte
	 * @param reemplazos
	 * @return
	 */
	public static String resuelveConsulta(String reporte, Map<String, Object> reemplazos) {
		// Busca la consulta del informe en la tabla
		String consulta = "";
		try {
			RequestManager re = new RequestManager();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(PAR_INFORME, reporte.toUpperCase());
			Registro registro = RegistroConverter
					.toRegistro(re.get(UrlServiceUtil.getUrlBeanById(SERVICIO_INFORMES).getUrl(), parametros));

			if (registro == null) {
				String msj = "CONSULTAS NO EXISTE";
				msj = msj.replace("s$reporte$s", reporte.toUpperCase());
				throw new SysmanException(msj);
			}

			consulta = (String) registro.getCampos().get(PAR_CONSULTA);
			if (!("".equals(consulta))) {
				if (reemplazos != null) {
					consulta = reemplazarInicial(reemplazos, consulta);
				}
			} else {
				JsfUtil.agregarMensajeError("EL INFORMES NO EXISTE EN LA APLICACION");
				return "";
			}
		} catch (NullPointerException | NumberFormatException | SystemException | SysmanException ex) {
			JsfUtil.agregarMensajeError("TRANSACCION INTERRUMPIDA" + " - " + ex.getMessage());
			Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
		}

		return consulta;
	}
	public static void resuelveConsulta(String reporte, int moduloReal, Map<String, Object> reemplazos,
			Map<String, Object> parametros) {
		// Busca la consulta del informe en la tabla
		String consulta = "";
		try {
			RequestManager re = new RequestManager();
			Map<String, Object> parametroInf = new HashMap<>();
			parametroInf.put(PAR_INFORME, reporte.toUpperCase());
			Registro registro = RegistroConverter
					.toRegistro(re.get(UrlServiceUtil.getUrlBeanById(SERVICIO_INFORMES).getUrl(), parametroInf));
			if (registro == null) {
				String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE_BD");
				msj = msj.replace("s$reporte$s", reporte.toUpperCase());
				throw new SysmanException(msj);
			}

			consulta = (String) registro.getCampos().get(PAR_CONSULTA);

			validarConsultaVacia(consulta, "PR_STRSQL", reemplazos, parametros);

			List<Registro> subReport = RegistroConverter.toListRegistro(
					re.getList(UrlServiceUtil.getUrlBeanById(SERVICIO_SUBINFORMES).getUrl(), parametroInf));

			for (Registro sub : subReport) {
				consulta = sub.getCampos().get(PAR_CONSULTA).toString();
				validarConsultaVacia(consulta, sub.getCampos().get("PARAMETRO").toString(), reemplazos, parametros);
			}

		} catch (NullPointerException | NumberFormatException ex) {
			JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " - " + ex.getMessage());
			Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SysmanException | SystemException ex) {
			JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " - " + ex.getMessage());
			Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

	}

	/**
	 * Metodo sobrecargado para efectos de generar reportes desde autoservicio sin
	 * crear sessión de JSF
	 * 
	 * @param reporte    : Identificador del reporte
	 * @param moduloReal : Modulo sobre el cual se genera el reporte
	 * @param parametros : Mapa que se entrega para reemplazar
	 * @param datos      : Objeto que contiene los datos con los que se simula la
	 *                   autenticación
	 * @throws SysmanException
	 */
	public static String resuelveConsulta(String reporte, int moduloReal, Map<String, Object> reemplazos,
			DatosSesion datos) throws SysmanException {
		datosSesion = datos;
		// Busca la consulta del informe en la tabla
		String consulta = "";
		try {
			RequestManager re = new RequestManager();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(PAR_INFORME, reporte.toUpperCase());
			Registro registro = RegistroConverter
					.toRegistro(re.get(UrlServiceUtil.getUrlBeanById(SERVICIO_INFORMES).getUrl(), parametros));

			if (registro == null) {
				String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE_BD");
				msj = msj.replace("s$reporte$s", reporte.toUpperCase());
				throw new SysmanException(msj);
			}

			consulta = (String) registro.getCampos().get(PAR_CONSULTA);
			if (!("".equals(consulta))) {
				if (reemplazos != null) {
					consulta = reemplazarInicial(reemplazos, consulta);
				}
				consulta = reemplazaSql(consulta);
			} else {
				throw new SysmanException(idioma.getString(Constantes.MSM_INFORME_NO_EXISTE_APLICACION));
			}
		} catch (NullPointerException | NumberFormatException | SystemException | SysmanException ex) {
			throw new SysmanException(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " - " + ex.getMessage());
		}

		return consulta;
	}

	/**
	 * Metodo sobrecargado para efectos de generar reportes desde autoservicio sin
	 * crear sessión de JSF
	 * 
	 * @param reporte    : Identificador del reporte
	 * @param moduloReal : Modulo sobre el cual se genera el reporte
	 * @param reemplazos : Mapa que contiene los reemplazos necesarios para generar
	 *                   la consulta
	 * @param parametros : Mapa que se entrega para reemplazar
	 * @param datos      : Objeto que contiene los datos con los que se simula la
	 *                   autenticación
	 * @throws SysmanException
	 */
	public static void resuelveConsulta(String reporte, int moduloReal, Map<String, Object> reemplazos,
			Map<String, Object> parametros, DatosSesion datos) throws SysmanException {
		datosSesion = datos;
		// Busca la consulta del informe en la tabla
		String consulta = "";
		try {
			RequestManager re = new RequestManager();
			Map<String, Object> parametroInf = new HashMap<>();
			parametroInf.put(PAR_INFORME, reporte.toUpperCase());
			Registro registro = RegistroConverter
					.toRegistro(re.get(UrlServiceUtil.getUrlBeanById(SERVICIO_INFORMES).getUrl(), parametroInf));
			if (registro == null) {
				String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE_BD");
				msj = msj.replace("s$reporte$s", reporte.toUpperCase());
				throw new SysmanException(msj);
			}

			consulta = (String) registro.getCampos().get(PAR_CONSULTA);

			validarConsultaVacia(consulta, "PR_STRSQL", reemplazos, parametros);

			List<Registro> subReport = RegistroConverter.toListRegistro(
					re.getList(UrlServiceUtil.getUrlBeanById(SERVICIO_SUBINFORMES).getUrl(), parametroInf));

			for (Registro sub : subReport) {
				consulta = sub.getCampos().get(PAR_CONSULTA).toString();
				validarConsultaVacia(consulta, sub.getCampos().get("PARAMETRO").toString(), reemplazos, parametros);
			}

		} catch (NullPointerException | NumberFormatException | SysmanException | SystemException ex) {
			throw new SysmanException(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " - " + ex.getMessage());
		}

	}

	private static void validarConsultaVacia(String consulta, String parametro, Map<String, Object> reemplazos,
			Map<String, Object> parametros) throws SysmanException {
		String consultaFinal = consulta;
		if (!("".equals(consulta))) {
			consultaFinal = reemplazos != null ? reemplazarInicial(reemplazos, consulta) : consultaFinal;
			consultaFinal = reemplazaSql(consultaFinal);
			parametros.put(parametro, consultaFinal);
		} else {
			throw new SysmanException(idioma.getString("MSM_INFORME_NO_EXISTE_APLICACION"));
		}

	}

	@SuppressWarnings("deprecation")
	public static void generaReporte(String reporte, ReportesBean.FORMATOS formato, String comando, int moduloReal,
			Map<String, Object> parametros, Map<String, Object> reemplazos) {
		// Busca la consulta del informe en la tabla
		String consulta = "";

		try {

			resuelveConsulta(reporte, moduloReal, reemplazos, parametros);
			consulta = parametros.get("PR_STRSQL").toString();

			if ("".equals(consulta)) {
				JsfUtil.agregarMensajeError(idioma.getString("MSM_INFORME_NO_EXISTE_APLICACION"));
				return;
			}
			// Generación de Excel o informe
			if ("QR".equals(comando)) {
				JsfUtil.exportarHojaDeDatos(consulta, ConectorPool.ESQUEMA_SYSMAN, formato);
			} else {
				JsfUtil.exportar(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			}
		} catch (FileNotFoundException ex) {
			String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE");
			msj = msj.replace("s$reporte$s", reporte);
			JsfUtil.agregarMensajeInformativo(msj);
			Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JRException | IOException | DRException | NullPointerException | NumberFormatException
				| SysmanException ex) {
			JsfUtil.agregarMensajeError(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA) + " - " + ex.getMessage());
			Logger.getLogger(Reporteador.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private static void iniciaVariables() {
		if (datosSesion != null) {
			compania = datosSesion.getCompania();
			companiaNombre = datosSesion.getCompaniaIngreso().getNombre();
			companiaNit = datosSesion.getCompaniaIngreso().getNit();
			companiaSigla = datosSesion.getCompaniaIngreso().getSigla();
			companiaCiudad = datosSesion.getCompaniaIngreso().getCiudad();
			companiaDepartamento = datosSesion.getCompaniaIngreso().getDepartamento();
			companiaPais = datosSesion.getCompaniaIngreso().getPais();
			user = datosSesion.getUser().getCodigo();
		} else {
			compania = SessionUtil.getCompania();
			companiaNombre = SessionUtil.getCompaniaIngreso().getNombre();
			companiaNit = SessionUtil.getCompaniaIngreso().getNit();
			companiaSigla = SessionUtil.getCompaniaIngreso().getSigla();
			companiaCiudad = SessionUtil.getCompaniaIngreso().getCiudad();
			companiaDepartamento = SessionUtil.getCompaniaIngreso().getDepartamento();
			companiaPais = SessionUtil.getCompaniaIngreso().getPais();
			user = SessionUtil.getUser().getCodigo();

		}
	}

	public static String reemplazaSql(String consulta) {
		iniciaVariables();
		String strFinal;
		strFinal = consulta.replace("s$compania$s", "'" + compania + "'");
		strFinal = strFinal.replace("s$companiaNombre$s", "'" + companiaNombre + "'");
		strFinal = strFinal.replace("s$companiaNit$s", "'" + companiaNit + "'");
		strFinal = strFinal.replace("s$companiaSigla$s", "'" + companiaSigla + "'");
		strFinal = strFinal.replace("s$companiaCiudad$s", "'" + companiaCiudad + "'");
		strFinal = strFinal.replace("s$companiaDepartamento$s", "'" + companiaDepartamento + "'");
		strFinal = strFinal.replace("s$companiaPais$s", "'" + companiaPais + "'");

		strFinal = strFinal.replace("s$getUser$s", "'" + user + "'");

		return strFinal;
	}

	public static String reemplazarInicial(Map campos, String consulta) {
		String rta = consulta;
		Iterator it = campos.entrySet().iterator();
		String regex = "s\\$.+\\$s";
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			String entrada = String.valueOf(e.getKey());
			boolean coincide = Pattern.matches(regex, entrada);
			String clave = coincide ? entrada : "s$" + entrada + "$s";
			rta = rta.replace(clave, e.getValue().toString());
		}
		return rta;
	}

	/**
	 * Establece los datos de sesi&oacute;n en caso de que no se pueda usar
	 * variables de sesi&oacute;n.
	 * 
	 * @param datosSesion conjunto de datos relacionados con la sesi&oacute;n
	 */
	public static void setDatosSesion(DatosSesion datosSesion) {
		Reporteador.datosSesion = datosSesion;
	}

}
