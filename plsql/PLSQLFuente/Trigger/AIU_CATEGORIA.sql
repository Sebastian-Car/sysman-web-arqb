CREATE OR REPLACE TRIGGER "AIU_CATEGORIA"  
  /*
    NAME              : AIU_CATEGORIA  
    AUTHORS           : STEFANINI SYSMAN 
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 16/01/2018
    TIME              : 17:54
    SOURCE MODULE     : NOMINAP2017.12.04
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Cuando se realiza una operacion de insercion o actualizacion en la tabla CATEGORIA,
                        se creara un nuevo registro en la tabla CARGOS
  */ 
AFTER INSERT OR UPDATE OF  ID_DE_CATEGORIA
                          ,NOMBRE_CATEGORIA 
                       ON CATEGORIA 
FOR EACH ROW 
DECLARE
  MI_USUARIO       PCK_SUBTIPOS.TI_USUARIO;  
BEGIN 
  IF UPDATING THEN
    MI_USUARIO     := :NEW.MODIFIED_BY;  
  ELSE
    MI_USUARIO     := :NEW.CREATED_BY;  
  END IF;

  PCK_NOMINA_COM7.PR_REGISTRARCARGO(UN_COMPANIA     => :NEW.COMPANIA,
                                    UN_IDCARGO      => :NEW.ESCALAFON||:NEW.ID_DE_CATEGORIA,
                                    UN_ESCALAFON    => :NEW.ESCALAFON,
                                    UN_NOMBRE_CARGO => :NEW.NOMBRE_CATEGORIA,
                                    UN_USUARIO      => MI_USUARIO);                                   
END ;
