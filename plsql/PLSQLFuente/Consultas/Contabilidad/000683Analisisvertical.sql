WITH SALDOS AS (s$baseBalance$s)
SELECT SALDOAUX.SALDOs$mes$s SALDO,
       SALDOAUX.TERCERO,
       SALDOAUX.AUXILIAR,
       SALDOAUX.CENTRO_COSTO,
       SALDOAUX.NOMBRE PLAN_CONTABLE_NOMBRE,
       s$mes$s MES,
       CASE WHEN PREDECESOR IS NULL 
          THEN 100
          ELSE
            CASE WHEN PLAN_CONTABLE_1.SALDOs$mes$s <>0 
              THEN SALDOAUX.SALDOs$mes$s/PLAN_CONTABLE_1.SALDOs$mes$s*100 
              ELSE 0 
              END 
          END EXPR1,
       SALDOAUX.COMPANIA, 
       SALDOAUX.CODIGO, 
       SALDOAUX.ANO, 
       SUBSTR(SALDOAUX.CODIGO,0,1) CLASE,
       SUBSTR((CASE WHEN SUBSTR(SALDOAUX.CODIGO,0,1)='0' THEN 'Z' || SALDOAUX.CODIGO ELSE SALDOAUX.CODIGO END),0,1) CLASEORDEN
  FROM (SALDOS) SALDOAUX LEFT JOIN (SELECT PLAN_CONTABLE.COMPANIA,
                                           PLAN_CONTABLE.ANO,
                                           PLAN_CONTABLE.CODIGO HIJO,
                                           MAX(P.CODIGO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) PREDECESOR
                                      FROM PLAN_CONTABLE 
                                        LEFT JOIN PLAN_CONTABLE P
                                          ON PLAN_CONTABLE.COMPANIA   = P.COMPANIA
                                         AND PLAN_CONTABLE.ANO        = P.ANO     
                                         AND PLAN_CONTABLE.CODIGO    <> P.CODIGO
                                         AND P.CODIGO= SUBSTR(PLAN_CONTABLE.CODIGO,1,LENGTH(P.CODIGO))
                                      WHERE PLAN_CONTABLE.COMPANIA = s$compania$s
                                        AND PLAN_CONTABLE.ANO = s$anio$s
                                     GROUP BY PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, PLAN_CONTABLE.CODIGO)  PREDECESOR
      ON SALDOAUX.COMPANIA   = PREDECESOR.COMPANIA
     AND SALDOAUX.ANO        = PREDECESOR.ANO     
     AND SALDOAUX.CODIGO     = PREDECESOR.HIJO
  LEFT JOIN PLAN_CONTABLE PLAN_CONTABLE_1
      ON PREDECESOR.COMPANIA   = PLAN_CONTABLE_1.COMPANIA
     AND PREDECESOR.ANO        = PLAN_CONTABLE_1.ANO     
     AND PREDECESOR.PREDECESOR = PLAN_CONTABLE_1.CODIGO
  WHERE LENGTH(SALDOAUX.CODIGO)<= s$digitos$s
       AND SALDOAUX.CODIGO  BETWEEN s$codigoInicial$s AND s$codigoFin$s
 s$saldoCero$s
GROUP BY SALDOAUX.SALDOs$mes$s,
         SALDOAUX.TERCERO,
         SALDOAUX.AUXILIAR,
         SALDOAUX.CENTRO_COSTO,
         SALDOAUX.NOMBRE,
         s$mes$s,
         CASE WHEN PREDECESOR IS NULL 
          THEN 100
          ELSE
            CASE WHEN PLAN_CONTABLE_1.SALDOs$mes$s <>0 
              THEN SALDOAUX.SALDOs$mes$s/PLAN_CONTABLE_1.SALDOs$mes$s*100 
              ELSE 0 
              END 
          END,
         SALDOAUX.COMPANIA, 
         SALDOAUX.CODIGO, 
         SALDOAUX.ANO, 
         SUBSTR(SALDOAUX.CODIGO,0,1),
         SUBSTR((CASE WHEN SUBSTR(SALDOAUX.CODIGO,0,1)='0' THEN 'Z' || SALDOAUX.CODIGO ELSE SALDOAUX.CODIGO END),0,1)
