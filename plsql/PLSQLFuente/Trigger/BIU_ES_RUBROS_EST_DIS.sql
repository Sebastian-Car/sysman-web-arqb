CREATE OR REPLACE TRIGGER "BIU_ES_RUBROS_EST_DIS" 
  /*
        NAME              : BIU_ES_RUBROS_EST_DIS 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 06/02/2017
        TIME              : 04:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla ES_RUBROS_EST_DIS, 
                            genera el ID del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF COMPANIA,ANO,CODIGO_CUENTA,CENTRO_COSTO,AUXILIAR,ID ON ES_RUBROS_EST_DIS 
FOR EACH ROW
BEGIN
 :NEW.ID:= PCK_SYSMAN_UTL.FC_CODIGO_PPTAL(:NEW.COMPANIA,:NEW.ANO,:NEW.CODIGO_CUENTA,:NEW.CENTRO_COSTO,NULL,NULL,:NEW.AUXILIAR,NULL,NULL);
END;
