SELECT  PLAN.COMPANIA,  
        PLAN.ID  CODIGO,  
        PLAN.NOMBRE,  
        NVL(PLAN_ANTERIOR.SALDOs$mesTrabajo$s,0) SALDO_ANTERIOR,  
        NVL(PLAN_NUEVO.SALDOs$mesComparar$s,0)  SALDO_NUEVO,  
        NVL(PLAN_NUEVO.SALDOs$mesComparar$s,0) - NVL(PLAN_ANTERIOR.SALDOs$mesTrabajo$s,0) VARIACION, 
        SUBSTR(PLAN.ID,1,1)  CLASE  
FROM (SELECT COMPANIA, ID, MIN(NOMBRE) NOMBRE
        FROM V_PLAN_CONTABLE
       WHERE COMPANIA=s$compania$s 
         AND (ANO=s$anoTrabajo$s OR ANO=s$anoComparar$s)
         AND CLASECUENTA IN('T') 
         AND (TRUNC(SALDOs$mesTrabajo$s*100+0.501)<>0 OR TRUNC(SALDOs$mesComparar$s*100+0.501)<>0) 
         AND (MOVIMIENTO + MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_REF + MAN_AUX_GEN + MAN_AUX_FUE  ) <> 0
        GROUP BY COMPANIA, ID
      ) PLAN
 LEFT JOIN (SELECT COMPANIA, ID, SALDOs$mesTrabajo$s
            FROM V_PLAN_CONTABLE
            WHERE COMPANIA=s$compania$s 
              AND ANO=s$anoTrabajo$s  
              AND CLASECUENTA IN ('T')
              AND (TRUNC(SALDOs$mesTrabajo$s*100+0.501)<>0 OR TRUNC(SALDOs$mesComparar$s*100+0.501)<>0) 
              AND (MOVIMIENTO + MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_REF + MAN_AUX_GEN + MAN_AUX_FUE  ) <> 0
            )PLAN_ANTERIOR  
  ON PLAN_ANTERIOR.COMPANIA = PLAN.COMPANIA
 AND PLAN_ANTERIOR.ID       = PLAN.ID 
 LEFT JOIN (SELECT COMPANIA, ID, SALDOs$mesComparar$s
            FROM V_PLAN_CONTABLE
            WHERE COMPANIA=s$compania$s  
              AND ANO=s$anoComparar$s 
              AND CLASECUENTA IN ('T')
              AND (TRUNC(SALDOs$mesTrabajo$s*100+0.501)<>0 OR TRUNC(SALDOs$mesComparar$s*100+0.501)<>0) 
              AND (MOVIMIENTO + MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_REF + MAN_AUX_GEN + MAN_AUX_FUE  ) <> 0
            )  PLAN_NUEVO 
   ON PLAN.COMPANIA = PLAN_NUEVO.COMPANIA   
  AND PLAN.ID       = PLAN_NUEVO.ID 
ORDER BY PLAN.ID
