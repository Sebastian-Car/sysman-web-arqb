package com.sysman.exception;

public class SystemException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SystemException() {
        super();
    }

    public SystemException(Exception e) {
        super(e);
    }

    public SystemException(String message, Exception e) {
        super(message, e);
    }

    public SystemException(String message) {
        super(message);
    }

}
