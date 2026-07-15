CREATE OR REPLACE TRIGGER AI_SF_DETALLE_COBRO
/*
      NAME              : AI_SF_DETALLE_COBRO
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 29/07/2019
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Verifica si las cuentas de los movimientos tienen movimiento,de lo contrario se envia mensaje de error


  */
AFTER INSERT ON SF_DETALLE_COBRO 
FOR EACH ROW
DECLARE
  MI_NATURALEZA  VARCHAR2(1 CHAR);
BEGIN  
  MI_NATURALEZA:= PCK_FACT_GENERAL_COM4.FC_VALIDARCUENTACONCEPTO(UN_COMPANIA   => :NEW.COMPANIA,
                                                                 UN_ANO        => :NEW.ANO,
                                                                 UN_TIPOCOBRO  => :NEW.TIPOCOBRO,
                                                                 UN_CONCEPTO   => :NEW.CONCEPTO    );
END;