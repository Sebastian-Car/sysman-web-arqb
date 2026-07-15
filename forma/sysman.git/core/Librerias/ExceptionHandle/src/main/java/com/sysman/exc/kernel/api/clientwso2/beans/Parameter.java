package com.sysman.exc.kernel.api.clientwso2.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Bean para almacenar informacion de Par�metros.
 */
public class Parameter {

    private Map<String, Object> fields;

    public Parameter() {
        fields = new HashMap<>();
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}
