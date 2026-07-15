CREATE OR REPLACE TRIGGER "BIU_COMPROBANTE_PPTAL" 
  /*
        NAME              : BIU_COMPROBANTE_PPTAL
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:20 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla COMPROBANTE_PPTAL, 
                            genera el campos relacionados con la fecha para registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF FECHA,DIA,MES ON COMPROBANTE_PPTAL
FOR EACH ROW
BEGIN
 :NEW.MES:=TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM'));
 :NEW.DIA:= TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD'));
END;
