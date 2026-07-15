/*-
 * ConstanteArchivo.java
 *
 * 1.0
 * 
 * 11/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.enums;

/**
 * Enumeracion para almacenar las content types y extensiones de tipos
 * de archivos comunes usados en las aplicaciones
 * 
 * @version 1.0, 11/08/2017
 * @author cmanrique
 *
 */
public enum ConstanteArchivo {

    EXCEL(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    EXCEL97(".xls", "application/vnd.ms-excel"),

    PDF(".pdf", "application/pdf"),

    CSV(".csv", "Content-disposition"),

    JASPER_COMP(".jasper", "Content-disposition"),

    ZIP(".zip", "application/zip"),
    
    TXT(".txt", "text/plain");

    private String extension;
    private String contentType;

    private ConstanteArchivo(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

}
