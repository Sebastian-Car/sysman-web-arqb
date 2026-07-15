SELECT  CUENTABANCOS.CUENTANUMERO
       ,CUENTABANCOS.CLASECUENTA
       ,BANCO.NOMBREBANCO
       ,CUENTABANCOS.BANCO CODIGO_BANCO
       ,CUENTABANCOS.SUCURSALBANCO SUCURSAL_BANCO
       ,FUENTE_RECURSOS.NOMBRE DESTINACION_RECURSO
       ,CASE WHEN PLAN_CONTABLE.NATURALEZA='D' 
                  AND SALDOs$mesanterior$s>=0 
             THEN SALDOs$mesanterior$s 
             ELSE 0 
        END 
        +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' 
                   AND SALDOs$mesanterior$s<0 
              THEN -SALDOs$mesanterior$s 
              ELSE 0 
         END
        -CASE WHEN PLAN_CONTABLE.NATURALEZA='C' 
                   AND SALDOs$mesanterior$s>=0 
              THEN SALDOs$mesanterior$s 
              ELSE 0 
         END 
        +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' 
                   AND SALDOs$mesanterior$s<0 
              THEN -SALDOs$mesanterior$s 
              ELSE 0 
         END SALDO_INICIAL, 
        s$strdebito$s DEBITO, 
        s$strcredito$s CREDITO, 
        CASE WHEN PLAN_CONTABLE.NATURALEZA='D' 
                  AND SALDOs$mesfinal$s>=0 
             THEN SALDOs$mesfinal$s 
             ELSE 0 
        END
        +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' 
                   AND SALDOs$mesfinal$s<0 
              THEN -SALDOs$mesfinal$s 
              ELSE 0 
         END
        -CASE WHEN PLAN_CONTABLE.NATURALEZA='C' 
                   AND SALDOs$mesfinal$s>=0 
              THEN SALDOs$mesfinal$s 
              ELSE 0 
         END
        +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' 
                   AND SALDOs$mesfinal$s<0 
              THEN -SALDOs$mesfinal$s 
              ELSE 0 
        END SALDO_FINAL
   FROM CUENTABANCOS 
       LEFT JOIN PLAN_CONTABLE 
           ON  CUENTABANCOS.COMPANIA   = PLAN_CONTABLE.COMPANIA 
           AND CUENTABANCOS.ANO        = PLAN_CONTABLE.ANO 
           AND CUENTABANCOS.IDCONTABLE = PLAN_CONTABLE.CODIGO 
       LEFT JOIN BANCO 
           ON  CUENTABANCOS.COMPANIA = BANCO.COMPANIA 
           AND CUENTABANCOS.BANCO    = BANCO.BANCO 
       LEFT JOIN FUENTE_RECURSOS 
           ON  CUENTABANCOS.COMPANIA = FUENTE_RECURSOS.COMPANIA
           AND CUENTABANCOS.ANO      = FUENTE_RECURSOS.ANO 
           AND CUENTABANCOS.RECURSOS = FUENTE_RECURSOS.CODIGO 
  WHERE PLAN_CONTABLE.COMPANIA = s$compania$s
    AND PLAN_CONTABLE.ANO      = s$ano$s
    AND CUENTABANCOS.SGP       NOT IN (0) 
    AND (ROUND(PLAN_CONTABLE.SALDOs$mesinicial$s,2)    NOT IN (0)
    	 OR ROUND(s$strdebito$s,2)                     NOT IN (0) 
    	 OR ROUND(s$strcredito$s,2)                    NOT IN (0) 
    	 OR ROUND(PLAN_CONTABLE.SALDOs$mesanterior$s,2)NOT IN (0))
  ORDER BY   PLAN_CONTABLE.COMPANIA
           , PLAN_CONTABLE.ANO
           , PLAN_CONTABLE.CODIGO
