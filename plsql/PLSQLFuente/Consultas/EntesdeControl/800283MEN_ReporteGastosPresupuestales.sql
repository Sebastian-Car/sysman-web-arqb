SELECT COMPANIA.COD_DANE_EE                           "Código Establecimiento",
       TO_CHAR(PLAN_PRESUPUESTAL.ANO)                          " Año",
         (CASE WHEN s$mesFinal$s=3   THEN 1
            ELSE (CASE WHEN s$mesFinal$s=6 THEN 2
            ELSE (CASE WHEN s$mesFinal$s=9 THEN 3
            ELSE (CASE WHEN s$mesFinal$s=12 THEN 4
             END)
                END)
                   END)
                        END)                            "Trimestre ",
        PLAN_PPTAL_CONFIG.FUENTES_MEN                  "Fuente de ingreso",        
         PLAN_PPTAL_CONFIG.CONCEPTOS_MEN                "Item Detalle", 
       SUM(CASE WHEN SALDO_AUX_PPTAL.MES < s$mesFinal$s
                THEN CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                          THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO
                          ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO  
                          END
                 END
            )                                                 "Presupuesto inicial",
       SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                          THEN   APROPIACION_DEBITO  - APROPIACION_CREDITO - TRASLADO_CREDITO + TRASLADO_DEBITO 
                          ELSE - APROPIACION_DEBITO  + APROPIACION_CREDITO + TRASLADO_CREDITO - TRASLADO_DEBITO 
                          END + ADICION + REDUCCION
           )                                                                                  "Presupuesto definitivo",  
          
         SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN  s$mesInicial$s AND  s$mesFinal$s THEN
               SALDO_AUX_PPTAL.REG_CONTRACT 
               + SALDO_AUX_PPTAL.REG_NO_CONTRACT
               + SALDO_AUX_PPTAL.MODIF_REG_CONT
               + SALDO_AUX_PPTAL.MODIF_REG_NOCONT
            ELSE 0 END  
            )                                                                                "Compromisos", 
         SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND s$mesFinal$s THEN
               SALDO_AUX_PPTAL.REGISTRO_OBLIGACION
               + SALDO_AUX_PPTAL.MODIF_REGISTRO_OBLIGACION
            ELSE 0 END  
            )                                                                                  "Obligaciones" ,     
         SUM(CASE WHEN SALDO_AUX_PPTAL.MES BETWEEN s$mesInicial$s AND  s$mesFinal$s THEN
                CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA='D' 
                THEN   SALDO_AUX_PPTAL.EJE_PPT_DEBITO - SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                ELSE - SALDO_AUX_PPTAL.EJE_PPT_DEBITO + SALDO_AUX_PPTAL.EJE_PPT_CREDITO
                END 
            ELSE 0
            END
          )  "Pagos"  
             
FROM PLAN_PRESUPUESTAL INNER JOIN SALDO_AUX_PPTAL
  ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA 
 AND PLAN_PRESUPUESTAL.ANO      = SALDO_AUX_PPTAL.ANO
 AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_AUX_PPTAL.CODIGO
 INNER JOIN COMPANIA
   ON PLAN_PRESUPUESTAL.COMPANIA=COMPANIA.CODIGO
  INNER JOIN PLAN_PPTAL_CONFIG 
    ON PLAN_PPTAL_CONFIG.COMPANIA=SALDO_AUX_PPTAL.COMPANIA
    AND PLAN_PPTAL_CONFIG.ANO=SALDO_AUX_PPTAL.ANO
    AND PLAN_PPTAL_CONFIG.CODIGO=SALDO_AUX_PPTAL.CODIGO
    AND  PLAN_PPTAL_CONFIG.CENTRO_COSTO=SALDO_AUX_PPTAL.CENTRO_COSTO
    AND  PLAN_PPTAL_CONFIG.AUXILIAR=SALDO_AUX_PPTAL.AUXILIAR
    AND  PLAN_PPTAL_CONFIG.FUENTE_RECURSO=SALDO_AUX_PPTAL.FUENTE_RECURSO
LEFT JOIN FUENTES_MEN
     ON PLAN_PPTAL_CONFIG.FUENTES_MEN=FUENTES_MEN.CODIGO
LEFT JOIN  CONCEPTO_MEN
     ON PLAN_PPTAL_CONFIG.CONCEPTOS_MEN=CONCEPTO_MEN.CODIGO 
WHERE PLAN_PRESUPUESTAL.COMPANIA     = s$compania$s
  AND PLAN_PRESUPUESTAL.ANO          = s$ano$s
   AND PLAN_PRESUPUESTAL.REGALIAS IN(0)
   AND PLAN_PRESUPUESTAL.NATURALEZA   = 'D'
  AND PLAN_PRESUPUESTAL.TIPOVIGENCIA NOT IN('RC')
  AND SALDO_AUX_PPTAL.MES<=s$mesFinal$s
GROUP BY PLAN_PRESUPUESTAL.COMPANIA,
         TO_CHAR(PLAN_PRESUPUESTAL.ANO) ,
        COMPANIA.COD_DANE_EE,
       PLAN_PRESUPUESTAL.ANO,
         (CASE WHEN s$mesFinal$s=3   THEN 1
            ELSE (CASE WHEN s$mesFinal$s=6 THEN 2
            ELSE (CASE WHEN s$mesFinal$s=9 THEN 3
            ELSE (CASE WHEN s$mesFinal$s=12 THEN 4
             END)
                END)
                   END)
                        END),
         PLAN_PPTAL_CONFIG.FUENTES_MEN,
         PLAN_PPTAL_CONFIG.CONCEPTOS_MEN
