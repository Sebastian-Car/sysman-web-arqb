CREATE OR REPLACE TRIGGER "BIU_TERCERO_NOMBRE" 
  /*
        NAME              : BIU_TERCERO 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 06/02/2017
        TIME              : 05:50 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla TERCERO, 
                            genera el NOMBRE y el DIGITOVERIFICACION del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF APELLIDO1,APELLIDO2,NOMBRE1,NOMBRE2,NOMBRE,NIT,DIGITOVERIFICACION ON TERCERO
FOR EACH ROW
BEGIN
 :NEW.NOMBRE :=TRIM(PCK_SYSMAN_UTL.FC_NOMBRECOMPLETO(:NEW.COMPANIA,:NEW.NOMBRE1,:NEW.NOMBRE2,:NEW.APELLIDO1,:NEW.APELLIDO2));
 :NEW.DIGITOVERIFICACION:=PCK_SYSMAN_UTL.FC_DCH(:NEW.NIT);
END;
