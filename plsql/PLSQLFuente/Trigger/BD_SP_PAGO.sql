CREATE OR REPLACE TRIGGER "BD_SP_PAGO" 
  /*
      NAME              : BD_SP_PAGO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 03/10/2017
      TIME              : 11:15 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Elimina los pagos y sus detalles de recaudos

  */
FOR DELETE ON SP_PAGO
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;

/*
BEFORE EACH ROW IS  --Ejecución antes de cada fila
BEGIN
END BEFORE EACH ROW;
*/

AFTER EACH ROW IS --Ejecución despues de cada fila,
BEGIN

    --Reversa los pagos.
    PCK_SERVICIOS_PUBLICOS_ABONOS.PR_ELIMINARPAGO
        (UN_COMPANIA            => :OLD.COMPANIA
        ,UN_FECHAPAGO           => :OLD.FECHA
        ,UN_BANCOPAGO           => :OLD.BANCO
        ,UN_NUMEROPAQUETEPAGO   => :OLD.NUMEROPAQUETE
        ,UN_CONSECUTIVO         => :OLD.CONSECUTIVO
        ,UN_CODIGORUTA          => :OLD.CODIGORUTA
        ,UN_CICLO               => :OLD.CICLO
        ,UN_ANO                 => :OLD.ANO
        ,UN_PERIODO             => :OLD.PERIODO
        ,UN_VALORPAGO           => :OLD.VALORPAGO
        ,UN_OPERACION           => :OLD.OPERACION
        ,UN_USUARIO             => :OLD.CREATED_BY );

END AFTER EACH ROW;

/*
AFTER STATEMENT IS --Ejecución despues de una consulta DML
BEGIN
END AFTER STATEMENT;
*/

END;
