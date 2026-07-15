/*-
 * ZonaHoraria.java
 *
 * 1.0
 * 
 * 4/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.identificacion;

import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilidades relacionadas con la zona horaria.
 * 
 * @version 1.0, 4/09/2017
 * @author jrodrigueza
 *
 */
public class ZonaHoraria {

    /**
     * Objeto para informar de eventos o acciones ocurridas durante un
     * proceso.
     */
    private static final Log LOGGER = LogFactory.getLog(ZonaHoraria.class);

    /**
     * Crea una instancia de ZonaHoraria
     */
    private ZonaHoraria() {
        // Constructor sin parametros
    }

    /**
     * Obtiene la zona horara para el host actual.
     * 
     * @return zona horaria
     */
    public static TimeZone cargarZonaHoraria() {
        TimeZone timeZone = TimeZone.getDefault();
        String id = timeZone.getID();
        LOGGER.info("name=" + timeZone.getDisplayName() + ",id="
            + timeZone.getID());
        return TimeZone.getTimeZone(id);
    }

}
