/*-
 * MessageProcessor.java
 *
 * 1.0
 * 
 * 27/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.exception.processor.message;

import java.util.Map;

/**
 * Esta interfce se crea como base para el procesamiento de mensajes
 * con diferentes fines
 * 
 * @version 1.0, 27/07/2017
 * @author cmanrique
 *
 */
public interface MessageProcessor {

    /**
     * Metodo de procesamiento del mensaje desde las implementaciones
     * se realizaran las tareas especificas con los paratros
     * ingresados
     * 
     * @param message
     * Mensaje original el cual debe ser procesado
     * @param adicionalParameters
     * Parametros adicionales a tener en cuenta en el momento del
     * procesamieto
     * @return El mensaje procesado
     */
    String getFinalMessage(String message,
        Map<String, Object> adicionalParameters);

}
