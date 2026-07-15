package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Named;

import org.jboss.marshalling.ContextClassResolver;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.enums.PqrProcesadorUrlEnum;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.ConsultaPqr;
import com.sysman.rest.logica.Pqr;
import com.sysman.rest.logica.PqrAnexo;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.workflow.gen.ejb.EjbWorkflowCeroRemote;
import com.sysman.workflow.gen.ejb.impl.EjbWorkflowCero;

/**
 * Procesador que permite manejar PQR en modelo Worflow
 *
 * @version 1.0, 13/12/2019
 * @author mochoa
 * 
 * 
 * 
 * 
 */
@Named("ProcesaWorflowPqr")
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesaWorflowPqr extends Procesador<Pqr, RespuestaApi> {
	@EJB
	private static EjbWorkflowCeroRemote wor = new EjbWorkflowCero();

	/**
	 * Propiedad para objeto serializable serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constante para evaluar el tipo tramite PQR
	 */
	public static final String TIPO_TRAMITE = "1";
	/**
	 * Constante para evaluar el tipo tramite PQR
	 */
	public static final String PROCESO_PQR = "00000";
	/**
	 * Constante para quemar el valor de la etapa 1 de pqr
	 */
	public static final String NODO_ORIGEN = "0000";
	/**
	 * Constante para quemar el valor de la etapa 1 de pqr
	 */
	public static final String NODO_DESTINO = "0000";
	/**
	 * Constante para quemar usuario de workflow
	 */
	public static final String USUARIO = "CIUDADANO";
	
	public static final String ANONIMO = "REGISTRO ANONIMO";
	
	/**
	 * Objeto
	 */
	private Registro rsPqr;

	/**
	 * Abstraccion creada para emitir respuesta del procesador
	 */
	RespuestaApi respuestaAPi = new RespuestaApi();
	/**
	 * Request manager
	 */
	RequestManager requestManager = new RequestManager();

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;


	/**
	 * LOG Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProcesaWorflowPqr.class);

	/**
	 * Lista los errores que se controlan del json
	 */
	List<String> errores = new ArrayList<>();

	/**
	 * Implementacion del EJB de workflow para llamar al procesoatramitar con el fin
	 * de PQR
	 */
	@EJB
	private static EjbWorkflowCero worflowPqr = new EjbWorkflowCero();

	private ConsultaPqr respuestaPqr = new ConsultaPqr();

	String anexo1 = "";
	String anexo2 = "";
	String anexo3 = "";
	String rutAplicacion = "";

	/**
	 * Para implementar hilos con la clase <code>Runnable</code>,
	 */
	@Override
	public void run() {
		// Sin implementar
	}

	/**
	 * Acciones que se ejecutan antes de procesar una petición.
	 * 
	 * @throws NegocioExcepcion
	 */
	@Override
	protected void preProcesar() throws NegocioExcepcion {
		boolean obligaCampos = true;
		try {
			String compania = contexto.getCompania();
			 obligaCampos = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
					compania,"ENVIAR CAMPOS OBLIGATORIOS PQRS",
					"35",new Date(),true ),"NO"));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if (contexto.getTipoConsumo().equals("1")) {
		  if(contexto.getAnonimo()== null || !contexto.getAnonimo()) {
			  
			if (contexto.getTipoTramite().isEmpty() && contexto.getCorreo().isEmpty()
					&& contexto.getDireccion().isEmpty() && contexto.getCedula().isEmpty()
					&& contexto.getNombre().isEmpty() && contexto.getDescripcion().isEmpty()) {
				errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r")).append(
						"NO se puede registrar una PQR debido a que no se han enviado los parametros necesarios")
						.toString());
			}
			if (contexto.getCedula().isEmpty() && contexto.getCorreo().isEmpty() && contexto.getDireccion().isEmpty()
					&& contexto.getCompania().isEmpty() && contexto.getDescripcion().isEmpty()
					&& contexto.getNombre().isEmpty()) {
				errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
						.append("No ha completado los datos minimos para registrar una PQR").toString());

			}
			if(obligaCampos) {
				if (((contexto.getTipoPoblacion().isEmpty() || contexto.getTipoPoblacion().equals("102")) && contexto.getDesTipoPoblacion().isEmpty()) ||
				    ((contexto.getOcupacion().isEmpty() || contexto.getOcupacion().equals("101")) && contexto.getDesOcupacion().isEmpty())) {
					errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
							.append("No ha completado los datos minimos para registrar una PQR").toString());
				}
			}
		  }
		} else {
			// agregar cedula
			if (null == contexto.getNumRadicado()) {
				errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
						.append("No puede consultar una PQR si no tiene un numero de radicado").toString());
			}
		}
		if (errores.isEmpty() || errores == null) {

			esValido = true;
		} else {
			esValido = false;
			respuestaAPi.setCuerpo(errores);
			respuestaAPi.setCodigo(10006);
			throw new NegocioExcepcion(errores.toString());
		}

	}

	/**
	 * Acciones que se ejecutan despues de procesar una petición.
	 */
	@Override
	protected void posProcesar() {
		LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(), resultado);
	}

	/**
	 * Acciones que ejecuta el procesador o comando concreto.
	 */
	@Override
	public void ejecutar() throws NegocioExcepcion {
		BigInteger numeroRad;
		String compania = contexto.getCompania();
		String modulo = "35";
		try {

			BigInteger bigIN = new BigInteger(TIPO_TRAMITE);
			if (contexto.getTipoConsumo().equals("1")) {
				Map<String, Object> param1 = new HashMap<>();
				
				//Creacion de tramite PQRSDF
				rsPqr = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(PqrProcesadorUrlEnum.URL5800.getValue()).getUrl(), param1));
				rutAplicacion = rsPqr.getCampos().get("RUTA_DOCUMENTOS").toString();

				regAnonimo();
				boolean anonimo = contexto.getAnonimo()==null?false:contexto.getAnonimo();
				numeroRad = worflowPqr.workflow_pqrs(contexto.getCompania(), PROCESO_PQR, bigIN, NODO_ORIGEN, USUARIO,
						true, TIPO_TRAMITE, contexto.getCedula(), contexto.getNombre(), contexto.getDireccion(),
						contexto.getDescripcion(), contexto.getCorreo(), anexo1, anexo2, anexo3,
						contexto.getTipoTramite(),contexto.getTelefono(),contexto.getGenero(),contexto.getCodigoTramite(), 
						contexto.getRangoEdad(), contexto.getTipoPersona(), contexto.getTipoPoblacion(), contexto.getVulnerabilidad(),
						contexto.getOcupacion(), contexto.getEscolaridad(), contexto.getDesTipoPoblacion(), contexto.getDesOcupacion(), anonimo);
				contexto.setNumRadicado(numeroRad);

				Map<String, Object> param = new HashMap<>();

				param.put("COMPANIA", contexto.getCompania());
				param.put("MODIFIED_BY", USUARIO);			
				//param.put(GeneralParameterEnum.UN_NUMERO.getName(), contexto.getNumRadicado());
				param.put("TRAMITE", contexto.getNumRadicado());
				param.put("PROCESO", PROCESO_PQR);
				param.put("TIPO_TRAMITE", contexto.getCodigoTramite());
				//param.put(GeneralParameterEnum.TRAMITE.getName(), TIPO_TRAMITE);
				int i = 0;
				String nodoVariable = "";
				String variable="";
				for (PqrAnexo anexo : contexto.getAnexos()) {
					i++;
					if (null != anexo.getAnexo() && !anexo.getAnexo().isEmpty() && null != anexo.getNombre()
							&& !anexo.getNombre().isEmpty() && null != anexo.getValor()
							&& !anexo.getValor().isEmpty()) {
						anexo1 = generarNombreArchivo(rutAplicacion, anexo, contexto);
						param.put("ADJUNTO", anexo1);
						
						if (i == 1) {
							//nodoVariable = "60";
							variable="anexouno";
						}
						if (i == 2) {
							//nodoVariable = "070";
							variable="anexodos";
						}
						if (i == 3) {
							//nodoVariable = "080";
							variable="anexotres";
						}
						//param.put(GeneralParameterEnum.UN_NODO.getName(), nodoVariable);
						param.put("VARIABLE", variable);

						Parameter parameter = new Parameter();
						parameter.setFields(param);
						
						UrlBean urlUpdate = UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(PqrProcesadorUrlEnum.URL1049.getValue());
						requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
					}
				}
				EmailPojo emailPojo = new EmailPojo();

				emailPojo.setFrom("PQRS");
				emailPojo.setTo(contexto.getCorreo());
				emailPojo.setSubject("Radicar PQRS");
				emailPojo.setBody("Estimado(a) <b>" + contexto.getNombre() + "</b>"
						+ "<br></br>Su "+contexto.getTipoTramite()+" ha sido registrada exitosamente a su numero de identificación " + "<b>"
						+ contexto.getCedula() + "</b>" +" con número de radicado <b>#" + numeroRad + "</b>"
						+ "<br></br>Cordialmente, " + contexto.getNombreCompania()
						+ "<br></br><br></br>Nota: Por favor no responder a este correo electrónico ya que es generado de manera automática con el único propósito de enviar notificaciones en las transacciones realizadas.");

				ApiRestClient client = new ApiRestClient();
				client.postClient(emailPojo, contexto.getCompania());
				respuestaAPi.setCuerpo(numeroRad);
			} else {
				//Respuerta de estadpo de tramite pqrsdf
				Map<String, Object> param = new HashMap<>();
				param.put(GeneralParameterEnum.UN_CEDULA.getName(), contexto.getCedula());
				param.put(GeneralParameterEnum.UN_COMPANIA.getName(), contexto.getCompania());
				param.put(GeneralParameterEnum.PROCESO.getName(), PROCESO_PQR);
				param.put(GeneralParameterEnum.UN_NUMERO.getName(), contexto.getNumRadicado());
				param.put(GeneralParameterEnum.TRAMITE.getName(), TIPO_TRAMITE);
				param.put(GeneralParameterEnum.CODIGOTRAMITE.getName(), contexto.getCodigoTramite());
		
				rsPqr = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(PqrProcesadorUrlEnum.URL1045.getValue()).getUrl(), param));
	
				if (rsPqr != null) {
					respuestaPqr.setEtapa(rsPqr.getCampos().get("ETAPA").toString());
					respuestaPqr.setNumRadicado(String.valueOf(rsPqr.getCampos().get("NUMERO")));
					respuestaPqr.setDependencia( rsPqr.getCampos().get("DEPENDENCIA").toString());
					respuestaPqr.setNodoFinal(String.valueOf(rsPqr.getCampos().get("NODOFINAL")).equals("-1")?"true":"false");		
					respuestaPqr.setMensajeParametro(ejbSysmanUtil.consultarParametro(compania,
							"MENSAJE ETAPA FINAL PQRS", modulo,
							new Date(),
							true));
					
					//	respuestaPqr.setNodoFinal((rsPqr.getCampos().get("NODOFINAL").equals("-1")?"true":"false").toString());
					respuestaAPi.setCuerpo(respuestaPqr);
				} else {
					errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
							.append("Los datos enviados para la consulta NO corresponden a un proceso radicado")
							.toString());
					throw new NegocioExcepcion(
							"Los datos enviados para la consulta NO corresponden a un proceso radicado");
				}

			}
			resultado = respuestaAPi;

		} catch (SystemException | IOException e) {
			String rta = e.getMessage();
			if (rta.contains("@#INI#@")) {
				rta = rta.substring(e.getMessage().indexOf("@#INI#@"));
				rta = rta.substring(rta.indexOf("Log:"), rta.indexOf("@#FIN#@"));
			}
			throw new NegocioExcepcion(rta);

		}
	}

	/**
	 * Obtiene el ejecutable o worker.
	 */
	@Override
	public Runnable getEjecutable() {
		return this;
	}

	/**
	 * Retorna resultado esperado posejecución
	 */
	@Override
	public RespuestaApi getResultado() {
		return resultado;
	}

	public static String generarNombreArchivo(String rutApli, PqrAnexo anexo, Pqr contexto)
			throws NegocioExcepcion, IOException {
		FileOutputStream out = null;
		try {

			byte[] bytesArray = Base64.getDecoder().decode(anexo.getValor().getBytes(StandardCharsets.UTF_8));

			String ruta = SysmanFunciones.concatenar("C", contexto.getCompania(), "/", "P", PROCESO_PQR,
					"/", "N", NODO_ORIGEN, "/");
			// VERIFICAR SI EL TIPOTRAMITE ES EL MISMO CONSECUTIVOTRAMITE
			Map<String, Object> keyAdjunto = new LinkedHashMap<>();
			keyAdjunto.put("KEY_TIPO_TRAMITE", TIPO_TRAMITE);
			keyAdjunto.put("KEY_NUMERO_TRAMITE", contexto.getNumRadicado());
			keyAdjunto.put("KEY_CONSECUTIVO_TRAMITE", TIPO_TRAMITE);

			StringBuilder nombreRuta = new StringBuilder();
			StringBuilder rutaRelativa = new StringBuilder();

			nombreRuta.append(rutApli);
			nombreRuta.append(ruta);
			rutaRelativa.append(ruta);

			File directorio = new File(nombreRuta.toString());
			if (!directorio.exists()) {
				if (directorio.mkdirs()) {
					LOG.info("El directorio creado es {}", nombreRuta);
				} else {
					LOG.info("Error al crear el directorio {}", nombreRuta);
					throw new NegocioExcepcion("Error al crear directorio");
				}
			}

			for (Object valor : keyAdjunto.values()) {
				nombreRuta.append(valor);
				nombreRuta.append("_");
				rutaRelativa.append(valor);
				rutaRelativa.append("_");
			}
			nombreRuta.append(anexo.getNombre());
			rutaRelativa.append(anexo.getNombre());

			out = new FileOutputStream(nombreRuta.toString());
			out.write(bytesArray);			

			return rutaRelativa.toString();
		} catch (IOException e) {
			LOG.info("Error al crear anexos >> ", e.getCause());
			throw new NegocioExcepcion("Se presentan problemas al anexar a su PQR");
		} finally {
			if (null != out) {
				out.close();
			}
		}

	}
	
	public void regAnonimo() {
		String compania = contexto.getCompania();
		String modulo = "35";
		try {
			Boolean permiteAnonimos = (ejbSysmanUtil.consultarParametro(compania, "PERMITE ANONIMOS EN REGISTROS PQRS",
					modulo, new Date(), true)).equals("SI");

			if ((contexto.getAnonimo()!= null && contexto.getAnonimo()) && permiteAnonimos) {
				contexto.setNombre(ANONIMO);
				contexto.setCedula(ANONIMO);
				contexto.setCorreo(ANONIMO);
				contexto.setGenero(ANONIMO);
				contexto.setTipoPersona("999");
				contexto.setVulnerabilidad("999");
				contexto.setDesOcupacion("999");
				contexto.setOcupacion("999");
				contexto.setRangoEdad("999");
				contexto.setEscolaridad("999");
				contexto.setDesTipoPoblacion("999");
				contexto.setTipoPoblacion("999");
				contexto.setTelefono(ANONIMO);
				contexto.setDireccion(ANONIMO);
				contexto.setTipoTramite(ANONIMO);
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
}
