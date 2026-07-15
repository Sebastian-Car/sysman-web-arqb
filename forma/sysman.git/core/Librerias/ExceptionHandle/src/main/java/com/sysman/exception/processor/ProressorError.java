package com.sysman.exception.processor;

import com.sysman.exc.kernel.api.clientwso2.util.enums.MessageHandleEnum;
import com.sysman.exc.kernel.api.commons.util.Constans;
import com.sysman.exception.singleton.ErrorMessageCache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;


public class ProressorError {

	private String message;
	private String code;
	private StringBuilder trace = new StringBuilder();
	static final  Logger logger = Logger.getLogger(ProressorError.class);
	

	public ProressorError(Throwable exception, String modulo) {
		ErrorMessageCache.getInstance().cargarCache();
		Map<String, String> cache = ErrorMessageCache.getInstance().getMessage();		
		String codeMap = exception.getClass().getSimpleName();
		logNegocio (exception);
	}
	
	public ProressorError(Throwable exception) {
		ErrorMessageCache.getInstance().cargarCache();
		Map<String, String> cache = ErrorMessageCache.getInstance().getMessage();		
		String codeMap = exception.getClass().getSimpleName();
		
		if (cache.get(codeMap) != null) {
			message = cache.get(codeMap);
		} else {
			message = MessageHandleEnum.GENERAL_ERROR.getMessage();
		}
		this.message=cache.get(codeMap);
		logNegocio (exception);
	}
	
	
	
	/**
	 *
	 * Metodo putMessageOnQueue envia un mensaje con un Logs como contenido.
	 * 
	 * @Param Map<String,String> log
	 **/
	public void putMessageOnQueue(Map<String,String> log){
		InitialContext ic = null;
		Connection jmsConnection = null;
		try {
			ic = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) ic.lookup(Constans.CONNECTION_FACTORY_QUEUE);
			Queue queue = (Queue) ic.lookup(Constans.ERRROR_MESSAGE_QUEUE);
			Session session = null;
			MessageProducer messageProducer = null;
			
			try {
				
				jmsConnection = cf.createConnection();
				session = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				messageProducer = session.createProducer(queue);
				ObjectMessage message = session.createObjectMessage((Serializable) log);
				messageProducer.send(message);
			} finally {
				if (messageProducer != null) {
					messageProducer.close();
				}
				if (session != null) {
					session.close();
				}
				if (jmsConnection != null) {
					jmsConnection.close();
				}
			}
		} catch (NamingException e) {
			logger.error(e);
		} catch (JMSException e) {
			logger.error(e);
		}		
		
	}
	
	public void logNegocio (Throwable ex){
		Map<String,String> log = new HashMap<String,String>();
		
		log.put(Constans.MESSAGE_COLUM, String.valueOf(ex.getMessage()));
		log.put(Constans.CODE_MESSAGE_COLUM, String.valueOf(ex.getClass().getSimpleName().toString()));
		
		java.io.StringWriter errors = new java.io.StringWriter();
		ex.printStackTrace(new java.io.PrintWriter(errors));
		log.put(Constans.TRACE_MESSAGE_COLUM, errors.toString().replaceAll("\n\t", "").substring(0, 500));
		putMessageOnQueue(log);
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
