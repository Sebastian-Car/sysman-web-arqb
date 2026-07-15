create or replace PACKAGE BODY PCK_CONTABILIDAD_CPTE AS

--1 
FUNCTION FC_VALIDAR_EQUIV_PPTALES
  /*
  NAME              : validarEquivPptales
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 19/04/2017
  TIME              : 05:52PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: validarEquivPptales
  @METHOD:  GET
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_TEMP NUMBER;
  MI_RTA1 NUMBER;
  MI_RTA2 NUMBER;
BEGIN
-- verifica si se requiereSeleccionarRubros dss 39032
  BEGIN
    BEGIN
      MI_RTA1 := 0;
      FOR RS IN (SELECT 
                    COUNT(DETALLE_COMPROBANTE_CNT.CUENTA) NUMERO_EQUIVALENCIAS
                  FROM DETALLE_COMPROBANTE_CNT
                    INNER JOIN PLAN_PPTAL_CUENTACNT
                    ON (DETALLE_COMPROBANTE_CNT.COMPANIA        = PLAN_PPTAL_CUENTACNT.COMPANIA)
                    AND (DETALLE_COMPROBANTE_CNT.ANO            = PLAN_PPTAL_CUENTACNT.ANO)
                    AND (DETALLE_COMPROBANTE_CNT.CUENTA         = PLAN_PPTAL_CUENTACNT.CUENTA_CONTABLE)
                  WHERE DETALLE_COMPROBANTE_CNT.COMPANIA        = UN_COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ANO             = UN_ANIO
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE       = UN_TIPOMOVIMIENTO
                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE     = UN_NUMERO
                    AND (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO  <> 0
                    OR DETALLE_COMPROBANTE_CNT.VALOR_DEBITO     <> 0)
                  GROUP BY DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                    DETALLE_COMPROBANTE_CNT.CUENTA
                  ORDER BY DETALLE_COMPROBANTE_CNT.CONSECUTIVO) 
      LOOP
        IF RS.NUMERO_EQUIVALENCIAS > 1 THEN
          MI_RTA1 := -1;
        END IF;
      END LOOP;
    END;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                 UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTABILIDAD_COMPNIIFNULA
                                 );
  END;
-- verifica si tieneRubrosAsociados dss 39033
  BEGIN
    IF MI_RTA1 = -1 THEN
      BEGIN
        BEGIN
          MI_RTA2 := 0;
          SELECT COUNT(*)
          INTO MI_TEMP
          FROM DETALLE_COMPROBANTE_CNT
            INNER JOIN PLAN_PPTAL_CUENTACNT
            ON (DETALLE_COMPROBANTE_CNT.COMPANIA        = PLAN_PPTAL_CUENTACNT.COMPANIA)
            AND (DETALLE_COMPROBANTE_CNT.ANO            = PLAN_PPTAL_CUENTACNT.ANO)
            AND (DETALLE_COMPROBANTE_CNT.CUENTA         = PLAN_PPTAL_CUENTACNT.CUENTA_CONTABLE)
          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA        = UN_COMPANIA
            AND DETALLE_COMPROBANTE_CNT.ANO             = UN_ANIO
            AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE       = UN_TIPOMOVIMIENTO
            AND DETALLE_COMPROBANTE_CNT.COMPROBANTE     = UN_NUMERO
            AND (DETALLE_COMPROBANTE_CNT.VALOR_CREDITO  <> 0
            OR DETALLE_COMPROBANTE_CNT.VALOR_DEBITO     <> 0)
            AND DETALLE_COMPROBANTE_CNT.CUENTAPPTAL     IS NULL
          GROUP BY DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
            DETALLE_COMPROBANTE_CNT.CUENTA
          HAVING COUNT(DETALLE_COMPROBANTE_CNT.CUENTA) > 1;
        END;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_RTA2 := -1;
      END;
      BEGIN
        IF MI_RTA2 = 0 THEN
          RETURN -1;
        ELSE
          RETURN 0;
        END IF;
      END;
    ELSE
      RETURN 0;
    END IF;
  END;
END FC_VALIDAR_EQUIV_PPTALES;




-- 2
FUNCTION FC_PREPARAR_NIIF
    /*
    NAME              : PREPARAR NIIF
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
    DATE MIGRADOR     : 17/04/2017
    TIME              : 12:52PM
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    MODIFICATIONS     :
    @NAME: prepararNiif
    @METHOD:  GET
    */
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
      UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO,
      UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
      UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG,
      UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_RTA          PCK_SUBTIPOS.TI_STRSQL;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_COMPANIANIIF PCK_SUBTIPOS.TI_COMPANIA;
BEGIN
    BEGIN
      MI_COMPANIANIIF := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                                              'COMPAÑIA EQUIVALENTE NIIF', 
                                              UN_MODULO, 
                                              SYSDATE);
      IF MI_COMPANIANIIF IS NULL THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;

      SELECT NUMERO 
      INTO MI_RTA
      FROM ANO
      WHERE COMPANIA = MI_COMPANIANIIF
      AND NUMERO     = UN_ANIO;

      IF MI_RTA IS NULL THEN
        MI_CAMPOS  := 'COMPANIA, 
                      NUMERO, 
                      CREATED_BY, 
                      DATE_CREATED';
        MI_VALORES := ' '''|| MI_COMPANIANIIF ||''' , 
                        '|| UN_ANIO ||' , 
                        '''|| UN_USUARIO ||''' 
                        , SYSDATE 
                        ';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                               UN_VALORES => MI_VALORES, 
                                               UN_TABLA   => 'ANO', 
                                               UN_ACCION  => 'I'); 
      END IF;
      PCK_PREPARAR_ANO.PR_COPIAR_TERCERO(UN_COMPANIA, MI_COMPANIANIIF);
      PCK_PREPARAR_ANO.PR_COPIAR_CENTRO_COSTO(UN_COMPANIA, UN_ANIO, UN_ANIO, MI_COMPANIANIIF);
      PCK_PREPARAR_ANO.PR_COPIAR_AUXILIAR(UN_COMPANIA, UN_ANIO, UN_ANIO, MI_COMPANIANIIF);
      PCK_PREPARAR_ANO.PR_COPIAR_FUENTE_RECURSO(UN_COMPANIA, UN_ANIO, UN_ANIO, MI_COMPANIANIIF);
      PCK_PREPARAR_ANO.PR_COPIAR_REFERENCIA(UN_COMPANIA, UN_ANIO, UN_ANIO, MI_COMPANIANIIF);
      -- Verifica si el comprobante ya Existe.
      MI_RTA := NULL;
      BEGIN
        BEGIN
          SELECT DISTINCT 'X'
          INTO MI_RTA
          FROM COMPROBANTE_CNT
          WHERE COMPANIA = ''||MI_COMPANIANIIF||''
          AND ANO        = UN_ANIO
          AND TIPO       = ''||UN_TIPOMOVIMIENTO||''
          AND NUMERO     = UN_NUMERO;
          IF MI_RTA IS NOT NULL THEN
            RETURN -1;
          ELSE
            RETURN 0;
          END IF;
        END;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          RETURN 0;
      END;  
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    =>  SQLCODE,
                                 UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTABILIDAD_COMPNIIFNULA
                                 );  

END FC_PREPARAR_NIIF;

-- 3
FUNCTION FC_CONTABILIZAR_NIIF
    /*
    NAME              : CONTABILIZAR NIIF
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
    DATE MIGRADOR     : 18/04/2017
    TIME              : 12:34PM
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    MODIFICATIONS     :
    @NAME: contabilizarNiif
    @METHOD:  GET
    */
    (
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_MODULO             IN PCK_SUBTIPOS.TI_MODULO,
      UN_FECHACREACION      IN DATE,
      UN_FECHA              IN DATE,
      UN_FECHAVNCDOC        IN DATE,
      UN_FECHAPAGADODOGN    IN DATE,
      UN_ANIO               IN PCK_SUBTIPOS.TI_ANIO,
      UN_TIPOMOVIMIENTO     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
      UN_NUMERO             IN PCK_SUBTIPOS.TI_LONG,
      UN_DESCRIPCION        IN COMPROBANTE_CNT.DESCRIPCION%TYPE,
      UN_TEXTO              IN COMPROBANTE_CNT.TEXTO%TYPE,
      UN_TERCERO            IN PCK_SUBTIPOS.TI_TERCERO,
      UN_SUCURSAL           IN PCK_SUBTIPOS.TI_SUCURSAL,
      UN_VLRDOCUMENTO       IN PCK_SUBTIPOS.TI_DOBLE,
      UN_CODUSUARIO         IN PCK_SUBTIPOS.TI_USUARIO,
      UN_VLRBASE            IN PCK_SUBTIPOS.TI_DOBLE,
      UN_VLRBASEIVA         IN PCK_SUBTIPOS.TI_DOBLE,
      UN_DEBITO             IN PCK_SUBTIPOS.TI_DOBLE,
      UN_CREDITO            IN PCK_SUBTIPOS.TI_DOBLE,
      UN_VLRAGIRAR          IN PCK_SUBTIPOS.TI_DOBLE,
      UN_DEBITOSAFECTADOS   IN PCK_SUBTIPOS.TI_DOBLE,
      UN_CREDITOSAFECTADOS  IN PCK_SUBTIPOS.TI_DOBLE,
      UN_PORCIVA            IN PCK_SUBTIPOS.TI_PORCENTAJE,
      UN_CENTRO_COSTO       IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
      UN_AUXILIAR           IN PCK_SUBTIPOS.TI_AUXILIAR,
      UN_FUENTE_RECURSO     IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
      UN_REFERENCIA         IN PCK_SUBTIPOS.TI_REFERENCIA
    )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_COMPANIANIIF PCK_SUBTIPOS.TI_COMPANIA;
    MI_COUNT        PCK_SUBTIPOS.TI_ENTERO;
BEGIN
    BEGIN
      MI_COMPANIANIIF := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                                               'COMPAÑIA EQUIVALENTE NIIF', 
                                               UN_MODULO, 
                                               SYSDATE);
      IF MI_COMPANIANIIF IS NULL THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;
      -- Crea el comprobante en NIIF.
      -- GDZ Filtro Fecha
      MI_CAMPOS  := 'COMPANIA,ANO,TIPO,NUMERO,FECHA,
                     DESCRIPCION,TEXTO,TERCERO,SUCURSAL,
                     VLR_DOCUMENTO,CREATED_BY,VLR_BASE, 
                     VLR_BASEIVA, FECHA_VCN_DOC, DEBITO, 
                     CREDITO, VLRAGIRAR,
                     DEBITOSAFECTADOS, CREDITOSAFECTADOS, 
                     PORCIVA, CENTRO_COSTO, AUXILIAR, HORA,
                     ';

      MI_VALORES := ' '''|| MI_COMPANIANIIF ||''' , '|| UN_ANIO ||' ,
                       '''|| UN_TIPOMOVIMIENTO ||''' , '''|| UN_NUMERO ||''' ,
                       TO_DATE('''|| TO_CHAR(UN_FECHA, 'DD/MM/YYYY HH24:mi:ss') ||''',
                       '''|| NVL(UN_DESCRIPCION, '') ||''' , '''|| NVL(UN_TEXTO, '') ||''' ,
                       '''|| UN_TERCERO ||''' , '''|| UN_SUCURSAL ||''' ,
                       '|| UN_VLRDOCUMENTO  ||' , '''|| UN_CODUSUARIO ||''' ,
                       '|| UN_VLRBASE  ||' , '|| UN_VLRBASEIVA  ||' , 
                       TO_DATE('''|| TO_CHAR(UN_FECHAVNCDOC, 'DD/MM/YYYY HH24:mi:ss') ||''' ,
                       '|| UN_DEBITO ||' , '|| UN_CREDITO ||' ,
                       '|| UN_VLRAGIRAR ||' , '|| UN_DEBITOSAFECTADOS ||' , '|| UN_CREDITOSAFECTADOS ||' ,
                       '|| UN_PORCIVA ||' , '''|| UN_CENTRO_COSTO ||''' ,
                       '''|| UN_AUXILIAR ||''' , '''|| SYSDATE ||''' ,
                       ';

      IF UN_FECHACREACION IS NULL THEN
        MI_CAMPOS  := MI_CAMPOS || 
                      ' FUENTE_RECURSO, 
                      REFERENCIA ';            
        MI_VALORES := MI_VALORES || ' 
                      '''|| UN_FUENTE_RECURSO ||''' , 
                      ''' || UN_REFERENCIA ||'''
                       ';
      ELSE
        MI_CAMPOS  := MI_CAMPOS  || 
                      ' FECHAPAGADOGN, 
                      DATE_CREATED ';
        MI_VALORES := MI_VALORES || 
                      ' TO_DATE('''|| TO_CHAR(UN_FECHAPAGADODOGN, 'DD/MM/YYYY HH24:mi:ss') ||
                      ''', ''DD/MM/YYYY HH24:mi:ss'') ||'' , 
                      ''' || SYSDATE || '''
                      ';
      END IF;

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES, 
                                                 UN_TABLA   => 'COMPROBANTE_CNT', 
                                                 UN_ACCION  => 'I'); 
        END;
        EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>  SQLCODE,
                                     UN_ERROR_COD =>  PCK_ERRORES.ERR_CONTABILIDAD_INS_COMPCNT
                                     );  
      END;

      FOR RS IN ( SELECT DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                    DETALLE_COMPROBANTE_CNT.COMPROBANTE ,
                    DETALLE_COMPROBANTE_CNT.CONSECUTIVO ,
                    DETALLE_COMPROBANTE_CNT.NATURALEZA,
                    DETALLE_COMPROBANTE_CNT.CUENTAPPTAL ,
                    DETALLE_COMPROBANTE_CNT.DESCRIPCION,
                    DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
                    DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                    DETALLE_COMPROBANTE_CNT.EJECUCION_DEBITO,
                    DETALLE_COMPROBANTE_CNT.EJECUCION_CREDITO,
                    DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,
                    DETALLE_COMPROBANTE_CNT.TIPO_DOCUMENTO,
                    DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                    DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                    DETALLE_COMPROBANTE_CNT.TERCERO,
                    DETALLE_COMPROBANTE_CNT.SUCURSAL,
                    DETALLE_COMPROBANTE_CNT.AUXILIAR,
                    DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT,
                    DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO,
                    DETALLE_COMPROBANTE_CNT.CHEQUEPARAANULAR,
                    DETALLE_COMPROBANTE_CNT.CONCEPTO_EX,
                    DETALLE_COMPROBANTE_CNT.TIPOPPTAL,
                    DETALLE_COMPROBANTE_CNT.BASE_IVA,
                    DETALLE_COMPROBANTE_CNT.DESEMBOLSO,
                    DETALLE_COMPROBANTE_CNT.SALDOCUENTA,
                    DETALLE_COMPROBANTE_CNT.DATE_CREATED,
                    DETALLE_COMPROBANTE_CNT.CREATED_BY,
                    DETALLE_COMPROBANTE_CNT.MODIFIED_BY,
                    DETALLE_COMPROBANTE_CNT.PORCENTAJERETENCION,
                    DETALLE_COMPROBANTE_CNT.REVELACIONES,
                    CASE
                      WHEN CODIGO_NIIF IS NULL
                      THEN CUENTA
                      ELSE CODIGO_NIIF
                    END COD_NIIF 
                  FROM DETALLE_COMPROBANTE_CNT
                    LEFT JOIN V_PLAN_CONTABLE
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA     = V_PLAN_CONTABLE.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.ID          = V_PLAN_CONTABLE.ID
                    AND DETALLE_COMPROBANTE_CNT.ANO         = V_PLAN_CONTABLE.ANO
                  WHERE DETALLE_COMPROBANTE_CNT.Compania  = ''||UN_COMPANIA||''
                    AND DETALLE_COMPROBANTE_CNT.Ano         = UN_ANIO
                    AND DETALLE_COMPROBANTE_CNT.Tipo_cpte   = ''||UN_TIPOMOVIMIENTO||''
                    AND DETALLE_COMPROBANTE_CNT.Comprobante = UN_NUMERO
                  ORDER BY DETALLE_COMPROBANTE_CNT.CONSECUTIVO ) 
      LOOP
        MI_COUNT := 0;  
        SELECT COUNT(*) X
        INTO MI_COUNT
        FROM PLAN_CONTABLE
        WHERE COMPANIA = ''||MI_COMPANIANIIF||''
        AND CODIGO     = RS.COD_NIIF
        AND ANO        = UN_ANIO ;

        IF MI_COUNT = 0 THEN
          MI_COUNT := PCK_CONTABILIDAD3.FC_CREARCUENTANIIF(MI_COMPANIANIIF, 
                                                           UN_COMPANIA, 
                                                           UN_ANIO,
                                                           UN_ANIO,
                                                           RS.COD_NIIF,
                                                           UN_CODUSUARIO);
        END IF;

        MI_CAMPOS  := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,NATURALEZA,CUENTAPPTAL,DESCRIPCION,
                       VALOR_DEBITO,VALOR_CREDITO,EJECUCION_DEBITO,EJECUCION_CREDITO,BASE_GRAVABLE,TIPO_DOCUMENTO,
                       NRO_DOCUMENTO,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,TIPO_CPTE_AFECT,CMPTE_AFECTADO,CHEQUEPARAANULAR,
                       CONCEPTO_EX,TIPOPPTAL,BASE_IVA,DESEMBOLSO,SALDOCUENTA,DATE_CREATED,CREATED_BY,
                       PORCENTAJERETENCION,HORA,REVELACIONES
                     ';

        MI_VALORES := ' '''|| MI_COMPANIANIIF ||''' , '|| UN_ANIO ||' , '''|| RS.TIPO_CPTE ||''' , '|| RS.COMPROBANTE ||' ,
                       '|| RS.CONSECUTIVO ||' , '''|| RS.COD_NIIF ||''' , TO_DATE('''|| TO_CHAR(UN_FECHA, 'DD/MM/YYYY HH24:mi:ss') ||''', ''DD/MM/YYYY HH24:mi:ss'') ||'' ,
                       '''|| RS.NATURALEZA ||''' , '''|| RS.CUENTAPPTAL ||''' , '''|| RS.DESCRIPCION ||''' ,
                       '|| NVL(RS.VALOR_DEBITO, 0) ||' , '|| NVL(RS.VALOR_CREDITO, 0) ||' , '|| NVL(RS.EJECUCION_DEBITO, 0) ||' , 
                       '|| NVL(RS.EJECUCION_CREDITO, 0) ||' , '|| NVL(RS.BASE_GRAVABLE, 0) ||' , '''|| NVL(RS.TIPO_DOCUMENTO, '') ||''',
                       '''|| RS.NRO_DOCUMENTO ||''' , '''|| RS.CENTRO_COSTO ||''' , '''|| NVL(RS.TERCERO, '99999999999') ||''', 
                       '''|| NVL(RS.SUCURSAL, '999') ||''' , '''|| RS.AUXILIAR ||''' , '''|| NVL(RS.TIPO_CPTE_AFECT, '') ||''' ,
                       '|| NVL(RS.CMPTE_AFECTADO, 0) ||' , '''|| RS.CHEQUEPARAANULAR ||''' , '''|| RS.CONCEPTO_EX ||''' , 
                       '''|| NVL(RS.TIPOPPTAL, '') ||''' , '|| NVL(RS.BASE_IVA, 0) ||' , '|| NVL(RS.DESEMBOLSO, 0) ||' ,
                       '|| RS.SALDOCUENTA ||' , SYSDATE,
                       '''|| UN_CODUSUARIO ||''' , '|| RS.PORCENTAJERETENCION ||', SYSDATE ,
                       '''|| RS.REVELACIONES ||''' 
                       ';

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                                   UN_VALORES => MI_VALORES, 
                                                   UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                                   UN_ACCION  => 'I'); 
            IF PCK_DATOS.GL_RTA = 0 THEN
              RETURN 0;
            END IF;
          END;
        END;

      END LOOP;

      MI_CAMPOS  := 'COMPANIA, ANO, TIPO, NUMERO, TIPORETENCION, CODIGORETENCION, VALOR, VALORBASE, MODIFIED_BY, DATE_MODIFIED';

      MI_VALORES := 'SELECT '''|| MI_COMPANIANIIF ||''' AS COMPANIA,
                            COMPROBANTE_CNTRETENCION.ANO,
                            '''|| UN_TIPOMOVIMIENTO ||''',
                            '''|| UN_NUMERO || ''',
                            COMPROBANTE_CNTRETENCION.TIPORETENCION,
                            COMPROBANTE_CNTRETENCION.CODIGORETENCION,
                            COMPROBANTE_CNTRETENCION.VALOR,
                            COMPROBANTE_CNTRETENCION.VALORBASE,
                            '''|| UN_CODUSUARIO ||''',
                            SYSDATE
                    FROM COMPROBANTE_CNTRETENCION
                    WHERE COMPROBANTE_CNTRETENCION.COMPANIA   = '''|| UN_COMPANIA || '''
                      AND COMPROBANTE_CNTRETENCION.ANO        = '||UN_ANIO||'
                      AND COMPROBANTE_CNTRETENCION.TIPO       = '''|| UN_TIPOMOVIMIENTO ||'''
                      AND COMPROBANTE_CNTRETENCION.NUMERO     = '||UN_NUMERO||'
                      ';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES, 
                                                 UN_TABLA   => 'COMPROBANTE_CNTRETENCION', 
                                                 UN_ACCION  => 'IS'); 
        END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD    =>  SQLCODE,
                                  UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTABILIDAD_INSERDETALLES
                                  );          
      END;
      RETURN -1;
    END;

    EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    =>  SQLCODE,
                                UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTABILIDAD_COMPNIIFNULA
                                );  
END FC_CONTABILIZAR_NIIF;

-- 4
-- validarPacEgreso
FUNCTION FC_VALIDAR_PAC_EGRESO   
/*
  NAME              : validarPacEgreso
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 19/04/2017
  TIME              : 10:16 AM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: validarPacEgreso
  @METHOD:  GET
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG,
    UN_CLASE          IN TIPO_COMPROBANTE.CLASE_CONTABLE%TYPE,
    UN_VLRDOCUMENTO   IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLRAGIRAR      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_INDIMPRESION   IN PCK_SUBTIPOS.TI_LOGICO)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RTA                PCK_SUBTIPOS.TI_LOGICO := 0;
  MI_OBLIGAPACEGRESO    PCK_SUBTIPOS.TI_PARAMETRO;
  MI_SALDO              PCK_SUBTIPOS.TI_DOBLE := 0;
BEGIN
  MI_OBLIGAPACEGRESO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                                              'OBLIGA CONTROLAR PAC EN EGRESO', 
                                              UN_MODULO, 
                                              SYSDATE);
  IF MI_OBLIGAPACEGRESO <> 'SI' 
      OR UN_CLASE <> 'E' 
      OR UN_TIPOMOVIMIENTO = 'TRA' THEN
    RETURN -1;
  END IF;
  -- VERIFICA SI HAY DETALLES EN DETALLE COMPROBANTE PPTAL.
  BEGIN
    BEGIN
      SELECT SUM(VALOR_DEBITO-VALOR_CREDITO) AS SALDO
      INTO MI_SALDO
      FROM DETALLE_COMPROBANTE_PPTAL
      WHERE COMPANIA    = UN_COMPANIA
        AND ANO         = UN_ANIO
        AND TIPO_CPTE   = UN_TIPOMOVIMIENTO
        AND COMPROBANTE = UN_NUMERO;

      CASE WHEN MI_SALDO <= 0 THEN
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    =>  SQLCODE,
                            UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_PAC_NOIMPPPTAL
                            );
        END;
      WHEN UN_INDIMPRESION = -1 THEN
          CASE WHEN UN_VLRDOCUMENTO > MI_SALDO THEN
            BEGIN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    =>  SQLCODE,
                                UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_PAC_DOCMAYORPPTAL
                                );
            END;
          WHEN UN_VLRAGIRAR > MI_SALDO THEN
            BEGIN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    =>  SQLCODE,
                                UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_PAC_GIROMAYORPPTAL
                                );
            END;
          WHEN UN_VLRAGIRAR > UN_VLRDOCUMENTO THEN
            BEGIN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    =>  SQLCODE,
                                UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_PAC_GIROMAYORDOC
                                );
            END;
          END CASE;
      ELSE
        MI_RTA := -1;
      END CASE;  

      RETURN MI_RTA;
    END;

    EXCEPTION WHEN NO_DATA_FOUND THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    =>  SQLCODE,
                      UN_ERROR_COD  =>  PCK_ERRORES.ERR_CONTAB_PAC_NOIMPPPTAL
                      );
  END;
END FC_VALIDAR_PAC_EGRESO;

-- 5
FUNCTION FC_ACT_NRODOC_DETALLE_CNT
  /*
  NAME              : actualizarNroDocDetalleCnt
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 24/04/2017
  TIME              : 05:10 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: actualizarNroDocDetalleCnt
  @METHOD:  GET
  */
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO            IN PCK_SUBTIPOS.TI_MODULO,
    UN_NRODOCUMENTO      IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE,
    UN_ANIO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO            IN PCK_SUBTIPOS.TI_LONG,
    UN_DEBITOSAFECTADOS  IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CREDITOSAFECTADOS IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_OBLIGAPACEGRESO  PCK_SUBTIPOS.TI_PARAMETRO;
  MI_MOD_GIRO         PCK_SUBTIPOS.TI_PARAMETRO;    
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  MI_OBLIGAPACEGRESO   := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
                                                'NRO DOCUMENTO DETALLE DIFERENTE A NRO DOCUMENTO DEL HEADER', 
                                                UN_MODULO, 
                                                SYSDATE);
  IF MI_OBLIGAPACEGRESO = 'NO' THEN
    BEGIN
      BEGIN
        MI_CAMPOS     := 'NRO_DOCUMENTO = ' ||  CASE WHEN UN_NRODOCUMENTO IS NULL 
                            THEN 'NULL' 
                            ELSE '''' || UN_NRODOCUMENTO || '''' 
                            END ||' , 
                          MODIFIED_BY     = '''|| UN_USUARIO ||''' , 
                          DATE_MODIFIED   = SYSDATE ';
        MI_CONDICION  := 'DETALLE_COMPROBANTE_CNT.COMPANIA            = '''||UN_COMPANIA||'''
                          AND DETALLE_COMPROBANTE_CNT.ANO             = '||UN_ANIO||'
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE       = '''||UN_TIPOMOVIMIENTO||'''
                          AND DETALLE_COMPROBANTE_CNT.COMPROBANTE     = '||UN_NUMERO||' ';

        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     =>  'DETALLE_COMPROBANTE_CNT',
                                             UN_ACCION    =>  'M',
                                             UN_CAMPOS    =>  MI_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION
                                             ); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        MI_MSGERROR (1).CLAVE := 'COMPANIA';
        MI_MSGERROR (1).VALOR := UN_COMPANIA;
        MI_MSGERROR (2).CLAVE := 'ANIO';
        MI_MSGERROR (2).VALOR := UN_ANIO;
        MI_MSGERROR (2).CLAVE := 'TIPO_MOVIMIENTO';
        MI_MSGERROR (2).VALOR := UN_TIPOMOVIMIENTO;
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_ACTNRO_DETCNT,
                                    UN_REEMPLAZOS => MI_MSGERROR);
    END;
    IF NVL(UN_DEBITOSAFECTADOS, 0) <> 0 
      OR NVL(UN_CREDITOSAFECTADOS, 0) <> 0 THEN
      MI_MOD_GIRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 
                                           'NO PERMITE MODIFICAR COM DESPUES DE GIRO', 
                                           UN_MODULO, 
                                           SYSDATE);
      IF MI_MOD_GIRO = 'SI' THEN
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          --De acuerdo a su configuración no se permite modificar el COM después de haber un giro
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_NO_EDIT_COM
                                      );
        END;
      ELSE
        RETURN -1;
      END IF;
    END IF;
  END IF;
  RETURN 0;
END FC_ACT_NRODOC_DETALLE_CNT;

-- 6
FUNCTION FC_BORRAR_DETCOMP_NIIF
  /*
  NAME              : borrarDetComproNiif
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 25/04/2017
  TIME              : 02:30 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: borrarDetComproNiif
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG )
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RTA          PCK_SUBTIPOS.TI_LOGICO := 0;
  MI_COMPANIANIIF PCK_SUBTIPOS.TI_PARAMETRO;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  BEGIN
    BEGIN
      MI_COMPANIANIIF := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                                               'COMPAÑIA EQUIVALENTE NIIF', 
                                               UN_MODULO, 
                                               SYSDATE);
      IF MI_COMPANIANIIF IS NULL THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END IF;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE, 
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_COMPNIIFNULA);
    END;
  END;
  SELECT COUNT(*)
  INTO MI_RTA
  FROM DETALLE_COMPROBANTE_CNT
  WHERE COMPANIA    = ''||UN_COMPANIA||''
    AND ANO         = UN_ANIO
    AND TIPO_CPTE   = UN_TIPOMOVIMIENTO
    AND COMPROBANTE = UN_NUMERO;

  IF MI_RTA       > 0 THEN
     --
    -- Eliminar los datos del detalle y del comprobante.
    --
    MI_MSGERROR (1).CLAVE := 'MI_COMPANIANIIF';
    MI_MSGERROR (1).VALOR := MI_COMPANIANIIF;
    MI_MSGERROR (2).CLAVE := 'UN_ANIO';
    MI_MSGERROR (2).VALOR := UN_ANIO;
    MI_MSGERROR (3).CLAVE := 'UN_TIPOMOVIMIENTO';
    MI_MSGERROR (3).VALOR := UN_TIPOMOVIMIENTO;
    MI_MSGERROR (4).CLAVE := 'UN_NUMERO';
    MI_MSGERROR (4).VALOR := UN_NUMERO;
    -- eliminar del DETALLE_COMPROBANTE_CNT
    MI_CONDICION:= 'COMPANIA       = '''||MI_COMPANIANIIF||'''                    
                    AND ANO        = '||UN_ANIO||'                     
                    AND TIPO_CPTE  = '''||UN_TIPOMOVIMIENTO||'''                    
                    AND COMPROBANTE= '||UN_NUMERO||' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT', 
                                             UN_ACCION    => 'E', 
                                             UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE, 
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_NO_BORRAR_DET, 
                                  UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT', 
                                  UN_REEMPLAZOS => MI_MSGERROR);
    END;
    -- eliminar de la tabla COMPROBANTE_CNTRETENCION
        MI_CONDICION:= 'COMPANIA   = '''||MI_COMPANIANIIF||'''                    
                        AND ANO        = '||UN_ANIO||'                     
                        AND TIPO       = '''||UN_TIPOMOVIMIENTO||'''                    
                        AND NUMERO     = '||UN_NUMERO||' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTRETENCION', 
                                             UN_ACCION    => 'E', 
                                             UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE, 
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_NO_BORRAR_DET, 
                                  UN_TABLAERROR => 'COMPROBANTE_CNTRETENCION', 
                                  UN_REEMPLAZOS => MI_MSGERROR);
    END;
    -- eliminar de la tabla COMPROBANTE_CNT
            MI_CONDICION:= 'COMPANIA       = '''||MI_COMPANIANIIF||'''                    
                            AND ANO        = '||UN_ANIO||'                     
                            AND TIPO       = '''||UN_TIPOMOVIMIENTO||'''                    
                            AND NUMERO     = '||UN_NUMERO||' ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT', 
                                             UN_ACCION    => 'E', 
                                             UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE, 
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_NO_BORRAR_DET, 
                                  UN_TABLAERROR => 'COMPROBANTE_CNT', 
                                  UN_REEMPLAZOS => MI_MSGERROR);
    END;
    RETURN -1;
  END IF;
  RETURN 0;
END FC_BORRAR_DETCOMP_NIIF;

-- 7
FUNCTION FC_VERIFICAR_COMP_COPIAR
  /*
  NAME              : verificarCompACopiar
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 27/04/2017
  TIME              : 11:08 AM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: verificarCompACopiar
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT)
  RETURN VARCHAR2
AS
  MI_CUENTAS PCK_SUBTIPOS.TI_STRSQL;
BEGIN
  IF UN_NUMERO IS NOT NULL THEN
    MI_CUENTAS := '';
    FOR RS IN (
      SELECT DETALLE_COMPROBANTE_CNT.CUENTA,
        V_PLAN_CONTABLE.BLOQUEACUENTA AS BLOQUEO
      FROM V_PLAN_CONTABLE
        INNER JOIN DETALLE_COMPROBANTE_CNT
        ON V_PLAN_CONTABLE.COMPANIA            = DETALLE_COMPROBANTE_CNT.COMPANIA
        AND V_PLAN_CONTABLE.ANO                = DETALLE_COMPROBANTE_CNT.ANO
        AND V_PLAN_CONTABLE.CODIGO             = DETALLE_COMPROBANTE_CNT.CUENTA
        AND V_PLAN_CONTABLE.CENTRO_COSTO       = DETALLE_COMPROBANTE_CNT.CENTRO_COSTO
        AND V_PLAN_CONTABLE.TERCERO            = DETALLE_COMPROBANTE_CNT.TERCERO
        AND V_PLAN_CONTABLE.SUCURSAL           = DETALLE_COMPROBANTE_CNT.SUCURSAL
        AND V_PLAN_CONTABLE.REFERENCIA         = DETALLE_COMPROBANTE_CNT.REFERENCIA
      WHERE DETALLE_COMPROBANTE_CNT.COMPANIA   = ''||UN_COMPANIA||''
        AND DETALLE_COMPROBANTE_CNT.ANO        = UN_ANIO
        AND TIPO_CPTE                          = ''||UN_TIPOMOVIMIENTO||''
        AND COMPROBANTE                        = UN_NUMERO
        AND V_PLAN_CONTABLE.BLOQUEACUENTA = 'SI'
      ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,
        DETALLE_COMPROBANTE_CNT.ANO,
        DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
        DETALLE_COMPROBANTE_CNT.COMPROBANTE,
        DETALLE_COMPROBANTE_CNT.CONSECUTIVO ) 
    LOOP
        MI_CUENTAS := MI_CUENTAS ||' ' || RS.CUENTA;
    END LOOP;
  END IF;
  RETURN MI_CUENTAS;
END FC_VERIFICAR_COMP_COPIAR;


-- 8
FUNCTION FC_VERIFICAR_TIENE_DETALLE
  /*
  NAME              : verificarTieneDetalle
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 28/04/2017
  TIME              : 09:08 AM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: verificarTieneDetalle
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RTA PCK_SUBTIPOS.TI_ENTERO := 0;
BEGIN
  BEGIN
    SELECT DISTINCT COUNT(*)
    INTO MI_RTA
    FROM DETALLE_COMPROBANTE_CNT
    WHERE COMPANIA = ''||UN_COMPANIA||''
    AND ANO        = UN_ANIO
    AND TIPO_CPTE  = ''||UN_TIPOMOVIMIENTO||''
    AND COMPROBANTE= UN_NUMERO;
    IF MI_RTA      > 0 THEN
      RETURN -1;
    ELSE
      RETURN 0;
    END IF;
  END;
EXCEPTION
WHEN NO_DATA_FOUND THEN
  RETURN 0;
END FC_VERIFICAR_TIENE_DETALLE;

-- 9
PROCEDURE PR_ACT_VALOR_DOC_NOCERO
  /*
  NAME              : actualizarValorDocNoCero
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 28/04/2017
  TIME              : 03:34 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: actualizarValorDocNoCero
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG,
    UN_VLRBASE        IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VALORBASE      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLRBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VALORIVA       IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_RTA        PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  -- DSS 39040
  -- URL13470
  SELECT COUNT(*)
  INTO MI_RTA
  FROM DETALLE_COMPROBANTE_CNT
  WHERE COMPANIA    = ''||UN_COMPANIA||''
  AND ANO           = UN_ANIO
  AND TIPO_CPTE     = ''||UN_TIPOMOVIMIENTO||''
  AND COMPROBANTE   = UN_NUMERO
  AND BASE_GRAVABLE = UN_VLRBASE;
  IF MI_RTA        <> 0 THEN
    BEGIN
      BEGIN
        MI_CAMPOS       := 'BASE_GRAVABLE     = '|| ROUND(NVL(UN_VALORBASE, 0), 2)||' ,
                            MODIFIED_BY       = '''|| UN_USUARIO ||''' , 
                            DATE_MODIFIED     = SYSDATE ';
        MI_CONDICION    := 'COMPANIA          ='''|| UN_COMPANIA ||''' 
                            AND ANO           ='||UN_ANIO||' 
                            AND TIPO_CPTE     ='''||UN_TIPOMOVIMIENTO||''' 
                            AND COMPROBANTE   ='||UN_NUMERO||' 
                            AND BASE_GRAVABLE = '||NVL(UN_VLRBASE, 0)||' ';
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT', 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION );
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_ACTDETNRO);
    END;
  END IF;
  -- DSS 39041
  -- URL13471
  MI_RTA := 0;
  SELECT COUNT(*)
  INTO MI_RTA
  FROM DETALLE_COMPROBANTE_CNT
  WHERE COMPANIA  = ''||UN_COMPANIA||''
    AND ANO         = UN_ANIO
    AND TIPO_CPTE   = ''||UN_TIPOMOVIMIENTO||''
    AND COMPROBANTE= UN_NUMERO
    AND BASE_IVA   = UN_VLRBASEIVA;
  IF MI_RTA      <> 0 THEN
    BEGIN
      BEGIN
        MI_CAMPOS       := 'BASE_IVA        = '|| NVL(UN_VALORIVA, 0)||' , 
                            MODIFIED_BY     = '''|| UN_USUARIO ||''' ,
                            DATE_MODIFIED   = SYSDATE ';
        MI_CONDICION    := 'COMPANIA        ='''|| UN_COMPANIA ||''' 
                            AND ANO         ='||UN_ANIO||' 
                            AND TIPO_CPTE   ='''||UN_TIPOMOVIMIENTO||''' 
                            AND COMPROBANTE ='||UN_NUMERO||'
                            AND BASE_IVA    = '||NVL(UN_VLRBASEIVA, 0)||' ';
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT', 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION );
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_ACTDETNRO);
    END;
  END IF;
END PR_ACT_VALOR_DOC_NOCERO;

-- 10
PROCEDURE PR_ACT_TERCERO_DET
  /*
  NAME              : actualizarTerceroDet
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 02/05/2017
  TIME              : 10:10 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: actualizarTerceroDet
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG,
    UN_TERCEROINI     IN PCK_SUBTIPOS.TI_TERCERO,
    UN_TERCEROFIN     IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSALINI    IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_SUCURSALFIN    IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )
AS
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  BEGIN
    MI_CAMPOS       := 'TERCERO         = '''|| UN_TERCEROFIN ||'''  , 
                        SUCURSAL        = '''|| UN_SUCURSALFIN ||''' ,
                        MODIFIED_BY     = '''|| UN_USUARIO ||''' ,
                        DATE_MODIFIED   = SYSDATE ';
    MI_CONDICION    := 'COMPANIA        ='''|| UN_COMPANIA ||''' 
                        AND ANO         ='||UN_ANIO||' 
                        AND TIPO_CPTE   ='''||UN_TIPOMOVIMIENTO||''' 
                        AND COMPROBANTE ='||UN_NUMERO||' 
                        AND TERCERO     = '''|| UN_TERCEROINI ||''' 
                        AND SUCURSAL    = '''|| UN_SUCURSALINI ||''' ';
    PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT', 
                                         UN_ACCION    => 'M', 
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION );
  EXCEPTION
  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
  END;
EXCEPTION
WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
  PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD => SQLCODE, 
                              UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_ACTTERCERO);
END PR_ACT_TERCERO_DET;

-- 11
PROCEDURE PR_INSERT_CPTE_CNTRET
  /*
  NAME              : insertarComprobanteCNTRet
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
  DATE MIGRADOR     : 02/05/2017
  TIME              : 11:50 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  MODIFICATIONS     :
  @NAME: insertarComprobanteCNTRet
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO         IN PCK_SUBTIPOS.TI_LONG,
    UN_VLRBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE,
    UN_VLRBASE        IN PCK_SUBTIPOS.TI_DOBLE,
    UN_TIPOCOBRO      IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_CONCEPTOSF     IN RETENCIONESCONCEPTO.CONCEPTO%TYPE,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_PARMONTO PCK_SUBTIPOS.TI_PARAMETRO;
  MI_CAMPOS   PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES  PCK_SUBTIPOS.TI_VALORES;
BEGIN
  BEGIN
    MI_PARMONTO     := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,
                                             'MONTO SOMETIDO RETENCION IVA = VALOR IVA', 
                                             UN_MODULO, 
                                             SYSDATE);
    MI_CAMPOS       := 'COMPANIA, 
                        ANO, 
                        TIPO, 
                        NUMERO, 
                        TIPORETENCION, 
                        CODIGORETENCION, 
                        VALOR, 
                        VALORBASE,
                        CREATED_BY,
                        DATE_CREATED 
                        ';
    MI_VALORES      := 'SELECT COMPANIA, ANO, 
                          '''|| UN_TIPOMOVIMIENTO ||''', 
                          '|| UN_NUMERO ||', TIPO, CODIGO, 0, 
                          CASE WHEN TIPO = ''IVA'' 
                            AND '''||MI_PARMONTO||''' = ''SI'' 
                          THEN '||UN_VLRBASEIVA||' 
                          ELSE '||UN_VLRBASE||' 
                          END ,
                          '''|| UN_USUARIO ||''' ,
                          SYSDATE
                        FROM RETENCIONESCONCEPTO 
                        WHERE COMPANIA = '''||UN_COMPANIA||'''  
                          AND TIPOCOBRO = '''||UN_TIPOCOBRO ||'''  
                          AND CONCEPTO = '''|| UN_CONCEPTOSF||''' ';
    PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA => 'COMPROBANTE_CNTRETENCION', 
                                         UN_ACCION => 'IS', 
                                         UN_CAMPOS => MI_CAMPOS, 
                                         UN_VALORES => MI_VALORES );
  EXCEPTION
  WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
  END;
EXCEPTION
WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
  PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD => SQLCODE, 
                              UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_INS_CPTERET);
END PR_INSERT_CPTE_CNTRET;

FUNCTION FC_ENUMERARCOMPROBANTECNT
  /*
    NAME              : FC_ENUMERARCOMPROBANTECNT
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ BLANCO
    DATE MIGRADOR     : 28/08/2017
    TIME              : 10:57 AM
    SOURCE MODULE     : Contabilidad (Función Enumerar del formulario ComprobanteCnt)
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : genera el consecutivo del comprobante cnt cuando este no fue ingresado como nulo
                        Sera usado unicamente en trigger de la misma tabla
    @NAME:  enumerarComprobanteCnt
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO         IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO       IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT DEFAULT 0,
    UN_CENTRO_COSTO IN PCK_SUBTIPOS.TI_CENTRO_COSTO         DEFAULT PCK_DATOS.FC_CONS_CENTRO
  )
  RETURN PCK_SUBTIPOS.TI_LONG
  AS
    MI_PAR    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_NUMERO PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_MSGERRORES             PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_NUMERO:=0;
    IF UN_NUMERO<>0 THEN
      RETURN UN_NUMERO;      
    END IF;


    MI_PAR:= UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, 
                                       UN_NOMBRE   => 'MANEJA CONSECUTIVO POR CENTRO DE COSTO', 
                                       UN_MODULO   => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                       UN_FECHA_PAR=> SYSDATE), 'NO'));

    IF MI_PAR='SI' THEN 
      MI_NUMERO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'COMPROBANTE_CNT' ,
                                                  UN_CRITERIO =>'COMPANIA     =''' || UN_COMPANIA     ||'''' ||
                                                           ' AND ANO          = '  || UN_ANIO         ||
                                                           ' AND TIPO         =''' || UN_TIPO         ||'''' ||
                                                           ' AND CENTRO_COSTO =''' || UN_CENTRO_COSTO ||'''',
                                                  UN_CAMPO    =>'NUMERO',
                                                  UN_INICIAL  =>'0');
    ELSE
      MI_NUMERO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'COMPROBANTE_CNT' ,
                                                  UN_CRITERIO =>'COMPANIA     =''' || UN_COMPANIA     ||'''' ||
                                                           ' AND ANO          ='   || UN_ANIO         ||
                                                           ' AND TIPO         =''' || UN_TIPO         ||'''' ,
                                                  UN_CAMPO    =>'NUMERO',
                                                  UN_INICIAL  =>'0');
    END IF;
    IF MI_NUMERO = 0 THEN
      MI_NUMERO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CONSECUTIVOTC' ,
                                                  UN_CRITERIO =>'COMPANIA        =''' || UN_COMPANIA     ||'''' ||
                                                           ' AND ANO             ='   || UN_ANIO         ||
                                                           ' AND TIPOCOMPROBANTE =''' || UN_TIPO         ||'''' ,
                                                  UN_CAMPO    =>'CONSECUTIVO',
                                                  UN_INICIAL  =>'0');
      IF MI_NUMERO <>0 THEN
        IF MI_PAR='SI' THEN
          MI_NUMERO := UN_ANIO || LPAD(TRIM(RPAD(UN_CENTRO_COSTO,2)),2,'0') || LPAD(TRIM(RPAD(MI_NUMERO,4)),4,'0');
        ELSE 
          MI_NUMERO := UN_ANIO ||  LPAD(TRIM(RPAD(MI_NUMERO,6)),6,'0');
        END IF;
      ELSE
        MI_NUMERO :=UN_ANIO ||  LPAD(1,6,'0');

        DECLARE
          MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
          MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
          MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
        BEGIN
          MI_CAMPOS := 'COMPANIA,TIPOCOMPROBANTE,ANO,CONSECUTIVO, DATE_CREATED';
          MI_VALORES := '''' || UN_COMPANIA || '''
                        ,''' || UN_TIPO     || '''
                        ,'   || UN_ANIO     || '
                        ,       1, SYSDATE';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'CONSECUTIVOTC', 
                                                 UN_ACCION  => 'I', 
                                                 UN_CAMPOS  => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;

      END IF;
    END IF;
    RETURN MI_NUMERO;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    MI_MSGERRORES(1).CLAVE := 'TIPO';
    MI_MSGERRORES(1).VALOR := UN_TIPO;
    PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD    => SQLCODE,
                             UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INS_CONSECUTIVO,
                             UN_REEMPLAZOS => MI_MSGERRORES);
END;

PROCEDURE PR_ELIMINAR_CUENTA 
(
UN_COMPANIA    PCK_SUBTIPOS.TI_COMPANIA, 
UN_ANO         PCK_SUBTIPOS.TI_ANIO,  
UN_CODIGO      PCK_SUBTIPOS.TI_CODIGOCONTA
)
AS
 MI_EXISTE 		      PCK_SUBTIPOS.TI_TEXTO1 DEFAULT 'N';
 MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;  
BEGIN
  BEGIN

  SELECT DISTINCT 'X'
  INTO MI_EXISTE
  FROM PLAN_CONTABLE INI 
  WHERE COMPANIA=UN_COMPANIA
    AND ANO = UN_ANO
    AND UN_CODIGO = SUBSTR(CODIGO,1,LENGTH(UN_CODIGO))
    AND UN_CODIGO <> CODIGO;

  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_EXISTE:='N';
  END;
  IF MI_EXISTE<>'N' THEN
    BEGIN  
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;
      MI_MSGERROR(2).CLAVE := 'ANO';
      MI_MSGERROR(2).VALOR := UN_ANO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE
           ,UN_TABLAERROR => 'PLAN_CONTABLE'
           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_ELIMINACUENTA
           ,UN_REEMPLAZOS => MI_MSGERROR
           );
    END;  
  END IF; 

END PR_ELIMINAR_CUENTA;

END PCK_CONTABILIDAD_CPTE;