/*
 * PlantillaswordsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PlantillaswordsControladorUrlEnum {

    URL165("PLANTILLASWORDSCONTROLADORURL165", "104054"),

    URL9766("PLANTILLASWORDSCONTROLADORURL9766", "10000C"),

    URL12778("PLANTILLASWORDSCONTROLADORURL12778", "100003"),

    URL17170("PLANTILLASWORDSCONTROLADORURL17170", "100005"),

    URL7379("PLANTILLASWORDSCONTROLADORURL7379", "100004"),

    URL4537("PLANTILLASWORDSCONTROLADORURL4537", "105001"),

    URL232("PLANTILLASWORDSCONTROLADORURL232", "105002"),

    URL233("PLANTILLASWORDSCONTROLADORURL233", "936001"),

    URL32269("PLANTILLASWORDSCONTROLADORURL32269", "1019002"),
    
	URL104084("PLANTILLASWORDSCONTROLADORURL104084", "104084"), 
	
	URL104086("PLANTILLASWORDSCONTROLADORURL104086", "104086"),
	
	URL104087("PLANTILLASWORDSCONTROLADORURL104086", "104087");

    private final String key;
    private final String value;

    private PlantillaswordsControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
