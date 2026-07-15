/*
 * SysmanException
 *
 * 1.0
 *
 * 12/08/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.exc.kernel.api.commons.util.exceptions;

import com.sysman.exception.processor.ProressorError;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase principal de excepciones que controla cualquier anomalia del
 * ERP.
 */
public class SysmanException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8219523877440526914L;
    private ProressorError processError;
    protected String code;
    protected String message;
    protected Throwable exception;

    /**
     * Constructs a {@code SysmanException} with no detail message.
     */
    public SysmanException() {
        super();
    }

    public SysmanException(String message) {
        super(message);
        this.message = message;
        // this.processError = new ProressorError(this, message);

    }

    public SysmanException(Exception e) {
        super(e);
        // this.processError = new ProressorError(e);
    }

    public SysmanException(Exception e, String message) {
        super(message, e);
        this.message = message;
        // this.processError = new ProressorError(e, message);
    }

    @Override
    public String getMessage() {
        // this.message = processError.getMessage();
        return message;
    }

    /**
     * Permite obtener el código de la excepción cargada
     * 
     * @return String
     */
    public String getCode() {
        this.code = processError.getCode();
        return code;
    }
}
