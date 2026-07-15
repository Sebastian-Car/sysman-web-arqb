CREATE OR REPLACE TRIGGER "CI_MANTENIMIENTO"  
BEFORE INSERT ON MANTENIMIENTO 
FOR EACH ROW
DECLARE
  MI_NUMERO NUMBER(20) DEFAULT 0;
  
BEGIN 

  MI_NUMERO:=MI_NUMERO+1;

  :NEW.NUMERO:=PCK_MANT.FC_ENUMERARMANTENIMIENTO(
                              UN_COMPANIA   => :NEW.COMPANIA, 
                              UN_ANIO       => :NEW.ANO, 
                              UN_TIPO       => :NEW.TIPO,
                              UN_NUMERO       => :OLD.NUMERO);
END;
