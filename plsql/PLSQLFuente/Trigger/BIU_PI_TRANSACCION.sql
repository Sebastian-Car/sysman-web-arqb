CREATE OR REPLACE TRIGGER "BIU_PI_TRANSACCION" 
  /*
        NAME              : BIU_PI_TRANSACCION 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 06/02/2017
        TIME              : 04:05 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla PI_TRANSACCION, 
                            genera el campos relacionados con la fecha del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF FECHA,DIA,MES ON PI_TRANSACCION
FOR EACH ROW
BEGIN
 :NEW.DIA:=TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD'));
 :NEW.MES:=TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM'));
END;
