CREATE OR REPLACE TRIGGER "BU_PERIODOS" 
  /*
      NAME              : BU_PERIODOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 16/10/2018
      TIME              : 11:24 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : No permite modificar la información de indicadores de acumulados, estado y diferidos si es periodo preliminar
  */
BEFORE UPDATE ON PERIODOS

FOR EACH ROW
BEGIN

    IF :NEW.PRELIMINAR = :OLD.PRELIMINAR AND :OLD.PRELIMINAR <> 0 AND (:NEW.ACUMULADO <> 0 OR :NEW.DIFERIDOS <> 0 OR :NEW.ESTADO <> 0) THEN
        --No permitir cambiar los indicadores a periodo preliminar dado que es informativo.
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            --No se permite modificar los indicadores de acumulados, diferidos y estado a un periodo que tiene indicador de cierre de prenómina.
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_TABLAERROR =>'PERIODOS',
                UN_ERROR_COD  => PCK_ERRORES.ERR_ACTINDPERPRELIMINAR
                );
         END;
    END IF;
END BU_PERIODOS;
