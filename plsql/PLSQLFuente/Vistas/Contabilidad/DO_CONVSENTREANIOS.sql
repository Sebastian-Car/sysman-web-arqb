CREATE OR REPLACE VIEW DO_CONVSENTREANIOS
AS WITH SALDOS AS (
        SELECT  PLAN_CONTABLE.COMPANIA, 
                PLAN_CONTABLE.ANO, 
                PLAN_CONTABLE.CODIGO,
                PLAN_CONTABLE.NOMBRE CUENTA_NOMBRE, 
                MES.NUMERO MES,
                MES.NOMBRE MES_NOMBRE,
                CASE MES.NUMERO WHEN 0 THEN PLAN_CONTABLE.SALDO0
                                WHEN 1 THEN PLAN_CONTABLE.SALDO1
                                WHEN 2 THEN PLAN_CONTABLE.SALDO2
                                WHEN 3 THEN PLAN_CONTABLE.SALDO3
                                WHEN 4 THEN PLAN_CONTABLE.SALDO4 
                                WHEN 5 THEN PLAN_CONTABLE.SALDO5        
                                WHEN 6 THEN PLAN_CONTABLE.SALDO6 
                                WHEN 7 THEN PLAN_CONTABLE.SALDO7 
                                WHEN 8 THEN PLAN_CONTABLE.SALDO8 
                                WHEN 9 THEN PLAN_CONTABLE.SALDO9
                                WHEN 10 THEN PLAN_CONTABLE.SALDO10
                                WHEN 11 THEN PLAN_CONTABLE.SALDO11
                                WHEN 12 THEN PLAN_CONTABLE.SALDO12
                                WHEN 13 THEN PLAN_CONTABLE.SALDO13
                                ELSE 0 END SALDO
        FROM PLAN_CONTABLE INNER JOIN MES
          ON PLAN_CONTABLE.COMPANIA = MES.COMPANIA
         AND PLAN_CONTABLE.ANO      = MES.ANO
        ) 
                
      , TOTALES AS (SELECT COMPANIA, ANO, CODIGO, MES, SALDO STOTAL
                   FROM SALDOS
                   WHERE LENGTH(CODIGO) =1
                   )                   
      , UNICO AS (  SELECT ACTUAL.COMPANIA,
                           ACTUAL.ANO ANO,
                           ACTUAL.CODIGO,
                           ACTUAL.CUENTA_NOMBRE,
                           ACTUAL.MES MES,
                           ACTUAL.MES_NOMBRE MES_NOMBRE,
                           ACTUAL.SALDO SALDO_ACTUAL,
                           COALESCE(ANTE.SALDO,   0) SALDO_ANTE,
                           ACTUAL.ANO ANO_ACTUAL,
                           ANTE.ANO ANO_ANTE
                    FROM (SALDOS ACTUAL LEFT JOIN SALDOS ANTE
                      ON ACTUAL.COMPANIA  = ANTE.COMPANIA
                     AND ACTUAL.ANO       = ANTE.ANO + 1
                     AND ACTUAL.CODIGO    = ANTE.CODIGO
                     AND ACTUAL.MES       = ANTE.MES)
                ) 
SELECT FIN.COMPANIA, 
       FIN.ANO,
       FIN.CODIGO,
       FIN.CUENTA_NOMBRE,
       FIN.MES,
       FIN.MES_NOMBRE,
       FIN.SALDO_ACTUAL,
       CASE WHEN T_ACT.STOTAL = 0 
            THEN 0 
            ELSE FIN.SALDO_ACTUAL / T_ACT.STOTAL 
       END PORC_ACTUAL,
       FIN.SALDO_ANTE,
       CASE WHEN T_ANT.STOTAL = 0 
            THEN 0 
            ELSE FIN.SALDO_ANTE / T_ANT.STOTAL 
       END PORC_ANTE,
       FIN.SALDO_ACTUAL - FIN.SALDO_ANTE VAR_ABSOLUTA,
       CASE FIN.SALDO_ANTE WHEN FIN.SALDO_ACTUAL THEN 0
                           WHEN 0 THEN 1
                           ELSE (FIN.SALDO_ACTUAL - FIN.SALDO_ANTE) / FIN.SALDO_ANTE
       END VAR_RELATIVA
FROM UNICO FIN INNER JOIN TOTALES T_ACT
  ON FIN.COMPANIA   = T_ACT.COMPANIA
 AND FIN.ANO_ACTUAL = T_ACT.ANO
 AND FIN.MES        = T_ACT.MES
 AND SUBSTR(FIN.CODIGO,1,1)   = T_ACT.CODIGO
LEFT JOIN TOTALES T_ANT
  ON FIN.COMPANIA   = T_ANT.COMPANIA
 AND FIN.ANO_ANTE   = T_ANT.ANO
 AND FIN.MES        = T_ANT.MES
 AND SUBSTR(FIN.CODIGO,1,1)   = T_ANT.CODIGO;


COMMENT ON TABLE DO_CONVSENTREANIOS IS 'Permite generar reporte de DASHONE de las situación financiera';