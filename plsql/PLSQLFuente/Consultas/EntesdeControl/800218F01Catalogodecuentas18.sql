SELECT     CODIGO                   "Código Contable",
           NOMBRE                   "Nombre De La Cuenta",
           SALDOANT                 "Saldo Anterior",   
           DEBITO                   "Débito", 
           CREDITO                  "Crédito",
           (CASE WHEN CORRIENTE=-1
             THEN SALDOFINAL
             ELSE  0
             END)                  "Saldo Corriente", 
        (CASE WHEN CORRIENTE=0
             THEN SALDOFINAL
             ELSE  0
             END)                  "Saldo NoCorriente"       
    FROM (   
        SELECT     CODIGO,                                                                                            
                   NOMBRE,
                   CORRIENTE,
                   (CASE WHEN 0  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                   + CASE WHEN 1  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                   + CASE WHEN 2  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                   + CASE WHEN 3  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                   + CASE WHEN 4  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                   + CASE WHEN 5  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                   + CASE WHEN 6  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                   + CASE WHEN 7  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                   + CASE WHEN 8  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                   + CASE WHEN 9  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                   + CASE WHEN 10 = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                   + CASE WHEN 11 = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                   + CASE WHEN 12 = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                   + CASE WHEN 13 = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
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
                                                                                     
           FROM  PLAN_CONTABLE
           WHERE PLAN_CONTABLE.COMPANIA =s$compania$s
             AND PLAN_CONTABLE.ANO      =s$ano$s
             AND  LENGTH (PLAN_CONTABLE.CODIGO)=s$digitos$s
           )
WHERE     ABS(SALDOANT) +  DEBITO  +  CREDITO +ABS(SALDOFINAL) NOT IN (0)
ORDER BY  CODIGO    
                            
