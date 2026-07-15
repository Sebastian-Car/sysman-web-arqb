CREATE OR REPLACE TRIGGER "BIU_CENTRO_COSTO_MOV"  
/*
      NAME              : BIU_CENTRO_COSTO_MOV
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 31/01/2017
      TIME              : 09:10 AM
      DESCRIPTION       : SE AJUSTA AL ESTANDAR                          
*/ 
BEFORE INSERT OR UPDATE OF   COMPANIA
                           , ANO
                           , CODIGO
                           , MOVIMIENTO 
                           , ACTIVOCENTRO
                        ON CENTRO_COSTO 
FOR EACH ROW 
DECLARE
  VAR           NUMBER :=0;
  MI_CON        NUMBER :=0;
  MI_CAMPOS     VARCHAR2(32000);
  MI_CONDICION  VARCHAR2(32000);
  GL_RTA        VARCHAR2(32000);
  MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
  PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
--controlar que no cambien el movimiento o inactiven el centro de costo varios.
  BEGIN
    IF :OLD.CODIGO=PCK_DATOS.CONS_CENTRO THEN 
      IF :NEW.MOVIMIENTO NOT IN(-1) OR :NEW.ACTIVOCENTRO NOT IN(-1) THEN
         :NEW.MOVIMIENTO:=-1;
         :NEW.ACTIVOCENTRO:=-1;
      END IF;   
    END IF;
  END;

  BEGIN
  --controlar que las cuentas padres no tengan movimientos (detalles) o saldos iniciales
    SELECT COUNT(COMPANIA)
      INTO MI_CON
      FROM DETALLE_COMPROBANTE_CNT
     WHERE COMPANIA = :NEW.COMPANIA
       AND ANO = :NEW.ANO
       AND CENTRO_COSTO NOT IN( :NEW.CODIGO)
       AND ((CENTRO_COSTO  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
           AND CENTRO_COSTO = SUBSTR(:NEW.CODIGO,1,LENGTH(CENTRO_COSTO))
       AND CENTRO_COSTO IN(:NEW.CODIGO)
       )
        --Cuando tenga referencias hijas y a demas la nueva cuenta tenga movimientos o auxiliares
        OR 
        (
        CENTRO_COSTO  BETWEEN :NEW.CODIGO AND :NEW.CODIGO || PCK_DATOS.CONS_CENTRO
        AND :NEW.CODIGO = SUBSTR(CENTRO_COSTO,1,LENGTH(:NEW.CODIGO))
        AND :NEW.MOVIMIENTO NOT IN(0)
    ));
  
  IF MI_CON NOT IN(0) THEN 
    MI_REEMPLAZOS(0).CLAVE := 'MI_CON';
    MI_REEMPLAZOS(0).VALOR := MI_CON;
    MI_REEMPLAZOS(1).CLAVE := 'CODIGO';
    MI_REEMPLAZOS(1).VALOR := :NEW.CODIGO;
    RAISE PCK_EXCEPCIONES.EXC_GENERAL;
    -- RAISE_APPLICATION_ERROR(-20000,MI_CON || '-' || :NEW.CODIGO || ' Existen movimientos para los centro de costo padres o hijos del que se desea crear '||SQLERRM );
  END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'CENTRO_COSTO',
                 UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_INSCTOCOSEXMOV,
                 UN_REEMPLAZOS => MI_REEMPLAZOS
               );
  END;
  MI_CON:=0;
   -- validar si tiene saldos iniciales
  BEGIN 
    SELECT COUNT(COMPANIA)
      INTO MI_CON
      FROM SALDOSINICIALES
     WHERE COMPANIA = :NEW.COMPANIA
       AND ANO = :NEW.ANO
       AND CENTRO_COSTO NOT IN( :NEW.CODIGO)
       AND ((CENTRO_COSTO  BETWEEN SUBSTR(:NEW.CODIGO, 1,1) AND :NEW.CODIGO
       AND CENTRO_COSTO = SUBSTR(:NEW.CODIGO,1,LENGTH(CENTRO_COSTO))
      ) OR 
      --Cuando tenga referencias hijas y a demas la nueva cuenta tenga movimientos o auxiliares
      (
      CENTRO_COSTO  BETWEEN :NEW.CODIGO AND :NEW.CODIGO || PCK_DATOS.CONS_CENTRO
      AND :NEW.CODIGO = SUBSTR(CENTRO_COSTO,1,LENGTH(:NEW.CODIGO))
      AND :NEW.MOVIMIENTO NOT IN(0)
      ))
      AND CONTABILIZADO NOT IN(0);
    IF MI_CON NOT IN(0) THEN
      ---RAISE_APPLICATION_ERROR(-20000,'Existen saldos iniciales para los centros de costo padres o hijos  del que se desea crear '||SQLERRM);
      RAISE PCK_EXCEPCIONES.EXC_GENERAL;
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'CENTRO_COSTO',
                 UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_INSCTOCOSEXSALDOINI
               );
  END;
  MI_CON:=0;
  BEGIN 
    --que no permita movimiento o auxiliares a cuentas padres
    IF :NEW.MOVIMIENTO NOT IN(0) THEN
      SELECT COUNT(CODIGO) 
      INTO MI_CON
      FROM CENTRO_COSTO  
      WHERE COMPANIA    =  :NEW.COMPANIA
        AND ANO         =  :NEW.ANO
        AND CODIGO      <> :NEW.CODIGO
        AND CODIGO      BETWEEN :NEW.CODIGO AND :NEW.CODIGO || PCK_DATOS.CONS_CENTRO
        AND :NEW.CODIGO = SUBSTR(CODIGO,1,LENGTH(:NEW.CODIGO));
    END IF;
  IF MI_CON NOT IN (0) THEN
    RAISE PCK_EXCEPCIONES.EXC_GENERAL;
    --RAISE_APPLICATION_ERROR(-20000, 'No se puede crear teniendo centros de costo a nivel inferior ' || SQLERRM);
  END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'CENTRO_COSTO',
                 UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_INSCTOCOSEXNIVELINF
               );
  END;
  --que no permita ingresar hijas si hay padres con indicadores
  MI_CON:=0;
  BEGIN 
     SELECT COUNT(CODIGO) 
       INTO MI_CON
       FROM CENTRO_COSTO  
      WHERE COMPANIA   =  :NEW.COMPANIA
        AND ANO        =  :NEW.ANO
        AND CODIGO     <> :NEW.CODIGO
        AND CODIGO     BETWEEN SUBSTR(:NEW.CODIGO,1,1) AND :NEW.CODIGO
        AND CODIGO     = SUBSTR(:NEW.CODIGO,1,LENGTH(CODIGO))
        AND MOVIMIENTO NOT IN(0);
    IF MI_CON NOT IN (0) THEN
      RAISE PCK_EXCEPCIONES.EXC_GENERAL;
      --RAISE_APPLICATION_ERROR(-20000, 'No se permite tener movimiento teniendo centros de costo a nivel superior con movimiento' ||SQLERRM );
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE,
                 UN_TABLAERROR => 'CENTRO_COSTO',
                 UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_INSCTOCOSMOVNVLSUP
               );
  END;  
END;