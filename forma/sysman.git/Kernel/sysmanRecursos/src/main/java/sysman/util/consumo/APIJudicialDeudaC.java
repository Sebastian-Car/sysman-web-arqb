package sysman.util.consumo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.google.gson.Gson;
import java.lang.reflect.Type;

import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.rest.enums.APIAutoServicioEnum;

import sysman.util.consumo.enums.ServicioEnum;
import sysman.util.consumo.enums.VariableComercioEnum;
import sysman.util.consumo.enums.VariableDeudaEnum;
import sysman.util.consumo.pojo.Comercio;
import sysman.util.consumo.pojo.ComercioConcepto;
import sysman.util.consumo.pojo.DeclaracionComercio;
import sysman.util.consumo.pojo.Predio;
import sysman.util.consumo.pojo.PredioConcepto;
import sysman.util.consumo.pojo.PredioFacturado;
import sysman.util.consumo.pojo.RespuestaApiUnico;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * Clase que sirve como cliente para obtener los datos de deuda de un predio
 * entre un rango de vigencias
 * 
 * @version 1.0, 01/12/2020
 * @author Jos&eacute; Pascual G&oacute;mez Blanco
 *
 */
public class APIJudicialDeudaC {
	protected ResourceBundle idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);

	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(APIJudicialDeudaC.class);

	public static final String SERVICIO_GUARDA_VARIABLE = "184700U";

	public static final String SERVICIO_INSERTA_DEUDA = "184900C";

	public static final String SERVICIO_ELIMINA_DEUDA = "1849001";

	public static final String REFERENCIA_DEFAULT = "-1";

	/**
	 * Para manejar el texto de error del procesador
	 */
	private static final String ERROR = "Error de: <<APIPredialJudicial>> parametros / message  ->> {} ";

	/**
	 * Identifica la compa&ntilde;ia en la que se guarda el resultado y se busca la
	 * url del servicio
	 */
	String compania;
	/**
	 * Identifica el c&oacute;digo del proceso al que se le asigna la deuda
	 */
	String proceso;
	/**
	 * c&oacute;digo del tipo de tr&aacute;mite al que se le asigna la deuda
	 */
	String tipoTramite;

	/**
	 * c&oacute;digo del tr&aacute;mite al que se le asigna la deuda
	 */
	String tramite;
	/**
	 * usuario que ejecuta la operaci&opacute;n
	 */
	String usuario;
	/**
	 * Enumerado que identifica el servicio a ejecutar y sus caracter&iacute;sticas
	 */
	ServicioEnum servicio;

	RequestManager requestManager = new RequestManager();

	/**
	 * Clase que sirve como cliente para obtener los datos de deuda de un predio
	 * entre un rango de vigencias
	 * 
	 * @param compania    : Identifica el c&oacute;digo del proceso al que se le
	 *                    asigna la deuda
	 * @param proceso     : c&oacute;digo del tipo de tr&aacute;mite al que se le
	 *                    asigna la deuda
	 * @param tipoTramite : c&oacute;digo del tr&aacute;mite al que se le asigna la
	 *                    deuda
	 * @param tramite     : c&oacute;digo del tr&aacute;mite al que se le asigna la
	 *                    deuda
	 * @param usuario     : usuario que ejecuta la operaci&opacute;n
	 * @param servicio    :Enumerado que identifica el servicio a ejecutar y sus
	 *                    caracter&iacute;sticas
	 */
	public APIJudicialDeudaC(String compania, String proceso, String tipoTramite, String tramite, String usuario,
			ServicioEnum servicio) {
		super();
		this.compania = compania;
		this.proceso = proceso;
		this.tipoTramite = tipoTramite;
		this.tramite = tramite;
		this.usuario = usuario;
		this.servicio = servicio;
	}

	/**
	 * Permite consumir el servicio configurado y de acuerdo al c&oacute;digo
	 * registra la informaci&oacute;n donde se hace necesaria
	 *
	 * @param proceso     : c&oacute;digo del proceso al que se le asigna la deuda
	 * @param tipoTramite : c&oacute;digo del tipo de tr&aacute;mite al que se le
	 *                    asigna la deuda
	 * @param tramite     : c&oacute;digo del tr&aacute;mite al que se le asigna la
	 *                    deuda
	 * @param servicio    : c&oacute;digo del servicio que se va a ejecutar
	 * @param parametros  : Mapa de par&aacute;metros que se reemplazan en la url
	 * @param usuario     : usuario que ejecuta la operaci&opacute;n
	 * @return
	 * @throws SysmanException : Excepci&pacute;n que sube hasta la forma
	 */
	@SuppressWarnings("unchecked")
	public boolean consultaDeuda(Map<String, Object> parametros) throws SysmanException, SystemException {
		boolean salida = false;
		double vigIni;
		double vigFin;
		/** Codificaci&oacute;n utf-8 */
		try {
			System.setProperty(APIAutoServicioEnum.FILE_ENCODING.getValue(), APIAutoServicioEnum.UTF_8.getValue());
			Field charset = Charset.class.getDeclaredField(APIAutoServicioEnum.DEFAULT_CHARSET.getValue());
			charset.setAccessible(true);
			charset.set(null, null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e2) {
			String msgError = idioma.getString(APIAutoServicioEnum.MSG_ERR_APIDEUDAJUDICIAL_UTF8.getValue());
			LOG.error(ERROR, msgError);
			throw new SysmanException(msgError);
		}

		@SuppressWarnings("rawtypes")
		RespuestaApiUnico res = consultar(parametros);
		eliminarDeuda();
		switch (servicio) {
		case VACIO:
			break;
		case PREDIAL:
			vigIni=  Double.parseDouble((String)parametros.get(VariableDeudaEnum.INICIAL.getName()));
			vigFin=  Double.parseDouble((String)parametros.get(VariableDeudaEnum.FINAL.getName()));
			
			guardaDeudaPredio(res, vigIni, vigFin);
			salida = true;
			break;
		case COMERCIO:			
			vigIni=  Double.parseDouble((String)parametros.get(VariableComercioEnum.INICIAL.getName()));
			vigFin=  Double.parseDouble((String)parametros.get(VariableComercioEnum.FINAL.getName()));
			
			guardaDatosComercio(res, vigIni, vigFin);
			salida = true;
			break;
		}
		return salida;
	}
	
	/**
	 * Permite realizar la consulta de los datos de acuerdo al servicio de la Url y
	 * el modulo que identifica el pojo sobre el cual se carga el tag de cuerpo de
	 * la respuesta
	 * 
	 * @param url
	 * @param modulo
	 * @return Retorna la respuesta general de acuerdo al consumo de servicios de
	 *         arquitectura C
	 * @throws SysmanException
	 */
	@SuppressWarnings("rawtypes")
	private RespuestaApiUnico consultar(Map<String, Object> parametros) throws SysmanException {
		String url = servicio.obtenerUrlGet(compania, parametros);
		String msgError = APIAutoServicioEnum.KEY_VACIO.getValue();
		RespuestaApiUnico respuesta;

		HttpURLConnection connection = null;
		StringBuffer response = null;

		try {
			LOG.info(APIAutoServicioEnum.KEY_LOG_URL.getValue(), url);
			connection = (HttpURLConnection) new URL(url).openConnection();
		} catch (IOException e1) {
			msgError = idioma.getString(APIAutoServicioEnum.MSG_ERR_APIDEUDAJUDICIAL_ABRIR_CONEXION.getValue());
			LOG.error(ERROR, msgError);
			throw new SysmanException(msgError);
		}
		try {
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setRequestProperty(APIAutoServicioEnum.CACHE_CONTROL_MINUS.getValue(),
					APIAutoServicioEnum.NO_CACHE.getValue());
			connection.setRequestProperty(APIAutoServicioEnum.CACHE_CONTROL.getValue(),
					APIAutoServicioEnum.NO_CACHE.getValue());
			connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
					APIAutoServicioEnum.APPLICATIONSJON.getValue());
			connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
					APIAutoServicioEnum.APPLICATIONSJON.getValue());
			connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.GET.getValue());
		} catch (Exception e) {
			if (connection != null) {
				connection.disconnect();
			}
			msgError = idioma.getString(APIAutoServicioEnum.MSG_ERR_APIDEUDAJUDICIAL_HEADER_CONEXION.getValue());
			LOG.error(ERROR, msgError);
			throw new SysmanException(msgError);
		}

		try {
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				msgError = idioma.getString(APIAutoServicioEnum.MSG_ERR_APIDEUDAJUDICIAL_FALLO_HTTP.getValue());
				msgError = new StringBuilder().append(APIAutoServicioEnum.GET.getValue())
						.append(APIAutoServicioEnum.DOS_PUNTOS.getValue()).append(msgError)
						.append(APIAutoServicioEnum.REEMPLZAO_SIG.getValue()).append(connection.getResponseCode())
						.toString();
				LOG.error(ERROR, msgError);
				throw new SysmanException(msgError);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			String output;
			response = new StringBuffer();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
			if (response.toString() == null) {
				msgError = new StringBuilder()
						.append(idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()))
						.append(msgError).append(APIAutoServicioEnum.REEMPLZAO_SIG.getValue()).append(this.getClass())
						.toString();
				LOG.error(ERROR, msgError);
				throw new SysmanException(msgError);
			} else {
				Type listType = servicio.getTipo();
				respuesta = new Gson().fromJson(response.toString(), listType);
				if (respuesta.getCodigo() != 0) {
					msgError = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue());
					msgError = msgError.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuesta.getMensaje());
					msgError = new StringBuilder().append(msgError).append(APIAutoServicioEnum.REEMPLZAO_SIG.getValue())
							.append(this.getClass()).toString();
					LOG.error(ERROR, msgError);
					throw new SysmanException(msgError);
				}
			}
			connection.disconnect();
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			msgError = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue());
			msgError = msgError.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(),
					APIAutoServicioEnum.KEY_VACIO.getValue());
			msgError = new StringBuilder().append(msgError).append(APIAutoServicioEnum.REEMPLZAO_SIG.getValue())
					.append(this.getClass()).toString();
			LOG.error(ERROR, msgError);
			throw new SysmanException(msgError);
		}
		return respuesta;
	}

	/**
	 * Permite guardar los datos en las variables de tr&aacute;mite y en la tabla de
	 * deuda del tr&aacute;mite
	 * 
	 * @param respuesta : Objeto que se desea guardar en el tr&aacute;mite dado
	 * @param vigIni    : Vigencia inicial desde la cual se trae la deuda
	 * @param vigFin    : Vigencia Final hasta la cual se trae la deuda
	 * @throws SystemException Excepci&pacute;n que sube hasta la forma
	 */
	private void guardaDeudaPredio(RespuestaApiUnico<Predio> respuesta, double vigIni, double vigFin)
			throws SystemException {
		Predio predio = respuesta.getCuerpo();
		guardaVariableTramite(VariableDeudaEnum.CODCATASTRAL.getName(), predio.getCodCatastral(), null, null);
		guardaVariableTramite(VariableDeudaEnum.CODEQUIVALENTE.getName(), predio.getCodEquivalente(), null, null);
		guardaVariableTramite(VariableDeudaEnum.TOTAL.getName(), null, predio.getTotal(), null);
		guardaVariableTramite(VariableDeudaEnum.MATRICULAIMOBILIARIA.getName(), predio.getMatriculaInmobiliaria(), null,
				null);
		guardaVariableTramite(VariableDeudaEnum.NIT.getName(), predio.getNit(), null, null);
		guardaVariableTramite(VariableDeudaEnum.DIRECCION.getName(), predio.getDireccion(), null, null);
		guardaVariableTramite(VariableDeudaEnum.NOMBRE.getName(), predio.getNombre(), null, null);
		guardaVariableTramite(VariableDeudaEnum.TELEFONO.getName(), predio.getTelefono(), null, null);
		guardaVariableTramite(VariableDeudaEnum.VIGENCIAINICIAL.getName(), null, vigIni, null);
		guardaVariableTramite(VariableDeudaEnum.VIGENCIAFINAL.getName(), null, vigFin, null);
		int consec = 0;
		for (PredioFacturado fac : predio.getVigencia()) {
			for (PredioConcepto cn : fac.getConceptos()) {
				consec++;
				insertarDeuda(fac.getVigencia(), 12, consec, cn.getNumConcepto(), cn.getNombreConcepto(), cn.getValor(),
						REFERENCIA_DEFAULT, REFERENCIA_DEFAULT,fac.getAvaluo(),fac.getPorcentajeTarifa(), fac.getTotal());
			}
		}
	}

	/**
	 * Permite guardar una variable de tr&aacute;mite *
	 * 
	 * @throws SystemException
	 */
	private void guardaVariableTramite(String codVariable, String valorTexto, Double valor, String valorFecha)
			throws SystemException {
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.KEY_PROCESO.getName(), proceso);
		params.put(GeneralParameterEnum.KEY_TIPO_TRAMITE.getName(), tipoTramite);
		params.put(GeneralParameterEnum.KEY_TRAMITE.getName(), tramite);
		params.put(GeneralParameterEnum.KEY_VARIABLE.getName(), codVariable);
		params.put(GeneralParameterEnum.VALOR_TEXTO.getName(), valorTexto);
		params.put(GeneralParameterEnum.VALOR.getName(), valor);
		params.put(GeneralParameterEnum.VALOR_FECHA.getName(), valorFecha);
		params.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
		params.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
		Parameter parameter = new Parameter();
		parameter.setFields(params);
		UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SERVICIO_GUARDA_VARIABLE);
		requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
	}

	/**
	 * Permite guardar una variable de tr&aacute;mite
	 * 
	 * @throws SystemException
	 */
	private void insertarDeuda(int ano, int periodo, int consec, int numConcepto, String nomConcepto, double valor,
			String referencia1, String referencia2, double avaluo,double tarifa, double total) throws SystemException {
		Map<String, Object> par = new TreeMap<>();
		par.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		par.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);
		par.put(GeneralParameterEnum.TIPO_TRAMITE.getName(), tipoTramite);
		par.put(GeneralParameterEnum.TRAMITEJUD.getName(), tramite);

		par.put(GeneralParameterEnum.ANO.getName(), ano);
		par.put(GeneralParameterEnum.PERIODO.getName(), periodo);
		par.put(GeneralParameterEnum.CONSECUTIVO.getName(), consec);
		par.put(GeneralParameterEnum.CONCEPTO.getName(), numConcepto);
		par.put(GeneralParameterEnum.NOMBRE.getName(), nomConcepto);
		par.put(GeneralParameterEnum.VALOR.getName(), valor);
		par.put(GeneralParameterEnum.REFERENCIA1.getName(), referencia1);
		par.put(GeneralParameterEnum.REFERENCIA2.getName(), referencia2);
		par.put(GeneralParameterEnum.AVALUO.getName(), avaluo);
		par.put("PORCENTAJE_TARIFA", tarifa);
		par.put(GeneralParameterEnum.TOTAL.getName(), total);

		par.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
		par.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

		UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SERVICIO_INSERTA_DEUDA);
		requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), par);
	}

	private void eliminarDeuda() throws SystemException {
		Map<String, Object> par = new TreeMap<>();
		par.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
		par.put(GeneralParameterEnum.KEY_PROCESO.getName(), proceso);
		par.put(GeneralParameterEnum.KEY_TIPO_TRAMITE.getName(), tipoTramite);
		par.put(GeneralParameterEnum.KEY_TRAMITE.getName(), tramite);
		UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SERVICIO_ELIMINA_DEUDA);
		requestManager.delete(urlDelete.getUrl(), par);
	}

	private void guardaDatosComercio(RespuestaApiUnico<Comercio> respuesta, double vigIni, double vigFin)
			throws SystemException {
		Comercio comercio = respuesta.getCuerpo();
		guardaVariableTramite(VariableComercioEnum.NOMBRE.getName(), comercio.getNombre(), null, null);
		guardaVariableTramite(VariableComercioEnum.IDENTIFICACION.getName(), comercio.getIdentificacion(), null, null);
		guardaVariableTramite(VariableComercioEnum.TIPOIDENTIFICACION.getName(), comercio.getTipoIdentificacion(),null, null);
		guardaVariableTramite(VariableComercioEnum.DIRECCION.getName(), comercio.getDireccion(), null, null);
		guardaVariableTramite(VariableComercioEnum.TELEFONO.getName(), comercio.getTelefono(), null, null);
		guardaVariableTramite(VariableComercioEnum.RECALCULA.getName(), comercio.getRecalcula(), null, null);
		guardaVariableTramite(VariableComercioEnum.PLACA_ESTABLECIMIENTO.getName(), comercio.getPlacaEstablecimiento(), null, null);
		guardaVariableTramite(VariableComercioEnum.RAZON_SOCIAL.getName(), comercio.getRazonSocial(), null, null);
		guardaVariableTramite(VariableComercioEnum.MUNICIPIO.getName(), comercio.getMunicipio(), null, null);
		guardaVariableTramite(VariableComercioEnum.DEPARTAMENTO.getName(), comercio.getDepartamento(), null, null);
		guardaVariableTramite(VariableComercioEnum.VIGENCIAINICIAL.getName(), null, vigIni, null);
		guardaVariableTramite(VariableComercioEnum.VIGENCIAFINAL.getName(), null, vigFin, null);
		
		int consec = 0;
		for (DeclaracionComercio fac : comercio.getDeclaraciones()) {
			for (ComercioConcepto cn : fac.getConceptos()) {
				consec++;
				insertarDeuda(fac.getVigencias(), 12, consec, cn.getCodigo(), cn.getNombre(), cn.getValor(),
						fac.getTipoDeclaracion(), fac.getConsecutivo(), 0, 0, 0);
			}
		}
	}
}
