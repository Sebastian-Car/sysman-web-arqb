CREATE OR REPLACE TRIGGER "BD_SP_D_ABONOS" 
  /*
      NAME              : BD_SP_D_ABONOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 17/05/2017
      TIME              : 11:30 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Reversa los detalles de los abonos en d_recaudo y facturado.

  */
FOR DELETE ON SP_D_ABONOS
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

MI_POS          NUMBER DEFAULT 0;
MI_RTA          NUMBER DEFAULT 0;
MI_CAMBIOCICLO  SP_USUARIO.CAMBIOCICLORUTA%TYPE;


BEFORE EACH ROW IS  --Ejecución antes de cada fila
BEGIN
   SELECT CAMBIOCICLORUTA
   INTO   MI_CAMBIOCICLO
   FROM   SP_USUARIO
   WHERE  COMPANIA   = :OLD.COMPANIA
     AND  CICLO      = :OLD.CICLO
     AND  CODIGORUTA = :OLD.CODIGORUTA;

    --VALIDA POR CADA FILA QUE SE PUEDA ELIMINAR
   MI_RTA :=0;
   IF MI_CAMBIOCICLO = 0 THEN
     MI_RTA := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_VALIDARABONO
                (UN_COMPANIA     => :OLD.COMPANIA
                ,UN_CODIGORUTA   => :OLD.CODIGORUTA
                ,UN_CICLO        => :OLD.CICLO
                ,UN_ANO          => :OLD.ANO
                ,UN_PERIODO      => :OLD.PERIODO
                ,UN_ACCION       => 'DETALLE');
   END IF;
END BEFORE EACH ROW;

AFTER EACH ROW IS --Ejecución despues de cada fila,
BEGIN
  IF MI_RTA <>0 THEN    --Si cumple la validación.
    BEGIN
        --Reversa el abono por cada concepto.
        PCK_SERVICIOS_PUBLICOS_ABONOS.PR_ELIMINARABONODETALLE
            ( UN_COMPANIA           => :OLD.COMPANIA
             ,UN_CODIGORUTA         => :OLD.CODIGORUTA
             ,UN_CICLO              => :OLD.CICLO
             ,UN_ANO                => :OLD.ANO
             ,UN_PERIODO            => :OLD.PERIODO
             ,UN_CONSECUTIVOABONO   => :OLD.CONSECUTIVO
             ,UN_CONCEPTO           => :OLD.CONCEPTO
             ,UN_ABONOPERIODO       => :OLD.VALORACT
             ,UN_ABONODEUDA         => :OLD.VALORANT
             ,UN_USUARIO            => :OLD.CREATED_BY);
    END;
  END IF;

END AFTER EACH ROW;

/*
AFTER STATEMENT IS  --Ejecución despues de una consulta DML
BEGIN

END IF;
END AFTER STATEMENT;
*/

END;
