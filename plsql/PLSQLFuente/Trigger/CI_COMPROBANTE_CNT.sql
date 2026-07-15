CREATE OR REPLACE TRIGGER "CI_COMPROBANTE_CNT" 
BEFORE INSERT 
ON COMPROBANTE_CNT
FOR EACH ROW
DECLARE
  MI_NUMERO NUMBER(20) DEFAULT 0;
  MI_VALIDA NUMBER(1,0) DEFAULT 0;
  --PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN 
  /*
  IF :OLD.NUMERO IS NULL THEN
    MI_VALIDA:=1;
  END IF;
  */
  MI_NUMERO:=MI_NUMERO+1;
  --Se realiza para dejar pasar las transacciones en bloque que ya tiene el número de comprobante generado
  IF MI_NUMERO>1 AND MI_VALIDA<>0 THEN
    BEGIN
    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD    => SQLCODE,
                             UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INSERTA_COMPROBANTE);
    END;   
  END IF;
/*
  :NEW.NUMERO:=PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(
                              UN_COMPANIA   => :NEW.COMPANIA, 
                              UN_ANIO       => :NEW.ANO, 
                              UN_TIPO       => :NEW.TIPO,
                              UN_PORCENTRO  => UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(
                                                    UN_COMPANIA => :NEW.COMPANIA, 
                                                    UN_NOMBRE   => 'MANEJA CONSECUTIVO POR CENTRO DE COSTO', 
                                                    UN_MODULO   => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                                    UN_FECHA_PAR=> SYSDATE), 'NO')),
                              UN_NUMERO       => :OLD.NUMERO,
                              UN_CENTRO_COSTO => :NEW.CENTRO_COSTO );
                              */
  :NEW.NUMERO:=PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(
                              UN_COMPANIA   => :NEW.COMPANIA, 
                              UN_ANIO       => :NEW.ANO, 
                              UN_TIPO       => :NEW.TIPO,
                              UN_NUMERO       => :NEW.NUMERO,
                              UN_CENTRO_COSTO => :NEW.CENTRO_COSTO );
  --COMMIT;
END;
