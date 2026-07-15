package com.sysman.exception.processor;

import com.sysman.exc.kernel.api.clientwso2.beans.Parameter;
import com.sysman.exc.kernel.api.clientwso2.connectors.PropertiesConfigUtil;
import com.sysman.exc.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.exc.kernel.api.commons.util.Constans;

import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;


@MessageDriven(name = "ErrorMessageManager", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = Constans.ERRROR_MESSAGE_QUEUE),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ErrorMessageManager implements MessageListener {
	
	private static Logger log = Logger.getLogger(ErrorMessageManager.class);
	
	@Override
	public void onMessage(Message message) {
		 try {
			ObjectMessage msg = (ObjectMessage) message;
			log.debug("Mensajes Recibidos: {}"+ msg);
			Map<String,String> logs2 = (Map<String,String>)msg.getObject();
			Map<String,Object> logs = (Map<String,Object>)msg.getObject();					
			RequestManager req = new RequestManager();
			Parameter parameter = new Parameter();
			parameter.setFields(logs);
			PropertiesConfigUtil pro = new PropertiesConfigUtil();
			req.save(pro.getValueFromConfigP(Constans.ERROR_MANAGER_CONFIG_KEY), Constans.ERROR_METOD_INSERT, parameter );
		
		 } catch (Exception e) {
				log.error(e);
			}
	}

}
