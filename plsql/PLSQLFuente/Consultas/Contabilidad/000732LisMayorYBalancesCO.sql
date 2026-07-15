SELECT SUBSTR(PLAN_CONTABLE.CODIGO,1,1) AS CLASE, 
       PLAN_CONTABLE.COMPANIA, 
       PLAN_CONTABLE.ANO, 
       PLAN_CONTABLE.CODIGO, 
       PLAN_CONTABLE.NOMBRE, 
       PLAN_CONTABLE.DEBITOs$mesTrabajo$s DEBITO , 
       PLAN_CONTABLE.CREDITOs$mesTrabajo$s CREDITO,  
       PLAN_CONTABLE.NETO2, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s  >=0
            THEN PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
            ELSE 0
            END + 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s  < 0
            THEN -PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
            ELSE 0
            END SALDOANTDEBITO, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s  >= 0
            THEN PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
            ELSE 0
            END+
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D'  AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s  <0
            THEN -PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
            ELSE 0
            END SALDOANTCREDITO, 
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s>=0
            THEN PLAN_CONTABLE.SALDOs$mesTrabajo$s
            ELSE 0
            END+
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s < 0
            THEN -PLAN_CONTABLE.SALDOs$mesTrabajo$s
            ELSE 0
            END SALDONUEDEBITO,
       CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s >= 0
            THEN PLAN_CONTABLE.SALDOs$mesTrabajo$s
            ELSE 0
            END+
       CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo$s<0
            THEN -PLAN_CONTABLE.SALDOs$mesTrabajo$s
            ELSE 0
            END SALDONUECREDITO,
       CASE WHEN 0 NOT IN(0)
            THEN (CASE WHEN SUBSTR(PLAN_CONTABLE.CODIGO,1,1)='0'
                       THEN 'z'||PLAN_CONTABLE.CODIGO
                       ELSE PLAN_CONTABLE.CODIGO
                       END)
            ELSE PLAN_CONTABLE.CODIGO
            END ORDEN, 
       SUBSTR(CASE WHEN 0 NOT IN(0)
                   THEN (CASE WHEN SUBSTR(PLAN_CONTABLE.CODIGO,0,1)='0'
                              THEN 'z'||PLAN_CONTABLE.CODIGO
                              ELSE PLAN_CONTABLE.CODIGO
                              END)
                   ELSE PLAN_CONTABLE.CODIGO
                   END,0,1) CLASEORDEN,
                   PLAN_CONTABLE.CODIGO ORDENCUENTAS
FROM V_PLAN_CONTABLE PLAN_CONTABLE 
WHERE PLAN_CONTABLE.COMPANIA       =  s$compania$s 
  AND PLAN_CONTABLE.ANO            =  s$anoTrabajo$s
  AND (LENGTH(PLAN_CONTABLE.CODIGO) =  s$digitos$s
           OR LENGTH(PLAN_CONTABLE.CODIGO) <  s$digitos$s 
  AND (PLAN_CONTABLE.MOVIMIENTO NOT IN (0) OR PLAN_CONTABLE.MAN_AUX_TER NOT IN (0) OR PLAN_CONTABLE.MAN_AUX_GEN NOT IN (0) OR PLAN_CONTABLE.MAN_CEN_CTO NOT IN (0)))
  AND (PLAN_CONTABLE.DEBITOs$mesTrabajo$s + PLAN_CONTABLE.CREDITOs$mesTrabajo$s + CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s >=0
                                                             THEN PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
                                                             ELSE 0
                                                             END+
                                                        CASE WHEN PLAN_CONTABLE.NATURALEZA='D'  AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s  <0
                                                             THEN -PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
                                                             ELSE 0
                                                             END) +  
                                                        CASE WHEN PLAN_CONTABLE.NATURALEZA='D' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s  >=0
                                                             THEN PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
                                                             ELSE 0
                                                             END + 
                                                        CASE WHEN PLAN_CONTABLE.NATURALEZA='C' AND PLAN_CONTABLE.SALDOs$mesTrabajo-1$s <0
                                                             THEN -PLAN_CONTABLE.SALDOs$mesTrabajo-1$s 
                                                             ELSE 0
                                                             END NOT IN (0)                                                           
  AND PLAN_CONTABLE.CODIGO BETWEEN 's$cuentaInicial$s'
                               AND 's$cuentaFinal$s'
  ORDER BY PLAN_CONTABLE.COMPANIA, PLAN_CONTABLE.ANO, PLAN_CONTABLE.CODIGO
