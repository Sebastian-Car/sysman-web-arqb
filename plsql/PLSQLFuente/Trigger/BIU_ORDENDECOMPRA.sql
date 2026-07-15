CREATE OR REPLACE TRIGGER "BIU_ORDENDECOMPRA"  
  /*
        NAME              : BIU_ORDENDECOMPRA
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Sergio Esteban Piña Vargas
        DATE MIGRADOR     : 02/10/2017
        TIME              : 12:28 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla ORDENDECOMPRA, 
                            actualiza el valor del campo FECHAINICIO según el valor que tome el  
                            campo FECHA
        */
BEFORE INSERT OR UPDATE OF FECHA,FECHAINICIO ON ORDENDECOMPRA 
FOR EACH ROW
BEGIN
  :NEW.FECHAINICIO := :NEW.FECHA;
END;
