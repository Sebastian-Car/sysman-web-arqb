package com.sysman.exc.kernel.api.commons.util;

/**
 * Clase constantes
 * 
 * @author erick
 *
 */
public final class Constans {
    /**
     * Constant for empty String
     **/
    public static final String EMPTYSTRG = "";

    /**
     * Constant for empty String
     **/
    public static final String OKCODE = "200";

    /**
     * bearer for WSO2 token
     **/
    public static final String BEARER = "Bearer ";

    /**
     * CONNECTION_FACTORY_QUEUE NAME
     */
    public static final String CONNECTION_FACTORY_QUEUE = "java:/ConnectionFactory";
    /**
     * MESSAGE QUE NAME
     **/
    public static final String ERRROR_MESSAGE_QUEUE = "java:/jms/queue/ErrorMessageQueue";

    public static final String BUSINESS_MESSAGE_QUEUE = "java:/jms/queue/BusinessMessageQueue";

    public static final String MESSAGE_COLUM = "MESSAGE";
    public static final String CODE_MESSAGE_COLUM = "CODE";
    public static final String TRACE_MESSAGE_COLUM = "TRACE";

    public static final String ERROR_METOD_INSERT = "_post_insertarlog";
    public static final String BUSINESS_METOD_INSERT = "_post_insertarlog";
    public static final String ERROR_MESSAGE_CONFIG_KEY = "url.error.message";

    public static final String ERROR_MANAGER_CONFIG_KEY = "url.error.manager";

    public static final String BUSINESS_MANAGER_CONFIG_KEY = "url.business.manager";

    public static final String ERROR_PROVIDER_CONFIG_KEY = "url.error.provider";

    public static final String CLASE_VALIDADOR_CRUD_MSG_KEY = "claseValidadorCrudMensaje";

    public static final String EJB_NAME = "ejbName";
    public static final String EJB_METHOD = "ejbMethod";
    public static final String EJB_PARAMS = "ejbparams";
    public static final String EJB_TYPE = "type";
}
