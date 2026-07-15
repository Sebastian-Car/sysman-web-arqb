/*
* BalanceConAuxiliaresControladorUrlEnum
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
public enum BalanceConAuxiliaresControladorUrlEnum {
   
    URL16130("BALANCECONAUXILIARESCONTROLADORURL16130","14001"),  
    
    URL18172("BALANCECONAUXILIARESCONTROLADORURL18172","23010"),  
    
    URL15101("BALANCECONAUXILIARESCONTROLADORURL15101","14001"),  
    
    URL20207("BALANCECONAUXILIARESCONTROLADORURL20207","13001"),  
    
    URL21231("BALANCECONAUXILIARESCONTROLADORURL21231","34001"),  
    
    URL14266("BALANCECONAUXILIARESCONTROLADORURL14266","20013"),  
             	
    URL11002("BALANCECONAUXILIARESCONTROLADORURL11002","4013"),  
    
    URL13433("BALANCECONAUXILIARESCONTROLADORURL13433","20013"),  
    
    URL17165("BALANCECONAUXILIARESCONTROLADORURL17165","23010"),  
    
    URL19185("BALANCECONAUXILIARESCONTROLADORURL19185","13001"),  
    
    URL22291("BALANCECONAUXILIARESCONTROLADORURL22291","34001"),  
    
    URL11513("BALANCECONAUXILIARESCONTROLADORURL11513","16005"),  
    
    URL12468("BALANCECONAUXILIARESCONTROLADORURL12468","16005");
    
    private final String key;
    private final String value;
	
    private  BalanceConAuxiliaresControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }
	
    public String getKey() {
        return key;
    }
	
    public String getValue() {
        return value;
    }
}
