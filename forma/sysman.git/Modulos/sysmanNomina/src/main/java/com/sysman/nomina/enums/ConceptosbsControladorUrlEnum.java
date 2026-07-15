/*
 * ConceptosbsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ConceptosbsControladorUrlEnum {

    URL11086("CONCEPTOSBSCONTROLADORURL11086", "475001"),

    URL0001("CONCEPTOSBSCONTROLADORURL0001", "209004"),

    URL10548("CONCEPTOSBSCONTROLADORURL10548", "470001"),

    URL10156("CONCEPTOSBSCONTROLADORURL10156", "151030"),

    URL8686("CONCEPTOSBSCONTROLADORURL8686", "151001"),

    URL10015("CONCEPTOSBSCONTROLADORURL10015", "36007"),

    URL11508("CONCEPTOSBSCONTROLADORURL11508", "20049"),

    URL9410("CONCEPTOSBSCONTROLADORURL9410", "14001"),

    URL6060("CONCEPTOSBSCONTROLADORURL6060", "16132"),

    URL4545("CONCEPTOSBSCONTROLADORURL4545", "45051"),

    URL1755("TIPOPAGOSIA", "1755001"),

    URL15135("CONCEPTOSINCPINDRENTAEXCENTA", "151035"),
    
    URL875("CONCEPTOSBSCONTROLADORURL875", "1779001"),
    
    URL20001("CONCEPTOSBSCONTROLADORURL11508", "20001"),
    
    URL23006("CONCEPTOSBSCONTROLADORURL12000", "23006"),
    
    URL4001( "CONCEPTOSBSCONTROLADORURL4001","4001"),
    
    URL4016( "CONCEPTOSBSCONTROLADORURL4016","4016");

    private final String key;
    private final String value;

    private ConceptosbsControladorUrlEnum(String key, String value)
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
