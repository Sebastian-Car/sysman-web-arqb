CREATE OR REPLACE TRIGGER "AIUD_DETALLE_COMPRO_CNT_CHEQU"  
/*
      NAME              : AIUD_DETALLE_COMPRO_CNT_CHEQU
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOS´PASCUAL GÓMEZ BLANCO
      DATE MIGRADOR     : 30/05/2017
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : REALIZA LA ACTUALIZACIÓN DE LOS CHEQUES DE LOS DETALLES DEL COMPROBANTE CONTABLE
                          
  */
AFTER INSERT OR DELETE OR UPDATE OF COMPANIA, CUENTA, ANO, NRO_DOCUMENTO ON DETALLE_COMPROBANTE_CNT 
FOR EACH ROW
DECLARE
  MI_RTA PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN
  IF  :OLD.COMPANIA      IS NOT NULL AND
      :OLD.CUENTA        IS NOT NULL AND
      :OLD.ANO           IS NOT NULL AND
      :OLD.NRO_DOCUMENTO IS NOT NULL THEN
    IF DELETING THEN    
      MI_RTA:= PCK_CONTABILIDAD6.FC_ACTUALIZARCHEQUES( UN_COMPANIA           => :OLD.COMPANIA
                                                      ,UN_CUENTA             => :OLD.CUENTA
                                                      ,UN_ANIO               => :OLD.ANO 
                                                      ,UN_CNRODOCUMENTO      => :OLD.NRO_DOCUMENTO) ;
    END IF;    
    IF INSERTING THEN
      MI_RTA:= PCK_CONTABILIDAD6.FC_ACTUALIZARCHEQUES( UN_COMPANIA           => :NEW.COMPANIA
                                                      ,UN_CUENTA             => :NEW.CUENTA
                                                      ,UN_ANIO               => :NEW.ANO 
                                                      ,UN_CNRODOCUMENTO      => :NEW.NRO_DOCUMENTO) ;
    END IF;
    IF UPDATING THEN
      MI_RTA:= PCK_CONTABILIDAD6.FC_ACTUALIZARCHEQUES( UN_COMPANIA           => :OLD.COMPANIA
                                                      ,UN_CUENTA             => :OLD.CUENTA
                                                      ,UN_ANIO               => :OLD.ANO 
                                                      ,UN_CNRODOCUMENTO      => :OLD.NRO_DOCUMENTO) ;
      MI_RTA:= PCK_CONTABILIDAD6.FC_ACTUALIZARCHEQUES( UN_COMPANIA           => :NEW.COMPANIA
                                                      ,UN_CUENTA             => :NEW.CUENTA
                                                      ,UN_ANIO               => :NEW.ANO 
                                                      ,UN_CNRODOCUMENTO      => :NEW.NRO_DOCUMENTO) ;
    END IF;
  END IF;
  
END;
