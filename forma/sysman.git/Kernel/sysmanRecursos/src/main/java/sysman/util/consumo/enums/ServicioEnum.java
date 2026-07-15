package sysman.util.consumo.enums;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.google.gson.reflect.TypeToken;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.enums.APIAutoServicioEnum;

import sysman.util.consumo.pojo.Predio;
import sysman.util.consumo.pojo.Comercio;
import sysman.util.consumo.pojo.RespuestaApiUnico;

public enum ServicioEnum {

	VACIO(0, new TypeToken<String>() {
	}.getType()),

	PREDIAL(101, new TypeToken<RespuestaApiUnico<Predio>>() {
	}.getType()),
	
	COMERCIO(102, new TypeToken<RespuestaApiUnico<Comercio>>() {
	}.getType()),

	;

	protected ResourceBundle idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);

	/**
	 * Variable de acceso al c&oacute;digo de la URL que se ejecuta para recuperar
	 * la deuda
	 */
	private final int urlServicio;

	private final Type tipo;

	/**
	 * Constructor del enumerado
	 * 
	 * @param urlServicio
	 */
	private ServicioEnum(int urlServicio, Type type) {
		this.urlServicio = urlServicio;
		this.tipo = type;
	}

	/**
	 * Metodo de acceso del c&oacute;digo de la URL que se ejecuta para recuperar la
	 * deuda
	 * 
	 * @return Retorna el c&oacute;digo de la URL que se ejecuta para recuperar la
	 *         deuda
	 */
	public int getUrlServicio() {
		return urlServicio;
	}

	public Type getTipo() {
		return tipo;
	}

	/**
	 * Permite resolver la url del servicio dado
	 * 
	 * @param compania   : C&oacute;digo de la compaa&ntilde;ia donde se consulta
	 *                   laurl del servicio
	 * @param parametros : Mapa de par&aacute;metros que se reemplazan en la url
	 * @return Url con par&aacute;metros resueltos
	 * @throws SysmanException
	 */
	public String obtenerUrlGet(String compania, Map<String, Object> parametros) throws SysmanException {
		String url = APIAutoServicioEnum.KEY_VACIO.getValue();
		// Consulta el servicio en la tabla
		Map<String, Object> paraServicio = new TreeMap<>();
		paraServicio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		paraServicio.put(GeneralParameterEnum.CODIGO.getName(), urlServicio);
		Registro rs = new Registro();
		RequestManager requestManager = new RequestManager();
		try {
			rs = RegistroConverter.toRegistro(requestManager
					.get(UrlServiceUtil.getUrlBeanById(SysmanConstantes.SERVICIO_API).getUrl(), paraServicio));
		} catch (NullPointerException | SystemException e) {
			throw new SysmanException(idioma.getString("TB_TB4230"));
		}
		if (rs == null) {
			throw new SysmanException(idioma.getString("TB_TB4232"));
		} else if (rs.getCampos().get(GeneralParameterEnum.URL.getName()).toString() == null) {
			throw new SysmanException(idioma.getString("TB_TB4231"));
		}
		url = rs.getCampos().get(GeneralParameterEnum.URL.getName()).toString();
		// Reemplaza parametros
		url = SysmanFunciones.resolverUrlGet(url, parametros);
		return url;
	}

	/**
	 * Permite devolver el enumerado que corresponde al c&oacute;digo de la url
	 * servicio dado
	 * 
	 * @param urlServicio : C&oacute;digo de la url servicio
	 * @return Retorna el Enumerado que coincide con el c&oacute;digo de la url
	 *         Servicio
	 */
	public ServicioEnum buscarPorUrlServicio(int urlServicio) {
		for (ServicioEnum ser : ServicioEnum.values()) {
			if (ser.getUrlServicio() == urlServicio) {
				return ser;
			}
		}
		return null;
	}

}
