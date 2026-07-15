/*-
 * CrudException.java
 *
 * 1.0
 * 
 * 27/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.exc.kernel.api.commons.util.exceptions;

import com.sysman.exc.kernel.api.clientwso2.connectors.PropertiesConfigUtil;
import com.sysman.exc.kernel.api.commons.util.Constans;
import com.sysman.exception.SystemException;
import com.sysman.exception.processor.message.MessageProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Excepcion creada para gestionar los mensajes finales mostradoes y
 * logs almacenados en la base de datos para cuando se genere una
 * excepcion al hacer una operacion CRUD.
 * 
 * @version 1.0, 27/07/2017
 * @author cmanrique
 *
 */
public class CrudException extends SystemException {

    /**
     * Atributo para gestion de escritura en log
     */
    private static final Logger logger = Logger
                    .getLogger(CrudException.class);

    /**
     * Atributo encargado de procesar el mensaje, se referencia a la
     * interface {@code MessageProcessor}, sin embargo la instancia se
     * crea en el metodo {@link #instanciarProcessesor()} dependiendo
     * de un parametro previamente confugurado
     */
    private transient MessageProcessor messageProcessor;

    /**
     * Mensage final de la excepcion mostrado al usuario
     */
    private String message;

    /**
     * Indicador de mensage formateado, se asigna #{@code true} cuando
     * se ha formateado el mensaje
     */
    private boolean formateado;

    /**
     * Parametros adicionales tenidos en cuenta para el proceso de
     * formateo del mensaje
     */
    private transient Map<String, Object> adicionalParameters;

    public CrudException() {
        super();
        instanciarProcessesor();
    }

    public CrudException(String message) {
        super(message);
        this.message = message;
        instanciarProcessesor();
    }

    public CrudException(Exception e) {
        super(e);
        instanciarProcessesor();
    }

    public CrudException(String message, Exception e) {
        super(message, e);
        this.message = message;
        instanciarProcessesor();
    }

    public CrudException(String message, Exception e,
        Map<String, Object> adicionalParameters) {
        super(message, e);
        this.message = message;
        this.adicionalParameters = adicionalParameters;
        instanciarProcessesor();
    }

    /**
     * {@inheritDoc}
     * 
     * Retorna el mensage de la excepcion, realiza el formateo
     * correpondiente llamando el metodo {@code getFinalMessage} de la
     * implementacion de {@link #messageProcessor} parametrizada. Solo
     * raliza el llamado cuando el indicador {@link #formateado} esta
     * en {@code false}
     * 
     * @see MessageProcessor#getFinalMessage(String, Map)
     */
    @Override
    public String getMessage() {
        if (!formateado) {
            this.message = messageProcessor.getFinalMessage(this.message,
                            this.adicionalParameters);
            formateado = true;
        }
        return message;
    }

    /**
     * Intancia el atributo {@link #messageProcessor} segun la
     * referencia a la clase parametrizada correctamente en el archivo
     * .properties configExc, en la llave
     * {@value Constans#CLASE_VALIDADOR_CRUD_MSG_KEY}
     */
    public void instanciarProcessesor() {
        try {

            Class c = Class.forName(
                            PropertiesConfigUtil.getValueFromConfigP(
                                            Constans.CLASE_VALIDADOR_CRUD_MSG_KEY));
            Constructor constructorSinParametros = c.getConstructor();

            messageProcessor = (MessageProcessor) constructorSinParametros
                            .newInstance();
        }
        catch (InstantiationException | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException
                        | ClassNotFoundException | NoSuchMethodException
                        | SecurityException e) {
            logger.error(e.getMessage(), e);
        }

    }

}
