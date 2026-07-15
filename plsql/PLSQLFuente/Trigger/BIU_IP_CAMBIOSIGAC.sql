CREATE OR REPLACE TRIGGER "BIU_IP_CAMBIOSIGAC" 
  /*
        NAME              : BIU_IP_CAMBIOSIGAC
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:10 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla IP_CAMBIOSIGAC, 
                            genera el digito de verificacion del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF NIT,DIGITO ON IP_CAMBIOSIGAC
FOR EACH ROW
BEGIN
 :NEW.DIGITO:=PCK_SYSMAN_UTL.FC_DCH(:NEW.NIT);
END;
