CREATE OR REPLACE TRIGGER "BIU_REFERENCIA_MOV"  
/*
      NAME              : BIU_REFERENCIA_MOV
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 31/01/2017
      TIME              : 10:50 AM
      DESCRIPTION       : SE AJUSTA AL ESTANDAR                          
*/
BEFORE INSERT OR UPDATE OF   COMPANIA
                           , ANO
                           , CODIGO
                           , MOVIMIENTO 
                        ON REFERENCIA
FOR EACH ROW 

DECLARE
PRAGMA AUTONOMOUS_TRANSACTION;
VAR          NUMBER          := 0;
MI_CON       NUMBER          := 0;
MI_CAMPOS    VARCHAR2(32000);
MI_CONDICION VARCHAR2(32000);
GL_RTA       VARCHAR2(32000);
BEGIN
 -- IMPIDE MODIFICAR EL MOVIMIENTO A LA REFERENCIA VARIOS
  BEGIN  
    IF :OLD.CODIGO = PCK_DATOS.CONS_REFERENCIA THEN
      IF :NEW.MOVIMIENTO NOT IN(-1) THEN
          RAISE PCK_EXCEPCIONES.EXC_GENERAL;
      END IF;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'REFERENCIA',
                 UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_ACTU_REFVARIOS
               );
  END;
  --controlar que las cuentas padres no tengan movimientos (detalles) o saldos iniciales
  BEGIN 
    SELECT COUNT(COMPANIA)
      INTO MI_CON
      FROM DETALLE_COMPROBANTE_CNT
     WHERE COMPANIA = :NEW.COMPANIA
       AND ANO = :NEW.ANO
       AND REFERENCIA NOT IN( :NEW.CODIGO)
       AND ((REFERENCIA  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
             AND REFERENCIA = SUBSTR(:NEW.CODIGO,1,LENGTH(REFERENCIA))
             AND REFERENCIA IN(:NEW.CODIGO)
            )
            --Cuando tenga referencias hijas y a demas la nueva cuenta tenga movimientos o auxiliares
            OR 
            (REFERENCIA  BETWEEN :NEW.CODIGO AND :NEW.CODIGO || PCK_DATOS.CONS_REFERENCIA
             AND :NEW.CODIGO = SUBSTR(REFERENCIA,1,LENGTH(:NEW.CODIGO))
             AND :NEW.MOVIMIENTO NOT IN(0)
            )
           );
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_CON := 0;
  END;
  BEGIN  
    IF MI_CON NOT IN(0) THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      --RAISE_APPLICATION_ERROR(-20000,MI_CON || '-' || :NEW.CODIGO || ' Existen movimientos para las referencias padres o hijas a la que se desea crear '||SQLERRM );
      --RETURN;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_TABLAERROR => 'REFERENCIA'
               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_MOV_REFERE
         );
  END;
  MI_CON:=0;
   -- validar si tiene saldos iniciales
  BEGIN 
    SELECT COUNT(COMPANIA)
    INTO   MI_CON
    FROM   SALDOSINICIALES
    WHERE COMPANIA = :NEW.COMPANIA
      AND ANO = :NEW.ANO
      AND REFERENCIA NOT IN( :NEW.CODIGO)
      AND ((REFERENCIA  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
      AND REFERENCIA = SUBSTR(:NEW.CODIGO,1,LENGTH(REFERENCIA))
      ) OR 
      --Cuando tenga referencias hijas y a demas la nueva cuenta tenga movimientos o auxiliares
      (
      REFERENCIA  BETWEEN :NEW.CODIGO AND :NEW.CODIGO || PCK_DATOS.CONS_REFERENCIA
      AND :NEW.CODIGO = SUBSTR(REFERENCIA,1,LENGTH(:NEW.CODIGO))
      AND :NEW.MOVIMIENTO NOT IN(0)
      ))
      AND CONTABILIZADO NOT IN(0);
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_CON := 0;
  END; 
  BEGIN 
    IF MI_CON NOT IN(0) THEN
       RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      --RAISE_APPLICATION_ERROR(-20000,'Existen saldos iniciales para las referencias padres o hijas a la que se desea crear '||SQLERRM);
      --RETURN;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_TABLAERROR => 'REFERENCIA'
               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_SALDOINIC
         );
  END;
  MI_CON:=0;
  --que no permita movimiento o auxiliares a cuentas padres
  IF :NEW.MOVIMIENTO NOT IN(0) THEN
    BEGIN 
      SELECT COUNT(CODIGO) 
        INTO MI_CON
        FROM REFERENCIA  
       WHERE COMPANIA    =  :NEW.COMPANIA
         AND ANO         =  :NEW.ANO
         AND CODIGO      <> :NEW.CODIGO
         AND CODIGO      BETWEEN :NEW.CODIGO AND :NEW.CODIGO || PCK_DATOS.CONS_REFERENCIA
         AND :NEW.CODIGO = SUBSTR(CODIGO,1,LENGTH(:NEW.CODIGO));
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_CON := 0;
    END; 
  END IF;
  BEGIN 
    IF MI_CON NOT IN (0) THEN
      --RAISE_APPLICATION_ERROR(-20000, 'No se crear teniendo referencias a nivel inferior' || SQLERRM);
      --RETURN;
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_TABLAERROR => 'REFERENCIA'
               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_REFNVLINF
         );
  END;
  --que no permita ingresar hijas si hay padres con indicadores
  MI_CON:=0;
  BEGIN 
    SELECT COUNT(CODIGO) 
      INTO MI_CON
      FROM REFERENCIA  
     WHERE COMPANIA   =  :NEW.COMPANIA
       AND ANO        =  :NEW.ANO
       AND CODIGO     <> :NEW.CODIGO
       AND CODIGO     BETWEEN SUBSTR(:NEW.CODIGO,1,1) AND :NEW.CODIGO
       AND CODIGO     = SUBSTR(:NEW.CODIGO,1,LENGTH(CODIGO))
       AND MOVIMIENTO NOT IN(0);
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_CON := 0;
  END; 
  BEGIN 
    IF MI_CON NOT IN (0) THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento teniendo referencias a nivel superior con movimiento' ||SQLERRM );
      --RETURN;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_TABLAERROR => 'REFERENCIA'
               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_REFNVLSUPE
         );
  END;
END;
