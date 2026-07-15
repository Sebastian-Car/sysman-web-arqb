CREATE OR REPLACE TRIGGER "CIU_PLAN_CONTABLE_IND"  
/*
      NAME              : BIU_PLAN_CONTABLE_IND
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 31/01/2017
      TIME              : 10:05 AM
      DESCRIPTION       : SE AJUSTA AL ESTANDAR                          
*/
  FOR INSERT OR UPDATE  OF   COMPANIA
                           , ANO
                           , CODIGO
                           , MOVIMIENTO
                           , MAN_CEN_CTO
                           , MAN_AUX_TER
                           , MAN_AUX_GEN
                           , MAN_AUX_REF
                           , MAN_AUX_FUE  
                        ON PLAN_CONTABLE 
  COMPOUND TRIGGER

  TYPE REGISTRO IS RECORD
  (
    COMPANIA     PLAN_CONTABLE.COMPANIA%TYPE   ,
    ANO          PLAN_CONTABLE.ANO%TYPE        ,
    CODIGO       PLAN_CONTABLE.CODIGO%TYPE     ,
    MOVIMIENTO   PLAN_CONTABLE.MOVIMIENTO%TYPE ,
    MAN_CEN_CTO  PLAN_CONTABLE.MAN_CEN_CTO%TYPE,
    MAN_AUX_TER  PLAN_CONTABLE.MAN_AUX_TER%TYPE,
    MAN_AUX_GEN  PLAN_CONTABLE.MAN_AUX_GEN%TYPE,
    MAN_AUX_REF  PLAN_CONTABLE.MAN_AUX_REF%TYPE,
    MAN_AUX_FUE  PLAN_CONTABLE.MAN_AUX_FUE%TYPE
  );
  TYPE REGISTROS IS TABLE OF REGISTRO INDEX BY BINARY_INTEGER;
  TABLA REGISTROS;
  POS NUMBER DEFAULT 0;
  MI_CON                NUMBER := 0;
  
  BEFORE EACH ROW IS
  BEGIN
    POS := POS +1;
    TABLA(POS).COMPANIA     := :NEW.COMPANIA;
    TABLA(POS).ANO          := :NEW.ANO;
    TABLA(POS).CODIGO       := :NEW.CODIGO;
    TABLA(POS).MOVIMIENTO   := :NEW.MOVIMIENTO;
    TABLA(POS).MAN_CEN_CTO  := :NEW.MAN_CEN_CTO;
    TABLA(POS).MAN_AUX_TER  := :NEW.MAN_AUX_TER;
    TABLA(POS).MAN_AUX_GEN  := :NEW.MAN_AUX_GEN;
    TABLA(POS).MAN_AUX_REF  := :NEW.MAN_AUX_REF;
    TABLA(POS).MAN_AUX_FUE  := :NEW.MAN_AUX_FUE;
    --
    BEGIN
      --que no permita movimiento y auxiliares
      IF :NEW.MOVIMIENTO NOT IN(0) 
         AND (:NEW.MAN_CEN_CTO 
              + :NEW.MAN_AUX_TER 
              + :NEW.MAN_AUX_GEN 
              + :NEW.MAN_AUX_REF 
              + :NEW.MAN_AUX_FUE) NOT IN(0) THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento y auxiliares'||SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_CONTABLE'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_1
           );
    END;
    --
    BEGIN
      --que no permita movimiento o auxiliares a cuentas con menos de 6 digitos
      IF LENGTH(:NEW.CODIGO) < 6 
         AND (:NEW.MOVIMIENTO 
              + :NEW.MAN_CEN_CTO 
              + :NEW.MAN_AUX_TER 
              + :NEW.MAN_AUX_GEN 
              + :NEW.MAN_AUX_REF 
              + :NEW.MAN_AUX_FUE) NOT IN(0) 
      THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento o auxiliares a cuentas con menos de 6 digitos'||SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_CONTABLE'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_2
                 );
    END;
    --
    BEGIN
      --valida indicadores seleccionados
      IF (:NEW.MOVIMIENTO 
          + :NEW.MAN_CEN_CTO 
          + :NEW.MAN_AUX_TER 
          + :NEW.MAN_AUX_GEN 
          + :NEW.MAN_AUX_REF 
          + :NEW.MAN_AUX_FUE) IN(0)
      THEN
          SELECT COUNT(COMPANIA)
            INTO MI_CON
            FROM DETALLE_COMPROBANTE_CNT
           WHERE COMPANIA = :NEW.COMPANIA
             AND ANO = :NEW.ANO
             AND CUENTA IN (:NEW.CODIGO);
          --
          IF MI_CON NOT IN(0)
          THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END IF;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_CONTABLE'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_3
           );
    END;
    --
    BEGIN
      --controlar que las cuentas padres no tengan movimientos (detalles) o saldos iniciales
      SELECT COUNT(COMPANIA)
        INTO MI_CON
        FROM DETALLE_COMPROBANTE_CNT
       WHERE COMPANIA = :NEW.COMPANIA
         AND ANO = :NEW.ANO
         AND CUENTA NOT IN( :NEW.CODIGO)
         AND ((CUENTA  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
               AND CUENTA = SUBSTR(:NEW.CODIGO,1,LENGTH(CUENTA))
               )
               --Cuando tenga cuentas hijas y a demas la nueva cuenta tenga movimientos o auxiliares
               OR 
              (
              CUENTA  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO || PCK_DATOS.CONS_MAX_ID
              AND :NEW.CODIGO = SUBSTR(CUENTA,1,LENGTH(:NEW.CODIGO))
              AND (:NEW.MOVIMIENTO
                   + :NEW.MAN_CEN_CTO 
                   + :NEW.MAN_AUX_TER 
                   + :NEW.MAN_AUX_GEN 
                   + :NEW.MAN_AUX_REF 
                   + :NEW.MAN_AUX_FUE) NOT IN(0)
              )
             );
      IF MI_CON NOT IN(0) THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        --RAISE_APPLICATION_ERROR(-20000,MI_CON || '-' || :NEW.CODIGO || ' Existen movimientos para cuentas padres o hijas a la que se desea crear '||SQLERRM );
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_CONTABLE'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_4
           );
    END;
    --
    BEGIN
      MI_CON:=0;
      -- validar si tiene saldos iniciales
      SELECT COUNT(COMPANIA)
        INTO MI_CON
        FROM SALDOSINICIALES
       WHERE COMPANIA = :NEW.COMPANIA
         AND ANO = :NEW.ANO
         AND CODIGO NOT IN( :NEW.CODIGO)
         AND ((CODIGO  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
               AND CODIGO = SUBSTR(:NEW.CODIGO,1,LENGTH(CODIGO))
              ) OR 
              --Cuando tenga cuentas hijas y a demas la nueva cuenta tenga movimientos o auxiliares
              (CODIGO  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO || PCK_DATOS.CONS_MAX_ID
               AND :NEW.CODIGO = SUBSTR(CODIGO,1,LENGTH(:NEW.CODIGO))
               AND (:NEW.MOVIMIENTO
                    + :NEW.MAN_CEN_CTO 
                    + :NEW.MAN_AUX_TER 
                    + :NEW.MAN_AUX_GEN 
                    + :NEW.MAN_AUX_REF 
                    + :NEW.MAN_AUX_FUE) NOT IN(0)
              )
             )
        AND CONTABILIZADO NOT IN (0);
        --
      IF MI_CON NOT IN(0) THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        --RAISE_APPLICATION_ERROR(-20000,'Existen saldos iniciales para cuentas padres o hijas a la que se desea crear '||SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_CONTABLE'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_5
           );
    END;
  END BEFORE EACH ROW;
  
  AFTER STATEMENT IS
  BEGIN
    FOR i IN 1..POS LOOP
        BEGIN
          MI_CON:=0;
          --que no permita movimiento o auxiliares a cuentas padres
          IF (TABLA(I).MOVIMIENTO 
              + TABLA(I).MAN_CEN_CTO 
              + TABLA(I).MAN_AUX_TER 
              + TABLA(I).MAN_AUX_GEN 
              + TABLA(I).MAN_AUX_REF 
              + TABLA(I).MAN_AUX_FUE) NOT IN(0) 
          THEN
            SELECT COUNT(CODIGO) 
            INTO MI_CON
            FROM PLAN_CONTABLE 
            WHERE COMPANIA = TABLA(I).COMPANIA
              AND ANO      = TABLA(I).ANO
              AND CODIGO   <> TABLA(I).CODIGO
              AND CODIGO   BETWEEN TABLA(I).CODIGO AND TABLA(I).CODIGO || PCK_DATOS.CONS_MAX_ID 
              AND TABLA(i).CODIGO = SUBSTR(CODIGO,1,LENGTH(TABLA(i).CODIGO));
          END IF;
          IF MI_CON NOT IN (0) THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento y auxiliares teniendo cuentas a nivel inferior' || SQLERRM);
            --RETURN;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD => SQLCODE
                     ,UN_TABLAERROR => 'PLAN_CONTABLE'
                     ,UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_6
               );
        END;
        --
        BEGIN
          --que no permita ingresar hijas si hay padres con indicadores
          MI_CON:=0;
          SELECT COUNT(CODIGO) 
          INTO MI_CON
          FROM PLAN_CONTABLE
          WHERE COMPANIA = TABLA(I).COMPANIA
            AND ANO      = TABLA(I).ANO
            AND CODIGO   <> TABLA(I).CODIGO
            AND CODIGO   BETWEEN SUBSTR(TABLA(I).CODIGO,1,1) AND TABLA(I).CODIGO
            AND CODIGO   = SUBSTR(TABLA(I).CODIGO,1,LENGTH(CODIGO))
            AND (MOVIMIENTO 
                 + MAN_CEN_CTO 
                 + MAN_AUX_TER 
                 + MAN_AUX_GEN 
                 + MAN_AUX_REF 
                 + MAN_AUX_FUE) NOT IN(0);
          IF MI_CON NOT IN (0) THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento o auxiliares teniendo cuentas a nivel superior con indicadores' ||SQLERRM );
            --RETURN;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_TABLAERROR => 'PLAN_CONTABLE'
                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_7
               );
        END;
    END LOOP;
  END AFTER STATEMENT;

END CIU_PLAN_CONTABLE_IND;
