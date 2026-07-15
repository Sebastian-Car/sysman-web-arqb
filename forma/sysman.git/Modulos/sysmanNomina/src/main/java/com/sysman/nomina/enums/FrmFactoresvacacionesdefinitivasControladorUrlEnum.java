/*-
 * FrmFactoresvacacionesdefinitivasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 9/09/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.nomina.enums;

 /**
  * Enumeracion que permite clasificar cada uno de los identificadores 
  * geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
  * 
  * @version 1.0, 9/09/2020
  * @author dcastiblanco
  *
  */
public enum FrmFactoresvacacionesdefinitivasControladorUrlEnum {
   
        URL4061("FRMFACTORESVACACIONESDEFINITIVASCONTROLADORURL4061", "471002"),

        URL4062("FRMFACTORESVACACIONESDEFINITIVASCONTROLADORURL4062", "7024"),

        URL4063("FRMFACTORESVACACIONESDEFINITIVASCONTROLADORURL4063", "471003");

        private final String key;
        private final String value;

        private FrmFactoresvacacionesdefinitivasControladorUrlEnum(String key, String value)
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
