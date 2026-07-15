	/*
	 * CAlmacenContabilidadsControladorUrlEnum
	 *
	 * 1.0
	 *
	 * 06/07/2018
	 *
	 * Copyright Stefanini Sysman
	 */
	package com.sysman.contabilizar.enums;

	/**
	 * @author Processors-api
	 *
	 * @version 1.0
	 *
	 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
	 */
	public enum EalmacencontabilidadTControladorUrlEnum{
	    // Recurso para obtener el último Comprobante Contable de tipo ALM
	    URL4410("EALMACENCONTABILIDADCONTROLADORURLENUM4410",
	                    "72075");

	    private final String key;
	    private final String value;

	    private EalmacencontabilidadTControladorUrlEnum(String key, String value)
	    {
	        this.key = key;
	        this.value = value;
	    }

	    public String getKey()
	    {
	        return key;
	    }

	    public String getValue()
	    {
	        return value;
	    }
	}
