CREATE OR REPLACE TRIGGER "BIU_PLAN_PRESUPUESTAL_IND"   
/*
      NAME              : BIU_PLAN_PRESUPUESTAL_IND
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS 
      DATE MODIFIED     : 31/01/2017 
      TIME              : 10:50 AM 
      DESCRIPTION       : SE AJUSTA AL ESTANDAR.  
      MODIFIER          : GUSTAVO ANDRÉS FIGUEREDO AVILA
      DATE MODIFIED     : 22/06/2021 
      TIME              : 10:18 AM 
      DESCRIPTION       : Se agrega llamado al procedimiento PCK_PRESUPUESTO3.PR_ACTUALIZAR_ID_DETALLE_PPTAL. 	  
*/
FOR INSERT OR UPDATE OF   COMPANIA
                        , ANO
                        , CODIGO
                        , MOVIMIENTO
                        , MAN_CEN_CTO
                        , MAN_AUX_TER
                        , MAN_AUX_GEN
                        , MAN_AUX_REF
                        , MAN_AUX_FUE 
                     ON PLAN_PRESUPUESTAL 
COMPOUND TRIGGER 
  VAR                   NUMBER :=0;
  MI_CON                NUMBER:=0;
  MI_CAMPOS             VARCHAR2(32000);
  MI_CONDICION          VARCHAR2(32000);
  GL_RTA                VARCHAR2(32000); 
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
  TYPE REGISTRO IS RECORD
  (
    COMPANIA     PLAN_PRESUPUESTAL.COMPANIA%TYPE   ,
    ANO          PLAN_PRESUPUESTAL.ANO%TYPE        ,
    CODIGO       PLAN_PRESUPUESTAL.CODIGO%TYPE     ,
    MOVIMIENTO   PLAN_PRESUPUESTAL.MOVIMIENTO%TYPE ,
    MAN_CEN_CTO  PLAN_PRESUPUESTAL.MAN_CEN_CTO%TYPE,
    MAN_AUX_TER  PLAN_PRESUPUESTAL.MAN_AUX_TER%TYPE,
    MAN_AUX_GEN  PLAN_PRESUPUESTAL.MAN_AUX_GEN%TYPE,
    MAN_AUX_REF  PLAN_PRESUPUESTAL.MAN_AUX_REF%TYPE,
    MAN_AUX_FUE  PLAN_PRESUPUESTAL.MAN_AUX_FUE%TYPE,
    NATURALEZA   PLAN_PRESUPUESTAL.NATURALEZA%TYPE
  );
  TYPE REGISTROS IS TABLE OF REGISTRO INDEX BY BINARY_INTEGER;
  TABLA REGISTROS;
  POS                   NUMBER DEFAULT 0; 
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
    TABLA(POS).NATURALEZA   := :NEW.NATURALEZA;
    
    --que no permita movimiento y auxiliares
    BEGIN
      IF :NEW.MOVIMIENTO NOT IN(0) 
         AND (:NEW.MAN_CEN_CTO 
              + :NEW.MAN_AUX_TER 
              + :NEW.MAN_AUX_GEN 
              + :NEW.MAN_AUX_REF 
              + :NEW.MAN_AUX_FUE) NOT IN(0) THEN
              
        BEGIN       
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;  
        END;
        --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento y auxiliares'||SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      MI_MSGERROR(1).CLAVE := 'CUENTA';
      MI_MSGERROR(1).VALOR := :NEW.CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_PRESUPUESTO_SIN_MOV_AUX
                 ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL' 
                 ,UN_REEMPLAZOS => MI_MSGERROR
           ); 
    END;
    --que no permita movimiento o axuliares a cuentas con menos de 6 digitos
    --13/03/2018 Por petición de Sogamoso(EDI) se pide quitar la restricción de movimiento
    --           se decide dejar para que por lo menos el primer nivel no tenga movimiento 
    BEGIN
      IF LENGTH(:NEW.CODIGO) < 2 
         AND  (:NEW.MOVIMIENTO 
               + :NEW.MAN_CEN_CTO 
               + :NEW.MAN_AUX_TER 
               + :NEW.MAN_AUX_GEN 
               + :NEW.MAN_AUX_REF 
               + :NEW.MAN_AUX_FUE) NOT IN(0) THEN
        BEGIN       
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;  
        END;
        --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento o auxiliares a cuentas con menos de 6 digitos'||SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
      MI_MSGERROR(1).CLAVE := 'CUENTA';
      MI_MSGERROR(1).VALOR := :NEW.CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                 ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_SIN_MOV_AUX_DIG
                 ,UN_REEMPLAZOS => MI_MSGERROR
               ); 
    END; 
    
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
            FROM DETALLE_COMPROBANTE_PPTAL
           WHERE COMPANIA = :NEW.COMPANIA
             AND ANO      = :NEW.ANO
             AND CUENTA   IN (:NEW.CODIGO);
          --Existen movimientos para la cuenta, debe seleccionar un indicador ya sea de movimiento o auxiliares.
          IF MI_CON NOT IN(0)  THEN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
          END IF;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      MI_MSGERROR(1).CLAVE := 'CUENTA';
      MI_MSGERROR(1).VALOR := :NEW.CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_PRESUPUESTO_IND
                 ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                 ,UN_REEMPLAZOS => MI_MSGERROR
               );
    END;
    
    BEGIN 
      --controlar que las cuentas padres no tengan movimientos (detalles) o saldos iniciales
      SELECT COUNT(COMPANIA)
        INTO MI_CON
        FROM DETALLE_COMPROBANTE_PPTAL
       WHERE COMPANIA = :NEW.COMPANIA
         AND ANO = :NEW.ANO
         AND CUENTA NOT IN( :NEW.CODIGO)
         AND ((CUENTA  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
               AND CUENTA = SUBSTR(:NEW.CODIGO,1,LENGTH(CUENTA))
               )
               --Cuando tenga cuentas hijas y a demas la nueva cuenta tenga movimientos o auxiliares
               OR 
              (CUENTA  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO || PCK_DATOS.CONS_MAX_ID
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
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        --RAISE_APPLICATION_ERROR(-20000,MI_CON || '-' || :NEW.CODIGO || ' Existen movimientos para cuentas padres o hijas a la que se desea crear '||SQLERRM );
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      MI_MSGERROR(1).CLAVE := 'CUENTA';
      MI_MSGERROR(1).VALOR := :NEW.CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_PRESUPUESTO_MOV_PADRE 
                 ,UN_REEMPLAZOS => MI_MSGERROR
                 );
    END;
    MI_CON:=0;
    -- validar si tiene saldos iniciales 
    BEGIN  
      BEGIN
        SELECT COUNT(COMPANIA)
          INTO MI_CON
          FROM APROPIACIONESINICIALES
         WHERE COMPANIA  = :NEW.COMPANIA
           AND ANO       = :NEW.ANO
           AND CODIGO    NOT IN( :NEW.CODIGO)
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
            ))
            AND CONTABILIZADO NOT IN (0);
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CON := 0;
      END;  
      IF MI_CON NOT IN(0) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        --RAISE_APPLICATION_ERROR(-20000,'Existen apropiaciones iniciales para cuentas padres o hijas a la que se desea crear '||SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
      MI_MSGERROR(1).CLAVE := 'CUENTA'; 
      MI_MSGERROR(1).VALOR := :NEW.CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_PRESUPUESTO_APR_INI 
                 ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                 ,UN_REEMPLAZOS => MI_MSGERROR
                 );
    END;
  END BEFORE EACH ROW;
       
  AFTER STATEMENT IS
  BEGIN
   FOR i IN 1..POS LOOP 
    MI_CON:=0;
    BEGIN 
      --que no permita movimiento o auxiliares a cuentas padres
      IF (  TABLA(I).MOVIMIENTO
          + TABLA(I).MAN_CEN_CTO 
          + TABLA(I).MAN_AUX_TER 
          + TABLA(I).MAN_AUX_GEN 
          + TABLA(I).MAN_AUX_REF 
          + TABLA(I).MAN_AUX_FUE) NOT IN(0) THEN
        BEGIN 
          SELECT COUNT(CODIGO) 
            INTO MI_CON
            FROM PLAN_PRESUPUESTAL 
           WHERE COMPANIA  =  TABLA(I).COMPANIA
             AND ANO       =  TABLA(I).ANO
             AND CODIGO    <> TABLA(I).CODIGO
             AND CODIGO    BETWEEN TABLA(I).CODIGO AND TABLA(I).CODIGO || PCK_DATOS.CONS_MAX_ID 
             AND TABLA(I).CODIGO = SUBSTR(CODIGO,1,LENGTH(TABLA(I).CODIGO));
        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_CON := 0;
        END;
      END IF;
      IF MI_CON NOT IN (0) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento y auxiliares teniendo cuentas a nivel inferior' || SQLERRM);
        --RETURN;
      END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
       MI_MSGERROR(1).CLAVE := 'CUENTA'; 
       MI_MSGERROR(1).VALOR := TABLA(I).CODIGO;
       PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE
                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_PRESUPUESTO_MOV_HIJ 
                  ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                  ,UN_REEMPLAZOS => MI_MSGERROR
                  );
    END;
    BEGIN 
    --que no permita ingresar hijas si hay padres con indicadores
      MI_CON:=0;
      BEGIN 
        SELECT COUNT(CODIGO) 
          INTO MI_CON
          FROM PLAN_PRESUPUESTAL
        WHERE COMPANIA =  TABLA(I).COMPANIA
          AND ANO      =  TABLA(I).ANO
          AND CODIGO   <> TABLA(I).CODIGO
          AND CODIGO   BETWEEN SUBSTR(TABLA(I).CODIGO,1,1) AND TABLA(I).CODIGO
          AND CODIGO   = SUBSTR(TABLA(I).CODIGO,1,LENGTH(CODIGO))
          AND (MOVIMIENTO 
               + MAN_CEN_CTO 
               + MAN_AUX_TER 
               + MAN_AUX_GEN 
               + MAN_AUX_REF 
               + MAN_AUX_FUE) NOT IN(0);
      EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CON := 0;
      END;
      IF MI_CON NOT IN (0) THEN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
      END IF;
      IF (  TABLA(I).MOVIMIENTO
          + TABLA(I).MAN_CEN_CTO 
          + TABLA(I).MAN_AUX_TER 
          + TABLA(I).MAN_AUX_GEN 
          + TABLA(I).MAN_AUX_REF 
          + TABLA(I).MAN_AUX_FUE) NOT IN(0) 
         AND TABLA(I).NATURALEZA IS NOT NULL THEN 
        PCK_PRESUPUESTO1.PR_CREAR_SALDOAUXPRESUPUESTAL(
                         UN_COMPANIA      => TABLA(I).COMPANIA
                        ,UN_ANIO          => TABLA(I).ANO
                        ,UN_CODIGO        => TABLA(I).CODIGO
                        ,UN_CENTRO        => PCK_DATOS.FC_CONS_CENTRO
                        ,UN_TERCERO       => PCK_DATOS.FC_CONS_TERCERO
                        ,UN_SUCURSAL      => PCK_DATOS.FC_CONS_SUCURSAL
                        ,UN_AUXILIAR      => PCK_DATOS.FC_CONS_AUXILIAR
                        ,UN_REFERENCIA    => PCK_DATOS.FC_CONS_REFERENCIA
                        ,UN_FUENTERECURSO => PCK_DATOS.FC_CONS_FUENTE
                        ,UN_NATURALEZA    => TABLA(I).NATURALEZA);
      END IF; 
      
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
      MI_MSGERROR(1).CLAVE := 'CUENTA'; 
      MI_MSGERROR(1).VALOR := TABLA(I).CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                 ,UN_ERROR_COD  => PCK_ERRORES.ERR_PRESUPUESTO_MOV_PAD 
                 ,UN_REEMPLAZOS => MI_MSGERROR
                 );
    END;
   PCK_PRESUPUESTO1.PR_CREAR_SALDOPLANPPTAL(
                    UN_COMPANIA => TABLA(I).COMPANIA
                   ,UN_ANIO     => TABLA(I).ANO
                   ,UN_CODIGO   => TABLA(I).CODIGO); 
                  
   PCK_PRESUPUESTO3.PR_ACTUALIZAR_ID_DETALLE_PPTAL(
   					UN_COMPANIA => TABLA(I).COMPANIA,
			        UN_ANO      => TABLA(I).ANO, 
			        UN_CUENTA	=> TABLA(I).CODIGO
  					);
                  
   END LOOP;
  END AFTER STATEMENT;
  
END "BIU_PLAN_PRESUPUESTAL_IND";




