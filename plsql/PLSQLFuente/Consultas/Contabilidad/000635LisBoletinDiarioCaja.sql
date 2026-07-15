SELECT COM.COMPANIA,
       COM.ANO,  
       COM.TIPO_CPTE,
       COM.TIPO_NOMBRE UNOMBRE,
       CASE WHEN -1=s$portercero$s THEN PNOM.NOMBRE ELSE PNOM.NOMBREUNICO END NOMBRE,
       CASE WHEN -1=s$portercero$s THEN DD.TERCERO  ELSE '' END TERCERO,
       DD.CUENTA,
       NVL(SALDOINIFINAL.SALDOINI,0) SALDOINI,
       SUM(DD.VALOR_DEBITO) VALOR_DEBITO,
       SUM(DD.VALOR_CREDITO) VALOR_CREDITO,
       SUM(CASE WHEN DD.ID  LIKE s$cuenta$s || '%' 
            THEN
              CASE WHEN DD.NATURALEZA='D' 
                   THEN DD.VALOR_DEBITO  - DD.VALOR_CREDITO  
                   ELSE DD.VALOR_CREDITO - DD.VALOR_DEBITO  
              END
            ELSE
            0 
       END) SALDOMES 
FROM (
    SELECT D.COMPANIA,
           D.ANO,
           D.TIPO_CPTE,
           D.COMPROBANTE,
           T.NOMBRE TIPO_NOMBRE
    FROM V_PLAN_CONTABLE P
    INNER JOIN DETALLE_COMPROBANTE_CNT D
       ON P.COMPANIA = D.COMPANIA
      AND P.ANO      = D.ANO
      AND D.ID       LIKE P.ID || '%'
    INNER JOIN TIPO_COMPROBANTE T
      ON T.COMPANIA  = D.COMPANIA
     AND T.CODIGO    = D.TIPO_CPTE
    WHERE P.COMPANIA = s$compania$s
      AND P.ANO      = EXTRACT (YEAR FROM TO_DATE(s$fecha$s,'DD/MM/YYYY')) 
      AND P.ID       = s$cuenta$s
      AND TO_CHAR(D.FECHA,'MM') = TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM') 
      AND TRUNC(D.FECHA)        = CASE WHEN -1=s$mensual$s THEN TRUNC(D.FECHA) ELSE TO_DATE(s$fecha$s,'DD/MM/YYYY') END         
    GROUP BY D.COMPANIA,
           D.ANO,
           D.TIPO_CPTE,
           D.COMPROBANTE,
           T.NOMBRE
) COM INNER JOIN DETALLE_COMPROBANTE_CNT DD
   ON COM.COMPANIA   = DD.COMPANIA
  AND COM.ANO        = DD.ANO
  AND COM.TIPO_CPTE  = DD.TIPO_CPTE
  AND COM.COMPROBANTE= DD.COMPROBANTE  
INNER JOIN V_PLAN_CONTABLE PNOM
    ON DD.COMPANIA = PNOM.COMPANIA
   AND DD.ANO      = PNOM.ANO
   AND DD.ID       = PNOM.ID
INNER JOIN   (SELECT PSALDO.COMPANIA,
                     PSALDO.ANO, 
                     CASE WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 1 THEN PSALDO.SALDO0 
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 2 THEN PSALDO.SALDO1
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 3 THEN PSALDO.SALDO2
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 4 THEN PSALDO.SALDO3
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 5 THEN PSALDO.SALDO4
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 6 THEN PSALDO.SALDO5
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 7 THEN PSALDO.SALDO6
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 8 THEN PSALDO.SALDO7
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 9 THEN PSALDO.SALDO8
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 10 THEN PSALDO.SALDO9
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 11 THEN PSALDO.SALDO10
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 12 THEN PSALDO.SALDO11
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 13 THEN PSALDO.SALDO12
                          ELSE 0 END +
                     SUM(NVL(D.SALDOINI,0)) SALDOINI
              FROM V_PLAN_CONTABLE PSALDO
              LEFT JOIN (SELECT DE.COMPANIA,
                                DE.ANO, 
                                DE.ID,
                                (CASE WHEN DE.FECHA < (CASE WHEN -1=s$mensual$s THEN 
                                    TO_DATE('01/' || TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM') || '/' || TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'YYYY'), 'DD/MM/YYYY')  
                                    ELSE TO_DATE(s$fecha$s,'DD/MM/YYYY') END
                                    )
                                   THEN  CASE WHEN DE.NATURALEZA='D' THEN DE.VALOR_DEBITO-DE.VALOR_CREDITO  
                                              ELSE DE.VALOR_CREDITO - DE.VALOR_DEBITO  END 
                                    ELSE 0 END) SALDOINI
                                    
                                FROM DETALLE_COMPROBANTE_CNT DE
                                WHERE DE.COMPANIA = s$compania$s
                                  AND DE.ANO      = EXTRACT (YEAR FROM TO_DATE(s$fecha$s,'DD/MM/YYYY')) 
                                  AND TO_CHAR(DE.FECHA,'MM') = TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM') 
                                  AND TRUNC(DE.FECHA)        < CASE WHEN -1=s$mensual$s THEN TRUNC(DE.FECHA) ELSE TO_DATE(s$fecha$s,'DD/MM/YYYY') END  
                                
                                ) D
                 ON PSALDO.COMPANIA = D.COMPANIA
                AND PSALDO.ANO      = D.ANO
                AND D.ID       LIKE PSALDO.ID || '%'
              WHERE PSALDO.COMPANIA = s$compania$s
                AND PSALDO.ANO      = EXTRACT (YEAR FROM TO_DATE(s$fecha$s,'DD/MM/YYYY')) 
                AND PSALDO.ID       = s$cuenta$s
              GROUP BY PSALDO.COMPANIA,
                     PSALDO.ANO,
                     CASE WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 1 THEN PSALDO.SALDO0 
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 2 THEN PSALDO.SALDO1
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 3 THEN PSALDO.SALDO2
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 4 THEN PSALDO.SALDO3
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 5 THEN PSALDO.SALDO4
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 6 THEN PSALDO.SALDO5
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 7 THEN PSALDO.SALDO6
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 8 THEN PSALDO.SALDO7
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 9 THEN PSALDO.SALDO8
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 10 THEN PSALDO.SALDO9
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 11 THEN PSALDO.SALDO10
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 12 THEN PSALDO.SALDO11
                          WHEN TO_NUMBER(TO_CHAR(TO_DATE(s$fecha$s,'DD/MM/YYYY'),'MM')) = 13 THEN PSALDO.SALDO12
                          ELSE 0 END
              ) SALDOINIFINAL
   ON COM.COMPANIA   = SALDOINIFINAL.COMPANIA
  AND COM.ANO        = SALDOINIFINAL.ANO
GROUP BY COM.COMPANIA,
       COM.ANO,  
       COM.TIPO_CPTE,
       COM.TIPO_NOMBRE,
       CASE WHEN -1=s$portercero$s THEN PNOM.NOMBRE ELSE PNOM.NOMBREUNICO END,
       CASE WHEN -1=s$portercero$s THEN DD.TERCERO  ELSE '' END,
       DD.CUENTA,
       SALDOINIFINAL.SALDOINI
ORDER BY COM.COMPANIA,
       COM.ANO,  
       COM.TIPO_CPTE,
       DD.CUENTA     
