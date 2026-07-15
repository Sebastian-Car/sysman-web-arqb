CREATE OR REPLACE TRIGGER "BIU_PI_MES" 
  /*
        NAME              : BIU_PI_MES
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:05 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla MES, 
                            genera el NOMBRE del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF NUMERO,NOMBRE ON MES
FOR EACH ROW
BEGIN
 :NEW.NOMBRE:=PCK_SYSMAN_UTL.FC_NOMBRE_MES(:NEW.NUMERO);
END;
