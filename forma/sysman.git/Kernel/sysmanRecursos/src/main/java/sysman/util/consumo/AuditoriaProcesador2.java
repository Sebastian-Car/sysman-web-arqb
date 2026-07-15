package sysman.util.consumo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import co.com.sysman.comun.excepcion.NegocioExcepcion;
import co.com.sysman.conector.IConector;
import co.com.sysman.utilidades.RespuestaApi;
import sysman.util.consumo.enums.EnumCodAuditoria;
import sysman.util.consumo.enums.EnumParametros;
import sysman.util.consumo.pojo.PojoAuditoria;

public class AuditoriaProcesador2 {

	private Logger log = Logger.getLogger(AuditoriaProcesador2.class.getName());
	private PojoAuditoria contexto;
	private Object resultado;
	private ClientResponse response;
	private Client client;
	protected RequestManager requestManager = new RequestManager();
	/**
	 * Valor de respuesta correcta
	 */
	private static final String OK = "OK";
	/**
	 * Objeto estatico para reemplazo
	 */
	private static final String CODCOMPANIA = "#codCompania#";
	private static final String CODPROCESO = "#codProceso#";
	private static final String CODENTIDAD = "#codEntidad#";

	public void validarDatos(PojoAuditoria dato) throws NegocioExcepcion {
		contexto = dato;

		validarCampo(contexto.getCodCompania(), EnumParametros.CODCOMPANIA.getValue());
		validarCampo(contexto.getCodEntidad(), EnumParametros.CODENTIDAD.getValue());
		validarCampo(contexto.getCodproceso(), EnumParametros.CODPROCESO.getValue());
		validarCampo(contexto.getUsuario(), EnumParametros.USUARIO.getValue());
		validarCampo(contexto.getEquipo(), EnumParametros.EQUIPO.getValue());

		if (contexto.getIp() == null || contexto.getIp().equals(""))
			try {
				contexto.setIp(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		
		procesarDatos();
	}

	private void procesarDatos() throws NegocioExcepcion {
		Gson gson = new Gson();
		// Validar la conexion con la base de datos de auditoria
		RespuestaApi resp = ejecutarServicio(
				obtenerUrlAuditoria(EnumParametros.SERV_CONEXION.getValue()), null,
				IConector.METHOD_GET, EnumParametros.SERV_CONEXION.getValue());

		if (resp.getCodigo() == 0 && resp.getMensaje().equals(OK)) {
			// Verificar si el proceso a auditar esta activo
			String urlCompleta = obtenerUrlAuditoria(EnumParametros.SERV_OBTENER_PROCE.getValue())
					.replace(CODCOMPANIA, contexto.getCodCompania())
					.replace(CODPROCESO, contexto.getCodproceso())
					.replace(CODENTIDAD, contexto.getCodEntidad());

			RespuestaApi resProAudit = ejecutarServicio(urlCompleta, null, IConector.METHOD_GET,
					EnumParametros.SERV_OBTENER_PROCE.getValue());

			if (resProAudit.getCodigo() == 0) {
				try {
					// Verifica si el proceso esta activo para ser auditable
					JSONArray list = (JSONArray) resProAudit.getCuerpo();
					JSONObject obj = (JSONObject) list.get(0);

					boolean auditable = Boolean.parseBoolean(String.valueOf(obj.get(EnumParametros.AUDITABLE.getValue())));

					if (auditable) {
						// Ejecutar registro de historico de auditoria
						RespuestaApi respAudit = ejecutarServicio(
								obtenerUrlAuditoria(EnumParametros.SERV_INSRT_AUDIT.getValue()),
								gson.toJson(contexto), IConector.METHOD_POST,
								EnumParametros.SERV_INSRT_AUDIT.getValue());

						resultado = respAudit.getCuerpo();
					} else {
						resultado = auditable;
					}
				} catch (JSONException e1) {
					log.info("Error consultando datos, motivo "+ e1);
				}
			}
		}
	}

	public Object respuesta() {
		return resultado;
	}

	private void validarCampo(Object campo, String nombreCampo) throws NegocioExcepcion {
		if (campo == null || campo.toString().equals("")) {
			throw new NegocioExcepcion("El campo {}, no tiene valor".replace("{}", nombreCampo));
		}
	}
	/**
	 * Api conector
	 */
	public RespuestaApi ejecutarServicio(String url, Object body, String method, String servicio) throws NegocioExcepcion {
		RespuestaApi respApi = new RespuestaApi();
		String typeRequest = MediaType.APPLICATION_JSON_TYPE.toString();
		String nameHeader = "Authorization";
		Object objetoEntrada = body;
		client = Client.create();
		
		switch (method) {
			case IConector.METHOD_GET:
				response = client.resource(url).accept(MediaType.APPLICATION_JSON_TYPE)
				.header(nameHeader, null).get(ClientResponse.class);
				break;
			case IConector.METHOD_POST:
				response = client.resource(url).accept(MediaType.APPLICATION_JSON_TYPE.toString())
				.type(typeRequest).header(nameHeader, null)
				.post(ClientResponse.class, objetoEntrada);
				break;
			default:
				break;
		}
		
		if (response.getStatusInfo().getStatusCode() != Status.OK.getStatusCode()) {
			String msg = new StringBuilder().append("No se logro consumir api requerida por ICONECTOR - ")
					.append(response.getStatusInfo().getStatusCode()).append(" ")
					.append(response.getStatusInfo().toString()).toString();
			throw new NegocioExcepcion(msg);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntityInputStream())));
		String output;
		StringBuilder response1 = new StringBuilder();

		try {
			while ((output = br.readLine()) != null) {
				response1.append(output);
			}
			
			JSONObject resJson = new JSONObject(response1.toString());
			respApi.setCodigo(Integer.parseInt(String.valueOf(resJson.get("codigo"))));
			respApi.setCuerpo(resJson.get("mensaje"));
			respApi.setCuerpo(resJson.get("cuerpo"));
			
			return respApi;
		} catch (JSONException | IOException e) {
			String error = "Error en auditoria IConector servicio " + servicio;
			log.info("Error ejecutar auditoria Motivo " + error);
			throw new NegocioExcepcion(error);
		}
	}

	public String obtenerUrlAuditoria(String codServicio) {
		String valor = "";
		
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.NOMBRE.getName(), codServicio);

			Registro audit = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID("1905001").getUrl(),param));
			
			if (audit != null) {
				valor = audit.getCampos().get("URL").toString();
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
     
		return valor;
	}
	
    public String obtenerCodProceso(String clase, String concepto, String tipo) {
    	String codProceso = "0";
    	
    	if ((clase.equals("E") && concepto.equals("CR")) || clase.equals("CR")) {
    		switch (tipo) {
    		case "N":
    			codProceso = EnumCodAuditoria.CORRECCION_VALOR_ELEMENTOS_INMUEBLES.getValue();
    			break;
    		case "M":
    			codProceso = EnumCodAuditoria.CORRECCION_VALOR_ELEMENTOS_CONTROL.getValue();
    			break;
    		case "D":
    			codProceso = EnumCodAuditoria.CORRECCION_VALOR_ELEMENTOS_DEVOLUTIVOS.getValue();
    			break;
    		default:
    			break;
    		}
    	} else if ((clase.equals("E") && concepto.equals("R")) || clase.equals("R")) {
    		switch (tipo) {
    		case "D":
    			codProceso = EnumCodAuditoria.REINTEGRO_ELEMENTOS_DEVOLUTIVOS.getValue();
    			break;
    		case "M":
    			codProceso = EnumCodAuditoria.REINTEGRO_ELEMENTOS_CONTROL.getValue();
    			break;
    		case "N":
    			codProceso = EnumCodAuditoria.REINTEGRO_ELEMENTOS_INMUEBLES.getValue();
    			break;
    		default:
    			break;
    		}
    	} else {
	    	try {
				Map<String, Object> param = new HashMap<>();
				param.put(GeneralParameterEnum.CLASE.getName(), clase);
				param.put(GeneralParameterEnum.TIPO.getName(), tipo);
	
				Registro proceso = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID("1912001").getUrl(),param));
				
				if (proceso != null) {
					codProceso = proceso.getCampos().get("PROCESO").toString();
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}
    	}
     
		return codProceso;
    }
	
    public String obtenerCodEntidad(String compania) {
		String codEntidad = "0";
		
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(), compania);

			Registro entidad = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID("59028").getUrl(),param));
			
			if (entidad != null) {
				codEntidad = entidad.getCampos().get("COD_ENTIDAD").toString();
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
     
		return codEntidad;
	}
}
