SELECT V_SALDO_PLAN_PPTAL.COMPANIA, 
       V_SALDO_PLAN_PPTAL.CODIGO CUENTA, 
       V_SALDO_PLAN_PPTAL.ID,
       SUM(CASE WHEN NATURALEZA = 'D' 
                THEN APROPIACION_DEBITO - APROPIACION_CREDITO  
                ELSE APROPIACION_CREDITO - APROPIACION_DEBITO 
                END)  APROPIACIONINICIAL, 
       SUM(V_SALDO_PLAN_PPTAL.ADICION)  ADIC, 
       SUM(V_SALDO_PLAN_PPTAL.REDUCCION)  RED, 
       SUM(CASE WHEN NATURALEZA = 'D'
                THEN APLAZAM_DEBITO - APLAZAM_CREDITO
                ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                END)  APLAZAMIENTOS, 
       SUM(CASE WHEN NATURALEZA = 'D' 
                THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                END)  TRASLADOS, 
       SUM(V_SALDO_PLAN_PPTAL.ADICION) + SUM(V_SALDO_PLAN_PPTAL.REDUCCION) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                  THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                                                                                  ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                                                                                  END) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                  THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                                                                                                  ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                                                                                                  END)  MODIFICACIONES, 
       SUM(CASE WHEN NATURALEZA = 'D' 
                THEN APROPIACION_DEBITO - APROPIACION_CREDITO  
                ELSE APROPIACION_CREDITO - APROPIACION_DEBITO
                END)  + (SUM(V_SALDO_PLAN_PPTAL.ADICION) + SUM(V_SALDO_PLAN_PPTAL.REDUCCION) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                    THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                                                                                                    ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                                                                                                    END) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                                    THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                                                                                                                    ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                                                                                                                    END))  APROPIACIONACUMULADA, 
       SUM(CASE WHEN  NATURALEZA = 'D' 
                THEN  EJE_PPT_DEBITO - EJE_PPT_CREDITO 
                ELSE EJE_PPT_CREDITO - EJE_PPT_DEBITO
                END)  + SUM(V_SALDO_PLAN_PPTAL.MODIF_INGRESOS) EJECUTADO, 
       (SUM(CASE WHEN NATURALEZA = 'D' 
                 THEN APROPIACION_DEBITO - APROPIACION_CREDITO  
                 ELSE APROPIACION_CREDITO - APROPIACION_DEBITO
                 END)  + (SUM(V_SALDO_PLAN_PPTAL.ADICION) + SUM(V_SALDO_PLAN_PPTAL.REDUCCION) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                     THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                                                                                                     ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                                                                                                     END) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                                THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                                                                                                                ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                                                                                                                END))) - (SUM(CASE WHEN  NATURALEZA = 'D' 
                                                                                                                                  THEN  EJE_PPT_DEBITO - EJE_PPT_CREDITO
                                                                                                                                  ELSE EJE_PPT_CREDITO - EJE_PPT_DEBITO 
                                                                                                                                  END)
                                                                                                                +SUM(V_SALDO_PLAN_PPTAL.MODIF_INGRESOS))   POREJECUTARANO, 
       V_PLAN_PRESUPUESTAL.NOMBRE, 
       V_SALDO_PLAN_PPTAL.ANO, 
       MAX(V_SALDO_PLAN_PPTAL.MES)  MESF, 
      ((SUM(CASE WHEN  NATURALEZA = 'D' 
                THEN  EJE_PPT_DEBITO - EJE_PPT_CREDITO 
                ELSE EJE_PPT_CREDITO - EJE_PPT_DEBITO
                END)  + SUM(V_SALDO_PLAN_PPTAL.MODIF_INGRESOS))/CASE WHEN ( SUM(CASE WHEN NATURALEZA = 'D' 
                THEN APROPIACION_DEBITO - APROPIACION_CREDITO  
                ELSE APROPIACION_CREDITO - APROPIACION_DEBITO
                END)  + (SUM(V_SALDO_PLAN_PPTAL.ADICION) 
                + SUM(V_SALDO_PLAN_PPTAL.REDUCCION) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                    THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                                                                                                    ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                                                                                                    END) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                                    THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                                                                                                                    ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                                                                                                                    END))) IN (0) 
 	THEN 1 ELSE ( SUM(CASE WHEN NATURALEZA = 'D' 
                THEN APROPIACION_DEBITO - APROPIACION_CREDITO  
                ELSE APROPIACION_CREDITO - APROPIACION_DEBITO
                END)  + (SUM(V_SALDO_PLAN_PPTAL.ADICION) + SUM(V_SALDO_PLAN_PPTAL.REDUCCION) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                    THEN TRASLADO_DEBITO - TRASLADO_CREDITO 
                                                                                                    ELSE TRASLADO_CREDITO - TRASLADO_DEBITO 
                                                                                                    END) + SUM(CASE WHEN NATURALEZA = 'D' 
                                                                                                                    THEN APLAZAM_DEBITO - APLAZAM_CREDITO 
                                                                                                                    ELSE APLAZAM_CREDITO - APLAZAM_DEBITO 
                                                                                                                    END))) END)*100  PORCENTAJE 
FROM  V_PLAN_PRESUPUESTAL 
	INNER JOIN V_SALDO_PLAN_PPTAL 
		ON    V_PLAN_PRESUPUESTAL.COMPANIA = V_SALDO_PLAN_PPTAL.COMPANIA 
		AND   V_PLAN_PRESUPUESTAL.ANO      = V_SALDO_PLAN_PPTAL.ANO 
		AND   V_PLAN_PRESUPUESTAL.ID       = V_SALDO_PLAN_PPTAL.ID 
 
WHERE V_SALDO_PLAN_PPTAL.COMPANIA = s$compania$s
AND   V_SALDO_PLAN_PPTAL.ID BETWEEN 's$cuentaInicial$s'  AND 's$cuentaFinal$s'
AND   V_SALDO_PLAN_PPTAL.ANO = s$ano$s
AND   V_SALDO_PLAN_PPTAL.MES BETWEEN 0 AND s$mes$s
GROUP BY 
      V_SALDO_PLAN_PPTAL.COMPANIA, 
      V_SALDO_PLAN_PPTAL.ID, 
      V_SALDO_PLAN_PPTAL.CODIGO, 
      V_PLAN_PRESUPUESTAL.NOMBRE, 
      V_SALDO_PLAN_PPTAL.ANO, 
      V_PLAN_PRESUPUESTAL.NATURALEZA
