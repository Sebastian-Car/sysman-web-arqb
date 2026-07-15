package com.sysman.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import co.com.sysman.comun.excepcion.NegocioExcepcion;

/**
 * Permite
 * @author jhernandez
 *
 */
public class HttpPostMultipart {
	private final String boundary;
	private static final String LINE = "\r\n";
	private static final String CONTENTYPE = "Content-Type";
	private static final String MULTIPART_BOUNDARY = "multipart/form-data; boundary=";
	private static final String CONTENT_FORMDATA = "Content-Disposition: form-data; name=\"";
	private static final String CONTENT_CHARSET = "Content-Type: text/plain; charset=";
	private static final String CONTENT_TRANSFER = "Content-Transfer-Encoding: binary";
	private static final String CONTENT_TYPE_E = "Content-Type: ";
	private static final String D_GUION = "--";
	private HttpURLConnection httpConn;
	private OutputStream outputStream;
	private PrintWriter writer;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HttpPostMultipart.class);

	/**
	 * Inicicializa una nueva peticion HTTP POST con el contenido asignado to
	 * multipart/form-data
	 *
	 * @param requestURL
	 * @param charset
	 * @param headers
	 * @throws IOException
	 */
	public HttpPostMultipart(String requestURL, Map<String, String> headers) throws IOException {
		boundary = UUID.randomUUID().toString();
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		httpConn.setRequestProperty(CONTENTYPE, MULTIPART_BOUNDARY + boundary);
		if (headers != null && headers.size() > 0) {
			Iterator<String> it = headers.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headers.get(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
	}

	/**
	 * Adiciona a la petición el campo de forma (form field)
	 *
	 * @param name  field name
	 * @param value field value
	 */
	public void addFormField(String name, String value) {
		writer.append(D_GUION + boundary).append(LINE);
		writer.append(CONTENT_FORMDATA + name + "\"").append(LINE);
		writer.append(CONTENT_CHARSET + StandardCharsets.UTF_8).append(LINE);
		writer.append(LINE);
		writer.append(value).append(LINE);
		writer.flush();
	}

	/**
	 * Agrega a la petición la seccion del file
	 *
	 * @param fieldName
	 * @param uploadFile
	 * @throws IOException
	 * @throws NegocioExcepcion
	 */
	public void addFilePart(String fieldName, File uploadFile) throws IOException, NegocioExcepcion {
		FileInputStream inputStream = new FileInputStream(uploadFile);
		try {
			String fileName = uploadFile.getName();
			writer.append(D_GUION + boundary).append(LINE);
			writer.append(CONTENT_FORMDATA + fieldName + "\"; filename=\"" + fileName + "\"")
					.append(LINE);
			writer.append(CONTENT_TYPE_E + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
			writer.append(CONTENT_TRANSFER).append(LINE);
			writer.append(LINE);
			writer.flush();

			int size = (int) uploadFile.length();
			byte[] buffer = new byte[size];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
			inputStream.close();
			writer.append(LINE);
			writer.flush();
		} catch (Exception e) {
			LOG.error("Error en construcción de peticion form data <<addFilePart>>", e);
			throw new NegocioExcepcion("Error en construcción de peticion form data <<addFilePart>>");
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Completa la petición y recibe la respuesta del servidor.
	 *
	 * @return String como respuesta en caso que el servidor retorne un estado OK,
	 *         de otra forma una excepcion es lanzada.
	 * @throws IOException
	 * @throws NegocioExcepcion
	 */
	public String finish() throws IOException, NegocioExcepcion {
		String response = "";
		writer.flush();
		writer.append("--" + boundary + "--").append(LINE);
		writer.close();
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = httpConn.getInputStream().read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			response = result.toString(StandardCharsets.UTF_8.toString());
			httpConn.disconnect();
		} else {
			LOG.error("El servidor retorno un estado de no conexión: {}", status + httpConn.getURL().toString() );
			throw new NegocioExcepcion("El servidor retorno un estado de no conexión: {}");
			
		}
		return response;
	}
}