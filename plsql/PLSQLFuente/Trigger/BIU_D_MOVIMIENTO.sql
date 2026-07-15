CREATE OR REPLACE TRIGGER "BIU_D_MOVIMIENTO"  
  /*
    NAME              : BIU_D_MOVIMIENTO
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 17/10/2018
    TIME              : 05:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : Evalúa si el valor de un elemento Devolutivo sobrepasa la cantidad de SMLVM definidos en el 
						parámetro "SMLVM CONSUMO CONTROLADO", cuando el parámetro "CONTROLAR CONSUMO CONTROLADO" posee el valor de SI
  
  */ 
BEFORE INSERT OR UPDATE OF COMPANIA,TIPOMOVIMIENTO,MOVIMIENTO,ELEMENTO,SERIE,VALORUNITARIO ON D_MOVIMIENTO  
FOR EACH ROW 
BEGIN                        
  PCK_ALMACEN_COM5.PR_EVALUARCONSUMOCONTROLADO(UN_COMPANIA         => :NEW.COMPANIA,
											   UN_TIPO_MOVIMIENTO  => :NEW.TIPOMOVIMIENTO,
											   UN_MOVIMIENTO       => :NEW.MOVIMIENTO,
											   UN_ELEMENTO         => :NEW.ELEMENTO,
											   UN_PLACA            => :NEW.SERIE,
											   UN_VALOR_UNITARIO   => :NEW.VALORUNITARIO);                          

END;
