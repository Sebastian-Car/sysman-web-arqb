/*
 * ObligaPedirDatosControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ObligaPedirDatosControladorUrlEnum {

    /**
     * 14001 getTercerosPagTodospornitQuery
     */
    URL8438("OBLIGAPEDIRDATOSCONTROLADORURL8438", "14001"),
    
    URL18842("OBLIGAPEDIRDATOSCONTROLADORURL18842", "1884003"),
    
    URL0005("OBLIGAPEDIRDATOSCONTROLADORURL0005", "1884005"),
    
    URL34063("OBLIGAPEDIRDATOSCONTROLADORURL34063", "34063"),
    /**
     * 23024 getAuxiliaresPagPorAnioConMovimientoCodDescQuery
     */
    URL7108("OBLIGAPEDIRDATOSCONTROLADORURL7108", "23024"),
    /**
     * 34018 getFuenterecursosPagConplanpptalporcuentaanoQuery
     */
    URL12406("OBLIGAPEDIRDATOSCONTROLADORURL12406", "34018"),
    /**
     * 20034 getCentrocostosPagCodigonombresinactivocentroconcodigoexcluidoQuery
     */
    URL8980("OBLIGAPEDIRDATOSCONTROLADORURL8980", "20034"),
    /**
     * 13003 getBpnovedadesproyectoPagConordendecompraauxiliarQuery
     */
    URL10915("OBLIGAPEDIRDATOSCONTROLADORURL10915", "13003"),
    /**
     * 34016 getFuenterecursosPagPoranoporcodigoexcluidoQuery
     */
    URL11483("OBLIGAPEDIRDATOSCONTROLADORURL11483", "34016"),
    /**
     * 20036 getCentrocostosConindicadoresdistribuiractivocentroporanoQuery
     */
    URL10000("OBLIGAPEDIRDATOSCONTROLADORURL10000", "20036"),
    /**
     * 120009 getApropiacionesinicialesCentrocostoporcuentaanoQuery
     */
    URL14986("OBLIGAPEDIRDATOSCONTROLADORURL14986", "120009"),
    /**
     * 20037 updateCentrocostosPorcentajedis
     */
    URL16075("OBLIGAPEDIRDATOSCONTROLADORURL16075", "20037"),
    /**
     * 120010 getApropiacionesinicialesFuenteauxiliarporcodigoQuery
     */
    URL19467("OBLIGAPEDIRDATOSCONTROLADORURL19467", "120010"),
    /**
     * 120011 getApropiacionesinicialesAuxiliaresQuery
     */
    URL5829("OBLIGAPEDIRDATOSCONTROLADORURL19467", "120011"),
    
    URL20076("OBLIGAPEDIRDATOSCONTROLADORURL19467", "20076"),
    
    URL13043("OBLIGAPEDIRDATOSCONTROLADORURL19467", "13043"),
    
    URL23054("OBLIGAPEDIRDATOSCONTROLADORURL19467", "23054"),
    
    URL124005("OBLIGAPEDIRDATOSCONTROLADORURL124005", "124005");

    private final String key;
    private final String value;

    private ObligaPedirDatosControladorUrlEnum(String key, String value)
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
