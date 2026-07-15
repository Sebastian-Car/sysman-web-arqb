SELECT DETALLE_COMPROBANTE_CNT.CODIGO_CUENTA                                             "Número De La Caja Menor",
       SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO - 
             CASE WHEN DETALLE_COMPROBANTE_CNT.CLASE_CONTABLE IN('J')
                 THEN VALOR_CREDITO
                 ELSE 0 END)                                                            "Total Ingresos",
        SUM(CASE WHEN DETALLE_COMPROBANTE_CNT.CLASE_CONTABLE NOT IN('J')
                 THEN VALOR_CREDITO
                 ELSE 0 END)                                                            "Total Gastos",
       SUM(VALOR_DEBITO-VALOR_CREDITO)                                                  "Saldo Efectivo Caja",
         SUM((CASE WHEN 0  = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
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
                   + CASE WHEN 13 = s$mesInicial-1$s  THEN PLAN_CONTABLE.SALDO13 ELSE 0 END)
                   -(VALOR_DEBITO-VALOR_CREDITO))                                           "Saldo Libro Bancos"    
FROM V_DETALLE_AUXILIAR_CNT DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
      ON   DETALLE_COMPROBANTE_CNT.COMPANIA=PLAN_CONTABLE.COMPANIA
      AND  DETALLE_COMPROBANTE_CNT.ANO=PLAN_CONTABLE.ANO
      AND  DETALLE_COMPROBANTE_CNT.CODIGO_CUENTA=PLAN_CONTABLE.CODIGO
WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=s$compania$s
      AND DETALLE_COMPROBANTE_CNT.ANO=s$ano$s
      AND DETALLE_COMPROBANTE_CNT.MES BETWEEN s$mesInicial$s AND s$mesFinal$s
      AND DETALLE_COMPROBANTE_CNT.CLASE_CONTABLE In ('J')
      AND DETALLE_COMPROBANTE_CNT.CLASECUENTA In ('J')
      AND INSTR(PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(PCK_SYSMAN_UTL.FC_PAR(DETALLE_COMPROBANTE_CNT.COMPANIA,'CUENTA(S) CAJA MENOR - SIA ',1,DETALLE_COMPROBANTE_CNT.FECHA)),
          '' || DETALLE_COMPROBANTE_CNT.TIPO_CPTE || '',1)=0
GROUP BY DETALLE_COMPROBANTE_CNT.CODIGO_CUENTA          
          
