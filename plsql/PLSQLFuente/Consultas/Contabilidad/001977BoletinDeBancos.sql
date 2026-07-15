SELECT     NOMBREBANCO                     BANCO,
                 CUENTANUMERO                   CUENTA,
                 NOMBRE                                 NOMBRE_CUENTA,
                 SALDOANT                             SALDO_ANTERIOR,   
                 DEBITO                                  INGRESOS, 
           CREDITO                                      EGRESOS,
           SALDOFINAL                                SALDO_SIGUIENTE,
          CLASECUENTA                              TIPODECUENTA,
           CASE WHEN UPPER(CLASECUENTA) = 'A' THEN 'CUENTAS AHORROS'
                WHEN UPPER(CLASECUENTA) = 'C' THEN 'CUENTAS CORRIENTES'
                WHEN UPPER(CLASECUENTA) = 'F' THEN 'CUENTAS FONDOS ESPECIALES'
                ELSE ' ' 
                END CLASE_CUENTA,          
          CASE WHEN DESTINO_RECURSOS = 1 THEN 'FUN LD'
               WHEN DESTINO_RECURSOS = 2 THEN 'FUN D.E.'
               WHEN DESTINO_RECURSOS = 3 THEN 'S.G.P EDUC'
               WHEN DESTINO_RECURSOS = 4 THEN 'SGP SALUD'
               WHEN DESTINO_RECURSOS = 5 THEN 'S.G.P AGUA POT'
               WHEN DESTINO_RECURSOS = 6 THEN 'S.G.R.'
               ELSE ' '
               END DESTINO_RECURSOS          
    FROM (   
        SELECT     CODIGO,                                                                                            
                   NOMBRE,
                   BANCO.NOMBREBANCO,
                   CUENTABANCOS.CUENTANUMERO,
                   CUENTABANCOS.CLASECUENTA,
                   CUENTABANCOS.DESTINO_RECURSOS,
                  (CASE WHEN 0  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                   + CASE WHEN 1  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                   + CASE WHEN 2  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                   + CASE WHEN 3  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                   + CASE WHEN 4  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                   + CASE WHEN 5  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                   + CASE WHEN 6  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                   + CASE WHEN 7  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                   + CASE WHEN 8  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                   + CASE WHEN 9  = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                   + CASE WHEN 10 = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                   + CASE WHEN 11 = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                   + CASE WHEN 12 = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                   + CASE WHEN 13 = s$mesInicial$s-1  THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
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
                   + CASE WHEN 8  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO8  ELSE 0 END
                   + CASE WHEN 9  BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO9  ELSE 0 END
                   + CASE WHEN 10 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO10 ELSE 0 END
                   + CASE WHEN 11 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO11 ELSE 0 END
                   + CASE WHEN 12 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO12 ELSE 0 END
                   + CASE WHEN 13 BETWEEN s$mesInicial$s AND s$mesFinal$s  THEN PLAN_CONTABLE.CREDITO13 ELSE 0 END
                   )  CREDITO,                                                                                         
                    (CASE WHEN 0  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 = s$mesFinal$s  THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )SALDOFINAL                                                                                                                                                                  
           FROM  PLAN_CONTABLE LEFT JOIN CUENTABANCOS
                  ON  PLAN_CONTABLE.COMPANIA = CUENTABANCOS.COMPANIA
                  AND PLAN_CONTABLE.ANO      = CUENTABANCOS.ANO
                  AND PLAN_CONTABLE.CODIGO   = CUENTABANCOS.IDCONTABLE
              LEFT JOIN BANCO
                   ON  CUENTABANCOS.COMPANIA = BANCO.COMPANIA
                   AND CUENTABANCOS.BANCO    = BANCO.BANCO
           WHERE PLAN_CONTABLE.COMPANIA = s$compania$s
           AND PLAN_CONTABLE.ANO        = s$ano$s
            AND PLAN_CONTABLE.CODIGO BETWEEN 's$cuentaInicial$s' AND 's$cuentaFinal$s'
           AND (PLAN_CONTABLE.MOVIMIENTO <>0 OR PLAN_CONTABLE.MAN_CEN_CTO<>0 
                   OR PLAN_CONTABLE.MAN_AUX_TER<>0 OR PLAN_CONTABLE.MAN_AUX_GEN<>0
                   OR PLAN_CONTABLE.MAN_AUX_REF<>0 OR PLAN_CONTABLE.MAN_AUX_FUE<>0)
                      )
WHERE     SALDOANT +  DEBITO  +  CREDITO + SALDOFINAL  <>0
ORDER BY CLASECUENTA,CODIGO
