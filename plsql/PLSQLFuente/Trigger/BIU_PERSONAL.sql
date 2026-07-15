CREATE OR REPLACE TRIGGER "BIU_PERSONAL" 
  /*
        NAME              : BIU_PERSONAL
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:20 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla PERSONAL, 
                            genera el campo NOMBRECOMPLETO para registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF APELLIDO1,APELLIDO2,NOMBRES,NOMBRECOMPLETO ON PERSONAL
FOR EACH ROW
BEGIN
 :NEW.NOMBRECOMPLETO:=:NEW.APELLIDO1||' '||TRIM(:NEW.APELLIDO2)||' '||:NEW.NOMBRES;
 
END;
