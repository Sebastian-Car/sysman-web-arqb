      SELECT  SUBSTR(V_SALDO_PLAN_PPTAL.ID,1,1) AS CLASE, 
              V_SALDO_PLAN_PPTAL.COMPANIA, 
              V_SALDO_PLAN_PPTAL.ANO, 
              V_SALDO_PLAN_PPTAL.ID CUENTA, 
              V_PLAN_PRESUPUESTAL.NOMBRE, 
              V_PLAN_PRESUPUESTAL.TIPOVIGENCIA, 
              CASE WHEN TIPOVIGENCIA='RC' OR TIPOVIGENCIA='RA' 
                THEN  
                  SUM(CASE WHEN s$miles$s = 1  
                    THEN  
                    ROUND((APROPIACION_DEBITO-APROPIACION_CREDITO) + V_SALDO_PLAN_PPTAL.ADICION + 
                            TRASLADO_DEBITO + APLAZAM_DEBITO + V_SALDO_PLAN_PPTAL.REDUCCION - 
                             (TRASLADO_CREDITO + APLAZAM_CREDITO),-3)/1000 
                    ELSE (APROPIACION_DEBITO-APROPIACION_CREDITO) + V_SALDO_PLAN_PPTAL.ADICION +  
                              TRASLADO_DEBITO + APLAZAM_DEBITO + V_SALDO_PLAN_PPTAL.REDUCCION -  
                              (TRASLADO_CREDITO + APLAZAM_CREDITO) 
                    END)           
                ELSE  
                  SUM(CASE WHEN s$miles$s = 1  
                    THEN  ROUND(V_SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION + V_SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION,-3)/1000 
                    ELSE V_SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION + V_SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION 
                  END) 
              END OBLIGACIONESACUM, 
             SUM(CASE WHEN s$miles$s = 1  
              THEN ROUND((EJE_PPT_DEBITO - EJE_PPT_CREDITO),3)/1000 
                ELSE (EJE_PPT_DEBITO - EJE_PPT_CREDITO) 
                     END) PAGOSACUM, 
             SUM(CASE WHEN V_SALDO_PLAN_PPTAL.MES = s$mes$s THEN  
                    CASE WHEN s$miles$s = 1  
                      THEN ROUND((EJE_PPT_DEBITO - EJE_PPT_CREDITO),3)/1000 
                      ELSE (EJE_PPT_DEBITO - EJE_PPT_CREDITO) 
                      END 
                 END) PAGOSMES          
      FROM V_PLAN_PRESUPUESTAL 
        INNER JOIN V_SALDO_PLAN_PPTAL 
          ON  V_PLAN_PRESUPUESTAL.COMPANIA    = V_SALDO_PLAN_PPTAL.COMPANIA  
          AND V_PLAN_PRESUPUESTAL.ANO         = V_SALDO_PLAN_PPTAL.ANO 
          AND V_PLAN_PRESUPUESTAL.ID          = V_SALDO_PLAN_PPTAL.ID 
      WHERE V_SALDO_PLAN_PPTAL.COMPANIA = s$compania$s 
       	AND V_SALDO_PLAN_PPTAL.ANO = s$ano$s 
        AND V_SALDO_PLAN_PPTAL.ID BETWEEN s$cuentaInicial$s AND s$cuentaFinal$s  
       	AND V_SALDO_PLAN_PPTAL.MES BETWEEN 0 AND s$mes$s 
        AND V_PLAN_PRESUPUESTAL.NATURALEZA = 'D' 
        AND LENGTH(V_SALDO_PLAN_PPTAL.ID) <= NVL(s$nivel$s,6) 
        AND V_PLAN_PRESUPUESTAL.INFORME NOT IN(0) 
      GROUP BY  SUBSTR(V_SALDO_PLAN_PPTAL.ID,1,1) , 
                V_SALDO_PLAN_PPTAL.COMPANIA, 
                V_SALDO_PLAN_PPTAL.ANO, 
                V_SALDO_PLAN_PPTAL.ID, 
                V_PLAN_PRESUPUESTAL.NOMBRE, 
                V_PLAN_PRESUPUESTAL.TIPOVIGENCIA
