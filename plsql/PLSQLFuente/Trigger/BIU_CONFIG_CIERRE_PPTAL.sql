CREATE OR REPLACE TRIGGER BIU_CONFIG_CIERRE_PPTAL 
/*
      NAME              : BIU_CONFIG_CIERRE_PPTAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
      DATE MIGRADOR     : 23/01/2019
      TIME              : 11:53 AM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Controlar la configuración del Cierre Presupuestal                        
*/ 
BEFORE INSERT OR UPDATE ON CONFIG_CIERRE_PPTAL 
FOR EACH ROW
BEGIN
  PCK_PRESUPUESTO_CIE.PR_VALIDARCONFIG(
         UN_COMPANIA          => :NEW.COMPANIA,
         UN_TIPOCIERRE        => :NEW.TIPOCIERRE,
         UN_CLASERESERVA      => :NEW.CLASERESERVA,
         UN_TIPO_DIS          => :NEW.TIPO_DIS,
         UN_TIPO_RES          => :NEW.TIPO_RES,
         UN_TIPO_REO          => :NEW.TIPO_REO,
         UN_TIPO_ADI          => :NEW.TIPO_ADI,
         UN_DIGITOSCAMBIO     => :NEW.DIGITOSCAMBIO,
         UN_TIPOVIGENCIAINICI => :NEW.TIPOVIGENCIAINICI,
         UN_TIPOVIGENCIAFINAL => :NEW.TIPOVIGENCIAFINAL,
         UN_GENERAR           => :NEW.GENERAR
   );
END;