CREATE OR REPLACE TRIGGER "BIU_COMPROBANTE_CNT" 
  /*
        NAME              : BIU_COMPROBANTE_CNT
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : Carlos Alberto Manrique Palacios
        DATE MIGRADOR     : 07/02/2017
        TIME              : 08:20 AM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Trigger antes de insertar y actualizar para la tabla COMPROBANTE_CNT, 
                            genera el campos relacionados con la fecha para registro insertado o modificado.
                            Se crea este trigger al eliminar las columnas vituales de la base de datos
        */
BEFORE INSERT OR UPDATE OF FECHA,DIA,MES,  TERCERO, SUCURSAL, BANCO  ON COMPROBANTE_CNT
FOR EACH ROW  
BEGIN
  
  
 :NEW.MES:=TO_NUMBER(TO_CHAR(:NEW.FECHA,'MM'));
 :NEW.DIA:= TO_NUMBER(TO_CHAR(:NEW.FECHA,'DD'));
 
 IF :NEW.BANCO IS NOT NULL AND :NEW.CUENTABANCO IS NULL THEN
  BEGIN
      SELECT CUENTA
        INTO :NEW.CUENTABANCO
      FROM TERCEROPAGOS
      WHERE COMPANIA = :NEW.COMPANIA
        AND NIT      = :NEW.TERCERO
        AND SUCURSAL = :NEW.SUCURSAL
        AND BANCO    = :NEW.BANCO
        AND ACTIVA NOT IN(0)
        AND ROWNUM<=1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    NULL;    
  END;
 END IF;  
 
END;
