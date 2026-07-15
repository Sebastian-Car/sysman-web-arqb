CREATE OR REPLACE TRIGGER "AIUD_DETALLE_COMPRO_CNT_ACTCON"  
/*
      NAME              : AIUD_DETALLE_COMPROBANTE_CNT
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 27/01/2017
      TIME              : 03:00 PM
      DESCRIPTION       : Genera la actualización de los saldos contables al momento de realizar un detalle en un comprobante
                          
  */
AFTER INSERT OR DELETE OR UPDATE OF COMPANIA,
                                    FECHA,   
                                    CUENTA,
                                    TERCERO,
                                    SUCURSAL,
                                    AUXILIAR,
                                    CENTRO_COSTO,
                                    REFERENCIA,
                                    FUENTE_RECURSO,
                                    VALOR_DEBITO,
                                    VALOR_CREDITO                                          
ON DETALLE_COMPROBANTE_CNT 
FOR EACH ROW
DECLARE
  MI_RTA PCK_SUBTIPOS.TI_RTA_ACME;
  MI_INDICADOR NUMBER;
BEGIN

    IF NVL(SYS_CONTEXT('SYSMAN_CTX','CIERRECONTABLE'), '0') = '1' THEN
        RETURN;
    END IF;

  MI_INDICADOR:=0;
  IF UPDATING THEN
    IF :OLD.COMPANIA       <>:NEW.COMPANIA OR
       :OLD.FECHA          <>:NEW.FECHA OR
       :OLD.CUENTA         <>:NEW.CUENTA OR
       :OLD.TERCERO        <>:NEW.TERCERO OR
       :OLD.SUCURSAL       <>:NEW.SUCURSAL OR
       :OLD.AUXILIAR       <>:NEW.AUXILIAR OR
       :OLD.CENTRO_COSTO   <>:NEW.CENTRO_COSTO OR
       :OLD.REFERENCIA     <>:NEW.REFERENCIA OR
       :OLD.FUENTE_RECURSO <>:NEW.FUENTE_RECURSO  THEN
      --Si cambia alguna variable sobre la cual se arman los id se envia el antes y despues con valores en cero 
      MI_INDICADOR:=1;
    ELSE
      --Si solo cambia els valor se envia una sola vez para evitar dos llamados
      PCK_CONTABILIDAD.PR_ACTCONTA( UN_COMPANIA      => :NEW.COMPANIA
                                   ,UN_FECHA         => :NEW.FECHA
                                   ,UN_CODIGO        => :NEW.CUENTA
                                   ,UN_TERCERO       => :NEW.TERCERO
                                   ,UN_SUCURSAL      => :NEW.SUCURSAL
                                   ,UN_AUXILIAR      => :NEW.AUXILIAR
                                   ,UN_CENTROCOSTO   => :NEW.CENTRO_COSTO
                                   ,UN_REFERENCIA    => :NEW.REFERENCIA
                                   ,UN_FUENTERECURSO => :NEW.FUENTE_RECURSO
                                   ,UN_DEBITO        => :NEW.VALOR_DEBITO
                                   ,UN_CREDITO       => :NEW.VALOR_CREDITO
                                   ,UN_DEBITO_ANT    => :OLD.VALOR_DEBITO
                                   ,UN_CREDITO_ANT   => :OLD.VALOR_CREDITO
                                   ,UN_IND_CIE       => :NEW.CIERRE);
    END IF;
  END IF;
  
  IF DELETING OR MI_INDICADOR<>0  THEN
     PCK_CONTABILIDAD.PR_ACTCONTA( UN_COMPANIA      => :OLD.COMPANIA
                                  ,UN_FECHA         => :OLD.FECHA
                                  ,UN_CODIGO        => :OLD.CUENTA
                                  ,UN_TERCERO       => :OLD.TERCERO
                                  ,UN_SUCURSAL      => :OLD.SUCURSAL
                                  ,UN_AUXILIAR      => :OLD.AUXILIAR
                                  ,UN_CENTROCOSTO   => :OLD.CENTRO_COSTO
                                  ,UN_REFERENCIA    => :OLD.REFERENCIA
                                  ,UN_FUENTERECURSO => :OLD.FUENTE_RECURSO
                                  ,UN_DEBITO        => 0
                                  ,UN_CREDITO       => 0
                                  ,UN_DEBITO_ANT    => :OLD.VALOR_DEBITO
                                  ,UN_CREDITO_ANT   => :OLD.VALOR_CREDITO
                                  ,UN_IND_CIE       => :OLD.CIERRE); 
  END IF;
  IF INSERTING  OR MI_INDICADOR<>0 THEN 
     PCK_CONTABILIDAD.PR_ACTCONTA(UN_COMPANIA       => :NEW.COMPANIA
                                  ,UN_FECHA         => :NEW.FECHA
                                  ,UN_CODIGO        => :NEW.CUENTA
                                  ,UN_TERCERO       => :NEW.TERCERO
                                  ,UN_SUCURSAL      => :NEW.SUCURSAL
                                  ,UN_AUXILIAR      => :NEW.AUXILIAR
                                  ,UN_CENTROCOSTO   => :NEW.CENTRO_COSTO
                                  ,UN_REFERENCIA    => :NEW.REFERENCIA
                                  ,UN_FUENTERECURSO => :NEW.FUENTE_RECURSO
                                  ,UN_DEBITO        => :NEW.VALOR_DEBITO
                                  ,UN_CREDITO       => :NEW.VALOR_CREDITO
                                  ,UN_DEBITO_ANT    => 0
                                  ,UN_CREDITO_ANT   => 0
                                  ,UN_IND_CIE       => :NEW.CIERRE); 
  END IF;  

END;