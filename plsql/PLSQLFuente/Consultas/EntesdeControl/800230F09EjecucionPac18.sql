SELECT PLAN_PPTAL_CONFIG.CODIGO_SIA||SALDO_AUX_PPTAL.CODIGO                            "Código Rubro Presupuestal",
       V_PLAN_PRESUPUESTAL.NOMBRE                                                      "Nombre Rubro Presupuestal",
       SUM(CASE WHEN SALDO_AUX_PPTAL.MES  <=s$mesFinal$s
             THEN  SALDO_AUX_PPTAL.PAC_APROPIADO
             ELSE 0 END)                                                                "Pac Período Rendido", 
       '0'                                                                              "Anticipos",
       SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s               
                THEN SALDO_AUX_PPTAL.ADICION 
                ELSE 0 END)                                                             "Adiciones",   
      SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s
                THEN SALDO_AUX_PPTAL.REDUCCION*-1 
                ELSE 0 END)                                                             "Reducciones",
      SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s 
                THEN (CASE WHEN V_PLAN_PRESUPUESTAL.NATURALEZA='D' 
                         THEN  SALDO_AUX_PPTAL.APLAZAM_CREDITO-SALDO_AUX_PPTAL.APLAZAM_CREDITO 
                         ELSE  SALDO_AUX_PPTAL.APLAZAM_DEBITO -SALDO_AUX_PPTAL.APLAZAM_CREDITO 
                         END
                       ) 
                ELSE 0 END)                                                             "Aplazamientos",
     SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s THEN
                CASE WHEN V_PLAN_PRESUPUESTAL.NATURALEZA='D' 
                THEN   SALDO_AUX_PPTAL.EJE_PPT_DEBITO - SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                ELSE - SALDO_AUX_PPTAL.EJE_PPT_DEBITO + SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                END 
            ELSE 0
            END)                                                                        "Pac Situado",
     SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s THEN
                CASE WHEN V_PLAN_PRESUPUESTAL.NATURALEZA='D' 
                THEN   SALDO_AUX_PPTAL.EJE_PPT_DEBITO - SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                ELSE - SALDO_AUX_PPTAL.EJE_PPT_DEBITO + SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                END 
            ELSE 0
            END
          )                                                                              "Pagos"         
FROM SALDO_AUX_PPTAL INNER JOIN V_PLAN_PRESUPUESTAL
    ON SALDO_AUX_PPTAL.COMPANIA= V_PLAN_PRESUPUESTAL.COMPANIA
    AND SALDO_AUX_PPTAL.ANO= V_PLAN_PRESUPUESTAL.ANO
    AND SALDO_AUX_PPTAL.CODIGO= V_PLAN_PRESUPUESTAL.CODIGO 
    AND SALDO_AUX_PPTAL.CENTRO_COSTO= V_PLAN_PRESUPUESTAL.CENTRO_COSTO 
    AND SALDO_AUX_PPTAL.AUXILIAR= V_PLAN_PRESUPUESTAL.AUXILIAR 
    AND SALDO_AUX_PPTAL.REFERENCIA= V_PLAN_PRESUPUESTAL.REFERENCIA
    AND SALDO_AUX_PPTAL.FUENTE_RECURSO= V_PLAN_PRESUPUESTAL.FUENTE_RECURSO
 INNER JOIN PLAN_PPTAL_CONFIG
     ON SALDO_AUX_PPTAL.COMPANIA= PLAN_PPTAL_CONFIG.COMPANIA
    AND SALDO_AUX_PPTAL.ANO= PLAN_PPTAL_CONFIG.ANO
    AND SALDO_AUX_PPTAL.CODIGO= PLAN_PPTAL_CONFIG.CODIGO 
    AND SALDO_AUX_PPTAL.CENTRO_COSTO= PLAN_PPTAL_CONFIG.CENTRO_COSTO 
    AND SALDO_AUX_PPTAL.AUXILIAR= PLAN_PPTAL_CONFIG.AUXILIAR 
    AND SALDO_AUX_PPTAL.REFERENCIA= PLAN_PPTAL_CONFIG.REFERENCIA
    AND SALDO_AUX_PPTAL.FUENTE_RECURSO= PLAN_PPTAL_CONFIG.FUENTE_RECURSO    
WHERE SALDO_AUX_PPTAL.COMPANIA     = s$compania$s
  AND SALDO_AUX_PPTAL.ANO          = s$ano$s
  AND SALDO_AUX_PPTAL.MES <=s$mesFinal$s
  AND V_PLAN_PRESUPUESTAL.NATURALEZA='D'
  AND V_PLAN_PRESUPUESTAL.TIPOVIGENCIA NOT IN ('RA')
  AND V_PLAN_PRESUPUESTAL.REGALIAS IN (0) 
GROUP BY PLAN_PPTAL_CONFIG.CODIGO_SIA||SALDO_AUX_PPTAL.CODIGO,
         V_PLAN_PRESUPUESTAL.NOMBRE   
ORDER BY PLAN_PPTAL_CONFIG.CODIGO_SIA||SALDO_AUX_PPTAL.CODIGO 
