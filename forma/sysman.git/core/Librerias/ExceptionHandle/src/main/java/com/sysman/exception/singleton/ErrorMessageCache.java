package com.sysman.exception.singleton;

import com.sysman.exc.kernel.api.clientwso2.beans.Parameter;
import com.sysman.exc.kernel.api.clientwso2.connectors.PropertiesConfigUtil;
import com.sysman.exc.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.exc.kernel.api.commons.util.Constans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class ErrorMessageCache implements Serializable{

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ErrorMessageCache.class);
	private static ErrorMessageCache errorMessageCache;
	private Map<String, String> cache;
	
	private ErrorMessageCache(){
		super();
	}
	/**
	 * Metodo statico que obtiene una instancia unica de la clase 
	 * ErrorMessageCache
	 * 
	 */
	public static ErrorMessageCache getInstance(){
		if(errorMessageCache==null){			
			errorMessageCache = new ErrorMessageCache();
		}
		return errorMessageCache;		
	}		

	/**
	 * Permite recuperar el objeto cache que contiene los registros 
	 * cargados de la tabla msg_errors_messages Map<String, String>
	 * 
	 * @return
	 */
	public Map<String, String> getMessage(){
		
		return cache;
	}
	/**
	 * permite cargar el objeto cache que contiene los registros 
	 * cargados de la tabla msg_errors_messages Map<String, String>
	 * @param cache
	 */
	public void setCache(Map<String, String> cache) {
		this.cache = cache;
	}
	
	/**
	 * Permite cargar los registros de la tabla msg_errors_messages 
	 * en un objeto cache de tipo Map
	 * 
	 * @return Map<String, String>
	 * @throws NamingException
	 */
	public Map<String, String> cargarCache(){		
		try {
			if(cache == null || cache.isEmpty()){			
				cache = new HashMap<>();
				RequestManager req = new RequestManager();
				PropertiesConfigUtil pro = new PropertiesConfigUtil();
				String url = pro.getValueFromConfigP(Constans.ERROR_MESSAGE_CONFIG_KEY);
				Map<String, Object> params= null;
				List<Parameter> errores = req.getList(url, params);
				for (Parameter param : errores){					
					cache.put(String.valueOf(param.getFields().get("CODE")),String.valueOf( param.getFields().get("MESSAGE")));
				}
				
			
			}
		} catch (Exception e) {
			log.error(e);
		}
		return cache;
	}
	
}
