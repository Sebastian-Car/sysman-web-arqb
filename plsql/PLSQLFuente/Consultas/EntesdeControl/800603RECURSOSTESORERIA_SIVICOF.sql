MERGE INTO CONSULTAS FIN USING (SELECT '800603RECURSOSTESORERIA_SIVICOF' INFORME ,TO_CLOB(q'[SELECT     '3 Cuenta Bancaria' AS "SUBCUENTA EFECTIVO", 
            TIPO_CUENTA_BANCARIA AS "TIPO DE CUENTA BANCARIA",
            NOMBREBANCO AS "ENTIDAD FINANCIERA",
            CASE WHEN CUENTANUMERO IS NOT NULL
            THEN  CUENTANUMERO
            ELSE CODIGO
            END  AS "No DE CUENTA O REFERENCIA",
            '1 PESOS' MONEDA,
            NOMBRE  UTILIZACION,                     
           SALDOANT                 AS "SALDO INICIAL",   
           DEBITO                   AS "MOVIMIENTO DE INGRESOS EN PESOS", 
           CREDITO                  AS "MOVIMIENTO DE EGRESOS EN PESOS",
           SALDOFINAL               AS "SALDO EN PESOS AL FINAL DE MES SEGUN TESORERIA",
           MOVIMIENTOMAX            AS "VALOR DE MOVIMIENTO MAXIMO EN EL MES EN PESOS",
           '' AS "TASA DE INTERES BANCARIO",
           '' AS "RESPONSABLE/CARGO",
           '' AS "POLIZA DE MANEJO",
           FECHA_CONSTITUCION AS "FECHA DE CONSTITUCION",
           '' AS "FECHA DE CIERRE",
           LAST_DAY(TO_DATE('01/'||s$mesFinal$s||'/'||s$ano$s,'DD/MM/YYYY'))  AS "FECHA DE CONCILIACION",
           '' OBSERVACIONES
    
    FROM (   
        SELECT     CODIGO,                                                                                            
                   NOMBRE,
                    CASE WHEN CUENTABANCOS.CLASECUENTA='A'
                      THEN '1 Ahorros'
                      ELSE CASE WHEN CUENTABANCOS.CLASECUENTA='C'
                      THEN '2 Corriente'
                      ELSE ''
                      END END TIPO_CUENTA_BANCARIA,
                      CUENTABANCOS.CUENTANUMERO ,
                      BANCO.NOMBREBANCO,
                      TO_CHAR(CUENTABANCOS.FECHAAPERTURA,'DD/MM/YYYY') FECHA_CONSTITUCION,
                      ULTIMOMOV.MOVIMIENTO MOVIMIENTOMAX,
                    (CASE WHEN 0  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                   + CASE WHEN 1  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                   + CASE WHEN 2  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                   + CASE WHEN 3  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                   + CASE WHEN 4  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                   + CASE WHEN 5  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                   + CASE WHEN 6  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                   + CASE WHEN 7  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                   + CASE WHEN 8  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                   + CASE WHEN 9  = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                   + CASE WHEN 10 = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                   + CASE WHEN 11 = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                   + ]') || TO_CLOB(q'[CASE WHEN 12 = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                   + CASE WHEN 13 = s$mesInicial$s-1   THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                   ) SALDOANT,                                                                                       
                 (CASE WHEN 0  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO0  ELSE 0 END
                   + CASE WHEN 1  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO1  ELSE 0 END
                   + CASE WHEN 2  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO2  ELSE 0 END
                   + CASE WHEN 3  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO3  ELSE 0 END
                   + CASE WHEN 4  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO4  ELSE 0 END
                   + CASE WHEN 5  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO5  ELSE 0 END
                   + CASE WHEN 6  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO6  ELSE 0 END
                   + CASE WHEN 7  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO7  ELSE 0 END
                   + CASE WHEN 8  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO8  ELSE 0 END
                   + CASE WHEN 9  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO9  ELSE 0 END
                   + CASE WHEN 10 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO10 ELSE 0 END
                   + CASE WHEN 11 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO11 ELSE 0 END
                   + CASE WHEN 12 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO12 ELSE 0 END
                   + CASE WHEN 13 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.DEBITO13 ELSE 0 END
                   )      DEBITO,                                                                                            
                 (CASE WHEN 0  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO0  ELSE 0 END
                   + CASE WHEN 1  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO1  ELSE 0 END
                   + CASE WHEN 2  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO2  ELSE 0 END
                   + CASE WHEN 3  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO3  ELSE 0 END
                   + CASE WHEN 4  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO4  ELSE 0 END
                   + CASE WHEN 5  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO5  ELSE 0 END
                   + CASE WHEN 6  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO6  ELSE 0 END
                   + CASE WHEN 7  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO7  ELSE 0 END
                   + CASE WHEN 8  BETWEEN s$mesInicial$s ]') || TO_CLOB(q'[AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO8  ELSE 0 END
                   + CASE WHEN 9  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO9  ELSE 0 END
                   + CASE WHEN 10 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO10 ELSE 0 END
                   + CASE WHEN 11 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO11 ELSE 0 END
                   + CASE WHEN 12 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO12 ELSE 0 END
                   + CASE WHEN 13 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO13 ELSE 0 END
                   )  CREDITO,                                                                                             
                 
                    (CASE WHEN 0  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  = s$mesFinal$s THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 = s$mesFinal$s THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )SALDOFINAL                                                                                
                                                                                     
           FROM  PLAN_CONTABLE LEFT JOIN CUENTABANCOS
                ON PLAN_CONTABLE.COMPANIA=CUENTABANCOS.COMPANIA
                AND PLAN_CONTABLE.ANO=CUENTABANCOS.ANO
                AND PLAN_CONTABLE.CODIGO=CUENTABANCOS.IDCONTABLE
                LEFT JOIN BANCO
                ON CUENTABANCOS.COMPANIA=BANCO.COMPANIA
                AND CUENTABANCOS.BANCO=BANCO.BANCO
                
                INNER JOIN (        
                 SELECT COMPANIA,
                 ANO,
                 CUENTA, 
                 CASE WHEN  (INGRESO -EGRESO) >0
                 THEN INGRESO
                 ELSE -EGRESO
                 END MOVIMIENTO
    ]') || TO_CLOB(q'[             
                 FROM 
                 (
                         SELECT COMPANIA,
                                ANO,
                                CUENTA,
                                SUM( CASE WHEN TIPO='INGRESO'
                                   THEN VALOR 
                                   ELSE 0 END)
                                    INGRESO,
                               SUM(CASE WHEN TIPO='EGRESO'
                                   THEN VALOR 
                                   ELSE 0 END)
                                    EGRESO
                                
                                
                         FROM  (
                            SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                   DETALLE_COMPROBANTE_CNT.ANO, 
                                   DETALLE_COMPROBANTE_CNT.CUENTA,
                                   CASE WHEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO >0
                                   THEN 'INGRESO'
                                   ELSE 'EGRESO'
                                   END TIPO,
                                   MAX(ABS((DETALLE_COMPROBANTE_CNT.VALOR_DEBITO -  DETALLE_COMPROBANTE_CNT.VALOR_CREDITO))) VALOR
                            FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
                              ON DETALLE_COMPROBANTE_CNT.COMPANIA=PLAN_CONTABLE.COMPANIA
                                AND DETALLE_COMPROBANTE_CNT.ANO=PLAN_CONTABLE.ANO
                                AND DETALLE_COMPROBANTE_CNT.CUENTA=PLAN_CONTABLE.CODIGO
                            WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=s$compania$s
                            AND DETALLE_COMPROBANTE_CNT.ANO=s$ano$s
                            AND DETALLE_COMPROBANTE_CNT.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
                            AND PLAN_CONTABLE.CLASECUENTA='B'
                            
                            GROUP BY  DETALLE_COMPROBANTE_CNT.COMPANIA,
                                   DETALLE_COMPROBANTE_CNT.ANO, 
                                   DETALLE_COMPROBANTE_CNT.MES,
                                   CASE WHEN DETALLE_COMPROBANTE_CNT.VALOR_DEBITO >0
                                   THEN 'INGRESO'
                                   ELSE 'EGRESO'
                                   END,
                                           
                                   DETALLE_COMPROBANTE_CNT.CUENTA
                                   ORDER BY MAX(ABS((DETALLE_COMPROBANTE_CNT.VALOR_DEBITO -  DETALLE_COMPROBANTE_CNT.VALOR_CREDITO))) DESC
                                   
                                 
                          )DATOS
                                                    
                           GROUP BY  
                              COMPANIA,
                                ANO,
                                CUENTA)) ULTIMOMOV
     ON   PLAN_CONTABLE.COMPANIA=  ULTIM]') || TO_CLOB(q'[OMOV.COMPANIA
     AND  PLAN_CONTABLE.ANO=  ULTIMOMOV.ANO
     AND  PLAN_CONTABLE.CODIGO=  ULTIMOMOV.CUENTA 
       
           WHERE PLAN_CONTABLE.COMPANIA =s$compania$s
             AND PLAN_CONTABLE.ANO      =s$ano$s
             AND PLAN_CONTABLE.CLASECUENTA='B'
            
           )
           
     WHERE     ABS(SALDOANT) +  DEBITO  +  CREDITO +ABS(SALDOFINAL) NOT IN (0)
ORDER BY  CODIGO]') CONSULTA, 99 APLICACION ,TO_CLOB(q'[]') CONSULTA_OPCIONAL, NULL CREATED_BY, NULL MODIFIED_BY  FROM DUAL ) INI ON (INI.INFORME = FIN.INFORME )  WHEN MATCHED THEN  UPDATE SET FIN.CONSULTA =  INI.CONSULTA, FIN.APLICACION =  INI.APLICACION, FIN.CONSULTA_OPCIONAL =  INI.CONSULTA_OPCIONAL, FIN.MODIFIED_BY = INI.MODIFIED_BY, FIN.DATE_MODIFIED = SYSDATE  WHEN NOT MATCHED THEN  INSERT (INFORME,CONSULTA, APLICACION,CONSULTA_OPCIONAL,CREATED_BY,DATE_CREATED)  VALUES (INI.INFORME,INI.CONSULTA, INI.APLICACION,INI.CONSULTA_OPCIONAL,INI.CREATED_BY,SYSDATE);