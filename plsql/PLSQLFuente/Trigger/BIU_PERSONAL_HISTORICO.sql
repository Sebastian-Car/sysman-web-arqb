CREATE OR REPLACE TRIGGER "BIU_PERSONAL_HISTORICO" 
  /*
        NAME              : BIU_PERSONAL_HISTORICO
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:25 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla PERSONAL_HISTORICO, 
                            genera el campo NOMBRECOMPLETO para registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF APELLIDO1,APELLIDO2,NOMBRES,NOMBRECOMPLETO ON PERSONAL_HISTORICO
FOR EACH ROW
BEGIN
 :NEW.NOMBRECOMPLETO:=:NEW.APELLIDO1||' '||TRIM(:NEW.APELLIDO2)||' '||:NEW.NOMBRES;
 
END;
