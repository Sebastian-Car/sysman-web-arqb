WITH SALDO_PPTAL AS(
  SELECT 
       SUM(OBLIGACIONESACUM        ) OBLIGACIONESACUM, 
       SUM(EJECUCIONPPT            ) PAGOSACUM,
       SUM(APROPIACIONVIGENTE      ) APROPIACIONVIGENTE,
       SUM(DISPONIBILIDAD          ) DISPONIBILIDADACUM,
       SUM(COMPROMISOSACUM         ) COMPROMISOSACUM,
       SUM(TOTALREO                ) OBLIGACIONES,
       SUM(EJECUCIONPPT - REINTEGRO) TOTALPAGOS,
       ID,
       COMPANIA, 
       ANO,
       MES
 FROM V_RESUMENPPTO_BASE
 GROUP BY COMPANIA, 
		   ANO, 
		   ID, 
       MES, 
		   CODIGO,
		   NATURALEZA,
		   CENTRO_COSTO,
		   TERCERO,
		   SUCURSAL,
		   AUXILIAR,
		   REFERENCIA,  
		   FUENTE_RECURSO,
		   MOVIMIENTO
)
SELECT PLAN_PRESUPUESTAL.APROPIACIONINICIAL APRINICIAL,
       TO_CHAR(DETALLE_COMPROBANTE_PPTAL.MES) MES,
       TO_CHAR(DETALLE_COMPROBANTE_PPTAL.DIA) DIA,
       TO_CHAR(DETALLE_COMPROBANTE_PPTAL.ANO) ANO,
       TO_CHAR(DETALLE_COMPROBANTE_PPTAL.COMPROBANTE) COMPROBANTE,
 
       DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
       TIPO_COMPROBPP.CLASE, 
       (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) VALORTOTAL,
       CASE WHEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) < 0
            THEN '-'
            ELSE '+' 
       END SIGNO,
       ABS(DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) VALOR, 
       CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
            THEN '7'
            ELSE '' 
       END || 
       CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
            THEN '8' 
            ELSE '' 
       END CLASECOMP,
       CASE CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                 THEN '7'
                 ELSE '' 
            END || 
            CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                 THEN '8' 
                 ELSE '' 
            END
            WHEN '7' THEN 'CPC'
            WHEN '8' THEN 'P'
            ELSE ''
       END CLASELETRAS,
       CASE CASE (CASE WHEN TIPO_COMPROBPP.CLASE IN ('REO', 'MRO', 'ARO','DRO')
                       THEN '7'
                       ELSE '' 
                  END || 
                  CASE WHEN TIPO_COMPROBPP.CLASE IN ('EGR', 'AEG', 'DEG')
                       THEN '8' 
                       ELSE '' 
                  END) 
                 WHEN '7' THEN 'CPC'
                 WHEN '8' THEN 'P'
                 ELSE ''
            END 
            WHEN 'CPC' THEN  CASE WHEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) < 0
                                  THEN SALDO_PPTAL.OBLIGACIONESACUM - ABS((DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO))
                                  ELSE SALDO_PPTAL.OBLIGACIONESACUM + ABS((DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)) 
                             END
       END CXPCONSTITUIDAS, 
       CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                       THEN '7'
                       ELSE '' 
                  END || 
                  CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                       THEN '8' 
                       ELSE '' 
                  END) = '7'
                 THEN DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO 
                 ELSE 0 
       END INICIAL,
       CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                       THEN '7'
                       ELSE '' 
                  END || 
                  CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                       THEN '8' 
                       ELSE '' 
                  END) = '7'
            THEN DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO 
            ELSE 0 
       END CANCELACION,
       CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                       THEN '7'
                       ELSE '' 
                  END || 
                  CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                       THEN '8' 
                       ELSE '' 
                  END) = '8' 
            THEN DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO 
            ELSE 0 
       END EJECUCION,
       CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                       THEN '7'
                       ELSE '' 
                  END || 
                  CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                       THEN '8' 
                       ELSE '' 
                  END) ='8' 
            THEN DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO 
            ELSE 0 
       END REVERSION,
       CASE CASE (CASE WHEN TIPO_COMPROBPP.CLASE IN ('REO', 'MRO', 'ARO','DRO')
                       THEN '7'
                       ELSE '' 
                  END || 
                  CASE WHEN TIPO_COMPROBPP.CLASE IN ('EGR', 'AEG', 'DEG')
                       THEN '8' 
                       ELSE '' 
                  END) 
                 WHEN '7' THEN 'CPC'
                 WHEN '8' THEN 'P'
                 ELSE ''
            END 
            WHEN 'P' THEN  CASE WHEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) < 0
                                THEN SALDO_PPTAL.PAGOSACUM - ABS((DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO))
                                ELSE SALDO_PPTAL.PAGOSACUM + ABS((DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)) 
                           END
       END TOTALPAGOS,
       DETALLE_COMPROBANTE_PPTAL.DESCRIPCION,
       PLAN_PRESUPUESTAL.NOMBRE,  
       PLAN_PRESUPUESTAL.NIVEL1,
       PLAN_PRESUPUESTAL.NIVEL2,
       PLAN_PRESUPUESTAL.NIVEL3,
       PLAN_PRESUPUESTAL.NIVEL4,
       PLAN_PRESUPUESTAL.NIVEL5,
       PLAN_PRESUPUESTAL.NIVEL6,
       PLAN_PRESUPUESTAL.NIVEL7,
       PLAN_PRESUPUESTAL.ID , 
       PLAN_PRESUPUESTAL.CODIGO, 
       PLAN_PRESUPUESTAL.NATURALEZA, 
       LOWER(TO_CHAR(DETALLE_COMPROBANTE_PPTAL.FECHA, 'DD-MON-YY')) FECHA,
       CASE PLAN_PRESUPUESTAL.DESTINO 
            WHEN 'F' THEN 'Funcionamiento'
            WHEN 'I' THEN 'Inversión'
            WHEN 'S' THEN 'Servicio de la deuda'
       END DESTINO,
       DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO, 
       PLAN_PRESUPUESTAL.TIPOVIGENCIA, 
       TERCERO.NOMBRE AS NOMBRETERCERO,
       ((CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                         THEN '7'
                         ELSE ''
                    END || 
                    CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                         THEN '8' 
                         ELSE ''
                    END) = '7' 
              THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)
              ELSE 0 
         END) - 
         (CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE IN('REO', 'MRO', 'ARO', 'DRO')
                          THEN '7'
                          ELSE '' 
                     END || 
                     CASE WHEN TIPO_COMPROBPP.CLASE IN('EGR', 'AEG', 'DEG')
                          THEN '8' 
                          ELSE '' 
                     END) = '8' 
               THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) 
               ELSE 0 
          END)) CUENTASXPAGAR
  FROM TERCERO
       INNER JOIN DETALLE_COMPROBANTE_PPTAL
             INNER JOIN TIPO_COMPROBPP
                     ON DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA 
                    AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
             INNER JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
                     ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA 
                    AND DETALLE_COMPROBANTE_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO 
                    AND DETALLE_COMPROBANTE_PPTAL.CUENTA   = PLAN_PRESUPUESTAL.ID 
             INNER JOIN COMPROBANTE_PPTAL
                     ON DETALLE_COMPROBANTE_PPTAL.COMPANIA    = COMPROBANTE_PPTAL.COMPANIA 
                    AND DETALLE_COMPROBANTE_PPTAL.ANO         = COMPROBANTE_PPTAL.ANO 
                    AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = COMPROBANTE_PPTAL.TIPO 
                    AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = COMPROBANTE_PPTAL.NUMERO  
               ON TERCERO.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA 
              AND TERCERO.NIT      = DETALLE_COMPROBANTE_PPTAL.TERCERO  
              AND TERCERO.SUCURSAL = DETALLE_COMPROBANTE_PPTAL.SUCURSAL  
       LEFT JOIN SALDO_PPTAL
              ON SALDO_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
             AND SALDO_PPTAL.ID       = PLAN_PRESUPUESTAL.ID
             AND SALDO_PPTAL.ANO      = DETALLE_COMPROBANTE_PPTAL.ANO
             AND SALDO_PPTAL.MES      = (DETALLE_COMPROBANTE_PPTAL.MES - 1)
 WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA    = s$compania$s 
   AND PLAN_PRESUPUESTAL.ID BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s' 
   AND DETALLE_COMPROBANTE_PPTAL.ANO = s$anio$s  
   AND DETALLE_COMPROBANTE_PPTAL.MES BETWEEN s$mesInicial$s AND s$mesFinal$s  
   AND LENGTH(DETALLE_COMPROBANTE_PPTAL.CUENTA) BETWEEN 0 AND s$nivel$s  
   AND PLAN_PRESUPUESTAL.NATURALEZA        ='D' 
   AND CASE (CASE WHEN TIPO_COMPROBPP.CLASE IN ('REO', 'MRO', 'ARO','DRO')
                  THEN '7'
 
                  ELSE '' 
             END || 
             CASE WHEN TIPO_COMPROBPP.CLASE IN ('EGR', 'AEG', 'DEG')
                  THEN '8' 
                  ELSE '' 
             END) 
             WHEN '7' THEN 'CPC'
             WHEN '8' THEN 'P'
             ELSE ''
       END IS NOT NULL     
 s$sqlUnion$s  
