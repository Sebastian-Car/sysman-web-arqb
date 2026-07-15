SELECT DETALLE_COMPROBANTE_PPTAL.MES,
       EXTRACT (DAY FROM DETALLE_COMPROBANTE_PPTAL.FECHA) DIA, 
       DETALLE_COMPROBANTE_PPTAL.ANO, 
       DETALLE_COMPROBANTE_PPTAL.COMPROBANTE, 
       (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) VALORTOTAL, 
       CASE WHEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)<0
       THEN '-' 
       ELSE '+'
       END SIGNO,
 
       ABS(DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)
 VALOR,
       CASE  WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC' 
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)='10'
             THEN 'VA'
             ELSE ''
             END||CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)='12'
                       THEN 'VFA'
                       ELSE ''
                       END||CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)='13'
                                 THEN 'VFC'
                                 ELSE ''
                                 END CLASELETRAS,
        CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)  ='10'
             THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)
             ELSE 0
             END VIGENCIAACTUAL,
      CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
           THEN '10'
           ELSE ''
           END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                     THEN '12'
                     ELSE ''
                     END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                               THEN '13'
                               ELSE ''
                               END)  ='12'
           THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)
           ELSE 0
           END AUTORIZADA,
      CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
           THEN '10'
           ELSE ''
           END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                     THEN '12'
                     ELSE ''
                     END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                               THEN '13'
                               ELSE ''
                               END)  ='13'
           THEN (DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO-DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO)
           ELSE 0
           END COMPROMETIDA,
     DETALLE_COMPROBANTE_PPTAL.DESCRIPCION,
     PLAN_PRESUPUESTAL.NOMBRE,
     PLAN_PRESUPUESTAL.NIVEL1,
     PLAN_PRESUPUESTAL.NIVEL2,
     PLAN_PRESUPUESTAL.NIVEL3,
     PLAN_PRESUPUESTAL.NIVEL4,
     PLAN_PRESUPUESTAL.NIVEL5,
     PLAN_PRESUPUESTAL.NIVEL6,
     PLAN_PRESUPUESTAL.ID,
     PLAN_PRESUPUESTAL.NATURALEZA,
     DETALLE_COMPROBANTE_PPTAL.FECHA,
     PLAN_PRESUPUESTAL.DESTINO,
     CASE WHEN DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO IS NULL
          THEN ''
          ELSE SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,LENGTH(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO)-INSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,' '))
          END NRO_DOCUMENTO,
     CASE WHEN SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,0,2)='Le' 
          THEN 'L' 
          ELSE '' 
          END||CASE WHEN SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,0,2)='De' 
                    THEN 'D' 
                    ELSE '' 
                    END||CASE WHEN SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,0,2)='Ac' 
                              THEN 'A' 
                              ELSE '' 
                              END||CASE WHEN SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,0,2)='Au' 
                                        THEN 'AUT' 
                                        ELSE '' 
                                        END||CASE WHEN SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,0,3)='Com' 
                                                  THEN 'CO' 
                                                  ELSE '' 
                                                  END||CASE WHEN SUBSTR(DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO,0,3)='Con' 
                                                            THEN 'CTR' 
                                                            ELSE '' 
                                                            END TIPODOCUMENTO,
  PLAN_PRESUPUESTAL.VIGENCIA, 
  PLAN_PRESUPUESTAL.AUXILIAR, 
  PLAN_PRESUPUESTAL.CENTRO_COSTO  
FROM COMPROBANTE_PPTAL 
 INNER JOIN DETALLE_COMPROBANTE_PPTAL
  ON  COMPROBANTE_PPTAL.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA 
  AND COMPROBANTE_PPTAL.ANO      = DETALLE_COMPROBANTE_PPTAL.ANO 
  AND COMPROBANTE_PPTAL.TIPO     = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE 
  AND COMPROBANTE_PPTAL.NUMERO   = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE 
 LEFT JOIN TIPO_COMPROBPP
  ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA  = TIPO_COMPROBPP.COMPANIA
  AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
 INNER JOIN V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL
  ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA 
  AND DETALLE_COMPROBANTE_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO 
  AND DETALLE_COMPROBANTE_PPTAL.ID   = PLAN_PRESUPUESTAL.ID 
WHERE COMPROBANTE_PPTAL.COMPANIA     = s$compania$s 
  AND DETALLE_COMPROBANTE_PPTAL.ANO  = s$anio$s
  AND PLAN_PRESUPUESTAL.TIPOVIGENCIA = 'VF' 
  AND DETALLE_COMPROBANTE_PPTAL.MES BETWEEN s$mesInicial$s AND s$mesFinal$s 
  AND PLAN_PRESUPUESTAL.ID          BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s' 
  s$centroCostoCond$s
  s$fuenteRecursoCond$s
  AND (CASE  WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC' 
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)='10'
             THEN 'VA'
             ELSE ''
             END||CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)='12'
                       THEN 'VFA'
                       ELSE ''
                       END||CASE WHEN (CASE WHEN TIPO_COMPROBPP.CLASE='VAC'
             THEN '10'
             ELSE ''
             END||CASE WHEN TIPO_COMPROBPP.CLASE='APR'
                       THEN '12'
                       ELSE ''
                       END||CASE WHEN TIPO_COMPROBPP.CLASE IN ('RES','DMR','ADR')
                                 THEN '13'
                                 ELSE ''
                                 END)='13'
                                 THEN 'VFC'
                                 ELSE ''
                                 END) IS NOT NULL
