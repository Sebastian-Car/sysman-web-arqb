/*-
 * ActualizaparametrosretroactivosControladorEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 18/08/2017
 * @author pespitia
 *
 */
public enum ResumentotalcuneControladorEnum {

    ANO1("ano1"),

    MES1("mes1"),

    PERIODO1("periodo1"),

    PARANIO("anio"),

    PARMES("mes"),

    INFORME2("900029CnCuneSinConf"),

    UN_COMPANIA("UN_COMPANIA"),

    UN_ANO("UN_ANO"),

    UN_MES("UN_MES"),

    NOM_BASE("T_NE_BASE"),
    
    NOM_AJUSTE("T_NE_AJUSTE_RE"),
    
    NOM_ELIMINACION("T_NE_ELIMINACION"),

    MSJ_CONSEC("El consecutivo de ajustes debe ser mayor o igual a 2"),

    NUMDCTO("NUMERO_DCTO"),

    UN_TIPONOM("UN_TIPONOM"),

    NIE199("NIE199"),

    UN_NUMERO_DCTO("UN_NUMERO_DCTO"),

    UN_NIE012("UN_NIE012"),

    NIE012("NIE012"),

    NIE008("NIE008"),

    CONS_TIPONOMINA("CONS_TIPONOMINA"),

    UN_CONSEC("UN_CONSEC"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    NIE072("NIE072"),

    FALSE("false"),

    TRUE("true"),

    TB_TB4255("TB_TB4255"),

    NIE088("NIE088"),

    NIE136("NIE136"),

    NIE139("NIE139"),

    NIE098("NIE098"),

    NIE083("NIE083"),

    NIE141("NIE141"),

    NIE078("NIE078"),

    NIE093("NIE093"),

    NIE155("NIE155"),

    NIE193("NIE193"),

    NIE194("NIE194"),

    NIE163("NIE163"),

    NIE166("NIE166"),

    NIE168("NIE168"),

    NIE171("NIE171"),

    NIE148("NIE148"),

    NIE122("NIE122"),

    NIE127("NIE127"),

    NIE108("NIE108"),

    NIE152("NIE152"),

    NIE103("NIE103"),

    NIE146("NIE146"),

    NIE063("NIE063"),

    NIE015("NIE015"),

    NIE042("NIE042"),

    NIE053("NIE053"),

    NIE041("NIE041"),

    NIE046("NIE046"),

    NIE043("NIE043"),

    NIE010("NIE010"),

    NIE065("NIE065"),

    NIE067("NIE067"),

    NIE014("NIE014"),

    NIE161("NIE161"),

    NIE164("NIE164"),

    NIE137("NIE137"),

    NIE051("NIE051"),

    NIE064("NIE064"),

    NIE047("NIE047"),

    NIE048("NIE048"),

    NIE068("NIE068"),

    NIE066("NIE066"),

    NIE061("NIE061"),

    NIE044("NIE044"),

    NIE052("NIE052"),

    NIE049("NIE049"),

    NIE056("NIE056"),

    NIE110("NIE110"),

    NIE109("NIE109"),

    NIE004("NIE004"),

    NIE003("NIE003"),

    NIE005("NIE005"),

    NIE002("NIE002"),

    NIE203("NIE203"),

    NIE170("NIE170"),

    NIE172("NIE172"),

    NIE195("NIE195"),

    NIE196("NIE196"),

    NIE197("NIE197"),

    MSGCERT("No existen datos del certificado"),

    NITSTEFANINI("800021261"),

    MSGCERTERROR("Se presento un error al consultar los datos del certificado"),

    MSGCERTERRORPOST(
                    "Se presento un error al consumir el servicio de nom Electronica.");

    private final String value;

    private ResumentotalcuneControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
