CREATE OR REPLACE TRIGGER BIUD_D_TRANSACCIONES 
/*
      NAME              : BIUD_D_TRANSACCIONES
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JOSE PASCUAL GOMEZ BLANCO
      DATE MODIFIED     : 04/12/2018
      TIME              : 09:05 AM
      DESCRIPTION       : Se ajusta el proceso para que no permite cambiar la transacción si ya esta validada en contabilidad               
*/ 
BEFORE DELETE OR INSERT OR UPDATE ON D_TRANSACCIONES 
FOR EACH ROW 
BEGIN
    PCK_TRANS_AUTOMATICAS.PR_CONTROLAR_TRANSACCION( 
                              UN_COMPANIA       => :NEW.COMPANIA,
                              UN_ANO            => :NEW.ANO,
                              UN_TIPO           => :NEW.TIPO,
                              UN_NUMERO_MODELO  => :NEW.NUMERO_MODELO,
                              UN_NUMERO         => :NEW.NUMERO); 
END;