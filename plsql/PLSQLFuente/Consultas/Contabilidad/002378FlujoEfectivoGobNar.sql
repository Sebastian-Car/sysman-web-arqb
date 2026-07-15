MERGE INTO CONSULTAS FIN USING (SELECT '002378FlujoEfectivoGobNar' INFORME ,TO_CLOB(q'[SELECT COMPANIA,
  ANO,
  CODIGO,
  CENTRO_COSTO,
  NOMBRE,
  'A' NATURALEZA,
  s$mesInicial$s MESINICIAL,
  s$mesFinal$s MESFINAL,
  SALDO0 ,
  SALDO1 ,
  SALDO2 ,
  SALDO3 ,
  SALDO4 ,
  SALDO5 ,
  SALDO6 ,
  SALDO7 ,
  SALDO8 ,
  SALDO9 ,
  SALDO10 ,
  SALDO11 ,
  SALDO12 
FROM V_PLAN_CONTABLE 
WHERE V_PLAN_CONTABLE.COMPANIA            = s$compania$s
and V_PLAN_CONTABLE.ANO = s$anio$s
AND V_PLAN_CONTABLE.CLASECUENTA          IN ('B','J')
AND V_PLAN_CONTABLE.MOSTRAR_EN_FLUJO NOT IN (0)
AND V_PLAN_CONTABLE.MOVIMIENTO NOT IN(0)
AND (SALDO0>0 OR SALDO1>0 OR SALDO2>0 OR SALDO3>0 
OR SALDO4>0 OR SALDO5>0 OR SALDO6>0 OR SALDO7>0 
OR SALDO8>0 OR SALDO9>0 OR SALDO10>0 OR SALDO11>0
OR SALDO12>0 OR SALDO13>0)
UNION ALL
SELECT PLAN_FLUJO_EFECTIVO.COMPANIA,
  PLAN_FLUJO_EFECTIVO.ANO,
  PLAN_FLUJO_EFECTIVO.CODIGO,
  DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
  PLAN_FLUJO_EFECTIVO.NOMBRE,
  PLAN_FLUJO_EFECTIVO.NATURALEZA,
  s$mesInicial$s MESINICIAL,
  s$mesFinal$s MESFINAL,
  0 SALDO0,
  SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 1 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO1,
    SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 2 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO2,
    SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 3 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
              ]') || TO_CLOB(q'[                                                  ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO3, 
    SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 4 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO4,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 5 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO5,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 6 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO6,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 7 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEB]') || TO_CLOB(q'[ITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO7,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 8 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO8,
    SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 9 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO9,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 10 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO10,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 11 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPR]') || TO_CLOB(q'[OBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO11,
   SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.MES = 12 THEN CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'D' 
                                                      THEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - DETALLE_COMPROBANTE_CNT.VALOR_CREDITO 
                                                      ELSE CASE WHEN PLAN_FLUJO_EFECTIVO.NATURALEZA = 'C' 
                                                                THEN DETALLE_COMPROBANTE_CNT.VALOR_CREDITO - DETALLE_COMPROBANTE_CNT.VALOR_DEBITO
                                                                ELSE 0
                                                           END
                                                      END 
                                                 ELSE 0 END) SALDO12
FROM DETALLE_COMPROBANTE_CNT
INNER JOIN PLAN_FLUJO_EFECTIVO
ON DETALLE_COMPROBANTE_CNT.COMPANIA               = PLAN_FLUJO_EFECTIVO.COMPANIA
AND DETALLE_COMPROBANTE_CNT.CODIGO_FLUJO_EFECTIVO = PLAN_FLUJO_EFECTIVO.CODIGO
AND DETALLE_COMPROBANTE_CNT.ANO                   = PLAN_FLUJO_EFECTIVO.ANO
WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = s$compania$s
AND DETALLE_COMPROBANTE_CNT.ANO  = s$anio$s
GROUP BY PLAN_FLUJO_EFECTIVO.COMPANIA,
  PLAN_FLUJO_EFECTIVO.ANO,
  PLAN_FLUJO_EFECTIVO.CODIGO,
  DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
  PLAN_FLUJO_EFECTIVO.NOMBRE,
  PLAN_FLUJO_EFECTIVO.NATURALEZA
ORDER BY NATURALEZA,
  CODIGO]') CONSULTA, 1 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);