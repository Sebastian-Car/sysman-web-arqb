CREATE OR REPLACE TRIGGER "BIU_NAT_EVALUACION"  
/*
      NAME              : BIU_NAT_EVALUACION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 08/02/2018
      TIME              : 15:45 PM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : ACTUALIZA EL PUNTAJE DE CALIFICACION A PARTIR DE LOS 
                          PARAMETROS 
                          
  */
BEFORE INSERT OR UPDATE ON NAT_EVALUACION
FOR EACH ROW

DECLARE

  MI_TOTAL               NUMBER;


BEGIN



  MI_TOTAL := 0;


  IF INSERTING  THEN
  
  :NEW.EV_PUNTOBJEDESE := :NEW.EV_P_PLAN  + :NEW.EV_P_UTILRECU  + :NEW.EV_P_CALI  + :NEW.EV_P_COMPTECN + :NEW.EV_P_RESP;  
 
  
  :NEW.EV_TOT_EV_FAC_DES := :NEW.EV_PUNTOBJEDESE * ((PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => :NEW.COMPANIA,
                                                                          UN_NOMBRE => 	 'PORCENTAJE DE EVALUACION FACTORES DE DESEMPEÑO',
                                                                          UN_MODULO =>   PCK_DATOS.MODULOHOJASDEVIDA,
                                                                          UN_FECHA_PAR => SYSDATE,
                                                                          UN_IND_MAYUS => -1))/100);       
                                                                          
                                                                          
  END IF;
  
  IF UPDATING THEN
  
      IF :OLD.EV_P_PLAN <> :NEW.EV_P_PLAN THEN      
        MI_TOTAL := MI_TOTAL + :NEW.EV_P_PLAN;
      ELSE
        MI_TOTAL := MI_TOTAL + :OLD.EV_P_PLAN;
      END IF;
      
      IF :OLD.EV_P_UTILRECU <> :NEW.EV_P_UTILRECU THEN      
        MI_TOTAL := MI_TOTAL + :NEW.EV_P_UTILRECU;        
      ELSE
        MI_TOTAL := MI_TOTAL + :OLD.EV_P_UTILRECU;        
      END IF;
      
      
      IF :OLD.EV_P_CALI <> :NEW.EV_P_CALI THEN      
        MI_TOTAL := MI_TOTAL + :NEW.EV_P_CALI;
       ELSE
        MI_TOTAL := MI_TOTAL + :OLD.EV_P_CALI;        
      END IF;
            
      IF :OLD.EV_P_COMPTECN <> :NEW.EV_P_COMPTECN THEN
        MI_TOTAL := MI_TOTAL + :NEW.EV_P_COMPTECN;
      ELSE
        MI_TOTAL := MI_TOTAL + :OLD.EV_P_PLAN;
      END IF;
      
      IF :OLD.EV_P_RESP <> :NEW.EV_P_RESP THEN
        MI_TOTAL := MI_TOTAL + :NEW.EV_P_RESP;
      ELSE
        MI_TOTAL := MI_TOTAL + :OLD.EV_P_PLAN;
      END IF;
      
  
      :NEW.EV_PUNTOBJEDESE := MI_TOTAL;  
 
  
      :NEW.EV_TOT_EV_FAC_DES := :NEW.EV_PUNTOBJEDESE * ((PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => :OLD.COMPANIA,
                                                                          UN_NOMBRE => 	 'PORCENTAJE DE EVALUACION FACTORES DE DESEMPEÑO',
                                                                          UN_MODULO =>   PCK_DATOS.MODULOHOJASDEVIDA,
                                                                          UN_FECHA_PAR => SYSDATE,
                                                                          UN_IND_MAYUS => -1))/100);       
                                                                          
                                                                          
  END IF;

END;
