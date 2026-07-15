SELECT 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND SALDOs$mesTrabajo-1$s >=0 
            THEN SALDOs$mesTrabajo-1$s
            ELSE 0 
            END 
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo-1$s<0
            THEN -SALDOs$mesTrabajo-1$s
            ELSE 0 
            END SALDOANTDEBITO,
       PLAN_CONTABLE.DEBITOs$mesTrabajo$s DEBITO, 
       PLAN_CONTABLE.CREDITOs$mesTrabajo$s CREDITO,  
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo$s>=0
            THEN SALDOs$mesTrabajo$s
            ELSE 0 
            END
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND SALDOs$mesTrabajo$s<0
            THEN -SALDOs$mesTrabajo$s
            ELSE 0 
            END SALDONUECREDITO, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND SALDOs$mesTrabajo$s>=0
            THEN SALDOs$mesTrabajo$s 
            ELSE 0 
            END
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo$s<0
            THEN -SALDOs$mesTrabajo$s
            ELSE 0 
            END SALDONUEDEBITO,
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo-1$s>=0 
            THEN SALDOs$mesTrabajo-1$s
            ELSE 0 
            END
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='D'  AND SALDOs$mesTrabajo-1$s <0
            THEN -SALDOs$mesTrabajo-1$s
            ELSE 0 
            END SALDOANTCREDITO,
       PLAN_CONTABLE.CODIGO,      
       PLAN_CONTABLE.NOMBRE,
       COMPANIA.NOMBRE COMPANIANOMBRE, 
       PLAN_CONTABLE.COMPANIA, 
       CASE WHEN s$claseCero$s  NOT IN (0) AND SUBSTR(PLAN_CONTABLE.CODIGO,0,1)='0'
                       THEN '99999999'||PLAN_CONTABLE.CODIGO
                       ELSE PLAN_CONTABLE.CODIGO
                       END  ORDENCUENTAS 
FROM V_PLAN_CONTABLE PLAN_CONTABLE 
  INNER JOIN  COMPANIA 
    ON PLAN_CONTABLE.COMPANIA = COMPANIA.CODIGO    
WHERE PLAN_CONTABLE.COMPANIA              =   s$compania$s 
     AND PLAN_CONTABLE.ANO                         =   s$anoTrabajo$s  
     AND (LENGTH(PLAN_CONTABLE.CODIGO)  =  s$digitos$s
		  OR (LENGTH(PLAN_CONTABLE.CODIGO)  <=  s$digitos$s AND (PLAN_CONTABLE.MOVIMIENTO NOT IN (0) OR MAN_AUX_TER NOT IN (0) OR MAN_AUX_GEN NOT IN (0) OR MAN_CEN_CTO NOT IN (0)))
		 )
     AND (PLAN_CONTABLE.DEBITOs$mesTrabajo$s 
             + PLAN_CONTABLE.CREDITOs$mesTrabajo$s 
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s>=0
                          THEN PLAN_CONTABLE.SALDOs$mesTrabajo-1$s
                          ELSE 0
                END
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s <0
                         THEN  -PLAN_CONTABLE.SALDOs$mesTrabajo-1$s
                         ELSE 0
                END 
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s >=0
                         THEN PLAN_CONTABLE.SALDOs$mesTrabajo$s
                         ELSE 0
                END 
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s<0
                         THEN -PLAN_CONTABLE.SALDOs$mesTrabajo$s
                         ELSE 0
               END) NOT IN (0)    
     AND PLAN_CONTABLE.CODIGO BETWEEN 's$cuentaInicial$s' AND  's$cuentaFinal$s'
     s$centroCostoCond$s  
     AND PLAN_CONTABLE.AUXILIAR_INFORME IS NULL
     AND PLAN_CONTABLE.TERCERO_INFORME IS NULL
     AND s$mayoriza$s  IN (0)

UNION ALL 
SELECT 
       SUM(SALDOANTDEBITO) SALDOANTDEBITO,
       SUM(DEBITO) DEBITO, 
       SUM(CREDITO) CREDITO, 
       SUM(SALDONUECREDITO) SALDONUECREDITO, 
       SUM(SALDONUEDEBITO) SALDONUEDEBITO,
       SUM(SALDOANTCREDITO) SALDOANTCREDITO,
       PLAN.CODIGO,
       PLAN.NOMBRE,
       SALDO.COMPANIANOMBRE,
       PLAN.COMPANIA,
       CASE WHEN s$claseCero$s NOT IN (0) AND SUBSTR(PLAN.CODIGO,0,1)='0'
                       THEN '999999'||PLAN.CODIGO
                       ELSE PLAN.CODIGO
                       END  ORDENCUENTAS
FROM (
SELECT PLAN_CONTABLE.COMPANIA, 
       PLAN_CONTABLE.CODIGO, 
       COMPANIA.NOMBRE COMPANIANOMBRE, 
       PLAN_CONTABLE.NOMBRE,
       PLAN_CONTABLE.ANO,
       PLAN_CONTABLE.DEBITOs$mesTrabajo$s DEBITO, 
       PLAN_CONTABLE.CREDITOs$mesTrabajo$s CREDITO,  
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND SALDOs$mesTrabajo-1$s >=0 
            THEN SALDOs$mesTrabajo-1$s
            ELSE 0 
            END 
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo-1$s<0
            THEN -SALDOs$mesTrabajo-1$s
            ELSE 0 
            END SALDOANTDEBITO, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo-1$s>=0 
            THEN SALDOs$mesTrabajo-1$s
            ELSE 0 
            END
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='D'  AND SALDOs$mesTrabajo-1$s <0
            THEN -SALDOs$mesTrabajo-1$s
            ELSE 0 
            END SALDOAntCredito, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND SALDOs$mesTrabajo$s>=0
            THEN SALDOs$mesTrabajo$s
            ELSE 0 
            END
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo$s<0
            THEN -SALDOs$mesTrabajo$s
            ELSE 0 
            END SALDONUEDEBITO, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND SALDOs$mesTrabajo-1$s>=0
            THEN SALDOs$mesTrabajo$s
            ELSE 0 
            END
       +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND SALDOs$mesTrabajo-1$s<0
            THEN -SALDOs$mesTrabajo$s
            ELSE 0 
            END SALDONUECREDITO  
FROM V_PLAN_CONTABLE PLAN_CONTABLE 
  INNER JOIN  COMPANIA 
    ON PLAN_CONTABLE.COMPANIA = COMPANIA.CODIGO    
WHERE PLAN_CONTABLE.COMPANIA           =   s$compania$s 
     AND PLAN_CONTABLE.ANO             =   s$anoTrabajo$s  
     AND LENGTH(PLAN_CONTABLE.CODIGO)  <=  s$digitos$s 
     AND (PLAN_CONTABLE.MOVIMIENTO NOT IN (0) OR MAN_AUX_TER NOT IN (0) OR MAN_AUX_GEN NOT IN (0) OR MAN_CEN_CTO NOT IN (0))  
     AND (PLAN_CONTABLE.DEBITOs$mesTrabajo$s 
             + PLAN_CONTABLE.CREDITOs$mesTrabajo$s 
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s>=0
                          THEN PLAN_CONTABLE.SALDOs$mesTrabajo-1$s
                          ELSE 0
                END
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s <0
                         THEN  -PLAN_CONTABLE.SALDOs$mesTrabajo-1$s
                         ELSE 0
                END 
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s >=0
                         THEN PLAN_CONTABLE.SALDOs$mesTrabajo$s
                         ELSE 0
                END 
             +CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s<0
                         THEN -PLAN_CONTABLE.SALDOs$mesTrabajo$s
                         ELSE 0
               END) NOT IN (0)    
     AND PLAN_CONTABLE.CODIGO BETWEEN 's$cuentaInicial$s' AND  's$cuentaFinal$s'
     s$centroCostoCond$s 
     AND PLAN_CONTABLE.AUXILIAR_INFORME     IS NULL
     AND PLAN_CONTABLE.TERCERO_INFORME      IS NULL
)SALDO INNER JOIN PLAN_CONTABLE PLAN
   ON SALDO.COMPANIA                                = PLAN.COMPANIA
   AND SALDO.ANO                                    = PLAN.ANO
   AND SUBSTR(SALDO.CODIGO, 1, LENGTH(PLAN.CODIGO)) = PLAN.CODIGO 
  WHERE s$mayoriza$s NOT IN (0)
GROUP BY PLAN.CODIGO, 
         PLAN.NOMBRE, 
         SALDO.COMPANIANOMBRE, 
         PLAN.COMPANIA
