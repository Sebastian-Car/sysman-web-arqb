/*
 * IprogramaciondemantenimientoexcelControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum IprogramaciondemantenimientoexcelControladorEnum {
    DICIEMBREP("DICIEMBREP"),

    DICIEMBREE("DICIEMBREE"),

    NOVIEMBREE("NOVIEMBREE"),

    NOVIEMBREP("NOVIEMBREP"),

    SEPTIEMBREE("SEPTIEMBREE"),

    SEPTIEMBREP("SEPTIEMBREP"),

    OCTUBREE("OCTUBREE"),

    OCTUBREP("OCTUBREP"),

    AGOSTOE("AGOSTOE"),

    AGOSTOP("AGOSTOP"),

    JULIOE("JULIOE"),

    JULIOP("JULIOP"),

    JUNIOE("JUNIOE"),

    JUNIOP("JUNIOP"),

    MAYOE("MAYOE"),

    MAYOP("MAYOP"),

    ABRILE("ABRILE"),

    ABRILP("ABRILP"),

    MARZOE("MARZOE"),

    MARZOP("MARZOP"),

    FEBREROE("FEBREROE"),

    FEBREROP("FEBREROP"),

    ENEROE("ENEROE"),

    ENEROP("ENEROP"),

    FRECUENCIA("FRECUENCIA"),

    FECHAHASTAAUX("FECHAHASTAAUX"),

    FECHADESDEAUX("FECHADESDEAUX"),

    ELEMENTOHASTA("ELEMENTOHASTA"),

    ELEMENTODESDE("ELEMENTODESDE"),

    NOMBRECORTO("NOMBRECORTO"),

    MARCA1("MARCA1"),

    ARIAL("Arial"),

    NOMBRELARGO("NOMBRELARGO");

    private final String value;

    private IprogramaciondemantenimientoexcelControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
