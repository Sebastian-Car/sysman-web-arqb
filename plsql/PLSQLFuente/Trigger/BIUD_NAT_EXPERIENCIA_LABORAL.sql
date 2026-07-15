CREATE OR REPLACE TRIGGER "BIUD_NAT_EXPERIENCIA_LABORAL"  
/*
      NAME              : BIUD_NAT_EXPERIENCIA_LABORAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 28/03/2018
      TIME              : 10:30 AM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Actualiza la experiencia laboral de la tabla NAT_DATOS_PERSONALES,luego de crear,editar o eliminar
                          datos en la tabla NAT_DATOS_PERSONALES
                          
                          
  */
BEFORE INSERT OR UPDATE OR DELETE ON NAT_EXPERIENCIA_LABORAL
FOR EACH ROW

BEGIN

  IF INSERTING OR UPDATING THEN  
    PCK_HOJAS_DE_VIDA.PR_ACTEXPLABORALPERSONALES(UN_COMPANIA      => :NEW.COMPANIA,
                                                  UN_NUMERO_DCTO  => :NEW.NUMERO_DCTO, 
                                                  UN_SUCURSAL     => :NEW.SUCURSAL, 
                                                  UN_CLASE        => :NEW.CLASE, 
                                                  UN_ANO          => :NEW.ANOSERVICIO, 
                                                  UN_MES          => :NEW.MESESERVICIO,
                                                  UN_DIA          => :NEW.DIASERVICIO,
                                                  UN_USUARIO      => :NEW.CREATED_BY );   
                                            
                                            
  END IF;  
  IF UPDATING OR DELETING THEN
    PCK_HOJAS_DE_VIDA.PR_ACTEXPLABORALPERSONALES(UN_COMPANIA      => :OLD.COMPANIA,
                                                  UN_NUMERO_DCTO  => :OLD.NUMERO_DCTO, 
                                                  UN_SUCURSAL     => :OLD.SUCURSAL, 
                                                  UN_CLASE        => :OLD.CLASE, 
                                                  UN_ANO          => :OLD.ANOSERVICIO * -1, 
                                                  UN_MES          => :OLD.MESESERVICIO * -1,
                                                  UN_DIA          => :OLD.DIASERVICIO * -1,
                                                  UN_USUARIO      => :OLD.CREATED_BY ); 
  END IF;

    


END;
