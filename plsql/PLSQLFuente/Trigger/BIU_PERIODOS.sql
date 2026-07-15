create or replace TRIGGER "BIU_PERIODOS"  
/*
      NAME              : BIU_PERIODOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JOSE PASCUAL GOMEZ
      DATE MODIFIED     : 29/12/2021
      TIME              : 09:20 PM
      DESCRIPTION       : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : 
*/ 
BEFORE INSERT OR UPDATE OF  COMPANIA
                           , ANO
                           , MES
                           , PERIODO
ON PERIODOS 
FOR EACH ROW 
BEGIN
    :NEW.ID:= LPAD(:NEW.ANO, 4, '0')
           || LPAD(:NEW.MES, 2, '0')
           || LPAD(:NEW.PERIODO, 2, '0');    
END;
