create or replace PACKAGE BODY "PCK_CONTABILIDAD3" AS
  /**@package:  Contabilidad **/

  FUNCTION FC_CALCULAR_INCONSISTENCIAS
    /*
    NAME              : FC_CALCULAR_INCONSISTENCIAS (En Access, CalcularInconsistencias, dentro del código de Descuadros)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Oscar Torres Corredor
    DATE MIGRADOR     : 14/04/2016
    TIME              : 08:00 AM
    SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Proceso que calcula las inconsistencias.
    @NAME:  verificarInconsistenciasCuentasContables
    @METHOD:  GET
    */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN VARCHAR2
  AS
     MI_LONGITUD_MAL                VARCHAR2(3200 CHAR);
     MI_CUENTAS_CON_MOV_MAL         VARCHAR2(3200 CHAR);
     MI_ID_CODIGO_DIFERENTE         VARCHAR2(3200 CHAR);
     MI_CUENTAS_CON_MOVIMIENTO      VARCHAR2(3200 CHAR);
     MI_CUENTAS_MAYORES_POR_CREAR   VARCHAR2(3200 CHAR);
     MI_TEMPORAL                    VARCHAR2(3200 CHAR);
     MI_INDANT                      VARCHAR2(3200 CHAR):= '***';
     MI_MOVANT                      VARCHAR2(3200 CHAR);
     MI_ERROR_FUN                   PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 1;
     MI_RESULTADO                   VARCHAR2(20000 CHAR);
     MI_VAR_CONTEO                  NUMBER := 6; 

  BEGIN
     <<INCONSISTENCIAS>>
       FOR RS IN(
          SELECT    ID,CODIGO,NOMBRE,MOVIMIENTO,CENTRO_COSTO,TERCERO,AUXILIAR,MAN_AUX_TER,MAN_AUX_GEN,MAN_CEN_CTO
          FROM      V_PLAN_CONTABLE
          WHERE     COMPANIA = UN_COMPANIA
          AND       ANO      = UN_ANIO
          ORDER BY  COMPANIA,
                    ANO,
                    ID
       )
       LOOP
          BEGIN
              IF  TRIM(SUBSTR(RS.ID,1,16)) <> RS.CODIGO THEN
                  MI_ID_CODIGO_DIFERENTE := (CASE WHEN MI_ID_CODIGO_DIFERENTE IS NOT NULL
                                                  THEN MI_ID_CODIGO_DIFERENTE||','
                                                  ELSE ''
                                                  END)||RS.ID;
              END IF;
              IF  LENGTH(RS.ID) <> 1 AND LENGTH(RS.ID) <> 2 AND LENGTH(RS.ID) <> 4 AND LENGTH(RS.ID) <> 6 AND LENGTH(RS.ID) <= 6 THEN
                  MI_LONGITUD_MAL        := (CASE WHEN MI_LONGITUD_MAL IS NOT NULL
                                                  THEN MI_LONGITUD_MAL||','
                                                  ELSE ''
                                                  END)||RS.ID;
              END IF;
              IF  LENGTH(RS.ID) < 6  AND (RS.MOVIMIENTO = -1 OR RS.MAN_AUX_TER = -1 OR RS.MAN_AUX_GEN = -1 OR RS.MAN_CEN_CTO = -1) THEN
                  MI_CUENTAS_CON_MOV_MAL := (CASE WHEN MI_CUENTAS_CON_MOV_MAL IS NOT NULL
                                                  THEN MI_CUENTAS_CON_MOV_MAL||','
                                                  ELSE ''
                                                  END)||RS.ID;
              END IF;
              IF  MI_INDANT <> '***' AND SUBSTR(RS.ID,1,LENGTH(MI_INDANT)) = MI_INDANT THEN
                  IF  RS.MOVIMIENTO = -1 AND MI_MOVANT = -1 THEN
                      IF  INSTR(MI_CUENTAS_CON_MOVIMIENTO,MI_INDANT) <= 0 THEN
                          MI_CUENTAS_CON_MOVIMIENTO := (CASE WHEN MI_CUENTAS_CON_MOVIMIENTO IS NOT NULL
                                                             THEN MI_CUENTAS_CON_MOVIMIENTO||','
                                                             ELSE ''
                                                             END)||MI_INDANT;
                      END IF;
                  END IF;
              END IF;
              IF  NVL(RS.CENTRO_COSTO ||''||RS.AUXILIAR||''||RS.TERCERO,' ') = ' ' THEN
                  IF ((LENGTH(RS.ID) > LENGTH(MI_INDANT)               AND
                       LENGTH(RS.ID) = 2 AND LENGTH(MI_INDANT) <> 1    OR
                       LENGTH(RS.ID) = 4 AND LENGTH(MI_INDANT) <> 2    OR
                       LENGTH(RS.ID) = 6 AND LENGTH(MI_INDANT) <> 4    OR
                       LENGTH(RS.ID) > 6 AND LENGTH(MI_INDANT) <  6)   OR
                      (LENGTH(RS.ID) = 2 AND LENGTH(MI_INDANT)  = 1    OR
                       LENGTH(RS.ID) = 4 AND LENGTH(MI_INDANT)  = 2    OR
                       LENGTH(RS.ID) = 6 AND LENGTH(MI_INDANT)  = 4    OR
                       LENGTH(RS.ID) > 6 AND LENGTH(MI_INDANT) <= 6)   AND
                       MI_INDANT <> SUBSTR(RS.ID,1,LENGTH(MI_INDANT))) OR
                      (LENGTH(RS.ID) = 2 AND SUBSTR(RS.ID,1,1) <> SUBSTR(MI_INDANT,1,1) OR
                       LENGTH(RS.ID) = 4 AND SUBSTR(RS.ID,1,2) <> SUBSTR(MI_INDANT,1,2) OR
                       LENGTH(RS.ID) = 6 AND SUBSTR(RS.ID,1,4) <> SUBSTR(MI_INDANT,1,4) OR
                       LENGTH(RS.ID) > 6 AND SUBSTR(RS.ID,1,6) <> SUBSTR(MI_INDANT,1,6))

                       THEN
                       IF  LENGTH(RS.ID) = 2 THEN
                           MI_TEMPORAL := SUBSTR(RS.ID,1,1);
                       END IF;
                       IF  LENGTH(RS.ID) = 4 THEN
                           MI_TEMPORAL := SUBSTR(RS.ID,1,2);
                       END IF;
                       IF  LENGTH(RS.ID) = 6 THEN
                           MI_TEMPORAL := SUBSTR(RS.ID,1,4);
                       END IF;
                       IF  LENGTH(RS.ID) > 6 THEN
                           MI_TEMPORAL := SUBSTR(RS.ID,1,6);
                       END IF;
                       /*IF  INSTR(MI_CUENTAS_MAYORES_POR_CREAR, MI_TEMPORAL) <= 0 THEN
                           MI_CUENTAS_MAYORES_POR_CREAR := MI_TEMPORAL||','||MI_CUENTAS_MAYORES_POR_CREAR;
                       END IF;*/
                  END IF;
              END IF;
              IF  NVL(RS.CENTRO_COSTO||''||RS.AUXILIAR||''||RS.TERCERO,' ') = ' '
                      AND (LENGTH(RS.ID) <> LENGTH(MI_INDANT) OR LENGTH(RS.ID) = LENGTH(MI_INDANT) AND RS.ID <> MI_INDANT) THEN
                    MI_INDANT := RS.ID;
                    MI_MOVANT := RS.MOVIMIENTO;
              END IF;
              EXCEPTION  WHEN OTHERS THEN
                  PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar la reversaciÃ³n.';
                  PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG, 'FC_CALCULAR_INCONSISTENCIAS','',SQLERRM );
                  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
          END;
       END LOOP INCONSISTENCIAS;
       --7727572 lvega-- INI: Se modifica la forma en la que evalua cuentas faltantes
        FOR i IN 1..3 LOOP
        
        FOR RS IN ((SELECT
                     DISTINCT SUBSTR(CODIGO,0,MI_VAR_CONTEO) CODIGO
                    FROM
                     SALDO_AUX_CONTABLE
                     WHERE COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANIO
                    UNION
                    SELECT
                     DISTINCT SUBSTR(CUENTA,0,MI_VAR_CONTEO) CUENTA
                    FROM
                     DETALLE_COMPROBANTE_CNT
                     WHERE COMPANIA = UN_COMPANIA
                     AND ANO = UN_ANIO
                    )
                     MINUS        
                     SELECT
                     CODIGO
                     FROM PLAN_CONTABLE
                     WHERE COMPANIA = UN_COMPANIA
                     AND ANO = UN_ANIO)
              LOOP
              MI_CUENTAS_MAYORES_POR_CREAR := RS.CODIGO ||','||MI_CUENTAS_MAYORES_POR_CREAR;        
              END LOOP;
              
              MI_VAR_CONTEO := MI_VAR_CONTEO - 2;
              
              END LOOP;
              MI_CUENTAS_MAYORES_POR_CREAR :=  SUBSTR(MI_CUENTAS_MAYORES_POR_CREAR, 1, LENGTH(MI_CUENTAS_MAYORES_POR_CREAR) - 1); 
         --7727572 lvega-- FIN   
       IF MI_LONGITUD_MAL IS NULL THEN
        MI_LONGITUD_MAL := 'NINGUNO';
       END IF;
       IF MI_CUENTAS_CON_MOV_MAL IS NULL THEN
        MI_CUENTAS_CON_MOV_MAL := 'NINGUNO';
       END IF;
       IF MI_CUENTAS_CON_MOVIMIENTO IS NULL THEN
        MI_CUENTAS_CON_MOVIMIENTO := 'NINGUNO';
       END IF;
       IF MI_CUENTAS_MAYORES_POR_CREAR IS NULL THEN
        MI_CUENTAS_MAYORES_POR_CREAR := 'NINGUNO';
        END IF;
       IF MI_ID_CODIGO_DIFERENTE IS NULL THEN
        MI_ID_CODIGO_DIFERENTE := 'NINGUNO';
       END IF;

       MI_RESULTADO :=  MI_CUENTAS_CON_MOV_MAL        ||'$'||
                        MI_CUENTAS_CON_MOVIMIENTO     ||'$'||
                        MI_LONGITUD_MAL               ||'$'||
                        MI_CUENTAS_MAYORES_POR_CREAR  ||'$'||
                        MI_ID_CODIGO_DIFERENTE;
    RETURN MI_RESULTADO;
    EXCEPTION  WHEN OTHERS THEN
          PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar la reversaciÃ³n.';
          PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_CALCULAR_INCONSISTENCIAS','',SQLERRM );
          RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); 
  END FC_CALCULAR_INCONSISTENCIAS;

  FUNCTION FC_CREA_CUENTA_NIIF (
    /*
    NAME              : FC_CREA_CUENTA_NIIF (En Access, creacuentaniif, dentro del código de Contabilizar comprobantes NIIF)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Oscar Torres Corredor
    DATE MIGRADOR     : 14/04/2016
    TIME              : 08:00 AM
    SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Proceso que permite crear cuentas niif.
    @NAME:  generarCuentasContablesNiif
    @METHOD:  GET
    */
    UN_COMPANIA 		IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO     		IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO   		IN PCK_SUBTIPOS.TI_CODIGOCONTA
  )
  RETURN VARCHAR2
  AS

   MI_ERROR_FUN   	PCK_SUBTIPOS.TI_ERROR_FUN := GL_ERROR_NUM + 2;
   MI_TRANSACCION 	PCK_SUBTIPOS.TI_RTA_ACME;
   MI_RESULTADO   	VARCHAR2(200 CHAR) := 'OK';

   BEGIN
     <<CREANIIF>>
       FOR DC IN(
            SELECT  COMPANIA,
                    ANO,
                    ID,
                    CODIGO,
                    CENTRO_COSTO,
                    NATURALEZA,
                    NOMBRE,
                    MOVIMIENTO,
                    MAN_CEN_CTO,
                    MAN_AUX_TER,
                    MAN_AUX_GEN,
                    CLASECUENTA,
                    CORRIENTE,
                    CONCEPTOEX,
                    PERMITECONSOLIDAR
             FROM   V_PLAN_CONTABLE
             WHERE  COMPANIA  =   UN_COMPANIA
             AND    ANO       =   UN_ANIO
             AND    ID        =   UN_CODIGO
             )
             LOOP
                BEGIN

                MI_TRANSACCION := PCK_DATOS.FC_ACME( 'PLAN_CONTABLE','I'
                                  , 'COMPANIA,ANO,CODIGO,NATURALEZA,NOMBRE,MOVIMIENTO, CLASECUENTA,
                                     CORRIENTE, CONCEPTOEX,MAN_CEN_CTO,MAN_AUX_TER, MAN_AUX_GEN,PERMITECONSOLIDAR,
                                     OBLIGA_TERCERO,OBLIGA_CENTRO,OBLIGA_AUXILIAR,OBLIGA_REFERENCIA,OBLIGA_FUENTE,
                                     PRESUPUESTO_ANUAL,BLOQUEACUENTA, SALDOINICIAL, SALDO0,SALDO1,SALDO2,
                                     SALDO3,SALDO4,SALDO5,SALDO6,SALDO7,SALDO8,SALDO9,SALDO10,SALDO11,SALDO12,SALDO13,
                                     NETO0,NETO1,NETO2,NETO3,NETO4,NETO5,NETO6,NETO7,NETO8,NETO9,NETO10,NETO11,NETO12,NETO13,
                                     DEBITO0,DEBITO1,DEBITO2,DEBITO3,DEBITO4,DEBITO5,DEBITO6,DEBITO7,DEBITO8,DEBITO9,DEBITO10,DEBITO11,DEBITO12,DEBITO13,
                                     CREDITO0,CREDITO1,CREDITO2,CREDITO3,CREDITO4,CREDITO5,CREDITO6,CREDITO7,CREDITO8,CREDITO9,CREDITO10,CREDITO11,CREDITO12,CREDITO13,
                                     AJUSTE0,AJUSTE1,AJUSTE2,AJUSTE3,AJUSTE4,AJUSTE5,AJUSTE6,AJUSTE7,AJUSTE8,AJUSTE9,AJUSTE10,AJUSTE11,AJUSTE12,AJUSTE13,
                                     GENERADESEMBOLSO,CHEQUE,PORCRETENCION,CREDITOEXTERNO,PASARSALDO,MAN_FACT_ARRENDAMIENTO,
                                     NOTRANSACCIONAL5544,NOREPORTARRECIPROCAS, ESOFICIAL, IVAEX,RETEPRACTICADA,RETEASUMIDA,IVACOMUN, IVASIMPLIFICADO, EXDISTRITAL,
                                     MAN_DISTRI_CCOSTO, RETEICA, CREE_PRACTICADA, CREE_ASUMIDA, REPORTASALDORECIPROCAS, MEN, VERIFICAR_MOV, SALDO_CONCILIACION'
                                  , ''''||UN_COMPANIA  ||''','||  UN_ANIO        ||','''||     UN_CODIGO         ||''','''||   DC.NATURALEZA    ||''','''||
                                          DC.NOMBRE    ||''','||  DC.MOVIMIENTO  ||','''||     DC.CLASECUENTA    ||''','||     DC.CORRIENTE     ||','''||
                                          NVL(DC.CONCEPTOEX,' ')  ||''','||  DC.MAN_CEN_CTO ||','|| DC.MAN_AUX_TER  ||','||       DC.MAN_AUX_GEN   ||','||
                                          DC.PERMITECONSOLIDAR ||'0,0,0,0,0,0,0,''NO'',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
                                          ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,0,0
                                          ,0,0,0,0,0,0,0,0,-1,0,0,0,0,0'
                                  ,NULL,NULL,NULL, NULL, NULL,NULL);
              /*   EXCEPTION  WHEN OTHERS THEN
                      MI_RESULTADO := 'ERROR';
                      PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar la reversación.';
                      PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_CALCULAR_INCONSISTENCIAS','',SQLERRM );
                      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); */

                END;
             END LOOP CREANIIF;
    RETURN MI_RESULTADO;
  /*  EXCEPTION  WHEN OTHERS THEN
              MI_RESULTADO := 'ERROR';
              PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar la reversación.';
              PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_CALCULAR_INCONSISTENCIAS','',SQLERRM );
              RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); */
    END FC_CREA_CUENTA_NIIF;

  FUNCTION FC_NIIF_LOTES
  (
    /*
    NAME              : FC_NIIF_LOTES (En Access, niif_lotes, dentro del código de Contabilizar comprobantes NIIF)
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Oscar Torres Corredor
    DATE MIGRADOR     : 14/04/2016
    TIME              : 08:00 AM
    SOURCE MODULE     : Contabilidad - SysmanCT2016.02.06
    MODIFIER          : Adriana Caceres Bonilla
    DATE MODIFIED     : 16-17/11/2016
    TIME              :
    DESCRIPTION       : Proceso que contabiliza los comprobantes NIIF.
    @NAME:  contabilizarComprobantesContablesNiif
    @METHOD:  GET
    */
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MES_INICIAL  IN PCK_SUBTIPOS.TI_MES,
    UN_MES_FINAL    IN PCK_SUBTIPOS.TI_MES,
    UN_TIPO_INICIAL IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_TIPO_FINAL   IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO      IN VARCHAR2
  )
  RETURN VARCHAR2
  AS

    MI_ERROR_FUN     PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 3;
    MI_DESCRIPCION   VARCHAR2(250 CHAR);
    MI_NRODOCUMENTO  VARCHAR2(30 CHAR);
    MI_IDCUENTA      VARCHAR2(200 CHAR);
    MI_RESULTADO     PCK_SUBTIPOS.TI_RTA_ACME;
    MI_RESPUESTA     VARCHAR2(3200 CHAR) := 'OK';
    MI_COMPANIA_NIIF PCK_SUBTIPOS.TI_COMPANIA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'COMPAÑIA EQUIVALENTE NIIF',1,SYSDATE);
    MI_STRETAPA      VARCHAR2(200 CHAR); 
    MI_RS            VARCHAR2(3200 CHAR); 
    MI_TEXTO         VARCHAR2(3200 CHAR); 
    MI_CAMPOS        VARCHAR2(3200 CHAR); 
    MI_VALORES       VARCHAR2(3200 CHAR); 
    MI_FECHA_VCN     DATE; 


  BEGIN
   EXECUTE IMMEDIATE  'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
      PCK_PREPARAR_ANO.PR_COPIAR_TERCERO(UN_COMPANIA, MI_COMPANIA_NIIF);
      PCK_PREPARAR_ANO.PR_COPIAR_CENTRO_COSTO(UN_COMPANIA, UN_ANIO, UN_ANIO, MI_COMPANIA_NIIF);
      PCK_PREPARAR_ANO.PR_COPIAR_AUXILIAR(UN_COMPANIA, UN_ANIO, UN_ANIO, MI_COMPANIA_NIIF);

      MI_STRETAPA:='01'; 

      --Envia a un txt las cuentas que no tengan configurado codigo NIIF
      BEGIN 
      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('DETALLE_COMPROBANTE_CNT','E',NULL,NULL,NULL
                        ,' COMPANIA        =        '''|| MI_COMPANIA_NIIF  ||'''
                            AND   ANO      =          '|| UN_ANIO           ||'
                            AND   TIPO_CPTE BETWEEN '''|| UN_TIPO_INICIAL   ||''' AND '''||UN_TIPO_FINAL||'''
                            AND   TO_NUMBER(TO_CHAR(FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL
                        ,NULL, NULL, NULL,NULL);

      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('COMPROBANTE_CNTRETENCION','E',NULL,NULL,NULL
                        ,' COMPROBANTE_CNTRETENCION.COMPANIA        =   '''|| MI_COMPANIA_NIIF ||'''
                            AND   COMPROBANTE_CNTRETENCION.ANO      =     '|| UN_ANIO          ||'
                            AND   COMPROBANTE_CNTRETENCION.TIPO BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''|| UN_TIPO_FINAL ||'''
                            AND   EXISTS( SELECT * FROM COMPROBANTE_CNT
                                          LEFT JOIN COMPROBANTE_CNTRETENCION ON
                                                    (COMPROBANTE_CNT.COMPANIA   =    COMPROBANTE_CNTRETENCION.COMPANIA
                                                 AND COMPROBANTE_CNT.TIPO       =    COMPROBANTE_CNTRETENCION.TIPO
                                                 AND COMPROBANTE_CNT.NUMERO     =    COMPROBANTE_CNTRETENCION.NUMERO
                                                 AND COMPROBANTE_CNT.ANO        =    COMPROBANTE_CNTRETENCION.ANO)
                                          WHERE  COMPROBANTE_CNT.COMPANIA       = '''|| MI_COMPANIA_NIIF ||'''
                                          AND    COMPROBANTE_CNT.ANO            =   '|| UN_ANIO          ||'
                                          AND    COMPROBANTE_CNT.TIPO     BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                                          AND    TO_NUMBER(TO_CHAR(COMPROBANTE_CNT.FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||')'
                        ,NULL, NULL, NULL,NULL);


      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('COMPROBANTE_CNTAFECTADOS','E',NULL,NULL,NULL
                        ,' COMPROBANTE_CNTAFECTADOS.COMPANIA        =        '''|| MI_COMPANIA_NIIF ||'''
                            AND   COMPROBANTE_CNTAFECTADOS.ANO      =          '|| UN_ANIO          ||'
                            AND   COMPROBANTE_CNTAFECTADOS.TIPO_CPTE BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                            AND   EXISTS( SELECT * FROM COMPROBANTE_CNT
                                          LEFT JOIN COMPROBANTE_CNTAFECTADOS ON
                                                    (COMPROBANTE_CNT.COMPANIA   =   COMPROBANTE_CNTAFECTADOS.COMPANIA
                                                 AND COMPROBANTE_CNT.TIPO       =   COMPROBANTE_CNTAFECTADOS.TIPO_CPTE
                                                 AND COMPROBANTE_CNT.NUMERO     =   COMPROBANTE_CNTAFECTADOS.COMPROBANTE
                                                 AND COMPROBANTE_CNT.ANO        =   COMPROBANTE_CNTAFECTADOS.ANO)
                                          WHERE  COMPROBANTE_CNT.COMPANIA       = '''|| MI_COMPANIA_NIIF ||'''
                                          AND    COMPROBANTE_CNT.ANO            =   '|| UN_ANIO          ||'
                                          AND    COMPROBANTE_CNT.TIPO     BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                                          AND    TO_NUMBER(TO_CHAR(COMPROBANTE_CNT.FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL ||')'
                        ,NULL, NULL, NULL,NULL);


      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('COMPROBANTE_CNTCONCEPTO','E',NULL,NULL,NULL
                        ,' COMPROBANTE_CNTCONCEPTO.COMPANIA        =   '''|| MI_COMPANIA_NIIF||'''
                            AND   COMPROBANTE_CNTCONCEPTO.ANO      =     '|| UN_ANIO         ||'
                            AND   COMPROBANTE_CNTCONCEPTO.TIPO BETWEEN '''|| UN_TIPO_INICIAL ||''' AND '''||UN_TIPO_FINAL||'''
                            AND   EXISTS( SELECT * FROM COMPROBANTE_CNT
                                          LEFT JOIN COMPROBANTE_CNTCONCEPTO ON
                                                    (COMPROBANTE_CNT.COMPANIA   =   COMPROBANTE_CNTCONCEPTO.COMPANIA
                                                 AND COMPROBANTE_CNT.TIPO       =   COMPROBANTE_CNTCONCEPTO.TIPO
                                                 AND COMPROBANTE_CNT.NUMERO     =   COMPROBANTE_CNTCONCEPTO.NUMERO
                                                 AND COMPROBANTE_CNT.ANO        =   COMPROBANTE_CNTCONCEPTO.ANO)
                                          WHERE  COMPROBANTE_CNT.COMPANIA       = '''|| MI_COMPANIA_NIIF ||'''
                                          AND    COMPROBANTE_CNT.ANO            =   '|| UN_ANIO          ||'
                                          AND    COMPROBANTE_CNT.TIPO     BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                                          AND    TO_NUMBER(TO_CHAR(COMPROBANTE_CNT.FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||')'
                        ,NULL, NULL, NULL,NULL);

      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('COMPROBANTE_CNTBANCOS','E',NULL,NULL,NULL
                        ,' COMPROBANTE_CNTBANCOS.COMPANIA        =   '''|| MI_COMPANIA_NIIF||'''
                            AND   COMPROBANTE_CNTBANCOS.ANO      =     '|| UN_ANIO         ||'
                            AND   COMPROBANTE_CNTBANCOS.TIPO BETWEEN '''|| UN_TIPO_INICIAL ||''' AND '''||UN_TIPO_FINAL||'''
                            AND   EXISTS( SELECT * FROM COMPROBANTE_CNT
                                          LEFT JOIN COMPROBANTE_CNTBANCOS ON
                                                    (COMPROBANTE_CNT.COMPANIA   =   COMPROBANTE_CNTBANCOS.COMPANIA
                                                 AND COMPROBANTE_CNT.TIPO       =   COMPROBANTE_CNTBANCOS.TIPO
                                                 AND COMPROBANTE_CNT.NUMERO     =   COMPROBANTE_CNTBANCOS.NUMERO
                                                 AND COMPROBANTE_CNT.ANO        =   COMPROBANTE_CNTBANCOS.ANO)
                                          WHERE  COMPROBANTE_CNT.COMPANIA       = '''|| MI_COMPANIA_NIIF ||'''
                                          AND    COMPROBANTE_CNT.ANO            =   '|| UN_ANIO          ||'
                                          AND    COMPROBANTE_CNT.TIPO     BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                                          AND    TO_NUMBER(TO_CHAR(COMPROBANTE_CNT.FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||')'
                        ,NULL, NULL, NULL,NULL);

      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('DESEMBOLSO','E',NULL,NULL,NULL
                        ,' DESEMBOLSO.COMPANIA        =        '''|| MI_COMPANIA_NIIF ||'''
                            AND   DESEMBOLSO.ANO      =          '|| UN_ANIO          ||'
                            AND   DESEMBOLSO.TIPO_CPTE BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                            AND   EXISTS( SELECT * FROM COMPROBANTE_CNT
                                          LEFT JOIN DESEMBOLSO ON
                                                    (COMPROBANTE_CNT.COMPANIA   =   DESEMBOLSO.COMPANIA
                                                 AND COMPROBANTE_CNT.TIPO       =   DESEMBOLSO.TIPO_CPTE
                                                 AND COMPROBANTE_CNT.NUMERO     =   DESEMBOLSO.COMPROBANTE
                                                 AND COMPROBANTE_CNT.ANO        =   DESEMBOLSO.ANO)
                                          WHERE  COMPROBANTE_CNT.COMPANIA       = '''|| MI_COMPANIA_NIIF ||'''
                                          AND    COMPROBANTE_CNT.ANO            =   '|| UN_ANIO          ||'
                                          AND    COMPROBANTE_CNT.TIPO     BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                                          AND    TO_NUMBER(TO_CHAR(COMPROBANTE_CNT.FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||')'
                        ,NULL, NULL, NULL,NULL);


      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('COMPROBANTE_CNT','E',NULL,NULL,NULL
                      ,' COMPANIA        =    '''|| MI_COMPANIA_NIIF ||'''
                          AND   ANO      =      '|| UN_ANIO          ||'
                          AND   TIPO  BETWEEN '''|| UN_TIPO_INICIAL  ||''' AND '''||UN_TIPO_FINAL||'''
                          AND   TO_NUMBER(TO_CHAR(FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL
                       ,NULL, NULL, NULL,NULL);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END; 

      BEGIN
      MI_RESULTADO :=
      PCK_DATOS.FC_ACME('COMPROBANTE_CNT','M',' NONIIF = 0 ',NULL,NULL
                        ,' COMPANIA                   =  '''|| UN_COMPANIA    ||'''
                            AND  ANO                  =    '|| UN_ANIO        ||'
                            AND  TIPO  BETWEEN           '''|| UN_TIPO_INICIAL||''' AND '''||UN_TIPO_FINAL||'''
                            AND  TO_NUMBER(TO_CHAR(FECHA,''MM'')) BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||'
                            AND  NONIIF IS NULL'
                        ,NULL, NULL, NULL,NULL);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END; 

      <<COMPROBANTES>>
      FOR RS IN (SELECT  COMPANIA, 
                         ANO,
                         TIPO, 
                         NUMERO,
                         FECHA,
                         DESCRIPCION,
                         TEXTO, 
                         TERCERO,      
                         VLR_DOCUMENTO, 
                         SUCURSAL,
                         VLR_BASE, 
                         FECHA_VCN_DOC, 
                         DEBITO,
                         CREDITO,
                         MODIFIED_BY, 
                         VLRAGIRAR,        
                         DEBITOSAFECTADOS, 
                         CREDITOSAFECTADOS, 
                         DATE_CREATED,
                         DATE_MODIFIED,       
                         PORCIVA, 
                         CENTRO_COSTO, 
                         AUXILIAR, 
                         CREATED_BY, 
                         VLR_BASEIVA, 
                         HORA
                   FROM  COMPROBANTE_CNT
                  WHERE  COMPANIA= UN_COMPANIA 
                    AND  ANO= UN_ANIO
                    AND  TIPO BETWEEN UN_TIPO_INICIAL AND UN_TIPO_FINAL 
                    AND  TO_CHAR(FECHA, 'MM') BETWEEN UN_MES_INICIAL AND UN_MES_FINAL
                    AND  NONIIF IN(0)
                ORDER BY TIPO, NUMERO)
      LOOP
      BEGIN
        MI_DESCRIPCION   := NVL(RS.DESCRIPCION, '.'); 
        MI_TEXTO         := NVL(RS.TEXTO, ''); 
        MI_DESCRIPCION   := REPLACE(MI_DESCRIPCION, CHR(39), ''); 
        MI_DESCRIPCION   := REPLACE(MI_DESCRIPCION, '|', ''); 
        MI_TEXTO         := REPLACE(MI_TEXTO, CHR(39), ''); 
        MI_TEXTO         := REPLACE(MI_TEXTO, '|', ''); 

       BEGIN
        MI_CAMPOS  := 'COMPANIA, ANO, TIPO, NUMERO, FECHA, DESCRIPCION, TEXTO, TERCERO, SUCURSAL, VLR_DOCUMENTO, CREATED_BY, 
                                VLR_BASE, VLR_BASEIVA, FECHA_VCN_DOC, DEBITO, CREDITO, MODIFIED_BY, VLRAGIRAR, DEBITOSAFECTADOS,  
                                CREDITOSAFECTADOS, DATE_CREATED, DATE_MODIFIED, PORCIVA, CENTRO_COSTO, AUXILIAR, HORA'; 

        MI_VALORES:=  ''''|| MI_COMPANIA_NIIF ||''',  '|| UN_ANIO ||', ''' || RS.TIPO ||''',
                               '   || RS.NUMERO ||',  ''' || RS.FECHA || ''', ''' || NVL(MI_DESCRIPCION, '') ||''',
                               ''' || NVL(MI_TEXTO, '') ||''', ''' || RS.TERCERO ||''','''||  RS.SUCURSAL ||''',
                               ' || RS.VLR_DOCUMENTO ||', '''|| UN_USUARIO  ||''',' ||  TO_NUMBER(NVL(RS.VLR_BASE,0)) ||',
                               ' || TO_NUMBER(NVL(RS.VLR_BASEIVA,0)) ||',
                               ''' || RS.FECHA_VCN_DOC ||''',
                               ' || RS.DEBITO ||', '|| RS.CREDITO ||', '''|| RS.MODIFIED_BY || ''', '|| RS.VLRAGIRAR || ', 
                               ' || RS.DEBITOSAFECTADOS || ', '|| RS.CREDITOSAFECTADOS ||', ''' || NVL(RS.DATE_CREATED, RS.FECHA) || ''', 
                               ''' || NVL(RS.DATE_MODIFIED, RS.FECHA) || ''', '|| NVL(RS.PORCIVA, 0) ||', '''|| RS.CENTRO_COSTO || ''',
                               '''|| RS.AUXILIAR ||''', TO_DATE(''' || SYSDATE || ''',''DD/MM/YYYY HH24:mi:ss'')';    

         MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_CNT', 
                                       UN_ACCION  => 'I', 
                                       UN_CAMPOS  => MI_CAMPOS, 
                                       UN_VALORES => MI_VALORES);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
       END;    
      END LOOP COMPROBANTES; 

     <<CONTABILIZA>>
     FOR RS_DETALLE IN (
         SELECT DISTINCT DETALLE_COMPROBANTE_CNT.DESCRIPCION,
                DETALLE_COMPROBANTE_CNT.ANO,
                DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO,
                DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
                DETALLE_COMPROBANTE_CNT.TERCERO,
                DETALLE_COMPROBANTE_CNT.CUENTA,
                DETALLE_COMPROBANTE_CNT.SUCURSAL,
                DETALLE_COMPROBANTE_CNT.AUXILIAR,
                DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                DETALLE_COMPROBANTE_CNT.FECHA,
                DETALLE_COMPROBANTE_CNT.NATURALEZA,
                DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,
                DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
                DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                DETALLE_COMPROBANTE_CNT.EJECUCION_DEBITO,
                DETALLE_COMPROBANTE_CNT.EJECUCION_CREDITO,
                DETALLE_COMPROBANTE_CNT.BASE_GRAVABLE,
                DETALLE_COMPROBANTE_CNT.TIPO_DOCUMENTO,
                DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT,
                DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO,
                DETALLE_COMPROBANTE_CNT.CHEQUEPARAANULAR,
                DETALLE_COMPROBANTE_CNT.CONCEPTO_EX,
                DETALLE_COMPROBANTE_CNT.TIPOPPTAL,
                DETALLE_COMPROBANTE_CNT.BASE_IVA,
                DETALLE_COMPROBANTE_CNT.DESEMBOLSO,
                DETALLE_COMPROBANTE_CNT.SALDOCUENTA,
                DETALLE_COMPROBANTE_CNT.DATE_CREATED FECHACREACION,
                DETALLE_COMPROBANTE_CNT.CREATED_BY CREADOR,
                DETALLE_COMPROBANTE_CNT.MODIFIED_BY MODIFICADOR,
                DETALLE_COMPROBANTE_CNT.PORCENTAJERETENCION,
                DETALLE_COMPROBANTE_CNT.REVELACIONES,
                TO_DATE(TO_CHAR(COMPROBANTE_CNT.HORA,'HH24:MI'),'HH24:MI')HORA,
               (CASE WHEN COMPROBANTE_CNT.CODIGO_NIIF IS NULL THEN DETALLE_COMPROBANTE_CNT.CUENTA ELSE V_PLAN_CONTABLE.CODIGO_NIIF END) AS COD_NIIF,
                DETALLE_COMPROBANTE_CNT.REFERENCIA,
                DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO,
                DETALLE_COMPROBANTE_CNT.CIERRE,
                DETALLE_COMPROBANTE_CNT.PAGADOBANCO,
                DETALLE_COMPROBANTE_CNT.NUMEROCONTRATO,
                DETALLE_COMPROBANTE_CNT.DEBITO_AFECTADO,
                DETALLE_COMPROBANTE_CNT.CREDITO_AFECTADO,
                DETALLE_COMPROBANTE_CNT.ABONOINICIAL,
                DETALLE_COMPROBANTE_CNT.VALORTOTAL,
                DETALLE_COMPROBANTE_CNT.RECONOCIMIENTO,
                DETALLE_COMPROBANTE_CNT.DEBITOSAFECTADOS_CXP,
                DETALLE_COMPROBANTE_CNT.CREDITOSAFECTADOS_CXP,
                DETALLE_COMPROBANTE_CNT.PORDISTRIBUIDOCNT,
                DETALLE_COMPROBANTE_CNT.PAIS,
                DETALLE_COMPROBANTE_CNT.DEPARTAMENTO,
                DETALLE_COMPROBANTE_CNT.CIUDAD,
                DETALLE_COMPROBANTE_CNT.DEBITO_EQUIV,
                DETALLE_COMPROBANTE_CNT.CREDITO_EQUIV,
                DETALLE_COMPROBANTE_CNT.SINIDENTIFICAR
          FROM  COMPROBANTE_CNT
         INNER JOIN DETALLE_COMPROBANTE_CNT 
                 ON (COMPROBANTE_CNT.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA 
                AND COMPROBANTE_CNT.NUMERO = DETALLE_COMPROBANTE_CNT.COMPROBANTE
                AND COMPROBANTE_CNT.TIPO = DETALLE_COMPROBANTE_CNT.TIPO_CPTE 
                AND COMPROBANTE_CNT.ANO = DETALLE_COMPROBANTE_CNT.ANO)
         LEFT  JOIN V_PLAN_CONTABLE 
                 ON (DETALLE_COMPROBANTE_CNT.CUENTA  = V_PLAN_CONTABLE.ID 
                AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
                AND DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA)
         WHERE    DETALLE_COMPROBANTE_CNT.COMPANIA   =  UN_COMPANIA
           AND      DETALLE_COMPROBANTE_CNT.ANO      =  UN_ANIO
           AND      DETALLE_COMPROBANTE_CNT.TIPO_CPTE BETWEEN  UN_TIPO_INICIAL AND UN_TIPO_FINAL
           AND      TO_NUMBER(TO_CHAR(DETALLE_COMPROBANTE_CNT.FECHA,'MM')) BETWEEN UN_MES_INICIAL AND UN_MES_FINAL
           AND      COMPROBANTE_CNT.NONIIF IN(0)
         ORDER BY DETALLE_COMPROBANTE_CNT.CONSECUTIVO, DETALLE_COMPROBANTE_CNT.TIPO_CPTE, DETALLE_COMPROBANTE_CNT.COMPROBANTE
     )
     LOOP

            MI_STRETAPA := '03-A';

            MI_DESCRIPCION  := NVL(RS_DETALLE.DESCRIPCION,'.');
            MI_NRODOCUMENTO := NVL(RS_DETALLE.NRO_DOCUMENTO,' ');
            MI_DESCRIPCION  := REPLACE(MI_DESCRIPCION,CHR(39),'');
            MI_DESCRIPCION  := REPLACE(MI_DESCRIPCION,'|','');
            MI_NRODOCUMENTO := REPLACE(MI_NRODOCUMENTO, CHR(39), '');
            MI_NRODOCUMENTO := REPLACE(MI_NRODOCUMENTO, '|', ''); 

            MI_RESULTADO  := FC_CREARCUENTANIIF(MI_COMPANIA_NIIF, UN_COMPANIA, UN_ANIO, NULL, NVL(RS_DETALLE.COD_NIIF, ''),UN_USUARIO);

            MI_STRETAPA := '05';  

            BEGIN
            MI_CAMPOS    := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE, CONSECUTIVO,CUENTA, NATURALEZA,
                                CUENTAPPTAL,DESCRIPCION,VALOR_CREDITO,EJECUCION_DEBITO,EJECUCION_CREDITO,BASE_GRAVABLE,
                                TIPO_DOCUMENTO,NRO_DOCUMENTO,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR, TIPO_CPTE_AFECT,
                                CMPTE_AFECTADO,CHEQUEPARAANULAR,CONCEPTO_EX,TIPOPPTAL,BASE_IVA,DESEMBOLSO,SALDOCUENTA,
                                CREATED_BY, MODIFIED_BY, PORCENTAJERETENCION,REVELACIONES,
                                VALOR_DEBITO, REFERENCIA, FUENTE_RECURSO, CIERRE, PAGADOBANCO, NUMEROCONTRATO,
                                DEBITO_AFECTADO, CREDITO_AFECTADO, ABONOINICIAL, VALORTOTAL, RECONOCIMIENTO,
                                DEBITOSAFECTADOS_CXP, CREDITOSAFECTADOS_CXP, PORDISTRIBUIDOCNT, PAIS, DEPARTAMENTO,
                                CIUDAD, DEBITO_EQUIV, CREDITO_EQUIV,SINIDENTIFICAR'; 

            MI_VALORES   := ''''|| MI_COMPANIA_NIIF ||''',                                 '|| UN_ANIO ||',
                             '''||RS_DETALLE.TIPO_CPTE ||''',                              '|| RS_DETALLE.COMPROBANTE ||'  ,
                               '|| RS_DETALLE.CONSECUTIVO ||',                           '''|| RS_DETALLE.COD_NIIF||''',
                             '''|| RS_DETALLE.NATURALEZA ||''',
                             '''||RS_DETALLE.CUENTAPPTAL ||''',                          '''|| MI_DESCRIPCION ||''',
                               '|| TO_NUMBER(NVL(RS_DETALLE.VALOR_CREDITO,0))||',          '|| TO_NUMBER(NVL(RS_DETALLE.EJECUCION_DEBITO,0)) ||',
                               '|| TO_NUMBER(NVL(RS_DETALLE.EJECUCION_CREDITO,0)) ||',     '|| RS_DETALLE.BASE_GRAVABLE  ||',
                             '''||  RS_DETALLE.TIPO_DOCUMENTO ||''',                     '''|| MI_NRODOCUMENTO ||''', 
                             '''|| RS_DETALLE.CENTRO_COSTO ||''',                        '''|| NVL(RS_DETALLE.TERCERO,PCK_DATOS.CONS_TERCERO)||''',
                             '''|| NVL(RS_DETALLE.SUCURSAL,PCK_DATOS.CONS_SUCURSAL)||''','''|| RS_DETALLE.AUXILIAR ||''',
                             '''|| RS_DETALLE.TIPO_CPTE_AFECT ||''',                       '|| RS_DETALLE.CMPTE_AFECTADO ||',
                             '''|| RS_DETALLE.CHEQUEPARAANULAR ||''',                    '''|| RS_DETALLE.CONCEPTO_EX ||''',
                             '''|| RS_DETALLE.TIPOPPTAL ||''',                             '|| RS_DETALLE.BASE_IVA ||',
                               '|| TO_NUMBER(NVL(RS_DETALLE.DESEMBOLSO,0)) ||',            '|| TO_NUMBER(NVL(RS_DETALLE.SALDOCUENTA,0)) ||',
                               '''|| RS_DETALLE.CREADOR  ||''',
                             '''|| RS_DETALLE.MODIFICADOR  ||''',                          '|| TO_NUMBER(NVL(RS_DETALLE.PORCENTAJERETENCION,0)) ||',                               
                               '|| NVL(RS_DETALLE.REVELACIONES,''' ''')||',                '|| RS_DETALLE.VALOR_DEBITO ||',
                             '''|| RS_DETALLE.REFERENCIA  ||''',                         '''|| RS_DETALLE.FUENTE_RECURSO ||''',
                               '|| RS_DETALLE.CIERRE ||',                                  '|| RS_DETALLE.PAGADOBANCO ||',
                               '|| RS_DETALLE.NUMEROCONTRATO ||',                          '|| RS_DETALLE.DEBITO_AFECTADO ||',
                               '|| RS_DETALLE.CREDITO_AFECTADO ||',                        '|| RS_DETALLE.ABONOINICIAL ||',
                               '|| RS_DETALLE.VALORTOTAL ||',                              '|| RS_DETALLE.RECONOCIMIENTO  ||',
                               '|| RS_DETALLE.DEBITOSAFECTADOS_CXP||',                     '|| RS_DETALLE.CREDITOSAFECTADOS_CXP ||',
                               '|| RS_DETALLE.PORDISTRIBUIDOCNT ||',                     '''|| RS_DETALLE.PAIS ||''',
                             '''|| RS_DETALLE.DEPARTAMENTO ||''',                        '''|| RS_DETALLE.CIUDAD ||''',
                               '|| RS_DETALLE.DEBITO_EQUIV ||',                            '|| RS_DETALLE.CREDITO_EQUIV ||',
                               '|| RS_DETALLE.SINIDENTIFICAR||''; 

            MI_RESULTADO := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                              UN_ACCION  => 'I', 
                                              UN_CAMPOS  => MI_CAMPOS, 
                                              UN_VALORES => MI_VALORES);


      END;
     END LOOP CONTABILIZA;

     MI_STRETAPA:= '06'; 
     --Pendiente

     MI_STRETAPA:= '09'; 

     BEGIN
     MI_CAMPOS := 'COMPANIA, ANO, TIPO, NUMERO, TIPORETENCION, CODIGORETENCION, VALOR, VALORBASE';

     MI_VALORES := 'SELECT '||MI_COMPANIA_NIIF||' AS COMPANIA, 
                            COMPROBANTE_CNTRETENCION.ANO, 
                            COMPROBANTE_CNT.TIPO, 
                            COMPROBANTE_CNT.NUMERO, 
                            COMPROBANTE_CNTRETENCION.TIPORETENCION,
                            COMPROBANTE_CNTRETENCION.CODIGORETENCION,
                            COMPROBANTE_CNTRETENCION.VALOR, 
                            COMPROBANTE_CNTRETENCION.VALORBASE
                      FROM COMPROBANTE_CNT 
                    LEFT JOIN COMPROBANTE_CNTRETENCION 
                           ON COMPROBANTE_CNT.COMPANIA = COMPROBANTE_CNTRETENCION.COMPANIA
                          AND COMPROBANTE_CNT.ANO = COMPROBANTE_CNTRETENCION.ANO 
                          AND COMPROBANTE_CNT.TIPO = COMPROBANTE_CNTRETENCION.TIPO
                          AND COMPROBANTE_CNT.NUMERO = COMPROBANTE_CNTRETENCION.NUMERO
                     WHERE COMPROBANTE_CNTRETENCION.COMPANIA= '''||UN_COMPANIA||'''
                       AND COMPROBANTE_CNTRETENCION.ANO = '||UN_ANIO||'  
                       AND COMPROBANTE_CNTRETENCION.TIPO BETWEEN '''||UN_TIPO_INICIAL||''' AND '''||UN_TIPO_FINAL||''' 
                       AND TO_CHAR(FECHA, ''MM'') BETWEEN '||UN_MES_INICIAL||' AND '||UN_MES_FINAL||'
                       AND COMPROBANTE_CNT.NONIIF IN(0)'; 

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME('COMPROBANTE_CNTRETENCION', 'IS', MI_CAMPOS, MI_VALORES, NULL, NULL);    
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;

    RETURN MI_RESPUESTA;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ERRR_CONTABILIDAD_NIIFLOTES,
        UN_TABLAERROR =>'COMPROBANTE_CNT'
      );  
  END FC_NIIF_LOTES;

  FUNCTION FC_PREDECESOR
      /*
        NAME              : FC_PREDECESOR
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA
        DATE MIGRADOR     : 12/05/2016
        TIME              : 9:03 AM
        SOURCE MODULE     : Contabilidad
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : TRAE EL PREDECESOR DE UNA CUENTA
        @NAME:  consultarPredecesorCuentaContable
        @METHOD:  GET
      */
     (
        UN_COMPANIA		    IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
        UN_CODIGO         IN PCK_SUBTIPOS.TI_CODIGOCONTA
      )
      RETURN VARCHAR2
        AS
        MI_ERROR_FUN      PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 4;
        MI_PREDECESOR     PCK_SUBTIPOS.TI_CODIGOCONTA;

      BEGIN

      IF LENGTH(UN_CODIGO) = 1 THEN
        MI_PREDECESOR := UN_CODIGO;

      ELSE
        SELECT MAX(V_PLAN_CONTABLE.ID) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) AS PRED
            INTO MI_PREDECESOR
               FROM V_PLAN_CONTABLE
              WHERE V_PLAN_CONTABLE.COMPANIA     = UN_COMPANIA
                AND V_PLAN_CONTABLE.ANO          = UN_ANIO
                AND LENGTH(V_PLAN_CONTABLE.ID)   < LENGTH(UN_CODIGO)
                AND V_PLAN_CONTABLE.ID = SUBSTR(UN_CODIGO,1,LENGTH(V_PLAN_CONTABLE.ID));
      END IF;

      RETURN MI_PREDECESOR;

      EXCEPTION WHEN OTHERS THEN
        PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante function PREDECESOR';
        PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD','',SQLERRM );
        RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
    END FC_PREDECESOR;


  FUNCTION FC_CREARCUENTANIIF
  /*
      NAME              : FC_CREARCUENTANIIF
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ADRIANA MARITZA CÁCERES BONILLA
      DATE MIGRADOR     : 05/09/2016
      TIME              : 15:23 PM
      SOURCE MODULE     : SysmanCT2016.02.06
      MODIFIER          : AURA LILIANA MONROY GARCÍA
      DATE MODIFIED     : 22/11/2016 - 18/05/2017
      TIME              : 17:40 PM
      DESCRIPTION       : Función para crear la cuenta mayor en el plan niif de acuerdo a la cuenta del plan contable base.
      MODIFICATIONS     : 1) Se adiciona el parámetro UN_ANO_DESTINO que define el año con el que se crea la nueva cuenta, además
                          se incluyen más campos para realizar la inserción
                          2) Se adicionan los campos de auditoría al realizar la inserción                          
      @NAME:  insertarCuentaContableEnNiif
      @METHOD:  GET
    */
  (
    UN_COMPANIANIIF	IN PCK_SUBTIPOS.TI_COMPANIA,  
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO_FUENTE   IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANO_DESTINO  IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO       IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_USUARIO 		  IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN NUMBER
    AS
    MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
    MI_CREARCUENTANIIF  PCK_SUBTIPOS.TI_LOGICO;
    MI_EXISTE           PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_ERROR_FUN        PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 4;

    BEGIN
      BEGIN
      -- Verifica la Existencia de cuentas
      SELECT COUNT(*) EXISTE
        INTO MI_EXISTE
        FROM   V_PLAN_CONTABLE
        WHERE  COMPANIA = UN_COMPANIANIIF
        AND    ANO      = UN_ANO_FUENTE
        AND    V_PLAN_CONTABLE.ID= UN_CODIGO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE := 0;
      MI_CREARCUENTANIIF := 0;
      END;

    IF MI_EXISTE > 0 THEN
     RETURN 0;
    ELSE
     BEGIN

    MI_CAMPOS := 'COMPANIA,ANO, CODIGO, NOMBRE, NATURALEZA, MOVIMIENTO, MAN_CEN_CTO, MAN_AUX_TER, MAN_AUX_GEN, 
                 DINAMICA, CUENTA_PPTAL, PRESUPUESTO_ANUAL, CORRIENTE, FORMATO, CLASECUENTA,
                 SALDOINICIAL, SALDO0,SALDO1,SALDO2,SALDO3,SALDO4,SALDO5,SALDO6,SALDO7,SALDO8,SALDO9,SALDO10,SALDO11,SALDO12,SALDO13,
                 NETO0,NETO1,NETO2,NETO3,NETO4,NETO5,NETO6,NETO7,NETO8,NETO9,NETO10,NETO11,NETO12,NETO13,
                 DEBITO0,DEBITO1,DEBITO2,DEBITO3,DEBITO4,DEBITO5,DEBITO6,DEBITO7,DEBITO8,DEBITO9,DEBITO10,DEBITO11,DEBITO12,DEBITO13,
                 CREDITO0,CREDITO1,CREDITO2,CREDITO3,CREDITO4,CREDITO5,CREDITO6,CREDITO7,CREDITO8,CREDITO9,CREDITO10,CREDITO11,CREDITO12,CREDITO13,
                 AJUSTE0,AJUSTE1,AJUSTE2,AJUSTE3,AJUSTE4,AJUSTE5,AJUSTE6,AJUSTE7,AJUSTE8,AJUSTE9,AJUSTE10,AJUSTE11,AJUSTE12,AJUSTE13,    
                 PERMITECONSOLIDAR, GENERADESEMBOLSO, CHEQUE, PORCRETENCION, CREDITOEXTERNO, PASARSALDO, COD_EQUIV, 
                 FORMATOEGRESO, BANCO, OBLIGA_TERCERO, OBLIGA_CENTRO, OBLIGA_AUXILIAR, NOTRANSACCIONAL5544, CONCEPTOEX, TERCEROEX, SUCURSALEX,
                 NOREPORTARRECIPROCAS, REPORTASALDORECIPROCAS, TERCEROEQUIVALENTERECIPROCAS, MAN_FACT_ARRENDAMIENTO, CUENTA_BANCARIA, 
                 ESOFICIAL, EQUIVPR_DEBITO, EQUIVPR_CREDITO, DESTINO,
                 FUENTE, CODBANCO_SIA, NUMEROCUENTA_SIA, CODBANCO_SEREC, NUMEROCUENTA_SEREC, DESTINOCUENTABANCO, TIPODESCUENTO_SIA,
                 IVAEX,RETEPRACTICADA, RETEASUMIDA, IVACOMUN, IVASIMPLIFICADO, ID_NIIF, CODIGO_NIIF, CCBALANCE, MAN_DISTRI_CCOSTO, COD_FLUJOCAJA,
                 RETEICA, EXDISTRITAL, CREE_PRACTICADA, CREE_ASUMIDA, MEN, DEB_CAUS_DET, CRE_CAUS_DET, DEB_REC_DET, CRE_REC_DET, DEB_RECO_DET, 
                 CRE_RECO_DET, APLICA_DETERIORO, OBLIGA_REFERENCIA, OBLIGA_FUENTE, BLOQUEACUENTA, VERIFICAR_MOV, CREATED_BY, DATE_CREATED';

    MI_VALORES:= 'SELECT '''||UN_COMPANIANIIF||''' COMPANIA, CASE WHEN '||UN_ANO_DESTINO||' IS NOT NULL THEN '||UN_ANO_DESTINO||' ELSE ANO END ANO,
                  CODIGO, NOMBRE, NATURALEZA, MOVIMIENTO, MAN_CEN_CTO, MAN_AUX_TER, MAN_AUX_GEN, DINAMICA, CUENTA_PPTAL,PRESUPUESTO_ANUAL, CORRIENTE,
                  FORMATO, CLASECUENTA, SALDOINICIAL, SALDO0,SALDO1,SALDO2,SALDO3,SALDO4,SALDO5,SALDO6,SALDO7,SALDO8,SALDO9,SALDO10,SALDO11,SALDO12,SALDO13,
                  NETO0,NETO1,NETO2,NETO3,NETO4,NETO5,NETO6,NETO7,NETO8,NETO9,NETO10,NETO11,NETO12,NETO13,
                  DEBITO0,DEBITO1,DEBITO2,DEBITO3,DEBITO4,DEBITO5,DEBITO6,DEBITO7,DEBITO8,DEBITO9,DEBITO10,DEBITO11,DEBITO12,DEBITO13,
                  CREDITO0,CREDITO1,CREDITO2,CREDITO3,CREDITO4,CREDITO5,CREDITO6,CREDITO7,CREDITO8,CREDITO9,CREDITO10,CREDITO11,CREDITO12,CREDITO13,
                  AJUSTE0,AJUSTE1,AJUSTE2,AJUSTE3,AJUSTE4,AJUSTE5,AJUSTE6,AJUSTE7,AJUSTE8,AJUSTE9,AJUSTE10,AJUSTE11,AJUSTE12,AJUSTE13,    
                  PERMITECONSOLIDAR, GENERADESEMBOLSO, CHEQUE, PORCRETENCION, CREDITOEXTERNO, PASARSALDO, COD_EQUIV, 
                  FORMATOEGRESO, BANCO, OBLIGA_TERCERO, OBLIGA_CENTRO, OBLIGA_AUXILIAR, NOTRANSACCIONAL5544, CONCEPTOEX, TERCEROEX, SUCURSALEX,
                  NOREPORTARRECIPROCAS, REPORTASALDORECIPROCAS, TERCEROEQUIVALENTERECIPROCAS, MAN_FACT_ARRENDAMIENTO, CUENTA_BANCARIA, 
                  ESOFICIAL, EQUIVPR_DEBITO, EQUIVPR_CREDITO, DESTINO,
                  FUENTE, CODBANCO_SIA, NUMEROCUENTA_SIA, CODBANCO_SEREC, NUMEROCUENTA_SEREC, DESTINOCUENTABANCO, TIPODESCUENTO_SIA,
                  IVAEX,RETEPRACTICADA, RETEASUMIDA, IVACOMUN, IVASIMPLIFICADO, ID_NIIF, CODIGO_NIIF, CCBALANCE, MAN_DISTRI_CCOSTO, COD_FLUJOCAJA,
                  RETEICA, EXDISTRITAL, CREE_PRACTICADA, CREE_ASUMIDA, MEN, DEB_CAUS_DET, CRE_CAUS_DET, DEB_REC_DET, CRE_REC_DET, DEB_RECO_DET, 
                  CRE_RECO_DET, APLICA_DETERIORO, OBLIGA_REFERENCIA, OBLIGA_FUENTE, BLOQUEACUENTA, VERIFICAR_MOV, ''' || UN_USUARIO || ''', SYSDATE
                  FROM  PLAN_CONTABLE
                  WHERE COMPANIA              = '''||UN_COMPANIA||'''
                    AND ANO                   = '||UN_ANO_FUENTE||'';

      IF UN_CODIGO IS NOT NULL 
          THEN MI_VALORES := MI_VALORES || ' AND PLAN_CONTABLE.CODIGO  = '''||UN_CODIGO||'''';
      END IF;

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME('PLAN_CONTABLE', 'IS', MI_CAMPOS, MI_VALORES, NULL, NULL);

    MI_CREARCUENTANIIF:=-1;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
       RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END; 

    END IF;
    RETURN MI_CREARCUENTANIIF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    =>SQLCODE,
        UN_ERROR_COD  =>PCK_ERRORES.ERRR_CONTABILIDAD_CUENTANIIF,
        UN_TABLAERROR =>'PLAN_CONTABLE'
      );  

    END FC_CREARCUENTANIIF;

    PROCEDURE PR_EQUIVALENTENIIIF
    /*
    NAME              : PR_EQUIVALENTENIIIF
    AUTHORS           : SYSMAN  
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 24/11/2016
    TIME              : 09:07 AM
    SOURCE MODULE     : SYSMANMGC2016.09.01
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : Copia la configuración NIIF a cuentas equivalentes de un año fuente a un año destino
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA    => Compañia de ingreso a la aplicación
                        UN_ANO_FUENTE  => Año en el que se verifican las cuentas con NIIF
                        UN_ANO_DESTINO => Año en el que se actualizan los valores NIIF 
    @NAME:  CopiarConfiguracionEquivalenteNIIF
    @METHOD:  POST     
    */ 
    (
      UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA, 
      UN_ANO_FUENTE   IN PCK_SUBTIPOS.TI_ANIO,
      UN_ANO_DESTINO  IN PCK_SUBTIPOS.TI_ANIO
    )
    AS 
      MI_TABLA        PCK_SUBTIPOS.TI_TABLA; 
      MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;  
      MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
    BEGIN
      -- Actualización de cuentas en el año destino que ya han sido creadas en el año  fuente
      BEGIN
        MI_TABLA 		    := 'PLAN_CONTABLE';
        MI_MERGEUSING 	:= 'SELECT P1.COMPANIA,
                              P1.ANO,
                              P1.CODIGO,
                              P1.CODIGO_NIIF,
                              P.CODIGO_NIIF NIIFDESTINO 
                            FROM PLAN_CONTABLE P
                            INNER JOIN (SELECT PLAN_CONTABLE.COMPANIA,
                                          PLAN_CONTABLE.ANO,
                                          PLAN_CONTABLE.CODIGO,
                                          PLAN_CONTABLE.CODIGO_NIIF
                                        FROM PLAN_CONTABLE
                                        WHERE COMPANIA = '''||UN_COMPANIA||'''
                                        AND ANO        = '||UN_ANO_DESTINO||')P1
                               ON P.COMPANIA = P1.COMPANIA
                              AND P.CODIGO   = P1.CODIGO
                            WHERE P.COMPANIA = '''||UN_COMPANIA||''' 
                              AND P.ANO      = '||UN_ANO_FUENTE||'
                              AND P.CODIGO_NIIF IS NOT NULL
                            ORDER BY P.CODIGO';
        MI_MERGEENLACE 	:= '    TABLA.COMPANIA = VISTA.COMPANIA
                            AND TABLA.ANO      = VISTA.ANO
                            AND TABLA.CODIGO   = VISTA.CODIGO';
        MI_MERGEEXISTE := ' UPDATE SET TABLA.CODIGO_NIIF = VISTA.NIIFDESTINO'; 
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, UN_ACCION => 'MM' , UN_MERGEUSING => MI_MERGEUSING, UN_MERGEENLACE => MI_MERGEENLACE , UN_MERGEEXISTE => MI_MERGEEXISTE);                  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                        THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;	

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   =>SQLCODE,
            UN_ERROR_COD =>PCK_ERRORES.ERRR_CONTABILIDAD_COPIANIIF
          );
    END PR_EQUIVALENTENIIIF;

    PROCEDURE PR_SUBIRSALDOSNIIF
    /*
      NAME              : PROCEDURE PR_SUBIRSALDOSNIIF
      AUTHORS           : SYSMAN SAS
      AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
      DATE MIGRATION    : 20/12/2016
      TIME              : 08:49 AM
      SOURCE MODULE     : SYSMANMGC2016.09.01
      PARAMETERS        : UN_COMPANIA     => Codigo de la compania con la que se inicio sesion.      
                          UN_COMPANIANIIF => Codigo de la compania equivalente NIIF.
                          UN_ANO_FUENTE   => Anio con los saldos iniciales fuente.
                          UN_ANO_DESTINO  => Anio al cuial se van a pasar los saldos iniciales.
                          UN_CLAVE        => 0 : Solo configurar plan contable.
                                            -1 : Configurar plan contable e insertar al anio destino.
                          UN_USUARIO      => Codigo del usuario que inicio sesion.

      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Procedimiento que sube los saldos iniciales de un anio origen a un anio destino.
      MODIFICATIONS     : 

      @NAME:  subirSaldosInicialesNiif
      @METHOD:  GET
    */
    (
      UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_COMPANIANIIF IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO_FUENTE  	IN PCK_SUBTIPOS.TI_ANIO,
      UN_ANO_DESTINO  IN PCK_SUBTIPOS.TI_ANIO,
      UN_CLAVE        IN PCK_SUBTIPOS.TI_LOGICO, /*1: */
      UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
    )
    AS
      MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
      MI_TABLA        PCK_SUBTIPOS.TI_TABLA; 
      MI_TABLA_P      PCK_SUBTIPOS.TI_TABLA; /*PLAN_CONTABLE*/
      MI_TABLA_S      PCK_SUBTIPOS.TI_TABLA; /*SALDOSINICIALES*/
      MI_CAMPOS    	  PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA          PCK_SUBTIPOS.TI_RTA_ACME;
      MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      MI_TABLA_P := 'PLAN_CONTABLE';
      MI_TABLA_S := 'SALDOSINICIALES';

      --ACTUALIZAR LAS CUENTAS PADRE MAL CONFIGURADAS
      BEGIN
        MI_CAMPOS := 'CODIGO_NIIF   = NULL
                     ,DATE_MODIFIED = SYSDATE
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''';

        MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA  ||''' 
                         AND ANO  =   '||UN_ANO_FUENTE||' 
                         AND CODIGO_NIIF IS NOT NULL 
                         AND (MAN_CEN_CTO + MOVIMIENTO + MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_FUE + MAN_AUX_REF) IN(0)';

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_P
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                               
      END;                                   

      --ACTUALIZAR EL CODIGO_NIIF DE LOS HIJOS MAL CONFIGURADOS
      BEGIN                                                            
        MI_TABLA := 'PLAN_CONTABLE P';

        MI_CONDICION := 'P.COMPANIA = '''||UN_COMPANIA  ||''' 
                     AND P.ANO      =   '||UN_ANO_FUENTE||'
                     AND P.CODIGO NOT IN (SELECT ID 
                                          FROM V_PLAN_CONTABLE V
                                          WHERE V.COMPANIA = P.COMPANIA
                                            AND V.ANO      = P.ANO
                                            AND V.CODIGO   = P.CODIGO)
                     AND P.CODIGO_NIIF IS NOT NULL 
                     AND (P.MAN_CEN_CTO+P.MOVIMIENTO+P.MAN_AUX_TER+P.MAN_AUX_GEN+P.MAN_AUX_FUE+P.MAN_AUX_REF) <> 0';

        MI_CAMPOS := ' P.CODIGO_NIIF = NULL';

        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                                                 
      END;

      --PROCESO DE INSERCION 
      IF UN_CLAVE NOT IN(0) THEN 
      --@pespitia (03/11/2017): La configuracion del plan contable no es parte de este proceso.
        --Copiar indicadores de la compania a la compania niif
      /*--PCK_PREPARAR_ANO.PR_COPIAR_TERCERO       (UN_COMPANIA,                                UN_COMPANIANIIF);
        PCK_PREPARAR_ANO.PR_COPIAR_CENTRO_COSTO  (UN_COMPANIA, UN_ANO_DESTINO, UN_ANO_FUENTE, UN_COMPANIANIIF);
        PCK_PREPARAR_ANO.PR_COPIAR_AUXILIAR      (UN_COMPANIA, UN_ANO_DESTINO, UN_ANO_FUENTE, UN_COMPANIANIIF);
        PCK_PREPARAR_ANO.PR_COPIAR_FUENTE_RECURSO(UN_COMPANIA, UN_ANO_DESTINO, UN_ANO_FUENTE, UN_COMPANIANIIF);
        PCK_PREPARAR_ANO.PR_COPIAR_REFERENCIA    (UN_COMPANIA, UN_ANO_DESTINO, UN_ANO_FUENTE, UN_COMPANIANIIF); */

        --Remover saldos iniciales con ANIODESTINO y COMPANIAEQUIVALENTENIIF de SALDOSINICIALES
        BEGIN
          MI_CONDICION := 'COMPANIA = '''||UN_COMPANIANIIF||''' 
                       AND ANO      =   '||UN_ANO_DESTINO;

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_S
                                     ,UN_ACCION    => 'E'
                                     ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                               
        END;   

        --Insertar en SALDOSINICIALES   
        BEGIN
/*          MI_CAMPOS := 'COMPANIA, 
                        ANO, 
                        CODIGO, 
                        CENTRO_COSTO, 
                        TERCERO, 
                        SUCURSAL, 
                        AUXILIAR, 
                        REFERENCIA, 
                        FUENTE_RECURSO, 
                        DEBITO, 
                        CREDITO, 
                        SALDOINICIAL';*/

          MI_CAMPOS := 'COMPANIA
                       ,ANO
                       ,CODIGO
                       ,CENTRO_COSTO
                       ,TERCERO
                       ,SUCURSAL
                       ,AUXILIAR
                       ,REFERENCIA
                       ,FUENTE_RECURSO
                       ,SALDOINICIAL
                       ,DEBITO
                       ,CREDITO
                       ,CREATED_BY
                       ,DATE_CREATED';

          MI_VALORES := 'WITH DETALLE AS (
                           SELECT 
                              DCC.COMPANIA
                             ,DCC.ANO
                             ,DCC.CUENTA
                             ,CASE WHEN PLAN.MAN_CEN_CTO IN(0) THEN ''99999999999999999999'' ELSE DCC.CENTRO_COSTO   END CENTRO_COSTO
                             ,CASE WHEN PLAN.MAN_AUX_TER IN(0) THEN ''999999999999999999  '' ELSE DCC.TERCERO        END TERCERO
                             ,CASE WHEN PLAN.MAN_AUX_TER IN(0) THEN ''999                 '' ELSE DCC.SUCURSAL       END SUCURSAL
                             ,CASE WHEN PLAN.MAN_AUX_GEN IN(0) THEN ''99999999999999999999'' ELSE DCC.AUXILIAR       END AUXILIAR
                             ,CASE WHEN PLAN.MAN_AUX_REF IN(0) THEN ''99999999999999999999'' ELSE DCC.REFERENCIA     END REFERENCIA
                             ,CASE WHEN PLAN.MAN_AUX_FUE IN(0) THEN ''99999999999999999999'' ELSE DCC.FUENTE_RECURSO END FUENTE_RECURSO
                             ,SUM(DCC.VALOR_DEBITO ) VALOR_DEBITO
                             ,SUM(DCC.VALOR_CREDITO) VALOR_CREDITO
                           FROM PLAN_CONTABLE PLAN 
                             INNER JOIN DETALLE_COMPROBANTE_CNT DCC 
                                ON PLAN.COMPANIA = DCC.COMPANIA 
                               AND PLAN.ANO      = DCC.ANO
                               AND PLAN.CODIGO   = DCC.CUENTA
                             INNER JOIN TIPO_COMPROBANTE TC
                                ON DCC.COMPANIA  = TC.COMPANIA
                               AND DCC.TIPO_CPTE = TC.CODIGO
                           WHERE PLAN.COMPANIA = '''||UN_COMPANIA||'''
                             AND PLAN.ANO      = '||UN_ANO_FUENTE||'
                             AND SUBSTR(PLAN.CODIGO,1,1) IN (1,2,3)
                             AND TC.CLASE_CONTABLE       IN (''Z'') 
                             AND TC.CODIGO NOT           IN (''CIE'')
                           GROUP BY 
                              DCC.COMPANIA
                             ,DCC.ANO
                             ,DCC.CUENTA
                             ,CASE WHEN PLAN.MAN_CEN_CTO IN(0) THEN ''99999999999999999999'' ELSE DCC.CENTRO_COSTO   END 
                             ,CASE WHEN PLAN.MAN_AUX_TER IN(0) THEN ''999999999999999999  '' ELSE DCC.TERCERO        END 
                             ,CASE WHEN PLAN.MAN_AUX_TER IN(0) THEN ''999                 '' ELSE DCC.SUCURSAL       END 
                             ,CASE WHEN PLAN.MAN_AUX_GEN IN(0) THEN ''99999999999999999999'' ELSE DCC.AUXILIAR       END 
                             ,CASE WHEN PLAN.MAN_AUX_REF IN(0) THEN ''99999999999999999999'' ELSE DCC.REFERENCIA     END 
                             ,CASE WHEN PLAN.MAN_AUX_FUE IN(0) THEN ''99999999999999999999'' ELSE DCC.FUENTE_RECURSO END 
                         )
                         SELECT 
                            '''||UN_COMPANIANIIF||''' COMPANIA
                           ,'''||UN_ANO_DESTINO ||''' ANO
                           ,SALDO.CODIGO
                           ,SALDO.CENTRO_COSTO
                           ,SALDO.TERCERO
                           ,SALDO.SUCURSAL
                           ,SALDO.AUXILIAR
                           ,SALDO.REFERENCIA
                           ,SALDO.FUENTE_RECURSO
                           ,SALDO.SALDO13 SALDOINICIAL
                           ,CASE WHEN SALDO.NATURALEZA IN(''D'') AND (SALDO.SALDO13                + 
                                                                      NVL(DETALLE.VALOR_DEBITO ,0) - 
                                                                      NVL(DETALLE.VALOR_CREDITO,0)) > 0 
                                 THEN (SALDO.SALDO13 + NVL(DETALLE.VALOR_DEBITO,0) - NVL(DETALLE.VALOR_CREDITO,0))
                                 ELSE CASE WHEN SALDO.NATURALEZA IN(''C'') AND (SALDO.SALDO13                + 
                                                                                NVL(DETALLE.VALOR_CREDITO,0) -
                                                                                NVL(DETALLE.VALOR_DEBITO ,0)) < 0 
                                           THEN ABS(SALDO.SALDO13 + NVL(DETALLE.VALOR_CREDITO,0) - NVL(DETALLE.VALOR_DEBITO,0))
                                           ELSE 0
                                      END
                                 END DEBITO
                           ,CASE WHEN SALDO.NATURALEZA IN(''C'') AND (SALDO.SALDO13                + 
                                                                      NVL(DETALLE.VALOR_CREDITO,0) - 
                                                                      NVL(DETALLE.VALOR_DEBITO ,0)) > 0 
                                 THEN (SALDO.SALDO13 + NVL(DETALLE.VALOR_CREDITO,0) - NVL(DETALLE.VALOR_DEBITO,0))
                                 ELSE CASE WHEN SALDO.NATURALEZA IN(''D'') AND (SALDO.SALDO13                + 
                                                                                NVL(DETALLE.VALOR_DEBITO ,0) -
                                                                                NVL(DETALLE.VALOR_CREDITO,0)) < 0 
                                           THEN ABS(SALDO.SALDO13 + NVL(DETALLE.VALOR_DEBITO,0) - NVL(DETALLE.VALOR_CREDITO,0))
                                           ELSE 0
                                      END
                            END CREDITO
                           ,'''||UN_USUARIO||'''
                           ,SYSDATE
                          FROM SALDO_AUX_CONTABLE SALDO 
                            LEFT JOIN DETALLE
                              ON SALDO.COMPANIA       = DETALLE.COMPANIA 
                             AND SALDO.ANO            = DETALLE.ANO
                             AND SALDO.CODIGO         = DETALLE.CUENTA
                             AND SALDO.CENTRO_COSTO   = DETALLE.CENTRO_COSTO
                             AND SALDO.TERCERO        = DETALLE.TERCERO
                             AND SALDO.SUCURSAL       = DETALLE.SUCURSAL
                             AND SALDO.AUXILIAR       = DETALLE.AUXILIAR
                             AND SALDO.REFERENCIA     = DETALLE.REFERENCIA
                             AND SALDO.FUENTE_RECURSO = DETALLE.FUENTE_RECURSO
                          WHERE SALDO.COMPANIA = '''||UN_COMPANIA  ||'''
                            AND SALDO.ANO      =   '||UN_ANO_FUENTE||'
                            AND SUBSTR(SALDO.CODIGO,1,1) IN (1,2,3)
                            AND SALDO.SALDO13 + (CASE WHEN SALDO.NATURALEZA IN(''D'')
                                                      THEN NVL(DETALLE.VALOR_DEBITO ,0) - NVL(DETALLE.VALOR_CREDITO,0) 
                                                      ELSE NVL(DETALLE.VALOR_CREDITO,0) - NVL(DETALLE.VALOR_DEBITO ,0) 
                                                 END) <> 0';

          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA_S
                                     ,UN_ACCION  => 'IS'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES);        

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
      END IF;

    	EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        MI_MSGERROR(1).CLAVE := 'ANIOFUENTE';
        MI_MSGERROR(1).VALOR := UN_ANO_FUENTE;
        MI_MSGERROR(2).CLAVE := 'ANIODESTINO';
        MI_MSGERROR(2).VALOR := UN_ANO_DESTINO;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_SALDOSNIIF,
          UN_REEMPLAZOS => MI_MSGERROR);

    END PR_SUBIRSALDOSNIIF; 

    PROCEDURE PR_RECLASIFICARNIIF
    /*
      NAME              : PR_RECLASIFICARNIIF
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 20/12/2016
      TIME              : 
      SOURCE MODULE     : SYSMANMGC2016.09.01
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Se crea el comprobante contable con los tipos de comprobante definidos en los detalles de la Raclasificación Niif.
                          Adicionalmente se realiza la actualización del campo "realizado" en la tabla RECLASIFICACION_NIIF para el número de registro que 
                          ingresa por parámetro.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA    		  => Compañia de ingreso a la aplicación
                          UN_ANO 		   		    => Año del Plan Contable definido en la Reclasificación
                          UN_TIPO 	   		    => Tipo de comprobante
                          UN_NUMEROREGISTRO 	=> Número de registro definido en la Reclasificación
                          UN_FECHA 			      => Fecha en la que se realiza el cambio de cuentas
                          UN_USUARIO 			    => Nombre del usuario que realiza el cambio de las cuentas 

      @NAME:   reclasificarniif
      @METHOD: POST     
    */ 
    (
      UN_COMPANIA       		IN PCK_SUBTIPOS.TI_COMPANIA, 
      UN_ANO 		      		  IN PCK_SUBTIPOS.TI_ANIO,
      UN_TIPO           		IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
      UN_NUMEROREGISTRO 		IN PCK_SUBTIPOS.TI_ENTERO,
      UN_CONSECUTIVO        IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
      UN_FECHA 		  		    IN DATE,
      UN_USUARIO 		  		  IN PCK_SUBTIPOS.TI_USUARIO
    )
    AS 
      MI_DETALLES           PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_RS                 SYS_REFCURSOR;
      MI_RS2                 SYS_REFCURSOR;
      MI_SQLRS              PCK_SUBTIPOS.TI_STRSQL;
      MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
      MI_TIPO               D_RECLASIFICAR_NIIF.TIPO%TYPE;
      MI_CODIGOANTERIOR     D_RECLASIFICAR_NIIF.CODIGOANTERIOR%TYPE;
      MI_NOMBREANTERIOR     D_RECLASIFICAR_NIIF.NOMBREANTERIOR%TYPE;
      MI_CODIGONUEVO        D_RECLASIFICAR_NIIF.CODIGONUEVO%TYPE;
      MI_NOMBRENUEVO        D_RECLASIFICAR_NIIF.NOMBRENUEVO%TYPE;
      MI_NATURALEZA         PCK_SUBTIPOS.TI_NATURALEZACONTA;
      MI_GENCONSECUTIVO     PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_CONSECUTIVO        PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_NATURALEZACUENTA   PCK_SUBTIPOS.TI_NATURALEZACONTA;

      MI_TABLA              PCK_SUBTIPOS.TI_TABLA; 
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
      MI_PCKDATOS           PCK_SUBTIPOS.TI_RTA_ACME; 
      MI_CAMPOSINSERT       VARCHAR2(3000);
      MI_VALORESINSERT      VARCHAR2(3000);
      MI_MES                PCK_SUBTIPOS.TI_MES;
      MI_STRSQL             CLOB;

      MI_COMPANIA            PLAN_CONTABLE.COMPANIA%TYPE;
      MI_ANO                 PLAN_CONTABLE.ANO%TYPE;
      MI_CODIGO              PLAN_CONTABLE.CODIGO%TYPE;
      MI_NOMBRE              PLAN_CONTABLE.NOMBRE%TYPE;
      MI_MOVIMIENTO          PLAN_CONTABLE.MOVIMIENTO%TYPE;
      MI_MAN_CEN_CTO         PLAN_CONTABLE.MAN_CEN_CTO%TYPE;
      MI_MAN_AUX_TER         PLAN_CONTABLE.MAN_AUX_TER%TYPE;
      MI_MAN_AUX_GEN         PLAN_CONTABLE.MAN_AUX_GEN%TYPE;
      MI_TERCERO             COMPROBANTE_CNT.TERCERO%TYPE;
      MI_SUCURSAL            COMPROBANTE_CNT.SUCURSAL%TYPE;
      MI_AUXILIAR            COMPROBANTE_CNT.AUXILIAR%TYPE;
      MI_REFERENCIA_CONTABLE COMPROBANTE_CNT.REFERENCIA%TYPE;
      MI_CENTRO_COSTO        COMPROBANTE_CNT.CENTRO_COSTO%TYPE;
      MI_FUENTE_RECURSO      COMPROBANTE_CNT.FUENTE_RECURSO%TYPE;
      MI_NOMBRETERCERO       TERCERO.NOMBRE%TYPE;
      MI_NOMBREAUXILIAR      AUXILIAR.NOMBRE%TYPE;
      MI_NOMBRECENTROCOSTO   CENTRO_COSTO.NOMBRE%TYPE;
      MI_SALDO0              PLAN_CONTABLE.SALDO0%TYPE;
      MI_DEBITO              PLAN_CONTABLE.SALDO0%TYPE;
      MI_CREDITO             PLAN_CONTABLE.SALDO0%TYPE;


    BEGIN
        MI_MES := EXTRACT (MONTH FROM TO_DATE( UN_FECHA ,'DD/MM/YYYY'));

        -- COMPRUEBA LA EXISTENCIA DE DATOS PARA REALIZAR LA CONFIGURACIÓN
        BEGIN
          BEGIN    
             SELECT COUNT(CODIGOANTERIOR) DETALLES      
              INTO MI_DETALLES 
                FROM D_RECLASIFICAR_NIIF
               WHERE COMPANIA   = UN_COMPANIA
                 AND ANO        = UN_ANO
                 AND TIPO       = UN_TIPO
                 AND NUMERO     = UN_NUMEROREGISTRO
                 AND CONSECUTIVO = UN_CONSECUTIVO;       

            IF MI_DETALLES = 0 
              THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END IF;        
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_RECLASIFICAR);
        END;

        BEGIN
          MI_SQLRS :='SELECT ANO,
                             TIPO,
                             CODIGOANTERIOR,
                             NOMBREANTERIOR,
                             CODIGONUEVO,
                             NOMBRENUEVO
                        FROM D_RECLASIFICAR_NIIF
                       WHERE COMPANIA    = '''||UN_COMPANIA||'''
                         AND ANO         = '||UN_ANO||'
                         AND TIPO        = '''||UN_TIPO||'''
                         AND NUMERO      = '||UN_NUMEROREGISTRO||'
                         AND CONSECUTIVO = '||UN_CONSECUTIVO;
          OPEN MI_RS FOR MI_SQLRS;
              LOOP
                FETCH MI_RS INTO MI_ANIO, MI_TIPO, MI_CODIGOANTERIOR, MI_NOMBREANTERIOR,MI_CODIGONUEVO, MI_NOMBRENUEVO;
                EXIT WHEN MI_RS%NOTFOUND;    
                  FOR RS1 IN(
                                  SELECT PLAN_CONTABLE.COMPANIA DATOS 
                                  FROM V_PLAN_CONTABLE PLAN_CONTABLE 
                                    LEFT JOIN TERCERO 
                                      ON  PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA
                                      AND PLAN_CONTABLE.TERCERO = TERCERO.NIT
                                      AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL 
                                    LEFT JOIN AUXILIAR 
                                      ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA
                                      AND PLAN_CONTABLE.ANO = AUXILIAR.ANO
                                      AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO
                                    LEFT JOIN CENTRO_COSTO 
                                      ON  PLAN_CONTABLE.COMPANIA = CENTRO_COSTO.COMPANIA 
                                      AND PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO
                                      AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO
                                  WHERE  PLAN_CONTABLE.COMPANIA    = UN_COMPANIA 
                                    AND  PLAN_CONTABLE.ANO         = UN_ANO
                                    AND  PLAN_CONTABLE.CODIGO      = MI_CODIGOANTERIOR
                                    AND  PLAN_CONTABLE.MOVIMIENTO  NOT IN(0)
                                    AND  PLAN_CONTABLE.SALDO0      <>0
                                  GROUP BY PLAN_CONTABLE.COMPANIA
                  ) 
                  LOOP      
                      -- DATOS PARA CREACIÓN DEL COMPROBANTE
                      MI_GENCONSECUTIVO   := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'COMPROBANTE_CNT', 
                                                                              UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' AND ANO = '||MI_ANIO||' AND TIPO = '''||MI_TIPO||'''', 
                                                                              UN_CAMPO    => 'NUMERO', UN_INICIAL => '1');
                      MI_NATURALEZACUENTA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA    => MI_CODIGONUEVO);

                      -- INSERCIÓN DEL COMPROBANTE    
                      --<TICKET:7725796 RAMA:7725796_CONTABILIDAD  FECHA:30/01/2023 AUTOR:CP >--Se realiza ajuste  para que el tercero y la sucursal cuando la envían  en varios este tenga en cuenta la constante y no quede quemada ya que estaba con menos 999*  
                      MI_TABLA    := 'COMPROBANTE_CNT';
                      MI_CAMPOS   := 'COMPANIA,ANO,TIPO,NUMERO,DESCRIPCION,FECHA,TERCERO,SUCURSAL,DEBITO,CREDITO,ABONADO,ANULADO,CREATED_BY,DATE_CREATED';
                      MI_VALORES  := ''''||UN_COMPANIA||''', '||MI_ANIO||', '''||MI_TIPO||''', '||MI_GENCONSECUTIVO||', ''COMPROBANTE DE RECLASIFICACIÓN'', TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''), ''' || PCK_DATOS.FC_CONS_TERCERO || ''' , ''' || PCK_DATOS.FC_CONS_SUCURSAL || ''' ,0,0,0,0,'''||UN_USUARIO||''', TO_CHAR(SYSDATE,''DD/MM/YYYY'')';
                      MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                       UN_ACCION  => 'I', 
                                                       UN_CAMPOS  => MI_CAMPOS, 
                                                       UN_VALORES => MI_VALORES);     
                      MI_CONSECUTIVO:=0;

                      MI_STRSQL :=  '  SELECT PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, PLAN_CONTABLE.CODIGO, PLAN_CONTABLE.NATURALEZA, PLAN_CONTABLE.NOMBRE ' ||
                                    '       , PLAN_CONTABLE.MOVIMIENTO, PLAN_CONTABLE.MAN_CEN_CTO, PLAN_CONTABLE.MAN_AUX_TER, PLAN_CONTABLE.MAN_AUX_GEN       ' ||
                                    '       , PLAN_CONTABLE.TERCERO, PLAN_CONTABLE.SUCURSAL, PLAN_CONTABLE.AUXILIAR,PLAN_CONTABLE.REFERENCIA REFERENCIA_CONTABLE ' ||
                                    '       , PLAN_CONTABLE.CENTRO_COSTO,PLAN_CONTABLE.FUENTE_RECURSO, TERCERO.NOMBRE AS NOMBRETERCERO, AUXILIAR.NOMBRE AS NOMBREAUXILIAR  ' ||
                                    '       , CENTRO_COSTO.NOMBRE AS NOMBRECENTROCOSTO, PLAN_CONTABLE.SALDO'|| MI_MES ||' SALDO0, ' ||
                                    '    CASE WHEN PLAN_CONTABLE.NATURALEZA   = ''D'' AND SALDO'|| MI_MES ||' < 0  ' ||
                                    '        THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '        ELSE CASE WHEN PLAN_CONTABLE.NATURALEZA = ''C'' AND SALDO'|| MI_MES ||' > 0  ' ||
                                    '                  THEN ABS(SALDO'|| MI_MES ||')  ' ||
                                    '                  ELSE 0 ' ||
                                    '              END ' ||
                                    '    END  AS DEBITO, ' ||
                                    '    CASE WHEN PLAN_CONTABLE.NATURALEZA = ''D'' AND  SALDO'|| MI_MES ||' > 0   ' ||
                                    '        THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '        ELSE CASE WHEN PLAN_CONTABLE.NATURALEZA = ''C'' AND  SALDO'|| MI_MES ||'<0 ' ||
                                    '                  THEN ABS(SALDO'|| MI_MES ||')  ' ||
                                    '                  ELSE 0 ' ||
                                    '        END ' ||
                                    '    END  AS CREDITO ' ||
                                    '  FROM V_PLAN_CONTABLE PLAN_CONTABLE  ' ||
                                    '    LEFT JOIN TERCERO   ' ||
                                    '     ON   PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA ' ||
                                    '     AND PLAN_CONTABLE.TERCERO = TERCERO.NIT ' ||
                                    '     AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL ' ||
                                    '    LEFT JOIN AUXILIAR  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA ' ||
                                    '      AND PLAN_CONTABLE.ANO = AUXILIAR.ANO  ' ||
                                    '      AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO  ' ||
                                    '    LEFT JOIN CENTRO_COSTO  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = CENTRO_COSTO.COMPANIA  ' ||
                                    '      AND  PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO  ' ||
                                    '      AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO ' ||
                                    '  WHERE  PLAN_CONTABLE.COMPANIA    = ''' || UN_COMPANIA || '''  ' ||
                                    '    AND  PLAN_CONTABLE.ANO         = ' || UN_ANO || 
                                    '    AND  PLAN_CONTABLE.CODIGO      = ''' || MI_CODIGOANTERIOR  || '''  ' ||
                                    '    AND  PLAN_CONTABLE.MOVIMIENTO  NOT IN(0) ' ||
                                    '    AND  PLAN_CONTABLE.SALDO'|| MI_MES ||'      <>0 ' ||
                                    '  UNION  ' ||
                                    '  SELECT PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, ''' || MI_CODIGONUEVO     ||'''  CODIGO, ''' || MI_NATURALEZACUENTA     ||''' AS NATURALEZA, PLAN_CONTABLE.NOMBRE ' ||
                                    '       , PLAN_CONTABLE.MOVIMIENTO, PLAN_CONTABLE.MAN_CEN_CTO, PLAN_CONTABLE.MAN_AUX_TER, PLAN_CONTABLE.MAN_AUX_GEN, PLAN_CONTABLE.TERCERO ' ||
                                    '       , PLAN_CONTABLE.SUCURSAL, PLAN_CONTABLE.AUXILIAR, PLAN_CONTABLE.REFERENCIA REFERENCIA_CONTABLE, PLAN_CONTABLE.CENTRO_COSTO ' ||
                                    '       , PLAN_CONTABLE.FUENTE_RECURSO, TERCERO.NOMBRE AS NOMBRETERCERO, AUXILIAR.NOMBRE AS NOMBREAUXILIAR, CENTRO_COSTO.NOMBRE AS NOMBRECENTROCOSTO ' ||
                                    '       , PLAN_CONTABLE.SALDO'|| MI_MES ||' SALDO0,  ' ||
                                    '         CASE WHEN PLAN_CONTABLE.NATURALEZA =''D'' AND  SALDO'|| MI_MES ||'>0  ' ||
                                    '              THEN ABS(SALDO'|| MI_MES ||')  ' ||
                                    '              ELSE  CASE WHEN PLAN_CONTABLE.NATURALEZA=''C'' AND SALDO'|| MI_MES ||'<0 ' ||
                                    '                    THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '                    ELSE 0 ' ||
                                    '              END ' ||
                                    '         END  AS DEBITO,  ' ||
                                    '         CASE WHEN PLAN_CONTABLE.NATURALEZA =''D'' AND SALDO'|| MI_MES ||' < 0 ' ||
                                    '              THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '              ELSE CASE WHEN PLAN_CONTABLE.NATURALEZA =''C'' AND SALDO'|| MI_MES ||'>0 ' ||
                                    '                        THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '                        ELSE 0 ' ||
                                    '                   END  ' ||
                                    '         END AS CREDITO  ' ||
                                    '  FROM V_PLAN_CONTABLE PLAN_CONTABLE  ' ||
                                    '    LEFT JOIN TERCERO  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA ' ||
                                    '      AND PLAN_CONTABLE.TERCERO = TERCERO.NIT ' ||
                                    '      AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL  ' ||
                                    '    LEFT JOIN AUXILIAR  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA ' ||
                                    '      AND  PLAN_CONTABLE.ANO = AUXILIAR.ANO ' ||
                                    '      AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO ' ||
                                    '    LEFT JOIN CENTRO_COSTO  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = CENTRO_COSTO.COMPANIA  ' ||
                                    '      AND  PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO  ' ||
                                    '      AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO ' ||
                                    '  WHERE PLAN_CONTABLE.COMPANIA   = ''' || UN_COMPANIA || '''  ' ||
                                    '    AND PLAN_CONTABLE.ANO        = ' || UN_ANO || 
                                    '    AND PLAN_CONTABLE.CODIGO     = ''' || MI_CODIGOANTERIOR  || '''  ' ||
                                    '    AND PLAN_CONTABLE.MOVIMIENTO NOT IN(0) ' ||
                                    '    AND PLAN_CONTABLE.SALDO'|| MI_MES ||'     <>0 ';
                      OPEN MI_RS2 FOR MI_STRSQL;
                      LOOP
                      FETCH MI_RS2 INTO MI_COMPANIA,MI_ANO,MI_CODIGO,MI_NATURALEZA,MI_NOMBRE,MI_MOVIMIENTO,MI_MAN_CEN_CTO
                                       ,MI_MAN_AUX_TER,MI_MAN_AUX_GEN,MI_TERCERO,MI_SUCURSAL,MI_AUXILIAR,MI_REFERENCIA_CONTABLE
                                       ,MI_CENTRO_COSTO,MI_FUENTE_RECURSO,MI_NOMBRETERCERO,MI_NOMBREAUXILIAR,MI_NOMBRECENTROCOSTO
                                       ,MI_SALDO0,MI_DEBITO,MI_CREDITO;
                      EXIT WHEN MI_RS2%NOTFOUND;    
                          MI_CONSECUTIVO :=MI_CONSECUTIVO+1;

                          MI_CAMPOSINSERT := '      COMPANIA
                                                   ,ANO
                                                   ,TIPO_CPTE
                                                   ,COMPROBANTE
                                                   ,CONSECUTIVO
                                                   ,CUENTA
                                                   ,FECHA
                                                   ,NATURALEZA
                                                   ,DESCRIPCION
                                                   ,VALOR_DEBITO
                                                   ,VALOR_CREDITO
                                                   ,EJECUCION_DEBITO
                                                   ,EJECUCION_CREDITO
                                                   ,BASE_GRAVABLE
                                                   ,CENTRO_COSTO
                                                   ,TERCERO
                                                   ,SUCURSAL
                                                   ,AUXILIAR
                                                   ,REFERENCIA
                                                   ,FUENTE_RECURSO
                                                   ,BASE_IVA
                                                   ,DESEMBOLSO
                                                   ,SALDOCUENTA
                                                   ,DATE_CREATED
                                                   ,CREATED_BY
                                                   ,PORCENTAJERETENCION
                                              ';

                          MI_VALORES:=' ''' || UN_COMPANIA             ||'''
                                       ,  ' || MI_ANIO                 ||'
                                       ,''' || MI_TIPO                 ||'''
                                       ,  ' || MI_GENCONSECUTIVO       ||'
                                       ,  ' || MI_CONSECUTIVO          ||'
                                       ,''' || MI_CODIGO              ||'''
                                       ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY')  || ''', ''DD/MM/YYYY'')
                                       ,''' || MI_NATURALEZACUENTA     ||'''
                                       , ''COMPROBANTE DE RECLASIFICACION''
                                       ,  ' || NVL(MI_DEBITO,0)       ||'
                                       ,  ' || NVL(MI_CREDITO,0)      ||'
                                       ,       0
                                       ,       0
                                       ,  ' || ABS(NVL(MI_SALDO0,0))       || '
                                       ,''' || NVL(MI_CENTRO_COSTO,PCK_DATOS.FC_CONS_CENTRO)            || '''
                                       ,''' || NVL(MI_TERCERO,PCK_DATOS.FC_CONS_TERCERO)                || '''
                                       ,''' || NVL(MI_SUCURSAL,PCK_DATOS.FC_CONS_SUCURSAL)              || '''
                                       ,''' || NVL(MI_AUXILIAR,PCK_DATOS.FC_CONS_AUXILIAR)              || '''
                                       ,  ' || NVL(MI_REFERENCIA_CONTABLE,PCK_DATOS.FC_CONS_REFERENCIA) || '
                                       ,  ' || NVL(MI_FUENTE_RECURSO,PCK_DATOS.FC_CONS_FUENTE)          || '
                                       ,       0
                                       ,       0
                                       ,       0
                                       ,TO_DATE(SYSDATE, ''DD/MM/YYYY'')
                                       ,''' || UN_USUARIO              || '''
                                       , 0
                                      ';

                          BEGIN
                              BEGIN
                                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                                                        UN_ACCION  => 'I', 
                                                                        UN_CAMPOS  => MI_CAMPOSINSERT, 
                                                                        UN_VALORES => MI_VALORES);  
                          
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN

                                  RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                              END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                      UN_EXC_COD   => SQLCODE,
                                      UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_RECLASIFICAR
                                 );
                          END;
                      END LOOP;  
                      CLOSE MI_RS2; 
                  END LOOP;
              END LOOP;
          CLOSE MI_RS; 

              --  ACTUALIZACIÓN EN LA TABLA RECLASIFICAR NIIF DEL CAMPO REALIZADO
          MI_TABLA      := 'RECLASIFICAR_NIIF';
          MI_CAMPOS     := 'REALIZADO ='||-1||',MODIFIED_BY='''||UN_USUARIO||''',DATE_MODIFIED=SYSDATE';
          MI_CONDICION  := 'COMPANIA   = '''||UN_COMPANIA||''' 
                            AND ANO    =  '||MI_ANIO||'
                            AND NUMERO =  '||UN_NUMEROREGISTRO||'';
          MI_PCKDATOS   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M',  
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;

        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>SQLCODE,
                                     UN_ERROR_COD =>PCK_ERRORES.ERR_CONTABILIDAD_RECLAS_NIIF);

    END PR_RECLASIFICARNIIF;

    FUNCTION FC_INCONS_PLANOBANCOLOMBIA 
(
 /*
      NAME              : FC_INCONS_PLANOBANCOLOMBIA 
      AUTHOR            : JONATHAN LEONARDO MALAVER JIMÉNEZ
      DATE              : 16/08/2018
      TIME              : 10:00 AM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              :    
      DESCRIPTION       : Genera un archivo plano con 
                          las inconsistencias encontradas durante el proceso y realiza la actualización del ENVIADO de la tabla COMPROBANTE_CNT.
      MODIFICATIONS     : 
      PARAMETERS        : 

      @NAME  :  inconsistenciasPlanoBancolombia
      @METHOD:  GET 
    */ 

   UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA, 
   UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
   UN_TIPO            IN COMPROBANTE_CNT.TIPO%TYPE,
   UN_CUENTA_INICIAL  IN COMPROBANTE_CNT.CUENTA%TYPE,
   UN_CUENTA_FINAL    IN COMPROBANTE_CNT.CUENTA%TYPE,
   UN_NUMERO_INICIAL  IN COMPROBANTE_CNT.NUMERO%TYPE,
   UN_NUMERO_FINAL    IN COMPROBANTE_CNT.NUMERO%TYPE,
   UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
   RETURN  CLOB

AS  
  	MI_RTA                  CLOB;
  	MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_EXISTE               PCK_SUBTIPOS.TI_ENTERO;
    MI_CONTADOR             PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA                VARCHAR2(200);
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;

BEGIN 
        MI_CONTADOR := 0;
        MI_RTA := 'Para los siguientes terceros debe ser configurada la cuenta de banco mediante la ruta: ARCHIVOS/TERCEROS/PESTAÑA PAGOS EN'||CHR(13)||CHR(10);
        MI_RTA := MI_RTA || RPAD('SEC',5)                      ||CHR(9)|| 
                            RPAD('DOCUMENTO DE IDENTIDAD',25)  ||CHR(9)||
                            RPAD('NOMBRE',50)                  ||CHR(9)||
                            RPAD('BANCO',12)                      
                            ||CHR(13)||CHR(10); 

		FOR MI_RS IN (
                    SELECT  TERCEROPAGOS.BANCO,
                            CASE WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) = -1
                              THEN APODERADO.NIT
                              ELSE TERCERO.NIT
                            END DOCUMENTODELBENEFICIARIO,
                            CASE WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) = -1
                              THEN SUBSTR(APODERADO.NOMBRE,1,18)
                              ELSE SUBSTR(TERCERO.NOMBRE,1,18)
                            END NOMBREDBENEFICIARIO,
                            TERCEROPAGOS.CUENTA AS NOCUENTABENEFICIERIO
                    FROM COMPROBANTE_CNT
                    LEFT JOIN TERCERO
                    ON COMPROBANTE_CNT.COMPANIA  = TERCERO.COMPANIA 
                    AND COMPROBANTE_CNT.TERCERO  = TERCERO.NIT
                    AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
                    LEFT JOIN TERCERO APODERADO
                    ON TERCERO.COMPANIA           = APODERADO.COMPANIA
                    AND TERCERO.NITAPODERADO      = APODERADO.NIT
                    AND TERCERO.SUCURSALAPODERADO = APODERADO.SUCURSAL
                    LEFT JOIN TERCEROPAGOS
                    ON COMPROBANTE_CNT.COMPANIA  = TERCEROPAGOS.COMPANIA
                    AND COMPROBANTE_CNT.SUCURSAL = TERCEROPAGOS.SUCURSAL
                    AND COMPROBANTE_CNT.TERCERO  = TERCEROPAGOS.NIT
                    LEFT JOIN BANCO
                    ON TERCEROPAGOS.COMPANIA      = BANCO.COMPANIA
                    AND TERCEROPAGOS.BANCO        = BANCO.BANCO
                    WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                    AND COMPROBANTE_CNT.ANO        = UN_ANO
                    AND COMPROBANTE_CNT.TIPO       = UN_TIPO
                    AND COMPROBANTE_CNT.CUENTA BETWEEN UN_CUENTA_INICIAL AND UN_CUENTA_FINAL
                    AND COMPROBANTE_CNT.NUMERO BETWEEN UN_NUMERO_INICIAL AND UN_NUMERO_FINAL
                    AND COMPROBANTE_CNT.PAGOENPLANO IN (-1) 
                    AND COMPROBANTE_CNT.ENVIADO     IN (0) 
                    AND NVL(TERCEROPAGOS.BANCO,0)   IN ('0')
                  ) 
			LOOP  

        MI_CONTADOR := MI_CONTADOR + 1;

        MI_RTA := MI_RTA || RPAD(MI_CONTADOR,5)                        ||CHR(9)||
                            RPAD(MI_RS.DOCUMENTODELBENEFICIARIO,25)    ||CHR(9)||
                            RPAD(MI_RS.NOMBREDBENEFICIARIO,50)         ||CHR(9)||
                            RPAD(MI_RS.BANCO,12)          
                            ||CHR(13)||CHR(10);  

      END LOOP;   

      IF MI_CONTADOR = 0 THEN 

      /*Se envia el valor del clob en 0 para la generación del archivo plano excel desde el controlador*/
            MI_RTA := '0';         
      END IF;      

		RETURN MI_RTA;		  
END FC_INCONS_PLANOBANCOLOMBIA;    



FUNCTION FC_ASOBANCARIA_IMPORTARCT
 /*
    NAME              : FC_ASOBANCARIA_IMPORTAR   ACCES => Asobancaria_importar/ SYSMANCT
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 19/07/2021
    TIME              : 2:00 PM
    DESCRIPCION       : FUNCION QUE PERMITE SUBIR EL PLANO DE ASOBANCARIA
    PARAMETROS        : UN_COMPANIA   COMPANIA DE INGRESO A LA COMPANIA
                        UN_USUARIO    USUARIO QUE EJECUTA EL PROCESO.
                        UN_CADENA     CADENA CONCATENADA DESDE EL CONTROLADOR 
  @NAME : asobancariaImportarCT
  @METHOD:  GET
  */
(
  UN_COMPANIA   IN    PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO    IN    PCK_SUBTIPOS.TI_USUARIO,
  UN_CADENA     IN    CLOB
)  RETURN CLOB AS
  MI_RETORNO          VARCHAR2(32000 CHAR);
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_RTA              PCK_SUBTIPOS.TI_ENTERO;  
  MI_ERROR            VARCHAR2(2 CHAR);
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;  
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;  
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;  
  MI_CADENA           CLOB;
  MI_LINEAS           PCK_SUBTIPOS.TI_ENTERO;
  MI_ARCHIVO          CLOB;
  MI_TIPOREGISTRO     VARCHAR2(5 CHAR);
  MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO;
  MI_NREGISTROS       PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_CODIGOASOBA      VARCHAR2(20 CHAR);
  MI_NATCTABANCO      VARCHAR2(1 CHAR);  
  MI_CUENTABANCO      PLAN_CONTABLE.CODIGO%TYPE;
  MI_NUMEROCTA        VARCHAR2(20 CHAR);
  MI_NUMEROPAGOS      PCK_SUBTIPOS.TI_ENTERO;
  MI_IDASOBANCARIA    PARAMETRO.VALOR%TYPE;
  MI_CANTREG          PCK_SUBTIPOS.TI_ENTERO;
  MI_FECHARECAUDO     VARCHAR2(10 CHAR);
  MI_HORAREGISTRO     VARCHAR2(5 CHAR);
  MI_BANCOCODASOB     VARCHAR2(4 CHAR);  
  MI_NROCUENTA        VARCHAR2(20 CHAR);
  MI_PAQUETEA         VARCHAR2(4 CHAR);
  MI_REGISTROSA       PCK_SUBTIPOS.TI_ENTERO;
  MI_VALORA           PCK_SUBTIPOS.TI_DOBLE;  
  MI_VLRPLANO         PCK_SUBTIPOS.TI_DOBLE;
  MI_TIPOFAC          VARCHAR2(3 CHAR);
  MI_NUMFACTURA       VARCHAR2(10 CHAR);
  MI_STRSQL           VARCHAR2(1000 CHAR):='';
  MI_ANO              COMPROBANTE_CNT.ANO%TYPE;
  MI_TIPO             COMPROBANTE_CNT.TIPO%TYPE;
  MI_NUMERO           COMPROBANTE_CNT.NUMERO%TYPE; 
  MI_FECHA_VCN_DOC    COMPROBANTE_CNT.FECHA_VCN_DOC%TYPE;
  MI_VALOR            COMPROBANTE_CNT.VLR_DOCUMENTO%TYPE; 
  MI_DESCRIPCION      COMPROBANTE_CNT.DESCRIPCION%TYPE;
  MI_TERCERO          COMPROBANTE_CNT.TERCERO%TYPE;
  MI_FECHAEXP         COMPROBANTE_CNT.FECHA%TYPE; 
  MI_CSCE             PCK_SUBTIPOS.TI_ENTERO;  
  MI_CSCP             PCK_SUBTIPOS.TI_ENTERO;  
  MI_EXISTEFRA        PCK_SUBTIPOS.TI_LOGICO;
  MI_PAGA             PCK_SUBTIPOS.TI_LOGICO;
  MI_AFECTADO         PCK_SUBTIPOS.TI_DOBLE;  
  MI_RTAINGRESO       VARCHAR2(250 CHAR);
  MI_PLANOCARGADO     VARCHAR2(250 CHAR);  
  MI_ARCHIVOSALIDA    VARCHAR2(32000 CHAR);--UTL_FILE.FILE_TYPE;
  MI_RUTASALIDA       VARCHAR2(4000 CHAR):= 'LOG_CARGAPLANOASOBANCARIA_';
BEGIN
      
    -- LIMPIAR TABLAS DEL PROCESO
    BEGIN
        BEGIN
            MI_TABLA     := 'ASOBANCARIA_ENC_ARC';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_MSGERROR(1).CLAVE := 'NOMTABLA';
            MI_MSGERROR(1).VALOR := MI_TABLA;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        BEGIN
            MI_TABLA     := 'ASOBANCARIA_ENC_LOT';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_MSGERROR(1).CLAVE := 'NOMTABLA';
            MI_MSGERROR(1).VALOR := MI_TABLA;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        BEGIN
            MI_TABLA     := 'ASOBANCARIA_DETALLE';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_MSGERROR(1).CLAVE := 'NOMTABLA';
            MI_MSGERROR(1).VALOR := MI_TABLA;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        BEGIN
            MI_TABLA     := 'ASOBANCARIA_CON_LOT';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_MSGERROR(1).CLAVE := 'NOMTABLA';
            MI_MSGERROR(1).VALOR := MI_TABLA;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        BEGIN
            MI_TABLA     := 'ASOBANCARIA_CON_ARC';
            MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''';
            MI_RTA       := PCK_DATOS.FC_ACME(UN_TABLA  => MI_TABLA, 
                                              UN_ACCION => 'E', 
                                              UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_MSGERROR(1).CLAVE := 'NOMTABLA';
            MI_MSGERROR(1).VALOR := MI_TABLA;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;

        END;
        
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ELIMINARREG,
                                    UN_TABLAERROR => MI_TABLA, 
                                    UN_REEMPLAZOS => MI_MSGERROR);
    END;
    
    MI_CADENA := UN_CADENA;
    IF REGEXP_COUNT(MI_CADENA, '[^@]+') = 0 THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERR_CNT_ASOBSINREG);
        END;
    END IF;
    MI_ARCHIVO      :=  REGEXP_SUBSTR(UN_CADENA,'[^´@]+', 1);
    MI_NREGISTROS   :=  REGEXP_COUNT(UN_CADENA, '[^@]+');
    MI_CODIGOASOBA  := REPLACE(SUBSTR(SUBSTR(MI_ARCHIVO, 21, 20), 1, 3), '-', '');
    MI_NUMEROCTA    := LTRIM(REPLACE(SUBSTR(SUBSTR(MI_ARCHIVO, 21, 20), 4, 18),'-', ''),'0');
    MI_CONSECUTIVO  := 1;
    MI_NUMEROPAGOS  := 0;
    MI_LINEAS := MI_NREGISTROS;
    
    WHILE MI_LINEAS <> 0
    LOOP
        MI_ARCHIVO    :=  REGEXP_SUBSTR(MI_CADENA, '[^´@]+', 1);
        MI_CADENA     :=  REPLACE(MI_CADENA, MI_ARCHIVO||'@','');
        MI_LINEAS     :=  REGEXP_COUNT(MI_CADENA, '[^@]+');
        MI_TIPOREGISTRO:= SUBSTR(MI_ARCHIVO,1,2);
        MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
        IF MI_TIPOREGISTRO IN ('01') THEN
            MI_TABLA      := 'ASOBANCARIA_ENC_ARC';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
            MI_CAMPOS     := 'COMPANIA, ID, TIPO_REGISTRO,FECHA_RECAUDO,
                           CODIGO_ENTIDAD,NUMERO_CUENTA,FECHA_ARCHIVO,
                           HORA,MODIFICADOR,TIPO_CUENTA,NIT_EMPRESA,
                           CREATED_BY,DATE_CREATED';
            MI_VALORES    := ' ''' || UN_COMPANIA            || ''',' 
                                   || MI_CONSECUTIVO          || ','''
                                   || MI_TIPOREGISTRO        || ''',''' 
                                   || SUBSTR(MI_ARCHIVO,13,8) || ''','''
                                   || SUBSTR(MI_ARCHIVO,21,3)|| ''',''' 
                                   || LTRIM(SUBSTR(MI_ARCHIVO,24,17), '0')|| ''','''
                                   || SUBSTR(MI_ARCHIVO,41,8)|| ''',''' 
                                   || SUBSTR(MI_ARCHIVO,49,4) || ''','''
                                   || LTRIM(SUBSTR(MI_ARCHIVO,53,1),'0')|| ''',''' 
                                   || LTRIM(SUBSTR(MI_ARCHIVO,54,2),'0') || ''','''
                                   || LTRIM(SUBSTR(MI_ARCHIVO,3,10),'0')|| ''',''' 
                                   || UN_USUARIO              || ''',SYSDATE';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(0).CLAVE:='TIPO';
                    MI_MSGERROR(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTARTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;    
        IF MI_TIPOREGISTRO IN ('05') THEN
            MI_TABLA      :=  'ASOBANCARIA_ENC_LOT';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,EAN,NUMERO_LOTE,
                            CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','
                                 || MI_CONSECUTIVO          || ','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,3,13),'0') ||''','''
                                 || SUBSTR(MI_ARCHIVO,18,2) || ''','''
                                 || UN_USUARIO              || ''',SYSDATE';

            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(0).CLAVE:='TIPO';
                    MI_MSGERROR(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTARTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;   
        IF MI_TIPOREGISTRO IN ('06') THEN
            MI_TABLA      :=  'ASOBANCARIA_DETALLE';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                  UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                  UN_CAMPO    =>  'ID',
                                  UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,CODIGO,VALOR,PROCEDENCIA,                
                            MEDIOS_PAGO,NUMERO_OPERACION,NUMERO_AUTORIZACION,        
                            CODIGO_ENTIDAD,CODIGO_SUCURSAL,SECUENCIA,CAUSAL,
                            CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','
                                 || MI_CONSECUTIVO          || ','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || (SUBSTR(MI_ARCHIVO,3,48)) || ''','''
                                 || SUBSTR(MI_ARCHIVO,51,14)|| ''','''
                                 || SUBSTR(MI_ARCHIVO,65,2) || ''','''
                                 || SUBSTR(MI_ARCHIVO,67,2) || ''','''
                                 || SUBSTR(MI_ARCHIVO,69,6) || ''','''
                                 || SUBSTR(MI_ARCHIVO,75,6) || ''','''
                                 || SUBSTR(MI_ARCHIVO,81,3) || ''','''
                                 || SUBSTR(MI_ARCHIVO,84,4) || ''','''
                                 || SUBSTR(MI_ARCHIVO,88,7) || ''','''
                                 || SUBSTR(MI_ARCHIVO,95,3) || ''','''
                                 || UN_USUARIO              || ''',SYSDATE';

            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(0).CLAVE:='TIPO';
                    MI_MSGERROR(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTARTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;
        IF MI_TIPOREGISTRO IN ('08') THEN
            MI_TABLA      :=  'ASOBANCARIA_CON_LOT';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                  UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                  UN_CAMPO    =>  'ID',
                                  UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,TOTAL_REGISTROS,                 
                            VALOR_TOTAL,NUMERO_LOTE,CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','
                                 || MI_CONSECUTIVO          || ','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,3,9),'0') || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,12,18),'0')|| ''','''
                                 || SUBSTR(MI_ARCHIVO,32,2)|| ''','''
                                 || UN_USUARIO              || ''',SYSDATE';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(0).CLAVE:='TIPO';
                    MI_MSGERROR(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTARTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;    
        IF MI_TIPOREGISTRO IN ('09') THEN
            MI_TABLA      :=  'ASOBANCARIA_CON_ARC';
            MI_CONSECUTIVO:=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA=>  MI_TABLA,
                                                           UN_CRITERIO =>  ' COMPANIA = '''||UN_COMPANIA||'''',
                                                           UN_CAMPO    =>  'ID',
                                                           UN_INICIAL  =>  '1');
            MI_CAMPOS  := ' COMPANIA,ID,TIPO_REGISTRO,TOTAL_REGISTROS,                 
                            VALOR_TOTAL,CREATED_BY,DATE_CREATED';
            MI_VALORES  := ' ''' || UN_COMPANIA             || ''','''
                                 || MI_CONSECUTIVO          || ''','''
                                 || MI_TIPOREGISTRO         || ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,3,9),'0')|| ''','''
                                 || LTRIM(SUBSTR(MI_ARCHIVO,12,18),'0')
                                 || ''','''|| UN_USUARIO            || ''',SYSDATE';

            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                           UN_ACCION  =>  'I',
                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                           UN_VALORES =>  MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(0).CLAVE:='TIPO';
                    MI_MSGERROR(0).VALOR:= MI_TIPOREGISTRO;
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTARTIPO
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;    
        IF MI_TIPOREGISTRO = 'RT' THEN
            MI_PLANOCARGADO := SUBSTR(MI_ARCHIVO,3,LENGTH(MI_ARCHIVO));
        END IF;
    END LOOP;
    
    MI_RETORNO:= 'OK';
    MI_IDASOBANCARIA      := PCK_SYSMAN_UTL.FC_VALIDARPARAMETRO
                                             (UN_COMPANIA  => UN_COMPANIA
                                             ,UN_PARAMETRO => 'CONSECUTIVO ASOBANCARIA CNT'
                                             ,UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD ); 
    BEGIN 
        BEGIN
            MI_IDASOBANCARIA := MI_IDASOBANCARIA + 1;
            MI_CAMPOS := 'VALOR =''' ||  MI_IDASOBANCARIA || '''';
            MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA || '''
                             AND NOMBRE     = ''CONSECUTIVO ASOBANCARIA CNT'' 
                             AND MODULO     = ' || PCK_DATOS.MODULOCONTABILIDAD;
                             
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PARAMETRO', 
                                                   UN_ACCION    => 'M', 
                                                   UN_CAMPOS    =>  MI_CAMPOS, 
                                                   UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            MI_MSGERROR(0).CLAVE := 'NOMPARAMETRO';
            MI_MSGERROR(0).VALOR := 'CONSECUTIVO ASOBANCARIA';
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_CNT_ACTPARAMETRO);
    END;
    
    BEGIN
        SELECT COUNT('X')
        INTO   MI_CANTREG
        FROM   ASOBANCARIA_ENC_ARC;
        
        IF MI_CANTREG = 0 THEN
            MI_MSGERROR(0).CLAVE := 'PROCESO';
            MI_MSGERROR(0).VALOR := 'encabezado de archivo';
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;            
        END IF;
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBSINDATOSARC
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
        
    END;
    
    BEGIN
        SELECT COUNT('X')
        INTO   MI_CANTREG
        FROM   ASOBANCARIA_ENC_LOT;
        
        IF MI_CANTREG = 0 THEN
            MI_MSGERROR(0).CLAVE := 'PROCESO';
            MI_MSGERROR(0).VALOR := 'encabezado de lote';
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;            
        END IF;
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBSINDATOSARC
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
        
    END;
    
    BEGIN
        SELECT COUNT('X')
        INTO   MI_CANTREG
        FROM   ASOBANCARIA_CON_LOT;
        
        IF MI_CANTREG = 0 THEN
            MI_MSGERROR(0).CLAVE := 'PROCESO';
            MI_MSGERROR(0).VALOR := 'control de lote';
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;            
        END IF;
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBSINDATOSARC
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
        
    END;
    
    BEGIN
        SELECT COUNT('X')
        INTO   MI_CANTREG
        FROM   ASOBANCARIA_CON_ARC;
        
        IF MI_CANTREG = 0 THEN
            MI_MSGERROR(0).CLAVE := 'PROCESO';
            MI_MSGERROR(0).VALOR := 'control de archivo';
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;            
        END IF;
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBSINDATOSARC
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
        
    END;
    
    BEGIN
        SELECT COUNT('X')
        INTO   MI_CANTREG
        FROM   ASOBANCARIA_DETALLE;
        
        IF MI_CANTREG = 0 THEN
            MI_MSGERROR(0).CLAVE := 'PROCESO';
            MI_MSGERROR(0).VALOR := 'detalle';
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;            
        END IF;
        
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBSINDATOSARC
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
        
    END;
    --INICIALIZAR VALORES CON LOS DATOS DEL ENCABEZADO DEL ARCHIVO 
    
    BEGIN
        SELECT (SUBSTR(FECHA_RECAUDO,7,2) || '/' || SUBSTR(FECHA_RECAUDO,5,2) || '/' ||SUBSTR(FECHA_RECAUDO,1,4)) FECRECAUDO,  SUBSTR(HORA,1,2) || ':' ||SUBSTR(HORA,3,2) HORAREC, CODIGO_ENTIDAD, NUMERO_CUENTA
        INTO   MI_FECHARECAUDO, MI_HORAREGISTRO, MI_BANCOCODASOB, MI_NROCUENTA
        FROM   ASOBANCARIA_ENC_ARC;
    EXCEPTION WHEN TOO_MANY_ROWS THEN
                  RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
              
    END;   
    MI_NROCUENTA := TRIM(MI_NROCUENTA);
    -- VALIDAR BANCO Y EXTRAER CODIGO PLAN CONTABLE CONFIGURADO
    BEGIN
        BEGIN
            SELECT  CB.IDCONTABLE, P.NATURALEZA
            INTO    MI_CUENTABANCO , MI_NATCTABANCO
            FROM    CUENTABANCOS CB INNER JOIN BANCO B
                        ON  CB.COMPANIA = B.COMPANIA
                        AND CB.BANCO =  B.BANCO
                INNER JOIN PLAN_CONTABLE P
                    ON CB.COMPANIA = P.COMPANIA
                    AND CB.ANO = P.ANO
                    AND CB.IDCONTABLE = P.CODIGO
            WHERE   CB.COMPANIA = UN_COMPANIA
            AND     CB.ANO      = SUBSTR(MI_FECHARECAUDO, 7, 4)
            AND     CB.ESTADO   = 'A'
            AND     B.BANCOASOBANCARIA = TO_NUMBER(MI_BANCOCODASOB)
            AND     CB.CUENTANUMERO    = MI_NROCUENTA ;
            
            EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_ERROR := '1';
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                      WHEN TOO_MANY_ROWS THEN
                        MI_ERROR := '2';
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    IF MI_ERROR = 1 THEN
                            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBSINCTABANCO
                                       ,UN_TABLAERROR => MI_TABLA);    
                    ELSIF MI_ERROR = 2 THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBVARIASCTASBANCO
                                       ,UN_TABLAERROR => MI_TABLA);    
                    END IF;
    END;
   -- EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
    
    -- REGISTRO DEL LOG DE LA CARGA
    MI_CSCE := 0;
    MI_CSCP := 0;
    MI_TABLA    :=  'ASOBANCARIA_LOG';
    MI_CAMPOS   := 'COMPANIA,ID, USUARIO, BANCO, NUMERO_PAGOS, NUMERO_ERRORES, FECHA, HORA, 
                    ARCHIVO, FECHARECAUDO,  CREATED_BY, DATE_CREATED';
    MI_VALORES  := '''' || UN_COMPANIA             || ''','
                        || MI_IDASOBANCARIA        || ','''
                        || UN_USUARIO              || ''','''
                        || MI_BANCOCODASOB         || ''','
                        || MI_CSCP                 || ','
                        || MI_CSCE                 || ', 
                        SYSDATE,                        
                        TO_CHAR(SYSDATE,''HH24:MI:SS''), '''
                        || MI_PLANOCARGADO          ||''', TO_DATE('''
                        || MI_FECHARECAUDO         || ''', ''DD/MM/YYYY HH24:MI:SS''),'''                        
                        || UN_USUARIO              || ''',
                       SYSDATE';
    BEGIN
        BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                       UN_ACCION  =>  'I',
                                                       UN_CAMPOS  =>  MI_CAMPOS, 
                                                       UN_VALORES =>  MI_VALORES);
            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                MI_MSGERROR(0).CLAVE:='PROCESO';
                MI_MSGERROR(0).VALOR:= '- Registro del log de carga del plano-';
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_MSGERROR);
    END;
 
    
    FOR RSLOT IN( SELECT NUMERO_LOTE, TOTAL_REGISTROS, VALOR_TOTAL
               FROM   ASOBANCARIA_CON_LOT
              )
        LOOP
        
        FOR RSDET IN ( SELECT TO_NUMBER(SUBSTR(VALOR,1,LENGTH(VALOR)-2)||'.'||SUBSTR(VALOR,LENGTH(VALOR)-1, LENGTH(VALOR))) VALORPLANO, SUBSTR(CODIGO, 37, 2) TIPOFAC,  SUBSTR(CODIGO, 39, LENGTH(CODIGO)-37) NUMFACTURA
                    FROM   ASOBANCARIA_DETALLE
                  )
            LOOP
                
                IF RSDET.TIPOFAC = '61' THEN
                    MI_TIPOFAC := 'FTA';
                ELSIF RSDET.TIPOFAC = '62' THEN    
                    MI_TIPOFAC := 'FTR';
                ELSIF RSDET.TIPOFAC = '81' THEN
                    MI_TIPOFAC := 'FSS';
                ELSIF RSDET.TIPOFAC = '82' THEN
                    MI_TIPOFAC := 'FSE';
                END IF;
                
                BEGIN
                    MI_STRSQL := 'SELECT  ANO, TIPO, NUMERO, FECHA_VCN_DOC, VLR_DOCUMENTO VALOR, DESCRIPCION, TERCERO,  TO_CHAR(FECHA, ''DD/MM/YYYY'') FECHA_EXP                                          
                                  FROM    COMPROBANTE_CNT
                                  WHERE   COMPANIA = ''' || UN_COMPANIA || ''' 
                                  AND     TIPO =     ''' || MI_TIPOFAC || ''' 
                                  AND     NUMERO =   ''' || RSDET.NUMFACTURA || '''';
                    EXECUTE IMMEDIATE MI_STRSQL
                      INTO MI_ANO,  MI_TIPO, MI_NUMERO, MI_FECHA_VCN_DOC, MI_VALOR, MI_DESCRIPCION, MI_TERCERO, MI_FECHAEXP;
                      MI_EXISTEFRA := 1;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTEFRA := 0;
                    MI_RETORNO := 'E';
                    MI_CSCE := MI_CSCE + 1;
                    MI_DESCRIPCION := 'La factura ' || MI_TIPOFAC || '-' || RSDET.NUMFACTURA || ' no se encuentra registrada en el sistema';
                    MI_TABLA    :=  'ASOBANCARIA_ERROR';
                    MI_CAMPOS   := 'COMPANIA, FACTURA, FECHA, BANCO, 
                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';
                    MI_VALORES  := '''' || UN_COMPANIA             || ''','''
                                        || RSDET.NUMFACTURA        || ''','''
                                        || MI_FECHARECAUDO         || ''','''
                                        || MI_BANCOCODASOB         || ''','''
                                        || MI_IDASOBANCARIA        || ''','''
                                        || MI_CSCE                 || ''','''
                                        || UN_USUARIO              || ''',
                                       SYSDATE, ''' || MI_DESCRIPCION ||'''';
                    BEGIN
                        BEGIN
                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                       UN_ACCION  =>  'I',
                                                                       UN_CAMPOS  =>  MI_CAMPOS, 
                                                                       UN_VALORES =>  MI_VALORES);
                            
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                MI_MSGERROR(0).CLAVE:='PROCESO';
                                MI_MSGERROR(0).VALOR:= '-Validar existencia factura-';
                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                        END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                       ,UN_TABLAERROR => MI_TABLA
                                       ,UN_REEMPLAZOS => MI_MSGERROR);
                    END;
                END ;
                
                -- SI EXISTE LA FACTURA VALIDAR SI EL COMPROBANTE SE ENCUENTRA AFECTADO
                IF MI_EXISTEFRA <> 0 THEN
                    BEGIN
                        MI_STRSQL := 'SELECT  DISTINCT TIPO_CPTE, COMPROBANTE
                                      FROM    DETALLE_COMPROBANTE_CNT
                                      WHERE   COMPANIA =            ''' || UN_COMPANIA || ''' 
                                      AND     TIPO_CPTE_AFECT =     ''' || MI_TIPOFAC || ''' 
                                      AND     CMPTE_AFECTADO =      ' || RSDET.NUMFACTURA || '
                                      AND     ROWNUM = 1';
                        EXECUTE IMMEDIATE MI_STRSQL
                          INTO  MI_TIPO, MI_NUMERO;
                        MI_PAGA := 1;
                        MI_RETORNO := 'E';
                        MI_CSCE := MI_CSCE + 1;
                        MI_DESCRIPCION := 'La factura ' || MI_TIPOFAC || '-' || RSDET.NUMFACTURA || ',  ya registra un pago con el comprobante ' || MI_TIPO || ' - ' || MI_NUMERO;
                        MI_TABLA    :=  'ASOBANCARIA_ERROR';
                        MI_CAMPOS   := 'COMPANIA, FACTURA, FECHA, BANCO, 
                                        ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';
                        MI_VALORES  := '''' || UN_COMPANIA             || ''','''
                                            || RSDET.NUMFACTURA        || ''','''
                                            || MI_FECHARECAUDO         || ''','''
                                            || MI_BANCOCODASOB         || ''','''
                                            || MI_IDASOBANCARIA        || ''','''
                                            || MI_CSCE                 || ''','''
                                            || UN_USUARIO              || ''',
                                           SYSDATE, ''' || MI_DESCRIPCION ||'''';
                        BEGIN
                            BEGIN
                                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                           UN_ACCION  =>  'I',
                                                                           UN_CAMPOS  =>  MI_CAMPOS, 
                                                                           UN_VALORES =>  MI_VALORES);
                                
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                    MI_MSGERROR(0).CLAVE:='PROCESO';
                                    MI_MSGERROR(0).VALOR:= '-Validar pago previo-';
                                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                            END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                PCK_ERR_MSG.RAISE_WITH_MSG(    
                                            UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                           ,UN_TABLAERROR => MI_TABLA
                                           ,UN_REEMPLAZOS => MI_MSGERROR);
                        END;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_PAGA := 0;
                    END ;               
                    
                    -- SI NO SE HA REGISTRADO PAGO SE DEBE VALIDAR EL VALOR DE LA FACTURA CONTRA EL VALOR DEL PLANO
                    IF MI_PAGA  = 0 THEN
                        IF RSDET.VALORPLANO = MI_VALOR   THEN
                            -- VALIDAR SI LA FACTURA A HA SIDO AFECTADA PARCIALMENTE
                            BEGIN
                                BEGIN
                                    MI_STRSQL := '  SELECT  DEBITOSAFECTADOS AFECTADO
                                                    FROM    COMPROBANTE_CNT
                                                    WHERE   COMPANIA =  ''' || UN_COMPANIA || ''' 
                                                    AND     TIPO   =    ''' || MI_TIPOFAC || ''' 
                                                    AND     NUMERO =    ' || RSDET.NUMFACTURA ;
                                    EXECUTE IMMEDIATE MI_STRSQL INTO MI_AFECTADO;
                                    IF MI_AFECTADO > 0 THEN
                                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                                    END IF;
                                EXCEPTION WHEN NO_DATA_FOUND THEN
                                    MI_AFECTADO := 0;
                                END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                PCK_ERR_MSG.RAISE_WITH_MSG(    
                                            UN_EXC_COD    => SQLCODE
                                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                           ,UN_TABLAERROR => MI_TABLA);
                            END;
                            
                            IF MI_AFECTADO = 0 THEN
                                MI_RTAINGRESO :=  PCK_CONTABILIDAD3.FC_GENERARINGRESOSTASAS (
                                                          UN_COMPANIA       => UN_COMPANIA, 
                                                          UN_USUARIO        => UN_USUARIO, 
                                                          UN_ANO            => MI_ANO,
                                                          UN_TIPOCPTE       => MI_TIPO,
                                                          UN_NROCPTE        => MI_NUMERO,
                                                          UN_FECHARECAUDO   => MI_FECHARECAUDO,    
                                                          UN_TIPOCPTEREC    => 'NBA',
                                                          UN_VLRCPTE        => MI_VALOR,
                                                          UN_FECHAVCTO      => MI_FECHA_VCN_DOC, 
                                                          UN_BANCOREC       => MI_BANCOCODASOB, 
                                                          UN_NATCTABANCO    => MI_NATCTABANCO,
                                                          UN_CUENTABANCO    => MI_CUENTABANCO
                                );
                                IF SUBSTR(MI_RTAINGRESO, 1,1) <> 'E' THEN
                                     MI_TABLA := 'ASOBANCARIA_PAGOS';
                                     MI_CSCP := MI_CSCP + 1; 
                                     MI_CAMPOS := 'COMPANIA , FACTURA ,	FECHA, 	BANCO, 	ID, CONSECUTIVO, 	CREATED_BY, DATE_CREATED, DESCRIPCION';
                                     MI_VALORES := '''' || UN_COMPANIA             || ''','''
                                                        || RSDET.NUMFACTURA        || ''','''
                                                        || MI_FECHARECAUDO         || ''','''
                                                        || MI_BANCOCODASOB         || ''','''
                                                        || MI_IDASOBANCARIA        || ''','''
                                                        || MI_CSCP                 || ''','''
                                                        || UN_USUARIO              || ''',
                                                       SYSDATE, ''' 
                                                       || MI_RTAINGRESO || '''';
                                    BEGIN
                                        BEGIN
                                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                                       UN_ACCION  =>  'I',
                                                                                       UN_CAMPOS  =>  MI_CAMPOS, 
                                                                                       UN_VALORES =>  MI_VALORES);
                                          
                                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                MI_MSGERROR(0).CLAVE:='PROCESO';
                                                MI_MSGERROR(0).VALOR:= '- Asobancaria pago-';
                                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                                        END;
                                        
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                                        UN_EXC_COD    => SQLCODE
                                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                                       ,UN_TABLAERROR => MI_TABLA
                                                       ,UN_REEMPLAZOS => MI_MSGERROR);
                                        
                                    END;
                                    
                                ELSE
                                    MI_RETORNO := 'E';
                                    MI_CSCE := MI_CSCE + 1; 
                                    MI_DESCRIPCION := MI_RTAINGRESO;
                                    MI_TABLA    :=  'ASOBANCARIA_ERROR';
                                    MI_CAMPOS   := 'COMPANIA, FACTURA, FECHA, BANCO, 
                                                    ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';
                                    MI_VALORES  := '''' || UN_COMPANIA             || ''','''
                                                        || RSDET.NUMFACTURA        || ''','''
                                                        || MI_FECHARECAUDO         || ''','''
                                                        || MI_BANCOCODASOB         || ''','''
                                                        || MI_IDASOBANCARIA        || ''','''
                                                        || MI_CSCE                 || ''','''
                                                        || UN_USUARIO              || ''',
                                                       SYSDATE, ''' 
                                                       || SUBSTR(MI_RTAINGRESO, 3, LENGTH(MI_RTAINGRESO)) ||'''';
                                    BEGIN
                                        BEGIN
                                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                                       UN_ACCION  =>  'I',
                                                                                       UN_CAMPOS  =>  MI_CAMPOS, 
                                                                                       UN_VALORES =>  MI_VALORES);
                                          
                                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                MI_MSGERROR(0).CLAVE:='PROCESO';
                                                MI_MSGERROR(0).VALOR:= '- Generar ingreso de Tasas -';
                                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                                        END;
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                                        UN_EXC_COD    => SQLCODE
                                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                                       ,UN_TABLAERROR => MI_TABLA
                                                       ,UN_REEMPLAZOS => MI_MSGERROR);
                                    END;
                                END IF;
                            END IF;
                        ELSE
                            MI_DESCRIPCION := 'Dif. Valor recaudo. La factura ' || MI_TIPOFAC || '-' || RSDET.NUMFACTURA || ' esta registrada en el sistema por un valor de ' || MI_VALOR || ', en el plano se reporta por pago por ' || RSDET.VALORPLANO ;
                            MI_RETORNO := 'E';
                            MI_CSCE := MI_CSCE + 1; 
                            MI_TABLA    :=  'ASOBANCARIA_ERROR';
                            MI_CAMPOS   := 'COMPANIA, FACTURA, FECHA, BANCO, 
                                            ID, CONSECUTIVO, CREATED_BY, DATE_CREATED, DESCRIPCION';
                            MI_VALORES  := '''' || UN_COMPANIA             || ''','''
                                                || RSDET.NUMFACTURA        || ''','''
                                                || MI_FECHARECAUDO         || ''','''
                                                || MI_BANCOCODASOB         || ''','''
                                                || MI_IDASOBANCARIA        || ''','''
                                                || MI_CSCE                 || ''','''
                                                || UN_USUARIO              || ''',
                                               SYSDATE, ''' || MI_DESCRIPCION ||'''';
                            BEGIN
                                BEGIN
                                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                               UN_ACCION  =>  'I',
                                                                               UN_CAMPOS  =>  MI_CAMPOS, 
                                                                               UN_VALORES =>  MI_VALORES);
                                                                         
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                        MI_MSGERROR(0).CLAVE:='PROCESO';
                                        MI_MSGERROR(0).VALOR:= '-Validar pago previo-';
                                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                                END;
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                                UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                               ,UN_TABLAERROR => MI_TABLA
                                               ,UN_REEMPLAZOS => MI_MSGERROR);
                            END;
                        END IF;
                    END IF;
                END IF;
        END LOOP;
    END LOOP;
    
    BEGIN   
        BEGIN
            MI_TABLA:='ASOBANCARIA_LOG';   
    
            MI_CAMPOS := 'NUMERO_PAGOS  =  '   || MI_CSCP || ',
                          NUMERO_ERRORES = '   || MI_CSCE || ',
                          MODIFIED_BY    = ''' || UN_USUARIO|| ''', 
                          DATE_MODIFIED         =SYSDATE ';
    
            MI_CONDICION:= '     COMPANIA = ''' || UN_COMPANIA ||'''' ||  
                           ' AND ID       = ' || MI_IDASOBANCARIA ;
    
    
            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLA,
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);      
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                                UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTARERROR
                                               ,UN_TABLAERROR => MI_TABLA);
    END;    
 
    MI_RETORNO := MI_IDASOBANCARIA;
    MI_RUTASALIDA := MI_RUTASALIDA || MI_PLANOCARGADO;
    -- CONSTRUIR RETORNO CON INFORMACION DEL PROCESO EJECUTADO       
    
    BEGIN
        FOR RSLOG IN (SELECT ARCHIVO, FECHA, HORA
                  FROM  ASOBANCARIA_LOG
                  WHERE COMPANIA = UN_COMPANIA
                  AND   ID = MI_IDASOBANCARIA)
            LOOP
            MI_ARCHIVOSALIDA := LPAD('PLANO CARGADO: ', 20) || MI_PLANOCARGADO || CHR(13)||CHR(10);
            MI_ARCHIVOSALIDA := MI_ARCHIVOSALIDA || LPAD('FECHA: ', 20) || RSLOG.FECHA || CHR(13)||CHR(10);
            MI_ARCHIVOSALIDA := MI_ARCHIVOSALIDA ||LPAD('HORA: ', 20) || RSLOG.HORA || CHR(13)||CHR(10);
            
            -- AGREGAR REGISTRO DE PAGOS SI EXISTE 
            IF MI_CSCP > 0 THEN
                MI_ARCHIVOSALIDA :=  MI_ARCHIVOSALIDA || CHR(13)||CHR(10);
                MI_ARCHIVOSALIDA :=  MI_ARCHIVOSALIDA || 'FACTURAS REGISTRARON PAGO CORRECTAMENTE' ||CHR(13)||CHR(10);
                MI_ARCHIVOSALIDA :=  MI_ARCHIVOSALIDA ||RPAD('No.', 10) || RPAD('FACTURA', 15) || RPAD('DESCRIPCION', 255)|| CHR(13)||CHR(10);
                FOR RSPAGOS IN ( SELECT CONSECUTIVO, FACTURA, DESCRIPCION
                                 FROM   ASOBANCARIA_PAGOS
                                 WHERE  COMPANIA = UN_COMPANIA
                                 AND    ID = MI_IDASOBANCARIA)
                    LOOP
                    
                    MI_ARCHIVOSALIDA := MI_ARCHIVOSALIDA ||RPAD(RSPAGOS.CONSECUTIVO, 10) || RPAD(RSPAGOS.FACTURA, 15) || RPAD(RSPAGOS.DESCRIPCION, 255) || CHR(13)||CHR(10);
                END LOOP;            
            END IF;
            
            IF MI_CSCE > 0 THEN
                MI_ARCHIVOSALIDA :=  MI_ARCHIVOSALIDA || CHR(13)||CHR(10);
                MI_ARCHIVOSALIDA :=  MI_ARCHIVOSALIDA || 'FACTURAS QUE REGISTRARON INCONSISTENCIAS EN EL PROCESO' ||CHR(13)||CHR(10);
                MI_ARCHIVOSALIDA :=  MI_ARCHIVOSALIDA || RPAD('No.', 10) || RPAD('FACTURA', 15) || RPAD('DESCRIPCION', 255)||CHR(13)||CHR(10);
                FOR RSERRORES IN ( SELECT CONSECUTIVO, FACTURA, DESCRIPCION
                                 FROM   ASOBANCARIA_ERROR
                                 WHERE  COMPANIA = UN_COMPANIA
                                 AND    ID = MI_IDASOBANCARIA)
                    LOOP
                    
                    MI_ARCHIVOSALIDA := MI_ARCHIVOSALIDA || RPAD(RSERRORES.CONSECUTIVO, 10) || RPAD(RSERRORES.FACTURA, 15) || RPAD(RSERRORES.DESCRIPCION, 255) || CHR(13)||CHR(10);
                END LOOP;            
            END IF;
        END LOOP;
        
           
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
             PCK_ERR_MSG.RAISE_WITH_MSG(    
                                                UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_ASOBINSERTLOG
                                               ,UN_TABLAERROR => MI_TABLA);
    END;
    
    
    
    MI_RETORNO := MI_ARCHIVOSALIDA;
    RETURN MI_RETORNO;

END FC_ASOBANCARIA_IMPORTARCT;

FUNCTION FC_GENERARINGRESOSTASAS 

/*
    NAME              : FC_GENERARINGRESOSTASAS   ACCES => GENERARINGRESOSTASAS/ SYSMANCT
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMON
    DATE MIGRADOR     : 24/07/2021
    TIME              : 11:00 AM
    DESCRIPCION       : FUNCION QUE PERMITE REALIZAR LA AFECTACION DEL COMPROBANTE CONTABLE DE LAS FACTURAS DE TASAS 
    PARAMETROS        : UN_COMPANIA         COMPANIA DE INGRESO A LA COMPANIA
                        UN_USUARIO          USUARIO QUE EJECUTA EL PROCESO.
                        UN_ANO              VIGENCIA EN LA CUAL SE GENERO LA FACTURA A RECAUDAR
                        UN_TIPOCPTE         TIPO DE COMPROBANTE CON EL CUAL SE CAUSO LA FACTURA
                        UN_NROCPTE          NUMERO DEL COMPROBANTE DE CAUSACION DE LA FACTURA
                        UN_FECHARECAUDO     FECHA EN EL CUAL SE REGISTRA EL PAGO DE LA FACTURA
                        UN_TIPOCPTEREC      TIPO DE COMPROBANTE CON EL CUAL SE CREARA EL COMPROBANTE DE RECAUDO
                        UN_VLRCPTE          VALOR COMPROBANTE DE CAUSACION
                        UN_FECHAVCTO        FECHA DE VENCIMIENTO DE LA FACTURA
                        UN_BANCOREC         CODIGO DEL BANCO EN EL CUAL SE REGISTRO EL PAGO
                        UN_CUENTABANCO      CODIGO CONTABLE DEL BANCO  DE RECAUDO
  @NAME : generarIngresosTasas
  @METHOD:  GET
  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO, 
  UN_ANO            IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCPTE       IN COMPROBANTE_CNT.TIPO%TYPE,
  UN_NROCPTE        IN COMPROBANTE_CNT.NUMERO%TYPE,
  UN_FECHARECAUDO   IN VARCHAR2,    
  UN_TIPOCPTEREC    IN COMPROBANTE_CNT.TIPO%TYPE,
  UN_VLRCPTE        IN PCK_SUBTIPOS.TI_DOBLE,
  UN_FECHAVCTO      IN COMPROBANTE_CNT.FECHA%TYPE, 
  UN_BANCOREC       IN COMPROBANTE_CNT.BANCO%TYPE, 
  UN_NATCTABANCO    IN PLAN_CONTABLE.NATURALEZA%TYPE,
  UN_CUENTABANCO    IN COMPROBANTE_CNT.CUENTA%TYPE
) RETURN  VARCHAR2 AS
    MI_STRSQL       VARCHAR2(4000 CHAR):='';
    MI_CANTREG      PCK_SUBTIPOS.TI_ENTERO;
    MI_NRODOCUMENTO COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE;
    MI_DESCRIPCION  COMPROBANTE_CNT.DESCRIPCION%TYPE;
    MI_TERCERO      COMPROBANTE_CNT.TERCERO%TYPE;
    MI_SUCURSAL     COMPROBANTE_CNT.SUCURSAL%TYPE;
    MI_RESULTADO    VARCHAR2(250 CHAR):= '';
    MI_NROREC       COMPROBANTE_CNT.NUMERO%TYPE;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_RTA          PCK_SUBTIPOS.TI_ENTERO;   
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;  
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;  
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;  
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TEXTO        COMPROBANTE_CNT.TEXTO%TYPE;
    MI_CSC          DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
    MI_CENTROCOSTO  COMPROBANTE_CNT.CENTRO_COSTO%TYPE;
    MI_AUXILIAR     COMPROBANTE_CNT.AUXILIAR%TYPE;
    MI_REFERENCIA   COMPROBANTE_CNT.REFERENCIA%TYPE;
    MI_CADENAINSERTAR          CLOB DEFAULT ' ';
    MI_CUENTADETALLE           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    
BEGIN
  BEGIN
    MI_STRSQL := ' SELECT  C.NRO_DOCUMENTO, C.DESCRIPCION, C.TERCERO, C.SUCURSAL,  C.CENTRO_COSTO, C.AUXILIAR, C.REFERENCIA, COUNT(D.ANO) 
                   FROM    COMPROBANTE_CNT C 
                        INNER JOIN  DETALLE_COMPROBANTE_CNT D 
                            ON      C.COMPANIA = D.COMPANIA 
                            AND     C.TIPO = D.TIPO_CPTE 
                            AND C.NUMERO  = D.COMPROBANTE
                        INNER JOIN PLAN_CONTABLE P
                            ON      D.COMPANIA = P.COMPANIA
                            AND     D.ANO = P.ANO
                            AND     D.CUENTA = P.CODIGO
                   WHERE       C.COMPANIA      = ''' || UN_COMPANIA || ''' 
                   AND         C.ANO           = ' || UN_ANO || '
                   AND         C.TIPO          = ''' || UN_TIPOCPTE ||'''
                   AND         C.NUMERO        = '|| UN_NROCPTE ||'
                   AND         P.CLASECUENTA   = ''C''
                   GROUP BY   C.NRO_DOCUMENTO, C.DESCRIPCION, C.TERCERO, C.SUCURSAL,  C.CENTRO_COSTO, C.AUXILIAR, C.REFERENCIA';

    BEGIN
        EXECUTE IMMEDIATE MI_STRSQL 
            INTO MI_NRODOCUMENTO, MI_DESCRIPCION, MI_TERCERO, MI_SUCURSAL, MI_CENTROCOSTO, MI_AUXILIAR,  MI_REFERENCIA, MI_CANTREG;            
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CANTREG := 0;           

    END;
<<ENUMERARCPTE>>
    MI_DESCRIPCION := 'Recaudo ' || MI_DESCRIPCION || '. Fact ' || UN_NROCPTE || ', Fecha Recaudo ' || UN_FECHARECAUDO;
    IF MI_CANTREG = 0 THEN        
        MI_RESULTADO := 'El comprobante ' || UN_TIPOCPTE || ' - ' || UN_NROCPTE || ' no tiene detalles, por favor revisar';
    ELSE    
        MI_NROREC :=  PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                                      ,UN_ANIO         => UN_ANO
                                                                      ,UN_TIPO         => UN_TIPOCPTEREC
                                                                      ,UN_NUMERO       => 0
                                                                      ,UN_CENTRO_COSTO => ''); 
        
        -- CONSULTAR SI EL NUMERO DE CPTE HA SIDO PREVIAMENTE USADO
        MI_STRSQL := ' SELECT COUNT(ANO) 
                       FROM   COMPROBANTE_CNT
                       WHERE  COMPANIA  = ''' || UN_COMPANIA ||'''
                       AND    ANO       = ' || UN_ANO || '
                       AND    TIPO      = ''' || UN_TIPOCPTEREC ||'''
                       AND    NUMERO    = ' || MI_NROREC;
        BEGIN
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_CANTREG;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CANTREG :=0;
        END;
        
        IF MI_CANTREG <> 0 THEN
            GOTO ENUMERARCPTE;
        END IF;
        
        -- CREACION DEL HEADER DEL COMPROBANTE DE RECAUDO
        BEGIN
            MI_TABLA    :=  'COMPROBANTE_CNT';
            MI_CAMPOS   := 'COMPANIA, ANO, TIPO, NUMERO, FECHA, HORA, FECHA_VCN_DOC, DESCRIPCION, TEXTO, TERCERO, SUCURSAL, 
                            VLR_BASE, ANULADO, NRO_DOCUMENTO, FECHAPAGADOGN, VLR_DOCUMENTO, VLRAGIRAR, BANCO, CUENTABANCO, DEBITO, CREDITO, 
                            CREATED_BY, DATE_CREATED ';
            MI_VALORES  := '''' || UN_COMPANIA             || ''','
                                || UN_ANO || ', '''
                                || UN_TIPOCPTEREC || ''', '
                                || MI_NROREC || ',
                                SYSDATE, 
                                SYSDATE, '''
                                || UN_FECHAVCTO || ''', '''
                                || MI_DESCRIPCION || ''', '''
                                || MI_TEXTO || ''', '''
                                || MI_TERCERO || ''', '''
                                || MI_SUCURSAL || ''', '
                                || UN_VLRCPTE || ', 
                                0, '''
                                || UN_FECHARECAUDO || ''', TO_DATE('''
                                || UN_FECHARECAUDO || ''',''DD/MM/YYYY HH24:MI:SS''), '
                                || UN_VLRCPTE || ', '
                                || UN_VLRCPTE || ', '''
                                || UN_BANCOREC || ''', '''
                                || UN_CUENTABANCO  || ''', '
                                || UN_VLRCPTE || ', '
                                || UN_VLRCPTE || ', '''
                                || UN_USUARIO   || ''',
                               SYSDATE ';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                               UN_ACCION  =>  'I',
                                                               UN_CAMPOS  =>  MI_CAMPOS, 
                                                               UN_VALORES =>  MI_VALORES);
                    MI_CANTREG := 1;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_CANTREG := 0;
                        MI_RESULTADO := 'E. Inconvenientes al crear el header del comprobante';
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INSCOMPCONTABLE
                               ,UN_TABLAERROR => MI_TABLA);
            END;
            
            IF MI_CANTREG = 1 THEN
                -- SE DEBE CREAR LOS DETALLES DEL COMPROBANTE, INICIANDO POR EL VALOR AL DEBITO QUE CORRESPONDE AL TOTAL DE LA FACTURA 
                MI_CSC := 1;
                MI_TABLA    :=  'DETALLE_COMPROBANTE_CNT';
                MI_CAMPOS   := 'COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA, FECHA, HORA, NATURALEZA, 
                                VALOR_DEBITO, VALOR_CREDITO, EJECUCION_DEBITO, EJECUCION_CREDITO, DESCRIPCION, CENTRO_COSTO,
                                TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, NRO_DOCUMENTO, FECHA_CONSIGNACIONPLANO, BASE_GRAVABLE,
                                CREATED_BY, DATE_CREATED ';
                MI_VALORES  := '''' || UN_COMPANIA             || ''','
                                    || UN_ANO || ', '''
                                    || UN_TIPOCPTEREC || ''', '
                                    || MI_NROREC || ','
                                    || MI_CSC ||', '''
                                    || UN_CUENTABANCO || ''', 
                                    SYSDATE, 
                                    SYSDATE, '''
                                    || UN_NATCTABANCO ||''', '
                                    || UN_VLRCPTE || ', 
                                    0, '
                                    || UN_VLRCPTE || ', 
                                    0, ''' 
                                    || MI_DESCRIPCION || ''', '''
                                    || MI_CENTROCOSTO || ''', '''
                                    || MI_TERCERO || ''', '''
                                    || MI_SUCURSAL || ''', '''
                                    || MI_AUXILIAR ||''', '''
                                    || MI_REFERENCIA ||''','''
                                    || MI_NRODOCUMENTO || ''',TO_DATE('''
                                    || UN_FECHARECAUDO || ''',''DD/MM/YYYY HH24:MI:SS''),
                                    0, '''
                                    || UN_USUARIO   || ''',
                                   SYSDATE ';
                BEGIN
                    BEGIN
                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                   UN_ACCION  =>  'I',
                                                                   UN_CAMPOS  =>  MI_CAMPOS, 
                                                                   UN_VALORES =>  MI_VALORES);
                        MI_CANTREG := 1;
                        
                        FOR RSDETCPTE IN (
                                        SELECT  D.ANO, D.CONSECUTIVO, D.CUENTA, D.NATURALEZA, D.DESCRIPCION, D.NRO_DOCUMENTO, D.TERCERO, D.SUCURSAL, D.CENTRO_COSTO, D.AUXILIAR, D.REFERENCIA, D.VALOR_CREDITO, D.VALOR_DEBITO
                                         FROM   DETALLE_COMPROBANTE_CNT D 
                                            LEFT JOIN COMPROBANTE_CNT C
                                              ON  D.COMPANIA = C.COMPANIA
                                              AND D.ANO = C.ANO
                                              AND D.TIPO_CPTE= C.TIPO
                                              AND D.COMPROBANTE = C.NUMERO
                                           INNER JOIN PLAN_CONTABLE P
                                              ON  D.COMPANIA = P.COMPANIA
                                             AND  D.ANO = P.ANO
                                             AND  D.CUENTA = P.CODIGO
                                           WHERE  D.COMPANIA =  UN_COMPANIA
                                           AND    D.TIPO_CPTE = UN_TIPOCPTE
                                           AND    D.COMPROBANTE = UN_NROCPTE
                                           AND    P.CLASECUENTA = 'C'                                        
                                          )                        
                            LOOP
                                MI_CSC := MI_CSC + 1;
                                MI_TABLA    :=  'DETALLE_COMPROBANTE_CNT';
                                MI_CAMPOS   := 'COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA, FECHA, HORA, NATURALEZA, 
                                                VALOR_DEBITO, VALOR_CREDITO, EJECUCION_DEBITO, EJECUCION_CREDITO, DESCRIPCION, CENTRO_COSTO,
                                                TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, NRO_DOCUMENTO, FECHA_CONSIGNACIONPLANO, BASE_GRAVABLE,
                                                TIPO_CPTE_AFECT, ANO_AFECT,  CMPTE_AFECTADO, CONSECUTIVOAFECTADO, CREATED_BY, DATE_CREATED ';
                                MI_VALORES  := '''' || UN_COMPANIA             || ''','
                                                    || UN_ANO || ', '''
                                                    || UN_TIPOCPTEREC || ''', '
                                                    || MI_NROREC || ','
                                                    || MI_CSC ||', '''
                                                    || RSDETCPTE.CUENTA || ''', 
                                                    SYSDATE, 
                                                    SYSDATE, '''
                                                    || RSDETCPTE.NATURALEZA || ''',
                                                    0, '
                                                    || UN_VLRCPTE || ', 
                                                    0, '
                                                    || UN_VLRCPTE || ', '''
                                                    || MI_DESCRIPCION || ''', '''
                                                    || MI_CENTROCOSTO || ''', '''
                                                    || MI_TERCERO || ''', '''
                                                    || MI_SUCURSAL || ''', '''
                                                    || MI_AUXILIAR ||''', '''
                                                    || MI_REFERENCIA ||''','''
                                                    || RSDETCPTE.NRO_DOCUMENTO ||''', TO_DATE('''
                                                    || UN_FECHARECAUDO || ''',''DD/MM/YYYY HH24:MI:SS''),
                                                    0, '''
                                                    || UN_TIPOCPTE || ''', '
                                                    || RSDETCPTE.ANO ||', '
                                                    || UN_NROCPTE ||', '
                                                    || RSDETCPTE.CONSECUTIVO   || ', '''
                                                    || UN_USUARIO   || ''',
                                                   SYSDATE';
                                BEGIN
                                    BEGIN
                                        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>  MI_TABLA, 
                                                                                   UN_ACCION  =>  'I',
                                                                                   UN_CAMPOS  =>  MI_CAMPOS, 
                                                                                   UN_VALORES =>  MI_VALORES);
                                        MI_CANTREG := 1;
                                        
                                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                            MI_CANTREG := 0;
                                            MI_RESULTADO := 'E. Inconvenientes al crear el detalle del comprobante';
                                            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                                    END;
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                                        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                                    UN_EXC_COD    => SQLCODE
                                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INSCOMPCONTABLE
                                                   ,UN_TABLAERROR => MI_TABLA);
                                END;
                        END LOOP;
                        MI_RESULTADO := 'Comprobante pago: ' || UN_TIPOCPTEREC || ' - ' || MI_NROREC ;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            MI_CANTREG := 0;
                            MI_RESULTADO := 'E. Inconvenientes al crear el detalle de la cuenta de banco';
                            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(    
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INSCOMPCONTABLE
                                   ,UN_TABLAERROR => MI_TABLA);
                END;
                
            END IF;
        END;
    END IF;

    -- CREAR COMPROBANTE PRESUPUESTAL 
    BEGIN
        MI_CADENAINSERTAR := '';
        MI_CUENTADETALLE := 0;
        FOR RS IN(
              SELECT CONSECUTIVO || ',' || CUENTA || ',' || VALOR || ',' || RUBRO_PPTAL ||  ';' LISTA_CNT
              FROM(
                  SELECT D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE,D.CONSECUTIVO,P.CODIGO CUENTA,
                         CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END VALOR,
                         MIN(C.RUBRO) RUBRO_PPTAL, COUNT(D.COMPANIA) CONTADOR
                  FROM DETALLE_COMPROBANTE_CNT D INNER JOIN PLAN_CONTABLE P
                        ON D.COMPANIA = P.COMPANIA
                        AND D.ANO      = P.ANO
                        AND D.CUENTA   = P.CODIGO
                      INNER JOIN PLAN_PPTAL_CUENTACNT C
                        ON P.COMPANIA    = C.COMPANIA
                        AND P.ANO         = C.ANO
                        AND P.CODIGO      = C.CUENTA_CONTABLE
                  WHERE D.COMPANIA  = UN_COMPANIA
                    AND D.ANO       = UN_ANO
                    AND D.TIPO_CPTE = UN_TIPOCPTEREC
                    AND D.COMPROBANTE = MI_NROREC
                  GROUP BY D.COMPANIA, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, P.CODIGO,
                  CASE WHEN P.NATURALEZA='D' THEN D.VALOR_DEBITO - D.VALOR_CREDITO ELSE D.VALOR_CREDITO - D.VALOR_DEBITO END
                  ORDER BY CONSECUTIVO)
                  )
        LOOP
            MI_CUENTADETALLE := MI_CUENTADETALLE + 1;
            MI_CADENAINSERTAR:= MI_CADENAINSERTAR || TO_CLOB(RS.LISTA_CNT);
        END LOOP;
        IF MI_CADENAINSERTAR = '' THEN
            MI_CADENAINSERTAR := ' ';
        END IF;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CADENAINSERTAR := ' ';
            MI_CUENTADETALLE := 0;
    END;
    
    IF MI_CUENTADETALLE > 0 THEN
        PCK_CONTABILIDAD1.PR_GENERARCOMPROBANTEPPTAL
                (UN_COMPANIA        => UN_COMPANIA
                ,UN_ANO             => UN_ANO
                ,UN_TIPO            => UN_TIPOCPTEREC
                ,UN_NUMERO          => MI_NROREC
                ,UN_FECHA           => UN_FECHARECAUDO
                ,UN_TERCERO         => MI_TERCERO
                ,UN_SUCURSAL        => MI_SUCURSAL
                ,UN_DESCRIPCION     => MI_DESCRIPCION
                ,UN_NUMERODOC       => MI_NRODOCUMENTO
                ,UN_VALORDOC        => UN_VLRCPTE
                ,UN_TIPOPPTAL       => UN_TIPOCPTEREC
                ,UN_CADENAINSERTAR  => MI_CADENAINSERTAR
                ,UN_CANTIDAD        => MI_CUENTADETALLE
                ,UN_USUARIO         => UN_USUARIO
                ,UN_DESDEINTERFAZ   => -1  );
    END IF;
  END;
  
  
  RETURN MI_RESULTADO;
END FC_GENERARINGRESOSTASAS;

PROCEDURE PR_RECLASIFICARNIIF_MENSUAL
    /*
      NAME              : PR_RECLASIFICARNIIF_MENSUAL
      AUTHORS           : SYSMAN  
      AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MIGRADOR     : 27/06/2023
      TIME              : 
      MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MODIFIED     : 23/07/2023
      TIME              :    
      DESCRIPTION       : Se crea el comprobante contable con el tipo de comprobante definido en la Raclasificación mensual Niif.
                          Adicionalmente se realiza la actualización del campo "realizado" en la tabla RECLASIFICACION_NIIF_MENSUAL para el número de registro que 
                          ingresa por parámetro.
      MODIFICATIONS     : Se modifica el proceso para que se cree un unico comprobante por registro de reclasificación mensual.
      PARAMETERS        : UN_COMPANIA        => Compañia de ingreso a la aplicación
                          UN_ANO         => Año del Plan Contable definido en la Reclasificación
                          UN_TIPO          => Tipo de comprobante
                          UN_NUMEROREGISTRO  => Número de registro definido en la Reclasificación
                          UN_FECHA       => Fecha en la que se realiza el cambio de cuentas
                          UN_USUARIO     => Nombre del usuario que realiza el cambio de las cuentas 

      @NAME:   reclasificarniifmensual
      @METHOD: POST     
    */ 
    (
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
      UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
      UN_TIPO             IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
      UN_NUMEROREGISTRO   IN PCK_SUBTIPOS.TI_ENTERO,
      UN_CONSECUTIVO      IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
      UN_FECHA            IN DATE,
      UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
    )
    AS 
      MI_DETALLES           PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_RS                 SYS_REFCURSOR;
      MI_RS2                 SYS_REFCURSOR;
      MI_SQLRS              PCK_SUBTIPOS.TI_STRSQL;
      MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
      MI_CODIGOANTERIOR     D_RECLASIFICAR_NIIF.CODIGOANTERIOR%TYPE;
      MI_NOMBREANTERIOR     D_RECLASIFICAR_NIIF.NOMBREANTERIOR%TYPE;
      MI_CODIGONUEVO        D_RECLASIFICAR_NIIF.CODIGONUEVO%TYPE;
      MI_NOMBRENUEVO        D_RECLASIFICAR_NIIF.NOMBRENUEVO%TYPE;
      MI_NATURALEZA         PCK_SUBTIPOS.TI_NATURALEZACONTA;
      MI_GENCONSECUTIVO     PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_CONSECUTIVO        PCK_SUBTIPOS.TI_ENTERO_LARGO;
      MI_NATURALEZACUENTA   PCK_SUBTIPOS.TI_NATURALEZACONTA;

      MI_TABLA              PCK_SUBTIPOS.TI_TABLA; 
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
      MI_PCKDATOS           PCK_SUBTIPOS.TI_RTA_ACME; 
      MI_CAMPOSINSERT       VARCHAR2(3000);
      MI_VALORESINSERT      VARCHAR2(3000);
      MI_MES                PCK_SUBTIPOS.TI_MES;
      MI_STRSQL             CLOB;

      MI_COMPANIA            PLAN_CONTABLE.COMPANIA%TYPE;
      MI_ANO                 PLAN_CONTABLE.ANO%TYPE;
      MI_CODIGO              PLAN_CONTABLE.CODIGO%TYPE;
      MI_NOMBRE              PLAN_CONTABLE.NOMBRE%TYPE;
      MI_MOVIMIENTO          PLAN_CONTABLE.MOVIMIENTO%TYPE;
      MI_MAN_CEN_CTO         PLAN_CONTABLE.MAN_CEN_CTO%TYPE;
      MI_MAN_AUX_TER         PLAN_CONTABLE.MAN_AUX_TER%TYPE;
      MI_MAN_AUX_GEN         PLAN_CONTABLE.MAN_AUX_GEN%TYPE;
      MI_TERCERO             COMPROBANTE_CNT.TERCERO%TYPE;
      MI_SUCURSAL            COMPROBANTE_CNT.SUCURSAL%TYPE;
      MI_AUXILIAR            COMPROBANTE_CNT.AUXILIAR%TYPE;
      MI_REFERENCIA_CONTABLE COMPROBANTE_CNT.REFERENCIA%TYPE;
      MI_CENTRO_COSTO        COMPROBANTE_CNT.CENTRO_COSTO%TYPE;
      MI_FUENTE_RECURSO      COMPROBANTE_CNT.FUENTE_RECURSO%TYPE;
      MI_NOMBRETERCERO       TERCERO.NOMBRE%TYPE;
      MI_NOMBREAUXILIAR      AUXILIAR.NOMBRE%TYPE;
      MI_NOMBRECENTROCOSTO   CENTRO_COSTO.NOMBRE%TYPE;
      MI_SALDO0              PLAN_CONTABLE.SALDO0%TYPE;
      MI_DEBITO              PLAN_CONTABLE.SALDO0%TYPE;
      MI_CREDITO             PLAN_CONTABLE.SALDO0%TYPE;
      MI_CONTADOR            NUMBER := 0;  

    BEGIN
        MI_MES := EXTRACT (MONTH FROM TO_DATE( UN_FECHA ,'DD/MM/YYYY'));

        -- COMPRUEBA LA EXISTENCIA DE DATOS PARA REALIZAR LA CONFIGURACIÓN
        BEGIN
          BEGIN    
             SELECT COUNT(CODIGOANTERIOR) DETALLES      
              INTO MI_DETALLES 
                FROM D_RECLASIFICAR_NIIF_MENSUAL
               WHERE COMPANIA    = UN_COMPANIA
                 AND ANO         = UN_ANO
                 AND NUMERO      = UN_NUMEROREGISTRO; -- MPEREZ TICKET7732757      

            IF MI_DETALLES = 0 
              THEN RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END IF;        
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_RECLASIFICAR);
        END;

        BEGIN
    			/* MPEREZ TICKET7732757 - Se cambia de lugar la creación del comprobante para 
    			que cree unicamente uno por reclasificación */
    			-- DATOS PARA CREACIÓN DEL COMPROBANTE
    			MI_GENCONSECUTIVO   := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'COMPROBANTE_CNT', 
                                                                                  UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' AND ANO = '||UN_ANO||' AND TIPO = '''||UN_TIPO||'''', 
                                                                                  UN_CAMPO    => 'NUMERO', UN_INICIAL => '1');
                                      
    			-- INSERCIÓN DEL COMPROBANTE    
          MI_TABLA    := 'COMPROBANTE_CNT';
          MI_CAMPOS   := 'COMPANIA,ANO,TIPO,NUMERO,DESCRIPCION,FECHA,TERCERO,SUCURSAL,DEBITO,CREDITO,ABONADO,ANULADO,CREATED_BY,DATE_CREATED';
          MI_VALORES  := ''''||UN_COMPANIA||''', '||UN_ANO||', '''||UN_TIPO||''', '||MI_GENCONSECUTIVO||', ''COMPROBANTE DE RECLASIFICACIÓN'', TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''), ''' || PCK_DATOS.FC_CONS_TERCERO || ''' , ''' || PCK_DATOS.FC_CONS_SUCURSAL || ''' ,0,0,0,0,'''||UN_USUARIO||''', TO_DATE(SYSDATE,''DD/MM/YYYY'')';
          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                       UN_ACCION  => 'I', 
                                                       UN_CAMPOS  => MI_CAMPOS, 
                                                       UN_VALORES => MI_VALORES);     
          MI_CONSECUTIVO:=0;
          MI_SQLRS :='SELECT ANO,
                             CODIGOANTERIOR,
                             NOMBREANTERIOR,
                             CODIGONUEVO,
                             NOMBRENUEVO
                        FROM D_RECLASIFICAR_NIIF_MENSUAL
                       WHERE COMPANIA    = '''||UN_COMPANIA||'''
                         AND ANO         = '||UN_ANO||'
                         AND NUMERO      = '||UN_NUMEROREGISTRO||''; -- MPEREZ TICKET7732757
          OPEN MI_RS FOR MI_SQLRS;
              LOOP
                FETCH MI_RS INTO MI_ANIO, MI_CODIGOANTERIOR, MI_NOMBREANTERIOR,MI_CODIGONUEVO, MI_NOMBRENUEVO;
                EXIT WHEN MI_RS%NOTFOUND;    
                  FOR RS1 IN(
                                  SELECT PLAN_CONTABLE.COMPANIA DATOS 
                                  FROM V_PLAN_CONTABLE PLAN_CONTABLE 
                                    LEFT JOIN TERCERO 
                                      ON  PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA
                                      AND PLAN_CONTABLE.TERCERO = TERCERO.NIT
                                      AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL 
                                    LEFT JOIN AUXILIAR 
                                      ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA
                                       AND PLAN_CONTABLE.ANO = AUXILIAR.ANO
                                      AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO
                                    LEFT JOIN CENTRO_COSTO 
                                      ON  PLAN_CONTABLE.COMPANIA = CENTRO_COSTO.COMPANIA 
                                      AND PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO
                                      AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO
                                  WHERE  PLAN_CONTABLE.COMPANIA    = UN_COMPANIA 
                                    AND  PLAN_CONTABLE.ANO         = UN_ANO
                                    AND  PLAN_CONTABLE.CODIGO      = MI_CODIGOANTERIOR
                                    AND  PLAN_CONTABLE.MOVIMIENTO  NOT IN(0)
                                    AND  PLAN_CONTABLE.SALDO0      <>0
                                  GROUP BY PLAN_CONTABLE.COMPANIA
                  )
                  LOOP      
                      MI_NATURALEZACUENTA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA    => MI_CODIGONUEVO);
                      
                      MI_STRSQL :=  '  SELECT PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, PLAN_CONTABLE.CODIGO, PLAN_CONTABLE.NATURALEZA, PLAN_CONTABLE.NOMBRE ' ||
                                    '       , PLAN_CONTABLE.MOVIMIENTO, PLAN_CONTABLE.MAN_CEN_CTO, PLAN_CONTABLE.MAN_AUX_TER, PLAN_CONTABLE.MAN_AUX_GEN       ' ||
                                    '       , PLAN_CONTABLE.TERCERO, PLAN_CONTABLE.SUCURSAL, PLAN_CONTABLE.AUXILIAR,PLAN_CONTABLE.REFERENCIA REFERENCIA_CONTABLE ' ||
                                    '       , PLAN_CONTABLE.CENTRO_COSTO,PLAN_CONTABLE.FUENTE_RECURSO, TERCERO.NOMBRE AS NOMBRETERCERO, AUXILIAR.NOMBRE AS NOMBREAUXILIAR  ' ||
                                    '       , CENTRO_COSTO.NOMBRE AS NOMBRECENTROCOSTO, PLAN_CONTABLE.SALDO'|| MI_MES ||' SALDO0, ' ||
                                    '    CASE WHEN PLAN_CONTABLE.NATURALEZA   = ''D'' AND SALDO'|| MI_MES ||' < 0  ' ||
                                    '        THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '        ELSE CASE WHEN PLAN_CONTABLE.NATURALEZA = ''C'' AND SALDO'|| MI_MES ||' > 0  ' ||
                                    '                  THEN ABS(SALDO'|| MI_MES ||')  ' ||
                                    '                  ELSE 0 ' ||
                                    '              END ' ||
                                    '    END  AS DEBITO, ' ||
                                    '    CASE WHEN PLAN_CONTABLE.NATURALEZA = ''D'' AND  SALDO'|| MI_MES ||' > 0   ' ||
                                    '        THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '        ELSE CASE WHEN PLAN_CONTABLE.NATURALEZA = ''C'' AND  SALDO'|| MI_MES ||'<0 ' ||
                                    '                  THEN ABS(SALDO'|| MI_MES ||')  ' ||
                                    '                  ELSE 0 ' ||
                                    '        END ' ||
                                    '    END  AS CREDITO ' ||
                                    '  FROM V_PLAN_CONTABLE PLAN_CONTABLE  ' ||
                                    '    LEFT JOIN TERCERO   ' ||
                                    '     ON   PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA ' ||
                                    '     AND PLAN_CONTABLE.TERCERO = TERCERO.NIT ' ||
                                    '     AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL ' ||
                                    '    LEFT JOIN AUXILIAR  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA ' ||
                                    '      AND PLAN_CONTABLE.ANO = AUXILIAR.ANO  ' ||
                                    '      AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO  ' ||
                                    '    LEFT JOIN CENTRO_COSTO  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = CENTRO_COSTO.COMPANIA  ' ||
                                    '      AND PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO ' ||
                                    '      AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO ' ||
                                    '  WHERE  PLAN_CONTABLE.COMPANIA    = ''' || UN_COMPANIA || '''  ' ||
                                    '    AND  PLAN_CONTABLE.ANO         = ' || UN_ANO || 
                                    '    AND  PLAN_CONTABLE.CODIGO      = ''' || MI_CODIGOANTERIOR  || '''  ' ||
                                    '    AND  PLAN_CONTABLE.MOVIMIENTO  NOT IN(0) ' ||
                                    '    AND  PLAN_CONTABLE.SALDO'|| MI_MES ||'      <>0 ' ||
                                    '  UNION  ' ||
                                    '  SELECT PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, ''' || MI_CODIGONUEVO     ||'''  CODIGO, ''' || MI_NATURALEZACUENTA     ||''' AS NATURALEZA, PLAN_CONTABLE.NOMBRE ' ||
                                    '       , PLAN_CONTABLE.MOVIMIENTO, PLAN_CONTABLE.MAN_CEN_CTO, PLAN_CONTABLE.MAN_AUX_TER, PLAN_CONTABLE.MAN_AUX_GEN, PLAN_CONTABLE.TERCERO ' ||
                                    '       , PLAN_CONTABLE.SUCURSAL, PLAN_CONTABLE.AUXILIAR, PLAN_CONTABLE.REFERENCIA REFERENCIA_CONTABLE, PLAN_CONTABLE.CENTRO_COSTO ' ||
                                    '       , PLAN_CONTABLE.FUENTE_RECURSO, TERCERO.NOMBRE AS NOMBRETERCERO, AUXILIAR.NOMBRE AS NOMBREAUXILIAR, CENTRO_COSTO.NOMBRE AS NOMBRECENTROCOSTO ' ||
                                    '       , PLAN_CONTABLE.SALDO'|| MI_MES ||' SALDO0,  ' ||
                                    '         CASE WHEN PLAN_CONTABLE.NATURALEZA =''D'' AND  SALDO'|| MI_MES ||'>0  ' ||
                                    '              THEN ABS(SALDO'|| MI_MES ||')  ' ||
                                    '              ELSE  CASE WHEN PLAN_CONTABLE.NATURALEZA=''C'' AND SALDO'|| MI_MES ||'<0 ' ||
                                    '                    THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '                    ELSE 0 ' ||
                                    '              END ' ||
                                    '         END  AS DEBITO,  ' ||
                                    '         CASE WHEN PLAN_CONTABLE.NATURALEZA =''D'' AND SALDO'|| MI_MES ||' < 0 ' ||
                                    '              THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '              ELSE CASE WHEN PLAN_CONTABLE.NATURALEZA =''C'' AND SALDO'|| MI_MES ||'>0 ' ||
                                    '                        THEN ABS(SALDO'|| MI_MES ||') ' ||
                                    '                        ELSE 0 ' ||
                                    '                   END  ' ||
                                    '         END AS CREDITO  ' ||
                                    '  FROM V_PLAN_CONTABLE PLAN_CONTABLE  ' ||
                                    '    LEFT JOIN TERCERO  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = TERCERO.COMPANIA ' ||
                                    '      AND PLAN_CONTABLE.TERCERO = TERCERO.NIT ' ||
                                    '      AND PLAN_CONTABLE.SUCURSAL = TERCERO.SUCURSAL  ' ||
                                    '    LEFT JOIN AUXILIAR  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = AUXILIAR.COMPANIA ' ||
                                    '      AND PLAN_CONTABLE.ANO = AUXILIAR.ANO ' ||
                                    '      AND PLAN_CONTABLE.AUXILIAR = AUXILIAR.CODIGO ' ||
                                    '    LEFT JOIN CENTRO_COSTO  ' ||
                                    '      ON  PLAN_CONTABLE.COMPANIA = CENTRO_COSTO.COMPANIA  ' ||
                                    '      AND PLAN_CONTABLE.ANO = CENTRO_COSTO.ANO ' ||
                                    '      AND PLAN_CONTABLE.CENTRO_COSTO = CENTRO_COSTO.CODIGO ' ||
                                    '  WHERE PLAN_CONTABLE.COMPANIA   = ''' || UN_COMPANIA || '''  ' ||
                                    '    AND PLAN_CONTABLE.ANO        = ' || UN_ANO || 
                                    '    AND PLAN_CONTABLE.CODIGO     = ''' || MI_CODIGOANTERIOR  || '''  ' ||
                                    '    AND PLAN_CONTABLE.MOVIMIENTO NOT IN(0) ' ||
                                    '    AND PLAN_CONTABLE.SALDO'|| MI_MES ||'     <>0 ';
                      OPEN MI_RS2 FOR MI_STRSQL;
                      LOOP
                      FETCH MI_RS2 INTO MI_COMPANIA,MI_ANO,MI_CODIGO,MI_NATURALEZA,MI_NOMBRE,MI_MOVIMIENTO,MI_MAN_CEN_CTO
                                       ,MI_MAN_AUX_TER,MI_MAN_AUX_GEN,MI_TERCERO,MI_SUCURSAL,MI_AUXILIAR,MI_REFERENCIA_CONTABLE
                                       ,MI_CENTRO_COSTO,MI_FUENTE_RECURSO,MI_NOMBRETERCERO,MI_NOMBREAUXILIAR,MI_NOMBRECENTROCOSTO
                                       ,MI_SALDO0,MI_DEBITO,MI_CREDITO;
                      EXIT WHEN MI_RS2%NOTFOUND;    
                          MI_CONSECUTIVO :=MI_CONSECUTIVO+1;

                          MI_CAMPOSINSERT := '      COMPANIA
                                                   ,ANO
                                                   ,TIPO_CPTE
                                                   ,COMPROBANTE
                                                   ,CONSECUTIVO
                                                   ,CUENTA
                                                   ,FECHA
                                                   ,NATURALEZA
                                                   ,DESCRIPCION
                                                   ,VALOR_DEBITO
                                                   ,VALOR_CREDITO
                                                   ,EJECUCION_DEBITO
                                                   ,EJECUCION_CREDITO
                                                   ,BASE_GRAVABLE
                                                   ,CENTRO_COSTO
                                                   ,TERCERO
                                                   ,SUCURSAL
                                                   ,AUXILIAR
                                                   ,REFERENCIA
                                                   ,FUENTE_RECURSO
                                                   ,BASE_IVA
                                                   ,DESEMBOLSO
                                                   ,SALDOCUENTA
                                                   ,DATE_CREATED
                                                   ,CREATED_BY
                                                   ,PORCENTAJERETENCION
                                              ';

                          MI_VALORES:=' ''' || UN_COMPANIA             ||'''
                                       ,  ' || MI_ANIO                 ||'
                                       ,''' || UN_TIPO                 ||'''
                                       ,  ' || MI_GENCONSECUTIVO       ||'
                                       ,  ' || MI_CONSECUTIVO          ||'
                                       ,''' || MI_CODIGO              ||'''
                                       ,TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY')  || ''', ''DD/MM/YYYY'')
                                       ,''' || MI_NATURALEZACUENTA     ||'''
                                       , ''COMPROBANTE DE RECLASIFICACION''
                                       ,  ' || NVL(MI_DEBITO,0)       ||'
                                       ,  ' || NVL(MI_CREDITO,0)      ||'
                                       ,       0
                                       ,       0
                                       ,  ' || ABS(NVL(MI_SALDO0,0))       || '
                                       ,''' || NVL(MI_CENTRO_COSTO,PCK_DATOS.FC_CONS_CENTRO)            || '''
                                       ,''' || NVL(MI_TERCERO,PCK_DATOS.FC_CONS_TERCERO)                || '''
                                       ,''' || NVL(MI_SUCURSAL,PCK_DATOS.FC_CONS_SUCURSAL)              || '''
                                       ,''' || NVL(MI_AUXILIAR,PCK_DATOS.FC_CONS_AUXILIAR)              || '''
                                       ,  ' || NVL(MI_REFERENCIA_CONTABLE,PCK_DATOS.FC_CONS_REFERENCIA) || '
                                       ,  ' || NVL(MI_FUENTE_RECURSO,PCK_DATOS.FC_CONS_FUENTE)          || '
                                       ,       0
                                       ,       0
                                       ,       0
                                       ,TO_DATE(SYSDATE, ''DD/MM/YYYY'')
                                       ,''' || UN_USUARIO              || '''
                                       , 0
                                      ';

                          BEGIN
                              BEGIN
                                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_CNT', 
                                                                        UN_ACCION  => 'I', 
                                                                        UN_CAMPOS  => MI_CAMPOSINSERT, 
                                                                        UN_VALORES => MI_VALORES);  
                          
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN

                                  RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                              END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                                      UN_EXC_COD   => SQLCODE,
                                      UN_ERROR_COD => PCK_ERRORES.ERR_CONTABILIDAD_RECLASIFICAR
                                 );
                          END;
                          MI_CONTADOR := MI_CONTADOR+1;
                      END LOOP;  
                      CLOSE MI_RS2; 
                  END LOOP;
              END LOOP;
          CLOSE MI_RS; 

              --  ACTUALIZACIÓN EN LA TABLA RECLASIFICAR NIIF DEL CAMPO REALIZADO
          IF MI_CONTADOR = 0 THEN 
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END IF;

          MI_TABLA      := 'RECLASIFICAR_NIIF_MENSUAL';
          MI_CAMPOS     := 'REALIZADO ='||-1||',MODIFIED_BY='''||UN_USUARIO||''',DATE_MODIFIED=SYSDATE';
          MI_CONDICION  := 'COMPANIA   = '''||UN_COMPANIA||''' 
                            AND ANO    =  '||MI_ANIO||'
                            AND NUMERO =  '||UN_NUMEROREGISTRO||'';
          MI_PCKDATOS   := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M',  
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;

        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>SQLCODE,
                                     UN_ERROR_COD =>PCK_ERRORES.ERR_CONTABILIDAD_RECLAS_NIIF);

    END PR_RECLASIFICARNIIF_MENSUAL;

PROCEDURE PR_ACTPAGOS_FACT
    /*
      NAME              : PR_ACTPAGOS_FACT
      AUTHORS           : LVEGA  
      TIME              : 30/01/2024
      DESCRIPTION       : Este proceso llevará los saldos de los comprobantes actualizando el valor de la SF_FACTURA en los campos NROCPTE_PAGO,  ANOCPTE_PAGO, TIPOCPTE_PAGO, FECHA_PAGO y VALOR_PAGO.
      

      @NAME:   actPagosFacturacion
      @METHOD: POST     
    */ (
        UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_FECHA_INI      IN DATE,
        UN_FECHA_FIN      IN DATE,
        UN_TIPO_COBRO_INI IN SF_FACTURA.TIPOCOBRO%TYPE,
        UN_TIPO_COBRO_FIN IN SF_FACTURA.TIPOCOBRO%TYPE,
        UN_FACTURA_INI    IN SF_FACTURA.NUMERO_FACTURA%TYPE,
        UN_FACTURA_FIN    IN SF_FACTURA.NUMERO_FACTURA%TYPE,
        UN_GENERAL        IN VARCHAR2
    ) AS

        MI_STRSQL        CLOB;
        MI_NROCPTE_PAGO  SF_FACTURA.NROCPTE_PAGO%TYPE;
        MI_ANOCPTE_PAGO  SF_FACTURA.ANOCPTE_PAGO%TYPE;
        MI_TIPOCPTE_PAGO SF_FACTURA.TIPOCPTE_PAGO%TYPE;
        MI_FECHA_PAGO    DATE;
        MI_VALOR_PAGO    SF_FACTURA.VALOR_PAGO%TYPE;
        MI_CMPTE_AFEC    SF_FACTURA.NROCPTE_PAGO%TYPE;
        MI_TIPO_COM_AFEC SF_FACTURA.TIPOCPTE_PAGO%TYPE;
        MI_ANO_AFEC      SF_FACTURA.ANO%TYPE;
        MI_TABLA         PCK_SUBTIPOS.TI_TABLA;
        MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
        MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
        MI_PCKDATOS      PCK_SUBTIPOS.TI_RTA_ACME;
        MI_RS            SYS_REFCURSOR;
        MI_FACTURA_INI   SF_FACTURA.CODIGO_COBRO%TYPE;
        MI_FACTURA_FIN   SF_FACTURA.CODIGO_COBRO%TYPE;
    BEGIN
        IF UN_GENERAL = '0' THEN
            MI_STRSQL := 'SELECT NUMERO_FACTURA,NUMERO_FACTURA AS NUM FROM SF_FACTURA WHERE COMPANIA IN ('''|| UN_COMPANIA ||''') AND CODIGO_COBRO IN (''' || UN_FACTURA_INI || ''') AND TIPOCOBRO IN ('''|| UN_TIPO_COBRO_INI ||''') ORDER BY NUMERO_FACTURA';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_FACTURA_INI, MI_FACTURA_FIN;
            
            MI_STRSQL := 'SELECT  D.COMPROBANTE, D.ANO, D.TIPO_CPTE, D.FECHA,SUM(D.VALOR_CREDITO) OVER(PARTITION BY D.CMPTE_AFECTADO) 
                          AS VALOR_CREDITO_TOTAL,D.CMPTE_AFECTADO, D.TIPO_CPTE_AFECT, D.ANO_AFECT                                       
                                  FROM  SF_FACTURA F
                                  INNER JOIN DETALLE_COMPROBANTE_CNT D
                                     ON D.COMPANIA = F.COMPANIA
                                     AND D.TIPO_CPTE_AFECT = F.TIPOCOBRO
                                     AND D.CMPTE_AFECTADO = F.CODIGO_COBRO
                                  WHERE   D.COMPANIA = ''' || UN_COMPANIA || '''  
                                  AND F.TIPO_FACTURA BETWEEN '''  || UN_TIPO_COBRO_INI || ''' AND ''' || UN_TIPO_COBRO_FIN || ''' 
                                  AND F.NUMERO_FACTURA BETWEEN ''' || MI_FACTURA_INI || ''' AND ''' || MI_FACTURA_FIN   || '''ORDER BY D.COMPROBANTE';

        ELSE
            MI_FACTURA_INI := UN_FACTURA_INI;
            MI_FACTURA_FIN := UN_FACTURA_FIN;
            MI_STRSQL := 'SELECT  D.COMPROBANTE, D.ANO, D.TIPO_CPTE, D.FECHA,SUM(D.VALOR_CREDITO) OVER(PARTITION BY D.CMPTE_AFECTADO) 
                          AS VALOR_CREDITO_TOTAL,D.CMPTE_AFECTADO, D.TIPO_CPTE_AFECT, D.ANO_AFECT                                       
                                  FROM  SF_FACTURA F
                                  INNER JOIN DETALLE_COMPROBANTE_CNT D
                                     ON D.COMPANIA = F.COMPANIA
                                     AND D.TIPO_CPTE_AFECT = F.TIPOCOBRO
                                     AND D.CMPTE_AFECTADO = F.CODIGO_COBRO
                                  WHERE   D.COMPANIA = ''' || UN_COMPANIA || ''' 
                                  AND F.FECHA_EXPEDICION BETWEEN  ''' || UN_FECHA_INI || ''' AND ''' || UN_FECHA_FIN || ''' 
                                  AND F.TIPO_FACTURA BETWEEN  ''' || UN_TIPO_COBRO_INI || ''' AND '''  || UN_TIPO_COBRO_FIN  || '''  
                                  AND F.NUMERO_FACTURA BETWEEN ''' || MI_FACTURA_INI || ''' AND ''' || MI_FACTURA_FIN || '''ORDER BY D.COMPROBANTE';
        END IF;

        OPEN MI_RS FOR MI_STRSQL;

        LOOP
            FETCH MI_RS INTO
                MI_NROCPTE_PAGO,
                MI_ANOCPTE_PAGO,
                MI_TIPOCPTE_PAGO,
                MI_FECHA_PAGO,
                MI_VALOR_PAGO,
                MI_CMPTE_AFEC,
                MI_TIPO_COM_AFEC,
                MI_ANO_AFEC;

            EXIT WHEN MI_RS%NOTFOUND;
            MI_TABLA := 'SF_FACTURA';
            MI_CAMPOS := 'NROCPTE_PAGO = '''
                         || MI_NROCPTE_PAGO
                         || ''',  ANOCPTE_PAGO = '''
                         || MI_ANOCPTE_PAGO
                         || ''',  TIPOCPTE_PAGO = '''
                         || MI_TIPOCPTE_PAGO
                         || ''',  FECHA_PAGO = TO_DATE('''
                         || TO_CHAR(MI_FECHA_PAGO, 'DD/MM/YYYY')
                         || ''',''DD/MM/YYYY''),  VALOR_PAGO = '''
                         || MI_VALOR_PAGO
                         || '''';

            MI_CONDICION := 'COMPANIA = '''
                            || UN_COMPANIA
                            || ''' AND ANO = '''
                            || MI_ANO_AFEC
                            || ''' AND TIPOCOBRO = '''
                            || MI_TIPO_COM_AFEC
                            || ''' AND CODIGO_COBRO = '''
                            || MI_CMPTE_AFEC
                            || '''';

            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, 
                                             UN_ACCION => 'M', 
                                             UN_CAMPOS => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);

        END LOOP;

        CLOSE MI_RS;
    END PR_ACTPAGOS_FACT;
    
FUNCTION FC_CAUSACIONAUTOMATICA
          /*
        NAME                    :FC_CAUSACIONAUTOMATICA 
        AUTHORS                 :MARIA CAMILA ROSERO P
        AUTHORS MIGRACION       :
        DATE MIGRADOR           :08/05/2024
        TIME                    :08:00 AM
        SOURCE MODULE           : 
        MODIFIER                :LINA PAOLA VEGA
        DATA MODIFIED           :15/05/2024
        TIME                    :
        DESCRIPCION             :
        MODIFICATIONS           :
        MODIFIER                :
        TIME                    :
        MODIFICATIONS           :

        */
        (
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
        UN_TIPO             IN COMPROBANTE_CNT.TIPO%TYPE,
        UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,   
        UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO, 
        UN_VARIABLE         IN NUMBER
         )
          RETURN PCK_SUBTIPOS.TI_LOGICO
      AS
        MI_CONSECUTIVO        TEMP_DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
        MI_CUENTADEBITO       VARCHAR2(4000 CHAR); 
        MI_CUENTADEBITOREF    VARCHAR2(4000 CHAR); 
        MI_CUENTACREDITO      VARCHAR2(4000 CHAR); 
        MI_VLR_BASE           NUMBER := 0; 
        MI_TERCERO            VARCHAR2(4000 CHAR); 
        MI_TERCERONOM         VARCHAR2(4000 CHAR); 
        MI_FECHA              DATE; 
        MI_DESCRIPCION        DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE; --JM 08/11/2024 7801577
        MI_NRODOCUMENTO       VARCHAR2(30 CHAR); 
        MI_VLRBASEIVA         NUMBER := 0;
        MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
        MI_VALORCREDITO       NUMBER := 0;
        MI_TOTALCREDITO       NUMBER := 0;
        MI_TOTALDEBITO        NUMBER := 0;
        MI_MANEJA_CAUSACION   VARCHAR2(2 CHAR);
        MI_MANEJA_RETENCIONES VARCHAR2(2 CHAR);
        MI_STRSQL             CLOB;
        MI_RSCAUSA            SYS_REFCURSOR;
        MI_SQLCAUSA           PCK_SUBTIPOS.TI_STRSQL;
        MI_NATURALEZA         PLAN_CONTABLE.NATURALEZA%TYPE;
        MI_CUENTAPPTAL        PLAN_CONTABLE.CUENTA_PPTAL%TYPE;
        MI_CAMPOS             VARCHAR2(4000 CHAR);
        MI_VALORES            VARCHAR2(4000 CHAR);
        MI_RTA                NUMBER;
        MI_STRCENTRO_COSTO    VARCHAR2(20 CHAR);
        MI_CENTRO_COSTO       VARCHAR2(20 CHAR);
        MI_SUCURSAL           VARCHAR2(20 CHAR);   
        MI_AUXILIAR           VARCHAR2(20 CHAR);
        MI_FUENTE_RECURSO     VARCHAR2(20 CHAR);
        MI_REFERENCIA         VARCHAR2(20 CHAR);
        MI_MANEJA_REF_FUEN    VARCHAR2(2 CHAR);
        MI_MANEJA_REF         VARCHAR2(2 CHAR); 
        MI_VALORP             NUMBER := 0;
        MI_VALOR_CONCEPTO     VARCHAR2(32 CHAR); 
        MI_VALOR_PROVEEDOR    VARCHAR2(32 CHAR);
        MI_CUENTA_ANTICIPO    VARCHAR2(32 CHAR);
        MI_VALOR_ANTICIPO     VARCHAR2(32 CHAR);
        MI_IND_ANTICIPO       NUMBER := 0;
        MI_TIENEDETALLES      NUMBER := 0;
        MI_RTAFUNC            PCK_SUBTIPOS.TI_LOGICO := 0;        
        
BEGIN 

		BEGIN
        	EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
        END;
    
        PCK_CONTABILIDAD3.GL_VARCAUSACION := UN_VARIABLE;
        
        MI_MANEJA_CAUSACION := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'MANEJA CAUSACION AUTOMATICA',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                            UN_FECHA_PAR => SYSDATE);
                                            
        MI_MANEJA_RETENCIONES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                            UN_NOMBRE    => 'MANEJA RETENCIONES POR TERCERO ',
                                            UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                            UN_FECHA_PAR => SYSDATE);
                                            
        MI_MANEJA_REF_FUEN := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                           UN_NOMBRE    => 'MANEJA REFERENCIA Y FUENTE EN RETENCIONES',
                                           UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                           UN_FECHA_PAR => SYSDATE);  
                                           
        MI_MANEJA_REF := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                       UN_NOMBRE    => 'MANEJA REFERENCIADO EN CAUSACION AUTOMATICA',
                                       UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                       UN_FECHA_PAR => SYSDATE);                                   
                          
-- ELIMINA DATOS DE LA TABLA TEMPORAL                                           
																							 
             EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_DETALLE_COMPROBANTE_CNT';                      

-- Obtener el consecutivo
            IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN
                   BEGIN                                           
                        SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM TEMP_DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO 
                             AND COMPROBANTE = UN_NUMERO;
                      EXCEPTION 
                        WHEN NO_DATA_FOUND THEN
                          MI_CONSECUTIVO := 0;
                   END;
                ELSE
                   IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                       BEGIN
                           SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO 
                             AND COMPROBANTE = UN_NUMERO;
                          EXCEPTION 
                            WHEN NO_DATA_FOUND THEN
                              MI_CONSECUTIVO := 0;
                       END;
                   END IF;
            END IF;
            
            BEGIN
                           SELECT COUNT(CONSECUTIVO)
                             INTO MI_TIENEDETALLES
                            FROM DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO
                             AND COMPROBANTE = UN_NUMERO;
                          EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                              MI_TIENEDETALLES := 0;
                       END;
                       
        IF MI_TIENEDETALLES <> 0 AND PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
            
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>-20001,
            UN_ERROR_COD =>PCK_ERRORES.ERR_CAUSACIONAUTOMATICADETALLE);
            
        ELSE

     --  BEGIN
    MI_SQLCAUSA := '       
          SELECT TERCERO.NOMBRE,COMPROBANTE_CNT.FECHA, COMPROBANTE_CNT.TERCERO, COMPROBANTE_CNT.SUCURSAL,
                COMPROBANTE_CNT.VLR_BASE, COMPROBANTE_CNT.VLR_BASEIVA, COMPROBANTE_CNT.DESCRIPCION,
                COMPROBANTE_CNT.NRO_DOCUMENTO,CAUSACION_AUTOMATICA.CENTROCOSTO, COMPROBANTE_CNT.AUXILIAR,
                CAUSACION_AUTOMATICA.REFERENCIADO, COMPROBANTE_CNT.FUENTE_RECURSO, TA_CONCEPTOS.CUENTADEBITO,CAUSACION_AUTOMATICA.CUENTACONTABLE,
                TA_PROVEEDORES.CUENTACREDITO,CAUSACION_AUTOMATICA.VALOR, CAUSACION_AUTOMATICA.CONCEPTO, CAUSACION_AUTOMATICA.PROVEEDOR,
                CAUSACION_AUTOMATICA.CUENTA_ANTICIPO, CAUSACION_AUTOMATICA.VALOR_ANTICIPO
            FROM COMPROBANTE_CNT 
              INNER JOIN CAUSACION_AUTOMATICA
                ON COMPROBANTE_CNT.COMPANIA =CAUSACION_AUTOMATICA.COMPANIA
                AND COMPROBANTE_CNT.ANO =CAUSACION_AUTOMATICA.ANO
                AND COMPROBANTE_CNT.NUMERO =CAUSACION_AUTOMATICA.NUMERO_COMPROBANTE
                AND COMPROBANTE_CNT.TIPO =CAUSACION_AUTOMATICA.TIPO_COMPROBANTE
              INNER JOIN TA_CONCEPTOS
                ON CAUSACION_AUTOMATICA.COMPANIA = TA_CONCEPTOS.COMPANIA
                AND CAUSACION_AUTOMATICA.ANO = TA_CONCEPTOS.ANO
                AND CAUSACION_AUTOMATICA.CONCEPTO = TA_CONCEPTOS.CODIGO  
              INNER JOIN TA_PROVEEDORES
                ON CAUSACION_AUTOMATICA.COMPANIA = TA_PROVEEDORES.COMPANIA
                AND CAUSACION_AUTOMATICA.ANO = TA_PROVEEDORES.ANO
                AND CAUSACION_AUTOMATICA.PROVEEDOR = TA_PROVEEDORES.CODIGO  
               LEFT JOIN TERCERO 
                ON COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA
                AND COMPROBANTE_CNT.TERCERO = TERCERO.NIT
                AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL  
            WHERE COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
              AND COMPROBANTE_CNT.ANO = '||UN_ANO||'
              AND COMPROBANTE_CNT.NUMERO = '||UN_NUMERO||'
              AND COMPROBANTE_CNT.TIPO = '''||UN_TIPO||''''; 
              
          OPEN MI_RSCAUSA FOR MI_SQLCAUSA;
              LOOP
                FETCH MI_RSCAUSA INTO MI_TERCERONOM, MI_FECHA, MI_TERCERO, MI_SUCURSAL, MI_VLR_BASE, MI_VLRBASEIVA, MI_DESCRIPCION, 
                	MI_NRODOCUMENTO, MI_CENTRO_COSTO, MI_AUXILIAR, MI_REFERENCIA, MI_FUENTE_RECURSO, MI_CUENTADEBITO, MI_CUENTADEBITOREF, MI_CUENTACREDITO, 
                    MI_VALORP,MI_VALOR_CONCEPTO,MI_VALOR_PROVEEDOR, MI_CUENTA_ANTICIPO, MI_VALOR_ANTICIPO;
                EXIT WHEN MI_RSCAUSA%NOTFOUND;
                
                  MI_CONSECUTIVO := MI_CONSECUTIVO + 1;              
  -- Obtener la naturaleza de la cuenta
            BEGIN
               SELECT NVL(NATURALEZA,'')
               INTO MI_NATURALEZA
                 FROM PLAN_CONTABLE 
                      WHERE COMPANIA = UN_COMPANIA
                      AND ANO      = UN_ANO 
                      AND CODIGO = CASE
                        WHEN MI_MANEJA_REF = 'SI' THEN MI_CUENTADEBITOREF
                        ELSE MI_CUENTADEBITO
                     END;
                   EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_NATURALEZA := '';
              END;
        
  -- Obtener la cuenta presupuestal  
        BEGIN
                SELECT CUENTA_PPTAL 
                INTO MI_CUENTAPPTAL
                FROM PLAN_CONTABLE 
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO      = UN_ANO 
                  AND CODIGO   = CASE
                        WHEN MI_MANEJA_REF = 'SI' THEN MI_CUENTADEBITOREF
                        ELSE MI_CUENTADEBITO
                     END;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CUENTAPPTAL := '';
          END; 

         BEGIN
            MI_CAMPOS := 'COMPANIA,
                          ANO,
                          TIPO_CPTE,
                          COMPROBANTE,
                          CONSECUTIVO,
                          CUENTA,
                          FECHA,
                          NATURALEZA,
                          VALOR_DEBITO,
                          VALOR_CREDITO,
                          BASE_GRAVABLE,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL,
                          AUXILIAR,
                          DESCRIPCION,
                          CUENTAPPTAL,
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          BASE_IVA,
                          NRO_DOCUMENTO,
                          CREATED_BY,
                          DATE_CREATED,
                          FUENTE_RECURSO,
                          REFERENCIA,
                          CONCEPTO,
                          PROVEEDOR';
            MI_VALORES:=' ''' || UN_COMPANIA || ''',
                            ' || UN_ANO || ',
                          ''' || UN_TIPO || ''',
                            ' || UN_NUMERO || ',
                            ' || MI_CONSECUTIVO || ',
                          ''' || CASE WHEN MI_MANEJA_REF = 'SI' THEN MI_CUENTADEBITOREF ELSE MI_CUENTADEBITO END || ''',
                          ''' || TO_DATE('' || MI_FECHA || '','DD/MM/YYYY') || ''',
                          ''' || MI_NATURALEZA || ''',
                            ' || MI_VALORP|| ',
                            ' || 0|| ',
                            ' || MI_VALORP || ',
                          ''' || NVL(MI_CENTRO_COSTO, '99999999999')||''',
                          ''' || NVL(MI_TERCERO, '99999999999') || ''',
                          ''' || NVL(MI_SUCURSAL,'999') || ''',
                            ' || MI_AUXILIAR||',
                          ''' || MI_DESCRIPCION || ''',
                          ''' || NVL(MI_CUENTAPPTAL,'') || ''',
                            ' || MI_VALORP|| ',
                            ' || 0|| ',
                            ' || NVL(MI_VLRBASEIVA,0) || ',
                          ''' || MI_NRODOCUMENTO || ''',
                          ''' || UN_USUARIO || ''',
                                 SYSDATE,
                          ''' || MI_FUENTE_RECURSO || ''',
                          ''' ||NVL(MI_REFERENCIA, '99999999999') || ''' ,
                          ''' || NVL(MI_VALOR_CONCEPTO,'0') || ''',
                          ''' || NVL(MI_VALOR_PROVEEDOR,'0') || '''';

                IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
                    MI_TABLA   := 'TEMP_DETALLE_COMPROBANTE_CNT';
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';
                    END IF;
                END IF;
                
                 MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD => SQLCODE,
                  UN_TABLAERROR => MI_TABLA,
                  UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INS_RETENCIONES); 
              END;


--************ RETENCIONES
  
         FOR MI_RS IN (SELECT COMPROBANTE_CNTRETENCION.TIPORETENCION,
                          COMPROBANTE_CNTRETENCION.CODIGORETENCION,
                          COMPROBANTE_CNTRETENCION.VALOR,
                          COMPROBANTE_CNTRETENCION.VALORBASE,
                          COMPROBANTE_CNTRETENCION.PORCIVA,
                          RETENCIONES.CUENTA_DEBITO,
                          RETENCIONES.CUENTA_CREDITO,
                          RETENCIONES.PCT_BASE,
                          RETENCIONES.LIMITE_INF,
                          RETENCIONES.PCT_APLICAR,
                          RETENCIONES.VALOR_APLICAR,
                          RETENCIONES.FACTORREDONDEO,
                          RETENCIONES.PERMITEMODIFICAR,
                          CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN COMPROBANTE_CNTRETENCION.CENTROCOSTO ELSE RETENCIONES.CENTRO_COSTO END CENTRO_COSTO,
                          --RETENCIONES.CENTRO_COSTO ,  --- tenemos que verificar CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN COMPROBANTE_CNTRETENCION.CENTROCOSTO ELSE RETENCIONES.CENTRO_COSTO END CENTRO_COSTO,
                          RETENCIONES.ALEY1450,
                          RETENCIONES.ALEY1819,
                          RETENCIONES.CUENTA_DEBITO1,
                          RETENCIONES.CUENTA_CREDITO1,
                          RETENCIONES.COD_AUXILIAR,
                          CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN COMPROBANTE_CNTRETENCION.REFERENCIADO ELSE RETENCIONES.COD_REFERENCIA END COD_REFERENCIA,
                          --RETENCIONES.COD_REFERENCIA, --- tenemos que verificar CASE WHEN PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0 THEN COMPROBANTE_CNTRETENCION.REFERENCIADO ELSE RETENCIONES.COD_REFERENCIA END COD_REFERENCIA,
                          RETENCIONES.COD_FUENTER,  
                          COMPROBANTE_CNTRETENCION.CONCEPTO,
                          COMPROBANTE_CNTRETENCION.PROVEEDOR
                      FROM COMPROBANTE_CNTRETENCION LEFT JOIN RETENCIONES
                        ON COMPROBANTE_CNTRETENCION.COMPANIA        = RETENCIONES.COMPANIA
                       AND COMPROBANTE_CNTRETENCION.ANO             = RETENCIONES.ANO
                       AND COMPROBANTE_CNTRETENCION.TIPORETENCION   = RETENCIONES.TIPO
                       AND COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO
                      WHERE COMPROBANTE_CNTRETENCION.COMPANIA = UN_COMPANIA
                        AND COMPROBANTE_CNTRETENCION.ANO      = UN_ANO
                        AND COMPROBANTE_CNTRETENCION.TIPO     = UN_TIPO
                        AND COMPROBANTE_CNTRETENCION.NUMERO   = UN_NUMERO
                        AND COMPROBANTE_CNTRETENCION.CONCEPTO = MI_VALOR_CONCEPTO
                        AND COMPROBANTE_CNTRETENCION.PROVEEDOR = MI_VALOR_PROVEEDOR
                        AND RETENCIONES.ALEY1819 = -1
                      ORDER  BY COMPROBANTE_CNTRETENCION.COMPANIA,
                          COMPROBANTE_CNTRETENCION.ANO,
                          COMPROBANTE_CNTRETENCION.TIPO,
                          COMPROBANTE_CNTRETENCION.NUMERO,
                          COMPROBANTE_CNTRETENCION.TIPORETENCION,
                          COMPROBANTE_CNTRETENCION.CODIGORETENCION )
        LOOP

        IF MI_RS.ALEY1819 <> 0 THEN           

            BEGIN
            MI_RTA   := PCK_CONTABILIDAD7.FC_CALCULORETENCIONLEY1819(UN_COMPANIA        => UN_COMPANIA,
                                                                  UN_MODULO          => '1',
                                                                  UN_CONSECMENSAJES  => 0,
                                                                  UN_ANO             => UN_ANO,
                                                                  UN_FECHA           => MI_FECHA,
                                                                  UN_TIPO            => UN_TIPO,
                                                                  UN_NUMERO          => UN_NUMERO,
                                                                  UN_TERCERO         => MI_TERCERO,
                                                                  UN_SUCURSAL        => MI_SUCURSAL,
                                                                  UN_NOMBRETERCERO   => MI_TERCERONOM,
                                                                  UN_VALORBASE       => NVL(MI_RS.VALORBASE, 0), -- MI_VALORP 
                                                                  UN_VALORBASEIVA    => NVL(MI_VLRBASEIVA, 0),
                                                                  UN_CONSECUTIVO     => MI_CONSECUTIVO,
                                                                  UN_TIENECONTENIDO  => 0,
                                                                  UN_CUENTA_DEBITO1  => NVL(MI_RS.CUENTA_DEBITO1, ''),
                                                                  UN_CUENTA_CREDITO1 => NVL(MI_RS.CUENTA_CREDITO1, ''),
                                                                  UN_TIPORETENCION   => NVL(MI_RS.TIPORETENCION, ''),
                                                                  UN_CUENTA_DEBITO   => NVL(MI_RS.CUENTA_DEBITO, ''),
                                                                  UN_CUENTA_CREDITO  => NVL(MI_RS.CUENTA_CREDITO, ''),
                                                                  UN_FACTORREDONDEO  => NVL(MI_RS.FACTORREDONDEO, 0),
                                                                  UN_CODIGORETENCION => NVL(MI_RS.CODIGORETENCION, ''),
                                                                  UN_DESCRIPCION     => MI_DESCRIPCION,
                                                                  UN_STRCENTRO_COSTO => MI_CENTRO_COSTO,
                                                                  UN_NRO_DOCUMENTO   => NVL(MI_NRODOCUMENTO, ''),
                                                                  UN_USUARIO         => UN_USUARIO,
                                                                  UN_CONCEPTO        => NVL(MI_RS.CONCEPTO, ''),
                                                                  UN_REFERENCIA      => NVL(MI_RS.COD_REFERENCIA, ''),
                                                                  UN_PROVEEDOR       => NVL(MI_RS.PROVEEDOR, '')); 
           END;    
		END IF;
    END LOOP; 
---VALIDAR    
            MI_RTA :=   PCK_CONTABILIDAD2.FC_CALCULORETENCIONES(  UN_COMPANIA       => UN_COMPANIA,
                                                                  UN_MODULO         => 1,
                                                                  UN_CONSECMENSAJES => MI_CONSECUTIVO,
                                                                  UN_ANO            => UN_ANO,
                                                                  UN_FECHA          => MI_FECHA,
                                                                  UN_TIPO           => UN_TIPO,
                                                                  UN_NUMERO         => UN_NUMERO,
                                                                  UN_AUXILIAR       => MI_AUXILIAR,
                                                                  UN_TERCERO        => MI_TERCERO,
                                                                  UN_SUCURSAL       => MI_SUCURSAL,
                                                                  UN_NOMBRETERCERO  => MI_TERCERONOM,
                                                                  UN_VALORBASE      => NVL(MI_VLR_BASE, 0),
                                                                  UN_VALORBASEIVA   => 0,
                                                                  UN_DESCRIPCION    => MI_DESCRIPCION,
                                                                  UN_CENTROCOSTO    => MI_CENTRO_COSTO,
                                                                  UN_NRODOCUMENTO   => MI_NRODOCUMENTO,
                                                                  UN_USUARIO        => UN_USUARIO,
                                                                  UN_FUENTER        => MI_FUENTE_RECURSO,
                                                                  UN_REFERENCIA     => MI_REFERENCIA,
                                                                  UN_CONCEPTO        => NVL(MI_VALOR_CONCEPTO, ''),
                                                                  UN_PROVEEDOR       => NVL(MI_VALOR_PROVEEDOR, ''),
                                                                  UN_LIMPIAR_TABLA  => CASE WHEN MI_CONSECUTIVO <> 0 THEN 0 ELSE 1 END);

-- VALIDAR SI EL COMPROBANTE TIENE ACTIVO EL INDICADOR DE ANTICIPO Y EN CASO QUE SI HACER EL CALCULO EN EL CREDITO.

        BEGIN
            SELECT IND_ANTICIPO
                INTO MI_IND_ANTICIPO
              FROM COMPROBANTE_CNT
                WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                      AND COMPROBANTE_CNT.ANO = UN_ANO
                      AND COMPROBANTE_CNT.NUMERO = UN_NUMERO
                      AND COMPROBANTE_CNT.TIPO = UN_TIPO;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_IND_ANTICIPO := '';
        END;  

            IF MI_IND_ANTICIPO <> 0 AND MI_VALOR_ANTICIPO <> 0 THEN

                IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN
                   BEGIN
                        SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM TEMP_DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO
                             AND COMPROBANTE = UN_NUMERO;
                      EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                          MI_CONSECUTIVO := 0;
                   END;
                ELSE
                   IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                       BEGIN
                           SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO
                             AND COMPROBANTE = UN_NUMERO;
                          EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                              MI_CONSECUTIVO := 0;
                       END;
                   END IF;
            END IF;

                --CARGAR EK CREDITO
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;

                BEGIN
               SELECT NVL(NATURALEZA,'')
               INTO MI_NATURALEZA
                 FROM PLAN_CONTABLE
                      WHERE COMPANIA = UN_COMPANIA
                      AND ANO      = UN_ANO
                      AND CODIGO   = MI_CUENTA_ANTICIPO;
                   EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_NATURALEZA := '';
              END;


                BEGIN
                MI_CAMPOS := 'COMPANIA,
                              ANO,
                              TIPO_CPTE,
                              COMPROBANTE,
                              CONSECUTIVO,
                              CUENTA,
                              FECHA,
                              NATURALEZA,
                              VALOR_DEBITO,
                              VALOR_CREDITO,
                              BASE_GRAVABLE,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              AUXILIAR,
                              DESCRIPCION,
                              CUENTAPPTAL,
                              EJECUCION_DEBITO,
                              EJECUCION_CREDITO,
                              BASE_IVA,
                              NRO_DOCUMENTO,
                              CREATED_BY,
                              DATE_CREATED,
                              FUENTE_RECURSO,
                              REFERENCIA,
                              CONCEPTO,
                              PROVEEDOR';
                MI_VALORES:=' ''' || UN_COMPANIA || ''',
                                ' || UN_ANO || ',
                              ''' || UN_TIPO || ''',
                                ' || UN_NUMERO || ',
                                ' || MI_CONSECUTIVO || ',
                              ''' || MI_CUENTA_ANTICIPO || ''',
                              ''' || TO_DATE('' || MI_FECHA || '','DD/MM/YYYY') || ''',
                              ''' || MI_NATURALEZA || ''',
                                ' || 0|| ',
                                ' || MI_VALOR_ANTICIPO|| ',
                                ' || MI_VALORP || ',
                              ''' || NVL(MI_CENTRO_COSTO, '99999999999')||''',
                              ''' || NVL(MI_TERCERO, '99999999999') || ''',
                              ''' || NVL(MI_SUCURSAL,'999') || ''',
                                ' || MI_AUXILIAR||',
                              ''' || MI_DESCRIPCION || ''',
                              ''' || NVL(MI_CUENTAPPTAL,'') || ''',
                                ' || 0|| ',
                                ' || MI_TOTALCREDITO|| ',
                                ' || NVL(MI_VLRBASEIVA,0) || ',
                              ''' || MI_NRODOCUMENTO || ''',
                              ''' || UN_USUARIO || ''',
                                     SYSDATE,
                              ''' || MI_FUENTE_RECURSO || ''',
                               ''' ||NVL(MI_REFERENCIA, '99999999999') || ''' ,
                              ''' || NVL(MI_VALOR_CONCEPTO,'0') || ''',
                              ''' || NVL(MI_VALOR_PROVEEDOR,'0') || '''';

                IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN
                    MI_TABLA   := 'TEMP_DETALLE_COMPROBANTE_CNT';
                ELSE
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';
                    END IF;
                END IF;

                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                               UN_ACCION  => 'I',
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_TABLAERROR => MI_TABLA,
              UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INS_RETENCIONES);
          END;

            END IF;


--- VALOR CREDITO
             IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
                   SELECT SUM(VALOR_CREDITO) 
                    INTO MI_VALORCREDITO
                    FROM TEMP_DETALLE_COMPROBANTE_CNT
                      WHERE TEMP_DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                          AND TEMP_DETALLE_COMPROBANTE_CNT.ANO = UN_ANO
                          AND TEMP_DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NUMERO
                          AND TEMP_DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPO
                          AND TEMP_DETALLE_COMPROBANTE_CNT.CENTRO_COSTO = MI_CENTRO_COSTO
                          AND TEMP_DETALLE_COMPROBANTE_CNT.REFERENCIA = MI_REFERENCIA
                          AND TEMP_DETALLE_COMPROBANTE_CNT.CONCEPTO=MI_VALOR_CONCEPTO
                          AND TEMP_DETALLE_COMPROBANTE_CNT.PROVEEDOR=MI_VALOR_PROVEEDOR
                    GROUP BY TERCERO;                    
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        SELECT SUM(VALOR_CREDITO) 
                            INTO MI_VALORCREDITO
                        FROM DETALLE_COMPROBANTE_CNT
                          WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                              AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANO
                              AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NUMERO
                              AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPO
                              AND DETALLE_COMPROBANTE_CNT.CENTRO_COSTO = MI_CENTRO_COSTO
                              AND DETALLE_COMPROBANTE_CNT.REFERENCIA = MI_REFERENCIA
                              AND DETALLE_COMPROBANTE_CNT.CONCEPTO=MI_VALOR_CONCEPTO
                              AND DETALLE_COMPROBANTE_CNT.PROVEEDOR=MI_VALOR_PROVEEDOR
                        GROUP BY TERCERO;
                    END IF;
                END IF;           
        
                 MI_TOTALCREDITO := MI_VALORP - MI_VALORCREDITO;
                
-- Obtener el consecutivo
            IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN
                   BEGIN                                           
                        SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM TEMP_DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO 
                             AND COMPROBANTE = UN_NUMERO;
                      EXCEPTION 
                        WHEN NO_DATA_FOUND THEN
                          MI_CONSECUTIVO := 0;
                   END;
                ELSE
                   IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                       BEGIN
                           SELECT NVL(MAX(CONSECUTIVO),0)
                             INTO MI_CONSECUTIVO
                            FROM DETALLE_COMPROBANTE_CNT
                           WHERE COMPANIA    = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO 
                             AND COMPROBANTE = UN_NUMERO;
                          EXCEPTION 
                            WHEN NO_DATA_FOUND THEN
                              MI_CONSECUTIVO := 0;
                       END;
                   END IF;
            END IF;    
            
                  MI_CONSECUTIVO := MI_CONSECUTIVO + 1;               
                
   -- Obtener la naturaleza de la cuenta
           BEGIN
               SELECT NVL(NATURALEZA,'')
               INTO MI_NATURALEZA
                 FROM PLAN_CONTABLE 
                      WHERE COMPANIA = UN_COMPANIA
                      AND ANO      = UN_ANO 
                      AND CODIGO   = MI_CUENTACREDITO;
                   EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_NATURALEZA := '';
              END; 
              
            BEGIN
                    SELECT CUENTA_PPTAL 
                    INTO MI_CUENTAPPTAL
                    FROM PLAN_CONTABLE 
                    WHERE COMPANIA = UN_COMPANIA
                      AND ANO      = UN_ANO 
                      AND CODIGO   = MI_CUENTACREDITO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CUENTAPPTAL := '';
              END; 
              
         BEGIN
            MI_CAMPOS := 'COMPANIA,
                          ANO,
                          TIPO_CPTE,
                          COMPROBANTE,
                          CONSECUTIVO,
                          CUENTA,
                          FECHA,
                          NATURALEZA,
                          VALOR_DEBITO,
                          VALOR_CREDITO,
                          BASE_GRAVABLE,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL,
                          AUXILIAR,
                          DESCRIPCION,
                          CUENTAPPTAL,
                          EJECUCION_DEBITO,
                          EJECUCION_CREDITO,
                          BASE_IVA,
                          NRO_DOCUMENTO,
                          CREATED_BY,
                          DATE_CREATED,
                          FUENTE_RECURSO,
                          REFERENCIA,
                          CONCEPTO,
                          PROVEEDOR';
            MI_VALORES:=' ''' || UN_COMPANIA || ''',
                            ' || UN_ANO || ',
                          ''' || UN_TIPO || ''',
                            ' || UN_NUMERO || ',
                            ' || MI_CONSECUTIVO || ',
                          ''' || MI_CUENTACREDITO || ''',
                          ''' || TO_DATE('' || MI_FECHA || '','DD/MM/YYYY') || ''',
                          ''' || MI_NATURALEZA || ''',
                            ' || 0|| ',
                            ' || MI_TOTALCREDITO|| ',
                            ' || MI_VALORP || ',
                          ''' || NVL(MI_CENTRO_COSTO, '99999999999')||''',
                          ''' || NVL(MI_TERCERO, '99999999999') || ''',
                          ''' || NVL(MI_SUCURSAL,'999') || ''',
                            ' || MI_AUXILIAR||',
                          ''' || MI_DESCRIPCION || ''',
                          ''' || NVL(MI_CUENTAPPTAL,'') || ''',
                            ' || 0|| ',
                            ' || MI_TOTALCREDITO|| ',
                            ' || NVL(MI_VLRBASEIVA,0) || ',
                          ''' || MI_NRODOCUMENTO || ''',
                          ''' || UN_USUARIO || ''',
                                 SYSDATE,
                          ''' || MI_FUENTE_RECURSO || ''',
                           ''' ||NVL(MI_REFERENCIA, '99999999999') || ''' ,
                          ''' || NVL(MI_VALOR_CONCEPTO,'0') || ''',
                          ''' || NVL(MI_VALOR_PROVEEDOR,'0') || '''';

                IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
                    MI_TABLA   := 'TEMP_DETALLE_COMPROBANTE_CNT';
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';
                    END IF;
                END IF;
            
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );
          
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_TABLAERROR => MI_TABLA,
              UN_ERROR_COD => PCK_ERRORES.ERR_CONTAB_INS_RETENCIONES); 
          END;
       END LOOP;   
              IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                PCK_CONTABILIDAD6.PR_CALCULAR_VALORAGIRAR( UN_COMPANIA            => UN_COMPANIA
                                      ,UN_ANIO                 => UN_ANO
                                      ,UN_TIPO                 => UN_TIPO
                                      ,UN_NUMERO               => UN_NUMERO
                                      ,UN_VLRDOCUMENTO         => 0
                                      ,UN_VLRGIRARDG           => 0);
              END IF;
        END IF; 
      RETURN MI_RTAFUNC;
END FC_CAUSACIONAUTOMATICA;

PROCEDURE PR_VALCAUSACIONAUTOMATICA
/*
NAME              : PR_VALCAUSACIONAUTOMATICA
AUTHORS           : MROSERO
AUTHOR MIGRACION  : MARIA CAMILA ROSERO
DATE MIGRADOR     : 12/07/2024
TIME              : 4:16 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Validacion que los auxiliares presupuestales coincidan con los auxiliares contables 
                    al igual que los saldos por auxiliar en el proceso de causacion automatica 
MODIFICATIONS     :
PARAMETERS        : UN_COMPANIA      => Compania de ingreso a la aplicacion
                    UN_ANO           => Ano de la creracion del comprobante
                    UN_TIPO          => tipo de la creracion del comprobante
                    UN_USUARIO       => usuario de la creracion del comprobante
--NAME:  validacionCausacionAutomatica
--METHOD:  POST
*/
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO             IN COMPROBANTE_CNT.TIPO%TYPE,
    UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
) AS

    MI_SQLCAUSA         PCK_SUBTIPOS.TI_STRSQL;
    MI_RSCAUSA          SYS_REFCURSOR;
    MI_TIPO             VARCHAR2(20 CHAR);
    MI_NUMERO           VARCHAR2(20 CHAR); 
    MI_VALOR            NUMBER;
    MI_CENTROCOSTO      VARCHAR2(20 CHAR); 
    MI_AUXILIAR         VARCHAR2(20 CHAR);
    MI_FUENTE           VARCHAR2(20 CHAR); 
    MI_REFERENCIA       VARCHAR2(20 CHAR);
    
    MI_SQLCNT           PCK_SUBTIPOS.TI_STRSQL;
    MI_RSCNT            SYS_REFCURSOR;
    MI_SUM_VALOR_DEBITO NUMBER;
    MI_SUM_VALOR_CREDITO NUMBER;
    MI_REFERENCIA_CNT   VARCHAR2(20 CHAR);
    MI_CENTROCOSTO_CNT  VARCHAR2(20 CHAR);
    
    TOTAL_VALOR_DEBITO_PPTAL NUMBER;
    TOTAL_VALOR_DEBITO_CNT   NUMBER;
    
    MI_TOTALPPTAL   NUMBER;
    MI_TOTALCNT   NUMBER;
    MI_IND_ANTICIPO NUMBER := 0;

BEGIN

    BEGIN 
            SELECT TOTAL_PPTAL
            INTO MI_TOTALPPTAL
             FROM (
                 SELECT COUNT(*) OVER () AS TOTAL_PPTAL
                FROM
                    TEMP_DETALLE_COMPROBANTE_PPTAL                        
                WHERE 
                    COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANO
                    AND COMPROBANTE = UN_NUMERO
                    AND TIPO_CPTE = UN_TIPO
                GROUP BY CENTRO_COSTO, REFERENCIA)
            GROUP BY TOTAL_PPTAL;
             
             SELECT TOTAL_CNT   
             INTO MI_TOTALCNT  
             FROM (
                 SELECT COUNT(*) OVER () AS TOTAL_CNT
                    FROM
                        TEMP_DETALLE_COMPROBANTE_CNT                        
                    WHERE 
                        COMPANIA = UN_COMPANIA
                        AND ANO = UN_ANO
                        AND COMPROBANTE = UN_NUMERO
                        AND TIPO_CPTE = UN_TIPO
                    GROUP BY CENTRO_COSTO, REFERENCIA)
                GROUP BY TOTAL_CNT;
                
            EXCEPTION WHEN NO_DATA_FOUND THEN
             MI_TOTALPPTAL := 0;
             MI_TOTALCNT := -1;
      END;    
        
         IF MI_TOTALPPTAL <> MI_TOTALCNT THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>-20001,
            UN_ERROR_COD =>PCK_ERRORES.ERR_CAUSACIONAUTOMATICAREF);
        END IF;
                
    
   -- Consulta SQL para TEMP_DETALLE_COMPROBANTE_PPTAL
    MI_SQLCAUSA := 'SELECT
                    SUM(VALOR_DEBITO) AS TOTAL_VALOR_DEBITO,
                    CENTRO_COSTO, 
                    REFERENCIA,
                    COUNT(*) OVER () AS TOTAL_PPTAL
                FROM
                    TEMP_DETALLE_COMPROBANTE_PPTAL                        
                WHERE 
                    COMPANIA = '''||UN_COMPANIA||'''
                    AND ANO = '||UN_ANO||'
                    AND COMPROBANTE = '||UN_NUMERO||'
                    AND TIPO_CPTE = '''||UN_TIPO||'''
                GROUP BY CENTRO_COSTO, REFERENCIA';
                    
    OPEN MI_RSCAUSA FOR MI_SQLCAUSA;
    LOOP
        FETCH MI_RSCAUSA INTO TOTAL_VALOR_DEBITO_PPTAL, MI_CENTROCOSTO, MI_REFERENCIA, MI_TOTALPPTAL;
        EXIT WHEN MI_RSCAUSA%NOTFOUND;

        -- Consulta SQL para TEMP_DETALLE_COMPROBANTE_CNT
        MI_SQLCNT := 'SELECT SUM(VALOR_DEBITO) AS TOTAL_VALOR_DEBITO_CNT,
                             CENTRO_COSTO, 
                             REFERENCIA,
                             COUNT(*) OVER () AS TOTAL_CNT
                      FROM TEMP_DETALLE_COMPROBANTE_CNT
                      WHERE COMPANIA = '''||UN_COMPANIA||'''
                        AND ANO = '||UN_ANO||'
                        AND COMPROBANTE = '||UN_NUMERO||'
                        AND TIPO_CPTE = '''||UN_TIPO||'''
                        AND CENTRO_COSTO = '''||MI_CENTROCOSTO||'''
                        AND REFERENCIA ='''||MI_REFERENCIA||'''
                      GROUP BY CENTRO_COSTO, REFERENCIA';
                      
        OPEN MI_RSCNT FOR MI_SQLCNT;
        FETCH MI_RSCNT INTO TOTAL_VALOR_DEBITO_CNT, MI_CENTROCOSTO_CNT, MI_REFERENCIA_CNT, MI_TOTALCNT;
        IF MI_RSCNT%NOTFOUND THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>-20001,
            UN_ERROR_COD =>PCK_ERRORES.ERR_CAUSACIONAUTOMATICAREF);
             EXIT WHEN MI_RSCNT%NOTFOUND;
        END IF;        
    
        -- Validaciones

        BEGIN
            SELECT IND_ANTICIPO
                INTO MI_IND_ANTICIPO
              FROM COMPROBANTE_CNT
                WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
                      AND COMPROBANTE_CNT.ANO = UN_ANO
                      AND COMPROBANTE_CNT.NUMERO = UN_NUMERO
                      AND COMPROBANTE_CNT.TIPO = UN_TIPO;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_IND_ANTICIPO := '';
        END;

            IF MI_CENTROCOSTO <> MI_CENTROCOSTO_CNT THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>-20001,
            UN_ERROR_COD =>PCK_ERRORES.ERR_CAUSACIONAUTOMATICACENTRO);
        END IF;
        
        IF MI_REFERENCIA <> MI_REFERENCIA_CNT THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>-20001,
            UN_ERROR_COD =>PCK_ERRORES.ERR_CAUSACIONAUTOMATICAREF);
        END IF;
            
        IF MI_IND_ANTICIPO = 0 THEN

            IF TOTAL_VALOR_DEBITO_PPTAL <> TOTAL_VALOR_DEBITO_CNT THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   =>-20001,
                UN_ERROR_COD =>PCK_ERRORES.ERR_CAUSACIONAUTOMATICASALDO);
            END IF;

        END IF;
        
        CLOSE MI_RSCNT;
    END LOOP;
    CLOSE MI_RSCAUSA;                             
END PR_VALCAUSACIONAUTOMATICA;

FUNCTION FC_PASAR_TASAS_INT
/*
NAME              : FC_PASAR_TASAS_INT
AUTHORS           : LVEGA
AUTHOR MIGRACION  : 
DATE MIGRADOR     : 26/05/2025
TIME              : 
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :Inserta tasas de interes del año origen al año destino que vienen desde controlador
--NAME:  pasarTasasInteres
--METHOD: GET
*/
(
  UN_ANIO_INI       IN PCK_SUBTIPOS.TI_ANIO,
  UN_ANIO_FIN       IN PCK_SUBTIPOS.TI_ANIO,
  UN_USUARIO        IN VARCHAR2
)RETURN NUMBER
AS
  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
  MI_ERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RTA           NUMBER := 0;
BEGIN
 
    MI_CAMPOS  := 'FECHA_INICIAL, FECHA_FINAL, ANO, CUENTA_CONTABLE, TASA, RESOLUCION, FECHA_RESOLUCION, TASA_TES, CREATED_BY, DATE_CREATED';
    MI_VALORES := 'SELECT TO_CHAR(TO_DATE('''|| UN_ANIO_FIN ||''' || TO_CHAR(FECHA_INICIAL, ''-MM-DD HH24:MI:SS''), ''YYYY-MM-DD HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') FECHA_INICIAL,
                          TO_CHAR(TO_DATE('''|| UN_ANIO_FIN ||''' || TO_CHAR(FECHA_FINAL, ''-MM-DD HH24:MI:SS''), ''YYYY-MM-DD HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') FECHA_FINAL, 
                          '''|| UN_ANIO_FIN ||''' ANO, CUENTA_CONTABLE,TASA, RESOLUCION, FECHA_RESOLUCION, TASA_TES, '''|| UN_USUARIO ||''', SYSDATE
                   FROM TASAS_INTERES 
                   WHERE TO_CHAR(TO_DATE('''|| UN_ANIO_FIN ||''' || TO_CHAR(FECHA_INICIAL, ''-MM-DD HH24:MI:SS''), ''YYYY-MM-DD HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') ||
                                                                     TO_CHAR(TO_DATE('''|| UN_ANIO_FIN ||''' || TO_CHAR(FECHA_FINAL, ''-MM-DD HH24:MI:SS''), ''YYYY-MM-DD HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') NOT IN (SELECT TO_CHAR(TO_DATE('''|| UN_ANIO_FIN ||''' || TO_CHAR(FECHA_INICIAL, ''-MM-DD HH24:MI:SS''), ''YYYY-MM-DD HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') ||
                                                                     TO_CHAR(TO_DATE('''|| UN_ANIO_FIN ||''' || TO_CHAR(FECHA_FINAL, ''-MM-DD HH24:MI:SS''), ''YYYY-MM-DD HH24:MI:SS''), ''DD/MM/YYYY HH24:MI:SS'') FECHA
                                                              FROM TASAS_INTERES 
                                                              WHERE ANO = '''|| UN_ANIO_FIN ||''')
                   AND ANO = '''|| UN_ANIO_INI ||''' ';
    BEGIN
        BEGIN
              MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TASAS_INTERES',
                                           UN_ACCION   => 'IS',
                                           UN_CAMPOS   =>  MI_CAMPOS,
                                           UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    END;
RETURN MI_RTA;
END FC_PASAR_TASAS_INT;

FUNCTION FC_GENERAR_PLANO_SUDAMERIS
(
   /*
      NAME              : FC_GENERAR_PLANO_SUDAMERIS
      AUTHOR            : CRISTIAN FERNEY SUESCUN BARRERA - CC:3204
      DATE              : 14-01-2026
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          :
      DATE MODIFIED     :
      DESCRIPTION       : Genera archivo plano para banco ITAU con los 
                          comprobantes contables según los parametros especificados
      MODIFICATIONS     :
      PARAMETERS        :
      --NAME  :  generarPlanoSudameris
    */
   UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA,        -- Companía de origen (filtro para consulta de comprobantes)
   UN_ANO                       IN PCK_SUBTIPOS.TI_ANIO,            -- Año contable (filtro para consulta de comprobantes)
   UN_EGRESO_INICIAL            IN COMPROBANTE_CNT.NUMERO%TYPE,     -- Número de egreso inicial del rango a procesar
   UN_EGRESO_FINAL              IN COMPROBANTE_CNT.NUMERO%TYPE,     -- Número de egreso final del rango a procesar
   UN_FECHA_PAGO                IN VARCHAR2,                        -- Fecha de pago (formato dd/MM/yyyy) - NO USADO en estructura Sudameris
   UN_CUENTA_DEBITAR            IN VARCHAR2,                        -- Cuenta bancaria a debitar del originador - Posición 1-12
   UN_CUENTA_PRINCIPAL_AFILIADA IN NUMBER,                          -- Parámetro reservado - NO USADO (siempre viene como 0)
   UN_CODIGO_BANCO              IN VARCHAR2,                        -- Código del banco destino (6 dígitos) - Posición 15-20
   UN_TIPO_EGRESO               IN COMPROBANTE_CNT.TIPO%TYPE,       -- Tipo de comprobante a filtrar (filtro para consulta)
   UN_CUENTA_INICIAL            IN COMPROBANTE_CNT.CUENTA%TYPE,     -- Cuenta contable inicial del rango a procesar
   UN_CUENTA_FINAL              IN COMPROBANTE_CNT.CUENTA%TYPE,     -- Cuenta contable final del rango a procesar
   UN_IDENTIFICADOR             IN NUMBER,                          -- Parámetro reservado - NO USADO (siempre viene como 0)
   UN_CODIGO_VERIFICACION       IN NUMBER,                          -- Parámetro reservado - NO USADO (siempre viene como 0)
   UN_TIPO_CUENTA_CLIENTE       IN VARCHAR2,                        -- Tipo de cuenta del originador (E=Ahorros, D=Corriente) - Posición 13-14
   UN_CLASE_TRANSACCION         IN VARCHAR2                         -- Parámetro reservado - NO USADO (puede venir vacío)
) RETURN CLOB
IS
    MI_NUMEROREGISTROS  NUMBER := 0;
    MI_CONSECUTIVO      NUMBER := 0;
    MI_SALIDA           CLOB := '';
    MI_NOMBRE_DEST      VARCHAR2(32000);
    MI_DESCRIPCION      VARCHAR2(32000);
    MI_FECHA_PAGO_DATE  DATE;
    
    -- Variables para valores por defecto
    V_TIPO_CUENTA_CLIENTE VARCHAR2(50) := NVL(UN_TIPO_CUENTA_CLIENTE, '');
    
BEGIN
    -- Convertir fecha de String a Date
    BEGIN
        MI_FECHA_PAGO_DATE := TO_DATE(UN_FECHA_PAGO, 'dd/MM/yyyy');
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 'ERROR: Formato de fecha inválido';
    END;
    
    -- Contar registros válidos
    BEGIN
        SELECT COUNT(*)
        INTO MI_NUMEROREGISTROS
        FROM COMPROBANTE_CNT C
            INNER JOIN TERCEROPAGOS TP
                ON C.COMPANIA  = TP.COMPANIA
                AND C.SUCURSAL = TP.SUCURSAL
                AND C.TERCERO  = TP.NIT
            INNER JOIN TERCERO T
                ON C.COMPANIA  = T.COMPANIA
                AND C.TERCERO  = T.NIT
                AND C.SUCURSAL = T.SUCURSAL
        WHERE C.COMPANIA = UN_COMPANIA
            AND C.ANO = UN_ANO
            AND C.PAGOENPLANO <> 0
            AND C.NUMERO BETWEEN UN_EGRESO_INICIAL AND UN_EGRESO_FINAL  
            AND C.TIPO = UN_TIPO_EGRESO                                   
            AND C.CUENTA BETWEEN UN_CUENTA_INICIAL AND UN_CUENTA_FINAL;
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            MI_NUMEROREGISTROS := 0;
    END;
    
    -- Validar que hay registros
    IF MI_NUMEROREGISTROS = 0 THEN
        RETURN 'ERROR';
    END IF;
    
    -- Procesar cada registro
    FOR MI_RS IN (
        SELECT 
            C.NUMERO AS NUMERO_EGRESO,
            C.TERCERO AS NIT,
            T.NOMBRE AS NOMBRE_DESTINATARIO,
            T.DIGITOVERIFICACION AS DV_TERCERO,
            TP.CUENTA AS CUENTA_DESTINO,
            TP.TIPOCUENTA AS TIPO_CUENTA_DESTINO,
            TP.BANCO AS BANCO_DESTINO,
            C.VLRAGIRAR AS VALOR_EGRESO,          
            C.DESCRIPCION AS DESCRIPCION,
            C.COMPANIA,
            C.ANO,
            C.TIPO,
            C.SUCURSAL
        FROM COMPROBANTE_CNT C
            INNER JOIN TERCEROPAGOS TP
                ON C.COMPANIA  = TP.COMPANIA
                AND C.SUCURSAL = TP.SUCURSAL
                AND C.TERCERO  = TP.NIT
            INNER JOIN TERCERO T
                ON C.COMPANIA  = T.COMPANIA
                AND C.TERCERO  = T.NIT
                AND C.SUCURSAL = T.SUCURSAL
        WHERE C.COMPANIA = UN_COMPANIA
            AND C.ANO = UN_ANO
            AND C.PAGOENPLANO <> 0
            AND C.NUMERO BETWEEN UN_EGRESO_INICIAL AND UN_EGRESO_FINAL  
            AND C.TIPO = UN_TIPO_EGRESO                                   
            AND C.CUENTA BETWEEN UN_CUENTA_INICIAL AND UN_CUENTA_FINAL
            AND C.CUENTABANCO IS NOT NULL
            AND TRIM(C.CUENTABANCO) <> ' '
            AND C.CUENTA IS NOT NULL
            AND TRIM(C.CUENTA)<> ' '
        ORDER BY C.NUMERO
    )
    LOOP
        -- Validar que no haya campos nulos críticos
        IF MI_RS.NIT IS NULL OR 
           MI_RS.NOMBRE_DESTINATARIO IS NULL OR 
           MI_RS.CUENTA_DESTINO IS NULL OR 
           MI_RS.VALOR_EGRESO IS NULL THEN
            CONTINUE;
        END IF;
        
        -- Incrementar consecutivo
        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        
        -- Agregar salto de línea si no es el primer registro
        IF MI_CONSECUTIVO > 1 THEN
            MI_SALIDA := MI_SALIDA || CHR(10);
        END IF;
        
        -- ============================================
        -- CONSTRUCCIÓN DEL PLANO
        -- ============================================
        
        -- DATOS DEL ORIGINADOR DEL PAGO:
        -- Posición 1-12: Nro. de Cuenta a Debitar (12 posiciones)
        MI_SALIDA := MI_SALIDA || LPAD(NVL(UN_CUENTA_DEBITAR, '0'), 12, '0');
        
        -- Posición 13-14: Tipo de Cuenta del Originador (2 posiciones)
        -- Indica si es Cuenta Corriente (20) o Cuenta de Ahorros (21)
        MI_SALIDA := MI_SALIDA || CASE 
            WHEN UN_TIPO_CUENTA_CLIENTE = 'E' THEN '21'  -- Ahorros
            WHEN UN_TIPO_CUENTA_CLIENTE = 'D' THEN '20'  -- Corriente
            ELSE '00'  -- Por defecto
        END;
       
        -- DATOS DEL DESTINATARIO:
        -- Posición 15-20: Cod. Banco a Acreditar (6 posiciones)
        -- Numérico de 6 posiciones. Debe coincidir con la tabla de códigos de banco
        MI_SALIDA := MI_SALIDA || LPAD(NVL(UN_CODIGO_BANCO, '0'), 6, '0');
        
        -- Posición 21-36: No de Cuenta a Acreditar (16 posiciones)
        -- Max 16 posiciones alfanuméricas
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(NVL(MI_RS.CUENTA_DESTINO, ''), 1, 16), 16, ' ');
        
        -- Posición 37-38: Tipo de Cuenta Destino (2 posiciones)
        -- Indica si es Cuenta Corriente (20), Cuenta de Ahorros (21) o Depósito Electrónico (19)
        MI_SALIDA := MI_SALIDA || CASE 
            WHEN MI_RS.TIPO_CUENTA_DESTINO = 'A' THEN '21'  -- Ahorros
            WHEN MI_RS.TIPO_CUENTA_DESTINO = 'C' THEN '20'  -- Corriente
            ELSE '19'  -- Depósito Electrónico por defecto
        END;
        
        -- Posición 39-50: No de Identificación del Destinatario (12 posiciones)
        -- Es el documento de identificación (NIT) del titular de la cuenta 
        MI_SALIDA := MI_SALIDA || LPAD(SUBSTR(MI_RS.NIT, 1, 12), 12, '0');
        
        -- Posición 51-85: Nombre del Destinatario del Pago - Beneficiario (35 posiciones)
        -- Max 35 posiciones
        MI_NOMBRE_DEST := MI_RS.NOMBRE_DESTINATARIO;
        -- Limpiar caracteres especiales
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, CHR(13) || CHR(10), ' ');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, CHR(13), ' ');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, CHR(10), ' ');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Á', 'A');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'É', 'E');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Í', 'I');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Ó', 'O');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Ú', 'U');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Ñ', 'N');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'á', 'a');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'é', 'e');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'í', 'i');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'ó', 'o');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'ú', 'u');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'ñ', 'n');
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_NOMBRE_DEST, 1, 35), 35, ' ');
        
        -- Posición 86-91: Descripción del Pago - Concepto (6 posiciones)
        -- Max 6 posiciones
        MI_DESCRIPCION := MI_RS.DESCRIPCION;
        -- Limpiar caracteres especiales
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, CHR(13) || CHR(10), ' ');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, CHR(13), ' ');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, CHR(10), ' ');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Á', 'A');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'É', 'E');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Í', 'I');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Ó', 'O');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Ú', 'U');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Ñ', 'N');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'á', 'a');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'é', 'e');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'í', 'i');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'ó', 'o');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'ú', 'u');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'ñ', 'n');
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_DESCRIPCION, 1, 6), 6, ' ');
        
        -- Posición 92-104: Valor (13 posiciones)
        -- 13 caracteres numéricos: 11 dígitos enteros + 2 decimales
        -- Cada registro tiene su propio VLRAGIRAR (valor individual del egreso)
        MI_SALIDA := MI_SALIDA || LPAD(TRUNC(PCK_SYSMAN_UTL.FC_ROUND(MI_RS.VALOR_EGRESO, 2) * 100), 13, '0');
        
    END LOOP;
  
    RETURN MI_SALIDA;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
        
END FC_GENERAR_PLANO_SUDAMERIS;


FUNCTION FC_GENERAR_PLANO_ITAU
/*
      NAME              : FC_GENERAR_PLANO_ITAU
      AUTHOR            : CRISTIAN FERNEY SUESCUN BARRERA - CC:3204
      DATE              : 22-01-2026
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          :
      DATE MODIFIED     :
      DESCRIPTION       : Genera archivo plano para banco ITAU con los 
                          comprobantes contables según los parametros especificados
      MODIFICATIONS     :
      PARAMETERS        :
      --NAME  :  generarPlanoSudameris
    */
(
   UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_ANO                       IN PCK_SUBTIPOS.TI_ANIO,
   UN_EGRESO_INICIAL            IN COMPROBANTE_CNT.NUMERO%TYPE,
   UN_EGRESO_FINAL              IN COMPROBANTE_CNT.NUMERO%TYPE,
   UN_FECHA_PAGO                IN VARCHAR2,
   UN_CUENTA_DEBITAR            IN VARCHAR2,
   UN_CUENTA_PRINCIPAL_AFILIADA IN NUMBER,
   UN_CODIGO_BANCO              IN VARCHAR2,
   UN_TIPO_EGRESO               IN COMPROBANTE_CNT.TIPO%TYPE,
   UN_CUENTA_INICIAL            IN COMPROBANTE_CNT.CUENTA%TYPE,
   UN_CUENTA_FINAL              IN COMPROBANTE_CNT.CUENTA%TYPE,
   UN_IDENTIFICADOR             IN NUMBER,
   UN_CODIGO_VERIFICACION       IN NUMBER,
   UN_TIPO_CUENTA_CLIENTE       IN VARCHAR2,
   UN_NIT_CLIENTE               IN VARCHAR2
) RETURN CLOB
IS
    MI_NUMEROREGISTROS  NUMBER := 0;
    MI_CONSECUTIVO      NUMBER := 0;
    MI_SALIDA           CLOB := '';
    MI_NOMBRE_DEST      VARCHAR2(32000);
    MI_DESCRIPCION      VARCHAR2(32000);
    MI_EMAIL_TEMP       VARCHAR2(100);
    MI_TELEFONO_TEMP    VARCHAR2(14);
    MI_FAX_TEMP         VARCHAR2(14);
    MI_DIRECCION_TEMP   VARCHAR2(40);
    MI_PAIS_TEMP        VARCHAR2(4);
    MI_DEPTO_TEMP       VARCHAR2(4);
    MI_CIUDAD_TEMP      VARCHAR2(4);
    MI_FECHA_PAGO_DATE  DATE;
    MI_FECHA_PAGO_STR   VARCHAR2(6);
    
BEGIN
    -- Convertir y validar fecha de pago
    BEGIN
        MI_FECHA_PAGO_DATE := TO_DATE(UN_FECHA_PAGO, 'dd/MM/yyyy');
        MI_FECHA_PAGO_STR := TO_CHAR(MI_FECHA_PAGO_DATE, 'MMDDYY');
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 'ERROR: Formato de fecha inválido. Use dd/MM/yyyy';
    END;
    
    -- Contar registros válidos
    BEGIN
        SELECT COUNT(*)
        INTO MI_NUMEROREGISTROS
        FROM COMPROBANTE_CNT C
            INNER JOIN TERCEROPAGOS TP
                ON C.COMPANIA  = TP.COMPANIA
                AND C.SUCURSAL = TP.SUCURSAL
                AND C.TERCERO  = TP.NIT
            INNER JOIN TERCERO T
                ON C.COMPANIA  = T.COMPANIA
                AND C.TERCERO  = T.NIT
                AND C.SUCURSAL = T.SUCURSAL
        WHERE C.COMPANIA = UN_COMPANIA
            AND C.ANO = UN_ANO
            AND C.PAGOENPLANO <> 0
            AND C.NUMERO BETWEEN UN_EGRESO_INICIAL AND UN_EGRESO_FINAL  
            AND C.TIPO = UN_TIPO_EGRESO                                   
            AND C.CUENTA BETWEEN UN_CUENTA_INICIAL AND UN_CUENTA_FINAL;
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            MI_NUMEROREGISTROS := 0;
    END;
    
    -- Validar que hay registros
    IF MI_NUMEROREGISTROS = 0 THEN
        RETURN 'ERROR: No se encontraron registros para procesar';
    END IF;
    
    -- Procesar cada registro
    FOR MI_RS IN (
        SELECT 
            T.TIPOID AS TIPO_IDENTIFICACION,
            C.TERCERO AS NIT,
            T.NOMBRE AS NOMBRE_DESTINATARIO,
            T.DIGITOVERIFICACION AS DV_TERCERO,
            CASE 
                WHEN NVL(T.CLASEENTIDADOFICIAL, 0) = 1 THEN 'S'
                ELSE 'N'
            END AS SENIAL_OFICIAL,
            TP.BANCO AS CODIGO_BANCO_TERCERO,
            CASE 
                WHEN UPPER(TRIM(TP.TIPOCUENTA)) = 'C' THEN 'CTE'
                WHEN UPPER(TRIM(TP.TIPOCUENTA)) = 'A' THEN 'AHO'
                WHEN UPPER(TRIM(TP.TIPOCUENTA)) = 'D' THEN 'DEP'
                ELSE 'AHO'
            END AS TIPO_CUENTA_TERCERO,
            TP.CUENTA AS CUENTA_DESTINO,
            C.VLRAGIRAR AS VALOR_EGRESO,  
            C.NUMERO AS NUMERO_EGRESO,
            C.DESCRIPCION AS DESCRIPCION,
            T.DIRECCIONEMAIL AS CORREO_TERCERO,
            T.TELEFONOS AS TELEFONO_1,
            T.FAX AS FAX_TERCERO,
            T.DIRECCION AS DIRECCION_TERCERO,
            T.PAIS AS PAIS_TERCERO,
            T.DEPARTAMENTO AS DEPARTAMENTO_TERCERO,
            T.CIUDAD AS CIUDAD_TERCERO,
            C.COMPANIA,
            C.ANO,
            C.TIPO,
            C.SUCURSAL
        FROM COMPROBANTE_CNT C
            INNER JOIN TERCEROPAGOS TP
                ON C.COMPANIA  = TP.COMPANIA
                AND C.SUCURSAL = TP.SUCURSAL
                AND C.TERCERO  = TP.NIT
            INNER JOIN TERCERO T
                ON C.COMPANIA  = T.COMPANIA
                AND C.TERCERO  = T.NIT
                AND C.SUCURSAL = T.SUCURSAL
        WHERE C.COMPANIA = UN_COMPANIA
            AND C.ANO = UN_ANO
            AND C.PAGOENPLANO <> 0
            AND C.NUMERO BETWEEN UN_EGRESO_INICIAL AND UN_EGRESO_FINAL  
            AND C.TIPO = UN_TIPO_EGRESO                                   
            AND C.CUENTA BETWEEN UN_CUENTA_INICIAL AND UN_CUENTA_FINAL
            AND C.CUENTABANCO IS NOT NULL
            AND TRIM(C.CUENTABANCO) <> ' '
            AND C.CUENTA IS NOT NULL
            AND TRIM(C.CUENTA) <> ' '
        ORDER BY C.NUMERO
    )
    LOOP
        -- Validar que no haya campos nulos críticos
        IF MI_RS.NIT IS NULL OR 
           MI_RS.NOMBRE_DESTINATARIO IS NULL OR 
           MI_RS.CUENTA_DESTINO IS NULL OR 
           MI_RS.VALOR_EGRESO IS NULL THEN
            CONTINUE;
        END IF;
        
        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        
        IF MI_CONSECUTIVO > 1 THEN
            MI_SALIDA := MI_SALIDA || CHR(10);
        END IF;
        -- ============================================
        -- CONSTRUCCIÓN DEL PLANO
        -- ============================================
        
        -- Posición 1-1: Identificador de registro
        MI_SALIDA := MI_SALIDA || '1';
        
        -- Posición 2-6: Secuencia
        MI_SALIDA := MI_SALIDA || LPAD(MI_CONSECUTIVO, 5, '0');
        
        -- Posición 7-12: Fecha del movimiento
        MI_SALIDA := MI_SALIDA || MI_FECHA_PAGO_STR;
        
        -- Posición 13-14: Tipo de identificación del tercero
        MI_SALIDA := MI_SALIDA || LPAD(NVL(MI_RS.TIPO_IDENTIFICACION, '01'), 2, '0');
        
        -- Posición 15-29: Número de identificación del proveedor/tercero
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(NVL(MI_RS.NIT, ''), 1, 15), 15, ' ');
        
        -- Posición 30-51: Nombre del proveedor/tercero
        MI_NOMBRE_DEST := NVL(MI_RS.NOMBRE_DESTINATARIO, '');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, CHR(13) || CHR(10), ' ');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, CHR(13), ' ');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, CHR(10), ' ');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Á', 'A');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'É', 'E');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Í', 'I');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Ó', 'O');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Ú', 'U');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'Ñ', 'N');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'á', 'a');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'é', 'e');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'í', 'i');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'ó', 'o');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'ú', 'u');
        MI_NOMBRE_DEST := REPLACE(MI_NOMBRE_DEST, 'ñ', 'n');
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_NOMBRE_DEST, 1, 22), 22, ' ');
        
        -- Posición 52-52: Señal oficial
        MI_SALIDA := MI_SALIDA || NVL(MI_RS.SENIAL_OFICIAL, 'N');
        
        -- Posición 53-55: Código de oficina para entrega de cheque
        MI_SALIDA := MI_SALIDA || '   ';
        
        -- Posición 56-58: Código del banco proveedor/tercero
        MI_SALIDA := MI_SALIDA || LPAD(NVL(UN_CODIGO_BANCO, '0'), 3, '0');
        
        -- Posición 59-61: Tipo de cuenta del proveedor/tercero
        MI_SALIDA := MI_SALIDA || NVL(MI_RS.TIPO_CUENTA_TERCERO, 'AHO');
        
        -- Posición 62-78: Número de cuenta del proveedor/tercero
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(NVL(MI_RS.CUENTA_DESTINO, ''), 1, 17), 17, ' ');
        
        -- Posición 79-80: Tipo de transacción
        MI_SALIDA := MI_SALIDA || 'CR';
        
        -- Posición 81-94: Valor
        MI_SALIDA := MI_SALIDA || LPAD(TRUNC(PCK_SYSMAN_UTL.FC_ROUND(NVL(MI_RS.VALOR_EGRESO, 0), 2) * 100), 14, '0');
        
        -- Posición 95-114: Referencia
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(TO_CHAR(NVL(MI_RS.NUMERO_EGRESO, 0)), 1, 20), 20, ' ');
        
        -- Posición 115-194: Observación
        MI_DESCRIPCION := NVL(MI_RS.DESCRIPCION, '');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, CHR(13) || CHR(10), ' ');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, CHR(13), ' ');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, CHR(10), ' ');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Á', 'A');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'É', 'E');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Í', 'I');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Ó', 'O');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Ú', 'U');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'Ñ', 'N');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'á', 'a');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'é', 'e');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'í', 'i');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'ó', 'o');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'ú', 'u');
        MI_DESCRIPCION := REPLACE(MI_DESCRIPCION, 'ñ', 'n');
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_DESCRIPCION, 1, 80), 80, ' ');
        
        -- Posición 195-294: E-mail del proveedor/tercero (100 posiciones)
        MI_EMAIL_TEMP := ' ';
        IF MI_RS.CORREO_TERCERO IS NOT NULL AND TRIM(MI_RS.CORREO_TERCERO) IS NOT NULL THEN
            MI_EMAIL_TEMP := TRIM(MI_RS.CORREO_TERCERO);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_EMAIL_TEMP, 1, 100), 100, ' ');
        
        -- Posición 295-308: Teléfono 1 del proveedor/tercero (14 posiciones)
        MI_TELEFONO_TEMP := ' ';
        IF MI_RS.TELEFONO_1 IS NOT NULL AND TRIM(MI_RS.TELEFONO_1) IS NOT NULL THEN
            MI_TELEFONO_TEMP := TRIM(MI_RS.TELEFONO_1);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_TELEFONO_TEMP, 1, 14), 14, ' ');
        
        -- Posición 309-322: Teléfono 2 del proveedor/tercero (14 posiciones)
        -- Usa el mismo teléfono 1
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_TELEFONO_TEMP, 1, 14), 14, ' ');
        
        -- Posición 323-336: Fax del proveedor/tercero (14 posiciones)
        MI_FAX_TEMP := ' ';
        IF MI_RS.FAX_TERCERO IS NOT NULL AND TRIM(MI_RS.FAX_TERCERO) IS NOT NULL THEN
            MI_FAX_TEMP := TRIM(MI_RS.FAX_TERCERO);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_FAX_TEMP, 1, 14), 14, ' ');
        
        -- Posición 337-376: Dirección del proveedor/tercero (40 posiciones)
        MI_DIRECCION_TEMP := ' ';
        IF MI_RS.DIRECCION_TERCERO IS NOT NULL AND TRIM(MI_RS.DIRECCION_TERCERO) IS NOT NULL THEN
            MI_DIRECCION_TEMP := TRIM(MI_RS.DIRECCION_TERCERO);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_DIRECCION_TEMP, 1, 40), 40, ' ');

        -- Posición 377-380: País del proveedor/tercero (4 posiciones)
        MI_PAIS_TEMP := ' ';
        IF MI_RS.PAIS_TERCERO IS NOT NULL AND TRIM(MI_RS.PAIS_TERCERO) IS NOT NULL THEN
            MI_PAIS_TEMP := TRIM(MI_RS.PAIS_TERCERO);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_PAIS_TEMP, 1, 4), 4, ' ');
        
        -- Posición 381-384: Depto del proveedor/tercero (4 posiciones)
        MI_DEPTO_TEMP := ' ';
        IF MI_RS.DEPARTAMENTO_TERCERO IS NOT NULL AND TRIM(MI_RS.DEPARTAMENTO_TERCERO) IS NOT NULL THEN
            MI_DEPTO_TEMP := TRIM(MI_RS.DEPARTAMENTO_TERCERO);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_DEPTO_TEMP, 1, 4), 4, ' ');
        
        -- Posición 385-388: Ciudad del proveedor/tercero (4 posiciones)
        MI_CIUDAD_TEMP := ' ';
        IF MI_RS.CIUDAD_TERCERO IS NOT NULL AND TRIM(MI_RS.CIUDAD_TERCERO) IS NOT NULL THEN
            MI_CIUDAD_TEMP := TRIM(MI_RS.CIUDAD_TERCERO);
        END IF;
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_CIUDAD_TEMP, 1, 4), 4, ' ');
        
        -- Posición 389-389: GAP
        MI_SALIDA := MI_SALIDA || ' ';
        
        -- Posición 390-391: Tipo de identificación del cliente
        MI_SALIDA := MI_SALIDA ||
        RPAD(
            CASE 
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'C' THEN '01' -- Cedula ciudadanía
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'E' THEN '02' -- Cédula extranjería
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'N' THEN '03' -- NIT
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'T' THEN '04' -- Tarjeta de identidad
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'P' THEN '05' -- Pasaporte
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'D' THEN '17' -- Pase diplomático
                WHEN UPPER(MI_RS.TIPO_IDENTIFICACION) = 'R' THEN '24' -- Registro civil
                ELSE '07' -- Soc. Ext. sin Nit en Colombia
            END,
            2,
            ' '
        );
        
        -- Posición 392-406: Número de identificación del cliente
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(NVL(UN_NIT_CLIENTE, ''), 1, 15), 15, ' ');
        
        -- Posición 407-409: Tipo de cuenta del cliente
        MI_SALIDA := MI_SALIDA || 
            RPAD(
                CASE 
                    WHEN UPPER(TRIM(UN_TIPO_CUENTA_CLIENTE)) = 'D' THEN 'CTE'
                    WHEN UPPER(TRIM(UN_TIPO_CUENTA_CLIENTE)) = 'E' THEN 'AHO'
                    ELSE ' '  -- Valor por defecto para cliente
                END,
                3, 
                ' '
            );
        
        -- Posición 410-426: Número de cuenta
        MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(NVL(UN_CUENTA_DEBITAR, ''), 1, 17), 17, ' ');
        
    END LOOP;
    
    RETURN MI_SALIDA;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
        
END FC_GENERAR_PLANO_ITAU;

FUNCTION FC_PASAR_PROVEDORES
/*
NAME              : FC_PASAR_PROVEDORES
AUTHORS           : CFBARRERA
DATE              : 16/02/2026
SOURCE MODULE     : CONTABILIDAD
DESCRIPTION       : Inserta los provedores del año origen al año destino
                    validando que las cuentas contables existan en el plan
                    contable destino y tengan al menos un indicador activo.
--NAME:  pasarProvedores
--METHOD: GET
*/
(
  UN_COMPANIA          IN  VARCHAR2,
  UN_ANO_ORIGEN        IN  INTEGER,
  UN_ANO_DESTINO       IN  INTEGER
) RETURN CLOB  
AS
  MI_MENSAJE           CLOB := '';
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
  MI_CUENTA_CONTABLE   TA_PROVEEDORES.CUENTACREDITO%TYPE;
  MI_CUENTA_CODIGO     TA_PROVEEDORES.CODIGO%TYPE;
  MI_EXISTE_CUENTA     NUMBER;
  MI_EXISTE_CHECKS     NUMBER;
  MI_EXISTE_DESTINO    NUMBER;
  MI_STRSQL            VARCHAR2(32000);
  MI_POS_NUEVOS        NUMBER := 0;
  MI_POS_EXISTIAN      NUMBER := 0;
  MI_POS_ERROR         NUMBER := 0;
  MI_POS_TOTAL         NUMBER := 0;
  MI_TEXTO_ERRORES     CLOB := '';
  MI_TEXTO_NUEVOS      CLOB := '';
  MI_TEXTO_EXISTIAN    CLOB := '';
  MI_EXCLUIDOS         VARCHAR2(500);
  
  RSRUBRO              SYS_REFCURSOR;
  
BEGIN

  -- ============================================================
  -- 1) Buscar registros del año origen
  -- ============================================================
  MI_STRSQL := 
    ' SELECT  ORI.CUENTACREDITO, ORI.CODIGO' ||
    ' FROM TA_PROVEEDORES ORI ' ||
    ' WHERE ORI.COMPANIA = ''' || UN_COMPANIA || '''' ||
    '   AND ORI.ANO      = ' || UN_ANO_ORIGEN;

  OPEN RSRUBRO FOR MI_STRSQL;
  
  LOOP
    FETCH RSRUBRO INTO MI_CUENTA_CONTABLE,MI_CUENTA_CODIGO;
    EXIT WHEN RSRUBRO%NOTFOUND;
    
    MI_POS_TOTAL := MI_POS_TOTAL + 1;
    
    BEGIN

      -- ============================================================
      -- 2) Verifica si ya existe en el año destino
      -- ============================================================
      MI_STRSQL :=
        ' SELECT COUNT(1) ' ||
        ' FROM TA_PROVEEDORES ' ||
        ' WHERE COMPANIA      = ''' || UN_COMPANIA || '''' ||
        '   AND ANO           = ' || UN_ANO_DESTINO ||
        '   AND CUENTACREDITO = ''' || MI_CUENTA_CONTABLE || '''';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_DESTINO;

      IF MI_EXISTE_DESTINO > 0 THEN
        MI_POS_EXISTIAN := MI_POS_EXISTIAN + 1;
        MI_TEXTO_EXISTIAN := MI_TEXTO_EXISTIAN
          || CHR(10) || MI_CUENTA_CONTABLE
          || ' (ya existia en el año destino)';
        CONTINUE;
    END IF;
      -- ============================================================
      -- 3) Valida si la cuenta contable existe en destino
      -- ============================================================
      MI_STRSQL := 
        ' SELECT COUNT(1) ' ||
        ' FROM PLAN_CONTABLE PL ' ||
        ' WHERE PL.COMPANIA = ''' || UN_COMPANIA || '''' ||
        '   AND PL.ANO      = ' || UN_ANO_DESTINO ||
        '   AND PL.CODIGO   = ''' || MI_CUENTA_CONTABLE || '''';
      
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_CUENTA;

      IF MI_EXISTE_CUENTA = 0 THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'La cuenta contable ' || MI_CUENTA_CONTABLE || ' con codigo proveedor ' || MI_CUENTA_CODIGO
          || ' no existe en el año destino ' || UN_ANO_DESTINO || '.'
          || CHR(10);
        CONTINUE;
      END IF;
      
      -- ============================================================
      -- 4) Valida que la cuenta tenga al menos UN indicador activo
      -- ============================================================
      MI_STRSQL := 
        ' SELECT (NVL(MOVIMIENTO, 0) + NVL(MAN_CEN_CTO, 0) + ' ||
        '         NVL(MAN_AUX_REF, 0) + NVL(MAN_AUX_FUE, 0) + ' ||
        '         NVL(MAN_AUX_GEN, 0)) ' ||
        ' FROM PLAN_CONTABLE PL ' ||
        ' WHERE PL.COMPANIA = ''' || UN_COMPANIA || '''' ||
        '   AND PL.ANO      = ' || UN_ANO_DESTINO ||  
        '   AND PL.CODIGO   = ''' || MI_CUENTA_CONTABLE || '''';
      
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_CHECKS;

      IF MI_EXISTE_CHECKS = 0 THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'La cuenta contable ' || MI_CUENTA_CONTABLE
          || ' no tiene ningun indicador de movimiento seleccionado'
          || ' para el año destino ' || UN_ANO_DESTINO || '.'
          || CHR(10);
        CONTINUE;
      END IF;

      -- ============================================================
      -- 5) Registro valido, acumula como nuevo
      -- ============================================================
      MI_POS_NUEVOS := MI_POS_NUEVOS + 1;
      MI_TEXTO_NUEVOS := MI_TEXTO_NUEVOS
        || CHR(10) || MI_CUENTA_CONTABLE;
      
    EXCEPTION
      WHEN OTHERS THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'La cuenta contable ' || MI_CUENTA_CONTABLE
          || ' - Error: ' || SQLERRM
          || CHR(10);
    END;
    
  END LOOP;
  
  CLOSE RSRUBRO;

  -- ============================================================
  -- 6) Ejecuta la insercion solo de los validos
  -- ============================================================
  MI_EXCLUIDOS := 'COMPANIA,ANO';
  MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('TA_PROVEEDORES', MI_EXCLUIDOS);
  MI_CAMPOS := 'ORI.' || REPLACE(MI_CAMPOS, ',', ', ORI.');
  
  MI_VALORES :=
    ' SELECT ' || MI_CAMPOS || ', ''' || UN_COMPANIA || ''', ' || UN_ANO_DESTINO ||
    ' FROM TA_PROVEEDORES ORI ' ||
    ' WHERE ORI.COMPANIA = ''' || UN_COMPANIA || '''' ||
    '   AND ORI.ANO      = ' || UN_ANO_ORIGEN ||
    ' AND NOT EXISTS ( ' ||
    '   SELECT 1 FROM TA_PROVEEDORES DES ' ||
    '   WHERE DES.COMPANIA      = ''' || UN_COMPANIA || '''' ||
    '     AND DES.ANO           = ' || UN_ANO_DESTINO ||
    '     AND DES.CUENTACREDITO = ORI.CUENTACREDITO) ' ||
    ' AND EXISTS ( ' ||
    '   SELECT 1 FROM PLAN_CONTABLE PL ' ||
    '   WHERE PL.COMPANIA = ''' || UN_COMPANIA || '''' ||
    '     AND PL.ANO      = ' || UN_ANO_DESTINO ||
    '     AND PL.CODIGO   = ORI.CUENTACREDITO ' ||
    '     AND (NVL(PL.MOVIMIENTO, 0) + NVL(PL.MAN_CEN_CTO, 0) + ' ||
    '          NVL(PL.MAN_AUX_REF, 0) + NVL(PL.MAN_AUX_FUE, 0) + ' ||
    '          NVL(PL.MAN_AUX_GEN, 0)) <> 0) ';
  
  MI_CAMPOS := REPLACE(MI_CAMPOS, 'ORI.', '');
  MI_CAMPOS := MI_CAMPOS || ', COMPANIA, ANO';

  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
    'TA_PROVEEDORES',  
    'IS', 
    MI_CAMPOS, 
    MI_VALORES, 
    NULL,  
    NULL   
  );

  -- ============================================================
  -- 7) Construcción del mensaje final
  -- ============================================================
  MI_MENSAJE :=
    '============================================================' || CHR(10) ||
    'ACTUALIZACIÓN DE PROVEEDORES AÑO ' || UN_ANO_DESTINO          || CHR(10) ||
    'Fecha: ' || TO_CHAR(SYSDATE, 'DD/MM/YYYY')       || CHR(10) ||
    'Año Origen:  ' || UN_ANO_ORIGEN ||
    ' | Año Destino: ' || UN_ANO_DESTINO                         || CHR(10) ||
    'Compañia: '   || UN_COMPANIA                                 || CHR(10) ||
    '============================================================' || CHR(10) ||
    'Registros procesados:  ' || MI_POS_TOTAL                     || CHR(10) ||
    'Registros nuevos:      ' || MI_POS_NUEVOS                    || CHR(10) ||
    'Registros ya existian: ' || MI_POS_EXISTIAN                  || CHR(10) ||
    'Registros con error:   ' || MI_POS_ERROR                     || CHR(10) ||
    '============================================================' || CHR(10);

  -- Seccion errores
  IF MI_POS_ERROR > 0 THEN
    MI_MENSAJE := MI_MENSAJE
      || CHR(10) || 'DETALLE DE ERRORES:'
      || CHR(10) || '------------------------------------------------------------'
      || MI_TEXTO_ERRORES
      || '------------------------------------------------------------' || CHR(10);
  END IF;

  -- Seccion nuevos y existentes
  MI_MENSAJE := MI_MENSAJE
    || CHR(10) || 'REGISTROS INSERTADOS NUEVOS:'
    || CHR(10) || '------------------------------------------------------------'
    || MI_TEXTO_NUEVOS
    || CHR(10) || '------------------------------------------------------------'
    || CHR(10) || 'REGISTROS QUE YA EXISTIAN EN EL AÑO DESTINO:'
    || CHR(10) || '------------------------------------------------------------'
    || MI_TEXTO_EXISTIAN
    || CHR(10) || '------------------------------------------------------------'
    || CHR(10) || '============================================================';

  RETURN MI_MENSAJE;
  
EXCEPTION
  WHEN OTHERS THEN
    IF RSRUBRO%ISOPEN THEN
      CLOSE RSRUBRO;
    END IF;
    RETURN 'ERROR: ' || SQLERRM || CHR(10) || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE;
    
END FC_PASAR_PROVEDORES;

FUNCTION FC_PASAR_REF_SERV_CTA
/*
NAME              : FC_PASAR_REF_SERV_CTA
AUTHORS           : CFBARRERA
DATE              : 16/02/2026
SOURCE MODULE     : CONTABILIDAD
DESCRIPTION       : Inserta las referencias del año origen al año destino
                    validando que las cuentas contables existan en el plan
                    contable destino y tengan al menos un indicador activo
                    ademas que exista el concepto en la tabla referencia
                    del año destino.
--NAME:  pasarReferenciados
--METHOD: GET
*/
(
  UN_COMPANIA          IN  VARCHAR2,
  UN_ANO_ORIGEN        IN  INTEGER,
  UN_ANO_DESTINO       IN  INTEGER
) RETURN CLOB  
AS
  MI_MENSAJE           CLOB := '';
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
  MI_CUENTA_CONTABLE   REFERENCIADOS.CUENTACONTABLE%TYPE;
  MI_CODIGO_REFERENCIA REFERENCIADOS.CODIGO%TYPE;
  MI_CODIGO_CONCEPTO   REFERENCIADOS.CONCEPTO%TYPE;
  MI_EXISTE_CUENTA     NUMBER;
  MI_EXISTE_CHECKS     NUMBER;
  MI_EXISTE_DESTINO    NUMBER;
  MI_STRSQL            VARCHAR2(32000);
  MI_POS_NUEVOS        NUMBER := 0;
  MI_POS_EXISTIAN      NUMBER := 0;
  MI_POS_ERROR         NUMBER := 0;
  MI_POS_TOTAL         NUMBER := 0;
  MI_TEXTO_ERRORES     CLOB := '';
  MI_TEXTO_NUEVOS      CLOB := '';
  MI_TEXTO_EXISTIAN    CLOB := '';
  MI_EXCLUIDOS         VARCHAR2(500);
  
  RSRUBRO              SYS_REFCURSOR;
  
BEGIN

  -- ============================================================
  -- 1) Buscar registros del año origen
  -- ============================================================
  MI_STRSQL := 
    ' SELECT  ORI.CUENTACONTABLE, ORI.CODIGO,ORI.CONCEPTO' ||
    ' FROM REFERENCIADOS ORI ' ||
    ' WHERE ORI.COMPANIA = ''' || UN_COMPANIA || '''' ||
    '   AND ORI.ANO      = ' || UN_ANO_ORIGEN;

  OPEN RSRUBRO FOR MI_STRSQL;
  
  LOOP
    FETCH RSRUBRO INTO MI_CUENTA_CONTABLE,MI_CODIGO_REFERENCIA,MI_CODIGO_CONCEPTO;
    EXIT WHEN RSRUBRO%NOTFOUND;
    
    MI_POS_TOTAL := MI_POS_TOTAL + 1;
    
    BEGIN

      -- ============================================================
      -- 2) Verifica si ya existe en el año destino
      -- ============================================================
          MI_STRSQL :=
      ' SELECT COUNT(1) ' ||
      ' FROM REFERENCIADOS ' ||
      ' WHERE COMPANIA       = ''' || UN_COMPANIA || '''' ||
      '   AND ANO            = ' || UN_ANO_DESTINO ||
      '   AND CUENTACONTABLE = ''' || MI_CUENTA_CONTABLE || '''' ||
      '   AND CODIGO         = ''' || MI_CODIGO_REFERENCIA || '''' ||
      '   AND CONCEPTO       = ''' || MI_CODIGO_CONCEPTO || '''';

      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_DESTINO;

      IF MI_EXISTE_DESTINO > 0 THEN
        MI_POS_EXISTIAN := MI_POS_EXISTIAN + 1;
        MI_TEXTO_EXISTIAN := MI_TEXTO_EXISTIAN
          || CHR(10) || MI_CUENTA_CONTABLE
          || ' (ya existia en el año destino)';
        CONTINUE;
      END IF;

      -- ============================================================
      -- 3) Valida si la cuenta contable existe en el plan contable destino
      -- ============================================================
      MI_STRSQL := 
        ' SELECT COUNT(1) ' ||
        ' FROM PLAN_CONTABLE PL ' ||
        ' WHERE PL.COMPANIA = ''' || UN_COMPANIA || '''' ||
        '   AND PL.ANO      = ' || UN_ANO_DESTINO ||
        '   AND PL.CODIGO   = ''' || MI_CUENTA_CONTABLE || '''';
      
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_CUENTA;

      IF MI_EXISTE_CUENTA = 0 THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'La cuenta contable ' || MI_CUENTA_CONTABLE|| ' con el concepto ' ||MI_CODIGO_CONCEPTO
          || ' no existe en el año destino ' || UN_ANO_DESTINO || '.'
          || CHR(10);
        CONTINUE;
      END IF;

      -- ============================================================
      -- 4) Valida que la cuenta tenga al menos un indicador activo
      -- ============================================================
      MI_STRSQL := 
        ' SELECT (NVL(MOVIMIENTO, 0) + NVL(MAN_CEN_CTO, 0) + ' ||
        '         NVL(MAN_AUX_REF, 0) + NVL(MAN_AUX_FUE, 0) + ' ||
        '         NVL(MAN_AUX_GEN, 0)) ' ||
        ' FROM PLAN_CONTABLE PL ' ||
        ' WHERE PL.COMPANIA = ''' || UN_COMPANIA || '''' ||
        '   AND PL.ANO      = ' || UN_ANO_DESTINO ||  
        '   AND PL.CODIGO   = ''' || MI_CUENTA_CONTABLE || '''';
      
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_CHECKS;

      IF MI_EXISTE_CHECKS = 0 THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'La cuenta contable ' || MI_CUENTA_CONTABLE
          || ' no tiene ningun indicador de movimiento seleccionado'
          || ' para el año destino ' || UN_ANO_DESTINO || '.'
          || CHR(10);
        CONTINUE;
      END IF;
      
      -- ============================================================
      -- 5) Valida si el codigo de referencia existe en REFERENCIA del año destino
      -- ============================================================
      MI_STRSQL := 
        ' SELECT COUNT(1) ' ||
        ' FROM REFERENCIA RF ' ||
        ' WHERE RF.COMPANIA = ''' || UN_COMPANIA || '''' ||
        '   AND RF.ANO      = ' || UN_ANO_DESTINO ||
        '   AND RF.CODIGO   = ''' || MI_CODIGO_REFERENCIA || '''';
      
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE_CUENTA;

      IF MI_EXISTE_CUENTA = 0 THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'El codigo de referencia ' || MI_CODIGO_REFERENCIA|| ' con el concepto ' ||MI_CODIGO_CONCEPTO
          || ' no existe en el año destino ' || UN_ANO_DESTINO || '.'
          || CHR(10);
        CONTINUE;
      END IF;

      -- ============================================================
      -- 6) Registro valido, acumula como nuevo
      -- ============================================================
      MI_POS_NUEVOS := MI_POS_NUEVOS + 1;
      MI_TEXTO_NUEVOS := MI_TEXTO_NUEVOS
        || CHR(10) || MI_CUENTA_CONTABLE;
      
    EXCEPTION
      WHEN OTHERS THEN
        MI_POS_ERROR := MI_POS_ERROR + 1;
        MI_TEXTO_ERRORES := MI_TEXTO_ERRORES
          || CHR(10) || 'La cuenta contable ' || MI_CUENTA_CONTABLE
          || ' - Error: ' || SQLERRM
          || CHR(10);
    END;
    
  END LOOP;
  
  CLOSE RSRUBRO;

     -- ============================================================
    -- 7) Ejecuta la insercion solo de los validos
    -- ============================================================
    MI_EXCLUIDOS := 'COMPANIA,ANO';
    MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('REFERENCIADOS', MI_EXCLUIDOS);
    MI_CAMPOS := 'ORI.' || REPLACE(MI_CAMPOS, ',', ', ORI.');
    
    MI_VALORES :=
      ' SELECT ' || MI_CAMPOS || ', ''' || UN_COMPANIA || ''', ' || UN_ANO_DESTINO ||
      ' FROM REFERENCIADOS ORI ' ||
      ' WHERE ORI.COMPANIA = ''' || UN_COMPANIA || '''' ||
      '   AND ORI.ANO      = ' || UN_ANO_ORIGEN ||
      ' AND NOT EXISTS ( ' ||
      '   SELECT 1 FROM REFERENCIADOS DES ' ||
      '   WHERE DES.COMPANIA       = ''' || UN_COMPANIA || '''' ||
      '     AND DES.ANO            = ' || UN_ANO_DESTINO ||
      '     AND DES.CUENTACONTABLE = ORI.CUENTACONTABLE ' ||
      '     AND DES.CODIGO         = ORI.CODIGO ' ||
      '     AND DES.CONCEPTO       = ORI.CONCEPTO) ' ||
      ' AND EXISTS ( ' ||
      '   SELECT 1 FROM PLAN_CONTABLE PL ' ||
      '   WHERE PL.COMPANIA = ''' || UN_COMPANIA || '''' ||
      '     AND PL.ANO      = ' || UN_ANO_DESTINO ||
      '     AND PL.CODIGO   = ORI.CUENTACONTABLE ' ||
      '     AND (NVL(PL.MOVIMIENTO, 0) + NVL(PL.MAN_CEN_CTO, 0) + ' ||
      '          NVL(PL.MAN_AUX_REF, 0) + NVL(PL.MAN_AUX_FUE, 0) + ' ||
      '          NVL(PL.MAN_AUX_GEN, 0)) <> 0) ' ||
      ' AND EXISTS ( ' ||
      '   SELECT 1 FROM REFERENCIA RF ' ||
      '   WHERE RF.COMPANIA = ''' || UN_COMPANIA || '''' ||
      '     AND RF.ANO      = ' || UN_ANO_DESTINO ||
      '     AND RF.CODIGO   = ORI.CODIGO) ';
    
    MI_CAMPOS := REPLACE(MI_CAMPOS, 'ORI.', '');
    MI_CAMPOS := MI_CAMPOS || ', COMPANIA, ANO';
    
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
      'REFERENCIADOS',  
      'IS', 
      MI_CAMPOS, 
      MI_VALORES, 
      NULL,  
      NULL   
    );

  -- ============================================================
  -- 8) Construcción del mensaje final
  -- ============================================================
  MI_MENSAJE :=
    '============================================================' || CHR(10) ||
    'ACTUALIZACIÓN DE REFERENCIADO DE SERVICIO AÑO ' || UN_ANO_DESTINO  || CHR(10) ||
    'Fecha: ' || TO_CHAR(SYSDATE, 'DD/MM/YYYY')       || CHR(10) ||
    'Año Origen:  ' || UN_ANO_ORIGEN ||
    ' | Año Destino: ' || UN_ANO_DESTINO                         || CHR(10) ||
    'Compañia: '   || UN_COMPANIA                                 || CHR(10) ||
    '============================================================' || CHR(10) ||
    'Registros procesados:  ' || MI_POS_TOTAL                     || CHR(10) ||
    'Registros nuevos:      ' || MI_POS_NUEVOS                    || CHR(10) ||
    'Registros ya existian: ' || MI_POS_EXISTIAN                  || CHR(10) ||
    'Registros con error:   ' || MI_POS_ERROR                     || CHR(10) ||
    '============================================================' || CHR(10);

  -- Seccion errores
  IF MI_POS_ERROR > 0 THEN
    MI_MENSAJE := MI_MENSAJE
      || CHR(10) || 'DETALLE DE ERRORES:'
      || CHR(10) || '------------------------------------------------------------'
      || MI_TEXTO_ERRORES
      || '------------------------------------------------------------' || CHR(10);
  END IF;

  -- Seccion nuevos y existentes
  MI_MENSAJE := MI_MENSAJE
    || CHR(10) || 'REGISTROS INSERTADOS NUEVOS:'
    || CHR(10) || '------------------------------------------------------------'
    || MI_TEXTO_NUEVOS
    || CHR(10) || '------------------------------------------------------------'
    || CHR(10) || 'REGISTROS QUE YA EXISTIAN EN EL AÑO DESTINO:'
    || CHR(10) || '------------------------------------------------------------'
    || MI_TEXTO_EXISTIAN
    || CHR(10) || '------------------------------------------------------------'
    || CHR(10) || '============================================================';

  RETURN MI_MENSAJE;
  
EXCEPTION
  WHEN OTHERS THEN
    IF RSRUBRO%ISOPEN THEN
      CLOSE RSRUBRO;
    END IF;
    RETURN 'ERROR: ' || SQLERRM || CHR(10) || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE;
    
END FC_PASAR_REF_SERV_CTA;

FUNCTION FC_CARGUE_MASIVO_RETENCION
/*
NAME              : FC_CARGUE_MASIVO_RETENCION
AUTHORS           : CFBARRERA
DATE              : 22/06/2026
SOURCE MODULE     : CONTABILIDAD
DESCRIPTION       : Realiza el cargue masivo de retenciones a partir de una
                    plantilla de datos, validando la estructura del archivo,
                    los tipos y códigos de retención, el estado del periodo
                    contable, la existencia de cuentas contables y la
                    consistencia de los valores requeridos. Los registros
                    válidos son insertados en la tabla RETENCIONES y al
                    finalizar se genera un resumen con el detalle de los
                    registros procesados, insertados y omitidos.
-- NAME   : cargarMasivoRetencion
-- METHOD : POST
*/
(
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA    IN CLOB,
    UN_MES       IN NUMBER,
    UN_DIA       IN NUMBER,
    UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_MENSAJE            CLOB := '';
    MI_TEXTO_ERRORES      CLOB := '';
    MI_TEXTO_INSERTADOS   CLOB := '';
    MI_INSERTADOS         NUMBER := 0;
    MI_OMITIDOS           NUMBER := 0;
    CONTADOR              NUMBER := 0;

    -- Columnas del Excel (strings crudos)
    MI_ANIO_STR           VARCHAR2(100 CHAR);
    MI_TIPO_RETENCION     VARCHAR2(100 CHAR);
    MI_CODIGO_RETENCION   VARCHAR2(100 CHAR);
    MI_NOMBRE_RETENCION   VARCHAR2(500 CHAR);
    MI_CUENTA_CREDITO     VARCHAR2(100 CHAR);
    MI_CUENTA_CREDITO1    VARCHAR2(100 CHAR);
    MI_LIMITE_INF_STR     VARCHAR2(100 CHAR);
    MI_PCT_BASE_STR       VARCHAR2(100 CHAR);
    MI_PCT_APLICAR_STR    VARCHAR2(100 CHAR);
    MI_APL_LEY1819_STR    VARCHAR2(100 CHAR);
    MI_FACTOR_RED_STR     VARCHAR2(100 CHAR);
    MI_MODIFICA_VALOR_STR VARCHAR2(100 CHAR);
    MI_MODIFICA_BASE_STR  VARCHAR2(100 CHAR);

    -- Variables convertidas
    MI_ANIO_NUM           NUMBER(4);
    MI_LIMITE_INF_NUM     NUMBER(20,2);
    MI_PCT_BASE_NUM       NUMBER(10,4);
    MI_PCT_APLICAR_NUM    NUMBER(10,4);
    MI_APL_LEY1819_NUM    NUMBER(1);
    MI_FACTOR_RED_NUM     NUMBER(5);
    MI_MODIFICA_VALOR_NUM NUMBER(1);
    MI_MODIFICA_BASE_NUM  NUMBER(1);

    -- Contadores de validación
    MI_EXISTE_TIPO_RET    NUMBER := 0;
    MI_EXISTE_COD_DUP     NUMBER := 0;
    MI_EXISTE_CTA_CRED    NUMBER := 0;
    MI_EXISTE_CTA_CRED1   NUMBER := 0;
    MI_ESTADO_ANIO        VARCHAR2(10 CHAR);

    -- Auxiliares INSERT
    MI_CAMPOS             VARCHAR2(32000 CHAR) := '';
    MI_VALORES            VARCHAR2(32000 CHAR) := '';
    MI_TABLA              VARCHAR2(200 CHAR);
    MI_RTA                CLOB := '';

    CN_NODATA    CONSTANT VARCHAR2(6) := 'NoData';

BEGIN

    DBMS_OUTPUT.PUT_LINE('ENTRO A LA FUNCION');

    IF UN_CADENA IS NULL OR TRIM(UN_CADENA) IS NULL THEN
        RETURN 'ERROR|No se recibió información para procesar.';
    END IF;

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                         UN_LISTA       => UN_CADENA,
                         UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG
                     );

    IF MI_DATOS_FILA IS NULL OR MI_DATOS_FILA.COUNT = 0 THEN
        RETURN 'ERROR|La cadena no contiene registros válidos.';
    END IF;

    <<DATOSRETENCION>>
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
    LOOP
        CONTADOR := CONTADOR + 1;

        IF TRIM(MI_DATOS_FILA(RS)) IS NULL THEN
            CONTINUE;
        END IF;

        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                                 UN_LISTA       => MI_DATOS_FILA(RS),
                                 UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL
                             );

        IF MI_DATOS_COLUMNAS.COUNT < 13 THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': Número de columnas incorrecto. ' ||
                'Se esperaban 13, llegaron ' || MI_DATOS_COLUMNAS.COUNT || '.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        -- Asignación de columnas
        BEGIN
            MI_ANIO_STR           := TRIM(MI_DATOS_COLUMNAS(1));
            MI_TIPO_RETENCION     := TRIM(MI_DATOS_COLUMNAS(2));
            MI_CODIGO_RETENCION   := TRIM(MI_DATOS_COLUMNAS(3));
            MI_NOMBRE_RETENCION   := TRIM(MI_DATOS_COLUMNAS(4));
            MI_CUENTA_CREDITO     := TRIM(MI_DATOS_COLUMNAS(5));
            MI_CUENTA_CREDITO1    := TRIM(MI_DATOS_COLUMNAS(6));
            MI_LIMITE_INF_STR     := TRIM(MI_DATOS_COLUMNAS(7));
            MI_PCT_BASE_STR       := TRIM(MI_DATOS_COLUMNAS(8));
            MI_PCT_APLICAR_STR    := TRIM(MI_DATOS_COLUMNAS(9));
            MI_APL_LEY1819_STR    := TRIM(MI_DATOS_COLUMNAS(10));
            MI_FACTOR_RED_STR     := TRIM(MI_DATOS_COLUMNAS(11));
            MI_MODIFICA_VALOR_STR := TRIM(MI_DATOS_COLUMNAS(12));
            MI_MODIFICA_BASE_STR  := TRIM(MI_DATOS_COLUMNAS(13));
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR ||
                    ': Uno o más campos exceden la longitud máxima permitida.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        -- Reset variables convertidas
        MI_ANIO_NUM           := NULL;
        MI_LIMITE_INF_NUM     := 0;
        MI_PCT_BASE_NUM       := NULL;
        MI_PCT_APLICAR_NUM    := NULL;
        MI_APL_LEY1819_NUM    := NULL;
        MI_FACTOR_RED_NUM     := NULL;
        MI_MODIFICA_VALOR_NUM := NULL;
        MI_MODIFICA_BASE_NUM  := NULL;
        MI_EXISTE_TIPO_RET    := 0;
        MI_EXISTE_COD_DUP     := 0;
        MI_EXISTE_CTA_CRED    := 0;
        MI_EXISTE_CTA_CRED1   := 0;

        --------------------------------------------------------
        -- 1.1  AÑO: obligatorio + conversion + no cerrado
        --------------------------------------------------------
        IF MI_ANIO_STR IS NULL OR MI_ANIO_STR = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': AÑO es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_ANIO_NUM := TO_NUMBER(MI_ANIO_STR);
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': AÑO "' || MI_ANIO_STR || '" no es un número válido.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        BEGIN
            SELECT ESTADO
              INTO MI_ESTADO_ANIO
              FROM DIA_BLOQUEO
             WHERE COMPANIA    = UN_COMPANIA
               AND ANO         = MI_ANIO_NUM
               AND MES         = UN_MES
               AND DIA         = UN_DIA
               AND APLICACION  = 1
               AND PROCESO     = 1;

            IF MI_ESTADO_ANIO <> 'A' THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': El año ' || MI_ANIO_NUM ||
                    ' mes ' || UN_MES || ' día ' || UN_DIA || ' está cerrado.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': El año ' || MI_ANIO_NUM ||
                    ' no existe para la compañía ' || UN_COMPANIA || '.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        --------------------------------------------------------
        -- 1.2  TIPO RETENCION: obligatorio + existe en catálogo
        --------------------------------------------------------
        IF MI_TIPO_RETENCION IS NULL OR MI_TIPO_RETENCION = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': TIPO RETENCION es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        SELECT COUNT(1)
          INTO MI_EXISTE_TIPO_RET
          FROM TIPORETENCION
         WHERE CODIGO = MI_TIPO_RETENCION;

        IF MI_EXISTE_TIPO_RET = 0 THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': TIPO RETENCION "' || MI_TIPO_RETENCION ||
                '" no existe en TIPORETENCION.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        --------------------------------------------------------
        -- 1.3  CODIGO RETENCION: obligatorio + max 3 chars + sin duplicado
        --------------------------------------------------------
        IF MI_CODIGO_RETENCION IS NULL OR MI_CODIGO_RETENCION = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ' [Año: ' || MI_ANIO_NUM ||
                ' / Tipo: ' || MI_TIPO_RETENCION || ']: ' ||
                'CODIGO RETENCION es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF LENGTH(MI_CODIGO_RETENCION) > 3 THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': CODIGO RETENCION "' || MI_CODIGO_RETENCION ||
                '" supera los 3 caracteres permitidos.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        SELECT COUNT(1)
          INTO MI_EXISTE_COD_DUP
          FROM RETENCIONES
         WHERE COMPANIA = UN_COMPANIA
           AND ANO      = MI_ANIO_NUM
           AND TIPO     = MI_TIPO_RETENCION
           AND CODIGO   = MI_CODIGO_RETENCION;

        IF MI_EXISTE_COD_DUP > 0 THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': El código "' || MI_CODIGO_RETENCION ||
                '" para el tipo "' || MI_TIPO_RETENCION ||
                '" en el año ' || MI_ANIO_NUM || ' ya existe (duplicado).' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        --------------------------------------------------------
        -- 1.4  NOMBRE RETENCION: obligatorio + max 64 chars + sin caracteres especiales
        --------------------------------------------------------
        IF MI_NOMBRE_RETENCION IS NULL OR MI_NOMBRE_RETENCION = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': NOMBRE RETENCION es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF LENGTH(MI_NOMBRE_RETENCION) > 64 THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': NOMBRE RETENCION "' || MI_NOMBRE_RETENCION ||
                '" supera los 64 caracteres permitidos. Longitud actual: ' ||
                LENGTH(MI_NOMBRE_RETENCION) || '.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF REGEXP_LIKE(MI_NOMBRE_RETENCION, '[''\"*]') THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': NOMBRE RETENCION contiene caracteres no permitidos.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        --------------------------------------------------------
        -- 1.5  CUENTA CREDITO: obligatoria + existe en PLAN_CONTABLE
        --------------------------------------------------------
        IF MI_CUENTA_CREDITO IS NULL OR MI_CUENTA_CREDITO = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': CUENTA CREDITO es obligatoria y está vacía.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        SELECT COUNT(1)
          INTO MI_EXISTE_CTA_CRED
          FROM PLAN_CONTABLE
         WHERE COMPANIA    = UN_COMPANIA
           AND ANO         = MI_ANIO_NUM
           AND CODIGO      = MI_CUENTA_CREDITO
           AND MOVIMIENTO <> 0
           AND CLASECUENTA = 'I';

        IF MI_EXISTE_CTA_CRED = 0 THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': La cuenta "' || MI_CUENTA_CREDITO ||
                '" no existe en el Plan Contable del año ' || MI_ANIO_NUM ||
                ', no tiene movimiento o no es clase I.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        --------------------------------------------------------
        -- 1.6  LÍMITE INFERIOR: opcional, default 0
        --------------------------------------------------------
        IF MI_LIMITE_INF_STR IS NULL OR MI_LIMITE_INF_STR = CN_NODATA THEN
            MI_LIMITE_INF_NUM := 0;
        ELSE
            BEGIN
                MI_LIMITE_INF_NUM := TO_NUMBER(REPLACE(MI_LIMITE_INF_STR, ',', '.'));
            EXCEPTION
                WHEN VALUE_ERROR THEN
                    MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                        'FILA ' || CONTADOR || ': LÍMITE INFERIOR "' || MI_LIMITE_INF_STR ||
                        '" no es un valor numérico válido.' || CHR(10);
                    MI_OMITIDOS := MI_OMITIDOS + 1;
                    CONTINUE;
            END;
        END IF;

        --------------------------------------------------------
        -- 1.7  % BASE: obligatorio, numerico
        --------------------------------------------------------
        IF MI_PCT_BASE_STR IS NULL OR MI_PCT_BASE_STR = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': % BASE es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_PCT_BASE_NUM := TO_NUMBER(REPLACE(MI_PCT_BASE_STR, ',', '.'));
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': % BASE "' || MI_PCT_BASE_STR ||
                    '" no es un valor numérico válido.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        --------------------------------------------------------
        -- 1.8  % APLICAR: obligatorio, numérico con decimales
        --------------------------------------------------------
        IF MI_PCT_APLICAR_STR IS NULL OR MI_PCT_APLICAR_STR = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': % APLICAR es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_PCT_APLICAR_NUM := TO_NUMBER(REPLACE(MI_PCT_APLICAR_STR, ',', '.'));
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': % APLICAR "' || MI_PCT_APLICAR_STR ||
                    '" no es un valor numérico válido.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        --------------------------------------------------------
        -- 1.9  APL LEY 1819: obligatorio, acepta SI/-1 o NO/0
        --------------------------------------------------------
        IF MI_APL_LEY1819_STR IS NULL OR MI_APL_LEY1819_STR = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': APL LEY 1819 es obligatorio (SI/-1 o NO/0).' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        CASE UPPER(TRIM(MI_APL_LEY1819_STR))
            WHEN 'SI'  THEN MI_APL_LEY1819_NUM := -1;
            WHEN '-1'  THEN MI_APL_LEY1819_NUM := -1;
            WHEN 'NO'  THEN MI_APL_LEY1819_NUM :=  0;
            WHEN '0'   THEN MI_APL_LEY1819_NUM :=  0;
            ELSE
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': APL LEY 1819 "' || MI_APL_LEY1819_STR ||
                    '" debe ser SI, NO, -1 o 0.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END CASE;

        --------------------------------------------------------
        -- 1.10  CUENTA CREDITO 1: requerida si APL LEY 1819 = -1
        --------------------------------------------------------
        IF MI_APL_LEY1819_NUM = -1 THEN
            IF MI_CUENTA_CREDITO1 IS NULL OR MI_CUENTA_CREDITO1 = CN_NODATA THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': CUENTA CREDITO 1 es obligatoria cuando APL LEY 1819 = SI.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
            END IF;

            SELECT COUNT(1)
              INTO MI_EXISTE_CTA_CRED1
              FROM PLAN_CONTABLE
             WHERE COMPANIA    = UN_COMPANIA
               AND ANO         = MI_ANIO_NUM
               AND CODIGO      = MI_CUENTA_CREDITO1
               AND MOVIMIENTO <> 0
               AND CLASECUENTA = 'I';

            IF MI_EXISTE_CTA_CRED1 = 0 THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': La cuenta "' || MI_CUENTA_CREDITO1 ||
                    '" (Crédito 1) no existe en el Plan Contable del año ' || MI_ANIO_NUM ||
                    ', no tiene movimiento o no es clase I.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
            END IF;
        END IF;

        --------------------------------------------------------
        -- 1.11  FACTOR REDONDEO: obligatorio, valores 1/10/100/1000
        --------------------------------------------------------
        IF MI_FACTOR_RED_STR IS NULL OR MI_FACTOR_RED_STR = CN_NODATA THEN
            MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                'FILA ' || CONTADOR || ': FACTOR REDONDEO es obligatorio (1, 10, 100 o 1000).' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_FACTOR_RED_NUM := TO_NUMBER(MI_FACTOR_RED_STR);
            IF MI_FACTOR_RED_NUM NOT IN (1, 10, 100, 1000) THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': FACTOR REDONDEO "' || MI_FACTOR_RED_STR ||
                    '" debe ser 1, 10, 100 o 1000.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
            END IF;
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': FACTOR REDONDEO "' || MI_FACTOR_RED_STR ||
                    '" no es numérico.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        --------------------------------------------------------
        -- 1.12  MODIFICA VALOR: acepta SI/-1 o NO/0, default 0
        --------------------------------------------------------
        CASE UPPER(TRIM(NVL(MI_MODIFICA_VALOR_STR, '0')))
            WHEN 'SI'  THEN MI_MODIFICA_VALOR_NUM := -1;
            WHEN '-1'  THEN MI_MODIFICA_VALOR_NUM := -1;
            WHEN 'NO'  THEN MI_MODIFICA_VALOR_NUM :=  0;
            WHEN '0'   THEN MI_MODIFICA_VALOR_NUM :=  0;
            ELSE
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': MODIFICA VALOR "' || MI_MODIFICA_VALOR_STR ||
                    '" debe ser SI, NO, -1 o 0.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END CASE;

        --------------------------------------------------------
        -- 1.13  MODIFICA BASE: acepta SI/-1 o NO/0, default 0
        --------------------------------------------------------
        CASE UPPER(TRIM(NVL(MI_MODIFICA_BASE_STR, '0')))
            WHEN 'SI'  THEN MI_MODIFICA_BASE_NUM := -1;
            WHEN '-1'  THEN MI_MODIFICA_BASE_NUM := -1;
            WHEN 'NO'  THEN MI_MODIFICA_BASE_NUM :=  0;
            WHEN '0'   THEN MI_MODIFICA_BASE_NUM :=  0;
            ELSE
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': MODIFICA BASE "' || MI_MODIFICA_BASE_STR ||
                    '" debe ser SI, NO, -1 o 0.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END CASE;

        --------------------------------------------------------
        -- INSERT
        --------------------------------------------------------
        BEGIN
            MI_TABLA  := 'RETENCIONES';

            MI_CAMPOS :=
                'COMPANIA,ANO,TIPO,CODIGO,NOMBRE,' ||
                'CUENTA_CREDITO,CUENTA_CREDITO1,LIMITE_INF,PCT_BASE,PCT_APLICAR,' ||
                'ALEY1819,FACTORREDONDEO,PERMITEMODIFICAR,PERMITEMODIFICARBASE,' ||
                'PCT_APLICARLEY1607,CENTRO_COSTO,COD_AUXILIAR,PRORRATEADO,EST_ELECTRONICA,' ||
                'DATE_CREATED,CREATED_BY';

            MI_VALORES :=
                '''' || UN_COMPANIA            || ''',' ||
                MI_ANIO_NUM                    || ',' ||
                '''' || MI_TIPO_RETENCION      || ''',' ||
                '''' || MI_CODIGO_RETENCION    || ''',' ||
                '''' || MI_NOMBRE_RETENCION    || ''',' ||
                '''' || MI_CUENTA_CREDITO      || ''',' ||
               
                CASE
                    WHEN MI_APL_LEY1819_NUM = 0
                      OR MI_CUENTA_CREDITO1 IS NULL
                      OR MI_CUENTA_CREDITO1 = CN_NODATA
                    THEN 'NULL'
                    ELSE '''' || MI_CUENTA_CREDITO1 || ''''
                END                            || ',' ||
                MI_LIMITE_INF_NUM              || ',' ||
                MI_PCT_BASE_NUM                || ',' ||
                MI_PCT_APLICAR_NUM             || ',' ||
                MI_APL_LEY1819_NUM             || ',' ||
                MI_FACTOR_RED_NUM              || ',' ||
                MI_MODIFICA_VALOR_NUM          || ',' ||
                MI_MODIFICA_BASE_NUM           || ',' ||
                '0,'                           ||
                '99999999999999999999,'        ||   -- CENTRO_COSTO
                '99999999999999999999,'        ||   -- COD_AUXILIAR
                '0,'                           ||   -- PRORRATEADO
                '0,'                           ||   -- EST_ELECTRONICA
                'SYSDATE,'                     ||
                '''' || UN_USUARIO             || '''';

            MI_RTA := PCK_DATOS.FC_ACME(
                          UN_TABLA   => MI_TABLA,
                          UN_ACCION  => 'I',
                          UN_CAMPOS  => MI_CAMPOS,
                          UN_VALORES => MI_VALORES
                      );

            MI_INSERTADOS := MI_INSERTADOS + 1;

            MI_TEXTO_INSERTADOS := MI_TEXTO_INSERTADOS ||
                CHR(10) || 'Año: '    || MI_ANIO_NUM        ||
                ' | Tipo: '           || MI_TIPO_RETENCION   ||
                ' | Código: '         || MI_CODIGO_RETENCION ||
                ' | Nombre: '         || MI_NOMBRE_RETENCION;

        EXCEPTION
            WHEN OTHERS THEN
                MI_TEXTO_ERRORES := MI_TEXTO_ERRORES ||
                    'FILA ' || CONTADOR || ': Error al insertar - ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
        END;

    END LOOP DATOSRETENCION;

    -- ============================================================
    -- Construccion del mensaje final
    -- ============================================================
    MI_MENSAJE :=
        '============================================================' || CHR(10) ||
        'CARGUE MASIVO DE RETENCIONES'                                 || CHR(10) ||
        'Fecha: '     || TO_CHAR(SYSDATE, 'DD/MM/YYYY')               || CHR(10) ||
        'Compañía: '  || UN_COMPANIA                                   || CHR(10) ||
        'Mes: '       || UN_MES || ' | Día: ' || UN_DIA               || CHR(10) ||
        '============================================================' || CHR(10) ||
        'Registros procesados:  ' || CONTADOR                          || CHR(10) ||
        'Registros insertados:  ' || MI_INSERTADOS                     || CHR(10) ||
        'Registros omitidos:    ' || MI_OMITIDOS                       || CHR(10) ||
        '============================================================' || CHR(10);

    -- Seccion errores
    IF MI_OMITIDOS > 0 THEN
        MI_MENSAJE := MI_MENSAJE
            || CHR(10) || 'DETALLE DE ERRORES:'
            || CHR(10) || '------------------------------------------------------------'
            || CHR(10) || MI_TEXTO_ERRORES
            || '------------------------------------------------------------' || CHR(10);
    END IF;

    -- Seccion insertados
    MI_MENSAJE := MI_MENSAJE
        || CHR(10) || 'REGISTROS INSERTADOS CORRECTAMENTE:'
        || CHR(10) || '------------------------------------------------------------'
        || MI_TEXTO_INSERTADOS
        || CHR(10) || '------------------------------------------------------------'
        || CHR(10) || '============================================================';

    RETURN MI_MENSAJE;

EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR_GENERAL|Fila ' || CONTADOR || ' - ' || SQLERRM;

END FC_CARGUE_MASIVO_RETENCION;

END PCK_CONTABILIDAD3;