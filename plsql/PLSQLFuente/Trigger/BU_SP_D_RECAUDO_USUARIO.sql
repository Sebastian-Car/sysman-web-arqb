CREATE OR REPLACE TRIGGER "BU_SP_D_RECAUDO_USUARIO" 
  /*
      NAME              : BU_SP_D_RECAUDO_USUARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 01/09/2017
      TIME              : 11:45 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Elimina y crea nuevamente los detalles, para que la información quede correcta.

  */
FOR UPDATE ON SP_D_RECAUDO_USUARIO
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

MI_CAMBIOCICLO  SP_USUARIO.CAMBIOCICLORUTA%TYPE;

AFTER EACH ROW IS --Ejecución despues de cada fila,
BEGIN
    SELECT CAMBIOCICLORUTA
    INTO   MI_CAMBIOCICLO
    FROM   SP_USUARIO
    WHERE  COMPANIA   = :OLD.COMPANIA
      AND  CICLO      = :OLD.CICLO
      AND  CODIGORUTA = :OLD.CODIGORUTA;

    IF MI_CAMBIOCICLO = 0 THEN
        --Reverza los recaudos.
        PCK_SERVICIOS_PUBLICOS_ABONOS.PR_ELIMINARRECAUDO
            ( UN_COMPANIA       => :OLD.COMPANIA
             ,UN_FECHARECAUDO   => :OLD.FECHA
             ,UN_BANCO          => :OLD.BANCO
             ,UN_PAQUETE        => :OLD.NUMEROPAQUETE
             ,UN_CONCEPTO       => :OLD.CONCEPTO
             ,UN_TIPO_RECAUDO   => :OLD.TIPOPAGO
             ,UN_VALORDEUDA     => :OLD.VALORDEUDA  
             ,UN_VALORPERIODO   => :OLD.VALORPAGOPERIODO
             ,UN_VALORFIN_ACT   => :OLD.VALORFINACT
             ,UN_VALORFIN_ANT   => :OLD.VALORFINANT
             ,UN_CREDITOABONADO => :OLD.CREDITOABONADO
             ,UN_ABONOACT       => :OLD.VALORABONOACT
             ,UN_ABONOANT       => :OLD.VALORABONOANT
             ,UN_USUARIO        => :OLD.CREATED_BY);

             --Registra los abonos en los recaudos.
        PCK_SERVICIOS_PUBLICOS_ABONOS.PR_REGISTRARRECAUDO
            (UN_COMPANIA       => :NEW.COMPANIA
            ,UN_FECHARECAUDO   => :NEW.FECHA
            ,UN_BANCO          => :NEW.BANCO
            ,UN_PAQUETE        => :NEW.NUMEROPAQUETE
            ,UN_CONCEPTO       => :NEW.CONCEPTO
            ,UN_TIPO_RECAUDO   => :NEW.TIPOPAGO
            ,UN_VALORDEUDA     => :NEW.VALORDEUDA
            ,UN_VALORPERIODO   => :NEW.VALORPAGOPERIODO
            ,UN_VALORFIN_ACT   => :NEW.VALORFINACT
            ,UN_VALORFIN_ANT   => :NEW.VALORFINANT
            ,UN_CREDITOABONADO => :NEW.CREDITOABONADO
            ,UN_ABONOACT       => :NEW.VALORABONOACT
            ,UN_ABONOANT       => :NEW.VALORABONOANT
            ,UN_USUARIO        => :NEW.CREATED_BY);
    END IF;
END AFTER EACH ROW;
END;
