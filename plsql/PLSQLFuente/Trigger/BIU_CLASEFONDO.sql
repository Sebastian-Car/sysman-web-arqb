CREATE OR REPLACE TRIGGER "BIU_CLASEFONDO" 
  /*
        NAME              : BIU_CLASEFONDO 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 06/02/2017
        TIME              : 04:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla CLASEFONDO, 
                            genera el ID del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF CLASE,ID_DE_FONDO,ID ON CLASEFONDO
FOR EACH ROW
BEGIN
 :NEW.ID:= :NEW.CLASE||:NEW.ID_DE_FONDO;
END;
