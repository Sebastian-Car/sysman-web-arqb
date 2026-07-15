CREATE OR REPLACE TRIGGER "BIU_TIPOCLASIFICADOR"     
  /*
        NAME              : BIU_TIPOCLASIFICADOR
        AUTHORS           : CAMILO ANDRES PEREZ DUEÑAS
        DATE MIGRADOR     : 23/12/2021
        TIME              : 04:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla TIPOCLASIFICADOR, 
                            genera el ID del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF CLASECLASIFICADOR,CODIGO,ID ON TIPOCLASIFICADOR
FOR EACH ROW
BEGIN
 :NEW.ID:= :NEW.CLASECLASIFICADOR||:NEW.CODIGO;
END;