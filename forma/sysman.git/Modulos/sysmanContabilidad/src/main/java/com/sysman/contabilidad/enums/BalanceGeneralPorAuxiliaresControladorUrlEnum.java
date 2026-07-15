/*
 * ActualizaConfiguracionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum BalanceGeneralPorAuxiliaresControladorUrlEnum {

    URL16130("BALANCEGENERALPORAUXILIARESCONTROLADORURL16130","14031"),  
    
    URL18172("BALANCEGENERALPORAUXILIARESCONTROLADORURL18172","23019"),  
    
    URL15101("BALANCEGENERALPORAUXILIARESCONTROLADORURL15101","14001"),  
    
    URL20207("BALANCEGENERALPORAUXILIARESCONTROLADORURL20207","13035"),  
    
    URL21231("BALANCEGENERALPORAUXILIARESCONTROLADORURL21231","34001"),  
    
    URL14266("BALANCEGENERALPORAUXILIARESCONTROLADORURL14266","20015"),  
                
    URL11002("BALANCEGENERALPORAUXILIARESCONTROLADORURL11002","4013"),  
    
    URL13433("BALANCEGENERALPORAUXILIARESCONTROLADORURL13433","20013"),  
    
    URL17165("BALANCEGENERALPORAUXILIARESCONTROLADORURL17165","23010"),  
    
    URL19185("BALANCEGENERALPORAUXILIARESCONTROLADORURL19185","13001"),  
    
    URL22291("BALANCEGENERALPORAUXILIARESCONTROLADORURL22291","34003"),  
    
    URL11513("BALANCEGENERALPORAUXILIARESCONTROLADORURL11513","16005"),  
    
    URL12468("BALANCEGENERALPORAUXILIARESCONTROLADORURL12468","16003");

    private final String key;
    private final String value;

    private BalanceGeneralPorAuxiliaresControladorUrlEnum(String key, String value) {
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
