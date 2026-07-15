package com.sysman.recursos.ejb.impl;

import java.util.Map;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import co.com.sysman.colas.procesador.EnumParamProcesador;

import static  com.sysman.recursos.ejb.impl.QueueProperties.*;


/**
 * 
 * @author jeguerrero Clase encargada de recibir el mensaje de la cola y enviar
 *         un correo adjuntando los datos de la cola
 *
 */

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = DESTINATION_TYPE , propertyValue = JAVAX_JMS_TOPIC),
		@ActivationConfigProperty(propertyName = DESTINATION, propertyValue = JAVA_JBOSS_EXPORTED_JMS_EMAIL_QUEUE) }

		)
public class RecibidorColas implements MessageListener {

	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RecibidorColas.class);

	/**
	 * Atributo que inyecta el envio de correos
	 */
	@Inject
	EnvioCorreo envioCorreos;

	/**
	 * Metodo implementado de la interfaz MessageListener el cual siempre esta
	 * escuchando
	 * 
	 */
	public void onMessage(Message message) {
		try {

			LOG.info("Entro a: <<onMessageeeeeee >> parametros / message  ->> {} ", message);
			ObjectMessage obj = (ObjectMessage) message;
			Map<String, Object> contexto = (Map<String, Object>) obj.getObject();

			String tipoRecepcion = (String) contexto.get(EnumParamProcesador.KEY_TIPO_RECEPCION.name());
			if (EnumParamProcesador.KEY_EMAIL.name().equals(tipoRecepcion)) {
				envioCorreos.send(contexto);
			}
			LOG.info("Salio de : <<onMessageeeeeee >> parametros / message  ->> {} ", message);

		} catch (

				JMSException e) {
			e.printStackTrace();
		}
	}
}
