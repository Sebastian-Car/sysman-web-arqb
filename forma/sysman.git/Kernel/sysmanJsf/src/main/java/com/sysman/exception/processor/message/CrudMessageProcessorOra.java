/*-
 * CrudMessageProcessorOra.java
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

import com.google.gson.JsonObject;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.clientwso2.converters.JsonConverter;
import com.sysman.exc.kernel.api.clientwso2.util.enums.ParametersEnum;
import com.sysman.exception.SystemException;
import com.sysman.exception.processor.message.enums.ParameterServiceErrorEnum;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Implementacion de {@code MessageProcessor} con el fin de procesar
 * especificamente los mensajes de excepciones retornadas desde el DSS
 * para una conexion a base de datos Oracle
 * 
 * @version 1.0, 27/07/2017
 * @author cmanrique
 *
 */
public class CrudMessageProcessorOra implements MessageProcessor {

    /**
     * Atributo para gestion de escritura en log
     */
    private static final Logger logger = Logger
                    .getLogger(CrudMessageProcessorOra.class);

    /**
     * Codigo de servicio para obtener equivalencias de mensajes
     */
    public static final String CODIGO_RUTA_MSG_ERROR = "423001";

    /**
     * Codigo de servicio para insercion en tabla LOG_ERROR
     */
    public static final String CODIGO_RUTA_INS_LOG_ERROR = "42400C";

    /**
     * {@inheritDoc}
     * 
     * El parametro message en el mensaje capturado de una
     * {@code ClientWSO2Exception} que viene en formato JSON, los
     * adicionalParameters deben traer los datos del servicio
     * invocado, como la url y los parametros y demas adicionales que
     * sean requeridos para alamcenar un log
     * 
     */
    @Override
    public String getFinalMessage(String message,
        Map<String, Object> adicionalParameters) {
        String rta = null;
        long codError = 0;
        RequestManager req = new RequestManager();
        try { // Desde aca busca el mensaje equivalente almacenado en
              // la tabla ERROR
            JsonObject obj = JsonConverter.stringToJsonObject(message);
            String msgError = obj.get("Fault").getAsJsonObject()
                            .get("faultstring").getAsString();

            if (!msgError.contains("@#INI#")) {
                msgError = msgError.substring(msgError.indexOf("ORA-") + 4);
                msgError = msgError.substring(0, msgError.indexOf(':'));

                Map<String, Object> params = new HashMap<>();
                codError = Long.parseLong(msgError) * -1;
                params.put(GeneralParameterEnum.CODIGO.getName(), codError);
                Parameter parRta = req.get(UrlServiceUtil
                                .getUrlBeanById(CODIGO_RUTA_MSG_ERROR).getUrl(),
                                params);
                rta = (String) parRta.getFields().get(
                                ParameterServiceErrorEnum.MSG_INTERFAZ
                                                .getValue());
                rta = rta == null ? message : rta;
            }
            else {
                rta = message;
            }
        }
        catch (Exception e) {
            logger.info(e.getMessage(), e);
            rta = message;
        }
        finally { // Hace el inser a la tabla LOG_ERROR
            UrlBean urlInsert = UrlServiceUtil
                            .getUrlBeanById(CODIGO_RUTA_INS_LOG_ERROR);

            JsonObject objRta = new JsonObject();
            objRta.addProperty(ParametersEnum.URL.getText(),
                            (String) adicionalParameters
                                            .get(ParametersEnum.URL.getText()));
            objRta.addProperty(ParametersEnum.PARAMETERS.getText(),
                            (String) adicionalParameters
                                            .get(ParametersEnum.PARAMETERS
                                                            .getText()));
            objRta.addProperty(ParametersEnum.RESPONSE.getText(), message);

            Map<String, Object> paramsInsert = new HashMap<>();
            Date fechaAct = new Date();
            paramsInsert.put(ParameterServiceErrorEnum.CODERROR.getValue(),
                            codError);
            paramsInsert.put(ParameterServiceErrorEnum.LOG_USUARIO.getValue(),
                            SessionUtil.getUser().getCodigo());
            paramsInsert.put(ParameterServiceErrorEnum.CREATED_BY.getValue(),
                            SessionUtil.getUser().getCodigo());
            paramsInsert.put(ParameterServiceErrorEnum.LOG_TABLA_ORIGEN
                            .getValue(), adicionalParameters
                                            .get(ParametersEnum.URL.getText()));
            paramsInsert.put(ParameterServiceErrorEnum.MSG_CAPTURADO.getValue(),
                            rta);
            paramsInsert.put(ParameterServiceErrorEnum.DESC_SQL.getValue(),
                            objRta.toString());
            paramsInsert.put(ParameterServiceErrorEnum.ID_FORM_MENU.getValue(),
                            "Menu: " + SessionUtil.getMenuActual());
            paramsInsert.put(ParameterServiceErrorEnum.LOG_FECHA.getValue(),
                            fechaAct);
            paramsInsert.put(ParameterServiceErrorEnum.DATE_CREATED.getValue(),
                            fechaAct);

            try {
                paramsInsert = req.saveLogError(urlInsert.getUrl(),
                                urlInsert.getMetodo(), paramsInsert);
                rta = "Log: "
                    + paramsInsert.get(ParameterServiceErrorEnum.KEY_LOG_IDENT
                                    .getValue())
                    + "\n" + rta;
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
            }

        }
        return rta;
    }

}
