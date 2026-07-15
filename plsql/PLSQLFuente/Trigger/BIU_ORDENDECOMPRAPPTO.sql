CREATE OR REPLACE TRIGGER "BIU_ORDENDECOMPRAPPTO" 
  /*
        NAME              : BIU_ORDENDECOMPRAPPTO 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 06/02/2017
        TIME              : 04:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla ORDENDECOMPRAPPTO, 
                            genera el ID del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF COMPANIA,ANOPPTO,RUBRO,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,REFERENCIA,FUENTE_RECURSO,ID ON ORDENDECOMPRAPPTO
FOR EACH ROW
BEGIN
 :NEW.ID:= PCK_SYSMAN_UTL.FC_CODIGO_PPTAL(:NEW.COMPANIA,:NEW.ANOPPTO,:NEW.RUBRO,:NEW.CENTRO_COSTO,:NEW.TERCERO,:NEW.SUCURSAL,:NEW.AUXILIAR,:NEW.REFERENCIA,:NEW.FUENTE_RECURSO);
END;
