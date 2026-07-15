/*
 * 
 */
package com.sysman.beanbase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sysman.auditoria.Auditoria;
import com.sysman.auditoria.Entidad;
import com.sysman.auditoria.Procesos;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GeneralParametrosEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.kernel.api.clientwso2.util.enums.HttpMethodEnum;
import com.sysman.util.SysmanFunciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.sql.Timestamp;



import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author grojas
 */
public class AuditoriaService {

	private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

	private static final String SERVICIO_MODULO = "58005";
	private static final String SERVICIO_AUDITORIA_TABLAS = "1991001";
	protected RequestManager requestManager = new RequestManager();
	private String parametro;

	/**
	 * Ejecuta el proceso de auditoría según la acción realizada en la tabla.
	 *
	 * Valida si la operación debe auditarse, construye la entidad de auditoría,
	 * compara los cambios (en caso de actualización) y envía la información
	 * al servicio de auditoría sin afectar el CRUD principal.
	 *
	 * @param accion tipo de operación (i=insertar, m=modificar, e=eliminar)
	 * @param tabla nombre de la tabla afectada
	 * @param llaves llaves primarias del registro
	 * @param parameters valores actuales
	 * @param parametrosAntes valores anteriores
	 */
	protected void auditar(String accion,
			String tabla,
			Map<String, Object> llaves,
			Map<String, Object> parameters,
			Map<String, Object> parametrosAntes) {

		try {

			String accionR;
			String codigo;
			switch (accion) {
			case "i": 
				accionR = "CREAR";
				codigo = "001";
				break;
			case "m": 
				accionR = "ACTUALIZAR";
				codigo = "002";
				break;
			case "e": 
				accionR = "ELIMINAR";
				codigo = "003";
				break;
			default: return;
			}

			if (!auditaOperacion(tabla, accionR)) {
				return;
			}

			// ── Validación temprana de datos ──────────────────────────────────────
			Map<String, Object> valAnterior = new HashMap<>();
			Map<String, Object> valActual   = new HashMap<>();

			if ("i".equals(accion)) {

				 // INSERT → solo valores actuales
			    if (parameters == null || parameters.isEmpty()) {
			        log.warn("[Auditoria] Sin datos para {} en {}. Se omite.", accionR, tabla);
			        return;
			    }

			    for (Map.Entry<String, Object> entry : parameters.entrySet()) {

			        String campo = entry.getKey();

			        if (campo != null && campo.startsWith("KEY_")) {
			            continue;
			        }

			        Object valorNormalizado = normalizar(entry.getValue());

			        if (!esVacio(valorNormalizado)) {
			            valActual.put(campo, valorNormalizado);
			        }
			    }

			} else if ("e".equals(accion)) {

			    // DELETE → solo valores anteriores
			    if (parametrosAntes == null || parametrosAntes.isEmpty()) {
			        log.warn("[Auditoria] Sin datos anteriores para {} en {}. Se omite.", accionR, tabla);
			        return;
			    }

			    for (Map.Entry<String, Object> entry : parametrosAntes.entrySet()) {

			        String campo = entry.getKey();

			        if (campo != null && campo.startsWith("KEY_")) {
			            continue;
			        }

			        Object valorNormalizado = normalizar(entry.getValue());

			        if (!esVacio(valorNormalizado)) {
			            valAnterior.put(campo, valorNormalizado);
			            valActual.put(campo, "null");
			        }
			    }
			} else if ("m".equals(accion)) {

				// UPDATE: calcular cambios; si no hay → salir
				Map<String, Map<String, Object>> cambios = obtenerCambios(parametrosAntes, parameters);

				if (cambios.isEmpty()) {
					return; // no hubo cambios reales
				}

				for(String campo : cambios.keySet()) {
					valAnterior.put(campo, cambios.get(campo).get("antes"));
					valActual.put(campo,   cambios.get(campo).get("despues"));
				}
			}
			// ─────────────────────────────────────────────────────────────────────

			String modulo     = SysmanFunciones.padl(SessionUtil.getModulo(), 3, "0");
			String codEntidad = SessionUtil.getCompaniaIngreso().getCodigo() + "_" + SessionUtil.getCompaniaIngreso().getSigla();
			String nomEntidad = SessionUtil.getCompaniaIngreso().getNombre();
			String ip         = obtenerIpCliente();
			String fechaActual = getSdf().format(new Date());
			String pcName     = System.getenv("COMPUTERNAME");
			if (pcName == null) {
				pcName = System.getenv("HOSTNAME");
			}

			Entidad entidad = new Entidad();
			entidad.setNombre(nomEntidad);
			entidad.setCodigo(codEntidad);
			entidad.setFechaCreacion(fechaActual);

			Procesos proceso = new Procesos();
			proceso.setNombre(accionR + " " + tabla);
			proceso.setCodigo(codigo + "_" + accionR + "_" + tabla);
			proceso.setCodCompania(SessionUtil.getCompaniaIngreso().getCodigo());
			proceso.setCodEntidad(codEntidad);
			proceso.setCodAplicacion(modulo);
			proceso.setNomAplicacion(nombreModulo(SessionUtil.getModulo()));
			proceso.setAuditable(true);
			proceso.setFechaCreacion(fechaActual);

			Auditoria auditoria = new Auditoria();
			auditoria.setAccion(accionR);
			auditoria.setUsuario(SessionUtil.getUser().getCodigo());
			auditoria.setIp(ip);
			auditoria.setEntidad(entidad);
			auditoria.setProcesosDto(proceso);
			auditoria.setEquipo(pcName);
			auditoria.setFechaCreacion(fechaActual);
			auditoria.setReferencia(construirReferencia(llaves));
			auditoria.setValAnterior(valAnterior);
			auditoria.setValActual(valActual);

			enviarAuditoria(auditoria);

		} catch (Exception e) {
			log.error("Error auditoría " + accion, e);
		}
	}

	/**
	 * Normaliza los valores para comparación en auditoría.
	 *
	 * Convierte:
	 * - Date y Timestamp a formato dd/MM/yyyy HH:mm:ss
	 * - Boolean a -1 o 0
	 * - Strings de fecha a formato estándar
	 * - Números a String
	 *
	 * @param valor valor a normalizar
	 * @return valor normalizado
	 */
	private Object normalizar(Object valor) {

		if (valor == null) {
			return null;
		}

		try {
			// Date
			if (valor instanceof Date) {
				return getSdf().format((Date) valor);
			}
			// Timestamp
			if (valor instanceof Timestamp) {
				return getSdf().format(new Date(((Timestamp) valor).getTime()));
			}
			// String que puede ser fecha
			if (valor instanceof String) {

				String v = ((String) valor).trim();

				if (v.isEmpty()) {
					return null;
				}

				// formato: Mon Apr 01 00:00:00 COT 2024
				try {
					SimpleDateFormat f1 =
							new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
					Date fecha = f1.parse(v);
					return getSdf().format(fecha);
				} catch (Exception ignored) {}

				// formato ISO: 2026-04-08T05:00:00.000Z
				try {
					SimpleDateFormat f2 =
							new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
					Date fecha = f2.parse(v);
					return getSdf().format(fecha);
				} catch (Exception ignored) {}

				// Boolean como texto
				if ("true".equalsIgnoreCase(v)) {
					return "-1";
				}
				if ("false".equalsIgnoreCase(v)) {
					return "0";
				}

				return v;
			}

			// Boolean real
			if (valor instanceof Boolean) {
				return ((Boolean) valor) ? "-1" : "0";
			}

			// Números
			if (valor instanceof Number) {
				return String.valueOf(valor);
			}

			return String.valueOf(valor);

		} catch (Exception e) {
			return String.valueOf(valor);
		}
	}

	private boolean esVacio(Object valor) {

		if (valor == null) {
			return true;
		}

		if (valor instanceof String) {
			return ((String) valor).trim().isEmpty();
		}

		return false;
	}

	protected boolean auditaOperacion(String tabla, String operacion) throws SystemException {

		// FASE 1 Validación parámetro
		parametro = JsfUtil.obtenerParametrosGeneral(GeneralParametrosEnum.MANEJA_PROCESO_AUDITORIA.getName());

		// Si el parámetro existe y está en NO, no auditar
		if ("NO".equalsIgnoreCase(parametro)) {
			return false;
		}

		// FASE 2: consulta a tabla AUDITORIA_TABLAS
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), SessionUtil.getCompania());
		param.put(GeneralParameterEnum.NOMBRE_TABLA.getName(), tabla);

		try {
			Registro aux = RegistroConverter.toRegistro(
					requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(SERVICIO_AUDITORIA_TABLAS)
							.getUrl(),
							param));

			// No existe configuración para la tabla
			if (aux == null || aux.getCampos() == null) {
				return false;
			}

			Boolean audInsert = (Boolean) aux.getCampos().get("IND_INSERTAR");
			Boolean audUpdate = (Boolean) aux.getCampos().get("IND_EDITAR");
			Boolean audDelete = (Boolean) aux.getCampos().get("IND_ELIMINAR");

			// Normalizar null - false
			audInsert = audInsert != null && audInsert;
			audUpdate = audUpdate != null && audUpdate;
			audDelete = audDelete != null && audDelete;

			switch (operacion) {
			case "CREAR":
				return audInsert;
			case "ACTUALIZAR":
				return audUpdate;
			case "ELIMINAR":
				return audDelete;
			default:
				return false;
			}

		} catch (SystemException e) {
			log.error("Error en auditoría para tabla: {}", tabla, e);
			return false;
		}
	}

	/**
	 * Envía la información de auditoría al servicio externo.
	 *
	 * Convierte el objeto Auditoria a JSON y lo envía vía HTTP.
	 * No lanza excepciones para no afectar el CRUD principal.
	 *
	 * @param auditoria objeto de auditoría a enviar
	 */
	protected void enviarAuditoria(Auditoria auditoria) {
	    try {

	        parametro = JsfUtil.obtenerParametrosGeneral(GeneralParametrosEnum.URL_REGISTRO_AUDITORIA.getName());

	        if (parametro == null || parametro.trim().isEmpty()) {
	            log.error("[Auditoria] No existe URL de auditoria configurada");
	            return;
	        }

	        Gson gson = new Gson();
	        String json = gson.toJson(auditoria);

	        log.info("[Auditoria] Enviando auditoria...");
	        log.info("[Auditoria] JSON: {}", json);

	        String response = processRequestJson(
	                parametro,
	                json,
	                HttpMethodEnum.PUT
	        );

	        if (response != null && !response.isEmpty()) {
	            log.info("[Auditoria] Respuesta servicio: {}", response);
	        } else {
	            log.warn("[Auditoria] Servicio respondió sin contenido");
	        }

	    } catch (IOException e) {
	        log.error("[Auditoria] Error de conexión con el servicio de auditoria", e);
	    }catch (Exception e) {
	        log.error("[Auditoria] Error inesperado enviando auditoria", e);
	    }
	}

	public String obtenerIpCliente() {
		HttpServletRequest request = (HttpServletRequest) 
				FacesContext.getCurrentInstance().getExternalContext().getRequest();

		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	protected Registro obtenerDatosAntesContinuo(String tabla,	Map<String, Object> llaves) {
		Registro resultado = new Registro();
		try {
			StringBuilder where = new StringBuilder();
			if (llaves != null && !llaves.isEmpty()) {
				boolean primero = true;

				for (Map.Entry<String, Object> entry : llaves.entrySet()) {
					String campo = entry.getKey().startsWith("KEY_")
							? entry.getKey().substring(4)
									: entry.getKey();
					if (!primero) {
						where.append(" AND ");
					}
					where.append(campo).append(" = ");
					Object value = entry.getValue();

					if (value instanceof Number) {
						where.append(value);
					} else if (value instanceof Date) {
						String fecha = SysmanFunciones.convertirAFechaCadena((Date) value);
						where.append("TO_DATE('")
						.append(fecha)
						.append("','DD/MM/YYYY HH24:MI:SS')");
					} else {
						String texto = String.valueOf(value)
								.replace("'", "''");
						where.append("'")
						.append(texto)
						.append("'");
					}
					primero = false;
				}
			}

			Map<String, Object> param = new TreeMap<>();
			param.put("UN_TABLA", tabla);
			param.put("UN_WHERE", where.toString());
			String whereFinal = where.toString().replace("'", "''");
			String cadena = JsfUtil.obtenerRegistroJson(tabla, whereFinal);
			if (cadena != null && !cadena.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String, Object>> lista =
						mapper.readValue(cadena, List.class);
				if (!lista.isEmpty()) {
					resultado.setCampos(new HashMap<>(lista.get(0)));
				}
			}

		} catch (Exception e) {
			log.error("Error en auditoría para tabla: {}", tabla, e);
		}
		return resultado;
	}

	public String construirReferencia(Map<String, Object> llaves) {

		if (llaves == null || llaves.isEmpty()) {
			return "";
		}

		StringBuilder referencia = new StringBuilder();
		for (Map.Entry<String, Object> entry : llaves.entrySet()) {

			String key = entry.getKey();
			Object value = entry.getValue();

			if (value == null) {
				continue;
			}

			key = key.replace("KEY_", "");

			String valorFormateado;

			if (value instanceof Date) {
				valorFormateado = getSdf().format((Date) value);
			} 
			else if (value instanceof String && key.toUpperCase().contains("FECHA")) {
				try {
					SimpleDateFormat formatoOriginal =
							new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

					Date fecha = formatoOriginal.parse(value.toString());
					valorFormateado = getSdf().format(fecha);
				} catch (Exception e) {
					valorFormateado = value.toString().trim();
				}
			} 
			else {
				valorFormateado = value.toString().trim();
			}

			referencia.append(key)
			.append(": ")
			.append(valorFormateado)
			.append(" / ");
		}

		if (referencia.length() > 3) {
			referencia.setLength(referencia.length() - 3);
		}

		return referencia.toString();
	}

	/**
	 * Ejecuta una petición HTTP enviando contenido JSON.
	 *
	 * Maneja códigos HTTP, respuesta del servicio y logs.
	 *
	 * @param urlString URL del servicio
	 * @param jsonContent contenido JSON
	 * @param httpMethodEnum método HTTP
	 * @return respuesta del servicio
	 * @throws IOException error de conexión
	 */
	private static String processRequestJson(
	        String urlString,
	        String jsonContent,
	        HttpMethodEnum httpMethodEnum) throws IOException {

	    URL url = new URL(urlString);

	    HttpURLConnection connection =
	            (HttpURLConnection) url.openConnection();

	    connection.setConnectTimeout(15000);
	    connection.setReadTimeout(15000);
	    connection.setDoOutput(true);
	    connection.setRequestMethod(httpMethodEnum.name());

	    connection.setRequestProperty("Content-Type", "application/json");
	    connection.setRequestProperty("Accept", "application/json");

	    log.info("[Auditoria] URL: {}", urlString);
	    log.info("[Auditoria] Metodo: {}", httpMethodEnum.name());

	    try (OutputStream os = connection.getOutputStream()) {
	        os.write(jsonContent.getBytes(StandardCharsets.UTF_8));
	        os.flush();
	    }

	    int responseCode = connection.getResponseCode();

	    log.info("[Auditoria] Response Code: {}", responseCode);

	    InputStream inputStream;

	    if (responseCode >= 200 && responseCode < 300) {
	        inputStream = connection.getInputStream();
	    } else {
	        inputStream = connection.getErrorStream();

	        log.error("[Auditoria] Error HTTP {} al consumir servicio", responseCode);
	    }

	    StringBuilder response = new StringBuilder();

	    if (inputStream != null) {

	        try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

	            String line;

	            while ((line = reader.readLine()) != null) {
	                response.append(line);
	            }
	        }
	    }

	    connection.disconnect();

	    String responseText = response.toString();

	    if (responseCode >= 400) {
	        log.error("[Auditoria] Respuesta de error: {}", responseText);
	    } else {
	        log.info("[Auditoria] Respuesta: {}", responseText);
	    }

	    return responseText;
	}

	private  String nombreModulo(String modulo) {
		String nombre = "";
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(GeneralParameterEnum.MODULO.getName(), modulo);
			Registro registro;


			registro = RegistroConverter
					.toRegistro(requestManager.get(UrlServiceUtil.getUrlBeanById(SERVICIO_MODULO).getUrl(), parametros));
			if (registro != null) {
				nombre = String.valueOf(registro.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
			}
		} catch (SystemException e) {
			log.error("Error al obtener el nombre del modulo.", e);
		}
		return nombre;
	}

	/**
	 * Compara los valores anteriores y actuales de un registro
	 * y devuelve únicamente los campos que realmente cambiaron.
	 *
	 * - Omite campos que comienzan por KEY_
	 * - Omite MODIFIED_BY y DATE_MODIFIED
	 * - Normaliza fechas, booleanos y números
	 * - Solo compara lo que viene en "despues"
	 *
	 * @param antes valores anteriores del registro
	 * @param despues valores actuales del registro
	 * @return mapa con cambios detectados
	 */
	protected Map<String, Map<String, Object>> obtenerCambios(
			Map<String, Object> antes,
			Map<String, Object> despues) {

		Map<String, Map<String, Object>> cambios = new HashMap<>();

		if (despues == null || despues.isEmpty()) {
			return cambios;
		}

		for (Map.Entry<String, Object> entry : despues.entrySet()) {

			String campo = entry.getKey();

			// omitir KEY_
			if (campo != null && campo.startsWith("KEY_")) {
				continue;
			}

			// campos que no se auditan
			if ("MODIFIED_BY".equalsIgnoreCase(campo)
					|| "DATE_MODIFIED".equalsIgnoreCase(campo)) {
				continue;
			}

			Object valorDespues = normalizar(entry.getValue());
			Object valorAntes = antes != null ? normalizar(antes.get(campo)) : null;

			// ambos vacíos
			if (esVacio(valorAntes) && esVacio(valorDespues)) {
				continue;
			}

			// no hubo cambio
			if (Objects.equals(valorAntes, valorDespues)) {
				continue;
			}

			Map<String, Object> detalle = new HashMap<>();

			if (!esVacio(valorAntes)) {
				detalle.put("antes", valorAntes);
			}

			if (!esVacio(valorDespues)) {
				detalle.put("despues", valorDespues);
			}

			// solo agrega si realmente hubo cambio
			if (!detalle.isEmpty()) {
				cambios.put(campo, detalle);
			}
		}

		return cambios;
	}

	private SimpleDateFormat getSdf() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}
}
