/**
 * 
 */
package com.sysman.workflow.enums;

/**
 * @author avega
 *
 */
public enum FrmrepnovedadesprgControladorUrlEnum {

	 URL988010("FRMREPNOVEDADESPRGCONTROLADOR988010","988010"),
	  
	  URL988012("FRMREPNOVEDADESPRGCONTROLADOR988012","988012"),
	  
	  URL62105("FRMREPNOVEDADESPRGCONTROLADOR62105","62105"),
	  
	  URL62107("FRMREPNOVEDADESPRGCONTROLADORURL62107","62107");
	
   private final String key;
   private final String value;

   private FrmrepnovedadesprgControladorUrlEnum(String key, String value) {
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
