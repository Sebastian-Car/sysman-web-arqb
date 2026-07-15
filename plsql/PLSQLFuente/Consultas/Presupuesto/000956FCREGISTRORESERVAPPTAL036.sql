SELECT 
  DETALLE_COMPROBANTE_PPTAL.ANO, 
  PLAN_PRESUPUESTAL.AUXILIAR, 
  PLAN_PRESUPUESTAL.CENTRO_COSTO, 
  CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END) ='7'
       THEN 'RC'
       ELSE ''
       END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='8'
                 THEN 'OC'
                 ELSE ''
                 END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='10'
                           THEN 'TP'
                           ELSE ''
                           END CLASELETRAS,
  PLAN_PRESUPUESTAL.CODIGO, 
  DETALLE_COMPROBANTE_PPTAL.COMPROBANTE, 
  DETALLE_COMPROBANTE_PPTAL.DESCRIPCION, 
  PLAN_PRESUPUESTAL.DESTINO, 
  EXTRACT (DAY FROM DETALLE_COMPROBANTE_PPTAL.FECHA) DIA, 
  DETALLE_COMPROBANTE_PPTAL.FECHA, 
  PLAN_PRESUPUESTAL.ID, 
  DETALLE_COMPROBANTE_PPTAL.MES,
  PLAN_PRESUPUESTAL.NIVEL1, 
  PLAN_PRESUPUESTAL.NIVEL2, 
  PLAN_PRESUPUESTAL.NIVEL3, 
  PLAN_PRESUPUESTAL.NIVEL4, 
  PLAN_PRESUPUESTAL.NIVEL5, 
  PLAN_PRESUPUESTAL.NIVEL6, 
  PLAN_PRESUPUESTAL.NOMBRE, 
  CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        
                  THEN '7'        
                  ELSE ''       
                  END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)  ='8' 
       THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)
       ELSE 0
       END OBLIGACIONESCAUSADAS,
  CASE WHEN (CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END) ='7'        THEN 'RC'        ELSE ''        END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='8'                  THEN 'OC'                  ELSE ''                  END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='10'                            THEN 'TP'                            ELSE ''                            END)='RC'
       THEN 1
       ELSE (CASE WHEN (CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END) ='7'        THEN 'RC'        ELSE ''        END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='8'                  THEN 'OC'                  ELSE ''                  END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='10'                            THEN 'TP'                            ELSE ''                            END)='OC'
                  THEN 2
                  ELSE 3
                  END)
       END ORDENLETRAS, 
  CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)  ='7'
       THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) 
       ELSE 0
       END RESERVACONSTITUIDA,
  CASE WHEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)<0
       THEN '-'
       ELSE '+'
       END SIGNO,
  CASE WHEN CLASE IN('RES','DMR','ADR')
       THEN '7'
       ELSE ''
       END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')
                 THEN '8'
                 ELSE ''
                 END||CASE WHEN CLASE IN ('EGR','AEG','DEG')
                           THEN '10'
                           ELSE ''
                           END CLASECOMP, 
  CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)  ='10'
       THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) 
       ELSE 0
       END TOTALPAGOS,
  ABS((DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)) VALOR, 
  (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) VALORTOTAL 
FROM DETALLE_COMPROBANTE_PPTAL 
 LEFT JOIN TIPO_COMPROBPP 
  ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA 
  AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO 
 INNER JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
  ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA 
  AND DETALLE_COMPROBANTE_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO  
  AND DETALLE_COMPROBANTE_PPTAL.ID   = PLAN_PRESUPUESTAL.ID 
 LEFT JOIN COMPROBANTE_PPTAL
  ON  DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = COMPROBANTE_PPTAL.NUMERO
  AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = COMPROBANTE_PPTAL.TIPO
  AND DETALLE_COMPROBANTE_PPTAL.ANO         = COMPROBANTE_PPTAL.ANO
  AND DETALLE_COMPROBANTE_PPTAL.COMPANIA    = COMPROBANTE_PPTAL.COMPANIA
WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = s$compania$s
  AND DETALLE_COMPROBANTE_PPTAL.ANO      = s$anio$s 
  AND PLAN_PRESUPUESTAL.NATURALEZA       = 'D' 
  AND DETALLE_COMPROBANTE_PPTAL.MES      BETWEEN s$mesInicial$s AND s$mesFinal$s
  AND PLAN_PRESUPUESTAL.CODIGO           BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
  AND PLAN_PRESUPUESTAL.TIPOVIGENCIA IN('RA')
  s$centroCostoCond$s  
  s$fuenteRecursoCond$s   
  AND LENGTH(DETALLE_COMPROBANTE_PPTAL.CUENTA) BETWEEN 0 AND s$nivel$s
  AND (CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END) ='7'
       THEN 'RC'
       ELSE ''
       END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='8'
                 THEN 'OC'
                 ELSE ''
                 END||CASE WHEN (CASE WHEN CLASE IN('RES','DMR','ADR')        THEN '7'        ELSE ''        END||CASE WHEN CLASE IN ('REO','MRO','ARO','DRO')                  THEN '8'                  ELSE ''                  END||CASE WHEN CLASE IN ('EGR','AEG','DEG')                            THEN '10'                            ELSE ''                            END)='10'
                           THEN 'TP'
                           ELSE ''
                           END) IS NOT NULL
