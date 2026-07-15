/*-
 * MovimientosporsemanasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.presupuesto.enums;

 /**
  *  Enumeracion que permite clasificar cada uno de los identificadores
  * geenerados en el refactoring y asociados al codigo legacy obtenido 
  * con patrones de busqueda.
  * 
  * @version 1.0, 6/10/2020
  * @author dcastiblanco
  *
  */
public enum MovimientosporsemanasControladorUrlEnum {
   
        URL4700("MOVIMIENTOSPORSEMANASCONTROLADORURLENUM4700", "45078"),

        URL3766("MOVIMIENTOSPORSEMANASCONTROLADORURLENUML3766", "45046");

        private final String key;
        private final String value;

        private MovimientosporsemanasControladorUrlEnum(String key, String value) {
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
