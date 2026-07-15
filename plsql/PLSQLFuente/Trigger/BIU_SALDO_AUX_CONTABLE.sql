CREATE OR REPLACE TRIGGER "BIU_SALDO_AUX_CONTABLE" 
  /*
        NAME              : BIU_SALDO_AUX_CONTABLE 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:30 PM
        MODIFIER          : Jose pascual Gómez Blanco
        DATE MODIFIED     : 17/05/2017  
        TIME              : 12:26 
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla SALDO_AUX_CONTABLE, 
                            genera el ID del registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF COMPANIA,ANO,CODIGO,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,REFERENCIA,FUENTE_RECURSO,ID ON SALDO_AUX_CONTABLE
FOR EACH ROW
BEGIN
  :NEW.ID:= PCK_SYSMAN_UTL.FC_CODIGO_CNT(:NEW.COMPANIA,:NEW.ANO,:NEW.CODIGO,:NEW.CENTRO_COSTO,:NEW.TERCERO,:NEW.SUCURSAL,:NEW.AUXILIAR,:NEW.REFERENCIA,:NEW.FUENTE_RECURSO);
  :NEW.NATURALEZA:= PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR(UN_COMPANIA        =>:NEW.COMPANIA,
                                                              UN_ANO             =>:NEW.ANO,
                                                              UN_CUENTA          =>:NEW.CODIGO,
                                                              UN_VALIDABLOQUEADO =>0);
END;
