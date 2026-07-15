CREATE OR REPLACE TRIGGER AU_ORDENDECOMPRA_AIU
 /*
    NAME              : AU_ORDENDECOMPRA_AIU
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Trigger se crea como compuesto debido a que al actualizar algun campo de porcentaje realice el recalculo de AIU 
    */
AFTER UPDATE OF PORADMINISTRACION,
              PORINPREVISTOS,
              PORUTILIDADES,
              PRECIOS_UNITARIOS
ON ORDENDECOMPRA
FOR EACH ROW
DECLARE
 MI_PARAM VARCHAR2(10);
BEGIN

    MI_PARAM := PCK_SYSMAN_UTL.FC_PAR(
                          UN_COMPANIA   => :NEW.COMPANIA,
                          UN_NOMBRE     => 'APLICAR AIU EN ENTRADAS DE ALMACEN',
                          UN_MODULO     => -1,
                          UN_FECHA_PAR  => SYSDATE,
                          UN_IND_MAYUS  => -1
                      );

     IF (
           :OLD.PORADMINISTRACION <> :NEW.PORADMINISTRACION
        OR :OLD.PORINPREVISTOS    <> :NEW.PORINPREVISTOS
        OR :OLD.PORUTILIDADES    <> :NEW.PORUTILIDADES
        OR :OLD.PRECIOS_UNITARIOS <> :NEW.PRECIOS_UNITARIOS
       )
       AND :NEW.PRECIOS_UNITARIOS NOT IN (0)
       AND MI_PARAM = 'SI'
    THEN
      PCK_CONTRATOS_COM2.PR_RECALCULAR_AIU(
      UN_COMPANIA         => :NEW.COMPANIA,
      UN_CLASEORDEN       => :NEW.CLASEORDEN,
      UN_NUMERO           => :NEW.NUMERO,
      UN_PORC_ADMIN       => :NEW.PORADMINISTRACION,
      UN_PORC_IMPRE       => :NEW.PORINPREVISTOS,
      UN_PORC_UTILI       => :NEW.PORUTILIDADES
    );
    END IF;
END;